package com.taqvimcod.calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.Context;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.res.ColorStateList;
import android.database.ContentObserver;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main screen: Persian month grid + live Google Calendar events (two-way sync).
 */
public class MainActivity extends Activity {

    // state shared with UiDialogs (same package)
    public Theme.Pal pal;
    public boolean fa;
    public List<CalendarRepo.Cal> writableCals = new ArrayList<>();
    public int selJy, selJm, selJd;

    private int curJy, curJm;
    private Dates.JDate today;

    private LinearLayout barWrap, chipsRow, panel;
    private View statusSpacer;
    private TextView tvTodayBtn, tvSettingsBtn, tvMonthYear, tvPrev, tvNext,
            tvDateMain, tvDateSub, tvLiveClock, tvEventsTitle, tvSyncBadge;
    private FrameLayout countdownCard;
    private TextView tvCountDays, tvCountLabel, tvCountName;
    private MonthView month;
    private SparseArray<MonthView.DayCell> lastCells = new SparseArray<>();
    private boolean rationaleShown;
    private volatile boolean destroyed;
    private int lastClockJdn = Integer.MIN_VALUE;

    private final ExecutorService exec = Executors.newSingleThreadExecutor();
    private final Handler ui = new Handler(Looper.getMainLooper());
    private ContentObserver observer;
    private boolean obsRegistered;
    private final Runnable pendingRefresh = this::refreshAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pal = Theme.get(this);
        fa = S.fa(this);
        today = Dates.todayJ();
        if (savedInstanceState != null) {
            curJy = savedInstanceState.getInt("curJy");
            curJm = savedInstanceState.getInt("curJm");
            selJy = savedInstanceState.getInt("selJy");
            selJm = savedInstanceState.getInt("selJm");
            selJd = savedInstanceState.getInt("selJd");
            if (curJm < 1 || curJm > 12 || curJy < 1000) {
                today = Dates.todayJ();
                resetToToday();
            }
        } else {
            resetToToday();
        }
        super.onCreate(savedInstanceState);
        buildUi();
        if (!CalendarRepo.hasPerms(this)) askPermission();
    }

    private void resetToToday() {
        today = Dates.todayJ();
        curJy = today.jy; curJm = today.jm;
        selJy = today.jy; selJm = today.jm; selJd = today.jd;
    }

    @Override
    protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        out.putInt("curJy", curJy); out.putInt("curJm", curJm);
        out.putInt("selJy", selJy); out.putInt("selJm", selJm); out.putInt("selJd", selJd);
    }

    // ---------------- helpers ----------------

    private int dp(float v) { return (int) (v * getResources().getDisplayMetrics().density); }

    /** Deliver background results only while this Activity is alive. */
    private void postUi(Runnable action) {
        ui.post(() -> {
            if (!destroyed && !isFinishing()) action.run();
        });
    }

    private Typeface font(String which) { return MonthView.Font.get(this, which); }

    private GradientDrawable round(int color, int radiusDp) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadius(dp(radiusDp));
        return gd;
    }

    private GradientDrawable grad(int radiusDp) {
        GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{pal.gradStart, pal.gradEnd});
        gd.setCornerRadius(dp(radiusDp));
        return gd;
    }

    private RippleDrawable ripple(int bg, int radiusDp) {
        return new RippleDrawable(ColorStateList.valueOf(pal.ripple), round(bg, radiusDp), null);
    }

    private TextView text(String s, float sp, int color, String fontKey, boolean bold) {
        TextView tv = new TextView(this);
        tv.setText(s);
        tv.setTextSize(sp);
        tv.setTextColor(color);
        tv.setTypeface(font(bold ? Font.BOLD : (fontKey != null ? fontKey : Font.REGULAR)));
        return tv;
    }

    private static final class Font {
        static final String REGULAR = MonthView.Font.REGULAR;
        static final String MEDIUM = MonthView.Font.MEDIUM;
        static final String SEMIBOLD = MonthView.Font.SEMIBOLD;
        static final String BOLD = MonthView.Font.BOLD;
    }

    // ---------------- UI ----------------

    private void buildUi() {
        Window w = getWindow();
        try { w.setStatusBarColor(pal.gradStart); } catch (Exception ignored) {}
        try { w.setNavigationBarColor(pal.bg); } catch (Exception ignored) {}
        View decor = w.getDecorView();
        int vis = decor.getSystemUiVisibility();
        if (!pal.dark) vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        else vis &= ~(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        decor.setSystemUiVisibility(vis);
        applySecureFlag();

        FrameLayout rootF = new FrameLayout(this);
        rootF.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        rootF.setBackgroundColor(pal.bg);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // ---- gradient top bar (extends behind the status bar on Android 15+) ----
        barWrap = new LinearLayout(this);
        barWrap.setOrientation(LinearLayout.VERTICAL);
        barWrap.setBackground(grad(0));
        barWrap.setPadding(dp(16), dp(4), dp(16), dp(14));
        content.addView(barWrap);

        statusSpacer = new View(this);
        barWrap.addView(statusSpacer, new LinearLayout.LayoutParams(-1, 0));

        LinearLayout barRow = new LinearLayout(this);
        barRow.setOrientation(LinearLayout.HORIZONTAL);
        barRow.setGravity(Gravity.CENTER_VERTICAL);
        barRow.setPadding(0, dp(8), 0, 0);

        TextView logo = text("ت", 20, pal.onGrad, Font.BOLD, true);
        logo.setGravity(Gravity.CENTER);
        logo.setBackground(grad(14));
        logo.setElevation(dp(2));
        barRow.addView(logo, new LinearLayout.LayoutParams(dp(40), dp(40)));

        LinearLayout titleCol = new LinearLayout(this);
        titleCol.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lpTitle = new LinearLayout.LayoutParams(0, -2, 1f);
        lpTitle.leftMargin = dp(11);
        titleCol.addView(text(S.t(this, "app_name"), 18.5f, pal.onGrad, Font.SEMIBOLD, true));
        titleCol.addView(text(S.t(this, "subtitle"), 10.5f, pal.onGradSub, Font.REGULAR, false));
        barRow.addView(titleCol, lpTitle);

        tvTodayBtn = text(S.t(this, "today"), 12.5f, pal.onGrad, Font.MEDIUM, false);
        tvTodayBtn.setPadding(dp(13), dp(7), dp(13), dp(7));
        tvTodayBtn.setBackground(new RippleDrawable(ColorStateList.valueOf(0x33FFFFFF),
                round(0x24FFFFFF, 19), null));
        tvTodayBtn.setOnClickListener(v -> { resetToToday(); refreshAll(); });
        barRow.addView(tvTodayBtn, new LinearLayout.LayoutParams(-2, -2));

        LinearLayout.LayoutParams lpSet = new LinearLayout.LayoutParams(-2, -2);
        lpSet.leftMargin = dp(7);
        tvSettingsBtn = text("⚙", 15, pal.onGrad, Font.REGULAR, false);
        tvSettingsBtn.setContentDescription(S.t(this, "settings"));
        tvSettingsBtn.setGravity(Gravity.CENTER);
        tvSettingsBtn.setPadding(dp(9), dp(5), dp(9), dp(5));
        tvSettingsBtn.setBackground(new RippleDrawable(ColorStateList.valueOf(0x33FFFFFF),
                round(0x24FFFFFF, 19), null));
        tvSettingsBtn.setOnClickListener(v -> UiDialogs.settings(this));
        barRow.addView(tvSettingsBtn, lpSet);
        barWrap.addView(barRow);

        // ---- header card ----
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(dp(16), dp(10), dp(16), dp(14));
        LinearLayout.LayoutParams lpHeader = new LinearLayout.LayoutParams(-1, -2);
        lpHeader.setMargins(dp(12), dp(10), dp(12), dp(4));
        header.setBackground(round(pal.surface, 22));
        header.setElevation(dp(3));
        content.addView(header, lpHeader);

        LinearLayout navRow = new LinearLayout(this);
        navRow.setOrientation(LinearLayout.HORIZONTAL);
        navRow.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        navRow.setGravity(Gravity.CENTER_VERTICAL);
        tvPrev = text("‹", 26, pal.accent, Font.BOLD, true);
        tvPrev.setContentDescription(S.t(this, "previous_month"));
        tvPrev.setPadding(dp(18), dp(4), dp(18), dp(4));
        tvPrev.setBackground(ripple(0x00000000, 20));
        tvPrev.setOnClickListener(v -> changeMonth(false));
        navRow.addView(tvPrev);
        tvMonthYear = text("", 20, pal.text, Font.SEMIBOLD, true);
        tvMonthYear.setGravity(Gravity.CENTER);
        tvMonthYear.setContentDescription(fa
                ? "انتخاب ماه و سال؛ برای رفتن مستقیم به ماه دلخواه لمس کنید"
                : "Choose month and year; tap to jump to a month");
        tvMonthYear.setFocusable(true);
        tvMonthYear.setOnClickListener(v -> openMonthPicker());
        navRow.addView(tvMonthYear, new LinearLayout.LayoutParams(0, -2, 1f));
        tvNext = text("›", 26, pal.accent, Font.BOLD, true);
        tvNext.setContentDescription(S.t(this, "next_month"));
        tvNext.setPadding(dp(18), dp(4), dp(18), dp(4));
        tvNext.setBackground(ripple(0x00000000, 20));
        tvNext.setOnClickListener(v -> changeMonth(true));
        navRow.addView(tvNext);
        header.addView(navRow);

        tvDateMain = text("", 16.5f, pal.text, Font.SEMIBOLD, true);
        header.addView(tvDateMain, new LinearLayout.LayoutParams(-2, -2));
        LinearLayout.LayoutParams lpSub = new LinearLayout.LayoutParams(-2, -2);
        lpSub.topMargin = dp(2);
        tvDateSub = text("", 12, pal.textSub, Font.REGULAR, false);
        header.addView(tvDateSub, lpSub);

        tvLiveClock = text("", 18, pal.accent, Font.SEMIBOLD, true);
        tvLiveClock.setPadding(0, dp(8), 0, dp(2));
        tvLiveClock.setContentDescription(fa ? "ساعت زنده دستگاه" : "Live device clock");
        header.addView(tvLiveClock, new LinearLayout.LayoutParams(-2, -2));

        chipsRow = new LinearLayout(this);
        chipsRow.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams lpChips = new LinearLayout.LayoutParams(-2, -2);
        lpChips.topMargin = dp(9);
        header.addView(chipsRow, lpChips);

        countdownCard = countdownView();
        LinearLayout.LayoutParams lpCd = new LinearLayout.LayoutParams(-1, -2);
        lpCd.topMargin = dp(10);
        header.addView(countdownCard, lpCd);

        // ---- month view ----
        month = new MonthView(this);
        month.setListener(new MonthView.Listener() {
            @Override public void onDayTap(int jd) { selectDay(jd); }
            @Override public void onSwipe(boolean next) { changeMonth(next); }
        });
        LinearLayout.LayoutParams lpMonth = new LinearLayout.LayoutParams(-1, -2);
        lpMonth.setMargins(dp(6), dp(2), dp(6), 0);
        content.addView(month, lpMonth);

        // ---- events title row ----
        LinearLayout syncRow = new LinearLayout(this);
        syncRow.setOrientation(LinearLayout.HORIZONTAL);
        syncRow.setGravity(Gravity.CENTER_VERTICAL);
        syncRow.setPadding(dp(18), dp(8), dp(18), dp(4));
        tvEventsTitle = text(S.t(this, "events"), 15, pal.text, Font.SEMIBOLD, true);
        syncRow.addView(tvEventsTitle);
        TextView sp2 = new TextView(this);
        sp2.setLayoutParams(new LinearLayout.LayoutParams(0, 1, 1f));
        syncRow.addView(sp2);
        tvSyncBadge = text("● " + S.t(this, "sync_active"), 10, pal.green, Font.MEDIUM, false);
        tvSyncBadge.setPadding(dp(10), dp(4), dp(10), dp(4));
        tvSyncBadge.setBackground(round(pal.surfaceAlt, 14));
        syncRow.addView(tvSyncBadge);
        content.addView(syncRow);

        // ---- events panel ----
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(12), dp(2), dp(12), dp(104));
        scroll.addView(panel);
        content.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1f));

        // ---- FAB ----
        TextView fab = text("+", 26, pal.onAccent, Font.BOLD, true);
        fab.setContentDescription(S.t(this, "add_event"));
        fab.setGravity(Gravity.CENTER);
        fab.setBackground(new RippleDrawable(ColorStateList.valueOf(0x33FFFFFF),
                grad(19), null));
        fab.setElevation(dp(7));
        FrameLayout.LayoutParams lpFab = new FrameLayout.LayoutParams(dp(58), dp(58),
                Gravity.BOTTOM | Gravity.LEFT);
        lpFab.leftMargin = dp(20);
        lpFab.bottomMargin = dp(26);
        fab.setOnClickListener(v -> onFab());

        // ---- edge-to-edge (enforced on Android 15 when targetSdk >= 35) ----
        final View fabRef = fab;
        rootF.setOnApplyWindowInsetsListener((v, ins) -> {
            int top, bottom;
            if (Build.VERSION.SDK_INT >= 30) {
                android.graphics.Insets sb = ins.getInsets(WindowInsets.Type.systemBars());
                top = sb.top; bottom = sb.bottom;
            } else {
                top = ins.getSystemWindowInsetTop();
                bottom = ins.getSystemWindowInsetBottom();
            }
            LinearLayout.LayoutParams lpSp = (LinearLayout.LayoutParams) statusSpacer.getLayoutParams();
            if (lpSp.height != top) { lpSp.height = top; statusSpacer.setLayoutParams(lpSp); }
            panel.setPadding(dp(12), dp(2), dp(12), dp(104) + bottom);
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) fabRef.getLayoutParams();
            int want = dp(26) + bottom;
            if (lp.bottomMargin != want) { lp.bottomMargin = want; fabRef.setLayoutParams(lp); }
            return ins;
        });

        rootF.addView(content);
        rootF.addView(fab, lpFab);
        setContentView(rootF);
    }

    private void applySecureFlag() {
        if (Prefs.secureScreen(this)) {
            getWindow().addFlags(WindowManager_FLAG_SECURE);
        } else {
            getWindow().clearFlags(WindowManager_FLAG_SECURE);
        }
    }

    private static final int WindowManager_FLAG_SECURE = android.view.WindowManager.LayoutParams.FLAG_SECURE;

    // ---------------- countdown card ----------------

    private FrameLayout countdownView() {
        FrameLayout card = new FrameLayout(this);
        card.setBackground(round(pal.surfaceAlt, 18));
        card.setPadding(dp(12), dp(10), dp(12), dp(10));

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);

        tvCountDays = text("", 15, pal.onGrad, Font.BOLD, true);
        tvCountDays.setGravity(Gravity.CENTER);
        tvCountDays.setBackground(grad(21));
        row.addView(tvCountDays, new LinearLayout.LayoutParams(dp(42), dp(42)));

        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lpCol = new LinearLayout.LayoutParams(0, -2, 1f);
        lpCol.leftMargin = dp(11);
        tvCountLabel = text("", 10, pal.textSub, Font.MEDIUM, false);
        col.addView(tvCountLabel);
        tvCountName = text("", 13, pal.text, Font.SEMIBOLD, true);
        tvCountName.setMaxLines(1);
        tvCountName.setEllipsize(TextUtils.TruncateAt.END);
        col.addView(tvCountName);
        row.addView(col, lpCol);

        card.addView(row);
        return card;
    }

    // ---------------- actions ----------------

    private void onFab() {
        if (!CalendarRepo.hasPerms(this)) {
            askPermission();
            return;
        }
        if (writableCals.isEmpty()) {
            Toast.makeText(this, S.t(this, "no_writable_cal"), Toast.LENGTH_LONG).show();
            return;
        }
        UiDialogs.eventForm(this, null);
    }

    /** rationale dialog first (Play-policy friendly), then the system prompt */
    private void askPermission() {
        if (rationaleShown) return;
        rationaleShown = true;
        new AlertDialog.Builder(new ContextThemeWrapper(this,
                pal.dark ? android.R.style.Theme_Material : android.R.style.Theme_Material_Light))
                .setTitle(S.t(this, "perm_title"))
                .setMessage(S.t(this, "perm_rationale"))
                .setPositiveButton(S.t(this, "grant"),
                        (d, w2) -> CalendarRepo.request(this))
                .setNegativeButton(S.t(this, "cancel"), null)
                .show();
    }

    /**
     * Lets users jump directly to a Jalali month instead of repeatedly tapping
     * the previous/next controls. Uses native pickers and keeps the current UI style.
     */
    private void openMonthPicker() {
        final String[] monthNames = S.months(this);
        LinearLayout choices = new LinearLayout(this);
        choices.setOrientation(LinearLayout.HORIZONTAL);
        choices.setGravity(Gravity.CENTER);
        choices.setPadding(dp(16), dp(12), dp(16), dp(8));

        android.widget.NumberPicker monthPicker = new android.widget.NumberPicker(this);
        monthPicker.setMinValue(0);
        monthPicker.setMaxValue(monthNames.length - 1);
        monthPicker.setDisplayedValues(monthNames);
        monthPicker.setValue(curJm - 1);
        monthPicker.setWrapSelectorWheel(true);
        monthPicker.setContentDescription(fa ? "ماه شمسی" : "Jalali month");

        android.widget.NumberPicker yearPicker = new android.widget.NumberPicker(this);
        yearPicker.setMinValue(1200);
        yearPicker.setMaxValue(1600);
        yearPicker.setValue(Math.max(1200, Math.min(1600, curJy)));
        yearPicker.setWrapSelectorWheel(false);
        yearPicker.setContentDescription(fa ? "سال شمسی" : "Jalali year");

        choices.addView(monthPicker, new LinearLayout.LayoutParams(0, -2, 1.15f));
        choices.addView(yearPicker, new LinearLayout.LayoutParams(0, -2, 1f));

        new AlertDialog.Builder(new ContextThemeWrapper(this,
                pal.dark ? android.R.style.Theme_Material : android.R.style.Theme_Material_Light))
                .setTitle(fa ? "رفتن به ماه دلخواه" : "Go to month")
                .setView(choices)
                .setNegativeButton(fa ? "انصراف" : "Cancel", (d, which) -> d.dismiss())
                .setPositiveButton(fa ? "نمایش ماه" : "Show month", (d, which) -> {
                    curJy = yearPicker.getValue();
                    curJm = monthPicker.getValue() + 1;
                    selJy = curJy;
                    selJm = curJm;
                    int len = Dates.jalaaliMonthLength(curJy, curJm);
                    if (selJd < 1) selJd = 1;
                    if (selJd > len) selJd = len;
                    refreshAll();
                })
                .show();
    }

    private void changeMonth(boolean next) {
        int m = next ? curJm + 1 : curJm - 1, y = curJy;
        if (m > 12) { m = 1; y++; }
        if (m < 1) { m = 12; y--; }
        curJy = y; curJm = m;
        selJy = y; selJm = m;
        int len = Dates.jalaaliMonthLength(y, m);
        if (selJd > len) selJd = len;
        refreshAll();
    }

    private void selectDay(int jd) {
        selJd = jd; selJy = curJy; selJm = curJm;
        int[] tdy = {today.jy, today.jm, today.jd};
        month.setup(curJy, curJm, jd, tdy, lastCells, pal, fa);
        updateHeaderInfo();
        loadDay();
    }

    // ---------------- data / refresh ----------------

    public void refreshAll() {
        if (destroyed) return;
        exec.execute(this::doRefresh);
    }

    private void doRefresh() {
        boolean canRead = CalendarRepo.hasReadPerm(this);
        boolean canWrite = CalendarRepo.hasPerms(this);
        writableCals = canWrite ? CalendarRepo.writableCalendars(this) : new ArrayList<>();
        SparseArray<MonthView.DayCell> cells = new SparseArray<>();
        int adj = Prefs.hijriAdj(this);
        int len = Dates.jalaaliMonthLength(curJy, curJm);
        if (canRead) {
            long from = Dates.dayStartMillis(Dates.j2d(curJy, curJm, 1));
            long to = Dates.dayStartMillis(Dates.j2d(curJy, curJm, len)) + 86400000L;
            List<CalendarRepo.Ev> evs = CalendarRepo.instances(this, from, to);
            for (CalendarRepo.Ev ev : evs) {
                int b = ev.allDay ? Dates.jdnOfMillisUtc(ev.begin) : Dates.jdnOfMillis(ev.begin);
                long endM = ev.end > 0 ? ev.end - 1 : ev.begin;
                int e = ev.allDay ? Dates.jdnOfMillisUtc(Math.max(endM, ev.begin)) : Dates.jdnOfMillis(Math.max(endM, ev.begin));
                for (int d = b; d <= e && d - b < 62; d++) {
                    Dates.JDate jj = Dates.d2j(d);
                    if (jj.jy == curJy && jj.jm == curJm) {
                        MonthView.DayCell cell = cells.get(jj.jd);
                        if (cell == null) { cell = new MonthView.DayCell(); cells.put(jj.jd, cell); }
                        cell.hasEvent = true;
                    }
                }
            }
        }
        for (int jd = 1; jd <= len; jd++) {
            Dates.HDate h = Dates.hijriFromJdn(Dates.j2d(curJy, curJm, jd), adj);
            if (Holidays.isHoliday(curJm, jd, h.hm, h.hd)) {
                MonthView.DayCell cell = cells.get(jd);
                if (cell == null) { cell = new MonthView.DayCell(); cells.put(jd, cell); }
                cell.isHoliday = true;
            }
        }
        lastCells = cells;
        Dates.JDate todayNow = Dates.todayJ();
        postUi(() -> {
            today = todayNow;
            tvMonthYear.setText(S.fmtMonthYear(this, curJy, curJm));
            int[] tdy = {today.jy, today.jm, today.jd};
            month.setup(curJy, curJm, selJd, tdy, cells, pal, fa);
            month.setAlpha(0.25f);
            month.animate().alpha(1f).setDuration(200).start();
            updateHeaderInfo();
            updateCountdown();
            tvSyncBadge.setVisibility(canRead ? View.VISIBLE : View.GONE);
            tvSyncBadge.setText("● " + S.t(this, "sync_active"));
            loadDay();
            WidgetProvider.updateAll(this);
        });
    }

    private void updateHeaderInfo() {
        Dates.JDate sel = new Dates.JDate();
        sel.jy = selJy; sel.jm = selJm; sel.jd = selJd;
        tvDateMain.setText(S.fmtJDate(this, sel));
        int jdn = Dates.j2d(selJy, selJm, selJd);
        Dates.GDate g = Dates.d2g(jdn);
        Dates.HDate h = Dates.hijriFromJdn(jdn, Prefs.hijriAdj(this));
        tvDateSub.setText(S.fmtGDate(this, g) + "  •  " + S.fmtHDate(this, h));
        tvDateMain.setContentDescription(fa
                ? S.fmtJDate(this, sel) + "؛ برای کپی تاریخ، چند لحظه نگه دارید"
                : S.fmtJDate(this, sel) + "; long press to copy date");
        tvDateMain.setOnLongClickListener(v -> {
            String allDates = S.fmtJDate(this, sel) + "\n"
                    + S.fmtGDate(this, g) + "\n" + S.fmtHDate(this, h);
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null) {
                clipboard.setPrimaryClip(ClipData.newPlainText("Taqvim date", allDates));
                Toast.makeText(this, fa ? "هر سه تاریخ کپی شدند" : "All three dates copied",
                        Toast.LENGTH_SHORT).show();
            }
            return true;
        });
        chipsRow.removeAllViews();
        List<Holidays.Ev> evs = Holidays.forDay(selJm, selJd, h.hm, h.hd);
        for (Holidays.Ev ev : evs) {
            TextView chip = new TextView(this);
            chip.setText((ev.holiday ? "◼ " : "◇ ") + ev.name(fa));
            chip.setTextSize(11.5f);
            chip.setTypeface(font(Font.MEDIUM));
            chip.setTextColor(ev.holiday ? pal.red : pal.textSub);
            chip.setPadding(dp(11), dp(5), dp(11), dp(5));
            chip.setBackground(round(ev.holiday ? pal.redSoft : pal.surfaceAlt, 16));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2);
            lp.rightMargin = dp(6);
            lp.topMargin = dp(2);
            chipsRow.addView(chip, lp);
        }
        chipsRow.setVisibility(chipsRow.getChildCount() == 0 ? View.GONE : View.VISIBLE);
    }

    private void updateCountdown() {
        Object[] nxt = Holidays.nextHoliday(today.jy, today.jm, today.jd,
                Prefs.hijriAdj(this), 366);
        if (nxt == null) {
            tvCountDays.setText("—");
            tvCountLabel.setText(S.t(this, "next_holiday"));
            tvCountName.setText(S.t(this, "no_holiday_soon"));
            return;
        }
        int days = (Integer) nxt[0];
        Holidays.Ev ev = (Holidays.Ev) nxt[1];
        tvCountDays.setText(S.d(this, days));
        tvCountLabel.setText(days == 0
                ? S.t(this, "today_holiday")
                : S.t(this, "next_holiday") + " • " + S.d(this, days) + " " + S.t(this, "days"));
        tvCountName.setText(ev.name(fa));
        tvCountName.setTextColor(days == 0 ? pal.red : pal.text);
    }

    private void loadDay() {
        final int jy = selJy, jm = selJm, jd = selJd;
        exec.execute(() -> {
            boolean canRead = CalendarRepo.hasReadPerm(this);
            List<CalendarRepo.Ev> day = new ArrayList<>();
            if (canRead) {
                long from = Dates.dayStartMillis(Dates.j2d(jy, jm, jd));
                List<CalendarRepo.Ev> evs = CalendarRepo.instances(this, from, from + 86400000L);
                int jdn = Dates.j2d(jy, jm, jd);
                for (CalendarRepo.Ev ev : evs) {
                    int b = ev.allDay ? Dates.jdnOfMillisUtc(ev.begin) : Dates.jdnOfMillis(ev.begin);
                    long endM = ev.end > 0 ? ev.end - 1 : ev.begin;
                    int e = ev.allDay ? Dates.jdnOfMillisUtc(Math.max(endM, ev.begin)) : Dates.jdnOfMillis(Math.max(endM, ev.begin));
                    if (jdn >= b && jdn <= e) day.add(ev);
                }
            }
            List<CalendarRepo.Ev> fday = day;
            postUi(() -> buildPanel(fday));
        });
    }

    // ---------------- panel ----------------

    private void buildPanel(List<CalendarRepo.Ev> events) {
        panel.removeAllViews();
        if (!CalendarRepo.hasReadPerm(this)) {
            panel.addView(banner());
            tvEventsTitle.setText(S.t(this, "events"));
            return;
        }
        tvEventsTitle.setText(S.t(this, "events_count").replace("%s",
                S.d(this, events.size())));
        if (events.isEmpty()) {
            LinearLayout empty = new LinearLayout(this);
            empty.setOrientation(LinearLayout.VERTICAL);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(dp(8), dp(30), dp(8), dp(30));
            empty.setBackground(round(pal.surface, 18));
            empty.setAlpha(0.9f);
            TextView t1 = text(S.t(this, "no_events"), 13.5f, pal.textSub, Font.MEDIUM, false);
            t1.setGravity(Gravity.CENTER);
            TextView t2 = text(S.t(this, "no_events_hint"), 11.5f, pal.textSub, Font.REGULAR, false);
            t2.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(-2, -2);
            lp2.topMargin = dp(4);
            empty.addView(t1);
            empty.addView(t2, lp2);
            panel.addView(empty, new LinearLayout.LayoutParams(-1, -2));
            return;
        }
        for (CalendarRepo.Ev ev : events) panel.addView(eventCard(ev));
    }

    private View banner() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(16), dp(14), dp(16), dp(16));
        box.setBackground(round(pal.redSoft, 18));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.bottomMargin = dp(10);
        TextView t = text(S.t(this, "perm_title"), 14, pal.red, Font.BOLD, true);
        TextView d = text(S.t(this, "perm_text"), 12, pal.textSub, Font.REGULAR, false);
        LinearLayout.LayoutParams lpD = new LinearLayout.LayoutParams(-2, -2);
        lpD.topMargin = dp(4);
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams lpRow = new LinearLayout.LayoutParams(-2, -2);
        lpRow.topMargin = dp(10);
        TextView grant = text(S.t(this, "grant"), 12.5f, pal.onAccent, Font.MEDIUM, false);
        grant.setPadding(dp(14), dp(8), dp(14), dp(8));
        grant.setBackground(ripple(pal.accent, 18));
        grant.setOnClickListener(v -> askPermission());
        TextView os = text(S.t(this, "open_settings"), 12.5f, pal.accent, Font.MEDIUM, false);
        os.setPadding(dp(14), dp(8), dp(14), dp(8));
        os.setBackground(ripple(pal.surfaceAlt, 18));
        LinearLayout.LayoutParams lpOs = new LinearLayout.LayoutParams(-2, -2);
        lpOs.rightMargin = dp(8);
        row.addView(grant);
        row.addView(os, lpOs);
        os.setOnClickListener(v -> {
            try {
                startActivity(new Intent(
                        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getPackageName())));
            } catch (Exception ignored) {}
        });
        box.addView(t);
        box.addView(d, lpD);
        box.addView(row, lpRow);
        return box;
    }

    private View eventCard(CalendarRepo.Ev ev) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(11), dp(11), dp(11), dp(11));
        card.setBackground(round(pal.surface, 17));
        card.setElevation(dp(1.5f));
        LinearLayout.LayoutParams lpCard = new LinearLayout.LayoutParams(-1, -2);
        lpCard.bottomMargin = dp(8);

        View strip = new View(this);
        GradientDrawable stripBg = new GradientDrawable();
        stripBg.setColor(ev.calColor);
        stripBg.setCornerRadius(dp(2.5f));
        strip.setBackground(stripBg);
        card.addView(strip, new LinearLayout.LayoutParams(dp(4), dp(44)));

        LinearLayout inner = new LinearLayout(this);
        inner.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lpInner = new LinearLayout.LayoutParams(0, -2, 1f);
        lpInner.leftMargin = dp(11);

        TextView title = text(ev.title, 14, pal.text, Font.SEMIBOLD, true);
        inner.addView(title, new LinearLayout.LayoutParams(-2, -2));

        String time = ev.allDay
                ? S.t(this, "all_day_label")
                : (ev.end > ev.begin
                    ? S.t(this, "time_range").replace("%s", S.fmtTime(this, ev.begin)).replace("%s", S.fmtTime(this, ev.end))
                    : S.fmtTime(this, ev.begin));
        TextView tvTime = text(time, 11, pal.accent, Font.MEDIUM, false);
        LinearLayout.LayoutParams lpTime = new LinearLayout.LayoutParams(-2, -2);
        lpTime.topMargin = dp(1);
        inner.addView(tvTime, lpTime);

        String meta = ev.calName == null ? "" : ev.calName;
        if (!TextUtils.isEmpty(ev.location)) meta += (meta.isEmpty() ? "" : " • ") + ev.location;
        if (!meta.isEmpty()) {
            TextView tvMeta = text(meta, 10.5f, pal.textSub, Font.REGULAR, false);
            tvMeta.setMaxLines(1);
            tvMeta.setEllipsize(TextUtils.TruncateAt.END);
            inner.addView(tvMeta, new LinearLayout.LayoutParams(-2, -2));
        }
        if (!TextUtils.isEmpty(ev.desc)) {
            TextView tvDesc = text(ev.desc, 10.5f, pal.textSub, Font.REGULAR, false);
            tvDesc.setMaxLines(2);
            tvDesc.setEllipsize(TextUtils.TruncateAt.END);
            inner.addView(tvDesc, new LinearLayout.LayoutParams(-2, -2));
        }
        card.addView(inner, lpInner);

        if (ev.writable) {
            LinearLayout actions = new LinearLayout(this);
            actions.setOrientation(LinearLayout.VERTICAL);
            actions.setGravity(Gravity.CENTER);
            TextView edit = text("✎", 15, pal.accent, Font.REGULAR, false);
            edit.setContentDescription(S.t(this, "edit"));
            edit.setGravity(Gravity.CENTER);
            edit.setPadding(dp(9), dp(6), dp(9), dp(6));
            edit.setBackground(ripple(pal.accentSoft, 12));
            edit.setOnClickListener(v -> UiDialogs.eventForm(this, ev));
            TextView del = text("🗑", 13, pal.red, Font.REGULAR, false);
            del.setContentDescription(S.t(this, "delete"));
            del.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams lpDel = new LinearLayout.LayoutParams(-2, -2);
            lpDel.topMargin = dp(5);
            del.setPadding(dp(9), dp(6), dp(9), dp(6));
            del.setBackground(ripple(pal.redSoft, 12));
            del.setOnClickListener(v -> deleteEvent(ev));
            actions.addView(edit);
            actions.addView(del, lpDel);
            card.addView(actions, new LinearLayout.LayoutParams(-2, -2));
        }
        return card;
    }

    // ---------------- mutations ----------------

    public void saveEvent(CalendarRepo.Ev edit, long calId, String title, String desc,
                          long start, long end, boolean allDay) {
        exec.execute(() -> {
            boolean ok = edit == null
                    ? CalendarRepo.insert(this, calId, title, desc, start, end, allDay)
                    : CalendarRepo.update(this, edit.eventId, title, desc, start, end, allDay);
            postUi(() -> {
                Toast.makeText(this, ok ? S.t(this, "saved") : S.t(this, "save_failed"),
                        Toast.LENGTH_SHORT).show();
                if (ok) refreshAll();
            });
        });
    }

    public void deleteEvent(CalendarRepo.Ev ev) {
        new AlertDialog.Builder(new ContextThemeWrapper(this,
                pal.dark ? android.R.style.Theme_Material : android.R.style.Theme_Material_Light))
                .setTitle(S.t(this, "del_confirm_title"))
                .setMessage(S.t(this, "del_confirm_text").replace("%s", ev.title))
                .setPositiveButton(S.t(this, "delete"), (d, w) -> exec.execute(() -> {
                    boolean ok = CalendarRepo.delete(this, ev.eventId);
                    postUi(() -> {
                        if (ok) {
                            Toast.makeText(this, S.t(this, "deleted"), Toast.LENGTH_SHORT).show();
                            refreshAll();
                        }
                    });
                }))
                .setNegativeButton(S.t(this, "cancel"), null)
                .show();
    }

    // ---------------- lifecycle / live sync ----------------

    private final Runnable liveClockTick = new Runnable() {
        @Override public void run() {
            if (destroyed || isFinishing() || tvLiveClock == null) return;
            Dates.JDate clockDate = Dates.todayJ();
            int clockJdn = Dates.j2d(clockDate.jy, clockDate.jm, clockDate.jd);
            if (lastClockJdn != Integer.MIN_VALUE && clockJdn != lastClockJdn) {
                refreshAll();
            }
            lastClockJdn = clockJdn;
            Locale clockLocale = fa ? new Locale("fa", "IR") : Locale.getDefault();
            SimpleDateFormat format = new SimpleDateFormat(fa ? "HH:mm:ss" : "h:mm:ss a", clockLocale);
            tvLiveClock.setText(S.d(MainActivity.this, format.format(new Date())));
            ui.postDelayed(this, 1000L);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        applySecureFlag();
        ui.removeCallbacks(liveClockTick);
        ui.post(liveClockTick);
        if (observer == null) {
            observer = new ContentObserver(new Handler()) {
                @Override
                public void onChange(boolean selfChange) {
                    ui.removeCallbacks(pendingRefresh);
                    ui.postDelayed(pendingRefresh, 400);
                }
            };
        }
        if (!obsRegistered) {
            try {
                getContentResolver().registerContentObserver(
                        CalendarContract.Instances.CONTENT_URI, true, observer);
                obsRegistered = true;
            } catch (Exception ignored) {}
        }
        refreshAll();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ui.removeCallbacks(liveClockTick);
        if (obsRegistered) {
            try {
                getContentResolver().unregisterContentObserver(observer);
            } catch (Exception ignored) {}
            obsRegistered = false;
        }
    }

    @Override
    protected void onDestroy() {
        destroyed = true;
        ui.removeCallbacksAndMessages(null);
        if (obsRegistered) {
            try { getContentResolver().unregisterContentObserver(observer); }
            catch (Exception ignored) {}
            obsRegistered = false;
        }
        exec.shutdownNow();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int code, String[] perms, int[] results) {
        super.onRequestPermissionsResult(code, perms, results);
        if (code == 42) refreshAll();
    }
}
