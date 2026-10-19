# تقویم کد — Taqvim Kod 📅

<div dir="rtl">

**تقویم ایرانی (شمسی) با همگام‌سازی لحظه‌ای Google Calendar — دقیق، سریع و دوزبانه**

اپ اندروید تقویم فارسی با پشتیبانی کامل از تقویم گوگل: رویدادهای اکانت گوگل‌ت به‌صورت **لحظه‌ای** داخل تقویم شمسی نمایش داده می‌شن و می‌تونی از داخل خود اپ رویداد **اضافه، ویرایش یا حذف** کنی — تغییرات مستقیم روی تقویم گوگلت اعمال می‌شه.

## ✨ امکانات

- 🗓 **تقویم شمسی (جلالی) دقیق** — الگوریتم نجومی Borkowski با پشتیبانی صحیح از سال‌های کبیسه
- 🔄 **همگام‌سازی دوطرفه با تقویم گوگل** — نمایش زنده، افزودن، ویرایش و حذف رویداد (بدون سرور واسط؛ اتصال مستقیم به اکانت گوگل گوشی)
- ⚡ **به‌روزرسانی لحظه‌ای** — هر تغییری در تقویم گوگل (از هر دستگاهی) بلافاصله داخل اپ ظاهر می‌شه
- 🇮🇷 **تعطیلات رسمی و مناسبت‌های ایران** — تعطیلات شمسی و قمری با هایلایت قرمز
- ☪ **تاریخ قمری (هجری)** کنار شمسی و میلادی + تنظیم تصحیح (پیش‌فرض مناسب ایران: -۱)
- ⏳ **شمارش معکوس** تا تعطیلی بعدی (مثلاً «۲۵ روز تا عید نوروز»)
- 📱 **ویجت صفحه اصلی** — تاریخ امروز، تعطیلی و رویداد بعدی
- 🌗 **تم روشن + تاریک + خودکار** (بر اساس تم سیستم)
- 🌐 **دوزبانه** فارسی / English با اعداد فارسی
- 🖋 **فونت وزیرمتن** و طراحی RTL کامل
- 👤 بخش **درباره و تماس با ما**

## 📲 نصب APK

### راه ۱: دانلود از Releases
آخرین نسخه از بخش [Releases](../../releases) همین ریپو رو دانلود و نصب کن (فایل `taqvim-kod-vX.X.X-release.apk`).

### راه ۲: بیلد خودکار با GitHub Actions
زبونه **Actions** رو باز کن → ورک‌فلو **Build APK** → دانلود آرتیفکت `TaqvimCod-APK` (شامل نسخه debug و release).

> هنگام نصب، پیام «نصب از منابع ناشناس» طبیعیه؛ برای نمایش و ثبت رویدادها، اجازه دسترسی تقویم رو تأیید کن.

## 🛠 بیلد از سورس

پیش‌نیازها: **JDK 17+** و **Android SDK (پلتفرم 34)**

```bash
git clone https://github.com/Matin-mohebi/TaqvimCod.git
cd TaqvimCod
./gradlew assembleDebug     # نسخه تست
./gradlew assembleRelease   # نسخه نهایی (اگر keystore موجود باشه امضا می‌شه)
```

خروجی: `app/build/outputs/apk/`

یا کل پروژه رو مستقیم در **Android Studio** باز کن.

## 🔐 امضای نسخه Release

فایل `taqvimcod.keystore` در ریپو **commit نمی‌شه** (به‌دلایل امنیتی). برای ساخت مجدد keystore:

```bash
keytool -genkeypair -keystore taqvimcod.keystore -alias taqvimcod \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -storepass "TaqvimKod@1403" -dname "CN=Taqvim Kod,O=TaqvimCod,C=IR"
```

فایل رو کنار `build.gradle` ریشه قرار بده؛ بیلد release خودکار امضا می‌شه. (پسورد فعلی: `TaqvimKod@1403` — برای انتشار عمومی حتماً عوضش کن.)

