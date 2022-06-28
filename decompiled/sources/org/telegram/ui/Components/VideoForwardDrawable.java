package org.telegram.ui.Components;

import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
/* loaded from: classes5.dex */
public class VideoForwardDrawable extends Drawable {
    private static final int[] playPath = {10, 7, 26, 16, 10, 25};
    private boolean animating;
    private float animationProgress;
    private Path clippingPath;
    private VideoForwardDrawableDelegate delegate;
    private float enterAnimationProgress;
    private boolean isOneShootAnimation;
    private boolean isRound;
    private long lastAnimationTime;
    private int lastClippingPath;
    private boolean leftSide;
    private boolean showing;
    private long time;
    private String timeStr;
    private Paint paint = new Paint(1);
    private TextPaint textPaint = new TextPaint(1);
    private Path path1 = new Path();
    private float playScaleFactor = 1.0f;

    /* loaded from: classes5.dex */
    public interface VideoForwardDrawableDelegate {
        void invalidate();

        void onAnimationEnd();
    }

    public void setTime(long dt) {
        this.time = dt;
        if (dt >= 1000) {
            this.timeStr = LocaleController.formatPluralString("Seconds", (int) (dt / 1000), new Object[0]);
        } else {
            this.timeStr = null;
        }
    }

    public VideoForwardDrawable(boolean isRound) {
        this.isRound = isRound;
        this.paint.setColor(-1);
        this.textPaint.setColor(-1);
        this.textPaint.setTextSize(AndroidUtilities.dp(12.0f));
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        this.path1.reset();
        int a = 0;
        while (true) {
            int[] iArr = playPath;
            if (a < iArr.length / 2) {
                if (a == 0) {
                    this.path1.moveTo(AndroidUtilities.dp(iArr[a * 2]), AndroidUtilities.dp(iArr[(a * 2) + 1]));
                } else {
                    this.path1.lineTo(AndroidUtilities.dp(iArr[a * 2]), AndroidUtilities.dp(iArr[(a * 2) + 1]));
                }
                a++;
            } else {
                this.path1.close();
                return;
            }
        }
    }

    public void setPlayScaleFactor(float playScaleFactor) {
        this.playScaleFactor = playScaleFactor;
        invalidate();
    }

    public boolean isAnimating() {
        return this.animating;
    }

    public void startAnimation() {
        this.animating = true;
        this.animationProgress = 0.0f;
        invalidateSelf();
    }

    public void setOneShootAnimation(boolean isOneShootAnimation) {
        if (this.isOneShootAnimation != isOneShootAnimation) {
            this.isOneShootAnimation = isOneShootAnimation;
            this.timeStr = null;
            this.time = 0L;
            this.animationProgress = 0.0f;
        }
    }

    public void setLeftSide(boolean value) {
        boolean z = this.leftSide;
        if (z == value && this.animationProgress >= 1.0f && this.isOneShootAnimation) {
            return;
        }
        if (z != value) {
            this.time = 0L;
            this.timeStr = null;
        }
        this.leftSide = value;
        startAnimation();
    }

    public void setDelegate(VideoForwardDrawableDelegate videoForwardDrawableDelegate) {
        this.delegate = videoForwardDrawableDelegate;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        this.paint.setAlpha(alpha);
        this.textPaint.setAlpha(alpha);
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.paint.setColorFilter(colorFilter);
    }

    public void setColor(int value) {
        this.paint.setColor(value);
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -2;
    }

    /* JADX WARN: Removed duplicated region for block: B:104:0x02af  */
    /* JADX WARN: Removed duplicated region for block: B:105:0x02b2  */
    @Override // android.graphics.drawable.Drawable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void draw(android.graphics.Canvas r15) {
        /*
            Method dump skipped, instructions count: 697
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.VideoForwardDrawable.draw(android.graphics.Canvas):void");
    }

    public void setShowing(boolean showing) {
        this.showing = showing;
        invalidate();
    }

    private void invalidate() {
        VideoForwardDrawableDelegate videoForwardDrawableDelegate = this.delegate;
        if (videoForwardDrawableDelegate != null) {
            videoForwardDrawableDelegate.invalidate();
        } else {
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(32.0f);
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(32.0f);
    }

    @Override // android.graphics.drawable.Drawable
    public int getMinimumWidth() {
        return AndroidUtilities.dp(32.0f);
    }

    @Override // android.graphics.drawable.Drawable
    public int getMinimumHeight() {
        return AndroidUtilities.dp(32.0f);
    }

    public void addTime(long time) {
        long j = this.time + time;
        this.time = j;
        this.timeStr = LocaleController.formatPluralString("Seconds", (int) (j / 1000), new Object[0]);
    }
}
