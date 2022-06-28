package com.google.android.gms.dynamite;

import android.content.Context;
import com.google.android.gms.dynamite.DynamiteModule;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes3.dex */
final class zzd implements DynamiteModule.VersionPolicy {
    @Override // com.google.android.gms.dynamite.DynamiteModule.VersionPolicy
    public final DynamiteModule.VersionPolicy.zza zza(Context context, String str, DynamiteModule.VersionPolicy.zzb zzbVar) throws DynamiteModule.LoadingException {
        DynamiteModule.VersionPolicy.zza zzaVar = new DynamiteModule.VersionPolicy.zza();
        zzaVar.zza = zzbVar.zza(context, str);
        if (zzaVar.zza != 0) {
            zzaVar.zzc = -1;
        } else {
            zzaVar.zzb = zzbVar.zza(context, str, true);
            if (zzaVar.zzb != 0) {
                zzaVar.zzc = 1;
            }
        }
        return zzaVar;
    }
}
