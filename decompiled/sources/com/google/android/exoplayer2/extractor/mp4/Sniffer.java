package com.google.android.exoplayer2.extractor.mp4;

import com.google.android.exoplayer2.extractor.ExtractorInput;
import java.io.IOException;
/* loaded from: classes3.dex */
final class Sniffer {
    private static final int[] COMPATIBLE_BRANDS = {1769172845, 1769172786, 1769172787, 1769172788, 1769172789, 1769172790, Atom.TYPE_avc1, Atom.TYPE_hvc1, Atom.TYPE_hev1, Atom.TYPE_av01, 1836069937, 1836069938, 862401121, 862401122, 862417462, 862417718, 862414134, 862414646, 1295275552, 1295270176, 1714714144, 1801741417, 1295275600, 1903435808, 1297305174, 1684175153};
    private static final int SEARCH_LENGTH = 4096;

    public static boolean sniffFragmented(ExtractorInput input) throws IOException, InterruptedException {
        return sniffInternal(input, true);
    }

    public static boolean sniffUnfragmented(ExtractorInput input) throws IOException, InterruptedException {
        return sniffInternal(input, false);
    }

    /* JADX WARN: Code restructure failed: missing block: B:56:0x00db, code lost:
        r9 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x00de, code lost:
        if (r8 == false) goto L62;
     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x00e2, code lost:
        if (r23 != r9) goto L84;
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x00e4, code lost:
        return true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x00e8, code lost:
        return r10;
     */
    /* JADX WARN: Code restructure failed: missing block: B:84:?, code lost:
        return r10;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static boolean sniffInternal(com.google.android.exoplayer2.extractor.ExtractorInput r22, boolean r23) throws java.io.IOException, java.lang.InterruptedException {
        /*
            Method dump skipped, instructions count: 233
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.mp4.Sniffer.sniffInternal(com.google.android.exoplayer2.extractor.ExtractorInput, boolean):boolean");
    }

    private static boolean isCompatibleBrand(int brand) {
        int[] iArr;
        if ((brand >>> 8) == 3368816) {
            return true;
        }
        for (int compatibleBrand : COMPATIBLE_BRANDS) {
            if (compatibleBrand == brand) {
                return true;
            }
        }
        return false;
    }

    private Sniffer() {
    }
}
