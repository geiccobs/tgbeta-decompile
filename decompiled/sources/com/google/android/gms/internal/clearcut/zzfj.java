package com.google.android.gms.internal.clearcut;

import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import java.nio.ByteBuffer;
/* loaded from: classes3.dex */
final class zzfj extends zzfg {
    private static int zza(byte[] bArr, int i, long j, int i2) {
        int zzam;
        int zzp;
        int zzd;
        switch (i2) {
            case 0:
                zzam = zzff.zzam(i);
                return zzam;
            case 1:
                zzp = zzff.zzp(i, zzfd.zza(bArr, j));
                return zzp;
            case 2:
                zzd = zzff.zzd(i, zzfd.zza(bArr, j), zzfd.zza(bArr, j + 1));
                return zzd;
            default:
                throw new AssertionError();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:31:0x0061, code lost:
        return -1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x00b6, code lost:
        return -1;
     */
    @Override // com.google.android.gms.internal.clearcut.zzfg
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    final int zzb(int r16, byte[] r17, int r18, int r19) {
        /*
            Method dump skipped, instructions count: 219
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.clearcut.zzfj.zzb(int, byte[], int, int):int");
    }

    @Override // com.google.android.gms.internal.clearcut.zzfg
    public final int zzb(CharSequence charSequence, byte[] bArr, int i, int i2) {
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
            zzfd.zza(bArr, j4, (byte) charAt);
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
                    zzfd.zza(bArr, j4, (byte) ((charAt3 >>> 6) | 960));
                    zzfd.zza(bArr, j6, (byte) ((charAt3 & '?') | 128));
                    j2 = j6 + j;
                    j3 = j;
                } else if ((charAt3 >= 55296 && 57343 >= charAt3) || j4 > j5 - 3) {
                    if (j4 > j5 - 4) {
                        if (55296 <= charAt3 && charAt3 <= 57343 && ((i3 = i4 + 1) == length || !Character.isSurrogatePair(charAt3, charSequence.charAt(i3)))) {
                            throw new zzfi(i4, length);
                        }
                        StringBuilder sb2 = new StringBuilder(46);
                        sb2.append("Failed writing ");
                        sb2.append(charAt3);
                        sb2.append(" at index ");
                        sb2.append(j4);
                        throw new ArrayIndexOutOfBoundsException(sb2.toString());
                    }
                    int i5 = i4 + 1;
                    if (i5 != length) {
                        char charAt4 = charSequence.charAt(i5);
                        if (Character.isSurrogatePair(charAt3, charAt4)) {
                            int codePoint = Character.toCodePoint(charAt3, charAt4);
                            long j7 = j4 + 1;
                            zzfd.zza(bArr, j4, (byte) ((codePoint >>> 18) | PsExtractor.VIDEO_STREAM_MASK));
                            long j8 = j7 + 1;
                            zzfd.zza(bArr, j7, (byte) (((codePoint >>> 12) & 63) | 128));
                            long j9 = j8 + 1;
                            zzfd.zza(bArr, j8, (byte) (((codePoint >>> 6) & 63) | 128));
                            j3 = 1;
                            j2 = j9 + 1;
                            zzfd.zza(bArr, j9, (byte) ((codePoint & 63) | 128));
                            i4 = i5;
                        } else {
                            i4 = i5;
                        }
                    }
                    throw new zzfi(i4 - 1, length);
                } else {
                    long j10 = j4 + j;
                    zzfd.zza(bArr, j4, (byte) ((charAt3 >>> '\f') | 480));
                    long j11 = j10 + j;
                    zzfd.zza(bArr, j10, (byte) (((charAt3 >>> 6) & 63) | 128));
                    zzfd.zza(bArr, j11, (byte) ((charAt3 & '?') | 128));
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
                zzfd.zza(bArr, j4, (byte) charAt3);
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

    @Override // com.google.android.gms.internal.clearcut.zzfg
    public final void zzb(CharSequence charSequence, ByteBuffer byteBuffer) {
        char c;
        int i;
        long j;
        int i2;
        char charAt;
        ByteBuffer byteBuffer2 = byteBuffer;
        long zzb = zzfd.zzb(byteBuffer);
        long position = byteBuffer.position() + zzb;
        long limit = byteBuffer.limit() + zzb;
        int length = charSequence.length();
        if (length > limit - position) {
            char charAt2 = charSequence.charAt(length - 1);
            int limit2 = byteBuffer.limit();
            StringBuilder sb = new StringBuilder(37);
            sb.append("Failed writing ");
            sb.append(charAt2);
            sb.append(" at index ");
            sb.append(limit2);
            throw new ArrayIndexOutOfBoundsException(sb.toString());
        }
        int i3 = 0;
        while (true) {
            c = 128;
            if (i3 >= length || (charAt = charSequence.charAt(i3)) >= 128) {
                break;
            }
            zzfd.zza(position, (byte) charAt);
            i3++;
            position = 1 + position;
        }
        if (i3 == length) {
            i = (int) (position - zzb);
        } else {
            while (i3 < length) {
                char charAt3 = charSequence.charAt(i3);
                if (charAt3 < c && position < limit) {
                    zzfd.zza(position, (byte) charAt3);
                    position++;
                    j = zzb;
                } else if (charAt3 >= 2048 || position > limit - 2) {
                    j = zzb;
                    if ((charAt3 >= 55296 && 57343 >= charAt3) || position > limit - 3) {
                        if (position > limit - 4) {
                            if (55296 <= charAt3 && charAt3 <= 57343 && ((i2 = i3 + 1) == length || !Character.isSurrogatePair(charAt3, charSequence.charAt(i2)))) {
                                throw new zzfi(i3, length);
                            }
                            StringBuilder sb2 = new StringBuilder(46);
                            sb2.append("Failed writing ");
                            sb2.append(charAt3);
                            sb2.append(" at index ");
                            sb2.append(position);
                            throw new ArrayIndexOutOfBoundsException(sb2.toString());
                        }
                        int i4 = i3 + 1;
                        if (i4 != length) {
                            char charAt4 = charSequence.charAt(i4);
                            if (Character.isSurrogatePair(charAt3, charAt4)) {
                                int codePoint = Character.toCodePoint(charAt3, charAt4);
                                long j2 = position + 1;
                                zzfd.zza(position, (byte) ((codePoint >>> 18) | PsExtractor.VIDEO_STREAM_MASK));
                                long j3 = j2 + 1;
                                zzfd.zza(j2, (byte) (((codePoint >>> 12) & 63) | 128));
                                long j4 = j3 + 1;
                                zzfd.zza(j3, (byte) (((codePoint >>> 6) & 63) | 128));
                                long j5 = j4 + 1;
                                zzfd.zza(j4, (byte) ((codePoint & 63) | 128));
                                i3 = i4;
                                position = j5;
                            } else {
                                i3 = i4;
                            }
                        }
                        throw new zzfi(i3 - 1, length);
                    }
                    long j6 = position + 1;
                    zzfd.zza(position, (byte) ((charAt3 >>> '\f') | 480));
                    long j7 = j6 + 1;
                    zzfd.zza(j6, (byte) (((charAt3 >>> 6) & 63) | 128));
                    zzfd.zza(j7, (byte) ((charAt3 & '?') | 128));
                    position = j7 + 1;
                } else {
                    j = zzb;
                    long j8 = position + 1;
                    zzfd.zza(position, (byte) ((charAt3 >>> 6) | 960));
                    zzfd.zza(j8, (byte) ((charAt3 & '?') | 128));
                    position = j8 + 1;
                }
                i3++;
                zzb = j;
                c = 128;
            }
            i = (int) (position - zzb);
            byteBuffer2 = byteBuffer;
        }
        byteBuffer2.position(i);
    }
}
