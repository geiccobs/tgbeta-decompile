package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.tracks.AbstractH26XTrack;
import com.googlecode.mp4parser.h264.model.PictureParameterSet;
import com.googlecode.mp4parser.h264.model.SeqParameterSet;
import com.googlecode.mp4parser.h264.read.CAVLCReader;
import com.googlecode.mp4parser.util.RangeStartMap;
import com.mp4parser.iso14496.part15.AvcConfigurationBox;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
/* loaded from: classes3.dex */
public class H264TrackImpl extends AbstractH26XTrack {
    private static final Logger LOG = Logger.getLogger(H264TrackImpl.class.getName());
    PictureParameterSet currentPictureParameterSet;
    SeqParameterSet currentSeqParameterSet;
    private boolean determineFrameRate;
    PictureParameterSet firstPictureParameterSet;
    SeqParameterSet firstSeqParameterSet;
    int frameNrInGop;
    private int frametick;
    private int height;
    private String lang;
    RangeStartMap<Integer, byte[]> pictureParameterRangeMap;
    Map<Integer, PictureParameterSet> ppsIdToPps;
    Map<Integer, byte[]> ppsIdToPpsBytes;
    SampleDescriptionBox sampleDescriptionBox;
    private List<Sample> samples;
    private SEIMessage seiMessage;
    RangeStartMap<Integer, byte[]> seqParameterRangeMap;
    Map<Integer, SeqParameterSet> spsIdToSps;
    Map<Integer, byte[]> spsIdToSpsBytes;
    private long timescale;
    private int width;

    public H264TrackImpl(DataSource dataSource, String lang, long timescale, int frametick) throws IOException {
        super(dataSource);
        this.spsIdToSpsBytes = new HashMap();
        this.spsIdToSps = new HashMap();
        this.ppsIdToPpsBytes = new HashMap();
        this.ppsIdToPps = new HashMap();
        this.firstSeqParameterSet = null;
        this.firstPictureParameterSet = null;
        this.currentSeqParameterSet = null;
        this.currentPictureParameterSet = null;
        this.seqParameterRangeMap = new RangeStartMap<>();
        this.pictureParameterRangeMap = new RangeStartMap<>();
        this.frameNrInGop = 0;
        this.determineFrameRate = true;
        this.lang = "eng";
        this.lang = lang;
        this.timescale = timescale;
        this.frametick = frametick;
        if (timescale > 0 && frametick > 0) {
            this.determineFrameRate = false;
        }
        parse(new AbstractH26XTrack.LookAhead(dataSource));
    }

    public H264TrackImpl(DataSource dataSource, String lang) throws IOException {
        this(dataSource, lang, -1L, -1);
    }

    public H264TrackImpl(DataSource dataSource) throws IOException {
        this(dataSource, "eng");
    }

