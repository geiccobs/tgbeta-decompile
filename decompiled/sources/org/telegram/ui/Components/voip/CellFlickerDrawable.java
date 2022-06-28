package org.telegram.ui.Components.voip;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.SvgHelper;
/* loaded from: classes5.dex */
public class CellFlickerDrawable {
    public float animationSpeedScale;
    public boolean drawFrame;
    public boolean frameInside;
    private Shader gradientShader;
    private Shader gradientShader2;
    long lastUpdateTime;
    Matrix matrix;
    Runnable onRestartCallback;
    private Paint paint;
    private Paint paintOutline;
    View parentView;
    int parentWidth;
    public float progress;
    public boolean repeatEnabled;
    public float repeatProgress;
    int size;

    public CellFlickerDrawable() {
        this(64, 204);
    }

    public CellFlickerDrawable(int a1, int a2) {
        this.paint = new Paint(1);
        this.paintOutline = new Paint(1);
        this.matrix = new Matrix();
        this.repeatEnabled = true;
        this.drawFrame = true;
        this.frameInside = false;
        this.repeatProgress = 1.2f;
        this.animationSpeedScale = 1.0f;
        this.size = AndroidUtilities.dp(160.0f);
        this.gradientShader = new LinearGradient(0.0f, 0.0f, this.size, 0.0f, new int[]{0, ColorUtils.setAlphaComponent(-1, a1), 0}, (float[]) null, Shader.TileMode.CLAMP);
        this.gradientShader2 = new LinearGradient(0.0f, 0.0f, this.size, 0.0f, new int[]{0, ColorUtils.setAlphaComponent(-1, a2), 0}, (float[]) null, Shader.TileMode.CLAMP);
        this.paint.setShader(this.gradientShader);
        this.paintOutline.setShader(this.gradientShader2);
        this.paintOutline.setStyle(Paint.Style.STROKE);
        this.paintOutline.setStrokeWidth(AndroidUtilities.dp(2.0f));
    }

    public void setColors(int color) {
        setColors(color, 64, 204);
    }

    public void setColors(int color, int alpha1, int alpha2) {
        this.gradientShader = new LinearGradient(0.0f, 0.0f, this.size, 0.0f, new int[]{0, ColorUtils.setAlphaComponent(color, alpha1), 0}, (float[]) null, Shader.TileMode.CLAMP);
        this.gradientShader2 = new LinearGradient(0.0f, 0.0f, this.size, 0.0f, new int[]{0, ColorUtils.setAlphaComponent(color, alpha2), 0}, (float[]) null, Shader.TileMode.CLAMP);
        this.paint.setShader(this.gradientShader);
        this.paintOutline.setShader(this.gradientShader2);
    }

