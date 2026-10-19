import Foundation
import SwiftUI

// MARK: - bilingual strings (fa default, en optional)

enum L {
    static let fa: [String: String] = [
        "app_name": "تقویم کد",
        "subtitle": "تقویم جلالی • همگام با تقویم گوگل",
        "slogan": "دقیق • امن • زیبا",
        "today": "امروز",
        "settings": "تنظیمات",
        "events": "رویدادها",
        "events_count": "رویدادها (%@)",
        "no_events": "رویدادی برای این روز ثبت نشده است",
        "no_events_hint": "با دکمه + رویداد جدید اضافه کنید",
        "perm_title": "اجازه دسترسی به تقویم",
        "perm_text": "برای نمایش و ثبت رویدادهای تقویم، دسترسی تقویم لازم است. رویدادها فقط روی گوشی خودتان خوانده و نوشته می‌شوند.",
        "grant": "اجازه می‌دهم",
        "open_settings": "رفتن به تنظیمات",
        "add_event": "رویداد جدید",
        "edit_event": "ویرایش رویداد",
        "title": "عنوان",
        "hint_title": "مثلاً جلسه کاری",
        "all_day": "تمام‌روز",
        "start_time": "شروع",
        "end_time": "پایان",
        "note": "یادداشت",
        "calendar_target": "تقویم مقصد",
        "save": "ذخیره",
        "cancel": "انصراف",
        "delete": "حذف",
        "edit": "ویرایش",
        "del_confirm_title": "حذف رویداد",
        "del_confirm_text": "«%@» حذف شود؟",
        "deleted": "رویداد حذف شد",
        "saved": "رویداد ذخیره شد",
        "no_writable_cal": "تقویم قابل‌نوشتن پیدا نشد؛ اول اکانت گوگل را در اپ تقویم گوشی اضافه کنید.",
        "theme": "تم",
        "theme_auto": "خودکار (سیستم)",
        "theme_light": "روشن",
        "theme_dark": "تاریک",
        "language": "زبان",
        "persian": "فارسی",
        "english": "English",
        "hijri_adjust": "تصحیح تاریخ قمری",
        "hijri_note": "مقدار پیشنهادی برای ایران: -۱",
        "about": "درباره و تماس با ما",
        "ok": "باشه",
        "holiday": "تعطیل رسمی",
        "next_holiday": "تعطیلی رسمی بعدی",
        "days": "روز",
        "today_holiday": "امروز تعطیل رسمی است",
        "no_holiday_soon": "در ۹۹ روز آینده تعطیلی رسمی نیست",
        "developer": "توسعه‌دهنده",
        "contact": "تماس با ما",
        "websites": "وب‌سایت‌ها",
        "phone": "تلفن",
        "instagram": "اینستاگرام",
        "desc_line": "تقویم ایرانی با همگام‌سازی لحظه‌ای گوگل کالندر — دقیق و سریع",
        "version": "نسخه",
        "time_range": "%s تا %s",
        "date_label": "تاریخ",
        "empty_title_required": "عنوان را وارد کنید",
        "invalid_time": "ساعت پایان باید بعد از ساعت شروع باشد",
        "tomorrow": "فردا",
        "privacy_title": "حریم خصوصی و امنیت",
        "privacy_text": "تقویم کد هیچ داده‌ای را به سرور نمی‌فرستد؛ رویدادها فقط روی دستگاه شما ذخیره و با تقویم گوگل شما همگام می‌شوند. تنظیمات روی همین دستگاه باقی می‌ماند.",
        "security": "امنیت",
        "empty_title": "(بدون عنوان)",
    ]

