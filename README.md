<div align="center">

<img src="docs/banner.svg" alt="Taqvim — Persian Calendar" width="100%">

# تقویم | Taqvim

### تقویم فارسی مدرن برای اندروید و iOS

تقویم شمسی، میلادی و قمری · مدیریت رویدادها · اوقات شرعی · قبله‌نما · ابزارهای تاریخ

[مشاهدهٔ کد منبع](https://github.com/Matin-mohebi/TaqvimCod) · [ساخت‌های APK](https://github.com/Matin-mohebi/TaqvimCod/actions) · [گزارش مشکل](https://github.com/Matin-mohebi/TaqvimCod/issues) · [تغییرات نسخه](CHANGELOG.md)

![Android](https://img.shields.io/badge/Android-10%2B-3DDC84?logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-64748b)
![Version](https://img.shields.io/badge/version-1.3.0-0f766e)
![Build](https://github.com/Matin-mohebi/TaqvimCod/actions/workflows/android-build.yml/badge.svg)

</div>

---

## ✨ دربارهٔ پروژه

**Taqvim** یک تقویم فارسی با رابط راست‌چین و تمرکز بر حریم خصوصی است. محاسبات تاریخ و ابزارهای تاریخ بدون نیاز به اینترنت انجام می‌شوند؛ رویدادهای شخصی با اجازهٔ کاربر از تقویم خود دستگاه خوانده و در همان مسیر ذخیره می‌شوند.

> وضعیت انتشار: نسخهٔ کد 1.3.0 است. فایل APK رسمی فقط زمانی منتشرشده محسوب می‌شود که در بخش [Releases](https://github.com/Matin-mohebi/TaqvimCod/releases) قرار بگیرد. فایل‌های Actions خروجی آزمایشی هستند.

## 🚀 امکانات

| بخش | امکانات |
|---|---|
| 📅 تقویم | تقویم جلالی، انتخاب ماه و سال، جابه‌جایی ماه‌ها و رفتن سریع به امروز |
| 🌐 سه تقویم | تبدیل شمسی و میلادی و نمایش تاریخ قمری محاسباتی |
| 🗓️ مناسبت‌ها | تعطیلات و مناسبت‌های ثبت‌شدهٔ ایران |
| 🔔 رویدادها | مشاهده و مدیریت رویدادهای تقویم دستگاه با مجوز صریح کاربر |
| 🧮 ابزارهای تاریخ | تبدیل تاریخ، محاسبهٔ فاصلهٔ دو تاریخ، شمارش معکوس نوروز |
| 📋 کپی و اشتراک | کپی یا اشتراک تاریخ امروز و کپی نتیجهٔ تبدیل |
| 🕌 اوقات شرعی | محاسبهٔ آفلاین اوقات شرعی با روش قابل انتخاب و مختصات قابل ویرایش |
| 🧭 قبله‌نما | نمایش جهت قبله با حسگرهای سازگار دستگاه |
| ⏰ یادآوری | اعلان‌های اختیاری اوقات شرعی و زمان‌بندی مجدد پس از راه‌اندازی دستگاه |
| 🧩 ویجت | دسترسی سریع از صفحهٔ اصلی اندروید |
| 🎨 ظاهر | تم روشن، تاریک و خودکار؛ رابط فارسی و انگلیسی |
| 🔐 حریم خصوصی | محاسبات محلی؛ بدون مجوز مستقیم اینترنت در برنامهٔ اندروید |

## 📦 دریافت APK

1. وارد [آخرین اجراهای GitHub Actions](https://github.com/Matin-mohebi/TaqvimCod/actions/workflows/android-build.yml) شو.
2. یک اجرای **موفق (Success)** را باز کن.
3. از بخش **Artifacts** فایل `TaqvimCod-debug-apk` را دریافت کن و ZIP را استخراج کن.

خروجی Debug برای آزمایش است و ممکن است امضای انتشار رسمی نداشته باشد. برای نصب و به‌روزرسانی مطمئن، از یک کلید امضای خصوصی ثابت استفاده می‌شود؛ کلید و رمز آن نباید در مخزن عمومی قرار بگیرند.

## 🛠️ ساخت از کد منبع

### پیش‌نیازها
- JDK 17
- Android SDK و Build Tools سازگار با پروژه
- اتصال اینترنت برای دریافت ابزارهای Gradle در اولین ساخت

```bash
git clone https://github.com/Matin-mohebi/TaqvimCod.git
cd TaqvimCod
./gradlew testDebugUnitTest
./gradlew assembleDebug
```

فایل خروجی Debug پس از ساخت در مسیر زیر قرار می‌گیرد:

```text
app/build/outputs/apk/debug/app-debug.apk
```

ساخت Release:

```bash
./gradlew assembleRelease
```

برای Release قابل انتشار، اطلاعات keystore را با Gradle Properties یا متغیرهای محیطی تنظیم کن. فایل keystore، رمزها و توکن‌های دسترسی را commit نکن.

## 🧱 ساختار پروژه

```text
TaqvimCod/
├── app/                         # برنامهٔ بومی Android
│   └── src/main/java/...        # تقویم، تاریخ‌ها، رویدادها و ابزارها
├── ios/                         # پروژهٔ iOS
├── site/                        # وب‌سایت معرفی پروژه
├── docs/banner.svg              # تصویر سربرگ مخزن
├── .github/workflows/           # ساخت و تست خودکار
├── CHANGELOG.md                 # تاریخچهٔ تغییرات
├── PRIVACY.md                   # سیاست حریم خصوصی
└── SECURITY.md                  # گزارش مسئولانهٔ مشکلات امنیتی
```

## 🔒 حریم خصوصی

- برنامه برای تبدیل تاریخ و ابزارهای آفلاین به اینترنت نیاز ندارد.
- مجوز تقویم فقط برای نمایش و مدیریت رویدادهای دستگاه درخواست می‌شود.
- اگر تقویم دستگاه به حساب ابری متصل باشد، همگام‌سازی آن تابع تنظیمات سیستم‌عامل و همان حساب است.
- مختصات شهر برای محاسبهٔ اوقات شرعی در دستگاه استفاده می‌شود؛ برنامه مجوز مکان‌یابی خودکار درخواست نمی‌کند.
- جزئیات کامل: [سیاست حریم خصوصی](PRIVACY.md) و [سیاست امنیتی](SECURITY.md).

## 🧪 کیفیت و تست

هر تغییر در شاخهٔ اصلی با GitHub Actions ساخته می‌شود و تست‌های واحد و تولید APK Debug اجرا می‌شوند. وضعیت دقیق آخرین اجرا را در [Actions](https://github.com/Matin-mohebi/TaqvimCod/actions) ببین.

## 🌱 مشارکت و گزارش مشکل

برای پیشنهاد قابلیت یا گزارش خطا، یک [Issue](https://github.com/Matin-mohebi/TaqvimCod/issues/new/choose) ثبت کن. لطفاً نسخهٔ اندروید، مدل دستگاه، مراحل بازتولید مشکل و گزارش خطا را بدون اطلاعات خصوصی بنویس.

## 👨‍💻 توسعه‌دهنده

**Matin Mohebi** — توسعهٔ نرم‌افزار و برنامه‌های کاربردی

- GitHub: [@Matin-mohebi](https://github.com/Matin-mohebi)
- وب‌سایت پروژه: [TaqvimCod](https://matin-mohebi.github.io/TaqvimCod/)

---

<div align="center">

ساخته‌شده با دقت برای تجربهٔ بهتر تقویم فارسی 💚

</div>
