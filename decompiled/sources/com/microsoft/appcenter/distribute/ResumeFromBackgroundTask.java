package com.microsoft.appcenter.distribute;

import android.content.Context;
import android.os.AsyncTask;
import com.microsoft.appcenter.utils.AppCenterLog;
import com.microsoft.appcenter.utils.storage.SharedPreferencesManager;
/* loaded from: classes3.dex */
class ResumeFromBackgroundTask extends AsyncTask<Void, Void, Void> {
    private final Context mContext;
    private final long mDownloadedId;

    public ResumeFromBackgroundTask(Context context, long downloadedId) {
        this.mContext = context;
        this.mDownloadedId = downloadedId;
    }

    public Void doInBackground(Void... args) {
        Distribute distribute = Distribute.getInstance();
        distribute.startFromBackground(this.mContext);
        AppCenterLog.debug(DistributeConstants.LOG_TAG, "Check download id=" + this.mDownloadedId);
        long expectedDownloadId = SharedPreferencesManager.getLong(DistributeConstants.PREFERENCE_KEY_DOWNLOAD_ID, -1L);
        if (expectedDownloadId == -1 || expectedDownloadId != this.mDownloadedId) {
            AppCenterLog.debug(DistributeConstants.LOG_TAG, "Ignoring download identifier we didn't expect, id=" + this.mDownloadedId);
            return null;
        }
        distribute.resumeDownload();
        return null;
    }
}
