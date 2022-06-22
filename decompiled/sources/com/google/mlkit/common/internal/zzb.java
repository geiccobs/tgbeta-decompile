package com.google.mlkit.common.internal;

import com.google.firebase.components.ComponentContainer;
import com.google.firebase.components.ComponentFactory;
import com.google.mlkit.common.sdkinternal.MlKitContext;
import com.google.mlkit.common.sdkinternal.model.ModelFileHelper;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes.dex */
final /* synthetic */ class zzb implements ComponentFactory {
    static final ComponentFactory zza = new zzb();

    private zzb() {
    }

    @Override // com.google.firebase.components.ComponentFactory
    public final Object create(ComponentContainer componentContainer) {
        return new ModelFileHelper((MlKitContext) componentContainer.get(MlKitContext.class));
    }
}
