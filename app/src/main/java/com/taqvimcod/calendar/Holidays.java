package com.taqvimcod.calendar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Iranian official holidays + occasions (Jalali fixed dates + Hijri lunar dates).
 * Official-holiday list follows Iran's official civil calendar.
 */
public final class Holidays {

    private Holidays() {}

    public static class Ev {
        public String fa, en;
        public boolean holiday;
        public Ev(String fa, String en, boolean holiday) {
            this.fa = fa; this.en = en; this.holiday = holiday;
        }
        public String name(boolean isFa) { return isFa ? fa : en; }
    }

    private static final HashMap<String, Ev> JAL = new HashMap<>();
    private static final HashMap<String, Ev> LUN = new HashMap<>();

    private static void putJ(int m, int d, String fa, String en, boolean holiday) {
        JAL.put(m + "/" + d, new Ev(fa, en, holiday));
    }

    private static void putL(int m, int d, String fa, String en, boolean holiday) {
        LUN.put(m + "/" + d, new Ev(fa, en, holiday));
    }

    static {
        // ===== Jalali fixed — official holidays =====
        putJ(1, 1,  "عید نوروز", "Nowruz (Persian New Year)", true);
        putJ(1, 2,  "عید نوروز", "Nowruz (Persian New Year)", true);
        putJ(1, 3,  "عید نوروز", "Nowruz (Persian New Year)", true);
        putJ(1, 4,  "عید نوروز", "Nowruz (Persian New Year)", true);
        putJ(1, 12, "روز جمهوری اسلامی", "Islamic Republic Day", true);
        putJ(1, 13, "سیزده‌بدر", "Sizdah Bedar (Nature Day)", true);
        putJ(3, 14, "رحلت امام خمینی", "Demise of Imam Khomeini", true);
        putJ(3, 15, "قیام ۱۵ خرداد", "Khordad 15 Uprising", true);
        putJ(11, 22, "پیروزی انقلاب اسلامی", "Islamic Revolution Victory", true);
        putJ(12, 29, "ملی شدن صنعت نفت", "Oil Industry Nationalization Day", true);

        // ===== Jalali fixed — occasions (not off) =====
        putJ(2, 1,  "روز بزرگداشت سعدی", "Saadi Day", false);
        putJ(2, 12, "روز معلم", "Teachers' Day", false);
        putJ(2, 25, "روز بزرگداشت فردوسی و زبان فارسی", "Ferdowsi & Persian Language Day", false);
        putJ(3, 3,  "آزادسازی خرمشهر", "Liberation of Khorramshahr", false);
        putJ(6, 7,  "روز آتش‌نشانی و خدمات ایمنی", "Firefighters & Safety Day", false);
        putJ(6, 20, "روز بزرگداشت حافظ", "Hafez Day", false);
        putJ(7, 7,  "روز پزشک", "Doctors' Day", false);
        putJ(8, 13, "روز دانش‌آموز و مبارزه با استکبار جهانی", "Student Day & Anti-Arrogance Day", false);
        putJ(8, 24, "روز کتاب، کتاب‌خوانی و ناشر", "Book & Reading Day", false);
        putJ(9, 16, "روز دانشجو", "University Student Day", false);
        putJ(9, 30, "شب یلدا (چله)", "Yalda Night (Longest Night)", false);
        putJ(10, 9, "روز بصیرت و میثاق امت با ولایت", "Dey 9 Day (Covenant Day)", false);
        putJ(11, 12, "ورود امام خمینی به ایران", "Imam Khomeini's Return to Iran", false);

        // ===== Lunar (Hijri) — official holidays =====
        putL(1, 9,  "تاسوعای حسینی", "Tasua of Husayn", true);
        putL(1, 10, "عاشورای حسینی", "Ashura of Husayn", true);
        putL(2, 20, "اربعین حسینی", "Arbaeen of Husayn", true);
        putL(2, 28, "رحلت پیامبر اکرم و شهادت امام حسن مجتبی", "Demise of Prophet & Martyrdom of Imam Hassan", true);
        putL(3, 17, "میلاد پیامبر اکرم و امام جعفر صادق", "Birth of Prophet Muhammad & Imam Sadeq", true);
        putL(6, 3,  "شهادت حضرت فاطمه زهرا و روز زن", "Martyrdom of Hazrat Fatemeh & Women's Day", true);
        putL(7, 13, "ولادت امام علی و روز پدر", "Birth of Imam Ali & Father's Day", true);
        putL(7, 27, "عید سعید مبعث", "Eid al-Mab'ath", true);
        putL(8, 15, "ولادت امام زمان و جشن نیمه شعبان", "Birth of Imam Mahdi (Mid-Sha'ban)", true);
        putL(9, 21, "شهادت امام علی و شب قدر", "Martyrdom of Imam Ali & Laylat al-Qadr", true);
        putL(10, 1, "عید سعید فطر", "Eid al-Fitr", true);
        putL(10, 2, "تعطیل به مناسبت عید سعید فطر", "Eid al-Fitr Holiday", true);
        putL(12, 10, "عید سعید قربان", "Eid al-Adha", true);
        putL(12, 18, "عید سعید غدیر خم", "Eid al-Ghadir", true);

        // ===== Lunar — occasions =====
        putL(3, 8,  "شهادت امام حسن عسکری", "Martyrdom of Imam Hassan Askari", false);
        putL(9, 1,  "آغاز ماه مبارک رمضان", "Beginning of Ramadan", false);
        putL(9, 19, "شب قدر", "Laylat al-Qadr", false);
        putL(9, 23, "شب قدر", "Laylat al-Qadr", false);
    }

    /** events for one day; hijri month length used for "last day of Safar" rule */
    public static List<Ev> forDay(int jm, int jd, int hm, int hd) {
        List<Ev> list = new ArrayList<>();
        Ev e = JAL.get(jm + "/" + jd);
        if (e != null) list.add(e);
        if (hm == 2 && hd == Dates.hijriMonthLength(0, 2)) {
            list.add(new Ev("شهادت امام رضا (آخر صفر)", "Martyrdom of Imam Reza (End of Safar)", true));
        }
        e = LUN.get(hm + "/" + hd);
        if (e != null) list.add(e);
        // holidays first
        list.sort((a, b) -> Boolean.compare(b.holiday, a.holiday));
        return list;
    }

    /** is this day an official holiday? */
    public static boolean isHoliday(int jm, int jd, int hm, int hd) {
        Ev e = JAL.get(jm + "/" + jd);
        if (e != null && e.holiday) return true;
        if (hm == 2 && hd == Dates.hijriMonthLength(0, 2)) return true;
        e = LUN.get(hm + "/" + hd);
        return e != null && e.holiday;
    }

    /** next official holiday from a given Jalali day (inclusive of today).
     *  returns days-until and event, or null if none within maxDays */
    public static Object[] nextHoliday(int jy, int jm, int jd, int hijriAdj, int maxDays) {
        int jdn = Dates.j2d(jy, jm, jd);
        for (int i = 0; i < maxDays; i++) {
            Dates.JDate j = Dates.d2j(jdn + i);
            Dates.HDate h = Dates.hijriFromJdn(jdn + i, hijriAdj);
            if (isHoliday(j.jm, j.jd, h.hm, h.hd)) {
                return new Object[]{i, forDay(j.jm, j.jd, h.hm, h.hd).get(0)};
            }
        }
        return null;
    }
}
