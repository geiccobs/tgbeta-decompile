package com.google.android.gms.internal.mlkit_common;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public final class zzex {
    private final zzfc zza;
    private final byte[] zzb;

    private zzex(int i) {
        byte[] bArr = new byte[i];
        this.zzb = bArr;
        this.zza = zzfc.zza(bArr);
    }

    public final zzep zza() {
        this.zza.zzb();
        return new zzez(this.zzb);
    }

    public final zzfc zzb() {
        return this.zza;
    }

    public /* synthetic */ zzex(int i, zzeo zzeoVar) {
        this(i);
    }
}
