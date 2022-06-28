package com.google.android.exoplayer2.extractor.ts;

import android.util.SparseArray;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.extractor.ts.TsPayloadReader;
import com.google.android.exoplayer2.util.CodecSpecificDataUtil;
import com.google.android.exoplayer2.util.NalUnitUtil;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.ParsableNalUnitBitArray;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes3.dex */
public final class H264Reader implements ElementaryStreamReader {
    private static final int NAL_UNIT_TYPE_PPS = 8;
    private static final int NAL_UNIT_TYPE_SEI = 6;
    private static final int NAL_UNIT_TYPE_SPS = 7;
    private final boolean allowNonIdrKeyframes;
    private final boolean detectAccessUnits;
    private String formatId;
    private boolean hasOutputFormat;
    private TrackOutput output;
    private long pesTimeUs;
    private boolean randomAccessIndicator;
    private SampleReader sampleReader;
    private final SeiReader seiReader;
    private long totalBytesWritten;
    private final boolean[] prefixFlags = new boolean[3];
    private final NalUnitTargetBuffer sps = new NalUnitTargetBuffer(7, 128);
    private final NalUnitTargetBuffer pps = new NalUnitTargetBuffer(8, 128);
    private final NalUnitTargetBuffer sei = new NalUnitTargetBuffer(6, 128);
    private final ParsableByteArray seiWrapper = new ParsableByteArray();

    public H264Reader(SeiReader seiReader, boolean allowNonIdrKeyframes, boolean detectAccessUnits) {
        this.seiReader = seiReader;
        this.allowNonIdrKeyframes = allowNonIdrKeyframes;
        this.detectAccessUnits = detectAccessUnits;
    }

    @Override // com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader
    public void seek() {
        NalUnitUtil.clearPrefixFlags(this.prefixFlags);
        this.sps.reset();
        this.pps.reset();
        this.sei.reset();
        this.sampleReader.reset();
        this.totalBytesWritten = 0L;
        this.randomAccessIndicator = false;
    }

    @Override // com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader
    public void createTracks(ExtractorOutput extractorOutput, TsPayloadReader.TrackIdGenerator idGenerator) {
        idGenerator.generateNewId();
        this.formatId = idGenerator.getFormatId();
        this.output = extractorOutput.track(idGenerator.getTrackId(), 2);
        this.sampleReader = new SampleReader(this.output, this.allowNonIdrKeyframes, this.detectAccessUnits);
        this.seiReader.createTracks(extractorOutput, idGenerator);
    }

    @Override // com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader
    public void packetStarted(long pesTimeUs, int flags) {
        this.pesTimeUs = pesTimeUs;
        this.randomAccessIndicator |= (flags & 2) != 0;
    }

    @Override // com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader
    public void consume(ParsableByteArray data) {
        int offset = data.getPosition();
        int limit = data.limit();
        byte[] dataArray = data.data;
        this.totalBytesWritten += data.bytesLeft();
        this.output.sampleData(data, data.bytesLeft());
        int offset2 = offset;
        while (true) {
            int nalUnitOffset = NalUnitUtil.findNalUnit(dataArray, offset2, limit, this.prefixFlags);
            if (nalUnitOffset == limit) {
                nalUnitData(dataArray, offset2, limit);
                return;
            }
            int nalUnitType = NalUnitUtil.getNalUnitType(dataArray, nalUnitOffset);
            int lengthToNalUnit = nalUnitOffset - offset2;
            if (lengthToNalUnit > 0) {
                nalUnitData(dataArray, offset2, nalUnitOffset);
            }
            int bytesWrittenPastPosition = limit - nalUnitOffset;
            long absolutePosition = this.totalBytesWritten - bytesWrittenPastPosition;
            endNalUnit(absolutePosition, bytesWrittenPastPosition, lengthToNalUnit < 0 ? -lengthToNalUnit : 0, this.pesTimeUs);
            startNalUnit(absolutePosition, nalUnitType, this.pesTimeUs);
            offset2 = nalUnitOffset + 3;
        }
    }

    @Override // com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader
    public void packetFinished() {
    }

    private void startNalUnit(long position, int nalUnitType, long pesTimeUs) {
        if (!this.hasOutputFormat || this.sampleReader.needsSpsPps()) {
            this.sps.startNalUnit(nalUnitType);
            this.pps.startNalUnit(nalUnitType);
        }
        this.sei.startNalUnit(nalUnitType);
        this.sampleReader.startNalUnit(position, nalUnitType, pesTimeUs);
    }

