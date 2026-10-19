package com.taqvimcod.calendar;

import android.content.Context;
import android.content.res.Configuration;

/**
 * Light / dark / auto theming.
 * Palette: Persian turquoise (فیروزه‌ای) + lapis blue (لاجورد) + saffron.
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
        p.bg = 0xFFF6F4EF;
        p.surface = 0xFFFFFFFF;
        p.surfaceAlt = 0xFFF0EDE6;
        p.divider = 0xFFE5E1D8;
        p.text = 0xFF242E39;
        p.textSub = 0xFF7D8791;
        p.accent = 0xFF0C8A82;
        p.accentSoft = 0xFFDFF1EF;
        p.onAccent = 0xFFFFFFFF;
        p.gradStart = 0xFF0B4F5E;
        p.gradEnd = 0xFF0E9488;
        p.onGrad = 0xFFFFFFFF;
        p.onGradSub = 0xB8FFFFFF;
        p.red = 0xFFC2483F;
        p.redSoft = 0xFFFAEAE8;
        p.amber = 0xFFD9A441;
        p.green = 0xFF3D8B5F;
        p.ripple = 0x33000000;
        return p;
    }

    public static Pal dark() {
        Pal p = new Pal();
        p.dark = true;
        p.bg = 0xFF0D1218;
        p.surface = 0xFF161D26;
        p.surfaceAlt = 0xFF202A35;
        p.divider = 0xFF27333F;
        p.text = 0xFFE9EEF3;
        p.textSub = 0xFF93A1AE;
        p.accent = 0xFF3BC9BE;
        p.accentSoft = 0xFF173B39;
        p.onAccent = 0xFF06201D;
        p.gradStart = 0xFF0E3A46;
        p.gradEnd = 0xFF0F6E68;
        p.onGrad = 0xFFF2F7F7;
        p.onGradSub = 0xC2FFFFFF;
        p.red = 0xFFE5776F;
        p.redSoft = 0xFF322020;
        p.amber = 0xFFE0B457;
        p.green = 0xFF55C07E;
        p.ripple = 0x33FFFFFF;
        return p;
    }
}
