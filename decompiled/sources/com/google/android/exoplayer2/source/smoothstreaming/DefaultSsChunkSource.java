package com.google.android.exoplayer2.source.smoothstreaming;

import android.net.Uri;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor;
import com.google.android.exoplayer2.extractor.mp4.Track;
import com.google.android.exoplayer2.extractor.mp4.TrackEncryptionBox;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.chunk.BaseMediaChunkIterator;
import com.google.android.exoplayer2.source.chunk.Chunk;
import com.google.android.exoplayer2.source.chunk.ChunkExtractorWrapper;
import com.google.android.exoplayer2.source.chunk.ChunkHolder;
import com.google.android.exoplayer2.source.chunk.ContainerMediaChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunkIterator;
import com.google.android.exoplayer2.source.smoothstreaming.SsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifest;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.LoaderErrorThrower;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.List;
/* loaded from: classes3.dex */
public class DefaultSsChunkSource implements SsChunkSource {
    private int currentManifestChunkOffset;
    private final DataSource dataSource;
    private final ChunkExtractorWrapper[] extractorWrappers;
    private IOException fatalError;
    private SsManifest manifest;
    private final LoaderErrorThrower manifestLoaderErrorThrower;
    private final int streamElementIndex;
    private TrackSelection trackSelection;

    /* loaded from: classes3.dex */
    public static final class Factory implements SsChunkSource.Factory {
        private final DataSource.Factory dataSourceFactory;

        public Factory(DataSource.Factory dataSourceFactory) {
            this.dataSourceFactory = dataSourceFactory;
        }

        @Override // com.google.android.exoplayer2.source.smoothstreaming.SsChunkSource.Factory
        public SsChunkSource createChunkSource(LoaderErrorThrower manifestLoaderErrorThrower, SsManifest manifest, int elementIndex, TrackSelection trackSelection, TransferListener transferListener) {
            DataSource dataSource = this.dataSourceFactory.createDataSource();
            if (transferListener != null) {
                dataSource.addTransferListener(transferListener);
            }
            return new DefaultSsChunkSource(manifestLoaderErrorThrower, manifest, elementIndex, trackSelection, dataSource);
        }
    }

    public DefaultSsChunkSource(LoaderErrorThrower manifestLoaderErrorThrower, SsManifest manifest, int streamElementIndex, TrackSelection trackSelection, DataSource dataSource) {
        SsManifest ssManifest = manifest;
        this.manifestLoaderErrorThrower = manifestLoaderErrorThrower;
        this.manifest = ssManifest;
        this.streamElementIndex = streamElementIndex;
        this.trackSelection = trackSelection;
        this.dataSource = dataSource;
        SsManifest.StreamElement streamElement = ssManifest.streamElements[streamElementIndex];
        this.extractorWrappers = new ChunkExtractorWrapper[trackSelection.length()];
        int i = 0;
        while (i < this.extractorWrappers.length) {
            int manifestTrackIndex = trackSelection.getIndexInTrackGroup(i);
            Format format = streamElement.formats[manifestTrackIndex];
            TrackEncryptionBox[] trackEncryptionBoxes = format.drmInitData != null ? ssManifest.protectionElement.trackEncryptionBoxes : null;
            int nalUnitLengthFieldLength = streamElement.type == 2 ? 4 : 0;
            Track track = new Track(manifestTrackIndex, streamElement.type, streamElement.timescale, C.TIME_UNSET, ssManifest.durationUs, format, 0, trackEncryptionBoxes, nalUnitLengthFieldLength, null, null);
            FragmentedMp4Extractor extractor = new FragmentedMp4Extractor(3, null, track);
            this.extractorWrappers[i] = new ChunkExtractorWrapper(extractor, streamElement.type, format);
            i++;
            ssManifest = manifest;
        }
    }

    @Override // com.google.android.exoplayer2.source.chunk.ChunkSource
    public long getAdjustedSeekPositionUs(long positionUs, SeekParameters seekParameters) {
        long secondSyncUs;
        SsManifest.StreamElement streamElement = this.manifest.streamElements[this.streamElementIndex];
        int chunkIndex = streamElement.getChunkIndex(positionUs);
        long firstSyncUs = streamElement.getStartTimeUs(chunkIndex);
        if (firstSyncUs < positionUs && chunkIndex < streamElement.chunkCount - 1) {
            secondSyncUs = streamElement.getStartTimeUs(chunkIndex + 1);
        } else {
            secondSyncUs = firstSyncUs;
        }
        return Util.resolveSeekPositionUs(positionUs, seekParameters, firstSyncUs, secondSyncUs);
    }

