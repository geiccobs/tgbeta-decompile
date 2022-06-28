package com.google.firebase.datatransport;

import android.content.Context;
import com.google.android.datatransport.runtime.TransportRuntime;
import com.google.firebase.components.ComponentContainer;
import com.google.firebase.components.ComponentFactory;
/* loaded from: classes3.dex */
public final /* synthetic */ class TransportRegistrar$$ExternalSyntheticLambda0 implements ComponentFactory {
    public static final /* synthetic */ TransportRegistrar$$ExternalSyntheticLambda0 INSTANCE = new TransportRegistrar$$ExternalSyntheticLambda0();

    private /* synthetic */ TransportRegistrar$$ExternalSyntheticLambda0() {
    }

    @Override // com.google.firebase.components.ComponentFactory
    public final Object create(ComponentContainer componentContainer) {
        return TransportRuntime.initialize((Context) componentContainer.get(Context.class));
    }
}
