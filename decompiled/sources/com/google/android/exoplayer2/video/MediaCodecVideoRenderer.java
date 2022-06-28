package com.google.android.exoplayer2.video;

import android.content.Context;
import android.graphics.Point;
import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Pair;
import android.view.Surface;
import androidx.core.text.HtmlCompat;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.RendererCapabilities;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.google.android.exoplayer2.mediacodec.MediaCodecInfo;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.mediacodec.MediaFormatUtil;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.TraceUtil;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.location.LocationRequest;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.microsoft.appcenter.crashes.utils.ErrorLogHelper;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.ui.Components.UndoView;
/* loaded from: classes3.dex */
public class MediaCodecVideoRenderer extends MediaCodecRenderer {
    private static final float INITIAL_FORMAT_MAX_INPUT_SIZE_SCALE_FACTOR = 1.5f;
    private static final String KEY_CROP_BOTTOM = "crop-bottom";
    private static final String KEY_CROP_LEFT = "crop-left";
    private static final String KEY_CROP_RIGHT = "crop-right";
    private static final String KEY_CROP_TOP = "crop-top";
    private static final int MAX_PENDING_OUTPUT_STREAM_OFFSET_COUNT = 10;
    private static final int[] STANDARD_LONG_EDGE_VIDEO_PX = {1920, 1600, 1440, 1280, 960, 854, 640, 540, 480};
    private static final String TAG = "MediaCodecVideoRenderer";
    private static final long TUNNELING_EOS_PRESENTATION_TIME_US = Long.MAX_VALUE;
    private static boolean deviceNeedsSetOutputSurfaceWorkaround;
    private static boolean evaluatedDeviceNeedsSetOutputSurfaceWorkaround;
    private final long allowedJoiningTimeMs;
    private int buffersInCodecCount;
    private boolean codecHandlesHdr10PlusOutOfBandMetadata;
    private CodecMaxValues codecMaxValues;
    private boolean codecNeedsSetOutputSurfaceWorkaround;
    private int consecutiveDroppedFrameCount;
    private final Context context;
    private int currentHeight;
    private MediaFormat currentMediaFormat;
    private float currentPixelWidthHeightRatio;
    private int currentUnappliedRotationDegrees;
    private int currentWidth;
    private final boolean deviceNeedsNoPostProcessWorkaround;
    private long droppedFrameAccumulationStartTimeMs;
    private int droppedFrames;
    private Surface dummySurface;
    private final VideoRendererEventListener.EventDispatcher eventDispatcher;
    private VideoFrameMetadataListener frameMetadataListener;
    private final VideoFrameReleaseTimeHelper frameReleaseTimeHelper;
    private long initialPositionUs;
    private long joiningDeadlineMs;
    private long lastInputTimeUs;
    private long lastRenderTimeUs;
    private final int maxDroppedFramesToNotify;
    private long outputStreamOffsetUs;
    private int pendingOutputStreamOffsetCount;
    private final long[] pendingOutputStreamOffsetsUs;
    private final long[] pendingOutputStreamSwitchTimesUs;
    private float pendingPixelWidthHeightRatio;
    private int pendingRotationDegrees;
    private boolean renderedFirstFrame;
    private int reportedHeight;
    private float reportedPixelWidthHeightRatio;
    private int reportedUnappliedRotationDegrees;
    private int reportedWidth;
    private int scalingMode;
    private Surface surface;
    private boolean tunneling;
    private int tunnelingAudioSessionId;
    OnFrameRenderedListenerV23 tunnelingOnFrameRenderedListener;

    /* loaded from: classes3.dex */
    public static final class VideoDecoderException extends MediaCodecRenderer.DecoderException {
        public final boolean isSurfaceValid;
        public final int surfaceIdentityHashCode;

        public VideoDecoderException(Throwable cause, MediaCodecInfo codecInfo, Surface surface) {
            super(cause, codecInfo);
            this.surfaceIdentityHashCode = System.identityHashCode(surface);
            this.isSurfaceValid = surface == null || surface.isValid();
        }
    }

    public MediaCodecVideoRenderer(Context context, MediaCodecSelector mediaCodecSelector) {
        this(context, mediaCodecSelector, 0L);
    }

    public MediaCodecVideoRenderer(Context context, MediaCodecSelector mediaCodecSelector, long allowedJoiningTimeMs) {
        this(context, mediaCodecSelector, allowedJoiningTimeMs, null, null, -1);
    }

    public MediaCodecVideoRenderer(Context context, MediaCodecSelector mediaCodecSelector, long allowedJoiningTimeMs, Handler eventHandler, VideoRendererEventListener eventListener, int maxDroppedFramesToNotify) {
        this(context, mediaCodecSelector, allowedJoiningTimeMs, null, false, eventHandler, eventListener, maxDroppedFramesToNotify);
    }

    @Deprecated
    public MediaCodecVideoRenderer(Context context, MediaCodecSelector mediaCodecSelector, long allowedJoiningTimeMs, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, boolean playClearSamplesWithoutKeys, Handler eventHandler, VideoRendererEventListener eventListener, int maxDroppedFramesToNotify) {
        this(context, mediaCodecSelector, allowedJoiningTimeMs, drmSessionManager, playClearSamplesWithoutKeys, false, eventHandler, eventListener, maxDroppedFramesToNotify);
    }

    public MediaCodecVideoRenderer(Context context, MediaCodecSelector mediaCodecSelector, long allowedJoiningTimeMs, boolean enableDecoderFallback, Handler eventHandler, VideoRendererEventListener eventListener, int maxDroppedFramesToNotify) {
        this(context, mediaCodecSelector, allowedJoiningTimeMs, null, false, enableDecoderFallback, eventHandler, eventListener, maxDroppedFramesToNotify);
    }

    @Deprecated
    public MediaCodecVideoRenderer(Context context, MediaCodecSelector mediaCodecSelector, long allowedJoiningTimeMs, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, boolean playClearSamplesWithoutKeys, boolean enableDecoderFallback, Handler eventHandler, VideoRendererEventListener eventListener, int maxDroppedFramesToNotify) {
        super(2, mediaCodecSelector, drmSessionManager, playClearSamplesWithoutKeys, enableDecoderFallback, 30.0f);
        this.allowedJoiningTimeMs = allowedJoiningTimeMs;
        this.maxDroppedFramesToNotify = maxDroppedFramesToNotify;
        Context applicationContext = context.getApplicationContext();
        this.context = applicationContext;
        this.frameReleaseTimeHelper = new VideoFrameReleaseTimeHelper(applicationContext);
        this.eventDispatcher = new VideoRendererEventListener.EventDispatcher(eventHandler, eventListener);
        this.deviceNeedsNoPostProcessWorkaround = deviceNeedsNoPostProcessWorkaround();
        this.pendingOutputStreamOffsetsUs = new long[10];
        this.pendingOutputStreamSwitchTimesUs = new long[10];
        this.outputStreamOffsetUs = C.TIME_UNSET;
        this.lastInputTimeUs = C.TIME_UNSET;
        this.joiningDeadlineMs = C.TIME_UNSET;
        this.currentWidth = -1;
        this.currentHeight = -1;
        this.currentPixelWidthHeightRatio = -1.0f;
        this.pendingPixelWidthHeightRatio = -1.0f;
        this.scalingMode = 1;
        clearReportedVideoSize();
    }

