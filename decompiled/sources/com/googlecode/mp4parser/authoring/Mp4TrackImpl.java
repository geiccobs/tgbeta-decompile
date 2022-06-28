package com.googlecode.mp4parser.authoring;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.EditListBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.MovieHeaderBox;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SampleTableBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.coremedia.iso.boxes.fragment.MovieExtendsBox;
import com.coremedia.iso.boxes.fragment.MovieFragmentBox;
import com.coremedia.iso.boxes.fragment.SampleFlags;
import com.coremedia.iso.boxes.fragment.TrackExtendsBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentHeaderBox;
import com.coremedia.iso.boxes.fragment.TrackRunBox;
import com.coremedia.iso.boxes.mdat.SampleList;
import com.googlecode.mp4parser.AbstractContainerBox;
import com.googlecode.mp4parser.BasicContainer;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.GroupEntry;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.SampleGroupDescriptionBox;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.SampleToGroupBox;
import com.googlecode.mp4parser.util.CastUtils;
import com.googlecode.mp4parser.util.Path;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
/* loaded from: classes3.dex */
public class Mp4TrackImpl extends AbstractTrack {
    private List<CompositionTimeToSample.Entry> compositionTimeEntries;
    private long[] decodingTimes;
    IsoFile[] fragments;
    private String handler;
    private List<SampleDependencyTypeBox.Entry> sampleDependencies;
    private SampleDescriptionBox sampleDescriptionBox;
    private List<Sample> samples;
    private SubSampleInformationBox subSampleInformationBox;
    private long[] syncSamples;
    TrackBox trackBox;
    private TrackMetaData trackMetaData;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public Mp4TrackImpl(String name, TrackBox trackBox, IsoFile... fragments) {
        super(name);
        MovieFragmentBox movieFragmentBox;
        Iterator<TrackFragmentBox> it;
        List<TrackFragmentBox> trafs;
        String str;
        List<MovieFragmentBox> movieFragmentBoxes;
        TrackFragmentBox traf;
        Iterator<TrackRunBox> it2;
        Iterator<TrackExtendsBox> it3;
        SampleFlags sampleFlags;
        long j;
        MovieFragmentBox movieFragmentBox2;
        SubSampleInformationBox.SubSampleEntry se;
        Mp4TrackImpl mp4TrackImpl = this;
        TrackBox trackBox2 = trackBox;
        mp4TrackImpl.syncSamples = new long[0];
        mp4TrackImpl.trackMetaData = new TrackMetaData();
        mp4TrackImpl.subSampleInformationBox = null;
        long trackId = trackBox.getTrackHeaderBox().getTrackId();
        mp4TrackImpl.samples = new SampleList(trackBox2, fragments);
        SampleTableBox stbl = trackBox.getMediaBox().getMediaInformationBox().getSampleTableBox();
        mp4TrackImpl.handler = trackBox.getMediaBox().getHandlerBox().getHandlerType();
        List<TimeToSampleBox.Entry> decodingTimeEntries = new ArrayList<>();
        mp4TrackImpl.compositionTimeEntries = new ArrayList();
        mp4TrackImpl.sampleDependencies = new ArrayList();
        decodingTimeEntries.addAll(stbl.getTimeToSampleBox().getEntries());
        if (stbl.getCompositionTimeToSample() != null) {
            mp4TrackImpl.compositionTimeEntries.addAll(stbl.getCompositionTimeToSample().getEntries());
        }
        if (stbl.getSampleDependencyTypeBox() != null) {
            mp4TrackImpl.sampleDependencies.addAll(stbl.getSampleDependencyTypeBox().getEntries());
        }
        if (stbl.getSyncSampleBox() != null) {
            mp4TrackImpl.syncSamples = stbl.getSyncSampleBox().getSampleNumber();
        }
        String str2 = "subs";
        mp4TrackImpl.subSampleInformationBox = (SubSampleInformationBox) Path.getPath((AbstractContainerBox) stbl, str2);
        List<MovieFragmentBox> movieFragmentBoxes2 = new ArrayList<>();
        movieFragmentBoxes2.addAll(((Box) trackBox.getParent()).getParent().getBoxes(MovieFragmentBox.class));
        int length = fragments.length;
        int i = 0;
        while (i < length) {
            List<MovieFragmentBox> movieFragmentBoxes3 = movieFragmentBoxes2;
            IsoFile fragment = fragments[i];
            movieFragmentBoxes3.addAll(fragment.getBoxes(MovieFragmentBox.class));
            i++;
            mp4TrackImpl = this;
            trackBox2 = trackBox;
            movieFragmentBoxes2 = movieFragmentBoxes3;
        }
        mp4TrackImpl.sampleDescriptionBox = stbl.getSampleDescriptionBox();
        List<MovieExtendsBox> movieExtendsBoxes = trackBox.getParent().getBoxes(MovieExtendsBox.class);
        if (movieExtendsBoxes.size() > 0) {
            Iterator<MovieExtendsBox> it4 = movieExtendsBoxes.iterator();
            while (it4.hasNext()) {
                MovieExtendsBox mvex = it4.next();
                List<TrackExtendsBox> trackExtendsBoxes = mvex.getBoxes(TrackExtendsBox.class);
                Iterator<TrackExtendsBox> it5 = trackExtendsBoxes.iterator();
                while (it5.hasNext()) {
                    TrackExtendsBox trex = it5.next();
                    if (trex.getTrackId() == trackId) {
                        List<MovieExtendsBox> movieExtendsBoxes2 = movieExtendsBoxes;
                        List<SubSampleInformationBox> subss = Path.getPaths(((Box) trackBox.getParent()).getParent(), "/moof/traf/subs");
                        if (subss.size() > 0) {
                            mp4TrackImpl.subSampleInformationBox = new SubSampleInformationBox();
                        }
                        List<Long> syncSampleList = new LinkedList<>();
                        long sampleNumber = 1;
                        Iterator<MovieFragmentBox> it6 = movieFragmentBoxes2.iterator();
                        while (it6.hasNext()) {
                            List<SubSampleInformationBox> subss2 = subss;
                            Iterator<MovieExtendsBox> it7 = it4;
                            MovieExtendsBox mvex2 = mvex;
                            List<TrackExtendsBox> trackExtendsBoxes2 = trackExtendsBoxes;
                            MovieFragmentBox movieFragmentBox3 = it6.next();
                            List<TrackFragmentBox> trafs2 = movieFragmentBox3.getBoxes(TrackFragmentBox.class);
                            Iterator<TrackFragmentBox> it8 = trafs2.iterator();
                            while (it8.hasNext()) {
                                TrackFragmentBox traf2 = it8.next();
                                if (traf2.getTrackFragmentHeaderBox().getTrackId() == trackId) {
                                    SubSampleInformationBox subs = (SubSampleInformationBox) Path.getPath((AbstractContainerBox) traf2, str2);
                                    long trackId2 = trackId;
                                    MovieFragmentBox movieFragmentBox4 = movieFragmentBox3;
                                    if (subs == null) {
                                        movieFragmentBox = movieFragmentBox4;
                                        trafs = trafs2;
                                        it = it8;
                                    } else {
                                        trafs = trafs2;
                                        it = it8;
                                        long difFromLastFragment = (sampleNumber - 0) - 1;
                                        for (SubSampleInformationBox.SubSampleEntry subSampleEntry : subs.getEntries()) {
                                            SubSampleInformationBox.SubSampleEntry se2 = new SubSampleInformationBox.SubSampleEntry();
                                            se2.getSubsampleEntries().addAll(subSampleEntry.getSubsampleEntries());
                                            if (difFromLastFragment != 0) {
                                                movieFragmentBox2 = movieFragmentBox4;
                                                se = se2;
                                                se.setSampleDelta(subSampleEntry.getSampleDelta() + difFromLastFragment);
                                                difFromLastFragment = 0;
                                            } else {
                                                movieFragmentBox2 = movieFragmentBox4;
                                                se = se2;
                                                se.setSampleDelta(subSampleEntry.getSampleDelta());
                                            }
                                            mp4TrackImpl.subSampleInformationBox.getEntries().add(se);
                                            movieFragmentBox4 = movieFragmentBox2;
                                        }
                                        movieFragmentBox = movieFragmentBox4;
                                    }
                                    List<TrackRunBox> truns = traf2.getBoxes(TrackRunBox.class);
                                    Iterator<TrackRunBox> it9 = truns.iterator();
                                    while (it9.hasNext()) {
                                        TrackRunBox trun = it9.next();
                                        TrackFragmentHeaderBox tfhd = ((TrackFragmentBox) trun.getParent()).getTrackFragmentHeaderBox();
                                        boolean first = true;
                                        for (TrackRunBox.Entry entry : trun.getEntries()) {
                                            List<TrackRunBox> truns2 = truns;
                                            if (trun.isSampleDurationPresent()) {
                                                if (decodingTimeEntries.size() == 0) {
                                                    it2 = it9;
                                                    str = str2;
                                                    movieFragmentBoxes = movieFragmentBoxes2;
                                                    traf = traf2;
                                                    it3 = it5;
                                                    j = 1;
                                                } else {
                                                    it2 = it9;
                                                    if (decodingTimeEntries.get(decodingTimeEntries.size() - 1).getDelta() != entry.getSampleDuration()) {
                                                        str = str2;
                                                        movieFragmentBoxes = movieFragmentBoxes2;
                                                        traf = traf2;
                                                        it3 = it5;
                                                        j = 1;
                                                    } else {
                                                        TimeToSampleBox.Entry e = decodingTimeEntries.get(decodingTimeEntries.size() - 1);
                                                        str = str2;
                                                        movieFragmentBoxes = movieFragmentBoxes2;
                                                        traf = traf2;
                                                        it3 = it5;
                                                        e.setCount(e.getCount() + 1);
                                                    }
                                                }
                                                decodingTimeEntries.add(new TimeToSampleBox.Entry(j, entry.getSampleDuration()));
                                            } else {
                                                it2 = it9;
                                                str = str2;
                                                movieFragmentBoxes = movieFragmentBoxes2;
                                                traf = traf2;
                                                it3 = it5;
                                                if (tfhd.hasDefaultSampleDuration()) {
                                                    decodingTimeEntries.add(new TimeToSampleBox.Entry(1L, tfhd.getDefaultSampleDuration()));
                                                } else {
                                                    decodingTimeEntries.add(new TimeToSampleBox.Entry(1L, trex.getDefaultSampleDuration()));
                                                }
                                            }
                                            if (trun.isSampleCompositionTimeOffsetPresent()) {
                                                if (mp4TrackImpl.compositionTimeEntries.size() != 0) {
                                                    List<CompositionTimeToSample.Entry> list = mp4TrackImpl.compositionTimeEntries;
                                                    if (list.get(list.size() - 1).getOffset() == entry.getSampleCompositionTimeOffset()) {
                                                        List<CompositionTimeToSample.Entry> list2 = mp4TrackImpl.compositionTimeEntries;
                                                        CompositionTimeToSample.Entry e2 = list2.get(list2.size() - 1);
                                                        e2.setCount(e2.getCount() + 1);
                                                    }
                                                }
                                                mp4TrackImpl.compositionTimeEntries.add(new CompositionTimeToSample.Entry(1, CastUtils.l2i(entry.getSampleCompositionTimeOffset())));
                                            }
                                            if (trun.isSampleFlagsPresent()) {
                                                sampleFlags = entry.getSampleFlags();
                                            } else if (first && trun.isFirstSampleFlagsPresent()) {
                                                sampleFlags = trun.getFirstSampleFlags();
                                            } else if (tfhd.hasDefaultSampleFlags()) {
                                                sampleFlags = tfhd.getDefaultSampleFlags();
                                            } else {
                                                sampleFlags = trex.getDefaultSampleFlags();
                                            }
                                            if (sampleFlags != null && !sampleFlags.isSampleIsDifferenceSample()) {
                                                syncSampleList.add(Long.valueOf(sampleNumber));
                                            }
                                            sampleNumber++;
                                            first = false;
                                            it5 = it3;
                                            truns = truns2;
                                            it9 = it2;
                                            traf2 = traf;
                                            movieFragmentBoxes2 = movieFragmentBoxes;
                                            str2 = str;
                                        }
                                    }
                                    trackId = trackId2;
                                    trafs2 = trafs;
                                    it8 = it;
                                    movieFragmentBox3 = movieFragmentBox;
                                }
                            }
                            subss = subss2;
                            it4 = it7;
                            mvex = mvex2;
                            trackExtendsBoxes = trackExtendsBoxes2;
                        }
                        long[] oldSS = mp4TrackImpl.syncSamples;
                        Iterator<MovieExtendsBox> it10 = it4;
                        long[] jArr = new long[mp4TrackImpl.syncSamples.length + syncSampleList.size()];
                        mp4TrackImpl.syncSamples = jArr;
                        MovieExtendsBox mvex3 = mvex;
                        List<TrackExtendsBox> trackExtendsBoxes3 = trackExtendsBoxes;
                        System.arraycopy(oldSS, 0, jArr, 0, oldSS.length);
                        int i2 = oldSS.length;
                        for (Long syncSampleNumber : syncSampleList) {
                            mp4TrackImpl.syncSamples[i2] = syncSampleNumber.longValue();
                            i2++;
                        }
                        movieExtendsBoxes = movieExtendsBoxes2;
                        it4 = it10;
                        mvex = mvex3;
                        trackExtendsBoxes = trackExtendsBoxes3;
                    }
                }
            }
            new ArrayList();
            new ArrayList();
            for (MovieFragmentBox movieFragmentBox5 : movieFragmentBoxes2) {
                for (TrackFragmentBox traf3 : movieFragmentBox5.getBoxes(TrackFragmentBox.class)) {
                    if (traf3.getTrackFragmentHeaderBox().getTrackId() == trackId) {
                        mp4TrackImpl.sampleGroups = mp4TrackImpl.getSampleGroups(Path.getPaths((Container) traf3, SampleGroupDescriptionBox.TYPE), Path.getPaths((Container) traf3, SampleToGroupBox.TYPE), mp4TrackImpl.sampleGroups);
                    }
                }
            }
        } else {
            mp4TrackImpl.sampleGroups = mp4TrackImpl.getSampleGroups(stbl.getBoxes(SampleGroupDescriptionBox.class), stbl.getBoxes(SampleToGroupBox.class), mp4TrackImpl.sampleGroups);
        }
        mp4TrackImpl.decodingTimes = TimeToSampleBox.blowupTimeToSamples(decodingTimeEntries);
        MediaHeaderBox mdhd = trackBox.getMediaBox().getMediaHeaderBox();
        TrackHeaderBox tkhd = trackBox.getTrackHeaderBox();
        mp4TrackImpl.trackMetaData.setTrackId(tkhd.getTrackId());
        mp4TrackImpl.trackMetaData.setCreationTime(mdhd.getCreationTime());
        mp4TrackImpl.trackMetaData.setLanguage(mdhd.getLanguage());
        mp4TrackImpl.trackMetaData.setModificationTime(mdhd.getModificationTime());
        mp4TrackImpl.trackMetaData.setTimescale(mdhd.getTimescale());
        mp4TrackImpl.trackMetaData.setHeight(tkhd.getHeight());
        mp4TrackImpl.trackMetaData.setWidth(tkhd.getWidth());
        mp4TrackImpl.trackMetaData.setLayer(tkhd.getLayer());
        mp4TrackImpl.trackMetaData.setMatrix(tkhd.getMatrix());
        EditListBox elst = (EditListBox) Path.getPath((AbstractContainerBox) trackBox2, "edts/elst");
        MovieHeaderBox mvhd = (MovieHeaderBox) Path.getPath((AbstractContainerBox) trackBox2, "../mvhd");
        if (elst != null) {
            for (EditListBox.Entry e3 : elst.getEntries()) {
                List<Edit> list3 = mp4TrackImpl.edits;
                long mediaTime = e3.getMediaTime();
                long timescale = mdhd.getTimescale();
                double mediaRate = e3.getMediaRate();
                double segmentDuration = e3.getSegmentDuration();
                double timescale2 = mvhd.getTimescale();
                Double.isNaN(segmentDuration);
                Double.isNaN(timescale2);
                list3.add(new Edit(mediaTime, timescale, mediaRate, segmentDuration / timescale2));
                mp4TrackImpl = this;
            }
        }
    }

