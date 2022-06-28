package com.google.android.exoplayer2.extractor.ts;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.ConstantBitrateSeekMap;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.ts.TsPayloadReader;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ParsableBitArray;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes3.dex */
public final class AdtsExtractor implements Extractor {
    public static final ExtractorsFactory FACTORY = AdtsExtractor$$ExternalSyntheticLambda0.INSTANCE;
    public static final int FLAG_ENABLE_CONSTANT_BITRATE_SEEKING = 1;
    private static final int MAX_PACKET_SIZE = 2048;
    private static final int MAX_SNIFF_BYTES = 8192;
    private static final int NUM_FRAMES_FOR_AVERAGE_FRAME_SIZE = 1000;
    private int averageFrameSize;
    private ExtractorOutput extractorOutput;
    private long firstFramePosition;
    private long firstSampleTimestampUs;
    private final int flags;
    private boolean hasCalculatedAverageFrameSize;
    private boolean hasOutputSeekMap;
    private final ParsableByteArray packetBuffer;
    private final AdtsReader reader;
    private final ParsableByteArray scratch;
    private final ParsableBitArray scratchBits;
    private boolean startedPacket;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Flags {
    }

    public static /* synthetic */ Extractor[] lambda$static$0() {
        return new Extractor[]{new AdtsExtractor()};
    }

    public AdtsExtractor() {
        this(0);
    }

