package com.google.android.exoplayer2.ext.opus;

import android.os.Handler;
import com.google.android.exoplayer2.BaseRenderer;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.audio.AudioProcessor;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.audio.SimpleDecoderAudioRenderer;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.ExoMediaCrypto;
/* loaded from: classes.dex */
public class LibopusAudioRenderer extends SimpleDecoderAudioRenderer {
    private static final int DEFAULT_INPUT_BUFFER_SIZE = 5760;
    private static final int NUM_BUFFERS = 16;
    private int channelCount;
    private int sampleRate;

    @Override // com.google.android.exoplayer2.BaseRenderer, com.google.android.exoplayer2.Renderer
    public /* bridge */ /* synthetic */ void setOperatingRate(float f) throws ExoPlaybackException {
        Renderer.CC.$default$setOperatingRate(this, f);
    }

    public LibopusAudioRenderer() {
        this(null, null, new AudioProcessor[0]);
    }

    public LibopusAudioRenderer(Handler handler, AudioRendererEventListener audioRendererEventListener, AudioProcessor... audioProcessorArr) {
        super(handler, audioRendererEventListener, audioProcessorArr);
    }

    @Deprecated
    public LibopusAudioRenderer(Handler handler, AudioRendererEventListener audioRendererEventListener, DrmSessionManager<ExoMediaCrypto> drmSessionManager, boolean z, AudioProcessor... audioProcessorArr) {
        super(handler, audioRendererEventListener, null, drmSessionManager, z, audioProcessorArr);
    }

    @Override // com.google.android.exoplayer2.audio.SimpleDecoderAudioRenderer
    protected int supportsFormatInternal(DrmSessionManager<ExoMediaCrypto> drmSessionManager, Format format) {
        boolean z = format.drmInitData == null || OpusLibrary.matchesExpectedExoMediaCryptoType(format.exoMediaCryptoType) || (format.exoMediaCryptoType == null && BaseRenderer.supportsFormatDrm(drmSessionManager, format.drmInitData));
        if (!"audio/opus".equalsIgnoreCase(format.sampleMimeType)) {
            return 0;
        }
        if (!supportsOutput(format.channelCount, 2)) {
            return 1;
        }
        return !z ? 2 : 4;
    }

    @Override // com.google.android.exoplayer2.audio.SimpleDecoderAudioRenderer
    public OpusDecoder createDecoder(Format format, ExoMediaCrypto exoMediaCrypto) throws OpusDecoderException {
        int i = format.maxInputSize;
        OpusDecoder opusDecoder = new OpusDecoder(16, 16, i != -1 ? i : DEFAULT_INPUT_BUFFER_SIZE, format.initializationData, exoMediaCrypto);
        this.channelCount = opusDecoder.getChannelCount();
        this.sampleRate = opusDecoder.getSampleRate();
        return opusDecoder;
    }

    @Override // com.google.android.exoplayer2.audio.SimpleDecoderAudioRenderer
    protected Format getOutputFormat() {
        return Format.createAudioSampleFormat(null, "audio/raw", null, -1, -1, this.channelCount, this.sampleRate, 2, null, null, 0, null);
    }
}
