package com.google.android.exoplayer2.mediacodec;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaCryptoException;
import android.media.MediaFormat;
import android.os.Bundle;
import android.os.SystemClock;
import com.google.android.exoplayer2.BaseRenderer;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.drm.DrmSession;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.NalUnitUtil;
import com.google.android.exoplayer2.util.TimedValueQueue;
import com.google.android.exoplayer2.util.TraceUtil;
import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.R;
/* loaded from: classes.dex */
public abstract class MediaCodecRenderer extends BaseRenderer {
    private static final byte[] ADAPTATION_WORKAROUND_BUFFER = {0, 0, 1, 103, 66, -64, 11, -38, 37, -112, 0, 0, 1, 104, -50, 15, 19, 32, 0, 0, 1, 101, -120, -124, 13, -50, 113, 24, -96, 0, 47, -65, 28, 49, -61, 39, 93, 120};
    private final float assumedMinimumCodecOperatingRate;
    private ArrayDeque<MediaCodecInfo> availableCodecInfos;
    private MediaCodec codec;
    private int codecAdaptationWorkaroundMode;
    private DrmSession<FrameworkMediaCrypto> codecDrmSession;
    private Format codecFormat;
    private boolean codecHasOutputMediaFormat;
    private long codecHotswapDeadlineMs;
    private MediaCodecInfo codecInfo;
    private boolean codecNeedsAdaptationWorkaroundBuffer;
    private boolean codecNeedsDiscardToSpsWorkaround;
    private boolean codecNeedsEosFlushWorkaround;
    private boolean codecNeedsEosOutputExceptionWorkaround;
    private boolean codecNeedsEosPropagation;
    private boolean codecNeedsFlushWorkaround;
    private boolean codecNeedsMonoChannelCountWorkaround;
    private boolean codecNeedsReconfigureWorkaround;
    private boolean codecNeedsSosFlushWorkaround;
    private boolean codecReceivedBuffers;
    private boolean codecReceivedEos;
    private boolean codecReconfigured;
    protected DecoderCounters decoderCounters;
    private boolean drmResourcesAcquired;
    private final DrmSessionManager<FrameworkMediaCrypto> drmSessionManager;
    private final boolean enableDecoderFallback;
    private ByteBuffer[] inputBuffers;
    private Format inputFormat;
    private int inputIndex;
    private boolean inputStreamEnded;
    private boolean isDecodeOnlyOutputBuffer;
    private boolean isLastOutputBuffer;
    private long largestQueuedPresentationTimeUs;
    private long lastBufferInStreamPresentationTimeUs;
    private final MediaCodecSelector mediaCodecSelector;
    private MediaCrypto mediaCrypto;
    private boolean mediaCryptoRequiresSecureDecoder;
    private ByteBuffer outputBuffer;
    private ByteBuffer[] outputBuffers;
    private Format outputFormat;
    private int outputIndex;
    private boolean outputStreamEnded;
    private boolean pendingOutputEndOfStream;
    private final boolean playClearSamplesWithoutKeys;
    private DecoderInitializationException preferredDecoderInitializationException;
    private boolean shouldSkipAdaptationWorkaroundOutputBuffer;
    private boolean skipMediaCodecStopOnRelease;
    private DrmSession<FrameworkMediaCrypto> sourceDrmSession;
    private boolean waitingForFirstSampleInFormat;
    private boolean waitingForFirstSyncSample;
    private boolean waitingForKeys;
    private final DecoderInputBuffer buffer = new DecoderInputBuffer(0);
    private final DecoderInputBuffer flagsOnlyBuffer = DecoderInputBuffer.newFlagsOnlyInstance();
    private final TimedValueQueue<Format> formatQueue = new TimedValueQueue<>();
    private final ArrayList<Long> decodeOnlyPresentationTimestamps = new ArrayList<>();
    private final MediaCodec.BufferInfo outputBufferInfo = new MediaCodec.BufferInfo();
    private int codecReconfigurationState = 0;
    private int codecDrainState = 0;
    private int codecDrainAction = 0;
    private float codecOperatingRate = -1.0f;
    private float rendererOperatingRate = 1.0f;
    private long renderTimeLimitMs = -9223372036854775807L;

    protected abstract int canKeepCodec(MediaCodec mediaCodec, MediaCodecInfo mediaCodecInfo, Format format, Format format2);

    protected abstract void configureCodec(MediaCodecInfo mediaCodecInfo, MediaCodec mediaCodec, Format format, MediaCrypto mediaCrypto, float f);

    protected boolean getCodecNeedsEosPropagation() {
        return false;
    }

    protected abstract float getCodecOperatingRateV23(float f, Format format, Format[] formatArr);

    protected abstract List<MediaCodecInfo> getDecoderInfos(MediaCodecSelector mediaCodecSelector, Format format, boolean z) throws MediaCodecUtil.DecoderQueryException;

    protected long getDequeueOutputBufferTimeoutUs() {
        return 0L;
    }

    protected void handleInputBufferSupplementalData(DecoderInputBuffer decoderInputBuffer) throws ExoPlaybackException {
    }

    protected abstract void onCodecInitialized(String str, long j, long j2);

    protected abstract void onOutputFormatChanged(MediaCodec mediaCodec, MediaFormat mediaFormat) throws ExoPlaybackException;

    protected abstract void onProcessedOutputBuffer(long j);

    protected abstract void onQueueInputBuffer(DecoderInputBuffer decoderInputBuffer);

    @Override // com.google.android.exoplayer2.BaseRenderer
    public void onStarted() {
    }

    @Override // com.google.android.exoplayer2.BaseRenderer
    public void onStopped() {
    }

    protected abstract boolean processOutputBuffer(long j, long j2, MediaCodec mediaCodec, ByteBuffer byteBuffer, int i, int i2, long j3, boolean z, boolean z2, Format format) throws ExoPlaybackException;

    protected void renderToEndOfStream() throws ExoPlaybackException {
    }

    protected boolean shouldInitCodec(MediaCodecInfo mediaCodecInfo) {
        return true;
    }

    protected abstract int supportsFormat(MediaCodecSelector mediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, Format format) throws MediaCodecUtil.DecoderQueryException;

    @Override // com.google.android.exoplayer2.BaseRenderer, com.google.android.exoplayer2.RendererCapabilities
    public final int supportsMixedMimeTypeAdaptation() {
        return 8;
    }

    /* loaded from: classes.dex */
    public static class DecoderInitializationException extends Exception {
        public final MediaCodecInfo codecInfo;
        public final String diagnosticInfo;
        public final String mimeType;
        public final boolean secureDecoderRequired;