## 🧭 معماری و دقت

| بخش | توضیح |
|---|---|
| تبدیل جلالی | الگوریتم نجومی Borkowski (پورت `jalaali-js`) — دقیق در محدوده بسیار وسیع سال‌ها |
| تاریخ قمری | تقویم جدولی (Kuwaiti/Fourmilab) + تنظیم تصحیح ‎-۲…+۲ روز |
| اتصال گوگل | `CalendarContract` اندروید — رویدادهای اکانت گوگل سینک‌شده روی گوشی، بدون نیاز به سرور یا OAuth API |
| سینک لحظه‌ای | `ContentObserver` روی provider تقویم؛ هر تغییر (گوشی/سایت/بقیه دستگاه‌ها) فوراً رفرش می‌شه |
| زبان | Java خالص + Android SDK (بدون هیچ وابستگی خارجی) — APK سبک (~۱ مگابایت) |

### ساختار پروژه

```
app/src/main/java/com/taqvimcod/calendar/
├── Dates.java          # تبدیل دقیق جلالی/قمری/میلادی + JDN
├── Holidays.java       # تعطیلات رسمی و مناسبت‌های ایران
├── CalendarRepo.java   # خواندن/نوشتن تقویم گوگل
├── MonthView.java      # نمای ماه (Canvas, RTL, Swipe)
├── MainActivity.java   # صفحه اصلی + سینک لحظه‌ای
├── UiDialogs.java      # فرم رویداد، تنظیمات، تماس با ما
├── WidgetProvider.java # ویجت صفحه اصلی
├── S.java              # متن‌های دوزبانه + اعداد فارسی
├── Theme.java          # تم روشن/تاریک/خودکار
└── Prefs.java          # تنظیمات
```

### مجوزهای موردنیاز

- `READ_CALENDAR` / `WRITE_CALENDAR` — نمایش و ثبت رویدادهای تقویم گوگل
- `RECEIVE_BOOT_COMPLETED` — به‌روزرسانی ویجت بعد از ری‌استارت گوشی

## 👤 تماس با ما

- 🌐 **وب‌سایت‌ها:** [matinzeus.ir](https://matinzeus.ir) · [matin-wp-pro.ir](https://matin-wp-pro.ir) · [dyno-tuning-znj.ir](https://dyno-tuning-znj.ir) · [elambar-zanjan.ir](https://elambar-zanjan.ir)
- 📞 **تلفن:** [09195394891](tel:09195394891)
- 📷 **اینستاگرام:** [@matin_web_site](https://instagram.com/matin_web_site)

</div>

---

# Taqvim Kod (English)

**Iranian Jalali calendar app for Android with real-time two-way Google Calendar sync.**

- ✅ Astronomically accurate Jalali conversion (Borkowski algorithm)
- ✅ Two-way Google Calendar sync via `CalendarContract` — view, add, edit, delete events; changes land in your Google account instantly
- ✅ Live updates via `ContentObserver` (no polling)
- ✅ Iranian official holidays & occasions (solar + lunar), red highlights
- ✅ Hijri date with adjustable correction (default -1 for Iran)
- ✅ Countdown to the next holiday
- ✅ Home-screen widget (today's date, holiday, next event)
- ✅ Light / Dark / Auto theme, Persian + English UI with Persian digits, Vazirmatn font, full RTL
- ✅ Zero third-party dependencies — tiny ~1 MB APK

**Build:** JDK 17+ and Android SDK 34 → `./gradlew assembleDebug` (or open in Android Studio).
**Download:** check the [Releases](../../releases) or the **Build APK** GitHub Action artifact.

**Contact:** [matinzeus.ir](https://matinzeus.ir) · Phone [09195394891](tel:09195394891) · Instagram [@matin_web_site](https://instagram.com/matin_web_site)

License: [MIT](LICENSE) © Matin
