package com.google.android.exoplayer2.extractor.wav;

import android.util.Pair;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
/* loaded from: classes3.dex */
final class WavHeaderReader {
    private static final String TAG = "WavHeaderReader";

    public static WavHeader peek(ExtractorInput input) throws IOException, InterruptedException {
        byte[] extraData;
        Assertions.checkNotNull(input);
        ParsableByteArray scratch = new ParsableByteArray(16);
        ChunkHeader chunkHeader = ChunkHeader.peek(input, scratch);
        if (chunkHeader.id != 1380533830) {
            return null;
        }
        input.peekFully(scratch.data, 0, 4);
        scratch.setPosition(0);
        int riffFormat = scratch.readInt();
        if (riffFormat != 1463899717) {
            Log.e(TAG, "Unsupported RIFF format: " + riffFormat);
            return null;
        }
        ChunkHeader chunkHeader2 = ChunkHeader.peek(input, scratch);
        while (chunkHeader2.id != 1718449184) {
            input.advancePeekPosition((int) chunkHeader2.size);
            chunkHeader2 = ChunkHeader.peek(input, scratch);
        }
        Assertions.checkState(chunkHeader2.size >= 16);
        input.peekFully(scratch.data, 0, 16);
        scratch.setPosition(0);
        int audioFormatType = scratch.readLittleEndianUnsignedShort();
        int numChannels = scratch.readLittleEndianUnsignedShort();
        int frameRateHz = scratch.readLittleEndianUnsignedIntToInt();
        int averageBytesPerSecond = scratch.readLittleEndianUnsignedIntToInt();
        int blockSize = scratch.readLittleEndianUnsignedShort();
        int bitsPerSample = scratch.readLittleEndianUnsignedShort();
        int bytesLeft = ((int) chunkHeader2.size) - 16;
        if (bytesLeft > 0) {
            byte[] extraData2 = new byte[bytesLeft];
            input.peekFully(extraData2, 0, bytesLeft);
            extraData = extraData2;
        } else {
            byte[] extraData3 = Util.EMPTY_BYTE_ARRAY;
            extraData = extraData3;
        }
        return new WavHeader(audioFormatType, numChannels, frameRateHz, averageBytesPerSecond, blockSize, bitsPerSample, extraData);
    }

    public static Pair<Long, Long> skipToData(ExtractorInput input) throws IOException, InterruptedException {
        Assertions.checkNotNull(input);
        input.resetPeekPosition();
        ParsableByteArray scratch = new ParsableByteArray(8);
        ChunkHeader chunkHeader = ChunkHeader.peek(input, scratch);
        while (chunkHeader.id != 1684108385) {
            if (chunkHeader.id != 1380533830 && chunkHeader.id != 1718449184) {
                Log.w(TAG, "Ignoring unknown WAV chunk: " + chunkHeader.id);
            }
            long bytesToSkip = chunkHeader.size + 8;
            if (chunkHeader.id == 1380533830) {
                bytesToSkip = 12;
            }
            if (bytesToSkip > 2147483647L) {
                throw new ParserException("Chunk is too large (~2GB+) to skip; id: " + chunkHeader.id);
            }
            input.skipFully((int) bytesToSkip);
            chunkHeader = ChunkHeader.peek(input, scratch);
        }
        input.skipFully(8);
        long dataStartPosition = input.getPosition();
        long dataEndPosition = chunkHeader.size + dataStartPosition;
        long inputLength = input.getLength();
        if (inputLength != -1 && dataEndPosition > inputLength) {
            Log.w(TAG, "Data exceeds input length: " + dataEndPosition + ", " + inputLength);
            dataEndPosition = inputLength;
        }
        return Pair.create(Long.valueOf(dataStartPosition), Long.valueOf(dataEndPosition));
    }

    private WavHeaderReader() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static final class ChunkHeader {
        public static final int SIZE_IN_BYTES = 8;
        public final int id;
        public final long size;

        private ChunkHeader(int id, long size) {
            this.id = id;
            this.size = size;
        }

        public static ChunkHeader peek(ExtractorInput input, ParsableByteArray scratch) throws IOException, InterruptedException {
            input.peekFully(scratch.data, 0, 8);
            scratch.setPosition(0);
            int id = scratch.readInt();
            long size = scratch.readLittleEndianUnsignedInt();
            return new ChunkHeader(id, size);
        }
    }
}
