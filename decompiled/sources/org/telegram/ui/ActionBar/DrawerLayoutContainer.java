package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.DisplayCutout;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
/* loaded from: classes4.dex */
public class DrawerLayoutContainer extends FrameLayout {
    private static final int MIN_DRAWER_MARGIN = 64;
    private boolean allowOpenDrawer;
    private boolean beginTrackingSent;
    private int behindKeyboardColor;
    private AnimatorSet currentAnimation;
    private boolean drawCurrentPreviewFragmentAbove;
    private ViewGroup drawerLayout;
    private boolean drawerOpened;
    private float drawerPosition;
    private boolean hasCutout;
    private int imeHeight;
    private boolean inLayout;
    private boolean keyboardVisibility;
    private Object lastInsets;
    private boolean maybeStartTracking;
    private int paddingTop;
    private ActionBarLayout parentActionBarLayout;
    private BitmapDrawable previewBlurDrawable;
    private PreviewForegroundDrawable previewForegroundDrawable;
    private float scrimOpacity;
    private Drawable shadowLeft;
    private float startY;
    private boolean startedTracking;
    private int startedTrackingPointerId;
    private int startedTrackingX;
    private int startedTrackingY;
    private VelocityTracker velocityTracker;
    private Rect rect = new Rect();
    private Paint scrimPaint = new Paint();
    private Paint backgroundPaint = new Paint();
    private boolean allowOpenDrawerBySwipe = true;
    private boolean allowDrawContent = true;
    private boolean firstLayout = true;
    private int minDrawerMargin = (int) ((AndroidUtilities.density * 64.0f) + 0.5f);

