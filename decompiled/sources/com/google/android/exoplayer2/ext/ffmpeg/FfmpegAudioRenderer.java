package com.google.android.exoplayer2.ext.ffmpeg;

import android.os.Handler;
import com.google.android.exoplayer2.BaseRenderer;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.audio.AudioProcessor;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.audio.AudioSink;
import com.google.android.exoplayer2.audio.DefaultAudioSink;
import com.google.android.exoplayer2.audio.SimpleDecoderAudioRenderer;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.ExoMediaCrypto;
import com.google.android.exoplayer2.util.Assertions;
import java.util.Collections;
/* loaded from: classes.dex */
public final class FfmpegAudioRenderer extends SimpleDecoderAudioRenderer {
    private static final int DEFAULT_INPUT_BUFFER_SIZE = 5760;
    private static final int NUM_BUFFERS = 16;
    private FfmpegDecoder decoder;
    private final boolean enableFloatOutput;

    @Override // com.google.android.exoplayer2.BaseRenderer, com.google.android.exoplayer2.Renderer
    public /* bridge */ /* synthetic */ void setOperatingRate(float f) throws ExoPlaybackException {
        Renderer.CC.$default$setOperatingRate(this, f);
    }

    @Override // com.google.android.exoplayer2.BaseRenderer, com.google.android.exoplayer2.RendererCapabilities
    public final int supportsMixedMimeTypeAdaptation() throws ExoPlaybackException {
        return 8;
    }

    public FfmpegAudioRenderer() {
        this(null, null, new AudioProcessor[0]);
    }

    public FfmpegAudioRenderer(Handler handler, AudioRendererEventListener audioRendererEventListener, AudioProcessor... audioProcessorArr) {
        this(handler, audioRendererEventListener, new DefaultAudioSink(null, audioProcessorArr), false);
    }

    public FfmpegAudioRenderer(Handler handler, AudioRendererEventListener audioRendererEventListener, AudioSink audioSink, boolean z) {
        super(handler, audioRendererEventListener, null, false, audioSink);
        this.enableFloatOutput = z;
    }

    @Override // com.google.android.exoplayer2.audio.SimpleDecoderAudioRenderer
    protected int supportsFormatInternal(DrmSessionManager<ExoMediaCrypto> drmSessionManager, Format format) {
        Assertions.checkNotNull(format.sampleMimeType);
        if (!FfmpegLibrary.supportsFormat(format.sampleMimeType) || !isOutputSupported(format)) {
            return 1;
        }
        return !BaseRenderer.supportsFormatDrm(drmSessionManager, format.drmInitData) ? 2 : 4;
    }

    @Override // com.google.android.exoplayer2.audio.SimpleDecoderAudioRenderer
    public FfmpegDecoder createDecoder(Format format, ExoMediaCrypto exoMediaCrypto) throws FfmpegDecoderException {
        int i = format.maxInputSize;
        FfmpegDecoder ffmpegDecoder = new FfmpegDecoder(16, 16, i != -1 ? i : DEFAULT_INPUT_BUFFER_SIZE, format, shouldUseFloatOutput(format));
        this.decoder = ffmpegDecoder;
        return ffmpegDecoder;
    }

    @Override // com.google.android.exoplayer2.audio.SimpleDecoderAudioRenderer
    public Format getOutputFormat() {
        Assertions.checkNotNull(this.decoder);
        return Format.createAudioSampleFormat(null, "audio/raw", null, -1, -1, this.decoder.getChannelCount(), this.decoder.getSampleRate(), this.decoder.getEncoding(), Collections.emptyList(), null, 0, null);
    }

    private boolean isOutputSupported(Format format) {
        return shouldUseFloatOutput(format) || supportsOutput(format.channelCount, 2);
    }

    private boolean shouldUseFloatOutput(Format format) {
        int i;
        Assertions.checkNotNull(format.sampleMimeType);
        if (!this.enableFloatOutput || !supportsOutput(format.channelCount, 4)) {
            return false;
        }
        String str = format.sampleMimeType;
        str.hashCode();
        if (str.equals("audio/ac3")) {
            return false;
        }
        return !str.equals("audio/raw") || (i = format.pcmEncoding) == 536870912 || i == 805306368 || i == 4;
    }
}
