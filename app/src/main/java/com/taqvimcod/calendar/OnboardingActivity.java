package com.taqvimcod.calendar;

import android.app.Activity;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * First-launch onboarding: three swipeable pages (calendar / prayer & qibla /
 * daily planner) with a dots indicator, "بعدی" button and "رد کردن" shortcut.
 * Built entirely with programmatic views — no ViewPager2, no new dependencies.
 * Persists Prefs.onboarding_done=true on skip or finish.
 */
public class OnboardingActivity extends Activity {

    private static final int PAGE_COUNT = 3;

    private Theme.Pal pal;
    private LinearLayout pageContainer;
    private LinearLayout dotsRow;
    private TextView nextButton;
    private int page = 0;
    private GestureDetector gestures;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        pal = Theme.get(this);
        getWindow().setStatusBarColor(pal.gradStart);
        getWindow().setNavigationBarColor(pal.bg);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackground(gradient(0));
        root.setPadding(dp(24), dp(18), dp(24), dp(26));
        root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        root.setClickable(true);
        setContentView(root);

        // skip (top corner)
        LinearLayout topRow = new LinearLayout(this);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(Gravity.CENTER_VERTICAL);
        TextView title = label(S.t(this, "app_name"), 18, pal.onGrad, true);
        topRow.addView(title, new LinearLayout.LayoutParams(0, -2, 1f));
        TextView skip = label(S.t(this, "ob_skip"), 13.5f, pal.onGradSub, false);
        skip.setPadding(dp(14), dp(8), dp(14), dp(8));
        skip.setBackground(pill(0x24FFFFFF));
        skip.setOnClickListener(v -> finishOnboarding());
        topRow.addView(skip, new LinearLayout.LayoutParams(-2, -2));
        root.addView(topRow);

        // page area (swipe left/right to move)
        pageContainer = new LinearLayout(this);
        pageContainer.setOrientation(LinearLayout.VERTICAL);
        pageContainer.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams pageLp = new LinearLayout.LayoutParams(-1, 0, 1f);
        pageLp.topMargin = dp(10);
        pageLp.bottomMargin = dp(10);
        root.addView(pageContainer, pageLp);

        // dots
        dotsRow = new LinearLayout(this);
        dotsRow.setOrientation(LinearLayout.HORIZONTAL);
        dotsRow.setGravity(Gravity.CENTER);
        root.addView(dotsRow, new LinearLayout.LayoutParams(-1, -2));

        // next / start button
        nextButton = label(S.t(this, "ob_next"), 15.5f, pal.onAccent, true);
        nextButton.setGravity(Gravity.CENTER);
        nextButton.setPadding(dp(24), dp(14), dp(24), dp(14));
        nextButton.setBackground(round(pal.onGrad, 18));
        LinearLayout.LayoutParams nextLp = new LinearLayout.LayoutParams(-1, -2);
        nextLp.topMargin = dp(22);
        root.addView(nextButton, nextLp);
        nextButton.setOnClickListener(v -> {
            if (page < PAGE_COUNT - 1) showPage(page + 1);
            else finishOnboarding();
        });

        gestures = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onFling(MotionEvent e1, MotionEvent e2, float vx, float vy) {
                if (Math.abs(vx) > Math.abs(vy) * 1.2f && Math.abs(vx) > 700f) {
                    // RTL: fling right = next page, fling left = previous
                    if (vx > 0 && page > 0) showPage(page - 1);
                    else if (vx < 0 && page < PAGE_COUNT - 1) showPage(page + 1);
                    return true;
                }
                return false;
            }
        });
        root.setOnTouchListener((v, e) -> { gestures.onTouchEvent(e); return false; });

        showPage(0);
    }

    private void showPage(int index) {
        page = index;
        pageContainer.removeAllViews();

        // glyph badge
        TextView glyph = label(pageGlyph(index), 52, pal.onGrad, false);
        glyph.setGravity(Gravity.CENTER);
        glyph.setBackground(gradient(26));
        glyph.setElevation(dp(6));
        LinearLayout.LayoutParams glyphLp = new LinearLayout.LayoutParams(dp(124), dp(124));
        glyphLp.bottomMargin = dp(26);
        pageContainer.addView(glyph, glyphLp);

        TextView title = label(S.t(this, "ob" + (index + 1) + "_title"), 22, pal.onGrad, true);
        title.setGravity(Gravity.CENTER);
        pageContainer.addView(title, new LinearLayout.LayoutParams(-1, -2));

        TextView body = label(S.t(this, "ob" + (index + 1) + "_text"), 14.5f, pal.onGradSub, false);
        body.setGravity(Gravity.CENTER);
        body.setLineSpacing(dp(3), 1f);
        LinearLayout.LayoutParams bodyLp = new LinearLayout.LayoutParams(-1, -2);
        bodyLp.topMargin = dp(12);
        pageContainer.addView(body, bodyLp);

        // crossfade entrance
        pageContainer.setAlpha(0f);
        pageContainer.setTranslationY(dp(10));
        pageContainer.animate().alpha(1f).translationY(0f)
                .setDuration(230).setInterpolator(new DecelerateInterpolator()).start();

        nextButton.setText(page == PAGE_COUNT - 1 ? S.t(this, "ob_start") : S.t(this, "ob_next"));

        dotsRow.removeAllViews();
        for (int i = 0; i < PAGE_COUNT; i++) {
            View dot = new View(this);
            GradientDrawable d = new GradientDrawable();
            d.setColor(i == page ? pal.onGrad : 0x40FFFFFF);
            d.setShape(GradientDrawable.OVAL);
            dot.setBackground(d);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(i == page ? 22 : 9), dp(9));
            lp.setMargins(dp(4), 0, dp(4), 0);
            dotsRow.addView(dot, lp);
        }
    }

    private String pageGlyph(int index) {
        switch (index) {
            case 0: return "🗓";
            case 1: return "🕌";
            default: return "✅";
        }
    }

    private void finishOnboarding() {
        Prefs.onboardingDone(this, true);
        finish();
    }

    @Override public void onBackPressed() {
        // treat system back as "seen" so onboarding only shows once
        finishOnboarding();
    }

    // ---------------- helpers ----------------

    private TextView label(String s, float sp, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(sp);
        t.setTextColor(color);
        t.setTypeface(Typeface.create("sans-serif", bold ? Typeface.BOLD : Typeface.NORMAL));
        return t;
    }

    private GradientDrawable gradient(int radiusDp) {
        GradientDrawable d = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{pal.gradStart, pal.gradEnd});
        if (radiusDp > 0) d.setCornerRadius(dp(radiusDp));
        return d;
    }

    private GradientDrawable round(int color, int radiusDp) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radiusDp));
        return d;
    }

    private GradientDrawable pill(int color) {
        return round(color, 19);
    }

    private int dp(float v) { return (int) (v * getResources().getDisplayMetrics().density); }
}