    @Override // com.google.android.exoplayer2.source.smoothstreaming.SsChunkSource
    public void updateManifest(SsManifest newManifest) {
        SsManifest.StreamElement currentElement = this.manifest.streamElements[this.streamElementIndex];
        int currentElementChunkCount = currentElement.chunkCount;
        SsManifest.StreamElement newElement = newManifest.streamElements[this.streamElementIndex];
        if (currentElementChunkCount == 0 || newElement.chunkCount == 0) {
            this.currentManifestChunkOffset += currentElementChunkCount;
        } else {
            long currentElementEndTimeUs = currentElement.getStartTimeUs(currentElementChunkCount - 1) + currentElement.getChunkDurationUs(currentElementChunkCount - 1);
            long newElementStartTimeUs = newElement.getStartTimeUs(0);
            if (currentElementEndTimeUs <= newElementStartTimeUs) {
                this.currentManifestChunkOffset += currentElementChunkCount;
            } else {
                this.currentManifestChunkOffset += currentElement.getChunkIndex(newElementStartTimeUs);
            }
        }
        this.manifest = newManifest;
    }

    @Override // com.google.android.exoplayer2.source.smoothstreaming.SsChunkSource
    public void updateTrackSelection(TrackSelection trackSelection) {
        this.trackSelection = trackSelection;
    }

    @Override // com.google.android.exoplayer2.source.chunk.ChunkSource
    public void maybeThrowError() throws IOException {
        IOException iOException = this.fatalError;
        if (iOException != null) {
            throw iOException;
        }
        this.manifestLoaderErrorThrower.maybeThrowError();
    }

    @Override // com.google.android.exoplayer2.source.chunk.ChunkSource
    public int getPreferredQueueSize(long playbackPositionUs, List<? extends MediaChunk> queue) {
        if (this.fatalError != null || this.trackSelection.length() < 2) {
            return queue.size();
        }
        return this.trackSelection.evaluateQueueSize(playbackPositionUs, queue);
    }

    @Override // com.google.android.exoplayer2.source.chunk.ChunkSource
    public final void getNextChunk(long playbackPositionUs, long loadPositionUs, List<? extends MediaChunk> queue, ChunkHolder out) {
        int chunkIndex;
        if (this.fatalError == null) {
            SsManifest.StreamElement streamElement = this.manifest.streamElements[this.streamElementIndex];
            if (streamElement.chunkCount == 0) {
                out.endOfStream = !this.manifest.isLive;
                return;
            }
            if (!queue.isEmpty()) {
                chunkIndex = (int) (queue.get(queue.size() - 1).getNextChunkIndex() - this.currentManifestChunkOffset);
                if (chunkIndex < 0) {
                    this.fatalError = new BehindLiveWindowException();
                    return;
                }
            } else {
                chunkIndex = streamElement.getChunkIndex(loadPositionUs);
            }
            if (chunkIndex >= streamElement.chunkCount) {
                out.endOfStream = !this.manifest.isLive;
                return;
            }
            long bufferedDurationUs = loadPositionUs - playbackPositionUs;
            long timeToLiveEdgeUs = resolveTimeToLiveEdgeUs(playbackPositionUs);
            MediaChunkIterator[] chunkIterators = new MediaChunkIterator[this.trackSelection.length()];
            for (int i = 0; i < chunkIterators.length; i++) {
                int trackIndex = this.trackSelection.getIndexInTrackGroup(i);
                chunkIterators[i] = new StreamElementIterator(streamElement, trackIndex, chunkIndex);
            }
            this.trackSelection.updateSelectedTrack(playbackPositionUs, bufferedDurationUs, timeToLiveEdgeUs, queue, chunkIterators);
            long chunkStartTimeUs = streamElement.getStartTimeUs(chunkIndex);
            long chunkEndTimeUs = streamElement.getChunkDurationUs(chunkIndex) + chunkStartTimeUs;
            long chunkSeekTimeUs = queue.isEmpty() ? loadPositionUs : -9223372036854775807L;
            int currentAbsoluteChunkIndex = this.currentManifestChunkOffset + chunkIndex;
            int trackSelectionIndex = this.trackSelection.getSelectedIndex();
            ChunkExtractorWrapper extractorWrapper = this.extractorWrappers[trackSelectionIndex];
            int manifestTrackIndex = this.trackSelection.getIndexInTrackGroup(trackSelectionIndex);
            Uri uri = streamElement.buildRequestUri(manifestTrackIndex, chunkIndex);
            out.chunk = newMediaChunk(this.trackSelection.getSelectedFormat(), this.dataSource, uri, null, currentAbsoluteChunkIndex, chunkStartTimeUs, chunkEndTimeUs, chunkSeekTimeUs, this.trackSelection.getSelectionReason(), this.trackSelection.getSelectionData(), extractorWrapper);
        }
    }

