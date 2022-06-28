package com.google.android.exoplayer2.source;

import com.google.android.exoplayer2.Timeline;
/* loaded from: classes3.dex */
public abstract class ForwardingTimeline extends Timeline {
    protected final Timeline timeline;

    public ForwardingTimeline(Timeline timeline) {
        this.timeline = timeline;
    }

    @Override // com.google.android.exoplayer2.Timeline
    public int getWindowCount() {
        return this.timeline.getWindowCount();
    }

    @Override // com.google.android.exoplayer2.Timeline
    public int getNextWindowIndex(int windowIndex, int repeatMode, boolean shuffleModeEnabled) {
        return this.timeline.getNextWindowIndex(windowIndex, repeatMode, shuffleModeEnabled);
    }

    @Override // com.google.android.exoplayer2.Timeline
    public int getPreviousWindowIndex(int windowIndex, int repeatMode, boolean shuffleModeEnabled) {
        return this.timeline.getPreviousWindowIndex(windowIndex, repeatMode, shuffleModeEnabled);
    }

    @Override // com.google.android.exoplayer2.Timeline
    public int getLastWindowIndex(boolean shuffleModeEnabled) {
        return this.timeline.getLastWindowIndex(shuffleModeEnabled);
    }

    @Override // com.google.android.exoplayer2.Timeline
    public int getFirstWindowIndex(boolean shuffleModeEnabled) {
        return this.timeline.getFirstWindowIndex(shuffleModeEnabled);
    }

    @Override // com.google.android.exoplayer2.Timeline
    public Timeline.Window getWindow(int windowIndex, Timeline.Window window, long defaultPositionProjectionUs) {
        return this.timeline.getWindow(windowIndex, window, defaultPositionProjectionUs);
    }

    @Override // com.google.android.exoplayer2.Timeline
    public int getPeriodCount() {
        return this.timeline.getPeriodCount();
    }

    @Override // com.google.android.exoplayer2.Timeline
    public Timeline.Period getPeriod(int periodIndex, Timeline.Period period, boolean setIds) {
        return this.timeline.getPeriod(periodIndex, period, setIds);
    }

    @Override // com.google.android.exoplayer2.Timeline
    public int getIndexOfPeriod(Object uid) {
        return this.timeline.getIndexOfPeriod(uid);
    }

    @Override // com.google.android.exoplayer2.Timeline
    public Object getUidOfPeriod(int periodIndex) {
        return this.timeline.getUidOfPeriod(periodIndex);
    }
}
