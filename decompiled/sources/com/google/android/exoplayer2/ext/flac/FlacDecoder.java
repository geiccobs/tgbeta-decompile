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
/* loaded from: classes.dex */
final class FlacDecoder extends SimpleDecoder<DecoderInputBuffer, SimpleOutputBuffer, FlacDecoderException> {
    private final FlacDecoderJni decoderJni;
    private final FlacStreamMetadata streamMetadata;

    @Override // com.google.android.exoplayer2.decoder.Decoder
    public String getName() {
        return "libflac";
    }

    public FlacDecoder(int i, int i2, int i3, List<byte[]> list) throws FlacDecoderException {
        super(new DecoderInputBuffer[i], new SimpleOutputBuffer[i2]);
        Throwable e;
        if (list.size() != 1) {
            throw new FlacDecoderException("Initialization data must be of length 1");
        }
        FlacDecoderJni flacDecoderJni = new FlacDecoderJni();
        this.decoderJni = flacDecoderJni;
        flacDecoderJni.setData(ByteBuffer.wrap(list.get(0)));
        try {
            FlacStreamMetadata decodeStreamMetadata = flacDecoderJni.decodeStreamMetadata();
            this.streamMetadata = decodeStreamMetadata;
            setInitialInputBufferSize(i3 == -1 ? decodeStreamMetadata.maxFrameSize : i3);
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

    @Override // com.google.android.exoplayer2.decoder.SimpleDecoder
    protected DecoderInputBuffer createInputBuffer() {
        return new DecoderInputBuffer(1);
    }

    @Override // com.google.android.exoplayer2.decoder.SimpleDecoder
    public SimpleOutputBuffer createOutputBuffer() {
        return new SimpleOutputBuffer(this);
    }

    @Override // com.google.android.exoplayer2.decoder.SimpleDecoder
    public FlacDecoderException createUnexpectedDecodeException(Throwable th) {
        return new FlacDecoderException("Unexpected decode error", th);
    }

    public FlacDecoderException decode(DecoderInputBuffer decoderInputBuffer, SimpleOutputBuffer simpleOutputBuffer, boolean z) {
        Throwable e;
        if (z) {
            this.decoderJni.flush();
        }
        this.decoderJni.setData((ByteBuffer) Util.castNonNull(decoderInputBuffer.data));
        try {
            this.decoderJni.decodeSample(simpleOutputBuffer.init(decoderInputBuffer.timeUs, this.streamMetadata.getMaxDecodedFrameSize()));
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
