# تقویم کد — Taqvim Kod 📅

<div dir="rtl">

**تقویم ایرانی (شمسی) با همگام‌سازی زنده Google Calendar — دقیق • امن • زیبا**

اپ تقویم فارسی برای **اندروید و آیفون** با پشتیبانی کامل از تقویم گوگل: رویدادهای اکانت گوگل‌ت به‌صورت **لحظه‌ای** داخل تقویم شمسی نمایش داده می‌شن و می‌تونی از داخل خود اپ رویداد **اضافه، ویرایش یا حذف** کنی — تغییرات مستقیم روی تقویم گوگلت اعمال می‌شه.

</div>

**Iranian (Jalali) calendar with live Google Calendar sync for Android & iOS — accurate, secure, beautiful.**

## ✨ امکانات / Features

- 🗓 **تقویم شمسی (جلالی) دقیق** — الگوریتم نجومی Borkowski با پشتیبانی صحیح از سال‌های کبیسه
- 🔄 **همگام‌سازی دوطرفه با تقویم گوگل** — نمایش زنده، افزودن، ویرایش و حذف رویداد (بدون سرور واسط؛ اتصال مستقیم به اکانت گوگل گوشی)
- ⚡ **به‌روزرسانی لحظه‌ای** — هر تغییری در تقویم گوگل (از هر دستگاهی) بلافاصله داخل اپ ظاهر می‌شه
- 🇮🇷 **تعطیلات رسمی و مناسبت‌های ایران** — تعطیلات شمسی و قمری با هایلایت قرمز
- ☪ **تاریخ قمری (هجری)** کنار شمسی و میلادی + تنظیم تصحیح (پیش‌فرض مناسب ایران: ‎-۱)
- ⏳ **کارت شمارش معکوس** تا تعطیلی بعدی (مثلاً «۲۵ روز تا عید نوروز»)
- 📱 **ویجت صفحه اصلی** (اندروید) — تاریخ امروز، تعطیلی و رویداد بعدی
- 🌗 **تم روشن + تاریک + خودکار** — پالت فیروزه‌ای و لاجوردی با هدر گرادیانی
- 🌐 **دوزبانه** فارسی / English با اعداد فارسی و فونت وزیرمتن، طراحی RTL کامل
- 🔒 **امنیت بالا** (جدید در نسخه ۱.۱.۰):
  - **بدون دسترسی اینترنت** — داده‌ها هرگز از گوشی خارج نمی‌شن
  - تنظیمات با **AES-256-GCM + Android Keystore** رمزنگاری می‌شن
  - قابلیت **جلوگیری از اسکرین‌شات** (FLAG_SECURE) در تنظیمات
  - غیرفعال‌بودن پشتیبان‌گیری ابری (allowBackup = false)
  - کد Release با **R8 مبهم‌سازی** می‌شه (obfuscation + shrinking)
- 👤 بخش **درباره و تماس با ما** + راهنمای حریم خصوصی

## 📲 نصب / Install (Android)

### راه ۱: دانلود از Releases
آخرین نسخه از بخش [Releases](../../releases) رو دانلود و نصب کن (`TaqvimCod-vX.X.X-release.apk`).

### راه ۲: بیلد خودکار با GitHub Actions
زبونه **Actions** → ورک‌فلو **Build** → دانلود آرتیفکت `TaqvimCod-APK`.

<div dir="rtl">

### ⚠️ پیام «Google Play Protect»؟
اگه موقع نصب پیام هشدار Play Protect دیدی نگران نباش — این پیام برای **هر** اپی که خارج از فروشگاه گوگل نصب می‌شه نمایش داده می‌شه و ربطی به خطرناک بودن برنامه نداره. روی پیام بزن و **«نصب در هر صورت»** رو انتخاب کن. توضیح کامل: [راهنمای فارسی](docs/PLAY-PROTECT-FA.md)

</div>

> برای نمایش و ثبت رویدادها، اجازه دسترسی تقویم رو تأیید کن.

## 🍎 نسخه آیفون / iOS

اپ iOS با **SwiftUI** در همین ریپو موجوده: [`ios/`](ios/README.md)
- همون موتور دقیق تقویم، همون تعطیلات، همون طراحی
- همگام‌سازی با تقویم گوگل از طریق EventKit خود iOS (اکانت گوگل داخل تنظیمات آیفون)
- نحوه بیلد: [راهنمای iOS](ios/README.md) (نیاز به مک + Xcode 15+)

## 🛠 بیلد از سورس / Build from source

**Android:** پیش‌نیازها JDK 17+ و Android SDK (پلتفرم 35)

```bash
git clone https://github.com/Matin-mohebi/TaqvimCod.git
cd TaqvimCod
./gradlew assembleDebug      # نسخه تست
./gradlew assembleRelease    # نسخه امضاشده (اگه keystore موجود باشه)
```

**iOS:** باز کن `ios/TaqvimCod.xcodeproj` در Xcode 15+ → Run

## 🔐 امنیت و حریم خصوصی / Security & Privacy

<div dir="rtl">

تقویم کد **هیچ مجوز اینترنتی ندارد**؛ بنابراین از نظر فنی غیرممکنه که اطلاعاتت جایی آپلود بشه. رویدادها فقط بین اپ و تقویم خودِ گوشی (اکانت گوگل خودت) جابه‌جا می‌شن. جزئیات کامل: [PRIVACY.md](PRIVACY.md) • گزارش آسیب‌پذیری: [SECURITY.md](SECURITY.md)

</div>

Taqvim Kod has **no INTERNET permission** — your data physically cannot leave the device.
Details: [PRIVACY.md](PRIVACY.md) • Responsible disclosure: [SECURITY.md](SECURITY.md)

## 📋 تغییرات نسخه‌ها / Changelog

- **v1.1.0** — طراحی کاملاً جدید (هدر گرادیانی، کارت‌های شیک)، امنیت بالا: رمزنگاری Keystore، ضداسکرین‌شات، R8، targetSdk 35، راهنمای Play Protect، **افزودن اپ iOS (SwiftUI)**
- **v1.0.0** — نسخه اولیه اندروید

## 👤 توسعه‌دهنده / Developer

**Matin**
- 🌐 https://matinzeus.ir
- 🌐 https://matin-wp-pro.ir
- 🌐 https://dyno-tuning-znj.ir
- 🌐 https://elambar-zanjan.ir
- 📞 09195394891
- 📷 Instagram: [@matin_web_site](https://instagram.com/matin_web_site)

<div dir="rtl">

## 📄 لایسنس / License

MIT — جزئیات در [LICENSE](LICENSE)

</div>
