package com.googlecode.mp4parser.authoring.builder;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.DataEntryUrlBox;
import com.coremedia.iso.boxes.DataInformationBox;
import com.coremedia.iso.boxes.DataReferenceBox;
import com.coremedia.iso.boxes.EditBox;
import com.coremedia.iso.boxes.EditListBox;
import com.coremedia.iso.boxes.FileTypeBox;
import com.coremedia.iso.boxes.HandlerBox;
import com.coremedia.iso.boxes.HintMediaHeaderBox;
import com.coremedia.iso.boxes.MediaBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.MediaInformationBox;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.MovieHeaderBox;
import com.coremedia.iso.boxes.NullMediaHeaderBox;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SampleSizeBox;
import com.coremedia.iso.boxes.SampleTableBox;
import com.coremedia.iso.boxes.SampleToChunkBox;
import com.coremedia.iso.boxes.SchemeTypeBox;
import com.coremedia.iso.boxes.SoundMediaHeaderBox;
import com.coremedia.iso.boxes.StaticChunkOffsetBox;
import com.coremedia.iso.boxes.SubtitleMediaHeaderBox;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.coremedia.iso.boxes.TrackReferenceTypeBox;
import com.coremedia.iso.boxes.VideoMediaHeaderBox;
import com.coremedia.iso.boxes.fragment.MovieExtendsBox;
import com.coremedia.iso.boxes.fragment.MovieExtendsHeaderBox;
import com.coremedia.iso.boxes.fragment.MovieFragmentBox;
import com.coremedia.iso.boxes.fragment.MovieFragmentHeaderBox;
import com.coremedia.iso.boxes.fragment.MovieFragmentRandomAccessBox;
import com.coremedia.iso.boxes.fragment.MovieFragmentRandomAccessOffsetBox;
import com.coremedia.iso.boxes.fragment.SampleFlags;
import com.coremedia.iso.boxes.fragment.TrackExtendsBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentBaseMediaDecodeTimeBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentHeaderBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentRandomAccessBox;
import com.coremedia.iso.boxes.fragment.TrackRunBox;
import com.coremedia.iso.boxes.mdat.MediaDataBox;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.google.android.exoplayer2.C;
import com.googlecode.mp4parser.AbstractContainerBox;
import com.googlecode.mp4parser.BasicContainer;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.authoring.Edit;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.tracks.CencEncryptedTrack;
import com.googlecode.mp4parser.boxes.dece.SampleEncryptionBox;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.GroupEntry;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.SampleGroupDescriptionBox;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.SampleToGroupBox;
import com.googlecode.mp4parser.util.CastUtils;
import com.googlecode.mp4parser.util.Path;
import com.mp4parser.iso14496.part12.SampleAuxiliaryInformationOffsetsBox;
import com.mp4parser.iso14496.part12.SampleAuxiliaryInformationSizesBox;
import com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat;
import com.mp4parser.iso23001.part7.TrackEncryptionBox;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
/* loaded from: classes3.dex */
public class FragmentedMp4Builder implements Mp4Builder {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final Logger LOG = Logger.getLogger(FragmentedMp4Builder.class.getName());
    protected FragmentIntersectionFinder intersectionFinder;

    public Date getDate() {
        return new Date();
    }

    public Box createFtyp(Movie movie) {
        List<String> minorBrands = new LinkedList<>();
        minorBrands.add("isom");
        minorBrands.add("iso2");
        minorBrands.add(VisualSampleEntry.TYPE3);
        return new FileTypeBox("isom", 0L, minorBrands);
    }

    protected List<Track> sortTracksInSequence(List<Track> tracks, final int cycle, final Map<Track, long[]> intersectionMap) {
        List<Track> tracks2 = new LinkedList<>(tracks);
        Collections.sort(tracks2, new Comparator<Track>() { // from class: com.googlecode.mp4parser.authoring.builder.FragmentedMp4Builder.1
            public int compare(Track o1, Track o2) {
                long startSample1 = ((long[]) intersectionMap.get(o1))[cycle];
                long startSample2 = ((long[]) intersectionMap.get(o2))[cycle];
                long[] decTimes1 = o1.getSampleDurations();
                long[] decTimes2 = o2.getSampleDurations();
                long startTime1 = 0;
                long startTime2 = 0;
                int i = 1;
                while (i < startSample1) {
                    startTime1 += decTimes1[i - 1];
                    i++;
                    startSample1 = startSample1;
                }
                int i2 = 1;
                while (i2 < startSample2) {
                    startTime2 += decTimes2[i2 - 1];
                    i2++;
                    startSample1 = startSample1;
                }
                double d = startTime1;
                double timescale = o1.getTrackMetaData().getTimescale();
                Double.isNaN(d);
                Double.isNaN(timescale);
                double d2 = d / timescale;
                double d3 = startTime2;
                double timescale2 = o2.getTrackMetaData().getTimescale();
                Double.isNaN(d3);
                Double.isNaN(timescale2);
                return (int) ((d2 - (d3 / timescale2)) * 100.0d);
            }
        });
        return tracks2;
    }

