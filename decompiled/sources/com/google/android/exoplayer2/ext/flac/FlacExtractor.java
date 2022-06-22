package com.google.android.exoplayer2.ext.flac;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ext.flac.FlacBinarySearchSeeker;
import com.google.android.exoplayer2.ext.flac.FlacDecoderJni;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.FlacMetadataReader;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.SeekPoint;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.FlacStreamMetadata;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;
/* loaded from: classes.dex */
public final class FlacExtractor implements Extractor {
    public static final ExtractorsFactory FACTORY = FlacExtractor$$ExternalSyntheticLambda0.INSTANCE;
    public static final int FLAG_DISABLE_ID3_METADATA = 1;
    private FlacBinarySearchSeeker binarySearchSeeker;
    private FlacDecoderJni decoderJni;
    private ExtractorOutput extractorOutput;
    private Metadata id3Metadata;
    private final boolean id3MetadataDisabled;
    private final ParsableByteArray outputBuffer;
    private FlacBinarySearchSeeker.OutputFrameHolder outputFrameHolder;
    private FlacStreamMetadata streamMetadata;
    private boolean streamMetadataDecoded;
    private TrackOutput trackOutput;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Flags {
    }

    public static /* synthetic */ Extractor[] lambda$static$0() {
        return new Extractor[]{new FlacExtractor()};
    }

    public FlacExtractor() {
        this(0);
    }

