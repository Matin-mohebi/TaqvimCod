package com.taqvimcod.calendar;

import android.content.Context;
import java.util.HashMap;

/**
 * Bilingual strings (fa / en) + Persian digit formatting.
 * All UI texts go through S.t(ctx, key).
 */
public final class S {

    private static final HashMap<String, String> FA = new HashMap<>();
    private static final HashMap<String, String> EN = new HashMap<>();

    private S() {}

    static {
        // ---- fa ----
        FA.put("app_name", "تقویم");
        FA.put("today", "امروز");
        FA.put("settings", "تنظیمات");
        FA.put("previous_month", "ماه قبل");
        FA.put("next_month", "ماه بعد");
        FA.put("events", "رویدادها");
        FA.put("events_count", "رویدادها (%s)");
        FA.put("no_events", "رویدادی برای این روز ثبت نشده است");
        FA.put("no_events_hint", "با دکمه + رویداد جدید اضافه کنید");
        FA.put("perm_title", "اجازه دسترسی به تقویم");
        FA.put("perm_text", "برای نمایش و ثبت رویدادهای تقویم گوگل، دسترسی تقویم لازم است.");
        FA.put("grant", "اجازه می‌دهم");
        FA.put("open_settings", "رفتن به تنظیمات");
        FA.put("add_event", "رویداد جدید");
        FA.put("edit_event", "ویرایش رویداد");
        FA.put("title", "عنوان");
        FA.put("hint_title", "مثلاً جلسه کاری");
        FA.put("all_day", "تمام‌روز");
        FA.put("start_time", "شروع");
        FA.put("end_time", "پایان");
        FA.put("note", "یادداشت");
        FA.put("calendar_target", "تقویم مقصد");
        FA.put("save", "ذخیره");
        FA.put("cancel", "انصراف");
        FA.put("delete", "حذف");
        FA.put("edit", "ویرایش");
        FA.put("del_confirm_title", "حذف رویداد");
        FA.put("del_confirm_text", "«%s» حذف شود؟");
        FA.put("deleted", "رویداد حذف شد");
        FA.put("saved", "رویداد ذخیره شد");
        FA.put("no_writable_cal", "تقویم قابل‌نوشتن پیدا نشد؛ اول اکانت گوگل را در اپ تقویم گوشی فعال کنید.");
        FA.put("recurring_note", "این رویداد تکرارشونده است؛ تغییرات روی همه تکرارها اعمال می‌شود.");
        FA.put("theme", "تم");
        FA.put("theme_auto", "خودکار (سیستم)");
        FA.put("theme_light", "روشن");
        FA.put("theme_dark", "تاریک");
        FA.put("language", "زبان");
        FA.put("persian", "فارسی");
        FA.put("english", "English");
        FA.put("hijri_adjust", "تصحیح تاریخ قمری");
        FA.put("hijri_note", "مقدار پیشنهادی برای ایران: -۱");
        FA.put("about", "درباره و تماس با ما");
        FA.put("ok", "باشه");
        FA.put("today_occasion", "امروز: %s");
        FA.put("holiday", "تعطیل رسمی");
        FA.put("occasion", "مناسبت");
        FA.put("next_event", "رویداد بعدی: %s");
        FA.put("all_day_label", "تمام‌روز");
        FA.put("developer", "توسعه‌دهنده");
        FA.put("contact", "تماس با ما");
        FA.put("websites", "وب‌سایت‌ها");
        FA.put("phone", "تلفن");
        FA.put("instagram", "اینستاگرام");
        FA.put("desc_line", "تقویم ایرانی با همگام‌سازی لحظه‌ای گوگل کالندر — دقیق و سریع");
        FA.put("version", "نسخه");
        FA.put("time_range", "%s تا %s");
        FA.put("sync_active", "همگام‌سازی خودکار فعال است");
        FA.put("date_label", "تاریخ");
        FA.put("empty_title_required", "عنوان را وارد کنید");
        FA.put("invalid_time", "ساعت پایان باید بعد از ساعت شروع باشد");
        FA.put("tomorrow", "فردا");
        FA.put("no_holiday_soon", "در ۹۹ روز آینده تعطیلی رسمی نیست");
        FA.put("subtitle", "تقویم جلالی • همگام با گوگل کالندر");
        FA.put("next_holiday", "تعطیلی رسمی بعدی");
        FA.put("days", "روز");
        FA.put("security", "امنیت");
        FA.put("secure_screen", "جلوگیری از اسکرین‌شات");
        FA.put("secure_screen_note", "محتوای برنامه در اسکرین‌شات، ضبط صفحه و پیش‌نمایش چندوظیفه‌ای محافظت می‌شود.");
        FA.put("privacy_title", "حریم خصوصی و امنیت");
        FA.put("privacy_text", "تقویم کد هیچ دسترسی اینترنتی ندارد؛ رویدادها و تنظیمات شما هرگز از گوشی خارج نمی‌شوند. تنظیمات با رمزنگاری سخت‌افزاری AES-256-GCM و کلید Android Keystore ذخیره می‌شود و پشتیبان‌گیری ابری برای این برنامه غیرفعال است.");
        FA.put("play_protect_title", "پیام Google Play Protect");
        FA.put("play_protect_text", "اگر هنگام نصب پیام «App blocked to protect your device» یا هشدار Play Protect دیدید، نگران نباشید: گوگل فقط منبع نصب را نمی‌شناسد و این پیام برای همه برنامه‌های نصب‌شده خارج از فروشگاه گوگل نمایش داده می‌شود و به معنی خطرناک بودن برنامه نیست.\n\nتقویم کد مخرب ندارد، دسترسی اینترنت ندارد و فقط به تقویم گوشی شما دسترسی دارد.\n\nبرای ادامه نصب: در پیام هشدار روی «جزئیات بیشتر (More details)» یا علامت ⓘ ضربه بزنید و سپس گزینه «نصب در هر صورت (Install anyway)» را انتخاب کنید.");
        FA.put("understood", "متوجه شدم");
        FA.put("perm_rationale", "تقویم کد برای نمایش و ذخیره رویدادهای تقویم گوگل، به دسترسی تقویم نیاز دارد. رویدادها فقط روی گوشی خودتان خوانده و نوشته می‌شوند.");
        FA.put("save_failed", "ذخیره نشد؛ دوباره تلاش کنید");
        FA.put("slogan", "دقیق • امن • زیبا");
        FA.put("today_holiday", "امروز تعطیل رسمی است");
        // v1.8.0 — share day card
        FA.put("share_card", "اشتراک کارت روز");
        FA.put("share_card_saved", "کارت روز در گالری ذخیره شد");
        FA.put("share_card_failed", "ساخت کارت روز ناموفق بود");
        // v1.8.0 — onboarding
        FA.put("ob_skip", "رد کردن");
        FA.put("ob_next", "بعدی");
        FA.put("ob_start", "شروع");
        FA.put("ob1_title", "تقویم جلالی و قمری");
        FA.put("ob1_text", "ماه شمسی با تاریخ قمری و میلادی، مناسبت‌ها و تعطیلات رسمی و رویدادهای گوگل کالندر — همه در یک نگاه.");
        FA.put("ob2_title", "اوقات شرعی و قبله‌نما");
        FA.put("ob2_text", "اوقات شرعی آفلاین برای بیش از ۱۳۰ شهر ایران، قبله‌نمای زنده و یادآور با صدای دلخواه.");
        FA.put("ob3_title", "برنامه‌ریز روزانه");
        FA.put("ob3_text", "کارهای هر روز را بنویس، انجام‌شده‌ها را علامت بزن و پشتیبان بگیر — کاملاً آفلاین.");
        // v1.8.0 — palettes & tones & backup
        FA.put("palette", "پالت رنگ");
        FA.put("pal_default", "فیروزه‌ای");
        FA.put("pal_midnight", "شب نیمه");
        FA.put("pal_saffron", "زعفران");
        FA.put("pal_emerald", "زمرد");
        FA.put("pal_rose", "گل محمدی");
        FA.put("reminder_tone", "صدای یادآور اذان");
        FA.put("tone_default", "پیش‌فرض سیستم");
        FA.put("tone_1", "آرپژ بلورین");
        FA.put("tone_2", "زنگ عمیق");
        FA.put("tone_3", "ماریمبا آرام");
        FA.put("tone_4", "ندای دوتایی");
                FA.put("tone_5", "موتف نوازنده");
        FA.put("tone_6", "زنگ کریستالی");
        FA.put("tone_7", "نان پنج‌نگامه");
        FA.put("tone_8", "زنگ عمیق معبد");
        FA.put("tone_9", "طرپ سحری");
        FA.put("tone_10", "ترانهٔ آرام");
        FA.put("share_wall_title", "پس‌زمینهٔ کارت روز");
        FA.put("wall_default", "گرادیان تم (پیش‌فرض)");
        FA.put("wall_1", "گل سرخ");
        FA.put("wall_2", "شب نیمه");
        FA.put("wall_3", "زمرد");
        FA.put("wall_4", "زعفران");
        FA.put("wall_5", "فیروزه");
        FA.put("wall_6", "شهریار");
        FA.put("wall_7", "لاجورد");
        FA.put("wall_8", "طلوع کوه");
        FA.put("wall_9", "نگارینه");
        FA.put("wall_10", "مرجان");
        FA.put("wall_11", "کهکشان");
        FA.put("wall_12", "هندسهٔ زرین");
        FA.put("backup_export", "پشتیبان‌گیری برنامه‌ریز");
        FA.put("backup_import", "بازیابی پشتیبان");
        FA.put("backup_caption", "پشتیبان‌گیری همهٔ کارها را در یک فایل JSON ذخیره می‌کند؛ بازیابی، فایل پشتیبان را با کارهای فعلی ادغام می‌کند.");
        FA.put("backup_done", "پشتیبان ذخیره شد");
        FA.put("backup_imported", "%s کار بازیابی شد");
        FA.put("backup_invalid", "فایل پشتیبان معتبر نیست");
        FA.put("backup_failed", "عملیات پشتیبان‌گیری ناموفق بود");
        // ---- en ----
        EN.put("app_name", "Taqvim");
        EN.put("today", "Today");
        EN.put("settings", "Settings");
        EN.put("previous_month", "Previous month");
        EN.put("next_month", "Next month");
        EN.put("events", "Events");
        EN.put("events_count", "Events (%s)");
        EN.put("no_events", "No events for this day");
        EN.put("no_events_hint", "Tap the + button to add one");
        EN.put("perm_title", "Calendar permission");
        EN.put("perm_text", "Calendar access is required to show and save your Google Calendar events.");
        EN.put("grant", "Grant access");
        EN.put("open_settings", "Open settings");
        EN.put("add_event", "New event");
        EN.put("edit_event", "Edit event");
        EN.put("title", "Title");
        EN.put("hint_title", "e.g. Team meeting");
        EN.put("all_day", "All day");
        EN.put("start_time", "Start");
        EN.put("end_time", "End");
        EN.put("note", "Notes");
        EN.put("calendar_target", "Target calendar");
        EN.put("save", "Save");
        EN.put("cancel", "Cancel");
        EN.put("delete", "Delete");
        EN.put("edit", "Edit");
        EN.put("del_confirm_title", "Delete event");
        EN.put("del_confirm_text", "Delete \"%s\"?");
        EN.put("deleted", "Event deleted");
        EN.put("saved", "Event saved");
        EN.put("no_writable_cal", "No writable calendar found. Add your Google account in the device Calendar app first.");
        EN.put("recurring_note", "This is a recurring event; changes apply to all occurrences.");
        EN.put("theme", "Theme");
        EN.put("theme_auto", "Auto (system)");
        EN.put("theme_light", "Light");
        EN.put("theme_dark", "Dark");
        EN.put("language", "Language");
        EN.put("persian", "فارسی");
        EN.put("english", "English");
        EN.put("hijri_adjust", "Hijri date correction");
        EN.put("hijri_note", "Suggested value for Iran: -1");
        EN.put("about", "About & Contact");
        EN.put("ok", "OK");
        EN.put("today_occasion", "Today: %s");
        EN.put("holiday", "Official holiday");
        EN.put("occasion", "Occasion");
        EN.put("next_event", "Next: %s");
        EN.put("all_day_label", "All day");
        EN.put("developer", "Developer");
        EN.put("contact", "Contact us");
        EN.put("websites", "Websites");
        EN.put("phone", "Phone");
        EN.put("instagram", "Instagram");
        EN.put("desc_line", "Iranian calendar with real-time Google Calendar sync — accurate & fast");
        EN.put("version", "Version");
        EN.put("time_range", "%s – %s");
        EN.put("sync_active", "Auto-sync active");
        EN.put("date_label", "Date");
        EN.put("empty_title_required", "Please enter a title");
        EN.put("invalid_time", "End time must be after start time");
        EN.put("tomorrow", "Tomorrow");
        EN.put("no_holiday_soon", "No official holiday in the next 99 days");
        EN.put("subtitle", "Jalali calendar • live Google Calendar sync");
        EN.put("next_holiday", "Next official holiday");
        EN.put("days", "days");
        EN.put("security", "Security");
        EN.put("secure_screen", "Block screenshots");
        EN.put("secure_screen_note", "Protects the app content in screenshots, screen recording and the recents preview.");
        EN.put("privacy_title", "Privacy & Security");
        EN.put("privacy_text", "Taqvim Kod has NO internet permission — your events and settings never leave the device. Settings are stored with hardware-backed AES-256-GCM encryption using the Android Keystore, and cloud backup for this app is disabled.");
        EN.put("play_protect_title", "Google Play Protect message");
        EN.put("play_protect_text", "If you see \"App blocked to protect your device\" or any Play Protect warning while installing, don't worry: Google only doesn't recognize the install source. This warning appears for every app installed outside Google Play and does not mean the app is dangerous.\n\nTaqvim contains no malicious code, has no internet access and only touches your device calendar.\n\nTo continue installing: tap \"More details\" or the ⓘ icon on the warning, then choose \"Install anyway\".");
        EN.put("understood", "Got it");
        EN.put("perm_rationale", "Taqvim Kod needs calendar access to show and save your Google Calendar events. Events are only read from and written to your own device calendar.");
        EN.put("save_failed", "Couldn't save; please try again");
        EN.put("slogan", "Accurate • Secure • Beautiful");
        EN.put("today_holiday", "Today is an official holiday");
        // v1.8.0 — share day card
        EN.put("share_card", "Share day card");
        EN.put("share_card_saved", "Day card saved to your gallery");
        EN.put("share_card_failed", "Couldn't build the day card");
        // v1.8.0 — onboarding
        EN.put("ob_skip", "Skip");
        EN.put("ob_next", "Next");
        EN.put("ob_start", "Get started");
        EN.put("ob1_title", "Jalali & Hijri calendar");
        EN.put("ob1_text", "A Persian month view with Hijri and Gregorian dates, official holidays and your Google Calendar events — all at a glance.");
        EN.put("ob2_title", "Prayer times & Qibla");
        EN.put("ob2_text", "Offline prayer times for 130+ Iranian cities, a live qibla compass and reminders with the chime you choose.");
        EN.put("ob3_title", "Daily planner");
        EN.put("ob3_text", "Write down each day's tasks, tick them off and keep a JSON backup — fully offline.");
        // v1.8.0 — palettes & tones & backup
        EN.put("palette", "Color palette");
        EN.put("pal_default", "Turquoise");
        EN.put("pal_midnight", "Midnight");
        EN.put("pal_saffron", "Saffron");
        EN.put("pal_emerald", "Emerald");
        EN.put("pal_rose", "Rose");
        EN.put("reminder_tone", "Azan reminder sound");
        EN.put("tone_default", "System default");
        EN.put("tone_1", "Crystal arpeggio");
        EN.put("tone_2", "Deep bell");
        EN.put("tone_3", "Soft marimba");
        EN.put("tone_4", "Two-note call");
                EN.put("tone_5", "Rising motif");
        EN.put("tone_6", "Crystal bells");
        EN.put("tone_7", "Pentatonic chime");
        EN.put("tone_8", "Deep temple bell");
        EN.put("tone_9", "Dawn trill");
        EN.put("tone_10", "Gentle melody");
        EN.put("share_wall_title", "Day card background");
        EN.put("wall_default", "Theme gradient (default)");
        EN.put("wall_1", "Rose");
        EN.put("wall_2", "Midnight");
        EN.put("wall_3", "Emerald");
        EN.put("wall_4", "Saffron");
        EN.put("wall_5", "Turquoise");
        EN.put("wall_6", "Shahryar");
        EN.put("wall_7", "Lapis");
        EN.put("wall_8", "Mountain Dawn");
        EN.put("wall_9", "Parchment");
        EN.put("wall_10", "Coral");
        EN.put("wall_11", "Galaxy");
        EN.put("wall_12", "Golden Geometry");
        EN.put("backup_export", "Back up planner");
        EN.put("backup_import", "Restore backup");
        EN.put("backup_caption", "Backup saves every day's tasks to a JSON file; restore merges the file with your current tasks.");
        EN.put("backup_done", "Backup saved");
        EN.put("backup_imported", "%s tasks restored");
        EN.put("backup_invalid", "Invalid backup file");
        EN.put("backup_failed", "Backup operation failed");

        // ---- Persian Jalali month names ----
        FA_JM = new String[]{"فروردین","اردیبهشت","خرداد","تیر","مرداد","شهریور","مهر","آبان","آذر","دی","بهمن","اسفند"};
        EN_JM = new String[]{"Farvardin","Ordibehesht","Khordad","Tir","Mordad","Shahrivar","Mehr","Aban","Azar","Dey","Bahman","Esfand"};
        FA_JD = new String[]{"شنبه","یکشنبه","دوشنبه","سه‌شنبه","چهارشنبه","پنجشنبه","جمعه"};
        EN_JD = new String[]{"Saturday","Sunday","Monday","Tuesday","Wednesday","Thursday","Friday"};
        FA_JDS = new String[]{"ش","ی","د","س","چ","پ","ج"};
        EN_JDS = new String[]{"Sat","Sun","Mon","Tue","Wed","Thu","Fri"};
        FA_GM = new String[]{"ژانویه","فوریه","مارس","آوریل","مه","ژوئن","ژوئیه","اوت","سپتامبر","اکتبر","نوامبر","دسامبر"};
        EN_GM = new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};
        FA_HM = new String[]{"محرم","صفر","ربیع‌الاول","ربیع‌الثانی","جمادی‌الاول","جمادی‌الثانی","رجب","شعبان","رمضان","شوال","ذی‌القعده","ذی‌الحجه"};
        EN_HM = new String[]{"Muharram","Safar","Rabi al-Awwal","Rabi al-Thani","Jumada al-Ula","Jumada al-Thani","Rajab","Shaban","Ramadan","Shawwal","Dhul-Qadah","Dhul-Hijjah"};
    }

    public static String[] FA_JM, EN_JM, FA_JD, EN_JD, FA_JDS, EN_JDS, FA_GM, EN_GM, FA_HM, EN_HM;

    public static boolean fa(Context c) { return !"en".equals(Prefs.lang(c)); }

    public static String t(Context c, String key) {
        String v = fa(c) ? FA.get(key) : EN.get(key);
        return v != null ? v : key;
    }

    public static String[] months(Context c) { return fa(c) ? FA_JM : EN_JM; }
    public static String[] weekdays(Context c) { return fa(c) ? FA_JD : EN_JD; }
    public static String[] weekdaysShort(Context c) { return fa(c) ? FA_JDS : EN_JDS; }
    public static String[] gregMonths(Context c) { return fa(c) ? FA_GM : EN_GM; }
    public static String[] hijriMonths(Context c) { return fa(c) ? FA_HM : EN_HM; }

    private static final char[] PD = {'۰','۱','۲','۳','۴','۵','۶','۷','۸','۹'};

    /** convert latin digits to Persian digits when lang is fa */
    public static String d(Context c, String s) {
        if (!fa(c)) return s;
        StringBuilder sb = new StringBuilder(s.length());
        for (char ch : s.toCharArray()) {
            sb.append(ch >= '0' && ch <= '9' ? PD[ch - '0'] : ch);
        }
        return sb.toString();
    }

    public static String d(Context c, int n) { return d(c, String.valueOf(n)); }

    /** two-digit zero padded */
    public static String d2(Context c, int n) {
        return d(c, String.format(java.util.Locale.US, "%02d", n));
    }

    /** "شنبه ۱۲ مهر ۱۴۰۳" */
    public static String fmtJDate(Context c, Dates.JDate j) {
        int wd = Dates.weekdayFromJdn(Dates.jdnOfJ(j.jy, j.jm, j.jd));
        return d(c, weekdays(c)[wd] + " " + j.jd + " " + months(c)[j.jm - 1] + " " + j.jy);
    }

    /** "۱۹ اکتبر ۲۰۲۵" */
    public static String fmtGDate(Context c, Dates.GDate g) {
        return d(c, g.gd + " " + gregMonths(c)[g.gm - 1] + " " + g.gy);
    }

    /** "۲۶ ربیع‌الثانی ۱۴۴۷" */
    public static String fmtHDate(Context c, Dates.HDate h) {
        return d(c, h.hd + " " + hijriMonths(c)[h.hm - 1] + " " + h.hy);
    }

    /** "۱۵:۰۰" */
    public static String fmtTime(Context c, long millis) {
        int[] hm = Dates.hourMin(millis);
        return d2(c, hm[0]) + ":" + d2(c, hm[1]);
    }

    /** month-year: "مهر ۱۴۰۳" */
    public static String fmtMonthYear(Context c, int jy, int jm) {
        return d(c, months(c)[jm - 1] + " " + jy);
    }
}
