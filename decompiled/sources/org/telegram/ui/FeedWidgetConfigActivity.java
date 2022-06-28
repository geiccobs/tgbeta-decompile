package org.telegram.ui;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FeedWidgetProvider;
import org.telegram.ui.DialogsActivity;
/* loaded from: classes4.dex */
public class FeedWidgetConfigActivity extends ExternalActionActivity {
    private int creatingAppWidgetId = 0;

    @Override // org.telegram.ui.ExternalActionActivity
    protected boolean handleIntent(Intent intent, boolean isNew, boolean restore, boolean fromPassword, int intentAccount, int state) {
        if (!checkPasscode(intent, isNew, restore, fromPassword, intentAccount, state)) {
            return false;
        }
        Bundle extras = intent.getExtras();
        if (extras != null) {
            this.creatingAppWidgetId = extras.getInt("appWidgetId", 0);
        }
        if (this.creatingAppWidgetId != 0) {
            Bundle args = new Bundle();
            args.putBoolean("onlySelect", true);
            args.putInt("dialogsType", 5);
            args.putBoolean("allowSwitchAccount", true);
            args.putBoolean("checkCanWrite", false);
            DialogsActivity fragment = new DialogsActivity(args);
            fragment.setDelegate(new DialogsActivity.DialogsActivityDelegate() { // from class: org.telegram.ui.FeedWidgetConfigActivity$$ExternalSyntheticLambda0
                @Override // org.telegram.ui.DialogsActivity.DialogsActivityDelegate
                public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
                    FeedWidgetConfigActivity.this.m3420lambda$handleIntent$0$orgtelegramuiFeedWidgetConfigActivity(dialogsActivity, arrayList, charSequence, z);
                }
            });
            if (AndroidUtilities.isTablet()) {
                if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                    this.layersActionBarLayout.addFragmentToStack(fragment);
                }
            } else if (this.actionBarLayout.fragmentsStack.isEmpty()) {
                this.actionBarLayout.addFragmentToStack(fragment);
            }
            if (!AndroidUtilities.isTablet()) {
                this.backgroundTablet.setVisibility(8);
            }
            this.actionBarLayout.showLastFragment();
            if (AndroidUtilities.isTablet()) {
                this.layersActionBarLayout.showLastFragment();
            }
            intent.setAction(null);
        } else {
            finish();
        }
        return true;
    }

    /* renamed from: lambda$handleIntent$0$org-telegram-ui-FeedWidgetConfigActivity */
    public /* synthetic */ void m3420lambda$handleIntent$0$orgtelegramuiFeedWidgetConfigActivity(DialogsActivity fragment1, ArrayList dids, CharSequence message, boolean param) {
        AccountInstance.getInstance(fragment1.getCurrentAccount()).getMessagesStorage().putWidgetDialogs(this.creatingAppWidgetId, dids);
        SharedPreferences preferences = getSharedPreferences("shortcut_widget", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("account" + this.creatingAppWidgetId, fragment1.getCurrentAccount());
        editor.putLong("dialogId" + this.creatingAppWidgetId, ((Long) dids.get(0)).longValue());
        editor.commit();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        FeedWidgetProvider.updateWidget(this, appWidgetManager, this.creatingAppWidgetId);
        Intent resultValue = new Intent();
        resultValue.putExtra("appWidgetId", this.creatingAppWidgetId);
        setResult(-1, resultValue);
        finish();
    }
}