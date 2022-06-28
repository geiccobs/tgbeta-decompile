package com.google.firebase.appindexing.builders;

import com.google.firebase.appindexing.internal.zzae;
import java.util.Calendar;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class StopwatchBuilder extends IndexableBuilder<StopwatchBuilder> {
    public static final String PAUSED = "Paused";
    public static final String STARTED = "Started";
    public static final String UNKNOWN = "Unknown";

    public StopwatchBuilder() {
        super("Stopwatch");
    }

    public StopwatchBuilder setElapsedTime(long elapsedTimeInMilliseconds) {
        put("elapsedTime", elapsedTimeInMilliseconds);
        return this;
    }

    public StopwatchBuilder setLaps(StopwatchLapBuilder... laps) {
        put("laps", laps);
        return this;
    }

    public StopwatchBuilder setStartTime(Calendar startTime) {
        put("startTime", zzae.zza(startTime));
        return this;
    }

    public StopwatchBuilder setStopwatchStatus(String status) {
        if ("Started".equals(status) || "Paused".equals(status) || "Unknown".equals(status)) {
            put("stopwatchStatus", status);
            return this;
        }
        String valueOf = String.valueOf(status);
        throw new IllegalArgumentException(valueOf.length() != 0 ? "Invalid stopwatch status ".concat(valueOf) : new String("Invalid stopwatch status "));
    }
}
