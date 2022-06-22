package com.google.firebase.installations;

import com.google.firebase.components.ComponentContainer;
import com.google.firebase.components.ComponentFactory;
/* loaded from: classes.dex */
public final /* synthetic */ class FirebaseInstallationsRegistrar$$ExternalSyntheticLambda0 implements ComponentFactory {
    public static final /* synthetic */ FirebaseInstallationsRegistrar$$ExternalSyntheticLambda0 INSTANCE = new FirebaseInstallationsRegistrar$$ExternalSyntheticLambda0();

    private /* synthetic */ FirebaseInstallationsRegistrar$$ExternalSyntheticLambda0() {
    }

    @Override // com.google.firebase.components.ComponentFactory
    public final Object create(ComponentContainer componentContainer) {
        FirebaseInstallationsApi lambda$getComponents$0;
        lambda$getComponents$0 = FirebaseInstallationsRegistrar.lambda$getComponents$0(componentContainer);
        return lambda$getComponents$0;
    }
}
