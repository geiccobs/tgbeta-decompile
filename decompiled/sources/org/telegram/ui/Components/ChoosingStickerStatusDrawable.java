package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class ChoosingStickerStatusDrawable extends StatusDrawable {
    int color;
    Paint fillPaint;
    float progress;
    Paint strokePaint;
    private boolean isChat = false;
    private long lastUpdateTime = 0;
    private boolean started = false;
    boolean increment = true;

    public ChoosingStickerStatusDrawable(boolean createPaint) {
        if (createPaint) {
            this.strokePaint = new Paint(1);
            this.fillPaint = new Paint(1);
            this.strokePaint.setStyle(Paint.Style.STROKE);
            this.strokePaint.setStrokeWidth(AndroidUtilities.dpf2(1.2f));
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

    @Override // org.telegram.ui.Components.StatusDrawable
    public void setIsChat(boolean value) {
        this.isChat = value;
    }

    @Override // org.telegram.ui.Components.StatusDrawable
    public void setColor(int color) {
        if (this.color != color) {
            this.fillPaint.setColor(color);
            this.strokePaint.setColor(color);
        }
        this.color = color;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        float xOffset;
        float cx;
        float animationProgress = Math.min(this.progress, 1.0f);
        float k = 39322;
        float p = CubicBezierInterpolator.EASE_IN.getInterpolation(animationProgress < 0.3f ? animationProgress / 0.3f : 1.0f);
        float p2 = CubicBezierInterpolator.EASE_OUT.getInterpolation(animationProgress < 0.3f ? 0.0f : (animationProgress - 0.3f) / (1.0f - 0.3f));
        float f = 2.0f;
        if (this.increment) {
            cx = (AndroidUtilities.dp(2.1f) * p) + ((AndroidUtilities.dp(7.0f) - AndroidUtilities.dp(2.1f)) * (1.0f - p));
            xOffset = AndroidUtilities.dpf2(1.5f) * (1.0f - CubicBezierInterpolator.EASE_OUT.getInterpolation(this.progress / 2.0f));
        } else {
            cx = (AndroidUtilities.dp(2.1f) * (1.0f - p)) + ((AndroidUtilities.dp(7.0f) - AndroidUtilities.dp(2.1f)) * p);
            xOffset = CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(this.progress / 2.0f) * AndroidUtilities.dpf2(1.5f);
        }
        float cy = AndroidUtilities.dp(11.0f) / 2.0f;
        float r = AndroidUtilities.dpf2(2.0f);
        float scaleOffset = (AndroidUtilities.dpf2(0.5f) * p) - (AndroidUtilities.dpf2(0.5f) * p2);
        Paint strokePaint = this.strokePaint;
        if (strokePaint == null) {
            strokePaint = Theme.chat_statusRecordPaint;
        }
        Paint paint = this.fillPaint;
        if (paint == null) {
            paint = Theme.chat_statusPaint;
        }
        if (strokePaint.getStrokeWidth() != AndroidUtilities.dp(0.8f)) {
            strokePaint.setStrokeWidth(AndroidUtilities.dp(0.8f));
        }
        int i = 0;
        while (i < 2) {
            canvas.save();
            canvas.translate((strokePaint.getStrokeWidth() / f) + xOffset + (AndroidUtilities.dp(9.0f) * i) + getBounds().left + AndroidUtilities.dpf2(0.2f), (strokePaint.getStrokeWidth() / 2.0f) + AndroidUtilities.dpf2(2.0f) + getBounds().top);
            AndroidUtilities.rectTmp.set(0.0f, scaleOffset, AndroidUtilities.dp(7.0f), AndroidUtilities.dp(11.0f) - scaleOffset);
            canvas.drawOval(AndroidUtilities.rectTmp, strokePaint);
            canvas.drawCircle(cx, cy, r, paint);
            canvas.restore();
            i++;
            animationProgress = animationProgress;
            k = k;
            f = 2.0f;
        }
        if (this.started) {
            update();
        }
    }

    private void update() {
        long newTime = System.currentTimeMillis();
        long dt = newTime - this.lastUpdateTime;
        this.lastUpdateTime = newTime;
        if (dt > 16) {
            dt = 16;
        }
        float f = this.progress + (((float) dt) / 500.0f);
        this.progress = f;
        if (f >= 2.0f) {
            this.progress = 0.0f;
            this.increment = !this.increment;
        }
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return 0;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(20.0f);
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(18.0f);
    }
}
