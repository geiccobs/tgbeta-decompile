package com.google.android.exoplayer2.source;

import android.util.Pair;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
/* loaded from: classes3.dex */
public final class MaskingMediaSource extends CompositeMediaSource<Void> {
    private boolean hasStartedPreparing;
    private boolean isPrepared;
    private final MediaSource mediaSource;
    private MaskingTimeline timeline;
    private MaskingMediaPeriod unpreparedMaskingMediaPeriod;
    private MediaSourceEventListener.EventDispatcher unpreparedMaskingMediaPeriodEventDispatcher;
    private final boolean useLazyPreparation;
    private final Timeline.Window window = new Timeline.Window();
    private final Timeline.Period period = new Timeline.Period();

    public MaskingMediaSource(MediaSource mediaSource, boolean useLazyPreparation) {
        this.mediaSource = mediaSource;
        this.useLazyPreparation = useLazyPreparation;
        this.timeline = MaskingTimeline.createWithDummyTimeline(mediaSource.getTag());
    }

    public Timeline getTimeline() {
        return this.timeline;
    }

    @Override // com.google.android.exoplayer2.source.CompositeMediaSource, com.google.android.exoplayer2.source.BaseMediaSource
    public void prepareSourceInternal(TransferListener mediaTransferListener) {
        super.prepareSourceInternal(mediaTransferListener);
        if (!this.useLazyPreparation) {
            this.hasStartedPreparing = true;
            prepareChildSource(null, this.mediaSource);
        }
    }

    @Override // com.google.android.exoplayer2.source.BaseMediaSource, com.google.android.exoplayer2.source.MediaSource
    public Object getTag() {
        return this.mediaSource.getTag();
    }

    @Override // com.google.android.exoplayer2.source.CompositeMediaSource, com.google.android.exoplayer2.source.MediaSource
    public void maybeThrowSourceInfoRefreshError() throws IOException {
    }

    @Override // com.google.android.exoplayer2.source.MediaSource
    public MaskingMediaPeriod createPeriod(MediaSource.MediaPeriodId id, Allocator allocator, long startPositionUs) {
        MaskingMediaPeriod mediaPeriod = new MaskingMediaPeriod(this.mediaSource, id, allocator, startPositionUs);
        if (this.isPrepared) {
            MediaSource.MediaPeriodId idInSource = id.copyWithPeriodUid(getInternalPeriodUid(id.periodUid));
            mediaPeriod.createPeriod(idInSource);
        } else {
            this.unpreparedMaskingMediaPeriod = mediaPeriod;
            MediaSourceEventListener.EventDispatcher createEventDispatcher = createEventDispatcher(0, id, 0L);
            this.unpreparedMaskingMediaPeriodEventDispatcher = createEventDispatcher;
            createEventDispatcher.mediaPeriodCreated();
            if (!this.hasStartedPreparing) {
                this.hasStartedPreparing = true;
                prepareChildSource(null, this.mediaSource);
            }
        }
        return mediaPeriod;
    }

    @Override // com.google.android.exoplayer2.source.MediaSource
    public void releasePeriod(MediaPeriod mediaPeriod) {
        ((MaskingMediaPeriod) mediaPeriod).releasePeriod();
        if (mediaPeriod == this.unpreparedMaskingMediaPeriod) {
            ((MediaSourceEventListener.EventDispatcher) Assertions.checkNotNull(this.unpreparedMaskingMediaPeriodEventDispatcher)).mediaPeriodReleased();
            this.unpreparedMaskingMediaPeriodEventDispatcher = null;
            this.unpreparedMaskingMediaPeriod = null;
        }
    }

    @Override // com.google.android.exoplayer2.source.CompositeMediaSource, com.google.android.exoplayer2.source.BaseMediaSource
    public void releaseSourceInternal() {
        this.isPrepared = false;
        this.hasStartedPreparing = false;
        super.releaseSourceInternal();
    }

