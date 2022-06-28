package com.google.android.exoplayer2.extractor;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes3.dex */
public abstract class BinarySearchSeeker {
    private static final long MAX_SKIP_BYTES = 262144;
    private final int minimumSearchRange;
    protected final BinarySearchSeekMap seekMap;
    protected SeekOperationParams seekOperationParams;
    protected final TimestampSeeker timestampSeeker;

    /* loaded from: classes3.dex */
    public interface SeekTimestampConverter {
        long timeUsToTargetTime(long j);
    }

    /* loaded from: classes3.dex */
    public interface TimestampSeeker {
        void onSeekFinished();

        TimestampSearchResult searchForTimestamp(ExtractorInput extractorInput, long j) throws IOException, InterruptedException;

        /* renamed from: com.google.android.exoplayer2.extractor.BinarySearchSeeker$TimestampSeeker$-CC */
        /* loaded from: classes3.dex */
        public final /* synthetic */ class CC {
            public static void $default$onSeekFinished(TimestampSeeker _this) {
            }
        }
    }

    /* loaded from: classes3.dex */
    public static final class DefaultSeekTimestampConverter implements SeekTimestampConverter {
        @Override // com.google.android.exoplayer2.extractor.BinarySearchSeeker.SeekTimestampConverter
        public long timeUsToTargetTime(long timeUs) {
            return timeUs;
        }
    }

    public BinarySearchSeeker(SeekTimestampConverter seekTimestampConverter, TimestampSeeker timestampSeeker, long durationUs, long floorTimePosition, long ceilingTimePosition, long floorBytePosition, long ceilingBytePosition, long approxBytesPerFrame, int minimumSearchRange) {
        this.timestampSeeker = timestampSeeker;
        this.minimumSearchRange = minimumSearchRange;
        this.seekMap = new BinarySearchSeekMap(seekTimestampConverter, durationUs, floorTimePosition, ceilingTimePosition, floorBytePosition, ceilingBytePosition, approxBytesPerFrame);
    }

    public final SeekMap getSeekMap() {
        return this.seekMap;
    }

    public final void setSeekTargetUs(long timeUs) {
        SeekOperationParams seekOperationParams = this.seekOperationParams;
        if (seekOperationParams != null && seekOperationParams.getSeekTimeUs() == timeUs) {
            return;
        }
        this.seekOperationParams = createSeekParamsForTargetTimeUs(timeUs);
    }

    public final boolean isSeeking() {
        return this.seekOperationParams != null;
    }

    public int handlePendingSeek(ExtractorInput input, PositionHolder seekPositionHolder) throws InterruptedException, IOException {
        TimestampSeeker timestampSeeker = (TimestampSeeker) Assertions.checkNotNull(this.timestampSeeker);
        while (true) {
            SeekOperationParams seekOperationParams = (SeekOperationParams) Assertions.checkNotNull(this.seekOperationParams);
            long floorPosition = seekOperationParams.getFloorBytePosition();
            long ceilingPosition = seekOperationParams.getCeilingBytePosition();
            long searchPosition = seekOperationParams.getNextSearchBytePosition();
            if (ceilingPosition - floorPosition <= this.minimumSearchRange) {
                markSeekOperationFinished(false, floorPosition);
                return seekToPosition(input, floorPosition, seekPositionHolder);
            } else if (!skipInputUntilPosition(input, searchPosition)) {
                return seekToPosition(input, searchPosition, seekPositionHolder);
            } else {
                input.resetPeekPosition();
                TimestampSearchResult timestampSearchResult = timestampSeeker.searchForTimestamp(input, seekOperationParams.getTargetTimePosition());
                switch (timestampSearchResult.type) {
                    case -3:
                        markSeekOperationFinished(false, searchPosition);
                        return seekToPosition(input, searchPosition, seekPositionHolder);
                    case -2:
                        seekOperationParams.updateSeekFloor(timestampSearchResult.timestampToUpdate, timestampSearchResult.bytePositionToUpdate);
                        break;
                    case -1:
                        seekOperationParams.updateSeekCeiling(timestampSearchResult.timestampToUpdate, timestampSearchResult.bytePositionToUpdate);
                        break;
                    case 0:
                        markSeekOperationFinished(true, timestampSearchResult.bytePositionToUpdate);
                        skipInputUntilPosition(input, timestampSearchResult.bytePositionToUpdate);
                        return seekToPosition(input, timestampSearchResult.bytePositionToUpdate, seekPositionHolder);
                    default:
                        throw new IllegalStateException("Invalid case");
                }
            }
        }
    }

