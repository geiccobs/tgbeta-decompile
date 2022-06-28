package com.google.android.exoplayer2.source.dash;

import android.net.Uri;
import android.os.SystemClock;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.extractor.ChunkIndex;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.extractor.mkv.MatroskaExtractor;
import com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor;
import com.google.android.exoplayer2.extractor.rawcc.RawCcExtractor;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.chunk.BaseMediaChunkIterator;
import com.google.android.exoplayer2.source.chunk.Chunk;
import com.google.android.exoplayer2.source.chunk.ChunkExtractorWrapper;
import com.google.android.exoplayer2.source.chunk.ChunkHolder;
import com.google.android.exoplayer2.source.chunk.ContainerMediaChunk;
import com.google.android.exoplayer2.source.chunk.InitializationChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunkIterator;
import com.google.android.exoplayer2.source.chunk.SingleSampleMediaChunk;
import com.google.android.exoplayer2.source.dash.DashChunkSource;
import com.google.android.exoplayer2.source.dash.PlayerEmsgHandler;
import com.google.android.exoplayer2.source.dash.manifest.AdaptationSet;
import com.google.android.exoplayer2.source.dash.manifest.DashManifest;
import com.google.android.exoplayer2.source.dash.manifest.RangedUri;
import com.google.android.exoplayer2.source.dash.manifest.Representation;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.LoaderErrorThrower;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes3.dex */
public class DefaultDashChunkSource implements DashChunkSource {
    private final int[] adaptationSetIndices;
    private final DataSource dataSource;
    private final long elapsedRealtimeOffsetMs;
    private IOException fatalError;
    private long liveEdgeTimeUs = C.TIME_UNSET;
    private DashManifest manifest;
    private final LoaderErrorThrower manifestLoaderErrorThrower;
    private final int maxSegmentsPerLoad;
    private boolean missingLastSegment;
    private int periodIndex;
    private final PlayerEmsgHandler.PlayerTrackEmsgHandler playerTrackEmsgHandler;
    protected final RepresentationHolder[] representationHolders;
    private TrackSelection trackSelection;
    private final int trackType;

    /* loaded from: classes3.dex */
    public static final class Factory implements DashChunkSource.Factory {
        private final DataSource.Factory dataSourceFactory;
        private final int maxSegmentsPerLoad;

        public Factory(DataSource.Factory dataSourceFactory) {
            this(dataSourceFactory, 1);
        }

        public Factory(DataSource.Factory dataSourceFactory, int maxSegmentsPerLoad) {
            this.dataSourceFactory = dataSourceFactory;
            this.maxSegmentsPerLoad = maxSegmentsPerLoad;
        }

        @Override // com.google.android.exoplayer2.source.dash.DashChunkSource.Factory
        public DashChunkSource createDashChunkSource(LoaderErrorThrower manifestLoaderErrorThrower, DashManifest manifest, int periodIndex, int[] adaptationSetIndices, TrackSelection trackSelection, int trackType, long elapsedRealtimeOffsetMs, boolean enableEventMessageTrack, List<Format> closedCaptionFormats, PlayerEmsgHandler.PlayerTrackEmsgHandler playerEmsgHandler, TransferListener transferListener) {
            DataSource dataSource = this.dataSourceFactory.createDataSource();
            if (transferListener != null) {
                dataSource.addTransferListener(transferListener);
            }
            return new DefaultDashChunkSource(manifestLoaderErrorThrower, manifest, periodIndex, adaptationSetIndices, trackSelection, trackType, dataSource, elapsedRealtimeOffsetMs, this.maxSegmentsPerLoad, enableEventMessageTrack, closedCaptionFormats, playerEmsgHandler);
        }
    }

