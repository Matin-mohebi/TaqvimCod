package com.taqvimcod.calendar;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.util.Linkify;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

/** Dialogs: event form, settings (incl. security), about & contact. */
public final class UiDialogs {

    private UiDialogs() {}

    private static LinearLayout box(Context c, Theme.Pal pal) {
        LinearLayout b = new LinearLayout(c);
        b.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(c, 22);
        b.setPadding(pad, pad, pad, dp(c, 8));
        b.setBackgroundColor(pal.surface);
        return b;
    }

    private static TextView label(Context c, Theme.Pal pal, String text, float sp, int color, boolean bold) {
        TextView tv = new TextView(c);
        tv.setText(text);
        tv.setTextSize(sp);
        tv.setTextColor(color);
        tv.setTypeface(MonthView.Font.get(c, bold ? MonthView.Font.BOLD : MonthView.Font.REGULAR));
        tv.setLineSpacing(dp(c, 2), 1f);
        return tv;
    }

    private static int dp(Context c, int v) {
        return (int) (v * c.getResources().getDisplayMetrics().density);
    }

    private static void gap(Context c, LinearLayout box, int h) {
        View v = new View(c);
        v.setLayoutParams(new LinearLayout.LayoutParams(1, dp(c, h)));
        box.addView(v);
    }

    private static View divider(Context c, Theme.Pal pal) {
        View v = new View(c);
        v.setBackgroundColor(pal.divider);
        v.setLayoutParams(new LinearLayout.LayoutParams(-1, Math.max(1, dp(c, 1))));
        return v;
    }

    private static Context themed(MainActivity act) {
        return new android.view.ContextThemeWrapper(act,
                act.pal.dark ? android.R.style.Theme_Material : android.R.style.Theme_Material_Light);
    }

    private static Typeface reg(Context c) { return MonthView.Font.get(c, MonthView.Font.REGULAR); }
    private static Typeface bold(Context c) { return MonthView.Font.get(c, MonthView.Font.BOLD); }

