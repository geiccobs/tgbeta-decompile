package com.google.android.gms.internal.mlkit_language_id;

import org.telegram.tgnet.ConnectionsManager;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
final class zzht extends zzho {
    /* JADX WARN: Code restructure failed: missing block: B:31:0x0061, code lost:
        return -1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x00b6, code lost:
        return -1;
     */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzho
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    final int zza(int r16, byte[] r17, int r18, int r19) {
        /*
            Method dump skipped, instructions count: 219
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.mlkit_language_id.zzht.zza(int, byte[], int, int):int");
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzho
    public final int zza(CharSequence charSequence, byte[] bArr, int i, int i2) {
        char c;
        long j;
        long j2;
        long j3;
        int i3;
        char charAt;
        long j4 = i;
        long j5 = i2 + j4;
        int length = charSequence.length();
        if (length > i2 || bArr.length - i2 < i) {
            char charAt2 = charSequence.charAt(length - 1);
            StringBuilder sb = new StringBuilder(37);
            sb.append("Failed writing ");
            sb.append(charAt2);
            sb.append(" at index ");
            sb.append(i + i2);
            throw new ArrayIndexOutOfBoundsException(sb.toString());
        }
        int i4 = 0;
        while (true) {
            c = 128;
            j = 1;
            if (i4 >= length || (charAt = charSequence.charAt(i4)) >= 128) {
                break;
            }
            zzhn.zza(bArr, j4, (byte) charAt);
            i4++;
            j4 = 1 + j4;
        }
        if (i4 == length) {
            return (int) j4;
        }
        while (i4 < length) {
            char charAt3 = charSequence.charAt(i4);
            if (charAt3 >= c || j4 >= j5) {
                if (charAt3 < 2048 && j4 <= j5 - 2) {
                    long j6 = j4 + j;
                    zzhn.zza(bArr, j4, (byte) ((charAt3 >>> 6) | 960));
                    zzhn.zza(bArr, j6, (byte) ((charAt3 & '?') | ConnectionsManager.RequestFlagNeedQuickAck));
                    j2 = j6 + j;
                    j3 = j;
                } else if ((charAt3 >= 55296 && 57343 >= charAt3) || j4 > j5 - 3) {
                    if (j4 <= j5 - 4) {
                        int i5 = i4 + 1;
                        if (i5 != length) {
                            char charAt4 = charSequence.charAt(i5);
                            if (Character.isSurrogatePair(charAt3, charAt4)) {
                                int codePoint = Character.toCodePoint(charAt3, charAt4);
                                long j7 = j4 + 1;
                                zzhn.zza(bArr, j4, (byte) ((codePoint >>> 18) | 240));
                                long j8 = j7 + 1;
                                zzhn.zza(bArr, j7, (byte) (((codePoint >>> 12) & 63) | ConnectionsManager.RequestFlagNeedQuickAck));
                                long j9 = j8 + 1;
                                zzhn.zza(bArr, j8, (byte) (((codePoint >>> 6) & 63) | ConnectionsManager.RequestFlagNeedQuickAck));
                                j3 = 1;
                                j2 = j9 + 1;
                                zzhn.zza(bArr, j9, (byte) ((codePoint & 63) | ConnectionsManager.RequestFlagNeedQuickAck));
                                i4 = i5;
                            } else {
                                i4 = i5;
                            }
                        }
                        throw new zzhq(i4 - 1, length);
                    } else if (55296 <= charAt3 && charAt3 <= 57343 && ((i3 = i4 + 1) == length || !Character.isSurrogatePair(charAt3, charSequence.charAt(i3)))) {
                        throw new zzhq(i4, length);
                    } else {
                        StringBuilder sb2 = new StringBuilder(46);
                        sb2.append("Failed writing ");
                        sb2.append(charAt3);
                        sb2.append(" at index ");
                        sb2.append(j4);
                        throw new ArrayIndexOutOfBoundsException(sb2.toString());
                    }
                } else {
                    long j10 = j4 + j;
                    zzhn.zza(bArr, j4, (byte) ((charAt3 >>> '\f') | 480));
                    long j11 = j10 + j;
                    zzhn.zza(bArr, j10, (byte) (((charAt3 >>> 6) & 63) | ConnectionsManager.RequestFlagNeedQuickAck));
                    zzhn.zza(bArr, j11, (byte) ((charAt3 & '?') | ConnectionsManager.RequestFlagNeedQuickAck));
                    j2 = j11 + 1;
                    j3 = 1;
                }
                i4++;
                c = 128;
                long j12 = j3;
                j4 = j2;
                j = j12;
            } else {
                long j13 = j4 + j;
                zzhn.zza(bArr, j4, (byte) charAt3);
                j3 = j;
                j2 = j13;
            }
            i4++;
            c = 128;
            long j122 = j3;
            j4 = j2;
            j = j122;
        }
        return (int) j4;
    }

    private static int zza(byte[] bArr, int i, long j, int i2) {
        int zzb;
        int zzb2;
        int zzb3;
        if (i2 == 0) {
            zzb = zzhp.zzb(i);
            return zzb;
        } else if (i2 == 1) {
            zzb2 = zzhp.zzb(i, zzhn.zza(bArr, j));
            return zzb2;
        } else if (i2 == 2) {
            zzb3 = zzhp.zzb(i, zzhn.zza(bArr, j), zzhn.zza(bArr, j + 1));
            return zzb3;
        } else {
            throw new AssertionError();
        }
    }
}
