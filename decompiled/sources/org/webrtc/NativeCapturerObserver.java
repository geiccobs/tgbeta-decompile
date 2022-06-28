package org.webrtc;

import org.webrtc.VideoFrame;
import org.webrtc.VideoProcessor;
/* loaded from: classes5.dex */
public class NativeCapturerObserver implements CapturerObserver {
    private final NativeAndroidVideoTrackSource nativeAndroidVideoTrackSource;

    public NativeCapturerObserver(long nativeSource) {
        this.nativeAndroidVideoTrackSource = new NativeAndroidVideoTrackSource(nativeSource);
    }

    @Override // org.webrtc.CapturerObserver
    public void onCapturerStarted(boolean success) {
        this.nativeAndroidVideoTrackSource.setState(success);
    }

    @Override // org.webrtc.CapturerObserver
    public void onCapturerStopped() {
        this.nativeAndroidVideoTrackSource.setState(false);
    }

    public NativeAndroidVideoTrackSource getNativeAndroidVideoTrackSource() {
        return this.nativeAndroidVideoTrackSource;
    }

    @Override // org.webrtc.CapturerObserver
    public void onFrameCaptured(VideoFrame frame) {
        VideoProcessor.FrameAdaptationParameters parameters = this.nativeAndroidVideoTrackSource.adaptFrame(frame);
        if (parameters == null || parameters.cropWidth == 0 || parameters.cropHeight == 0) {
            return;
        }
        VideoFrame.Buffer adaptedBuffer = frame.getBuffer().cropAndScale(parameters.cropX, parameters.cropY, parameters.cropWidth, parameters.cropHeight, parameters.scaleWidth, parameters.scaleHeight);
        this.nativeAndroidVideoTrackSource.onFrameCaptured(new VideoFrame(adaptedBuffer, frame.getRotation(), parameters.timestampNs));
        adaptedBuffer.release();
    }
}
