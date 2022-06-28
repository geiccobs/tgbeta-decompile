package com.google.android.exoplayer2;

import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
/* loaded from: classes3.dex */
public class DefaultLoadControl implements LoadControl {
    public static final int DEFAULT_AUDIO_BUFFER_SIZE = 3538944;
    public static final int DEFAULT_BACK_BUFFER_DURATION_MS = 0;
    public static final int DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS = 5000;
    public static final int DEFAULT_BUFFER_FOR_PLAYBACK_MS = 2500;
    public static final int DEFAULT_CAMERA_MOTION_BUFFER_SIZE = 131072;
    public static final int DEFAULT_MAX_BUFFER_MS = 50000;
    public static final int DEFAULT_METADATA_BUFFER_SIZE = 131072;
    public static final int DEFAULT_MIN_BUFFER_MS = 15000;
    public static final int DEFAULT_MUXED_BUFFER_SIZE = 36438016;
    public static final boolean DEFAULT_PRIORITIZE_TIME_OVER_SIZE_THRESHOLDS = true;
    public static final boolean DEFAULT_RETAIN_BACK_BUFFER_FROM_KEYFRAME = false;
    public static final int DEFAULT_TARGET_BUFFER_BYTES = -1;
    public static final int DEFAULT_TEXT_BUFFER_SIZE = 131072;
    public static final int DEFAULT_VIDEO_BUFFER_SIZE = 32768000;
    private final DefaultAllocator allocator;
    private final long backBufferDurationUs;
    private final long bufferForPlaybackAfterRebufferUs;
    private final long bufferForPlaybackUs;
    private boolean hasVideo;
    private boolean isBuffering;
    private final long maxBufferUs;
    private final long minBufferAudioUs;
    private final long minBufferVideoUs;
    private final boolean prioritizeTimeOverSizeThresholds;
    private final boolean retainBackBufferFromKeyframe;
    private final int targetBufferBytesOverwrite;
    private int targetBufferSize;

    /* loaded from: classes3.dex */
    public static final class Builder {
        private DefaultAllocator allocator;
        private boolean createDefaultLoadControlCalled;
        private int minBufferAudioMs = 15000;
        private int minBufferVideoMs = 50000;
        private int maxBufferMs = 50000;
        private int bufferForPlaybackMs = 2500;
        private int bufferForPlaybackAfterRebufferMs = 5000;
        private int targetBufferBytes = -1;
        private boolean prioritizeTimeOverSizeThresholds = true;
        private int backBufferDurationMs = 0;
        private boolean retainBackBufferFromKeyframe = false;

        public Builder setAllocator(DefaultAllocator allocator) {
            Assertions.checkState(!this.createDefaultLoadControlCalled);
            this.allocator = allocator;
            return this;
        }

        public Builder setBufferDurationsMs(int minBufferMs, int maxBufferMs, int bufferForPlaybackMs, int bufferForPlaybackAfterRebufferMs) {
            Assertions.checkState(!this.createDefaultLoadControlCalled);
            DefaultLoadControl.assertGreaterOrEqual(bufferForPlaybackMs, 0, "bufferForPlaybackMs", "0");
            DefaultLoadControl.assertGreaterOrEqual(bufferForPlaybackAfterRebufferMs, 0, "bufferForPlaybackAfterRebufferMs", "0");
            DefaultLoadControl.assertGreaterOrEqual(minBufferMs, bufferForPlaybackMs, "minBufferMs", "bufferForPlaybackMs");
            DefaultLoadControl.assertGreaterOrEqual(minBufferMs, bufferForPlaybackAfterRebufferMs, "minBufferMs", "bufferForPlaybackAfterRebufferMs");
            DefaultLoadControl.assertGreaterOrEqual(maxBufferMs, minBufferMs, "maxBufferMs", "minBufferMs");
            this.minBufferAudioMs = minBufferMs;
            this.minBufferVideoMs = minBufferMs;
            this.maxBufferMs = maxBufferMs;
            this.bufferForPlaybackMs = bufferForPlaybackMs;
            this.bufferForPlaybackAfterRebufferMs = bufferForPlaybackAfterRebufferMs;
            return this;
        }

        public Builder setTargetBufferBytes(int targetBufferBytes) {
            Assertions.checkState(!this.createDefaultLoadControlCalled);
            this.targetBufferBytes = targetBufferBytes;
            return this;
        }

        public Builder setPrioritizeTimeOverSizeThresholds(boolean prioritizeTimeOverSizeThresholds) {
            Assertions.checkState(!this.createDefaultLoadControlCalled);
            this.prioritizeTimeOverSizeThresholds = prioritizeTimeOverSizeThresholds;
            return this;
        }

