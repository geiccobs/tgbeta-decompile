package org.webrtc.audio;
@Deprecated
/* loaded from: classes5.dex */
public class LegacyAudioDeviceModule implements AudioDeviceModule {
    @Override // org.webrtc.audio.AudioDeviceModule
    public long getNativeAudioDeviceModulePointer() {
        return 0L;
    }

    @Override // org.webrtc.audio.AudioDeviceModule
    public void release() {
    }

    @Override // org.webrtc.audio.AudioDeviceModule
    public void setSpeakerMute(boolean mute) {
        org.webrtc.voiceengine.WebRtcAudioTrack.setSpeakerMute(mute);
    }

    @Override // org.webrtc.audio.AudioDeviceModule
    public void setMicrophoneMute(boolean mute) {
        org.webrtc.voiceengine.WebRtcAudioRecord.setMicrophoneMute(mute);
    }
}