    static let en: [String: String] = [
        "app_name": "Taqvim Kod",
        "subtitle": "Jalali calendar • live calendar sync",
        "slogan": "Accurate • Secure • Beautiful",
        "today": "Today",
        "settings": "Settings",
        "events": "Events",
        "events_count": "Events (%@)",
        "no_events": "No events for this day",
        "no_events_hint": "Tap the + button to add one",
        "perm_title": "Calendar permission",
        "perm_text": "Calendar access is required to show and save your calendar events. Events are only read from and written to your own device calendar.",
        "grant": "Grant access",
        "open_settings": "Open settings",
        "add_event": "New event",
        "edit_event": "Edit event",
        "title": "Title",
        "hint_title": "e.g. Team meeting",
        "all_day": "All day",
        "start_time": "Start",
        "end_time": "End",
        "note": "Notes",
        "calendar_target": "Target calendar",
        "save": "Save",
        "cancel": "Cancel",
        "delete": "Delete",
        "edit": "Edit",
        "del_confirm_title": "Delete event",
        "del_confirm_text": "Delete \"%@\"?",
        "deleted": "Event deleted",
        "saved": "Event saved",
        "no_writable_cal": "No writable calendar found. Add your Google account in the device Calendar app first.",
        "theme": "Theme",
        "theme_auto": "Auto (system)",
        "theme_light": "Light",
        "theme_dark": "Dark",
        "language": "Language",
        "persian": "فارسی",
        "english": "English",
        "hijri_adjust": "Hijri date correction",
        "hijri_note": "Suggested value for Iran: -1",
        "about": "About & Contact",
        "ok": "OK",
        "holiday": "Official holiday",
        "next_holiday": "Next official holiday",
        "days": "days",
        "today_holiday": "Today is an official holiday",
        "no_holiday_soon": "No official holiday in the next 99 days",
        "developer": "Developer",
        "contact": "Contact us",
        "websites": "Websites",
        "phone": "Phone",
        "instagram": "Instagram",
        "desc_line": "Iranian calendar with real-time Google Calendar sync — accurate & fast",
        "version": "Version",
        "time_range": "%s – %s",
        "date_label": "Date",
        "empty_title_required": "Please enter a title",
        "invalid_time": "End time must be after start time",
        "tomorrow": "Tomorrow",
        "privacy_title": "Privacy & Security",
        "privacy_text": "Taqvim Kod never sends data to any server; events live only on your device and sync with YOUR Google calendar. Settings stay on this device.",
        "security": "Security",
        "empty_title": "(No title)",
    ]

    static func t(_ key: String) -> String {
        let lang = UserDefaults.standard.string(forKey: "lang") ?? "fa"
        return (lang == "en" ? en[key] : fa[key]) ?? key
    }

    static var isFa: Bool { (UserDefaults.standard.string(forKey: "lang") ?? "fa") != "en" }

    static let jalaliMonths = ["فروردین","اردیبهشت","خرداد","تیر","مرداد","شهریور",
                               "مهر","آبان","آذر","دی","بهمن","اسفند"]
    static let jalaliMonthsEn = ["Farvardin","Ordibehesht","Khordad","Tir","Mordad","Shahrivar",
                                 "Mehr","Aban","Azar","Dey","Bahman","Esfand"]
    static let weekdays = ["شنبه","یکشنبه","دوشنبه","سه‌شنبه","چهارشنبه","پنجشنبه","جمعه"]
    static let weekdaysEn = ["Saturday","Sunday","Monday","Tuesday","Wednesday","Thursday","Friday"]
    static let weekdaysShort = ["ش","ی","د","س","چ","پ","ج"]
    static let weekdaysShortEn = ["Sa","Su","Mo","Tu","We","Th","Fr"]
    static let gregMonths = ["ژانویه","فوریه","مارس","آوریل","مه","ژوئن",
                             "ژوئیه","اوت","سپتامبر","اکتبر","نوامبر","دسامبر"]
    static let gregMonthsEn = ["January","February","March","April","May","June",
                               "July","August","September","October","November","December"]
    static let hijriMonths = ["محرم","صفر","ربیع‌الاول","ربیع‌الثانی","جمادی‌الاول","جمادی‌الثانی",
                              "رجب","شعبان","رمضان","شوال","ذی‌القعده","ذی‌الحجه"]
    static let hijriMonthsEn = ["Muharram","Safar","Rabi al-Awwal","Rabi al-Thani","Jumada al-Ula",
                                "Jumada al-Thani","Rajab","Shaban","Ramadan","Shawwal","Dhul-Qadah","Dhul-Hijjah"]

