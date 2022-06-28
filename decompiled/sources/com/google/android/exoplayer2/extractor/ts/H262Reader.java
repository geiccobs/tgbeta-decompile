package com.google.android.exoplayer2.extractor.ts;

import android.util.Pair;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.extractor.ts.TsPayloadReader;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.NalUnitUtil;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.util.Arrays;
import java.util.Collections;
/* loaded from: classes3.dex */
public final class H262Reader implements ElementaryStreamReader {
    private static final double[] FRAME_RATE_VALUES = {23.976023976023978d, 24.0d, 25.0d, 29.97002997002997d, 30.0d, 50.0d, 59.94005994005994d, 60.0d};
    private static final int START_EXTENSION = 181;
    private static final int START_GROUP = 184;
    private static final int START_PICTURE = 0;
    private static final int START_SEQUENCE_HEADER = 179;
    private static final int START_USER_DATA = 178;
    private final CsdBuffer csdBuffer;
    private String formatId;
    private long frameDurationUs;
    private boolean hasOutputFormat;
    private TrackOutput output;
    private long pesTimeUs;
    private final boolean[] prefixFlags;
    private boolean sampleHasPicture;
    private boolean sampleIsKeyframe;
    private long samplePosition;
    private long sampleTimeUs;
    private boolean startedFirstSample;
    private long totalBytesWritten;
    private final NalUnitTargetBuffer userData;
    private final ParsableByteArray userDataParsable;
    private final UserDataReader userDataReader;

    public H262Reader() {
        this(null);
    }

    public H262Reader(UserDataReader userDataReader) {
        this.userDataReader = userDataReader;
        this.prefixFlags = new boolean[4];
        this.csdBuffer = new CsdBuffer(128);
        if (userDataReader != null) {
            this.userData = new NalUnitTargetBuffer(START_USER_DATA, 128);
            this.userDataParsable = new ParsableByteArray();
            return;
        }
        this.userData = null;
        this.userDataParsable = null;
    }

    @Override // com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader
    public void seek() {
        NalUnitUtil.clearPrefixFlags(this.prefixFlags);
        this.csdBuffer.reset();
        if (this.userDataReader != null) {
            this.userData.reset();
        }
        this.totalBytesWritten = 0L;
        this.startedFirstSample = false;
    }

    @Override // com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader
    public void createTracks(ExtractorOutput extractorOutput, TsPayloadReader.TrackIdGenerator idGenerator) {
        idGenerator.generateNewId();
        this.formatId = idGenerator.getFormatId();
        this.output = extractorOutput.track(idGenerator.getTrackId(), 2);
        UserDataReader userDataReader = this.userDataReader;
        if (userDataReader != null) {
            userDataReader.createTracks(extractorOutput, idGenerator);
        }
    }

    @Override // com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader
    public void packetStarted(long pesTimeUs, int flags) {
        this.pesTimeUs = pesTimeUs;
    }

    @Override // com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader
    public void consume(ParsableByteArray data) {
        boolean z;
        int offset = data.getPosition();
        int limit = data.limit();
        byte[] dataArray = data.data;
        this.totalBytesWritten += data.bytesLeft();
        this.output.sampleData(data, data.bytesLeft());
        while (true) {
            int startCodeOffset = NalUnitUtil.findNalUnit(dataArray, offset, limit, this.prefixFlags);
            if (startCodeOffset == limit) {
                break;
            }
            int startCodeValue = data.data[startCodeOffset + 3] & 255;
            int lengthToStartCode = startCodeOffset - offset;
            if (!this.hasOutputFormat) {
                if (lengthToStartCode > 0) {
                    this.csdBuffer.onData(dataArray, offset, startCodeOffset);
                }
                int bytesAlreadyPassed = lengthToStartCode < 0 ? -lengthToStartCode : 0;
                if (this.csdBuffer.onStartCode(startCodeValue, bytesAlreadyPassed)) {
                    Pair<Format, Long> result = parseCsdBuffer(this.csdBuffer, this.formatId);
                    this.output.format((Format) result.first);
                    this.frameDurationUs = ((Long) result.second).longValue();
                    this.hasOutputFormat = true;
                }
            }
            if (this.userDataReader != null) {
                int bytesAlreadyPassed2 = 0;
                if (lengthToStartCode > 0) {
                    this.userData.appendToNalUnit(dataArray, offset, startCodeOffset);
                } else {
                    bytesAlreadyPassed2 = -lengthToStartCode;
                }
                if (this.userData.endNalUnit(bytesAlreadyPassed2)) {
                    int unescapedLength = NalUnitUtil.unescapeStream(this.userData.nalData, this.userData.nalLength);
                    this.userDataParsable.reset(this.userData.nalData, unescapedLength);
                    this.userDataReader.consume(this.sampleTimeUs, this.userDataParsable);
                }
                if (startCodeValue == START_USER_DATA && data.data[startCodeOffset + 2] == 1) {
                    this.userData.startNalUnit(startCodeValue);
                }
            }
            if (startCodeValue == 0 || startCodeValue == START_SEQUENCE_HEADER) {
                int bytesWrittenPastStartCode = limit - startCodeOffset;
                if (this.startedFirstSample && this.sampleHasPicture && this.hasOutputFormat) {
                    boolean z2 = this.sampleIsKeyframe;
                    int size = ((int) (this.totalBytesWritten - this.samplePosition)) - bytesWrittenPastStartCode;
                    TrackOutput trackOutput = this.output;
                    long j = this.sampleTimeUs;
                    int flags = z2 ? 1 : 0;
                    trackOutput.sampleMetadata(j, flags, size, bytesWrittenPastStartCode, null);
                }
                boolean z3 = this.startedFirstSample;
                if (!z3 || this.sampleHasPicture) {
                    this.samplePosition = this.totalBytesWritten - bytesWrittenPastStartCode;
                    long j2 = this.pesTimeUs;
                    if (j2 == C.TIME_UNSET) {
                        j2 = z3 ? this.sampleTimeUs + this.frameDurationUs : 0L;
                    }
                    this.sampleTimeUs = j2;
                    z = false;
                    this.sampleIsKeyframe = false;
                    this.pesTimeUs = C.TIME_UNSET;
                    this.startedFirstSample = true;
                } else {
                    z = false;
                }
                if (startCodeValue == 0) {
                    z = true;
                }
                this.sampleHasPicture = z;
            } else if (startCodeValue == START_GROUP) {
                this.sampleIsKeyframe = true;
            }
            offset = startCodeOffset + 3;
        }
        if (!this.hasOutputFormat) {
            this.csdBuffer.onData(dataArray, offset, limit);
        }
        if (this.userDataReader != null) {
            this.userData.appendToNalUnit(dataArray, offset, limit);
        }
    }

