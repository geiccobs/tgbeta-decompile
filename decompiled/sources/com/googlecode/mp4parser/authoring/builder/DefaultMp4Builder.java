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
import com.coremedia.iso.boxes.NullMediaHeaderBox;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleSizeBox;
import com.coremedia.iso.boxes.SampleTableBox;
import com.coremedia.iso.boxes.SampleToChunkBox;
import com.coremedia.iso.boxes.SoundMediaHeaderBox;
import com.coremedia.iso.boxes.StaticChunkOffsetBox;
import com.coremedia.iso.boxes.SubtitleMediaHeaderBox;
import com.coremedia.iso.boxes.SyncSampleBox;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.coremedia.iso.boxes.TrackReferenceTypeBox;
import com.coremedia.iso.boxes.VideoMediaHeaderBox;
import com.coremedia.iso.boxes.mdat.MediaDataBox;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.google.android.exoplayer2.C;
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
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
/* loaded from: classes3.dex */
public class DefaultMp4Builder implements Mp4Builder {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static Logger LOG = Logger.getLogger(DefaultMp4Builder.class.getName());
    private FragmentIntersectionFinder intersectionFinder;
    Set<StaticChunkOffsetBox> chunkOffsetBoxes = new HashSet();
    Set<SampleAuxiliaryInformationOffsetsBox> sampleAuxiliaryInformationOffsetsBoxes = new HashSet();
    HashMap<Track, List<Sample>> track2Sample = new HashMap<>();
    HashMap<Track, long[]> track2SampleSizes = new HashMap<>();

    private static long sum(int[] ls) {
        long rc = 0;
        for (long l : ls) {
            rc += l;
        }
        return rc;
    }

    private static long sum(long[] ls) {
        long rc = 0;
        for (long l : ls) {
            rc += l;
        }
        return rc;
    }

    public static long gcd(long a, long b) {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }

    public void setIntersectionFinder(FragmentIntersectionFinder intersectionFinder) {
        this.intersectionFinder = intersectionFinder;
    }

