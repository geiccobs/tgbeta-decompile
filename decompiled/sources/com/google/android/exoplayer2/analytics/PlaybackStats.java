package com.google.android.exoplayer2.analytics;

import android.util.Pair;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.List;
/* loaded from: classes3.dex */
public final class PlaybackStats {
    public static final PlaybackStats EMPTY = merge(new PlaybackStats[0]);
    public static final int PLAYBACK_STATE_ABANDONED = 15;
    public static final int PLAYBACK_STATE_BUFFERING = 6;
    static final int PLAYBACK_STATE_COUNT = 16;
    public static final int PLAYBACK_STATE_ENDED = 11;
    public static final int PLAYBACK_STATE_FAILED = 13;
    public static final int PLAYBACK_STATE_INTERRUPTED_BY_AD = 14;
    public static final int PLAYBACK_STATE_JOINING_BACKGROUND = 1;
    public static final int PLAYBACK_STATE_JOINING_FOREGROUND = 2;
    public static final int PLAYBACK_STATE_NOT_STARTED = 0;
    public static final int PLAYBACK_STATE_PAUSED = 4;
    public static final int PLAYBACK_STATE_PAUSED_BUFFERING = 7;
    public static final int PLAYBACK_STATE_PLAYING = 3;
    public static final int PLAYBACK_STATE_SEEKING = 5;
    public static final int PLAYBACK_STATE_SEEK_BUFFERING = 8;
    public static final int PLAYBACK_STATE_STOPPED = 12;
    public static final int PLAYBACK_STATE_SUPPRESSED = 9;
    public static final int PLAYBACK_STATE_SUPPRESSED_BUFFERING = 10;
    public final int abandonedBeforeReadyCount;
    public final int adPlaybackCount;
    public final List<Pair<AnalyticsListener.EventTime, Format>> audioFormatHistory;
    public final int backgroundJoiningCount;
    public final int endedCount;
    public final int fatalErrorCount;
    public final List<Pair<AnalyticsListener.EventTime, Exception>> fatalErrorHistory;
    public final int fatalErrorPlaybackCount;
    public final long firstReportedTimeMs;
    public final int foregroundPlaybackCount;
    public final int initialAudioFormatBitrateCount;
    public final int initialVideoFormatBitrateCount;
    public final int initialVideoFormatHeightCount;
    public final long maxRebufferTimeMs;
    public final List<long[]> mediaTimeHistory;
    public final int nonFatalErrorCount;
    public final List<Pair<AnalyticsListener.EventTime, Exception>> nonFatalErrorHistory;
    public final int playbackCount;
    private final long[] playbackStateDurationsMs;
    public final List<Pair<AnalyticsListener.EventTime, Integer>> playbackStateHistory;
    public final long totalAudioFormatBitrateTimeProduct;
    public final long totalAudioFormatTimeMs;
    public final long totalAudioUnderruns;
    public final long totalBandwidthBytes;
    public final long totalBandwidthTimeMs;
    public final long totalDroppedFrames;
    public final long totalInitialAudioFormatBitrate;
    public final long totalInitialVideoFormatBitrate;
    public final int totalInitialVideoFormatHeight;
    public final int totalPauseBufferCount;
    public final int totalPauseCount;
    public final int totalRebufferCount;
    public final int totalSeekCount;
    public final long totalValidJoinTimeMs;
    public final long totalVideoFormatBitrateTimeMs;
    public final long totalVideoFormatBitrateTimeProduct;
    public final long totalVideoFormatHeightTimeMs;
    public final long totalVideoFormatHeightTimeProduct;
    public final int validJoinTimeCount;
    public final List<Pair<AnalyticsListener.EventTime, Format>> videoFormatHistory;