    @Override // com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader
    public void packetFinished() {
    }

    private static Pair<Format, Long> parseCsdBuffer(CsdBuffer csdBuffer, String formatId) {
        float pixelWidthHeightRatio;
        byte[] csdData = Arrays.copyOf(csdBuffer.data, csdBuffer.length);
        int firstByte = csdData[4] & 255;
        int secondByte = csdData[5] & 255;
        int thirdByte = csdData[6] & 255;
        int width = (firstByte << 4) | (secondByte >> 4);
        int height = ((secondByte & 15) << 8) | thirdByte;
        int aspectRatioCode = (csdData[7] & 240) >> 4;
        switch (aspectRatioCode) {
            case 2:
                float pixelWidthHeightRatio2 = (height * 4) / (width * 3);
                pixelWidthHeightRatio = pixelWidthHeightRatio2;
                break;
            case 3:
                float pixelWidthHeightRatio3 = (height * 16) / (width * 9);
                pixelWidthHeightRatio = pixelWidthHeightRatio3;
                break;
            case 4:
                float pixelWidthHeightRatio4 = (height * 121) / (width * 100);
                pixelWidthHeightRatio = pixelWidthHeightRatio4;
                break;
            default:
                pixelWidthHeightRatio = 1.0f;
                break;
        }
        Format format = Format.createVideoSampleFormat(formatId, MimeTypes.VIDEO_MPEG2, null, -1, -1, width, height, -1.0f, Collections.singletonList(csdData), -1, pixelWidthHeightRatio, null);
        long frameDurationUs = 0;
        int frameRateCodeMinusOne = (csdData[7] & 15) - 1;
        if (frameRateCodeMinusOne >= 0) {
            double[] dArr = FRAME_RATE_VALUES;
            if (frameRateCodeMinusOne < dArr.length) {
                double frameRate = dArr[frameRateCodeMinusOne];
                int sequenceExtensionPosition = csdBuffer.sequenceExtensionPosition;
                int frameRateExtensionN = (csdData[sequenceExtensionPosition + 9] & 96) >> 5;
                int frameRateExtensionD = csdData[sequenceExtensionPosition + 9] & 31;
                if (frameRateExtensionN != frameRateExtensionD) {
                    double d = frameRateExtensionN;
                    Double.isNaN(d);
                    int firstByte2 = frameRateExtensionD + 1;
                    double d2 = firstByte2;
                    Double.isNaN(d2);
                    frameRate *= (d + 1.0d) / d2;
                }
                frameDurationUs = (long) (1000000.0d / frameRate);
                return Pair.create(format, Long.valueOf(frameDurationUs));
            }
        }
        return Pair.create(format, Long.valueOf(frameDurationUs));
    }

    /* loaded from: classes3.dex */
    public static final class CsdBuffer {
        private static final byte[] START_CODE = {0, 0, 1};
        public byte[] data;
        private boolean isFilling;
        public int length;
        public int sequenceExtensionPosition;

        public CsdBuffer(int initialCapacity) {
            this.data = new byte[initialCapacity];
        }

        public void reset() {
            this.isFilling = false;
            this.length = 0;
            this.sequenceExtensionPosition = 0;
        }

        public boolean onStartCode(int startCodeValue, int bytesAlreadyPassed) {
            if (this.isFilling) {
                int i = this.length - bytesAlreadyPassed;
                this.length = i;
                if (this.sequenceExtensionPosition == 0 && startCodeValue == H262Reader.START_EXTENSION) {
                    this.sequenceExtensionPosition = i;
                } else {
                    this.isFilling = false;
                    return true;
                }
            } else if (startCodeValue == H262Reader.START_SEQUENCE_HEADER) {
                this.isFilling = true;
            }
            byte[] bArr = START_CODE;
            onData(bArr, 0, bArr.length);
            return false;
        }

        public void onData(byte[] newData, int offset, int limit) {
            if (!this.isFilling) {
                return;
            }
            int readLength = limit - offset;
            byte[] bArr = this.data;
            int length = bArr.length;
            int i = this.length;
            if (length < i + readLength) {
                this.data = Arrays.copyOf(bArr, (i + readLength) * 2);
            }
            System.arraycopy(newData, offset, this.data, this.length, readLength);
            this.length += readLength;
        }
    }
}
