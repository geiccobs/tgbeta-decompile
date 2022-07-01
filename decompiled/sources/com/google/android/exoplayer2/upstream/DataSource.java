package com.google.android.exoplayer2.upstream;

import android.net.Uri;
import java.io.IOException;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public interface DataSource {

    /* loaded from: classes.dex */
    public interface Factory {
        DataSource createDataSource();
    }

    void addTransferListener(TransferListener transferListener);

    void close() throws IOException;

    Map<String, List<String>> getResponseHeaders();

    Uri getUri();

    long open(DataSpec dataSpec) throws IOException;

    int read(byte[] bArr, int i, int i2) throws IOException;

    /* renamed from: com.google.android.exoplayer2.upstream.DataSource$-CC */
    /* loaded from: classes.dex */
    public final /* synthetic */ class CC {
    }
}
