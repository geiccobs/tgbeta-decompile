package com.google.android.exoplayer2.extractor;

import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.FlacStreamMetadata;
import com.google.android.exoplayer2.util.Util;
/* loaded from: classes3.dex */
public final class FlacSeekTableSeekMap implements SeekMap {
    private final long firstFrameOffset;
    private final FlacStreamMetadata flacStreamMetadata;

    public FlacSeekTableSeekMap(FlacStreamMetadata flacStreamMetadata, long firstFrameOffset) {
        this.flacStreamMetadata = flacStreamMetadata;
        this.firstFrameOffset = firstFrameOffset;
    }

    @Override // com.google.android.exoplayer2.extractor.SeekMap
    public boolean isSeekable() {
        return true;
    }

    @Override // com.google.android.exoplayer2.extractor.SeekMap
    public long getDurationUs() {
        return this.flacStreamMetadata.getDurationUs();
    }

    @Override // com.google.android.exoplayer2.extractor.SeekMap
    public SeekMap.SeekPoints getSeekPoints(long timeUs) {
        Assertions.checkNotNull(this.flacStreamMetadata.seekTable);
        long[] pointSampleNumbers = this.flacStreamMetadata.seekTable.pointSampleNumbers;
        long[] pointOffsets = this.flacStreamMetadata.seekTable.pointOffsets;
        long targetSampleNumber = this.flacStreamMetadata.getSampleNumber(timeUs);
        int index = Util.binarySearchFloor(pointSampleNumbers, targetSampleNumber, true, false);
        long seekPointOffsetFromFirstFrame = 0;
        long seekPointSampleNumber = index == -1 ? 0L : pointSampleNumbers[index];
        if (index != -1) {
            seekPointOffsetFromFirstFrame = pointOffsets[index];
        }
        SeekPoint seekPoint = getSeekPoint(seekPointSampleNumber, seekPointOffsetFromFirstFrame);
        if (seekPoint.timeUs == timeUs || index == pointSampleNumbers.length - 1) {
            return new SeekMap.SeekPoints(seekPoint);
        }
        SeekPoint secondSeekPoint = getSeekPoint(pointSampleNumbers[index + 1], pointOffsets[index + 1]);
        return new SeekMap.SeekPoints(seekPoint, secondSeekPoint);
    }

    private SeekPoint getSeekPoint(long sampleNumber, long offsetFromFirstFrame) {
        long seekTimeUs = (1000000 * sampleNumber) / this.flacStreamMetadata.sampleRate;
        long seekPosition = this.firstFrameOffset + offsetFromFirstFrame;
        return new SeekPoint(seekTimeUs, seekPosition);
    }
}
