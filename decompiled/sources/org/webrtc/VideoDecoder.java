package org.webrtc;
/* loaded from: classes5.dex */
public interface VideoDecoder {

    /* loaded from: classes5.dex */
    public interface Callback {
        void onDecodedFrame(VideoFrame videoFrame, Integer num, Integer num2);
    }

    long createNativeVideoDecoder();

    VideoCodecStatus decode(EncodedImage encodedImage, DecodeInfo decodeInfo);

    String getImplementationName();

    boolean getPrefersLateDecoding();

    VideoCodecStatus initDecode(Settings settings, Callback callback);

    VideoCodecStatus release();

    /* loaded from: classes5.dex */
    public static class Settings {
        public final int height;
        public final int numberOfCores;
        public final int width;

        public Settings(int numberOfCores, int width, int height) {
            this.numberOfCores = numberOfCores;
            this.width = width;
            this.height = height;
        }
    }

    /* loaded from: classes5.dex */
    public static class DecodeInfo {
        public final boolean isMissingFrames;
        public final long renderTimeMs;

        public DecodeInfo(boolean isMissingFrames, long renderTimeMs) {
            this.isMissingFrames = isMissingFrames;
            this.renderTimeMs = renderTimeMs;
        }
    }

    /* renamed from: org.webrtc.VideoDecoder$-CC */
    /* loaded from: classes5.dex */
    public final /* synthetic */ class CC {
        public static long $default$createNativeVideoDecoder(VideoDecoder _this) {
            return 0L;
        }
    }
}
