package com.google.android.gms.internal.wearable;

import java.io.IOException;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public final class zzaj {
    public static int zza(byte[] bArr, int i, zzai zzaiVar) {
        int i2 = i + 1;
        byte b = bArr[i];
        if (b >= 0) {
            zzaiVar.zza = b;
            return i2;
        }
        return zzb(b, bArr, i2, zzaiVar);
    }

    public static int zzb(int i, byte[] bArr, int i2, zzai zzaiVar) {
        int i3 = i & 127;
        int i4 = i2 + 1;
        byte b = bArr[i2];
        if (b >= 0) {
            zzaiVar.zza = i3 | (b << 7);
            return i4;
        }
        int i5 = i3 | ((b & Byte.MAX_VALUE) << 7);
        int i6 = i4 + 1;
        byte b2 = bArr[i4];
        if (b2 >= 0) {
            zzaiVar.zza = i5 | (b2 << 14);
            return i6;
        }
        int i7 = i5 | ((b2 & Byte.MAX_VALUE) << 14);
        int i8 = i6 + 1;
        byte b3 = bArr[i6];
        if (b3 >= 0) {
            zzaiVar.zza = i7 | (b3 << 21);
            return i8;
        }
        int i9 = i7 | ((b3 & Byte.MAX_VALUE) << 21);
        int i10 = i8 + 1;
        byte b4 = bArr[i8];
        if (b4 >= 0) {
            zzaiVar.zza = i9 | (b4 << 28);
            return i10;
        }
        int i11 = i9 | ((b4 & Byte.MAX_VALUE) << 28);
        while (true) {
            int i12 = i10 + 1;
            if (bArr[i10] >= 0) {
                zzaiVar.zza = i11;
                return i12;
            }
            i10 = i12;
        }
    }

    public static int zzc(byte[] bArr, int i, zzai zzaiVar) {
        byte b;
        int i2 = i + 1;
        long j = bArr[i];
        if (j >= 0) {
            zzaiVar.zzb = j;
            return i2;
        }
        int i3 = i2 + 1;
        byte b2 = bArr[i2];
        long j2 = (j & 127) | ((b2 & Byte.MAX_VALUE) << 7);
        int i4 = 7;
        while (b2 < 0) {
            int i5 = i3 + 1;
            i4 += 7;
            j2 |= (b & Byte.MAX_VALUE) << i4;
            b2 = bArr[i3];
            i3 = i5;
        }
        zzaiVar.zzb = j2;
        return i3;
    }

    public static int zzd(byte[] bArr, int i) {
        return ((bArr[i + 3] & 255) << 24) | (bArr[i] & 255) | ((bArr[i + 1] & 255) << 8) | ((bArr[i + 2] & 255) << 16);
    }

    public static long zze(byte[] bArr, int i) {
        return ((bArr[i + 7] & 255) << 56) | (bArr[i] & 255) | ((bArr[i + 1] & 255) << 8) | ((bArr[i + 2] & 255) << 16) | ((bArr[i + 3] & 255) << 24) | ((bArr[i + 4] & 255) << 32) | ((bArr[i + 5] & 255) << 40) | ((bArr[i + 6] & 255) << 48);
    }

    public static int zzf(byte[] bArr, int i, zzai zzaiVar) throws zzcc {
        int zza = zza(bArr, i, zzaiVar);
        int i2 = zzaiVar.zza;
        if (i2 >= 0) {
            if (i2 == 0) {
                zzaiVar.zzc = "";
                return zza;
            }
            zzaiVar.zzc = new String(bArr, zza, i2, zzca.zza);
            return zza + i2;
        }
        throw zzcc.zzc();
    }

    public static int zzg(byte[] bArr, int i, zzai zzaiVar) throws zzcc {
        int zza = zza(bArr, i, zzaiVar);
        int i2 = zzaiVar.zza;
        if (i2 >= 0) {
            if (i2 == 0) {
                zzaiVar.zzc = "";
                return zza;
            }
            zzaiVar.zzc = zzel.zze(bArr, zza, i2);
            return zza + i2;
        }
        throw zzcc.zzc();
    }

    public static int zzh(byte[] bArr, int i, zzai zzaiVar) throws zzcc {
        int zza = zza(bArr, i, zzaiVar);
        int i2 = zzaiVar.zza;
        if (i2 < 0) {
            throw zzcc.zzc();
        }
        if (i2 <= bArr.length - zza) {
            if (i2 == 0) {
                zzaiVar.zzc = zzau.zzb;
                return zza;
            }
            zzaiVar.zzc = zzau.zzk(bArr, zza, i2);
            return zza + i2;
        }
        throw zzcc.zzb();
    }

    public static int zzi(zzdi zzdiVar, byte[] bArr, int i, int i2, zzai zzaiVar) throws IOException {
        int i3;
        int i4 = i + 1;
        int i5 = bArr[i];
        if (i5 < 0) {
            int zzb = zzb(i5, bArr, i4, zzaiVar);
            i5 = zzaiVar.zza;
            i3 = zzb;
        } else {
            i3 = i4;
        }
        if (i5 < 0 || i5 > i2 - i3) {
            throw zzcc.zzb();
        }
        Object zza = zzdiVar.zza();
        int i6 = i5 + i3;
        zzdiVar.zzh(zza, bArr, i3, i6, zzaiVar);
        zzdiVar.zzi(zza);
        zzaiVar.zzc = zza;
        return i6;
    }

    public static int zzj(zzdi zzdiVar, byte[] bArr, int i, int i2, int i3, zzai zzaiVar) throws IOException {
        zzda zzdaVar = (zzda) zzdiVar;
        Object zza = zzdaVar.zza();
        int zzg = zzdaVar.zzg(zza, bArr, i, i2, i3, zzaiVar);
        zzdaVar.zzi(zza);
        zzaiVar.zzc = zza;
        return zzg;
    }

    public static int zzk(int i, byte[] bArr, int i2, int i3, zzbz<?> zzbzVar, zzai zzaiVar) {
        zzbt zzbtVar = (zzbt) zzbzVar;
        int zza = zza(bArr, i2, zzaiVar);
        zzbtVar.zzf(zzaiVar.zza);
        while (zza < i3) {
            int zza2 = zza(bArr, zza, zzaiVar);
            if (i != zzaiVar.zza) {
                break;
            }
            zza = zza(bArr, zza2, zzaiVar);
            zzbtVar.zzf(zzaiVar.zza);
        }
        return zza;
    }

    public static int zzl(byte[] bArr, int i, zzbz<?> zzbzVar, zzai zzaiVar) throws IOException {
        zzbt zzbtVar = (zzbt) zzbzVar;
        int zza = zza(bArr, i, zzaiVar);
        int i2 = zzaiVar.zza + zza;
        while (zza < i2) {
            zza = zza(bArr, zza, zzaiVar);
            zzbtVar.zzf(zzaiVar.zza);
        }
        if (zza == i2) {
            return zza;
        }
        throw zzcc.zzb();
    }

    public static int zzm(zzdi<?> zzdiVar, int i, byte[] bArr, int i2, int i3, zzbz<?> zzbzVar, zzai zzaiVar) throws IOException {
        int zzi = zzi(zzdiVar, bArr, i2, i3, zzaiVar);
        zzbzVar.add(zzaiVar.zzc);
        while (zzi < i3) {
            int zza = zza(bArr, zzi, zzaiVar);
            if (i != zzaiVar.zza) {
                break;
            }
            zzi = zzi(zzdiVar, bArr, zza, i3, zzaiVar);
            zzbzVar.add(zzaiVar.zzc);
        }
        return zzi;
    }

    public static int zzn(int i, byte[] bArr, int i2, int i3, zzdx zzdxVar, zzai zzaiVar) throws zzcc {
        if ((i >>> 3) != 0) {
            switch (i & 7) {
                case 0:
                    int zzc = zzc(bArr, i2, zzaiVar);
                    zzdxVar.zzh(i, Long.valueOf(zzaiVar.zzb));
                    return zzc;
                case 1:
                    zzdxVar.zzh(i, Long.valueOf(zze(bArr, i2)));
                    return i2 + 8;
                case 2:
                    int zza = zza(bArr, i2, zzaiVar);
                    int i4 = zzaiVar.zza;
                    if (i4 < 0) {
                        throw zzcc.zzc();
                    }
                    if (i4 <= bArr.length - zza) {
                        if (i4 == 0) {
                            zzdxVar.zzh(i, zzau.zzb);
                        } else {
                            zzdxVar.zzh(i, zzau.zzk(bArr, zza, i4));
                        }
                        return zza + i4;
                    }
                    throw zzcc.zzb();
                case 3:
                    int i5 = (i & (-8)) | 4;
                    zzdx zzb = zzdx.zzb();
                    int i6 = 0;
                    while (true) {
                        if (i2 < i3) {
                            int zza2 = zza(bArr, i2, zzaiVar);
                            int i7 = zzaiVar.zza;
                            if (i7 == i5) {
                                i6 = i7;
                                i2 = zza2;
                            } else {
                                i6 = i7;
                                i2 = zzn(i7, bArr, zza2, i3, zzb, zzaiVar);
                            }
                        }
                    }
                    if (i2 > i3 || i6 != i5) {
                        throw zzcc.zzf();
                    }
                    zzdxVar.zzh(i, zzb);
                    return i2;
                case 4:
                default:
                    throw zzcc.zzd();
                case 5:
                    zzdxVar.zzh(i, Integer.valueOf(zzd(bArr, i2)));
                    return i2 + 4;
            }
        }
        throw zzcc.zzd();
    }
}
