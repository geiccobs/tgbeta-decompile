package com.google.android.exoplayer2.trackselection;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunkIterator;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes3.dex */
public class AdaptiveTrackSelection extends BaseTrackSelection {
    public static final float DEFAULT_BANDWIDTH_FRACTION = 0.7f;
    public static final float DEFAULT_BUFFERED_FRACTION_TO_LIVE_EDGE_FOR_QUALITY_INCREASE = 0.75f;
    public static final int DEFAULT_MAX_DURATION_FOR_QUALITY_DECREASE_MS = 25000;
    public static final int DEFAULT_MIN_DURATION_FOR_QUALITY_INCREASE_MS = 10000;
    public static final int DEFAULT_MIN_DURATION_TO_RETAIN_AFTER_DISCARD_MS = 25000;
    public static final long DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS = 2000;
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

    /* loaded from: classes3.dex */
    public interface BandwidthProvider {
        long getAllocatedBandwidth();
    }

    /* loaded from: classes3.dex */
    public static class Factory implements TrackSelection.Factory {
        private final float bandwidthFraction;
        private final BandwidthMeter bandwidthMeter;
        private final float bufferedFractionToLiveEdgeForQualityIncrease;
        private final Clock clock;
        private final int maxDurationForQualityDecreaseMs;
        private final int minDurationForQualityIncreaseMs;
        private final int minDurationToRetainAfterDiscardMs;
        private final long minTimeBetweenBufferReevaluationMs;

        public Factory() {
            this(10000, 25000, 25000, 0.7f, 0.75f, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS, Clock.DEFAULT);
        }

        @Deprecated
        public Factory(BandwidthMeter bandwidthMeter) {
            this(bandwidthMeter, 10000, 25000, 25000, 0.7f, 0.75f, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS, Clock.DEFAULT);
        }

        public Factory(int minDurationForQualityIncreaseMs, int maxDurationForQualityDecreaseMs, int minDurationToRetainAfterDiscardMs, float bandwidthFraction) {
            this(minDurationForQualityIncreaseMs, maxDurationForQualityDecreaseMs, minDurationToRetainAfterDiscardMs, bandwidthFraction, 0.75f, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS, Clock.DEFAULT);
        }

        @Deprecated
        public Factory(BandwidthMeter bandwidthMeter, int minDurationForQualityIncreaseMs, int maxDurationForQualityDecreaseMs, int minDurationToRetainAfterDiscardMs, float bandwidthFraction) {
            this(bandwidthMeter, minDurationForQualityIncreaseMs, maxDurationForQualityDecreaseMs, minDurationToRetainAfterDiscardMs, bandwidthFraction, 0.75f, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS, Clock.DEFAULT);
        }

        public Factory(int minDurationForQualityIncreaseMs, int maxDurationForQualityDecreaseMs, int minDurationToRetainAfterDiscardMs, float bandwidthFraction, float bufferedFractionToLiveEdgeForQualityIncrease, long minTimeBetweenBufferReevaluationMs, Clock clock) {
            this(null, minDurationForQualityIncreaseMs, maxDurationForQualityDecreaseMs, minDurationToRetainAfterDiscardMs, bandwidthFraction, bufferedFractionToLiveEdgeForQualityIncrease, minTimeBetweenBufferReevaluationMs, clock);
        }

        @Deprecated
        public Factory(BandwidthMeter bandwidthMeter, int minDurationForQualityIncreaseMs, int maxDurationForQualityDecreaseMs, int minDurationToRetainAfterDiscardMs, float bandwidthFraction, float bufferedFractionToLiveEdgeForQualityIncrease, long minTimeBetweenBufferReevaluationMs, Clock clock) {
            this.bandwidthMeter = bandwidthMeter;
            this.minDurationForQualityIncreaseMs = minDurationForQualityIncreaseMs;
            this.maxDurationForQualityDecreaseMs = maxDurationForQualityDecreaseMs;
            this.minDurationToRetainAfterDiscardMs = minDurationToRetainAfterDiscardMs;
            this.bandwidthFraction = bandwidthFraction;
            this.bufferedFractionToLiveEdgeForQualityIncrease = bufferedFractionToLiveEdgeForQualityIncrease;
            this.minTimeBetweenBufferReevaluationMs = minTimeBetweenBufferReevaluationMs;
            this.clock = clock;
        }

