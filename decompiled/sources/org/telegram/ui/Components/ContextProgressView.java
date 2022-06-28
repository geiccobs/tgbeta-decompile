package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class ContextProgressView extends View {
    private int currentColorType;
    private int innerColor;
    private String innerKey;
    private long lastUpdateTime;
    private int outerColor;
    private String outerKey;
    private Paint innerPaint = new Paint(1);
    private Paint outerPaint = new Paint(1);
    private RectF cicleRect = new RectF();
    private int radOffset = 0;

    public ContextProgressView(Context context, int colorType) {
        super(context);
        this.innerPaint.setStyle(Paint.Style.STROKE);
        this.innerPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        this.outerPaint.setStyle(Paint.Style.STROKE);
        this.outerPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        this.outerPaint.setStrokeCap(Paint.Cap.ROUND);
        if (colorType == 0) {
            this.innerKey = Theme.key_contextProgressInner1;
            this.outerKey = Theme.key_contextProgressOuter1;
        } else if (colorType == 1) {
            this.innerKey = Theme.key_contextProgressInner2;
            this.outerKey = Theme.key_contextProgressOuter2;
        } else if (colorType == 2) {
            this.innerKey = Theme.key_contextProgressInner3;
            this.outerKey = Theme.key_contextProgressOuter3;
        } else if (colorType == 3) {
            this.innerKey = Theme.key_contextProgressInner4;
            this.outerKey = Theme.key_contextProgressOuter4;
        }
        updateColors();
    }

    public void setColors(int innerColor, int outerColor) {
        this.innerKey = null;
        this.outerKey = null;
        this.innerColor = innerColor;
        this.outerColor = outerColor;
        updateColors();
    }

    public void updateColors() {
        String str = this.innerKey;
        if (str != null) {
            this.innerPaint.setColor(Theme.getColor(str));
        } else {
            this.innerPaint.setColor(this.innerColor);
        }
        String str2 = this.outerKey;
        if (str2 != null) {
            this.outerPaint.setColor(Theme.getColor(str2));
        } else {
            this.outerPaint.setColor(this.outerColor);
        }
        invalidate();
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (getVisibility() != 0) {
            return;
        }
        long newTime = System.currentTimeMillis();
        long dt = newTime - this.lastUpdateTime;
        this.lastUpdateTime = newTime;
        this.radOffset = (int) (this.radOffset + (((float) (360 * dt)) / 1000.0f));
        int x = (getMeasuredWidth() / 2) - AndroidUtilities.dp(9.0f);
        int y = (getMeasuredHeight() / 2) - AndroidUtilities.dp(9.0f);
        this.cicleRect.set(x, y, AndroidUtilities.dp(18.0f) + x, AndroidUtilities.dp(18.0f) + y);
        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, AndroidUtilities.dp(9.0f), this.innerPaint);
        canvas.drawArc(this.cicleRect, this.radOffset - 90, 90.0f, false, this.outerPaint);
        invalidate();
    }
}
