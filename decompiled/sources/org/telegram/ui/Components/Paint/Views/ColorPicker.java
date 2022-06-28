package org.telegram.ui.Components.Paint.Views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Paint.Swatch;
/* loaded from: classes5.dex */
public class ColorPicker extends FrameLayout {
    private static final int[] COLORS = {-1431751, -2409774, -13610525, -11942419, -8337308, -205211, -223667, -16777216, -1};
    private static final float[] LOCATIONS = {0.0f, 0.14f, 0.24f, 0.39f, 0.49f, 0.62f, 0.73f, 0.85f, 1.0f};
    private boolean changingWeight;
    private ColorPickerDelegate delegate;
    private boolean dragging;
    private float draggingFactor;
    private boolean interacting;
    private float location;
    public ImageView settingsButton;
    private ImageView undoButton;
    private boolean wasChangingWeight;
    private OvershootInterpolator interpolator = new OvershootInterpolator(1.02f);
    private Paint gradientPaint = new Paint(1);
    private Paint backgroundPaint = new Paint(1);
    private Paint swatchPaint = new Paint(1);
    private Paint swatchStrokePaint = new Paint(1);
    private RectF rectF = new RectF();
    private float weight = 0.016773745f;
    private Drawable shadowDrawable = getResources().getDrawable(R.drawable.knob_shadow);

    /* loaded from: classes5.dex */
    public interface ColorPickerDelegate {
        void onBeganColorPicking();

        void onColorValueChanged();

        void onFinishedColorPicking();

        void onSettingsPressed();

        void onUndoPressed();
    }

