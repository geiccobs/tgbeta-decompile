package com.google.android.exoplayer2.source;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
/* loaded from: classes3.dex */
public final class ClippingMediaSource extends CompositeMediaSource<Void> {
    private final boolean allowDynamicClippingUpdates;
    private IllegalClippingException clippingError;
    private ClippingTimeline clippingTimeline;
    private final boolean enableInitialDiscontinuity;
    private final long endUs;
    private final ArrayList<ClippingMediaPeriod> mediaPeriods;
    private final MediaSource mediaSource;
    private long periodEndUs;
    private long periodStartUs;
    private final boolean relativeToDefaultPosition;
    private final long startUs;
    private final Timeline.Window window;

    /* loaded from: classes3.dex */
    public static final class IllegalClippingException extends IOException {
        public static final int REASON_INVALID_PERIOD_COUNT = 0;
        public static final int REASON_NOT_SEEKABLE_TO_START = 1;
        public static final int REASON_START_EXCEEDS_END = 2;
        public final int reason;

        @Documented
        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes.dex */
        public @interface Reason {
        }

        public IllegalClippingException(int reason) {
            super("Illegal clipping: " + getReasonDescription(reason));
            this.reason = reason;
        }

        private static String getReasonDescription(int reason) {
            switch (reason) {
                case 0:
                    return "invalid period count";
                case 1:
                    return "not seekable to start";
                case 2:
                    return "start exceeds end";
                default:
                    return "unknown";
            }
        }
    }

    public ClippingMediaSource(MediaSource mediaSource, long startPositionUs, long endPositionUs) {
        this(mediaSource, startPositionUs, endPositionUs, true, false, false);
    }

    public ClippingMediaSource(MediaSource mediaSource, long durationUs) {
        this(mediaSource, 0L, durationUs, true, false, true);
    }

    public ClippingMediaSource(MediaSource mediaSource, long startPositionUs, long endPositionUs, boolean enableInitialDiscontinuity, boolean allowDynamicClippingUpdates, boolean relativeToDefaultPosition) {
        Assertions.checkArgument(startPositionUs >= 0);
        this.mediaSource = (MediaSource) Assertions.checkNotNull(mediaSource);
        this.startUs = startPositionUs;
        this.endUs = endPositionUs;
        this.enableInitialDiscontinuity = enableInitialDiscontinuity;
        this.allowDynamicClippingUpdates = allowDynamicClippingUpdates;
        this.relativeToDefaultPosition = relativeToDefaultPosition;
        this.mediaPeriods = new ArrayList<>();
        this.window = new Timeline.Window();
    }

    @Override // com.google.android.exoplayer2.source.BaseMediaSource, com.google.android.exoplayer2.source.MediaSource
    public Object getTag() {
        return this.mediaSource.getTag();
    }

    @Override // com.google.android.exoplayer2.source.CompositeMediaSource, com.google.android.exoplayer2.source.BaseMediaSource
    public void prepareSourceInternal(TransferListener mediaTransferListener) {
        super.prepareSourceInternal(mediaTransferListener);
        prepareChildSource(null, this.mediaSource);
    }

    @Override // com.google.android.exoplayer2.source.CompositeMediaSource, com.google.android.exoplayer2.source.MediaSource
    public void maybeThrowSourceInfoRefreshError() throws IOException {
        IllegalClippingException illegalClippingException = this.clippingError;
        if (illegalClippingException != null) {
            throw illegalClippingException;
        }
        super.maybeThrowSourceInfoRefreshError();
    }

    @Override // com.google.android.exoplayer2.source.MediaSource
    public MediaPeriod createPeriod(MediaSource.MediaPeriodId id, Allocator allocator, long startPositionUs) {
        ClippingMediaPeriod mediaPeriod = new ClippingMediaPeriod(this.mediaSource.createPeriod(id, allocator, startPositionUs), this.enableInitialDiscontinuity, this.periodStartUs, this.periodEndUs);
        this.mediaPeriods.add(mediaPeriod);
        return mediaPeriod;
    }

    @Override // com.google.android.exoplayer2.source.MediaSource
    public void releasePeriod(MediaPeriod mediaPeriod) {
        Assertions.checkState(this.mediaPeriods.remove(mediaPeriod));
        this.mediaSource.releasePeriod(((ClippingMediaPeriod) mediaPeriod).mediaPeriod);
        if (this.mediaPeriods.isEmpty() && !this.allowDynamicClippingUpdates) {
            refreshClippedTimeline(((ClippingTimeline) Assertions.checkNotNull(this.clippingTimeline)).timeline);
        }
    }

    @Override // com.google.android.exoplayer2.source.CompositeMediaSource, com.google.android.exoplayer2.source.BaseMediaSource
    public void releaseSourceInternal() {
        super.releaseSourceInternal();
        this.clippingError = null;
        this.clippingTimeline = null;
    }

    public void onChildSourceInfoRefreshed(Void id, MediaSource mediaSource, Timeline timeline) {
        if (this.clippingError != null) {
            return;
        }
        refreshClippedTimeline(timeline);
    }

