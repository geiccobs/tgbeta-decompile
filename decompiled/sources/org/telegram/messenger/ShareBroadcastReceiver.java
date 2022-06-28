package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.microsoft.appcenter.crashes.ingestion.models.ErrorAttachmentLog;
/* loaded from: classes4.dex */
public class ShareBroadcastReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String url = intent.getDataString();
        if (url != null) {
            Intent shareIntent = new Intent("android.intent.action.SEND");
            shareIntent.setType(ErrorAttachmentLog.CONTENT_TYPE_TEXT_PLAIN);
            shareIntent.putExtra("android.intent.extra.TEXT", url);
            Intent chooserIntent = Intent.createChooser(shareIntent, LocaleController.getString("ShareLink", org.telegram.messenger.beta.R.string.ShareLink));
            chooserIntent.setFlags(268435456);
            context.startActivity(chooserIntent);
        }
    }
}
