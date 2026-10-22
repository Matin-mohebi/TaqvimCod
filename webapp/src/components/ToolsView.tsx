import { useMemo, useState } from 'react';
import {
  d2j, d2g, hijriFromJdn, j2d, g2d, weekdayFromJdn,
  jalaaliMonthLength, hijriMonthLength, jdnFromHijri,
} from '../lib/dates';
import { daysToNowruz } from '../lib/holidays';
import { faNum, WEEKDAYS, JALALI_MONTHS, GREGORIAN_MONTHS, HIJRI_MONTHS } from '../lib/fa';
import { IconCalendar, IconConvert, IconGift, IconClock, IconCopy, IconSparkle, IconWarning } from './icons';

type Cal = 'jalali' | 'gregorian' | 'hijri';

async function copyText(text: string): Promise<boolean> {
  try {
    await navigator.clipboard.writeText(text);
    return true;
  } catch { return false; }
}

/* ---------- three-way converter ---------- */
function Converter({ today, toast }: { today: number; toast: (s: string) => void }) {
  const tJ = d2j(today);
  const [cal, setCal] = useState<Cal>('jalali');
  const init = d2j(today);
  const [jy, setJy] = useState(init.jy);
  const [jm, setJm] = useState(init.jm);
  const [jd, setJd] = useState(init.jd);

  const months = cal === 'gregorian' ? GREGORIAN_MONTHS : cal === 'hijri' ? HIJRI_MONTHS : JALALI_MONTHS;
  const maxDay = cal === 'gregorian' ? 31 : cal === 'hijri' ? hijriMonthLength(jm) : jalaaliMonthLength(jy, jm);
  const shownJd = Math.min(jd, maxDay);

  const effJdn = useMemo(() => {
    if (cal === 'jalali') return j2d(jy, jm, shownJd);
    if (cal === 'gregorian') return g2d(jy, jm, shownJd);
    return jdnFromHijri(jy, jm, shownJd, -1);
  }, [cal, jy, jm, shownJd]);

  const out = useMemo(() => {
    if (effJdn < 0) return null;
    const j = d2j(effJdn);
    const g = d2g(effJdn);
    const h = hijriFromJdn(effJdn, -1);
    return { jdn: effJdn, j, g, h, wd: weekdayFromJdn(effJdn) };
  }, [effJdn]);

  const years = useMemo(() => {
    if (cal === 'jalali') return Array.from({ length: 171 }, (_, i) => 1300 + i);
    if (cal === 'gregorian') return Array.from({ length: 171 }, (_, i) => 1921 + i);
    return Array.from({ length: 171 }, (_, i) => 1350 + i);
  }, [cal]);

  const switchCal = (c: Cal) => {
    if (!out) { setCal(c); return; }
    // carry the date across calendars
    if (c === 'jalali') { setJy(out.j.jy); setJm(out.j.jm); setJd(out.j.jd); }
    if (c === 'gregorian') { setJy(out.g.gy); setJm(out.g.gm); setJd(out.g.gd); }
    if (c === 'hijri') { setJy(out.h.hy); setJm(out.h.hm); setJd(out.h.hd); }
    setCal(c);
  };

  const line = out
    ? `${WEEKDAYS[out.wd]}: ${faNum(out.j.jd)} ${JALALI_MONTHS[out.j.jm - 1]} ${faNum(out.j.jy)} — ${faNum(out.g.gd)} ${GREGORIAN_MONTHS[out.g.gm - 1]} ${faNum(out.g.gy)} — ${faNum(out.h.hd)} ${HIJRI_MONTHS[out.h.hm - 1]} ${faNum(out.h.hy)}`
    : '';

  return (
    <section className="card" aria-label="تبدیل تاریخ">
      <h3 className="card-title"><IconConvert /> تبدیل تاریخ</h3>
      <div className="seg" role="tablist">
        <button className={cal === 'jalali' ? 'active' : ''} onClick={() => switchCal('jalali')}>شمسی</button>
        <button className={cal === 'gregorian' ? 'active' : ''} onClick={() => switchCal('gregorian')}>میلادی</button>
        <button className={cal === 'hijri' ? 'active' : ''} onClick={() => switchCal('hijri')}>قمری</button>
      </div>
      <div className="date-picks">
        <select className="pick" value={shownJd} onChange={(e) => setJd(+e.target.value)} aria-label="روز">
          {Array.from({ length: maxDay }, (_, i) => i + 1).map((d) => (
            <option key={d} value={d}>{faNum(d)}</option>
          ))}
        </select>
        <select className="pick" value={jm} onChange={(e) => setJm(+e.target.value)} aria-label="ماه">
          {months.map((m, i) => (
            <option key={m} value={i + 1}>{m}</option>
          ))}
        </select>
        <select className="pick" value={jy} onChange={(e) => setJy(+e.target.value)} aria-label="سال">
          {years.map((y) => (
            <option key={y} value={y}>{faNum(y)}</option>
          ))}
        </select>
      </div>

      {out && (
        <div className="convert-out">
          <div className="out-row main">
            <span className="k">{WEEKDAYS[out.wd]}</span>
            <span className="v">{faNum(out.j.jd)} {JALALI_MONTHS[out.j.jm - 1]} {faNum(out.j.jy)}</span>
          </div>
          <div className="out-row">
            <span className="k">میلادی</span>
            <span className="v" style={{ direction: 'ltr' }}>{faNum(out.g.gd)} {GREGORIAN_MONTHS[out.g.gm - 1]} {faNum(out.g.gy)}</span>
          </div>
          <div className="out-row">
            <span className="k">قمری (جدولی، تصحیح ایران)</span>
            <span className="v">{faNum(out.h.hd)} {HIJRI_MONTHS[out.h.hm - 1]} {faNum(out.h.hy)}</span>
          </div>
          <div className="mini-actions">
            <button className="mini-btn" onClick={async () => {
              if (await copyText(line)) toast('نتیجه کپی شد');
              else toast('کپی ممکن نشد');
            }}>
              <IconCopy /> کپی نتیجه
            </button>
            <button className="mini-btn ghost" onClick={() => { setCal('jalali'); setJy(tJ.jy); setJm(tJ.jm); setJd(tJ.jd); }}>
              <IconCalendar /> بازگشت به امروز
            </button>
          </div>
        </div>
      )}
    </section>
  );
}