    @Override // com.google.android.exoplayer2.source.chunk.ChunkSource
    public void onChunkLoadCompleted(Chunk chunk) {
    }

    @Override // com.google.android.exoplayer2.source.chunk.ChunkSource
    public boolean onChunkLoadError(Chunk chunk, boolean cancelable, Exception e, long blacklistDurationMs) {
        if (cancelable && blacklistDurationMs != C.TIME_UNSET) {
            TrackSelection trackSelection = this.trackSelection;
            if (trackSelection.blacklist(trackSelection.indexOf(chunk.trackFormat), blacklistDurationMs)) {
                return true;
            }
        }
        return false;
    }

    private static MediaChunk newMediaChunk(Format format, DataSource dataSource, Uri uri, String cacheKey, int chunkIndex, long chunkStartTimeUs, long chunkEndTimeUs, long chunkSeekTimeUs, int trackSelectionReason, Object trackSelectionData, ChunkExtractorWrapper extractorWrapper) {
        DataSpec dataSpec = new DataSpec(uri, 0L, -1L, cacheKey);
        return new ContainerMediaChunk(dataSource, dataSpec, format, trackSelectionReason, trackSelectionData, chunkStartTimeUs, chunkEndTimeUs, chunkSeekTimeUs, C.TIME_UNSET, chunkIndex, 1, chunkStartTimeUs, extractorWrapper);
    }

    private long resolveTimeToLiveEdgeUs(long playbackPositionUs) {
        if (!this.manifest.isLive) {
            return C.TIME_UNSET;
        }
        SsManifest.StreamElement currentElement = this.manifest.streamElements[this.streamElementIndex];
        int lastChunkIndex = currentElement.chunkCount - 1;
        long lastChunkEndTimeUs = currentElement.getStartTimeUs(lastChunkIndex) + currentElement.getChunkDurationUs(lastChunkIndex);
        return lastChunkEndTimeUs - playbackPositionUs;
    }

    /* loaded from: classes3.dex */
    private static final class StreamElementIterator extends BaseMediaChunkIterator {
        private final SsManifest.StreamElement streamElement;
        private final int trackIndex;

        public StreamElementIterator(SsManifest.StreamElement streamElement, int trackIndex, int chunkIndex) {
            super(chunkIndex, streamElement.chunkCount - 1);
            this.streamElement = streamElement;
            this.trackIndex = trackIndex;
        }

        @Override // com.google.android.exoplayer2.source.chunk.MediaChunkIterator
        public DataSpec getDataSpec() {
            checkInBounds();
            Uri uri = this.streamElement.buildRequestUri(this.trackIndex, (int) getCurrentIndex());
            return new DataSpec(uri);
        }

        @Override // com.google.android.exoplayer2.source.chunk.MediaChunkIterator
        public long getChunkStartTimeUs() {
            checkInBounds();
            return this.streamElement.getStartTimeUs((int) getCurrentIndex());
        }

        @Override // com.google.android.exoplayer2.source.chunk.MediaChunkIterator
        public long getChunkEndTimeUs() {
            long chunkStartTimeUs = getChunkStartTimeUs();
            return this.streamElement.getChunkDurationUs((int) getCurrentIndex()) + chunkStartTimeUs;
        }
    }
}
