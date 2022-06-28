package com.google.android.gms.internal.mlkit_common;

import android.content.Context;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public final class zzh {
    public static final zzh zza = zza().zzb().zzc();
    private static final zzh zzb = zza().zza().zzc();
    private final boolean zzc;
    private final boolean zzd;
    private final zzad<zzo> zze;

    private zzh(boolean z, boolean z2, zzad<zzo> zzadVar) {
        this.zzc = z;
        this.zzd = false;
        this.zze = zzadVar;
    }

    private static zzg zza() {
        return new zzg(null);
    }

    public /* synthetic */ zzh(boolean z, boolean z2, zzad zzadVar, zze zzeVar) {
        this(z, false, zzadVar);
    }

    public static /* synthetic */ boolean zza(zzh zzhVar) {
        return zzhVar.zzc;
    }

    public static /* synthetic */ int zza(zzh zzhVar, Context context, zzr zzrVar) {
        zzad<zzo> zzadVar = zzhVar.zze;
        int size = zzadVar.size();
        int i = 0;
        while (i < size) {
            zzo zzoVar = zzadVar.get(i);
            i++;
            switch (zze.zza[zzoVar.zza(context, zzrVar, zzhVar.zzc) - 1]) {
                case 1:
                    return zzq.zza;
                case 2:
                    return zzq.zzb;
            }
        }
        return zzq.zzc;
    }

    public static /* synthetic */ boolean zzb(zzh zzhVar) {
        return zzhVar.zzd;
    }
}
