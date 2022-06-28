package org.webrtc;
/* loaded from: classes5.dex */
public class AudioTrack extends MediaStreamTrack {
    private static native void nativeSetVolume(long j, double d);

    public AudioTrack(long nativeTrack) {
        super(nativeTrack);
    }

    public void setVolume(double volume) {
        nativeSetVolume(getNativeAudioTrack(), volume);
    }

    public long getNativeAudioTrack() {
        return getNativeMediaStreamTrack();
    }
}
