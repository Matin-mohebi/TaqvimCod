package com.taqvimcod.calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GeomagneticField;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/** Offline prayer-time dashboard and sensor-assisted Qibla compass. */
public class PrayerActivity extends Activity implements SensorEventListener {
    private double latitude, longitude;
    private String city;
    private Theme.Pal pal;
    private SensorManager sensorManager;
    private Sensor orientationSensor;
    private TextView compassArrow, compassHeading, qiblaLabel, cityLabel;
    private double qiblaBearing;
    private float magneticAzimuth;
    private boolean sensorAvailable;
    private Button reminderButton;
    private static final int REQUEST_NOTIFICATIONS = 813;

    private int dp(float value) { return (int)(value * getResources().getDisplayMetrics().density); }

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        pal = Theme.get(this);
        city = SecurePrefs.getString(this, "prayer_city", "زنجان");
        latitude = parse(SecurePrefs.getString(this, "prayer_lat", "36.6736"), 36.6736);
        longitude = parse(SecurePrefs.getString(this, "prayer_lon", "48.4787"), 48.4787);
        getWindow().setStatusBarColor(pal.gradStart);
        getWindow().setNavigationBarColor(pal.bg);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(pal.bg);
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(18), dp(18), dp(18), dp(28));
        scroll.addView(page);
        TextView title = text("اوقات شرعی و قبله‌نما", 23, pal.text, true);
        page.addView(title);
        TextView subtitle = text("محاسبه آفلاین بر اساس مختصات شهر و منطقه زمانی دستگاه", 13, pal.textSub, false);
        subtitle.setPadding(0, dp(5), 0, dp(14));
        page.addView(subtitle);

        LinearLayout cityCard = card();
        cityLabel = text("", 17, pal.text, true);
        cityCard.addView(cityLabel);
        Button change = button("تغییر شهر و مختصات");
        change.setOnClickListener(v -> editLocation());
        cityCard.addView(change);
        reminderButton = button("");
        reminderButton.setOnClickListener(v -> toggleReminders());
        cityCard.addView(reminderButton);
        page.addView(cityCard);

        LinearLayout timesCard = card();
        timesCard.addView(text("اوقات امروز", 18, pal.text, true));
        timesCard.addView(text(new SimpleDateFormat("EEEE، d MMMM yyyy", new Locale("fa","IR")).format(new Date()), 12, pal.textSub, false));
        String[] names = {"اذان صبح", "طلوع آفتاب", "اذان ظهر", "عصر (محاسباتی)", "غروب آفتاب", "اذان عشا"};
        TextView[] timeViews = new TextView[names.length];
        for (int i=0;i<names.length;i++) {
            LinearLayout row = new LinearLayout(this);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(0, dp(9), 0, dp(9));
            TextView name = text(names[i], 15, pal.text, false);
            row.addView(name, new LinearLayout.LayoutParams(0, -2, 1f));
            timeViews[i] = text("--:--", 17, pal.accent, true);
            row.addView(timeViews[i]);
            timesCard.addView(row);
            if (i < names.length-1) {
                View line = new View(this); line.setBackgroundColor(pal.divider);
                timesCard.addView(line, new LinearLayout.LayoutParams(-1, dp(1)));
            }
        }
        page.addView(timesCard);
        renderTimes(timeViews);

        LinearLayout qiblaCard = card();
        qiblaCard.addView(text("قبله‌نما", 18, pal.text, true));
        compassArrow = text("▲", 84, pal.accent, true);
        compassArrow.setGravity(Gravity.CENTER);
        compassArrow.setPadding(0, dp(12), 0, dp(6));
        qiblaCard.addView(compassArrow, new LinearLayout.LayoutParams(-1, -2));
        compassHeading = text("حسگر جهت در حال بررسی است…", 14, pal.textSub, false);
        compassHeading.setGravity(Gravity.CENTER);
        qiblaCard.addView(compassHeading);
        qiblaLabel = text("", 13, pal.text, true);
        qiblaLabel.setGravity(Gravity.CENTER);
        qiblaLabel.setPadding(0, dp(6), 0, 0);
        qiblaCard.addView(qiblaLabel);
        page.addView(qiblaCard);
        TextView note = text("دقت اوقات شرعی به روش محاسبه و افق محلی وابسته است و ممکن است با جدول رسمی چند دقیقه اختلاف داشته باشد. برای قطب‌نما، گوشی را دور از آهن‌ربا و وسایل فلزی نگه دارید.", 12, pal.textSub, false);
        note.setPadding(dp(4), dp(10), dp(4), 0);
        page.addView(note);
        setContentView(scroll);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        orientationSensor = sensorManager == null ? null : sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorAvailable = orientationSensor != null;
        updateLocationLabels();
        updateReminderButton();
        qiblaBearing = PrayerTimes.qiblaBearing(latitude, longitude);
        qiblaLabel.setText("جهت قبله از شمال جغرافیایی: " + S.d(this, String.format(Locale.US, "%.1f°", qiblaBearing)));
        if (!sensorAvailable) compassHeading.setText("حسگر جهت در این گوشی موجود نیست؛ زاویه قبله همچنان نمایش داده می‌شود.");
    }

    private void toggleReminders() {
        boolean enabled = SecurePrefs.getBoolean(this,"prayer_reminders_enabled",false);
        if (enabled) {
            SecurePrefs.putBoolean(this,"prayer_reminders_enabled",false);
            PrayerReminderReceiver.cancelAll(this);
            updateReminderButton();
            Toast.makeText(this,"یادآوری اوقات شرعی خاموش شد.",Toast.LENGTH_SHORT).show();
            return;
        }
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},REQUEST_NOTIFICATIONS);
            return;
        }
        enableReminders();
    }

    private void enableReminders() {
        SecurePrefs.putBoolean(this,"prayer_reminders_enabled",true);
        PrayerReminderReceiver.scheduleAll(this);
        updateReminderButton();
        Toast.makeText(this,"یادآوری روزانه اوقات شرعی فعال شد.",Toast.LENGTH_SHORT).show();
    }

    private void updateReminderButton() {
        if (reminderButton == null) return;
        boolean enabled=SecurePrefs.getBoolean(this,"prayer_reminders_enabled",false);
        reminderButton.setText(enabled ? "خاموش‌کردن یادآوری‌های روزانه" : "فعال‌کردن یادآوری‌های روزانه");
    }

    @Override public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (requestCode==REQUEST_NOTIFICATIONS) {
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) enableReminders();
            else Toast.makeText(this,"برای نمایش یادآوری‌ها باید اجازه اعلان‌ها را بدهید.",Toast.LENGTH_LONG).show();
        }
    }

    private void renderTimes(TextView[] out) {
        PrayerTimes.Day day = PrayerTimes.calculate(new Date(), latitude, longitude, TimeZone.getDefault());
        long[] times = {day.fajr,day.sunrise,day.dhuhr,day.asr,day.maghrib,day.isha};
        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm", Locale.getDefault());
        for (int i=0;i<out.length;i++) {
            out[i].setText(times[i] == Long.MIN_VALUE ? "—" : S.d(this, fmt.format(new Date(times[i]))));
        }
    }

    private void editLocation() {
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(dp(18), dp(8), dp(18), dp(4));
        EditText name = field("نام شهر", city);
        EditText lat = field("عرض جغرافیایی (مثلاً 36.67)", Double.toString(latitude));
        EditText lon = field("طول جغرافیایی (مثلاً 48.48)", Double.toString(longitude));
        form.addView(name); form.addView(lat); form.addView(lon);
        new AlertDialog.Builder(this).setTitle("تنظیم شهر").setView(form)
                .setNegativeButton("انصراف", null)
                .setPositiveButton("ذخیره", (d,w) -> {
                    try {
                        double newLat=Double.parseDouble(lat.getText().toString().trim());
                        double newLon=Double.parseDouble(lon.getText().toString().trim());
                        if (newLat < -90 || newLat > 90 || newLon < -180 || newLon > 180) throw new IllegalArgumentException();
                        latitude=newLat; longitude=newLon;
                        city=name.getText().toString().trim();
                        if (city.isEmpty()) city="شهر انتخابی";
                        SecurePrefs.putString(this,"prayer_city",city);
                        SecurePrefs.putString(this,"prayer_lat",Double.toString(latitude));
                        SecurePrefs.putString(this,"prayer_lon",Double.toString(longitude));
                        updateLocationLabels();
                        qiblaBearing=PrayerTimes.qiblaBearing(latitude,longitude);
                        qiblaLabel.setText("جهت قبله از شمال جغرافیایی: "+S.d(this,String.format(Locale.US,"%.1f°",qiblaBearing)));
                        recreate();
                    } catch (Exception ex) {
                        Toast.makeText(this,"مختصات معتبر وارد کنید.",Toast.LENGTH_LONG).show();
                    }
                }).show();
    }

    private EditText field(String hint, String value) {
        EditText e=new EditText(this); e.setSingleLine(true); e.setHint(hint); e.setText(value);
        e.setTextColor(pal.text); e.setHintTextColor(pal.textSub);
        return e;
    }

    private void updateLocationLabels() {
        if (cityLabel != null) cityLabel.setText("شهر: "+city+"  •  "+String.format(Locale.US,"%.4f, %.4f",latitude,longitude));
    }

    private LinearLayout card() {
        LinearLayout b=new LinearLayout(this); b.setOrientation(LinearLayout.VERTICAL);
        b.setPadding(dp(16),dp(14),dp(16),dp(14));
        android.graphics.drawable.GradientDrawable bg=new android.graphics.drawable.GradientDrawable();
        bg.setColor(pal.surface); bg.setCornerRadius(dp(20)); b.setBackground(bg); b.setElevation(dp(2));
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2); lp.bottomMargin=dp(14);
        b.setLayoutParams(lp); return b;
    }

    private TextView text(String s,float size,int color,boolean bold) {
        TextView t=new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color);
        t.setTypeface(MonthView.Font.get(this,bold?MonthView.Font.BOLD:MonthView.Font.REGULAR));
        t.setLineSpacing(dp(2),1f); return t;
    }

    private Button button(String s) {
        Button b=new Button(this); b.setText(s); b.setTextColor(pal.accent);
        return b;
    }

    private static double parse(String value,double fallback) {
        try { return Double.parseDouble(value); } catch(Exception e) { return fallback; }
    }

    @Override protected void onResume() {
        super.onResume();
        if (sensorAvailable && sensorManager != null) sensorManager.registerListener(this,orientationSensor,SensorManager.SENSOR_DELAY_UI);
    }

    @Override protected void onPause() {
        if (sensorManager != null) sensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType()!=Sensor.TYPE_ORIENTATION) return;
        magneticAzimuth=event.values[0];
        float trueAzimuth=magneticAzimuth;
        try {
            GeomagneticField field=new GeomagneticField((float)latitude,(float)longitude,0f,System.currentTimeMillis());
            trueAzimuth += field.getDeclination();
        } catch(Exception ignored) {}
        float rotation=(float)(qiblaBearing-trueAzimuth);
        compassArrow.setRotation(rotation);
        compassHeading.setText("جهت گوشی: "+S.d(this,String.format(Locale.US,"%.0f°",(trueAzimuth+360)%360))+"  •  قبله: "+S.d(this,String.format(Locale.US,"%.0f°",qiblaBearing)));
    }

    @Override public void onAccuracyChanged(Sensor sensor,int accuracy) {}
}
