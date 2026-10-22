package com.taqvimcod.calendar;

import org.junit.Test;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import static org.junit.Assert.*;

public class PrayerTimesTest {
    @Test public void zanjanPrayerTimesAreOrderedAndFinite() {
        TimeZone tehran=TimeZone.getTimeZone("Asia/Tehran");
        Calendar date=Calendar.getInstance(tehran);
        date.set(2026,Calendar.OCTOBER,21,12,0,0);
        PrayerTimes.Day d=PrayerTimes.calculate(date.getTime(),36.6736,48.4787,tehran,0);
        assertTrue(d.fajr>0);
        assertTrue(d.fajr<d.sunrise);
        assertTrue(d.sunrise<d.dhuhr);
        assertTrue(d.dhuhr<d.asr);
        assertTrue(d.asr<d.maghrib);
        assertTrue(d.maghrib<d.isha);
    }

    @Test public void qiblaBearingIsNormalized() {
        double bearing=PrayerTimes.qiblaBearing(36.6736,48.4787);
        assertTrue(bearing>=0.0 && bearing<360.0);
    }

    @Test public void allCalculationMethodsProduceOrderedDaylightTimes() {
        TimeZone tehran=TimeZone.getTimeZone("Asia/Tehran");
        Date date=new Date(1792584000000L);
        for(int method=0;method<=5;method++) {
            PrayerTimes.Day d=PrayerTimes.calculate(date,36.6736,48.4787,tehran,method);
            assertTrue("method "+method,d.fajr<d.sunrise);
            assertTrue("method "+method,d.sunrise<d.dhuhr);
            assertTrue("method "+method,d.dhuhr<d.asr);
            assertTrue("method "+method,d.asr<d.maghrib);
            assertTrue("method "+method,d.maghrib<d.isha);
        }
    }

    /** Regression: sunset must mirror sunrise around solar noon (same -0.833°
     *  altitude). The old code passed -0.833 to hourAngle (altitude +0.833) and
     *  produced a maghrib ~10 minutes too early. */
    @Test public void sunsetMirrorsSunriseAroundSolarNoon() {
        TimeZone tehran=TimeZone.getTimeZone("Asia/Tehran");
        Calendar date=Calendar.getInstance(tehran);
        date.set(2026,Calendar.OCTOBER,21,12,0,0);
        PrayerTimes.Day d=PrayerTimes.calculate(date.getTime(),36.6736,48.4787,tehran,0);
        long morning=d.dhuhr-d.sunrise;
        long evening=d.maghrib-d.dhuhr;
        assertTrue("morning="+morning+" evening="+evening,
                Math.abs(morning-evening)<2*60*1000L);
    }

    /** Regression: the calculation method must actually change the twilight
     *  angles (the method parameter used to be accepted but ignored). */
    @Test public void methodSelectionChangesTwilightTimes() {
        TimeZone tehran=TimeZone.getTimeZone("Asia/Tehran");
        Calendar date=Calendar.getInstance(tehran);
        date.set(2026,Calendar.OCTOBER,21,12,0,0);
        long when=date.getTimeInMillis();
        PrayerTimes.Day dTehran=PrayerTimes.calculate(when,36.6736,48.4787,tehran,0);
        PrayerTimes.Day dMwl=PrayerTimes.calculate(when,36.6736,48.4787,tehran,1);
        assertTrue("Tehran isha (14°) must be earlier than MWL isha (17°)",
                dTehran.isha<dMwl.isha);
        assertTrue("ISNA fajr (15°) must be later than Egyptian fajr (19.5°)",
                PrayerTimes.calculate(when,36.6736,48.4787,tehran,4).fajr
                        > PrayerTimes.calculate(when,36.6736,48.4787,tehran,2).fajr);
        PrayerTimes.Day dUmm=PrayerTimes.calculate(when,36.6736,48.4787,tehran,5);
        assertEquals("Umm al-Qura isha = maghrib + 90 min",90*60*1000L,dUmm.isha-dUmm.maghrib);
    }
}
