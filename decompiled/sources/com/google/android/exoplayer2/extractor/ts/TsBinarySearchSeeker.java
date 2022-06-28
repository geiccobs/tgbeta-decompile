package com.google.android.exoplayer2.extractor.ts;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.BinarySearchSeeker;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
/* loaded from: classes3.dex */
public final class TsBinarySearchSeeker extends BinarySearchSeeker {
    private static final int MINIMUM_SEARCH_RANGE_BYTES = 940;
    private static final long SEEK_TOLERANCE_US = 100000;
    private static final int TIMESTAMP_SEARCH_BYTES = 112800;

    public TsBinarySearchSeeker(TimestampAdjuster pcrTimestampAdjuster, long streamDurationUs, long inputLength, int pcrPid) {
        super(new BinarySearchSeeker.DefaultSeekTimestampConverter(), new TsPcrSeeker(pcrPid, pcrTimestampAdjuster), streamDurationUs, 0L, streamDurationUs + 1, 0L, inputLength, 188L, MINIMUM_SEARCH_RANGE_BYTES);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static final class TsPcrSeeker implements BinarySearchSeeker.TimestampSeeker {
        private final ParsableByteArray packetBuffer = new ParsableByteArray();
        private final int pcrPid;
        private final TimestampAdjuster pcrTimestampAdjuster;

        public TsPcrSeeker(int pcrPid, TimestampAdjuster pcrTimestampAdjuster) {
            this.pcrPid = pcrPid;
            this.pcrTimestampAdjuster = pcrTimestampAdjuster;
        }

        @Override // com.google.android.exoplayer2.extractor.BinarySearchSeeker.TimestampSeeker
        public BinarySearchSeeker.TimestampSearchResult searchForTimestamp(ExtractorInput input, long targetTimestamp) throws IOException, InterruptedException {
            long inputPosition = input.getPosition();
            int bytesToSearch = (int) Math.min(112800L, input.getLength() - inputPosition);
            this.packetBuffer.reset(bytesToSearch);
            input.peekFully(this.packetBuffer.data, 0, bytesToSearch);
            return searchForPcrValueInBuffer(this.packetBuffer, targetTimestamp, inputPosition);
        }

        private BinarySearchSeeker.TimestampSearchResult searchForPcrValueInBuffer(ParsableByteArray packetBuffer, long targetPcrTimeUs, long bufferStartOffset) {
            long endOfLastPacketPosition;
            int limit;
            int limit2 = packetBuffer.limit();
            long startOfLastPacketPosition = -1;
            long pcrValue = -1;
            long lastPcrTimeUsInRange = C.TIME_UNSET;
            while (true) {
                if (packetBuffer.bytesLeft() < 188) {
                    endOfLastPacketPosition = pcrValue;
                    break;
                }
                int startOfPacket = TsUtil.findSyncBytePosition(packetBuffer.data, packetBuffer.getPosition(), limit2);
                int endOfPacket = startOfPacket + TsExtractor.TS_PACKET_SIZE;
                if (endOfPacket > limit2) {
                    endOfLastPacketPosition = pcrValue;
                    break;
                }
                long pcrValue2 = TsUtil.readPcrFromPacket(packetBuffer, startOfPacket, this.pcrPid);
                if (pcrValue2 == C.TIME_UNSET) {
                    limit = limit2;
                } else {
                    long pcrTimeUs = this.pcrTimestampAdjuster.adjustTsTimestamp(pcrValue2);
                    if (pcrTimeUs > targetPcrTimeUs) {
                        if (lastPcrTimeUsInRange == C.TIME_UNSET) {
                            return BinarySearchSeeker.TimestampSearchResult.overestimatedResult(pcrTimeUs, bufferStartOffset);
                        }
                        return BinarySearchSeeker.TimestampSearchResult.targetFoundResult(bufferStartOffset + startOfLastPacketPosition);
                    } else if (pcrTimeUs + TsBinarySearchSeeker.SEEK_TOLERANCE_US > targetPcrTimeUs) {
                        long startOfPacketInStream = startOfPacket + bufferStartOffset;
                        return BinarySearchSeeker.TimestampSearchResult.targetFoundResult(startOfPacketInStream);
                    } else {
                        limit = limit2;
                        long lastPcrTimeUsInRange2 = startOfPacket;
                        startOfLastPacketPosition = lastPcrTimeUsInRange2;
                        lastPcrTimeUsInRange = pcrTimeUs;
                    }
                }
                packetBuffer.setPosition(endOfPacket);
                pcrValue = endOfPacket;
                limit2 = limit;
            }
            if (lastPcrTimeUsInRange != C.TIME_UNSET) {
                long endOfLastPacketPositionInStream = bufferStartOffset + endOfLastPacketPosition;
                return BinarySearchSeeker.TimestampSearchResult.underestimatedResult(lastPcrTimeUsInRange, endOfLastPacketPositionInStream);
            }
            return BinarySearchSeeker.TimestampSearchResult.NO_TIMESTAMP_IN_RANGE_RESULT;
        }

        @Override // com.google.android.exoplayer2.extractor.BinarySearchSeeker.TimestampSeeker
        public void onSeekFinished() {
            this.packetBuffer.reset(Util.EMPTY_BYTE_ARRAY);
        }
    }
}
