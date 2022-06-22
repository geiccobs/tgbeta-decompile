package com.google.android.exoplayer2.extractor;

import java.io.IOException;
/* loaded from: classes.dex */
public interface Extractor {
    void init(ExtractorOutput extractorOutput);

    int read(ExtractorInput extractorInput, PositionHolder positionHolder) throws IOException, InterruptedException;

    void release();

    void seek(long j, long j2);

    boolean sniff(ExtractorInput extractorInput) throws IOException, InterruptedException;
}
