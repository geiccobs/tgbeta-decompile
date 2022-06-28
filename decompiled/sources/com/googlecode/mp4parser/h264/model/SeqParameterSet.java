package com.googlecode.mp4parser.h264.model;

import com.googlecode.mp4parser.h264.model.VUIParameters;
import com.googlecode.mp4parser.h264.read.CAVLCReader;
import com.googlecode.mp4parser.h264.write.CAVLCWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
/* loaded from: classes3.dex */
public class SeqParameterSet extends BitstreamElement {
    public int bit_depth_chroma_minus8;
    public int bit_depth_luma_minus8;
    public ChromaFormat chroma_format_idc;
    public boolean constraint_set_0_flag;
    public boolean constraint_set_1_flag;
    public boolean constraint_set_2_flag;
    public boolean constraint_set_3_flag;
    public boolean constraint_set_4_flag;
    public boolean constraint_set_5_flag;
    public boolean delta_pic_order_always_zero_flag;
    public boolean direct_8x8_inference_flag;
    public boolean entropy_coding_mode_flag;
    public boolean field_pic_flag;
    public int frame_crop_bottom_offset;
    public int frame_crop_left_offset;
    public int frame_crop_right_offset;
    public int frame_crop_top_offset;
    public boolean frame_cropping_flag;
    public boolean frame_mbs_only_flag;
    public boolean gaps_in_frame_num_value_allowed_flag;
    public int level_idc;
    public int log2_max_frame_num_minus4;
    public int log2_max_pic_order_cnt_lsb_minus4;
    public boolean mb_adaptive_frame_field_flag;
    public int num_ref_frames;
    public int num_ref_frames_in_pic_order_cnt_cycle;
    public int[] offsetForRefFrame;
    public int offset_for_non_ref_pic;
    public int offset_for_top_to_bottom_field;
    public int pic_height_in_map_units_minus1;
    public int pic_order_cnt_type;
    public int pic_width_in_mbs_minus1;
    public int profile_idc;
    public boolean qpprime_y_zero_transform_bypass_flag;
    public long reserved_zero_2bits;
    public boolean residual_color_transform_flag;
    public ScalingMatrix scalingMatrix;
    public int seq_parameter_set_id;
    public VUIParameters vuiParams;
    public int weighted_bipred_idc;
    public boolean weighted_pred_flag;

