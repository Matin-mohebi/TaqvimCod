/**
 * Accurate date conversions: Gregorian <-> Jalali (Persian) <-> Hijri (tabular).
 * 1:1 port of the Android app's Dates.java (jalaali-js algorithm + Kuwaiti tabular
 * Hijri with adjustable correction). Weekday: 0 = Saturday ... 6 = Friday.
 */

export interface JDate { jy: number; jm: number; jd: number }
export interface GDate { gy: number; gm: number; gd: number }
export interface HDate { hy: number; hm: number; hd: number }

const div = (a: number, b: number) => Math.trunc(a / b);
const mod = (a: number, b: number) => a - Math.trunc(a / b) * b;

// ---------- Jalaali core ----------
const BREAKS = [-61, 9, 38, 199, 426, 686, 756, 818, 1111, 1181,
  1210, 1635, 2060, 2097, 2192, 2262, 2324, 2394, 2456, 3178];

/** returns [leap, gy, march] */
function jalCal(jy: number): [number, number, number] {
  const bl = BREAKS.length;
  const gy = jy + 621;
  let leapJ = -14;
  let jp = BREAKS[0];
  let jump = 0, n = 0;
  for (let i = 1; i < bl; i++) {
    const jm = BREAKS[i];
    jump = jm - jp;
    if (jy < jm) break;
    leapJ = leapJ + div(jump, 33) * 8 + div(mod(jump, 33), 4);
    jp = jm;
  }
  n = jy - jp;
  leapJ = leapJ + div(n, 33) * 8 + div(mod(n, 33) + 3, 4);
  if (mod(jump, 33) === 4 && jump - n === 4) leapJ += 1;
  const leapG = div(gy, 4) - div((div(gy, 100) + 1) * 3, 4) - 150;
  const march = 20 + leapJ - leapG;
  if (jump - n < 6) n = n - jump + div(jump + 4, 33) * 33;
  let leap = mod(mod(n + 1, 33) - 1, 4);
  if (leap === -1) leap = 4;
  return [leap, gy, march];
}

/** Jalaali -> Julian Day Number */
export function j2d(jy: number, jm: number, jd: number): number {
  const r = jalCal(jy);
  return g2d(r[1], 3, r[2]) + (jm - 1) * 31 - div(jm, 7) * (jm - 7) + jd - 1;
}

/** Julian Day Number -> Jalaali */
export function d2j(jdn: number): JDate {
  const gy = d2g(jdn).gy;
  let jy = gy - 621;
  const r = jalCal(jy);
  const jdn1f = g2d(gy, 3, r[2]);
  let k = jdn - jdn1f;
  let jm: number, jd: number;
  if (k >= 0) {
    if (k <= 185) {
      jm = 1 + div(k, 31);
      jd = mod(k, 31) + 1;
      return { jy, jm, jd };
    }
    k -= 186;
  } else {
    jy -= 1;
    k += 179;
    if (r[0] === 1) k += 1;
  }
  jm = 7 + div(k, 30);
  jd = mod(k, 30) + 1;
  return { jy, jm, jd };
}

// ---------- Gregorian <-> JDN ----------
export function g2d(gy: number, gm: number, gd: number): number {
  let d = div((gy + div(gm - 8, 6) + 100100) * 1461, 4)
    + div(153 * mod(gm + 9, 12) + 2, 5)
    + gd - 34840408;
  d = d - div(div(gy + 100100 + div(gm - 8, 6), 100) * 3, 4) + 752;
  return d;
}

export function d2g(jdn: number): GDate {
  let j = 4 * jdn + 139361631;
  j = j + div(div(4 * jdn + 183187720, 146097) * 3, 4) * 4 - 3908;
  const i = div(mod(j, 1461), 4) * 5 + 308;
  const gd = div(mod(i, 153), 5) + 1;
  const gm = mod(div(i, 153), 12) + 1;
  const gy = div(j, 1461) - 100100 + div(8 - gm, 6);
  return { gy, gm, gd };
}

// ---------- Jalaali helpers ----------
export function isLeapJalaali(jy: number): boolean {
  return jalCal(jy)[0] === 0;
}

export function jalaaliMonthLength(jy: number, jm: number): number {
  if (jm <= 6) return 31;
  if (jm <= 11) return 30;
  return isLeapJalaali(jy) ? 30 : 29;
}

export function toJalaali(gy: number, gm: number, gd: number): JDate {
  return d2j(g2d(gy, gm, gd));
}

export function toGregorian(jy: number, jm: number, jd: number): GDate {
  return d2g(j2d(jy, jm, jd));
}

// ---------- Hijri (tabular, with correction) ----------
/** JDN -> Hijri. adjust: correction days (Iran default: -1) */
export function hijriFromJdn(jdn: number, adjust: number): HDate {
  const jd = jdn - adjust;
  let l = jd - 1948440 + 10632;
  const n = Math.floor((l - 1) / 10631);
  l = l - 10631 * n + 354;
  const j = Math.floor((10985 - l) / 5316) * Math.floor((50 * l) / 17719)
    + Math.floor(l / 5670) * Math.floor((43 * l) / 15238);
  l = l - (Math.floor((30 - j) / 15) * Math.floor((17719 * j) / 50)
    - Math.floor(j / 16) * Math.floor((15238 * j) / 43)) + 29;
  const m = Math.floor((24 * l) / 709);
  const d = l - Math.floor((709 * m) / 24);
  const y = 30 * n + j - 30;
  return { hy: y, hm: m, hd: d };
}

/** tabular Islamic month length: odd months 30, even months 29 */
export function hijriMonthLength(hm: number): number {
  return hm % 2 === 1 ? 30 : 29;
}

/** Hijri -> JDN via binary search on the forward tabular mapping (exact). */
export function jdnFromHijri(hy: number, hm: number, hd: number, adjust: number): number {
  // Hijri year hy roughly maps to JDN 1948440 + 354.36667*(hy) ; search a wide window
  let lo = Math.floor(1948439 + 354.36667 * (hy - 1)) - 400;
  let hi = Math.floor(1948439 + 354.36667 * (hy + 2)) + 400;
  const target = { hy, hm, hd };
  const matches = (jdn: number) => {
    const h = hijriFromJdn(jdn, adjust);
    return h.hy === target.hy && h.hm === target.hm && h.hd === target.hd;
  };
  // first find any day in the target month
  while (lo < hi) {
    const mid = (lo + hi) >> 1;
    const h = hijriFromJdn(mid, adjust);
    if (h.hy > hy || (h.hy === hy && h.hm >= hm)) hi = mid;
    else lo = mid + 1;
  }
  // lo is first day of the month; scan forward for the day
  for (let jdn = lo; jdn < lo + 31; jdn++) {
    if (matches(jdn)) return jdn;
  }
  return -1;
}

// ---------- weekday & today ----------
/** 0 = Saturday ... 6 = Friday (Persian week) */
export function weekdayFromJdn(jdn: number): number {
  return ((jdn + 2) % 7 + 7) % 7;
}

/** today's JDN in the device timezone */
export function todayJdn(): number {
  const c = new Date();
  return g2d(c.getFullYear(), c.getMonth() + 1, c.getDate());
}
