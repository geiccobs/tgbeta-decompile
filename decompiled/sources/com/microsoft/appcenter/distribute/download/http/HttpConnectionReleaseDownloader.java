package com.microsoft.appcenter.distribute.download.http;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import com.microsoft.appcenter.distribute.DistributeConstants;
import com.microsoft.appcenter.distribute.PermissionUtils;
import com.microsoft.appcenter.distribute.R;
import com.microsoft.appcenter.distribute.ReleaseDetails;
import com.microsoft.appcenter.distribute.download.AbstractReleaseDownloader;
import com.microsoft.appcenter.distribute.download.ReleaseDownloader;
import com.microsoft.appcenter.utils.AppCenterLog;
import com.microsoft.appcenter.utils.AsyncTaskUtils;
import com.microsoft.appcenter.utils.NetworkStateHelper;
import com.microsoft.appcenter.utils.storage.SharedPreferencesManager;
import java.io.File;
import java.util.ArrayList;
/* loaded from: classes3.dex */
public class HttpConnectionReleaseDownloader extends AbstractReleaseDownloader {
    private HttpConnectionCheckTask mCheckTask;
    private HttpConnectionDownloadFileTask mDownloadTask;
    private Notification.Builder mNotificationBuilder;
    private File mTargetFile;

    public HttpConnectionReleaseDownloader(Context context, ReleaseDetails releaseDetails, ReleaseDownloader.Listener listener) {
        super(context, releaseDetails, listener);
    }

