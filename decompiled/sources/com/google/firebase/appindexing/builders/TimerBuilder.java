package com.google.firebase.appindexing.builders;

import com.google.firebase.appindexing.internal.zzae;
import java.util.Calendar;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class TimerBuilder extends IndexableBuilder<TimerBuilder> {
    public static final String EXPIRED = "Expired";
    public static final String MISSED = "Missed";
    public static final String PAUSED = "Paused";
    public static final String RESET = "Reset";
    public static final String STARTED = "Started";
    public static final String UNKNOWN = "Unknown";

    public TimerBuilder() {
        super("Timer");
    }

    public TimerBuilder setExpireTime(Calendar wallClockExpirationTime) {
        put("expireTime", zzae.zza(wallClockExpirationTime));
        return this;
    }

    public TimerBuilder setIdentifier(String identifier) {
        put("identifier", identifier);
        return this;
    }

    public TimerBuilder setLength(long lengthInMilliseconds) {
        put("length", lengthInMilliseconds);
        return this;
    }

    public TimerBuilder setMessage(String message) {
        put("message", message);
        return this;
    }

    public TimerBuilder setRemainingTime(long remainingTimeInMilliseconds) {
        put("remainingTime", remainingTimeInMilliseconds);
        return this;
    }

    public TimerBuilder setRingtone(String ringtone) {
        put("ringtone", ringtone);
        return this;
    }

    public TimerBuilder setTimerStatus(String status) {
        if ("Started".equals(status) || "Paused".equals(status) || EXPIRED.equals(status) || "Missed".equals(status) || RESET.equals(status) || "Unknown".equals(status)) {
            put("timerStatus", status);
            return this;
        }
        String valueOf = String.valueOf(status);
        throw new IllegalArgumentException(valueOf.length() != 0 ? "Invalid timer status ".concat(valueOf) : new String("Invalid timer status "));
    }

    public TimerBuilder setVibrate(boolean vibrate) {
        put("vibrate", vibrate);
        return this;
    }
}
