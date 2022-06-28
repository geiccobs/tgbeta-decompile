package com.google.android.exoplayer2.source.dash.manifest;

import android.net.Uri;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.offline.FilterableManifest;
import com.google.android.exoplayer2.offline.StreamKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
/* loaded from: classes3.dex */
public class DashManifest implements FilterableManifest<DashManifest> {
    public final long availabilityStartTimeMs;
    public final long durationMs;
    public final boolean dynamic;
    public final Uri location;
    public final long minBufferTimeMs;
    public final long minUpdatePeriodMs;
    private final List<Period> periods;
    public final ProgramInformation programInformation;
    public final long publishTimeMs;
    public final long suggestedPresentationDelayMs;
    public final long timeShiftBufferDepthMs;
    public final UtcTimingElement utcTiming;

    @Deprecated
    public DashManifest(long availabilityStartTimeMs, long durationMs, long minBufferTimeMs, boolean dynamic, long minUpdatePeriodMs, long timeShiftBufferDepthMs, long suggestedPresentationDelayMs, long publishTimeMs, UtcTimingElement utcTiming, Uri location, List<Period> periods) {
        this(availabilityStartTimeMs, durationMs, minBufferTimeMs, dynamic, minUpdatePeriodMs, timeShiftBufferDepthMs, suggestedPresentationDelayMs, publishTimeMs, null, utcTiming, location, periods);
    }

    public DashManifest(long availabilityStartTimeMs, long durationMs, long minBufferTimeMs, boolean dynamic, long minUpdatePeriodMs, long timeShiftBufferDepthMs, long suggestedPresentationDelayMs, long publishTimeMs, ProgramInformation programInformation, UtcTimingElement utcTiming, Uri location, List<Period> periods) {
        this.availabilityStartTimeMs = availabilityStartTimeMs;
        this.durationMs = durationMs;
        this.minBufferTimeMs = minBufferTimeMs;
        this.dynamic = dynamic;
        this.minUpdatePeriodMs = minUpdatePeriodMs;
        this.timeShiftBufferDepthMs = timeShiftBufferDepthMs;
        this.suggestedPresentationDelayMs = suggestedPresentationDelayMs;
        this.publishTimeMs = publishTimeMs;
        this.programInformation = programInformation;
        this.utcTiming = utcTiming;
        this.location = location;
        this.periods = periods == null ? Collections.emptyList() : periods;
    }

    public final int getPeriodCount() {
        return this.periods.size();
    }

    public final Period getPeriod(int index) {
        return this.periods.get(index);
    }

    public final long getPeriodDurationMs(int index) {
        if (index == this.periods.size() - 1) {
            long j = this.durationMs;
            return j == C.TIME_UNSET ? C.TIME_UNSET : j - this.periods.get(index).startMs;
        }
        return this.periods.get(index + 1).startMs - this.periods.get(index).startMs;
    }

    public final long getPeriodDurationUs(int index) {
        return C.msToUs(getPeriodDurationMs(index));
    }

    @Override // com.google.android.exoplayer2.offline.FilterableManifest
    public final DashManifest copy(List<StreamKey> streamKeys) {
        long newDuration;
        LinkedList<StreamKey> keys = new LinkedList<>(streamKeys);
        Collections.sort(keys);
        keys.add(new StreamKey(-1, -1, -1));
        ArrayList<Period> copyPeriods = new ArrayList<>();
        int periodIndex = 0;
        long shiftMs = 0;
        while (true) {
            int periodCount = getPeriodCount();
            newDuration = C.TIME_UNSET;
            if (periodIndex >= periodCount) {
                break;
            }
            if (keys.peek().periodIndex != periodIndex) {
                long periodDurationMs = getPeriodDurationMs(periodIndex);
                if (periodDurationMs != C.TIME_UNSET) {
                    shiftMs += periodDurationMs;
                }
            } else {
                Period period = getPeriod(periodIndex);
                ArrayList<AdaptationSet> copyAdaptationSets = copyAdaptationSets(period.adaptationSets, keys);
                Period copiedPeriod = new Period(period.id, period.startMs - shiftMs, copyAdaptationSets, period.eventStreams);
                copyPeriods.add(copiedPeriod);
            }
            periodIndex++;
        }
        long j = this.durationMs;
        if (j != C.TIME_UNSET) {
            newDuration = j - shiftMs;
        }
        return new DashManifest(this.availabilityStartTimeMs, newDuration, this.minBufferTimeMs, this.dynamic, this.minUpdatePeriodMs, this.timeShiftBufferDepthMs, this.suggestedPresentationDelayMs, this.publishTimeMs, this.programInformation, this.utcTiming, this.location, copyPeriods);
    }

    private static ArrayList<AdaptationSet> copyAdaptationSets(List<AdaptationSet> adaptationSets, LinkedList<StreamKey> keys) {
        StreamKey key = keys.poll();
        int periodIndex = key.periodIndex;
        ArrayList<AdaptationSet> copyAdaptationSets = new ArrayList<>();
        while (true) {
            int adaptationSetIndex = key.groupIndex;
            AdaptationSet adaptationSet = adaptationSets.get(adaptationSetIndex);
            List<Representation> representations = adaptationSet.representations;
            ArrayList<Representation> copyRepresentations = new ArrayList<>();
            do {
                Representation representation = representations.get(key.trackIndex);
                copyRepresentations.add(representation);
                key = keys.poll();
                if (key.periodIndex != periodIndex) {
                    break;
                }
            } while (key.groupIndex == adaptationSetIndex);
            copyAdaptationSets.add(new AdaptationSet(adaptationSet.id, adaptationSet.type, copyRepresentations, adaptationSet.accessibilityDescriptors, adaptationSet.essentialProperties, adaptationSet.supplementalProperties));
            if (key.periodIndex != periodIndex) {
                keys.addFirst(key);
                return copyAdaptationSets;
            }
        }
    }
}