    private static String version(Context c) {
        try {
            return c.getPackageManager().getPackageInfo(c.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "1.1.0";
        }
    }

    // ================= event form =================
    public static void eventForm(MainActivity act, CalendarRepo.Ev edit) {
        Context c = act;
        Theme.Pal pal = act.pal;

        LinearLayout b = box(c, pal);
        EditText etTitle = new EditText(c);
        etTitle.setHint(S.t(c, "hint_title"));
        etTitle.setTextSize(15);
        etTitle.setTextColor(pal.text);
        etTitle.setHintTextColor(pal.textSub);
        etTitle.setTypeface(reg(c));
        if (edit != null) etTitle.setText(edit.title);
        b.addView(label(c, pal, S.t(c, "title"), 13, pal.textSub, false));
        gap(c, b, 6);
        b.addView(etTitle);
        gap(c, b, 12);

        b.addView(label(c, pal, S.t(c, "date_label"), 13, pal.textSub, false));
        gap(c, b, 4);
        Dates.JDate sel = new Dates.JDate(); sel.jy = act.selJy; sel.jm = act.selJm; sel.jd = act.selJd;
        b.addView(label(c, pal, S.fmtJDate(c, sel), 15, pal.text, true));
        gap(c, b, 12);

        CheckBox cbAll = new CheckBox(c);
        cbAll.setText(S.t(c, "all_day"));
        cbAll.setTextColor(pal.text);
        cbAll.setTextSize(14);
        cbAll.setTypeface(reg(c));
        if (edit != null) cbAll.setChecked(edit.allDay);
        b.addView(cbAll);
        gap(c, b, 6);

        final int[] sh = {9}, sm = {0}, eh = {10}, em = {0};
        if (edit != null && !edit.allDay) {
            int[] s = Dates.hourMin(edit.begin), e2 = Dates.hourMin(edit.end);
            sh[0] = s[0]; sm[0] = s[1]; eh[0] = e2[0]; em[0] = e2[1];
        }
        LinearLayout timeRow = new LinearLayout(c);
        timeRow.setOrientation(LinearLayout.HORIZONTAL);
        timeRow.setGravity(Gravity.CENTER_VERTICAL);
        TextView tvStart = chip(c, pal, "", pal.surfaceAlt, pal.text);
        TextView tvEnd = chip(c, pal, "", pal.surfaceAlt, pal.text);
        TextView tvSep = label(c, pal, "—", 13, pal.textSub, false);
        timeRow.addView(label(c, pal, S.t(c, "start_time") + ": ", 13, pal.textSub, false));
        timeRow.addView(tvStart);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.leftMargin = dp(c, 8); lp2.rightMargin = dp(c, 8);
        timeRow.addView(tvSep, lp2);
        timeRow.addView(label(c, pal, S.t(c, "end_time") + ": ", 13, pal.textSub, false));
        timeRow.addView(tvEnd);
        b.addView(timeRow);
        gap(c, b, 12);

        Runnable updTimes = () -> {
            tvStart.setText(S.d2(c, sh[0]) + ":" + S.d2(c, sm[0]));
            tvEnd.setText(S.d2(c, eh[0]) + ":" + S.d2(c, em[0]));
        };
        updTimes.run();
        View.OnClickListener pickStart = v -> new TimePickerDialog(themed(act),
                (tp, h, m) -> { sh[0] = h; sm[0] = m; updTimes.run(); }, sh[0], sm[0], true).show();
        View.OnClickListener pickEnd = v -> new TimePickerDialog(themed(act),
                (tp, h, m) -> { eh[0] = h; em[0] = m; updTimes.run(); }, eh[0], em[0], true).show();
        tvStart.setOnClickListener(pickStart);
        tvEnd.setOnClickListener(pickEnd);
        cbAll.setOnCheckedChangeListener((btn, on) ->
                timeRow.setVisibility(on ? View.GONE : View.VISIBLE));
        if (edit != null && edit.allDay) timeRow.setVisibility(View.GONE);

        // target calendar spinner
        b.addView(label(c, pal, S.t(c, "calendar_target"), 13, pal.textSub, false));
        gap(c, b, 6);
        Spinner sp = new Spinner(c);
        ArrayAdapter<String> ad = new ArrayAdapter<>(c, android.R.layout.simple_spinner_item);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        int selPos = 0;
        for (int i = 0; i < act.writableCals.size(); i++) {
            CalendarRepo.Cal cal = act.writableCals.get(i);
            ad.add(cal.name + (cal.account != null ? " (" + cal.account + ")" : ""));
            if (edit != null && edit.calendarId == cal.id) selPos = i;
        }
        sp.setAdapter(ad);
        sp.setSelection(selPos);
        b.addView(sp);
        gap(c, b, 12);

        EditText etNote = new EditText(c);
        etNote.setHint(S.t(c, "note"));
        etNote.setTextSize(14);
        etNote.setTextColor(pal.text);
        etNote.setHintTextColor(pal.textSub);
        etNote.setMinLines(1);
        etNote.setMaxLines(3);
        etNote.setTypeface(reg(c));
        if (edit != null && edit.desc != null) etNote.setText(edit.desc);
        b.addView(etNote);
        gap(c, b, 4);

        final String rrule = edit != null ? CalendarRepo.rruleOf(act, edit.eventId) : null;
        if (rrule != null) {
            gap(c, b, 4);
            b.addView(label(c, pal, S.t(c, "recurring_note"), 12, pal.red, false));
        }

        AlertDialog.Builder bd = new AlertDialog.Builder(themed(act));
        bd.setTitle(edit == null ? S.t(c, "add_event") : S.t(c, "edit_event"));
        bd.setView(b);
        bd.setPositiveButton(S.t(c, "save"), null);
        bd.setNegativeButton(S.t(c, "cancel"), null);
        AlertDialog dlg = bd.create();
        dlg.show();
        dlg.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            if (title.isEmpty()) {
                Toast.makeText(act, S.t(c, "empty_title_required"), Toast.LENGTH_SHORT).show();
                return;
            }
            long dayStart = Dates.dayStartMillis(Dates.j2d(act.selJy, act.selJm, act.selJd));
            long start, end;
            if (cbAll.isChecked()) {
                int selectedJdn = Dates.j2d(act.selJy, act.selJm, act.selJd);
                start = Dates.dayStartUtcMillis(selectedJdn);
                // DTEND is exclusive for all-day events; calculate the next date
                // rather than adding 24 hours to local midnight (DST-safe).
                end = Dates.dayStartUtcMillis(selectedJdn + 1);
            } else {
                start = dayStart + (sh[0] * 60L + sm[0]) * 60000L;
                end = dayStart + (eh[0] * 60L + em[0]) * 60000L;
                if (end <= start) {
                    Toast.makeText(act, S.t(c, "invalid_time"), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            int pos = sp.getSelectedItemPosition();
            long calId = pos >= 0 && pos < act.writableCals.size()
                    ? act.writableCals.get(pos).id : act.writableCals.get(0).id;
            hideKb(act, etTitle);
            act.saveEvent(edit, calId, title, etNote.getText().toString().trim(), start, end,
                    cbAll.isChecked());
            dlg.dismiss();
        });
    }

    private static TextView chip(Context c, Theme.Pal pal, String text, int bg, int fg) {
        TextView tv = new TextView(c);
        tv.setText(text);
        tv.setTextSize(14);
        tv.setTextColor(fg);
        tv.setTypeface(reg(c));
        tv.setPadding(dp(c, 12), dp(c, 7), dp(c, 12), dp(c, 7));
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(bg);
        gd.setCornerRadius(dp(c, 20));
        tv.setBackground(gd);
        return tv;
    }

    private static void hideKb(MainActivity act, View v) {
        try {
            InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } catch (Exception ignored) {}
    }

    private static void showLargeDialog(MainActivity act, String title, LinearLayout content, String positiveLabel) {
        Context c = act;
        ScrollView scroll = new ScrollView(c);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        scroll.setVerticalScrollBarEnabled(true);
        scroll.addView(content, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        AlertDialog dialog = new AlertDialog.Builder(themed(act))
                .setTitle(title)
                .setView(scroll)
                .setPositiveButton(positiveLabel, null)
                .create();
        dialog.show();
        if (dialog.getWindow() != null) {
            int screenWidth = c.getResources().getDisplayMetrics().widthPixels;
            int screenHeight = c.getResources().getDisplayMetrics().heightPixels;
            int width = Math.min(screenWidth - dp(c, 28), dp(c, 560));
            int height = Math.min(screenHeight - dp(c, 120), dp(c, 680));
            dialog.getWindow().setLayout(width, height);
        }
    }

    // ================= settings =================
    public static void settings(MainActivity act) {
        Context c = act;
        Theme.Pal pal = act.pal;
        LinearLayout b = box(c, pal);

        b.addView(label(c, pal, S.t(c, "theme"), 14, pal.text, true));
        gap(c, b, 6);
        RadioGroup rgTheme = new RadioGroup(c);
        String[] tKeys = {"theme_auto", "theme_light", "theme_dark"};
        int curTheme = Prefs.theme(c);
        for (int i = 0; i < 3; i++) {
            RadioButton rb = new RadioButton(c);
            rb.setId(100 + i);
            rb.setText(S.t(c, tKeys[i]));
            rb.setTextColor(pal.text);
            rb.setTextSize(14);
            rb.setTypeface(reg(c));
            rgTheme.addView(rb);
        }
        rgTheme.check(100 + curTheme);
        rgTheme.setOnCheckedChangeListener((g, id) -> {
            int m = id - 100;
            if (m != Prefs.theme(c)) { Prefs.theme(c, m); act.recreate(); }
        });
        b.addView(rgTheme);
        gap(c, b, 12);

        // ---- color palette picker (gradient circles; applies on recreate) ----
        b.addView(label(c, pal, S.t(c, "palette"), 14, pal.text, true));
        gap(c, b, 6);
        HorizontalScrollView palScroll = new HorizontalScrollView(c);
        palScroll.setHorizontalScrollBarEnabled(false);
        LinearLayout palRow = new LinearLayout(c);
        palRow.setOrientation(LinearLayout.HORIZONTAL);
        palRow.setPadding(dp(c, 2), dp(c, 2), dp(c, 2), dp(c, 2));
        int curPal = Prefs.palette(c);
        for (int i = 0; i < Theme.PALETTE_COUNT; i++) {
            final int pid = i;
            Theme.Pal sample = Theme.palette(i, pal.dark);
            GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                    new int[]{sample.gradStart, sample.gradEnd});
            gd.setShape(GradientDrawable.OVAL);
            if (i == curPal) gd.setStroke(dp(c, 3), pal.text);
            View circle = new View(c);
            circle.setBackground(gd);
            circle.setContentDescription(paletteName(c, pid));
            circle.setOnClickListener(v -> {
                if (pid != Prefs.palette(c)) { Prefs.palette(c, pid); act.recreate(); }
            });
            LinearLayout.LayoutParams lpC = new LinearLayout.LayoutParams(dp(c, 46), dp(c, 46));
            lpC.setMargins(dp(c, 5), dp(c, 2), dp(c, 5), dp(c, 2));
            palRow.addView(circle, lpC);
        }
        palScroll.addView(palRow);
        b.addView(palScroll, new LinearLayout.LayoutParams(-1, -2));
        gap(c, b, 12);

        b.addView(label(c, pal, S.t(c, "language"), 14, pal.text, true));
        gap(c, b, 6);
        RadioGroup rgLang = new RadioGroup(c);
        String curLang = Prefs.lang(c);
        String[] langs = {"fa", "en"};
        String[] langNames = {S.t(c, "persian"), S.t(c, "english")};
        for (int i = 0; i < 2; i++) {
            RadioButton rb = new RadioButton(c);
            rb.setId(200 + i);
            rb.setText(langNames[i]);
            rb.setTextColor(pal.text);
            rb.setTextSize(14);
            rb.setTypeface(reg(c));
            rgLang.addView(rb);
        }
        rgLang.check(200 + ("en".equals(curLang) ? 1 : 0));
        rgLang.setOnCheckedChangeListener((g, id) -> {
            String l = langs[id - 200];
            if (!l.equals(Prefs.lang(c))) { Prefs.lang(c, l); act.recreate(); }
        });
        b.addView(rgLang);
        gap(c, b, 12);

        b.addView(label(c, pal, S.t(c, "hijri_adjust"), 14, pal.text, true));
        gap(c, b, 6);
        Spinner spAdj = new Spinner(c);
        ArrayAdapter<String> ad = new ArrayAdapter<>(c, android.R.layout.simple_spinner_item);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        int[] vals = {-2, -1, 0, 1, 2};
        int curAdj = Prefs.hijriAdj(c), selPos = 1;
        for (int i = 0; i < vals.length; i++) {
            String disp = S.d(c, (vals[i] > 0 ? "+" : "") + vals[i]);
            if (vals[i] == -1) disp += (S.fa(c) ? " (پیشنهادی ایران)" : " (suggested for Iran)");
            ad.add(disp);
            if (vals[i] == curAdj) selPos = i;
        }
        spAdj.setAdapter(ad);
        spAdj.setSelection(selPos);
        b.addView(spAdj, new LinearLayout.LayoutParams(-2, -2));
        gap(c, b, 4);
        b.addView(label(c, pal, S.t(c, "hijri_note"), 11, pal.textSub, false));
        spAdj.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> p, View v, int pos, long id) {
                if (vals[pos] != Prefs.hijriAdj(c)) { Prefs.hijriAdj(c, vals[pos]); act.refreshAll(); }
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> p) {}
        });
        gap(c, b, 12);

