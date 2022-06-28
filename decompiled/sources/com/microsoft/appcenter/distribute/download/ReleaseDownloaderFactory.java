package com.microsoft.appcenter.distribute.download;

import android.content.Context;
import android.os.Build;
import com.microsoft.appcenter.distribute.ReleaseDetails;
import com.microsoft.appcenter.distribute.download.ReleaseDownloader;
import com.microsoft.appcenter.distribute.download.http.HttpConnectionReleaseDownloader;
import com.microsoft.appcenter.distribute.download.manager.DownloadManagerReleaseDownloader;
/* loaded from: classes3.dex */
public class ReleaseDownloaderFactory {
    public static ReleaseDownloader create(Context context, ReleaseDetails releaseDetails, ReleaseDownloader.Listener listener) {
        if (Build.VERSION.SDK_INT < 21) {
            return new HttpConnectionReleaseDownloader(context, releaseDetails, listener);
        }
        return new DownloadManagerReleaseDownloader(context, releaseDetails, listener);
    }
}
