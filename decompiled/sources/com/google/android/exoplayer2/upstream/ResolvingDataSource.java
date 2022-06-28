package com.google.android.exoplayer2.upstream;

import android.net.Uri;
import com.google.android.exoplayer2.upstream.DataSource;
import java.io.IOException;
import java.util.List;
import java.util.Map;
/* loaded from: classes3.dex */
public final class ResolvingDataSource implements DataSource {
    private final Resolver resolver;
    private final DataSource upstreamDataSource;
    private boolean upstreamOpened;

    /* loaded from: classes3.dex */
    public interface Resolver {
        DataSpec resolveDataSpec(DataSpec dataSpec) throws IOException;

        Uri resolveReportedUri(Uri uri);

        /* renamed from: com.google.android.exoplayer2.upstream.ResolvingDataSource$Resolver$-CC */
        /* loaded from: classes3.dex */
        public final /* synthetic */ class CC {
            public static Uri $default$resolveReportedUri(Resolver _this, Uri uri) {
                return uri;
            }
        }
    }

    /* loaded from: classes3.dex */
    public static final class Factory implements DataSource.Factory {
        private final Resolver resolver;
        private final DataSource.Factory upstreamFactory;

        public Factory(DataSource.Factory upstreamFactory, Resolver resolver) {
            this.upstreamFactory = upstreamFactory;
            this.resolver = resolver;
        }

        @Override // com.google.android.exoplayer2.upstream.DataSource.Factory
        public ResolvingDataSource createDataSource() {
            return new ResolvingDataSource(this.upstreamFactory.createDataSource(), this.resolver);
        }
    }

    public ResolvingDataSource(DataSource upstreamDataSource, Resolver resolver) {
        this.upstreamDataSource = upstreamDataSource;
        this.resolver = resolver;
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public void addTransferListener(TransferListener transferListener) {
        this.upstreamDataSource.addTransferListener(transferListener);
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public long open(DataSpec dataSpec) throws IOException {
        DataSpec resolvedDataSpec = this.resolver.resolveDataSpec(dataSpec);
        this.upstreamOpened = true;
        return this.upstreamDataSource.open(resolvedDataSpec);
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public int read(byte[] buffer, int offset, int readLength) throws IOException {
        return this.upstreamDataSource.read(buffer, offset, readLength);
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public Uri getUri() {
        Uri reportedUri = this.upstreamDataSource.getUri();
        if (reportedUri == null) {
            return null;
        }
        return this.resolver.resolveReportedUri(reportedUri);
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public Map<String, List<String>> getResponseHeaders() {
        return this.upstreamDataSource.getResponseHeaders();
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public void close() throws IOException {
        if (this.upstreamOpened) {
            this.upstreamOpened = false;
            this.upstreamDataSource.close();
        }
    }
}
