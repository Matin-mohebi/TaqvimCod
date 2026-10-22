package com.taqvimcod.calendar;

import android.content.Context;
import android.content.res.Configuration;

/**
 * Light / dark / auto theming with 5 selectable color palettes.
 * Visual identity: Persian turquoise, deep teal, and a restrained saffron accent,
 * plus midnight-blue, saffron, emerald and rose alternatives.
 * Every screen resolves Theme.get() in onCreate; changing the palette
 * recreates the activity so it applies on next open of each screen.
 */
public final class Theme {

    public static final int MODE_AUTO = 0, MODE_LIGHT = 1, MODE_DARK = 2;

    public static final int PAL_TURQUOISE = 0;   // فیروزه‌ای (پیش‌فرض)
    public static final int PAL_MIDNIGHT = 1;    // شب نیمه — آبی نیمه‌شب
    public static final int PAL_SAFFRON = 2;     // زعفران — گرم
    public static final int PAL_EMERALD = 3;     // زمرد
    public static final int PAL_ROSE = 4;        // گل محمدی

    public static final int PALETTE_COUNT = 5;

    private Theme() {}

    public static class Pal {
        public int bg, surface, surfaceAlt, divider, text, textSub,
                   accent, accentSoft, onAccent,
                   gradStart, gradEnd, onGrad, onGradSub,
                   red, redSoft, amber, green, ripple;
        public boolean dark;
    }

    public static boolean systemDark(Context c) {
        int m = c.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return m == Configuration.UI_MODE_NIGHT_YES;
    }

    /** resolved palette for the current mode + persisted palette choice */
    public static Pal get(Context c) {
        int mode = Prefs.theme(c);
        boolean dark = mode == MODE_DARK || (mode == MODE_AUTO && systemDark(c));
        return palette(Prefs.palette(c), dark);
    }

    /** pure (context-free) palette lookup — unit-testable */
    public static Pal palette(int paletteId, boolean dark) {
        switch (paletteId) {
            case PAL_MIDNIGHT: return dark ? midnightDark() : midnightLight();
            case PAL_SAFFRON:  return dark ? saffronDark()  : saffronLight();
            case PAL_EMERALD:  return dark ? emeraldDark()  : emeraldLight();
            case PAL_ROSE:     return dark ? roseDark()     : roseLight();
            default:           return dark ? dark()         : light();
        }
    }

    // ---------------- turquoise (default) ----------------

    public static Pal light() {
        Pal p = new Pal();
        p.dark = false;
        // Warm, low-glare canvas with crisp white cards.
        p.bg = 0xFFF5F7F8;
        p.surface = 0xFFFFFFFF;
        p.surfaceAlt = 0xFFEAF1F2;
        p.divider = 0xFFDCE5E8;
        p.text = 0xFF172B36;
        p.textSub = 0xFF6C7F89;
        // Turquoise accent with a deep-teal-to-jade hero gradient.
        p.accent = 0xFF087F8C;
        p.accentSoft = 0xFFDDF3F2;
        p.onAccent = 0xFFFFFFFF;
        p.gradStart = 0xFF103B50;
        p.gradEnd = 0xFF087F75;
        p.onGrad = 0xFFFFFFFF;
        p.onGradSub = 0xCFFFFFFF;
        p.red = 0xFFB83F46;
        p.redSoft = 0xFFFBE9E9;
        p.amber = 0xFFB98220;
        p.green = 0xFF287D52;
        p.ripple = 0x260A3540;
        return p;
    }

    public static Pal dark() {
        Pal p = new Pal();
        p.dark = true;
        // Layered blue-charcoal surfaces keep the calendar calm and readable.
        p.bg = 0xFF0B1218;
        p.surface = 0xFF141F28;
        p.surfaceAlt = 0xFF1C2B35;
        p.divider = 0xFF293B46;
        p.text = 0xFFEAF3F5;
        p.textSub = 0xFFA0B2BA;
        p.accent = 0xFF52D4C8;
        p.accentSoft = 0xFF173C3D;
        p.onAccent = 0xFF082321;
        p.gradStart = 0xFF102D42;
        p.gradEnd = 0xFF075A55;
        p.onGrad = 0xFFF6FFFF;
        p.onGradSub = 0xCFFFFFFF;
        p.red = 0xFFFF8585;
        p.redSoft = 0xFF382326;
        p.amber = 0xFFFFC96B;
        p.green = 0xFF69D99A;
        p.ripple = 0x33FFFFFF;
        return p;
    }

