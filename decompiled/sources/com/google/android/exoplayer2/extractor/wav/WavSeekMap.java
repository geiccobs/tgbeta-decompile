package com.google.android.exoplayer2.extractor.wav;

import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.SeekPoint;
import com.google.android.exoplayer2.util.Util;
/* loaded from: classes3.dex */
final class WavSeekMap implements SeekMap {
    private final long blockCount;
    private final long durationUs;
    private final long firstBlockPosition;
    private final int framesPerBlock;
    private final WavHeader wavHeader;

    public WavSeekMap(WavHeader wavHeader, int framesPerBlock, long dataStartPosition, long dataEndPosition) {
        this.wavHeader = wavHeader;
        this.framesPerBlock = framesPerBlock;
        this.firstBlockPosition = dataStartPosition;
        long j = (dataEndPosition - dataStartPosition) / wavHeader.blockSize;
        this.blockCount = j;
        this.durationUs = blockIndexToTimeUs(j);
    }

    @Override // com.google.android.exoplayer2.extractor.SeekMap
    public boolean isSeekable() {
        return true;
    }

    @Override // com.google.android.exoplayer2.extractor.SeekMap
    public long getDurationUs() {
        return this.durationUs;
    }

    @Override // com.google.android.exoplayer2.extractor.SeekMap
    public SeekMap.SeekPoints getSeekPoints(long timeUs) {
        long blockIndex = Util.constrainValue((this.wavHeader.frameRateHz * timeUs) / (this.framesPerBlock * 1000000), 0L, this.blockCount - 1);
        long seekPosition = this.firstBlockPosition + (this.wavHeader.blockSize * blockIndex);
        long seekTimeUs = blockIndexToTimeUs(blockIndex);
        SeekPoint seekPoint = new SeekPoint(seekTimeUs, seekPosition);
        if (seekTimeUs >= timeUs || blockIndex == this.blockCount - 1) {
            return new SeekMap.SeekPoints(seekPoint);
        }
        long secondBlockIndex = 1 + blockIndex;
        long secondSeekPosition = this.firstBlockPosition + (this.wavHeader.blockSize * secondBlockIndex);
        long secondSeekTimeUs = blockIndexToTimeUs(secondBlockIndex);
        SeekPoint secondSeekPoint = new SeekPoint(secondSeekTimeUs, secondSeekPosition);
        return new SeekMap.SeekPoints(seekPoint, secondSeekPoint);
    }

    private long blockIndexToTimeUs(long blockIndex) {
        return Util.scaleLargeTimestamp(blockIndex * this.framesPerBlock, 1000000L, this.wavHeader.frameRateHz);
    }
}
