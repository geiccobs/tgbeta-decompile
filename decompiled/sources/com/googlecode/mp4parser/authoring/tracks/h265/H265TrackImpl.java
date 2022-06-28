package com.googlecode.mp4parser.authoring.tracks.h265;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.tracks.AbstractH26XTrack;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import com.googlecode.mp4parser.util.ByteBufferByteChannel;
import com.mp4parser.iso14496.part15.HevcConfigurationBox;
import com.mp4parser.iso14496.part15.HevcDecoderConfigurationRecord;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes3.dex */
public class H265TrackImpl extends AbstractH26XTrack implements NalUnitTypes {
    SampleDescriptionBox stsd;
    ArrayList<ByteBuffer> sps = new ArrayList<>();
    ArrayList<ByteBuffer> pps = new ArrayList<>();
    ArrayList<ByteBuffer> vps = new ArrayList<>();
    ArrayList<Sample> samples = new ArrayList<>();

    public H265TrackImpl(DataSource dataSource) throws IOException {
        super(dataSource);
        ArrayList<ByteBuffer> nals = new ArrayList<>();
        AbstractH26XTrack.LookAhead la = new AbstractH26XTrack.LookAhead(dataSource);
        boolean[] vclNalUnitSeenInAU = new boolean[1];
        boolean[] isIdr = {true};
        while (true) {
            ByteBuffer nal = findNextNal(la);
            if (nal != null) {
                NalUnitHeader unitHeader = getNalUnitHeader(nal);
                if (vclNalUnitSeenInAU[0]) {
                    if (isVcl(unitHeader)) {
                        if ((nal.get(2) & Byte.MIN_VALUE) != 0) {
                            wrapUp(nals, vclNalUnitSeenInAU, isIdr);
                        }
                    } else {
                        switch (unitHeader.nalUnitType) {
                            case 32:
                            case 33:
                            case 34:
                            case 35:
                            case 36:
                            case 37:
                            case 39:
                            case 41:
                            case 42:
                            case 43:
                            case 44:
                            case 48:
                            case 49:
                            case 50:
                            case 51:
                            case 52:
                            case 53:
                            case 54:
                            case 55:
                                wrapUp(nals, vclNalUnitSeenInAU, isIdr);
                                break;
                        }
                    }
                }
                switch (unitHeader.nalUnitType) {
                    case 32:
                        nal.position(2);
                        this.vps.add(nal.slice());
                        System.err.println("Stored VPS");
                        break;
                    case 33:
                        nal.position(2);
                        this.sps.add(nal.slice());
                        nal.position(1);
                        new SequenceParameterSetRbsp(Channels.newInputStream(new ByteBufferByteChannel(nal.slice())));
                        System.err.println("Stored SPS");
                        break;
                    case 34:
                        nal.position(2);
                        this.pps.add(nal.slice());
                        System.err.println("Stored PPS");
                        break;
                    case 39:
                        new SEIMessage(new BitReaderBuffer(nal.slice()));
                        break;
                }
                switch (unitHeader.nalUnitType) {
                    case 32:
                    case 33:
                    case 34:
                    case 35:
                    case 36:
                    case 37:
                    case 38:
                        break;
                    default:
                        PrintStream printStream = System.err;
                        printStream.println("Adding " + unitHeader.nalUnitType);
                        nals.add(nal);
                        break;
                }
                if (isVcl(unitHeader)) {
                    switch (unitHeader.nalUnitType) {
                        case 19:
                        case 20:
                            isIdr[0] = isIdr[0] & true;
                            continue;
                        default:
                            isIdr[0] = false;
                            continue;
                    }
                }
                vclNalUnitSeenInAU[0] = vclNalUnitSeenInAU[0] | isVcl(unitHeader);
            } else {
                this.stsd = createSampleDescriptionBox();
                this.decodingTimes = new long[this.samples.size()];
                getTrackMetaData().setTimescale(25L);
                Arrays.fill(this.decodingTimes, 1L);
                return;
            }
        }
    }

