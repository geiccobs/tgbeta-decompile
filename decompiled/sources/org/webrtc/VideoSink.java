package org.webrtc;
/* loaded from: classes3.dex */
public interface VideoSink {

    /* renamed from: org.webrtc.VideoSink$-CC */
    /* loaded from: classes3.dex */
    public final /* synthetic */ class CC {
        public static void $default$setParentSink(VideoSink videoSink, VideoSink videoSink2) {
        }
    }

    @CalledByNative
    void onFrame(VideoFrame videoFrame);

    void setParentSink(VideoSink videoSink);
}
