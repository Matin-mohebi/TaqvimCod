package com.taqvimcod.calendar;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class DatesTest {

    @Test
    public void knownNowruzDatesConvertCorrectly() {
        assertJalali(2024, 3, 20, 1403, 1, 1);
        assertJalali(2025, 3, 20, 1403, 12, 30);
        assertJalali(2026, 3, 21, 1405, 1, 1);
    }

    @Test
    public void jalaliLeapYearsHaveCorrectEsfandLength() {
        assertTrue(Dates.isLeapJalaali(1403));
        assertEquals(30, Dates.jalaaliMonthLength(1403, 12));
        assertFalse(Dates.isLeapJalaali(1404));
        assertEquals(29, Dates.jalaaliMonthLength(1404, 12));
    }

    @Test
    public void gregorianJalaliRoundTripAcrossManyYears() {
        for (int gy = 1900; gy <= 2100; gy++) {
            for (int gm = 1; gm <= 12; gm++) {
                int maxDay = gregorianMonthLength(gy, gm);
                for (int gd = 1; gd <= maxDay; gd++) {
                    Dates.JDate j = Dates.toJalaali(gy, gm, gd);
                    Dates.GDate g = Dates.toGregorian(j.jy, j.jm, j.jd);
                    assertEquals("year " + gy + " month " + gm + " day " + gd, gy, g.gy);
                    assertEquals("year " + gy + " month " + gm + " day " + gd, gm, g.gm);
                    assertEquals("year " + gy + " month " + gm + " day " + gd, gd, g.gd);
                }
            }
        }
    }

    @Test
    public void jalaliMonthLengthsAreWithinExpectedBounds() {
        for (int jy = 1300; jy <= 1500; jy++) {
            for (int jm = 1; jm <= 12; jm++) {
                int length = Dates.jalaaliMonthLength(jy, jm);
                if (jm <= 6) assertEquals(31, length);
                else if (jm <= 11) assertEquals(30, length);
                else assertTrue(length == 29 || length == 30);
            }
        }
    }

    private static void assertJalali(int gy, int gm, int gd, int jy, int jm, int jd) {
        Dates.JDate actual = Dates.toJalaali(gy, gm, gd);
        assertEquals(jy, actual.jy);
        assertEquals(jm, actual.jm);
        assertEquals(jd, actual.jd);
        Dates.GDate back = Dates.toGregorian(actual.jy, actual.jm, actual.jd);
        assertEquals(gy, back.gy);
        assertEquals(gm, back.gm);
        assertEquals(gd, back.gd);
    }

    private static int gregorianMonthLength(int year, int month) {
        switch (month) {
            case 2: return (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) ? 29 : 28;
            case 4: case 6: case 9: case 11: return 30;
            default: return 31;
        }
    }
}
