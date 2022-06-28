package com.googlecode.mp4parser.authoring.builder;
/* loaded from: classes3.dex */
public class ByteBufferHelper {
    /* JADX WARN: Code restructure failed: missing block: B:19:0x0086, code lost:
        if ((r2 instanceof java.nio.MappedByteBuffer) == false) goto L33;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x008e, code lost:
        if ((r0.get(r3) instanceof java.nio.MappedByteBuffer) == false) goto L34;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x00a9, code lost:
        if (r0.get(r3).limit() != (r0.get(r3).capacity() - r2.capacity())) goto L36;
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x00ab, code lost:
        r4 = r0.get(r3);
        r4.limit(r2.limit() + r4.limit());
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.util.List<java.nio.ByteBuffer> mergeAdjacentBuffers(java.util.List<java.nio.ByteBuffer> r9) {
        /*
            java.util.ArrayList r0 = new java.util.ArrayList
            int r1 = r9.size()
            r0.<init>(r1)
            java.util.Iterator r1 = r9.iterator()
        Ld:
            boolean r2 = r1.hasNext()
            if (r2 != 0) goto L14
            return r0
        L14:
            java.lang.Object r2 = r1.next()
            java.nio.ByteBuffer r2 = (java.nio.ByteBuffer) r2
            int r3 = r0.size()
            int r3 = r3 + (-1)
            if (r3 < 0) goto L82
            boolean r4 = r2.hasArray()
            if (r4 == 0) goto L82
            java.lang.Object r4 = r0.get(r3)
            java.nio.ByteBuffer r4 = (java.nio.ByteBuffer) r4
            boolean r4 = r4.hasArray()
            if (r4 == 0) goto L82
            byte[] r4 = r2.array()
            java.lang.Object r5 = r0.get(r3)
            java.nio.ByteBuffer r5 = (java.nio.ByteBuffer) r5
            byte[] r5 = r5.array()
            if (r4 != r5) goto L82
            java.lang.Object r4 = r0.get(r3)
            java.nio.ByteBuffer r4 = (java.nio.ByteBuffer) r4
            int r4 = r4.arrayOffset()
            java.lang.Object r5 = r0.get(r3)
            java.nio.ByteBuffer r5 = (java.nio.ByteBuffer) r5
            int r5 = r5.limit()
            int r4 = r4 + r5
            int r5 = r2.arrayOffset()
            if (r4 != r5) goto L82
            java.lang.Object r4 = r0.remove(r3)
            java.nio.ByteBuffer r4 = (java.nio.ByteBuffer) r4
            byte[] r5 = r2.array()
            int r6 = r4.arrayOffset()
            int r7 = r4.limit()
            int r8 = r2.limit()
            int r7 = r7 + r8
            java.nio.ByteBuffer r5 = java.nio.ByteBuffer.wrap(r5, r6, r7)
            java.nio.ByteBuffer r5 = r5.slice()
            r0.add(r5)
            goto Ld
        L82:
            if (r3 < 0) goto Lbf
            boolean r4 = r2 instanceof java.nio.MappedByteBuffer
            if (r4 == 0) goto Lbf
            java.lang.Object r4 = r0.get(r3)
            boolean r4 = r4 instanceof java.nio.MappedByteBuffer
            if (r4 == 0) goto Lbf
            java.lang.Object r4 = r0.get(r3)
            java.nio.ByteBuffer r4 = (java.nio.ByteBuffer) r4
            int r4 = r4.limit()
            java.lang.Object r5 = r0.get(r3)
            java.nio.ByteBuffer r5 = (java.nio.ByteBuffer) r5
            int r5 = r5.capacity()
            int r6 = r2.capacity()
            int r5 = r5 - r6
            if (r4 != r5) goto Lbf
            java.lang.Object r4 = r0.get(r3)
            java.nio.ByteBuffer r4 = (java.nio.ByteBuffer) r4
            int r5 = r2.limit()
            int r6 = r4.limit()
            int r5 = r5 + r6
            r4.limit(r5)
            goto Ld
        Lbf:
            r2.reset()
            r0.add(r2)
            goto Ld
        */
        throw new UnsupportedOperationException("Method not decompiled: com.googlecode.mp4parser.authoring.builder.ByteBufferHelper.mergeAdjacentBuffers(java.util.List):java.util.List");
    }
}
