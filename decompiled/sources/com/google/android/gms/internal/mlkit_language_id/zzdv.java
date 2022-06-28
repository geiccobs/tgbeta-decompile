package com.google.android.gms.internal.mlkit_language_id;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes3.dex */
public final class zzdv {
    private final zzea zza;
    private final byte[] zzb;

    private zzdv(int i) {
        byte[] bArr = new byte[i];
        this.zzb = bArr;
        this.zza = zzea.zza(bArr);
    }

    public final zzdn zza() {
        this.zza.zzb();
        return new zzdx(this.zzb);
    }

    public final zzea zzb() {
        return this.zza;
    }

    public /* synthetic */ zzdv(int i, zzdm zzdmVar) {
        this(i);
    }
}
