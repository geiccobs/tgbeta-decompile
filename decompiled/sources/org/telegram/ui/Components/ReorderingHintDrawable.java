package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.animation.Interpolator;
import org.telegram.messenger.AndroidUtilities;
/* loaded from: classes5.dex */
public class ReorderingHintDrawable extends Drawable {
    public static final int DURATION = 1500;
    private static final int DURATION_DELAY1 = 300;
    private static final int DURATION_DELAY2 = 300;
    private static final int DURATION_DELAY3 = 300;
    private static final int DURATION_DELAY4 = 100;
    private static final int DURATION_STAGE1 = 150;
    private static final int DURATION_STAGE2 = 200;
    private static final int DURATION_STAGE3 = 150;
    private final RectDrawable primaryRectDrawable;
    private float scaleX;
    private float scaleY;
    private final RectDrawable secondaryRectDrawable;
    private final android.graphics.Rect tempRect = new android.graphics.Rect();
    private final Interpolator interpolator = Easings.easeInOutSine;
    private final int intrinsicWidth = AndroidUtilities.dp(24.0f);
    private final int intrinsicHeight = AndroidUtilities.dp(24.0f);
    private long startedTime = -1;

    public ReorderingHintDrawable() {
        RectDrawable rectDrawable = new RectDrawable();
        this.primaryRectDrawable = rectDrawable;
        rectDrawable.setColor(-2130706433);
        RectDrawable rectDrawable2 = new RectDrawable();
        this.secondaryRectDrawable = rectDrawable2;
        rectDrawable2.setColor(-2130706433);
    }

    public void startAnimation() {
        this.startedTime = System.currentTimeMillis();
        invalidateSelf();
    }

