package com.google.android.exoplayer2.extractor.wav;

import android.util.Pair;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.audio.WavUtil;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.CustomPhoneKeyboardView;
/* loaded from: classes3.dex */
public final class WavExtractor implements Extractor {
    public static final ExtractorsFactory FACTORY = WavExtractor$$ExternalSyntheticLambda0.INSTANCE;
    private static final int TARGET_SAMPLES_PER_SECOND = 10;
    private ExtractorOutput extractorOutput;
    private OutputWriter outputWriter;
    private TrackOutput trackOutput;
    private int dataStartPosition = -1;
    private long dataEndPosition = -1;

    /* loaded from: classes3.dex */
    private interface OutputWriter {
        void init(int i, long j) throws ParserException;

        void reset(long j);

        boolean sampleData(ExtractorInput extractorInput, long j) throws IOException, InterruptedException;
    }

    public static /* synthetic */ Extractor[] lambda$static$0() {
        return new Extractor[]{new WavExtractor()};
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        return WavHeaderReader.peek(input) != null;
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public void init(ExtractorOutput output) {
        this.extractorOutput = output;
        this.trackOutput = output.track(0, 1);
        output.endTracks();
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public void seek(long position, long timeUs) {
        OutputWriter outputWriter = this.outputWriter;
        if (outputWriter != null) {
            outputWriter.reset(timeUs);
        }
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public void release() {
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        assertInitialized();
        if (this.outputWriter == null) {
            WavHeader header = WavHeaderReader.peek(input);
            if (header == null) {
                throw new ParserException("Unsupported or unrecognized wav header.");
            }
            if (header.formatType == 17) {
                this.outputWriter = new ImaAdPcmOutputWriter(this.extractorOutput, this.trackOutput, header);
            } else if (header.formatType == 6) {
                this.outputWriter = new PassthroughOutputWriter(this.extractorOutput, this.trackOutput, header, MimeTypes.AUDIO_ALAW, -1);
            } else if (header.formatType == 7) {
                this.outputWriter = new PassthroughOutputWriter(this.extractorOutput, this.trackOutput, header, MimeTypes.AUDIO_MLAW, -1);
            } else {
                int pcmEncoding = WavUtil.getPcmEncodingForType(header.formatType, header.bitsPerSample);
                if (pcmEncoding == 0) {
                    throw new ParserException("Unsupported WAV format type: " + header.formatType);
                }
                this.outputWriter = new PassthroughOutputWriter(this.extractorOutput, this.trackOutput, header, MimeTypes.AUDIO_RAW, pcmEncoding);
            }
        }
        if (this.dataStartPosition == -1) {
            Pair<Long, Long> dataBounds = WavHeaderReader.skipToData(input);
            this.dataStartPosition = ((Long) dataBounds.first).intValue();
            long longValue = ((Long) dataBounds.second).longValue();
            this.dataEndPosition = longValue;
            this.outputWriter.init(this.dataStartPosition, longValue);
        } else if (input.getPosition() == 0) {
            input.skipFully(this.dataStartPosition);
        }
        Assertions.checkState(this.dataEndPosition != -1);
        long bytesLeft = this.dataEndPosition - input.getPosition();
        return this.outputWriter.sampleData(input, bytesLeft) ? -1 : 0;
    }

    @EnsuresNonNull({"extractorOutput", "trackOutput"})
    private void assertInitialized() {
        Assertions.checkStateNotNull(this.trackOutput);
        Util.castNonNull(this.extractorOutput);
    }

    /* loaded from: classes3.dex */
    private static final class PassthroughOutputWriter implements OutputWriter {
        private final ExtractorOutput extractorOutput;
        private final Format format;
        private final WavHeader header;
        private long outputFrameCount;
        private int pendingOutputBytes;
        private long startTimeUs;
        private final int targetSampleSizeBytes;
        private final TrackOutput trackOutput;

        public PassthroughOutputWriter(ExtractorOutput extractorOutput, TrackOutput trackOutput, WavHeader header, String mimeType, int pcmEncoding) throws ParserException {
            this.extractorOutput = extractorOutput;
            this.trackOutput = trackOutput;
            this.header = header;
            int bytesPerFrame = (header.numChannels * header.bitsPerSample) / 8;
            if (header.blockSize != bytesPerFrame) {
                throw new ParserException("Expected block size: " + bytesPerFrame + "; got: " + header.blockSize);
            }
            int max = Math.max(bytesPerFrame, (header.frameRateHz * bytesPerFrame) / 10);
            this.targetSampleSizeBytes = max;
            this.format = Format.createAudioSampleFormat(null, mimeType, null, header.frameRateHz * bytesPerFrame * 8, max, header.numChannels, header.frameRateHz, pcmEncoding, null, null, 0, null);
        }

        @Override // com.google.android.exoplayer2.extractor.wav.WavExtractor.OutputWriter
        public void reset(long timeUs) {
            this.startTimeUs = timeUs;
            this.pendingOutputBytes = 0;
            this.outputFrameCount = 0L;
        }

        @Override // com.google.android.exoplayer2.extractor.wav.WavExtractor.OutputWriter
        public void init(int dataStartPosition, long dataEndPosition) {
            this.extractorOutput.seekMap(new WavSeekMap(this.header, 1, dataStartPosition, dataEndPosition));
            this.trackOutput.format(this.format);
        }

        @Override // com.google.android.exoplayer2.extractor.wav.WavExtractor.OutputWriter
        public boolean sampleData(ExtractorInput input, long bytesLeft) throws IOException, InterruptedException {
            int i;
            int i2;
            long bytesLeft2 = bytesLeft;
            while (bytesLeft2 > 0 && (i = this.pendingOutputBytes) < (i2 = this.targetSampleSizeBytes)) {
                int bytesToRead = (int) Math.min(i2 - i, bytesLeft2);
                int bytesAppended = this.trackOutput.sampleData(input, bytesToRead, true);
                if (bytesAppended == -1) {
                    bytesLeft2 = 0;
                } else {
                    this.pendingOutputBytes += bytesAppended;
                    bytesLeft2 -= bytesAppended;
                }
            }
            int bytesPerFrame = this.header.blockSize;
            int pendingFrames = this.pendingOutputBytes / bytesPerFrame;
            if (pendingFrames > 0) {
                long timeUs = this.startTimeUs + Util.scaleLargeTimestamp(this.outputFrameCount, 1000000L, this.header.frameRateHz);
                int size = pendingFrames * bytesPerFrame;
                int offset = this.pendingOutputBytes - size;
                this.trackOutput.sampleMetadata(timeUs, 1, size, offset, null);
                this.outputFrameCount += pendingFrames;
                this.pendingOutputBytes = offset;
            }
            return bytesLeft2 <= 0;
        }
    }

    /* loaded from: classes3.dex */
    private static final class ImaAdPcmOutputWriter implements OutputWriter {
        private static final int[] INDEX_TABLE = {-1, -1, -1, -1, 2, 4, 6, 8, -1, -1, -1, -1, 2, 4, 6, 8};
        private static final int[] STEP_TABLE = {7, 8, 9, 10, 11, 12, 13, 14, 16, 17, 19, 21, 23, 25, 28, 31, 34, 37, 41, 45, 50, 55, 60, 66, 73, 80, 88, 97, 107, 118, TsExtractor.TS_STREAM_TYPE_HDMV_DTS, TLRPC.LAYER, 157, 173, 190, 209, CustomPhoneKeyboardView.KEYBOARD_HEIGHT_DP, 253, 279, 307, 337, 371, 408, 449, ChatMessageCell.MessageAccessibilityNodeProvider.FORWARD, 544, 598, 658, 724, 796, 876, 963, 1060, 1166, 1282, 1411, 1552, 1707, 1878, 2066, 2272, 2499, 2749, 3024, 3327, 3660, 4026, 4428, 4871, 5358, 5894, 6484, 7132, 7845, 8630, 9493, 10442, 11487, 12635, 13899, 15289, 16818, 18500, 20350, 22385, 24623, 27086, 29794, 32767};
        private final ParsableByteArray decodedData;
        private final ExtractorOutput extractorOutput;
        private final Format format;
        private final int framesPerBlock;
        private final WavHeader header;
        private final byte[] inputData;
        private long outputFrameCount;
        private int pendingInputBytes;
        private int pendingOutputBytes;
        private long startTimeUs;
        private final int targetSampleSizeFrames;
        private final TrackOutput trackOutput;

        public ImaAdPcmOutputWriter(ExtractorOutput extractorOutput, TrackOutput trackOutput, WavHeader header) throws ParserException {
            this.extractorOutput = extractorOutput;
            this.trackOutput = trackOutput;
            this.header = header;
            int max = Math.max(1, header.frameRateHz / 10);
            this.targetSampleSizeFrames = max;
            ParsableByteArray scratch = new ParsableByteArray(header.extraData);
            scratch.readLittleEndianUnsignedShort();
            int readLittleEndianUnsignedShort = scratch.readLittleEndianUnsignedShort();
            this.framesPerBlock = readLittleEndianUnsignedShort;
            int numChannels = header.numChannels;
            int expectedFramesPerBlock = (((header.blockSize - (numChannels * 4)) * 8) / (header.bitsPerSample * numChannels)) + 1;
            if (readLittleEndianUnsignedShort != expectedFramesPerBlock) {
                throw new ParserException("Expected frames per block: " + expectedFramesPerBlock + "; got: " + readLittleEndianUnsignedShort);
            }
            int maxBlocksToDecode = Util.ceilDivide(max, readLittleEndianUnsignedShort);
            this.inputData = new byte[header.blockSize * maxBlocksToDecode];
            this.decodedData = new ParsableByteArray(numOutputFramesToBytes(readLittleEndianUnsignedShort, numChannels) * maxBlocksToDecode);
            int bitrate = ((header.frameRateHz * header.blockSize) * 8) / readLittleEndianUnsignedShort;
            this.format = Format.createAudioSampleFormat(null, MimeTypes.AUDIO_RAW, null, bitrate, numOutputFramesToBytes(max, numChannels), header.numChannels, header.frameRateHz, 2, null, null, 0, null);
        }

        @Override // com.google.android.exoplayer2.extractor.wav.WavExtractor.OutputWriter
        public void reset(long timeUs) {
            this.pendingInputBytes = 0;
            this.startTimeUs = timeUs;
            this.pendingOutputBytes = 0;
            this.outputFrameCount = 0L;
        }

        @Override // com.google.android.exoplayer2.extractor.wav.WavExtractor.OutputWriter
        public void init(int dataStartPosition, long dataEndPosition) {
            this.extractorOutput.seekMap(new WavSeekMap(this.header, this.framesPerBlock, dataStartPosition, dataEndPosition));
            this.trackOutput.format(this.format);
        }

        @Override // com.google.android.exoplayer2.extractor.wav.WavExtractor.OutputWriter
        public boolean sampleData(ExtractorInput input, long bytesLeft) throws IOException, InterruptedException {
            int pendingOutputFrames;
            int i;
            int targetFramesRemaining = this.targetSampleSizeFrames - numOutputBytesToFrames(this.pendingOutputBytes);
            int blocksToDecode = Util.ceilDivide(targetFramesRemaining, this.framesPerBlock);
            int targetReadBytes = this.header.blockSize * blocksToDecode;
            boolean endOfSampleData = bytesLeft == 0;
            while (!endOfSampleData) {
                if (this.pendingInputBytes >= targetReadBytes) {
                    break;
                }
                int bytesToRead = (int) Math.min(targetReadBytes - i, bytesLeft);
                int bytesAppended = input.read(this.inputData, this.pendingInputBytes, bytesToRead);
                if (bytesAppended == -1) {
                    endOfSampleData = true;
                } else {
                    this.pendingInputBytes += bytesAppended;
                }
            }
            int pendingBlockCount = this.pendingInputBytes / this.header.blockSize;
            if (pendingBlockCount > 0) {
                decode(this.inputData, pendingBlockCount, this.decodedData);
                this.pendingInputBytes -= this.header.blockSize * pendingBlockCount;
                int decodedDataSize = this.decodedData.limit();
                this.trackOutput.sampleData(this.decodedData, decodedDataSize);
                int i2 = this.pendingOutputBytes + decodedDataSize;
                this.pendingOutputBytes = i2;
                int pendingOutputFrames2 = numOutputBytesToFrames(i2);
                int i3 = this.targetSampleSizeFrames;
                if (pendingOutputFrames2 >= i3) {
                    writeSampleMetadata(i3);
                }
            }
            if (endOfSampleData && (pendingOutputFrames = numOutputBytesToFrames(this.pendingOutputBytes)) > 0) {
                writeSampleMetadata(pendingOutputFrames);
            }
            return endOfSampleData;
        }

        private void writeSampleMetadata(int sampleFrames) {
            long timeUs = this.startTimeUs + Util.scaleLargeTimestamp(this.outputFrameCount, 1000000L, this.header.frameRateHz);
            int size = numOutputFramesToBytes(sampleFrames);
            int offset = this.pendingOutputBytes - size;
            this.trackOutput.sampleMetadata(timeUs, 1, size, offset, null);
            this.outputFrameCount += sampleFrames;
            this.pendingOutputBytes -= size;
        }

        private void decode(byte[] input, int blockCount, ParsableByteArray output) {
            for (int blockIndex = 0; blockIndex < blockCount; blockIndex++) {
                for (int channelIndex = 0; channelIndex < this.header.numChannels; channelIndex++) {
                    decodeBlockForChannel(input, blockIndex, channelIndex, output.data);
                }
            }
            int blockIndex2 = this.framesPerBlock;
            int decodedDataSize = numOutputFramesToBytes(blockIndex2 * blockCount);
            output.reset(decodedDataSize);
        }

        private void decodeBlockForChannel(byte[] input, int blockIndex, int channelIndex, byte[] output) {
            int originalSample;
            int blockSize = this.header.blockSize;
            int numChannels = this.header.numChannels;
            int blockStartIndex = blockIndex * blockSize;
            int headerStartIndex = (channelIndex * 4) + blockStartIndex;
            int dataStartIndex = (numChannels * 4) + headerStartIndex;
            int dataSizeBytes = (blockSize / numChannels) - 4;
            int predictedSample = (short) (((input[headerStartIndex + 1] & 255) << 8) | (input[headerStartIndex] & 255));
            int stepIndex = Math.min(input[headerStartIndex + 2] & 255, 88);
            int step = STEP_TABLE[stepIndex];
            int outputIndex = ((this.framesPerBlock * blockIndex * numChannels) + channelIndex) * 2;
            output[outputIndex] = (byte) (predictedSample & 255);
            output[outputIndex + 1] = (byte) (predictedSample >> 8);
            int i = 0;
            while (i < dataSizeBytes * 2) {
                int dataSegmentIndex = i / 8;
                int dataSegmentOffset = (i / 2) % 4;
                int dataIndex = (dataSegmentIndex * numChannels * 4) + dataStartIndex + dataSegmentOffset;
                int originalSample2 = input[dataIndex] & 255;
                if (i % 2 == 0) {
                    originalSample = originalSample2 & 15;
                } else {
                    originalSample = originalSample2 >> 4;
                }
                int delta = originalSample & 7;
                int difference = (((delta * 2) + 1) * step) >> 3;
                if ((originalSample & 8) != 0) {
                    difference = -difference;
                }
                int blockSize2 = blockSize;
                predictedSample = Util.constrainValue(predictedSample + difference, -32768, 32767);
                outputIndex += numChannels * 2;
                output[outputIndex] = (byte) (predictedSample & 255);
                output[outputIndex + 1] = (byte) (predictedSample >> 8);
                int[] iArr = STEP_TABLE;
                stepIndex = Util.constrainValue(stepIndex + INDEX_TABLE[originalSample], 0, iArr.length - 1);
                step = iArr[stepIndex];
                i++;
                blockSize = blockSize2;
                numChannels = numChannels;
            }
        }

        private int numOutputBytesToFrames(int bytes) {
            return bytes / (this.header.numChannels * 2);
        }

        private int numOutputFramesToBytes(int frames) {
            return numOutputFramesToBytes(frames, this.header.numChannels);
        }

        private static int numOutputFramesToBytes(int frames, int numChannels) {
            return frames * 2 * numChannels;
        }
    }
}
