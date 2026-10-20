package com.taqvimcod.calendar;

/**
 * Accurate date conversions: Gregorian <-> Jalali (Persian) <-> Hijri (tabular Islamic),
 * plus Julian Day Number math and helpers.
 *
 * Jalali algorithm: port of the well-tested "jalaali-js" implementation
 * (Behrooz Kamali / Kazimierz M. Borkowski astronomical algorithm) — accurate
 * for a very wide range of years including leap-year irregularities.
 *
 * Hijri algorithm: classic tabular (arithmetic) Islamic calendar (Fourmilab /
 * Kuwaiti algorithm) with a user-adjustable correction (default -1 for Iran).
 */
public final class Dates {

    private Dates() {}

    // ---------- holder classes ----------
    public static final class JDate { public int jy, jm, jd; }
    public static final class GDate { public int gy, gm, gd; }
    public static final class HDate { public int hy, hm, hd; }

    // ---------- integer helpers (JS truncating semantics) ----------
    private static int div(int a, int b) { return a / b; }
    private static int mod(int a, int b) { return a - (a / b) * b; }

    // ---------- Jalaali core ----------
    private static final int[] BREAKS = {-61, 9, 38, 199, 426, 686, 756, 818, 1111, 1181,
            1210, 1635, 2060, 2097, 2192, 2262, 2324, 2394, 2456, 3178};

    /** returns {leap, gy, march} */
    private static int[] jalCal(int jy) {
        int bl = BREAKS.length;
        int gy = jy + 621;
        int leapJ = -14;
        int jp = BREAKS[0];
        int jump = 0, n = 0;
        for (int i = 1; i < bl; i++) {
            int jm = BREAKS[i];
            jump = jm - jp;
            if (jy < jm) break;
            leapJ = leapJ + div(jump, 33) * 8 + div(mod(jump, 33), 4);
            jp = jm;
        }
        n = jy - jp;
        leapJ = leapJ + div(n, 33) * 8 + div(mod(n, 33) + 3, 4);
        if (mod(jump, 33) == 4 && jump - n == 4) leapJ += 1;
        int leapG = div(gy, 4) - div((div(gy, 100) + 1) * 3, 4) - 150;
        int march = 20 + leapJ - leapG;
        if (jump - n < 6) n = n - jump + div(jump + 4, 33) * 33;
        int leap = mod(mod(n + 1, 33) - 1, 4);
        if (leap == -1) leap = 4;
        return new int[]{leap, gy, march};
    }

    /** Jalaali -> Julian Day Number */
    public static int j2d(int jy, int jm, int jd) {
        int[] r = jalCal(jy);
        return g2d(r[1], 3, r[2]) + (jm - 1) * 31 - div(jm, 7) * (jm - 7) + jd - 1;
    }

    /** Julian Day Number -> Jalaali */
    public static JDate d2j(int jdn) {
        int gy = d2g(jdn).gy;
        int jy = gy - 621;
        int[] r = jalCal(jy);
        int jdn1f = g2d(gy, 3, r[2]);
        int k = jdn - jdn1f;
        int jm, jd;
        if (k >= 0) {
            if (k <= 185) {
                jm = 1 + div(k, 31);
                jd = mod(k, 31) + 1;
                JDate res = new JDate(); res.jy = jy; res.jm = jm; res.jd = jd;
                return res;
            }
            k -= 186;
        } else {
            jy -= 1;
            k += 179;
            if (r[0] == 1) k += 1;
        }
        jm = 7 + div(k, 30);
        jd = mod(k, 30) + 1;
        JDate res = new JDate(); res.jy = jy; res.jm = jm; res.jd = jd;
        return res;
    }

    // ---------- Gregorian <-> JDN ----------
    public static int g2d(int gy, int gm, int gd) {
        int d = div((gy + div(gm - 8, 6) + 100100) * 1461, 4)
                + div(153 * mod(gm + 9, 12) + 2, 5)
                + gd - 34840408;
        d = d - div(div(gy + 100100 + div(gm - 8, 6), 100) * 3, 4) + 752;
        return d;
    }

    public static GDate d2g(int jdn) {
        int j = 4 * jdn + 139361631;
        j = j + div(div(4 * jdn + 183187720, 146097) * 3, 4) * 4 - 3908;
        int i = div(mod(j, 1461), 4) * 5 + 308;
        int gd = div(mod(i, 153), 5) + 1;
        int gm = mod(div(i, 153), 12) + 1;
        int gy = div(j, 1461) - 100100 + div(8 - gm, 6);
        GDate g = new GDate(); g.gy = gy; g.gm = gm; g.gd = gd;
        return g;
    }

