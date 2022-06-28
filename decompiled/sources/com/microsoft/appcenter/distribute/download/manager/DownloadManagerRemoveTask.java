package com.microsoft.appcenter.distribute.download.manager;

import android.app.DownloadManager;
import android.content.Context;
import android.os.AsyncTask;
/* loaded from: classes3.dex */
class DownloadManagerRemoveTask extends AsyncTask<Void, Void, Void> {
    private final Context mContext;
    private final long mDownloadId;

    public DownloadManagerRemoveTask(Context context, long downloadId) {
        this.mContext = context;
        this.mDownloadId = downloadId;
    }

    public Void doInBackground(Void... params) {
        DownloadManager downloadManager = (DownloadManager) this.mContext.getSystemService("download");
        downloadManager.remove(this.mDownloadId);
        return null;
    }
}
