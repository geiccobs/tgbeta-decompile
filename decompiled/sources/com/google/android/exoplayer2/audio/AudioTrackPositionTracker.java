package com.google.android.exoplayer2.audio;

import android.media.AudioTrack;
import android.os.SystemClock;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
/* loaded from: classes3.dex */
final class AudioTrackPositionTracker {
    private static final long FORCE_RESET_WORKAROUND_TIMEOUT_MS = 200;
    private static final long MAX_AUDIO_TIMESTAMP_OFFSET_US = 5000000;
    private static final long MAX_LATENCY_US = 5000000;
    private static final int MAX_PLAYHEAD_OFFSET_COUNT = 10;
    private static final int MIN_LATENCY_SAMPLE_INTERVAL_US = 500000;
    private static final int MIN_PLAYHEAD_OFFSET_SAMPLE_INTERVAL_US = 30000;
    private static final long MODE_SWITCH_SMOOTHING_DURATION_US = 1000000;
    private static final int PLAYSTATE_PAUSED = 2;
    private static final int PLAYSTATE_PLAYING = 3;
    private static final int PLAYSTATE_STOPPED = 1;
    private AudioTimestampPoller audioTimestampPoller;
    private AudioTrack audioTrack;
    private int bufferSize;
    private long bufferSizeUs;
    private long endPlaybackHeadPosition;
    private long forceResetWorkaroundTimeMs;
    private Method getLatencyMethod;
    private boolean hasData;
    private boolean isOutputPcm;
    private long lastLatencySampleTimeUs;
    private long lastPlayheadSampleTimeUs;
    private long lastPositionUs;
    private long lastRawPlaybackHeadPosition;
    private boolean lastSampleUsedGetTimestampMode;
    private long lastSystemTimeUs;
    private long latencyUs;
    private final Listener listener;
    private boolean needsPassthroughWorkarounds;
    private int nextPlayheadOffsetIndex;
    private int outputPcmFrameSize;
    private int outputSampleRate;
    private long passthroughWorkaroundPauseOffset;
    private int playheadOffsetCount;
    private final long[] playheadOffsets;
    private long previousModePositionUs;
    private long previousModeSystemTimeUs;
    private long rawPlaybackHeadWrapCount;
    private long smoothedPlayheadOffsetUs;
    private long stopPlaybackHeadPosition;
    private long stopTimestampUs;

    /* loaded from: classes3.dex */
    public interface Listener {
        void onInvalidLatency(long j);

        void onPositionFramesMismatch(long j, long j2, long j3, long j4);

        void onSystemTimeUsMismatch(long j, long j2, long j3, long j4);

        void onUnderrun(int i, long j);
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    private @interface PlayState {
    }

    public AudioTrackPositionTracker(Listener listener) {
        this.listener = (Listener) Assertions.checkNotNull(listener);
        if (Util.SDK_INT >= 18) {
            try {
                this.getLatencyMethod = AudioTrack.class.getMethod("getLatency", null);
            } catch (NoSuchMethodException e) {
            }
        }
        this.playheadOffsets = new long[10];
    }

    public void setAudioTrack(AudioTrack audioTrack, int outputEncoding, int outputPcmFrameSize, int bufferSize) {
        this.audioTrack = audioTrack;
        this.outputPcmFrameSize = outputPcmFrameSize;
        this.bufferSize = bufferSize;
        this.audioTimestampPoller = new AudioTimestampPoller(audioTrack);
        this.outputSampleRate = audioTrack.getSampleRate();
        this.needsPassthroughWorkarounds = needsPassthroughWorkarounds(outputEncoding);
        boolean isEncodingLinearPcm = Util.isEncodingLinearPcm(outputEncoding);
        this.isOutputPcm = isEncodingLinearPcm;
        this.bufferSizeUs = isEncodingLinearPcm ? framesToDurationUs(bufferSize / outputPcmFrameSize) : -9223372036854775807L;
        this.lastRawPlaybackHeadPosition = 0L;
        this.rawPlaybackHeadWrapCount = 0L;
        this.passthroughWorkaroundPauseOffset = 0L;
        this.hasData = false;
        this.stopTimestampUs = C.TIME_UNSET;
        this.forceResetWorkaroundTimeMs = C.TIME_UNSET;
        this.lastLatencySampleTimeUs = 0L;
        this.latencyUs = 0L;
    }

