package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class MapPlaceholderDrawable extends Drawable {
    private Paint linePaint;
    private Paint paint = new Paint();

    public MapPlaceholderDrawable() {
        Paint paint = new Paint();
        this.linePaint = paint;
        paint.setStrokeWidth(AndroidUtilities.dp(1.0f));
        if (Theme.getCurrentTheme().isDark()) {
            this.paint.setColor(-14865331);
            this.linePaint.setColor(-15854042);
            return;
        }
        this.paint.setColor(-2172970);
        this.linePaint.setColor(-3752002);
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        canvas.drawRect(getBounds(), this.paint);
        int gap = AndroidUtilities.dp(9.0f);
        int xcount = getBounds().width() / gap;
        int ycount = getBounds().height() / gap;
        int x = getBounds().left;
        int y = getBounds().top;
        for (int a = 0; a < xcount; a++) {
            canvas.drawLine(((a + 1) * gap) + x, y, ((a + 1) * gap) + x, getBounds().height() + y, this.linePaint);
        }
        for (int a2 = 0; a2 < ycount; a2++) {
            canvas.drawLine(x, ((a2 + 1) * gap) + y, getBounds().width() + x, ((a2 + 1) * gap) + y, this.linePaint);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter cf) {
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return 0;
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
