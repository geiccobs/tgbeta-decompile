package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import java.util.Random;
/* loaded from: classes5.dex */
public class CircleBezierDrawable {
    private final float L;
    private final int N;
    public float radius;
    public float radiusDiff;
    float[] randomAdditionals;
    public float randomK;
    private Path path = new Path();
    private float[] pointStart = new float[4];
    private float[] pointEnd = new float[4];
    private Matrix m = new Matrix();
    float globalRotate = 0.0f;
    public float idleStateDiff = 0.0f;
    public float cubicBezierK = 1.0f;
    final Random random = new Random();

    public CircleBezierDrawable(int n) {
        this.N = n;
        double d = n * 2;
        Double.isNaN(d);
        this.L = (float) (Math.tan(3.141592653589793d / d) * 1.3333333333333333d);
        this.randomAdditionals = new float[n];
        calculateRandomAdditionals();
    }

    public void calculateRandomAdditionals() {
        for (int i = 0; i < this.N; i++) {
            this.randomAdditionals[i] = (this.random.nextInt() % 100) / 100.0f;
        }
    }

    public void setAdditionals(int[] additionals) {
        for (int i = 0; i < this.N; i += 2) {
            float[] fArr = this.randomAdditionals;
            fArr[i] = additionals[i / 2];
            fArr[i + 1] = 0.0f;
        }
    }

    public void draw(float cX, float cY, Canvas canvas, Paint paint) {
        float f = this.radius;
        float f2 = this.idleStateDiff;
        float f3 = this.radiusDiff;
        float r1 = (f - (f2 / 2.0f)) - (f3 / 2.0f);
        float r2 = f + (f3 / 2.0f) + (f2 / 2.0f);
        float l = this.L * Math.max(r1, r2) * this.cubicBezierK;
        this.path.reset();
        for (int i = 0; i < this.N; i++) {
            this.m.reset();
            this.m.setRotate((360.0f / this.N) * i, cX, cY);
            float f4 = i % 2 == 0 ? r1 : r2;
            float f5 = this.randomK;
            float[] fArr = this.randomAdditionals;
            float r = f4 + (fArr[i] * f5);
            float[] fArr2 = this.pointStart;
            fArr2[0] = cX;
            fArr2[1] = cY - r;
            fArr2[2] = cX + l + (f5 * fArr[i] * this.L);
            fArr2[3] = cY - r;
            this.m.mapPoints(fArr2);
            int j = i + 1;
            if (j >= this.N) {
                j = 0;
            }
            float f6 = j % 2 == 0 ? r1 : r2;
            float f7 = this.randomK;
            float[] fArr3 = this.randomAdditionals;
            float r3 = f6 + (fArr3[j] * f7);
            float[] fArr4 = this.pointEnd;
            fArr4[0] = cX;
            fArr4[1] = cY - r3;
            fArr4[2] = (cX - l) + (f7 * fArr3[j] * this.L);
            fArr4[3] = cY - r3;
            this.m.reset();
            this.m.setRotate((360.0f / this.N) * j, cX, cY);
            this.m.mapPoints(this.pointEnd);
            if (i == 0) {
                Path path = this.path;
                float[] fArr5 = this.pointStart;
                path.moveTo(fArr5[0], fArr5[1]);
            }
            Path path2 = this.path;
            float[] fArr6 = this.pointStart;
            float f8 = fArr6[2];
            float f9 = fArr6[3];
            float[] fArr7 = this.pointEnd;
            path2.cubicTo(f8, f9, fArr7[2], fArr7[3], fArr7[0], fArr7[1]);
        }
        canvas.save();
        canvas.rotate(this.globalRotate, cX, cY);
        canvas.drawPath(this.path, paint);
        canvas.restore();
    }

    public void setRandomAdditions(float randomK) {
        this.randomK = randomK;
    }
}
