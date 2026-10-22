package com.taqvimcod.calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** Refreshes the optional date/next-prayer notification after system time changes or reboot. */
public final class PrayerStatusReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        String action = intent == null ? null : intent.getAction();
        if (PrayerStatusNotification.ACTION_REFRESH.equals(action)) {
            if (SecurePrefs.getBoolean(context, "status_notification_enabled", false))
                PrayerStatusNotification.update(context);
            return;
        }
        if (Intent.ACTION_BOOT_COMPLETED.equals(action) || Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)) {
            if (SecurePrefs.getBoolean(context, "prayer_reminders_enabled", false))
                PrayerReminderReceiver.scheduleAll(context);
        }
        if (SecurePrefs.getBoolean(context, "status_notification_enabled", false))
            PrayerStatusNotification.update(context);
    }
}
