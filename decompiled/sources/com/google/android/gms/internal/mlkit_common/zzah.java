package com.google.android.gms.internal.mlkit_common;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes.dex */
public final class zzah<E> extends zzad<E> {
    static final zzad<Object> zza = new zzah(new Object[0], 0);
    private final transient Object[] zzb;
    private final transient int zzc;

    public zzah(Object[] objArr, int i) {
        this.zzb = objArr;
        this.zzc = i;
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzac
    public final int zzc() {
        return 0;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public final int size() {
        return this.zzc;
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzac
    public final Object[] zzb() {
        return this.zzb;
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzac
    final int zzd() {
        return this.zzc;
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzad, com.google.android.gms.internal.mlkit_common.zzac
    final int zza(Object[] objArr, int i) {
        System.arraycopy(this.zzb, 0, objArr, 0, this.zzc);
        return this.zzc + 0;
    }

    @Override // java.util.List
    public final E get(int i) {
        zzy.zza(i, this.zzc);
        return (E) this.zzb[i];
    }
}
