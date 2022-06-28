package com.google.android.exoplayer2.mediacodec;

import android.graphics.Point;
import android.media.MediaCodecInfo;
import android.util.Pair;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
/* loaded from: classes3.dex */
public final class MediaCodecInfo {
    public static final int MAX_SUPPORTED_INSTANCES_UNKNOWN = -1;
    public static final String TAG = "MediaCodecInfo";
    public final boolean adaptive;
    public final MediaCodecInfo.CodecCapabilities capabilities;
    public final String codecMimeType;
    public final boolean hardwareAccelerated;
    private final boolean isVideo;
    public final String mimeType;
    public final String name;
    public final boolean passthrough;
    public final boolean secure;
    public final boolean softwareOnly;
    public final boolean tunneling;
    public final boolean vendor;

    public static MediaCodecInfo newPassthroughInstance(String name) {
        return new MediaCodecInfo(name, null, null, null, true, false, true, false, false, false);
    }

    public static MediaCodecInfo newInstance(String name, String mimeType, String codecMimeType, MediaCodecInfo.CodecCapabilities capabilities, boolean hardwareAccelerated, boolean softwareOnly, boolean vendor, boolean forceDisableAdaptive, boolean forceSecure) {
        return new MediaCodecInfo(name, mimeType, codecMimeType, capabilities, false, hardwareAccelerated, softwareOnly, vendor, forceDisableAdaptive, forceSecure);
    }

    private MediaCodecInfo(String name, String mimeType, String codecMimeType, MediaCodecInfo.CodecCapabilities capabilities, boolean passthrough, boolean hardwareAccelerated, boolean softwareOnly, boolean vendor, boolean forceDisableAdaptive, boolean forceSecure) {
        this.name = (String) Assertions.checkNotNull(name);
        this.mimeType = mimeType;
        this.codecMimeType = codecMimeType;
        this.capabilities = capabilities;
        this.passthrough = passthrough;
        this.hardwareAccelerated = hardwareAccelerated;
        this.softwareOnly = softwareOnly;
        this.vendor = vendor;
        boolean z = true;
        this.adaptive = !forceDisableAdaptive && capabilities != null && isAdaptive(capabilities);
        this.tunneling = capabilities != null && isTunneling(capabilities);
        if (!forceSecure && (capabilities == null || !isSecure(capabilities))) {
            z = false;
        }
        this.secure = z;
        this.isVideo = MimeTypes.isVideo(mimeType);
    }

    public String toString() {
        return this.name;
    }

    public MediaCodecInfo.CodecProfileLevel[] getProfileLevels() {
        MediaCodecInfo.CodecCapabilities codecCapabilities = this.capabilities;
        if (codecCapabilities == null || codecCapabilities.profileLevels == null) {
            return new MediaCodecInfo.CodecProfileLevel[0];
        }
        return this.capabilities.profileLevels;
    }

    public int getMaxSupportedInstances() {
        MediaCodecInfo.CodecCapabilities codecCapabilities;
        if (Util.SDK_INT < 23 || (codecCapabilities = this.capabilities) == null) {
            return -1;
        }
        return getMaxSupportedInstancesV23(codecCapabilities);
    }

    public boolean isFormatSupported(Format format) throws MediaCodecUtil.DecoderQueryException {
        boolean z = false;
        if (!isCodecSupported(format)) {
            return false;
        }
        if (this.isVideo) {
            if (format.width <= 0 || format.height <= 0) {
                return true;
            }
            if (Util.SDK_INT >= 21) {
                return isVideoSizeAndRateSupportedV21(format.width, format.height, format.frameRate);
            }
            if (format.width * format.height <= MediaCodecUtil.maxH264DecodableFrameSize()) {
                z = true;
            }
            boolean isFormatSupported = z;
            if (!isFormatSupported) {
                logNoSupport("legacyFrameSize, " + format.width + "x" + format.height);
            }
            return isFormatSupported;
        }
        if (Util.SDK_INT >= 21) {
            if (format.sampleRate != -1 && !isAudioSampleRateSupportedV21(format.sampleRate)) {
                return false;
            }
            if (format.channelCount != -1 && !isAudioChannelCountSupportedV21(format.channelCount)) {
                return false;
            }
        }
        return true;
    }

    public boolean isCodecSupported(Format format) {
        String codecMimeType;
        MediaCodecInfo.CodecProfileLevel[] profileLevels;
        if (format.codecs == null || this.mimeType == null || (codecMimeType = MimeTypes.getMediaMimeType(format.codecs)) == null) {
            return true;
        }
        if (!this.mimeType.equals(codecMimeType)) {
            logNoSupport("codec.mime " + format.codecs + ", " + codecMimeType);
            return false;
        }
        Pair<Integer, Integer> codecProfileAndLevel = MediaCodecUtil.getCodecProfileAndLevel(format);
        if (codecProfileAndLevel == null) {
            return true;
        }
        int profile = ((Integer) codecProfileAndLevel.first).intValue();
        int level = ((Integer) codecProfileAndLevel.second).intValue();
        if (!this.isVideo && profile != 42) {
            return true;
        }
        for (MediaCodecInfo.CodecProfileLevel capabilities : getProfileLevels()) {
            if (capabilities.profile == profile && capabilities.level >= level) {
                return true;
            }
        }
        logNoSupport("codec.profileLevel, " + format.codecs + ", " + codecMimeType);
        return false;
    }

