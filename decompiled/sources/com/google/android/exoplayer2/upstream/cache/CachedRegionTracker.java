package com.google.android.exoplayer2.upstream.cache;

import com.google.android.exoplayer2.extractor.ChunkIndex;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeSet;
/* loaded from: classes3.dex */
public final class CachedRegionTracker implements Cache.Listener {
    public static final int CACHED_TO_END = -2;
    public static final int NOT_CACHED = -1;
    private static final String TAG = "CachedRegionTracker";
    private final Cache cache;
    private final String cacheKey;
    private final ChunkIndex chunkIndex;
    private final TreeSet<Region> regions = new TreeSet<>();
    private final Region lookupRegion = new Region(0, 0);

    public CachedRegionTracker(Cache cache, String cacheKey, ChunkIndex chunkIndex) {
        this.cache = cache;
        this.cacheKey = cacheKey;
        this.chunkIndex = chunkIndex;
        synchronized (this) {
            NavigableSet<CacheSpan> cacheSpans = cache.addListener(cacheKey, this);
            Iterator<CacheSpan> spanIterator = cacheSpans.descendingIterator();
            while (spanIterator.hasNext()) {
                CacheSpan span = spanIterator.next();
                mergeSpan(span);
            }
        }
    }

    public void release() {
        this.cache.removeListener(this.cacheKey, this);
    }

    public synchronized int getRegionEndTimeMs(long byteOffset) {
        this.lookupRegion.startOffset = byteOffset;
        Region floorRegion = this.regions.floor(this.lookupRegion);
        if (floorRegion != null && byteOffset <= floorRegion.endOffset && floorRegion.endOffsetIndex != -1) {
            int index = floorRegion.endOffsetIndex;
            if (index == this.chunkIndex.length - 1) {
                if (floorRegion.endOffset == this.chunkIndex.offsets[index] + this.chunkIndex.sizes[index]) {
                    return -2;
                }
            }
            long segmentFractionUs = (this.chunkIndex.durationsUs[index] * (floorRegion.endOffset - this.chunkIndex.offsets[index])) / this.chunkIndex.sizes[index];
            return (int) ((this.chunkIndex.timesUs[index] + segmentFractionUs) / 1000);
        }
        return -1;
    }

    @Override // com.google.android.exoplayer2.upstream.cache.Cache.Listener
    public synchronized void onSpanAdded(Cache cache, CacheSpan span) {
        mergeSpan(span);
    }

    @Override // com.google.android.exoplayer2.upstream.cache.Cache.Listener
    public synchronized void onSpanRemoved(Cache cache, CacheSpan span) {
        Region removedRegion = new Region(span.position, span.position + span.length);
        Region floorRegion = this.regions.floor(removedRegion);
        if (floorRegion == null) {
            Log.e(TAG, "Removed a span we were not aware of");
            return;
        }
        this.regions.remove(floorRegion);
        if (floorRegion.startOffset < removedRegion.startOffset) {
            Region newFloorRegion = new Region(floorRegion.startOffset, removedRegion.startOffset);
            int index = Arrays.binarySearch(this.chunkIndex.offsets, newFloorRegion.endOffset);
            newFloorRegion.endOffsetIndex = index < 0 ? (-index) - 2 : index;
            this.regions.add(newFloorRegion);
        }
        if (floorRegion.endOffset > removedRegion.endOffset) {
            Region newCeilingRegion = new Region(removedRegion.endOffset + 1, floorRegion.endOffset);
            newCeilingRegion.endOffsetIndex = floorRegion.endOffsetIndex;
            this.regions.add(newCeilingRegion);
        }
    }

    @Override // com.google.android.exoplayer2.upstream.cache.Cache.Listener
    public void onSpanTouched(Cache cache, CacheSpan oldSpan, CacheSpan newSpan) {
    }

    private void mergeSpan(CacheSpan span) {
        Region newRegion = new Region(span.position, span.position + span.length);
        Region floorRegion = this.regions.floor(newRegion);
        Region ceilingRegion = this.regions.ceiling(newRegion);
        boolean floorConnects = regionsConnect(floorRegion, newRegion);
        boolean ceilingConnects = regionsConnect(newRegion, ceilingRegion);
        if (ceilingConnects) {
            if (floorConnects) {
                floorRegion.endOffset = ceilingRegion.endOffset;
                floorRegion.endOffsetIndex = ceilingRegion.endOffsetIndex;
            } else {
                newRegion.endOffset = ceilingRegion.endOffset;
                newRegion.endOffsetIndex = ceilingRegion.endOffsetIndex;
                this.regions.add(newRegion);
            }
            this.regions.remove(ceilingRegion);
        } else if (floorConnects) {
            floorRegion.endOffset = newRegion.endOffset;
            int index = floorRegion.endOffsetIndex;
            while (index < this.chunkIndex.length - 1 && this.chunkIndex.offsets[index + 1] <= floorRegion.endOffset) {
                index++;
            }
            floorRegion.endOffsetIndex = index;
        } else {
            int index2 = Arrays.binarySearch(this.chunkIndex.offsets, newRegion.endOffset);
            newRegion.endOffsetIndex = index2 < 0 ? (-index2) - 2 : index2;
            this.regions.add(newRegion);
        }
    }

    private boolean regionsConnect(Region lower, Region upper) {
        return (lower == null || upper == null || lower.endOffset != upper.startOffset) ? false : true;
    }

    /* loaded from: classes3.dex */
    public static class Region implements Comparable<Region> {
        public long endOffset;
        public int endOffsetIndex;
        public long startOffset;

        public Region(long position, long endOffset) {
            this.startOffset = position;
            this.endOffset = endOffset;
        }

        public int compareTo(Region another) {
            return Util.compareLong(this.startOffset, another.startOffset);
        }
    }
}
