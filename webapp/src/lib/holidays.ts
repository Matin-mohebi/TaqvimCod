/**
 * Iranian official holidays + occasions (Jalali fixed + Hijri lunar dates).
 * 1:1 port of the Android app's Holidays.java.
 */
import { d2j, d2g, hijriFromJdn, hijriMonthLength, j2d, weekdayFromJdn } from './dates';

export interface Ev {
  fa: string;
  en: string;
  holiday: boolean;
}

const JAL = new Map<string, Ev>();
const LUN = new Map<string, Ev>();

function putJ(m: number, d: number, fa: string, en: string, holiday: boolean) {
  JAL.set(`${m}/${d}`, { fa, en, holiday });
}
function putL(m: number, d: number, fa: string, en: string, holiday: boolean) {
  LUN.set(`${m}/${d}`, { fa, en, holiday });
}

// ===== Jalali fixed — official holidays =====
putJ(1, 1, 'عید نوروز', 'Nowruz (Persian New Year)', true);
putJ(1, 2, 'عید نوروز', 'Nowruz (Persian New Year)', true);
putJ(1, 3, 'عید نوروز', 'Nowruz (Persian New Year)', true);
putJ(1, 4, 'عید نوروز', 'Nowruz (Persian New Year)', true);
putJ(1, 12, 'روز جمهوری اسلامی', 'Islamic Republic Day', true);
putJ(1, 13, 'سیزده‌بدر', 'Sizdah Bedar (Nature Day)', true);
putJ(3, 14, 'رحلت امام خمینی', 'Demise of Imam Khomeini', true);
putJ(3, 15, 'قیام ۱۵ خرداد', 'Khordad 15 Uprising', true);
putJ(11, 22, 'پیروزی انقلاب اسلامی', 'Islamic Revolution Victory', true);
putJ(12, 29, 'ملی شدن صنعت نفت', 'Oil Industry Nationalization Day', true);

// ===== Jalali fixed — occasions (not off) =====
putJ(2, 1, 'روز بزرگداشت سعدی', 'Saadi Day', false);
putJ(2, 12, 'روز معلم', "Teachers' Day", false);
putJ(2, 25, 'روز بزرگداشت فردوسی و زبان فارسی', 'Ferdowsi & Persian Language Day', false);
putJ(3, 3, 'آزادسازی خرمشهر', 'Liberation of Khorramshahr', false);
putJ(6, 7, 'روز آتش‌نشانی و خدمات ایمنی', 'Firefighters & Safety Day', false);
putJ(6, 20, 'روز بزرگداشت حافظ', 'Hafez Day', false);
putJ(7, 7, 'روز پزشک', "Doctors' Day", false);
putJ(8, 13, 'روز دانش‌آموز و مبارزه با استکبار جهانی', 'Student Day & Anti-Arrogance Day', false);
putJ(8, 24, 'روز کتاب، کتاب‌خوانی و ناشر', 'Book & Reading Day', false);
putJ(9, 16, 'روز دانشجو', 'University Student Day', false);
putJ(9, 30, 'شب یلدا (چله)', 'Yalda Night (Longest Night)', false);
putJ(10, 9, 'روز بصیرت و میثاق امت با ولایت', 'Dey 9 Day (Covenant Day)', false);
putJ(11, 12, 'ورود امام خمینی به ایران', "Imam Khomeini's Return to Iran", false);