        @Override // com.google.android.exoplayer2.trackselection.TrackSelection.Factory
        public final TrackSelection[] createTrackSelections(TrackSelection.Definition[] definitions, BandwidthMeter bandwidthMeter) {
            if (this.bandwidthMeter != null) {
                bandwidthMeter = this.bandwidthMeter;
            }
            TrackSelection[] selections = new TrackSelection[definitions.length];
            int totalFixedBandwidth = 0;
            for (int i = 0; i < definitions.length; i++) {
                TrackSelection.Definition definition = definitions[i];
                if (definition != null && definition.tracks.length == 1) {
                    selections[i] = new FixedTrackSelection(definition.group, definition.tracks[0], definition.reason, definition.data);
                    int trackBitrate = definition.group.getFormat(definition.tracks[0]).bitrate;
                    if (trackBitrate != -1) {
                        totalFixedBandwidth += trackBitrate;
                    }
                }
            }
            List<AdaptiveTrackSelection> adaptiveSelections = new ArrayList<>();
            for (int i2 = 0; i2 < definitions.length; i2++) {
                TrackSelection.Definition definition2 = definitions[i2];
                if (definition2 != null && definition2.tracks.length > 1) {
                    AdaptiveTrackSelection adaptiveSelection = createAdaptiveTrackSelection(definition2.group, bandwidthMeter, definition2.tracks, totalFixedBandwidth);
                    adaptiveSelections.add(adaptiveSelection);
                    selections[i2] = adaptiveSelection;
                }
            }
            int i3 = adaptiveSelections.size();
            if (i3 > 1) {
                long[][] adaptiveTrackBitrates = new long[adaptiveSelections.size()];
                for (int i4 = 0; i4 < adaptiveSelections.size(); i4++) {
                    AdaptiveTrackSelection adaptiveSelection2 = adaptiveSelections.get(i4);
                    adaptiveTrackBitrates[i4] = new long[adaptiveSelection2.length()];
                    for (int j = 0; j < adaptiveSelection2.length(); j++) {
                        adaptiveTrackBitrates[i4][j] = adaptiveSelection2.getFormat((adaptiveSelection2.length() - j) - 1).bitrate;
                    }
                }
                long[][][] bandwidthCheckpoints = AdaptiveTrackSelection.getAllocationCheckpoints(adaptiveTrackBitrates);
                for (int i5 = 0; i5 < adaptiveSelections.size(); i5++) {
                    adaptiveSelections.get(i5).experimental_setBandwidthAllocationCheckpoints(bandwidthCheckpoints[i5]);
                }
            }
            return selections;
        }

        protected AdaptiveTrackSelection createAdaptiveTrackSelection(TrackGroup group, BandwidthMeter bandwidthMeter, int[] tracks, int totalFixedTrackBandwidth) {
            return new AdaptiveTrackSelection(group, tracks, new DefaultBandwidthProvider(bandwidthMeter, this.bandwidthFraction, totalFixedTrackBandwidth), this.minDurationForQualityIncreaseMs, this.maxDurationForQualityDecreaseMs, this.minDurationToRetainAfterDiscardMs, this.bufferedFractionToLiveEdgeForQualityIncrease, this.minTimeBetweenBufferReevaluationMs, this.clock);
        }
    }

    public AdaptiveTrackSelection(TrackGroup group, int[] tracks, BandwidthMeter bandwidthMeter) {
        this(group, tracks, bandwidthMeter, 0L, 10000L, 25000L, 25000L, 0.7f, 0.75f, DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS, Clock.DEFAULT);
    }

    public AdaptiveTrackSelection(TrackGroup group, int[] tracks, BandwidthMeter bandwidthMeter, long reservedBandwidth, long minDurationForQualityIncreaseMs, long maxDurationForQualityDecreaseMs, long minDurationToRetainAfterDiscardMs, float bandwidthFraction, float bufferedFractionToLiveEdgeForQualityIncrease, long minTimeBetweenBufferReevaluationMs, Clock clock) {
        this(group, tracks, new DefaultBandwidthProvider(bandwidthMeter, bandwidthFraction, reservedBandwidth), minDurationForQualityIncreaseMs, maxDurationForQualityDecreaseMs, minDurationToRetainAfterDiscardMs, bufferedFractionToLiveEdgeForQualityIncrease, minTimeBetweenBufferReevaluationMs, clock);
    }

