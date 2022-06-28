package com.google.zxing.qrcode.detector;

import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.common.BitMatrix;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes3.dex */
final class AlignmentPatternFinder {
    private final int height;
    private final BitMatrix image;
    private final float moduleSize;
    private final ResultPointCallback resultPointCallback;
    private final int startX;
    private final int startY;
    private final int width;
    private final List<AlignmentPattern> possibleCenters = new ArrayList(5);
    private final int[] crossCheckStateCount = new int[3];

    public AlignmentPatternFinder(BitMatrix image, int startX, int startY, int width, int height, float moduleSize, ResultPointCallback resultPointCallback) {
        this.image = image;
        this.startX = startX;
        this.startY = startY;
        this.width = width;
        this.height = height;
        this.moduleSize = moduleSize;
        this.resultPointCallback = resultPointCallback;
    }

    public AlignmentPattern find() throws NotFoundException {
        AlignmentPattern confirmed;
        AlignmentPattern confirmed2;
        int startX = this.startX;
        int height = this.height;
        int maxJ = this.width + startX;
        int middleI = this.startY + (height / 2);
        int[] stateCount = new int[3];
        for (int iGen = 0; iGen < height; iGen++) {
            int i = ((iGen & 1) == 0 ? (iGen + 1) / 2 : -((iGen + 1) / 2)) + middleI;
            stateCount[0] = 0;
            stateCount[1] = 0;
            stateCount[2] = 0;
            int j = startX;
            while (j < maxJ && !this.image.get(j, i)) {
                j++;
            }
            int currentState = 0;
            while (j < maxJ) {
                if (this.image.get(j, i)) {
                    if (currentState == 1) {
                        stateCount[1] = stateCount[1] + 1;
                    } else if (currentState == 2) {
                        if (foundPatternCross(stateCount) && (confirmed2 = handlePossibleCenter(stateCount, i, j)) != null) {
                            return confirmed2;
                        }
                        stateCount[0] = stateCount[2];
                        stateCount[1] = 1;
                        stateCount[2] = 0;
                        currentState = 1;
                    } else {
                        currentState++;
                        stateCount[currentState] = stateCount[currentState] + 1;
                    }
                } else {
                    if (currentState == 1) {
                        currentState++;
                    }
                    stateCount[currentState] = stateCount[currentState] + 1;
                }
                j++;
            }
            if (foundPatternCross(stateCount) && (confirmed = handlePossibleCenter(stateCount, i, maxJ)) != null) {
                return confirmed;
            }
        }
        if (!this.possibleCenters.isEmpty()) {
            return this.possibleCenters.get(0);
        }
        throw NotFoundException.getNotFoundInstance();
    }

    private static float centerFromEnd(int[] stateCount, int end) {
        return (end - stateCount[2]) - (stateCount[1] / 2.0f);
    }

