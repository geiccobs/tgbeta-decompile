package com.google.android.exoplayer2.upstream.cache;

import java.util.Comparator;
/* loaded from: classes3.dex */
public final /* synthetic */ class LeastRecentlyUsedCacheEvictor$$ExternalSyntheticLambda0 implements Comparator {
    public static final /* synthetic */ LeastRecentlyUsedCacheEvictor$$ExternalSyntheticLambda0 INSTANCE = new LeastRecentlyUsedCacheEvictor$$ExternalSyntheticLambda0();

    private /* synthetic */ LeastRecentlyUsedCacheEvictor$$ExternalSyntheticLambda0() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int compare;
        compare = LeastRecentlyUsedCacheEvictor.compare((CacheSpan) obj, (CacheSpan) obj2);
        return compare;
    }
}
