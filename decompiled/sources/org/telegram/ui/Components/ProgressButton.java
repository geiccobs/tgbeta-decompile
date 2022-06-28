package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.widget.Button;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class ProgressButton extends Button {
    private int angle;
    private boolean drawProgress;
    private long lastUpdateTime;
    private float progressAlpha;
    private final Paint progressPaint;
    private final RectF progressRect;

    public ProgressButton(Context context) {
        super(context);
        setAllCaps(false);
        setTextSize(1, 14.0f);
        setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        if (Build.VERSION.SDK_INT >= 21) {
            setOutlineProvider(null);
        }
        ViewHelper.setPadding(this, 8.0f, 0.0f, 8.0f, 0.0f);
        int minWidth = AndroidUtilities.dp(60.0f);
        setMinWidth(minWidth);
        setMinimumWidth(minWidth);
        this.progressRect = new RectF();
        Paint paint = new Paint(1);
        this.progressPaint = paint;
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(AndroidUtilities.dp(2.0f));
    }

    @Override // android.widget.TextView, android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.drawProgress || this.progressAlpha != 0.0f) {
            int x = getMeasuredWidth() - AndroidUtilities.dp(11.0f);
            this.progressRect.set(x, AndroidUtilities.dp(3.0f), AndroidUtilities.dp(8.0f) + x, AndroidUtilities.dp(11.0f));
            this.progressPaint.setAlpha(Math.min(255, (int) (this.progressAlpha * 255.0f)));
            canvas.drawArc(this.progressRect, this.angle, 220.0f, false, this.progressPaint);
            long newTime = System.currentTimeMillis();
            if (Math.abs(this.lastUpdateTime - System.currentTimeMillis()) < 1000) {
                long delta = newTime - this.lastUpdateTime;
                float dt = ((float) (360 * delta)) / 2000.0f;
                int i = (int) (this.angle + dt);
                this.angle = i;
                this.angle = i - ((i / 360) * 360);
                if (this.drawProgress) {
                    float f = this.progressAlpha;
                    if (f < 1.0f) {
                        float f2 = f + (((float) delta) / 200.0f);
                        this.progressAlpha = f2;
                        if (f2 > 1.0f) {
                            this.progressAlpha = 1.0f;
                        }
                    }
                } else {
                    float f3 = this.progressAlpha;
                    if (f3 > 0.0f) {
                        float f4 = f3 - (((float) delta) / 200.0f);
                        this.progressAlpha = f4;
                        if (f4 < 0.0f) {
                            this.progressAlpha = 0.0f;
                        }
                    }
                }
            }
            this.lastUpdateTime = newTime;
            postInvalidateOnAnimation();
        }
    }

    public void setBackgroundRoundRect(int backgroundColor, int pressedBackgroundColor) {
        setBackgroundRoundRect(backgroundColor, pressedBackgroundColor, 4.0f);
    }

    public void setBackgroundRoundRect(int backgroundColor, int pressedBackgroundColor, float radius) {
        setBackground(Theme.AdaptiveRipple.filledRect(backgroundColor, radius));
    }

    public void setProgressColor(int progressColor) {
        this.progressPaint.setColor(progressColor);
    }

    public void setDrawProgress(boolean drawProgress, boolean animated) {
        if (this.drawProgress != drawProgress) {
            this.drawProgress = drawProgress;
            if (!animated) {
                this.progressAlpha = drawProgress ? 1.0f : 0.0f;
            }
            this.lastUpdateTime = System.currentTimeMillis();
            invalidate();
        }
    }
}