    public static SeqParameterSet read(InputStream is) throws IOException {
        CAVLCReader reader = new CAVLCReader(is);
        SeqParameterSet sps = new SeqParameterSet();
        sps.profile_idc = (int) reader.readNBit(8, "SPS: profile_idc");
        sps.constraint_set_0_flag = reader.readBool("SPS: constraint_set_0_flag");
        sps.constraint_set_1_flag = reader.readBool("SPS: constraint_set_1_flag");
        sps.constraint_set_2_flag = reader.readBool("SPS: constraint_set_2_flag");
        sps.constraint_set_3_flag = reader.readBool("SPS: constraint_set_3_flag");
        sps.constraint_set_4_flag = reader.readBool("SPS: constraint_set_4_flag");
        sps.constraint_set_5_flag = reader.readBool("SPS: constraint_set_5_flag");
        sps.reserved_zero_2bits = reader.readNBit(2, "SPS: reserved_zero_2bits");
        sps.level_idc = (int) reader.readNBit(8, "SPS: level_idc");
        sps.seq_parameter_set_id = reader.readUE("SPS: seq_parameter_set_id");
        int i = sps.profile_idc;
        if (i == 100 || i == 110 || i == 122 || i == 144) {
            ChromaFormat fromId = ChromaFormat.fromId(reader.readUE("SPS: chroma_format_idc"));
            sps.chroma_format_idc = fromId;
            if (fromId == ChromaFormat.YUV_444) {
                sps.residual_color_transform_flag = reader.readBool("SPS: residual_color_transform_flag");
            }
            sps.bit_depth_luma_minus8 = reader.readUE("SPS: bit_depth_luma_minus8");
            sps.bit_depth_chroma_minus8 = reader.readUE("SPS: bit_depth_chroma_minus8");
            sps.qpprime_y_zero_transform_bypass_flag = reader.readBool("SPS: qpprime_y_zero_transform_bypass_flag");
            boolean seqScalingMatrixPresent = reader.readBool("SPS: seq_scaling_matrix_present_lag");
            if (seqScalingMatrixPresent) {
                readScalingListMatrix(reader, sps);
            }
        } else {
            sps.chroma_format_idc = ChromaFormat.YUV_420;
        }
        sps.log2_max_frame_num_minus4 = reader.readUE("SPS: log2_max_frame_num_minus4");
        int readUE = reader.readUE("SPS: pic_order_cnt_type");
        sps.pic_order_cnt_type = readUE;
        if (readUE == 0) {
            sps.log2_max_pic_order_cnt_lsb_minus4 = reader.readUE("SPS: log2_max_pic_order_cnt_lsb_minus4");
        } else if (readUE == 1) {
            sps.delta_pic_order_always_zero_flag = reader.readBool("SPS: delta_pic_order_always_zero_flag");
            sps.offset_for_non_ref_pic = reader.readSE("SPS: offset_for_non_ref_pic");
            sps.offset_for_top_to_bottom_field = reader.readSE("SPS: offset_for_top_to_bottom_field");
            int readUE2 = reader.readUE("SPS: num_ref_frames_in_pic_order_cnt_cycle");
            sps.num_ref_frames_in_pic_order_cnt_cycle = readUE2;
            sps.offsetForRefFrame = new int[readUE2];
            for (int i2 = 0; i2 < sps.num_ref_frames_in_pic_order_cnt_cycle; i2++) {
                int[] iArr = sps.offsetForRefFrame;
                iArr[i2] = reader.readSE("SPS: offsetForRefFrame [" + i2 + "]");
            }
        }
        sps.num_ref_frames = reader.readUE("SPS: num_ref_frames");
        sps.gaps_in_frame_num_value_allowed_flag = reader.readBool("SPS: gaps_in_frame_num_value_allowed_flag");
        sps.pic_width_in_mbs_minus1 = reader.readUE("SPS: pic_width_in_mbs_minus1");
        sps.pic_height_in_map_units_minus1 = reader.readUE("SPS: pic_height_in_map_units_minus1");
        boolean readBool = reader.readBool("SPS: frame_mbs_only_flag");
        sps.frame_mbs_only_flag = readBool;
        if (!readBool) {
            sps.mb_adaptive_frame_field_flag = reader.readBool("SPS: mb_adaptive_frame_field_flag");
        }
        sps.direct_8x8_inference_flag = reader.readBool("SPS: direct_8x8_inference_flag");
        boolean readBool2 = reader.readBool("SPS: frame_cropping_flag");
        sps.frame_cropping_flag = readBool2;
        if (readBool2) {
            sps.frame_crop_left_offset = reader.readUE("SPS: frame_crop_left_offset");
            sps.frame_crop_right_offset = reader.readUE("SPS: frame_crop_right_offset");
            sps.frame_crop_top_offset = reader.readUE("SPS: frame_crop_top_offset");
            sps.frame_crop_bottom_offset = reader.readUE("SPS: frame_crop_bottom_offset");
        }
        boolean vui_parameters_present_flag = reader.readBool("SPS: vui_parameters_present_flag");
        if (vui_parameters_present_flag) {
            sps.vuiParams = ReadVUIParameters(reader);
        }
        reader.readTrailingBits();
        return sps;
    }

    private static void readScalingListMatrix(CAVLCReader reader, SeqParameterSet sps) throws IOException {
        sps.scalingMatrix = new ScalingMatrix();
        for (int i = 0; i < 8; i++) {
            boolean seqScalingListPresentFlag = reader.readBool("SPS: seqScalingListPresentFlag");
            if (seqScalingListPresentFlag) {
                sps.scalingMatrix.ScalingList4x4 = new ScalingList[8];
                sps.scalingMatrix.ScalingList8x8 = new ScalingList[8];
                if (i < 6) {
                    sps.scalingMatrix.ScalingList4x4[i] = ScalingList.read(reader, 16);
                } else {
                    sps.scalingMatrix.ScalingList8x8[i - 6] = ScalingList.read(reader, 64);
                }
            }
        }
    }

