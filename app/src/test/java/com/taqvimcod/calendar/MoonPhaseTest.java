package com.taqvimcod.calendar;

import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MoonPhaseTest {

    private static long utc(int y, int month, int d, int h, int min) {
        Calendar c = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        c.clear();
        c.set(y, month - 1, d, h, min, 0);
        return c.getTimeInMillis();
    }

    /** Known new moon: 11 January 2024, 11:57 UTC */
    @Test public void newMoonJanuary2024IsPhaseZero() {
        MoonPhase.Moon m = MoonPhase.at(utc(2024, 1, 11, 11, 57));
        assertEquals(MoonPhase.NEW, m.phase);
        assertTrue("illumination was " + m.illuminationPercent, m.illuminationPercent <= 2);
    }

    /** Known full moon: 25 January 2024, 17:54 UTC */
    @Test public void fullMoonJanuary2024IsPhaseFour() {
        MoonPhase.Moon m = MoonPhase.at(utc(2024, 1, 25, 17, 54));
        assertEquals(MoonPhase.FULL, m.phase);
        assertTrue("illumination was " + m.illuminationPercent, m.illuminationPercent >= 98);
    }

    /** Known first quarter: 18 January 2024, 03:53 UTC */
    @Test public void firstQuarterJanuary2024IsPhaseTwo() {
        assertEquals(MoonPhase.FIRST_QUARTER, MoonPhase.at(utc(2024, 1, 18, 3, 53)).phase);
    }

    /** Known full moon: 18 September 2024, 02:34 UTC */
    @Test public void fullMoonSeptember2024IsPhaseFour() {
        MoonPhase.Moon m = MoonPhase.at(utc(2024, 9, 18, 2, 34));
        assertEquals(MoonPhase.FULL, m.phase);
        assertTrue(m.illuminationPercent >= 98);
    }

    /** The algorithm's own anchor: new moon of 2000-01-06 18:14 UTC */
    @Test public void epochAnchorIsANewMoon() {
        MoonPhase.Moon m = MoonPhase.at(utc(2000, 1, 6, 18, 14));
        assertEquals(MoonPhase.NEW, m.phase);
        assertTrue(m.ageDays < 0.2);
    }

    @Test public void illuminationIsSymmetricAroundFullMoon() {
        double q = MoonPhase.illuminationFraction(0.25);
        double w = MoonPhase.illuminationFraction(0.75);
        assertTrue(Math.abs(q - 0.5) < 0.001);
        assertTrue(Math.abs(w - 0.5) < 0.001);
        assertTrue(MoonPhase.illuminationFraction(0.5) > 0.999);
        assertTrue(MoonPhase.illuminationFraction(0.0) < 0.001);
    }

    @Test public void phaseAdvancesMonotonicallyAcrossOneLunation() {
        long start = utc(2024, 1, 11, 12, 0); // new moon
        long step = 3L * 24L * 3600L * 1000L; // every 3 days
        int previous = MoonPhase.at(start).phase;
        int sawFull = 0, wrappedToNew = 0;
        for (int i = 1; i < 13; i++) {
            int p = MoonPhase.at(start + i * step).phase;
            assertTrue("phase went backwards: " + previous + " -> " + p,
                    p >= previous || (previous >= 7 && p == 0));
            if (previous >= 7 && p == 0) wrappedToNew++;
            if (p == MoonPhase.FULL) sawFull++;
            previous = p;
        }
        assertEquals(1, sawFull);
        assertEquals(1, wrappedToNew);
    }

    @Test public void ageStaysInsideSynodicMonth() {
        long t = utc(2023, 6, 1, 0, 0);
        for (int i = 0; i < 60; i++) {
            MoonPhase.Moon m = MoonPhase.at(t + i * 24L * 3600L * 1000L);
            assertTrue(m.ageDays >= 0.0 && m.ageDays < MoonPhase.SYNODIC_MONTH);
            assertTrue(m.fraction >= 0.0 && m.fraction < 1.0);
            assertTrue(m.illuminationPercent >= 0 && m.illuminationPercent <= 100);
        }
    }
}
