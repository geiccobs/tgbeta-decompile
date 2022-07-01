package com.google.android.datatransport;

import com.google.auto.value.AutoValue;
@AutoValue
/* loaded from: classes.dex */
public abstract class Event<T> {
    public abstract Integer getCode();

    public abstract T getPayload();

    public abstract Priority getPriority();

    public static <T> Event<T> ofTelemetry(T t) {
        return new AutoValue_Event(null, t, Priority.VERY_LOW);
    }
}
