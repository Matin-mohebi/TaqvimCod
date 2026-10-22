package com.taqvimcod.calendar;

/**
 * Offline date helpers used by the age calculator.
 * Zodiac labels are month-based cultural labels, not astronomical predictions.
 */
public final class BahesabTools {
    private BahesabTools() {}

    public static final String[] ZODIAC_SIGNS = {
        "حمل (فروردین)", "ثور (اردیبهشت)", "جوزا (خرداد)",
        "سرطان (تیر)", "اسد (مرداد)", "سنبله (شهریور)",
        "میزان (مهر)", "عقرب (آبان)", "قوس (آذر)",
        "جدی (دی)", "دلو (بهمن)", "حوت (اسفند)"
    };

    public static final class AgeResult {
        public final int years, months, days;
        public final long totalDays;
        public final int daysToNextBirthday;
        public final String zodiacSign;

        AgeResult(int years, int months, int days, long totalDays,
                  int daysToNextBirthday, String zodiacSign) {
            this.years = years;
            this.months = months;
            this.days = days;
            this.totalDays = totalDays;
            this.daysToNextBirthday = daysToNextBirthday;
            this.zodiacSign = zodiacSign;
        }
    }

    /** Calculates age using the project's own Jalali calendar implementation. */
    public static AgeResult calculateAge(int birthY, int birthM, int birthD) {
        if (birthM < 1 || birthM > 12) throw new IllegalArgumentException("Invalid Jalali month");
        int maxDay = Dates.jalaaliMonthLength(birthY, birthM);
        if (birthD < 1 || birthD > maxDay) throw new IllegalArgumentException("Invalid Jalali day");

        Dates.JDate today = Dates.todayJ();
        int birthJdn = Dates.j2d(birthY, birthM, birthD);
        int todayJdn = Dates.j2d(today.jy, today.jm, today.jd);
        if (birthJdn > todayJdn) throw new IllegalArgumentException("Birth date is in the future");

        int years = today.jy - birthY;
        int months = today.jm - birthM;
        int days = today.jd - birthD;
        if (days < 0) {
            months--;
            int previousMonth = today.jm - 1;
            int previousYear = today.jy;
            if (previousMonth < 1) {
                previousMonth = 12;
                previousYear--;
            }
            days += Dates.jalaaliMonthLength(previousYear, previousMonth);
        }
        if (months < 0) {
            years--;
            months += 12;
        }

        int nextYear = today.jy;
        if (today.jm > birthM || (today.jm == birthM && today.jd >= birthD)) nextYear++;
        int nextDay = Math.min(birthD, Dates.jalaaliMonthLength(nextYear, birthM));
        int nextBirthdayJdn = Dates.j2d(nextYear, birthM, nextDay);

        return new AgeResult(Math.max(0, years), Math.max(0, months), Math.max(0, days),
                todayJdn - birthJdn, Math.max(0, nextBirthdayJdn - todayJdn),
                ZODIAC_SIGNS[birthM - 1]);
    }
}
