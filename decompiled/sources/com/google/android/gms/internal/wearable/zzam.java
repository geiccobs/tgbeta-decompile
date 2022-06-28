package com.google.android.gms.internal.wearable;

import java.util.NoSuchElementException;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzam extends zzao {
    final /* synthetic */ zzau zza;
    private int zzb = 0;
    private final int zzc;

    public zzam(zzau zzauVar) {
        this.zza = zzauVar;
        this.zzc = zzauVar.zzc();
    }

    @Override // java.util.Iterator, j$.util.Iterator
    public final boolean hasNext() {
        return this.zzb < this.zzc;
    }

    @Override // com.google.android.gms.internal.wearable.zzaq
    public final byte zza() {
        int i = this.zzb;
        if (i < this.zzc) {
            this.zzb = i + 1;
            return this.zza.zzb(i);
        }
        throw new NoSuchElementException();
    }
}