// ===== Lunar (Hijri) — official holidays =====
putL(1, 9, 'تاسوعای حسینی', 'Tasua of Husayn', true);
putL(1, 10, 'عاشورای حسینی', 'Ashura of Husayn', true);
putL(2, 20, 'اربعین حسینی', 'Arbaeen of Husayn', true);
putL(2, 28, 'رحلت پیامبر اکرم و شهادت امام حسن مجتبی', 'Demise of Prophet & Martyrdom of Imam Hassan', true);
putL(3, 17, 'میلاد پیامبر اکرم و امام جعفر صادق', 'Birth of Prophet Muhammad & Imam Sadeq', true);
putL(6, 3, 'شهادت حضرت فاطمه زهرا و روز زن', "Martyrdom of Hazrat Fatemeh & Women's Day", true);
putL(7, 13, 'ولادت امام علی و روز پدر', "Birth of Imam Ali & Father's Day", true);
putL(7, 27, 'عید سعید مبعث', "Eid al-Mab'ath", true);
putL(8, 15, 'ولادت امام زمان و جشن نیمه شعبان', "Birth of Imam Mahdi (Mid-Sha'ban)", true);
putL(9, 21, 'شهادت امام علی و شب قدر', 'Martyrdom of Imam Ali & Laylat al-Qadr', true);
putL(10, 1, 'عید سعید فطر', 'Eid al-Fitr', true);
putL(10, 2, 'تعطیل به مناسبت عید سعید فطر', 'Eid al-Fitr Holiday', true);
putL(12, 10, 'عید سعید قربان', 'Eid al-Adha', true);
putL(12, 18, 'عید سعید غدیر خم', 'Eid al-Ghadir', true);

// ===== Lunar — occasions =====
putL(3, 8, 'شهادت امام حسن عسکری', 'Martyrdom of Imam Hassan Askari', false);
putL(9, 1, 'آغاز ماه مبارک رمضان', 'Beginning of Ramadan', false);
putL(9, 19, 'شب قدر', 'Laylat al-Qadr', false);
putL(9, 23, 'شب قدر', 'Laylat al-Qadr', false);

export interface DayInfo {
  events: Ev[];
  holiday: boolean;
  friday: boolean;
}

/** events + holiday flag for one day; includes the "end of Safar" rule */
export function forDay(jdn: number, hijriAdj: number): DayInfo {
  const j = d2j(jdn);
  const h = hijriFromJdn(jdn, hijriAdj);
  const list: Ev[] = [];
  const je = JAL.get(`${j.jm}/${j.jd}`);
  if (je) list.push(je);
  if (h.hm === 2 && h.hd === hijriMonthLength(2)) {
    list.push({ fa: 'شهادت امام رضا (آخر صفر)', en: 'Martyrdom of Imam Reza (End of Safar)', holiday: true });
  }
  const le = LUN.get(`${h.hm}/${h.hd}`);
  if (le) list.push(le);
  list.sort((a, b) => (a.holiday === b.holiday ? 0 : a.holiday ? -1 : 1));
  const holiday = list.some((e) => e.holiday);
  const friday = weekdayFromJdn(jdn) === 6;
  return { events: list, holiday: holiday || friday, friday };
}

/** is this day an official (civil) holiday? */
export function isHoliday(jdn: number, hijriAdj: number): boolean {
  const j = d2j(jdn);
  const h = hijriFromJdn(jdn, hijriAdj);
  const je = JAL.get(`${j.jm}/${j.jd}`);
  if (je && je.holiday) return true;
  if (h.hm === 2 && h.hd === hijriMonthLength(2)) return true;
  const le = LUN.get(`${h.hm}/${h.hd}`);
  return !!(le && le.holiday);
}

export interface NextHoliday { days: number; ev: Ev; jdn: number }

/** next official holiday from a given JDN (inclusive); searches maxDays ahead */
export function nextHoliday(jdn: number, hijriAdj: number, maxDays = 400): NextHoliday | null {
  for (let i = 0; i < maxDays; i++) {
    if (isHoliday(jdn + i, hijriAdj)) {
      const info = forDay(jdn + i, hijriAdj);
      const ev = info.events.find((e) => e.holiday) ?? info.events[0];
      if (ev) return { days: i, ev, jdn: jdn + i };
    }
  }
  return null;
}

/** days remaining until next Nowruz (1 Farvardin) from a JDN (0 = today) */
export function daysToNowruz(jdn: number): { days: number; jy: number } {
  const j = d2j(jdn);
  let target = j2d(j.jy + 1, 1, 1);
  let days = target - jdn;
  if (days < 0) {
    target = j2d(j.jy + 2, 1, 1);
    days = target - jdn;
  }
  return { days, jy: d2j(target).jy };
}

// re-exports for convenience
export { d2j, d2g, hijriFromJdn, hijriMonthLength, j2d, weekdayFromJdn };
