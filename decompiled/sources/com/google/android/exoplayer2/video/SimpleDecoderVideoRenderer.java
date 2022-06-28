package com.google.android.exoplayer2.video;

import android.os.Handler;
import android.os.SystemClock;
import android.view.Surface;
import com.google.android.exoplayer2.BaseRenderer;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.decoder.SimpleDecoder;
import com.google.android.exoplayer2.drm.DrmSession;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.ExoMediaCrypto;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.TimedValueQueue;
import com.google.android.exoplayer2.util.TraceUtil;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes3.dex */
public abstract class SimpleDecoderVideoRenderer extends BaseRenderer {
    private static final int REINITIALIZATION_STATE_NONE = 0;
    private static final int REINITIALIZATION_STATE_SIGNAL_END_OF_STREAM = 1;
    private static final int REINITIALIZATION_STATE_WAIT_END_OF_STREAM = 2;
    private final long allowedJoiningTimeMs;
    private int buffersInCodecCount;
    private int consecutiveDroppedFrameCount;
    private SimpleDecoder<VideoDecoderInputBuffer, ? extends VideoDecoderOutputBuffer, ? extends VideoDecoderException> decoder;
    protected DecoderCounters decoderCounters;
    private DrmSession<ExoMediaCrypto> decoderDrmSession;
    private boolean decoderReceivedBuffers;
    private boolean drmResourcesAcquired;
    private final DrmSessionManager<ExoMediaCrypto> drmSessionManager;
    private long droppedFrameAccumulationStartTimeMs;
    private int droppedFrames;
    private final VideoRendererEventListener.EventDispatcher eventDispatcher;
    private long initialPositionUs;
    private VideoDecoderInputBuffer inputBuffer;
    private Format inputFormat;
    private boolean inputStreamEnded;
    private long lastRenderTimeUs;
    private final int maxDroppedFramesToNotify;
    private VideoDecoderOutputBuffer outputBuffer;
    private VideoDecoderOutputBufferRenderer outputBufferRenderer;
    private Format outputFormat;
    private boolean outputStreamEnded;
    private long outputStreamOffsetUs;
    private final boolean playClearSamplesWithoutKeys;
    private boolean renderedFirstFrame;
    private int reportedHeight;
    private int reportedWidth;
    private DrmSession<ExoMediaCrypto> sourceDrmSession;
    private Surface surface;
    private boolean waitingForFirstSampleInFormat;
    private boolean waitingForKeys;
    private long joiningDeadlineMs = C.TIME_UNSET;
    private final TimedValueQueue<Format> formatQueue = new TimedValueQueue<>();
    private final DecoderInputBuffer flagsOnlyBuffer = DecoderInputBuffer.newFlagsOnlyInstance();
    private int decoderReinitializationState = 0;
    private int outputMode = -1;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    private @interface ReinitializationState {
    }

    protected abstract SimpleDecoder<VideoDecoderInputBuffer, ? extends VideoDecoderOutputBuffer, ? extends VideoDecoderException> createDecoder(Format format, ExoMediaCrypto exoMediaCrypto) throws VideoDecoderException;

    protected abstract void renderOutputBufferToSurface(VideoDecoderOutputBuffer videoDecoderOutputBuffer, Surface surface) throws VideoDecoderException;

    protected abstract void setDecoderOutputMode(int i);

    protected abstract int supportsFormatInternal(DrmSessionManager<ExoMediaCrypto> drmSessionManager, Format format);

    protected SimpleDecoderVideoRenderer(long allowedJoiningTimeMs, Handler eventHandler, VideoRendererEventListener eventListener, int maxDroppedFramesToNotify, DrmSessionManager<ExoMediaCrypto> drmSessionManager, boolean playClearSamplesWithoutKeys) {
        super(2);
        this.allowedJoiningTimeMs = allowedJoiningTimeMs;
        this.maxDroppedFramesToNotify = maxDroppedFramesToNotify;
        this.drmSessionManager = drmSessionManager;
        this.playClearSamplesWithoutKeys = playClearSamplesWithoutKeys;
        clearReportedVideoSize();
        this.eventDispatcher = new VideoRendererEventListener.EventDispatcher(eventHandler, eventListener);
    }

    @Override // com.google.android.exoplayer2.RendererCapabilities
    public final int supportsFormat(Format format) {
        return supportsFormatInternal(this.drmSessionManager, format);
    }

