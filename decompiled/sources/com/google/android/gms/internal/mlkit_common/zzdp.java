package com.google.android.gms.internal.mlkit_common;

import com.google.firebase.components.ComponentContainer;
import com.google.firebase.components.ComponentFactory;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
final /* synthetic */ class zzdp implements ComponentFactory {
    static final ComponentFactory zza = new zzdp();

    private zzdp() {
    }

    @Override // com.google.firebase.components.ComponentFactory
    public final Object create(ComponentContainer componentContainer) {
        return new zzdq((zzdo) componentContainer.get(zzdo.class));
    }
}
