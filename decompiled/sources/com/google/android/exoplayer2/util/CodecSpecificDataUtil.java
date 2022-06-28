package com.google.android.exoplayer2.util;

import android.util.Pair;
import com.google.android.exoplayer2.ParserException;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes3.dex */
public final class CodecSpecificDataUtil {
    private static final int AUDIO_OBJECT_TYPE_AAC_LC = 2;
    private static final int AUDIO_OBJECT_TYPE_ER_BSAC = 22;
    private static final int AUDIO_OBJECT_TYPE_ESCAPE = 31;
    private static final int AUDIO_OBJECT_TYPE_PS = 29;
    private static final int AUDIO_OBJECT_TYPE_SBR = 5;
    private static final int AUDIO_SPECIFIC_CONFIG_CHANNEL_CONFIGURATION_INVALID = -1;
    private static final int AUDIO_SPECIFIC_CONFIG_FREQUENCY_INDEX_ARBITRARY = 15;
    private static final byte[] NAL_START_CODE = {0, 0, 0, 1};
    private static final int[] AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE = {96000, 88200, 64000, 48000, 44100, 32000, 24000, 22050, 16000, 12000, 11025, 8000, 7350};
    private static final int[] AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE = {0, 1, 2, 3, 4, 5, 6, 8, -1, -1, -1, 7, 8, -1, 8, -1};

    private CodecSpecificDataUtil() {
    }

    public static Pair<Integer, Integer> parseAacAudioSpecificConfig(byte[] audioSpecificConfig) throws ParserException {
        return parseAacAudioSpecificConfig(new ParsableBitArray(audioSpecificConfig), false);
    }

