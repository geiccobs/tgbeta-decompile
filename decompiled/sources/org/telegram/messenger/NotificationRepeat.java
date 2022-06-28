package org.telegram.messenger;

import android.app.IntentService;
import android.content.Intent;
/* loaded from: classes4.dex */
public class NotificationRepeat extends IntentService {
    public NotificationRepeat() {
        super("NotificationRepeat");
    }

    @Override // android.app.IntentService
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        final int currentAccount = intent.getIntExtra("currentAccount", UserConfig.selectedAccount);
        if (!UserConfig.isValidAccount(currentAccount)) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationRepeat$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.getInstance(currentAccount).repeatNotificationMaybe();
            }
        });
    }
}
