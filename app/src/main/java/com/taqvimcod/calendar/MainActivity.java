package com.taqvimcod.calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.database.ContentObserver;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main screen: Persian month grid + live Google Calendar events (two-way).
 */
public class MainActivity extends Activity {

    // state shared with UiDialogs (same package)
    public Theme.Pal pal;
    public boolean fa;
    public List<CalendarRepo.Cal> writableCals = new ArrayList<>();
    public int selJy, selJm, selJd;

    private int curJy, curJm;
    private Dates.JDate today;

    private TextView tvTitle, tvTodayBtn, tvSettingsBtn, tvMonthYear, tvPrev, tvNext,
            tvDateMain, tvDateSub, tvCountdown, tvEventsTitle, tvSyncBadge;
    private LinearLayout chipsRow, panel;
    private MonthView month;
    private SparseArray<MonthView.DayCell> lastCells = new SparseArray<>();

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
        if (!CalendarRepo.hasPerms(this)) CalendarRepo.request(this);
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

    // ---------------- UI ----------------

    private int dp(int v) { return (int) (v * getResources().getDisplayMetrics().density); }

    private Typeface font(String which) { return MonthView.Font.get(this, which); }

    private GradientDrawable round(int color, int radiusDp) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
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

    private void buildUi() {
        Window w = getWindow();
        w.setStatusBarColor(pal.surface);
        w.setNavigationBarColor(pal.surface);
        View decor = w.getDecorView();
        int vis = decor.getSystemUiVisibility();
        if (!pal.dark) vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        else vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        decor.setSystemUiVisibility(vis);

        FrameLayout rootF = new FrameLayout(this);
        rootF.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        rootF.setBackgroundColor(pal.bg);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // ---- top bar ----
        LinearLayout topBar = new LinearLayout(this);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setGravity(Gravity.CENTER_VERTICAL);
        topBar.setPadding(dp(18), dp(12), dp(18), dp(6));
        tvTitle = text(S.t(this, "app_name"), 20, pal.accent, Font.SEMIBOLD, true);
        topBar.addView(tvTitle);
        TextView spacer = new TextView(this);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(0, 1, 1f));
        topBar.addView(spacer);
        tvTodayBtn = text(S.t(this, "today"), 13, pal.accent, Font.MEDIUM, false);
        tvTodayBtn.setPadding(dp(14), dp(7), dp(14), dp(7));
        tvTodayBtn.setBackground(ripple(pal.accentSoft, 20));
        tvTodayBtn.setOnClickListener(v -> { resetToToday(); refreshAll(); });
        LinearLayout.LayoutParams lpToday = new LinearLayout.LayoutParams(-2, -2);
        lpToday.leftMargin = dp(8);
        topBar.addView(tvTodayBtn, lpToday);
        tvSettingsBtn = text(S.t(this, "settings"), 13, pal.textSub, Font.MEDIUM, false);
        tvSettingsBtn.setPadding(dp(14), dp(7), dp(14), dp(7));
        tvSettingsBtn.setBackground(ripple(pal.surfaceAlt, 20));
        tvSettingsBtn.setOnClickListener(v -> UiDialogs.settings(this));
        LinearLayout.LayoutParams lpSet = new LinearLayout.LayoutParams(-2, -2);
        lpSet.leftMargin = dp(6);
        topBar.addView(tvSettingsBtn, lpSet);
        content.addView(topBar);

        // ---- header card ----
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(dp(14), dp(12), dp(14), dp(14));
        LinearLayout.LayoutParams lpHeader = new LinearLayout.LayoutParams(-1, -2);
        lpHeader.setMargins(dp(12), dp(4), dp(12), dp(6));
        header.setBackground(round(pal.surface, 18));
        content.addView(header, lpHeader);

