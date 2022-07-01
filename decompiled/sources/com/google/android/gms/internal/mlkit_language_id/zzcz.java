package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.common.internal.GmsLogger;
import com.google.firebase.components.Component;
import com.google.firebase.components.ComponentContainer;
import com.google.firebase.components.Dependency;
import com.google.mlkit.common.sdkinternal.LazyInstanceMap;
import com.google.mlkit.common.sdkinternal.SharedPrefManager;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzcz {
    public static final Component<?> zza = Component.builder(zza.class).add(Dependency.required(zzcv.class)).add(Dependency.required(SharedPrefManager.class)).factory(zzdb.zza).build();

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static class zza extends LazyInstanceMap<Object, zzcz> {
        private zza(zzcv zzcvVar, SharedPrefManager sharedPrefManager) {
        }
    }

    public static final /* synthetic */ zza zza(ComponentContainer componentContainer) {
        return new zza((zzcv) componentContainer.get(zzcv.class), (SharedPrefManager) componentContainer.get(SharedPrefManager.class));
    }

    static {
        new GmsLogger("ModelDownloadLogger", "");
    }
}
