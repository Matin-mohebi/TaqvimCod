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
import android.hardware.GeomagneticField;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/** Offline prayer-time dashboard with a live sensor-driven Qibla compass. */
public class PrayerActivity extends Activity implements SensorEventListener {
    private double latitude, longitude;
    private String city;
    private Theme.Pal pal;
    private SensorManager sensorManager;
    // sensor preference chain: rotation vector → magnetometer+accelerometer → legacy orientation
    private Sensor rotationVector, magnetometer, accelerometer, legacyOrientation;
    private final float[] magnetLowPass = new float[3];
    private final float[] gravityLowPass = new float[3];
    private boolean magnetReady, gravityReady;
    private final float[] rotationMatrix = new float[9];
    private final float[] remapped = new float[9];
    private final float[] orientationValues = new float[3];
    private long lastDialUpdate;

    private QiblaDialView qiblaDial;
    private TextView compassHeading, qiblaLabel, calibrationHint, cityLabel;
    private double qiblaBearing;
    private boolean sensorAvailable;
    private Button reminderButton;
    private Button statusNotificationButton;
    private TextView[] prayerTimesViews;
    private Spinner methodSpinner;
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
        Button chooseCity = button("انتخاب شهر از فهرست ایران");
        chooseCity.setOnClickListener(v -> chooseCity());
        cityCard.addView(chooseCity);
        Button change = button("تنظیم دستی شهر و مختصات");
        change.setOnClickListener(v -> editLocation());
        cityCard.addView(change);
        reminderButton = button("");
        reminderButton.setOnClickListener(v -> toggleReminders());
        cityCard.addView(reminderButton);
        statusNotificationButton = button("");
        statusNotificationButton.setOnClickListener(v -> toggleStatusNotification());
        cityCard.addView(statusNotificationButton);
        TextView methodLabel=text("روش محاسبه اوقات شرعی",13,pal.textSub,false);
        methodLabel.setPadding(0,dp(10),0,dp(3));
        cityCard.addView(methodLabel);
        methodSpinner=new Spinner(this);
        String[] methods={"تهران (پیشنهادی برای ایران)","رابطة العالم الإسلامي","مصر","کراچی","ISNA","ام‌القری"};
        ArrayAdapter<String> methodAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,methods);
        methodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        methodSpinner.setAdapter(methodAdapter);
        methodSpinner.setSelection(Math.max(0,Math.min(5,SecurePrefs.getInt(this,"prayer_method",0))));
        cityCard.addView(methodSpinner);
        methodSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent,View view,int position,long id) {
                SecurePrefs.putInt(PrayerActivity.this,"prayer_method",position);
                if(prayerTimesViews!=null) renderTimes(prayerTimesViews);
                PrayerWidgetProvider.updateAll(PrayerActivity.this);
                if (SecurePrefs.getBoolean(PrayerActivity.this,"status_notification_enabled",false))
                    PrayerStatusNotification.update(PrayerActivity.this);
                if(SecurePrefs.getBoolean(PrayerActivity.this,"prayer_reminders_enabled",false)) {
                    PrayerReminderReceiver.cancelAll(PrayerActivity.this);
                    PrayerReminderReceiver.scheduleAll(PrayerActivity.this);
                }
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
        page.addView(cityCard);

        LinearLayout timesCard = card();
        timesCard.addView(text("اوقات امروز", 18, pal.text, true));
        timesCard.addView(text(new SimpleDateFormat("EEEE، d MMMM yyyy", new Locale("fa","IR")).format(new Date()), 12, pal.textSub, false));
        String[] names = {"اذان صبح", "طلوع آفتاب", "اذان ظهر", "عصر (محاسباتی)", "غروب آفتاب", "اذان عشا"};
        TextView[] timeViews = new TextView[names.length];
        prayerTimesViews = timeViews;
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
        qiblaCard.addView(text("قبله‌نما زنده", 18, pal.text, true));
        qiblaCard.addView(text("نوک گوشی (لبه بالایی) را به سمت قبله بگیرید؛ فلش زرد نشان قبله و عقربه وسط، جهت گوشی است.", 12, pal.textSub, false));
        qiblaDial = new QiblaDialView(this);
        qiblaDial.setContentDescription("قطب‌نمای قبله");
        LinearLayout.LayoutParams dialLp = new LinearLayout.LayoutParams(-1, dp(252));
        dialLp.topMargin = dp(10);
        qiblaCard.addView(qiblaDial, dialLp);
        compassHeading = text("حسگر جهت در حال بررسی است…", 14, pal.textSub, false);
        compassHeading.setGravity(Gravity.CENTER);
        compassHeading.setPadding(0, dp(6), 0, 0);
        qiblaCard.addView(compassHeading);
        qiblaLabel = text("", 13, pal.text, true);
        qiblaLabel.setGravity(Gravity.CENTER);
        qiblaLabel.setPadding(0, dp(6), 0, 0);
        qiblaCard.addView(qiblaLabel);
        calibrationHint = text("برای کالیبره‌شدن قطب‌نما، گوشی را در هوا به شکل ۸ (۸) حرکت دهید.", 12.5f, pal.amber, false);
        calibrationHint.setGravity(Gravity.CENTER);
        calibrationHint.setPadding(0, dp(6), 0, 0);
        calibrationHint.setVisibility(View.GONE);
        qiblaCard.addView(calibrationHint);
        page.addView(qiblaCard);
        TextView note = text("دقت اوقات شرعی به روش محاسبه و افق محلی وابسته است و ممکن است با جدول رسمی چند دقیقه اختلاف داشته باشد. برای قطب‌نما، گوشی را دور از آهن‌ربا و وسایل فلزی نگه دارید.", 12, pal.textSub, false);
        note.setPadding(dp(4), dp(10), dp(4), 0);
        page.addView(note);
        setContentView(scroll);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            rotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            legacyOrientation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        }
        // rotation vector alone is the best source; mag+accel is the fused fallback
        sensorAvailable = rotationVector != null || (magnetometer != null && accelerometer != null)
                || legacyOrientation != null;
        qiblaBearing = PrayerTimes.qiblaBearing(latitude, longitude);
        updateQiblaLabels(Float.NaN);
        updateLocationLabels();
        updateReminderButton();
        updateStatusNotificationButton();
        if (SecurePrefs.getBoolean(this,"status_notification_enabled",false)) PrayerStatusNotification.update(this);
        if (!sensorAvailable) {
            compassHeading.setText("حسگر قطب‌نما در این گوشی موجود نیست؛ زاویه قبله همچنان نمایش داده می‌شود.");
        }
    }

    private void updateQiblaLabels(float trueHeading) {
        boolean aligned = !Float.isNaN(trueHeading)
                && Math.abs(((qiblaBearing - trueHeading + 540f) % 360f) - 180f) <= 5f;
        qiblaLabel.setText(aligned
                ? "رو به قبله ✓  (" + S.d(this, String.format(Locale.US, "%.1f°", qiblaBearing)) + ")"
                : "جهت قبله از شمال جغرافیایی: " + S.d(this, String.format(Locale.US, "%.1f°", qiblaBearing)));
        qiblaLabel.setTextColor(aligned ? pal.green : pal.text);
        if (qiblaDial != null) qiblaDial.setup(pal, (float) qiblaBearing, sensorAvailable);
    }

    private void chooseCity() {
        java.util.List<CityDatabase.City> cities = CityDatabase.getIranianCities();
        String[] names = new String[cities.size()];
        for (int i=0;i<cities.size();i++) names[i] = cities.get(i).name;
        new AlertDialog.Builder(this).setTitle("انتخاب شهر")
                .setItems(names, (dialog, which) -> {
                    CityDatabase.City selected = cities.get(which);
                    city = selected.name;
                    latitude = selected.latitude;
                    longitude = selected.longitude;
                    saveLocationAndRefresh();
                }).setNegativeButton("انصراف", null).show();
    }

    private void saveLocationAndRefresh() {
        SecurePrefs.putString(this,"prayer_city",city);
        SecurePrefs.putString(this,"prayer_lat",Double.toString(latitude));
        SecurePrefs.putString(this,"prayer_lon",Double.toString(longitude));
        updateLocationLabels();
        qiblaBearing = PrayerTimes.qiblaBearing(latitude, longitude);
        if (qiblaLabel != null) updateQiblaLabels(Float.NaN);
        if (prayerTimesViews != null) renderTimes(prayerTimesViews);
        PrayerWidgetProvider.updateAll(this);
        if (SecurePrefs.getBoolean(this,"prayer_reminders_enabled",false)) {
            PrayerReminderReceiver.cancelAll(this);
            PrayerReminderReceiver.scheduleAll(this);
        }
        if (SecurePrefs.getBoolean(this,"status_notification_enabled",false)) PrayerStatusNotification.update(this);
    }

    private void toggleStatusNotification() {
        boolean enabled = SecurePrefs.getBoolean(this,"status_notification_enabled",false);
        if (enabled) {
            SecurePrefs.putBoolean(this,"status_notification_enabled",false);
            PrayerStatusNotification.cancel(this);
            updateStatusNotificationButton();
            Toast.makeText(this,"اعلان دائمی خاموش شد.",Toast.LENGTH_SHORT).show();
            return;
        }
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},814);
            return;
        }
        enableStatusNotification();
    }

    private void enableStatusNotification() {
        SecurePrefs.putBoolean(this,"status_notification_enabled",true);
        PrayerStatusNotification.update(this);
        updateStatusNotificationButton();
        Toast.makeText(this,"اعلان دائمی تقویم فعال شد.",Toast.LENGTH_SHORT).show();
    }

    private void updateStatusNotificationButton() {
        if (statusNotificationButton == null) return;
        boolean enabled = SecurePrefs.getBoolean(this,"status_notification_enabled",false);
        statusNotificationButton.setText(enabled ? "خاموش‌کردن اعلان دائمی تاریخ و اذان بعدی" : "نمایش دائمی تاریخ و اذان بعدی در نوار اعلان");
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
        } else if (requestCode==814) {
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) enableStatusNotification();
            else Toast.makeText(this,"برای نمایش اعلان دائمی باید اجازه اعلان‌ها را بدهید.",Toast.LENGTH_LONG).show();
        }
    }

    private void renderTimes(TextView[] out) {
        PrayerTimes.Day day = PrayerTimes.calculate(new Date(), latitude, longitude, TimeZone.getDefault(), SecurePrefs.getInt(this,"prayer_method",0));
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
                        saveLocationAndRefresh();
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

    // ---------------- live compass ----------------

    @Override protected void onResume() {
        super.onResume();
        if (sensorManager == null || !sensorAvailable) return;
        magnetReady = false; gravityReady = false;
        if (rotationVector != null) {
            sensorManager.registerListener(this, rotationVector, SensorManager.SENSOR_DELAY_UI);
        } else {
            if (magnetometer != null) sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
            if (accelerometer != null) sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
        if (rotationVector == null && (magnetometer == null || accelerometer == null)
                && legacyOrientation != null) {
            sensorManager.registerListener(this, legacyOrientation, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override protected void onPause() {
        if (sensorManager != null) sensorManager.unregisterListener(this);
        if (calibrationHint != null) calibrationHint.setVisibility(View.GONE);
        super.onPause();
    }

    @Override public void onSensorChanged(SensorEvent event) {
        if (event == null || event.values == null || event.values.length == 0) return;
        float trueAzimuth = Float.NaN;
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ROTATION_VECTOR:
                trueAzimuth = azimuthFromRotationVector(event.values);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                lowPass(event.values, magnetLowPass);
                magnetReady = true;
                break;
            case Sensor.TYPE_ACCELEROMETER:
                lowPass(event.values, gravityLowPass);
                gravityReady = true;
                break;
            case Sensor.TYPE_ORIENTATION:
                trueAzimuth = event.values[0];
                break;
            default:
                return;
        }
        if (Float.isNaN(trueAzimuth)) {
            if (magnetReady && gravityReady) trueAzimuth = azimuthFromTiltCompass();
            if (Float.isNaN(trueAzimuth)) return;
        }
        try {
            GeomagneticField field = new GeomagneticField(
                    (float) latitude, (float) longitude, 0f, System.currentTimeMillis());
            trueAzimuth += field.getDeclination();
        } catch (Exception ignored) {}
        trueAzimuth = (trueAzimuth % 360f + 360f) % 360f;

        // cheap redraws: throttle dial + text updates to ~30 fps
        long now = System.currentTimeMillis();
        if (now - lastDialUpdate >= 33) {
            lastDialUpdate = now;
            if (qiblaDial != null) qiblaDial.setTargetHeading(trueAzimuth);
            updateQiblaLabels(trueAzimuth);
            compassHeading.setText("جهت گوشی: " + S.d(this, String.format(Locale.US, "%.0f°", trueAzimuth))
                    + "  •  قبله: " + S.d(this, String.format(Locale.US, "%.0f°", qiblaBearing)));
        }
    }

    /** TYPE_ROTATION_VECTOR → device azimuth (0° = north along the top edge) */
    private float azimuthFromRotationVector(float[] values) {
        SensorManager.getRotationMatrixFromVector(rotationMatrix, values);
        // map the matrix so azimuth is relative to the device's top edge (portrait)
        SensorManager.remapCoordinateSystem(rotationMatrix,
                SensorManager.AXIS_X, SensorManager.AXIS_Z, remapped);
        SensorManager.getOrientation(remapped, orientationValues);
        return (float) Math.toDegrees(orientationValues[0]);
    }

    /** fused magnetometer + accelerometer fallback with low-pass smoothing */
    private float azimuthFromTiltCompass() {
        if (!SensorManager.getRotationMatrix(rotationMatrix, null, gravityLowPass, magnetLowPass))
            return Float.NaN;
        SensorManager.remapCoordinateSystem(rotationMatrix,
                SensorManager.AXIS_X, SensorManager.AXIS_Z, remapped);
        SensorManager.getOrientation(remapped, orientationValues);
        return (float) Math.toDegrees(orientationValues[0]);
    }

    private static void lowPass(float[] input, float[] output) {
        final float alpha = 0.18f;
        for (int i = 0; i < Math.min(input.length, output.length); i++) {
            output[i] = output[i] + alpha * (input[i] - output[i]);
        }
    }

    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (calibrationHint == null) return;
        boolean poor = accuracy == SensorManager.SENSOR_STATUS_ACCURACY_LOW
                || accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE;
        calibrationHint.setVisibility(poor ? View.VISIBLE : View.GONE);
    }
}