    private void nalUnitData(byte[] dataArray, int offset, int limit) {
        if (!this.hasOutputFormat || this.sampleReader.needsSpsPps()) {
            this.sps.appendToNalUnit(dataArray, offset, limit);
            this.pps.appendToNalUnit(dataArray, offset, limit);
        }
        this.sei.appendToNalUnit(dataArray, offset, limit);
        this.sampleReader.appendToNalUnit(dataArray, offset, limit);
    }

    private void endNalUnit(long position, int offset, int discardPadding, long pesTimeUs) {
        if (!this.hasOutputFormat || this.sampleReader.needsSpsPps()) {
            this.sps.endNalUnit(discardPadding);
            this.pps.endNalUnit(discardPadding);
            if (!this.hasOutputFormat) {
                if (this.sps.isCompleted() && this.pps.isCompleted()) {
                    List<byte[]> initializationData = new ArrayList<>();
                    initializationData.add(Arrays.copyOf(this.sps.nalData, this.sps.nalLength));
                    initializationData.add(Arrays.copyOf(this.pps.nalData, this.pps.nalLength));
                    NalUnitUtil.SpsData spsData = NalUnitUtil.parseSpsNalUnit(this.sps.nalData, 3, this.sps.nalLength);
                    NalUnitUtil.PpsData ppsData = NalUnitUtil.parsePpsNalUnit(this.pps.nalData, 3, this.pps.nalLength);
                    this.output.format(Format.createVideoSampleFormat(this.formatId, "video/avc", CodecSpecificDataUtil.buildAvcCodecString(spsData.profileIdc, spsData.constraintsFlagsAndReservedZero2Bits, spsData.levelIdc), -1, -1, spsData.width, spsData.height, -1.0f, initializationData, -1, spsData.pixelWidthAspectRatio, null));
                    this.hasOutputFormat = true;
                    this.sampleReader.putSps(spsData);
                    this.sampleReader.putPps(ppsData);
                    this.sps.reset();
                    this.pps.reset();
                }
            } else if (this.sps.isCompleted()) {
                this.sampleReader.putSps(NalUnitUtil.parseSpsNalUnit(this.sps.nalData, 3, this.sps.nalLength));
                this.sps.reset();
            } else if (this.pps.isCompleted()) {
                NalUnitUtil.PpsData ppsData2 = NalUnitUtil.parsePpsNalUnit(this.pps.nalData, 3, this.pps.nalLength);
                this.sampleReader.putPps(ppsData2);
                this.pps.reset();
            }
        }
        if (this.sei.endNalUnit(discardPadding)) {
            int unescapedLength = NalUnitUtil.unescapeStream(this.sei.nalData, this.sei.nalLength);
            this.seiWrapper.reset(this.sei.nalData, unescapedLength);
            this.seiWrapper.setPosition(4);
            this.seiReader.consume(pesTimeUs, this.seiWrapper);
        }
        boolean sampleIsKeyFrame = this.sampleReader.endNalUnit(position, offset, this.hasOutputFormat, this.randomAccessIndicator);
        if (sampleIsKeyFrame) {
            this.randomAccessIndicator = false;
        }
    }

    /* loaded from: classes3.dex */
    public static final class SampleReader {
        private static final int DEFAULT_BUFFER_SIZE = 128;
        private static final int NAL_UNIT_TYPE_AUD = 9;
        private static final int NAL_UNIT_TYPE_IDR = 5;
        private static final int NAL_UNIT_TYPE_NON_IDR = 1;
        private static final int NAL_UNIT_TYPE_PARTITION_A = 2;
        private final boolean allowNonIdrKeyframes;
        private int bufferLength;
        private final boolean detectAccessUnits;
        private boolean isFilling;
        private long nalUnitStartPosition;
        private long nalUnitTimeUs;
        private int nalUnitType;
        private final TrackOutput output;
        private boolean readingSample;
        private boolean sampleIsKeyframe;
        private long samplePosition;
        private long sampleTimeUs;
        private final SparseArray<NalUnitUtil.SpsData> sps = new SparseArray<>();
        private final SparseArray<NalUnitUtil.PpsData> pps = new SparseArray<>();
        private SliceHeaderData previousSliceHeader = new SliceHeaderData();
        private SliceHeaderData sliceHeader = new SliceHeaderData();
        private byte[] buffer = new byte[128];
        private final ParsableNalUnitBitArray bitArray = new ParsableNalUnitBitArray(this.buffer, 0, 0);

        public SampleReader(TrackOutput output, boolean allowNonIdrKeyframes, boolean detectAccessUnits) {
            this.output = output;
            this.allowNonIdrKeyframes = allowNonIdrKeyframes;
            this.detectAccessUnits = detectAccessUnits;
            reset();
        }

