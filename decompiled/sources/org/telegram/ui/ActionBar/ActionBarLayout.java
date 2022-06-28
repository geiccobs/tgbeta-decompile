package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import com.google.android.exoplayer2.C;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.BackButtonMenu;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.GroupCallPip;
import org.telegram.ui.Components.LayoutHelper;
/* loaded from: classes4.dex */
public class ActionBarLayout extends FrameLayout {
    private static Drawable headerShadowDrawable;
    private static Drawable layerShadowDrawable;
    private static Paint scrimPaint;
    private int animateSetThemeAccentIdAfterAnimation;
    private Theme.ThemeInfo animateSetThemeAfterAnimation;
    private boolean animateSetThemeNightAfterAnimation;
    private boolean animateThemeAfterAnimation;
    protected boolean animationInProgress;
    private float animationProgress;
    public ThemeAnimationSettings.onAnimationProgress animationProgressListener;
    private Runnable animationRunnable;
    private View backgroundView;
    private boolean beginTrackingSent;
    public LayoutContainer containerView;
    private LayoutContainer containerViewBack;
    private ActionBar currentActionBar;
    private AnimatorSet currentAnimation;
    private boolean delayedAnimationResumed;
    private Runnable delayedOpenAnimationRunnable;
    private ActionBarLayoutDelegate delegate;
    private DrawerLayoutContainer drawerLayoutContainer;
    public ArrayList<BaseFragment> fragmentsStack;
    private boolean inActionMode;
    private boolean inBubbleMode;
    private boolean inPreviewMode;
    public float innerTranslationX;
    private long lastFrameTime;
    private View layoutToIgnore;
    private boolean maybeStartTracking;
    public Theme.MessageDrawable messageDrawableOutMediaStart;
    public Theme.MessageDrawable messageDrawableOutStart;
    private BaseFragment newFragment;
    private BaseFragment oldFragment;
    private Runnable onCloseAnimationEndRunnable;
    private Runnable onFragmentStackChangedListener;
    private Runnable onOpenAnimationEndRunnable;
    private Runnable overlayAction;
    protected Activity parentActivity;
    private ArrayList<ThemeDescription> presentingFragmentDescriptions;
    private ColorDrawable previewBackgroundDrawable;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout previewMenu;
    private boolean previewOpenAnimationInProgress;
    public ArrayList<BackButtonMenu.PulledDialog> pulledDialogs;
    private boolean rebuildAfterAnimation;
    private boolean rebuildLastAfterAnimation;
    private boolean removeActionBarExtraHeight;
    private boolean showLastAfterAnimation;
    protected boolean startedTracking;
    private int startedTrackingPointerId;
    private int startedTrackingX;
    private int startedTrackingY;
    private float themeAnimationValue;
    private AnimatorSet themeAnimatorSet;
    private String titleOverlayText;
    private int titleOverlayTextId;
    private boolean transitionAnimationInProgress;
    private boolean transitionAnimationPreviewMode;
    private long transitionAnimationStartTime;
    private boolean useAlphaAnimations;
    private VelocityTracker velocityTracker;
    private Runnable waitingForKeyboardCloseRunnable;
    public boolean highlightActionButtons = false;
    private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator(1.5f);
    private OvershootInterpolator overshootInterpolator = new OvershootInterpolator(1.02f);
    private AccelerateDecelerateInterpolator accelerateDecelerateInterpolator = new AccelerateDecelerateInterpolator();
    private ArrayList<int[]> animateStartColors = new ArrayList<>();
    private ArrayList<int[]> animateEndColors = new ArrayList<>();
    StartColorsProvider startColorsProvider = new StartColorsProvider();
    private ArrayList<ArrayList<ThemeDescription>> themeAnimatorDescriptions = new ArrayList<>();
    private ArrayList<ThemeDescription.ThemeDescriptionDelegate> themeAnimatorDelegate = new ArrayList<>();
    private Rect rect = new Rect();
    private int overrideWidthOffset = -1;

    /* loaded from: classes4.dex */
    public interface ActionBarLayoutDelegate {
        boolean needAddFragmentToStack(BaseFragment baseFragment, ActionBarLayout actionBarLayout);

        boolean needCloseLastFragment(ActionBarLayout actionBarLayout);

        boolean needPresentFragment(BaseFragment baseFragment, boolean z, boolean z2, ActionBarLayout actionBarLayout);

        boolean onPreIme();

        void onRebuildAllFragments(ActionBarLayout actionBarLayout, boolean z);
    }

    static /* synthetic */ float access$1216(ActionBarLayout x0, float x1) {
        float f = x0.animationProgress + x1;
        x0.animationProgress = f;
        return f;
    }

    /* loaded from: classes4.dex */
    public class LayoutContainer extends FrameLayout {
        private boolean allowToPressByHover;
        private int backgroundColor;
        private int fragmentPanTranslationOffset;
        private boolean isKeyboardVisible;
        private float pressX;
        private float pressY;
        private boolean wasPortrait;
        private Rect rect = new Rect();
        private Paint backgroundPaint = new Paint();

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public LayoutContainer(Context context) {
            super(context);
            ActionBarLayout.this = this$0;
            setWillNotDraw(false);
        }

        @Override // android.view.ViewGroup
        protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
            if (child instanceof ActionBar) {
                return super.drawChild(canvas, child, drawingTime);
            }
            int actionBarHeight = 0;
            int actionBarY = 0;
            int childCount = getChildCount();
            int a = 0;
            while (true) {
                if (a >= childCount) {
                    break;
                }
                View view = getChildAt(a);
                if (view == child || !(view instanceof ActionBar) || view.getVisibility() != 0) {
                    a++;
                } else if (((ActionBar) view).getCastShadows()) {
                    actionBarHeight = view.getMeasuredHeight();
                    actionBarY = (int) view.getY();
                }
            }
            boolean result = super.drawChild(canvas, child, drawingTime);
            if (actionBarHeight != 0 && ActionBarLayout.headerShadowDrawable != null) {
                ActionBarLayout.headerShadowDrawable.setBounds(0, actionBarY + actionBarHeight, getMeasuredWidth(), actionBarY + actionBarHeight + ActionBarLayout.headerShadowDrawable.getIntrinsicHeight());
                ActionBarLayout.headerShadowDrawable.draw(canvas);
            }
            return result;
        }