    public long getCurrentPositionUs(boolean sourceEnded) {
        long timestampPositionUs;
        if (((AudioTrack) Assertions.checkNotNull(this.audioTrack)).getPlayState() == 3) {
            maybeSampleSyncParams();
        }
        long systemTimeUs = System.nanoTime() / 1000;
        AudioTimestampPoller audioTimestampPoller = (AudioTimestampPoller) Assertions.checkNotNull(this.audioTimestampPoller);
        boolean useGetTimestampMode = audioTimestampPoller.hasAdvancingTimestamp();
        if (useGetTimestampMode) {
            long timestampPositionFrames = audioTimestampPoller.getTimestampPositionFrames();
            long timestampPositionUs2 = framesToDurationUs(timestampPositionFrames);
            long elapsedSinceTimestampUs = systemTimeUs - audioTimestampPoller.getTimestampSystemTimeUs();
            timestampPositionUs = timestampPositionUs2 + elapsedSinceTimestampUs;
        } else {
            if (this.playheadOffsetCount == 0) {
                timestampPositionUs = getPlaybackHeadPositionUs();
            } else {
                long positionUs = this.smoothedPlayheadOffsetUs;
                timestampPositionUs = positionUs + systemTimeUs;
            }
            if (!sourceEnded) {
                timestampPositionUs = Math.max(0L, timestampPositionUs - this.latencyUs);
            }
        }
        if (this.lastSampleUsedGetTimestampMode != useGetTimestampMode) {
            this.previousModeSystemTimeUs = this.lastSystemTimeUs;
            this.previousModePositionUs = this.lastPositionUs;
        }
        long elapsedSincePreviousModeUs = systemTimeUs - this.previousModeSystemTimeUs;
        if (elapsedSincePreviousModeUs < 1000000) {
            long previousModeProjectedPositionUs = this.previousModePositionUs + elapsedSincePreviousModeUs;
            long rampPoint = (elapsedSincePreviousModeUs * 1000) / 1000000;
            long positionUs2 = timestampPositionUs * rampPoint;
            timestampPositionUs = (positionUs2 + ((1000 - rampPoint) * previousModeProjectedPositionUs)) / 1000;
        }
        this.lastSystemTimeUs = systemTimeUs;
        this.lastPositionUs = timestampPositionUs;
        this.lastSampleUsedGetTimestampMode = useGetTimestampMode;
        return timestampPositionUs;
    }

    public void start() {
        ((AudioTimestampPoller) Assertions.checkNotNull(this.audioTimestampPoller)).reset();
    }

    public boolean isPlaying() {
        return ((AudioTrack) Assertions.checkNotNull(this.audioTrack)).getPlayState() == 3;
    }

    public boolean mayHandleBuffer(long writtenFrames) {
        Listener listener;
        int playState = ((AudioTrack) Assertions.checkNotNull(this.audioTrack)).getPlayState();
        if (this.needsPassthroughWorkarounds) {
            if (playState == 2) {
                this.hasData = false;
                return false;
            } else if (playState == 1 && getPlaybackHeadPosition() == 0) {
                return false;
            }
        }
        boolean hadData = this.hasData;
        boolean hasPendingData = hasPendingData(writtenFrames);
        this.hasData = hasPendingData;
        if (hadData && !hasPendingData && playState != 1 && (listener = this.listener) != null) {
            listener.onUnderrun(this.bufferSize, C.usToMs(this.bufferSizeUs));
        }
        return true;
    }

