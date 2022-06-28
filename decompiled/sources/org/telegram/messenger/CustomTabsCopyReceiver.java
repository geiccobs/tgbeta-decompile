package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
/* loaded from: classes4.dex */
public class CustomTabsCopyReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String url = intent.getDataString();
        if (url != null) {
            AndroidUtilities.addToClipboard(url);
            Toast.makeText(context, LocaleController.getString("LinkCopied", org.telegram.messenger.beta.R.string.LinkCopied), 0).show();
        }
    }
}
