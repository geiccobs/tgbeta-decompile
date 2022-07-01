package com.google.android.datatransport.cct;

import com.google.android.datatransport.cct.CctTransportBackend;
import com.google.android.datatransport.runtime.retries.RetryStrategy;
/* loaded from: classes.dex */
public final /* synthetic */ class CctTransportBackend$$ExternalSyntheticLambda1 implements RetryStrategy {
    public static final /* synthetic */ CctTransportBackend$$ExternalSyntheticLambda1 INSTANCE = new CctTransportBackend$$ExternalSyntheticLambda1();

    private /* synthetic */ CctTransportBackend$$ExternalSyntheticLambda1() {
    }

    @Override // com.google.android.datatransport.runtime.retries.RetryStrategy
    public final Object shouldRetry(Object obj, Object obj2) {
        CctTransportBackend.HttpRequest lambda$send$0;
        lambda$send$0 = CctTransportBackend.lambda$send$0((CctTransportBackend.HttpRequest) obj, (CctTransportBackend.HttpResponse) obj2);
        return lambda$send$0;
    }
}
