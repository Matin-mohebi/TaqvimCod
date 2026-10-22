# Data Safety & Content Rating — آمادهٔ ارسال در کنسول Play

<div dir="rtl">

این فایل پاسخ دقیق فرم‌های «ایمنی داده» (Data Safety) و «درجه‌بندی محتوا» (Content Rating) کنسول Google Play را برای برنامهٔ تقویم دارد. فقط عیناً انتخاب‌ها را وارد کنید.

---

## بخش ۱ — فرم Data Safety

### سوال اول: Does your app collect or share any of the required user data types?

**پاسخ: `No`** ✅

دلیل (برای اطمینان خودتان، در کنسول وارد نمی‌شود): برنامه مجوز `android.permission.INTERNET` ندارد، پس هیچ داده‌ای را از دستگاه خارج نمی‌کند؛ هیچ SDK، تبلیغات یا Analytics ندارد و هیچ داده‌ای روی سرور ذخیره نمی‌شود. رویدادهای کاربر فقط در تقویم سیستم (حساب خود کاربر) و تنظیمات فقط روی دستگاه ذخیره می‌شوند.

با انتخاب `No` بقیهٔ بخش‌های فرم ایمنی داده ساده می‌شود و نیازی به اعلام انواع داده نیست.

### سوال‌های تکمیلی که ممکن است کنسول بپرسد

| سوال | پاسخ |
|---|---|
| Is all of the user data collected by your app encrypted in transit? | Not applicable (no data collected) |
| Do you provide a way for users to request that their data is deleted? | Not applicable (no data collected; uninstalling the app deletes all local data) |

### یادداشت دربارهٔ Google Calendar

رویدادهایی که کاربر در حساب گوگل خودش نگه می‌دارد طبق تنظیمات خود حسابش توسط گوگل همگام‌سازی می‌شوند؛ این انتقال توسط برنامه انجام نمی‌شود و برنامه هیچ کپی جداگانه‌ای نگه نمی‌دارد. اگر کنسول جداگانه دربارهٔ دسترسی به Calendar API پرسید: برنامه از CalendarProvider اندروید استفاده می‌کند (نه از Google Calendar REST API)، بنابراین **نیازی به ثبت Google API Console و تأیید OAuth برای این برنامه نیست**.

---

## بخش ۲ — فرم Content Rating (IARC)

پرسش‌نامهٔ IARC را با این پاسخ‌ها پر کنید (سوال‌ها دسته‌بندی‌شده؛ پاسخ همهٔ موارد نامبرده «No» است):

| دسته | پاسخ |
|---|---|
| Category: Violence — any violent content? | **No** |
| Sexual content / nudity? | **No** |
| Language — offensive language? | **No** |
| Controlled substances? | **No** |
| User interaction — can users interact with each other (chat, share content, buy/sell)? | **No** |
| Sharing user's location with other users? | **No** |
| Digital purchases? | **No** |
| Gambling — any gambling, contests or betting? | **No** |
| Health — medical information or reminders? | **No** |
| Does the app collect/share any user-provided personal info? | **No** |

**نتیجهٔ مورد انتظار:** درجهٔ «Everyone» (۳+) در همهٔ مناطق.

---

## بخش ۳ — سایر فرم‌های لازم

| بخش کنسول | پاسخ |
|---|---|
| Privacy Policy (URL) | `https://github.com/Matin-mohebi/TaqvimCod/blob/main/PRIVACY.md` |
| Ads | No, my app contains no ads |
| App access | All functionality is available without restrictions (no login) |
| Government apps | No |
| Financial features | None of these |
| Health apps | None of these |
| News apps | None of these |
| COVID-19 apps | No |
| Data safety form | answered above |
| Target audience | 13+ (or “Everyone”) — برنامه برای همهٔ سنین بی‌خطر است |
| Is this app designed primarily for children? | **No** |

</div>
