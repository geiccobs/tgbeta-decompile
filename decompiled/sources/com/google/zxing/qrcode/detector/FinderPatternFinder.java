package com.google.zxing.qrcode.detector;

import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.common.BitMatrix;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
/* loaded from: classes3.dex */
public class FinderPatternFinder {
    private static final int CENTER_QUORUM = 2;
    protected static final int MAX_MODULES = 97;
    protected static final int MIN_SKIP = 3;
    private static final EstimatedModuleComparator moduleComparator = new EstimatedModuleComparator();
    private final int[] crossCheckStateCount;
    private boolean hasSkipped;
    private final BitMatrix image;
    private final List<FinderPattern> possibleCenters;
    private final ResultPointCallback resultPointCallback;

    public FinderPatternFinder(BitMatrix image) {
        this(image, null);
    }

    public FinderPatternFinder(BitMatrix image, ResultPointCallback resultPointCallback) {
        this.image = image;
        this.possibleCenters = new ArrayList();
        this.crossCheckStateCount = new int[5];
        this.resultPointCallback = resultPointCallback;
    }

    public final BitMatrix getImage() {
        return this.image;
    }

    public final List<FinderPattern> getPossibleCenters() {
        return this.possibleCenters;
    }

    public final FinderPatternInfo find(Map<DecodeHintType, ?> hints) throws NotFoundException {
        boolean tryHarder = hints != null && hints.containsKey(DecodeHintType.TRY_HARDER);
        int maxI = this.image.getHeight();
        int maxJ = this.image.getWidth();
        int iSkip = (maxI * 3) / 388;
        if (iSkip < 3 || tryHarder) {
            iSkip = 3;
        }
        boolean done = false;
        int[] stateCount = new int[5];
        int i = iSkip - 1;
        while (i < maxI && !done) {
            clearCounts(stateCount);
            int currentState = 0;
            int j = 0;
            while (j < maxJ) {
                if (this.image.get(j, i)) {
                    if ((currentState & 1) == 1) {
                        currentState++;
                    }
                    stateCount[currentState] = stateCount[currentState] + 1;
                } else if ((currentState & 1) == 0) {
                    if (currentState == 4) {
                        if (foundPatternCross(stateCount)) {
                            boolean confirmed = handlePossibleCenter(stateCount, i, j);
                            if (confirmed) {
                                iSkip = 2;
                                if (this.hasSkipped) {
                                    done = haveMultiplyConfirmedCenters();
                                } else {
                                    int rowSkip = findRowSkip();
                                    if (rowSkip > stateCount[2]) {
                                        i += (rowSkip - stateCount[2]) - 2;
                                        j = maxJ - 1;
                                    }
                                }
                                clearCounts(stateCount);
                                currentState = 0;
                            } else {
                                shiftCounts2(stateCount);
                                currentState = 3;
                            }
                        } else {
                            shiftCounts2(stateCount);
                            currentState = 3;
                        }
                    } else {
                        currentState++;
                        stateCount[currentState] = stateCount[currentState] + 1;
                    }
                } else {
                    stateCount[currentState] = stateCount[currentState] + 1;
                }
                j++;
            }
            if (foundPatternCross(stateCount)) {
                boolean confirmed2 = handlePossibleCenter(stateCount, i, maxJ);
                if (confirmed2) {
                    iSkip = stateCount[0];
                    if (this.hasSkipped) {
                        done = haveMultiplyConfirmedCenters();
                    }
                }
            }
            i += iSkip;
        }
        FinderPattern[] patternInfo = selectBestPatterns();
        ResultPoint.orderBestPatterns(patternInfo);
        return new FinderPatternInfo(patternInfo);
    }

    private static float centerFromEnd(int[] stateCount, int end) {
        return ((end - stateCount[4]) - stateCount[3]) - (stateCount[2] / 2.0f);
    }

    public static boolean foundPatternCross(int[] stateCount) {
        int totalModuleSize = 0;
        for (int i = 0; i < 5; i++) {
            int count = stateCount[i];
            if (count == 0) {
                return false;
            }
            totalModuleSize += count;
        }
        if (totalModuleSize < 7) {
            return false;
        }
        float moduleSize = totalModuleSize / 7.0f;
        float maxVariance = moduleSize / 2.0f;
        return Math.abs(moduleSize - ((float) stateCount[0])) < maxVariance && Math.abs(moduleSize - ((float) stateCount[1])) < maxVariance && Math.abs((moduleSize * 3.0f) - ((float) stateCount[2])) < 3.0f * maxVariance && Math.abs(moduleSize - ((float) stateCount[3])) < maxVariance && Math.abs(moduleSize - ((float) stateCount[4])) < maxVariance;
    }

