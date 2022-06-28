package com.google.android.exoplayer2.util;

import android.text.TextUtils;
import android.view.Surface;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.RendererCapabilities;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.firebase.remoteconfig.RemoteConfigConstants;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
/* loaded from: classes3.dex */
public class EventLogger implements AnalyticsListener {
    private static final String DEFAULT_TAG = "EventLogger";
    private static final int MAX_TIMELINE_ITEM_LINES = 3;
    private static final NumberFormat TIME_FORMAT;
    private final Timeline.Period period;
    private final long startTimeMs;
    private final String tag;
    private final MappingTrackSelector trackSelector;
    private final Timeline.Window window;

    static {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
        TIME_FORMAT = numberFormat;
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setGroupingUsed(false);
    }

    public EventLogger(MappingTrackSelector trackSelector) {
        this(trackSelector, DEFAULT_TAG);
    }

    public EventLogger(MappingTrackSelector trackSelector, String tag) {
        this.trackSelector = trackSelector;
        this.tag = tag;
        this.window = new Timeline.Window();
        this.period = new Timeline.Period();
        this.startTimeMs = android.os.SystemClock.elapsedRealtime();
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onLoadingChanged(AnalyticsListener.EventTime eventTime, boolean isLoading) {
        logd(eventTime, "loading", Boolean.toString(isLoading));
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onPlayerStateChanged(AnalyticsListener.EventTime eventTime, boolean playWhenReady, int state) {
        logd(eventTime, RemoteConfigConstants.ResponseFieldKey.STATE, playWhenReady + ", " + getStateString(state));
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onPlaybackSuppressionReasonChanged(AnalyticsListener.EventTime eventTime, int playbackSuppressionReason) {
        logd(eventTime, "playbackSuppressionReason", getPlaybackSuppressionReasonString(playbackSuppressionReason));
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onIsPlayingChanged(AnalyticsListener.EventTime eventTime, boolean isPlaying) {
        logd(eventTime, "isPlaying", Boolean.toString(isPlaying));
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onRepeatModeChanged(AnalyticsListener.EventTime eventTime, int repeatMode) {
        logd(eventTime, "repeatMode", getRepeatModeString(repeatMode));
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onShuffleModeChanged(AnalyticsListener.EventTime eventTime, boolean shuffleModeEnabled) {
        logd(eventTime, "shuffleModeEnabled", Boolean.toString(shuffleModeEnabled));
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onPositionDiscontinuity(AnalyticsListener.EventTime eventTime, int reason) {
        logd(eventTime, "positionDiscontinuity", getDiscontinuityReasonString(reason));
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onSeekStarted(AnalyticsListener.EventTime eventTime) {
        logd(eventTime, "seekStarted");
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onPlaybackParametersChanged(AnalyticsListener.EventTime eventTime, PlaybackParameters playbackParameters) {
        logd(eventTime, "playbackParameters", Util.formatInvariant("speed=%.2f, pitch=%.2f, skipSilence=%s", Float.valueOf(playbackParameters.speed), Float.valueOf(playbackParameters.pitch), Boolean.valueOf(playbackParameters.skipSilence)));
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onTimelineChanged(AnalyticsListener.EventTime eventTime, int reason) {
        int periodCount = eventTime.timeline.getPeriodCount();
        int windowCount = eventTime.timeline.getWindowCount();
        logd("timeline [" + getEventTimeString(eventTime) + ", periodCount=" + periodCount + ", windowCount=" + windowCount + ", reason=" + getTimelineChangeReasonString(reason));
        for (int i = 0; i < Math.min(periodCount, 3); i++) {
            eventTime.timeline.getPeriod(i, this.period);
            logd("  period [" + getTimeString(this.period.getDurationMs()) + "]");
        }
        if (periodCount > 3) {
            logd("  ...");
        }
        for (int i2 = 0; i2 < Math.min(windowCount, 3); i2++) {
            eventTime.timeline.getWindow(i2, this.window);
            logd("  window [" + getTimeString(this.window.getDurationMs()) + ", " + this.window.isSeekable + ", " + this.window.isDynamic + "]");
        }
        if (windowCount > 3) {
            logd("  ...");
        }
        logd("]");
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onPlayerError(AnalyticsListener.EventTime eventTime, ExoPlaybackException e) {
        loge(eventTime, "playerFailed", e);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onTracksChanged(AnalyticsListener.EventTime eventTime, TrackGroupArray ignored, TrackSelectionArray trackSelections) {
        String str;
        String str2;
        MappingTrackSelector mappingTrackSelector = this.trackSelector;
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = mappingTrackSelector != null ? mappingTrackSelector.getCurrentMappedTrackInfo() : null;
        if (mappedTrackInfo == null) {
            logd(eventTime, "tracks", "[]");
            return;
        }
        logd("tracks [" + getEventTimeString(eventTime));
        int rendererCount = mappedTrackInfo.getRendererCount();
        int rendererIndex = 0;
        while (true) {
            str = "  ]";
            str2 = " [";
            if (rendererIndex >= rendererCount) {
                break;
            }
            TrackGroupArray rendererTrackGroups = mappedTrackInfo.getTrackGroups(rendererIndex);
            TrackSelection trackSelection = trackSelections.get(rendererIndex);
            int rendererCount2 = rendererCount;
            int rendererCount3 = rendererTrackGroups.length;
            if (rendererCount3 > 0) {
                logd("  Renderer:" + rendererIndex + str2);
                int groupIndex = 0;
                while (groupIndex < rendererTrackGroups.length) {
                    TrackGroup trackGroup = rendererTrackGroups.get(groupIndex);
                    TrackGroupArray rendererTrackGroups2 = rendererTrackGroups;
                    String str3 = str;
                    String adaptiveSupport = getAdaptiveSupportString(trackGroup.length, mappedTrackInfo.getAdaptiveSupport(rendererIndex, groupIndex, false));
                    logd("    Group:" + groupIndex + ", adaptive_supported=" + adaptiveSupport + str2);
                    int trackIndex = 0;
                    while (trackIndex < trackGroup.length) {
                        String status = getTrackStatusString(trackSelection, trackGroup, trackIndex);
                        String adaptiveSupport2 = adaptiveSupport;
                        String formatSupport = RendererCapabilities.CC.getFormatSupportString(mappedTrackInfo.getTrackSupport(rendererIndex, groupIndex, trackIndex));
                        String str4 = str2;
                        logd("      " + status + " Track:" + trackIndex + ", " + Format.toLogString(trackGroup.getFormat(trackIndex)) + ", supported=" + formatSupport);
                        trackIndex++;
                        str2 = str4;
                        adaptiveSupport = adaptiveSupport2;
                        trackGroup = trackGroup;
                    }
                    logd("    ]");
                    groupIndex++;
                    rendererTrackGroups = rendererTrackGroups2;
                    str = str3;
                }
                String str5 = str;
                if (trackSelection != null) {
                    int selectionIndex = 0;
                    while (true) {
                        if (selectionIndex >= trackSelection.length()) {
                            break;
                        }
                        Metadata metadata = trackSelection.getFormat(selectionIndex).metadata;
                        if (metadata != null) {
                            logd("    Metadata [");
                            printMetadata(metadata, "      ");
                            logd("    ]");
                            break;
                        }
                        selectionIndex++;
                    }
                }
                logd(str5);
            }
            rendererIndex++;
            rendererCount = rendererCount2;
        }
        String str6 = str2;
        TrackGroupArray unassociatedTrackGroups = mappedTrackInfo.getUnmappedTrackGroups();
        if (unassociatedTrackGroups.length > 0) {
            logd("  Renderer:None [");
            int groupIndex2 = 0;
            while (groupIndex2 < unassociatedTrackGroups.length) {
                StringBuilder sb = new StringBuilder();
                sb.append("    Group:");
                sb.append(groupIndex2);
                String str7 = str6;
                sb.append(str7);
                logd(sb.toString());
                TrackGroup trackGroup2 = unassociatedTrackGroups.get(groupIndex2);
                int trackIndex2 = 0;
                while (trackIndex2 < trackGroup2.length) {
                    String status2 = getTrackStatusString(false);
                    MappingTrackSelector.MappedTrackInfo mappedTrackInfo2 = mappedTrackInfo;
                    String formatSupport2 = RendererCapabilities.CC.getFormatSupportString(0);
                    logd("      " + status2 + " Track:" + trackIndex2 + ", " + Format.toLogString(trackGroup2.getFormat(trackIndex2)) + ", supported=" + formatSupport2);
                    trackIndex2++;
                    mappedTrackInfo = mappedTrackInfo2;
                    unassociatedTrackGroups = unassociatedTrackGroups;
                }
                logd("    ]");
                groupIndex2++;
                str6 = str7;
            }
            logd(str);
        }
        logd("]");
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onSeekProcessed(AnalyticsListener.EventTime eventTime) {
        logd(eventTime, "seekProcessed");
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onMetadata(AnalyticsListener.EventTime eventTime, Metadata metadata) {
        logd("metadata [" + getEventTimeString(eventTime));
        printMetadata(metadata, "  ");
        logd("]");
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onDecoderEnabled(AnalyticsListener.EventTime eventTime, int trackType, DecoderCounters counters) {
        logd(eventTime, "decoderEnabled", Util.getTrackTypeString(trackType));
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onAudioSessionId(AnalyticsListener.EventTime eventTime, int audioSessionId) {
        logd(eventTime, "audioSessionId", Integer.toString(audioSessionId));
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onAudioAttributesChanged(AnalyticsListener.EventTime eventTime, AudioAttributes audioAttributes) {
        logd(eventTime, "audioAttributes", audioAttributes.contentType + "," + audioAttributes.flags + "," + audioAttributes.usage + "," + audioAttributes.allowedCapturePolicy);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onVolumeChanged(AnalyticsListener.EventTime eventTime, float volume) {
        logd(eventTime, "volume", Float.toString(volume));
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onDecoderInitialized(AnalyticsListener.EventTime eventTime, int trackType, String decoderName, long initializationDurationMs) {
        logd(eventTime, "decoderInitialized", Util.getTrackTypeString(trackType) + ", " + decoderName);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onDecoderInputFormatChanged(AnalyticsListener.EventTime eventTime, int trackType, Format format) {
        logd(eventTime, "decoderInputFormat", Util.getTrackTypeString(trackType) + ", " + Format.toLogString(format));
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onDecoderDisabled(AnalyticsListener.EventTime eventTime, int trackType, DecoderCounters counters) {
        logd(eventTime, "decoderDisabled", Util.getTrackTypeString(trackType));
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onAudioUnderrun(AnalyticsListener.EventTime eventTime, int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {
        loge(eventTime, "audioTrackUnderrun", bufferSize + ", " + bufferSizeMs + ", " + elapsedSinceLastFeedMs + "]", null);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onDroppedVideoFrames(AnalyticsListener.EventTime eventTime, int count, long elapsedMs) {
        logd(eventTime, "droppedFrames", Integer.toString(count));
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onVideoSizeChanged(AnalyticsListener.EventTime eventTime, int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        logd(eventTime, "videoSize", width + ", " + height);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime, Surface surface) {
        logd(eventTime, "renderedFirstFrame", String.valueOf(surface));
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onMediaPeriodCreated(AnalyticsListener.EventTime eventTime) {
        logd(eventTime, "mediaPeriodCreated");
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onMediaPeriodReleased(AnalyticsListener.EventTime eventTime) {
        logd(eventTime, "mediaPeriodReleased");
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onLoadStarted(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onLoadError(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {
        printInternalError(eventTime, "loadError", error);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onLoadCanceled(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onLoadCompleted(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onReadingStarted(AnalyticsListener.EventTime eventTime) {
        logd(eventTime, "mediaPeriodReadingStarted");
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onBandwidthEstimate(AnalyticsListener.EventTime eventTime, int totalLoadTimeMs, long totalBytesLoaded, long bitrateEstimate) {
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onSurfaceSizeChanged(AnalyticsListener.EventTime eventTime, int width, int height) {
        logd(eventTime, "surfaceSize", width + ", " + height);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onUpstreamDiscarded(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        logd(eventTime, "upstreamDiscarded", Format.toLogString(mediaLoadData.trackFormat));
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onDownstreamFormatChanged(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        logd(eventTime, "downstreamFormat", Format.toLogString(mediaLoadData.trackFormat));
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onDrmSessionAcquired(AnalyticsListener.EventTime eventTime) {
        logd(eventTime, "drmSessionAcquired");
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onDrmSessionManagerError(AnalyticsListener.EventTime eventTime, Exception e) {
        printInternalError(eventTime, "drmSessionManagerError", e);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onDrmKeysRestored(AnalyticsListener.EventTime eventTime) {
        logd(eventTime, "drmKeysRestored");
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onDrmKeysRemoved(AnalyticsListener.EventTime eventTime) {
        logd(eventTime, "drmKeysRemoved");
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onDrmKeysLoaded(AnalyticsListener.EventTime eventTime) {
        logd(eventTime, "drmKeysLoaded");
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onDrmSessionReleased(AnalyticsListener.EventTime eventTime) {
        logd(eventTime, "drmSessionReleased");
    }

    protected void logd(String msg) {
        Log.d(this.tag, msg);
    }

    protected void loge(String msg) {
        Log.e(this.tag, msg);
    }

    private void logd(AnalyticsListener.EventTime eventTime, String eventName) {
        logd(getEventString(eventTime, eventName, null, null));
    }

    private void logd(AnalyticsListener.EventTime eventTime, String eventName, String eventDescription) {
        logd(getEventString(eventTime, eventName, eventDescription, null));
    }

    private void loge(AnalyticsListener.EventTime eventTime, String eventName, Throwable throwable) {
        loge(getEventString(eventTime, eventName, null, throwable));
    }

    private void loge(AnalyticsListener.EventTime eventTime, String eventName, String eventDescription, Throwable throwable) {
        loge(getEventString(eventTime, eventName, eventDescription, throwable));
    }

    private void printInternalError(AnalyticsListener.EventTime eventTime, String type, Exception e) {
        loge(eventTime, "internalError", type, e);
    }

    private void printMetadata(Metadata metadata, String prefix) {
        for (int i = 0; i < metadata.length(); i++) {
            logd(prefix + metadata.get(i));
        }
    }

    private String getEventString(AnalyticsListener.EventTime eventTime, String eventName, String eventDescription, Throwable throwable) {
        String eventString = eventName + " [" + getEventTimeString(eventTime);
        if (eventDescription != null) {
            eventString = eventString + ", " + eventDescription;
        }
        String throwableString = Log.getThrowableString(throwable);
        if (!TextUtils.isEmpty(throwableString)) {
            eventString = eventString + "\n  " + throwableString.replace("\n", "\n  ") + '\n';
        }
        return eventString + "]";
    }

    private String getEventTimeString(AnalyticsListener.EventTime eventTime) {
        String windowPeriodString = "window=" + eventTime.windowIndex;
        if (eventTime.mediaPeriodId != null) {
            windowPeriodString = windowPeriodString + ", period=" + eventTime.timeline.getIndexOfPeriod(eventTime.mediaPeriodId.periodUid);
            if (eventTime.mediaPeriodId.isAd()) {
                windowPeriodString = (windowPeriodString + ", adGroup=" + eventTime.mediaPeriodId.adGroupIndex) + ", ad=" + eventTime.mediaPeriodId.adIndexInAdGroup;
            }
        }
        return "eventTime=" + getTimeString(eventTime.realtimeMs - this.startTimeMs) + ", mediaPos=" + getTimeString(eventTime.currentPlaybackPositionMs) + ", " + windowPeriodString;
    }

    private static String getTimeString(long timeMs) {
        return timeMs == C.TIME_UNSET ? "?" : TIME_FORMAT.format(((float) timeMs) / 1000.0f);
    }

    private static String getStateString(int state) {
        switch (state) {
            case 1:
                return "IDLE";
            case 2:
                return "BUFFERING";
            case 3:
                return "READY";
            case 4:
                return "ENDED";
            default:
                return "?";
        }
    }

    private static String getAdaptiveSupportString(int trackCount, int adaptiveSupport) {
        if (trackCount < 2) {
            return "N/A";
        }
        switch (adaptiveSupport) {
            case 0:
                return "NO";
            case 8:
                return "YES_NOT_SEAMLESS";
            case 16:
                return "YES";
            default:
                throw new IllegalStateException();
        }
    }

    private static String getTrackStatusString(TrackSelection selection, TrackGroup group, int trackIndex) {
        return getTrackStatusString((selection == null || selection.getTrackGroup() != group || selection.indexOf(trackIndex) == -1) ? false : true);
    }

    private static String getTrackStatusString(boolean enabled) {
        return enabled ? "[X]" : "[ ]";
    }

    private static String getRepeatModeString(int repeatMode) {
        switch (repeatMode) {
            case 0:
                return "OFF";
            case 1:
                return "ONE";
            case 2:
                return "ALL";
            default:
                return "?";
        }
    }

    private static String getDiscontinuityReasonString(int reason) {
        switch (reason) {
            case 0:
                return "PERIOD_TRANSITION";
            case 1:
                return "SEEK";
            case 2:
                return "SEEK_ADJUSTMENT";
            case 3:
                return "AD_INSERTION";
            case 4:
                return "INTERNAL";
            default:
                return "?";
        }
    }

    private static String getTimelineChangeReasonString(int reason) {
        switch (reason) {
            case 0:
                return "PREPARED";
            case 1:
                return "RESET";
            case 2:
                return "DYNAMIC";
            default:
                return "?";
        }
    }

    private static String getPlaybackSuppressionReasonString(int playbackSuppressionReason) {
        switch (playbackSuppressionReason) {
            case 0:
                return "NONE";
            case 1:
                return "TRANSIENT_AUDIO_FOCUS_LOSS";
            default:
                return "?";
        }
    }
}
