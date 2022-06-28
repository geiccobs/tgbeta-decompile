package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CheckBoxSquare;
import org.telegram.ui.Components.LayoutHelper;
/* loaded from: classes4.dex */
public class CheckBoxCell extends FrameLayout {
    public static final int TYPE_CHECK_BOX_ROUND = 4;
    private View checkBox;
    private CheckBox2 checkBoxRound;
    private int checkBoxSize;
    private CheckBoxSquare checkBoxSquare;
    private int currentType;
    private boolean isMultiline;
    private boolean needDivider;
    private final Theme.ResourcesProvider resourcesProvider;
    private TextView textView;
    private TextView valueTextView;

    public CheckBoxCell(Context context, int type) {
        this(context, type, 17, null);
    }

    public CheckBoxCell(Context context, int type, Theme.ResourcesProvider resourcesProvider) {
        this(context, type, 17, resourcesProvider);
    }

    public CheckBoxCell(Context context, int type, int padding, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.checkBoxSize = 18;
        this.resourcesProvider = resourcesProvider;
        this.currentType = type;
        TextView textView = new TextView(context);
        this.textView = textView;
        int i = 5;
        boolean z = true;
        textView.setTag(Integer.valueOf(getThemedColor((type == 1 || type == 5) ? Theme.key_dialogTextBlack : Theme.key_windowBackgroundWhiteBlackText)));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        if (type == 3) {
            this.textView.setGravity(19);
            addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, 51, 29.0f, 0.0f, 0.0f, 0.0f));
            this.textView.setPadding(0, 0, 0, AndroidUtilities.dp(3.0f));
        } else {
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            if (type == 2) {
                addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0 : 29, 0.0f, LocaleController.isRTL ? 29 : 0, 0.0f));
            } else {
                int offset = type == 4 ? 56 : 46;
                addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? padding : (padding - 17) + offset, 0.0f, LocaleController.isRTL ? offset + (padding - 17) : padding, 0.0f));
            }
        }
        TextView textView2 = new TextView(context);
        this.valueTextView = textView2;
        textView2.setTag((type == 1 || type == 5) ? Theme.key_dialogTextBlue : Theme.key_windowBackgroundWhiteValueText);
        this.valueTextView.setTextSize(1, 16.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.valueTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 16);
        addView(this.valueTextView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 3 : 5) | 48, padding, 0.0f, padding, 0.0f));
        if (type == 4) {
            CheckBox2 checkBox2 = new CheckBox2(context, 21, resourcesProvider);
            this.checkBoxRound = checkBox2;
            this.checkBox = checkBox2;
            checkBox2.setDrawUnchecked(true);
            int i2 = 0;
            this.checkBoxRound.setChecked(true, false);
            this.checkBoxRound.setDrawBackgroundAsArc(10);
            this.checkBoxSize = 21;
            addView(this.checkBox, LayoutHelper.createFrame(21, 21, (!LocaleController.isRTL ? 3 : i) | 48, LocaleController.isRTL ? 0 : padding, 16.0f, LocaleController.isRTL ? padding : i2, 0.0f));
        } else {
            int i3 = 0;
            if (type != 1 && type != 5) {
                z = false;
            }
            CheckBoxSquare checkBoxSquare = new CheckBoxSquare(context, z, resourcesProvider);
            this.checkBoxSquare = checkBoxSquare;
            this.checkBox = checkBoxSquare;
            this.checkBoxSize = 18;
            if (type == 5) {
                addView(checkBoxSquare, LayoutHelper.createFrame(18, 18, (!LocaleController.isRTL ? 3 : i) | 16, LocaleController.isRTL ? 0 : padding, 0.0f, LocaleController.isRTL ? padding : i3, 0.0f));
            } else if (type == 3) {
                addView(checkBoxSquare, LayoutHelper.createFrame(18, 18, 51, 0.0f, 15.0f, 0.0f, 0.0f));
            } else if (type == 2) {
                addView(checkBoxSquare, LayoutHelper.createFrame(18, 18, (!LocaleController.isRTL ? 3 : i) | 48, 0.0f, 15.0f, 0.0f, 0.0f));
            } else {
                addView(checkBoxSquare, LayoutHelper.createFrame(18, 18, (!LocaleController.isRTL ? 3 : i) | 48, LocaleController.isRTL ? 0 : padding, 16.0f, LocaleController.isRTL ? padding : i3, 0.0f));
            }
        }
        updateTextColor();
    }

    public void updateTextColor() {
        TextView textView = this.textView;
        int i = this.currentType;
        textView.setTextColor(getThemedColor((i == 1 || i == 5) ? Theme.key_dialogTextBlack : Theme.key_windowBackgroundWhiteBlackText));
        TextView textView2 = this.textView;
        int i2 = this.currentType;
        textView2.setLinkTextColor(getThemedColor((i2 == 1 || i2 == 5) ? Theme.key_dialogTextLink : Theme.key_windowBackgroundWhiteLinkText));
        TextView textView3 = this.valueTextView;
        int i3 = this.currentType;
        textView3.setTextColor(getThemedColor((i3 == 1 || i3 == 5) ? Theme.key_dialogTextBlue : Theme.key_windowBackgroundWhiteValueText));
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.currentType == 3) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            this.valueTextView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(10.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), C.BUFFER_FLAG_ENCRYPTED));
            this.textView.measure(View.MeasureSpec.makeMeasureSpec(width - AndroidUtilities.dp(34.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), C.BUFFER_FLAG_ENCRYPTED));
            this.checkBox.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.checkBoxSize), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.checkBoxSize), C.BUFFER_FLAG_ENCRYPTED));
            setMeasuredDimension(this.textView.getMeasuredWidth() + AndroidUtilities.dp(29.0f), AndroidUtilities.dp(50.0f));
        } else if (this.isMultiline) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(0, 0));
        } else {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0));
            int availableWidth = ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp(34.0f);
            this.valueTextView.measure(View.MeasureSpec.makeMeasureSpec(availableWidth / 2, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), C.BUFFER_FLAG_ENCRYPTED));
            this.textView.measure(View.MeasureSpec.makeMeasureSpec((availableWidth - this.valueTextView.getMeasuredWidth()) - AndroidUtilities.dp(8.0f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), C.BUFFER_FLAG_ENCRYPTED));
            this.checkBox.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.checkBoxSize), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.checkBoxSize), C.BUFFER_FLAG_ENCRYPTED));
        }
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setText(CharSequence text, String value, boolean checked, boolean divider) {
        this.textView.setText(text);
        CheckBox2 checkBox2 = this.checkBoxRound;
        if (checkBox2 != null) {
            checkBox2.setChecked(checked, false);
        } else {
            this.checkBoxSquare.setChecked(checked, false);
        }
        this.valueTextView.setText(value);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setNeedDivider(boolean needDivider) {
        this.needDivider = needDivider;
    }

    public void setMultiline(boolean value) {
        this.isMultiline = value;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.textView.getLayoutParams();
        FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) this.checkBox.getLayoutParams();
        if (this.isMultiline) {
            this.textView.setLines(0);
            this.textView.setMaxLines(0);
            this.textView.setSingleLine(false);
            this.textView.setEllipsize(null);
            if (this.currentType != 5) {
                this.textView.setPadding(0, 0, 0, AndroidUtilities.dp(5.0f));
                layoutParams.height = -2;
                layoutParams.topMargin = AndroidUtilities.dp(10.0f);
                layoutParams1.topMargin = AndroidUtilities.dp(12.0f);
            }
        } else {
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setPadding(0, 0, 0, 0);
            layoutParams.height = -1;
            layoutParams.topMargin = 0;
            layoutParams1.topMargin = AndroidUtilities.dp(15.0f);
        }
        this.textView.setLayoutParams(layoutParams);
        this.checkBox.setLayoutParams(layoutParams1);
    }

    @Override // android.view.View
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        float f = 1.0f;
        this.textView.setAlpha(enabled ? 1.0f : 0.5f);
        this.valueTextView.setAlpha(enabled ? 1.0f : 0.5f);
        View view = this.checkBox;
        if (!enabled) {
            f = 0.5f;
        }
        view.setAlpha(f);
    }

    public void setChecked(boolean checked, boolean animated) {
        CheckBox2 checkBox2 = this.checkBoxRound;
        if (checkBox2 != null) {
            checkBox2.setChecked(checked, animated);
        } else {
            this.checkBoxSquare.setChecked(checked, animated);
        }
    }

    public boolean isChecked() {
        CheckBox2 checkBox2 = this.checkBoxRound;
        if (checkBox2 != null) {
            return checkBox2.isChecked();
        }
        return this.checkBoxSquare.isChecked();
    }

    public TextView getTextView() {
        return this.textView;
    }

    public TextView getValueTextView() {
        return this.valueTextView;
    }

    public View getCheckBoxView() {
        return this.checkBox;
    }

    public void setCheckBoxColor(String background, String background1, String check) {
        CheckBox2 checkBox2 = this.checkBoxRound;
        if (checkBox2 != null) {
            checkBox2.setColor(background, background, check);
        }
    }

    public void setSquareCheckBoxColor(String uncheckedColor, String checkedColor, String checkColor) {
        CheckBoxSquare checkBoxSquare = this.checkBoxSquare;
        if (checkBoxSquare != null) {
            checkBoxSquare.setColors(uncheckedColor, checkedColor, checkColor);
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            int offset = this.currentType == 4 ? 50 : 20;
            canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(offset), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(offset) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName("android.widget.CheckBox");
        info.setCheckable(true);
        info.setChecked(isChecked());
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
