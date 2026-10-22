import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import {
  d2j, d2g, hijriFromJdn, j2d, weekdayFromJdn, jalaaliMonthLength,
} from '../lib/dates';
import { forDay, nextHoliday, daysToNowruz } from '../lib/holidays';
import { faNum, faPad2, WEEKDAYS, WEEKDAYS_SHORT, JALALI_MONTHS, HIJRI_MONTHS, GREGORIAN_MONTHS } from '../lib/fa';
import {
  IconCheck, IconCopy, IconGift, IconNote, IconShare, IconSparkle,
  IconChevronLeft, IconChevronRight, IconWarning,
} from './icons';

interface Todo { id: number; text: string; done: boolean }
type PlannerData = Record<string, Todo[]>;

function loadPlanner(): PlannerData {
  try {
    const raw = localStorage.getItem('taqvim.planner.v1');
    return raw ? (JSON.parse(raw) as PlannerData) : {};
  } catch {
    return {};
  }
}

function savePlanner(d: PlannerData) {
  try { localStorage.setItem('taqvim.planner.v1', JSON.stringify(d)); } catch { /* ignore */ }
}

async function copyText(text: string): Promise<boolean> {
  try {
    await navigator.clipboard.writeText(text);
    return true;
  } catch {
    try {
      const ta = document.createElement('textarea');
      ta.value = text;
      ta.style.position = 'fixed';
      ta.style.opacity = '0';
      document.body.appendChild(ta);
      ta.select();
      const ok = document.execCommand('copy');
      document.body.removeChild(ta);
      return ok;
    } catch {
      return false;
    }
  }
}

/* ================= Hero: today ================= */
function Hero({ today, hijriAdj, toast }: { today: number; hijriAdj: number; toast: (s: string) => void }) {
  const [now, setNow] = useState(() => new Date());
  useEffect(() => {
    const iv = window.setInterval(() => setNow(new Date()), 1000);
    return () => window.clearInterval(iv);
  }, []);

  const j = d2j(today);
  const g = d2g(today);
  const h = hijriFromJdn(today, hijriAdj);
  const nextHol = useMemo(() => nextHoliday(today, hijriAdj, 400), [today, hijriAdj]);
  const nowruz = useMemo(() => daysToNowruz(today), [today]);
  const info = forDay(today, hijriAdj);

  const hh = faPad2(now.getHours());
  const mm = faPad2(now.getMinutes());
  const ss = faPad2(now.getSeconds());

  const triple = `${faNum(j.jd)} ${JALALI_MONTHS[j.jm - 1]} ${faNum(j.jy)} — ${faNum(g.gd)} ${GREGORIAN_MONTHS[g.gm - 1]} ${faNum(g.gy)} — ${faNum(h.hd)} ${HIJRI_MONTHS[h.hm - 1]} ${faNum(h.hy)}`;

  const shareToday = async () => {
    const text = `امروز: ${WEEKDAYS[weekdayFromJdn(today)]} ${triple}`;
    try {
      if (navigator.share) await navigator.share({ title: 'تاریخ امروز', text });
      else throw new Error('no share');
    } catch {
      if (await copyText(text)) toast('تاریخ امروز کپی شد');
    }
  };

  return (
    <section className="hero" aria-label="امروز">
      <div className="hero-top">
        <div>
          <div className="hero-label">امروز · {WEEKDAYS[weekdayFromJdn(today)]}</div>
          <div className="hero-jdate">{faNum(j.jd)} {JALALI_MONTHS[j.jm - 1]} {faNum(j.jy)}</div>
        </div>
        <div>
          <div className="hero-clock" dir="ltr">{hh}:{mm}:{ss}</div>
          <div className="hero-gdate">{g.gd} {GREGORIAN_MONTHS[g.gm - 1]} {g.gy}</div>
        </div>
      </div>
      <div className="hero-row">
        {info.holiday && info.events.length > 0 && (
          <span className="hero-chip holiday-chip">
            <IconWarning /> {info.events.find((e) => e.holiday)?.fa ?? info.events[0].fa}
          </span>
        )}
        {nextHol && nextHol.days > 0 && (
          <button className="hero-chip" onClick={() => { document.getElementById('dp')?.scrollIntoView({ behavior: 'smooth', block: 'start' }); }}>
            <IconGift /> تعطیلی بعدی: {nextHol.ev.fa} — {faNum(nextHol.days)} روز مانده
          </button>
        )}
        <button className="hero-chip" onClick={shareToday}>
          <IconShare /> کپی / اشتراک تاریخ
        </button>
        <span className="hero-chip" style={{ cursor: 'default' }}>
          <IconSparkle /> نوروز {faNum(nowruz.jy)}: {faNum(nowruz.days)} روز مانده
        </span>
      </div>
    </section>
  );
}