    private static VUIParameters ReadVUIParameters(CAVLCReader reader) throws IOException {
        VUIParameters vuip = new VUIParameters();
        vuip.aspect_ratio_info_present_flag = reader.readBool("VUI: aspect_ratio_info_present_flag");
        if (vuip.aspect_ratio_info_present_flag) {
            vuip.aspect_ratio = AspectRatio.fromValue((int) reader.readNBit(8, "VUI: aspect_ratio"));
            if (vuip.aspect_ratio == AspectRatio.Extended_SAR) {
                vuip.sar_width = (int) reader.readNBit(16, "VUI: sar_width");
                vuip.sar_height = (int) reader.readNBit(16, "VUI: sar_height");
            }
        }
        vuip.overscan_info_present_flag = reader.readBool("VUI: overscan_info_present_flag");
        if (vuip.overscan_info_present_flag) {
            vuip.overscan_appropriate_flag = reader.readBool("VUI: overscan_appropriate_flag");
        }
        vuip.video_signal_type_present_flag = reader.readBool("VUI: video_signal_type_present_flag");
        if (vuip.video_signal_type_present_flag) {
            vuip.video_format = (int) reader.readNBit(3, "VUI: video_format");
            vuip.video_full_range_flag = reader.readBool("VUI: video_full_range_flag");
            vuip.colour_description_present_flag = reader.readBool("VUI: colour_description_present_flag");
            if (vuip.colour_description_present_flag) {
                vuip.colour_primaries = (int) reader.readNBit(8, "VUI: colour_primaries");
                vuip.transfer_characteristics = (int) reader.readNBit(8, "VUI: transfer_characteristics");
                vuip.matrix_coefficients = (int) reader.readNBit(8, "VUI: matrix_coefficients");
            }
        }
        vuip.chroma_loc_info_present_flag = reader.readBool("VUI: chroma_loc_info_present_flag");
        if (vuip.chroma_loc_info_present_flag) {
            vuip.chroma_sample_loc_type_top_field = reader.readUE("VUI chroma_sample_loc_type_top_field");
            vuip.chroma_sample_loc_type_bottom_field = reader.readUE("VUI chroma_sample_loc_type_bottom_field");
        }
        vuip.timing_info_present_flag = reader.readBool("VUI: timing_info_present_flag");
        if (vuip.timing_info_present_flag) {
            vuip.num_units_in_tick = (int) reader.readNBit(32, "VUI: num_units_in_tick");
            vuip.time_scale = (int) reader.readNBit(32, "VUI: time_scale");
            vuip.fixed_frame_rate_flag = reader.readBool("VUI: fixed_frame_rate_flag");
        }
        boolean nal_hrd_parameters_present_flag = reader.readBool("VUI: nal_hrd_parameters_present_flag");
        if (nal_hrd_parameters_present_flag) {
            vuip.nalHRDParams = readHRDParameters(reader);
        }
        boolean vcl_hrd_parameters_present_flag = reader.readBool("VUI: vcl_hrd_parameters_present_flag");
        if (vcl_hrd_parameters_present_flag) {
            vuip.vclHRDParams = readHRDParameters(reader);
        }
        if (nal_hrd_parameters_present_flag || vcl_hrd_parameters_present_flag) {
            vuip.low_delay_hrd_flag = reader.readBool("VUI: low_delay_hrd_flag");
        }
        vuip.pic_struct_present_flag = reader.readBool("VUI: pic_struct_present_flag");
        boolean bitstream_restriction_flag = reader.readBool("VUI: bitstream_restriction_flag");
        if (bitstream_restriction_flag) {
            vuip.bitstreamRestriction = new VUIParameters.BitstreamRestriction();
            vuip.bitstreamRestriction.motion_vectors_over_pic_boundaries_flag = reader.readBool("VUI: motion_vectors_over_pic_boundaries_flag");
            vuip.bitstreamRestriction.max_bytes_per_pic_denom = reader.readUE("VUI max_bytes_per_pic_denom");
            vuip.bitstreamRestriction.max_bits_per_mb_denom = reader.readUE("VUI max_bits_per_mb_denom");
            vuip.bitstreamRestriction.log2_max_mv_length_horizontal = reader.readUE("VUI log2_max_mv_length_horizontal");
            vuip.bitstreamRestriction.log2_max_mv_length_vertical = reader.readUE("VUI log2_max_mv_length_vertical");
            vuip.bitstreamRestriction.num_reorder_frames = reader.readUE("VUI num_reorder_frames");
            vuip.bitstreamRestriction.max_dec_frame_buffering = reader.readUE("VUI max_dec_frame_buffering");
        }
        return vuip;
    }

