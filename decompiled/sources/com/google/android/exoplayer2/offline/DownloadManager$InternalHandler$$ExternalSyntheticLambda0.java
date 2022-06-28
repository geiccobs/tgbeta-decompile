package com.google.android.exoplayer2.offline;

import com.google.android.exoplayer2.offline.DownloadManager;
import java.util.Comparator;
/* loaded from: classes3.dex */
public final /* synthetic */ class DownloadManager$InternalHandler$$ExternalSyntheticLambda0 implements Comparator {
    public static final /* synthetic */ DownloadManager$InternalHandler$$ExternalSyntheticLambda0 INSTANCE = new DownloadManager$InternalHandler$$ExternalSyntheticLambda0();

    private /* synthetic */ DownloadManager$InternalHandler$$ExternalSyntheticLambda0() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int compareStartTimes;
        compareStartTimes = DownloadManager.InternalHandler.compareStartTimes((Download) obj, (Download) obj2);
        return compareStartTimes;
    }
}
