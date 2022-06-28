package com.google.android.exoplayer2.ext.flac;

import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.decoder.SimpleDecoder;
import com.google.android.exoplayer2.decoder.SimpleOutputBuffer;
import com.google.android.exoplayer2.ext.flac.FlacDecoderJni;
import com.google.android.exoplayer2.util.FlacStreamMetadata;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
/* loaded from: classes3.dex */
final class FlacDecoder extends SimpleDecoder<DecoderInputBuffer, SimpleOutputBuffer, FlacDecoderException> {
    private final FlacDecoderJni decoderJni;
    private final FlacStreamMetadata streamMetadata;

    public FlacDecoder(int numInputBuffers, int numOutputBuffers, int maxInputBufferSize, List<byte[]> initializationData) throws FlacDecoderException {
        super(new DecoderInputBuffer[numInputBuffers], new SimpleOutputBuffer[numOutputBuffers]);
        Exception e;
        if (initializationData.size() != 1) {
            throw new FlacDecoderException("Initialization data must be of length 1");
        }
        FlacDecoderJni flacDecoderJni = new FlacDecoderJni();
        this.decoderJni = flacDecoderJni;
        flacDecoderJni.setData(ByteBuffer.wrap(initializationData.get(0)));
        try {
            FlacStreamMetadata decodeStreamMetadata = flacDecoderJni.decodeStreamMetadata();
            this.streamMetadata = decodeStreamMetadata;
            int initialInputBufferSize = maxInputBufferSize != -1 ? maxInputBufferSize : decodeStreamMetadata.maxFrameSize;
            setInitialInputBufferSize(initialInputBufferSize);
        } catch (ParserException e2) {
            throw new FlacDecoderException("Failed to decode StreamInfo", e2);
        } catch (IOException e3) {
            e = e3;
            throw new IllegalStateException(e);
        } catch (InterruptedException e4) {
            e = e4;
            throw new IllegalStateException(e);
        }
    }

    @Override // com.google.android.exoplayer2.decoder.Decoder
    public String getName() {
        return "libflac";
    }

    @Override // com.google.android.exoplayer2.decoder.SimpleDecoder
    protected DecoderInputBuffer createInputBuffer() {
        return new DecoderInputBuffer(1);
    }

    @Override // com.google.android.exoplayer2.decoder.SimpleDecoder
    public SimpleOutputBuffer createOutputBuffer() {
        return new SimpleOutputBuffer(this);
    }

    @Override // com.google.android.exoplayer2.decoder.SimpleDecoder
    public FlacDecoderException createUnexpectedDecodeException(Throwable error) {
        return new FlacDecoderException("Unexpected decode error", error);
    }

    public FlacDecoderException decode(DecoderInputBuffer inputBuffer, SimpleOutputBuffer outputBuffer, boolean reset) {
        Exception e;
        if (reset) {
            this.decoderJni.flush();
        }
        this.decoderJni.setData((ByteBuffer) Util.castNonNull(inputBuffer.data));
        ByteBuffer outputData = outputBuffer.init(inputBuffer.timeUs, this.streamMetadata.getMaxDecodedFrameSize());
        try {
            this.decoderJni.decodeSample(outputData);
            return null;
        } catch (FlacDecoderJni.FlacFrameDecodeException e2) {
            return new FlacDecoderException("Frame decoding failed", e2);
        } catch (IOException e3) {
            e = e3;
            throw new IllegalStateException(e);
        } catch (InterruptedException e4) {
            e = e4;
            throw new IllegalStateException(e);
        }
    }

    @Override // com.google.android.exoplayer2.decoder.SimpleDecoder, com.google.android.exoplayer2.decoder.Decoder
    public void release() {
        super.release();
        this.decoderJni.release();
    }

    public FlacStreamMetadata getStreamMetadata() {
        return this.streamMetadata;
    }
}
