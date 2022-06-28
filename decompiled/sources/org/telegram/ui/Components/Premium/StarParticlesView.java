package org.telegram.ui.Components.Premium;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.view.View;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.GLIconSettingsView;
/* loaded from: classes5.dex */
public class StarParticlesView extends View {
    public static final int TYPE_APP_ICON_REACT = 1001;
    public static final int TYPE_APP_ICON_STAR_PREMIUM = 1002;
    public Drawable drawable;
    int size;

    public StarParticlesView(Context context) {
        super(context);
        int particlesCount = 50;
        if (SharedConfig.getDevicePerformanceClass() == 2) {
            particlesCount = 200;
        } else if (SharedConfig.getDevicePerformanceClass() == 1) {
            particlesCount = 100;
        }
        Drawable drawable = new Drawable(particlesCount);
        this.drawable = drawable;
        drawable.type = 100;
        this.drawable.roundEffect = true;
        this.drawable.useRotate = true;
        this.drawable.useBlur = true;
        this.drawable.checkBounds = true;
        this.drawable.size1 = 4;
        Drawable drawable2 = this.drawable;
        drawable2.k3 = 0.98f;
        drawable2.k2 = 0.98f;
        drawable2.k1 = 0.98f;
        this.drawable.init();
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int sizeInternal = getMeasuredWidth() << (getMeasuredHeight() + 16);
        this.drawable.rect.set(0.0f, 0.0f, AndroidUtilities.dp(140.0f), AndroidUtilities.dp(140.0f));
        this.drawable.rect.offset((getMeasuredWidth() - this.drawable.rect.width()) / 2.0f, (getMeasuredHeight() - this.drawable.rect.height()) / 2.0f);
        this.drawable.rect2.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
        if (this.size != sizeInternal) {
            this.size = sizeInternal;
            this.drawable.resetPositions();
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.drawable.onDraw(canvas);
        if (!this.drawable.paused) {
            invalidate();
        }
    }

    public void flingParticles(float sum) {
        float maxSpeed = 15.0f;
        if (sum < 60.0f) {
            maxSpeed = 5.0f;
        } else if (sum < 180.0f) {
            maxSpeed = 9.0f;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        ValueAnimator.AnimatorUpdateListener updateListener = new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.Premium.StarParticlesView$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                StarParticlesView.this.m2907x5530cc73(valueAnimator);
            }
        };
        ValueAnimator a1 = ValueAnimator.ofFloat(1.0f, maxSpeed);
        a1.addUpdateListener(updateListener);
        a1.setDuration(600L);
        ValueAnimator a2 = ValueAnimator.ofFloat(maxSpeed, 1.0f);
        a2.addUpdateListener(updateListener);
        a2.setDuration(AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        animatorSet.playTogether(a1, a2);
        animatorSet.start();
    }

    /* renamed from: lambda$flingParticles$0$org-telegram-ui-Components-Premium-StarParticlesView */
    public /* synthetic */ void m2907x5530cc73(ValueAnimator animation) {
        this.drawable.speedScale = ((Float) animation.getAnimatedValue()).floatValue();
    }

    /* loaded from: classes5.dex */
    public static class Drawable {
        public static final int TYPE_SETTINGS = 101;
        float a;
        float a1;
        float a2;
        public final int count;
        private boolean distributionAlgorithm;
        private int lastColor;
        public boolean paused;
        long pausedTime;
        float[] points1;
        float[] points2;
        float[] points3;
        int pointsCount1;
        int pointsCount2;
        int pointsCount3;
        public boolean startFromCenter;
        public boolean svg;
        public boolean useGradient;
        public boolean useRotate;
        public RectF rect = new RectF();
        public RectF rect2 = new RectF();
        public RectF excludeRect = new RectF();
        private final Bitmap[] stars = new Bitmap[3];
        private Paint paint = new Paint();
        ArrayList<Particle> particles = new ArrayList<>();
        public float speedScale = 1.0f;
        public int size1 = 14;
        public int size2 = 12;
        public int size3 = 10;
        public float k1 = 0.85f;
        public float k2 = 0.85f;
        public float k3 = 0.9f;
        public long minLifeTime = AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS;
        public int randLifeTime = 1000;
        private final float dt = 1000.0f / AndroidUtilities.screenRefreshRate;
        Matrix matrix = new Matrix();
        Matrix matrix2 = new Matrix();
        Matrix matrix3 = new Matrix();
        public boolean checkBounds = false;
        public boolean checkTime = true;
        public boolean isCircle = true;
        public boolean useBlur = false;
        public boolean roundEffect = true;
        public int type = -1;
        public String colorKey = Theme.key_premiumStartSmallStarsColor;

        public Drawable(int count) {
            boolean z = false;
            this.count = count;
            this.distributionAlgorithm = count < 50 ? true : z;
        }

        public void init() {
            if (this.useRotate) {
                int i = this.count;
                this.points1 = new float[i * 2];
                this.points2 = new float[i * 2];
                this.points3 = new float[i * 2];
            }
            generateBitmaps();
            if (this.particles.isEmpty()) {
                for (int i2 = 0; i2 < this.count; i2++) {
                    this.particles.add(new Particle());
                }
            }
        }

        public void updateColors() {
            int c = Theme.getColor(this.colorKey);
            if (this.lastColor != c) {
                this.lastColor = c;
                generateBitmaps();
            }
        }

        private void generateBitmaps() {
            int size;
            int res;
            int res2;
            int res3;
            for (int i = 0; i < 3; i++) {
                float k = this.k1;
                if (i == 0) {
                    size = AndroidUtilities.dp(this.size1);
                } else if (i == 1) {
                    k = this.k2;
                    size = AndroidUtilities.dp(this.size2);
                } else {
                    k = this.k3;
                    size = AndroidUtilities.dp(this.size3);
                }
                int i2 = this.type;
                if (i2 == 9) {
                    if (i == 0) {
                        res3 = R.raw.premium_object_folder;
                    } else if (i == 1) {
                        res3 = R.raw.premium_object_bubble;
                    } else {
                        res3 = R.raw.premium_object_settings;
                    }
                    this.stars[i] = SvgHelper.getBitmap(res3, size, size, ColorUtils.setAlphaComponent(Theme.getColor(this.colorKey), 30));
                    this.svg = true;
                } else if (i2 == 3) {
                    if (i == 0) {
                        res2 = R.raw.premium_object_adsbubble;
                    } else if (i == 1) {
                        res2 = R.raw.premium_object_like;
                    } else {
                        res2 = R.raw.premium_object_noads;
                    }
                    this.stars[i] = SvgHelper.getBitmap(res2, size, size, ColorUtils.setAlphaComponent(Theme.getColor(this.colorKey), 30));
                    this.svg = true;
                } else if (i2 == 7) {
                    if (i == 0) {
                        res = R.raw.premium_object_video2;
                    } else if (i == 1) {
                        res = R.raw.premium_object_video;
                    } else {
                        res = R.raw.premium_object_user;
                    }
                    this.stars[i] = SvgHelper.getBitmap(res, size, size, ColorUtils.setAlphaComponent(Theme.getColor(this.colorKey), 30));
                    this.svg = true;
                } else if (i2 == 1001) {
                    this.stars[i] = SvgHelper.getBitmap((int) R.raw.premium_object_fire, size, size, ColorUtils.setAlphaComponent(Theme.getColor(this.colorKey), 30));
                    this.svg = true;
                } else if (i2 == 1002) {
                    this.stars[i] = SvgHelper.getBitmap((int) R.raw.premium_object_star2, size, size, ColorUtils.setAlphaComponent(Theme.getColor(this.colorKey), 30));
                    this.svg = true;
                } else {
                    Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
                    this.stars[i] = bitmap;
                    Canvas canvas = new Canvas(bitmap);
                    if (this.type == 6 && (i == 1 || i == 2)) {
                        android.graphics.drawable.Drawable drawable = ContextCompat.getDrawable(ApplicationLoader.applicationContext, R.drawable.msg_premium_liststar);
                        drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(this.colorKey), PorterDuff.Mode.MULTIPLY));
                        drawable.setBounds(0, 0, size, size);
                        drawable.draw(canvas);
                    } else {
                        Path path = new Path();
                        int sizeHalf = size >> 1;
                        int mid = (int) (sizeHalf * k);
                        path.moveTo(0.0f, sizeHalf);
                        path.lineTo(mid, mid);
                        path.lineTo(sizeHalf, 0.0f);
                        path.lineTo(size - mid, mid);
                        path.lineTo(size, sizeHalf);
                        path.lineTo(size - mid, size - mid);
                        path.lineTo(sizeHalf, size);
                        path.lineTo(mid, size - mid);
                        path.lineTo(0.0f, sizeHalf);
                        path.close();
                        Paint paint = new Paint();
                        if (!this.useGradient) {
                            if (this.type == 100) {
                                paint.setColor(ColorUtils.setAlphaComponent(Theme.getColor(this.colorKey), 200));
                            } else {
                                paint.setColor(Theme.getColor(this.colorKey));
                            }
                            if (this.roundEffect) {
                                paint.setPathEffect(new CornerPathEffect(AndroidUtilities.dpf2(this.size1 / 5.0f)));
                            }
                            canvas.drawPath(path, paint);
                        } else {
                            if (size >= AndroidUtilities.dp(10.0f)) {
                                PremiumGradient.getInstance().updateMainGradientMatrix(0, 0, size, size, size * (-2), 0.0f);
                            } else {
                                PremiumGradient.getInstance().updateMainGradientMatrix(0, 0, size, size, size * (-4), 0.0f);
                            }
                            Paint paint1 = PremiumGradient.getInstance().getMainGradientPaint();
                            if (this.roundEffect) {
                                paint1.setPathEffect(new CornerPathEffect(AndroidUtilities.dpf2(this.size1 / 5.0f)));
                            }
                            if (this.useBlur) {
                                paint1.setAlpha(60);
                            } else {
                                paint1.setAlpha(120);
                            }
                            canvas.drawPath(path, paint1);
                            paint1.setPathEffect(null);
                            paint1.setAlpha(255);
                        }
                        if (this.useBlur) {
                            Utilities.stackBlurBitmap(bitmap, 2);
                        }
                    }
                }
            }
        }

