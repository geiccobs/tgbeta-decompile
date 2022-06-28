package org.webrtc;
/* loaded from: classes5.dex */
class BaseBitrateAdjuster implements BitrateAdjuster {
    protected int targetBitrateBps;
    protected int targetFps;

    @Override // org.webrtc.BitrateAdjuster
    public void setTargets(int targetBitrateBps, int targetFps) {
        this.targetBitrateBps = targetBitrateBps;
        this.targetFps = targetFps;
    }

    @Override // org.webrtc.BitrateAdjuster
    public void reportEncodedFrame(int size) {
    }

    @Override // org.webrtc.BitrateAdjuster
    public int getAdjustedBitrateBps() {
        return this.targetBitrateBps;
    }

    @Override // org.webrtc.BitrateAdjuster
    public int getCodecConfigFramerate() {
        return this.targetFps;
    }
}
