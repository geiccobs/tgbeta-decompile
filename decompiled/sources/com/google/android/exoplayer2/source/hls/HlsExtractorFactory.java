package com.google.android.exoplayer2.source.hls;

import android.net.Uri;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import java.io.IOException;
import java.util.List;
import java.util.Map;
/* loaded from: classes3.dex */
public interface HlsExtractorFactory {
    public static final HlsExtractorFactory DEFAULT = new DefaultHlsExtractorFactory();

    Result createExtractor(Extractor extractor, Uri uri, Format format, List<Format> list, TimestampAdjuster timestampAdjuster, Map<String, List<String>> map, ExtractorInput extractorInput) throws InterruptedException, IOException;

    /* loaded from: classes3.dex */
    public static final class Result {
        public final Extractor extractor;
        public final boolean isPackedAudioExtractor;
        public final boolean isReusable;

        public Result(Extractor extractor, boolean isPackedAudioExtractor, boolean isReusable) {
            this.extractor = extractor;
            this.isPackedAudioExtractor = isPackedAudioExtractor;
            this.isReusable = isReusable;
        }
    }
}
