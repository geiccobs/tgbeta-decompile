package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class BotCommandsMenuContainer extends FrameLayout implements NestedScrollingParent {
    private boolean entering;
    public RecyclerListView listView;
    float scrollYOffset;
    Drawable shadowDrawable;
    private ObjectAnimator currentAnimation = null;
    Paint backgroundPaint = new Paint();
    Paint topBackground = new Paint(1);
    boolean dismissed = true;
    private NestedScrollingParentHelper nestedScrollingParentHelper = new NestedScrollingParentHelper(this);

    public BotCommandsMenuContainer(Context context) {
        super(context);
        this.shadowDrawable = context.getResources().getDrawable(R.drawable.sheet_shadow_round).mutate();
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.Components.BotCommandsMenuContainer.1
            @Override // org.telegram.ui.Components.RecyclerListView, android.view.ViewGroup, android.view.View
            public void dispatchDraw(Canvas canvas) {
                if (BotCommandsMenuContainer.this.listView.getLayoutManager() == null || BotCommandsMenuContainer.this.listView.getAdapter() == null || BotCommandsMenuContainer.this.listView.getAdapter().getItemCount() == 0) {
                    super.dispatchDraw(canvas);
                    return;
                }
                View firstView = BotCommandsMenuContainer.this.listView.getLayoutManager().findViewByPosition(0);
                float y = 0.0f;
                if (firstView != null) {
                    y = firstView.getY();
                }
                if (y < 0.0f) {
                    y = 0.0f;
                }
                BotCommandsMenuContainer.this.scrollYOffset = y;
                float y2 = y - AndroidUtilities.dp(8.0f);
                if (y2 > 0.0f) {
                    BotCommandsMenuContainer.this.shadowDrawable.setBounds(-AndroidUtilities.dp(8.0f), ((int) y2) - AndroidUtilities.dp(24.0f), getMeasuredWidth() + AndroidUtilities.dp(8.0f), (int) y2);
                    BotCommandsMenuContainer.this.shadowDrawable.draw(canvas);
                }
                canvas.drawRect(0.0f, y2, getMeasuredWidth(), getMeasuredHeight() + AndroidUtilities.dp(16.0f), BotCommandsMenuContainer.this.backgroundPaint);
                AndroidUtilities.rectTmp.set((getMeasuredWidth() / 2.0f) - AndroidUtilities.dp(12.0f), y2 - AndroidUtilities.dp(4.0f), (getMeasuredWidth() / 2.0f) + AndroidUtilities.dp(12.0f), y2);
                canvas.drawRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), BotCommandsMenuContainer.this.topBackground);
                super.dispatchDraw(canvas);
            }
        };
        this.listView = recyclerListView;
        recyclerListView.setClipToPadding(false);
        addView(this.listView);
        updateColors();
        setClipChildren(false);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return !this.dismissed && nestedScrollAxes == 2;
    }

    @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        this.nestedScrollingParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
        if (this.dismissed) {
            return;
        }
        cancelCurrentAnimation();
    }

    @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
    public void onStopNestedScroll(View target) {
        this.nestedScrollingParentHelper.onStopNestedScroll(target);
        if (this.dismissed) {
            return;
        }
        checkDismiss();
    }

    private void checkDismiss() {
        if (this.dismissed) {
            return;
        }
        if (this.listView.getTranslationY() > AndroidUtilities.dp(16.0f)) {
            dismiss();
        } else {
            playEnterAnim(false);
        }
    }

    @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        if (this.dismissed) {
            return;
        }
        cancelCurrentAnimation();
        if (dyUnconsumed != 0) {
            float currentTranslation = this.listView.getTranslationY() - dyUnconsumed;
            if (currentTranslation < 0.0f) {
                currentTranslation = 0.0f;
            }
            this.listView.setTranslationY(currentTranslation);
            invalidate();
        }
    }

    @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        if (this.dismissed) {
            return;
        }
        cancelCurrentAnimation();
        float currentTranslation = this.listView.getTranslationY();
        if (currentTranslation > 0.0f && dy > 0) {
            float currentTranslation2 = currentTranslation - dy;
            consumed[1] = dy;
            if (currentTranslation2 < 0.0f) {
                currentTranslation2 = 0.0f;
            }
            this.listView.setTranslationY(currentTranslation2);
            invalidate();
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

    private void cancelCurrentAnimation() {
        ObjectAnimator objectAnimator = this.currentAnimation;
        if (objectAnimator != null) {
            objectAnimator.removeAllListeners();
            this.currentAnimation.cancel();
            this.currentAnimation = null;
        }
    }

    public void show() {
        if (getVisibility() != 0) {
            setVisibility(0);
            this.listView.scrollToPosition(0);
            this.entering = true;
            this.dismissed = false;
        } else if (this.dismissed) {
            this.dismissed = false;
            cancelCurrentAnimation();
            playEnterAnim(false);
        }
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.entering && !this.dismissed) {
            RecyclerListView recyclerListView = this.listView;
            recyclerListView.setTranslationY((recyclerListView.getMeasuredHeight() - this.listView.getPaddingTop()) + AndroidUtilities.dp(16.0f));
            playEnterAnim(true);
            this.entering = false;
        }
    }

    private void playEnterAnim(boolean firstTime) {
        if (this.dismissed) {
            return;
        }
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.listView, TRANSLATION_Y, this.listView.getTranslationY(), 0.0f);
        this.currentAnimation = ofFloat;
        if (firstTime) {
            ofFloat.setDuration(320L);
            this.currentAnimation.setInterpolator(new OvershootInterpolator(0.8f));
        } else {
            ofFloat.setDuration(150L);
            this.currentAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
        }
        this.currentAnimation.start();
    }

    public void dismiss() {
        if (!this.dismissed) {
            this.dismissed = true;
            cancelCurrentAnimation();
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.listView, TRANSLATION_Y, this.listView.getTranslationY(), (getMeasuredHeight() - this.scrollYOffset) + AndroidUtilities.dp(40.0f));
            this.currentAnimation = ofFloat;
            ofFloat.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.BotCommandsMenuContainer.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    BotCommandsMenuContainer.this.setVisibility(8);
                    BotCommandsMenuContainer.this.currentAnimation = null;
                }
            });
            this.currentAnimation.setDuration(150L);
            this.currentAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.currentAnimation.start();
            onDismiss();
        }
    }

    public void onDismiss() {
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 0 && ev.getY() < this.scrollYOffset - AndroidUtilities.dp(24.0f)) {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void updateColors() {
        this.topBackground.setColor(Theme.getColor(Theme.key_dialogGrayLine));
        this.backgroundPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhite), PorterDuff.Mode.MULTIPLY));
        invalidate();
    }
}
