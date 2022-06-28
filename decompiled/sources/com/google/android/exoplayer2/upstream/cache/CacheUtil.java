package com.google.android.exoplayer2.upstream.cache;

import android.net.Uri;
import android.util.Pair;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSourceException;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.ContentMetadata;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.PriorityTaskManager;
import com.google.android.exoplayer2.util.Util;
import java.io.EOFException;
import java.io.IOException;
import java.util.NavigableSet;
import java.util.concurrent.atomic.AtomicBoolean;
/* loaded from: classes3.dex */
public final class CacheUtil {
    public static final int DEFAULT_BUFFER_SIZE_BYTES = 131072;
    public static final CacheKeyFactory DEFAULT_CACHE_KEY_FACTORY = CacheUtil$$ExternalSyntheticLambda0.INSTANCE;

    /* loaded from: classes3.dex */
    public interface ProgressListener {
        void onProgress(long j, long j2, long j3);
    }

    public static /* synthetic */ String lambda$static$0(DataSpec dataSpec) {
        return dataSpec.key != null ? dataSpec.key : generateKey(dataSpec.uri);
    }

    public static String generateKey(Uri uri) {
        return uri.toString();
    }

    public static Pair<Long, Long> getCached(DataSpec dataSpec, Cache cache, CacheKeyFactory cacheKeyFactory) {
        String key = buildCacheKey(dataSpec, cacheKeyFactory);
        long position = dataSpec.absoluteStreamPosition;
        long requestLength = getRequestLength(dataSpec, cache, key);
        long position2 = position;
        long bytesAlreadyCached = 0;
        long bytesLeft = requestLength;
        while (true) {
            long j = 0;
            if (bytesLeft == 0) {
                break;
            }
            long blockLength = cache.getCachedLength(key, position2, bytesLeft != -1 ? bytesLeft : Long.MAX_VALUE);
            if (blockLength > 0) {
                bytesAlreadyCached += blockLength;
            } else {
                blockLength = -blockLength;
                if (blockLength == Long.MAX_VALUE) {
                    break;
                }
            }
            position2 += blockLength;
            if (bytesLeft != -1) {
                j = blockLength;
            }
            bytesLeft -= j;
        }
        return Pair.create(Long.valueOf(requestLength), Long.valueOf(bytesAlreadyCached));
    }

    public static void cache(DataSpec dataSpec, Cache cache, DataSource upstream, ProgressListener progressListener, AtomicBoolean isCanceled) throws IOException, InterruptedException {
        cache(dataSpec, cache, null, new CacheDataSource(cache, upstream), new byte[131072], null, 0, progressListener, isCanceled, false);
    }

    public static void cache(DataSpec dataSpec, Cache cache, CacheKeyFactory cacheKeyFactory, CacheDataSource dataSource, byte[] buffer, PriorityTaskManager priorityTaskManager, int priority, ProgressListener progressListener, AtomicBoolean isCanceled, boolean enableEOFException) throws IOException, InterruptedException {
        ProgressNotifier progressNotifier;
        long bytesLeft;
        Assertions.checkNotNull(dataSource);
        Assertions.checkNotNull(buffer);
        String key = buildCacheKey(dataSpec, cacheKeyFactory);
        if (progressListener == null) {
            bytesLeft = getRequestLength(dataSpec, cache, key);
            progressNotifier = null;
        } else {
            ProgressNotifier progressNotifier2 = new ProgressNotifier(progressListener);
            Pair<Long, Long> lengthAndBytesAlreadyCached = getCached(dataSpec, cache, cacheKeyFactory);
            progressNotifier2.init(((Long) lengthAndBytesAlreadyCached.first).longValue(), ((Long) lengthAndBytesAlreadyCached.second).longValue());
            bytesLeft = ((Long) lengthAndBytesAlreadyCached.first).longValue();
            progressNotifier = progressNotifier2;
        }
        long position = dataSpec.absoluteStreamPosition;
        boolean lengthUnset = bytesLeft == -1;
        long bytesLeft2 = bytesLeft;
        long position2 = position;
        while (bytesLeft2 != 0) {
            throwExceptionIfInterruptedOrCancelled(isCanceled);
            long read = cache.getCachedLength(key, position2, lengthUnset ? Long.MAX_VALUE : bytesLeft2);
            if (read <= 0) {
                long blockLength = -read;
                long length = blockLength == Long.MAX_VALUE ? -1L : blockLength;
                boolean isLastBlock = length == bytesLeft2;
                if (readAndDiscard(dataSpec, position2, length, dataSource, buffer, priorityTaskManager, priority, progressNotifier, isLastBlock, isCanceled) >= blockLength) {
                    read = blockLength;
                } else if (enableEOFException && !lengthUnset) {
                    throw new EOFException();
                } else {
                    return;
                }
            }
            position2 += read;
            if (!lengthUnset) {
                bytesLeft2 -= read;
            }
        }
    }