        // ---- security section ----
        b.addView(divider(c, pal));
        gap(c, b, 12);
        b.addView(label(c, pal, S.t(c, "security"), 14, pal.text, true));
        gap(c, b, 2);

        LinearLayout secureRow = new LinearLayout(c);
        secureRow.setOrientation(LinearLayout.VERTICAL);
        Switch sw = new Switch(c);
        sw.setText(S.t(c, "secure_screen"));
        sw.setTextColor(pal.text);
        sw.setTextSize(14);
        sw.setTypeface(reg(c));
        sw.setChecked(Prefs.secureScreen(c));
        sw.setOnCheckedChangeListener((btn, on) -> Prefs.secureScreen(c, on));
        secureRow.addView(sw);
        secureRow.addView(label(c, pal, S.t(c, "secure_screen_note"), 11, pal.textSub, false));
        b.addView(secureRow);
        gap(c, b, 10);

        TextView privacyBtn = chip(c, pal, "🛡 " + S.t(c, "privacy_title"), pal.accentSoft, pal.accent);
        privacyBtn.setOnClickListener(v -> privacy(act));
        b.addView(privacyBtn, new LinearLayout.LayoutParams(-2, -2));
        gap(c, b, 10);

        TextView playBtn = chip(c, pal, "▶ " + S.t(c, "play_protect_title"), pal.surfaceAlt, pal.text);
        playBtn.setOnClickListener(v -> playProtectFaq(act));
        b.addView(playBtn, new LinearLayout.LayoutParams(-2, -2));
        gap(c, b, 10);

