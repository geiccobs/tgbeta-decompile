package org.telegram.ui.Components.Crop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.os.Build;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
/* loaded from: classes5.dex */
public class CropRotationWheel extends FrameLayout {
    private static final int DELTA_ANGLE = 5;
    private static final int MAX_ANGLE = 45;
    private ImageView aspectRatioButton;
    private Paint bluePaint;
    private String degreesText;
    private TextPaint degreesTextPaint;
    private ImageView mirrorButton;
    private float prevX;
    protected float rotation;
    private ImageView rotation90Button;
    private RotationWheelListener rotationListener;
    private RectF tempRect = new RectF(0.0f, 0.0f, 0.0f, 0.0f);
    private Paint whitePaint;

    /* loaded from: classes5.dex */
    public interface RotationWheelListener {
        void aspectRatioPressed();

        boolean mirror();

        void onChange(float f);

        void onEnd(float f);

        void onStart();

        boolean rotate90Pressed();
    }

    public CropRotationWheel(Context context) {
        super(context);
        Paint paint = new Paint();
        this.whitePaint = paint;
        paint.setStyle(Paint.Style.FILL);
        this.whitePaint.setColor(-1);
        this.whitePaint.setAlpha(255);
        this.whitePaint.setAntiAlias(true);
        Paint paint2 = new Paint();
        this.bluePaint = paint2;
        paint2.setStyle(Paint.Style.FILL);
        this.bluePaint.setColor(-11420173);
        this.bluePaint.setAlpha(255);
        this.bluePaint.setAntiAlias(true);
        ImageView imageView = new ImageView(context);
        this.mirrorButton = imageView;
        imageView.setImageResource(R.drawable.msg_photo_flip);
        this.mirrorButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        this.mirrorButton.setScaleType(ImageView.ScaleType.CENTER);
        this.mirrorButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.Crop.CropRotationWheel$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                CropRotationWheel.this.m2544lambda$new$0$orgtelegramuiComponentsCropCropRotationWheel(view);
            }
        });
        this.mirrorButton.setOnLongClickListener(new View.OnLongClickListener() { // from class: org.telegram.ui.Components.Crop.CropRotationWheel$$ExternalSyntheticLambda3
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view) {
                return CropRotationWheel.this.m2545lambda$new$1$orgtelegramuiComponentsCropCropRotationWheel(view);
            }
        });
        this.mirrorButton.setContentDescription(LocaleController.getString("AccDescrMirror", R.string.AccDescrMirror));
        addView(this.mirrorButton, LayoutHelper.createFrame(70, 64, 19));
        ImageView imageView2 = new ImageView(context);
        this.aspectRatioButton = imageView2;
        imageView2.setImageResource(R.drawable.msg_photo_cropfix);
        this.aspectRatioButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        this.aspectRatioButton.setScaleType(ImageView.ScaleType.CENTER);
        this.aspectRatioButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.Crop.CropRotationWheel$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                CropRotationWheel.this.m2546lambda$new$2$orgtelegramuiComponentsCropCropRotationWheel(view);
            }
        });
        this.aspectRatioButton.setVisibility(8);
        this.aspectRatioButton.setContentDescription(LocaleController.getString("AccDescrAspectRatio", R.string.AccDescrAspectRatio));
        addView(this.aspectRatioButton, LayoutHelper.createFrame(70, 64, 19));
        ImageView imageView3 = new ImageView(context);
        this.rotation90Button = imageView3;
        imageView3.setImageResource(R.drawable.msg_photo_rotate);
        this.rotation90Button.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        this.rotation90Button.setScaleType(ImageView.ScaleType.CENTER);
        this.rotation90Button.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.Crop.CropRotationWheel$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                CropRotationWheel.this.m2547lambda$new$3$orgtelegramuiComponentsCropCropRotationWheel(view);
            }
        });
        this.rotation90Button.setContentDescription(LocaleController.getString("AccDescrRotate", R.string.AccDescrRotate));
        addView(this.rotation90Button, LayoutHelper.createFrame(70, 64, 21));
        TextPaint textPaint = new TextPaint(1);
        this.degreesTextPaint = textPaint;
        textPaint.setColor(-1);
        this.degreesTextPaint.setTextSize(AndroidUtilities.dp(14.0f));
        setWillNotDraw(false);
        setRotation(0.0f, false);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-Crop-CropRotationWheel */
    public /* synthetic */ void m2544lambda$new$0$orgtelegramuiComponentsCropCropRotationWheel(View v) {
        RotationWheelListener rotationWheelListener = this.rotationListener;
        if (rotationWheelListener != null) {
            setMirrored(rotationWheelListener.mirror());
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-Crop-CropRotationWheel */
    public /* synthetic */ boolean m2545lambda$new$1$orgtelegramuiComponentsCropCropRotationWheel(View v) {
        this.aspectRatioButton.callOnClick();
        return true;
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-Crop-CropRotationWheel */
    public /* synthetic */ void m2546lambda$new$2$orgtelegramuiComponentsCropCropRotationWheel(View v) {
        RotationWheelListener rotationWheelListener = this.rotationListener;
        if (rotationWheelListener != null) {
            rotationWheelListener.aspectRatioPressed();
        }
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-Crop-CropRotationWheel */
    public /* synthetic */ void m2547lambda$new$3$orgtelegramuiComponentsCropCropRotationWheel(View v) {
        RotationWheelListener rotationWheelListener = this.rotationListener;
        if (rotationWheelListener != null) {
            setRotated(rotationWheelListener.rotate90Pressed());
        }
    }

    public void setFreeform(boolean freeform) {
    }

    public void setMirrored(boolean value) {
        this.mirrorButton.setColorFilter(value ? new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogFloatingButton), PorterDuff.Mode.MULTIPLY) : null);
    }

    public void setRotated(boolean value) {
        this.rotation90Button.setColorFilter(value ? new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogFloatingButton), PorterDuff.Mode.MULTIPLY) : null);
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.min(width, AndroidUtilities.dp(400.0f)), C.BUFFER_FLAG_ENCRYPTED), heightMeasureSpec);
    }

    public void reset(boolean resetMirror) {
        setRotation(0.0f, false);
        if (resetMirror) {
            setMirrored(false);
        }
        setRotated(false);
    }

    public void setListener(RotationWheelListener listener) {
        this.rotationListener = listener;
    }

    public void setRotation(float rotation, boolean animated) {
        this.rotation = rotation;
        float value = rotation;
        if (Math.abs(value) < 0.099d) {
            value = Math.abs(value);
        }
        this.degreesText = String.format("%.1fº", Float.valueOf(value));
        invalidate();
    }

    @Override // android.view.View
    public float getRotation() {
        return this.rotation;
    }

    public void setAspectLock(boolean enabled) {
        this.aspectRatioButton.setColorFilter(enabled ? new PorterDuffColorFilter(-11420173, PorterDuff.Mode.MULTIPLY) : null);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        float x = ev.getX();
        if (action != 0) {
            if (action == 1 || action == 3) {
                RotationWheelListener rotationWheelListener = this.rotationListener;
                if (rotationWheelListener != null) {
                    rotationWheelListener.onEnd(this.rotation);
                }
                AndroidUtilities.makeAccessibilityAnnouncement(String.format("%.1f°", Float.valueOf(this.rotation)));
            } else if (action == 2) {
                float delta = this.prevX - x;
                float f = this.rotation;
                double d = delta / AndroidUtilities.density;
                Double.isNaN(d);
                float newAngle = Math.max(-45.0f, Math.min(45.0f, f + ((float) ((d / 3.141592653589793d) / 1.649999976158142d))));
                if (Build.VERSION.SDK_INT >= 27) {
                    try {
                        if ((Math.abs(newAngle - 45.0f) < 0.001f && Math.abs(this.rotation - 45.0f) >= 0.001f) || (Math.abs(newAngle - (-45.0f)) < 0.001f && Math.abs(this.rotation - (-45.0f)) >= 0.001f)) {
                            performHapticFeedback(3, 1);
                        } else if (Math.floor(this.rotation / 2.5f) != Math.floor(newAngle / 2.5f)) {
                            performHapticFeedback(9, 1);
                        }
                    } catch (Exception e) {
                    }
                }
                if (Math.abs(newAngle - this.rotation) > 0.001d) {
                    if (Math.abs(newAngle) < 0.05d) {
                        newAngle = 0.0f;
                    }
                    setRotation(newAngle, false);
                    RotationWheelListener rotationWheelListener2 = this.rotationListener;
                    if (rotationWheelListener2 != null) {
                        rotationWheelListener2.onChange(this.rotation);
                    }
                    this.prevX = x;
                }
            }
        } else {
            this.prevX = x;
            RotationWheelListener rotationWheelListener3 = this.rotationListener;
            if (rotationWheelListener3 != null) {
                rotationWheelListener3.onStart();
            }
        }
        return true;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        Paint paint;
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        float angle = (-this.rotation) * 2.0f;
        float delta = angle % 5.0f;
        int segments = (int) Math.floor(angle / 5.0f);
        int i = 0;
        while (i < 16) {
            Paint paint2 = this.whitePaint;
            int a = i;
            if (a < segments || (a == 0 && delta < 0.0f)) {
                Paint paint3 = this.bluePaint;
                paint = paint3;
            } else {
                paint = paint2;
            }
            boolean z = false;
            int i2 = i;
            drawLine(canvas, a, delta, width, height, a == segments || (a == 0 && segments == -1), paint);
            if (i2 != 0) {
                int a2 = -i2;
                Paint paint4 = a2 > segments ? this.bluePaint : this.whitePaint;
                if (a2 == segments + 1) {
                    z = true;
                }
                drawLine(canvas, a2, delta, width, height, z, paint4);
            }
            i = i2 + 1;
        }
        this.bluePaint.setAlpha(255);
        this.tempRect.left = (width - AndroidUtilities.dp(2.5f)) / 2;
        this.tempRect.top = (height - AndroidUtilities.dp(22.0f)) / 2;
        this.tempRect.right = (AndroidUtilities.dp(2.5f) + width) / 2;
        this.tempRect.bottom = (AndroidUtilities.dp(22.0f) + height) / 2;
        canvas.drawRoundRect(this.tempRect, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), this.bluePaint);
        float tx = (width - this.degreesTextPaint.measureText(this.degreesText)) / 2.0f;
        float ty = AndroidUtilities.dp(14.0f);
        canvas.drawText(this.degreesText, tx, ty, this.degreesTextPaint);
    }

    protected void drawLine(Canvas canvas, int i, float delta, int width, int height, boolean center, Paint paint) {
        int radius = (int) ((width / 2.0f) - AndroidUtilities.dp(70.0f));
        float angle = 90.0f - ((i * 5) + delta);
        double d = radius;
        double cos = Math.cos(Math.toRadians(angle));
        Double.isNaN(d);
        int val = (int) (d * cos);
        int x = (width / 2) + val;
        float f = Math.abs(val) / radius;
        int alpha = Math.min(255, Math.max(0, (int) ((1.0f - (f * f)) * 255.0f)));
        Paint paint2 = center ? this.bluePaint : paint;
        paint2.setAlpha(alpha);
        int w = center ? 4 : 2;
        int h = AndroidUtilities.dp(center ? 16.0f : 12.0f);
        canvas.drawRect(x - (w / 2), (height - h) / 2, (w / 2) + x, (height + h) / 2, paint2);
    }
}
