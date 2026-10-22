package com.taqvimcod.calendar;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Environment;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Builds the shareable "day card" bitmap (1080x1350) drawn entirely on a
 * Canvas with the bundled Vazirmatn fonts — Android's text stack performs the
 * Persian shaping/bidi, so plain drawText is correct.
 *
 * The same bitmap is (1) saved to the gallery via MediaStore
 * Pictures/TaqvimCod and (2) copied into cacheDir/share for the share sheet.
 */
public final class DayCard {

    public static final int WIDTH = 1080, HEIGHT = 1350;

    private DayCard() {}

    /**
     * @param events day events (titles shown, up to 4)
     */
    public static Bitmap build(Context ctx, Theme.Pal pal, boolean fa,
                               int jy, int jm, int jd, List<CalendarRepo.Ev> events) {
        int jdn = Dates.j2d(jy, jm, jd);
        Dates.JDate j = new Dates.JDate(); j.jy = jy; j.jm = jm; j.jd = jd;
        Dates.GDate g = Dates.d2g(jdn);
        Dates.HDate h = Dates.hijriFromJdn(jdn, Prefs.hijriAdj(ctx));
        MoonPhase.Moon moon = MoonPhase.at(Dates.dayStartUtcMillis(jdn) + 12L * 3600000L);

        Bitmap bmp = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        if (!drawWallpaper(ctx, canvas, paint)) {
            // fallback: vertical hero gradient (theme-based)
            paint.setShader(new LinearGradient(0, 0, WIDTH * 0.65f, HEIGHT,
                    pal.gradStart, pal.gradEnd, Shader.TileMode.CLAMP));
            canvas.drawRect(0, 0, WIDTH, HEIGHT, paint);
            paint.setShader(null);
        }

        Typeface reg = MonthView.Font.get(ctx, MonthView.Font.REGULAR);
        Typeface med = MonthView.Font.get(ctx, MonthView.Font.MEDIUM);
        Typeface semi = MonthView.Font.get(ctx, MonthView.Font.SEMIBOLD);
        Typeface bold = MonthView.Font.get(ctx, MonthView.Font.BOLD);

        int white = pal.onGrad;
        int whiteSub = pal.onGradSub;

        // ---- brand row ----
        float logoX = 64f, logoY = 72f, logoSize = 96f;
        Paint logoBox = new Paint(Paint.ANTI_ALIAS_FLAG);
        logoBox.setColor(0x30FFFFFF);
        canvas.drawRoundRect(new RectF(logoX, logoY, logoX + logoSize, logoY + logoSize),
                28f, 28f, logoBox);
        paint.setColor(white);
        paint.setTypeface(bold);
        paint.setTextSize(56f);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("ت", logoX + logoSize / 2f, logoY + logoSize / 2f + 20f, paint);

        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTypeface(semi);
        paint.setTextSize(46f);
        canvas.drawText("تقویم | TaqvimCod", WIDTH - 64f, logoY + 58f, paint);
        paint.setTypeface(reg);
        paint.setTextSize(30f);
        paint.setColor(whiteSub);
        canvas.drawText(S.t(ctx, "slogan"), WIDTH - 64f, logoY + 104f, paint);

        // ---- hero date ----
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(med);
        paint.setTextSize(46f);
        paint.setColor(whiteSub);
        String weekday = S.weekdays(ctx)[Dates.weekdayFromJdn(jdn)];
        canvas.drawText(weekday, WIDTH / 2f, 310f, paint);

        paint.setTypeface(bold);
        paint.setTextSize(120f);
        paint.setColor(white);
        canvas.drawText(S.d(ctx, j.jd + " " + S.months(ctx)[j.jm - 1] + " " + j.jy),
                WIDTH / 2f, 445f, paint);

        paint.setTypeface(reg);
        paint.setTextSize(42f);
        paint.setColor(whiteSub);
        canvas.drawText(S.fmtGDate(ctx, g) + "  •  " + S.fmtHDate(ctx, h),
                WIDTH / 2f, 525f, paint);

        // ---- moon glyph + illumination ----
        float moonCx = WIDTH / 2f, moonCy = 680f, moonR = 66f;
        MoonGlyphView.drawGlyph(canvas, moonCx, moonCy, moonR,
                pal.amber, blend(pal.gradEnd, pal.onGrad, 0.35f), moon.fraction);
        paint.setTypeface(med);
        paint.setTextSize(34f);
        paint.setColor(whiteSub);
        String[] phaseNames = {"ماه نو", "هلال نو", "تربیع اول", "احدب نو", "ماه کامل",
                "احدب کهن", "تربیع آخر", "هلال کهن"};
        canvas.drawText(phaseNames[moon.phase] + "  •  " + S.d(ctx, moon.illuminationPercent) + "٪ روشنایی",
                WIDTH / 2f, 800f, paint);

        // ---- events ----
        paint.setTypeface(semi);
        paint.setTextSize(38f);
        paint.setColor(white);
        canvas.drawText("✦ " + S.t(ctx, "events"), WIDTH / 2f, 885f, paint);

        paint.setTypeface(reg);
        paint.setTextSize(36f);
        paint.setColor(whiteSub);
        if (events == null || events.isEmpty()) {
            canvas.drawText(S.t(ctx, "no_events"), WIDTH / 2f, 945f, paint);
        } else {
            int n = Math.min(4, events.size());
            for (int i = 0; i < n; i++) {
                CalendarRepo.Ev ev = events.get(i);
                String time = ev.allDay ? S.t(ctx, "all_day_label") : S.fmtTime(ctx, ev.begin);
                String line = "◇ " + ev.title
                        + (ev.allDay ? "" : "  (" + time + ")");
                canvas.drawText(ellipsize(paint, line, WIDTH - 160f), WIDTH / 2f, 945f + i * 54f, paint);
            }
        }

        // ---- prayer times (fajr / dhuhr / maghrib) ----
        try {
            double lat = parse(SecurePrefs.getString(ctx, "prayer_lat", "36.6736"), 36.6736);
            double lon = parse(SecurePrefs.getString(ctx, "prayer_lon", "48.4787"), 48.4787);
            PrayerTimes.Day day = PrayerTimes.calculate(
                    new Date(Dates.dayStartMillis(jdn) + 12L * 3600000L),
                    lat, lon, java.util.TimeZone.getDefault(),
                    SecurePrefs.getInt(ctx, "prayer_method", 0));
            long[] vals = {day.fajr, day.dhuhr, day.maghrib};
            String[] labels = {"اذان صبح", "اذان ظهر", "اذان مغرب"};
            float colW = (WIDTH - 128f) / 3f;
            Paint colBox = new Paint(Paint.ANTI_ALIAS_FLAG);
            colBox.setColor(0x24FFFFFF);
            for (int i = 0; i < 3; i++) {
                float left = 64f + i * colW;
                canvas.drawRoundRect(new RectF(left + 8f, 1160f, left + colW - 8f, 1256f),
                        26f, 26f, colBox);
                paint.setTypeface(reg);
                paint.setTextSize(32f);
                paint.setColor(whiteSub);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(labels[i], left + colW / 2f, 1198f, paint);
                paint.setTypeface(semi);
                paint.setTextSize(40f);
                paint.setColor(white);
                String timeText = vals[i] == Long.MIN_VALUE ? "—"
                        : S.d(ctx, new SimpleDateFormat("HH:mm", Locale.US)
                                .format(new Date(vals[i])));
                canvas.drawText(timeText, left + colW / 2f, 1244f, paint);
            }
        } catch (Exception ignored) {
            // prayer block is decorative on the card; never fail the share
        }

        // ---- footer ----
        paint.setTypeface(reg);
        paint.setTextSize(30f);
        paint.setColor(whiteSub);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("matinzeus.ir", WIDTH / 2f, HEIGHT - 46f, paint);

        return bmp;
    }