    static var jm: [String] { isFa ? jalaliMonths : jalaliMonthsEn }
    static var wd: [String] { isFa ? weekdays : weekdaysEn }
    static var wds: [String] { isFa ? weekdaysShort : weekdaysShortEn }
    static var gm: [String] { isFa ? gregMonths : gregMonthsEn }
    static var hm: [String] { isFa ? hijriMonths : hijriMonthsEn }

    private static let faDigits = ["۰","۱","۲","۳","۴","۵","۶","۷","۸","۹"]

    /// latin → persian digits when fa
    static func d(_ s: String) -> String {
        guard isFa else { return s }
        return String(s.map { ch in
            guard let v = ch.wholeNumberValue, (0...9).contains(v) else { return ch }
            return Array(faDigits[v])[0]
        })
    }

    static func d(_ n: Int) -> String { d(String(n)) }

    static func d2(_ n: Int) -> String { d(String(format: "%02d", n)) }

    /// "شنبه ۱۲ مهر ۱۴۰۳"
    static func fmtJDate(_ j: JDate) -> String {
        let wdIdx = DateEngine.weekday(fromJdn: DateEngine.j2d(jy: j.y, jm: j.m, jd: j.d))
        return d("\(wd[wdIdx]) \(j.d) \(jm[j.m - 1]) \(j.y)")
    }

    /// "۱۹ اکتبر ۲۰۲۵"
    static func fmtGDate(_ g: GDate) -> String { d("\(g.d) \(gm[g.m - 1]) \(g.y)") }

    /// "۲۶ ربیع‌الثانی ۱۴۴۷"
    static func fmtHDate(_ h: HDate) -> String { d("\(h.d) \(hm[h.m - 1]) \(h.y)") }

    static func fmtTime(_ date: Date) -> String {
        let (h, m) = DateEngine.hourMinute(of: date)
        return d2(h) + ":" + d2(m)
    }

    static func fmtMonthYear(_ jy: Int, _ jm: Int) -> String { d("\(Self.jm[jm - 1]) \(jy)") }
}

// MARK: - palette

enum Pal {
    static func colors(_ scheme: ColorScheme) -> (
        bg: Color, surface: Color, surfaceAlt: Color, text: Color, textSub: Color,
        accent: Color, accentSoft: Color, gradStart: Color, gradEnd: Color,
        red: Color, redSoft: Color, amber: Color, green: Color
    ) {
        scheme == .dark
        ? (Color(hex: 0x0D1218), Color(hex: 0x161D26), Color(hex: 0x202A35),
           Color(hex: 0xE9EEF3), Color(hex: 0x93A1AE),
           Color(hex: 0x3BC9BE), Color(hex: 0x173B39),
           Color(hex: 0x0E3A46), Color(hex: 0x0F6E68),
           Color(hex: 0xE5776F), Color(hex: 0x322020),
           Color(hex: 0xE0B457), Color(hex: 0x55C07E))
        : (Color(hex: 0xF6F4EF), Color(hex: 0xFFFFFF), Color(hex: 0xF0EDE6),
           Color(hex: 0x242E39), Color(hex: 0x7D8791),
           Color(hex: 0x0C8A82), Color(hex: 0xDFF1EF),
           Color(hex: 0x0B4F5E), Color(hex: 0x0E9488),
           Color(hex: 0xC2483F), Color(hex: 0xFAEAE8),
           Color(hex: 0xD9A441), Color(hex: 0x3D8B5F))
    }
}

extension Color {
    init(hex: UInt32, alpha: Double = 1) {
        self.init(.sRGB,
                  Double((hex >> 16) & 0xFF) / 255,
                  Double((hex >> 8) & 0xFF) / 255,
                  Double(hex & 0xFF) / 255,
                  opacity: alpha)
    }
}
