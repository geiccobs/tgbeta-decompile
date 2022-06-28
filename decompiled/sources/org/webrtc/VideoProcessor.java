package org.webrtc;

import org.webrtc.VideoFrame;
/* loaded from: classes5.dex */
public interface VideoProcessor extends CapturerObserver {
    void onFrameCaptured(VideoFrame videoFrame, FrameAdaptationParameters frameAdaptationParameters);

    void setSink(VideoSink videoSink);

    /* loaded from: classes5.dex */
    public static class FrameAdaptationParameters {
        public final int cropHeight;
        public final int cropWidth;
        public final int cropX;
        public final int cropY;
        public final boolean drop;
        public final int scaleHeight;
        public final int scaleWidth;
        public final long timestampNs;

        public FrameAdaptationParameters(int cropX, int cropY, int cropWidth, int cropHeight, int scaleWidth, int scaleHeight, long timestampNs, boolean drop) {
            this.cropX = cropX;
            this.cropY = cropY;
            this.cropWidth = cropWidth;
            this.cropHeight = cropHeight;
            this.scaleWidth = scaleWidth;
            this.scaleHeight = scaleHeight;
            this.timestampNs = timestampNs;
            this.drop = drop;
        }
    }

    /* renamed from: org.webrtc.VideoProcessor$-CC */
    /* loaded from: classes5.dex */
    public final /* synthetic */ class CC {
        public static void $default$onFrameCaptured(VideoProcessor _this, VideoFrame frame, FrameAdaptationParameters parameters) {
            VideoFrame adaptedFrame = applyFrameAdaptationParameters(frame, parameters);
            if (adaptedFrame != null) {
                _this.onFrameCaptured(adaptedFrame);
                adaptedFrame.release();
            }
        }

        public static VideoFrame applyFrameAdaptationParameters(VideoFrame frame, FrameAdaptationParameters parameters) {
            if (parameters.drop) {
                return null;
            }
            VideoFrame.Buffer adaptedBuffer = frame.getBuffer().cropAndScale(parameters.cropX, parameters.cropY, parameters.cropWidth, parameters.cropHeight, parameters.scaleWidth, parameters.scaleHeight);
            return new VideoFrame(adaptedBuffer, frame.getRotation(), parameters.timestampNs);
        }
    }
}
