package com.googlecode.mp4parser.authoring.tracks.mjpeg;

import com.coremedia.iso.Hex;
import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Edit;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.boxes.mp4.ESDescriptorBox;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ESDescriptor;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ObjectDescriptorFactory;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
/* loaded from: classes3.dex */
public class OneJpegPerIframe extends AbstractTrack {
    File[] jpegs;
    long[] sampleDurations;
    SampleDescriptionBox stsd;
    long[] syncSamples;
    TrackMetaData trackMetaData = new TrackMetaData();

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    /* JADX WARN: Multi-variable type inference failed */
    public OneJpegPerIframe(String name, File[] jpegs, Track alignTo) throws IOException {
        super(name);
        long[] jArr;
        long duration;
        File[] fileArr = jpegs;
        this.jpegs = fileArr;
        if (alignTo.getSyncSamples().length != fileArr.length) {
            throw new RuntimeException("Number of sync samples doesn't match the number of stills (" + alignTo.getSyncSamples().length + " vs. " + jpegs.length + ")");
        }
        int i = 0;
        BufferedImage a = ImageIO.read(fileArr[0]);
        this.trackMetaData.setWidth(a.getWidth());
        this.trackMetaData.setHeight(a.getHeight());
        this.trackMetaData.setTimescale(alignTo.getTrackMetaData().getTimescale());
        long[] sampleDurationsToiAlignTo = alignTo.getSampleDurations();
        long[] syncSamples = alignTo.getSyncSamples();
        int currentSyncSample = 1;
        long duration2 = 0;
        this.sampleDurations = new long[syncSamples.length];
        int i2 = 1;
        while (i2 < sampleDurationsToiAlignTo.length) {
            BufferedImage a2 = a;
            long[] sampleDurationsToiAlignTo2 = sampleDurationsToiAlignTo;
            long duration3 = duration2;
            if (currentSyncSample < syncSamples.length && i2 == syncSamples[currentSyncSample]) {
                this.sampleDurations[currentSyncSample - 1] = duration3;
                duration = 0;
                currentSyncSample++;
            } else {
                duration = duration3;
            }
            duration2 = duration + sampleDurationsToiAlignTo2[i2];
            i2++;
            fileArr = jpegs;
            sampleDurationsToiAlignTo = sampleDurationsToiAlignTo2;
            a = a2;
            i = 0;
        }
        this.sampleDurations[jArr.length - 1] = duration2;
        this.stsd = new SampleDescriptionBox();
        VisualSampleEntry mp4v = new VisualSampleEntry(VisualSampleEntry.TYPE1);
        this.stsd.addBox(mp4v);
        ESDescriptorBox esds = new ESDescriptorBox();
        esds.setData(ByteBuffer.wrap(Hex.decodeHex("038080801B000100048080800D6C11000000000A1CB4000A1CB4068080800102")));
        esds.setEsDescriptor((ESDescriptor) ObjectDescriptorFactory.createFrom(-1, ByteBuffer.wrap(Hex.decodeHex("038080801B000100048080800D6C11000000000A1CB4000A1CB4068080800102"))));
        mp4v.addBox(esds);
        this.syncSamples = new long[fileArr.length];
        int i3 = 0;
        while (true) {
            long[] jArr2 = this.syncSamples;
            if (i3 >= jArr2.length) {
                break;
            }
            jArr2[i3] = i3 + 1;
            i3++;
            i = 0;
        }
        double earliestTrackPresentationTime = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        boolean acceptDwell = true;
        boolean acceptEdit = true;
        for (Edit edit : alignTo.getEdits()) {
            BufferedImage a3 = a;
            long[] sampleDurationsToiAlignTo3 = sampleDurationsToiAlignTo;
            long duration4 = duration2;
            VisualSampleEntry mp4v2 = mp4v;
            ESDescriptorBox esds2 = esds;
            if (edit.getMediaTime() == -1 && !acceptDwell) {
                throw new RuntimeException("Cannot accept edit list for processing (1)");
            }
            if (edit.getMediaTime() >= 0 && !acceptEdit) {
                throw new RuntimeException("Cannot accept edit list for processing (2)");
            }
            if (edit.getMediaTime() == -1) {
                earliestTrackPresentationTime += edit.getSegmentDuration();
                sampleDurationsToiAlignTo = sampleDurationsToiAlignTo3;
                mp4v = mp4v2;
                a = a3;
                esds = esds2;
                duration2 = duration4;
                i = 0;
            } else {
                double mediaTime = edit.getMediaTime();
                double timeScale = edit.getTimeScale();
                Double.isNaN(mediaTime);
                Double.isNaN(timeScale);
                earliestTrackPresentationTime -= mediaTime / timeScale;
                acceptEdit = false;
                acceptDwell = false;
                sampleDurationsToiAlignTo = sampleDurationsToiAlignTo3;
                mp4v = mp4v2;
                a = a3;
                esds = esds2;
                duration2 = duration4;
                i = 0;
            }
        }
        if (alignTo.getCompositionTimeEntries() != null && alignTo.getCompositionTimeEntries().size() > 0) {
            long currentTime = 0;
            int[] ptss = Arrays.copyOfRange(CompositionTimeToSample.blowupCompositionTimes(alignTo.getCompositionTimeEntries()), i, 50);
            int j = 0;
            while (j < ptss.length) {
                ESDescriptorBox esds3 = esds;
                int[] ptss2 = ptss;
                ptss2[j] = (int) (ptss[j] + currentTime);
                currentTime += alignTo.getSampleDurations()[j];
                j++;
                ptss = ptss2;
                esds = esds3;
            }
            Arrays.sort(ptss);
            double d = ptss[0];
            double timescale = alignTo.getTrackMetaData().getTimescale();
            Double.isNaN(d);
            Double.isNaN(timescale);
            earliestTrackPresentationTime += d / timescale;
        }
        if (earliestTrackPresentationTime >= FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
            if (earliestTrackPresentationTime > FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
                getEdits().add(new Edit(-1L, getTrackMetaData().getTimescale(), 1.0d, earliestTrackPresentationTime));
                List<Edit> edits = getEdits();
                long timescale2 = getTrackMetaData().getTimescale();
                double duration5 = getDuration();
                double timescale3 = getTrackMetaData().getTimescale();
                Double.isNaN(duration5);
                Double.isNaN(timescale3);
                edits.add(new Edit(0L, timescale2, 1.0d, duration5 / timescale3));
                return;
            }
            return;
        }
        List<Edit> edits2 = getEdits();
        double timescale4 = getTrackMetaData().getTimescale();
        Double.isNaN(timescale4);
        long timescale5 = getTrackMetaData().getTimescale();
        double duration6 = getDuration();
        long duration7 = getTrackMetaData().getTimescale();
        double d2 = duration7;
        Double.isNaN(duration6);
        Double.isNaN(d2);
        edits2.add(new Edit((long) ((-earliestTrackPresentationTime) * timescale4), timescale5, 1.0d, duration6 / d2));
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public SampleDescriptionBox getSampleDescriptionBox() {
        return this.stsd;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public long[] getSampleDurations() {
        return this.sampleDurations;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public TrackMetaData getTrackMetaData() {
        return this.trackMetaData;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public String getHandler() {
        return "vide";
    }

    @Override // com.googlecode.mp4parser.authoring.AbstractTrack, com.googlecode.mp4parser.authoring.Track
    public long[] getSyncSamples() {
        return this.syncSamples;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public List<Sample> getSamples() {
        return new AbstractList<Sample>() { // from class: com.googlecode.mp4parser.authoring.tracks.mjpeg.OneJpegPerIframe.1
            @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
            public int size() {
                return OneJpegPerIframe.this.jpegs.length;
            }

            @Override // java.util.AbstractList, java.util.List
            public Sample get(final int index) {
                return new Sample() { // from class: com.googlecode.mp4parser.authoring.tracks.mjpeg.OneJpegPerIframe.1.1
                    ByteBuffer sample = null;

                    @Override // com.googlecode.mp4parser.authoring.Sample
                    public void writeTo(WritableByteChannel channel) throws IOException {
                        RandomAccessFile raf = new RandomAccessFile(OneJpegPerIframe.this.jpegs[index], "r");
                        raf.getChannel().transferTo(0L, raf.length(), channel);
                        raf.close();
                    }

                    @Override // com.googlecode.mp4parser.authoring.Sample
                    public long getSize() {
                        return OneJpegPerIframe.this.jpegs[index].length();
                    }

                    @Override // com.googlecode.mp4parser.authoring.Sample
                    public ByteBuffer asByteBuffer() {
                        if (this.sample == null) {
                            try {
                                RandomAccessFile raf = new RandomAccessFile(OneJpegPerIframe.this.jpegs[index], "r");
                                this.sample = raf.getChannel().map(FileChannel.MapMode.READ_ONLY, 0L, raf.length());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        return this.sample;
                    }
                };
            }
        };
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
    }
}
