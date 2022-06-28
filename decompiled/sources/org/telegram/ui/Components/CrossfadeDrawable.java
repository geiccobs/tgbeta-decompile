package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
/* loaded from: classes5.dex */
public class CrossfadeDrawable extends Drawable {
    private final Drawable bottomDrawable;
    float globalAlpha = 255.0f;
    private float progress;
    private final Drawable topDrawable;

    public CrossfadeDrawable(Drawable topDrawable, Drawable bottomDrawable) {
        this.topDrawable = topDrawable;
        this.bottomDrawable = bottomDrawable;
        if (topDrawable != null) {
            topDrawable.setCallback(new Drawable.Callback() { // from class: org.telegram.ui.Components.CrossfadeDrawable.1
                @Override // android.graphics.drawable.Drawable.Callback
                public void invalidateDrawable(Drawable drawable) {
                    if (CrossfadeDrawable.this.progress < 1.0f) {
                        CrossfadeDrawable.this.invalidateSelf();
                    }
                }

                @Override // android.graphics.drawable.Drawable.Callback
                public void scheduleDrawable(Drawable drawable, Runnable runnable, long l) {
                    if (CrossfadeDrawable.this.progress < 1.0f) {
                        CrossfadeDrawable.this.scheduleSelf(runnable, l);
                    }
                }

                @Override // android.graphics.drawable.Drawable.Callback
                public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
                    if (CrossfadeDrawable.this.progress < 1.0f) {
                        CrossfadeDrawable.this.unscheduleSelf(runnable);
                    }
                }
            });
        }
        if (bottomDrawable != null) {
            bottomDrawable.setCallback(new Drawable.Callback() { // from class: org.telegram.ui.Components.CrossfadeDrawable.2
                @Override // android.graphics.drawable.Drawable.Callback
                public void invalidateDrawable(Drawable drawable) {
                    if (CrossfadeDrawable.this.progress > 0.0f) {
                        CrossfadeDrawable.this.invalidateSelf();
                    }
                }

                @Override // android.graphics.drawable.Drawable.Callback
                public void scheduleDrawable(Drawable drawable, Runnable runnable, long l) {
                }

                @Override // android.graphics.drawable.Drawable.Callback
                public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
                }
            });
        }
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(android.graphics.Rect bounds) {
        this.topDrawable.setBounds(bounds);
        this.bottomDrawable.setBounds(bounds);
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        Drawable drawable = this.topDrawable;
        int topAlpha = (int) (this.globalAlpha * (1.0f - this.progress));
        drawable.setAlpha(topAlpha);
        Drawable drawable2 = this.bottomDrawable;
        int bottomAlpha = (int) (this.globalAlpha * this.progress);
        drawable2.setAlpha(bottomAlpha);
        if (topAlpha > 0) {
            this.topDrawable.draw(canvas);
        }
        if (bottomAlpha > 0) {
            this.bottomDrawable.draw(canvas);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        this.globalAlpha = alpha;
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.topDrawable.setColorFilter(colorFilter);
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.topDrawable.getIntrinsicWidth();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.topDrawable.getIntrinsicHeight();
    }

    public float getProgress() {
        return this.progress;
    }

    public void setProgress(float value) {
        this.progress = value;
        invalidateSelf();
    }
}
