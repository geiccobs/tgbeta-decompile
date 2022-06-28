package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.boxes.EC3SpecificBox;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import com.googlecode.mp4parser.util.CastUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
/* loaded from: classes3.dex */
public class EC3TrackImpl extends AbstractTrack {
    private static final long MAX_FRAMES_PER_MMAP = 20;
    private int bitrate;
    private final DataSource dataSource;
    private long[] decodingTimes;
    private int frameSize;
    SampleDescriptionBox sampleDescriptionBox;
    private List<Sample> samples;
    TrackMetaData trackMetaData = new TrackMetaData();
    private List<BitStreamInfo> bitStreamInfos = new LinkedList();

    public EC3TrackImpl(DataSource dataSource) throws IOException {
        super(dataSource.toString());
        this.dataSource = dataSource;
        boolean done = false;
        while (!done) {
            BitStreamInfo bsi = readVariables();
            if (bsi == null) {
                throw new IOException();
            }
            for (BitStreamInfo entry : this.bitStreamInfos) {
                if (bsi.strmtyp != 1 && entry.substreamid == bsi.substreamid) {
                    done = true;
                }
            }
            if (!done) {
                this.bitStreamInfos.add(bsi);
            }
        }
        if (this.bitStreamInfos.size() == 0) {
            throw new IOException();
        }
        int samplerate = this.bitStreamInfos.get(0).samplerate;
        this.sampleDescriptionBox = new SampleDescriptionBox();
        AudioSampleEntry audioSampleEntry = new AudioSampleEntry(AudioSampleEntry.TYPE9);
        audioSampleEntry.setChannelCount(2);
        audioSampleEntry.setSampleRate(samplerate);
        audioSampleEntry.setDataReferenceIndex(1);
        audioSampleEntry.setSampleSize(16);
        EC3SpecificBox ec3 = new EC3SpecificBox();
        int[] deps = new int[this.bitStreamInfos.size()];
        int[] chan_locs = new int[this.bitStreamInfos.size()];
        for (BitStreamInfo bsi2 : this.bitStreamInfos) {
            if (bsi2.strmtyp == 1) {
                int i = bsi2.substreamid;
                deps[i] = deps[i] + 1;
                chan_locs[bsi2.substreamid] = ((bsi2.chanmap >> 6) & 256) | ((bsi2.chanmap >> 5) & 255);
            }
        }
        for (BitStreamInfo bsi3 : this.bitStreamInfos) {
            if (bsi3.strmtyp != 1) {
                EC3SpecificBox.Entry e = new EC3SpecificBox.Entry();
                e.fscod = bsi3.fscod;
                e.bsid = bsi3.bsid;
                e.bsmod = bsi3.bsmod;
                e.acmod = bsi3.acmod;
                e.lfeon = bsi3.lfeon;
                e.reserved = 0;
                e.num_dep_sub = deps[bsi3.substreamid];
                e.chan_loc = chan_locs[bsi3.substreamid];
                e.reserved2 = 0;
                ec3.addEntry(e);
            }
            this.bitrate += bsi3.bitrate;
            this.frameSize += bsi3.frameSize;
        }
        ec3.setDataRate(this.bitrate / 1000);
        audioSampleEntry.addBox(ec3);
        this.sampleDescriptionBox.addBox(audioSampleEntry);
        this.trackMetaData.setCreationTime(new Date());
        this.trackMetaData.setModificationTime(new Date());
        this.trackMetaData.setTimescale(samplerate);
        this.trackMetaData.setVolume(1.0f);
        dataSource.position(0L);
        List<Sample> readSamples = readSamples();
        this.samples = readSamples;
        long[] jArr = new long[readSamples.size()];
        this.decodingTimes = jArr;
        Arrays.fill(jArr, 1536L);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.dataSource.close();
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public List<Sample> getSamples() {
        return this.samples;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public SampleDescriptionBox getSampleDescriptionBox() {
        return this.sampleDescriptionBox;
    }

    @Override // com.googlecode.mp4parser.authoring.AbstractTrack, com.googlecode.mp4parser.authoring.Track
    public List<CompositionTimeToSample.Entry> getCompositionTimeEntries() {
        return null;
    }

    @Override // com.googlecode.mp4parser.authoring.AbstractTrack, com.googlecode.mp4parser.authoring.Track
    public long[] getSyncSamples() {
        return null;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public long[] getSampleDurations() {
        return this.decodingTimes;
    }

    @Override // com.googlecode.mp4parser.authoring.AbstractTrack, com.googlecode.mp4parser.authoring.Track
    public List<SampleDependencyTypeBox.Entry> getSampleDependencies() {
        return null;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public TrackMetaData getTrackMetaData() {
        return this.trackMetaData;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public String getHandler() {
        return "soun";
    }

    @Override // com.googlecode.mp4parser.authoring.AbstractTrack, com.googlecode.mp4parser.authoring.Track
    public SubSampleInformationBox getSubsampleInformationBox() {
        return null;
    }

    private BitStreamInfo readVariables() throws IOException {
        int numblkscod;
        int i;
        int i2;
        long startPosition = this.dataSource.position();
        ByteBuffer bb = ByteBuffer.allocate(200);
        this.dataSource.read(bb);
        bb.rewind();
        BitReaderBuffer brb = new BitReaderBuffer(bb);
        int syncword = brb.readBits(16);
        if (syncword != 2935) {
            return null;
        }
        BitStreamInfo entry = new BitStreamInfo();
        entry.strmtyp = brb.readBits(2);
        entry.substreamid = brb.readBits(3);
        int frmsiz = brb.readBits(11);
        entry.frameSize = (frmsiz + 1) * 2;
        entry.fscod = brb.readBits(2);
        int fscod2 = -1;
        if (entry.fscod == 3) {
            fscod2 = brb.readBits(2);
            numblkscod = 3;
        } else {
            numblkscod = brb.readBits(2);
        }
        int numberOfBlocksPerSyncFrame = 0;
        switch (numblkscod) {
            case 0:
                numberOfBlocksPerSyncFrame = 1;
                break;
            case 1:
                numberOfBlocksPerSyncFrame = 2;
                break;
            case 2:
                numberOfBlocksPerSyncFrame = 3;
                break;
            case 3:
                numberOfBlocksPerSyncFrame = 6;
                break;
        }
        entry.frameSize *= 6 / numberOfBlocksPerSyncFrame;
        entry.acmod = brb.readBits(3);
        entry.lfeon = brb.readBits(1);
        entry.bsid = brb.readBits(5);
        brb.readBits(5);
        if (1 == brb.readBits(1)) {
            brb.readBits(8);
        }
        if (entry.acmod == 0) {
            brb.readBits(5);
            if (1 == brb.readBits(1)) {
                brb.readBits(8);
            }
        }
        if (1 == entry.strmtyp && 1 == brb.readBits(1)) {
            entry.chanmap = brb.readBits(16);
        }
        if (1 == brb.readBits(1)) {
            if (entry.acmod > 2) {
                brb.readBits(2);
            }
            if (1 == (entry.acmod & 1) && entry.acmod > 2) {
                brb.readBits(3);
                brb.readBits(3);
            }
            if ((entry.acmod & 4) > 0) {
                brb.readBits(3);
                brb.readBits(3);
            }
            if (1 == entry.lfeon && 1 == brb.readBits(1)) {
                brb.readBits(5);
            }
            if (entry.strmtyp == 0) {
                if (1 != brb.readBits(1)) {
                    i = 6;
                } else {
                    i = 6;
                    brb.readBits(6);
                }
                if (entry.acmod == 0 && 1 == brb.readBits(1)) {
                    brb.readBits(i);
                }
                if (1 == brb.readBits(1)) {
                    brb.readBits(i);
                }
                int mixdef = brb.readBits(2);
                if (1 != mixdef) {
                    if (2 == mixdef) {
                        brb.readBits(12);
                    } else if (3 == mixdef) {
                        int mixdeflen = brb.readBits(5);
                        if (1 == brb.readBits(1)) {
                            brb.readBits(5);
                            if (1 != brb.readBits(1)) {
                                i2 = 4;
                            } else {
                                i2 = 4;
                                brb.readBits(4);
                            }
                            if (1 == brb.readBits(1)) {
                                brb.readBits(i2);
                            }
                            if (1 == brb.readBits(1)) {
                                brb.readBits(i2);
                            }
                            if (1 == brb.readBits(1)) {
                                brb.readBits(i2);
                            }
                            if (1 == brb.readBits(1)) {
                                brb.readBits(i2);
                            }
                            if (1 == brb.readBits(1)) {
                                brb.readBits(i2);
                            }
                            if (1 == brb.readBits(1)) {
                                brb.readBits(i2);
                            }
                            if (1 == brb.readBits(1)) {
                                if (1 == brb.readBits(1)) {
                                    brb.readBits(i2);
                                }
                                if (1 == brb.readBits(1)) {
                                    brb.readBits(i2);
                                }
                            }
                        }
                        if (1 == brb.readBits(1)) {
                            brb.readBits(5);
                            if (1 == brb.readBits(1)) {
                                brb.readBits(7);
                                if (1 == brb.readBits(1)) {
                                    brb.readBits(8);
                                }
                            }
                        }
                        for (int i3 = 0; i3 < mixdeflen + 2; i3++) {
                            brb.readBits(8);
                        }
                        brb.byteSync();
                    }
                } else {
                    brb.readBits(5);
                }
                if (entry.acmod < 2) {
                    if (1 == brb.readBits(1)) {
                        brb.readBits(14);
                    }
                    if (entry.acmod == 0 && 1 == brb.readBits(1)) {
                        brb.readBits(14);
                    }
                    if (1 == brb.readBits(1)) {
                        if (numblkscod == 0) {
                            brb.readBits(5);
                        } else {
                            for (int i4 = 0; i4 < numberOfBlocksPerSyncFrame; i4++) {
                                if (1 == brb.readBits(1)) {
                                    brb.readBits(5);
                                }
                            }
                        }
                    }
                }
            }
        }
        int i5 = brb.readBits(1);
        if (1 == i5) {
            entry.bsmod = brb.readBits(3);
        }
        switch (entry.fscod) {
            case 0:
                entry.samplerate = 48000;
                break;
            case 1:
                entry.samplerate = 44100;
                break;
            case 2:
                entry.samplerate = 32000;
                break;
            case 3:
                switch (fscod2) {
                    case 0:
                        entry.samplerate = 24000;
                        break;
                    case 1:
                        entry.samplerate = 22050;
                        break;
                    case 2:
                        entry.samplerate = 16000;
                        break;
                    case 3:
                        entry.samplerate = 0;
                        break;
                }
        }
        if (entry.samplerate == 0) {
            return null;
        }
        double d = entry.samplerate;
        Double.isNaN(d);
        double d2 = entry.frameSize;
        Double.isNaN(d2);
        entry.bitrate = (int) ((d / 1536.0d) * d2 * 8.0d);
        this.dataSource.position(entry.frameSize + startPosition);
        return entry;
    }

    private List<Sample> readSamples() throws IOException {
        int framesLeft = CastUtils.l2i((this.dataSource.size() - this.dataSource.position()) / this.frameSize);
        List<Sample> mySamples = new ArrayList<>(framesLeft);
        for (int i = 0; i < framesLeft; i++) {
            final int start = this.frameSize * i;
            mySamples.add(new Sample() { // from class: com.googlecode.mp4parser.authoring.tracks.EC3TrackImpl.1
                @Override // com.googlecode.mp4parser.authoring.Sample
                public void writeTo(WritableByteChannel channel) throws IOException {
                    EC3TrackImpl.this.dataSource.transferTo(start, EC3TrackImpl.this.frameSize, channel);
                }

                @Override // com.googlecode.mp4parser.authoring.Sample
                public long getSize() {
                    return EC3TrackImpl.this.frameSize;
                }

                @Override // com.googlecode.mp4parser.authoring.Sample
                public ByteBuffer asByteBuffer() {
                    try {
                        return EC3TrackImpl.this.dataSource.map(start, EC3TrackImpl.this.frameSize);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
        return mySamples;
    }

    /* loaded from: classes3.dex */
    public static class BitStreamInfo extends EC3SpecificBox.Entry {
        public int bitrate;
        public int chanmap;
        public int frameSize;
        public int samplerate;
        public int strmtyp;
        public int substreamid;

        @Override // com.googlecode.mp4parser.boxes.EC3SpecificBox.Entry
        public String toString() {
            return "BitStreamInfo{frameSize=" + this.frameSize + ", substreamid=" + this.substreamid + ", bitrate=" + this.bitrate + ", samplerate=" + this.samplerate + ", strmtyp=" + this.strmtyp + ", chanmap=" + this.chanmap + '}';
        }
    }

    public String toString() {
        return "EC3TrackImpl{bitrate=" + this.bitrate + ", bitStreamInfos=" + this.bitStreamInfos + '}';
    }
}