    protected SeekOperationParams createSeekParamsForTargetTimeUs(long timeUs) {
        return new SeekOperationParams(timeUs, this.seekMap.timeUsToTargetTime(timeUs), this.seekMap.floorTimePosition, this.seekMap.ceilingTimePosition, this.seekMap.floorBytePosition, this.seekMap.ceilingBytePosition, this.seekMap.approxBytesPerFrame);
    }

    protected final void markSeekOperationFinished(boolean foundTargetFrame, long resultPosition) {
        this.seekOperationParams = null;
        this.timestampSeeker.onSeekFinished();
        onSeekOperationFinished(foundTargetFrame, resultPosition);
    }

    protected void onSeekOperationFinished(boolean foundTargetFrame, long resultPosition) {
    }

    protected final boolean skipInputUntilPosition(ExtractorInput input, long position) throws IOException, InterruptedException {
        long bytesToSkip = position - input.getPosition();
        if (bytesToSkip >= 0 && bytesToSkip <= MAX_SKIP_BYTES) {
            input.skipFully((int) bytesToSkip);
            return true;
        }
        return false;
    }

    protected final int seekToPosition(ExtractorInput input, long position, PositionHolder seekPositionHolder) {
        if (position == input.getPosition()) {
            return 0;
        }
        seekPositionHolder.position = position;
        return 1;
    }

    /* loaded from: classes3.dex */
    public static class SeekOperationParams {
        private final long approxBytesPerFrame;
        private long ceilingBytePosition;
        private long ceilingTimePosition;
        private long floorBytePosition;
        private long floorTimePosition;
        private long nextSearchBytePosition;
        private final long seekTimeUs;
        private final long targetTimePosition;

        protected static long calculateNextSearchBytePosition(long targetTimePosition, long floorTimePosition, long ceilingTimePosition, long floorBytePosition, long ceilingBytePosition, long approxBytesPerFrame) {
            if (floorBytePosition + 1 >= ceilingBytePosition || floorTimePosition + 1 >= ceilingTimePosition) {
                return floorBytePosition;
            }
            long seekTimeDuration = targetTimePosition - floorTimePosition;
            float estimatedBytesPerTimeUnit = ((float) (ceilingBytePosition - floorBytePosition)) / ((float) (ceilingTimePosition - floorTimePosition));
            long bytesToSkip = ((float) seekTimeDuration) * estimatedBytesPerTimeUnit;
            long confidenceInterval = bytesToSkip / 20;
            long estimatedFramePosition = (floorBytePosition + bytesToSkip) - approxBytesPerFrame;
            long estimatedPosition = estimatedFramePosition - confidenceInterval;
            return Util.constrainValue(estimatedPosition, floorBytePosition, ceilingBytePosition - 1);
        }

        protected SeekOperationParams(long seekTimeUs, long targetTimePosition, long floorTimePosition, long ceilingTimePosition, long floorBytePosition, long ceilingBytePosition, long approxBytesPerFrame) {
            this.seekTimeUs = seekTimeUs;
            this.targetTimePosition = targetTimePosition;
            this.floorTimePosition = floorTimePosition;
            this.ceilingTimePosition = ceilingTimePosition;
            this.floorBytePosition = floorBytePosition;
            this.ceilingBytePosition = ceilingBytePosition;
            this.approxBytesPerFrame = approxBytesPerFrame;
            this.nextSearchBytePosition = calculateNextSearchBytePosition(targetTimePosition, floorTimePosition, ceilingTimePosition, floorBytePosition, ceilingBytePosition, approxBytesPerFrame);
        }

        public long getFloorBytePosition() {
            return this.floorBytePosition;
        }

        public long getCeilingBytePosition() {
            return this.ceilingBytePosition;
        }

        public long getTargetTimePosition() {
            return this.targetTimePosition;
        }

        public long getSeekTimeUs() {
            return this.seekTimeUs;
        }

        public void updateSeekFloor(long floorTimePosition, long floorBytePosition) {
            this.floorTimePosition = floorTimePosition;
            this.floorBytePosition = floorBytePosition;
            updateNextSearchBytePosition();
        }

