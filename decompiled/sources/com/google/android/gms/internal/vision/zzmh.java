package com.google.android.gms.internal.vision;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
final class zzmh extends zzme {
    @Override // com.google.android.gms.internal.vision.zzme
    final int zza(int i, byte[] bArr, int i2, int i3) {
        int zzd;
        int zzd2;
        while (i2 < i3 && bArr[i2] >= 0) {
            i2++;
        }
        if (i2 >= i3) {
            return 0;
        }
        while (i2 < i3) {
            int i4 = i2 + 1;
            byte b = bArr[i2];
            if (b >= 0) {
                i2 = i4;
            } else if (b < -32) {
                if (i4 >= i3) {
                    return b;
                }
                if (b >= -62) {
                    i2 = i4 + 1;
                    if (bArr[i4] > -65) {
                    }
                }
                return -1;
            } else if (b < -16) {
                if (i4 < i3 - 1) {
                    int i5 = i4 + 1;
                    byte b2 = bArr[i4];
                    if (b2 <= -65 && ((b != -32 || b2 >= -96) && (b != -19 || b2 < -96))) {
                        i2 = i5 + 1;
                        if (bArr[i5] > -65) {
                        }
                    }
                    return -1;
                }
                zzd = zzmd.zzd(bArr, i4, i3);
                return zzd;
            } else if (i4 < i3 - 2) {
                int i6 = i4 + 1;
                byte b3 = bArr[i4];
                if (b3 <= -65 && (((b << 28) + (b3 + 112)) >> 30) == 0) {
                    int i7 = i6 + 1;
                    if (bArr[i6] <= -65) {
                        int i8 = i7 + 1;
                        if (bArr[i7] <= -65) {
                            i2 = i8;
                        }
                    }
                }
                return -1;
            } else {
                zzd2 = zzmd.zzd(bArr, i4, i3);
                return zzd2;
            }
        }
        return 0;
    }

    @Override // com.google.android.gms.internal.vision.zzme
    public final String zzb(byte[] bArr, int i, int i2) throws zzjk {
        boolean zzd;
        boolean zzd2;
        boolean zze;
        boolean zzf;
        boolean zzd3;
        if ((i | i2 | ((bArr.length - i) - i2)) < 0) {
            throw new ArrayIndexOutOfBoundsException(String.format("buffer length=%d, index=%d, size=%d", Integer.valueOf(bArr.length), Integer.valueOf(i), Integer.valueOf(i2)));
        }
        int i3 = i + i2;
        char[] cArr = new char[i2];
        int i4 = 0;
        while (i < i3) {
            byte b = bArr[i];
            zzd3 = zzmf.zzd(b);
            if (!zzd3) {
                break;
            }
            i++;
            zzmf.zzb(b, cArr, i4);
            i4++;
        }
        int i5 = i4;
        while (i < i3) {
            int i6 = i + 1;
            byte b2 = bArr[i];
            zzd = zzmf.zzd(b2);
            if (zzd) {
                int i7 = i5 + 1;
                zzmf.zzb(b2, cArr, i5);
                while (i6 < i3) {
                    byte b3 = bArr[i6];
                    zzd2 = zzmf.zzd(b3);
                    if (!zzd2) {
                        break;
                    }
                    i6++;
                    zzmf.zzb(b3, cArr, i7);
                    i7++;
                }
                i = i6;
                i5 = i7;
            } else {
                zze = zzmf.zze(b2);
                if (!zze) {
                    zzf = zzmf.zzf(b2);
                    if (zzf) {
                        if (i6 >= i3 - 1) {
                            throw zzjk.zzh();
                        }
                        int i8 = i6 + 1;
                        zzmf.zzb(b2, bArr[i6], bArr[i8], cArr, i5);
                        i = i8 + 1;
                        i5++;
                    } else if (i6 >= i3 - 2) {
                        throw zzjk.zzh();
                    } else {
                        int i9 = i6 + 1;
                        byte b4 = bArr[i6];
                        int i10 = i9 + 1;
                        zzmf.zzb(b2, b4, bArr[i9], bArr[i10], cArr, i5);
                        i = i10 + 1;
                        i5 = i5 + 1 + 1;
                    }
                } else if (i6 < i3) {
                    zzmf.zzb(b2, bArr[i6], cArr, i5);
                    i = i6 + 1;
                    i5++;
                } else {
                    throw zzjk.zzh();
                }
            }
        }
        return new String(cArr, 0, i5);
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x001f, code lost:
        return r10 + r0;
     */
    @Override // com.google.android.gms.internal.vision.zzme
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final int zza(java.lang.CharSequence r8, byte[] r9, int r10, int r11) {
        /*
            Method dump skipped, instructions count: 259
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.vision.zzmh.zza(java.lang.CharSequence, byte[], int, int):int");
    }
}