    private static HRDParameters readHRDParameters(CAVLCReader reader) throws IOException {
        HRDParameters hrd = new HRDParameters();
        hrd.cpb_cnt_minus1 = reader.readUE("SPS: cpb_cnt_minus1");
        hrd.bit_rate_scale = (int) reader.readNBit(4, "HRD: bit_rate_scale");
        hrd.cpb_size_scale = (int) reader.readNBit(4, "HRD: cpb_size_scale");
        hrd.bit_rate_value_minus1 = new int[hrd.cpb_cnt_minus1 + 1];
        hrd.cpb_size_value_minus1 = new int[hrd.cpb_cnt_minus1 + 1];
        hrd.cbr_flag = new boolean[hrd.cpb_cnt_minus1 + 1];
        for (int SchedSelIdx = 0; SchedSelIdx <= hrd.cpb_cnt_minus1; SchedSelIdx++) {
            hrd.bit_rate_value_minus1[SchedSelIdx] = reader.readUE("HRD: bit_rate_value_minus1");
            hrd.cpb_size_value_minus1[SchedSelIdx] = reader.readUE("HRD: cpb_size_value_minus1");
            hrd.cbr_flag[SchedSelIdx] = reader.readBool("HRD: cbr_flag");
        }
        hrd.initial_cpb_removal_delay_length_minus1 = (int) reader.readNBit(5, "HRD: initial_cpb_removal_delay_length_minus1");
        hrd.cpb_removal_delay_length_minus1 = (int) reader.readNBit(5, "HRD: cpb_removal_delay_length_minus1");
        hrd.dpb_output_delay_length_minus1 = (int) reader.readNBit(5, "HRD: dpb_output_delay_length_minus1");
        hrd.time_offset_length = (int) reader.readNBit(5, "HRD: time_offset_length");
        return hrd;
    }

