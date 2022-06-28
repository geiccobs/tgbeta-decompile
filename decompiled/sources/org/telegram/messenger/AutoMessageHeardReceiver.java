package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public class AutoMessageHeardReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        ApplicationLoader.postInitApplication();
        final long dialogId = intent.getLongExtra("dialog_id", 0L);
        final int maxId = intent.getIntExtra("max_id", 0);
        final int currentAccount = intent.getIntExtra("currentAccount", 0);
        if (dialogId == 0 || maxId == 0) {
            return;
        }
        if (!UserConfig.isValidAccount(currentAccount)) {
            return;
        }
        final AccountInstance accountInstance = AccountInstance.getInstance(currentAccount);
        if (DialogObject.isUserDialog(dialogId)) {
            TLRPC.User user = accountInstance.getMessagesController().getUser(Long.valueOf(dialogId));
            if (user == null) {
                Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.AutoMessageHeardReceiver$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        AutoMessageHeardReceiver.lambda$onReceive$1(AccountInstance.this, dialogId, currentAccount, maxId);
                    }
                });
                return;
            }
        } else if (DialogObject.isChatDialog(dialogId)) {
            TLRPC.Chat chat = accountInstance.getMessagesController().getChat(Long.valueOf(-dialogId));
            if (chat == null) {
                Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.AutoMessageHeardReceiver$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        AutoMessageHeardReceiver.lambda$onReceive$3(AccountInstance.this, dialogId, currentAccount, maxId);
                    }
                });
                return;
            }
        }
        MessagesController.getInstance(currentAccount).markDialogAsRead(dialogId, maxId, maxId, 0, false, 0, 0, true, 0);
    }

    public static /* synthetic */ void lambda$onReceive$1(final AccountInstance accountInstance, final long dialogId, final int currentAccount, final int maxId) {
        final TLRPC.User user1 = accountInstance.getMessagesStorage().getUserSync(dialogId);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.AutoMessageHeardReceiver$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                AutoMessageHeardReceiver.lambda$onReceive$0(AccountInstance.this, user1, currentAccount, dialogId, maxId);
            }
        });
    }

    public static /* synthetic */ void lambda$onReceive$0(AccountInstance accountInstance, TLRPC.User user1, int currentAccount, long dialogId, int maxId) {
        accountInstance.getMessagesController().putUser(user1, true);
        MessagesController.getInstance(currentAccount).markDialogAsRead(dialogId, maxId, maxId, 0, false, 0, 0, true, 0);
    }

    public static /* synthetic */ void lambda$onReceive$3(final AccountInstance accountInstance, final long dialogId, final int currentAccount, final int maxId) {
        final TLRPC.Chat chat1 = accountInstance.getMessagesStorage().getChatSync(-dialogId);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.AutoMessageHeardReceiver$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                AutoMessageHeardReceiver.lambda$onReceive$2(AccountInstance.this, chat1, currentAccount, dialogId, maxId);
            }
        });
    }

    public static /* synthetic */ void lambda$onReceive$2(AccountInstance accountInstance, TLRPC.Chat chat1, int currentAccount, long dialogId, int maxId) {
        accountInstance.getMessagesController().putChat(chat1, true);
        MessagesController.getInstance(currentAccount).markDialogAsRead(dialogId, maxId, maxId, 0, false, 0, 0, true, 0);
    }
}
