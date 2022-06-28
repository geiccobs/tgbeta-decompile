package com.google.android.gms.internal.mlkit_common;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public abstract class zzfb {
    private int zza;
    private int zzb;
    private boolean zzc;

    public static zzfb zza(byte[] bArr, int i, int i2, boolean z) {
        zzfd zzfdVar = new zzfd(bArr, i2);
        try {
            zzfdVar.zza(i2);
            return zzfdVar;
        } catch (zzgb e) {
            throw new IllegalArgumentException(e);
        }
    }

    public abstract int zza();

    public abstract int zza(int i) throws zzgb;

    /* JADX INFO: Access modifiers changed from: private */
    public zzfb() {
        this.zza = 100;
        this.zzb = Integer.MAX_VALUE;
        this.zzc = false;
    }
}