    @Override // com.google.android.exoplayer2.Renderer
    public void render(long positionUs, long elapsedRealtimeUs) throws ExoPlaybackException {
        if (this.outputStreamEnded) {
            return;
        }
        if (this.inputFormat == null) {
            FormatHolder formatHolder = getFormatHolder();
            this.flagsOnlyBuffer.clear();
            int result = readSource(formatHolder, this.flagsOnlyBuffer, true);
            if (result == -5) {
                onInputFormatChanged(formatHolder);
            } else if (result == -4) {
                Assertions.checkState(this.flagsOnlyBuffer.isEndOfStream());
                this.inputStreamEnded = true;
                this.outputStreamEnded = true;
                return;
            } else {
                return;
            }
        }
        maybeInitDecoder();
        if (this.decoder != null) {
            try {
                TraceUtil.beginSection("drainAndFeed");
                while (drainOutputBuffer(positionUs, elapsedRealtimeUs)) {
                }
                while (feedInputBuffer()) {
                }
                TraceUtil.endSection();
                this.decoderCounters.ensureUpdated();
            } catch (VideoDecoderException e) {
                throw createRendererException(e, this.inputFormat);
            }
        }
    }

    @Override // com.google.android.exoplayer2.Renderer
    public boolean isEnded() {
        return this.outputStreamEnded;
    }

    @Override // com.google.android.exoplayer2.Renderer
    public boolean isReady() {
        if (this.waitingForKeys) {
            return false;
        }
        if (this.inputFormat != null && ((isSourceReady() || this.outputBuffer != null) && (this.renderedFirstFrame || !hasOutput()))) {
            this.joiningDeadlineMs = C.TIME_UNSET;
            return true;
        } else if (this.joiningDeadlineMs == C.TIME_UNSET) {
            return false;
        } else {
            if (SystemClock.elapsedRealtime() < this.joiningDeadlineMs) {
                return true;
            }
            this.joiningDeadlineMs = C.TIME_UNSET;
            return false;
        }
    }

    @Override // com.google.android.exoplayer2.BaseRenderer
    protected void onEnabled(boolean joining) throws ExoPlaybackException {
        DrmSessionManager<ExoMediaCrypto> drmSessionManager = this.drmSessionManager;
        if (drmSessionManager != null && !this.drmResourcesAcquired) {
            this.drmResourcesAcquired = true;
            drmSessionManager.prepare();
        }
        DecoderCounters decoderCounters = new DecoderCounters();
        this.decoderCounters = decoderCounters;
        this.eventDispatcher.enabled(decoderCounters);
    }

    @Override // com.google.android.exoplayer2.BaseRenderer
    protected void onPositionReset(long positionUs, boolean joining) throws ExoPlaybackException {
        this.inputStreamEnded = false;
        this.outputStreamEnded = false;
        clearRenderedFirstFrame();
        this.initialPositionUs = C.TIME_UNSET;
        this.consecutiveDroppedFrameCount = 0;
        if (this.decoder != null) {
            flushDecoder();
        }
        if (joining) {
            setJoiningDeadlineMs();
        } else {
            this.joiningDeadlineMs = C.TIME_UNSET;
        }
        this.formatQueue.clear();
    }

    @Override // com.google.android.exoplayer2.BaseRenderer
    protected void onStarted() {
        this.droppedFrames = 0;
        this.droppedFrameAccumulationStartTimeMs = SystemClock.elapsedRealtime();
        this.lastRenderTimeUs = SystemClock.elapsedRealtime() * 1000;
    }

    @Override // com.google.android.exoplayer2.BaseRenderer
    protected void onStopped() {
        this.joiningDeadlineMs = C.TIME_UNSET;
        maybeNotifyDroppedFrames();
    }

    @Override // com.google.android.exoplayer2.BaseRenderer
    protected void onDisabled() {
        this.inputFormat = null;
        this.waitingForKeys = false;
        clearReportedVideoSize();
        clearRenderedFirstFrame();
        try {
            setSourceDrmSession(null);
            releaseDecoder();
        } finally {
            this.eventDispatcher.disabled(this.decoderCounters);
        }
    }

    @Override // com.google.android.exoplayer2.BaseRenderer
    protected void onReset() {
        DrmSessionManager<ExoMediaCrypto> drmSessionManager = this.drmSessionManager;
        if (drmSessionManager != null && this.drmResourcesAcquired) {
            this.drmResourcesAcquired = false;
            drmSessionManager.release();
        }
    }

