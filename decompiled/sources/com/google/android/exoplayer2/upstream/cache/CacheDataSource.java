package com.google.android.exoplayer2.upstream.cache;

import android.net.Uri;
import com.google.android.exoplayer2.upstream.DataSink;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSourceException;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.TeeDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.ContentMetadata;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.List;
import java.util.Map;
/* loaded from: classes3.dex */
public final class CacheDataSource implements DataSource {
    public static final int CACHE_IGNORED_REASON_ERROR = 0;
    public static final int CACHE_IGNORED_REASON_UNSET_LENGTH = 1;
    private static final int CACHE_NOT_IGNORED = -1;
    public static final int FLAG_BLOCK_ON_CACHE = 1;
    public static final int FLAG_IGNORE_CACHE_FOR_UNSET_LENGTH_REQUESTS = 4;
    public static final int FLAG_IGNORE_CACHE_ON_ERROR = 2;
    private static final long MIN_READ_BEFORE_CHECKING_CACHE = 102400;
    private Uri actualUri;
    private final boolean blockOnCache;
    private long bytesRemaining;
    private final Cache cache;
    private final CacheKeyFactory cacheKeyFactory;
    private final DataSource cacheReadDataSource;
    private final DataSource cacheWriteDataSource;
    private long checkCachePosition;
    private DataSource currentDataSource;
    private boolean currentDataSpecLengthUnset;
    private CacheSpan currentHoleSpan;
    private boolean currentRequestIgnoresCache;
    private final EventListener eventListener;
    private int flags;
    private byte[] httpBody;
    private int httpMethod;
    private Map<String, String> httpRequestHeaders;
    private final boolean ignoreCacheForUnsetLengthRequests;
    private final boolean ignoreCacheOnError;
    private String key;
    private long readPosition;
    private boolean seenCacheError;
    private long totalCachedBytesRead;
    private final DataSource upstreamDataSource;
    private Uri uri;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface CacheIgnoredReason {
    }

    /* loaded from: classes3.dex */
    public interface EventListener {
        void onCacheIgnored(int i);

        void onCachedBytesRead(long j, long j2);
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Flags {
    }

    public CacheDataSource(Cache cache, DataSource upstream) {
        this(cache, upstream, 0);
    }

    public CacheDataSource(Cache cache, DataSource upstream, int flags) {
        this(cache, upstream, new FileDataSource(), new CacheDataSink(cache, CacheDataSink.DEFAULT_FRAGMENT_SIZE), flags, null);
    }

    public CacheDataSource(Cache cache, DataSource upstream, DataSource cacheReadDataSource, DataSink cacheWriteDataSink, int flags, EventListener eventListener) {
        this(cache, upstream, cacheReadDataSource, cacheWriteDataSink, flags, eventListener, null);
    }