    public DefaultDashChunkSource(LoaderErrorThrower manifestLoaderErrorThrower, DashManifest manifest, int periodIndex, int[] adaptationSetIndices, TrackSelection trackSelection, int trackType, DataSource dataSource, long elapsedRealtimeOffsetMs, int maxSegmentsPerLoad, boolean enableEventMessageTrack, List<Format> closedCaptionFormats, PlayerEmsgHandler.PlayerTrackEmsgHandler playerTrackEmsgHandler) {
        this.manifestLoaderErrorThrower = manifestLoaderErrorThrower;
        this.manifest = manifest;
        this.adaptationSetIndices = adaptationSetIndices;
        this.trackSelection = trackSelection;
        this.trackType = trackType;
        this.dataSource = dataSource;
        this.periodIndex = periodIndex;
        this.elapsedRealtimeOffsetMs = elapsedRealtimeOffsetMs;
        this.maxSegmentsPerLoad = maxSegmentsPerLoad;
        this.playerTrackEmsgHandler = playerTrackEmsgHandler;
        long periodDurationUs = manifest.getPeriodDurationUs(periodIndex);
        List<Representation> representations = getRepresentations();
        this.representationHolders = new RepresentationHolder[trackSelection.length()];
        int i = 0;
        while (i < this.representationHolders.length) {
            Representation representation = representations.get(trackSelection.getIndexInTrackGroup(i));
            int i2 = i;
            this.representationHolders[i2] = new RepresentationHolder(periodDurationUs, trackType, representation, enableEventMessageTrack, closedCaptionFormats, playerTrackEmsgHandler);
            i = i2 + 1;
            representations = representations;
        }
    }

    @Override // com.google.android.exoplayer2.source.chunk.ChunkSource
    public long getAdjustedSeekPositionUs(long positionUs, SeekParameters seekParameters) {
        RepresentationHolder[] representationHolderArr;
        long secondSyncUs;
        for (RepresentationHolder representationHolder : this.representationHolders) {
            if (representationHolder.segmentIndex != null) {
                long segmentNum = representationHolder.getSegmentNum(positionUs);
                long firstSyncUs = representationHolder.getSegmentStartTimeUs(segmentNum);
                if (firstSyncUs < positionUs && segmentNum < representationHolder.getSegmentCount() - 1) {
                    secondSyncUs = representationHolder.getSegmentStartTimeUs(1 + segmentNum);
                } else {
                    secondSyncUs = firstSyncUs;
                }
                return Util.resolveSeekPositionUs(positionUs, seekParameters, firstSyncUs, secondSyncUs);
            }
        }
        return positionUs;
    }

    @Override // com.google.android.exoplayer2.source.dash.DashChunkSource
    public void updateManifest(DashManifest newManifest, int newPeriodIndex) {
        try {
            this.manifest = newManifest;
            this.periodIndex = newPeriodIndex;
            long periodDurationUs = newManifest.getPeriodDurationUs(newPeriodIndex);
            List<Representation> representations = getRepresentations();
            for (int i = 0; i < this.representationHolders.length; i++) {
                Representation representation = representations.get(this.trackSelection.getIndexInTrackGroup(i));
                RepresentationHolder[] representationHolderArr = this.representationHolders;
                representationHolderArr[i] = representationHolderArr[i].copyWithNewRepresentation(periodDurationUs, representation);
            }
        } catch (BehindLiveWindowException e) {
            this.fatalError = e;
        }
    }