    public File getTargetFile() {
        File downloadsDirectory;
        if (this.mTargetFile == null && (downloadsDirectory = this.mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)) != null) {
            this.mTargetFile = new File(downloadsDirectory, this.mReleaseDetails.getReleaseHash() + ".apk");
        }
        return this.mTargetFile;
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) this.mContext.getSystemService("notification");
    }

    public synchronized String getDownloadedReleaseFilePath() {
        return SharedPreferencesManager.getString(DistributeConstants.PREFERENCE_KEY_DOWNLOADED_RELEASE_FILE, null);
    }

    public synchronized void setDownloadedReleaseFilePath(String downloadedReleaseFilePath) {
        if (isCancelled()) {
            return;
        }
        if (downloadedReleaseFilePath != null) {
            SharedPreferencesManager.putString(DistributeConstants.PREFERENCE_KEY_DOWNLOADED_RELEASE_FILE, downloadedReleaseFilePath);
        } else {
            SharedPreferencesManager.remove(DistributeConstants.PREFERENCE_KEY_DOWNLOADED_RELEASE_FILE);
        }
    }

    Notification.Builder getNotificationBuilder() {
        if (this.mNotificationBuilder == null) {
            this.mNotificationBuilder = new Notification.Builder(this.mContext);
        }
        return this.mNotificationBuilder;
    }

    @Override // com.microsoft.appcenter.distribute.download.ReleaseDownloader
    public synchronized boolean isDownloading() {
        return this.mDownloadTask != null;
    }

    @Override // com.microsoft.appcenter.distribute.download.ReleaseDownloader
    public synchronized void resume() {
        if (isCancelled()) {
            return;
        }
        if (!NetworkStateHelper.getSharedInstance(this.mContext).isNetworkConnected()) {
            this.mListener.onError("No network connection, abort downloading.");
            return;
        }
        String[] permissions = requiredPermissions();
        int[] permissionsState = PermissionUtils.permissionsState(this.mContext, permissions);
        if (!PermissionUtils.permissionsAreGranted(permissionsState)) {
            this.mListener.onError("No external storage permission.");
        } else {
            check();
        }
    }

    @Override // com.microsoft.appcenter.distribute.download.AbstractReleaseDownloader, com.microsoft.appcenter.distribute.download.ReleaseDownloader
    public synchronized void cancel() {
        if (isCancelled()) {
            return;
        }
        super.cancel();
        HttpConnectionCheckTask httpConnectionCheckTask = this.mCheckTask;
        if (httpConnectionCheckTask != null) {
            httpConnectionCheckTask.cancel(true);
            this.mCheckTask = null;
        }
        HttpConnectionDownloadFileTask httpConnectionDownloadFileTask = this.mDownloadTask;
        if (httpConnectionDownloadFileTask != null) {
            httpConnectionDownloadFileTask.cancel(true);
            this.mDownloadTask = null;
        }
        String filePath = getDownloadedReleaseFilePath();
        if (filePath != null) {
            removeFile(new File(filePath));
            SharedPreferencesManager.remove(DistributeConstants.PREFERENCE_KEY_DOWNLOADED_RELEASE_FILE);
        }
        cancelProgressNotification();
    }

    private synchronized void check() {
        this.mCheckTask = (HttpConnectionCheckTask) AsyncTaskUtils.execute(DistributeConstants.LOG_TAG, new HttpConnectionCheckTask(this), new Void[0]);
    }

    private synchronized void downloadFile(File file) {
        if (this.mDownloadTask != null) {
            AppCenterLog.debug(DistributeConstants.LOG_TAG, "Downloading of " + file.getPath() + " is already in progress.");
            return;
        }
        Uri downloadUrl = this.mReleaseDetails.getDownloadUrl();
        AppCenterLog.debug(DistributeConstants.LOG_TAG, "Start downloading new release from " + downloadUrl);
        this.mDownloadTask = (HttpConnectionDownloadFileTask) AsyncTaskUtils.execute(DistributeConstants.LOG_TAG, new HttpConnectionDownloadFileTask(this, downloadUrl, file), new Void[0]);
    }

    private void removeFile(File file) {
        AppCenterLog.debug(DistributeConstants.LOG_TAG, "Removing downloaded file from " + file.getAbsolutePath());
        AsyncTaskUtils.execute(DistributeConstants.LOG_TAG, new HttpConnectionRemoveFileTask(file), new Void[0]);
    }

    private void showProgressNotification(long currentSize, long totalSize) {
        if (this.mReleaseDetails.isMandatoryUpdate()) {
            return;
        }
        Notification.Builder builder = getNotificationBuilder();
        builder.setContentTitle(this.mContext.getString(R.string.appcenter_distribute_downloading_update)).setSmallIcon(this.mContext.getApplicationInfo().icon).setProgress((int) (totalSize / DistributeConstants.KIBIBYTE_IN_BYTES), (int) (currentSize / DistributeConstants.KIBIBYTE_IN_BYTES), totalSize <= 0);
        getNotificationManager().notify(getNotificationId(), builder.build());
    }

    private void cancelProgressNotification() {
        getNotificationManager().cancel(getNotificationId());
    }

    public synchronized void onStart(File targetFile) {
        if (isCancelled()) {
            return;
        }
        downloadFile(targetFile);
    }

    public synchronized void onDownloadStarted(long enqueueTime) {
        if (isCancelled()) {
            return;
        }
        showProgressNotification(0L, 0L);
        this.mListener.onStart(enqueueTime);
    }

    public synchronized void onDownloadProgress(long currentSize, long totalSize) {
        if (isCancelled()) {
            return;
        }
        showProgressNotification(currentSize, totalSize);
        this.mListener.onProgress(currentSize, totalSize);
    }

    public synchronized void onDownloadComplete(File targetFile) {
        if (isCancelled()) {
            return;
        }
        cancelProgressNotification();
        if (this.mReleaseDetails.getSize() != targetFile.length()) {
            this.mListener.onError("Downloaded file has incorrect size.");
            return;
        }
        String downloadedReleaseFilePath = targetFile.getAbsolutePath();
        setDownloadedReleaseFilePath(downloadedReleaseFilePath);
        ReleaseDownloader.Listener listener = this.mListener;
        listener.onComplete(Uri.parse("file://" + downloadedReleaseFilePath));
    }

    public synchronized void onDownloadError(String errorMessage) {
        if (isCancelled()) {
            return;
        }
        cancelProgressNotification();
        this.mListener.onError(errorMessage);
    }

    static String[] requiredPermissions() {
        ArrayList<String> permissions = new ArrayList<>();
        if (Build.VERSION.SDK_INT < 19) {
            permissions.add("android.permission.WRITE_EXTERNAL_STORAGE");
        }
        return (String[]) permissions.toArray(new String[0]);
    }

    private static int getNotificationId() {
        return HttpConnectionReleaseDownloader.class.getName().hashCode();
    }
}
