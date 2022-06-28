package com.google.android.datatransport.runtime.backends;
/* loaded from: classes3.dex */
public abstract class BackendResponse {

    /* loaded from: classes3.dex */
    public enum Status {
        OK,
        TRANSIENT_ERROR,
        FATAL_ERROR,
        INVALID_PAYLOAD
    }

    public abstract long getNextRequestWaitMillis();

    public abstract Status getStatus();

    public static BackendResponse transientError() {
        return new AutoValue_BackendResponse(Status.TRANSIENT_ERROR, -1L);
    }

    public static BackendResponse fatalError() {
        return new AutoValue_BackendResponse(Status.FATAL_ERROR, -1L);
    }

    public static BackendResponse invalidPayload() {
        return new AutoValue_BackendResponse(Status.INVALID_PAYLOAD, -1L);
    }

    public static BackendResponse ok(long nextRequestWaitMillis) {
        return new AutoValue_BackendResponse(Status.OK, nextRequestWaitMillis);
    }
}
