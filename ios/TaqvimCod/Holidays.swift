import Foundation

/// Iranian official holidays + occasions (Jalali fixed + Hijri lunar dates).
/// Port of the Android Holidays database — identical content.
enum Holidays {

    struct Occasion {
        let fa: String
        let en: String
        let holiday: Bool
    }

    private static let jal: [String: Occasion] = [
        // ===== Jalali fixed — official holidays =====
        "1/1":  Occasion(fa: "عید نوروز", en: "Nowruz (Persian New Year)", holiday: true),
        "1/2":  Occasion(fa: "عید نوروز", en: "Nowruz (Persian New Year)", holiday: true),
        "1/3":  Occasion(fa: "عید نوروز", en: "Nowruz (Persian New Year)", holiday: true),
        "1/4":  Occasion(fa: "عید نوروز", en: "Nowruz (Persian New Year)", holiday: true),
        "1/12": Occasion(fa: "روز جمهوری اسلامی", en: "Islamic Republic Day", holiday: true),
        "1/13": Occasion(fa: "سیزده‌بدر", en: "Sizdah Bedar (Nature Day)", holiday: true),
        "3/14": Occasion(fa: "رحلت امام خمینی", en: "Demise of Imam Khomeini", holiday: true),
        "3/15": Occasion(fa: "قیام ۱۵ خرداد", en: "Khordad 15 Uprising", holiday: true),
        "11/22": Occasion(fa: "پیروزی انقلاب اسلامی", en: "Islamic Revolution Victory", holiday: true),
        "12/29": Occasion(fa: "ملی شدن صنعت نفت", en: "Oil Industry Nationalization Day", holiday: true),

        // ===== Jalali fixed — occasions =====
        "2/1":  Occasion(fa: "روز بزرگداشت سعدی", en: "Saadi Day", holiday: false),
        "2/12": Occasion(fa: "روز معلم", en: "Teachers' Day", holiday: false),
        "2/25": Occasion(fa: "روز بزرگداشت فردوسی و زبان فارسی", en: "Ferdowsi & Persian Language Day", holiday: false),
        "3/3":  Occasion(fa: "آزادسازی خرمشهر", en: "Liberation of Khorramshahr", holiday: false),
        "6/7":  Occasion(fa: "روز آتش‌نشانی و خدمات ایمنی", en: "Firefighters & Safety Day", holiday: false),
        "6/20": Occasion(fa: "روز بزرگداشت حافظ", en: "Hafez Day", holiday: false),
        "7/7":  Occasion(fa: "روز پزشک", en: "Doctors' Day", holiday: false),
        "8/13": Occasion(fa: "روز دانش‌آموز و مبارزه با استکبار جهانی", en: "Student Day & Anti-Arrogance Day", holiday: false),
        "8/24": Occasion(fa: "روز کتاب، کتاب‌خوانی و ناشر", en: "Book & Reading Day", holiday: false),
        "9/16": Occasion(fa: "روز دانشجو", en: "University Student Day", holiday: false),
        "9/30": Occasion(fa: "شب یلدا (چله)", en: "Yalda Night (Longest Night)", holiday: false),
        "10/9": Occasion(fa: "روز بصیرت و میثاق امت با ولایت", en: "Dey 9 Day (Covenant Day)", holiday: false),
        "11/12": Occasion(fa: "ورود امام خمینی به ایران", en: "Imam Khomeini's Return to Iran", holiday: false),
    ]

