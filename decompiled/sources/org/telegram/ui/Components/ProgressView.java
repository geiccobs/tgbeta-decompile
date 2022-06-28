package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Paint;
import org.telegram.messenger.AndroidUtilities;
/* loaded from: classes5.dex */
public class ProgressView {
    public int height;
    public int width;
    public float currentProgress = 0.0f;
    public float progressHeight = AndroidUtilities.dp(2.0f);
    private Paint innerPaint = new Paint();
    private Paint outerPaint = new Paint();

    public void setProgressColors(int innerColor, int outerColor) {
        this.innerPaint.setColor(innerColor);
        this.outerPaint.setColor(outerColor);
    }

    public void setProgress(float progress) {
        this.currentProgress = progress;
        if (progress < 0.0f) {
            this.currentProgress = 0.0f;
        } else if (progress > 1.0f) {
            this.currentProgress = 1.0f;
        }
    }

    public void draw(Canvas canvas) {
        int i = this.height;
        float f = this.progressHeight;
        canvas.drawRect(0.0f, (i / 2) - (f / 2.0f), this.width, (i / 2) + (f / 2.0f), this.innerPaint);
        int i2 = this.height;
        float f2 = this.progressHeight;
        canvas.drawRect(0.0f, (i2 / 2) - (f2 / 2.0f), this.width * this.currentProgress, (i2 / 2) + (f2 / 2.0f), this.outerPaint);
    }
}
