/**
 * Offline solar prayer-time calculation + Qibla bearing.
 * Port of the Android app's PrayerTimes.java, upgraded so that the chosen
 * calculation method actually controls the twilight angles (the app's
 * `method` parameter was accepted but unused — fixed here and in the app).
 *
 * methods: 0 Tehran (Institute of Geophysics), 1 Muslim World League,
 *          2 Egyptian, 3 Karachi, 4 ISNA, 5 Umm al-Qura
 */
import { g2d, d2g } from './dates';

export interface PrayerDay {
  fajr: number | null;   // minutes after local midnight
  sunrise: number | null;
  dhuhr: number | null;
  asr: number | null;
  maghrib: number | null;
  isha: number | null;
}

export const METHOD_NAMES = [
  'تهران (پیشنهادی برای ایران)',
  'رابطة العالم الإسلامي',
  'مصر',
  'کراچی',
  'ISNA (آمریکا/کانادا)',
  'ام‌القری (مکه)',
];

const FAJR_ANGLES = [17.7, 18.0, 19.5, 18.0, 15.0, 18.5];
const ISHA_ANGLES = [14.0, 17.0, 17.5, 18.0, 15.0, -1]; // -1 = maghrib + 90 min (Umm al-Qura)

function julianDay(y: number, m: number, d: number): number {
  if (m <= 2) { y--; m += 12; }
  const a = Math.floor(y / 100), b = 2 - a + Math.floor(a / 4);
  return Math.floor(365.25 * (y + 4716)) + Math.floor(30.6001 * (m + 1)) + d + b - 1524.5;
}

function norm(x: number): number { return ((x % 360) + 360) % 360; }

function equationOfTime(jd: number): number {
  const t = (jd - 2451545.0) / 36525.0;
  const epsilon = (23.439291 - 0.0130042 * t) * Math.PI / 180;
  const l0 = norm(280.46646 + t * (36000.76983 + 0.0003032 * t)) * Math.PI / 180;
  const m = (357.52911 + t * (35999.05029 - 0.0001537 * t)) * Math.PI / 180;
  const e = 0.016708634 - t * (0.000042037 + 0.0000001267 * t);
  let y = Math.tan(epsilon / 2); y *= y;
  const eq = y * Math.sin(2 * l0) - 2 * e * Math.sin(m) + 4 * e * y * Math.sin(m) * Math.cos(2 * l0)
    - 0.5 * y * y * Math.sin(4 * l0) - 1.25 * e * e * Math.sin(2 * m);
  return (eq * 180) / Math.PI * 4.0;
}

function sunDeclination(jd: number): number {
  const t = (jd - 2451545.0) / 36525.0;
  const l0 = norm(280.46646 + t * (36000.76983 + 0.0003032 * t)) * Math.PI / 180;
  const m = (357.52911 + t * (35999.05029 - 0.0001537 * t)) * Math.PI / 180;
  const c = Math.sin(m) * (1.914602 - t * (0.004817 + 0.000014 * t))
    + Math.sin(2 * m) * (0.019993 - 0.000101 * t) + Math.sin(3 * m) * 0.000289;
  const omega = (125.04 - 1934.136 * t) * Math.PI / 180;
  const lambda = norm((l0 * 180) / Math.PI + c - 0.00569 - 0.00478 * Math.sin(omega)) * Math.PI / 180;
  const epsilon = (23.439291 - 0.0130042 * t + 0.00256 * Math.cos(omega)) * Math.PI / 180;
  return Math.asin(Math.sin(epsilon) * Math.sin(lambda));
}

function hourAngle(lat: number, decl: number, angle: number): number {
  const numerator = -Math.sin((angle * Math.PI) / 180) - Math.sin((lat * Math.PI) / 180) * Math.sin(decl);
  const denominator = Math.cos((lat * Math.PI) / 180) * Math.cos(decl);
  const v = numerator / denominator;
  if (v < -1 || v > 1 || Number.isNaN(v)) return NaN;
  return (Math.acos(v) * 180) / Math.PI;
}

const h2m = (h: number): number | null =>
  Number.isFinite(h) ? Math.round(h * 60) : null;

/** Calculate prayer times (minutes after local midnight) for the JDN of a date.
 *  tzHours: fixed civil offset of the CITY's timezone — all 31 supported cities
 *  are in Iran, so the default is Asia/Tehran (+3.5, no DST since 2022). Using a
 *  fixed offset (instead of the visitor's device offset) keeps results identical
 *  for Iranian users and meaningful for the diaspora. */
export function calculatePrayerTimes(jdn: number, latitude: number, longitude: number, method: number, tzHours = 3.5): PrayerDay {
  const g = d2g(jdn);
  const jd = julianDay(g.gy, g.gm, g.gd);
  const noon = 12.0 + tzHours - longitude / 15.0 - equationOfTime(jd) / 60.0;
  const decl = sunDeclination(jd + 0.5);

  const fajrAngle = FAJR_ANGLES[Math.max(0, Math.min(5, method))];
  const ishaAngle = ISHA_ANGLES[Math.max(0, Math.min(5, method))];

  const dhuhr = h2m(noon);
  const sunrise = h2m(noon - hourAngle(latitude, decl, 0.833) / 15.0);
  // NOTE: hourAngle(a) is defined for altitude -a (the -sin(a) term), so sunrise,
  // sunset, fajr and isha all pass POSITIVE angles; only asr (above-horizon
  // altitude) passes a negative one. Passing -0.833 for sunset (as the original
  // app did) computes the sun at +0.833° and yields a maghrib ~10 min too early.
  const maghrib = h2m(noon + hourAngle(latitude, decl, 0.833) / 15.0);
  const fajr = h2m(noon - hourAngle(latitude, decl, fajrAngle) / 15.0);
  const isha = ishaAngle < 0
    ? (maghrib !== null ? maghrib + 90 : null)          // Umm al-Qura: 90 min after sunset
    : h2m(noon + hourAngle(latitude, decl, ishaAngle) / 15.0);
  const asrAltitude = (Math.atan(1.0 / (1.0 + Math.tan(Math.abs((latitude * Math.PI) / 180 - decl))))) * 180 / Math.PI;
  const asr = h2m(noon + hourAngle(latitude, decl, -asrAltitude) / 15.0);

  return { fajr, sunrise, dhuhr, asr, maghrib, isha };
}

/** Initial great-circle bearing to the Kaaba, clockwise from true north. */
export function qiblaBearing(latitude: number, longitude: number): number {
  const lat1 = (latitude * Math.PI) / 180, lon1 = (longitude * Math.PI) / 180;
  const lat2 = (21.422487 * Math.PI) / 180, lon2 = (39.826206 * Math.PI) / 180;
  const y = Math.sin(lon2 - lon1);
  const x = Math.cos(lat1) * Math.tan(lat2) - Math.sin(lat1) * Math.cos(lon2 - lon1);
  return ((Math.atan2(y, x) * 180) / Math.PI + 360.0) % 360.0;
}

export { g2d };
