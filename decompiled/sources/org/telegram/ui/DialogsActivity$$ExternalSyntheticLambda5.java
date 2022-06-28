package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.messenger.MessagesController;
/* loaded from: classes4.dex */
public final /* synthetic */ class DialogsActivity$$ExternalSyntheticLambda5 implements DialogInterface.OnClickListener {
    public static final /* synthetic */ DialogsActivity$$ExternalSyntheticLambda5 INSTANCE = new DialogsActivity$$ExternalSyntheticLambda5();

    private /* synthetic */ DialogsActivity$$ExternalSyntheticLambda5() {
    }

    @Override // android.content.DialogInterface.OnClickListener
    public final void onClick(DialogInterface dialogInterface, int i) {
        MessagesController.getGlobalNotificationsSettings().edit().putBoolean("askedAboutMiuiLockscreen", true).commit();
    }
}
