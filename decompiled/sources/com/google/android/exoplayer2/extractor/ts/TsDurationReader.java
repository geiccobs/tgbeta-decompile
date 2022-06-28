package com.google.android.exoplayer2.extractor.ts;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
/* loaded from: classes3.dex */
public final class TsDurationReader {
    private static final int TIMESTAMP_SEARCH_BYTES = 112800;
    private boolean isDurationRead;
    private boolean isFirstPcrValueRead;
    private boolean isLastPcrValueRead;
    private final TimestampAdjuster pcrTimestampAdjuster = new TimestampAdjuster(0);
    private long firstPcrValue = C.TIME_UNSET;
    private long lastPcrValue = C.TIME_UNSET;
    private long durationUs = C.TIME_UNSET;
    private final ParsableByteArray packetBuffer = new ParsableByteArray();

    public boolean isDurationReadFinished() {
        return this.isDurationRead;
    }

    public int readDuration(ExtractorInput input, PositionHolder seekPositionHolder, int pcrPid) throws IOException, InterruptedException {
        if (pcrPid <= 0) {
            return finishReadDuration(input);
        }
        if (!this.isLastPcrValueRead) {
            return readLastPcrValue(input, seekPositionHolder, pcrPid);
        }
        if (this.lastPcrValue == C.TIME_UNSET) {
            return finishReadDuration(input);
        }
        if (!this.isFirstPcrValueRead) {
            return readFirstPcrValue(input, seekPositionHolder, pcrPid);
        }
        long j = this.firstPcrValue;
        if (j == C.TIME_UNSET) {
            return finishReadDuration(input);
        }
        long minPcrPositionUs = this.pcrTimestampAdjuster.adjustTsTimestamp(j);
        long maxPcrPositionUs = this.pcrTimestampAdjuster.adjustTsTimestamp(this.lastPcrValue);
        this.durationUs = maxPcrPositionUs - minPcrPositionUs;
        return finishReadDuration(input);
    }

    public long getDurationUs() {
        return this.durationUs;
    }

    public TimestampAdjuster getPcrTimestampAdjuster() {
        return this.pcrTimestampAdjuster;
    }

    private int finishReadDuration(ExtractorInput input) {
        this.packetBuffer.reset(Util.EMPTY_BYTE_ARRAY);
        this.isDurationRead = true;
        input.resetPeekPosition();
        return 0;
    }

    private int readFirstPcrValue(ExtractorInput input, PositionHolder seekPositionHolder, int pcrPid) throws IOException, InterruptedException {
        int bytesToSearch = (int) Math.min(112800L, input.getLength());
        if (input.getPosition() != 0) {
            seekPositionHolder.position = 0;
            return 1;
        }
        this.packetBuffer.reset(bytesToSearch);
        input.resetPeekPosition();
        input.peekFully(this.packetBuffer.data, 0, bytesToSearch);
        this.firstPcrValue = readFirstPcrValueFromBuffer(this.packetBuffer, pcrPid);
        this.isFirstPcrValueRead = true;
        return 0;
    }

    private long readFirstPcrValueFromBuffer(ParsableByteArray packetBuffer, int pcrPid) {
        int searchStartPosition = packetBuffer.getPosition();
        int searchEndPosition = packetBuffer.limit();
        for (int searchPosition = searchStartPosition; searchPosition < searchEndPosition; searchPosition++) {
            if (packetBuffer.data[searchPosition] == 71) {
                long pcrValue = TsUtil.readPcrFromPacket(packetBuffer, searchPosition, pcrPid);
                if (pcrValue != C.TIME_UNSET) {
                    return pcrValue;
                }
            }
        }
        return C.TIME_UNSET;
    }

    private int readLastPcrValue(ExtractorInput input, PositionHolder seekPositionHolder, int pcrPid) throws IOException, InterruptedException {
        long inputLength = input.getLength();
        int bytesToSearch = (int) Math.min(112800L, inputLength);
        long searchStartPosition = inputLength - bytesToSearch;
        if (input.getPosition() != searchStartPosition) {
            seekPositionHolder.position = searchStartPosition;
            return 1;
        }
        this.packetBuffer.reset(bytesToSearch);
        input.resetPeekPosition();
        input.peekFully(this.packetBuffer.data, 0, bytesToSearch);
        this.lastPcrValue = readLastPcrValueFromBuffer(this.packetBuffer, pcrPid);
        this.isLastPcrValueRead = true;
        return 0;
    }

    private long readLastPcrValueFromBuffer(ParsableByteArray packetBuffer, int pcrPid) {
        int searchStartPosition = packetBuffer.getPosition();
        int searchEndPosition = packetBuffer.limit();
        for (int searchPosition = searchEndPosition - 1; searchPosition >= searchStartPosition; searchPosition--) {
            if (packetBuffer.data[searchPosition] == 71) {
                long pcrValue = TsUtil.readPcrFromPacket(packetBuffer, searchPosition, pcrPid);
                if (pcrValue != C.TIME_UNSET) {
                    return pcrValue;
                }
            }
        }
        return C.TIME_UNSET;
    }
}
