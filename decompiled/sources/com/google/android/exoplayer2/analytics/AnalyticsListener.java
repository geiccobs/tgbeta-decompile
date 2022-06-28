package com.google.android.exoplayer2.analytics;

import android.view.Surface;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import java.io.IOException;
/* loaded from: classes3.dex */
public interface AnalyticsListener {
    void onAudioAttributesChanged(EventTime eventTime, AudioAttributes audioAttributes);

    void onAudioSessionId(EventTime eventTime, int i);

    void onAudioUnderrun(EventTime eventTime, int i, long j, long j2);

    void onBandwidthEstimate(EventTime eventTime, int i, long j, long j2);

    void onDecoderDisabled(EventTime eventTime, int i, DecoderCounters decoderCounters);

    void onDecoderEnabled(EventTime eventTime, int i, DecoderCounters decoderCounters);

    void onDecoderInitialized(EventTime eventTime, int i, String str, long j);

    void onDecoderInputFormatChanged(EventTime eventTime, int i, Format format);

    void onDownstreamFormatChanged(EventTime eventTime, MediaSourceEventListener.MediaLoadData mediaLoadData);

    void onDrmKeysLoaded(EventTime eventTime);

    void onDrmKeysRemoved(EventTime eventTime);

    void onDrmKeysRestored(EventTime eventTime);

    void onDrmSessionAcquired(EventTime eventTime);

    void onDrmSessionManagerError(EventTime eventTime, Exception exc);

    void onDrmSessionReleased(EventTime eventTime);

    void onDroppedVideoFrames(EventTime eventTime, int i, long j);

    void onIsPlayingChanged(EventTime eventTime, boolean z);

    void onLoadCanceled(EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData);

    void onLoadCompleted(EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData);

    void onLoadError(EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData, IOException iOException, boolean z);

    void onLoadStarted(EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData);

    void onLoadingChanged(EventTime eventTime, boolean z);

    void onMediaPeriodCreated(EventTime eventTime);

    void onMediaPeriodReleased(EventTime eventTime);

    void onMetadata(EventTime eventTime, Metadata metadata);

    void onPlaybackParametersChanged(EventTime eventTime, PlaybackParameters playbackParameters);

    void onPlaybackSuppressionReasonChanged(EventTime eventTime, int i);

    void onPlayerError(EventTime eventTime, ExoPlaybackException exoPlaybackException);

    void onPlayerStateChanged(EventTime eventTime, boolean z, int i);

    void onPositionDiscontinuity(EventTime eventTime, int i);

    void onReadingStarted(EventTime eventTime);

    void onRenderedFirstFrame(EventTime eventTime, Surface surface);

    void onRepeatModeChanged(EventTime eventTime, int i);

    void onSeekProcessed(EventTime eventTime);

    void onSeekStarted(EventTime eventTime);

    void onShuffleModeChanged(EventTime eventTime, boolean z);

    void onSurfaceSizeChanged(EventTime eventTime, int i, int i2);

    void onTimelineChanged(EventTime eventTime, int i);

    void onTracksChanged(EventTime eventTime, TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray);

    void onUpstreamDiscarded(EventTime eventTime, MediaSourceEventListener.MediaLoadData mediaLoadData);

    void onVideoSizeChanged(EventTime eventTime, int i, int i2, int i3, float f);

    void onVolumeChanged(EventTime eventTime, float f);

    /* loaded from: classes3.dex */
    public static final class EventTime {
        public final long currentPlaybackPositionMs;
        public final long eventPlaybackPositionMs;
        public final MediaSource.MediaPeriodId mediaPeriodId;
        public final long realtimeMs;
        public final Timeline timeline;
        public final long totalBufferedDurationMs;
        public final int windowIndex;

        public EventTime(long realtimeMs, Timeline timeline, int windowIndex, MediaSource.MediaPeriodId mediaPeriodId, long eventPlaybackPositionMs, long currentPlaybackPositionMs, long totalBufferedDurationMs) {
            this.realtimeMs = realtimeMs;
            this.timeline = timeline;
            this.windowIndex = windowIndex;
            this.mediaPeriodId = mediaPeriodId;
            this.eventPlaybackPositionMs = eventPlaybackPositionMs;
            this.currentPlaybackPositionMs = currentPlaybackPositionMs;
            this.totalBufferedDurationMs = totalBufferedDurationMs;
        }
    }

