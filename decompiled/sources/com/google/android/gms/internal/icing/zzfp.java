package com.google.android.gms.internal.icing;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
final class zzfp extends zzfo {
    @Override // com.google.android.gms.internal.icing.zzfo
    final int zzb(int i, byte[] bArr, int i2, int i3) {
        int i4 = 0;
        while (i4 < i3 && bArr[i4] >= 0) {
            i4++;
        }
        if (i4 >= i3) {
            return 0;
        }
        while (i4 < i3) {
            int i5 = i4 + 1;
            byte b = bArr[i4];
            if (b < 0) {
                if (b < -32) {
                    if (i5 >= i3) {
                        return b;
                    }
                    if (b >= -62) {
                        i4 = i5 + 1;
                        if (bArr[i5] > -65) {
                        }
                    }
                    return -1;
                } else if (b >= -16) {
                    if (i5 < i3 - 2) {
                        int i6 = i5 + 1;
                        byte b2 = bArr[i5];
                        if (b2 <= -65 && (((b << 28) + (b2 + 112)) >> 30) == 0) {
                            int i7 = i6 + 1;
                            if (bArr[i6] <= -65) {
                                i5 = i7 + 1;
                                if (bArr[i7] > -65) {
                                }
                            }
                        }
                        return -1;
                    }
                    return zzfr.zze(bArr, i5, i3);
                } else if (i5 < i3 - 1) {
                    int i8 = i5 + 1;
                    byte b3 = bArr[i5];
                    if (b3 <= -65 && ((b != -32 || b3 >= -96) && (b != -19 || b3 < -96))) {
                        i4 = i8 + 1;
                        if (bArr[i8] > -65) {
                        }
                    }
                    return -1;
                } else {
                    return zzfr.zze(bArr, i5, i3);
                }
            }
            i4 = i5;
        }
        return 0;
    }
}