    @Override // com.google.android.exoplayer2.mediacodec.MediaCodecRenderer
    protected int supportsFormat(MediaCodecSelector mediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, Format format) throws MediaCodecUtil.DecoderQueryException {
        int adaptiveSupport;
        String mimeType = format.sampleMimeType;
        if (!MimeTypes.isVideo(mimeType)) {
            return RendererCapabilities.CC.create(0);
        }
        DrmInitData drmInitData = format.drmInitData;
        boolean requiresSecureDecryption = drmInitData != null;
        List<MediaCodecInfo> decoderInfos = getDecoderInfos(mediaCodecSelector, format, requiresSecureDecryption, false);
        if (requiresSecureDecryption && decoderInfos.isEmpty()) {
            decoderInfos = getDecoderInfos(mediaCodecSelector, format, false, false);
        }
        if (decoderInfos.isEmpty()) {
            return RendererCapabilities.CC.create(1);
        }
        boolean supportsFormatDrm = drmInitData == null || FrameworkMediaCrypto.class.equals(format.exoMediaCryptoType) || (format.exoMediaCryptoType == null && supportsFormatDrm(drmSessionManager, drmInitData));
        if (!supportsFormatDrm) {
            return RendererCapabilities.CC.create(2);
        }
        MediaCodecInfo decoderInfo = decoderInfos.get(0);
        boolean isFormatSupported = decoderInfo.isFormatSupported(format);
        if (decoderInfo.isSeamlessAdaptationSupported(format)) {
            adaptiveSupport = 16;
        } else {
            adaptiveSupport = 8;
        }
        int tunnelingSupport = 0;
        if (isFormatSupported) {
            List<MediaCodecInfo> tunnelingDecoderInfos = getDecoderInfos(mediaCodecSelector, format, requiresSecureDecryption, true);
            if (!tunnelingDecoderInfos.isEmpty()) {
                MediaCodecInfo tunnelingDecoderInfo = tunnelingDecoderInfos.get(0);
                if (tunnelingDecoderInfo.isFormatSupported(format) && tunnelingDecoderInfo.isSeamlessAdaptationSupported(format)) {
                    tunnelingSupport = 32;
                }
            }
        }
        int formatSupport = isFormatSupported ? 4 : 3;
        return RendererCapabilities.CC.create(formatSupport, adaptiveSupport, tunnelingSupport);
    }

    @Override // com.google.android.exoplayer2.mediacodec.MediaCodecRenderer
    protected List<MediaCodecInfo> getDecoderInfos(MediaCodecSelector mediaCodecSelector, Format format, boolean requiresSecureDecoder) throws MediaCodecUtil.DecoderQueryException {
        return getDecoderInfos(mediaCodecSelector, format, requiresSecureDecoder, this.tunneling);
    }

    private static List<MediaCodecInfo> getDecoderInfos(MediaCodecSelector mediaCodecSelector, Format format, boolean requiresSecureDecoder, boolean requiresTunnelingDecoder) throws MediaCodecUtil.DecoderQueryException {
        Pair<Integer, Integer> codecProfileAndLevel;
        String mimeType = format.sampleMimeType;
        if (mimeType == null) {
            return Collections.emptyList();
        }
        List<MediaCodecInfo> decoderInfos = MediaCodecUtil.getDecoderInfosSortedByFormatSupport(mediaCodecSelector.getDecoderInfos(mimeType, requiresSecureDecoder, requiresTunnelingDecoder), format);
        if (MimeTypes.VIDEO_DOLBY_VISION.equals(mimeType) && (codecProfileAndLevel = MediaCodecUtil.getCodecProfileAndLevel(format)) != null) {
            int profile = ((Integer) codecProfileAndLevel.first).intValue();
            if (profile == 16 || profile == 256) {
                decoderInfos.addAll(mediaCodecSelector.getDecoderInfos(MimeTypes.VIDEO_H265, requiresSecureDecoder, requiresTunnelingDecoder));
            } else if (profile == 512) {
                decoderInfos.addAll(mediaCodecSelector.getDecoderInfos("video/avc", requiresSecureDecoder, requiresTunnelingDecoder));
            }
        }
        return Collections.unmodifiableList(decoderInfos);
    }

    @Override // com.google.android.exoplayer2.mediacodec.MediaCodecRenderer, com.google.android.exoplayer2.BaseRenderer
    public void onEnabled(boolean joining) throws ExoPlaybackException {
        super.onEnabled(joining);
        int oldTunnelingAudioSessionId = this.tunnelingAudioSessionId;
        int i = getConfiguration().tunnelingAudioSessionId;
        this.tunnelingAudioSessionId = i;
        this.tunneling = i != 0;
        if (i != oldTunnelingAudioSessionId) {
            releaseCodec();
        }
        this.eventDispatcher.enabled(this.decoderCounters);
        this.frameReleaseTimeHelper.enable();
    }

    @Override // com.google.android.exoplayer2.BaseRenderer
    public void onStreamChanged(Format[] formats, long offsetUs) throws ExoPlaybackException {
        if (this.outputStreamOffsetUs == C.TIME_UNSET) {
            this.outputStreamOffsetUs = offsetUs;
        } else {
            int i = this.pendingOutputStreamOffsetCount;
            if (i == this.pendingOutputStreamOffsetsUs.length) {
                Log.w(TAG, "Too many stream changes, so dropping offset: " + this.pendingOutputStreamOffsetsUs[this.pendingOutputStreamOffsetCount - 1]);
            } else {
                this.pendingOutputStreamOffsetCount = i + 1;
            }
            long[] jArr = this.pendingOutputStreamOffsetsUs;
            int i2 = this.pendingOutputStreamOffsetCount;
            jArr[i2 - 1] = offsetUs;
            this.pendingOutputStreamSwitchTimesUs[i2 - 1] = this.lastInputTimeUs;
        }
        super.onStreamChanged(formats, offsetUs);
    }

    @Override // com.google.android.exoplayer2.mediacodec.MediaCodecRenderer, com.google.android.exoplayer2.BaseRenderer
    public void onPositionReset(long positionUs, boolean joining) throws ExoPlaybackException {
        super.onPositionReset(positionUs, joining);
        clearRenderedFirstFrame();
        this.initialPositionUs = C.TIME_UNSET;
        this.consecutiveDroppedFrameCount = 0;
        this.lastInputTimeUs = C.TIME_UNSET;
        int i = this.pendingOutputStreamOffsetCount;
        if (i != 0) {
            this.outputStreamOffsetUs = this.pendingOutputStreamOffsetsUs[i - 1];
            this.pendingOutputStreamOffsetCount = 0;
        }
        if (joining) {
            setJoiningDeadlineMs();
        } else {
            this.joiningDeadlineMs = C.TIME_UNSET;
        }
    }

