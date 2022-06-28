package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class TypingDotsDrawable extends StatusDrawable {
    private Paint currentPaint;
    private int currentAccount = UserConfig.selectedAccount;
    private boolean isChat = false;
    private float[] scales = new float[3];
    private float[] startTimes = {0.0f, 150.0f, 300.0f};
    private float[] elapsedTimes = {0.0f, 0.0f, 0.0f};
    private long lastUpdateTime = 0;
    private boolean started = false;
    private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();

    public TypingDotsDrawable(boolean createPaint) {
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
        for (int a = 0; a < 3; a++) {
            float[] fArr = this.elapsedTimes;
            fArr[a] = fArr[a] + ((float) dt);
            float f = fArr[a];
            float[] fArr2 = this.startTimes;
            float timeSinceStart = f - fArr2[a];
            if (timeSinceStart > 0.0f) {
                if (timeSinceStart <= 320.0f) {
                    float diff = this.decelerateInterpolator.getInterpolation(timeSinceStart / 320.0f);
                    this.scales[a] = 1.33f + diff;
                } else if (timeSinceStart <= 640.0f) {
                    float diff2 = this.decelerateInterpolator.getInterpolation((timeSinceStart - 320.0f) / 320.0f);
                    this.scales[a] = (1.0f - diff2) + 1.33f;
                } else if (timeSinceStart >= 800.0f) {
                    fArr[a] = 0.0f;
                    fArr2[a] = 0.0f;
                    this.scales[a] = 1.33f;
                } else {
                    this.scales[a] = 1.33f;
                }
            } else {
                this.scales[a] = 1.33f;
            }
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
        for (int a = 0; a < 3; a++) {
            this.elapsedTimes[a] = 0.0f;
            this.scales[a] = 1.33f;
        }
        float[] fArr = this.startTimes;
        fArr[0] = 0.0f;
        fArr[1] = 150.0f;
        fArr[2] = 300.0f;
        this.started = false;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        int y;
        Paint paint;
        if (this.isChat) {
            y = AndroidUtilities.dp(8.5f) + getBounds().top;
        } else {
            y = AndroidUtilities.dp(9.3f) + getBounds().top;
        }
        if (this.currentPaint == null) {
            paint = Theme.chat_statusPaint;
            paint.setAlpha(255);
        } else {
            paint = this.currentPaint;
        }
        canvas.drawCircle(AndroidUtilities.dp(3.0f), y, this.scales[0] * AndroidUtilities.density, paint);
        canvas.drawCircle(AndroidUtilities.dp(9.0f), y, this.scales[1] * AndroidUtilities.density, paint);
        canvas.drawCircle(AndroidUtilities.dp(15.0f), y, this.scales[2] * AndroidUtilities.density, paint);
        checkUpdate();
    }

    public void checkUpdate() {
        if (this.started) {
            if (!NotificationCenter.getInstance(this.currentAccount).isAnimationInProgress()) {
                update();
            } else {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.TypingDotsDrawable$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        TypingDotsDrawable.this.checkUpdate();
                    }
                }, 100L);
            }
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
        return -2;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(18.0f);
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(18.0f);
    }
}