    /**
     * Draws the user-selected bundled artwork (drawable/wall_N.jpg) as the card
     * background, center-cropped to 1080x1350 with a legibility scrim.
     * @return false when no wallpaper is selected/available (caller draws gradient)
     */
    private static boolean drawWallpaper(Context ctx, Canvas canvas, Paint paint) {
        int wall = Prefs.shareWall(ctx);
        if (wall < 1) return false;
        try {
            int resId = ctx.getResources().getIdentifier("wall_" + wall, "drawable", ctx.getPackageName());
            if (resId == 0) return false;
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inSampleSize = 2; // source is 1728x2160 — decode at 864x1080
            Bitmap w = BitmapFactory.decodeResource(ctx.getResources(), resId, o);
            if (w == null) return false;
            // center-crop source rect
            float scale = Math.max(WIDTH / (float) w.getWidth(), HEIGHT / (float) w.getHeight());
            int sw = Math.round(WIDTH / scale), sh = Math.round(HEIGHT / scale);
            int sx = (w.getWidth() - sw) / 2, sy = (w.getHeight() - sh) / 2;
            canvas.drawBitmap(w, new Rect(sx, sy, sx + sw, sy + sh),
                    new RectF(0, 0, WIDTH, HEIGHT), paint);
            w.recycle();
            // scrim: keeps white text readable on every artwork
            paint.setShader(new LinearGradient(0, 0, 0, HEIGHT,
                    0x54000000, 0x99000000, Shader.TileMode.CLAMP));
            canvas.drawRect(0, 0, WIDTH, HEIGHT, paint);
            paint.setShader(null);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static String ellipsize(Paint paint, String text, float maxWidth) {
        if (paint.measureText(text) <= maxWidth) return text;
        String ell = "…";
        while (text.length() > 1 && paint.measureText(text + ell) > maxWidth) {
            text = text.substring(0, text.length() - 1);
        }
        return text + ell;
    }

    private static int blend(int a, int b, float t) {
        int ar = (a >> 16) & 0xFF, ag = (a >> 8) & 0xFF, ab = a & 0xFF;
        int br = (b >> 16) & 0xFF, bg = (b >> 8) & 0xFF, bb = b & 0xFF;
        int r = Math.round(ar + (br - ar) * t);
        int g = Math.round(ag + (bg - ag) * t);
        int c = Math.round(ab + (bb - ab) * t);
        return 0xFF000000 | (r << 16) | (g << 8) | c;
    }

    private static double parse(String value, double fallback) {
        try { return Double.parseDouble(value); } catch (Exception e) { return fallback; }
    }

    /** saves into MediaStore Pictures/TaqvimCod (API 29+), returns the media uri or null */
    public static String saveToGallery(Context ctx, Bitmap bmp, String fileName) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            cv.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            cv.put(MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + "/TaqvimCod");
            cv.put(MediaStore.MediaColumns.IS_PENDING, 1);
            Uri uri = ctx.getContentResolver()
                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
            if (uri == null) return null;
            try (OutputStream os = ctx.getContentResolver().openOutputStream(uri)) {
                if (os == null || !bmp.compress(Bitmap.CompressFormat.PNG, 100, os))
                    throw new IllegalStateException("PNG compress failed");
            }
            ContentValues done = new ContentValues();
            done.put(MediaStore.MediaColumns.IS_PENDING, 0);
            ctx.getContentResolver().update(uri, done, null, null);
            return uri.toString();
        } catch (Exception e) {
            return null;
        }
    }

    /** writes the same image into the read-only share provider directory */
    public static File saveToShareCache(Context ctx, Bitmap bmp, String fileName) {
        File dir = new File(ctx.getCacheDir(), ShareProvider.SERVED_DIR);
        if (!dir.exists()) dir.mkdirs();
        File out = new File(dir, fileName);
        try (FileOutputStream fos = new FileOutputStream(out)) {
            if (!bmp.compress(Bitmap.CompressFormat.PNG, 100, fos))
                throw new IllegalStateException("PNG compress failed");
            fos.getFD().sync();
        } catch (Exception e) {
            return null;
        }
        return out;
    }
}
