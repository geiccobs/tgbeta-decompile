package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
/* loaded from: classes5.dex */
public class MessageBackgroundDrawable extends Drawable {
    private boolean animationInProgress;
    private float currentAnimationProgress;
    private float finalRadius;
    private boolean isSelected;
    private long lastAnimationTime;
    private long lastTouchTime;
    private View parentView;
    private Paint paint = new Paint(1);
    private Paint customPaint = null;
    private float touchX = -1.0f;
    private float touchY = -1.0f;
    private float touchOverrideX = -1.0f;
    private float touchOverrideY = -1.0f;

    public MessageBackgroundDrawable(View parent) {
        this.parentView = parent;
    }

    public void setColor(int color) {
        this.paint.setColor(color);
    }

    public void setCustomPaint(Paint paint) {
        this.customPaint = paint;
    }

    public void setSelected(boolean selected, boolean animated) {
        float f = 1.0f;
        if (this.isSelected == selected) {
            if (this.animationInProgress != animated && !animated) {
                if (!selected) {
                    f = 0.0f;
                }
                this.currentAnimationProgress = f;
                this.animationInProgress = false;
                return;
            }
            return;
        }
        this.isSelected = selected;
        this.animationInProgress = animated;
        if (animated) {
            this.lastAnimationTime = SystemClock.elapsedRealtime();
        } else {
            if (!selected) {
                f = 0.0f;
            }
            this.currentAnimationProgress = f;
        }
        calcRadius();
        invalidate();
    }

    private void invalidate() {
        View view = this.parentView;
        if (view != null) {
            view.invalidate();
            if (this.parentView.getParent() != null) {
                ((ViewGroup) this.parentView.getParent()).invalidate();
            }
        }
    }

    private void calcRadius() {
        android.graphics.Rect bounds = getBounds();
        float x1 = bounds.centerX();
        float y1 = bounds.centerY();
        this.finalRadius = (float) Math.ceil(Math.sqrt(((bounds.left - x1) * (bounds.left - x1)) + ((bounds.top - y1) * (bounds.top - y1))));
    }

    public void setTouchCoords(float x, float y) {
        this.touchX = x;
        this.touchY = y;
        this.lastTouchTime = SystemClock.elapsedRealtime();
    }

    public void setTouchCoordsOverride(float x, float y) {
        this.touchOverrideX = x;
        this.touchOverrideY = y;
    }

    public float getTouchX() {
        return this.touchX;
    }

    public float getTouchY() {
        return this.touchY;
    }

    public long getLastTouchTime() {
        return this.lastTouchTime;
    }

    public boolean isAnimationInProgress() {
        return this.animationInProgress;
    }

    @Override // android.graphics.drawable.Drawable
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        calcRadius();
    }

    @Override // android.graphics.drawable.Drawable
    public void setBounds(android.graphics.Rect bounds) {
        super.setBounds(bounds);
        calcRadius();
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.paint.setColorFilter(colorFilter);
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -2;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        this.paint.setAlpha(alpha);
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        float interpolatedProgress;
        float y1;
        float x1;
        float f = this.currentAnimationProgress;
        if (f == 1.0f) {
            android.graphics.Rect bounds = getBounds();
            Paint paint = this.customPaint;
            if (paint == null) {
                paint = this.paint;
            }
            canvas.drawRect(bounds, paint);
        } else if (f != 0.0f) {
            if (this.isSelected) {
                interpolatedProgress = CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(this.currentAnimationProgress);
            } else {
                interpolatedProgress = 1.0f - CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(1.0f - this.currentAnimationProgress);
            }
            android.graphics.Rect bounds2 = getBounds();
            float centerX = bounds2.centerX();
            float centerY = bounds2.centerY();
            if (this.touchOverrideX >= 0.0f && this.touchOverrideY >= 0.0f) {
                x1 = this.touchOverrideX;
                y1 = this.touchOverrideY;
            } else {
                float x12 = this.touchX;
                if (x12 >= 0.0f && this.touchY >= 0.0f) {
                    x1 = this.touchX;
                    y1 = this.touchY;
                } else {
                    x1 = centerX;
                    y1 = centerY;
                }
            }
            float x13 = ((1.0f - interpolatedProgress) * (x1 - centerX)) + centerX;
            float x14 = 1.0f - interpolatedProgress;
            float y12 = (x14 * (y1 - centerY)) + centerY;
            float y13 = this.finalRadius;
            float f2 = y13 * interpolatedProgress;
            Paint paint2 = this.customPaint;
            if (paint2 == null) {
                paint2 = this.paint;
            }
            canvas.drawCircle(x13, y12, f2, paint2);
        }
        if (this.animationInProgress) {
            long newTime = SystemClock.elapsedRealtime();
            long dt = newTime - this.lastAnimationTime;
            if (dt > 20) {
                dt = 17;
            }
            this.lastAnimationTime = newTime;
            boolean finished = false;
            if (this.isSelected) {
                float f3 = this.currentAnimationProgress + (((float) dt) / 240.0f);
                this.currentAnimationProgress = f3;
                if (f3 >= 1.0f) {
                    this.currentAnimationProgress = 1.0f;
                    finished = true;
                }
            } else {
                float f4 = this.currentAnimationProgress - (((float) dt) / 240.0f);
                this.currentAnimationProgress = f4;
                if (f4 <= 0.0f) {
                    this.currentAnimationProgress = 0.0f;
                    finished = true;
                }
            }
            if (finished) {
                this.touchX = -1.0f;
                this.touchY = -1.0f;
                this.touchOverrideX = -1.0f;
                this.touchOverrideY = -1.0f;
                this.animationInProgress = false;
            }
            invalidate();
        }
    }
}
