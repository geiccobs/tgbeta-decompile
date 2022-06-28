package com.google.android.gms.internal.mlkit_common;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
final class zzgm extends zzgl {
    /* JADX INFO: Access modifiers changed from: private */
    public zzgm() {
        super();
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzgl
    public final void zza(Object obj, long j) {
        zzb(obj, j).b_();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v2, types: [com.google.android.gms.internal.mlkit_common.zzfy] */
    @Override // com.google.android.gms.internal.mlkit_common.zzgl
    public final <E> void zza(Object obj, Object obj2, long j) {
        zzfy<E> zzb = zzb(obj, j);
        zzfy<E> zzb2 = zzb(obj2, j);
        int size = zzb.size();
        int size2 = zzb2.size();
        zzfy<E> zzfyVar = zzb;
        zzfyVar = zzb;
        if (size > 0 && size2 > 0) {
            boolean zza = zzb.zza();
            zzfy<E> zzfyVar2 = zzb;
            if (!zza) {
                zzfyVar2 = zzb.zzb(size2 + size);
            }
            zzfyVar2.addAll(zzb2);
            zzfyVar = zzfyVar2;
        }
        if (size > 0) {
            zzb2 = zzfyVar;
        }
        zzip.zza(obj, j, zzb2);
    }

    private static <E> zzfy<E> zzb(Object obj, long j) {
        return (zzfy) zzip.zzf(obj, j);
    }
}
