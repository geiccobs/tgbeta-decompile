package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.SystemClock;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
/* loaded from: classes5.dex */
public class SeekBar {
    private static Paint paint;
    private static int thumbWidth;
    private int backgroundColor;
    private int backgroundSelectedColor;
    private float bufferedProgress;
    private int cacheColor;
    private int circleColor;
    private float currentRadius;
    private SeekBarDelegate delegate;
    private int height;
    private long lastUpdateTime;
    private View parentView;
    private int progressColor;
    private boolean selected;
    private int width;
    private int thumbX = 0;
    private int draggingThumbX = 0;
    private int thumbDX = 0;
    private boolean pressed = false;
    private RectF rect = new RectF();
    private int lineHeight = AndroidUtilities.dp(2.0f);

    /* loaded from: classes5.dex */
    public interface SeekBarDelegate {
        void onSeekBarContinuousDrag(float f);

        void onSeekBarDrag(float f);

        /* renamed from: org.telegram.ui.Components.SeekBar$SeekBarDelegate$-CC */
        /* loaded from: classes5.dex */
        public final /* synthetic */ class CC {
            public static void $default$onSeekBarContinuousDrag(SeekBarDelegate _this, float progress) {
            }
        }
    }

    public SeekBar(View parent) {
        if (paint == null) {
            paint = new Paint(1);
        }
        this.parentView = parent;
        thumbWidth = AndroidUtilities.dp(24.0f);
        this.currentRadius = AndroidUtilities.dp(6.0f);
    }

    public void setDelegate(SeekBarDelegate seekBarDelegate) {
        this.delegate = seekBarDelegate;
    }

    public boolean onTouch(int action, float x, float y) {
        SeekBarDelegate seekBarDelegate;
        if (action == 0) {
            int i = this.height;
            int i2 = thumbWidth;
            int additionWidth = (i - i2) / 2;
            if (x >= (-additionWidth)) {
                int i3 = this.width;
                if (x <= i3 + additionWidth && y >= 0.0f && y <= i) {
                    int i4 = this.thumbX;
                    if (i4 - additionWidth > x || x > i4 + i2 + additionWidth) {
                        int i5 = ((int) x) - (i2 / 2);
                        this.thumbX = i5;
                        if (i5 < 0) {
                            this.thumbX = 0;
                        } else if (i5 > i3 - i2) {
                            this.thumbX = i3 - i2;
                        }
                    }
                    this.pressed = true;
                    int i6 = this.thumbX;
                    this.draggingThumbX = i6;
                    this.thumbDX = (int) (x - i6);
                    return true;
                }
            }
        } else if (action == 1 || action == 3) {
            if (this.pressed) {
                int i7 = this.draggingThumbX;
                this.thumbX = i7;
                if (action == 1 && (seekBarDelegate = this.delegate) != null) {
                    seekBarDelegate.onSeekBarDrag(i7 / (this.width - thumbWidth));
                }
                this.pressed = false;
                return true;
            }
        } else if (action == 2 && this.pressed) {
            int i8 = (int) (x - this.thumbDX);
            this.draggingThumbX = i8;
            if (i8 < 0) {
                this.draggingThumbX = 0;
            } else {
                int i9 = this.width;
                int i10 = thumbWidth;
                if (i8 > i9 - i10) {
                    this.draggingThumbX = i9 - i10;
                }
            }
            SeekBarDelegate seekBarDelegate2 = this.delegate;
            if (seekBarDelegate2 != null) {
                seekBarDelegate2.onSeekBarContinuousDrag(this.draggingThumbX / (this.width - thumbWidth));
            }
            return true;
        }
        return false;
    }

    public void setColors(int background, int cache, int progress, int circle, int selected) {
        this.backgroundColor = background;
        this.cacheColor = cache;
        this.circleColor = circle;
        this.progressColor = progress;
        this.backgroundSelectedColor = selected;
    }

