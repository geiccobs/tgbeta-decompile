package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class RecordStatusDrawable extends StatusDrawable {
    Paint currentPaint;
    private float progress;
    private boolean isChat = false;
    private long lastUpdateTime = 0;
    private boolean started = false;
    private RectF rect = new RectF();
    int alpha = 255;

    public RecordStatusDrawable(boolean createPaint) {
        if (createPaint) {
            Paint paint = new Paint(1);
            this.currentPaint = paint;
            paint.setStyle(Paint.Style.STROKE);
            this.currentPaint.setStrokeCap(Paint.Cap.ROUND);
            this.currentPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        }
    }

    @Override // org.telegram.ui.Components.StatusDrawable
    public void setIsChat(boolean value) {
        this.isChat = value;
    }

    @Override // org.telegram.ui.Components.StatusDrawable
    public void setColor(int color) {
        Paint paint = this.currentPaint;
        if (paint != null) {
            paint.setColor(color);
        }
    }

    private void update() {
        long newTime = System.currentTimeMillis();
        long dt = newTime - this.lastUpdateTime;
        this.lastUpdateTime = newTime;
        if (dt > 50) {
            dt = 50;
        }
        this.progress += ((float) dt) / 800.0f;
        while (true) {
            float f = this.progress;
            if (f > 1.0f) {
                this.progress = f - 1.0f;
            } else {
                invalidateSelf();
                return;
            }
        }
    }

    @Override // org.telegram.ui.Components.StatusDrawable
    public void start() {
        this.lastUpdateTime = System.currentTimeMillis();
        this.started = true;
        invalidateSelf();
    }

    @Override // org.telegram.ui.Components.StatusDrawable
    public void stop() {
        this.started = false;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        Paint paint = this.currentPaint;
        if (paint == null) {
            paint = Theme.chat_statusRecordPaint;
        }
        float f = 2.0f;
        if (paint.getStrokeWidth() != AndroidUtilities.dp(2.0f)) {
            paint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        }
        canvas.save();
        int intrinsicHeight = getIntrinsicHeight() / 2;
        if (this.isChat) {
            f = 1.0f;
        }
        canvas.translate(0.0f, intrinsicHeight + AndroidUtilities.dp(f));
        for (int a = 0; a < 4; a++) {
            if (a == 0) {
                paint.setAlpha((int) (this.alpha * this.progress));
            } else if (a == 3) {
                paint.setAlpha((int) (this.alpha * (1.0f - this.progress)));
            } else {
                paint.setAlpha(this.alpha);
            }
            float side = (AndroidUtilities.dp(4.0f) * a) + (AndroidUtilities.dp(4.0f) * this.progress);
            this.rect.set(-side, -side, side, side);
            canvas.drawArc(this.rect, -15.0f, 30.0f, false, paint);
        }
        canvas.restore();
        if (this.started) {
            update();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter cf) {
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return 0;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(18.0f);
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(14.0f);
    }
}