    /* renamed from: com.google.android.exoplayer2.analytics.AnalyticsListener$-CC */
    /* loaded from: classes3.dex */
    public final /* synthetic */ class CC {
        public static void $default$onPlayerStateChanged(AnalyticsListener _this, EventTime eventTime, boolean playWhenReady, int playbackState) {
        }

        public static void $default$onPlaybackSuppressionReasonChanged(AnalyticsListener _this, EventTime eventTime, int playbackSuppressionReason) {
        }

        public static void $default$onIsPlayingChanged(AnalyticsListener _this, EventTime eventTime, boolean isPlaying) {
        }

        public static void $default$onTimelineChanged(AnalyticsListener _this, EventTime eventTime, int reason) {
        }

        public static void $default$onPositionDiscontinuity(AnalyticsListener _this, EventTime eventTime, int reason) {
        }

        public static void $default$onSeekStarted(AnalyticsListener _this, EventTime eventTime) {
        }

        public static void $default$onSeekProcessed(AnalyticsListener _this, EventTime eventTime) {
        }

        public static void $default$onPlaybackParametersChanged(AnalyticsListener _this, EventTime eventTime, PlaybackParameters playbackParameters) {
        }

        public static void $default$onRepeatModeChanged(AnalyticsListener _this, EventTime eventTime, int repeatMode) {
        }

        public static void $default$onShuffleModeChanged(AnalyticsListener _this, EventTime eventTime, boolean shuffleModeEnabled) {
        }

        public static void $default$onLoadingChanged(AnalyticsListener _this, EventTime eventTime, boolean isLoading) {
        }

        public static void $default$onPlayerError(AnalyticsListener _this, EventTime eventTime, ExoPlaybackException error) {
        }

        public static void $default$onTracksChanged(AnalyticsListener _this, EventTime eventTime, TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        }

        public static void $default$onLoadStarted(AnalyticsListener _this, EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        }

        public static void $default$onLoadCompleted(AnalyticsListener _this, EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        }

        public static void $default$onLoadCanceled(AnalyticsListener _this, EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        }

        public static void $default$onLoadError(AnalyticsListener _this, EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {
        }

        public static void $default$onDownstreamFormatChanged(AnalyticsListener _this, EventTime eventTime, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        }

        public static void $default$onUpstreamDiscarded(AnalyticsListener _this, EventTime eventTime, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        }

        public static void $default$onMediaPeriodCreated(AnalyticsListener _this, EventTime eventTime) {
        }

        public static void $default$onMediaPeriodReleased(AnalyticsListener _this, EventTime eventTime) {
        }

        public static void $default$onReadingStarted(AnalyticsListener _this, EventTime eventTime) {
        }

        public static void $default$onBandwidthEstimate(AnalyticsListener _this, EventTime eventTime, int totalLoadTimeMs, long totalBytesLoaded, long bitrateEstimate) {
        }

        public static void $default$onSurfaceSizeChanged(AnalyticsListener _this, EventTime eventTime, int width, int height) {
        }

        public static void $default$onMetadata(AnalyticsListener _this, EventTime eventTime, Metadata metadata) {
        }

        public static void $default$onDecoderEnabled(AnalyticsListener _this, EventTime eventTime, int trackType, DecoderCounters decoderCounters) {
        }

        public static void $default$onDecoderInitialized(AnalyticsListener _this, EventTime eventTime, int trackType, String decoderName, long initializationDurationMs) {
        }

        public static void $default$onDecoderInputFormatChanged(AnalyticsListener _this, EventTime eventTime, int trackType, Format format) {
        }

        public static void $default$onDecoderDisabled(AnalyticsListener _this, EventTime eventTime, int trackType, DecoderCounters decoderCounters) {
        }

        public static void $default$onAudioSessionId(AnalyticsListener _this, EventTime eventTime, int audioSessionId) {
        }

        public static void $default$onAudioAttributesChanged(AnalyticsListener _this, EventTime eventTime, AudioAttributes audioAttributes) {
        }

        public static void $default$onVolumeChanged(AnalyticsListener _this, EventTime eventTime, float volume) {
        }

        public static void $default$onAudioUnderrun(AnalyticsListener _this, EventTime eventTime, int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {
        }

        public static void $default$onDroppedVideoFrames(AnalyticsListener _this, EventTime eventTime, int droppedFrames, long elapsedMs) {
        }

        public static void $default$onVideoSizeChanged(AnalyticsListener _this, EventTime eventTime, int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        }

        public static void $default$onRenderedFirstFrame(AnalyticsListener _this, EventTime eventTime, Surface surface) {
        }

        public static void $default$onDrmSessionAcquired(AnalyticsListener _this, EventTime eventTime) {
        }

        public static void $default$onDrmKeysLoaded(AnalyticsListener _this, EventTime eventTime) {
        }

        public static void $default$onDrmSessionManagerError(AnalyticsListener _this, EventTime eventTime, Exception error) {
        }

        public static void $default$onDrmKeysRestored(AnalyticsListener _this, EventTime eventTime) {
        }

        public static void $default$onDrmKeysRemoved(AnalyticsListener _this, EventTime eventTime) {
        }

        public static void $default$onDrmSessionReleased(AnalyticsListener _this, EventTime eventTime) {
        }
    }
}
