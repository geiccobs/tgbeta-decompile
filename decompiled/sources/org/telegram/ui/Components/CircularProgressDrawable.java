package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import org.telegram.messenger.AndroidUtilities;
/* loaded from: classes5.dex */
public class CircularProgressDrawable extends Drawable {
    private final RectF bounds;
    private final FastOutSlowInInterpolator interpolator;
    private final Paint paint;
    private float segmentFrom;
    private float segmentTo;
    private float size;
    private long start;
    private float thickness;

    public CircularProgressDrawable() {
        this(-1);
    }

    public CircularProgressDrawable(int color) {
        this.size = AndroidUtilities.dp(18.0f);
        this.thickness = AndroidUtilities.dp(2.25f);
        this.start = -1L;
        this.interpolator = new FastOutSlowInInterpolator();
        Paint paint = new Paint();
        this.paint = paint;
        paint.setStyle(Paint.Style.STROKE);
        this.bounds = new RectF();
        setColor(color);
    }

    public CircularProgressDrawable(float size, float thickness, int color) {
        this.size = AndroidUtilities.dp(18.0f);
        this.thickness = AndroidUtilities.dp(2.25f);
        this.start = -1L;
        this.interpolator = new FastOutSlowInInterpolator();
        Paint paint = new Paint();
        this.paint = paint;
        paint.setStyle(Paint.Style.STROKE);
        this.bounds = new RectF();
        this.size = size;
        this.thickness = thickness;
        setColor(color);
    }

    private void updateSegment() {
        long now = SystemClock.elapsedRealtime();
        long t = (now - this.start) % 5400;
        this.segmentFrom = (((float) (t * 1520)) / 5400.0f) - 20.0f;
        this.segmentTo = ((float) (1520 * t)) / 5400.0f;
        for (int i = 0; i < 4; i++) {
            float fraction = ((float) (t - (i * 1350))) / 667.0f;
            this.segmentTo += this.interpolator.getInterpolation(fraction) * 250.0f;
            float fraction2 = ((float) (t - ((i * 1350) + 667))) / 667.0f;
            float fraction3 = this.segmentFrom;
            this.segmentFrom = fraction3 + (this.interpolator.getInterpolation(fraction2) * 250.0f);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if (this.start < 0) {
            this.start = SystemClock.elapsedRealtime();
        }
        updateSegment();
        RectF rectF = this.bounds;
        float f = this.segmentFrom;
        canvas.drawArc(rectF, f, this.segmentTo - f, false, this.paint);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void setBounds(int left, int top, int right, int bottom) {
        int width = right - left;
        int height = bottom - top;
        RectF rectF = this.bounds;
        float f = this.thickness;
        float f2 = this.size;
        rectF.set(left + (((width - (f / 2.0f)) - f2) / 2.0f), top + (((height - (f / 2.0f)) - f2) / 2.0f), left + (((width + (f / 2.0f)) + f2) / 2.0f), top + (((height + (f / 2.0f)) + f2) / 2.0f));
        super.setBounds(left, top, right, bottom);
        this.paint.setStrokeWidth(this.thickness);
    }

    public void setColor(int color) {
        this.paint.setColor(color);
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        this.paint.setAlpha(alpha);
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -2;
    }
}
