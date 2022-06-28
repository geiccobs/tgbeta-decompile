package com.google.android.exoplayer2.offline;

import java.io.IOException;
/* loaded from: classes3.dex */
public interface DownloadIndex {
    Download getDownload(String str) throws IOException;

    DownloadCursor getDownloads(int... iArr) throws IOException;
}
