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
        FA.put("app_name", "تقویم کد");
        FA.put("today", "امروز");
        FA.put("settings", "تنظیمات");
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
        // ---- en ----
        EN.put("app_name", "Taqvim Kod");
        EN.put("today", "Today");
        EN.put("settings", "Settings");
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