    @Target({ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    @interface PlaybackState {
    }

    public static PlaybackStats merge(PlaybackStats... playbackStats) {
        int playbackCount;
        PlaybackStats[] playbackStatsArr = playbackStats;
        int playbackCount2 = 0;
        long[] playbackStateDurationsMs = new long[16];
        int length = playbackStatsArr.length;
        int foregroundPlaybackCount = 0;
        int abandonedBeforeReadyCount = 0;
        int endedCount = 0;
        int backgroundJoiningCount = 0;
        long totalValidJoinTimeMs = -9223372036854775807L;
        int validJoinTimeCount = 0;
        int totalPauseCount = 0;
        int totalPauseBufferCount = 0;
        int totalSeekCount = 0;
        int totalRebufferCount = 0;
        long maxRebufferTimeMs = -9223372036854775807L;
        int adPlaybackCount = 0;
        long totalVideoFormatHeightTimeMs = 0;
        long totalVideoFormatHeightTimeProduct = 0;
        long totalVideoFormatBitrateTimeMs = 0;
        long totalVideoFormatBitrateTimeProduct = 0;
        long totalAudioFormatTimeMs = 0;
        long totalAudioFormatBitrateTimeProduct = 0;
        int initialVideoFormatHeightCount = 0;
        int initialVideoFormatBitrateCount = 0;
        int totalInitialVideoFormatHeight = -1;
        long totalInitialVideoFormatBitrate = -1;
        int initialAudioFormatBitrateCount = 0;
        long totalInitialAudioFormatBitrate = -1;
        long totalBandwidthTimeMs = 0;
        long totalBandwidthBytes = 0;
        long totalDroppedFrames = 0;
        long totalAudioUnderruns = 0;
        int fatalErrorPlaybackCount = 0;
        int fatalErrorCount = 0;
        int nonFatalErrorCount = 0;
        long firstReportedTimeMs = -9223372036854775807L;
        int i = 0;
        while (i < length) {
            PlaybackStats stats = playbackStatsArr[i];
            int playbackCount3 = playbackCount2 + stats.playbackCount;
            for (int i2 = 0; i2 < 16; i2++) {
                playbackStateDurationsMs[i2] = playbackStateDurationsMs[i2] + stats.playbackStateDurationsMs[i2];
            }
            if (firstReportedTimeMs == C.TIME_UNSET) {
                firstReportedTimeMs = stats.firstReportedTimeMs;
                playbackCount = playbackCount3;
            } else {
                playbackCount = playbackCount3;
                long j = stats.firstReportedTimeMs;
                if (j != C.TIME_UNSET) {
                    firstReportedTimeMs = Math.min(firstReportedTimeMs, j);
                }
            }
            foregroundPlaybackCount += stats.foregroundPlaybackCount;
            abandonedBeforeReadyCount += stats.abandonedBeforeReadyCount;
            endedCount += stats.endedCount;
            backgroundJoiningCount += stats.backgroundJoiningCount;
            if (totalValidJoinTimeMs == C.TIME_UNSET) {
                totalValidJoinTimeMs = stats.totalValidJoinTimeMs;
            } else {
                long totalValidJoinTimeMs2 = stats.totalValidJoinTimeMs;
                if (totalValidJoinTimeMs2 != C.TIME_UNSET) {
                    totalValidJoinTimeMs += totalValidJoinTimeMs2;
                }
            }
            validJoinTimeCount += stats.validJoinTimeCount;
            totalPauseCount += stats.totalPauseCount;
            totalPauseBufferCount += stats.totalPauseBufferCount;
            totalSeekCount += stats.totalSeekCount;
            totalRebufferCount += stats.totalRebufferCount;
            if (maxRebufferTimeMs == C.TIME_UNSET) {
                maxRebufferTimeMs = stats.maxRebufferTimeMs;
            } else {
                long maxRebufferTimeMs2 = stats.maxRebufferTimeMs;
                if (maxRebufferTimeMs2 != C.TIME_UNSET) {
                    maxRebufferTimeMs = Math.max(maxRebufferTimeMs, maxRebufferTimeMs2);
                }
            }
            adPlaybackCount += stats.adPlaybackCount;
            totalVideoFormatHeightTimeMs += stats.totalVideoFormatHeightTimeMs;
            totalVideoFormatHeightTimeProduct += stats.totalVideoFormatHeightTimeProduct;
            totalVideoFormatBitrateTimeMs += stats.totalVideoFormatBitrateTimeMs;
            totalVideoFormatBitrateTimeProduct += stats.totalVideoFormatBitrateTimeProduct;
            totalAudioFormatTimeMs += stats.totalAudioFormatTimeMs;
            totalAudioFormatBitrateTimeProduct += stats.totalAudioFormatBitrateTimeProduct;
            initialVideoFormatHeightCount += stats.initialVideoFormatHeightCount;
            initialVideoFormatBitrateCount += stats.initialVideoFormatBitrateCount;
            if (totalInitialVideoFormatHeight == -1) {
                totalInitialVideoFormatHeight = stats.totalInitialVideoFormatHeight;
            } else {
                int i3 = stats.totalInitialVideoFormatHeight;
                if (i3 != -1) {
                    totalInitialVideoFormatHeight += i3;
                }
            }
            if (totalInitialVideoFormatBitrate == -1) {
                totalInitialVideoFormatBitrate = stats.totalInitialVideoFormatBitrate;
            } else {
                long totalInitialVideoFormatBitrate2 = stats.totalInitialVideoFormatBitrate;
                if (totalInitialVideoFormatBitrate2 != -1) {
                    totalInitialVideoFormatBitrate += totalInitialVideoFormatBitrate2;
                }
            }
            initialAudioFormatBitrateCount += stats.initialAudioFormatBitrateCount;
            if (totalInitialAudioFormatBitrate == -1) {
                totalInitialAudioFormatBitrate = stats.totalInitialAudioFormatBitrate;
            } else {
                long j2 = stats.totalInitialAudioFormatBitrate;
                if (j2 != -1) {
                    totalInitialAudioFormatBitrate += j2;
                }
            }
            totalBandwidthTimeMs += stats.totalBandwidthTimeMs;
            totalBandwidthBytes += stats.totalBandwidthBytes;
            totalDroppedFrames += stats.totalDroppedFrames;
            totalAudioUnderruns += stats.totalAudioUnderruns;
            fatalErrorPlaybackCount += stats.fatalErrorPlaybackCount;
            fatalErrorCount += stats.fatalErrorCount;
            nonFatalErrorCount += stats.nonFatalErrorCount;
            i++;
            playbackStatsArr = playbackStats;
            playbackCount2 = playbackCount;
        }
        return new PlaybackStats(playbackCount2, playbackStateDurationsMs, Collections.emptyList(), Collections.emptyList(), firstReportedTimeMs, foregroundPlaybackCount, abandonedBeforeReadyCount, endedCount, backgroundJoiningCount, totalValidJoinTimeMs, validJoinTimeCount, totalPauseCount, totalPauseBufferCount, totalSeekCount, totalRebufferCount, maxRebufferTimeMs, adPlaybackCount, Collections.emptyList(), Collections.emptyList(), totalVideoFormatHeightTimeMs, totalVideoFormatHeightTimeProduct, totalVideoFormatBitrateTimeMs, totalVideoFormatBitrateTimeProduct, totalAudioFormatTimeMs, totalAudioFormatBitrateTimeProduct, initialVideoFormatHeightCount, initialVideoFormatBitrateCount, totalInitialVideoFormatHeight, totalInitialVideoFormatBitrate, initialAudioFormatBitrateCount, totalInitialAudioFormatBitrate, totalBandwidthTimeMs, totalBandwidthBytes, totalDroppedFrames, totalAudioUnderruns, fatalErrorPlaybackCount, fatalErrorCount, nonFatalErrorCount, Collections.emptyList(), Collections.emptyList());
    }

    public PlaybackStats(int playbackCount, long[] playbackStateDurationsMs, List<Pair<AnalyticsListener.EventTime, Integer>> playbackStateHistory, List<long[]> mediaTimeHistory, long firstReportedTimeMs, int foregroundPlaybackCount, int abandonedBeforeReadyCount, int endedCount, int backgroundJoiningCount, long totalValidJoinTimeMs, int validJoinTimeCount, int totalPauseCount, int totalPauseBufferCount, int totalSeekCount, int totalRebufferCount, long maxRebufferTimeMs, int adPlaybackCount, List<Pair<AnalyticsListener.EventTime, Format>> videoFormatHistory, List<Pair<AnalyticsListener.EventTime, Format>> audioFormatHistory, long totalVideoFormatHeightTimeMs, long totalVideoFormatHeightTimeProduct, long totalVideoFormatBitrateTimeMs, long totalVideoFormatBitrateTimeProduct, long totalAudioFormatTimeMs, long totalAudioFormatBitrateTimeProduct, int initialVideoFormatHeightCount, int initialVideoFormatBitrateCount, int totalInitialVideoFormatHeight, long totalInitialVideoFormatBitrate, int initialAudioFormatBitrateCount, long totalInitialAudioFormatBitrate, long totalBandwidthTimeMs, long totalBandwidthBytes, long totalDroppedFrames, long totalAudioUnderruns, int fatalErrorPlaybackCount, int fatalErrorCount, int nonFatalErrorCount, List<Pair<AnalyticsListener.EventTime, Exception>> fatalErrorHistory, List<Pair<AnalyticsListener.EventTime, Exception>> nonFatalErrorHistory) {
        this.playbackCount = playbackCount;
        this.playbackStateDurationsMs = playbackStateDurationsMs;
        this.playbackStateHistory = Collections.unmodifiableList(playbackStateHistory);
        this.mediaTimeHistory = Collections.unmodifiableList(mediaTimeHistory);
        this.firstReportedTimeMs = firstReportedTimeMs;
        this.foregroundPlaybackCount = foregroundPlaybackCount;
        this.abandonedBeforeReadyCount = abandonedBeforeReadyCount;
        this.endedCount = endedCount;
        this.backgroundJoiningCount = backgroundJoiningCount;
        this.totalValidJoinTimeMs = totalValidJoinTimeMs;
        this.validJoinTimeCount = validJoinTimeCount;
        this.totalPauseCount = totalPauseCount;
        this.totalPauseBufferCount = totalPauseBufferCount;
        this.totalSeekCount = totalSeekCount;
        this.totalRebufferCount = totalRebufferCount;
        this.maxRebufferTimeMs = maxRebufferTimeMs;
        this.adPlaybackCount = adPlaybackCount;
        this.videoFormatHistory = Collections.unmodifiableList(videoFormatHistory);
        this.audioFormatHistory = Collections.unmodifiableList(audioFormatHistory);
        this.totalVideoFormatHeightTimeMs = totalVideoFormatHeightTimeMs;
        this.totalVideoFormatHeightTimeProduct = totalVideoFormatHeightTimeProduct;
        this.totalVideoFormatBitrateTimeMs = totalVideoFormatBitrateTimeMs;
        this.totalVideoFormatBitrateTimeProduct = totalVideoFormatBitrateTimeProduct;
        this.totalAudioFormatTimeMs = totalAudioFormatTimeMs;
        this.totalAudioFormatBitrateTimeProduct = totalAudioFormatBitrateTimeProduct;
        this.initialVideoFormatHeightCount = initialVideoFormatHeightCount;
        this.initialVideoFormatBitrateCount = initialVideoFormatBitrateCount;
        this.totalInitialVideoFormatHeight = totalInitialVideoFormatHeight;
        this.totalInitialVideoFormatBitrate = totalInitialVideoFormatBitrate;
        this.initialAudioFormatBitrateCount = initialAudioFormatBitrateCount;
        this.totalInitialAudioFormatBitrate = totalInitialAudioFormatBitrate;
        this.totalBandwidthTimeMs = totalBandwidthTimeMs;
        this.totalBandwidthBytes = totalBandwidthBytes;
        this.totalDroppedFrames = totalDroppedFrames;
        this.totalAudioUnderruns = totalAudioUnderruns;
        this.fatalErrorPlaybackCount = fatalErrorPlaybackCount;
        this.fatalErrorCount = fatalErrorCount;
        this.nonFatalErrorCount = nonFatalErrorCount;
        this.fatalErrorHistory = Collections.unmodifiableList(fatalErrorHistory);
        this.nonFatalErrorHistory = Collections.unmodifiableList(nonFatalErrorHistory);
    }

    public long getPlaybackStateDurationMs(int playbackState) {
        return this.playbackStateDurationsMs[playbackState];
    }

    public int getPlaybackStateAtTime(long realtimeMs) {
        int state = 0;
        for (Pair<AnalyticsListener.EventTime, Integer> timeAndState : this.playbackStateHistory) {
            if (((AnalyticsListener.EventTime) timeAndState.first).realtimeMs > realtimeMs) {
                break;
            }
            state = ((Integer) timeAndState.second).intValue();
        }
        return state;
    }

    public long getMediaTimeMsAtRealtimeMs(long realtimeMs) {
        if (this.mediaTimeHistory.isEmpty()) {
            return C.TIME_UNSET;
        }
        int nextIndex = 0;
        while (nextIndex < this.mediaTimeHistory.size() && this.mediaTimeHistory.get(nextIndex)[0] <= realtimeMs) {
            nextIndex++;
        }
        if (nextIndex == 0) {
            return this.mediaTimeHistory.get(0)[1];
        }
        if (nextIndex == this.mediaTimeHistory.size()) {
            List<long[]> list = this.mediaTimeHistory;
            return list.get(list.size() - 1)[1];
        }
        long prevRealtimeMs = this.mediaTimeHistory.get(nextIndex - 1)[0];
        long prevMediaTimeMs = this.mediaTimeHistory.get(nextIndex - 1)[1];
        long nextRealtimeMs = this.mediaTimeHistory.get(nextIndex)[0];
        long nextMediaTimeMs = this.mediaTimeHistory.get(nextIndex)[1];
        long realtimeDurationMs = nextRealtimeMs - prevRealtimeMs;
        if (realtimeDurationMs == 0) {
            return prevMediaTimeMs;
        }
        float fraction = ((float) (realtimeMs - prevRealtimeMs)) / ((float) realtimeDurationMs);
        return (((float) (nextMediaTimeMs - prevMediaTimeMs)) * fraction) + prevMediaTimeMs;
    }

    public long getMeanJoinTimeMs() {
        int i = this.validJoinTimeCount;
        return i == 0 ? C.TIME_UNSET : this.totalValidJoinTimeMs / i;
    }

    public long getTotalJoinTimeMs() {
        return getPlaybackStateDurationMs(2);
    }

    public long getTotalPlayTimeMs() {
        return getPlaybackStateDurationMs(3);
    }

    public long getMeanPlayTimeMs() {
        if (this.foregroundPlaybackCount == 0) {
            return C.TIME_UNSET;
        }
        return getTotalPlayTimeMs() / this.foregroundPlaybackCount;
    }

    public long getTotalPausedTimeMs() {
        return getPlaybackStateDurationMs(4) + getPlaybackStateDurationMs(7);
    }

    public long getMeanPausedTimeMs() {
        if (this.foregroundPlaybackCount == 0) {
            return C.TIME_UNSET;
        }
        return getTotalPausedTimeMs() / this.foregroundPlaybackCount;
    }

    public long getTotalRebufferTimeMs() {
        return getPlaybackStateDurationMs(6);
    }

    public long getMeanRebufferTimeMs() {
        if (this.foregroundPlaybackCount == 0) {
            return C.TIME_UNSET;
        }
        return getTotalRebufferTimeMs() / this.foregroundPlaybackCount;
    }

    public long getMeanSingleRebufferTimeMs() {
        if (this.totalRebufferCount == 0) {
            return C.TIME_UNSET;
        }
        return (getPlaybackStateDurationMs(6) + getPlaybackStateDurationMs(7)) / this.totalRebufferCount;
    }

    public long getTotalSeekTimeMs() {
        return getPlaybackStateDurationMs(5) + getPlaybackStateDurationMs(8);
    }

    public long getMeanSeekTimeMs() {
        if (this.foregroundPlaybackCount == 0) {
            return C.TIME_UNSET;
        }
        return getTotalSeekTimeMs() / this.foregroundPlaybackCount;
    }

    public long getMeanSingleSeekTimeMs() {
        return this.totalSeekCount == 0 ? C.TIME_UNSET : getTotalSeekTimeMs() / this.totalSeekCount;
    }

    public long getTotalWaitTimeMs() {
        return getPlaybackStateDurationMs(2) + getPlaybackStateDurationMs(6) + getPlaybackStateDurationMs(5) + getPlaybackStateDurationMs(8);
    }

    public long getMeanWaitTimeMs() {
        if (this.foregroundPlaybackCount == 0) {
            return C.TIME_UNSET;
        }
        return getTotalWaitTimeMs() / this.foregroundPlaybackCount;
    }

    public long getTotalPlayAndWaitTimeMs() {
        return getTotalPlayTimeMs() + getTotalWaitTimeMs();
    }

    public long getMeanPlayAndWaitTimeMs() {
        if (this.foregroundPlaybackCount == 0) {
            return C.TIME_UNSET;
        }
        return getTotalPlayAndWaitTimeMs() / this.foregroundPlaybackCount;
    }

    public long getTotalElapsedTimeMs() {
        long totalTimeMs = 0;
        for (int i = 0; i < 16; i++) {
            totalTimeMs += this.playbackStateDurationsMs[i];
        }
        return totalTimeMs;
    }

    public long getMeanElapsedTimeMs() {
        return this.playbackCount == 0 ? C.TIME_UNSET : getTotalElapsedTimeMs() / this.playbackCount;
    }

    public float getAbandonedBeforeReadyRatio() {
        int i = this.abandonedBeforeReadyCount;
        int i2 = this.playbackCount;
        int i3 = this.foregroundPlaybackCount;
        int foregroundAbandonedBeforeReady = i - (i2 - i3);
        if (i3 == 0) {
            return 0.0f;
        }
        return foregroundAbandonedBeforeReady / i3;
    }

    public float getEndedRatio() {
        int i = this.foregroundPlaybackCount;
        if (i == 0) {
            return 0.0f;
        }
        return this.endedCount / i;
    }

    public float getMeanPauseCount() {
        int i = this.foregroundPlaybackCount;
        if (i == 0) {
            return 0.0f;
        }
        return this.totalPauseCount / i;
    }

    public float getMeanPauseBufferCount() {
        int i = this.foregroundPlaybackCount;
        if (i == 0) {
            return 0.0f;
        }
        return this.totalPauseBufferCount / i;
    }

    public float getMeanSeekCount() {
        int i = this.foregroundPlaybackCount;
        if (i == 0) {
            return 0.0f;
        }
        return this.totalSeekCount / i;
    }

    public float getMeanRebufferCount() {
        int i = this.foregroundPlaybackCount;
        if (i == 0) {
            return 0.0f;
        }
        return this.totalRebufferCount / i;
    }

    public float getWaitTimeRatio() {
        long playAndWaitTimeMs = getTotalPlayAndWaitTimeMs();
        if (playAndWaitTimeMs == 0) {
            return 0.0f;
        }
        return ((float) getTotalWaitTimeMs()) / ((float) playAndWaitTimeMs);
    }

    public float getJoinTimeRatio() {
        long playAndWaitTimeMs = getTotalPlayAndWaitTimeMs();
        if (playAndWaitTimeMs == 0) {
            return 0.0f;
        }
        return ((float) getTotalJoinTimeMs()) / ((float) playAndWaitTimeMs);
    }

    public float getRebufferTimeRatio() {
        long playAndWaitTimeMs = getTotalPlayAndWaitTimeMs();
        if (playAndWaitTimeMs == 0) {
            return 0.0f;
        }
        return ((float) getTotalRebufferTimeMs()) / ((float) playAndWaitTimeMs);
    }

    public float getSeekTimeRatio() {
        long playAndWaitTimeMs = getTotalPlayAndWaitTimeMs();
        if (playAndWaitTimeMs == 0) {
            return 0.0f;
        }
        return ((float) getTotalSeekTimeMs()) / ((float) playAndWaitTimeMs);
    }

    public float getRebufferRate() {
        long playTimeMs = getTotalPlayTimeMs();
        if (playTimeMs == 0) {
            return 0.0f;
        }
        return (this.totalRebufferCount * 1000.0f) / ((float) playTimeMs);
    }

    public float getMeanTimeBetweenRebuffers() {
        return 1.0f / getRebufferRate();
    }

    public int getMeanInitialVideoFormatHeight() {
        int i = this.initialVideoFormatHeightCount;
        if (i == 0) {
            return -1;
        }
        return this.totalInitialVideoFormatHeight / i;
    }

    public int getMeanInitialVideoFormatBitrate() {
        int i = this.initialVideoFormatBitrateCount;
        if (i == 0) {
            return -1;
        }
        return (int) (this.totalInitialVideoFormatBitrate / i);
    }

    public int getMeanInitialAudioFormatBitrate() {
        int i = this.initialAudioFormatBitrateCount;
        if (i == 0) {
            return -1;
        }
        return (int) (this.totalInitialAudioFormatBitrate / i);
    }

    public int getMeanVideoFormatHeight() {
        long j = this.totalVideoFormatHeightTimeMs;
        if (j == 0) {
            return -1;
        }
        return (int) (this.totalVideoFormatHeightTimeProduct / j);
    }

    public int getMeanVideoFormatBitrate() {
        long j = this.totalVideoFormatBitrateTimeMs;
        if (j == 0) {
            return -1;
        }
        return (int) (this.totalVideoFormatBitrateTimeProduct / j);
    }

    public int getMeanAudioFormatBitrate() {
        long j = this.totalAudioFormatTimeMs;
        if (j == 0) {
            return -1;
        }
        return (int) (this.totalAudioFormatBitrateTimeProduct / j);
    }

    public int getMeanBandwidth() {
        long j = this.totalBandwidthTimeMs;
        if (j == 0) {
            return -1;
        }
        return (int) ((this.totalBandwidthBytes * 8000) / j);
    }

    public float getDroppedFramesRate() {
        long playTimeMs = getTotalPlayTimeMs();
        if (playTimeMs == 0) {
            return 0.0f;
        }
        return (((float) this.totalDroppedFrames) * 1000.0f) / ((float) playTimeMs);
    }

    public float getAudioUnderrunRate() {
        long playTimeMs = getTotalPlayTimeMs();
        if (playTimeMs == 0) {
            return 0.0f;
        }
        return (((float) this.totalAudioUnderruns) * 1000.0f) / ((float) playTimeMs);
    }

    public float getFatalErrorRatio() {
        int i = this.foregroundPlaybackCount;
        if (i == 0) {
            return 0.0f;
        }
        return this.fatalErrorPlaybackCount / i;
    }

    public float getFatalErrorRate() {
        long playTimeMs = getTotalPlayTimeMs();
        if (playTimeMs == 0) {
            return 0.0f;
        }
        return (this.fatalErrorCount * 1000.0f) / ((float) playTimeMs);
    }

    public float getMeanTimeBetweenFatalErrors() {
        return 1.0f / getFatalErrorRate();
    }

    public float getMeanNonFatalErrorCount() {
        int i = this.foregroundPlaybackCount;
        if (i == 0) {
            return 0.0f;
        }
        return this.nonFatalErrorCount / i;
    }

    public float getNonFatalErrorRate() {
        long playTimeMs = getTotalPlayTimeMs();
        if (playTimeMs == 0) {
            return 0.0f;
        }
        return (this.nonFatalErrorCount * 1000.0f) / ((float) playTimeMs);
    }

    public float getMeanTimeBetweenNonFatalErrors() {
        return 1.0f / getNonFatalErrorRate();
    }
}
