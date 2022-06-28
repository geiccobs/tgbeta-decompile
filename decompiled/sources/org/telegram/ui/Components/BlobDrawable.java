package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import java.util.Random;
/* loaded from: classes5.dex */
public class BlobDrawable {
    private static final float ANIMATION_SPEED_WAVE_HUGE = 0.65f;
    private static final float ANIMATION_SPEED_WAVE_SMALL = 0.45f;
    private static final float animationSpeed = 0.35000002f;
    private static final float animationSpeedTiny = 0.55f;
    private final float L;
    private final float N;
    public float amplitude;
    private float[] angle;
    private float[] angleNext;
    private float animateAmplitudeDiff;
    private float animateToAmplitude;
    public float maxRadius;
    public float minRadius;
    private float[] progress;
    private float[] radius;
    private float[] radiusNext;
    private float[] speed;
    public static float MAX_SPEED = 8.2f;
    public static float MIN_SPEED = 0.8f;
    public static float AMPLITUDE_SPEED = 0.33f;
    public static float SCALE_BIG = 0.807f;
    public static float SCALE_SMALL = 0.704f;
    public static float SCALE_BIG_MIN = 0.878f;
    public static float SCALE_SMALL_MIN = 0.926f;
    public static float FORM_BIG_MAX = 0.6f;
    public static float FORM_SMALL_MAX = 0.6f;
    public static float GLOBAL_SCALE = 1.0f;
    public static float FORM_BUTTON_MAX = 0.0f;
    public static float GRADIENT_SPEED_MIN = 0.5f;
    public static float GRADIENT_SPEED_MAX = 0.01f;
    public static float LIGHT_GRADIENT_SIZE = 0.5f;
    private Path path = new Path();
    public Paint paint = new Paint(1);
    private float[] pointStart = new float[4];
    private float[] pointEnd = new float[4];
    final Random random = new Random();
    public float cubicBezierK = 1.0f;
    private final Matrix m = new Matrix();

    public BlobDrawable(int n) {
        float f = n;
        this.N = f;
        double d = f * 2.0f;
        Double.isNaN(d);
        this.L = (float) (Math.tan(3.141592653589793d / d) * 1.3333333333333333d);
        this.radius = new float[n];
        this.angle = new float[n];
        this.radiusNext = new float[n];
        this.angleNext = new float[n];
        this.progress = new float[n];
        this.speed = new float[n];
        for (int i = 0; i < this.N; i++) {
            generateBlob(this.radius, this.angle, i);
            generateBlob(this.radiusNext, this.angleNext, i);
            this.progress[i] = 0.0f;
        }
    }

    private void generateBlob(float[] radius, float[] angle, int i) {
        float angleDif = (360.0f / this.N) * 0.05f;
        float f = this.maxRadius;
        float f2 = this.minRadius;
        float radDif = f - f2;
        radius[i] = f2 + (Math.abs((this.random.nextInt() % 100.0f) / 100.0f) * radDif);
        angle[i] = ((360.0f / this.N) * i) + (((this.random.nextInt() % 100.0f) / 100.0f) * angleDif);
        float[] fArr = this.speed;
        double abs = Math.abs(this.random.nextInt() % 100.0f) / 100.0f;
        Double.isNaN(abs);
        fArr[i] = (float) ((abs * 0.003d) + 0.017d);
    }

    public void update(float amplitude, float speedScale) {
        for (int i = 0; i < this.N; i++) {
            float[] fArr = this.progress;
            float f = fArr[i];
            float[] fArr2 = this.speed;
            fArr[i] = f + (fArr2[i] * MIN_SPEED) + (fArr2[i] * amplitude * MAX_SPEED * speedScale);
            if (fArr[i] >= 1.0f) {
                fArr[i] = 0.0f;
                float[] fArr3 = this.radius;
                float[] fArr4 = this.radiusNext;
                fArr3[i] = fArr4[i];
                float[] fArr5 = this.angle;
                float[] fArr6 = this.angleNext;
                fArr5[i] = fArr6[i];
                generateBlob(fArr4, fArr6, i);
            }
        }
    }

