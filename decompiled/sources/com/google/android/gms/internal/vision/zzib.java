package com.google.android.gms.internal.vision;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
public final class zzib {
    private final zzii zza;
    private final byte[] zzb;

    private zzib(int i) {
        byte[] bArr = new byte[i];
        this.zzb = bArr;
        this.zza = zzii.zza(bArr);
    }

    public final zzht zza() {
        this.zza.zzb();
        return new zzid(this.zzb);
    }

    public final zzii zzb() {
        return this.zza;
    }

    public /* synthetic */ zzib(int i, zzhs zzhsVar) {
        this(i);
    }
}
