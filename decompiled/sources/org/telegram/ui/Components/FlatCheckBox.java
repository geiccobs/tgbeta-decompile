package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class FlatCheckBox extends View {
    boolean attached;
    ValueAnimator checkAnimator;
    public boolean checked;
    int colorActive;
    int colorInactive;
    int colorTextActive;
    String text;
    public boolean enabled = true;
    TextPaint textPaint = new TextPaint(1);
    Paint fillPaint = new Paint(1);
    Paint outLinePaint = new Paint(1);
    Paint checkPaint = new Paint(1);
    int HEIGHT = AndroidUtilities.dp(36.0f);
    int INNER_PADDING = AndroidUtilities.dp(22.0f);
    int TRANSLETE_TEXT = AndroidUtilities.dp(8.0f);
    int P = AndroidUtilities.dp(2.0f);
    RectF rectF = new RectF();
    float progress = 0.0f;
    int lastW = 0;

    public FlatCheckBox(Context context) {
        super(context);
        this.textPaint.setTextSize(AndroidUtilities.dp(14.0f));
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        this.textPaint.setTypeface(Typeface.create("sans-serif-medium", 0));
        this.outLinePaint.setStrokeWidth(AndroidUtilities.dpf2(1.5f));
        this.outLinePaint.setStyle(Paint.Style.STROKE);
        this.checkPaint.setStyle(Paint.Style.STROKE);
        this.checkPaint.setStrokeCap(Paint.Cap.ROUND);
        this.checkPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
    }

    public void recolor(int c) {
        this.colorActive = Theme.getColor(Theme.key_windowBackgroundWhite);
        this.colorTextActive = -1;
        this.colorInactive = c;
        invalidate();
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attached = true;
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attached = false;
    }

    public void setChecked(boolean enabled) {
        setChecked(enabled, true);
    }

    public void setChecked(boolean enabled, boolean animate) {
        this.checked = enabled;
        float f = 1.0f;
        if (!this.attached || !animate) {
            if (!enabled) {
                f = 0.0f;
            }
            this.progress = f;
            return;
        }
        ValueAnimator valueAnimator = this.checkAnimator;
        if (valueAnimator != null) {
            valueAnimator.removeAllListeners();
            this.checkAnimator.cancel();
        }
        float[] fArr = new float[2];
        fArr[0] = this.progress;
        if (!enabled) {
            f = 0.0f;
        }
        fArr[1] = f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
        this.checkAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.FlatCheckBox$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                FlatCheckBox.this.m2623lambda$setChecked$0$orgtelegramuiComponentsFlatCheckBox(valueAnimator2);
            }
        });
        this.checkAnimator.setDuration(300L);
        this.checkAnimator.start();
    }

    /* renamed from: lambda$setChecked$0$org-telegram-ui-Components-FlatCheckBox */
    public /* synthetic */ void m2623lambda$setChecked$0$orgtelegramuiComponentsFlatCheckBox(ValueAnimator animation) {
        this.progress = ((Float) animation.getAnimatedValue()).floatValue();
        invalidate();
    }

    public void setText(String text) {
        this.text = text;
        requestLayout();
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        String str = this.text;
        int textW = str == null ? 0 : (int) this.textPaint.measureText(str);
        setMeasuredDimension((this.P * 2) + textW + (this.INNER_PADDING << 1), this.HEIGHT + AndroidUtilities.dp(4.0f));
        if (getMeasuredWidth() != this.lastW) {
            this.rectF.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
            this.rectF.inset(this.P + (this.outLinePaint.getStrokeWidth() / 2.0f), this.P + (this.outLinePaint.getStrokeWidth() / 2.0f) + AndroidUtilities.dp(2.0f));
        }
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        float textTranslation;
        super.draw(canvas);
        float f = this.progress;
        if (f <= 0.5f) {
            float checkProgress = f / 0.5f;
            textTranslation = checkProgress;
            int rD = (int) ((Color.red(this.colorInactive) - Color.red(this.colorActive)) * checkProgress);
            int gD = (int) ((Color.green(this.colorInactive) - Color.green(this.colorActive)) * checkProgress);
            int bD = (int) ((Color.blue(this.colorInactive) - Color.blue(this.colorActive)) * checkProgress);
            int c = Color.rgb(Color.red(this.colorActive) + rD, Color.green(this.colorActive) + gD, Color.blue(this.colorActive) + bD);
            this.fillPaint.setColor(c);
            int rD2 = (int) ((Color.red(this.colorTextActive) - Color.red(this.colorInactive)) * checkProgress);
            int gD2 = (int) ((Color.green(this.colorTextActive) - Color.green(this.colorInactive)) * checkProgress);
            int bD2 = (int) ((Color.blue(this.colorTextActive) - Color.blue(this.colorInactive)) * checkProgress);
            int c2 = Color.rgb(Color.red(this.colorInactive) + rD2, Color.green(this.colorInactive) + gD2, Color.blue(this.colorInactive) + bD2);
            this.textPaint.setColor(c2);
        } else {
            textTranslation = 1.0f;
            this.textPaint.setColor(this.colorTextActive);
            this.fillPaint.setColor(this.colorInactive);
        }
        int heightHalf = getMeasuredHeight() >> 1;
        this.outLinePaint.setColor(this.colorInactive);
        RectF rectF = this.rectF;
        int i = this.HEIGHT;
        canvas.drawRoundRect(rectF, i / 2.0f, i / 2.0f, this.fillPaint);
        RectF rectF2 = this.rectF;
        int i2 = this.HEIGHT;
        canvas.drawRoundRect(rectF2, i2 / 2.0f, i2 / 2.0f, this.outLinePaint);
        String str = this.text;
        if (str != null) {
            canvas.drawText(str, (getMeasuredWidth() >> 1) + (this.TRANSLETE_TEXT * textTranslation), heightHalf + (this.textPaint.getTextSize() * 0.35f), this.textPaint);
        }
        float bounceProgress = 2.0f - (this.progress / 0.5f);
        canvas.save();
        canvas.scale(0.9f, 0.9f, AndroidUtilities.dpf2(7.0f), heightHalf);
        canvas.translate(AndroidUtilities.dp(12.0f), heightHalf - AndroidUtilities.dp(9.0f));
        if (this.progress > 0.5f) {
            this.checkPaint.setColor(this.colorTextActive);
            int endX = (int) (AndroidUtilities.dpf2(7.0f) - (AndroidUtilities.dp(4.0f) * (1.0f - bounceProgress)));
            int endY = (int) (AndroidUtilities.dpf2(13.0f) - (AndroidUtilities.dp(4.0f) * (1.0f - bounceProgress)));
            canvas.drawLine(AndroidUtilities.dpf2(7.0f), (int) AndroidUtilities.dpf2(13.0f), endX, endY, this.checkPaint);
            int endX2 = (int) (AndroidUtilities.dpf2(7.0f) + (AndroidUtilities.dp(8.0f) * (1.0f - bounceProgress)));
            int endY2 = (int) (AndroidUtilities.dpf2(13.0f) - (AndroidUtilities.dp(8.0f) * (1.0f - bounceProgress)));
            canvas.drawLine((int) AndroidUtilities.dpf2(7.0f), (int) AndroidUtilities.dpf2(13.0f), endX2, endY2, this.checkPaint);
        }
        canvas.restore();
    }

    public void denied() {
        AndroidUtilities.shakeView(this, 2.0f, 0);
    }
}