    @Override // com.google.android.exoplayer2.source.dash.DashChunkSource
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
    public void getNextChunk(long playbackPositionUs, long loadPositionUs, List<? extends MediaChunk> queue, ChunkHolder out) {
        ChunkHolder chunkHolder;
        int maxSegmentCount;
        RangedUri pendingInitializationUri;
        RangedUri pendingIndexUri;
        MediaChunkIterator[] chunkIterators;
        int i;
        if (this.fatalError != null) {
            return;
        }
        long bufferedDurationUs = loadPositionUs - playbackPositionUs;
        long timeToLiveEdgeUs = resolveTimeToLiveEdgeUs(playbackPositionUs);
        long presentationPositionUs = C.msToUs(this.manifest.availabilityStartTimeMs) + C.msToUs(this.manifest.getPeriod(this.periodIndex).startMs) + loadPositionUs;
        PlayerEmsgHandler.PlayerTrackEmsgHandler playerTrackEmsgHandler = this.playerTrackEmsgHandler;
        if (playerTrackEmsgHandler != null && playerTrackEmsgHandler.maybeRefreshManifestBeforeLoadingNextChunk(presentationPositionUs)) {
            return;
        }
        long nowUnixTimeUs = getNowUnixTimeUs();
        MediaChunk previous = queue.isEmpty() ? null : queue.get(queue.size() - 1);
        MediaChunkIterator[] chunkIterators2 = new MediaChunkIterator[this.trackSelection.length()];
        int i2 = 0;
        while (i2 < chunkIterators2.length) {
            RepresentationHolder representationHolder = this.representationHolders[i2];
            if (representationHolder.segmentIndex == null) {
                chunkIterators2[i2] = MediaChunkIterator.EMPTY;
                i = i2;
                chunkIterators = chunkIterators2;
            } else {
                long firstAvailableSegmentNum = representationHolder.getFirstAvailableSegmentNum(this.manifest, this.periodIndex, nowUnixTimeUs);
                long lastAvailableSegmentNum = representationHolder.getLastAvailableSegmentNum(this.manifest, this.periodIndex, nowUnixTimeUs);
                i = i2;
                chunkIterators = chunkIterators2;
                long segmentNum = getSegmentNum(representationHolder, previous, loadPositionUs, firstAvailableSegmentNum, lastAvailableSegmentNum);
                if (segmentNum < firstAvailableSegmentNum) {
                    chunkIterators[i] = MediaChunkIterator.EMPTY;
                } else {
                    chunkIterators[i] = new RepresentationSegmentIterator(representationHolder, segmentNum, lastAvailableSegmentNum);
                }
            }
            i2 = i + 1;
            chunkIterators2 = chunkIterators;
        }
        this.trackSelection.updateSelectedTrack(playbackPositionUs, bufferedDurationUs, timeToLiveEdgeUs, queue, chunkIterators2);
        RepresentationHolder representationHolder2 = this.representationHolders[this.trackSelection.getSelectedIndex()];
        if (representationHolder2.extractorWrapper != null) {
            Representation selectedRepresentation = representationHolder2.representation;
            if (representationHolder2.extractorWrapper.getSampleFormats() != null) {
                pendingInitializationUri = null;
            } else {
                pendingInitializationUri = selectedRepresentation.getInitializationUri();
            }
            if (representationHolder2.segmentIndex != null) {
                pendingIndexUri = null;
            } else {
                pendingIndexUri = selectedRepresentation.getIndexUri();
            }
            if (pendingInitializationUri != null || pendingIndexUri != null) {
                out.chunk = newInitializationChunk(representationHolder2, this.dataSource, this.trackSelection.getSelectedFormat(), this.trackSelection.getSelectionReason(), this.trackSelection.getSelectionData(), pendingInitializationUri, pendingIndexUri);
                return;
            }
        }
        long periodDurationUs = representationHolder2.periodDurationUs;
        long j = C.TIME_UNSET;
        boolean periodEnded = periodDurationUs != C.TIME_UNSET;
        if (representationHolder2.getSegmentCount() == 0) {
            out.endOfStream = periodEnded;
            return;
        }
        long firstAvailableSegmentNum2 = representationHolder2.getFirstAvailableSegmentNum(this.manifest, this.periodIndex, nowUnixTimeUs);
        long lastAvailableSegmentNum2 = representationHolder2.getLastAvailableSegmentNum(this.manifest, this.periodIndex, nowUnixTimeUs);
        updateLiveEdgeTimeUs(representationHolder2, lastAvailableSegmentNum2);
        long segmentNum2 = getSegmentNum(representationHolder2, previous, loadPositionUs, firstAvailableSegmentNum2, lastAvailableSegmentNum2);
        if (segmentNum2 < firstAvailableSegmentNum2) {
            this.fatalError = new BehindLiveWindowException();
            return;
        }
        if (segmentNum2 > lastAvailableSegmentNum2) {
            chunkHolder = out;
        } else if (!this.missingLastSegment || segmentNum2 < lastAvailableSegmentNum2) {
            if (periodEnded && representationHolder2.getSegmentStartTimeUs(segmentNum2) >= periodDurationUs) {
                out.endOfStream = true;
                return;
            }
            int maxSegmentCount2 = (int) Math.min(this.maxSegmentsPerLoad, (lastAvailableSegmentNum2 - segmentNum2) + 1);
            if (periodDurationUs == C.TIME_UNSET) {
                maxSegmentCount = maxSegmentCount2;
            } else {
                while (maxSegmentCount2 > 1 && representationHolder2.getSegmentStartTimeUs((maxSegmentCount2 + segmentNum2) - 1) >= periodDurationUs) {
                    maxSegmentCount2--;
                }
                maxSegmentCount = maxSegmentCount2;
            }
            if (queue.isEmpty()) {
                j = loadPositionUs;
            }
            long presentationPositionUs2 = j;
            out.chunk = newMediaChunk(representationHolder2, this.dataSource, this.trackType, this.trackSelection.getSelectedFormat(), this.trackSelection.getSelectionReason(), this.trackSelection.getSelectionData(), segmentNum2, maxSegmentCount, presentationPositionUs2);
            return;
        } else {
            chunkHolder = out;
        }
        chunkHolder.endOfStream = periodEnded;
    }

