package com.taqvimcod.calendar;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/** Offline solar prayer-time calculations. Results are estimates; local authority settings may differ. */
public final class PrayerTimes {
    private PrayerTimes() {}

    public static final class Day {
        public final long fajr, sunrise, dhuhr, asr, maghrib, isha;
        Day(long fajr, long sunrise, long dhuhr, long asr, long maghrib, long isha) {
            this.fajr=fajr; this.sunrise=sunrise; this.dhuhr=dhuhr;
            this.asr=asr; this.maghrib=maghrib; this.isha=isha;
        }
    }

    public static Day calculate(Date date, double latitude, double longitude, TimeZone zone) {
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180)
            throw new IllegalArgumentException("Coordinates out of range");
        Calendar c = Calendar.getInstance(zone);
        c.setTime(date);
        int year=c.get(Calendar.YEAR), month=c.get(Calendar.MONTH)+1, day=c.get(Calendar.DAY_OF_MONTH);
        double jd = julianDay(year, month, day);
        double noon = 12.0 + zone.getOffset(date.getTime()) / 3600000.0
                - longitude / 15.0 - equationOfTime(jd) / 60.0;
        double decl = sunDeclination(jd + 0.5);
        double dhuhr = noon;
        double sunrise = noon - hourAngle(latitude, decl, 0.833) / 15.0;
        double sunset = noon + hourAngle(latitude, decl, -0.833) / 15.0;
        double fajr = noon - hourAngle(latitude, decl, 18.0) / 15.0;
        double isha = noon + hourAngle(latitude, decl, 17.5) / 15.0;
        double asrAltitude = Math.toDegrees(Math.atan(1.0 / (1.0 + Math.tan(Math.abs(Math.toRadians(latitude)-decl)))));
        double asr = noon + hourAngle(latitude, decl, -asrAltitude) / 15.0;
        return new Day(toMillis(c, fajr), toMillis(c, sunrise), toMillis(c, dhuhr),
                toMillis(c, asr), toMillis(c, sunset), toMillis(c, isha));
    }

    private static long toMillis(Calendar base, double hours) {
        if (Double.isNaN(hours) || Double.isInfinite(hours)) return Long.MIN_VALUE;
        Calendar c=(Calendar)base.clone();
        c.set(Calendar.HOUR_OF_DAY,0); c.set(Calendar.MINUTE,0); c.set(Calendar.SECOND,0); c.set(Calendar.MILLISECOND,0);
        long dayStart=c.getTimeInMillis();
        return dayStart + Math.round(hours*3600000.0);
    }

    private static double hourAngle(double lat, double decl, double angle) {
        double numerator = -Math.sin(Math.toRadians(angle)) - Math.sin(Math.toRadians(lat))*Math.sin(decl);
        double denominator = Math.cos(Math.toRadians(lat))*Math.cos(decl);
        double v=numerator/denominator;
        if (v < -1 || v > 1 || Double.isNaN(v)) return Double.NaN;
        return Math.toDegrees(Math.acos(v));
    }

    private static double julianDay(int y, int m, int d) {
        if (m <= 2) { y--; m+=12; }
        int a=y/100, b=2-a+a/4;
        return Math.floor(365.25*(y+4716))+Math.floor(30.6001*(m+1))+d+b-1524.5;
    }

    private static double equationOfTime(double jd) {
        double t=(jd-2451545.0)/36525.0;
        double epsilon=Math.toRadians(23.439291-0.0130042*t);
        double l0=Math.toRadians(norm(280.46646+t*(36000.76983+0.0003032*t)));
        double m=Math.toRadians(357.52911+t*(35999.05029-0.0001537*t));
        double e=0.016708634-t*(0.000042037+0.0000001267*t);
        double y=Math.tan(epsilon/2); y*=y;
        double eq=y*Math.sin(2*l0)-2*e*Math.sin(m)+4*e*y*Math.sin(m)*Math.cos(2*l0)
                -0.5*y*y*Math.sin(4*l0)-1.25*e*e*Math.sin(2*m);
        return Math.toDegrees(eq)*4.0;
    }

    private static double sunDeclination(double jd) {
        double t=(jd-2451545.0)/36525.0;
        double l0=Math.toRadians(norm(280.46646+t*(36000.76983+0.0003032*t)));
        double m=Math.toRadians(357.52911+t*(35999.05029-0.0001537*t));
        double c=Math.sin(m)*(1.914602-t*(0.004817+0.000014*t))
                +Math.sin(2*m)*(0.019993-0.000101*t)+Math.sin(3*m)*0.000289;
        double omega=Math.toRadians(125.04-1934.136*t);
        double lambda=Math.toRadians(norm(Math.toDegrees(l0)+c-0.00569-0.00478*Math.sin(omega)));
        double epsilon=Math.toRadians(23.439291-0.0130042*t+0.00256*Math.cos(omega));
        return Math.asin(Math.sin(epsilon)*Math.sin(lambda));
    }

    private static double norm(double x) { return (x%360+360)%360; }

    /** Initial great-circle bearing from the supplied coordinates to the Kaaba, clockwise from north. */
    public static double qiblaBearing(double latitude, double longitude) {
        double lat1=Math.toRadians(latitude), lon1=Math.toRadians(longitude);
        double lat2=Math.toRadians(21.422487), lon2=Math.toRadians(39.826206);
        double y=Math.sin(lon2-lon1);
        double x=Math.cos(lat1)*Math.tan(lat2)-Math.sin(lat1)*Math.cos(lon2-lon1);
        return (Math.toDegrees(Math.atan2(y,x))+360.0)%360.0;
    }
}
