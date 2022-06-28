package com.google.android.exoplayer2.extractor.mp3;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.GaplessInfoHolder;
import com.google.android.exoplayer2.extractor.Id3Peeker;
import com.google.android.exoplayer2.extractor.MpegAudioHeader;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.extractor.mp3.Seeker;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.id3.Id3Decoder;
import com.google.android.exoplayer2.metadata.id3.MlltFrame;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.io.EOFException;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes3.dex */
public final class Mp3Extractor implements Extractor {
    public static final int FLAG_DISABLE_ID3_METADATA = 2;
    public static final int FLAG_ENABLE_CONSTANT_BITRATE_SEEKING = 1;
    private static final int MAX_SNIFF_BYTES = 16384;
    private static final int MAX_SYNC_BYTES = 131072;
    private static final int MPEG_AUDIO_HEADER_MASK = -128000;
    private static final int SCRATCH_LENGTH = 10;
    private static final int SEEK_HEADER_INFO = 1231971951;
    private static final int SEEK_HEADER_UNSET = 0;
    private static final int SEEK_HEADER_VBRI = 1447187017;
    private static final int SEEK_HEADER_XING = 1483304551;
    private long basisTimeUs;
    private boolean disableSeeking;
    private ExtractorOutput extractorOutput;
    private long firstSamplePosition;
    private final int flags;
    private final long forcedFirstSampleTimestampUs;
    private final GaplessInfoHolder gaplessInfoHolder;
    private final Id3Peeker id3Peeker;
    private Metadata metadata;
    private int sampleBytesRemaining;
    private long samplesRead;
    private final ParsableByteArray scratch;
    private Seeker seeker;
    private final MpegAudioHeader synchronizedHeader;
    private int synchronizedHeaderData;
    private TrackOutput trackOutput;
    public static final ExtractorsFactory FACTORY = Mp3Extractor$$ExternalSyntheticLambda0.INSTANCE;
    private static final Id3Decoder.FramePredicate REQUIRED_ID3_FRAME_PREDICATE = Mp3Extractor$$ExternalSyntheticLambda1.INSTANCE;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Flags {
    }

    public static /* synthetic */ Extractor[] lambda$static$0() {
        return new Extractor[]{new Mp3Extractor()};
    }

    public static /* synthetic */ boolean lambda$static$1(int majorVersion, int id0, int id1, int id2, int id3) {
        return (id0 == 67 && id1 == 79 && id2 == 77 && (id3 == 77 || majorVersion == 2)) || (id0 == 77 && id1 == 76 && id2 == 76 && (id3 == 84 || majorVersion == 2));
    }

    public Mp3Extractor() {
        this(0);
    }

    public Mp3Extractor(int flags) {
        this(flags, C.TIME_UNSET);
    }

