package org.telegram.ui.Components;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.Property;
import android.view.View;
import androidx.core.app.NotificationCompat;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimationProperties;
/* loaded from: classes5.dex */
public class WallpaperCheckBoxView extends View {
    private static final float progressBounceDiff = 0.2f;
    private Paint backgroundPaint;
    private ObjectAnimator checkAnimator;
    private Paint checkPaint;
    private String currentText;
    private int currentTextSize;
    private Bitmap drawBitmap;
    private Canvas drawCanvas;
    private Paint eraserPaint;
    private boolean isChecked;
    private int maxTextSize;
    private View parentView;
    private float progress;
    private TextPaint textPaint;
    private int[] colors = new int[4];
    public final Property<WallpaperCheckBoxView, Float> PROGRESS_PROPERTY = new AnimationProperties.FloatProperty<WallpaperCheckBoxView>(NotificationCompat.CATEGORY_PROGRESS) { // from class: org.telegram.ui.Components.WallpaperCheckBoxView.1
        public void setValue(WallpaperCheckBoxView object, float value) {
            WallpaperCheckBoxView.this.progress = value;
            WallpaperCheckBoxView.this.invalidate();
        }

        public Float get(WallpaperCheckBoxView object) {
            return Float.valueOf(WallpaperCheckBoxView.this.progress);
        }
    };
    private RectF rect = new RectF();

