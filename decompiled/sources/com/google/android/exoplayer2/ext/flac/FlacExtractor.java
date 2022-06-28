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
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;
/* loaded from: classes3.dex */
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

    public FlacExtractor(int flags) {
        this.outputBuffer = new ParsableByteArray();
        this.id3MetadataDisabled = (flags & 1) != 0;
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public void init(ExtractorOutput output) {
        this.extractorOutput = output;
        this.trackOutput = output.track(0, 1);
        this.extractorOutput.endTracks();
        try {
            this.decoderJni = new FlacDecoderJni();
        } catch (FlacDecoderException e) {
            throw new RuntimeException(e);
        }
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        this.id3Metadata = FlacMetadataReader.peekId3Metadata(input, !this.id3MetadataDisabled);
        return FlacMetadataReader.checkAndPeekStreamMarker(input);
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        if (input.getPosition() == 0 && !this.id3MetadataDisabled && this.id3Metadata == null) {
            this.id3Metadata = FlacMetadataReader.peekId3Metadata(input, true);
        }
        FlacDecoderJni decoderJni = initDecoderJni(input);
        try {
            decodeStreamMetadata(input);
            FlacBinarySearchSeeker flacBinarySearchSeeker = this.binarySearchSeeker;
            if (flacBinarySearchSeeker != null && flacBinarySearchSeeker.isSeeking()) {
                return handlePendingSeek(input, seekPosition, this.outputBuffer, this.outputFrameHolder, this.trackOutput);
            }
            ByteBuffer outputByteBuffer = this.outputFrameHolder.byteBuffer;
            long lastDecodePosition = decoderJni.getDecodePosition();
            try {
                decoderJni.decodeSampleWithBacktrackPosition(outputByteBuffer, lastDecodePosition);
                int outputSize = outputByteBuffer.limit();
                int i = -1;
                if (outputSize == 0) {
                    return -1;
                }
                outputSample(this.outputBuffer, outputSize, decoderJni.getLastFrameTimestamp(), this.trackOutput);
                if (!decoderJni.isEndOfData()) {
                    i = 0;
                }
                return i;
            } catch (FlacDecoderJni.FlacFrameDecodeException e) {
                throw new IOException("Cannot read frame at position " + lastDecodePosition, e);
            }
        } finally {
            decoderJni.clearData();
        }
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public void seek(long position, long timeUs) {
        if (position == 0) {
            this.streamMetadataDecoded = false;
        }
        FlacDecoderJni flacDecoderJni = this.decoderJni;
        if (flacDecoderJni != null) {
            flacDecoderJni.reset(position);
        }
        FlacBinarySearchSeeker flacBinarySearchSeeker = this.binarySearchSeeker;
        if (flacBinarySearchSeeker != null) {
            flacBinarySearchSeeker.setSeekTargetUs(timeUs);
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
    private FlacDecoderJni initDecoderJni(ExtractorInput input) {
        FlacDecoderJni decoderJni = (FlacDecoderJni) Assertions.checkNotNull(this.decoderJni);
        decoderJni.setData(input);
        return decoderJni;
    }

    @EnsuresNonNull({"streamMetadata", "outputFrameHolder"})
    @RequiresNonNull({"decoderJni", "extractorOutput", "trackOutput"})
    private void decodeStreamMetadata(ExtractorInput input) throws InterruptedException, IOException {
        if (this.streamMetadataDecoded) {
            return;
        }
        FlacDecoderJni flacDecoderJni = this.decoderJni;
        try {
            FlacStreamMetadata streamMetadata = flacDecoderJni.decodeStreamMetadata();
            this.streamMetadataDecoded = true;
            if (this.streamMetadata == null) {
                this.streamMetadata = streamMetadata;
                this.outputBuffer.reset(streamMetadata.getMaxDecodedFrameSize());
                this.outputFrameHolder = new FlacBinarySearchSeeker.OutputFrameHolder(ByteBuffer.wrap(this.outputBuffer.data));
                this.binarySearchSeeker = outputSeekMap(flacDecoderJni, streamMetadata, input.getLength(), this.extractorOutput, this.outputFrameHolder);
                Metadata metadata = streamMetadata.getMetadataCopyWithAppendedEntriesFrom(this.id3Metadata);
                outputFormat(streamMetadata, metadata, this.trackOutput);
            }
        } catch (IOException e) {
            flacDecoderJni.reset(0L);
            input.setRetryPosition(0L, e);
            throw e;
        }
    }

    @RequiresNonNull({"binarySearchSeeker"})
    private int handlePendingSeek(ExtractorInput input, PositionHolder seekPosition, ParsableByteArray outputBuffer, FlacBinarySearchSeeker.OutputFrameHolder outputFrameHolder, TrackOutput trackOutput) throws InterruptedException, IOException {
        int seekResult = this.binarySearchSeeker.handlePendingSeek(input, seekPosition);
        ByteBuffer outputByteBuffer = outputFrameHolder.byteBuffer;
        if (seekResult == 0 && outputByteBuffer.limit() > 0) {
            outputSample(outputBuffer, outputByteBuffer.limit(), outputFrameHolder.timeUs, trackOutput);
        }
        return seekResult;
    }

    private static FlacBinarySearchSeeker outputSeekMap(FlacDecoderJni decoderJni, FlacStreamMetadata streamMetadata, long streamLength, ExtractorOutput output, FlacBinarySearchSeeker.OutputFrameHolder outputFrameHolder) {
        SeekMap seekMap;
        boolean haveSeekTable = decoderJni.getSeekPoints(0L) != null;
        FlacBinarySearchSeeker binarySearchSeeker = null;
        if (haveSeekTable) {
            seekMap = new FlacSeekMap(streamMetadata.getDurationUs(), decoderJni);
        } else if (streamLength != -1) {
            long firstFramePosition = decoderJni.getDecodePosition();
            binarySearchSeeker = new FlacBinarySearchSeeker(streamMetadata, firstFramePosition, streamLength, decoderJni, outputFrameHolder);
            seekMap = binarySearchSeeker.getSeekMap();
        } else {
            seekMap = new SeekMap.Unseekable(streamMetadata.getDurationUs());
        }
        output.seekMap(seekMap);
        return binarySearchSeeker;
    }

    private static void outputFormat(FlacStreamMetadata streamMetadata, Metadata metadata, TrackOutput output) {
        Format mediaFormat = Format.createAudioSampleFormat(null, MimeTypes.AUDIO_RAW, null, streamMetadata.getBitRate(), streamMetadata.getMaxDecodedFrameSize(), streamMetadata.channels, streamMetadata.sampleRate, Util.getPcmEncoding(streamMetadata.bitsPerSample), 0, 0, null, null, 0, null, metadata);
        output.format(mediaFormat);
    }

    private static void outputSample(ParsableByteArray sampleData, int size, long timeUs, TrackOutput output) {
        sampleData.setPosition(0);
        output.sampleData(sampleData, size);
        output.sampleMetadata(timeUs, 1, size, 0, null);
    }

    /* loaded from: classes3.dex */
    public static final class FlacSeekMap implements SeekMap {
        private final FlacDecoderJni decoderJni;
        private final long durationUs;

        public FlacSeekMap(long durationUs, FlacDecoderJni decoderJni) {
            this.durationUs = durationUs;
            this.decoderJni = decoderJni;
        }

        @Override // com.google.android.exoplayer2.extractor.SeekMap
        public boolean isSeekable() {
            return true;
        }

        @Override // com.google.android.exoplayer2.extractor.SeekMap
        public SeekMap.SeekPoints getSeekPoints(long timeUs) {
            SeekMap.SeekPoints seekPoints = this.decoderJni.getSeekPoints(timeUs);
            return seekPoints == null ? new SeekMap.SeekPoints(SeekPoint.START) : seekPoints;
        }

        @Override // com.google.android.exoplayer2.extractor.SeekMap
        public long getDurationUs() {
            return this.durationUs;
        }
    }
}
