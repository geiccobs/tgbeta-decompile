package com.google.android.exoplayer2.extractor.mp3;

import com.google.android.exoplayer2.extractor.MpegAudioHeader;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.SeekPoint;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public final class VbriSeeker implements Seeker {
    private static final String TAG = "VbriSeeker";
    private final long dataEndPosition;
    private final long durationUs;
    private final long[] positions;
    private final long[] timesUs;

    public static VbriSeeker create(long inputLength, long position, MpegAudioHeader mpegAudioHeader, ParsableByteArray frame) {
        int segmentSize;
        frame.skipBytes(10);
        int numFrames = frame.readInt();
        if (numFrames <= 0) {
            return null;
        }
        int sampleRate = mpegAudioHeader.sampleRate;
        long durationUs = Util.scaleLargeTimestamp(numFrames, 1000000 * (sampleRate >= 32000 ? 1152 : 576), sampleRate);
        int entryCount = frame.readUnsignedShort();
        int scale = frame.readUnsignedShort();
        int entrySize = frame.readUnsignedShort();
        frame.skipBytes(2);
        long minPosition = position + mpegAudioHeader.frameSize;
        long[] timesUs = new long[entryCount];
        long[] positions = new long[entryCount];
        int index = 0;
        long position2 = position;
        while (index < entryCount) {
            int numFrames2 = numFrames;
            long j = index * durationUs;
            long durationUs2 = durationUs;
            long durationUs3 = entryCount;
            timesUs[index] = j / durationUs3;
            positions[index] = Math.max(position2, minPosition);
            switch (entrySize) {
                case 1:
                    segmentSize = frame.readUnsignedByte();
                    break;
                case 2:
                    segmentSize = frame.readUnsignedShort();
                    break;
                case 3:
                    segmentSize = frame.readUnsignedInt24();
                    break;
                case 4:
                    segmentSize = frame.readUnsignedIntToInt();
                    break;
                default:
                    return null;
            }
            position2 += segmentSize * scale;
            index++;
            numFrames = numFrames2;
            durationUs = durationUs2;
        }
        long durationUs4 = durationUs;
        if (inputLength != -1 && inputLength != position2) {
            Log.w(TAG, "VBRI data size mismatch: " + inputLength + ", " + position2);
        }
        return new VbriSeeker(timesUs, positions, durationUs4, position2);
    }

    private VbriSeeker(long[] timesUs, long[] positions, long durationUs, long dataEndPosition) {
        this.timesUs = timesUs;
        this.positions = positions;
        this.durationUs = durationUs;
        this.dataEndPosition = dataEndPosition;
    }

    @Override // com.google.android.exoplayer2.extractor.SeekMap
    public boolean isSeekable() {
        return true;
    }

    @Override // com.google.android.exoplayer2.extractor.SeekMap
    public SeekMap.SeekPoints getSeekPoints(long timeUs) {
        int tableIndex = Util.binarySearchFloor(this.timesUs, timeUs, true, true);
        SeekPoint seekPoint = new SeekPoint(this.timesUs[tableIndex], this.positions[tableIndex]);
        if (seekPoint.timeUs >= timeUs || tableIndex == this.timesUs.length - 1) {
            return new SeekMap.SeekPoints(seekPoint);
        }
        SeekPoint nextSeekPoint = new SeekPoint(this.timesUs[tableIndex + 1], this.positions[tableIndex + 1]);
        return new SeekMap.SeekPoints(seekPoint, nextSeekPoint);
    }

    @Override // com.google.android.exoplayer2.extractor.mp3.Seeker
    public long getTimeUs(long position) {
        return this.timesUs[Util.binarySearchFloor(this.positions, position, true, true)];
    }

    @Override // com.google.android.exoplayer2.extractor.SeekMap
    public long getDurationUs() {
        return this.durationUs;
    }

    @Override // com.google.android.exoplayer2.extractor.mp3.Seeker
    public long getDataEndPosition() {
        return this.dataEndPosition;
    }
}
