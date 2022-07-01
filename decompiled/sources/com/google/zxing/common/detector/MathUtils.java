package com.google.zxing.common.detector;
/* loaded from: classes.dex */
public final class MathUtils {
    public static int round(float f) {
        return (int) (f + (f < 0.0f ? -0.5f : 0.5f));
    }

    public static float distance(float f, float f2, float f3, float f4) {
        double d = f - f3;
        double d2 = f2 - f4;
        Double.isNaN(d);
        Double.isNaN(d);
        Double.isNaN(d2);
        Double.isNaN(d2);
        return (float) Math.sqrt((d * d) + (d2 * d2));
    }

    public static float distance(int i, int i2, int i3, int i4) {
        double d = i - i3;
        double d2 = i2 - i4;
        Double.isNaN(d);
        Double.isNaN(d);
        Double.isNaN(d2);
        Double.isNaN(d2);
        return (float) Math.sqrt((d * d) + (d2 * d2));
    }
}
