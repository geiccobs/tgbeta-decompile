package com.googlecode.mp4parser.authoring.samples;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.fragment.MovieFragmentBox;
import com.coremedia.iso.boxes.fragment.TrackExtendsBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentHeaderBox;
import com.coremedia.iso.boxes.fragment.TrackRunBox;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.util.CastUtils;
import com.googlecode.mp4parser.util.Path;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/* loaded from: classes3.dex */
public class FragmentedMp4SampleList extends AbstractList<Sample> {
    private List<TrackFragmentBox> allTrafs;
    private int[] firstSamples;
    IsoFile[] fragments;
    private SoftReference<Sample>[] sampleCache;
    Container topLevel;
    TrackBox trackBox;
    TrackExtendsBox trex;
    private Map<TrackRunBox, SoftReference<ByteBuffer>> trunDataCache = new HashMap();
    private int size_ = -1;

    public FragmentedMp4SampleList(long track, Container topLevel, IsoFile... fragments) {
        this.trackBox = null;
        this.trex = null;
        this.topLevel = topLevel;
        this.fragments = fragments;
        List<TrackBox> tbs = Path.getPaths(topLevel, "moov[0]/trak");
        for (TrackBox tb : tbs) {
            if (tb.getTrackHeaderBox().getTrackId() == track) {
                this.trackBox = tb;
            }
        }
        if (this.trackBox == null) {
            throw new RuntimeException("This MP4 does not contain track " + track);
        }
        List<TrackExtendsBox> trexs = Path.getPaths(topLevel, "moov[0]/mvex[0]/trex");
        for (TrackExtendsBox box : trexs) {
            if (box.getTrackId() == this.trackBox.getTrackHeaderBox().getTrackId()) {
                this.trex = box;
            }
        }
        this.sampleCache = (SoftReference[]) Array.newInstance(SoftReference.class, size());
        initAllFragments();
    }

    private List<TrackFragmentBox> initAllFragments() {
        List<TrackFragmentBox> list = this.allTrafs;
        if (list != null) {
            return list;
        }
        List<TrackFragmentBox> trafs = new ArrayList<>();
        for (MovieFragmentBox moof : this.topLevel.getBoxes(MovieFragmentBox.class)) {
            for (TrackFragmentBox trackFragmentBox : moof.getBoxes(TrackFragmentBox.class)) {
                if (trackFragmentBox.getTrackFragmentHeaderBox().getTrackId() == this.trackBox.getTrackHeaderBox().getTrackId()) {
                    trafs.add(trackFragmentBox);
                }
            }
        }
        IsoFile[] isoFileArr = this.fragments;
        if (isoFileArr != null) {
            for (IsoFile fragment : isoFileArr) {
                for (MovieFragmentBox moof2 : fragment.getBoxes(MovieFragmentBox.class)) {
                    for (TrackFragmentBox trackFragmentBox2 : moof2.getBoxes(TrackFragmentBox.class)) {
                        if (trackFragmentBox2.getTrackFragmentHeaderBox().getTrackId() == this.trackBox.getTrackHeaderBox().getTrackId()) {
                            trafs.add(trackFragmentBox2);
                        }
                    }
                }
            }
        }
        this.allTrafs = trafs;
        int firstSample = 1;
        this.firstSamples = new int[trafs.size()];
        for (int i = 0; i < this.allTrafs.size(); i++) {
            this.firstSamples[i] = firstSample;
            firstSample += getTrafSize(this.allTrafs.get(i));
        }
        return trafs;
    }

    private int getTrafSize(TrackFragmentBox traf) {
        List<Box> boxes = traf.getBoxes();
        int size = 0;
        for (int i = 0; i < boxes.size(); i++) {
            Box b = boxes.get(i);
            if (b instanceof TrackRunBox) {
                size += CastUtils.l2i(((TrackRunBox) b).getSampleCount());
            }
        }
        return size;
    }

