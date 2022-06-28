package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
/* loaded from: classes5.dex */
public class VideoSeekBarView extends View {
    private SeekBarDelegate delegate;
    private Paint paint = new Paint();
    private Paint paint2 = new Paint(1);
    private int thumbWidth = AndroidUtilities.dp(12.0f);
    private int thumbHeight = AndroidUtilities.dp(12.0f);
    private int thumbDX = 0;
    private float progress = 0.0f;
    private boolean pressed = false;

    /* loaded from: classes5.dex */
    public interface SeekBarDelegate {
        void onSeekBarDrag(float f);
    }

    public VideoSeekBarView(Context context) {
        super(context);
        this.paint.setColor(-10724260);
        this.paint2.setColor(-1);
    }

    public void setDelegate(SeekBarDelegate seekBarDelegate) {
        this.delegate = seekBarDelegate;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        SeekBarDelegate seekBarDelegate;
        if (event == null) {
            return false;
        }
        float x = event.getX();
        float y = event.getY();
        float thumbX = (int) ((getMeasuredWidth() - this.thumbWidth) * this.progress);
        if (event.getAction() == 0) {
            int measuredHeight = getMeasuredHeight();
            int i = this.thumbWidth;
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
                if (event.getAction() == 1 && (seekBarDelegate = this.delegate) != null) {
                    seekBarDelegate.onSeekBarDrag(thumbX / (getMeasuredWidth() - this.thumbWidth));
                }
                this.pressed = false;
                invalidate();
                return true;
            }
        } else if (event.getAction() == 2 && this.pressed) {
            float thumbX2 = (int) (x - this.thumbDX);
            if (thumbX2 < 0.0f) {
                thumbX2 = 0.0f;
            } else if (thumbX2 > getMeasuredWidth() - this.thumbWidth) {
                thumbX2 = getMeasuredWidth() - this.thumbWidth;
            }
            this.progress = thumbX2 / (getMeasuredWidth() - this.thumbWidth);
            invalidate();
            return true;
        }
        return false;
    }

    public void setProgress(float progress) {
        if (progress < 0.0f) {
            progress = 0.0f;
        } else if (progress > 1.0f) {
            progress = 1.0f;
        }
        this.progress = progress;
        invalidate();
    }

    public float getProgress() {
        return this.progress;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int y = (getMeasuredHeight() - this.thumbHeight) / 2;
        int measuredWidth = getMeasuredWidth();
        int i = this.thumbWidth;
        int thumbX = (int) ((measuredWidth - i) * this.progress);
        canvas.drawRect(i / 2, (getMeasuredHeight() / 2) - AndroidUtilities.dp(1.0f), getMeasuredWidth() - (this.thumbWidth / 2), (getMeasuredHeight() / 2) + AndroidUtilities.dp(1.0f), this.paint);
        int i2 = this.thumbWidth;
        canvas.drawCircle((i2 / 2) + thumbX, (this.thumbHeight / 2) + y, i2 / 2, this.paint2);
    }
}
