package org.telegram.ui.Components;

import android.content.Context;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
/* loaded from: classes5.dex */
public class Scroller {
    private static final int DEFAULT_DURATION = 250;
    private static final int FLING_MODE = 1;
    private static final int NB_SAMPLES = 100;
    private static final int SCROLL_MODE = 0;
    private static float sViscousFluidNormalize;
    private static float sViscousFluidScale;
    private int mCurrX;
    private int mCurrY;
    private float mDeceleration;
    private float mDeltaX;
    private float mDeltaY;
    private int mDuration;
    private float mDurationReciprocal;
    private int mFinalX;
    private int mFinalY;
    private boolean mFinished;
    private boolean mFlywheel;
    private Interpolator mInterpolator;
    private int mMaxX;
    private int mMaxY;
    private int mMinX;
    private int mMinY;
    private int mMode;
    private final float mPpi;
    private long mStartTime;
    private int mStartX;
    private int mStartY;
    private float mVelocity;
    private static float DECELERATION_RATE = (float) (Math.log(0.75d) / Math.log(0.9d));
    private static float START_TENSION = 0.4f;
    private static float END_TENSION = 1.0f - 0.4f;
    private static final float[] SPLINE = new float[101];

    static {
        float x;
        float coef;
        float x_min = 0.0f;
        for (int i = 0; i <= 100; i++) {
            float t = i / 100.0f;
            float x_max = 1.0f;
            while (true) {
                x = ((x_max - x_min) / 2.0f) + x_min;
                coef = 3.0f * x * (1.0f - x);
                float tx = ((((1.0f - x) * START_TENSION) + (END_TENSION * x)) * coef) + (x * x * x);
                if (Math.abs(tx - t) < 1.0E-5d) {
                    break;
                } else if (tx > t) {
                    x_max = x;
                } else {
                    x_min = x;
                }
            }
            float d = (x * x * x) + coef;
            SPLINE[i] = d;
        }
        SPLINE[100] = 1.0f;
        sViscousFluidScale = 8.0f;
        sViscousFluidNormalize = 1.0f;
        sViscousFluidNormalize = 1.0f / viscousFluid(1.0f);
    }

    public Scroller(Context context) {
        this(context, null);
    }

    public Scroller(Context context, Interpolator interpolator) {
        this(context, interpolator, true);
    }

    public Scroller(Context context, Interpolator interpolator, boolean flywheel) {
        this.mFinished = true;
        this.mInterpolator = interpolator;
        this.mPpi = context.getResources().getDisplayMetrics().density * 160.0f;
        this.mDeceleration = computeDeceleration(ViewConfiguration.getScrollFriction());
        this.mFlywheel = flywheel;
    }

    public final void setFriction(float friction) {
        this.mDeceleration = computeDeceleration(friction);
    }

    private float computeDeceleration(float friction) {
        return this.mPpi * 386.0878f * friction;
    }

    public final boolean isFinished() {
        return this.mFinished;
    }

    public final void forceFinished(boolean finished) {
        this.mFinished = finished;
    }

    public final int getDuration() {
        return this.mDuration;
    }

    public final int getCurrX() {
        return this.mCurrX;
    }

    public final int getCurrY() {
        return this.mCurrY;
    }

    public float getCurrVelocity() {
        return this.mVelocity - ((this.mDeceleration * timePassed()) / 2000.0f);
    }

    public final int getStartX() {
        return this.mStartX;
    }

    public final int getStartY() {
        return this.mStartY;
    }

    public final int getFinalX() {
        return this.mFinalX;
    }

    public final int getFinalY() {
        return this.mFinalY;
    }

    public boolean computeScrollOffset() {
        float x;
        int i;
        int i2;
        if (this.mFinished) {
            return false;
        }
        int timePassed = (int) (AnimationUtils.currentAnimationTimeMillis() - this.mStartTime);
        int i3 = this.mDuration;
        if (timePassed < i3) {
            switch (this.mMode) {
                case 0:
                    float d_sup = timePassed;
                    float x2 = d_sup * this.mDurationReciprocal;
                    Interpolator interpolator = this.mInterpolator;
                    if (interpolator == null) {
                        x = viscousFluid(x2);
                    } else {
                        x = interpolator.getInterpolation(x2);
                    }
                    this.mCurrX = this.mStartX + Math.round(this.mDeltaX * x);
                    this.mCurrY = this.mStartY + Math.round(this.mDeltaY * x);
                    break;
                case 1:
                    float t = timePassed / i3;
                    int index = (int) (t * 100.0f);
                    float t_inf = index / 100.0f;
                    float t_sup = (index + 1) / 100.0f;
                    float[] fArr = SPLINE;
                    float d_inf = fArr[index];
                    float d_sup2 = fArr[index + 1];
                    float distanceCoef = (((t - t_inf) / (t_sup - t_inf)) * (d_sup2 - d_inf)) + d_inf;
                    int round = this.mStartX + Math.round((this.mFinalX - i) * distanceCoef);
                    this.mCurrX = round;
                    int min = Math.min(round, this.mMaxX);
                    this.mCurrX = min;
                    this.mCurrX = Math.max(min, this.mMinX);
                    int round2 = this.mStartY + Math.round((this.mFinalY - i2) * distanceCoef);
                    this.mCurrY = round2;
                    int min2 = Math.min(round2, this.mMaxY);
                    this.mCurrY = min2;
                    int max = Math.max(min2, this.mMinY);
                    this.mCurrY = max;
                    if (this.mCurrX == this.mFinalX && max == this.mFinalY) {
                        this.mFinished = true;
                        break;
                    }
                    break;
            }
        } else {
            this.mCurrX = this.mFinalX;
            this.mCurrY = this.mFinalY;
            this.mFinished = true;
        }
        return true;
    }