    private void parse(AbstractH26XTrack.LookAhead la) throws IOException {
        this.samples = new LinkedList();
        if (readSamples(la)) {
            if (!readVariables()) {
                throw new IOException();
            }
            this.sampleDescriptionBox = new SampleDescriptionBox();
            VisualSampleEntry visualSampleEntry = new VisualSampleEntry(VisualSampleEntry.TYPE3);
            visualSampleEntry.setDataReferenceIndex(1);
            visualSampleEntry.setDepth(24);
            visualSampleEntry.setFrameCount(1);
            visualSampleEntry.setHorizresolution(72.0d);
            visualSampleEntry.setVertresolution(72.0d);
            visualSampleEntry.setWidth(this.width);
            visualSampleEntry.setHeight(this.height);
            visualSampleEntry.setCompressorname("AVC Coding");
            AvcConfigurationBox avcConfigurationBox = new AvcConfigurationBox();
            avcConfigurationBox.setSequenceParameterSets(new ArrayList(this.spsIdToSpsBytes.values()));
            avcConfigurationBox.setPictureParameterSets(new ArrayList(this.ppsIdToPpsBytes.values()));
            avcConfigurationBox.setAvcLevelIndication(this.firstSeqParameterSet.level_idc);
            avcConfigurationBox.setAvcProfileIndication(this.firstSeqParameterSet.profile_idc);
            avcConfigurationBox.setBitDepthLumaMinus8(this.firstSeqParameterSet.bit_depth_luma_minus8);
            avcConfigurationBox.setBitDepthChromaMinus8(this.firstSeqParameterSet.bit_depth_chroma_minus8);
            avcConfigurationBox.setChromaFormat(this.firstSeqParameterSet.chroma_format_idc.getId());
            avcConfigurationBox.setConfigurationVersion(1);
            avcConfigurationBox.setLengthSizeMinusOne(3);
            int i = 0;
            int i2 = (this.firstSeqParameterSet.constraint_set_0_flag ? 128 : 0) + (this.firstSeqParameterSet.constraint_set_1_flag ? 64 : 0) + (this.firstSeqParameterSet.constraint_set_2_flag ? 32 : 0) + (this.firstSeqParameterSet.constraint_set_3_flag ? 16 : 0);
            if (this.firstSeqParameterSet.constraint_set_4_flag) {
                i = 8;
            }
            avcConfigurationBox.setProfileCompatibility(i2 + i + ((int) (this.firstSeqParameterSet.reserved_zero_2bits & 3)));
            visualSampleEntry.addBox(avcConfigurationBox);
            this.sampleDescriptionBox.addBox(visualSampleEntry);
            this.trackMetaData.setCreationTime(new Date());
            this.trackMetaData.setModificationTime(new Date());
            this.trackMetaData.setLanguage(this.lang);
            this.trackMetaData.setTimescale(this.timescale);
            this.trackMetaData.setWidth(this.width);
            this.trackMetaData.setHeight(this.height);
            return;
        }
        throw new IOException();
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public SampleDescriptionBox getSampleDescriptionBox() {
        return this.sampleDescriptionBox;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public String getHandler() {
        return "vide";
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public List<Sample> getSamples() {
        return this.samples;
    }

    private boolean readVariables() {
        this.width = (this.firstSeqParameterSet.pic_width_in_mbs_minus1 + 1) * 16;
        int mult = 2;
        if (this.firstSeqParameterSet.frame_mbs_only_flag) {
            mult = 1;
        }
        this.height = (this.firstSeqParameterSet.pic_height_in_map_units_minus1 + 1) * 16 * mult;
        if (this.firstSeqParameterSet.frame_cropping_flag) {
            int chromaArrayType = 0;
            if (!this.firstSeqParameterSet.residual_color_transform_flag) {
                chromaArrayType = this.firstSeqParameterSet.chroma_format_idc.getId();
            }
            int cropUnitX = 1;
            int cropUnitY = mult;
            if (chromaArrayType != 0) {
                cropUnitX = this.firstSeqParameterSet.chroma_format_idc.getSubWidth();
                cropUnitY = this.firstSeqParameterSet.chroma_format_idc.getSubHeight() * mult;
            }
            this.width -= (this.firstSeqParameterSet.frame_crop_left_offset + this.firstSeqParameterSet.frame_crop_right_offset) * cropUnitX;
            this.height -= (this.firstSeqParameterSet.frame_crop_top_offset + this.firstSeqParameterSet.frame_crop_bottom_offset) * cropUnitY;
        }
        return true;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r6v1, types: [com.googlecode.mp4parser.authoring.tracks.H264TrackImpl$1FirstVclNalDetector] */
    private boolean readSamples(AbstractH26XTrack.LookAhead la) throws IOException {
        List<ByteBuffer> buffered = new ArrayList<>();
        C1FirstVclNalDetector fvnd = 0;
        while (true) {
            ByteBuffer nal = findNextNal(la);
            if (nal != null) {
                int type = nal.get(0);
                int nal_ref_idc = (type >> 5) & 3;
                int nal_unit_type = type & 31;
                switch (nal_unit_type) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        ?? r6 = new Object(nal, nal_ref_idc, nal_unit_type) { // from class: com.googlecode.mp4parser.authoring.tracks.H264TrackImpl.1FirstVclNalDetector
                            boolean bottom_field_flag;
                            int delta_pic_order_cnt_0;
                            int delta_pic_order_cnt_1;
                            int delta_pic_order_cnt_bottom;
                            boolean field_pic_flag;
                            int frame_num;
                            boolean idrPicFlag;
                            int idr_pic_id;
                            int nal_ref_idc;
                            int pic_order_cnt_lsb;
                            int pic_order_cnt_type;
                            int pic_parameter_set_id;

                            {
                                H264TrackImpl.this = this;
                                InputStream bs = H264TrackImpl.cleanBuffer(new ByteBufferBackedInputStream(nal));
                                SliceHeader sh = new SliceHeader(bs, this.spsIdToSps, this.ppsIdToPps, nal_unit_type == 5);
                                this.frame_num = sh.frame_num;
                                this.pic_parameter_set_id = sh.pic_parameter_set_id;
                                this.field_pic_flag = sh.field_pic_flag;
                                this.bottom_field_flag = sh.bottom_field_flag;
                                this.nal_ref_idc = nal_ref_idc;
                                this.pic_order_cnt_type = this.spsIdToSps.get(Integer.valueOf(this.ppsIdToPps.get(Integer.valueOf(sh.pic_parameter_set_id)).seq_parameter_set_id)).pic_order_cnt_type;
                                this.delta_pic_order_cnt_bottom = sh.delta_pic_order_cnt_bottom;
                                this.pic_order_cnt_lsb = sh.pic_order_cnt_lsb;
                                this.delta_pic_order_cnt_0 = sh.delta_pic_order_cnt_0;
                                this.delta_pic_order_cnt_1 = sh.delta_pic_order_cnt_1;
                                this.idr_pic_id = sh.idr_pic_id;
                            }

                            boolean isFirstInNew(C1FirstVclNalDetector nu) {
                                boolean z;
                                boolean z2;
                                boolean z3;
                                if (nu.frame_num == this.frame_num && nu.pic_parameter_set_id == this.pic_parameter_set_id && (z = nu.field_pic_flag) == this.field_pic_flag) {
                                    if ((z && nu.bottom_field_flag != this.bottom_field_flag) || nu.nal_ref_idc != this.nal_ref_idc) {
                                        return true;
                                    }
                                    int i = nu.pic_order_cnt_type;
                                    if (i == 0 && this.pic_order_cnt_type == 0 && (nu.pic_order_cnt_lsb != this.pic_order_cnt_lsb || nu.delta_pic_order_cnt_bottom != this.delta_pic_order_cnt_bottom)) {
                                        return true;
                                    }
                                    if ((i == 1 && this.pic_order_cnt_type == 1 && (nu.delta_pic_order_cnt_0 != this.delta_pic_order_cnt_0 || nu.delta_pic_order_cnt_1 != this.delta_pic_order_cnt_1)) || (z2 = nu.idrPicFlag) != (z3 = this.idrPicFlag)) {
                                        return true;
                                    }
                                    return z2 && z3 && nu.idr_pic_id != this.idr_pic_id;
                                }
                                return true;
                            }
                        };
                        if (fvnd == 0) {
                            fvnd = r6;
                        } else {
                            boolean isFirstInNew = fvnd.isFirstInNew(r6);
                            fvnd = fvnd;
                            if (isFirstInNew) {
                                createSample(buffered);
                                fvnd = r6;
                            }
                        }
                        buffered.add((ByteBuffer) nal.rewind());
                        break;
                    case 6:
                        if (fvnd != 0) {
                            createSample(buffered);
                            fvnd = 0;
                        }
                        this.seiMessage = new SEIMessage(cleanBuffer(new ByteBufferBackedInputStream(nal)), this.currentSeqParameterSet);
                        buffered.add(nal);
                        break;
                    case 7:
                        if (fvnd != 0) {
                            createSample(buffered);
                            fvnd = 0;
                        }
                        handleSPS((ByteBuffer) nal.rewind());
                        break;
                    case 8:
                        if (fvnd != 0) {
                            createSample(buffered);
                            fvnd = 0;
                        }
                        handlePPS((ByteBuffer) nal.rewind());
                        break;
                    case 9:
                        if (fvnd != 0) {
                            createSample(buffered);
                            fvnd = 0;
                        }
                        buffered.add(nal);
                        break;
                    case 10:
                    case 11:
                        break;
                    case 12:
                    default:
                        PrintStream printStream = System.err;
                        printStream.println("Unknown NAL unit type: " + nal_unit_type);
                        break;
                    case 13:
                        throw new RuntimeException("Sequence parameter set extension is not yet handled. Needs TLC.");
                }
            }
        }
        createSample(buffered);
        this.decodingTimes = new long[this.samples.size()];
        Arrays.fill(this.decodingTimes, this.frametick);
        return true;
    }

    private void createSample(List<ByteBuffer> buffered) throws IOException {
        int stdpValue = 22;
        boolean IdrPicFlag = false;
        for (ByteBuffer nal : buffered) {
            int type = nal.get(0);
            int nal_unit_type = type & 31;
            if (nal_unit_type == 5) {
                IdrPicFlag = true;
            }
        }
        if (IdrPicFlag) {
            stdpValue = 22 + 16;
        }
        InputStream bs = cleanBuffer(new ByteBufferBackedInputStream(buffered.get(buffered.size() - 1)));
        SliceHeader sh = new SliceHeader(bs, this.spsIdToSps, this.ppsIdToPps, IdrPicFlag);
        if (sh.slice_type == SliceHeader.SliceType.B) {
            stdpValue += 4;
        }
        Sample bb = createSampleObject(buffered);
        buffered.clear();
        SEIMessage sEIMessage = this.seiMessage;
        if (sEIMessage == null || sEIMessage.n_frames == 0) {
            this.frameNrInGop = 0;
        }
        int offset = 0;
        SEIMessage sEIMessage2 = this.seiMessage;
        if (sEIMessage2 == null || !sEIMessage2.clock_timestamp_flag) {
            SEIMessage sEIMessage3 = this.seiMessage;
            if (sEIMessage3 != null && sEIMessage3.removal_delay_flag) {
                offset = this.seiMessage.dpb_removal_delay / 2;
            }
        } else {
            offset = this.seiMessage.n_frames - this.frameNrInGop;
        }
        this.ctts.add(new CompositionTimeToSample.Entry(1, this.frametick * offset));
        this.sdtp.add(new SampleDependencyTypeBox.Entry(stdpValue));
        this.frameNrInGop++;
        this.samples.add(bb);
        if (IdrPicFlag) {
            this.stss.add(Integer.valueOf(this.samples.size()));
        }
    }

    private void handlePPS(ByteBuffer data) throws IOException {
        InputStream is = new ByteBufferBackedInputStream(data);
        is.read();
        PictureParameterSet _pictureParameterSet = PictureParameterSet.read(is);
        if (this.firstPictureParameterSet == null) {
            this.firstPictureParameterSet = _pictureParameterSet;
        }
        this.currentPictureParameterSet = _pictureParameterSet;
        byte[] ppsBytes = toArray((ByteBuffer) data.rewind());
        byte[] oldPpsSameId = this.ppsIdToPpsBytes.get(Integer.valueOf(_pictureParameterSet.pic_parameter_set_id));
        if (oldPpsSameId != null && !Arrays.equals(oldPpsSameId, ppsBytes)) {
            throw new RuntimeException("OMG - I got two SPS with same ID but different settings! (AVC3 is the solution)");
        }
        if (oldPpsSameId == null) {
            this.pictureParameterRangeMap.put((RangeStartMap<Integer, byte[]>) Integer.valueOf(this.samples.size()), (Integer) ppsBytes);
        }
        this.ppsIdToPpsBytes.put(Integer.valueOf(_pictureParameterSet.pic_parameter_set_id), ppsBytes);
        this.ppsIdToPps.put(Integer.valueOf(_pictureParameterSet.pic_parameter_set_id), _pictureParameterSet);
    }

    private void handleSPS(ByteBuffer data) throws IOException {
        InputStream spsInputStream = cleanBuffer(new ByteBufferBackedInputStream(data));
        spsInputStream.read();
        SeqParameterSet _seqParameterSet = SeqParameterSet.read(spsInputStream);
        if (this.firstSeqParameterSet == null) {
            this.firstSeqParameterSet = _seqParameterSet;
            configureFramerate();
        }
        this.currentSeqParameterSet = _seqParameterSet;
        byte[] spsBytes = toArray((ByteBuffer) data.rewind());
        byte[] oldSpsSameId = this.spsIdToSpsBytes.get(Integer.valueOf(_seqParameterSet.seq_parameter_set_id));
        if (oldSpsSameId != null && !Arrays.equals(oldSpsSameId, spsBytes)) {
            throw new RuntimeException("OMG - I got two SPS with same ID but different settings!");
        }
        if (oldSpsSameId != null) {
            this.seqParameterRangeMap.put((RangeStartMap<Integer, byte[]>) Integer.valueOf(this.samples.size()), (Integer) spsBytes);
        }
        this.spsIdToSpsBytes.put(Integer.valueOf(_seqParameterSet.seq_parameter_set_id), spsBytes);
        this.spsIdToSps.put(Integer.valueOf(_seqParameterSet.seq_parameter_set_id), _seqParameterSet);
    }

    private void configureFramerate() {
        if (this.determineFrameRate) {
            if (this.firstSeqParameterSet.vuiParams != null) {
                this.timescale = this.firstSeqParameterSet.vuiParams.time_scale >> 1;
                int i = this.firstSeqParameterSet.vuiParams.num_units_in_tick;
                this.frametick = i;
                if (this.timescale == 0 || i == 0) {
                    PrintStream printStream = System.err;
                    printStream.println("Warning: vuiParams contain invalid values: time_scale: " + this.timescale + " and frame_tick: " + this.frametick + ". Setting frame rate to 25fps");
                    this.timescale = 90000L;
                    this.frametick = 3600;
                    return;
                }
                return;
            }
            System.err.println("Warning: Can't determine frame rate. Guessing 25 fps");
            this.timescale = 90000L;
            this.frametick = 3600;
        }
    }

    /* loaded from: classes3.dex */
    public static class SliceHeader {
        public boolean bottom_field_flag;
        public int colour_plane_id;
        public int delta_pic_order_cnt_0;
        public int delta_pic_order_cnt_1;
        public int delta_pic_order_cnt_bottom;
        public boolean field_pic_flag;
        public int first_mb_in_slice;
        public int frame_num;
        public int idr_pic_id;
        public int pic_order_cnt_lsb;
        public int pic_parameter_set_id;
        public SliceType slice_type;

        /* loaded from: classes3.dex */
        public enum SliceType {
            P,
            B,
            I,
            SP,
            SI
        }

        public SliceHeader(InputStream is, Map<Integer, SeqParameterSet> spss, Map<Integer, PictureParameterSet> ppss, boolean IdrPicFlag) {
            this.field_pic_flag = false;
            this.bottom_field_flag = false;
            try {
                is.read();
                CAVLCReader reader = new CAVLCReader(is);
                this.first_mb_in_slice = reader.readUE("SliceHeader: first_mb_in_slice");
                int sliceTypeInt = reader.readUE("SliceHeader: slice_type");
                switch (sliceTypeInt) {
                    case 0:
                    case 5:
                        this.slice_type = SliceType.P;
                        break;
                    case 1:
                    case 6:
                        this.slice_type = SliceType.B;
                        break;
                    case 2:
                    case 7:
                        this.slice_type = SliceType.I;
                        break;
                    case 3:
                    case 8:
                        this.slice_type = SliceType.SP;
                        break;
                    case 4:
                    case 9:
                        this.slice_type = SliceType.SI;
                        break;
                }
                int readUE = reader.readUE("SliceHeader: pic_parameter_set_id");
                this.pic_parameter_set_id = readUE;
                PictureParameterSet pps = ppss.get(Integer.valueOf(readUE));
                SeqParameterSet sps = spss.get(Integer.valueOf(pps.seq_parameter_set_id));
                if (sps.residual_color_transform_flag) {
                    this.colour_plane_id = reader.readU(2, "SliceHeader: colour_plane_id");
                }
                this.frame_num = reader.readU(sps.log2_max_frame_num_minus4 + 4, "SliceHeader: frame_num");
                if (!sps.frame_mbs_only_flag) {
                    boolean readBool = reader.readBool("SliceHeader: field_pic_flag");
                    this.field_pic_flag = readBool;
                    if (readBool) {
                        this.bottom_field_flag = reader.readBool("SliceHeader: bottom_field_flag");
                    }
                }
                if (IdrPicFlag) {
                    this.idr_pic_id = reader.readUE("SliceHeader: idr_pic_id");
                }
                if (sps.pic_order_cnt_type == 0) {
                    this.pic_order_cnt_lsb = reader.readU(sps.log2_max_pic_order_cnt_lsb_minus4 + 4, "SliceHeader: pic_order_cnt_lsb");
                    if (pps.bottom_field_pic_order_in_frame_present_flag && !this.field_pic_flag) {
                        this.delta_pic_order_cnt_bottom = reader.readSE("SliceHeader: delta_pic_order_cnt_bottom");
                    }
                }
                if (sps.pic_order_cnt_type == 1 && !sps.delta_pic_order_always_zero_flag) {
                    this.delta_pic_order_cnt_0 = reader.readSE("delta_pic_order_cnt_0");
                    if (pps.bottom_field_pic_order_in_frame_present_flag && !this.field_pic_flag) {
                        this.delta_pic_order_cnt_1 = reader.readSE("delta_pic_order_cnt_1");
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public String toString() {
            return "SliceHeader{first_mb_in_slice=" + this.first_mb_in_slice + ", slice_type=" + this.slice_type + ", pic_parameter_set_id=" + this.pic_parameter_set_id + ", colour_plane_id=" + this.colour_plane_id + ", frame_num=" + this.frame_num + ", field_pic_flag=" + this.field_pic_flag + ", bottom_field_flag=" + this.bottom_field_flag + ", idr_pic_id=" + this.idr_pic_id + ", pic_order_cnt_lsb=" + this.pic_order_cnt_lsb + ", delta_pic_order_cnt_bottom=" + this.delta_pic_order_cnt_bottom + '}';
        }
    }

    /* loaded from: classes3.dex */
    public class ByteBufferBackedInputStream extends InputStream {
        private final ByteBuffer buf;

        public ByteBufferBackedInputStream(ByteBuffer buf) {
            H264TrackImpl.this = r1;
            this.buf = buf.duplicate();
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            if (!this.buf.hasRemaining()) {
                return -1;
            }
            return this.buf.get() & 255;
        }

        @Override // java.io.InputStream
        public int read(byte[] bytes, int off, int len) throws IOException {
            if (!this.buf.hasRemaining()) {
                return -1;
            }
            int len2 = Math.min(len, this.buf.remaining());
            this.buf.get(bytes, off, len2);
            return len2;
        }
    }

    /* loaded from: classes3.dex */
    public class SEIMessage {
        boolean clock_timestamp_flag;
        int cnt_dropped_flag;
        int counting_type;
        int cpb_removal_delay;
        int ct_type;
        int discontinuity_flag;
        int dpb_removal_delay;
        int full_timestamp_flag;
        int hours_value;
        int minutes_value;
        int n_frames;
        int nuit_field_based_flag;
        int payloadSize;
        int payloadType;
        int pic_struct;
        boolean removal_delay_flag;
        int seconds_value;
        SeqParameterSet sps;
        int time_offset;
        int time_offset_length;

        /* JADX WARN: Multi-variable type inference failed */
        public SEIMessage(InputStream is, SeqParameterSet sps) throws IOException {
            int read;
            int numClockTS;
            int read2;
            H264TrackImpl.this = r18;
            int i = 0;
            this.payloadType = 0;
            this.payloadSize = 0;
            this.sps = sps;
            is.read();
            int datasize = is.available();
            int read3 = 0;
            while (read3 < datasize) {
                int i2 = i == 1 ? 1 : 0;
                int i3 = i == 1 ? 1 : 0;
                int i4 = i == 1 ? 1 : 0;
                int i5 = i == 1 ? 1 : 0;
                this.payloadType = i2;
                this.payloadSize = i;
                int last_payload_type_bytes = is.read();
                int read4 = read3 + 1;
                int i6 = i;
                while (last_payload_type_bytes == 255) {
                    this.payloadType += last_payload_type_bytes;
                    last_payload_type_bytes = is.read();
                    read4++;
                    i6 = 0;
                }
                this.payloadType += last_payload_type_bytes;
                int last_payload_size_bytes = is.read();
                read3 = read4 + 1;
                boolean z = i6;
                while (last_payload_size_bytes == 255) {
                    this.payloadSize += last_payload_size_bytes;
                    last_payload_size_bytes = is.read();
                    read3++;
                    z = 0;
                }
                int i7 = this.payloadSize + last_payload_size_bytes;
                this.payloadSize = i7;
                if (datasize - read3 < i7) {
                    read3 = datasize;
                } else if (this.payloadType != 1) {
                    for (int i8 = 0; i8 < this.payloadSize; i8++) {
                        is.read();
                        read3++;
                    }
                } else {
                    if (sps.vuiParams != null && (sps.vuiParams.nalHRDParams != null || sps.vuiParams.vclHRDParams != null || sps.vuiParams.pic_struct_present_flag)) {
                        byte[] data = new byte[this.payloadSize];
                        is.read(data);
                        int read5 = read3 + this.payloadSize;
                        CAVLCReader reader = new CAVLCReader(new ByteArrayInputStream(data));
                        if (sps.vuiParams.nalHRDParams != null || sps.vuiParams.vclHRDParams != null) {
                            this.removal_delay_flag = true;
                            this.cpb_removal_delay = reader.readU(sps.vuiParams.nalHRDParams.cpb_removal_delay_length_minus1 + 1, "SEI: cpb_removal_delay");
                            this.dpb_removal_delay = reader.readU(sps.vuiParams.nalHRDParams.dpb_output_delay_length_minus1 + 1, "SEI: dpb_removal_delay");
                        } else {
                            this.removal_delay_flag = z;
                        }
                        if (!sps.vuiParams.pic_struct_present_flag) {
                            read = read5;
                        } else {
                            int readU = reader.readU(4, "SEI: pic_struct");
                            this.pic_struct = readU;
                            switch (readU) {
                                case 3:
                                case 4:
                                case 7:
                                    numClockTS = 2;
                                    break;
                                case 5:
                                case 6:
                                case 8:
                                    numClockTS = 3;
                                    break;
                                default:
                                    numClockTS = 1;
                                    break;
                            }
                            int i9 = 0;
                            while (i9 < numClockTS) {
                                boolean readBool = reader.readBool("pic_timing SEI: clock_timestamp_flag[" + i9 + "]");
                                this.clock_timestamp_flag = readBool;
                                if (readBool) {
                                    this.ct_type = reader.readU(2, "pic_timing SEI: ct_type");
                                    this.nuit_field_based_flag = reader.readU(1, "pic_timing SEI: nuit_field_based_flag");
                                    this.counting_type = reader.readU(5, "pic_timing SEI: counting_type");
                                    this.full_timestamp_flag = reader.readU(1, "pic_timing SEI: full_timestamp_flag");
                                    this.discontinuity_flag = reader.readU(1, "pic_timing SEI: discontinuity_flag");
                                    this.cnt_dropped_flag = reader.readU(1, "pic_timing SEI: cnt_dropped_flag");
                                    this.n_frames = reader.readU(8, "pic_timing SEI: n_frames");
                                    read2 = read5;
                                    if (this.full_timestamp_flag == 1) {
                                        this.seconds_value = reader.readU(6, "pic_timing SEI: seconds_value");
                                        this.minutes_value = reader.readU(6, "pic_timing SEI: minutes_value");
                                        this.hours_value = reader.readU(5, "pic_timing SEI: hours_value");
                                    } else if (reader.readBool("pic_timing SEI: seconds_flag")) {
                                        this.seconds_value = reader.readU(6, "pic_timing SEI: seconds_value");
                                        if (reader.readBool("pic_timing SEI: minutes_flag")) {
                                            this.minutes_value = reader.readU(6, "pic_timing SEI: minutes_value");
                                            if (reader.readBool("pic_timing SEI: hours_flag")) {
                                                this.hours_value = reader.readU(5, "pic_timing SEI: hours_value");
                                            }
                                        }
                                    }
                                    if (sps.vuiParams.nalHRDParams != null) {
                                        this.time_offset_length = sps.vuiParams.nalHRDParams.time_offset_length;
                                    } else if (sps.vuiParams.vclHRDParams != null) {
                                        this.time_offset_length = sps.vuiParams.vclHRDParams.time_offset_length;
                                    } else {
                                        this.time_offset_length = 24;
                                    }
                                    this.time_offset = reader.readU(24, "pic_timing SEI: time_offset");
                                } else {
                                    read2 = read5;
                                }
                                i9++;
                                read5 = read2;
                            }
                            read = read5;
                        }
                        read3 = read;
                    }
                    for (int i10 = 0; i10 < this.payloadSize; i10++) {
                        is.read();
                        read3++;
                    }
                }
                H264TrackImpl.LOG.fine(toString());
                i = 0;
            }
        }

        public String toString() {
            String out = "SEIMessage{payloadType=" + this.payloadType + ", payloadSize=" + this.payloadSize;
            if (this.payloadType == 1) {
                if (this.sps.vuiParams.nalHRDParams != null || this.sps.vuiParams.vclHRDParams != null) {
                    out = String.valueOf(out) + ", cpb_removal_delay=" + this.cpb_removal_delay + ", dpb_removal_delay=" + this.dpb_removal_delay;
                }
                if (this.sps.vuiParams.pic_struct_present_flag) {
                    out = String.valueOf(out) + ", pic_struct=" + this.pic_struct;
                    if (this.clock_timestamp_flag) {
                        out = String.valueOf(out) + ", ct_type=" + this.ct_type + ", nuit_field_based_flag=" + this.nuit_field_based_flag + ", counting_type=" + this.counting_type + ", full_timestamp_flag=" + this.full_timestamp_flag + ", discontinuity_flag=" + this.discontinuity_flag + ", cnt_dropped_flag=" + this.cnt_dropped_flag + ", n_frames=" + this.n_frames + ", seconds_value=" + this.seconds_value + ", minutes_value=" + this.minutes_value + ", hours_value=" + this.hours_value + ", time_offset_length=" + this.time_offset_length + ", time_offset=" + this.time_offset;
                    }
                }
            }
            return String.valueOf(out) + '}';
        }
    }
}