    @Override // com.google.android.exoplayer2.BaseRenderer
    public void onStreamChanged(Format[] formats, long offsetUs) throws ExoPlaybackException {
        this.outputStreamOffsetUs = offsetUs;
        super.onStreamChanged(formats, offsetUs);
    }

    protected void onDecoderInitialized(String name, long initializedTimestampMs, long initializationDurationMs) {
        this.eventDispatcher.decoderInitialized(name, initializedTimestampMs, initializationDurationMs);
    }

    protected void flushDecoder() throws ExoPlaybackException {
        this.waitingForKeys = false;
        this.buffersInCodecCount = 0;
        if (this.decoderReinitializationState != 0) {
            releaseDecoder();
            maybeInitDecoder();
            return;
        }
        this.inputBuffer = null;
        VideoDecoderOutputBuffer videoDecoderOutputBuffer = this.outputBuffer;
        if (videoDecoderOutputBuffer != null) {
            videoDecoderOutputBuffer.release();
            this.outputBuffer = null;
        }
        this.decoder.flush();
        this.decoderReceivedBuffers = false;
    }

    protected void releaseDecoder() {
        this.inputBuffer = null;
        this.outputBuffer = null;
        this.decoderReinitializationState = 0;
        this.decoderReceivedBuffers = false;
        this.buffersInCodecCount = 0;
        SimpleDecoder<VideoDecoderInputBuffer, ? extends VideoDecoderOutputBuffer, ? extends VideoDecoderException> simpleDecoder = this.decoder;
        if (simpleDecoder != null) {
            simpleDecoder.release();
            this.decoder = null;
            this.decoderCounters.decoderReleaseCount++;
        }
        setDecoderDrmSession(null);
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void onInputFormatChanged(FormatHolder formatHolder) throws ExoPlaybackException {
        this.waitingForFirstSampleInFormat = true;
        Format newFormat = (Format) Assertions.checkNotNull(formatHolder.format);
        if (formatHolder.includesDrmSession) {
            setSourceDrmSession(formatHolder.drmSession);
        } else {
            this.sourceDrmSession = getUpdatedSourceDrmSession(this.inputFormat, newFormat, this.drmSessionManager, this.sourceDrmSession);
        }
        this.inputFormat = newFormat;
        if (this.sourceDrmSession != this.decoderDrmSession) {
            if (this.decoderReceivedBuffers) {
                this.decoderReinitializationState = 1;
            } else {
                releaseDecoder();
                maybeInitDecoder();
            }
        }
        this.eventDispatcher.inputFormatChanged(this.inputFormat);
    }

    protected void onQueueInputBuffer(VideoDecoderInputBuffer buffer) {
    }

    protected void onProcessedOutputBuffer(long presentationTimeUs) {
        this.buffersInCodecCount--;
    }

    protected boolean shouldDropOutputBuffer(long earlyUs, long elapsedRealtimeUs) {
        return isBufferLate(earlyUs);
    }

    protected boolean shouldDropBuffersToKeyframe(long earlyUs, long elapsedRealtimeUs) {
        return isBufferVeryLate(earlyUs);
    }

    protected boolean shouldForceRenderOutputBuffer(long earlyUs, long elapsedSinceLastRenderUs) {
        return isBufferLate(earlyUs) && elapsedSinceLastRenderUs > 100000;
    }

    protected void skipOutputBuffer(VideoDecoderOutputBuffer outputBuffer) {
        this.decoderCounters.skippedOutputBufferCount++;
        outputBuffer.release();
    }

    protected void dropOutputBuffer(VideoDecoderOutputBuffer outputBuffer) {
        updateDroppedBufferCounters(1);
        outputBuffer.release();
    }

    protected boolean maybeDropBuffersToKeyframe(long positionUs) throws ExoPlaybackException {
        int droppedSourceBufferCount = skipSource(positionUs);
        if (droppedSourceBufferCount == 0) {
            return false;
        }
        this.decoderCounters.droppedToKeyframeCount++;
        updateDroppedBufferCounters(this.buffersInCodecCount + droppedSourceBufferCount);
        flushDecoder();
        return true;
    }

    protected void updateDroppedBufferCounters(int droppedBufferCount) {
        this.decoderCounters.droppedBufferCount += droppedBufferCount;
        this.droppedFrames += droppedBufferCount;
        int i = this.consecutiveDroppedFrameCount + droppedBufferCount;
        this.consecutiveDroppedFrameCount = i;
        DecoderCounters decoderCounters = this.decoderCounters;
        decoderCounters.maxConsecutiveDroppedBufferCount = Math.max(i, decoderCounters.maxConsecutiveDroppedBufferCount);
        int i2 = this.maxDroppedFramesToNotify;
        if (i2 > 0 && this.droppedFrames >= i2) {
            maybeNotifyDroppedFrames();
        }
    }

    protected void renderOutputBuffer(VideoDecoderOutputBuffer outputBuffer, long presentationTimeUs, Format outputFormat) throws VideoDecoderException {
        this.lastRenderTimeUs = C.msToUs(SystemClock.elapsedRealtime() * 1000);
        int bufferMode = outputBuffer.mode;
        boolean renderSurface = bufferMode == 1 && this.surface != null;
        boolean renderYuv = bufferMode == 0 && this.outputBufferRenderer != null;
        if (!renderYuv && !renderSurface) {
            dropOutputBuffer(outputBuffer);
            return;
        }
        maybeNotifyVideoSizeChanged(outputBuffer.width, outputBuffer.height);
        if (renderYuv) {
            this.outputBufferRenderer.setOutputBuffer(outputBuffer);
        } else {
            renderOutputBufferToSurface(outputBuffer, this.surface);
        }
        this.consecutiveDroppedFrameCount = 0;
        this.decoderCounters.renderedOutputBufferCount++;
        maybeNotifyRenderedFirstFrame();
    }

    protected final void setOutputSurface(Surface surface) {
        if (this.surface != surface) {
            this.surface = surface;
            if (surface != null) {
                this.outputBufferRenderer = null;
                this.outputMode = 1;
                if (this.decoder != null) {
                    setDecoderOutputMode(1);
                }
                onOutputChanged();
                return;
            }
            this.outputMode = -1;
            onOutputRemoved();
        } else if (surface != null) {
            onOutputReset();
        }
    }

    protected final void setOutputBufferRenderer(VideoDecoderOutputBufferRenderer outputBufferRenderer) {
        if (this.outputBufferRenderer != outputBufferRenderer) {
            this.outputBufferRenderer = outputBufferRenderer;
            if (outputBufferRenderer != null) {
                this.surface = null;
                this.outputMode = 0;
                if (this.decoder != null) {
                    setDecoderOutputMode(0);
                }
                onOutputChanged();
                return;
            }
            this.outputMode = -1;
            onOutputRemoved();
        } else if (outputBufferRenderer != null) {
            onOutputReset();
        }
    }

    private void setSourceDrmSession(DrmSession<ExoMediaCrypto> session) {
        DrmSession.CC.replaceSession(this.sourceDrmSession, session);
        this.sourceDrmSession = session;
    }

    private void setDecoderDrmSession(DrmSession<ExoMediaCrypto> session) {
        DrmSession.CC.replaceSession(this.decoderDrmSession, session);
        this.decoderDrmSession = session;
    }

    private void maybeInitDecoder() throws ExoPlaybackException {
        if (this.decoder != null) {
            return;
        }
        setDecoderDrmSession(this.sourceDrmSession);
        ExoMediaCrypto mediaCrypto = null;
        DrmSession<ExoMediaCrypto> drmSession = this.decoderDrmSession;
        if (drmSession != null && (mediaCrypto = drmSession.getMediaCrypto()) == null) {
            DrmSession.DrmSessionException drmError = this.decoderDrmSession.getError();
            if (drmError == null) {
                return;
            }
        }
        try {
            long decoderInitializingTimestamp = SystemClock.elapsedRealtime();
            this.decoder = createDecoder(this.inputFormat, mediaCrypto);
            setDecoderOutputMode(this.outputMode);
            long decoderInitializedTimestamp = SystemClock.elapsedRealtime();
            onDecoderInitialized(this.decoder.getName(), decoderInitializedTimestamp, decoderInitializedTimestamp - decoderInitializingTimestamp);
            this.decoderCounters.decoderInitCount++;
        } catch (VideoDecoderException e) {
            throw createRendererException(e, this.inputFormat);
        }
    }

    private boolean feedInputBuffer() throws VideoDecoderException, ExoPlaybackException {
        int result;
        SimpleDecoder<VideoDecoderInputBuffer, ? extends VideoDecoderOutputBuffer, ? extends VideoDecoderException> simpleDecoder = this.decoder;
        if (simpleDecoder == null || this.decoderReinitializationState == 2 || this.inputStreamEnded) {
            return false;
        }
        if (this.inputBuffer == null) {
            VideoDecoderInputBuffer dequeueInputBuffer = simpleDecoder.dequeueInputBuffer();
            this.inputBuffer = dequeueInputBuffer;
            if (dequeueInputBuffer == null) {
                return false;
            }
        }
        if (this.decoderReinitializationState == 1) {
            this.inputBuffer.setFlags(4);
            this.decoder.queueInputBuffer((SimpleDecoder<VideoDecoderInputBuffer, ? extends VideoDecoderOutputBuffer, ? extends VideoDecoderException>) this.inputBuffer);
            this.inputBuffer = null;
            this.decoderReinitializationState = 2;
            return false;
        }
        FormatHolder formatHolder = getFormatHolder();
        if (this.waitingForKeys) {
            result = -4;
        } else {
            result = readSource(formatHolder, this.inputBuffer, false);
        }
        if (result == -3) {
            return false;
        }
        if (result == -5) {
            onInputFormatChanged(formatHolder);
            return true;
        } else if (this.inputBuffer.isEndOfStream()) {
            this.inputStreamEnded = true;
            this.decoder.queueInputBuffer((SimpleDecoder<VideoDecoderInputBuffer, ? extends VideoDecoderOutputBuffer, ? extends VideoDecoderException>) this.inputBuffer);
            this.inputBuffer = null;
            return false;
        } else {
            boolean bufferEncrypted = this.inputBuffer.isEncrypted();
            boolean shouldWaitForKeys = shouldWaitForKeys(bufferEncrypted);
            this.waitingForKeys = shouldWaitForKeys;
            if (shouldWaitForKeys) {
                return false;
            }
            if (this.waitingForFirstSampleInFormat) {
                this.formatQueue.add(this.inputBuffer.timeUs, this.inputFormat);
                this.waitingForFirstSampleInFormat = false;
            }
            this.inputBuffer.flip();
            this.inputBuffer.colorInfo = this.inputFormat.colorInfo;
            onQueueInputBuffer(this.inputBuffer);
            this.decoder.queueInputBuffer((SimpleDecoder<VideoDecoderInputBuffer, ? extends VideoDecoderOutputBuffer, ? extends VideoDecoderException>) this.inputBuffer);
            this.buffersInCodecCount++;
            this.decoderReceivedBuffers = true;
            this.decoderCounters.inputBufferCount++;
            this.inputBuffer = null;
            return true;
        }
    }

    private boolean drainOutputBuffer(long positionUs, long elapsedRealtimeUs) throws ExoPlaybackException, VideoDecoderException {
        if (this.outputBuffer == null) {
            VideoDecoderOutputBuffer dequeueOutputBuffer = this.decoder.dequeueOutputBuffer();
            this.outputBuffer = dequeueOutputBuffer;
            if (dequeueOutputBuffer == null) {
                return false;
            }
            this.decoderCounters.skippedOutputBufferCount += this.outputBuffer.skippedOutputBufferCount;
            this.buffersInCodecCount -= this.outputBuffer.skippedOutputBufferCount;
        }
        if (this.outputBuffer.isEndOfStream()) {
            if (this.decoderReinitializationState == 2) {
                releaseDecoder();
                maybeInitDecoder();
            } else {
                this.outputBuffer.release();
                this.outputBuffer = null;
                this.outputStreamEnded = true;
            }
            return false;
        }
        boolean processedOutputBuffer = processOutputBuffer(positionUs, elapsedRealtimeUs);
        if (processedOutputBuffer) {
            onProcessedOutputBuffer(this.outputBuffer.timeUs);
            this.outputBuffer = null;
        }
        return processedOutputBuffer;
    }

    private boolean processOutputBuffer(long positionUs, long elapsedRealtimeUs) throws ExoPlaybackException, VideoDecoderException {
        if (this.initialPositionUs == C.TIME_UNSET) {
            this.initialPositionUs = positionUs;
        }
        long earlyUs = this.outputBuffer.timeUs - positionUs;
        if (hasOutput()) {
            long presentationTimeUs = this.outputBuffer.timeUs - this.outputStreamOffsetUs;
            Format format = this.formatQueue.pollFloor(presentationTimeUs);
            if (format != null) {
                this.outputFormat = format;
            }
            long elapsedRealtimeNowUs = SystemClock.elapsedRealtime() * 1000;
            boolean isStarted = getState() == 2;
            if (!this.renderedFirstFrame || (isStarted && shouldForceRenderOutputBuffer(earlyUs, elapsedRealtimeNowUs - this.lastRenderTimeUs))) {
                renderOutputBuffer(this.outputBuffer, presentationTimeUs, this.outputFormat);
                return true;
            } else if (!isStarted || positionUs == this.initialPositionUs) {
                return false;
            } else {
                if (shouldDropBuffersToKeyframe(earlyUs, elapsedRealtimeUs) && maybeDropBuffersToKeyframe(positionUs)) {
                    return false;
                }
                if (shouldDropOutputBuffer(earlyUs, elapsedRealtimeUs)) {
                    dropOutputBuffer(this.outputBuffer);
                    return true;
                } else if (earlyUs < 30000) {
                    renderOutputBuffer(this.outputBuffer, presentationTimeUs, this.outputFormat);
                    return true;
                } else {
                    return false;
                }
            }
        } else if (!isBufferLate(earlyUs)) {
            return false;
        } else {
            skipOutputBuffer(this.outputBuffer);
            return true;
        }
    }

    private boolean hasOutput() {
        return this.outputMode != -1;
    }

    private void onOutputChanged() {
        maybeRenotifyVideoSizeChanged();
        clearRenderedFirstFrame();
        if (getState() == 2) {
            setJoiningDeadlineMs();
        }
    }

    private void onOutputRemoved() {
        clearReportedVideoSize();
        clearRenderedFirstFrame();
    }

    private void onOutputReset() {
        maybeRenotifyVideoSizeChanged();
        maybeRenotifyRenderedFirstFrame();
    }

    private boolean shouldWaitForKeys(boolean bufferEncrypted) throws ExoPlaybackException {
        DrmSession<ExoMediaCrypto> drmSession = this.decoderDrmSession;
        if (drmSession == null || (!bufferEncrypted && (this.playClearSamplesWithoutKeys || drmSession.playClearSamplesWithoutKeys()))) {
            return false;
        }
        int drmSessionState = this.decoderDrmSession.getState();
        if (drmSessionState == 1) {
            throw createRendererException(this.decoderDrmSession.getError(), this.inputFormat);
        }
        return drmSessionState != 4;
    }

    private void setJoiningDeadlineMs() {
        long j;
        if (this.allowedJoiningTimeMs > 0) {
            j = SystemClock.elapsedRealtime() + this.allowedJoiningTimeMs;
        } else {
            j = C.TIME_UNSET;
        }
        this.joiningDeadlineMs = j;
    }

    private void clearRenderedFirstFrame() {
        this.renderedFirstFrame = false;
    }

    private void maybeNotifyRenderedFirstFrame() {
        if (!this.renderedFirstFrame) {
            this.renderedFirstFrame = true;
            this.eventDispatcher.renderedFirstFrame(this.surface);
        }
    }

    private void maybeRenotifyRenderedFirstFrame() {
        if (this.renderedFirstFrame) {
            this.eventDispatcher.renderedFirstFrame(this.surface);
        }
    }

    private void clearReportedVideoSize() {
        this.reportedWidth = -1;
        this.reportedHeight = -1;
    }

    private void maybeNotifyVideoSizeChanged(int width, int height) {
        if (this.reportedWidth != width || this.reportedHeight != height) {
            this.reportedWidth = width;
            this.reportedHeight = height;
            this.eventDispatcher.videoSizeChanged(width, height, 0, 1.0f);
        }
    }

    private void maybeRenotifyVideoSizeChanged() {
        int i = this.reportedWidth;
        if (i != -1 || this.reportedHeight != -1) {
            this.eventDispatcher.videoSizeChanged(i, this.reportedHeight, 0, 1.0f);
        }
    }

    private void maybeNotifyDroppedFrames() {
        if (this.droppedFrames > 0) {
            long now = SystemClock.elapsedRealtime();
            long elapsedMs = now - this.droppedFrameAccumulationStartTimeMs;
            this.eventDispatcher.droppedFrames(this.droppedFrames, elapsedMs);
            this.droppedFrames = 0;
            this.droppedFrameAccumulationStartTimeMs = now;
        }
    }

    private static boolean isBufferLate(long earlyUs) {
        return earlyUs < -30000;
    }

    private static boolean isBufferVeryLate(long earlyUs) {
        return earlyUs < -500000;
    }
}
