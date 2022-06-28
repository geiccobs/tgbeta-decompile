package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import java.util.Random;
/* loaded from: classes5.dex */
public class LineBlobDrawable {
    private final float N;
    public float maxRadius;
    public float minRadius;
    private float[] progress;
    private float[] radius;
    private float[] radiusNext;
    private float[] speed;
    public Path path = new Path();
    public Paint paint = new Paint(1);
    final Random random = new Random();

    public LineBlobDrawable(int n) {
        this.N = n;
        this.radius = new float[n + 1];
        this.radiusNext = new float[n + 1];
        this.progress = new float[n + 1];
        this.speed = new float[n + 1];
        for (int i = 0; i <= this.N; i++) {
            generateBlob(this.radius, i);
            generateBlob(this.radiusNext, i);
            this.progress[i] = 0.0f;
        }
    }

    private void generateBlob(float[] radius, int i) {
        float f = this.maxRadius;
        float f2 = this.minRadius;
        float radDif = f - f2;
        radius[i] = f2 + (Math.abs((this.random.nextInt() % 100.0f) / 100.0f) * radDif);
        float[] fArr = this.speed;
        double abs = Math.abs(this.random.nextInt() % 100.0f) / 100.0f;
        Double.isNaN(abs);
        fArr[i] = (float) ((abs * 0.003d) + 0.017d);
    }

    public void update(float amplitude, float speedScale) {
        for (int i = 0; i <= this.N; i++) {
            float[] fArr = this.progress;
            fArr[i] = fArr[i] + (this.speed[i] * BlobDrawable.MIN_SPEED) + (this.speed[i] * amplitude * BlobDrawable.MAX_SPEED * speedScale);
            float[] fArr2 = this.progress;
            if (fArr2[i] >= 1.0f) {
                fArr2[i] = 0.0f;
                float[] fArr3 = this.radius;
                float[] fArr4 = this.radiusNext;
                fArr3[i] = fArr4[i];
                generateBlob(fArr4, i);
            }
        }
    }

    public void draw(float left, float top, float right, float bottom, Canvas canvas, Paint paint, float pinnedTop, float progressToPinned) {
        this.path.reset();
        this.path.moveTo(right, bottom);
        this.path.lineTo(left, bottom);
        int i = 0;
        while (true) {
            float f = this.N;
            if (i <= f) {
                if (i == 0) {
                    float progress = this.progress[i];
                    float r1 = (this.radius[i] * (1.0f - progress)) + (this.radiusNext[i] * progress);
                    float y = ((top - r1) * progressToPinned) + ((1.0f - progressToPinned) * pinnedTop);
                    this.path.lineTo(left, y);
                } else {
                    float[] fArr = this.progress;
                    float progress2 = fArr[i - 1];
                    float[] fArr2 = this.radius;
                    float f2 = fArr2[i - 1] * (1.0f - progress2);
                    float[] fArr3 = this.radiusNext;
                    float r12 = f2 + (fArr3[i - 1] * progress2);
                    float progressNext = fArr[i];
                    float r2 = (fArr2[i] * (1.0f - progressNext)) + (fArr3[i] * progressNext);
                    float x1 = ((right - left) / f) * (i - 1);
                    float x2 = ((right - left) / f) * i;
                    float cx = ((x2 - x1) / 2.0f) + x1;
                    float y1 = ((top - r12) * progressToPinned) + ((1.0f - progressToPinned) * pinnedTop);
                    float y2 = ((1.0f - progressToPinned) * pinnedTop) + ((top - r2) * progressToPinned);
                    this.path.cubicTo(cx, y1, cx, y2, x2, y2);
                    if (i == this.N) {
                        this.path.lineTo(right, bottom);
                    }
                }
                i++;
            } else {
                canvas.drawPath(this.path, paint);
                return;
            }
        }
    }

    public void generateBlob() {
        for (int i = 0; i < this.N; i++) {
            generateBlob(this.radius, i);
            generateBlob(this.radiusNext, i);
            this.progress[i] = 0.0f;
        }
    }
}
