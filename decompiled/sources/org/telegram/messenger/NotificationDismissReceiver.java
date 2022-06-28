package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
/* loaded from: classes4.dex */
public class NotificationDismissReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        int currentAccount = intent.getIntExtra("currentAccount", UserConfig.selectedAccount);
        if (!UserConfig.isValidAccount(currentAccount)) {
            return;
        }
        long dialogId = intent.getLongExtra("dialogId", 0L);
        int date = intent.getIntExtra("messageDate", 0);
        if (dialogId == 0) {
            MessagesController.getNotificationsSettings(currentAccount).edit().putInt("dismissDate", date).commit();
            return;
        }
        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(currentAccount).edit();
        edit.putInt("dismissDate" + dialogId, date).commit();
    }
}