    public WallpaperCheckBoxView(Context context, boolean check, View parent) {
        super(context);
        if (check) {
            this.drawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f), Bitmap.Config.ARGB_4444);
            this.drawCanvas = new Canvas(this.drawBitmap);
        }
        this.parentView = parent;
        TextPaint textPaint = new TextPaint(1);
        this.textPaint = textPaint;
        textPaint.setTextSize(AndroidUtilities.dp(14.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        Paint paint = new Paint(1);
        this.checkPaint = paint;
        paint.setStyle(Paint.Style.STROKE);
        this.checkPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        this.checkPaint.setColor(0);
        this.checkPaint.setStrokeCap(Paint.Cap.ROUND);
        this.checkPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        Paint paint2 = new Paint(1);
        this.eraserPaint = paint2;
        paint2.setColor(0);
        this.eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        this.backgroundPaint = new Paint(1);
    }

    public void setText(String text, int current, int max) {
        this.currentText = text;
        this.currentTextSize = current;
        this.maxTextSize = max;
    }

    public void setColor(int index, int color) {
        if (this.colors == null) {
            this.colors = new int[4];
        }
        this.colors[index] = color;
        invalidate();
    }

    public TextPaint getTextPaint() {
        return this.textPaint;
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(this.maxTextSize + AndroidUtilities.dp(56.0f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), C.BUFFER_FLAG_ENCRYPTED));
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        float checkProgress;
        float bounceProgress;
        this.rect.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
        Theme.applyServiceShaderMatrixForView(this, this.parentView);
        canvas.drawRoundRect(this.rect, getMeasuredHeight() / 2, getMeasuredHeight() / 2, Theme.chat_actionBackgroundPaint);
        if (Theme.hasGradientService()) {
            canvas.drawRoundRect(this.rect, getMeasuredHeight() / 2, getMeasuredHeight() / 2, Theme.chat_actionBackgroundGradientDarkenPaint);
        }
        this.textPaint.setColor(Theme.getColor(Theme.key_chat_serviceText));
        int x = ((getMeasuredWidth() - this.currentTextSize) - AndroidUtilities.dp(28.0f)) / 2;
        canvas.drawText(this.currentText, AndroidUtilities.dp(28.0f) + x, AndroidUtilities.dp(21.0f), this.textPaint);
        canvas.save();
        canvas.translate(x, AndroidUtilities.dp(7.0f));
        if (this.drawBitmap != null) {
            float bounceProgress2 = this.progress;
            if (bounceProgress2 <= 0.5f) {
                bounceProgress = bounceProgress2 / 0.5f;
                checkProgress = bounceProgress;
            } else {
                bounceProgress = 2.0f - (bounceProgress2 / 0.5f);
                checkProgress = 1.0f;
            }
            float bounce = AndroidUtilities.dp(1.0f) * bounceProgress;
            this.rect.set(bounce, bounce, AndroidUtilities.dp(18.0f) - bounce, AndroidUtilities.dp(18.0f) - bounce);
            this.drawBitmap.eraseColor(0);
            this.backgroundPaint.setColor(Theme.getColor(Theme.key_chat_serviceText));
            Canvas canvas2 = this.drawCanvas;
            RectF rectF = this.rect;
            canvas2.drawRoundRect(rectF, rectF.width() / 2.0f, this.rect.height() / 2.0f, this.backgroundPaint);
            if (checkProgress != 1.0f) {
                float rad = Math.min(AndroidUtilities.dp(7.0f), (AndroidUtilities.dp(7.0f) * checkProgress) + bounce);
                this.rect.set(AndroidUtilities.dp(2.0f) + rad, AndroidUtilities.dp(2.0f) + rad, AndroidUtilities.dp(16.0f) - rad, AndroidUtilities.dp(16.0f) - rad);
                Canvas canvas3 = this.drawCanvas;
                RectF rectF2 = this.rect;
                canvas3.drawRoundRect(rectF2, rectF2.width() / 2.0f, this.rect.height() / 2.0f, this.eraserPaint);
            }
            if (this.progress > 0.5f) {
                int endX = (int) (AndroidUtilities.dp(7.3f) - (AndroidUtilities.dp(2.5f) * (1.0f - bounceProgress)));
                int endY = (int) (AndroidUtilities.dp(13.0f) - (AndroidUtilities.dp(2.5f) * (1.0f - bounceProgress)));
                this.drawCanvas.drawLine(AndroidUtilities.dp(7.3f), AndroidUtilities.dp(13.0f), endX, endY, this.checkPaint);
                int endX2 = (int) (AndroidUtilities.dp(7.3f) + (AndroidUtilities.dp(6.0f) * (1.0f - bounceProgress)));
                int endY2 = (int) (AndroidUtilities.dp(13.0f) - (AndroidUtilities.dp(6.0f) * (1.0f - bounceProgress)));
                this.drawCanvas.drawLine(AndroidUtilities.dp(7.3f), AndroidUtilities.dp(13.0f), endX2, endY2, this.checkPaint);
            }
            canvas.drawBitmap(this.drawBitmap, 0.0f, 0.0f, (Paint) null);
        } else {
            this.rect.set(0.0f, 0.0f, AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f));
            int[] iArr = this.colors;
            if (iArr[3] != 0) {
                for (int a = 0; a < 4; a++) {
                    this.backgroundPaint.setColor(this.colors[a]);
                    canvas.drawArc(this.rect, (a * 90) - 90, 90.0f, true, this.backgroundPaint);
                }
            } else if (iArr[2] != 0) {
                for (int a2 = 0; a2 < 3; a2++) {
                    this.backgroundPaint.setColor(this.colors[a2]);
                    canvas.drawArc(this.rect, (a2 * 120) - 90, 120.0f, true, this.backgroundPaint);
                }
            } else if (iArr[1] != 0) {
                for (int a3 = 0; a3 < 2; a3++) {
                    this.backgroundPaint.setColor(this.colors[a3]);
                    canvas.drawArc(this.rect, (a3 * 180) - 90, 180.0f, true, this.backgroundPaint);
                }
            } else {
                this.backgroundPaint.setColor(iArr[0]);
                RectF rectF3 = this.rect;
                canvas.drawRoundRect(rectF3, rectF3.width() / 2.0f, this.rect.height() / 2.0f, this.backgroundPaint);
            }
        }
        canvas.restore();
    }

    private void setProgress(float value) {
        if (this.progress == value) {
            return;
        }
        this.progress = value;
        invalidate();
    }

    private void cancelCheckAnimator() {
        ObjectAnimator objectAnimator = this.checkAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
    }

    private void animateToCheckedState(boolean newCheckedState) {
        Property<WallpaperCheckBoxView, Float> property = this.PROGRESS_PROPERTY;
        float[] fArr = new float[1];
        fArr[0] = newCheckedState ? 1.0f : 0.0f;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, property, fArr);
        this.checkAnimator = ofFloat;
        ofFloat.setDuration(300L);
        this.checkAnimator.start();
    }

    @Override // android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public void setChecked(boolean checked, boolean animated) {
        if (checked == this.isChecked) {
            return;
        }
        this.isChecked = checked;
        if (animated) {
            animateToCheckedState(checked);
            return;
        }
        cancelCheckAnimator();
        this.progress = checked ? 1.0f : 0.0f;
        invalidate();
    }

    public boolean isChecked() {
        return this.isChecked;
    }
}
