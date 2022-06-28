package com.google.android.exoplayer2.offline;

import java.io.IOException;
/* loaded from: classes3.dex */
public interface Downloader {

    /* loaded from: classes3.dex */
    public interface ProgressListener {
        void onProgress(long j, long j2, float f);
    }

    void cancel();

    void download(ProgressListener progressListener) throws InterruptedException, IOException;

    void remove() throws InterruptedException;
}
