package org.telegram.ui.Components.Premium;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.view.GravityCompat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
/* loaded from: classes5.dex */
public class PremiumNotAvailableBottomSheet extends BottomSheet {
    public PremiumNotAvailableBottomSheet(BaseFragment fragment) {
        super(fragment.getParentActivity(), false);
        Context context = fragment.getParentActivity();
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        TextView title = new TextView(context);
        title.setGravity(GravityCompat.START);
        title.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        title.setTextSize(1, 20.0f);
        title.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        linearLayout.addView(title, LayoutHelper.createFrame(-1, -2.0f, 0, 21.0f, 16.0f, 21.0f, 0.0f));
        TextView description = new TextView(context);
        description.setGravity(GravityCompat.START);
        description.setTextSize(1, 16.0f);
        description.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        linearLayout.addView(description, LayoutHelper.createFrame(-1, -2.0f, 0, 21.0f, 15.0f, 21.0f, 16.0f));
        TextView buttonTextView = new TextView(context);
        buttonTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        buttonTextView.setGravity(17);
        buttonTextView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        buttonTextView.setTextSize(1, 14.0f);
        buttonTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        buttonTextView.setBackground(Theme.AdaptiveRipple.filledRect(Theme.key_featuredStickers_addButton, 8.0f));
        buttonTextView.setText(LocaleController.getString((int) R.string.InstallOfficialApp));
        buttonTextView.setOnClickListener(PremiumNotAvailableBottomSheet$$ExternalSyntheticLambda0.INSTANCE);
        FrameLayout buttonContainer = new FrameLayout(context);
        buttonContainer.addView(buttonTextView, LayoutHelper.createFrame(-1, 48.0f, 16, 16.0f, 0.0f, 16.0f, 0.0f));
        buttonContainer.setBackgroundColor(getThemedColor(Theme.key_dialogBackground));
        linearLayout.addView(buttonContainer, LayoutHelper.createLinear(-1, 68, 80));
        title.setText(AndroidUtilities.replaceTags(LocaleController.getString((int) R.string.SubscribeToPremiumOfficialAppNeeded)));
        description.setText(AndroidUtilities.replaceTags(LocaleController.getString((int) R.string.SubscribeToPremiumOfficialAppNeededDescription)));
        ScrollView scrollView = new ScrollView(context);
        scrollView.addView(linearLayout);
        setCustomView(scrollView);
    }

    public static /* synthetic */ void lambda$new$0(View v) {
        try {
            v.getContext().startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=org.telegram.messenger")));
        } catch (ActivityNotFoundException e) {
            FileLog.e(e);
        }
    }
}
