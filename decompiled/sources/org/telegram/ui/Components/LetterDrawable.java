package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class LetterDrawable extends Drawable {
    private static TextPaint namePaint;
    public static Paint paint = new Paint();
    private RectF rect;
    private StringBuilder stringBuilder;
    private float textHeight;
    private StaticLayout textLayout;
    private float textLeft;
    private float textWidth;

    public LetterDrawable() {
        this(null);
    }

    public LetterDrawable(Theme.ResourcesProvider resourcesProvider) {
        this.rect = new RectF();
        this.stringBuilder = new StringBuilder(5);
        if (namePaint == null) {
            namePaint = new TextPaint(1);
        }
        namePaint.setTextSize(AndroidUtilities.dp(28.0f));
        paint.setColor(Theme.getColor(Theme.key_sharedMedia_linkPlaceholder, resourcesProvider));
        namePaint.setColor(Theme.getColor(Theme.key_sharedMedia_linkPlaceholderText, resourcesProvider));
    }

    public void setBackgroundColor(int value) {
        paint.setColor(value);
    }

    public void setColor(int value) {
        namePaint.setColor(value);
    }

    public void setTitle(String title) {
        this.stringBuilder.setLength(0);
        if (title != null && title.length() > 0) {
            this.stringBuilder.append(title.substring(0, 1));
        }
        if (this.stringBuilder.length() > 0) {
            String text = this.stringBuilder.toString().toUpperCase();
            try {
                StaticLayout staticLayout = new StaticLayout(text, namePaint, AndroidUtilities.dp(100.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.textLayout = staticLayout;
                if (staticLayout.getLineCount() > 0) {
                    this.textLeft = this.textLayout.getLineLeft(0);
                    this.textWidth = this.textLayout.getLineWidth(0);
                    this.textHeight = this.textLayout.getLineBottom(0);
                    return;
                }
                return;
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        this.textLayout = null;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        android.graphics.Rect bounds = getBounds();
        if (bounds == null) {
            return;
        }
        this.rect.set(bounds.left, bounds.top, bounds.right, bounds.bottom);
        canvas.drawRoundRect(this.rect, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
        canvas.save();
        if (this.textLayout != null) {
            int size = bounds.width();
            canvas.translate((bounds.left + ((size - this.textWidth) / 2.0f)) - this.textLeft, bounds.top + ((size - this.textHeight) / 2.0f));
            this.textLayout.draw(canvas);
        }
        canvas.restore();
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        namePaint.setAlpha(alpha);
        paint.setAlpha(alpha);
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter cf) {
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -2;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return 0;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return 0;
    }
}
