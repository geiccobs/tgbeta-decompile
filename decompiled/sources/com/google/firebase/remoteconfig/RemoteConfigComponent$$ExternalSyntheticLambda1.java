package com.google.firebase.remoteconfig;

import com.google.firebase.analytics.connector.AnalyticsConnector;
import com.google.firebase.inject.Provider;
/* loaded from: classes.dex */
public final /* synthetic */ class RemoteConfigComponent$$ExternalSyntheticLambda1 implements Provider {
    public static final /* synthetic */ RemoteConfigComponent$$ExternalSyntheticLambda1 INSTANCE = new RemoteConfigComponent$$ExternalSyntheticLambda1();

    private /* synthetic */ RemoteConfigComponent$$ExternalSyntheticLambda1() {
    }

    @Override // com.google.firebase.inject.Provider
    public final Object get() {
        AnalyticsConnector lambda$getFetchHandler$0;
        lambda$getFetchHandler$0 = RemoteConfigComponent.lambda$getFetchHandler$0();
        return lambda$getFetchHandler$0;
    }
}
