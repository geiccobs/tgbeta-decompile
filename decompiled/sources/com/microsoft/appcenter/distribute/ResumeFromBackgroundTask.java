package com.microsoft.appcenter.distribute;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import com.microsoft.appcenter.utils.AppCenterLog;
import com.microsoft.appcenter.utils.storage.SharedPreferencesManager;
/* loaded from: classes.dex */
class ResumeFromBackgroundTask extends AsyncTask<Void, Void, Void> {
    @SuppressLint({"StaticFieldLeak"})
    private final Context mContext;
    private final long mDownloadedId;

    public ResumeFromBackgroundTask(Context context, long j) {
        this.mContext = context;
        this.mDownloadedId = j;
    }

    public Void doInBackground(Void... voidArr) {
        Distribute distribute = Distribute.getInstance();
        distribute.startFromBackground(this.mContext);
        AppCenterLog.debug("AppCenterDistribute", "Check download id=" + this.mDownloadedId);
        long j = SharedPreferencesManager.getLong("Distribute.download_id", -1L);
        if (j == -1 || j != this.mDownloadedId) {
            AppCenterLog.debug("AppCenterDistribute", "Ignoring download identifier we didn't expect, id=" + this.mDownloadedId);
            return null;
        }
        distribute.resumeDownload();
        return null;
    }
}
