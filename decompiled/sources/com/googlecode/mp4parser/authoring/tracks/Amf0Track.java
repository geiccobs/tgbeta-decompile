package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.SampleImpl;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.boxes.adobe.ActionMessageFormat0SampleEntryBox;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
/* loaded from: classes3.dex */
public class Amf0Track extends AbstractTrack {
    SortedMap<Long, byte[]> rawSamples;
    private TrackMetaData trackMetaData = new TrackMetaData();

    public Amf0Track(Map<Long, byte[]> rawSamples) {
        super(ActionMessageFormat0SampleEntryBox.TYPE);
        this.rawSamples = new TreeMap<Long, byte[]>() { // from class: com.googlecode.mp4parser.authoring.tracks.Amf0Track.1
        };
        this.rawSamples = new TreeMap(rawSamples);
        this.trackMetaData.setCreationTime(new Date());
        this.trackMetaData.setModificationTime(new Date());
        this.trackMetaData.setTimescale(1000L);
        this.trackMetaData.setLanguage("eng");
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public List<Sample> getSamples() {
        LinkedList<Sample> samples = new LinkedList<>();
        for (byte[] bytes : this.rawSamples.values()) {
            samples.add(new SampleImpl(ByteBuffer.wrap(bytes)));
        }
        return samples;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public SampleDescriptionBox getSampleDescriptionBox() {
        SampleDescriptionBox stsd = new SampleDescriptionBox();
        ActionMessageFormat0SampleEntryBox amf0 = new ActionMessageFormat0SampleEntryBox();
        amf0.setDataReferenceIndex(1);
        stsd.addBox(amf0);
        return stsd;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public long[] getSampleDurations() {
        LinkedList<Long> keys = new LinkedList<>(this.rawSamples.keySet());
        Collections.sort(keys);
        long[] rc = new long[keys.size()];
        for (int i = 0; i < keys.size(); i++) {
            long key = keys.get(i).longValue();
            long delta = key - 0;
            rc[i] = delta;
        }
        return rc;
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
        return "data";
    }

    @Override // com.googlecode.mp4parser.authoring.AbstractTrack, com.googlecode.mp4parser.authoring.Track
    public SubSampleInformationBox getSubsampleInformationBox() {
        return null;
    }
}
