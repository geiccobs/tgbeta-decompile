package com.google.mlkit.common.internal;

import com.google.android.gms.internal.mlkit_common.zzds;
import com.google.firebase.components.ComponentContainer;
import com.google.firebase.components.ComponentFactory;
import com.google.mlkit.common.sdkinternal.Cleaner;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes.dex */
final /* synthetic */ class zze implements ComponentFactory {
    static final ComponentFactory zza = new zze();

    private zze() {
    }

    @Override // com.google.firebase.components.ComponentFactory
    public final Object create(ComponentContainer componentContainer) {
        return new Object((Cleaner) componentContainer.get(Cleaner.class), (zzds) componentContainer.get(zzds.class)) { // from class: com.google.mlkit.common.sdkinternal.CloseGuard$Factory
        };
    }
}
