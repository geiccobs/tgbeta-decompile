package com.google.android.exoplayer2.extractor.ts;

import com.google.android.exoplayer2.util.Assertions;
import java.util.Arrays;
/* loaded from: classes3.dex */
final class NalUnitTargetBuffer {
    private boolean isCompleted;
    private boolean isFilling;
    public byte[] nalData;
    public int nalLength;
    private final int targetType;

    public NalUnitTargetBuffer(int targetType, int initialCapacity) {
        this.targetType = targetType;
        byte[] bArr = new byte[initialCapacity + 3];
        this.nalData = bArr;
        bArr[2] = 1;
    }

    public void reset() {
        this.isFilling = false;
        this.isCompleted = false;
    }

    public boolean isCompleted() {
        return this.isCompleted;
    }

    public void startNalUnit(int type) {
        boolean z = true;
        Assertions.checkState(!this.isFilling);
        if (type != this.targetType) {
            z = false;
        }
        this.isFilling = z;
        if (z) {
            this.nalLength = 3;
            this.isCompleted = false;
        }
    }

    public void appendToNalUnit(byte[] data, int offset, int limit) {
        if (!this.isFilling) {
            return;
        }
        int readLength = limit - offset;
        byte[] bArr = this.nalData;
        int length = bArr.length;
        int i = this.nalLength;
        if (length < i + readLength) {
            this.nalData = Arrays.copyOf(bArr, (i + readLength) * 2);
        }
        System.arraycopy(data, offset, this.nalData, this.nalLength, readLength);
        this.nalLength += readLength;
    }

    public boolean endNalUnit(int discardPadding) {
        if (!this.isFilling) {
            return false;
        }
        this.nalLength -= discardPadding;
        this.isFilling = false;
        this.isCompleted = true;
        return true;
    }
}
