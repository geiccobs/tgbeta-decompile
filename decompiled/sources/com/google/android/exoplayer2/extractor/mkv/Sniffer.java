package com.google.android.exoplayer2.extractor.mkv;

import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.io.IOException;
/* loaded from: classes3.dex */
final class Sniffer {
    private static final int ID_EBML = 440786851;
    private static final int SEARCH_LENGTH = 1024;
    private int peekLength;
    private final ParsableByteArray scratch = new ParsableByteArray(8);

    /* JADX WARN: Code restructure failed: missing block: B:34:0x00a9, code lost:
        return false;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean sniff(com.google.android.exoplayer2.extractor.ExtractorInput r25) throws java.io.IOException, java.lang.InterruptedException {
        /*
            r24 = this;
            r0 = r24
            r1 = r25
            long r2 = r25.getLength()
            r4 = 1024(0x400, double:5.06E-321)
            r6 = -1
            int r8 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r8 == 0) goto L17
            int r8 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r8 <= 0) goto L15
            goto L17
        L15:
            r4 = r2
            goto L18
        L17:
        L18:
            int r5 = (int) r4
            com.google.android.exoplayer2.util.ParsableByteArray r4 = r0.scratch
            byte[] r4 = r4.data
            r8 = 0
            r9 = 4
            r1.peekFully(r4, r8, r9)
            com.google.android.exoplayer2.util.ParsableByteArray r4 = r0.scratch
            long r10 = r4.readUnsignedInt()
            r0.peekLength = r9
        L2a:
            r12 = 440786851(0x1a45dfa3, double:2.1777764E-315)
            r4 = 1
            int r9 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r9 == 0) goto L55
            int r9 = r0.peekLength
            int r9 = r9 + r4
            r0.peekLength = r9
            if (r9 != r5) goto L3a
            return r8
        L3a:
            com.google.android.exoplayer2.util.ParsableByteArray r9 = r0.scratch
            byte[] r9 = r9.data
            r1.peekFully(r9, r8, r4)
            r4 = 8
            long r12 = r10 << r4
            r14 = -256(0xffffffffffffff00, double:NaN)
            long r9 = r12 & r14
            com.google.android.exoplayer2.util.ParsableByteArray r4 = r0.scratch
            byte[] r4 = r4.data
            r4 = r4[r8]
            r4 = r4 & 255(0xff, float:3.57E-43)
            long r11 = (long) r4
            long r9 = r9 | r11
            r10 = r9
            goto L2a
        L55:
            long r12 = r24.readUint(r25)
            int r9 = r0.peekLength
            long r14 = (long) r9
            r16 = -9223372036854775808
            int r9 = (r12 > r16 ? 1 : (r12 == r16 ? 0 : -1))
            if (r9 == 0) goto Lb5
            int r9 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r9 == 0) goto L70
            long r6 = r14 + r12
            int r9 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r9 < 0) goto L70
            r7 = r5
            r18 = r10
            goto Lb8
        L70:
            int r6 = r0.peekLength
            r7 = r5
            long r4 = (long) r6
            long r18 = r14 + r12
            int r20 = (r4 > r18 ? 1 : (r4 == r18 ? 0 : -1))
            if (r20 >= 0) goto Laa
            long r4 = r24.readUint(r25)
            int r6 = (r4 > r16 ? 1 : (r4 == r16 ? 0 : -1))
            if (r6 != 0) goto L83
            return r8
        L83:
            r18 = r10
            long r9 = r24.readUint(r25)
            r20 = 0
            int r6 = (r9 > r20 ? 1 : (r9 == r20 ? 0 : -1))
            if (r6 < 0) goto La9
            r22 = 2147483647(0x7fffffff, double:1.060997895E-314)
            int r6 = (r9 > r22 ? 1 : (r9 == r22 ? 0 : -1))
            if (r6 <= 0) goto L97
            goto La9
        L97:
            int r6 = (r9 > r20 ? 1 : (r9 == r20 ? 0 : -1))
            if (r6 == 0) goto La4
            int r6 = (int) r9
            r1.advancePeekPosition(r6)
            int r11 = r0.peekLength
            int r11 = r11 + r6
            r0.peekLength = r11
        La4:
            r5 = r7
            r10 = r18
            r4 = 1
            goto L70
        La9:
            return r8
        Laa:
            r18 = r10
            long r4 = (long) r6
            long r9 = r14 + r12
            int r6 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
            if (r6 != 0) goto Lb4
            r8 = 1
        Lb4:
            return r8
        Lb5:
            r7 = r5
            r18 = r10
        Lb8:
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.mkv.Sniffer.sniff(com.google.android.exoplayer2.extractor.ExtractorInput):boolean");
    }

    private long readUint(ExtractorInput input) throws IOException, InterruptedException {
        input.peekFully(this.scratch.data, 0, 1);
        int value = this.scratch.data[0] & 255;
        if (value == 0) {
            return Long.MIN_VALUE;
        }
        int mask = 128;
        int length = 0;
        while ((value & mask) == 0) {
            mask >>= 1;
            length++;
        }
        int value2 = value & (mask ^ (-1));
        input.peekFully(this.scratch.data, 1, length);
        for (int i = 0; i < length; i++) {
            value2 = (value2 << 8) + (this.scratch.data[i + 1] & 255);
        }
        int i2 = this.peekLength;
        this.peekLength = i2 + length + 1;
        return value2;
    }
}
