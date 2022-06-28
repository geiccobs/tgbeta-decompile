package org.telegram.ui;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
/* loaded from: classes4.dex */
public class SponsoredMessageInfoView extends FrameLayout {
    LinearLayout linearLayout;

    public SponsoredMessageInfoView(final Activity context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        TextView textView = new TextView(context);
        textView.setText(LocaleController.getString("SponsoredMessageInfo", R.string.SponsoredMessageInfo));
        textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        textView.setTextSize(1, 20.0f);
        TextView description1 = new TextView(context);
        description1.setText(LocaleController.getString("SponsoredMessageInfoDescription1", R.string.SponsoredMessageInfoDescription1));
        description1.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        description1.setTextSize(1, 14.0f);
        description1.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
        TextView description2 = new TextView(context);
        description2.setText(LocaleController.getString("SponsoredMessageInfoDescription2", R.string.SponsoredMessageInfoDescription2));
        description2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        description2.setTextSize(1, 14.0f);
        description2.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
        TextView description3 = new TextView(context);
        description3.setText(LocaleController.getString("SponsoredMessageInfoDescription3", R.string.SponsoredMessageInfoDescription3));
        description3.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        description3.setTextSize(1, 14.0f);
        description3.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
        final Paint buttonPaint = new Paint(1);
        buttonPaint.setStyle(Paint.Style.STROKE);
        buttonPaint.setColor(Theme.getColor(Theme.key_featuredStickers_addButton, resourcesProvider));
        buttonPaint.setStrokeWidth(AndroidUtilities.dp(1.0f));
        TextView button = new TextView(context) { // from class: org.telegram.ui.SponsoredMessageInfoView.1
            @Override // android.widget.TextView, android.view.View
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                AndroidUtilities.rectTmp.set(AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), getMeasuredWidth() - AndroidUtilities.dp(1.0f), getMeasuredHeight() - AndroidUtilities.dp(1.0f));
                canvas.drawRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), buttonPaint);
            }
        };
        button.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.SponsoredMessageInfoView.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Browser.openUrl(context, LocaleController.getString("SponsoredMessageAlertLearnMoreUrl", R.string.SponsoredMessageAlertLearnMoreUrl));
            }
        });
        button.setPadding(AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f), 0);
        button.setText(LocaleController.getString("SponsoredMessageAlertLearnMoreUrl", R.string.SponsoredMessageAlertLearnMoreUrl));
        button.setTextColor(Theme.getColor(Theme.key_featuredStickers_addButton, resourcesProvider));
        button.setBackground(Theme.AdaptiveRipple.filledRect(Theme.getColor(Theme.key_dialogBackground, resourcesProvider), 4.0f));
        button.setTextSize(1, 14.0f);
        button.setGravity(16);
        TextView description4 = new TextView(context);
        description4.setText(LocaleController.getString("SponsoredMessageInfoDescription4", R.string.SponsoredMessageInfoDescription4));
        description4.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
        description4.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        description4.setTextSize(1, 14.0f);
        linearLayout.addView(textView);
        linearLayout.addView(description1, LayoutHelper.createLinear(-1, -2, 0, 0, 18, 0, 0));
        linearLayout.addView(description2, LayoutHelper.createLinear(-1, -2, 0, 0, 24, 0, 0));
        linearLayout.addView(description3, LayoutHelper.createLinear(-1, -2, 0, 0, 24, 0, 0));
        linearLayout.addView(button, LayoutHelper.createLinear(-2, 34, 1, 0, 14, 0, 0));
        linearLayout.addView(description4, LayoutHelper.createLinear(-1, -2, 0, 0, 14, 0, 0));
        ScrollView scrollView = new ScrollView(getContext());
        scrollView.addView(linearLayout);
        addView(scrollView, LayoutHelper.createFrame(-1, -2.0f, 0, 22.0f, 12.0f, 22.0f, 22.0f));
    }
}
