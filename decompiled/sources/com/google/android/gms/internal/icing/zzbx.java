package com.google.android.gms.internal.icing;

import java.util.NoSuchElementException;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
final class zzbx extends zzbz {
    final /* synthetic */ zzcf zza;
    private int zzb = 0;
    private final int zzc;

    public zzbx(zzcf zzcfVar) {
        this.zza = zzcfVar;
        this.zzc = zzcfVar.zzc();
    }

    @Override // java.util.Iterator, j$.util.Iterator
    public final boolean hasNext() {
        return this.zzb < this.zzc;
    }

    @Override // com.google.android.gms.internal.icing.zzcb
    public final byte zza() {
        int i = this.zzb;
        if (i < this.zzc) {
            this.zzb = i + 1;
            return this.zza.zzb(i);
        }
        throw new NoSuchElementException();
    }
}