        public void resetPositions() {
            long time = System.currentTimeMillis();
            for (int i = 0; i < this.particles.size(); i++) {
                this.particles.get(i).genPosition(time);
            }
        }

        public void onDraw(Canvas canvas) {
            onDraw(canvas, 1.0f);
        }

        public void onDraw(Canvas canvas, float alpha) {
            long time = System.currentTimeMillis();
            if (this.useRotate) {
                this.matrix.reset();
                float f = this.a + 0.144f;
                this.a = f;
                this.a1 += 0.1152f;
                this.a2 += 0.096f;
                this.matrix.setRotate(f, this.rect.centerX(), this.rect.centerY());
                this.matrix2.setRotate(this.a1, this.rect.centerX(), this.rect.centerY());
                this.matrix3.setRotate(this.a2, this.rect.centerX(), this.rect.centerY());
                this.pointsCount1 = 0;
                this.pointsCount2 = 0;
                this.pointsCount3 = 0;
                for (int i = 0; i < this.particles.size(); i++) {
                    this.particles.get(i).updatePoint();
                }
                Matrix matrix = this.matrix;
                float[] fArr = this.points1;
                matrix.mapPoints(fArr, 0, fArr, 0, this.pointsCount1);
                Matrix matrix2 = this.matrix2;
                float[] fArr2 = this.points2;
                matrix2.mapPoints(fArr2, 0, fArr2, 0, this.pointsCount2);
                Matrix matrix3 = this.matrix3;
                float[] fArr3 = this.points3;
                matrix3.mapPoints(fArr3, 0, fArr3, 0, this.pointsCount3);
                this.pointsCount1 = 0;
                this.pointsCount2 = 0;
                this.pointsCount3 = 0;
            }
            for (int i2 = 0; i2 < this.particles.size(); i2++) {
                Particle particle = this.particles.get(i2);
                if (this.paused) {
                    particle.draw(canvas, this.pausedTime, alpha);
                } else {
                    particle.draw(canvas, time, alpha);
                }
                if (this.checkTime && time > particle.lifeTime) {
                    particle.genPosition(time);
                }
                if (this.checkBounds && !this.rect2.contains(particle.drawingX, particle.drawingY)) {
                    particle.genPosition(time);
                }
            }
        }