    // ---------------- midnight (شب نیمه) ----------------

    private static Pal midnightLight() {
        Pal p = new Pal();
        p.dark = false;
        p.bg = 0xFFEFF1F7;
        p.surface = 0xFFFFFFFF;
        p.surfaceAlt = 0xFFE5E9F5;
        p.divider = 0xFFD7DCEC;
        p.text = 0xFF1A2238;
        p.textSub = 0xFF6B7694;
        p.accent = 0xFF3B5BDB;
        p.accentSoft = 0xFFE2E8FD;
        p.onAccent = 0xFFFFFFFF;
        p.gradStart = 0xFF16213E;
        p.gradEnd = 0xFF2C4A9A;
        p.onGrad = 0xFFFFFFFF;
        p.onGradSub = 0xCCFFFFFF;
        p.red = 0xFFB83F46;
        p.redSoft = 0xFFFBE9E9;
        p.amber = 0xFFB98220;
        p.green = 0xFF2E7D54;
        p.ripple = 0x261A2C64;
        return p;
    }

    private static Pal midnightDark() {
        Pal p = new Pal();
        p.dark = true;
        p.bg = 0xFF090E1C;
        p.surface = 0xFF121A30;
        p.surfaceAlt = 0xFF1A2440;
        p.divider = 0xFF283352;
        p.text = 0xFFE9EEFB;
        p.textSub = 0xFF9FAACB;
        p.accent = 0xFF89A2FF;
        p.accentSoft = 0xFF1E2A55;
        p.onAccent = 0xFF0A1230;
        p.gradStart = 0xFF0F1B3D;
        p.gradEnd = 0xFF28417E;
        p.onGrad = 0xFFF4F7FF;
        p.onGradSub = 0xCCFFFFFF;
        p.red = 0xFFFF8585;
        p.redSoft = 0xFF34263A;
        p.amber = 0xFFFFC96B;
        p.green = 0xFF6FD9A0;
        p.ripple = 0x33FFFFFF;
        return p;
    }

    // ---------------- saffron (زعفران) ----------------

    private static Pal saffronLight() {
        Pal p = new Pal();
        p.dark = false;
        p.bg = 0xFFFAF4E9;
        p.surface = 0xFFFFFCF5;
        p.surfaceAlt = 0xFFF4E8D2;
        p.divider = 0xFFE8D9BC;
        p.text = 0xFF3A2A12;
        p.textSub = 0xFF8A7452;
        p.accent = 0xFFB4770F;
        p.accentSoft = 0xFFF9EBD0;
        p.onAccent = 0xFFFFFFFF;
        p.gradStart = 0xFF6D4A15;
        p.gradEnd = 0xFFC98A1B;
        p.onGrad = 0xFFFFF8EC;
        p.onGradSub = 0xD8FFF3DD;
        p.red = 0xFFB03A2E;
        p.redSoft = 0xFFF9E5E1;
        p.amber = 0xFFA56A0A;
        p.green = 0xFF5E7C2E;
        p.ripple = 0x265C3F14;
        return p;
    }

    private static Pal saffronDark() {
        Pal p = new Pal();
        p.dark = true;
        p.bg = 0xFF171106;
        p.surface = 0xFF241B0C;
        p.surfaceAlt = 0xFF31250F;
        p.divider = 0xFF453317;
        p.text = 0xFFF7ECD8;
        p.textSub = 0xFFC0A87E;
        p.accent = 0xFFF0B450;
        p.accentSoft = 0xFF3F2F10;
        p.onAccent = 0xFF241803;
        p.gradStart = 0xFF3A2A0D;
        p.gradEnd = 0xFF8A6215;
        p.onGrad = 0xFFFFF6E6;
        p.onGradSub = 0xD8FFF3DD;
        p.red = 0xFFFF8A78;
        p.redSoft = 0xFF3A2620;
        p.amber = 0xFFFFC96B;
        p.green = 0xFFA8CE7A;
        p.ripple = 0x33FFFFFF;
        return p;
    }

