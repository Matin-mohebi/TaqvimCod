package com.taqvimcod.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

/**
 * Canvas-painted qibla compass dial.
 *
 * The rose (ticks, cardinal letters, qibla arrow) rotates to -deviceHeading so
 * that when the top of the phone points at the qibla the amber qibla arrow
 * sits under the fixed needle at the top. Heading changes are eased toward
 * the newest sensor target with a per-frame lerp driven by
 * postInvalidateOnAnimation — a cheap, dependency-free needle animation.
 */
public class QiblaDialView extends View {

    private final Paint pRose = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pTick = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pText = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pNeedle = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pHub = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pQibla = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Theme.Pal pal;
    private float heading = 0f;          // displayed (eased) true heading, degrees
    private float targetHeading = 0f;    // newest sensor heading
    private float qiblaBearing = 0f;
    private boolean sensorOk = true;
    private boolean aligned = false;
    private android.graphics.Typeface face;

    public QiblaDialView(Context c) {
        super(c);
        pRose.setStyle(Paint.Style.FILL);
        pTick.setStyle(Paint.Style.STROKE);
        pNeedle.setStyle(Paint.Style.FILL);
        pHub.setStyle(Paint.Style.FILL);
        pQibla.setStyle(Paint.Style.FILL);
        pText.setTextAlign(Paint.Align.CENTER);
    }

    public void setup(Theme.Pal pal, float qiblaBearing, boolean sensorOk) {
        this.pal = pal;
        this.qiblaBearing = qiblaBearing;
        this.sensorOk = sensorOk;
        if (face == null) face = MonthView.Font.get(getContext(), MonthView.Font.SEMIBOLD);
        invalidate();
    }

    public void setTargetHeading(float trueHeading) {
        targetHeading = (trueHeading % 360f + 360f) % 360f;
        aligned = angleDistance(targetHeading, qiblaBearing) <= 5f;
        postInvalidateOnAnimation();
    }

    private static float angleDistance(float a, float b) {
        float d = Math.abs(a - b) % 360f;
        return d > 180f ? 360f - d : d;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (pal == null) return;
        float cx = getWidth() / 2f, cy = getHeight() / 2f;
        float r = Math.min(cx, cy) - 2f;
        if (r <= 0) return;
        float density = getResources().getDisplayMetrics().density;

        // ease displayed heading toward the target (needle animation, ~60fps)
        float diff = shortestDelta(targetHeading, heading);
        if (Math.abs(diff) > 0.05f) {
            heading += diff * 0.28f;
            postInvalidateOnAnimation();
        } else {
            heading = targetHeading;
        }

        // dial base
        pRose.setColor(pal.dark ? pal.surfaceAlt : pal.surfaceAlt);
        canvas.drawCircle(cx, cy, r, pRose);
        Paint ring = new Paint(Paint.ANTI_ALIAS_FLAG);
        ring.setStyle(Paint.Style.STROKE);
        ring.setStrokeWidth(density * 1.4f);
        ring.setColor(pal.divider);
        canvas.drawCircle(cx, cy, r - ring.getStrokeWidth(), ring);

        // rotating rose
        canvas.save();
        canvas.rotate(-heading, cx, cy);

        pTick.setColor(pal.textSub);
        pTick.setStrokeWidth(density * 1.1f);
        for (int deg = 0; deg < 360; deg += 15) {
            boolean major = deg % 45 == 0;
            float len = major ? density * 9 : density * 5;
            double rad = Math.toRadians(deg - 90);
            float x1 = cx + (float) Math.cos(rad) * (r - density * 6);
            float y1 = cy + (float) Math.sin(rad) * (r - density * 6);
            float x2 = cx + (float) Math.cos(rad) * (r - density * 6 - len);
            float y2 = cy + (float) Math.sin(rad) * (r - density * 6 - len);
            canvas.drawLine(x1, y1, x2, y2, pTick);
        }

        // cardinal letters (شمال / خاور / جنوب / غرب) rotate with the rose
        pText.setColor(pal.text);
        pText.setTextSize(density * 13);
        pText.setTypeface(face);
        pText.setFakeBoldText(true);
        String[] letters = {"ش", "خ", "ج", "غ"};
        for (int i = 0; i < 4; i++) {
            double rad = Math.toRadians(i * 90 - 90);
            float lx = cx + (float) Math.cos(rad) * (r - density * 24);
            float ly = cy + (float) Math.sin(rad) * (r - density * 24) + density * 4.5f;
            canvas.drawText(letters[i], lx, ly, pText);
        }

        // qibla arrow on the rim (amber), pointing outward
        canvas.save();
        canvas.rotate(qiblaBearing, cx, cy);
        pQibla.setColor(pal.amber);
        Path tri = new Path();
        float tip = r - density * 7;
        float base = r - density * 22;
        float half = density * 6.5f;
        tri.moveTo(cx, cy - tip);
        tri.lineTo(cx - half, cy - base);
        tri.lineTo(cx + half, cy - base);
        tri.close();
        canvas.drawPath(tri, pQibla);
        pText.setTextSize(density * 9.5f);
        pText.setFakeBoldText(false);
        canvas.drawText("قبله", cx, cy - (tip - density * 3), pText);
        pText.setTextSize(density * 13);
        pText.setFakeBoldText(true);
        canvas.restore();

        canvas.restore();

        // fixed needle at the top of the dial
        pNeedle.setColor(aligned ? pal.green : pal.accent);
        Path needle = new Path();
        float nTip = cy - r + density * 26;
        float nHalf = density * 7;
        needle.moveTo(cx, nTip);
        needle.lineTo(cx - nHalf, cy - density * 4);
        needle.lineTo(cx + nHalf, cy - density * 4);
        needle.close();
        canvas.drawPath(needle, pNeedle);

        // hub
        pHub.setColor(aligned ? pal.green : pal.accent);
        canvas.drawCircle(cx, cy, density * 5, pHub);
        pHub.setColor(pal.onGrad);
        canvas.drawCircle(cx, cy, density * 2, pHub);
    }

    private static float shortestDelta(float target, float current) {
        float d = (target - current) % 360f;
        if (d > 180f) d -= 360f;
        if (d < -180f) d += 360f;
        return d;
    }
}
