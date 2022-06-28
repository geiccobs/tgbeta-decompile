package com.google.android.exoplayer2.offline;

import com.google.android.exoplayer2.upstream.DataSink;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DummyDataSource;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.PriorityDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSink;
import com.google.android.exoplayer2.upstream.cache.CacheDataSinkFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheKeyFactory;
import com.google.android.exoplayer2.upstream.cache.CacheUtil;
import com.google.android.exoplayer2.util.PriorityTaskManager;
/* loaded from: classes3.dex */
public final class DownloaderConstructorHelper {
    private final Cache cache;
    private final CacheKeyFactory cacheKeyFactory;
    private final CacheDataSourceFactory offlineCacheDataSourceFactory;
    private final CacheDataSourceFactory onlineCacheDataSourceFactory;
    private final PriorityTaskManager priorityTaskManager;

    public DownloaderConstructorHelper(Cache cache, DataSource.Factory upstreamFactory) {
        this(cache, upstreamFactory, null, null, null);
    }

    public DownloaderConstructorHelper(Cache cache, DataSource.Factory upstreamFactory, DataSource.Factory cacheReadDataSourceFactory, DataSink.Factory cacheWriteDataSinkFactory, PriorityTaskManager priorityTaskManager) {
        this(cache, upstreamFactory, cacheReadDataSourceFactory, cacheWriteDataSinkFactory, priorityTaskManager, null);
    }

    public DownloaderConstructorHelper(Cache cache, DataSource.Factory upstreamFactory, DataSource.Factory cacheReadDataSourceFactory, DataSink.Factory cacheWriteDataSinkFactory, PriorityTaskManager priorityTaskManager, CacheKeyFactory cacheKeyFactory) {
        DataSource.Factory upstreamFactory2;
        DataSource.Factory readDataSourceFactory;
        DataSink.Factory cacheWriteDataSinkFactory2;
        if (priorityTaskManager == null) {
            upstreamFactory2 = upstreamFactory;
        } else {
            upstreamFactory2 = new PriorityDataSourceFactory(upstreamFactory, priorityTaskManager, -1000);
        }
        if (cacheReadDataSourceFactory != null) {
            readDataSourceFactory = cacheReadDataSourceFactory;
        } else {
            readDataSourceFactory = new FileDataSource.Factory();
        }
        if (cacheWriteDataSinkFactory != null) {
            cacheWriteDataSinkFactory2 = cacheWriteDataSinkFactory;
        } else {
            cacheWriteDataSinkFactory2 = new CacheDataSinkFactory(cache, CacheDataSink.DEFAULT_FRAGMENT_SIZE);
        }
        this.onlineCacheDataSourceFactory = new CacheDataSourceFactory(cache, upstreamFactory2, readDataSourceFactory, cacheWriteDataSinkFactory2, 1, null, cacheKeyFactory);
        this.offlineCacheDataSourceFactory = new CacheDataSourceFactory(cache, DummyDataSource.FACTORY, readDataSourceFactory, null, 1, null, cacheKeyFactory);
        this.cache = cache;
        this.priorityTaskManager = priorityTaskManager;
        this.cacheKeyFactory = cacheKeyFactory;
    }

    public Cache getCache() {
        return this.cache;
    }

    public CacheKeyFactory getCacheKeyFactory() {
        CacheKeyFactory cacheKeyFactory = this.cacheKeyFactory;
        return cacheKeyFactory != null ? cacheKeyFactory : CacheUtil.DEFAULT_CACHE_KEY_FACTORY;
    }

    public PriorityTaskManager getPriorityTaskManager() {
        PriorityTaskManager priorityTaskManager = this.priorityTaskManager;
        return priorityTaskManager != null ? priorityTaskManager : new PriorityTaskManager();
    }

    public CacheDataSource createCacheDataSource() {
        return this.onlineCacheDataSourceFactory.createDataSource();
    }

    public CacheDataSource createOfflineCacheDataSource() {
        return this.offlineCacheDataSourceFactory.createDataSource();
    }
}
