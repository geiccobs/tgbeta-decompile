package com.google.android.gms.internal.vision;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes.dex */
public abstract class zzif {
    public static long zza(long j) {
        return (-(j & 1)) ^ (j >>> 1);
    }

    public static zzif zza(byte[] bArr, int i, int i2, boolean z) {
        zzih zzihVar = new zzih(bArr, i2);
        try {
            zzihVar.zzc(i2);
            return zzihVar;
        } catch (zzjk e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static int zze(int i) {
        return (-(i & 1)) ^ (i >>> 1);
    }

    public abstract int zzc(int i) throws zzjk;

    public abstract int zzu();

    /* JADX INFO: Access modifiers changed from: private */
    public zzif() {
    }
}
