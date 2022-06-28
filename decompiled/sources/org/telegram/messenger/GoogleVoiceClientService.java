package org.telegram.messenger;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import com.google.android.search.verification.client.SearchActionVerificationClientService;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public class GoogleVoiceClientService extends SearchActionVerificationClientService {
    @Override // com.google.android.search.verification.client.SearchActionVerificationClientService
    public void performAction(final Intent intent, boolean isVerified, Bundle options) {
        if (!isVerified) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.GoogleVoiceClientService$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                GoogleVoiceClientService.lambda$performAction$0(intent);
            }
        });
    }

    public static /* synthetic */ void lambda$performAction$0(Intent intent) {
        TLRPC.User user;
        try {
            int currentAccount = UserConfig.selectedAccount;
            ApplicationLoader.postInitApplication();
            if (!AndroidUtilities.needShowPasscode() && !SharedConfig.isWaitingForPasscodeEnter) {
                String text = intent.getStringExtra("android.intent.extra.TEXT");
                if (!TextUtils.isEmpty(text)) {
                    String contactUri = intent.getStringExtra("com.google.android.voicesearch.extra.RECIPIENT_CONTACT_URI");
                    String id = intent.getStringExtra("com.google.android.voicesearch.extra.RECIPIENT_CONTACT_CHAT_ID");
                    long uid = Long.parseLong(id);
                    TLRPC.User user2 = MessagesController.getInstance(currentAccount).getUser(Long.valueOf(uid));
                    if (user2 != null) {
                        user = user2;
                    } else {
                        TLRPC.User user3 = MessagesStorage.getInstance(currentAccount).getUserSync(uid);
                        if (user3 != null) {
                            MessagesController.getInstance(currentAccount).putUser(user3, true);
                        }
                        user = user3;
                    }
                    if (user != null) {
                        ContactsController.getInstance(currentAccount).markAsContacted(contactUri);
                        SendMessagesHelper.getInstance(currentAccount).sendMessage(text, user.id, null, null, null, true, null, null, null, true, 0, null);
                    }
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }
}
