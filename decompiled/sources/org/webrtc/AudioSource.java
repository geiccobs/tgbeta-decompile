package org.webrtc;
/* loaded from: classes5.dex */
public class AudioSource extends MediaSource {
    public AudioSource(long nativeSource) {
        super(nativeSource);
    }

    public long getNativeAudioSource() {
        return getNativeMediaSource();
    }
}
