package org.webrtc;

import android.media.MediaCodecInfo;
import android.os.Build;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.voip.Instance;
import org.telegram.messenger.voip.VoIPService;
import org.webrtc.EglBase;
import org.webrtc.EglBase14;
import org.webrtc.VideoEncoderFactory;
/* loaded from: classes5.dex */
public class HardwareVideoEncoderFactory implements VideoEncoderFactory {
    private static final int QCOM_VP8_KEY_FRAME_INTERVAL_ANDROID_L_MS = 15000;
    private static final int QCOM_VP8_KEY_FRAME_INTERVAL_ANDROID_M_MS = 20000;
    private static final int QCOM_VP8_KEY_FRAME_INTERVAL_ANDROID_N_MS = 15000;
    private static final String TAG = "HardwareVideoEncoderFactory";
    private final Predicate<MediaCodecInfo> codecAllowedPredicate;
    private final boolean enableH264HighProfile;
    private final boolean enableIntelVp8Encoder;
    private final EglBase14.Context sharedContext;

    @Override // org.webrtc.VideoEncoderFactory
    public /* synthetic */ VideoEncoderFactory.VideoEncoderSelector getEncoderSelector() {
        return VideoEncoderFactory.CC.$default$getEncoderSelector(this);
    }

    @Override // org.webrtc.VideoEncoderFactory
    public /* synthetic */ VideoCodecInfo[] getImplementations() {
        VideoCodecInfo[] supportedCodecs;
        supportedCodecs = getSupportedCodecs();
        return supportedCodecs;
    }

    public HardwareVideoEncoderFactory(EglBase.Context sharedContext, boolean enableIntelVp8Encoder, boolean enableH264HighProfile) {
        this(sharedContext, enableIntelVp8Encoder, enableH264HighProfile, null);
    }

    public HardwareVideoEncoderFactory(EglBase.Context sharedContext, boolean enableIntelVp8Encoder, boolean enableH264HighProfile, Predicate<MediaCodecInfo> codecAllowedPredicate) {
        if (sharedContext instanceof EglBase14.Context) {
            this.sharedContext = (EglBase14.Context) sharedContext;
        } else {
            Logging.w(TAG, "No shared EglBase.Context.  Encoders will not use texture mode.");
            this.sharedContext = null;
        }
        this.enableIntelVp8Encoder = enableIntelVp8Encoder;
        this.enableH264HighProfile = enableH264HighProfile;
        this.codecAllowedPredicate = codecAllowedPredicate;
    }

    @Deprecated
    public HardwareVideoEncoderFactory(boolean enableIntelVp8Encoder, boolean enableH264HighProfile) {
        this(null, enableIntelVp8Encoder, enableH264HighProfile);
    }

    @Override // org.webrtc.VideoEncoderFactory
    public VideoEncoder createEncoder(VideoCodecInfo input) {
        VideoCodecMimeType type;
        MediaCodecInfo info;
        if (Build.VERSION.SDK_INT >= 19 && (info = findCodecForType((type = VideoCodecMimeType.valueOf(input.name)))) != null) {
            String codecName = info.getName();
            String mime = type.mimeType();
            Integer surfaceColorFormat = MediaCodecUtils.selectColorFormat(MediaCodecUtils.TEXTURE_COLOR_FORMATS, info.getCapabilitiesForType(mime));
            Integer yuvColorFormat = MediaCodecUtils.selectColorFormat(MediaCodecUtils.ENCODER_COLOR_FORMATS, info.getCapabilitiesForType(mime));
            if (type == VideoCodecMimeType.H264) {
                boolean isHighProfile = H264Utils.isSameH264Profile(input.params, MediaCodecUtils.getCodecProperties(type, true));
                boolean isBaselineProfile = H264Utils.isSameH264Profile(input.params, MediaCodecUtils.getCodecProperties(type, false));
                if (!isHighProfile && !isBaselineProfile) {
                    return null;
                }
                if (isHighProfile && !isH264HighProfileSupported(info)) {
                    return null;
                }
            }
            return new HardwareVideoEncoder(new MediaCodecWrapperFactoryImpl(), codecName, type, surfaceColorFormat, yuvColorFormat, input.params, getKeyFrameIntervalSec(type), getForcedKeyFrameIntervalMs(type, codecName), createBitrateAdjuster(type, codecName), this.sharedContext);
        }
        return null;
    }

