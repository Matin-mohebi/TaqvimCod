package com.taqvimcod.calendar;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/** Optional home-screen widget dedicated to today's prayer times. */
public class PrayerWidgetProvider extends AppWidgetProvider {
    @Override public void onUpdate(Context c, AppWidgetManager m, int[] ids) {
        for (int id:ids) render(c,m,id);
    }

    @Override public void onReceive(Context c, Intent i) {
        super.onReceive(c,i);
        String a=i.getAction();
        if (Intent.ACTION_TIME_CHANGED.equals(a) || Intent.ACTION_DATE_CHANGED.equals(a)
                || Intent.ACTION_TIMEZONE_CHANGED.equals(a) || Intent.ACTION_BOOT_COMPLETED.equals(a))
            updateAll(c);
    }

    public static void updateAll(Context c) {
        AppWidgetManager m=AppWidgetManager.getInstance(c);
        int[] ids=m.getAppWidgetIds(new ComponentName(c,PrayerWidgetProvider.class));
        for(int id:ids) render(c,m,id);
    }

    private static void render(Context c,AppWidgetManager m,int id) {
        String city=SecurePrefs.getString(c,"prayer_city","زنجان");
        double lat=parse(SecurePrefs.getString(c,"prayer_lat","36.6736"),36.6736);
        double lon=parse(SecurePrefs.getString(c,"prayer_lon","48.4787"),48.4787);
        PrayerTimes.Day d=PrayerTimes.calculate(new Date(),lat,lon,TimeZone.getDefault());
        SimpleDateFormat time=new SimpleDateFormat("HH:mm",Locale.getDefault());
        RemoteViews v=new RemoteViews(c.getPackageName(),R.layout.widget_prayer);
        v.setTextViewText(R.id.prayer_widget_city,city+" • اوقات شرعی");
        v.setTextViewText(R.id.prayer_widget_date,new SimpleDateFormat("EEEE، d MMMM",new Locale("fa","IR")).format(new Date()));
        v.setTextViewText(R.id.prayer_widget_times1,"صبح "+fmt(c,time,d.fajr)+"   ظهر "+fmt(c,time,d.dhuhr)+"   عصر "+fmt(c,time,d.asr));
        v.setTextViewText(R.id.prayer_widget_times2,"مغرب "+fmt(c,time,d.maghrib)+"   عشا "+fmt(c,time,d.isha)+"   طلوع "+fmt(c,time,d.sunrise));
        Intent open=new Intent(c,PrayerActivity.class);
        PendingIntent pi=PendingIntent.getActivity(c,881,open,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        v.setOnClickPendingIntent(R.id.prayer_widget_root,pi);
        try{m.updateAppWidget(id,v);}catch(Exception ignored){}
    }

    private static String fmt(Context c,SimpleDateFormat f,long millis) {
        return millis==Long.MIN_VALUE ? "—" : S.d(c,f.format(new Date(millis)));
    }
    private static double parse(String s,double fallback){try{return Double.parseDouble(s);}catch(Exception e){return fallback;}}
}