    @Override // com.googlecode.mp4parser.h264.model.BitstreamElement
    public void write(OutputStream out) throws IOException {
        CAVLCWriter writer = new CAVLCWriter(out);
        writer.writeNBit(this.profile_idc, 8, "SPS: profile_idc");
        writer.writeBool(this.constraint_set_0_flag, "SPS: constraint_set_0_flag");
        writer.writeBool(this.constraint_set_1_flag, "SPS: constraint_set_1_flag");
        writer.writeBool(this.constraint_set_2_flag, "SPS: constraint_set_2_flag");
        writer.writeBool(this.constraint_set_3_flag, "SPS: constraint_set_3_flag");
        writer.writeNBit(0L, 4, "SPS: reserved");
        writer.writeNBit(this.level_idc, 8, "SPS: level_idc");
        writer.writeUE(this.seq_parameter_set_id, "SPS: seq_parameter_set_id");
        int i = this.profile_idc;
        boolean z = false;
        if (i == 100 || i == 110 || i == 122 || i == 144) {
            writer.writeUE(this.chroma_format_idc.getId(), "SPS: chroma_format_idc");
            if (this.chroma_format_idc == ChromaFormat.YUV_444) {
                writer.writeBool(this.residual_color_transform_flag, "SPS: residual_color_transform_flag");
            }
            writer.writeUE(this.bit_depth_luma_minus8, "SPS: ");
            writer.writeUE(this.bit_depth_chroma_minus8, "SPS: ");
            writer.writeBool(this.qpprime_y_zero_transform_bypass_flag, "SPS: qpprime_y_zero_transform_bypass_flag");
            writer.writeBool(this.scalingMatrix != null, "SPS: ");
            if (this.scalingMatrix != null) {
                for (int i2 = 0; i2 < 8; i2++) {
                    if (i2 < 6) {
                        writer.writeBool(this.scalingMatrix.ScalingList4x4[i2] != null, "SPS: ");
                        if (this.scalingMatrix.ScalingList4x4[i2] != null) {
                            this.scalingMatrix.ScalingList4x4[i2].write(writer);
                        }
                    } else {
                        writer.writeBool(this.scalingMatrix.ScalingList8x8[i2 + (-6)] != null, "SPS: ");
                        if (this.scalingMatrix.ScalingList8x8[i2 - 6] != null) {
                            this.scalingMatrix.ScalingList8x8[i2 - 6].write(writer);
                        }
                    }
                }
            }
        }
        int i3 = this.log2_max_frame_num_minus4;
        writer.writeUE(i3, "SPS: log2_max_frame_num_minus4");
        writer.writeUE(this.pic_order_cnt_type, "SPS: pic_order_cnt_type");
        int i4 = this.pic_order_cnt_type;
        if (i4 == 0) {
            writer.writeUE(this.log2_max_pic_order_cnt_lsb_minus4, "SPS: log2_max_pic_order_cnt_lsb_minus4");
        } else if (i4 == 1) {
            writer.writeBool(this.delta_pic_order_always_zero_flag, "SPS: delta_pic_order_always_zero_flag");
            writer.writeSE(this.offset_for_non_ref_pic, "SPS: offset_for_non_ref_pic");
            writer.writeSE(this.offset_for_top_to_bottom_field, "SPS: offset_for_top_to_bottom_field");
            writer.writeUE(this.offsetForRefFrame.length, "SPS: ");
            int i5 = 0;
            while (true) {
                int[] iArr = this.offsetForRefFrame;
                if (i5 >= iArr.length) {
                    break;
                }
                writer.writeSE(iArr[i5], "SPS: ");
                i5++;
            }
        }
        writer.writeUE(this.num_ref_frames, "SPS: num_ref_frames");
        writer.writeBool(this.gaps_in_frame_num_value_allowed_flag, "SPS: gaps_in_frame_num_value_allowed_flag");
        writer.writeUE(this.pic_width_in_mbs_minus1, "SPS: pic_width_in_mbs_minus1");
        writer.writeUE(this.pic_height_in_map_units_minus1, "SPS: pic_height_in_map_units_minus1");
        writer.writeBool(this.frame_mbs_only_flag, "SPS: frame_mbs_only_flag");
        if (!this.frame_mbs_only_flag) {
            writer.writeBool(this.mb_adaptive_frame_field_flag, "SPS: mb_adaptive_frame_field_flag");
        }
        writer.writeBool(this.direct_8x8_inference_flag, "SPS: direct_8x8_inference_flag");
        writer.writeBool(this.frame_cropping_flag, "SPS: frame_cropping_flag");
        if (this.frame_cropping_flag) {
            writer.writeUE(this.frame_crop_left_offset, "SPS: frame_crop_left_offset");
            writer.writeUE(this.frame_crop_right_offset, "SPS: frame_crop_right_offset");
            writer.writeUE(this.frame_crop_top_offset, "SPS: frame_crop_top_offset");
            writer.writeUE(this.frame_crop_bottom_offset, "SPS: frame_crop_bottom_offset");
        }
        if (this.vuiParams != null) {
            z = true;
        }
        writer.writeBool(z, "SPS: ");
        VUIParameters vUIParameters = this.vuiParams;
        if (vUIParameters != null) {
            writeVUIParameters(vUIParameters, writer);
        }
        writer.writeTrailingBits();
    }