    @Override // org.webrtc.VideoEncoderFactory
    public VideoCodecInfo[] getSupportedCodecs() {
        if (Build.VERSION.SDK_INT < 19 || (VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().groupCall != null)) {
            return new VideoCodecInfo[0];
        }
        List<VideoCodecInfo> supportedCodecInfos = new ArrayList<>();
        VideoCodecMimeType[] videoCodecMimeTypeArr = {VideoCodecMimeType.VP8, VideoCodecMimeType.VP9, VideoCodecMimeType.H264, VideoCodecMimeType.H265};
        for (int i = 0; i < 4; i++) {
            VideoCodecMimeType type = videoCodecMimeTypeArr[i];
            MediaCodecInfo codec = findCodecForType(type);
            if (codec != null) {
                String name = type.name();
                if (type == VideoCodecMimeType.H264 && isH264HighProfileSupported(codec)) {
                    supportedCodecInfos.add(new VideoCodecInfo(name, MediaCodecUtils.getCodecProperties(type, true)));
                }
                supportedCodecInfos.add(new VideoCodecInfo(name, MediaCodecUtils.getCodecProperties(type, false)));
            }
        }
        return (VideoCodecInfo[]) supportedCodecInfos.toArray(new VideoCodecInfo[supportedCodecInfos.size()]);
    }

    private MediaCodecInfo findCodecForType(VideoCodecMimeType type) {
        ArrayList<MediaCodecInfo> infos = MediaCodecUtils.getSortedCodecsList();
        int count = infos.size();
        for (int i = 0; i < count; i++) {
            MediaCodecInfo info = infos.get(i);
            if (info != null && info.isEncoder() && isSupportedCodec(info, type)) {
                return info;
            }
        }
        return null;
    }

    private boolean isSupportedCodec(MediaCodecInfo info, VideoCodecMimeType type) {
        return MediaCodecUtils.codecSupportsType(info, type) && MediaCodecUtils.selectColorFormat(MediaCodecUtils.ENCODER_COLOR_FORMATS, info.getCapabilitiesForType(type.mimeType())) != null && isHardwareSupportedInCurrentSdk(info, type) && isMediaCodecAllowed(info);
    }

    private boolean isHardwareSupportedInCurrentSdk(MediaCodecInfo info, VideoCodecMimeType type) {
        if (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().groupCall == null) {
            Instance.ServerConfig config = Instance.getGlobalServerConfig();
            if (!config.enable_h264_encoder && !config.enable_h265_encoder && !config.enable_vp8_encoder && !config.enable_vp9_encoder) {
                return false;
            }
            switch (AnonymousClass1.$SwitchMap$org$webrtc$VideoCodecMimeType[type.ordinal()]) {
                case 1:
                    return isHardwareSupportedInCurrentSdkVp8(info);
                case 2:
                    return isHardwareSupportedInCurrentSdkVp9(info);
                case 3:
                    return isHardwareSupportedInCurrentSdkH264(info);
                case 4:
                    return isHardwareSupportedInCurrentSdkH265(info);
                default:
                    return false;
            }
        }
        return false;
    }

