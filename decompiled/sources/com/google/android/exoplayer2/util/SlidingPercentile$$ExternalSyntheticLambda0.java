package com.google.android.exoplayer2.util;

import com.google.android.exoplayer2.util.SlidingPercentile;
import java.util.Comparator;
/* loaded from: classes3.dex */
public final /* synthetic */ class SlidingPercentile$$ExternalSyntheticLambda0 implements Comparator {
    public static final /* synthetic */ SlidingPercentile$$ExternalSyntheticLambda0 INSTANCE = new SlidingPercentile$$ExternalSyntheticLambda0();

    private /* synthetic */ SlidingPercentile$$ExternalSyntheticLambda0() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        return SlidingPercentile.lambda$static$0((SlidingPercentile.Sample) obj, (SlidingPercentile.Sample) obj2);
    }
}
