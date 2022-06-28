package com.google.android.gms.internal.vision;

import java.io.IOException;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
public final class zzhl {
    public static int zza(byte[] bArr, int i, zzhn zzhnVar) {
        int i2 = i + 1;
        byte b = bArr[i];
        if (b >= 0) {
            zzhnVar.zza = b;
            return i2;
        }
        return zza(b, bArr, i2, zzhnVar);
    }

    public static int zza(int i, byte[] bArr, int i2, zzhn zzhnVar) {
        int i3 = i & 127;
        int i4 = i2 + 1;
        byte b = bArr[i2];
        if (b >= 0) {
            zzhnVar.zza = i3 | (b << 7);
            return i4;
        }
        int i5 = i3 | ((b & Byte.MAX_VALUE) << 7);
        int i6 = i4 + 1;
        byte b2 = bArr[i4];
        if (b2 >= 0) {
            zzhnVar.zza = i5 | (b2 << 14);
            return i6;
        }
        int i7 = i5 | ((b2 & Byte.MAX_VALUE) << 14);
        int i8 = i6 + 1;
        byte b3 = bArr[i6];
        if (b3 >= 0) {
            zzhnVar.zza = i7 | (b3 << 21);
            return i8;
        }
        int i9 = i7 | ((b3 & Byte.MAX_VALUE) << 21);
        int i10 = i8 + 1;
        byte b4 = bArr[i8];
        if (b4 >= 0) {
            zzhnVar.zza = i9 | (b4 << 28);
            return i10;
        }
        int i11 = i9 | ((b4 & Byte.MAX_VALUE) << 28);
        while (true) {
            int i12 = i10 + 1;
            if (bArr[i10] < 0) {
                i10 = i12;
            } else {
                zzhnVar.zza = i11;
                return i12;
            }
        }
    }