    private Map<GroupEntry, long[]> getSampleGroups(List<SampleGroupDescriptionBox> sgdbs, List<SampleToGroupBox> sbgps, Map<GroupEntry, long[]> sampleGroups) {
        for (SampleGroupDescriptionBox sgdb : sgdbs) {
            boolean sampleNum = false;
            Iterator<SampleToGroupBox> it = sbgps.iterator();
            while (true) {
                int i = 0;
                if (!it.hasNext()) {
                    break;
                }
                SampleToGroupBox sbgp = it.next();
                if (sbgp.getGroupingType().equals(sgdb.getGroupEntries().get(0).getType())) {
                    boolean found = true;
                    int sampleNum2 = 0;
                    for (SampleToGroupBox.Entry entry : sbgp.getEntries()) {
                        if (entry.getGroupDescriptionIndex() > 0) {
                            GroupEntry groupEntry = sgdb.getGroupEntries().get(entry.getGroupDescriptionIndex() - 1);
                            long[] samples = sampleGroups.get(groupEntry);
                            if (samples == null) {
                                samples = new long[i];
                            }
                            long[] nuSamples = new long[CastUtils.l2i(entry.getSampleCount()) + samples.length];
                            System.arraycopy(samples, i, nuSamples, i, samples.length);
                            int i2 = 0;
                            while (i2 < entry.getSampleCount()) {
                                nuSamples[samples.length + i2] = sampleNum2 + i2;
                                i2++;
                                found = found;
                            }
                            sampleGroups.put(groupEntry, nuSamples);
                        }
                        sampleNum2 = (int) (sampleNum2 + entry.getSampleCount());
                        found = found;
                        i = 0;
                    }
                    sampleNum = found;
                }
            }
            if (!sampleNum) {
                throw new RuntimeException("Could not find SampleToGroupBox for " + sgdb.getGroupEntries().get(0).getType() + ".");
            }
        }
        return sampleGroups;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        IsoFile[] isoFileArr;
        Container c = this.trackBox.getParent();
        if (c instanceof BasicContainer) {
            ((BasicContainer) c).close();
        }
        for (IsoFile fragment : this.fragments) {
            fragment.close();
        }
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public List<Sample> getSamples() {
        return this.samples;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public synchronized long[] getSampleDurations() {
        return this.decodingTimes;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public SampleDescriptionBox getSampleDescriptionBox() {
        return this.sampleDescriptionBox;
    }

    @Override // com.googlecode.mp4parser.authoring.AbstractTrack, com.googlecode.mp4parser.authoring.Track
    public List<CompositionTimeToSample.Entry> getCompositionTimeEntries() {
        return this.compositionTimeEntries;
    }

    @Override // com.googlecode.mp4parser.authoring.AbstractTrack, com.googlecode.mp4parser.authoring.Track
    public long[] getSyncSamples() {
        if (this.syncSamples.length == this.samples.size()) {
            return null;
        }
        return this.syncSamples;
    }

    @Override // com.googlecode.mp4parser.authoring.AbstractTrack, com.googlecode.mp4parser.authoring.Track
    public List<SampleDependencyTypeBox.Entry> getSampleDependencies() {
        return this.sampleDependencies;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public TrackMetaData getTrackMetaData() {
        return this.trackMetaData;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public String getHandler() {
        return this.handler;
    }

    @Override // com.googlecode.mp4parser.authoring.AbstractTrack, com.googlecode.mp4parser.authoring.Track
    public SubSampleInformationBox getSubsampleInformationBox() {
        return this.subSampleInformationBox;
    }
}
