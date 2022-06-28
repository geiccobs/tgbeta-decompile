package org.webrtc;
/* loaded from: classes5.dex */
public interface VideoDecoderFactory {
    @Deprecated
    VideoDecoder createDecoder(String str);

    VideoDecoder createDecoder(VideoCodecInfo videoCodecInfo);

    VideoCodecInfo[] getSupportedCodecs();

    /* renamed from: org.webrtc.VideoDecoderFactory$-CC */
    /* loaded from: classes5.dex */
    public final /* synthetic */ class CC {
        @Deprecated
        public static VideoDecoder $default$createDecoder(VideoDecoderFactory _this, String codecType) {
            throw new UnsupportedOperationException("Deprecated and not implemented.");
        }

        public static VideoCodecInfo[] $default$getSupportedCodecs(VideoDecoderFactory _this) {
            return new VideoCodecInfo[0];
        }
    }
}