    @Override // com.google.android.exoplayer2.mediacodec.MediaCodecRenderer, com.google.android.exoplayer2.Renderer
    public boolean isReady() {
        Surface surface;
        if (super.isReady() && (this.renderedFirstFrame || (((surface = this.dummySurface) != null && this.surface == surface) || getCodec() == null || this.tunneling))) {
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

    @Override // com.google.android.exoplayer2.mediacodec.MediaCodecRenderer, com.google.android.exoplayer2.BaseRenderer
    public void onStarted() {
        super.onStarted();
        this.droppedFrames = 0;
        this.droppedFrameAccumulationStartTimeMs = SystemClock.elapsedRealtime();
        this.lastRenderTimeUs = SystemClock.elapsedRealtime() * 1000;
    }

    @Override // com.google.android.exoplayer2.mediacodec.MediaCodecRenderer, com.google.android.exoplayer2.BaseRenderer
    public void onStopped() {
        this.joiningDeadlineMs = C.TIME_UNSET;
        maybeNotifyDroppedFrames();
        super.onStopped();
    }

    @Override // com.google.android.exoplayer2.mediacodec.MediaCodecRenderer, com.google.android.exoplayer2.BaseRenderer
    public void onDisabled() {
        this.lastInputTimeUs = C.TIME_UNSET;
        this.outputStreamOffsetUs = C.TIME_UNSET;
        this.pendingOutputStreamOffsetCount = 0;
        this.currentMediaFormat = null;
        clearReportedVideoSize();
        clearRenderedFirstFrame();
        this.frameReleaseTimeHelper.disable();
        this.tunnelingOnFrameRenderedListener = null;
        try {
            super.onDisabled();
        } finally {
            this.eventDispatcher.disabled(this.decoderCounters);
        }
    }

    @Override // com.google.android.exoplayer2.mediacodec.MediaCodecRenderer, com.google.android.exoplayer2.BaseRenderer
    public void onReset() {
        try {
            super.onReset();
            Surface surface = this.dummySurface;
            if (surface != null) {
                if (this.surface == surface) {
                    this.surface = null;
                }
                surface.release();
                this.dummySurface = null;
            }
        } catch (Throwable th) {
            if (this.dummySurface != null) {
                Surface surface2 = this.surface;
                Surface surface3 = this.dummySurface;
                if (surface2 == surface3) {
                    this.surface = null;
                }
                surface3.release();
                this.dummySurface = null;
            }
            throw th;
        }
    }

    @Override // com.google.android.exoplayer2.BaseRenderer, com.google.android.exoplayer2.PlayerMessage.Target
    public void handleMessage(int messageType, Object message) throws ExoPlaybackException {
        if (messageType == 1) {
            setSurface((Surface) message);
        } else if (messageType != 4) {
            if (messageType == 6) {
                this.frameMetadataListener = (VideoFrameMetadataListener) message;
            } else {
                super.handleMessage(messageType, message);
            }
        } else {
            this.scalingMode = ((Integer) message).intValue();
            MediaCodec codec = getCodec();
            if (codec != null) {
                codec.setVideoScalingMode(this.scalingMode);
            }
        }
    }

    private void setSurface(Surface surface) throws ExoPlaybackException {
        if (surface == null) {
            if (this.dummySurface != null) {
                surface = this.dummySurface;
            } else {
                MediaCodecInfo codecInfo = getCodecInfo();
                if (codecInfo != null && shouldUseDummySurface(codecInfo)) {
                    this.dummySurface = DummySurface.newInstanceV17(this.context, codecInfo.secure);
                    surface = this.dummySurface;
                }
            }
        }
        if (this.surface != surface) {
            this.surface = surface;
            int state = getState();
            MediaCodec codec = getCodec();
            if (codec != null) {
                if (Util.SDK_INT >= 23 && surface != null && !this.codecNeedsSetOutputSurfaceWorkaround) {
                    try {
                        setOutputSurfaceV23(codec, surface);
                    } catch (Throwable e) {
                        throw new SurfaceNotValidException(e);
                    }
                } else {
                    releaseCodec();
                    maybeInitCodec();
                }
            }
            if (surface != null && surface != this.dummySurface) {
                maybeRenotifyVideoSizeChanged();
                clearRenderedFirstFrame();
                if (state == 2) {
                    setJoiningDeadlineMs();
                    return;
                }
                return;
            }
            clearReportedVideoSize();
            clearRenderedFirstFrame();
        } else if (surface != null && surface != this.dummySurface) {
            maybeRenotifyVideoSizeChanged();
            maybeRenotifyRenderedFirstFrame();
        }
    }

    @Override // com.google.android.exoplayer2.mediacodec.MediaCodecRenderer
    protected boolean shouldInitCodec(MediaCodecInfo codecInfo) {
        return this.surface != null || shouldUseDummySurface(codecInfo);
    }

    @Override // com.google.android.exoplayer2.mediacodec.MediaCodecRenderer
    protected boolean getCodecNeedsEosPropagation() {
        return this.tunneling && Util.SDK_INT < 23;
    }

    @Override // com.google.android.exoplayer2.mediacodec.MediaCodecRenderer
    protected void configureCodec(MediaCodecInfo codecInfo, MediaCodec codec, Format format, MediaCrypto crypto, float codecOperatingRate) {
        String codecMimeType = codecInfo.codecMimeType;
        CodecMaxValues codecMaxValues = getCodecMaxValues(codecInfo, format, getStreamFormats());
        this.codecMaxValues = codecMaxValues;
        MediaFormat mediaFormat = getMediaFormat(format, codecMimeType, codecMaxValues, codecOperatingRate, this.deviceNeedsNoPostProcessWorkaround, this.tunnelingAudioSessionId);
        if (this.surface == null) {
            Assertions.checkState(shouldUseDummySurface(codecInfo));
            if (this.dummySurface == null) {
                this.dummySurface = DummySurface.newInstanceV17(this.context, codecInfo.secure);
            }
            this.surface = this.dummySurface;
        }
        codec.configure(mediaFormat, this.surface, crypto, 0);
        if (Util.SDK_INT >= 23 && this.tunneling) {
            this.tunnelingOnFrameRenderedListener = new OnFrameRenderedListenerV23(codec);
        }
    }

    @Override // com.google.android.exoplayer2.mediacodec.MediaCodecRenderer
    protected int canKeepCodec(MediaCodec codec, MediaCodecInfo codecInfo, Format oldFormat, Format newFormat) {
        if (codecInfo.isSeamlessAdaptationSupported(oldFormat, newFormat, true) && newFormat.width <= this.codecMaxValues.width && newFormat.height <= this.codecMaxValues.height && getMaxInputSize(codecInfo, newFormat) <= this.codecMaxValues.inputSize) {
            if (oldFormat.initializationDataEquals(newFormat)) {
                return 3;
            }
            return 2;
        }
        return 0;
    }

    @Override // com.google.android.exoplayer2.mediacodec.MediaCodecRenderer
    public void releaseCodec() {
        try {
            super.releaseCodec();
        } finally {
            this.buffersInCodecCount = 0;
        }
    }

    @Override // com.google.android.exoplayer2.mediacodec.MediaCodecRenderer
    public boolean flushOrReleaseCodec() {
        try {
            return super.flushOrReleaseCodec();
        } finally {
            this.buffersInCodecCount = 0;
        }
    }

    @Override // com.google.android.exoplayer2.mediacodec.MediaCodecRenderer
    protected float getCodecOperatingRateV23(float operatingRate, Format format, Format[] streamFormats) {
        float maxFrameRate = -1.0f;
        for (Format streamFormat : streamFormats) {
            float streamFrameRate = streamFormat.frameRate;
            if (streamFrameRate != -1.0f) {
                maxFrameRate = Math.max(maxFrameRate, streamFrameRate);
            }
        }
        if (maxFrameRate == -1.0f) {
            return -1.0f;
        }
        return maxFrameRate * operatingRate;
    }

    @Override // com.google.android.exoplayer2.mediacodec.MediaCodecRenderer
    protected void onCodecInitialized(String name, long initializedTimestampMs, long initializationDurationMs) {
        this.eventDispatcher.decoderInitialized(name, initializedTimestampMs, initializationDurationMs);
        this.codecNeedsSetOutputSurfaceWorkaround = codecNeedsSetOutputSurfaceWorkaround(name);
        this.codecHandlesHdr10PlusOutOfBandMetadata = ((MediaCodecInfo) Assertions.checkNotNull(getCodecInfo())).isHdr10PlusOutOfBandMetadataSupported();
    }

    @Override // com.google.android.exoplayer2.mediacodec.MediaCodecRenderer
    public void onInputFormatChanged(FormatHolder formatHolder) throws ExoPlaybackException {
        super.onInputFormatChanged(formatHolder);
        Format newFormat = formatHolder.format;
        this.eventDispatcher.inputFormatChanged(newFormat);
        this.pendingPixelWidthHeightRatio = newFormat.pixelWidthHeightRatio;
        this.pendingRotationDegrees = newFormat.rotationDegrees;
    }

    @Override // com.google.android.exoplayer2.mediacodec.MediaCodecRenderer
    protected void onQueueInputBuffer(DecoderInputBuffer buffer) {
        if (!this.tunneling) {
            this.buffersInCodecCount++;
        }
        this.lastInputTimeUs = Math.max(buffer.timeUs, this.lastInputTimeUs);
        if (Util.SDK_INT < 23 && this.tunneling) {
            onProcessedTunneledBuffer(buffer.timeUs);
        }
    }

    @Override // com.google.android.exoplayer2.mediacodec.MediaCodecRenderer
    protected void onOutputFormatChanged(MediaCodec codec, MediaFormat outputMediaFormat) {
        int width;
        int i;
        this.currentMediaFormat = outputMediaFormat;
        boolean hasCrop = outputMediaFormat.containsKey(KEY_CROP_RIGHT) && outputMediaFormat.containsKey(KEY_CROP_LEFT) && outputMediaFormat.containsKey(KEY_CROP_BOTTOM) && outputMediaFormat.containsKey(KEY_CROP_TOP);
        if (hasCrop) {
            width = (outputMediaFormat.getInteger(KEY_CROP_RIGHT) - outputMediaFormat.getInteger(KEY_CROP_LEFT)) + 1;
        } else {
            width = outputMediaFormat.getInteger("width");
        }
        if (hasCrop) {
            i = (outputMediaFormat.getInteger(KEY_CROP_BOTTOM) - outputMediaFormat.getInteger(KEY_CROP_TOP)) + 1;
        } else {
            i = outputMediaFormat.getInteger("height");
        }
        int height = i;
        processOutputFormat(codec, width, height);
    }

    @Override // com.google.android.exoplayer2.mediacodec.MediaCodecRenderer
    protected void handleInputBufferSupplementalData(DecoderInputBuffer buffer) throws ExoPlaybackException {
        if (!this.codecHandlesHdr10PlusOutOfBandMetadata) {
            return;
        }
        ByteBuffer data = (ByteBuffer) Assertions.checkNotNull(buffer.supplementalData);
        if (data.remaining() >= 7) {
            byte ituTT35CountryCode = data.get();
            int ituTT35TerminalProviderCode = data.getShort();
            int ituTT35TerminalProviderOrientedCode = data.getShort();
            byte applicationIdentifier = data.get();
            byte applicationVersion = data.get();
            data.position(0);
            if (ituTT35CountryCode == -75 && ituTT35TerminalProviderCode == 60 && ituTT35TerminalProviderOrientedCode == 1 && applicationIdentifier == 4 && applicationVersion == 0) {
                byte[] hdr10PlusInfo = new byte[data.remaining()];
                data.get(hdr10PlusInfo);
                data.position(0);
                setHdr10PlusInfoV29(getCodec(), hdr10PlusInfo);
            }
        }
    }

    @Override // com.google.android.exoplayer2.mediacodec.MediaCodecRenderer
    protected boolean processOutputBuffer(long positionUs, long elapsedRealtimeUs, MediaCodec codec, ByteBuffer buffer, int bufferIndex, int bufferFlags, long bufferPresentationTimeUs, boolean isDecodeOnlyBuffer, boolean isLastBuffer, Format format) throws ExoPlaybackException {
        long unadjustedFrameReleaseTimeNs;
        if (this.initialPositionUs == C.TIME_UNSET) {
            this.initialPositionUs = positionUs;
        }
        long presentationTimeUs = bufferPresentationTimeUs - this.outputStreamOffsetUs;
        if (isDecodeOnlyBuffer && !isLastBuffer) {
            skipOutputBuffer(codec, bufferIndex, presentationTimeUs);
            return true;
        }
        long earlyUs = bufferPresentationTimeUs - positionUs;
        if (this.surface == this.dummySurface) {
            if (!isBufferLate(earlyUs)) {
                return false;
            }
            skipOutputBuffer(codec, bufferIndex, presentationTimeUs);
            return true;
        }
        long elapsedRealtimeNowUs = SystemClock.elapsedRealtime() * 1000;
        long elapsedSinceLastRenderUs = elapsedRealtimeNowUs - this.lastRenderTimeUs;
        boolean isStarted = getState() == 2;
        boolean forceRenderOutputBuffer = this.joiningDeadlineMs == C.TIME_UNSET && positionUs >= this.outputStreamOffsetUs && (!this.renderedFirstFrame || (isStarted && shouldForceRenderOutputBuffer(earlyUs, elapsedSinceLastRenderUs)));
        if (!forceRenderOutputBuffer) {
            if (isStarted && positionUs != this.initialPositionUs) {
                long elapsedSinceStartOfLoopUs = elapsedRealtimeNowUs - elapsedRealtimeUs;
                long systemTimeNs = System.nanoTime();
                long adjustedReleaseTimeNs = this.frameReleaseTimeHelper.adjustReleaseTime(bufferPresentationTimeUs, systemTimeNs + ((earlyUs - elapsedSinceStartOfLoopUs) * 1000));
                long earlyUs2 = (adjustedReleaseTimeNs - systemTimeNs) / 1000;
                boolean treatDroppedBuffersAsSkipped = this.joiningDeadlineMs != C.TIME_UNSET;
                if (shouldDropBuffersToKeyframe(earlyUs2, elapsedRealtimeUs, isLastBuffer)) {
                    unadjustedFrameReleaseTimeNs = presentationTimeUs;
                    if (maybeDropBuffersToKeyframe(codec, bufferIndex, presentationTimeUs, positionUs, treatDroppedBuffersAsSkipped)) {
                        return false;
                    }
                } else {
                    unadjustedFrameReleaseTimeNs = presentationTimeUs;
                }
                if (!shouldDropOutputBuffer(earlyUs2, elapsedRealtimeUs, isLastBuffer)) {
                    if (Util.SDK_INT >= 21) {
                        if (earlyUs2 < 50000) {
                            notifyFrameMetadataListener(unadjustedFrameReleaseTimeNs, adjustedReleaseTimeNs, format, this.currentMediaFormat);
                            renderOutputBufferV21(codec, bufferIndex, unadjustedFrameReleaseTimeNs, adjustedReleaseTimeNs);
                            return true;
                        }
                    } else if (earlyUs2 < 30000) {
                        if (earlyUs2 > 11000) {
                            try {
                                Thread.sleep((earlyUs2 - 10000) / 1000);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                return false;
                            }
                        }
                        notifyFrameMetadataListener(unadjustedFrameReleaseTimeNs, adjustedReleaseTimeNs, format, this.currentMediaFormat);
                        renderOutputBuffer(codec, bufferIndex, unadjustedFrameReleaseTimeNs);
                        return true;
                    }
                    return false;
                } else if (treatDroppedBuffersAsSkipped) {
                    skipOutputBuffer(codec, bufferIndex, unadjustedFrameReleaseTimeNs);
                    return true;
                } else {
                    dropOutputBuffer(codec, bufferIndex, unadjustedFrameReleaseTimeNs);
                    return true;
                }
            }
            return false;
        }
        long releaseTimeNs = System.nanoTime();
        notifyFrameMetadataListener(presentationTimeUs, releaseTimeNs, format, this.currentMediaFormat);
        if (Util.SDK_INT < 21) {
            renderOutputBuffer(codec, bufferIndex, presentationTimeUs);
            return true;
        }
        renderOutputBufferV21(codec, bufferIndex, presentationTimeUs, releaseTimeNs);
        return true;
    }

    private void processOutputFormat(MediaCodec codec, int width, int height) {
        this.currentWidth = width;
        this.currentHeight = height;
        this.currentPixelWidthHeightRatio = this.pendingPixelWidthHeightRatio;
        if (Util.SDK_INT >= 21) {
            int i = this.pendingRotationDegrees;
            if (i == 90 || i == 270) {
                int rotatedHeight = this.currentWidth;
                this.currentWidth = this.currentHeight;
                this.currentHeight = rotatedHeight;
                this.currentPixelWidthHeightRatio = 1.0f / this.currentPixelWidthHeightRatio;
            }
        } else {
            this.currentUnappliedRotationDegrees = this.pendingRotationDegrees;
        }
        codec.setVideoScalingMode(this.scalingMode);
    }

    private void notifyFrameMetadataListener(long presentationTimeUs, long releaseTimeNs, Format format, MediaFormat mediaFormat) {
        VideoFrameMetadataListener videoFrameMetadataListener = this.frameMetadataListener;
        if (videoFrameMetadataListener != null) {
            videoFrameMetadataListener.onVideoFrameAboutToBeRendered(presentationTimeUs, releaseTimeNs, format, mediaFormat);
        }
    }

    protected long getOutputStreamOffsetUs() {
        return this.outputStreamOffsetUs;
    }

    protected void onProcessedTunneledBuffer(long presentationTimeUs) {
        Format format = updateOutputFormatForTime(presentationTimeUs);
        if (format != null) {
            processOutputFormat(getCodec(), format.width, format.height);
        }
        maybeNotifyVideoSizeChanged();
        this.decoderCounters.renderedOutputBufferCount++;
        maybeNotifyRenderedFirstFrame();
        onProcessedOutputBuffer(presentationTimeUs);
    }

    public void onProcessedTunneledEndOfStream() {
        setPendingOutputEndOfStream();
    }

    @Override // com.google.android.exoplayer2.mediacodec.MediaCodecRenderer
    protected void onProcessedOutputBuffer(long presentationTimeUs) {
        if (!this.tunneling) {
            this.buffersInCodecCount--;
        }
        while (true) {
            int i = this.pendingOutputStreamOffsetCount;
            if (i != 0 && presentationTimeUs >= this.pendingOutputStreamSwitchTimesUs[0]) {
                long[] jArr = this.pendingOutputStreamOffsetsUs;
                this.outputStreamOffsetUs = jArr[0];
                int i2 = i - 1;
                this.pendingOutputStreamOffsetCount = i2;
                System.arraycopy(jArr, 1, jArr, 0, i2);
                long[] jArr2 = this.pendingOutputStreamSwitchTimesUs;
                System.arraycopy(jArr2, 1, jArr2, 0, this.pendingOutputStreamOffsetCount);
                clearRenderedFirstFrame();
            } else {
                return;
            }
        }
    }

    protected boolean shouldDropOutputBuffer(long earlyUs, long elapsedRealtimeUs, boolean isLastBuffer) {
        return isBufferLate(earlyUs) && !isLastBuffer;
    }

    protected boolean shouldDropBuffersToKeyframe(long earlyUs, long elapsedRealtimeUs, boolean isLastBuffer) {
        return isBufferVeryLate(earlyUs) && !isLastBuffer;
    }

    protected boolean shouldForceRenderOutputBuffer(long earlyUs, long elapsedSinceLastRenderUs) {
        return isBufferLate(earlyUs) && elapsedSinceLastRenderUs > 100000;
    }

    protected void skipOutputBuffer(MediaCodec codec, int index, long presentationTimeUs) {
        TraceUtil.beginSection("skipVideoBuffer");
        codec.releaseOutputBuffer(index, false);
        TraceUtil.endSection();
        this.decoderCounters.skippedOutputBufferCount++;
    }

    protected void dropOutputBuffer(MediaCodec codec, int index, long presentationTimeUs) {
        TraceUtil.beginSection("dropVideoBuffer");
        codec.releaseOutputBuffer(index, false);
        TraceUtil.endSection();
        updateDroppedBufferCounters(1);
    }

    protected boolean maybeDropBuffersToKeyframe(MediaCodec codec, int index, long presentationTimeUs, long positionUs, boolean treatDroppedBuffersAsSkipped) throws ExoPlaybackException {
        int droppedSourceBufferCount = skipSource(positionUs);
        if (droppedSourceBufferCount == 0) {
            return false;
        }
        this.decoderCounters.droppedToKeyframeCount++;
        int totalDroppedBufferCount = this.buffersInCodecCount + droppedSourceBufferCount;
        if (treatDroppedBuffersAsSkipped) {
            this.decoderCounters.skippedOutputBufferCount += totalDroppedBufferCount;
        } else {
            updateDroppedBufferCounters(totalDroppedBufferCount);
        }
        flushOrReinitializeCodec();
        return true;
    }

    protected void updateDroppedBufferCounters(int droppedBufferCount) {
        this.decoderCounters.droppedBufferCount += droppedBufferCount;
        this.droppedFrames += droppedBufferCount;
        this.consecutiveDroppedFrameCount += droppedBufferCount;
        this.decoderCounters.maxConsecutiveDroppedBufferCount = Math.max(this.consecutiveDroppedFrameCount, this.decoderCounters.maxConsecutiveDroppedBufferCount);
        int i = this.maxDroppedFramesToNotify;
        if (i > 0 && this.droppedFrames >= i) {
            maybeNotifyDroppedFrames();
        }
    }

    protected void renderOutputBuffer(MediaCodec codec, int index, long presentationTimeUs) {
        maybeNotifyVideoSizeChanged();
        TraceUtil.beginSection("releaseOutputBuffer");
        codec.releaseOutputBuffer(index, true);
        TraceUtil.endSection();
        this.lastRenderTimeUs = SystemClock.elapsedRealtime() * 1000;
        this.decoderCounters.renderedOutputBufferCount++;
        this.consecutiveDroppedFrameCount = 0;
        maybeNotifyRenderedFirstFrame();
    }

    protected void renderOutputBufferV21(MediaCodec codec, int index, long presentationTimeUs, long releaseTimeNs) {
        maybeNotifyVideoSizeChanged();
        TraceUtil.beginSection("releaseOutputBuffer");
        codec.releaseOutputBuffer(index, releaseTimeNs);
        TraceUtil.endSection();
        this.lastRenderTimeUs = SystemClock.elapsedRealtime() * 1000;
        this.decoderCounters.renderedOutputBufferCount++;
        this.consecutiveDroppedFrameCount = 0;
        maybeNotifyRenderedFirstFrame();
    }

    private boolean shouldUseDummySurface(MediaCodecInfo codecInfo) {
        return Util.SDK_INT >= 23 && !this.tunneling && !codecNeedsSetOutputSurfaceWorkaround(codecInfo.name) && (!codecInfo.secure || DummySurface.isSecureSupported(this.context));
    }

    private void setJoiningDeadlineMs() {
        this.joiningDeadlineMs = this.allowedJoiningTimeMs > 0 ? SystemClock.elapsedRealtime() + this.allowedJoiningTimeMs : C.TIME_UNSET;
    }

    private void clearRenderedFirstFrame() {
        MediaCodec codec;
        this.renderedFirstFrame = false;
        if (Util.SDK_INT >= 23 && this.tunneling && (codec = getCodec()) != null) {
            this.tunnelingOnFrameRenderedListener = new OnFrameRenderedListenerV23(codec);
        }
    }

    void maybeNotifyRenderedFirstFrame() {
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
        this.reportedPixelWidthHeightRatio = -1.0f;
        this.reportedUnappliedRotationDegrees = -1;
    }

    private void maybeNotifyVideoSizeChanged() {
        int i = this.currentWidth;
        if (i == -1 && this.currentHeight == -1) {
            return;
        }
        if (this.reportedWidth != i || this.reportedHeight != this.currentHeight || this.reportedUnappliedRotationDegrees != this.currentUnappliedRotationDegrees || this.reportedPixelWidthHeightRatio != this.currentPixelWidthHeightRatio) {
            this.eventDispatcher.videoSizeChanged(i, this.currentHeight, this.currentUnappliedRotationDegrees, this.currentPixelWidthHeightRatio);
            this.reportedWidth = this.currentWidth;
            this.reportedHeight = this.currentHeight;
            this.reportedUnappliedRotationDegrees = this.currentUnappliedRotationDegrees;
            this.reportedPixelWidthHeightRatio = this.currentPixelWidthHeightRatio;
        }
    }

    private void maybeRenotifyVideoSizeChanged() {
        int i = this.reportedWidth;
        if (i != -1 || this.reportedHeight != -1) {
            this.eventDispatcher.videoSizeChanged(i, this.reportedHeight, this.reportedUnappliedRotationDegrees, this.reportedPixelWidthHeightRatio);
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

    private static void setHdr10PlusInfoV29(MediaCodec codec, byte[] hdr10PlusInfo) {
        Bundle codecParameters = new Bundle();
        codecParameters.putByteArray("hdr10-plus-info", hdr10PlusInfo);
        codec.setParameters(codecParameters);
    }

    private static void setOutputSurfaceV23(MediaCodec codec, Surface surface) {
        codec.setOutputSurface(surface);
    }

    private static void configureTunnelingV21(MediaFormat mediaFormat, int tunnelingAudioSessionId) {
        mediaFormat.setFeatureEnabled("tunneled-playback", true);
        mediaFormat.setInteger("audio-session-id", tunnelingAudioSessionId);
    }

    protected MediaFormat getMediaFormat(Format format, String codecMimeType, CodecMaxValues codecMaxValues, float codecOperatingRate, boolean deviceNeedsNoPostProcessWorkaround, int tunnelingAudioSessionId) {
        Pair<Integer, Integer> codecProfileAndLevel;
        MediaFormat mediaFormat = new MediaFormat();
        mediaFormat.setString("mime", codecMimeType);
        mediaFormat.setInteger("width", format.width);
        mediaFormat.setInteger("height", format.height);
        MediaFormatUtil.setCsdBuffers(mediaFormat, format.initializationData);
        MediaFormatUtil.maybeSetFloat(mediaFormat, "frame-rate", format.frameRate);
        MediaFormatUtil.maybeSetInteger(mediaFormat, "rotation-degrees", format.rotationDegrees);
        MediaFormatUtil.maybeSetColorInfo(mediaFormat, format.colorInfo);
        if (MimeTypes.VIDEO_DOLBY_VISION.equals(format.sampleMimeType) && (codecProfileAndLevel = MediaCodecUtil.getCodecProfileAndLevel(format)) != null) {
            MediaFormatUtil.maybeSetInteger(mediaFormat, Scopes.PROFILE, ((Integer) codecProfileAndLevel.first).intValue());
        }
        mediaFormat.setInteger("max-width", codecMaxValues.width);
        mediaFormat.setInteger("max-height", codecMaxValues.height);
        MediaFormatUtil.maybeSetInteger(mediaFormat, "max-input-size", codecMaxValues.inputSize);
        if (Util.SDK_INT >= 23) {
            mediaFormat.setInteger("priority", 0);
            if (codecOperatingRate != -1.0f) {
                mediaFormat.setFloat("operating-rate", codecOperatingRate);
            }
        }
        if (deviceNeedsNoPostProcessWorkaround) {
            mediaFormat.setInteger("no-post-process", 1);
            mediaFormat.setInteger("auto-frc", 0);
        }
        if (tunnelingAudioSessionId != 0) {
            configureTunnelingV21(mediaFormat, tunnelingAudioSessionId);
        }
        return mediaFormat;
    }

    protected CodecMaxValues getCodecMaxValues(MediaCodecInfo codecInfo, Format format, Format[] streamFormats) {
        int codecMaxInputSize;
        int maxWidth = format.width;
        int maxHeight = format.height;
        int maxInputSize = getMaxInputSize(codecInfo, format);
        if (streamFormats.length == 1) {
            if (maxInputSize != -1 && (codecMaxInputSize = getCodecMaxInputSize(codecInfo, format.sampleMimeType, format.width, format.height)) != -1) {
                int scaledMaxInputSize = (int) (maxInputSize * INITIAL_FORMAT_MAX_INPUT_SIZE_SCALE_FACTOR);
                maxInputSize = Math.min(scaledMaxInputSize, codecMaxInputSize);
            }
            return new CodecMaxValues(maxWidth, maxHeight, maxInputSize);
        }
        boolean haveUnknownDimensions = false;
        for (Format streamFormat : streamFormats) {
            if (codecInfo.isSeamlessAdaptationSupported(format, streamFormat, false)) {
                haveUnknownDimensions |= streamFormat.width == -1 || streamFormat.height == -1;
                maxWidth = Math.max(maxWidth, streamFormat.width);
                maxHeight = Math.max(maxHeight, streamFormat.height);
                maxInputSize = Math.max(maxInputSize, getMaxInputSize(codecInfo, streamFormat));
            }
        }
        if (haveUnknownDimensions) {
            Log.w(TAG, "Resolutions unknown. Codec max resolution: " + maxWidth + "x" + maxHeight);
            Point codecMaxSize = getCodecMaxSize(codecInfo, format);
            if (codecMaxSize != null) {
                maxWidth = Math.max(maxWidth, codecMaxSize.x);
                maxHeight = Math.max(maxHeight, codecMaxSize.y);
                maxInputSize = Math.max(maxInputSize, getCodecMaxInputSize(codecInfo, format.sampleMimeType, maxWidth, maxHeight));
                Log.w(TAG, "Codec max resolution adjusted to: " + maxWidth + "x" + maxHeight);
            }
        }
        return new CodecMaxValues(maxWidth, maxHeight, maxInputSize);
    }

    @Override // com.google.android.exoplayer2.mediacodec.MediaCodecRenderer
    protected MediaCodecRenderer.DecoderException createDecoderException(Throwable cause, MediaCodecInfo codecInfo) {
        return new VideoDecoderException(cause, codecInfo, this.surface);
    }

    private static Point getCodecMaxSize(MediaCodecInfo codecInfo, Format format) {
        int formatShortEdgePx;
        int formatLongEdgePx;
        int i = 0;
        boolean isVerticalVideo = format.height > format.width;
        int formatLongEdgePx2 = isVerticalVideo ? format.height : format.width;
        int formatShortEdgePx2 = isVerticalVideo ? format.width : format.height;
        float aspectRatio = formatShortEdgePx2 / formatLongEdgePx2;
        int[] iArr = STANDARD_LONG_EDGE_VIDEO_PX;
        int length = iArr.length;
        while (i < length) {
            int longEdgePx = iArr[i];
            int shortEdgePx = (int) (longEdgePx * aspectRatio);
            if (longEdgePx > formatLongEdgePx2 && shortEdgePx > formatShortEdgePx2) {
                if (Util.SDK_INT >= 21) {
                    Point alignedSize = codecInfo.alignVideoSizeV21(isVerticalVideo ? shortEdgePx : longEdgePx, isVerticalVideo ? longEdgePx : shortEdgePx);
                    float frameRate = format.frameRate;
                    formatLongEdgePx = formatLongEdgePx2;
                    formatShortEdgePx = formatShortEdgePx2;
                    if (codecInfo.isVideoSizeAndRateSupportedV21(alignedSize.x, alignedSize.y, frameRate)) {
                        return alignedSize;
                    }
                } else {
                    formatLongEdgePx = formatLongEdgePx2;
                    formatShortEdgePx = formatShortEdgePx2;
                    try {
                        int longEdgePx2 = Util.ceilDivide(longEdgePx, 16) * 16;
                        int shortEdgePx2 = Util.ceilDivide(shortEdgePx, 16) * 16;
                        if (longEdgePx2 * shortEdgePx2 <= MediaCodecUtil.maxH264DecodableFrameSize()) {
                            return new Point(isVerticalVideo ? shortEdgePx2 : longEdgePx2, isVerticalVideo ? longEdgePx2 : shortEdgePx2);
                        }
                    } catch (MediaCodecUtil.DecoderQueryException e) {
                        return null;
                    }
                }
                i++;
                formatLongEdgePx2 = formatLongEdgePx;
                formatShortEdgePx2 = formatShortEdgePx;
            }
            return null;
        }
        return null;
    }

    private static int getMaxInputSize(MediaCodecInfo codecInfo, Format format) {
        if (format.maxInputSize != -1) {
            int totalInitializationDataSize = 0;
            int initializationDataCount = format.initializationData.size();
            for (int i = 0; i < initializationDataCount; i++) {
                totalInitializationDataSize += format.initializationData.get(i).length;
            }
            int i2 = format.maxInputSize;
            return i2 + totalInitializationDataSize;
        }
        return getCodecMaxInputSize(codecInfo, format.sampleMimeType, format.width, format.height);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private static int getCodecMaxInputSize(MediaCodecInfo codecInfo, String sampleMimeType, int width, int height) {
        char c;
        int minCompressionRatio;
        int maxPixels;
        if (width == -1 || height == -1) {
            return -1;
        }
        switch (sampleMimeType.hashCode()) {
            case -1664118616:
                if (sampleMimeType.equals(MimeTypes.VIDEO_H263)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -1662541442:
                if (sampleMimeType.equals(MimeTypes.VIDEO_H265)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 1187890754:
                if (sampleMimeType.equals(MimeTypes.VIDEO_MP4V)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1331836730:
                if (sampleMimeType.equals("video/avc")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 1599127256:
                if (sampleMimeType.equals(MimeTypes.VIDEO_VP8)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 1599127257:
                if (sampleMimeType.equals(MimeTypes.VIDEO_VP9)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
            case 1:
                maxPixels = width * height;
                minCompressionRatio = 2;
                break;
            case 2:
                if ("BRAVIA 4K 2015".equals(Util.MODEL) || ("Amazon".equals(Util.MANUFACTURER) && ("KFSOWI".equals(Util.MODEL) || ("AFTS".equals(Util.MODEL) && codecInfo.secure)))) {
                    return -1;
                }
                maxPixels = Util.ceilDivide(width, 16) * Util.ceilDivide(height, 16) * 16 * 16;
                minCompressionRatio = 2;
                break;
                break;
            case 3:
                maxPixels = width * height;
                minCompressionRatio = 2;
                break;
            case 4:
            case 5:
                maxPixels = width * height;
                minCompressionRatio = 4;
                break;
            default:
                return -1;
        }
        return (maxPixels * 3) / (minCompressionRatio * 2);
    }

    private static boolean deviceNeedsNoPostProcessWorkaround() {
        return "NVIDIA".equals(Util.MANUFACTURER);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    protected boolean codecNeedsSetOutputSurfaceWorkaround(String name) {
        char c = 0;
        if (name.startsWith("OMX.google")) {
            return false;
        }
        synchronized (MediaCodecVideoRenderer.class) {
            if (!evaluatedDeviceNeedsSetOutputSurfaceWorkaround) {
                if ("dangal".equals(Util.DEVICE)) {
                    deviceNeedsSetOutputSurfaceWorkaround = true;
                } else {
                    char c2 = 27;
                    if (Util.SDK_INT <= 27 && "HWEML".equals(Util.DEVICE)) {
                        deviceNeedsSetOutputSurfaceWorkaround = true;
                    } else if (Util.SDK_INT < 27) {
                        String str = Util.DEVICE;
                        switch (str.hashCode()) {
                            case -2144781245:
                                if (str.equals("GIONEE_SWW1609")) {
                                    c2 = '+';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -2144781185:
                                if (str.equals("GIONEE_SWW1627")) {
                                    c2 = ',';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -2144781160:
                                if (str.equals("GIONEE_SWW1631")) {
                                    c2 = '-';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -2097309513:
                                if (str.equals("K50a40")) {
                                    c2 = '?';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -2022874474:
                                if (str.equals("CP8676_I02")) {
                                    c2 = 19;
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -1978993182:
                                if (str.equals("NX541J")) {
                                    c2 = 'M';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -1978990237:
                                if (str.equals("NX573J")) {
                                    c2 = 'N';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -1936688988:
                                if (str.equals("PGN528")) {
                                    c2 = 'X';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -1936688066:
                                if (str.equals("PGN610")) {
                                    c2 = 'Y';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -1936688065:
                                if (str.equals("PGN611")) {
                                    c2 = 'Z';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -1931988508:
                                if (str.equals("AquaPowerM")) {
                                    c2 = 11;
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -1696512866:
                                if (str.equals("XT1663")) {
                                    c2 = '{';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -1680025915:
                                if (str.equals("ComioS1")) {
                                    c2 = 18;
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -1615810839:
                                if (str.equals("Phantom6")) {
                                    c2 = '[';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -1554255044:
                                if (str.equals("vernee_M5")) {
                                    c2 = 't';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -1481772737:
                                if (str.equals("panell_dl")) {
                                    c2 = 'T';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -1481772730:
                                if (str.equals("panell_ds")) {
                                    c2 = 'U';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -1481772729:
                                if (str.equals("panell_dt")) {
                                    c2 = 'V';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -1320080169:
                                if (str.equals("GiONEE_GBL7319")) {
                                    c2 = ')';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -1217592143:
                                if (str.equals("BRAVIA_ATV2")) {
                                    c2 = 15;
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -1180384755:
                                if (str.equals("iris60")) {
                                    c2 = ';';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -1139198265:
                                if (str.equals("Slate_Pro")) {
                                    c2 = 'h';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -1052835013:
                                if (str.equals("namath")) {
                                    c2 = 'K';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -993250464:
                                if (str.equals("A10-70F")) {
                                    c2 = 3;
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -993250458:
                                if (str.equals("A10-70L")) {
                                    c2 = 4;
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -965403638:
                                if (str.equals("s905x018")) {
                                    c2 = 'j';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -958336948:
                                if (str.equals("ELUGA_Ray_X")) {
                                    c2 = 29;
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -879245230:
                                if (str.equals("tcl_eu")) {
                                    c2 = 'p';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -842500323:
                                if (str.equals("nicklaus_f")) {
                                    c2 = 'L';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -821392978:
                                if (str.equals("A7000-a")) {
                                    c2 = 7;
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -797483286:
                                if (str.equals("SVP-DTV15")) {
                                    c2 = 'i';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -794946968:
                                if (str.equals("watson")) {
                                    c2 = 'u';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -788334647:
                                if (str.equals("whyred")) {
                                    c2 = 'v';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -782144577:
                                if (str.equals("OnePlus5T")) {
                                    c2 = 'O';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -575125681:
                                if (str.equals("GiONEE_CBL7513")) {
                                    c2 = '(';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -521118391:
                                if (str.equals("GIONEE_GBL7360")) {
                                    c2 = '*';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -430914369:
                                if (str.equals("Pixi4-7_3G")) {
                                    c2 = '\\';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -290434366:
                                if (str.equals("taido_row")) {
                                    c2 = 'k';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -282781963:
                                if (str.equals("BLACK-1X")) {
                                    c2 = 14;
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -277133239:
                                if (str.equals("Z12_PRO")) {
                                    c2 = '|';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -173639913:
                                if (str.equals("ELUGA_A3_Pro")) {
                                    c2 = 26;
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case -56598463:
                                if (str.equals("woods_fn")) {
                                    c2 = 'x';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 2126:
                                if (str.equals("C1")) {
                                    c2 = 17;
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 2564:
                                if (str.equals("Q5")) {
                                    c2 = 'd';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 2715:
                                if (str.equals("V1")) {
                                    c2 = 'q';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 2719:
                                if (str.equals("V5")) {
                                    c2 = 's';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 3483:
                                if (str.equals("mh")) {
                                    c2 = 'H';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 73405:
                                if (str.equals("JGZ")) {
                                    c2 = '>';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 75739:
                                if (str.equals("M5c")) {
                                    c2 = 'D';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 76779:
                                if (str.equals("MX6")) {
                                    c2 = 'J';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 78669:
                                if (str.equals("P85")) {
                                    c2 = 'R';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 79305:
                                if (str.equals("PLE")) {
                                    c2 = '^';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 80618:
                                if (str.equals("QX1")) {
                                    c2 = 'f';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 88274:
                                if (str.equals("Z80")) {
                                    c2 = '}';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 98846:
                                if (str.equals("cv1")) {
                                    c2 = 22;
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 98848:
                                if (str.equals("cv3")) {
                                    c2 = 23;
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 99329:
                                if (str.equals("deb")) {
                                    c2 = 24;
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 101481:
                                if (str.equals("flo")) {
                                    c2 = '&';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 1513190:
                                if (str.equals("1601")) {
                                    c2 = 0;
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 1514184:
                                if (str.equals("1713")) {
                                    c2 = 1;
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 1514185:
                                if (str.equals("1714")) {
                                    c2 = 2;
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 2436959:
                                if (str.equals("P681")) {
                                    c2 = 'Q';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 2463773:
                                if (str.equals("Q350")) {
                                    c2 = '`';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 2464648:
                                if (str.equals("Q427")) {
                                    c2 = 'b';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 2689555:
                                if (str.equals("XE2X")) {
                                    c2 = 'z';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 3154429:
                                if (str.equals("fugu")) {
                                    c2 = '\'';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 3284551:
                                if (str.equals("kate")) {
                                    c2 = '@';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 3351335:
                                if (str.equals("mido")) {
                                    c2 = 'I';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 3386211:
                                if (str.equals("p212")) {
                                    c2 = 'P';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 41325051:
                                if (str.equals("MEIZU_M5")) {
                                    c2 = 'G';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 55178625:
                                if (str.equals("Aura_Note_2")) {
                                    c2 = '\r';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 61542055:
                                if (str.equals("A1601")) {
                                    c2 = 5;
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 65355429:
                                if (str.equals("E5643")) {
                                    c2 = 25;
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 66214468:
                                if (str.equals("F3111")) {
                                    c2 = 31;
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 66214470:
                                if (str.equals("F3113")) {
                                    c2 = ' ';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 66214473:
                                if (str.equals("F3116")) {
                                    c2 = '!';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 66215429:
                                if (str.equals("F3211")) {
                                    c2 = '\"';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 66215431:
                                if (str.equals("F3213")) {
                                    c2 = '#';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 66215433:
                                if (str.equals("F3215")) {
                                    c2 = '$';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 66216390:
                                if (str.equals("F3311")) {
                                    c2 = '%';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 76402249:
                                if (str.equals("PRO7S")) {
                                    c2 = '_';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 76404105:
                                if (str.equals("Q4260")) {
                                    c2 = 'a';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 76404911:
                                if (str.equals("Q4310")) {
                                    c2 = 'c';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 80963634:
                                if (str.equals("V23GB")) {
                                    c2 = 'r';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 82882791:
                                if (str.equals("X3_HK")) {
                                    c2 = 'y';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 98715550:
                                if (str.equals("i9031")) {
                                    c2 = '8';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 101370885:
                                if (str.equals("l5460")) {
                                    c2 = 'A';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 102844228:
                                if (str.equals("le_x6")) {
                                    c2 = 'B';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 165221241:
                                if (str.equals("A2016a40")) {
                                    c2 = 6;
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 182191441:
                                if (str.equals("CPY83_I00")) {
                                    c2 = 21;
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 245388979:
                                if (str.equals("marino_f")) {
                                    c2 = 'F';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 287431619:
                                if (str.equals("griffin")) {
                                    c2 = '1';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 307593612:
                                if (str.equals("A7010a48")) {
                                    c2 = '\t';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 308517133:
                                if (str.equals("A7020a48")) {
                                    c2 = '\n';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 316215098:
                                if (str.equals("TB3-730F")) {
                                    c2 = 'l';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 316215116:
                                if (str.equals("TB3-730X")) {
                                    c2 = 'm';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 316246811:
                                if (str.equals("TB3-850F")) {
                                    c2 = 'n';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 316246818:
                                if (str.equals("TB3-850M")) {
                                    c2 = 'o';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 407160593:
                                if (str.equals("Pixi5-10_4G")) {
                                    c2 = ']';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 507412548:
                                if (str.equals("QM16XE_U")) {
                                    c2 = 'e';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 793982701:
                                if (str.equals("GIONEE_WBL5708")) {
                                    c2 = '.';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 794038622:
                                if (str.equals("GIONEE_WBL7365")) {
                                    c2 = '/';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 794040393:
                                if (str.equals("GIONEE_WBL7519")) {
                                    c2 = '0';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 835649806:
                                if (str.equals("manning")) {
                                    c2 = 'E';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 917340916:
                                if (str.equals("A7000plus")) {
                                    c2 = '\b';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 958008161:
                                if (str.equals("j2xlteins")) {
                                    c2 = '=';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 1060579533:
                                if (str.equals("panell_d")) {
                                    c2 = 'S';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 1150207623:
                                if (str.equals("LS-5017")) {
                                    c2 = 'C';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 1176899427:
                                if (str.equals("itel_S41")) {
                                    c2 = '<';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 1280332038:
                                if (str.equals("hwALE-H")) {
                                    c2 = '3';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 1306947716:
                                if (str.equals("EverStar_S")) {
                                    c2 = 30;
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 1349174697:
                                if (str.equals("htc_e56ml_dtul")) {
                                    c2 = '2';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 1522194893:
                                if (str.equals("woods_f")) {
                                    c2 = 'w';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 1691543273:
                                if (str.equals("CPH1609")) {
                                    c2 = 20;
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 1709443163:
                                if (str.equals("iball8735_9806")) {
                                    c2 = '9';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 1865889110:
                                if (str.equals("santoni")) {
                                    c2 = 'g';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 1906253259:
                                if (str.equals("PB2-670M")) {
                                    c2 = 'W';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 1977196784:
                                if (str.equals("Infinix-X572")) {
                                    c2 = ':';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 2006372676:
                                if (str.equals("BRAVIA_ATV3_4K")) {
                                    c2 = 16;
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 2029784656:
                                if (str.equals("HWBLN-H")) {
                                    c2 = '4';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 2030379515:
                                if (str.equals("HWCAM-H")) {
                                    c2 = '5';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 2033393791:
                                if (str.equals("ASUS_X00AD_2")) {
                                    c2 = '\f';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 2047190025:
                                if (str.equals("ELUGA_Note")) {
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 2047252157:
                                if (str.equals("ELUGA_Prim")) {
                                    c2 = 28;
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 2048319463:
                                if (str.equals("HWVNS-H")) {
                                    c2 = '6';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 2048855701:
                                if (str.equals("HWWAS-H")) {
                                    c2 = '7';
                                    break;
                                }
                                c2 = 65535;
                                break;
                            default:
                                c2 = 65535;
                                break;
                        }
                        switch (c2) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                            case 7:
                            case '\b':
                            case '\t':
                            case '\n':
                            case 11:
                            case '\f':
                            case '\r':
                            case 14:
                            case 15:
                            case 16:
                            case 17:
                            case 18:
                            case 19:
                            case 20:
                            case 21:
                            case 22:
                            case 23:
                            case 24:
                            case 25:
                            case 26:
                            case 27:
                            case 28:
                            case NalUnitTypes.NAL_TYPE_RSV_VCL29 /* 29 */:
                            case 30:
                            case 31:
                            case ' ':
                            case '!':
                            case '\"':
                            case '#':
                            case '$':
                            case '%':
                            case '&':
                            case '\'':
                            case '(':
                            case ')':
                            case '*':
                            case '+':
                            case ',':
                            case '-':
                            case '.':
                            case '/':
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case UndoView.ACTION_USERNAME_COPIED /* 56 */:
                            case UndoView.ACTION_HASHTAG_COPIED /* 57 */:
                            case UndoView.ACTION_TEXT_COPIED /* 58 */:
                            case ';':
                            case UndoView.ACTION_PHONE_COPIED /* 60 */:
                            case UndoView.ACTION_SHARE_BACKGROUND /* 61 */:
                            case '>':
                            case HtmlCompat.FROM_HTML_MODE_COMPACT /* 63 */:
                            case '@':
                            case VoIPService.CALL_MIN_LAYER /* 65 */:
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case UndoView.ACTION_AUTO_DELETE_ON /* 70 */:
                            case 'G':
                            case 'H':
                            case 'I':
                            case UndoView.ACTION_REPORT_SENT /* 74 */:
                            case UndoView.ACTION_GIGAGROUP_CANCEL /* 75 */:
                            case UndoView.ACTION_GIGAGROUP_SUCCESS /* 76 */:
                            case UndoView.ACTION_PAYMENT_SUCCESS /* 77 */:
                            case UndoView.ACTION_PIN_DIALOGS /* 78 */:
                            case UndoView.ACTION_UNPIN_DIALOGS /* 79 */:
                            case UndoView.ACTION_EMAIL_COPIED /* 80 */:
                            case UndoView.ACTION_CLEAR_DATES /* 81 */:
                            case UndoView.ACTION_PREVIEW_MEDIA_DESELECTED /* 82 */:
                            case 'S':
                            case 'T':
                            case 'U':
                            case 'V':
                            case 'W':
                            case 'X':
                            case TsExtractor.TS_STREAM_TYPE_DVBSUBS /* 89 */:
                            case 'Z':
                            case '[':
                            case '\\':
                            case ']':
                            case '^':
                            case '_':
                            case '`':
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                            case 'g':
                            case LocationRequest.PRIORITY_LOW_POWER /* 104 */:
                            case LocationRequest.PRIORITY_NO_POWER /* 105 */:
                            case 'j':
                            case 'k':
                            case 'l':
                            case 'm':
                            case 'n':
                            case 'o':
                            case 'p':
                            case 'q':
                            case 'r':
                            case 's':
                            case 't':
                            case 'u':
                            case 'v':
                            case 'w':
                            case 'x':
                            case 'y':
                            case 'z':
                            case '{':
                            case '|':
                            case ErrorLogHelper.MAX_PROPERTY_ITEM_LENGTH /* 125 */:
                                deviceNeedsSetOutputSurfaceWorkaround = true;
                                break;
                        }
                        String str2 = Util.MODEL;
                        switch (str2.hashCode()) {
                            case -594534941:
                                if (str2.equals("JSN-L21")) {
                                    c = 2;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 2006354:
                                if (str2.equals("AFTA")) {
                                    break;
                                }
                                c = 65535;
                                break;
                            case 2006367:
                                if (str2.equals("AFTN")) {
                                    c = 1;
                                    break;
                                }
                                c = 65535;
                                break;
                            default:
                                c = 65535;
                                break;
                        }
                        switch (c) {
                            case 0:
                            case 1:
                            case 2:
                                deviceNeedsSetOutputSurfaceWorkaround = true;
                                break;
                        }
                    }
                }
                evaluatedDeviceNeedsSetOutputSurfaceWorkaround = true;
            }
        }
        return deviceNeedsSetOutputSurfaceWorkaround;
    }

    protected Surface getSurface() {
        return this.surface;
    }

    /* loaded from: classes3.dex */
    public static final class CodecMaxValues {
        public final int height;
        public final int inputSize;
        public final int width;

        public CodecMaxValues(int width, int height, int inputSize) {
            this.width = width;
            this.height = height;
            this.inputSize = inputSize;
        }
    }

    /* loaded from: classes3.dex */
    public final class OnFrameRenderedListenerV23 implements MediaCodec.OnFrameRenderedListener, Handler.Callback {
        private static final int HANDLE_FRAME_RENDERED = 0;
        private final Handler handler;

        public OnFrameRenderedListenerV23(MediaCodec codec) {
            MediaCodecVideoRenderer.this = r1;
            Handler handler = new Handler(this);
            this.handler = handler;
            codec.setOnFrameRenderedListener(this, handler);
        }

        @Override // android.media.MediaCodec.OnFrameRenderedListener
        public void onFrameRendered(MediaCodec codec, long presentationTimeUs, long nanoTime) {
            if (Util.SDK_INT < 30) {
                Message message = Message.obtain(this.handler, 0, (int) (presentationTimeUs >> 32), (int) presentationTimeUs);
                this.handler.sendMessageAtFrontOfQueue(message);
                return;
            }
            handleFrameRendered(presentationTimeUs);
        }

        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    handleFrameRendered(Util.toLong(message.arg1, message.arg2));
                    return true;
                default:
                    return false;
            }
        }

        private void handleFrameRendered(long presentationTimeUs) {
            if (this != MediaCodecVideoRenderer.this.tunnelingOnFrameRenderedListener) {
                return;
            }
            if (presentationTimeUs == Long.MAX_VALUE) {
                MediaCodecVideoRenderer.this.onProcessedTunneledEndOfStream();
            } else {
                MediaCodecVideoRenderer.this.onProcessedTunneledBuffer(presentationTimeUs);
            }
        }
    }
}