/* ---------- days between ---------- */
function JalaliPick({ label, y, m, d, set }: {
  label: string; y: number; m: number; d: number;
  set: (v: { y: number; m: number; d: number }) => void;
}) {
  const maxDay = jalaaliMonthLength(y, m);
  return (
    <div>
      <div style={{ fontSize: 12, color: 'var(--muted)', margin: '10px 0 6px' }}>{label}</div>
      <div className="date-picks">
        <select className="pick" value={Math.min(d, maxDay)} onChange={(e) => set({ y, m, d: +e.target.value })} aria-label={`${label} روز`}>
          {Array.from({ length: maxDay }, (_, i) => i + 1).map((x) => (
            <option key={x} value={x}>{faNum(x)}</option>
          ))}
        </select>
        <select className="pick" value={m} onChange={(e) => set({ y, m: +e.target.value, d })} aria-label={`${label} ماه`}>
          {JALALI_MONTHS.map((mm, i) => (
            <option key={mm} value={i + 1}>{mm}</option>
          ))}
        </select>
        <select className="pick" value={y} onChange={(e) => set({ y: +e.target.value, m, d })} aria-label={`${label} سال`}>
          {Array.from({ length: 171 }, (_, i) => 1300 + i).map((yy) => (
            <option key={yy} value={yy}>{faNum(yy)}</option>
          ))}
        </select>
      </div>
    </div>
  );
}

function DaysBetween({ today, toast }: { today: number; toast: (s: string) => void }) {
  const tj = d2j(today);
  const [a, setA] = useState({ y: tj.jy, m: tj.jm, d: tj.jd });
  const [b, setB] = useState({ y: tj.jy, m: tj.jm, d: tj.jd });

  const diff = Math.abs(j2d(b.y, b.m, b.d) - j2d(a.y, a.m, a.d));
  const weeks = Math.floor(diff / 7);
  const rem = diff % 7;

  return (
    <section className="card" aria-label="فاصله دو تاریخ">
      <h3 className="card-title"><IconClock /> فاصلهٔ دو تاریخ</h3>
      <JalaliPick label="از تاریخ" y={a.y} m={a.m} d={a.d} set={setA} />
      <JalaliPick label="تا تاریخ" y={b.y} m={b.m} d={b.d} set={setB} />
      <div className="big-stat" style={{ marginTop: 16 }}>
        <div className="big-num">{faNum(diff)}</div>
        <div className="big-label">
          روز
          {diff > 0 && <> — معادل {faNum(weeks)} هفته و {faNum(rem)} روز<br />معادل {faNum(diff / 30 < 1 ? 0 : Math.floor(diff / 30.44))} ماه تقریبی</>}
        </div>
      </div>
      <div className="mini-actions">
        <button className="mini-btn ghost" onClick={async () => {
          if (await copyText(String(diff))) toast('نتیجه کپی شد');
        }}><IconCopy /> کپی</button>
        <button className="mini-btn ghost" onClick={() => { setA({ y: tj.jy, m: tj.jm, d: tj.jd }); setB({ y: tj.jy, m: tj.jm, d: tj.jd }); }}>
          بازنشانی به امروز
        </button>
      </div>
    </section>
  );
}

