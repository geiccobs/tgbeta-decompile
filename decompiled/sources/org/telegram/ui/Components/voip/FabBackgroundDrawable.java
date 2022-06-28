package org.telegram.ui.Components.voip;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import org.telegram.messenger.AndroidUtilities;
/* loaded from: classes5.dex */
public class FabBackgroundDrawable extends Drawable {
    private Paint bgPaint = new Paint(1);
    private Bitmap shadowBitmap;
    private Paint shadowPaint;

    public FabBackgroundDrawable() {
        Paint paint = new Paint();
        this.shadowPaint = paint;
        paint.setColor(1275068416);
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if (this.shadowBitmap == null) {
            onBoundsChange(getBounds());
        }
        int size = Math.min(getBounds().width(), getBounds().height());
        Bitmap bitmap = this.shadowBitmap;
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, getBounds().centerX() - (this.shadowBitmap.getWidth() / 2), getBounds().centerY() - (this.shadowBitmap.getHeight() / 2), this.shadowPaint);
        }
        canvas.drawCircle(size / 2, size / 2, (size / 2) - AndroidUtilities.dp(4.0f), this.bgPaint);
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -2;
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect bounds) {
        int size = Math.min(bounds.width(), bounds.height());
        if (size <= 0) {
            this.shadowBitmap = null;
            return;
        }
        this.shadowBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ALPHA_8);
        Canvas c = new Canvas(this.shadowBitmap);
        Paint p = new Paint(1);
        p.setShadowLayer(AndroidUtilities.dp(3.33333f), 0.0f, AndroidUtilities.dp(0.666f), -1);
        c.drawCircle(size / 2, size / 2, (size / 2) - AndroidUtilities.dp(4.0f), p);
    }

    public void setColor(int color) {
        this.bgPaint.setColor(color);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public boolean getPadding(Rect padding) {
        int pad = AndroidUtilities.dp(4.0f);
        padding.set(pad, pad, pad, pad);
        return true;
    }
}
