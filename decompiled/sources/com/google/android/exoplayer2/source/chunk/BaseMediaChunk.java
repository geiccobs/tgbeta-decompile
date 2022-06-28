package com.google.android.exoplayer2.source.chunk;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
/* loaded from: classes3.dex */
public abstract class BaseMediaChunk extends MediaChunk {
    public final long clippedEndTimeUs;
    public final long clippedStartTimeUs;
    private int[] firstSampleIndices;
    private BaseMediaChunkOutput output;

    public BaseMediaChunk(DataSource dataSource, DataSpec dataSpec, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long startTimeUs, long endTimeUs, long clippedStartTimeUs, long clippedEndTimeUs, long chunkIndex) {
        super(dataSource, dataSpec, trackFormat, trackSelectionReason, trackSelectionData, startTimeUs, endTimeUs, chunkIndex);
        this.clippedStartTimeUs = clippedStartTimeUs;
        this.clippedEndTimeUs = clippedEndTimeUs;
    }

    public void init(BaseMediaChunkOutput output) {
        this.output = output;
        this.firstSampleIndices = output.getWriteIndices();
    }

    public final int getFirstSampleIndex(int trackIndex) {
        return this.firstSampleIndices[trackIndex];
    }

    public final BaseMediaChunkOutput getOutput() {
        return this.output;
    }
}