/* ================= Month grid ================= */
function MonthGrid({ selected, setSelected, today, hijriAdj }:
{ selected: number; setSelected: (jdn: number) => void; today: number; hijriAdj: number }) {
  const sel = d2j(selected);
  const [view, setView] = useState<{ jy: number; jm: number }>(() => ({ jy: sel.jy, jm: sel.jm }));
  const touchX = useRef<number | null>(null);

  // keep the grid month in sync when the selection jumps (e.g. another view)
  useEffect(() => {
    if (sel.jy !== view.jy || sel.jm !== view.jm) setView({ jy: sel.jy, jm: sel.jm });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selected]);

  const shiftMonth = useCallback((delta: number) => {
    setView((v) => {
      let jm = v.jm + delta;
      let jy = v.jy;
      if (jm > 12) { jm = 1; jy++; }
      if (jm < 1) { jm = 12; jy--; }
      return { jy, jm };
    });
  }, []);

  const cells = useMemo(() => {
    const first = j2d(view.jy, view.jm, 1);
    const lead = weekdayFromJdn(first); // 0 = Saturday
    const list: { jdn: number; inMonth: boolean }[] = [];
    for (let i = lead; i > 0; i--) list.push({ jdn: first - i, inMonth: false });
    const len = jalaaliMonthLength(view.jy, view.jm);
    for (let d = 0; d < len; d++) list.push({ jdn: first + d, inMonth: true });
    while (list.length % 7 !== 0 || list.length < 42) {
      const last = list[list.length - 1].jdn;
      list.push({ jdn: last + 1, inMonth: false });
      if (list.length >= 42) break;
    }
    return list;
  }, [view]);

  const isSelMonthToday = view.jy === sel.jy && view.jm === sel.jm;

  return (
    <section className="card" aria-label="تقویم ماه">
      <div className="month-nav">
        <button className="nav-btn" onClick={() => shiftMonth(-1)} aria-label="ماه قبل">
          <IconChevronRight />
        </button>
        <div className="month-title">{JALALI_MONTHS[view.jm - 1]} {faNum(view.jy)}</div>
        <button className="nav-btn" onClick={() => shiftMonth(1)} aria-label="ماه بعد">
          <IconChevronLeft />
        </button>
      </div>
      <div className="month-picks">
        <select
          className="pick"
          value={view.jm}
          onChange={(e) => setView((v) => ({ ...v, jm: +e.target.value }))}
          aria-label="انتخاب ماه"
        >
          {JALALI_MONTHS.map((m, i) => (
            <option key={m} value={i + 1}>{m}</option>
          ))}
        </select>
        <select
          className="pick"
          value={view.jy}
          onChange={(e) => setView((v) => ({ ...v, jy: +e.target.value }))}
          aria-label="انتخاب سال"
        >
          {Array.from({ length: 121 }, (_, i) => 1350 + i).map((y) => (
            <option key={y} value={y}>{faNum(y)}</option>
          ))}
        </select>
        {!isSelMonthToday && (
          <button className="mini-btn ghost" onClick={() => setSelected(today)}>امروز</button>
        )}
      </div>

      <div
        className="grid-wrap"
        onTouchStart={(e) => { touchX.current = e.touches[0].clientX; }}
        onTouchEnd={(e) => {
          if (touchX.current === null) return;
          const dx = e.changedTouches[0].clientX - touchX.current;
          if (Math.abs(dx) > 56) shiftMonth(dx > 0 ? -1 : 1);
          touchX.current = null;
        }}
      >
        <div className="grid-head">
          {WEEKDAYS_SHORT.map((w, i) => (
            <div key={w} className={`dow${i === 6 ? ' friday' : ''}`}>{w}</div>
          ))}
        </div>
        <div className="grid">
          {cells.map(({ jdn, inMonth }) => {
            const info = forDay(jdn, hijriAdj);
            const classes = [
              'day-cell',
              !inMonth ? 'other' : '',
              info.holiday ? 'holiday' : '',
              !info.holiday && weekdayFromJdn(jdn) === 6 ? 'friday' : '',
              jdn === selected ? 'selected' : '',
              jdn === today ? 'today' : '',
            ].filter(Boolean).join(' ');
            const jd = d2j(jdn).jd;
            return (
              <button
                key={jdn}
                className={classes}
                onClick={() => setSelected(jdn)}
                aria-label={`${WEEKDAYS[weekdayFromJdn(jdn)]} ${faNum(jd)} ${JALALI_MONTHS[d2j(jdn).jm - 1]}`}
              >
                <span>{faNum(jd)}</span>
                {info.events.length > 0 && (
                  <span className={`dot${info.holiday ? ' off' : ''}`} />
                )}
              </button>
            );
          })}
        </div>
        <div className="grid-hint">برای جابه‌جایی ماه‌ها، صفحه را به چپ و راست بکشید</div>
      </div>
    </section>
  );
}

