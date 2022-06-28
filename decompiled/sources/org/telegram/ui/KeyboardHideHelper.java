package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Insets;
import android.os.Build;
import android.os.CancellationSignal;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.WindowInsetsAnimationControlListener;
import android.view.WindowInsetsAnimationController;
import android.view.animation.LinearInterpolator;
import androidx.core.math.MathUtils;
import androidx.core.view.WindowInsetsCompat;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.KeyboardHideHelper;
/* loaded from: classes4.dex */
public class KeyboardHideHelper {
    public static boolean ENABLED = false;
    private int bottomNavBarSize;
    private View enterView;
    private float fromY;
    private WindowInsetsAnimationController insetsController;
    private int keyboardSize;
    private float lastDifferentT;
    private float lastT;
    private AdjustPanLayoutHelper panLayoutHelper;
    private float rawT;
    private float t;
    private VelocityTracker tracker;
    private View view;
    private boolean isKeyboard = false;
    private boolean movingKeyboard = false;
    private boolean exactlyMovingKeyboard = false;
    private boolean endingMovingKeyboard = false;
    private boolean startedOutsideView = false;
    private boolean startedAtBottom = false;

    public boolean onTouch(AdjustPanLayoutHelper panLayoutHelper, View view, RecyclerListView listView, ChatActivityEnterView enterView, ChatActivity ca, MotionEvent ev) {
        boolean z;
        int i;
        if (!ENABLED) {
            return false;
        }
        this.panLayoutHelper = panLayoutHelper;
        this.view = view;
        this.enterView = enterView;
        if (view != null && enterView != null && Build.VERSION.SDK_INT >= 30) {
            boolean isKeyboardVisible = view.getRootWindowInsets().getInsets(WindowInsetsCompat.Type.ime()).bottom > 0;
            if (!this.movingKeyboard && !isKeyboardVisible && !this.endingMovingKeyboard) {
                return false;
            }
            boolean insideEnterView = ev.getY() >= ((float) enterView.getTop());
            if (ev.getAction() == 0) {
                this.startedOutsideView = !insideEnterView;
                this.startedAtBottom = !listView.canScrollVertically(1);
            }
            float f = 0.0f;
            if (!this.movingKeyboard && insideEnterView && this.startedOutsideView && ev.getAction() == 2) {
                this.movingKeyboard = true;
                boolean z2 = !enterView.isPopupShowing();
                this.isKeyboard = z2;
                if (z2) {
                    i = view.getRootWindowInsets().getInsets(WindowInsetsCompat.Type.ime()).bottom;
                } else {
                    i = enterView.getEmojiPadding();
                }
                this.keyboardSize = i;
                this.bottomNavBarSize = view.getRootWindowInsets().getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
                view.getWindowInsetsController().controlWindowInsetsAnimation(WindowInsetsCompat.Type.ime(), -1L, new LinearInterpolator(), new CancellationSignal(), new WindowInsetsAnimationControlListener() { // from class: org.telegram.ui.KeyboardHideHelper.1
                    @Override // android.view.WindowInsetsAnimationControlListener
                    public void onReady(WindowInsetsAnimationController windowInsetsAnimationController, int i2) {
                        KeyboardHideHelper.this.insetsController = windowInsetsAnimationController;
                    }

                    @Override // android.view.WindowInsetsAnimationControlListener
                    public void onFinished(WindowInsetsAnimationController windowInsetsAnimationController) {
                        KeyboardHideHelper.this.insetsController = null;
                    }

                    @Override // android.view.WindowInsetsAnimationControlListener
                    public void onCancelled(WindowInsetsAnimationController windowInsetsAnimationController) {
                        KeyboardHideHelper.this.insetsController = null;
                    }
                });
                this.fromY = ev.getRawY();
                this.exactlyMovingKeyboard = false;
                panLayoutHelper.setEnabled(false);
                update(0.0f, false);
                listView.stopScroll();
                this.lastDifferentT = 0.0f;
                this.lastT = 0.0f;
                this.rawT = 0.0f;
                this.t = 0.0f;
                panLayoutHelper.OnTransitionStart(true, view.getHeight());
                if (this.tracker == null) {
                    this.tracker = VelocityTracker.obtain();
                }
                this.tracker.clear();
            }
            if (!this.movingKeyboard) {
                return false;
            }
            this.tracker.addMovement(ev);
            float rawY = (ev.getRawY() - this.fromY) / this.keyboardSize;
            this.rawT = rawY;
            this.t = MathUtils.clamp(rawY, 0.0f, 1.0f);
            if (ev.getAction() != 1 && ev.getAction() != 3) {
                update(this.t, true);
                float f2 = this.lastT;
                float f3 = this.t;
                if (f2 != f3) {
                    this.lastDifferentT = f2;
                }
                this.lastT = f3;
                return true;
            }
            this.movingKeyboard = false;
            this.exactlyMovingKeyboard = false;
            this.endingMovingKeyboard = true;
            this.tracker.computeCurrentVelocity(1000);
            float f4 = this.t;
            boolean end = (f4 > 0.15f && f4 >= this.lastDifferentT) || f4 > 0.8f;
            if (end) {
                f = 1.0f;
            }
            float endT = f;
            ValueAnimator va = ValueAnimator.ofFloat(f4, endT);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.KeyboardHideHelper$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    KeyboardHideHelper.this.m3579lambda$onTouch$0$orgtelegramuiKeyboardHideHelper(valueAnimator);
                }
            });
            va.addListener(new AnonymousClass2(end, endT, panLayoutHelper, view));
            va.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            va.setDuration(200L);
            va.start();
            if (!end || !this.startedAtBottom || ca == null) {
                z = true;
            } else {
                z = true;
                ca.scrollToLastMessage(true);
            }
            this.startedOutsideView = false;
            return z;
        }
        return false;
    }

    /* renamed from: lambda$onTouch$0$org-telegram-ui-KeyboardHideHelper */
    public /* synthetic */ void m3579lambda$onTouch$0$orgtelegramuiKeyboardHideHelper(ValueAnimator a) {
        float floatValue = ((Float) a.getAnimatedValue()).floatValue();
        this.t = floatValue;
        update(floatValue, true);
    }

    /* renamed from: org.telegram.ui.KeyboardHideHelper$2 */
    /* loaded from: classes4.dex */
    public class AnonymousClass2 extends AnimatorListenerAdapter {
        final /* synthetic */ boolean val$end;
        final /* synthetic */ float val$endT;
        final /* synthetic */ AdjustPanLayoutHelper val$panLayoutHelper;
        final /* synthetic */ View val$view;

        AnonymousClass2(boolean z, float f, AdjustPanLayoutHelper adjustPanLayoutHelper, View view) {
            KeyboardHideHelper.this = this$0;
            this.val$end = z;
            this.val$endT = f;
            this.val$panLayoutHelper = adjustPanLayoutHelper;
            this.val$view = view;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            if (KeyboardHideHelper.this.insetsController != null && KeyboardHideHelper.this.isKeyboard) {
                KeyboardHideHelper.this.insetsController.finish(!this.val$end);
            }
            KeyboardHideHelper.this.update(1.0f, false);
            KeyboardHideHelper.this.rawT = this.val$endT;
            this.val$panLayoutHelper.OnTransitionEnd();
            View view = this.val$view;
            final AdjustPanLayoutHelper adjustPanLayoutHelper = this.val$panLayoutHelper;
            view.post(new Runnable() { // from class: org.telegram.ui.KeyboardHideHelper$2$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    KeyboardHideHelper.AnonymousClass2.this.m3580lambda$onAnimationEnd$0$orgtelegramuiKeyboardHideHelper$2(adjustPanLayoutHelper);
                }
            });
        }

        /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-KeyboardHideHelper$2 */
        public /* synthetic */ void m3580lambda$onAnimationEnd$0$orgtelegramuiKeyboardHideHelper$2(AdjustPanLayoutHelper panLayoutHelper) {
            panLayoutHelper.setEnabled(true);
            KeyboardHideHelper.this.endingMovingKeyboard = false;
        }
    }

    public boolean disableScrolling() {
        return ENABLED && (this.movingKeyboard || this.endingMovingKeyboard) && this.rawT >= 0.0f;
    }

    public void update(float t, boolean withKeyboard) {
        if (this.isKeyboard) {
            float y = Math.max((((1.0f - t) * this.keyboardSize) - this.bottomNavBarSize) - 1.0f, 0.0f);
            this.panLayoutHelper.OnPanTranslationUpdate(y, t, true);
            ((View) ((View) this.view.getParent()).getParent()).setTranslationY(-y);
            if (withKeyboard && this.insetsController != null && Build.VERSION.SDK_INT >= 30) {
                this.insetsController.setInsetsAndAlpha(Insets.of(0, 0, 0, (int) (this.keyboardSize * (1.0f - t))), 1.0f, t);
                return;
            }
            return;
        }
        this.panLayoutHelper.OnPanTranslationUpdate((1.0f - t) * this.keyboardSize, t, true);
    }
}
