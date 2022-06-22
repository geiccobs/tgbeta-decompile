package androidx.core.graphics;

import android.graphics.Color;
/* loaded from: classes.dex */
public final class ColorUtils {
    private static final ThreadLocal<double[]> TEMP_ARRAY = new ThreadLocal<>();

    private static float constrain(float amount, float low, float high) {
        return amount < low ? low : amount > high ? high : amount;
    }

    public static int compositeColors(int foreground, int background) {
        int alpha = Color.alpha(background);
        int alpha2 = Color.alpha(foreground);
        int compositeAlpha = compositeAlpha(alpha2, alpha);
        return Color.argb(compositeAlpha, compositeComponent(Color.red(foreground), alpha2, Color.red(background), alpha, compositeAlpha), compositeComponent(Color.green(foreground), alpha2, Color.green(background), alpha, compositeAlpha), compositeComponent(Color.blue(foreground), alpha2, Color.blue(background), alpha, compositeAlpha));
    }

    private static int compositeAlpha(int foregroundAlpha, int backgroundAlpha) {
        return 255 - (((255 - backgroundAlpha) * (255 - foregroundAlpha)) / 255);
    }

    private static int compositeComponent(int fgC, int fgA, int bgC, int bgA, int a) {
        if (a == 0) {
            return 0;
        }
        return (((fgC * 255) * fgA) + ((bgC * bgA) * (255 - fgA))) / (a * 255);
    }

    public static double calculateLuminance(int color) {
        double[] tempDouble3Array = getTempDouble3Array();
        colorToXYZ(color, tempDouble3Array);
        return tempDouble3Array[1] / 100.0d;
    }

    public static double calculateContrast(int foreground, int background) {
        if (Color.alpha(background) != 255) {
            throw new IllegalArgumentException("background can not be translucent: #" + Integer.toHexString(background));
        }
        if (Color.alpha(foreground) < 255) {
            foreground = compositeColors(foreground, background);
        }
        double calculateLuminance = calculateLuminance(foreground) + 0.05d;
        double calculateLuminance2 = calculateLuminance(background) + 0.05d;
        return Math.max(calculateLuminance, calculateLuminance2) / Math.min(calculateLuminance, calculateLuminance2);
    }

    public static int calculateMinimumAlpha(int foreground, int background, float minContrastRatio) {
        int i = 255;
        if (Color.alpha(background) != 255) {
            throw new IllegalArgumentException("background can not be translucent: #" + Integer.toHexString(background));
        }
        double d = minContrastRatio;
        if (calculateContrast(setAlphaComponent(foreground, 255), background) < d) {
            return -1;
        }
        int i2 = 0;
        for (int i3 = 0; i3 <= 10 && i - i2 > 1; i3++) {
            int i4 = (i2 + i) / 2;
            if (calculateContrast(setAlphaComponent(foreground, i4), background) < d) {
                i2 = i4;
            } else {
                i = i4;
            }
        }
        return i;
    }

    public static void RGBToHSL(int r, int g, int b, float[] outHsl) {
        float f;
        float f2;
        float f3 = r / 255.0f;
        float f4 = g / 255.0f;
        float f5 = b / 255.0f;
        float max = Math.max(f3, Math.max(f4, f5));
        float min = Math.min(f3, Math.min(f4, f5));
        float f6 = max - min;
        float f7 = (max + min) / 2.0f;
        if (max == min) {
            f = 0.0f;
            f2 = 0.0f;
        } else {
            f = max == f3 ? ((f4 - f5) / f6) % 6.0f : max == f4 ? ((f5 - f3) / f6) + 2.0f : 4.0f + ((f3 - f4) / f6);
            f2 = f6 / (1.0f - Math.abs((2.0f * f7) - 1.0f));
        }
        float f8 = (f * 60.0f) % 360.0f;
        if (f8 < 0.0f) {
            f8 += 360.0f;
        }
        outHsl[0] = constrain(f8, 0.0f, 360.0f);
        outHsl[1] = constrain(f2, 0.0f, 1.0f);
        outHsl[2] = constrain(f7, 0.0f, 1.0f);
    }

    public static void colorToHSL(int color, float[] outHsl) {
        RGBToHSL(Color.red(color), Color.green(color), Color.blue(color), outHsl);
    }

    public static int setAlphaComponent(int color, int alpha) {
        if (alpha < 0 || alpha > 255) {
            throw new IllegalArgumentException("alpha must be between 0 and 255.");
        }
        return (color & 16777215) | (alpha << 24);
    }

    public static void colorToXYZ(int color, double[] outXyz) {
        RGBToXYZ(Color.red(color), Color.green(color), Color.blue(color), outXyz);
    }

    public static void RGBToXYZ(int r, int g, int b, double[] outXyz) {
        if (outXyz.length != 3) {
            throw new IllegalArgumentException("outXyz must have a length of 3.");
        }
        double d = r;
        Double.isNaN(d);
        double d2 = d / 255.0d;
        double pow = d2 < 0.04045d ? d2 / 12.92d : Math.pow((d2 + 0.055d) / 1.055d, 2.4d);
        double d3 = g;
        Double.isNaN(d3);
        double d4 = d3 / 255.0d;
        double pow2 = d4 < 0.04045d ? d4 / 12.92d : Math.pow((d4 + 0.055d) / 1.055d, 2.4d);
        double d5 = b;
        Double.isNaN(d5);
        double d6 = d5 / 255.0d;
        double pow3 = d6 < 0.04045d ? d6 / 12.92d : Math.pow((d6 + 0.055d) / 1.055d, 2.4d);
        outXyz[0] = ((0.4124d * pow) + (0.3576d * pow2) + (0.1805d * pow3)) * 100.0d;
        outXyz[1] = ((0.2126d * pow) + (0.7152d * pow2) + (0.0722d * pow3)) * 100.0d;
        outXyz[2] = ((pow * 0.0193d) + (pow2 * 0.1192d) + (pow3 * 0.9505d)) * 100.0d;
    }

    public static int blendARGB(int color1, int color2, float ratio) {
        float f = 1.0f - ratio;
        return Color.argb((int) ((Color.alpha(color1) * f) + (Color.alpha(color2) * ratio)), (int) ((Color.red(color1) * f) + (Color.red(color2) * ratio)), (int) ((Color.green(color1) * f) + (Color.green(color2) * ratio)), (int) ((Color.blue(color1) * f) + (Color.blue(color2) * ratio)));
    }

    private static double[] getTempDouble3Array() {
        ThreadLocal<double[]> threadLocal = TEMP_ARRAY;
        double[] dArr = threadLocal.get();
        if (dArr == null) {
            double[] dArr2 = new double[3];
            threadLocal.set(dArr2);
            return dArr2;
        }
        return dArr;
    }
}
