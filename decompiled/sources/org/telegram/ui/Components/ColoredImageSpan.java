package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.style.ReplacementSpan;
import androidx.core.content.ContextCompat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class ColoredImageSpan extends ReplacementSpan {
    String colorKey;
    Drawable drawable;
    int drawableColor;
    private int size;
    private int topOffset;
    boolean usePaintColor;

    public ColoredImageSpan(int imageRes) {
        this(ContextCompat.getDrawable(ApplicationLoader.applicationContext, imageRes));
    }

    public ColoredImageSpan(Drawable drawable) {
        this.usePaintColor = true;
        this.topOffset = 0;
        this.drawable = drawable;
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }

    public void setSize(int size) {
        this.size = size;
        this.drawable.setBounds(0, 0, size, size);
    }

    @Override // android.text.style.ReplacementSpan
    public int getSize(Paint paint, CharSequence charSequence, int i, int i1, Paint.FontMetricsInt fontMetricsInt) {
        int i2 = this.size;
        return i2 != 0 ? i2 : this.drawable.getIntrinsicWidth();
    }

    @Override // android.text.style.ReplacementSpan
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        int color;
        if (this.usePaintColor) {
            color = paint.getColor();
        } else {
            color = Theme.getColor(this.colorKey);
        }
        if (this.drawableColor != color) {
            this.drawableColor = color;
            this.drawable.setColorFilter(new PorterDuffColorFilter(this.drawableColor, PorterDuff.Mode.MULTIPLY));
        }
        int lineHeight = bottom - top;
        int drawableHeight = this.size;
        if (drawableHeight == 0) {
            drawableHeight = this.drawable.getIntrinsicHeight();
        }
        int padding = (lineHeight - drawableHeight) / 2;
        canvas.save();
        canvas.translate(x, top + padding + AndroidUtilities.dp(this.topOffset));
        this.drawable.draw(canvas);
        canvas.restore();
    }

    public void setColorKey(String colorKey) {
        this.colorKey = colorKey;
        this.usePaintColor = false;
    }

    public void setTopOffset(int topOffset) {
        this.topOffset = topOffset;
    }
}