    @Override // com.googlecode.mp4parser.authoring.builder.Mp4Builder
    public Container build(Movie movie) {
        Object b;
        InterleaveChunkMdat mdat;
        if (this.intersectionFinder == null) {
            this.intersectionFinder = new TwoSecondIntersectionFinder(movie, 2);
        }
        LOG.fine("Creating movie " + movie);
        for (Track track : movie.getTracks()) {
            List<Sample> samples = track.getSamples();
            putSamples(track, samples);
            long[] sizes = new long[samples.size()];
            for (int i = 0; i < sizes.length; i++) {
                Sample b2 = samples.get(i);
                sizes[i] = b2.getSize();
            }
            this.track2SampleSizes.put(track, sizes);
        }
        BasicContainer isoFile = new BasicContainer();
        isoFile.addBox(createFileTypeBox(movie));
        Map<Track, int[]> chunks = new HashMap<>();
        for (Track track2 : movie.getTracks()) {
            chunks.put(track2, getChunkSizes(track2, movie));
        }
        Box moov = createMovieBox(movie, chunks);
        isoFile.addBox(moov);
        List<SampleSizeBox> stszs = Path.getPaths(moov, "trak/mdia/minf/stbl/stsz");
        long contentSize = 0;
        for (SampleSizeBox stsz : stszs) {
            contentSize += sum(stsz.getSampleSizes());
        }
        InterleaveChunkMdat mdat2 = new InterleaveChunkMdat(this, movie, chunks, contentSize, null);
        isoFile.addBox(mdat2);
        long dataOffset = mdat2.getDataOffset();
        for (StaticChunkOffsetBox chunkOffsetBox : this.chunkOffsetBoxes) {
            InterleaveChunkMdat mdat3 = mdat2;
            long[] offsets = chunkOffsetBox.getChunkOffsets();
            for (int i2 = 0; i2 < offsets.length; i2++) {
                offsets[i2] = offsets[i2] + dataOffset;
            }
            mdat2 = mdat3;
        }
        for (SampleAuxiliaryInformationOffsetsBox saio : this.sampleAuxiliaryInformationOffsetsBoxes) {
            long offset = saio.getSize();
            long offset2 = offset + 44;
            Object b3 = saio;
            while (true) {
                Object current = b3;
                b = ((Box) b3).getParent();
                Iterator<Box> it = ((Container) b).getBoxes().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        mdat = mdat2;
                        break;
                    }
                    mdat = mdat2;
                    Box box = it.next();
                    if (box == current) {
                        break;
                    }
                    offset2 += box.getSize();
                    mdat2 = mdat;
                }
                if (!(b instanceof Box)) {
                    break;
                }
                mdat2 = mdat;
                b3 = b;
            }
            long[] saioOffsets = saio.getOffsets();
            int i3 = 0;
            while (true) {
                Object b4 = b;
                if (i3 >= saioOffsets.length) {
                    break;
                }
                saioOffsets[i3] = saioOffsets[i3] + offset2;
                i3++;
                b = b4;
            }
            saio.setOffsets(saioOffsets);
            mdat2 = mdat;
        }
        return isoFile;
    }

    protected List<Sample> putSamples(Track track, List<Sample> samples) {
        return this.track2Sample.put(track, samples);
    }

    protected FileTypeBox createFileTypeBox(Movie movie) {
        List<String> minorBrands = new LinkedList<>();
        minorBrands.add("isom");
        minorBrands.add("iso2");
        minorBrands.add(VisualSampleEntry.TYPE3);
        return new FileTypeBox("isom", 0L, minorBrands);
    }

    /* JADX WARN: Removed duplicated region for block: B:37:0x011c A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:39:0x0115 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected com.coremedia.iso.boxes.MovieBox createMovieBox(com.googlecode.mp4parser.authoring.Movie r20, java.util.Map<com.googlecode.mp4parser.authoring.Track, int[]> r21) {
        /*
            Method dump skipped, instructions count: 290
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder.createMovieBox(com.googlecode.mp4parser.authoring.Movie, java.util.Map):com.coremedia.iso.boxes.MovieBox");
    }

    protected Box createUdta(Movie movie) {
        return null;
    }

    protected TrackBox createTrackBox(Track track, Movie movie, Map<Track, int[]> chunks) {
        TrackBox trackBox = new TrackBox();
        TrackHeaderBox tkhd = new TrackHeaderBox();
        tkhd.setEnabled(true);
        tkhd.setInMovie(true);
        tkhd.setInPreview(true);
        tkhd.setInPoster(true);
        tkhd.setMatrix(track.getTrackMetaData().getMatrix());
        tkhd.setAlternateGroup(track.getTrackMetaData().getGroup());
        tkhd.setCreationTime(track.getTrackMetaData().getCreationTime());
        if (track.getEdits() == null || track.getEdits().isEmpty()) {
            long d = track.getDuration();
            tkhd.setDuration((d * getTimescale(movie)) / track.getTrackMetaData().getTimescale());
        } else {
            long d2 = 0;
            for (Edit edit : track.getEdits()) {
                d2 += (long) edit.getSegmentDuration();
            }
            tkhd.setDuration(track.getTrackMetaData().getTimescale() * d2);
        }
        tkhd.setHeight(track.getTrackMetaData().getHeight());
        tkhd.setWidth(track.getTrackMetaData().getWidth());
        tkhd.setLayer(track.getTrackMetaData().getLayer());
        tkhd.setModificationTime(new Date());
        tkhd.setTrackId(track.getTrackMetaData().getTrackId());
        tkhd.setVolume(track.getTrackMetaData().getVolume());
        trackBox.addBox(tkhd);
        trackBox.addBox(createEdts(track, movie));
        MediaBox mdia = new MediaBox();
        trackBox.addBox(mdia);
        MediaHeaderBox mdhd = new MediaHeaderBox();
        mdhd.setCreationTime(track.getTrackMetaData().getCreationTime());
        mdhd.setDuration(track.getDuration());
        mdhd.setTimescale(track.getTrackMetaData().getTimescale());
        mdhd.setLanguage(track.getTrackMetaData().getLanguage());
        mdia.addBox(mdhd);
        HandlerBox hdlr = new HandlerBox();
        mdia.addBox(hdlr);
        hdlr.setHandlerType(track.getHandler());
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
        DataInformationBox dinf = new DataInformationBox();
        DataReferenceBox dref = new DataReferenceBox();
        dinf.addBox(dref);
        DataEntryUrlBox url = new DataEntryUrlBox();
        url.setFlags(1);
        dref.addBox(url);
        minf.addBox(dinf);
        Box stbl = createStbl(track, movie, chunks);
        minf.addBox(stbl);
        mdia.addBox(minf);
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

    protected Box createStbl(Track track, Movie movie, Map<Track, int[]> chunks) {
        long j;
        DefaultMp4Builder defaultMp4Builder = this;
        Track track2 = track;
        Map<Track, int[]> map = chunks;
        SampleTableBox stbl = new SampleTableBox();
        defaultMp4Builder.createStsd(track2, stbl);
        defaultMp4Builder.createStts(track2, stbl);
        defaultMp4Builder.createCtts(track2, stbl);
        defaultMp4Builder.createStss(track2, stbl);
        defaultMp4Builder.createSdtp(track2, stbl);
        defaultMp4Builder.createStsc(track2, map, stbl);
        defaultMp4Builder.createStsz(track2, stbl);
        defaultMp4Builder.createStco(track2, movie, map, stbl);
        Map<String, List<GroupEntry>> groupEntryFamilies = new HashMap<>();
        for (Map.Entry<GroupEntry, long[]> sg : track.getSampleGroups().entrySet()) {
            SampleTableBox stbl2 = stbl;
            String type = sg.getKey().getType();
            List<GroupEntry> groupEntries = groupEntryFamilies.get(type);
            if (groupEntries == null) {
                groupEntries = new ArrayList<>();
                groupEntryFamilies.put(type, groupEntries);
            }
            groupEntries.add(sg.getKey());
            defaultMp4Builder = this;
            track2 = track;
            map = chunks;
            stbl = stbl2;
        }
        for (Map.Entry<String, List<GroupEntry>> sg2 : groupEntryFamilies.entrySet()) {
            SampleGroupDescriptionBox sgdb = new SampleGroupDescriptionBox();
            String type2 = sg2.getKey();
            sgdb.setGroupEntries(sg2.getValue());
            SampleToGroupBox sbgp = new SampleToGroupBox();
            sbgp.setGroupingType(type2);
            int i = 0;
            SampleToGroupBox.Entry last = null;
            while (i < track.getSamples().size()) {
                int index = 0;
                int j2 = 0;
                while (j2 < sg2.getValue().size()) {
                    GroupEntry groupEntry = sg2.getValue().get(j2);
                    long[] sampleNums = track.getSampleGroups().get(groupEntry);
                    SampleTableBox stbl3 = stbl;
                    if (Arrays.binarySearch(sampleNums, i) >= 0) {
                        index = j2 + 1;
                    }
                    j2++;
                    map = chunks;
                    stbl = stbl3;
                }
                if (last == null) {
                    j = 1;
                } else if (last.getGroupDescriptionIndex() != index) {
                    j = 1;
                } else {
                    last.setSampleCount(last.getSampleCount() + 1);
                    i++;
                    defaultMp4Builder = this;
                    track2 = track;
                }
                SampleToGroupBox.Entry last2 = new SampleToGroupBox.Entry(j, index);
                sbgp.getEntries().add(last2);
                last = last2;
                i++;
                defaultMp4Builder = this;
                track2 = track;
            }
            stbl.addBox(sgdb);
            stbl.addBox(sbgp);
        }
        if (track2 instanceof CencEncryptedTrack) {
            defaultMp4Builder.createCencBoxes((CencEncryptedTrack) track2, stbl, map.get(track2));
        }
        defaultMp4Builder.createSubs(track2, stbl);
        return stbl;
    }

    protected void createSubs(Track track, SampleTableBox stbl) {
        if (track.getSubsampleInformationBox() != null) {
            stbl.addBox(track.getSubsampleInformationBox());
        }
    }

    protected void createCencBoxes(CencEncryptedTrack track, SampleTableBox stbl, int[] chunkSizes) {
        SampleAuxiliaryInformationSizesBox saiz = new SampleAuxiliaryInformationSizesBox();
        saiz.setAuxInfoType(C.CENC_TYPE_cenc);
        saiz.setFlags(1);
        List<CencSampleAuxiliaryDataFormat> sampleEncryptionEntries = track.getSampleEncryptionEntries();
        if (track.hasSubSampleEncryption()) {
            short[] sizes = new short[sampleEncryptionEntries.size()];
            for (int i = 0; i < sizes.length; i++) {
                sizes[i] = (short) sampleEncryptionEntries.get(i).getSize();
            }
            saiz.setSampleInfoSizes(sizes);
        } else {
            saiz.setDefaultSampleInfoSize(8);
            saiz.setSampleCount(track.getSamples().size());
        }
        SampleAuxiliaryInformationOffsetsBox saio = new SampleAuxiliaryInformationOffsetsBox();
        SampleEncryptionBox senc = new SampleEncryptionBox();
        senc.setSubSampleEncryption(track.hasSubSampleEncryption());
        senc.setEntries(sampleEncryptionEntries);
        long offset = senc.getOffsetToFirstIV();
        int index = 0;
        long[] offsets = new long[chunkSizes.length];
        for (int i2 = 0; i2 < chunkSizes.length; i2++) {
            offsets[i2] = offset;
            int j = 0;
            while (j < chunkSizes[i2]) {
                offset += sampleEncryptionEntries.get(index).getSize();
                j++;
                index++;
            }
        }
        saio.setOffsets(offsets);
        stbl.addBox(saiz);
        stbl.addBox(saio);
        stbl.addBox(senc);
        this.sampleAuxiliaryInformationOffsetsBoxes.add(saio);
    }

    protected void createStsd(Track track, SampleTableBox stbl) {
        stbl.addBox(track.getSampleDescriptionBox());
    }

    protected void createStco(Track track, Movie movie, Map<Track, int[]> chunks, SampleTableBox stbl) {
        StaticChunkOffsetBox stco;
        int[] tracksChunkSizes;
        long[] chunkOffset;
        int i;
        Track track2 = track;
        Map<Track, int[]> map = chunks;
        int[] chunkSizes = map.get(track2);
        StaticChunkOffsetBox stco2 = new StaticChunkOffsetBox();
        this.chunkOffsetBoxes.add(stco2);
        long offset = 0;
        long[] chunkOffset2 = new long[chunkSizes.length];
        if (LOG.isLoggable(Level.FINE)) {
            Logger logger = LOG;
            logger.fine("Calculating chunk offsets for track_" + track.getTrackMetaData().getTrackId());
        }
        int i2 = 0;
        while (i2 < chunkSizes.length) {
            if (LOG.isLoggable(Level.FINER)) {
                Logger logger2 = LOG;
                logger2.finer("Calculating chunk offsets for track_" + track.getTrackMetaData().getTrackId() + " chunk " + i2);
            }
            for (Track current : movie.getTracks()) {
                if (!LOG.isLoggable(Level.FINEST)) {
                    tracksChunkSizes = chunkSizes;
                    stco = stco2;
                } else {
                    Logger logger3 = LOG;
                    StringBuilder sb = new StringBuilder("Adding offsets of track_");
                    tracksChunkSizes = chunkSizes;
                    stco = stco2;
                    sb.append(current.getTrackMetaData().getTrackId());
                    logger3.finest(sb.toString());
                }
                int[] tracksChunkSizes2 = map.get(current);
                int[] chunkSizes2 = tracksChunkSizes2;
                long firstSampleOfChunk = 0;
                int j = 0;
                while (j < i2) {
                    firstSampleOfChunk += chunkSizes2[j];
                    j++;
                    track2 = track;
                }
                if (current == track2) {
                    chunkOffset2[i2] = offset;
                }
                int j2 = CastUtils.l2i(firstSampleOfChunk);
                while (true) {
                    chunkOffset = chunkOffset2;
                    i = i2;
                    if (j2 >= chunkSizes2[i2] + firstSampleOfChunk) {
                        break;
                    }
                    offset += this.track2SampleSizes.get(current)[j2];
                    j2++;
                    chunkOffset2 = chunkOffset;
                    i2 = i;
                }
                track2 = track;
                map = chunks;
                chunkSizes = tracksChunkSizes;
                stco2 = stco;
                chunkOffset2 = chunkOffset;
                i2 = i;
            }
            i2++;
        }
        stco2.setChunkOffsets(chunkOffset2);
        stbl.addBox(stco2);
    }

    protected void createStsz(Track track, SampleTableBox stbl) {
        SampleSizeBox stsz = new SampleSizeBox();
        stsz.setSampleSizes(this.track2SampleSizes.get(track));
        stbl.addBox(stsz);
    }

    protected void createStsc(Track track, Map<Track, int[]> chunks, SampleTableBox stbl) {
        int[] tracksChunkSizes = chunks.get(track);
        SampleToChunkBox stsc = new SampleToChunkBox();
        stsc.setEntries(new LinkedList());
        long lastChunkSize = -2147483648L;
        for (int i = 0; i < tracksChunkSizes.length; i++) {
            if (lastChunkSize != tracksChunkSizes[i]) {
                stsc.getEntries().add(new SampleToChunkBox.Entry(i + 1, tracksChunkSizes[i], 1L));
                lastChunkSize = tracksChunkSizes[i];
            }
        }
        stbl.addBox(stsc);
    }

    protected void createSdtp(Track track, SampleTableBox stbl) {
        if (track.getSampleDependencies() != null && !track.getSampleDependencies().isEmpty()) {
            SampleDependencyTypeBox sdtp = new SampleDependencyTypeBox();
            sdtp.setEntries(track.getSampleDependencies());
            stbl.addBox(sdtp);
        }
    }

    protected void createStss(Track track, SampleTableBox stbl) {
        long[] syncSamples = track.getSyncSamples();
        if (syncSamples != null && syncSamples.length > 0) {
            SyncSampleBox stss = new SyncSampleBox();
            stss.setSampleNumber(syncSamples);
            stbl.addBox(stss);
        }
    }

    protected void createCtts(Track track, SampleTableBox stbl) {
        List<CompositionTimeToSample.Entry> compositionTimeToSampleEntries = track.getCompositionTimeEntries();
        if (compositionTimeToSampleEntries != null && !compositionTimeToSampleEntries.isEmpty()) {
            CompositionTimeToSample ctts = new CompositionTimeToSample();
            ctts.setEntries(compositionTimeToSampleEntries);
            stbl.addBox(ctts);
        }
    }

    protected void createStts(Track track, SampleTableBox stbl) {
        long[] sampleDurations;
        TimeToSampleBox.Entry lastEntry = null;
        List<TimeToSampleBox.Entry> entries = new ArrayList<>();
        for (long delta : track.getSampleDurations()) {
            if (lastEntry != null && lastEntry.getDelta() == delta) {
                lastEntry.setCount(lastEntry.getCount() + 1);
            } else {
                lastEntry = new TimeToSampleBox.Entry(1L, delta);
                entries.add(lastEntry);
            }
        }
        TimeToSampleBox stts = new TimeToSampleBox();
        stts.setEntries(entries);
        stbl.addBox(stts);
    }

    int[] getChunkSizes(Track track, Movie movie) {
        long end;
        long[] referenceChunkStarts = this.intersectionFinder.sampleNumbers(track);
        int[] chunkSizes = new int[referenceChunkStarts.length];
        for (int i = 0; i < referenceChunkStarts.length; i++) {
            long start = referenceChunkStarts[i] - 1;
            if (referenceChunkStarts.length == i + 1) {
                end = track.getSamples().size();
            } else {
                end = referenceChunkStarts[i + 1] - 1;
            }
            chunkSizes[i] = CastUtils.l2i(end - start);
        }
        if (this.track2Sample.get(track).size() != sum(chunkSizes)) {
            throw new AssertionError("The number of samples and the sum of all chunk lengths must be equal");
        }
        return chunkSizes;
    }

    public long getTimescale(Movie movie) {
        long timescale = movie.getTracks().iterator().next().getTrackMetaData().getTimescale();
        for (Track track : movie.getTracks()) {
            timescale = gcd(track.getTrackMetaData().getTimescale(), timescale);
        }
        return timescale;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class InterleaveChunkMdat implements Box {
        List<List<Sample>> chunkList;
        long contentSize;
        Container parent;
        List<Track> tracks;

        private InterleaveChunkMdat(Movie movie, Map<Track, int[]> chunks, long contentSize) {
            DefaultMp4Builder.this = r11;
            this.chunkList = new ArrayList();
            this.contentSize = contentSize;
            this.tracks = movie.getTracks();
            for (int i = 0; i < chunks.values().iterator().next().length; i++) {
                for (Track track : this.tracks) {
                    int[] chunkSizes = chunks.get(track);
                    long firstSampleOfChunk = 0;
                    for (int j = 0; j < i; j++) {
                        firstSampleOfChunk += chunkSizes[j];
                    }
                    List<Sample> chunk = r11.track2Sample.get(track).subList(CastUtils.l2i(firstSampleOfChunk), CastUtils.l2i(chunkSizes[i] + firstSampleOfChunk));
                    this.chunkList.add(chunk);
                }
            }
        }

        /* synthetic */ InterleaveChunkMdat(DefaultMp4Builder defaultMp4Builder, Movie movie, Map map, long j, InterleaveChunkMdat interleaveChunkMdat) {
            this(movie, map, j);
        }

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
        public void parse(DataSource dataSource, ByteBuffer header, long contentSize, BoxParser boxParser) throws IOException {
        }

        public long getDataOffset() {
            Box box;
            long offset = 16;
            for (Object b = this; b instanceof Box; b = ((Box) b).getParent()) {
                Iterator<Box> it = ((Box) b).getParent().getBoxes().iterator();
                while (it.hasNext() && b != (box = it.next())) {
                    offset += box.getSize();
                }
            }
            return offset;
        }

        @Override // com.coremedia.iso.boxes.Box
        public String getType() {
            return MediaDataBox.TYPE;
        }

        @Override // com.coremedia.iso.boxes.Box
        public long getSize() {
            return this.contentSize + 16;
        }

        private boolean isSmallBox(long contentSize) {
            return 8 + contentSize < 4294967296L;
        }

        @Override // com.coremedia.iso.boxes.Box
        public void getBox(WritableByteChannel writableByteChannel) throws IOException {
            ByteBuffer bb = ByteBuffer.allocate(16);
            long size = getSize();
            if (isSmallBox(size)) {
                IsoTypeWriter.writeUInt32(bb, size);
            } else {
                IsoTypeWriter.writeUInt32(bb, 1L);
            }
            bb.put(IsoFile.fourCCtoBytes(MediaDataBox.TYPE));
            if (isSmallBox(size)) {
                bb.put(new byte[8]);
            } else {
                IsoTypeWriter.writeUInt64(bb, size);
            }
            bb.rewind();
            writableByteChannel.write(bb);
            for (List<Sample> samples : this.chunkList) {
                for (Sample sample : samples) {
                    sample.writeTo(writableByteChannel);
                }
            }
        }
    }
}
