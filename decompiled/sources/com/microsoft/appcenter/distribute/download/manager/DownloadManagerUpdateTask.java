package com.microsoft.appcenter.distribute.download.manager;

import android.app.DownloadManager;
import android.database.Cursor;
import android.os.AsyncTask;
import java.util.NoSuchElementException;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class DownloadManagerUpdateTask extends AsyncTask<Void, Void, Void> {
    private final DownloadManagerReleaseDownloader mDownloader;

    public DownloadManagerUpdateTask(DownloadManagerReleaseDownloader downloadManagerReleaseDownloader) {
        this.mDownloader = downloadManagerReleaseDownloader;
    }

    public Void doInBackground(Void... voidArr) {
        Cursor query;
        DownloadManager downloadManager = this.mDownloader.getDownloadManager();
        long downloadId = this.mDownloader.getDownloadId();
        if (downloadId == -1) {
            this.mDownloader.onStart();
            return null;
        }
        try {
            query = downloadManager.query(new DownloadManager.Query().setFilterById(downloadId));
        } catch (RuntimeException e) {
            this.mDownloader.onDownloadError(e);
        }
        if (query == null) {
            throw new NoSuchElementException("Cannot find download with id=" + downloadId);
        } else if (!query.moveToFirst()) {
            throw new NoSuchElementException("Cannot find download with id=" + downloadId);
        } else if (isCancelled()) {
            query.close();
            return null;
        } else {
            int i = query.getInt(query.getColumnIndexOrThrow("status"));
            if (i == 16) {
                int i2 = query.getInt(query.getColumnIndexOrThrow("reason"));
                throw new IllegalStateException("The download has failed with reason code: " + i2);
            } else if (i != 8) {
                this.mDownloader.onDownloadProgress(query);
                query.close();
                return null;
            } else {
                this.mDownloader.onDownloadComplete(query);
                query.close();
                return null;
            }
        }
    }
}
