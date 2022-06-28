package com.google.android.gms.internal.mlkit_common;

import java.io.IOException;
import java.util.Arrays;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public final class zzii {
    private static final zzii zza = new zzii(0, new int[0], new Object[0], false);
    private int zzb;
    private int[] zzc;
    private Object[] zzd;
    private int zze;
    private boolean zzf;

    public static zzii zza() {
        return zza;
    }

    public static zzii zza(zzii zziiVar, zzii zziiVar2) {
        int i = zziiVar.zzb + zziiVar2.zzb;
        int[] copyOf = Arrays.copyOf(zziiVar.zzc, i);
        System.arraycopy(zziiVar2.zzc, 0, copyOf, zziiVar.zzb, zziiVar2.zzb);
        Object[] copyOf2 = Arrays.copyOf(zziiVar.zzd, i);
        System.arraycopy(zziiVar2.zzd, 0, copyOf2, zziiVar.zzb, zziiVar2.zzb);
        return new zzii(i, copyOf, copyOf2, true);
    }

    private zzii() {
        this(0, new int[8], new Object[8], true);
    }

    private zzii(int i, int[] iArr, Object[] objArr, boolean z) {
        this.zze = -1;
        this.zzb = i;
        this.zzc = iArr;
        this.zzd = objArr;
        this.zzf = z;
    }

    public final void zzb() {
        this.zzf = false;
    }

    public final void zza(zzjd zzjdVar) throws IOException {
        if (zzjdVar.zza() == zzjc.zzb) {
            for (int i = this.zzb - 1; i >= 0; i--) {
                zzjdVar.zza(this.zzc[i] >>> 3, this.zzd[i]);
            }
            return;
        }
        for (int i2 = 0; i2 < this.zzb; i2++) {
            zzjdVar.zza(this.zzc[i2] >>> 3, this.zzd[i2]);
        }
    }

    public final void zzb(zzjd zzjdVar) throws IOException {
        if (this.zzb == 0) {
            return;
        }
        if (zzjdVar.zza() == zzjc.zza) {
            for (int i = 0; i < this.zzb; i++) {
                zza(this.zzc[i], this.zzd[i], zzjdVar);
            }
            return;
        }
        for (int i2 = this.zzb - 1; i2 >= 0; i2--) {
            zza(this.zzc[i2], this.zzd[i2], zzjdVar);
        }
    }

    private static void zza(int i, Object obj, zzjd zzjdVar) throws IOException {
        int i2 = i >>> 3;
        switch (i & 7) {
            case 0:
                zzjdVar.zza(i2, ((Long) obj).longValue());
                return;
            case 1:
                zzjdVar.zzd(i2, ((Long) obj).longValue());
                return;
            case 2:
                zzjdVar.zza(i2, (zzep) obj);
                return;
            case 3:
                if (zzjdVar.zza() == zzjc.zza) {
                    zzjdVar.zza(i2);
                    ((zzii) obj).zzb(zzjdVar);
                    zzjdVar.zzb(i2);
                    return;
                }
                zzjdVar.zzb(i2);
                ((zzii) obj).zzb(zzjdVar);
                zzjdVar.zza(i2);
                return;
            case 4:
            default:
                throw new RuntimeException(zzgb.zza());
            case 5:
                zzjdVar.zzd(i2, ((Integer) obj).intValue());
                return;
        }
    }

    public final int zzc() {
        int i = this.zze;
        if (i != -1) {
            return i;
        }
        int i2 = 0;
        for (int i3 = 0; i3 < this.zzb; i3++) {
            i2 += zzfc.zzd(this.zzc[i3] >>> 3, (zzep) this.zzd[i3]);
        }
        this.zze = i2;
        return i2;
    }

    public final int zzd() {
        int i;
        int i2 = this.zze;
        if (i2 != -1) {
            return i2;
        }
        int i3 = 0;
        for (int i4 = 0; i4 < this.zzb; i4++) {
            int i5 = this.zzc[i4];
            int i6 = i5 >>> 3;
            switch (i5 & 7) {
                case 0:
                    i = zzfc.zze(i6, ((Long) this.zzd[i4]).longValue());
                    break;
                case 1:
                    i = zzfc.zzg(i6, ((Long) this.zzd[i4]).longValue());
                    break;
                case 2:
                    i = zzfc.zzc(i6, (zzep) this.zzd[i4]);
                    break;
                case 3:
                    i = (zzfc.zze(i6) << 1) + ((zzii) this.zzd[i4]).zzd();
                    break;
                case 4:
                default:
                    throw new IllegalStateException(zzgb.zza());
                case 5:
                    i = zzfc.zzi(i6, ((Integer) this.zzd[i4]).intValue());
                    break;
            }
            i3 += i;
        }
        this.zze = i3;
        return i3;
    }

    public final boolean equals(Object obj) {
        boolean z;
        boolean z2;
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof zzii)) {
            return false;
        }
        zzii zziiVar = (zzii) obj;
        int i = this.zzb;
        if (i == zziiVar.zzb) {
            int[] iArr = this.zzc;
            int[] iArr2 = zziiVar.zzc;
            int i2 = 0;
            while (true) {
                if (i2 < i) {
                    if (iArr[i2] != iArr2[i2]) {
                        z = false;
                        break;
                    }
                    i2++;
                } else {
                    z = true;
                    break;
                }
            }
            if (z) {
                Object[] objArr = this.zzd;
                Object[] objArr2 = zziiVar.zzd;
                int i3 = this.zzb;
                int i4 = 0;
                while (true) {
                    if (i4 < i3) {
                        if (!objArr[i4].equals(objArr2[i4])) {
                            z2 = false;
                            break;
                        }
                        i4++;
                    } else {
                        z2 = true;
                        break;
                    }
                }
                if (z2) {
                    return true;
                }
            }
        }
        return false;
    }

    public final int hashCode() {
        int i = this.zzb;
        int i2 = (i + 527) * 31;
        int[] iArr = this.zzc;
        int i3 = 17;
        int i4 = 17;
        for (int i5 = 0; i5 < i; i5++) {
            i4 = (i4 * 31) + iArr[i5];
        }
        int i6 = (i2 + i4) * 31;
        Object[] objArr = this.zzd;
        int i7 = this.zzb;
        for (int i8 = 0; i8 < i7; i8++) {
            i3 = (i3 * 31) + objArr[i8].hashCode();
        }
        return i6 + i3;
    }

    public final void zza(StringBuilder sb, int i) {
        for (int i2 = 0; i2 < this.zzb; i2++) {
            zzhc.zza(sb, i, String.valueOf(this.zzc[i2] >>> 3), this.zzd[i2]);
        }
    }
}
