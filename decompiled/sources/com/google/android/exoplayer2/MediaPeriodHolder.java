package com.google.android.exoplayer2;

import com.google.android.exoplayer2.source.ClippingMediaPeriod;
import com.google.android.exoplayer2.source.EmptySampleStream;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.SampleStream;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectorResult;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
/* loaded from: classes3.dex */
final class MediaPeriodHolder {
    private static final String TAG = "MediaPeriodHolder";
    public boolean hasEnabledTracks;
    public MediaPeriodInfo info;
    private final boolean[] mayRetainStreamFlags;
    public final MediaPeriod mediaPeriod;
    private final MediaSource mediaSource;
    private MediaPeriodHolder next;
    public boolean prepared;
    private final RendererCapabilities[] rendererCapabilities;
    private long rendererPositionOffsetUs;
    public final SampleStream[] sampleStreams;
    private TrackGroupArray trackGroups = TrackGroupArray.EMPTY;
    private final TrackSelector trackSelector;
    private TrackSelectorResult trackSelectorResult;
    public final Object uid;

    public MediaPeriodHolder(RendererCapabilities[] rendererCapabilities, long rendererPositionOffsetUs, TrackSelector trackSelector, Allocator allocator, MediaSource mediaSource, MediaPeriodInfo info, TrackSelectorResult emptyTrackSelectorResult) {
        this.rendererCapabilities = rendererCapabilities;
        this.rendererPositionOffsetUs = rendererPositionOffsetUs;
        this.trackSelector = trackSelector;
        this.mediaSource = mediaSource;
        this.uid = info.id.periodUid;
        this.info = info;
        this.trackSelectorResult = emptyTrackSelectorResult;
        this.sampleStreams = new SampleStream[rendererCapabilities.length];
        this.mayRetainStreamFlags = new boolean[rendererCapabilities.length];
        this.mediaPeriod = createMediaPeriod(info.id, mediaSource, allocator, info.startPositionUs, info.endPositionUs);
    }

    public long toRendererTime(long periodTimeUs) {
        return getRendererOffset() + periodTimeUs;
    }

    public long toPeriodTime(long rendererTimeUs) {
        return rendererTimeUs - getRendererOffset();
    }

    public long getRendererOffset() {
        return this.rendererPositionOffsetUs;
    }

    public void setRendererOffset(long rendererPositionOffsetUs) {
        this.rendererPositionOffsetUs = rendererPositionOffsetUs;
    }

    public long getStartPositionRendererTime() {
        return this.info.startPositionUs + this.rendererPositionOffsetUs;
    }

    public boolean isFullyBuffered() {
        return this.prepared && (!this.hasEnabledTracks || this.mediaPeriod.getBufferedPositionUs() == Long.MIN_VALUE);
    }

    public long getBufferedPositionUs() {
        if (!this.prepared) {
            return this.info.startPositionUs;
        }
        long bufferedPositionUs = this.hasEnabledTracks ? this.mediaPeriod.getBufferedPositionUs() : Long.MIN_VALUE;
        return bufferedPositionUs == Long.MIN_VALUE ? this.info.durationUs : bufferedPositionUs;
    }

    public long getNextLoadPositionUs() {
        if (!this.prepared) {
            return 0L;
        }
        return this.mediaPeriod.getNextLoadPositionUs();
    }

    public void handlePrepared(float playbackSpeed, Timeline timeline) throws ExoPlaybackException {
        this.prepared = true;
        this.trackGroups = this.mediaPeriod.getTrackGroups();
        TrackSelectorResult selectorResult = selectTracks(playbackSpeed, timeline);
        long newStartPositionUs = applyTrackSelection(selectorResult, this.info.startPositionUs, false);
        this.rendererPositionOffsetUs += this.info.startPositionUs - newStartPositionUs;
        this.info = this.info.copyWithStartPositionUs(newStartPositionUs);
    }

    public void reevaluateBuffer(long rendererPositionUs) {
        Assertions.checkState(isLoadingMediaPeriod());
        if (this.prepared) {
            this.mediaPeriod.reevaluateBuffer(toPeriodTime(rendererPositionUs));
        }
    }

    public void continueLoading(long rendererPositionUs) {
        Assertions.checkState(isLoadingMediaPeriod());
        long loadingPeriodPositionUs = toPeriodTime(rendererPositionUs);
        this.mediaPeriod.continueLoading(loadingPeriodPositionUs);
    }

    public TrackSelectorResult selectTracks(float playbackSpeed, Timeline timeline) throws ExoPlaybackException {
        TrackSelection[] all;
        TrackSelectorResult selectorResult = this.trackSelector.selectTracks(this.rendererCapabilities, getTrackGroups(), this.info.id, timeline);
        for (TrackSelection trackSelection : selectorResult.selections.getAll()) {
            if (trackSelection != null) {
                trackSelection.onPlaybackSpeed(playbackSpeed);
            }
        }
        return selectorResult;
    }

    public long applyTrackSelection(TrackSelectorResult trackSelectorResult, long positionUs, boolean forceRecreateStreams) {
        return applyTrackSelection(trackSelectorResult, positionUs, forceRecreateStreams, new boolean[this.rendererCapabilities.length]);
    }

