package com.google.android.gms.internal.mlkit_common;

import java.util.List;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public final class zzai extends zzad<E> {
    private final transient int zza;
    private final transient int zzb;
    private final /* synthetic */ zzad zzc;

    public zzai(zzad zzadVar, int i, int i2) {
        this.zzc = zzadVar;
        this.zza = i;
        this.zzb = i2;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public final int size() {
        return this.zzb;
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzac
    public final Object[] zzb() {
        return this.zzc.zzb();
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzac
    public final int zzc() {
        return this.zzc.zzc() + this.zza;
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzac
    final int zzd() {
        return this.zzc.zzc() + this.zza + this.zzb;
    }

    @Override // java.util.List
    public final E get(int i) {
        zzy.zza(i, this.zzb);
        return this.zzc.get(i + this.zza);
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzad
    public final zzad<E> zza(int i, int i2) {
        zzy.zza(i, i2, this.zzb);
        zzad zzadVar = this.zzc;
        int i3 = this.zza;
        return (zzad) zzadVar.subList(i + i3, i2 + i3);
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzad, java.util.List
    public final /* synthetic */ List subList(int i, int i2) {
        return subList(i, i2);
    }
}
