package com.google.android.exoplayer2.offline;

import android.net.Uri;
import android.util.Pair;
import com.google.android.exoplayer2.offline.Downloader;
import com.google.android.exoplayer2.offline.FilterableManifest;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheKeyFactory;
import com.google.android.exoplayer2.upstream.cache.CacheUtil;
import com.google.android.exoplayer2.util.PriorityTaskManager;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
/* loaded from: classes3.dex */
public abstract class SegmentDownloader<M extends FilterableManifest<M>> implements Downloader {
    private static final int BUFFER_SIZE_BYTES = 131072;
    private final Cache cache;
    private final CacheKeyFactory cacheKeyFactory;
    private final CacheDataSource dataSource;
    private final AtomicBoolean isCanceled = new AtomicBoolean();
    private final DataSpec manifestDataSpec;
    private final CacheDataSource offlineDataSource;
    private final PriorityTaskManager priorityTaskManager;
    private final ArrayList<StreamKey> streamKeys;

    protected abstract M getManifest(DataSource dataSource, DataSpec dataSpec) throws IOException;

    protected abstract List<Segment> getSegments(DataSource dataSource, M m, boolean z) throws InterruptedException, IOException;

    /* loaded from: classes3.dex */
    protected static class Segment implements Comparable<Segment> {
        public final DataSpec dataSpec;
        public final long startTimeUs;

        public Segment(long startTimeUs, DataSpec dataSpec) {
            this.startTimeUs = startTimeUs;
            this.dataSpec = dataSpec;
        }

        public int compareTo(Segment other) {
            return Util.compareLong(this.startTimeUs, other.startTimeUs);
        }
    }

    public SegmentDownloader(Uri manifestUri, List<StreamKey> streamKeys, DownloaderConstructorHelper constructorHelper) {
        this.manifestDataSpec = getCompressibleDataSpec(manifestUri);
        this.streamKeys = new ArrayList<>(streamKeys);
        this.cache = constructorHelper.getCache();
        this.dataSource = constructorHelper.createCacheDataSource();
        this.offlineDataSource = constructorHelper.createOfflineCacheDataSource();
        this.cacheKeyFactory = constructorHelper.getCacheKeyFactory();
        this.priorityTaskManager = constructorHelper.getPriorityTaskManager();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v8, types: [com.google.android.exoplayer2.offline.FilterableManifest] */
    @Override // com.google.android.exoplayer2.offline.Downloader
    public final void download(Downloader.ProgressListener progressListener) throws IOException, InterruptedException {
        this.priorityTaskManager.add(-1000);
        try {
            M manifest = getManifest(this.dataSource, this.manifestDataSpec);
            if (!this.streamKeys.isEmpty()) {
                manifest = (FilterableManifest) manifest.copy(this.streamKeys);
            }
            List<Segment> segments = getSegments(this.dataSource, manifest, false);
            int totalSegments = segments.size();
            int segmentsDownloaded = 0;
            long contentLength = 0;
            long bytesDownloaded = 0;
            for (int i = segments.size() - 1; i >= 0; i--) {
                Segment segment = segments.get(i);
                Pair<Long, Long> segmentLengthAndBytesDownloaded = CacheUtil.getCached(segment.dataSpec, this.cache, this.cacheKeyFactory);
                long segmentLength = ((Long) segmentLengthAndBytesDownloaded.first).longValue();
                long segmentBytesDownloaded = ((Long) segmentLengthAndBytesDownloaded.second).longValue();
                bytesDownloaded += segmentBytesDownloaded;
                if (segmentLength != -1) {
                    if (segmentLength == segmentBytesDownloaded) {
                        segmentsDownloaded++;
                        segments.remove(i);
                    }
                    if (contentLength != -1) {
                        contentLength += segmentLength;
                    }
                } else {
                    contentLength = -1;
                }
            }
            Collections.sort(segments);
            ProgressNotifier progressNotifier = null;
            if (progressListener != null) {
                progressNotifier = new ProgressNotifier(progressListener, contentLength, totalSegments, bytesDownloaded, segmentsDownloaded);
            }
            byte[] buffer = new byte[131072];
            for (int i2 = 0; i2 < segments.size(); i2++) {
                CacheUtil.cache(segments.get(i2).dataSpec, this.cache, this.cacheKeyFactory, this.dataSource, buffer, this.priorityTaskManager, -1000, progressNotifier, this.isCanceled, true);
                if (progressNotifier != null) {
                    progressNotifier.onSegmentDownloaded();
                }
            }
        } finally {
            this.priorityTaskManager.remove(-1000);
        }
    }

    @Override // com.google.android.exoplayer2.offline.Downloader
    public void cancel() {
        this.isCanceled.set(true);
    }

    @Override // com.google.android.exoplayer2.offline.Downloader
    public final void remove() throws InterruptedException {
        try {
            M manifest = getManifest(this.offlineDataSource, this.manifestDataSpec);
            List<Segment> segments = getSegments(this.offlineDataSource, manifest, true);
            for (int i = 0; i < segments.size(); i++) {
                removeDataSpec(segments.get(i).dataSpec);
            }
        } catch (IOException e) {
        } catch (Throwable th) {
            removeDataSpec(this.manifestDataSpec);
            throw th;
        }
        removeDataSpec(this.manifestDataSpec);
    }

    private void removeDataSpec(DataSpec dataSpec) {
        CacheUtil.remove(dataSpec, this.cache, this.cacheKeyFactory);
    }

    public static DataSpec getCompressibleDataSpec(Uri uri) {
        return new DataSpec(uri, 0L, -1L, null, 1);
    }

    /* loaded from: classes3.dex */
    private static final class ProgressNotifier implements CacheUtil.ProgressListener {
        private long bytesDownloaded;
        private final long contentLength;
        private final Downloader.ProgressListener progressListener;
        private int segmentsDownloaded;
        private final int totalSegments;

        public ProgressNotifier(Downloader.ProgressListener progressListener, long contentLength, int totalSegments, long bytesDownloaded, int segmentsDownloaded) {
            this.progressListener = progressListener;
            this.contentLength = contentLength;
            this.totalSegments = totalSegments;
            this.bytesDownloaded = bytesDownloaded;
            this.segmentsDownloaded = segmentsDownloaded;
        }

        @Override // com.google.android.exoplayer2.upstream.cache.CacheUtil.ProgressListener
        public void onProgress(long requestLength, long bytesCached, long newBytesCached) {
            long j = this.bytesDownloaded + newBytesCached;
            this.bytesDownloaded = j;
            this.progressListener.onProgress(this.contentLength, j, getPercentDownloaded());
        }

        public void onSegmentDownloaded() {
            this.segmentsDownloaded++;
            this.progressListener.onProgress(this.contentLength, this.bytesDownloaded, getPercentDownloaded());
        }

        private float getPercentDownloaded() {
            long j = this.contentLength;
            if (j != -1 && j != 0) {
                return (((float) this.bytesDownloaded) * 100.0f) / ((float) j);
            }
            int i = this.totalSegments;
            if (i != 0) {
                return (this.segmentsDownloaded * 100.0f) / i;
            }
            return -1.0f;
        }
    }
}
