package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.GravityCompat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.StickerImageView;
/* loaded from: classes4.dex */
public class SuggestClearDatabaseBottomSheet extends BottomSheet {
    private static SuggestClearDatabaseBottomSheet dialog;
    BaseFragment fragment;

    public static void show(BaseFragment fragment) {
        if (dialog == null) {
            SuggestClearDatabaseBottomSheet suggestClearDatabaseBottomSheet = new SuggestClearDatabaseBottomSheet(fragment);
            dialog = suggestClearDatabaseBottomSheet;
            suggestClearDatabaseBottomSheet.show();
        }
    }

    private SuggestClearDatabaseBottomSheet(final BaseFragment fragment) {
        super(fragment.getParentActivity(), false);
        this.fragment = fragment;
        Context context = fragment.getParentActivity();
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        StickerImageView imageView = new StickerImageView(context, this.currentAccount);
        imageView.setStickerNum(7);
        imageView.getImageReceiver().setAutoRepeat(1);
        linearLayout.addView(imageView, LayoutHelper.createLinear(144, 144, 1, 0, 16, 0, 0));
        TextView title = new TextView(context);
        title.setGravity(GravityCompat.START);
        title.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        title.setTextSize(1, 20.0f);
        title.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        title.setText(LocaleController.getString("SuggestClearDatabaseTitle", R.string.SuggestClearDatabaseTitle));
        linearLayout.addView(title, LayoutHelper.createFrame(-1, -2.0f, 0, 21.0f, 30.0f, 21.0f, 0.0f));
        TextView description = new TextView(context);
        description.setGravity(GravityCompat.START);
        description.setTextSize(1, 15.0f);
        description.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        description.setText(AndroidUtilities.replaceTags(LocaleController.formatString("SuggestClearDatabaseMessage", R.string.SuggestClearDatabaseMessage, AndroidUtilities.formatFileSize(fragment.getMessagesStorage().getDatabaseSize()))));
        linearLayout.addView(description, LayoutHelper.createFrame(-1, -2.0f, 0, 21.0f, 15.0f, 21.0f, 16.0f));
        TextView buttonTextView = new TextView(context);
        buttonTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        buttonTextView.setGravity(17);
        buttonTextView.setTextSize(1, 14.0f);
        buttonTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        buttonTextView.setText(LocaleController.getString("ClearLocalDatabase", R.string.ClearLocalDatabase));
        buttonTextView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        buttonTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor(Theme.key_featuredStickers_addButton), ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_windowBackgroundWhite), 120)));
        linearLayout.addView(buttonTextView, LayoutHelper.createFrame(-1, 48.0f, 0, 16.0f, 15.0f, 16.0f, 16.0f));
        buttonTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.SuggestClearDatabaseBottomSheet$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SuggestClearDatabaseBottomSheet.this.m4613lambda$new$1$orgtelegramuiSuggestClearDatabaseBottomSheet(fragment, view);
            }
        });
        ScrollView scrollView = new ScrollView(context);
        scrollView.addView(linearLayout);
        setCustomView(scrollView);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-SuggestClearDatabaseBottomSheet */
    public /* synthetic */ void m4613lambda$new$1$orgtelegramuiSuggestClearDatabaseBottomSheet(final BaseFragment fragment, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getParentActivity());
        builder.setTitle(LocaleController.getString("LocalDatabaseClearTextTitle", R.string.LocalDatabaseClearTextTitle));
        builder.setMessage(LocaleController.getString("LocalDatabaseClearText", R.string.LocalDatabaseClearText));
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.setPositiveButton(LocaleController.getString("CacheClear", R.string.CacheClear), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.SuggestClearDatabaseBottomSheet$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                SuggestClearDatabaseBottomSheet.this.m4612lambda$new$0$orgtelegramuiSuggestClearDatabaseBottomSheet(fragment, dialogInterface, i);
            }
        });
        AlertDialog alertDialog = builder.create();
        fragment.showDialog(alertDialog);
        TextView button = (TextView) alertDialog.getButton(-1);
        if (button != null) {
            button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-SuggestClearDatabaseBottomSheet */
    public /* synthetic */ void m4612lambda$new$0$orgtelegramuiSuggestClearDatabaseBottomSheet(BaseFragment fragment, DialogInterface dialogInterface, int i) {
        if (fragment.getParentActivity() == null) {
            return;
        }
        MessagesController.getInstance(this.currentAccount).clearQueryTime();
        fragment.getMessagesStorage().clearLocalDatabase();
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        super.dismiss();
        dialog = null;
    }

    public static void dismissDialog() {
        SuggestClearDatabaseBottomSheet suggestClearDatabaseBottomSheet = dialog;
        if (suggestClearDatabaseBottomSheet != null) {
            suggestClearDatabaseBottomSheet.dismiss();
            dialog = null;
        }
    }
}