    public int getAvailableBufferSize(long writtenBytes) {
        int bytesPending = (int) (writtenBytes - (getPlaybackHeadPosition() * this.outputPcmFrameSize));
        return this.bufferSize - bytesPending;
    }

    public boolean isStalled(long writtenFrames) {
        return this.forceResetWorkaroundTimeMs != C.TIME_UNSET && writtenFrames > 0 && SystemClock.elapsedRealtime() - this.forceResetWorkaroundTimeMs >= 200;
    }

    public void handleEndOfStream(long writtenFrames) {
        this.stopPlaybackHeadPosition = getPlaybackHeadPosition();
        this.stopTimestampUs = SystemClock.elapsedRealtime() * 1000;
        this.endPlaybackHeadPosition = writtenFrames;
    }

    public boolean hasPendingData(long writtenFrames) {
        return writtenFrames > getPlaybackHeadPosition() || forceHasPendingData();
    }

    public boolean pause() {
        resetSyncParams();
        if (this.stopTimestampUs == C.TIME_UNSET) {
            ((AudioTimestampPoller) Assertions.checkNotNull(this.audioTimestampPoller)).reset();
            return true;
        }
        return false;
    }

    public void reset() {
        resetSyncParams();
        this.audioTrack = null;
        this.audioTimestampPoller = null;
    }

    private void maybeSampleSyncParams() {
        long playbackPositionUs = getPlaybackHeadPositionUs();
        if (playbackPositionUs == 0) {
            return;
        }
        long systemTimeUs = System.nanoTime() / 1000;
        if (systemTimeUs - this.lastPlayheadSampleTimeUs >= 30000) {
            long[] jArr = this.playheadOffsets;
            int i = this.nextPlayheadOffsetIndex;
            jArr[i] = playbackPositionUs - systemTimeUs;
            this.nextPlayheadOffsetIndex = (i + 1) % 10;
            int i2 = this.playheadOffsetCount;
            if (i2 < 10) {
                this.playheadOffsetCount = i2 + 1;
            }
            this.lastPlayheadSampleTimeUs = systemTimeUs;
            this.smoothedPlayheadOffsetUs = 0L;
            int i3 = 0;
            while (true) {
                int i4 = this.playheadOffsetCount;
                if (i3 >= i4) {
                    break;
                }
                this.smoothedPlayheadOffsetUs += this.playheadOffsets[i3] / i4;
                i3++;
            }
        }
        if (this.needsPassthroughWorkarounds) {
            return;
        }
        maybePollAndCheckTimestamp(systemTimeUs, playbackPositionUs);
        maybeUpdateLatency(systemTimeUs);
    }

    private void maybePollAndCheckTimestamp(long systemTimeUs, long playbackPositionUs) {
        AudioTimestampPoller audioTimestampPoller = (AudioTimestampPoller) Assertions.checkNotNull(this.audioTimestampPoller);
        if (!audioTimestampPoller.maybePollTimestamp(systemTimeUs)) {
            return;
        }
        long audioTimestampSystemTimeUs = audioTimestampPoller.getTimestampSystemTimeUs();
        long audioTimestampPositionFrames = audioTimestampPoller.getTimestampPositionFrames();
        if (Math.abs(audioTimestampSystemTimeUs - systemTimeUs) > 5000000) {
            this.listener.onSystemTimeUsMismatch(audioTimestampPositionFrames, audioTimestampSystemTimeUs, systemTimeUs, playbackPositionUs);
            audioTimestampPoller.rejectTimestamp();
        } else if (Math.abs(framesToDurationUs(audioTimestampPositionFrames) - playbackPositionUs) > 5000000) {
            this.listener.onPositionFramesMismatch(audioTimestampPositionFrames, audioTimestampSystemTimeUs, systemTimeUs, playbackPositionUs);
            audioTimestampPoller.rejectTimestamp();
        } else {
            audioTimestampPoller.acceptTimestamp();
        }
    }

