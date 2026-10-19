package com.taqvimcod.calendar;

import android.content.Context;
import android.content.res.Configuration;

/**
 * Light / dark / auto theming with hand-tuned palettes (teal accent).
 */
public final class Theme {

    public static final int MODE_AUTO = 0, MODE_LIGHT = 1, MODE_DARK = 2;

    private Theme() {}

    public static class Pal {
        public int bg, surface, surfaceAlt, divider, text, textSub,
                   accent, accentSoft, onAccent, red, redSoft, amber, green, ripple;
        public boolean dark;
    }

    public static boolean systemDark(Context c) {
        int m = c.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return m == Configuration.UI_MODE_NIGHT_YES;
    }

    public static Pal get(Context c) {
        int mode = Prefs.theme(c);
        boolean dark = mode == MODE_DARK || (mode == MODE_AUTO && systemDark(c));
        return dark ? dark() : light();
    }

    public static Pal light() {
        Pal p = new Pal();
        p.dark = false;
        p.bg = 0xFFF3F5F7;
        p.surface = 0xFFFFFFFF;
        p.surfaceAlt = 0xFFEDF1F3;
        p.divider = 0xFFE2E7EA;
        p.text = 0xFF1B242B;
        p.textSub = 0xFF6E7D88;
        p.accent = 0xFF0B7C74;
        p.accentSoft = 0xFFDCF0EE;
        p.onAccent = 0xFFFFFFFF;
        p.red = 0xFFD64545;
        p.redSoft = 0xFFFBE9E9;
        p.amber = 0xFFF2B705;
        p.green = 0xFF2E9E5B;
        p.ripple = 0x33000000;
        return p;
    }

    public static Pal dark() {
        Pal p = new Pal();
        p.dark = true;
        p.bg = 0xFF10151A;
        p.surface = 0xFF1A2129;
        p.surfaceAlt = 0xFF232C35;
        p.divider = 0xFF2A343E;
        p.text = 0xFFE7ECEF;
        p.textSub = 0xFF94A3AE;
        p.accent = 0xFF35B5A9;
        p.accentSoft = 0xFF1E3A37;
        p.onAccent = 0xFF06231F;
        p.red = 0xFFFF6B62;
        p.redSoft = 0xFF33201F;
        p.amber = 0xFFF2B705;
        p.green = 0xFF4CC97E;
        p.ripple = 0x33FFFFFF;
        return p;
    }
}
