package com.google.android.exoplayer2.offline;

import java.io.File;
import java.io.IOException;
/* loaded from: classes3.dex */
public final class ActionFileUpgradeUtil {

    /* loaded from: classes3.dex */
    public interface DownloadIdProvider {
        String getId(DownloadRequest downloadRequest);
    }

    private ActionFileUpgradeUtil() {
    }

    public static void upgradeAndDelete(File actionFilePath, DownloadIdProvider downloadIdProvider, DefaultDownloadIndex downloadIndex, boolean deleteOnFailure, boolean addNewDownloadsAsCompleted) throws IOException {
        DownloadRequest[] load;
        ActionFile actionFile = new ActionFile(actionFilePath);
        if (actionFile.exists()) {
            boolean success = false;
            try {
                long nowMs = System.currentTimeMillis();
                for (DownloadRequest request : actionFile.load()) {
                    if (downloadIdProvider != null) {
                        request = request.copyWithId(downloadIdProvider.getId(request));
                    }
                    mergeRequest(request, downloadIndex, addNewDownloadsAsCompleted, nowMs);
                }
                success = true;
            } finally {
                if (success || deleteOnFailure) {
                    actionFile.delete();
                }
            }
        }
    }

    static void mergeRequest(DownloadRequest request, DefaultDownloadIndex downloadIndex, boolean addNewDownloadAsCompleted, long nowMs) throws IOException {
        Download download;
        Download download2 = downloadIndex.getDownload(request.id);
        if (download2 != null) {
            download = DownloadManager.mergeRequest(download2, request, download2.stopReason, nowMs);
        } else {
            download = new Download(request, addNewDownloadAsCompleted ? 3 : 0, nowMs, nowMs, -1L, 0, 0);
        }
        downloadIndex.putDownload(download);
    }
}