        @Override // android.view.View
        public boolean hasOverlappingRendering() {
            if (Build.VERSION.SDK_INT >= 28) {
                return true;
            }
            return false;
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int actionBarHeight;
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = View.MeasureSpec.getSize(heightMeasureSpec);
            boolean isPortrait = height > width;
            if (this.wasPortrait != isPortrait && ActionBarLayout.this.isInPreviewMode()) {
                ActionBarLayout.this.finishPreviewFragment();
            }
            this.wasPortrait = isPortrait;
            int count = getChildCount();
            int a = 0;
            while (true) {
                if (a >= count) {
                    actionBarHeight = 0;
                    break;
                }
                View child = getChildAt(a);
                if (child instanceof ActionBar) {
                    child.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, 0));
                    int actionBarHeight2 = child.getMeasuredHeight();
                    actionBarHeight = actionBarHeight2;
                    break;
                }
                a++;
            }
            for (int a2 = 0; a2 < count; a2++) {
                View child2 = getChildAt(a2);
                if (!(child2 instanceof ActionBar)) {
                    if (child2.getFitsSystemWindows()) {
                        measureChildWithMargins(child2, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    } else {
                        measureChildWithMargins(child2, widthMeasureSpec, 0, heightMeasureSpec, actionBarHeight);
                    }
                }
            }
            setMeasuredDimension(width, height);
        }

        @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            boolean z;
            int count = getChildCount();
            int actionBarHeight = 0;
            int a = 0;
            while (true) {
                z = false;
                if (a >= count) {
                    break;
                }
                View child = getChildAt(a);
                if (!(child instanceof ActionBar)) {
                    a++;
                } else {
                    actionBarHeight = child.getMeasuredHeight();
                    child.layout(0, 0, child.getMeasuredWidth(), actionBarHeight);
                    break;
                }
            }
            for (int a2 = 0; a2 < count; a2++) {
                View child2 = getChildAt(a2);
                if (!(child2 instanceof ActionBar)) {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) child2.getLayoutParams();
                    if (child2.getFitsSystemWindows()) {
                        child2.layout(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.leftMargin + child2.getMeasuredWidth(), layoutParams.topMargin + child2.getMeasuredHeight());
                    } else {
                        child2.layout(layoutParams.leftMargin, layoutParams.topMargin + actionBarHeight, layoutParams.leftMargin + child2.getMeasuredWidth(), layoutParams.topMargin + actionBarHeight + child2.getMeasuredHeight());
                    }
                }
            }
            View rootView = getRootView();
            getWindowVisibleDisplayFrame(this.rect);
            int usableViewHeight = (rootView.getHeight() - (this.rect.top != 0 ? AndroidUtilities.statusBarHeight : 0)) - AndroidUtilities.getViewInset(rootView);
            if (usableViewHeight - (this.rect.bottom - this.rect.top) > 0) {
                z = true;
            }
            this.isKeyboardVisible = z;
            if (ActionBarLayout.this.waitingForKeyboardCloseRunnable != null && !ActionBarLayout.this.containerView.isKeyboardVisible && !ActionBarLayout.this.containerViewBack.isKeyboardVisible) {
                AndroidUtilities.cancelRunOnUIThread(ActionBarLayout.this.waitingForKeyboardCloseRunnable);
                ActionBarLayout.this.waitingForKeyboardCloseRunnable.run();
                ActionBarLayout.this.waitingForKeyboardCloseRunnable = null;
            }
        }

        /* JADX WARN: Code restructure failed: missing block: B:17:0x0036, code lost:
            if (r5 != org.telegram.ui.ActionBar.ActionBarLayout.this.containerView) goto L18;
         */
        @Override // android.view.ViewGroup, android.view.View
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public boolean dispatchTouchEvent(android.view.MotionEvent r6) {
            /*
                r5 = this;
                r5.processMenuButtonsTouch(r6)
                org.telegram.ui.ActionBar.ActionBarLayout r0 = org.telegram.ui.ActionBar.ActionBarLayout.this
                boolean r0 = org.telegram.ui.ActionBar.ActionBarLayout.access$300(r0)
                r1 = 1
                r2 = 0
                if (r0 == 0) goto L17
                org.telegram.ui.ActionBar.ActionBarLayout r0 = org.telegram.ui.ActionBar.ActionBarLayout.this
                org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r0 = org.telegram.ui.ActionBar.ActionBarLayout.access$400(r0)
                if (r0 != 0) goto L17
                r0 = 1
                goto L18
            L17:
                r0 = 0
            L18:
                if (r0 != 0) goto L22
                org.telegram.ui.ActionBar.ActionBarLayout r3 = org.telegram.ui.ActionBar.ActionBarLayout.this
                boolean r3 = org.telegram.ui.ActionBar.ActionBarLayout.access$500(r3)
                if (r3 == 0) goto L30
            L22:
                int r3 = r6.getActionMasked()
                if (r3 == 0) goto L46
                int r3 = r6.getActionMasked()
                r4 = 5
                if (r3 != r4) goto L30
                goto L46
            L30:
                if (r0 == 0) goto L38
                org.telegram.ui.ActionBar.ActionBarLayout r3 = org.telegram.ui.ActionBar.ActionBarLayout.this     // Catch: java.lang.Throwable -> L41
                org.telegram.ui.ActionBar.ActionBarLayout$LayoutContainer r3 = r3.containerView     // Catch: java.lang.Throwable -> L41
                if (r5 == r3) goto L3f
            L38:
                boolean r3 = super.dispatchTouchEvent(r6)     // Catch: java.lang.Throwable -> L41
                if (r3 == 0) goto L3f
                goto L40
            L3f:
                r1 = 0
            L40:
                return r1
            L41:
                r1 = move-exception
                org.telegram.messenger.FileLog.e(r1)
                return r2
            L46:
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.ActionBarLayout.LayoutContainer.dispatchTouchEvent(android.view.MotionEvent):boolean");
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.fragmentPanTranslationOffset != 0) {
                int color = Theme.getColor(Theme.key_windowBackgroundWhite);
                if (this.backgroundColor != color) {
                    Paint paint = this.backgroundPaint;
                    int color2 = Theme.getColor(Theme.key_windowBackgroundWhite);
                    this.backgroundColor = color2;
                    paint.setColor(color2);
                }
                canvas.drawRect(0.0f, (getMeasuredHeight() - this.fragmentPanTranslationOffset) - 3, getMeasuredWidth(), getMeasuredHeight(), this.backgroundPaint);
            }
            super.onDraw(canvas);
        }

        public void setFragmentPanTranslationOffset(int fragmentPanTranslationOffset) {
            this.fragmentPanTranslationOffset = fragmentPanTranslationOffset;
            invalidate();
        }

        public void processMenuButtonsTouch(MotionEvent event) {
            if (event.getAction() == 0) {
                this.pressX = event.getX();
                this.pressY = event.getY();
                this.allowToPressByHover = false;
            } else if ((event.getAction() == 2 || event.getAction() == 1) && ActionBarLayout.this.previewMenu != null && ActionBarLayout.this.highlightActionButtons) {
                if (!this.allowToPressByHover && Math.sqrt(Math.pow(this.pressX - event.getX(), 2.0d) + Math.pow(this.pressY - event.getY(), 2.0d)) > AndroidUtilities.dp(30.0f)) {
                    this.allowToPressByHover = true;
                }
                if (this.allowToPressByHover && (ActionBarLayout.this.previewMenu.getSwipeBack() == null || !ActionBarLayout.this.previewMenu.getSwipeBack().isForegroundOpen())) {
                    for (int i = 0; i < ActionBarLayout.this.previewMenu.getItemsCount(); i++) {
                        ActionBarMenuSubItem button = (ActionBarMenuSubItem) ActionBarLayout.this.previewMenu.getItemAt(i);
                        if (button != null) {
                            Drawable ripple = button.getBackground();
                            button.getGlobalVisibleRect(AndroidUtilities.rectTmp2);
                            boolean shouldBeEnabled = AndroidUtilities.rectTmp2.contains((int) event.getX(), (int) event.getY());
                            boolean enabled = ripple.getState().length == 2;
                            if (event.getAction() == 2) {
                                if (shouldBeEnabled != enabled) {
                                    ripple.setState(shouldBeEnabled ? new int[]{16842919, 16842910} : new int[0]);
                                    if (shouldBeEnabled) {
                                        try {
                                            button.performHapticFeedback(9, 1);
                                        } catch (Exception e) {
                                        }
                                    }
                                }
                            } else if (event.getAction() == 1 && shouldBeEnabled) {
                                button.performClick();
                            }
                        }
                    }
                }
            }
            int i2 = event.getAction();
            if (i2 == 1 || event.getAction() == 3) {
                if (ActionBarLayout.this.previewMenu != null && ActionBarLayout.this.highlightActionButtons) {
                    int alpha = 255;
                    if (Build.VERSION.SDK_INT >= 19) {
                        alpha = Theme.moveUpDrawable.getAlpha();
                    }
                    ValueAnimator arrowAlphaUpdate = ValueAnimator.ofFloat(alpha, 0.0f);
                    arrowAlphaUpdate.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ActionBar.ActionBarLayout$LayoutContainer$$ExternalSyntheticLambda0
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            ActionBarLayout.LayoutContainer.this.m1385xb0129fab(valueAnimator);
                        }
                    });
                    arrowAlphaUpdate.setDuration(150L);
                    arrowAlphaUpdate.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    arrowAlphaUpdate.start();
                    ObjectAnimator containerTranslationUpdate = ObjectAnimator.ofFloat(ActionBarLayout.this.containerView, View.TRANSLATION_Y, 0.0f);
                    containerTranslationUpdate.setDuration(150L);
                    containerTranslationUpdate.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    containerTranslationUpdate.start();
                }
                ActionBarLayout.this.highlightActionButtons = false;
            }
        }

        /* renamed from: lambda$processMenuButtonsTouch$0$org-telegram-ui-ActionBar-ActionBarLayout$LayoutContainer */
        public /* synthetic */ void m1385xb0129fab(ValueAnimator a) {
            Theme.moveUpDrawable.setAlpha(((Float) a.getAnimatedValue()).intValue());
            if (ActionBarLayout.this.drawerLayoutContainer != null) {
                ActionBarLayout.this.drawerLayoutContainer.invalidate();
            }
            if (ActionBarLayout.this.containerView != null) {
                ActionBarLayout.this.containerView.invalidate();
            }
            ActionBarLayout.this.invalidate();
        }
    }

    /* loaded from: classes4.dex */
    public static class ThemeAnimationSettings {
        public final int accentId;
        public Runnable afterAnimationRunnable;
        public Runnable afterStartDescriptionsAddedRunnable;
        public onAnimationProgress animationProgress;
        public Runnable beforeAnimationRunnable;
        public final boolean instant;
        public final boolean nightTheme;
        public boolean onlyTopFragment;
        public Theme.ResourcesProvider resourcesProvider;
        public final Theme.ThemeInfo theme;
        public boolean applyTheme = true;
        public long duration = 200;

        /* loaded from: classes4.dex */
        public interface onAnimationProgress {
            void setProgress(float f);
        }

        public ThemeAnimationSettings(Theme.ThemeInfo theme, int accentId, boolean nightTheme, boolean instant) {
            this.theme = theme;
            this.accentId = accentId;
            this.nightTheme = nightTheme;
            this.instant = instant;
        }
    }

    public ActionBarLayout(Context context) {
        super(context);
        this.parentActivity = (Activity) context;
        if (layerShadowDrawable == null) {
            layerShadowDrawable = getResources().getDrawable(R.drawable.layer_shadow);
            headerShadowDrawable = getResources().getDrawable(R.drawable.header_shadow).mutate();
            scrimPaint = new Paint();
        }
    }

    public void init(ArrayList<BaseFragment> stack) {
        this.fragmentsStack = stack;
        LayoutContainer layoutContainer = new LayoutContainer(this.parentActivity);
        this.containerViewBack = layoutContainer;
        addView(layoutContainer);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.containerViewBack.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.gravity = 51;
        this.containerViewBack.setLayoutParams(layoutParams);
        LayoutContainer layoutContainer2 = new LayoutContainer(this.parentActivity);
        this.containerView = layoutContainer2;
        addView(layoutContainer2);
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.containerView.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = -1;
        layoutParams2.gravity = 51;
        this.containerView.setLayoutParams(layoutParams2);
        Iterator<BaseFragment> it = this.fragmentsStack.iterator();
        while (it.hasNext()) {
            BaseFragment fragment = it.next();
            fragment.setParentLayout(this);
        }
    }

    @Override // android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!this.fragmentsStack.isEmpty()) {
            int N = this.fragmentsStack.size();
            for (int a = 0; a < N; a++) {
                BaseFragment fragment = this.fragmentsStack.get(a);
                fragment.onConfigurationChanged(newConfig);
                if (fragment.visibleDialog instanceof BottomSheet) {
                    ((BottomSheet) fragment.visibleDialog).onConfigurationChanged(newConfig);
                }
            }
        }
    }

    public void drawHeaderShadow(Canvas canvas, int y) {
        drawHeaderShadow(canvas, 255, y);
    }

    public void setInBubbleMode(boolean value) {
        this.inBubbleMode = value;
    }

    public boolean isInBubbleMode() {
        return this.inBubbleMode;
    }

    public void drawHeaderShadow(Canvas canvas, int alpha, int y) {
        if (headerShadowDrawable != null) {
            if (Build.VERSION.SDK_INT >= 19) {
                if (headerShadowDrawable.getAlpha() != alpha) {
                    headerShadowDrawable.setAlpha(alpha);
                }
            } else {
                headerShadowDrawable.setAlpha(alpha);
            }
            headerShadowDrawable.setBounds(0, y, getMeasuredWidth(), headerShadowDrawable.getIntrinsicHeight() + y);
            headerShadowDrawable.draw(canvas);
        }
    }

    public void setInnerTranslationX(float value) {
        int currNavigationBarColor;
        int prevNavigationBarColor;
        this.innerTranslationX = value;
        invalidate();
        if (this.fragmentsStack.size() >= 2 && this.containerView.getMeasuredWidth() > 0) {
            float progress = value / this.containerView.getMeasuredWidth();
            ArrayList<BaseFragment> arrayList = this.fragmentsStack;
            BaseFragment prevFragment = arrayList.get(arrayList.size() - 2);
            int newStatusBarColor = 0;
            prevFragment.onSlideProgress(false, progress);
            ArrayList<BaseFragment> arrayList2 = this.fragmentsStack;
            BaseFragment currFragment = arrayList2.get(arrayList2.size() - 1);
            float ratio = MathUtils.clamp(2.0f * progress, 0.0f, 1.0f);
            if (currFragment.isBeginToShow() && (currNavigationBarColor = currFragment.getNavigationBarColor()) != (prevNavigationBarColor = prevFragment.getNavigationBarColor())) {
                currFragment.setNavigationBarColor(ColorUtils.blendARGB(currNavigationBarColor, prevNavigationBarColor, ratio));
            }
            if (currFragment != null && !currFragment.inPreviewMode && Build.VERSION.SDK_INT >= 23 && !SharedConfig.noStatusBar) {
                int overlayColor = Theme.getColor(Theme.key_actionBarDefault) == -1 ? AndroidUtilities.LIGHT_STATUS_BAR_OVERLAY : AndroidUtilities.DARK_STATUS_BAR_OVERLAY;
                int oldStatusBarColor = (prevFragment == null || !prevFragment.hasForceLightStatusBar()) ? overlayColor : 0;
                if (currFragment == null || !currFragment.hasForceLightStatusBar()) {
                    newStatusBarColor = overlayColor;
                }
                this.parentActivity.getWindow().setStatusBarColor(ColorUtils.blendARGB(newStatusBarColor, oldStatusBarColor, ratio));
            }
        }
    }

    public float getInnerTranslationX() {
        return this.innerTranslationX;
    }

    public void dismissDialogs() {
        if (!this.fragmentsStack.isEmpty()) {
            ArrayList<BaseFragment> arrayList = this.fragmentsStack;
            BaseFragment lastFragment = arrayList.get(arrayList.size() - 1);
            lastFragment.dismissCurrentDialog();
        }
    }

    public void onResume() {
        if (this.transitionAnimationInProgress) {
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.currentAnimation = null;
            }
            Runnable runnable = this.animationRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.animationRunnable = null;
            }
            Runnable runnable2 = this.waitingForKeyboardCloseRunnable;
            if (runnable2 != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable2);
                this.waitingForKeyboardCloseRunnable = null;
            }
            if (this.onCloseAnimationEndRunnable != null) {
                onCloseAnimationEnd();
            } else if (this.onOpenAnimationEndRunnable != null) {
                onOpenAnimationEnd();
            }
        }
        if (!this.fragmentsStack.isEmpty()) {
            ArrayList<BaseFragment> arrayList = this.fragmentsStack;
            BaseFragment lastFragment = arrayList.get(arrayList.size() - 1);
            lastFragment.onResume();
        }
    }

    public void onUserLeaveHint() {
        if (!this.fragmentsStack.isEmpty()) {
            ArrayList<BaseFragment> arrayList = this.fragmentsStack;
            BaseFragment lastFragment = arrayList.get(arrayList.size() - 1);
            lastFragment.onUserLeaveHint();
        }
    }

    public void onPause() {
        if (!this.fragmentsStack.isEmpty()) {
            ArrayList<BaseFragment> arrayList = this.fragmentsStack;
            BaseFragment lastFragment = arrayList.get(arrayList.size() - 1);
            lastFragment.onPause();
        }
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return this.animationInProgress || checkTransitionAnimation() || onTouchEvent(ev);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        onTouchEvent(null);
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (event != null && event.getKeyCode() == 4 && event.getAction() == 1) {
            ActionBarLayoutDelegate actionBarLayoutDelegate = this.delegate;
            return (actionBarLayoutDelegate != null && actionBarLayoutDelegate.onPreIme()) || super.dispatchKeyEventPreIme(event);
        }
        return super.dispatchKeyEventPreIme(event);
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        int clipRight;
        int clipLeft;
        LayoutContainer layoutContainer;
        DrawerLayoutContainer drawerLayoutContainer = this.drawerLayoutContainer;
        if (drawerLayoutContainer != null && drawerLayoutContainer.isDrawCurrentPreviewFragmentAbove() && (this.inPreviewMode || this.transitionAnimationPreviewMode || this.previewOpenAnimationInProgress)) {
            BaseFragment baseFragment = this.oldFragment;
            if (child == ((baseFragment == null || !baseFragment.inPreviewMode) ? this.containerView : this.containerViewBack)) {
                this.drawerLayoutContainer.invalidate();
                return false;
            }
        }
        int width = (getWidth() - getPaddingLeft()) - getPaddingRight();
        int translationX = ((int) this.innerTranslationX) + getPaddingRight();
        int clipLeft2 = getPaddingLeft();
        int clipRight2 = getPaddingLeft() + width;
        if (child == this.containerViewBack) {
            clipLeft = clipLeft2;
            clipRight = translationX + AndroidUtilities.dp(1.0f);
        } else if (child != this.containerView) {
            clipLeft = clipLeft2;
            clipRight = clipRight2;
        } else {
            clipLeft = translationX;
            clipRight = clipRight2;
        }
        int restoreCount = canvas.save();
        if (!isTransitionAnimationInProgress() && !this.inPreviewMode) {
            canvas.clipRect(clipLeft, 0, clipRight, getHeight());
        }
        if ((this.inPreviewMode || this.transitionAnimationPreviewMode) && child == (layoutContainer = this.containerView)) {
            drawPreviewDrawables(canvas, layoutContainer);
        }
        boolean result = super.drawChild(canvas, child, drawingTime);
        canvas.restoreToCount(restoreCount);
        if (translationX != 0 || this.overrideWidthOffset != -1) {
            int i = this.overrideWidthOffset;
            if (i == -1) {
                i = width - translationX;
            }
            int widthOffset = i;
            if (child == this.containerView) {
                float alpha = MathUtils.clamp(widthOffset / AndroidUtilities.dp(20.0f), 0.0f, 1.0f);
                Drawable drawable = layerShadowDrawable;
                drawable.setBounds(translationX - drawable.getIntrinsicWidth(), child.getTop(), translationX, child.getBottom());
                layerShadowDrawable.setAlpha((int) (255.0f * alpha));
                layerShadowDrawable.draw(canvas);
            } else if (child == this.containerViewBack) {
                float opacity = MathUtils.clamp(widthOffset / width, 0.0f, 0.8f);
                scrimPaint.setColor(Color.argb((int) (153.0f * opacity), 0, 0, 0));
                if (this.overrideWidthOffset != -1) {
                    canvas.drawRect(0.0f, 0.0f, getWidth(), getHeight(), scrimPaint);
                } else {
                    canvas.drawRect(clipLeft, 0.0f, clipRight, getHeight(), scrimPaint);
                }
            }
        }
        return result;
    }

    public void setOverrideWidthOffset(int overrideWidthOffset) {
        this.overrideWidthOffset = overrideWidthOffset;
        invalidate();
    }

    public float getCurrentPreviewFragmentAlpha() {
        if (this.inPreviewMode || this.transitionAnimationPreviewMode || this.previewOpenAnimationInProgress) {
            BaseFragment baseFragment = this.oldFragment;
            return ((baseFragment == null || !baseFragment.inPreviewMode) ? this.containerView : this.containerViewBack).getAlpha();
        }
        return 0.0f;
    }

    public void drawCurrentPreviewFragment(Canvas canvas, Drawable foregroundDrawable) {
        if (this.inPreviewMode || this.transitionAnimationPreviewMode || this.previewOpenAnimationInProgress) {
            BaseFragment baseFragment = this.oldFragment;
            ViewGroup v = (baseFragment == null || !baseFragment.inPreviewMode) ? this.containerView : this.containerViewBack;
            drawPreviewDrawables(canvas, v);
            if (v.getAlpha() < 1.0f) {
                canvas.saveLayerAlpha(0.0f, 0.0f, getWidth(), getHeight(), (int) (v.getAlpha() * 255.0f), 31);
            } else {
                canvas.save();
            }
            canvas.concat(v.getMatrix());
            v.draw(canvas);
            if (foregroundDrawable != null) {
                int i = 0;
                View child = v.getChildAt(0);
                if (child != null) {
                    ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
                    Rect rect = new Rect();
                    child.getLocalVisibleRect(rect);
                    rect.offset(lp.leftMargin, lp.topMargin);
                    int i2 = rect.top;
                    if (Build.VERSION.SDK_INT >= 21) {
                        i = AndroidUtilities.statusBarHeight - 1;
                    }
                    rect.top = i2 + i;
                    foregroundDrawable.setAlpha((int) (v.getAlpha() * 255.0f));
                    foregroundDrawable.setBounds(rect);
                    foregroundDrawable.draw(canvas);
                }
            }
            canvas.restore();
        }
    }

    private void drawPreviewDrawables(Canvas canvas, ViewGroup containerView) {
        int i = 0;
        View view = containerView.getChildAt(0);
        if (view != null) {
            this.previewBackgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            this.previewBackgroundDrawable.draw(canvas);
            if (this.previewMenu == null) {
                int width = AndroidUtilities.dp(32.0f);
                int height = width / 2;
                int x = (getMeasuredWidth() - width) / 2;
                float top = view.getTop() + containerView.getTranslationY();
                if (Build.VERSION.SDK_INT < 21) {
                    i = 20;
                }
                int y = (int) (top - AndroidUtilities.dp(i + 12));
                Theme.moveUpDrawable.setBounds(x, y, x + width, y + height);
                Theme.moveUpDrawable.draw(canvas);
            }
        }
    }

    public void setDelegate(ActionBarLayoutDelegate actionBarLayoutDelegate) {
        this.delegate = actionBarLayoutDelegate;
    }

    public void onSlideAnimationEnd(boolean backAnimation) {
        ViewGroup parent;
        ViewGroup parent2;
        if (!backAnimation) {
            if (this.fragmentsStack.size() < 2) {
                return;
            }
            ArrayList<BaseFragment> arrayList = this.fragmentsStack;
            BaseFragment lastFragment = arrayList.get(arrayList.size() - 1);
            lastFragment.prepareFragmentToSlide(true, false);
            lastFragment.onPause();
            lastFragment.onFragmentDestroy();
            lastFragment.setParentLayout(null);
            ArrayList<BaseFragment> arrayList2 = this.fragmentsStack;
            arrayList2.remove(arrayList2.size() - 1);
            onFragmentStackChanged();
            LayoutContainer temp = this.containerView;
            LayoutContainer layoutContainer = this.containerViewBack;
            this.containerView = layoutContainer;
            this.containerViewBack = temp;
            bringChildToFront(layoutContainer);
            ArrayList<BaseFragment> arrayList3 = this.fragmentsStack;
            BaseFragment lastFragment2 = arrayList3.get(arrayList3.size() - 1);
            this.currentActionBar = lastFragment2.actionBar;
            lastFragment2.onResume();
            lastFragment2.onBecomeFullyVisible();
            lastFragment2.prepareFragmentToSlide(false, false);
            this.layoutToIgnore = this.containerView;
        } else {
            if (this.fragmentsStack.size() >= 2) {
                ArrayList<BaseFragment> arrayList4 = this.fragmentsStack;
                arrayList4.get(arrayList4.size() - 1).prepareFragmentToSlide(true, false);
                ArrayList<BaseFragment> arrayList5 = this.fragmentsStack;
                BaseFragment lastFragment3 = arrayList5.get(arrayList5.size() - 2);
                lastFragment3.prepareFragmentToSlide(false, false);
                lastFragment3.onPause();
                if (lastFragment3.fragmentView != null && (parent2 = (ViewGroup) lastFragment3.fragmentView.getParent()) != null) {
                    lastFragment3.onRemoveFromParent();
                    parent2.removeViewInLayout(lastFragment3.fragmentView);
                }
                if (lastFragment3.actionBar != null && lastFragment3.actionBar.shouldAddToContainer() && (parent = (ViewGroup) lastFragment3.actionBar.getParent()) != null) {
                    parent.removeViewInLayout(lastFragment3.actionBar);
                }
            }
            this.layoutToIgnore = null;
        }
        this.containerViewBack.setVisibility(4);
        this.startedTracking = false;
        this.animationInProgress = false;
        this.containerView.setTranslationX(0.0f);
        this.containerViewBack.setTranslationX(0.0f);
        setInnerTranslationX(0.0f);
    }

    private void prepareForMoving(MotionEvent ev) {
        this.maybeStartTracking = false;
        this.startedTracking = true;
        this.layoutToIgnore = this.containerViewBack;
        this.startedTrackingX = (int) ev.getX();
        this.containerViewBack.setVisibility(0);
        this.beginTrackingSent = false;
        ArrayList<BaseFragment> arrayList = this.fragmentsStack;
        BaseFragment lastFragment = arrayList.get(arrayList.size() - 2);
        View fragmentView = lastFragment.fragmentView;
        if (fragmentView == null) {
            fragmentView = lastFragment.createView(this.parentActivity);
        }
        ViewGroup parent = (ViewGroup) fragmentView.getParent();
        if (parent != null) {
            lastFragment.onRemoveFromParent();
            parent.removeView(fragmentView);
        }
        this.containerViewBack.addView(fragmentView);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fragmentView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.leftMargin = 0;
        layoutParams.rightMargin = 0;
        layoutParams.bottomMargin = 0;
        layoutParams.topMargin = 0;
        fragmentView.setLayoutParams(layoutParams);
        if (lastFragment.actionBar != null && lastFragment.actionBar.shouldAddToContainer()) {
            ViewGroup parent2 = (ViewGroup) lastFragment.actionBar.getParent();
            if (parent2 != null) {
                parent2.removeView(lastFragment.actionBar);
            }
            if (this.removeActionBarExtraHeight) {
                lastFragment.actionBar.setOccupyStatusBar(false);
            }
            this.containerViewBack.addView(lastFragment.actionBar);
            lastFragment.actionBar.setTitleOverlayText(this.titleOverlayText, this.titleOverlayTextId, this.overlayAction);
        }
        if (!lastFragment.hasOwnBackground && fragmentView.getBackground() == null) {
            fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        }
        lastFragment.onResume();
        if (this.themeAnimatorSet != null) {
            this.presentingFragmentDescriptions = lastFragment.getThemeDescriptions();
        }
        ArrayList<BaseFragment> arrayList2 = this.fragmentsStack;
        BaseFragment currentFragment = arrayList2.get(arrayList2.size() - 1);
        currentFragment.prepareFragmentToSlide(true, true);
        lastFragment.prepareFragmentToSlide(false, true);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        VelocityTracker velocityTracker;
        float distToMove;
        Animator customTransition;
        float distToMove2;
        if (!checkTransitionAnimation() && !this.inActionMode && !this.animationInProgress) {
            if (this.fragmentsStack.size() > 1) {
                if (ev == null || ev.getAction() != 0) {
                    if (ev == null || ev.getAction() != 2 || ev.getPointerId(0) != this.startedTrackingPointerId) {
                        if (ev != null && ev.getPointerId(0) == this.startedTrackingPointerId && (ev.getAction() == 3 || ev.getAction() == 1 || ev.getAction() == 6)) {
                            if (this.velocityTracker == null) {
                                this.velocityTracker = VelocityTracker.obtain();
                            }
                            this.velocityTracker.computeCurrentVelocity(1000);
                            ArrayList<BaseFragment> arrayList = this.fragmentsStack;
                            BaseFragment currentFragment = arrayList.get(arrayList.size() - 1);
                            if (!this.inPreviewMode && !this.transitionAnimationPreviewMode && !this.startedTracking && currentFragment.isSwipeBackEnabled(ev)) {
                                float velX = this.velocityTracker.getXVelocity();
                                float velY = this.velocityTracker.getYVelocity();
                                if (velX >= 3500.0f && velX > Math.abs(velY) && currentFragment.canBeginSlide()) {
                                    prepareForMoving(ev);
                                    if (!this.beginTrackingSent) {
                                        if (((Activity) getContext()).getCurrentFocus() != null) {
                                            AndroidUtilities.hideKeyboard(((Activity) getContext()).getCurrentFocus());
                                        }
                                        this.beginTrackingSent = true;
                                    }
                                }
                            }
                            if (!this.startedTracking) {
                                this.maybeStartTracking = false;
                                this.startedTracking = false;
                                velocityTracker = null;
                                this.layoutToIgnore = null;
                            } else {
                                float x = this.containerView.getX();
                                AnimatorSet animatorSet = new AnimatorSet();
                                float velX2 = this.velocityTracker.getXVelocity();
                                float velY2 = this.velocityTracker.getYVelocity();
                                final boolean backAnimation = x < ((float) this.containerView.getMeasuredWidth()) / 3.0f && (velX2 < 3500.0f || velX2 < velY2);
                                boolean overrideTransition = currentFragment.shouldOverrideSlideTransition(false, backAnimation);
                                if (!backAnimation) {
                                    float distToMove3 = this.containerView.getMeasuredWidth() - x;
                                    int duration = Math.max((int) ((200.0f / this.containerView.getMeasuredWidth()) * distToMove3), 50);
                                    if (overrideTransition) {
                                        distToMove2 = distToMove3;
                                    } else {
                                        distToMove2 = distToMove3;
                                        animatorSet.playTogether(ObjectAnimator.ofFloat(this.containerView, View.TRANSLATION_X, this.containerView.getMeasuredWidth()).setDuration(duration), ObjectAnimator.ofFloat(this, "innerTranslationX", this.containerView.getMeasuredWidth()).setDuration(duration));
                                    }
                                    distToMove = distToMove2;
                                } else {
                                    distToMove = x;
                                    int duration2 = Math.max((int) ((200.0f / this.containerView.getMeasuredWidth()) * distToMove), 50);
                                    if (!overrideTransition) {
                                        animatorSet.playTogether(ObjectAnimator.ofFloat(this.containerView, View.TRANSLATION_X, 0.0f).setDuration(duration2), ObjectAnimator.ofFloat(this, "innerTranslationX", 0.0f).setDuration(duration2));
                                    }
                                }
                                Animator customTransition2 = currentFragment.getCustomSlideTransition(false, backAnimation, distToMove);
                                if (customTransition2 != null) {
                                    animatorSet.playTogether(customTransition2);
                                }
                                ArrayList<BaseFragment> arrayList2 = this.fragmentsStack;
                                BaseFragment lastFragment = arrayList2.get(arrayList2.size() - 2);
                                if (lastFragment != null && (customTransition = lastFragment.getCustomSlideTransition(false, backAnimation, distToMove)) != null) {
                                    animatorSet.playTogether(customTransition);
                                }
                                animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.ActionBarLayout.1
                                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                                    public void onAnimationEnd(Animator animator) {
                                        ActionBarLayout.this.onSlideAnimationEnd(backAnimation);
                                    }
                                });
                                animatorSet.start();
                                this.animationInProgress = true;
                                this.layoutToIgnore = this.containerViewBack;
                                velocityTracker = null;
                            }
                            VelocityTracker velocityTracker2 = this.velocityTracker;
                            if (velocityTracker2 != null) {
                                velocityTracker2.recycle();
                                this.velocityTracker = velocityTracker;
                            }
                        } else if (ev == null) {
                            this.maybeStartTracking = false;
                            this.startedTracking = false;
                            this.layoutToIgnore = null;
                            VelocityTracker velocityTracker3 = this.velocityTracker;
                            if (velocityTracker3 != null) {
                                velocityTracker3.recycle();
                                this.velocityTracker = null;
                            }
                        }
                    } else {
                        if (this.velocityTracker == null) {
                            this.velocityTracker = VelocityTracker.obtain();
                        }
                        int dx = Math.max(0, (int) (ev.getX() - this.startedTrackingX));
                        int dy = Math.abs(((int) ev.getY()) - this.startedTrackingY);
                        this.velocityTracker.addMovement(ev);
                        if (!this.transitionAnimationInProgress && !this.inPreviewMode && this.maybeStartTracking && !this.startedTracking && dx >= AndroidUtilities.getPixelsInCM(0.4f, true) && Math.abs(dx) / 3 > dy) {
                            ArrayList<BaseFragment> arrayList3 = this.fragmentsStack;
                            if (arrayList3.get(arrayList3.size() - 1).canBeginSlide() && findScrollingChild(this, ev.getX(), ev.getY()) == null) {
                                prepareForMoving(ev);
                            } else {
                                this.maybeStartTracking = false;
                            }
                        } else if (this.startedTracking) {
                            if (!this.beginTrackingSent) {
                                if (this.parentActivity.getCurrentFocus() != null) {
                                    AndroidUtilities.hideKeyboard(this.parentActivity.getCurrentFocus());
                                }
                                ArrayList<BaseFragment> arrayList4 = this.fragmentsStack;
                                arrayList4.get(arrayList4.size() - 1).onBeginSlide();
                                this.beginTrackingSent = true;
                            }
                            this.containerView.setTranslationX(dx);
                            setInnerTranslationX(dx);
                        }
                    }
                } else {
                    ArrayList<BaseFragment> arrayList5 = this.fragmentsStack;
                    if (arrayList5.get(arrayList5.size() - 1).isSwipeBackEnabled(ev)) {
                        this.startedTrackingPointerId = ev.getPointerId(0);
                        this.maybeStartTracking = true;
                        this.startedTrackingX = (int) ev.getX();
                        this.startedTrackingY = (int) ev.getY();
                        VelocityTracker velocityTracker4 = this.velocityTracker;
                        if (velocityTracker4 != null) {
                            velocityTracker4.clear();
                        }
                    } else {
                        this.maybeStartTracking = false;
                        this.startedTracking = false;
                        return false;
                    }
                }
            }
            return this.startedTracking;
        }
        return false;
    }

    public void onBackPressed() {
        if (this.transitionAnimationPreviewMode || this.startedTracking || checkTransitionAnimation() || this.fragmentsStack.isEmpty() || GroupCallPip.onBackPressed()) {
            return;
        }
        ActionBar actionBar = this.currentActionBar;
        if (actionBar != null && !actionBar.isActionModeShowed() && this.currentActionBar.isSearchFieldVisible) {
            this.currentActionBar.closeSearchField();
            return;
        }
        ArrayList<BaseFragment> arrayList = this.fragmentsStack;
        BaseFragment lastFragment = arrayList.get(arrayList.size() - 1);
        if (lastFragment.onBackPressed() && !this.fragmentsStack.isEmpty()) {
            closeLastFragment(true);
        }
    }

    public void onLowMemory() {
        Iterator<BaseFragment> it = this.fragmentsStack.iterator();
        while (it.hasNext()) {
            BaseFragment fragment = it.next();
            fragment.onLowMemory();
        }
    }

    public void onAnimationEndCheck(boolean byCheck) {
        onCloseAnimationEnd();
        onOpenAnimationEnd();
        Runnable runnable = this.waitingForKeyboardCloseRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.waitingForKeyboardCloseRunnable = null;
        }
        AnimatorSet animatorSet = this.currentAnimation;
        if (animatorSet != null) {
            if (byCheck) {
                animatorSet.cancel();
            }
            this.currentAnimation = null;
        }
        Runnable runnable2 = this.animationRunnable;
        if (runnable2 != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable2);
            this.animationRunnable = null;
        }
        setAlpha(1.0f);
        this.containerView.setAlpha(1.0f);
        this.containerView.setScaleX(1.0f);
        this.containerView.setScaleY(1.0f);
        this.containerViewBack.setAlpha(1.0f);
        this.containerViewBack.setScaleX(1.0f);
        this.containerViewBack.setScaleY(1.0f);
    }

    public BaseFragment getLastFragment() {
        if (this.fragmentsStack.isEmpty()) {
            return null;
        }
        ArrayList<BaseFragment> arrayList = this.fragmentsStack;
        return arrayList.get(arrayList.size() - 1);
    }

    public boolean checkTransitionAnimation() {
        if (this.transitionAnimationPreviewMode) {
            return false;
        }
        if (this.transitionAnimationInProgress && this.transitionAnimationStartTime < System.currentTimeMillis() - 1500) {
            onAnimationEndCheck(true);
        }
        return this.transitionAnimationInProgress;
    }

    public boolean isPreviewOpenAnimationInProgress() {
        return this.previewOpenAnimationInProgress;
    }

    public boolean isTransitionAnimationInProgress() {
        return this.transitionAnimationInProgress || this.animationInProgress;
    }

    private void presentFragmentInternalRemoveOld(boolean removeLast, BaseFragment fragment) {
        ViewGroup parent;
        ViewGroup parent2;
        if (fragment == null) {
            return;
        }
        fragment.onBecomeFullyHidden();
        fragment.onPause();
        if (removeLast) {
            fragment.onFragmentDestroy();
            fragment.setParentLayout(null);
            this.fragmentsStack.remove(fragment);
            onFragmentStackChanged();
        } else {
            if (fragment.fragmentView != null && (parent2 = (ViewGroup) fragment.fragmentView.getParent()) != null) {
                fragment.onRemoveFromParent();
                try {
                    parent2.removeViewInLayout(fragment.fragmentView);
                } catch (Exception e) {
                    FileLog.e(e);
                    try {
                        parent2.removeView(fragment.fragmentView);
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                }
            }
            if (fragment.actionBar != null && fragment.actionBar.shouldAddToContainer() && (parent = (ViewGroup) fragment.actionBar.getParent()) != null) {
                parent.removeViewInLayout(fragment.actionBar);
            }
        }
        this.containerViewBack.setVisibility(4);
    }

    public boolean presentFragmentAsPreview(BaseFragment fragment) {
        return presentFragment(fragment, false, false, true, true, null);
    }

    public boolean presentFragmentAsPreviewWithMenu(BaseFragment fragment, ActionBarPopupWindow.ActionBarPopupWindowLayout menu) {
        return presentFragment(fragment, false, false, true, true, menu);
    }

    public boolean presentFragment(BaseFragment fragment) {
        return presentFragment(fragment, false, false, true, false, null);
    }

    public boolean presentFragment(BaseFragment fragment, boolean removeLast) {
        return presentFragment(fragment, removeLast, false, true, false, null);
    }

    public void startLayoutAnimation(final boolean open, final boolean first, final boolean preview) {
        if (first) {
            this.animationProgress = 0.0f;
            this.lastFrameTime = System.nanoTime() / 1000000;
        }
        Runnable runnable = new Runnable() { // from class: org.telegram.ui.ActionBar.ActionBarLayout.2
            @Override // java.lang.Runnable
            public void run() {
                float interpolated;
                if (ActionBarLayout.this.animationRunnable == this) {
                    Integer newNavigationBarColor = null;
                    ActionBarLayout.this.animationRunnable = null;
                    if (first) {
                        ActionBarLayout.this.transitionAnimationStartTime = System.currentTimeMillis();
                    }
                    long newTime = System.nanoTime() / 1000000;
                    long dt = newTime - ActionBarLayout.this.lastFrameTime;
                    if (dt > 18) {
                        dt = 18;
                    }
                    ActionBarLayout.this.lastFrameTime = newTime;
                    float duration = (!preview || !open) ? 150.0f : 190.0f;
                    ActionBarLayout.access$1216(ActionBarLayout.this, ((float) dt) / duration);
                    if (ActionBarLayout.this.animationProgress > 1.0f) {
                        ActionBarLayout.this.animationProgress = 1.0f;
                    }
                    if (ActionBarLayout.this.newFragment != null) {
                        ActionBarLayout.this.newFragment.onTransitionAnimationProgress(true, ActionBarLayout.this.animationProgress);
                    }
                    if (ActionBarLayout.this.oldFragment != null) {
                        ActionBarLayout.this.oldFragment.onTransitionAnimationProgress(false, ActionBarLayout.this.animationProgress);
                    }
                    Integer oldNavigationBarColor = ActionBarLayout.this.oldFragment != null ? Integer.valueOf(ActionBarLayout.this.oldFragment.getNavigationBarColor()) : null;
                    if (ActionBarLayout.this.newFragment != null) {
                        newNavigationBarColor = Integer.valueOf(ActionBarLayout.this.newFragment.getNavigationBarColor());
                    }
                    if (ActionBarLayout.this.newFragment != null && oldNavigationBarColor != null) {
                        float ratio = MathUtils.clamp((ActionBarLayout.this.animationProgress * 2.0f) - (open ? 1.0f : 0.0f), 0.0f, 1.0f);
                        ActionBarLayout.this.newFragment.setNavigationBarColor(ColorUtils.blendARGB(oldNavigationBarColor.intValue(), newNavigationBarColor.intValue(), ratio));
                    }
                    if (!preview) {
                        interpolated = ActionBarLayout.this.decelerateInterpolator.getInterpolation(ActionBarLayout.this.animationProgress);
                    } else {
                        interpolated = open ? ActionBarLayout.this.overshootInterpolator.getInterpolation(ActionBarLayout.this.animationProgress) : CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(ActionBarLayout.this.animationProgress);
                    }
                    if (open) {
                        float clampedInterpolated = MathUtils.clamp(interpolated, 0.0f, 1.0f);
                        ActionBarLayout.this.containerView.setAlpha(clampedInterpolated);
                        if (preview) {
                            ActionBarLayout.this.containerView.setScaleX((interpolated * 0.3f) + 0.7f);
                            ActionBarLayout.this.containerView.setScaleY((0.3f * interpolated) + 0.7f);
                            if (ActionBarLayout.this.previewMenu != null) {
                                ActionBarLayout.this.containerView.setTranslationY(AndroidUtilities.dp(40.0f) * (1.0f - interpolated));
                                ActionBarLayout.this.previewMenu.setTranslationY((-AndroidUtilities.dp(70.0f)) * (1.0f - interpolated));
                                ActionBarLayout.this.previewMenu.setScaleX((interpolated * 0.05f) + 0.95f);
                                ActionBarLayout.this.previewMenu.setScaleY((0.05f * interpolated) + 0.95f);
                            }
                            ActionBarLayout.this.previewBackgroundDrawable.setAlpha((int) (46.0f * clampedInterpolated));
                            Theme.moveUpDrawable.setAlpha((int) (255.0f * clampedInterpolated));
                            ActionBarLayout.this.containerView.invalidate();
                            ActionBarLayout.this.invalidate();
                        } else {
                            ActionBarLayout.this.containerView.setTranslationX(AndroidUtilities.dp(48.0f) * (1.0f - interpolated));
                        }
                    } else {
                        float clampedReverseInterpolated = MathUtils.clamp(1.0f - interpolated, 0.0f, 1.0f);
                        ActionBarLayout.this.containerViewBack.setAlpha(clampedReverseInterpolated);
                        if (preview) {
                            ActionBarLayout.this.containerViewBack.setScaleX(((1.0f - interpolated) * 0.1f) + 0.9f);
                            ActionBarLayout.this.containerViewBack.setScaleY(((1.0f - interpolated) * 0.1f) + 0.9f);
                            ActionBarLayout.this.previewBackgroundDrawable.setAlpha((int) (46.0f * clampedReverseInterpolated));
                            if (ActionBarLayout.this.previewMenu == null) {
                                Theme.moveUpDrawable.setAlpha((int) (255.0f * clampedReverseInterpolated));
                            }
                            ActionBarLayout.this.containerView.invalidate();
                            ActionBarLayout.this.invalidate();
                        } else {
                            ActionBarLayout.this.containerViewBack.setTranslationX(AndroidUtilities.dp(48.0f) * interpolated);
                        }
                    }
                    if (ActionBarLayout.this.animationProgress < 1.0f) {
                        ActionBarLayout.this.startLayoutAnimation(open, false, preview);
                    } else {
                        ActionBarLayout.this.onAnimationEndCheck(false);
                    }
                }
            }
        };
        this.animationRunnable = runnable;
        AndroidUtilities.runOnUIThread(runnable);
    }

    public void resumeDelayedFragmentAnimation() {
        this.delayedAnimationResumed = true;
        Runnable runnable = this.delayedOpenAnimationRunnable;
        if (runnable == null || this.waitingForKeyboardCloseRunnable != null) {
            return;
        }
        AndroidUtilities.cancelRunOnUIThread(runnable);
        this.delayedOpenAnimationRunnable.run();
        this.delayedOpenAnimationRunnable = null;
    }

    public boolean isInPreviewMode() {
        return this.inPreviewMode || this.transitionAnimationPreviewMode;
    }

    public boolean isInPassivePreviewMode() {
        return (this.inPreviewMode && this.previewMenu == null) || this.transitionAnimationPreviewMode;
    }

    public boolean isInPreviewMenuMode() {
        return isInPreviewMode() && this.previewMenu != null;
    }

    public boolean presentFragment(BaseFragment fragment, boolean removeLast, boolean forceWithoutAnimation, boolean check, boolean preview) {
        return presentFragment(fragment, removeLast, forceWithoutAnimation, check, preview, null);
    }

    public boolean presentFragment(final BaseFragment fragment, final boolean removeLast, boolean forceWithoutAnimation, boolean check, final boolean preview, final ActionBarPopupWindow.ActionBarPopupWindowLayout menu) {
        View fragmentView;
        int menuHeight;
        boolean z;
        AnimatorSet animation;
        long j;
        boolean z2;
        boolean z3;
        if (fragment != null && !checkTransitionAnimation()) {
            ActionBarLayoutDelegate actionBarLayoutDelegate = this.delegate;
            if ((actionBarLayoutDelegate != null && check && !actionBarLayoutDelegate.needPresentFragment(fragment, removeLast, forceWithoutAnimation, this)) || !fragment.onFragmentCreate()) {
                return false;
            }
            BaseFragment baseFragment = null;
            if (this.inPreviewMode && this.transitionAnimationPreviewMode) {
                Runnable runnable = this.delayedOpenAnimationRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                    this.delayedOpenAnimationRunnable = null;
                }
                closeLastFragment(false, true);
            }
            fragment.setInPreviewMode(preview);
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.previewMenu;
            if (actionBarPopupWindowLayout != null) {
                if (actionBarPopupWindowLayout.getParent() != null) {
                    ((ViewGroup) this.previewMenu.getParent()).removeView(this.previewMenu);
                }
                this.previewMenu = null;
            }
            this.previewMenu = menu;
            fragment.setInMenuMode(menu != null);
            if (this.parentActivity.getCurrentFocus() != null && fragment.hideKeyboardOnShow() && !preview) {
                AndroidUtilities.hideKeyboard(this.parentActivity.getCurrentFocus());
            }
            boolean needAnimation = preview || (!forceWithoutAnimation && MessagesController.getGlobalMainSettings().getBoolean("view_animations", true));
            if (!this.fragmentsStack.isEmpty()) {
                ArrayList<BaseFragment> arrayList = this.fragmentsStack;
                baseFragment = arrayList.get(arrayList.size() - 1);
            }
            final BaseFragment currentFragment = baseFragment;
            fragment.setParentLayout(this);
            View fragmentView2 = fragment.fragmentView;
            if (fragmentView2 == null) {
                fragmentView = fragment.createView(this.parentActivity);
            } else {
                ViewGroup parent = (ViewGroup) fragmentView2.getParent();
                if (parent != null) {
                    fragment.onRemoveFromParent();
                    parent.removeView(fragmentView2);
                }
                fragmentView = fragmentView2;
            }
            View wrappedView = fragmentView;
            this.containerViewBack.addView(wrappedView);
            if (menu == null) {
                menuHeight = 0;
            } else {
                this.containerViewBack.addView(menu);
                menu.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), Integer.MIN_VALUE));
                int menuHeight2 = menu.getMeasuredHeight() + AndroidUtilities.dp(24.0f);
                FrameLayout.LayoutParams menuParams = (FrameLayout.LayoutParams) menu.getLayoutParams();
                menuParams.width = -2;
                menuParams.height = -2;
                menuParams.topMargin = (getMeasuredHeight() - menuHeight2) - AndroidUtilities.dp(6.0f);
                menu.setLayoutParams(menuParams);
                menuHeight = menuHeight2;
            }
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) wrappedView.getLayoutParams();
            layoutParams.width = -1;
            layoutParams.height = -1;
            if (preview) {
                int height = fragment.getPreviewHeight();
                int statusBarHeight = Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
                if (height > 0 && height < getMeasuredHeight() - statusBarHeight) {
                    layoutParams.height = height;
                    layoutParams.topMargin = (((getMeasuredHeight() - statusBarHeight) - height) / 2) + statusBarHeight;
                } else {
                    int dp = AndroidUtilities.dp(menu != null ? 0.0f : 24.0f);
                    layoutParams.bottomMargin = dp;
                    layoutParams.topMargin = dp;
                    layoutParams.topMargin += AndroidUtilities.statusBarHeight;
                }
                if (menu != null) {
                    layoutParams.bottomMargin += menuHeight + AndroidUtilities.dp(8.0f);
                }
                int dp2 = AndroidUtilities.dp(8.0f);
                layoutParams.leftMargin = dp2;
                layoutParams.rightMargin = dp2;
            } else {
                layoutParams.leftMargin = 0;
                layoutParams.rightMargin = 0;
                layoutParams.bottomMargin = 0;
                layoutParams.topMargin = 0;
            }
            wrappedView.setLayoutParams(layoutParams);
            if (fragment.actionBar != null && fragment.actionBar.shouldAddToContainer()) {
                if (this.removeActionBarExtraHeight) {
                    fragment.actionBar.setOccupyStatusBar(false);
                }
                ViewGroup parent2 = (ViewGroup) fragment.actionBar.getParent();
                if (parent2 != null) {
                    parent2.removeView(fragment.actionBar);
                }
                this.containerViewBack.addView(fragment.actionBar);
                fragment.actionBar.setTitleOverlayText(this.titleOverlayText, this.titleOverlayTextId, this.overlayAction);
            }
            this.fragmentsStack.add(fragment);
            onFragmentStackChanged();
            fragment.onResume();
            this.currentActionBar = fragment.actionBar;
            if (!fragment.hasOwnBackground && fragmentView.getBackground() == null) {
                fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
            LayoutContainer temp = this.containerView;
            LayoutContainer layoutContainer = this.containerViewBack;
            this.containerView = layoutContainer;
            this.containerViewBack = temp;
            layoutContainer.setVisibility(0);
            setInnerTranslationX(0.0f);
            this.containerView.setTranslationY(0.0f);
            if (preview) {
                if (Build.VERSION.SDK_INT >= 21) {
                    fragmentView.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.ActionBar.ActionBarLayout.3
                        @Override // android.view.ViewOutlineProvider
                        public void getOutline(View view, Outline outline) {
                            outline.setRoundRect(0, AndroidUtilities.statusBarHeight, view.getMeasuredWidth(), view.getMeasuredHeight(), AndroidUtilities.dp(6.0f));
                        }
                    });
                    fragmentView.setClipToOutline(true);
                    fragmentView.setElevation(AndroidUtilities.dp(4.0f));
                }
                if (this.previewBackgroundDrawable == null) {
                    this.previewBackgroundDrawable = new ColorDrawable(771751936);
                }
                this.previewBackgroundDrawable.setAlpha(0);
                Theme.moveUpDrawable.setAlpha(0);
            }
            bringChildToFront(this.containerView);
            if (!needAnimation) {
                presentFragmentInternalRemoveOld(removeLast, currentFragment);
                View view = this.backgroundView;
                if (view != null) {
                    view.setVisibility(0);
                }
            }
            if (this.themeAnimatorSet != null) {
                this.presentingFragmentDescriptions = fragment.getThemeDescriptions();
            }
            if (needAnimation || preview) {
                if (this.useAlphaAnimations && this.fragmentsStack.size() == 1) {
                    presentFragmentInternalRemoveOld(removeLast, currentFragment);
                    this.transitionAnimationStartTime = System.currentTimeMillis();
                    this.transitionAnimationInProgress = true;
                    this.layoutToIgnore = this.containerView;
                    this.onOpenAnimationEndRunnable = new Runnable() { // from class: org.telegram.ui.ActionBar.ActionBarLayout$$ExternalSyntheticLambda5
                        @Override // java.lang.Runnable
                        public final void run() {
                            ActionBarLayout.lambda$presentFragment$0(BaseFragment.this, fragment);
                        }
                    };
                    ArrayList<Animator> animators = new ArrayList<>();
                    animators.add(ObjectAnimator.ofFloat(this, View.ALPHA, 0.0f, 1.0f));
                    View view2 = this.backgroundView;
                    if (view2 != null) {
                        view2.setVisibility(0);
                        animators.add(ObjectAnimator.ofFloat(this.backgroundView, View.ALPHA, 0.0f, 1.0f));
                    }
                    if (currentFragment == null) {
                        z2 = false;
                    } else {
                        z2 = false;
                        currentFragment.onTransitionAnimationStart(false, false);
                    }
                    fragment.onTransitionAnimationStart(true, z2);
                    AnimatorSet animatorSet = new AnimatorSet();
                    this.currentAnimation = animatorSet;
                    animatorSet.playTogether(animators);
                    this.currentAnimation.setInterpolator(this.accelerateDecelerateInterpolator);
                    this.currentAnimation.setDuration(200L);
                    this.currentAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.ActionBarLayout.4
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation2) {
                            ActionBarLayout.this.onAnimationEndCheck(false);
                        }
                    });
                    this.currentAnimation.start();
                    return true;
                }
                this.transitionAnimationPreviewMode = preview;
                this.transitionAnimationStartTime = System.currentTimeMillis();
                this.transitionAnimationInProgress = true;
                this.layoutToIgnore = this.containerView;
                this.onOpenAnimationEndRunnable = new Runnable() { // from class: org.telegram.ui.ActionBar.ActionBarLayout$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        ActionBarLayout.this.m1383xd7da5798(preview, menu, removeLast, currentFragment, fragment);
                    }
                };
                final boolean noDelay = !fragment.needDelayOpenAnimation();
                if (!noDelay) {
                    z = false;
                } else {
                    if (currentFragment == null) {
                        z = false;
                    } else {
                        z = false;
                        currentFragment.onTransitionAnimationStart(false, false);
                    }
                    fragment.onTransitionAnimationStart(true, z);
                }
                this.delayedAnimationResumed = z;
                this.oldFragment = currentFragment;
                this.newFragment = fragment;
                if (!preview) {
                    animation = fragment.onCustomTransitionAnimation(true, new Runnable() { // from class: org.telegram.ui.ActionBar.ActionBarLayout$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            ActionBarLayout.this.m1384xbd1bc659();
                        }
                    });
                } else {
                    animation = null;
                }
                if (animation == null) {
                    this.containerView.setAlpha(0.0f);
                    if (preview) {
                        this.containerView.setTranslationX(0.0f);
                        this.containerView.setScaleX(0.9f);
                        this.containerView.setScaleY(0.9f);
                    } else {
                        this.containerView.setTranslationX(48.0f);
                        this.containerView.setScaleX(1.0f);
                        this.containerView.setScaleY(1.0f);
                    }
                    if (!this.containerView.isKeyboardVisible) {
                        if (!this.containerViewBack.isKeyboardVisible) {
                            if (fragment.needDelayOpenAnimation()) {
                                Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.ActionBar.ActionBarLayout.7
                                    @Override // java.lang.Runnable
                                    public void run() {
                                        if (ActionBarLayout.this.delayedOpenAnimationRunnable == this) {
                                            ActionBarLayout.this.delayedOpenAnimationRunnable = null;
                                            fragment.onTransitionAnimationStart(true, false);
                                            ActionBarLayout.this.startLayoutAnimation(true, true, preview);
                                        }
                                    }
                                };
                                this.delayedOpenAnimationRunnable = runnable2;
                                AndroidUtilities.runOnUIThread(runnable2, 200L);
                                return true;
                            }
                            startLayoutAnimation(true, true, preview);
                            return true;
                        }
                        j = 200;
                    } else {
                        j = 200;
                    }
                    if (currentFragment != null && !preview) {
                        currentFragment.saveKeyboardPositionBeforeTransition();
                    }
                    long j2 = j;
                    this.waitingForKeyboardCloseRunnable = new Runnable() { // from class: org.telegram.ui.ActionBar.ActionBarLayout.5
                        @Override // java.lang.Runnable
                        public void run() {
                            if (ActionBarLayout.this.waitingForKeyboardCloseRunnable == this) {
                                ActionBarLayout.this.waitingForKeyboardCloseRunnable = null;
                                if (!noDelay) {
                                    if (ActionBarLayout.this.delayedOpenAnimationRunnable != null) {
                                        AndroidUtilities.cancelRunOnUIThread(ActionBarLayout.this.delayedOpenAnimationRunnable);
                                        if (ActionBarLayout.this.delayedAnimationResumed) {
                                            ActionBarLayout.this.delayedOpenAnimationRunnable.run();
                                            return;
                                        } else {
                                            AndroidUtilities.runOnUIThread(ActionBarLayout.this.delayedOpenAnimationRunnable, 200L);
                                            return;
                                        }
                                    }
                                    return;
                                }
                                BaseFragment baseFragment2 = currentFragment;
                                if (baseFragment2 != null) {
                                    baseFragment2.onTransitionAnimationStart(false, false);
                                }
                                fragment.onTransitionAnimationStart(true, false);
                                ActionBarLayout.this.startLayoutAnimation(true, true, preview);
                            }
                        }
                    };
                    if (fragment.needDelayOpenAnimation()) {
                        this.delayedOpenAnimationRunnable = new Runnable() { // from class: org.telegram.ui.ActionBar.ActionBarLayout.6
                            @Override // java.lang.Runnable
                            public void run() {
                                if (ActionBarLayout.this.delayedOpenAnimationRunnable == this) {
                                    ActionBarLayout.this.delayedOpenAnimationRunnable = null;
                                    BaseFragment baseFragment2 = currentFragment;
                                    if (baseFragment2 != null) {
                                        baseFragment2.onTransitionAnimationStart(false, false);
                                    }
                                    fragment.onTransitionAnimationStart(true, false);
                                    ActionBarLayout.this.startLayoutAnimation(true, true, preview);
                                }
                            }
                        };
                    }
                    AndroidUtilities.runOnUIThread(this.waitingForKeyboardCloseRunnable, SharedConfig.smoothKeyboard ? 250L : j2);
                    return true;
                }
                if (!preview && ((this.containerView.isKeyboardVisible || this.containerViewBack.isKeyboardVisible) && currentFragment != null)) {
                    currentFragment.saveKeyboardPositionBeforeTransition();
                }
                this.currentAnimation = animation;
                return true;
            }
            View view3 = this.backgroundView;
            if (view3 == null) {
                z3 = false;
            } else {
                view3.setAlpha(1.0f);
                z3 = false;
                this.backgroundView.setVisibility(0);
            }
            if (currentFragment != null) {
                currentFragment.onTransitionAnimationStart(z3, z3);
                currentFragment.onTransitionAnimationEnd(z3, z3);
            }
            fragment.onTransitionAnimationStart(true, z3);
            fragment.onTransitionAnimationEnd(true, z3);
            fragment.onBecomeFullyVisible();
            return true;
        }
        return false;
    }

    public static /* synthetic */ void lambda$presentFragment$0(BaseFragment currentFragment, BaseFragment fragment) {
        if (currentFragment != null) {
            currentFragment.onTransitionAnimationEnd(false, false);
        }
        fragment.onTransitionAnimationEnd(true, false);
        fragment.onBecomeFullyVisible();
    }

    /* renamed from: lambda$presentFragment$1$org-telegram-ui-ActionBar-ActionBarLayout */
    public /* synthetic */ void m1383xd7da5798(boolean preview, ActionBarPopupWindow.ActionBarPopupWindowLayout menu, boolean removeLast, BaseFragment currentFragment, BaseFragment fragment) {
        if (preview) {
            this.inPreviewMode = true;
            this.previewMenu = menu;
            this.transitionAnimationPreviewMode = false;
            this.containerView.setScaleX(1.0f);
            this.containerView.setScaleY(1.0f);
        } else {
            presentFragmentInternalRemoveOld(removeLast, currentFragment);
            this.containerView.setTranslationX(0.0f);
        }
        if (currentFragment != null) {
            currentFragment.onTransitionAnimationEnd(false, false);
        }
        fragment.onTransitionAnimationEnd(true, false);
        fragment.onBecomeFullyVisible();
    }

    /* renamed from: lambda$presentFragment$2$org-telegram-ui-ActionBar-ActionBarLayout */
    public /* synthetic */ void m1384xbd1bc659() {
        onAnimationEndCheck(false);
    }

    public void setFragmentStackChangedListener(Runnable onFragmentStackChanged) {
        this.onFragmentStackChangedListener = onFragmentStackChanged;
    }

    private void onFragmentStackChanged() {
        Runnable runnable = this.onFragmentStackChangedListener;
        if (runnable != null) {
            runnable.run();
        }
        ImageLoader.getInstance().onFragmentStackChanged();
    }

    public boolean addFragmentToStack(BaseFragment fragment) {
        return addFragmentToStack(fragment, -1);
    }

    public boolean addFragmentToStack(BaseFragment fragment, int position) {
        ViewGroup parent;
        ViewGroup parent2;
        ActionBarLayoutDelegate actionBarLayoutDelegate = this.delegate;
        if ((actionBarLayoutDelegate != null && !actionBarLayoutDelegate.needAddFragmentToStack(fragment, this)) || !fragment.onFragmentCreate()) {
            return false;
        }
        fragment.setParentLayout(this);
        if (position == -1) {
            if (!this.fragmentsStack.isEmpty()) {
                ArrayList<BaseFragment> arrayList = this.fragmentsStack;
                BaseFragment previousFragment = arrayList.get(arrayList.size() - 1);
                previousFragment.onPause();
                if (previousFragment.actionBar != null && previousFragment.actionBar.shouldAddToContainer() && (parent2 = (ViewGroup) previousFragment.actionBar.getParent()) != null) {
                    parent2.removeView(previousFragment.actionBar);
                }
                if (previousFragment.fragmentView != null && (parent = (ViewGroup) previousFragment.fragmentView.getParent()) != null) {
                    previousFragment.onRemoveFromParent();
                    parent.removeView(previousFragment.fragmentView);
                }
            }
            this.fragmentsStack.add(fragment);
            onFragmentStackChanged();
        } else {
            this.fragmentsStack.add(position, fragment);
            onFragmentStackChanged();
        }
        return true;
    }

    private void closeLastFragmentInternalRemoveOld(BaseFragment fragment) {
        fragment.finishing = true;
        fragment.onPause();
        fragment.onFragmentDestroy();
        fragment.setParentLayout(null);
        this.fragmentsStack.remove(fragment);
        this.containerViewBack.setVisibility(4);
        this.containerViewBack.setTranslationY(0.0f);
        bringChildToFront(this.containerView);
        onFragmentStackChanged();
    }

    public void movePreviewFragment(float dy) {
        if (!this.inPreviewMode || this.previewMenu != null || this.transitionAnimationPreviewMode) {
            return;
        }
        float currentTranslation = this.containerView.getTranslationY();
        float nextTranslation = -dy;
        if (nextTranslation > 0.0f) {
            nextTranslation = 0.0f;
        } else if (nextTranslation < (-AndroidUtilities.dp(60.0f))) {
            nextTranslation = 0.0f;
            expandPreviewFragment();
        }
        if (currentTranslation != nextTranslation) {
            this.containerView.setTranslationY(nextTranslation);
            invalidate();
        }
    }

    public void expandPreviewFragment() {
        this.previewOpenAnimationInProgress = true;
        this.inPreviewMode = false;
        ArrayList<BaseFragment> arrayList = this.fragmentsStack;
        BaseFragment prevFragment = arrayList.get(arrayList.size() - 2);
        ArrayList<BaseFragment> arrayList2 = this.fragmentsStack;
        final BaseFragment fragment = arrayList2.get(arrayList2.size() - 1);
        if (Build.VERSION.SDK_INT >= 21) {
            fragment.fragmentView.setOutlineProvider(null);
            fragment.fragmentView.setClipToOutline(false);
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fragment.fragmentView.getLayoutParams();
        layoutParams.leftMargin = 0;
        layoutParams.rightMargin = 0;
        layoutParams.bottomMargin = 0;
        layoutParams.topMargin = 0;
        layoutParams.height = -1;
        fragment.fragmentView.setLayoutParams(layoutParams);
        presentFragmentInternalRemoveOld(false, prevFragment);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(fragment.fragmentView, View.SCALE_X, 1.0f, 1.05f, 1.0f), ObjectAnimator.ofFloat(fragment.fragmentView, View.SCALE_Y, 1.0f, 1.05f, 1.0f));
        animatorSet.setDuration(200L);
        animatorSet.setInterpolator(new CubicBezierInterpolator(0.42d, (double) FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, 0.58d, 1.0d));
        animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.ActionBarLayout.8
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                ActionBarLayout.this.previewOpenAnimationInProgress = false;
                fragment.onPreviewOpenAnimationEnd();
            }
        });
        animatorSet.start();
        performHapticFeedback(3);
        fragment.setInPreviewMode(false);
        fragment.setInMenuMode(false);
    }

    public void finishPreviewFragment() {
        if (!this.inPreviewMode && !this.transitionAnimationPreviewMode) {
            return;
        }
        Runnable runnable = this.delayedOpenAnimationRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.delayedOpenAnimationRunnable = null;
        }
        closeLastFragment(true);
    }

    public void closeLastFragment(boolean animated) {
        closeLastFragment(animated, false);
    }

    public void closeLastFragment(boolean animated, boolean forceNoAnimation) {
        BaseFragment previousFragment;
        View fragmentView;
        ActionBarLayoutDelegate actionBarLayoutDelegate = this.delegate;
        if ((actionBarLayoutDelegate != null && !actionBarLayoutDelegate.needCloseLastFragment(this)) || checkTransitionAnimation() || this.fragmentsStack.isEmpty()) {
            return;
        }
        if (this.parentActivity.getCurrentFocus() != null) {
            AndroidUtilities.hideKeyboard(this.parentActivity.getCurrentFocus());
        }
        setInnerTranslationX(0.0f);
        boolean needAnimation = !forceNoAnimation && (this.inPreviewMode || this.transitionAnimationPreviewMode || (animated && MessagesController.getGlobalMainSettings().getBoolean("view_animations", true)));
        ArrayList<BaseFragment> arrayList = this.fragmentsStack;
        final BaseFragment currentFragment = arrayList.get(arrayList.size() - 1);
        if (this.fragmentsStack.size() <= 1) {
            previousFragment = null;
        } else {
            ArrayList<BaseFragment> arrayList2 = this.fragmentsStack;
            previousFragment = arrayList2.get(arrayList2.size() - 2);
        }
        if (previousFragment != null) {
            AndroidUtilities.setLightStatusBar(this.parentActivity.getWindow(), Theme.getColor(Theme.key_actionBarDefault) == -1 || (previousFragment.hasForceLightStatusBar() && !Theme.getCurrentTheme().isDark()), previousFragment.hasForceLightStatusBar());
            LayoutContainer temp = this.containerView;
            this.containerView = this.containerViewBack;
            this.containerViewBack = temp;
            previousFragment.setParentLayout(this);
            View fragmentView2 = previousFragment.fragmentView;
            if (fragmentView2 != null) {
                fragmentView = fragmentView2;
            } else {
                fragmentView = previousFragment.createView(this.parentActivity);
            }
            if (!this.inPreviewMode) {
                this.containerView.setVisibility(0);
                ViewGroup parent = (ViewGroup) fragmentView.getParent();
                if (parent != null) {
                    previousFragment.onRemoveFromParent();
                    try {
                        parent.removeView(fragmentView);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
                this.containerView.addView(fragmentView);
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fragmentView.getLayoutParams();
                layoutParams.width = -1;
                layoutParams.height = -1;
                layoutParams.leftMargin = 0;
                layoutParams.rightMargin = 0;
                layoutParams.bottomMargin = 0;
                layoutParams.topMargin = 0;
                fragmentView.setLayoutParams(layoutParams);
                if (previousFragment.actionBar != null && previousFragment.actionBar.shouldAddToContainer()) {
                    if (this.removeActionBarExtraHeight) {
                        previousFragment.actionBar.setOccupyStatusBar(false);
                    }
                    ViewGroup parent2 = (ViewGroup) previousFragment.actionBar.getParent();
                    if (parent2 != null) {
                        parent2.removeView(previousFragment.actionBar);
                    }
                    this.containerView.addView(previousFragment.actionBar);
                    previousFragment.actionBar.setTitleOverlayText(this.titleOverlayText, this.titleOverlayTextId, this.overlayAction);
                }
            }
            this.newFragment = previousFragment;
            this.oldFragment = currentFragment;
            previousFragment.onTransitionAnimationStart(true, true);
            currentFragment.onTransitionAnimationStart(false, true);
            previousFragment.onResume();
            if (this.themeAnimatorSet != null) {
                this.presentingFragmentDescriptions = previousFragment.getThemeDescriptions();
            }
            this.currentActionBar = previousFragment.actionBar;
            if (!previousFragment.hasOwnBackground && fragmentView.getBackground() == null) {
                fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
            if (needAnimation) {
                this.transitionAnimationStartTime = System.currentTimeMillis();
                this.transitionAnimationInProgress = true;
                this.layoutToIgnore = this.containerView;
                final BaseFragment previousFragmentFinal = previousFragment;
                currentFragment.setRemovingFromStack(true);
                this.onCloseAnimationEndRunnable = new Runnable() { // from class: org.telegram.ui.ActionBar.ActionBarLayout$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        ActionBarLayout.this.m1380x789344cd(currentFragment, previousFragmentFinal);
                    }
                };
                AnimatorSet animation = null;
                if (!this.inPreviewMode && !this.transitionAnimationPreviewMode) {
                    animation = currentFragment.onCustomTransitionAnimation(false, new Runnable() { // from class: org.telegram.ui.ActionBar.ActionBarLayout$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            ActionBarLayout.this.m1381x5dd4b38e();
                        }
                    });
                }
                if (animation == null) {
                    if (this.inPreviewMode || (!this.containerView.isKeyboardVisible && !this.containerViewBack.isKeyboardVisible)) {
                        startLayoutAnimation(false, true, this.inPreviewMode || this.transitionAnimationPreviewMode);
                    } else {
                        Runnable runnable = new Runnable() { // from class: org.telegram.ui.ActionBar.ActionBarLayout.9
                            @Override // java.lang.Runnable
                            public void run() {
                                if (ActionBarLayout.this.waitingForKeyboardCloseRunnable == this) {
                                    ActionBarLayout.this.waitingForKeyboardCloseRunnable = null;
                                    ActionBarLayout.this.startLayoutAnimation(false, true, false);
                                }
                            }
                        };
                        this.waitingForKeyboardCloseRunnable = runnable;
                        AndroidUtilities.runOnUIThread(runnable, 200L);
                    }
                } else {
                    this.currentAnimation = animation;
                    if (Bulletin.getVisibleBulletin() != null && Bulletin.getVisibleBulletin().isShowing()) {
                        Bulletin.getVisibleBulletin().hide();
                    }
                }
                onFragmentStackChanged();
                return;
            }
            closeLastFragmentInternalRemoveOld(currentFragment);
            currentFragment.onTransitionAnimationEnd(false, true);
            previousFragment.onTransitionAnimationEnd(true, true);
            previousFragment.onBecomeFullyVisible();
        } else if (this.useAlphaAnimations && !forceNoAnimation) {
            this.transitionAnimationStartTime = System.currentTimeMillis();
            this.transitionAnimationInProgress = true;
            this.layoutToIgnore = this.containerView;
            this.onCloseAnimationEndRunnable = new Runnable() { // from class: org.telegram.ui.ActionBar.ActionBarLayout$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    ActionBarLayout.this.m1382x4316224f(currentFragment);
                }
            };
            ArrayList<Animator> animators = new ArrayList<>();
            animators.add(ObjectAnimator.ofFloat(this, View.ALPHA, 1.0f, 0.0f));
            View view = this.backgroundView;
            if (view != null) {
                animators.add(ObjectAnimator.ofFloat(view, View.ALPHA, 1.0f, 0.0f));
            }
            AnimatorSet animatorSet = new AnimatorSet();
            this.currentAnimation = animatorSet;
            animatorSet.playTogether(animators);
            this.currentAnimation.setInterpolator(this.accelerateDecelerateInterpolator);
            this.currentAnimation.setDuration(200L);
            this.currentAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.ActionBarLayout.10
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation2) {
                    ActionBarLayout.this.transitionAnimationStartTime = System.currentTimeMillis();
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation2) {
                    ActionBarLayout.this.onAnimationEndCheck(false);
                }
            });
            this.currentAnimation.start();
        } else {
            removeFragmentFromStackInternal(currentFragment);
            setVisibility(8);
            View view2 = this.backgroundView;
            if (view2 != null) {
                view2.setVisibility(8);
            }
        }
    }

    /* renamed from: lambda$closeLastFragment$3$org-telegram-ui-ActionBar-ActionBarLayout */
    public /* synthetic */ void m1380x789344cd(BaseFragment currentFragment, BaseFragment previousFragmentFinal) {
        ViewGroup parent;
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.previewMenu;
        if (actionBarPopupWindowLayout != null && (parent = (ViewGroup) actionBarPopupWindowLayout.getParent()) != null) {
            parent.removeView(this.previewMenu);
        }
        if (this.inPreviewMode || this.transitionAnimationPreviewMode) {
            this.containerViewBack.setScaleX(1.0f);
            this.containerViewBack.setScaleY(1.0f);
            this.inPreviewMode = false;
            this.previewMenu = null;
            this.transitionAnimationPreviewMode = false;
        } else {
            this.containerViewBack.setTranslationX(0.0f);
        }
        closeLastFragmentInternalRemoveOld(currentFragment);
        currentFragment.setRemovingFromStack(false);
        currentFragment.onTransitionAnimationEnd(false, true);
        previousFragmentFinal.onTransitionAnimationEnd(true, true);
        previousFragmentFinal.onBecomeFullyVisible();
    }

    /* renamed from: lambda$closeLastFragment$4$org-telegram-ui-ActionBar-ActionBarLayout */
    public /* synthetic */ void m1381x5dd4b38e() {
        onAnimationEndCheck(false);
    }

    /* renamed from: lambda$closeLastFragment$5$org-telegram-ui-ActionBar-ActionBarLayout */
    public /* synthetic */ void m1382x4316224f(BaseFragment currentFragment) {
        removeFragmentFromStackInternal(currentFragment);
        setVisibility(8);
        View view = this.backgroundView;
        if (view != null) {
            view.setVisibility(8);
        }
        DrawerLayoutContainer drawerLayoutContainer = this.drawerLayoutContainer;
        if (drawerLayoutContainer != null) {
            drawerLayoutContainer.setAllowOpenDrawer(true, false);
        }
    }

    public void showFragment(int i) {
        ViewGroup parent;
        ViewGroup parent2;
        if (this.fragmentsStack.isEmpty()) {
            return;
        }
        for (int a = 0; a < i; a++) {
            BaseFragment previousFragment = this.fragmentsStack.get(a);
            if (previousFragment.actionBar != null && previousFragment.actionBar.shouldAddToContainer() && (parent2 = (ViewGroup) previousFragment.actionBar.getParent()) != null) {
                parent2.removeView(previousFragment.actionBar);
            }
            if (previousFragment.fragmentView != null && (parent = (ViewGroup) previousFragment.fragmentView.getParent()) != null) {
                previousFragment.onPause();
                previousFragment.onRemoveFromParent();
                parent.removeView(previousFragment.fragmentView);
            }
        }
        BaseFragment previousFragment2 = this.fragmentsStack.get(i);
        previousFragment2.setParentLayout(this);
        View fragmentView = previousFragment2.fragmentView;
        if (fragmentView == null) {
            fragmentView = previousFragment2.createView(this.parentActivity);
        } else {
            ViewGroup parent3 = (ViewGroup) fragmentView.getParent();
            if (parent3 != null) {
                previousFragment2.onRemoveFromParent();
                parent3.removeView(fragmentView);
            }
        }
        this.containerView.addView(fragmentView, LayoutHelper.createFrame(-1, -1.0f));
        if (previousFragment2.actionBar != null && previousFragment2.actionBar.shouldAddToContainer()) {
            if (this.removeActionBarExtraHeight) {
                previousFragment2.actionBar.setOccupyStatusBar(false);
            }
            ViewGroup parent4 = (ViewGroup) previousFragment2.actionBar.getParent();
            if (parent4 != null) {
                parent4.removeView(previousFragment2.actionBar);
            }
            this.containerView.addView(previousFragment2.actionBar);
            previousFragment2.actionBar.setTitleOverlayText(this.titleOverlayText, this.titleOverlayTextId, this.overlayAction);
        }
        previousFragment2.onResume();
        this.currentActionBar = previousFragment2.actionBar;
        if (!previousFragment2.hasOwnBackground && fragmentView.getBackground() == null) {
            fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        }
    }

    public void showLastFragment() {
        if (this.fragmentsStack.isEmpty()) {
            return;
        }
        showFragment(this.fragmentsStack.size() - 1);
    }

    private void removeFragmentFromStackInternal(BaseFragment fragment) {
        fragment.onPause();
        fragment.onFragmentDestroy();
        fragment.setParentLayout(null);
        this.fragmentsStack.remove(fragment);
        onFragmentStackChanged();
    }

    public void removeFragmentFromStack(int num) {
        if (num >= this.fragmentsStack.size()) {
            return;
        }
        removeFragmentFromStackInternal(this.fragmentsStack.get(num));
    }

    public void removeFragmentFromStack(BaseFragment fragment) {
        if (this.useAlphaAnimations && this.fragmentsStack.size() == 1 && AndroidUtilities.isTablet()) {
            closeLastFragment(true);
            return;
        }
        if (this.delegate != null && this.fragmentsStack.size() == 1 && AndroidUtilities.isTablet()) {
            this.delegate.needCloseLastFragment(this);
        }
        removeFragmentFromStackInternal(fragment);
    }

    public void removeAllFragments() {
        for (int a = 0; a < this.fragmentsStack.size(); a = (a - 1) + 1) {
            removeFragmentFromStackInternal(this.fragmentsStack.get(a));
        }
    }

    public void setThemeAnimationValue(float value) {
        this.themeAnimationValue = value;
        int N = this.themeAnimatorDescriptions.size();
        for (int j = 0; j < N; j++) {
            ArrayList<ThemeDescription> descriptions = this.themeAnimatorDescriptions.get(j);
            int[] startColors = this.animateStartColors.get(j);
            int[] endColors = this.animateEndColors.get(j);
            int i = 0;
            int b = descriptions.size();
            while (i < b) {
                int rE = Color.red(endColors[i]);
                int gE = Color.green(endColors[i]);
                int bE = Color.blue(endColors[i]);
                int aE = Color.alpha(endColors[i]);
                int rS = Color.red(startColors[i]);
                int gS = Color.green(startColors[i]);
                int bS = Color.blue(startColors[i]);
                int N2 = N;
                int aS = Color.alpha(startColors[i]);
                int[] startColors2 = startColors;
                int[] endColors2 = endColors;
                int a = Math.min(255, (int) (aS + ((aE - aS) * value)));
                int r = Math.min(255, (int) (rS + ((rE - rS) * value)));
                int N22 = b;
                int N23 = gE - gS;
                int g = Math.min(255, (int) (gS + (N23 * value)));
                int rE2 = bE - bS;
                int b2 = Math.min(255, (int) (bS + (rE2 * value)));
                int color = Color.argb(a, r, g, b2);
                ThemeDescription description = descriptions.get(i);
                description.setAnimatedColor(color);
                description.setColor(color, false, false);
                i++;
                startColors = startColors2;
                N = N2;
                endColors = endColors2;
                b = N22;
            }
        }
        int N3 = this.themeAnimatorDelegate.size();
        for (int j2 = 0; j2 < N3; j2++) {
            ThemeDescription.ThemeDescriptionDelegate delegate = this.themeAnimatorDelegate.get(j2);
            if (delegate != null) {
                delegate.didSetColor();
                delegate.onAnimationProgress(value);
            }
        }
        ArrayList<ThemeDescription> arrayList = this.presentingFragmentDescriptions;
        if (arrayList != null) {
            int N4 = arrayList.size();
            for (int i2 = 0; i2 < N4; i2++) {
                ThemeDescription description2 = this.presentingFragmentDescriptions.get(i2);
                String key = description2.getCurrentKey();
                description2.setColor(Theme.getColor(key), false, false);
            }
        }
        ThemeAnimationSettings.onAnimationProgress onanimationprogress = this.animationProgressListener;
        if (onanimationprogress != null) {
            onanimationprogress.setProgress(value);
        }
    }

    public float getThemeAnimationValue() {
        return this.themeAnimationValue;
    }

    private void addStartDescriptions(ArrayList<ThemeDescription> descriptions) {
        if (descriptions == null) {
            return;
        }
        this.themeAnimatorDescriptions.add(descriptions);
        int[] startColors = new int[descriptions.size()];
        this.animateStartColors.add(startColors);
        int N = descriptions.size();
        for (int a = 0; a < N; a++) {
            ThemeDescription description = descriptions.get(a);
            startColors[a] = description.getSetColor();
            ThemeDescription.ThemeDescriptionDelegate delegate = description.setDelegateDisabled();
            if (delegate != null && !this.themeAnimatorDelegate.contains(delegate)) {
                this.themeAnimatorDelegate.add(delegate);
            }
        }
    }

    private void addEndDescriptions(ArrayList<ThemeDescription> descriptions) {
        if (descriptions == null) {
            return;
        }
        int[] endColors = new int[descriptions.size()];
        this.animateEndColors.add(endColors);
        int N = descriptions.size();
        for (int a = 0; a < N; a++) {
            endColors[a] = descriptions.get(a).getSetColor();
        }
    }

    public void animateThemedValues(Theme.ThemeInfo theme, int accentId, boolean nightTheme, boolean instant) {
        animateThemedValues(new ThemeAnimationSettings(theme, accentId, nightTheme, instant));
    }

    public void animateThemedValues(final ThemeAnimationSettings settings) {
        BaseFragment fragment;
        if (this.transitionAnimationInProgress || this.startedTracking) {
            this.animateThemeAfterAnimation = true;
            this.animateSetThemeAfterAnimation = settings.theme;
            this.animateSetThemeNightAfterAnimation = settings.nightTheme;
            this.animateSetThemeAccentIdAfterAnimation = settings.accentId;
            return;
        }
        AnimatorSet animatorSet = this.themeAnimatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.themeAnimatorSet = null;
        }
        boolean startAnimation = false;
        int fragmentCount = settings.onlyTopFragment ? 1 : this.fragmentsStack.size();
        for (int i = 0; i < fragmentCount; i++) {
            if (i == 0) {
                fragment = getLastFragment();
            } else {
                if ((this.inPreviewMode || this.transitionAnimationPreviewMode) && this.fragmentsStack.size() > 1) {
                    ArrayList<BaseFragment> arrayList = this.fragmentsStack;
                    fragment = arrayList.get(arrayList.size() - 2);
                }
            }
            if (fragment != null) {
                startAnimation = true;
                if (settings.resourcesProvider != null) {
                    if (this.messageDrawableOutStart == null) {
                        Theme.MessageDrawable messageDrawable = new Theme.MessageDrawable(0, true, false, this.startColorsProvider);
                        this.messageDrawableOutStart = messageDrawable;
                        messageDrawable.isCrossfadeBackground = true;
                        Theme.MessageDrawable messageDrawable2 = new Theme.MessageDrawable(1, true, false, this.startColorsProvider);
                        this.messageDrawableOutMediaStart = messageDrawable2;
                        messageDrawable2.isCrossfadeBackground = true;
                    }
                    this.startColorsProvider.saveColors(settings.resourcesProvider);
                }
                ArrayList<ThemeDescription> descriptions = fragment.getThemeDescriptions();
                addStartDescriptions(descriptions);
                if (fragment.visibleDialog instanceof BottomSheet) {
                    BottomSheet sheet = (BottomSheet) fragment.visibleDialog;
                    addStartDescriptions(sheet.getThemeDescriptions());
                } else if (fragment.visibleDialog instanceof AlertDialog) {
                    AlertDialog dialog = (AlertDialog) fragment.visibleDialog;
                    addStartDescriptions(dialog.getThemeDescriptions());
                }
                if (i == 0) {
                    if (settings.applyTheme) {
                        if (settings.accentId != -1 && settings.theme != null) {
                            settings.theme.setCurrentAccentId(settings.accentId);
                            Theme.saveThemeAccents(settings.theme, true, false, true, false);
                        }
                        Theme.applyTheme(settings.theme, settings.nightTheme);
                    }
                    if (settings.afterStartDescriptionsAddedRunnable != null) {
                        settings.afterStartDescriptionsAddedRunnable.run();
                    }
                }
                addEndDescriptions(descriptions);
                if (fragment.visibleDialog instanceof BottomSheet) {
                    addEndDescriptions(((BottomSheet) fragment.visibleDialog).getThemeDescriptions());
                } else if (fragment.visibleDialog instanceof AlertDialog) {
                    addEndDescriptions(((AlertDialog) fragment.visibleDialog).getThemeDescriptions());
                }
            }
        }
        if (startAnimation) {
            if (!settings.onlyTopFragment) {
                int count = this.fragmentsStack.size() - ((this.inPreviewMode || this.transitionAnimationPreviewMode) ? 2 : 1);
                for (int a = 0; a < count; a++) {
                    BaseFragment fragment2 = this.fragmentsStack.get(a);
                    fragment2.clearViews();
                    fragment2.setParentLayout(this);
                }
            }
            if (settings.instant) {
                setThemeAnimationValue(1.0f);
                this.themeAnimatorDescriptions.clear();
                this.animateStartColors.clear();
                this.animateEndColors.clear();
                this.themeAnimatorDelegate.clear();
                this.presentingFragmentDescriptions = null;
                if (settings.afterAnimationRunnable != null) {
                    settings.afterAnimationRunnable.run();
                    return;
                }
                return;
            }
            Theme.setAnimatingColor(true);
            if (settings.beforeAnimationRunnable != null) {
                settings.beforeAnimationRunnable.run();
            }
            ThemeAnimationSettings.onAnimationProgress onanimationprogress = settings.animationProgress;
            this.animationProgressListener = onanimationprogress;
            if (onanimationprogress != null) {
                onanimationprogress.setProgress(0.0f);
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.themeAnimatorSet = animatorSet2;
            animatorSet2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.ActionBarLayout.11
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (animation.equals(ActionBarLayout.this.themeAnimatorSet)) {
                        ActionBarLayout.this.themeAnimatorDescriptions.clear();
                        ActionBarLayout.this.animateStartColors.clear();
                        ActionBarLayout.this.animateEndColors.clear();
                        ActionBarLayout.this.themeAnimatorDelegate.clear();
                        Theme.setAnimatingColor(false);
                        ActionBarLayout.this.presentingFragmentDescriptions = null;
                        ActionBarLayout.this.themeAnimatorSet = null;
                        if (settings.afterAnimationRunnable != null) {
                            settings.afterAnimationRunnable.run();
                        }
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    if (animation.equals(ActionBarLayout.this.themeAnimatorSet)) {
                        ActionBarLayout.this.themeAnimatorDescriptions.clear();
                        ActionBarLayout.this.animateStartColors.clear();
                        ActionBarLayout.this.animateEndColors.clear();
                        ActionBarLayout.this.themeAnimatorDelegate.clear();
                        Theme.setAnimatingColor(false);
                        ActionBarLayout.this.presentingFragmentDescriptions = null;
                        ActionBarLayout.this.themeAnimatorSet = null;
                        if (settings.afterAnimationRunnable != null) {
                            settings.afterAnimationRunnable.run();
                        }
                    }
                }
            });
            this.themeAnimatorSet.playTogether(ObjectAnimator.ofFloat(this, "themeAnimationValue", 0.0f, 1.0f));
            this.themeAnimatorSet.setDuration(settings.duration);
            this.themeAnimatorSet.start();
        }
    }

    public void rebuildLogout() {
        this.containerView.removeAllViews();
        this.containerViewBack.removeAllViews();
        this.currentActionBar = null;
        this.newFragment = null;
        this.oldFragment = null;
    }

    public void rebuildAllFragmentViews(boolean last, boolean showLastAfter) {
        if (this.transitionAnimationInProgress || this.startedTracking) {
            this.rebuildAfterAnimation = true;
            this.rebuildLastAfterAnimation = last;
            this.showLastAfterAnimation = showLastAfter;
            return;
        }
        int size = this.fragmentsStack.size();
        if (!last) {
            size--;
        }
        if (this.inPreviewMode) {
            size--;
        }
        for (int a = 0; a < size; a++) {
            this.fragmentsStack.get(a).clearViews();
            this.fragmentsStack.get(a).setParentLayout(this);
        }
        ActionBarLayoutDelegate actionBarLayoutDelegate = this.delegate;
        if (actionBarLayoutDelegate != null) {
            actionBarLayoutDelegate.onRebuildAllFragments(this, last);
        }
        if (showLastAfter) {
            showLastFragment();
        }
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        ActionBar actionBar;
        if (keyCode == 82 && !checkTransitionAnimation() && !this.startedTracking && (actionBar = this.currentActionBar) != null) {
            actionBar.onMenuButtonPressed();
        }
        return super.onKeyUp(keyCode, event);
    }

    public void onActionModeStarted(Object mode) {
        ActionBar actionBar = this.currentActionBar;
        if (actionBar != null) {
            actionBar.setVisibility(8);
        }
        this.inActionMode = true;
    }

    public void onActionModeFinished(Object mode) {
        ActionBar actionBar = this.currentActionBar;
        if (actionBar != null) {
            actionBar.setVisibility(0);
        }
        this.inActionMode = false;
    }

    private void onCloseAnimationEnd() {
        if (this.transitionAnimationInProgress && this.onCloseAnimationEndRunnable != null) {
            this.transitionAnimationInProgress = false;
            this.layoutToIgnore = null;
            this.transitionAnimationPreviewMode = false;
            this.transitionAnimationStartTime = 0L;
            this.newFragment = null;
            this.oldFragment = null;
            Runnable endRunnable = this.onCloseAnimationEndRunnable;
            this.onCloseAnimationEndRunnable = null;
            endRunnable.run();
            checkNeedRebuild();
            checkNeedRebuild();
        }
    }

    private void checkNeedRebuild() {
        if (this.rebuildAfterAnimation) {
            rebuildAllFragmentViews(this.rebuildLastAfterAnimation, this.showLastAfterAnimation);
            this.rebuildAfterAnimation = false;
        } else if (this.animateThemeAfterAnimation) {
            animateThemedValues(this.animateSetThemeAfterAnimation, this.animateSetThemeAccentIdAfterAnimation, this.animateSetThemeNightAfterAnimation, false);
            this.animateSetThemeAfterAnimation = null;
            this.animateThemeAfterAnimation = false;
        }
    }

    private void onOpenAnimationEnd() {
        if (this.transitionAnimationInProgress && this.onOpenAnimationEndRunnable != null) {
            this.transitionAnimationInProgress = false;
            this.layoutToIgnore = null;
            this.transitionAnimationPreviewMode = false;
            this.transitionAnimationStartTime = 0L;
            this.newFragment = null;
            this.oldFragment = null;
            Runnable endRunnable = this.onOpenAnimationEndRunnable;
            this.onOpenAnimationEndRunnable = null;
            endRunnable.run();
            checkNeedRebuild();
        }
    }

    public void startActivityForResult(Intent intent, int requestCode) {
        if (this.parentActivity == null) {
            return;
        }
        if (this.transitionAnimationInProgress) {
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.currentAnimation = null;
            }
            if (this.onCloseAnimationEndRunnable != null) {
                onCloseAnimationEnd();
            } else if (this.onOpenAnimationEndRunnable != null) {
                onOpenAnimationEnd();
            }
            this.containerView.invalidate();
        }
        if (intent != null) {
            this.parentActivity.startActivityForResult(intent, requestCode);
        }
    }

    public void setUseAlphaAnimations(boolean value) {
        this.useAlphaAnimations = value;
    }

    public void setBackgroundView(View view) {
        this.backgroundView = view;
    }

    public void setDrawerLayoutContainer(DrawerLayoutContainer layout) {
        this.drawerLayoutContainer = layout;
    }

    public DrawerLayoutContainer getDrawerLayoutContainer() {
        return this.drawerLayoutContainer;
    }

    public void setRemoveActionBarExtraHeight(boolean value) {
        this.removeActionBarExtraHeight = value;
    }

    public void setTitleOverlayText(String title, int titleId, Runnable action) {
        this.titleOverlayText = title;
        this.titleOverlayTextId = titleId;
        this.overlayAction = action;
        for (int a = 0; a < this.fragmentsStack.size(); a++) {
            BaseFragment fragment = this.fragmentsStack.get(a);
            if (fragment.actionBar != null) {
                fragment.actionBar.setTitleOverlayText(this.titleOverlayText, this.titleOverlayTextId, action);
            }
        }
    }

    public boolean extendActionMode(Menu menu) {
        if (!this.fragmentsStack.isEmpty()) {
            ArrayList<BaseFragment> arrayList = this.fragmentsStack;
            if (arrayList.get(arrayList.size() - 1).extendActionMode(menu)) {
                return true;
            }
        }
        return false;
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    public void setFragmentPanTranslationOffset(int offset) {
        LayoutContainer layoutContainer = this.containerView;
        if (layoutContainer != null) {
            layoutContainer.setFragmentPanTranslationOffset(offset);
        }
    }

    private View findScrollingChild(ViewGroup parent, float x, float y) {
        View v;
        int n = parent.getChildCount();
        for (int i = 0; i < n; i++) {
            View child = parent.getChildAt(i);
            if (child.getVisibility() == 0) {
                child.getHitRect(this.rect);
                if (!this.rect.contains((int) x, (int) y)) {
                    continue;
                } else if (child.canScrollHorizontally(-1)) {
                    return child;
                } else {
                    if ((child instanceof ViewGroup) && (v = findScrollingChild((ViewGroup) child, x - this.rect.left, y - this.rect.top)) != null) {
                        return v;
                    }
                }
            }
        }
        return null;
    }

    /* loaded from: classes4.dex */
    public class StartColorsProvider implements Theme.ResourcesProvider {
        HashMap<String, Integer> colors;
        String[] keysToSave;

        @Override // org.telegram.ui.ActionBar.Theme.ResourcesProvider
        public /* synthetic */ void applyServiceShaderMatrix(int i, int i2, float f, float f2) {
            Theme.applyServiceShaderMatrix(i, i2, f, f2);
        }

        @Override // org.telegram.ui.ActionBar.Theme.ResourcesProvider
        public /* synthetic */ int getColorOrDefault(String str) {
            return getColor(str);
        }

        @Override // org.telegram.ui.ActionBar.Theme.ResourcesProvider
        public /* synthetic */ Drawable getDrawable(String str) {
            return Theme.ResourcesProvider.CC.$default$getDrawable(this, str);
        }

        @Override // org.telegram.ui.ActionBar.Theme.ResourcesProvider
        public /* synthetic */ Paint getPaint(String str) {
            return Theme.ResourcesProvider.CC.$default$getPaint(this, str);
        }

        @Override // org.telegram.ui.ActionBar.Theme.ResourcesProvider
        public /* synthetic */ boolean hasGradientService() {
            return Theme.ResourcesProvider.CC.$default$hasGradientService(this);
        }

        @Override // org.telegram.ui.ActionBar.Theme.ResourcesProvider
        public /* synthetic */ void setAnimatedColor(String str, int i) {
            Theme.ResourcesProvider.CC.$default$setAnimatedColor(this, str, i);
        }

        private StartColorsProvider() {
            ActionBarLayout.this = r3;
            this.colors = new HashMap<>();
            this.keysToSave = new String[]{Theme.key_chat_outBubble, Theme.key_chat_outBubbleGradient1, Theme.key_chat_outBubbleGradient2, Theme.key_chat_outBubbleGradient3, Theme.key_chat_outBubbleGradientAnimated, Theme.key_chat_outBubbleShadow};
        }

        @Override // org.telegram.ui.ActionBar.Theme.ResourcesProvider
        public Integer getColor(String key) {
            return this.colors.get(key);
        }

        @Override // org.telegram.ui.ActionBar.Theme.ResourcesProvider
        public Integer getCurrentColor(String key) {
            return this.colors.get(key);
        }

        public void saveColors(Theme.ResourcesProvider fragmentResourceProvider) {
            String[] strArr;
            this.colors.clear();
            for (String key : this.keysToSave) {
                this.colors.put(key, fragmentResourceProvider.getCurrentColor(key));
            }
        }
    }
}
