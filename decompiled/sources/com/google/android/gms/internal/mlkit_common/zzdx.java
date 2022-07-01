package com.google.android.gms.internal.mlkit_common;

import com.google.android.gms.common.internal.GmsLogger;
import com.google.firebase.components.Component;
import com.google.firebase.components.ComponentContainer;
import com.google.firebase.components.Dependency;
import com.google.mlkit.common.sdkinternal.LazyInstanceMap;
import com.google.mlkit.common.sdkinternal.SharedPrefManager;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes.dex */
public final class zzdx {
    public static final Component<?> zza = Component.builder(zza.class).add(Dependency.required(zzds.class)).add(Dependency.required(SharedPrefManager.class)).factory(zzdw.zza).build();

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes.dex */
    public static class zza extends LazyInstanceMap<Object, zzdx> {
        private zza(zzds zzdsVar, SharedPrefManager sharedPrefManager) {
        }
    }

    public static final /* synthetic */ zza zza(ComponentContainer componentContainer) {
        return new zza((zzds) componentContainer.get(zzds.class), (SharedPrefManager) componentContainer.get(SharedPrefManager.class));
    }

    static {
        new GmsLogger("ModelDownloadLogger", "");
    }
}
