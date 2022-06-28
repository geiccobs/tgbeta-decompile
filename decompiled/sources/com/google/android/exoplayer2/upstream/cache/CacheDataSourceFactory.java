package com.google.android.exoplayer2.upstream.cache;

import com.google.android.exoplayer2.upstream.DataSink;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
/* loaded from: classes3.dex */
public final class CacheDataSourceFactory implements DataSource.Factory {
    private final Cache cache;
    private final CacheKeyFactory cacheKeyFactory;
    private final DataSource.Factory cacheReadDataSourceFactory;
    private final DataSink.Factory cacheWriteDataSinkFactory;
    private final CacheDataSource.EventListener eventListener;
    private final int flags;
    private final DataSource.Factory upstreamFactory;

    public CacheDataSourceFactory(Cache cache, DataSource.Factory upstreamFactory) {
        this(cache, upstreamFactory, 0);
    }

    public CacheDataSourceFactory(Cache cache, DataSource.Factory upstreamFactory, int flags) {
        this(cache, upstreamFactory, new FileDataSource.Factory(), new CacheDataSinkFactory(cache, CacheDataSink.DEFAULT_FRAGMENT_SIZE), flags, null);
    }

    public CacheDataSourceFactory(Cache cache, DataSource.Factory upstreamFactory, DataSource.Factory cacheReadDataSourceFactory, DataSink.Factory cacheWriteDataSinkFactory, int flags, CacheDataSource.EventListener eventListener) {
        this(cache, upstreamFactory, cacheReadDataSourceFactory, cacheWriteDataSinkFactory, flags, eventListener, null);
    }

    public CacheDataSourceFactory(Cache cache, DataSource.Factory upstreamFactory, DataSource.Factory cacheReadDataSourceFactory, DataSink.Factory cacheWriteDataSinkFactory, int flags, CacheDataSource.EventListener eventListener, CacheKeyFactory cacheKeyFactory) {
        this.cache = cache;
        this.upstreamFactory = upstreamFactory;
        this.cacheReadDataSourceFactory = cacheReadDataSourceFactory;
        this.cacheWriteDataSinkFactory = cacheWriteDataSinkFactory;
        this.flags = flags;
        this.eventListener = eventListener;
        this.cacheKeyFactory = cacheKeyFactory;
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource.Factory
    public CacheDataSource createDataSource() {
        Cache cache = this.cache;
        DataSource createDataSource = this.upstreamFactory.createDataSource();
        DataSource createDataSource2 = this.cacheReadDataSourceFactory.createDataSource();
        DataSink.Factory factory = this.cacheWriteDataSinkFactory;
        return new CacheDataSource(cache, createDataSource, createDataSource2, factory == null ? null : factory.createDataSink(), this.flags, this.eventListener, this.cacheKeyFactory);
    }
}