        // ---- reminder tone picker (10 bundled chimes + system default) ----
        int curTone = Prefs.reminderTone(c);
        String toneLabel = curTone == 0 ? S.t(c, "tone_default") : S.t(c, "tone_" + curTone);
        TextView toneBtn = chip(c, pal, "🔊 " + S.t(c, "reminder_tone") + ": " + toneLabel,
                pal.accentSoft, pal.accent);
        toneBtn.setOnClickListener(v -> reminderTonePicker(act));
        b.addView(toneBtn, new LinearLayout.LayoutParams(-2, -2));
        gap(c, b, 10);

        // ---- share-card wallpaper picker (12 artworks + theme gradient) ----
        int curWall = Prefs.shareWall(c);
        String wallLabel = curWall == 0 ? S.t(c, "wall_default") : S.t(c, "wall_" + curWall);
        TextView wallBtn = chip(c, pal, "🖼 " + S.t(c, "share_wall_title") + ": " + wallLabel,
                pal.accentSoft, pal.accent);
        wallBtn.setOnClickListener(v -> shareWallPicker(act));
        b.addView(wallBtn, new LinearLayout.LayoutParams(-2, -2));
        gap(c, b, 14);

        b.addView(divider(c, pal));
        gap(c, b, 12);

        TextView aboutBtn = chip(c, pal, S.t(c, "about"), pal.accentSoft, pal.accent);
        aboutBtn.setOnClickListener(v -> { UiDialogs.about(act); });
        b.addView(aboutBtn, new LinearLayout.LayoutParams(-2, -2));

