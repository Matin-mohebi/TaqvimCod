# تقویم کد — Taqvim Kod 📅

<div dir="rtl">

**تقویم ایرانی (شمسی) با دسترسی به رویدادهای تقویم سیستم — دقیق • امن • زیبا**

🌐 **وب‌سایت رسمی و نسخه وب تقویم:** <https://matin-mohebi.github.io/TaqvimCod/>

اپ تقویم فارسی برای **اندروید و آیفون**. نسخه اندروید از طریق تقویم سیستم گوشی به رویدادهای تقویم‌های قابل‌دسترسی می‌رسد و می‌تواند رویدادها را بخواند یا تغییر دهد. همگام‌سازی با حساب گوگل به تنظیمات همگام‌سازی دستگاه و نوع تقویم انتخاب‌شده بستگی دارد؛ خود برنامه به سرور اختصاصی وصل نمی‌شود.

</div>

**Iranian (Jalali) calendar with system-calendar access — accurate, privacy-conscious, and beautiful.**

## ✨ امکانات / Features

- 🗓 **تقویم شمسی (جلالی) دقیق** — الگوریتم نجومی Borkowski با پشتیبانی صحیح از سال‌های کبیسه
- 🔄 **دسترسی به تقویم سیستم** — خواندن و افزودن/ویرایش/حذف رویدادها در تقویم‌هایی که دستگاه اجازه می‌دهد؛ همگام‌سازی ابری تابع تنظیمات حساب و خود دستگاه است
- ⚡ **به‌روزرسانی رویدادها** — برنامه تغییرات ارائه‌شده توسط تقویم سیستم را دنبال می‌کند؛ زمان همگام‌سازی حساب گوگل در اختیار سیستم و ارائه‌دهنده تقویم است
- 🇮🇷 **تعطیلات رسمی و مناسبت‌های ایران** — تعطیلات شمسی و قمری با هایلایت قرمز
- ☪ **تاریخ قمری (هجری)** کنار شمسی و میلادی + تنظیم تصحیح (پیش‌فرض مناسب ایران: ‎-۱)
- ⏳ **کارت شمارش معکوس** تا تعطیلی بعدی (مثلاً «۲۵ روز تا عید نوروز»)
- 📱 **ویجت صفحه اصلی** (اندروید) — تاریخ امروز، تعطیلی و رویداد بعدی
- 🌗 **تم روشن + تاریک + خودکار** — پالت فیروزه‌ای و لاجوردی با هدر گرادیانی
- 🌐 **دوزبانه** فارسی / English با اعداد فارسی و فونت وزیرمتن، طراحی RTL کامل
- 🔒 **امنیت بالا** (جدید در نسخه ۱.۱.۰):
  - **بدون مجوز اینترنت برای خود برنامه** — برنامه درخواست شبکه نمی‌فرستد؛ بااین‌حال رویدادهایی که در تقویم ابری مثل Google Calendar ذخیره می‌شوند ممکن است طبق تنظیمات حساب توسط سرویس تقویم همگام‌سازی شوند
  - تنظیمات با **AES-256-GCM** رمزنگاری می‌شن و در صورت دسترس‌بودن از **Android Keystore** استفاده می‌شود
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
./gradlew testDebugUnitTest  # تست‌های موتور تاریخ
./gradlew assembleDebug      # نسخه تست
./gradlew assembleRelease    # بدون کلید تنظیم‌شده، خروجی Release امضانشده است
```

برای امضای Release، کلید را جدا از مخزن نگه دارید و این متغیرهای محیطی را تنظیم کنید: `TAQVIMCOD_STORE_FILE`, `TAQVIMCOD_STORE_PASSWORD`, `TAQVIMCOD_KEY_ALIAS`, `TAQVIMCOD_KEY_PASSWORD`. هرگز رمز یا فایل keystore را به Git اضافه نکنید. در GitHub Actions نیز از Secrets با همین نام‌ها و `TAQVIMCOD_KEYSTORE_BASE64` استفاده کنید؛ مقدار Base64 باید فقط حاوی keystore خصوصی باشد.

**iOS:** باز کن `ios/TaqvimCod.xcodeproj` در Xcode 15+ → Run

## 🔐 امنیت و حریم خصوصی / Security & Privacy

<div dir="rtl">

تقویم کد **مجوز اینترنت ندارد** و خود برنامه درخواست شبکه ارسال نمی‌کند. اما اگر رویدادی را در تقویمی ذخیره کنی که با یک سرویس ابری (مثل حساب گوگل) همگام می‌شود، ارائه‌دهنده تقویم ممکن است طبق تنظیمات همان حساب آن رویداد را همگام‌سازی کند. این رفتار در اختیار برنامه تقویم کد نیست. جزئیات کامل: [PRIVACY.md](PRIVACY.md) • گزارش آسیب‌پذیری: [SECURITY.md](SECURITY.md)

</div>

Taqvim Kod has **no INTERNET permission** and the app itself makes no network requests. Events saved in a cloud-backed calendar (such as Google Calendar) may still sync according to the account and device settings; that sync is performed by the calendar provider, not by Taqvim Kod.
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