/* ================= Day panel + planner ================= */
function DayPanel({ selected, today, hijriAdj, toast }: { selected: number; today: number; hijriAdj: number; toast: (s: string) => void }) {
  const [planner, setPlanner] = useState<PlannerData>(loadPlanner);
  const [text, setText] = useState('');
  const [confirmDel, setConfirmDel] = useState<number | null>(null);

  const j = d2j(selected);
  const g = d2g(selected);
  const h = hijriFromJdn(selected, hijriAdj);
  const info = forDay(selected, hijriAdj);
  const wd = weekdayFromJdn(selected);
  const key = String(selected);
  const todos = planner[key] ?? [];
  const doneCount = todos.filter((t) => t.done).length;

  const addTodo = () => {
    const t = text.trim();
    if (!t) return;
    const next = { ...planner, [key]: [...todos, { id: Date.now(), text: t, done: false }] };
    setPlanner(next);
    savePlanner(next);
    setText('');
  };

  const toggleTodo = (id: number) => {
    const next = { ...planner, [key]: todos.map((t) => (t.id === id ? { ...t, done: !t.done } : t)) };
    setPlanner(next);
    savePlanner(next);
  };

  const removeTodo = (id: number) => {
    if (confirmDel !== id) { setConfirmDel(id); window.setTimeout(() => setConfirmDel((c) => (c === id ? null : c)), 2600); return; }
    const next = { ...planner, [key]: todos.filter((t) => t.id !== id) };
    setPlanner(next);
    savePlanner(next);
    setConfirmDel(null);
    toast('کار حذف شد');
  };

  const triple = `${WEEKDAYS[wd]} ${faNum(j.jd)} ${JALALI_MONTHS[j.jm - 1]} ${faNum(j.jy)} — ${faNum(g.gd)} ${GREGORIAN_MONTHS[g.gm - 1]} ${faNum(g.gy)} — ${faNum(h.hd)} ${HIJRI_MONTHS[h.hm - 1]} ${faNum(h.hy)}`;

  const copyDay = async () => {
    if (await copyText(triple)) toast('تاریخ کپی شد');
    else toast('کپی ممکن نشد');
  };
  const shareDay = async () => {
    try {
      if (navigator.share) await navigator.share({ title: 'تاریخ', text: triple });
      else throw new Error('no share');
    } catch {
      void copyDay();
    }
  };

  return (
    <section className="card" id="dp" aria-label="روز انتخاب‌شده">
      <div className="dp-head">
        <div>
          <div className="dp-jdate">
            {faNum(j.jd)} {JALALI_MONTHS[j.jm - 1]} {faNum(j.jy)}
            {selected === today && <span style={{ color: 'var(--accent-strong)', fontSize: 12 }}> · امروز</span>}
          </div>
          <div className="dp-gdate">{g.gd} {GREGORIAN_MONTHS[g.gm - 1]} {g.gy}</div>
          <div className="dp-hdate">{faNum(h.hd)} {HIJRI_MONTHS[h.hm - 1]} {faNum(h.hy)}</div>
        </div>
        <span className={`dp-weekday${info.holiday ? ' off' : ''}`}>{WEEKDAYS[wd]}</span>
      </div>

      {info.events.length > 0 && (
        <div className="events">
          {info.events.map((ev, i) => (
            <div key={i} className={`event${ev.holiday ? ' off' : ' plain'}`}>
              <span className="badge">{ev.holiday ? 'تعطیل' : 'مناسبت'}</span>
              <span>{ev.fa}</span>
            </div>
          ))}
        </div>
      )}

      <div className="mini-actions">
        <button className="mini-btn" onClick={copyDay}><IconCopy /> کپی تاریخ</button>
        <button className="mini-btn ghost" onClick={shareDay}><IconShare /> اشتراک‌گذاری</button>
      </div>

      <h4 className="card-title" style={{ marginTop: 18 }}>
        <IconNote /> برنامه‌ریز این روز
      </h4>
      <div className="planner-add">
        <input
          value={text}
          onChange={(e) => setText(e.target.value)}
          onKeyDown={(e) => { if (e.key === 'Enter') addTodo(); }}
          placeholder="یک کار برای این روز بنویس…"
          maxLength={140}
        />
        <button className="add-btn" onClick={addTodo} aria-label="افزودن کار">+</button>
      </div>
      {todos.length > 0 && (
        <>
          <div className="todos">
            {todos.map((t) => (
              <div key={t.id} className={`todo${t.done ? ' done' : ''}`}>
                <button className="check" onClick={() => toggleTodo(t.id)} aria-label="انجام شد">
                  <IconCheck />
                </button>
                <span className="txt">{t.text}</span>
                <button
                  className={`del${confirmDel === t.id ? ' confirm' : ''}`}
                  onClick={() => removeTodo(t.id)}
                >
                  {confirmDel === t.id ? 'تأیید؟' : 'حذف'}
                </button>
              </div>
            ))}
          </div>
          <div className="progress-line">
            <div className="progress-fill" style={{ width: `${Math.round((doneCount / todos.length) * 100)}%` }} />
          </div>
          <div className="progress-text">
            {faNum(doneCount)} از {faNum(todos.length)} انجام شد
          </div>
        </>
      )}
    </section>
  );
}

/* ================= View ================= */
export default function CalendarView({ today, hijriAdj, toast }: { today: number; hijriAdj: number; toast: (s: string) => void }) {
  const [selected, setSelected] = useState(today);
  return (
    <>
      <Hero today={today} hijriAdj={hijriAdj} toast={toast} />
      <MonthGrid selected={selected} setSelected={setSelected} today={today} hijriAdj={hijriAdj} />
      <DayPanel selected={selected} today={today} hijriAdj={hijriAdj} toast={toast} />
    </>
  );
}