    private AdaptiveTrackSelection(TrackGroup group, int[] tracks, BandwidthProvider bandwidthProvider, long minDurationForQualityIncreaseMs, long maxDurationForQualityDecreaseMs, long minDurationToRetainAfterDiscardMs, float bufferedFractionToLiveEdgeForQualityIncrease, long minTimeBetweenBufferReevaluationMs, Clock clock) {
        super(group, tracks);
        this.bandwidthProvider = bandwidthProvider;
        this.minDurationForQualityIncreaseUs = minDurationForQualityIncreaseMs * 1000;
        this.maxDurationForQualityDecreaseUs = maxDurationForQualityDecreaseMs * 1000;
        this.minDurationToRetainAfterDiscardUs = 1000 * minDurationToRetainAfterDiscardMs;
        this.bufferedFractionToLiveEdgeForQualityIncrease = bufferedFractionToLiveEdgeForQualityIncrease;
        this.minTimeBetweenBufferReevaluationMs = minTimeBetweenBufferReevaluationMs;
        this.clock = clock;
        this.playbackSpeed = 1.0f;
        this.reason = 0;
        this.lastBufferEvaluationMs = C.TIME_UNSET;
    }

    public void experimental_setBandwidthAllocationCheckpoints(long[][] allocationCheckpoints) {
        ((DefaultBandwidthProvider) this.bandwidthProvider).experimental_setBandwidthAllocationCheckpoints(allocationCheckpoints);
    }

    @Override // com.google.android.exoplayer2.trackselection.BaseTrackSelection, com.google.android.exoplayer2.trackselection.TrackSelection
    public void enable() {
        this.lastBufferEvaluationMs = C.TIME_UNSET;
    }

    @Override // com.google.android.exoplayer2.trackselection.BaseTrackSelection, com.google.android.exoplayer2.trackselection.TrackSelection
    public void onPlaybackSpeed(float playbackSpeed) {
        this.playbackSpeed = playbackSpeed;
    }

    @Override // com.google.android.exoplayer2.trackselection.TrackSelection
    public void updateSelectedTrack(long playbackPositionUs, long bufferedDurationUs, long availableDurationUs, List<? extends MediaChunk> queue, MediaChunkIterator[] mediaChunkIterators) {
        long nowMs = this.clock.elapsedRealtime();
        if (this.reason == 0) {
            this.reason = 1;
            this.selectedIndex = determineIdealSelectedIndex(nowMs);
            return;
        }
        int currentSelectedIndex = this.selectedIndex;
        int determineIdealSelectedIndex = determineIdealSelectedIndex(nowMs);
        this.selectedIndex = determineIdealSelectedIndex;
        if (determineIdealSelectedIndex == currentSelectedIndex) {
            return;
        }
        if (!isBlacklisted(currentSelectedIndex, nowMs)) {
            Format currentFormat = getFormat(currentSelectedIndex);
            Format selectedFormat = getFormat(this.selectedIndex);
            if (selectedFormat.bitrate > currentFormat.bitrate && bufferedDurationUs < minDurationForQualityIncreaseUs(availableDurationUs)) {
                this.selectedIndex = currentSelectedIndex;
            }
            if (selectedFormat.bitrate < currentFormat.bitrate && bufferedDurationUs >= this.maxDurationForQualityDecreaseUs) {
                this.selectedIndex = currentSelectedIndex;
            }
        }
        if (this.selectedIndex != currentSelectedIndex) {
            this.reason = 3;
        }
    }

    @Override // com.google.android.exoplayer2.trackselection.TrackSelection
    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    @Override // com.google.android.exoplayer2.trackselection.TrackSelection
    public int getSelectionReason() {
        return this.reason;
    }

    @Override // com.google.android.exoplayer2.trackselection.TrackSelection
    public Object getSelectionData() {
        return null;
    }

