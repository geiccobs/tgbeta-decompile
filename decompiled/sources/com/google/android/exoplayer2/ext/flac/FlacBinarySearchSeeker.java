package com.google.android.exoplayer2.ext.flac;

import com.google.android.exoplayer2.ext.flac.FlacDecoderJni;
import com.google.android.exoplayer2.extractor.BinarySearchSeeker;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.FlacStreamMetadata;
import java.io.IOException;
import java.nio.ByteBuffer;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public final class FlacBinarySearchSeeker extends BinarySearchSeeker {
    private final FlacDecoderJni decoderJni;

    /* loaded from: classes3.dex */
    public static final class OutputFrameHolder {
        public final ByteBuffer byteBuffer;
        public long timeUs = 0;

        public OutputFrameHolder(ByteBuffer outputByteBuffer) {
            this.byteBuffer = outputByteBuffer;
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public FlacBinarySearchSeeker(FlacStreamMetadata streamMetadata, long firstFramePosition, long inputLength, FlacDecoderJni decoderJni, OutputFrameHolder outputFrameHolder) {
        super(new FlacBinarySearchSeeker$$ExternalSyntheticLambda0(streamMetadata), new FlacTimestampSeeker(decoderJni, outputFrameHolder), streamMetadata.getDurationUs(), 0L, streamMetadata.totalSamples, firstFramePosition, inputLength, streamMetadata.getApproxBytesPerFrame(), Math.max(6, streamMetadata.minFrameSize));
        streamMetadata.getClass();
        this.decoderJni = (FlacDecoderJni) Assertions.checkNotNull(decoderJni);
    }

    @Override // com.google.android.exoplayer2.extractor.BinarySearchSeeker
    protected void onSeekOperationFinished(boolean foundTargetFrame, long resultPosition) {
        if (!foundTargetFrame) {
            this.decoderJni.reset(resultPosition);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static final class FlacTimestampSeeker implements BinarySearchSeeker.TimestampSeeker {
        private final FlacDecoderJni decoderJni;
        private final OutputFrameHolder outputFrameHolder;

        @Override // com.google.android.exoplayer2.extractor.BinarySearchSeeker.TimestampSeeker
        public /* synthetic */ void onSeekFinished() {
            BinarySearchSeeker.TimestampSeeker.CC.$default$onSeekFinished(this);
        }

        private FlacTimestampSeeker(FlacDecoderJni decoderJni, OutputFrameHolder outputFrameHolder) {
            this.decoderJni = decoderJni;
            this.outputFrameHolder = outputFrameHolder;
        }

        @Override // com.google.android.exoplayer2.extractor.BinarySearchSeeker.TimestampSeeker
        public BinarySearchSeeker.TimestampSearchResult searchForTimestamp(ExtractorInput input, long targetSampleIndex) throws IOException, InterruptedException {
            ByteBuffer outputBuffer = this.outputFrameHolder.byteBuffer;
            long searchPosition = input.getPosition();
            this.decoderJni.reset(searchPosition);
            try {
                this.decoderJni.decodeSampleWithBacktrackPosition(outputBuffer, searchPosition);
                if (outputBuffer.limit() == 0) {
                    return BinarySearchSeeker.TimestampSearchResult.NO_TIMESTAMP_IN_RANGE_RESULT;
                }
                long lastFrameSampleIndex = this.decoderJni.getLastFrameFirstSampleIndex();
                long nextFrameSampleIndex = this.decoderJni.getNextFrameFirstSampleIndex();
                long nextFrameSamplePosition = this.decoderJni.getDecodePosition();
                boolean targetSampleInLastFrame = lastFrameSampleIndex <= targetSampleIndex && nextFrameSampleIndex > targetSampleIndex;
                if (targetSampleInLastFrame) {
                    this.outputFrameHolder.timeUs = this.decoderJni.getLastFrameTimestamp();
                    return BinarySearchSeeker.TimestampSearchResult.targetFoundResult(input.getPosition());
                } else if (nextFrameSampleIndex <= targetSampleIndex) {
                    return BinarySearchSeeker.TimestampSearchResult.underestimatedResult(nextFrameSampleIndex, nextFrameSamplePosition);
                } else {
                    return BinarySearchSeeker.TimestampSearchResult.overestimatedResult(lastFrameSampleIndex, searchPosition);
                }
            } catch (FlacDecoderJni.FlacFrameDecodeException e) {
                return BinarySearchSeeker.TimestampSearchResult.NO_TIMESTAMP_IN_RANGE_RESULT;
            }
        }
    }
}
