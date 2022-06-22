package com.google.android.exoplayer2.extractor.mp4;

import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.io.IOException;
/* loaded from: classes.dex */
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

    public void initTables(int i, int i2) {
        this.trunCount = i;
        this.sampleCount = i2;
        int[] iArr = this.trunLength;
        if (iArr == null || iArr.length < i) {
            this.trunDataPosition = new long[i];
            this.trunLength = new int[i];
        }
        int[] iArr2 = this.sampleSizeTable;
        if (iArr2 == null || iArr2.length < i2) {
            int i3 = (i2 * 125) / 100;
            this.sampleSizeTable = new int[i3];
            this.sampleCompositionTimeOffsetUsTable = new int[i3];
            this.sampleDecodingTimeUsTable = new long[i3];
            this.sampleIsSyncFrameTable = new boolean[i3];
            this.sampleHasSubsampleEncryptionTable = new boolean[i3];
        }
    }

    public void initEncryptionData(int i) {
        ParsableByteArray parsableByteArray = this.sampleEncryptionData;
        if (parsableByteArray == null || parsableByteArray.limit() < i) {
            this.sampleEncryptionData = new ParsableByteArray(i);
        }
        this.sampleEncryptionDataLength = i;
        this.definesEncryptionData = true;
        this.sampleEncryptionDataNeedsFill = true;
    }

    public void fillEncryptionData(ExtractorInput extractorInput) throws IOException, InterruptedException {
        extractorInput.readFully(this.sampleEncryptionData.data, 0, this.sampleEncryptionDataLength);
        this.sampleEncryptionData.setPosition(0);
        this.sampleEncryptionDataNeedsFill = false;
    }

    public void fillEncryptionData(ParsableByteArray parsableByteArray) {
        parsableByteArray.readBytes(this.sampleEncryptionData.data, 0, this.sampleEncryptionDataLength);
        this.sampleEncryptionData.setPosition(0);
        this.sampleEncryptionDataNeedsFill = false;
    }

    public long getSamplePresentationTimeUs(int i) {
        return this.sampleDecodingTimeUsTable[i] + this.sampleCompositionTimeOffsetUsTable[i];
    }

    public boolean sampleHasSubsampleEncryptionTable(int i) {
        return this.definesEncryptionData && this.sampleHasSubsampleEncryptionTable[i];
    }
}
