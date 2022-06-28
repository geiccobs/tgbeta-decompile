package com.google.android.exoplayer2.analytics;

import android.os.SystemClock;
import android.util.Pair;
import android.view.Surface;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.analytics.PlaybackSessionManager;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/* loaded from: classes3.dex */
public final class PlaybackStatsListener implements AnalyticsListener, PlaybackSessionManager.Listener {
    private String activeAdPlayback;
    private String activeContentPlayback;
    private final Callback callback;
    private boolean isSuppressed;
    private final boolean keepHistory;
    private final PlaybackSessionManager sessionManager;
    private final Map<String, PlaybackStatsTracker> playbackStatsTrackers = new HashMap();
    private final Map<String, AnalyticsListener.EventTime> sessionStartEventTimes = new HashMap();
    private PlaybackStats finishedPlaybackStats = PlaybackStats.EMPTY;
    private boolean playWhenReady = false;
    private int playbackState = 1;
    private float playbackSpeed = 1.0f;
    private final Timeline.Period period = new Timeline.Period();

    /* loaded from: classes3.dex */
    public interface Callback {
        void onPlaybackStatsReady(AnalyticsListener.EventTime eventTime, PlaybackStats playbackStats);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onAudioAttributesChanged(AnalyticsListener.EventTime eventTime, AudioAttributes audioAttributes) {
        AnalyticsListener.CC.$default$onAudioAttributesChanged(this, eventTime, audioAttributes);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onAudioSessionId(AnalyticsListener.EventTime eventTime, int i) {
        AnalyticsListener.CC.$default$onAudioSessionId(this, eventTime, i);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onDecoderDisabled(AnalyticsListener.EventTime eventTime, int i, DecoderCounters decoderCounters) {
        AnalyticsListener.CC.$default$onDecoderDisabled(this, eventTime, i, decoderCounters);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onDecoderEnabled(AnalyticsListener.EventTime eventTime, int i, DecoderCounters decoderCounters) {
        AnalyticsListener.CC.$default$onDecoderEnabled(this, eventTime, i, decoderCounters);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onDecoderInitialized(AnalyticsListener.EventTime eventTime, int i, String str, long j) {
        AnalyticsListener.CC.$default$onDecoderInitialized(this, eventTime, i, str, j);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onDecoderInputFormatChanged(AnalyticsListener.EventTime eventTime, int i, Format format) {
        AnalyticsListener.CC.$default$onDecoderInputFormatChanged(this, eventTime, i, format);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onDrmKeysLoaded(AnalyticsListener.EventTime eventTime) {
        AnalyticsListener.CC.$default$onDrmKeysLoaded(this, eventTime);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onDrmKeysRemoved(AnalyticsListener.EventTime eventTime) {
        AnalyticsListener.CC.$default$onDrmKeysRemoved(this, eventTime);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onDrmKeysRestored(AnalyticsListener.EventTime eventTime) {
        AnalyticsListener.CC.$default$onDrmKeysRestored(this, eventTime);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onDrmSessionAcquired(AnalyticsListener.EventTime eventTime) {
        AnalyticsListener.CC.$default$onDrmSessionAcquired(this, eventTime);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onDrmSessionReleased(AnalyticsListener.EventTime eventTime) {
        AnalyticsListener.CC.$default$onDrmSessionReleased(this, eventTime);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onIsPlayingChanged(AnalyticsListener.EventTime eventTime, boolean z) {
        AnalyticsListener.CC.$default$onIsPlayingChanged(this, eventTime, z);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onLoadCanceled(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        AnalyticsListener.CC.$default$onLoadCanceled(this, eventTime, loadEventInfo, mediaLoadData);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onLoadCompleted(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        AnalyticsListener.CC.$default$onLoadCompleted(this, eventTime, loadEventInfo, mediaLoadData);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onLoadingChanged(AnalyticsListener.EventTime eventTime, boolean z) {
        AnalyticsListener.CC.$default$onLoadingChanged(this, eventTime, z);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onMediaPeriodCreated(AnalyticsListener.EventTime eventTime) {
        AnalyticsListener.CC.$default$onMediaPeriodCreated(this, eventTime);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onMediaPeriodReleased(AnalyticsListener.EventTime eventTime) {
        AnalyticsListener.CC.$default$onMediaPeriodReleased(this, eventTime);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onMetadata(AnalyticsListener.EventTime eventTime, Metadata metadata) {
        AnalyticsListener.CC.$default$onMetadata(this, eventTime, metadata);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onReadingStarted(AnalyticsListener.EventTime eventTime) {
        AnalyticsListener.CC.$default$onReadingStarted(this, eventTime);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime, Surface surface) {
        AnalyticsListener.CC.$default$onRenderedFirstFrame(this, eventTime, surface);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onRepeatModeChanged(AnalyticsListener.EventTime eventTime, int i) {
        AnalyticsListener.CC.$default$onRepeatModeChanged(this, eventTime, i);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onShuffleModeChanged(AnalyticsListener.EventTime eventTime, boolean z) {
        AnalyticsListener.CC.$default$onShuffleModeChanged(this, eventTime, z);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onSurfaceSizeChanged(AnalyticsListener.EventTime eventTime, int i, int i2) {
        AnalyticsListener.CC.$default$onSurfaceSizeChanged(this, eventTime, i, i2);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onUpstreamDiscarded(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        AnalyticsListener.CC.$default$onUpstreamDiscarded(this, eventTime, mediaLoadData);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onVolumeChanged(AnalyticsListener.EventTime eventTime, float f) {
        AnalyticsListener.CC.$default$onVolumeChanged(this, eventTime, f);
    }

    public PlaybackStatsListener(boolean keepHistory, Callback callback) {
        this.callback = callback;
        this.keepHistory = keepHistory;
        DefaultPlaybackSessionManager defaultPlaybackSessionManager = new DefaultPlaybackSessionManager();
        this.sessionManager = defaultPlaybackSessionManager;
        defaultPlaybackSessionManager.setListener(this);
    }

    public PlaybackStats getCombinedPlaybackStats() {
        PlaybackStats[] allPendingPlaybackStats = new PlaybackStats[this.playbackStatsTrackers.size() + 1];
        allPendingPlaybackStats[0] = this.finishedPlaybackStats;
        int index = 1;
        for (PlaybackStatsTracker tracker : this.playbackStatsTrackers.values()) {
            allPendingPlaybackStats[index] = tracker.build(false);
            index++;
        }
        return PlaybackStats.merge(allPendingPlaybackStats);
    }

    public PlaybackStats getPlaybackStats() {
        PlaybackStatsTracker activeStatsTracker;
        String str = this.activeAdPlayback;
        if (str != null) {
            activeStatsTracker = this.playbackStatsTrackers.get(str);
        } else {
            String str2 = this.activeContentPlayback;
            if (str2 != null) {
                activeStatsTracker = this.playbackStatsTrackers.get(str2);
            } else {
                activeStatsTracker = null;
            }
        }
        if (activeStatsTracker == null) {
            return null;
        }
        return activeStatsTracker.build(false);
    }

    public void finishAllSessions() {
        AnalyticsListener.EventTime dummyEventTime = new AnalyticsListener.EventTime(SystemClock.elapsedRealtime(), Timeline.EMPTY, 0, null, 0L, 0L, 0L);
        this.sessionManager.finishAllSessions(dummyEventTime);
    }

    @Override // com.google.android.exoplayer2.analytics.PlaybackSessionManager.Listener
    public void onSessionCreated(AnalyticsListener.EventTime eventTime, String session) {
        PlaybackStatsTracker tracker = new PlaybackStatsTracker(this.keepHistory, eventTime);
        tracker.onPlayerStateChanged(eventTime, this.playWhenReady, this.playbackState, true);
        tracker.onIsSuppressedChanged(eventTime, this.isSuppressed, true);
        tracker.onPlaybackSpeedChanged(eventTime, this.playbackSpeed);
        this.playbackStatsTrackers.put(session, tracker);
        this.sessionStartEventTimes.put(session, eventTime);
    }

    @Override // com.google.android.exoplayer2.analytics.PlaybackSessionManager.Listener
    public void onSessionActive(AnalyticsListener.EventTime eventTime, String session) {
        ((PlaybackStatsTracker) Assertions.checkNotNull(this.playbackStatsTrackers.get(session))).onForeground(eventTime);
        if (eventTime.mediaPeriodId != null && eventTime.mediaPeriodId.isAd()) {
            this.activeAdPlayback = session;
        } else {
            this.activeContentPlayback = session;
        }
    }

    @Override // com.google.android.exoplayer2.analytics.PlaybackSessionManager.Listener
    public void onAdPlaybackStarted(AnalyticsListener.EventTime eventTime, String contentSession, String adSession) {
        Assertions.checkState(((MediaSource.MediaPeriodId) Assertions.checkNotNull(eventTime.mediaPeriodId)).isAd());
        long contentPeriodPositionUs = eventTime.timeline.getPeriodByUid(eventTime.mediaPeriodId.periodUid, this.period).getAdGroupTimeUs(eventTime.mediaPeriodId.adGroupIndex);
        long contentWindowPositionUs = Long.MIN_VALUE;
        if (contentPeriodPositionUs != Long.MIN_VALUE) {
            contentWindowPositionUs = this.period.getPositionInWindowUs() + contentPeriodPositionUs;
        }
        AnalyticsListener.EventTime contentEventTime = new AnalyticsListener.EventTime(eventTime.realtimeMs, eventTime.timeline, eventTime.windowIndex, new MediaSource.MediaPeriodId(eventTime.mediaPeriodId.periodUid, eventTime.mediaPeriodId.windowSequenceNumber, eventTime.mediaPeriodId.adGroupIndex), C.usToMs(contentWindowPositionUs), eventTime.currentPlaybackPositionMs, eventTime.totalBufferedDurationMs);
        ((PlaybackStatsTracker) Assertions.checkNotNull(this.playbackStatsTrackers.get(contentSession))).onInterruptedByAd(contentEventTime);
    }

    @Override // com.google.android.exoplayer2.analytics.PlaybackSessionManager.Listener
    public void onSessionFinished(AnalyticsListener.EventTime eventTime, String session, boolean automaticTransition) {
        if (session.equals(this.activeAdPlayback)) {
            this.activeAdPlayback = null;
        } else if (session.equals(this.activeContentPlayback)) {
            this.activeContentPlayback = null;
        }
        PlaybackStatsTracker tracker = (PlaybackStatsTracker) Assertions.checkNotNull(this.playbackStatsTrackers.remove(session));
        AnalyticsListener.EventTime startEventTime = (AnalyticsListener.EventTime) Assertions.checkNotNull(this.sessionStartEventTimes.remove(session));
        if (automaticTransition) {
            tracker.onPlayerStateChanged(eventTime, true, 4, false);
        }
        tracker.onFinished(eventTime);
        PlaybackStats playbackStats = tracker.build(true);
        this.finishedPlaybackStats = PlaybackStats.merge(this.finishedPlaybackStats, playbackStats);
        Callback callback = this.callback;
        if (callback != null) {
            callback.onPlaybackStatsReady(startEventTime, playbackStats);
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onPlayerStateChanged(AnalyticsListener.EventTime eventTime, boolean playWhenReady, int playbackState) {
        this.playWhenReady = playWhenReady;
        this.playbackState = playbackState;
        maybeAddSession(eventTime);
        for (String session : this.playbackStatsTrackers.keySet()) {
            boolean belongsToPlayback = this.sessionManager.belongsToSession(eventTime, session);
            this.playbackStatsTrackers.get(session).onPlayerStateChanged(eventTime, playWhenReady, playbackState, belongsToPlayback);
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onPlaybackSuppressionReasonChanged(AnalyticsListener.EventTime eventTime, int playbackSuppressionReason) {
        this.isSuppressed = playbackSuppressionReason != 0;
        maybeAddSession(eventTime);
        for (String session : this.playbackStatsTrackers.keySet()) {
            boolean belongsToPlayback = this.sessionManager.belongsToSession(eventTime, session);
            this.playbackStatsTrackers.get(session).onIsSuppressedChanged(eventTime, this.isSuppressed, belongsToPlayback);
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onTimelineChanged(AnalyticsListener.EventTime eventTime, int reason) {
        this.sessionManager.handleTimelineUpdate(eventTime);
        maybeAddSession(eventTime);
        for (String session : this.playbackStatsTrackers.keySet()) {
            if (this.sessionManager.belongsToSession(eventTime, session)) {
                this.playbackStatsTrackers.get(session).onPositionDiscontinuity(eventTime);
            }
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onPositionDiscontinuity(AnalyticsListener.EventTime eventTime, int reason) {
        this.sessionManager.handlePositionDiscontinuity(eventTime, reason);
        maybeAddSession(eventTime);
        for (String session : this.playbackStatsTrackers.keySet()) {
            if (this.sessionManager.belongsToSession(eventTime, session)) {
                this.playbackStatsTrackers.get(session).onPositionDiscontinuity(eventTime);
            }
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onSeekStarted(AnalyticsListener.EventTime eventTime) {
        maybeAddSession(eventTime);
        for (String session : this.playbackStatsTrackers.keySet()) {
            if (this.sessionManager.belongsToSession(eventTime, session)) {
                this.playbackStatsTrackers.get(session).onSeekStarted(eventTime);
            }
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onSeekProcessed(AnalyticsListener.EventTime eventTime) {
        maybeAddSession(eventTime);
        for (String session : this.playbackStatsTrackers.keySet()) {
            if (this.sessionManager.belongsToSession(eventTime, session)) {
                this.playbackStatsTrackers.get(session).onSeekProcessed(eventTime);
            }
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onPlayerError(AnalyticsListener.EventTime eventTime, ExoPlaybackException error) {
        maybeAddSession(eventTime);
        for (String session : this.playbackStatsTrackers.keySet()) {
            if (this.sessionManager.belongsToSession(eventTime, session)) {
                this.playbackStatsTrackers.get(session).onFatalError(eventTime, error);
            }
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onPlaybackParametersChanged(AnalyticsListener.EventTime eventTime, PlaybackParameters playbackParameters) {
        this.playbackSpeed = playbackParameters.speed;
        maybeAddSession(eventTime);
        for (PlaybackStatsTracker tracker : this.playbackStatsTrackers.values()) {
            tracker.onPlaybackSpeedChanged(eventTime, this.playbackSpeed);
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onTracksChanged(AnalyticsListener.EventTime eventTime, TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        maybeAddSession(eventTime);
        for (String session : this.playbackStatsTrackers.keySet()) {
            if (this.sessionManager.belongsToSession(eventTime, session)) {
                this.playbackStatsTrackers.get(session).onTracksChanged(eventTime, trackSelections);
            }
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onLoadStarted(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        maybeAddSession(eventTime);
        for (String session : this.playbackStatsTrackers.keySet()) {
            if (this.sessionManager.belongsToSession(eventTime, session)) {
                this.playbackStatsTrackers.get(session).onLoadStarted(eventTime);
            }
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onDownstreamFormatChanged(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        maybeAddSession(eventTime);
        for (String session : this.playbackStatsTrackers.keySet()) {
            if (this.sessionManager.belongsToSession(eventTime, session)) {
                this.playbackStatsTrackers.get(session).onDownstreamFormatChanged(eventTime, mediaLoadData);
            }
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onVideoSizeChanged(AnalyticsListener.EventTime eventTime, int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        maybeAddSession(eventTime);
        for (String session : this.playbackStatsTrackers.keySet()) {
            if (this.sessionManager.belongsToSession(eventTime, session)) {
                this.playbackStatsTrackers.get(session).onVideoSizeChanged(eventTime, width, height);
            }
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onBandwidthEstimate(AnalyticsListener.EventTime eventTime, int totalLoadTimeMs, long totalBytesLoaded, long bitrateEstimate) {
        maybeAddSession(eventTime);
        for (String session : this.playbackStatsTrackers.keySet()) {
            if (this.sessionManager.belongsToSession(eventTime, session)) {
                this.playbackStatsTrackers.get(session).onBandwidthData(totalLoadTimeMs, totalBytesLoaded);
            }
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onAudioUnderrun(AnalyticsListener.EventTime eventTime, int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {
        maybeAddSession(eventTime);
        for (String session : this.playbackStatsTrackers.keySet()) {
            if (this.sessionManager.belongsToSession(eventTime, session)) {
                this.playbackStatsTrackers.get(session).onAudioUnderrun();
            }
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onDroppedVideoFrames(AnalyticsListener.EventTime eventTime, int droppedFrames, long elapsedMs) {
        maybeAddSession(eventTime);
        for (String session : this.playbackStatsTrackers.keySet()) {
            if (this.sessionManager.belongsToSession(eventTime, session)) {
                this.playbackStatsTrackers.get(session).onDroppedVideoFrames(droppedFrames);
            }
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onLoadError(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {
        maybeAddSession(eventTime);
        for (String session : this.playbackStatsTrackers.keySet()) {
            if (this.sessionManager.belongsToSession(eventTime, session)) {
                this.playbackStatsTrackers.get(session).onNonFatalError(eventTime, error);
            }
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onDrmSessionManagerError(AnalyticsListener.EventTime eventTime, Exception error) {
        maybeAddSession(eventTime);
        for (String session : this.playbackStatsTrackers.keySet()) {
            if (this.sessionManager.belongsToSession(eventTime, session)) {
                this.playbackStatsTrackers.get(session).onNonFatalError(eventTime, error);
            }
        }
    }

    private void maybeAddSession(AnalyticsListener.EventTime eventTime) {
        boolean z = true;
        if (!eventTime.timeline.isEmpty() || this.playbackState != 1) {
            z = false;
        }
        boolean isCompletelyIdle = z;
        if (!isCompletelyIdle) {
            this.sessionManager.updateSessions(eventTime);
        }
    }

    /* loaded from: classes3.dex */
    private static final class PlaybackStatsTracker {
        private long audioFormatBitrateTimeProduct;
        private final List<Pair<AnalyticsListener.EventTime, Format>> audioFormatHistory;
        private long audioFormatTimeMs;
        private long audioUnderruns;
        private long bandwidthBytes;
        private long bandwidthTimeMs;
        private Format currentAudioFormat;
        private float currentPlaybackSpeed;
        private int currentPlaybackState;
        private long currentPlaybackStateStartTimeMs;
        private Format currentVideoFormat;
        private long droppedFrames;
        private int fatalErrorCount;
        private final List<Pair<AnalyticsListener.EventTime, Exception>> fatalErrorHistory;
        private long firstReportedTimeMs;
        private boolean hasBeenReady;
        private boolean hasEnded;
        private boolean hasFatalError;
        private long initialAudioFormatBitrate;
        private long initialVideoFormatBitrate;
        private int initialVideoFormatHeight;
        private final boolean isAd;
        private boolean isFinished;
        private boolean isForeground;
        private boolean isInterruptedByAd;
        private boolean isJoinTimeInvalid;
        private boolean isSeeking;
        private boolean isSuppressed;
        private final boolean keepHistory;
        private long lastAudioFormatStartTimeMs;
        private long lastRebufferStartTimeMs;
        private long lastVideoFormatStartTimeMs;
        private long maxRebufferTimeMs;
        private final List<long[]> mediaTimeHistory;
        private int nonFatalErrorCount;
        private final List<Pair<AnalyticsListener.EventTime, Exception>> nonFatalErrorHistory;
        private int pauseBufferCount;
        private int pauseCount;
        private boolean playWhenReady;
        private final long[] playbackStateDurationsMs = new long[16];
        private final List<Pair<AnalyticsListener.EventTime, Integer>> playbackStateHistory;
        private int playerPlaybackState;
        private int rebufferCount;
        private int seekCount;
        private boolean startedLoading;
        private long videoFormatBitrateTimeMs;
        private long videoFormatBitrateTimeProduct;
        private long videoFormatHeightTimeMs;
        private long videoFormatHeightTimeProduct;
        private final List<Pair<AnalyticsListener.EventTime, Format>> videoFormatHistory;

        public PlaybackStatsTracker(boolean keepHistory, AnalyticsListener.EventTime startTime) {
            this.keepHistory = keepHistory;
            this.playbackStateHistory = keepHistory ? new ArrayList<>() : Collections.emptyList();
            this.mediaTimeHistory = keepHistory ? new ArrayList<>() : Collections.emptyList();
            this.videoFormatHistory = keepHistory ? new ArrayList<>() : Collections.emptyList();
            this.audioFormatHistory = keepHistory ? new ArrayList<>() : Collections.emptyList();
            this.fatalErrorHistory = keepHistory ? new ArrayList<>() : Collections.emptyList();
            this.nonFatalErrorHistory = keepHistory ? new ArrayList<>() : Collections.emptyList();
            boolean z = false;
            this.currentPlaybackState = 0;
            this.currentPlaybackStateStartTimeMs = startTime.realtimeMs;
            this.playerPlaybackState = 1;
            this.firstReportedTimeMs = C.TIME_UNSET;
            this.maxRebufferTimeMs = C.TIME_UNSET;
            if (startTime.mediaPeriodId != null && startTime.mediaPeriodId.isAd()) {
                z = true;
            }
            this.isAd = z;
            this.initialAudioFormatBitrate = -1L;
            this.initialVideoFormatBitrate = -1L;
            this.initialVideoFormatHeight = -1;
            this.currentPlaybackSpeed = 1.0f;
        }

        public void onPlayerStateChanged(AnalyticsListener.EventTime eventTime, boolean playWhenReady, int playbackState, boolean belongsToPlayback) {
            this.playWhenReady = playWhenReady;
            this.playerPlaybackState = playbackState;
            if (playbackState != 1) {
                this.hasFatalError = false;
            }
            if (playbackState == 1 || playbackState == 4) {
                this.isInterruptedByAd = false;
            }
            maybeUpdatePlaybackState(eventTime, belongsToPlayback);
        }

        public void onIsSuppressedChanged(AnalyticsListener.EventTime eventTime, boolean isSuppressed, boolean belongsToPlayback) {
            this.isSuppressed = isSuppressed;
            maybeUpdatePlaybackState(eventTime, belongsToPlayback);
        }

        public void onPositionDiscontinuity(AnalyticsListener.EventTime eventTime) {
            this.isInterruptedByAd = false;
            maybeUpdatePlaybackState(eventTime, true);
        }

        public void onSeekStarted(AnalyticsListener.EventTime eventTime) {
            this.isSeeking = true;
            maybeUpdatePlaybackState(eventTime, true);
        }

        public void onSeekProcessed(AnalyticsListener.EventTime eventTime) {
            this.isSeeking = false;
            maybeUpdatePlaybackState(eventTime, true);
        }

        public void onFatalError(AnalyticsListener.EventTime eventTime, Exception error) {
            this.fatalErrorCount++;
            if (this.keepHistory) {
                this.fatalErrorHistory.add(Pair.create(eventTime, error));
            }
            this.hasFatalError = true;
            this.isInterruptedByAd = false;
            this.isSeeking = false;
            maybeUpdatePlaybackState(eventTime, true);
        }

        public void onLoadStarted(AnalyticsListener.EventTime eventTime) {
            this.startedLoading = true;
            maybeUpdatePlaybackState(eventTime, true);
        }

        public void onForeground(AnalyticsListener.EventTime eventTime) {
            this.isForeground = true;
            maybeUpdatePlaybackState(eventTime, true);
        }

        public void onInterruptedByAd(AnalyticsListener.EventTime eventTime) {
            this.isInterruptedByAd = true;
            this.isSeeking = false;
            maybeUpdatePlaybackState(eventTime, true);
        }

        public void onFinished(AnalyticsListener.EventTime eventTime) {
            this.isFinished = true;
            maybeUpdatePlaybackState(eventTime, false);
        }

        public void onTracksChanged(AnalyticsListener.EventTime eventTime, TrackSelectionArray trackSelections) {
            TrackSelection[] all;
            boolean videoEnabled = false;
            boolean audioEnabled = false;
            for (TrackSelection trackSelection : trackSelections.getAll()) {
                if (trackSelection != null && trackSelection.length() > 0) {
                    int trackType = MimeTypes.getTrackType(trackSelection.getFormat(0).sampleMimeType);
                    if (trackType == 2) {
                        videoEnabled = true;
                    } else if (trackType == 1) {
                        audioEnabled = true;
                    }
                }
            }
            if (!videoEnabled) {
                maybeUpdateVideoFormat(eventTime, null);
            }
            if (!audioEnabled) {
                maybeUpdateAudioFormat(eventTime, null);
            }
        }

        public void onDownstreamFormatChanged(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.MediaLoadData mediaLoadData) {
            if (mediaLoadData.trackType == 2 || mediaLoadData.trackType == 0) {
                maybeUpdateVideoFormat(eventTime, mediaLoadData.trackFormat);
            } else if (mediaLoadData.trackType == 1) {
                maybeUpdateAudioFormat(eventTime, mediaLoadData.trackFormat);
            }
        }

        public void onVideoSizeChanged(AnalyticsListener.EventTime eventTime, int width, int height) {
            Format format = this.currentVideoFormat;
            if (format != null && format.height == -1) {
                Format formatWithHeight = this.currentVideoFormat.copyWithVideoSize(width, height);
                maybeUpdateVideoFormat(eventTime, formatWithHeight);
            }
        }

        public void onPlaybackSpeedChanged(AnalyticsListener.EventTime eventTime, float playbackSpeed) {
            maybeUpdateMediaTimeHistory(eventTime.realtimeMs, eventTime.eventPlaybackPositionMs);
            maybeRecordVideoFormatTime(eventTime.realtimeMs);
            maybeRecordAudioFormatTime(eventTime.realtimeMs);
            this.currentPlaybackSpeed = playbackSpeed;
        }

        public void onAudioUnderrun() {
            this.audioUnderruns++;
        }

        public void onDroppedVideoFrames(int droppedFrames) {
            this.droppedFrames += droppedFrames;
        }

        public void onBandwidthData(long timeMs, long bytes) {
            this.bandwidthTimeMs += timeMs;
            this.bandwidthBytes += bytes;
        }

        public void onNonFatalError(AnalyticsListener.EventTime eventTime, Exception error) {
            this.nonFatalErrorCount++;
            if (this.keepHistory) {
                this.nonFatalErrorHistory.add(Pair.create(eventTime, error));
            }
        }

        public PlaybackStats build(boolean isFinal) {
            long validJoinTimeMs;
            long[] playbackStateDurationsMs = this.playbackStateDurationsMs;
            List<long[]> mediaTimeHistory = this.mediaTimeHistory;
            if (!isFinal) {
                long buildTimeMs = SystemClock.elapsedRealtime();
                playbackStateDurationsMs = Arrays.copyOf(this.playbackStateDurationsMs, 16);
                long lastStateDurationMs = Math.max(0L, buildTimeMs - this.currentPlaybackStateStartTimeMs);
                int i = this.currentPlaybackState;
                playbackStateDurationsMs[i] = playbackStateDurationsMs[i] + lastStateDurationMs;
                maybeUpdateMaxRebufferTimeMs(buildTimeMs);
                maybeRecordVideoFormatTime(buildTimeMs);
                maybeRecordAudioFormatTime(buildTimeMs);
                mediaTimeHistory = new ArrayList(this.mediaTimeHistory);
                if (this.keepHistory && this.currentPlaybackState == 3) {
                    mediaTimeHistory.add(guessMediaTimeBasedOnElapsedRealtime(buildTimeMs));
                }
            }
            boolean isJoinTimeInvalid = this.isJoinTimeInvalid || !this.hasBeenReady;
            if (isJoinTimeInvalid) {
                validJoinTimeMs = -9223372036854775807L;
            } else {
                validJoinTimeMs = playbackStateDurationsMs[2];
            }
            boolean hasBackgroundJoin = playbackStateDurationsMs[1] > 0;
            List<Pair<AnalyticsListener.EventTime, Format>> videoHistory = isFinal ? this.videoFormatHistory : new ArrayList<>(this.videoFormatHistory);
            List<Pair<AnalyticsListener.EventTime, Format>> audioHistory = isFinal ? this.audioFormatHistory : new ArrayList<>(this.audioFormatHistory);
            List arrayList = isFinal ? this.playbackStateHistory : new ArrayList(this.playbackStateHistory);
            long j = this.firstReportedTimeMs;
            boolean z = this.isForeground;
            int i2 = !this.hasBeenReady ? 1 : 0;
            boolean z2 = this.hasEnded;
            int i3 = hasBackgroundJoin ? 1 : 0;
            int i4 = isJoinTimeInvalid ? 0 : 1;
            int i5 = this.pauseCount;
            int i6 = this.pauseBufferCount;
            int i7 = this.seekCount;
            int i8 = this.rebufferCount;
            long j2 = this.maxRebufferTimeMs;
            boolean z3 = this.isAd;
            long j3 = this.videoFormatHeightTimeMs;
            long j4 = this.videoFormatHeightTimeProduct;
            long j5 = this.videoFormatBitrateTimeMs;
            long j6 = this.videoFormatBitrateTimeProduct;
            long j7 = this.audioFormatTimeMs;
            long j8 = this.audioFormatBitrateTimeProduct;
            int i9 = this.initialVideoFormatHeight;
            int i10 = i9 == -1 ? 0 : 1;
            long j9 = this.initialVideoFormatBitrate;
            int i11 = j9 == -1 ? 0 : 1;
            long j10 = this.initialAudioFormatBitrate;
            int i12 = j10 == -1 ? 0 : 1;
            long j11 = this.bandwidthTimeMs;
            long j12 = this.bandwidthBytes;
            long j13 = this.droppedFrames;
            long j14 = this.audioUnderruns;
            int i13 = this.fatalErrorCount;
            return new PlaybackStats(1, playbackStateDurationsMs, arrayList, mediaTimeHistory, j, z ? 1 : 0, i2, z2 ? 1 : 0, i3, validJoinTimeMs, i4, i5, i6, i7, i8, j2, z3 ? 1 : 0, videoHistory, audioHistory, j3, j4, j5, j6, j7, j8, i10, i11, i9, j9, i12, j10, j11, j12, j13, j14, i13 > 0 ? 1 : 0, i13, this.nonFatalErrorCount, this.fatalErrorHistory, this.nonFatalErrorHistory);
        }

        private void maybeUpdatePlaybackState(AnalyticsListener.EventTime eventTime, boolean belongsToPlayback) {
            int newPlaybackState = resolveNewPlaybackState();
            if (newPlaybackState == this.currentPlaybackState) {
                return;
            }
            boolean z = false;
            Assertions.checkArgument(eventTime.realtimeMs >= this.currentPlaybackStateStartTimeMs);
            long stateDurationMs = eventTime.realtimeMs - this.currentPlaybackStateStartTimeMs;
            long[] jArr = this.playbackStateDurationsMs;
            int i = this.currentPlaybackState;
            jArr[i] = jArr[i] + stateDurationMs;
            long j = this.firstReportedTimeMs;
            long j2 = C.TIME_UNSET;
            if (j == C.TIME_UNSET) {
                this.firstReportedTimeMs = eventTime.realtimeMs;
            }
            this.isJoinTimeInvalid |= isInvalidJoinTransition(this.currentPlaybackState, newPlaybackState);
            this.hasBeenReady |= isReadyState(newPlaybackState);
            boolean z2 = this.hasEnded;
            if (newPlaybackState == 11) {
                z = true;
            }
            this.hasEnded = z2 | z;
            if (!isPausedState(this.currentPlaybackState) && isPausedState(newPlaybackState)) {
                this.pauseCount++;
            }
            if (newPlaybackState == 5) {
                this.seekCount++;
            }
            if (!isRebufferingState(this.currentPlaybackState) && isRebufferingState(newPlaybackState)) {
                this.rebufferCount++;
                this.lastRebufferStartTimeMs = eventTime.realtimeMs;
            }
            if (isRebufferingState(this.currentPlaybackState) && this.currentPlaybackState != 7 && newPlaybackState == 7) {
                this.pauseBufferCount++;
            }
            long j3 = eventTime.realtimeMs;
            if (belongsToPlayback) {
                j2 = eventTime.eventPlaybackPositionMs;
            }
            maybeUpdateMediaTimeHistory(j3, j2);
            maybeUpdateMaxRebufferTimeMs(eventTime.realtimeMs);
            maybeRecordVideoFormatTime(eventTime.realtimeMs);
            maybeRecordAudioFormatTime(eventTime.realtimeMs);
            this.currentPlaybackState = newPlaybackState;
            this.currentPlaybackStateStartTimeMs = eventTime.realtimeMs;
            if (this.keepHistory) {
                this.playbackStateHistory.add(Pair.create(eventTime, Integer.valueOf(this.currentPlaybackState)));
            }
        }

        private int resolveNewPlaybackState() {
            if (this.isFinished) {
                return this.currentPlaybackState == 11 ? 11 : 15;
            } else if (this.isSeeking) {
                return 5;
            } else {
                if (this.hasFatalError) {
                    return 13;
                }
                if (!this.isForeground) {
                    return this.startedLoading ? 1 : 0;
                } else if (this.isInterruptedByAd) {
                    return 14;
                } else {
                    int i = this.playerPlaybackState;
                    if (i == 4) {
                        return 11;
                    }
                    if (i == 2) {
                        int i2 = this.currentPlaybackState;
                        if (i2 == 0 || i2 == 1 || i2 == 2 || i2 == 14) {
                            return 2;
                        }
                        if (i2 == 5 || i2 == 8) {
                            return 8;
                        }
                        if (!this.playWhenReady) {
                            return 7;
                        }
                        if (this.isSuppressed) {
                            return 10;
                        }
                        return 6;
                    } else if (i == 3) {
                        if (!this.playWhenReady) {
                            return 4;
                        }
                        if (!this.isSuppressed) {
                            return 3;
                        }
                        return 9;
                    } else if (i == 1 && this.currentPlaybackState != 0) {
                        return 12;
                    } else {
                        return this.currentPlaybackState;
                    }
                }
            }
        }

        private void maybeUpdateMaxRebufferTimeMs(long nowMs) {
            if (isRebufferingState(this.currentPlaybackState)) {
                long rebufferDurationMs = nowMs - this.lastRebufferStartTimeMs;
                long j = this.maxRebufferTimeMs;
                if (j == C.TIME_UNSET || rebufferDurationMs > j) {
                    this.maxRebufferTimeMs = rebufferDurationMs;
                }
            }
        }

        private void maybeUpdateMediaTimeHistory(long realtimeMs, long mediaTimeMs) {
            if (!this.keepHistory) {
                return;
            }
            if (this.currentPlaybackState != 3) {
                if (mediaTimeMs == C.TIME_UNSET) {
                    return;
                }
                if (!this.mediaTimeHistory.isEmpty()) {
                    List<long[]> list = this.mediaTimeHistory;
                    long previousMediaTimeMs = list.get(list.size() - 1)[1];
                    if (previousMediaTimeMs != mediaTimeMs) {
                        this.mediaTimeHistory.add(new long[]{realtimeMs, previousMediaTimeMs});
                    }
                }
            }
            this.mediaTimeHistory.add(mediaTimeMs == C.TIME_UNSET ? guessMediaTimeBasedOnElapsedRealtime(realtimeMs) : new long[]{realtimeMs, mediaTimeMs});
        }

        private long[] guessMediaTimeBasedOnElapsedRealtime(long realtimeMs) {
            List<long[]> list = this.mediaTimeHistory;
            long[] previousKnownMediaTimeHistory = list.get(list.size() - 1);
            long previousRealtimeMs = previousKnownMediaTimeHistory[0];
            long previousMediaTimeMs = previousKnownMediaTimeHistory[1];
            long elapsedMediaTimeEstimateMs = ((float) (realtimeMs - previousRealtimeMs)) * this.currentPlaybackSpeed;
            long mediaTimeEstimateMs = previousMediaTimeMs + elapsedMediaTimeEstimateMs;
            return new long[]{realtimeMs, mediaTimeEstimateMs};
        }

        private void maybeUpdateVideoFormat(AnalyticsListener.EventTime eventTime, Format newFormat) {
            if (Util.areEqual(this.currentVideoFormat, newFormat)) {
                return;
            }
            maybeRecordVideoFormatTime(eventTime.realtimeMs);
            if (newFormat != null) {
                if (this.initialVideoFormatHeight == -1 && newFormat.height != -1) {
                    this.initialVideoFormatHeight = newFormat.height;
                }
                if (this.initialVideoFormatBitrate == -1 && newFormat.bitrate != -1) {
                    this.initialVideoFormatBitrate = newFormat.bitrate;
                }
            }
            this.currentVideoFormat = newFormat;
            if (this.keepHistory) {
                this.videoFormatHistory.add(Pair.create(eventTime, newFormat));
            }
        }

        private void maybeUpdateAudioFormat(AnalyticsListener.EventTime eventTime, Format newFormat) {
            if (Util.areEqual(this.currentAudioFormat, newFormat)) {
                return;
            }
            maybeRecordAudioFormatTime(eventTime.realtimeMs);
            if (newFormat != null && this.initialAudioFormatBitrate == -1 && newFormat.bitrate != -1) {
                this.initialAudioFormatBitrate = newFormat.bitrate;
            }
            this.currentAudioFormat = newFormat;
            if (this.keepHistory) {
                this.audioFormatHistory.add(Pair.create(eventTime, newFormat));
            }
        }

        private void maybeRecordVideoFormatTime(long nowMs) {
            Format format;
            if (this.currentPlaybackState == 3 && (format = this.currentVideoFormat) != null) {
                long mediaDurationMs = ((float) (nowMs - this.lastVideoFormatStartTimeMs)) * this.currentPlaybackSpeed;
                if (format.height != -1) {
                    this.videoFormatHeightTimeMs += mediaDurationMs;
                    this.videoFormatHeightTimeProduct += this.currentVideoFormat.height * mediaDurationMs;
                }
                if (this.currentVideoFormat.bitrate != -1) {
                    this.videoFormatBitrateTimeMs += mediaDurationMs;
                    this.videoFormatBitrateTimeProduct += this.currentVideoFormat.bitrate * mediaDurationMs;
                }
            }
            this.lastVideoFormatStartTimeMs = nowMs;
        }

        private void maybeRecordAudioFormatTime(long nowMs) {
            Format format;
            if (this.currentPlaybackState == 3 && (format = this.currentAudioFormat) != null && format.bitrate != -1) {
                long mediaDurationMs = ((float) (nowMs - this.lastAudioFormatStartTimeMs)) * this.currentPlaybackSpeed;
                this.audioFormatTimeMs += mediaDurationMs;
                this.audioFormatBitrateTimeProduct += this.currentAudioFormat.bitrate * mediaDurationMs;
            }
            this.lastAudioFormatStartTimeMs = nowMs;
        }

        private static boolean isReadyState(int state) {
            return state == 3 || state == 4 || state == 9;
        }

        private static boolean isPausedState(int state) {
            return state == 4 || state == 7;
        }

        private static boolean isRebufferingState(int state) {
            return state == 6 || state == 7 || state == 10;
        }

        private static boolean isInvalidJoinTransition(int oldState, int newState) {
            return ((oldState != 1 && oldState != 2 && oldState != 14) || newState == 1 || newState == 2 || newState == 14 || newState == 3 || newState == 4 || newState == 9 || newState == 11) ? false : true;
        }
    }
}
