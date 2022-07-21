package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
/* loaded from: classes3.dex */
public class ScamDrawable extends Drawable {
    private int currentType;
    private String text;
    private TextPaint textPaint;
    private int textWidth;
    private RectF rect = new RectF();
    private Paint paint = new Paint(1);
    int colorAlpha = 255;
    int alpha = 255;

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -2;
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    public ScamDrawable(int i, int i2) {
        TextPaint textPaint = new TextPaint(1);
        this.textPaint = textPaint;
        this.currentType = i2;
        textPaint.setTextSize(AndroidUtilities.dp(i));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeWidth(AndroidUtilities.dp(1.0f));
        if (i2 == 0) {
            this.text = LocaleController.getString("ScamMessage", R.string.ScamMessage);
        } else {
            this.text = LocaleController.getString("FakeMessage", R.string.FakeMessage);
        }
        this.textWidth = (int) Math.ceil(this.textPaint.measureText(this.text));
    }

    public void checkText() {
        String str;
        if (this.currentType == 0) {
            str = LocaleController.getString("ScamMessage", R.string.ScamMessage);
        } else {
            str = LocaleController.getString("FakeMessage", R.string.FakeMessage);
        }
        if (!str.equals(this.text)) {
            this.text = str;
            this.textWidth = (int) Math.ceil(this.textPaint.measureText(str));
        }
    }

    public void setColor(int i) {
        this.textPaint.setColor(i);
        this.paint.setColor(i);
        this.colorAlpha = Color.alpha(i);
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        if (this.alpha != i) {
            int i2 = (int) (this.colorAlpha * (i / 255.0f));
            this.paint.setAlpha(i2);
            this.textPaint.setAlpha(i2);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.textWidth + AndroidUtilities.dp(10.0f);
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(16.0f);
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        this.rect.set(getBounds());
        canvas.drawRoundRect(this.rect, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), this.paint);
        canvas.drawText(this.text, this.rect.left + AndroidUtilities.dp(5.0f), this.rect.top + AndroidUtilities.dp(12.0f), this.textPaint);
    }
}
