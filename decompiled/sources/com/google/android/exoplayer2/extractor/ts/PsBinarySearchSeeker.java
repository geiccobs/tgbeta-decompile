package com.google.android.exoplayer2.extractor.ts;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.audio.SilenceSkippingAudioProcessor;
import com.google.android.exoplayer2.extractor.BinarySearchSeeker;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
/* loaded from: classes3.dex */
public final class PsBinarySearchSeeker extends BinarySearchSeeker {
    private static final int MINIMUM_SEARCH_RANGE_BYTES = 1000;
    private static final long SEEK_TOLERANCE_US = 100000;
    private static final int TIMESTAMP_SEARCH_BYTES = 20000;

    public PsBinarySearchSeeker(TimestampAdjuster scrTimestampAdjuster, long streamDurationUs, long inputLength) {
        super(new BinarySearchSeeker.DefaultSeekTimestampConverter(), new PsScrSeeker(scrTimestampAdjuster), streamDurationUs, 0L, streamDurationUs + 1, 0L, inputLength, 188L, 1000);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static final class PsScrSeeker implements BinarySearchSeeker.TimestampSeeker {
        private final ParsableByteArray packetBuffer;
        private final TimestampAdjuster scrTimestampAdjuster;

        private PsScrSeeker(TimestampAdjuster scrTimestampAdjuster) {
            this.scrTimestampAdjuster = scrTimestampAdjuster;
            this.packetBuffer = new ParsableByteArray();
        }

        @Override // com.google.android.exoplayer2.extractor.BinarySearchSeeker.TimestampSeeker
        public BinarySearchSeeker.TimestampSearchResult searchForTimestamp(ExtractorInput input, long targetTimestamp) throws IOException, InterruptedException {
            long inputPosition = input.getPosition();
            int bytesToSearch = (int) Math.min((long) SilenceSkippingAudioProcessor.DEFAULT_PADDING_SILENCE_US, input.getLength() - inputPosition);
            this.packetBuffer.reset(bytesToSearch);
            input.peekFully(this.packetBuffer.data, 0, bytesToSearch);
            return searchForScrValueInBuffer(this.packetBuffer, targetTimestamp, inputPosition);
        }

        @Override // com.google.android.exoplayer2.extractor.BinarySearchSeeker.TimestampSeeker
        public void onSeekFinished() {
            this.packetBuffer.reset(Util.EMPTY_BYTE_ARRAY);
        }

        private BinarySearchSeeker.TimestampSearchResult searchForScrValueInBuffer(ParsableByteArray packetBuffer, long targetScrTimeUs, long bufferStartOffset) {
            int startOfLastPacketPosition = -1;
            int endOfLastPacketPosition = -1;
            long lastScrTimeUsInRange = C.TIME_UNSET;
            while (packetBuffer.bytesLeft() >= 4) {
                int nextStartCode = PsBinarySearchSeeker.peekIntAtPosition(packetBuffer.data, packetBuffer.getPosition());
                if (nextStartCode != 442) {
                    packetBuffer.skipBytes(1);
                } else {
                    packetBuffer.skipBytes(4);
                    long scrValue = PsDurationReader.readScrValueFromPack(packetBuffer);
                    if (scrValue != C.TIME_UNSET) {
                        long scrTimeUs = this.scrTimestampAdjuster.adjustTsTimestamp(scrValue);
                        if (scrTimeUs > targetScrTimeUs) {
                            if (lastScrTimeUsInRange == C.TIME_UNSET) {
                                return BinarySearchSeeker.TimestampSearchResult.overestimatedResult(scrTimeUs, bufferStartOffset);
                            }
                            return BinarySearchSeeker.TimestampSearchResult.targetFoundResult(startOfLastPacketPosition + bufferStartOffset);
                        } else if (PsBinarySearchSeeker.SEEK_TOLERANCE_US + scrTimeUs > targetScrTimeUs) {
                            long startOfPacketInStream = packetBuffer.getPosition() + bufferStartOffset;
                            return BinarySearchSeeker.TimestampSearchResult.targetFoundResult(startOfPacketInStream);
                        } else {
                            lastScrTimeUsInRange = scrTimeUs;
                            startOfLastPacketPosition = packetBuffer.getPosition();
                        }
                    }
                    skipToEndOfCurrentPack(packetBuffer);
                    endOfLastPacketPosition = packetBuffer.getPosition();
                }
            }
            if (lastScrTimeUsInRange != C.TIME_UNSET) {
                long endOfLastPacketPositionInStream = endOfLastPacketPosition + bufferStartOffset;
                return BinarySearchSeeker.TimestampSearchResult.underestimatedResult(lastScrTimeUsInRange, endOfLastPacketPositionInStream);
            }
            return BinarySearchSeeker.TimestampSearchResult.NO_TIMESTAMP_IN_RANGE_RESULT;
        }

        private static void skipToEndOfCurrentPack(ParsableByteArray packetBuffer) {
            int nextStartCode;
            int limit = packetBuffer.limit();
            if (packetBuffer.bytesLeft() < 10) {
                packetBuffer.setPosition(limit);
                return;
            }
            packetBuffer.skipBytes(9);
            int packStuffingLength = packetBuffer.readUnsignedByte() & 7;
            if (packetBuffer.bytesLeft() < packStuffingLength) {
                packetBuffer.setPosition(limit);
                return;
            }
            packetBuffer.skipBytes(packStuffingLength);
            if (packetBuffer.bytesLeft() < 4) {
                packetBuffer.setPosition(limit);
                return;
            }
            if (PsBinarySearchSeeker.peekIntAtPosition(packetBuffer.data, packetBuffer.getPosition()) == 443) {
                packetBuffer.skipBytes(4);
                int systemHeaderLength = packetBuffer.readUnsignedShort();
                if (packetBuffer.bytesLeft() < systemHeaderLength) {
                    packetBuffer.setPosition(limit);
                    return;
                }
                packetBuffer.skipBytes(systemHeaderLength);
            }
            while (packetBuffer.bytesLeft() >= 4 && (nextStartCode = PsBinarySearchSeeker.peekIntAtPosition(packetBuffer.data, packetBuffer.getPosition())) != 442 && nextStartCode != 441 && (nextStartCode >>> 8) == 1) {
                packetBuffer.skipBytes(4);
                if (packetBuffer.bytesLeft() < 2) {
                    packetBuffer.setPosition(limit);
                    return;
                } else {
                    int pesPacketLength = packetBuffer.readUnsignedShort();
                    packetBuffer.setPosition(Math.min(packetBuffer.limit(), packetBuffer.getPosition() + pesPacketLength));
                }
            }
        }
    }

    public static int peekIntAtPosition(byte[] data, int position) {
        return ((data[position] & 255) << 24) | ((data[position + 1] & 255) << 16) | ((data[position + 2] & 255) << 8) | (data[position + 3] & 255);
    }
}
