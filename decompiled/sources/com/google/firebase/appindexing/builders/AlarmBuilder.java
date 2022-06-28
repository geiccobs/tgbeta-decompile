package com.google.firebase.appindexing.builders;

import com.microsoft.appcenter.utils.PrefStorageConstants;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class AlarmBuilder extends IndexableBuilder<AlarmBuilder> {
    public static final String FRIDAY = "Friday";
    public static final String MONDAY = "Monday";
    public static final String SATURDAY = "Saturday";
    public static final String SUNDAY = "Sunday";
    public static final String THURSDAY = "Thursday";
    public static final String TUESDAY = "Tuesday";
    public static final String WEDNESDAY = "Wednesday";

    public AlarmBuilder() {
        super("Alarm");
    }

    public AlarmBuilder setAlarmInstances(AlarmInstanceBuilder... alarmInstanceBuilders) {
        put("alarmInstances", alarmInstanceBuilders);
        return this;
    }

    public AlarmBuilder setDayOfWeek(String... daysOfWeek) {
        for (String str : daysOfWeek) {
            if (!SUNDAY.equals(str) && !MONDAY.equals(str) && !TUESDAY.equals(str) && !WEDNESDAY.equals(str) && !THURSDAY.equals(str) && !FRIDAY.equals(str) && !SATURDAY.equals(str)) {
                String valueOf = String.valueOf(str);
                throw new IllegalArgumentException(valueOf.length() != 0 ? "Invalid weekday ".concat(valueOf) : new String("Invalid weekday "));
            }
        }
        put("dayOfWeek", daysOfWeek);
        return this;
    }

    public AlarmBuilder setEnabled(boolean enabled) {
        put(PrefStorageConstants.KEY_ENABLED, enabled);
        return this;
    }

    public AlarmBuilder setHour(int hour) {
        if (hour < 0 || hour > 23) {
            throw new IllegalArgumentException("Invalid alarm hour");
        }
        put("hour", hour);
        return this;
    }

    public AlarmBuilder setIdentifier(String identifier) {
        put("identifier", identifier);
        return this;
    }

    public AlarmBuilder setMessage(String message) {
        put("message", message);
        return this;
    }

    public AlarmBuilder setMinute(int minute) {
        if (minute < 0 || minute > 59) {
            throw new IllegalArgumentException("Invalid alarm minute");
        }
        put("minute", minute);
        return this;
    }

    public AlarmBuilder setRingtone(String ringtone) {
        put("ringtone", ringtone);
        return this;
    }

    public AlarmBuilder setVibrate(boolean vibrate) {
        put("vibrate", vibrate);
        return this;
    }
}