        showLargeDialog(act, S.t(c, "settings"), b, S.t(c, "ok"));
    }

    // ================= reminder tone picker =================
    public static void reminderTonePicker(MainActivity act) {
        Context c = act;
        Theme.Pal pal = act.pal;
        String[] names = new String[11];
        names[0] = S.t(c, "tone_default");
        for (int i = 1; i <= 10; i++) names[i] = S.t(c, "tone_" + i);
        final MediaPlayer[] preview = {null};
        AlertDialog dlg = new AlertDialog.Builder(themed(act))
                .setTitle(S.t(c, "reminder_tone"))
                .setSingleChoiceItems(names, Prefs.reminderTone(c), (d, which) -> {
                    if (which != Prefs.reminderTone(c)) {
                        Prefs.reminderTone(c, which);
                        // rebuild the reminder notification channel with the new sound
                        PrayerReminderReceiver.refreshReminderChannel(c);
                    }
                    try {
                        if (preview[0] != null) { preview[0].release(); preview[0] = null; }
                        if (which > 0) {
                            int resId = c.getResources().getIdentifier(
                                    "tone_" + which, "raw", c.getPackageName());
                            if (resId != 0) {
                                preview[0] = MediaPlayer.create(c, resId);
                                if (preview[0] != null) preview[0].start();
                            }
                        }
                    } catch (Exception ignored) {}
                })
                .setPositiveButton(S.t(c, "ok"), null)
                .create();
        dlg.setOnDismissListener(d -> {
            try { if (preview[0] != null) { preview[0].release(); preview[0] = null; } }
            catch (Exception ignored) {}
        });
        dlg.show();
    }

    // ================= share-card wallpaper picker =================
    public static void shareWallPicker(MainActivity act) {
        Context c = act;
        String[] names = new String[13];
        names[0] = S.t(c, "wall_default");
        for (int i = 1; i <= 12; i++) names[i] = S.t(c, "wall_" + i);
        new AlertDialog.Builder(themed(act))
                .setTitle(S.t(c, "share_wall_title"))
                .setSingleChoiceItems(names, Prefs.shareWall(c), (d, which) ->
                        Prefs.shareWall(c, which))
                .setPositiveButton(S.t(c, "ok"), null)
                .show();
    }
    private static String paletteName(Context c, int id) {
        switch (id) {
            case Theme.PAL_MIDNIGHT: return S.t(c, "pal_midnight");
            case Theme.PAL_SAFFRON:  return S.t(c, "pal_saffron");
            case Theme.PAL_EMERALD:  return S.t(c, "pal_emerald");
            case Theme.PAL_ROSE:     return S.t(c, "pal_rose");
            default: return S.t(c, "pal_default");
        }
    }

    // ================= privacy & play protect =================
    public static void privacy(MainActivity act) {
        Context c = act;
        Theme.Pal pal = act.pal;
        LinearLayout b = box(c, pal);
        TextView tv = label(c, pal, S.t(c, "privacy_text"), 13, pal.text, false);
        b.addView(tv);
        gap(c, b, 12);
        b.addView(label(c, pal, "🔒 " + S.t(c, "slogan"), 12, pal.accent, true));
        new AlertDialog.Builder(themed(act))
                .setTitle(S.t(c, "privacy_title"))
                .setView(b)
                .setPositiveButton(S.t(c, "ok"), null)
                .show();
    }

    public static void playProtectFaq(MainActivity act) {
        Context c = act;
        Theme.Pal pal = act.pal;
        LinearLayout b = box(c, pal);
        b.addView(label(c, pal, S.t(c, "play_protect_text"), 13, pal.text, false));
        new AlertDialog.Builder(themed(act))
                .setTitle(S.t(c, "play_protect_title"))
                .setView(b)
                .setPositiveButton(S.t(c, "understood"), null)
                .show();
    }

    // ================= about & contact =================
    public static void about(MainActivity act) {
        Context c = act;
        Theme.Pal pal = act.pal;
        LinearLayout b = box(c, pal);

        TextView name = label(c, pal, S.t(c, "app_name"), 22, pal.accent, true);
        b.addView(name);
        gap(c, b, 2);
        b.addView(label(c, pal, S.t(c, "version") + ": " + S.d(c, version(c)), 12, pal.textSub, false));
        gap(c, b, 8);
        b.addView(label(c, pal, S.t(c, "desc_line"), 13, pal.text, false));
        gap(c, b, 12);
        b.addView(label(c, pal, "🔒 " + S.t(c, "privacy_text"), 11.5f, pal.textSub, false));
        gap(c, b, 14);

        LinearLayout devRow = new LinearLayout(c);
        devRow.setOrientation(LinearLayout.HORIZONTAL);
        devRow.addView(label(c, pal, S.t(c, "developer") + ": ", 13, pal.textSub, false));
        devRow.addView(label(c, pal, "Matin", 13, pal.text, true));
        b.addView(devRow);
        gap(c, b, 14);

        b.addView(label(c, pal, S.t(c, "contact"), 14, pal.text, true));
        gap(c, b, 8);
        b.addView(label(c, pal, "سایت‌های من — برای باز کردن هر سایت لمس کن", 12, pal.textSub, false));
        gap(c, b, 8);
        addWebsiteButton(act, b, pal, "🌐 فروشگاه Matin Zeus  •  matinzeus.ir", "https://matinzeus.ir");
        gap(c, b, 6);
        addWebsiteButton(act, b, pal, "💻 نمونه‌کارها و طراحی سایت  •  matin-wp-pro.ir", "https://matin-wp-pro.ir");
        gap(c, b, 6);
        addWebsiteButton(act, b, pal, "🚗 داینوجت زنجان  •  dyno-tuning-znj.ir", "https://dyno-tuning-znj.ir");
        gap(c, b, 6);
        addWebsiteButton(act, b, pal, "🚚 اعلام بار زنجان  •  elambar-zanjan.ir", "https://elambar-zanjan.ir");
        gap(c, b, 8);
        TextView tvTg = label(c, pal, S.t(c, "telegram") + ": t.me/matin3620",
                13, pal.accent, false);
        linkify(tvTg, Pattern.compile("t\\.me/[^\\s]+"));
        b.addView(tvTg);
        gap(c, b, 6);
        TextView tvInsta = label(c, pal, S.t(c, "instagram") + ": instagram.com/matin_web_site",
                13, pal.accent, false);
        linkify(tvInsta, Pattern.compile("instagram\\.com/[^\\s]+"));
        b.addView(tvInsta);

        showLargeDialog(act, S.t(c, "about"), b, S.t(c, "ok"));
    }

    private static void addWebsiteButton(MainActivity act, LinearLayout parent, Theme.Pal pal,
                                           String title, String url) {
        TextView button = chip(act, pal, title, pal.accentSoft, pal.accent);
        button.setClickable(true);
        button.setFocusable(true);
        button.setTextSize(14);
        button.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        button.setMinHeight(dp(act, 56));
        button.setPadding(dp(act, 18), dp(act, 14), dp(act, 18), dp(act, 14));
        button.setContentDescription(title + "؛ باز کردن در مرورگر");
        button.setOnClickListener(v -> openWebsite(act, url));
        LinearLayout.LayoutParams websiteLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        websiteLp.bottomMargin = dp(act, 4);
        parent.addView(button, websiteLp);
    }

    private static void openWebsite(Context context, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "مرورگری برای باز کردن سایت پیدا نشد.", Toast.LENGTH_LONG).show();
        }
    }

    private static void linkify(TextView tv, Pattern p) {
        try {
            Linkify.addLinks(tv, p, "https://", null, (match, url) -> {
                String target = match.group();
                if (target.startsWith("http://") || target.startsWith("https://")) return target;
                return "https://" + target;
            });
            tv.setLinksClickable(true);
            // TextView links inside AlertDialog need a movement method to receive taps.
            tv.setMovementMethod(LinkMovementMethod.getInstance());
            tv.setLinkTextColor(tv.getPaint().getColor());
        } catch (Exception ignored) {}
    }
}
