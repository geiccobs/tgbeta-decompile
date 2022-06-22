package com.google.mlkit.common.sdkinternal;

import com.google.firebase.components.ComponentContainer;
import com.google.firebase.components.ComponentFactory;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes.dex */
final /* synthetic */ class zzn implements ComponentFactory {
    static final ComponentFactory zza = new zzn();

    private zzn() {
    }

    @Override // com.google.firebase.components.ComponentFactory
    public final Object create(ComponentContainer componentContainer) {
        return SharedPrefManager.zza(componentContainer);
    }
}
