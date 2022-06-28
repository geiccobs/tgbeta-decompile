package com.google.firebase.appindexing.builders;

import com.google.firebase.appindexing.internal.zzae;
import java.util.Calendar;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public class AlarmInstanceBuilder extends IndexableBuilder<AlarmInstanceBuilder> {
    public static final String DISMISSED = "Dismissed";
    public static final String FIRED = "Fired";
    public static final String MISSED = "Missed";
    public static final String SCHEDULED = "Scheduled";
    public static final String SNOOZED = "Snoozed";
    public static final String UNKNOWN = "Unknown";

    public AlarmInstanceBuilder() {
        super("AlarmInstance");
    }

    public AlarmInstanceBuilder setAlarmStatus(String status) {
        if (FIRED.equals(status) || SNOOZED.equals(status) || "Missed".equals(status) || DISMISSED.equals(status) || SCHEDULED.equals(status) || "Unknown".equals(status)) {
            return put("alarmStatus", status);
        }
        String valueOf = String.valueOf(status);
        throw new IllegalArgumentException(valueOf.length() != 0 ? "Invalid alarm status ".concat(valueOf) : new String("Invalid alarm status "));
    }

    public AlarmInstanceBuilder setScheduledTime(Calendar scheduledTime) {
        return put("scheduledTime", zzae.zza(scheduledTime));
    }
}
