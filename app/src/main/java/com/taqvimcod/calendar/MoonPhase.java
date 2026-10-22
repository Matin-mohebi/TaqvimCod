package com.taqvimcod.calendar;

/**
 * Pure-Java moon phase calculator (no Android dependencies, unit-testable).
 *
 * Model: mean synodic month 29.53058867 d anchored to the reference new moon
 * of 2000-01-06 18:14 UTC (Julian Day 2451550.2597222). Accuracy of the mean
 * approach is roughly +/- 0.5 day near extremes, which is plenty for a
 * decorative glyph and an illumination percentage.
 */
public final class MoonPhase {
    private MoonPhase() {}

    /** mean synodic month length in days */
    public static final double SYNODIC_MONTH = 29.53058867;
    /** Julian Day of the reference new moon: 2000-01-06 18:14 UTC */
    public static final double EPOCH_JD = 2451550.2597222;

    /** Lunar phase index: 0 new, 1 waxing crescent, 2 first quarter, 3 waxing gibbous,
     *  4 full, 5 waning gibbous, 6 last quarter, 7 waning crescent. */
    public static final int NEW = 0, WAXING_CRESCENT = 1, FIRST_QUARTER = 2, WAXING_GIBBOUS = 3,
            FULL = 4, WANING_GIBBOUS = 5, LAST_QUARTER = 6, WANING_CRESCENT = 7;

    /** snapshot of the moon at a moment */
    public static final class Moon {
        public final int phase;          // 0..7
        public final double fraction;    // cycle position 0..1
        public final double ageDays;     // days since (mean) new moon
        public final int illuminationPercent; // 0..100
        Moon(int phase, double fraction, double ageDays, int illuminationPercent) {
            this.phase = phase; this.fraction = fraction;
            this.ageDays = ageDays; this.illuminationPercent = illuminationPercent;
        }
    }

    /** moon state at the given epoch milliseconds (interpreted as UTC) */
    public static Moon at(long utcMillis) {
        double jd = utcMillis / 86400000.0 + 2440587.5;
        double age = norm(jd - EPOCH_JD);
        double fraction = age / SYNODIC_MONTH;
        int phase = (int) Math.round(fraction * 8.0) % 8;
        int illum = (int) Math.round(illuminationFraction(fraction) * 100.0);
        return new Moon(phase, fraction, age, illum);
    }

    /** illuminated disc fraction 0..1 for a cycle position 0..1 */
    public static double illuminationFraction(double cycleFraction) {
        return (1.0 - Math.cos(2.0 * Math.PI * cycleFraction)) / 2.0;
    }

    /** days since the reference new moon, wrapped into [0, SYNODIC_MONTH) */
    public static double ageFromJd(double julianDay) {
        return norm(julianDay - EPOCH_JD);
    }

    private static double norm(double days) {
        double v = days % SYNODIC_MONTH;
        if (v < 0) v += SYNODIC_MONTH;
        return v;
    }
}
