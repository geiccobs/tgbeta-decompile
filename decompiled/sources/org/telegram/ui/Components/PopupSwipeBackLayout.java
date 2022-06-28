package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.SparseIntArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.core.view.GestureDetectorCompat;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class PopupSwipeBackLayout extends FrameLayout {
    private static final int DURATION = 300;
    private GestureDetectorCompat detector;
    private ValueAnimator foregroundAnimator;
    private boolean isAnimationInProgress;
    private boolean isProcessingSwipe;
    private boolean isSwipeBackDisallowed;
    private boolean isSwipeDisallowed;
    float lastToProgress;
    float lastTransitionProgress;
    private int notificationIndex;
    private IntCallback onHeightUpdateListener;
    private float overrideForegroundHeight;
    Theme.ResourcesProvider resourcesProvider;
    public float transitionProgress;
    SparseIntArray overrideHeightIndex = new SparseIntArray();
    private float toProgress = -1.0f;
    private Paint overlayPaint = new Paint(1);
    private Paint foregroundPaint = new Paint();
    private int foregroundColor = 0;
    private Path mPath = new Path();
    private RectF mRect = new RectF();
    private ArrayList<OnSwipeBackProgressListener> onSwipeBackProgressListeners = new ArrayList<>();
    private int currentForegroundIndex = -1;
    private int lastHeightReported = -1;
    private android.graphics.Rect hitRect = new android.graphics.Rect();

    /* loaded from: classes5.dex */
    public interface IntCallback {
        void run(int i);
    }

    /* loaded from: classes5.dex */
    public interface OnSwipeBackProgressListener {
        void onSwipeBackProgress(PopupSwipeBackLayout popupSwipeBackLayout, float f, float f2);
    }

    public PopupSwipeBackLayout(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        final int touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.detector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() { // from class: org.telegram.ui.Components.PopupSwipeBackLayout.1
            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (!PopupSwipeBackLayout.this.isProcessingSwipe && !PopupSwipeBackLayout.this.isSwipeDisallowed) {
                    if (!PopupSwipeBackLayout.this.isSwipeBackDisallowed && PopupSwipeBackLayout.this.transitionProgress == 1.0f && distanceX <= (-touchSlop) && Math.abs(distanceX) >= Math.abs(1.5f * distanceY)) {
                        PopupSwipeBackLayout popupSwipeBackLayout = PopupSwipeBackLayout.this;
                        if (!popupSwipeBackLayout.isDisallowedView(e2, popupSwipeBackLayout.getChildAt(popupSwipeBackLayout.transitionProgress > 0.5f ? 1 : 0))) {
                            PopupSwipeBackLayout.this.isProcessingSwipe = true;
                            MotionEvent c = MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0);
                            for (int i = 0; i < PopupSwipeBackLayout.this.getChildCount(); i++) {
                                PopupSwipeBackLayout.this.getChildAt(i).dispatchTouchEvent(c);
                            }
                            c.recycle();
                        }
                    }
                    PopupSwipeBackLayout.this.isSwipeDisallowed = true;
                }
                if (PopupSwipeBackLayout.this.isProcessingSwipe) {
                    PopupSwipeBackLayout.this.toProgress = -1.0f;
                    PopupSwipeBackLayout.this.transitionProgress = 1.0f - Math.max(0.0f, Math.min(1.0f, (e2.getX() - e1.getX()) / PopupSwipeBackLayout.this.getWidth()));
                    PopupSwipeBackLayout.this.invalidateTransforms();
                }
                return PopupSwipeBackLayout.this.isProcessingSwipe;
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (!PopupSwipeBackLayout.this.isAnimationInProgress && !PopupSwipeBackLayout.this.isSwipeDisallowed && velocityX >= 600.0f) {
                    PopupSwipeBackLayout.this.clearFlags();
                    PopupSwipeBackLayout.this.animateToState(0.0f, velocityX / 6000.0f);
                }
                return false;
            }
        });
        this.overlayPaint.setColor(-16777216);
    }

    public void setSwipeBackDisallowed(boolean swipeBackDisallowed) {
        this.isSwipeBackDisallowed = swipeBackDisallowed;
    }

    public void addOnSwipeBackProgressListener(OnSwipeBackProgressListener onSwipeBackProgressListener) {
        this.onSwipeBackProgressListeners.add(onSwipeBackProgressListener);
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        int i = indexOfChild(child);
        int s = canvas.save();
        if (i != 0) {
            int i2 = this.foregroundColor;
            if (i2 == 0) {
                this.foregroundPaint.setColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground, this.resourcesProvider));
            } else {
                this.foregroundPaint.setColor(i2);
            }
            canvas.drawRect(child.getX(), 0.0f, child.getX() + child.getMeasuredWidth(), getMeasuredHeight(), this.foregroundPaint);
        }
        boolean b = super.drawChild(canvas, child, drawingTime);
        if (i == 0) {
            this.overlayPaint.setAlpha((int) (this.transitionProgress * 64.0f));
            canvas.drawRect(0.0f, 0.0f, getWidth(), getHeight(), this.overlayPaint);
        }
        canvas.restoreToCount(s);
        return b;
    }

    public void invalidateTransforms() {
        invalidateTransforms(true);
    }

    public void invalidateTransforms(boolean applyBackScaleY) {
        if (this.lastToProgress != this.toProgress || this.lastTransitionProgress != this.transitionProgress) {
            if (!this.onSwipeBackProgressListeners.isEmpty()) {
                for (int i = 0; i < this.onSwipeBackProgressListeners.size(); i++) {
                    this.onSwipeBackProgressListeners.get(i).onSwipeBackProgress(this, this.toProgress, this.transitionProgress);
                }
            }
            this.lastToProgress = this.toProgress;
            this.lastTransitionProgress = this.transitionProgress;
        }
        View backgroundView = getChildAt(0);
        View foregroundView = null;
        int i2 = this.currentForegroundIndex;
        if (i2 >= 0 && i2 < getChildCount()) {
            foregroundView = getChildAt(this.currentForegroundIndex);
        }
        backgroundView.setTranslationX((-this.transitionProgress) * getWidth() * 0.5f);
        float bSc = ((1.0f - this.transitionProgress) * 0.05f) + 0.95f;
        backgroundView.setScaleX(bSc);
        backgroundView.setScaleY(bSc);
        if (foregroundView != null) {
            foregroundView.setTranslationX((1.0f - this.transitionProgress) * getWidth());
        }
        invalidateVisibility();
        float fW = backgroundView.getMeasuredWidth();
        float fH = backgroundView.getMeasuredHeight();
        float tW = 0.0f;
        float tH = 0.0f;
        if (foregroundView != null) {
            tW = foregroundView.getMeasuredWidth();
            float f = this.overrideForegroundHeight;
            if (f == 0.0f) {
                f = foregroundView.getMeasuredHeight();
            }
            tH = f;
        }
        if (backgroundView.getMeasuredWidth() == 0 || backgroundView.getMeasuredHeight() == 0) {
            return;
        }
        ActionBarPopupWindow.ActionBarPopupWindowLayout p = (ActionBarPopupWindow.ActionBarPopupWindowLayout) getParent();
        float f2 = this.transitionProgress;
        float w = ((tW - fW) * f2) + fW;
        float h = ((tH - fH) * f2) + fH;
        float h2 = h + p.getPaddingTop() + p.getPaddingBottom();
        p.updateAnimation = false;
        p.setBackScaleX((w + (p.getPaddingLeft() + p.getPaddingRight())) / p.getMeasuredWidth());
        if (applyBackScaleY) {
            p.setBackScaleY(h2 / p.getMeasuredHeight());
        }
        p.updateAnimation = true;
        for (int i3 = 0; i3 < getChildCount(); i3++) {
            View ch = getChildAt(i3);
            ch.setPivotX(0.0f);
            ch.setPivotY(0.0f);
        }
        invalidate();
    }

    public boolean isForegroundOpen() {
        return this.transitionProgress > 0.0f;
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (processTouchEvent(ev)) {
            return true;
        }
        int act = ev.getActionMasked();
        RectF rectF = this.mRect;
        if (rectF != null) {
            rectF.contains(ev.getX(), ev.getY());
        }
        if (act == 0 && !this.mRect.contains(ev.getX(), ev.getY())) {
            callOnClick();
            return true;
        }
        int i = this.currentForegroundIndex;
        if (i < 0 || i >= getChildCount()) {
            return super.dispatchTouchEvent(ev);
        }
        View bv = getChildAt(0);
        View fv = getChildAt(this.currentForegroundIndex);
        boolean b = (this.transitionProgress > 0.5f ? fv : bv).dispatchTouchEvent(ev);
        return (!b && act == 0) || b || onTouchEvent(ev);
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        invalidateTransforms();
    }

    private boolean processTouchEvent(MotionEvent ev) {
        int act = ev.getAction() & 255;
        if (this.isAnimationInProgress) {
            return true;
        }
        if (!this.detector.onTouchEvent(ev)) {
            switch (act) {
                case 1:
                case 3:
                    if (this.isProcessingSwipe) {
                        clearFlags();
                        animateToState(this.transitionProgress >= 0.5f ? 1.0f : 0.0f, 0.0f);
                        return false;
                    } else if (this.isSwipeDisallowed) {
                        clearFlags();
                        return false;
                    } else {
                        return false;
                    }
            }
        }
        return this.isProcessingSwipe;
    }

    public void animateToState(final float f, float flingVal) {
        ValueAnimator val = ValueAnimator.ofFloat(this.transitionProgress, f).setDuration(Math.max(0.5f, Math.abs(this.transitionProgress - f) - Math.min(0.2f, flingVal)) * 300.0f);
        val.setInterpolator(CubicBezierInterpolator.DEFAULT);
        final int selectedAccount = UserConfig.selectedAccount;
        this.notificationIndex = NotificationCenter.getInstance(selectedAccount).setAnimationInProgress(this.notificationIndex, null);
        val.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.PopupSwipeBackLayout$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                PopupSwipeBackLayout.this.m2876xb053141a(valueAnimator);
            }
        });
        val.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.PopupSwipeBackLayout.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                PopupSwipeBackLayout.this.isAnimationInProgress = true;
                PopupSwipeBackLayout.this.toProgress = f;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                NotificationCenter.getInstance(selectedAccount).onAnimationFinish(PopupSwipeBackLayout.this.notificationIndex);
                PopupSwipeBackLayout.this.transitionProgress = f;
                if (f <= 0.0f) {
                    PopupSwipeBackLayout.this.currentForegroundIndex = -1;
                }
                PopupSwipeBackLayout.this.invalidateTransforms();
                PopupSwipeBackLayout.this.isAnimationInProgress = false;
            }
        });
        val.start();
    }

    /* renamed from: lambda$animateToState$0$org-telegram-ui-Components-PopupSwipeBackLayout */
    public /* synthetic */ void m2876xb053141a(ValueAnimator animation) {
        this.transitionProgress = ((Float) animation.getAnimatedValue()).floatValue();
        invalidateTransforms();
    }

    public void clearFlags() {
        this.isProcessingSwipe = false;
        this.isSwipeDisallowed = false;
    }

    public void openForeground(int viewIndex) {
        if (this.isAnimationInProgress) {
            return;
        }
        this.currentForegroundIndex = viewIndex;
        this.overrideForegroundHeight = this.overrideHeightIndex.get(viewIndex);
        animateToState(1.0f, 0.0f);
    }

    public void closeForeground() {
        closeForeground(true);
    }

    public void closeForeground(boolean animated) {
        if (this.isAnimationInProgress) {
            return;
        }
        if (!animated) {
            this.currentForegroundIndex = -1;
            this.transitionProgress = 0.0f;
            invalidateTransforms();
            return;
        }
        animateToState(0.0f, 0.0f);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        for (int i = 0; i < getChildCount(); i++) {
            View ch = getChildAt(i);
            boolean shownFromBottom = (ch.getLayoutParams() instanceof FrameLayout.LayoutParams) && ((FrameLayout.LayoutParams) ch.getLayoutParams()).gravity == 80;
            if (shownFromBottom) {
                ch.layout(0, (bottom - top) - ch.getMeasuredHeight(), ch.getMeasuredWidth(), bottom - top);
            } else {
                ch.layout(0, 0, ch.getMeasuredWidth(), ch.getMeasuredHeight());
            }
        }
    }

    @Override // android.view.ViewGroup
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        invalidateTransforms();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        float h;
        float w;
        float y;
        if (getChildCount() == 0) {
            return;
        }
        View backgroundView = getChildAt(0);
        float fY = backgroundView.getTop();
        float fW = backgroundView.getMeasuredWidth();
        float fH = backgroundView.getMeasuredHeight();
        int i = this.currentForegroundIndex;
        if (i == -1 || i >= getChildCount()) {
            y = fY;
            w = fW;
            h = fH;
        } else {
            View foregroundView = getChildAt(this.currentForegroundIndex);
            float tY = foregroundView.getTop();
            float tW = foregroundView.getMeasuredWidth();
            float tH = this.overrideForegroundHeight;
            if (tH == 0.0f) {
                tH = foregroundView.getMeasuredHeight();
            }
            if (backgroundView.getMeasuredWidth() == 0 || backgroundView.getMeasuredHeight() == 0 || foregroundView.getMeasuredWidth() == 0 || foregroundView.getMeasuredHeight() == 0) {
                y = fY;
                w = fW;
                h = fH;
            } else {
                y = AndroidUtilities.lerp(fY, tY, this.transitionProgress);
                w = AndroidUtilities.lerp(fW, tW, this.transitionProgress);
                h = AndroidUtilities.lerp(fH, tH, this.transitionProgress);
            }
        }
        int s = canvas.save();
        this.mPath.rewind();
        int rad = AndroidUtilities.dp(6.0f);
        this.mRect.set(0.0f, y, w, y + h);
        this.mPath.addRoundRect(this.mRect, rad, rad, Path.Direction.CW);
        canvas.clipPath(this.mPath);
        super.dispatchDraw(canvas);
        canvas.restoreToCount(s);
        if (this.onHeightUpdateListener != null && this.lastHeightReported != this.mRect.height()) {
            IntCallback intCallback = this.onHeightUpdateListener;
            int height = (int) this.mRect.height();
            this.lastHeightReported = height;
            intCallback.run(height);
        }
    }

    public void setOnHeightUpdateListener(IntCallback onHeightUpdateListener) {
        this.onHeightUpdateListener = onHeightUpdateListener;
    }

    public boolean isDisallowedView(MotionEvent e, View v) {
        v.getHitRect(this.hitRect);
        if (!this.hitRect.contains((int) e.getX(), (int) e.getY()) || !v.canScrollHorizontally(-1)) {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    if (isDisallowedView(e, vg.getChildAt(i))) {
                        return true;
                    }
                }
                return false;
            }
            return false;
        }
        return true;
    }

    private void invalidateVisibility() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (i == 0) {
                if (this.transitionProgress == 1.0f && child.getVisibility() != 4) {
                    child.setVisibility(4);
                }
                if (this.transitionProgress != 1.0f && child.getVisibility() != 0) {
                    child.setVisibility(0);
                }
            } else if (i == this.currentForegroundIndex) {
                if (this.transitionProgress == 0.0f && child.getVisibility() != 4) {
                    child.setVisibility(4);
                }
                if (this.transitionProgress != 0.0f && child.getVisibility() != 0) {
                    child.setVisibility(0);
                }
            } else {
                child.setVisibility(4);
            }
        }
    }

    public void setNewForegroundHeight(int index, int height, boolean animated) {
        this.overrideHeightIndex.put(index, height);
        int i = this.currentForegroundIndex;
        if (index != i || i < 0 || i >= getChildCount()) {
            return;
        }
        ValueAnimator valueAnimator = this.foregroundAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.foregroundAnimator = null;
        }
        if (animated) {
            View fg = getChildAt(this.currentForegroundIndex);
            float fromH = this.overrideForegroundHeight;
            if (fromH == 0.0f) {
                fromH = fg.getMeasuredHeight();
            }
            float toH = height;
            ValueAnimator animator = ValueAnimator.ofFloat(fromH, toH).setDuration(240L);
            animator.setInterpolator(Easings.easeInOutQuad);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.PopupSwipeBackLayout$$ExternalSyntheticLambda1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    PopupSwipeBackLayout.this.m2877xca58b7ee(valueAnimator2);
                }
            });
            this.isAnimationInProgress = true;
            animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.PopupSwipeBackLayout.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    PopupSwipeBackLayout.this.isAnimationInProgress = false;
                    PopupSwipeBackLayout.this.foregroundAnimator = null;
                }
            });
            animator.start();
            this.foregroundAnimator = animator;
            return;
        }
        this.overrideForegroundHeight = height;
        invalidateTransforms();
    }

    /* renamed from: lambda$setNewForegroundHeight$1$org-telegram-ui-Components-PopupSwipeBackLayout */
    public /* synthetic */ void m2877xca58b7ee(ValueAnimator animation) {
        this.overrideForegroundHeight = ((Float) animation.getAnimatedValue()).floatValue();
        invalidateTransforms();
    }

    public void setForegroundColor(int color) {
        this.foregroundColor = color;
    }
}
