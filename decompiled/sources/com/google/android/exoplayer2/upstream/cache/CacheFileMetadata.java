package com.google.android.exoplayer2.upstream.cache;
/* loaded from: classes3.dex */
final class CacheFileMetadata {
    public final long lastTouchTimestamp;
    public final long length;

    public CacheFileMetadata(long length, long lastTouchTimestamp) {
        this.length = length;
        this.lastTouchTimestamp = lastTouchTimestamp;
    }
}
