package com.microsoft.appcenter.distribute.download.manager;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.AsyncTask;
import com.microsoft.appcenter.distribute.ReleaseDetails;
import com.microsoft.appcenter.utils.AppCenterLog;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class DownloadManagerRequestTask extends AsyncTask<Void, Void, Void> {
    private final DownloadManagerReleaseDownloader mDownloader;

    public DownloadManagerRequestTask(DownloadManagerReleaseDownloader downloadManagerReleaseDownloader) {
        this.mDownloader = downloadManagerReleaseDownloader;
    }

    public Void doInBackground(Void... voidArr) {
        ReleaseDetails releaseDetails = this.mDownloader.getReleaseDetails();
        Uri downloadUrl = releaseDetails.getDownloadUrl();
        AppCenterLog.debug("AppCenterDistribute", "Start downloading new release from " + downloadUrl);
        DownloadManager downloadManager = this.mDownloader.getDownloadManager();
        DownloadManager.Request createRequest = createRequest(downloadUrl);
        if (releaseDetails.isMandatoryUpdate()) {
            createRequest.setNotificationVisibility(2);
            createRequest.setVisibleInDownloadsUi(false);
        }
        long currentTimeMillis = System.currentTimeMillis();
        long enqueue = downloadManager.enqueue(createRequest);
        if (isCancelled()) {
            return null;
        }
        this.mDownloader.onDownloadStarted(enqueue, currentTimeMillis);
        return null;
    }

    DownloadManager.Request createRequest(Uri uri) {
        return new DownloadManager.Request(uri);
    }
}