    public static Pair<Integer, Integer> parseAacAudioSpecificConfig(ParsableBitArray bitArray, boolean forceReadToEnd) throws ParserException {
        int audioObjectType = getAacAudioObjectType(bitArray);
        int sampleRate = getAacSamplingFrequency(bitArray);
        int channelConfiguration = bitArray.readBits(4);
        if (audioObjectType == 5 || audioObjectType == 29) {
            sampleRate = getAacSamplingFrequency(bitArray);
            audioObjectType = getAacAudioObjectType(bitArray);
            if (audioObjectType == 22) {
                channelConfiguration = bitArray.readBits(4);
            }
        }
        if (forceReadToEnd) {
            switch (audioObjectType) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 6:
                case 7:
                case 17:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                    parseGaSpecificConfig(bitArray, audioObjectType, channelConfiguration);
                    switch (audioObjectType) {
                        case 17:
                        case 19:
                        case 20:
                        case 21:
                        case 22:
                        case 23:
                            int epConfig = bitArray.readBits(2);
                            if (epConfig == 2 || epConfig == 3) {
                                throw new ParserException("Unsupported epConfig: " + epConfig);
                            }
                            break;
                    }
                case 5:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                case 18:
                default:
                    throw new ParserException("Unsupported audio object type: " + audioObjectType);
            }
        }
        int channelCount = AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE[channelConfiguration];
        Assertions.checkArgument(channelCount != -1);
        return Pair.create(Integer.valueOf(sampleRate), Integer.valueOf(channelCount));
    }

    public static byte[] buildAacLcAudioSpecificConfig(int sampleRate, int channelCount) {
        int sampleRateIndex = -1;
        int i = 0;
        while (true) {
            int[] iArr = AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE;
            if (i >= iArr.length) {
                break;
            }
            if (sampleRate == iArr[i]) {
                sampleRateIndex = i;
            }
            i++;
        }
        int channelConfig = -1;
        int i2 = 0;
        while (true) {
            int[] iArr2 = AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE;
            if (i2 >= iArr2.length) {
                break;
            }
            if (channelCount == iArr2[i2]) {
                channelConfig = i2;
            }
            i2++;
        }
        if (sampleRate == -1 || channelConfig == -1) {
            throw new IllegalArgumentException("Invalid sample rate or number of channels: " + sampleRate + ", " + channelCount);
        }
        return buildAacAudioSpecificConfig(2, sampleRateIndex, channelConfig);
    }

    public static byte[] buildAacAudioSpecificConfig(int audioObjectType, int sampleRateIndex, int channelConfig) {
        byte[] specificConfig = {(byte) (((audioObjectType << 3) & 248) | ((sampleRateIndex >> 1) & 7)), (byte) (((sampleRateIndex << 7) & 128) | ((channelConfig << 3) & 120))};
        return specificConfig;
    }

    public static Pair<Integer, Integer> parseAlacAudioSpecificConfig(byte[] audioSpecificConfig) {
        ParsableByteArray byteArray = new ParsableByteArray(audioSpecificConfig);
        byteArray.setPosition(9);
        int channelCount = byteArray.readUnsignedByte();
        byteArray.setPosition(20);
        int sampleRate = byteArray.readUnsignedIntToInt();
        return Pair.create(Integer.valueOf(sampleRate), Integer.valueOf(channelCount));
    }

    public static String buildAvcCodecString(int profileIdc, int constraintsFlagsAndReservedZero2Bits, int levelIdc) {
        return String.format("avc1.%02X%02X%02X", Integer.valueOf(profileIdc), Integer.valueOf(constraintsFlagsAndReservedZero2Bits), Integer.valueOf(levelIdc));
    }

    public static byte[] buildNalUnit(byte[] data, int offset, int length) {
        byte[] bArr = NAL_START_CODE;
        byte[] nalUnit = new byte[bArr.length + length];
        System.arraycopy(bArr, 0, nalUnit, 0, bArr.length);
        System.arraycopy(data, offset, nalUnit, bArr.length, length);
        return nalUnit;
    }

    public static byte[][] splitNalUnits(byte[] data) {
        if (!isNalStartCode(data, 0)) {
            return null;
        }
        List<Integer> starts = new ArrayList<>();
        int nalUnitIndex = 0;
        do {
            starts.add(Integer.valueOf(nalUnitIndex));
            nalUnitIndex = findNalStartCode(data, NAL_START_CODE.length + nalUnitIndex);
        } while (nalUnitIndex != -1);
        byte[][] split = new byte[starts.size()];
        int i = 0;
        while (i < starts.size()) {
            int startIndex = starts.get(i).intValue();
            int endIndex = i < starts.size() + (-1) ? starts.get(i + 1).intValue() : data.length;
            byte[] nal = new byte[endIndex - startIndex];
            System.arraycopy(data, startIndex, nal, 0, nal.length);
            split[i] = nal;
            i++;
        }
        return split;
    }

    private static int findNalStartCode(byte[] data, int index) {
        int endIndex = data.length - NAL_START_CODE.length;
        for (int i = index; i <= endIndex; i++) {
            if (isNalStartCode(data, i)) {
                return i;
            }
        }
        return -1;
    }

    private static boolean isNalStartCode(byte[] data, int index) {
        if (data.length - index <= NAL_START_CODE.length) {
            return false;
        }
        int j = 0;
        while (true) {
            byte[] bArr = NAL_START_CODE;
            if (j < bArr.length) {
                if (data[index + j] != bArr[j]) {
                    return false;
                }
                j++;
            } else {
                return true;
            }
        }
    }

    private static int getAacAudioObjectType(ParsableBitArray bitArray) {
        int audioObjectType = bitArray.readBits(5);
        if (audioObjectType == 31) {
            return bitArray.readBits(6) + 32;
        }
        return audioObjectType;
    }

    private static int getAacSamplingFrequency(ParsableBitArray bitArray) {
        int frequencyIndex = bitArray.readBits(4);
        if (frequencyIndex == 15) {
            int samplingFrequency = bitArray.readBits(24);
            return samplingFrequency;
        }
        Assertions.checkArgument(frequencyIndex < 13);
        int samplingFrequency2 = AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE[frequencyIndex];
        return samplingFrequency2;
    }

    private static void parseGaSpecificConfig(ParsableBitArray bitArray, int audioObjectType, int channelConfiguration) {
        bitArray.skipBits(1);
        boolean dependsOnCoreDecoder = bitArray.readBit();
        if (dependsOnCoreDecoder) {
            bitArray.skipBits(14);
        }
        boolean extensionFlag = bitArray.readBit();
        if (channelConfiguration == 0) {
            throw new UnsupportedOperationException();
        }
        if (audioObjectType == 6 || audioObjectType == 20) {
            bitArray.skipBits(3);
        }
        if (extensionFlag) {
            if (audioObjectType == 22) {
                bitArray.skipBits(16);
            }
            if (audioObjectType == 17 || audioObjectType == 19 || audioObjectType == 20 || audioObjectType == 23) {
                bitArray.skipBits(3);
            }
            bitArray.skipBits(1);
        }
    }
}
