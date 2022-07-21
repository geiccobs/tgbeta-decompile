package com.google.android.gms.internal.vision;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes.dex */
public final class zzih extends zzif {
    private int zzf;
    private int zzg;
    private int zzh;
    private int zzi;
    private int zzk;

    /* JADX INFO: Access modifiers changed from: private */
    public zzih(byte[] bArr, int i, int i2, boolean z) {
        super();
        this.zzk = Integer.MAX_VALUE;
        this.zzf = i2 + i;
        this.zzh = i;
        this.zzi = i;
    }

    @Override // com.google.android.gms.internal.vision.zzif
    public final int zzc(int i) throws zzjk {
        if (i < 0) {
            throw zzjk.zzb();
        }
        int zzu = i + zzu();
        int i2 = this.zzk;
        if (zzu > i2) {
            throw zzjk.zza();
        }
        this.zzk = zzu;
        zzz();
        return i2;
    }

    private final void zzz() {
        int i = this.zzf + this.zzg;
        this.zzf = i;
        int i2 = i - this.zzi;
        int i3 = this.zzk;
        if (i2 > i3) {
            int i4 = i2 - i3;
            this.zzg = i4;
            this.zzf = i - i4;
            return;
        }
        this.zzg = 0;
    }

    @Override // com.google.android.gms.internal.vision.zzif
    public final int zzu() {
        return this.zzh - this.zzi;
    }
}
