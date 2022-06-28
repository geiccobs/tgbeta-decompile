package org.telegram.ui;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumButtonView;
/* loaded from: classes4.dex */
public class UnlockPremiumView extends FrameLayout {
    public static final int TYPE_REACTIONS = 1;
    public static final int TYPE_STICKERS = 0;
    public final PremiumButtonView premiumButtonView;

    public UnlockPremiumView(Context context, int type, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        String text;
        LinearLayout linearLayout = new LinearLayout(context);
        addView(linearLayout, LayoutHelper.createFrame(-1, -2, 80));
        linearLayout.setOrientation(1);
        TextView descriptionTextView = new TextView(context);
        descriptionTextView.setTextColor(ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider), 100));
        descriptionTextView.setTextSize(1, 13.0f);
        descriptionTextView.setGravity(17);
        if (type == 0) {
            descriptionTextView.setText(LocaleController.getString("UnlockPremiumStickersDescription", R.string.UnlockPremiumStickersDescription));
        } else if (type == 1) {
            descriptionTextView.setText(LocaleController.getString("UnlockPremiumReactionsDescription", R.string.UnlockPremiumReactionsDescription));
        }
        linearLayout.addView(descriptionTextView, LayoutHelper.createLinear(-1, -2, 0, 16, 17, 17, 16));
        PremiumButtonView premiumButtonView = new PremiumButtonView(context, false);
        this.premiumButtonView = premiumButtonView;
        if (type == 0) {
            text = LocaleController.getString("UnlockPremiumStickers", R.string.UnlockPremiumStickers);
        } else {
            text = LocaleController.getString("UnlockPremiumReactions", R.string.UnlockPremiumReactions);
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append((CharSequence) "d ").setSpan(new ColoredImageSpan(ContextCompat.getDrawable(context, R.drawable.msg_premium_normal)), 0, 1, 0);
        spannableStringBuilder.append((CharSequence) text);
        premiumButtonView.buttonTextView.setText(spannableStringBuilder);
        linearLayout.addView(premiumButtonView, LayoutHelper.createLinear(-1, 48, 0, 16, 0, 16, 16));
    }
}
