package com.google.android.exoplayer2.ext.opus;

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
/* loaded from: classes.dex */
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

    public int getSampleRate() {
        return SAMPLE_RATE;
    }

    public OpusDecoder(int i, int i2, int i3, List<byte[]> list, ExoMediaCrypto exoMediaCrypto) throws OpusDecoderException {
        super(new DecoderInputBuffer[i], new SimpleOutputBuffer[i2]);
        int i4;
        int i5;
        this.exoMediaCrypto = exoMediaCrypto;
        if (exoMediaCrypto != null && !OpusLibrary.opusIsSecureDecodeSupported()) {
            throw new OpusDecoderException("Opus decoder does not support secure decode.");
        }
        byte[] bArr = list.get(0);
        if (bArr.length < 19) {
            throw new OpusDecoderException("Header size is too small.");
        }
        int i6 = bArr[9] & 255;
        this.channelCount = i6;
        if (i6 > 8) {
            throw new OpusDecoderException("Invalid channel count: " + i6);
        }
        int readUnsignedLittleEndian16 = readUnsignedLittleEndian16(bArr, 10);
        int readSignedLittleEndian16 = readSignedLittleEndian16(bArr, 16);
        byte[] bArr2 = new byte[8];
        if (bArr[18] == 0) {
            if (i6 > 2) {
                throw new OpusDecoderException("Invalid Header, missing stream map.");
            }
            i4 = i6 == 2 ? 1 : 0;
            bArr2[0] = 0;
            bArr2[1] = 1;
            i5 = 1;
        } else if (bArr.length < i6 + 21) {
            throw new OpusDecoderException("Header size is too small.");
        } else {
            i5 = bArr[19] & 255;
            System.arraycopy(bArr, 21, bArr2, 0, i6);
            i4 = bArr[20] & 255;
        }
        if (list.size() == 3) {
            if (list.get(1).length != 8 || list.get(2).length != 8) {
                throw new OpusDecoderException("Invalid Codec Delay or Seek Preroll");
            }
            long j = ByteBuffer.wrap(list.get(1)).order(ByteOrder.nativeOrder()).getLong();
            long j2 = ByteBuffer.wrap(list.get(2)).order(ByteOrder.nativeOrder()).getLong();
            this.headerSkipSamples = nsToSamples(j);
            this.headerSeekPreRollSamples = nsToSamples(j2);
        } else {
            this.headerSkipSamples = readUnsignedLittleEndian16;
            this.headerSeekPreRollSamples = DEFAULT_SEEK_PRE_ROLL_SAMPLES;
        }
        long opusInit = opusInit(SAMPLE_RATE, i6, i5, i4, readSignedLittleEndian16, bArr2);
        this.nativeDecoderContext = opusInit;
        if (opusInit == 0) {
            throw new OpusDecoderException("Failed to initialize decoder");
        }
        setInitialInputBufferSize(i3);
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
    public OpusDecoderException createUnexpectedDecodeException(Throwable th) {
        return new OpusDecoderException("Unexpected decode error", th);
    }

    public OpusDecoderException decode(DecoderInputBuffer decoderInputBuffer, SimpleOutputBuffer simpleOutputBuffer, boolean z) {
        OpusDecoder opusDecoder;
        int i;
        if (z) {
            opusReset(this.nativeDecoderContext);
            this.skipSamples = decoderInputBuffer.timeUs == 0 ? this.headerSkipSamples : this.headerSeekPreRollSamples;
        }
        ByteBuffer byteBuffer = (ByteBuffer) Util.castNonNull(decoderInputBuffer.data);
        CryptoInfo cryptoInfo = decoderInputBuffer.cryptoInfo;
        if (decoderInputBuffer.isEncrypted()) {
            i = opusSecureDecode(this.nativeDecoderContext, decoderInputBuffer.timeUs, byteBuffer, byteBuffer.limit(), simpleOutputBuffer, SAMPLE_RATE, this.exoMediaCrypto, cryptoInfo.mode, cryptoInfo.key, cryptoInfo.iv, cryptoInfo.numSubSamples, cryptoInfo.numBytesOfClearData, cryptoInfo.numBytesOfEncryptedData);
            opusDecoder = this;
        } else {
            opusDecoder = this;
            i = opusDecode(opusDecoder.nativeDecoderContext, decoderInputBuffer.timeUs, byteBuffer, byteBuffer.limit(), simpleOutputBuffer);
        }
        if (i < 0) {
            if (i == -2) {
                String str = "Drm error: " + opusDecoder.opusGetErrorMessage(opusDecoder.nativeDecoderContext);
                return new OpusDecoderException(str, new DecryptionException(opusDecoder.opusGetErrorCode(opusDecoder.nativeDecoderContext), str));
            }
            return new OpusDecoderException("Decode error: " + opusDecoder.opusGetErrorMessage(i));
        }
        ByteBuffer byteBuffer2 = (ByteBuffer) Util.castNonNull(simpleOutputBuffer.data);
        byteBuffer2.position(0);
        byteBuffer2.limit(i);
        int i2 = opusDecoder.skipSamples;
        if (i2 <= 0) {
            return null;
        }
        int i3 = opusDecoder.channelCount * 2;
        int i4 = i2 * i3;
        if (i <= i4) {
            opusDecoder.skipSamples = i2 - (i / i3);
            simpleOutputBuffer.addFlag(Integer.MIN_VALUE);
            byteBuffer2.position(i);
            return null;
        }
        opusDecoder.skipSamples = 0;
        byteBuffer2.position(i4);
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

    private static int nsToSamples(long j) {
        return (int) ((j * 48000) / 1000000000);
    }

    private static int readUnsignedLittleEndian16(byte[] bArr, int i) {
        return ((bArr[i + 1] & 255) << 8) | (bArr[i] & 255);
    }

    private static int readSignedLittleEndian16(byte[] bArr, int i) {
        return (short) readUnsignedLittleEndian16(bArr, i);
    }
}
