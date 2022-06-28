package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.TextPaint;
import android.view.View;
import androidx.core.app.NotificationCompat;
import androidx.core.view.ViewCompat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class CheckBoxBase {
    private static Paint eraser;
    private static Paint paint;
    private boolean attachedToWindow;
    private Paint backgroundPaint;
    private int backgroundType;
    private Canvas bitmapCanvas;
    private ObjectAnimator checkAnimator;
    private Paint checkPaint;
    private String checkedText;
    private Bitmap drawBitmap;
    private boolean isChecked;
    private Theme.MessageDrawable messageDrawable;
    private View parentView;
    private float progress;
    private ProgressDelegate progressDelegate;
    private final Theme.ResourcesProvider resourcesProvider;
    private float size;
    private TextPaint textPaint;
    private boolean useDefaultCheck;
    private android.graphics.Rect bounds = new android.graphics.Rect();
    private RectF rect = new RectF();
    private Path path = new Path();
    private boolean enabled = true;
    private float backgroundAlpha = 1.0f;
    private String checkColorKey = Theme.key_checkboxCheck;
    private String backgroundColorKey = Theme.key_chat_serviceBackground;
    private String background2ColorKey = Theme.key_chat_serviceBackground;
    private boolean drawUnchecked = true;
    public long animationDuration = 200;

    /* loaded from: classes5.dex */
    public interface ProgressDelegate {
        void setProgress(float f);
    }

    public CheckBoxBase(View parent, int sz, Theme.ResourcesProvider resourcesProvider) {
        this.resourcesProvider = resourcesProvider;
        this.parentView = parent;
        this.size = sz;
        if (paint == null) {
            paint = new Paint(1);
            Paint paint2 = new Paint(1);
            eraser = paint2;
            paint2.setColor(0);
            eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
        Paint paint3 = new Paint(1);
        this.checkPaint = paint3;
        paint3.setStrokeCap(Paint.Cap.ROUND);
        this.checkPaint.setStyle(Paint.Style.STROKE);
        this.checkPaint.setStrokeJoin(Paint.Join.ROUND);
        this.checkPaint.setStrokeWidth(AndroidUtilities.dp(1.9f));
        Paint paint4 = new Paint(1);
        this.backgroundPaint = paint4;
        paint4.setStyle(Paint.Style.STROKE);
        this.backgroundPaint.setStrokeWidth(AndroidUtilities.dp(1.2f));
        this.drawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(this.size), AndroidUtilities.dp(this.size), Bitmap.Config.ARGB_4444);
        this.bitmapCanvas = new Canvas(this.drawBitmap);
    }

    public void onAttachedToWindow() {
        this.attachedToWindow = true;
    }

    public void onDetachedFromWindow() {
        this.attachedToWindow = false;
    }

    public void setBounds(int x, int y, int width, int height) {
        this.bounds.left = x;
        this.bounds.top = y;
        this.bounds.right = x + width;
        this.bounds.bottom = y + height;
    }

    public void setDrawUnchecked(boolean value) {
        this.drawUnchecked = value;
    }

    public void setProgress(float value) {
        if (this.progress == value) {
            return;
        }
        this.progress = value;
        invalidate();
        ProgressDelegate progressDelegate = this.progressDelegate;
        if (progressDelegate != null) {
            progressDelegate.setProgress(value);
        }
    }

    private void invalidate() {
        if (this.parentView.getParent() != null) {
            View parent = (View) this.parentView.getParent();
            parent.invalidate();
        }
        View parent2 = this.parentView;
        parent2.invalidate();
    }

    public void setProgressDelegate(ProgressDelegate delegate) {
        this.progressDelegate = delegate;
    }

    public float getProgress() {
        return this.progress;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setEnabled(boolean value) {
        this.enabled = value;
    }

    public void setBackgroundType(int type) {
        this.backgroundType = type;
        if (type == 12 || type == 13) {
            this.backgroundPaint.setStrokeWidth(AndroidUtilities.dp(1.0f));
        } else if (type == 4 || type == 5) {
            this.backgroundPaint.setStrokeWidth(AndroidUtilities.dp(1.9f));
            if (type == 5) {
                this.checkPaint.setStrokeWidth(AndroidUtilities.dp(1.5f));
            }
        } else if (type == 3) {
            this.backgroundPaint.setStrokeWidth(AndroidUtilities.dp(1.2f));
        } else if (type != 0) {
            this.backgroundPaint.setStrokeWidth(AndroidUtilities.dp(1.5f));
        }
    }

    private void cancelCheckAnimator() {
        ObjectAnimator objectAnimator = this.checkAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
            this.checkAnimator = null;
        }
    }

    private void animateToCheckedState(boolean newCheckedState) {
        float[] fArr = new float[1];
        fArr[0] = newCheckedState ? 1.0f : 0.0f;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, NotificationCompat.CATEGORY_PROGRESS, fArr);
        this.checkAnimator = ofFloat;
        ofFloat.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.CheckBoxBase.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (animation.equals(CheckBoxBase.this.checkAnimator)) {
                    CheckBoxBase.this.checkAnimator = null;
                }
                if (!CheckBoxBase.this.isChecked) {
                    CheckBoxBase.this.checkedText = null;
                }
            }
        });
        this.checkAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.checkAnimator.setDuration(this.animationDuration);
        this.checkAnimator.start();
    }

    public void setColor(String background, String background2, String check) {
        this.backgroundColorKey = background;
        this.background2ColorKey = background2;
        this.checkColorKey = check;
        invalidate();
    }

    public void setBackgroundDrawable(Theme.MessageDrawable drawable) {
        this.messageDrawable = drawable;
    }

    public void setUseDefaultCheck(boolean value) {
        this.useDefaultCheck = value;
    }

    public void setBackgroundAlpha(float alpha) {
        this.backgroundAlpha = alpha;
    }

    public void setNum(int num) {
        if (num >= 0) {
            this.checkedText = "" + (num + 1);
        } else if (this.checkAnimator == null) {
            this.checkedText = null;
        }
        invalidate();
    }

    public void setChecked(boolean checked, boolean animated) {
        setChecked(-1, checked, animated);
    }

    public void setChecked(int num, boolean checked, boolean animated) {
        if (num >= 0) {
            this.checkedText = "" + (num + 1);
            invalidate();
        }
        if (checked == this.isChecked) {
            return;
        }
        this.isChecked = checked;
        if (this.attachedToWindow && animated) {
            animateToCheckedState(checked);
            return;
        }
        cancelCheckAnimator();
        setProgress(checked ? 1.0f : 0.0f);
    }

    public void draw(Canvas canvas) {
        float outerRad;
        float rad;
        int cx;
        int cy;
        float y;
        float textSize;
        Bitmap bitmap;
        String str;
        int startAngle;
        int sweepAngle;
        int sweepAngle2;
        int i;
        Bitmap bitmap2 = this.drawBitmap;
        if (bitmap2 == null) {
            return;
        }
        bitmap2.eraseColor(0);
        float rad2 = AndroidUtilities.dp(this.size / 2.0f);
        int i2 = this.backgroundType;
        if (i2 == 12 || i2 == 13) {
            float outerRad2 = AndroidUtilities.dp(10.0f);
            rad = outerRad2;
            outerRad = outerRad2;
        } else if (i2 != 0 && i2 != 11) {
            rad = rad2;
            outerRad = rad2 - AndroidUtilities.dp(0.2f);
        } else {
            rad = rad2;
            outerRad = rad2;
        }
        float rad3 = this.progress;
        float roundProgress = rad3 >= 0.5f ? 1.0f : rad3 / 0.5f;
        int cx2 = this.bounds.centerX();
        int cy2 = this.bounds.centerY();
        String str2 = this.backgroundColorKey;
        if (str2 != null) {
            if (this.drawUnchecked) {
                int i3 = this.backgroundType;
                if (i3 == 12 || i3 == 13) {
                    paint.setColor(getThemedColor(str2));
                    paint.setAlpha((int) (this.backgroundAlpha * 255.0f));
                    this.backgroundPaint.setColor(getThemedColor(this.checkColorKey));
                } else if (i3 == 6 || i3 == 7) {
                    paint.setColor(getThemedColor(this.background2ColorKey));
                    this.backgroundPaint.setColor(getThemedColor(this.checkColorKey));
                } else if (i3 == 10) {
                    this.backgroundPaint.setColor(getThemedColor(this.background2ColorKey));
                } else {
                    paint.setColor((Theme.getServiceMessageColor() & ViewCompat.MEASURED_SIZE_MASK) | 671088640);
                    this.backgroundPaint.setColor(getThemedColor(this.checkColorKey));
                }
            } else {
                Paint paint2 = this.backgroundPaint;
                String str3 = this.background2ColorKey;
                if (str3 == null) {
                    str3 = this.checkColorKey;
                }
                paint2.setColor(AndroidUtilities.getOffsetColor(ViewCompat.MEASURED_SIZE_MASK, getThemedColor(str3), this.progress, this.backgroundAlpha));
            }
        } else if (this.drawUnchecked) {
            paint.setColor(Color.argb((int) (this.backgroundAlpha * 25.0f), 0, 0, 0));
            if (this.backgroundType == 8) {
                this.backgroundPaint.setColor(getThemedColor(this.background2ColorKey));
            } else {
                this.backgroundPaint.setColor(AndroidUtilities.getOffsetColor(-1, getThemedColor(this.checkColorKey), this.progress, this.backgroundAlpha));
            }
        } else {
            Paint paint3 = this.backgroundPaint;
            String str4 = this.background2ColorKey;
            if (str4 == null) {
                str4 = this.checkColorKey;
            }
            paint3.setColor(AndroidUtilities.getOffsetColor(ViewCompat.MEASURED_SIZE_MASK, getThemedColor(str4), this.progress, this.backgroundAlpha));
        }
        if (this.drawUnchecked && (i = this.backgroundType) >= 0 && i != 12 && i != 13) {
            if (i == 8 || i == 10) {
                canvas.drawCircle(cx2, cy2, rad - AndroidUtilities.dp(1.5f), this.backgroundPaint);
            } else if (i == 6 || i == 7) {
                canvas.drawCircle(cx2, cy2, rad - AndroidUtilities.dp(1.0f), paint);
                canvas.drawCircle(cx2, cy2, rad - AndroidUtilities.dp(1.5f), this.backgroundPaint);
            } else {
                canvas.drawCircle(cx2, cy2, rad, paint);
            }
        }
        paint.setColor(getThemedColor(this.checkColorKey));
        int i4 = this.backgroundType;
        if (i4 == -1 || i4 == 7 || i4 == 8 || i4 == 9 || i4 == 10) {
            cy = cy2;
            cx = cx2;
        } else {
            if (i4 == 12) {
                cy = cy2;
                cx = cx2;
            } else if (i4 == 13) {
                cy = cy2;
                cx = cx2;
            } else {
                if (i4 == 0) {
                    cy = cy2;
                    cx = cx2;
                } else if (i4 == 11) {
                    cy = cy2;
                    cx = cx2;
                } else {
                    this.rect.set(cx2 - outerRad, cy2 - outerRad, cx2 + outerRad, cy2 + outerRad);
                    int i5 = this.backgroundType;
                    if (i5 == 6) {
                        startAngle = 0;
                        sweepAngle = (int) (this.progress * (-360.0f));
                    } else if (i5 == 1) {
                        startAngle = -90;
                        sweepAngle = (int) (this.progress * (-270.0f));
                    } else {
                        startAngle = 90;
                        sweepAngle = (int) (this.progress * 270.0f);
                    }
                    if (i5 == 6) {
                        int color = getThemedColor(Theme.key_dialogBackground);
                        int alpha = Color.alpha(color);
                        this.backgroundPaint.setColor(color);
                        this.backgroundPaint.setAlpha((int) (alpha * this.progress));
                        sweepAngle2 = sweepAngle;
                        cy = cy2;
                        cx = cx2;
                        canvas.drawArc(this.rect, startAngle, sweepAngle, false, this.backgroundPaint);
                        int color2 = getThemedColor(Theme.key_chat_attachPhotoBackground);
                        int alpha2 = Color.alpha(color2);
                        this.backgroundPaint.setColor(color2);
                        this.backgroundPaint.setAlpha((int) (alpha2 * this.progress));
                    } else {
                        sweepAngle2 = sweepAngle;
                        cy = cy2;
                        cx = cx2;
                    }
                    canvas.drawArc(this.rect, startAngle, sweepAngle2, false, this.backgroundPaint);
                }
                canvas.drawCircle(cx, cy, rad, this.backgroundPaint);
            }
            this.backgroundPaint.setStyle(Paint.Style.FILL);
            Theme.MessageDrawable messageDrawable = this.messageDrawable;
            if (messageDrawable != null && messageDrawable.hasGradient()) {
                Shader shader = this.messageDrawable.getGradientShader();
                Matrix matrix = this.messageDrawable.getMatrix();
                matrix.reset();
                this.messageDrawable.applyMatrixScale();
                matrix.postTranslate(0.0f, (-this.messageDrawable.getTopY()) + this.bounds.top);
                shader.setLocalMatrix(matrix);
                this.backgroundPaint.setShader(shader);
            } else {
                this.backgroundPaint.setShader(null);
            }
            canvas.drawCircle(cx, cy, (rad - AndroidUtilities.dp(1.0f)) * this.backgroundAlpha, this.backgroundPaint);
            this.backgroundPaint.setStyle(Paint.Style.STROKE);
        }
        if (roundProgress > 0.0f) {
            float f = this.progress;
            float checkProgress = f < 0.5f ? 0.0f : (f - 0.5f) / 0.5f;
            int i6 = this.backgroundType;
            if (i6 == 9) {
                paint.setColor(getThemedColor(this.background2ColorKey));
            } else if (i6 == 11 || i6 == 6 || i6 == 7 || i6 == 10 || (!this.drawUnchecked && this.backgroundColorKey != null)) {
                paint.setColor(getThemedColor(this.backgroundColorKey));
            } else {
                paint.setColor(getThemedColor(this.enabled ? Theme.key_checkbox : Theme.key_checkboxDisabled));
            }
            if (this.useDefaultCheck || (str = this.checkColorKey) == null) {
                this.checkPaint.setColor(getThemedColor(Theme.key_checkboxCheck));
            } else {
                this.checkPaint.setColor(getThemedColor(str));
            }
            int i7 = this.backgroundType;
            if (i7 != -1) {
                if (i7 == 12 || i7 == 13) {
                    paint.setAlpha((int) (roundProgress * 255.0f));
                    this.bitmapCanvas.drawCircle(this.drawBitmap.getWidth() / 2, this.drawBitmap.getHeight() / 2, rad * roundProgress, paint);
                } else {
                    float rad4 = rad - AndroidUtilities.dp(0.5f);
                    this.bitmapCanvas.drawCircle(this.drawBitmap.getWidth() / 2, this.drawBitmap.getHeight() / 2, rad4, paint);
                    this.bitmapCanvas.drawCircle(this.drawBitmap.getWidth() / 2, this.drawBitmap.getHeight() / 2, (1.0f - roundProgress) * rad4, eraser);
                }
                canvas.drawBitmap(this.drawBitmap, cx - (bitmap.getWidth() / 2), cy - (this.drawBitmap.getHeight() / 2), (Paint) null);
            }
            if (checkProgress != 0.0f) {
                if (this.checkedText != null) {
                    if (this.textPaint == null) {
                        TextPaint textPaint = new TextPaint(1);
                        this.textPaint = textPaint;
                        textPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                    }
                    switch (this.checkedText.length()) {
                        case 0:
                        case 1:
                        case 2:
                            textSize = 14.0f;
                            y = 18.0f;
                            break;
                        case 3:
                            textSize = 10.0f;
                            y = 16.5f;
                            break;
                        default:
                            textSize = 8.0f;
                            y = 15.75f;
                            break;
                    }
                    this.textPaint.setTextSize(AndroidUtilities.dp(textSize));
                    this.textPaint.setColor(getThemedColor(this.checkColorKey));
                    canvas.save();
                    canvas.scale(checkProgress, 1.0f, cx, cy);
                    String str5 = this.checkedText;
                    canvas.drawText(str5, cx - (this.textPaint.measureText(str5) / 2.0f), AndroidUtilities.dp(y), this.textPaint);
                    canvas.restore();
                    return;
                }
                this.path.reset();
                float scale = 1.0f;
                int i8 = this.backgroundType;
                if (i8 == -1) {
                    scale = 1.4f;
                } else if (i8 == 5) {
                    scale = 0.8f;
                }
                float checkSide = AndroidUtilities.dp(9.0f * scale) * checkProgress;
                float smallCheckSide = AndroidUtilities.dp(scale * 4.0f) * checkProgress;
                int x = cx - AndroidUtilities.dp(1.5f);
                int y2 = AndroidUtilities.dp(4.0f) + cy;
                float side = (float) Math.sqrt((smallCheckSide * smallCheckSide) / 2.0f);
                this.path.moveTo(x - side, y2 - side);
                this.path.lineTo(x, y2);
                float side2 = (float) Math.sqrt((checkSide * checkSide) / 2.0f);
                this.path.lineTo(x + side2, y2 - side2);
                canvas.drawPath(this.path, this.checkPaint);
            }
        }
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