    private static long getRequestLength(DataSpec dataSpec, Cache cache, String key) {
        if (dataSpec.length != -1) {
            return dataSpec.length;
        }
        long contentLength = ContentMetadata.CC.getContentLength(cache.getContentMetadata(key));
        if (contentLength != -1) {
            return contentLength - dataSpec.absoluteStreamPosition;
        }
        return -1L;
    }

    private static long readAndDiscard(DataSpec dataSpec, long absoluteStreamPosition, long length, DataSource dataSource, byte[] buffer, PriorityTaskManager priorityTaskManager, int priority, ProgressNotifier progressNotifier, boolean isLastBlock, AtomicBoolean isCanceled) throws IOException, InterruptedException {
        long resolvedLength;
        boolean isDataSourceOpen;
        DataSpec dataSpec2 = dataSpec;
        long positionOffset = absoluteStreamPosition - dataSpec2.absoluteStreamPosition;
        long j = -1;
        long endOffset = length != -1 ? positionOffset + length : -1L;
        while (true) {
            if (priorityTaskManager != null) {
                priorityTaskManager.proceed(priority);
            }
            throwExceptionIfInterruptedOrCancelled(isCanceled);
            resolvedLength = -1;
            isDataSourceOpen = false;
            if (endOffset == j) {
                break;
            }
            try {
                try {
                    resolvedLength = dataSource.open(dataSpec2.subrange(positionOffset, endOffset - positionOffset));
                    isDataSourceOpen = true;
                    break;
                } catch (IOException exception) {
                    if (!isLastBlock) {
                        break;
                    }
                    try {
                        if (isCausedByPositionOutOfRange(exception)) {
                            Util.closeQuietly(dataSource);
                        }
                    } catch (PriorityTaskManager.PriorityTooLowException e) {
                        Util.closeQuietly(dataSource);
                        j = -1;
                        dataSpec2 = dataSpec;
                    }
                    throw exception;
                }
            } finally {
                Util.closeQuietly(dataSource);
            }
        }
        if (!isDataSourceOpen) {
            resolvedLength = dataSource.open(dataSpec2.subrange(positionOffset, -1L));
        }
        if (isLastBlock && progressNotifier != null && resolvedLength != -1) {
            progressNotifier.onRequestLengthResolved(positionOffset + resolvedLength);
        }
        while (true) {
            if (positionOffset == endOffset) {
                break;
            }
            throwExceptionIfInterruptedOrCancelled(isCanceled);
            int bytesRead = dataSource.read(buffer, 0, endOffset != -1 ? (int) Math.min(buffer.length, endOffset - positionOffset) : buffer.length);
            if (bytesRead != -1) {
                positionOffset += bytesRead;
                if (progressNotifier != null) {
                    progressNotifier.onBytesCached(bytesRead);
                }
            } else if (progressNotifier != null) {
                progressNotifier.onRequestLengthResolved(positionOffset);
            }
        }
        return positionOffset - positionOffset;
    }

    public static void remove(DataSpec dataSpec, Cache cache, CacheKeyFactory cacheKeyFactory) {
        remove(cache, buildCacheKey(dataSpec, cacheKeyFactory));
    }

    public static void remove(Cache cache, String key) {
        NavigableSet<CacheSpan> cachedSpans = cache.getCachedSpans(key);
        for (CacheSpan cachedSpan : cachedSpans) {
            try {
                cache.removeSpan(cachedSpan);
            } catch (Cache.CacheException e) {
            }
        }
    }

    public static boolean isCausedByPositionOutOfRange(IOException e) {
        for (Throwable cause = e; cause != null; cause = cause.getCause()) {
            if (cause instanceof DataSourceException) {
                int reason = ((DataSourceException) cause).reason;
                if (reason == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String buildCacheKey(DataSpec dataSpec, CacheKeyFactory cacheKeyFactory) {
        return (cacheKeyFactory != null ? cacheKeyFactory : DEFAULT_CACHE_KEY_FACTORY).buildCacheKey(dataSpec);
    }

    private static void throwExceptionIfInterruptedOrCancelled(AtomicBoolean isCanceled) throws InterruptedException {
        if (Thread.interrupted() || (isCanceled != null && isCanceled.get())) {
            throw new InterruptedException();
        }
    }

    private CacheUtil() {
    }

    /* loaded from: classes3.dex */
    public static final class ProgressNotifier {
        private long bytesCached;
        private final ProgressListener listener;
        private long requestLength;

        public ProgressNotifier(ProgressListener listener) {
            this.listener = listener;
        }

        public void init(long requestLength, long bytesCached) {
            this.requestLength = requestLength;
            this.bytesCached = bytesCached;
            this.listener.onProgress(requestLength, bytesCached, 0L);
        }

        public void onRequestLengthResolved(long requestLength) {
            if (this.requestLength == -1 && requestLength != -1) {
                this.requestLength = requestLength;
                this.listener.onProgress(requestLength, this.bytesCached, 0L);
            }
        }

        public void onBytesCached(long newBytesCached) {
            long j = this.bytesCached + newBytesCached;
            this.bytesCached = j;
            this.listener.onProgress(this.requestLength, j, newBytesCached);
        }
    }
}