    private static let lun: [String: Occasion] = [
        // ===== Lunar — official holidays =====
        "1/9":  Occasion(fa: "تاسوعای حسینی", en: "Tasua of Husayn", holiday: true),
        "1/10": Occasion(fa: "عاشورای حسینی", en: "Ashura of Husayn", holiday: true),
        "2/20": Occasion(fa: "اربعین حسینی", en: "Arbaeen of Husayn", holiday: true),
        "2/28": Occasion(fa: "رحلت پیامبر اکرم و شهادت امام حسن مجتبی", en: "Demise of Prophet & Martyrdom of Imam Hassan", holiday: true),
        "3/17": Occasion(fa: "میلاد پیامبر اکرم و امام جعفر صادق", en: "Birth of Prophet Muhammad & Imam Sadeq", holiday: true),
        "6/3":  Occasion(fa: "شهادت حضرت فاطمه زهرا و روز زن", en: "Martyrdom of Hazrat Fatemeh & Women's Day", holiday: true),
        "7/13": Occasion(fa: "ولادت امام علی و روز پدر", en: "Birth of Imam Ali & Father's Day", holiday: true),
        "7/27": Occasion(fa: "عید سعید مبعث", en: "Eid al-Mab'ath", holiday: true),
        "8/15": Occasion(fa: "ولادت امام زمان و جشن نیمه شعبان", en: "Birth of Imam Mahdi (Mid-Sha'ban)", holiday: true),
        "9/21": Occasion(fa: "شهادت امام علی و شب قدر", en: "Martyrdom of Imam Ali & Laylat al-Qadr", holiday: true),
        "10/1": Occasion(fa: "عید سعید فطر", en: "Eid al-Fitr", holiday: true),
        "10/2": Occasion(fa: "تعطیل به مناسبت عید سعید فطر", en: "Eid al-Fitr Holiday", holiday: true),
        "12/10": Occasion(fa: "عید سعید قربان", en: "Eid al-Adha", holiday: true),
        "12/18": Occasion(fa: "عید سعید غدیر خم", en: "Eid al-Ghadir", holiday: true),

        // ===== Lunar — occasions =====
        "3/8":  Occasion(fa: "شهادت امام حسن عسکری", en: "Martyrdom of Imam Hassan Askari", holiday: false),
        "9/1":  Occasion(fa: "آغاز ماه مبارک رمضان", en: "Beginning of Ramadan", holiday: false),
        "9/19": Occasion(fa: "شب قدر", en: "Laylat al-Qadr", holiday: false),
        "9/23": Occasion(fa: "شب قدر", en: "Laylat al-Qadr", holiday: false),
    ]

    private static let endOfSafar = Occasion(
        fa: "شهادت امام رضا (آخر صفر)", en: "Martyrdom of Imam Reza (End of Safar)", holiday: true)

    /// events for one Jalali/Hijri day (holidays first)
    static func forDay(jm: Int, jd: Int, hm: Int, hd: Int) -> [Occasion] {
        var list: [Occasion] = []
        if let e = jal["\(jm)/\(jd)"] { list.append(e) }
        if hm == 2 && hd == DateEngine.hijriMonthLength(2) { list.append(endOfSafar) }
        if let e = lun["\(hm)/\(hd)"] { list.append(e) }
        return list.sorted { $0.holiday && !$1.holiday }
    }

    static func isHoliday(jm: Int, jd: Int, hm: Int, hd: Int) -> Bool {
        if let e = jal["\(jm)/\(jd)"], e.holiday { return true }
        if hm == 2 && hd == DateEngine.hijriMonthLength(2) { return true }
        return lun["\(hm)/\(hd)"]?.holiday ?? false
    }

    /// next official holiday within maxDays → (daysUntil, occasion) or nil
    static func nextHoliday(from j: JDate, hijriAdj: Int, maxDays: Int) -> (Int, Occasion)? {
        let start = DateEngine.j2d(jy: j.y, jm: j.m, jd: j.d)
        for i in 0..<maxDays {
            let jj = DateEngine.d2j(start + i)
            let hh = DateEngine.hijriFromJdn(start + i, adjust: hijriAdj)
            if isHoliday(jm: jj.m, jd: jj.d, hm: hh.m, hd: hh.d) {
                return (i, forDay(jm: jj.m, jd: jj.d, hm: hh.m, hd: hh.d)[0])
            }
        }
        return nil
    }
}
