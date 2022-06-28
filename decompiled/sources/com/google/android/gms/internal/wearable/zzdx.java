package com.google.android.gms.internal.wearable;

import java.io.IOException;
import java.util.Arrays;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public final class zzdx {
    private static final zzdx zza = new zzdx(0, new int[0], new Object[0], false);
    private int zzb;
    private int[] zzc;
    private Object[] zzd;
    private int zze;
    private boolean zzf;

    private zzdx() {
        this(0, new int[8], new Object[8], true);
    }

    private zzdx(int i, int[] iArr, Object[] objArr, boolean z) {
        this.zze = -1;
        this.zzb = i;
        this.zzc = iArr;
        this.zzd = objArr;
        this.zzf = z;
    }

    public static zzdx zza() {
        return zza;
    }

    public static zzdx zzb() {
        return new zzdx(0, new int[8], new Object[8], true);
    }

    public static zzdx zzc(zzdx zzdxVar, zzdx zzdxVar2) {
        int i = zzdxVar.zzb + zzdxVar2.zzb;
        int[] copyOf = Arrays.copyOf(zzdxVar.zzc, i);
        System.arraycopy(zzdxVar2.zzc, 0, copyOf, zzdxVar.zzb, zzdxVar2.zzb);
        Object[] copyOf2 = Arrays.copyOf(zzdxVar.zzd, i);
        System.arraycopy(zzdxVar2.zzd, 0, copyOf2, zzdxVar.zzb, zzdxVar2.zzb);
        return new zzdx(i, copyOf, copyOf2, true);
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof zzdx)) {
            return false;
        }
        zzdx zzdxVar = (zzdx) obj;
        int i = this.zzb;
        if (i == zzdxVar.zzb) {
            int[] iArr = this.zzc;
            int[] iArr2 = zzdxVar.zzc;
            int i2 = 0;
            while (true) {
                if (i2 >= i) {
                    Object[] objArr = this.zzd;
                    Object[] objArr2 = zzdxVar.zzd;
                    int i3 = this.zzb;
                    for (int i4 = 0; i4 < i3; i4++) {
                        if (objArr[i4].equals(objArr2[i4])) {
                        }
                    }
                    return true;
                } else if (iArr[i2] != iArr2[i2]) {
                    break;
                } else {
                    i2++;
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

    public final void zzd() {
        this.zzf = false;
    }

    public final int zze() {
        int i = this.zze;
        if (i == -1) {
            int i2 = 0;
            for (int i3 = 0; i3 < this.zzb; i3++) {
                int i4 = this.zzc[i3];
                int zzw = zzbb.zzw(8);
                int zzc = ((zzau) this.zzd[i3]).zzc();
                i2 += zzw + zzw + zzbb.zzw(16) + zzbb.zzw(i4 >>> 3) + zzbb.zzw(24) + zzbb.zzw(zzc) + zzc;
            }
            this.zze = i2;
            return i2;
        }
        return i;
    }

    public final int zzf() {
        int i = this.zze;
        if (i == -1) {
            int i2 = 0;
            for (int i3 = 0; i3 < this.zzb; i3++) {
                int i4 = this.zzc[i3];
                int i5 = i4 >>> 3;
                switch (i4 & 7) {
                    case 0:
                        i2 += zzbb.zzw(i5 << 3) + zzbb.zzx(((Long) this.zzd[i3]).longValue());
                        break;
                    case 1:
                        ((Long) this.zzd[i3]).longValue();
                        i2 += zzbb.zzw(i5 << 3) + 8;
                        break;
                    case 2:
                        int zzw = zzbb.zzw(i5 << 3);
                        int zzc = ((zzau) this.zzd[i3]).zzc();
                        i2 += zzw + zzbb.zzw(zzc) + zzc;
                        break;
                    case 3:
                        int zzu = zzbb.zzu(i5);
                        i2 += zzu + zzu + ((zzdx) this.zzd[i3]).zzf();
                        break;
                    case 4:
                    default:
                        throw new IllegalStateException(zzcc.zze());
                    case 5:
                        ((Integer) this.zzd[i3]).intValue();
                        i2 += zzbb.zzw(i5 << 3) + 4;
                        break;
                }
            }
            this.zze = i2;
            return i2;
        }
        return i;
    }

    public final void zzg(StringBuilder sb, int i) {
        for (int i2 = 0; i2 < this.zzb; i2++) {
            zzcz.zzb(sb, i, String.valueOf(this.zzc[i2] >>> 3), this.zzd[i2]);
        }
    }

    public final void zzh(int i, Object obj) {
        if (this.zzf) {
            int i2 = this.zzb;
            int[] iArr = this.zzc;
            if (i2 == iArr.length) {
                int i3 = i2 + (i2 < 4 ? 8 : i2 >> 1);
                this.zzc = Arrays.copyOf(iArr, i3);
                this.zzd = Arrays.copyOf(this.zzd, i3);
            }
            int[] iArr2 = this.zzc;
            int i4 = this.zzb;
            iArr2[i4] = i;
            this.zzd[i4] = obj;
            this.zzb = i4 + 1;
            return;
        }
        throw new UnsupportedOperationException();
    }

    public final void zzi(zzbc zzbcVar) throws IOException {
        if (this.zzb != 0) {
            for (int i = 0; i < this.zzb; i++) {
                int i2 = this.zzc[i];
                Object obj = this.zzd[i];
                int i3 = i2 >>> 3;
                switch (i2 & 7) {
                    case 0:
                        zzbcVar.zzc(i3, ((Long) obj).longValue());
                        break;
                    case 1:
                        zzbcVar.zzj(i3, ((Long) obj).longValue());
                        break;
                    case 2:
                        zzbcVar.zzn(i3, (zzau) obj);
                        break;
                    case 3:
                        zzbcVar.zzt(i3);
                        ((zzdx) obj).zzi(zzbcVar);
                        zzbcVar.zzu(i3);
                        break;
                    case 4:
                    default:
                        throw new RuntimeException(zzcc.zze());
                    case 5:
                        zzbcVar.zzk(i3, ((Integer) obj).intValue());
                        break;
                }
            }
        }
    }
}
