package com.taqvimcod.calendar;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Instances;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;

import java.util.ArrayList;
import java.util.List;

/**
 * Two-way Google Calendar integration through the system calendar provider
 * (CalendarContract). Events of the user's Google accounts are read live and
 * new / edited events are written back to the chosen Google calendar.
 *
 * All user-provided strings are length-clamped before writing and every
 * provider call is guarded by an explicit permission check.
 */
public final class CalendarRepo {

    private CalendarRepo() {}

    private static final int MAX_TEXT = 8000;

    public static class Ev {
        public long eventId, calendarId;
        public String title, location, desc, calName;
        public long begin, end;
        public boolean allDay;
        public int calColor;
        public boolean writable;
    }

    public static class Cal {
        public long id;
        public String name, account, accountType;
        public int color;
        public boolean primary;
    }

    public static boolean hasReadPerm(Context c) {
        return c.checkSelfPermission(Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasPerms(Context c) {
        return hasReadPerm(c)
                && c.checkSelfPermission(Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED;
    }

    public static void request(Activity a) {
        a.requestPermissions(new String[]{
                Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR}, 42);
    }

    /** writable calendars (Google accounts first, primary first) */
    public static List<Cal> writableCalendars(Context c) {
        List<Cal> out = new ArrayList<>();
        if (!hasPerms(c)) return out;
        String[] proj = {Calendars._ID, Calendars.CALENDAR_DISPLAY_NAME, Calendars.ACCOUNT_NAME,
                Calendars.ACCOUNT_TYPE, Calendars.CALENDAR_COLOR, Calendars.CALENDAR_ACCESS_LEVEL,
                Calendars.IS_PRIMARY};
        try (Cursor cur = c.getContentResolver().query(Calendars.CONTENT_URI, proj,
                Calendars.VISIBLE + "=1 AND " + Calendars.CALENDAR_ACCESS_LEVEL + ">=" +
                        Calendars.CAL_ACCESS_CONTRIBUTOR,
                null, Calendars.IS_PRIMARY + " DESC, " + Calendars.CALENDAR_DISPLAY_NAME)) {
            if (cur != null) {
                int gi = cur.getColumnIndexOrThrow(Calendars._ID);
                int gn = cur.getColumnIndexOrThrow(Calendars.CALENDAR_DISPLAY_NAME);
                int ga = cur.getColumnIndexOrThrow(Calendars.ACCOUNT_NAME);
                int gc = cur.getColumnIndexOrThrow(Calendars.CALENDAR_COLOR);
                while (cur.moveToNext()) {
                    Cal cal = new Cal();
                    cal.id = cur.getLong(gi);
                    cal.name = cur.getString(gn);
                    cal.account = cur.getString(ga);
                    int gat = cur.getColumnIndex(Calendars.ACCOUNT_TYPE);
                    int gp = cur.getColumnIndex(Calendars.IS_PRIMARY);
                    cal.accountType = gat >= 0 ? cur.getString(gat) : null;
                    cal.primary = gp >= 0 && cur.getInt(gp) == 1;
                    cal.color = cur.getInt(gc);
                    out.add(cal);
                }
            }
        } catch (Exception ignored) {}
        // Prefer Google calendars, then primary calendars; keep names stable within each group.
        out.sort((a, b) -> {
            int googleFirst = Boolean.compare(isGoogle(b), isGoogle(a));
            if (googleFirst != 0) return googleFirst;
            int primaryFirst = Boolean.compare(b.primary, a.primary);
            if (primaryFirst != 0) return primaryFirst;
            return String.valueOf(a.name).compareToIgnoreCase(String.valueOf(b.name));
        });
        return out;
    }

    private static boolean isGoogle(Cal cal) {
        return "com.google".equals(cal.accountType);
    }

    /** defensive clamp for any text we hand to the provider */
    private static String clamp(String s) {
        if (s == null) return null;
        s = s.replace("\u0000", "").trim();
        return s.length() > MAX_TEXT ? s.substring(0, MAX_TEXT) : s;
    }

    /** expanded instances overlapping [from, to) */
    public static List<Ev> instances(Context c, long from, long to) {
        List<Ev> out = new ArrayList<>();
        if (!hasReadPerm(c)) return out;
        List<Cal> writable = writableCalendars(c);
        String[] proj = {Instances.EVENT_ID, Instances.TITLE, Instances.BEGIN, Instances.END,
                Instances.ALL_DAY, Instances.EVENT_LOCATION, Instances.DESCRIPTION,
                Instances.CALENDAR_ID, Instances.CALENDAR_DISPLAY_NAME, Instances.CALENDAR_COLOR};
        try (Cursor cur = Instances.query(c.getContentResolver(), proj, from, to)) {
            if (cur != null) {
                int gId = cur.getColumnIndexOrThrow(Instances.EVENT_ID);
                int gT = cur.getColumnIndexOrThrow(Instances.TITLE);
                int gB = cur.getColumnIndexOrThrow(Instances.BEGIN);
                int gE = cur.getColumnIndexOrThrow(Instances.END);
                int gA = cur.getColumnIndexOrThrow(Instances.ALL_DAY);
                int gL = cur.getColumnIndexOrThrow(Instances.EVENT_LOCATION);
                int gD = cur.getColumnIndexOrThrow(Instances.DESCRIPTION);
                int gC = cur.getColumnIndexOrThrow(Instances.CALENDAR_ID);
                int gN = cur.getColumnIndexOrThrow(Instances.CALENDAR_DISPLAY_NAME);
                int gCol = cur.getColumnIndexOrThrow(Instances.CALENDAR_COLOR);
                while (cur.moveToNext()) {
                    Ev ev = new Ev();
                    ev.eventId = cur.getLong(gId);
                    ev.title = cur.getString(gT);
                    ev.begin = cur.getLong(gB);
                    ev.end = cur.getLong(gE);
                    ev.allDay = cur.getInt(gA) == 1;
                    ev.location = cur.getString(gL);
                    ev.desc = cur.getString(gD);
                    ev.calendarId = cur.getLong(gC);
                    ev.calName = cur.getString(gN);
                    ev.calColor = cur.getInt(gCol);
                    for (Cal wc : writable) if (wc.id == ev.calendarId) { ev.writable = true; break; }
                    if (ev.title == null || ev.title.trim().isEmpty())
                        ev.title = S.fa(c) ? "(بدون عنوان)" : "(No title)";
                    out.add(ev);
                }
            }
        } catch (Exception ignored) {}
        out.sort((a, b) -> {
            if (a.allDay != b.allDay) return a.allDay ? -1 : 1;
            return Long.compare(a.begin, b.begin);
        });
        return out;
    }

    public static boolean insert(Context c, long calId, String title, String desc,
                                 long start, long end, boolean allDay) {
        if (!hasPerms(c)) return false;
        ContentValues v = new ContentValues();
        v.put(Events.CALENDAR_ID, calId);
        v.put(Events.TITLE, clamp(title));
        if (desc != null && !desc.isEmpty()) v.put(Events.DESCRIPTION, clamp(desc));
        v.put(Events.DTSTART, start);
        v.put(Events.DTEND, end);
        v.put(Events.ALL_DAY, allDay ? 1 : 0);
        v.put(Events.EVENT_TIMEZONE, java.util.TimeZone.getDefault().getID());
        v.put(Events.STATUS, Events.STATUS_CONFIRMED);
        try {
            Uri uri = c.getContentResolver().insert(Events.CONTENT_URI, v);
            return uri != null;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean update(Context c, long eventId, String title, String desc,
                                 long start, long end, boolean allDay) {
        if (!hasPerms(c)) return false;
        ContentValues v = new ContentValues();
        v.put(Events.TITLE, clamp(title));
        v.put(Events.DESCRIPTION, clamp(desc) == null ? "" : clamp(desc));
        v.put(Events.DTSTART, start);
        v.put(Events.DTEND, end);
        v.put(Events.ALL_DAY, allDay ? 1 : 0);
        v.put(Events.EVENT_TIMEZONE, java.util.TimeZone.getDefault().getID());
        try {
            int updated = c.getContentResolver().update(
                    ContentUris.withAppendedId(Events.CONTENT_URI, eventId), v, null, null);
            return updated > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean delete(Context c, long eventId) {
        if (!hasPerms(c)) return false;
        try {
            int deleted = c.getContentResolver().delete(
                    ContentUris.withAppendedId(Events.CONTENT_URI, eventId), null, null);
            return deleted > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /** RRULE of parent event (null if none / one-time) */
    public static String rruleOf(Context c, long eventId) {
        if (!hasPerms(c)) return null;
        try (Cursor cur = c.getContentResolver().query(
                ContentUris.withAppendedId(Events.CONTENT_URI, eventId),
                new String[]{Events.RRULE}, null, null, null)) {
            if (cur != null && cur.moveToFirst()) return cur.getString(0);
        } catch (Exception ignored) {}
        return null;
    }
}
