package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
/* loaded from: classes5.dex */
public class ReplaceableIconDrawable extends Drawable implements Animator.AnimatorListener {
    private ValueAnimator animation;
    private ColorFilter colorFilter;
    private Context context;
    private Drawable currentDrawable;
    private Drawable outDrawable;
    private int currentResId = 0;
    private float progress = 1.0f;

    public ReplaceableIconDrawable(Context context) {
        this.context = context;
    }

    public void setIcon(int resId, boolean animated) {
        if (this.currentResId == resId) {
            return;
        }
        setIcon(ContextCompat.getDrawable(this.context, resId).mutate(), animated);
        this.currentResId = resId;
    }

    public void setIcon(Drawable drawable, boolean animated) {
        if (drawable == null) {
            this.currentDrawable = null;
            this.outDrawable = null;
            invalidateSelf();
            return;
        }
        if (getBounds() == null || getBounds().isEmpty()) {
            animated = false;
        }
        Drawable drawable2 = this.currentDrawable;
        if (drawable == drawable2) {
            drawable2.setColorFilter(this.colorFilter);
            return;
        }
        this.currentResId = 0;
        this.outDrawable = drawable2;
        this.currentDrawable = drawable;
        drawable.setColorFilter(this.colorFilter);
        updateBounds(this.currentDrawable, getBounds());
        updateBounds(this.outDrawable, getBounds());
        ValueAnimator valueAnimator = this.animation;
        if (valueAnimator != null) {
            valueAnimator.removeAllListeners();
            this.animation.cancel();
        }
        if (!animated) {
            this.progress = 1.0f;
            this.outDrawable = null;
            return;
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.animation = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ReplaceableIconDrawable$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                ReplaceableIconDrawable.this.m2958xd9f8d97e(valueAnimator2);
            }
        });
        this.animation.addListener(this);
        this.animation.setDuration(150L);
        this.animation.start();
    }

    /* renamed from: lambda$setIcon$0$org-telegram-ui-Components-ReplaceableIconDrawable */
    public /* synthetic */ void m2958xd9f8d97e(ValueAnimator animation) {
        this.progress = ((Float) animation.getAnimatedValue()).floatValue();
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(android.graphics.Rect bounds) {
        super.onBoundsChange(bounds);
        updateBounds(this.currentDrawable, bounds);
        updateBounds(this.outDrawable, bounds);
    }

    private void updateBounds(Drawable d, android.graphics.Rect bounds) {
        int top;
        int offset;
        int left;
        int offset2;
        if (d == null) {
            return;
        }
        if (d.getIntrinsicHeight() < 0) {
            offset = bounds.top;
            top = bounds.bottom;
        } else {
            int top2 = bounds.height();
            int offset3 = (top2 - d.getIntrinsicHeight()) / 2;
            int top3 = bounds.top + offset3;
            offset = top3;
            top = bounds.top + offset3 + d.getIntrinsicHeight();
        }
        if (d.getIntrinsicWidth() < 0) {
            offset2 = bounds.left;
            left = bounds.right;
        } else {
            int left2 = bounds.width();
            int offset4 = (left2 - d.getIntrinsicWidth()) / 2;
            int left3 = bounds.left + offset4;
            offset2 = left3;
            left = bounds.left + offset4 + d.getIntrinsicWidth();
        }
        d.setBounds(offset2, offset, left, top);
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        int cX = getBounds().centerX();
        int cY = getBounds().centerY();
        if (this.progress != 1.0f && this.currentDrawable != null) {
            canvas.save();
            float f = this.progress;
            canvas.scale(f, f, cX, cY);
            this.currentDrawable.setAlpha((int) (this.progress * 255.0f));
            this.currentDrawable.draw(canvas);
            canvas.restore();
        } else {
            Drawable drawable = this.currentDrawable;
            if (drawable != null) {
                drawable.setAlpha(255);
                this.currentDrawable.draw(canvas);
            }
        }
        float f2 = this.progress;
        if (f2 != 1.0f && this.outDrawable != null) {
            float progressRev = 1.0f - f2;
            canvas.save();
            canvas.scale(progressRev, progressRev, cX, cY);
            this.outDrawable.setAlpha((int) (255.0f * progressRev));
            this.outDrawable.draw(canvas);
            canvas.restore();
            return;
        }
        Drawable drawable2 = this.outDrawable;
        if (drawable2 != null) {
            drawable2.setAlpha(255);
            this.outDrawable.draw(canvas);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.colorFilter = colorFilter;
        Drawable drawable = this.currentDrawable;
        if (drawable != null) {
            drawable.setColorFilter(colorFilter);
        }
        Drawable drawable2 = this.outDrawable;
        if (drawable2 != null) {
            drawable2.setColorFilter(colorFilter);
        }
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -2;
    }

    @Override // android.animation.Animator.AnimatorListener
    public void onAnimationEnd(Animator animation) {
        this.outDrawable = null;
        invalidateSelf();
    }

    @Override // android.animation.Animator.AnimatorListener
    public void onAnimationStart(Animator animation) {
    }

    @Override // android.animation.Animator.AnimatorListener
    public void onAnimationCancel(Animator animation) {
    }

    @Override // android.animation.Animator.AnimatorListener
    public void onAnimationRepeat(Animator animation) {
    }
}
