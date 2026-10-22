package com.taqvimcod.calendar;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/** Daily local prayer reminders. Alarms are inexact to respect Android power policies. */
public class PrayerReminderReceiver extends BroadcastReceiver {
    public static final String ACTION_PRAYER = "com.taqvimcod.calendar.PRAYER_REMINDER";
    private static final String CHANNEL = "prayer_times";
    private static final String[] NAMES = {"اذان صبح","اذان ظهر","عصر","اذان مغرب","اذان عشا"};

    @Override public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())
                || Intent.ACTION_MY_PACKAGE_REPLACED.equals(intent.getAction())) {
            if (SecurePrefs.getBoolean(context,"prayer_reminders_enabled",false)) scheduleAll(context);
            return;
        }
        if (!ACTION_PRAYER.equals(intent.getAction())) return;
        int prayer = intent.getIntExtra("prayer_id",-1);
        if (prayer < 0 || prayer >= NAMES.length) return;
        showNotification(context, prayer);
        if (SecurePrefs.getBoolean(context, "status_notification_enabled", false))
            PrayerStatusNotification.update(context);
        scheduleOne(context, prayer, System.currentTimeMillis()+60_000L);
    }

    public static void scheduleAll(Context context) {
        for (int i=0;i<NAMES.length;i++) scheduleOne(context,i,System.currentTimeMillis()+1000L);
    }

    public static void cancelAll(Context context) {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if(am==null)return;
        for(int i=0;i<NAMES.length;i++){
            PendingIntent pi=pending(context,i,PendingIntent.FLAG_NO_CREATE);
            if(pi!=null){am.cancel(pi);pi.cancel();}
        }
    }

    private static PendingIntent pending(Context c,int id,int extraFlags) {
        Intent i=new Intent(c,PrayerReminderReceiver.class).setAction(ACTION_PRAYER).putExtra("prayer_id",id);
        int flags=PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE;
        if(extraFlags==PendingIntent.FLAG_NO_CREATE) flags=PendingIntent.FLAG_NO_CREATE|PendingIntent.FLAG_IMMUTABLE;
        return PendingIntent.getBroadcast(c,700+id,i,flags);
    }

    private static void scheduleOne(Context context,int id,long after) {
        if (!SecurePrefs.getBoolean(context,"prayer_reminders_enabled",false)) return;
        double lat=parse(SecurePrefs.getString(context,"prayer_lat","36.6736"),36.6736);
        double lon=parse(SecurePrefs.getString(context,"prayer_lon","48.4787"),48.4787);
        Calendar cal=Calendar.getInstance();
        cal.setTimeInMillis(after);
        cal.set(Calendar.HOUR_OF_DAY,12); cal.set(Calendar.MINUTE,0); cal.set(Calendar.SECOND,0); cal.set(Calendar.MILLISECOND,0);
        PrayerTimes.Day day=PrayerTimes.calculate(cal.getTime(),lat,lon,TimeZone.getDefault(),SecurePrefs.getInt(context,"prayer_method",0));
        long[] times={day.fajr,day.dhuhr,day.asr,day.maghrib,day.isha};
        long when=times[id];
        if(when==Long.MIN_VALUE)return;
        if(when<=after){
            cal.add(Calendar.DAY_OF_YEAR,1);
            day=PrayerTimes.calculate(cal.getTime(),lat,lon,TimeZone.getDefault(),SecurePrefs.getInt(context,"prayer_method",0));
            times=new long[]{day.fajr,day.dhuhr,day.asr,day.maghrib,day.isha};
            when=times[id];
        }
        if(when==Long.MIN_VALUE)return;
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi=pending(context,id,0);
        if(am!=null && pi!=null) {
            try { am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,when,pi); }
            catch(SecurityException ignored) { am.set(AlarmManager.RTC_WAKEUP,when,pi); }
        }
    }

    // ---------------- notification channel + tone ----------------

    /**
     * Channel id carries the selected tone (notification-channel sound is
     * immutable after creation, so each tone gets its own channel and stale
     * ones are deleted when the choice changes).
     */
    private static String channelFor(Context c) {
        int tone = Prefs.reminderTone(c);
        return tone <= 0 ? CHANNEL : CHANNEL + "_t" + tone;
    }

    private static NotificationChannel buildChannel(Context c, String channelId) {
        NotificationChannel ch = new NotificationChannel(channelId,
                "یادآوری اوقات شرعی", NotificationManager.IMPORTANCE_DEFAULT);
        int tone = Prefs.reminderTone(c);
        if (tone > 0) {
            int resId = c.getResources().getIdentifier("tone_" + tone, "raw", c.getPackageName());
            if (resId != 0) {
                Uri sound = Uri.parse("android.resource://" + c.getPackageName() + "/" + resId);
                AudioAttributes attrs = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();
                ch.setSound(sound, attrs);
            }
        }
        return ch;
    }

    /** called after the user picks a new tone: drop stale tone channels and
     *  pre-create the channel for the current choice */
    public static void refreshReminderChannel(Context c) {
        if (Build.VERSION.SDK_INT < 26) return;
        NotificationManager nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return;
        String current = channelFor(c);
        for (int i = 1; i <= 10; i++) {
            String id = CHANNEL + "_t" + i;
            if (!id.equals(current) && nm.getNotificationChannel(id) != null)
                nm.deleteNotificationChannel(id);
        }
        if (nm.getNotificationChannel(current) == null)
            nm.createNotificationChannel(buildChannel(c, current));
    }

    private static void showNotification(Context context,int id) {
        NotificationManager nm=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(nm==null)return;
        String channelId = channelFor(context);
        if(Build.VERSION.SDK_INT>=26) {
            if (nm.getNotificationChannel(channelId) == null)
                nm.createNotificationChannel(buildChannel(context, channelId));
        }
        Intent open=new Intent(context,PrayerActivity.class);
        PendingIntent content=PendingIntent.getActivity(context,900+id,open,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        android.app.Notification.Builder b=Build.VERSION.SDK_INT>=26
                ? new android.app.Notification.Builder(context,channelId)
                : new android.app.Notification.Builder(context);
        b.setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle("یادآوری اوقات شرعی")
                .setContentText(NAMES[id]+" فرا رسیده است.")
                .setContentIntent(content).setAutoCancel(true);
        try { nm.notify(700+id,b.build()); } catch(SecurityException ignored) {}
    }

    private static double parse(String s,double fallback){try{return Double.parseDouble(s);}catch(Exception e){return fallback;}}
}
