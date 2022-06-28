package com.google.android.exoplayer2.ext.ffmpeg;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.decoder.SimpleDecoder;
import com.google.android.exoplayer2.decoder.SimpleOutputBuffer;
import com.google.android.exoplayer2.extractor.mp4.Atom;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;
import java.util.List;
/* loaded from: classes3.dex */
final class FfmpegDecoder extends SimpleDecoder<DecoderInputBuffer, SimpleOutputBuffer, FfmpegDecoderException> {
    private static final int DECODER_ERROR_INVALID_DATA = -1;
    private static final int DECODER_ERROR_OTHER = -2;
    private static final int OUTPUT_BUFFER_SIZE_16BIT = 65536;
    private static final int OUTPUT_BUFFER_SIZE_32BIT = 131072;
    private volatile int channelCount;
    private final String codecName;
    private final int encoding;
    private final byte[] extraData;
    private boolean hasOutputFormat;
    private long nativeContext;
    private final int outputBufferSize;
    private volatile int sampleRate;

    private native int ffmpegDecode(long j, ByteBuffer byteBuffer, int i, ByteBuffer byteBuffer2, int i2);

    private native int ffmpegGetChannelCount(long j);

    private native int ffmpegGetSampleRate(long j);

    private native long ffmpegInitialize(String str, byte[] bArr, boolean z, int i, int i2);

    private native void ffmpegRelease(long j);

    private native long ffmpegReset(long j, byte[] bArr);

    public FfmpegDecoder(int numInputBuffers, int numOutputBuffers, int initialInputBufferSize, Format format, boolean outputFloat) throws FfmpegDecoderException {
        super(new DecoderInputBuffer[numInputBuffers], new SimpleOutputBuffer[numOutputBuffers]);
        Assertions.checkNotNull(format.sampleMimeType);
        String str = (String) Assertions.checkNotNull(FfmpegLibrary.getCodecName(format.sampleMimeType));
        this.codecName = str;
        byte[] extraData = getExtraData(format.sampleMimeType, format.initializationData);
        this.extraData = extraData;
        this.encoding = outputFloat ? 4 : 2;
        this.outputBufferSize = outputFloat ? 131072 : 65536;
        long ffmpegInitialize = ffmpegInitialize(str, extraData, outputFloat, format.sampleRate, format.channelCount);
        this.nativeContext = ffmpegInitialize;
        if (ffmpegInitialize == 0) {
            throw new FfmpegDecoderException("Initialization failed.");
        }
        setInitialInputBufferSize(initialInputBufferSize);
    }

    @Override // com.google.android.exoplayer2.decoder.Decoder
    public String getName() {
        return "ffmpeg" + FfmpegLibrary.getVersion() + "-" + this.codecName;
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
    public FfmpegDecoderException createUnexpectedDecodeException(Throwable error) {
        return new FfmpegDecoderException("Unexpected decode error", error);
    }

    public FfmpegDecoderException decode(DecoderInputBuffer inputBuffer, SimpleOutputBuffer outputBuffer, boolean reset) {
        if (reset) {
            long ffmpegReset = ffmpegReset(this.nativeContext, this.extraData);
            this.nativeContext = ffmpegReset;
            if (ffmpegReset == 0) {
                return new FfmpegDecoderException("Error resetting (see logcat).");
            }
        }
        ByteBuffer inputData = (ByteBuffer) Util.castNonNull(inputBuffer.data);
        int inputSize = inputData.limit();
        ByteBuffer outputData = outputBuffer.init(inputBuffer.timeUs, this.outputBufferSize);
        int result = ffmpegDecode(this.nativeContext, inputData, inputSize, outputData, this.outputBufferSize);
        if (result == -1) {
            outputBuffer.setFlags(Integer.MIN_VALUE);
            return null;
        } else if (result == -2) {
            return new FfmpegDecoderException("Error decoding (see logcat).");
        } else {
            if (!this.hasOutputFormat) {
                this.channelCount = ffmpegGetChannelCount(this.nativeContext);
                this.sampleRate = ffmpegGetSampleRate(this.nativeContext);
                if (this.sampleRate == 0 && "alac".equals(this.codecName)) {
                    Assertions.checkNotNull(this.extraData);
                    ParsableByteArray parsableExtraData = new ParsableByteArray(this.extraData);
                    parsableExtraData.setPosition(this.extraData.length - 4);
                    this.sampleRate = parsableExtraData.readUnsignedIntToInt();
                }
                this.hasOutputFormat = true;
            }
            outputData.position(0);
            outputData.limit(result);
            return null;
        }
    }

    @Override // com.google.android.exoplayer2.decoder.SimpleDecoder, com.google.android.exoplayer2.decoder.Decoder
    public void release() {
        super.release();
        ffmpegRelease(this.nativeContext);
        this.nativeContext = 0L;
    }

    public int getChannelCount() {
        return this.channelCount;
    }

    public int getSampleRate() {
        return this.sampleRate;
    }

    public int getEncoding() {
        return this.encoding;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private static byte[] getExtraData(String mimeType, List<byte[]> initializationData) {
        char c;
        switch (mimeType.hashCode()) {
            case -1003765268:
                if (mimeType.equals(MimeTypes.AUDIO_VORBIS)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -53558318:
                if (mimeType.equals("audio/mp4a-latm")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 1504470054:
                if (mimeType.equals(MimeTypes.AUDIO_ALAC)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 1504891608:
                if (mimeType.equals(MimeTypes.AUDIO_OPUS)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
            case 1:
                return initializationData.get(0);
            case 2:
                return getAlacExtraData(initializationData);
            case 3:
                return getVorbisExtraData(initializationData);
            default:
                return null;
        }
    }

    private static byte[] getAlacExtraData(List<byte[]> initializationData) {
        byte[] magicCookie = initializationData.get(0);
        int alacAtomLength = magicCookie.length + 12;
        ByteBuffer alacAtom = ByteBuffer.allocate(alacAtomLength);
        alacAtom.putInt(alacAtomLength);
        alacAtom.putInt(Atom.TYPE_alac);
        alacAtom.putInt(0);
        alacAtom.put(magicCookie, 0, magicCookie.length);
        return alacAtom.array();
    }

    private static byte[] getVorbisExtraData(List<byte[]> initializationData) {
        byte[] header0 = initializationData.get(0);
        byte[] header1 = initializationData.get(1);
        byte[] extraData = new byte[header0.length + header1.length + 6];
        extraData[0] = (byte) (header0.length >> 8);
        extraData[1] = (byte) (header0.length & 255);
        System.arraycopy(header0, 0, extraData, 2, header0.length);
        extraData[header0.length + 2] = 0;
        extraData[header0.length + 3] = 0;
        extraData[header0.length + 4] = (byte) (header1.length >> 8);
        extraData[header0.length + 5] = (byte) (header1.length & 255);
        System.arraycopy(header1, 0, extraData, header0.length + 6, header1.length);
        return extraData;
    }
}
