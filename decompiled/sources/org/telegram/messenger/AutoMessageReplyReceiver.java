package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.core.app.RemoteInput;
/* loaded from: classes4.dex */
public class AutoMessageReplyReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        ApplicationLoader.postInitApplication();
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput == null) {
            return;
        }
        CharSequence text = remoteInput.getCharSequence(NotificationsController.EXTRA_VOICE_REPLY);
        if (!TextUtils.isEmpty(text)) {
            long dialogId = intent.getLongExtra("dialog_id", 0L);
            int maxId = intent.getIntExtra("max_id", 0);
            int currentAccount = intent.getIntExtra("currentAccount", 0);
            if (dialogId == 0 || maxId == 0 || !UserConfig.isValidAccount(currentAccount)) {
                return;
            }
            SendMessagesHelper.getInstance(currentAccount).sendMessage(text.toString(), dialogId, null, null, null, true, null, null, null, true, 0, null);
            MessagesController.getInstance(currentAccount).markDialogAsRead(dialogId, maxId, maxId, 0, false, 0, 0, true, 0);
        }
    }
}
