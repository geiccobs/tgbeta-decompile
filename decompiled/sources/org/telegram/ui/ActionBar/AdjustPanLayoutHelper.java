package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.SystemClock;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.WindowInsetsAnimation;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ChatListItemAnimator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
/* loaded from: classes4.dex */
public class AdjustPanLayoutHelper {
    public static final long keyboardDuration = 250;
    private boolean animationInProgress;
    ValueAnimator animator;
    boolean checkHierarchyHeight;
    private ViewGroup contentView;
    float from;
    private boolean ignoreOnce;
    boolean inverse;
    boolean isKeyboardVisible;
    protected float keyboardSize;
    private boolean needDelay;
    int notificationsIndex;
    private final View parent;
    View parentForListener;
    private View resizableView;
    private View resizableViewToSet;
    long startAfter;
    float to;
    public static boolean USE_ANDROID11_INSET_ANIMATOR = false;
    public static final Interpolator keyboardInterpolator = ChatListItemAnimator.DEFAULT_INTERPOLATOR;
    private boolean usingInsetAnimator = false;
    private Runnable delayedAnimationRunnable = new Runnable() { // from class: org.telegram.ui.ActionBar.AdjustPanLayoutHelper.1
        @Override // java.lang.Runnable
        public void run() {
            if (AdjustPanLayoutHelper.this.animator != null && !AdjustPanLayoutHelper.this.animator.isRunning()) {
                AdjustPanLayoutHelper.this.animator.start();
            }
        }
    };
    int previousHeight = -1;
    int previousContentHeight = -1;
    int previousStartOffset = -1;
    ArrayList<View> viewsToHeightSet = new ArrayList<>();
    ViewTreeObserver.OnPreDrawListener onPreDrawListener = new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.ActionBar.AdjustPanLayoutHelper.2
        @Override // android.view.ViewTreeObserver.OnPreDrawListener
        public boolean onPreDraw() {
            boolean z = true;
            if (SharedConfig.smoothKeyboard) {
                int contentHeight = AdjustPanLayoutHelper.this.parent.getHeight();
                if (contentHeight - AdjustPanLayoutHelper.this.startOffset() == AdjustPanLayoutHelper.this.previousHeight - AdjustPanLayoutHelper.this.previousStartOffset || contentHeight == AdjustPanLayoutHelper.this.previousHeight || AdjustPanLayoutHelper.this.animator != null) {
                    if (AdjustPanLayoutHelper.this.animator == null) {
                        AdjustPanLayoutHelper.this.previousHeight = contentHeight;
                        AdjustPanLayoutHelper adjustPanLayoutHelper = AdjustPanLayoutHelper.this;
                        adjustPanLayoutHelper.previousContentHeight = adjustPanLayoutHelper.contentView.getHeight();
                        AdjustPanLayoutHelper adjustPanLayoutHelper2 = AdjustPanLayoutHelper.this;
                        adjustPanLayoutHelper2.previousStartOffset = adjustPanLayoutHelper2.startOffset();
                        AdjustPanLayoutHelper.this.usingInsetAnimator = false;
                    }
                    return true;
                } else if (!AdjustPanLayoutHelper.this.heightAnimationEnabled() || Math.abs(AdjustPanLayoutHelper.this.previousHeight - contentHeight) < AndroidUtilities.dp(20.0f)) {
                    AdjustPanLayoutHelper.this.previousHeight = contentHeight;
                    AdjustPanLayoutHelper adjustPanLayoutHelper3 = AdjustPanLayoutHelper.this;
                    adjustPanLayoutHelper3.previousContentHeight = adjustPanLayoutHelper3.contentView.getHeight();
                    AdjustPanLayoutHelper adjustPanLayoutHelper4 = AdjustPanLayoutHelper.this;
                    adjustPanLayoutHelper4.previousStartOffset = adjustPanLayoutHelper4.startOffset();
                    AdjustPanLayoutHelper.this.usingInsetAnimator = false;
                    return true;
                } else if (AdjustPanLayoutHelper.this.previousHeight != -1 && AdjustPanLayoutHelper.this.previousContentHeight == AdjustPanLayoutHelper.this.contentView.getHeight()) {
                    AdjustPanLayoutHelper adjustPanLayoutHelper5 = AdjustPanLayoutHelper.this;
                    if (contentHeight >= adjustPanLayoutHelper5.contentView.getBottom()) {
                        z = false;
                    }
                    adjustPanLayoutHelper5.isKeyboardVisible = z;
                    AdjustPanLayoutHelper adjustPanLayoutHelper6 = AdjustPanLayoutHelper.this;
                    adjustPanLayoutHelper6.animateHeight(adjustPanLayoutHelper6.previousHeight, contentHeight, AdjustPanLayoutHelper.this.isKeyboardVisible);
                    AdjustPanLayoutHelper.this.previousHeight = contentHeight;
                    AdjustPanLayoutHelper adjustPanLayoutHelper7 = AdjustPanLayoutHelper.this;
                    adjustPanLayoutHelper7.previousContentHeight = adjustPanLayoutHelper7.contentView.getHeight();
                    AdjustPanLayoutHelper adjustPanLayoutHelper8 = AdjustPanLayoutHelper.this;
                    adjustPanLayoutHelper8.previousStartOffset = adjustPanLayoutHelper8.startOffset();
                    return false;
                } else {
                    AdjustPanLayoutHelper.this.previousHeight = contentHeight;
                    AdjustPanLayoutHelper adjustPanLayoutHelper9 = AdjustPanLayoutHelper.this;
                    adjustPanLayoutHelper9.previousContentHeight = adjustPanLayoutHelper9.contentView.getHeight();
                    AdjustPanLayoutHelper adjustPanLayoutHelper10 = AdjustPanLayoutHelper.this;
                    adjustPanLayoutHelper10.previousStartOffset = adjustPanLayoutHelper10.startOffset();
                    return false;
                }
            }
            AdjustPanLayoutHelper.this.onDetach();
            return true;
        }
    };
    private boolean enabled = true;

    public View getAdjustingParent() {
        return this.parent;
    }

    public View getAdjustingContentView() {
        return this.contentView;
    }

    public void animateHeight(int previousHeight, int contentHeight, boolean isKeyboardVisible) {
        if (this.ignoreOnce) {
            this.ignoreOnce = false;
        } else if (!this.enabled) {
        } else {
            startTransition(previousHeight, contentHeight, isKeyboardVisible);
            this.animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ActionBar.AdjustPanLayoutHelper$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    AdjustPanLayoutHelper.this.m1402x3382a933(valueAnimator);
                }
            });
            int selectedAccount = UserConfig.selectedAccount;
            this.animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.AdjustPanLayoutHelper.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (!AdjustPanLayoutHelper.this.usingInsetAnimator) {
                        AdjustPanLayoutHelper.this.stopTransition();
                    }
                }
            });
            this.animator.setDuration(250L);
            this.animator.setInterpolator(keyboardInterpolator);
            this.notificationsIndex = NotificationCenter.getInstance(selectedAccount).setAnimationInProgress(this.notificationsIndex, null);
            if (this.needDelay) {
                this.needDelay = false;
                this.startAfter = SystemClock.elapsedRealtime() + 100;
                AndroidUtilities.runOnUIThread(this.delayedAnimationRunnable, 100L);
                return;
            }
            this.animator.start();
            this.startAfter = -1L;
        }
    }

    /* renamed from: lambda$animateHeight$0$org-telegram-ui-ActionBar-AdjustPanLayoutHelper */
    public /* synthetic */ void m1402x3382a933(ValueAnimator animation) {
        if (!this.usingInsetAnimator) {
            updateTransition(((Float) animation.getAnimatedValue()).floatValue());
        }
    }

    public void startTransition(int previousHeight, int contentHeight, boolean isKeyboardVisible) {
        ValueAnimator valueAnimator = this.animator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        int startOffset = startOffset();
        getViewsToSetHeight(this.parent);
        int additionalContentHeight = 0;
        if (this.checkHierarchyHeight) {
            ViewParent viewParent = this.parent.getParent();
            if (viewParent instanceof View) {
                additionalContentHeight = ((View) viewParent).getHeight() - contentHeight;
            }
        }
        setViewHeight(Math.max(previousHeight, contentHeight + additionalContentHeight));
        this.resizableView.requestLayout();
        onTransitionStart(isKeyboardVisible, contentHeight);
        float dy = contentHeight - previousHeight;
        this.keyboardSize = Math.abs(dy);
        this.animationInProgress = true;
        if (contentHeight > previousHeight) {
            float dy2 = dy - startOffset;
            this.parent.setTranslationY(-dy2);
            onPanTranslationUpdate(dy2, 1.0f, isKeyboardVisible);
            this.from = -dy2;
            this.to = 0.0f;
            this.inverse = true;
        } else {
            this.parent.setTranslationY(this.previousStartOffset);
            onPanTranslationUpdate(-this.previousStartOffset, 0.0f, isKeyboardVisible);
            this.to = -this.previousStartOffset;
            this.from = dy;
            this.inverse = false;
        }
        this.animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.usingInsetAnimator = false;
    }

    public void updateTransition(float t) {
        if (this.inverse) {
            t = 1.0f - t;
        }
        float y = (int) ((this.from * t) + (this.to * (1.0f - t)));
        this.parent.setTranslationY(y);
        onPanTranslationUpdate(-y, t, this.isKeyboardVisible);
    }

    public void stopTransition() {
        ValueAnimator valueAnimator = this.animator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.animationInProgress = false;
        this.usingInsetAnimator = false;
        NotificationCenter.getInstance(UserConfig.selectedAccount).onAnimationFinish(this.notificationsIndex);
        this.animator = null;
        setViewHeight(-1);
        this.viewsToHeightSet.clear();
        this.resizableView.requestLayout();
        boolean z = this.isKeyboardVisible;
        onPanTranslationUpdate(0.0f, z ? 1.0f : 0.0f, z);
        this.parent.setTranslationY(0.0f);
        onTransitionEnd();
    }

    public void stopTransition(float t, boolean isKeyboardVisible) {
        ValueAnimator valueAnimator = this.animator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.animationInProgress = false;
        NotificationCenter.getInstance(UserConfig.selectedAccount).onAnimationFinish(this.notificationsIndex);
        this.animator = null;
        setViewHeight(-1);
        this.viewsToHeightSet.clear();
        this.resizableView.requestLayout();
        this.isKeyboardVisible = isKeyboardVisible;
        onPanTranslationUpdate(0.0f, t, isKeyboardVisible);
        this.parent.setTranslationY(0.0f);
        onTransitionEnd();
    }

    public void setViewHeight(int height) {
        for (int i = 0; i < this.viewsToHeightSet.size(); i++) {
            this.viewsToHeightSet.get(i).getLayoutParams().height = height;
            this.viewsToHeightSet.get(i).requestLayout();
        }
    }

    protected int startOffset() {
        return 0;
    }

    public void getViewsToSetHeight(View parent) {
        this.viewsToHeightSet.clear();
        View v = parent;
        while (v != null) {
            this.viewsToHeightSet.add(v);
            if (v == this.resizableView) {
                return;
            }
            if (v.getParent() instanceof View) {
                v = (View) v.getParent();
            } else {
                v = null;
            }
        }
    }

    public AdjustPanLayoutHelper(View parent) {
        this.parent = parent;
        onAttach();
    }

    public AdjustPanLayoutHelper(View parent, boolean useInsetsAnimator) {
        boolean z = false;
        if (USE_ANDROID11_INSET_ANIMATOR && useInsetsAnimator) {
            z = true;
        }
        USE_ANDROID11_INSET_ANIMATOR = z;
        this.parent = parent;
        onAttach();
    }

    public void onAttach() {
        if (!SharedConfig.smoothKeyboard) {
            return;
        }
        onDetach();
        Context context = this.parent.getContext();
        Activity activity = getActivity(context);
        if (activity != null) {
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            this.contentView = (ViewGroup) decorView.findViewById(16908290);
        }
        View findResizableView = findResizableView(this.parent);
        this.resizableView = findResizableView;
        if (findResizableView != null) {
            this.parentForListener = findResizableView;
            findResizableView.getViewTreeObserver().addOnPreDrawListener(this.onPreDrawListener);
        }
        if (USE_ANDROID11_INSET_ANIMATOR && Build.VERSION.SDK_INT >= 30) {
            setupNewCallback();
        }
    }

    private Activity getActivity(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (context instanceof ContextThemeWrapper) {
            return getActivity(((ContextThemeWrapper) context).getBaseContext());
        }
        return null;
    }

    private View findResizableView(View parent) {
        View view = this.resizableViewToSet;
        if (view != null) {
            return view;
        }
        for (View view2 = parent; view2 != null; view2 = (View) view2.getParent()) {
            if (view2.getParent() instanceof DrawerLayoutContainer) {
                return view2;
            }
            if (!(view2.getParent() instanceof View)) {
                return null;
            }
        }
        return null;
    }

    public void onDetach() {
        ValueAnimator valueAnimator = this.animator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        View view = this.parentForListener;
        if (view != null) {
            view.getViewTreeObserver().removeOnPreDrawListener(this.onPreDrawListener);
            this.parentForListener = null;
        }
        if (this.parent != null && USE_ANDROID11_INSET_ANIMATOR && Build.VERSION.SDK_INT >= 30) {
            this.parent.setWindowInsetsAnimationCallback(null);
        }
    }

    public void setEnabled(boolean value) {
        this.enabled = value;
    }

    public void ignoreOnce() {
        this.ignoreOnce = true;
    }

    protected boolean heightAnimationEnabled() {
        return true;
    }

    public void OnPanTranslationUpdate(float y, float progress, boolean keyboardVisible) {
        onPanTranslationUpdate(y, progress, keyboardVisible);
    }

    public void OnTransitionStart(boolean keyboardVisible, int contentHeight) {
        onTransitionStart(keyboardVisible, contentHeight);
    }

    public void OnTransitionEnd() {
        onTransitionEnd();
    }

    public void onPanTranslationUpdate(float y, float progress, boolean keyboardVisible) {
    }

    public void onTransitionStart(boolean keyboardVisible, int contentHeight) {
    }

    public void onTransitionEnd() {
    }

    public void setResizableView(FrameLayout windowView) {
        this.resizableViewToSet = windowView;
    }

    public boolean animationInProgress() {
        return this.animationInProgress;
    }

    public void setCheckHierarchyHeight(boolean checkHierarchyHeight) {
        this.checkHierarchyHeight = checkHierarchyHeight;
    }

    public void delayAnimation() {
        this.needDelay = true;
    }

    public void runDelayedAnimation() {
        AndroidUtilities.cancelRunOnUIThread(this.delayedAnimationRunnable);
        this.delayedAnimationRunnable.run();
    }

    private void setupNewCallback() {
        View view = this.resizableView;
        if (view == null) {
            return;
        }
        view.setWindowInsetsAnimationCallback(new WindowInsetsAnimation.Callback(1) { // from class: org.telegram.ui.ActionBar.AdjustPanLayoutHelper.4
            @Override // android.view.WindowInsetsAnimation.Callback
            public WindowInsets onProgress(WindowInsets insets, List<WindowInsetsAnimation> runningAnimations) {
                if (!AdjustPanLayoutHelper.this.animationInProgress || AndroidUtilities.screenRefreshRate < 90.0f) {
                    return insets;
                }
                WindowInsetsAnimation imeAnimation = null;
                Iterator<WindowInsetsAnimation> it = runningAnimations.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    WindowInsetsAnimation animation = it.next();
                    if ((animation.getTypeMask() & WindowInsetsCompat.Type.ime()) != 0) {
                        imeAnimation = animation;
                        break;
                    }
                }
                if (imeAnimation != null && SystemClock.elapsedRealtime() >= AdjustPanLayoutHelper.this.startAfter) {
                    AdjustPanLayoutHelper.this.usingInsetAnimator = true;
                    AdjustPanLayoutHelper.this.updateTransition(imeAnimation.getInterpolatedFraction());
                }
                return insets;
            }

            @Override // android.view.WindowInsetsAnimation.Callback
            public void onEnd(WindowInsetsAnimation animation) {
                if (!AdjustPanLayoutHelper.this.animationInProgress || AndroidUtilities.screenRefreshRate < 90.0f) {
                    return;
                }
                AdjustPanLayoutHelper.this.stopTransition();
            }
        });
    }
}
