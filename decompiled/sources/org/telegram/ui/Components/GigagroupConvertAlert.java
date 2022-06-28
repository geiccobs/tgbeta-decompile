package org.telegram.ui.Components;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class GigagroupConvertAlert extends BottomSheet {

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

    public GigagroupConvertAlert(final Context context, final BaseFragment parentFragment) {
        super(context, true);
        setApplyBottomPadding(false);
        setApplyTopPadding(false);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        setCustomView(linearLayout);
        RLottieImageView lottieImageView = new RLottieImageView(context);
        lottieImageView.setAutoRepeat(true);
        lottieImageView.setAnimation(R.raw.utyan_gigagroup, 120, 120);
        lottieImageView.playAnimation();
        linearLayout.addView(lottieImageView, LayoutHelper.createLinear(160, 160, 49, 17, 30, 17, 0));
        TextView percentTextView = new TextView(context);
        percentTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        percentTextView.setTextSize(1, 24.0f);
        percentTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        percentTextView.setText(LocaleController.getString("GigagroupConvertTitle", R.string.GigagroupConvertTitle));
        linearLayout.addView(percentTextView, LayoutHelper.createLinear(-2, -2, 49, 17, 18, 17, 0));
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(1);
        linearLayout.addView(container, LayoutHelper.createLinear(-2, -2, 1, 0, 12, 0, 0));
        int a = 0;
        while (true) {
            int i = 3;
            if (a < 3) {
                LinearLayout linearLayout2 = new LinearLayout(context);
                linearLayout2.setOrientation(0);
                container.addView(linearLayout2, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 8, 0, 0));
                ImageView imageView = new ImageView(context);
                imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogTextGray3), PorterDuff.Mode.MULTIPLY));
                imageView.setImageResource(R.drawable.list_circle);
                TextView textView = new TextView(context);
                textView.setTextSize(1, 15.0f);
                textView.setTextColor(Theme.getColor(Theme.key_dialogTextGray3));
                textView.setGravity((LocaleController.isRTL ? 5 : i) | 16);
                textView.setMaxWidth(AndroidUtilities.dp(260.0f));
                switch (a) {
                    case 0:
                        textView.setText(LocaleController.getString("GigagroupConvertInfo1", R.string.GigagroupConvertInfo1));
                        break;
                    case 1:
                        textView.setText(LocaleController.getString("GigagroupConvertInfo2", R.string.GigagroupConvertInfo2));
                        break;
                    case 2:
                        textView.setText(LocaleController.getString("GigagroupConvertInfo3", R.string.GigagroupConvertInfo3));
                        break;
                }
                if (LocaleController.isRTL) {
                    linearLayout2.addView(textView, LayoutHelper.createLinear(-2, -2));
                    linearLayout2.addView(imageView, LayoutHelper.createLinear(-2, -2, 8.0f, 7.0f, 0.0f, 0.0f));
                } else {
                    linearLayout2.addView(imageView, LayoutHelper.createLinear(-2, -2, 0.0f, 8.0f, 8.0f, 0.0f));
                    linearLayout2.addView(textView, LayoutHelper.createLinear(-2, -2));
                }
                a++;
            } else {
                BottomSheetCell clearButton = new BottomSheetCell(context);
                clearButton.setBackground(null);
                clearButton.setText(LocaleController.getString("GigagroupConvertProcessButton", R.string.GigagroupConvertProcessButton));
                clearButton.background.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.GigagroupConvertAlert$$ExternalSyntheticLambda2
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        GigagroupConvertAlert.this.m2652lambda$new$1$orgtelegramuiComponentsGigagroupConvertAlert(context, parentFragment, view);
                    }
                });
                linearLayout.addView(clearButton, LayoutHelper.createLinear(-1, 50, 51, 0, 29, 0, 0));
                TextView cancelTextView = new TextView(context);
                cancelTextView.setTextSize(1, 14.0f);
                cancelTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
                cancelTextView.setText(LocaleController.getString("GigagroupConvertCancelButton", R.string.GigagroupConvertCancelButton));
                cancelTextView.setGravity(17);
                linearLayout.addView(cancelTextView, LayoutHelper.createLinear(-2, 48, 49, 17, 0, 17, 16));
                cancelTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.GigagroupConvertAlert$$ExternalSyntheticLambda1
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        GigagroupConvertAlert.this.m2653lambda$new$2$orgtelegramuiComponentsGigagroupConvertAlert(view);
                    }
                });
                return;
            }
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-GigagroupConvertAlert */
    public /* synthetic */ void m2652lambda$new$1$orgtelegramuiComponentsGigagroupConvertAlert(Context context, BaseFragment parentFragment, View v) {
        dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString("GigagroupConvertAlertTitle", R.string.GigagroupConvertAlertTitle));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("GigagroupConvertAlertText", R.string.GigagroupConvertAlertText)));
        builder.setPositiveButton(LocaleController.getString("GigagroupConvertAlertConver", R.string.GigagroupConvertAlertConver), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.GigagroupConvertAlert$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                GigagroupConvertAlert.this.m2651lambda$new$0$orgtelegramuiComponentsGigagroupConvertAlert(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        parentFragment.showDialog(builder.create());
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-GigagroupConvertAlert */
    public /* synthetic */ void m2651lambda$new$0$orgtelegramuiComponentsGigagroupConvertAlert(DialogInterface dialogInterface, int i) {
        onCovert();
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-GigagroupConvertAlert */
    public /* synthetic */ void m2653lambda$new$2$orgtelegramuiComponentsGigagroupConvertAlert(View v) {
        onCancel();
        dismiss();
    }

    protected void onCovert() {
    }

    protected void onCancel() {
    }
}
