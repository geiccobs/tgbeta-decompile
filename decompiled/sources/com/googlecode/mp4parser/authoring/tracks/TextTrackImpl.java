package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.coremedia.iso.boxes.sampleentry.TextSampleEntry;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.SampleImpl;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.boxes.threegpp26245.FontTableBox;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
/* loaded from: classes3.dex */
public class TextTrackImpl extends AbstractTrack {
    TrackMetaData trackMetaData = new TrackMetaData();
    List<Line> subs = new LinkedList();
    SampleDescriptionBox sampleDescriptionBox = new SampleDescriptionBox();

    public List<Line> getSubs() {
        return this.subs;
    }

    public TextTrackImpl() {
        super("subtiles");
        TextSampleEntry tx3g = new TextSampleEntry(TextSampleEntry.TYPE1);
        tx3g.setDataReferenceIndex(1);
        tx3g.setStyleRecord(new TextSampleEntry.StyleRecord());
        tx3g.setBoxRecord(new TextSampleEntry.BoxRecord());
        this.sampleDescriptionBox.addBox(tx3g);
        FontTableBox ftab = new FontTableBox();
        ftab.setEntries(Collections.singletonList(new FontTableBox.FontRecord(1, "Serif")));
        tx3g.addBox(ftab);
        this.trackMetaData.setCreationTime(new Date());
        this.trackMetaData.setModificationTime(new Date());
        this.trackMetaData.setTimescale(1000L);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public List<Sample> getSamples() {
        List<Sample> samples = new LinkedList<>();
        long lastEnd = 0;
        for (Line sub : this.subs) {
            long silentTime = sub.from - lastEnd;
            if (silentTime > 0) {
                samples.add(new SampleImpl(ByteBuffer.wrap(new byte[2])));
            } else if (silentTime < 0) {
                throw new Error("Subtitle display times may not intersect");
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            try {
                dos.writeShort(sub.text.getBytes("UTF-8").length);
                dos.write(sub.text.getBytes("UTF-8"));
                dos.close();
                samples.add(new SampleImpl(ByteBuffer.wrap(baos.toByteArray())));
                lastEnd = sub.to;
            } catch (IOException e) {
                throw new Error("VM is broken. Does not support UTF-8");
            }
        }
        return samples;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public SampleDescriptionBox getSampleDescriptionBox() {
        return this.sampleDescriptionBox;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public long[] getSampleDurations() {
        List<Long> decTimes = new ArrayList<>();
        long lastEnd = 0;
        for (Line sub : this.subs) {
            long silentTime = sub.from - lastEnd;
            if (silentTime > 0) {
                decTimes.add(Long.valueOf(silentTime));
            } else if (silentTime < 0) {
                throw new Error("Subtitle display times may not intersect");
            }
            decTimes.add(Long.valueOf(sub.to - sub.from));
            lastEnd = sub.to;
        }
        long[] decTimesArray = new long[decTimes.size()];
        int index = 0;
        for (Long decTime : decTimes) {
            decTimesArray[index] = decTime.longValue();
            index++;
        }
        return decTimesArray;
    }

    @Override // com.googlecode.mp4parser.authoring.AbstractTrack, com.googlecode.mp4parser.authoring.Track
    public List<CompositionTimeToSample.Entry> getCompositionTimeEntries() {
        return null;
    }

    @Override // com.googlecode.mp4parser.authoring.AbstractTrack, com.googlecode.mp4parser.authoring.Track
    public long[] getSyncSamples() {
        return null;
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
        return "sbtl";
    }

    /* loaded from: classes3.dex */
    public static class Line {
        long from;
        String text;
        long to;

        public Line(long from, long to, String text) {
            this.from = from;
            this.to = to;
            this.text = text;
        }

        public long getFrom() {
            return this.from;
        }

        public String getText() {
            return this.text;
        }

        public long getTo() {
            return this.to;
        }
    }

    @Override // com.googlecode.mp4parser.authoring.AbstractTrack, com.googlecode.mp4parser.authoring.Track
    public SubSampleInformationBox getSubsampleInformationBox() {
        return null;
    }
}