        public Builder setBackBuffer(int backBufferDurationMs, boolean retainBackBufferFromKeyframe) {
            Assertions.checkState(!this.createDefaultLoadControlCalled);
            DefaultLoadControl.assertGreaterOrEqual(backBufferDurationMs, 0, "backBufferDurationMs", "0");
            this.backBufferDurationMs = backBufferDurationMs;
            this.retainBackBufferFromKeyframe = retainBackBufferFromKeyframe;
            return this;
        }

        public DefaultLoadControl createDefaultLoadControl() {
            Assertions.checkState(!this.createDefaultLoadControlCalled);
            this.createDefaultLoadControlCalled = true;
            if (this.allocator == null) {
                this.allocator = new DefaultAllocator(true, 65536);
            }
            return new DefaultLoadControl(this.allocator, this.minBufferAudioMs, this.minBufferVideoMs, this.maxBufferMs, this.bufferForPlaybackMs, this.bufferForPlaybackAfterRebufferMs, this.targetBufferBytes, this.prioritizeTimeOverSizeThresholds, this.backBufferDurationMs, this.retainBackBufferFromKeyframe);
        }
    }

    public DefaultLoadControl() {
        this(new DefaultAllocator(true, 65536));
    }

    @Deprecated
    public DefaultLoadControl(DefaultAllocator allocator) {
        this(allocator, 15000, 50000, 50000, 2500, 5000, -1, true, 0, false);
    }

    @Deprecated
    public DefaultLoadControl(DefaultAllocator allocator, int minBufferMs, int maxBufferMs, int bufferForPlaybackMs, int bufferForPlaybackAfterRebufferMs, int targetBufferBytes, boolean prioritizeTimeOverSizeThresholds) {
        this(allocator, minBufferMs, minBufferMs, maxBufferMs, bufferForPlaybackMs, bufferForPlaybackAfterRebufferMs, targetBufferBytes, prioritizeTimeOverSizeThresholds, 0, false);
    }

    protected DefaultLoadControl(DefaultAllocator allocator, int minBufferAudioMs, int minBufferVideoMs, int maxBufferMs, int bufferForPlaybackMs, int bufferForPlaybackAfterRebufferMs, int targetBufferBytes, boolean prioritizeTimeOverSizeThresholds, int backBufferDurationMs, boolean retainBackBufferFromKeyframe) {
        assertGreaterOrEqual(bufferForPlaybackMs, 0, "bufferForPlaybackMs", "0");
        assertGreaterOrEqual(bufferForPlaybackAfterRebufferMs, 0, "bufferForPlaybackAfterRebufferMs", "0");
        assertGreaterOrEqual(minBufferAudioMs, bufferForPlaybackMs, "minBufferAudioMs", "bufferForPlaybackMs");
        assertGreaterOrEqual(minBufferVideoMs, bufferForPlaybackMs, "minBufferVideoMs", "bufferForPlaybackMs");
        assertGreaterOrEqual(minBufferAudioMs, bufferForPlaybackAfterRebufferMs, "minBufferAudioMs", "bufferForPlaybackAfterRebufferMs");
        assertGreaterOrEqual(minBufferVideoMs, bufferForPlaybackAfterRebufferMs, "minBufferVideoMs", "bufferForPlaybackAfterRebufferMs");
        assertGreaterOrEqual(maxBufferMs, minBufferAudioMs, "maxBufferMs", "minBufferAudioMs");
        assertGreaterOrEqual(maxBufferMs, minBufferVideoMs, "maxBufferMs", "minBufferVideoMs");
        assertGreaterOrEqual(backBufferDurationMs, 0, "backBufferDurationMs", "0");
        this.allocator = allocator;
        this.minBufferAudioUs = C.msToUs(minBufferAudioMs);
        this.minBufferVideoUs = C.msToUs(minBufferVideoMs);
        this.maxBufferUs = C.msToUs(maxBufferMs);
        this.bufferForPlaybackUs = C.msToUs(bufferForPlaybackMs);
        this.bufferForPlaybackAfterRebufferUs = C.msToUs(bufferForPlaybackAfterRebufferMs);
        this.targetBufferBytesOverwrite = targetBufferBytes;
        this.prioritizeTimeOverSizeThresholds = prioritizeTimeOverSizeThresholds;
        this.backBufferDurationUs = C.msToUs(backBufferDurationMs);
        this.retainBackBufferFromKeyframe = retainBackBufferFromKeyframe;
    }