        /* loaded from: classes5.dex */
        public class Particle {
            private int alpha;
            private float drawingX;
            private float drawingY;
            float inProgress;
            private long lifeTime;
            private float randomRotate;
            private int starIndex;
            private float vecX;
            private float vecY;
            private float x;
            private float x2;
            private float y;
            private float y2;

            private Particle() {
                Drawable.this = r1;
            }

            static /* synthetic */ long access$114(Particle x0, long x1) {
                long j = x0.lifeTime + x1;
                x0.lifeTime = j;
                return j;
            }

            public void updatePoint() {
                int i = this.starIndex;
                if (i == 0) {
                    Drawable.this.points1[Drawable.this.pointsCount1 * 2] = this.x;
                    Drawable.this.points1[(Drawable.this.pointsCount1 * 2) + 1] = this.y;
                    Drawable.this.pointsCount1++;
                } else if (i == 1) {
                    Drawable.this.points2[Drawable.this.pointsCount2 * 2] = this.x;
                    Drawable.this.points2[(Drawable.this.pointsCount2 * 2) + 1] = this.y;
                    Drawable.this.pointsCount2++;
                } else if (i == 2) {
                    Drawable.this.points3[Drawable.this.pointsCount3 * 2] = this.x;
                    Drawable.this.points3[(Drawable.this.pointsCount3 * 2) + 1] = this.y;
                    Drawable.this.pointsCount3++;
                }
            }

