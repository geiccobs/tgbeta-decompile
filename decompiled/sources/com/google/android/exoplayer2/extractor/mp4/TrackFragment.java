package com.google.android.exoplayer2.extractor.mp4;

import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.microsoft.appcenter.crashes.utils.ErrorLogHelper;
import java.io.IOException;
/* loaded from: classes3.dex */
final class TrackFragment {
    public long atomPosition;
    public long auxiliaryDataPosition;
    public long dataPosition;
    public boolean definesEncryptionData;
    public DefaultSampleValues header;
    public long nextFragmentDecodeTime;
    public int[] sampleCompositionTimeOffsetUsTable;
    public int sampleCount;
    public long[] sampleDecodingTimeUsTable;
    public ParsableByteArray sampleEncryptionData;
    public int sampleEncryptionDataLength;
    public boolean sampleEncryptionDataNeedsFill;
    public boolean[] sampleHasSubsampleEncryptionTable;
    public boolean[] sampleIsSyncFrameTable;
    public int[] sampleSizeTable;
    public TrackEncryptionBox trackEncryptionBox;
    public int trunCount;
    public long[] trunDataPosition;
    public int[] trunLength;

    public void reset() {
        this.trunCount = 0;
        this.nextFragmentDecodeTime = 0L;
        this.definesEncryptionData = false;
        this.sampleEncryptionDataNeedsFill = false;
        this.trackEncryptionBox = null;
    }

    public void initTables(int trunCount, int sampleCount) {
        this.trunCount = trunCount;
        this.sampleCount = sampleCount;
        int[] iArr = this.trunLength;
        if (iArr == null || iArr.length < trunCount) {
            this.trunDataPosition = new long[trunCount];
            this.trunLength = new int[trunCount];
        }
        int[] iArr2 = this.sampleSizeTable;
        if (iArr2 == null || iArr2.length < sampleCount) {
            int tableSize = (sampleCount * ErrorLogHelper.MAX_PROPERTY_ITEM_LENGTH) / 100;
            this.sampleSizeTable = new int[tableSize];
            this.sampleCompositionTimeOffsetUsTable = new int[tableSize];
            this.sampleDecodingTimeUsTable = new long[tableSize];
            this.sampleIsSyncFrameTable = new boolean[tableSize];
            this.sampleHasSubsampleEncryptionTable = new boolean[tableSize];
        }
    }

    public void initEncryptionData(int length) {
        ParsableByteArray parsableByteArray = this.sampleEncryptionData;
        if (parsableByteArray == null || parsableByteArray.limit() < length) {
            this.sampleEncryptionData = new ParsableByteArray(length);
        }
        this.sampleEncryptionDataLength = length;
        this.definesEncryptionData = true;
        this.sampleEncryptionDataNeedsFill = true;
    }

    public void fillEncryptionData(ExtractorInput input) throws IOException, InterruptedException {
        input.readFully(this.sampleEncryptionData.data, 0, this.sampleEncryptionDataLength);
        this.sampleEncryptionData.setPosition(0);
        this.sampleEncryptionDataNeedsFill = false;
    }

    public void fillEncryptionData(ParsableByteArray source) {
        source.readBytes(this.sampleEncryptionData.data, 0, this.sampleEncryptionDataLength);
        this.sampleEncryptionData.setPosition(0);
        this.sampleEncryptionDataNeedsFill = false;
    }

    public long getSamplePresentationTimeUs(int index) {
        return this.sampleDecodingTimeUsTable[index] + this.sampleCompositionTimeOffsetUsTable[index];
    }

    public boolean sampleHasSubsampleEncryptionTable(int index) {
        return this.definesEncryptionData && this.sampleHasSubsampleEncryptionTable[index];
    }
}