        public boolean needsSpsPps() {
            return this.detectAccessUnits;
        }

        public void putSps(NalUnitUtil.SpsData spsData) {
            this.sps.append(spsData.seqParameterSetId, spsData);
        }

        public void putPps(NalUnitUtil.PpsData ppsData) {
            this.pps.append(ppsData.picParameterSetId, ppsData);
        }

        public void reset() {
            this.isFilling = false;
            this.readingSample = false;
            this.sliceHeader.clear();
        }

        public void startNalUnit(long position, int type, long pesTimeUs) {
            this.nalUnitType = type;
            this.nalUnitTimeUs = pesTimeUs;
            this.nalUnitStartPosition = position;
            if (!this.allowNonIdrKeyframes || type != 1) {
                if (!this.detectAccessUnits) {
                    return;
                }
                if (type != 5 && type != 1 && type != 2) {
                    return;
                }
            }
            SliceHeaderData newSliceHeader = this.previousSliceHeader;
            this.previousSliceHeader = this.sliceHeader;
            this.sliceHeader = newSliceHeader;
            newSliceHeader.clear();
            this.bufferLength = 0;
            this.isFilling = true;
        }

        public void appendToNalUnit(byte[] data, int offset, int limit) {
            boolean bottomFieldFlag;
            boolean bottomFieldFlagPresent;
            int idrPicId;
            int deltaPicOrderCnt1;
            int deltaPicOrderCnt0;
            int deltaPicOrderCntBottom;
            int picOrderCntLsb;
            if (!this.isFilling) {
                return;
            }
            int readLength = limit - offset;
            byte[] bArr = this.buffer;
            int length = bArr.length;
            int i = this.bufferLength;
            if (length < i + readLength) {
                this.buffer = Arrays.copyOf(bArr, (i + readLength) * 2);
            }
            System.arraycopy(data, offset, this.buffer, this.bufferLength, readLength);
            int i2 = this.bufferLength + readLength;
            this.bufferLength = i2;
            this.bitArray.reset(this.buffer, 0, i2);
            if (!this.bitArray.canReadBits(8)) {
                return;
            }
            this.bitArray.skipBit();
            int nalRefIdc = this.bitArray.readBits(2);
            this.bitArray.skipBits(5);
            if (!this.bitArray.canReadExpGolombCodedNum()) {
                return;
            }
            this.bitArray.readUnsignedExpGolombCodedInt();
            if (!this.bitArray.canReadExpGolombCodedNum()) {
                return;
            }
            int sliceType = this.bitArray.readUnsignedExpGolombCodedInt();
            if (!this.detectAccessUnits) {
                this.isFilling = false;
                this.sliceHeader.setSliceType(sliceType);
            } else if (!this.bitArray.canReadExpGolombCodedNum()) {
            } else {
                int picParameterSetId = this.bitArray.readUnsignedExpGolombCodedInt();
                if (this.pps.indexOfKey(picParameterSetId) < 0) {
                    this.isFilling = false;
                    return;
                }
                NalUnitUtil.PpsData ppsData = this.pps.get(picParameterSetId);
                NalUnitUtil.SpsData spsData = this.sps.get(ppsData.seqParameterSetId);
                if (spsData.separateColorPlaneFlag) {
                    if (!this.bitArray.canReadBits(2)) {
                        return;
                    }
                    this.bitArray.skipBits(2);
                }
                if (!this.bitArray.canReadBits(spsData.frameNumLength)) {
                    return;
                }
                boolean fieldPicFlag = false;
                int frameNum = this.bitArray.readBits(spsData.frameNumLength);
                if (spsData.frameMbsOnlyFlag) {
                    bottomFieldFlagPresent = false;
                    bottomFieldFlag = false;
                } else if (!this.bitArray.canReadBits(1)) {
                    return;
                } else {
                    fieldPicFlag = this.bitArray.readBit();
                    if (!fieldPicFlag) {
                        bottomFieldFlagPresent = false;
                        bottomFieldFlag = false;
                    } else if (!this.bitArray.canReadBits(1)) {
                        return;
                    } else {
                        boolean bottomFieldFlag2 = this.bitArray.readBit();
                        bottomFieldFlagPresent = true;
                        bottomFieldFlag = bottomFieldFlag2;
                    }
                }
                boolean idrPicFlag = this.nalUnitType == 5;
                if (!idrPicFlag) {
                    idrPicId = 0;
                } else if (!this.bitArray.canReadExpGolombCodedNum()) {
                    return;
                } else {
                    int idrPicId2 = this.bitArray.readUnsignedExpGolombCodedInt();
                    idrPicId = idrPicId2;
                }
                int picOrderCntLsb2 = 0;
                int deltaPicOrderCnt02 = 0;
                if (spsData.picOrderCountType == 0) {
                    if (!this.bitArray.canReadBits(spsData.picOrderCntLsbLength)) {
                        return;
                    }
                    picOrderCntLsb2 = this.bitArray.readBits(spsData.picOrderCntLsbLength);
                    if (ppsData.bottomFieldPicOrderInFramePresentFlag && !fieldPicFlag) {
                        if (!this.bitArray.canReadExpGolombCodedNum()) {
                            return;
                        }
                        int deltaPicOrderCntBottom2 = this.bitArray.readSignedExpGolombCodedInt();
                        picOrderCntLsb = picOrderCntLsb2;
                        deltaPicOrderCntBottom = deltaPicOrderCntBottom2;
                        deltaPicOrderCnt0 = 0;
                        deltaPicOrderCnt1 = 0;
                        this.sliceHeader.setAll(spsData, nalRefIdc, sliceType, frameNum, picParameterSetId, fieldPicFlag, bottomFieldFlagPresent, bottomFieldFlag, idrPicFlag, idrPicId, picOrderCntLsb, deltaPicOrderCntBottom, deltaPicOrderCnt0, deltaPicOrderCnt1);
                        this.isFilling = false;
                    }
                    picOrderCntLsb = picOrderCntLsb2;
                    deltaPicOrderCntBottom = 0;
                    deltaPicOrderCnt0 = deltaPicOrderCnt02;
                    deltaPicOrderCnt1 = 0;
                    this.sliceHeader.setAll(spsData, nalRefIdc, sliceType, frameNum, picParameterSetId, fieldPicFlag, bottomFieldFlagPresent, bottomFieldFlag, idrPicFlag, idrPicId, picOrderCntLsb, deltaPicOrderCntBottom, deltaPicOrderCnt0, deltaPicOrderCnt1);
                    this.isFilling = false;
                }
                if (spsData.picOrderCountType == 1 && !spsData.deltaPicOrderAlwaysZeroFlag) {
                    if (!this.bitArray.canReadExpGolombCodedNum()) {
                        return;
                    }
                    deltaPicOrderCnt02 = this.bitArray.readSignedExpGolombCodedInt();
                    if (ppsData.bottomFieldPicOrderInFramePresentFlag && !fieldPicFlag) {
                        if (!this.bitArray.canReadExpGolombCodedNum()) {
                            return;
                        }
                        int deltaPicOrderCnt12 = this.bitArray.readSignedExpGolombCodedInt();
                        picOrderCntLsb = 0;
                        deltaPicOrderCntBottom = 0;
                        deltaPicOrderCnt0 = deltaPicOrderCnt02;
                        deltaPicOrderCnt1 = deltaPicOrderCnt12;
                        this.sliceHeader.setAll(spsData, nalRefIdc, sliceType, frameNum, picParameterSetId, fieldPicFlag, bottomFieldFlagPresent, bottomFieldFlag, idrPicFlag, idrPicId, picOrderCntLsb, deltaPicOrderCntBottom, deltaPicOrderCnt0, deltaPicOrderCnt1);
                        this.isFilling = false;
                    }
                }
                picOrderCntLsb = picOrderCntLsb2;
                deltaPicOrderCntBottom = 0;
                deltaPicOrderCnt0 = deltaPicOrderCnt02;
                deltaPicOrderCnt1 = 0;
                this.sliceHeader.setAll(spsData, nalRefIdc, sliceType, frameNum, picParameterSetId, fieldPicFlag, bottomFieldFlagPresent, bottomFieldFlag, idrPicFlag, idrPicId, picOrderCntLsb, deltaPicOrderCntBottom, deltaPicOrderCnt0, deltaPicOrderCnt1);
                this.isFilling = false;
            }
        }

