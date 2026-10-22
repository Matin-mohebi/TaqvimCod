/* Sanity tests for Taqvim web algorithms (run: node sanity.mjs) */
import { j2d, d2j, g2d, d2g, toJalaali, toGregorian, isLeapJalaali, jalaaliMonthLength, weekdayFromJdn, hijriFromJdn, jdnFromHijri } from './src/lib/dates.ts';
import { forDay, nextHoliday, daysToNowruz, isHoliday } from './src/lib/holidays.ts';
import { calculatePrayerTimes, qiblaBearing } from './src/lib/prayer.ts';

let failures = 0;
const ok = (cond, msg) => {
  if (cond) console.log(`  ✓ ${msg}`);
  else { console.error(`  ✗ FAIL: ${msg}`); failures++; }
};

console.log('--- Jalaali conversions ---');
{
  const j = toJalaali(2026, 3, 21);
  ok(j.jy === 1405 && j.jm === 1 && j.jd === 1, `Nowruz 2026 -> 1405/1/1 (got ${j.jy}/${j.jm}/${j.jd})`);
  const t = toJalaali(2026, 10, 22);
  ok(t.jy === 1405 && t.jm === 7 && t.jd === 30, `2026-10-22 -> 1405/7/30 (got ${t.jy}/${t.jm}/${t.jd})`);
  const g = toGregorian(1404, 1, 1);
  ok(g.gy === 2025 && g.gm === 3 && g.gd === 21, `1404/1/1 -> 2025-03-21 (got ${g.gy}-${g.gm}-${g.gd})`);
  const yalda = toJalaali(2026, 12, 21);
  ok(yalda.jm === 9 && yalda.jd === 30, `Yalda 2026-12-21 -> 1405/9/30 (got ${yalda.jm}/${yalda.jd})`);
}
console.log('--- roundtrip j2d/d2j over 1300..1470 ---');
{
  let bad = 0;
  for (let jy = 1300; jy <= 1470; jy++) {
    for (let jm = 1; jm <= 12; jm += 5) {
      for (const jd of [1, 15, jalaaliMonthLength(jy, jm)]) {
        const jdn = j2d(jy, jm, jd);
        const back = d2j(jdn);
        if (back.jy !== jy || back.jm !== jm || back.jd !== jd) bad++;
      }
    }
  }
  ok(bad === 0, `roundtrip mismatches: ${bad}`);
}
console.log('--- leap years / month lengths ---');
{
  ok(isLeapJalaali(1403) === true, '1403 is leap');
  ok(isLeapJalaali(1404) === false, '1404 is not leap');
  ok(jalaaliMonthLength(1403, 12) === 30, 'Esfand 1403 = 30');
  ok(jalaaliMonthLength(1404, 12) === 29, 'Esfand 1404 = 29');
}
console.log('--- weekday ---');
{
  // 2026-10-22 is Thursday -> پنجشنبه = index 5 (0=Saturday)
  const jdn = g2d(2026, 10, 22);
  ok(weekdayFromJdn(jdn) === 5, `2026-10-22 Thursday -> 5 (got ${weekdayFromJdn(jdn)})`);
  const jdn2 = g2d(2026, 10, 23); // Friday
  ok(weekdayFromJdn(jdn2) === 6, `2026-10-23 Friday -> 6 (got ${weekdayFromJdn(jdn2)})`);
}
console.log('--- Hijri (tabular, Iran -1) ---');
{
  const today = g2d(2026, 10, 22);
  const h = hijriFromJdn(today, -1);
  ok(h.hy >= 1448 && h.hy <= 1448, `today hijri year 1448 (got ${h.hy}/${h.hm}/${h.hd})`);
  // roundtrip via binary-search inverse
  let bad = 0;
  for (let k = 0; k < 200; k++) {
    const jdn = today + k * 37 - 3000;
    const hh = hijriFromJdn(jdn, -1);
    const inv = jdnFromHijri(hh.hy, hh.hm, hh.hd, -1);
    if (inv !== jdn) bad++;
  }
  ok(bad === 0, `hijri inverse mismatches: ${bad}`);
}
console.log('--- holidays ---');
{
  const nowruz = g2d(2026, 3, 21);
  ok(isHoliday(nowruz, -1) === true, 'Nowruz is holiday');
  const info = forDay(nowruz, -1);
  ok(info.events.some((e) => e.fa === 'عید نوروز' && e.holiday), 'Nowruz event present');
  const nh = nextHoliday(g2d(2026, 10, 22), -1, 400);
  ok(nh !== null, `next holiday found: ${nh ? nh.ev.fa + ' in ' + nh.days + 'd' : 'NONE'}`);
  const nz = daysToNowruz(g2d(2026, 10, 22));
  ok(nz.days === 150, `days to Nowruz 1407 = 150 (got ${nz.days}, target year ${nz.jy})`);
}
console.log('--- prayer times (Tehran, 2026-10-22) ---');
{
  const jdn = g2d(2026, 10, 22);
  const p0 = calculatePrayerTimes(jdn, 35.6892, 51.3890, 0); // Tehran method
  const fmt = (m) => `${String(Math.floor(m / 60)).padStart(2, '0')}:${String(m % 60).padStart(2, '0')}`;
  console.log('   Tehran-method:', ['fajr','sunrise','dhuhr','asr','maghrib','isha'].map((k) => `${k}=${p0[k] !== null ? fmt(p0[k]) : '--'}`).join(' '));
  ok(p0.fajr !== null && p0.fajr > 4 * 60 && p0.fajr < 5.5 * 60, `fajr ~05:00 (got ${fmt(p0.fajr)})`);
  ok(p0.sunrise > 6 * 60 && p0.sunrise < 6.75 * 60, `sunrise ~06:25 (got ${fmt(p0.sunrise)})`);
  ok(p0.dhuhr > 11.5 * 60 && p0.dhuhr < 12.1 * 60, `dhuhr ~11:49 (got ${fmt(p0.dhuhr)})`);
  ok(p0.sunrise < p0.dhuhr && p0.dhuhr < p0.asr && p0.asr < p0.maghrib && p0.maghrib < p0.isha, 'order fajr<sunrise<dhuhr<asr<maghrib<isha');
  ok(p0.maghrib - p0.sunrise > 10 * 60 && p0.maghrib - p0.sunrise < 11.2 * 60, `daylight ~10h45m (got ${fmt(p0.maghrib - p0.sunrise)})`);
  // Tehran 14° isha earlier than MWL 17°
  const p1 = calculatePrayerTimes(jdn, 35.6892, 51.3890, 1);
  ok(p1.isha > p0.isha, `MWL isha (${fmt(p1.isha)}) later than Tehran isha (${fmt(p0.isha)})`);
  // Umm al-Qura: isha = maghrib + 90
  const p5 = calculatePrayerTimes(jdn, 35.6892, 51.3890, 5);
  ok(p5.isha === p5.maghrib + 90, `Umm al-Qura isha = maghrib+90 (got ${fmt(p5.isha)} vs ${fmt(p5.maghrib)})`);
  // summer solstice sanity (no DST in Iran since 2022)
  const jun = calculatePrayerTimes(g2d(2026, 6, 21), 35.6892, 51.3890, 0);
  ok(jun.sunrise > 4.5 * 60 && jun.sunrise < 5.2 * 60, `June sunrise ~04:5x no-DST (got ${fmt(jun.sunrise)})`);
  ok(jun.maghrib > 19.3 * 60 && jun.maghrib < 19.6 * 60, `June sunset ~19:2x-19:4x (got ${fmt(jun.maghrib)})`);
  const q = qiblaBearing(35.6892, 51.3890);
  ok(q > 217 && q < 220, `Tehran qibla ~218.5° SW (got ${q.toFixed(2)})`);
}

console.log(failures === 0 ? '\nALL PASSED' : `\n${failures} FAILURES`);
process.exit(failures === 0 ? 0 : 1);