    @Override // com.google.android.exoplayer2.trackselection.BaseTrackSelection, com.google.android.exoplayer2.trackselection.TrackSelection
    public int evaluateQueueSize(long playbackPositionUs, List<? extends MediaChunk> queue) {
        AdaptiveTrackSelection adaptiveTrackSelection = this;
        List<? extends MediaChunk> list = queue;
        long nowMs = adaptiveTrackSelection.clock.elapsedRealtime();
        if (!adaptiveTrackSelection.shouldEvaluateQueueSize(nowMs)) {
            return queue.size();
        }
        adaptiveTrackSelection.lastBufferEvaluationMs = nowMs;
        if (queue.isEmpty()) {
            return 0;
        }
        int queueSize = queue.size();
        MediaChunk lastChunk = list.get(queueSize - 1);
        long playoutBufferedDurationBeforeLastChunkUs = Util.getPlayoutDurationForMediaDuration(lastChunk.startTimeUs - playbackPositionUs, adaptiveTrackSelection.playbackSpeed);
        long minDurationToRetainAfterDiscardUs = getMinDurationToRetainAfterDiscardUs();
        if (playoutBufferedDurationBeforeLastChunkUs < minDurationToRetainAfterDiscardUs) {
            return queueSize;
        }
        int idealSelectedIndex = adaptiveTrackSelection.determineIdealSelectedIndex(nowMs);
        Format idealFormat = adaptiveTrackSelection.getFormat(idealSelectedIndex);
        int i = 0;
        while (i < queueSize) {
            MediaChunk chunk = list.get(i);
            Format format = chunk.trackFormat;
            long nowMs2 = nowMs;
            long mediaDurationBeforeThisChunkUs = chunk.startTimeUs - playbackPositionUs;
            long playoutDurationBeforeThisChunkUs = Util.getPlayoutDurationForMediaDuration(mediaDurationBeforeThisChunkUs, adaptiveTrackSelection.playbackSpeed);
            if (playoutDurationBeforeThisChunkUs < minDurationToRetainAfterDiscardUs || format.bitrate >= idealFormat.bitrate || format.height == -1 || format.height >= 720 || format.width == -1 || format.width >= 1280 || format.height >= idealFormat.height) {
                i++;
                adaptiveTrackSelection = this;
                list = queue;
                nowMs = nowMs2;
            } else {
                return i;
            }
        }
        return queueSize;
    }

    protected boolean canSelectFormat(Format format, int trackBitrate, float playbackSpeed, long effectiveBitrate) {
        return ((long) Math.round(((float) trackBitrate) * playbackSpeed)) <= effectiveBitrate;
    }

    protected boolean shouldEvaluateQueueSize(long nowMs) {
        long j = this.lastBufferEvaluationMs;
        return j == C.TIME_UNSET || nowMs - j >= this.minTimeBetweenBufferReevaluationMs;
    }

    protected long getMinDurationToRetainAfterDiscardUs() {
        return this.minDurationToRetainAfterDiscardUs;
    }

    private int determineIdealSelectedIndex(long nowMs) {
        long effectiveBitrate = this.bandwidthProvider.getAllocatedBandwidth();
        int lowestBitrateNonBlacklistedIndex = 0;
        for (int i = 0; i < this.length; i++) {
            if (nowMs == Long.MIN_VALUE || !isBlacklisted(i, nowMs)) {
                Format format = getFormat(i);
                if (canSelectFormat(format, format.bitrate, this.playbackSpeed, effectiveBitrate)) {
                    return i;
                }
                lowestBitrateNonBlacklistedIndex = i;
            }
        }
        return lowestBitrateNonBlacklistedIndex;
    }

    private long minDurationForQualityIncreaseUs(long availableDurationUs) {
        boolean isAvailableDurationTooShort = availableDurationUs != C.TIME_UNSET && availableDurationUs <= this.minDurationForQualityIncreaseUs;
        if (isAvailableDurationTooShort) {
            return ((float) availableDurationUs) * this.bufferedFractionToLiveEdgeForQualityIncrease;
        }
        return this.minDurationForQualityIncreaseUs;
    }

    /* loaded from: classes3.dex */
    public static final class DefaultBandwidthProvider implements BandwidthProvider {
        private long[][] allocationCheckpoints;
        private final float bandwidthFraction;
        private final BandwidthMeter bandwidthMeter;
        private final long reservedBandwidth;

        DefaultBandwidthProvider(BandwidthMeter bandwidthMeter, float bandwidthFraction, long reservedBandwidth) {
            this.bandwidthMeter = bandwidthMeter;
            this.bandwidthFraction = bandwidthFraction;
            this.reservedBandwidth = reservedBandwidth;
        }