    public float getProgress() {
        return this.progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public void draw(Canvas canvas, RectF rectF, float rad, View view) {
        update(view);
        canvas.drawRoundRect(rectF, rad, rad, this.paint);
        if (this.drawFrame) {
            if (this.frameInside) {
                rectF.inset(this.paintOutline.getStrokeWidth() / 2.0f, this.paintOutline.getStrokeWidth() / 2.0f);
            }
            canvas.drawRoundRect(rectF, rad, rad, this.paintOutline);
        }
    }

    public void draw(Canvas canvas, Path path, View view) {
        update(view);
        canvas.drawPath(path, this.paint);
        if (this.drawFrame) {
            canvas.drawPath(path, this.paintOutline);
        }
    }

    private void update(View view) {
        if (this.progress > 1.0f && !this.repeatEnabled) {
            return;
        }
        if (view != null) {
            view.invalidate();
        }
        long currentTime = System.currentTimeMillis();
        long j = this.lastUpdateTime;
        if (j != 0) {
            long dt = currentTime - j;
            if (dt > 10) {
                float f = this.progress + ((((float) dt) / 1200.0f) * this.animationSpeedScale);
                this.progress = f;
                if (f > this.repeatProgress) {
                    this.progress = 0.0f;
                    Runnable runnable = this.onRestartCallback;
                    if (runnable != null) {
                        runnable.run();
                    }
                }
                this.lastUpdateTime = currentTime;
            }
        } else {
            this.lastUpdateTime = currentTime;
        }
        int i = this.parentWidth;
        int i2 = this.size;
        float x = ((i + (i2 * 2)) * this.progress) - i2;
        this.matrix.reset();
        this.matrix.setTranslate(x, 0.0f);
        this.gradientShader.setLocalMatrix(this.matrix);
        this.gradientShader2.setLocalMatrix(this.matrix);
    }

    public void draw(Canvas canvas, GroupCallMiniTextureView view) {
        long currentTime = System.currentTimeMillis();
        long j = this.lastUpdateTime;
        if (j != 0) {
            long dt = currentTime - j;
            if (dt > 10) {
                float f = this.progress + (((float) dt) / 500.0f);
                this.progress = f;
                if (f > 4.0f) {
                    this.progress = 0.0f;
                    Runnable runnable = this.onRestartCallback;
                    if (runnable != null) {
                        runnable.run();
                    }
                }
                this.lastUpdateTime = currentTime;
            }
        } else {
            this.lastUpdateTime = currentTime;
        }
        float f2 = this.progress;
        if (f2 > 1.0f) {
            return;
        }
        int i = this.parentWidth;
        int i2 = this.size;
        float x = (((i + (i2 * 2)) * f2) - i2) - view.getX();
        this.matrix.setTranslate(x, 0.0f);
        this.gradientShader.setLocalMatrix(this.matrix);
        this.gradientShader2.setLocalMatrix(this.matrix);
        AndroidUtilities.rectTmp.set(view.textureView.currentClipHorizontal, view.textureView.currentClipVertical, view.textureView.getMeasuredWidth() - view.textureView.currentClipHorizontal, view.textureView.getMeasuredHeight() - view.textureView.currentClipVertical);
        canvas.drawRect(AndroidUtilities.rectTmp, this.paint);
        if (this.drawFrame) {
            if (this.frameInside) {
                AndroidUtilities.rectTmp.inset(this.paintOutline.getStrokeWidth() / 2.0f, this.paintOutline.getStrokeWidth() / 2.0f);
            }
            canvas.drawRoundRect(AndroidUtilities.rectTmp, view.textureView.roundRadius, view.textureView.roundRadius, this.paintOutline);
        }
    }

    public void setParentWidth(int parentWidth) {
        this.parentWidth = parentWidth;
    }

    public DrawableInterface getDrawableInterface(View parentView, SvgHelper.SvgDrawable drawable) {
        this.parentView = parentView;
        return new DrawableInterface(drawable);
    }

    public void setOnRestartCallback(Runnable runnable) {
        this.onRestartCallback = runnable;
    }

    /* loaded from: classes5.dex */
    public class DrawableInterface extends Drawable {
        public float radius;
        SvgHelper.SvgDrawable svgDrawable;

        public DrawableInterface(SvgHelper.SvgDrawable drawable) {
            CellFlickerDrawable.this = this$0;
            this.svgDrawable = drawable;
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            CellFlickerDrawable.this.setParentWidth(getBounds().width());
            AndroidUtilities.rectTmp.set(getBounds());
            CellFlickerDrawable.this.draw(canvas, AndroidUtilities.rectTmp, this.radius, null);
            SvgHelper.SvgDrawable svgDrawable = this.svgDrawable;
            if (svgDrawable != null) {
                svgDrawable.setPaint(CellFlickerDrawable.this.paint);
                float x = ((CellFlickerDrawable.this.parentWidth + (CellFlickerDrawable.this.size * 2)) * CellFlickerDrawable.this.progress) - CellFlickerDrawable.this.size;
                int drawableSize = (int) (CellFlickerDrawable.this.parentWidth * 0.5f);
                float s = this.svgDrawable.getScale();
                CellFlickerDrawable.this.matrix.reset();
                CellFlickerDrawable.this.matrix.setScale(1.0f / s, 0.0f, CellFlickerDrawable.this.size / 2.0f, 0.0f);
                CellFlickerDrawable.this.matrix.setTranslate((x - this.svgDrawable.getBounds().left) - (CellFlickerDrawable.this.size / s), 0.0f);
                CellFlickerDrawable.this.gradientShader.setLocalMatrix(CellFlickerDrawable.this.matrix);
                this.svgDrawable.setBounds(getBounds().centerX() - (drawableSize / 2), getBounds().centerY() - (drawableSize / 2), getBounds().centerX() + (drawableSize / 2), getBounds().centerY() + (drawableSize / 2));
                this.svgDrawable.draw(canvas);
            }
            CellFlickerDrawable.this.parentView.invalidate();
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int alpha) {
            CellFlickerDrawable.this.paint.setAlpha(alpha);
            CellFlickerDrawable.this.paintOutline.setAlpha(alpha);
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
        }

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return -3;
        }
    }
}
