package com.google.android.exoplayer2.extractor.flac;

import com.google.android.exoplayer2.ext.flac.FlacBinarySearchSeeker$$ExternalSyntheticLambda0;
import com.google.android.exoplayer2.extractor.BinarySearchSeeker;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.FlacFrameReader;
import com.google.android.exoplayer2.util.FlacStreamMetadata;
import java.io.IOException;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public final class FlacBinarySearchSeeker extends BinarySearchSeeker {
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public FlacBinarySearchSeeker(FlacStreamMetadata flacStreamMetadata, int frameStartMarker, long firstFramePosition, long inputLength) {
        super(new FlacBinarySearchSeeker$$ExternalSyntheticLambda0(flacStreamMetadata), new FlacTimestampSeeker(flacStreamMetadata, frameStartMarker), flacStreamMetadata.getDurationUs(), 0L, flacStreamMetadata.totalSamples, firstFramePosition, inputLength, flacStreamMetadata.getApproxBytesPerFrame(), Math.max(6, flacStreamMetadata.minFrameSize));
        flacStreamMetadata.getClass();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static final class FlacTimestampSeeker implements BinarySearchSeeker.TimestampSeeker {
        private final FlacStreamMetadata flacStreamMetadata;
        private final int frameStartMarker;
        private final FlacFrameReader.SampleNumberHolder sampleNumberHolder;

        @Override // com.google.android.exoplayer2.extractor.BinarySearchSeeker.TimestampSeeker
        public /* synthetic */ void onSeekFinished() {
            BinarySearchSeeker.TimestampSeeker.CC.$default$onSeekFinished(this);
        }

        private FlacTimestampSeeker(FlacStreamMetadata flacStreamMetadata, int frameStartMarker) {
            this.flacStreamMetadata = flacStreamMetadata;
            this.frameStartMarker = frameStartMarker;
            this.sampleNumberHolder = new FlacFrameReader.SampleNumberHolder();
        }

        @Override // com.google.android.exoplayer2.extractor.BinarySearchSeeker.TimestampSeeker
        public BinarySearchSeeker.TimestampSearchResult searchForTimestamp(ExtractorInput input, long targetSampleNumber) throws IOException, InterruptedException {
            long searchPosition = input.getPosition();
            long leftFrameFirstSampleNumber = findNextFrame(input);
            long leftFramePosition = input.getPeekPosition();
            input.advancePeekPosition(Math.max(6, this.flacStreamMetadata.minFrameSize));
            long rightFrameFirstSampleNumber = findNextFrame(input);
            long rightFramePosition = input.getPeekPosition();
            if (leftFrameFirstSampleNumber <= targetSampleNumber && rightFrameFirstSampleNumber > targetSampleNumber) {
                return BinarySearchSeeker.TimestampSearchResult.targetFoundResult(leftFramePosition);
            }
            if (rightFrameFirstSampleNumber <= targetSampleNumber) {
                return BinarySearchSeeker.TimestampSearchResult.underestimatedResult(rightFrameFirstSampleNumber, rightFramePosition);
            }
            return BinarySearchSeeker.TimestampSearchResult.overestimatedResult(leftFrameFirstSampleNumber, searchPosition);
        }

        private long findNextFrame(ExtractorInput input) throws IOException, InterruptedException {
            while (input.getPeekPosition() < input.getLength() - 6 && !FlacFrameReader.checkFrameHeaderFromPeek(input, this.flacStreamMetadata, this.frameStartMarker, this.sampleNumberHolder)) {
                input.advancePeekPosition(1);
            }
            if (input.getPeekPosition() >= input.getLength() - 6) {
                input.advancePeekPosition((int) (input.getLength() - input.getPeekPosition()));
                return this.flacStreamMetadata.totalSamples;
            }
            return this.sampleNumberHolder.sampleNumber;
        }
    }
}
