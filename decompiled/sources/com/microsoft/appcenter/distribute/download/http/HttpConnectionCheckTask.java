package com.microsoft.appcenter.distribute.download.http;

import android.os.AsyncTask;
import java.io.File;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public class HttpConnectionCheckTask extends AsyncTask<Void, Void, Void> {
    private final HttpConnectionReleaseDownloader mDownloader;

    public HttpConnectionCheckTask(HttpConnectionReleaseDownloader downloader) {
        this.mDownloader = downloader;
    }

    public Void doInBackground(Void... params) {
        File targetFile = this.mDownloader.getTargetFile();
        if (targetFile == null) {
            this.mDownloader.onDownloadError("Cannot access to downloads folder. Shared storage is not currently available.");
            return null;
        }
        String downloadedReleaseFilePath = this.mDownloader.getDownloadedReleaseFilePath();
        if (downloadedReleaseFilePath != null) {
            File downloadedReleaseFile = new File(downloadedReleaseFilePath);
            if (downloadedReleaseFilePath.equals(targetFile.getAbsolutePath())) {
                if (downloadedReleaseFile.exists()) {
                    this.mDownloader.onDownloadComplete(targetFile);
                    return null;
                }
            } else {
                downloadedReleaseFile.delete();
                this.mDownloader.setDownloadedReleaseFilePath(null);
            }
        }
        if (isCancelled()) {
            return null;
        }
        this.mDownloader.onStart(targetFile);
        return null;
    }
}
