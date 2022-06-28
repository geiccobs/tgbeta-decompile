package com.google.android.gms.internal.wearable;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzck extends zzcl {
    private zzck() {
        super(null);
    }

    public /* synthetic */ zzck(zzci zzciVar) {
        super(null);
    }

    @Override // com.google.android.gms.internal.wearable.zzcl
    public final void zza(Object obj, long j) {
        ((zzbz) zzeg.zzn(obj, j)).zzb();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v3, types: [com.google.android.gms.internal.wearable.zzbz] */
    @Override // com.google.android.gms.internal.wearable.zzcl
    public final <E> void zzb(Object obj, Object obj2, long j) {
        zzbz<E> zzbzVar = (zzbz) zzeg.zzn(obj, j);
        zzbz<E> zzbzVar2 = (zzbz) zzeg.zzn(obj2, j);
        int size = zzbzVar.size();
        int size2 = zzbzVar2.size();
        zzbz<E> zzbzVar3 = zzbzVar;
        zzbzVar3 = zzbzVar;
        if (size > 0 && size2 > 0) {
            boolean zza = zzbzVar.zza();
            zzbz<E> zzbzVar4 = zzbzVar;
            if (!zza) {
                zzbzVar4 = zzbzVar.zze(size2 + size);
            }
            zzbzVar4.addAll(zzbzVar2);
            zzbzVar3 = zzbzVar4;
        }
        if (size > 0) {
            zzbzVar2 = zzbzVar3;
        }
        zzeg.zzo(obj, j, zzbzVar2);
    }
}
