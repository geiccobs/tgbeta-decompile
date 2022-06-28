package com.google.android.gms.internal.wearable;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzaw extends zzax {
    private final byte[] zza;
    private int zzc;
    private int zzd = Integer.MAX_VALUE;
    private int zzb = 0;

    public /* synthetic */ zzaw(byte[] bArr, int i, int i2, boolean z, zzav zzavVar) {
        super(null);
        this.zza = bArr;
    }

    public final int zza(int i) throws zzcc {
        int i2 = this.zzd;
        this.zzd = 0;
        int i3 = this.zzb + this.zzc;
        this.zzb = i3;
        if (i3 > 0) {
            this.zzc = i3;
            this.zzb = 0;
        } else {
            this.zzc = 0;
        }
        return i2;
    }
}
