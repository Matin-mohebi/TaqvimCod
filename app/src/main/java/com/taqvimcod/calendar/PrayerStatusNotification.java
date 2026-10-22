package com.taqvimcod.calendar;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/** User-opt-in ongoing notification; deliberately does not run a foreground service. */
public final class PrayerStatusNotification {
    private static final String CHANNEL = "taqvim_status";
    private static final int ID = 8092;
    private static final String[] NAMES = {"اذان صبح", "اذان ظهر", "عصر", "اذان مغرب", "اذان عشا"};

    private PrayerStatusNotification() {}

    public static void update(Context context) {
        if (!SecurePrefs.getBoolean(context, "status_notification_enabled", false)) return;
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) return;
        if (Build.VERSION.SDK_INT >= 26) {
            manager.createNotificationChannel(new NotificationChannel(CHANNEL, "تاریخ و اذان بعدی", NotificationManager.IMPORTANCE_LOW));
        }
        Date now = new Date();
        double lat = parse(SecurePrefs.getString(context, "prayer_lat", "36.6736"), 36.6736);
        double lon = parse(SecurePrefs.getString(context, "prayer_lon", "48.4787"), 48.4787);
        int method = SecurePrefs.getInt(context, "prayer_method", 0);
        PrayerTimes.Day day = PrayerTimes.calculate(now, lat, lon, TimeZone.getDefault(), method);
        long[] times = {day.fajr, day.dhuhr, day.asr, day.maghrib, day.isha};
        int next = -1;
        for (int i=0; i<times.length; i++) {
            if (times[i] != Long.MIN_VALUE && times[i] > now.getTime()) { next = i; break; }
        }
        if (next < 0) {
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.DAY_OF_YEAR, 1);
            day = PrayerTimes.calculate(tomorrow.getTime(), lat, lon, TimeZone.getDefault(), method);
            times = new long[]{day.fajr, day.dhuhr, day.asr, day.maghrib, day.isha};
            next = 0;
        }
        String dateLabel = S.fmtJDate(context, Dates.todayJ());
        String timeLabel = times[next] == Long.MIN_VALUE ? "زمان نامشخص" :
                S.d(context, new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(times[next])));
        String description = "اذان بعدی: " + NAMES[next] + " • ساعت " + timeLabel;
        Intent open = new Intent(context, PrayerActivity.class);
        PendingIntent pending = PendingIntent.getActivity(context, ID, open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Notification.Builder builder = Build.VERSION.SDK_INT >= 26
                ? new Notification.Builder(context, CHANNEL)
                : new Notification.Builder(context);
        Notification notification = builder.setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle("تقویم‌کد • " + dateLabel)
                .setContentText(description)
                .setStyle(new Notification.BigTextStyle().bigText(description))
                .setContentIntent(pending)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setCategory(Notification.CATEGORY_STATUS)
                .build();
        try { manager.notify(ID, notification); } catch (SecurityException ignored) { }
    }

    public static void cancel(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) manager.cancel(ID);
    }

    private static double parse(String value, double fallback) {
        try { return Double.parseDouble(value); } catch (Exception ignored) { return fallback; }
    }
}
