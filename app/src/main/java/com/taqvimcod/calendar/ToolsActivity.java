package com.taqvimcod.calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Offline date toolbox: Jalali/Gregorian/Hijri conversion, date difference,
 * Nowruz countdown and native copy/share actions.
 */
public final class ToolsActivity extends Activity {
    private Theme.Pal pal;
    private LinearLayout body;
    private final Calendar[] range = {Calendar.getInstance(), Calendar.getInstance()};
    private int rangeStage = 0;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        pal = Theme.get(this);
        getWindow().setStatusBarColor(pal.gradStart);
        getWindow().setNavigationBarColor(pal.bg);
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(pal.bg);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(14), dp(18), dp(28));
        scroll.addView(root);

        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);
        TextView back = label("‹", 30, pal.onGrad, true);
        back.setGravity(Gravity.CENTER);
        back.setBackground(round(pal.gradEnd, 18));
        top.addView(back, new LinearLayout.LayoutParams(dp(48), dp(48)));
        back.setOnClickListener(v -> finish());
        LinearLayout heading = new LinearLayout(this);
        heading.setOrientation(LinearLayout.VERTICAL);
        heading.setPadding(dp(12), 0, 0, 0);
        heading.addView(label("ابزارهای تقویم", 21, pal.text, true));
        heading.addView(label("همه‌چیز آفلاین؛ بدون ارسال اطلاعات", 12, pal.textSub, false));
        top.addView(heading, new LinearLayout.LayoutParams(0, -2, 1));
        root.addView(top);

        TextView today = label(todaySummary(), 15, pal.onGrad, true);
        today.setPadding(dp(17), dp(18), dp(17), dp(18));
        today.setBackground(gradient());
        LinearLayout.LayoutParams todayLp = new LinearLayout.LayoutParams(-1, -2);
        todayLp.topMargin = dp(18);
        root.addView(today, todayLp);

        addSection(root, "ابزارهای تاریخ");
        addTool(root, "⇄", "تبدیل تاریخ میلادی", "تبدیل میلادی به شمسی و قمری", v -> chooseConversionDate());
        addTool(root, "☼", "تبدیل تاریخ شمسی", "انتخاب تاریخ شمسی و نمایش معادل میلادی و قمری", v -> chooseJalaliDate());
        addTool(root, "↔", "فاصلهٔ بین دو تاریخ", "محاسبهٔ تعداد روزهای بین دو تاریخ دلخواه", v -> chooseRangeStart());
        addTool(root, "🌱", "شمارش معکوس نوروز", "تعداد روزهای باقی‌مانده تا آغاز سال شمسی", v -> showNowruz());
        addTool(root, "🎂", "محاسبهٔ سن", "سن شمسی، روزهای سپری‌شده و تولد بعدی", v -> startActivity(new Intent(this, AgeCalculatorActivity.class)));
        addSection(root, "اشتراک‌گذاری");
        addTool(root, "▣", "کپی تاریخ امروز", "کپی تاریخ شمسی، میلادی و قمری در کلیپ‌بورد", v -> copyToday());
        addTool(root, "↗", "اشتراک‌گذاری تاریخ", "ارسال تاریخ امروز به برنامه‌های دیگر", v -> shareToday());
        addSection(root, "دربارهٔ ابزارها");
        TextView note = label("محاسبه‌ها روی خود دستگاه انجام می‌شوند. تاریخ قمری بر پایهٔ تقویم حسابی است و ممکن است با اعلام رسمی محلی یک روز تفاوت داشته باشد.", 13, pal.textSub, false);
        note.setPadding(dp(15), dp(14), dp(15), dp(14));
        note.setBackground(round(pal.surface, 16));
        root.addView(note);
        setContentView(scroll);
    }

    private void chooseConversionDate() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (picker, y, m, d) -> {
            Dates.JDate j = Dates.toJalaali(y, m + 1, d);
            int jdn = Dates.g2d(y, m + 1, d);
            Dates.HDate h = Dates.hijriFromJdn(jdn, Prefs.hijriAdj(this));
            Calendar selected = Calendar.getInstance();
            selected.clear();
            selected.set(y, m, d);
            String weekday = new SimpleDateFormat("EEEE", new Locale("fa", "IR")).format(selected.getTime());
            String result = "میلادی: " + y + "/" + two(m + 1) + "/" + two(d)
                    + "\nشمسی: " + j.jy + "/" + two(j.jm) + "/" + two(j.jd)
                    + "\nقمری (حسابی): " + h.hy + "/" + two(h.hm) + "/" + two(h.hd)
                    + "\nروز هفته: " + weekday;
            showResult("نتیجهٔ تبدیل تاریخ", result);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    /** Lets the user select a Jalali date directly, then converts it without network access. */
    private void chooseJalaliDate() {
        Dates.JDate current = Dates.todayJ();
        LinearLayout fields = new LinearLayout(this);
        fields.setGravity(Gravity.CENTER);
        fields.setPadding(dp(12), dp(8), dp(12), dp(8));

        android.widget.NumberPicker year = new android.widget.NumberPicker(this);
        year.setMinValue(1200);
        year.setMaxValue(1600);
        year.setValue(Math.max(1200, Math.min(1600, current.jy)));
        year.setWrapSelectorWheel(false);
        year.setContentDescription("سال شمسی");

        android.widget.NumberPicker month = new android.widget.NumberPicker(this);
        String[] months = {"فروردین","اردیبهشت","خرداد","تیر","مرداد","شهریور","مهر","آبان","آذر","دی","بهمن","اسفند"};
        month.setMinValue(1);
        month.setMaxValue(12);
        month.setDisplayedValues(months);
        month.setValue(current.jm);
        month.setWrapSelectorWheel(true);
        month.setContentDescription("ماه شمسی");

        android.widget.NumberPicker day = new android.widget.NumberPicker(this);
        day.setMinValue(1);
        day.setMaxValue(Dates.jalaaliMonthLength(year.getValue(), month.getValue()));
        day.setValue(Math.min(current.jd, day.getMaxValue()));
        day.setWrapSelectorWheel(true);
        day.setContentDescription("روز ماه");

        android.widget.NumberPicker.OnValueChangeListener updateDays = (picker, oldValue, newValue) -> {
            int max = Dates.jalaaliMonthLength(year.getValue(), month.getValue());
            day.setMaxValue(max);
            if (day.getValue() > max) day.setValue(max);
        };
        year.setOnValueChangedListener(updateDays);
        month.setOnValueChangedListener(updateDays);

        fields.addView(day, new LinearLayout.LayoutParams(0, -2, 0.7f));
        fields.addView(month, new LinearLayout.LayoutParams(0, -2, 1.3f));
        fields.addView(year, new LinearLayout.LayoutParams(0, -2, 0.9f));

        new android.app.AlertDialog.Builder(this)
                .setTitle("انتخاب تاریخ شمسی")
                .setView(fields)
                .setNegativeButton("انصراف", null)
                .setPositiveButton("تبدیل", (dialog, which) -> {
                    int jy = year.getValue(), jm = month.getValue(), jd = day.getValue();
                    Dates.GDate g = Dates.toGregorian(jy, jm, jd);
                    int jdn = Dates.j2d(jy, jm, jd);
                    Dates.HDate h = Dates.hijriFromJdn(jdn, Prefs.hijriAdj(this));
                    Calendar date = Calendar.getInstance();
                    date.clear();
                    date.set(g.gy, g.gm - 1, g.gd);
                    String weekday = new SimpleDateFormat("EEEE", new Locale("fa", "IR")).format(date.getTime());
                    String result = "شمسی: " + jy + "/" + two(jm) + "/" + two(jd)
                            + "\\nمیلادی: " + g.gy + "/" + two(g.gm) + "/" + two(g.gd)
                            + "\\nقمری (حسابی): " + h.hy + "/" + two(h.hm) + "/" + two(h.hd)
                            + "\\nروز هفته: " + weekday;
                    showResult("نتیجهٔ تبدیل تاریخ", result);
                }).show();
    }

    private void chooseRangeStart() {
        rangeStage = 0;
        pickRangeDate();
    }

    private void pickRangeDate() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (picker, y, m, d) -> {
            range[rangeStage].set(y, m, d, 0, 0, 0);
            range[rangeStage].set(Calendar.MILLISECOND, 0);
            if (rangeStage == 0) {
                rangeStage = 1;
                Toast.makeText(this, "حالا تاریخ دوم را انتخاب کن", Toast.LENGTH_SHORT).show();
                pickRangeDate();
            } else {
                int jdnA = Dates.g2d(range[0].get(Calendar.YEAR), range[0].get(Calendar.MONTH) + 1, range[0].get(Calendar.DAY_OF_MONTH));
                int jdnB = Dates.g2d(range[1].get(Calendar.YEAR), range[1].get(Calendar.MONTH) + 1, range[1].get(Calendar.DAY_OF_MONTH));
                long days = Math.abs((long) jdnB - jdnA);
                Dates.JDate a = Dates.toJalaali(range[0].get(Calendar.YEAR), range[0].get(Calendar.MONTH) + 1, range[0].get(Calendar.DAY_OF_MONTH));
                Dates.JDate b = Dates.toJalaali(range[1].get(Calendar.YEAR), range[1].get(Calendar.MONTH) + 1, range[1].get(Calendar.DAY_OF_MONTH));
                showResult("فاصلهٔ دو تاریخ", "فاصله: " + days + " روز\nتاریخ اول شمسی: " + a.jy + "/" + two(a.jm) + "/" + two(a.jd)
                        + "\nتاریخ دوم شمسی: " + b.jy + "/" + two(b.jm) + "/" + two(b.jd));
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showNowruz() {
        Dates.JDate now = Dates.todayJ();
        if (now.jm == 1 && now.jd == 1) {
            showResult("شمارش معکوس نوروز", "نوروز امروز است؛ سال نو مبارک! 🌱");
            return;
        }
        Dates.GDate next = Dates.toGregorian(now.jy + 1, 1, 1);
        Calendar target = Calendar.getInstance();
        target.clear();
        target.set(next.gy, next.gm - 1, next.gd);
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        long days = (long) Dates.g2d(next.gy, next.gm, next.gd)
                - Dates.g2d(today.get(Calendar.YEAR), today.get(Calendar.MONTH) + 1, today.get(Calendar.DAY_OF_MONTH));
        showResult("شمارش معکوس نوروز", days == 0 ? "نوروز امروز است؛ سال نو مبارک! 🌱" :
                "تا اول فروردین " + (now.jy + 1) + "، " + Math.max(0, days) + " روز باقی مانده است.\n"
                        + "تاریخ میلادی: " + next.gy + "/" + two(next.gm) + "/" + two(next.gd));
    }

    private String todaySummary() {
        Dates.JDate j = Dates.todayJ();
        Calendar c = Calendar.getInstance();
        Dates.HDate h = Dates.hijriFromJdn(Dates.g2d(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH)), Prefs.hijriAdj(this));
        return "امروز\n" + j.jd + " " + monthName(j.jm) + " " + j.jy
                + "\nمیلادی " + c.get(Calendar.YEAR) + "/" + two(c.get(Calendar.MONTH) + 1) + "/" + two(c.get(Calendar.DAY_OF_MONTH))
                + "  •  قمری " + h.hd + "/" + h.hm + "/" + h.hy;
    }

    private String monthName(int month) {
        String[] names = {"فروردین","اردیبهشت","خرداد","تیر","مرداد","شهریور","مهر","آبان","آذر","دی","بهمن","اسفند"};
        return names[Math.max(0, Math.min(11, month - 1))];
    }
    private String two(int n) { return n < 10 ? "0" + n : String.valueOf(n); }

    private void copyToday() {
        android.content.ClipboardManager cm = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        cm.setPrimaryClip(android.content.ClipData.newPlainText("تاریخ امروز", todaySummary()));
        Toast.makeText(this, "تاریخ در کلیپ‌بورد کپی شد", Toast.LENGTH_SHORT).show();
    }
    private void shareToday() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, todaySummary() + "\n\nساخته‌شده با تقویم");
        startActivity(Intent.createChooser(i, "اشتراک‌گذاری تاریخ"));
    }

    private void showResult(String title, String message) {
        new android.app.AlertDialog.Builder(this).setTitle(title).setMessage(message)
                .setPositiveButton("باشه", null).setNeutralButton("کپی نتیجه", (d, w) -> {
                    android.content.ClipboardManager cm = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    cm.setPrimaryClip(android.content.ClipData.newPlainText(title, message));
                    Toast.makeText(this, "نتیجه کپی شد", Toast.LENGTH_SHORT).show();
                }).show();
    }

    private void addTool(LinearLayout parent, String icon, String title, String subtitle, View.OnClickListener click) {
        LinearLayout card = new LinearLayout(this);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(14), dp(13), dp(14), dp(13));
        card.setBackground(round(pal.surface, 18));
        TextView glyph = label(icon, 23, pal.onGrad, true);
        glyph.setGravity(Gravity.CENTER);
        glyph.setBackground(round(pal.gradStart, 14));
        card.addView(glyph, new LinearLayout.LayoutParams(dp(48), dp(48)));
        LinearLayout words = new LinearLayout(this);
        words.setOrientation(LinearLayout.VERTICAL);
        words.setPadding(dp(12), 0, 0, 0);
        words.addView(label(title, 15, pal.text, true));
        TextView sub = label(subtitle, 11.5f, pal.textSub, false);
        words.addView(sub);
        card.addView(words, new LinearLayout.LayoutParams(0, -2, 1));
        TextView arrow = label("‹", 24, pal.accent, true);
        card.addView(arrow);
        card.setClickable(true);
        card.setFocusable(true);
        card.setOnClickListener(click);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.topMargin = dp(9);
        parent.addView(card, lp);
    }

    private void addSection(LinearLayout parent, String text) {
        TextView t = label(text, 14, pal.accent, true);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.topMargin = dp(22);
        lp.bottomMargin = dp(2);
        parent.addView(t, lp);
    }

    private TextView label(String text, float size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(text);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setTypeface(Typeface.create("sans-serif", bold ? Typeface.BOLD : Typeface.NORMAL));
        return t;
    }
    private int dp(float value) { return (int) (value * getResources().getDisplayMetrics().density); }
    private GradientDrawable round(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        return d;
    }
    private GradientDrawable gradient() {
        GradientDrawable d = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{pal.gradStart, pal.gradEnd});
        d.setCornerRadius(dp(22));
        return d;
    }
}