    private boolean foundPatternCross(int[] stateCount) {
        float moduleSize = this.moduleSize;
        float maxVariance = moduleSize / 2.0f;
        for (int i = 0; i < 3; i++) {
            if (Math.abs(moduleSize - stateCount[i]) >= maxVariance) {
                return false;
            }
        }
        return true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:31:0x0063, code lost:
        if (r2[1] <= r12) goto L32;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x0066, code lost:
        if (r6 >= r1) goto L60;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x006c, code lost:
        if (r0.get(r11, r6) != false) goto L61;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x0070, code lost:
        if (r2[2] > r12) goto L59;
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x0072, code lost:
        r2[2] = r2[2] + 1;
        r6 = r6 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x007c, code lost:
        if (r2[2] <= r12) goto L41;
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x007e, code lost:
        return Float.NaN;
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x007f, code lost:
        r3 = (r2[0] + r2[1]) + r2[2];
     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x0091, code lost:
        if ((java.lang.Math.abs(r3 - r13) * 5) < (r13 * 2)) goto L44;
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x0093, code lost:
        return Float.NaN;
     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x0098, code lost:
        if (foundPatternCross(r2) == false) goto L62;
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x009e, code lost:
        return centerFromEnd(r2, r6);
     */
    /* JADX WARN: Code restructure failed: missing block: B:62:?, code lost:
        return Float.NaN;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private float crossCheckVertical(int r10, int r11, int r12, int r13) {
        /*
            r9 = this;
            com.google.zxing.common.BitMatrix r0 = r9.image
            int r1 = r0.getHeight()
            int[] r2 = r9.crossCheckStateCount
            r3 = 0
            r2[r3] = r3
            r4 = 1
            r2[r4] = r3
            r5 = 2
            r2[r5] = r3
            r6 = r10
        L12:
            if (r6 < 0) goto L26
            boolean r7 = r0.get(r11, r6)
            if (r7 == 0) goto L26
            r7 = r2[r4]
            if (r7 > r12) goto L26
            r7 = r2[r4]
            int r7 = r7 + r4
            r2[r4] = r7
            int r6 = r6 + (-1)
            goto L12
        L26:
            r7 = 2143289344(0x7fc00000, float:NaN)
            if (r6 < 0) goto La0
            r8 = r2[r4]
            if (r8 <= r12) goto L30
            goto La0
        L30:
            if (r6 < 0) goto L44
            boolean r8 = r0.get(r11, r6)
            if (r8 != 0) goto L44
            r8 = r2[r3]
            if (r8 > r12) goto L44
            r8 = r2[r3]
            int r8 = r8 + r4
            r2[r3] = r8
            int r6 = r6 + (-1)
            goto L30
        L44:
            r8 = r2[r3]
            if (r8 <= r12) goto L49
            return r7
        L49:
            int r6 = r10 + 1
        L4b:
            if (r6 >= r1) goto L5f
            boolean r8 = r0.get(r11, r6)
            if (r8 == 0) goto L5f
            r8 = r2[r4]
            if (r8 > r12) goto L5f
            r8 = r2[r4]
            int r8 = r8 + r4
            r2[r4] = r8
            int r6 = r6 + 1
            goto L4b
        L5f:
            if (r6 == r1) goto L9f
            r8 = r2[r4]
            if (r8 <= r12) goto L66
            goto L9f
        L66:
            if (r6 >= r1) goto L7a
            boolean r8 = r0.get(r11, r6)
            if (r8 != 0) goto L7a
            r8 = r2[r5]
            if (r8 > r12) goto L7a
            r8 = r2[r5]
            int r8 = r8 + r4
            r2[r5] = r8
            int r6 = r6 + 1
            goto L66
        L7a:
            r8 = r2[r5]
            if (r8 <= r12) goto L7f
            return r7
        L7f:
            r3 = r2[r3]
            r4 = r2[r4]
            int r3 = r3 + r4
            r4 = r2[r5]
            int r3 = r3 + r4
            int r4 = r3 - r13
            int r4 = java.lang.Math.abs(r4)
            int r4 = r4 * 5
            int r5 = r13 * 2
            if (r4 < r5) goto L94
            return r7
        L94:
            boolean r4 = r9.foundPatternCross(r2)
            if (r4 == 0) goto L9e
            float r7 = centerFromEnd(r2, r6)
        L9e:
            return r7
        L9f:
            return r7
        La0:
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.qrcode.detector.AlignmentPatternFinder.crossCheckVertical(int, int, int, int):float");
    }

    private AlignmentPattern handlePossibleCenter(int[] stateCount, int i, int j) {
        int stateCountTotal = stateCount[0] + stateCount[1] + stateCount[2];
        float centerJ = centerFromEnd(stateCount, j);
        float centerI = crossCheckVertical(i, (int) centerJ, stateCount[1] * 2, stateCountTotal);
        if (!Float.isNaN(centerI)) {
            float estimatedModuleSize = ((stateCount[0] + stateCount[1]) + stateCount[2]) / 3.0f;
            for (AlignmentPattern center : this.possibleCenters) {
                if (center.aboutEquals(estimatedModuleSize, centerI, centerJ)) {
                    return center.combineEstimate(centerI, centerJ, estimatedModuleSize);
                }
            }
            AlignmentPattern point = new AlignmentPattern(centerJ, centerI, estimatedModuleSize);
            this.possibleCenters.add(point);
            ResultPointCallback resultPointCallback = this.resultPointCallback;
            if (resultPointCallback != null) {
                resultPointCallback.foundPossibleResultPoint(point);
                return null;
            }
            return null;
        }
        return null;
    }
}
