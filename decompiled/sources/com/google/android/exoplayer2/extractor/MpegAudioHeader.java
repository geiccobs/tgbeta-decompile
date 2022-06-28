package com.google.android.exoplayer2.extractor;

import com.google.android.exoplayer2.util.MimeTypes;
import org.telegram.messenger.OneUIUtilities;
/* loaded from: classes3.dex */
public final class MpegAudioHeader {
    public static final int MAX_FRAME_SIZE_BYTES = 4096;
    private static final int SAMPLES_PER_FRAME_L1 = 384;
    private static final int SAMPLES_PER_FRAME_L2 = 1152;
    private static final int SAMPLES_PER_FRAME_L3_V1 = 1152;
    private static final int SAMPLES_PER_FRAME_L3_V2 = 576;
    public int bitrate;
    public int channels;
    public int frameSize;
    public String mimeType;
    public int sampleRate;
    public int samplesPerFrame;
    public int version;
    private static final String[] MIME_TYPE_BY_LAYER = {MimeTypes.AUDIO_MPEG_L1, MimeTypes.AUDIO_MPEG_L2, MimeTypes.AUDIO_MPEG};
    private static final int[] SAMPLING_RATE_V1 = {44100, 48000, 32000};
    private static final int[] BITRATE_V1_L1 = {32000, 64000, 96000, 128000, 160000, 192000, 224000, 256000, 288000, 320000, 352000, 384000, 416000, 448000};
    private static final int[] BITRATE_V2_L1 = {32000, 48000, 56000, 64000, 80000, 96000, 112000, 128000, 144000, 160000, 176000, 192000, 224000, 256000};
    private static final int[] BITRATE_V1_L2 = {32000, 48000, 56000, 64000, 80000, 96000, 112000, 128000, 160000, 192000, 224000, 256000, 320000, 384000};
    private static final int[] BITRATE_V1_L3 = {32000, OneUIUtilities.ONE_UI_4_0, 48000, 56000, 64000, 80000, 96000, 112000, 128000, 160000, 192000, 224000, 256000, 320000};
    private static final int[] BITRATE_V2 = {8000, 16000, 24000, 32000, OneUIUtilities.ONE_UI_4_0, 48000, 56000, 64000, 80000, 96000, 112000, 128000, 144000, 160000};

    public static int getFrameSize(int header) {
        int version;
        int layer;
        int bitrateIndex;
        int samplingRateIndex;
        int bitrate;
        if (!isMagicPresent(header) || (version = (header >>> 19) & 3) == 1 || (layer = (header >>> 17) & 3) == 0 || (bitrateIndex = (header >>> 12) & 15) == 0 || bitrateIndex == 15 || (samplingRateIndex = (header >>> 10) & 3) == 3) {
            return -1;
        }
        int samplingRate = SAMPLING_RATE_V1[samplingRateIndex];
        if (version == 2) {
            samplingRate /= 2;
        } else if (version == 0) {
            samplingRate /= 4;
        }
        int padding = (header >>> 9) & 1;
        if (layer == 3) {
            return ((((version == 3 ? BITRATE_V1_L1[bitrateIndex - 1] : BITRATE_V2_L1[bitrateIndex - 1]) * 12) / samplingRate) + padding) * 4;
        }
        if (version == 3) {
            bitrate = layer == 2 ? BITRATE_V1_L2[bitrateIndex - 1] : BITRATE_V1_L3[bitrateIndex - 1];
        } else {
            bitrate = BITRATE_V2[bitrateIndex - 1];
        }
        if (version == 3) {
            return ((bitrate * 144) / samplingRate) + padding;
        }
        return (((layer == 1 ? 72 : 144) * bitrate) / samplingRate) + padding;
    }

    public static int getFrameSampleCount(int header) {
        int version;
        int layer;
        if (!isMagicPresent(header) || (version = (header >>> 19) & 3) == 1 || (layer = (header >>> 17) & 3) == 0) {
            return -1;
        }
        int bitrateIndex = (header >>> 12) & 15;
        int samplingRateIndex = (header >>> 10) & 3;
        if (bitrateIndex != 0 && bitrateIndex != 15 && samplingRateIndex != 3) {
            return getFrameSizeInSamples(version, layer);
        }
        return -1;
    }

    public static boolean populateHeader(int headerData, MpegAudioHeader header) {
        int version;
        int layer;
        int bitrateIndex;
        int samplingRateIndex;
        int frameSize;
        int bitrate;
        if (!isMagicPresent(headerData) || (version = (headerData >>> 19) & 3) == 1 || (layer = (headerData >>> 17) & 3) == 0 || (bitrateIndex = (headerData >>> 12) & 15) == 0 || bitrateIndex == 15 || (samplingRateIndex = (headerData >>> 10) & 3) == 3) {
            return false;
        }
        int sampleRate = SAMPLING_RATE_V1[samplingRateIndex];
        if (version == 2) {
            sampleRate /= 2;
        } else if (version == 0) {
            sampleRate /= 4;
        }
        int padding = (headerData >>> 9) & 1;
        int samplesPerFrame = getFrameSizeInSamples(version, layer);
        if (layer == 3) {
            int bitrate2 = version == 3 ? BITRATE_V1_L1[bitrateIndex - 1] : BITRATE_V2_L1[bitrateIndex - 1];
            bitrate = bitrate2;
            frameSize = (((bitrate2 * 12) / sampleRate) + padding) * 4;
        } else if (version == 3) {
            int bitrate3 = layer == 2 ? BITRATE_V1_L2[bitrateIndex - 1] : BITRATE_V1_L3[bitrateIndex - 1];
            bitrate = bitrate3;
            frameSize = ((bitrate3 * 144) / sampleRate) + padding;
        } else {
            int bitrate4 = BITRATE_V2[bitrateIndex - 1];
            bitrate = bitrate4;
            frameSize = (((layer == 1 ? 72 : 144) * bitrate4) / sampleRate) + padding;
        }
        String mimeType = MIME_TYPE_BY_LAYER[3 - layer];
        int channels = ((headerData >> 6) & 3) == 3 ? 1 : 2;
        header.setValues(version, mimeType, frameSize, sampleRate, channels, bitrate, samplesPerFrame);
        return true;
    }

    private static boolean isMagicPresent(int header) {
        return (header & (-2097152)) == -2097152;
    }

    private static int getFrameSizeInSamples(int version, int layer) {
        switch (layer) {
            case 1:
                if (version != 3) {
                    return SAMPLES_PER_FRAME_L3_V2;
                }
                return 1152;
            case 2:
                return 1152;
            case 3:
                return SAMPLES_PER_FRAME_L1;
            default:
                throw new IllegalArgumentException();
        }
    }

    private void setValues(int version, String mimeType, int frameSize, int sampleRate, int channels, int bitrate, int samplesPerFrame) {
        this.version = version;
        this.mimeType = mimeType;
        this.frameSize = frameSize;
        this.sampleRate = sampleRate;
        this.channels = channels;
        this.bitrate = bitrate;
        this.samplesPerFrame = samplesPerFrame;
    }
}
