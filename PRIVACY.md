# سیاست حریم خصوصی — تقویم / Privacy Policy

<div dir="rtl">

## فارسی

تاریخ آخرین بروزرسانی: اکتبر ۲۰۲۶ (مهر ۱۴۰۵)

### اصل کلی
تقویم **هیچ مجوز اینترنتی ندارد** (`android.permission.INTERNET` درخواست نشده است). این یعنی خود برنامه نمی‌تواند مستقیماً درخواست شبکه ارسال کند. بااین‌حال، رویدادهایی که در یک تقویم ابری (مثلاً تقویم متصل به حساب گوگل) ذخیره می‌شوند ممکن است طبق تنظیمات حساب و دستگاه توسط ارائه‌دهنده تقویم همگام‌سازی شوند؛ این ارتباط را برنامه تقویم انجام نمی‌دهد.

### چه داده‌هایی پردازش می‌شود؟
1. **رویدادهای تقویم** — فقط از طریق تقویم سیستم گوشی (اکانت گوگل خودِ کاربر) خوانده و نوشته می‌شوند. برنامه کپی جداگانه‌ای از رویدادها در سرور خودش ذخیره نمی‌کند؛ ارائه‌دهنده تقویم ممکن است طبق تنظیمات همگام‌سازی خودش نسخه ابری داشته باشد.
2. **تنظیمات برنامه** (تم، زبان، تصحیح قمری) — روی خود دستگاه با **رمزنگاری AES-256-GCM** و کلید داخل **Android Keystore** ذخیره می‌شوند؛ در حالت عادی کلید از طریق API برنامه قابل خواندن نیست؛ در دستگاه‌هایی که Android Keystore در دسترس نباشد، برنامه از کلید محلیِ جایگزین استفاده می‌کند که سطح حفاظت آن با Keystore یکسان نیست.
3. **داده‌های شخصی حساس** (موقعیت مکانی، مخاطبین، دوربین، میکروفون، عکس) — هیچ‌کدام درخواست یا پردازش نمی‌شوند.

### پشتیبان‌گیری ابری
پشتیبان‌گیری خودکار اندروید و انتقال داده بین دستگاه‌ها برای این برنامه **غیرفعال** است (`allowBackup=false` + `dataExtractionRules`).

### اشتراک‌گذاری با شخص ثالث
هیچ SDK، تبلیغات، تحلیل‌گر (Analytics) یا ردیابی در برنامه وجود ندارد. برنامه **صفر وابستگی خارجی** دارد.

### کودکان
برنامه برای تمام سنین مناسب است و هیچ داده‌ای جمع‌آوری نمی‌کند.

### تغییرات این سیاست
هر تغییری در همین فایل (PRIVACY.md) در ریپوی GitHub منتشر می‌شود.

---

</div>

## English

Last updated: October 2025

### Core principle
تقویم has **no INTERNET permission** (`android.permission.INTERNET` is never requested). Therefore, the app itself cannot directly make network requests. However, events saved into a cloud-backed calendar (for example, a calendar connected to a Google account) may be synced by that calendar provider according to account and device settings; that network activity is not performed by تقویم.

### What data is processed?
1. **Calendar events** — read/written only through the device's system calendar provider (the user's own Google account). The app does not copy events to its own server. A calendar provider may keep a cloud copy according to its sync settings.
2. **App settings** (theme, language, Hijri correction) — stored on-device, encrypted with **AES-256-GCM** using a key inside the **Android Keystore**; the Android Keystore key is not exposed through the app API under normal operation. If Android Keystore is unavailable, a local fallback key is used and does not provide the same protection level.
3. **Sensitive personal data** (location, contacts, camera, microphone, photos) — never requested or processed.

### Cloud backup
Android auto-backup and device-to-device migration are **disabled** for this app (`allowBackup=false` + `dataExtractionRules`).

### Third parties
No SDKs, ads, analytics or trackers. The app has **zero external dependencies**.

### Children
Safe for all ages; no data collection of any kind.

### Changes to this policy
Any change is published in this file (PRIVACY.md) in the GitHub repository.

## تماس / Contact
Matin — https://matinzeus.ir — 09195394891 — instagram.com/matin_web_site