    @Override // com.google.android.exoplayer2.LoadControl
    public void onPrepared() {
        reset(false);
    }

    @Override // com.google.android.exoplayer2.LoadControl
    public void onTracksSelected(Renderer[] renderers, TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        this.hasVideo = hasVideo(renderers, trackSelections);
        int i = this.targetBufferBytesOverwrite;
        if (i == -1) {
            i = calculateTargetBufferSize(renderers, trackSelections);
        }
        this.targetBufferSize = i;
        this.allocator.setTargetBufferSize(i);
    }

    @Override // com.google.android.exoplayer2.LoadControl
    public void onStopped() {
        reset(true);
    }

    @Override // com.google.android.exoplayer2.LoadControl
    public void onReleased() {
        reset(true);
    }

    @Override // com.google.android.exoplayer2.LoadControl
    public Allocator getAllocator() {
        return this.allocator;
    }

    @Override // com.google.android.exoplayer2.LoadControl
    public long getBackBufferDurationUs() {
        return this.backBufferDurationUs;
    }

    @Override // com.google.android.exoplayer2.LoadControl
    public boolean retainBackBufferFromKeyframe() {
        return this.retainBackBufferFromKeyframe;
    }

    @Override // com.google.android.exoplayer2.LoadControl
    public boolean shouldContinueLoading(long bufferedDurationUs, float playbackSpeed) {
        boolean z = true;
        boolean targetBufferSizeReached = this.allocator.getTotalBytesAllocated() >= this.targetBufferSize;
        long minBufferUs = this.hasVideo ? this.minBufferVideoUs : this.minBufferAudioUs;
        if (playbackSpeed > 1.0f) {
            long mediaDurationMinBufferUs = Util.getMediaDurationForPlayoutDuration(minBufferUs, playbackSpeed);
            minBufferUs = Math.min(mediaDurationMinBufferUs, this.maxBufferUs);
        }
        if (bufferedDurationUs < minBufferUs) {
            if (!this.prioritizeTimeOverSizeThresholds && targetBufferSizeReached) {
                z = false;
            }
            this.isBuffering = z;
        } else if (bufferedDurationUs >= this.maxBufferUs || targetBufferSizeReached) {
            this.isBuffering = false;
        }
        return this.isBuffering;
    }

    @Override // com.google.android.exoplayer2.LoadControl
    public boolean shouldStartPlayback(long bufferedDurationUs, float playbackSpeed, boolean rebuffering) {
        long bufferedDurationUs2 = Util.getPlayoutDurationForMediaDuration(bufferedDurationUs, playbackSpeed);
        long minBufferDurationUs = rebuffering ? this.bufferForPlaybackAfterRebufferUs : this.bufferForPlaybackUs;
        return minBufferDurationUs <= 0 || bufferedDurationUs2 >= minBufferDurationUs || (!this.prioritizeTimeOverSizeThresholds && this.allocator.getTotalBytesAllocated() >= this.targetBufferSize);
    }

    protected int calculateTargetBufferSize(Renderer[] renderers, TrackSelectionArray trackSelectionArray) {
        int targetBufferSize = 0;
        for (int i = 0; i < renderers.length; i++) {
            if (trackSelectionArray.get(i) != null) {
                targetBufferSize += getDefaultBufferSize(renderers[i].getTrackType());
            }
        }
        return targetBufferSize;
    }

    private void reset(boolean resetAllocator) {
        this.targetBufferSize = 0;
        this.isBuffering = false;
        if (resetAllocator) {
            this.allocator.reset();
        }
    }

    private static int getDefaultBufferSize(int trackType) {
        switch (trackType) {
            case 0:
                return DEFAULT_MUXED_BUFFER_SIZE;
            case 1:
                return DEFAULT_AUDIO_BUFFER_SIZE;
            case 2:
                return DEFAULT_VIDEO_BUFFER_SIZE;
            case 3:
                return 131072;
            case 4:
                return 131072;
            case 5:
                return 131072;
            case 6:
                return 0;
            default:
                throw new IllegalArgumentException();
        }
    }

    private static boolean hasVideo(Renderer[] renderers, TrackSelectionArray trackSelectionArray) {
        for (int i = 0; i < renderers.length; i++) {
            if (renderers[i].getTrackType() == 2 && trackSelectionArray.get(i) != null) {
                return true;
            }
        }
        return false;
    }

    public static void assertGreaterOrEqual(int value1, int value2, String name1, String name2) {
        boolean z = value1 >= value2;
        Assertions.checkArgument(z, name1 + " cannot be less than " + name2);
    }
}
