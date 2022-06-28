package com.google.android.exoplayer2.source.smoothstreaming;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.offline.StreamKey;
import com.google.android.exoplayer2.source.CompositeSequenceableLoaderFactory;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.SampleStream;
import com.google.android.exoplayer2.source.SequenceableLoader;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.chunk.ChunkSampleStream;
import com.google.android.exoplayer2.source.smoothstreaming.SsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifest;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.LoaderErrorThrower;
import com.google.android.exoplayer2.upstream.TransferListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes3.dex */
final class SsMediaPeriod implements MediaPeriod, SequenceableLoader.Callback<ChunkSampleStream<SsChunkSource>> {
    private final Allocator allocator;
    private MediaPeriod.Callback callback;
    private final SsChunkSource.Factory chunkSourceFactory;
    private SequenceableLoader compositeSequenceableLoader;
    private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
    private final DrmSessionManager<?> drmSessionManager;
    private final MediaSourceEventListener.EventDispatcher eventDispatcher;
    private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
    private SsManifest manifest;
    private final LoaderErrorThrower manifestLoaderErrorThrower;
    private boolean notifiedReadingStarted;
    private ChunkSampleStream<SsChunkSource>[] sampleStreams;
    private final TrackGroupArray trackGroups;
    private final TransferListener transferListener;

    public SsMediaPeriod(SsManifest manifest, SsChunkSource.Factory chunkSourceFactory, TransferListener transferListener, CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory, DrmSessionManager<?> drmSessionManager, LoadErrorHandlingPolicy loadErrorHandlingPolicy, MediaSourceEventListener.EventDispatcher eventDispatcher, LoaderErrorThrower manifestLoaderErrorThrower, Allocator allocator) {
        this.manifest = manifest;
        this.chunkSourceFactory = chunkSourceFactory;
        this.transferListener = transferListener;
        this.manifestLoaderErrorThrower = manifestLoaderErrorThrower;
        this.drmSessionManager = drmSessionManager;
        this.loadErrorHandlingPolicy = loadErrorHandlingPolicy;
        this.eventDispatcher = eventDispatcher;
        this.allocator = allocator;
        this.compositeSequenceableLoaderFactory = compositeSequenceableLoaderFactory;
        this.trackGroups = buildTrackGroups(manifest, drmSessionManager);
        ChunkSampleStream<SsChunkSource>[] newSampleStreamArray = newSampleStreamArray(0);
        this.sampleStreams = newSampleStreamArray;
        this.compositeSequenceableLoader = compositeSequenceableLoaderFactory.createCompositeSequenceableLoader(newSampleStreamArray);
        eventDispatcher.mediaPeriodCreated();
    }

    public void updateManifest(SsManifest manifest) {
        ChunkSampleStream<SsChunkSource>[] chunkSampleStreamArr;
        this.manifest = manifest;
        for (ChunkSampleStream<SsChunkSource> sampleStream : this.sampleStreams) {
            sampleStream.getChunkSource().updateManifest(manifest);
        }
        this.callback.onContinueLoadingRequested(this);
    }