    public Mp3Extractor(int flags, long forcedFirstSampleTimestampUs) {
        this.flags = flags;
        this.forcedFirstSampleTimestampUs = forcedFirstSampleTimestampUs;
        this.scratch = new ParsableByteArray(10);
        this.synchronizedHeader = new MpegAudioHeader();
        this.gaplessInfoHolder = new GaplessInfoHolder();
        this.basisTimeUs = C.TIME_UNSET;
        this.id3Peeker = new Id3Peeker();
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        return synchronize(input, true);
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public void init(ExtractorOutput output) {
        this.extractorOutput = output;
        this.trackOutput = output.track(0, 1);
        this.extractorOutput.endTracks();
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public void seek(long position, long timeUs) {
        this.synchronizedHeaderData = 0;
        this.basisTimeUs = C.TIME_UNSET;
        this.samplesRead = 0L;
        this.sampleBytesRemaining = 0;
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public void release() {
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        if (this.synchronizedHeaderData == 0) {
            try {
                synchronize(input, false);
            } catch (EOFException e) {
                return -1;
            }
        }
        if (this.seeker == null) {
            Seeker seekFrameSeeker = maybeReadSeekFrame(input);
            Seeker metadataSeeker = maybeHandleSeekMetadata(this.metadata, input.getPosition());
            if (this.disableSeeking) {
                this.seeker = new Seeker.UnseekableSeeker();
            } else {
                if (metadataSeeker != null) {
                    this.seeker = metadataSeeker;
                } else if (seekFrameSeeker != null) {
                    this.seeker = seekFrameSeeker;
                }
                Seeker seeker = this.seeker;
                if (seeker == null || (!seeker.isSeekable() && (this.flags & 1) != 0)) {
                    this.seeker = getConstantBitrateSeeker(input);
                }
            }
            this.extractorOutput.seekMap(this.seeker);
            this.trackOutput.format(Format.createAudioSampleFormat(null, this.synchronizedHeader.mimeType, null, -1, 4096, this.synchronizedHeader.channels, this.synchronizedHeader.sampleRate, -1, this.gaplessInfoHolder.encoderDelay, this.gaplessInfoHolder.encoderPadding, null, null, 0, null, (this.flags & 2) != 0 ? null : this.metadata));
            this.firstSamplePosition = input.getPosition();
        } else if (this.firstSamplePosition != 0) {
            long inputPosition = input.getPosition();
            long j = this.firstSamplePosition;
            if (inputPosition < j) {
                input.skipFully((int) (j - inputPosition));
            }
        }
        return readSample(input);
    }

    public void disableSeeking() {
        this.disableSeeking = true;
    }

    private int readSample(ExtractorInput extractorInput) throws IOException, InterruptedException {
        if (this.sampleBytesRemaining == 0) {
            extractorInput.resetPeekPosition();
            if (peekEndOfStreamOrHeader(extractorInput)) {
                return -1;
            }
            this.scratch.setPosition(0);
            int sampleHeaderData = this.scratch.readInt();
            if (!headersMatch(sampleHeaderData, this.synchronizedHeaderData) || MpegAudioHeader.getFrameSize(sampleHeaderData) == -1) {
                extractorInput.skipFully(1);
                this.synchronizedHeaderData = 0;
                return 0;
            }
            MpegAudioHeader.populateHeader(sampleHeaderData, this.synchronizedHeader);
            if (this.basisTimeUs == C.TIME_UNSET) {
                this.basisTimeUs = this.seeker.getTimeUs(extractorInput.getPosition());
                if (this.forcedFirstSampleTimestampUs != C.TIME_UNSET) {
                    long embeddedFirstSampleTimestampUs = this.seeker.getTimeUs(0L);
                    this.basisTimeUs += this.forcedFirstSampleTimestampUs - embeddedFirstSampleTimestampUs;
                }
            }
            this.sampleBytesRemaining = this.synchronizedHeader.frameSize;
        }
        int bytesAppended = this.trackOutput.sampleData(extractorInput, this.sampleBytesRemaining, true);
        if (bytesAppended == -1) {
            return -1;
        }
        int i = this.sampleBytesRemaining - bytesAppended;
        this.sampleBytesRemaining = i;
        if (i > 0) {
            return 0;
        }
        long timeUs = this.basisTimeUs + ((this.samplesRead * 1000000) / this.synchronizedHeader.sampleRate);
        this.trackOutput.sampleMetadata(timeUs, 1, this.synchronizedHeader.frameSize, 0, null);
        this.samplesRead += this.synchronizedHeader.samplesPerFrame;
        this.sampleBytesRemaining = 0;
        return 0;
    }

    /* JADX WARN: Code restructure failed: missing block: B:48:0x009d, code lost:
        if (r14 == false) goto L50;
     */
    /* JADX WARN: Code restructure failed: missing block: B:49:0x009f, code lost:
        r13.skipFully(r2 + r3);
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x00a5, code lost:
        r13.resetPeekPosition();
     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x00a8, code lost:
        r12.synchronizedHeaderData = r1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x00aa, code lost:
        return true;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean synchronize(com.google.android.exoplayer2.extractor.ExtractorInput r13, boolean r14) throws java.io.IOException, java.lang.InterruptedException {
        /*
            r12 = this;
            r0 = 0
            r1 = 0
            r2 = 0
            r3 = 0
            if (r14 == 0) goto L9
            r4 = 16384(0x4000, float:2.2959E-41)
            goto Lb
        L9:
            r4 = 131072(0x20000, float:1.83671E-40)
        Lb:
            r13.resetPeekPosition()
            long r5 = r13.getPosition()
            r7 = 0
            r9 = 0
            r10 = 1
            int r11 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r11 != 0) goto L42
            int r5 = r12.flags
            r5 = r5 & 2
            if (r5 != 0) goto L22
            r5 = 1
            goto L23
        L22:
            r5 = 0
        L23:
            if (r5 == 0) goto L27
            r6 = 0
            goto L29
        L27:
            com.google.android.exoplayer2.metadata.id3.Id3Decoder$FramePredicate r6 = com.google.android.exoplayer2.extractor.mp3.Mp3Extractor.REQUIRED_ID3_FRAME_PREDICATE
        L29:
            com.google.android.exoplayer2.extractor.Id3Peeker r7 = r12.id3Peeker
            com.google.android.exoplayer2.metadata.Metadata r7 = r7.peekId3Data(r13, r6)
            r12.metadata = r7
            if (r7 == 0) goto L38
            com.google.android.exoplayer2.extractor.GaplessInfoHolder r8 = r12.gaplessInfoHolder
            r8.setFromMetadata(r7)
        L38:
            long r7 = r13.getPeekPosition()
            int r2 = (int) r7
            if (r14 != 0) goto L42
            r13.skipFully(r2)
        L42:
            boolean r5 = r12.peekEndOfStreamOrHeader(r13)
            if (r5 == 0) goto L51
            if (r0 <= 0) goto L4b
            goto L9d
        L4b:
            java.io.EOFException r5 = new java.io.EOFException
            r5.<init>()
            throw r5
        L51:
            com.google.android.exoplayer2.util.ParsableByteArray r5 = r12.scratch
            r5.setPosition(r9)
            com.google.android.exoplayer2.util.ParsableByteArray r5 = r12.scratch
            int r5 = r5.readInt()
            if (r1 == 0) goto L65
            long r6 = (long) r1
            boolean r6 = headersMatch(r5, r6)
            if (r6 == 0) goto L6d
        L65:
            int r6 = com.google.android.exoplayer2.extractor.MpegAudioHeader.getFrameSize(r5)
            r7 = r6
            r8 = -1
            if (r6 != r8) goto L8e
        L6d:
            int r6 = r3 + 1
            if (r3 != r4) goto L7c
            if (r14 == 0) goto L74
            return r9
        L74:
            com.google.android.exoplayer2.ParserException r3 = new com.google.android.exoplayer2.ParserException
            java.lang.String r7 = "Searched too many bytes."
            r3.<init>(r7)
            throw r3
        L7c:
            r0 = 0
            r1 = 0
            if (r14 == 0) goto L89
            r13.resetPeekPosition()
            int r3 = r2 + r6
            r13.advancePeekPosition(r3)
            goto L8c
        L89:
            r13.skipFully(r10)
        L8c:
            r3 = r6
            goto Lb0
        L8e:
            int r0 = r0 + 1
            if (r0 != r10) goto L99
            com.google.android.exoplayer2.extractor.MpegAudioHeader r6 = r12.synchronizedHeader
            com.google.android.exoplayer2.extractor.MpegAudioHeader.populateHeader(r5, r6)
            r1 = r5
            goto Lab
        L99:
            r6 = 4
            if (r0 != r6) goto Lab
        L9d:
            if (r14 == 0) goto La5
            int r5 = r2 + r3
            r13.skipFully(r5)
            goto La8
        La5:
            r13.resetPeekPosition()
        La8:
            r12.synchronizedHeaderData = r1
            return r10
        Lab:
            int r6 = r7 + (-4)
            r13.advancePeekPosition(r6)
        Lb0:
            goto L42
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.mp3.Mp3Extractor.synchronize(com.google.android.exoplayer2.extractor.ExtractorInput, boolean):boolean");
    }

    private boolean peekEndOfStreamOrHeader(ExtractorInput extractorInput) throws IOException, InterruptedException {
        Seeker seeker = this.seeker;
        if (seeker != null) {
            long dataEndPosition = seeker.getDataEndPosition();
            if (dataEndPosition != -1 && extractorInput.getPeekPosition() > dataEndPosition - 4) {
                return true;
            }
        }
        try {
            return !extractorInput.peekFully(this.scratch.data, 0, 4, true);
        } catch (EOFException e) {
            return true;
        }
    }

    private Seeker maybeReadSeekFrame(ExtractorInput input) throws IOException, InterruptedException {
        ParsableByteArray frame = new ParsableByteArray(this.synchronizedHeader.frameSize);
        input.peekFully(frame.data, 0, this.synchronizedHeader.frameSize);
        int i = 21;
        if ((this.synchronizedHeader.version & 1) != 0) {
            if (this.synchronizedHeader.channels != 1) {
                i = 36;
            }
        } else if (this.synchronizedHeader.channels == 1) {
            i = 13;
        }
        int xingBase = i;
        int seekHeader = getSeekFrameHeader(frame, xingBase);
        if (seekHeader == SEEK_HEADER_XING || seekHeader == SEEK_HEADER_INFO) {
            Seeker seeker = XingSeeker.create(input.getLength(), input.getPosition(), this.synchronizedHeader, frame);
            if (seeker != null && !this.gaplessInfoHolder.hasGaplessInfo()) {
                input.resetPeekPosition();
                input.advancePeekPosition(xingBase + 141);
                input.peekFully(this.scratch.data, 0, 3);
                this.scratch.setPosition(0);
                this.gaplessInfoHolder.setFromXingHeaderValue(this.scratch.readUnsignedInt24());
            }
            input.skipFully(this.synchronizedHeader.frameSize);
            if (seeker != null && !seeker.isSeekable() && seekHeader == SEEK_HEADER_INFO) {
                return getConstantBitrateSeeker(input);
            }
            return seeker;
        } else if (seekHeader == SEEK_HEADER_VBRI) {
            Seeker seeker2 = VbriSeeker.create(input.getLength(), input.getPosition(), this.synchronizedHeader, frame);
            input.skipFully(this.synchronizedHeader.frameSize);
            return seeker2;
        } else {
            input.resetPeekPosition();
            return null;
        }
    }

    private Seeker getConstantBitrateSeeker(ExtractorInput input) throws IOException, InterruptedException {
        input.peekFully(this.scratch.data, 0, 4);
        this.scratch.setPosition(0);
        MpegAudioHeader.populateHeader(this.scratch.readInt(), this.synchronizedHeader);
        return new ConstantBitrateSeeker(input.getLength(), input.getPosition(), this.synchronizedHeader);
    }

    private static boolean headersMatch(int headerA, long headerB) {
        return ((long) (MPEG_AUDIO_HEADER_MASK & headerA)) == ((-128000) & headerB);
    }

    private static int getSeekFrameHeader(ParsableByteArray frame, int xingBase) {
        if (frame.limit() >= xingBase + 4) {
            frame.setPosition(xingBase);
            int headerData = frame.readInt();
            if (headerData == SEEK_HEADER_XING || headerData == SEEK_HEADER_INFO) {
                return headerData;
            }
        }
        if (frame.limit() >= 40) {
            frame.setPosition(36);
            if (frame.readInt() != SEEK_HEADER_VBRI) {
                return 0;
            }
            return SEEK_HEADER_VBRI;
        }
        return 0;
    }

    private static MlltSeeker maybeHandleSeekMetadata(Metadata metadata, long firstFramePosition) {
        if (metadata != null) {
            int length = metadata.length();
            for (int i = 0; i < length; i++) {
                Metadata.Entry entry = metadata.get(i);
                if (entry instanceof MlltFrame) {
                    return MlltSeeker.create(firstFramePosition, (MlltFrame) entry);
                }
            }
            return null;
        }
        return null;
    }
}
