package com.taqvimcod.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

/**
 * Small canvas-painted moon phase glyph (two-arc technique: a lit half-disc
 * plus a terminator ellipse). No images, cheap redraws. Used next to the date
 * on the main header and inside the shareable day card.
 */
public class MoonGlyphView extends View {

    private final Paint lit = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint dark = new Paint(Paint.ANTI_ALIAS_FLAG);
    private double cycleFraction = 0.0;
    private int paletteTextSub = 0xFF6C7F89;
    private int paletteAmber = 0xFFB98220;
    private int paletteSurfaceAlt = 0xFFEAF1F2;

    public MoonGlyphView(Context c) {
        super(c);
        dark.setStyle(Paint.Style.FILL);
        lit.setStyle(Paint.Style.FILL);
    }

    /** bind current moon state and palette colors */
    public void setup(Theme.Pal pal, MoonPhase.Moon moon) {
        if (pal != null) {
            paletteTextSub = pal.textSub;
            paletteAmber = pal.amber;
            paletteSurfaceAlt = pal.surfaceAlt;
        }
        if (moon != null) cycleFraction = moon.fraction;
        setContentDescription(getContentDescriptionText());
        invalidate();
    }

    private String getContentDescriptionText() {
        String[] names = {"ماه نو", "هلال نو", "تربیع اول", "احدب نو", "ماه کامل",
                "احدب کهن", "تربیع آخر", "هلال کهن"};
        int idx = (int) Math.round(cycleFraction * 8.0) % 8;
        return names[idx] + "، روشنایی " + Math.round(MoonPhase.illuminationFraction(cycleFraction) * 100) + "٪";
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth(), h = getHeight();
        float r = Math.min(w, h) / 2f - 1.5f;
        if (r <= 0) return;
        drawGlyph(canvas, w / 2f, h / 2f, r, paletteAmber, mixDark(), cycleFraction);
    }

    private int mixDark() {
        // shadow side: blend of surfaceAlt and textSub keeps contrast on both themes
        return blend(paletteSurfaceAlt, paletteTextSub, 0.45f);
    }

    private static int blend(int a, int b, float t) {
        int ar = (a >> 16) & 0xFF, ag = (a >> 8) & 0xFF, ab = a & 0xFF;
        int br = (b >> 16) & 0xFF, bg = (b >> 8) & 0xFF, bb = b & 0xFF;
        int r = Math.round(ar + (br - ar) * t);
        int g = Math.round(ag + (bg - ag) * t);
        int c = Math.round(ab + (bb - ab) * t);
        return 0xFF000000 | (r << 16) | (g << 8) | c;
    }

    /**
     * Draws a moon of the given cycle fraction (0 new … 0.5 full … 1 new) as a
     * two-arc composite: dark disc + lit half + terminator ellipse.
     */
    public static void drawGlyph(Canvas canvas, float cx, float cy, float r,
                                 int litColor, int darkColor, double cycleFraction) {
        Paint litPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint darkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        litPaint.setStyle(Paint.Style.FILL);
        darkPaint.setStyle(Paint.Style.FILL);
        litPaint.setColor(litColor);
        darkPaint.setColor(darkColor);

        // disc outline (subtle ring so the shadow side is visible on tinted cards)
        Paint ring = new Paint(Paint.ANTI_ALIAS_FLAG);
        ring.setStyle(Paint.Style.STROKE);
        ring.setStrokeWidth(Math.max(1f, r * 0.06f));
        ring.setColor(darkColor);
        canvas.drawCircle(cx, cy, r - ring.getStrokeWidth() / 2f, ring);

        canvas.drawCircle(cx, cy, r - ring.getStrokeWidth(), darkPaint);

        double cos = Math.cos(2.0 * Math.PI * cycleFraction);
        boolean waxing = cycleFraction <= 0.5;
        RectF half = new RectF(cx - r, cy - r, cx + r, cy + r);
        // lit half-disc on the waxing (right) or waning (left) side
        float rIn = r - ring.getStrokeWidth();
        half.set(cx - rIn, cy - rIn, cx + rIn, cy + rIn);
        canvas.drawArc(half, waxing ? -90 : 90, 180, true, litPaint);

        float w = (float) (Math.abs(cos) * rIn);
        RectF ellipse = new RectF(cx - w, cy - rIn, cx + w, cy + rIn);
        canvas.drawOval(ellipse, cos >= 0 ? darkPaint : litPaint);
    }
}