    private void maybeUpdateLatency(long systemTimeUs) {
        Method method;
        if (this.isOutputPcm && (method = this.getLatencyMethod) != null && systemTimeUs - this.lastLatencySampleTimeUs >= 500000) {
            try {
                long intValue = (((Integer) Util.castNonNull((Integer) method.invoke(Assertions.checkNotNull(this.audioTrack), new Object[0]))).intValue() * 1000) - this.bufferSizeUs;
                this.latencyUs = intValue;
                long max = Math.max(intValue, 0L);
                this.latencyUs = max;
                if (max > 5000000) {
                    this.listener.onInvalidLatency(max);
                    this.latencyUs = 0L;
                }
            } catch (Exception e) {
                this.getLatencyMethod = null;
            }
            this.lastLatencySampleTimeUs = systemTimeUs;
        }
    }

    private long framesToDurationUs(long frameCount) {
        return (1000000 * frameCount) / this.outputSampleRate;
    }

    private void resetSyncParams() {
        this.smoothedPlayheadOffsetUs = 0L;
        this.playheadOffsetCount = 0;
        this.nextPlayheadOffsetIndex = 0;
        this.lastPlayheadSampleTimeUs = 0L;
        this.lastSystemTimeUs = 0L;
        this.previousModeSystemTimeUs = 0L;
    }

    private boolean forceHasPendingData() {
        return this.needsPassthroughWorkarounds && ((AudioTrack) Assertions.checkNotNull(this.audioTrack)).getPlayState() == 2 && getPlaybackHeadPosition() == 0;
    }

    private static boolean needsPassthroughWorkarounds(int outputEncoding) {
        return Util.SDK_INT < 23 && (outputEncoding == 5 || outputEncoding == 6);
    }

    private long getPlaybackHeadPositionUs() {
        return framesToDurationUs(getPlaybackHeadPosition());
    }

    private long getPlaybackHeadPosition() {
        AudioTrack audioTrack = (AudioTrack) Assertions.checkNotNull(this.audioTrack);
        if (this.stopTimestampUs != C.TIME_UNSET) {
            long elapsedTimeSinceStopUs = (SystemClock.elapsedRealtime() * 1000) - this.stopTimestampUs;
            long framesSinceStop = (this.outputSampleRate * elapsedTimeSinceStopUs) / 1000000;
            return Math.min(this.endPlaybackHeadPosition, this.stopPlaybackHeadPosition + framesSinceStop);
        }
        int state = audioTrack.getPlayState();
        if (state == 1) {
            return 0L;
        }
        long rawPlaybackHeadPosition = 4294967295L & audioTrack.getPlaybackHeadPosition();
        if (this.needsPassthroughWorkarounds) {
            if (state == 2 && rawPlaybackHeadPosition == 0) {
                this.passthroughWorkaroundPauseOffset = this.lastRawPlaybackHeadPosition;
            }
            rawPlaybackHeadPosition += this.passthroughWorkaroundPauseOffset;
        }
        if (Util.SDK_INT <= 29) {
            if (rawPlaybackHeadPosition == 0 && this.lastRawPlaybackHeadPosition > 0 && state == 3) {
                if (this.forceResetWorkaroundTimeMs == C.TIME_UNSET) {
                    this.forceResetWorkaroundTimeMs = SystemClock.elapsedRealtime();
                }
                return this.lastRawPlaybackHeadPosition;
            }
            this.forceResetWorkaroundTimeMs = C.TIME_UNSET;
        }
        if (this.lastRawPlaybackHeadPosition > rawPlaybackHeadPosition) {
            this.rawPlaybackHeadWrapCount++;
        }
        this.lastRawPlaybackHeadPosition = rawPlaybackHeadPosition;
        return (this.rawPlaybackHeadWrapCount << 32) + rawPlaybackHeadPosition;
    }
}
