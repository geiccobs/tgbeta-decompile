package com.google.firebase.remoteconfig;

import com.google.firebase.components.ComponentContainer;
import com.google.firebase.components.ComponentFactory;
/* loaded from: classes.dex */
public final /* synthetic */ class RemoteConfigRegistrar$$ExternalSyntheticLambda0 implements ComponentFactory {
    public static final /* synthetic */ RemoteConfigRegistrar$$ExternalSyntheticLambda0 INSTANCE = new RemoteConfigRegistrar$$ExternalSyntheticLambda0();

    private /* synthetic */ RemoteConfigRegistrar$$ExternalSyntheticLambda0() {
    }

    @Override // com.google.firebase.components.ComponentFactory
    public final Object create(ComponentContainer componentContainer) {
        RemoteConfigComponent lambda$getComponents$0;
        lambda$getComponents$0 = RemoteConfigRegistrar.lambda$getComponents$0(componentContainer);
        return lambda$getComponents$0;
    }
}