    public void setProgress(float progress) {
        int ceil = (int) Math.ceil((this.width - thumbWidth) * progress);
        this.thumbX = ceil;
        if (ceil < 0) {
            this.thumbX = 0;
            return;
        }
        int i = this.width;
        int i2 = thumbWidth;
        if (ceil > i - i2) {
            this.thumbX = i - i2;
        }
    }

    public void setBufferedProgress(float value) {
        this.bufferedProgress = value;
    }

    public float getProgress() {
        return this.thumbX / (this.width - thumbWidth);
    }

    public int getThumbX() {
        return (this.pressed ? this.draggingThumbX : this.thumbX) + (thumbWidth / 2);
    }

    public boolean isDragging() {
        return this.pressed;
    }

    public void setSelected(boolean value) {
        this.selected = value;
    }

    public void setSize(int w, int h) {
        this.width = w;
        this.height = h;
    }

    public int getWidth() {
        return this.width - thumbWidth;
    }

    public void setLineHeight(int value) {
        this.lineHeight = value;
    }

    public void draw(Canvas canvas) {
        int i;
        RectF rectF = this.rect;
        int i2 = thumbWidth;
        int i3 = this.height;
        int i4 = this.lineHeight;
        rectF.set(i2 / 2, (i3 / 2) - (i4 / 2), this.width - (i2 / 2), (i3 / 2) + (i4 / 2));
        paint.setColor(this.selected ? this.backgroundSelectedColor : this.backgroundColor);
        RectF rectF2 = this.rect;
        int i5 = thumbWidth;
        canvas.drawRoundRect(rectF2, i5 / 2, i5 / 2, paint);
        if (this.bufferedProgress > 0.0f) {
            paint.setColor(this.selected ? this.backgroundSelectedColor : this.cacheColor);
            RectF rectF3 = this.rect;
            int i6 = thumbWidth;
            int i7 = this.height;
            int i8 = this.lineHeight;
            rectF3.set(i6 / 2, (i7 / 2) - (i8 / 2), (i6 / 2) + (this.bufferedProgress * (this.width - i6)), (i7 / 2) + (i8 / 2));
            RectF rectF4 = this.rect;
            int i9 = thumbWidth;
            canvas.drawRoundRect(rectF4, i9 / 2, i9 / 2, paint);
        }
        RectF rectF5 = this.rect;
        float f = thumbWidth / 2;
        int i10 = this.height;
        int i11 = this.lineHeight;
        rectF5.set(f, (i10 / 2) - (i11 / 2), (i / 2) + (this.pressed ? this.draggingThumbX : this.thumbX), (i10 / 2) + (i11 / 2));
        paint.setColor(this.progressColor);
        RectF rectF6 = this.rect;
        int i12 = thumbWidth;
        canvas.drawRoundRect(rectF6, i12 / 2, i12 / 2, paint);
        paint.setColor(this.circleColor);
        int newRad = AndroidUtilities.dp(this.pressed ? 8.0f : 6.0f);
        if (this.currentRadius != newRad) {
            long newUpdateTime = SystemClock.elapsedRealtime();
            long dt = newUpdateTime - this.lastUpdateTime;
            if (dt > 18) {
                dt = 16;
            }
            float f2 = this.currentRadius;
            if (f2 < newRad) {
                float dp = f2 + (AndroidUtilities.dp(1.0f) * (((float) dt) / 60.0f));
                this.currentRadius = dp;
                if (dp > newRad) {
                    this.currentRadius = newRad;
                }
            } else {
                float dp2 = f2 - (AndroidUtilities.dp(1.0f) * (((float) dt) / 60.0f));
                this.currentRadius = dp2;
                if (dp2 < newRad) {
                    this.currentRadius = newRad;
                }
            }
            View view = this.parentView;
            if (view != null) {
                view.invalidate();
            }
        }
        canvas.drawCircle((this.pressed ? this.draggingThumbX : this.thumbX) + (thumbWidth / 2), this.height / 2, this.currentRadius, paint);
    }
}
