package org.telegram.ui.Cells;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.URLSpanNoUnderline;
/* loaded from: classes4.dex */
public class SettingsSuggestionCell extends LinearLayout {
    public static final int TYPE_PASSWORD = 1;
    public static final int TYPE_PHONE = 0;
    private int currentAccount = UserConfig.selectedAccount;
    private int currentType;
    private TextView detailTextView;
    private TextView noButton;
    private Theme.ResourcesProvider resourcesProvider;
    private TextView textView;
    private TextView yesButton;

    public SettingsSuggestionCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        setOrientation(1);
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTextSize(1, 15.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader, resourcesProvider));
        addView(this.textView, LayoutHelper.createLinear(-1, -2, (LocaleController.isRTL ? 5 : 3) | 48, 21, 15, 21, 0));
        TextView textView2 = new TextView(context);
        this.detailTextView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2, resourcesProvider));
        this.detailTextView.setTextSize(1, 13.0f);
        this.detailTextView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText, resourcesProvider));
        this.detailTextView.setHighlightColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkSelection, resourcesProvider));
        this.detailTextView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        this.detailTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.detailTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 21, 8, 21, 0));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        addView(linearLayout, LayoutHelper.createLinear(-1, 40, 21.0f, 17.0f, 21.0f, 20.0f));
        int a = 0;
        while (a < 2) {
            TextView textView3 = new TextView(context);
            textView3.setBackground(Theme.AdaptiveRipple.filledRect(Theme.key_featuredStickers_addButton, 4.0f));
            textView3.setLines(1);
            textView3.setSingleLine(true);
            textView3.setGravity(1);
            textView3.setEllipsize(TextUtils.TruncateAt.END);
            textView3.setGravity(17);
            textView3.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText, resourcesProvider));
            textView3.setTextSize(1, 14.0f);
            textView3.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            linearLayout.addView(textView3, LayoutHelper.createLinear(0, 40, 0.5f, a == 0 ? 0 : 4, 0, a == 0 ? 4 : 0, 0));
            if (a == 0) {
                this.yesButton = textView3;
                textView3.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Cells.SettingsSuggestionCell$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        SettingsSuggestionCell.this.m1665lambda$new$0$orgtelegramuiCellsSettingsSuggestionCell(view);
                    }
                });
            } else {
                this.noButton = textView3;
                textView3.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Cells.SettingsSuggestionCell$$ExternalSyntheticLambda1
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        SettingsSuggestionCell.this.m1666lambda$new$1$orgtelegramuiCellsSettingsSuggestionCell(view);
                    }
                });
            }
            a++;
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Cells-SettingsSuggestionCell */
    public /* synthetic */ void m1665lambda$new$0$orgtelegramuiCellsSettingsSuggestionCell(View v) {
        onYesClick(this.currentType);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Cells-SettingsSuggestionCell */
    public /* synthetic */ void m1666lambda$new$1$orgtelegramuiCellsSettingsSuggestionCell(View v) {
        onNoClick(this.currentType);
    }

    public void setType(int type) {
        this.currentType = type;
        if (type == 0) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(UserConfig.getInstance(this.currentAccount).clientUserId));
            TextView textView = this.textView;
            PhoneFormat phoneFormat = PhoneFormat.getInstance();
            textView.setText(LocaleController.formatString("CheckPhoneNumber", R.string.CheckPhoneNumber, phoneFormat.format("+" + user.phone)));
            String text = LocaleController.getString("CheckPhoneNumberInfo", R.string.CheckPhoneNumberInfo);
            SpannableStringBuilder builder = new SpannableStringBuilder(text);
            int index1 = text.indexOf("**");
            int index2 = text.lastIndexOf("**");
            if (index1 >= 0 && index2 >= 0 && index1 != index2) {
                builder.replace(index2, index2 + 2, (CharSequence) "");
                builder.replace(index1, index1 + 2, (CharSequence) "");
                try {
                    builder.setSpan(new URLSpanNoUnderline(LocaleController.getString("CheckPhoneNumberLearnMoreUrl", R.string.CheckPhoneNumberLearnMoreUrl)), index1, index2 - 2, 33);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            this.detailTextView.setText(builder);
            this.yesButton.setText(LocaleController.getString("CheckPhoneNumberYes", R.string.CheckPhoneNumberYes));
            this.noButton.setText(LocaleController.getString("CheckPhoneNumberNo", R.string.CheckPhoneNumberNo));
        } else if (type == 1) {
            this.textView.setText(LocaleController.getString("YourPasswordHeader", R.string.YourPasswordHeader));
            this.detailTextView.setText(LocaleController.getString("YourPasswordRemember", R.string.YourPasswordRemember));
            this.yesButton.setText(LocaleController.getString("YourPasswordRememberYes", R.string.YourPasswordRememberYes));
            this.noButton.setText(LocaleController.getString("YourPasswordRememberNo", R.string.YourPasswordRememberNo));
        }
    }

    protected void onYesClick(int type) {
    }

    protected void onNoClick(int type) {
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), heightMeasureSpec);
    }
}
