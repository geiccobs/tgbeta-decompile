package com.google.android.exoplayer2.source.chunk;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.DefaultExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
/* loaded from: classes3.dex */
public final class SingleSampleMediaChunk extends BaseMediaChunk {
    private boolean loadCompleted;
    private long nextLoadPosition;
    private final Format sampleFormat;
    private final int trackType;

    public SingleSampleMediaChunk(DataSource dataSource, DataSpec dataSpec, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long startTimeUs, long endTimeUs, long chunkIndex, int trackType, Format sampleFormat) {
        super(dataSource, dataSpec, trackFormat, trackSelectionReason, trackSelectionData, startTimeUs, endTimeUs, C.TIME_UNSET, C.TIME_UNSET, chunkIndex);
        this.trackType = trackType;
        this.sampleFormat = sampleFormat;
    }

    @Override // com.google.android.exoplayer2.source.chunk.MediaChunk
    public boolean isLoadCompleted() {
        return this.loadCompleted;
    }

    @Override // com.google.android.exoplayer2.upstream.Loader.Loadable
    public void cancelLoad() {
    }

    @Override // com.google.android.exoplayer2.upstream.Loader.Loadable
    public void load() throws IOException, InterruptedException {
        long length;
        BaseMediaChunkOutput output = getOutput();
        output.setSampleOffsetUs(0L);
        TrackOutput trackOutput = output.track(0, this.trackType);
        trackOutput.format(this.sampleFormat);
        try {
            DataSpec loadDataSpec = this.dataSpec.subrange(this.nextLoadPosition);
            long length2 = this.dataSource.open(loadDataSpec);
            if (length2 == -1) {
                length = length2;
            } else {
                length = length2 + this.nextLoadPosition;
            }
            ExtractorInput extractorInput = new DefaultExtractorInput(this.dataSource, this.nextLoadPosition, length);
            for (int result = 0; result != -1; result = trackOutput.sampleData(extractorInput, Integer.MAX_VALUE, true)) {
                this.nextLoadPosition += result;
            }
            int sampleSize = (int) this.nextLoadPosition;
            trackOutput.sampleMetadata(this.startTimeUs, 1, sampleSize, 0, null);
            Util.closeQuietly(this.dataSource);
            this.loadCompleted = true;
        } catch (Throwable th) {
            Util.closeQuietly(this.dataSource);
            throw th;
        }
    }
}