    public void onChildSourceInfoRefreshed(Void id, MediaSource mediaSource, Timeline newTimeline) {
        if (this.isPrepared) {
            this.timeline = this.timeline.cloneWithUpdatedTimeline(newTimeline);
        } else if (newTimeline.isEmpty()) {
            this.timeline = MaskingTimeline.createWithRealTimeline(newTimeline, Timeline.Window.SINGLE_WINDOW_UID, MaskingTimeline.DUMMY_EXTERNAL_PERIOD_UID);
        } else {
            newTimeline.getWindow(0, this.window);
            long windowStartPositionUs = this.window.getDefaultPositionUs();
            MaskingMediaPeriod maskingMediaPeriod = this.unpreparedMaskingMediaPeriod;
            if (maskingMediaPeriod != null) {
                long periodPreparePositionUs = maskingMediaPeriod.getPreparePositionUs();
                if (periodPreparePositionUs != 0) {
                    windowStartPositionUs = periodPreparePositionUs;
                }
            }
            Object windowUid = this.window.uid;
            Pair<Object, Long> periodPosition = newTimeline.getPeriodPosition(this.window, this.period, 0, windowStartPositionUs);
            Object periodUid = periodPosition.first;
            long periodPositionUs = ((Long) periodPosition.second).longValue();
            this.timeline = MaskingTimeline.createWithRealTimeline(newTimeline, windowUid, periodUid);
            if (this.unpreparedMaskingMediaPeriod != null) {
                MaskingMediaPeriod maskingPeriod = this.unpreparedMaskingMediaPeriod;
                maskingPeriod.overridePreparePositionUs(periodPositionUs);
                MediaSource.MediaPeriodId idInSource = maskingPeriod.id.copyWithPeriodUid(getInternalPeriodUid(maskingPeriod.id.periodUid));
                maskingPeriod.createPeriod(idInSource);
            }
        }
        this.isPrepared = true;
        refreshSourceInfo(this.timeline);
    }

    public MediaSource.MediaPeriodId getMediaPeriodIdForChildMediaPeriodId(Void id, MediaSource.MediaPeriodId mediaPeriodId) {
        return mediaPeriodId.copyWithPeriodUid(getExternalPeriodUid(mediaPeriodId.periodUid));
    }

    @Override // com.google.android.exoplayer2.source.CompositeMediaSource
    protected boolean shouldDispatchCreateOrReleaseEvent(MediaSource.MediaPeriodId mediaPeriodId) {
        MaskingMediaPeriod maskingMediaPeriod = this.unpreparedMaskingMediaPeriod;
        return maskingMediaPeriod == null || !mediaPeriodId.equals(maskingMediaPeriod.id);
    }

    private Object getInternalPeriodUid(Object externalPeriodUid) {
        if (!externalPeriodUid.equals(MaskingTimeline.DUMMY_EXTERNAL_PERIOD_UID)) {
            return externalPeriodUid;
        }
        return this.timeline.replacedInternalPeriodUid;
    }

    private Object getExternalPeriodUid(Object internalPeriodUid) {
        if (this.timeline.replacedInternalPeriodUid.equals(internalPeriodUid)) {
            return MaskingTimeline.DUMMY_EXTERNAL_PERIOD_UID;
        }
        return internalPeriodUid;
    }

    /* loaded from: classes3.dex */
    public static final class MaskingTimeline extends ForwardingTimeline {
        public static final Object DUMMY_EXTERNAL_PERIOD_UID = new Object();
        private final Object replacedInternalPeriodUid;
        private final Object replacedInternalWindowUid;

        public static MaskingTimeline createWithDummyTimeline(Object windowTag) {
            return new MaskingTimeline(new DummyTimeline(windowTag), Timeline.Window.SINGLE_WINDOW_UID, DUMMY_EXTERNAL_PERIOD_UID);
        }

        public static MaskingTimeline createWithRealTimeline(Timeline timeline, Object firstWindowUid, Object firstPeriodUid) {
            return new MaskingTimeline(timeline, firstWindowUid, firstPeriodUid);
        }