    private void refreshClippedTimeline(Timeline timeline) {
        long windowStartUs;
        long windowEndUs;
        timeline.getWindow(0, this.window);
        long windowPositionInPeriodUs = this.window.getPositionInFirstPeriodUs();
        long j = Long.MIN_VALUE;
        if (this.clippingTimeline == null || this.mediaPeriods.isEmpty() || this.allowDynamicClippingUpdates) {
            long windowStartUs2 = this.startUs;
            long windowEndUs2 = this.endUs;
            if (this.relativeToDefaultPosition) {
                long windowDefaultPositionUs = this.window.getDefaultPositionUs();
                windowStartUs2 += windowDefaultPositionUs;
                windowEndUs2 += windowDefaultPositionUs;
            }
            this.periodStartUs = windowPositionInPeriodUs + windowStartUs2;
            if (this.endUs != Long.MIN_VALUE) {
                j = windowPositionInPeriodUs + windowEndUs2;
            }
            this.periodEndUs = j;
            int count = this.mediaPeriods.size();
            for (int i = 0; i < count; i++) {
                this.mediaPeriods.get(i).updateClipping(this.periodStartUs, this.periodEndUs);
            }
            windowStartUs = windowStartUs2;
            windowEndUs = windowEndUs2;
        } else {
            long windowStartUs3 = this.periodStartUs - windowPositionInPeriodUs;
            if (this.endUs != Long.MIN_VALUE) {
                j = this.periodEndUs - windowPositionInPeriodUs;
            }
            windowEndUs = j;
            windowStartUs = windowStartUs3;
        }
        try {
            ClippingTimeline clippingTimeline = new ClippingTimeline(timeline, windowStartUs, windowEndUs);
            this.clippingTimeline = clippingTimeline;
            refreshSourceInfo(clippingTimeline);
        } catch (IllegalClippingException e) {
            this.clippingError = e;
        }
    }

    public long getMediaTimeForChildMediaTime(Void id, long mediaTimeMs) {
        if (mediaTimeMs == C.TIME_UNSET) {
            return C.TIME_UNSET;
        }
        long startMs = C.usToMs(this.startUs);
        long clippedTimeMs = Math.max(0L, mediaTimeMs - startMs);
        long j = this.endUs;
        if (j != Long.MIN_VALUE) {
            return Math.min(C.usToMs(j) - startMs, clippedTimeMs);
        }
        return clippedTimeMs;
    }

    /* loaded from: classes3.dex */
    public static final class ClippingTimeline extends ForwardingTimeline {
        private final long durationUs;
        private final long endUs;
        private final boolean isDynamic;
        private final long startUs;

        public ClippingTimeline(Timeline timeline, long startUs, long endUs) throws IllegalClippingException {
            super(timeline);
            boolean z = false;
            if (timeline.getPeriodCount() == 1) {
                Timeline.Window window = timeline.getWindow(0, new Timeline.Window());
                long startUs2 = Math.max(0L, startUs);
                long resolvedEndUs = endUs == Long.MIN_VALUE ? window.durationUs : Math.max(0L, endUs);
                if (window.durationUs != C.TIME_UNSET) {
                    resolvedEndUs = resolvedEndUs > window.durationUs ? window.durationUs : resolvedEndUs;
                    if (startUs2 != 0 && !window.isSeekable) {
                        throw new IllegalClippingException(1);
                    }
                    if (startUs2 > resolvedEndUs) {
                        throw new IllegalClippingException(2);
                    }
                }
                this.startUs = startUs2;
                this.endUs = resolvedEndUs;
                this.durationUs = resolvedEndUs == C.TIME_UNSET ? -9223372036854775807L : resolvedEndUs - startUs2;
                if (window.isDynamic && (resolvedEndUs == C.TIME_UNSET || (window.durationUs != C.TIME_UNSET && resolvedEndUs == window.durationUs))) {
                    z = true;
                }
                this.isDynamic = z;
                return;
            }
            throw new IllegalClippingException(0);
        }

        @Override // com.google.android.exoplayer2.source.ForwardingTimeline, com.google.android.exoplayer2.Timeline
        public Timeline.Window getWindow(int windowIndex, Timeline.Window window, long defaultPositionProjectionUs) {
            this.timeline.getWindow(0, window, 0L);
            window.positionInFirstPeriodUs += this.startUs;
            window.durationUs = this.durationUs;
            window.isDynamic = this.isDynamic;
            if (window.defaultPositionUs != C.TIME_UNSET) {
                window.defaultPositionUs = Math.max(window.defaultPositionUs, this.startUs);
                window.defaultPositionUs = this.endUs == C.TIME_UNSET ? window.defaultPositionUs : Math.min(window.defaultPositionUs, this.endUs);
                window.defaultPositionUs -= this.startUs;
            }
            long startMs = C.usToMs(this.startUs);
            if (window.presentationStartTimeMs != C.TIME_UNSET) {
                window.presentationStartTimeMs += startMs;
            }
            if (window.windowStartTimeMs != C.TIME_UNSET) {
                window.windowStartTimeMs += startMs;
            }
            return window;
        }

        @Override // com.google.android.exoplayer2.source.ForwardingTimeline, com.google.android.exoplayer2.Timeline
        public Timeline.Period getPeriod(int periodIndex, Timeline.Period period, boolean setIds) {
            this.timeline.getPeriod(0, period, setIds);
            long positionInClippedWindowUs = period.getPositionInWindowUs() - this.startUs;
            long j = this.durationUs;
            long periodDurationUs = j == C.TIME_UNSET ? -9223372036854775807L : j - positionInClippedWindowUs;
            return period.set(period.id, period.uid, 0, periodDurationUs, positionInClippedWindowUs);
        }
    }
}