    public long applyTrackSelection(TrackSelectorResult newTrackSelectorResult, long positionUs, boolean forceRecreateStreams, boolean[] streamResetFlags) {
        int i = 0;
        while (true) {
            boolean z = false;
            if (i >= newTrackSelectorResult.length) {
                break;
            }
            boolean[] zArr = this.mayRetainStreamFlags;
            if (!forceRecreateStreams && newTrackSelectorResult.isEquivalent(this.trackSelectorResult, i)) {
                z = true;
            }
            zArr[i] = z;
            i++;
        }
        disassociateNoSampleRenderersWithEmptySampleStream(this.sampleStreams);
        disableTrackSelectionsInResult();
        this.trackSelectorResult = newTrackSelectorResult;
        enableTrackSelectionsInResult();
        TrackSelectionArray trackSelections = newTrackSelectorResult.selections;
        long positionUs2 = this.mediaPeriod.selectTracks(trackSelections.getAll(), this.mayRetainStreamFlags, this.sampleStreams, streamResetFlags, positionUs);
        associateNoSampleRenderersWithEmptySampleStream(this.sampleStreams);
        this.hasEnabledTracks = false;
        int i2 = 0;
        while (true) {
            SampleStream[] sampleStreamArr = this.sampleStreams;
            if (i2 < sampleStreamArr.length) {
                if (sampleStreamArr[i2] != null) {
                    Assertions.checkState(newTrackSelectorResult.isRendererEnabled(i2));
                    if (this.rendererCapabilities[i2].getTrackType() != 6) {
                        this.hasEnabledTracks = true;
                    }
                } else {
                    Assertions.checkState(trackSelections.get(i2) == null);
                }
                i2++;
            } else {
                return positionUs2;
            }
        }
    }

    public void release() {
        disableTrackSelectionsInResult();
        releaseMediaPeriod(this.info.endPositionUs, this.mediaSource, this.mediaPeriod);
    }

    public void setNext(MediaPeriodHolder nextMediaPeriodHolder) {
        if (nextMediaPeriodHolder == this.next) {
            return;
        }
        disableTrackSelectionsInResult();
        this.next = nextMediaPeriodHolder;
        enableTrackSelectionsInResult();
    }

    public MediaPeriodHolder getNext() {
        return this.next;
    }

    public TrackGroupArray getTrackGroups() {
        return this.trackGroups;
    }

    public TrackSelectorResult getTrackSelectorResult() {
        return this.trackSelectorResult;
    }

    private void enableTrackSelectionsInResult() {
        if (!isLoadingMediaPeriod()) {
            return;
        }
        for (int i = 0; i < this.trackSelectorResult.length; i++) {
            boolean rendererEnabled = this.trackSelectorResult.isRendererEnabled(i);
            TrackSelection trackSelection = this.trackSelectorResult.selections.get(i);
            if (rendererEnabled && trackSelection != null) {
                trackSelection.enable();
            }
        }
    }

    private void disableTrackSelectionsInResult() {
        if (!isLoadingMediaPeriod()) {
            return;
        }
        for (int i = 0; i < this.trackSelectorResult.length; i++) {
            boolean rendererEnabled = this.trackSelectorResult.isRendererEnabled(i);
            TrackSelection trackSelection = this.trackSelectorResult.selections.get(i);
            if (rendererEnabled && trackSelection != null) {
                trackSelection.disable();
            }
        }
    }

    private void disassociateNoSampleRenderersWithEmptySampleStream(SampleStream[] sampleStreams) {
        int i = 0;
        while (true) {
            RendererCapabilities[] rendererCapabilitiesArr = this.rendererCapabilities;
            if (i < rendererCapabilitiesArr.length) {
                if (rendererCapabilitiesArr[i].getTrackType() == 6) {
                    sampleStreams[i] = null;
                }
                i++;
            } else {
                return;
            }
        }
    }

    private void associateNoSampleRenderersWithEmptySampleStream(SampleStream[] sampleStreams) {
        int i = 0;
        while (true) {
            RendererCapabilities[] rendererCapabilitiesArr = this.rendererCapabilities;
            if (i < rendererCapabilitiesArr.length) {
                if (rendererCapabilitiesArr[i].getTrackType() == 6 && this.trackSelectorResult.isRendererEnabled(i)) {
                    sampleStreams[i] = new EmptySampleStream();
                }
                i++;
            } else {
                return;
            }
        }
    }

    private boolean isLoadingMediaPeriod() {
        return this.next == null;
    }

    private static MediaPeriod createMediaPeriod(MediaSource.MediaPeriodId id, MediaSource mediaSource, Allocator allocator, long startPositionUs, long endPositionUs) {
        MediaPeriod mediaPeriod = mediaSource.createPeriod(id, allocator, startPositionUs);
        if (endPositionUs != C.TIME_UNSET && endPositionUs != Long.MIN_VALUE) {
            return new ClippingMediaPeriod(mediaPeriod, true, 0L, endPositionUs);
        }
        return mediaPeriod;
    }

    private static void releaseMediaPeriod(long endPositionUs, MediaSource mediaSource, MediaPeriod mediaPeriod) {
        try {
            if (endPositionUs != C.TIME_UNSET && endPositionUs != Long.MIN_VALUE) {
                mediaSource.releasePeriod(((ClippingMediaPeriod) mediaPeriod).mediaPeriod);
            } else {
                mediaSource.releasePeriod(mediaPeriod);
            }
        } catch (RuntimeException e) {
            Log.e(TAG, "Period release failed.", e);
        }
    }
}
