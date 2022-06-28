package com.google.android.exoplayer2.source.hls;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseIntArray;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.extractor.DummyTrackOutput;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.emsg.EventMessage;
import com.google.android.exoplayer2.metadata.emsg.EventMessageDecoder;
import com.google.android.exoplayer2.metadata.id3.PrivFrame;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.SampleQueue;
import com.google.android.exoplayer2.source.SampleStream;
import com.google.android.exoplayer2.source.SequenceableLoader;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.chunk.Chunk;
import com.google.android.exoplayer2.source.hls.HlsChunkSource;
import com.google.android.exoplayer2.source.hls.HlsSampleStreamWrapper;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.Loader;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;
/* loaded from: classes3.dex */
public final class HlsSampleStreamWrapper implements Loader.Callback<Chunk>, Loader.ReleaseCallback, SequenceableLoader, ExtractorOutput, SampleQueue.UpstreamFormatChangedListener {
    private static final Set<Integer> MAPPABLE_TYPES = Collections.unmodifiableSet(new HashSet(Arrays.asList(1, 2, 4)));
    public static final int SAMPLE_QUEUE_INDEX_NO_MAPPING_FATAL = -2;
    public static final int SAMPLE_QUEUE_INDEX_NO_MAPPING_NON_FATAL = -3;
    public static final int SAMPLE_QUEUE_INDEX_PENDING = -1;
    private static final String TAG = "HlsSampleStreamWrapper";
    private final Allocator allocator;
    private final Callback callback;
    private final HlsChunkSource chunkSource;
    private int chunkUid;
    private Format downstreamTrackFormat;
    private DrmInitData drmInitData;
    private final DrmSessionManager<?> drmSessionManager;
    private TrackOutput emsgUnwrappingTrackOutput;
    private int enabledTrackGroupCount;
    private final MediaSourceEventListener.EventDispatcher eventDispatcher;
    private boolean haveAudioVideoSampleQueues;
    private long lastSeekPositionUs;
    private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
    private boolean loadingFinished;
    private final Runnable maybeFinishPrepareRunnable;
    private final ArrayList<HlsMediaChunk> mediaChunks;
    private final int metadataType;
    private final Format muxedAudioFormat;
    private final Runnable onTracksEndedRunnable;
    private Set<TrackGroup> optionalTrackGroups;
    private final Map<String, DrmInitData> overridingDrmInitData;
    private long pendingResetPositionUs;
    private boolean pendingResetUpstreamFormats;
    private boolean prepared;
    private int primarySampleQueueIndex;
    private int primarySampleQueueType;
    private int primaryTrackGroupIndex;
    private final List<HlsMediaChunk> readOnlyMediaChunks;
    private boolean released;
    private long sampleOffsetUs;
    private SparseIntArray sampleQueueIndicesByType;
    private Set<Integer> sampleQueueMappingDoneByType;
    private boolean sampleQueuesBuilt;
    private boolean seenFirstTrackSelection;
    private int[] trackGroupToSampleQueueIndex;
    private TrackGroupArray trackGroups;
    private final int trackType;
    private boolean tracksEnded;
    private Format upstreamTrackFormat;
    private final Loader loader = new Loader("Loader:HlsSampleStreamWrapper");
    private final HlsChunkSource.HlsChunkHolder nextChunkHolder = new HlsChunkSource.HlsChunkHolder();
    private int[] sampleQueueTrackIds = new int[0];
    private FormatAdjustingSampleQueue[] sampleQueues = new FormatAdjustingSampleQueue[0];
    private boolean[] sampleQueueIsAudioVideoFlags = new boolean[0];
    private boolean[] sampleQueuesEnabledStates = new boolean[0];
    private final ArrayList<HlsSampleStream> hlsSampleStreams = new ArrayList<>();
    private final Handler handler = new Handler();

    /* loaded from: classes3.dex */
    public interface Callback extends SequenceableLoader.Callback<HlsSampleStreamWrapper> {
        void onPlaylistRefreshRequired(Uri uri);

        void onPrepared();
    }