        public boolean endNalUnit(long position, int offset, boolean hasOutputFormat, boolean randomAccessIndicator) {
            boolean z = false;
            if (this.nalUnitType == 9 || (this.detectAccessUnits && this.sliceHeader.isFirstVclNalUnitOfPicture(this.previousSliceHeader))) {
                if (hasOutputFormat && this.readingSample) {
                    int nalUnitLength = (int) (position - this.nalUnitStartPosition);
                    outputSample(offset + nalUnitLength);
                }
                this.samplePosition = this.nalUnitStartPosition;
                this.sampleTimeUs = this.nalUnitTimeUs;
                this.sampleIsKeyframe = false;
                this.readingSample = true;
            }
            boolean treatIFrameAsKeyframe = this.allowNonIdrKeyframes ? this.sliceHeader.isISlice() : randomAccessIndicator;
            boolean z2 = this.sampleIsKeyframe;
            int i = this.nalUnitType;
            if (i == 5 || (treatIFrameAsKeyframe && i == 1)) {
                z = true;
            }
            boolean z3 = z | z2;
            this.sampleIsKeyframe = z3;
            return z3;
        }

        private void outputSample(int offset) {
            boolean z = this.sampleIsKeyframe;
            int size = (int) (this.nalUnitStartPosition - this.samplePosition);
            TrackOutput trackOutput = this.output;
            long j = this.sampleTimeUs;
            int flags = z ? 1 : 0;
            trackOutput.sampleMetadata(j, flags, size, offset, null);
        }

