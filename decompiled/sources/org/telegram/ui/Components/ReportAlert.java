package org.telegram.ui.Components;

import android.content.Context;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class ReportAlert extends BottomSheet {
    private BottomSheetCell clearButton;
    private EditTextBoldCursor editText;

    /* loaded from: classes5.dex */
    public static class BottomSheetCell extends FrameLayout {
        private View background;
        private LinearLayout linearLayout;
        private TextView textView;

        public BottomSheetCell(Context context) {
            super(context);
            View view = new View(context);
            this.background = view;
            view.setBackground(Theme.AdaptiveRipple.filledRect(Theme.key_featuredStickers_addButton, 4.0f));
            addView(this.background, LayoutHelper.createFrame(-1, -1.0f, 0, 16.0f, 16.0f, 16.0f, 16.0f));
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setGravity(17);
            this.textView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
            this.textView.setTextSize(1, 14.0f);
            this.textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            addView(this.textView, LayoutHelper.createFrame(-2, -2, 17));
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), C.BUFFER_FLAG_ENCRYPTED));
        }

        public void setText(CharSequence text) {
            this.textView.setText(text);
        }
    }

    public ReportAlert(Context context, final int type) {
        super(context, true);
        setApplyBottomPadding(false);
        setApplyTopPadding(false);
        ScrollView scrollView = new ScrollView(context);
        scrollView.setFillViewport(true);
        setCustomView(scrollView);
        FrameLayout frameLayout = new FrameLayout(context);
        scrollView.addView(frameLayout, LayoutHelper.createScroll(-1, -2, 51));
        RLottieImageView imageView = new RLottieImageView(context);
        imageView.setAnimation(R.raw.report_police, 120, 120);
        imageView.playAnimation();
        frameLayout.addView(imageView, LayoutHelper.createFrame(160, 160.0f, 49, 17.0f, 14.0f, 17.0f, 0.0f));
        TextView percentTextView = new TextView(context);
        percentTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        percentTextView.setTextSize(1, 24.0f);
        percentTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        if (type == 0) {
            percentTextView.setText(LocaleController.getString("ReportTitleSpam", R.string.ReportTitleSpam));
        } else if (type == 6) {
            percentTextView.setText(LocaleController.getString("ReportTitleFake", R.string.ReportTitleFake));
        } else if (type == 1) {
            percentTextView.setText(LocaleController.getString("ReportTitleViolence", R.string.ReportTitleViolence));
        } else if (type == 2) {
            percentTextView.setText(LocaleController.getString("ReportTitleChild", R.string.ReportTitleChild));
        } else if (type == 5) {
            percentTextView.setText(LocaleController.getString("ReportTitlePornography", R.string.ReportTitlePornography));
        } else if (type == 100) {
            percentTextView.setText(LocaleController.getString("ReportChat", R.string.ReportChat));
        }
        frameLayout.addView(percentTextView, LayoutHelper.createFrame(-2, -2.0f, 49, 17.0f, 197.0f, 17.0f, 0.0f));
        TextView infoTextView = new TextView(context);
        infoTextView.setTextSize(1, 14.0f);
        infoTextView.setTextColor(Theme.getColor(Theme.key_dialogTextGray3));
        infoTextView.setGravity(1);
        infoTextView.setText(LocaleController.getString("ReportInfo", R.string.ReportInfo));
        frameLayout.addView(infoTextView, LayoutHelper.createFrame(-2, -2.0f, 49, 30.0f, 235.0f, 30.0f, 44.0f));
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context);
        this.editText = editTextBoldCursor;
        editTextBoldCursor.setTextSize(1, 18.0f);
        this.editText.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.editText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.editText.setBackgroundDrawable(null);
        this.editText.setLineColors(getThemedColor(Theme.key_windowBackgroundWhiteInputField), getThemedColor(Theme.key_windowBackgroundWhiteInputFieldActivated), getThemedColor(Theme.key_windowBackgroundWhiteRedText3));
        this.editText.setMaxLines(1);
        this.editText.setLines(1);
        this.editText.setPadding(0, 0, 0, 0);
        this.editText.setSingleLine(true);
        this.editText.setGravity(LocaleController.isRTL ? 5 : 3);
        this.editText.setInputType(180224);
        this.editText.setImeOptions(6);
        this.editText.setHint(LocaleController.getString("ReportHint", R.string.ReportHint));
        this.editText.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.editText.setCursorSize(AndroidUtilities.dp(20.0f));
        this.editText.setCursorWidth(1.5f);
        this.editText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.Components.ReportAlert$$ExternalSyntheticLambda1
            @Override // android.widget.TextView.OnEditorActionListener
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return ReportAlert.this.m2959lambda$new$0$orgtelegramuiComponentsReportAlert(textView, i, keyEvent);
            }
        });
        frameLayout.addView(this.editText, LayoutHelper.createFrame(-1, 36.0f, 51, 17.0f, 305.0f, 17.0f, 0.0f));
        BottomSheetCell bottomSheetCell = new BottomSheetCell(context);
        this.clearButton = bottomSheetCell;
        bottomSheetCell.setBackground(null);
        this.clearButton.setText(LocaleController.getString("ReportSend", R.string.ReportSend));
        this.clearButton.background.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ReportAlert$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ReportAlert.this.m2960lambda$new$1$orgtelegramuiComponentsReportAlert(type, view);
            }
        });
        frameLayout.addView(this.clearButton, LayoutHelper.createFrame(-1, 50.0f, 51, 0.0f, 357.0f, 0.0f, 0.0f));
        this.smoothKeyboardAnimationEnabled = true;
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-ReportAlert */
    public /* synthetic */ boolean m2959lambda$new$0$orgtelegramuiComponentsReportAlert(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 6) {
            this.clearButton.background.callOnClick();
            return true;
        }
        return false;
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-ReportAlert */
    public /* synthetic */ void m2960lambda$new$1$orgtelegramuiComponentsReportAlert(int type, View v) {
        AndroidUtilities.hideKeyboard(this.editText);
        onSend(type, this.editText.getText().toString());
        dismiss();
    }

    protected void onSend(int type, String message) {
    }
}
