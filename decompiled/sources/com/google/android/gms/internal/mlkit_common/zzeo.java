package com.google.android.gms.internal.mlkit_common;

import java.util.NoSuchElementException;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public final class zzeo extends zzeq {
    private int zza = 0;
    private final int zzb;
    private final /* synthetic */ zzep zzc;

    public zzeo(zzep zzepVar) {
        this.zzc = zzepVar;
        this.zzb = zzepVar.zza();
    }

    @Override // java.util.Iterator, j$.util.Iterator
    public final boolean hasNext() {
        return this.zza < this.zzb;
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzeu
    public final byte zza() {
        int i = this.zza;
        if (i >= this.zzb) {
            throw new NoSuchElementException();
        }
        this.zza = i + 1;
        return this.zzc.zzb(i);
    }
}