    @Override // com.google.android.exoplayer2.source.chunk.ChunkSource
    public void onChunkLoadCompleted(Chunk chunk) {
        SeekMap seekMap;
        if (chunk instanceof InitializationChunk) {
            InitializationChunk initializationChunk = (InitializationChunk) chunk;
            int trackIndex = this.trackSelection.indexOf(initializationChunk.trackFormat);
            RepresentationHolder representationHolder = this.representationHolders[trackIndex];
            if (representationHolder.segmentIndex == null && (seekMap = representationHolder.extractorWrapper.getSeekMap()) != null) {
                this.representationHolders[trackIndex] = representationHolder.copyWithNewSegmentIndex(new DashWrappingSegmentIndex((ChunkIndex) seekMap, representationHolder.representation.presentationTimeOffsetUs));
            }
        }
        PlayerEmsgHandler.PlayerTrackEmsgHandler playerTrackEmsgHandler = this.playerTrackEmsgHandler;
        if (playerTrackEmsgHandler != null) {
            playerTrackEmsgHandler.onChunkLoadCompleted(chunk);
        }
    }

    @Override // com.google.android.exoplayer2.source.chunk.ChunkSource
    public boolean onChunkLoadError(Chunk chunk, boolean cancelable, Exception e, long blacklistDurationMs) {
        RepresentationHolder representationHolder;
        int segmentCount;
        if (!cancelable) {
            return false;
        }
        PlayerEmsgHandler.PlayerTrackEmsgHandler playerTrackEmsgHandler = this.playerTrackEmsgHandler;
        if (playerTrackEmsgHandler != null && playerTrackEmsgHandler.maybeRefreshManifestOnLoadingError(chunk)) {
            return true;
        }
        if (!this.manifest.dynamic && (chunk instanceof MediaChunk) && (e instanceof HttpDataSource.InvalidResponseCodeException) && ((HttpDataSource.InvalidResponseCodeException) e).responseCode == 404 && (segmentCount = (representationHolder = this.representationHolders[this.trackSelection.indexOf(chunk.trackFormat)]).getSegmentCount()) != -1 && segmentCount != 0) {
            long lastAvailableSegmentNum = (representationHolder.getFirstSegmentNum() + segmentCount) - 1;
            if (((MediaChunk) chunk).getNextChunkIndex() > lastAvailableSegmentNum) {
                this.missingLastSegment = true;
                return true;
            }
        }
        if (blacklistDurationMs == C.TIME_UNSET) {
            return false;
        }
        TrackSelection trackSelection = this.trackSelection;
        return trackSelection.blacklist(trackSelection.indexOf(chunk.trackFormat), blacklistDurationMs);
    }

