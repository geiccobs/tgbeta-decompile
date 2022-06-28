package org.webrtc;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
/* loaded from: classes5.dex */
class DynamicBitrateAdjuster extends BaseBitrateAdjuster {
    private static final double BITRATE_ADJUSTMENT_MAX_SCALE = 4.0d;
    private static final double BITRATE_ADJUSTMENT_SEC = 3.0d;
    private static final int BITRATE_ADJUSTMENT_STEPS = 20;
    private static final double BITS_PER_BYTE = 8.0d;
    private int bitrateAdjustmentScaleExp;
    private double deviationBytes;
    private double timeSinceLastAdjustmentMs;

    @Override // org.webrtc.BaseBitrateAdjuster, org.webrtc.BitrateAdjuster
    public void setTargets(int targetBitrateBps, int targetFps) {
        if (this.targetBitrateBps > 0 && targetBitrateBps < this.targetBitrateBps) {
            double d = this.deviationBytes;
            double d2 = targetBitrateBps;
            Double.isNaN(d2);
            double d3 = d * d2;
            double d4 = this.targetBitrateBps;
            Double.isNaN(d4);
            this.deviationBytes = d3 / d4;
        }
        super.setTargets(targetBitrateBps, targetFps);
    }

    @Override // org.webrtc.BaseBitrateAdjuster, org.webrtc.BitrateAdjuster
    public void reportEncodedFrame(int size) {
        if (this.targetFps == 0) {
            return;
        }
        double d = this.targetBitrateBps;
        Double.isNaN(d);
        double d2 = d / BITS_PER_BYTE;
        double d3 = this.targetFps;
        Double.isNaN(d3);
        double expectedBytesPerFrame = d2 / d3;
        double d4 = this.deviationBytes;
        double d5 = size;
        Double.isNaN(d5);
        this.deviationBytes = d4 + (d5 - expectedBytesPerFrame);
        double d6 = this.timeSinceLastAdjustmentMs;
        double d7 = this.targetFps;
        Double.isNaN(d7);
        this.timeSinceLastAdjustmentMs = d6 + (1000.0d / d7);
        double d8 = this.targetBitrateBps;
        Double.isNaN(d8);
        double deviationThresholdBytes = d8 / BITS_PER_BYTE;
        double deviationCap = BITRATE_ADJUSTMENT_SEC * deviationThresholdBytes;
        double min = Math.min(this.deviationBytes, deviationCap);
        this.deviationBytes = min;
        double max = Math.max(min, -deviationCap);
        this.deviationBytes = max;
        if (this.timeSinceLastAdjustmentMs <= 3000.0d) {
            return;
        }
        if (max > deviationThresholdBytes) {
            int bitrateAdjustmentInc = (int) ((max / deviationThresholdBytes) + 0.5d);
            int i = this.bitrateAdjustmentScaleExp - bitrateAdjustmentInc;
            this.bitrateAdjustmentScaleExp = i;
            this.bitrateAdjustmentScaleExp = Math.max(i, -20);
            this.deviationBytes = deviationThresholdBytes;
        } else if (max < (-deviationThresholdBytes)) {
            int bitrateAdjustmentInc2 = (int) (((-max) / deviationThresholdBytes) + 0.5d);
            int i2 = this.bitrateAdjustmentScaleExp + bitrateAdjustmentInc2;
            this.bitrateAdjustmentScaleExp = i2;
            this.bitrateAdjustmentScaleExp = Math.min(i2, 20);
            this.deviationBytes = -deviationThresholdBytes;
        }
        this.timeSinceLastAdjustmentMs = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
    }

    private double getBitrateAdjustmentScale() {
        double d = this.bitrateAdjustmentScaleExp;
        Double.isNaN(d);
        return Math.pow(BITRATE_ADJUSTMENT_MAX_SCALE, d / 20.0d);
    }

    @Override // org.webrtc.BaseBitrateAdjuster, org.webrtc.BitrateAdjuster
    public int getAdjustedBitrateBps() {
        double d = this.targetBitrateBps;
        double bitrateAdjustmentScale = getBitrateAdjustmentScale();
        Double.isNaN(d);
        return (int) (d * bitrateAdjustmentScale);
    }
}
