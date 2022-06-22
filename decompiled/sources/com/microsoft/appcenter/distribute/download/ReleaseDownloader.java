package com.microsoft.appcenter.distribute.download;

import android.net.Uri;
import com.microsoft.appcenter.distribute.ReleaseDetails;
/* loaded from: classes.dex */
public interface ReleaseDownloader {

    /* loaded from: classes.dex */
    public interface Listener {
        boolean onComplete(Uri uri);

        void onError(String str);

        boolean onProgress(long j, long j2);

        void onStart(long j);
    }

    void cancel();

    ReleaseDetails getReleaseDetails();

    boolean isDownloading();

    void resume();
}
