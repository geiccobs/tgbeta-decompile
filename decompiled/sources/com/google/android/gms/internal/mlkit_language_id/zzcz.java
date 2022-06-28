package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.common.internal.GmsLogger;
import com.google.firebase.components.Component;
import com.google.firebase.components.ComponentContainer;
import com.google.firebase.components.Dependency;
import com.google.mlkit.common.model.RemoteModel;
import com.google.mlkit.common.sdkinternal.LazyInstanceMap;
import com.google.mlkit.common.sdkinternal.SharedPrefManager;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes3.dex */
public final class zzcz {
    private final zzcv zzc;
    private final RemoteModel zzd;
    private final SharedPrefManager zze;
    private static final GmsLogger zzb = new GmsLogger("ModelDownloadLogger", "");
    public static final Component<?> zza = Component.builder(zza.class).add(Dependency.required(zzcv.class)).add(Dependency.required(SharedPrefManager.class)).factory(zzdb.zza).build();

    private zzcz(zzcv zzcvVar, SharedPrefManager sharedPrefManager, RemoteModel remoteModel) {
        this.zzc = zzcvVar;
        this.zzd = remoteModel;
        this.zze = sharedPrefManager;
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes3.dex */
    public static class zza extends LazyInstanceMap<RemoteModel, zzcz> {
        private final zzcv zza;
        private final SharedPrefManager zzb;

        private zza(zzcv zzcvVar, SharedPrefManager sharedPrefManager) {
            this.zza = zzcvVar;
            this.zzb = sharedPrefManager;
        }

        @Override // com.google.mlkit.common.sdkinternal.LazyInstanceMap
        protected /* synthetic */ zzcz create(RemoteModel remoteModel) {
            return new zzcz(this.zza, this.zzb, remoteModel);
        }
    }

    public static final /* synthetic */ zza zza(ComponentContainer componentContainer) {
        return new zza((zzcv) componentContainer.get(zzcv.class), (SharedPrefManager) componentContainer.get(SharedPrefManager.class));
    }
}
