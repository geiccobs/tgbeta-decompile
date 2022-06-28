package com.google.android.exoplayer2.upstream.cache;

import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import java.io.File;
import java.util.TreeSet;
/* loaded from: classes3.dex */
public final class CachedContent {
    private static final String TAG = "CachedContent";
    private final TreeSet<SimpleCacheSpan> cachedSpans;
    public final int id;
    public final String key;
    private boolean locked;
    private DefaultContentMetadata metadata;

    public CachedContent(int id, String key) {
        this(id, key, DefaultContentMetadata.EMPTY);
    }

    public CachedContent(int id, String key, DefaultContentMetadata metadata) {
        this.id = id;
        this.key = key;
        this.metadata = metadata;
        this.cachedSpans = new TreeSet<>();
    }

    public DefaultContentMetadata getMetadata() {
        return this.metadata;
    }

    public boolean applyMetadataMutations(ContentMetadataMutations mutations) {
        DefaultContentMetadata oldMetadata = this.metadata;
        DefaultContentMetadata copyWithMutationsApplied = this.metadata.copyWithMutationsApplied(mutations);
        this.metadata = copyWithMutationsApplied;
        return !copyWithMutationsApplied.equals(oldMetadata);
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void addSpan(SimpleCacheSpan span) {
        this.cachedSpans.add(span);
    }

    public TreeSet<SimpleCacheSpan> getSpans() {
        return this.cachedSpans;
    }

    public SimpleCacheSpan getSpan(long position) {
        SimpleCacheSpan lookupSpan = SimpleCacheSpan.createLookup(this.key, position);
        SimpleCacheSpan floorSpan = this.cachedSpans.floor(lookupSpan);
        if (floorSpan != null && floorSpan.position + floorSpan.length > position) {
            return floorSpan;
        }
        SimpleCacheSpan ceilSpan = this.cachedSpans.ceiling(lookupSpan);
        return ceilSpan == null ? SimpleCacheSpan.createOpenHole(this.key, position) : SimpleCacheSpan.createClosedHole(this.key, position, ceilSpan.position - position);
    }

    public long getCachedBytesLength(long position, long length) {
        boolean z = true;
        Assertions.checkArgument(position >= 0);
        if (length < 0) {
            z = false;
        }
        Assertions.checkArgument(z);
        SimpleCacheSpan span = getSpan(position);
        if (span.isHoleSpan()) {
            return -Math.min(span.isOpenEnded() ? Long.MAX_VALUE : span.length, length);
        }
        long queryEndPosition = position + length;
        if (queryEndPosition < 0) {
            queryEndPosition = Long.MAX_VALUE;
        }
        long currentEndPosition = span.position + span.length;
        if (currentEndPosition < queryEndPosition) {
            for (SimpleCacheSpan next : this.cachedSpans.tailSet(span, false)) {
                if (next.position > currentEndPosition) {
                    break;
                }
                currentEndPosition = Math.max(currentEndPosition, next.position + next.length);
                if (currentEndPosition >= queryEndPosition) {
                    break;
                }
            }
        }
        return Math.min(currentEndPosition - position, length);
    }

    public SimpleCacheSpan setLastTouchTimestamp(SimpleCacheSpan cacheSpan, long lastTouchTimestamp, boolean updateFile) {
        Assertions.checkState(this.cachedSpans.remove(cacheSpan));
        File file = cacheSpan.file;
        if (updateFile) {
            File directory = file.getParentFile();
            long position = cacheSpan.position;
            File newFile = SimpleCacheSpan.getCacheFile(directory, this.id, position, lastTouchTimestamp);
            if (file.renameTo(newFile)) {
                file = newFile;
            } else {
                Log.w(TAG, "Failed to rename " + file + " to " + newFile);
            }
        }
        SimpleCacheSpan newCacheSpan = cacheSpan.copyWithFileAndLastTouchTimestamp(file, lastTouchTimestamp);
        this.cachedSpans.add(newCacheSpan);
        return newCacheSpan;
    }

    public boolean isEmpty() {
        return this.cachedSpans.isEmpty();
    }

    public boolean removeSpan(CacheSpan span) {
        if (this.cachedSpans.remove(span)) {
            span.file.delete();
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = this.id;
        return (((result * 31) + this.key.hashCode()) * 31) + this.metadata.hashCode();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CachedContent that = (CachedContent) o;
        return this.id == that.id && this.key.equals(that.key) && this.cachedSpans.equals(that.cachedSpans) && this.metadata.equals(that.metadata);
    }
}
