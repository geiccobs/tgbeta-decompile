package org.webrtc;
/* loaded from: classes5.dex */
class FramerateBitrateAdjuster extends BaseBitrateAdjuster {
    private static final int INITIAL_FPS = 30;

    @Override // org.webrtc.BaseBitrateAdjuster, org.webrtc.BitrateAdjuster
    public void setTargets(int targetBitrateBps, int targetFps) {
        if (this.targetFps == 0) {
            targetFps = 30;
        }
        super.setTargets(targetBitrateBps, targetFps);
        this.targetBitrateBps = (this.targetBitrateBps * 30) / this.targetFps;
    }

    @Override // org.webrtc.BaseBitrateAdjuster, org.webrtc.BitrateAdjuster
    public int getCodecConfigFramerate() {
        return 30;
    }
}
