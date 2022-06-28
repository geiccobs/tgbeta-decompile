package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.view.View;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class SnowflakesEffect {
    private int color;
    private long lastAnimationTime;
    Bitmap particleBitmap;
    private Paint particlePaint;
    private Paint particleThinPaint;
    private int viewType;
    private Paint bitmapPaint = new Paint();
    private String colorKey = Theme.key_actionBarDefaultTitle;
    final float angleDiff = 1.0471976f;
    private ArrayList<Particle> particles = new ArrayList<>();
    private ArrayList<Particle> freeParticles = new ArrayList<>();

    /* loaded from: classes5.dex */
    public class Particle {
        float alpha;
        float currentTime;
        float lifeTime;
        float scale;
        int type;
        float velocity;
        float vx;
        float vy;
        float x;
        float y;

        private Particle() {
            SnowflakesEffect.this = r1;
        }

        public void draw(Canvas canvas) {
            switch (this.type) {
                case 0:
                    SnowflakesEffect.this.particlePaint.setAlpha((int) (this.alpha * 255.0f));
                    canvas.drawPoint(this.x, this.y, SnowflakesEffect.this.particlePaint);
                    return;
                default:
                    float y1 = -1.5707964f;
                    if (SnowflakesEffect.this.particleBitmap == null) {
                        SnowflakesEffect.this.particleThinPaint.setAlpha(255);
                        SnowflakesEffect.this.particleBitmap = Bitmap.createBitmap(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), Bitmap.Config.ARGB_8888);
                        Canvas bitmapCanvas = new Canvas(SnowflakesEffect.this.particleBitmap);
                        float px = AndroidUtilities.dpf2(2.0f) * 2.0f;
                        float px1 = (-AndroidUtilities.dpf2(0.57f)) * 2.0f;
                        float py1 = 2.0f * AndroidUtilities.dpf2(1.55f);
                        for (int a = 0; a < 6; a++) {
                            float x = AndroidUtilities.dp(8.0f);
                            float y = AndroidUtilities.dp(8.0f);
                            float x1 = ((float) Math.cos(y1)) * px;
                            float y12 = ((float) Math.sin(y1)) * px;
                            float cx = x1 * 0.66f;
                            float cy = y12 * 0.66f;
                            bitmapCanvas.drawLine(x, y, x + x1, y + y12, SnowflakesEffect.this.particleThinPaint);
                            double d = y1;
                            Double.isNaN(d);
                            float angle2 = (float) (d - 1.5707963267948966d);
                            double cos = Math.cos(angle2);
                            double d2 = px1;
                            Double.isNaN(d2);
                            double d3 = cos * d2;
                            double sin = Math.sin(angle2);
                            float angle = y1;
                            double d4 = py1;
                            Double.isNaN(d4);
                            float x12 = (float) (d3 - (sin * d4));
                            double sin2 = Math.sin(angle2);
                            double d5 = px1;
                            Double.isNaN(d5);
                            double d6 = sin2 * d5;
                            double cos2 = Math.cos(angle2);
                            double d7 = py1;
                            Double.isNaN(d7);
                            float y13 = (float) (d6 + (cos2 * d7));
                            bitmapCanvas.drawLine(x + cx, y + cy, x + x12, y + y13, SnowflakesEffect.this.particleThinPaint);
                            double d8 = px1;
                            Double.isNaN(d8);
                            double d9 = (-Math.cos(angle2)) * d8;
                            double sin3 = Math.sin(angle2);
                            double d10 = py1;
                            Double.isNaN(d10);
                            float x13 = (float) (d9 - (sin3 * d10));
                            double d11 = px1;
                            Double.isNaN(d11);
                            double d12 = (-Math.sin(angle2)) * d11;
                            double cos3 = Math.cos(angle2);
                            double d13 = py1;
                            Double.isNaN(d13);
                            float y14 = (float) (d12 + (cos3 * d13));
                            bitmapCanvas.drawLine(x + cx, y + cy, x + x13, y + y14, SnowflakesEffect.this.particleThinPaint);
                            y1 = angle + 1.0471976f;
                        }
                    }
                    SnowflakesEffect.this.bitmapPaint.setAlpha((int) (this.alpha * 255.0f));
                    canvas.save();
                    float f = this.scale;
                    canvas.scale(f, f, this.x, this.y);
                    canvas.drawBitmap(SnowflakesEffect.this.particleBitmap, this.x, this.y, SnowflakesEffect.this.bitmapPaint);
                    canvas.restore();
                    return;
            }
        }
    }

    public SnowflakesEffect(int viewType) {
        this.viewType = viewType;
        Paint paint = new Paint(1);
        this.particlePaint = paint;
        paint.setStrokeWidth(AndroidUtilities.dp(1.5f));
        this.particlePaint.setStrokeCap(Paint.Cap.ROUND);
        this.particlePaint.setStyle(Paint.Style.STROKE);
        Paint paint2 = new Paint(1);
        this.particleThinPaint = paint2;
        paint2.setStrokeWidth(AndroidUtilities.dp(0.5f));
        this.particleThinPaint.setStrokeCap(Paint.Cap.ROUND);
        this.particleThinPaint.setStyle(Paint.Style.STROKE);
        updateColors();
        for (int a = 0; a < 20; a++) {
            this.freeParticles.add(new Particle());
        }
    }

    public void setColorKey(String key) {
        this.colorKey = key;
        updateColors();
    }

    public void updateColors() {
        int color = Theme.getColor(this.colorKey) & (-1644826);
        if (this.color != color) {
            this.color = color;
            this.particlePaint.setColor(color);
            this.particleThinPaint.setColor(color);
        }
    }

    private void updateParticles(long dt) {
        int count = this.particles.size();
        int a = 0;
        while (a < count) {
            Particle particle = this.particles.get(a);
            if (particle.currentTime >= particle.lifeTime) {
                if (this.freeParticles.size() < 40) {
                    this.freeParticles.add(particle);
                }
                this.particles.remove(a);
                a--;
                count--;
            } else {
                if (this.viewType == 0) {
                    if (particle.currentTime < 200.0f) {
                        particle.alpha = AndroidUtilities.accelerateInterpolator.getInterpolation(particle.currentTime / 200.0f);
                    } else {
                        particle.alpha = 1.0f - AndroidUtilities.decelerateInterpolator.getInterpolation((particle.currentTime - 200.0f) / (particle.lifeTime - 200.0f));
                    }
                } else if (particle.currentTime < 200.0f) {
                    particle.alpha = AndroidUtilities.accelerateInterpolator.getInterpolation(particle.currentTime / 200.0f);
                } else if (particle.lifeTime - particle.currentTime < 2000.0f) {
                    particle.alpha = AndroidUtilities.decelerateInterpolator.getInterpolation((particle.lifeTime - particle.currentTime) / 2000.0f);
                }
                particle.x += ((particle.vx * particle.velocity) * ((float) dt)) / 500.0f;
                particle.y += ((particle.vy * particle.velocity) * ((float) dt)) / 500.0f;
                particle.currentTime += (float) dt;
            }
            a++;
        }
    }

    public void onDraw(View parent, Canvas canvas) {
        Particle newParticle;
        if (parent == null || canvas == null) {
            return;
        }
        int count = this.particles.size();
        for (int a = 0; a < count; a++) {
            Particle particle = this.particles.get(a);
            particle.draw(canvas);
        }
        int a2 = this.viewType;
        int maxCount = a2 == 0 ? 100 : 300;
        int createPerFrame = a2 == 0 ? 1 : 10;
        if (this.particles.size() < maxCount) {
            for (int i = 0; i < createPerFrame; i++) {
                if (this.particles.size() < maxCount && Utilities.random.nextFloat() > 0.7f) {
                    int statusBarHeight = Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
                    float cx = Utilities.random.nextFloat() * parent.getMeasuredWidth();
                    float cy = statusBarHeight + (Utilities.random.nextFloat() * ((parent.getMeasuredHeight() - AndroidUtilities.dp(20.0f)) - statusBarHeight));
                    int angle = (Utilities.random.nextInt(40) - 20) + 90;
                    double d = angle;
                    Double.isNaN(d);
                    float vx = (float) Math.cos(d * 0.017453292519943295d);
                    double d2 = angle;
                    Double.isNaN(d2);
                    float vy = (float) Math.sin(d2 * 0.017453292519943295d);
                    if (this.freeParticles.isEmpty()) {
                        newParticle = new Particle();
                    } else {
                        newParticle = this.freeParticles.get(0);
                        this.freeParticles.remove(0);
                    }
                    newParticle.x = cx;
                    newParticle.y = cy;
                    newParticle.vx = vx;
                    newParticle.vy = vy;
                    newParticle.alpha = 0.0f;
                    newParticle.currentTime = 0.0f;
                    newParticle.scale = Utilities.random.nextFloat() * 1.2f;
                    newParticle.type = Utilities.random.nextInt(2);
                    if (this.viewType == 0) {
                        newParticle.lifeTime = Utilities.random.nextInt(100) + 2000;
                    } else {
                        newParticle.lifeTime = Utilities.random.nextInt(2000) + 3000;
                    }
                    newParticle.velocity = (Utilities.random.nextFloat() * 4.0f) + 20.0f;
                    this.particles.add(newParticle);
                }
            }
        }
        long newTime = System.currentTimeMillis();
        long dt = Math.min(17L, newTime - this.lastAnimationTime);
        updateParticles(dt);
        this.lastAnimationTime = newTime;
        parent.invalidate();
    }
}
