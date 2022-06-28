package com.google.firebase.appindexing.builders;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public class StopwatchLapBuilder extends IndexableBuilder<StopwatchLapBuilder> {
    public StopwatchLapBuilder() {
        super("StopwatchLap");
    }

    public StopwatchLapBuilder setAccumulatedTime(long accumulatedTimeInMilliseconds) {
        return put("accumulatedTime", accumulatedTimeInMilliseconds);
    }

    public StopwatchLapBuilder setElapsedTime(long elapsedTimeInMilliseconds) {
        return put("elapsedTime", elapsedTimeInMilliseconds);
    }
}
