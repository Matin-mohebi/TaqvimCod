/** Offline list of Iranian cities with approximate center coordinates.
 *  Port of the Android app's CityDatabase.java (Zanjan first — it is the
 *  maintainer's home city — then Tehran and the rest). */
export interface City {
  name: string;
  lat: number;
  lon: number;
}

export const CITIES: City[] = [
  { name: 'زنجان', lat: 36.6736, lon: 48.4787 },
  { name: 'تهران', lat: 35.6892, lon: 51.3890 },
  { name: 'مشهد', lat: 36.2972, lon: 59.6067 },
  { name: 'اصفهان', lat: 32.6546, lon: 51.6680 },
  { name: 'شیراز', lat: 29.5918, lon: 52.5837 },
  { name: 'تبریز', lat: 38.0800, lon: 46.2919 },
  { name: 'اهواز', lat: 31.3183, lon: 48.6706 },
  { name: 'قم', lat: 34.6401, lon: 50.8764 },
  { name: 'کرج', lat: 35.8400, lon: 50.9391 },
  { name: 'کرمانشاه', lat: 34.3277, lon: 47.0778 },
  { name: 'رشت', lat: 37.2808, lon: 49.5832 },
  { name: 'ارومیه', lat: 37.5527, lon: 45.0761 },
  { name: 'یزد', lat: 31.8974, lon: 54.3569 },
  { name: 'همدان', lat: 34.7989, lon: 48.5150 },
  { name: 'قزوین', lat: 36.2797, lon: 50.0049 },
  { name: 'اردبیل', lat: 38.2498, lon: 48.2933 },
  { name: 'بندرعباس', lat: 27.1832, lon: 56.2666 },
  { name: 'ساری', lat: 36.5633, lon: 53.0601 },
  { name: 'کرمان', lat: 30.2839, lon: 57.0834 },
  { name: 'زاهدان', lat: 29.4963, lon: 60.8629 },
  { name: 'ایلام', lat: 33.6374, lon: 46.4227 },
  { name: 'بوشهر', lat: 28.9234, lon: 50.8203 },
  { name: 'شهرکرد', lat: 32.3256, lon: 50.8644 },
  { name: 'بیرجند', lat: 32.8663, lon: 59.2211 },
  { name: 'بجنورد', lat: 37.4750, lon: 57.3290 },
  { name: 'یاسوج', lat: 30.6682, lon: 51.5879 },
  { name: 'گرگان', lat: 36.8456, lon: 54.4393 },
  { name: 'خرم‌آباد', lat: 33.4878, lon: 48.3558 },
  { name: 'سنندج', lat: 35.3149, lon: 46.9988 },
  { name: 'اراک', lat: 34.0917, lon: 49.6892 },
  { name: 'سمنان', lat: 35.5769, lon: 53.3970 },
];
