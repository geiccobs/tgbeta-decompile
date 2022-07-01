package com.google.android.gms.dynamite;

import android.content.Context;
import com.google.android.gms.dynamite.DynamiteModule;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes.dex */
final class zzf implements DynamiteModule.VersionPolicy {
    @Override // com.google.android.gms.dynamite.DynamiteModule.VersionPolicy
    public final DynamiteModule.VersionPolicy.zza zza(Context context, String str, DynamiteModule.VersionPolicy.zzb zzbVar) throws DynamiteModule.LoadingException {
        DynamiteModule.VersionPolicy.zza zzaVar = new DynamiteModule.VersionPolicy.zza();
        zzaVar.zza = zzbVar.zza(context, str);
        int zza = zzbVar.zza(context, str, true);
        zzaVar.zzb = zza;
        int i = zzaVar.zza;
        if (i == 0 && zza == 0) {
            zzaVar.zzc = 0;
        } else if (i >= zza) {
            zzaVar.zzc = -1;
        } else {
            zzaVar.zzc = 1;
        }
        return zzaVar;
    }
}