/* ---------- age calculator ---------- */
function AgeCalc({ today, toast }: { today: number; toast: (s: string) => void }) {
  const tj = d2j(today);
  const [birth, setBirth] = useState({ y: 1375, m: 1, d: 1 });

  const res = useMemo(() => {
    const bJdn = j2d(birth.y, birth.m, birth.d);
    if (bJdn > today) return null;
    let years = tj.jy - birth.y;
    let months = tj.jm - birth.m;
    let days = tj.jd - birth.d;
    if (days < 0) {
      months -= 1;
      const prevMonth = tj.jm === 1 ? 12 : tj.jm - 1;
      const prevYear = tj.jm === 1 ? tj.jy - 1 : tj.jy;
      days += jalaaliMonthLength(prevYear, prevMonth);
    }
    if (months < 0) { years -= 1; months += 12; }
    const totalDays = today - bJdn;
    // next birthday
    let nb = j2d(tj.jy, birth.m, birth.d);
    if (nb <= today) nb = j2d(tj.jy + 1, birth.m, birth.d);
    const nextB = nb - today;
    return { years, months, days, totalDays, birthWd: weekdayFromJdn(bJdn), nextB };
  }, [birth, today]);

  return (
    <section className="card" aria-label="محاسبه سن">
      <h3 className="card-title"><IconSparkle /> محاسبهٔ سن</h3>
      <JalaliPick label="تاریخ تولد" y={birth.y} m={birth.m} d={birth.d} set={setBirth} />
      {res ? (
        <div style={{ marginTop: 14 }}>
          <div className="big-stat">
            <div className="big-num">{faNum(res.years)}</div>
            <div className="big-label">
              سال و {faNum(res.months)} ماه و {faNum(res.days)} روز
              <br />
              مجموع {faNum(res.totalDays)} روز زندگی — متولد {WEEKDAYS[res.birthWd]}
              <br />
              تولد بعدی: {faNum(res.nextB)} روز دیگر
            </div>
          </div>
        </div>
      ) : (
        <div className="note" style={{ marginTop: 12 }}>
          <IconWarning /> تاریخ تولد از امروز جلوتر است.
        </div>
      )}
      <div className="mini-actions">
        <button className="mini-btn ghost" onClick={async () => {
          if (!res) return;
          const line = `سن: ${res.years} سال و ${res.months} ماه و ${res.days} روز (${res.totalDays} روز)`;
          if (await copyText(line)) toast('نتیجه کپی شد');
        }}><IconCopy /> کپی نتیجه</button>
      </div>
    </section>
  );
}

/* ---------- view ---------- */
export default function ToolsView({ today, toast }: { today: number; hijriAdj: number; toast: (s: string) => void }) {
  const nowruz = useMemo(() => daysToNowruz(today), [today]);
  return (
    <>
      <section className="card" aria-label="شمارش معکوس نوروز">
        <div className="big-stat">
          <div className="big-num">{faNum(nowruz.days)}</div>
          <div className="big-label">
            روز تا نوروز {faNum(nowruz.jy)}
            <br />آغاز سال نو، بهار طبیعت
          </div>
          <div style={{ marginInlineStart: 'auto', color: 'var(--gold)' }}><IconGift style={{ width: 30, height: 30 }} /></div>
        </div>
      </section>
      <Converter today={today} toast={toast} />
      <DaysBetween today={today} toast={toast} />
      <AgeCalc today={today} toast={toast} />
    </>
  );
}