    private long getSegmentNum(RepresentationHolder representationHolder, MediaChunk previousChunk, long loadPositionUs, long firstAvailableSegmentNum, long lastAvailableSegmentNum) {
        if (previousChunk != null) {
            return previousChunk.getNextChunkIndex();
        }
        return Util.constrainValue(representationHolder.getSegmentNum(loadPositionUs), firstAvailableSegmentNum, lastAvailableSegmentNum);
    }

    private ArrayList<Representation> getRepresentations() {
        int[] iArr;
        List<AdaptationSet> manifestAdaptationSets = this.manifest.getPeriod(this.periodIndex).adaptationSets;
        ArrayList<Representation> representations = new ArrayList<>();
        for (int adaptationSetIndex : this.adaptationSetIndices) {
            representations.addAll(manifestAdaptationSets.get(adaptationSetIndex).representations);
        }
        return representations;
    }

    private void updateLiveEdgeTimeUs(RepresentationHolder representationHolder, long lastAvailableSegmentNum) {
        this.liveEdgeTimeUs = this.manifest.dynamic ? representationHolder.getSegmentEndTimeUs(lastAvailableSegmentNum) : C.TIME_UNSET;
    }

    private long getNowUnixTimeUs() {
        if (this.elapsedRealtimeOffsetMs != 0) {
            return (SystemClock.elapsedRealtime() + this.elapsedRealtimeOffsetMs) * 1000;
        }
        return System.currentTimeMillis() * 1000;
    }

    private long resolveTimeToLiveEdgeUs(long playbackPositionUs) {
        boolean resolveTimeToLiveEdgePossible = this.manifest.dynamic && this.liveEdgeTimeUs != C.TIME_UNSET;
        return resolveTimeToLiveEdgePossible ? this.liveEdgeTimeUs - playbackPositionUs : C.TIME_UNSET;
    }

    protected Chunk newInitializationChunk(RepresentationHolder representationHolder, DataSource dataSource, Format trackFormat, int trackSelectionReason, Object trackSelectionData, RangedUri initializationUri, RangedUri indexUri) {
        RangedUri requestUri;
        String baseUrl = representationHolder.representation.baseUrl;
        if (initializationUri != null) {
            requestUri = initializationUri.attemptMerge(indexUri, baseUrl);
            if (requestUri == null) {
                requestUri = initializationUri;
            }
        } else {
            requestUri = indexUri;
        }
        DataSpec dataSpec = new DataSpec(requestUri.resolveUri(baseUrl), requestUri.start, requestUri.length, representationHolder.representation.getCacheKey());
        return new InitializationChunk(dataSource, dataSpec, trackFormat, trackSelectionReason, trackSelectionData, representationHolder.extractorWrapper);
    }

