package androidx.palette.graphics;
/* loaded from: classes3.dex */
public final class Target {
    public static final Target DARK_MUTED;
    public static final Target DARK_VIBRANT;
    static final int INDEX_MAX = 2;
    static final int INDEX_MIN = 0;
    static final int INDEX_TARGET = 1;
    static final int INDEX_WEIGHT_LUMA = 1;
    static final int INDEX_WEIGHT_POP = 2;
    static final int INDEX_WEIGHT_SAT = 0;
    public static final Target LIGHT_MUTED;
    public static final Target LIGHT_VIBRANT;
    private static final float MAX_DARK_LUMA = 0.45f;
    private static final float MAX_MUTED_SATURATION = 0.4f;
    private static final float MAX_NORMAL_LUMA = 0.7f;
    private static final float MIN_LIGHT_LUMA = 0.55f;
    private static final float MIN_NORMAL_LUMA = 0.3f;
    private static final float MIN_VIBRANT_SATURATION = 0.35f;
    public static final Target MUTED;
    private static final float TARGET_DARK_LUMA = 0.26f;
    private static final float TARGET_LIGHT_LUMA = 0.74f;
    private static final float TARGET_MUTED_SATURATION = 0.3f;
    private static final float TARGET_NORMAL_LUMA = 0.5f;
    private static final float TARGET_VIBRANT_SATURATION = 1.0f;
    public static final Target VIBRANT;
    private static final float WEIGHT_LUMA = 0.52f;
    private static final float WEIGHT_POPULATION = 0.24f;
    private static final float WEIGHT_SATURATION = 0.24f;
    boolean mIsExclusive;
    final float[] mLightnessTargets;
    final float[] mSaturationTargets;
    final float[] mWeights;

    static {
        Target target = new Target();
        LIGHT_VIBRANT = target;
        setDefaultLightLightnessValues(target);
        setDefaultVibrantSaturationValues(target);
        Target target2 = new Target();
        VIBRANT = target2;
        setDefaultNormalLightnessValues(target2);
        setDefaultVibrantSaturationValues(target2);
        Target target3 = new Target();
        DARK_VIBRANT = target3;
        setDefaultDarkLightnessValues(target3);
        setDefaultVibrantSaturationValues(target3);
        Target target4 = new Target();
        LIGHT_MUTED = target4;
        setDefaultLightLightnessValues(target4);
        setDefaultMutedSaturationValues(target4);
        Target target5 = new Target();
        MUTED = target5;
        setDefaultNormalLightnessValues(target5);
        setDefaultMutedSaturationValues(target5);
        Target target6 = new Target();
        DARK_MUTED = target6;
        setDefaultDarkLightnessValues(target6);
        setDefaultMutedSaturationValues(target6);
    }

    Target() {
        float[] fArr = new float[3];
        this.mSaturationTargets = fArr;
        float[] fArr2 = new float[3];
        this.mLightnessTargets = fArr2;
        this.mWeights = new float[3];
        this.mIsExclusive = true;
        setTargetDefaultValues(fArr);
        setTargetDefaultValues(fArr2);
        setDefaultWeights();
    }

    Target(Target from) {
        float[] fArr = new float[3];
        this.mSaturationTargets = fArr;
        float[] fArr2 = new float[3];
        this.mLightnessTargets = fArr2;
        float[] fArr3 = new float[3];
        this.mWeights = fArr3;
        this.mIsExclusive = true;
        System.arraycopy(from.mSaturationTargets, 0, fArr, 0, fArr.length);
        System.arraycopy(from.mLightnessTargets, 0, fArr2, 0, fArr2.length);
        System.arraycopy(from.mWeights, 0, fArr3, 0, fArr3.length);
    }

    public float getMinimumSaturation() {
        return this.mSaturationTargets[0];
    }

    public float getTargetSaturation() {
        return this.mSaturationTargets[1];
    }

    public float getMaximumSaturation() {
        return this.mSaturationTargets[2];
    }

    public float getMinimumLightness() {
        return this.mLightnessTargets[0];
    }

