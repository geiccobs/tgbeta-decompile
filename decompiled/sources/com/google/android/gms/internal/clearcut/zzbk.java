package com.google.android.gms.internal.clearcut;
/* loaded from: classes3.dex */
public abstract class zzbk {
    private static volatile boolean zzft;
    private int zzfq;
    private int zzfr;
    private boolean zzfs;

    static {
        zzft = false;
        zzft = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public zzbk() {
        this.zzfq = 100;
        this.zzfr = Integer.MAX_VALUE;
        this.zzfs = false;
    }

    public static long zza(long j) {
        return (-(j & 1)) ^ (j >>> 1);
    }

    public static zzbk zza(byte[] bArr, int i, int i2, boolean z) {
        zzbm zzbmVar = new zzbm(bArr, 0, i2, false);
        try {
            zzbmVar.zzl(i2);
            return zzbmVar;
        } catch (zzco e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static int zzm(int i) {
        return (-(i & 1)) ^ (i >>> 1);
    }

    public abstract int zzaf();

    public abstract int zzl(int i) throws zzco;
}
