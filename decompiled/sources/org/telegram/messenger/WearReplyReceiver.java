package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.core.app.RemoteInput;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public class WearReplyReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        ApplicationLoader.postInitApplication();
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput == null) {
            return;
        }
        final CharSequence text = remoteInput.getCharSequence(NotificationsController.EXTRA_VOICE_REPLY);
        if (!TextUtils.isEmpty(text)) {
            final long dialogId = intent.getLongExtra("dialog_id", 0L);
            final int maxId = intent.getIntExtra("max_id", 0);
            int currentAccount = intent.getIntExtra("currentAccount", 0);
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
                    Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.WearReplyReceiver$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            WearReplyReceiver.this.m1233lambda$onReceive$1$orgtelegrammessengerWearReplyReceiver(accountInstance, dialogId, text, maxId);
                        }
                    });
                    return;
                }
            } else if (DialogObject.isChatDialog(dialogId)) {
                TLRPC.Chat chat = accountInstance.getMessagesController().getChat(Long.valueOf(-dialogId));
                if (chat == null) {
                    Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.WearReplyReceiver$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            WearReplyReceiver.this.m1235lambda$onReceive$3$orgtelegrammessengerWearReplyReceiver(accountInstance, dialogId, text, maxId);
                        }
                    });
                    return;
                }
            }
            sendMessage(accountInstance, text, dialogId, maxId);
        }
    }

    /* renamed from: lambda$onReceive$1$org-telegram-messenger-WearReplyReceiver */
    public /* synthetic */ void m1233lambda$onReceive$1$orgtelegrammessengerWearReplyReceiver(final AccountInstance accountInstance, final long dialogId, final CharSequence text, final int maxId) {
        final TLRPC.User user1 = accountInstance.getMessagesStorage().getUserSync(dialogId);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.WearReplyReceiver$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                WearReplyReceiver.this.m1232lambda$onReceive$0$orgtelegrammessengerWearReplyReceiver(accountInstance, user1, text, dialogId, maxId);
            }
        });
    }

    /* renamed from: lambda$onReceive$0$org-telegram-messenger-WearReplyReceiver */
    public /* synthetic */ void m1232lambda$onReceive$0$orgtelegrammessengerWearReplyReceiver(AccountInstance accountInstance, TLRPC.User user1, CharSequence text, long dialogId, int maxId) {
        accountInstance.getMessagesController().putUser(user1, true);
        sendMessage(accountInstance, text, dialogId, maxId);
    }

    /* renamed from: lambda$onReceive$3$org-telegram-messenger-WearReplyReceiver */
    public /* synthetic */ void m1235lambda$onReceive$3$orgtelegrammessengerWearReplyReceiver(final AccountInstance accountInstance, final long dialogId, final CharSequence text, final int maxId) {
        final TLRPC.Chat chat1 = accountInstance.getMessagesStorage().getChatSync(-dialogId);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.WearReplyReceiver$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                WearReplyReceiver.this.m1234lambda$onReceive$2$orgtelegrammessengerWearReplyReceiver(accountInstance, chat1, text, dialogId, maxId);
            }
        });
    }

    /* renamed from: lambda$onReceive$2$org-telegram-messenger-WearReplyReceiver */
    public /* synthetic */ void m1234lambda$onReceive$2$orgtelegrammessengerWearReplyReceiver(AccountInstance accountInstance, TLRPC.Chat chat1, CharSequence text, long dialogId, int maxId) {
        accountInstance.getMessagesController().putChat(chat1, true);
        sendMessage(accountInstance, text, dialogId, maxId);
    }

    private void sendMessage(AccountInstance accountInstance, CharSequence text, long dialog_id, int max_id) {
        accountInstance.getSendMessagesHelper().sendMessage(text.toString(), dialog_id, null, null, null, true, null, null, null, true, 0, null);
        accountInstance.getMessagesController().markDialogAsRead(dialog_id, max_id, max_id, 0, false, 0, 0, true, 0);
    }
}