    public void startScroll(int startX, int startY, int dx, int dy) {
        startScroll(startX, startY, dx, dy, 250);
    }

    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        this.mMode = 0;
        this.mFinished = false;
        this.mDuration = duration;
        this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
        this.mStartX = startX;
        this.mStartY = startY;
        this.mFinalX = startX + dx;
        this.mFinalY = startY + dy;
        this.mDeltaX = dx;
        this.mDeltaY = dy;
        this.mDurationReciprocal = 1.0f / this.mDuration;
    }

    public void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX, int minY, int maxY) {
        int velocityX2 = velocityX;
        int velocityY2 = velocityY;
        if (this.mFlywheel && !this.mFinished) {
            float oldVel = getCurrVelocity();
            float dx = this.mFinalX - this.mStartX;
            float dy = this.mFinalY - this.mStartY;
            float hyp = (float) Math.sqrt((dx * dx) + (dy * dy));
            float ndx = dx / hyp;
            float ndy = dy / hyp;
            float oldVelocityX = ndx * oldVel;
            float oldVelocityY = ndy * oldVel;
            if (Math.signum(velocityX2) == Math.signum(oldVelocityX) && Math.signum(velocityY2) == Math.signum(oldVelocityY)) {
                velocityX2 = (int) (velocityX2 + oldVelocityX);
                velocityY2 = (int) (velocityY2 + oldVelocityY);
            }
        }
        this.mMode = 1;
        this.mFinished = false;
        float velocity = (float) Math.sqrt((velocityX2 * velocityX2) + (velocityY2 * velocityY2));
        this.mVelocity = velocity;
        double l = Math.log((START_TENSION * velocity) / 800.0f);
        double d = DECELERATION_RATE;
        Double.isNaN(d);
        this.mDuration = (int) (Math.exp(l / (d - 1.0d)) * 1000.0d);
        this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
        this.mStartX = startX;
        this.mStartY = startY;
        float coeffY = 1.0f;
        float coeffX = velocity == 0.0f ? 1.0f : velocityX2 / velocity;
        if (velocity != 0.0f) {
            coeffY = velocityY2 / velocity;
        }
        double d2 = 800.0f;
        float f = DECELERATION_RATE;
        double d3 = f;
        double d4 = f;
        Double.isNaN(d4);
        Double.isNaN(d3);
        double exp = Math.exp((d3 / (d4 - 1.0d)) * l);
        Double.isNaN(d2);
        int totalDistance = (int) (d2 * exp);
        this.mMinX = minX;
        this.mMaxX = maxX;
        this.mMinY = minY;
        this.mMaxY = maxY;
        int round = Math.round(totalDistance * coeffX) + startX;
        this.mFinalX = round;
        int min = Math.min(round, this.mMaxX);
        this.mFinalX = min;
        this.mFinalX = Math.max(min, this.mMinX);
        int round2 = Math.round(totalDistance * coeffY) + startY;
        this.mFinalY = round2;
        int min2 = Math.min(round2, this.mMaxY);
        this.mFinalY = min2;
        this.mFinalY = Math.max(min2, this.mMinY);
    }

    static float viscousFluid(float x) {
        float x2;
        float x3 = x * sViscousFluidScale;
        if (x3 < 1.0f) {
            x2 = x3 - (1.0f - ((float) Math.exp(-x3)));
        } else {
            x2 = 0.36787945f + ((1.0f - 0.36787945f) * (1.0f - ((float) Math.exp(1.0f - x3))));
        }
        return x2 * sViscousFluidNormalize;
    }

    public void abortAnimation() {
        this.mCurrX = this.mFinalX;
        this.mCurrY = this.mFinalY;
        this.mFinished = true;
    }

    public void extendDuration(int extend) {
        int passed = timePassed();
        int i = passed + extend;
        this.mDuration = i;
        this.mDurationReciprocal = 1.0f / i;
        this.mFinished = false;
    }

    public int timePassed() {
        return (int) (AnimationUtils.currentAnimationTimeMillis() - this.mStartTime);
    }

    public void setFinalX(int newX) {
        this.mFinalX = newX;
        this.mDeltaX = newX - this.mStartX;
        this.mFinished = false;
    }

    public void setFinalY(int newY) {
        this.mFinalY = newY;
        this.mDeltaY = newY - this.mStartY;
        this.mFinished = false;
    }

    public boolean isScrollingInDirection(float xvel, float yvel) {
        return !this.mFinished && Math.signum(xvel) == Math.signum((float) (this.mFinalX - this.mStartX)) && Math.signum(yvel) == Math.signum((float) (this.mFinalY - this.mStartY));
    }
}
