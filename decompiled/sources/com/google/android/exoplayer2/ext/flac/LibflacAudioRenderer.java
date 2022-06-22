package com.google.android.exoplayer2.ext.flac;

import android.os.Handler;
import com.google.android.exoplayer2.BaseRenderer;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.audio.AudioProcessor;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.audio.AudioSink;
import com.google.android.exoplayer2.audio.SimpleDecoderAudioRenderer;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.ExoMediaCrypto;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.FlacStreamMetadata;
import com.google.android.exoplayer2.util.Util;
/* loaded from: classes.dex */
public final class LibflacAudioRenderer extends SimpleDecoderAudioRenderer {
    private static final int NUM_BUFFERS = 16;
    private FlacStreamMetadata streamMetadata;

    @Override // com.google.android.exoplayer2.BaseRenderer, com.google.android.exoplayer2.Renderer
    public /* bridge */ /* synthetic */ void setOperatingRate(float f) throws ExoPlaybackException {
        Renderer.CC.$default$setOperatingRate(this, f);
    }

    public LibflacAudioRenderer() {
        this((Handler) null, (AudioRendererEventListener) null, new AudioProcessor[0]);
    }

    public LibflacAudioRenderer(Handler handler, AudioRendererEventListener audioRendererEventListener, AudioProcessor... audioProcessorArr) {
        super(handler, audioRendererEventListener, audioProcessorArr);
    }

    public LibflacAudioRenderer(Handler handler, AudioRendererEventListener audioRendererEventListener, AudioSink audioSink) {
        super(handler, audioRendererEventListener, null, false, audioSink);
    }

    @Override // com.google.android.exoplayer2.audio.SimpleDecoderAudioRenderer
    protected int supportsFormatInternal(DrmSessionManager<ExoMediaCrypto> drmSessionManager, Format format) {
        if (!"audio/flac".equalsIgnoreCase(format.sampleMimeType)) {
            return 0;
        }
        if (!supportsOutput(format.channelCount, format.initializationData.isEmpty() ? 2 : Util.getPcmEncoding(new FlacStreamMetadata(format.initializationData.get(0), 8).bitsPerSample))) {
            return 1;
        }
        return !BaseRenderer.supportsFormatDrm(drmSessionManager, format.drmInitData) ? 2 : 4;
    }

    @Override // com.google.android.exoplayer2.audio.SimpleDecoderAudioRenderer
    public FlacDecoder createDecoder(Format format, ExoMediaCrypto exoMediaCrypto) throws FlacDecoderException {
        FlacDecoder flacDecoder = new FlacDecoder(16, 16, format.maxInputSize, format.initializationData);
        this.streamMetadata = flacDecoder.getStreamMetadata();
        return flacDecoder;
    }

    @Override // com.google.android.exoplayer2.audio.SimpleDecoderAudioRenderer
    protected Format getOutputFormat() {
        Assertions.checkNotNull(this.streamMetadata);
        FlacStreamMetadata flacStreamMetadata = this.streamMetadata;
        return Format.createAudioSampleFormat(null, "audio/raw", null, -1, -1, flacStreamMetadata.channels, flacStreamMetadata.sampleRate, Util.getPcmEncoding(flacStreamMetadata.bitsPerSample), null, null, 0, null);
    }
}
