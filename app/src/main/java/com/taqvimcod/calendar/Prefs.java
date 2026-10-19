package com.taqvimcod.calendar;

import android.content.Context;
import android.content.SharedPreferences;

/** Simple settings store. */
public final class Prefs {

    private static final String NAME = "taqvimkod";
    public static final String KEY_THEME = "theme";           // 0 auto / 1 light / 2 dark
    public static final String KEY_LANG = "lang";             // "fa" / "en"
    public static final String KEY_HIJRI_ADJ = "hijri_adj";   // -2..+2 (default -1, Iran)

    private Prefs() {}

    private static SharedPreferences sp(Context c) {
        return c.getApplicationContext().getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public static int theme(Context c) { return sp(c).getInt(KEY_THEME, 0); }
    public static void theme(Context c, int v) { sp(c).edit().putInt(KEY_THEME, v).apply(); }

    public static String lang(Context c) { return sp(c).getString(KEY_LANG, "fa"); }
    public static void lang(Context c, String v) { sp(c).edit().putString(KEY_LANG, v).apply(); }

    public static int hijriAdj(Context c) { return sp(c).getInt(KEY_HIJRI_ADJ, -1); }
    public static void hijriAdj(Context c, int v) { sp(c).edit().putInt(KEY_HIJRI_ADJ, v).apply(); }
}
