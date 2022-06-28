package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadioButton;
/* loaded from: classes4.dex */
public class RadioButtonCell extends FrameLayout {
    private boolean needDivider;
    private RadioButton radioButton;
    private TextView textView;
    public TextView valueTextView;

    public RadioButtonCell(Context context) {
        this(context, false);
    }

    public RadioButtonCell(Context context, boolean dialog) {
        super(context);
        RadioButton radioButton = new RadioButton(context);
        this.radioButton = radioButton;
        radioButton.setSize(AndroidUtilities.dp(20.0f));
        if (dialog) {
            this.radioButton.setColor(Theme.getColor(Theme.key_dialogRadioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
        } else {
            this.radioButton.setColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_radioBackgroundChecked));
        }
        int i = 5;
        addView(this.radioButton, LayoutHelper.createFrame(22, 22.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0 : 20, 10.0f, !LocaleController.isRTL ? 0 : 20, 0.0f));
        TextView textView = new TextView(context);
        this.textView = textView;
        if (dialog) {
            textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        } else {
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        }
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        int i2 = 61;
        addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 23 : 61, 10.0f, LocaleController.isRTL ? 61 : 23, 0.0f));
        TextView textView2 = new TextView(context);
        this.valueTextView = textView2;
        if (dialog) {
            textView2.setTextColor(Theme.getColor(Theme.key_dialogTextGray2));
        } else {
            textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        }
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.valueTextView.setLines(0);
        this.valueTextView.setMaxLines(0);
        this.valueTextView.setSingleLine(false);
        this.valueTextView.setPadding(0, 0, 0, AndroidUtilities.dp(12.0f));
        addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0f, (!LocaleController.isRTL ? 3 : i) | 48, LocaleController.isRTL ? 17 : 61, 35.0f, !LocaleController.isRTL ? 17 : i2, 0.0f));
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(0, 0));
    }

    public void setTextAndValue(String text, String value, boolean divider, boolean checked) {
        this.textView.setText(text);
        this.valueTextView.setText(value);
        this.radioButton.setChecked(checked, false);
        this.needDivider = divider;
    }

    public void setChecked(boolean checked, boolean animated) {
        this.radioButton.setChecked(checked, animated);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            float f = 0.0f;
            float dp = AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : 60.0f);
            float height = getHeight() - 1;
            int measuredWidth = getMeasuredWidth();
            if (LocaleController.isRTL) {
                f = 60.0f;
            }
            canvas.drawLine(dp, height, measuredWidth - AndroidUtilities.dp(f), getHeight() - 1, Theme.dividerPaint);
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName("android.widget.RadioButton");
        info.setCheckable(true);
        info.setChecked(this.radioButton.isChecked());
    }
}