        public DecoderInitializationException(Format format, Throwable th, boolean z, int i) {
            this("Decoder init failed: [" + i + "], " + format, th, format.sampleMimeType, z, null, buildCustomDiagnosticInfo(i), null);
        }

        public DecoderInitializationException(Format format, Throwable th, boolean z, MediaCodecInfo mediaCodecInfo) {
            this("Decoder init failed: " + mediaCodecInfo.name + ", " + format, th, format.sampleMimeType, z, mediaCodecInfo, Util.SDK_INT >= 21 ? getDiagnosticInfoV21(th) : null, null);
        }

        private DecoderInitializationException(String str, Throwable th, String str2, boolean z, MediaCodecInfo mediaCodecInfo, String str3, DecoderInitializationException decoderInitializationException) {
            super(str, th);
            this.mimeType = str2;
            this.secureDecoderRequired = z;
            this.codecInfo = mediaCodecInfo;
            this.diagnosticInfo = str3;
        }

        public DecoderInitializationException copyWithFallbackException(DecoderInitializationException decoderInitializationException) {
            return new DecoderInitializationException(getMessage(), getCause(), this.mimeType, this.secureDecoderRequired, this.codecInfo, this.diagnosticInfo, decoderInitializationException);
        }

        @TargetApi(R.styleable.MapAttrs_uiZoomGestures)
        private static String getDiagnosticInfoV21(Throwable th) {
            if (th instanceof MediaCodec.CodecException) {
                return ((MediaCodec.CodecException) th).getDiagnosticInfo();
            }
            return null;
        }

        private static String buildCustomDiagnosticInfo(int i) {
            String str = i < 0 ? "neg_" : "";
            return "com.google.android.exoplayer2.mediacodec.MediaCodecRenderer_" + str + Math.abs(i);
        }
    }

    public MediaCodecRenderer(int i, MediaCodecSelector mediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, boolean z, boolean z2, float f) {
        super(i);
        this.mediaCodecSelector = (MediaCodecSelector) Assertions.checkNotNull(mediaCodecSelector);
        this.drmSessionManager = drmSessionManager;
        this.playClearSamplesWithoutKeys = z;
        this.enableDecoderFallback = z2;
        this.assumedMinimumCodecOperatingRate = f;
    }

    @Override // com.google.android.exoplayer2.RendererCapabilities
    public final int supportsFormat(Format format) throws ExoPlaybackException {
        try {
            return supportsFormat(this.mediaCodecSelector, this.drmSessionManager, format);
        } catch (MediaCodecUtil.DecoderQueryException e) {
            throw createRendererException(e, format);
        }
    }

    public final void maybeInitCodec() throws ExoPlaybackException {
        if (this.codec != null || this.inputFormat == null) {
            return;
        }
        setCodecDrmSession(this.sourceDrmSession);
        String str = this.inputFormat.sampleMimeType;
        DrmSession<FrameworkMediaCrypto> drmSession = this.codecDrmSession;
        if (drmSession != null) {
            if (this.mediaCrypto == null) {
                FrameworkMediaCrypto mediaCrypto = drmSession.getMediaCrypto();
                if (mediaCrypto == null) {
                    if (this.codecDrmSession.getError() == null) {
                        return;
                    }
                } else {
                    try {
                        MediaCrypto mediaCrypto2 = new MediaCrypto(mediaCrypto.uuid, mediaCrypto.sessionId);
                        this.mediaCrypto = mediaCrypto2;
                        this.mediaCryptoRequiresSecureDecoder = !mediaCrypto.forceAllowInsecureDecoderComponents && mediaCrypto2.requiresSecureDecoderComponent(str);
                    } catch (MediaCryptoException e) {
                        throw createRendererException(e, this.inputFormat);
                    }
                }
            }
            if (FrameworkMediaCrypto.WORKAROUND_DEVICE_NEEDS_KEYS_TO_CONFIGURE_CODEC) {
                int state = this.codecDrmSession.getState();
                if (state == 1) {
                    throw createRendererException(this.codecDrmSession.getError(), this.inputFormat);
                }
                if (state != 4) {
                    return;
                }
            }
        }
        try {
            maybeInitCodecWithFallback(this.mediaCrypto, this.mediaCryptoRequiresSecureDecoder);
        } catch (DecoderInitializationException e2) {
            throw createRendererException(e2, this.inputFormat);
        }
    }

    public final Format updateOutputFormatForTime(long j) {
        Format pollFloor = this.formatQueue.pollFloor(j);
        if (pollFloor != null) {
            this.outputFormat = pollFloor;
        }
        return pollFloor;
    }

    public final MediaCodec getCodec() {
        return this.codec;
    }

    public final MediaCodecInfo getCodecInfo() {
        return this.codecInfo;
    }

    @Override // com.google.android.exoplayer2.BaseRenderer
    public void onEnabled(boolean z) throws ExoPlaybackException {
        DrmSessionManager<FrameworkMediaCrypto> drmSessionManager = this.drmSessionManager;
        if (drmSessionManager != null && !this.drmResourcesAcquired) {
            this.drmResourcesAcquired = true;
            drmSessionManager.prepare();
        }
        this.decoderCounters = new DecoderCounters();
    }

    @Override // com.google.android.exoplayer2.BaseRenderer
    public void onPositionReset(long j, boolean z) throws ExoPlaybackException {
        this.inputStreamEnded = false;
        this.outputStreamEnded = false;
        this.pendingOutputEndOfStream = false;
        flushOrReinitializeCodec();
        this.formatQueue.clear();
    }

    @Override // com.google.android.exoplayer2.BaseRenderer, com.google.android.exoplayer2.Renderer
    public final void setOperatingRate(float f) throws ExoPlaybackException {
        this.rendererOperatingRate = f;
        if (this.codec == null || this.codecDrainAction == 3 || getState() == 0) {
            return;
        }
        updateCodecOperatingRate();
    }

    @Override // com.google.android.exoplayer2.BaseRenderer
    public void onDisabled() {
        this.inputFormat = null;
        if (this.sourceDrmSession != null || this.codecDrmSession != null) {
            onReset();
        } else {
            flushOrReleaseCodec();
        }
    }