    private void writeVUIParameters(VUIParameters vuip, CAVLCWriter writer) throws IOException {
        writer.writeBool(vuip.aspect_ratio_info_present_flag, "VUI: aspect_ratio_info_present_flag");
        if (vuip.aspect_ratio_info_present_flag) {
            writer.writeNBit(vuip.aspect_ratio.getValue(), 8, "VUI: aspect_ratio");
            if (vuip.aspect_ratio == AspectRatio.Extended_SAR) {
                writer.writeNBit(vuip.sar_width, 16, "VUI: sar_width");
                writer.writeNBit(vuip.sar_height, 16, "VUI: sar_height");
            }
        }
        writer.writeBool(vuip.overscan_info_present_flag, "VUI: overscan_info_present_flag");
        if (vuip.overscan_info_present_flag) {
            writer.writeBool(vuip.overscan_appropriate_flag, "VUI: overscan_appropriate_flag");
        }
        writer.writeBool(vuip.video_signal_type_present_flag, "VUI: video_signal_type_present_flag");
        if (vuip.video_signal_type_present_flag) {
            writer.writeNBit(vuip.video_format, 3, "VUI: video_format");
            writer.writeBool(vuip.video_full_range_flag, "VUI: video_full_range_flag");
            writer.writeBool(vuip.colour_description_present_flag, "VUI: colour_description_present_flag");
            if (vuip.colour_description_present_flag) {
                writer.writeNBit(vuip.colour_primaries, 8, "VUI: colour_primaries");
                writer.writeNBit(vuip.transfer_characteristics, 8, "VUI: transfer_characteristics");
                writer.writeNBit(vuip.matrix_coefficients, 8, "VUI: matrix_coefficients");
            }
        }
        writer.writeBool(vuip.chroma_loc_info_present_flag, "VUI: chroma_loc_info_present_flag");
        if (vuip.chroma_loc_info_present_flag) {
            writer.writeUE(vuip.chroma_sample_loc_type_top_field, "VUI: chroma_sample_loc_type_top_field");
            writer.writeUE(vuip.chroma_sample_loc_type_bottom_field, "VUI: chroma_sample_loc_type_bottom_field");
        }
        writer.writeBool(vuip.timing_info_present_flag, "VUI: timing_info_present_flag");
        if (vuip.timing_info_present_flag) {
            writer.writeNBit(vuip.num_units_in_tick, 32, "VUI: num_units_in_tick");
            writer.writeNBit(vuip.time_scale, 32, "VUI: time_scale");
            writer.writeBool(vuip.fixed_frame_rate_flag, "VUI: fixed_frame_rate_flag");
        }
        boolean z = true;
        writer.writeBool(vuip.nalHRDParams != null, "VUI: ");
        if (vuip.nalHRDParams != null) {
            writeHRDParameters(vuip.nalHRDParams, writer);
        }
        writer.writeBool(vuip.vclHRDParams != null, "VUI: ");
        if (vuip.vclHRDParams != null) {
            writeHRDParameters(vuip.vclHRDParams, writer);
        }
        if (vuip.nalHRDParams != null || vuip.vclHRDParams != null) {
            writer.writeBool(vuip.low_delay_hrd_flag, "VUI: low_delay_hrd_flag");
        }
        writer.writeBool(vuip.pic_struct_present_flag, "VUI: pic_struct_present_flag");
        if (vuip.bitstreamRestriction == null) {
            z = false;
        }
        writer.writeBool(z, "VUI: ");
        if (vuip.bitstreamRestriction != null) {
            writer.writeBool(vuip.bitstreamRestriction.motion_vectors_over_pic_boundaries_flag, "VUI: motion_vectors_over_pic_boundaries_flag");
            writer.writeUE(vuip.bitstreamRestriction.max_bytes_per_pic_denom, "VUI: max_bytes_per_pic_denom");
            writer.writeUE(vuip.bitstreamRestriction.max_bits_per_mb_denom, "VUI: max_bits_per_mb_denom");
            writer.writeUE(vuip.bitstreamRestriction.log2_max_mv_length_horizontal, "VUI: log2_max_mv_length_horizontal");
            writer.writeUE(vuip.bitstreamRestriction.log2_max_mv_length_vertical, "VUI: log2_max_mv_length_vertical");
            writer.writeUE(vuip.bitstreamRestriction.num_reorder_frames, "VUI: num_reorder_frames");
            writer.writeUE(vuip.bitstreamRestriction.max_dec_frame_buffering, "VUI: max_dec_frame_buffering");
        }
    }

