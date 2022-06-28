package com.google.android.exoplayer2.extractor.flac;

import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.FlacFrameReader;
import com.google.android.exoplayer2.extractor.FlacMetadataReader;
import com.google.android.exoplayer2.extractor.FlacSeekTableSeekMap;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap;
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
/* loaded from: classes3.dex */
public final class FlacExtractor implements Extractor {
    private static final int BUFFER_LENGTH = 32768;
    public static final ExtractorsFactory FACTORY = FlacExtractor$$ExternalSyntheticLambda0.INSTANCE;
    public static final int FLAG_DISABLE_ID3_METADATA = 1;
    private static final int SAMPLE_NUMBER_UNKNOWN = -1;
    private static final int STATE_GET_FRAME_START_MARKER = 4;
    private static final int STATE_GET_STREAM_MARKER_AND_INFO_BLOCK_BYTES = 1;
    private static final int STATE_READ_FRAMES = 5;
    private static final int STATE_READ_ID3_METADATA = 0;
    private static final int STATE_READ_METADATA_BLOCKS = 3;
    private static final int STATE_READ_STREAM_MARKER = 2;
    private FlacBinarySearchSeeker binarySearchSeeker;
    private final ParsableByteArray buffer;
    private int currentFrameBytesWritten;
    private long currentFrameFirstSampleNumber;
    private ExtractorOutput extractorOutput;
    private FlacStreamMetadata flacStreamMetadata;
    private int frameStartMarker;
    private Metadata id3Metadata;
    private final boolean id3MetadataDisabled;
    private int minFrameSize;
    private final FlacFrameReader.SampleNumberHolder sampleNumberHolder;
    private int state;
    private final byte[] streamMarkerAndInfoBlock;
    private TrackOutput trackOutput;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Flags {
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    private @interface State {
    }

    public static /* synthetic */ Extractor[] lambda$static$0() {
        return new Extractor[]{new FlacExtractor()};
    }

    public FlacExtractor() {
        this(0);
    }

    public FlacExtractor(int flags) {
        this.streamMarkerAndInfoBlock = new byte[42];
        this.buffer = new ParsableByteArray(new byte[32768], 0);
        this.id3MetadataDisabled = (flags & 1) != 0;
        this.sampleNumberHolder = new FlacFrameReader.SampleNumberHolder();
        this.state = 0;
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        FlacMetadataReader.peekId3Metadata(input, false);
        return FlacMetadataReader.checkAndPeekStreamMarker(input);
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public void init(ExtractorOutput output) {
        this.extractorOutput = output;
        this.trackOutput = output.track(0, 1);
        output.endTracks();
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        switch (this.state) {
            case 0:
                readId3Metadata(input);
                return 0;
            case 1:
                getStreamMarkerAndInfoBlockBytes(input);
                return 0;
            case 2:
                readStreamMarker(input);
                return 0;
            case 3:
                readMetadataBlocks(input);
                return 0;
            case 4:
                getFrameStartMarker(input);
                return 0;
            case 5:
                return readFrames(input, seekPosition);
            default:
                throw new IllegalStateException();
        }
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public void seek(long position, long timeUs) {
        long j = 0;
        if (position == 0) {
            this.state = 0;
        } else {
            FlacBinarySearchSeeker flacBinarySearchSeeker = this.binarySearchSeeker;
            if (flacBinarySearchSeeker != null) {
                flacBinarySearchSeeker.setSeekTargetUs(timeUs);
            }
        }
        if (timeUs != 0) {
            j = -1;
        }
        this.currentFrameFirstSampleNumber = j;
        this.currentFrameBytesWritten = 0;
        this.buffer.reset();
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public void release() {
    }

    private void readId3Metadata(ExtractorInput input) throws IOException, InterruptedException {
        this.id3Metadata = FlacMetadataReader.readId3Metadata(input, !this.id3MetadataDisabled);
        this.state = 1;
    }

    private void getStreamMarkerAndInfoBlockBytes(ExtractorInput input) throws IOException, InterruptedException {
        byte[] bArr = this.streamMarkerAndInfoBlock;
        input.peekFully(bArr, 0, bArr.length);
        input.resetPeekPosition();
        this.state = 2;
    }

    private void readStreamMarker(ExtractorInput input) throws IOException, InterruptedException {
        FlacMetadataReader.readStreamMarker(input);
        this.state = 3;
    }

    private void readMetadataBlocks(ExtractorInput input) throws IOException, InterruptedException {
        boolean isLastMetadataBlock = false;
        FlacMetadataReader.FlacStreamMetadataHolder metadataHolder = new FlacMetadataReader.FlacStreamMetadataHolder(this.flacStreamMetadata);
        while (!isLastMetadataBlock) {
            isLastMetadataBlock = FlacMetadataReader.readMetadataBlock(input, metadataHolder);
            this.flacStreamMetadata = (FlacStreamMetadata) Util.castNonNull(metadataHolder.flacStreamMetadata);
        }
        Assertions.checkNotNull(this.flacStreamMetadata);
        this.minFrameSize = Math.max(this.flacStreamMetadata.minFrameSize, 6);
        ((TrackOutput) Util.castNonNull(this.trackOutput)).format(this.flacStreamMetadata.getFormat(this.streamMarkerAndInfoBlock, this.id3Metadata));
        this.state = 4;
    }

    private void getFrameStartMarker(ExtractorInput input) throws IOException, InterruptedException {
        this.frameStartMarker = FlacMetadataReader.getFrameStartMarker(input);
        ((ExtractorOutput) Util.castNonNull(this.extractorOutput)).seekMap(getSeekMap(input.getPosition(), input.getLength()));
        this.state = 5;
    }

    private int readFrames(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        Assertions.checkNotNull(this.trackOutput);
        Assertions.checkNotNull(this.flacStreamMetadata);
        FlacBinarySearchSeeker flacBinarySearchSeeker = this.binarySearchSeeker;
        if (flacBinarySearchSeeker != null && flacBinarySearchSeeker.isSeeking()) {
            return this.binarySearchSeeker.handlePendingSeek(input, seekPosition);
        }
        if (this.currentFrameFirstSampleNumber == -1) {
            this.currentFrameFirstSampleNumber = FlacFrameReader.getFirstSampleNumber(input, this.flacStreamMetadata);
            return 0;
        }
        int currentLimit = this.buffer.limit();
        boolean foundEndOfInput = false;
        if (currentLimit < 32768) {
            int bytesRead = input.read(this.buffer.data, currentLimit, 32768 - currentLimit);
            foundEndOfInput = bytesRead == -1;
            if (!foundEndOfInput) {
                this.buffer.setLimit(currentLimit + bytesRead);
            } else if (this.buffer.bytesLeft() == 0) {
                outputSampleMetadata();
                return -1;
            }
        }
        int positionBeforeFindingAFrame = this.buffer.getPosition();
        int i = this.currentFrameBytesWritten;
        int i2 = this.minFrameSize;
        if (i < i2) {
            ParsableByteArray parsableByteArray = this.buffer;
            parsableByteArray.skipBytes(Math.min(i2 - i, parsableByteArray.bytesLeft()));
        }
        long nextFrameFirstSampleNumber = findFrame(this.buffer, foundEndOfInput);
        int numberOfFrameBytes = this.buffer.getPosition() - positionBeforeFindingAFrame;
        this.buffer.setPosition(positionBeforeFindingAFrame);
        this.trackOutput.sampleData(this.buffer, numberOfFrameBytes);
        this.currentFrameBytesWritten += numberOfFrameBytes;
        if (nextFrameFirstSampleNumber != -1) {
            outputSampleMetadata();
            this.currentFrameBytesWritten = 0;
            this.currentFrameFirstSampleNumber = nextFrameFirstSampleNumber;
        }
        if (this.buffer.bytesLeft() < 16) {
            System.arraycopy(this.buffer.data, this.buffer.getPosition(), this.buffer.data, 0, this.buffer.bytesLeft());
            ParsableByteArray parsableByteArray2 = this.buffer;
            parsableByteArray2.reset(parsableByteArray2.bytesLeft());
        }
        return 0;
    }

    private SeekMap getSeekMap(long firstFramePosition, long streamLength) {
        Assertions.checkNotNull(this.flacStreamMetadata);
        if (this.flacStreamMetadata.seekTable == null) {
            if (streamLength != -1 && this.flacStreamMetadata.totalSamples > 0) {
                FlacBinarySearchSeeker flacBinarySearchSeeker = new FlacBinarySearchSeeker(this.flacStreamMetadata, this.frameStartMarker, firstFramePosition, streamLength);
                this.binarySearchSeeker = flacBinarySearchSeeker;
                return flacBinarySearchSeeker.getSeekMap();
            }
            return new SeekMap.Unseekable(this.flacStreamMetadata.getDurationUs());
        }
        return new FlacSeekTableSeekMap(this.flacStreamMetadata, firstFramePosition);
    }

    private long findFrame(ParsableByteArray data, boolean foundEndOfInput) {
        boolean frameFound;
        Assertions.checkNotNull(this.flacStreamMetadata);
        int frameOffset = data.getPosition();
        while (frameOffset <= data.limit() - 16) {
            data.setPosition(frameOffset);
            if (FlacFrameReader.checkAndReadFrameHeader(data, this.flacStreamMetadata, this.frameStartMarker, this.sampleNumberHolder)) {
                data.setPosition(frameOffset);
                return this.sampleNumberHolder.sampleNumber;
            }
            frameOffset++;
        }
        if (foundEndOfInput) {
            while (frameOffset <= data.limit() - this.minFrameSize) {
                data.setPosition(frameOffset);
                try {
                    frameFound = FlacFrameReader.checkAndReadFrameHeader(data, this.flacStreamMetadata, this.frameStartMarker, this.sampleNumberHolder);
                } catch (IndexOutOfBoundsException e) {
                    frameFound = false;
                }
                if (data.getPosition() > data.limit()) {
                    frameFound = false;
                }
                if (frameFound) {
                    data.setPosition(frameOffset);
                    return this.sampleNumberHolder.sampleNumber;
                }
                frameOffset++;
            }
            data.setPosition(data.limit());
            return -1L;
        }
        data.setPosition(frameOffset);
        return -1L;
    }

    private void outputSampleMetadata() {
        long timeUs = (this.currentFrameFirstSampleNumber * 1000000) / ((FlacStreamMetadata) Util.castNonNull(this.flacStreamMetadata)).sampleRate;
        ((TrackOutput) Util.castNonNull(this.trackOutput)).sampleMetadata(timeUs, 1, this.currentFrameBytesWritten, 0, null);
    }
}
