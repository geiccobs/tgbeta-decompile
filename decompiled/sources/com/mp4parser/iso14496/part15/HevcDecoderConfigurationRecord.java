package com.mp4parser.iso14496.part15;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
/* loaded from: classes3.dex */
public class HevcDecoderConfigurationRecord {
    int avgFrameRate;
    int bitDepthChromaMinus8;
    int bitDepthLumaMinus8;
    int chromaFormat;
    int configurationVersion;
    int constantFrameRate;
    boolean frame_only_constraint_flag;
    long general_constraint_indicator_flags;
    int general_level_idc;
    long general_profile_compatibility_flags;
    int general_profile_idc;
    int general_profile_space;
    boolean general_tier_flag;
    boolean interlaced_source_flag;
    int lengthSizeMinusOne;
    int min_spatial_segmentation_idc;
    boolean non_packed_constraint_flag;
    int numTemporalLayers;
    int parallelismType;
    boolean progressive_source_flag;
    boolean temporalIdNested;
    int reserved1 = 15;
    int reserved2 = 63;
    int reserved3 = 63;
    int reserved4 = 31;
    int reserved5 = 31;
    List<Array> arrays = new ArrayList();

    public void parse(ByteBuffer content) {
        this.configurationVersion = IsoTypeReader.readUInt8(content);
        int a = IsoTypeReader.readUInt8(content);
        this.general_profile_space = (a & PsExtractor.AUDIO_STREAM) >> 6;
        this.general_tier_flag = (a & 32) > 0;
        this.general_profile_idc = a & 31;
        this.general_profile_compatibility_flags = IsoTypeReader.readUInt32(content);
        long readUInt48 = IsoTypeReader.readUInt48(content);
        this.general_constraint_indicator_flags = readUInt48;
        this.frame_only_constraint_flag = ((readUInt48 >> 44) & 8) > 0;
        this.non_packed_constraint_flag = ((readUInt48 >> 44) & 4) > 0;
        this.interlaced_source_flag = ((readUInt48 >> 44) & 2) > 0;
        this.progressive_source_flag = ((readUInt48 >> 44) & 1) > 0;
        this.general_constraint_indicator_flags = readUInt48 & 140737488355327L;
        this.general_level_idc = IsoTypeReader.readUInt8(content);
        int a2 = IsoTypeReader.readUInt16(content);
        this.reserved1 = (61440 & a2) >> 12;
        this.min_spatial_segmentation_idc = a2 & 4095;
        int a3 = IsoTypeReader.readUInt8(content);
        this.reserved2 = (a3 & 252) >> 2;
        this.parallelismType = a3 & 3;
        int a4 = IsoTypeReader.readUInt8(content);
        this.reserved3 = (a4 & 252) >> 2;
        this.chromaFormat = a4 & 3;
        int a5 = IsoTypeReader.readUInt8(content);
        this.reserved4 = (a5 & 248) >> 3;
        this.bitDepthLumaMinus8 = a5 & 7;
        int a6 = IsoTypeReader.readUInt8(content);
        this.reserved5 = (a6 & 248) >> 3;
        this.bitDepthChromaMinus8 = a6 & 7;
        this.avgFrameRate = IsoTypeReader.readUInt16(content);
        int a7 = IsoTypeReader.readUInt8(content);
        this.constantFrameRate = (a7 & PsExtractor.AUDIO_STREAM) >> 6;
        this.numTemporalLayers = (a7 & 56) >> 3;
        this.temporalIdNested = (a7 & 4) > 0;
        this.lengthSizeMinusOne = a7 & 3;
        int numOfArrays = IsoTypeReader.readUInt8(content);
        this.arrays = new ArrayList();
        for (int i = 0; i < numOfArrays; i++) {
            Array array = new Array();
            int a8 = IsoTypeReader.readUInt8(content);
            array.array_completeness = (a8 & 128) > 0;
            array.reserved = (a8 & 64) > 0;
            array.nal_unit_type = a8 & 63;
            int numNalus = IsoTypeReader.readUInt16(content);
            array.nalUnits = new ArrayList();
            for (int j = 0; j < numNalus; j++) {
                int nalUnitLength = IsoTypeReader.readUInt16(content);
                byte[] nal = new byte[nalUnitLength];
                content.get(nal);
                array.nalUnits.add(nal);
            }
            this.arrays.add(array);
        }
    }

