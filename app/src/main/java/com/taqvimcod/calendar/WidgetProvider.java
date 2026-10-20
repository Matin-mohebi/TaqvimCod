package com.taqvimcod.calendar;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.RemoteViews;

import java.util.List;

/** Home-screen widget: today's Jalali date, Hijri/Gregorian, holiday and next event. */
public class WidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context ctx, AppWidgetManager mgr, int[] ids) {
        for (int id : ids) render(ctx, mgr, id);
    }

    @Override
    public void onReceive(Context ctx, Intent intent) {
        super.onReceive(ctx, intent);
        String a = intent.getAction();
        if (Intent.ACTION_TIME_CHANGED.equals(a) || Intent.ACTION_DATE_CHANGED.equals(a)
                || Intent.ACTION_TIMEZONE_CHANGED.equals(a)
                || Intent.ACTION_BOOT_COMPLETED.equals(a)) {
            updateAll(ctx);
        }
    }

    public static void updateAll(Context ctx) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(ctx);
        int[] ids = mgr.getAppWidgetIds(new ComponentName(ctx, WidgetProvider.class));
        for (int id : ids) render(ctx, mgr, id);
    }

    private static void render(Context ctx, AppWidgetManager mgr, int id) {
        boolean fa = S.fa(ctx);
        Dates.JDate j = Dates.todayJ();
        int jdn = Dates.j2d(j.jy, j.jm, j.jd);
        int wd = Dates.weekdayFromJdn(jdn);
        Dates.GDate g = Dates.d2g(jdn);
        Dates.HDate h = Dates.hijriFromJdn(jdn, Prefs.hijriAdj(ctx));

        RemoteViews rv = new RemoteViews(ctx.getPackageName(), R.layout.widget_taqvim);

        rv.setTextViewText(R.id.tv_dow, S.weekdays(ctx)[wd]);
        rv.setTextViewText(R.id.tv_jalali, (fa ? S.d(ctx, j.jd + " " + S.months(ctx)[j.jm - 1] + " " + j.jy)
                : j.jd + " " + S.months(ctx)[j.jm - 1] + " " + j.jy));
        String other = (fa ? S.d(ctx, g.gd + " " + S.gregMonths(ctx)[g.gm - 1] + " " + g.gy)
                : g.gd + " " + S.gregMonths(ctx)[g.gm - 1] + " " + g.gy)
                + "  •  " + S.fmtHDate(ctx, h);
        rv.setTextViewText(R.id.tv_other, other);

        // today's holiday / occasion
        List<Holidays.Ev> occ = Holidays.forDay(j.jm, j.jd, h.hm, h.hd);
        if (!occ.isEmpty()) {
            Holidays.Ev ev = occ.get(0);
            rv.setViewVisibility(R.id.tv_holiday, android.view.View.VISIBLE);
            rv.setTextViewText(R.id.tv_holiday, (ev.holiday ? "◼ " : "◇ ") + ev.name(fa));
        } else {
            rv.setViewVisibility(R.id.tv_holiday, android.view.View.GONE);
        }

        // next event within 7 days
        String next = "";
        if (CalendarRepo.hasPerms(ctx)) {
            long now = System.currentTimeMillis();
            List<CalendarRepo.Ev> evs = CalendarRepo.instances(ctx, now, now + 7L * 86400000L);
            for (CalendarRepo.Ev ev : evs) {
                if (ev.begin < now - 60000L) continue;
                int eventJdn = ev.allDay ? Dates.jdnOfMillisUtc(ev.begin) : Dates.jdnOfMillis(ev.begin);
                int dayDiff = eventJdn - jdn;
                String dayName = dayDiff == 0 ? (fa ? "امروز" : "Today")
                        : dayDiff == 1 ? S.t(ctx, "tomorrow")
                        : S.weekdays(ctx)[Dates.weekdayFromJdn(eventJdn)];
                String when;
                if (ev.allDay) {
                    when = S.t(ctx, "all_day_label");
                } else {
                    when = S.fmtTime(ctx, ev.begin);
                }
                next = dayName + " " + when + " — " + ev.title;
                break;
            }
        }
        if (!next.isEmpty()) {
            rv.setViewVisibility(R.id.tv_next, android.view.View.VISIBLE);
            rv.setTextViewText(R.id.tv_next, S.t(ctx, "next_event").replace("%s", next));
        } else {
            rv.setViewVisibility(R.id.tv_next, android.view.View.GONE);
        }

        // tap opens the app
        Intent open = new Intent(ctx, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(ctx, 0, open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        rv.setOnClickPendingIntent(R.id.widget_root, pi);

        try {
            mgr.updateAppWidget(id, rv);
        } catch (Exception ignored) {}
    }
}