    // ---------------- emerald (زمرد) ----------------

    private static Pal emeraldLight() {
        Pal p = new Pal();
        p.dark = false;
        p.bg = 0xFFEFF6F1;
        p.surface = 0xFFFFFFFF;
        p.surfaceAlt = 0xFFE0F0E7;
        p.divider = 0xFFD2E6DA;
        p.text = 0xFF14301F;
        p.textSub = 0xFF647F6E;
        p.accent = 0xFF1D8A5B;
        p.accentSoft = 0xFFDFF3E7;
        p.onAccent = 0xFFFFFFFF;
        p.gradStart = 0xFF0C4A34;
        p.gradEnd = 0xFF219465;
        p.onGrad = 0xFFFFFFFF;
        p.onGradSub = 0xD2FFFFFF;
        p.red = 0xFFB83F46;
        p.redSoft = 0xFFFBE9E9;
        p.amber = 0xFFB98220;
        p.green = 0xFF1D8A5B;
        p.ripple = 0x26124330;
        return p;
    }

    private static Pal emeraldDark() {
        Pal p = new Pal();
        p.dark = true;
        p.bg = 0xFF09130E;
        p.surface = 0xFF11211A;
        p.surfaceAlt = 0xFF172C22;
        p.divider = 0xFF244033;
        p.text = 0xFFE7F5EC;
        p.textSub = 0xFF9BB8A7;
        p.accent = 0xFF63D6A0;
        p.accentSoft = 0xFF173B2A;
        p.onAccent = 0xFF06231A;
        p.gradStart = 0xFF0D3327;
        p.gradEnd = 0xFF17714F;
        p.onGrad = 0xFFF2FFF8;
        p.onGradSub = 0xD2FFFFFF;
        p.red = 0xFFFF8585;
        p.redSoft = 0xFF24352C;
        p.amber = 0xFFFFC96B;
        p.green = 0xFF6FD9A0;
        p.ripple = 0x33FFFFFF;
        return p;
    }

    // ---------------- rose (گل محمدی) ----------------

    private static Pal roseLight() {
        Pal p = new Pal();
        p.dark = false;
        p.bg = 0xFFFBF1F3;
        p.surface = 0xFFFFFFFF;
        p.surfaceAlt = 0xFFF6E2E7;
        p.divider = 0xFFEBD4DA;
        p.text = 0xFF371B23;
        p.textSub = 0xFF8A6772;
        p.accent = 0xFFA63D5C;
        p.accentSoft = 0xFFF8E3E9;
        p.onAccent = 0xFFFFFFFF;
        p.gradStart = 0xFF5E1F33;
        p.gradEnd = 0xFFC25E7A;
        p.onGrad = 0xFFFFF5F7;
        p.onGradSub = 0xD8FFFFFF;
        p.red = 0xFFB03A2E;
        p.redSoft = 0xFFF9E5E1;
        p.amber = 0xFFB98220;
        p.green = 0xFF3E7D52;
        p.ripple = 0x265E1F33;
        return p;
    }

    private static Pal roseDark() {
        Pal p = new Pal();
        p.dark = true;
        p.bg = 0xFF160D11;
        p.surface = 0xFF241519;
        p.surfaceAlt = 0xFF301B21;
        p.divider = 0xFF452731;
        p.text = 0xFFF9E9ED;
        p.textSub = 0xFFC39AA6;
        p.accent = 0xFFF08CA6;
        p.accentSoft = 0xFF41212C;
        p.onAccent = 0xFF260A13;
        p.gradStart = 0xFF3A1622;
        p.gradEnd = 0xFF8A3B55;
        p.onGrad = 0xFFFFF4F7;
        p.onGradSub = 0xD8FFFFFF;
        p.red = 0xFFFF8A78;
        p.redSoft = 0xFF3A2620;
        p.amber = 0xFFFFC96B;
        p.green = 0xFF7BDB9C;
        p.ripple = 0x33FFFFFF;
        return p;
    }
}