    protected List<Box> createMoofMdat(Movie movie) {
        List<Box> moofsMdats = new LinkedList<>();
        HashMap<Track, long[]> intersectionMap = new HashMap<>();
        int maxNumberOfFragments = 0;
        for (Track track : movie.getTracks()) {
            long[] intersects = this.intersectionFinder.sampleNumbers(track);
            intersectionMap.put(track, intersects);
            maxNumberOfFragments = Math.max(maxNumberOfFragments, intersects.length);
        }
        int sequence = 1;
        int cycle = 0;
        while (cycle < maxNumberOfFragments) {
            List<Track> sortedTracks = sortTracksInSequence(movie.getTracks(), cycle, intersectionMap);
            int sequence2 = sequence;
            for (Track track2 : sortedTracks) {
                long[] startSamples = intersectionMap.get(track2);
                sequence2 = createFragment(moofsMdats, track2, startSamples, cycle, sequence2);
            }
            cycle++;
            sequence = sequence2;
        }
        return moofsMdats;
    }

    protected int createFragment(List<Box> moofsMdats, Track track, long[] startSamples, int cycle, int sequence) {
        if (cycle < startSamples.length) {
            long startSample = startSamples[cycle];
            long endSample = cycle + 1 < startSamples.length ? startSamples[cycle + 1] : track.getSamples().size() + 1;
            if (startSample != endSample) {
                moofsMdats.add(createMoof(startSample, endSample, track, sequence));
                int sequence2 = sequence + 1;
                moofsMdats.add(createMdat(startSample, endSample, track, sequence));
                return sequence2;
            }
        }
        return sequence;
    }

