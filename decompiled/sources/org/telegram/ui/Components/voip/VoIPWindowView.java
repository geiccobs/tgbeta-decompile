package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.Build;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.WindowManager;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.VoIPFragment;
/* loaded from: classes5.dex */
public class VoIPWindowView extends FrameLayout {
    Activity activity;
    private int animationIndex = -1;
    boolean finished;
    protected boolean lockOnScreen;
    private int orientationBefore;
    boolean runEnterTransition;
    boolean startDragging;
    float startX;
    float startY;
    VelocityTracker velocityTracker;

    public VoIPWindowView(Activity activity, boolean enterAnimation) {
        super(activity);
        this.activity = activity;
        setSystemUiVisibility(1792);
        setFitsSystemWindows(true);
        this.orientationBefore = activity.getRequestedOrientation();
        activity.setRequestedOrientation(1);
        if (!enterAnimation) {
            this.runEnterTransition = true;
        }
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!this.runEnterTransition) {
            this.runEnterTransition = true;
            startEnterTransition();
        }
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return onTouchEvent(ev);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (this.lockOnScreen) {
            return false;
        }
        if (event.getAction() == 0) {
            this.startX = event.getX();
            this.startY = event.getY();
            if (this.velocityTracker == null) {
                this.velocityTracker = VelocityTracker.obtain();
            }
            this.velocityTracker.clear();
        } else {
            boolean z = true;
            if (event.getAction() == 2) {
                float dx = event.getX() - this.startX;
                float dy = event.getY() - this.startY;
                if (!this.startDragging && Math.abs(dx) > AndroidUtilities.getPixelsInCM(0.4f, true) && Math.abs(dx) / 3.0f > dy) {
                    this.startX = event.getX();
                    dx = 0.0f;
                    this.startDragging = true;
                }
                if (this.startDragging) {
                    if (dx < 0.0f) {
                        dx = 0.0f;
                    }
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    this.velocityTracker.addMovement(event);
                    setTranslationX(dx);
                }
                return this.startDragging;
            } else if (event.getAction() == 1 || event.getAction() == 3) {
                float x = getTranslationX();
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                }
                this.velocityTracker.computeCurrentVelocity(1000);
                float velX = this.velocityTracker.getXVelocity();
                float velY = this.velocityTracker.getYVelocity();
                if (x >= getMeasuredWidth() / 3.0f || (velX >= 3500.0f && velX >= velY)) {
                    z = false;
                }
                boolean backAnimation = z;
                if (!backAnimation) {
                    float distToMove = getMeasuredWidth() - getTranslationX();
                    finish(Math.max((int) ((200.0f / getMeasuredWidth()) * distToMove), 50));
                } else {
                    animate().translationX(0.0f).start();
                }
                this.startDragging = false;
            }
        }
        return false;
    }

    public void finish() {
        finish(150L);
    }

    public void finish(long animDuration) {
        if (!this.finished) {
            this.finished = true;
            VoIPFragment.clearInstance();
            if (this.lockOnScreen) {
                try {
                    WindowManager wm = (WindowManager) this.activity.getSystemService("window");
                    wm.removeView(this);
                    return;
                } catch (Exception e) {
                    return;
                }
            }
            final int account = UserConfig.selectedAccount;
            this.animationIndex = NotificationCenter.getInstance(account).setAnimationInProgress(this.animationIndex, null);
            animate().translationX(getMeasuredWidth()).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.voip.VoIPWindowView.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    NotificationCenter.getInstance(account).onAnimationFinish(VoIPWindowView.this.animationIndex);
                    if (VoIPWindowView.this.getParent() != null) {
                        VoIPWindowView.this.activity.setRequestedOrientation(VoIPWindowView.this.orientationBefore);
                        WindowManager wm2 = (WindowManager) VoIPWindowView.this.activity.getSystemService("window");
                        VoIPWindowView.this.setVisibility(8);
                        try {
                            wm2.removeView(VoIPWindowView.this);
                        } catch (Exception e2) {
                        }
                    }
                }
            }).setDuration(animDuration).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        }
    }

    public void startEnterTransition() {
        if (!this.lockOnScreen) {
            setTranslationX(getMeasuredWidth());
            animate().translationX(0.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        }
    }

    public void setLockOnScreen(boolean lock) {
        this.lockOnScreen = lock;
    }

    public WindowManager.LayoutParams createWindowLayoutParams() {
        WindowManager.LayoutParams windowLayoutParams = new WindowManager.LayoutParams();
        windowLayoutParams.height = -1;
        windowLayoutParams.format = -2;
        windowLayoutParams.width = -1;
        windowLayoutParams.gravity = 51;
        windowLayoutParams.type = 99;
        windowLayoutParams.screenOrientation = 1;
        if (Build.VERSION.SDK_INT >= 28) {
            windowLayoutParams.layoutInDisplayCutoutMode = 1;
        }
        if (Build.VERSION.SDK_INT >= 21) {
            windowLayoutParams.flags = -2147286784;
        } else {
            windowLayoutParams.flags = 131072;
        }
        windowLayoutParams.flags |= 2621568;
        return windowLayoutParams;
    }

    public boolean isLockOnScreen() {
        return this.lockOnScreen;
    }

    public void requestFullscreen(boolean request) {
        if (request) {
            setSystemUiVisibility(getSystemUiVisibility() | 4);
            return;
        }
        int flags = getSystemUiVisibility();
        setSystemUiVisibility(flags & (-5));
    }

    public void finishImmediate() {
        if (getParent() != null) {
            this.activity.setRequestedOrientation(this.orientationBefore);
            WindowManager wm = (WindowManager) this.activity.getSystemService("window");
            setVisibility(8);
            wm.removeView(this);
        }
    }
}