    protected static boolean foundPatternDiagonal(int[] stateCount) {
        int totalModuleSize = 0;
        for (int i = 0; i < 5; i++) {
            int count = stateCount[i];
            if (count == 0) {
                return false;
            }
            totalModuleSize += count;
        }
        if (totalModuleSize < 7) {
            return false;
        }
        float moduleSize = totalModuleSize / 7.0f;
        float maxVariance = moduleSize / 1.333f;
        return Math.abs(moduleSize - ((float) stateCount[0])) < maxVariance && Math.abs(moduleSize - ((float) stateCount[1])) < maxVariance && Math.abs((moduleSize * 3.0f) - ((float) stateCount[2])) < 3.0f * maxVariance && Math.abs(moduleSize - ((float) stateCount[3])) < maxVariance && Math.abs(moduleSize - ((float) stateCount[4])) < maxVariance;
    }

    private int[] getCrossCheckStateCount() {
        clearCounts(this.crossCheckStateCount);
        return this.crossCheckStateCount;
    }

    public final void clearCounts(int[] counts) {
        Arrays.fill(counts, 0);
    }

    public final void shiftCounts2(int[] stateCount) {
        stateCount[0] = stateCount[2];
        stateCount[1] = stateCount[3];
        stateCount[2] = stateCount[4];
        stateCount[3] = 1;
        stateCount[4] = 0;
    }

    private boolean crossCheckDiagonal(int centerI, int centerJ) {
        int[] stateCount = getCrossCheckStateCount();
        int i = 0;
        while (centerI >= i && centerJ >= i && this.image.get(centerJ - i, centerI - i)) {
            stateCount[2] = stateCount[2] + 1;
            i++;
        }
        if (stateCount[2] == 0) {
            return false;
        }
        while (centerI >= i && centerJ >= i && !this.image.get(centerJ - i, centerI - i)) {
            stateCount[1] = stateCount[1] + 1;
            i++;
        }
        if (stateCount[1] == 0) {
            return false;
        }
        while (centerI >= i && centerJ >= i && this.image.get(centerJ - i, centerI - i)) {
            stateCount[0] = stateCount[0] + 1;
            i++;
        }
        if (stateCount[0] == 0) {
            return false;
        }
        int maxI = this.image.getHeight();
        int maxJ = this.image.getWidth();
        int i2 = 1;
        while (centerI + i2 < maxI && centerJ + i2 < maxJ && this.image.get(centerJ + i2, centerI + i2)) {
            stateCount[2] = stateCount[2] + 1;
            i2++;
        }
        while (centerI + i2 < maxI && centerJ + i2 < maxJ && !this.image.get(centerJ + i2, centerI + i2)) {
            stateCount[3] = stateCount[3] + 1;
            i2++;
        }
        if (stateCount[3] == 0) {
            return false;
        }
        while (centerI + i2 < maxI && centerJ + i2 < maxJ && this.image.get(centerJ + i2, centerI + i2)) {
            stateCount[4] = stateCount[4] + 1;
            i2++;
        }
        if (stateCount[4] != 0) {
            return foundPatternDiagonal(stateCount);
        }
        return false;
    }

