package com.microsoft.appcenter.distribute;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.microsoft.appcenter.utils.AsyncTaskUtils;
/* loaded from: classes.dex */
public class DownloadManagerReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if ("android.intent.action.DOWNLOAD_NOTIFICATION_CLICKED".equals(action)) {
            Distribute.getInstance().resumeApp(context);
        } else if (!"android.intent.action.DOWNLOAD_COMPLETE".equals(action)) {
        } else {
            long longExtra = intent.getLongExtra("extra_download_id", -1L);
            if (longExtra == -1) {
                return;
            }
            AsyncTaskUtils.execute("AppCenterDistribute", new ResumeFromBackgroundTask(context, longExtra), new Void[0]);
        }
    }
}