    public ColorPicker(Context context) {
        super(context);
        setWillNotDraw(false);
        this.backgroundPaint.setColor(-1);
        this.swatchStrokePaint.setStyle(Paint.Style.STROKE);
        this.swatchStrokePaint.setStrokeWidth(AndroidUtilities.dp(1.0f));
        ImageView imageView = new ImageView(context);
        this.settingsButton = imageView;
        imageView.setContentDescription(LocaleController.getString("AccDescrBrushType", R.string.AccDescrBrushType));
        this.settingsButton.setScaleType(ImageView.ScaleType.CENTER);
        this.settingsButton.setImageResource(R.drawable.photo_paint_brush);
        addView(this.settingsButton, LayoutHelper.createFrame(46, 52.0f));
        this.settingsButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.Paint.Views.ColorPicker$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ColorPicker.this.m2788lambda$new$0$orgtelegramuiComponentsPaintViewsColorPicker(view);
            }
        });
        ImageView imageView2 = new ImageView(context);
        this.undoButton = imageView2;
        imageView2.setContentDescription(LocaleController.getString("Undo", R.string.Undo));
        this.undoButton.setScaleType(ImageView.ScaleType.CENTER);
        this.undoButton.setImageResource(R.drawable.photo_undo);
        addView(this.undoButton, LayoutHelper.createFrame(46, 52.0f));
        this.undoButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.Paint.Views.ColorPicker$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ColorPicker.this.m2789lambda$new$1$orgtelegramuiComponentsPaintViewsColorPicker(view);
            }
        });
        SharedPreferences preferences = context.getSharedPreferences("paint", 0);
        this.location = preferences.getFloat("last_color_location", 1.0f);
        setWeight(preferences.getFloat("last_color_weight", 0.016773745f));
        setLocation(this.location);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-Paint-Views-ColorPicker */
    public /* synthetic */ void m2788lambda$new$0$orgtelegramuiComponentsPaintViewsColorPicker(View v) {
        ColorPickerDelegate colorPickerDelegate = this.delegate;
        if (colorPickerDelegate != null) {
            colorPickerDelegate.onSettingsPressed();
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-Paint-Views-ColorPicker */
    public /* synthetic */ void m2789lambda$new$1$orgtelegramuiComponentsPaintViewsColorPicker(View v) {
        ColorPickerDelegate colorPickerDelegate = this.delegate;
        if (colorPickerDelegate != null) {
            colorPickerDelegate.onUndoPressed();
        }
    }

    public void setUndoEnabled(boolean enabled) {
        this.undoButton.setAlpha(enabled ? 1.0f : 0.3f);
        this.undoButton.setEnabled(enabled);
    }

    public void setDelegate(ColorPickerDelegate colorPickerDelegate) {
        this.delegate = colorPickerDelegate;
    }

    public View getSettingsButton() {
        return this.settingsButton;
    }

    public void setSettingsButtonImage(int resId) {
        this.settingsButton.setImageResource(resId);
    }

    public Swatch getSwatch() {
        return new Swatch(colorForLocation(this.location), this.location, this.weight);
    }

    public void setSwatch(Swatch swatch) {
        setLocation(swatch.colorLocation);
        setWeight(swatch.brushWeight);
    }

    public int colorForLocation(float location) {
        float[] fArr;
        int[] iArr;
        if (location <= 0.0f) {
            return COLORS[0];
        }
        if (location >= 1.0f) {
            return COLORS[iArr.length - 1];
        }
        int leftIndex = -1;
        int rightIndex = -1;
        int i = 1;
        while (true) {
            fArr = LOCATIONS;
            if (i >= fArr.length) {
                break;
            }
            float value = fArr[i];
            if (value < location) {
                i++;
            } else {
                leftIndex = i - 1;
                rightIndex = i;
                break;
            }
        }
        float leftLocation = fArr[leftIndex];
        int[] iArr2 = COLORS;
        int leftColor = iArr2[leftIndex];
        float rightLocation = fArr[rightIndex];
        int rightColor = iArr2[rightIndex];
        float factor = (location - leftLocation) / (rightLocation - leftLocation);
        return interpolateColors(leftColor, rightColor, factor);
    }

    private int interpolateColors(int leftColor, int rightColor, float factor) {
        float factor2 = Math.min(Math.max(factor, 0.0f), 1.0f);
        int r1 = Color.red(leftColor);
        int r2 = Color.red(rightColor);
        int g1 = Color.green(leftColor);
        int g2 = Color.green(rightColor);
        int b1 = Color.blue(leftColor);
        int b2 = Color.blue(rightColor);
        int r = Math.min(255, (int) (r1 + ((r2 - r1) * factor2)));
        int g = Math.min(255, (int) (g1 + ((g2 - g1) * factor2)));
        int b = Math.min(255, (int) (b1 + ((b2 - b1) * factor2)));
        return Color.argb(255, r, g, b);
    }

    public void setLocation(float value) {
        this.location = value;
        int color = colorForLocation(value);
        this.swatchPaint.setColor(color);
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        if (hsv[0] < 0.001d && hsv[1] < 0.001d && hsv[2] > 0.92f) {
            int c = (int) ((1.0f - (((hsv[2] - 0.92f) / 0.08f) * 0.22f)) * 255.0f);
            this.swatchStrokePaint.setColor(Color.rgb(c, c, c));
        } else {
            this.swatchStrokePaint.setColor(color);
        }
        invalidate();
    }

    public void setWeight(float value) {
        this.weight = value;
        invalidate();
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        ColorPickerDelegate colorPickerDelegate;
        if (event.getPointerCount() > 1) {
            return false;
        }
        float x = event.getX() - this.rectF.left;
        float y = event.getY() - this.rectF.top;
        if (!this.interacting && y < (-AndroidUtilities.dp(10.0f))) {
            return false;
        }
        int action = event.getActionMasked();
        if (action == 3 || action == 1 || action == 6) {
            if (this.interacting && (colorPickerDelegate = this.delegate) != null) {
                colorPickerDelegate.onFinishedColorPicking();
                SharedPreferences.Editor editor = getContext().getSharedPreferences("paint", 0).edit();
                editor.putFloat("last_color_location", this.location);
                editor.putFloat("last_color_weight", this.weight);
                editor.commit();
            }
            this.interacting = false;
            this.wasChangingWeight = this.changingWeight;
            this.changingWeight = false;
            setDragging(false, true);
        } else if (action == 0 || action == 2) {
            if (!this.interacting) {
                this.interacting = true;
                ColorPickerDelegate colorPickerDelegate2 = this.delegate;
                if (colorPickerDelegate2 != null) {
                    colorPickerDelegate2.onBeganColorPicking();
                }
            }
            float colorLocation = Math.max(0.0f, Math.min(1.0f, x / this.rectF.width()));
            setLocation(colorLocation);
            setDragging(true, true);
            if (y < (-AndroidUtilities.dp(10.0f))) {
                this.changingWeight = true;
                float weightLocation = ((-y) - AndroidUtilities.dp(10.0f)) / AndroidUtilities.dp(190.0f);
                setWeight(Math.max(0.0f, Math.min(1.0f, weightLocation)));
            }
            ColorPickerDelegate colorPickerDelegate3 = this.delegate;
            if (colorPickerDelegate3 != null) {
                colorPickerDelegate3.onColorValueChanged();
            }
            return true;
        }
        return false;
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = right - left;
        int height = bottom - top;
        this.gradientPaint.setShader(new LinearGradient(AndroidUtilities.dp(56.0f), 0.0f, width - AndroidUtilities.dp(56.0f), 0.0f, COLORS, LOCATIONS, Shader.TileMode.REPEAT));
        int y = height - AndroidUtilities.dp(32.0f);
        this.rectF.set(AndroidUtilities.dp(56.0f), y, width - AndroidUtilities.dp(56.0f), AndroidUtilities.dp(12.0f) + y);
        ImageView imageView = this.settingsButton;
        imageView.layout(width - imageView.getMeasuredWidth(), height - AndroidUtilities.dp(52.0f), width, height);
        this.undoButton.layout(0, height - AndroidUtilities.dp(52.0f), this.settingsButton.getMeasuredWidth(), height);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), this.gradientPaint);
        int cx = (int) (this.rectF.left + (this.rectF.width() * this.location));
        int cy = (int) ((this.rectF.centerY() + (this.draggingFactor * (-AndroidUtilities.dp(70.0f)))) - (this.changingWeight ? this.weight * AndroidUtilities.dp(190.0f) : 0.0f));
        int side = (int) (AndroidUtilities.dp(24.0f) * (this.draggingFactor + 1.0f) * 0.5f);
        this.shadowDrawable.setBounds(cx - side, cy - side, cx + side, cy + side);
        this.shadowDrawable.draw(canvas);
        float swatchRadius = (((int) Math.floor(AndroidUtilities.dp(4.0f) + ((AndroidUtilities.dp(19.0f) - AndroidUtilities.dp(4.0f)) * this.weight))) * (this.draggingFactor + 1.0f)) / 2.0f;
        canvas.drawCircle(cx, cy, (AndroidUtilities.dp(22.0f) / 2) * (this.draggingFactor + 1.0f), this.backgroundPaint);
        canvas.drawCircle(cx, cy, swatchRadius, this.swatchPaint);
        canvas.drawCircle(cx, cy, swatchRadius - AndroidUtilities.dp(0.5f), this.swatchStrokePaint);
    }

    private void setDraggingFactor(float factor) {
        this.draggingFactor = factor;
        invalidate();
    }

    public float getDraggingFactor() {
        return this.draggingFactor;
    }

    private void setDragging(boolean value, boolean animated) {
        if (this.dragging == value) {
            return;
        }
        this.dragging = value;
        float target = value ? 1.0f : 0.0f;
        if (animated) {
            Animator a = ObjectAnimator.ofFloat(this, "draggingFactor", this.draggingFactor, target);
            a.setInterpolator(this.interpolator);
            int duration = 300;
            if (this.wasChangingWeight) {
                duration = (int) (300 + (this.weight * 75.0f));
            }
            a.setDuration(duration);
            a.start();
            return;
        }
        setDraggingFactor(target);
    }
}
