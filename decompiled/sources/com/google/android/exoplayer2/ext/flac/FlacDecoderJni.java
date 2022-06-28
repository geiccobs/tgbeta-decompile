package com.google.android.exoplayer2.ext.flac;

import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.SeekPoint;
import com.google.android.exoplayer2.util.FlacStreamMetadata;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.nio.ByteBuffer;
/* loaded from: classes3.dex */
public final class FlacDecoderJni {
    private static final int TEMP_BUFFER_SIZE = 8192;
    private ByteBuffer byteBufferData;
    private boolean endOfExtractorInput;
    private ExtractorInput extractorInput;
    private final long nativeDecoderContext;
    private byte[] tempBuffer;

    private native FlacStreamMetadata flacDecodeMetadata(long j) throws IOException, InterruptedException;

    private native int flacDecodeToArray(long j, byte[] bArr) throws IOException, InterruptedException;

    private native int flacDecodeToBuffer(long j, ByteBuffer byteBuffer) throws IOException, InterruptedException;

    private native void flacFlush(long j);

    private native long flacGetDecodePosition(long j);

    private native long flacGetLastFrameFirstSampleIndex(long j);

    private native long flacGetLastFrameTimestamp(long j);

    private native long flacGetNextFrameFirstSampleIndex(long j);

    private native boolean flacGetSeekPoints(long j, long j2, long[] jArr);

    private native String flacGetStateString(long j);

    private native long flacInit();

    private native boolean flacIsDecoderAtEndOfStream(long j);

    private native void flacRelease(long j);

    private native void flacReset(long j, long j2);

    /* loaded from: classes3.dex */
    public static final class FlacFrameDecodeException extends Exception {
        public final int errorCode;

        public FlacFrameDecodeException(String message, int errorCode) {
            super(message);
            this.errorCode = errorCode;
        }
    }

    public FlacDecoderJni() throws FlacDecoderException {
        long flacInit = flacInit();
        this.nativeDecoderContext = flacInit;
        if (flacInit == 0) {
            throw new FlacDecoderException("Failed to initialize decoder");
        }
    }

    public void setData(ByteBuffer byteBufferData) {
        this.byteBufferData = byteBufferData;
        this.extractorInput = null;
    }

    public void setData(ExtractorInput extractorInput) {
        this.byteBufferData = null;
        this.extractorInput = extractorInput;
        this.endOfExtractorInput = false;
        if (this.tempBuffer == null) {
            this.tempBuffer = new byte[8192];
        }
    }

    public boolean isEndOfData() {
        ByteBuffer byteBuffer = this.byteBufferData;
        if (byteBuffer != null) {
            return byteBuffer.remaining() == 0;
        } else if (this.extractorInput == null) {
            return true;
        } else {
            return this.endOfExtractorInput;
        }
    }

    public void clearData() {
        this.byteBufferData = null;
        this.extractorInput = null;
    }

    public int read(ByteBuffer target) throws IOException, InterruptedException {
        int byteCount = target.remaining();
        ByteBuffer byteBuffer = this.byteBufferData;
        if (byteBuffer != null) {
            int byteCount2 = Math.min(byteCount, byteBuffer.remaining());
            int originalLimit = this.byteBufferData.limit();
            ByteBuffer byteBuffer2 = this.byteBufferData;
            byteBuffer2.limit(byteBuffer2.position() + byteCount2);
            target.put(this.byteBufferData);
            this.byteBufferData.limit(originalLimit);
            return byteCount2;
        } else if (this.extractorInput != null) {
            ExtractorInput extractorInput = this.extractorInput;
            byte[] tempBuffer = (byte[]) Util.castNonNull(this.tempBuffer);
            int byteCount3 = Math.min(byteCount, 8192);
            int read = readFromExtractorInput(extractorInput, tempBuffer, 0, byteCount3);
            if (read < 4) {
                read += readFromExtractorInput(extractorInput, tempBuffer, read, byteCount3 - read);
            }
            int byteCount4 = read;
            target.put(tempBuffer, 0, byteCount4);
            return byteCount4;
        } else {
            return -1;
        }
    }

    public FlacStreamMetadata decodeStreamMetadata() throws IOException, InterruptedException {
        FlacStreamMetadata streamMetadata = flacDecodeMetadata(this.nativeDecoderContext);
        if (streamMetadata == null) {
            throw new ParserException("Failed to decode stream metadata");
        }
        return streamMetadata;
    }

    public void decodeSampleWithBacktrackPosition(ByteBuffer output, long retryPosition) throws InterruptedException, IOException, FlacFrameDecodeException {
        try {
            decodeSample(output);
        } catch (IOException e) {
            if (retryPosition >= 0) {
                reset(retryPosition);
                ExtractorInput extractorInput = this.extractorInput;
                if (extractorInput != null) {
                    extractorInput.setRetryPosition(retryPosition, e);
                }
            }
            throw e;
        }
    }

    public void decodeSample(ByteBuffer output) throws IOException, InterruptedException, FlacFrameDecodeException {
        int frameSize;
        output.clear();
        if (output.isDirect()) {
            frameSize = flacDecodeToBuffer(this.nativeDecoderContext, output);
        } else {
            frameSize = flacDecodeToArray(this.nativeDecoderContext, output.array());
        }
        if (frameSize < 0) {
            if (!isDecoderAtEndOfInput()) {
                throw new FlacFrameDecodeException("Cannot decode FLAC frame", frameSize);
            }
            output.limit(0);
            return;
        }
        output.limit(frameSize);
    }

    public long getDecodePosition() {
        return flacGetDecodePosition(this.nativeDecoderContext);
    }

    public long getLastFrameTimestamp() {
        return flacGetLastFrameTimestamp(this.nativeDecoderContext);
    }

    public long getLastFrameFirstSampleIndex() {
        return flacGetLastFrameFirstSampleIndex(this.nativeDecoderContext);
    }

    public long getNextFrameFirstSampleIndex() {
        return flacGetNextFrameFirstSampleIndex(this.nativeDecoderContext);
    }

    public SeekMap.SeekPoints getSeekPoints(long timeUs) {
        SeekPoint secondSeekPoint;
        long[] seekPoints = new long[4];
        if (!flacGetSeekPoints(this.nativeDecoderContext, timeUs, seekPoints)) {
            return null;
        }
        SeekPoint firstSeekPoint = new SeekPoint(seekPoints[0], seekPoints[1]);
        if (seekPoints[2] == seekPoints[0]) {
            secondSeekPoint = firstSeekPoint;
        } else {
            secondSeekPoint = new SeekPoint(seekPoints[2], seekPoints[3]);
        }
        return new SeekMap.SeekPoints(firstSeekPoint, secondSeekPoint);
    }

    public String getStateString() {
        return flacGetStateString(this.nativeDecoderContext);
    }

    public boolean isDecoderAtEndOfInput() {
        return flacIsDecoderAtEndOfStream(this.nativeDecoderContext);
    }

    public void flush() {
        flacFlush(this.nativeDecoderContext);
    }

    public void reset(long newPosition) {
        flacReset(this.nativeDecoderContext, newPosition);
    }

    public void release() {
        flacRelease(this.nativeDecoderContext);
    }

    private int readFromExtractorInput(ExtractorInput extractorInput, byte[] tempBuffer, int offset, int length) throws IOException, InterruptedException {
        int read = extractorInput.read(tempBuffer, offset, length);
        if (read == -1) {
            this.endOfExtractorInput = true;
            return 0;
        }
        return read;
    }
}
