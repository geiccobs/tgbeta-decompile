package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import org.telegram.messenger.AndroidUtilities;
/* loaded from: classes5.dex */
public class WaveDrawable {
    private static final float ANIMATION_SPEED_CIRCLE = 0.45f;
    private static final float ANIMATION_SPEED_WAVE_HUGE = 0.65f;
    private static final float ANIMATION_SPEED_WAVE_SMALL = 0.45f;
    public static final float CIRCLE_ALPHA_1 = 0.3f;
    public static final float CIRCLE_ALPHA_2 = 0.15f;
    public static final float FLING_DISTANCE = 0.5f;
    private static final float IDLE_RADIUS = 0.56f;
    private static final float IDLE_ROTATE_DIF = 0.020000001f;
    private static final float IDLE_ROTATION_SPEED = 0.2f;
    private static final float IDLE_SCALE_SPEED = 0.3f;
    private static final float IDLE_WAVE_ANGLE = 0.5f;
    public static final float MAX_AMPLITUDE = 1800.0f;
    private static final float RANDOM_RADIUS_SIZE = 0.3f;
    private static final float ROTATION_SPEED = 0.036000002f;
    public static final float SINE_WAVE_SPEED = 0.81f;
    public static final float SMALL_WAVE_RADIUS = 0.55f;
    public static final float SMALL_WAVE_SCALE = 0.4f;
    public static final float SMALL_WAVE_SCALE_SPEED = 0.6f;
    private static final float WAVE_ANGLE = 0.03f;
    private static final float animationSpeed = 0.35000002f;
    public static final float animationSpeedCircle = 0.55f;
    private static final float animationSpeedTiny = 0.55f;
    private float amplitude;
    public float amplitudeRadius;
    public float amplitudeWaveDif;
    private float animateAmplitudeDiff;
    private float animateAmplitudeSlowDiff;
    private float animateToAmplitude;
    private ValueAnimator animator;
    private final CircleBezierDrawable circleBezierDrawable;
    private float circleRadius;
    private boolean expandIdleRadius;
    private boolean expandScale;
    public float fling;
    private Animator flingAnimator;
    private float flingRadius;
    float idleRotation;
    private boolean incRandomAdditionals;
    private boolean isBig;
    float lastRadius;
    private long lastUpdateTime;
    public float maxScale;
    private View parentView;
    float radiusDiff;
    public float rotation;
    private float scaleDif;
    private float scaleIdleDif;
    private float sineAngleMax;
    private float slowAmplitude;
    private WaveDrawable tinyWaveDrawable;
    boolean wasFling;
    double waveAngle;
    float waveDif;
    private Paint paintRecordWaveBig = new Paint();
    private Paint paintRecordWaveTin = new Paint();
    private Interpolator linearInterpolator = new LinearInterpolator();
    private float idleRadius = 0.0f;
    private float idleRadiusK = 0.075f;
    private boolean isIdle = true;
    public float scaleSpeed = 8.0E-5f;
    public float scaleSpeedIdle = 6.0000002E-5f;
    float randomAdditions = AndroidUtilities.dp(8.0f) * 0.3f;
    private final ValueAnimator.AnimatorUpdateListener flingUpdateListener = new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.WaveDrawable$$ExternalSyntheticLambda0
        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
            WaveDrawable.this.m3209lambda$new$0$orgtelegramuiComponentsWaveDrawable(valueAnimator);
        }
    };
    private float idleGlobalRadius = AndroidUtilities.dp(10.0f) * IDLE_RADIUS;

    /* renamed from: lambda$new$0$org-telegram-ui-Components-WaveDrawable */
    public /* synthetic */ void m3209lambda$new$0$orgtelegramuiComponentsWaveDrawable(ValueAnimator animation) {
        this.flingRadius = ((Float) animation.getAnimatedValue()).floatValue();
    }

    public WaveDrawable(View parent, int n, float rotateDif, float radius, WaveDrawable tinyDrawable) {
        this.parentView = parent;
        this.circleBezierDrawable = new CircleBezierDrawable(n);
        this.amplitudeRadius = radius;
        boolean z = tinyDrawable != null;
        this.isBig = z;
        this.tinyWaveDrawable = tinyDrawable;
        this.expandIdleRadius = z;
        this.radiusDiff = AndroidUtilities.dp(34.0f) * 0.0012f;
        if (Build.VERSION.SDK_INT >= 26) {
            this.paintRecordWaveBig.setAntiAlias(true);
            this.paintRecordWaveTin.setAntiAlias(true);
        }
    }

    public void setValue(float value) {
        ValueAnimator valueAnimator;
        this.animateToAmplitude = value;
        boolean z = this.isBig;
        if (z) {
            float f = this.amplitude;
            if (value > f) {
                this.animateAmplitudeDiff = (value - f) / 205.0f;
            } else {
                this.animateAmplitudeDiff = (value - f) / 275.0f;
            }
            this.animateAmplitudeSlowDiff = (value - this.slowAmplitude) / 275.0f;
        } else {
            float f2 = this.amplitude;
            if (value > f2) {
                this.animateAmplitudeDiff = (value - f2) / 320.0f;
            } else {
                this.animateAmplitudeDiff = (value - f2) / 375.0f;
            }
            this.animateAmplitudeSlowDiff = (value - this.slowAmplitude) / 375.0f;
        }
        boolean idle = value < 0.1f;
        if (this.isIdle != idle && idle && z) {
            final float bRotation = this.rotation;
            final float animateToBRotation = (Math.round(this.rotation / 60) * 60) + (60 / 2);
            final float tRotation = this.tinyWaveDrawable.rotation;
            final float animateToTRotation = Math.round(tRotation / 60) * 60;
            final float bWaveDif = this.waveDif;
            final float tWaveDif = this.tinyWaveDrawable.waveDif;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(1.0f, 0.0f);
            this.animator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.WaveDrawable$$ExternalSyntheticLambda1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    WaveDrawable.this.m3210lambda$setValue$1$orgtelegramuiComponentsWaveDrawable(animateToBRotation, bRotation, animateToTRotation, tRotation, bWaveDif, tWaveDif, valueAnimator2);
                }
            });
            this.animator.setDuration(1200L);
            this.animator.start();
        }
        this.isIdle = idle;
        if (!idle && (valueAnimator = this.animator) != null) {
            valueAnimator.cancel();
            this.animator = null;
        }
    }

    /* renamed from: lambda$setValue$1$org-telegram-ui-Components-WaveDrawable */
    public /* synthetic */ void m3210lambda$setValue$1$orgtelegramuiComponentsWaveDrawable(float animateToBRotation, float bRotation, float animateToTRotation, float tRotation, float bWaveDif, float tWaveDif, ValueAnimator animation) {
        float v = ((Float) animation.getAnimatedValue()).floatValue();
        this.rotation = ((bRotation - animateToBRotation) * v) + animateToBRotation;
        WaveDrawable waveDrawable = this.tinyWaveDrawable;
        waveDrawable.rotation = ((tRotation - animateToTRotation) * v) + animateToTRotation;
        this.waveDif = ((bWaveDif - 1.0f) * v) + 1.0f;
        waveDrawable.waveDif = ((tWaveDif - 1.0f) * v) + 1.0f;
        this.waveAngle = (float) Math.acos(this.waveDif);
        WaveDrawable waveDrawable2 = this.tinyWaveDrawable;
        waveDrawable2.waveAngle = (float) Math.acos(-waveDrawable2.waveDif);
    }

    private void startFling(float delta) {
        Animator animator = this.flingAnimator;
        if (animator != null) {
            animator.cancel();
        }
        float fling = this.fling * 2.0f;
        float flingDistance = this.amplitudeRadius * delta * (this.isBig ? 8 : 20) * 16.0f * fling;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(this.flingRadius, flingDistance);
        valueAnimator.addUpdateListener(this.flingUpdateListener);
        valueAnimator.setDuration((this.isBig ? 200 : 350) * fling);
        valueAnimator.setInterpolator(this.linearInterpolator);
        ValueAnimator valueAnimator1 = ValueAnimator.ofFloat(flingDistance, 0.0f);
        valueAnimator1.addUpdateListener(this.flingUpdateListener);
        valueAnimator1.setInterpolator(this.linearInterpolator);
        valueAnimator1.setDuration((this.isBig ? 220 : 380) * fling);
        AnimatorSet animatorSet = new AnimatorSet();
        this.flingAnimator = animatorSet;
        animatorSet.playSequentially(valueAnimator, valueAnimator1);
        animatorSet.start();
    }

    public void tick(float circleRadius) {
        long newTime = SystemClock.elapsedRealtime();
        long dt = newTime - this.lastUpdateTime;
        this.lastUpdateTime = newTime;
        if (dt > 20) {
            dt = 17;
        }
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
            if (Math.abs(this.amplitude - f) * this.amplitudeRadius < AndroidUtilities.dp(4.0f)) {
                if (!this.wasFling) {
                    startFling(this.animateAmplitudeDiff);
                    this.wasFling = true;
                }
            } else {
                this.wasFling = false;
            }
        }
        float f5 = this.animateToAmplitude;
        float f6 = this.slowAmplitude;
        if (f5 != f6) {
            float f7 = f6 + (this.animateAmplitudeSlowDiff * ((float) dt));
            this.slowAmplitude = f7;
            float abs = Math.abs(f7 - this.amplitude);
            float f8 = 0.2f;
            if (abs > 0.2f) {
                float f9 = this.amplitude;
                if (this.slowAmplitude <= f9) {
                    f8 = -0.2f;
                }
                this.slowAmplitude = f9 + f8;
            }
            if (this.animateAmplitudeSlowDiff > 0.0f) {
                float f10 = this.slowAmplitude;
                float f11 = this.animateToAmplitude;
                if (f10 > f11) {
                    this.slowAmplitude = f11;
                }
            } else {
                float f12 = this.slowAmplitude;
                float f13 = this.animateToAmplitude;
                if (f12 < f13) {
                    this.slowAmplitude = f13;
                }
            }
        }
        this.idleRadius = this.idleRadiusK * circleRadius;
        if (this.expandIdleRadius) {
            float f14 = this.scaleIdleDif + (this.scaleSpeedIdle * ((float) dt));
            this.scaleIdleDif = f14;
            if (f14 >= 0.05f) {
                this.scaleIdleDif = 0.05f;
                this.expandIdleRadius = false;
            }
        } else {
            float f15 = this.scaleIdleDif - (this.scaleSpeedIdle * ((float) dt));
            this.scaleIdleDif = f15;
            if (f15 < 0.0f) {
                this.scaleIdleDif = 0.0f;
                this.expandIdleRadius = true;
            }
        }
        float f16 = this.maxScale;
        if (f16 > 0.0f) {
            if (this.expandScale) {
                float f17 = this.scaleDif + (this.scaleSpeed * ((float) dt));
                this.scaleDif = f17;
                if (f17 >= f16) {
                    this.scaleDif = f16;
                    this.expandScale = false;
                }
            } else {
                float f18 = this.scaleDif - (this.scaleSpeed * ((float) dt));
                this.scaleDif = f18;
                if (f18 < 0.0f) {
                    this.scaleDif = 0.0f;
                    this.expandScale = true;
                }
            }
        }
        float f19 = this.sineAngleMax;
        float f20 = this.animateToAmplitude;
        if (f19 > f20) {
            float f21 = f19 - 0.25f;
            this.sineAngleMax = f21;
            if (f21 < f20) {
                this.sineAngleMax = f20;
            }
        } else if (f19 < f20) {
            float f22 = f19 + 0.25f;
            this.sineAngleMax = f22;
            if (f22 > f20) {
                this.sineAngleMax = f20;
            }
        }
        boolean z = this.isIdle;
        if (!z) {
            float f23 = this.rotation;
            float f24 = this.amplitude;
            float f25 = f23 + ((((f24 > 0.5f ? 1.0f : f24 / 0.5f) * 0.14400001f) + 0.018000001f) * ((float) dt));
            this.rotation = f25;
            if (f25 > 360.0f) {
                this.rotation = f25 % 360.0f;
            }
        } else {
            float f26 = this.idleRotation + (((float) dt) * IDLE_ROTATE_DIF);
            this.idleRotation = f26;
            if (f26 > 360.0f) {
                this.idleRotation = f26 % 360.0f;
            }
        }
        float f27 = this.lastRadius;
        if (f27 < circleRadius) {
            this.lastRadius = circleRadius;
        } else {
            float f28 = f27 - (this.radiusDiff * ((float) dt));
            this.lastRadius = f28;
            if (f28 < circleRadius) {
                this.lastRadius = circleRadius;
            }
        }
        this.lastRadius = circleRadius;
        if (!z) {
            double d = this.waveAngle;
            double d2 = this.amplitudeWaveDif * this.sineAngleMax * ((float) dt);
            Double.isNaN(d2);
            double d3 = d + d2;
            this.waveAngle = d3;
            if (this.isBig) {
                this.waveDif = (float) Math.cos(d3);
            } else {
                this.waveDif = -((float) Math.cos(d3));
            }
            float f29 = this.waveDif;
            if (f29 > 0.0f && this.incRandomAdditionals) {
                this.circleBezierDrawable.calculateRandomAdditionals();
                this.incRandomAdditionals = false;
            } else if (f29 < 0.0f && !this.incRandomAdditionals) {
                this.circleBezierDrawable.calculateRandomAdditionals();
                this.incRandomAdditionals = true;
            }
        }
        this.parentView.invalidate();
    }

    public void draw(float cx, float cy, float scale, Canvas canvas) {
        float f = this.amplitude;
        float waveAmplitude = f < 0.3f ? f / 0.3f : 1.0f;
        float radiusDiff = AndroidUtilities.dp(10.0f) + (AndroidUtilities.dp(50.0f) * WAVE_ANGLE * this.animateToAmplitude);
        this.circleBezierDrawable.idleStateDiff = this.idleRadius * (1.0f - waveAmplitude);
        float kDiff = 0.35f * waveAmplitude * this.waveDif;
        this.circleBezierDrawable.radiusDiff = radiusDiff * kDiff;
        this.circleBezierDrawable.cubicBezierK = (Math.abs(kDiff) * waveAmplitude) + 1.0f + ((1.0f - waveAmplitude) * this.idleRadiusK);
        this.circleBezierDrawable.radius = this.lastRadius + (this.amplitudeRadius * this.amplitude) + this.idleGlobalRadius + (this.flingRadius * waveAmplitude);
        float f2 = this.circleBezierDrawable.radius + this.circleBezierDrawable.radiusDiff;
        float f3 = this.circleRadius;
        if (f2 < f3) {
            CircleBezierDrawable circleBezierDrawable = this.circleBezierDrawable;
            circleBezierDrawable.radiusDiff = f3 - circleBezierDrawable.radius;
        }
        if (this.isBig) {
            this.circleBezierDrawable.globalRotate = this.rotation + this.idleRotation;
        } else {
            this.circleBezierDrawable.globalRotate = (-this.rotation) + this.idleRotation;
        }
        canvas.save();
        float s = (this.scaleIdleDif * (1.0f - waveAmplitude)) + scale + (this.scaleDif * waveAmplitude);
        canvas.scale(s, s, cx, cy);
        this.circleBezierDrawable.setRandomAdditions(this.waveDif * waveAmplitude * this.randomAdditions);
        this.circleBezierDrawable.draw(cx, cy, canvas, this.isBig ? this.paintRecordWaveBig : this.paintRecordWaveTin);
        canvas.restore();
    }

    public void setCircleRadius(float radius) {
        this.circleRadius = radius;
    }

    public void setColor(int color, int alpha) {
        this.paintRecordWaveBig.setColor(color);
        this.paintRecordWaveTin.setColor(color);
        this.paintRecordWaveBig.setAlpha(alpha);
        this.paintRecordWaveTin.setAlpha(alpha);
    }
}
