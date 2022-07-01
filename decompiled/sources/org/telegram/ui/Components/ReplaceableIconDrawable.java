package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
/* loaded from: classes3.dex */
public class ReplaceableIconDrawable extends Drawable implements Animator.AnimatorListener {
    private ValueAnimator animation;
    private ColorFilter colorFilter;
    private Context context;
    private Drawable currentDrawable;
    private Drawable outDrawable;
    private int currentResId = 0;
    private float progress = 1.0f;

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -2;
    }

    @Override // android.animation.Animator.AnimatorListener
    public void onAnimationCancel(Animator animator) {
    }

    @Override // android.animation.Animator.AnimatorListener
    public void onAnimationRepeat(Animator animator) {
    }

    @Override // android.animation.Animator.AnimatorListener
    public void onAnimationStart(Animator animator) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
    }

    public ReplaceableIconDrawable(Context context) {
        this.context = context;
    }

    public void setIcon(int i, boolean z) {
        if (this.currentResId == i) {
            return;
        }
        setIcon(ContextCompat.getDrawable(this.context, i).mutate(), z);
        this.currentResId = i;
    }

    public void setIcon(Drawable drawable, boolean z) {
        if (drawable == null) {
            this.currentDrawable = null;
            this.outDrawable = null;
            invalidateSelf();
            return;
        }
        if (getBounds() == null || getBounds().isEmpty()) {
            z = false;
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
        if (!z) {
            this.progress = 1.0f;
            this.outDrawable = null;
            return;
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.animation = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ReplaceableIconDrawable$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                ReplaceableIconDrawable.this.lambda$setIcon$0(valueAnimator2);
            }
        });
        this.animation.addListener(this);
        this.animation.setDuration(150L);
        this.animation.start();
    }

    public /* synthetic */ void lambda$setIcon$0(ValueAnimator valueAnimator) {
        this.progress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(android.graphics.Rect rect) {
        super.onBoundsChange(rect);
        updateBounds(this.currentDrawable, rect);
        updateBounds(this.outDrawable, rect);
    }

    private void updateBounds(Drawable drawable, android.graphics.Rect rect) {
        int i;
        int i2;
        int i3;
        int i4;
        if (drawable == null) {
            return;
        }
        if (drawable.getIntrinsicHeight() < 0) {
            i2 = rect.top;
            i = rect.bottom;
        } else {
            int height = (rect.height() - drawable.getIntrinsicHeight()) / 2;
            int i5 = rect.top;
            int i6 = i5 + height;
            i = i5 + height + drawable.getIntrinsicHeight();
            i2 = i6;
        }
        if (drawable.getIntrinsicWidth() < 0) {
            i4 = rect.left;
            i3 = rect.right;
        } else {
            int width = (rect.width() - drawable.getIntrinsicWidth()) / 2;
            int i7 = rect.left;
            int i8 = i7 + width;
            i3 = i7 + width + drawable.getIntrinsicWidth();
            i4 = i8;
        }
        drawable.setBounds(i4, i2, i3, i);
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        int centerX = getBounds().centerX();
        int centerY = getBounds().centerY();
        if (this.progress != 1.0f && this.currentDrawable != null) {
            canvas.save();
            float f = this.progress;
            canvas.scale(f, f, centerX, centerY);
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
            float f3 = 1.0f - f2;
            canvas.save();
            canvas.scale(f3, f3, centerX, centerY);
            this.outDrawable.setAlpha((int) (f3 * 255.0f));
            this.outDrawable.draw(canvas);
            canvas.restore();
            return;
        }
        Drawable drawable2 = this.outDrawable;
        if (drawable2 == null) {
            return;
        }
        drawable2.setAlpha(255);
        this.outDrawable.draw(canvas);
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

    @Override // android.animation.Animator.AnimatorListener
    public void onAnimationEnd(Animator animator) {
        this.outDrawable = null;
        invalidateSelf();
    }
}
