package com.google.android.exoplayer2.ext.opus;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.decoder.CryptoInfo;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.decoder.SimpleDecoder;
import com.google.android.exoplayer2.decoder.SimpleOutputBuffer;
import com.google.android.exoplayer2.drm.DecryptionException;
import com.google.android.exoplayer2.drm.ExoMediaCrypto;
import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
/* loaded from: classes3.dex */
final class OpusDecoder extends SimpleDecoder<DecoderInputBuffer, SimpleOutputBuffer, OpusDecoderException> {
    private static final int DECODE_ERROR = -1;
    private static final int DEFAULT_SEEK_PRE_ROLL_SAMPLES = 3840;
    private static final int DRM_ERROR = -2;
    private static final int NO_ERROR = 0;
    private static final int SAMPLE_RATE = 48000;
    private final int channelCount;
    private final ExoMediaCrypto exoMediaCrypto;
    private final int headerSeekPreRollSamples;
    private final int headerSkipSamples;
    private final long nativeDecoderContext;
    private int skipSamples;

    private native void opusClose(long j);

    private native int opusDecode(long j, long j2, ByteBuffer byteBuffer, int i, SimpleOutputBuffer simpleOutputBuffer);

    private native int opusGetErrorCode(long j);

    private native String opusGetErrorMessage(long j);

    private native long opusInit(int i, int i2, int i3, int i4, int i5, byte[] bArr);

    private native void opusReset(long j);

    private native int opusSecureDecode(long j, long j2, ByteBuffer byteBuffer, int i, SimpleOutputBuffer simpleOutputBuffer, int i2, ExoMediaCrypto exoMediaCrypto, int i3, byte[] bArr, byte[] bArr2, int i4, int[] iArr, int[] iArr2);

    public OpusDecoder(int numInputBuffers, int numOutputBuffers, int initialInputBufferSize, List<byte[]> initializationData, ExoMediaCrypto exoMediaCrypto) throws OpusDecoderException {
        super(new DecoderInputBuffer[numInputBuffers], new SimpleOutputBuffer[numOutputBuffers]);
        int numCoupled;
        int numStreams;
        this.exoMediaCrypto = exoMediaCrypto;
        if (exoMediaCrypto != null && !OpusLibrary.opusIsSecureDecodeSupported()) {
            throw new OpusDecoderException("Opus decoder does not support secure decode.");
        }
        byte[] headerBytes = initializationData.get(0);
        if (headerBytes.length < 19) {
            throw new OpusDecoderException("Header size is too small.");
        }
        int i = headerBytes[9] & 255;
        this.channelCount = i;
        if (i > 8) {
            throw new OpusDecoderException("Invalid channel count: " + i);
        }
        int preskip = readUnsignedLittleEndian16(headerBytes, 10);
        int gain = readSignedLittleEndian16(headerBytes, 16);
        byte[] streamMap = new byte[8];
        if (headerBytes[18] == 0) {
            if (i > 2) {
                throw new OpusDecoderException("Invalid Header, missing stream map.");
            }
            int numCoupled2 = i == 2 ? 1 : 0;
            streamMap[0] = 0;
            streamMap[1] = 1;
            numStreams = 1;
            numCoupled = numCoupled2;
        } else if (headerBytes.length < i + 21) {
            throw new OpusDecoderException("Header size is too small.");
        } else {
            int numStreams2 = headerBytes[19] & 255;
            int numCoupled3 = headerBytes[20] & 255;
            System.arraycopy(headerBytes, 21, streamMap, 0, i);
            numStreams = numStreams2;
            numCoupled = numCoupled3;
        }
        if (initializationData.size() == 3) {
            if (initializationData.get(1).length != 8 || initializationData.get(2).length != 8) {
                throw new OpusDecoderException("Invalid Codec Delay or Seek Preroll");
            }
            long codecDelayNs = ByteBuffer.wrap(initializationData.get(1)).order(ByteOrder.nativeOrder()).getLong();
            long seekPreRollNs = ByteBuffer.wrap(initializationData.get(2)).order(ByteOrder.nativeOrder()).getLong();
            this.headerSkipSamples = nsToSamples(codecDelayNs);
            this.headerSeekPreRollSamples = nsToSamples(seekPreRollNs);
        } else {
            this.headerSkipSamples = preskip;
            this.headerSeekPreRollSamples = DEFAULT_SEEK_PRE_ROLL_SAMPLES;
        }
        long opusInit = opusInit(SAMPLE_RATE, i, numStreams, numCoupled, gain, streamMap);
        this.nativeDecoderContext = opusInit;
        if (opusInit == 0) {
            throw new OpusDecoderException("Failed to initialize decoder");
        }
        setInitialInputBufferSize(initialInputBufferSize);
    }

