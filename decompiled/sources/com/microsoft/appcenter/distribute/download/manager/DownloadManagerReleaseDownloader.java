package com.microsoft.appcenter.distribute.download.manager;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import com.microsoft.appcenter.distribute.DistributeConstants;
import com.microsoft.appcenter.distribute.ReleaseDetails;
import com.microsoft.appcenter.distribute.download.AbstractReleaseDownloader;
import com.microsoft.appcenter.distribute.download.ReleaseDownloader;
import com.microsoft.appcenter.utils.AppCenterLog;
import com.microsoft.appcenter.utils.AsyncTaskUtils;
import com.microsoft.appcenter.utils.HandlerUtils;
import com.microsoft.appcenter.utils.storage.SharedPreferencesManager;
/* loaded from: classes3.dex */
public class DownloadManagerReleaseDownloader extends AbstractReleaseDownloader {
    private long mDownloadId = -1;
    private DownloadManagerRequestTask mRequestTask;
    private DownloadManagerUpdateTask mUpdateTask;

    public DownloadManagerReleaseDownloader(Context context, ReleaseDetails releaseDetails, ReleaseDownloader.Listener listener) {
        super(context, releaseDetails, listener);
    }

    public DownloadManager getDownloadManager() {
        return (DownloadManager) this.mContext.getSystemService("download");
    }

    public synchronized long getDownloadId() {
        if (this.mDownloadId == -1) {
            this.mDownloadId = SharedPreferencesManager.getLong(DistributeConstants.PREFERENCE_KEY_DOWNLOAD_ID, -1L);
        }
        return this.mDownloadId;
    }

    private synchronized void setDownloadId(long downloadId) {
        this.mDownloadId = downloadId;
        if (downloadId != -1) {
            SharedPreferencesManager.putLong(DistributeConstants.PREFERENCE_KEY_DOWNLOAD_ID, downloadId);
        } else {
            SharedPreferencesManager.remove(DistributeConstants.PREFERENCE_KEY_DOWNLOAD_ID);
        }
    }

    @Override // com.microsoft.appcenter.distribute.download.ReleaseDownloader
    public synchronized boolean isDownloading() {
        return this.mDownloadId != -1;
    }

    @Override // com.microsoft.appcenter.distribute.download.ReleaseDownloader
    public synchronized void resume() {
        update();
    }

    @Override // com.microsoft.appcenter.distribute.download.AbstractReleaseDownloader, com.microsoft.appcenter.distribute.download.ReleaseDownloader
    public synchronized void cancel() {
        if (isCancelled()) {
            return;
        }
        super.cancel();
        DownloadManagerRequestTask downloadManagerRequestTask = this.mRequestTask;
        if (downloadManagerRequestTask != null) {
            downloadManagerRequestTask.cancel(true);
            this.mRequestTask = null;
        }
        DownloadManagerUpdateTask downloadManagerUpdateTask = this.mUpdateTask;
        if (downloadManagerUpdateTask != null) {
            downloadManagerUpdateTask.cancel(true);
            this.mUpdateTask = null;
        }
        long downloadId = getDownloadId();
        if (downloadId != -1) {
            remove(downloadId);
            setDownloadId(-1L);
        }
    }

    private synchronized void request() {
        if (isCancelled()) {
            return;
        }
        if (this.mRequestTask != null) {
            AppCenterLog.debug(DistributeConstants.LOG_TAG, "Downloading is already in progress.");
        } else {
            this.mRequestTask = (DownloadManagerRequestTask) AsyncTaskUtils.execute(DistributeConstants.LOG_TAG, new DownloadManagerRequestTask(this), new Void[0]);
        }
    }

    public synchronized void update() {
        if (isCancelled()) {
            return;
        }
        this.mUpdateTask = (DownloadManagerUpdateTask) AsyncTaskUtils.execute(DistributeConstants.LOG_TAG, new DownloadManagerUpdateTask(this), new Void[0]);
    }

    private void remove(long downloadId) {
        AppCenterLog.debug(DistributeConstants.LOG_TAG, "Removing download and notification id=" + downloadId);
        AsyncTaskUtils.execute(DistributeConstants.LOG_TAG, new DownloadManagerRemoveTask(this.mContext, downloadId), new Void[0]);
    }

    public synchronized void onStart() {
        request();
    }

    public synchronized void onDownloadStarted(long downloadId, long enqueueTime) {
        if (isCancelled()) {
            return;
        }
        setDownloadId(downloadId);
        this.mListener.onStart(enqueueTime);
        if (this.mReleaseDetails.isMandatoryUpdate()) {
            update();
        }
    }

    public synchronized void onDownloadProgress(Cursor cursor) {
        if (isCancelled()) {
            return;
        }
        long totalSize = cursor.getLong(cursor.getColumnIndexOrThrow("total_size"));
        long currentSize = cursor.getLong(cursor.getColumnIndexOrThrow("bytes_so_far"));
        if (this.mListener.onProgress(currentSize, totalSize)) {
            HandlerUtils.getMainHandler().postAtTime(new Runnable() { // from class: com.microsoft.appcenter.distribute.download.manager.DownloadManagerReleaseDownloader.1
                @Override // java.lang.Runnable
                public void run() {
                    DownloadManagerReleaseDownloader.this.update();
                }
            }, DistributeConstants.HANDLER_TOKEN_CHECK_PROGRESS, SystemClock.uptimeMillis() + 500);
        }
    }

    public synchronized void onDownloadComplete(Cursor cursor) {
        if (isCancelled()) {
            return;
        }
        AppCenterLog.debug(DistributeConstants.LOG_TAG, "Download was successful for id=" + this.mDownloadId);
        Uri localUri = Uri.parse(cursor.getString(cursor.getColumnIndexOrThrow("local_uri")));
        boolean installerFound = false;
        if (!this.mListener.onComplete(localUri)) {
            if (Build.VERSION.SDK_INT < 24) {
                installerFound = this.mListener.onComplete(getFileUriOnOldDevices(cursor));
            }
        } else {
            installerFound = true;
        }
        if (!installerFound) {
            this.mListener.onError("Installer not found");
        }
    }

    public synchronized void onDownloadError(RuntimeException e) {
        if (isCancelled()) {
            return;
        }
        AppCenterLog.error(DistributeConstants.LOG_TAG, "Failed to download update id=" + this.mDownloadId, e);
        this.mListener.onError(e.getMessage());
    }

    private static Uri getFileUriOnOldDevices(Cursor cursor) throws IllegalArgumentException {
        return Uri.parse("file://" + cursor.getString(cursor.getColumnIndexOrThrow("local_filename")));
    }
}
