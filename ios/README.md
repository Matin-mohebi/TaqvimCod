# 🍎 Taqvim Kod — iOS (SwiftUI)

اپلیکیشن تقویم کد برای آیفون / آیپد — کاملاً بومی با SwiftUI، هم‌تراز با نسخه اندروید.

The iOS app is **100% native SwiftUI**, feature-matched with the Android version.

## ✨ ویژگی‌ها / Features

- تقویم جلالی دقیق (الگوریتم نجومی Borkowski — همان موتور نسخه اندروید)
- تاریخ قمری (الگوریتم کویتی با تصحیح قابل تنظیم، پیش‌فرض ایران ‎-۱)
- همه تعطیلات رسمی ایران (شمسی + قمری) و مناسبت‌ها
- **همگام‌سازی زنده با تقویم گوگل** از طریق EventKit — اگر اکانت گوگل در تنظیمات آیفون باشد، رویدادها مستقیم نمایش و ذخیره می‌شوند (افزودن/ویرایش/حذف)
- تم روشن / تاریک / خودکار
- زبان فارسی + English، اعداد فارسی، فونت وزیرمتن
- شمارش معکوس تا تعطیلی بعدی

## 🛠 نحوه ساخت (Build)

> برای کامپایل به یک **مک با Xcode 15 یا جدیدتر** نیاز دارید (این الزام اپل است).

1. کل ریپو را کلون کنید:
   ```bash
   git clone https://github.com/Matin-mohebi/TaqvimCod.git
   cd TaqvimCod/ios
   open TaqvimCod.xcodeproj
   ```
2. در Xcode:
   - تارگت **TaqvimCod** → تب **Signing & Capabilities**
   - **Team** خودتان را انتخاب کنید (Apple ID رایگان کافی است؛ برای نصب روی گوشی خودتان)
   - Bundle Identifier در صورت تمایل تغییر دهید
3. آیفون را با کابل وصل کنید → **Run** ▶

برای انتشار در **App Store / TestFlight**: اکانت Apple Developer ($99/سال) + Archive → Distribute.

### اجرای تست سریع بدون گوشی
در Xcode یک Simulator iPhone انتخاب کنید و Run بزنید.

## 🔧 ساخت فقط با خط فرمان (CI)

```bash
xcodebuild -project ios/TaqvimCod.xcodeproj -target TaqvimCod \
  -configuration Debug -destination 'generic/platform=iOS Simulator' \
  build CODE_SIGNING_ALLOWED=NO
```

این دستور در GitHub Actions (ایونت `workflow_dispatch` یا push روی `ios/**`) هم اجرا می‌شود
و کامپایل‌شدن پروژه را روی هر push تأیید می‌کند.

## 📁 ساختار پروژه

```
ios/
├── TaqvimCod.xcodeproj          ← پروژه آماده (بدون نیاز به XcodeGen)
└── TaqvimCod/
    ├── TaqvimCodApp.swift       ← نقطه ورود
    ├── DateEngine.swift         ← موتور تبدیل تاریخ (جلالی/قمری/میلادی + JDN)
    ├── Holidays.swift           ← دیتابیس تعطیلات ایران
    ├── EventStore.swift         ← EventKit (تقویم گوگل روی آیفون)
    ├── Strings.swift            ← رشته‌های دوزبانه + اعداد فارسی + پالت
    ├── HomeView.swift           ← صفحه اصلی (هدر گرادیانی، شبکه ماه، رویدادها، FAB)
    ├── MonthGridView.swift      ← شبکه تقویم RTL
    ├── EventEditorView.swift    ← فرم رویداد (DatePicker با تقویم شمسی!)
    ├── SettingsView.swift       ← تنظیمات + AboutView
    ├── Info.plist               ← مجوزهای تقویم + فونت‌ها
    ├── Fonts/                   ← وزیرمتن (4 وزن)
    └── Assets.xcassets/         ← آیکون 1024 + رنگ اکسنت
```

## ⚠️ نکته امنیتی

همان سیاست نسخه اندروید: هیچ داده‌ای به سرور ما ارسال نمی‌شود؛ همه‌چیز بین دستگاه شما و
اکانت گوگل خودتان جابه‌جا می‌شود (از طریق EventKit خود iOS).
