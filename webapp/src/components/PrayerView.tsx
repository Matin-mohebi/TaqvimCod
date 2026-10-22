import { useEffect, useMemo, useState } from 'react';
import { CITIES } from '../lib/cities';
import { calculatePrayerTimes, qiblaBearing, METHOD_NAMES } from '../lib/prayer';
import { minutesToFaTime, faNum } from '../lib/fa';
import { useLocal } from '../lib/useLocal';
import { IconClock, IconCompass, IconWarning } from './icons';

const ROWS: { key: 'fajr' | 'sunrise' | 'dhuhr' | 'asr' | 'maghrib' | 'isha'; name: string }[] = [
  { key: 'fajr', name: 'اذان صبح' },
  { key: 'sunrise', name: 'طلوع آفتاب' },
  { key: 'dhuhr', name: 'اذان ظهر' },
  { key: 'asr', name: 'عصر (محاسباتی)' },
  { key: 'maghrib', name: 'غروب آفتاب / اذان مغرب' },
  { key: 'isha', name: 'اذان عشا' },
];

/** Current time-of-day in the CITY timezone (Asia/Tehran) so the countdown to
 *  the next prayer is correct for visitors abroad, not just inside Iran. */
function useTehranNowMinutes(): number {
  const [minutes, setMinutes] = useState(() => tehranNowMinutes());
  useEffect(() => {
    const iv = window.setInterval(() => setMinutes(tehranNowMinutes()), 20_000);
    return () => window.clearInterval(iv);
  }, []);
  return minutes;
}

const tehranFmt = new Intl.DateTimeFormat('en-US', {
  timeZone: 'Asia/Tehran',
  hour: 'numeric',
  minute: 'numeric',
  second: 'numeric',
  hour12: false,
});

function tehranNowMinutes(): number {
  try {
    const parts = tehranFmt.formatToParts(new Date());
    const get = (t: string) => +(parts.find((p) => p.type === t)?.value ?? '0');
    return get('hour') % 24 * 60 + get('minute') + get('second') / 60;
  } catch {
    const n = new Date();
    return n.getHours() * 60 + n.getMinutes() + n.getSeconds() / 60;
  }
}

export default function PrayerView({ today }: { today: number }) {
  const [cityIdx, setCityIdx] = useLocal<number>('taqvim.city', 0);
  const [method, setMethod] = useLocal<number>('taqvim.method', 0);
  const city = CITIES[Math.max(0, Math.min(CITIES.length - 1, cityIdx))];

  const times = useMemo(
    () => calculatePrayerTimes(today, city.lat, city.lon, method),
    [today, city, method],
  );
  const tomorrow = useMemo(
    () => calculatePrayerTimes(today + 1, city.lat, city.lon, method),
    [today, city, method],
  );
  const nowMin = useTehranNowMinutes();

  const next = useMemo(() => {
    const list = ROWS.map((r) => ({ ...r, m: times[r.key] }));
    for (const row of list) {
      if (row.m !== null && nowMin < row.m) {
        return { row, left: row.m - nowMin, tomorrow: false };
      }
    }
    const tf = tomorrow.fajr;
    if (tf !== null) return { row: ROWS[0], left: 1440 - nowMin + tf, tomorrow: true };
    return null;
  }, [times, tomorrow, nowMin]);

  const qibla = useMemo(() => qiblaBearing(city.lat, city.lon), [city]);

  return (
    <>
      <section className="card" aria-label="اوقات شرعی">
        <h3 className="card-title"><IconClock /> اوقات شرعی امروز</h3>
        <div className="selects-row">
          <select className="pick" value={cityIdx} onChange={(e) => setCityIdx(+e.target.value)} aria-label="انتخاب شهر">
            {CITIES.map((c, i) => (
              <option key={c.name} value={i}>{c.name}</option>
            ))}
          </select>
          <select className="pick" value={method} onChange={(e) => setMethod(+e.target.value)} aria-label="روش محاسبه">
            {METHOD_NAMES.map((m, i) => (
              <option key={m} value={i}>{m}</option>
            ))}
          </select>
        </div>

        <div className="times-list">
          {ROWS.map((r) => {
            const isNext = next && next.row.key === r.key && !next.tomorrow;
            return (
              <div key={r.key} className={`time-row${isNext ? ' next' : ''}`}>
                <div className="t-icon"><IconClock /></div>
                <div className="t-name">
                  {r.name}
                  {isNext && next && (
                    <span className="t-sub">
                      {faNum(Math.max(1, Math.round(next.left)))} دقیقه مانده
                    </span>
                  )}
                </div>
                <div className="t-time">{minutesToFaTime(times[r.key])}</div>
              </div>
            );
          })}
        </div>

        {next?.tomorrow && (
          <div className="progress-text" style={{ marginTop: 8 }}>
            اذان صبح فردا: {minutesToFaTime(tomorrow.fajr)} — {faNum(Math.max(1, Math.round(next.left)))} دقیقه مانده
          </div>
        )}

        <div className="note">
          <IconWarning />
          <span>
            زمان‌ها به‌صورت نجومی و روی دستگاه شما محاسبه می‌شوند و تقریبی هستند؛ برای اطمینان،
            زمان‌های مرجع محلی شهرتان را در نظر بگیرید.
          </span>
        </div>
      </section>

      <section className="card" aria-label="قبله‌نما">
        <h3 className="card-title"><IconCompass /> قبله‌نما — {city.name}</h3>
        <div className="qibla-wrap">
          <div className="qibla-dial">
            <span className="qibla-n">شمال</span>
            <div className="qibla-needle" style={{ transform: `rotate(${qibla}deg)` }}>
              <svg viewBox="0 0 24 24" fill="currentColor" aria-hidden>
                <path d="M12 2l3.5 9L12 9.5 8.5 11 12 2z" />
                <path d="M12 22l-2-7 2 1.5L14 15l-2 7z" opacity="0.35" />
              </svg>
            </div>
          </div>
          <div className="qibla-info">
            جهت قبله: <b>{faNum(Math.round(qibla))}°</b> نسبت به شمال واقعی
            <br />
            مختصات: {faNum(city.lat.toFixed(2))} ، {faNum(city.lon.toFixed(2))}
            <br />
            <span style={{ color: 'var(--muted)', fontSize: 12 }}>
              سمت فلش را رو به شمال بگیرید؛ همان جهت قبله است.
            </span>
          </div>
        </div>
      </section>
    </>
  );
}