    public FlacExtractor(int i) {
        this.outputBuffer = new ParsableByteArray();
        this.id3MetadataDisabled = (i & 1) == 0 ? false : true;
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public void init(ExtractorOutput extractorOutput) {
        this.extractorOutput = extractorOutput;
        this.trackOutput = extractorOutput.track(0, 1);
        this.extractorOutput.endTracks();
        try {
            this.decoderJni = new FlacDecoderJni();
        } catch (FlacDecoderException e) {
            throw new RuntimeException(e);
        }
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public boolean sniff(ExtractorInput extractorInput) throws IOException, InterruptedException {
        this.id3Metadata = FlacMetadataReader.peekId3Metadata(extractorInput, !this.id3MetadataDisabled);
        return FlacMetadataReader.checkAndPeekStreamMarker(extractorInput);
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public int read(ExtractorInput extractorInput, PositionHolder positionHolder) throws IOException, InterruptedException {
        if (extractorInput.getPosition() == 0 && !this.id3MetadataDisabled && this.id3Metadata == null) {
            this.id3Metadata = FlacMetadataReader.peekId3Metadata(extractorInput, true);
        }
        FlacDecoderJni initDecoderJni = initDecoderJni(extractorInput);
        try {
            decodeStreamMetadata(extractorInput);
            FlacBinarySearchSeeker flacBinarySearchSeeker = this.binarySearchSeeker;
            if (flacBinarySearchSeeker != null && flacBinarySearchSeeker.isSeeking()) {
                return handlePendingSeek(extractorInput, positionHolder, this.outputBuffer, this.outputFrameHolder, this.trackOutput);
            }
            ByteBuffer byteBuffer = this.outputFrameHolder.byteBuffer;
            long decodePosition = initDecoderJni.getDecodePosition();
            try {
                initDecoderJni.decodeSampleWithBacktrackPosition(byteBuffer, decodePosition);
                int limit = byteBuffer.limit();
                int i = -1;
                if (limit == 0) {
                    return -1;
                }
                outputSample(this.outputBuffer, limit, initDecoderJni.getLastFrameTimestamp(), this.trackOutput);
                if (!initDecoderJni.isEndOfData()) {
                    i = 0;
                }
                return i;
            } catch (FlacDecoderJni.FlacFrameDecodeException e) {
                throw new IOException("Cannot read frame at position " + decodePosition, e);
            }
        } finally {
            initDecoderJni.clearData();
        }
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public void seek(long j, long j2) {
        if (j == 0) {
            this.streamMetadataDecoded = false;
        }
        FlacDecoderJni flacDecoderJni = this.decoderJni;
        if (flacDecoderJni != null) {
            flacDecoderJni.reset(j);
        }
        FlacBinarySearchSeeker flacBinarySearchSeeker = this.binarySearchSeeker;
        if (flacBinarySearchSeeker != null) {
            flacBinarySearchSeeker.setSeekTargetUs(j2);
        }
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public void release() {
        this.binarySearchSeeker = null;
        FlacDecoderJni flacDecoderJni = this.decoderJni;
        if (flacDecoderJni != null) {
            flacDecoderJni.release();
            this.decoderJni = null;
        }
    }

    @EnsuresNonNull({"decoderJni", "extractorOutput", "trackOutput"})
    private FlacDecoderJni initDecoderJni(ExtractorInput extractorInput) {
        FlacDecoderJni flacDecoderJni = (FlacDecoderJni) Assertions.checkNotNull(this.decoderJni);
        flacDecoderJni.setData(extractorInput);
        return flacDecoderJni;
    }

    @EnsuresNonNull({"streamMetadata", "outputFrameHolder"})
    @RequiresNonNull({"decoderJni", "extractorOutput", "trackOutput"})
    private void decodeStreamMetadata(ExtractorInput extractorInput) throws InterruptedException, IOException {
        if (this.streamMetadataDecoded) {
            return;
        }
        FlacDecoderJni flacDecoderJni = this.decoderJni;
        try {
            FlacStreamMetadata decodeStreamMetadata = flacDecoderJni.decodeStreamMetadata();
            this.streamMetadataDecoded = true;
            if (this.streamMetadata != null) {
                return;
            }
            this.streamMetadata = decodeStreamMetadata;
            this.outputBuffer.reset(decodeStreamMetadata.getMaxDecodedFrameSize());
            this.outputFrameHolder = new FlacBinarySearchSeeker.OutputFrameHolder(ByteBuffer.wrap(this.outputBuffer.data));
            this.binarySearchSeeker = outputSeekMap(flacDecoderJni, decodeStreamMetadata, extractorInput.getLength(), this.extractorOutput, this.outputFrameHolder);
            outputFormat(decodeStreamMetadata, decodeStreamMetadata.getMetadataCopyWithAppendedEntriesFrom(this.id3Metadata), this.trackOutput);
        } catch (IOException e) {
            flacDecoderJni.reset(0L);
            extractorInput.setRetryPosition(0L, e);
            throw e;
        }
    }

    @RequiresNonNull({"binarySearchSeeker"})
    private int handlePendingSeek(ExtractorInput extractorInput, PositionHolder positionHolder, ParsableByteArray parsableByteArray, FlacBinarySearchSeeker.OutputFrameHolder outputFrameHolder, TrackOutput trackOutput) throws InterruptedException, IOException {
        int handlePendingSeek = this.binarySearchSeeker.handlePendingSeek(extractorInput, positionHolder);
        ByteBuffer byteBuffer = outputFrameHolder.byteBuffer;
        if (handlePendingSeek == 0 && byteBuffer.limit() > 0) {
            outputSample(parsableByteArray, byteBuffer.limit(), outputFrameHolder.timeUs, trackOutput);
        }
        return handlePendingSeek;
    }

    private static FlacBinarySearchSeeker outputSeekMap(FlacDecoderJni flacDecoderJni, FlacStreamMetadata flacStreamMetadata, long j, ExtractorOutput extractorOutput, FlacBinarySearchSeeker.OutputFrameHolder outputFrameHolder) {
        SeekMap seekMap;
        FlacBinarySearchSeeker flacBinarySearchSeeker = null;
        if (flacDecoderJni.getSeekPoints(0L) != null) {
            seekMap = new FlacSeekMap(flacStreamMetadata.getDurationUs(), flacDecoderJni);
        } else if (j != -1) {
            flacBinarySearchSeeker = new FlacBinarySearchSeeker(flacStreamMetadata, flacDecoderJni.getDecodePosition(), j, flacDecoderJni, outputFrameHolder);
            seekMap = flacBinarySearchSeeker.getSeekMap();
        } else {
            seekMap = new SeekMap.Unseekable(flacStreamMetadata.getDurationUs());
        }
        extractorOutput.seekMap(seekMap);
        return flacBinarySearchSeeker;
    }

    private static void outputFormat(FlacStreamMetadata flacStreamMetadata, Metadata metadata, TrackOutput trackOutput) {
        trackOutput.format(Format.createAudioSampleFormat(null, "audio/raw", null, flacStreamMetadata.getBitRate(), flacStreamMetadata.getMaxDecodedFrameSize(), flacStreamMetadata.channels, flacStreamMetadata.sampleRate, Util.getPcmEncoding(flacStreamMetadata.bitsPerSample), 0, 0, null, null, 0, null, metadata));
    }

    private static void outputSample(ParsableByteArray parsableByteArray, int i, long j, TrackOutput trackOutput) {
        parsableByteArray.setPosition(0);
        trackOutput.sampleData(parsableByteArray, i);
        trackOutput.sampleMetadata(j, 1, i, 0, null);
    }

    /* loaded from: classes.dex */
    public static final class FlacSeekMap implements SeekMap {
        private final FlacDecoderJni decoderJni;
        private final long durationUs;

        @Override // com.google.android.exoplayer2.extractor.SeekMap
        public boolean isSeekable() {
            return true;
        }

        public FlacSeekMap(long j, FlacDecoderJni flacDecoderJni) {
            this.durationUs = j;
            this.decoderJni = flacDecoderJni;
        }

        @Override // com.google.android.exoplayer2.extractor.SeekMap
        public SeekMap.SeekPoints getSeekPoints(long j) {
            SeekMap.SeekPoints seekPoints = this.decoderJni.getSeekPoints(j);
            return seekPoints == null ? new SeekMap.SeekPoints(SeekPoint.START) : seekPoints;
        }

        @Override // com.google.android.exoplayer2.extractor.SeekMap
        public long getDurationUs() {
            return this.durationUs;
        }
    }
}
