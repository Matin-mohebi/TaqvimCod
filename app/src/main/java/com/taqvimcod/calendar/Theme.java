package com.taqvimcod.calendar;

import android.content.Context;
import android.content.res.Configuration;

/**
 * Light / dark / auto theming.
 * Visual identity: Persian turquoise, deep teal, and a restrained saffron accent.
 */
public final class Theme {

    public static final int MODE_AUTO = 0, MODE_LIGHT = 1, MODE_DARK = 2;

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

    public static Pal get(Context c) {
        int mode = Prefs.theme(c);
        boolean dark = mode == MODE_DARK || (mode == MODE_AUTO && systemDark(c));
        return dark ? dark() : light();
    }

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
}
