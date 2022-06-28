package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
/* loaded from: classes5.dex */
public class CallSwipeView extends View {
    private AnimatorSet arrowAnim;
    private Paint arrowsPaint;
    private boolean dragFromRight;
    private float dragStartX;
    private Listener listener;
    private Paint pullBgPaint;
    private View viewToDrag;
    private int[] arrowAlphas = {64, 64, 64};
    private boolean dragging = false;
    private RectF tmpRect = new RectF();
    private Path arrow = new Path();
    private boolean animatingArrows = false;
    private boolean canceled = false;

    /* loaded from: classes5.dex */
    public interface Listener {
        void onDragCancel();

        void onDragComplete();

        void onDragStart();
    }

    public CallSwipeView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setClickable(true);
        Paint paint = new Paint(1);
        this.arrowsPaint = paint;
        paint.setColor(-1);
        this.arrowsPaint.setStyle(Paint.Style.STROKE);
        this.arrowsPaint.setStrokeWidth(AndroidUtilities.dp(2.5f));
        this.pullBgPaint = new Paint(1);
        ArrayList<Animator> anims = new ArrayList<>();
        for (int i = 0; i < this.arrowAlphas.length; i++) {
            ArrowAnimWrapper aaw = new ArrowAnimWrapper(i);
            ObjectAnimator anim = ObjectAnimator.ofInt(aaw, "arrowAlpha", 64, 255, 64);
            anim.setDuration(700L);
            anim.setStartDelay(i * 200);
            anims.add(anim);
        }
        AnimatorSet animatorSet = new AnimatorSet();
        this.arrowAnim = animatorSet;
        animatorSet.playTogether(anims);
        this.arrowAnim.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.voip.CallSwipeView.1
            private Runnable restarter = new Runnable() { // from class: org.telegram.ui.Components.voip.CallSwipeView.1.1
                @Override // java.lang.Runnable
                public void run() {
                    if (CallSwipeView.this.arrowAnim != null) {
                        CallSwipeView.this.arrowAnim.start();
                    }
                }
            };
            private long startTime;

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (System.currentTimeMillis() - this.startTime >= animation.getDuration() / 4) {
                    if (!CallSwipeView.this.canceled && CallSwipeView.this.animatingArrows) {
                        CallSwipeView.this.post(this.restarter);
                    }
                } else if (BuildVars.LOGS_ENABLED) {
                    FileLog.w("Not repeating animation because previous loop was too fast");
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                CallSwipeView.this.canceled = true;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                this.startTime = System.currentTimeMillis();
            }
        });
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AnimatorSet animatorSet = this.arrowAnim;
        if (animatorSet != null) {
            this.canceled = true;
            animatorSet.cancel();
            this.arrowAnim = null;
        }
    }

    public void setColor(int color) {
        this.pullBgPaint.setColor(color);
        this.pullBgPaint.setAlpha(178);
    }

    public void setViewToDrag(View viewToDrag, boolean dragFromRight) {
        this.viewToDrag = viewToDrag;
        this.dragFromRight = dragFromRight;
        updateArrowPath();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private int getDraggedViewWidth() {
        return getHeight();
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        AccessibilityManager am = (AccessibilityManager) getContext().getSystemService("accessibility");
        if (!isEnabled() || am.isTouchExplorationEnabled()) {
            return super.onTouchEvent(ev);
        }
        if (ev.getAction() == 0) {
            if ((!this.dragFromRight && ev.getX() < getDraggedViewWidth()) || (this.dragFromRight && ev.getX() > getWidth() - getDraggedViewWidth())) {
                this.dragging = true;
                this.dragStartX = ev.getX();
                getParent().requestDisallowInterceptTouchEvent(true);
                this.listener.onDragStart();
                stopAnimatingArrows();
            }
        } else {
            float f = 0.0f;
            if (ev.getAction() == 2) {
                View view = this.viewToDrag;
                float f2 = this.dragFromRight ? -(getWidth() - getDraggedViewWidth()) : 0.0f;
                float x = ev.getX() - this.dragStartX;
                if (!this.dragFromRight) {
                    f = getWidth() - getDraggedViewWidth();
                }
                view.setTranslationX(Math.max(f2, Math.min(x, f)));
                invalidate();
            } else if (ev.getAction() == 1 || ev.getAction() == 3) {
                if (Math.abs(this.viewToDrag.getTranslationX()) >= getWidth() - getDraggedViewWidth() && ev.getAction() == 1) {
                    this.listener.onDragComplete();
                } else {
                    this.listener.onDragCancel();
                    this.viewToDrag.animate().translationX(0.0f).setDuration(200L).start();
                    invalidate();
                    startAnimatingArrows();
                    this.dragging = false;
                }
            }
        }
        return this.dragging;
    }

    public void stopAnimatingArrows() {
        this.animatingArrows = false;
    }

    public void startAnimatingArrows() {
        AnimatorSet animatorSet;
        if (this.animatingArrows || (animatorSet = this.arrowAnim) == null) {
            return;
        }
        this.animatingArrows = true;
        if (animatorSet != null) {
            animatorSet.start();
        }
    }

    public void reset() {
        if (this.arrowAnim == null || this.canceled) {
            return;
        }
        this.listener.onDragCancel();
        this.viewToDrag.animate().translationX(0.0f).setDuration(200L).start();
        invalidate();
        startAnimatingArrows();
        this.dragging = false;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.viewToDrag.getTranslationX() != 0.0f) {
            if (this.dragFromRight) {
                this.tmpRect.set((getWidth() + this.viewToDrag.getTranslationX()) - getDraggedViewWidth(), 0.0f, getWidth(), getHeight());
            } else {
                this.tmpRect.set(0.0f, 0.0f, this.viewToDrag.getTranslationX() + getDraggedViewWidth(), getHeight());
            }
            canvas.drawRoundRect(this.tmpRect, getHeight() / 2, getHeight() / 2, this.pullBgPaint);
        }
        canvas.save();
        if (this.dragFromRight) {
            canvas.translate((getWidth() - getHeight()) - AndroidUtilities.dp(18.0f), getHeight() / 2);
        } else {
            canvas.translate(getHeight() + AndroidUtilities.dp(12.0f), getHeight() / 2);
        }
        float offsetX = Math.abs(this.viewToDrag.getTranslationX());
        for (int i = 0; i < 3; i++) {
            float masterAlpha = 1.0f;
            float f = 16.0f;
            if (offsetX > AndroidUtilities.dp(i * 16)) {
                masterAlpha = 1.0f - Math.min(1.0f, Math.max(0.0f, (offsetX - (AndroidUtilities.dp(16.0f) * i)) / AndroidUtilities.dp(16.0f)));
            }
            this.arrowsPaint.setAlpha(Math.round(this.arrowAlphas[i] * masterAlpha));
            canvas.drawPath(this.arrow, this.arrowsPaint);
            if (this.dragFromRight) {
                f = -16.0f;
            }
            canvas.translate(AndroidUtilities.dp(f), 0.0f);
        }
        canvas.restore();
        invalidate();
    }

    private void updateArrowPath() {
        this.arrow.reset();
        int size = AndroidUtilities.dp(6.0f);
        if (this.dragFromRight) {
            this.arrow.moveTo(size, -size);
            this.arrow.lineTo(0.0f, 0.0f);
            this.arrow.lineTo(size, size);
            return;
        }
        this.arrow.moveTo(0.0f, -size);
        this.arrow.lineTo(size, 0.0f);
        this.arrow.lineTo(0.0f, size);
    }

    @Override // android.view.View
    public void onPopulateAccessibilityEvent(AccessibilityEvent ev) {
        if (isEnabled() && ev.getEventType() == 1) {
            this.listener.onDragComplete();
        }
        super.onPopulateAccessibilityEvent(ev);
    }

    /* loaded from: classes5.dex */
    public class ArrowAnimWrapper {
        private int index;

        public ArrowAnimWrapper(int value) {
            CallSwipeView.this = r1;
            this.index = value;
        }

        public int getArrowAlpha() {
            return CallSwipeView.this.arrowAlphas[this.index];
        }

        public void setArrowAlpha(int value) {
            CallSwipeView.this.arrowAlphas[this.index] = value;
        }
    }
}
