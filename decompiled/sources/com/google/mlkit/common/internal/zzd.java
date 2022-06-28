package com.google.mlkit.common.internal;

import com.google.firebase.components.ComponentContainer;
import com.google.firebase.components.ComponentFactory;
import com.google.mlkit.common.model.RemoteModelManager;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
final /* synthetic */ class zzd implements ComponentFactory {
    static final ComponentFactory zza = new zzd();

    private zzd() {
    }

    @Override // com.google.firebase.components.ComponentFactory
    public final Object create(ComponentContainer componentContainer) {
        return new RemoteModelManager(componentContainer.setOf(RemoteModelManager.RemoteModelManagerRegistration.class));
    }
}
