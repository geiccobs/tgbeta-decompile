package com.google.android.exoplayer2.source.chunk;

import com.google.android.exoplayer2.extractor.DummyTrackOutput;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.source.SampleQueue;
import com.google.android.exoplayer2.source.chunk.ChunkExtractorWrapper;
import com.google.android.exoplayer2.util.Log;
/* loaded from: classes3.dex */
public final class BaseMediaChunkOutput implements ChunkExtractorWrapper.TrackOutputProvider {
    private static final String TAG = "BaseMediaChunkOutput";
    private final SampleQueue[] sampleQueues;
    private final int[] trackTypes;

    public BaseMediaChunkOutput(int[] trackTypes, SampleQueue[] sampleQueues) {
        this.trackTypes = trackTypes;
        this.sampleQueues = sampleQueues;
    }

    @Override // com.google.android.exoplayer2.source.chunk.ChunkExtractorWrapper.TrackOutputProvider
    public TrackOutput track(int id, int type) {
        int i = 0;
        while (true) {
            int[] iArr = this.trackTypes;
            if (i < iArr.length) {
                if (type != iArr[i]) {
                    i++;
                } else {
                    return this.sampleQueues[i];
                }
            } else {
                Log.e(TAG, "Unmatched track of type: " + type);
                return new DummyTrackOutput();
            }
        }
    }

    public int[] getWriteIndices() {
        int[] writeIndices = new int[this.sampleQueues.length];
        int i = 0;
        while (true) {
            SampleQueue[] sampleQueueArr = this.sampleQueues;
            if (i < sampleQueueArr.length) {
                if (sampleQueueArr[i] != null) {
                    writeIndices[i] = sampleQueueArr[i].getWriteIndex();
                }
                i++;
            } else {
                return writeIndices;
            }
        }
    }

    public void setSampleOffsetUs(long sampleOffsetUs) {
        SampleQueue[] sampleQueueArr;
        for (SampleQueue sampleQueue : this.sampleQueues) {
            if (sampleQueue != null) {
                sampleQueue.setSampleOffsetUs(sampleOffsetUs);
            }
        }
    }
}
