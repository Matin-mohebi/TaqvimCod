package com.taqvimcod.calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

/** Offline, date-scoped daily planner with JSON backup/restore via SAF. Tasks remain on this device only. */
public final class DailyPlannerActivity extends Activity {
    private Theme.Pal pal;
    private LinearLayout list;
    private TextView summary;
    private JSONArray tasks;
    private String storageKey;

    private static final int REQ_EXPORT = 7101;
    private static final int REQ_IMPORT = 7102;
    private static final String PLANNER_FILE = "daily_planner";
    private static final String KEY_PREFIX = "daily_planner_";
    private static final String MIME_JSON = "application/json";

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        pal = Theme.get(this);
        getWindow().setStatusBarColor(pal.gradStart);
        getWindow().setNavigationBarColor(pal.bg);
        storageKey = "daily_planner_" + new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date());
        tasks = loadTasks();

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(pal.bg);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(16), dp(18), dp(28));
        root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        scroll.addView(root);
        setContentView(scroll);

        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);
        TextView back = label("‹", 28, pal.onGrad, true);
        back.setGravity(Gravity.CENTER);
        back.setBackground(round(pal.gradEnd, 16));
        top.addView(back, new LinearLayout.LayoutParams(dp(46), dp(46)));
        back.setOnClickListener(v -> finish());
        LinearLayout title = new LinearLayout(this);
        title.setOrientation(LinearLayout.VERTICAL);
        title.setPadding(dp(12), 0, 0, 0);
        title.addView(label("برنامه‌ریز روزانه", 21, pal.text, true));
        title.addView(label(new SimpleDateFormat("EEEE، d MMMM yyyy", new Locale("fa", "IR")).format(new Date()), 12, pal.textSub, false));
        top.addView(title, new LinearLayout.LayoutParams(0, -2, 1f));
        root.addView(top);

        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setPadding(dp(20), dp(20), dp(20), dp(20));
        hero.setBackground(gradient());
        hero.setElevation(dp(7));
        hero.setRotationX(3f);
        hero.setAlpha(0f);
        hero.setTranslationY(dp(12));
        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(-1, -2);
        hp.topMargin = dp(18);
        root.addView(hero, hp);
        hero.animate().alpha(1f).translationY(0).rotationX(0).setDuration(420).start();
        hero.addView(label("✦  روزت رو هدفمند بساز", 20, pal.onGrad, true));
        hero.addView(label("کارها را ثبت کن، انجام‌شده‌ها را علامت بزن؛ همه‌چیز آفلاین می‌ماند.", 12, pal.onGradSub, false));
        summary = label("", 13, pal.onGrad, true);
        summary.setPadding(0, dp(12), 0, 0);
        hero.addView(summary);

        TextView add = label("＋  افزودن کار جدید", 15, pal.onGrad, true);
        add.setGravity(Gravity.CENTER);
        add.setPadding(dp(16), dp(15), dp(16), dp(15));
        add.setBackground(round(pal.accent, 17));
        add.setElevation(dp(3));
        LinearLayout.LayoutParams ap = new LinearLayout.LayoutParams(-1, -2);
        ap.topMargin = dp(16);
        root.addView(add, ap);
        add.setOnClickListener(v -> promptTask());

        TextView heading = label("فهرست کارهای امروز", 17, pal.text, true);
        LinearLayout.LayoutParams sh = new LinearLayout.LayoutParams(-1, -2);
        sh.topMargin = dp(24);
        sh.bottomMargin = dp(8);
        root.addView(heading, sh);
        list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        root.addView(list);
        renderTasks();
        addBackupRow(root);
        TextView tip = label("نکته: برای حذف یک کار، روی آن چند لحظه نگه دار. با شروع روز جدید، فهرست همان روز جداگانه ساخته می‌شود.", 12, pal.textSub, false);
        tip.setPadding(dp(3), dp(18), dp(3), 0);
        root.addView(tip);
        TextView backupCaption = label(S.t(this, "backup_caption"), 11.5f, pal.textSub, false);
        backupCaption.setPadding(dp(3), dp(10), dp(3), 0);
        root.addView(backupCaption);
    }

    private JSONArray loadTasks() {
        String raw = getSharedPreferences(PLANNER_FILE, MODE_PRIVATE).getString(storageKey, "[]");
        try { return new JSONArray(raw); } catch (Exception ignored) { return new JSONArray(); }
    }

    private void saveTasks() {
        getSharedPreferences(PLANNER_FILE, MODE_PRIVATE).edit().putString(storageKey, tasks.toString()).apply();
    }

    private void promptTask() {
        EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setHint("مثلاً: تمرین برنامه‌نویسی");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        input.setPadding(dp(14), dp(12), dp(14), dp(12));
        new AlertDialog.Builder(this)
                .setTitle("کار جدید")
                .setView(input)
                .setNegativeButton("انصراف", null)
                .setPositiveButton("افزودن", (dialog, which) -> {
                    String value = input.getText().toString().trim();
                    if (value.isEmpty()) {
                        Toast.makeText(this, "عنوان کار را وارد کن.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (value.length() > 120) value = value.substring(0, 120);
                    JSONObject item = new JSONObject();
                    try { item.put("title", value); item.put("done", false); tasks.put(item); }
                    catch (Exception ignored) {}
                    saveTasks();
                    renderTasks();
                }).show();
    }

    private void renderTasks() {
        list.removeAllViews();
        int done = 0;
        for (int i = 0; i < tasks.length(); i++) if (tasks.optJSONObject(i) != null && tasks.optJSONObject(i).optBoolean("done")) done++;
        summary.setText(tasks.length() == 0 ? "هنوز کاری ثبت نکردی" : done + " از " + tasks.length() + " کار انجام شده");
        if (tasks.length() == 0) {
            LinearLayout empty = new LinearLayout(this);
            empty.setGravity(Gravity.CENTER);
            empty.setOrientation(LinearLayout.VERTICAL);
            empty.setPadding(dp(18), dp(26), dp(18), dp(26));
            empty.setBackground(round(pal.surface, 18));
            empty.addView(label("☀", 31, pal.accent, true));
            empty.addView(label("فهرستت خالیه؛ با یک کار کوچک شروع کن.", 13, pal.textSub, false));
            list.addView(empty);
            return;
        }
        for (int i = 0; i < tasks.length(); i++) {
            final int index = i;
            JSONObject item = tasks.optJSONObject(i);
            if (item == null) continue;
            LinearLayout card = new LinearLayout(this);
            card.setGravity(Gravity.CENTER_VERTICAL);
            card.setPadding(dp(10), dp(9), dp(10), dp(9));
            card.setBackground(round(pal.surface, 16));
            card.setElevation(dp(2));
            CheckBox check = new CheckBox(this);
            check.setButtonTintList(android.content.res.ColorStateList.valueOf(pal.accent));
            check.setChecked(item.optBoolean("done", false));
            card.addView(check, new LinearLayout.LayoutParams(dp(42), dp(48)));
            TextView title = label(item.optString("title", "کار بدون عنوان"), 14, pal.text, true);
            title.setPadding(dp(7), dp(8), dp(7), dp(8));
            if (item.optBoolean("done", false)) {
                title.setPaintFlags(title.getPaintFlags() | 0x10);
                title.setAlpha(0.62f);
            }
            card.addView(title, new LinearLayout.LayoutParams(0, -2, 1f));
            TextView more = label("⋮", 24, pal.textSub, true);
            more.setGravity(Gravity.CENTER);
            card.addView(more, new LinearLayout.LayoutParams(dp(34), dp(42)));
            check.setOnCheckedChangeListener((button, checked) -> {
                JSONObject current = tasks.optJSONObject(index);
                if (current != null) {
                    try { current.put("done", checked); } catch (Exception ignored) {}
                    saveTasks();
                    renderTasks();
                }
            });
            View.OnLongClickListener remove = v -> {
                new AlertDialog.Builder(this).setTitle("حذف کار؟")
                        .setMessage(item.optString("title"))
                        .setNegativeButton("لغو", null)
                        .setPositiveButton("حذف", (d, w) -> {
                            JSONArray next = new JSONArray();
                            for (int k = 0; k < tasks.length(); k++) if (k != index) next.put(tasks.opt(k));
                            tasks = next; saveTasks(); renderTasks();
                        }).show();
                return true;
            };
            card.setOnLongClickListener(remove);
            more.setOnClickListener(v -> remove.onLongClick(v));
            title.setOnLongClickListener(remove);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
            lp.bottomMargin = dp(8);
            list.addView(card, lp);
            card.setAlpha(0f);
            card.setTranslationY(dp(7));
            card.animate().alpha(1f).translationY(0).setStartDelay(25L * i).setDuration(220).start();
        }
    }

    // ---------------- backup / restore (SAF) ----------------

    private void addBackupRow(LinearLayout root) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER);
        TextView export = label("⬆ " + S.t(this, "backup_export"), 13.5f, pal.onAccent, true);
        export.setGravity(Gravity.CENTER);
        export.setPadding(dp(16), dp(12), dp(16), dp(12));
        export.setBackground(round(pal.accent, 15));
        export.setElevation(dp(2));
        export.setOnClickListener(v -> exportBackup());
        LinearLayout.LayoutParams lpE = new LinearLayout.LayoutParams(0, -2, 1f);
        lpE.leftMargin = dp(4);
        row.addView(export, lpE);
        TextView imp = label("⬇ " + S.t(this, "backup_import"), 13.5f, pal.accent, true);
        imp.setGravity(Gravity.CENTER);
        imp.setPadding(dp(16), dp(12), dp(16), dp(12));
        imp.setBackground(round(pal.accentSoft, 15));
        imp.setOnClickListener(v -> importBackup());
        LinearLayout.LayoutParams lpI = new LinearLayout.LayoutParams(0, -2, 1f);
        lpI.leftMargin = dp(10);
        row.addView(imp, lpI);
        LinearLayout.LayoutParams lpRow = new LinearLayout.LayoutParams(-1, -2);
        lpRow.topMargin = dp(18);
        root.addView(row, lpRow);
    }

    /** all planner days as a validated JSON document */
    private String buildBackupJson() throws Exception {
        JSONObject days = new JSONObject();
        android.content.SharedPreferences sp = getSharedPreferences(PLANNER_FILE, MODE_PRIVATE);
        for (java.util.Map.Entry<String, ?> e : sp.getAll().entrySet()) {
            if (!e.getKey().startsWith(KEY_PREFIX)) continue;
            Object v = e.getValue();
            if (!(v instanceof String)) continue;
            JSONArray arr = new JSONArray((String) v); // throws on corrupt entry -> rejected
            // shape-check every task before it lands in the backup
            JSONArray clean = new JSONArray();
            for (int i = 0; i < arr.length(); i++) {
                JSONObject t = arr.optJSONObject(i);
                if (t == null) continue;
                JSONObject item = new JSONObject();
                item.put("title", t.optString("title", ""));
                item.put("done", t.optBoolean("done", false));
                clean.put(item);
            }
            days.put(e.getKey(), clean);
        }
        JSONObject root = new JSONObject();
        root.put("app", "taqvimcod");
        root.put("type", "daily_planner");
        root.put("version", 1);
        root.put("exported", new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(new Date()));
        root.put("days", days);
        return root.toString();
    }

    private void exportBackup() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(MIME_JSON);
        intent.putExtra(Intent.EXTRA_TITLE, "taqvim-planner-backup.json");
        try {
            startActivityForResult(intent, REQ_EXPORT);
        } catch (Exception ignored) {
            Toast.makeText(this, S.t(this, "backup_failed"), Toast.LENGTH_LONG).show();
        }
    }

    private void importBackup() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(MIME_JSON);
        try {
            startActivityForResult(intent, REQ_IMPORT);
        } catch (Exception ignored) {
            Toast.makeText(this, S.t(this, "backup_failed"), Toast.LENGTH_LONG).show();
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null || data.getData() == null) return;
        Uri uri = data.getData();
        if (requestCode == REQ_EXPORT) {
            try (OutputStream os = getContentResolver().openOutputStream(uri)) {
                if (os == null) throw new IllegalStateException("no stream");
                os.write(buildBackupJson().getBytes(java.nio.charset.StandardCharsets.UTF_8));
                os.flush();
                Toast.makeText(this, S.t(this, "backup_done"), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, S.t(this, "backup_failed"), Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQ_IMPORT) {
            restoreFrom(uri);
        }
    }

    /** strict validation: unknown/corrupt shapes are rejected with a toast only */
    private void restoreFrom(Uri uri) {
        try {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader r = new BufferedReader(new java.io.InputStreamReader(
                    getContentResolver().openInputStream(uri), java.nio.charset.StandardCharsets.UTF_8))) {
                String line;
                while ((line = r.readLine()) != null) sb.append(line).append('\n');
            }
            JSONObject root = new JSONObject(sb.toString());
            if (!"taqvimcod".equals(root.optString("app"))
                    || !"daily_planner".equals(root.optString("type")))
                throw new IllegalArgumentException("not a taqvim planner backup");
            JSONObject days = root.optJSONObject("days");
            if (days == null) throw new IllegalArgumentException("missing days");
            android.content.SharedPreferences sp = getSharedPreferences(PLANNER_FILE, MODE_PRIVATE);
            android.content.SharedPreferences.Editor ed = sp.edit();
            int restored = 0;
            for (Iterator<String> it = days.keys(); it.hasNext(); ) {
                String key = it.next();
                if (!key.startsWith(KEY_PREFIX)) continue;
                JSONArray arr = days.optJSONArray(key);
                if (arr == null) throw new IllegalArgumentException("bad day array: " + key);
                JSONArray existing = new JSONArray(sp.getString(key, "[]"));
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject t = arr.optJSONObject(i);
                    if (t == null) throw new IllegalArgumentException("bad task in " + key);
                    String title = t.optString("title", "");
                    if (title.trim().isEmpty()) throw new IllegalArgumentException("empty title");
                    if (title.length() > 120) title = title.substring(0, 120);
                    boolean already = false;
                    for (int k = 0; k < existing.length(); k++) {
                        JSONObject cur = existing.optJSONObject(k);
                        if (cur != null && title.equals(cur.optString("title", "\u0000"))) {
                            already = true; break;
                        }
                    }
                    if (!already) {
                        JSONObject item = new JSONObject();
                        item.put("title", title);
                        item.put("done", t.optBoolean("done", false));
                        existing.put(item);
                        restored++;
                    }
                }
                ed.putString(key, existing.toString());
            }
            ed.apply();
            tasks = loadTasks();
            renderTasks();
            Toast.makeText(this,
                    S.t(this, "backup_imported").replace("%s", S.d(this, restored)),
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            // silent reject: nothing was written, user just gets a toast
            Toast.makeText(this, S.t(this, "backup_invalid"), Toast.LENGTH_LONG).show();
        }
    }

    private TextView label(String value, float size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value); t.setTextSize(size); t.setTextColor(color);
        t.setTypeface(Typeface.create("sans-serif", bold ? Typeface.BOLD : Typeface.NORMAL));
        return t;
    }
    private GradientDrawable round(int color, int radius) {
        GradientDrawable d = new GradientDrawable(); d.setColor(color); d.setCornerRadius(dp(radius)); return d;
    }
    private GradientDrawable gradient() {
        GradientDrawable d = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{pal.gradStart, pal.gradEnd, pal.accent});
        d.setCornerRadius(dp(24)); return d;
    }
    private int dp(float v) { return (int) (v * getResources().getDisplayMetrics().density); }
}
