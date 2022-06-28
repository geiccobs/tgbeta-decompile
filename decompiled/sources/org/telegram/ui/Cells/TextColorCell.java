package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Property;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.internal.view.SupportMenu;
import androidx.core.view.InputDeviceCompat;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
/* loaded from: classes4.dex */
public class TextColorCell extends FrameLayout {
    private static Paint colorPaint;
    public static final int[] colors = {-1031100, -29183, -12769, -8792480, -12521994, -12140801, -2984711, -45162, -4473925};
    public static final int[] colorsToSave = {SupportMenu.CATEGORY_MASK, -29183, InputDeviceCompat.SOURCE_ANY, -16711936, -16711681, -16776961, -2984711, -65281, -1};
    private float alpha;
    private int currentColor;
    private boolean needDivider;
    private Theme.ResourcesProvider resourcesProvider;
    private TextView textView;

    public TextColorCell(Context context) {
        this(context, null);
    }

    public TextColorCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.alpha = 1.0f;
        this.resourcesProvider = resourcesProvider;
        if (colorPaint == null) {
            colorPaint = new Paint(1);
        }
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        int i = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (!LocaleController.isRTL ? 3 : i) | 48, 21.0f, 0.0f, 21.0f, 0.0f));
    }

    @Override // android.view.View
    public void setAlpha(float value) {
        this.alpha = value;
        invalidate();
    }

    @Override // android.view.View
    public float getAlpha() {
        return this.alpha;
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0), C.BUFFER_FLAG_ENCRYPTED));
    }

    public void setTextAndColor(String text, int color, boolean divider) {
        this.textView.setText(text);
        this.needDivider = divider;
        this.currentColor = color;
        setWillNotDraw(!divider && color == 0);
        invalidate();
    }

    public void setEnabled(boolean value, ArrayList<Animator> animators) {
        super.setEnabled(value);
        float f = 1.0f;
        if (animators != null) {
            TextView textView = this.textView;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = value ? 1.0f : 0.5f;
            animators.add(ObjectAnimator.ofFloat(textView, property, fArr));
            Property property2 = View.ALPHA;
            float[] fArr2 = new float[1];
            if (!value) {
                f = 0.5f;
            }
            fArr2[0] = f;
            animators.add(ObjectAnimator.ofFloat(this, property2, fArr2));
            return;
        }
        this.textView.setAlpha(value ? 1.0f : 0.5f);
        if (!value) {
            f = 0.5f;
        }
        setAlpha(f);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(20.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
        }
        int i = this.currentColor;
        if (i != 0) {
            colorPaint.setColor(i);
            colorPaint.setAlpha((int) (this.alpha * 255.0f));
            canvas.drawCircle(LocaleController.isRTL ? AndroidUtilities.dp(33.0f) : getMeasuredWidth() - AndroidUtilities.dp(33.0f), getMeasuredHeight() / 2, AndroidUtilities.dp(10.0f), colorPaint);
        }
    }
}