    @Override // java.util.AbstractList, java.util.List
    public Sample get(int index) {
        long defaultSampleSize;
        ByteBuffer trunData;
        long sampleSize;
        long j;
        Container base;
        ByteBuffer trunData2;
        IOException e;
        ByteBuffer trunData3;
        Sample cachedSample;
        SoftReference<Sample>[] softReferenceArr = this.sampleCache;
        if (softReferenceArr[index] == null || (cachedSample = softReferenceArr[index].get()) == null) {
            int targetIndex = index + 1;
            int j2 = this.firstSamples.length - 1;
            int j3 = j2;
            while (targetIndex - this.firstSamples[j3] < 0) {
                j3--;
            }
            TrackFragmentBox trackFragmentBox = this.allTrafs.get(j3);
            int sampleIndexWithInTraf = targetIndex - this.firstSamples[j3];
            MovieFragmentBox moof = (MovieFragmentBox) trackFragmentBox.getParent();
            int previousTrunsSize = 0;
            for (Box box : trackFragmentBox.getBoxes()) {
                if (box instanceof TrackRunBox) {
                    TrackRunBox trun = (TrackRunBox) box;
                    if (trun.getEntries().size() < sampleIndexWithInTraf - previousTrunsSize) {
                        previousTrunsSize += trun.getEntries().size();
                    } else {
                        List<TrackRunBox.Entry> trackRunEntries = trun.getEntries();
                        TrackFragmentHeaderBox tfhd = trackFragmentBox.getTrackFragmentHeaderBox();
                        boolean sampleSizePresent = trun.isSampleSizePresent();
                        boolean hasDefaultSampleSize = tfhd.hasDefaultSampleSize();
                        if (sampleSizePresent) {
                            defaultSampleSize = 0;
                        } else if (hasDefaultSampleSize) {
                            defaultSampleSize = tfhd.getDefaultSampleSize();
                        } else {
                            TrackExtendsBox trackExtendsBox = this.trex;
                            if (trackExtendsBox == null) {
                                throw new RuntimeException("File doesn't contain trex box but track fragments aren't fully self contained. Cannot determine sample size.");
                            }
                            defaultSampleSize = trackExtendsBox.getDefaultSampleSize();
                        }
                        SoftReference<ByteBuffer> trunDataRef = this.trunDataCache.get(trun);
                        ByteBuffer trunData4 = trunDataRef != null ? trunDataRef.get() : null;
                        if (trunData4 != null) {
                            trunData = trunData4;
                        } else {
                            long offset = 0;
                            if (tfhd.hasBaseDataOffset()) {
                                offset = 0 + tfhd.getBaseDataOffset();
                                Container base2 = moof.getParent();
                                base = base2;
                            } else {
                                base = moof;
                            }
                            if (!trun.isDataOffsetPresent()) {
                                trunData2 = trunData4;
                            } else {
                                trunData2 = trunData4;
                                offset += trun.getDataOffset();
                            }
                            Iterator<TrackRunBox.Entry> it = trackRunEntries.iterator();
                            int size = 0;
                            while (it.hasNext()) {
                                Container base3 = base;
                                TrackRunBox.Entry e2 = it.next();
                                if (sampleSizePresent) {
                                    size = (int) (size + e2.getSampleSize());
                                    base = base3;
                                    it = it;
                                } else {
                                    size = (int) (size + defaultSampleSize);
                                    base = base3;
                                    it = it;
                                }
                            }
                            try {
                                trunData3 = base.getByteBuffer(offset, size);
                                try {
                                } catch (IOException e3) {
                                    e = e3;
                                }
                            } catch (IOException e4) {
                                e = e4;
                            }
                            try {
                                this.trunDataCache.put(trun, new SoftReference<>(trunData3));
                                trunData = trunData3;
                            } catch (IOException e5) {
                                e = e5;
                                throw new RuntimeException(e);
                            }
                        }
                        int offset2 = 0;
                        int i = 0;
                        while (i < sampleIndexWithInTraf - previousTrunsSize) {
                            int targetIndex2 = targetIndex;
                            List<TrackRunBox.Entry> trackRunEntries2 = trackRunEntries;
                            if (sampleSizePresent) {
                                j = offset2 + trackRunEntries2.get(i).getSampleSize();
                            } else {
                                j = offset2 + defaultSampleSize;
                            }
                            offset2 = (int) j;
                            i++;
                            trackRunEntries = trackRunEntries2;
                            targetIndex = targetIndex2;
                        }
                        if (sampleSizePresent) {
                            long sampleSize2 = trackRunEntries.get(sampleIndexWithInTraf - previousTrunsSize).getSampleSize();
                            sampleSize = sampleSize2;
                        } else {
                            long sampleSize3 = defaultSampleSize;
                            sampleSize = sampleSize3;
                        }
                        final ByteBuffer finalTrunData = trunData;
                        final int finalOffset = offset2;
                        final long j4 = sampleSize;
                        Sample sample = new Sample() { // from class: com.googlecode.mp4parser.authoring.samples.FragmentedMp4SampleList.1
                            @Override // com.googlecode.mp4parser.authoring.Sample
                            public void writeTo(WritableByteChannel channel) throws IOException {
                                channel.write(asByteBuffer());
                            }

                            @Override // com.googlecode.mp4parser.authoring.Sample
                            public long getSize() {
                                return j4;
                            }

                            @Override // com.googlecode.mp4parser.authoring.Sample
                            public ByteBuffer asByteBuffer() {
                                return (ByteBuffer) ((ByteBuffer) finalTrunData.position(finalOffset)).slice().limit(CastUtils.l2i(j4));
                            }
                        };
                        this.sampleCache[index] = new SoftReference<>(sample);
                        return sample;
                    }
                }
            }
            throw new RuntimeException("Couldn't find sample in the traf I was looking");
        }
        return cachedSample;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        IsoFile[] isoFileArr;
        int i = this.size_;
        if (i != -1) {
            return i;
        }
        int i2 = 0;
        for (MovieFragmentBox moof : this.topLevel.getBoxes(MovieFragmentBox.class)) {
            for (TrackFragmentBox trackFragmentBox : moof.getBoxes(TrackFragmentBox.class)) {
                if (trackFragmentBox.getTrackFragmentHeaderBox().getTrackId() == this.trackBox.getTrackHeaderBox().getTrackId()) {
                    i2 = (int) (i2 + ((TrackRunBox) trackFragmentBox.getBoxes(TrackRunBox.class).get(0)).getSampleCount());
                }
            }
        }
        for (IsoFile fragment : this.fragments) {
            for (MovieFragmentBox moof2 : fragment.getBoxes(MovieFragmentBox.class)) {
                for (TrackFragmentBox trackFragmentBox2 : moof2.getBoxes(TrackFragmentBox.class)) {
                    if (trackFragmentBox2.getTrackFragmentHeaderBox().getTrackId() == this.trackBox.getTrackHeaderBox().getTrackId()) {
                        i2 = (int) (i2 + ((TrackRunBox) trackFragmentBox2.getBoxes(TrackRunBox.class).get(0)).getSampleCount());
                    }
                }
            }
        }
        this.size_ = i2;
        return i2;
    }
}
