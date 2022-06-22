package com.google.firebase.datatransport;

import com.google.android.datatransport.TransportFactory;
import com.google.firebase.components.ComponentContainer;
import com.google.firebase.components.ComponentFactory;
/* loaded from: classes.dex */
public final /* synthetic */ class TransportRegistrar$$ExternalSyntheticLambda0 implements ComponentFactory {
    public static final /* synthetic */ TransportRegistrar$$ExternalSyntheticLambda0 INSTANCE = new TransportRegistrar$$ExternalSyntheticLambda0();

    private /* synthetic */ TransportRegistrar$$ExternalSyntheticLambda0() {
    }

    @Override // com.google.firebase.components.ComponentFactory
    public final Object create(ComponentContainer componentContainer) {
        TransportFactory lambda$getComponents$0;
        lambda$getComponents$0 = TransportRegistrar.lambda$getComponents$0(componentContainer);
        return lambda$getComponents$0;
    }
}
