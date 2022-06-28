package com.google.android.exoplayer2.source.chunk;

import android.util.SparseArray;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.DummyTrackOutput;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.io.IOException;
/* loaded from: classes3.dex */
public final class ChunkExtractorWrapper implements ExtractorOutput {
    private final SparseArray<BindingTrackOutput> bindingTrackOutputs = new SparseArray<>();
    private long endTimeUs;
    public final Extractor extractor;
    private boolean extractorInitialized;
    private final Format primaryTrackManifestFormat;
    private final int primaryTrackType;
    private Format[] sampleFormats;
    private SeekMap seekMap;
    private TrackOutputProvider trackOutputProvider;

    /* loaded from: classes3.dex */
    public interface TrackOutputProvider {
        TrackOutput track(int i, int i2);
    }

    public ChunkExtractorWrapper(Extractor extractor, int primaryTrackType, Format primaryTrackManifestFormat) {
        this.extractor = extractor;
        this.primaryTrackType = primaryTrackType;
        this.primaryTrackManifestFormat = primaryTrackManifestFormat;
    }

    public SeekMap getSeekMap() {
        return this.seekMap;
    }

    public Format[] getSampleFormats() {
        return this.sampleFormats;
    }

    public void init(TrackOutputProvider trackOutputProvider, long startTimeUs, long endTimeUs) {
        this.trackOutputProvider = trackOutputProvider;
        this.endTimeUs = endTimeUs;
        if (!this.extractorInitialized) {
            this.extractor.init(this);
            if (startTimeUs != C.TIME_UNSET) {
                this.extractor.seek(0L, startTimeUs);
            }
            this.extractorInitialized = true;
            return;
        }
        this.extractor.seek(0L, startTimeUs == C.TIME_UNSET ? 0L : startTimeUs);
        for (int i = 0; i < this.bindingTrackOutputs.size(); i++) {
            this.bindingTrackOutputs.valueAt(i).bind(trackOutputProvider, endTimeUs);
        }
    }

    @Override // com.google.android.exoplayer2.extractor.ExtractorOutput
    public TrackOutput track(int id, int type) {
        BindingTrackOutput bindingTrackOutput = this.bindingTrackOutputs.get(id);
        if (bindingTrackOutput == null) {
            Assertions.checkState(this.sampleFormats == null);
            BindingTrackOutput bindingTrackOutput2 = new BindingTrackOutput(id, type, type == this.primaryTrackType ? this.primaryTrackManifestFormat : null);
            bindingTrackOutput2.bind(this.trackOutputProvider, this.endTimeUs);
            this.bindingTrackOutputs.put(id, bindingTrackOutput2);
            return bindingTrackOutput2;
        }
        return bindingTrackOutput;
    }

    @Override // com.google.android.exoplayer2.extractor.ExtractorOutput
    public void endTracks() {
        Format[] sampleFormats = new Format[this.bindingTrackOutputs.size()];
        for (int i = 0; i < this.bindingTrackOutputs.size(); i++) {
            sampleFormats[i] = this.bindingTrackOutputs.valueAt(i).sampleFormat;
        }
        this.sampleFormats = sampleFormats;
    }

    @Override // com.google.android.exoplayer2.extractor.ExtractorOutput
    public void seekMap(SeekMap seekMap) {
        this.seekMap = seekMap;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static final class BindingTrackOutput implements TrackOutput {
        private final DummyTrackOutput dummyTrackOutput = new DummyTrackOutput();
        private long endTimeUs;
        private final int id;
        private final Format manifestFormat;
        public Format sampleFormat;
        private TrackOutput trackOutput;
        private final int type;

        public BindingTrackOutput(int id, int type, Format manifestFormat) {
            this.id = id;
            this.type = type;
            this.manifestFormat = manifestFormat;
        }

        public void bind(TrackOutputProvider trackOutputProvider, long endTimeUs) {
            if (trackOutputProvider == null) {
                this.trackOutput = this.dummyTrackOutput;
                return;
            }
            this.endTimeUs = endTimeUs;
            TrackOutput track = trackOutputProvider.track(this.id, this.type);
            this.trackOutput = track;
            Format format = this.sampleFormat;
            if (format != null) {
                track.format(format);
            }
        }

        @Override // com.google.android.exoplayer2.extractor.TrackOutput
        public void format(Format format) {
            Format format2 = this.manifestFormat;
            Format copyWithManifestFormatInfo = format2 != null ? format.copyWithManifestFormatInfo(format2) : format;
            this.sampleFormat = copyWithManifestFormatInfo;
            this.trackOutput.format(copyWithManifestFormatInfo);
        }

        @Override // com.google.android.exoplayer2.extractor.TrackOutput
        public int sampleData(ExtractorInput input, int length, boolean allowEndOfInput) throws IOException, InterruptedException {
            return this.trackOutput.sampleData(input, length, allowEndOfInput);
        }

        @Override // com.google.android.exoplayer2.extractor.TrackOutput
        public void sampleData(ParsableByteArray data, int length) {
            this.trackOutput.sampleData(data, length);
        }

        @Override // com.google.android.exoplayer2.extractor.TrackOutput
        public void sampleMetadata(long timeUs, int flags, int size, int offset, TrackOutput.CryptoData cryptoData) {
            long j = this.endTimeUs;
            if (j != C.TIME_UNSET && timeUs >= j) {
                this.trackOutput = this.dummyTrackOutput;
            }
            this.trackOutput.sampleMetadata(timeUs, flags, size, offset, cryptoData);
        }
    }
}
