package org.webrtc;
/* loaded from: classes5.dex */
public interface SdpObserver {
    void onCreateFailure(String str);

    void onCreateSuccess(SessionDescription sessionDescription);

    void onSetFailure(String str);

    void onSetSuccess();
}