    @Override // com.google.android.exoplayer2.BaseRenderer
    public void onReset() {
        try {
            releaseCodec();
            setSourceDrmSession(null);
            DrmSessionManager<FrameworkMediaCrypto> drmSessionManager = this.drmSessionManager;
            if (drmSessionManager == null || !this.drmResourcesAcquired) {
                return;
            }
            this.drmResourcesAcquired = false;
            drmSessionManager.release();
        } catch (Throwable th) {
            setSourceDrmSession(null);
            throw th;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v0, types: [com.google.android.exoplayer2.drm.DrmSession, android.media.MediaCrypto] */
    public void releaseCodec() {
        this.availableCodecInfos = null;
        this.codecInfo = null;
        this.codecFormat = null;
        this.codecHasOutputMediaFormat = false;
        resetInputBuffer();
        resetOutputBuffer();
        resetCodecBuffers();
        this.waitingForKeys = false;
        this.codecHotswapDeadlineMs = -9223372036854775807L;
        this.decodeOnlyPresentationTimestamps.clear();
        this.largestQueuedPresentationTimeUs = -9223372036854775807L;
        this.lastBufferInStreamPresentationTimeUs = -9223372036854775807L;
        try {
            MediaCodec mediaCodec = this.codec;
            if (mediaCodec != null) {
                this.decoderCounters.decoderReleaseCount++;
                if (!this.skipMediaCodecStopOnRelease) {
                    mediaCodec.stop();
                }
                this.codec.release();
            }
            this.codec = null;
            try {
                MediaCrypto mediaCrypto = this.mediaCrypto;
                if (mediaCrypto != null) {
                    mediaCrypto.release();
                }
            } finally {
            }
        } catch (Throwable th) {
            this.codec = null;
            try {
                MediaCrypto mediaCrypto2 = this.mediaCrypto;
                if (mediaCrypto2 != null) {
                    mediaCrypto2.release();
                }
                throw th;
            } finally {
            }
        }
    }

    @Override // com.google.android.exoplayer2.Renderer
    public void render(long j, long j2) throws ExoPlaybackException {
        if (this.pendingOutputEndOfStream) {
            this.pendingOutputEndOfStream = false;
            processEndOfStream();
        }
        try {
            if (this.outputStreamEnded) {
                renderToEndOfStream();
            } else if (this.inputFormat == null && !readToFlagsOnlyBuffer(true)) {
            } else {
                maybeInitCodec();
                if (this.codec != null) {
                    long elapsedRealtime = SystemClock.elapsedRealtime();
                    TraceUtil.beginSection("drainAndFeed");
                    while (drainOutputBuffer(j, j2)) {
                    }
                    while (feedInputBuffer() && shouldContinueFeeding(elapsedRealtime)) {
                    }
                    TraceUtil.endSection();
                } else {
                    this.decoderCounters.skippedInputBufferCount += skipSource(j);
                    readToFlagsOnlyBuffer(false);
                }
                this.decoderCounters.ensureUpdated();
            }
        } catch (IllegalStateException e) {
            if (!isMediaCodecException(e)) {
                throw e;
            }
            throw createRendererException(e, this.inputFormat);
        }
    }

    public final boolean flushOrReinitializeCodec() throws ExoPlaybackException {
        boolean flushOrReleaseCodec = flushOrReleaseCodec();
        if (flushOrReleaseCodec) {
            maybeInitCodec();
        }
        return flushOrReleaseCodec;
    }

    public boolean flushOrReleaseCodec() {
        MediaCodec mediaCodec = this.codec;
        if (mediaCodec == null) {
            return false;
        }
        if (this.codecDrainAction == 3 || this.codecNeedsFlushWorkaround || ((this.codecNeedsSosFlushWorkaround && !this.codecHasOutputMediaFormat) || (this.codecNeedsEosFlushWorkaround && this.codecReceivedEos))) {
            releaseCodec();
            return true;
        }
        mediaCodec.flush();
        resetInputBuffer();
        resetOutputBuffer();
        this.codecHotswapDeadlineMs = -9223372036854775807L;
        this.codecReceivedEos = false;
        this.codecReceivedBuffers = false;
        this.waitingForFirstSyncSample = true;
        this.codecNeedsAdaptationWorkaroundBuffer = false;
        this.shouldSkipAdaptationWorkaroundOutputBuffer = false;
        this.isDecodeOnlyOutputBuffer = false;
        this.isLastOutputBuffer = false;
        this.waitingForKeys = false;
        this.decodeOnlyPresentationTimestamps.clear();
        this.largestQueuedPresentationTimeUs = -9223372036854775807L;
        this.lastBufferInStreamPresentationTimeUs = -9223372036854775807L;
        this.codecDrainState = 0;
        this.codecDrainAction = 0;
        this.codecReconfigurationState = this.codecReconfigured ? 1 : 0;
        return false;
    }

    private boolean readToFlagsOnlyBuffer(boolean z) throws ExoPlaybackException {
        FormatHolder formatHolder = getFormatHolder();
        this.flagsOnlyBuffer.clear();
        int readSource = readSource(formatHolder, this.flagsOnlyBuffer, z);
        if (readSource == -5) {
            onInputFormatChanged(formatHolder);
            return true;
        } else if (readSource != -4 || !this.flagsOnlyBuffer.isEndOfStream()) {
            return false;
        } else {
            this.inputStreamEnded = true;
            processEndOfStream();
            return false;
        }
    }

    private void maybeInitCodecWithFallback(MediaCrypto mediaCrypto, boolean z) throws DecoderInitializationException {
        if (this.availableCodecInfos == null) {
            try {
                List<MediaCodecInfo> availableCodecInfos = getAvailableCodecInfos(z);
                ArrayDeque<MediaCodecInfo> arrayDeque = new ArrayDeque<>();
                this.availableCodecInfos = arrayDeque;
                if (this.enableDecoderFallback) {
                    arrayDeque.addAll(availableCodecInfos);
                } else if (!availableCodecInfos.isEmpty()) {
                    this.availableCodecInfos.add(availableCodecInfos.get(0));
                }
                this.preferredDecoderInitializationException = null;
            } catch (MediaCodecUtil.DecoderQueryException e) {
                throw new DecoderInitializationException(this.inputFormat, e, z, -49998);
            }
        }
        if (this.availableCodecInfos.isEmpty()) {
            throw new DecoderInitializationException(this.inputFormat, (Throwable) null, z, -49999);
        }
        while (this.codec == null) {
            MediaCodecInfo peekFirst = this.availableCodecInfos.peekFirst();
            if (!shouldInitCodec(peekFirst)) {
                return;
            }
            try {
                initCodec(peekFirst, mediaCrypto);
            } catch (Exception e2) {
                Log.w("MediaCodecRenderer", "Failed to initialize decoder: " + peekFirst, e2);
                this.availableCodecInfos.removeFirst();
                DecoderInitializationException decoderInitializationException = new DecoderInitializationException(this.inputFormat, e2, z, peekFirst);
                if (this.preferredDecoderInitializationException != null) {
                    this.preferredDecoderInitializationException = this.preferredDecoderInitializationException.copyWithFallbackException(decoderInitializationException);
                } else {
                    this.preferredDecoderInitializationException = decoderInitializationException;
                }
                if (this.availableCodecInfos.isEmpty()) {
                    throw this.preferredDecoderInitializationException;
                }
            }
        }
        this.availableCodecInfos = null;
    }

    private List<MediaCodecInfo> getAvailableCodecInfos(boolean z) throws MediaCodecUtil.DecoderQueryException {
        List<MediaCodecInfo> decoderInfos = getDecoderInfos(this.mediaCodecSelector, this.inputFormat, z);
        if (decoderInfos.isEmpty() && z) {
            decoderInfos = getDecoderInfos(this.mediaCodecSelector, this.inputFormat, false);
            if (!decoderInfos.isEmpty()) {
                Log.w("MediaCodecRenderer", "Drm session requires secure decoder for " + this.inputFormat.sampleMimeType + ", but no secure decoder available. Trying to proceed with " + decoderInfos + ".");
            }
        }
        return decoderInfos;
    }

    private void initCodec(MediaCodecInfo mediaCodecInfo, MediaCrypto mediaCrypto) throws Exception {
        Exception e;
        long elapsedRealtime;
        MediaCodec createByCodecName;
        String str = mediaCodecInfo.name;
        float codecOperatingRateV23 = Util.SDK_INT < 23 ? -1.0f : getCodecOperatingRateV23(this.rendererOperatingRate, this.inputFormat, getStreamFormats());
        float f = codecOperatingRateV23 <= this.assumedMinimumCodecOperatingRate ? -1.0f : codecOperatingRateV23;
        MediaCodec mediaCodec = null;
        try {
            elapsedRealtime = SystemClock.elapsedRealtime();
            TraceUtil.beginSection("createCodec:" + str);
            createByCodecName = MediaCodec.createByCodecName(str);
        } catch (Exception e2) {
            e = e2;
        }
        try {
            TraceUtil.endSection();
            TraceUtil.beginSection("configureCodec");
            configureCodec(mediaCodecInfo, createByCodecName, this.inputFormat, mediaCrypto, f);
            TraceUtil.endSection();
            TraceUtil.beginSection("startCodec");
            createByCodecName.start();
            TraceUtil.endSection();
            long elapsedRealtime2 = SystemClock.elapsedRealtime();
            getCodecBuffers(createByCodecName);
            this.codec = createByCodecName;
            this.codecInfo = mediaCodecInfo;
            this.codecOperatingRate = f;
            this.codecFormat = this.inputFormat;
            this.codecAdaptationWorkaroundMode = codecAdaptationWorkaroundMode(str);
            this.codecNeedsReconfigureWorkaround = codecNeedsReconfigureWorkaround(str);
            this.codecNeedsDiscardToSpsWorkaround = codecNeedsDiscardToSpsWorkaround(str, this.codecFormat);
            this.codecNeedsFlushWorkaround = codecNeedsFlushWorkaround(str);
            this.codecNeedsSosFlushWorkaround = codecNeedsSosFlushWorkaround(str);
            this.codecNeedsEosFlushWorkaround = codecNeedsEosFlushWorkaround(str);
            this.codecNeedsEosOutputExceptionWorkaround = codecNeedsEosOutputExceptionWorkaround(str);
            this.codecNeedsMonoChannelCountWorkaround = codecNeedsMonoChannelCountWorkaround(str, this.codecFormat);
            this.codecNeedsEosPropagation = codecNeedsEosPropagationWorkaround(mediaCodecInfo) || getCodecNeedsEosPropagation();
            resetInputBuffer();
            resetOutputBuffer();
            this.codecHotswapDeadlineMs = getState() == 2 ? SystemClock.elapsedRealtime() + 1000 : -9223372036854775807L;
            this.codecReconfigured = false;
            this.codecReconfigurationState = 0;
            this.codecReceivedEos = false;
            this.codecReceivedBuffers = false;
            this.largestQueuedPresentationTimeUs = -9223372036854775807L;
            this.lastBufferInStreamPresentationTimeUs = -9223372036854775807L;
            this.codecDrainState = 0;
            this.codecDrainAction = 0;
            this.codecNeedsAdaptationWorkaroundBuffer = false;
            this.shouldSkipAdaptationWorkaroundOutputBuffer = false;
            this.isDecodeOnlyOutputBuffer = false;
            this.isLastOutputBuffer = false;
            this.waitingForFirstSyncSample = true;
            this.decoderCounters.decoderInitCount++;
            onCodecInitialized(str, elapsedRealtime2, elapsedRealtime2 - elapsedRealtime);
        } catch (Exception e3) {
            e = e3;
            mediaCodec = createByCodecName;
            if (mediaCodec != null) {
                resetCodecBuffers();
                mediaCodec.release();
            }
            throw e;
        }
    }

    private boolean shouldContinueFeeding(long j) {
        return this.renderTimeLimitMs == -9223372036854775807L || SystemClock.elapsedRealtime() - j < this.renderTimeLimitMs;
    }

    private void getCodecBuffers(MediaCodec mediaCodec) {
        if (Util.SDK_INT < 21) {
            this.inputBuffers = mediaCodec.getInputBuffers();
            this.outputBuffers = mediaCodec.getOutputBuffers();
        }
    }

    private void resetCodecBuffers() {
        if (Util.SDK_INT < 21) {
            this.inputBuffers = null;
            this.outputBuffers = null;
        }
    }

    private ByteBuffer getInputBuffer(int i) {
        if (Util.SDK_INT >= 21) {
            return this.codec.getInputBuffer(i);
        }
        return this.inputBuffers[i];
    }

    private ByteBuffer getOutputBuffer(int i) {
        if (Util.SDK_INT >= 21) {
            return this.codec.getOutputBuffer(i);
        }
        return this.outputBuffers[i];
    }

    private boolean hasOutputBuffer() {
        return this.outputIndex >= 0;
    }

    private void resetInputBuffer() {
        this.inputIndex = -1;
        this.buffer.data = null;
    }

    private void resetOutputBuffer() {
        this.outputIndex = -1;
        this.outputBuffer = null;
    }

    private void setSourceDrmSession(DrmSession<FrameworkMediaCrypto> drmSession) {
        DrmSession.CC.replaceSession(this.sourceDrmSession, drmSession);
        this.sourceDrmSession = drmSession;
    }

    private void setCodecDrmSession(DrmSession<FrameworkMediaCrypto> drmSession) {
        DrmSession.CC.replaceSession(this.codecDrmSession, drmSession);
        this.codecDrmSession = drmSession;
    }

    private boolean feedInputBuffer() throws ExoPlaybackException {
        int i;
        int i2;
        MediaCodec mediaCodec = this.codec;
        if (mediaCodec == null || this.codecDrainState == 2 || this.inputStreamEnded) {
            return false;
        }
        if (this.inputIndex < 0) {
            int dequeueInputBuffer = mediaCodec.dequeueInputBuffer(0L);
            this.inputIndex = dequeueInputBuffer;
            if (dequeueInputBuffer < 0) {
                return false;
            }
            this.buffer.data = getInputBuffer(dequeueInputBuffer);
            this.buffer.clear();
        }
        if (this.codecDrainState == 1) {
            if (!this.codecNeedsEosPropagation) {
                this.codecReceivedEos = true;
                this.codec.queueInputBuffer(this.inputIndex, 0, 0, 0L, 4);
                resetInputBuffer();
            }
            this.codecDrainState = 2;
            return false;
        } else if (this.codecNeedsAdaptationWorkaroundBuffer) {
            this.codecNeedsAdaptationWorkaroundBuffer = false;
            ByteBuffer byteBuffer = this.buffer.data;
            byte[] bArr = ADAPTATION_WORKAROUND_BUFFER;
            byteBuffer.put(bArr);
            this.codec.queueInputBuffer(this.inputIndex, 0, bArr.length, 0L, 0);
            resetInputBuffer();
            this.codecReceivedBuffers = true;
            return true;
        } else {
            FormatHolder formatHolder = getFormatHolder();
            if (this.waitingForKeys) {
                i2 = -4;
                i = 0;
            } else {
                if (this.codecReconfigurationState == 1) {
                    for (int i3 = 0; i3 < this.codecFormat.initializationData.size(); i3++) {
                        this.buffer.data.put(this.codecFormat.initializationData.get(i3));
                    }
                    this.codecReconfigurationState = 2;
                }
                i = this.buffer.data.position();
                i2 = readSource(formatHolder, this.buffer, false);
            }
            if (hasReadStreamToEnd()) {
                this.lastBufferInStreamPresentationTimeUs = this.largestQueuedPresentationTimeUs;
            }
            if (i2 == -3) {
                return false;
            }
            if (i2 == -5) {
                if (this.codecReconfigurationState == 2) {
                    this.buffer.clear();
                    this.codecReconfigurationState = 1;
                }
                onInputFormatChanged(formatHolder);
                return true;
            } else if (this.buffer.isEndOfStream()) {
                if (this.codecReconfigurationState == 2) {
                    this.buffer.clear();
                    this.codecReconfigurationState = 1;
                }
                this.inputStreamEnded = true;
                if (!this.codecReceivedBuffers) {
                    processEndOfStream();
                    return false;
                }
                try {
                    if (!this.codecNeedsEosPropagation) {
                        this.codecReceivedEos = true;
                        this.codec.queueInputBuffer(this.inputIndex, 0, 0, 0L, 4);
                        resetInputBuffer();
                    }
                    return false;
                } catch (MediaCodec.CryptoException e) {
                    throw createRendererException(e, this.inputFormat);
                }
            } else if (this.waitingForFirstSyncSample && !this.buffer.isKeyFrame()) {
                this.buffer.clear();
                if (this.codecReconfigurationState == 2) {
                    this.codecReconfigurationState = 1;
                }
                return true;
            } else {
                this.waitingForFirstSyncSample = false;
                boolean isEncrypted = this.buffer.isEncrypted();
                boolean shouldWaitForKeys = shouldWaitForKeys(isEncrypted);
                this.waitingForKeys = shouldWaitForKeys;
                if (shouldWaitForKeys) {
                    return false;
                }
                if (this.codecNeedsDiscardToSpsWorkaround && !isEncrypted) {
                    NalUnitUtil.discardToSps(this.buffer.data);
                    if (this.buffer.data.position() == 0) {
                        return true;
                    }
                    this.codecNeedsDiscardToSpsWorkaround = false;
                }
                try {
                    DecoderInputBuffer decoderInputBuffer = this.buffer;
                    long j = decoderInputBuffer.timeUs;
                    if (decoderInputBuffer.isDecodeOnly()) {
                        this.decodeOnlyPresentationTimestamps.add(Long.valueOf(j));
                    }
                    if (this.waitingForFirstSampleInFormat) {
                        this.formatQueue.add(j, this.inputFormat);
                        this.waitingForFirstSampleInFormat = false;
                    }
                    this.largestQueuedPresentationTimeUs = Math.max(this.largestQueuedPresentationTimeUs, j);
                    this.buffer.flip();
                    if (this.buffer.hasSupplementalData()) {
                        handleInputBufferSupplementalData(this.buffer);
                    }
                    onQueueInputBuffer(this.buffer);
                    if (isEncrypted) {
                        this.codec.queueSecureInputBuffer(this.inputIndex, 0, getFrameworkCryptoInfo(this.buffer, i), j, 0);
                    } else {
                        this.codec.queueInputBuffer(this.inputIndex, 0, this.buffer.data.limit(), j, 0);
                    }
                    resetInputBuffer();
                    this.codecReceivedBuffers = true;
                    this.codecReconfigurationState = 0;
                    this.decoderCounters.inputBufferCount++;
                    return true;
                } catch (MediaCodec.CryptoException e2) {
                    throw createRendererException(e2, this.inputFormat);
                }
            }
        }
    }

    private boolean shouldWaitForKeys(boolean z) throws ExoPlaybackException {
        DrmSession<FrameworkMediaCrypto> drmSession = this.codecDrmSession;
        if (drmSession == null || (!z && (this.playClearSamplesWithoutKeys || drmSession.playClearSamplesWithoutKeys()))) {
            return false;
        }
        int state = this.codecDrmSession.getState();
        if (state == 1) {
            throw createRendererException(this.codecDrmSession.getError(), this.inputFormat);
        }
        return state != 4;
    }

    /* JADX WARN: Code restructure failed: missing block: B:50:0x00a0, code lost:
        if (r1.height == r2.height) goto L52;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onInputFormatChanged(com.google.android.exoplayer2.FormatHolder r5) throws com.google.android.exoplayer2.ExoPlaybackException {
        /*
            r4 = this;
            r0 = 1
            r4.waitingForFirstSampleInFormat = r0
            com.google.android.exoplayer2.Format r1 = r5.format
            java.lang.Object r1 = com.google.android.exoplayer2.util.Assertions.checkNotNull(r1)
            com.google.android.exoplayer2.Format r1 = (com.google.android.exoplayer2.Format) r1
            boolean r2 = r5.includesDrmSession
            if (r2 == 0) goto L15
            com.google.android.exoplayer2.drm.DrmSession<?> r5 = r5.drmSession
            r4.setSourceDrmSession(r5)
            goto L21
        L15:
            com.google.android.exoplayer2.Format r5 = r4.inputFormat
            com.google.android.exoplayer2.drm.DrmSessionManager<com.google.android.exoplayer2.drm.FrameworkMediaCrypto> r2 = r4.drmSessionManager
            com.google.android.exoplayer2.drm.DrmSession<com.google.android.exoplayer2.drm.FrameworkMediaCrypto> r3 = r4.sourceDrmSession
            com.google.android.exoplayer2.drm.DrmSession r5 = r4.getUpdatedSourceDrmSession(r5, r1, r2, r3)
            r4.sourceDrmSession = r5
        L21:
            r4.inputFormat = r1
            android.media.MediaCodec r5 = r4.codec
            if (r5 != 0) goto L2b
            r4.maybeInitCodec()
            return
        L2b:
            com.google.android.exoplayer2.drm.DrmSession<com.google.android.exoplayer2.drm.FrameworkMediaCrypto> r5 = r4.sourceDrmSession
            if (r5 != 0) goto L33
            com.google.android.exoplayer2.drm.DrmSession<com.google.android.exoplayer2.drm.FrameworkMediaCrypto> r2 = r4.codecDrmSession
            if (r2 != 0) goto L55
        L33:
            if (r5 == 0) goto L39
            com.google.android.exoplayer2.drm.DrmSession<com.google.android.exoplayer2.drm.FrameworkMediaCrypto> r2 = r4.codecDrmSession
            if (r2 == 0) goto L55
        L39:
            com.google.android.exoplayer2.drm.DrmSession<com.google.android.exoplayer2.drm.FrameworkMediaCrypto> r2 = r4.codecDrmSession
            if (r5 == r2) goto L49
            com.google.android.exoplayer2.mediacodec.MediaCodecInfo r2 = r4.codecInfo
            boolean r2 = r2.secure
            if (r2 != 0) goto L49
            boolean r5 = maybeRequiresSecureDecoder(r5, r1)
            if (r5 != 0) goto L55
        L49:
            int r5 = com.google.android.exoplayer2.util.Util.SDK_INT
            r2 = 23
            if (r5 >= r2) goto L59
            com.google.android.exoplayer2.drm.DrmSession<com.google.android.exoplayer2.drm.FrameworkMediaCrypto> r5 = r4.sourceDrmSession
            com.google.android.exoplayer2.drm.DrmSession<com.google.android.exoplayer2.drm.FrameworkMediaCrypto> r2 = r4.codecDrmSession
            if (r5 == r2) goto L59
        L55:
            r4.drainAndReinitializeCodec()
            return
        L59:
            android.media.MediaCodec r5 = r4.codec
            com.google.android.exoplayer2.mediacodec.MediaCodecInfo r2 = r4.codecInfo
            com.google.android.exoplayer2.Format r3 = r4.codecFormat
            int r5 = r4.canKeepCodec(r5, r2, r3, r1)
            if (r5 == 0) goto Lc8
            if (r5 == r0) goto Lb5
            r2 = 2
            if (r5 == r2) goto L82
            r0 = 3
            if (r5 != r0) goto L7c
            r4.codecFormat = r1
            r4.updateCodecOperatingRate()
            com.google.android.exoplayer2.drm.DrmSession<com.google.android.exoplayer2.drm.FrameworkMediaCrypto> r5 = r4.sourceDrmSession
            com.google.android.exoplayer2.drm.DrmSession<com.google.android.exoplayer2.drm.FrameworkMediaCrypto> r0 = r4.codecDrmSession
            if (r5 == r0) goto Lcb
            r4.drainAndUpdateCodecDrmSession()
            goto Lcb
        L7c:
            java.lang.IllegalStateException r5 = new java.lang.IllegalStateException
            r5.<init>()
            throw r5
        L82:
            boolean r5 = r4.codecNeedsReconfigureWorkaround
            if (r5 == 0) goto L8a
            r4.drainAndReinitializeCodec()
            goto Lcb
        L8a:
            r4.codecReconfigured = r0
            r4.codecReconfigurationState = r0
            int r5 = r4.codecAdaptationWorkaroundMode
            if (r5 == r2) goto La4
            if (r5 != r0) goto La3
            int r5 = r1.width
            com.google.android.exoplayer2.Format r2 = r4.codecFormat
            int r3 = r2.width
            if (r5 != r3) goto La3
            int r5 = r1.height
            int r2 = r2.height
            if (r5 != r2) goto La3
            goto La4
        La3:
            r0 = 0
        La4:
            r4.codecNeedsAdaptationWorkaroundBuffer = r0
            r4.codecFormat = r1
            r4.updateCodecOperatingRate()
            com.google.android.exoplayer2.drm.DrmSession<com.google.android.exoplayer2.drm.FrameworkMediaCrypto> r5 = r4.sourceDrmSession
            com.google.android.exoplayer2.drm.DrmSession<com.google.android.exoplayer2.drm.FrameworkMediaCrypto> r0 = r4.codecDrmSession
            if (r5 == r0) goto Lcb
            r4.drainAndUpdateCodecDrmSession()
            goto Lcb
        Lb5:
            r4.codecFormat = r1
            r4.updateCodecOperatingRate()
            com.google.android.exoplayer2.drm.DrmSession<com.google.android.exoplayer2.drm.FrameworkMediaCrypto> r5 = r4.sourceDrmSession
            com.google.android.exoplayer2.drm.DrmSession<com.google.android.exoplayer2.drm.FrameworkMediaCrypto> r0 = r4.codecDrmSession
            if (r5 == r0) goto Lc4
            r4.drainAndUpdateCodecDrmSession()
            goto Lcb
        Lc4:
            r4.drainAndFlushCodec()
            goto Lcb
        Lc8:
            r4.drainAndReinitializeCodec()
        Lcb:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.mediacodec.MediaCodecRenderer.onInputFormatChanged(com.google.android.exoplayer2.FormatHolder):void");
    }

    @Override // com.google.android.exoplayer2.Renderer
    public boolean isEnded() {
        return this.outputStreamEnded;
    }

    @Override // com.google.android.exoplayer2.Renderer
    public boolean isReady() {
        return this.inputFormat != null && !this.waitingForKeys && (isSourceReady() || hasOutputBuffer() || (this.codecHotswapDeadlineMs != -9223372036854775807L && SystemClock.elapsedRealtime() < this.codecHotswapDeadlineMs));
    }

    private void updateCodecOperatingRate() throws ExoPlaybackException {
        if (Util.SDK_INT < 23) {
            return;
        }
        float codecOperatingRateV23 = getCodecOperatingRateV23(this.rendererOperatingRate, this.codecFormat, getStreamFormats());
        float f = this.codecOperatingRate;
        if (f == codecOperatingRateV23) {
            return;
        }
        if (codecOperatingRateV23 == -1.0f) {
            drainAndReinitializeCodec();
        } else if (f == -1.0f && codecOperatingRateV23 <= this.assumedMinimumCodecOperatingRate) {
        } else {
            Bundle bundle = new Bundle();
            bundle.putFloat("operating-rate", codecOperatingRateV23);
            this.codec.setParameters(bundle);
            this.codecOperatingRate = codecOperatingRateV23;
        }
    }

    private void drainAndFlushCodec() {
        if (this.codecReceivedBuffers) {
            this.codecDrainState = 1;
            this.codecDrainAction = 1;
        }
    }

    private void drainAndUpdateCodecDrmSession() throws ExoPlaybackException {
        if (Util.SDK_INT < 23) {
            drainAndReinitializeCodec();
        } else if (this.codecReceivedBuffers) {
            this.codecDrainState = 1;
            this.codecDrainAction = 2;
        } else {
            updateDrmSessionOrReinitializeCodecV23();
        }
    }

    private void drainAndReinitializeCodec() throws ExoPlaybackException {
        if (this.codecReceivedBuffers) {
            this.codecDrainState = 1;
            this.codecDrainAction = 3;
            return;
        }
        reinitializeCodec();
    }

    private boolean drainOutputBuffer(long j, long j2) throws ExoPlaybackException {
        boolean z;
        boolean z2;
        int i;
        if (!hasOutputBuffer()) {
            if (this.codecNeedsEosOutputExceptionWorkaround && this.codecReceivedEos) {
                try {
                    i = this.codec.dequeueOutputBuffer(this.outputBufferInfo, getDequeueOutputBufferTimeoutUs());
                } catch (IllegalStateException unused) {
                    processEndOfStream();
                    if (this.outputStreamEnded) {
                        releaseCodec();
                    }
                    return false;
                }
            } else {
                i = this.codec.dequeueOutputBuffer(this.outputBufferInfo, getDequeueOutputBufferTimeoutUs());
            }
            if (i < 0) {
                if (i == -2) {
                    processOutputFormat();
                    return true;
                } else if (i == -3) {
                    processOutputBuffersChanged();
                    return true;
                } else {
                    if (this.codecNeedsEosPropagation && (this.inputStreamEnded || this.codecDrainState == 2)) {
                        processEndOfStream();
                    }
                    return false;
                }
            } else if (this.shouldSkipAdaptationWorkaroundOutputBuffer) {
                this.shouldSkipAdaptationWorkaroundOutputBuffer = false;
                this.codec.releaseOutputBuffer(i, false);
                return true;
            } else {
                MediaCodec.BufferInfo bufferInfo = this.outputBufferInfo;
                if (bufferInfo.size == 0 && (bufferInfo.flags & 4) != 0) {
                    processEndOfStream();
                    return false;
                }
                this.outputIndex = i;
                ByteBuffer outputBuffer = getOutputBuffer(i);
                this.outputBuffer = outputBuffer;
                if (outputBuffer != null) {
                    outputBuffer.position(this.outputBufferInfo.offset);
                    ByteBuffer byteBuffer = this.outputBuffer;
                    MediaCodec.BufferInfo bufferInfo2 = this.outputBufferInfo;
                    byteBuffer.limit(bufferInfo2.offset + bufferInfo2.size);
                }
                this.isDecodeOnlyOutputBuffer = isDecodeOnlyBuffer(this.outputBufferInfo.presentationTimeUs);
                long j3 = this.lastBufferInStreamPresentationTimeUs;
                long j4 = this.outputBufferInfo.presentationTimeUs;
                this.isLastOutputBuffer = j3 == j4;
                updateOutputFormatForTime(j4);
            }
        }
        if (this.codecNeedsEosOutputExceptionWorkaround && this.codecReceivedEos) {
            try {
                MediaCodec mediaCodec = this.codec;
                ByteBuffer byteBuffer2 = this.outputBuffer;
                int i2 = this.outputIndex;
                MediaCodec.BufferInfo bufferInfo3 = this.outputBufferInfo;
                z = false;
                try {
                    z2 = processOutputBuffer(j, j2, mediaCodec, byteBuffer2, i2, bufferInfo3.flags, bufferInfo3.presentationTimeUs, this.isDecodeOnlyOutputBuffer, this.isLastOutputBuffer, this.outputFormat);
                } catch (IllegalStateException unused2) {
                    processEndOfStream();
                    if (this.outputStreamEnded) {
                        releaseCodec();
                    }
                    return z;
                }
            } catch (IllegalStateException unused3) {
                z = false;
            }
        } else {
            z = false;
            MediaCodec mediaCodec2 = this.codec;
            ByteBuffer byteBuffer3 = this.outputBuffer;
            int i3 = this.outputIndex;
            MediaCodec.BufferInfo bufferInfo4 = this.outputBufferInfo;
            z2 = processOutputBuffer(j, j2, mediaCodec2, byteBuffer3, i3, bufferInfo4.flags, bufferInfo4.presentationTimeUs, this.isDecodeOnlyOutputBuffer, this.isLastOutputBuffer, this.outputFormat);
        }
        if (z2) {
            onProcessedOutputBuffer(this.outputBufferInfo.presentationTimeUs);
            boolean z3 = (this.outputBufferInfo.flags & 4) != 0;
            resetOutputBuffer();
            if (!z3) {
                return true;
            }
            processEndOfStream();
        }
        return z;
    }

    private void processOutputFormat() throws ExoPlaybackException {
        this.codecHasOutputMediaFormat = true;
        MediaFormat outputFormat = this.codec.getOutputFormat();
        if (this.codecAdaptationWorkaroundMode != 0 && outputFormat.getInteger("width") == 32 && outputFormat.getInteger("height") == 32) {
            this.shouldSkipAdaptationWorkaroundOutputBuffer = true;
            return;
        }
        if (this.codecNeedsMonoChannelCountWorkaround) {
            outputFormat.setInteger("channel-count", 1);
        }
        onOutputFormatChanged(this.codec, outputFormat);
    }

    private void processOutputBuffersChanged() {
        if (Util.SDK_INT < 21) {
            this.outputBuffers = this.codec.getOutputBuffers();
        }
    }

    private void processEndOfStream() throws ExoPlaybackException {
        int i = this.codecDrainAction;
        if (i == 1) {
            flushOrReinitializeCodec();
        } else if (i == 2) {
            updateDrmSessionOrReinitializeCodecV23();
        } else if (i == 3) {
            reinitializeCodec();
        } else {
            this.outputStreamEnded = true;
            renderToEndOfStream();
        }
    }

    public final void setPendingOutputEndOfStream() {
        this.pendingOutputEndOfStream = true;
    }

    private void reinitializeCodec() throws ExoPlaybackException {
        releaseCodec();
        maybeInitCodec();
    }

    private boolean isDecodeOnlyBuffer(long j) {
        int size = this.decodeOnlyPresentationTimestamps.size();
        for (int i = 0; i < size; i++) {
            if (this.decodeOnlyPresentationTimestamps.get(i).longValue() == j) {
                this.decodeOnlyPresentationTimestamps.remove(i);
                return true;
            }
        }
        return false;
    }

    @TargetApi(R.styleable.MapAttrs_zOrderOnTop)
    private void updateDrmSessionOrReinitializeCodecV23() throws ExoPlaybackException {
        FrameworkMediaCrypto mediaCrypto = this.sourceDrmSession.getMediaCrypto();
        if (mediaCrypto == null) {
            reinitializeCodec();
        } else if (C.PLAYREADY_UUID.equals(mediaCrypto.uuid)) {
            reinitializeCodec();
        } else if (flushOrReinitializeCodec()) {
        } else {
            try {
                this.mediaCrypto.setMediaDrmSession(mediaCrypto.sessionId);
                setCodecDrmSession(this.sourceDrmSession);
                this.codecDrainState = 0;
                this.codecDrainAction = 0;
            } catch (MediaCryptoException e) {
                throw createRendererException(e, this.inputFormat);
            }
        }
    }

    private static boolean maybeRequiresSecureDecoder(DrmSession<FrameworkMediaCrypto> drmSession, Format format) {
        FrameworkMediaCrypto mediaCrypto = drmSession.getMediaCrypto();
        if (mediaCrypto == null) {
            return true;
        }
        if (mediaCrypto.forceAllowInsecureDecoderComponents) {
            return false;
        }
        try {
            MediaCrypto mediaCrypto2 = new MediaCrypto(mediaCrypto.uuid, mediaCrypto.sessionId);
            try {
                return mediaCrypto2.requiresSecureDecoderComponent(format.sampleMimeType);
            } finally {
                mediaCrypto2.release();
            }
        } catch (MediaCryptoException unused) {
            return true;
        }
    }

    private static MediaCodec.CryptoInfo getFrameworkCryptoInfo(DecoderInputBuffer decoderInputBuffer, int i) {
        MediaCodec.CryptoInfo frameworkCryptoInfo = decoderInputBuffer.cryptoInfo.getFrameworkCryptoInfo();
        if (i == 0) {
            return frameworkCryptoInfo;
        }
        if (frameworkCryptoInfo.numBytesOfClearData == null) {
            frameworkCryptoInfo.numBytesOfClearData = new int[1];
        }
        int[] iArr = frameworkCryptoInfo.numBytesOfClearData;
        iArr[0] = iArr[0] + i;
        return frameworkCryptoInfo;
    }

    private static boolean isMediaCodecException(IllegalStateException illegalStateException) {
        if (Util.SDK_INT < 21 || !isMediaCodecExceptionV21(illegalStateException)) {
            StackTraceElement[] stackTrace = illegalStateException.getStackTrace();
            return stackTrace.length > 0 && stackTrace[0].getClassName().equals("android.media.MediaCodec");
        }
        return true;
    }

    @TargetApi(R.styleable.MapAttrs_uiZoomGestures)
    private static boolean isMediaCodecExceptionV21(IllegalStateException illegalStateException) {
        return illegalStateException instanceof MediaCodec.CodecException;
    }

    private static boolean codecNeedsFlushWorkaround(String str) {
        int i = Util.SDK_INT;
        return i < 18 || (i == 18 && ("OMX.SEC.avc.dec".equals(str) || "OMX.SEC.avc.dec.secure".equals(str))) || (i == 19 && Util.MODEL.startsWith("SM-G800") && ("OMX.Exynos.avc.dec".equals(str) || "OMX.Exynos.avc.dec.secure".equals(str)));
    }

    private int codecAdaptationWorkaroundMode(String str) {
        int i = Util.SDK_INT;
        if (i <= 25 && "OMX.Exynos.avc.dec.secure".equals(str)) {
            String str2 = Util.MODEL;
            if (str2.startsWith("SM-T585") || str2.startsWith("SM-A510") || str2.startsWith("SM-A520") || str2.startsWith("SM-J700")) {
                return 2;
            }
        }
        if (i < 24) {
            if (!"OMX.Nvidia.h264.decode".equals(str) && !"OMX.Nvidia.h264.decode.secure".equals(str)) {
                return 0;
            }
            String str3 = Util.DEVICE;
            return ("flounder".equals(str3) || "flounder_lte".equals(str3) || "grouper".equals(str3) || "tilapia".equals(str3)) ? 1 : 0;
        }
        return 0;
    }

    private static boolean codecNeedsReconfigureWorkaround(String str) {
        return Util.MODEL.startsWith("SM-T230") && "OMX.MARVELL.VIDEO.HW.CODA7542DECODER".equals(str);
    }

    private static boolean codecNeedsDiscardToSpsWorkaround(String str, Format format) {
        return Util.SDK_INT < 21 && format.initializationData.isEmpty() && "OMX.MTK.VIDEO.DECODER.AVC".equals(str);
    }

    private static boolean codecNeedsEosPropagationWorkaround(MediaCodecInfo mediaCodecInfo) {
        String str = mediaCodecInfo.name;
        int i = Util.SDK_INT;
        return (i <= 25 && "OMX.rk.video_decoder.avc".equals(str)) || (i <= 17 && "OMX.allwinner.video.decoder.avc".equals(str)) || ("Amazon".equals(Util.MANUFACTURER) && "AFTS".equals(Util.MODEL) && mediaCodecInfo.secure);
    }

    private static boolean codecNeedsEosFlushWorkaround(String str) {
        int i = Util.SDK_INT;
        if (i > 23 || !"OMX.google.vorbis.decoder".equals(str)) {
            if (i <= 19) {
                String str2 = Util.DEVICE;
                if (("hb2000".equals(str2) || "stvm8".equals(str2)) && ("OMX.amlogic.avc.decoder.awesome".equals(str) || "OMX.amlogic.avc.decoder.awesome.secure".equals(str))) {
                }
            }
            return false;
        }
        return true;
    }

    private static boolean codecNeedsEosOutputExceptionWorkaround(String str) {
        return Util.SDK_INT == 21 && "OMX.google.aac.decoder".equals(str);
    }

    private static boolean codecNeedsMonoChannelCountWorkaround(String str, Format format) {
        return Util.SDK_INT <= 18 && format.channelCount == 1 && "OMX.MTK.AUDIO.DECODER.MP3".equals(str);
    }

    private static boolean codecNeedsSosFlushWorkaround(String str) {
        return Util.SDK_INT == 29 && "c2.android.aac.decoder".equals(str);
    }
}
