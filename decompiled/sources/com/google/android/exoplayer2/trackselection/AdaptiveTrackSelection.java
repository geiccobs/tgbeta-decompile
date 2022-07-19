package com.google.android.exoplayer2.trackselection;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunkIterator;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.Util;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class AdaptiveTrackSelection extends BaseTrackSelection {
    private final BandwidthProvider bandwidthProvider;
    private final float bufferedFractionToLiveEdgeForQualityIncrease;
    private final Clock clock;
    private long lastBufferEvaluationMs;
    private final long maxDurationForQualityDecreaseUs;
    private final long minDurationForQualityIncreaseUs;
    private final long minDurationToRetainAfterDiscardUs;
    private final long minTimeBetweenBufferReevaluationMs;
    private float playbackSpeed;
    private int reason;
    private int selectedIndex;

    /* loaded from: classes.dex */
    public interface BandwidthProvider {
        long getAllocatedBandwidth();
    }

    @Override // com.google.android.exoplayer2.trackselection.TrackSelection
    public Object getSelectionData() {
        return null;
    }

    /* loaded from: classes.dex */
    public static class Factory implements TrackSelection.Factory {
        private final float bandwidthFraction;
        private final BandwidthMeter bandwidthMeter;
        private final float bufferedFractionToLiveEdgeForQualityIncrease;
        private final Clock clock;
        private final int maxDurationForQualityDecreaseMs;
        private final int minDurationForQualityIncreaseMs;
        private final int minDurationToRetainAfterDiscardMs;
        private final long minTimeBetweenBufferReevaluationMs;

        @Deprecated
        public Factory(BandwidthMeter bandwidthMeter) {
            this(bandwidthMeter, 10000, 25000, 25000, 0.7f, 0.75f, 2000L, Clock.DEFAULT);
        }

        @Deprecated
        public Factory(BandwidthMeter bandwidthMeter, int i, int i2, int i3, float f, float f2, long j, Clock clock) {
            this.bandwidthMeter = bandwidthMeter;
            this.minDurationForQualityIncreaseMs = i;
            this.maxDurationForQualityDecreaseMs = i2;
            this.minDurationToRetainAfterDiscardMs = i3;
            this.bandwidthFraction = f;
            this.bufferedFractionToLiveEdgeForQualityIncrease = f2;
            this.minTimeBetweenBufferReevaluationMs = j;
            this.clock = clock;
        }

        @Override // com.google.android.exoplayer2.trackselection.TrackSelection.Factory
        public final TrackSelection[] createTrackSelections(TrackSelection.Definition[] definitionArr, BandwidthMeter bandwidthMeter) {
            BandwidthMeter bandwidthMeter2 = this.bandwidthMeter;
            if (bandwidthMeter2 != null) {
                bandwidthMeter = bandwidthMeter2;
            }
            TrackSelection[] trackSelectionArr = new TrackSelection[definitionArr.length];
            int i = 0;
            for (int i2 = 0; i2 < definitionArr.length; i2++) {
                TrackSelection.Definition definition = definitionArr[i2];
                if (definition != null) {
                    int[] iArr = definition.tracks;
                    if (iArr.length == 1) {
                        trackSelectionArr[i2] = new FixedTrackSelection(definition.group, iArr[0], definition.reason, definition.data);
                        int i3 = definition.group.getFormat(definition.tracks[0]).bitrate;
                        if (i3 != -1) {
                            i += i3;
                        }
                    }
                }
            }
            ArrayList arrayList = new ArrayList();
            for (int i4 = 0; i4 < definitionArr.length; i4++) {
                TrackSelection.Definition definition2 = definitionArr[i4];
                if (definition2 != null) {
                    int[] iArr2 = definition2.tracks;
                    if (iArr2.length > 1) {
                        AdaptiveTrackSelection createAdaptiveTrackSelection = createAdaptiveTrackSelection(definition2.group, bandwidthMeter, iArr2, i);
                        arrayList.add(createAdaptiveTrackSelection);
                        trackSelectionArr[i4] = createAdaptiveTrackSelection;
                    }
                }
            }
            if (arrayList.size() > 1) {
                long[][] jArr = new long[arrayList.size()];
                for (int i5 = 0; i5 < arrayList.size(); i5++) {
                    AdaptiveTrackSelection adaptiveTrackSelection = (AdaptiveTrackSelection) arrayList.get(i5);
                    jArr[i5] = new long[adaptiveTrackSelection.length()];
                    for (int i6 = 0; i6 < adaptiveTrackSelection.length(); i6++) {
                        jArr[i5][i6] = adaptiveTrackSelection.getFormat((adaptiveTrackSelection.length() - i6) - 1).bitrate;
                    }
                }
                long[][][] allocationCheckpoints = AdaptiveTrackSelection.getAllocationCheckpoints(jArr);
                for (int i7 = 0; i7 < arrayList.size(); i7++) {
                    ((AdaptiveTrackSelection) arrayList.get(i7)).experimental_setBandwidthAllocationCheckpoints(allocationCheckpoints[i7]);
                }
            }
            return trackSelectionArr;
        }

        protected AdaptiveTrackSelection createAdaptiveTrackSelection(TrackGroup trackGroup, BandwidthMeter bandwidthMeter, int[] iArr, int i) {
            return new AdaptiveTrackSelection(trackGroup, iArr, new DefaultBandwidthProvider(bandwidthMeter, this.bandwidthFraction, i), this.minDurationForQualityIncreaseMs, this.maxDurationForQualityDecreaseMs, this.minDurationToRetainAfterDiscardMs, this.bufferedFractionToLiveEdgeForQualityIncrease, this.minTimeBetweenBufferReevaluationMs, this.clock);
        }
    }

    private AdaptiveTrackSelection(TrackGroup trackGroup, int[] iArr, BandwidthProvider bandwidthProvider, long j, long j2, long j3, float f, long j4, Clock clock) {
        super(trackGroup, iArr);
        this.bandwidthProvider = bandwidthProvider;
        this.minDurationForQualityIncreaseUs = j * 1000;
        this.maxDurationForQualityDecreaseUs = j2 * 1000;
        this.minDurationToRetainAfterDiscardUs = j3 * 1000;
        this.bufferedFractionToLiveEdgeForQualityIncrease = f;
        this.minTimeBetweenBufferReevaluationMs = j4;
        this.clock = clock;
        this.playbackSpeed = 1.0f;
        this.reason = 0;
        this.lastBufferEvaluationMs = -9223372036854775807L;
    }

    public void experimental_setBandwidthAllocationCheckpoints(long[][] jArr) {
        ((DefaultBandwidthProvider) this.bandwidthProvider).experimental_setBandwidthAllocationCheckpoints(jArr);
    }

    @Override // com.google.android.exoplayer2.trackselection.BaseTrackSelection, com.google.android.exoplayer2.trackselection.TrackSelection
    public void enable() {
        this.lastBufferEvaluationMs = -9223372036854775807L;
    }

    @Override // com.google.android.exoplayer2.trackselection.BaseTrackSelection, com.google.android.exoplayer2.trackselection.TrackSelection
    public void onPlaybackSpeed(float f) {
        this.playbackSpeed = f;
    }

    @Override // com.google.android.exoplayer2.trackselection.TrackSelection
    public void updateSelectedTrack(long j, long j2, long j3, List<? extends MediaChunk> list, MediaChunkIterator[] mediaChunkIteratorArr) {
        long elapsedRealtime = this.clock.elapsedRealtime();
        if (this.reason == 0) {
            this.reason = 1;
            this.selectedIndex = determineIdealSelectedIndex(elapsedRealtime);
            return;
        }
        int i = this.selectedIndex;
        int determineIdealSelectedIndex = determineIdealSelectedIndex(elapsedRealtime);
        this.selectedIndex = determineIdealSelectedIndex;
        if (determineIdealSelectedIndex == i) {
            return;
        }
        if (!isBlacklisted(i, elapsedRealtime)) {
            Format format = getFormat(i);
            Format format2 = getFormat(this.selectedIndex);
            if (format2.bitrate > format.bitrate && j2 < minDurationForQualityIncreaseUs(j3)) {
                this.selectedIndex = i;
            } else if (format2.bitrate < format.bitrate && j2 >= this.maxDurationForQualityDecreaseUs) {
                this.selectedIndex = i;
            }
        }
        if (this.selectedIndex == i) {
            return;
        }
        this.reason = 3;
    }

    @Override // com.google.android.exoplayer2.trackselection.TrackSelection
    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    @Override // com.google.android.exoplayer2.trackselection.TrackSelection
    public int getSelectionReason() {
        return this.reason;
    }

    @Override // com.google.android.exoplayer2.trackselection.BaseTrackSelection, com.google.android.exoplayer2.trackselection.TrackSelection
    public int evaluateQueueSize(long j, List<? extends MediaChunk> list) {
        int i;
        int i2;
        long elapsedRealtime = this.clock.elapsedRealtime();
        if (!shouldEvaluateQueueSize(elapsedRealtime)) {
            return list.size();
        }
        this.lastBufferEvaluationMs = elapsedRealtime;
        if (list.isEmpty()) {
            return 0;
        }
        int size = list.size();
        long playoutDurationForMediaDuration = Util.getPlayoutDurationForMediaDuration(list.get(size - 1).startTimeUs - j, this.playbackSpeed);
        long minDurationToRetainAfterDiscardUs = getMinDurationToRetainAfterDiscardUs();
        if (playoutDurationForMediaDuration < minDurationToRetainAfterDiscardUs) {
            return size;
        }
        Format format = getFormat(determineIdealSelectedIndex(elapsedRealtime));
        for (int i3 = 0; i3 < size; i3++) {
            MediaChunk mediaChunk = list.get(i3);
            Format format2 = mediaChunk.trackFormat;
            if (Util.getPlayoutDurationForMediaDuration(mediaChunk.startTimeUs - j, this.playbackSpeed) >= minDurationToRetainAfterDiscardUs && format2.bitrate < format.bitrate && (i = format2.height) != -1 && i < 720 && (i2 = format2.width) != -1 && i2 < 1280 && i < format.height) {
                return i3;
            }
        }
        return size;
    }

    protected boolean canSelectFormat(Format format, int i, float f, long j) {
        return ((long) Math.round(((float) i) * f)) <= j;
    }

    protected boolean shouldEvaluateQueueSize(long j) {
        long j2 = this.lastBufferEvaluationMs;
        return j2 == -9223372036854775807L || j - j2 >= this.minTimeBetweenBufferReevaluationMs;
    }

    protected long getMinDurationToRetainAfterDiscardUs() {
        return this.minDurationToRetainAfterDiscardUs;
    }

    private int determineIdealSelectedIndex(long j) {
        long allocatedBandwidth = this.bandwidthProvider.getAllocatedBandwidth();
        int i = 0;
        for (int i2 = 0; i2 < this.length; i2++) {
            if (j == Long.MIN_VALUE || !isBlacklisted(i2, j)) {
                Format format = getFormat(i2);
                if (canSelectFormat(format, format.bitrate, this.playbackSpeed, allocatedBandwidth)) {
                    return i2;
                }
                i = i2;
            }
        }
        return i;
    }

    private long minDurationForQualityIncreaseUs(long j) {
        if (j != -9223372036854775807L && j <= this.minDurationForQualityIncreaseUs) {
            return ((float) j) * this.bufferedFractionToLiveEdgeForQualityIncrease;
        }
        return this.minDurationForQualityIncreaseUs;
    }

    /* loaded from: classes.dex */
    public static final class DefaultBandwidthProvider implements BandwidthProvider {
        private long[][] allocationCheckpoints;
        private final float bandwidthFraction;
        private final BandwidthMeter bandwidthMeter;
        private final long reservedBandwidth;

        DefaultBandwidthProvider(BandwidthMeter bandwidthMeter, float f, long j) {
            this.bandwidthMeter = bandwidthMeter;
            this.bandwidthFraction = f;
            this.reservedBandwidth = j;
        }

        @Override // com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection.BandwidthProvider
        public long getAllocatedBandwidth() {
            long[][] jArr;
            long max = Math.max(0L, (((float) this.bandwidthMeter.getBitrateEstimate()) * this.bandwidthFraction) - this.reservedBandwidth);
            if (this.allocationCheckpoints == null) {
                return max;
            }
            int i = 1;
            while (true) {
                jArr = this.allocationCheckpoints;
                if (i >= jArr.length - 1 || jArr[i][0] >= max) {
                    break;
                }
                i++;
            }
            long[] jArr2 = jArr[i - 1];
            long[] jArr3 = jArr[i];
            return jArr2[1] + ((((float) (max - jArr2[0])) / ((float) (jArr3[0] - jArr2[0]))) * ((float) (jArr3[1] - jArr2[1])));
        }

        void experimental_setBandwidthAllocationCheckpoints(long[][] jArr) {
            Assertions.checkArgument(jArr.length >= 2);
            this.allocationCheckpoints = jArr;
        }
    }

    public static long[][][] getAllocationCheckpoints(long[][] jArr) {
        int i;
        double[][] logArrayValues = getLogArrayValues(jArr);
        double[][] switchPoints = getSwitchPoints(logArrayValues);
        int countArrayElements = countArrayElements(switchPoints) + 3;
        long[][][] jArr2 = (long[][][]) Array.newInstance(long.class, logArrayValues.length, countArrayElements, 2);
        int[] iArr = new int[logArrayValues.length];
        setCheckpointValues(jArr2, 1, jArr, iArr);
        int i2 = 2;
        while (true) {
            i = countArrayElements - 1;
            if (i2 >= i) {
                break;
            }
            double d = Double.MAX_VALUE;
            int i3 = 0;
            for (int i4 = 0; i4 < logArrayValues.length; i4++) {
                if (iArr[i4] + 1 != logArrayValues[i4].length) {
                    double d2 = switchPoints[i4][iArr[i4]];
                    if (d2 < d) {
                        i3 = i4;
                        d = d2;
                    }
                }
            }
            iArr[i3] = iArr[i3] + 1;
            setCheckpointValues(jArr2, i2, jArr, iArr);
            i2++;
        }
        for (long[][] jArr3 : jArr2) {
            int i5 = countArrayElements - 2;
            jArr3[i][0] = jArr3[i5][0] * 2;
            jArr3[i][1] = jArr3[i5][1] * 2;
        }
        return jArr2;
    }

    private static double[][] getLogArrayValues(long[][] jArr) {
        double[][] dArr = new double[jArr.length];
        for (int i = 0; i < jArr.length; i++) {
            dArr[i] = new double[jArr[i].length];
            for (int i2 = 0; i2 < jArr[i].length; i2++) {
                dArr[i][i2] = jArr[i][i2] == -1 ? 0.0d : Math.log(jArr[i][i2]);
            }
        }
        return dArr;
    }

    private static double[][] getSwitchPoints(double[][] dArr) {
        double[][] dArr2 = new double[dArr.length];
        for (int i = 0; i < dArr.length; i++) {
            dArr2[i] = new double[dArr[i].length - 1];
            if (dArr2[i].length != 0) {
                double d = dArr[i][dArr[i].length - 1] - dArr[i][0];
                int i2 = 0;
                while (i2 < dArr[i].length - 1) {
                    int i3 = i2 + 1;
                    dArr2[i][i2] = d == 0.0d ? 1.0d : (((dArr[i][i2] + dArr[i][i3]) * 0.5d) - dArr[i][0]) / d;
                    i2 = i3;
                }
            }
        }
        return dArr2;
    }

    private static int countArrayElements(double[][] dArr) {
        int i = 0;
        for (double[] dArr2 : dArr) {
            i += dArr2.length;
        }
        return i;
    }

    private static void setCheckpointValues(long[][][] jArr, int i, long[][] jArr2, int[] iArr) {
        long j = 0;
        for (int i2 = 0; i2 < jArr.length; i2++) {
            jArr[i2][i][1] = jArr2[i2][iArr[i2]];
            j += jArr[i2][i][1];
        }
        for (long[][] jArr3 : jArr) {
            jArr3[i][0] = j;
        }
    }
}
