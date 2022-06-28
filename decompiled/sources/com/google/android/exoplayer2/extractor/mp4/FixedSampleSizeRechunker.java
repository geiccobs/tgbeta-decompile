package com.google.android.exoplayer2.extractor.mp4;
/* loaded from: classes3.dex */
public final class FixedSampleSizeRechunker {
    private static final int MAX_SAMPLE_SIZE = 8192;

    /* loaded from: classes3.dex */
    public static final class Results {
        public final long duration;
        public final int[] flags;
        public final int maximumSize;
        public final long[] offsets;
        public final int[] sizes;
        public final long[] timestamps;

        private Results(long[] offsets, int[] sizes, int maximumSize, long[] timestamps, int[] flags, long duration) {
            this.offsets = offsets;
            this.sizes = sizes;
            this.maximumSize = maximumSize;
            this.timestamps = timestamps;
            this.flags = flags;
            this.duration = duration;
        }
    }

    /* JADX WARN: Incorrect condition in loop: B:7:0x0027 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static com.google.android.exoplayer2.extractor.mp4.FixedSampleSizeRechunker.Results rechunk(int r23, long[] r24, int[] r25, long r26) {
        /*
            r0 = r25
            r1 = 8192(0x2000, float:1.14794E-41)
            int r1 = r1 / r23
            r2 = 0
            int r3 = r0.length
            r4 = 0
        L9:
            if (r4 >= r3) goto L15
            r5 = r0[r4]
            int r6 = com.google.android.exoplayer2.util.Util.ceilDivide(r5, r1)
            int r2 = r2 + r6
            int r4 = r4 + 1
            goto L9
        L15:
            long[] r3 = new long[r2]
            int[] r4 = new int[r2]
            r5 = 0
            long[] r14 = new long[r2]
            int[] r15 = new int[r2]
            r6 = 0
            r7 = 0
            r8 = 0
            r16 = r5
            r13 = r6
            r17 = r7
        L26:
            int r5 = r0.length
            if (r8 >= r5) goto L58
            r5 = r0[r8]
            r6 = r24[r8]
            r9 = r16
        L2f:
            if (r5 <= 0) goto L53
            int r10 = java.lang.Math.min(r1, r5)
            r3[r17] = r6
            int r11 = r23 * r10
            r4[r17] = r11
            r11 = r4[r17]
            int r9 = java.lang.Math.max(r9, r11)
            long r11 = (long) r13
            long r11 = r11 * r26
            r14[r17] = r11
            r11 = 1
            r15[r17] = r11
            r11 = r4[r17]
            long r11 = (long) r11
            long r6 = r6 + r11
            int r13 = r13 + r10
            int r5 = r5 - r10
            int r17 = r17 + 1
            goto L2f
        L53:
            int r8 = r8 + 1
            r16 = r9
            goto L26
        L58:
            long r5 = (long) r13
            long r18 = r26 * r5
            com.google.android.exoplayer2.extractor.mp4.FixedSampleSizeRechunker$Results r20 = new com.google.android.exoplayer2.extractor.mp4.FixedSampleSizeRechunker$Results
            r21 = 0
            r5 = r20
            r6 = r3
            r7 = r4
            r8 = r16
            r9 = r14
            r10 = r15
            r11 = r18
            r22 = r13
            r13 = r21
            r5.<init>(r6, r7, r8, r9, r10, r11)
            return r20
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.mp4.FixedSampleSizeRechunker.rechunk(int, long[], int[], long):com.google.android.exoplayer2.extractor.mp4.FixedSampleSizeRechunker$Results");
    }

    private FixedSampleSizeRechunker() {
    }
}