    protected Chunk newMediaChunk(RepresentationHolder representationHolder, DataSource dataSource, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long firstSegmentNum, int maxSegmentCount, long seekTimeUs) {
        long clippedEndTimeUs;
        Representation representation = representationHolder.representation;
        long startTimeUs = representationHolder.getSegmentStartTimeUs(firstSegmentNum);
        RangedUri segmentUri = representationHolder.getSegmentUrl(firstSegmentNum);
        String baseUrl = representation.baseUrl;
        if (representationHolder.extractorWrapper == null) {
            long endTimeUs = representationHolder.getSegmentEndTimeUs(firstSegmentNum);
            DataSpec dataSpec = new DataSpec(segmentUri.resolveUri(baseUrl), segmentUri.start, segmentUri.length, representation.getCacheKey());
            return new SingleSampleMediaChunk(dataSource, dataSpec, trackFormat, trackSelectionReason, trackSelectionData, startTimeUs, endTimeUs, firstSegmentNum, trackType, trackFormat);
        }
        int segmentCount = 1;
        RangedUri segmentUri2 = segmentUri;
        for (int i = 1; i < maxSegmentCount; i++) {
            RangedUri nextSegmentUri = representationHolder.getSegmentUrl(firstSegmentNum + i);
            RangedUri mergedSegmentUri = segmentUri2.attemptMerge(nextSegmentUri, baseUrl);
            if (mergedSegmentUri == null) {
                break;
            }
            segmentUri2 = mergedSegmentUri;
            segmentCount++;
        }
        long endTimeUs2 = representationHolder.getSegmentEndTimeUs((firstSegmentNum + segmentCount) - 1);
        long periodDurationUs = representationHolder.periodDurationUs;
        if (periodDurationUs != C.TIME_UNSET && periodDurationUs <= endTimeUs2) {
            clippedEndTimeUs = periodDurationUs;
        } else {
            clippedEndTimeUs = -9223372036854775807L;
        }
        DataSpec dataSpec2 = new DataSpec(segmentUri2.resolveUri(baseUrl), segmentUri2.start, segmentUri2.length, representation.getCacheKey());
        long sampleOffsetUs = -representation.presentationTimeOffsetUs;
        return new ContainerMediaChunk(dataSource, dataSpec2, trackFormat, trackSelectionReason, trackSelectionData, startTimeUs, endTimeUs2, seekTimeUs, clippedEndTimeUs, firstSegmentNum, segmentCount, sampleOffsetUs, representationHolder.extractorWrapper);
    }

    /* loaded from: classes3.dex */
    protected static final class RepresentationSegmentIterator extends BaseMediaChunkIterator {
        private final RepresentationHolder representationHolder;

        public RepresentationSegmentIterator(RepresentationHolder representation, long firstAvailableSegmentNum, long lastAvailableSegmentNum) {
            super(firstAvailableSegmentNum, lastAvailableSegmentNum);
            this.representationHolder = representation;
        }

        @Override // com.google.android.exoplayer2.source.chunk.MediaChunkIterator
        public DataSpec getDataSpec() {
            checkInBounds();
            Representation representation = this.representationHolder.representation;
            RangedUri segmentUri = this.representationHolder.getSegmentUrl(getCurrentIndex());
            Uri resolvedUri = segmentUri.resolveUri(representation.baseUrl);
            String cacheKey = representation.getCacheKey();
            return new DataSpec(resolvedUri, segmentUri.start, segmentUri.length, cacheKey);
        }

        @Override // com.google.android.exoplayer2.source.chunk.MediaChunkIterator
        public long getChunkStartTimeUs() {
            checkInBounds();
            return this.representationHolder.getSegmentStartTimeUs(getCurrentIndex());
        }

        @Override // com.google.android.exoplayer2.source.chunk.MediaChunkIterator
        public long getChunkEndTimeUs() {
            checkInBounds();
            return this.representationHolder.getSegmentEndTimeUs(getCurrentIndex());
        }
    }

    /* loaded from: classes3.dex */
    public static final class RepresentationHolder {
        final ChunkExtractorWrapper extractorWrapper;
        private final long periodDurationUs;
        public final Representation representation;
        public final DashSegmentIndex segmentIndex;
        private final long segmentNumShift;