    public CacheDataSource(Cache cache, DataSource upstream, DataSource cacheReadDataSource, DataSink cacheWriteDataSink, int flags, EventListener eventListener, CacheKeyFactory cacheKeyFactory) {
        this.httpRequestHeaders = Collections.emptyMap();
        this.cache = cache;
        this.cacheReadDataSource = cacheReadDataSource;
        this.cacheKeyFactory = cacheKeyFactory != null ? cacheKeyFactory : CacheUtil.DEFAULT_CACHE_KEY_FACTORY;
        boolean z = false;
        this.blockOnCache = (flags & 1) != 0;
        this.ignoreCacheOnError = (flags & 2) != 0;
        this.ignoreCacheForUnsetLengthRequests = (flags & 4) != 0 ? true : z;
        this.upstreamDataSource = upstream;
        if (cacheWriteDataSink != null) {
            this.cacheWriteDataSource = new TeeDataSource(upstream, cacheWriteDataSink);
        } else {
            this.cacheWriteDataSource = null;
        }
        this.eventListener = eventListener;
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public void addTransferListener(TransferListener transferListener) {
        this.cacheReadDataSource.addTransferListener(transferListener);
        this.upstreamDataSource.addTransferListener(transferListener);
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public long open(DataSpec dataSpec) throws IOException {
        try {
            this.key = this.cacheKeyFactory.buildCacheKey(dataSpec);
            Uri uri = dataSpec.uri;
            this.uri = uri;
            this.actualUri = getRedirectedUriOrDefault(this.cache, this.key, uri);
            this.httpMethod = dataSpec.httpMethod;
            this.httpBody = dataSpec.httpBody;
            this.httpRequestHeaders = dataSpec.httpRequestHeaders;
            this.flags = dataSpec.flags;
            this.readPosition = dataSpec.position;
            int reason = shouldIgnoreCacheForRequest(dataSpec);
            boolean z = reason != -1;
            this.currentRequestIgnoresCache = z;
            if (z) {
                notifyCacheIgnored(reason);
            }
            if (dataSpec.length == -1 && !this.currentRequestIgnoresCache) {
                long contentLength = ContentMetadata.CC.getContentLength(this.cache.getContentMetadata(this.key));
                this.bytesRemaining = contentLength;
                if (contentLength != -1) {
                    long j = contentLength - dataSpec.position;
                    this.bytesRemaining = j;
                    if (j <= 0) {
                        throw new DataSourceException(0);
                    }
                }
                openNextSource(false);
                return this.bytesRemaining;
            }
            this.bytesRemaining = dataSpec.length;
            openNextSource(false);
            return this.bytesRemaining;
        } catch (Throwable e) {
            handleBeforeThrow(e);
            throw e;
        }
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public int read(byte[] buffer, int offset, int readLength) throws IOException {
        if (readLength == 0) {
            return 0;
        }
        if (this.bytesRemaining == 0) {
            return -1;
        }
        try {
            if (this.readPosition >= this.checkCachePosition) {
                openNextSource(true);
            }
            int bytesRead = this.currentDataSource.read(buffer, offset, readLength);
            if (bytesRead != -1) {
                if (isReadingFromCache()) {
                    this.totalCachedBytesRead += bytesRead;
                }
                this.readPosition += bytesRead;
                long j = this.bytesRemaining;
                if (j != -1) {
                    this.bytesRemaining = j - bytesRead;
                }
            } else if (this.currentDataSpecLengthUnset) {
                setNoBytesRemainingAndMaybeStoreLength();
            } else {
                long j2 = this.bytesRemaining;
                if (j2 <= 0) {
                    if (j2 == -1) {
                    }
                }
                closeCurrentSource();
                openNextSource(false);
                return read(buffer, offset, readLength);
            }
            return bytesRead;
        } catch (IOException e) {
            if (this.currentDataSpecLengthUnset && CacheUtil.isCausedByPositionOutOfRange(e)) {
                setNoBytesRemainingAndMaybeStoreLength();
                return -1;
            }
            handleBeforeThrow(e);
            throw e;
        } catch (Throwable e2) {
            handleBeforeThrow(e2);
            throw e2;
        }
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public Uri getUri() {
        return this.actualUri;
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public Map<String, List<String>> getResponseHeaders() {
        if (isReadingFromUpstream()) {
            return this.upstreamDataSource.getResponseHeaders();
        }
        return Collections.emptyMap();
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public void close() throws IOException {
        this.uri = null;
        this.actualUri = null;
        this.httpMethod = 1;
        this.httpBody = null;
        this.httpRequestHeaders = Collections.emptyMap();
        this.flags = 0;
        this.readPosition = 0L;
        this.key = null;
        notifyBytesRead();
        try {
            closeCurrentSource();
        } catch (Throwable e) {
            handleBeforeThrow(e);
            throw e;
        }
    }

    private void openNextSource(boolean checkCache) throws IOException {
        CacheSpan nextSpan;
        DataSource nextDataSource;
        DataSpec nextDataSpec;
        CacheSpan nextSpan2;
        long j;
        long length;
        long length2;
        if (this.currentRequestIgnoresCache) {
            nextSpan = null;
        } else if (this.blockOnCache) {
            try {
                nextSpan = this.cache.startReadWrite(this.key, this.readPosition);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new InterruptedIOException();
            }
        } else {
            nextSpan = this.cache.startReadWriteNonBlocking(this.key, this.readPosition);
        }
        if (nextSpan == null) {
            DataSource nextDataSource2 = this.upstreamDataSource;
            Uri uri = this.uri;
            int i = this.httpMethod;
            byte[] bArr = this.httpBody;
            long j2 = this.readPosition;
            nextDataSpec = new DataSpec(uri, i, bArr, j2, j2, this.bytesRemaining, this.key, this.flags, this.httpRequestHeaders);
            nextDataSource = nextDataSource2;
            nextSpan2 = nextSpan;
        } else if (nextSpan.isCached) {
            Uri fileUri = Uri.fromFile(nextSpan.file);
            long filePosition = this.readPosition - nextSpan.position;
            long length3 = nextSpan.length - filePosition;
            long j3 = this.bytesRemaining;
            if (j3 == -1) {
                length2 = length3;
            } else {
                length2 = Math.min(length3, j3);
            }
            nextDataSpec = new DataSpec(fileUri, this.readPosition, filePosition, length2, this.key, this.flags);
            nextDataSource = this.cacheReadDataSource;
            nextSpan2 = nextSpan;
        } else {
            if (nextSpan.isOpenEnded()) {
                length = this.bytesRemaining;
            } else {
                length = nextSpan.length;
                long j4 = this.bytesRemaining;
                if (j4 != -1) {
                    length = Math.min(length, j4);
                }
            }
            Uri uri2 = this.uri;
            int i2 = this.httpMethod;
            byte[] bArr2 = this.httpBody;
            long j5 = this.readPosition;
            DataSpec nextDataSpec2 = new DataSpec(uri2, i2, bArr2, j5, j5, length, this.key, this.flags, this.httpRequestHeaders);
            if (this.cacheWriteDataSource != null) {
                nextDataSource = this.cacheWriteDataSource;
                nextSpan2 = nextSpan;
                nextDataSpec = nextDataSpec2;
            } else {
                nextDataSource = this.upstreamDataSource;
                this.cache.releaseHoleSpan(nextSpan);
                nextSpan2 = null;
                nextDataSpec = nextDataSpec2;
            }
        }
        if (!this.currentRequestIgnoresCache && nextDataSource == this.upstreamDataSource) {
            j = this.readPosition + MIN_READ_BEFORE_CHECKING_CACHE;
        } else {
            j = Long.MAX_VALUE;
        }
        this.checkCachePosition = j;
        if (checkCache) {
            Assertions.checkState(isBypassingCache());
            if (nextDataSource == this.upstreamDataSource) {
                return;
            }
            try {
                closeCurrentSource();
            } catch (Throwable e2) {
                if (nextSpan2.isHoleSpan()) {
                    this.cache.releaseHoleSpan(nextSpan2);
                }
                throw e2;
            }
        }
        if (nextSpan2 != null && nextSpan2.isHoleSpan()) {
            this.currentHoleSpan = nextSpan2;
        }
        this.currentDataSource = nextDataSource;
        this.currentDataSpecLengthUnset = nextDataSpec.length == -1;
        long resolvedLength = nextDataSource.open(nextDataSpec);
        ContentMetadataMutations mutations = new ContentMetadataMutations();
        if (this.currentDataSpecLengthUnset && resolvedLength != -1) {
            this.bytesRemaining = resolvedLength;
            ContentMetadataMutations.setContentLength(mutations, this.readPosition + resolvedLength);
        }
        if (isReadingFromUpstream()) {
            Uri uri3 = this.currentDataSource.getUri();
            this.actualUri = uri3;
            boolean isRedirected = true ^ this.uri.equals(uri3);
            ContentMetadataMutations.setRedirectedUri(mutations, isRedirected ? this.actualUri : null);
        }
        boolean isRedirected2 = isWritingToCache();
        if (isRedirected2) {
            this.cache.applyContentMetadataMutations(this.key, mutations);
        }
    }

    private void setNoBytesRemainingAndMaybeStoreLength() throws IOException {
        this.bytesRemaining = 0L;
        if (isWritingToCache()) {
            ContentMetadataMutations mutations = new ContentMetadataMutations();
            ContentMetadataMutations.setContentLength(mutations, this.readPosition);
            this.cache.applyContentMetadataMutations(this.key, mutations);
        }
    }

    private static Uri getRedirectedUriOrDefault(Cache cache, String key, Uri defaultUri) {
        Uri redirectedUri = ContentMetadata.CC.getRedirectedUri(cache.getContentMetadata(key));
        return redirectedUri != null ? redirectedUri : defaultUri;
    }

    private boolean isReadingFromUpstream() {
        return !isReadingFromCache();
    }

    private boolean isBypassingCache() {
        return this.currentDataSource == this.upstreamDataSource;
    }

    private boolean isReadingFromCache() {
        return this.currentDataSource == this.cacheReadDataSource;
    }

    private boolean isWritingToCache() {
        return this.currentDataSource == this.cacheWriteDataSource;
    }

    /* JADX WARN: Type inference failed for: r2v0, types: [com.google.android.exoplayer2.upstream.cache.CacheSpan, com.google.android.exoplayer2.upstream.DataSource] */
    private void closeCurrentSource() throws IOException {
        DataSource dataSource = this.currentDataSource;
        if (dataSource == null) {
            return;
        }
        try {
            dataSource.close();
        } finally {
            this.currentDataSource = null;
            this.currentDataSpecLengthUnset = false;
            CacheSpan cacheSpan = this.currentHoleSpan;
            if (cacheSpan != null) {
                this.cache.releaseHoleSpan(cacheSpan);
                this.currentHoleSpan = null;
            }
        }
    }

    private void handleBeforeThrow(Throwable exception) {
        if (isReadingFromCache() || (exception instanceof Cache.CacheException)) {
            this.seenCacheError = true;
        }
    }

    private int shouldIgnoreCacheForRequest(DataSpec dataSpec) {
        if (this.ignoreCacheOnError && this.seenCacheError) {
            return 0;
        }
        if (this.ignoreCacheForUnsetLengthRequests && dataSpec.length == -1) {
            return 1;
        }
        return -1;
    }

    private void notifyCacheIgnored(int reason) {
        EventListener eventListener = this.eventListener;
        if (eventListener != null) {
            eventListener.onCacheIgnored(reason);
        }
    }

    private void notifyBytesRead() {
        EventListener eventListener = this.eventListener;
        if (eventListener != null && this.totalCachedBytesRead > 0) {
            eventListener.onCachedBytesRead(this.cache.getCacheSpace(), this.totalCachedBytesRead);
            this.totalCachedBytesRead = 0L;
        }
    }
}