        LinearLayout navRow = new LinearLayout(this);
        navRow.setOrientation(LinearLayout.HORIZONTAL);
        navRow.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        navRow.setGravity(Gravity.CENTER_VERTICAL);
        tvPrev = text("‹", 26, pal.accent, Font.BOLD, true);
        tvPrev.setPadding(dp(16), dp(2), dp(16), dp(2));
        tvPrev.setBackground(ripple(0x00000000, 20));
        tvPrev.setOnClickListener(v -> changeMonth(false));
        navRow.addView(tvPrev);
        tvMonthYear = text("", 20, pal.text, Font.SEMIBOLD, true);
        tvMonthYear.setGravity(Gravity.CENTER);
        navRow.addView(tvMonthYear, new LinearLayout.LayoutParams(0, -2, 1f));
        tvNext = text("›", 26, pal.accent, Font.BOLD, true);
        tvNext.setPadding(dp(16), dp(2), dp(16), dp(2));
        tvNext.setBackground(ripple(0x00000000, 20));
        tvNext.setOnClickListener(v -> changeMonth(true));
        navRow.addView(tvNext);
        header.addView(navRow);

        tvDateMain = text("", 16, pal.text, Font.SEMIBOLD, true);
        header.addView(tvDateMain, new LinearLayout.LayoutParams(-2, -2));
        LinearLayout.LayoutParams lpSub = new LinearLayout.LayoutParams(-2, -2);
        lpSub.topMargin = dp(2);
        tvDateSub = text("", 12.5f, pal.textSub, Font.REGULAR, false);
        header.addView(tvDateSub, lpSub);

        chipsRow = new LinearLayout(this);
        chipsRow.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams lpChips = new LinearLayout.LayoutParams(-2, -2);
        lpChips.topMargin = dp(8);
        header.addView(chipsRow, lpChips);

        tvCountdown = text("", 12, pal.accent, Font.MEDIUM, false);
        tvCountdown.setPadding(dp(12), dp(6), dp(12), dp(6));
        tvCountdown.setBackground(round(pal.accentSoft, 16));
        LinearLayout.LayoutParams lpCd = new LinearLayout.LayoutParams(-2, -2);
        lpCd.topMargin = dp(8);
        header.addView(tvCountdown, lpCd);

        // ---- month view ----
        month = new MonthView(this);
        month.setListener(new MonthView.Listener() {
            @Override public void onDayTap(int jd) { selectDay(jd); }
            @Override public void onSwipe(boolean next) { changeMonth(next); }
        });
        LinearLayout.LayoutParams lpMonth = new LinearLayout.LayoutParams(-1, -2);
        lpMonth.setMargins(dp(6), dp(4), dp(6), 0);
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
        tvSyncBadge = text("● " + S.t(this, "sync_active"), 10.5f, pal.green, Font.REGULAR, false);
        syncRow.addView(tvSyncBadge);
        content.addView(syncRow);