            public void draw(Canvas canvas, long time, float alpha) {
                if (!Drawable.this.useRotate) {
                    this.drawingX = this.x;
                    this.drawingY = this.y;
                } else {
                    int i = this.starIndex;
                    if (i == 0) {
                        this.drawingX = Drawable.this.points1[Drawable.this.pointsCount1 * 2];
                        this.drawingY = Drawable.this.points1[(Drawable.this.pointsCount1 * 2) + 1];
                        Drawable.this.pointsCount1++;
                    } else if (i == 1) {
                        this.drawingX = Drawable.this.points2[Drawable.this.pointsCount2 * 2];
                        this.drawingY = Drawable.this.points2[(Drawable.this.pointsCount2 * 2) + 1];
                        Drawable.this.pointsCount2++;
                    } else if (i == 2) {
                        this.drawingX = Drawable.this.points3[Drawable.this.pointsCount3 * 2];
                        this.drawingY = Drawable.this.points3[(Drawable.this.pointsCount3 * 2) + 1];
                        Drawable.this.pointsCount3++;
                    }
                }
                boolean skipDraw = false;
                if (!Drawable.this.excludeRect.isEmpty() && Drawable.this.excludeRect.contains(this.drawingX, this.drawingY)) {
                    skipDraw = true;
                }
                if (!skipDraw) {
                    canvas.save();
                    canvas.translate(this.drawingX, this.drawingY);
                    float f = this.randomRotate;
                    if (f != 0.0f) {
                        canvas.rotate(f, Drawable.this.stars[this.starIndex].getWidth() / 2.0f, Drawable.this.stars[this.starIndex].getHeight() / 2.0f);
                    }
                    if (this.inProgress < 1.0f || GLIconSettingsView.smallStarsSize != 1.0f) {
                        float s = AndroidUtilities.overshootInterpolator.getInterpolation(this.inProgress) * GLIconSettingsView.smallStarsSize;
                        canvas.scale(s, s, 0.0f, 0.0f);
                    }
                    float outProgress = 0.0f;
                    if (Drawable.this.checkTime) {
                        long j = this.lifeTime;
                        if (j - time < 200) {
                            outProgress = Utilities.clamp(1.0f - (((float) (j - time)) / 150.0f), 1.0f, 0.0f);
                        }
                    }
                    Drawable.this.paint.setAlpha((int) (this.alpha * (1.0f - outProgress) * alpha));
                    canvas.drawBitmap(Drawable.this.stars[this.starIndex], -(Drawable.this.stars[this.starIndex].getWidth() >> 1), -(Drawable.this.stars[this.starIndex].getHeight() >> 1), Drawable.this.paint);
                    canvas.restore();
                }
                if (!Drawable.this.paused) {
                    float speed = AndroidUtilities.dp(4.0f) * (Drawable.this.dt / 660.0f) * Drawable.this.speedScale;
                    this.x += this.vecX * speed;
                    this.y += this.vecY * speed;
                    float f2 = this.inProgress;
                    if (f2 != 1.0f) {
                        float f3 = f2 + (Drawable.this.dt / 200.0f);
                        this.inProgress = f3;
                        if (f3 > 1.0f) {
                            this.inProgress = 1.0f;
                        }
                    }
                }
            }

