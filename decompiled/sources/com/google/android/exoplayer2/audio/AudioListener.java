package com.google.android.exoplayer2.audio;
/* loaded from: classes.dex */
public interface AudioListener {
    void onAudioAttributesChanged(AudioAttributes audioAttributes);

    void onAudioSessionId(int i);

    void onVolumeChanged(float f);
}
