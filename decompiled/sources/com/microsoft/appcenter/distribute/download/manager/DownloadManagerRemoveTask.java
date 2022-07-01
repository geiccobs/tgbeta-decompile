package com.microsoft.appcenter.distribute.download.manager;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.os.AsyncTask;
/* loaded from: classes.dex */
class DownloadManagerRemoveTask extends AsyncTask<Void, Void, Void> {
    @SuppressLint({"StaticFieldLeak"})
    private final Context mContext;
    private final long mDownloadId;

    public DownloadManagerRemoveTask(Context context, long j) {
        this.mContext = context;
        this.mDownloadId = j;
    }

    public Void doInBackground(Void... voidArr) {
        ((DownloadManager) this.mContext.getSystemService("download")).remove(this.mDownloadId);
        return null;
    }
}
