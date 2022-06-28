package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Insets;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.camera.CameraView;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.UndoView;
/* loaded from: classes4.dex */
public class BottomSheet extends Dialog {
    private static final boolean AVOID_SYSTEM_CUTOUT_FULLSCREEN = false;
    private boolean allowCustomAnimation;
    private boolean allowDrawContent;
    protected boolean allowNestedScroll;
    private boolean applyBottomPadding;
    private boolean applyTopPadding;
    protected ColorDrawable backDrawable;
    protected int backgroundPaddingLeft;
    protected int backgroundPaddingTop;
    protected int behindKeyboardColor;
    protected String behindKeyboardColorKey;
    private boolean bigTitle;
    private int bottomInset;
    protected boolean calcMandatoryInsets;
    private boolean canDismissWithSwipe;
    protected ContainerView container;
    protected ViewGroup containerView;
    protected int currentAccount;
    private float currentPanTranslationY;
    protected AnimatorSet currentSheetAnimation;
    protected int currentSheetAnimationType;
    private View customView;
    protected BottomSheetDelegateInterface delegate;
    protected boolean dimBehind;
    protected int dimBehindAlpha;
    private boolean disableScroll;
    private Runnable dismissRunnable;
    private boolean dismissed;
    public boolean drawDoubleNavigationBar;
    public boolean drawNavigationBar;
    private boolean focusable;
    private boolean fullHeight;
    protected boolean fullWidth;
    private float hideSystemVerticalInsetsProgress;
    protected boolean isFullscreen;
    protected boolean isPortrait;
    private int[] itemIcons;
    private ArrayList<BottomSheetCell> itemViews;
    private CharSequence[] items;
    private ValueAnimator keyboardContentAnimator;
    protected boolean keyboardVisible;
    private WindowInsets lastInsets;
    private int layoutCount;
    private int leftInset;
    private boolean multipleLinesTitle;
    protected int navBarColor;
    protected String navBarColorKey;
    protected float navigationBarAlpha;
    protected ValueAnimator navigationBarAnimation;
    protected View nestedScrollChild;
    private DialogInterface.OnClickListener onClickListener;
    private DialogInterface.OnDismissListener onHideListener;
    protected Interpolator openInterpolator;
    private boolean openNoDelay;
    private int overlayDrawNavBarColor;
    protected Theme.ResourcesProvider resourcesProvider;
    private int rightInset;
    public boolean scrollNavBar;
    protected Drawable shadowDrawable;
    private boolean showWithoutAnimation;
    protected boolean smoothKeyboardAnimationEnabled;
    protected Runnable startAnimationRunnable;
    private int statusBarHeight;
    private int tag;
    private CharSequence title;
    private TextView titleView;
    private int touchSlop;
    public boolean useBackgroundTopPadding;
    private boolean useFastDismiss;
    protected boolean useHardwareLayer;
    protected boolean useLightNavBar;
    protected boolean useLightStatusBar;
    protected boolean useSmoothKeyboard;

    /* loaded from: classes4.dex */
    public interface BottomSheetDelegateInterface {
        boolean canDismiss();

        void onOpenAnimationEnd();

        void onOpenAnimationStart();
    }

    static /* synthetic */ int access$1210(BottomSheet x0) {
        int i = x0.layoutCount;
        x0.layoutCount = i - 1;
        return i;
    }

    static /* synthetic */ int access$712(BottomSheet x0, int x1) {
        int i = x0.bottomInset + x1;
        x0.bottomInset = i;
        return i;
    }

    static /* synthetic */ int access$720(BottomSheet x0, int x1) {
        int i = x0.bottomInset - x1;
        x0.bottomInset = i;
        return i;
    }

    public void setDisableScroll(boolean b) {
        this.disableScroll = b;
    }

    /* loaded from: classes4.dex */
    public class ContainerView extends FrameLayout implements NestedScrollingParent {
        private boolean keyboardChanged;
        private int keyboardHeight;
        private int startedTrackingX;
        private int startedTrackingY;
        private VelocityTracker velocityTracker = null;
        private int startedTrackingPointerId = -1;
        private boolean maybeStartTracking = false;
        private boolean startedTracking = false;
        private AnimatorSet currentAnimation = null;
        private Rect rect = new Rect();
        private Paint backgroundPaint = new Paint();
        private float y = 0.0f;
        private NestedScrollingParentHelper nestedScrollingParentHelper = new NestedScrollingParentHelper(this);

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ContainerView(Context context) {
            super(context);
            BottomSheet.this = this$0;
            setWillNotDraw(false);
        }

        @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
        public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
            return (BottomSheet.this.nestedScrollChild == null || child == BottomSheet.this.nestedScrollChild) && !BottomSheet.this.dismissed && BottomSheet.this.allowNestedScroll && nestedScrollAxes == 2 && !BottomSheet.this.canDismissWithSwipe();
        }