    @Override // com.google.android.exoplayer2.decoder.Decoder
    public String getName() {
        return "libopus" + OpusLibrary.getVersion();
    }

    @Override // com.google.android.exoplayer2.decoder.SimpleDecoder
    protected DecoderInputBuffer createInputBuffer() {
        return new DecoderInputBuffer(2);
    }

    @Override // com.google.android.exoplayer2.decoder.SimpleDecoder
    public SimpleOutputBuffer createOutputBuffer() {
        return new SimpleOutputBuffer(this);
    }

    @Override // com.google.android.exoplayer2.decoder.SimpleDecoder
    public OpusDecoderException createUnexpectedDecodeException(Throwable error) {
        return new OpusDecoderException("Unexpected decode error", error);
    }

    public OpusDecoderException decode(DecoderInputBuffer inputBuffer, SimpleOutputBuffer outputBuffer, boolean reset) {
        OpusDecoder opusDecoder;
        int result;
        if (reset) {
            opusReset(this.nativeDecoderContext);
            this.skipSamples = inputBuffer.timeUs == 0 ? this.headerSkipSamples : this.headerSeekPreRollSamples;
        }
        ByteBuffer inputData = (ByteBuffer) Util.castNonNull(inputBuffer.data);
        CryptoInfo cryptoInfo = inputBuffer.cryptoInfo;
        if (inputBuffer.isEncrypted()) {
            result = opusSecureDecode(this.nativeDecoderContext, inputBuffer.timeUs, inputData, inputData.limit(), outputBuffer, SAMPLE_RATE, this.exoMediaCrypto, cryptoInfo.mode, cryptoInfo.key, cryptoInfo.iv, cryptoInfo.numSubSamples, cryptoInfo.numBytesOfClearData, cryptoInfo.numBytesOfEncryptedData);
            opusDecoder = this;
        } else {
            opusDecoder = this;
            result = opusDecode(opusDecoder.nativeDecoderContext, inputBuffer.timeUs, inputData, inputData.limit(), outputBuffer);
        }
        if (result < 0) {
            if (result == -2) {
                String message = "Drm error: " + opusDecoder.opusGetErrorMessage(opusDecoder.nativeDecoderContext);
                DecryptionException cause = new DecryptionException(opusDecoder.opusGetErrorCode(opusDecoder.nativeDecoderContext), message);
                return new OpusDecoderException(message, cause);
            }
            return new OpusDecoderException("Decode error: " + opusDecoder.opusGetErrorMessage(result));
        }
        ByteBuffer outputData = (ByteBuffer) Util.castNonNull(outputBuffer.data);
        outputData.position(0);
        outputData.limit(result);
        int i = opusDecoder.skipSamples;
        if (i > 0) {
            int bytesPerSample = opusDecoder.channelCount * 2;
            int skipBytes = i * bytesPerSample;
            if (result <= skipBytes) {
                opusDecoder.skipSamples = i - (result / bytesPerSample);
                outputBuffer.addFlag(Integer.MIN_VALUE);
                outputData.position(result);
                return null;
            }
            opusDecoder.skipSamples = 0;
            outputData.position(skipBytes);
            return null;
        }
        return null;
    }

    @Override // com.google.android.exoplayer2.decoder.SimpleDecoder, com.google.android.exoplayer2.decoder.Decoder
    public void release() {
        super.release();
        opusClose(this.nativeDecoderContext);
    }

    public int getChannelCount() {
        return this.channelCount;
    }

    public int getSampleRate() {
        return SAMPLE_RATE;
    }

    private static int nsToSamples(long ns) {
        return (int) ((48000 * ns) / C.NANOS_PER_SECOND);
    }

    private static int readUnsignedLittleEndian16(byte[] input, int offset) {
        int value = input[offset] & 255;
        return value | ((input[offset + 1] & 255) << 8);
    }

    private static int readSignedLittleEndian16(byte[] input, int offset) {
        return (short) readUnsignedLittleEndian16(input, offset);
    }
}
