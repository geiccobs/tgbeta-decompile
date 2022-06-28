package com.google.mlkit.common.internal;

import com.google.firebase.components.ComponentContainer;
import com.google.firebase.components.ComponentFactory;
import com.google.mlkit.common.sdkinternal.MlKitThreadPool;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
final /* synthetic */ class zza implements ComponentFactory {
    static final ComponentFactory zza = new zza();

    private zza() {
    }

    @Override // com.google.firebase.components.ComponentFactory
    public final Object create(ComponentContainer componentContainer) {
        return new MlKitThreadPool();
    }
}
