package com.google.android.exoplayer2.extractor;

import java.io.IOException;
/* loaded from: classes3.dex */
final class ExtractorUtil {
    public static int peekToLength(ExtractorInput input, byte[] target, int offset, int length) throws IOException, InterruptedException {
        int totalBytesPeeked = 0;
        while (totalBytesPeeked < length) {
            int bytesPeeked = input.peek(target, offset + totalBytesPeeked, length - totalBytesPeeked);
            if (bytesPeeked == -1) {
                break;
            }
            totalBytesPeeked += bytesPeeked;
        }
        return totalBytesPeeked;
    }

    private ExtractorUtil() {
    }
}
