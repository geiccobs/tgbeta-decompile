package com.google.android.exoplayer2.extractor.mp3;

import android.util.Pair;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.SeekPoint;
import com.google.android.exoplayer2.metadata.id3.MlltFrame;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public final class MlltSeeker implements Seeker {
    private final long durationUs;
    private final long[] referencePositions;
    private final long[] referenceTimesMs;

    public static MlltSeeker create(long firstFramePosition, MlltFrame mlltFrame) {
        int referenceCount = mlltFrame.bytesDeviations.length;
        long[] referencePositions = new long[referenceCount + 1];
        long[] referenceTimesMs = new long[referenceCount + 1];
        referencePositions[0] = firstFramePosition;
        referenceTimesMs[0] = 0;
        long position = firstFramePosition;
        long timeMs = 0;
        for (int i = 1; i <= referenceCount; i++) {
            position += mlltFrame.bytesBetweenReference + mlltFrame.bytesDeviations[i - 1];
            timeMs += mlltFrame.millisecondsBetweenReference + mlltFrame.millisecondsDeviations[i - 1];
            referencePositions[i] = position;
            referenceTimesMs[i] = timeMs;
        }
        return new MlltSeeker(referencePositions, referenceTimesMs);
    }

    private MlltSeeker(long[] referencePositions, long[] referenceTimesMs) {
        this.referencePositions = referencePositions;
        this.referenceTimesMs = referenceTimesMs;
        this.durationUs = C.msToUs(referenceTimesMs[referenceTimesMs.length - 1]);
    }

    @Override // com.google.android.exoplayer2.extractor.SeekMap
    public boolean isSeekable() {
        return true;
    }

    @Override // com.google.android.exoplayer2.extractor.SeekMap
    public SeekMap.SeekPoints getSeekPoints(long timeUs) {
        Pair<Long, Long> timeMsAndPosition = linearlyInterpolate(C.usToMs(Util.constrainValue(timeUs, 0L, this.durationUs)), this.referenceTimesMs, this.referencePositions);
        long timeUs2 = C.msToUs(((Long) timeMsAndPosition.first).longValue());
        long position = ((Long) timeMsAndPosition.second).longValue();
        return new SeekMap.SeekPoints(new SeekPoint(timeUs2, position));
    }

    @Override // com.google.android.exoplayer2.extractor.mp3.Seeker
    public long getTimeUs(long position) {
        Pair<Long, Long> positionAndTimeMs = linearlyInterpolate(position, this.referencePositions, this.referenceTimesMs);
        return C.msToUs(((Long) positionAndTimeMs.second).longValue());
    }

    @Override // com.google.android.exoplayer2.extractor.SeekMap
    public long getDurationUs() {
        return this.durationUs;
    }

    private static Pair<Long, Long> linearlyInterpolate(long x, long[] xReferences, long[] yReferences) {
        double d;
        int previousReferenceIndex = Util.binarySearchFloor(xReferences, x, true, true);
        long xPreviousReference = xReferences[previousReferenceIndex];
        long yPreviousReference = yReferences[previousReferenceIndex];
        int nextReferenceIndex = previousReferenceIndex + 1;
        if (nextReferenceIndex == xReferences.length) {
            return Pair.create(Long.valueOf(xPreviousReference), Long.valueOf(yPreviousReference));
        }
        long xNextReference = xReferences[nextReferenceIndex];
        long yNextReference = yReferences[nextReferenceIndex];
        if (xNextReference == xPreviousReference) {
            d = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        } else {
            double d2 = x;
            double d3 = xPreviousReference;
            Double.isNaN(d2);
            Double.isNaN(d3);
            double d4 = d2 - d3;
            double d5 = xNextReference - xPreviousReference;
            Double.isNaN(d5);
            d = d4 / d5;
        }
        double proportion = d;
        double d6 = yNextReference - yPreviousReference;
        Double.isNaN(d6);
        long y = ((long) (d6 * proportion)) + yPreviousReference;
        return Pair.create(Long.valueOf(x), Long.valueOf(y));
    }

    @Override // com.google.android.exoplayer2.extractor.mp3.Seeker
    public long getDataEndPosition() {
        return -1L;
    }
}
