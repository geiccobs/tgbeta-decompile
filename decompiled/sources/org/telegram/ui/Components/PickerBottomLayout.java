package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class PickerBottomLayout extends FrameLayout {
    public TextView cancelButton;
    public LinearLayout doneButton;
    public TextView doneButtonBadgeTextView;
    public TextView doneButtonTextView;
    private Theme.ResourcesProvider resourcesProvider;

    public PickerBottomLayout(Context context) {
        this(context, true, null);
    }

    public PickerBottomLayout(Context context, boolean darkTheme) {
        this(context, darkTheme, null);
    }

    public PickerBottomLayout(Context context, boolean darkTheme, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        setBackgroundColor(Theme.getColor(darkTheme ? Theme.key_dialogBackground : Theme.key_windowBackgroundWhite, resourcesProvider));
        TextView textView = new TextView(context);
        this.cancelButton = textView;
        textView.setTextSize(1, 14.0f);
        this.cancelButton.setTextColor(Theme.getColor(Theme.key_picker_enabledButton, resourcesProvider));
        this.cancelButton.setGravity(17);
        this.cancelButton.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_picker_enabledButton, resourcesProvider) & 268435455, 0));
        this.cancelButton.setPadding(AndroidUtilities.dp(33.0f), 0, AndroidUtilities.dp(33.0f), 0);
        this.cancelButton.setText(LocaleController.getString("Cancel", R.string.Cancel).toUpperCase());
        this.cancelButton.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        addView(this.cancelButton, LayoutHelper.createFrame(-2, -1, 51));
        LinearLayout linearLayout = new LinearLayout(context);
        this.doneButton = linearLayout;
        linearLayout.setOrientation(0);
        this.doneButton.setBackground(Theme.createSelectorDrawable(268435455 & Theme.getColor(Theme.key_picker_enabledButton, resourcesProvider), 0));
        this.doneButton.setPadding(AndroidUtilities.dp(33.0f), 0, AndroidUtilities.dp(33.0f), 0);
        addView(this.doneButton, LayoutHelper.createFrame(-2, -1, 53));
        TextView textView2 = new TextView(context);
        this.doneButtonBadgeTextView = textView2;
        textView2.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.doneButtonBadgeTextView.setTextSize(1, 13.0f);
        this.doneButtonBadgeTextView.setTextColor(Theme.getColor(Theme.key_picker_badgeText, resourcesProvider));
        this.doneButtonBadgeTextView.setGravity(17);
        Drawable drawable = Theme.createRoundRectDrawable(AndroidUtilities.dp(11.0f), Theme.getColor(Theme.key_picker_badge, resourcesProvider));
        this.doneButtonBadgeTextView.setBackgroundDrawable(drawable);
        this.doneButtonBadgeTextView.setMinWidth(AndroidUtilities.dp(23.0f));
        this.doneButtonBadgeTextView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(1.0f));
        this.doneButton.addView(this.doneButtonBadgeTextView, LayoutHelper.createLinear(-2, 23, 16, 0, 0, 10, 0));
        TextView textView3 = new TextView(context);
        this.doneButtonTextView = textView3;
        textView3.setTextSize(1, 14.0f);
        this.doneButtonTextView.setTextColor(Theme.getColor(Theme.key_picker_enabledButton, resourcesProvider));
        this.doneButtonTextView.setGravity(17);
        this.doneButtonTextView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
        this.doneButtonTextView.setText(LocaleController.getString("Send", R.string.Send).toUpperCase());
        this.doneButtonTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.doneButton.addView(this.doneButtonTextView, LayoutHelper.createLinear(-2, -2, 16));
    }

    public void updateSelectedCount(int count, boolean disable) {
        if (count != 0) {
            this.doneButtonBadgeTextView.setVisibility(0);
            this.doneButtonBadgeTextView.setText(String.format("%d", Integer.valueOf(count)));
            this.doneButtonTextView.setTag(Theme.key_picker_enabledButton);
            this.doneButtonTextView.setTextColor(Theme.getColor(Theme.key_picker_enabledButton, this.resourcesProvider));
            if (disable) {
                this.doneButton.setEnabled(true);
                return;
            }
            return;
        }
        this.doneButtonBadgeTextView.setVisibility(8);
        if (disable) {
            this.doneButtonTextView.setTag(Theme.key_picker_disabledButton);
            this.doneButtonTextView.setTextColor(Theme.getColor(Theme.key_picker_disabledButton, this.resourcesProvider));
            this.doneButton.setEnabled(false);
            return;
        }
        this.doneButtonTextView.setTag(Theme.key_picker_enabledButton);
        this.doneButtonTextView.setTextColor(Theme.getColor(Theme.key_picker_enabledButton, this.resourcesProvider));
    }
}