    public void draw(float cX, float cY, Canvas canvas, Paint paint) {
        float f = cX;
        this.path.reset();
        int i = 0;
        while (true) {
            float f2 = this.N;
            if (i < f2) {
                float[] fArr = this.progress;
                float progress = fArr[i];
                int nextIndex = ((float) (i + 1)) < f2 ? i + 1 : 0;
                float progressNext = fArr[nextIndex];
                float[] fArr2 = this.radius;
                float f3 = fArr2[i] * (1.0f - progress);
                float[] fArr3 = this.radiusNext;
                float r1 = f3 + (fArr3[i] * progress);
                float r2 = (fArr2[nextIndex] * (1.0f - progressNext)) + (fArr3[nextIndex] * progressNext);
                float[] fArr4 = this.angle;
                float f4 = fArr4[i] * (1.0f - progress);
                float[] fArr5 = this.angleNext;
                float angle1 = f4 + (fArr5[i] * progress);
                float angle2 = (fArr4[nextIndex] * (1.0f - progressNext)) + (fArr5[nextIndex] * progressNext);
                float l = this.L * (Math.min(r1, r2) + ((Math.max(r1, r2) - Math.min(r1, r2)) / 2.0f)) * this.cubicBezierK;
                this.m.reset();
                this.m.setRotate(angle1, f, cY);
                float[] fArr6 = this.pointStart;
                fArr6[0] = f;
                fArr6[1] = cY - r1;
                fArr6[2] = f + l;
                fArr6[3] = cY - r1;
                this.m.mapPoints(fArr6);
                float[] fArr7 = this.pointEnd;
                fArr7[0] = f;
                fArr7[1] = cY - r2;
                fArr7[2] = f - l;
                fArr7[3] = cY - r2;
                this.m.reset();
                this.m.setRotate(angle2, f, cY);
                this.m.mapPoints(this.pointEnd);
                if (i == 0) {
                    Path path = this.path;
                    float[] fArr8 = this.pointStart;
                    path.moveTo(fArr8[0], fArr8[1]);
                }
                Path path2 = this.path;
                float[] fArr9 = this.pointStart;
                float f5 = fArr9[2];
                float f6 = fArr9[3];
                float[] fArr10 = this.pointEnd;
                path2.cubicTo(f5, f6, fArr10[2], fArr10[3], fArr10[0], fArr10[1]);
                i++;
                f = cX;
            } else {
                canvas.save();
                canvas.drawPath(this.path, paint);
                canvas.restore();
                return;
            }
        }
    }

    public void generateBlob() {
        for (int i = 0; i < this.N; i++) {
            generateBlob(this.radius, this.angle, i);
            generateBlob(this.radiusNext, this.angleNext, i);
            this.progress[i] = 0.0f;
        }
    }

    public void setValue(float value, boolean isBig) {
        this.animateToAmplitude = value;
        if (isBig) {
            float f = this.amplitude;
            if (value > f) {
                this.animateAmplitudeDiff = (value - f) / 205.0f;
                return;
            } else {
                this.animateAmplitudeDiff = (value - f) / 275.0f;
                return;
            }
        }
        float f2 = this.amplitude;
        if (value > f2) {
            this.animateAmplitudeDiff = (value - f2) / 320.0f;
        } else {
            this.animateAmplitudeDiff = (value - f2) / 375.0f;
        }
    }

    public void updateAmplitude(long dt) {
        float f = this.animateToAmplitude;
        float f2 = this.amplitude;
        if (f != f2) {
            float f3 = this.animateAmplitudeDiff;
            float f4 = f2 + (((float) dt) * f3);
            this.amplitude = f4;
            if (f3 > 0.0f) {
                if (f4 > f) {
                    this.amplitude = f;
                }
            } else if (f4 < f) {
                this.amplitude = f;
            }
        }
    }
}
