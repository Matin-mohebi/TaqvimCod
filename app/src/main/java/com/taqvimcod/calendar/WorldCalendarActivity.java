package com.taqvimcod.calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/** International Gregorian month grid with local Jalali/Hijri date details. */
public final class WorldCalendarActivity extends Activity {
    private Theme.Pal pal;
    private LinearLayout root;
    private TextView monthTitle;
    private GridLayout grid;
    private Calendar visible = Calendar.getInstance();

    private int dp(float n) { return (int)(n * getResources().getDisplayMetrics().density); }

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        pal = Theme.get(this);
        getWindow().setStatusBarColor(pal.gradStart);
        getWindow().setNavigationBarColor(pal.bg);
        if (state != null) visible.setTimeInMillis(state.getLong("visible", System.currentTimeMillis()));
        visible.set(Calendar.DAY_OF_MONTH, 1);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(pal.bg);
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(16), dp(16), dp(26));
        root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        scroll.addView(root);
        setContentView(scroll);

        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);
        TextView back = label("‹", 29, pal.onGrad, true);
        back.setGravity(Gravity.CENTER);
        back.setBackground(round(pal.gradEnd, 16));
        top.addView(back, new LinearLayout.LayoutParams(dp(46), dp(46)));
        back.setOnClickListener(v -> finish());
        LinearLayout titles = new LinearLayout(this);
        titles.setOrientation(LinearLayout.VERTICAL);
        titles.setPadding(dp(12), 0, 0, 0);
        titles.addView(label("تقویم میلادی", 22, pal.text, true));
        titles.addView(label("نمای بین‌المللی با معادل شمسی و قمری", 12, pal.textSub, false));
        top.addView(titles);
        root.addView(top);

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14), dp(14), dp(14), dp(14));
        card.setBackground(round(pal.surface, 22));
        card.setElevation(dp(5));
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(-1, -2);
        cp.topMargin = dp(18);
        root.addView(card, cp);

        LinearLayout nav = new LinearLayout(this);
        nav.setGravity(Gravity.CENTER_VERTICAL);
        TextView prev = label("‹", 29, pal.accent, true);
        prev.setGravity(Gravity.CENTER);
        prev.setBackground(round(pal.surfaceAlt, 14));
        nav.addView(prev, new LinearLayout.LayoutParams(dp(44), dp(44)));
        prev.setOnClickListener(v -> { visible.add(Calendar.MONTH, -1); renderMonth(); });
        monthTitle = label("", 18, pal.text, true);
        monthTitle.setGravity(Gravity.CENTER);
        nav.addView(monthTitle, new LinearLayout.LayoutParams(0, -2, 1f));
        TextView next = label("›", 29, pal.accent, true);
        next.setGravity(Gravity.CENTER);
        next.setBackground(round(pal.surfaceAlt, 14));
        nav.addView(next, new LinearLayout.LayoutParams(dp(44), dp(44)));
        next.setOnClickListener(v -> { visible.add(Calendar.MONTH, 1); renderMonth(); });
        card.addView(nav);

        grid = new GridLayout(this);
        grid.setColumnCount(7);
        grid.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        LinearLayout.LayoutParams gp = new LinearLayout.LayoutParams(-1, -2);
        gp.topMargin = dp(12);
        card.addView(grid, gp);
        renderMonth();

        LinearLayout note = new LinearLayout(this);
        note.setOrientation(LinearLayout.VERTICAL);
        note.setPadding(dp(15), dp(14), dp(15), dp(14));
        note.setBackground(round(pal.surfaceAlt, 17));
        TextView noteTitle = label("سه تقویم، یک لمس", 15, pal.text, true);
        note.addView(noteTitle);
        note.addView(label("هر روز را لمس کن تا تاریخ میلادی، شمسی، قمری و مناسبت‌های ثبت‌شده را ببینی. تاریخ قمری محاسباتی است و ممکن است با تقویم رسمی محلی اختلاف یک‌روزه داشته باشد.", 12, pal.textSub, false));
        LinearLayout.LayoutParams np = new LinearLayout.LayoutParams(-1, -2);
        np.topMargin = dp(16);
        root.addView(note, np);
    }

    private void renderMonth() {
        monthTitle.setText(new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).format(visible.getTime()));
        grid.setCameraDistance(dp(7000));
        grid.setAlpha(0.35f);
        grid.setRotationY(7f);
        grid.removeAllViews();
        String[] weekdays = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
        for (String day : weekdays) {
            TextView h = label(day, 10, pal.textSub, true);
            h.setGravity(Gravity.CENTER);
            GridLayout.LayoutParams p = cellParams(32);
            grid.addView(h, p);
        }

        Calendar first = (Calendar) visible.clone();
        first.set(Calendar.DAY_OF_MONTH, 1);
        int offset = first.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
        int count = first.getActualMaximum(Calendar.DAY_OF_MONTH);
        Calendar today = Calendar.getInstance();
        for (int i = 0; i < offset; i++) {
            TextView blank = label("", 12, pal.textSub, false);
            grid.addView(blank, cellParams(43));
        }
        for (int d = 1; d <= count; d++) {
            final int day = d;
            Calendar selected = (Calendar) first.clone();
            selected.set(Calendar.DAY_OF_MONTH, d);
            boolean isToday = selected.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                    && selected.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                    && selected.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH);
            int jdn = Dates.g2d(selected.get(Calendar.YEAR), selected.get(Calendar.MONTH) + 1, d);
            Dates.JDate j = Dates.d2j(jdn);
            Dates.HDate h = Dates.hijriFromJdn(jdn, Prefs.hijriAdj(this));
            boolean holiday = Holidays.isHoliday(j.jm, j.jd, h.hm, h.hd);
            TextView cell = label(String.valueOf(d), 14, isToday ? pal.onGrad : (holiday ? pal.red : pal.text), isToday || holiday);
            cell.setGravity(Gravity.CENTER);
            cell.setBackground(round(isToday ? pal.accent : (holiday ? pal.redSoft : 0x00000000), 13));
            cell.setOnClickListener(v -> {
                v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(70).withEndAction(() -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(110).start();
                    showDay(selected, jdn, day);
                }).start();
            });
            grid.addView(cell, cellParams(43));
        }
        grid.animate().alpha(1f).rotationY(0f).setDuration(260).start();
    }

    private void showDay(Calendar date, int jdn, int gd) {
        Dates.JDate j = Dates.d2j(jdn);
        Dates.HDate h = Dates.hijriFromJdn(jdn, Prefs.hijriAdj(this));
        SimpleDateFormat fmt = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.ENGLISH);
        StringBuilder message = new StringBuilder();
        message.append("Gregorian: ").append(fmt.format(date.getTime()));
        message.append("\nشمسی: ").append(j.jd).append(" ").append(monthName(j.jm)).append(" ").append(j.jy);
        message.append("\nقمری (حسابی): ").append(h.hd).append("/").append(h.hm).append("/").append(h.hy);
        java.util.List<Holidays.Ev> events = Holidays.forDay(j.jm, j.jd, h.hm, h.hd);
        if (!events.isEmpty()) {
            for (Holidays.Ev event : events) message.append("\n\n").append(event.holiday ? "تعطیل: " : "مناسبت: ").append(event.name(true));
        } else {
            message.append("\n\nمناسبتی در فهرست آفلاین ثبت نشده است.");
        }
        new AlertDialog.Builder(this).setTitle("روز " + gd).setMessage(message.toString())
                .setPositiveButton("باشه", null).show();
    }

    private GridLayout.LayoutParams cellParams(int height) {
        GridLayout.LayoutParams p = new GridLayout.LayoutParams(
                GridLayout.spec(GridLayout.UNDEFINED, 1f),
                GridLayout.spec(GridLayout.UNDEFINED));
        p.width = 0;
        p.height = dp(height);
        p.setMargins(dp(1), dp(1), dp(1), dp(1));
        return p;
    }

    private TextView label(String s, float size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s);
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
    private String monthName(int month) {
        return new String[]{"فروردین","اردیبهشت","خرداد","تیر","مرداد","شهریور","مهر","آبان","آذر","دی","بهمن","اسفند"}[Math.max(0, Math.min(11, month - 1))];
    }
    @Override protected void onSaveInstanceState(Bundle out) {
        out.putLong("visible", visible.getTimeInMillis());
        super.onSaveInstanceState(out);
    }
}
