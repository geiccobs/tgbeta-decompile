package com.google.android.exoplayer2.upstream;

import android.net.Uri;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.util.List;
import java.util.Map;
/* loaded from: classes3.dex */
public final class TeeDataSource implements DataSource {
    private long bytesRemaining;
    private final DataSink dataSink;
    private boolean dataSinkNeedsClosing;
    private final DataSource upstream;

    public TeeDataSource(DataSource upstream, DataSink dataSink) {
        this.upstream = (DataSource) Assertions.checkNotNull(upstream);
        this.dataSink = (DataSink) Assertions.checkNotNull(dataSink);
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public void addTransferListener(TransferListener transferListener) {
        this.upstream.addTransferListener(transferListener);
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public long open(DataSpec dataSpec) throws IOException {
        long open = this.upstream.open(dataSpec);
        this.bytesRemaining = open;
        if (open == 0) {
            return 0L;
        }
        if (dataSpec.length == -1) {
            long j = this.bytesRemaining;
            if (j != -1) {
                dataSpec = dataSpec.subrange(0L, j);
            }
        }
        this.dataSinkNeedsClosing = true;
        this.dataSink.open(dataSpec);
        return this.bytesRemaining;
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public int read(byte[] buffer, int offset, int max) throws IOException {
        if (this.bytesRemaining == 0) {
            return -1;
        }
        int bytesRead = this.upstream.read(buffer, offset, max);
        if (bytesRead > 0) {
            this.dataSink.write(buffer, offset, bytesRead);
            long j = this.bytesRemaining;
            if (j != -1) {
                this.bytesRemaining = j - bytesRead;
            }
        }
        return bytesRead;
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public Uri getUri() {
        return this.upstream.getUri();
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public Map<String, List<String>> getResponseHeaders() {
        return this.upstream.getResponseHeaders();
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public void close() throws IOException {
        try {
            this.upstream.close();
        } finally {
            if (this.dataSinkNeedsClosing) {
                this.dataSinkNeedsClosing = false;
                this.dataSink.close();
            }
        }
    }
}