    public void resetAnimation() {
        this.startedTime = -1L;
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(android.graphics.Rect bounds) {
        this.scaleX = bounds.width() / this.intrinsicWidth;
        this.scaleY = bounds.height() / this.intrinsicHeight;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if (this.startedTime > 0) {
            int passedTime = ((int) (System.currentTimeMillis() - this.startedTime)) - 300;
            if (passedTime < 0) {
                drawStage1(canvas, 0.0f);
            } else if (passedTime < 150) {
                drawStage1(canvas, passedTime / 150.0f);
            } else {
                int passedTime2 = passedTime - 450;
                if (passedTime2 >= 0) {
                    if (passedTime2 < 200) {
                        drawStage2(canvas, passedTime2 / 200.0f);
                    } else {
                        int passedTime3 = passedTime2 - 500;
                        if (passedTime3 >= 0) {
                            if (passedTime3 < 150) {
                                drawStage3(canvas, passedTime3 / 150.0f);
                            } else {
                                drawStage3(canvas, 1.0f);
                                if (passedTime3 - 150 >= 100) {
                                    this.startedTime = System.currentTimeMillis();
                                }
                            }
                        } else {
                            drawStage2(canvas, 1.0f);
                        }
                    }
                } else {
                    drawStage1(canvas, 1.0f);
                }
            }
            invalidateSelf();
            return;
        }
        drawStage1(canvas, 0.0f);
    }

    private void drawStage1(Canvas canvas, float progress) {
        android.graphics.Rect bounds = getBounds();
        float progress2 = this.interpolator.getInterpolation(progress);
        this.tempRect.left = (int) (AndroidUtilities.dp(2.0f) * this.scaleX);
        this.tempRect.bottom = bounds.bottom - ((int) (AndroidUtilities.dp(6.0f) * this.scaleY));
        this.tempRect.right = bounds.right - this.tempRect.left;
        android.graphics.Rect rect = this.tempRect;
        rect.top = rect.bottom - ((int) (AndroidUtilities.dp(4.0f) * this.scaleY));
        this.secondaryRectDrawable.setBounds(this.tempRect);
        this.secondaryRectDrawable.draw(canvas);
        android.graphics.Rect rect2 = this.tempRect;
        int dp = AndroidUtilities.dp(12.0f);
        rect2.right = dp;
        rect2.left = dp;
        android.graphics.Rect rect3 = this.tempRect;
        int dp2 = AndroidUtilities.dp(8.0f);
        rect3.bottom = dp2;
        rect3.top = dp2;
        this.tempRect.inset(-AndroidUtilities.dp(AndroidUtilities.lerp(10, 11, progress2)), -AndroidUtilities.dp(AndroidUtilities.lerp(2, 3, progress2)));
        this.primaryRectDrawable.setBounds(this.tempRect);
        this.primaryRectDrawable.setAlpha(AndroidUtilities.lerp(128, 255, progress2));
        this.primaryRectDrawable.draw(canvas);
    }

    private void drawStage2(Canvas canvas, float progress) {
        android.graphics.Rect bounds = getBounds();
        float progress2 = this.interpolator.getInterpolation(progress);
        this.tempRect.left = (int) (AndroidUtilities.dp(2.0f) * this.scaleX);
        this.tempRect.bottom = bounds.bottom - ((int) (AndroidUtilities.dp(6.0f) * this.scaleY));
        this.tempRect.right = bounds.right - this.tempRect.left;
        android.graphics.Rect rect = this.tempRect;
        rect.top = rect.bottom - ((int) (AndroidUtilities.dp(4.0f) * this.scaleY));
        this.tempRect.offset(0, AndroidUtilities.dp(AndroidUtilities.lerp(0, -8, progress2)));
        this.secondaryRectDrawable.setBounds(this.tempRect);
        this.secondaryRectDrawable.draw(canvas);
        this.tempRect.left = (int) (AndroidUtilities.dpf2(AndroidUtilities.lerp(1, 2, progress2)) * this.scaleX);
        this.tempRect.top = (int) (AndroidUtilities.dpf2(AndroidUtilities.lerp(5, 6, progress2)) * this.scaleY);
        this.tempRect.right = bounds.right - this.tempRect.left;
        android.graphics.Rect rect2 = this.tempRect;
        rect2.bottom = rect2.top + ((int) (AndroidUtilities.dpf2(AndroidUtilities.lerp(6, 4, progress2)) * this.scaleY));
        this.tempRect.offset(0, AndroidUtilities.dp(AndroidUtilities.lerp(0, 8, progress2)));
        this.primaryRectDrawable.setBounds(this.tempRect);
        this.primaryRectDrawable.setAlpha(255);
        this.primaryRectDrawable.draw(canvas);
    }

    private void drawStage3(Canvas canvas, float progress) {
        android.graphics.Rect bounds = getBounds();
        float progress2 = this.interpolator.getInterpolation(progress);
        this.tempRect.left = (int) (AndroidUtilities.dp(2.0f) * this.scaleX);
        this.tempRect.bottom = bounds.bottom - ((int) (AndroidUtilities.dp(6.0f) * this.scaleY));
        this.tempRect.right = bounds.right - this.tempRect.left;
        android.graphics.Rect rect = this.tempRect;
        rect.top = rect.bottom - ((int) (AndroidUtilities.dp(4.0f) * this.scaleY));
        this.tempRect.offset(0, AndroidUtilities.dp(-8.0f));
        this.secondaryRectDrawable.setBounds(this.tempRect);
        this.secondaryRectDrawable.draw(canvas);
        this.tempRect.left = (int) (AndroidUtilities.dpf2(2.0f) * this.scaleX);
        this.tempRect.top = (int) (AndroidUtilities.dpf2(6.0f) * this.scaleY);
        this.tempRect.right = bounds.right - this.tempRect.left;
        android.graphics.Rect rect2 = this.tempRect;
        rect2.bottom = rect2.top + ((int) (AndroidUtilities.dpf2(4.0f) * this.scaleY));
        this.tempRect.offset(0, AndroidUtilities.dp(8.0f));
        this.primaryRectDrawable.setBounds(this.tempRect);
        this.primaryRectDrawable.setAlpha(AndroidUtilities.lerp(255, 128, progress2));
        this.primaryRectDrawable.draw(canvas);
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.primaryRectDrawable.setColorFilter(colorFilter);
        this.secondaryRectDrawable.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.intrinsicWidth;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.intrinsicHeight;
    }

    /* loaded from: classes5.dex */
    public static class RectDrawable extends Drawable {
        private final RectF tempRect = new RectF();
        private final Paint paint = new Paint(1);

        protected RectDrawable() {
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            this.tempRect.set(getBounds());
            float radius = this.tempRect.height() * 0.2f;
            canvas.drawRoundRect(this.tempRect, radius, radius, this.paint);
        }

        public void setColor(int color) {
            this.paint.setColor(color);
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int alpha) {
            this.paint.setAlpha(alpha);
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
            this.paint.setColorFilter(colorFilter);
        }

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return -3;
        }
    }
}