        // ---- events panel ----
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(12), dp(2), dp(12), dp(96));
        scroll.addView(panel);
        content.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1f));

        // ---- FAB ----
        TextView fab = text("+", 26, pal.onAccent, Font.BOLD, true);
        fab.setGravity(Gravity.CENTER);
        fab.setBackground(ripple(pal.accent, 28));
        fab.setElevation(dp(6));
        FrameLayout.LayoutParams lpFab = new FrameLayout.LayoutParams(dp(56), dp(56),
                Gravity.BOTTOM | Gravity.LEFT);
        lpFab.leftMargin = dp(20);
        lpFab.bottomMargin = dp(24);
        fab.setOnClickListener(v -> onFab());
        rootF.addView(content);
        rootF.addView(fab, lpFab);

        setContentView(rootF);
    }

    // ---------------- actions ----------------

    private void onFab() {
        if (!CalendarRepo.hasPerms(this)) {
            CalendarRepo.request(this);
            return;
        }
        if (writableCals.isEmpty()) {
            Toast.makeText(this, S.t(this, "no_writable_cal"), Toast.LENGTH_LONG).show();
            return;
        }
        UiDialogs.eventForm(this, null);
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
        exec.execute(this::doRefresh);
    }

    private void doRefresh() {
        boolean perms = CalendarRepo.hasPerms(this);
        writableCals = perms ? CalendarRepo.writableCalendars(this) : new ArrayList<>();
        SparseArray<MonthView.DayCell> cells = new SparseArray<>();
        int adj = Prefs.hijriAdj(this);
        int len = Dates.jalaaliMonthLength(curJy, curJm);
        if (perms) {
            long from = Dates.dayStartMillis(Dates.j2d(curJy, curJm, 1));
            long to = Dates.dayStartMillis(Dates.j2d(curJy, curJm, len)) + 86400000L;
            List<CalendarRepo.Ev> evs = CalendarRepo.instances(this, from, to);
            for (CalendarRepo.Ev ev : evs) {
                int b = Dates.jdnOfMillis(ev.begin);
                long endM = ev.end > 0 ? ev.end - 1 : ev.begin;
                int e = Dates.jdnOfMillis(Math.max(endM, ev.begin));
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
        ui.post(() -> {
            today = todayNow;
            tvMonthYear.setText(S.fmtMonthYear(this, curJy, curJm));
            int[] tdy = {today.jy, today.jm, today.jd};
            month.setup(curJy, curJm, selJd, tdy, cells, pal, fa);
            updateHeaderInfo();
            updateCountdown();
            tvSyncBadge.setVisibility(perms ? View.VISIBLE : View.GONE);
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
        chipsRow.removeAllViews();
        List<Holidays.Ev> evs = Holidays.forDay(selJm, selJd, h.hm, h.hd);
        for (Holidays.Ev ev : evs) {
            TextView chip = new TextView(this);
            chip.setText((ev.holiday ? "◼ " : "◇ ") + ev.name(fa));
            chip.setTextSize(11.5f);
            chip.setTypeface(font(Font.MEDIUM));
            chip.setTextColor(ev.holiday ? pal.red : pal.textSub);
            chip.setPadding(dp(10), dp(5), dp(10), dp(5));
            chip.setBackground(round(ev.holiday ? pal.redSoft : pal.surfaceAlt, 16));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2);
            lp.rightMargin = dp(6);
            chipsRow.addView(chip, lp);
        }
        chipsRow.setVisibility(chipsRow.getChildCount() == 0 ? View.GONE : View.VISIBLE);
    }

    private void updateCountdown() {
        Object[] nxt = Holidays.nextHoliday(today.jy, today.jm, today.jd, Prefs.hijriAdj(this), 366);
        String txt;
        if (nxt == null) {
            txt = S.t(this, "no_holiday_soon");
        } else {
            int days = (Integer) nxt[0];
            Holidays.Ev ev = (Holidays.Ev) nxt[1];
            txt = days == 0
                    ? S.t(this, "today_occasion").replace("%s", ev.name(fa))
                    : S.d(this, days) + " " + (fa ? "روز تا " : "days until ") + ev.name(fa);
        }
        tvCountdown.setText(txt);
    }

    private void loadDay() {
        final int jy = selJy, jm = selJm, jd = selJd;
        exec.execute(() -> {
            boolean perms = CalendarRepo.hasPerms(this);
            List<CalendarRepo.Ev> day = new ArrayList<>();
            if (perms) {
                long from = Dates.dayStartMillis(Dates.j2d(jy, jm, jd));
                List<CalendarRepo.Ev> evs = CalendarRepo.instances(this, from, from + 86400000L);
                int jdn = Dates.j2d(jy, jm, jd);
                for (CalendarRepo.Ev ev : evs) {
                    int b = Dates.jdnOfMillis(ev.begin);
                    long endM = ev.end > 0 ? ev.end - 1 : ev.begin;
                    int e = Dates.jdnOfMillis(Math.max(endM, ev.begin));
                    if (jdn >= b && jdn <= e) day.add(ev);
                }
            }
            List<CalendarRepo.Ev> fday = day;
            ui.post(() -> buildPanel(fday));
        });
    }

    // ---------------- panel ----------------

    private void buildPanel(List<CalendarRepo.Ev> events) {
        panel.removeAllViews();
        if (!CalendarRepo.hasPerms(this)) {
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
            empty.setPadding(dp(8), dp(28), dp(8), dp(28));
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
        box.setBackground(round(pal.redSoft, 16));
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
        grant.setOnClickListener(v -> CalendarRepo.request(this));
        TextView os = text(S.t(this, "open_settings"), 12.5f, pal.accent, Font.MEDIUM, false);
        os.setPadding(dp(14), dp(8), dp(14), dp(8));
        os.setBackground(ripple(pal.surfaceAlt, 18));
        LinearLayout.LayoutParams lpOs = new LinearLayout.LayoutParams(-2, -2);
        lpOs.rightMargin = dp(8);
        row.addView(grant);
        row.addView(os, lpOs);
        os.setOnClickListener(v -> {
            try {
                startActivity(new android.content.Intent(
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
        card.setPadding(dp(10), dp(10), dp(10), dp(10));
        card.setBackground(round(pal.surface, 14));
        LinearLayout.LayoutParams lpCard = new LinearLayout.LayoutParams(-1, -2);
        lpCard.bottomMargin = dp(8);

        View strip = new View(this);
        GradientDrawable stripBg = new GradientDrawable();
        stripBg.setColor(ev.calColor);
        stripBg.setCornerRadius(dp(2));
        strip.setBackground(stripBg);
        card.addView(strip, new LinearLayout.LayoutParams(dp(4), ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout inner = new LinearLayout(this);
        inner.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lpInner = new LinearLayout.LayoutParams(0, -2, 1f);
        lpInner.leftMargin = dp(10);

        TextView title = text(ev.title, 14.5f, pal.text, Font.SEMIBOLD, true);
        inner.addView(title, new LinearLayout.LayoutParams(-2, -2));

        String time = ev.allDay
                ? S.t(this, "all_day_label")
                : (ev.end > ev.begin
                    ? S.t(this, "time_range").replace("%s", S.fmtTime(this, ev.begin)).replace("%s", S.fmtTime(this, ev.end))
                    : S.fmtTime(this, ev.begin));
        TextView tvTime = text(time, 11.5f, pal.textSub, Font.REGULAR, false);
        inner.addView(tvTime, new LinearLayout.LayoutParams(-2, -2));

        String meta = ev.calName == null ? "" : ev.calName;
        if (!TextUtils.isEmpty(ev.location)) meta += (meta.isEmpty() ? "" : " • ") + ev.location;
        if (!meta.isEmpty()) {
            TextView tvMeta = text(meta, 11, pal.textSub, Font.REGULAR, false);
            tvMeta.setMaxLines(1);
            tvMeta.setEllipsize(TextUtils.TruncateAt.END);
            inner.addView(tvMeta, new LinearLayout.LayoutParams(-2, -2));
        }
        if (!TextUtils.isEmpty(ev.desc)) {
            TextView tvDesc = text(ev.desc, 11, pal.textSub, Font.REGULAR, false);
            tvDesc.setMaxLines(2);
            tvDesc.setEllipsize(TextUtils.TruncateAt.END);
            inner.addView(tvDesc, new LinearLayout.LayoutParams(-2, -2));
        }
        card.addView(inner, lpInner);

        if (ev.writable) {
            LinearLayout actions = new LinearLayout(this);
            actions.setOrientation(LinearLayout.HORIZONTAL);
            actions.setGravity(Gravity.CENTER_VERTICAL);
            TextView edit = text(S.t(this, "edit"), 11.5f, pal.accent, Font.MEDIUM, false);
            edit.setPadding(dp(10), dp(6), dp(10), dp(6));
            edit.setBackground(ripple(pal.accentSoft, 14));
            edit.setOnClickListener(v -> UiDialogs.eventForm(this, ev));
            TextView del = text(S.t(this, "delete"), 11.5f, pal.red, Font.MEDIUM, false);
            LinearLayout.LayoutParams lpDel = new LinearLayout.LayoutParams(-2, -2);
            lpDel.leftMargin = dp(6);
            del.setPadding(dp(10), dp(6), dp(10), dp(6));
            del.setBackground(ripple(pal.redSoft, 14));
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
            ui.post(() -> {
                Toast.makeText(this, ok ? S.t(this, "saved") : S.t(this, "no_writable_cal"),
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
                    ui.post(() -> {
                        if (ok) {
                            Toast.makeText(this, S.t(this, "deleted"), Toast.LENGTH_SHORT).show();
                            refreshAll();
                        }
                    });
                }))
                .setNegativeButton(S.t(this, "cancel"), null)
                .show();
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    // ---------------- lifecycle / live sync ----------------

    @Override
    protected void onResume() {
        super.onResume();
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
        if (obsRegistered) {
            try {
                getContentResolver().unregisterContentObserver(observer);
            } catch (Exception ignored) {}
            obsRegistered = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int code, String[] perms, int[] results) {
        super.onRequestPermissionsResult(code, perms, results);
        if (code == 42) refreshAll();
    }
}
