package com.google.android.gms.internal.clearcut;
/* loaded from: classes.dex */
final class zzdb extends zzcy {
    /* JADX INFO: Access modifiers changed from: private */
    public zzdb() {
        super();
    }

    private static <E> zzcn<E> zzc(Object obj, long j) {
        return (zzcn) zzfd.zzo(obj, j);
    }

    @Override // com.google.android.gms.internal.clearcut.zzcy
    public final void zza(Object obj, long j) {
        zzc(obj, j).zzv();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v2, types: [java.util.List] */
    @Override // com.google.android.gms.internal.clearcut.zzcy
    public final <E> void zza(Object obj, Object obj2, long j) {
        zzcn<E> zzc = zzc(obj, j);
        zzcn<E> zzc2 = zzc(obj2, j);
        int size = zzc.size();
        int size2 = zzc2.size();
        zzcn<E> zzcnVar = zzc;
        zzcnVar = zzc;
        if (size > 0 && size2 > 0) {
            boolean zzu = zzc.zzu();
            zzcn<E> zzcnVar2 = zzc;
            if (!zzu) {
                zzcnVar2 = zzc.zzi(size2 + size);
            }
            zzcnVar2.addAll(zzc2);
            zzcnVar = zzcnVar2;
        }
        if (size > 0) {
            zzc2 = zzcnVar;
        }
        zzfd.zza(obj, j, zzc2);
    }
}