        /* loaded from: classes3.dex */
        public static final class SliceHeaderData {
            private static final int SLICE_TYPE_ALL_I = 7;
            private static final int SLICE_TYPE_I = 2;
            private boolean bottomFieldFlag;
            private boolean bottomFieldFlagPresent;
            private int deltaPicOrderCnt0;
            private int deltaPicOrderCnt1;
            private int deltaPicOrderCntBottom;
            private boolean fieldPicFlag;
            private int frameNum;
            private boolean hasSliceType;
            private boolean idrPicFlag;
            private int idrPicId;
            private boolean isComplete;
            private int nalRefIdc;
            private int picOrderCntLsb;
            private int picParameterSetId;
            private int sliceType;
            private NalUnitUtil.SpsData spsData;

            private SliceHeaderData() {
            }

            public void clear() {
                this.hasSliceType = false;
                this.isComplete = false;
            }

            public void setSliceType(int sliceType) {
                this.sliceType = sliceType;
                this.hasSliceType = true;
            }

            public void setAll(NalUnitUtil.SpsData spsData, int nalRefIdc, int sliceType, int frameNum, int picParameterSetId, boolean fieldPicFlag, boolean bottomFieldFlagPresent, boolean bottomFieldFlag, boolean idrPicFlag, int idrPicId, int picOrderCntLsb, int deltaPicOrderCntBottom, int deltaPicOrderCnt0, int deltaPicOrderCnt1) {
                this.spsData = spsData;
                this.nalRefIdc = nalRefIdc;
                this.sliceType = sliceType;
                this.frameNum = frameNum;
                this.picParameterSetId = picParameterSetId;
                this.fieldPicFlag = fieldPicFlag;
                this.bottomFieldFlagPresent = bottomFieldFlagPresent;
                this.bottomFieldFlag = bottomFieldFlag;
                this.idrPicFlag = idrPicFlag;
                this.idrPicId = idrPicId;
                this.picOrderCntLsb = picOrderCntLsb;
                this.deltaPicOrderCntBottom = deltaPicOrderCntBottom;
                this.deltaPicOrderCnt0 = deltaPicOrderCnt0;
                this.deltaPicOrderCnt1 = deltaPicOrderCnt1;
                this.isComplete = true;
                this.hasSliceType = true;
            }

            public boolean isISlice() {
                int i;
                return this.hasSliceType && ((i = this.sliceType) == 7 || i == 2);
            }

            public boolean isFirstVclNalUnitOfPicture(SliceHeaderData other) {
                boolean z;
                boolean z2;
                if (this.isComplete) {
                    if (!other.isComplete || this.frameNum != other.frameNum || this.picParameterSetId != other.picParameterSetId || this.fieldPicFlag != other.fieldPicFlag) {
                        return true;
                    }
                    if (this.bottomFieldFlagPresent && other.bottomFieldFlagPresent && this.bottomFieldFlag != other.bottomFieldFlag) {
                        return true;
                    }
                    int i = this.nalRefIdc;
                    int i2 = other.nalRefIdc;
                    if (i != i2 && (i == 0 || i2 == 0)) {
                        return true;
                    }
                    if (this.spsData.picOrderCountType == 0 && other.spsData.picOrderCountType == 0 && (this.picOrderCntLsb != other.picOrderCntLsb || this.deltaPicOrderCntBottom != other.deltaPicOrderCntBottom)) {
                        return true;
                    }
                    if ((this.spsData.picOrderCountType == 1 && other.spsData.picOrderCountType == 1 && (this.deltaPicOrderCnt0 != other.deltaPicOrderCnt0 || this.deltaPicOrderCnt1 != other.deltaPicOrderCnt1)) || (z = this.idrPicFlag) != (z2 = other.idrPicFlag)) {
                        return true;
                    }
                    if (z && z2 && this.idrPicId != other.idrPicId) {
                        return true;
                    }
                }
                return false;
            }
        }
    }
}
