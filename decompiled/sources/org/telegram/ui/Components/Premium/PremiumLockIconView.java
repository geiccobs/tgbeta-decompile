package org.telegram.ui.Components.Premium;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.widget.ImageView;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Premium.StarParticlesView;
import org.telegram.ui.Components.voip.CellFlickerDrawable;
/* loaded from: classes5.dex */
public class PremiumLockIconView extends ImageView {
    public static int TYPE_REACTIONS = 0;
    public static int TYPE_STICKERS_PREMIUM_LOCKED = 1;
    int color1;
    int color2;
    ImageReceiver imageReceiver;
    public boolean isEnter;
    private boolean locked;
    Paint oldShaderPaint;
    StarParticlesView.Drawable starParticles;
    private final int type;
    boolean waitingImage;
    boolean wasDrawn;
    private float[] colorFloat = new float[3];
    int currentColor = -1;
    Shader shader = null;
    Path path = new Path();
    Paint paint = new Paint(1);
    float shaderCrossfadeProgress = 1.0f;
    CellFlickerDrawable cellFlickerDrawable = new CellFlickerDrawable();

    public PremiumLockIconView(Context context, int type) {
        super(context);
        this.type = type;
        setImageResource(type == TYPE_REACTIONS ? R.drawable.msg_premium_lock2 : R.drawable.msg_mini_premiumlock);
        if (type == TYPE_REACTIONS) {
            StarParticlesView.Drawable drawable = new StarParticlesView.Drawable(5);
            this.starParticles = drawable;
            drawable.updateColors();
            this.starParticles.roundEffect = false;
            StarParticlesView.Drawable drawable2 = this.starParticles;
            drawable2.size2 = 4;
            drawable2.size3 = 4;
            this.starParticles.size1 = 2;
            this.starParticles.speedScale = 0.1f;
            this.starParticles.init();
        }
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.type == TYPE_REACTIONS) {
            this.path.rewind();
            AndroidUtilities.rectTmp.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
            this.path.addCircle(AndroidUtilities.rectTmp.width() / 2.0f, AndroidUtilities.rectTmp.centerY(), AndroidUtilities.rectTmp.width() / 2.0f, Path.Direction.CW);
            AndroidUtilities.rectTmp.set((getMeasuredWidth() / 2.0f) + AndroidUtilities.dp(2.5f), (getMeasuredHeight() / 2.0f) + AndroidUtilities.dpf2(5.7f), getMeasuredWidth() - AndroidUtilities.dpf2(0.2f), getMeasuredHeight());
            this.path.addRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), Path.Direction.CW);
            this.path.close();
            this.starParticles.rect.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
            this.starParticles.rect.inset(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f));
            return;
        }
        updateGradient();
    }

    public void setColor(int color) {
        if (this.currentColor != color) {
            this.currentColor = color;
            if (this.type == TYPE_REACTIONS) {
                this.paint.setColor(color);
            } else {
                updateGradient();
            }
            invalidate();
        }
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.waitingImage) {
            if (this.imageReceiver.getBitmap() != null) {
                this.waitingImage = false;
                setColor(getDominantColor(this.imageReceiver.getBitmap()));
            } else {
                invalidate();
            }
        }
        if (this.type == TYPE_REACTIONS) {
            if (this.currentColor != 0) {
                canvas.drawPath(this.path, this.paint);
            } else {
                PremiumGradient.getInstance().updateMainGradientMatrix(0, 0, getMeasuredWidth(), getMeasuredHeight(), -AndroidUtilities.dp(24.0f), 0.0f);
                canvas.drawPath(this.path, PremiumGradient.getInstance().getMainGradientPaint());
            }
            this.cellFlickerDrawable.setParentWidth(getMeasuredWidth() / 2);
            this.cellFlickerDrawable.drawFrame = false;
            this.cellFlickerDrawable.draw(canvas, this.path, this);
            canvas.save();
            canvas.clipPath(this.path);
            this.starParticles.onDraw(canvas);
            canvas.restore();
            invalidate();
        } else {
            float cx = getMeasuredWidth() / 2.0f;
            float cy = getMeasuredHeight() / 2.0f;
            if (this.oldShaderPaint == null) {
                this.shaderCrossfadeProgress = 1.0f;
            }
            float f = this.shaderCrossfadeProgress;
            if (f != 1.0f) {
                this.paint.setAlpha((int) (f * 255.0f));
                canvas.drawCircle(cx, cy, cx, this.oldShaderPaint);
                canvas.drawCircle(cx, cy, cx, this.paint);
                float f2 = this.shaderCrossfadeProgress + 0.10666667f;
                this.shaderCrossfadeProgress = f2;
                if (f2 > 1.0f) {
                    this.shaderCrossfadeProgress = 1.0f;
                    this.oldShaderPaint = null;
                }
                invalidate();
                this.paint.setAlpha(255);
            } else {
                canvas.drawCircle(cx, cy, cx, this.paint);
            }
        }
        super.onDraw(canvas);
        this.wasDrawn = true;
    }

    public void setImageReceiver(ImageReceiver imageReceiver) {
        this.imageReceiver = imageReceiver;
        this.waitingImage = true;
    }

    public static int getDominantColor(Bitmap bitmap) {
        if (bitmap == null) {
            return -1;
        }
        float stepH = (bitmap.getHeight() - 1) / 10.0f;
        float stepW = (bitmap.getWidth() - 1) / 10.0f;
        int r = 0;
        int g = 0;
        int b = 0;
        int amount = 0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                int x = (int) (i * stepW);
                int y = (int) (j * stepH);
                int pixel = bitmap.getPixel(x, y);
                if (pixel != 0) {
                    r += Color.red(pixel);
                    g += Color.green(pixel);
                    b += Color.blue(pixel);
                    amount++;
                }
            }
        }
        if (amount == 0) {
            return 0;
        }
        return Color.argb(255, r / amount, g / amount, b / amount);
    }

    private void updateGradient() {
        if (getMeasuredHeight() != 0 && getMeasuredWidth() != 0) {
            Color.colorToHSV(this.currentColor, this.colorFloat);
            float[] fArr = this.colorFloat;
            fArr[1] = fArr[1] * (this.locked ? 2.0f : 1.0f);
            if (fArr[2] > 0.7f) {
                fArr[2] = 0.7f;
            }
            int baseColor = Color.HSVToColor(fArr);
            int c2 = ColorUtils.blendARGB(baseColor, Theme.getColor(Theme.key_windowBackgroundWhite), 0.5f);
            int c1 = ColorUtils.blendARGB(baseColor, Theme.getColor(Theme.key_windowBackgroundWhite), 0.4f);
            if (this.shader == null || this.color1 != c1 || this.color2 != c2) {
                if (this.wasDrawn) {
                    Paint paint = this.paint;
                    this.oldShaderPaint = paint;
                    paint.setAlpha(255);
                    this.shaderCrossfadeProgress = 0.0f;
                }
                this.paint = new Paint(1);
                this.color1 = c1;
                this.color2 = c2;
                LinearGradient linearGradient = new LinearGradient(0.0f, getMeasuredHeight(), 0.0f, 0.0f, new int[]{c1, c2}, (float[]) null, Shader.TileMode.CLAMP);
                this.shader = linearGradient;
                this.paint.setShader(linearGradient);
                invalidate();
            }
        }
    }

    public void setWaitingImage() {
        this.waitingImage = true;
        this.wasDrawn = false;
        invalidate();
    }

    public void play(int delay) {
        this.isEnter = true;
        this.cellFlickerDrawable.progress = 0.0f;
        this.cellFlickerDrawable.repeatEnabled = false;
        invalidate();
        animate().scaleX(1.1f).scaleY(1.1f).setStartDelay(delay).setInterpolator(AndroidUtilities.overshootInterpolator).setDuration(300L);
    }

    public void resetAnimation() {
        this.isEnter = false;
        setScaleX(0.0f);
        setScaleY(0.0f);
    }

    public void setLocked(boolean locked) {
        if (this.type != TYPE_REACTIONS) {
            setImageResource(locked ? R.drawable.msg_mini_premiumlock : R.drawable.msg_mini_stickerstar);
        }
    }
}
