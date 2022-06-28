package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
/* loaded from: classes3.dex */
public class CroppedTrack extends AbstractTrack {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private int fromSample;
    Track origTrack;
    private int toSample;

    public CroppedTrack(Track origTrack, long fromSample, long toSample) {
        super("crop(" + origTrack.getName() + ")");
        this.origTrack = origTrack;
        if (fromSample > 2147483647L) {
            throw new AssertionError();
        }
        if (toSample > 2147483647L) {
            throw new AssertionError();
        }
        this.fromSample = (int) fromSample;
        this.toSample = (int) toSample;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.origTrack.close();
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public List<Sample> getSamples() {
        return this.origTrack.getSamples().subList(this.fromSample, this.toSample);
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public SampleDescriptionBox getSampleDescriptionBox() {
        return this.origTrack.getSampleDescriptionBox();
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public synchronized long[] getSampleDurations() {
        long[] decodingTimes;
        decodingTimes = new long[this.toSample - this.fromSample];
        System.arraycopy(this.origTrack.getSampleDurations(), this.fromSample, decodingTimes, 0, decodingTimes.length);
        return decodingTimes;
    }

    static List<TimeToSampleBox.Entry> getDecodingTimeEntries(List<TimeToSampleBox.Entry> origSamples, long fromSample, long toSample) {
        TimeToSampleBox.Entry currentEntry;
        if (origSamples != null && !origSamples.isEmpty()) {
            long current = 0;
            ListIterator<TimeToSampleBox.Entry> e = origSamples.listIterator();
            LinkedList<TimeToSampleBox.Entry> nuList = new LinkedList<>();
            while (true) {
                TimeToSampleBox.Entry next = e.next();
                currentEntry = next;
                if (next.getCount() + current > fromSample) {
                    break;
                }
                current += currentEntry.getCount();
            }
            if (currentEntry.getCount() + current >= toSample) {
                nuList.add(new TimeToSampleBox.Entry(toSample - fromSample, currentEntry.getDelta()));
                return nuList;
            }
            nuList.add(new TimeToSampleBox.Entry((currentEntry.getCount() + current) - fromSample, currentEntry.getDelta()));
            long count = currentEntry.getCount();
            while (true) {
                current += count;
                if (!e.hasNext()) {
                    break;
                }
                TimeToSampleBox.Entry next2 = e.next();
                currentEntry = next2;
                if (next2.getCount() + current >= toSample) {
                    break;
                }
                nuList.add(currentEntry);
                count = currentEntry.getCount();
            }
            nuList.add(new TimeToSampleBox.Entry(toSample - current, currentEntry.getDelta()));
            return nuList;
        }
        return null;
    }

    @Override // com.googlecode.mp4parser.authoring.AbstractTrack, com.googlecode.mp4parser.authoring.Track
    public List<CompositionTimeToSample.Entry> getCompositionTimeEntries() {
        return getCompositionTimeEntries(this.origTrack.getCompositionTimeEntries(), this.fromSample, this.toSample);
    }

    static List<CompositionTimeToSample.Entry> getCompositionTimeEntries(List<CompositionTimeToSample.Entry> origSamples, long fromSample, long toSample) {
        CompositionTimeToSample.Entry currentEntry;
        if (origSamples != null && !origSamples.isEmpty()) {
            long current = 0;
            ListIterator<CompositionTimeToSample.Entry> e = origSamples.listIterator();
            ArrayList<CompositionTimeToSample.Entry> nuList = new ArrayList<>();
            while (true) {
                CompositionTimeToSample.Entry next = e.next();
                currentEntry = next;
                if (next.getCount() + current > fromSample) {
                    break;
                }
                current += currentEntry.getCount();
            }
            if (currentEntry.getCount() + current >= toSample) {
                nuList.add(new CompositionTimeToSample.Entry((int) (toSample - fromSample), currentEntry.getOffset()));
                return nuList;
            }
            nuList.add(new CompositionTimeToSample.Entry((int) ((currentEntry.getCount() + current) - fromSample), currentEntry.getOffset()));
            int count = currentEntry.getCount();
            while (true) {
                current += count;
                if (!e.hasNext()) {
                    break;
                }
                CompositionTimeToSample.Entry next2 = e.next();
                currentEntry = next2;
                if (next2.getCount() + current >= toSample) {
                    break;
                }
                nuList.add(currentEntry);
                count = currentEntry.getCount();
            }
            nuList.add(new CompositionTimeToSample.Entry((int) (toSample - current), currentEntry.getOffset()));
            return nuList;
        }
        return null;
    }

    @Override // com.googlecode.mp4parser.authoring.AbstractTrack, com.googlecode.mp4parser.authoring.Track
    public synchronized long[] getSyncSamples() {
        if (this.origTrack.getSyncSamples() != null) {
            long[] origSyncSamples = this.origTrack.getSyncSamples();
            int i = 0;
            int j = origSyncSamples.length;
            while (i < origSyncSamples.length && origSyncSamples[i] < this.fromSample) {
                i++;
            }
            while (j > 0 && this.toSample < origSyncSamples[j - 1]) {
                j--;
            }
            long[] syncSampleArray = Arrays.copyOfRange(this.origTrack.getSyncSamples(), i, j);
            for (int k = 0; k < syncSampleArray.length; k++) {
                syncSampleArray[k] = syncSampleArray[k] - this.fromSample;
            }
            return syncSampleArray;
        }
        return null;
    }

    @Override // com.googlecode.mp4parser.authoring.AbstractTrack, com.googlecode.mp4parser.authoring.Track
    public List<SampleDependencyTypeBox.Entry> getSampleDependencies() {
        if (this.origTrack.getSampleDependencies() != null && !this.origTrack.getSampleDependencies().isEmpty()) {
            return this.origTrack.getSampleDependencies().subList(this.fromSample, this.toSample);
        }
        return null;
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
}