    /* JADX WARN: Code restructure failed: missing block: B:19:0x003a, code lost:
        if (r2[1] <= r14) goto L20;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x003f, code lost:
        if (r3 < 0) goto L73;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0045, code lost:
        if (r0.get(r13, r3) == false) goto L71;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0049, code lost:
        if (r2[0] > r14) goto L72;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x004b, code lost:
        r2[0] = r2[0] + 1;
        r3 = r3 - 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x0055, code lost:
        if (r2[0] <= r14) goto L30;
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x0057, code lost:
        return Float.NaN;
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x0058, code lost:
        r3 = r12 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x005a, code lost:
        if (r3 >= r1) goto L75;
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x0060, code lost:
        if (r0.get(r13, r3) == false) goto L74;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x0062, code lost:
        r2[2] = r2[2] + 1;
        r3 = r3 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x006a, code lost:
        if (r3 != r1) goto L37;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x006c, code lost:
        return Float.NaN;
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x006e, code lost:
        if (r3 >= r1) goto L78;
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x0074, code lost:
        if (r0.get(r13, r3) != false) goto L76;
     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x0078, code lost:
        if (r2[3] >= r14) goto L77;
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x007a, code lost:
        r2[3] = r2[3] + 1;
        r3 = r3 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x0082, code lost:
        if (r3 == r1) goto L64;
     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x0086, code lost:
        if (r2[3] < r14) goto L47;
     */
    /* JADX WARN: Code restructure failed: missing block: B:48:0x008a, code lost:
        if (r3 >= r1) goto L81;
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x0090, code lost:
        if (r0.get(r13, r3) == false) goto L79;
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x0094, code lost:
        if (r2[4] >= r14) goto L80;
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x0096, code lost:
        r2[4] = r2[4] + 1;
        r3 = r3 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x00a0, code lost:
        if (r2[4] < r14) goto L57;
     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x00a2, code lost:
        return Float.NaN;
     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x00a3, code lost:
        r7 = (((r2[0] + r2[1]) + r2[2]) + r2[3]) + r2[4];
     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x00bb, code lost:
        if ((java.lang.Math.abs(r7 - r15) * 5) < (r15 * 2)) goto L60;
     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x00bd, code lost:
        return Float.NaN;
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x00c2, code lost:
        if (foundPatternCross(r2) == false) goto L82;
     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x00c8, code lost:
        return centerFromEnd(r2, r3);
     */
    /* JADX WARN: Code restructure failed: missing block: B:64:0x00c9, code lost:
        return Float.NaN;
     */
    /* JADX WARN: Code restructure failed: missing block: B:82:?, code lost:
        return Float.NaN;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private float crossCheckVertical(int r12, int r13, int r14, int r15) {
        /*
            Method dump skipped, instructions count: 203
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.qrcode.detector.FinderPatternFinder.crossCheckVertical(int, int, int, int):float");
    }

    /* JADX WARN: Code restructure failed: missing block: B:19:0x003a, code lost:
        if (r2[1] <= r14) goto L20;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x003f, code lost:
        if (r3 < 0) goto L73;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0045, code lost:
        if (r0.get(r3, r13) == false) goto L71;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0049, code lost:
        if (r2[0] > r14) goto L72;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x004b, code lost:
        r2[0] = r2[0] + 1;
        r3 = r3 - 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x0055, code lost:
        if (r2[0] <= r14) goto L30;
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x0057, code lost:
        return Float.NaN;
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x0058, code lost:
        r3 = r12 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x005a, code lost:
        if (r3 >= r1) goto L75;
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x0060, code lost:
        if (r0.get(r3, r13) == false) goto L74;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x0062, code lost:
        r2[2] = r2[2] + 1;
        r3 = r3 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x006a, code lost:
        if (r3 != r1) goto L37;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x006c, code lost:
        return Float.NaN;
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x006e, code lost:
        if (r3 >= r1) goto L78;
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x0074, code lost:
        if (r0.get(r3, r13) != false) goto L76;
     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x0078, code lost:
        if (r2[3] >= r14) goto L77;
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x007a, code lost:
        r2[3] = r2[3] + 1;
        r3 = r3 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x0082, code lost:
        if (r3 == r1) goto L64;
     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x0086, code lost:
        if (r2[3] < r14) goto L47;
     */
    /* JADX WARN: Code restructure failed: missing block: B:48:0x008a, code lost:
        if (r3 >= r1) goto L81;
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x0090, code lost:
        if (r0.get(r3, r13) == false) goto L79;
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x0094, code lost:
        if (r2[4] >= r14) goto L80;
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x0096, code lost:
        r2[4] = r2[4] + 1;
        r3 = r3 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x00a0, code lost:
        if (r2[4] < r14) goto L57;
     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x00a2, code lost:
        return Float.NaN;
     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x00a3, code lost:
        r7 = (((r2[0] + r2[1]) + r2[2]) + r2[3]) + r2[4];
     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x00b9, code lost:
        if ((java.lang.Math.abs(r7 - r15) * 5) < r15) goto L60;
     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x00bb, code lost:
        return Float.NaN;
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x00c0, code lost:
        if (foundPatternCross(r2) == false) goto L82;
     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x00c6, code lost:
        return centerFromEnd(r2, r3);
     */
    /* JADX WARN: Code restructure failed: missing block: B:64:0x00c7, code lost:
        return Float.NaN;
     */
    /* JADX WARN: Code restructure failed: missing block: B:82:?, code lost:
        return Float.NaN;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private float crossCheckHorizontal(int r12, int r13, int r14, int r15) {
        /*
            Method dump skipped, instructions count: 201
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.qrcode.detector.FinderPatternFinder.crossCheckHorizontal(int, int, int, int):float");
    }

    @Deprecated
    protected final boolean handlePossibleCenter(int[] stateCount, int i, int j, boolean pureBarcode) {
        return handlePossibleCenter(stateCount, i, j);
    }

    public final boolean handlePossibleCenter(int[] stateCount, int i, int j) {
        int stateCountTotal = stateCount[0] + stateCount[1] + stateCount[2] + stateCount[3] + stateCount[4];
        float centerJ = centerFromEnd(stateCount, j);
        float centerI = crossCheckVertical(i, (int) centerJ, stateCount[2], stateCountTotal);
        if (!Float.isNaN(centerI)) {
            float centerJ2 = crossCheckHorizontal((int) centerJ, (int) centerI, stateCount[2], stateCountTotal);
            if (!Float.isNaN(centerJ2) && crossCheckDiagonal((int) centerI, (int) centerJ2)) {
                float estimatedModuleSize = stateCountTotal / 7.0f;
                boolean found = false;
                int index = 0;
                while (true) {
                    if (index >= this.possibleCenters.size()) {
                        break;
                    }
                    FinderPattern center = this.possibleCenters.get(index);
                    if (!center.aboutEquals(estimatedModuleSize, centerI, centerJ2)) {
                        index++;
                    } else {
                        this.possibleCenters.set(index, center.combineEstimate(centerI, centerJ2, estimatedModuleSize));
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    FinderPattern point = new FinderPattern(centerJ2, centerI, estimatedModuleSize);
                    this.possibleCenters.add(point);
                    ResultPointCallback resultPointCallback = this.resultPointCallback;
                    if (resultPointCallback != null) {
                        resultPointCallback.foundPossibleResultPoint(point);
                    }
                }
                return true;
            }
        }
        return false;
    }

    private int findRowSkip() {
        int max = this.possibleCenters.size();
        if (max <= 1) {
            return 0;
        }
        ResultPoint firstConfirmedCenter = null;
        for (FinderPattern center : this.possibleCenters) {
            if (center.getCount() >= 2) {
                if (firstConfirmedCenter == null) {
                    firstConfirmedCenter = center;
                } else {
                    this.hasSkipped = true;
                    return ((int) (Math.abs(firstConfirmedCenter.getX() - center.getX()) - Math.abs(firstConfirmedCenter.getY() - center.getY()))) / 2;
                }
            }
        }
        return 0;
    }

    private boolean haveMultiplyConfirmedCenters() {
        int confirmedCount = 0;
        float totalModuleSize = 0.0f;
        int max = this.possibleCenters.size();
        for (FinderPattern pattern : this.possibleCenters) {
            if (pattern.getCount() >= 2) {
                confirmedCount++;
                totalModuleSize += pattern.getEstimatedModuleSize();
            }
        }
        if (confirmedCount < 3) {
            return false;
        }
        float average = totalModuleSize / max;
        float totalDeviation = 0.0f;
        for (FinderPattern pattern2 : this.possibleCenters) {
            totalDeviation += Math.abs(pattern2.getEstimatedModuleSize() - average);
        }
        return totalDeviation <= 0.05f * totalModuleSize;
    }

    private static double squaredDistance(FinderPattern a, FinderPattern b) {
        double x = a.getX() - b.getX();
        double y = a.getY() - b.getY();
        Double.isNaN(x);
        Double.isNaN(x);
        Double.isNaN(y);
        Double.isNaN(y);
        return (x * x) + (y * y);
    }

    private FinderPattern[] selectBestPatterns() throws NotFoundException {
        int startSize = this.possibleCenters.size();
        if (startSize < 3) {
            throw NotFoundException.getNotFoundInstance();
        }
        Collections.sort(this.possibleCenters, moduleComparator);
        double distortion = Double.MAX_VALUE;
        double[] squares = new double[3];
        FinderPattern[] bestPatterns = new FinderPattern[3];
        for (int i = 0; i < this.possibleCenters.size() - 2; i++) {
            FinderPattern fpi = this.possibleCenters.get(i);
            float minModuleSize = fpi.getEstimatedModuleSize();
            for (int j = i + 1; j < this.possibleCenters.size() - 1; j++) {
                FinderPattern fpj = this.possibleCenters.get(j);
                double squares0 = squaredDistance(fpi, fpj);
                for (int k = j + 1; k < this.possibleCenters.size(); k++) {
                    FinderPattern fpk = this.possibleCenters.get(k);
                    float maxModuleSize = fpk.getEstimatedModuleSize();
                    if (maxModuleSize <= 1.4f * minModuleSize) {
                        squares[0] = squares0;
                        squares[1] = squaredDistance(fpj, fpk);
                        squares[2] = squaredDistance(fpi, fpk);
                        Arrays.sort(squares);
                        double d = Math.abs(squares[2] - (squares[1] * 2.0d)) + Math.abs(squares[2] - (squares[0] * 2.0d));
                        if (d < distortion) {
                            distortion = d;
                            bestPatterns[0] = fpi;
                            bestPatterns[1] = fpj;
                            bestPatterns[2] = fpk;
                        }
                    }
                }
            }
        }
        if (distortion == Double.MAX_VALUE) {
            throw NotFoundException.getNotFoundInstance();
        }
        return bestPatterns;
    }

    /* loaded from: classes3.dex */
    public static final class EstimatedModuleComparator implements Comparator<FinderPattern>, Serializable {
        private EstimatedModuleComparator() {
        }

        public int compare(FinderPattern center1, FinderPattern center2) {
            return Float.compare(center1.getEstimatedModuleSize(), center2.getEstimatedModuleSize());
        }
    }
}
