package com.google.android.exoplayer2.source;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
/* loaded from: classes3.dex */
final class MergingMediaPeriod implements MediaPeriod, MediaPeriod.Callback {
    private MediaPeriod.Callback callback;
    private SequenceableLoader compositeSequenceableLoader;
    private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
    public final MediaPeriod[] periods;
    private TrackGroupArray trackGroups;
    private final ArrayList<MediaPeriod> childrenPendingPreparation = new ArrayList<>();
    private final IdentityHashMap<SampleStream, Integer> streamPeriodIndices = new IdentityHashMap<>();
    private MediaPeriod[] enabledPeriods = new MediaPeriod[0];

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public /* synthetic */ List getStreamKeys(List list) {
        List emptyList;
        emptyList = Collections.emptyList();
        return emptyList;
    }

    public MergingMediaPeriod(CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory, MediaPeriod... periods) {
        this.compositeSequenceableLoaderFactory = compositeSequenceableLoaderFactory;
        this.periods = periods;
        this.compositeSequenceableLoader = compositeSequenceableLoaderFactory.createCompositeSequenceableLoader(new SequenceableLoader[0]);
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public void prepare(MediaPeriod.Callback callback, long positionUs) {
        MediaPeriod[] mediaPeriodArr;
        this.callback = callback;
        Collections.addAll(this.childrenPendingPreparation, this.periods);
        for (MediaPeriod period : this.periods) {
            period.prepare(this, positionUs);
        }
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public void maybeThrowPrepareError() throws IOException {
        MediaPeriod[] mediaPeriodArr;
        for (MediaPeriod period : this.periods) {
            period.maybeThrowPrepareError();
        }
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public TrackGroupArray getTrackGroups() {
        return (TrackGroupArray) Assertions.checkNotNull(this.trackGroups);
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public long selectTracks(TrackSelection[] selections, boolean[] mayRetainStreamFlags, SampleStream[] streams, boolean[] streamResetFlags, long positionUs) {
        int[] streamChildIndices = new int[selections.length];
        int[] selectionChildIndices = new int[selections.length];
        for (int i = 0; i < selections.length; i++) {
            streamChildIndices[i] = streams[i] == null ? -1 : this.streamPeriodIndices.get(streams[i]).intValue();
            selectionChildIndices[i] = -1;
            if (selections[i] != null) {
                TrackGroup trackGroup = selections[i].getTrackGroup();
                int j = 0;
                while (true) {
                    MediaPeriod[] mediaPeriodArr = this.periods;
                    if (j < mediaPeriodArr.length) {
                        if (mediaPeriodArr[j].getTrackGroups().indexOf(trackGroup) == -1) {
                            j++;
                        } else {
                            selectionChildIndices[i] = j;
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
        }
        this.streamPeriodIndices.clear();
        SampleStream[] newStreams = new SampleStream[selections.length];
        SampleStream[] childStreams = new SampleStream[selections.length];
        TrackSelection[] childSelections = new TrackSelection[selections.length];
        ArrayList<MediaPeriod> enabledPeriodsList = new ArrayList<>(this.periods.length);
        long positionUs2 = positionUs;
        int i2 = 0;
        while (i2 < this.periods.length) {
            for (int j2 = 0; j2 < selections.length; j2++) {
                TrackSelection trackSelection = null;
                childStreams[j2] = streamChildIndices[j2] == i2 ? streams[j2] : null;
                if (selectionChildIndices[j2] == i2) {
                    trackSelection = selections[j2];
                }
                childSelections[j2] = trackSelection;
            }
            TrackSelection[] trackSelectionArr = childSelections;
            TrackSelection[] childSelections2 = childSelections;
            int i3 = i2;
            long selectPositionUs = this.periods[i2].selectTracks(trackSelectionArr, mayRetainStreamFlags, childStreams, streamResetFlags, positionUs2);
            if (i3 == 0) {
                positionUs2 = selectPositionUs;
            } else if (selectPositionUs != positionUs2) {
                throw new IllegalStateException("Children enabled at different positions.");
            }
            boolean periodEnabled = false;
            for (int j3 = 0; j3 < selections.length; j3++) {
                if (selectionChildIndices[j3] == i3) {
                    SampleStream childStream = (SampleStream) Assertions.checkNotNull(childStreams[j3]);
                    newStreams[j3] = childStreams[j3];
                    periodEnabled = true;
                    this.streamPeriodIndices.put(childStream, Integer.valueOf(i3));
                } else if (streamChildIndices[j3] == i3) {
                    Assertions.checkState(childStreams[j3] == null);
                }
            }
            if (periodEnabled) {
                enabledPeriodsList.add(this.periods[i3]);
            }
            i2 = i3 + 1;
            childSelections = childSelections2;
        }
        System.arraycopy(newStreams, 0, streams, 0, newStreams.length);
        MediaPeriod[] mediaPeriodArr2 = new MediaPeriod[enabledPeriodsList.size()];
        this.enabledPeriods = mediaPeriodArr2;
        enabledPeriodsList.toArray(mediaPeriodArr2);
        this.compositeSequenceableLoader = this.compositeSequenceableLoaderFactory.createCompositeSequenceableLoader(this.enabledPeriods);
        return positionUs2;
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public void discardBuffer(long positionUs, boolean toKeyframe) {
        MediaPeriod[] mediaPeriodArr;
        for (MediaPeriod period : this.enabledPeriods) {
            period.discardBuffer(positionUs, toKeyframe);
        }
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
    public void reevaluateBuffer(long positionUs) {
        this.compositeSequenceableLoader.reevaluateBuffer(positionUs);
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
    public boolean continueLoading(long positionUs) {
        if (!this.childrenPendingPreparation.isEmpty()) {
            int childrenPendingPreparationSize = this.childrenPendingPreparation.size();
            for (int i = 0; i < childrenPendingPreparationSize; i++) {
                this.childrenPendingPreparation.get(i).continueLoading(positionUs);
            }
            return false;
        }
        return this.compositeSequenceableLoader.continueLoading(positionUs);
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
    public boolean isLoading() {
        return this.compositeSequenceableLoader.isLoading();
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
    public long getNextLoadPositionUs() {
        return this.compositeSequenceableLoader.getNextLoadPositionUs();
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public long readDiscontinuity() {
        MediaPeriod[] mediaPeriodArr;
        long positionUs = this.periods[0].readDiscontinuity();
        int i = 1;
        while (true) {
            MediaPeriod[] mediaPeriodArr2 = this.periods;
            if (i < mediaPeriodArr2.length) {
                if (mediaPeriodArr2[i].readDiscontinuity() == C.TIME_UNSET) {
                    i++;
                } else {
                    throw new IllegalStateException("Child reported discontinuity.");
                }
            } else {
                int i2 = (positionUs > C.TIME_UNSET ? 1 : (positionUs == C.TIME_UNSET ? 0 : -1));
                if (i2 != 0) {
                    for (MediaPeriod enabledPeriod : this.enabledPeriods) {
                        if (enabledPeriod != this.periods[0] && enabledPeriod.seekToUs(positionUs) != positionUs) {
                            throw new IllegalStateException("Unexpected child seekToUs result.");
                        }
                    }
                }
                return positionUs;
            }
        }
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
    public long getBufferedPositionUs() {
        return this.compositeSequenceableLoader.getBufferedPositionUs();
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public long seekToUs(long positionUs) {
        long positionUs2 = this.enabledPeriods[0].seekToUs(positionUs);
        int i = 1;
        while (true) {
            MediaPeriod[] mediaPeriodArr = this.enabledPeriods;
            if (i < mediaPeriodArr.length) {
                if (mediaPeriodArr[i].seekToUs(positionUs2) == positionUs2) {
                    i++;
                } else {
                    throw new IllegalStateException("Unexpected child seekToUs result.");
                }
            } else {
                return positionUs2;
            }
        }
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public long getAdjustedSeekPositionUs(long positionUs, SeekParameters seekParameters) {
        MediaPeriod[] mediaPeriodArr = this.enabledPeriods;
        MediaPeriod queryPeriod = mediaPeriodArr.length > 0 ? mediaPeriodArr[0] : this.periods[0];
        return queryPeriod.getAdjustedSeekPositionUs(positionUs, seekParameters);
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod.Callback
    public void onPrepared(MediaPeriod preparedPeriod) {
        MediaPeriod[] mediaPeriodArr;
        MediaPeriod[] mediaPeriodArr2;
        this.childrenPendingPreparation.remove(preparedPeriod);
        if (!this.childrenPendingPreparation.isEmpty()) {
            return;
        }
        int totalTrackGroupCount = 0;
        for (MediaPeriod period : this.periods) {
            totalTrackGroupCount += period.getTrackGroups().length;
        }
        TrackGroup[] trackGroupArray = new TrackGroup[totalTrackGroupCount];
        int trackGroupIndex = 0;
        for (MediaPeriod period2 : this.periods) {
            TrackGroupArray periodTrackGroups = period2.getTrackGroups();
            int periodTrackGroupCount = periodTrackGroups.length;
            int j = 0;
            while (j < periodTrackGroupCount) {
                trackGroupArray[trackGroupIndex] = periodTrackGroups.get(j);
                j++;
                trackGroupIndex++;
            }
        }
        this.trackGroups = new TrackGroupArray(trackGroupArray);
        ((MediaPeriod.Callback) Assertions.checkNotNull(this.callback)).onPrepared(this);
    }

    public void onContinueLoadingRequested(MediaPeriod ignored) {
        ((MediaPeriod.Callback) Assertions.checkNotNull(this.callback)).onContinueLoadingRequested(this);
    }
}
