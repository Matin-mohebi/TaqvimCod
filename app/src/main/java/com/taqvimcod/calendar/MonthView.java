package com.taqvimcod.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Persian month grid, drawn on canvas. RTL: first column (Saturday) on the right.
 * Visual language: soft circular day badges, gradient "today" medallion,
 * holiday tint, event dots — no harsh column shading.
 */
public class MonthView extends View {

    public interface Listener {
        void onDayTap(int jd);
        void onSwipe(boolean next);
    }

    public static class DayCell {
        public boolean hasEvent, isHoliday;
    }

    private final Paint pNum = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pDow = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pBadge = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pRing = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pDot = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Typeface tfNum, tfDow;

    private int jy, jm, selectedJd = -1, offset, monthLen;
    private int todayJy = -1, todayJm = -1, todayJd = -1;
    private SparseArray<DayCell> days = new SparseArray<>();
    private Theme.Pal pal;
    private boolean fa;
    private Listener listener;

    private float pad = 0, dowH = 0, rowH = 0, cellW = 0, badgeR = 0;

    private final GestureDetector gd;

    public MonthView(Context c) {
        super(c);
        tfNum = Font.get(c, Font.SEMIBOLD);
        tfDow = Font.get(c, Font.MEDIUM);
        pDow.setTypeface(tfDow);
        pNum.setTypeface(tfNum);
        gd = new GestureDetector(c, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                handleTap(e);
                return true;
            }
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float vx, float vy) {
                if (Math.abs(vx) > Math.abs(vy) * 1.2f && Math.abs(vx) > 900f) {
                    if (listener != null) listener.onSwipe(vx < 0); // fling left = next
                    return true;
                }
                return false;
            }
        });
    }

    public void setListener(Listener l) { listener = l; }

    public void setup(int jy, int jm, int selJd, int[] today, SparseArray<DayCell> cells,
                      Theme.Pal pal, boolean fa) {
        this.jy = jy; this.jm = jm; this.selectedJd = selJd;
        if (today != null) { todayJy = today[0]; todayJm = today[1]; todayJd = today[2]; }
        this.days = cells != null ? cells : new SparseArray<>();
        this.pal = pal; this.fa = fa;
        this.monthLen = Dates.jalaaliMonthLength(jy, jm);
        this.offset = Dates.weekdayFromJdn(Dates.j2d(jy, jm, 1));
        applyMetrics();
        int rows = (offset + monthLen + 6) / 7;
        float h = dowH + rows * rowH + pad;
        if (getLayoutParams() == null || getLayoutParams().height != ViewGroup.LayoutParams.MATCH_PARENT) {
            getLayoutParams().height = (int) h;
            setLayoutParams(getLayoutParams());
        }
        requestLayout();
        invalidate();
    }

    private void applyMetrics() {
        float density = getResources().getDisplayMetrics().density;
        pad = density * 6;
        dowH = density * 30;
        rowH = density * 47;
        cellW = (getWidth() - pad * 2) / 7f;
        badgeR = Math.min(cellW, rowH) * 0.345f;
        pBadge.setShader(null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);
        applyMetrics();
    }

    @Override
    protected void onMeasure(int wms, int hms) {
        float density = getResources().getDisplayMetrics().density;
        int w = MeasureSpec.getSize(wms);
        int rows = (offset + monthLen + 6) / 7;
        int h = (int) (density * 30 + rows * density * 47 + density * 6);
        super.onMeasure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
    }

    private float cxOf(int col) {
        // RTL: col 0 (Saturday) at the right edge
        return getWidth() - pad - cellW * col - cellW / 2f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (pal == null) return;
        float density = getResources().getDisplayMetrics().density;

        // weekday header
        pDow.setTextSize(density * 12.5f);
        String[] dows = S.weekdaysShort(getContext());
        float dowY = dowH / 2f + density * 4.5f;
        for (int c = 0; c < 7; c++) {
            pDow.setColor(c == 6 ? pal.red : pal.textSub);
            float cx = cxOf(c);
            float tw = pDow.measureText(dows[c]);
            canvas.drawText(dows[c], cx - tw / 2f, dowY, pDow);
        }
        // thin divider under header
        Paint pLine = new Paint();
        pLine.setColor(pal.divider);
        pLine.setStrokeWidth(density * 1);
        canvas.drawLine(pad, dowH - density * 4, getWidth() - pad, dowH - density * 4, pLine);

        // day cells
        pNum.setTextSize(density * 17.5f);
        pRing.setStyle(Paint.Style.STROKE);
        pRing.setStrokeWidth(density * 1.7f);
        pDot.setStyle(Paint.Style.FILL);
        for (int jd = 1; jd <= monthLen; jd++) {
            int idx = offset + jd - 1;
            int r = idx / 7, c = idx % 7;
            float cx = cxOf(c);
            float cy = dowH + r * rowH + rowH / 2f;

            boolean isToday = jy == todayJy && jm == todayJm && jd == todayJd;
            boolean isSel = jd == selectedJd;
            DayCell cell = days.get(jd);
            boolean holiday = cell != null && cell.isHoliday;
            boolean hasEvent = cell != null && cell.hasEvent;
            boolean friday = c == 6;

            // background badge
            if (isToday) {
                pBadge.setShader(new LinearGradient(
                        cx - badgeR, cy - badgeR, cx + badgeR, cy + badgeR,
                        pal.gradStart, pal.gradEnd, Shader.TileMode.CLAMP));
                pBadge.setStyle(Paint.Style.FILL);
                canvas.drawCircle(cx, cy, badgeR, pBadge);
                pBadge.setShader(null);
            } else {
                if (isSel) {
                    pBadge.setShader(null);
                    pBadge.setColor(pal.accentSoft);
                    pBadge.setStyle(Paint.Style.FILL);
                    canvas.drawCircle(cx, cy, badgeR, pBadge);
                } else if (holiday) {
                    pBadge.setShader(null);
                    pBadge.setColor(pal.redSoft);
                    pBadge.setStyle(Paint.Style.FILL);
                    canvas.drawCircle(cx, cy, badgeR, pBadge);
                }
                if (isSel) {
                    pRing.setColor(pal.accent);
                    canvas.drawCircle(cx, cy, badgeR - density * 0.8f, pRing);
                }
            }

            // number
            pNum.setColor(isToday ? pal.onGrad
                    : (holiday || friday) ? pal.red
                    : isSel ? pal.accent : pal.text);
            float tw = pNum.measureText(S.d(getContext(), jd));
            canvas.drawText(S.d(getContext(), jd), cx - tw / 2f, cy + density * 6.2f, pNum);

            // indicator dots
            if (hasEvent || holiday) {
                float dy = cy + badgeR + density * 4.5f;
                float dotR = density * 2.1f;
                if (hasEvent) {
                    pDot.setColor(pal.accent);
                    canvas.drawCircle(cx + (holiday ? dotR * 2.4f : 0), dy, dotR, pDot);
                }
                if (holiday) {
                    pDot.setColor(pal.red);
                    canvas.drawCircle(cx - (hasEvent ? dotR * 2.4f : 0), dy, dotR, pDot);
                }
            }
        }
    }

    private void handleTap(MotionEvent e) {
        float x = e.getX(), y = e.getY();
        if (x < pad || x > getWidth() - pad || y < dowH) return;
        int col = (int) ((getWidth() - pad - x) / cellW);
        if (col < 0 || col > 6) return;
        int row = (int) ((y - dowH) / rowH);
        if (row < 0 || row > 5) return;
        int idx = row * 7 + col;
        int jd = idx - offset + 1;
        if (jd >= 1 && jd <= monthLen && listener != null) listener.onDayTap(jd);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gd.onTouchEvent(event);
        return true;
    }

    /** load bundled Vazirmatn typefaces */
    public static final class Font {
        public static final String REGULAR = "fonts/Vazirmatn-Regular.ttf";
        public static final String MEDIUM = "fonts/Vazirmatn-Medium.ttf";
        public static final String SEMIBOLD = "fonts/Vazirmatn-SemiBold.ttf";
        public static final String BOLD = "fonts/Vazirmatn-Bold.ttf";

        public static Typeface get(Context c, String path) {
            try {
                return Typeface.createFromAsset(c.getAssets(), path);
            } catch (Exception e) {
                return Typeface.DEFAULT;
            }
        }
    }
}