        @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
        public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
            this.nestedScrollingParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
            if (BottomSheet.this.dismissed || !BottomSheet.this.allowNestedScroll) {
                return;
            }
            cancelCurrentAnimation();
        }

        @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
        public void onStopNestedScroll(View target) {
            this.nestedScrollingParentHelper.onStopNestedScroll(target);
            if (BottomSheet.this.dismissed || !BottomSheet.this.allowNestedScroll) {
                return;
            }
            BottomSheet.this.containerView.getTranslationY();
            checkDismiss(0.0f, 0.0f);
        }

        @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
        public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
            if (BottomSheet.this.dismissed || !BottomSheet.this.allowNestedScroll) {
                return;
            }
            cancelCurrentAnimation();
            if (dyUnconsumed != 0) {
                float currentTranslation = BottomSheet.this.containerView.getTranslationY() - dyUnconsumed;
                if (currentTranslation < 0.0f) {
                    currentTranslation = 0.0f;
                }
                BottomSheet.this.containerView.setTranslationY(currentTranslation);
                BottomSheet.this.container.invalidate();
            }
        }

        @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
        public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
            if (BottomSheet.this.dismissed || !BottomSheet.this.allowNestedScroll) {
                return;
            }
            cancelCurrentAnimation();
            float currentTranslation = BottomSheet.this.containerView.getTranslationY();
            if (currentTranslation > 0.0f && dy > 0) {
                float currentTranslation2 = currentTranslation - dy;
                consumed[1] = dy;
                if (currentTranslation2 < 0.0f) {
                    currentTranslation2 = 0.0f;
                }
                BottomSheet.this.containerView.setTranslationY(currentTranslation2);
                BottomSheet.this.container.invalidate();
            }
        }

        @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
        public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
            return false;
        }

        @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
        public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
            return false;
        }

        @Override // android.view.ViewGroup, androidx.core.view.NestedScrollingParent
        public int getNestedScrollAxes() {
            return this.nestedScrollingParentHelper.getNestedScrollAxes();
        }

        private void checkDismiss(float velX, float velY) {
            float translationY = BottomSheet.this.containerView.getTranslationY();
            boolean backAnimation = (translationY < AndroidUtilities.getPixelsInCM(0.8f, false) && (velY < 3500.0f || Math.abs(velY) < Math.abs(velX))) || (velY < 0.0f && Math.abs(velY) >= 3500.0f);
            if (!backAnimation) {
                boolean allowOld = BottomSheet.this.allowCustomAnimation;
                BottomSheet.this.allowCustomAnimation = false;
                BottomSheet.this.useFastDismiss = true;
                BottomSheet.this.dismiss();
                BottomSheet.this.allowCustomAnimation = allowOld;
                return;
            }
            this.currentAnimation = new AnimatorSet();
            ValueAnimator invalidateContainer = ValueAnimator.ofFloat(0.0f, 1.0f);
            invalidateContainer.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ActionBar.BottomSheet$ContainerView$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    BottomSheet.ContainerView.this.m1420x6c97df57(valueAnimator);
                }
            });
            this.currentAnimation.playTogether(ObjectAnimator.ofFloat(BottomSheet.this.containerView, "translationY", 0.0f), invalidateContainer);
            this.currentAnimation.setDuration((int) ((Math.max(0.0f, translationY) / AndroidUtilities.getPixelsInCM(0.8f, false)) * 250.0f));
            this.currentAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.currentAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.BottomSheet.ContainerView.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (ContainerView.this.currentAnimation != null && ContainerView.this.currentAnimation.equals(animation)) {
                        ContainerView.this.currentAnimation = null;
                    }
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
                }
            });
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 512);
            this.currentAnimation.start();
        }

        /* renamed from: lambda$checkDismiss$0$org-telegram-ui-ActionBar-BottomSheet$ContainerView */
        public /* synthetic */ void m1420x6c97df57(ValueAnimator a) {
            if (BottomSheet.this.container != null) {
                BottomSheet.this.container.invalidate();
            }
        }

        private void cancelCurrentAnimation() {
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.currentAnimation = null;
            }
        }

        public boolean processTouchEvent(MotionEvent ev, boolean intercept) {
            if (BottomSheet.this.dismissed) {
                return false;
            }
            if (BottomSheet.this.onContainerTouchEvent(ev)) {
                return true;
            }
            if (BottomSheet.this.canDismissWithTouchOutside() && ev != null && ((ev.getAction() == 0 || ev.getAction() == 2) && !this.startedTracking && !this.maybeStartTracking && ev.getPointerCount() == 1)) {
                this.startedTrackingX = (int) ev.getX();
                int y = (int) ev.getY();
                this.startedTrackingY = y;
                if (y < BottomSheet.this.containerView.getTop() || this.startedTrackingX < BottomSheet.this.containerView.getLeft() || this.startedTrackingX > BottomSheet.this.containerView.getRight()) {
                    BottomSheet.this.dismiss();
                    return true;
                }
                BottomSheet.this.onScrollUpBegin(this.y);
                this.startedTrackingPointerId = ev.getPointerId(0);
                this.maybeStartTracking = true;
                cancelCurrentAnimation();
                VelocityTracker velocityTracker = this.velocityTracker;
                if (velocityTracker != null) {
                    velocityTracker.clear();
                }
            } else if (ev != null && ev.getAction() == 2 && ev.getPointerId(0) == this.startedTrackingPointerId) {
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                }
                float dx = Math.abs((int) (ev.getX() - this.startedTrackingX));
                float dy = ((int) ev.getY()) - this.startedTrackingY;
                boolean canScrollUp = BottomSheet.this.onScrollUp(this.y + dy);
                this.velocityTracker.addMovement(ev);
                if (!BottomSheet.this.disableScroll && this.maybeStartTracking && !this.startedTracking && dy > 0.0f && dy / 3.0f > Math.abs(dx) && Math.abs(dy) >= BottomSheet.this.touchSlop) {
                    this.startedTrackingY = (int) ev.getY();
                    this.maybeStartTracking = false;
                    this.startedTracking = true;
                    requestDisallowInterceptTouchEvent(true);
                } else if (this.startedTracking) {
                    float f = this.y + dy;
                    this.y = f;
                    if (!canScrollUp) {
                        this.y = Math.max(f, 0.0f);
                    }
                    BottomSheet.this.containerView.setTranslationY(Math.max(this.y, 0.0f));
                    this.startedTrackingY = (int) ev.getY();
                    BottomSheet.this.container.invalidate();
                }
            } else if (ev == null || (ev.getPointerId(0) == this.startedTrackingPointerId && (ev.getAction() == 3 || ev.getAction() == 1 || ev.getAction() == 6))) {
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                }
                this.velocityTracker.computeCurrentVelocity(1000);
                BottomSheet.this.onScrollUpEnd(this.y);
                if (this.startedTracking || this.y > 0.0f) {
                    checkDismiss(this.velocityTracker.getXVelocity(), this.velocityTracker.getYVelocity());
                } else {
                    this.maybeStartTracking = false;
                }
                this.startedTracking = false;
                VelocityTracker velocityTracker2 = this.velocityTracker;
                if (velocityTracker2 != null) {
                    velocityTracker2.recycle();
                    this.velocityTracker = null;
                }
                this.startedTrackingPointerId = -1;
            }
            return (!intercept && this.maybeStartTracking) || this.startedTracking || !BottomSheet.this.canDismissWithSwipe();
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent ev) {
            return processTouchEvent(ev, false);
        }

        /* JADX WARN: Removed duplicated region for block: B:57:0x014b  */
        /* JADX WARN: Removed duplicated region for block: B:60:0x0156  */
        /* JADX WARN: Removed duplicated region for block: B:70:0x01c0  */
        @Override // android.widget.FrameLayout, android.view.View
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        protected void onMeasure(int r17, int r18) {
            /*
                Method dump skipped, instructions count: 495
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.BottomSheet.ContainerView.onMeasure(int, int):void");
        }

        @Override // android.view.View, android.view.ViewParent
        public void requestLayout() {
            super.requestLayout();
        }

        @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            int right2;
            int left2;
            int childLeft;
            int childTop;
            int right3;
            int left3;
            BottomSheet.access$1210(BottomSheet.this);
            int i = 2;
            if (BottomSheet.this.containerView != null) {
                int t = (bottom - top) - BottomSheet.this.containerView.getMeasuredHeight();
                if (BottomSheet.this.lastInsets != null && Build.VERSION.SDK_INT >= 21) {
                    left3 = left + BottomSheet.this.getLeftInset();
                    right3 = right - BottomSheet.this.getRightInset();
                    if (BottomSheet.this.useSmoothKeyboard) {
                        t = 0;
                    } else {
                        t = (int) (t - ((BottomSheet.this.lastInsets.getSystemWindowInsetBottom() * (1.0f - BottomSheet.this.hideSystemVerticalInsetsProgress)) - (BottomSheet.this.drawNavigationBar ? 0 : BottomSheet.this.getBottomInset())));
                        if (Build.VERSION.SDK_INT >= 29) {
                            t -= BottomSheet.this.getAdditionalMandatoryOffsets();
                        }
                    }
                } else {
                    left3 = left;
                    right3 = right;
                }
                int l = ((right3 - left3) - BottomSheet.this.containerView.getMeasuredWidth()) / 2;
                if (BottomSheet.this.lastInsets != null && Build.VERSION.SDK_INT >= 21) {
                    l += BottomSheet.this.getLeftInset();
                }
                if (BottomSheet.this.smoothKeyboardAnimationEnabled && BottomSheet.this.startAnimationRunnable == null && this.keyboardChanged && !BottomSheet.this.dismissed && BottomSheet.this.containerView.getTop() != t) {
                    BottomSheet.this.containerView.setTranslationY(BottomSheet.this.containerView.getTop() - t);
                    if (BottomSheet.this.keyboardContentAnimator != null) {
                        BottomSheet.this.keyboardContentAnimator.cancel();
                    }
                    BottomSheet bottomSheet = BottomSheet.this;
                    bottomSheet.keyboardContentAnimator = ValueAnimator.ofFloat(bottomSheet.containerView.getTranslationY(), 0.0f);
                    BottomSheet.this.keyboardContentAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ActionBar.BottomSheet$ContainerView$$ExternalSyntheticLambda1
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            BottomSheet.ContainerView.this.m1421x8219f19f(valueAnimator);
                        }
                    });
                    BottomSheet.this.keyboardContentAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.BottomSheet.ContainerView.2
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            BottomSheet.this.containerView.setTranslationY(0.0f);
                            ContainerView.this.invalidate();
                        }
                    });
                    BottomSheet.this.keyboardContentAnimator.setDuration(250L).setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
                    BottomSheet.this.keyboardContentAnimator.start();
                }
                BottomSheet.this.containerView.layout(l, t, BottomSheet.this.containerView.getMeasuredWidth() + l, BottomSheet.this.containerView.getMeasuredHeight() + t);
                left2 = left3;
                right2 = right3;
            } else {
                left2 = left;
                right2 = right;
            }
            int count = getChildCount();
            int i2 = 0;
            while (i2 < count) {
                View child = getChildAt(i2);
                if (child.getVisibility() != 8 && child != BottomSheet.this.containerView) {
                    BottomSheet bottomSheet2 = BottomSheet.this;
                    if (!bottomSheet2.onCustomLayout(child, left2, top, right2, bottom - (bottomSheet2.drawNavigationBar ? BottomSheet.this.getBottomInset() : 0))) {
                        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                        int width = child.getMeasuredWidth();
                        int height = child.getMeasuredHeight();
                        int gravity = lp.gravity;
                        if (gravity == -1) {
                            gravity = 51;
                        }
                        int absoluteGravity = gravity & 7;
                        int verticalGravity = gravity & 112;
                        switch (absoluteGravity & 7) {
                            case 1:
                                childLeft = ((((right2 - left2) - width) / i) + lp.leftMargin) - lp.rightMargin;
                                break;
                            case 5:
                                childLeft = (right2 - width) - lp.rightMargin;
                                break;
                            default:
                                childLeft = lp.leftMargin;
                                break;
                        }
                        switch (verticalGravity) {
                            case 16:
                                childTop = ((((bottom - top) - height) / i) + lp.topMargin) - lp.bottomMargin;
                                break;
                            case UndoView.ACTION_EMAIL_COPIED /* 80 */:
                                childTop = ((bottom - top) - height) - lp.bottomMargin;
                                break;
                            default:
                                childTop = lp.topMargin;
                                break;
                        }
                        if (BottomSheet.this.lastInsets != null && Build.VERSION.SDK_INT >= 21) {
                            childLeft += BottomSheet.this.getLeftInset();
                        }
                        child.layout(childLeft, childTop, childLeft + width, childTop + height);
                    }
                }
                i2++;
                i = 2;
            }
            if (BottomSheet.this.layoutCount == 0 && BottomSheet.this.startAnimationRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(BottomSheet.this.startAnimationRunnable);
                BottomSheet.this.startAnimationRunnable.run();
                BottomSheet.this.startAnimationRunnable = null;
            }
            this.keyboardChanged = false;
        }

        /* renamed from: lambda$onLayout$1$org-telegram-ui-ActionBar-BottomSheet$ContainerView */
        public /* synthetic */ void m1421x8219f19f(ValueAnimator valueAnimator) {
            BottomSheet.this.containerView.setTranslationY(((Float) valueAnimator.getAnimatedValue()).floatValue());
            invalidate();
        }

        @Override // android.view.ViewGroup
        public boolean onInterceptTouchEvent(MotionEvent event) {
            if (BottomSheet.this.canDismissWithSwipe()) {
                return processTouchEvent(event, true);
            }
            return super.onInterceptTouchEvent(event);
        }

        @Override // android.view.ViewGroup, android.view.ViewParent
        public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            if (this.maybeStartTracking && !this.startedTracking) {
                onTouchEvent(null);
            }
            super.requestDisallowInterceptTouchEvent(disallowIntercept);
        }

        @Override // android.view.View
        public boolean hasOverlappingRendering() {
            return false;
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchDraw(Canvas canvas) {
            int i;
            if (Build.VERSION.SDK_INT >= 26) {
                if (BottomSheet.this.navBarColorKey != null) {
                    Paint paint = this.backgroundPaint;
                    BottomSheet bottomSheet = BottomSheet.this;
                    paint.setColor(bottomSheet.getThemedColor(bottomSheet.navBarColorKey));
                } else {
                    this.backgroundPaint.setColor(BottomSheet.this.navBarColor);
                }
            } else {
                this.backgroundPaint.setColor(-16777216);
            }
            float f = 1.0f;
            if (BottomSheet.this.drawDoubleNavigationBar && !BottomSheet.this.shouldOverlayCameraViewOverNavBar()) {
                drawNavigationBar(canvas, 1.0f);
            }
            if (this.backgroundPaint.getAlpha() < 255 && BottomSheet.this.drawNavigationBar) {
                float translation = 0.0f;
                if (BottomSheet.this.scrollNavBar || (Build.VERSION.SDK_INT >= 29 && BottomSheet.this.getAdditionalMandatoryOffsets() > 0)) {
                    float dist = BottomSheet.this.containerView.getMeasuredHeight() - BottomSheet.this.containerView.getTranslationY();
                    translation = Math.max(0.0f, BottomSheet.this.getBottomInset() - dist);
                }
                int navBarHeight = BottomSheet.this.drawNavigationBar ? BottomSheet.this.getBottomInset() : 0;
                canvas.save();
                canvas.clipRect(BottomSheet.this.containerView.getLeft() + BottomSheet.this.backgroundPaddingLeft, ((getMeasuredHeight() - navBarHeight) + translation) - BottomSheet.this.currentPanTranslationY, BottomSheet.this.containerView.getRight() - BottomSheet.this.backgroundPaddingLeft, getMeasuredHeight() + translation, Region.Op.DIFFERENCE);
                super.dispatchDraw(canvas);
                canvas.restore();
            } else {
                super.dispatchDraw(canvas);
            }
            if (!BottomSheet.this.shouldOverlayCameraViewOverNavBar()) {
                if (BottomSheet.this.drawDoubleNavigationBar) {
                    f = BottomSheet.this.navigationBarAlpha * 0.7f;
                }
                drawNavigationBar(canvas, f);
            }
            if (BottomSheet.this.drawNavigationBar && BottomSheet.this.rightInset != 0 && BottomSheet.this.rightInset > BottomSheet.this.leftInset && BottomSheet.this.fullWidth && AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                canvas.drawRect(BottomSheet.this.containerView.getRight() - BottomSheet.this.backgroundPaddingLeft, BottomSheet.this.containerView.getTranslationY(), BottomSheet.this.containerView.getRight() + BottomSheet.this.rightInset, getMeasuredHeight(), this.backgroundPaint);
            }
            if (BottomSheet.this.drawNavigationBar && BottomSheet.this.leftInset != 0 && BottomSheet.this.leftInset > BottomSheet.this.rightInset && BottomSheet.this.fullWidth && AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                canvas.drawRect(0.0f, BottomSheet.this.containerView.getTranslationY(), BottomSheet.this.containerView.getLeft() + BottomSheet.this.backgroundPaddingLeft, getMeasuredHeight(), this.backgroundPaint);
            }
            if (BottomSheet.this.containerView.getTranslationY() < 0.0f) {
                Paint paint2 = this.backgroundPaint;
                if (BottomSheet.this.behindKeyboardColorKey != null) {
                    BottomSheet bottomSheet2 = BottomSheet.this;
                    i = bottomSheet2.getThemedColor(bottomSheet2.behindKeyboardColorKey);
                } else {
                    i = BottomSheet.this.behindKeyboardColor;
                }
                paint2.setColor(i);
                canvas.drawRect(BottomSheet.this.containerView.getLeft() + BottomSheet.this.backgroundPaddingLeft, BottomSheet.this.containerView.getY() + BottomSheet.this.containerView.getMeasuredHeight(), BottomSheet.this.containerView.getRight() - BottomSheet.this.backgroundPaddingLeft, getMeasuredHeight(), this.backgroundPaint);
            }
        }

        @Override // android.view.ViewGroup
        protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
            if (child instanceof CameraView) {
                if (BottomSheet.this.shouldOverlayCameraViewOverNavBar()) {
                    drawNavigationBar(canvas, 1.0f);
                }
                return super.drawChild(canvas, child, drawingTime);
            }
            return super.drawChild(canvas, child, drawingTime);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            int i;
            boolean restore = false;
            int i2 = 0;
            if (this.backgroundPaint.getAlpha() < 255 && BottomSheet.this.drawNavigationBar) {
                float translation = 0.0f;
                if (BottomSheet.this.scrollNavBar || (Build.VERSION.SDK_INT >= 29 && BottomSheet.this.getAdditionalMandatoryOffsets() > 0)) {
                    float dist = BottomSheet.this.containerView.getMeasuredHeight() - BottomSheet.this.containerView.getTranslationY();
                    translation = Math.max(0.0f, BottomSheet.this.getBottomInset() - dist);
                }
                int navBarHeight = BottomSheet.this.drawNavigationBar ? BottomSheet.this.getBottomInset() : 0;
                canvas.save();
                canvas.clipRect(BottomSheet.this.containerView.getLeft() + BottomSheet.this.backgroundPaddingLeft, ((getMeasuredHeight() - navBarHeight) + translation) - BottomSheet.this.currentPanTranslationY, BottomSheet.this.containerView.getRight() - BottomSheet.this.backgroundPaddingLeft, getMeasuredHeight() + translation, Region.Op.DIFFERENCE);
                restore = true;
            }
            super.onDraw(canvas);
            if (BottomSheet.this.lastInsets != null && this.keyboardHeight != 0) {
                Paint paint = this.backgroundPaint;
                if (BottomSheet.this.behindKeyboardColorKey != null) {
                    BottomSheet bottomSheet = BottomSheet.this;
                    i = bottomSheet.getThemedColor(bottomSheet.behindKeyboardColorKey);
                } else {
                    i = BottomSheet.this.behindKeyboardColor;
                }
                paint.setColor(i);
                float left = BottomSheet.this.containerView.getLeft() + BottomSheet.this.backgroundPaddingLeft;
                float measuredHeight = (getMeasuredHeight() - this.keyboardHeight) - (BottomSheet.this.drawNavigationBar ? BottomSheet.this.getBottomInset() : 0);
                float right = BottomSheet.this.containerView.getRight() - BottomSheet.this.backgroundPaddingLeft;
                int measuredHeight2 = getMeasuredHeight();
                if (BottomSheet.this.drawNavigationBar) {
                    i2 = BottomSheet.this.getBottomInset();
                }
                canvas.drawRect(left, measuredHeight, right, measuredHeight2 - i2, this.backgroundPaint);
            }
            BottomSheet.this.onContainerDraw(canvas);
            if (restore) {
                canvas.restore();
            }
        }

        public void drawNavigationBar(Canvas canvas, float alpha) {
            if (Build.VERSION.SDK_INT >= 26) {
                if (BottomSheet.this.navBarColorKey != null) {
                    Paint paint = this.backgroundPaint;
                    BottomSheet bottomSheet = BottomSheet.this;
                    paint.setColor(bottomSheet.getThemedColor(bottomSheet.navBarColorKey));
                } else {
                    this.backgroundPaint.setColor(BottomSheet.this.navBarColor);
                }
            } else {
                this.backgroundPaint.setColor(-16777216);
            }
            if ((BottomSheet.this.drawNavigationBar && BottomSheet.this.bottomInset != 0) || BottomSheet.this.currentPanTranslationY != 0.0f) {
                float translation = 0.0f;
                int navBarHeight = BottomSheet.this.drawNavigationBar ? BottomSheet.this.getBottomInset() : 0;
                if (BottomSheet.this.scrollNavBar || (Build.VERSION.SDK_INT >= 29 && BottomSheet.this.getAdditionalMandatoryOffsets() > 0)) {
                    if (BottomSheet.this.drawDoubleNavigationBar) {
                        translation = Math.max(0.0f, Math.min(navBarHeight - BottomSheet.this.currentPanTranslationY, BottomSheet.this.containerView.getTranslationY()));
                    } else {
                        float dist = BottomSheet.this.containerView.getMeasuredHeight() - BottomSheet.this.containerView.getTranslationY();
                        translation = Math.max(0.0f, BottomSheet.this.getBottomInset() - dist);
                    }
                }
                int wasAlpha = this.backgroundPaint.getAlpha();
                if (alpha < 1.0f) {
                    this.backgroundPaint.setAlpha((int) (wasAlpha * alpha));
                }
                canvas.drawRect(BottomSheet.this.containerView.getLeft() + BottomSheet.this.backgroundPaddingLeft, ((getMeasuredHeight() - navBarHeight) + translation) - BottomSheet.this.currentPanTranslationY, BottomSheet.this.containerView.getRight() - BottomSheet.this.backgroundPaddingLeft, getMeasuredHeight() + translation, this.backgroundPaint);
                this.backgroundPaint.setAlpha(wasAlpha);
                if (BottomSheet.this.overlayDrawNavBarColor != 0) {
                    this.backgroundPaint.setColor(BottomSheet.this.overlayDrawNavBarColor);
                    int wasAlpha2 = this.backgroundPaint.getAlpha();
                    if (alpha < 1.0f) {
                        this.backgroundPaint.setAlpha((int) (wasAlpha2 * alpha));
                        translation = 0.0f;
                    }
                    canvas.drawRect(BottomSheet.this.containerView.getLeft() + BottomSheet.this.backgroundPaddingLeft, ((getMeasuredHeight() - navBarHeight) + translation) - BottomSheet.this.currentPanTranslationY, BottomSheet.this.containerView.getRight() - BottomSheet.this.backgroundPaddingLeft, getMeasuredHeight() + translation, this.backgroundPaint);
                    this.backgroundPaint.setAlpha(wasAlpha2);
                }
            }
        }
    }

    protected int getBottomSheetWidth(boolean isPortrait, int width, int height) {
        return isPortrait ? width : (int) Math.max(width * 0.8f, Math.min(AndroidUtilities.dp(480.0f), width));
    }

    protected boolean shouldOverlayCameraViewOverNavBar() {
        return false;
    }

    public void setHideSystemVerticalInsets(boolean hideSystemVerticalInsets) {
        float[] fArr = new float[2];
        fArr[0] = this.hideSystemVerticalInsetsProgress;
        fArr[1] = hideSystemVerticalInsets ? 1.0f : 0.0f;
        ValueAnimator animator = ValueAnimator.ofFloat(fArr).setDuration(180L);
        animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ActionBar.BottomSheet$$ExternalSyntheticLambda1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                BottomSheet.this.m1416x3bf1c798(valueAnimator);
            }
        });
        animator.start();
    }

    /* renamed from: lambda$setHideSystemVerticalInsets$0$org-telegram-ui-ActionBar-BottomSheet */
    public /* synthetic */ void m1416x3bf1c798(ValueAnimator animation) {
        this.hideSystemVerticalInsetsProgress = ((Float) animation.getAnimatedValue()).floatValue();
        this.container.requestLayout();
        this.containerView.requestLayout();
    }

    public int getAdditionalMandatoryOffsets() {
        if (!this.calcMandatoryInsets) {
            return 0;
        }
        Insets insets = this.lastInsets.getSystemGestureInsets();
        if (this.keyboardVisible || !this.drawNavigationBar || insets == null) {
            return 0;
        }
        if (insets.left != 0 || insets.right != 0) {
            return insets.bottom;
        }
        return 0;
    }

    public void setCalcMandatoryInsets(boolean value) {
        this.calcMandatoryInsets = value;
        this.drawNavigationBar = value;
    }

    /* loaded from: classes4.dex */
    public static class BottomSheetDelegate implements BottomSheetDelegateInterface {
        @Override // org.telegram.ui.ActionBar.BottomSheet.BottomSheetDelegateInterface
        public void onOpenAnimationStart() {
        }

        @Override // org.telegram.ui.ActionBar.BottomSheet.BottomSheetDelegateInterface
        public void onOpenAnimationEnd() {
        }

        @Override // org.telegram.ui.ActionBar.BottomSheet.BottomSheetDelegateInterface
        public boolean canDismiss() {
            return true;
        }
    }

    /* loaded from: classes4.dex */
    public static class BottomSheetCell extends FrameLayout {
        int currentType;
        private ImageView imageView;
        public boolean isSelected;
        private final Theme.ResourcesProvider resourcesProvider;
        private TextView textView;

        public BottomSheetCell(Context context, int type) {
            this(context, type, null);
        }

        public BottomSheetCell(Context context, int type, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.isSelected = false;
            this.resourcesProvider = resourcesProvider;
            this.currentType = type;
            setBackgroundDrawable(Theme.getSelectorDrawable(false));
            ImageView imageView = new ImageView(context);
            this.imageView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_dialogIcon), PorterDuff.Mode.MULTIPLY));
            int i = 5;
            addView(this.imageView, LayoutHelper.createFrame(56, 48, (LocaleController.isRTL ? 5 : 3) | 16));
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            if (type != 0) {
                if (type == 1) {
                    this.textView.setGravity(17);
                    this.textView.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
                    this.textView.setTextSize(1, 14.0f);
                    this.textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                    addView(this.textView, LayoutHelper.createFrame(-1, -1.0f));
                    return;
                } else if (type == 2) {
                    this.textView.setGravity(17);
                    this.textView.setTextColor(getThemedColor(Theme.key_featuredStickers_buttonText));
                    this.textView.setTextSize(1, 14.0f);
                    this.textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                    this.textView.setBackground(Theme.AdaptiveRipple.filledRect(getThemedColor(Theme.key_featuredStickers_addButton), 4.0f));
                    addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, 0, 16.0f, 16.0f, 16.0f, 16.0f));
                    return;
                } else {
                    return;
                }
            }
            this.textView.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
            this.textView.setTextSize(1, 16.0f);
            addView(this.textView, LayoutHelper.createFrame(-2, -2, (!LocaleController.isRTL ? 3 : i) | 16));
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int i = this.currentType;
            int height = i == 2 ? 80 : 48;
            if (i == 0) {
                widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED);
            }
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(height), C.BUFFER_FLAG_ENCRYPTED));
        }

        public void setTextColor(int color) {
            this.textView.setTextColor(color);
        }

        public void setIconColor(int color) {
            this.imageView.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        }

        public void setGravity(int gravity) {
            this.textView.setGravity(gravity);
        }

        public void setTextAndIcon(CharSequence text, int icon) {
            setTextAndIcon(text, icon, null, false);
        }

        public void setTextAndIcon(CharSequence text, Drawable icon) {
            setTextAndIcon(text, 0, icon, false);
        }

        public void setTextAndIcon(CharSequence text, int icon, Drawable drawable, boolean bigTitle) {
            this.textView.setText(text);
            float f = 21.0f;
            float f2 = 16.0f;
            if (icon != 0 || drawable != null) {
                if (drawable != null) {
                    this.imageView.setImageDrawable(drawable);
                } else {
                    this.imageView.setImageResource(icon);
                }
                this.imageView.setVisibility(0);
                if (bigTitle) {
                    TextView textView = this.textView;
                    int dp = AndroidUtilities.dp(LocaleController.isRTL ? 21.0f : 72.0f);
                    if (LocaleController.isRTL) {
                        f = 72.0f;
                    }
                    textView.setPadding(dp, 0, AndroidUtilities.dp(f), 0);
                    this.imageView.setPadding(LocaleController.isRTL ? 0 : AndroidUtilities.dp(5.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(5.0f) : 5, 0);
                    return;
                }
                TextView textView2 = this.textView;
                int dp2 = AndroidUtilities.dp(LocaleController.isRTL ? 16.0f : 72.0f);
                if (LocaleController.isRTL) {
                    f2 = 72.0f;
                }
                textView2.setPadding(dp2, 0, AndroidUtilities.dp(f2), 0);
                this.imageView.setPadding(0, 0, 0, 0);
                return;
            }
            this.imageView.setVisibility(4);
            TextView textView3 = this.textView;
            int dp3 = AndroidUtilities.dp(bigTitle ? 21.0f : 16.0f);
            if (!bigTitle) {
                f = 16.0f;
            }
            textView3.setPadding(dp3, 0, AndroidUtilities.dp(f), 0);
        }

        public TextView getTextView() {
            return this.textView;
        }

        public ImageView getImageView() {
            return this.imageView;
        }

        private int getThemedColor(String key) {
            Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
            Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
            return color != null ? color.intValue() : Theme.getColor(key);
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            if (this.isSelected) {
                info.setSelected(true);
            }
        }
    }

    public void setAllowNestedScroll(boolean value) {
        this.allowNestedScroll = value;
        if (!value) {
            this.containerView.setTranslationY(0.0f);
        }
    }

    public BottomSheet(Context context, boolean needFocus) {
        this(context, needFocus, null);
    }

    public BottomSheet(Context context, boolean needFocus, Theme.ResourcesProvider resourcesProvider) {
        super(context, R.style.TransparentDialog);
        this.currentAccount = UserConfig.selectedAccount;
        this.allowDrawContent = true;
        this.useHardwareLayer = true;
        this.backDrawable = new ColorDrawable(-16777216) { // from class: org.telegram.ui.ActionBar.BottomSheet.1
            @Override // android.graphics.drawable.ColorDrawable, android.graphics.drawable.Drawable
            public void setAlpha(int alpha) {
                super.setAlpha(alpha);
                BottomSheet.this.container.invalidate();
            }
        };
        this.useLightStatusBar = true;
        this.behindKeyboardColorKey = Theme.key_dialogBackground;
        this.canDismissWithSwipe = true;
        this.allowCustomAnimation = true;
        this.statusBarHeight = AndroidUtilities.statusBarHeight;
        this.openInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        this.dimBehind = true;
        this.dimBehindAlpha = 51;
        this.allowNestedScroll = true;
        this.applyTopPadding = true;
        this.applyBottomPadding = true;
        this.itemViews = new ArrayList<>();
        this.dismissRunnable = new Runnable() { // from class: org.telegram.ui.ActionBar.BottomSheet$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                BottomSheet.this.dismiss();
            }
        };
        this.navigationBarAlpha = 0.0f;
        this.navBarColorKey = Theme.key_windowBackgroundGray;
        this.useBackgroundTopPadding = true;
        this.resourcesProvider = resourcesProvider;
        if (Build.VERSION.SDK_INT >= 30) {
            getWindow().addFlags(-2147483392);
        } else if (Build.VERSION.SDK_INT >= 21) {
            getWindow().addFlags(-2147417856);
        }
        ViewConfiguration vc = ViewConfiguration.get(context);
        this.touchSlop = vc.getScaledTouchSlop();
        Rect padding = new Rect();
        Drawable mutate = context.getResources().getDrawable(R.drawable.sheet_shadow_round).mutate();
        this.shadowDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_dialogBackground), PorterDuff.Mode.MULTIPLY));
        this.shadowDrawable.getPadding(padding);
        this.backgroundPaddingLeft = padding.left;
        this.backgroundPaddingTop = padding.top;
        ContainerView containerView = new ContainerView(getContext()) { // from class: org.telegram.ui.ActionBar.BottomSheet.2
            @Override // org.telegram.ui.ActionBar.BottomSheet.ContainerView, android.view.ViewGroup
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                try {
                    if (BottomSheet.this.allowDrawContent) {
                        if (super.drawChild(canvas, child, drawingTime)) {
                            return true;
                        }
                    }
                    return false;
                } catch (Exception e) {
                    FileLog.e(e);
                    return true;
                }
            }

            @Override // org.telegram.ui.ActionBar.BottomSheet.ContainerView, android.view.ViewGroup, android.view.View
            protected void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                BottomSheet.this.mainContainerDispatchDraw(canvas);
            }
        };
        this.container = containerView;
        containerView.setBackgroundDrawable(this.backDrawable);
        this.focusable = needFocus;
        if (Build.VERSION.SDK_INT >= 21) {
            this.container.setFitsSystemWindows(true);
            this.container.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() { // from class: org.telegram.ui.ActionBar.BottomSheet$$ExternalSyntheticLambda3
                @Override // android.view.View.OnApplyWindowInsetsListener
                public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                    return BottomSheet.this.m1414lambda$new$1$orgtelegramuiActionBarBottomSheet(view, windowInsets);
                }
            });
            if (Build.VERSION.SDK_INT >= 30) {
                this.container.setSystemUiVisibility(1792);
            } else {
                this.container.setSystemUiVisibility(1280);
            }
        }
        this.backDrawable.setAlpha(0);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-ActionBar-BottomSheet */
    public /* synthetic */ WindowInsets m1414lambda$new$1$orgtelegramuiActionBarBottomSheet(View v, WindowInsets insets) {
        int newTopInset = insets.getSystemWindowInsetTop();
        if ((newTopInset != 0 || AndroidUtilities.isInMultiwindow) && this.statusBarHeight != newTopInset) {
            this.statusBarHeight = newTopInset;
        }
        this.lastInsets = insets;
        v.requestLayout();
        if (Build.VERSION.SDK_INT >= 30) {
            return WindowInsets.CONSUMED;
        }
        return insets.consumeSystemWindowInsets();
    }

    public void mainContainerDispatchDraw(Canvas canvas) {
    }

    public void fixNavigationBar() {
        fixNavigationBar(getThemedColor(Theme.key_dialogBackground));
    }

    public void fixNavigationBar(int bgColor) {
        this.drawNavigationBar = true;
        this.drawDoubleNavigationBar = true;
        this.scrollNavBar = true;
        this.navBarColorKey = null;
        this.navBarColor = bgColor;
        setOverlayNavBarColor(bgColor);
    }

    @Override // android.app.Dialog
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setWindowAnimations(R.style.DialogNoAnimation);
        setContentView(this.container, new ViewGroup.LayoutParams(-1, -1));
        Drawable drawable = null;
        if (this.useLightStatusBar && Build.VERSION.SDK_INT >= 23) {
            int color = Theme.getColor(Theme.key_actionBarDefault, null, true);
            if (color == -1) {
                int flags = this.container.getSystemUiVisibility();
                this.container.setSystemUiVisibility(flags | 8192);
            }
        }
        if (this.useLightNavBar && Build.VERSION.SDK_INT >= 26) {
            AndroidUtilities.setLightNavigationBar(getWindow(), false);
        }
        if (this.containerView == null) {
            FrameLayout frameLayout = new FrameLayout(getContext()) { // from class: org.telegram.ui.ActionBar.BottomSheet.3
                @Override // android.view.View
                public boolean hasOverlappingRendering() {
                    return false;
                }

                @Override // android.view.View
                public void setTranslationY(float translationY) {
                    super.setTranslationY(translationY);
                    BottomSheet.this.onContainerTranslationYChanged(translationY);
                }
            };
            this.containerView = frameLayout;
            frameLayout.setBackgroundDrawable(this.shadowDrawable);
            this.containerView.setPadding(this.backgroundPaddingLeft, ((this.applyTopPadding ? AndroidUtilities.dp(8.0f) : 0) + this.backgroundPaddingTop) - 1, this.backgroundPaddingLeft, this.applyBottomPadding ? AndroidUtilities.dp(8.0f) : 0);
        }
        this.containerView.setVisibility(4);
        this.container.addView(this.containerView, 0, LayoutHelper.createFrame(-1, -2, 80));
        int topOffset = 0;
        if (this.title != null) {
            TextView textView = new TextView(getContext()) { // from class: org.telegram.ui.ActionBar.BottomSheet.4
                @Override // android.widget.TextView, android.view.View
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                    if (BottomSheet.this.multipleLinesTitle) {
                        int topOffset2 = getMeasuredHeight();
                        if (BottomSheet.this.customView != null) {
                            ((ViewGroup.MarginLayoutParams) BottomSheet.this.customView.getLayoutParams()).topMargin = topOffset2;
                        } else if (BottomSheet.this.containerView != null) {
                            for (int i = 1; i < BottomSheet.this.containerView.getChildCount(); i++) {
                                View child = BottomSheet.this.containerView.getChildAt(i);
                                if (child instanceof BottomSheetCell) {
                                    ((ViewGroup.MarginLayoutParams) child.getLayoutParams()).topMargin = topOffset2;
                                    topOffset2 += AndroidUtilities.dp(48.0f);
                                }
                            }
                        }
                    }
                }
            };
            this.titleView = textView;
            textView.setText(this.title);
            if (this.bigTitle) {
                this.titleView.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
                this.titleView.setTextSize(1, 20.0f);
                this.titleView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                this.titleView.setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(this.multipleLinesTitle ? 14.0f : 6.0f), AndroidUtilities.dp(21.0f), AndroidUtilities.dp(8.0f));
            } else {
                this.titleView.setTextColor(getThemedColor(Theme.key_dialogTextGray2));
                this.titleView.setTextSize(1, 16.0f);
                this.titleView.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(this.multipleLinesTitle ? 8.0f : 0.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(8.0f));
            }
            if (this.multipleLinesTitle) {
                this.titleView.setSingleLine(false);
                this.titleView.setMaxLines(5);
                this.titleView.setEllipsize(TextUtils.TruncateAt.END);
            } else {
                this.titleView.setLines(1);
                this.titleView.setSingleLine(true);
                this.titleView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            }
            this.titleView.setGravity(16);
            this.containerView.addView(this.titleView, LayoutHelper.createFrame(-1, this.multipleLinesTitle ? -2.0f : 48));
            this.titleView.setOnTouchListener(BottomSheet$$ExternalSyntheticLambda5.INSTANCE);
            topOffset = 0 + 48;
        }
        View view = this.customView;
        if (view != null) {
            if (view.getParent() != null) {
                ViewGroup viewGroup = (ViewGroup) this.customView.getParent();
                viewGroup.removeView(this.customView);
            }
            if (this.useBackgroundTopPadding) {
                this.containerView.addView(this.customView, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, topOffset, 0.0f, 0.0f));
            } else {
                this.containerView.setClipToPadding(false);
                this.containerView.setClipChildren(false);
                this.container.setClipToPadding(false);
                this.container.setClipChildren(false);
                this.containerView.addView(this.customView, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, (-this.backgroundPaddingTop) + topOffset, 0.0f, 0.0f));
            }
        } else if (this.items != null) {
            int a = 0;
            while (true) {
                CharSequence[] charSequenceArr = this.items;
                if (a >= charSequenceArr.length) {
                    break;
                }
                if (charSequenceArr[a] != null) {
                    BottomSheetCell cell = new BottomSheetCell(getContext(), 0, this.resourcesProvider);
                    CharSequence charSequence = this.items[a];
                    int[] iArr = this.itemIcons;
                    cell.setTextAndIcon(charSequence, iArr != null ? iArr[a] : 0, drawable, this.bigTitle);
                    this.containerView.addView(cell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, topOffset, 0.0f, 0.0f));
                    topOffset += 48;
                    cell.setTag(Integer.valueOf(a));
                    cell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ActionBar.BottomSheet$$ExternalSyntheticLambda4
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view2) {
                            BottomSheet.this.m1415lambda$onCreate$3$orgtelegramuiActionBarBottomSheet(view2);
                        }
                    });
                    this.itemViews.add(cell);
                }
                a++;
                drawable = null;
            }
        }
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = -1;
        params.gravity = 51;
        params.dimAmount = 0.0f;
        params.flags &= -3;
        if (this.focusable) {
            params.softInputMode = 16;
        } else {
            params.flags |= 131072;
        }
        if (this.isFullscreen) {
            if (Build.VERSION.SDK_INT >= 21) {
                params.flags |= -2147417856;
            }
            params.flags |= 1024;
            this.container.setSystemUiVisibility(1284);
        }
        params.height = -1;
        if (Build.VERSION.SDK_INT >= 28) {
            params.layoutInDisplayCutoutMode = 1;
        }
        window.setAttributes(params);
    }

    public static /* synthetic */ boolean lambda$onCreate$2(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$onCreate$3$org-telegram-ui-ActionBar-BottomSheet */
    public /* synthetic */ void m1415lambda$onCreate$3$orgtelegramuiActionBarBottomSheet(View v) {
        dismissWithButtonClick(((Integer) v.getTag()).intValue());
    }

    @Override // android.app.Dialog
    public void onStart() {
        super.onStart();
    }

    public void setUseLightStatusBar(boolean value) {
        int flags;
        this.useLightStatusBar = value;
        if (Build.VERSION.SDK_INT >= 23) {
            int color = Theme.getColor(Theme.key_actionBarDefault, null, true);
            int flags2 = this.container.getSystemUiVisibility();
            if (this.useLightStatusBar && color == -1) {
                flags = flags2 | 8192;
            } else {
                flags = flags2 & (-8193);
            }
            this.container.setSystemUiVisibility(flags);
        }
    }

    public boolean isFocusable() {
        return this.focusable;
    }

    public void setFocusable(boolean value) {
        if (this.focusable == value) {
            return;
        }
        this.focusable = value;
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        if (this.focusable) {
            params.softInputMode = 16;
            params.flags &= -131073;
        } else {
            params.softInputMode = 48;
            params.flags |= 131072;
        }
        window.setAttributes(params);
    }

    public void setShowWithoutAnimation(boolean value) {
        this.showWithoutAnimation = value;
    }

    public void setBackgroundColor(int color) {
        this.shadowDrawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    }

    @Override // android.app.Dialog
    public void show() {
        super.show();
        if (this.focusable) {
            getWindow().setSoftInputMode(16);
        }
        int i = 0;
        this.dismissed = false;
        cancelSheetAnimation();
        this.containerView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x + (this.backgroundPaddingLeft * 2), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y, Integer.MIN_VALUE));
        float f = 0.0f;
        if (!this.showWithoutAnimation) {
            this.backDrawable.setAlpha(0);
            if (Build.VERSION.SDK_INT >= 18) {
                this.layoutCount = 2;
                ViewGroup viewGroup = this.containerView;
                if (Build.VERSION.SDK_INT >= 21) {
                    f = AndroidUtilities.statusBarHeight * (1.0f - this.hideSystemVerticalInsetsProgress);
                }
                float measuredHeight = f + this.containerView.getMeasuredHeight();
                if (this.scrollNavBar) {
                    i = getBottomInset();
                }
                viewGroup.setTranslationY(measuredHeight + i);
                Runnable runnable = new Runnable() { // from class: org.telegram.ui.ActionBar.BottomSheet.5
                    @Override // java.lang.Runnable
                    public void run() {
                        if (BottomSheet.this.startAnimationRunnable != this || BottomSheet.this.dismissed) {
                            return;
                        }
                        BottomSheet.this.startAnimationRunnable = null;
                        BottomSheet.this.startOpenAnimation();
                    }
                };
                this.startAnimationRunnable = runnable;
                AndroidUtilities.runOnUIThread(runnable, this.openNoDelay ? 0L : 150L);
                return;
            }
            startOpenAnimation();
            return;
        }
        ColorDrawable colorDrawable = this.backDrawable;
        if (this.dimBehind) {
            i = this.dimBehindAlpha;
        }
        colorDrawable.setAlpha(i);
        this.containerView.setTranslationY(0.0f);
    }

    public ColorDrawable getBackDrawable() {
        return this.backDrawable;
    }

    public int getBackgroundPaddingTop() {
        return this.backgroundPaddingTop;
    }

    public void setAllowDrawContent(boolean value) {
        if (this.allowDrawContent != value) {
            this.allowDrawContent = value;
            this.container.setBackgroundDrawable(value ? this.backDrawable : null);
            this.container.invalidate();
        }
    }

    protected boolean canDismissWithSwipe() {
        return this.canDismissWithSwipe;
    }

    public void setCanDismissWithSwipe(boolean value) {
        this.canDismissWithSwipe = value;
    }

    protected boolean onContainerTouchEvent(MotionEvent event) {
        return false;
    }

    protected boolean onScrollUp(float translationY) {
        return false;
    }

    protected void onScrollUpEnd(float translationY) {
    }

    protected void onScrollUpBegin(float translationY) {
    }

    public void setCustomView(View view) {
        this.customView = view;
    }

    @Override // android.app.Dialog
    public void setTitle(CharSequence value) {
        setTitle(value, false);
    }

    public void setTitle(CharSequence value, boolean big) {
        this.title = value;
        this.bigTitle = big;
    }

    public void setApplyTopPadding(boolean value) {
        this.applyTopPadding = value;
    }

    public void setApplyBottomPadding(boolean value) {
        this.applyBottomPadding = value;
    }

    protected boolean onCustomMeasure(View view, int width, int height) {
        return false;
    }

    protected boolean onCustomLayout(View view, int left, int top, int right, int bottom) {
        return false;
    }

    protected boolean canDismissWithTouchOutside() {
        return true;
    }

    public TextView getTitleView() {
        return this.titleView;
    }

    public void onContainerTranslationYChanged(float translationY) {
    }

    protected void cancelSheetAnimation() {
        AnimatorSet animatorSet = this.currentSheetAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.currentSheetAnimation = null;
        }
        this.currentSheetAnimationType = 0;
    }

    public void setOnHideListener(DialogInterface.OnDismissListener listener) {
        this.onHideListener = listener;
    }

    protected int getTargetOpenTranslationY() {
        return 0;
    }

    public void startOpenAnimation() {
        if (this.dismissed) {
            return;
        }
        this.containerView.setVisibility(0);
        if (!onCustomOpenAnimation()) {
            if (Build.VERSION.SDK_INT >= 20 && this.useHardwareLayer) {
                this.container.setLayerType(2, null);
            }
            this.containerView.setTranslationY(getContainerViewHeight() + this.container.keyboardHeight + AndroidUtilities.dp(10.0f) + (this.scrollNavBar ? getBottomInset() : 0));
            this.currentSheetAnimationType = 1;
            ValueAnimator valueAnimator = this.navigationBarAnimation;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(this.navigationBarAlpha, 1.0f);
            this.navigationBarAnimation = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ActionBar.BottomSheet$$ExternalSyntheticLambda2
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    BottomSheet.this.m1417x1fb6af0f(valueAnimator2);
                }
            });
            AnimatorSet animatorSet = new AnimatorSet();
            this.currentSheetAnimation = animatorSet;
            Animator[] animatorArr = new Animator[3];
            animatorArr[0] = ObjectAnimator.ofFloat(this.containerView, View.TRANSLATION_Y, 0.0f);
            ColorDrawable colorDrawable = this.backDrawable;
            Property<ColorDrawable, Integer> property = AnimationProperties.COLOR_DRAWABLE_ALPHA;
            int[] iArr = new int[1];
            iArr[0] = this.dimBehind ? this.dimBehindAlpha : 0;
            animatorArr[1] = ObjectAnimator.ofInt(colorDrawable, property, iArr);
            animatorArr[2] = this.navigationBarAnimation;
            animatorSet.playTogether(animatorArr);
            this.currentSheetAnimation.setDuration(400L);
            this.currentSheetAnimation.setStartDelay(20L);
            this.currentSheetAnimation.setInterpolator(this.openInterpolator);
            this.currentSheetAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.BottomSheet.6
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animation)) {
                        BottomSheet.this.currentSheetAnimation = null;
                        BottomSheet.this.currentSheetAnimationType = 0;
                        if (BottomSheet.this.delegate != null) {
                            BottomSheet.this.delegate.onOpenAnimationEnd();
                        }
                        if (BottomSheet.this.useHardwareLayer) {
                            BottomSheet.this.container.setLayerType(0, null);
                        }
                        if (BottomSheet.this.isFullscreen) {
                            WindowManager.LayoutParams params = BottomSheet.this.getWindow().getAttributes();
                            params.flags &= -1025;
                            BottomSheet.this.getWindow().setAttributes(params);
                        }
                    }
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animation)) {
                        BottomSheet.this.currentSheetAnimation = null;
                        BottomSheet.this.currentSheetAnimationType = 0;
                    }
                }
            });
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 512);
            this.currentSheetAnimation.start();
        }
    }

    /* renamed from: lambda$startOpenAnimation$4$org-telegram-ui-ActionBar-BottomSheet */
    public /* synthetic */ void m1417x1fb6af0f(ValueAnimator a) {
        this.navigationBarAlpha = ((Float) a.getAnimatedValue()).floatValue();
        ContainerView containerView = this.container;
        if (containerView != null) {
            containerView.invalidate();
        }
    }

    public void setDelegate(BottomSheetDelegateInterface bottomSheetDelegate) {
        this.delegate = bottomSheetDelegate;
    }

    public FrameLayout getContainer() {
        return this.container;
    }

    public ViewGroup getSheetContainer() {
        return this.containerView;
    }

    public int getTag() {
        return this.tag;
    }

    public void setDimBehind(boolean value) {
        this.dimBehind = value;
    }

    public void setDimBehindAlpha(int value) {
        this.dimBehindAlpha = value;
    }

    public void setItemText(int item, CharSequence text) {
        if (item < 0 || item >= this.itemViews.size()) {
            return;
        }
        BottomSheetCell cell = this.itemViews.get(item);
        cell.textView.setText(text);
    }

    public void setItemColor(int item, int color, int icon) {
        if (item < 0 || item >= this.itemViews.size()) {
            return;
        }
        BottomSheetCell cell = this.itemViews.get(item);
        cell.textView.setTextColor(color);
        cell.imageView.setColorFilter(new PorterDuffColorFilter(icon, PorterDuff.Mode.MULTIPLY));
    }

    public ArrayList<BottomSheetCell> getItemViews() {
        return this.itemViews;
    }

    public void setItems(CharSequence[] i, int[] icons, DialogInterface.OnClickListener listener) {
        this.items = i;
        this.itemIcons = icons;
        this.onClickListener = listener;
    }

    public void setTitleColor(int color) {
        TextView textView = this.titleView;
        if (textView == null) {
            return;
        }
        textView.setTextColor(color);
    }

    public boolean isDismissed() {
        return this.dismissed;
    }

    public void dismissWithButtonClick(int item) {
        if (this.dismissed) {
            return;
        }
        this.dismissed = true;
        cancelSheetAnimation();
        this.currentSheetAnimationType = 2;
        AnimatorSet animatorSet = new AnimatorSet();
        this.currentSheetAnimation = animatorSet;
        Animator[] animatorArr = new Animator[2];
        ViewGroup viewGroup = this.containerView;
        Property property = View.TRANSLATION_Y;
        float[] fArr = new float[1];
        fArr[0] = getContainerViewHeight() + this.container.keyboardHeight + AndroidUtilities.dp(10.0f) + (this.scrollNavBar ? getBottomInset() : 0);
        animatorArr[0] = ObjectAnimator.ofFloat(viewGroup, property, fArr);
        animatorArr[1] = ObjectAnimator.ofInt(this.backDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, 0);
        animatorSet.playTogether(animatorArr);
        this.currentSheetAnimation.setDuration(180L);
        this.currentSheetAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.currentSheetAnimation.addListener(new AnonymousClass7(item));
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 512);
        this.currentSheetAnimation.start();
    }

    /* renamed from: org.telegram.ui.ActionBar.BottomSheet$7 */
    /* loaded from: classes4.dex */
    public class AnonymousClass7 extends AnimatorListenerAdapter {
        final /* synthetic */ int val$item;

        AnonymousClass7(int i) {
            BottomSheet.this = this$0;
            this.val$item = i;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animation)) {
                BottomSheet.this.currentSheetAnimation = null;
                BottomSheet.this.currentSheetAnimationType = 0;
                if (BottomSheet.this.onClickListener != null) {
                    BottomSheet.this.onClickListener.onClick(BottomSheet.this, this.val$item);
                }
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ActionBar.BottomSheet$7$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        BottomSheet.AnonymousClass7.this.m1418lambda$onAnimationEnd$0$orgtelegramuiActionBarBottomSheet$7();
                    }
                });
            }
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
        }

        /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-ActionBar-BottomSheet$7 */
        public /* synthetic */ void m1418lambda$onAnimationEnd$0$orgtelegramuiActionBarBottomSheet$7() {
            if (BottomSheet.this.onHideListener != null) {
                BottomSheet.this.onHideListener.onDismiss(BottomSheet.this);
            }
            try {
                BottomSheet.this.dismissInternal();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animation) {
            if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animation)) {
                BottomSheet.this.currentSheetAnimation = null;
                BottomSheet.this.currentSheetAnimationType = 0;
            }
        }
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (this.dismissed) {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void onDismissAnimationStart() {
    }

    public int getContainerViewHeight() {
        ViewGroup viewGroup = this.containerView;
        if (viewGroup == null) {
            return 0;
        }
        return viewGroup.getMeasuredHeight();
    }

    @Override // android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        BottomSheetDelegateInterface bottomSheetDelegateInterface = this.delegate;
        if ((bottomSheetDelegateInterface != null && !bottomSheetDelegateInterface.canDismiss()) || this.dismissed) {
            return;
        }
        this.dismissed = true;
        DialogInterface.OnDismissListener onDismissListener = this.onHideListener;
        if (onDismissListener != null) {
            onDismissListener.onDismiss(this);
        }
        cancelSheetAnimation();
        long duration = 0;
        onDismissAnimationStart();
        if (!this.allowCustomAnimation || !onCustomCloseAnimation()) {
            this.currentSheetAnimationType = 2;
            this.currentSheetAnimation = new AnimatorSet();
            ValueAnimator valueAnimator = this.navigationBarAnimation;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(this.navigationBarAlpha, 0.0f);
            this.navigationBarAnimation = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ActionBar.BottomSheet$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    BottomSheet.this.m1413lambda$dismiss$5$orgtelegramuiActionBarBottomSheet(valueAnimator2);
                }
            });
            AnimatorSet animatorSet = this.currentSheetAnimation;
            Animator[] animatorArr = new Animator[3];
            ViewGroup viewGroup = this.containerView;
            Property property = View.TRANSLATION_Y;
            float[] fArr = new float[1];
            fArr[0] = getContainerViewHeight() + this.container.keyboardHeight + AndroidUtilities.dp(10.0f) + (this.scrollNavBar ? getBottomInset() : 0);
            animatorArr[0] = ObjectAnimator.ofFloat(viewGroup, property, fArr);
            animatorArr[1] = ObjectAnimator.ofInt(this.backDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, 0);
            animatorArr[2] = this.navigationBarAnimation;
            animatorSet.playTogether(animatorArr);
            duration = 250;
            this.currentSheetAnimation.setDuration(250L);
            this.currentSheetAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            this.currentSheetAnimation.addListener(new AnonymousClass8());
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 512);
            this.currentSheetAnimation.start();
        }
        Bulletin bulletin = Bulletin.getVisibleBulletin();
        if (bulletin != null && bulletin.isShowing()) {
            if (duration > 0) {
                bulletin.hide(((float) duration) * 0.6f);
            } else {
                bulletin.hide();
            }
        }
    }

    /* renamed from: lambda$dismiss$5$org-telegram-ui-ActionBar-BottomSheet */
    public /* synthetic */ void m1413lambda$dismiss$5$orgtelegramuiActionBarBottomSheet(ValueAnimator a) {
        this.navigationBarAlpha = ((Float) a.getAnimatedValue()).floatValue();
        ContainerView containerView = this.container;
        if (containerView != null) {
            containerView.invalidate();
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.BottomSheet$8 */
    /* loaded from: classes4.dex */
    public class AnonymousClass8 extends AnimatorListenerAdapter {
        AnonymousClass8() {
            BottomSheet.this = this$0;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animation)) {
                BottomSheet.this.currentSheetAnimation = null;
                BottomSheet.this.currentSheetAnimationType = 0;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ActionBar.BottomSheet$8$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        BottomSheet.AnonymousClass8.this.m1419lambda$onAnimationEnd$0$orgtelegramuiActionBarBottomSheet$8();
                    }
                });
            }
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
        }

        /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-ActionBar-BottomSheet$8 */
        public /* synthetic */ void m1419lambda$onAnimationEnd$0$orgtelegramuiActionBarBottomSheet$8() {
            try {
                BottomSheet.this.dismissInternal();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animation) {
            if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animation)) {
                BottomSheet.this.currentSheetAnimation = null;
                BottomSheet.this.currentSheetAnimationType = 0;
            }
        }
    }

    public int getSheetAnimationType() {
        return this.currentSheetAnimationType;
    }

    public void dismissInternal() {
        try {
            super.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    protected boolean onCustomCloseAnimation() {
        return false;
    }

    public boolean onCustomOpenAnimation() {
        return false;
    }

    /* loaded from: classes4.dex */
    public static class Builder {
        private BottomSheet bottomSheet;

        public Builder(Context context) {
            this(context, false);
        }

        public Builder(Context context, int bgColor) {
            this(context, false, null, bgColor);
        }

        public Builder(Context context, boolean needFocus) {
            this(context, needFocus, (Theme.ResourcesProvider) null);
        }

        public Builder(Context context, boolean needFocus, int bgColor) {
            this(context, needFocus, null, bgColor);
        }

        public Builder(Context context, boolean needFocus, Theme.ResourcesProvider resourcesProvider) {
            BottomSheet bottomSheet = new BottomSheet(context, needFocus, resourcesProvider);
            this.bottomSheet = bottomSheet;
            bottomSheet.fixNavigationBar();
        }

        public Builder(Context context, boolean needFocus, Theme.ResourcesProvider resourcesProvider, int bgColor) {
            BottomSheet bottomSheet = new BottomSheet(context, needFocus, resourcesProvider);
            this.bottomSheet = bottomSheet;
            bottomSheet.setBackgroundColor(bgColor);
            this.bottomSheet.fixNavigationBar(bgColor);
        }

        public Builder setItems(CharSequence[] items, DialogInterface.OnClickListener onClickListener) {
            this.bottomSheet.items = items;
            this.bottomSheet.onClickListener = onClickListener;
            return this;
        }

        public Builder setItems(CharSequence[] items, int[] icons, DialogInterface.OnClickListener onClickListener) {
            this.bottomSheet.items = items;
            this.bottomSheet.itemIcons = icons;
            this.bottomSheet.onClickListener = onClickListener;
            return this;
        }

        public Builder setCustomView(View view) {
            this.bottomSheet.customView = view;
            return this;
        }

        public View getCustomView() {
            return this.bottomSheet.customView;
        }

        public Builder setTitle(CharSequence title) {
            return setTitle(title, false);
        }

        public Builder setTitle(CharSequence title, boolean big) {
            this.bottomSheet.title = title;
            this.bottomSheet.bigTitle = big;
            return this;
        }

        public Builder setTitleMultipleLines(boolean allowMultipleLines) {
            this.bottomSheet.multipleLinesTitle = allowMultipleLines;
            return this;
        }

        public BottomSheet create() {
            return this.bottomSheet;
        }

        public BottomSheet setDimBehind(boolean value) {
            this.bottomSheet.dimBehind = value;
            return this.bottomSheet;
        }

        public BottomSheet show() {
            this.bottomSheet.show();
            return this.bottomSheet;
        }

        public Builder setTag(int tag) {
            this.bottomSheet.tag = tag;
            return this;
        }

        public Builder setUseHardwareLayer(boolean value) {
            this.bottomSheet.useHardwareLayer = value;
            return this;
        }

        public Builder setDelegate(BottomSheetDelegate delegate) {
            this.bottomSheet.setDelegate(delegate);
            return this;
        }

        public Builder setApplyTopPadding(boolean value) {
            this.bottomSheet.applyTopPadding = value;
            return this;
        }

        public Builder setApplyBottomPadding(boolean value) {
            this.bottomSheet.applyBottomPadding = value;
            return this;
        }

        public Runnable getDismissRunnable() {
            return this.bottomSheet.dismissRunnable;
        }

        public BottomSheet setUseFullWidth(boolean value) {
            this.bottomSheet.fullWidth = value;
            return this.bottomSheet;
        }

        public BottomSheet setUseFullscreen(boolean value) {
            this.bottomSheet.isFullscreen = value;
            return this.bottomSheet;
        }

        public Builder setOnPreDismissListener(DialogInterface.OnDismissListener onDismissListener) {
            this.bottomSheet.setOnHideListener(onDismissListener);
            return this;
        }
    }

    public int getLeftInset() {
        if (this.lastInsets != null && Build.VERSION.SDK_INT >= 21) {
            float inset = this.lastInsets.getSystemWindowInsetLeft() * (1.0f - this.hideSystemVerticalInsetsProgress);
            return (int) inset;
        }
        return 0;
    }

    public int getRightInset() {
        if (this.lastInsets != null && Build.VERSION.SDK_INT >= 21) {
            float inset = this.lastInsets.getSystemWindowInsetRight() * (1.0f - this.hideSystemVerticalInsetsProgress);
            return (int) inset;
        }
        return 0;
    }

    public int getStatusBarHeight() {
        return (int) (this.statusBarHeight * (1.0f - this.hideSystemVerticalInsetsProgress));
    }

    public int getBottomInset() {
        return (int) (this.bottomInset * (1.0f - this.hideSystemVerticalInsetsProgress));
    }

    public void onConfigurationChanged(Configuration newConfig) {
    }

    public void onContainerDraw(Canvas canvas) {
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return null;
    }

    public void setCurrentPanTranslationY(float currentPanTranslationY) {
        this.currentPanTranslationY = currentPanTranslationY;
        this.container.invalidate();
    }

    public void setOverlayNavBarColor(int color) {
        this.overlayDrawNavBarColor = color;
        ContainerView containerView = this.container;
        if (containerView != null) {
            containerView.invalidate();
        }
        AndroidUtilities.setNavigationBarColor(getWindow(), this.overlayDrawNavBarColor);
        AndroidUtilities.setLightNavigationBar(getWindow(), ((double) AndroidUtilities.computePerceivedBrightness(this.overlayDrawNavBarColor)) > 0.721d);
    }

    public ViewGroup getContainerView() {
        return this.containerView;
    }

    public int getCurrentAccount() {
        return this.currentAccount;
    }

    public int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    public void setOpenNoDelay(boolean openNoDelay) {
        this.openNoDelay = openNoDelay;
    }

    public int getBackgroundPaddingLeft() {
        return this.backgroundPaddingLeft;
    }
}