        private MaskingTimeline(Timeline timeline, Object replacedInternalWindowUid, Object replacedInternalPeriodUid) {
            super(timeline);
            this.replacedInternalWindowUid = replacedInternalWindowUid;
            this.replacedInternalPeriodUid = replacedInternalPeriodUid;
        }

        public MaskingTimeline cloneWithUpdatedTimeline(Timeline timeline) {
            return new MaskingTimeline(timeline, this.replacedInternalWindowUid, this.replacedInternalPeriodUid);
        }

        public Timeline getTimeline() {
            return this.timeline;
        }

        @Override // com.google.android.exoplayer2.source.ForwardingTimeline, com.google.android.exoplayer2.Timeline
        public Timeline.Window getWindow(int windowIndex, Timeline.Window window, long defaultPositionProjectionUs) {
            this.timeline.getWindow(windowIndex, window, defaultPositionProjectionUs);
            if (Util.areEqual(window.uid, this.replacedInternalWindowUid)) {
                window.uid = Timeline.Window.SINGLE_WINDOW_UID;
            }
            return window;
        }

        @Override // com.google.android.exoplayer2.source.ForwardingTimeline, com.google.android.exoplayer2.Timeline
        public Timeline.Period getPeriod(int periodIndex, Timeline.Period period, boolean setIds) {
            this.timeline.getPeriod(periodIndex, period, setIds);
            if (Util.areEqual(period.uid, this.replacedInternalPeriodUid)) {
                period.uid = DUMMY_EXTERNAL_PERIOD_UID;
            }
            return period;
        }

        @Override // com.google.android.exoplayer2.source.ForwardingTimeline, com.google.android.exoplayer2.Timeline
        public int getIndexOfPeriod(Object uid) {
            return this.timeline.getIndexOfPeriod(DUMMY_EXTERNAL_PERIOD_UID.equals(uid) ? this.replacedInternalPeriodUid : uid);
        }

        @Override // com.google.android.exoplayer2.source.ForwardingTimeline, com.google.android.exoplayer2.Timeline
        public Object getUidOfPeriod(int periodIndex) {
            Object uid = this.timeline.getUidOfPeriod(periodIndex);
            return Util.areEqual(uid, this.replacedInternalPeriodUid) ? DUMMY_EXTERNAL_PERIOD_UID : uid;
        }
    }

    /* loaded from: classes3.dex */
    public static final class DummyTimeline extends Timeline {
        private final Object tag;

        public DummyTimeline(Object tag) {
            this.tag = tag;
        }

        @Override // com.google.android.exoplayer2.Timeline
        public int getWindowCount() {
            return 1;
        }

        @Override // com.google.android.exoplayer2.Timeline
        public Timeline.Window getWindow(int windowIndex, Timeline.Window window, long defaultPositionProjectionUs) {
            return window.set(Timeline.Window.SINGLE_WINDOW_UID, this.tag, null, C.TIME_UNSET, C.TIME_UNSET, false, true, false, 0L, C.TIME_UNSET, 0, 0, 0L);
        }

        @Override // com.google.android.exoplayer2.Timeline
        public int getPeriodCount() {
            return 1;
        }

        @Override // com.google.android.exoplayer2.Timeline
        public Timeline.Period getPeriod(int periodIndex, Timeline.Period period, boolean setIds) {
            return period.set(0, MaskingTimeline.DUMMY_EXTERNAL_PERIOD_UID, 0, C.TIME_UNSET, 0L);
        }

        @Override // com.google.android.exoplayer2.Timeline
        public int getIndexOfPeriod(Object uid) {
            return uid == MaskingTimeline.DUMMY_EXTERNAL_PERIOD_UID ? 0 : -1;
        }

        @Override // com.google.android.exoplayer2.Timeline
        public Object getUidOfPeriod(int periodIndex) {
            return MaskingTimeline.DUMMY_EXTERNAL_PERIOD_UID;
        }
    }
}
