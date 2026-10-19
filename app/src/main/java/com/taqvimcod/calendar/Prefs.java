package com.taqvimcod.calendar;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Settings facade over SecurePrefs (AES-256-GCM, Android Keystore).
 * Migrates the legacy plaintext store on first launch.
 */
public final class Prefs {

    private static final String LEGACY = "taqvimkod";

    public static final String KEY_THEME = "theme";           // 0 auto / 1 light / 2 dark
    public static final String KEY_LANG = "lang";             // "fa" / "en"
    public static final String KEY_HIJRI_ADJ = "hijri_adj";   // -2..+2 (default -1, Iran)
    public static final String KEY_SECURE_SCREEN = "secure_screen"; // FLAG_SECURE

    private static volatile boolean migrated = false;

    private Prefs() {}

    /** one-time upgrade from the v1.0 plaintext prefs */
    private static void migrate(Context c) {
        if (migrated) return;
        synchronized (Prefs.class) {
            if (migrated) return;
            try {
                SharedPreferences old = c.getApplicationContext()
                        .getSharedPreferences(LEGACY, Context.MODE_PRIVATE);
                boolean any = false;
                for (String k : new String[]{KEY_THEME, KEY_LANG, KEY_HIJRI_ADJ}) {
                    if (old.contains(k) && !SecurePrefs.contains(c, k)) {
                        Object v = old.getAll().get(k);
                        if (v instanceof Integer) SecurePrefs.putInt(c, k, (Integer) v);
                        else if (v instanceof String) SecurePrefs.putString(c, k, (String) v);
                        any = true;
                    }
                }
                if (any) old.edit().clear().apply();
            } catch (Exception ignored) {
            }
            migrated = true;
        }
    }

    public static int theme(Context c) {
        migrate(c);
        return SecurePrefs.getInt(c, KEY_THEME, 0);
    }

    public static void theme(Context c, int v) {
        migrate(c);
        SecurePrefs.putInt(c, KEY_THEME, v);
    }

    public static String lang(Context c) {
        migrate(c);
        return SecurePrefs.getString(c, KEY_LANG, "fa");
    }

    public static void lang(Context c, String v) {
        migrate(c);
        SecurePrefs.putString(c, KEY_LANG, v);
    }

    public static int hijriAdj(Context c) {
        migrate(c);
        return SecurePrefs.getInt(c, KEY_HIJRI_ADJ, -1);
    }

    public static void hijriAdj(Context c, int v) {
        migrate(c);
        SecurePrefs.putInt(c, KEY_HIJRI_ADJ, v);
    }

    public static boolean secureScreen(Context c) {
        migrate(c);
        return SecurePrefs.getBoolean(c, KEY_SECURE_SCREEN, false);
    }

    public static void secureScreen(Context c, boolean v) {
        migrate(c);
        SecurePrefs.putBoolean(c, KEY_SECURE_SCREEN, v);
    }
}