        RepresentationHolder(long periodDurationUs, int trackType, Representation representation, boolean enableEventMessageTrack, List<Format> closedCaptionFormats, TrackOutput playerEmsgTrackOutput) {
            this(periodDurationUs, representation, createExtractorWrapper(trackType, representation, enableEventMessageTrack, closedCaptionFormats, playerEmsgTrackOutput), 0L, representation.getIndex());
        }

        private RepresentationHolder(long periodDurationUs, Representation representation, ChunkExtractorWrapper extractorWrapper, long segmentNumShift, DashSegmentIndex segmentIndex) {
            this.periodDurationUs = periodDurationUs;
            this.representation = representation;
            this.segmentNumShift = segmentNumShift;
            this.extractorWrapper = extractorWrapper;
            this.segmentIndex = segmentIndex;
        }

        RepresentationHolder copyWithNewRepresentation(long newPeriodDurationUs, Representation newRepresentation) throws BehindLiveWindowException {
            long newSegmentNumShift;
            DashSegmentIndex oldIndex = this.representation.getIndex();
            DashSegmentIndex newIndex = newRepresentation.getIndex();
            if (oldIndex == null) {
                return new RepresentationHolder(newPeriodDurationUs, newRepresentation, this.extractorWrapper, this.segmentNumShift, oldIndex);
            }
            if (!oldIndex.isExplicit()) {
                return new RepresentationHolder(newPeriodDurationUs, newRepresentation, this.extractorWrapper, this.segmentNumShift, newIndex);
            }
            int oldIndexSegmentCount = oldIndex.getSegmentCount(newPeriodDurationUs);
            if (oldIndexSegmentCount == 0) {
                return new RepresentationHolder(newPeriodDurationUs, newRepresentation, this.extractorWrapper, this.segmentNumShift, newIndex);
            }
            long oldIndexFirstSegmentNum = oldIndex.getFirstSegmentNum();
            long oldIndexStartTimeUs = oldIndex.getTimeUs(oldIndexFirstSegmentNum);
            long oldIndexLastSegmentNum = (oldIndexSegmentCount + oldIndexFirstSegmentNum) - 1;
            long oldIndexEndTimeUs = oldIndex.getTimeUs(oldIndexLastSegmentNum) + oldIndex.getDurationUs(oldIndexLastSegmentNum, newPeriodDurationUs);
            long newIndexFirstSegmentNum = newIndex.getFirstSegmentNum();
            long newIndexStartTimeUs = newIndex.getTimeUs(newIndexFirstSegmentNum);
            long newSegmentNumShift2 = this.segmentNumShift;
            if (oldIndexEndTimeUs == newIndexStartTimeUs) {
                newSegmentNumShift = newSegmentNumShift2 + ((oldIndexLastSegmentNum + 1) - newIndexFirstSegmentNum);
            } else if (oldIndexEndTimeUs < newIndexStartTimeUs) {
                throw new BehindLiveWindowException();
            } else {
                if (newIndexStartTimeUs < oldIndexStartTimeUs) {
                    newSegmentNumShift = newSegmentNumShift2 - (newIndex.getSegmentNum(oldIndexStartTimeUs, newPeriodDurationUs) - oldIndexFirstSegmentNum);
                } else {
                    newSegmentNumShift = newSegmentNumShift2 + (oldIndex.getSegmentNum(newIndexStartTimeUs, newPeriodDurationUs) - newIndexFirstSegmentNum);
                }
            }
            return new RepresentationHolder(newPeriodDurationUs, newRepresentation, this.extractorWrapper, newSegmentNumShift, newIndex);
        }

        RepresentationHolder copyWithNewSegmentIndex(DashSegmentIndex segmentIndex) {
            return new RepresentationHolder(this.periodDurationUs, this.representation, this.extractorWrapper, this.segmentNumShift, segmentIndex);
        }

        public long getFirstSegmentNum() {
            return this.segmentIndex.getFirstSegmentNum() + this.segmentNumShift;
        }