    public void write(ByteBuffer byteBuffer) {
        IsoTypeWriter.writeUInt8(byteBuffer, this.configurationVersion);
        IsoTypeWriter.writeUInt8(byteBuffer, (this.general_profile_space << 6) + (this.general_tier_flag ? 32 : 0) + this.general_profile_idc);
        IsoTypeWriter.writeUInt32(byteBuffer, this.general_profile_compatibility_flags);
        long _general_constraint_indicator_flags = this.general_constraint_indicator_flags;
        if (this.frame_only_constraint_flag) {
            _general_constraint_indicator_flags |= 140737488355328L;
        }
        if (this.non_packed_constraint_flag) {
            _general_constraint_indicator_flags |= 70368744177664L;
        }
        if (this.interlaced_source_flag) {
            _general_constraint_indicator_flags |= 35184372088832L;
        }
        if (this.progressive_source_flag) {
            _general_constraint_indicator_flags |= 17592186044416L;
        }
        IsoTypeWriter.writeUInt48(byteBuffer, _general_constraint_indicator_flags);
        IsoTypeWriter.writeUInt8(byteBuffer, this.general_level_idc);
        IsoTypeWriter.writeUInt16(byteBuffer, (this.reserved1 << 12) + this.min_spatial_segmentation_idc);
        IsoTypeWriter.writeUInt8(byteBuffer, (this.reserved2 << 2) + this.parallelismType);
        IsoTypeWriter.writeUInt8(byteBuffer, (this.reserved3 << 2) + this.chromaFormat);
        IsoTypeWriter.writeUInt8(byteBuffer, (this.reserved4 << 3) + this.bitDepthLumaMinus8);
        IsoTypeWriter.writeUInt8(byteBuffer, (this.reserved5 << 3) + this.bitDepthChromaMinus8);
        IsoTypeWriter.writeUInt16(byteBuffer, this.avgFrameRate);
        IsoTypeWriter.writeUInt8(byteBuffer, (this.constantFrameRate << 6) + (this.numTemporalLayers << 3) + (this.temporalIdNested ? 4 : 0) + this.lengthSizeMinusOne);
        IsoTypeWriter.writeUInt8(byteBuffer, this.arrays.size());
        for (Array array : this.arrays) {
            IsoTypeWriter.writeUInt8(byteBuffer, (array.array_completeness ? 128 : 0) + (array.reserved ? 64 : 0) + array.nal_unit_type);
            IsoTypeWriter.writeUInt16(byteBuffer, array.nalUnits.size());
            for (byte[] nalUnit : array.nalUnits) {
                IsoTypeWriter.writeUInt16(byteBuffer, nalUnit.length);
                byteBuffer.put(nalUnit);
            }
        }
    }

