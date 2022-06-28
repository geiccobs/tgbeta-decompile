package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.SvgHelper;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class LoadingStickerDrawable extends Drawable {
    private Bitmap bitmap;
    int currentColor0;
    int currentColor1;
    private float gradientWidth;
    private long lastUpdateTime;
    private View parentView;
    private LinearGradient placeholderGradient;
    private float totalTranslation;
    private Paint placeholderPaint = new Paint(2);
    private Matrix placeholderMatrix = new Matrix();

    public LoadingStickerDrawable(View parent, String svg, int w, int h) {
        this.bitmap = SvgHelper.getBitmapByPathOnly(svg, 512, 512, w, h);
        this.parentView = parent;
    }

    public void setColors(String key1, String key2) {
        int color0 = Theme.getColor(key1);
        int color1 = Theme.getColor(key2);
        if (this.currentColor0 != color0 || this.currentColor1 != color1) {
            this.currentColor0 = color0;
            this.currentColor1 = color1;
            int color02 = AndroidUtilities.getAverageColor(color1, color0);
            this.placeholderPaint.setColor(color1);
            float dp = AndroidUtilities.dp(500.0f);
            this.gradientWidth = dp;
            LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, dp, 0.0f, new int[]{color1, color02, color1}, new float[]{0.0f, 0.18f, 0.36f}, Shader.TileMode.REPEAT);
            this.placeholderGradient = linearGradient;
            linearGradient.setLocalMatrix(this.placeholderMatrix);
            Shader shaderB = new BitmapShader(this.bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            this.placeholderPaint.setShader(new ComposeShader(this.placeholderGradient, shaderB, PorterDuff.Mode.MULTIPLY));
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if (this.bitmap == null) {
            return;
        }
        setColors(Theme.key_dialogBackground, Theme.key_dialogBackgroundGray);
        android.graphics.Rect bounds = getBounds();
        canvas.drawRect(bounds.left, bounds.top, bounds.right, bounds.bottom, this.placeholderPaint);
        long newUpdateTime = SystemClock.elapsedRealtime();
        long dt = Math.abs(this.lastUpdateTime - newUpdateTime);
        if (dt > 17) {
            dt = 16;
        }
        this.lastUpdateTime = newUpdateTime;
        this.totalTranslation += (((float) dt) * this.gradientWidth) / 1800.0f;
        while (true) {
            float f = this.totalTranslation;
            float f2 = this.gradientWidth;
            if (f >= f2 * 2.0f) {
                this.totalTranslation = f - (f2 * 2.0f);
            } else {
                this.placeholderMatrix.setTranslate(f, 0.0f);
                this.placeholderGradient.setLocalMatrix(this.placeholderMatrix);
                this.parentView.invalidate();
                return;
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -2;
    }
}
