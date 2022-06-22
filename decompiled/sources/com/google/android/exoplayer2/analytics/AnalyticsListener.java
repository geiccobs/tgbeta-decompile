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
/* loaded from: classes.dex */
public interface AnalyticsListener {

    /* renamed from: com.google.android.exoplayer2.analytics.AnalyticsListener$-CC */
    /* loaded from: classes.dex */
    public final /* synthetic */ class CC {
        public static void $default$onAudioAttributesChanged(AnalyticsListener analyticsListener, EventTime eventTime, AudioAttributes audioAttributes) {
        }

        public static void $default$onAudioSessionId(AnalyticsListener analyticsListener, EventTime eventTime, int i) {
        }

        public static void $default$onAudioUnderrun(AnalyticsListener analyticsListener, EventTime eventTime, int i, long j, long j2) {
        }

        public static void $default$onBandwidthEstimate(AnalyticsListener analyticsListener, EventTime eventTime, int i, long j, long j2) {
        }

        public static void $default$onDecoderDisabled(AnalyticsListener analyticsListener, EventTime eventTime, int i, DecoderCounters decoderCounters) {
        }

        public static void $default$onDecoderEnabled(AnalyticsListener analyticsListener, EventTime eventTime, int i, DecoderCounters decoderCounters) {
        }

        public static void $default$onDecoderInitialized(AnalyticsListener analyticsListener, EventTime eventTime, int i, String str, long j) {
        }

        public static void $default$onDecoderInputFormatChanged(AnalyticsListener analyticsListener, EventTime eventTime, int i, Format format) {
        }

        public static void $default$onDownstreamFormatChanged(AnalyticsListener analyticsListener, EventTime eventTime, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        }

        public static void $default$onDrmKeysLoaded(AnalyticsListener analyticsListener, EventTime eventTime) {
        }

        public static void $default$onDrmKeysRestored(AnalyticsListener analyticsListener, EventTime eventTime) {
        }

        public static void $default$onDrmSessionAcquired(AnalyticsListener analyticsListener, EventTime eventTime) {
        }

        public static void $default$onDrmSessionManagerError(AnalyticsListener analyticsListener, EventTime eventTime, Exception exc) {
        }

        public static void $default$onDrmSessionReleased(AnalyticsListener analyticsListener, EventTime eventTime) {
        }

        public static void $default$onDroppedVideoFrames(AnalyticsListener analyticsListener, EventTime eventTime, int i, long j) {
        }

        public static void $default$onIsPlayingChanged(AnalyticsListener analyticsListener, EventTime eventTime, boolean z) {
        }

        public static void $default$onLoadCanceled(AnalyticsListener analyticsListener, EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        }

        public static void $default$onLoadCompleted(AnalyticsListener analyticsListener, EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        }

        public static void $default$onLoadError(AnalyticsListener analyticsListener, EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData, IOException iOException, boolean z) {
        }

        public static void $default$onLoadStarted(AnalyticsListener analyticsListener, EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        }

        public static void $default$onLoadingChanged(AnalyticsListener analyticsListener, EventTime eventTime, boolean z) {
        }

        public static void $default$onMediaPeriodCreated(AnalyticsListener analyticsListener, EventTime eventTime) {
        }

        public static void $default$onMediaPeriodReleased(AnalyticsListener analyticsListener, EventTime eventTime) {
        }

        public static void $default$onMetadata(AnalyticsListener analyticsListener, EventTime eventTime, Metadata metadata) {
        }

        public static void $default$onPlaybackParametersChanged(AnalyticsListener analyticsListener, EventTime eventTime, PlaybackParameters playbackParameters) {
        }

        public static void $default$onPlaybackSuppressionReasonChanged(AnalyticsListener analyticsListener, EventTime eventTime, int i) {
        }

        public static void $default$onPlayerError(AnalyticsListener analyticsListener, EventTime eventTime, ExoPlaybackException exoPlaybackException) {
        }

        public static void $default$onPlayerStateChanged(AnalyticsListener analyticsListener, EventTime eventTime, boolean z, int i) {
        }

        public static void $default$onPositionDiscontinuity(AnalyticsListener analyticsListener, EventTime eventTime, int i) {
        }

        public static void $default$onReadingStarted(AnalyticsListener analyticsListener, EventTime eventTime) {
        }

        public static void $default$onRepeatModeChanged(AnalyticsListener analyticsListener, EventTime eventTime, int i) {
        }

        public static void $default$onSurfaceSizeChanged(AnalyticsListener analyticsListener, EventTime eventTime, int i, int i2) {
        }

        public static void $default$onTimelineChanged(AnalyticsListener analyticsListener, EventTime eventTime, int i) {
        }

        public static void $default$onTracksChanged(AnalyticsListener analyticsListener, EventTime eventTime, TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray) {
        }

        public static void $default$onUpstreamDiscarded(AnalyticsListener analyticsListener, EventTime eventTime, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        }

        public static void $default$onVideoSizeChanged(AnalyticsListener analyticsListener, EventTime eventTime, int i, int i2, int i3, float f) {
        }

        public static void $default$onVolumeChanged(AnalyticsListener analyticsListener, EventTime eventTime, float f) {
        }
    }

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

    void onSurfaceSizeChanged(EventTime eventTime, int i, int i2);

    void onTimelineChanged(EventTime eventTime, int i);

    void onTracksChanged(EventTime eventTime, TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray);

    void onUpstreamDiscarded(EventTime eventTime, MediaSourceEventListener.MediaLoadData mediaLoadData);

    void onVideoSizeChanged(EventTime eventTime, int i, int i2, int i3, float f);

    void onVolumeChanged(EventTime eventTime, float f);

    /* loaded from: classes.dex */
    public static final class EventTime {
        public final long eventPlaybackPositionMs;

        public EventTime(long j, Timeline timeline, int i, MediaSource.MediaPeriodId mediaPeriodId, long j2, long j3, long j4) {
            this.eventPlaybackPositionMs = j2;
        }
    }
}
