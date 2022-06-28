package org.webrtc;
/* loaded from: classes5.dex */
public interface VideoSink {
    void onFrame(VideoFrame videoFrame);

    void setParentSink(VideoSink videoSink);

    /* renamed from: org.webrtc.VideoSink$-CC */
    /* loaded from: classes5.dex */
    public final /* synthetic */ class CC {
        public static void $default$setParentSink(VideoSink _this, VideoSink parent) {
        }
    }
}