    /* renamed from: org.webrtc.HardwareVideoEncoderFactory$1 */
    /* loaded from: classes5.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$webrtc$VideoCodecMimeType;

        static {
            int[] iArr = new int[VideoCodecMimeType.values().length];
            $SwitchMap$org$webrtc$VideoCodecMimeType = iArr;
            try {
                iArr[VideoCodecMimeType.VP8.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$webrtc$VideoCodecMimeType[VideoCodecMimeType.VP9.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$webrtc$VideoCodecMimeType[VideoCodecMimeType.H264.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$webrtc$VideoCodecMimeType[VideoCodecMimeType.H265.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    private boolean isHardwareSupportedInCurrentSdkVp8(MediaCodecInfo info) {
        if (!Instance.getGlobalServerConfig().enable_vp8_encoder) {
            return false;
        }
        String name = info.getName();
        return (name.startsWith("OMX.qcom.") && Build.VERSION.SDK_INT >= 19) || (name.startsWith("OMX.hisi.") && Build.VERSION.SDK_INT >= 19) || ((name.startsWith("OMX.Exynos.") && Build.VERSION.SDK_INT >= 23) || (name.startsWith("OMX.Intel.") && Build.VERSION.SDK_INT >= 21 && this.enableIntelVp8Encoder));
    }

    private boolean isHardwareSupportedInCurrentSdkVp9(MediaCodecInfo info) {
        if (!Instance.getGlobalServerConfig().enable_vp9_encoder) {
            return false;
        }
        String name = info.getName();
        return (name.startsWith("OMX.qcom.") || name.startsWith("OMX.Exynos.") || name.startsWith("OMX.hisi.")) && Build.VERSION.SDK_INT >= 24;
    }

    private boolean isHardwareSupportedInCurrentSdkH264(MediaCodecInfo info) {
        if (!Instance.getGlobalServerConfig().enable_h264_encoder) {
            return false;
        }
        String name = info.getName();
        return (name.startsWith("OMX.qcom.") && Build.VERSION.SDK_INT >= 19) || (name.startsWith("OMX.Exynos.") && Build.VERSION.SDK_INT >= 21);
    }

    private boolean isHardwareSupportedInCurrentSdkH265(MediaCodecInfo info) {
        if (!Instance.getGlobalServerConfig().enable_h265_encoder) {
            return false;
        }
        String name = info.getName();
        return (name.startsWith("OMX.qcom.") && Build.VERSION.SDK_INT >= 19) || (name.startsWith("OMX.Exynos.") && Build.VERSION.SDK_INT >= 21);
    }

    private boolean isMediaCodecAllowed(MediaCodecInfo info) {
        Predicate<MediaCodecInfo> predicate = this.codecAllowedPredicate;
        if (predicate == null) {
            return true;
        }
        return predicate.test(info);
    }

    private int getKeyFrameIntervalSec(VideoCodecMimeType type) {
        switch (AnonymousClass1.$SwitchMap$org$webrtc$VideoCodecMimeType[type.ordinal()]) {
            case 1:
            case 2:
                return 100;
            case 3:
            case 4:
                return 20;
            default:
                throw new IllegalArgumentException("Unsupported VideoCodecMimeType " + type);
        }
    }

    private int getForcedKeyFrameIntervalMs(VideoCodecMimeType type, String codecName) {
        if (type == VideoCodecMimeType.VP8 && codecName.startsWith("OMX.qcom.")) {
            if (Build.VERSION.SDK_INT == 21 || Build.VERSION.SDK_INT == 22) {
                return 15000;
            }
            if (Build.VERSION.SDK_INT == 23) {
                return 20000;
            }
            return Build.VERSION.SDK_INT > 23 ? 15000 : 0;
        }
        return 0;
    }

    private BitrateAdjuster createBitrateAdjuster(VideoCodecMimeType type, String codecName) {
        if (codecName.startsWith("OMX.Exynos.")) {
            if (type == VideoCodecMimeType.VP8) {
                return new DynamicBitrateAdjuster();
            }
            return new FramerateBitrateAdjuster();
        }
        return new BaseBitrateAdjuster();
    }

    private boolean isH264HighProfileSupported(MediaCodecInfo info) {
        return this.enableH264HighProfile && Build.VERSION.SDK_INT > 23 && info.getName().startsWith("OMX.Exynos.");
    }
}
