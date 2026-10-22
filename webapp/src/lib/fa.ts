/** Persian formatting helpers: digits, month names, weekday names. */

const FA_DIGITS = ['۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹'];

export function faNum(n: number | string): string {
  return String(n).replace(/\d/g, (d) => FA_DIGITS[+d]);
}

export function faPad2(n: number): string {
  return faNum(n < 10 ? `0${n}` : String(n));
}

export const WEEKDAYS = ['شنبه', 'یکشنبه', 'دوشنبه', 'سه‌شنبه', 'چهارشنبه', 'پنجشنبه', 'جمعه'];
export const WEEKDAYS_SHORT = ['ش', 'ی', 'د', 'س', 'چ', 'پ', 'ج'];

export const JALALI_MONTHS = [
  'فروردین', 'اردیبهشت', 'خرداد', 'تیر', 'مرداد', 'شهریور',
  'مهر', 'آبان', 'آذر', 'دی', 'بهمن', 'اسفند',
];

export const GREGORIAN_MONTHS = [
  'ژانویه', 'فوریه', 'مارس', 'آپریل', 'مه', 'ژوئن',
  'ژوئیه', 'اوت', 'سپتامبر', 'اکتبر', 'نوامبر', 'دسامبر',
];

export const HIJRI_MONTHS = [
  'محرم', 'صفر', 'ربیع‌الاول', 'ربیع‌الثانی', 'جمادی‌الاول', 'جمادی‌الثانی',
  'رجب', 'شعبان', 'رمضان', 'شوال', 'ذی‌القعده', 'ذی‌الحجه',
];

/** minutes-after-midnight -> ۱۴:۳۵ */
export function minutesToFaTime(m: number | null): string {
  if (m === null || !Number.isFinite(m)) return '--:--';
  const mm = ((m % 1440) + 1440) % 1440;
  const h = Math.floor(mm / 60);
  const min = mm % 60;
  return `${faPad2(h)}:${faPad2(min)}`;
}

/** format a triple date line like: ۲۹ آبان ۱۴۰۵ / 20 November 2025 / 8 جمادی‌الاول 1447 */
export function formatTriple(jy: number, jm: number, jd: number, gy: number, gm: number, gd: number, hy: number, hm: number, hd: number): string {
  return `${faNum(jd)} ${JALALI_MONTHS[jm - 1]} ${faNum(jy)} — ${faNum(gd)} ${GREGORIAN_MONTHS[gm - 1]} ${faNum(gy)} — ${faNum(hd)} ${HIJRI_MONTHS[hm - 1]} ${faNum(hy)}`;
}
