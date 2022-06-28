package com.google.android.exoplayer2.ext.ffmpeg;

import android.os.Handler;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.audio.AudioProcessor;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.audio.AudioSink;
import com.google.android.exoplayer2.audio.DefaultAudioSink;
import com.google.android.exoplayer2.audio.SimpleDecoderAudioRenderer;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.ExoMediaCrypto;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import java.util.Collections;
/* loaded from: classes3.dex */
public final class FfmpegAudioRenderer extends SimpleDecoderAudioRenderer {
    private static final int DEFAULT_INPUT_BUFFER_SIZE = 5760;
    private static final int NUM_BUFFERS = 16;
    private FfmpegDecoder decoder;
    private final boolean enableFloatOutput;

    public FfmpegAudioRenderer() {
        this(null, null, new AudioProcessor[0]);
    }

    public FfmpegAudioRenderer(Handler eventHandler, AudioRendererEventListener eventListener, AudioProcessor... audioProcessors) {
        this(eventHandler, eventListener, new DefaultAudioSink(null, audioProcessors), false);
    }

    public FfmpegAudioRenderer(Handler eventHandler, AudioRendererEventListener eventListener, AudioSink audioSink, boolean enableFloatOutput) {
        super(eventHandler, eventListener, null, false, audioSink);
        this.enableFloatOutput = enableFloatOutput;
    }

    @Override // com.google.android.exoplayer2.audio.SimpleDecoderAudioRenderer
    protected int supportsFormatInternal(DrmSessionManager<ExoMediaCrypto> drmSessionManager, Format format) {
        Assertions.checkNotNull(format.sampleMimeType);
        if (!FfmpegLibrary.supportsFormat(format.sampleMimeType) || !isOutputSupported(format)) {
            return 1;
        }
        if (!supportsFormatDrm(drmSessionManager, format.drmInitData)) {
            return 2;
        }
        return 4;
    }

    @Override // com.google.android.exoplayer2.BaseRenderer, com.google.android.exoplayer2.RendererCapabilities
    public final int supportsMixedMimeTypeAdaptation() throws ExoPlaybackException {
        return 8;
    }

    @Override // com.google.android.exoplayer2.audio.SimpleDecoderAudioRenderer
    public FfmpegDecoder createDecoder(Format format, ExoMediaCrypto mediaCrypto) throws FfmpegDecoderException {
        int initialInputBufferSize = format.maxInputSize != -1 ? format.maxInputSize : DEFAULT_INPUT_BUFFER_SIZE;
        FfmpegDecoder ffmpegDecoder = new FfmpegDecoder(16, 16, initialInputBufferSize, format, shouldUseFloatOutput(format));
        this.decoder = ffmpegDecoder;
        return ffmpegDecoder;
    }

    @Override // com.google.android.exoplayer2.audio.SimpleDecoderAudioRenderer
    public Format getOutputFormat() {
        Assertions.checkNotNull(this.decoder);
        int channelCount = this.decoder.getChannelCount();
        int sampleRate = this.decoder.getSampleRate();
        int encoding = this.decoder.getEncoding();
        return Format.createAudioSampleFormat(null, MimeTypes.AUDIO_RAW, null, -1, -1, channelCount, sampleRate, encoding, Collections.emptyList(), null, 0, null);
    }

    private boolean isOutputSupported(Format inputFormat) {
        return shouldUseFloatOutput(inputFormat) || supportsOutput(inputFormat.channelCount, 2);
    }

    private boolean shouldUseFloatOutput(Format inputFormat) {
        Assertions.checkNotNull(inputFormat.sampleMimeType);
        if (!this.enableFloatOutput || !supportsOutput(inputFormat.channelCount, 4)) {
            return false;
        }
        String str = inputFormat.sampleMimeType;
        char c = 65535;
        switch (str.hashCode()) {
            case 187078296:
                if (str.equals(MimeTypes.AUDIO_AC3)) {
                    c = 1;
                    break;
                }
                break;
            case 187094639:
                if (str.equals(MimeTypes.AUDIO_RAW)) {
                    c = 0;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return inputFormat.pcmEncoding == 536870912 || inputFormat.pcmEncoding == 805306368 || inputFormat.pcmEncoding == 4;
            case 1:
                return false;
            default:
                return true;
        }
    }
}
