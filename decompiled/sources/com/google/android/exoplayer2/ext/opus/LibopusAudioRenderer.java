package com.google.android.exoplayer2.ext.opus;

import android.os.Handler;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.audio.AudioProcessor;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.audio.SimpleDecoderAudioRenderer;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.ExoMediaCrypto;
import com.google.android.exoplayer2.util.MimeTypes;
/* loaded from: classes3.dex */
public class LibopusAudioRenderer extends SimpleDecoderAudioRenderer {
    private static final int DEFAULT_INPUT_BUFFER_SIZE = 5760;
    private static final int NUM_BUFFERS = 16;
    private int channelCount;
    private int sampleRate;

    public LibopusAudioRenderer() {
        this(null, null, new AudioProcessor[0]);
    }

    public LibopusAudioRenderer(Handler eventHandler, AudioRendererEventListener eventListener, AudioProcessor... audioProcessors) {
        super(eventHandler, eventListener, audioProcessors);
    }

    @Deprecated
    public LibopusAudioRenderer(Handler eventHandler, AudioRendererEventListener eventListener, DrmSessionManager<ExoMediaCrypto> drmSessionManager, boolean playClearSamplesWithoutKeys, AudioProcessor... audioProcessors) {
        super(eventHandler, eventListener, null, drmSessionManager, playClearSamplesWithoutKeys, audioProcessors);
    }

    @Override // com.google.android.exoplayer2.audio.SimpleDecoderAudioRenderer
    protected int supportsFormatInternal(DrmSessionManager<ExoMediaCrypto> drmSessionManager, Format format) {
        boolean drmIsSupported = format.drmInitData == null || OpusLibrary.matchesExpectedExoMediaCryptoType(format.exoMediaCryptoType) || (format.exoMediaCryptoType == null && supportsFormatDrm(drmSessionManager, format.drmInitData));
        if (!MimeTypes.AUDIO_OPUS.equalsIgnoreCase(format.sampleMimeType)) {
            return 0;
        }
        if (!supportsOutput(format.channelCount, 2)) {
            return 1;
        }
        return !drmIsSupported ? 2 : 4;
    }

    @Override // com.google.android.exoplayer2.audio.SimpleDecoderAudioRenderer
    public OpusDecoder createDecoder(Format format, ExoMediaCrypto mediaCrypto) throws OpusDecoderException {
        int initialInputBufferSize = format.maxInputSize != -1 ? format.maxInputSize : DEFAULT_INPUT_BUFFER_SIZE;
        OpusDecoder decoder = new OpusDecoder(16, 16, initialInputBufferSize, format.initializationData, mediaCrypto);
        this.channelCount = decoder.getChannelCount();
        this.sampleRate = decoder.getSampleRate();
        return decoder;
    }

    @Override // com.google.android.exoplayer2.audio.SimpleDecoderAudioRenderer
    protected Format getOutputFormat() {
        return Format.createAudioSampleFormat(null, MimeTypes.AUDIO_RAW, null, -1, -1, this.channelCount, this.sampleRate, 2, null, null, 0, null);
    }
}