    public DrawerLayoutContainer(Context context) {
        super(context);
        setDescendantFocusability(262144);
        setFocusableInTouchMode(true);
        if (Build.VERSION.SDK_INT >= 21) {
            setFitsSystemWindows(true);
            setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() { // from class: org.telegram.ui.ActionBar.DrawerLayoutContainer$$ExternalSyntheticLambda0
                @Override // android.view.View.OnApplyWindowInsetsListener
                public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                    return DrawerLayoutContainer.this.m1422lambda$new$0$orgtelegramuiActionBarDrawerLayoutContainer(view, windowInsets);
                }
            });
            setSystemUiVisibility(1280);
        }
        this.shadowLeft = getResources().getDrawable(R.drawable.menu_shadow);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-ActionBar-DrawerLayoutContainer */
    public /* synthetic */ WindowInsets m1422lambda$new$0$orgtelegramuiActionBarDrawerLayoutContainer(View v, WindowInsets insets) {
        if (Build.VERSION.SDK_INT >= 30) {
            boolean newKeyboardVisibility = insets.isVisible(WindowInsets.Type.ime());
            int imeHeight = insets.getInsets(WindowInsets.Type.ime()).bottom;
            if (this.keyboardVisibility != newKeyboardVisibility || this.imeHeight != imeHeight) {
                this.keyboardVisibility = newKeyboardVisibility;
                this.imeHeight = imeHeight;
                requestLayout();
            }
        }
        DrawerLayoutContainer drawerLayoutContainer = (DrawerLayoutContainer) v;
        if (AndroidUtilities.statusBarHeight != insets.getSystemWindowInsetTop()) {
            drawerLayoutContainer.requestLayout();
        }
        int newTopInset = insets.getSystemWindowInsetTop();
        if ((newTopInset != 0 || AndroidUtilities.isInMultiwindow || this.firstLayout) && AndroidUtilities.statusBarHeight != newTopInset) {
            AndroidUtilities.statusBarHeight = newTopInset;
        }
        boolean z = false;
        this.firstLayout = false;
        this.lastInsets = insets;
        drawerLayoutContainer.setWillNotDraw(insets.getSystemWindowInsetTop() <= 0 && getBackground() == null);
        if (Build.VERSION.SDK_INT >= 28) {
            DisplayCutout cutout = insets.getDisplayCutout();
            if (cutout != null && cutout.getBoundingRects().size() != 0) {
                z = true;
            }
            this.hasCutout = z;
        }
        invalidate();
        if (Build.VERSION.SDK_INT >= 30) {
            return WindowInsets.CONSUMED;
        }
        return insets.consumeSystemWindowInsets();
    }

    private void dispatchChildInsets(View child, Object insets, int drawerGravity) {
        WindowInsets wi = (WindowInsets) insets;
        if (drawerGravity == 3) {
            wi = wi.replaceSystemWindowInsets(wi.getSystemWindowInsetLeft(), wi.getSystemWindowInsetTop(), 0, wi.getSystemWindowInsetBottom());
        } else if (drawerGravity == 5) {
            wi = wi.replaceSystemWindowInsets(0, wi.getSystemWindowInsetTop(), wi.getSystemWindowInsetRight(), wi.getSystemWindowInsetBottom());
        }
        child.dispatchApplyWindowInsets(wi);
    }

    private void applyMarginInsets(ViewGroup.MarginLayoutParams lp, Object insets, int drawerGravity, boolean topOnly) {
        WindowInsets wi = (WindowInsets) insets;
        int i = 0;
        if (drawerGravity == 3) {
            wi = wi.replaceSystemWindowInsets(wi.getSystemWindowInsetLeft(), wi.getSystemWindowInsetTop(), 0, wi.getSystemWindowInsetBottom());
        } else if (drawerGravity == 5) {
            wi = wi.replaceSystemWindowInsets(0, wi.getSystemWindowInsetTop(), wi.getSystemWindowInsetRight(), wi.getSystemWindowInsetBottom());
        }
        lp.leftMargin = wi.getSystemWindowInsetLeft();
        if (!topOnly) {
            i = wi.getSystemWindowInsetTop();
        }
        lp.topMargin = i;
        lp.rightMargin = wi.getSystemWindowInsetRight();
        lp.bottomMargin = wi.getSystemWindowInsetBottom();
    }

    private int getTopInset(Object insets) {
        if (Build.VERSION.SDK_INT < 21 || insets == null) {
            return 0;
        }
        return ((WindowInsets) insets).getSystemWindowInsetTop();
    }

    public void setDrawerLayout(ViewGroup layout) {
        this.drawerLayout = layout;
        addView(layout);
        this.drawerLayout.setVisibility(4);
        if (Build.VERSION.SDK_INT >= 21) {
            this.drawerLayout.setFitsSystemWindows(true);
        }
    }

    public void moveDrawerByX(float dx) {
        setDrawerPosition(this.drawerPosition + dx);
    }

    public void setDrawerPosition(float value) {
        ViewGroup viewGroup = this.drawerLayout;
        if (viewGroup == null) {
            return;
        }
        this.drawerPosition = value;
        if (value > viewGroup.getMeasuredWidth()) {
            this.drawerPosition = this.drawerLayout.getMeasuredWidth();
        } else if (this.drawerPosition < 0.0f) {
            this.drawerPosition = 0.0f;
        }
        this.drawerLayout.setTranslationX(this.drawerPosition);
        int newVisibility = this.drawerPosition > 0.0f ? 0 : 4;
        if (this.drawerLayout.getVisibility() != newVisibility) {
            this.drawerLayout.setVisibility(newVisibility);
        }
        if (!this.parentActionBarLayout.fragmentsStack.isEmpty()) {
            BaseFragment currentFragment = this.parentActionBarLayout.fragmentsStack.get(0);
            if (this.drawerPosition == this.drawerLayout.getMeasuredWidth()) {
                currentFragment.setProgressToDrawerOpened(1.0f);
            } else {
                float f = this.drawerPosition;
                if (f == 0.0f) {
                    currentFragment.setProgressToDrawerOpened(0.0f);
                } else {
                    currentFragment.setProgressToDrawerOpened(f / this.drawerLayout.getMeasuredWidth());
                }
            }
        }
        setScrimOpacity(this.drawerPosition / this.drawerLayout.getMeasuredWidth());
    }

    public float getDrawerPosition() {
        return this.drawerPosition;
    }

    public void cancelCurrentAnimation() {
        AnimatorSet animatorSet = this.currentAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.currentAnimation = null;
        }
    }

    public void openDrawer(boolean fast) {
        ActionBarLayout actionBarLayout;
        if (!this.allowOpenDrawer || this.drawerLayout == null) {
            return;
        }
        if (AndroidUtilities.isTablet() && (actionBarLayout = this.parentActionBarLayout) != null && actionBarLayout.parentActivity != null) {
            AndroidUtilities.hideKeyboard(this.parentActionBarLayout.parentActivity.getCurrentFocus());
        }
        cancelCurrentAnimation();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(this, "drawerPosition", this.drawerLayout.getMeasuredWidth()));
        animatorSet.setInterpolator(new DecelerateInterpolator());
        if (fast) {
            animatorSet.setDuration(Math.max((int) ((200.0f / this.drawerLayout.getMeasuredWidth()) * (this.drawerLayout.getMeasuredWidth() - this.drawerPosition)), 50));
        } else {
            animatorSet.setDuration(250L);
        }
        animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.DrawerLayoutContainer.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                DrawerLayoutContainer.this.onDrawerAnimationEnd(true);
            }
        });
        animatorSet.start();
        this.currentAnimation = animatorSet;
    }

    public void closeDrawer(boolean fast) {
        if (this.drawerLayout == null) {
            return;
        }
        cancelCurrentAnimation();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(this, "drawerPosition", 0.0f));
        animatorSet.setInterpolator(new DecelerateInterpolator());
        if (fast) {
            animatorSet.setDuration(Math.max((int) ((200.0f / this.drawerLayout.getMeasuredWidth()) * this.drawerPosition), 50));
        } else {
            animatorSet.setDuration(250L);
        }
        animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.DrawerLayoutContainer.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                DrawerLayoutContainer.this.onDrawerAnimationEnd(false);
            }
        });
        animatorSet.start();
    }

    public void onDrawerAnimationEnd(boolean opened) {
        this.startedTracking = false;
        this.currentAnimation = null;
        this.drawerOpened = opened;
        if (Build.VERSION.SDK_INT >= 19) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child != this.drawerLayout) {
                    child.setImportantForAccessibility(opened ? 4 : 0);
                }
            }
        }
        sendAccessibilityEvent(32);
    }

    private void setScrimOpacity(float value) {
        this.scrimOpacity = value;
        invalidate();
    }

    private float getScrimOpacity() {
        return this.scrimOpacity;
    }

    public View getDrawerLayout() {
        return this.drawerLayout;
    }

    public void setParentActionBarLayout(ActionBarLayout layout) {
        this.parentActionBarLayout = layout;
    }

    public void presentFragment(BaseFragment fragment) {
        ActionBarLayout actionBarLayout = this.parentActionBarLayout;
        if (actionBarLayout != null) {
            actionBarLayout.presentFragment(fragment);
        }
        closeDrawer(false);
    }

    public void closeDrawer() {
        if (this.drawerPosition != 0.0f) {
            setDrawerPosition(0.0f);
            onDrawerAnimationEnd(false);
        }
    }

    public void setAllowOpenDrawer(boolean value, boolean animated) {
        this.allowOpenDrawer = value;
        if (!value && this.drawerPosition != 0.0f) {
            if (!animated) {
                setDrawerPosition(0.0f);
                onDrawerAnimationEnd(false);
                return;
            }
            closeDrawer(true);
        }
    }

    public boolean isAllowOpenDrawer() {
        return this.allowOpenDrawer;
    }

    public void setAllowOpenDrawerBySwipe(boolean value) {
        this.allowOpenDrawerBySwipe = value;
    }

    private void prepareForDrawerOpen(MotionEvent ev) {
        this.maybeStartTracking = false;
        this.startedTracking = true;
        if (ev != null) {
            this.startedTrackingX = (int) ev.getX();
        }
        this.beginTrackingSent = false;
    }

    public boolean isDrawerOpened() {
        return this.drawerOpened;
    }

    public void setAllowDrawContent(boolean value) {
        if (this.allowDrawContent != value) {
            this.allowDrawContent = value;
            invalidate();
        }
    }

    public boolean isDrawCurrentPreviewFragmentAbove() {
        return this.drawCurrentPreviewFragmentAbove;
    }

    public void setDrawCurrentPreviewFragmentAbove(boolean drawCurrentPreviewFragmentAbove) {
        if (this.drawCurrentPreviewFragmentAbove != drawCurrentPreviewFragmentAbove) {
            this.drawCurrentPreviewFragmentAbove = drawCurrentPreviewFragmentAbove;
            if (drawCurrentPreviewFragmentAbove) {
                createBlurDrawable();
                this.previewForegroundDrawable = new PreviewForegroundDrawable();
            } else {
                this.startY = 0.0f;
                this.previewBlurDrawable = null;
                this.previewForegroundDrawable = null;
            }
            invalidate();
        }
    }

    private void createBlurDrawable() {
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        int w = (int) (measuredWidth / 6.0f);
        int h = (int) (measuredHeight / 6.0f);
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.scale(0.16666667f, 0.16666667f);
        draw(canvas);
        Utilities.stackBlurBitmap(bitmap, Math.max(7, Math.max(w, h) / 180));
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
        this.previewBlurDrawable = bitmapDrawable;
        bitmapDrawable.setBounds(0, 0, measuredWidth, measuredHeight);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (this.drawCurrentPreviewFragmentAbove && this.parentActionBarLayout != null) {
            int action = ev.getActionMasked();
            if (action == 2) {
                float f = this.startY;
                if (f == 0.0f) {
                    this.startY = ev.getY();
                    MotionEvent event = MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0);
                    super.dispatchTouchEvent(event);
                    event.recycle();
                } else {
                    this.parentActionBarLayout.movePreviewFragment(f - ev.getY());
                }
            } else if (action == 1 || action == 6 || action == 3) {
                this.parentActionBarLayout.finishPreviewFragment();
            }
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    /* JADX WARN: Code restructure failed: missing block: B:98:0x01a0, code lost:
        if (r0 != r9.drawerLayout.getMeasuredWidth()) goto L99;
     */
    /* JADX WARN: Removed duplicated region for block: B:126:0x020d  */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onTouchEvent(android.view.MotionEvent r10) {
        /*
            Method dump skipped, instructions count: 578
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.DrawerLayoutContainer.onTouchEvent(android.view.MotionEvent):boolean");
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

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return this.parentActionBarLayout.checkTransitionAnimation() || onTouchEvent(ev);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (this.maybeStartTracking && !this.startedTracking) {
            onTouchEvent(null);
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        this.inLayout = true;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                if (BuildVars.DEBUG_VERSION) {
                    if (this.drawerLayout != child) {
                        child.layout(lp.leftMargin, lp.topMargin + getPaddingTop(), lp.leftMargin + child.getMeasuredWidth(), lp.topMargin + child.getMeasuredHeight() + getPaddingTop());
                    } else {
                        child.layout(-child.getMeasuredWidth(), lp.topMargin + getPaddingTop(), 0, lp.topMargin + child.getMeasuredHeight() + getPaddingTop());
                    }
                } else {
                    try {
                        if (this.drawerLayout != child) {
                            child.layout(lp.leftMargin, lp.topMargin + getPaddingTop(), lp.leftMargin + child.getMeasuredWidth(), lp.topMargin + child.getMeasuredHeight() + getPaddingTop());
                        } else {
                            child.layout(-child.getMeasuredWidth(), lp.topMargin + getPaddingTop(), 0, lp.topMargin + child.getMeasuredHeight() + getPaddingTop());
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
            }
        }
        this.inLayout = false;
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (!this.inLayout) {
            super.requestLayout();
        }
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int contentHeightSpec;
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
        if (Build.VERSION.SDK_INT < 21) {
            this.inLayout = true;
            if (heightSize == AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight) {
                if (getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    setPadding(0, AndroidUtilities.statusBarHeight, 0, 0);
                }
                heightSize = AndroidUtilities.displaySize.y;
            } else if (getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                setPadding(0, 0, 0, 0);
            }
            this.inLayout = false;
        } else {
            int newSize = heightSize - AndroidUtilities.statusBarHeight;
            if (newSize > 0 && newSize < 4096) {
                AndroidUtilities.displaySize.y = newSize;
            }
        }
        boolean applyInsets = this.lastInsets != null && Build.VERSION.SDK_INT >= 21;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                if (applyInsets) {
                    if (child.getFitsSystemWindows()) {
                        dispatchChildInsets(child, this.lastInsets, lp.gravity);
                    } else if (child.getTag() == null) {
                        applyMarginInsets(lp, this.lastInsets, lp.gravity, Build.VERSION.SDK_INT >= 21);
                    }
                }
                if (this.drawerLayout != child) {
                    int contentWidthSpec = View.MeasureSpec.makeMeasureSpec((widthSize - lp.leftMargin) - lp.rightMargin, C.BUFFER_FLAG_ENCRYPTED);
                    if (lp.height > 0) {
                        contentHeightSpec = lp.height;
                    } else {
                        contentHeightSpec = View.MeasureSpec.makeMeasureSpec((heightSize - lp.topMargin) - lp.bottomMargin, C.BUFFER_FLAG_ENCRYPTED);
                    }
                    child.measure(contentWidthSpec, contentHeightSpec);
                } else {
                    child.setPadding(0, 0, 0, 0);
                    int drawerWidthSpec = getChildMeasureSpec(widthMeasureSpec, this.minDrawerMargin + lp.leftMargin + lp.rightMargin, lp.width);
                    int drawerHeightSpec = getChildMeasureSpec(heightMeasureSpec, lp.topMargin + lp.bottomMargin, lp.height);
                    child.measure(drawerWidthSpec, drawerHeightSpec);
                }
            }
        }
    }

    public void setBehindKeyboardColor(int color) {
        this.behindKeyboardColor = color;
        invalidate();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        ActionBarLayout actionBarLayout;
        super.dispatchDraw(canvas);
        if (this.drawCurrentPreviewFragmentAbove && (actionBarLayout = this.parentActionBarLayout) != null) {
            BitmapDrawable bitmapDrawable = this.previewBlurDrawable;
            if (bitmapDrawable != null) {
                bitmapDrawable.setAlpha((int) (actionBarLayout.getCurrentPreviewFragmentAlpha() * 255.0f));
                this.previewBlurDrawable.draw(canvas);
            }
            this.parentActionBarLayout.drawCurrentPreviewFragment(canvas, Build.VERSION.SDK_INT >= 21 ? this.previewForegroundDrawable : null);
        }
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        int clipLeft;
        int lastVisibleChild;
        int vright;
        if (!this.allowDrawContent) {
            return false;
        }
        int height = getHeight();
        boolean drawingContent = child != this.drawerLayout;
        int lastVisibleChild2 = 0;
        int clipLeft2 = 0;
        int clipRight = getWidth();
        int restoreCount = canvas.save();
        if (!drawingContent) {
            lastVisibleChild = 0;
            clipLeft = 0;
        } else {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View v = getChildAt(i);
                if (v.getVisibility() == 0 && v != this.drawerLayout) {
                    lastVisibleChild2 = i;
                }
                if (v != child && v.getVisibility() == 0 && v == this.drawerLayout && v.getHeight() >= height && (vright = ((int) Math.ceil(v.getX())) + v.getMeasuredWidth()) > clipLeft2) {
                    clipLeft2 = vright;
                }
            }
            if (clipLeft2 != 0) {
                canvas.clipRect(clipLeft2 - AndroidUtilities.dp(1.0f), 0, clipRight, getHeight());
            }
            lastVisibleChild = lastVisibleChild2;
            clipLeft = clipLeft2;
        }
        boolean result = super.drawChild(canvas, child, drawingTime);
        canvas.restoreToCount(restoreCount);
        if (this.scrimOpacity > 0.0f && drawingContent) {
            if (indexOfChild(child) == lastVisibleChild) {
                this.scrimPaint.setColor(((int) (this.scrimOpacity * 153.0f)) << 24);
                canvas.drawRect(clipLeft, 0.0f, clipRight, getHeight(), this.scrimPaint);
            }
        } else if (this.shadowLeft != null) {
            float alpha = Math.max(0.0f, Math.min(this.drawerPosition / AndroidUtilities.dp(20.0f), 1.0f));
            if (alpha != 0.0f) {
                this.shadowLeft.setBounds((int) this.drawerPosition, child.getTop(), ((int) this.drawerPosition) + this.shadowLeft.getIntrinsicWidth(), child.getBottom());
                this.shadowLeft.setAlpha((int) (255.0f * alpha));
                this.shadowLeft.draw(canvas);
            }
        }
        return result;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        Object obj;
        if (Build.VERSION.SDK_INT >= 21 && (obj = this.lastInsets) != null) {
            WindowInsets insets = (WindowInsets) obj;
            int bottomInset = insets.getSystemWindowInsetBottom();
            if (bottomInset > 0) {
                this.backgroundPaint.setColor(this.behindKeyboardColor);
                canvas.drawRect(0.0f, getMeasuredHeight() - bottomInset, getMeasuredWidth(), getMeasuredHeight(), this.backgroundPaint);
            }
            if (this.hasCutout) {
                this.backgroundPaint.setColor(-16777216);
                int left = insets.getSystemWindowInsetLeft();
                if (left != 0) {
                    canvas.drawRect(0.0f, 0.0f, left, getMeasuredHeight(), this.backgroundPaint);
                }
                int right = insets.getSystemWindowInsetRight();
                if (right != 0) {
                    canvas.drawRect(right, 0.0f, getMeasuredWidth(), getMeasuredHeight(), this.backgroundPaint);
                }
            }
        }
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override // android.view.ViewGroup
    public boolean onRequestSendAccessibilityEvent(View child, AccessibilityEvent event) {
        if (this.drawerOpened && child != this.drawerLayout) {
            return false;
        }
        return super.onRequestSendAccessibilityEvent(child, event);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class PreviewForegroundDrawable extends Drawable {
        private final GradientDrawable bottomDrawable;
        private final GradientDrawable topDrawable;

        public PreviewForegroundDrawable() {
            GradientDrawable gradientDrawable = new GradientDrawable();
            this.topDrawable = gradientDrawable;
            gradientDrawable.setStroke(AndroidUtilities.dp(1.0f), Theme.getColor(Theme.key_actionBarDefault));
            gradientDrawable.setCornerRadius(AndroidUtilities.dp(6.0f));
            GradientDrawable gradientDrawable2 = new GradientDrawable();
            this.bottomDrawable = gradientDrawable2;
            gradientDrawable2.setStroke(1, Theme.getColor(Theme.key_divider));
            gradientDrawable2.setCornerRadius(AndroidUtilities.dp(6.0f));
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            Rect bounds = getBounds();
            canvas.save();
            canvas.clipRect(bounds.left, bounds.top, bounds.right, bounds.top + ActionBar.getCurrentActionBarHeight());
            this.topDrawable.draw(canvas);
            canvas.restore();
            canvas.save();
            canvas.clipRect(bounds.left, bounds.top + ActionBar.getCurrentActionBarHeight(), bounds.right, bounds.bottom);
            this.bottomDrawable.draw(canvas);
            canvas.restore();
        }

        @Override // android.graphics.drawable.Drawable
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            this.topDrawable.setBounds(bounds);
            this.bottomDrawable.setBounds(bounds);
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int i) {
            this.topDrawable.setAlpha(i);
            this.bottomDrawable.setAlpha(i);
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
        }

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return -3;
        }
    }
}
