package com.microsoft.appcenter.distribute.download.manager;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.AsyncTask;
import com.microsoft.appcenter.distribute.DistributeConstants;
import com.microsoft.appcenter.distribute.ReleaseDetails;
import com.microsoft.appcenter.utils.AppCenterLog;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public class DownloadManagerRequestTask extends AsyncTask<Void, Void, Void> {
    private final DownloadManagerReleaseDownloader mDownloader;

    public DownloadManagerRequestTask(DownloadManagerReleaseDownloader downloader) {
        this.mDownloader = downloader;
    }

    public Void doInBackground(Void... params) {
        ReleaseDetails releaseDetails = this.mDownloader.getReleaseDetails();
        Uri downloadUrl = releaseDetails.getDownloadUrl();
        AppCenterLog.debug(DistributeConstants.LOG_TAG, "Start downloading new release from " + downloadUrl);
        DownloadManager downloadManager = this.mDownloader.getDownloadManager();
        DownloadManager.Request request = createRequest(downloadUrl);
        if (releaseDetails.isMandatoryUpdate()) {
            request.setNotificationVisibility(2);
            request.setVisibleInDownloadsUi(false);
        }
        long enqueueTime = System.currentTimeMillis();
        long downloadId = downloadManager.enqueue(request);
        if (isCancelled()) {
            return null;
        }
        this.mDownloader.onDownloadStarted(downloadId, enqueueTime);
        return null;
    }

    DownloadManager.Request createRequest(Uri Uri) {
        return new DownloadManager.Request(Uri);
    }
}
