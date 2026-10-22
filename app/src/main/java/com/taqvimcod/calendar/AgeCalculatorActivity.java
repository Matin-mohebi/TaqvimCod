package com.taqvimcod.calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Locale;

/** Offline Jalali age calculator. No network access or personal-data upload. */
public final class AgeCalculatorActivity extends Activity {
    private Theme.Pal pal;
    private int dp(float v) { return (int) (v * getResources().getDisplayMetrics().density); }

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
        root.setPadding(dp(18), dp(18), dp(18), dp(24));
        scroll.addView(root);

        TextView back = text("‹  بازگشت", 16, pal.accent, true);
        back.setPadding(0, 0, 0, dp(16));
        back.setOnClickListener(v -> finish());
        root.addView(back);
        root.addView(text("محاسبهٔ سن", 25, pal.text, true));
        root.addView(text("تاریخ تولد شمسی را انتخاب کن؛ همهٔ محاسبات روی گوشی انجام می‌شود.", 14, pal.textSub, false));

        TextView choose = text("انتخاب تاریخ تولد", 17, pal.onGrad, true);
        choose.setGravity(Gravity.CENTER);
        choose.setPadding(dp(16), dp(15), dp(16), dp(15));
        choose.setBackground(round(pal.gradStart, 16));
        LinearLayout.LayoutParams buttonLp = new LinearLayout.LayoutParams(-1, -2);
        buttonLp.topMargin = dp(22);
        root.addView(choose, buttonLp);
        choose.setOnClickListener(v -> chooseBirthDate());

        TextView note = text("برج نمایش‌داده‌شده فقط بر اساس ماه شمسی است و طالع‌بینی علمی محسوب نمی‌شود.", 12, pal.textSub, false);
        note.setPadding(dp(14), dp(16), dp(14), dp(16));
        root.addView(note);
        setContentView(scroll);
    }

    private void chooseBirthDate() {
        Dates.JDate today = Dates.todayJ();
        LinearLayout fields = new LinearLayout(this);
        fields.setGravity(Gravity.CENTER);
        fields.setPadding(dp(8), dp(8), dp(8), dp(8));

        NumberPicker day = new NumberPicker(this);
        day.setMinValue(1);
        day.setMaxValue(Dates.jalaaliMonthLength(today.jy, today.jm));
        day.setValue(today.jd);
        day.setWrapSelectorWheel(true);

        NumberPicker month = new NumberPicker(this);
        String[] months = {"فروردین","اردیبهشت","خرداد","تیر","مرداد","شهریور","مهر","آبان","آذر","دی","بهمن","اسفند"};
        month.setMinValue(1); month.setMaxValue(12);
        month.setDisplayedValues(months);
        month.setValue(today.jm);
        month.setWrapSelectorWheel(true);

        NumberPicker year = new NumberPicker(this);
        year.setMinValue(1200); year.setMaxValue(today.jy);
        year.setValue(today.jy);
        year.setWrapSelectorWheel(false);

        NumberPicker.OnValueChangeListener update = (picker, oldValue, newValue) -> {
            int max = Dates.jalaaliMonthLength(year.getValue(), month.getValue());
            day.setMaxValue(max);
            if (day.getValue() > max) day.setValue(max);
        };
        month.setOnValueChangedListener(update);
        year.setOnValueChangedListener(update);
        fields.addView(day, new LinearLayout.LayoutParams(0, -2, 0.7f));
        fields.addView(month, new LinearLayout.LayoutParams(0, -2, 1.4f));
        fields.addView(year, new LinearLayout.LayoutParams(0, -2, 1f));

        new AlertDialog.Builder(this)
            .setTitle("تاریخ تولد شمسی")
            .setView(fields)
            .setNegativeButton("انصراف", null)
            .setPositiveButton("محاسبه", (dialog, which) -> {
                try {
                    BahesabTools.AgeResult result = BahesabTools.calculateAge(year.getValue(), month.getValue(), day.getValue());
                    String summary = String.format(Locale.ROOT,
                        "سن: %d سال، %d ماه و %d روز\nمجموع روزهای سپری‌شده: %d\nروز تا تولد بعدی: %d\nبرج ماه شمسی: %s",
                        result.years, result.months, result.days, result.totalDays,
                        result.daysToNextBirthday, result.zodiacSign);
                    new AlertDialog.Builder(this).setTitle("نتیجهٔ محاسبه").setMessage(summary).setPositiveButton("باشه", null).show();
                } catch (IllegalArgumentException e) {
                    new AlertDialog.Builder(this).setTitle("تاریخ نامعتبر").setMessage("تاریخ تولد باید معتبر و در گذشته باشد.").setPositiveButton("باشه", null).show();
                }
            }).show();
    }

    private TextView text(String value, float size, int color, boolean bold) {
        TextView v = new TextView(this);
        v.setText(value); v.setTextSize(size); v.setTextColor(color);
        if (bold) v.setTypeface(null, android.graphics.Typeface.BOLD);
        v.setGravity(Gravity.RIGHT);
        return v;
    }

    private android.graphics.drawable.GradientDrawable round(int color, int radius) {
        android.graphics.drawable.GradientDrawable d = new android.graphics.drawable.GradientDrawable();
        d.setColor(color); d.setCornerRadius(dp(radius)); return d;
    }
}
