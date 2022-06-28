package com.google.android.exoplayer2.source.chunk;

import com.google.android.exoplayer2.upstream.DataSpec;
import java.util.List;
/* loaded from: classes3.dex */
public final class MediaChunkListIterator extends BaseMediaChunkIterator {
    private final List<? extends MediaChunk> chunks;
    private final boolean reverseOrder;

    public MediaChunkListIterator(List<? extends MediaChunk> chunks, boolean reverseOrder) {
        super(0L, chunks.size() - 1);
        this.chunks = chunks;
        this.reverseOrder = reverseOrder;
    }

    @Override // com.google.android.exoplayer2.source.chunk.MediaChunkIterator
    public DataSpec getDataSpec() {
        return getCurrentChunk().dataSpec;
    }

    @Override // com.google.android.exoplayer2.source.chunk.MediaChunkIterator
    public long getChunkStartTimeUs() {
        return getCurrentChunk().startTimeUs;
    }

    @Override // com.google.android.exoplayer2.source.chunk.MediaChunkIterator
    public long getChunkEndTimeUs() {
        return getCurrentChunk().endTimeUs;
    }

    private MediaChunk getCurrentChunk() {
        int index = (int) super.getCurrentIndex();
        if (this.reverseOrder) {
            index = (this.chunks.size() - 1) - index;
        }
        return this.chunks.get(index);
    }
}
