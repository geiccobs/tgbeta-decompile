package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.googlecode.mp4parser.authoring.Edit;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.GroupEntry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
/* loaded from: classes3.dex */
public class ChangeTimeScaleTrack implements Track {
    private static final Logger LOG = Logger.getLogger(ChangeTimeScaleTrack.class.getName());
    List<CompositionTimeToSample.Entry> ctts;
    long[] decodingTimes;
    Track source;
    long timeScale;

    public ChangeTimeScaleTrack(Track source, long targetTimeScale, long[] syncSamples) {
        this.source = source;
        this.timeScale = targetTimeScale;
        double d = targetTimeScale;
        double timescale = source.getTrackMetaData().getTimescale();
        Double.isNaN(d);
        Double.isNaN(timescale);
        double timeScaleFactor = d / timescale;
        this.ctts = adjustCtts(source.getCompositionTimeEntries(), timeScaleFactor);
        this.decodingTimes = adjustTts(source.getSampleDurations(), timeScaleFactor, syncSamples, getTimes(source, syncSamples, targetTimeScale));
    }

    private static long[] getTimes(Track track, long[] syncSamples, long targetTimeScale) {
        long[] syncSampleTimes = new long[syncSamples.length];
        long currentDuration = 0;
        int currentSyncSampleIndex = 0;
        for (int currentSample = 1; currentSample <= syncSamples[syncSamples.length - 1]; currentSample++) {
            if (currentSample == syncSamples[currentSyncSampleIndex]) {
                syncSampleTimes[currentSyncSampleIndex] = (currentDuration * targetTimeScale) / track.getTrackMetaData().getTimescale();
                currentSyncSampleIndex++;
            }
            currentDuration += track.getSampleDurations()[currentSample - 1];
        }
        return syncSampleTimes;
    }

    static List<CompositionTimeToSample.Entry> adjustCtts(List<CompositionTimeToSample.Entry> source, double timeScaleFactor) {
        if (source != null) {
            List<CompositionTimeToSample.Entry> entries2 = new ArrayList<>(source.size());
            for (CompositionTimeToSample.Entry entry : source) {
                int count = entry.getCount();
                double offset = entry.getOffset();
                Double.isNaN(offset);
                entries2.add(new CompositionTimeToSample.Entry(count, (int) Math.round(offset * timeScaleFactor)));
            }
            return entries2;
        }
        return null;
    }

    static long[] adjustTts(long[] sourceArray, double timeScaleFactor, long[] syncSample, long[] syncSampleTimes) {
        long summedDurations = 0;
        long[] scaledArray = new long[sourceArray.length];
        for (int i = 1; i <= sourceArray.length; i++) {
            long duration = sourceArray[i - 1];
            double d = duration;
            Double.isNaN(d);
            long x = Math.round(d * timeScaleFactor);
            int ssIndex = Arrays.binarySearch(syncSample, i + 1);
            if (ssIndex >= 0 && syncSampleTimes[ssIndex] != summedDurations) {
                long correction = syncSampleTimes[ssIndex] - (summedDurations + x);
                LOG.finest(String.format("Sample %d %d / %d - correct by %d", Integer.valueOf(i), Long.valueOf(summedDurations), Long.valueOf(syncSampleTimes[ssIndex]), Long.valueOf(correction)));
                x += correction;
            }
            summedDurations += x;
            scaledArray[i - 1] = x;
        }
        return scaledArray;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.source.close();
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public SampleDescriptionBox getSampleDescriptionBox() {
        return this.source.getSampleDescriptionBox();
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public long[] getSampleDurations() {
        return this.decodingTimes;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public List<CompositionTimeToSample.Entry> getCompositionTimeEntries() {
        return this.ctts;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public long[] getSyncSamples() {
        return this.source.getSyncSamples();
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public List<SampleDependencyTypeBox.Entry> getSampleDependencies() {
        return this.source.getSampleDependencies();
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public TrackMetaData getTrackMetaData() {
        TrackMetaData trackMetaData = (TrackMetaData) this.source.getTrackMetaData().clone();
        trackMetaData.setTimescale(this.timeScale);
        return trackMetaData;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public String getHandler() {
        return this.source.getHandler();
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public List<Sample> getSamples() {
        return this.source.getSamples();
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public SubSampleInformationBox getSubsampleInformationBox() {
        return this.source.getSubsampleInformationBox();
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public long getDuration() {
        long[] jArr;
        long duration = 0;
        for (long delta : this.decodingTimes) {
            duration += delta;
        }
        return duration;
    }

    public String toString() {
        return "ChangeTimeScaleTrack{source=" + this.source + '}';
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public String getName() {
        return "timeScale(" + this.source.getName() + ")";
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public List<Edit> getEdits() {
        return this.source.getEdits();
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public Map<GroupEntry, long[]> getSampleGroups() {
        return this.source.getSampleGroups();
    }
}
