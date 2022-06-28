package org.telegram.ui.Components.Premium;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.core.graphics.ColorUtils;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class SpeedLineParticles {

    /* loaded from: classes5.dex */
    public static class Drawable {
        public final int count;
        private int lastColor;
        private float[] lines;
        public boolean paused;
        long pausedTime;
        public boolean useGradient;
        public RectF rect = new RectF();
        public RectF screenRect = new RectF();
        private Paint paint = new Paint();
        ArrayList<Particle> particles = new ArrayList<>();
        public float speedScale = 1.0f;
        public int size1 = 14;
        public int size2 = 12;
        public int size3 = 10;
        public long minLifeTime = AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS;
        private final float dt = 1000.0f / AndroidUtilities.screenRefreshRate;

        public Drawable(int count) {
            this.count = count;
            this.lines = new float[count * 4];
        }

        public void init() {
            if (this.particles.isEmpty()) {
                for (int i = 0; i < this.count; i++) {
                    this.particles.add(new Particle());
                }
            }
            updateColors();
        }

        public void updateColors() {
            int c = ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_premiumStartSmallStarsColor2), 80);
            if (this.lastColor != c) {
                this.lastColor = c;
                this.paint.setColor(c);
            }
        }

        public void resetPositions() {
            long time = System.currentTimeMillis();
            for (int i = 0; i < this.particles.size(); i++) {
                this.particles.get(i).genPosition(time, true);
            }
        }

        public void onDraw(Canvas canvas) {
            long time = System.currentTimeMillis();
            for (int i = 0; i < this.particles.size(); i++) {
                Particle particle = this.particles.get(i);
                if (this.paused) {
                    particle.draw(canvas, i, this.pausedTime);
                } else {
                    particle.draw(canvas, i, time);
                }
                if (time > particle.lifeTime || !this.screenRect.contains(particle.x, particle.y)) {
                    particle.genPosition(time, false);
                }
            }
            canvas.drawLines(this.lines, this.paint);
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes5.dex */
        public class Particle {
            private int alpha;
            float inProgress;
            private long lifeTime;
            private int starIndex;
            private float vecX;
            private float vecY;
            private float x;
            private float y;

            private Particle() {
                Drawable.this = r1;
            }

            public void draw(Canvas canvas, int index, long time) {
                Drawable.this.lines[index * 4] = this.x;
                Drawable.this.lines[(index * 4) + 1] = this.y;
                Drawable.this.lines[(index * 4) + 2] = this.x + (AndroidUtilities.dp(30.0f) * this.vecX);
                Drawable.this.lines[(index * 4) + 3] = this.y + (AndroidUtilities.dp(30.0f) * this.vecY);
                if (!Drawable.this.paused) {
                    float speed = AndroidUtilities.dp(4.0f) * (Drawable.this.dt / 660.0f) * Drawable.this.speedScale;
                    this.x += this.vecX * speed;
                    this.y += this.vecY * speed;
                    float f = this.inProgress;
                    if (f != 1.0f) {
                        float f2 = f + (Drawable.this.dt / 200.0f);
                        this.inProgress = f2;
                        if (f2 > 1.0f) {
                            this.inProgress = 1.0f;
                        }
                    }
                }
            }

            public void genPosition(long time, boolean reset) {
                this.lifeTime = Drawable.this.minLifeTime + time + Utilities.fastRandom.nextInt(1000);
                Drawable drawable = Drawable.this;
                RectF currentRect = reset ? drawable.screenRect : drawable.rect;
                float randX = currentRect.left + Math.abs(Utilities.fastRandom.nextInt() % currentRect.width());
                float randY = currentRect.top + Math.abs(Utilities.fastRandom.nextInt() % currentRect.height());
                this.x = randX;
                this.y = randY;
                double a = Math.atan2(randX - Drawable.this.rect.centerX(), this.y - Drawable.this.rect.centerY());
                this.vecX = (float) Math.sin(a);
                this.vecY = (float) Math.cos(a);
                this.alpha = (int) (((Utilities.fastRandom.nextInt(50) + 50) / 100.0f) * 255.0f);
                this.inProgress = 0.0f;
            }
        }
    }
}
