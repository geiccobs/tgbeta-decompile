package com.google.android.exoplayer2.mediacodec;

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
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes3.dex */
public abstract class MediaCodecRenderer extends BaseRenderer {
    private static final byte[] ADAPTATION_WORKAROUND_BUFFER = {0, 0, 1, 103, 66, -64, 11, -38, 37, -112, 0, 0, 1, 104, -50, 15, 19, 32, 0, 0, 1, 101, -120, -124, 13, -50, 113, 24, -96, 0, 47, -65, 28, 49, -61, 39, 93, 120};
    private static final int ADAPTATION_WORKAROUND_MODE_ALWAYS = 2;
    private static final int ADAPTATION_WORKAROUND_MODE_NEVER = 0;
    private static final int ADAPTATION_WORKAROUND_MODE_SAME_RESOLUTION = 1;
    private static final int ADAPTATION_WORKAROUND_SLICE_WIDTH_HEIGHT = 32;
    protected static final float CODEC_OPERATING_RATE_UNSET = -1.0f;
    private static final int DRAIN_ACTION_FLUSH = 1;
    private static final int DRAIN_ACTION_NONE = 0;
    private static final int DRAIN_ACTION_REINITIALIZE = 3;
    private static final int DRAIN_ACTION_UPDATE_DRM_SESSION = 2;
    private static final int DRAIN_STATE_NONE = 0;
    private static final int DRAIN_STATE_SIGNAL_END_OF_STREAM = 1;
    private static final int DRAIN_STATE_WAIT_END_OF_STREAM = 2;
    protected static final int KEEP_CODEC_RESULT_NO = 0;
    protected static final int KEEP_CODEC_RESULT_YES_WITHOUT_RECONFIGURATION = 3;
    protected static final int KEEP_CODEC_RESULT_YES_WITH_FLUSH = 1;
    protected static final int KEEP_CODEC_RESULT_YES_WITH_RECONFIGURATION = 2;
    private static final long MAX_CODEC_HOTSWAP_TIME_MS = 1000;
    private static final int RECONFIGURATION_STATE_NONE = 0;
    private static final int RECONFIGURATION_STATE_QUEUE_PENDING = 2;
    private static final int RECONFIGURATION_STATE_WRITE_PENDING = 1;
    private static final String TAG = "MediaCodecRenderer";
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
    private long renderTimeLimitMs = C.TIME_UNSET;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    private @interface AdaptationWorkaroundMode {
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    private @interface DrainAction {
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    private @interface DrainState {
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    protected @interface KeepCodecResult {
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    private @interface ReconfigurationState {
    }

    protected abstract void configureCodec(MediaCodecInfo mediaCodecInfo, MediaCodec mediaCodec, Format format, MediaCrypto mediaCrypto, float f);

    protected abstract List<MediaCodecInfo> getDecoderInfos(MediaCodecSelector mediaCodecSelector, Format format, boolean z) throws MediaCodecUtil.DecoderQueryException;

    protected abstract boolean processOutputBuffer(long j, long j2, MediaCodec mediaCodec, ByteBuffer byteBuffer, int i, int i2, long j3, boolean z, boolean z2, Format format) throws ExoPlaybackException;

    protected abstract int supportsFormat(MediaCodecSelector mediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, Format format) throws MediaCodecUtil.DecoderQueryException;

    /* loaded from: classes3.dex */
    public static class DecoderInitializationException extends Exception {
        private static final int CUSTOM_ERROR_CODE_BASE = -50000;
        private static final int DECODER_QUERY_ERROR = -49998;
        private static final int NO_SUITABLE_DECODER_ERROR = -49999;
        public final MediaCodecInfo codecInfo;
        public final String diagnosticInfo;
        public final DecoderInitializationException fallbackDecoderInitializationException;
        public final String mimeType;
        public final boolean secureDecoderRequired;

        public DecoderInitializationException(Format format, Throwable cause, boolean secureDecoderRequired, int errorCode) {
            this("Decoder init failed: [" + errorCode + "], " + format, cause, format.sampleMimeType, secureDecoderRequired, null, buildCustomDiagnosticInfo(errorCode), null);
        }

        public DecoderInitializationException(Format format, Throwable cause, boolean secureDecoderRequired, MediaCodecInfo mediaCodecInfo) {
            this("Decoder init failed: " + mediaCodecInfo.name + ", " + format, cause, format.sampleMimeType, secureDecoderRequired, mediaCodecInfo, Util.SDK_INT >= 21 ? getDiagnosticInfoV21(cause) : null, null);
        }

        private DecoderInitializationException(String message, Throwable cause, String mimeType, boolean secureDecoderRequired, MediaCodecInfo mediaCodecInfo, String diagnosticInfo, DecoderInitializationException fallbackDecoderInitializationException) {
            super(message, cause);
            this.mimeType = mimeType;
            this.secureDecoderRequired = secureDecoderRequired;
            this.codecInfo = mediaCodecInfo;
            this.diagnosticInfo = diagnosticInfo;
            this.fallbackDecoderInitializationException = fallbackDecoderInitializationException;
        }

        public DecoderInitializationException copyWithFallbackException(DecoderInitializationException fallbackException) {
            return new DecoderInitializationException(getMessage(), getCause(), this.mimeType, this.secureDecoderRequired, this.codecInfo, this.diagnosticInfo, fallbackException);
        }

        private static String getDiagnosticInfoV21(Throwable cause) {
            if (cause instanceof MediaCodec.CodecException) {
                return ((MediaCodec.CodecException) cause).getDiagnosticInfo();
            }
            return null;
        }

        private static String buildCustomDiagnosticInfo(int errorCode) {
            String sign = errorCode < 0 ? "neg_" : "";
            return "com.google.android.exoplayer2.mediacodec.MediaCodecRenderer_" + sign + Math.abs(errorCode);
        }
    }

    /* loaded from: classes3.dex */
    public static class DecoderException extends Exception {
        public final MediaCodecInfo codecInfo;
        public final String diagnosticInfo;

        /* JADX WARN: Illegal instructions before constructor call */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public DecoderException(java.lang.Throwable r4, com.google.android.exoplayer2.mediacodec.MediaCodecInfo r5) {
            /*
                r3 = this;
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r1 = "Decoder failed: "
                r0.append(r1)
                r1 = 0
                if (r5 != 0) goto Lf
                r2 = r1
                goto L11
            Lf:
                java.lang.String r2 = r5.name
            L11:
                r0.append(r2)
                java.lang.String r0 = r0.toString()
                r3.<init>(r0, r4)
                r3.codecInfo = r5
                int r0 = com.google.android.exoplayer2.util.Util.SDK_INT
                r2 = 21
                if (r0 < r2) goto L27
                java.lang.String r1 = getDiagnosticInfoV21(r4)
            L27:
                r3.diagnosticInfo = r1
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.mediacodec.MediaCodecRenderer.DecoderException.<init>(java.lang.Throwable, com.google.android.exoplayer2.mediacodec.MediaCodecInfo):void");
        }

        private static String getDiagnosticInfoV21(Throwable cause) {
            if (cause instanceof MediaCodec.CodecException) {
                return ((MediaCodec.CodecException) cause).getDiagnosticInfo();
            }
            return null;
        }
    }

    public MediaCodecRenderer(int trackType, MediaCodecSelector mediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, boolean playClearSamplesWithoutKeys, boolean enableDecoderFallback, float assumedMinimumCodecOperatingRate) {
        super(trackType);
        this.mediaCodecSelector = (MediaCodecSelector) Assertions.checkNotNull(mediaCodecSelector);
        this.drmSessionManager = drmSessionManager;
        this.playClearSamplesWithoutKeys = playClearSamplesWithoutKeys;
        this.enableDecoderFallback = enableDecoderFallback;
        this.assumedMinimumCodecOperatingRate = assumedMinimumCodecOperatingRate;
    }

    public void experimental_setRenderTimeLimitMs(long renderTimeLimitMs) {
        this.renderTimeLimitMs = renderTimeLimitMs;
    }

    public void experimental_setSkipMediaCodecStopOnRelease(boolean enabled) {
        this.skipMediaCodecStopOnRelease = enabled;
    }

    @Override // com.google.android.exoplayer2.BaseRenderer, com.google.android.exoplayer2.RendererCapabilities
    public final int supportsMixedMimeTypeAdaptation() {
        return 8;
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
        String mimeType = this.inputFormat.sampleMimeType;
        DrmSession<FrameworkMediaCrypto> drmSession = this.codecDrmSession;
        if (drmSession != null) {
            if (this.mediaCrypto == null) {
                FrameworkMediaCrypto sessionMediaCrypto = drmSession.getMediaCrypto();
                if (sessionMediaCrypto == null) {
                    DrmSession.DrmSessionException drmError = this.codecDrmSession.getError();
                    if (drmError == null) {
                        return;
                    }
                } else {
                    try {
                        this.mediaCrypto = new MediaCrypto(sessionMediaCrypto.uuid, sessionMediaCrypto.sessionId);
                        this.mediaCryptoRequiresSecureDecoder = !sessionMediaCrypto.forceAllowInsecureDecoderComponents && this.mediaCrypto.requiresSecureDecoderComponent(mimeType);
                    } catch (MediaCryptoException e) {
                        throw createRendererException(e, this.inputFormat);
                    }
                }
            }
            if (FrameworkMediaCrypto.WORKAROUND_DEVICE_NEEDS_KEYS_TO_CONFIGURE_CODEC) {
                int drmSessionState = this.codecDrmSession.getState();
                if (drmSessionState == 1) {
                    throw createRendererException(this.codecDrmSession.getError(), this.inputFormat);
                }
                if (drmSessionState != 4) {
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

    protected boolean shouldInitCodec(MediaCodecInfo codecInfo) {
        return true;
    }

    protected boolean getCodecNeedsEosPropagation() {
        return false;
    }

    public final Format updateOutputFormatForTime(long presentationTimeUs) {
        Format format = this.formatQueue.pollFloor(presentationTimeUs);
        if (format != null) {
            this.outputFormat = format;
        }
        return format;
    }

    public final MediaCodec getCodec() {
        return this.codec;
    }

    public final MediaCodecInfo getCodecInfo() {
        return this.codecInfo;
    }

    @Override // com.google.android.exoplayer2.BaseRenderer
    public void onEnabled(boolean joining) throws ExoPlaybackException {
        DrmSessionManager<FrameworkMediaCrypto> drmSessionManager = this.drmSessionManager;
        if (drmSessionManager != null && !this.drmResourcesAcquired) {
            this.drmResourcesAcquired = true;
            drmSessionManager.prepare();
        }
        this.decoderCounters = new DecoderCounters();
    }

    @Override // com.google.android.exoplayer2.BaseRenderer
    public void onPositionReset(long positionUs, boolean joining) throws ExoPlaybackException {
        this.inputStreamEnded = false;
        this.outputStreamEnded = false;
        this.pendingOutputEndOfStream = false;
        flushOrReinitializeCodec();
        this.formatQueue.clear();
    }

    @Override // com.google.android.exoplayer2.BaseRenderer, com.google.android.exoplayer2.Renderer
    public final void setOperatingRate(float operatingRate) throws ExoPlaybackException {
        this.rendererOperatingRate = operatingRate;
        if (this.codec != null && this.codecDrainAction != 3 && getState() != 0) {
            updateCodecOperatingRate();
        }
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
            if (drmSessionManager != null && this.drmResourcesAcquired) {
                this.drmResourcesAcquired = false;
                drmSessionManager.release();
            }
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
        this.codecHotswapDeadlineMs = C.TIME_UNSET;
        this.decodeOnlyPresentationTimestamps.clear();
        this.largestQueuedPresentationTimeUs = C.TIME_UNSET;
        this.lastBufferInStreamPresentationTimeUs = C.TIME_UNSET;
        try {
            if (this.codec != null) {
                this.decoderCounters.decoderReleaseCount++;
                if (!this.skipMediaCodecStopOnRelease) {
                    this.codec.stop();
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

    @Override // com.google.android.exoplayer2.BaseRenderer
    public void onStarted() {
    }

    @Override // com.google.android.exoplayer2.BaseRenderer
    public void onStopped() {
    }

    @Override // com.google.android.exoplayer2.Renderer
    public void render(long positionUs, long elapsedRealtimeUs) throws ExoPlaybackException {
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
                    long drainStartTimeMs = SystemClock.elapsedRealtime();
                    TraceUtil.beginSection("drainAndFeed");
                    while (drainOutputBuffer(positionUs, elapsedRealtimeUs)) {
                    }
                    while (feedInputBuffer() && shouldContinueFeeding(drainStartTimeMs)) {
                    }
                    TraceUtil.endSection();
                } else {
                    this.decoderCounters.skippedInputBufferCount += skipSource(positionUs);
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
        boolean released = flushOrReleaseCodec();
        if (released) {
            maybeInitCodec();
        }
        return released;
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
        this.codecHotswapDeadlineMs = C.TIME_UNSET;
        this.codecReceivedEos = false;
        this.codecReceivedBuffers = false;
        this.waitingForFirstSyncSample = true;
        this.codecNeedsAdaptationWorkaroundBuffer = false;
        this.shouldSkipAdaptationWorkaroundOutputBuffer = false;
        this.isDecodeOnlyOutputBuffer = false;
        this.isLastOutputBuffer = false;
        this.waitingForKeys = false;
        this.decodeOnlyPresentationTimestamps.clear();
        this.largestQueuedPresentationTimeUs = C.TIME_UNSET;
        this.lastBufferInStreamPresentationTimeUs = C.TIME_UNSET;
        this.codecDrainState = 0;
        this.codecDrainAction = 0;
        this.codecReconfigurationState = this.codecReconfigured ? 1 : 0;
        return false;
    }

    protected DecoderException createDecoderException(Throwable cause, MediaCodecInfo codecInfo) {
        return new DecoderException(cause, codecInfo);
    }

    private boolean readToFlagsOnlyBuffer(boolean requireFormat) throws ExoPlaybackException {
        FormatHolder formatHolder = getFormatHolder();
        this.flagsOnlyBuffer.clear();
        int result = readSource(formatHolder, this.flagsOnlyBuffer, requireFormat);
        if (result == -5) {
            onInputFormatChanged(formatHolder);
            return true;
        } else if (result == -4 && this.flagsOnlyBuffer.isEndOfStream()) {
            this.inputStreamEnded = true;
            processEndOfStream();
            return false;
        } else {
            return false;
        }
    }

    private void maybeInitCodecWithFallback(MediaCrypto crypto, boolean mediaCryptoRequiresSecureDecoder) throws DecoderInitializationException {
        if (this.availableCodecInfos == null) {
            try {
                List<MediaCodecInfo> allAvailableCodecInfos = getAvailableCodecInfos(mediaCryptoRequiresSecureDecoder);
                ArrayDeque<MediaCodecInfo> arrayDeque = new ArrayDeque<>();
                this.availableCodecInfos = arrayDeque;
                if (this.enableDecoderFallback) {
                    arrayDeque.addAll(allAvailableCodecInfos);
                } else if (!allAvailableCodecInfos.isEmpty()) {
                    this.availableCodecInfos.add(allAvailableCodecInfos.get(0));
                }
                this.preferredDecoderInitializationException = null;
            } catch (MediaCodecUtil.DecoderQueryException e) {
                throw new DecoderInitializationException(this.inputFormat, e, mediaCryptoRequiresSecureDecoder, -49998);
            }
        }
        if (this.availableCodecInfos.isEmpty()) {
            throw new DecoderInitializationException(this.inputFormat, (Throwable) null, mediaCryptoRequiresSecureDecoder, -49999);
        }
        while (this.codec == null) {
            MediaCodecInfo codecInfo = this.availableCodecInfos.peekFirst();
            if (!shouldInitCodec(codecInfo)) {
                return;
            }
            try {
                initCodec(codecInfo, crypto);
            } catch (Exception e2) {
                Log.w(TAG, "Failed to initialize decoder: " + codecInfo, e2);
                this.availableCodecInfos.removeFirst();
                DecoderInitializationException exception = new DecoderInitializationException(this.inputFormat, e2, mediaCryptoRequiresSecureDecoder, codecInfo);
                if (this.preferredDecoderInitializationException != null) {
                    this.preferredDecoderInitializationException = this.preferredDecoderInitializationException.copyWithFallbackException(exception);
                } else {
                    this.preferredDecoderInitializationException = exception;
                }
                if (this.availableCodecInfos.isEmpty()) {
                    throw this.preferredDecoderInitializationException;
                }
            }
        }
        this.availableCodecInfos = null;
    }

    private List<MediaCodecInfo> getAvailableCodecInfos(boolean mediaCryptoRequiresSecureDecoder) throws MediaCodecUtil.DecoderQueryException {
        List<MediaCodecInfo> codecInfos = getDecoderInfos(this.mediaCodecSelector, this.inputFormat, mediaCryptoRequiresSecureDecoder);
        if (codecInfos.isEmpty() && mediaCryptoRequiresSecureDecoder) {
            codecInfos = getDecoderInfos(this.mediaCodecSelector, this.inputFormat, false);
            if (!codecInfos.isEmpty()) {
                Log.w(TAG, "Drm session requires secure decoder for " + this.inputFormat.sampleMimeType + ", but no secure decoder available. Trying to proceed with " + codecInfos + ".");
            }
        }
        return codecInfos;
    }

    private void initCodec(MediaCodecInfo codecInfo, MediaCrypto crypto) throws Exception {
        float codecOperatingRate;
        float codecOperatingRate2;
        Exception e;
        long codecInitializingTimestamp;
        MediaCodec codec;
        long j;
        MediaCodec codec2 = null;
        String codecName = codecInfo.name;
        if (Util.SDK_INT >= 23) {
            codecOperatingRate = getCodecOperatingRateV23(this.rendererOperatingRate, this.inputFormat, getStreamFormats());
        } else {
            codecOperatingRate = -1.0f;
        }
        if (codecOperatingRate > this.assumedMinimumCodecOperatingRate) {
            codecOperatingRate2 = codecOperatingRate;
        } else {
            codecOperatingRate2 = -1.0f;
        }
        try {
            codecInitializingTimestamp = SystemClock.elapsedRealtime();
            TraceUtil.beginSection("createCodec:" + codecName);
            codec = MediaCodec.createByCodecName(codecName);
        } catch (Exception e2) {
            e = e2;
        }
        try {
            TraceUtil.endSection();
            TraceUtil.beginSection("configureCodec");
            configureCodec(codecInfo, codec, this.inputFormat, crypto, codecOperatingRate2);
            TraceUtil.endSection();
            TraceUtil.beginSection("startCodec");
            codec.start();
            TraceUtil.endSection();
            long codecInitializedTimestamp = SystemClock.elapsedRealtime();
            getCodecBuffers(codec);
            this.codec = codec;
            this.codecInfo = codecInfo;
            this.codecOperatingRate = codecOperatingRate2;
            this.codecFormat = this.inputFormat;
            this.codecAdaptationWorkaroundMode = codecAdaptationWorkaroundMode(codecName);
            this.codecNeedsReconfigureWorkaround = codecNeedsReconfigureWorkaround(codecName);
            this.codecNeedsDiscardToSpsWorkaround = codecNeedsDiscardToSpsWorkaround(codecName, this.codecFormat);
            this.codecNeedsFlushWorkaround = codecNeedsFlushWorkaround(codecName);
            this.codecNeedsSosFlushWorkaround = codecNeedsSosFlushWorkaround(codecName);
            this.codecNeedsEosFlushWorkaround = codecNeedsEosFlushWorkaround(codecName);
            this.codecNeedsEosOutputExceptionWorkaround = codecNeedsEosOutputExceptionWorkaround(codecName);
            this.codecNeedsMonoChannelCountWorkaround = codecNeedsMonoChannelCountWorkaround(codecName, this.codecFormat);
            this.codecNeedsEosPropagation = codecNeedsEosPropagationWorkaround(codecInfo) || getCodecNeedsEosPropagation();
            resetInputBuffer();
            resetOutputBuffer();
            if (getState() == 2) {
                j = SystemClock.elapsedRealtime() + 1000;
            } else {
                j = C.TIME_UNSET;
            }
            this.codecHotswapDeadlineMs = j;
            this.codecReconfigured = false;
            this.codecReconfigurationState = 0;
            this.codecReceivedEos = false;
            this.codecReceivedBuffers = false;
            this.largestQueuedPresentationTimeUs = C.TIME_UNSET;
            this.lastBufferInStreamPresentationTimeUs = C.TIME_UNSET;
            this.codecDrainState = 0;
            this.codecDrainAction = 0;
            this.codecNeedsAdaptationWorkaroundBuffer = false;
            this.shouldSkipAdaptationWorkaroundOutputBuffer = false;
            this.isDecodeOnlyOutputBuffer = false;
            this.isLastOutputBuffer = false;
            this.waitingForFirstSyncSample = true;
            this.decoderCounters.decoderInitCount++;
            long elapsed = codecInitializedTimestamp - codecInitializingTimestamp;
            onCodecInitialized(codecName, codecInitializedTimestamp, elapsed);
        } catch (Exception e3) {
            e = e3;
            codec2 = codec;
            if (codec2 != null) {
                resetCodecBuffers();
                codec2.release();
            }
            throw e;
        }
    }

    private boolean shouldContinueFeeding(long drainStartTimeMs) {
        return this.renderTimeLimitMs == C.TIME_UNSET || SystemClock.elapsedRealtime() - drainStartTimeMs < this.renderTimeLimitMs;
    }

    private void getCodecBuffers(MediaCodec codec) {
        if (Util.SDK_INT < 21) {
            this.inputBuffers = codec.getInputBuffers();
            this.outputBuffers = codec.getOutputBuffers();
        }
    }

    private void resetCodecBuffers() {
        if (Util.SDK_INT < 21) {
            this.inputBuffers = null;
            this.outputBuffers = null;
        }
    }

    private ByteBuffer getInputBuffer(int inputIndex) {
        if (Util.SDK_INT >= 21) {
            return this.codec.getInputBuffer(inputIndex);
        }
        return this.inputBuffers[inputIndex];
    }

    private ByteBuffer getOutputBuffer(int outputIndex) {
        if (Util.SDK_INT >= 21) {
            return this.codec.getOutputBuffer(outputIndex);
        }
        return this.outputBuffers[outputIndex];
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

    private void setSourceDrmSession(DrmSession<FrameworkMediaCrypto> session) {
        DrmSession.CC.replaceSession(this.sourceDrmSession, session);
        this.sourceDrmSession = session;
    }

    private void setCodecDrmSession(DrmSession<FrameworkMediaCrypto> session) {
        DrmSession.CC.replaceSession(this.codecDrmSession, session);
        this.codecDrmSession = session;
    }

    private boolean feedInputBuffer() throws ExoPlaybackException {
        int result;
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
            int adaptiveReconfigurationBytes = 0;
            if (this.waitingForKeys) {
                result = -4;
            } else {
                if (this.codecReconfigurationState == 1) {
                    for (int i = 0; i < this.codecFormat.initializationData.size(); i++) {
                        byte[] data = this.codecFormat.initializationData.get(i);
                        this.buffer.data.put(data);
                    }
                    this.codecReconfigurationState = 2;
                }
                adaptiveReconfigurationBytes = this.buffer.data.position();
                result = readSource(formatHolder, this.buffer, false);
            }
            if (hasReadStreamToEnd()) {
                this.lastBufferInStreamPresentationTimeUs = this.largestQueuedPresentationTimeUs;
            }
            if (result == -3) {
                return false;
            }
            if (result == -5) {
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
                boolean bufferEncrypted = this.buffer.isEncrypted();
                boolean shouldWaitForKeys = shouldWaitForKeys(bufferEncrypted);
                this.waitingForKeys = shouldWaitForKeys;
                if (shouldWaitForKeys) {
                    return false;
                }
                if (this.codecNeedsDiscardToSpsWorkaround && !bufferEncrypted) {
                    NalUnitUtil.discardToSps(this.buffer.data);
                    if (this.buffer.data.position() == 0) {
                        return true;
                    }
                    this.codecNeedsDiscardToSpsWorkaround = false;
                }
                try {
                    long presentationTimeUs = this.buffer.timeUs;
                    if (this.buffer.isDecodeOnly()) {
                        this.decodeOnlyPresentationTimestamps.add(Long.valueOf(presentationTimeUs));
                    }
                    if (this.waitingForFirstSampleInFormat) {
                        this.formatQueue.add(presentationTimeUs, this.inputFormat);
                        this.waitingForFirstSampleInFormat = false;
                    }
                    this.largestQueuedPresentationTimeUs = Math.max(this.largestQueuedPresentationTimeUs, presentationTimeUs);
                    this.buffer.flip();
                    if (this.buffer.hasSupplementalData()) {
                        handleInputBufferSupplementalData(this.buffer);
                    }
                    onQueueInputBuffer(this.buffer);
                    if (bufferEncrypted) {
                        MediaCodec.CryptoInfo cryptoInfo = getFrameworkCryptoInfo(this.buffer, adaptiveReconfigurationBytes);
                        this.codec.queueSecureInputBuffer(this.inputIndex, 0, cryptoInfo, presentationTimeUs, 0);
                    } else {
                        this.codec.queueInputBuffer(this.inputIndex, 0, this.buffer.data.limit(), presentationTimeUs, 0);
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

    private boolean shouldWaitForKeys(boolean bufferEncrypted) throws ExoPlaybackException {
        DrmSession<FrameworkMediaCrypto> drmSession = this.codecDrmSession;
        if (drmSession == null || (!bufferEncrypted && (this.playClearSamplesWithoutKeys || drmSession.playClearSamplesWithoutKeys()))) {
            return false;
        }
        int drmSessionState = this.codecDrmSession.getState();
        if (drmSessionState == 1) {
            throw createRendererException(this.codecDrmSession.getError(), this.inputFormat);
        }
        return drmSessionState != 4;
    }

    protected void onCodecInitialized(String name, long initializedTimestampMs, long initializationDurationMs) {
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void onInputFormatChanged(FormatHolder formatHolder) throws ExoPlaybackException {
        boolean z = true;
        this.waitingForFirstSampleInFormat = true;
        Format newFormat = (Format) Assertions.checkNotNull(formatHolder.format);
        if (formatHolder.includesDrmSession) {
            setSourceDrmSession(formatHolder.drmSession);
        } else {
            this.sourceDrmSession = getUpdatedSourceDrmSession(this.inputFormat, newFormat, this.drmSessionManager, this.sourceDrmSession);
        }
        this.inputFormat = newFormat;
        if (this.codec == null) {
            maybeInitCodec();
            return;
        }
        DrmSession<FrameworkMediaCrypto> drmSession = this.sourceDrmSession;
        if ((drmSession == null && this.codecDrmSession != null) || ((drmSession != null && this.codecDrmSession == null) || ((drmSession != this.codecDrmSession && !this.codecInfo.secure && maybeRequiresSecureDecoder(this.sourceDrmSession, newFormat)) || (Util.SDK_INT < 23 && this.sourceDrmSession != this.codecDrmSession)))) {
            drainAndReinitializeCodec();
            return;
        }
        switch (canKeepCodec(this.codec, this.codecInfo, this.codecFormat, newFormat)) {
            case 0:
                drainAndReinitializeCodec();
                return;
            case 1:
                this.codecFormat = newFormat;
                updateCodecOperatingRate();
                if (this.sourceDrmSession != this.codecDrmSession) {
                    drainAndUpdateCodecDrmSession();
                    return;
                } else {
                    drainAndFlushCodec();
                    return;
                }
            case 2:
                if (this.codecNeedsReconfigureWorkaround) {
                    drainAndReinitializeCodec();
                    return;
                }
                this.codecReconfigured = true;
                this.codecReconfigurationState = 1;
                int i = this.codecAdaptationWorkaroundMode;
                if (i != 2 && (i != 1 || newFormat.width != this.codecFormat.width || newFormat.height != this.codecFormat.height)) {
                    z = false;
                }
                this.codecNeedsAdaptationWorkaroundBuffer = z;
                this.codecFormat = newFormat;
                updateCodecOperatingRate();
                if (this.sourceDrmSession != this.codecDrmSession) {
                    drainAndUpdateCodecDrmSession();
                    return;
                }
                return;
            case 3:
                this.codecFormat = newFormat;
                updateCodecOperatingRate();
                if (this.sourceDrmSession != this.codecDrmSession) {
                    drainAndUpdateCodecDrmSession();
                    return;
                }
                return;
            default:
                throw new IllegalStateException();
        }
    }

    protected void onOutputFormatChanged(MediaCodec codec, MediaFormat outputMediaFormat) throws ExoPlaybackException {
    }

    protected void handleInputBufferSupplementalData(DecoderInputBuffer buffer) throws ExoPlaybackException {
    }

    protected void onQueueInputBuffer(DecoderInputBuffer buffer) {
    }

    protected void onProcessedOutputBuffer(long presentationTimeUs) {
    }

    protected int canKeepCodec(MediaCodec codec, MediaCodecInfo codecInfo, Format oldFormat, Format newFormat) {
        return 0;
    }

    @Override // com.google.android.exoplayer2.Renderer
    public boolean isEnded() {
        return this.outputStreamEnded;
    }

    @Override // com.google.android.exoplayer2.Renderer
    public boolean isReady() {
        return this.inputFormat != null && !this.waitingForKeys && (isSourceReady() || hasOutputBuffer() || (this.codecHotswapDeadlineMs != C.TIME_UNSET && SystemClock.elapsedRealtime() < this.codecHotswapDeadlineMs));
    }

    protected long getDequeueOutputBufferTimeoutUs() {
        return 0L;
    }

    protected float getCodecOperatingRateV23(float operatingRate, Format format, Format[] streamFormats) {
        return -1.0f;
    }

    private void updateCodecOperatingRate() throws ExoPlaybackException {
        if (Util.SDK_INT < 23) {
            return;
        }
        float newCodecOperatingRate = getCodecOperatingRateV23(this.rendererOperatingRate, this.codecFormat, getStreamFormats());
        float f = this.codecOperatingRate;
        if (f != newCodecOperatingRate) {
            if (newCodecOperatingRate == -1.0f) {
                drainAndReinitializeCodec();
            } else if (f != -1.0f || newCodecOperatingRate > this.assumedMinimumCodecOperatingRate) {
                Bundle codecParameters = new Bundle();
                codecParameters.putFloat("operating-rate", newCodecOperatingRate);
                this.codec.setParameters(codecParameters);
                this.codecOperatingRate = newCodecOperatingRate;
            }
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

    private boolean drainOutputBuffer(long positionUs, long elapsedRealtimeUs) throws ExoPlaybackException {
        boolean z;
        boolean processedOutputBuffer;
        int outputIndex;
        if (!hasOutputBuffer()) {
            if (this.codecNeedsEosOutputExceptionWorkaround && this.codecReceivedEos) {
                try {
                    outputIndex = this.codec.dequeueOutputBuffer(this.outputBufferInfo, getDequeueOutputBufferTimeoutUs());
                } catch (IllegalStateException e) {
                    processEndOfStream();
                    if (this.outputStreamEnded) {
                        releaseCodec();
                    }
                    return false;
                }
            } else {
                outputIndex = this.codec.dequeueOutputBuffer(this.outputBufferInfo, getDequeueOutputBufferTimeoutUs());
            }
            if (outputIndex < 0) {
                if (outputIndex == -2) {
                    processOutputFormat();
                    return true;
                } else if (outputIndex == -3) {
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
                this.codec.releaseOutputBuffer(outputIndex, false);
                return true;
            } else if (this.outputBufferInfo.size == 0 && (this.outputBufferInfo.flags & 4) != 0) {
                processEndOfStream();
                return false;
            } else {
                this.outputIndex = outputIndex;
                ByteBuffer outputBuffer = getOutputBuffer(outputIndex);
                this.outputBuffer = outputBuffer;
                if (outputBuffer != null) {
                    outputBuffer.position(this.outputBufferInfo.offset);
                    this.outputBuffer.limit(this.outputBufferInfo.offset + this.outputBufferInfo.size);
                }
                this.isDecodeOnlyOutputBuffer = isDecodeOnlyBuffer(this.outputBufferInfo.presentationTimeUs);
                this.isLastOutputBuffer = this.lastBufferInStreamPresentationTimeUs == this.outputBufferInfo.presentationTimeUs;
                updateOutputFormatForTime(this.outputBufferInfo.presentationTimeUs);
            }
        }
        if (!this.codecNeedsEosOutputExceptionWorkaround || !this.codecReceivedEos) {
            z = false;
            processedOutputBuffer = processOutputBuffer(positionUs, elapsedRealtimeUs, this.codec, this.outputBuffer, this.outputIndex, this.outputBufferInfo.flags, this.outputBufferInfo.presentationTimeUs, this.isDecodeOnlyOutputBuffer, this.isLastOutputBuffer, this.outputFormat);
        } else {
            try {
                z = false;
                try {
                    processedOutputBuffer = processOutputBuffer(positionUs, elapsedRealtimeUs, this.codec, this.outputBuffer, this.outputIndex, this.outputBufferInfo.flags, this.outputBufferInfo.presentationTimeUs, this.isDecodeOnlyOutputBuffer, this.isLastOutputBuffer, this.outputFormat);
                } catch (IllegalStateException e2) {
                    processEndOfStream();
                    if (this.outputStreamEnded) {
                        releaseCodec();
                    }
                    return z;
                }
            } catch (IllegalStateException e3) {
                z = false;
            }
        }
        if (processedOutputBuffer) {
            onProcessedOutputBuffer(this.outputBufferInfo.presentationTimeUs);
            boolean isEndOfStream = (this.outputBufferInfo.flags & 4) != 0;
            resetOutputBuffer();
            if (!isEndOfStream) {
                return true;
            }
            processEndOfStream();
        }
        return z;
    }

    private void processOutputFormat() throws ExoPlaybackException {
        this.codecHasOutputMediaFormat = true;
        MediaFormat mediaFormat = this.codec.getOutputFormat();
        if (this.codecAdaptationWorkaroundMode != 0 && mediaFormat.getInteger("width") == 32 && mediaFormat.getInteger("height") == 32) {
            this.shouldSkipAdaptationWorkaroundOutputBuffer = true;
            return;
        }
        if (this.codecNeedsMonoChannelCountWorkaround) {
            mediaFormat.setInteger("channel-count", 1);
        }
        onOutputFormatChanged(this.codec, mediaFormat);
    }

    private void processOutputBuffersChanged() {
        if (Util.SDK_INT < 21) {
            this.outputBuffers = this.codec.getOutputBuffers();
        }
    }

    protected void renderToEndOfStream() throws ExoPlaybackException {
    }

    private void processEndOfStream() throws ExoPlaybackException {
        switch (this.codecDrainAction) {
            case 1:
                flushOrReinitializeCodec();
                return;
            case 2:
                updateDrmSessionOrReinitializeCodecV23();
                return;
            case 3:
                reinitializeCodec();
                return;
            default:
                this.outputStreamEnded = true;
                renderToEndOfStream();
                return;
        }
    }

    public final void setPendingOutputEndOfStream() {
        this.pendingOutputEndOfStream = true;
    }

    private void reinitializeCodec() throws ExoPlaybackException {
        releaseCodec();
        maybeInitCodec();
    }

    private boolean isDecodeOnlyBuffer(long presentationTimeUs) {
        int size = this.decodeOnlyPresentationTimestamps.size();
        for (int i = 0; i < size; i++) {
            if (this.decodeOnlyPresentationTimestamps.get(i).longValue() == presentationTimeUs) {
                this.decodeOnlyPresentationTimestamps.remove(i);
                return true;
            }
        }
        return false;
    }

    private void updateDrmSessionOrReinitializeCodecV23() throws ExoPlaybackException {
        FrameworkMediaCrypto sessionMediaCrypto = this.sourceDrmSession.getMediaCrypto();
        if (sessionMediaCrypto == null) {
            reinitializeCodec();
        } else if (C.PLAYREADY_UUID.equals(sessionMediaCrypto.uuid)) {
            reinitializeCodec();
        } else if (flushOrReinitializeCodec()) {
        } else {
            try {
                this.mediaCrypto.setMediaDrmSession(sessionMediaCrypto.sessionId);
                setCodecDrmSession(this.sourceDrmSession);
                this.codecDrainState = 0;
                this.codecDrainAction = 0;
            } catch (MediaCryptoException e) {
                throw createRendererException(e, this.inputFormat);
            }
        }
    }

    private static boolean maybeRequiresSecureDecoder(DrmSession<FrameworkMediaCrypto> drmSession, Format format) {
        FrameworkMediaCrypto sessionMediaCrypto = drmSession.getMediaCrypto();
        if (sessionMediaCrypto == null) {
            return true;
        }
        if (sessionMediaCrypto.forceAllowInsecureDecoderComponents) {
            return false;
        }
        try {
            MediaCrypto mediaCrypto = new MediaCrypto(sessionMediaCrypto.uuid, sessionMediaCrypto.sessionId);
            try {
                return mediaCrypto.requiresSecureDecoderComponent(format.sampleMimeType);
            } finally {
                mediaCrypto.release();
            }
        } catch (MediaCryptoException e) {
            return true;
        }
    }

    private static MediaCodec.CryptoInfo getFrameworkCryptoInfo(DecoderInputBuffer buffer, int adaptiveReconfigurationBytes) {
        MediaCodec.CryptoInfo cryptoInfo = buffer.cryptoInfo.getFrameworkCryptoInfo();
        if (adaptiveReconfigurationBytes == 0) {
            return cryptoInfo;
        }
        if (cryptoInfo.numBytesOfClearData == null) {
            cryptoInfo.numBytesOfClearData = new int[1];
        }
        int[] iArr = cryptoInfo.numBytesOfClearData;
        iArr[0] = iArr[0] + adaptiveReconfigurationBytes;
        return cryptoInfo;
    }

    private static boolean isMediaCodecException(IllegalStateException error) {
        if (Util.SDK_INT < 21 || !isMediaCodecExceptionV21(error)) {
            StackTraceElement[] stackTrace = error.getStackTrace();
            return stackTrace.length > 0 && stackTrace[0].getClassName().equals("android.media.MediaCodec");
        }
        return true;
    }

    private static boolean isMediaCodecExceptionV21(IllegalStateException error) {
        return error instanceof MediaCodec.CodecException;
    }

    private static boolean codecNeedsFlushWorkaround(String name) {
        return Util.SDK_INT < 18 || (Util.SDK_INT == 18 && ("OMX.SEC.avc.dec".equals(name) || "OMX.SEC.avc.dec.secure".equals(name))) || (Util.SDK_INT == 19 && Util.MODEL.startsWith("SM-G800") && ("OMX.Exynos.avc.dec".equals(name) || "OMX.Exynos.avc.dec.secure".equals(name)));
    }

    private int codecAdaptationWorkaroundMode(String name) {
        if (Util.SDK_INT <= 25 && "OMX.Exynos.avc.dec.secure".equals(name) && (Util.MODEL.startsWith("SM-T585") || Util.MODEL.startsWith("SM-A510") || Util.MODEL.startsWith("SM-A520") || Util.MODEL.startsWith("SM-J700"))) {
            return 2;
        }
        if (Util.SDK_INT < 24) {
            if ("OMX.Nvidia.h264.decode".equals(name) || "OMX.Nvidia.h264.decode.secure".equals(name)) {
                if ("flounder".equals(Util.DEVICE) || "flounder_lte".equals(Util.DEVICE) || "grouper".equals(Util.DEVICE) || "tilapia".equals(Util.DEVICE)) {
                    return 1;
                }
                return 0;
            }
            return 0;
        }
        return 0;
    }

    private static boolean codecNeedsReconfigureWorkaround(String name) {
        return Util.MODEL.startsWith("SM-T230") && "OMX.MARVELL.VIDEO.HW.CODA7542DECODER".equals(name);
    }

    private static boolean codecNeedsDiscardToSpsWorkaround(String name, Format format) {
        return Util.SDK_INT < 21 && format.initializationData.isEmpty() && "OMX.MTK.VIDEO.DECODER.AVC".equals(name);
    }

    private static boolean codecNeedsEosPropagationWorkaround(MediaCodecInfo codecInfo) {
        String name = codecInfo.name;
        return (Util.SDK_INT <= 25 && "OMX.rk.video_decoder.avc".equals(name)) || (Util.SDK_INT <= 17 && "OMX.allwinner.video.decoder.avc".equals(name)) || ("Amazon".equals(Util.MANUFACTURER) && "AFTS".equals(Util.MODEL) && codecInfo.secure);
    }

    private static boolean codecNeedsEosFlushWorkaround(String name) {
        return (Util.SDK_INT <= 23 && "OMX.google.vorbis.decoder".equals(name)) || (Util.SDK_INT <= 19 && (("hb2000".equals(Util.DEVICE) || "stvm8".equals(Util.DEVICE)) && ("OMX.amlogic.avc.decoder.awesome".equals(name) || "OMX.amlogic.avc.decoder.awesome.secure".equals(name))));
    }

    private static boolean codecNeedsEosOutputExceptionWorkaround(String name) {
        return Util.SDK_INT == 21 && "OMX.google.aac.decoder".equals(name);
    }

    private static boolean codecNeedsMonoChannelCountWorkaround(String name, Format format) {
        return Util.SDK_INT <= 18 && format.channelCount == 1 && "OMX.MTK.AUDIO.DECODER.MP3".equals(name);
    }

    private static boolean codecNeedsSosFlushWorkaround(String name) {
        return Util.SDK_INT == 29 && "c2.android.aac.decoder".equals(name);
    }
}
