package com.google.firebase.remoteconfig;
/* loaded from: classes3.dex */
public class FirebaseRemoteConfigFetchThrottledException extends FirebaseRemoteConfigException {
    private final long throttleEndTimeMillis;

    public FirebaseRemoteConfigFetchThrottledException(long throttleEndTimeMillis) {
        this("Fetch was throttled.", throttleEndTimeMillis);
    }

    public FirebaseRemoteConfigFetchThrottledException(String message, long throttledEndTimeInMillis) {
        super(message);
        this.throttleEndTimeMillis = throttledEndTimeInMillis;
    }

    public long getThrottleEndTimeMillis() {
        return this.throttleEndTimeMillis;
    }
}
