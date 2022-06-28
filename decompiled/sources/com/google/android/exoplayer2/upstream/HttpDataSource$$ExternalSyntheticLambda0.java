package com.google.android.exoplayer2.upstream;

import com.google.android.exoplayer2.util.Predicate;
import com.google.android.exoplayer2.util.Util;
/* loaded from: classes3.dex */
public final /* synthetic */ class HttpDataSource$$ExternalSyntheticLambda0 implements Predicate {
    public static final /* synthetic */ HttpDataSource$$ExternalSyntheticLambda0 INSTANCE = new HttpDataSource$$ExternalSyntheticLambda0();

    private /* synthetic */ HttpDataSource$$ExternalSyntheticLambda0() {
    }

    @Override // com.google.android.exoplayer2.util.Predicate
    public final boolean evaluate(Object obj) {
        return Util.toLowerInvariant((String) obj);
    }
}
