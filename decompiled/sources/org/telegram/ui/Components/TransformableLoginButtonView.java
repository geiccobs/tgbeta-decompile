package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;
import android.view.View;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class TransformableLoginButtonView extends View {
    private static final float ARROW_BACK_SIZE = 9.0f;
    private static final float ARROW_IN = 0.4f;
    private static final float ARROW_PADDING = 21.0f;
    private static final float BUTTON_RADIUS_DP = 6.0f;
    private static final float BUTTON_TEXT_IN = 0.6f;
    private static final float CIRCLE_RADIUS_DP = 32.0f;
    private static final float LEFT_CHECK_LINE = 8.0f;
    private static final float RIGHT_CHECK_LINE = 16.0f;
    public static final int TRANSFORM_ARROW_CHECK = 1;
    public static final int TRANSFORM_OPEN_ARROW = 0;
    private String buttonText;
    private float buttonWidth;
    private float progress;
    private Drawable rippleDrawable;
    private TextPaint textPaint;
    private Paint backgroundPaint = new Paint(1);
    private Paint outlinePaint = new Paint(1);
    private boolean drawBackground = true;
    private int transformType = 0;
    private RectF rect = new RectF();

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    private @interface TransformType {
    }

    public TransformableLoginButtonView(Context context) {
        super(context);
        this.backgroundPaint.setColor(Theme.getColor(Theme.key_chats_actionBackground));
        this.outlinePaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        this.outlinePaint.setStyle(Paint.Style.STROKE);
        this.outlinePaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setDrawBackground(boolean drawBackground) {
        this.drawBackground = drawBackground;
    }

    public void setRippleDrawable(Drawable d) {
        this.rippleDrawable = d;
        invalidate();
    }

    public void setTransformType(int transformType) {
        this.transformType = transformType;
        invalidate();
    }

    @Override // android.view.View
    public void setBackgroundColor(int color) {
        this.backgroundPaint.setColor(color);
        invalidate();
    }

    public void setColor(int color) {
        this.outlinePaint.setColor(color);
        invalidate();
    }

    public void setButtonText(TextPaint textPaint, String buttonText) {
        this.textPaint = textPaint;
        this.buttonText = buttonText;
        this.outlinePaint.setColor(textPaint.getColor());
        this.buttonWidth = textPaint.measureText(buttonText);
    }

    public void setProgress(float p) {
        this.progress = p;
        invalidate();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.drawBackground) {
            boolean animateCornerRadius = this.transformType == 0;
            float rad = AndroidUtilities.dp(((animateCornerRadius ? this.progress : 1.0f) * 26.0f) + BUTTON_RADIUS_DP);
            this.rect.set(0.0f, 0.0f, getWidth(), getHeight());
            canvas.drawRoundRect(this.rect, rad, rad, this.backgroundPaint);
        }
        switch (this.transformType) {
            case 0:
                TextPaint textPaint = this.textPaint;
                if (textPaint != null && this.buttonText != null) {
                    int alpha = textPaint.getAlpha();
                    this.textPaint.setAlpha((int) (alpha * (1.0f - (Math.min(0.6f, this.progress) / 0.6f))));
                    canvas.drawText(this.buttonText, (getWidth() - this.buttonWidth) / 2.0f, ((getHeight() / 2.0f) + (this.textPaint.getTextSize() / 2.0f)) - AndroidUtilities.dp(1.75f), this.textPaint);
                    this.textPaint.setAlpha(alpha);
                }
                float arrowProgress = (Math.max(0.4f, this.progress) - 0.4f) / 0.6f;
                if (arrowProgress != 0.0f) {
                    float endX = AndroidUtilities.dp(ARROW_PADDING) + ((getWidth() - (AndroidUtilities.dp(ARROW_PADDING) * 2)) * arrowProgress);
                    float centerY = getHeight() / 2.0f;
                    canvas.drawLine(AndroidUtilities.dp(ARROW_PADDING), centerY, endX, centerY, this.outlinePaint);
                    float backSize = AndroidUtilities.dp(ARROW_BACK_SIZE) * arrowProgress;
                    double d = endX;
                    double cos = Math.cos(0.7853981633974483d);
                    double d2 = backSize;
                    Double.isNaN(d2);
                    Double.isNaN(d);
                    float backX = (float) (d - (cos * d2));
                    double sin = Math.sin(0.7853981633974483d);
                    double d3 = backSize;
                    Double.isNaN(d3);
                    float backY = (float) (sin * d3);
                    canvas.drawLine(endX, centerY, backX, centerY - backY, this.outlinePaint);
                    canvas.drawLine(endX, centerY, backX, centerY + backY, this.outlinePaint);
                    break;
                }
                break;
            case 1:
                float startX = AndroidUtilities.dp(ARROW_PADDING);
                float endX2 = getWidth() - AndroidUtilities.dp(ARROW_PADDING);
                float centerY2 = getHeight() / 2.0f;
                canvas.save();
                canvas.translate((-AndroidUtilities.dp(2.0f)) * this.progress, 0.0f);
                canvas.rotate(this.progress * 90.0f, getWidth() / 2.0f, getHeight() / 2.0f);
                canvas.drawLine(startX + ((endX2 - startX) * this.progress), centerY2, endX2, centerY2, this.outlinePaint);
                int leftSize = AndroidUtilities.dp((this.progress * (-1.0f)) + ARROW_BACK_SIZE);
                int rightSize = AndroidUtilities.dp((this.progress * 7.0f) + ARROW_BACK_SIZE);
                double d4 = endX2;
                double d5 = leftSize;
                double cos2 = Math.cos(0.7853981633974483d);
                Double.isNaN(d5);
                Double.isNaN(d4);
                float f = (float) (d4 - (d5 * cos2));
                double d6 = centerY2;
                double d7 = leftSize;
                double sin2 = Math.sin(0.7853981633974483d);
                Double.isNaN(d7);
                Double.isNaN(d6);
                canvas.drawLine(endX2, centerY2, f, (float) (d6 + (d7 * sin2)), this.outlinePaint);
                double d8 = endX2;
                double d9 = rightSize;
                double cos3 = Math.cos(0.7853981633974483d);
                Double.isNaN(d9);
                Double.isNaN(d8);
                float f2 = (float) (d8 - (d9 * cos3));
                double d10 = centerY2;
                double d11 = rightSize;
                double sin3 = Math.sin(0.7853981633974483d);
                Double.isNaN(d11);
                Double.isNaN(d10);
                canvas.drawLine(endX2, centerY2, f2, (float) (d10 - (d11 * sin3)), this.outlinePaint);
                canvas.restore();
                break;
        }
        Drawable drawable = this.rippleDrawable;
        if (drawable != null) {
            drawable.setBounds(0, 0, getWidth(), getHeight());
            if (Build.VERSION.SDK_INT >= 21) {
                this.rippleDrawable.setHotspotBounds(0, 0, getWidth(), getHeight());
            }
            this.rippleDrawable.draw(canvas);
        }
    }

    @Override // android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable drawable = this.rippleDrawable;
        if (drawable != null) {
            drawable.setState(getDrawableState());
            invalidate();
        }
    }

    @Override // android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        Drawable drawable = this.rippleDrawable;
        if (drawable != null) {
            drawable.jumpToCurrentState();
        }
    }

    @Override // android.view.View
    public void drawableHotspotChanged(float x, float y) {
        super.drawableHotspotChanged(x, y);
        if (this.rippleDrawable != null && Build.VERSION.SDK_INT >= 21) {
            this.rippleDrawable.setHotspot(x, y);
        }
    }

    @Override // android.view.View
    protected boolean verifyDrawable(Drawable who) {
        Drawable drawable;
        return super.verifyDrawable(who) || ((drawable = this.rippleDrawable) != null && who == drawable);
    }
}