    public HlsSampleStreamWrapper(int trackType, Callback callback, HlsChunkSource chunkSource, Map<String, DrmInitData> overridingDrmInitData, Allocator allocator, long positionUs, Format muxedAudioFormat, DrmSessionManager<?> drmSessionManager, LoadErrorHandlingPolicy loadErrorHandlingPolicy, MediaSourceEventListener.EventDispatcher eventDispatcher, int metadataType) {
        this.trackType = trackType;
        this.callback = callback;
        this.chunkSource = chunkSource;
        this.overridingDrmInitData = overridingDrmInitData;
        this.allocator = allocator;
        this.muxedAudioFormat = muxedAudioFormat;
        this.drmSessionManager = drmSessionManager;
        this.loadErrorHandlingPolicy = loadErrorHandlingPolicy;
        this.eventDispatcher = eventDispatcher;
        this.metadataType = metadataType;
        Set<Integer> set = MAPPABLE_TYPES;
        this.sampleQueueMappingDoneByType = new HashSet(set.size());
        this.sampleQueueIndicesByType = new SparseIntArray(set.size());
        ArrayList<HlsMediaChunk> arrayList = new ArrayList<>();
        this.mediaChunks = arrayList;
        this.readOnlyMediaChunks = Collections.unmodifiableList(arrayList);
        Runnable maybeFinishPrepareRunnable = new Runnable() { // from class: com.google.android.exoplayer2.source.hls.HlsSampleStreamWrapper$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                HlsSampleStreamWrapper.this.maybeFinishPrepare();
            }
        };
        this.maybeFinishPrepareRunnable = maybeFinishPrepareRunnable;
        Runnable onTracksEndedRunnable = new Runnable() { // from class: com.google.android.exoplayer2.source.hls.HlsSampleStreamWrapper$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                HlsSampleStreamWrapper.this.onTracksEnded();
            }
        };
        this.onTracksEndedRunnable = onTracksEndedRunnable;
        this.lastSeekPositionUs = positionUs;
        this.pendingResetPositionUs = positionUs;
    }

    public void continuePreparing() {
        if (!this.prepared) {
            continueLoading(this.lastSeekPositionUs);
        }
    }

    public void prepareWithMasterPlaylistInfo(TrackGroup[] trackGroups, int primaryTrackGroupIndex, int... optionalTrackGroupsIndices) {
        this.trackGroups = createTrackGroupArrayWithDrmInfo(trackGroups);
        this.optionalTrackGroups = new HashSet();
        for (int optionalTrackGroupIndex : optionalTrackGroupsIndices) {
            this.optionalTrackGroups.add(this.trackGroups.get(optionalTrackGroupIndex));
        }
        this.primaryTrackGroupIndex = primaryTrackGroupIndex;
        Handler handler = this.handler;
        final Callback callback = this.callback;
        callback.getClass();
        handler.post(new Runnable() { // from class: com.google.android.exoplayer2.source.hls.HlsSampleStreamWrapper$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                HlsSampleStreamWrapper.Callback.this.onPrepared();
            }
        });
        setIsPrepared();
    }

    public void maybeThrowPrepareError() throws IOException {
        maybeThrowError();
        if (this.loadingFinished && !this.prepared) {
            throw new ParserException("Loading finished before preparation is complete.");
        }
    }

    public TrackGroupArray getTrackGroups() {
        assertIsPrepared();
        return this.trackGroups;
    }

    public int getPrimaryTrackGroupIndex() {
        return this.primaryTrackGroupIndex;
    }

    public int bindSampleQueueToSampleStream(int trackGroupIndex) {
        assertIsPrepared();
        Assertions.checkNotNull(this.trackGroupToSampleQueueIndex);
        int sampleQueueIndex = this.trackGroupToSampleQueueIndex[trackGroupIndex];
        if (sampleQueueIndex == -1) {
            if (!this.optionalTrackGroups.contains(this.trackGroups.get(trackGroupIndex))) {
                return -2;
            }
            return -3;
        }
        boolean[] zArr = this.sampleQueuesEnabledStates;
        if (zArr[sampleQueueIndex]) {
            return -2;
        }
        zArr[sampleQueueIndex] = true;
        return sampleQueueIndex;
    }

    public void unbindSampleQueue(int trackGroupIndex) {
        assertIsPrepared();
        Assertions.checkNotNull(this.trackGroupToSampleQueueIndex);
        int sampleQueueIndex = this.trackGroupToSampleQueueIndex[trackGroupIndex];
        Assertions.checkState(this.sampleQueuesEnabledStates[sampleQueueIndex]);
        this.sampleQueuesEnabledStates[sampleQueueIndex] = false;
    }

    /* JADX WARN: Removed duplicated region for block: B:75:0x0153  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean selectTracks(com.google.android.exoplayer2.trackselection.TrackSelection[] r23, boolean[] r24, com.google.android.exoplayer2.source.SampleStream[] r25, boolean[] r26, long r27, boolean r29) {
        /*
            Method dump skipped, instructions count: 363
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.hls.HlsSampleStreamWrapper.selectTracks(com.google.android.exoplayer2.trackselection.TrackSelection[], boolean[], com.google.android.exoplayer2.source.SampleStream[], boolean[], long, boolean):boolean");
    }

    public void discardBuffer(long positionUs, boolean toKeyframe) {
        if (!this.sampleQueuesBuilt || isPendingReset()) {
            return;
        }
        int sampleQueueCount = this.sampleQueues.length;
        for (int i = 0; i < sampleQueueCount; i++) {
            this.sampleQueues[i].discardTo(positionUs, toKeyframe, this.sampleQueuesEnabledStates[i]);
        }
    }

    public boolean seekToUs(long positionUs, boolean forceReset) {
        this.lastSeekPositionUs = positionUs;
        if (isPendingReset()) {
            this.pendingResetPositionUs = positionUs;
            return true;
        } else if (this.sampleQueuesBuilt && !forceReset && seekInsideBufferUs(positionUs)) {
            return false;
        } else {
            this.pendingResetPositionUs = positionUs;
            this.loadingFinished = false;
            this.mediaChunks.clear();
            if (this.loader.isLoading()) {
                this.loader.cancelLoading();
            } else {
                this.loader.clearFatalError();
                resetSampleQueues();
            }
            return true;
        }
    }

    public void release() {
        SampleQueue[] sampleQueueArr;
        if (this.prepared) {
            for (SampleQueue sampleQueue : this.sampleQueues) {
                sampleQueue.preRelease();
            }
        }
        this.loader.release(this);
        this.handler.removeCallbacksAndMessages(null);
        this.released = true;
        this.hlsSampleStreams.clear();
    }

    @Override // com.google.android.exoplayer2.upstream.Loader.ReleaseCallback
    public void onLoaderReleased() {
        SampleQueue[] sampleQueueArr;
        for (SampleQueue sampleQueue : this.sampleQueues) {
            sampleQueue.release();
        }
    }

    public void setIsTimestampMaster(boolean isTimestampMaster) {
        this.chunkSource.setIsTimestampMaster(isTimestampMaster);
    }

    public boolean onPlaylistError(Uri playlistUrl, long blacklistDurationMs) {
        return this.chunkSource.onPlaylistError(playlistUrl, blacklistDurationMs);
    }

    public boolean isReady(int sampleQueueIndex) {
        return !isPendingReset() && this.sampleQueues[sampleQueueIndex].isReady(this.loadingFinished);
    }

    public void maybeThrowError(int sampleQueueIndex) throws IOException {
        maybeThrowError();
        this.sampleQueues[sampleQueueIndex].maybeThrowError();
    }

    public void maybeThrowError() throws IOException {
        this.loader.maybeThrowError();
        this.chunkSource.maybeThrowError();
    }

    public int readData(int sampleQueueIndex, FormatHolder formatHolder, DecoderInputBuffer buffer, boolean requireFormat) {
        Format trackFormat;
        if (isPendingReset()) {
            return -3;
        }
        if (!this.mediaChunks.isEmpty()) {
            int discardToMediaChunkIndex = 0;
            while (discardToMediaChunkIndex < this.mediaChunks.size() - 1 && finishedReadingChunk(this.mediaChunks.get(discardToMediaChunkIndex))) {
                discardToMediaChunkIndex++;
            }
            Util.removeRange(this.mediaChunks, 0, discardToMediaChunkIndex);
            HlsMediaChunk currentChunk = this.mediaChunks.get(0);
            Format trackFormat2 = currentChunk.trackFormat;
            if (!trackFormat2.equals(this.downstreamTrackFormat)) {
                this.eventDispatcher.downstreamFormatChanged(this.trackType, trackFormat2, currentChunk.trackSelectionReason, currentChunk.trackSelectionData, currentChunk.startTimeUs);
            }
            this.downstreamTrackFormat = trackFormat2;
        }
        int result = this.sampleQueues[sampleQueueIndex].read(formatHolder, buffer, requireFormat, this.loadingFinished, this.lastSeekPositionUs);
        if (result == -5) {
            Format format = (Format) Assertions.checkNotNull(formatHolder.format);
            if (sampleQueueIndex == this.primarySampleQueueIndex) {
                int chunkUid = this.sampleQueues[sampleQueueIndex].peekSourceId();
                int chunkIndex = 0;
                while (chunkIndex < this.mediaChunks.size() && this.mediaChunks.get(chunkIndex).uid != chunkUid) {
                    chunkIndex++;
                }
                if (chunkIndex < this.mediaChunks.size()) {
                    trackFormat = this.mediaChunks.get(chunkIndex).trackFormat;
                } else {
                    trackFormat = (Format) Assertions.checkNotNull(this.upstreamTrackFormat);
                }
                format = format.copyWithManifestFormatInfo(trackFormat);
            }
            formatHolder.format = format;
        }
        return result;
    }

    public int skipData(int sampleQueueIndex, long positionUs) {
        if (isPendingReset()) {
            return 0;
        }
        SampleQueue sampleQueue = this.sampleQueues[sampleQueueIndex];
        if (this.loadingFinished && positionUs > sampleQueue.getLargestQueuedTimestampUs()) {
            return sampleQueue.advanceToEnd();
        }
        return sampleQueue.advanceTo(positionUs);
    }

    @Override // com.google.android.exoplayer2.source.SequenceableLoader
    public long getBufferedPositionUs() {
        HlsMediaChunk lastCompletedMediaChunk;
        SampleQueue[] sampleQueueArr;
        ArrayList<HlsMediaChunk> arrayList;
        if (this.loadingFinished) {
            return Long.MIN_VALUE;
        }
        if (isPendingReset()) {
            return this.pendingResetPositionUs;
        }
        long bufferedPositionUs = this.lastSeekPositionUs;
        HlsMediaChunk lastMediaChunk = getLastMediaChunk();
        if (lastMediaChunk.isLoadCompleted()) {
            lastCompletedMediaChunk = lastMediaChunk;
        } else if (this.mediaChunks.size() > 1) {
            lastCompletedMediaChunk = this.mediaChunks.get(arrayList.size() - 2);
        } else {
            lastCompletedMediaChunk = null;
        }
        if (lastCompletedMediaChunk != null) {
            bufferedPositionUs = Math.max(bufferedPositionUs, lastCompletedMediaChunk.endTimeUs);
        }
        if (this.sampleQueuesBuilt) {
            for (SampleQueue sampleQueue : this.sampleQueues) {
                bufferedPositionUs = Math.max(bufferedPositionUs, sampleQueue.getLargestQueuedTimestampUs());
            }
        }
        return bufferedPositionUs;
    }

    @Override // com.google.android.exoplayer2.source.SequenceableLoader
    public long getNextLoadPositionUs() {
        if (isPendingReset()) {
            return this.pendingResetPositionUs;
        }
        if (!this.loadingFinished) {
            return getLastMediaChunk().endTimeUs;
        }
        return Long.MIN_VALUE;
    }

    @Override // com.google.android.exoplayer2.source.SequenceableLoader
    public boolean continueLoading(long positionUs) {
        long loadPositionUs;
        List<HlsMediaChunk> chunkQueue;
        long j;
        if (this.loadingFinished || this.loader.isLoading() || this.loader.hasFatalError()) {
            return false;
        }
        if (isPendingReset()) {
            chunkQueue = Collections.emptyList();
            loadPositionUs = this.pendingResetPositionUs;
        } else {
            chunkQueue = this.readOnlyMediaChunks;
            HlsMediaChunk lastMediaChunk = getLastMediaChunk();
            if (lastMediaChunk.isLoadCompleted()) {
                j = lastMediaChunk.endTimeUs;
            } else {
                j = Math.max(this.lastSeekPositionUs, lastMediaChunk.startTimeUs);
            }
            loadPositionUs = j;
        }
        this.chunkSource.getNextChunk(positionUs, loadPositionUs, chunkQueue, this.prepared || !chunkQueue.isEmpty(), this.nextChunkHolder);
        boolean endOfStream = this.nextChunkHolder.endOfStream;
        Chunk loadable = this.nextChunkHolder.chunk;
        Uri playlistUrlToLoad = this.nextChunkHolder.playlistUrl;
        this.nextChunkHolder.clear();
        if (endOfStream) {
            this.pendingResetPositionUs = C.TIME_UNSET;
            this.loadingFinished = true;
            return true;
        } else if (loadable == null) {
            if (playlistUrlToLoad != null) {
                this.callback.onPlaylistRefreshRequired(playlistUrlToLoad);
            }
            return false;
        } else {
            if (isMediaChunk(loadable)) {
                this.pendingResetPositionUs = C.TIME_UNSET;
                HlsMediaChunk mediaChunk = (HlsMediaChunk) loadable;
                mediaChunk.init(this);
                this.mediaChunks.add(mediaChunk);
                this.upstreamTrackFormat = mediaChunk.trackFormat;
            }
            long elapsedRealtimeMs = this.loader.startLoading(loadable, this, this.loadErrorHandlingPolicy.getMinimumLoadableRetryCount(loadable.type));
            MediaSourceEventListener.EventDispatcher eventDispatcher = this.eventDispatcher;
            DataSpec dataSpec = loadable.dataSpec;
            int i = loadable.type;
            int i2 = this.trackType;
            Format format = loadable.trackFormat;
            int i3 = loadable.trackSelectionReason;
            Object obj = loadable.trackSelectionData;
            long j2 = loadable.startTimeUs;
            long loadPositionUs2 = loadable.endTimeUs;
            eventDispatcher.loadStarted(dataSpec, i, i2, format, i3, obj, j2, loadPositionUs2, elapsedRealtimeMs);
            return true;
        }
    }

    @Override // com.google.android.exoplayer2.source.SequenceableLoader
    public boolean isLoading() {
        return this.loader.isLoading();
    }

    @Override // com.google.android.exoplayer2.source.SequenceableLoader
    public void reevaluateBuffer(long positionUs) {
    }

    public void onLoadCompleted(Chunk loadable, long elapsedRealtimeMs, long loadDurationMs) {
        this.chunkSource.onChunkLoadCompleted(loadable);
        this.eventDispatcher.loadCompleted(loadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), loadable.type, this.trackType, loadable.trackFormat, loadable.trackSelectionReason, loadable.trackSelectionData, loadable.startTimeUs, loadable.endTimeUs, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
        if (!this.prepared) {
            continueLoading(this.lastSeekPositionUs);
        } else {
            this.callback.onContinueLoadingRequested(this);
        }
    }

    public void onLoadCanceled(Chunk loadable, long elapsedRealtimeMs, long loadDurationMs, boolean released) {
        this.eventDispatcher.loadCanceled(loadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), loadable.type, this.trackType, loadable.trackFormat, loadable.trackSelectionReason, loadable.trackSelectionData, loadable.startTimeUs, loadable.endTimeUs, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
        if (!released) {
            resetSampleQueues();
            if (this.enabledTrackGroupCount > 0) {
                this.callback.onContinueLoadingRequested(this);
            }
        }
    }

    public Loader.LoadErrorAction onLoadError(Chunk loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error, int errorCount) {
        boolean blacklistSucceeded;
        Loader.LoadErrorAction loadErrorAction;
        Loader.LoadErrorAction loadErrorAction2;
        long bytesLoaded = loadable.bytesLoaded();
        boolean isMediaChunk = isMediaChunk(loadable);
        long blacklistDurationMs = this.loadErrorHandlingPolicy.getBlacklistDurationMsFor(loadable.type, loadDurationMs, error, errorCount);
        if (blacklistDurationMs == C.TIME_UNSET) {
            blacklistSucceeded = false;
        } else {
            blacklistSucceeded = this.chunkSource.maybeBlacklistTrack(loadable, blacklistDurationMs);
        }
        boolean blacklistSucceeded2 = false;
        if (blacklistSucceeded) {
            if (isMediaChunk && bytesLoaded == 0) {
                ArrayList<HlsMediaChunk> arrayList = this.mediaChunks;
                HlsMediaChunk removed = arrayList.remove(arrayList.size() - 1);
                if (removed == loadable) {
                    blacklistSucceeded2 = true;
                }
                Assertions.checkState(blacklistSucceeded2);
                if (this.mediaChunks.isEmpty()) {
                    this.pendingResetPositionUs = this.lastSeekPositionUs;
                }
            }
            loadErrorAction = Loader.DONT_RETRY;
        } else {
            long retryDelayMs = this.loadErrorHandlingPolicy.getRetryDelayMsFor(loadable.type, loadDurationMs, error, errorCount);
            if (retryDelayMs != C.TIME_UNSET) {
                loadErrorAction2 = Loader.createRetryAction(false, retryDelayMs);
            } else {
                loadErrorAction2 = Loader.DONT_RETRY_FATAL;
            }
            loadErrorAction = loadErrorAction2;
        }
        this.eventDispatcher.loadError(loadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), loadable.type, this.trackType, loadable.trackFormat, loadable.trackSelectionReason, loadable.trackSelectionData, loadable.startTimeUs, loadable.endTimeUs, elapsedRealtimeMs, loadDurationMs, bytesLoaded, error, !loadErrorAction.isRetry());
        if (blacklistSucceeded) {
            if (!this.prepared) {
                continueLoading(this.lastSeekPositionUs);
            } else {
                this.callback.onContinueLoadingRequested(this);
            }
        }
        return loadErrorAction;
    }

    public void init(int chunkUid, boolean shouldSpliceIn) {
        SampleQueue[] sampleQueueArr;
        SampleQueue[] sampleQueueArr2;
        this.chunkUid = chunkUid;
        for (SampleQueue sampleQueue : this.sampleQueues) {
            sampleQueue.sourceId(chunkUid);
        }
        if (shouldSpliceIn) {
            for (SampleQueue sampleQueue2 : this.sampleQueues) {
                sampleQueue2.splice();
            }
        }
    }

    @Override // com.google.android.exoplayer2.extractor.ExtractorOutput
    public TrackOutput track(int id, int type) {
        TrackOutput trackOutput = null;
        if (MAPPABLE_TYPES.contains(Integer.valueOf(type))) {
            trackOutput = getMappedTrackOutput(id, type);
        } else {
            int i = 0;
            while (true) {
                TrackOutput[] trackOutputArr = this.sampleQueues;
                if (i >= trackOutputArr.length) {
                    break;
                } else if (this.sampleQueueTrackIds[i] != id) {
                    i++;
                } else {
                    trackOutput = trackOutputArr[i];
                    break;
                }
            }
        }
        if (trackOutput == null) {
            if (this.tracksEnded) {
                return createDummyTrackOutput(id, type);
            }
            trackOutput = createSampleQueue(id, type);
        }
        if (type == 4) {
            if (this.emsgUnwrappingTrackOutput == null) {
                this.emsgUnwrappingTrackOutput = new EmsgUnwrappingTrackOutput(trackOutput, this.metadataType);
            }
            return this.emsgUnwrappingTrackOutput;
        }
        return trackOutput;
    }

    private TrackOutput getMappedTrackOutput(int id, int type) {
        Assertions.checkArgument(MAPPABLE_TYPES.contains(Integer.valueOf(type)));
        int sampleQueueIndex = this.sampleQueueIndicesByType.get(type, -1);
        if (sampleQueueIndex == -1) {
            return null;
        }
        if (this.sampleQueueMappingDoneByType.add(Integer.valueOf(type))) {
            this.sampleQueueTrackIds[sampleQueueIndex] = id;
        }
        if (this.sampleQueueTrackIds[sampleQueueIndex] == id) {
            return this.sampleQueues[sampleQueueIndex];
        }
        return createDummyTrackOutput(id, type);
    }

    private SampleQueue createSampleQueue(int id, int type) {
        int trackCount = this.sampleQueues.length;
        boolean isAudioVideo = true;
        if (type != 1 && type != 2) {
            isAudioVideo = false;
        }
        FormatAdjustingSampleQueue trackOutput = new FormatAdjustingSampleQueue(this.allocator, this.handler.getLooper(), this.drmSessionManager, this.overridingDrmInitData);
        if (isAudioVideo) {
            trackOutput.setDrmInitData(this.drmInitData);
        }
        trackOutput.setSampleOffsetUs(this.sampleOffsetUs);
        trackOutput.sourceId(this.chunkUid);
        trackOutput.setUpstreamFormatChangeListener(this);
        int[] copyOf = Arrays.copyOf(this.sampleQueueTrackIds, trackCount + 1);
        this.sampleQueueTrackIds = copyOf;
        copyOf[trackCount] = id;
        this.sampleQueues = (FormatAdjustingSampleQueue[]) Util.nullSafeArrayAppend(this.sampleQueues, trackOutput);
        boolean[] copyOf2 = Arrays.copyOf(this.sampleQueueIsAudioVideoFlags, trackCount + 1);
        this.sampleQueueIsAudioVideoFlags = copyOf2;
        copyOf2[trackCount] = isAudioVideo;
        this.haveAudioVideoSampleQueues = copyOf2[trackCount] | this.haveAudioVideoSampleQueues;
        this.sampleQueueMappingDoneByType.add(Integer.valueOf(type));
        this.sampleQueueIndicesByType.append(type, trackCount);
        if (getTrackTypeScore(type) > getTrackTypeScore(this.primarySampleQueueType)) {
            this.primarySampleQueueIndex = trackCount;
            this.primarySampleQueueType = type;
        }
        this.sampleQueuesEnabledStates = Arrays.copyOf(this.sampleQueuesEnabledStates, trackCount + 1);
        return trackOutput;
    }

    @Override // com.google.android.exoplayer2.extractor.ExtractorOutput
    public void endTracks() {
        this.tracksEnded = true;
        this.handler.post(this.onTracksEndedRunnable);
    }

    @Override // com.google.android.exoplayer2.extractor.ExtractorOutput
    public void seekMap(SeekMap seekMap) {
    }

    @Override // com.google.android.exoplayer2.source.SampleQueue.UpstreamFormatChangedListener
    public void onUpstreamFormatChanged(Format format) {
        this.handler.post(this.maybeFinishPrepareRunnable);
    }

    public void onNewExtractor() {
        this.sampleQueueMappingDoneByType.clear();
    }

    public void setSampleOffsetUs(long sampleOffsetUs) {
        SampleQueue[] sampleQueueArr;
        if (this.sampleOffsetUs != sampleOffsetUs) {
            this.sampleOffsetUs = sampleOffsetUs;
            for (SampleQueue sampleQueue : this.sampleQueues) {
                sampleQueue.setSampleOffsetUs(sampleOffsetUs);
            }
        }
    }

    public void setDrmInitData(DrmInitData drmInitData) {
        if (!Util.areEqual(this.drmInitData, drmInitData)) {
            this.drmInitData = drmInitData;
            int i = 0;
            while (true) {
                FormatAdjustingSampleQueue[] formatAdjustingSampleQueueArr = this.sampleQueues;
                if (i < formatAdjustingSampleQueueArr.length) {
                    if (this.sampleQueueIsAudioVideoFlags[i]) {
                        formatAdjustingSampleQueueArr[i].setDrmInitData(drmInitData);
                    }
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    private void updateSampleStreams(SampleStream[] streams) {
        this.hlsSampleStreams.clear();
        for (SampleStream stream : streams) {
            if (stream != null) {
                this.hlsSampleStreams.add((HlsSampleStream) stream);
            }
        }
    }

    private boolean finishedReadingChunk(HlsMediaChunk chunk) {
        int chunkUid = chunk.uid;
        int sampleQueueCount = this.sampleQueues.length;
        for (int i = 0; i < sampleQueueCount; i++) {
            if (this.sampleQueuesEnabledStates[i] && this.sampleQueues[i].peekSourceId() == chunkUid) {
                return false;
            }
        }
        return true;
    }

    private void resetSampleQueues() {
        SampleQueue[] sampleQueueArr;
        for (SampleQueue sampleQueue : this.sampleQueues) {
            sampleQueue.reset(this.pendingResetUpstreamFormats);
        }
        this.pendingResetUpstreamFormats = false;
    }

    public void onTracksEnded() {
        this.sampleQueuesBuilt = true;
        maybeFinishPrepare();
    }

    public void maybeFinishPrepare() {
        SampleQueue[] sampleQueueArr;
        if (this.released || this.trackGroupToSampleQueueIndex != null || !this.sampleQueuesBuilt) {
            return;
        }
        for (SampleQueue sampleQueue : this.sampleQueues) {
            if (sampleQueue.getUpstreamFormat() == null) {
                return;
            }
        }
        if (this.trackGroups != null) {
            mapSampleQueuesToMatchTrackGroups();
            return;
        }
        buildTracksFromSampleStreams();
        setIsPrepared();
        this.callback.onPrepared();
    }

    @EnsuresNonNull({"trackGroupToSampleQueueIndex"})
    @RequiresNonNull({"trackGroups"})
    private void mapSampleQueuesToMatchTrackGroups() {
        int trackGroupCount = this.trackGroups.length;
        int[] iArr = new int[trackGroupCount];
        this.trackGroupToSampleQueueIndex = iArr;
        Arrays.fill(iArr, -1);
        for (int i = 0; i < trackGroupCount; i++) {
            int queueIndex = 0;
            while (true) {
                SampleQueue[] sampleQueueArr = this.sampleQueues;
                if (queueIndex < sampleQueueArr.length) {
                    SampleQueue sampleQueue = sampleQueueArr[queueIndex];
                    if (!formatsMatch(sampleQueue.getUpstreamFormat(), this.trackGroups.get(i).getFormat(0))) {
                        queueIndex++;
                    } else {
                        this.trackGroupToSampleQueueIndex[i] = queueIndex;
                        break;
                    }
                }
            }
        }
        Iterator<HlsSampleStream> it = this.hlsSampleStreams.iterator();
        while (it.hasNext()) {
            HlsSampleStream sampleStream = it.next();
            sampleStream.bindSampleQueue();
        }
    }

    @EnsuresNonNull({"trackGroups", "optionalTrackGroups", "trackGroupToSampleQueueIndex"})
    private void buildTracksFromSampleStreams() {
        boolean z;
        Format trackFormat;
        int trackType;
        int primaryExtractorTrackType = 6;
        int primaryExtractorTrackIndex = -1;
        int extractorTrackCount = this.sampleQueues.length;
        for (int i = 0; i < extractorTrackCount; i++) {
            String sampleMimeType = this.sampleQueues[i].getUpstreamFormat().sampleMimeType;
            if (MimeTypes.isVideo(sampleMimeType)) {
                trackType = 2;
            } else if (MimeTypes.isAudio(sampleMimeType)) {
                trackType = 1;
            } else if (MimeTypes.isText(sampleMimeType)) {
                trackType = 3;
            } else {
                trackType = 6;
            }
            if (getTrackTypeScore(trackType) > getTrackTypeScore(primaryExtractorTrackType)) {
                primaryExtractorTrackType = trackType;
                primaryExtractorTrackIndex = i;
            } else if (trackType == primaryExtractorTrackType && primaryExtractorTrackIndex != -1) {
                primaryExtractorTrackIndex = -1;
            }
        }
        TrackGroup chunkSourceTrackGroup = this.chunkSource.getTrackGroup();
        int chunkSourceTrackCount = chunkSourceTrackGroup.length;
        this.primaryTrackGroupIndex = -1;
        this.trackGroupToSampleQueueIndex = new int[extractorTrackCount];
        for (int i2 = 0; i2 < extractorTrackCount; i2++) {
            this.trackGroupToSampleQueueIndex[i2] = i2;
        }
        TrackGroup[] trackGroups = new TrackGroup[extractorTrackCount];
        int i3 = 0;
        while (true) {
            z = false;
            if (i3 >= extractorTrackCount) {
                break;
            }
            Format sampleFormat = this.sampleQueues[i3].getUpstreamFormat();
            if (i3 == primaryExtractorTrackIndex) {
                Format[] formats = new Format[chunkSourceTrackCount];
                if (chunkSourceTrackCount == 1) {
                    formats[0] = sampleFormat.copyWithManifestFormatInfo(chunkSourceTrackGroup.getFormat(0));
                } else {
                    for (int j = 0; j < chunkSourceTrackCount; j++) {
                        formats[j] = deriveFormat(chunkSourceTrackGroup.getFormat(j), sampleFormat, true);
                    }
                }
                trackGroups[i3] = new TrackGroup(formats);
                this.primaryTrackGroupIndex = i3;
            } else {
                if (primaryExtractorTrackType == 2 && MimeTypes.isAudio(sampleFormat.sampleMimeType)) {
                    trackFormat = this.muxedAudioFormat;
                } else {
                    trackFormat = null;
                }
                trackGroups[i3] = new TrackGroup(deriveFormat(trackFormat, sampleFormat, false));
            }
            i3++;
        }
        this.trackGroups = createTrackGroupArrayWithDrmInfo(trackGroups);
        if (this.optionalTrackGroups == null) {
            z = true;
        }
        Assertions.checkState(z);
        this.optionalTrackGroups = Collections.emptySet();
    }

    private TrackGroupArray createTrackGroupArrayWithDrmInfo(TrackGroup[] trackGroups) {
        for (int i = 0; i < trackGroups.length; i++) {
            TrackGroup trackGroup = trackGroups[i];
            Format[] exposedFormats = new Format[trackGroup.length];
            for (int j = 0; j < trackGroup.length; j++) {
                Format format = trackGroup.getFormat(j);
                if (format.drmInitData != null) {
                    format = format.copyWithExoMediaCryptoType(this.drmSessionManager.getExoMediaCryptoType(format.drmInitData));
                }
                exposedFormats[j] = format;
            }
            trackGroups[i] = new TrackGroup(exposedFormats);
        }
        return new TrackGroupArray(trackGroups);
    }

    private HlsMediaChunk getLastMediaChunk() {
        ArrayList<HlsMediaChunk> arrayList = this.mediaChunks;
        return arrayList.get(arrayList.size() - 1);
    }

    private boolean isPendingReset() {
        return this.pendingResetPositionUs != C.TIME_UNSET;
    }

    private boolean seekInsideBufferUs(long positionUs) {
        int sampleQueueCount = this.sampleQueues.length;
        for (int i = 0; i < sampleQueueCount; i++) {
            SampleQueue sampleQueue = this.sampleQueues[i];
            boolean seekInsideQueue = sampleQueue.seekTo(positionUs, false);
            if (!seekInsideQueue && (this.sampleQueueIsAudioVideoFlags[i] || !this.haveAudioVideoSampleQueues)) {
                return false;
            }
        }
        return true;
    }

    @RequiresNonNull({"trackGroups", "optionalTrackGroups"})
    private void setIsPrepared() {
        this.prepared = true;
    }

    @EnsuresNonNull({"trackGroups", "optionalTrackGroups"})
    private void assertIsPrepared() {
        Assertions.checkState(this.prepared);
        Assertions.checkNotNull(this.trackGroups);
        Assertions.checkNotNull(this.optionalTrackGroups);
    }

    private static int getTrackTypeScore(int trackType) {
        switch (trackType) {
            case 1:
                return 2;
            case 2:
                return 3;
            case 3:
                return 1;
            default:
                return 0;
        }
    }

    private static Format deriveFormat(Format playlistFormat, Format sampleFormat, boolean propagateBitrate) {
        int channelCount;
        String mimeType;
        if (playlistFormat == null) {
            return sampleFormat;
        }
        int bitrate = propagateBitrate ? playlistFormat.bitrate : -1;
        if (playlistFormat.channelCount != -1) {
            channelCount = playlistFormat.channelCount;
        } else {
            channelCount = sampleFormat.channelCount;
        }
        int sampleTrackType = MimeTypes.getTrackType(sampleFormat.sampleMimeType);
        String codecs = Util.getCodecsOfType(playlistFormat.codecs, sampleTrackType);
        String mimeType2 = MimeTypes.getMediaMimeType(codecs);
        if (mimeType2 != null) {
            mimeType = mimeType2;
        } else {
            mimeType = sampleFormat.sampleMimeType;
        }
        return sampleFormat.copyWithContainerInfo(playlistFormat.id, playlistFormat.label, mimeType, codecs, playlistFormat.metadata, bitrate, playlistFormat.width, playlistFormat.height, channelCount, playlistFormat.selectionFlags, playlistFormat.language);
    }

    private static boolean isMediaChunk(Chunk chunk) {
        return chunk instanceof HlsMediaChunk;
    }

    private static boolean formatsMatch(Format manifestFormat, Format sampleFormat) {
        String manifestFormatMimeType = manifestFormat.sampleMimeType;
        String sampleFormatMimeType = sampleFormat.sampleMimeType;
        int manifestFormatTrackType = MimeTypes.getTrackType(manifestFormatMimeType);
        if (manifestFormatTrackType != 3) {
            return manifestFormatTrackType == MimeTypes.getTrackType(sampleFormatMimeType);
        } else if (!Util.areEqual(manifestFormatMimeType, sampleFormatMimeType)) {
            return false;
        } else {
            return (!MimeTypes.APPLICATION_CEA608.equals(manifestFormatMimeType) && !MimeTypes.APPLICATION_CEA708.equals(manifestFormatMimeType)) || manifestFormat.accessibilityChannel == sampleFormat.accessibilityChannel;
        }
    }

    private static DummyTrackOutput createDummyTrackOutput(int id, int type) {
        Log.w(TAG, "Unmapped track with id " + id + " of type " + type);
        return new DummyTrackOutput();
    }

    /* loaded from: classes3.dex */
    public static final class FormatAdjustingSampleQueue extends SampleQueue {
        private DrmInitData drmInitData;
        private final Map<String, DrmInitData> overridingDrmInitData;

        public FormatAdjustingSampleQueue(Allocator allocator, Looper playbackLooper, DrmSessionManager<?> drmSessionManager, Map<String, DrmInitData> overridingDrmInitData) {
            super(allocator, playbackLooper, drmSessionManager);
            this.overridingDrmInitData = overridingDrmInitData;
        }

        public void setDrmInitData(DrmInitData drmInitData) {
            this.drmInitData = drmInitData;
            invalidateUpstreamFormatAdjustment();
        }

        @Override // com.google.android.exoplayer2.source.SampleQueue
        public Format getAdjustedUpstreamFormat(Format format) {
            DrmInitData overridingDrmInitData;
            DrmInitData drmInitData = this.drmInitData;
            if (drmInitData == null) {
                drmInitData = format.drmInitData;
            }
            if (drmInitData != null && (overridingDrmInitData = this.overridingDrmInitData.get(drmInitData.schemeType)) != null) {
                drmInitData = overridingDrmInitData;
            }
            return super.getAdjustedUpstreamFormat(format.copyWithAdjustments(drmInitData, getAdjustedMetadata(format.metadata)));
        }

        private Metadata getAdjustedMetadata(Metadata metadata) {
            if (metadata == null) {
                return null;
            }
            int length = metadata.length();
            int transportStreamTimestampMetadataIndex = -1;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                Metadata.Entry metadataEntry = metadata.get(i);
                if (metadataEntry instanceof PrivFrame) {
                    PrivFrame privFrame = (PrivFrame) metadataEntry;
                    if (HlsMediaChunk.PRIV_TIMESTAMP_FRAME_OWNER.equals(privFrame.owner)) {
                        transportStreamTimestampMetadataIndex = i;
                        break;
                    }
                }
                i++;
            }
            if (transportStreamTimestampMetadataIndex == -1) {
                return metadata;
            }
            if (length == 1) {
                return null;
            }
            Metadata.Entry[] newMetadataEntries = new Metadata.Entry[length - 1];
            int i2 = 0;
            while (i2 < length) {
                if (i2 != transportStreamTimestampMetadataIndex) {
                    int newIndex = i2 < transportStreamTimestampMetadataIndex ? i2 : i2 - 1;
                    newMetadataEntries[newIndex] = metadata.get(i2);
                }
                i2++;
            }
            return new Metadata(newMetadataEntries);
        }
    }

    /* loaded from: classes3.dex */
    private static class EmsgUnwrappingTrackOutput implements TrackOutput {
        private static final String TAG = "EmsgUnwrappingTrackOutput";
        private byte[] buffer;
        private int bufferPosition;
        private final TrackOutput delegate;
        private final Format delegateFormat;
        private final EventMessageDecoder emsgDecoder = new EventMessageDecoder();
        private Format format;
        private static final Format ID3_FORMAT = Format.createSampleFormat(null, MimeTypes.APPLICATION_ID3, Long.MAX_VALUE);
        private static final Format EMSG_FORMAT = Format.createSampleFormat(null, MimeTypes.APPLICATION_EMSG, Long.MAX_VALUE);

        public EmsgUnwrappingTrackOutput(TrackOutput delegate, int metadataType) {
            this.delegate = delegate;
            switch (metadataType) {
                case 1:
                    this.delegateFormat = ID3_FORMAT;
                    break;
                case 2:
                default:
                    throw new IllegalArgumentException("Unknown metadataType: " + metadataType);
                case 3:
                    this.delegateFormat = EMSG_FORMAT;
                    break;
            }
            this.buffer = new byte[0];
            this.bufferPosition = 0;
        }

        @Override // com.google.android.exoplayer2.extractor.TrackOutput
        public void format(Format format) {
            this.format = format;
            this.delegate.format(this.delegateFormat);
        }

        @Override // com.google.android.exoplayer2.extractor.TrackOutput
        public int sampleData(ExtractorInput input, int length, boolean allowEndOfInput) throws IOException, InterruptedException {
            ensureBufferCapacity(this.bufferPosition + length);
            int numBytesRead = input.read(this.buffer, this.bufferPosition, length);
            if (numBytesRead == -1) {
                if (allowEndOfInput) {
                    return -1;
                }
                throw new EOFException();
            }
            this.bufferPosition += numBytesRead;
            return numBytesRead;
        }

        @Override // com.google.android.exoplayer2.extractor.TrackOutput
        public void sampleData(ParsableByteArray buffer, int length) {
            ensureBufferCapacity(this.bufferPosition + length);
            buffer.readBytes(this.buffer, this.bufferPosition, length);
            this.bufferPosition += length;
        }

        @Override // com.google.android.exoplayer2.extractor.TrackOutput
        public void sampleMetadata(long timeUs, int flags, int size, int offset, TrackOutput.CryptoData cryptoData) {
            ParsableByteArray sampleForDelegate;
            Assertions.checkNotNull(this.format);
            ParsableByteArray sample = getSampleAndTrimBuffer(size, offset);
            if (Util.areEqual(this.format.sampleMimeType, this.delegateFormat.sampleMimeType)) {
                sampleForDelegate = sample;
            } else if (MimeTypes.APPLICATION_EMSG.equals(this.format.sampleMimeType)) {
                EventMessage emsg = this.emsgDecoder.decode(sample);
                if (!emsgContainsExpectedWrappedFormat(emsg)) {
                    Log.w(TAG, String.format("Ignoring EMSG. Expected it to contain wrapped %s but actual wrapped format: %s", this.delegateFormat.sampleMimeType, emsg.getWrappedMetadataFormat()));
                    return;
                }
                sampleForDelegate = new ParsableByteArray((byte[]) Assertions.checkNotNull(emsg.getWrappedMetadataBytes()));
            } else {
                Log.w(TAG, "Ignoring sample for unsupported format: " + this.format.sampleMimeType);
                return;
            }
            int sampleSize = sampleForDelegate.bytesLeft();
            this.delegate.sampleData(sampleForDelegate, sampleSize);
            this.delegate.sampleMetadata(timeUs, flags, sampleSize, offset, cryptoData);
        }

        private boolean emsgContainsExpectedWrappedFormat(EventMessage emsg) {
            Format wrappedMetadataFormat = emsg.getWrappedMetadataFormat();
            return wrappedMetadataFormat != null && Util.areEqual(this.delegateFormat.sampleMimeType, wrappedMetadataFormat.sampleMimeType);
        }

        private void ensureBufferCapacity(int requiredLength) {
            byte[] bArr = this.buffer;
            if (bArr.length < requiredLength) {
                this.buffer = Arrays.copyOf(bArr, (requiredLength / 2) + requiredLength);
            }
        }

        private ParsableByteArray getSampleAndTrimBuffer(int size, int offset) {
            int sampleEnd = this.bufferPosition - offset;
            int sampleStart = sampleEnd - size;
            byte[] sampleBytes = Arrays.copyOfRange(this.buffer, sampleStart, sampleEnd);
            ParsableByteArray sample = new ParsableByteArray(sampleBytes);
            byte[] bArr = this.buffer;
            System.arraycopy(bArr, sampleEnd, bArr, 0, offset);
            this.bufferPosition = offset;
            return sample;
        }
    }
}
