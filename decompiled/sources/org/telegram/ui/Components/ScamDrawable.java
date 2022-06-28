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
/* loaded from: classes5.dex */
public class ScamDrawable extends Drawable {
    private int currentType;
    private String text;
    private TextPaint textPaint;
    private int textWidth;
    private RectF rect = new RectF();
    private Paint paint = new Paint(1);
    int colorAlpha = 255;
    int alpha = 255;

    public ScamDrawable(int textSize, int type) {
        TextPaint textPaint = new TextPaint(1);
        this.textPaint = textPaint;
        this.currentType = type;
        textPaint.setTextSize(AndroidUtilities.dp(textSize));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeWidth(AndroidUtilities.dp(1.0f));
        if (type == 0) {
            this.text = LocaleController.getString("ScamMessage", R.string.ScamMessage);
        } else {
            this.text = LocaleController.getString("FakeMessage", R.string.FakeMessage);
        }
        this.textWidth = (int) Math.ceil(this.textPaint.measureText(this.text));
    }

    public void checkText() {
        String newText;
        if (this.currentType == 0) {
            newText = LocaleController.getString("ScamMessage", R.string.ScamMessage);
        } else {
            newText = LocaleController.getString("FakeMessage", R.string.FakeMessage);
        }
        if (!newText.equals(this.text)) {
            this.text = newText;
            this.textWidth = (int) Math.ceil(this.textPaint.measureText(newText));
        }
    }

    public void setColor(int color) {
        this.textPaint.setColor(color);
        this.paint.setColor(color);
        this.colorAlpha = Color.alpha(color);
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        if (this.alpha != alpha) {
            int localAlpha = (int) (this.colorAlpha * (alpha / 255.0f));
            this.paint.setAlpha(localAlpha);
            this.textPaint.setAlpha(localAlpha);
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

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -2;
    }
}
