package com.google.android.gms.internal.mlkit_language_id;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes3.dex */
final class zzfk extends zzfj {
    /* JADX INFO: Access modifiers changed from: private */
    public zzfk() {
        super();
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzfj
    public final void zza(Object obj, long j) {
        zzb(obj, j).b_();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v2, types: [com.google.android.gms.internal.mlkit_language_id.zzew] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzfj
    public final <E> void zza(Object obj, Object obj2, long j) {
        zzew<E> zzb = zzb(obj, j);
        zzew<E> zzb2 = zzb(obj2, j);
        int size = zzb.size();
        int size2 = zzb2.size();
        zzew<E> zzewVar = zzb;
        zzewVar = zzb;
        if (size > 0 && size2 > 0) {
            boolean zza = zzb.zza();
            zzew<E> zzewVar2 = zzb;
            if (!zza) {
                zzewVar2 = zzb.zzb(size2 + size);
            }
            zzewVar2.addAll(zzb2);
            zzewVar = zzewVar2;
        }
        if (size > 0) {
            zzb2 = zzewVar;
        }
        zzhn.zza(obj, j, zzb2);
    }

    private static <E> zzew<E> zzb(Object obj, long j) {
        return (zzew) zzhn.zzf(obj, j);
    }
}
