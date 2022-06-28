package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
/* loaded from: classes5.dex */
public class PhotoEditorSeekBar extends View {
    private PhotoEditorSeekBarDelegate delegate;
    private int maxValue;
    private int minValue;
    private Paint innerPaint = new Paint();
    private Paint outerPaint = new Paint(1);
    private int thumbSize = AndroidUtilities.dp(16.0f);
    private int thumbDX = 0;
    private float progress = 0.0f;
    private boolean pressed = false;

    /* loaded from: classes5.dex */
    public interface PhotoEditorSeekBarDelegate {
        void onProgressChanged(int i, int i2);
    }

    public PhotoEditorSeekBar(Context context) {
        super(context);
        this.innerPaint.setColor(-11711155);
        this.outerPaint.setColor(-1);
    }

    public void setDelegate(PhotoEditorSeekBarDelegate delegate) {
        this.delegate = delegate;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (event == null) {
            return false;
        }
        float x = event.getX();
        float y = event.getY();
        float thumbX = (int) ((getMeasuredWidth() - this.thumbSize) * this.progress);
        if (event.getAction() == 0) {
            int measuredHeight = getMeasuredHeight();
            int i = this.thumbSize;
            int additionWidth = (measuredHeight - i) / 2;
            if (thumbX - additionWidth <= x && x <= i + thumbX + additionWidth && y >= 0.0f && y <= getMeasuredHeight()) {
                this.pressed = true;
                this.thumbDX = (int) (x - thumbX);
                getParent().requestDisallowInterceptTouchEvent(true);
                invalidate();
                return true;
            }
        } else if (event.getAction() == 1 || event.getAction() == 3) {
            if (this.pressed) {
                this.pressed = false;
                invalidate();
                return true;
            }
        } else if (event.getAction() == 2 && this.pressed) {
            float thumbX2 = (int) (x - this.thumbDX);
            if (thumbX2 < 0.0f) {
                thumbX2 = 0.0f;
            } else if (thumbX2 > getMeasuredWidth() - this.thumbSize) {
                thumbX2 = getMeasuredWidth() - this.thumbSize;
            }
            this.progress = thumbX2 / (getMeasuredWidth() - this.thumbSize);
            PhotoEditorSeekBarDelegate photoEditorSeekBarDelegate = this.delegate;
            if (photoEditorSeekBarDelegate != null) {
                photoEditorSeekBarDelegate.onProgressChanged(((Integer) getTag()).intValue(), getProgress());
            }
            invalidate();
            return true;
        }
        return false;
    }

    public void setProgress(int progress) {
        setProgress(progress, true);
    }

    public void setProgress(int progress, boolean notify) {
        PhotoEditorSeekBarDelegate photoEditorSeekBarDelegate;
        int i = this.minValue;
        if (progress < i) {
            progress = this.minValue;
        } else if (progress > this.maxValue) {
            progress = this.maxValue;
        }
        this.progress = (progress - i) / (this.maxValue - i);
        invalidate();
        if (notify && (photoEditorSeekBarDelegate = this.delegate) != null) {
            photoEditorSeekBarDelegate.onProgressChanged(((Integer) getTag()).intValue(), getProgress());
        }
    }

    public int getProgress() {
        int i = this.minValue;
        return (int) (i + (this.progress * (this.maxValue - i)));
    }

    public void setMinMax(int min, int max) {
        this.minValue = min;
        this.maxValue = max;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int y = (getMeasuredHeight() - this.thumbSize) / 2;
        int measuredWidth = getMeasuredWidth();
        int i = this.thumbSize;
        int thumbX = (int) ((measuredWidth - i) * this.progress);
        canvas.drawRect(i / 2, (getMeasuredHeight() / 2) - AndroidUtilities.dp(1.0f), getMeasuredWidth() - (this.thumbSize / 2), (getMeasuredHeight() / 2) + AndroidUtilities.dp(1.0f), this.innerPaint);
        if (this.minValue == 0) {
            canvas.drawRect(this.thumbSize / 2, (getMeasuredHeight() / 2) - AndroidUtilities.dp(1.0f), thumbX, (getMeasuredHeight() / 2) + AndroidUtilities.dp(1.0f), this.outerPaint);
        } else if (this.progress > 0.5f) {
            canvas.drawRect((getMeasuredWidth() / 2) - AndroidUtilities.dp(1.0f), (getMeasuredHeight() - this.thumbSize) / 2, getMeasuredWidth() / 2, (getMeasuredHeight() + this.thumbSize) / 2, this.outerPaint);
            canvas.drawRect(getMeasuredWidth() / 2, (getMeasuredHeight() / 2) - AndroidUtilities.dp(1.0f), thumbX, (getMeasuredHeight() / 2) + AndroidUtilities.dp(1.0f), this.outerPaint);
        } else {
            canvas.drawRect(getMeasuredWidth() / 2, (getMeasuredHeight() - this.thumbSize) / 2, (getMeasuredWidth() / 2) + AndroidUtilities.dp(1.0f), (getMeasuredHeight() + this.thumbSize) / 2, this.outerPaint);
            canvas.drawRect(thumbX, (getMeasuredHeight() / 2) - AndroidUtilities.dp(1.0f), getMeasuredWidth() / 2, (getMeasuredHeight() / 2) + AndroidUtilities.dp(1.0f), this.outerPaint);
        }
        int i2 = this.thumbSize;
        canvas.drawCircle((i2 / 2) + thumbX, (i2 / 2) + y, i2 / 2, this.outerPaint);
    }
}
