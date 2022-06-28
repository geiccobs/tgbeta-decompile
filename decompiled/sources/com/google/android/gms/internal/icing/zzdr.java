package com.google.android.gms.internal.icing;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
final class zzdr extends zzds {
    private zzdr() {
        super(null);
    }

    public /* synthetic */ zzdr(zzdp zzdpVar) {
        super(null);
    }

    @Override // com.google.android.gms.internal.icing.zzds
    public final void zza(Object obj, long j) {
        ((zzdg) zzfn.zzn(obj, j)).zzb();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v3, types: [com.google.android.gms.internal.icing.zzdg] */
    @Override // com.google.android.gms.internal.icing.zzds
    public final <E> void zzb(Object obj, Object obj2, long j) {
        zzdg<E> zzdgVar = (zzdg) zzfn.zzn(obj, j);
        zzdg<E> zzdgVar2 = (zzdg) zzfn.zzn(obj2, j);
        int size = zzdgVar.size();
        int size2 = zzdgVar2.size();
        zzdg<E> zzdgVar3 = zzdgVar;
        zzdgVar3 = zzdgVar;
        if (size > 0 && size2 > 0) {
            boolean zza = zzdgVar.zza();
            zzdg<E> zzdgVar4 = zzdgVar;
            if (!zza) {
                zzdgVar4 = zzdgVar.zze(size2 + size);
            }
            zzdgVar4.addAll(zzdgVar2);
            zzdgVar3 = zzdgVar4;
        }
        if (size > 0) {
            zzdgVar2 = zzdgVar3;
        }
        zzfn.zzo(obj, j, zzdgVar2);
    }
}