        public void updateSeekCeiling(long ceilingTimePosition, long ceilingBytePosition) {
            this.ceilingTimePosition = ceilingTimePosition;
            this.ceilingBytePosition = ceilingBytePosition;
            updateNextSearchBytePosition();
        }

        public long getNextSearchBytePosition() {
            return this.nextSearchBytePosition;
        }

        private void updateNextSearchBytePosition() {
            this.nextSearchBytePosition = calculateNextSearchBytePosition(this.targetTimePosition, this.floorTimePosition, this.ceilingTimePosition, this.floorBytePosition, this.ceilingBytePosition, this.approxBytesPerFrame);
        }
    }

    /* loaded from: classes3.dex */
    public static final class TimestampSearchResult {
        public static final TimestampSearchResult NO_TIMESTAMP_IN_RANGE_RESULT = new TimestampSearchResult(-3, C.TIME_UNSET, -1);
        public static final int TYPE_NO_TIMESTAMP = -3;
        public static final int TYPE_POSITION_OVERESTIMATED = -1;
        public static final int TYPE_POSITION_UNDERESTIMATED = -2;
        public static final int TYPE_TARGET_TIMESTAMP_FOUND = 0;
        private final long bytePositionToUpdate;
        private final long timestampToUpdate;
        private final int type;

        @Documented
        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes.dex */
        @interface Type {
        }

        private TimestampSearchResult(int type, long timestampToUpdate, long bytePositionToUpdate) {
            this.type = type;
            this.timestampToUpdate = timestampToUpdate;
            this.bytePositionToUpdate = bytePositionToUpdate;
        }

        public static TimestampSearchResult overestimatedResult(long newCeilingTimestamp, long newCeilingBytePosition) {
            return new TimestampSearchResult(-1, newCeilingTimestamp, newCeilingBytePosition);
        }

        public static TimestampSearchResult underestimatedResult(long newFloorTimestamp, long newCeilingBytePosition) {
            return new TimestampSearchResult(-2, newFloorTimestamp, newCeilingBytePosition);
        }

        public static TimestampSearchResult targetFoundResult(long resultBytePosition) {
            return new TimestampSearchResult(0, C.TIME_UNSET, resultBytePosition);
        }
    }

    /* loaded from: classes3.dex */
    public static class BinarySearchSeekMap implements SeekMap {
        private final long approxBytesPerFrame;
        private final long ceilingBytePosition;
        private final long ceilingTimePosition;
        private final long durationUs;
        private final long floorBytePosition;
        private final long floorTimePosition;
        private final SeekTimestampConverter seekTimestampConverter;

        public BinarySearchSeekMap(SeekTimestampConverter seekTimestampConverter, long durationUs, long floorTimePosition, long ceilingTimePosition, long floorBytePosition, long ceilingBytePosition, long approxBytesPerFrame) {
            this.seekTimestampConverter = seekTimestampConverter;
            this.durationUs = durationUs;
            this.floorTimePosition = floorTimePosition;
            this.ceilingTimePosition = ceilingTimePosition;
            this.floorBytePosition = floorBytePosition;
            this.ceilingBytePosition = ceilingBytePosition;
            this.approxBytesPerFrame = approxBytesPerFrame;
        }

        @Override // com.google.android.exoplayer2.extractor.SeekMap
        public boolean isSeekable() {
            return true;
        }

        @Override // com.google.android.exoplayer2.extractor.SeekMap
        public SeekMap.SeekPoints getSeekPoints(long timeUs) {
            long nextSearchPosition = SeekOperationParams.calculateNextSearchBytePosition(this.seekTimestampConverter.timeUsToTargetTime(timeUs), this.floorTimePosition, this.ceilingTimePosition, this.floorBytePosition, this.ceilingBytePosition, this.approxBytesPerFrame);
            return new SeekMap.SeekPoints(new SeekPoint(timeUs, nextSearchPosition));
        }

        @Override // com.google.android.exoplayer2.extractor.SeekMap
        public long getDurationUs() {
            return this.durationUs;
        }

        public long timeUsToTargetTime(long timeUs) {
            return this.seekTimestampConverter.timeUsToTargetTime(timeUs);
        }
    }
}
