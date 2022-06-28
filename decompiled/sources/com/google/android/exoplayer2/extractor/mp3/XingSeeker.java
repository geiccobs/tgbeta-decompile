package com.google.android.exoplayer2.extractor.mp3;

import com.google.android.exoplayer2.extractor.MpegAudioHeader;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.SeekPoint;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public final class XingSeeker implements Seeker {
    private static final String TAG = "XingSeeker";
    private final long dataEndPosition;
    private final long dataSize;
    private final long dataStartPosition;
    private final long durationUs;
    private final long[] tableOfContents;
    private final int xingFrameSize;

    public static XingSeeker create(long inputLength, long position, MpegAudioHeader mpegAudioHeader, ParsableByteArray frame) {
        int frameCount;
        int samplesPerFrame = mpegAudioHeader.samplesPerFrame;
        int sampleRate = mpegAudioHeader.sampleRate;
        int flags = frame.readInt();
        if ((flags & 1) != 1 || (frameCount = frame.readUnsignedIntToInt()) == 0) {
            return null;
        }
        long durationUs = Util.scaleLargeTimestamp(frameCount, samplesPerFrame * 1000000, sampleRate);
        if ((flags & 6) != 6) {
            return new XingSeeker(position, mpegAudioHeader.frameSize, durationUs);
        }
        long dataSize = frame.readUnsignedInt();
        long[] tableOfContents = new long[100];
        for (int i = 0; i < 100; i++) {
            tableOfContents[i] = frame.readUnsignedByte();
        }
        if (inputLength != -1 && inputLength != position + dataSize) {
            Log.w(TAG, "XING data size mismatch: " + inputLength + ", " + (position + dataSize));
        }
        return new XingSeeker(position, mpegAudioHeader.frameSize, durationUs, dataSize, tableOfContents);
    }

    private XingSeeker(long dataStartPosition, int xingFrameSize, long durationUs) {
        this(dataStartPosition, xingFrameSize, durationUs, -1L, null);
    }

    private XingSeeker(long dataStartPosition, int xingFrameSize, long durationUs, long dataSize, long[] tableOfContents) {
        this.dataStartPosition = dataStartPosition;
        this.xingFrameSize = xingFrameSize;
        this.durationUs = durationUs;
        this.tableOfContents = tableOfContents;
        this.dataSize = dataSize;
        this.dataEndPosition = dataSize != -1 ? dataStartPosition + dataSize : -1L;
    }

    @Override // com.google.android.exoplayer2.extractor.SeekMap
    public boolean isSeekable() {
        return this.tableOfContents != null;
    }

    @Override // com.google.android.exoplayer2.extractor.SeekMap
    public SeekMap.SeekPoints getSeekPoints(long timeUs) {
        double scaledPosition;
        if (!isSeekable()) {
            return new SeekMap.SeekPoints(new SeekPoint(0L, this.dataStartPosition + this.xingFrameSize));
        }
        long timeUs2 = Util.constrainValue(timeUs, 0L, this.durationUs);
        double d = timeUs2;
        Double.isNaN(d);
        double d2 = this.durationUs;
        Double.isNaN(d2);
        double percent = (d * 100.0d) / d2;
        if (percent <= FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
            scaledPosition = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        } else if (percent >= 100.0d) {
            scaledPosition = 256.0d;
        } else {
            int prevTableIndex = (int) percent;
            long[] tableOfContents = (long[]) Assertions.checkNotNull(this.tableOfContents);
            double prevScaledPosition = tableOfContents[prevTableIndex];
            double nextScaledPosition = prevTableIndex == 99 ? 256.0d : tableOfContents[prevTableIndex + 1];
            double d3 = prevTableIndex;
            Double.isNaN(d3);
            double interpolateFraction = percent - d3;
            Double.isNaN(prevScaledPosition);
            Double.isNaN(prevScaledPosition);
            scaledPosition = ((nextScaledPosition - prevScaledPosition) * interpolateFraction) + prevScaledPosition;
        }
        double d4 = this.dataSize;
        Double.isNaN(d4);
        long positionOffset = Math.round((scaledPosition / 256.0d) * d4);
        return new SeekMap.SeekPoints(new SeekPoint(timeUs2, this.dataStartPosition + Util.constrainValue(positionOffset, this.xingFrameSize, this.dataSize - 1)));
    }

    @Override // com.google.android.exoplayer2.extractor.mp3.Seeker
    public long getTimeUs(long position) {
        double interpolateFraction;
        long positionOffset = position - this.dataStartPosition;
        if (isSeekable() && positionOffset > this.xingFrameSize) {
            long[] tableOfContents = (long[]) Assertions.checkNotNull(this.tableOfContents);
            double d = positionOffset;
            Double.isNaN(d);
            double d2 = this.dataSize;
            Double.isNaN(d2);
            double scaledPosition = (d * 256.0d) / d2;
            int prevTableIndex = Util.binarySearchFloor(tableOfContents, (long) scaledPosition, true, true);
            long prevTimeUs = getTimeUsForTableIndex(prevTableIndex);
            long prevScaledPosition = tableOfContents[prevTableIndex];
            long nextTimeUs = getTimeUsForTableIndex(prevTableIndex + 1);
            long nextScaledPosition = prevTableIndex == 99 ? 256L : tableOfContents[prevTableIndex + 1];
            if (prevScaledPosition == nextScaledPosition) {
                interpolateFraction = 0.0d;
            } else {
                double d3 = prevScaledPosition;
                Double.isNaN(d3);
                double d4 = nextScaledPosition - prevScaledPosition;
                Double.isNaN(d4);
                interpolateFraction = (scaledPosition - d3) / d4;
            }
            double d5 = nextTimeUs - prevTimeUs;
            Double.isNaN(d5);
            return Math.round(d5 * interpolateFraction) + prevTimeUs;
        }
        return 0L;
    }

    @Override // com.google.android.exoplayer2.extractor.SeekMap
    public long getDurationUs() {
        return this.durationUs;
    }

    @Override // com.google.android.exoplayer2.extractor.mp3.Seeker
    public long getDataEndPosition() {
        return this.dataEndPosition;
    }

    private long getTimeUsForTableIndex(int tableIndex) {
        return (this.durationUs * tableIndex) / 100;
    }
}
