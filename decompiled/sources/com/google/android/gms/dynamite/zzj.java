package com.google.android.gms.dynamite;

import android.content.Context;
import com.google.android.gms.dynamite.DynamiteModule;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes.dex */
final class zzj implements DynamiteModule.VersionPolicy {
    @Override // com.google.android.gms.dynamite.DynamiteModule.VersionPolicy
    public final DynamiteModule.VersionPolicy.zza zza(Context context, String str, DynamiteModule.VersionPolicy.zzb zzbVar) throws DynamiteModule.LoadingException {
        DynamiteModule.VersionPolicy.zza zzaVar = new DynamiteModule.VersionPolicy.zza();
        int zza = zzbVar.zza(context, str);
        zzaVar.zza = zza;
        if (zza != 0) {
            zzaVar.zzb = zzbVar.zza(context, str, false);
        } else {
            zzaVar.zzb = zzbVar.zza(context, str, true);
        }
        int i = zzaVar.zza;
        if (i == 0 && zzaVar.zzb == 0) {
            zzaVar.zzc = 0;
        } else if (zzaVar.zzb >= i) {
            zzaVar.zzc = 1;
        } else {
            zzaVar.zzc = -1;
        }
        return zzaVar;
    }
}
