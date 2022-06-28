package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadioButton;
/* loaded from: classes4.dex */
public class LanguageCell extends FrameLayout {
    private LocaleController.LocaleInfo currentLocale;
    private boolean needDivider;
    private RadioButton radioButton;
    private TextView textView;
    private TextView textView2;
    private int marginStartDp = 62;
    private int marginEndDp = 23;

    public LanguageCell(Context context) {
        super(context);
        if (Theme.dividerPaint == null) {
            Theme.createCommonResources(context);
        }
        int i = 0;
        setWillNotDraw(false);
        RadioButton radioButton = new RadioButton(context);
        this.radioButton = radioButton;
        radioButton.setSize(AndroidUtilities.dp(20.0f));
        this.radioButton.setColor(Theme.getColor(Theme.key_dialogRadioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
        int i2 = 5;
        addView(this.radioButton, LayoutHelper.createFrame(22, 22.0f, (LocaleController.isRTL ? 5 : 3) | 16, LocaleController.isRTL ? 0 : 20, 0.0f, LocaleController.isRTL ? 20 : i, 0.0f));
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? this.marginEndDp : this.marginStartDp, 0.0f, LocaleController.isRTL ? this.marginStartDp : this.marginEndDp, 17.0f));
        TextView textView2 = new TextView(context);
        this.textView2 = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_dialogTextGray3));
        this.textView2.setTextSize(1, 13.0f);
        this.textView2.setSingleLine(true);
        this.textView2.setEllipsize(TextUtils.TruncateAt.END);
        this.textView2.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.textView2, LayoutHelper.createFrame(-1, -1.0f, (!LocaleController.isRTL ? 3 : i2) | 48, LocaleController.isRTL ? this.marginEndDp : this.marginStartDp, 20.0f, LocaleController.isRTL ? this.marginStartDp : this.marginEndDp, 0.0f));
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f) + (this.needDivider ? 1 : 0), C.BUFFER_FLAG_ENCRYPTED));
    }

    public void setLanguage(LocaleController.LocaleInfo language, String desc, boolean divider) {
        this.textView.setText(desc != null ? desc : language.name);
        this.textView2.setText(language.nameEnglish);
        this.currentLocale = language;
        this.needDivider = divider;
    }

    public void setValue(String name, String nameEnglish) {
        this.textView.setText(name);
        this.textView2.setText(nameEnglish);
        this.radioButton.setChecked(false, false);
        this.currentLocale = null;
        this.needDivider = false;
    }

    public LocaleController.LocaleInfo getCurrentLocale() {
        return this.currentLocale;
    }

    public void setLanguageSelected(boolean value, boolean animated) {
        this.radioButton.setChecked(value, animated);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(this.marginStartDp - 3), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(this.marginStartDp - 3) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
        }
    }
}
