package com.google.android.exoplayer2.ext.flac;

import android.os.Handler;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.audio.AudioProcessor;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.audio.AudioSink;
import com.google.android.exoplayer2.audio.SimpleDecoderAudioRenderer;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.ExoMediaCrypto;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.FlacStreamMetadata;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
/* loaded from: classes3.dex */
public final class LibflacAudioRenderer extends SimpleDecoderAudioRenderer {
    private static final int NUM_BUFFERS = 16;
    private FlacStreamMetadata streamMetadata;

    public LibflacAudioRenderer() {
        this((Handler) null, (AudioRendererEventListener) null, new AudioProcessor[0]);
    }

    public LibflacAudioRenderer(Handler eventHandler, AudioRendererEventListener eventListener, AudioProcessor... audioProcessors) {
        super(eventHandler, eventListener, audioProcessors);
    }

    public LibflacAudioRenderer(Handler eventHandler, AudioRendererEventListener eventListener, AudioSink audioSink) {
        super(eventHandler, eventListener, null, false, audioSink);
    }

    @Override // com.google.android.exoplayer2.audio.SimpleDecoderAudioRenderer
    protected int supportsFormatInternal(DrmSessionManager<ExoMediaCrypto> drmSessionManager, Format format) {
        int streamMetadataOffset;
        if (!MimeTypes.AUDIO_FLAC.equalsIgnoreCase(format.sampleMimeType)) {
            return 0;
        }
        if (format.initializationData.isEmpty()) {
            streamMetadataOffset = 2;
        } else {
            FlacStreamMetadata streamMetadata = new FlacStreamMetadata(format.initializationData.get(0), 8);
            streamMetadataOffset = Util.getPcmEncoding(streamMetadata.bitsPerSample);
        }
        if (!supportsOutput(format.channelCount, streamMetadataOffset)) {
            return 1;
        }
        if (!supportsFormatDrm(drmSessionManager, format.drmInitData)) {
            return 2;
        }
        return 4;
    }

    @Override // com.google.android.exoplayer2.audio.SimpleDecoderAudioRenderer
    public FlacDecoder createDecoder(Format format, ExoMediaCrypto mediaCrypto) throws FlacDecoderException {
        FlacDecoder decoder = new FlacDecoder(16, 16, format.maxInputSize, format.initializationData);
        this.streamMetadata = decoder.getStreamMetadata();
        return decoder;
    }

    @Override // com.google.android.exoplayer2.audio.SimpleDecoderAudioRenderer
    protected Format getOutputFormat() {
        Assertions.checkNotNull(this.streamMetadata);
        return Format.createAudioSampleFormat(null, MimeTypes.AUDIO_RAW, null, -1, -1, this.streamMetadata.channels, this.streamMetadata.sampleRate, Util.getPcmEncoding(this.streamMetadata.bitsPerSample), null, null, 0, null);
    }
}
