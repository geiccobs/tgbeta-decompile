package com.google.android.exoplayer2;

import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectorResult;
/* loaded from: classes3.dex */
final class PlaybackInfo {
    private static final MediaSource.MediaPeriodId DUMMY_MEDIA_PERIOD_ID = new MediaSource.MediaPeriodId(new Object());
    public volatile long bufferedPositionUs;
    public final long contentPositionUs;
    public final boolean isLoading;
    public final MediaSource.MediaPeriodId loadingMediaPeriodId;
    public final MediaSource.MediaPeriodId periodId;
    public final ExoPlaybackException playbackError;
    public final int playbackState;
    public volatile long positionUs;
    public final long startPositionUs;
    public final Timeline timeline;
    public volatile long totalBufferedDurationUs;
    public final TrackGroupArray trackGroups;
    public final TrackSelectorResult trackSelectorResult;

    public static PlaybackInfo createDummy(long startPositionUs, TrackSelectorResult emptyTrackSelectorResult) {
        Timeline timeline = Timeline.EMPTY;
        MediaSource.MediaPeriodId mediaPeriodId = DUMMY_MEDIA_PERIOD_ID;
        return new PlaybackInfo(timeline, mediaPeriodId, startPositionUs, C.TIME_UNSET, 1, null, false, TrackGroupArray.EMPTY, emptyTrackSelectorResult, mediaPeriodId, startPositionUs, 0L, startPositionUs);
    }

    public PlaybackInfo(Timeline timeline, MediaSource.MediaPeriodId periodId, long startPositionUs, long contentPositionUs, int playbackState, ExoPlaybackException playbackError, boolean isLoading, TrackGroupArray trackGroups, TrackSelectorResult trackSelectorResult, MediaSource.MediaPeriodId loadingMediaPeriodId, long bufferedPositionUs, long totalBufferedDurationUs, long positionUs) {
        this.timeline = timeline;
        this.periodId = periodId;
        this.startPositionUs = startPositionUs;
        this.contentPositionUs = contentPositionUs;
        this.playbackState = playbackState;
        this.playbackError = playbackError;
        this.isLoading = isLoading;
        this.trackGroups = trackGroups;
        this.trackSelectorResult = trackSelectorResult;
        this.loadingMediaPeriodId = loadingMediaPeriodId;
        this.bufferedPositionUs = bufferedPositionUs;
        this.totalBufferedDurationUs = totalBufferedDurationUs;
        this.positionUs = positionUs;
    }

    public MediaSource.MediaPeriodId getDummyFirstMediaPeriodId(boolean shuffleModeEnabled, Timeline.Window window, Timeline.Period period) {
        if (this.timeline.isEmpty()) {
            return DUMMY_MEDIA_PERIOD_ID;
        }
        int firstWindowIndex = this.timeline.getFirstWindowIndex(shuffleModeEnabled);
        int firstPeriodIndex = this.timeline.getWindow(firstWindowIndex, window).firstPeriodIndex;
        int currentPeriodIndex = this.timeline.getIndexOfPeriod(this.periodId.periodUid);
        long windowSequenceNumber = -1;
        if (currentPeriodIndex != -1) {
            int currentWindowIndex = this.timeline.getPeriod(currentPeriodIndex, period).windowIndex;
            if (firstWindowIndex == currentWindowIndex) {
                windowSequenceNumber = this.periodId.windowSequenceNumber;
            }
        }
        return new MediaSource.MediaPeriodId(this.timeline.getUidOfPeriod(firstPeriodIndex), windowSequenceNumber);
    }

    public PlaybackInfo copyWithNewPosition(MediaSource.MediaPeriodId periodId, long positionUs, long contentPositionUs, long totalBufferedDurationUs) {
        return new PlaybackInfo(this.timeline, periodId, positionUs, periodId.isAd() ? contentPositionUs : -9223372036854775807L, this.playbackState, this.playbackError, this.isLoading, this.trackGroups, this.trackSelectorResult, this.loadingMediaPeriodId, this.bufferedPositionUs, totalBufferedDurationUs, positionUs);
    }

    public PlaybackInfo copyWithTimeline(Timeline timeline) {
        return new PlaybackInfo(timeline, this.periodId, this.startPositionUs, this.contentPositionUs, this.playbackState, this.playbackError, this.isLoading, this.trackGroups, this.trackSelectorResult, this.loadingMediaPeriodId, this.bufferedPositionUs, this.totalBufferedDurationUs, this.positionUs);
    }

    public PlaybackInfo copyWithPlaybackState(int playbackState) {
        return new PlaybackInfo(this.timeline, this.periodId, this.startPositionUs, this.contentPositionUs, playbackState, this.playbackError, this.isLoading, this.trackGroups, this.trackSelectorResult, this.loadingMediaPeriodId, this.bufferedPositionUs, this.totalBufferedDurationUs, this.positionUs);
    }

    public PlaybackInfo copyWithPlaybackError(ExoPlaybackException playbackError) {
        return new PlaybackInfo(this.timeline, this.periodId, this.startPositionUs, this.contentPositionUs, this.playbackState, playbackError, this.isLoading, this.trackGroups, this.trackSelectorResult, this.loadingMediaPeriodId, this.bufferedPositionUs, this.totalBufferedDurationUs, this.positionUs);
    }

    public PlaybackInfo copyWithIsLoading(boolean isLoading) {
        return new PlaybackInfo(this.timeline, this.periodId, this.startPositionUs, this.contentPositionUs, this.playbackState, this.playbackError, isLoading, this.trackGroups, this.trackSelectorResult, this.loadingMediaPeriodId, this.bufferedPositionUs, this.totalBufferedDurationUs, this.positionUs);
    }

    public PlaybackInfo copyWithTrackInfo(TrackGroupArray trackGroups, TrackSelectorResult trackSelectorResult) {
        return new PlaybackInfo(this.timeline, this.periodId, this.startPositionUs, this.contentPositionUs, this.playbackState, this.playbackError, this.isLoading, trackGroups, trackSelectorResult, this.loadingMediaPeriodId, this.bufferedPositionUs, this.totalBufferedDurationUs, this.positionUs);
    }

    public PlaybackInfo copyWithLoadingMediaPeriodId(MediaSource.MediaPeriodId loadingMediaPeriodId) {
        return new PlaybackInfo(this.timeline, this.periodId, this.startPositionUs, this.contentPositionUs, this.playbackState, this.playbackError, this.isLoading, this.trackGroups, this.trackSelectorResult, loadingMediaPeriodId, this.bufferedPositionUs, this.totalBufferedDurationUs, this.positionUs);
    }
}
