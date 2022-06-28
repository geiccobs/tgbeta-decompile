package com.google.android.exoplayer2.upstream;

import android.net.Uri;
import com.google.android.exoplayer2.upstream.Loader;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
/* loaded from: classes3.dex */
public final class ParsingLoadable<T> implements Loader.Loadable {
    private final StatsDataSource dataSource;
    public final DataSpec dataSpec;
    private final Parser<? extends T> parser;
    private volatile T result;
    public final int type;

    /* loaded from: classes3.dex */
    public interface Parser<T> {
        T parse(Uri uri, InputStream inputStream) throws IOException;
    }

    public static <T> T load(DataSource dataSource, Parser<? extends T> parser, Uri uri, int type) throws IOException {
        ParsingLoadable<T> loadable = new ParsingLoadable<>(dataSource, uri, type, parser);
        loadable.load();
        return (T) Assertions.checkNotNull(loadable.getResult());
    }

    public static <T> T load(DataSource dataSource, Parser<? extends T> parser, DataSpec dataSpec, int type) throws IOException {
        ParsingLoadable<T> loadable = new ParsingLoadable<>(dataSource, dataSpec, type, parser);
        loadable.load();
        return (T) Assertions.checkNotNull(loadable.getResult());
    }

    public ParsingLoadable(DataSource dataSource, Uri uri, int type, Parser<? extends T> parser) {
        this(dataSource, new DataSpec(uri, 1), type, parser);
    }

    public ParsingLoadable(DataSource dataSource, DataSpec dataSpec, int type, Parser<? extends T> parser) {
        this.dataSource = new StatsDataSource(dataSource);
        this.dataSpec = dataSpec;
        this.type = type;
        this.parser = parser;
    }

    public final T getResult() {
        return this.result;
    }

    public long bytesLoaded() {
        return this.dataSource.getBytesRead();
    }

    public Uri getUri() {
        return this.dataSource.getLastOpenedUri();
    }

    public Map<String, List<String>> getResponseHeaders() {
        return this.dataSource.getLastResponseHeaders();
    }

    @Override // com.google.android.exoplayer2.upstream.Loader.Loadable
    public final void cancelLoad() {
    }

    @Override // com.google.android.exoplayer2.upstream.Loader.Loadable
    public final void load() throws IOException {
        this.dataSource.resetBytesRead();
        DataSourceInputStream inputStream = new DataSourceInputStream(this.dataSource, this.dataSpec);
        try {
            inputStream.open();
            Uri dataSourceUri = (Uri) Assertions.checkNotNull(this.dataSource.getUri());
            this.result = this.parser.parse(dataSourceUri, inputStream);
        } finally {
            Util.closeQuietly(inputStream);
        }
    }
}
