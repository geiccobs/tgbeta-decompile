package com.google.android.exoplayer2.audio;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.ParsableBitArray;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.nio.ByteBuffer;
/* loaded from: classes3.dex */
public final class Ac4Util {
    public static final int AC40_SYNCWORD = 44096;
    public static final int AC41_SYNCWORD = 44097;
    private static final int CHANNEL_COUNT_2 = 2;
    public static final int HEADER_SIZE_FOR_PARSER = 16;
    private static final int[] SAMPLE_COUNT = {2002, 2000, 1920, 1601, 1600, 1001, 1000, 960, 800, 800, 480, 400, 400, 2048};
    public static final int SAMPLE_HEADER_SIZE = 7;

    /* loaded from: classes3.dex */
    public static final class SyncFrameInfo {
        public final int bitstreamVersion;
        public final int channelCount;
        public final int frameSize;
        public final int sampleCount;
        public final int sampleRate;

        private SyncFrameInfo(int bitstreamVersion, int channelCount, int sampleRate, int frameSize, int sampleCount) {
            this.bitstreamVersion = bitstreamVersion;
            this.channelCount = channelCount;
            this.sampleRate = sampleRate;
            this.frameSize = frameSize;
            this.sampleCount = sampleCount;
        }
    }

    public static Format parseAc4AnnexEFormat(ParsableByteArray data, String trackId, String language, DrmInitData drmInitData) {
        data.skipBytes(1);
        int sampleRate = ((data.readUnsignedByte() & 32) >> 5) == 1 ? 48000 : 44100;
        return Format.createAudioSampleFormat(trackId, MimeTypes.AUDIO_AC4, null, -1, -1, 2, sampleRate, null, drmInitData, 0, language);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static SyncFrameInfo parseAc4SyncframeInfo(ParsableBitArray data) {
        int bitstreamVersion;
        int sampleCount;
        int syncWord = data.readBits(16);
        int frameSize = data.readBits(16);
        int headerSize = 0 + 2 + 2;
        if (frameSize == 65535) {
            frameSize = data.readBits(24);
            headerSize += 3;
        }
        int frameSize2 = frameSize + headerSize;
        if (syncWord == 44097) {
            frameSize2 += 2;
        }
        int bitstreamVersion2 = data.readBits(2);
        if (bitstreamVersion2 != 3) {
            bitstreamVersion = bitstreamVersion2;
        } else {
            bitstreamVersion = bitstreamVersion2 + readVariableBits(data, 2);
        }
        int sequenceCounter = data.readBits(10);
        if (data.readBit() && data.readBits(3) > 0) {
            data.skipBits(2);
        }
        int sampleRate = data.readBit() ? 48000 : 44100;
        int frameRateIndex = data.readBits(4);
        int sampleCount2 = 0;
        if (sampleRate == 44100 && frameRateIndex == 13) {
            sampleCount = SAMPLE_COUNT[frameRateIndex];
        } else {
            if (sampleRate == 48000) {
                int[] iArr = SAMPLE_COUNT;
                if (frameRateIndex < iArr.length) {
                    sampleCount2 = iArr[frameRateIndex];
                    switch (sequenceCounter % 5) {
                        case 1:
                        case 3:
                            if (frameRateIndex == 3 || frameRateIndex == 8) {
                                sampleCount = sampleCount2 + 1;
                                break;
                            }
                        case 2:
                            if (frameRateIndex == 8 || frameRateIndex == 11) {
                                sampleCount = sampleCount2 + 1;
                                break;
                            }
                        case 4:
                            if (frameRateIndex == 3 || frameRateIndex == 8 || frameRateIndex == 11) {
                                sampleCount = sampleCount2 + 1;
                                break;
                            }
                    }
                }
            }
            sampleCount = sampleCount2;
        }
        return new SyncFrameInfo(bitstreamVersion, 2, sampleRate, frameSize2, sampleCount);
    }

    public static int parseAc4SyncframeSize(byte[] data, int syncword) {
        if (data.length < 7) {
            return -1;
        }
        int frameSize = ((data[2] & 255) << 8) | (data[3] & 255);
        int headerSize = 2 + 2;
        if (frameSize == 65535) {
            frameSize = ((data[4] & 255) << 16) | ((data[5] & 255) << 8) | (data[6] & 255);
            headerSize += 3;
        }
        if (syncword == 44097) {
            headerSize += 2;
        }
        return frameSize + headerSize;
    }

    public static int parseAc4SyncframeAudioSampleCount(ByteBuffer buffer) {
        byte[] bufferBytes = new byte[16];
        int position = buffer.position();
        buffer.get(bufferBytes);
        buffer.position(position);
        return parseAc4SyncframeInfo(new ParsableBitArray(bufferBytes)).sampleCount;
    }

    public static void getAc4SampleHeader(int size, ParsableByteArray buffer) {
        buffer.reset(7);
        buffer.data[0] = -84;
        buffer.data[1] = 64;
        buffer.data[2] = -1;
        buffer.data[3] = -1;
        buffer.data[4] = (byte) ((size >> 16) & 255);
        buffer.data[5] = (byte) ((size >> 8) & 255);
        buffer.data[6] = (byte) (size & 255);
    }

    private static int readVariableBits(ParsableBitArray data, int bitsPerRead) {
        int value = 0;
        while (true) {
            int value2 = value + data.readBits(bitsPerRead);
            if (data.readBit()) {
                value = (value2 + 1) << bitsPerRead;
            } else {
                return value2;
            }
        }
    }

    private Ac4Util() {
    }
}
