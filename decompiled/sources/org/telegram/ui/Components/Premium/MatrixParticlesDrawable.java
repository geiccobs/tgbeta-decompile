package org.telegram.ui.Components.Premium;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class MatrixParticlesDrawable {
    MatrixTextParticle[][] matrixTextParticles;
    ArrayList<Particle>[] particles;
    int size;
    RectF excludeRect = new RectF();
    Bitmap[] bitmaps = new Bitmap[16];
    Rect drawingRect = new Rect();
    Paint paint = new Paint();

    public void init() {
        this.size = AndroidUtilities.dp(16.0f);
        TextPaint textPaint = new TextPaint(65);
        textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rcondensedbold.ttf"));
        textPaint.setTextSize(this.size);
        textPaint.setColor(ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_premiumStartSmallStarsColor2), 30));
        textPaint.setTextAlign(Paint.Align.CENTER);
        int i = 0;
        while (i < 16) {
            char c = (char) (i < 10 ? i + 48 : (i - 10) + 65);
            Bitmap[] bitmapArr = this.bitmaps;
            int i2 = this.size;
            bitmapArr[i] = Bitmap.createBitmap(i2, i2, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(this.bitmaps[i]);
            String ch = Character.toString(c);
            int i3 = this.size;
            canvas.drawText(ch, i3 >> 1, i3, textPaint);
            i++;
        }
    }

    public void onDraw(Canvas canvas) {
        int i;
        Particle particle;
        int n;
        int y;
        int nx = this.drawingRect.width() / this.size;
        int ny = this.drawingRect.height() / this.size;
        if (nx == 0 || ny == 0) {
            return;
        }
        long currentTime = System.currentTimeMillis();
        ArrayList<Particle>[] arrayListArr = this.particles;
        AnonymousClass1 anonymousClass1 = null;
        if (arrayListArr == null || arrayListArr.length != nx + 1) {
            this.particles = new ArrayList[nx + 1];
            for (int x = 0; x <= nx; x++) {
                this.particles[x] = new ArrayList<>();
                Particle particle2 = new Particle();
                particle2.init(ny, currentTime);
                this.particles[x].add(particle2);
            }
        }
        MatrixTextParticle[][] matrixTextParticleArr = this.matrixTextParticles;
        int i2 = 0;
        if (matrixTextParticleArr == null || matrixTextParticleArr.length != nx + 1 || matrixTextParticleArr[0].length != ny + 1) {
            this.matrixTextParticles = new MatrixTextParticle[nx + 1];
            for (int x2 = 0; x2 <= nx; x2++) {
                this.matrixTextParticles[x2] = new MatrixTextParticle[ny + 1];
                for (int y2 = 0; y2 <= ny; y2++) {
                    this.matrixTextParticles[x2][y2] = new MatrixTextParticle();
                    this.matrixTextParticles[x2][y2].init(currentTime);
                }
            }
        }
        int x3 = 0;
        while (x3 <= nx) {
            ArrayList<Particle> list = this.particles[x3];
            int i3 = 0;
            while (i3 < list.size()) {
                Particle particle3 = list.get(i3);
                int i4 = 1;
                if (currentTime - particle3.time <= 50) {
                    i = i3;
                } else {
                    particle3.y++;
                    particle3.time = currentTime;
                    if (particle3.y - particle3.len >= ny) {
                        if (list.size() == 1) {
                            particle3.reset(currentTime);
                        } else {
                            list.remove(particle3);
                            i3--;
                        }
                    }
                    if (particle3.y > particle3.len && i3 == list.size() - 1 && Math.abs(Utilities.fastRandom.nextInt(4)) == 0) {
                        Particle newParticle = new Particle();
                        newParticle.reset(currentTime);
                        list.add(newParticle);
                    }
                    i = i3;
                }
                int i5 = particle3.y;
                int n2 = Math.min(i5, ny + 1);
                int y3 = Math.max(i2, particle3.y - particle3.len);
                while (y3 < n2) {
                    int i6 = this.size;
                    float finalX = i6 * x3;
                    float finalY = i6 * y3;
                    if (!this.excludeRect.contains(finalX, finalY)) {
                        float alpha = Utilities.clamp(((1.0f - ((particle3.y - y3) / (particle3.len - i4))) * 0.8f) + 0.2f, 1.0f, 0.0f);
                        y = y3;
                        n = n2;
                        particle = particle3;
                        this.matrixTextParticles[x3][y3].draw(canvas, finalX, finalY, currentTime, alpha);
                    } else {
                        y = y3;
                        n = n2;
                        particle = particle3;
                    }
                    y3 = y + 1;
                    n2 = n;
                    particle3 = particle;
                    i4 = 1;
                }
                i3 = i + 1;
                anonymousClass1 = null;
                i2 = 0;
            }
            x3++;
            anonymousClass1 = null;
            i2 = 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public class Particle {
        int len;
        long time;
        int y;

        private Particle() {
            MatrixParticlesDrawable.this = r1;
            this.len = 5;
        }

        public void init(int ny, long currentTime) {
            this.y = Math.abs(Utilities.fastRandom.nextInt() % ny);
            this.time = currentTime;
            this.len = Math.abs(Utilities.fastRandom.nextInt() % 6) + 4;
        }

        public void reset(long currentTime) {
            this.y = 0;
            this.time = currentTime;
            this.len = Math.abs(Utilities.fastRandom.nextInt() % 6) + 4;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public class MatrixTextParticle {
        int index;
        long lastUpdateTime;
        int nextIndex;
        long nextUpdateTime;

        private MatrixTextParticle() {
            MatrixParticlesDrawable.this = r1;
        }

        public void init(long time) {
            this.index = Math.abs(Utilities.fastRandom.nextInt() % 16);
            this.nextIndex = Math.abs(Utilities.fastRandom.nextInt() % 16);
            this.lastUpdateTime = time;
            this.nextUpdateTime = Math.abs(Utilities.fastRandom.nextInt() % 300) + time + 150;
        }

        public void draw(Canvas canvas, float x, float y, long currentTime, float alpha) {
            long j = this.nextUpdateTime;
            if (j - currentTime < 150) {
                float p = Utilities.clamp(1.0f - (((float) (j - currentTime)) / 150.0f), 1.0f, 0.0f);
                MatrixParticlesDrawable.this.paint.setAlpha((int) ((1.0f - p) * alpha * 255.0f));
                canvas.drawBitmap(MatrixParticlesDrawable.this.bitmaps[this.index], x, y, MatrixParticlesDrawable.this.paint);
                MatrixParticlesDrawable.this.paint.setAlpha((int) (p * alpha * 255.0f));
                canvas.drawBitmap(MatrixParticlesDrawable.this.bitmaps[this.nextIndex], x, y, MatrixParticlesDrawable.this.paint);
                MatrixParticlesDrawable.this.paint.setAlpha(255);
                if (p >= 1.0f) {
                    this.index = this.nextIndex;
                    this.lastUpdateTime = currentTime;
                    this.nextIndex = Math.abs(Utilities.fastRandom.nextInt() % 16);
                    this.nextUpdateTime = Math.abs(Utilities.fastRandom.nextInt() % 300) + currentTime + 150;
                    return;
                }
                return;
            }
            MatrixParticlesDrawable.this.paint.setAlpha((int) (255.0f * alpha));
            canvas.drawBitmap(MatrixParticlesDrawable.this.bitmaps[this.index], x, y, MatrixParticlesDrawable.this.paint);
        }
    }
}