    public AdtsExtractor(int flags) {
        this.flags = flags;
        this.reader = new AdtsReader(true);
        this.packetBuffer = new ParsableByteArray(2048);
        this.averageFrameSize = -1;
        this.firstFramePosition = -1L;
        ParsableByteArray parsableByteArray = new ParsableByteArray(10);
        this.scratch = parsableByteArray;
        this.scratchBits = new ParsableBitArray(parsableByteArray.data);
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        int startPosition = peekId3Header(input);
        int headerPosition = startPosition;
        int totalValidFramesSize = 0;
        int validFramesCount = 0;
        while (true) {
            input.peekFully(this.scratch.data, 0, 2);
            this.scratch.setPosition(0);
            int syncBytes = this.scratch.readUnsignedShort();
            if (!AdtsReader.isAdtsSyncWord(syncBytes)) {
                validFramesCount = 0;
                totalValidFramesSize = 0;
                input.resetPeekPosition();
                headerPosition++;
                if (headerPosition - startPosition >= 8192) {
                    return false;
                }
                input.advancePeekPosition(headerPosition);
            } else {
                validFramesCount++;
                if (validFramesCount >= 4 && totalValidFramesSize > 188) {
                    return true;
                }
                input.peekFully(this.scratch.data, 0, 4);
                this.scratchBits.setPosition(14);
                int frameSize = this.scratchBits.readBits(13);
                if (frameSize <= 6) {
                    return false;
                }
                input.advancePeekPosition(frameSize - 6);
                totalValidFramesSize += frameSize;
            }
        }
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public void init(ExtractorOutput output) {
        this.extractorOutput = output;
        this.reader.createTracks(output, new TsPayloadReader.TrackIdGenerator(0, 1));
        output.endTracks();
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public void seek(long position, long timeUs) {
        this.startedPacket = false;
        this.reader.seek();
        this.firstSampleTimestampUs = timeUs;
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public void release() {
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        long inputLength = input.getLength();
        boolean canUseConstantBitrateSeeking = ((this.flags & 1) == 0 || inputLength == -1) ? false : true;
        if (canUseConstantBitrateSeeking) {
            calculateAverageFrameSize(input);
        }
        int bytesRead = input.read(this.packetBuffer.data, 0, 2048);
        boolean readEndOfStream = bytesRead == -1;
        maybeOutputSeekMap(inputLength, canUseConstantBitrateSeeking, readEndOfStream);
        if (readEndOfStream) {
            return -1;
        }
        this.packetBuffer.setPosition(0);
        this.packetBuffer.setLimit(bytesRead);
        if (!this.startedPacket) {
            this.reader.packetStarted(this.firstSampleTimestampUs, 4);
            this.startedPacket = true;
        }
        this.reader.consume(this.packetBuffer);
        return 0;
    }

    private int peekId3Header(ExtractorInput input) throws IOException, InterruptedException {
        int firstFramePosition = 0;
        while (true) {
            input.peekFully(this.scratch.data, 0, 10);
            this.scratch.setPosition(0);
            if (this.scratch.readUnsignedInt24() != 4801587) {
                break;
            }
            this.scratch.skipBytes(3);
            int length = this.scratch.readSynchSafeInt();
            firstFramePosition += length + 10;
            input.advancePeekPosition(length);
        }
        input.resetPeekPosition();
        input.advancePeekPosition(firstFramePosition);
        if (this.firstFramePosition == -1) {
            this.firstFramePosition = firstFramePosition;
        }
        return firstFramePosition;
    }

    private void maybeOutputSeekMap(long inputLength, boolean canUseConstantBitrateSeeking, boolean readEndOfStream) {
        if (this.hasOutputSeekMap) {
            return;
        }
        boolean useConstantBitrateSeeking = canUseConstantBitrateSeeking && this.averageFrameSize > 0;
        if (useConstantBitrateSeeking && this.reader.getSampleDurationUs() == C.TIME_UNSET && !readEndOfStream) {
            return;
        }
        ExtractorOutput extractorOutput = (ExtractorOutput) Assertions.checkNotNull(this.extractorOutput);
        if (useConstantBitrateSeeking && this.reader.getSampleDurationUs() != C.TIME_UNSET) {
            extractorOutput.seekMap(getConstantBitrateSeekMap(inputLength));
        } else {
            extractorOutput.seekMap(new SeekMap.Unseekable(C.TIME_UNSET));
        }
        this.hasOutputSeekMap = true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:27:0x006c, code lost:
        r9.hasCalculatedAverageFrameSize = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x0075, code lost:
        throw new com.google.android.exoplayer2.ParserException("Malformed ADTS stream");
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void calculateAverageFrameSize(com.google.android.exoplayer2.extractor.ExtractorInput r10) throws java.io.IOException, java.lang.InterruptedException {
        /*
            r9 = this;
            boolean r0 = r9.hasCalculatedAverageFrameSize
            if (r0 == 0) goto L5
            return
        L5:
            r0 = -1
            r9.averageFrameSize = r0
            r10.resetPeekPosition()
            long r1 = r10.getPosition()
            r3 = 0
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 != 0) goto L18
            r9.peekId3Header(r10)
        L18:
            r1 = 0
            r2 = 0
        L1b:
            r4 = 1
            com.google.android.exoplayer2.util.ParsableByteArray r5 = r9.scratch     // Catch: java.io.EOFException -> L77
            byte[] r5 = r5.data     // Catch: java.io.EOFException -> L77
            r6 = 2
            r7 = 0
            boolean r5 = r10.peekFully(r5, r7, r6, r4)     // Catch: java.io.EOFException -> L77
            if (r5 == 0) goto L76
            com.google.android.exoplayer2.util.ParsableByteArray r5 = r9.scratch     // Catch: java.io.EOFException -> L77
            r5.setPosition(r7)     // Catch: java.io.EOFException -> L77
            com.google.android.exoplayer2.util.ParsableByteArray r5 = r9.scratch     // Catch: java.io.EOFException -> L77
            int r5 = r5.readUnsignedShort()     // Catch: java.io.EOFException -> L77
            boolean r6 = com.google.android.exoplayer2.extractor.ts.AdtsReader.isAdtsSyncWord(r5)     // Catch: java.io.EOFException -> L77
            if (r6 != 0) goto L3b
            r1 = 0
            goto L76
        L3b:
            com.google.android.exoplayer2.util.ParsableByteArray r6 = r9.scratch     // Catch: java.io.EOFException -> L77
            byte[] r6 = r6.data     // Catch: java.io.EOFException -> L77
            r8 = 4
            boolean r6 = r10.peekFully(r6, r7, r8, r4)     // Catch: java.io.EOFException -> L77
            if (r6 != 0) goto L47
            goto L76
        L47:
            com.google.android.exoplayer2.util.ParsableBitArray r6 = r9.scratchBits     // Catch: java.io.EOFException -> L77
            r7 = 14
            r6.setPosition(r7)     // Catch: java.io.EOFException -> L77
            com.google.android.exoplayer2.util.ParsableBitArray r6 = r9.scratchBits     // Catch: java.io.EOFException -> L77
            r7 = 13
            int r6 = r6.readBits(r7)     // Catch: java.io.EOFException -> L77
            r7 = 6
            if (r6 <= r7) goto L6c
            long r7 = (long) r6     // Catch: java.io.EOFException -> L77
            long r2 = r2 + r7
            int r1 = r1 + 1
            r7 = 1000(0x3e8, float:1.401E-42)
            if (r1 != r7) goto L62
            goto L76
        L62:
            int r7 = r6 + (-6)
            boolean r7 = r10.advancePeekPosition(r7, r4)     // Catch: java.io.EOFException -> L77
            if (r7 != 0) goto L6b
            goto L76
        L6b:
            goto L1b
        L6c:
            r9.hasCalculatedAverageFrameSize = r4     // Catch: java.io.EOFException -> L77
            com.google.android.exoplayer2.ParserException r7 = new com.google.android.exoplayer2.ParserException     // Catch: java.io.EOFException -> L77
            java.lang.String r8 = "Malformed ADTS stream"
            r7.<init>(r8)     // Catch: java.io.EOFException -> L77
            throw r7     // Catch: java.io.EOFException -> L77
        L76:
            goto L78
        L77:
            r5 = move-exception
        L78:
            r10.resetPeekPosition()
            if (r1 <= 0) goto L84
            long r5 = (long) r1
            long r5 = r2 / r5
            int r0 = (int) r5
            r9.averageFrameSize = r0
            goto L86
        L84:
            r9.averageFrameSize = r0
        L86:
            r9.hasCalculatedAverageFrameSize = r4
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.ts.AdtsExtractor.calculateAverageFrameSize(com.google.android.exoplayer2.extractor.ExtractorInput):void");
    }

    private SeekMap getConstantBitrateSeekMap(long inputLength) {
        int bitrate = getBitrateFromFrameSize(this.averageFrameSize, this.reader.getSampleDurationUs());
        return new ConstantBitrateSeekMap(inputLength, this.firstFramePosition, bitrate, this.averageFrameSize);
    }

    private static int getBitrateFromFrameSize(int frameSize, long durationUsPerFrame) {
        return (int) (((frameSize * 8) * 1000000) / durationUsPerFrame);
    }
}
