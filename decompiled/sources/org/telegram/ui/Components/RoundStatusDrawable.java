package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class RoundStatusDrawable extends StatusDrawable {
    private Paint currentPaint;
    private float progress;
    private boolean isChat = false;
    private long lastUpdateTime = 0;
    private boolean started = false;
    private int progressDirection = 1;

    public RoundStatusDrawable(boolean createPaint) {
        if (createPaint) {
            this.currentPaint = new Paint(1);
        }
    }

    @Override // org.telegram.ui.Components.StatusDrawable
    public void setColor(int color) {
        Paint paint = this.currentPaint;
        if (paint != null) {
            paint.setColor(color);
        }
    }

    @Override // org.telegram.ui.Components.StatusDrawable
    public void setIsChat(boolean value) {
        this.isChat = value;
    }

    private void update() {
        long newTime = System.currentTimeMillis();
        long dt = newTime - this.lastUpdateTime;
        this.lastUpdateTime = newTime;
        if (dt > 50) {
            dt = 50;
        }
        float f = this.progress;
        int i = this.progressDirection;
        float f2 = f + (((float) (i * dt)) / 400.0f);
        this.progress = f2;
        if (i > 0 && f2 >= 1.0f) {
            this.progressDirection = -1;
            this.progress = 1.0f;
        } else if (i < 0 && f2 <= 0.0f) {
            this.progressDirection = 1;
            this.progress = 0.0f;
        }
        invalidateSelf();
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
            paint = Theme.chat_statusPaint;
        }
        paint.setAlpha(((int) (this.progress * 200.0f)) + 55);
        canvas.drawCircle(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(this.isChat ? 8.0f : 9.0f), AndroidUtilities.dp(4.0f), paint);
        if (this.started) {
            update();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
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
        return AndroidUtilities.dp(12.0f);
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(10.0f);
    }
}