    @Override // com.googlecode.mp4parser.authoring.builder.Mp4Builder
    public Container build(Movie movie) {
        Logger logger = LOG;
        logger.fine("Creating movie " + movie);
        if (this.intersectionFinder == null) {
            Track refTrack = null;
            Iterator<Track> it = movie.getTracks().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Track track = it.next();
                if (track.getHandler().equals("vide")) {
                    refTrack = track;
                    break;
                }
            }
            this.intersectionFinder = new SyncSampleIntersectFinderImpl(movie, refTrack, -1);
        }
        BasicContainer isoFile = new BasicContainer();
        isoFile.addBox(createFtyp(movie));
        isoFile.addBox(createMoov(movie));
        for (Box box : createMoofMdat(movie)) {
            isoFile.addBox(box);
        }
        isoFile.addBox(createMfra(movie, isoFile));
        return isoFile;
    }

    protected Box createMdat(final long startSample, final long endSample, final Track track, final int i) {
        return new Box() { // from class: com.googlecode.mp4parser.authoring.builder.FragmentedMp4Builder.1Mdat
            Container parent;
            long size_ = -1;

            @Override // com.coremedia.iso.boxes.Box
            public Container getParent() {
                return this.parent;
            }

            @Override // com.coremedia.iso.boxes.Box
            public void setParent(Container parent) {
                this.parent = parent;
            }

            @Override // com.coremedia.iso.boxes.Box
            public long getOffset() {
                throw new RuntimeException("Doesn't have any meaning for programmatically created boxes");
            }

            @Override // com.coremedia.iso.boxes.Box
            public long getSize() {
                long j = this.size_;
                if (j != -1) {
                    return j;
                }
                long size = 8;
                for (Sample sample : FragmentedMp4Builder.this.getSamples(startSample, endSample, track, i)) {
                    size += sample.getSize();
                }
                this.size_ = size;
                return size;
            }

            @Override // com.coremedia.iso.boxes.Box
            public String getType() {
                return MediaDataBox.TYPE;
            }

            @Override // com.coremedia.iso.boxes.Box
            public void getBox(WritableByteChannel writableByteChannel) throws IOException {
                ByteBuffer header = ByteBuffer.allocate(8);
                IsoTypeWriter.writeUInt32(header, CastUtils.l2i(getSize()));
                header.put(IsoFile.fourCCtoBytes(getType()));
                header.rewind();
                writableByteChannel.write(header);
                List<Sample> samples = FragmentedMp4Builder.this.getSamples(startSample, endSample, track, i);
                for (Sample sample : samples) {
                    sample.writeTo(writableByteChannel);
                }
            }

            @Override // com.coremedia.iso.boxes.Box
            public void parse(DataSource fileChannel, ByteBuffer header, long contentSize, BoxParser boxParser) throws IOException {
            }
        };
    }

    protected void createTfhd(long startSample, long endSample, Track track, int sequenceNumber, TrackFragmentBox parent) {
        TrackFragmentHeaderBox tfhd = new TrackFragmentHeaderBox();
        SampleFlags sf = new SampleFlags();
        tfhd.setDefaultSampleFlags(sf);
        tfhd.setBaseDataOffset(-1L);
        tfhd.setTrackId(track.getTrackMetaData().getTrackId());
        tfhd.setDefaultBaseIsMoof(true);
        parent.addBox(tfhd);
    }

    protected void createMfhd(long startSample, long endSample, Track track, int sequenceNumber, MovieFragmentBox parent) {
        MovieFragmentHeaderBox mfhd = new MovieFragmentHeaderBox();
        mfhd.setSequenceNumber(sequenceNumber);
        parent.addBox(mfhd);
    }

    protected void createTraf(long startSample, long endSample, Track track, int sequenceNumber, MovieFragmentBox parent) {
        Iterator<Map.Entry<String, List<GroupEntry>>> it;
        String type;
        long j;
        SampleGroupDescriptionBox sgpd;
        long j2 = startSample;
        TrackFragmentBox traf = new TrackFragmentBox();
        parent.addBox(traf);
        createTfhd(startSample, endSample, track, sequenceNumber, traf);
        createTfdt(j2, track, traf);
        createTrun(startSample, endSample, track, sequenceNumber, traf);
        if (track instanceof CencEncryptedTrack) {
            createSaiz(startSample, endSample, (CencEncryptedTrack) track, sequenceNumber, traf);
            createSenc(startSample, endSample, (CencEncryptedTrack) track, sequenceNumber, traf);
            createSaio(startSample, endSample, (CencEncryptedTrack) track, sequenceNumber, traf);
        }
        Map<String, List<GroupEntry>> groupEntryFamilies = new HashMap<>();
        for (Map.Entry<GroupEntry, long[]> sg : track.getSampleGroups().entrySet()) {
            String type2 = sg.getKey().getType();
            List<GroupEntry> groupEntries = groupEntryFamilies.get(type2);
            if (groupEntries == null) {
                groupEntries = new ArrayList<>();
                groupEntryFamilies.put(type2, groupEntries);
            }
            groupEntries.add(sg.getKey());
            j2 = startSample;
        }
        Iterator<Map.Entry<String, List<GroupEntry>>> it2 = groupEntryFamilies.entrySet().iterator();
        while (it2.hasNext()) {
            Map.Entry<String, List<GroupEntry>> sg2 = it2.next();
            SampleGroupDescriptionBox sgpd2 = new SampleGroupDescriptionBox();
            String type3 = sg2.getKey();
            sgpd2.setGroupEntries(sg2.getValue());
            SampleToGroupBox sbgp = new SampleToGroupBox();
            sbgp.setGroupingType(type3);
            long j3 = 1;
            SampleToGroupBox.Entry last = null;
            int i = CastUtils.l2i(j2 - 1);
            while (i < CastUtils.l2i(endSample - j3)) {
                int index = 0;
                int j4 = 0;
                while (true) {
                    it = it2;
                    if (j4 >= sg2.getValue().size()) {
                        break;
                    }
                    SampleGroupDescriptionBox sgpd3 = sgpd2;
                    String type4 = type3;
                    GroupEntry groupEntry = sg2.getValue().get(j4);
                    long[] sampleNums = track.getSampleGroups().get(groupEntry);
                    if (Arrays.binarySearch(sampleNums, i) >= 0) {
                        index = j4 + 1;
                    }
                    j4++;
                    sgpd2 = sgpd3;
                    it2 = it;
                    type3 = type4;
                }
                if (last == null) {
                    sgpd = sgpd2;
                    type = type3;
                    j = 1;
                } else if (last.getGroupDescriptionIndex() != index) {
                    sgpd = sgpd2;
                    type = type3;
                    j = 1;
                } else {
                    sgpd = sgpd2;
                    type = type3;
                    j = 1;
                    last.setSampleCount(last.getSampleCount() + 1);
                    i++;
                    j2 = startSample;
                    j3 = j;
                    type3 = type;
                    sgpd2 = sgpd;
                    it2 = it;
                }
                last = new SampleToGroupBox.Entry(j, index);
                sbgp.getEntries().add(last);
                i++;
                j2 = startSample;
                j3 = j;
                type3 = type;
                sgpd2 = sgpd;
                it2 = it;
            }
            traf.addBox(sgpd2);
            traf.addBox(sbgp);
        }
    }

    protected void createSenc(long startSample, long endSample, CencEncryptedTrack track, int sequenceNumber, TrackFragmentBox parent) {
        SampleEncryptionBox senc = new SampleEncryptionBox();
        senc.setSubSampleEncryption(track.hasSubSampleEncryption());
        senc.setEntries(track.getSampleEncryptionEntries().subList(CastUtils.l2i(startSample - 1), CastUtils.l2i(endSample - 1)));
        parent.addBox(senc);
    }

    protected void createSaio(long startSample, long endSample, CencEncryptedTrack track, int sequenceNumber, TrackFragmentBox parent) {
        Box box;
        SchemeTypeBox schemeTypeBox = (SchemeTypeBox) Path.getPath((AbstractContainerBox) track.getSampleDescriptionBox(), "enc.[0]/sinf[0]/schm[0]");
        SampleAuxiliaryInformationOffsetsBox saio = new SampleAuxiliaryInformationOffsetsBox();
        parent.addBox(saio);
        if (parent.getBoxes(TrackRunBox.class).size() != 1) {
            throw new AssertionError("Don't know how to deal with multiple Track Run Boxes when encrypting");
        }
        saio.setAuxInfoType(C.CENC_TYPE_cenc);
        saio.setFlags(1);
        long offset = 0 + 8;
        Iterator<Box> it = parent.getBoxes().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Box box2 = it.next();
            if (box2 instanceof SampleEncryptionBox) {
                offset += ((SampleEncryptionBox) box2).getOffsetToFirstIV();
                break;
            }
            offset += box2.getSize();
        }
        MovieFragmentBox moof = (MovieFragmentBox) parent.getParent();
        long offset2 = offset + 16;
        Iterator<Box> it2 = moof.getBoxes().iterator();
        while (it2.hasNext() && (box = it2.next()) != parent) {
            offset2 += box.getSize();
        }
        saio.setOffsets(new long[]{offset2});
    }

    protected void createSaiz(long startSample, long endSample, CencEncryptedTrack track, int sequenceNumber, TrackFragmentBox parent) {
        SampleDescriptionBox sampleDescriptionBox = track.getSampleDescriptionBox();
        SchemeTypeBox schemeTypeBox = (SchemeTypeBox) Path.getPath((AbstractContainerBox) sampleDescriptionBox, "enc.[0]/sinf[0]/schm[0]");
        TrackEncryptionBox tenc = (TrackEncryptionBox) Path.getPath((AbstractContainerBox) sampleDescriptionBox, "enc.[0]/sinf[0]/schi[0]/tenc[0]");
        SampleAuxiliaryInformationSizesBox saiz = new SampleAuxiliaryInformationSizesBox();
        saiz.setAuxInfoType(C.CENC_TYPE_cenc);
        saiz.setFlags(1);
        if (track.hasSubSampleEncryption()) {
            short[] sizes = new short[CastUtils.l2i(endSample - startSample)];
            List<CencSampleAuxiliaryDataFormat> auxs = track.getSampleEncryptionEntries().subList(CastUtils.l2i(startSample - 1), CastUtils.l2i(endSample - 1));
            for (int i = 0; i < sizes.length; i++) {
                sizes[i] = (short) auxs.get(i).getSize();
            }
            saiz.setSampleInfoSizes(sizes);
        } else {
            saiz.setDefaultSampleInfoSize(tenc.getDefaultIvSize());
            saiz.setSampleCount(CastUtils.l2i(endSample - startSample));
        }
        parent.addBox(saiz);
    }

    protected List<Sample> getSamples(long startSample, long endSample, Track track, int sequenceNumber) {
        return track.getSamples().subList(CastUtils.l2i(startSample) - 1, CastUtils.l2i(endSample) - 1);
    }

    protected long[] getSampleSizes(long startSample, long endSample, Track track, int sequenceNumber) {
        List<Sample> samples = getSamples(startSample, endSample, track, sequenceNumber);
        long[] sampleSizes = new long[samples.size()];
        for (int i = 0; i < sampleSizes.length; i++) {
            sampleSizes[i] = samples.get(i).getSize();
        }
        return sampleSizes;
    }

    protected void createTfdt(long startSample, Track track, TrackFragmentBox parent) {
        TrackFragmentBaseMediaDecodeTimeBox tfdt = new TrackFragmentBaseMediaDecodeTimeBox();
        tfdt.setVersion(1);
        long startTime = 0;
        long[] times = track.getSampleDurations();
        for (int i = 1; i < startSample; i++) {
            startTime += times[i - 1];
        }
        tfdt.setBaseMediaDecodeTime(startTime);
        parent.addBox(tfdt);
    }

    protected void createTrun(long startSample, long endSample, Track track, int sequenceNumber, TrackFragmentBox parent) {
        long j;
        TrackRunBox trun = new TrackRunBox();
        trun.setVersion(1);
        long[] sampleSizes = getSampleSizes(startSample, endSample, track, sequenceNumber);
        trun.setSampleDurationPresent(true);
        trun.setSampleSizePresent(true);
        List<TrackRunBox.Entry> entries = new ArrayList<>(CastUtils.l2i(endSample - startSample));
        List<CompositionTimeToSample.Entry> compositionTimeEntries = track.getCompositionTimeEntries();
        int compositionTimeQueueIndex = 0;
        CompositionTimeToSample.Entry[] compositionTimeQueue = (compositionTimeEntries == null || compositionTimeEntries.size() <= 0) ? null : (CompositionTimeToSample.Entry[]) compositionTimeEntries.toArray(new CompositionTimeToSample.Entry[compositionTimeEntries.size()]);
        long compositionTimeEntriesLeft = compositionTimeQueue != null ? compositionTimeQueue[0].getCount() : -1;
        trun.setSampleCompositionTimeOffsetPresent(compositionTimeEntriesLeft > 0);
        for (long i = 1; i < startSample; i++) {
            if (compositionTimeQueue == null) {
                j = 0;
            } else {
                long j2 = compositionTimeEntriesLeft - 1;
                compositionTimeEntriesLeft = j2;
                j = 0;
                if (j2 == 0 && compositionTimeQueue.length - compositionTimeQueueIndex > 1) {
                    compositionTimeQueueIndex++;
                    compositionTimeEntriesLeft = compositionTimeQueue[compositionTimeQueueIndex].getCount();
                }
            }
        }
        boolean sampleFlagsRequired = (track.getSampleDependencies() != null && !track.getSampleDependencies().isEmpty()) || !(track.getSyncSamples() == null || track.getSyncSamples().length == 0);
        trun.setSampleFlagsPresent(sampleFlagsRequired);
        for (int i2 = 0; i2 < sampleSizes.length; i2++) {
            TrackRunBox.Entry entry = new TrackRunBox.Entry();
            entry.setSampleSize(sampleSizes[i2]);
            if (sampleFlagsRequired) {
                SampleFlags sflags = new SampleFlags();
                if (track.getSampleDependencies() != null && !track.getSampleDependencies().isEmpty()) {
                    SampleDependencyTypeBox.Entry e = track.getSampleDependencies().get(i2);
                    sflags.setSampleDependsOn(e.getSampleDependsOn());
                    sflags.setSampleIsDependedOn(e.getSampleIsDependentOn());
                    sflags.setSampleHasRedundancy(e.getSampleHasRedundancy());
                }
                if (track.getSyncSamples() != null && track.getSyncSamples().length > 0) {
                    if (Arrays.binarySearch(track.getSyncSamples(), startSample + i2) >= 0) {
                        sflags.setSampleIsDifferenceSample(false);
                        sflags.setSampleDependsOn(2);
                    } else {
                        sflags.setSampleIsDifferenceSample(true);
                        sflags.setSampleDependsOn(1);
                    }
                }
                entry.setSampleFlags(sflags);
            }
            entry.setSampleDuration(track.getSampleDurations()[CastUtils.l2i((startSample + i2) - 1)]);
            if (compositionTimeQueue != null) {
                entry.setSampleCompositionTimeOffset(compositionTimeQueue[compositionTimeQueueIndex].getOffset());
                long j3 = compositionTimeEntriesLeft - 1;
                compositionTimeEntriesLeft = j3;
                if (j3 == 0 && compositionTimeQueue.length - compositionTimeQueueIndex > 1) {
                    compositionTimeQueueIndex++;
                    compositionTimeEntriesLeft = compositionTimeQueue[compositionTimeQueueIndex].getCount();
                }
            }
            entries.add(entry);
        }
        trun.setEntries(entries);
        parent.addBox(trun);
    }

    protected Box createMoof(long startSample, long endSample, Track track, int sequenceNumber) {
        MovieFragmentBox moof = new MovieFragmentBox();
        createMfhd(startSample, endSample, track, sequenceNumber, moof);
        createTraf(startSample, endSample, track, sequenceNumber, moof);
        TrackRunBox firstTrun = moof.getTrackRunBoxes().get(0);
        firstTrun.setDataOffset(1);
        firstTrun.setDataOffset((int) (moof.getSize() + 8));
        return moof;
    }

    protected Box createMvhd(Movie movie) {
        MovieHeaderBox mvhd = new MovieHeaderBox();
        mvhd.setVersion(1);
        mvhd.setCreationTime(getDate());
        mvhd.setModificationTime(getDate());
        mvhd.setDuration(0L);
        long movieTimeScale = movie.getTimescale();
        mvhd.setTimescale(movieTimeScale);
        long nextTrackId = 0;
        for (Track track : movie.getTracks()) {
            nextTrackId = nextTrackId < track.getTrackMetaData().getTrackId() ? track.getTrackMetaData().getTrackId() : nextTrackId;
        }
        mvhd.setNextTrackId(1 + nextTrackId);
        return mvhd;
    }

    protected Box createMoov(Movie movie) {
        MovieBox movieBox = new MovieBox();
        movieBox.addBox(createMvhd(movie));
        for (Track track : movie.getTracks()) {
            movieBox.addBox(createTrak(track, movie));
        }
        movieBox.addBox(createMvex(movie));
        return movieBox;
    }

    protected Box createTfra(Track track, Container isoFile) {
        long offset;
        TrackExtendsBox trex;
        List<TrackFragmentRandomAccessBox.Entry> offset2timeEntries;
        List<TrackExtendsBox> trexs;
        TrackFragmentRandomAccessBox tfra;
        SampleFlags sf;
        TrackExtendsBox trex2;
        List<TrackFragmentRandomAccessBox.Entry> offset2timeEntries2;
        Box box;
        int i;
        List<TrackFragmentBox> trafs;
        List<TrackRunBox> truns;
        TrackFragmentBox traf;
        int j;
        int k;
        long offset2;
        TrackRunBox.Entry trunEntry;
        TrackFragmentRandomAccessBox tfra2 = new TrackFragmentRandomAccessBox();
        tfra2.setVersion(1);
        List<TrackFragmentRandomAccessBox.Entry> offset2timeEntries3 = new LinkedList<>();
        TrackExtendsBox trex3 = null;
        List<TrackExtendsBox> trexs2 = Path.getPaths(isoFile, "moov/mvex/trex");
        for (TrackExtendsBox innerTrex : trexs2) {
            TrackFragmentRandomAccessBox tfra3 = tfra2;
            List<TrackFragmentRandomAccessBox.Entry> offset2timeEntries4 = offset2timeEntries3;
            TrackExtendsBox trex4 = trex3;
            List<TrackExtendsBox> trexs3 = trexs2;
            if (innerTrex.getTrackId() == track.getTrackMetaData().getTrackId()) {
                trex3 = innerTrex;
                tfra2 = tfra3;
                trexs2 = trexs3;
                offset2timeEntries3 = offset2timeEntries4;
            } else {
                tfra2 = tfra3;
                trexs2 = trexs3;
                offset2timeEntries3 = offset2timeEntries4;
                trex3 = trex4;
            }
        }
        long offset3 = 0;
        long duration = 0;
        Iterator<Box> it = isoFile.getBoxes().iterator();
        while (it.hasNext()) {
            Box box2 = it.next();
            if (box2 instanceof MovieFragmentBox) {
                List<TrackFragmentBox> trafs2 = ((MovieFragmentBox) box2).getBoxes(TrackFragmentBox.class);
                int i2 = 0;
                while (i2 < trafs2.size()) {
                    TrackFragmentBox traf2 = trafs2.get(i2);
                    if (traf2.getTrackFragmentHeaderBox().getTrackId() != track.getTrackMetaData().getTrackId()) {
                        tfra = tfra2;
                        offset2timeEntries = offset2timeEntries3;
                        trex = trex3;
                        trexs = trexs2;
                        offset = offset3;
                    } else {
                        List<TrackRunBox> truns2 = traf2.getBoxes(TrackRunBox.class);
                        int j2 = 0;
                        while (true) {
                            tfra = tfra2;
                            if (j2 >= truns2.size()) {
                                offset2timeEntries = offset2timeEntries3;
                                trex = trex3;
                                trexs = trexs2;
                                offset = offset3;
                                break;
                            }
                            List<TrackFragmentRandomAccessBox.Entry> offset2timeEntriesThisTrun = new LinkedList<>();
                            TrackRunBox trun = truns2.get(j2);
                            List<TrackExtendsBox> trexs4 = trexs2;
                            int k2 = 0;
                            while (k2 < trun.getEntries().size()) {
                                TrackRunBox.Entry trunEntry2 = trun.getEntries().get(k2);
                                if (k2 == 0 && trun.isFirstSampleFlagsPresent()) {
                                    sf = trun.getFirstSampleFlags();
                                } else if (trun.isSampleFlagsPresent()) {
                                    sf = trunEntry2.getSampleFlags();
                                } else {
                                    sf = trex3.getDefaultSampleFlags();
                                }
                                if (sf == null) {
                                    offset2timeEntries2 = offset2timeEntries3;
                                    trex2 = trex3;
                                    if (track.getHandler().equals("vide")) {
                                        throw new RuntimeException("Cannot find SampleFlags for video track but it's required to build tfra");
                                    }
                                } else {
                                    offset2timeEntries2 = offset2timeEntries3;
                                    trex2 = trex3;
                                }
                                if (sf == null || sf.getSampleDependsOn() == 2) {
                                    offset2 = offset3;
                                    trunEntry = trunEntry2;
                                    k = k2;
                                    j = j2;
                                    traf = traf2;
                                    truns = truns2;
                                    trafs = trafs2;
                                    i = i2;
                                    box = box2;
                                    offset2timeEntriesThisTrun.add(new TrackFragmentRandomAccessBox.Entry(duration, offset2, i2 + 1, j2 + 1, k2 + 1));
                                } else {
                                    k = k2;
                                    offset2 = offset3;
                                    j = j2;
                                    traf = traf2;
                                    truns = truns2;
                                    trafs = trafs2;
                                    i = i2;
                                    box = box2;
                                    trunEntry = trunEntry2;
                                }
                                duration += trunEntry.getSampleDuration();
                                k2 = k + 1;
                                offset2timeEntries3 = offset2timeEntries2;
                                trex3 = trex2;
                                offset3 = offset2;
                                j2 = j;
                                traf2 = traf;
                                truns2 = truns;
                                trafs2 = trafs;
                                i2 = i;
                                box2 = box;
                            }
                            int k3 = offset2timeEntriesThisTrun.size();
                            if (k3 == trun.getEntries().size() && trun.getEntries().size() > 0) {
                                offset2timeEntries3.add(offset2timeEntriesThisTrun.get(0));
                            } else {
                                offset2timeEntries3.addAll(offset2timeEntriesThisTrun);
                            }
                            j2++;
                            tfra2 = tfra;
                            trexs2 = trexs4;
                        }
                    }
                    i2++;
                    tfra2 = tfra;
                    trexs2 = trexs;
                    offset2timeEntries3 = offset2timeEntries;
                    trex3 = trex;
                    offset3 = offset;
                    trafs2 = trafs2;
                    box2 = box2;
                }
                continue;
            }
            offset3 += box2.getSize();
            tfra2 = tfra2;
            trexs2 = trexs2;
            offset2timeEntries3 = offset2timeEntries3;
            trex3 = trex3;
        }
        tfra2.setEntries(offset2timeEntries3);
        tfra2.setTrackId(track.getTrackMetaData().getTrackId());
        return tfra2;
    }

    protected Box createMfra(Movie movie, Container isoFile) {
        MovieFragmentRandomAccessBox mfra = new MovieFragmentRandomAccessBox();
        for (Track track : movie.getTracks()) {
            mfra.addBox(createTfra(track, isoFile));
        }
        MovieFragmentRandomAccessOffsetBox mfro = new MovieFragmentRandomAccessOffsetBox();
        mfra.addBox(mfro);
        mfro.setMfraSize(mfra.getSize());
        return mfra;
    }

    protected Box createTrex(Movie movie, Track track) {
        TrackExtendsBox trex = new TrackExtendsBox();
        trex.setTrackId(track.getTrackMetaData().getTrackId());
        trex.setDefaultSampleDescriptionIndex(1L);
        trex.setDefaultSampleDuration(0L);
        trex.setDefaultSampleSize(0L);
        SampleFlags sf = new SampleFlags();
        if ("soun".equals(track.getHandler()) || "subt".equals(track.getHandler())) {
            sf.setSampleDependsOn(2);
            sf.setSampleIsDependedOn(2);
        }
        trex.setDefaultSampleFlags(sf);
        return trex;
    }

    protected Box createMvex(Movie movie) {
        MovieExtendsBox mvex = new MovieExtendsBox();
        MovieExtendsHeaderBox mved = new MovieExtendsHeaderBox();
        mved.setVersion(1);
        for (Track track : movie.getTracks()) {
            long trackDuration = getTrackDuration(movie, track);
            if (mved.getFragmentDuration() < trackDuration) {
                mved.setFragmentDuration(trackDuration);
            }
        }
        mvex.addBox(mved);
        for (Track track2 : movie.getTracks()) {
            mvex.addBox(createTrex(movie, track2));
        }
        return mvex;
    }

    protected Box createTkhd(Movie movie, Track track) {
        TrackHeaderBox tkhd = new TrackHeaderBox();
        tkhd.setVersion(1);
        tkhd.setFlags(7);
        tkhd.setAlternateGroup(track.getTrackMetaData().getGroup());
        tkhd.setCreationTime(track.getTrackMetaData().getCreationTime());
        tkhd.setDuration(0L);
        tkhd.setHeight(track.getTrackMetaData().getHeight());
        tkhd.setWidth(track.getTrackMetaData().getWidth());
        tkhd.setLayer(track.getTrackMetaData().getLayer());
        tkhd.setModificationTime(getDate());
        tkhd.setTrackId(track.getTrackMetaData().getTrackId());
        tkhd.setVolume(track.getTrackMetaData().getVolume());
        return tkhd;
    }

    private long getTrackDuration(Movie movie, Track track) {
        return (track.getDuration() * movie.getTimescale()) / track.getTrackMetaData().getTimescale();
    }

    protected Box createMdhd(Movie movie, Track track) {
        MediaHeaderBox mdhd = new MediaHeaderBox();
        mdhd.setCreationTime(track.getTrackMetaData().getCreationTime());
        mdhd.setModificationTime(getDate());
        mdhd.setDuration(0L);
        mdhd.setTimescale(track.getTrackMetaData().getTimescale());
        mdhd.setLanguage(track.getTrackMetaData().getLanguage());
        return mdhd;
    }

    protected Box createStbl(Movie movie, Track track) {
        SampleTableBox stbl = new SampleTableBox();
        createStsd(track, stbl);
        stbl.addBox(new TimeToSampleBox());
        stbl.addBox(new SampleToChunkBox());
        stbl.addBox(new SampleSizeBox());
        stbl.addBox(new StaticChunkOffsetBox());
        return stbl;
    }

    protected void createStsd(Track track, SampleTableBox stbl) {
        stbl.addBox(track.getSampleDescriptionBox());
    }

    protected Box createMinf(Track track, Movie movie) {
        MediaInformationBox minf = new MediaInformationBox();
        if (track.getHandler().equals("vide")) {
            minf.addBox(new VideoMediaHeaderBox());
        } else if (track.getHandler().equals("soun")) {
            minf.addBox(new SoundMediaHeaderBox());
        } else if (track.getHandler().equals("text")) {
            minf.addBox(new NullMediaHeaderBox());
        } else if (track.getHandler().equals("subt")) {
            minf.addBox(new SubtitleMediaHeaderBox());
        } else if (track.getHandler().equals(TrackReferenceTypeBox.TYPE1)) {
            minf.addBox(new HintMediaHeaderBox());
        } else if (track.getHandler().equals("sbtl")) {
            minf.addBox(new NullMediaHeaderBox());
        }
        minf.addBox(createDinf(movie, track));
        minf.addBox(createStbl(movie, track));
        return minf;
    }

    protected Box createMdiaHdlr(Track track, Movie movie) {
        HandlerBox hdlr = new HandlerBox();
        hdlr.setHandlerType(track.getHandler());
        return hdlr;
    }

    protected Box createMdia(Track track, Movie movie) {
        MediaBox mdia = new MediaBox();
        mdia.addBox(createMdhd(movie, track));
        mdia.addBox(createMdiaHdlr(track, movie));
        mdia.addBox(createMinf(track, movie));
        return mdia;
    }

    protected Box createTrak(Track track, Movie movie) {
        Logger logger = LOG;
        logger.fine("Creating Track " + track);
        TrackBox trackBox = new TrackBox();
        trackBox.addBox(createTkhd(movie, track));
        Box edts = createEdts(track, movie);
        if (edts != null) {
            trackBox.addBox(edts);
        }
        trackBox.addBox(createMdia(track, movie));
        return trackBox;
    }

    protected Box createEdts(Track track, Movie movie) {
        if (track.getEdits() != null && track.getEdits().size() > 0) {
            EditListBox elst = new EditListBox();
            elst.setVersion(1);
            List<EditListBox.Entry> entries = new ArrayList<>();
            for (Edit edit : track.getEdits()) {
                double segmentDuration = edit.getSegmentDuration();
                double timescale = movie.getTimescale();
                Double.isNaN(timescale);
                entries.add(new EditListBox.Entry(elst, Math.round(segmentDuration * timescale), (edit.getMediaTime() * track.getTrackMetaData().getTimescale()) / edit.getTimeScale(), edit.getMediaRate()));
            }
            elst.setEntries(entries);
            EditBox edts = new EditBox();
            edts.addBox(elst);
            return edts;
        }
        return null;
    }

    protected DataInformationBox createDinf(Movie movie, Track track) {
        DataInformationBox dinf = new DataInformationBox();
        DataReferenceBox dref = new DataReferenceBox();
        dinf.addBox(dref);
        DataEntryUrlBox url = new DataEntryUrlBox();
        url.setFlags(1);
        dref.addBox(url);
        return dinf;
    }

    public FragmentIntersectionFinder getFragmentIntersectionFinder() {
        return this.intersectionFinder;
    }

    public void setIntersectionFinder(FragmentIntersectionFinder intersectionFinder) {
        this.intersectionFinder = intersectionFinder;
    }
}
