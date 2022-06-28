package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.coremedia.iso.boxes.sampleentry.SampleEntry;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.boxes.mp4.AbstractDescriptorBox;
import com.googlecode.mp4parser.boxes.mp4.ESDescriptorBox;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BaseDescriptor;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.DecoderConfigDescriptor;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ESDescriptor;
import com.googlecode.mp4parser.util.Logger;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
/* loaded from: classes3.dex */
public class AppendTrack extends AbstractTrack {
    private static Logger LOG = Logger.getLogger(AppendTrack.class);
    SampleDescriptionBox stsd;
    Track[] tracks;

    public static String appendTracknames(Track... tracks) {
        String name = "";
        for (Track track : tracks) {
            name = String.valueOf(name) + track.getName() + " + ";
        }
        return name.substring(0, name.length() - 3);
    }

    public AppendTrack(Track... tracks) throws IOException {
        super(appendTracknames(tracks));
        this.tracks = tracks;
        for (Track track : tracks) {
            SampleDescriptionBox sampleDescriptionBox = this.stsd;
            if (sampleDescriptionBox == null) {
                SampleDescriptionBox sampleDescriptionBox2 = new SampleDescriptionBox();
                this.stsd = sampleDescriptionBox2;
                sampleDescriptionBox2.addBox((Box) track.getSampleDescriptionBox().getBoxes(SampleEntry.class).get(0));
            } else {
                this.stsd = mergeStsds(sampleDescriptionBox, track.getSampleDescriptionBox());
            }
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        Track[] trackArr;
        for (Track track : this.tracks) {
            track.close();
        }
    }

    private SampleDescriptionBox mergeStsds(SampleDescriptionBox stsd1, SampleDescriptionBox stsd2) throws IOException {
        ByteArrayOutputStream curBaos = new ByteArrayOutputStream();
        ByteArrayOutputStream refBaos = new ByteArrayOutputStream();
        try {
            stsd1.getBox(Channels.newChannel(curBaos));
            stsd2.getBox(Channels.newChannel(refBaos));
            byte[] cur = curBaos.toByteArray();
            byte[] ref = refBaos.toByteArray();
            if (!Arrays.equals(ref, cur)) {
                SampleEntry se = mergeSampleEntry((SampleEntry) stsd1.getBoxes(SampleEntry.class).get(0), (SampleEntry) stsd2.getBoxes(SampleEntry.class).get(0));
                if (se != null) {
                    stsd1.setBoxes(Collections.singletonList(se));
                } else {
                    throw new IOException("Cannot merge " + stsd1.getBoxes(SampleEntry.class).get(0) + " and " + stsd2.getBoxes(SampleEntry.class).get(0));
                }
            }
            return stsd1;
        } catch (IOException e) {
            LOG.logError(e.getMessage());
            return null;
        }
    }

    private SampleEntry mergeSampleEntry(SampleEntry se1, SampleEntry se2) {
        if (!se1.getType().equals(se2.getType())) {
            return null;
        }
        if ((se1 instanceof VisualSampleEntry) && (se2 instanceof VisualSampleEntry)) {
            return mergeVisualSampleEntry((VisualSampleEntry) se1, (VisualSampleEntry) se2);
        }
        if ((se1 instanceof AudioSampleEntry) && (se2 instanceof AudioSampleEntry)) {
            return mergeAudioSampleEntries((AudioSampleEntry) se1, (AudioSampleEntry) se2);
        }
        return null;
    }

    private VisualSampleEntry mergeVisualSampleEntry(VisualSampleEntry vse1, VisualSampleEntry vse2) {
        VisualSampleEntry vse = new VisualSampleEntry();
        if (vse1.getHorizresolution() == vse2.getHorizresolution()) {
            vse.setHorizresolution(vse1.getHorizresolution());
            vse.setCompressorname(vse1.getCompressorname());
            if (vse1.getDepth() == vse2.getDepth()) {
                vse.setDepth(vse1.getDepth());
                if (vse1.getFrameCount() == vse2.getFrameCount()) {
                    vse.setFrameCount(vse1.getFrameCount());
                    if (vse1.getHeight() == vse2.getHeight()) {
                        vse.setHeight(vse1.getHeight());
                        if (vse1.getWidth() == vse2.getWidth()) {
                            vse.setWidth(vse1.getWidth());
                            if (vse1.getVertresolution() == vse2.getVertresolution()) {
                                vse.setVertresolution(vse1.getVertresolution());
                                if (vse1.getHorizresolution() == vse2.getHorizresolution()) {
                                    vse.setHorizresolution(vse1.getHorizresolution());
                                    if (vse1.getBoxes().size() == vse2.getBoxes().size()) {
                                        Iterator<Box> bxs2 = vse2.getBoxes().iterator();
                                        for (Box cur1 : vse1.getBoxes()) {
                                            Box cur2 = bxs2.next();
                                            ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
                                            ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                                            try {
                                                cur1.getBox(Channels.newChannel(baos1));
                                                cur2.getBox(Channels.newChannel(baos2));
                                                if (Arrays.equals(baos1.toByteArray(), baos2.toByteArray())) {
                                                    vse.addBox(cur1);
                                                } else if ((cur1 instanceof AbstractDescriptorBox) && (cur2 instanceof AbstractDescriptorBox)) {
                                                    BaseDescriptor esd = mergeDescriptors(((AbstractDescriptorBox) cur1).getDescriptor(), ((AbstractDescriptorBox) cur2).getDescriptor());
                                                    ((AbstractDescriptorBox) cur1).setDescriptor(esd);
                                                    vse.addBox(cur1);
                                                }
                                            } catch (IOException e) {
                                                LOG.logWarn(e.getMessage());
                                                return null;
                                            }
                                        }
                                    }
                                    return vse;
                                }
                                LOG.logError("horizontal resolution differs");
                                return null;
                            }
                            LOG.logError("vert resolution differs");
                            return null;
                        }
                        LOG.logError("width differs");
                        return null;
                    }
                    LOG.logError("height differs");
                    return null;
                }
                LOG.logError("frame count differs");
                return null;
            }
            LOG.logError("Depth differs");
            return null;
        }
        LOG.logError("Horizontal Resolution differs");
        return null;
    }

    private AudioSampleEntry mergeAudioSampleEntries(AudioSampleEntry ase1, AudioSampleEntry ase2) {
        AudioSampleEntry ase = new AudioSampleEntry(ase2.getType());
        if (ase1.getBytesPerFrame() == ase2.getBytesPerFrame()) {
            ase.setBytesPerFrame(ase1.getBytesPerFrame());
            if (ase1.getBytesPerPacket() != ase2.getBytesPerPacket()) {
                return null;
            }
            ase.setBytesPerPacket(ase1.getBytesPerPacket());
            if (ase1.getBytesPerSample() == ase2.getBytesPerSample()) {
                ase.setBytesPerSample(ase1.getBytesPerSample());
                if (ase1.getChannelCount() != ase2.getChannelCount()) {
                    return null;
                }
                ase.setChannelCount(ase1.getChannelCount());
                if (ase1.getPacketSize() == ase2.getPacketSize()) {
                    ase.setPacketSize(ase1.getPacketSize());
                    if (ase1.getCompressionId() != ase2.getCompressionId()) {
                        return null;
                    }
                    ase.setCompressionId(ase1.getCompressionId());
                    if (ase1.getSampleRate() != ase2.getSampleRate()) {
                        return null;
                    }
                    ase.setSampleRate(ase1.getSampleRate());
                    if (ase1.getSampleSize() != ase2.getSampleSize()) {
                        return null;
                    }
                    ase.setSampleSize(ase1.getSampleSize());
                    if (ase1.getSamplesPerPacket() != ase2.getSamplesPerPacket()) {
                        return null;
                    }
                    ase.setSamplesPerPacket(ase1.getSamplesPerPacket());
                    if (ase1.getSoundVersion() != ase2.getSoundVersion()) {
                        return null;
                    }
                    ase.setSoundVersion(ase1.getSoundVersion());
                    if (!Arrays.equals(ase1.getSoundVersion2Data(), ase2.getSoundVersion2Data())) {
                        return null;
                    }
                    ase.setSoundVersion2Data(ase1.getSoundVersion2Data());
                    if (ase1.getBoxes().size() == ase2.getBoxes().size()) {
                        Iterator<Box> bxs2 = ase2.getBoxes().iterator();
                        for (Box cur1 : ase1.getBoxes()) {
                            Box cur2 = bxs2.next();
                            ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
                            ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                            try {
                                cur1.getBox(Channels.newChannel(baos1));
                                cur2.getBox(Channels.newChannel(baos2));
                                if (Arrays.equals(baos1.toByteArray(), baos2.toByteArray())) {
                                    ase.addBox(cur1);
                                } else if (ESDescriptorBox.TYPE.equals(cur1.getType()) && ESDescriptorBox.TYPE.equals(cur2.getType())) {
                                    ESDescriptorBox esdsBox1 = (ESDescriptorBox) cur1;
                                    ESDescriptorBox esdsBox2 = (ESDescriptorBox) cur2;
                                    ESDescriptor esd = mergeDescriptors(esdsBox1.getEsDescriptor(), esdsBox2.getEsDescriptor());
                                    esdsBox1.setDescriptor(esd);
                                    ase.addBox(cur1);
                                }
                            } catch (IOException e) {
                                LOG.logWarn(e.getMessage());
                                return null;
                            }
                        }
                    }
                    return ase;
                }
                LOG.logError("ChannelCount differ");
                return null;
            }
            LOG.logError("BytesPerSample differ");
            return null;
        }
        LOG.logError("BytesPerFrame differ");
        return null;
    }

    private ESDescriptor mergeDescriptors(BaseDescriptor des1, BaseDescriptor des2) {
        if ((des1 instanceof ESDescriptor) && (des2 instanceof ESDescriptor)) {
            ESDescriptor esds1 = (ESDescriptor) des1;
            ESDescriptor esds2 = (ESDescriptor) des2;
            if (esds1.getURLFlag() != esds2.getURLFlag()) {
                return null;
            }
            esds1.getURLLength();
            esds2.getURLLength();
            if (esds1.getDependsOnEsId() != esds2.getDependsOnEsId() || esds1.getEsId() != esds2.getEsId() || esds1.getoCREsId() != esds2.getoCREsId() || esds1.getoCRstreamFlag() != esds2.getoCRstreamFlag() || esds1.getRemoteODFlag() != esds2.getRemoteODFlag() || esds1.getStreamDependenceFlag() != esds2.getStreamDependenceFlag()) {
                return null;
            }
            esds1.getStreamPriority();
            esds2.getStreamPriority();
            if (esds1.getURLString() != null) {
                esds1.getURLString().equals(esds2.getURLString());
            } else {
                esds2.getURLString();
            }
            if (esds1.getDecoderConfigDescriptor() == null ? esds2.getDecoderConfigDescriptor() != null : !esds1.getDecoderConfigDescriptor().equals(esds2.getDecoderConfigDescriptor())) {
                DecoderConfigDescriptor dcd1 = esds1.getDecoderConfigDescriptor();
                DecoderConfigDescriptor dcd2 = esds2.getDecoderConfigDescriptor();
                if (dcd1.getAudioSpecificInfo() != null && dcd2.getAudioSpecificInfo() != null && !dcd1.getAudioSpecificInfo().equals(dcd2.getAudioSpecificInfo())) {
                    return null;
                }
                if (dcd1.getAvgBitRate() != dcd2.getAvgBitRate()) {
                    dcd1.setAvgBitRate((dcd1.getAvgBitRate() + dcd2.getAvgBitRate()) / 2);
                }
                dcd1.getBufferSizeDB();
                dcd2.getBufferSizeDB();
                if (dcd1.getDecoderSpecificInfo() == null ? dcd2.getDecoderSpecificInfo() != null : !dcd1.getDecoderSpecificInfo().equals(dcd2.getDecoderSpecificInfo())) {
                    return null;
                }
                if (dcd1.getMaxBitRate() != dcd2.getMaxBitRate()) {
                    dcd1.setMaxBitRate(Math.max(dcd1.getMaxBitRate(), dcd2.getMaxBitRate()));
                }
                if (!dcd1.getProfileLevelIndicationDescriptors().equals(dcd2.getProfileLevelIndicationDescriptors()) || dcd1.getObjectTypeIndication() != dcd2.getObjectTypeIndication() || dcd1.getStreamType() != dcd2.getStreamType() || dcd1.getUpStream() != dcd2.getUpStream()) {
                    return null;
                }
            }
            if (esds1.getOtherDescriptors() == null ? esds2.getOtherDescriptors() != null : !esds1.getOtherDescriptors().equals(esds2.getOtherDescriptors())) {
                return null;
            }
            if (esds1.getSlConfigDescriptor() == null ? esds2.getSlConfigDescriptor() != null : !esds1.getSlConfigDescriptor().equals(esds2.getSlConfigDescriptor())) {
                return null;
            }
            return esds1;
        }
        LOG.logError("I can only merge ESDescriptors");
        return null;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public List<Sample> getSamples() {
        Track[] trackArr;
        ArrayList<Sample> lists = new ArrayList<>();
        for (Track track : this.tracks) {
            lists.addAll(track.getSamples());
        }
        return lists;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public SampleDescriptionBox getSampleDescriptionBox() {
        return this.stsd;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public synchronized long[] getSampleDurations() {
        Track[] trackArr;
        long[] decodingTimes;
        Track[] trackArr2;
        int numSamples = 0;
        for (Track track : this.tracks) {
            numSamples += track.getSampleDurations().length;
        }
        decodingTimes = new long[numSamples];
        int index = 0;
        for (Track track2 : this.tracks) {
            long[] sampleDurations = track2.getSampleDurations();
            int length = sampleDurations.length;
            int i = 0;
            while (i < length) {
                long l = sampleDurations[i];
                decodingTimes[index] = l;
                i++;
                index++;
            }
        }
        return decodingTimes;
    }

    @Override // com.googlecode.mp4parser.authoring.AbstractTrack, com.googlecode.mp4parser.authoring.Track
    public List<CompositionTimeToSample.Entry> getCompositionTimeEntries() {
        Track[] trackArr;
        if (this.tracks[0].getCompositionTimeEntries() != null && !this.tracks[0].getCompositionTimeEntries().isEmpty()) {
            List<int[]> lists = new LinkedList<>();
            for (Track track : this.tracks) {
                lists.add(CompositionTimeToSample.blowupCompositionTimes(track.getCompositionTimeEntries()));
            }
            LinkedList<CompositionTimeToSample.Entry> compositionTimeEntries = new LinkedList<>();
            for (int[] list : lists) {
                for (int compositionTime : list) {
                    if (compositionTimeEntries.isEmpty() || compositionTimeEntries.getLast().getOffset() != compositionTime) {
                        compositionTimeEntries.add(new CompositionTimeToSample.Entry(1, compositionTime));
                    } else {
                        CompositionTimeToSample.Entry e = compositionTimeEntries.getLast();
                        e.setCount(e.getCount() + 1);
                    }
                }
            }
            return compositionTimeEntries;
        }
        return null;
    }

    @Override // com.googlecode.mp4parser.authoring.AbstractTrack, com.googlecode.mp4parser.authoring.Track
    public long[] getSyncSamples() {
        Track[] trackArr;
        if (this.tracks[0].getSyncSamples() != null && this.tracks[0].getSyncSamples().length > 0) {
            int numSyncSamples = 0;
            for (Track track : this.tracks) {
                numSyncSamples += track.getSyncSamples().length;
            }
            long[] returnSyncSamples = new long[numSyncSamples];
            int pos = 0;
            long samplesBefore = 0;
            for (Track track2 : this.tracks) {
                long[] syncSamples = track2.getSyncSamples();
                int length = syncSamples.length;
                int i = 0;
                while (i < length) {
                    long l = syncSamples[i];
                    returnSyncSamples[pos] = samplesBefore + l;
                    i++;
                    pos++;
                }
                samplesBefore += track2.getSamples().size();
            }
            return returnSyncSamples;
        }
        return null;
    }

    @Override // com.googlecode.mp4parser.authoring.AbstractTrack, com.googlecode.mp4parser.authoring.Track
    public List<SampleDependencyTypeBox.Entry> getSampleDependencies() {
        Track[] trackArr;
        if (this.tracks[0].getSampleDependencies() != null && !this.tracks[0].getSampleDependencies().isEmpty()) {
            List<SampleDependencyTypeBox.Entry> list = new LinkedList<>();
            for (Track track : this.tracks) {
                list.addAll(track.getSampleDependencies());
            }
            return list;
        }
        return null;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public TrackMetaData getTrackMetaData() {
        return this.tracks[0].getTrackMetaData();
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public String getHandler() {
        return this.tracks[0].getHandler();
    }

    @Override // com.googlecode.mp4parser.authoring.AbstractTrack, com.googlecode.mp4parser.authoring.Track
    public SubSampleInformationBox getSubsampleInformationBox() {
        return this.tracks[0].getSubsampleInformationBox();
    }
}
