package org.telegram.ui.Components;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
/* loaded from: classes5.dex */
public class ShutterButton extends View {
    private static final int LONG_PRESS_TIME = 800;
    private ShutterButtonDelegate delegate;
    private long lastUpdateTime;
    private boolean pressed;
    private boolean processRelease;
    private Paint redPaint;
    private float redProgress;
    private long totalTime;
    private Paint whitePaint;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator();
    private Runnable longPressed = new Runnable() { // from class: org.telegram.ui.Components.ShutterButton.1
        @Override // java.lang.Runnable
        public void run() {
            if (ShutterButton.this.delegate != null && !ShutterButton.this.delegate.shutterLongPressed()) {
                ShutterButton.this.processRelease = false;
            }
        }
    };
    private Drawable shadowDrawable = getResources().getDrawable(R.drawable.camera_btn);
    private State state = State.DEFAULT;

    /* loaded from: classes5.dex */
    public interface ShutterButtonDelegate {
        boolean onTranslationChanged(float f, float f2);

        void shutterCancel();

        boolean shutterLongPressed();

        void shutterReleased();
    }

    /* loaded from: classes5.dex */
    public enum State {
        DEFAULT,
        RECORDING
    }

    public ShutterButton(Context context) {
        super(context);
        Paint paint = new Paint(1);
        this.whitePaint = paint;
        paint.setStyle(Paint.Style.FILL);
        this.whitePaint.setColor(-1);
        Paint paint2 = new Paint(1);
        this.redPaint = paint2;
        paint2.setStyle(Paint.Style.FILL);
        this.redPaint.setColor(-3324089);
    }

    public void setDelegate(ShutterButtonDelegate shutterButtonDelegate) {
        this.delegate = shutterButtonDelegate;
    }

    public ShutterButtonDelegate getDelegate() {
        return this.delegate;
    }

    private void setHighlighted(boolean value) {
        AnimatorSet animatorSet = new AnimatorSet();
        if (value) {
            animatorSet.playTogether(ObjectAnimator.ofFloat(this, View.SCALE_X, 1.06f), ObjectAnimator.ofFloat(this, View.SCALE_Y, 1.06f));
        } else {
            animatorSet.playTogether(ObjectAnimator.ofFloat(this, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this, View.SCALE_Y, 1.0f));
            animatorSet.setStartDelay(40L);
        }
        animatorSet.setDuration(120L);
        animatorSet.setInterpolator(this.interpolator);
        animatorSet.start();
    }

    @Override // android.view.View
    public void setScaleX(float scaleX) {
        super.setScaleX(scaleX);
        invalidate();
    }

    public State getState() {
        return this.state;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int cx = getMeasuredWidth() / 2;
        int cy = getMeasuredHeight() / 2;
        this.shadowDrawable.setBounds(cx - AndroidUtilities.dp(36.0f), cy - AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f) + cx, AndroidUtilities.dp(36.0f) + cy);
        this.shadowDrawable.draw(canvas);
        if (this.pressed || getScaleX() != 1.0f) {
            float scale = (getScaleX() - 1.0f) / 0.06f;
            this.whitePaint.setAlpha((int) (255.0f * scale));
            canvas.drawCircle(cx, cy, AndroidUtilities.dp(26.0f), this.whitePaint);
            if (this.state == State.RECORDING) {
                if (this.redProgress != 1.0f) {
                    long dt = Math.abs(System.currentTimeMillis() - this.lastUpdateTime);
                    if (dt > 17) {
                        dt = 17;
                    }
                    long j = this.totalTime + dt;
                    this.totalTime = j;
                    if (j > 120) {
                        this.totalTime = 120L;
                    }
                    this.redProgress = this.interpolator.getInterpolation(((float) this.totalTime) / 120.0f);
                    invalidate();
                }
                canvas.drawCircle(cx, cy, AndroidUtilities.dp(26.5f) * scale * this.redProgress, this.redPaint);
            } else if (this.redProgress != 0.0f) {
                canvas.drawCircle(cx, cy, AndroidUtilities.dp(26.5f) * scale, this.redPaint);
            }
        } else if (this.redProgress != 0.0f) {
            this.redProgress = 0.0f;
        }
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(AndroidUtilities.dp(84.0f), AndroidUtilities.dp(84.0f));
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        switch (motionEvent.getAction()) {
            case 0:
                AndroidUtilities.runOnUIThread(this.longPressed, 800L);
                this.pressed = true;
                this.processRelease = true;
                setHighlighted(true);
                break;
            case 1:
                setHighlighted(false);
                AndroidUtilities.cancelRunOnUIThread(this.longPressed);
                if (this.processRelease) {
                    this.delegate.shutterReleased();
                    break;
                }
                break;
            case 2:
                float dy = 0.0f;
                float dx = (x < 0.0f || x > ((float) getMeasuredWidth())) ? x : 0.0f;
                if (y < 0.0f || y > getMeasuredHeight()) {
                    dy = y;
                }
                if (this.delegate.onTranslationChanged(dx, dy)) {
                    AndroidUtilities.cancelRunOnUIThread(this.longPressed);
                    if (this.state == State.RECORDING) {
                        this.processRelease = false;
                        setHighlighted(false);
                        this.delegate.shutterCancel();
                        setState(State.DEFAULT, true);
                        break;
                    }
                }
                break;
            case 3:
                setHighlighted(false);
                this.pressed = false;
                break;
        }
        return true;
    }

    public void setState(State value, boolean animated) {
        if (this.state != value) {
            this.state = value;
            if (animated) {
                this.lastUpdateTime = System.currentTimeMillis();
                this.totalTime = 0L;
                if (this.state != State.RECORDING) {
                    this.redProgress = 0.0f;
                }
            } else if (value == State.RECORDING) {
                this.redProgress = 1.0f;
            } else {
                this.redProgress = 0.0f;
            }
            invalidate();
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName("android.widget.Button");
        info.setClickable(true);
        info.setLongClickable(true);
        if (Build.VERSION.SDK_INT >= 21) {
            info.addAction(new AccessibilityNodeInfo.AccessibilityAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK.getId(), LocaleController.getString("AccActionTakePicture", R.string.AccActionTakePicture)));
            info.addAction(new AccessibilityNodeInfo.AccessibilityAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_LONG_CLICK.getId(), LocaleController.getString("AccActionRecordVideo", R.string.AccActionRecordVideo)));
        }
    }
}