    public float getTargetLightness() {
        return this.mLightnessTargets[1];
    }

    public float getMaximumLightness() {
        return this.mLightnessTargets[2];
    }

    public float getSaturationWeight() {
        return this.mWeights[0];
    }

    public float getLightnessWeight() {
        return this.mWeights[1];
    }

    public float getPopulationWeight() {
        return this.mWeights[2];
    }

    public boolean isExclusive() {
        return this.mIsExclusive;
    }

    private static void setTargetDefaultValues(float[] values) {
        values[0] = 0.0f;
        values[1] = 0.5f;
        values[2] = 1.0f;
    }

    private void setDefaultWeights() {
        float[] fArr = this.mWeights;
        fArr[0] = 0.24f;
        fArr[1] = 0.52f;
        fArr[2] = 0.24f;
    }

    public void normalizeWeights() {
        float sum = 0.0f;
        int z = this.mWeights.length;
        for (int i = 0; i < z; i++) {
            float weight = this.mWeights[i];
            if (weight > 0.0f) {
                sum += weight;
            }
        }
        int i2 = (sum > 0.0f ? 1 : (sum == 0.0f ? 0 : -1));
        if (i2 != 0) {
            int z2 = this.mWeights.length;
            for (int i3 = 0; i3 < z2; i3++) {
                float[] fArr = this.mWeights;
                if (fArr[i3] > 0.0f) {
                    fArr[i3] = fArr[i3] / sum;
                }
            }
        }
    }

    private static void setDefaultDarkLightnessValues(Target target) {
        float[] fArr = target.mLightnessTargets;
        fArr[1] = 0.26f;
        fArr[2] = 0.45f;
    }

    private static void setDefaultNormalLightnessValues(Target target) {
        float[] fArr = target.mLightnessTargets;
        fArr[0] = 0.3f;
        fArr[1] = 0.5f;
        fArr[2] = 0.7f;
    }

    private static void setDefaultLightLightnessValues(Target target) {
        float[] fArr = target.mLightnessTargets;
        fArr[0] = 0.55f;
        fArr[1] = 0.74f;
    }

    private static void setDefaultVibrantSaturationValues(Target target) {
        float[] fArr = target.mSaturationTargets;
        fArr[0] = 0.35f;
        fArr[1] = 1.0f;
    }

    private static void setDefaultMutedSaturationValues(Target target) {
        float[] fArr = target.mSaturationTargets;
        fArr[1] = 0.3f;
        fArr[2] = 0.4f;
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private final Target mTarget;

        public Builder() {
            this.mTarget = new Target();
        }

        public Builder(Target target) {
            this.mTarget = new Target(target);
        }

        public Builder setMinimumSaturation(float value) {
            this.mTarget.mSaturationTargets[0] = value;
            return this;
        }

        public Builder setTargetSaturation(float value) {
            this.mTarget.mSaturationTargets[1] = value;
            return this;
        }

        public Builder setMaximumSaturation(float value) {
            this.mTarget.mSaturationTargets[2] = value;
            return this;
        }

        public Builder setMinimumLightness(float value) {
            this.mTarget.mLightnessTargets[0] = value;
            return this;
        }

        public Builder setTargetLightness(float value) {
            this.mTarget.mLightnessTargets[1] = value;
            return this;
        }

        public Builder setMaximumLightness(float value) {
            this.mTarget.mLightnessTargets[2] = value;
            return this;
        }

        public Builder setSaturationWeight(float weight) {
            this.mTarget.mWeights[0] = weight;
            return this;
        }

        public Builder setLightnessWeight(float weight) {
            this.mTarget.mWeights[1] = weight;
            return this;
        }

        public Builder setPopulationWeight(float weight) {
            this.mTarget.mWeights[2] = weight;
            return this;
        }

        public Builder setExclusive(boolean exclusive) {
            this.mTarget.mIsExclusive = exclusive;
            return this;
        }

        public Target build() {
            return this.mTarget;
        }
    }
}
