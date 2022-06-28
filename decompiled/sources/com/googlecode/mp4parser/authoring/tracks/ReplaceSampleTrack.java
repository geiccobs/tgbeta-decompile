package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.SampleImpl;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.AbstractList;
import java.util.List;
/* loaded from: classes3.dex */
public class ReplaceSampleTrack extends AbstractTrack {
    Track origTrack;
    private Sample sampleContent;
    private long sampleNumber;
    private List<Sample> samples = new ReplaceASingleEntryList(this, null);

    public ReplaceSampleTrack(Track origTrack, long sampleNumber, ByteBuffer content) {
        super("replace(" + origTrack.getName() + ")");
        this.origTrack = origTrack;
        this.sampleNumber = sampleNumber;
        this.sampleContent = new SampleImpl(content);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.origTrack.close();
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public List<Sample> getSamples() {
        return this.samples;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public SampleDescriptionBox getSampleDescriptionBox() {
        return this.origTrack.getSampleDescriptionBox();
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public synchronized long[] getSampleDurations() {
        return this.origTrack.getSampleDurations();
    }

    @Override // com.googlecode.mp4parser.authoring.AbstractTrack, com.googlecode.mp4parser.authoring.Track
    public List<CompositionTimeToSample.Entry> getCompositionTimeEntries() {
        return this.origTrack.getCompositionTimeEntries();
    }

    @Override // com.googlecode.mp4parser.authoring.AbstractTrack, com.googlecode.mp4parser.authoring.Track
    public synchronized long[] getSyncSamples() {
        return this.origTrack.getSyncSamples();
    }

    @Override // com.googlecode.mp4parser.authoring.AbstractTrack, com.googlecode.mp4parser.authoring.Track
    public List<SampleDependencyTypeBox.Entry> getSampleDependencies() {
        return this.origTrack.getSampleDependencies();
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public TrackMetaData getTrackMetaData() {
        return this.origTrack.getTrackMetaData();
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public String getHandler() {
        return this.origTrack.getHandler();
    }

    @Override // com.googlecode.mp4parser.authoring.AbstractTrack, com.googlecode.mp4parser.authoring.Track
    public SubSampleInformationBox getSubsampleInformationBox() {
        return this.origTrack.getSubsampleInformationBox();
    }

    /* loaded from: classes3.dex */
    private class ReplaceASingleEntryList extends AbstractList<Sample> {
        private ReplaceASingleEntryList() {
            ReplaceSampleTrack.this = r1;
        }

        /* synthetic */ ReplaceASingleEntryList(ReplaceSampleTrack replaceSampleTrack, ReplaceASingleEntryList replaceASingleEntryList) {
            this();
        }

        @Override // java.util.AbstractList, java.util.List
        public Sample get(int index) {
            if (ReplaceSampleTrack.this.sampleNumber == index) {
                return ReplaceSampleTrack.this.sampleContent;
            }
            return ReplaceSampleTrack.this.origTrack.getSamples().get(index);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return ReplaceSampleTrack.this.origTrack.getSamples().size();
        }
    }
}