    public int getSize() {
        int size = 23;
        for (Array array : this.arrays) {
            size += 3;
            for (byte[] nalUnit : array.nalUnits) {
                size = size + 2 + nalUnit.length;
            }
        }
        return size;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HevcDecoderConfigurationRecord that = (HevcDecoderConfigurationRecord) o;
        if (this.avgFrameRate != that.avgFrameRate || this.bitDepthChromaMinus8 != that.bitDepthChromaMinus8 || this.bitDepthLumaMinus8 != that.bitDepthLumaMinus8 || this.chromaFormat != that.chromaFormat || this.configurationVersion != that.configurationVersion || this.constantFrameRate != that.constantFrameRate || this.general_constraint_indicator_flags != that.general_constraint_indicator_flags || this.general_level_idc != that.general_level_idc || this.general_profile_compatibility_flags != that.general_profile_compatibility_flags || this.general_profile_idc != that.general_profile_idc || this.general_profile_space != that.general_profile_space || this.general_tier_flag != that.general_tier_flag || this.lengthSizeMinusOne != that.lengthSizeMinusOne || this.min_spatial_segmentation_idc != that.min_spatial_segmentation_idc || this.numTemporalLayers != that.numTemporalLayers || this.parallelismType != that.parallelismType || this.reserved1 != that.reserved1 || this.reserved2 != that.reserved2 || this.reserved3 != that.reserved3 || this.reserved4 != that.reserved4 || this.reserved5 != that.reserved5 || this.temporalIdNested != that.temporalIdNested) {
            return false;
        }
        List<Array> list = this.arrays;
        if (list == null ? that.arrays == null : list.equals(that.arrays)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = this.configurationVersion;
        int result2 = (result * 31) + this.general_profile_space;
        long j = this.general_profile_compatibility_flags;
        long j2 = this.general_constraint_indicator_flags;
        int result3 = ((((((((((((((((((((((((((((((((((((((((result2 * 31) + (this.general_tier_flag ? 1 : 0)) * 31) + this.general_profile_idc) * 31) + ((int) (j ^ (j >>> 32)))) * 31) + ((int) (j2 ^ (j2 >>> 32)))) * 31) + this.general_level_idc) * 31) + this.reserved1) * 31) + this.min_spatial_segmentation_idc) * 31) + this.reserved2) * 31) + this.parallelismType) * 31) + this.reserved3) * 31) + this.chromaFormat) * 31) + this.reserved4) * 31) + this.bitDepthLumaMinus8) * 31) + this.reserved5) * 31) + this.bitDepthChromaMinus8) * 31) + this.avgFrameRate) * 31) + this.constantFrameRate) * 31) + this.numTemporalLayers) * 31) + (this.temporalIdNested ? 1 : 0)) * 31) + this.lengthSizeMinusOne) * 31;
        List<Array> list = this.arrays;
        return result3 + (list != null ? list.hashCode() : 0);
    }

    public String toString() {
        String str;
        String str2;
        String str3;
        String str4;
        StringBuilder sb = new StringBuilder("HEVCDecoderConfigurationRecord{configurationVersion=");
        sb.append(this.configurationVersion);
        sb.append(", general_profile_space=");
        sb.append(this.general_profile_space);
        sb.append(", general_tier_flag=");
        sb.append(this.general_tier_flag);
        sb.append(", general_profile_idc=");
        sb.append(this.general_profile_idc);
        sb.append(", general_profile_compatibility_flags=");
        sb.append(this.general_profile_compatibility_flags);
        sb.append(", general_constraint_indicator_flags=");
        sb.append(this.general_constraint_indicator_flags);
        sb.append(", general_level_idc=");
        sb.append(this.general_level_idc);
        String str5 = "";
        if (this.reserved1 != 15) {
            str = ", reserved1=" + this.reserved1;
        } else {
            str = str5;
        }
        sb.append(str);
        sb.append(", min_spatial_segmentation_idc=");
        sb.append(this.min_spatial_segmentation_idc);
        if (this.reserved2 != 63) {
            str2 = ", reserved2=" + this.reserved2;
        } else {
            str2 = str5;
        }
        sb.append(str2);
        sb.append(", parallelismType=");
        sb.append(this.parallelismType);
        if (this.reserved3 != 63) {
            str3 = ", reserved3=" + this.reserved3;
        } else {
            str3 = str5;
        }
        sb.append(str3);
        sb.append(", chromaFormat=");
        sb.append(this.chromaFormat);
        if (this.reserved4 != 31) {
            str4 = ", reserved4=" + this.reserved4;
        } else {
            str4 = str5;
        }
        sb.append(str4);
        sb.append(", bitDepthLumaMinus8=");
        sb.append(this.bitDepthLumaMinus8);
        if (this.reserved5 != 31) {
            str5 = ", reserved5=" + this.reserved5;
        }
        sb.append(str5);
        sb.append(", bitDepthChromaMinus8=");
        sb.append(this.bitDepthChromaMinus8);
        sb.append(", avgFrameRate=");
        sb.append(this.avgFrameRate);
        sb.append(", constantFrameRate=");
        sb.append(this.constantFrameRate);
        sb.append(", numTemporalLayers=");
        sb.append(this.numTemporalLayers);
        sb.append(", temporalIdNested=");
        sb.append(this.temporalIdNested);
        sb.append(", lengthSizeMinusOne=");
        sb.append(this.lengthSizeMinusOne);
        sb.append(", arrays=");
        sb.append(this.arrays);
        sb.append('}');
        return sb.toString();
    }

    public int getConfigurationVersion() {
        return this.configurationVersion;
    }

    public void setConfigurationVersion(int configurationVersion) {
        this.configurationVersion = configurationVersion;
    }

    public int getGeneral_profile_space() {
        return this.general_profile_space;
    }

    public void setGeneral_profile_space(int general_profile_space) {
        this.general_profile_space = general_profile_space;
    }

    public boolean isGeneral_tier_flag() {
        return this.general_tier_flag;
    }

    public void setGeneral_tier_flag(boolean general_tier_flag) {
        this.general_tier_flag = general_tier_flag;
    }

    public int getGeneral_profile_idc() {
        return this.general_profile_idc;
    }

    public void setGeneral_profile_idc(int general_profile_idc) {
        this.general_profile_idc = general_profile_idc;
    }

    public long getGeneral_profile_compatibility_flags() {
        return this.general_profile_compatibility_flags;
    }

    public void setGeneral_profile_compatibility_flags(long general_profile_compatibility_flags) {
        this.general_profile_compatibility_flags = general_profile_compatibility_flags;
    }

    public long getGeneral_constraint_indicator_flags() {
        return this.general_constraint_indicator_flags;
    }

    public void setGeneral_constraint_indicator_flags(long general_constraint_indicator_flags) {
        this.general_constraint_indicator_flags = general_constraint_indicator_flags;
    }

    public int getGeneral_level_idc() {
        return this.general_level_idc;
    }

    public void setGeneral_level_idc(int general_level_idc) {
        this.general_level_idc = general_level_idc;
    }

    public int getMin_spatial_segmentation_idc() {
        return this.min_spatial_segmentation_idc;
    }

    public void setMin_spatial_segmentation_idc(int min_spatial_segmentation_idc) {
        this.min_spatial_segmentation_idc = min_spatial_segmentation_idc;
    }

    public int getParallelismType() {
        return this.parallelismType;
    }

    public void setParallelismType(int parallelismType) {
        this.parallelismType = parallelismType;
    }

    public int getChromaFormat() {
        return this.chromaFormat;
    }

    public void setChromaFormat(int chromaFormat) {
        this.chromaFormat = chromaFormat;
    }

    public int getBitDepthLumaMinus8() {
        return this.bitDepthLumaMinus8;
    }

    public void setBitDepthLumaMinus8(int bitDepthLumaMinus8) {
        this.bitDepthLumaMinus8 = bitDepthLumaMinus8;
    }

    public int getBitDepthChromaMinus8() {
        return this.bitDepthChromaMinus8;
    }

    public void setBitDepthChromaMinus8(int bitDepthChromaMinus8) {
        this.bitDepthChromaMinus8 = bitDepthChromaMinus8;
    }

    public int getAvgFrameRate() {
        return this.avgFrameRate;
    }

    public void setAvgFrameRate(int avgFrameRate) {
        this.avgFrameRate = avgFrameRate;
    }

    public int getNumTemporalLayers() {
        return this.numTemporalLayers;
    }

    public void setNumTemporalLayers(int numTemporalLayers) {
        this.numTemporalLayers = numTemporalLayers;
    }

    public int getLengthSizeMinusOne() {
        return this.lengthSizeMinusOne;
    }

    public void setLengthSizeMinusOne(int lengthSizeMinusOne) {
        this.lengthSizeMinusOne = lengthSizeMinusOne;
    }

    public boolean isTemporalIdNested() {
        return this.temporalIdNested;
    }

    public void setTemporalIdNested(boolean temporalIdNested) {
        this.temporalIdNested = temporalIdNested;
    }

    public int getConstantFrameRate() {
        return this.constantFrameRate;
    }

    public void setConstantFrameRate(int constantFrameRate) {
        this.constantFrameRate = constantFrameRate;
    }

    public List<Array> getArrays() {
        return this.arrays;
    }

    public void setArrays(List<Array> arrays) {
        this.arrays = arrays;
    }

    public boolean isFrame_only_constraint_flag() {
        return this.frame_only_constraint_flag;
    }

    public void setFrame_only_constraint_flag(boolean frame_only_constraint_flag) {
        this.frame_only_constraint_flag = frame_only_constraint_flag;
    }

    public boolean isNon_packed_constraint_flag() {
        return this.non_packed_constraint_flag;
    }

    public void setNon_packed_constraint_flag(boolean non_packed_constraint_flag) {
        this.non_packed_constraint_flag = non_packed_constraint_flag;
    }

    public boolean isInterlaced_source_flag() {
        return this.interlaced_source_flag;
    }

    public void setInterlaced_source_flag(boolean interlaced_source_flag) {
        this.interlaced_source_flag = interlaced_source_flag;
    }

    public boolean isProgressive_source_flag() {
        return this.progressive_source_flag;
    }

    public void setProgressive_source_flag(boolean progressive_source_flag) {
        this.progressive_source_flag = progressive_source_flag;
    }

    /* loaded from: classes3.dex */
    public static class Array {
        public boolean array_completeness;
        public List<byte[]> nalUnits;
        public int nal_unit_type;
        public boolean reserved;

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Array array = (Array) o;
            if (this.array_completeness != array.array_completeness || this.nal_unit_type != array.nal_unit_type || this.reserved != array.reserved) {
                return false;
            }
            ListIterator<byte[]> e1 = this.nalUnits.listIterator();
            ListIterator<byte[]> e2 = array.nalUnits.listIterator();
            while (e1.hasNext() && e2.hasNext()) {
                byte[] o1 = e1.next();
                byte[] o2 = e2.next();
                if (o1 == null) {
                    if (o2 != null) {
                        return false;
                    }
                } else if (!Arrays.equals(o1, o2)) {
                    return false;
                }
            }
            return !e1.hasNext() && !e2.hasNext();
        }

        public int hashCode() {
            int result = this.array_completeness ? 1 : 0;
            int result2 = ((((result * 31) + (this.reserved ? 1 : 0)) * 31) + this.nal_unit_type) * 31;
            List<byte[]> list = this.nalUnits;
            return result2 + (list != null ? list.hashCode() : 0);
        }

        public String toString() {
            return "Array{nal_unit_type=" + this.nal_unit_type + ", reserved=" + this.reserved + ", array_completeness=" + this.array_completeness + ", num_nals=" + this.nalUnits.size() + '}';
        }
    }
}
