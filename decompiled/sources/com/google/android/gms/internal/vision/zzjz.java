package com.google.android.gms.internal.vision;

import java.util.List;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
final class zzjz extends zzju {
    /* JADX INFO: Access modifiers changed from: private */
    public zzjz() {
        super();
    }

    @Override // com.google.android.gms.internal.vision.zzju
    public final <L> List<L> zza(Object obj, long j) {
        zzjl zzc = zzc(obj, j);
        if (!zzc.zza()) {
            int size = zzc.size();
            zzjl zza = zzc.zza(size == 0 ? 10 : size << 1);
            zzma.zza(obj, j, zza);
            return zza;
        }
        return zzc;
    }

    @Override // com.google.android.gms.internal.vision.zzju
    public final void zzb(Object obj, long j) {
        zzc(obj, j).zzb();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v2, types: [com.google.android.gms.internal.vision.zzjl] */
    @Override // com.google.android.gms.internal.vision.zzju
    public final <E> void zza(Object obj, Object obj2, long j) {
        zzjl<E> zzc = zzc(obj, j);
        zzjl<E> zzc2 = zzc(obj2, j);
        int size = zzc.size();
        int size2 = zzc2.size();
        zzjl<E> zzjlVar = zzc;
        zzjlVar = zzc;
        if (size > 0 && size2 > 0) {
            boolean zza = zzc.zza();
            zzjl<E> zzjlVar2 = zzc;
            if (!zza) {
                zzjlVar2 = zzc.zza(size2 + size);
            }
            zzjlVar2.addAll(zzc2);
            zzjlVar = zzjlVar2;
        }
        if (size > 0) {
            zzc2 = zzjlVar;
        }
        zzma.zza(obj, j, zzc2);
    }

    private static <E> zzjl<E> zzc(Object obj, long j) {
        return (zzjl) zzma.zzf(obj, j);
    }
}