    private void writeHRDParameters(HRDParameters hrd, CAVLCWriter writer) throws IOException {
        writer.writeUE(hrd.cpb_cnt_minus1, "HRD: cpb_cnt_minus1");
        writer.writeNBit(hrd.bit_rate_scale, 4, "HRD: bit_rate_scale");
        writer.writeNBit(hrd.cpb_size_scale, 4, "HRD: cpb_size_scale");
        for (int SchedSelIdx = 0; SchedSelIdx <= hrd.cpb_cnt_minus1; SchedSelIdx++) {
            writer.writeUE(hrd.bit_rate_value_minus1[SchedSelIdx], "HRD: ");
            writer.writeUE(hrd.cpb_size_value_minus1[SchedSelIdx], "HRD: ");
            writer.writeBool(hrd.cbr_flag[SchedSelIdx], "HRD: ");
        }
        int SchedSelIdx2 = hrd.initial_cpb_removal_delay_length_minus1;
        writer.writeNBit(SchedSelIdx2, 5, "HRD: initial_cpb_removal_delay_length_minus1");
        writer.writeNBit(hrd.cpb_removal_delay_length_minus1, 5, "HRD: cpb_removal_delay_length_minus1");
        writer.writeNBit(hrd.dpb_output_delay_length_minus1, 5, "HRD: dpb_output_delay_length_minus1");
        writer.writeNBit(hrd.time_offset_length, 5, "HRD: time_offset_length");
    }

    public String toString() {
        return "SeqParameterSet{ \n        pic_order_cnt_type=" + this.pic_order_cnt_type + ", \n        field_pic_flag=" + this.field_pic_flag + ", \n        delta_pic_order_always_zero_flag=" + this.delta_pic_order_always_zero_flag + ", \n        weighted_pred_flag=" + this.weighted_pred_flag + ", \n        weighted_bipred_idc=" + this.weighted_bipred_idc + ", \n        entropy_coding_mode_flag=" + this.entropy_coding_mode_flag + ", \n        mb_adaptive_frame_field_flag=" + this.mb_adaptive_frame_field_flag + ", \n        direct_8x8_inference_flag=" + this.direct_8x8_inference_flag + ", \n        chroma_format_idc=" + this.chroma_format_idc + ", \n        log2_max_frame_num_minus4=" + this.log2_max_frame_num_minus4 + ", \n        log2_max_pic_order_cnt_lsb_minus4=" + this.log2_max_pic_order_cnt_lsb_minus4 + ", \n        pic_height_in_map_units_minus1=" + this.pic_height_in_map_units_minus1 + ", \n        pic_width_in_mbs_minus1=" + this.pic_width_in_mbs_minus1 + ", \n        bit_depth_luma_minus8=" + this.bit_depth_luma_minus8 + ", \n        bit_depth_chroma_minus8=" + this.bit_depth_chroma_minus8 + ", \n        qpprime_y_zero_transform_bypass_flag=" + this.qpprime_y_zero_transform_bypass_flag + ", \n        profile_idc=" + this.profile_idc + ", \n        constraint_set_0_flag=" + this.constraint_set_0_flag + ", \n        constraint_set_1_flag=" + this.constraint_set_1_flag + ", \n        constraint_set_2_flag=" + this.constraint_set_2_flag + ", \n        constraint_set_3_flag=" + this.constraint_set_3_flag + ", \n        constraint_set_4_flag=" + this.constraint_set_4_flag + ", \n        constraint_set_5_flag=" + this.constraint_set_5_flag + ", \n        level_idc=" + this.level_idc + ", \n        seq_parameter_set_id=" + this.seq_parameter_set_id + ", \n        residual_color_transform_flag=" + this.residual_color_transform_flag + ", \n        offset_for_non_ref_pic=" + this.offset_for_non_ref_pic + ", \n        offset_for_top_to_bottom_field=" + this.offset_for_top_to_bottom_field + ", \n        num_ref_frames=" + this.num_ref_frames + ", \n        gaps_in_frame_num_value_allowed_flag=" + this.gaps_in_frame_num_value_allowed_flag + ", \n        frame_mbs_only_flag=" + this.frame_mbs_only_flag + ", \n        frame_cropping_flag=" + this.frame_cropping_flag + ", \n        frame_crop_left_offset=" + this.frame_crop_left_offset + ", \n        frame_crop_right_offset=" + this.frame_crop_right_offset + ", \n        frame_crop_top_offset=" + this.frame_crop_top_offset + ", \n        frame_crop_bottom_offset=" + this.frame_crop_bottom_offset + ", \n        offsetForRefFrame=" + this.offsetForRefFrame + ", \n        vuiParams=" + this.vuiParams + ", \n        scalingMatrix=" + this.scalingMatrix + ", \n        num_ref_frames_in_pic_order_cnt_cycle=" + this.num_ref_frames_in_pic_order_cnt_cycle + '}';
    }
}
