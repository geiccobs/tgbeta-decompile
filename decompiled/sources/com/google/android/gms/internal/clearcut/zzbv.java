package com.google.android.gms.internal.clearcut;

import com.google.android.gms.internal.clearcut.zzcg;
import java.io.IOException;
import java.util.Map;
/* loaded from: classes.dex */
final class zzbv extends zzbu<zzcg.zze> {
    @Override // com.google.android.gms.internal.clearcut.zzbu
    public final int zza(Map.Entry<?, ?> entry) {
        return ((zzcg.zze) entry.getKey()).number;
    }

    @Override // com.google.android.gms.internal.clearcut.zzbu
    public final zzby<zzcg.zze> zza(Object obj) {
        return ((zzcg.zzd) obj).zzjv;
    }

    @Override // com.google.android.gms.internal.clearcut.zzbu
    public final void zza(zzfr zzfrVar, Map.Entry<?, ?> entry) throws IOException {
        zzcg.zze zzeVar = (zzcg.zze) entry.getKey();
        switch (zzbw.zzgq[zzeVar.zzjx.ordinal()]) {
            case 1:
                zzfrVar.zza(zzeVar.number, ((Double) entry.getValue()).doubleValue());
                return;
            case 2:
                zzfrVar.zza(zzeVar.number, ((Float) entry.getValue()).floatValue());
                return;
            case 3:
                zzfrVar.zzi(zzeVar.number, ((Long) entry.getValue()).longValue());
                return;
            case 4:
                zzfrVar.zza(zzeVar.number, ((Long) entry.getValue()).longValue());
                return;
            case 5:
                zzfrVar.zzc(zzeVar.number, ((Integer) entry.getValue()).intValue());
                return;
            case 6:
                zzfrVar.zzc(zzeVar.number, ((Long) entry.getValue()).longValue());
                return;
            case 7:
                zzfrVar.zzf(zzeVar.number, ((Integer) entry.getValue()).intValue());
                return;
            case 8:
                zzfrVar.zzb(zzeVar.number, ((Boolean) entry.getValue()).booleanValue());
                return;
            case 9:
                zzfrVar.zzd(zzeVar.number, ((Integer) entry.getValue()).intValue());
                return;
            case 10:
                zzfrVar.zzm(zzeVar.number, ((Integer) entry.getValue()).intValue());
                return;
            case 11:
                zzfrVar.zzj(zzeVar.number, ((Long) entry.getValue()).longValue());
                return;
            case 12:
                zzfrVar.zze(zzeVar.number, ((Integer) entry.getValue()).intValue());
                return;
            case 13:
                zzfrVar.zzb(zzeVar.number, ((Long) entry.getValue()).longValue());
                return;
            case 14:
                zzfrVar.zzc(zzeVar.number, ((Integer) entry.getValue()).intValue());
                return;
            case 15:
                zzfrVar.zza(zzeVar.number, (zzbb) entry.getValue());
                return;
            case 16:
                zzfrVar.zza(zzeVar.number, (String) entry.getValue());
                return;
            case 17:
                zzfrVar.zzb(zzeVar.number, entry.getValue(), zzea.zzcm().zze(entry.getValue().getClass()));
                return;
            case 18:
                zzfrVar.zza(zzeVar.number, entry.getValue(), zzea.zzcm().zze(entry.getValue().getClass()));
                return;
            default:
                return;
        }
    }

    @Override // com.google.android.gms.internal.clearcut.zzbu
    public final void zza(Object obj, zzby<zzcg.zze> zzbyVar) {
        ((zzcg.zzd) obj).zzjv = zzbyVar;
    }

    @Override // com.google.android.gms.internal.clearcut.zzbu
    public final zzby<zzcg.zze> zzb(Object obj) {
        zzby<zzcg.zze> zza = zza(obj);
        if (zza.isImmutable()) {
            zzby<zzcg.zze> zzbyVar = (zzby) zza.clone();
            zza(obj, zzbyVar);
            return zzbyVar;
        }
        return zza;
    }

    @Override // com.google.android.gms.internal.clearcut.zzbu
    public final void zzc(Object obj) {
        zza(obj).zzv();
    }

    @Override // com.google.android.gms.internal.clearcut.zzbu
    public final boolean zze(zzdo zzdoVar) {
        return zzdoVar instanceof zzcg.zzd;
    }
}
