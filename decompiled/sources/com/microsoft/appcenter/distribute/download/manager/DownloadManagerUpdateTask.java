package com.microsoft.appcenter.distribute.download.manager;

import android.app.DownloadManager;
import android.database.Cursor;
import android.os.AsyncTask;
import androidx.core.app.NotificationCompat;
import java.util.NoSuchElementException;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public class DownloadManagerUpdateTask extends AsyncTask<Void, Void, Void> {
    private final DownloadManagerReleaseDownloader mDownloader;

    public DownloadManagerUpdateTask(DownloadManagerReleaseDownloader downloader) {
        this.mDownloader = downloader;
    }

    public Void doInBackground(Void... params) {
        Cursor cursor;
        DownloadManager downloadManager = this.mDownloader.getDownloadManager();
        long downloadId = this.mDownloader.getDownloadId();
        if (downloadId == -1) {
            this.mDownloader.onStart();
            return null;
        }
        try {
            cursor = downloadManager.query(new DownloadManager.Query().setFilterById(downloadId));
        } catch (RuntimeException e) {
            this.mDownloader.onDownloadError(e);
        }
        if (cursor == null) {
            throw new NoSuchElementException("Cannot find download with id=" + downloadId);
        } else if (!cursor.moveToFirst()) {
            throw new NoSuchElementException("Cannot find download with id=" + downloadId);
        } else if (isCancelled()) {
            cursor.close();
            return null;
        } else {
            int status = cursor.getInt(cursor.getColumnIndexOrThrow(NotificationCompat.CATEGORY_STATUS));
            if (status == 16) {
                int reason = cursor.getInt(cursor.getColumnIndexOrThrow("reason"));
                throw new IllegalStateException("The download has failed with reason code: " + reason);
            } else if (status != 8) {
                this.mDownloader.onDownloadProgress(cursor);
                cursor.close();
                return null;
            } else {
                this.mDownloader.onDownloadComplete(cursor);
                cursor.close();
                return null;
            }
        }
    }
}