    // ---------- Jalaali helpers ----------
    public static boolean isLeapJalaali(int jy) {
        return jalCal(jy)[0] == 0;
    }

    public static int jalaaliMonthLength(int jy, int jm) {
        if (jm <= 6) return 31;
        if (jm <= 11) return 30;
        return isLeapJalaali(jy) ? 30 : 29;
    }

    public static JDate toJalaali(int gy, int gm, int gd) {
        return d2j(g2d(gy, gm, gd));
    }

    public static GDate toGregorian(int jy, int jm, int jd) {
        return d2g(j2d(jy, jm, jd));
    }

    // ---------- Hijri (tabular, with correction) ----------
    /** JDN -> Hijri. adjust: correction days (Iran suggested: -1) */
    public static HDate hijriFromJdn(int jdn, int adjust) {
        int jd = jdn - adjust;
        int l = jd - 1948440 + 10632;
        int n = Math.floorDiv(l - 1, 10631);
        l = l - 10631 * n + 354;
        int j = Math.floorDiv(10985 - l, 5316) * Math.floorDiv(50 * l, 17719)
                + Math.floorDiv(l, 5670) * Math.floorDiv(43 * l, 15238);
        l = l - (Math.floorDiv(30 - j, 15) * Math.floorDiv(17719 * j, 50)
                - Math.floorDiv(j, 16) * Math.floorDiv(15238 * j, 43)) + 29;
        int m = Math.floorDiv(24 * l, 709);
        int d = l - Math.floorDiv(709 * m, 24);
        int y = 30 * n + j - 30;
        HDate h = new HDate(); h.hy = y; h.hm = m; h.hd = d;
        return h;
    }

    /** tabular Islamic month length: odd months 30, even months 29 */
    public static int hijriMonthLength(int hy, int hm) {
        return (hm % 2 == 1) ? 30 : 29;
    }

    // ---------- weekday & day helpers ----------
    /** 0 = Saturday ... 6 = Friday (Persian week) */
    public static int weekdayFromJdn(int jdn) {
        return (int) Math.floorMod((long) jdn + 2L, 7L);
    }

    public static JDate jOfJdn(int jdn) { return d2j(jdn); }
    public static int jdnOfJ(int jy, int jm, int jd) { return j2d(jy, jm, jd); }
    public static GDate gOfJdn(int jdn) { return d2g(jdn); }
    public static int jdnOfG(int gy, int gm, int gd) { return g2d(gy, gm, gd); }

    /** today in device timezone */
    public static GDate todayG() {
        java.util.Calendar c = java.util.Calendar.getInstance();
        GDate g = new GDate();
        g.gy = c.get(java.util.Calendar.YEAR);
        g.gm = c.get(java.util.Calendar.MONTH) + 1;
        g.gd = c.get(java.util.Calendar.DAY_OF_MONTH);
        return g;
    }

    public static JDate todayJ() {
        GDate g = todayG();
        return toJalaali(g.gy, g.gm, g.gd);
    }

    /** local midnight millis of the day containing jdn */
    public static long dayStartMillis(int jdn) {
        GDate g = d2g(jdn);
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.clear();
        c.set(g.gy, g.gm - 1, g.gd, 0, 0, 0);
        c.set(java.util.Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    /** UTC midnight millis for the Gregorian date represented by a JDN.
     * Android all-day calendar events are stored as UTC dates, not local times. */
    public static long dayStartUtcMillis(int jdn) {
        GDate g = d2g(jdn);
        java.util.Calendar c = new java.util.GregorianCalendar(
                java.util.TimeZone.getTimeZone("UTC"));
        c.clear();
        c.set(g.gy, g.gm - 1, g.gd, 0, 0, 0);
        c.set(java.util.Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    /** jdn containing the given epoch millis (device timezone) */
    public static int jdnOfMillis(long millis) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTimeInMillis(millis);
        return g2d(c.get(java.util.Calendar.YEAR), c.get(java.util.Calendar.MONTH) + 1,
                c.get(java.util.Calendar.DAY_OF_MONTH));
    }

    /** jdn containing UTC-based all-day event millis, independent of device timezone. */
    public static int jdnOfMillisUtc(long millis) {
        java.util.Calendar c = new java.util.GregorianCalendar(
                java.util.TimeZone.getTimeZone("UTC"));
        c.setTimeInMillis(millis);
        return g2d(c.get(java.util.Calendar.YEAR), c.get(java.util.Calendar.MONTH) + 1,
                c.get(java.util.Calendar.DAY_OF_MONTH));
    }

    /** hour/min of millis */
    public static int[] hourMin(long millis) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTimeInMillis(millis);
        return new int[]{c.get(java.util.Calendar.HOUR_OF_DAY), c.get(java.util.Calendar.MINUTE)};
    }
}