    private SampleDescriptionBox createSampleDescriptionBox() {
        this.stsd = new SampleDescriptionBox();
        VisualSampleEntry visualSampleEntry = new VisualSampleEntry(VisualSampleEntry.TYPE6);
        visualSampleEntry.setDataReferenceIndex(1);
        visualSampleEntry.setDepth(24);
        visualSampleEntry.setFrameCount(1);
        visualSampleEntry.setHorizresolution(72.0d);
        visualSampleEntry.setVertresolution(72.0d);
        visualSampleEntry.setWidth(640);
        visualSampleEntry.setHeight(480);
        visualSampleEntry.setCompressorname("HEVC Coding");
        HevcConfigurationBox hevcConfigurationBox = new HevcConfigurationBox();
        HevcDecoderConfigurationRecord.Array spsArray = new HevcDecoderConfigurationRecord.Array();
        spsArray.array_completeness = true;
        spsArray.nal_unit_type = 33;
        spsArray.nalUnits = new ArrayList();
        Iterator<ByteBuffer> it = this.sps.iterator();
        while (it.hasNext()) {
            ByteBuffer sp = it.next();
            spsArray.nalUnits.add(toArray(sp));
        }
        HevcDecoderConfigurationRecord.Array ppsArray = new HevcDecoderConfigurationRecord.Array();
        ppsArray.array_completeness = true;
        ppsArray.nal_unit_type = 34;
        ppsArray.nalUnits = new ArrayList();
        Iterator<ByteBuffer> it2 = this.pps.iterator();
        while (it2.hasNext()) {
            ByteBuffer pp = it2.next();
            ppsArray.nalUnits.add(toArray(pp));
        }
        HevcDecoderConfigurationRecord.Array vpsArray = new HevcDecoderConfigurationRecord.Array();
        vpsArray.array_completeness = true;
        vpsArray.nal_unit_type = 34;
        vpsArray.nalUnits = new ArrayList();
        Iterator<ByteBuffer> it3 = this.vps.iterator();
        while (it3.hasNext()) {
            ByteBuffer vp = it3.next();
            vpsArray.nalUnits.add(toArray(vp));
        }
        hevcConfigurationBox.getArrays().addAll(Arrays.asList(spsArray, vpsArray, ppsArray));
        visualSampleEntry.addBox(hevcConfigurationBox);
        this.stsd.addBox(visualSampleEntry);
        return this.stsd;
    }

    public void wrapUp(List<ByteBuffer> nals, boolean[] vclNalUnitSeenInAU, boolean[] isIdr) {
        this.samples.add(createSampleObject(nals));
        PrintStream printStream = System.err;
        printStream.print("Create AU from " + nals.size() + " NALs");
        if (isIdr[0]) {
            System.err.println("  IDR");
        } else {
            System.err.println();
        }
        vclNalUnitSeenInAU[0] = false;
        isIdr[0] = true;
        nals.clear();
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public SampleDescriptionBox getSampleDescriptionBox() {
        return null;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public String getHandler() {
        return "vide";
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public List<Sample> getSamples() {
        return this.samples;
    }

    boolean isVcl(NalUnitHeader nalUnitHeader) {
        return nalUnitHeader.nalUnitType >= 0 && nalUnitHeader.nalUnitType <= 31;
    }

    public NalUnitHeader getNalUnitHeader(ByteBuffer nal) {
        nal.position(0);
        int nal_unit_header = IsoTypeReader.readUInt16(nal);
        NalUnitHeader nalUnitHeader = new NalUnitHeader();
        nalUnitHeader.forbiddenZeroFlag = (32768 & nal_unit_header) >> 15;
        nalUnitHeader.nalUnitType = (nal_unit_header & 32256) >> 9;
        nalUnitHeader.nuhLayerId = (nal_unit_header & 504) >> 3;
        nalUnitHeader.nuhTemporalIdPlusOne = nal_unit_header & 7;
        return nalUnitHeader;
    }

    public static void main(String[] args) throws IOException {
        Track track = new H265TrackImpl(new FileDataSourceImpl("c:\\content\\test-UHD-HEVC_01_FMV_Med_track1.hvc"));
        Movie movie = new Movie();
        movie.addTrack(track);
        DefaultMp4Builder mp4Builder = new DefaultMp4Builder();
        Container c = mp4Builder.build(movie);
        c.writeContainer(new FileOutputStream("output.mp4").getChannel());
    }
}
