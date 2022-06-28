package org.telegram.ui.Components;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.View;
import androidx.core.app.NotificationCompat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
/* loaded from: classes5.dex */
public class RadioButton extends View {
    private static Paint checkedPaint;
    private static Paint eraser;
    private static Paint paint;
    private boolean attachedToWindow;
    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private ObjectAnimator checkAnimator;
    private int checkedColor;
    private int color;
    private boolean isChecked;
    private float progress;
    private int size = AndroidUtilities.dp(16.0f);

    public RadioButton(Context context) {
        super(context);
        if (paint == null) {
            Paint paint2 = new Paint(1);
            paint = paint2;
            paint2.setStrokeWidth(AndroidUtilities.dp(2.0f));
            paint.setStyle(Paint.Style.STROKE);
            checkedPaint = new Paint(1);
            Paint paint3 = new Paint(1);
            eraser = paint3;
            paint3.setColor(0);
            eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
        try {
            this.bitmap = Bitmap.createBitmap(AndroidUtilities.dp(this.size), AndroidUtilities.dp(this.size), Bitmap.Config.ARGB_4444);
            this.bitmapCanvas = new Canvas(this.bitmap);
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public void setProgress(float value) {
        if (this.progress == value) {
            return;
        }
        this.progress = value;
        invalidate();
    }

    public float getProgress() {
        return this.progress;
    }

    public void setSize(int value) {
        if (this.size == value) {
            return;
        }
        this.size = value;
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int color1, int color2) {
        this.color = color1;
        this.checkedColor = color2;
        invalidate();
    }

    @Override // android.view.View
    public void setBackgroundColor(int color1) {
        this.color = color1;
        invalidate();
    }

    public void setCheckedColor(int color2) {
        this.checkedColor = color2;
        invalidate();
    }

    private void cancelCheckAnimator() {
        ObjectAnimator objectAnimator = this.checkAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
    }

    private void animateToCheckedState(boolean newCheckedState) {
        float[] fArr = new float[1];
        fArr[0] = newCheckedState ? 1.0f : 0.0f;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, NotificationCompat.CATEGORY_PROGRESS, fArr);
        this.checkAnimator = ofFloat;
        ofFloat.setDuration(200L);
        this.checkAnimator.start();
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attachedToWindow = true;
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attachedToWindow = false;
    }

    public void setChecked(boolean checked, boolean animated) {
        if (checked == this.isChecked) {
            return;
        }
        this.isChecked = checked;
        if (this.attachedToWindow && animated) {
            animateToCheckedState(checked);
            return;
        }
        cancelCheckAnimator();
        setProgress(checked ? 1.0f : 0.0f);
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        float circleProgress;
        Bitmap bitmap = this.bitmap;
        if (bitmap == null || bitmap.getWidth() != getMeasuredWidth()) {
            Bitmap bitmap2 = this.bitmap;
            if (bitmap2 != null) {
                bitmap2.recycle();
                this.bitmap = null;
            }
            try {
                this.bitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                this.bitmapCanvas = new Canvas(this.bitmap);
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        float f = this.progress;
        if (f <= 0.5f) {
            paint.setColor(this.color);
            checkedPaint.setColor(this.color);
            circleProgress = this.progress / 0.5f;
        } else {
            circleProgress = 2.0f - (f / 0.5f);
            int r1 = Color.red(this.color);
            int rD = (int) ((Color.red(this.checkedColor) - r1) * (1.0f - circleProgress));
            int g1 = Color.green(this.color);
            int gD = (int) ((Color.green(this.checkedColor) - g1) * (1.0f - circleProgress));
            int b1 = Color.blue(this.color);
            int bD = (int) ((Color.blue(this.checkedColor) - b1) * (1.0f - circleProgress));
            int c = Color.rgb(r1 + rD, g1 + gD, b1 + bD);
            paint.setColor(c);
            checkedPaint.setColor(c);
        }
        Bitmap bitmap3 = this.bitmap;
        if (bitmap3 != null) {
            bitmap3.eraseColor(0);
            float rad = (this.size / 2) - ((circleProgress + 1.0f) * AndroidUtilities.density);
            this.bitmapCanvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, rad, paint);
            if (this.progress <= 0.5f) {
                this.bitmapCanvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, rad - AndroidUtilities.dp(1.0f), checkedPaint);
                this.bitmapCanvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, (rad - AndroidUtilities.dp(1.0f)) * (1.0f - circleProgress), eraser);
            } else {
                this.bitmapCanvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, (this.size / 4) + (((rad - AndroidUtilities.dp(1.0f)) - (this.size / 4)) * circleProgress), checkedPaint);
            }
            canvas.drawBitmap(this.bitmap, 0.0f, 0.0f, (Paint) null);
        }
    }
}