        public int getSegmentCount() {
            return this.segmentIndex.getSegmentCount(this.periodDurationUs);
        }

        public long getSegmentStartTimeUs(long segmentNum) {
            return this.segmentIndex.getTimeUs(segmentNum - this.segmentNumShift);
        }

        public long getSegmentEndTimeUs(long segmentNum) {
            return getSegmentStartTimeUs(segmentNum) + this.segmentIndex.getDurationUs(segmentNum - this.segmentNumShift, this.periodDurationUs);
        }

        public long getSegmentNum(long positionUs) {
            return this.segmentIndex.getSegmentNum(positionUs, this.periodDurationUs) + this.segmentNumShift;
        }

        public RangedUri getSegmentUrl(long segmentNum) {
            return this.segmentIndex.getSegmentUrl(segmentNum - this.segmentNumShift);
        }

        public long getFirstAvailableSegmentNum(DashManifest manifest, int periodIndex, long nowUnixTimeUs) {
            if (getSegmentCount() == -1 && manifest.timeShiftBufferDepthMs != C.TIME_UNSET) {
                long liveEdgeTimeUs = nowUnixTimeUs - C.msToUs(manifest.availabilityStartTimeMs);
                long periodStartUs = C.msToUs(manifest.getPeriod(periodIndex).startMs);
                long liveEdgeTimeInPeriodUs = liveEdgeTimeUs - periodStartUs;
                long bufferDepthUs = C.msToUs(manifest.timeShiftBufferDepthMs);
                return Math.max(getFirstSegmentNum(), getSegmentNum(liveEdgeTimeInPeriodUs - bufferDepthUs));
            }
            return getFirstSegmentNum();
        }

        public long getLastAvailableSegmentNum(DashManifest manifest, int periodIndex, long nowUnixTimeUs) {
            int availableSegmentCount = getSegmentCount();
            if (availableSegmentCount == -1) {
                long liveEdgeTimeUs = nowUnixTimeUs - C.msToUs(manifest.availabilityStartTimeMs);
                long periodStartUs = C.msToUs(manifest.getPeriod(periodIndex).startMs);
                long liveEdgeTimeInPeriodUs = liveEdgeTimeUs - periodStartUs;
                return getSegmentNum(liveEdgeTimeInPeriodUs) - 1;
            }
            long liveEdgeTimeUs2 = getFirstSegmentNum();
            return (liveEdgeTimeUs2 + availableSegmentCount) - 1;
        }

        private static boolean mimeTypeIsWebm(String mimeType) {
            return mimeType.startsWith(MimeTypes.VIDEO_WEBM) || mimeType.startsWith(MimeTypes.AUDIO_WEBM) || mimeType.startsWith(MimeTypes.APPLICATION_WEBM);
        }

        private static boolean mimeTypeIsRawText(String mimeType) {
            return MimeTypes.isText(mimeType) || MimeTypes.APPLICATION_TTML.equals(mimeType);
        }

        private static ChunkExtractorWrapper createExtractorWrapper(int trackType, Representation representation, boolean enableEventMessageTrack, List<Format> closedCaptionFormats, TrackOutput playerEmsgTrackOutput) {
            Extractor extractor;
            String containerMimeType = representation.format.containerMimeType;
            if (mimeTypeIsRawText(containerMimeType)) {
                return null;
            }
            if (MimeTypes.APPLICATION_RAWCC.equals(containerMimeType)) {
                extractor = new RawCcExtractor(representation.format);
            } else if (mimeTypeIsWebm(containerMimeType)) {
                extractor = new MatroskaExtractor(1);
            } else {
                int flags = 0;
                if (enableEventMessageTrack) {
                    flags = 0 | 4;
                }
                extractor = new FragmentedMp4Extractor(flags, null, null, closedCaptionFormats, playerEmsgTrackOutput);
            }
            return new ChunkExtractorWrapper(extractor, trackType, representation.format);
        }
    }
}
