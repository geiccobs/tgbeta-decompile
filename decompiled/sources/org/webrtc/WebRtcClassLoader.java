package org.webrtc;
/* loaded from: classes5.dex */
class WebRtcClassLoader {
    WebRtcClassLoader() {
    }

    static Object getClassLoader() {
        Object loader = WebRtcClassLoader.class.getClassLoader();
        if (loader == null) {
            throw new RuntimeException("Failed to get WebRTC class loader.");
        }
        return loader;
    }
}
