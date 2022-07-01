package org.webrtc;
/* loaded from: classes3.dex */
class FramerateBitrateAdjuster extends BaseBitrateAdjuster {
    private static final int INITIAL_FPS = 30;

    @Override // org.webrtc.BaseBitrateAdjuster, org.webrtc.BitrateAdjuster
    public int getCodecConfigFramerate() {
        return INITIAL_FPS;
    }

    @Override // org.webrtc.BaseBitrateAdjuster, org.webrtc.BitrateAdjuster
    public void setTargets(int i, int i2) {
        if (this.targetFps == 0) {
            i2 = INITIAL_FPS;
        }
        super.setTargets(i, i2);
        this.targetBitrateBps = (this.targetBitrateBps * INITIAL_FPS) / this.targetFps;
    }
}
