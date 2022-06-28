package com.google.android.exoplayer2.util;

import com.google.android.exoplayer2.util.SlidingPercentile;
import java.util.Comparator;
/* loaded from: classes3.dex */
public final /* synthetic */ class SlidingPercentile$$ExternalSyntheticLambda1 implements Comparator {
    public static final /* synthetic */ SlidingPercentile$$ExternalSyntheticLambda1 INSTANCE = new SlidingPercentile$$ExternalSyntheticLambda1();

    private /* synthetic */ SlidingPercentile$$ExternalSyntheticLambda1() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int compare;
        compare = Float.compare(((SlidingPercentile.Sample) obj).value, ((SlidingPercentile.Sample) obj2).value);
        return compare;
    }
}