        @Override // com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection.BandwidthProvider
        public long getAllocatedBandwidth() {
            long[][] jArr;
            long totalBandwidth = ((float) this.bandwidthMeter.getBitrateEstimate()) * this.bandwidthFraction;
            long allocatableBandwidth = Math.max(0L, totalBandwidth - this.reservedBandwidth);
            if (this.allocationCheckpoints == null) {
                return allocatableBandwidth;
            }
            int nextIndex = 1;
            while (true) {
                jArr = this.allocationCheckpoints;
                if (nextIndex >= jArr.length - 1 || jArr[nextIndex][0] >= allocatableBandwidth) {
                    break;
                }
                nextIndex++;
            }
            long[] previous = jArr[nextIndex - 1];
            long[] next = jArr[nextIndex];
            float fractionBetweenCheckpoints = ((float) (allocatableBandwidth - previous[0])) / ((float) (next[0] - previous[0]));
            return previous[1] + (((float) (next[1] - previous[1])) * fractionBetweenCheckpoints);
        }

        void experimental_setBandwidthAllocationCheckpoints(long[][] allocationCheckpoints) {
            Assertions.checkArgument(allocationCheckpoints.length >= 2);
            this.allocationCheckpoints = allocationCheckpoints;
        }
    }

    public static long[][][] getAllocationCheckpoints(long[][] trackBitrates) {
        double[][] logBitrates = getLogArrayValues(trackBitrates);
        double[][] switchPoints = getSwitchPoints(logBitrates);
        int checkpointCount = countArrayElements(switchPoints) + 3;
        long[][][] checkpoints = (long[][][]) Array.newInstance(long.class, logBitrates.length, checkpointCount, 2);
        int[] currentSelection = new int[logBitrates.length];
        setCheckpointValues(checkpoints, 1, trackBitrates, currentSelection);
        for (int checkpointIndex = 2; checkpointIndex < checkpointCount - 1; checkpointIndex++) {
            int nextUpdateIndex = 0;
            double nextUpdateSwitchPoint = Double.MAX_VALUE;
            for (int i = 0; i < logBitrates.length; i++) {
                if (currentSelection[i] + 1 != logBitrates[i].length) {
                    double switchPoint = switchPoints[i][currentSelection[i]];
                    if (switchPoint < nextUpdateSwitchPoint) {
                        nextUpdateSwitchPoint = switchPoint;
                        nextUpdateIndex = i;
                    }
                }
            }
            int i2 = currentSelection[nextUpdateIndex];
            currentSelection[nextUpdateIndex] = i2 + 1;
            setCheckpointValues(checkpoints, checkpointIndex, trackBitrates, currentSelection);
        }
        for (long[][] points : checkpoints) {
            points[checkpointCount - 1][0] = points[checkpointCount - 2][0] * 2;
            points[checkpointCount - 1][1] = points[checkpointCount - 2][1] * 2;
        }
        return checkpoints;
    }

    private static double[][] getLogArrayValues(long[][] values) {
        double[][] logValues = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            logValues[i] = new double[values[i].length];
            for (int j = 0; j < values[i].length; j++) {
                logValues[i][j] = values[i][j] == -1 ? FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE : Math.log(values[i][j]);
            }
        }
        return logValues;
    }

    private static double[][] getSwitchPoints(double[][] logBitrates) {
        double[][] switchPoints = new double[logBitrates.length];
        for (int i = 0; i < logBitrates.length; i++) {
            switchPoints[i] = new double[logBitrates[i].length - 1];
            if (switchPoints[i].length != 0) {
                double totalBitrateDiff = logBitrates[i][logBitrates[i].length - 1] - logBitrates[i][0];
                for (int j = 0; j < logBitrates[i].length - 1; j++) {
                    double switchBitrate = (logBitrates[i][j] + logBitrates[i][j + 1]) * 0.5d;
                    switchPoints[i][j] = totalBitrateDiff == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE ? 1.0d : (switchBitrate - logBitrates[i][0]) / totalBitrateDiff;
                }
            }
        }
        return switchPoints;
    }

    private static int countArrayElements(double[][] array) {
        int count = 0;
        for (double[] subArray : array) {
            count += subArray.length;
        }
        return count;
    }

    private static void setCheckpointValues(long[][][] checkpoints, int checkpointIndex, long[][] trackBitrates, int[] selectedTracks) {
        long totalBitrate = 0;
        for (int i = 0; i < checkpoints.length; i++) {
            checkpoints[i][checkpointIndex][1] = trackBitrates[i][selectedTracks[i]];
            totalBitrate += checkpoints[i][checkpointIndex][1];
        }
        for (long[][] points : checkpoints) {
            points[checkpointIndex][0] = totalBitrate;
        }
    }
}