    public static int zzb(byte[] bArr, int i, zzhn zzhnVar) {
        byte b;
        int i2 = i + 1;
        long j = bArr[i];
        if (j >= 0) {
            zzhnVar.zzb = j;
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
        zzhnVar.zzb = j2;
        return i3;
    }

    public static int zza(byte[] bArr, int i) {
        return ((bArr[i + 3] & 255) << 24) | (bArr[i] & 255) | ((bArr[i + 1] & 255) << 8) | ((bArr[i + 2] & 255) << 16);
    }

    public static long zzb(byte[] bArr, int i) {
        return ((bArr[i + 7] & 255) << 56) | (bArr[i] & 255) | ((bArr[i + 1] & 255) << 8) | ((bArr[i + 2] & 255) << 16) | ((bArr[i + 3] & 255) << 24) | ((bArr[i + 4] & 255) << 32) | ((bArr[i + 5] & 255) << 40) | ((bArr[i + 6] & 255) << 48);
    }

    public static double zzc(byte[] bArr, int i) {
        return Double.longBitsToDouble(zzb(bArr, i));
    }

    public static float zzd(byte[] bArr, int i) {
        return Float.intBitsToFloat(zza(bArr, i));
    }

    public static int zzc(byte[] bArr, int i, zzhn zzhnVar) throws zzjk {
        int zza = zza(bArr, i, zzhnVar);
        int i2 = zzhnVar.zza;
        if (i2 < 0) {
            throw zzjk.zzb();
        }
        if (i2 == 0) {
            zzhnVar.zzc = "";
            return zza;
        }
        zzhnVar.zzc = new String(bArr, zza, i2, zzjf.zza);
        return zza + i2;
    }

    public static int zzd(byte[] bArr, int i, zzhn zzhnVar) throws zzjk {
        int zza = zza(bArr, i, zzhnVar);
        int i2 = zzhnVar.zza;
        if (i2 < 0) {
            throw zzjk.zzb();
        }
        if (i2 == 0) {
            zzhnVar.zzc = "";
            return zza;
        }
        zzhnVar.zzc = zzmd.zzb(bArr, zza, i2);
        return zza + i2;
    }

    public static int zze(byte[] bArr, int i, zzhn zzhnVar) throws zzjk {
        int zza = zza(bArr, i, zzhnVar);
        int i2 = zzhnVar.zza;
        if (i2 < 0) {
            throw zzjk.zzb();
        }
        if (i2 > bArr.length - zza) {
            throw zzjk.zza();
        }
        if (i2 == 0) {
            zzhnVar.zzc = zzht.zza;
            return zza;
        }
        zzhnVar.zzc = zzht.zza(bArr, zza, i2);
        return zza + i2;
    }

    public static int zza(zzlc zzlcVar, byte[] bArr, int i, int i2, zzhn zzhnVar) throws IOException {
        int i3;
        int i4 = i + 1;
        int i5 = bArr[i];
        if (i5 >= 0) {
            i3 = i4;
        } else {
            int zza = zza(i5, bArr, i4, zzhnVar);
            i5 = zzhnVar.zza;
            i3 = zza;
        }
        if (i5 < 0 || i5 > i2 - i3) {
            throw zzjk.zza();
        }
        Object zza2 = zzlcVar.zza();
        int i6 = i5 + i3;
        zzlcVar.zza(zza2, bArr, i3, i6, zzhnVar);
        zzlcVar.zzc(zza2);
        zzhnVar.zzc = zza2;
        return i6;
    }

    public static int zza(zzlc zzlcVar, byte[] bArr, int i, int i2, int i3, zzhn zzhnVar) throws IOException {
        zzko zzkoVar = (zzko) zzlcVar;
        Object zza = zzkoVar.zza();
        int zza2 = zzkoVar.zza((zzko) zza, bArr, i, i2, i3, zzhnVar);
        zzkoVar.zzc((zzko) zza);
        zzhnVar.zzc = zza;
        return zza2;
    }

    public static int zza(int i, byte[] bArr, int i2, int i3, zzjl<?> zzjlVar, zzhn zzhnVar) {
        zzjd zzjdVar = (zzjd) zzjlVar;
        int zza = zza(bArr, i2, zzhnVar);
        zzjdVar.zzc(zzhnVar.zza);
        while (zza < i3) {
            int zza2 = zza(bArr, zza, zzhnVar);
            if (i != zzhnVar.zza) {
                break;
            }
            zza = zza(bArr, zza2, zzhnVar);
            zzjdVar.zzc(zzhnVar.zza);
        }
        return zza;
    }

    public static int zza(byte[] bArr, int i, zzjl<?> zzjlVar, zzhn zzhnVar) throws IOException {
        zzjd zzjdVar = (zzjd) zzjlVar;
        int zza = zza(bArr, i, zzhnVar);
        int i2 = zzhnVar.zza + zza;
        while (zza < i2) {
            zza = zza(bArr, zza, zzhnVar);
            zzjdVar.zzc(zzhnVar.zza);
        }
        if (zza != i2) {
            throw zzjk.zza();
        }
        return zza;
    }

    public static int zza(zzlc<?> zzlcVar, int i, byte[] bArr, int i2, int i3, zzjl<?> zzjlVar, zzhn zzhnVar) throws IOException {
        int zza = zza(zzlcVar, bArr, i2, i3, zzhnVar);
        zzjlVar.add(zzhnVar.zzc);
        while (zza < i3) {
            int zza2 = zza(bArr, zza, zzhnVar);
            if (i != zzhnVar.zza) {
                break;
            }
            zza = zza(zzlcVar, bArr, zza2, i3, zzhnVar);
            zzjlVar.add(zzhnVar.zzc);
        }
        return zza;
    }

    public static int zza(int i, byte[] bArr, int i2, int i3, zzlx zzlxVar, zzhn zzhnVar) throws zzjk {
        if ((i >>> 3) == 0) {
            throw zzjk.zzd();
        }
        switch (i & 7) {
            case 0:
                int zzb = zzb(bArr, i2, zzhnVar);
                zzlxVar.zza(i, Long.valueOf(zzhnVar.zzb));
                return zzb;
            case 1:
                zzlxVar.zza(i, Long.valueOf(zzb(bArr, i2)));
                return i2 + 8;
            case 2:
                int zza = zza(bArr, i2, zzhnVar);
                int i4 = zzhnVar.zza;
                if (i4 < 0) {
                    throw zzjk.zzb();
                }
                if (i4 > bArr.length - zza) {
                    throw zzjk.zza();
                }
                if (i4 == 0) {
                    zzlxVar.zza(i, zzht.zza);
                } else {
                    zzlxVar.zza(i, zzht.zza(bArr, zza, i4));
                }
                return zza + i4;
            case 3:
                zzlx zzb2 = zzlx.zzb();
                int i5 = (i & (-8)) | 4;
                int i6 = 0;
                while (true) {
                    if (i2 < i3) {
                        int zza2 = zza(bArr, i2, zzhnVar);
                        int i7 = zzhnVar.zza;
                        if (i7 == i5) {
                            i6 = i7;
                            i2 = zza2;
                        } else {
                            i6 = i7;
                            i2 = zza(i7, bArr, zza2, i3, zzb2, zzhnVar);
                        }
                    }
                }
                if (i2 > i3 || i6 != i5) {
                    throw zzjk.zzg();
                }
                zzlxVar.zza(i, zzb2);
                return i2;
            case 4:
            default:
                throw zzjk.zzd();
            case 5:
                zzlxVar.zza(i, Integer.valueOf(zza(bArr, i2)));
                return i2 + 4;
        }
    }

    public static int zza(int i, byte[] bArr, int i2, int i3, zzhn zzhnVar) throws zzjk {
        if ((i >>> 3) == 0) {
            throw zzjk.zzd();
        }
        switch (i & 7) {
            case 0:
                return zzb(bArr, i2, zzhnVar);
            case 1:
                return i2 + 8;
            case 2:
                return zza(bArr, i2, zzhnVar) + zzhnVar.zza;
            case 3:
                int i4 = (i & (-8)) | 4;
                int i5 = 0;
                while (i2 < i3) {
                    i2 = zza(bArr, i2, zzhnVar);
                    i5 = zzhnVar.zza;
                    if (i5 != i4) {
                        i2 = zza(i5, bArr, i2, i3, zzhnVar);
                    } else if (i2 <= i3 || i5 != i4) {
                        throw zzjk.zzg();
                    } else {
                        return i2;
                    }
                }
                if (i2 <= i3) {
                }
                throw zzjk.zzg();
            case 4:
            default:
                throw zzjk.zzd();
            case 5:
                return i2 + 4;
        }
    }
}