            public void genPosition(long time) {
                int i;
                float ry;
                float rx;
                this.starIndex = Math.abs(Utilities.fastRandom.nextInt() % Drawable.this.stars.length);
                this.lifeTime = Drawable.this.minLifeTime + time + Utilities.fastRandom.nextInt(Drawable.this.randLifeTime);
                this.randomRotate = 0.0f;
                if (Drawable.this.distributionAlgorithm) {
                    float bestDistance = 0.0f;
                    float bestX = Drawable.this.rect.left + Math.abs(Utilities.fastRandom.nextInt() % Drawable.this.rect.width());
                    float bestY = Drawable.this.rect.top + Math.abs(Utilities.fastRandom.nextInt() % Drawable.this.rect.height());
                    for (int k = 0; k < 10; k++) {
                        float randX = Drawable.this.rect.left + Math.abs(Utilities.fastRandom.nextInt() % Drawable.this.rect.width());
                        float randY = Drawable.this.rect.top + Math.abs(Utilities.fastRandom.nextInt() % Drawable.this.rect.height());
                        float minDistance = 2.14748365E9f;
                        for (int j = 0; j < Drawable.this.particles.size(); j++) {
                            if (Drawable.this.startFromCenter) {
                                rx = Drawable.this.particles.get(j).x2 - randX;
                                ry = Drawable.this.particles.get(j).y2 - randY;
                            } else {
                                rx = Drawable.this.particles.get(j).x - randX;
                                ry = Drawable.this.particles.get(j).y - randY;
                            }
                            float distance = (rx * rx) + (ry * ry);
                            if (distance < minDistance) {
                                minDistance = distance;
                            }
                        }
                        int j2 = (minDistance > bestDistance ? 1 : (minDistance == bestDistance ? 0 : -1));
                        if (j2 > 0) {
                            bestDistance = minDistance;
                            bestX = randX;
                            bestY = randY;
                        }
                    }
                    this.x = bestX;
                    this.y = bestY;
                } else if (Drawable.this.isCircle) {
                    float r = (Math.abs(Utilities.fastRandom.nextInt() % 1000) / 1000.0f) * Drawable.this.rect.width();
                    float a = Math.abs(Utilities.fastRandom.nextInt() % 360);
                    float centerX = Drawable.this.rect.centerX();
                    double d = r;
                    double sin = Math.sin(Math.toRadians(a));
                    Double.isNaN(d);
                    this.x = centerX + ((float) (d * sin));
                    float centerY = Drawable.this.rect.centerY();
                    double d2 = r;
                    double cos = Math.cos(Math.toRadians(a));
                    Double.isNaN(d2);
                    this.y = centerY + ((float) (d2 * cos));
                } else {
                    this.x = Drawable.this.rect.left + Math.abs(Utilities.fastRandom.nextInt() % Drawable.this.rect.width());
                    this.y = Drawable.this.rect.top + Math.abs(Utilities.fastRandom.nextInt() % Drawable.this.rect.height());
                }
                double a2 = Math.atan2(this.x - Drawable.this.rect.centerX(), this.y - Drawable.this.rect.centerY());
                this.vecX = (float) Math.sin(a2);
                this.vecY = (float) Math.cos(a2);
                if (Drawable.this.svg) {
                    this.alpha = (int) (((Utilities.fastRandom.nextInt(50) + 50) / 100.0f) * 120.0f);
                } else {
                    this.alpha = (int) (((Utilities.fastRandom.nextInt(50) + 50) / 100.0f) * 255.0f);
                }
                if ((Drawable.this.type == 6 && ((i = this.starIndex) == 1 || i == 2)) || Drawable.this.type == 9 || Drawable.this.type == 3 || Drawable.this.type == 7) {
                    this.randomRotate = (int) (((Utilities.fastRandom.nextInt() % 100) / 100.0f) * 45.0f);
                }
                if (Drawable.this.type != 101) {
                    this.inProgress = 0.0f;
                }
                if (Drawable.this.startFromCenter) {
                    this.x2 = this.x;
                    this.y2 = this.y;
                    this.x = Drawable.this.rect.centerX();
                    this.y = Drawable.this.rect.centerY();
                }
            }
        }
    }

    public void setPaused(boolean paused) {
        if (paused == this.drawable.paused) {
            return;
        }
        this.drawable.paused = paused;
        if (paused) {
            this.drawable.pausedTime = System.currentTimeMillis();
            return;
        }
        for (int i = 0; i < this.drawable.particles.size(); i++) {
            Drawable.Particle.access$114(this.drawable.particles.get(i), System.currentTimeMillis() - this.drawable.pausedTime);
        }
        invalidate();
    }
}
