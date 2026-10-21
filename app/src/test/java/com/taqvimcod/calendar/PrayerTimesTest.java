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
}