    public void release() {
        ChunkSampleStream<SsChunkSource>[] chunkSampleStreamArr;
        for (ChunkSampleStream<SsChunkSource> sampleStream : this.sampleStreams) {
            sampleStream.release();
        }
        this.callback = null;
        this.eventDispatcher.mediaPeriodReleased();
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public void prepare(MediaPeriod.Callback callback, long positionUs) {
        this.callback = callback;
        callback.onPrepared(this);
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public void maybeThrowPrepareError() throws IOException {
        this.manifestLoaderErrorThrower.maybeThrowError();
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public TrackGroupArray getTrackGroups() {
        return this.trackGroups;
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public long selectTracks(TrackSelection[] selections, boolean[] mayRetainStreamFlags, SampleStream[] streams, boolean[] streamResetFlags, long positionUs) {
        ArrayList<ChunkSampleStream<SsChunkSource>> sampleStreamsList = new ArrayList<>();
        for (int i = 0; i < selections.length; i++) {
            if (streams[i] != null) {
                ChunkSampleStream<SsChunkSource> stream = (ChunkSampleStream) streams[i];
                if (selections[i] == null || !mayRetainStreamFlags[i]) {
                    stream.release();
                    streams[i] = null;
                } else {
                    stream.getChunkSource().updateTrackSelection(selections[i]);
                    sampleStreamsList.add(stream);
                }
            }
            if (streams[i] == null && selections[i] != null) {
                ChunkSampleStream<SsChunkSource> stream2 = buildSampleStream(selections[i], positionUs);
                sampleStreamsList.add(stream2);
                streams[i] = stream2;
                streamResetFlags[i] = true;
            }
        }
        int i2 = sampleStreamsList.size();
        ChunkSampleStream<SsChunkSource>[] newSampleStreamArray = newSampleStreamArray(i2);
        this.sampleStreams = newSampleStreamArray;
        sampleStreamsList.toArray(newSampleStreamArray);
        this.compositeSequenceableLoader = this.compositeSequenceableLoaderFactory.createCompositeSequenceableLoader(this.sampleStreams);
        return positionUs;
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public List<StreamKey> getStreamKeys(List<TrackSelection> trackSelections) {
        List<StreamKey> streamKeys = new ArrayList<>();
        for (int selectionIndex = 0; selectionIndex < trackSelections.size(); selectionIndex++) {
            TrackSelection trackSelection = trackSelections.get(selectionIndex);
            int streamElementIndex = this.trackGroups.indexOf(trackSelection.getTrackGroup());
            for (int i = 0; i < trackSelection.length(); i++) {
                streamKeys.add(new StreamKey(streamElementIndex, trackSelection.getIndexInTrackGroup(i)));
            }
        }
        return streamKeys;
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public void discardBuffer(long positionUs, boolean toKeyframe) {
        ChunkSampleStream<SsChunkSource>[] chunkSampleStreamArr;
        for (ChunkSampleStream<SsChunkSource> sampleStream : this.sampleStreams) {
            sampleStream.discardBuffer(positionUs, toKeyframe);
        }
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
    public void reevaluateBuffer(long positionUs) {
        this.compositeSequenceableLoader.reevaluateBuffer(positionUs);
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
    public boolean continueLoading(long positionUs) {
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
        if (!this.notifiedReadingStarted) {
            this.eventDispatcher.readingStarted();
            this.notifiedReadingStarted = true;
            return C.TIME_UNSET;
        }
        return C.TIME_UNSET;
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
    public long getBufferedPositionUs() {
        return this.compositeSequenceableLoader.getBufferedPositionUs();
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public long seekToUs(long positionUs) {
        ChunkSampleStream<SsChunkSource>[] chunkSampleStreamArr;
        for (ChunkSampleStream<SsChunkSource> sampleStream : this.sampleStreams) {
            sampleStream.seekToUs(positionUs);
        }
        return positionUs;
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public long getAdjustedSeekPositionUs(long positionUs, SeekParameters seekParameters) {
        ChunkSampleStream<SsChunkSource>[] chunkSampleStreamArr;
        for (ChunkSampleStream<SsChunkSource> sampleStream : this.sampleStreams) {
            if (sampleStream.primaryTrackType == 2) {
                return sampleStream.getAdjustedSeekPositionUs(positionUs, seekParameters);
            }
        }
        return positionUs;
    }

    public void onContinueLoadingRequested(ChunkSampleStream<SsChunkSource> sampleStream) {
        this.callback.onContinueLoadingRequested(this);
    }

    private ChunkSampleStream<SsChunkSource> buildSampleStream(TrackSelection selection, long positionUs) {
        int streamElementIndex = this.trackGroups.indexOf(selection.getTrackGroup());
        SsChunkSource chunkSource = this.chunkSourceFactory.createChunkSource(this.manifestLoaderErrorThrower, this.manifest, streamElementIndex, selection, this.transferListener);
        return new ChunkSampleStream<>(this.manifest.streamElements[streamElementIndex].type, null, null, chunkSource, this, this.allocator, positionUs, this.drmSessionManager, this.loadErrorHandlingPolicy, this.eventDispatcher);
    }

    private static TrackGroupArray buildTrackGroups(SsManifest manifest, DrmSessionManager<?> drmSessionManager) {
        Format format;
        TrackGroup[] trackGroups = new TrackGroup[manifest.streamElements.length];
        for (int i = 0; i < manifest.streamElements.length; i++) {
            Format[] manifestFormats = manifest.streamElements[i].formats;
            Format[] exposedFormats = new Format[manifestFormats.length];
            for (int j = 0; j < manifestFormats.length; j++) {
                Format manifestFormat = manifestFormats[j];
                if (manifestFormat.drmInitData != null) {
                    format = manifestFormat.copyWithExoMediaCryptoType(drmSessionManager.getExoMediaCryptoType(manifestFormat.drmInitData));
                } else {
                    format = manifestFormat;
                }
                exposedFormats[j] = format;
            }
            trackGroups[i] = new TrackGroup(exposedFormats);
        }
        return new TrackGroupArray(trackGroups);
    }

    private static ChunkSampleStream<SsChunkSource>[] newSampleStreamArray(int length) {
        return new ChunkSampleStream[length];
    }
}