    public boolean isHdr10PlusOutOfBandMetadataSupported() {
        MediaCodecInfo.CodecProfileLevel[] profileLevels;
        if (Util.SDK_INT >= 29 && MimeTypes.VIDEO_VP9.equals(this.mimeType)) {
            for (MediaCodecInfo.CodecProfileLevel capabilities : getProfileLevels()) {
                if (capabilities.profile == 16384) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isSeamlessAdaptationSupported(Format format) {
        if (this.isVideo) {
            return this.adaptive;
        }
        Pair<Integer, Integer> codecProfileLevel = MediaCodecUtil.getCodecProfileAndLevel(format);
        return codecProfileLevel != null && ((Integer) codecProfileLevel.first).intValue() == 42;
    }

    public boolean isSeamlessAdaptationSupported(Format oldFormat, Format newFormat, boolean isNewFormatComplete) {
        if (this.isVideo) {
            return oldFormat.sampleMimeType.equals(newFormat.sampleMimeType) && oldFormat.rotationDegrees == newFormat.rotationDegrees && (this.adaptive || (oldFormat.width == newFormat.width && oldFormat.height == newFormat.height)) && ((!isNewFormatComplete && newFormat.colorInfo == null) || Util.areEqual(oldFormat.colorInfo, newFormat.colorInfo));
        } else if (!"audio/mp4a-latm".equals(this.mimeType) || !oldFormat.sampleMimeType.equals(newFormat.sampleMimeType) || oldFormat.channelCount != newFormat.channelCount || oldFormat.sampleRate != newFormat.sampleRate) {
            return false;
        } else {
            Pair<Integer, Integer> oldCodecProfileLevel = MediaCodecUtil.getCodecProfileAndLevel(oldFormat);
            Pair<Integer, Integer> newCodecProfileLevel = MediaCodecUtil.getCodecProfileAndLevel(newFormat);
            if (oldCodecProfileLevel == null || newCodecProfileLevel == null) {
                return false;
            }
            int oldProfile = ((Integer) oldCodecProfileLevel.first).intValue();
            int newProfile = ((Integer) newCodecProfileLevel.first).intValue();
            return oldProfile == 42 && newProfile == 42;
        }
    }

    public boolean isVideoSizeAndRateSupportedV21(int width, int height, double frameRate) {
        MediaCodecInfo.CodecCapabilities codecCapabilities = this.capabilities;
        if (codecCapabilities == null) {
            logNoSupport("sizeAndRate.caps");
            return false;
        }
        MediaCodecInfo.VideoCapabilities videoCapabilities = codecCapabilities.getVideoCapabilities();
        if (videoCapabilities == null) {
            logNoSupport("sizeAndRate.vCaps");
            return false;
        } else if (!areSizeAndRateSupportedV21(videoCapabilities, width, height, frameRate)) {
            if (width >= height || !enableRotatedVerticalResolutionWorkaround(this.name) || !areSizeAndRateSupportedV21(videoCapabilities, height, width, frameRate)) {
                logNoSupport("sizeAndRate.support, " + width + "x" + height + "x" + frameRate);
                return false;
            }
            logAssumedSupport("sizeAndRate.rotated, " + width + "x" + height + "x" + frameRate);
            return true;
        } else {
            return true;
        }
    }

    public Point alignVideoSizeV21(int width, int height) {
        MediaCodecInfo.VideoCapabilities videoCapabilities;
        MediaCodecInfo.CodecCapabilities codecCapabilities = this.capabilities;
        if (codecCapabilities == null || (videoCapabilities = codecCapabilities.getVideoCapabilities()) == null) {
            return null;
        }
        return alignVideoSizeV21(videoCapabilities, width, height);
    }

    public boolean isAudioSampleRateSupportedV21(int sampleRate) {
        MediaCodecInfo.CodecCapabilities codecCapabilities = this.capabilities;
        if (codecCapabilities == null) {
            logNoSupport("sampleRate.caps");
            return false;
        }
        MediaCodecInfo.AudioCapabilities audioCapabilities = codecCapabilities.getAudioCapabilities();
        if (audioCapabilities == null) {
            logNoSupport("sampleRate.aCaps");
            return false;
        } else if (!audioCapabilities.isSampleRateSupported(sampleRate)) {
            logNoSupport("sampleRate.support, " + sampleRate);
            return false;
        } else {
            return true;
        }
    }

    public boolean isAudioChannelCountSupportedV21(int channelCount) {
        MediaCodecInfo.CodecCapabilities codecCapabilities = this.capabilities;
        if (codecCapabilities == null) {
            logNoSupport("channelCount.caps");
            return false;
        }
        MediaCodecInfo.AudioCapabilities audioCapabilities = codecCapabilities.getAudioCapabilities();
        if (audioCapabilities == null) {
            logNoSupport("channelCount.aCaps");
            return false;
        }
        int maxInputChannelCount = adjustMaxInputChannelCount(this.name, this.mimeType, audioCapabilities.getMaxInputChannelCount());
        if (maxInputChannelCount < channelCount) {
            logNoSupport("channelCount.support, " + channelCount);
            return false;
        }
        return true;
    }

    private void logNoSupport(String message) {
        Log.d(TAG, "NoSupport [" + message + "] [" + this.name + ", " + this.mimeType + "] [" + Util.DEVICE_DEBUG_INFO + "]");
    }

    private void logAssumedSupport(String message) {
        Log.d(TAG, "AssumedSupport [" + message + "] [" + this.name + ", " + this.mimeType + "] [" + Util.DEVICE_DEBUG_INFO + "]");
    }

    private static int adjustMaxInputChannelCount(String name, String mimeType, int maxChannelCount) {
        int assumedMaxChannelCount;
        if (maxChannelCount > 1 || (Util.SDK_INT >= 26 && maxChannelCount > 0)) {
            return maxChannelCount;
        }
        if (MimeTypes.AUDIO_MPEG.equals(mimeType) || MimeTypes.AUDIO_AMR_NB.equals(mimeType) || MimeTypes.AUDIO_AMR_WB.equals(mimeType) || "audio/mp4a-latm".equals(mimeType) || MimeTypes.AUDIO_VORBIS.equals(mimeType) || MimeTypes.AUDIO_OPUS.equals(mimeType) || MimeTypes.AUDIO_RAW.equals(mimeType) || MimeTypes.AUDIO_FLAC.equals(mimeType) || MimeTypes.AUDIO_ALAW.equals(mimeType) || MimeTypes.AUDIO_MLAW.equals(mimeType) || MimeTypes.AUDIO_MSGSM.equals(mimeType)) {
            return maxChannelCount;
        }
        if (MimeTypes.AUDIO_AC3.equals(mimeType)) {
            assumedMaxChannelCount = 6;
        } else if (MimeTypes.AUDIO_E_AC3.equals(mimeType)) {
            assumedMaxChannelCount = 16;
        } else {
            assumedMaxChannelCount = 30;
        }
        Log.w(TAG, "AssumedMaxChannelAdjustment: " + name + ", [" + maxChannelCount + " to " + assumedMaxChannelCount + "]");
        return assumedMaxChannelCount;
    }

    private static boolean isAdaptive(MediaCodecInfo.CodecCapabilities capabilities) {
        return Util.SDK_INT >= 19 && isAdaptiveV19(capabilities);
    }

    private static boolean isAdaptiveV19(MediaCodecInfo.CodecCapabilities capabilities) {
        return capabilities.isFeatureSupported("adaptive-playback");
    }

    private static boolean isTunneling(MediaCodecInfo.CodecCapabilities capabilities) {
        return Util.SDK_INT >= 21 && isTunnelingV21(capabilities);
    }

    private static boolean isTunnelingV21(MediaCodecInfo.CodecCapabilities capabilities) {
        return capabilities.isFeatureSupported("tunneled-playback");
    }

    private static boolean isSecure(MediaCodecInfo.CodecCapabilities capabilities) {
        return Util.SDK_INT >= 21 && isSecureV21(capabilities);
    }

    private static boolean isSecureV21(MediaCodecInfo.CodecCapabilities capabilities) {
        return capabilities.isFeatureSupported("secure-playback");
    }

    private static boolean areSizeAndRateSupportedV21(MediaCodecInfo.VideoCapabilities capabilities, int width, int height, double frameRate) {
        Point alignedSize = alignVideoSizeV21(capabilities, width, height);
        int width2 = alignedSize.x;
        int height2 = alignedSize.y;
        if (frameRate == -1.0d || frameRate < 1.0d) {
            return capabilities.isSizeSupported(width2, height2);
        }
        double floorFrameRate = Math.floor(frameRate);
        return capabilities.areSizeAndRateSupported(width2, height2, floorFrameRate);
    }

    private static Point alignVideoSizeV21(MediaCodecInfo.VideoCapabilities capabilities, int width, int height) {
        int widthAlignment = capabilities.getWidthAlignment();
        int heightAlignment = capabilities.getHeightAlignment();
        return new Point(Util.ceilDivide(width, widthAlignment) * widthAlignment, Util.ceilDivide(height, heightAlignment) * heightAlignment);
    }

    private static int getMaxSupportedInstancesV23(MediaCodecInfo.CodecCapabilities capabilities) {
        return capabilities.getMaxSupportedInstances();
    }

    private static final boolean enableRotatedVerticalResolutionWorkaround(String name) {
        if ("OMX.MTK.VIDEO.DECODER.HEVC".equals(name) && "mcv5a".equals(Util.DEVICE)) {
            return false;
        }
        return true;
    }
}
