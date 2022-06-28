package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;
/* loaded from: classes5.dex */
public class CloseProgressDrawable extends Drawable {
    private int currentAnimationTime;
    private int currentSegment;
    private long lastFrameTime;
    private Paint paint = new Paint(1);
    private DecelerateInterpolator interpolator = new DecelerateInterpolator();

    public CloseProgressDrawable() {
        this.paint.setColor(-9079435);
        this.paint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        this.paint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        long newTime = System.currentTimeMillis();
        long j = this.lastFrameTime;
        if (j != 0) {
            long dt = newTime - j;
            int i = (int) (this.currentAnimationTime + dt);
            this.currentAnimationTime = i;
            if (i > 200) {
                this.currentAnimationTime = 0;
                int i2 = this.currentSegment + 1;
                this.currentSegment = i2;
                if (i2 == 4) {
                    this.currentSegment = i2 - 4;
                }
            }
        }
        canvas.save();
        canvas.translate(getIntrinsicWidth() / 2, getIntrinsicHeight() / 2);
        canvas.rotate(45.0f);
        this.paint.setAlpha(255 - ((this.currentSegment % 4) * 40));
        canvas.drawLine(-AndroidUtilities.dp(8.0f), 0.0f, 0.0f, 0.0f, this.paint);
        this.paint.setAlpha(255 - (((this.currentSegment + 1) % 4) * 40));
        canvas.drawLine(0.0f, -AndroidUtilities.dp(8.0f), 0.0f, 0.0f, this.paint);
        this.paint.setAlpha(255 - (((this.currentSegment + 2) % 4) * 40));
        canvas.drawLine(0.0f, 0.0f, AndroidUtilities.dp(8.0f), 0.0f, this.paint);
        this.paint.setAlpha(255 - (((this.currentSegment + 3) % 4) * 40));
        canvas.drawLine(0.0f, 0.0f, 0.0f, AndroidUtilities.dp(8.0f), this.paint);
        canvas.restore();
        this.lastFrameTime = newTime;
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter cf) {
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -2;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(24.0f);
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(24.0f);
    }
}
