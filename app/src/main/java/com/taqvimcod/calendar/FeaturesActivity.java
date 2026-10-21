package com.taqvimcod.calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Feature hub with upcoming occasions, international clocks and quick actions.
 * Everything is calculated locally; no network access or external SDKs required.
 */
public final class FeaturesActivity extends Activity {
    private Theme.Pal pal;
    private LinearLayout root;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final TextView[] clockValues = new TextView[4];
    private final String[] zones = {"Asia/Tehran", "Europe/London", "America/New_York", "Asia/Tokyo"};
    private final String[] cities = {"تهران", "لندن", "نیویورک", "توکیو"};
    private final Runnable clockTick = new Runnable() {
        @Override public void run() {
            SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            for (int i = 0; i < clockValues.length; i++) {
                fmt.setTimeZone(TimeZone.getTimeZone(zones[i]));
                if (clockValues[i] != null) clockValues[i].setText(fmt.format(new Date()));
            }
            handler.postDelayed(this, 1000L);
        }
    };

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        pal = Theme.get(this);
        getWindow().setStatusBarColor(pal.gradStart);
        getWindow().setNavigationBarColor(pal.bg);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(pal.bg);
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(14), dp(16), dp(30));
        root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        scroll.addView(root);
        setContentView(scroll);

        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);
        TextView back = label("‹", 29, pal.onGrad, true);
        back.setGravity(Gravity.CENTER);
        back.setBackground(round(pal.gradEnd, 17));
        top.addView(back, new LinearLayout.LayoutParams(dp(46), dp(46)));
        back.setOnClickListener(v -> finish());
        LinearLayout titleCol = new LinearLayout(this);
        titleCol.setOrientation(LinearLayout.VERTICAL);
        titleCol.setPadding(dp(12), 0, 0, 0);
        titleCol.addView(label("مرکز هوشمند تقویم", 21, pal.text, true));
        titleCol.addView(label("زمان، مناسبت‌ها و ابزارهای روزانه", 12, pal.textSub, false));
        top.addView(titleCol, new LinearLayout.LayoutParams(0, -2, 1));
        root.addView(top);

        addHero();
        addQuickActions();
        addWorldClocks();
        addUpcomingOccasions();
        addPrivacyNote();
    }

    private void addHero() {
        Dates.JDate j = Dates.todayJ();
        Calendar now = Calendar.getInstance();
        int jdn = Dates.j2d(j.jy, j.jm, j.jd);
        Dates.GDate g = Dates.d2g(jdn);
        Dates.HDate h = Dates.hijriFromJdn(jdn, Prefs.hijriAdj(this));
        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setPadding(dp(20), dp(20), dp(20), dp(20));
        hero.setBackground(gradient());
        hero.setElevation(dp(9));
        hero.setRotationX(3f);
        hero.setAlpha(0f);
        hero.setTranslationY(dp(18));
        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(-1, -2);
        hp.topMargin = dp(18);
        root.addView(hero, hp);
        hero.animate().alpha(1f).translationY(0).rotationX(0f).setDuration(520).setInterpolator(new DecelerateInterpolator()).start();

        hero.addView(label(new SimpleDateFormat("EEEE، d MMMM", new Locale("fa", "IR")).format(now.getTime()), 13, pal.onGradSub, false));
        hero.addView(label(j.jd + " " + monthName(j.jm) + " " + j.jy, 30, pal.onGrad, true));
        hero.addView(label("میلادی  " + g.gy + "/" + two(g.gm) + "/" + two(g.gd)
                + "     •     قمری " + h.hd + "/" + h.hm + "/" + h.hy, 12, pal.onGradSub, false));
        LinearLayout badges = new LinearLayout(this);
        badges.setGravity(Gravity.CENTER_VERTICAL);
        badges.setPadding(0, dp(14), 0, 0);
        TextView weekday = label("✦  روز " + (Dates.weekdayFromJdn(jdn) + 1) + " هفته", 12, pal.onGrad, true);
        weekday.setPadding(dp(11), dp(7), dp(11), dp(7));
        weekday.setBackground(round(0x2BFFFFFF, 18));
        badges.addView(weekday);
        TextView live = label("  زنده و آفلاین  ", 12, pal.onGrad, true);
        live.setPadding(dp(11), dp(7), dp(11), dp(7));
        live.setBackground(round(0x2BFFFFFF, 18));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2);
        lp.rightMargin = dp(8);
        badges.addView(live, lp);
        hero.addView(badges);
    }

    private void addQuickActions() {
        section("میانبرهای حرفه‌ای");
        addAction("▦", "تقویم میلادی", "نمای ماهانهٔ بین‌المللی با جزئیات شمسی و قمری", () ->
                startActivity(new Intent(this, WorldCalendarActivity.class)));
        addAction("⇄", "تبدیل تاریخ", "شمسی، میلادی و قمری", () ->
                startActivity(new Intent(this, ToolsActivity.class)));
        addAction("☪", "اوقات شرعی و قبله‌نما", "اوقات نماز، روش محاسبه و قطب‌نما", () ->
                startActivity(new Intent(this, PrayerActivity.class)));
        addAction("✧", "تقویم و رویدادهای گوشی", "ثبت و همگام‌سازی با تقویم دستگاه", () -> finish());
        addAction("✓", "برنامه‌ریز روزانه", "فهرست کارها، تیک انجام و ذخیرهٔ آفلاین", () ->
                startActivity(new Intent(this, DailyPlannerActivity.class)));
    }

    private void addWorldClocks() {
        section("ساعت جهانی");
        TextView hint = label("زمان شهرهای مهم؛ بر اساس منطقهٔ زمانی خودکار", 12, pal.textSub, false);
        root.addView(hint);
        LinearLayout row1 = new LinearLayout(this);
        row1.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout row2 = new LinearLayout(this);
        row2.setOrientation(LinearLayout.HORIZONTAL);
        for (int i = 0; i < cities.length; i++) {
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dp(13), dp(14), dp(13), dp(14));
            card.setBackground(round(pal.surface, 18));
            card.setElevation(dp(3));
            TextView city = label("◉  " + cities[i], 13, pal.text, true);
            TextView time = label("--:--:--", 20, pal.accent, true);
            time.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
            card.addView(city);
            TextView zone = label(zones[i], 9.5f, pal.textSub, false);
            zone.setPadding(0, dp(4), 0, dp(8));
            card.addView(zone);
            card.addView(time);
            clockValues[i] = time;
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -2, 1f);
            lp.setMargins(dp(3), dp(7), dp(3), dp(3));
            (i < 2 ? row1 : row2).addView(card, lp);
            card.setAlpha(0f);
            card.setTranslationY(dp(12));
            card.animate().alpha(1f).translationY(0).setStartDelay(100L * i).setDuration(360).start();
        }
        root.addView(row1);
        root.addView(row2);
    }

    private void addUpcomingOccasions() {
        section("مناسبت‌های پیشِ رو");
        Dates.JDate today = Dates.todayJ();
        int base = Dates.j2d(today.jy, today.jm, today.jd);
        int shown = 0;
        for (int offset = 0; offset < 45 && shown < 8; offset++) {
            int jdn = base + offset;
            Dates.JDate j = Dates.d2j(jdn);
            Dates.HDate h = Dates.hijriFromJdn(jdn, Prefs.hijriAdj(this));
            java.util.List<Holidays.Ev> events = Holidays.forDay(j.jm, j.jd, h.hm, h.hd);
            if (events.isEmpty()) continue;
            for (Holidays.Ev event : events) {
                LinearLayout card = new LinearLayout(this);
                card.setGravity(Gravity.CENTER_VERTICAL);
                card.setPadding(dp(13), dp(12), dp(13), dp(12));
                card.setBackground(round(pal.surface, 17));
                card.setElevation(dp(2));
                LinearLayout dateBox = new LinearLayout(this);
                dateBox.setGravity(Gravity.CENTER);
                dateBox.setOrientation(LinearLayout.VERTICAL);
                dateBox.setPadding(dp(10), dp(8), dp(10), dp(8));
                dateBox.setBackground(round(event.holiday ? pal.redSoft : pal.accentSoft, 13));
                dateBox.addView(label(two(j.jd), 19, event.holiday ? pal.red : pal.accent, true));
                dateBox.addView(label(monthShort(j.jm), 10, pal.textSub, false));
                card.addView(dateBox);
                LinearLayout words = new LinearLayout(this);
                words.setOrientation(LinearLayout.VERTICAL);
                words.setPadding(dp(12), 0, 0, 0);
                words.addView(label(event.name(true), 14, pal.text, true));
                String relative = offset == 0 ? "امروز" : offset == 1 ? "فردا" : offset + " روز دیگر";
                words.addView(label(relative + (event.holiday ? "  •  تعطیل رسمی" : "  •  مناسبت"), 11, pal.textSub, false));
                card.addView(words, new LinearLayout.LayoutParams(0, -2, 1f));
                card.setOnClickListener(v -> new AlertDialog.Builder(this)
                        .setTitle(event.name(true))
                        .setMessage(monthName(j.jm) + " " + j.jd + " " + j.jy + "\n"
                                + "میلادی: " + Dates.d2g(jdn).gy + "/" + two(Dates.d2g(jdn).gm) + "/" + two(Dates.d2g(jdn).gd)
                                + "\n" + (event.holiday ? "تعطیل رسمی در تقویم ایران" : "مناسبت تقویمی"))
                        .setPositiveButton("باشه", null).show());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
                lp.topMargin = dp(8);
                root.addView(card, lp);
                card.setAlpha(0f);
                card.setTranslationY(dp(10));
                card.animate().alpha(1f).translationY(0).setStartDelay(45L * shown).setDuration(300).start();
                shown++;
                if (shown >= 8) break;
            }
        }
        if (shown == 0) {
            root.addView(label("در ۴۵ روز آینده مناسبتی در فهرست آفلاین ثبت نشده است.", 13, pal.textSub, false));
        }
    }

    private void addPrivacyNote() {
        LinearLayout note = new LinearLayout(this);
        note.setOrientation(LinearLayout.VERTICAL);
        note.setPadding(dp(15), dp(14), dp(15), dp(14));
        note.setBackground(round(pal.surfaceAlt, 16));
        TextView title = label("🔒  حریم خصوصی", 13, pal.text, true);
        note.addView(title);
        note.addView(label("ساعت‌ها و مناسبت‌ها روی خود گوشی محاسبه می‌شوند. برای نمایش این صفحه اینترنت لازم نیست.", 12, pal.textSub, false));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.topMargin = dp(18);
        root.addView(note, lp);
    }

    private void addAction(String icon, String title, String subtitle, Runnable action) {
        LinearLayout card = new LinearLayout(this);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(14), dp(13), dp(14), dp(13));
        card.setBackground(round(pal.surface, 18));
        card.setElevation(dp(4));
        TextView glyph = label(icon, 25, pal.onGrad, true);
        glyph.setGravity(Gravity.CENTER);
        glyph.setBackground(gradient());
        card.addView(glyph, new LinearLayout.LayoutParams(dp(50), dp(50)));
        LinearLayout words = new LinearLayout(this);
        words.setOrientation(LinearLayout.VERTICAL);
        words.setPadding(dp(12), 0, 0, 0);
        words.addView(label(title, 14, pal.text, true));
        words.addView(label(subtitle, 11, pal.textSub, false));
        card.addView(words, new LinearLayout.LayoutParams(0, -2, 1f));
        card.addView(label("‹", 24, pal.accent, true));
        card.setOnClickListener(v -> {
            v.animate().scaleX(0.975f).scaleY(0.975f).setDuration(80).withEndAction(() -> {
                v.animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                action.run();
            }).start();
        });
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.topMargin = dp(8);
        root.addView(card, lp);
    }

    private void section(String text) {
        TextView t = label(text, 16, pal.text, true);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.topMargin = dp(23);
        lp.bottomMargin = dp(4);
        root.addView(t, lp);
    }

    private TextView label(String value, float size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setTypeface(Typeface.create("sans-serif", bold ? Typeface.BOLD : Typeface.NORMAL));
        return t;
    }

    private GradientDrawable round(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        return d;
    }

    private GradientDrawable gradient() {
        GradientDrawable d = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{pal.gradStart, pal.gradEnd, pal.accent});
        d.setCornerRadius(dp(24));
        return d;
    }

    private int dp(float value) { return (int) (value * getResources().getDisplayMetrics().density); }
    private String two(int n) { return n < 10 ? "0" + n : String.valueOf(n); }
    private String monthName(int month) {
        String[] names = {"فروردین","اردیبهشت","خرداد","تیر","مرداد","شهریور","مهر","آبان","آذر","دی","بهمن","اسفند"};
        return names[Math.max(0, Math.min(11, month - 1))];
    }
    private String monthShort(int month) { return new String[]{"فرو","ارد","خرد","تیر","مرد","شهر","مهر","آبا","آذر","دی","بهم","اسف"}[Math.max(0, Math.min(11, month - 1))]; }

    @Override protected void onResume() {
        super.onResume();
        handler.removeCallbacks(clockTick);
        handler.post(clockTick);
    }
    @Override protected void onPause() {
        handler.removeCallbacks(clockTick);
        super.onPause();
    }
}
