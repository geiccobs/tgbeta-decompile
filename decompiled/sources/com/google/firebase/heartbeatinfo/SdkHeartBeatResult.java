package com.google.firebase.heartbeatinfo;
/* loaded from: classes3.dex */
public abstract class SdkHeartBeatResult implements Comparable<SdkHeartBeatResult> {
    public abstract long getMillis();

    public abstract String getSdkName();

    public static SdkHeartBeatResult create(String sdkName, long millis) {
        return new AutoValue_SdkHeartBeatResult(sdkName, millis);
    }

    public int compareTo(SdkHeartBeatResult sdkHeartBeatResult) {
        return getMillis() < sdkHeartBeatResult.getMillis() ? -1 : 1;
    }
}
