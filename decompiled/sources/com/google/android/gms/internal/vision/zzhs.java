package com.google.android.gms.internal.vision;

import java.util.NoSuchElementException;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes.dex */
public final class zzhs extends zzhu {
    private int zza = 0;
    private final int zzb;
    private final /* synthetic */ zzht zzc;

    public zzhs(zzht zzhtVar) {
        this.zzc = zzhtVar;
        this.zzb = zzhtVar.zza();
    }

    @Override // java.util.Iterator, j$.util.Iterator
    public final boolean hasNext() {
        return this.zza < this.zzb;
    }

    @Override // com.google.android.gms.internal.vision.zzhy
    public final byte zza() {
        int i = this.zza;
        if (i >= this.zzb) {
            throw new NoSuchElementException();
        }
        this.zza = i + 1;
        return this.zzc.zzb(i);
    }
}
