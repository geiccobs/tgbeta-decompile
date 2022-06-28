package com.google.android.exoplayer2.source;

import android.net.Uri;
import android.os.Handler;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.extractor.DefaultExtractorInput;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.icy.IcyHeaders;
import com.google.android.exoplayer2.source.IcyDataSource;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.SampleQueue;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.Loader;
import com.google.android.exoplayer2.upstream.StatsDataSource;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ConditionVariable;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/* loaded from: classes3.dex */
public final class ProgressiveMediaPeriod implements MediaPeriod, ExtractorOutput, Loader.Callback<ExtractingLoadable>, Loader.ReleaseCallback, SampleQueue.UpstreamFormatChangedListener {
    private static final long DEFAULT_LAST_SAMPLE_DURATION_US = 10000;
    private final Allocator allocator;
    private MediaPeriod.Callback callback;
    private final long continueLoadingCheckIntervalBytes;
    private final String customCacheKey;
    private final DataSource dataSource;
    private final DrmSessionManager<?> drmSessionManager;
    private int enabledTrackCount;
    private final MediaSourceEventListener.EventDispatcher eventDispatcher;
    private int extractedSamplesCountAtStartOfLoad;
    private final ExtractorHolder extractorHolder;
    private boolean haveAudioVideoTracks;
    private IcyHeaders icyHeaders;
    private boolean isLive;
    private long lastSeekPositionUs;
    private final Listener listener;
    private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
    private boolean loadingFinished;
    private boolean notifiedReadingStarted;
    private boolean notifyDiscontinuity;
    private boolean pendingDeferredRetry;
    private boolean prepared;
    private PreparedState preparedState;
    private boolean released;
    private boolean sampleQueuesBuilt;
    private SeekMap seekMap;
    private boolean seenFirstTrackSelection;
    private final Uri uri;
    private static final Map<String, String> ICY_METADATA_HEADERS = createIcyMetadataHeaders();
    private static final Format ICY_FORMAT = Format.createSampleFormat("icy", MimeTypes.APPLICATION_ICY, Long.MAX_VALUE);
    private final Loader loader = new Loader("Loader:ProgressiveMediaPeriod");
    private final ConditionVariable loadCondition = new ConditionVariable();
    private final Runnable maybeFinishPrepareRunnable = new Runnable() { // from class: com.google.android.exoplayer2.source.ProgressiveMediaPeriod$$ExternalSyntheticLambda1
        @Override // java.lang.Runnable
        public final void run() {
            ProgressiveMediaPeriod.this.maybeFinishPrepare();
        }
    };
    private final Runnable onContinueLoadingRequestedRunnable = new Runnable() { // from class: com.google.android.exoplayer2.source.ProgressiveMediaPeriod$$ExternalSyntheticLambda0
        @Override // java.lang.Runnable
        public final void run() {
            ProgressiveMediaPeriod.this.m67x3e939aa5();
        }
    };
    private final Handler handler = new Handler();
    private TrackId[] sampleQueueTrackIds = new TrackId[0];
    private SampleQueue[] sampleQueues = new SampleQueue[0];
    private long pendingResetPositionUs = C.TIME_UNSET;
    private long length = -1;
    private long durationUs = C.TIME_UNSET;
    private int dataType = 1;

    /* loaded from: classes3.dex */
    public interface Listener {
        void onSourceInfoRefreshed(long j, boolean z, boolean z2);
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public /* synthetic */ List getStreamKeys(List list) {
        List emptyList;
        emptyList = Collections.emptyList();
        return emptyList;
    }

    public ProgressiveMediaPeriod(Uri uri, DataSource dataSource, Extractor[] extractors, DrmSessionManager<?> drmSessionManager, LoadErrorHandlingPolicy loadErrorHandlingPolicy, MediaSourceEventListener.EventDispatcher eventDispatcher, Listener listener, Allocator allocator, String customCacheKey, int continueLoadingCheckIntervalBytes) {
        this.uri = uri;
        this.dataSource = dataSource;
        this.drmSessionManager = drmSessionManager;
        this.loadErrorHandlingPolicy = loadErrorHandlingPolicy;
        this.eventDispatcher = eventDispatcher;
        this.listener = listener;
        this.allocator = allocator;
        this.customCacheKey = customCacheKey;
        this.continueLoadingCheckIntervalBytes = continueLoadingCheckIntervalBytes;
        this.extractorHolder = new ExtractorHolder(extractors);
        eventDispatcher.mediaPeriodCreated();
    }

    /* renamed from: lambda$new$0$com-google-android-exoplayer2-source-ProgressiveMediaPeriod */
    public /* synthetic */ void m67x3e939aa5() {
        if (!this.released) {
            ((MediaPeriod.Callback) Assertions.checkNotNull(this.callback)).onContinueLoadingRequested(this);
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
        this.callback = null;
        this.released = true;
        this.eventDispatcher.mediaPeriodReleased();
    }

    @Override // com.google.android.exoplayer2.upstream.Loader.ReleaseCallback
    public void onLoaderReleased() {
        SampleQueue[] sampleQueueArr;
        for (SampleQueue sampleQueue : this.sampleQueues) {
            sampleQueue.release();
        }
        this.extractorHolder.release();
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public void prepare(MediaPeriod.Callback callback, long positionUs) {
        this.callback = callback;
        this.loadCondition.open();
        startLoading();
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public void maybeThrowPrepareError() throws IOException {
        maybeThrowError();
        if (this.loadingFinished && !this.prepared) {
            throw new ParserException("Loading finished before preparation is complete.");
        }
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public TrackGroupArray getTrackGroups() {
        return getPreparedState().tracks;
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public long selectTracks(TrackSelection[] selections, boolean[] mayRetainStreamFlags, SampleStream[] streams, boolean[] streamResetFlags, long positionUs) {
        int i;
        long positionUs2 = positionUs;
        PreparedState preparedState = getPreparedState();
        TrackGroupArray tracks = preparedState.tracks;
        boolean[] trackEnabledStates = preparedState.trackEnabledStates;
        int oldEnabledTrackCount = this.enabledTrackCount;
        int i2 = 0;
        while (true) {
            i = 0;
            if (i2 >= selections.length) {
                break;
            }
            if (streams[i2] != null && (selections[i2] == null || !mayRetainStreamFlags[i2])) {
                int track = ((SampleStreamImpl) streams[i2]).track;
                Assertions.checkState(trackEnabledStates[track]);
                this.enabledTrackCount--;
                trackEnabledStates[track] = false;
                streams[i2] = null;
            }
            i2++;
        }
        boolean seekRequired = !this.seenFirstTrackSelection ? positionUs2 != 0 : oldEnabledTrackCount == 0;
        for (int i3 = 0; i3 < selections.length; i3++) {
            if (streams[i3] == null && selections[i3] != null) {
                TrackSelection selection = selections[i3];
                Assertions.checkState(selection.length() == 1);
                Assertions.checkState(selection.getIndexInTrackGroup(0) == 0);
                int track2 = tracks.indexOf(selection.getTrackGroup());
                Assertions.checkState(!trackEnabledStates[track2]);
                this.enabledTrackCount++;
                trackEnabledStates[track2] = true;
                streams[i3] = new SampleStreamImpl(track2);
                streamResetFlags[i3] = true;
                if (!seekRequired) {
                    SampleQueue sampleQueue = this.sampleQueues[track2];
                    seekRequired = !sampleQueue.seekTo(positionUs2, true) && sampleQueue.getReadIndex() != 0;
                }
            }
        }
        int i4 = this.enabledTrackCount;
        if (i4 == 0) {
            this.pendingDeferredRetry = false;
            this.notifyDiscontinuity = false;
            if (this.loader.isLoading()) {
                SampleQueue[] sampleQueueArr = this.sampleQueues;
                int length = sampleQueueArr.length;
                while (i < length) {
                    SampleQueue sampleQueue2 = sampleQueueArr[i];
                    sampleQueue2.discardToEnd();
                    i++;
                }
                this.loader.cancelLoading();
            } else {
                SampleQueue[] sampleQueueArr2 = this.sampleQueues;
                int length2 = sampleQueueArr2.length;
                while (i < length2) {
                    SampleQueue sampleQueue3 = sampleQueueArr2[i];
                    sampleQueue3.reset();
                    i++;
                }
            }
        } else if (seekRequired) {
            positionUs2 = seekToUs(positionUs2);
            for (int i5 = 0; i5 < streams.length; i5++) {
                if (streams[i5] != null) {
                    streamResetFlags[i5] = true;
                }
            }
        }
        this.seenFirstTrackSelection = true;
        return positionUs2;
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public void discardBuffer(long positionUs, boolean toKeyframe) {
        if (isPendingReset()) {
            return;
        }
        boolean[] trackEnabledStates = getPreparedState().trackEnabledStates;
        int trackCount = this.sampleQueues.length;
        for (int i = 0; i < trackCount; i++) {
            this.sampleQueues[i].discardTo(positionUs, toKeyframe, trackEnabledStates[i]);
        }
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
    public void reevaluateBuffer(long positionUs) {
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
    public boolean continueLoading(long playbackPositionUs) {
        if (this.loadingFinished || this.loader.hasFatalError() || this.pendingDeferredRetry) {
            return false;
        }
        if (this.prepared && this.enabledTrackCount == 0) {
            return false;
        }
        boolean continuedLoading = this.loadCondition.open();
        if (!this.loader.isLoading()) {
            startLoading();
            return true;
        }
        return continuedLoading;
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
    public boolean isLoading() {
        return this.loader.isLoading() && this.loadCondition.isOpen();
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
    public long getNextLoadPositionUs() {
        if (this.enabledTrackCount == 0) {
            return Long.MIN_VALUE;
        }
        return getBufferedPositionUs();
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public long readDiscontinuity() {
        if (!this.notifiedReadingStarted) {
            this.eventDispatcher.readingStarted();
            this.notifiedReadingStarted = true;
        }
        if (this.notifyDiscontinuity) {
            if (this.loadingFinished || getExtractedSamplesCount() > this.extractedSamplesCountAtStartOfLoad) {
                this.notifyDiscontinuity = false;
                return this.lastSeekPositionUs;
            }
            return C.TIME_UNSET;
        }
        return C.TIME_UNSET;
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
    public long getBufferedPositionUs() {
        boolean[] trackIsAudioVideoFlags = getPreparedState().trackIsAudioVideoFlags;
        if (this.loadingFinished) {
            return Long.MIN_VALUE;
        }
        if (isPendingReset()) {
            return this.pendingResetPositionUs;
        }
        long largestQueuedTimestampUs = Long.MAX_VALUE;
        if (this.haveAudioVideoTracks) {
            int trackCount = this.sampleQueues.length;
            for (int i = 0; i < trackCount; i++) {
                if (trackIsAudioVideoFlags[i] && !this.sampleQueues[i].isLastSampleQueued()) {
                    largestQueuedTimestampUs = Math.min(largestQueuedTimestampUs, this.sampleQueues[i].getLargestQueuedTimestampUs());
                }
            }
        }
        if (largestQueuedTimestampUs == Long.MAX_VALUE) {
            largestQueuedTimestampUs = getLargestQueuedTimestampUs();
        }
        return largestQueuedTimestampUs == Long.MIN_VALUE ? this.lastSeekPositionUs : largestQueuedTimestampUs;
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public long seekToUs(long positionUs) {
        SampleQueue[] sampleQueueArr;
        PreparedState preparedState = getPreparedState();
        SeekMap seekMap = preparedState.seekMap;
        boolean[] trackIsAudioVideoFlags = preparedState.trackIsAudioVideoFlags;
        long positionUs2 = seekMap.isSeekable() ? positionUs : 0L;
        this.notifyDiscontinuity = false;
        this.lastSeekPositionUs = positionUs2;
        if (isPendingReset()) {
            this.pendingResetPositionUs = positionUs2;
            return positionUs2;
        } else if (this.dataType != 7 && seekInsideBufferUs(trackIsAudioVideoFlags, positionUs2)) {
            return positionUs2;
        } else {
            this.pendingDeferredRetry = false;
            this.pendingResetPositionUs = positionUs2;
            this.loadingFinished = false;
            if (this.loader.isLoading()) {
                this.loader.cancelLoading();
            } else {
                this.loader.clearFatalError();
                for (SampleQueue sampleQueue : this.sampleQueues) {
                    sampleQueue.reset();
                }
            }
            return positionUs2;
        }
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public long getAdjustedSeekPositionUs(long positionUs, SeekParameters seekParameters) {
        SeekMap seekMap = getPreparedState().seekMap;
        if (!seekMap.isSeekable()) {
            return 0L;
        }
        SeekMap.SeekPoints seekPoints = seekMap.getSeekPoints(positionUs);
        return Util.resolveSeekPositionUs(positionUs, seekParameters, seekPoints.first.timeUs, seekPoints.second.timeUs);
    }

    boolean isReady(int track) {
        return !suppressRead() && this.sampleQueues[track].isReady(this.loadingFinished);
    }

    void maybeThrowError(int sampleQueueIndex) throws IOException {
        this.sampleQueues[sampleQueueIndex].maybeThrowError();
        maybeThrowError();
    }

    void maybeThrowError() throws IOException {
        this.loader.maybeThrowError(this.loadErrorHandlingPolicy.getMinimumLoadableRetryCount(this.dataType));
    }

    int readData(int sampleQueueIndex, FormatHolder formatHolder, DecoderInputBuffer buffer, boolean formatRequired) {
        if (suppressRead()) {
            return -3;
        }
        maybeNotifyDownstreamFormat(sampleQueueIndex);
        int result = this.sampleQueues[sampleQueueIndex].read(formatHolder, buffer, formatRequired, this.loadingFinished, this.lastSeekPositionUs);
        if (result == -3) {
            maybeStartDeferredRetry(sampleQueueIndex);
        }
        return result;
    }

    int skipData(int track, long positionUs) {
        int skipCount;
        if (suppressRead()) {
            return 0;
        }
        maybeNotifyDownstreamFormat(track);
        SampleQueue sampleQueue = this.sampleQueues[track];
        if (this.loadingFinished && positionUs > sampleQueue.getLargestQueuedTimestampUs()) {
            skipCount = sampleQueue.advanceToEnd();
        } else {
            skipCount = sampleQueue.advanceTo(positionUs);
        }
        if (skipCount == 0) {
            maybeStartDeferredRetry(track);
        }
        return skipCount;
    }

    private void maybeNotifyDownstreamFormat(int track) {
        PreparedState preparedState = getPreparedState();
        boolean[] trackNotifiedDownstreamFormats = preparedState.trackNotifiedDownstreamFormats;
        if (!trackNotifiedDownstreamFormats[track]) {
            Format trackFormat = preparedState.tracks.get(track).getFormat(0);
            this.eventDispatcher.downstreamFormatChanged(MimeTypes.getTrackType(trackFormat.sampleMimeType), trackFormat, 0, null, this.lastSeekPositionUs);
            trackNotifiedDownstreamFormats[track] = true;
        }
    }

    private void maybeStartDeferredRetry(int track) {
        SampleQueue[] sampleQueueArr;
        boolean[] trackIsAudioVideoFlags = getPreparedState().trackIsAudioVideoFlags;
        if (this.pendingDeferredRetry && trackIsAudioVideoFlags[track]) {
            if (this.sampleQueues[track].isReady(false)) {
                return;
            }
            this.pendingResetPositionUs = 0L;
            this.pendingDeferredRetry = false;
            this.notifyDiscontinuity = true;
            this.lastSeekPositionUs = 0L;
            this.extractedSamplesCountAtStartOfLoad = 0;
            for (SampleQueue sampleQueue : this.sampleQueues) {
                sampleQueue.reset();
            }
            ((MediaPeriod.Callback) Assertions.checkNotNull(this.callback)).onContinueLoadingRequested(this);
        }
    }

    private boolean suppressRead() {
        return this.notifyDiscontinuity || isPendingReset();
    }

    public void onLoadCompleted(ExtractingLoadable loadable, long elapsedRealtimeMs, long loadDurationMs) {
        SeekMap seekMap;
        if (this.durationUs == C.TIME_UNSET && (seekMap = this.seekMap) != null) {
            boolean isSeekable = seekMap.isSeekable();
            long largestQueuedTimestampUs = getLargestQueuedTimestampUs();
            long j = largestQueuedTimestampUs == Long.MIN_VALUE ? 0L : DEFAULT_LAST_SAMPLE_DURATION_US + largestQueuedTimestampUs;
            this.durationUs = j;
            this.listener.onSourceInfoRefreshed(j, isSeekable, this.isLive);
        }
        this.eventDispatcher.loadCompleted(loadable.dataSpec, loadable.dataSource.getLastOpenedUri(), loadable.dataSource.getLastResponseHeaders(), 1, -1, null, 0, null, loadable.seekTimeUs, this.durationUs, elapsedRealtimeMs, loadDurationMs, loadable.dataSource.getBytesRead());
        copyLengthFromLoader(loadable);
        this.loadingFinished = true;
        ((MediaPeriod.Callback) Assertions.checkNotNull(this.callback)).onContinueLoadingRequested(this);
    }

    public void onLoadCanceled(ExtractingLoadable loadable, long elapsedRealtimeMs, long loadDurationMs, boolean released) {
        SampleQueue[] sampleQueueArr;
        this.eventDispatcher.loadCanceled(loadable.dataSpec, loadable.dataSource.getLastOpenedUri(), loadable.dataSource.getLastResponseHeaders(), 1, -1, null, 0, null, loadable.seekTimeUs, this.durationUs, elapsedRealtimeMs, loadDurationMs, loadable.dataSource.getBytesRead());
        if (!released) {
            copyLengthFromLoader(loadable);
            for (SampleQueue sampleQueue : this.sampleQueues) {
                sampleQueue.reset();
            }
            if (this.enabledTrackCount > 0) {
                ((MediaPeriod.Callback) Assertions.checkNotNull(this.callback)).onContinueLoadingRequested(this);
            }
        }
    }

    public Loader.LoadErrorAction onLoadError(ExtractingLoadable loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error, int errorCount) {
        Loader.LoadErrorAction loadErrorAction;
        Loader.LoadErrorAction loadErrorAction2;
        copyLengthFromLoader(loadable);
        long retryDelayMs = this.loadErrorHandlingPolicy.getRetryDelayMsFor(this.dataType, loadDurationMs, error, errorCount);
        if (retryDelayMs == C.TIME_UNSET) {
            loadErrorAction = Loader.DONT_RETRY_FATAL;
        } else {
            int extractedSamplesCount = getExtractedSamplesCount();
            boolean madeProgress = extractedSamplesCount > this.extractedSamplesCountAtStartOfLoad;
            if (configureRetry(loadable, extractedSamplesCount)) {
                loadErrorAction2 = Loader.createRetryAction(madeProgress, retryDelayMs);
            } else {
                loadErrorAction2 = Loader.DONT_RETRY;
            }
            loadErrorAction = loadErrorAction2;
        }
        this.eventDispatcher.loadError(loadable.dataSpec, loadable.dataSource.getLastOpenedUri(), loadable.dataSource.getLastResponseHeaders(), 1, -1, null, 0, null, loadable.seekTimeUs, this.durationUs, elapsedRealtimeMs, loadDurationMs, loadable.dataSource.getBytesRead(), error, !loadErrorAction.isRetry());
        return loadErrorAction;
    }

    @Override // com.google.android.exoplayer2.extractor.ExtractorOutput
    public TrackOutput track(int id, int type) {
        return prepareTrackOutput(new TrackId(id, false));
    }

    @Override // com.google.android.exoplayer2.extractor.ExtractorOutput
    public void endTracks() {
        this.sampleQueuesBuilt = true;
        this.handler.post(this.maybeFinishPrepareRunnable);
    }

    @Override // com.google.android.exoplayer2.extractor.ExtractorOutput
    public void seekMap(SeekMap seekMap) {
        this.seekMap = this.icyHeaders == null ? seekMap : new SeekMap.Unseekable(C.TIME_UNSET);
        this.handler.post(this.maybeFinishPrepareRunnable);
    }

    TrackOutput icyTrack() {
        return prepareTrackOutput(new TrackId(0, true));
    }

    @Override // com.google.android.exoplayer2.source.SampleQueue.UpstreamFormatChangedListener
    public void onUpstreamFormatChanged(Format format) {
        this.handler.post(this.maybeFinishPrepareRunnable);
    }

    private TrackOutput prepareTrackOutput(TrackId id) {
        int trackCount = this.sampleQueues.length;
        for (int i = 0; i < trackCount; i++) {
            if (id.equals(this.sampleQueueTrackIds[i])) {
                return this.sampleQueues[i];
            }
        }
        SampleQueue trackOutput = new SampleQueue(this.allocator, this.handler.getLooper(), this.drmSessionManager);
        trackOutput.setUpstreamFormatChangeListener(this);
        TrackId[] sampleQueueTrackIds = (TrackId[]) Arrays.copyOf(this.sampleQueueTrackIds, trackCount + 1);
        sampleQueueTrackIds[trackCount] = id;
        this.sampleQueueTrackIds = (TrackId[]) Util.castNonNullTypeArray(sampleQueueTrackIds);
        SampleQueue[] sampleQueues = (SampleQueue[]) Arrays.copyOf(this.sampleQueues, trackCount + 1);
        sampleQueues[trackCount] = trackOutput;
        this.sampleQueues = (SampleQueue[]) Util.castNonNullTypeArray(sampleQueues);
        return trackOutput;
    }

    public void maybeFinishPrepare() {
        SampleQueue[] sampleQueueArr;
        SeekMap seekMap = this.seekMap;
        if (this.released || this.prepared || !this.sampleQueuesBuilt || seekMap == null) {
            return;
        }
        boolean z = false;
        for (SampleQueue sampleQueue : this.sampleQueues) {
            if (sampleQueue.getUpstreamFormat() == null) {
                return;
            }
        }
        this.loadCondition.close();
        int trackCount = this.sampleQueues.length;
        TrackGroup[] trackArray = new TrackGroup[trackCount];
        boolean[] trackIsAudioVideoFlags = new boolean[trackCount];
        this.durationUs = seekMap.getDurationUs();
        for (int i = 0; i < trackCount; i++) {
            Format trackFormat = this.sampleQueues[i].getUpstreamFormat();
            String mimeType = trackFormat.sampleMimeType;
            boolean isAudio = MimeTypes.isAudio(mimeType);
            boolean isAudioVideo = isAudio || MimeTypes.isVideo(mimeType);
            trackIsAudioVideoFlags[i] = isAudioVideo;
            this.haveAudioVideoTracks |= isAudioVideo;
            IcyHeaders icyHeaders = this.icyHeaders;
            if (icyHeaders != null) {
                if (isAudio || this.sampleQueueTrackIds[i].isIcyTrack) {
                    Metadata metadata = trackFormat.metadata;
                    trackFormat = trackFormat.copyWithMetadata(metadata == null ? new Metadata(icyHeaders) : metadata.copyWithAppendedEntries(icyHeaders));
                }
                if (isAudio && trackFormat.bitrate == -1 && icyHeaders.bitrate != -1) {
                    trackFormat = trackFormat.copyWithBitrate(icyHeaders.bitrate);
                }
            }
            if (trackFormat.drmInitData != null) {
                trackFormat = trackFormat.copyWithExoMediaCryptoType(this.drmSessionManager.getExoMediaCryptoType(trackFormat.drmInitData));
            }
            trackArray[i] = new TrackGroup(trackFormat);
        }
        if (this.length == -1 && seekMap.getDurationUs() == C.TIME_UNSET) {
            z = true;
        }
        this.isLive = z;
        this.dataType = z ? 7 : 1;
        this.preparedState = new PreparedState(seekMap, new TrackGroupArray(trackArray), trackIsAudioVideoFlags);
        this.prepared = true;
        this.listener.onSourceInfoRefreshed(this.durationUs, seekMap.isSeekable(), this.isLive);
        ((MediaPeriod.Callback) Assertions.checkNotNull(this.callback)).onPrepared(this);
    }

    private PreparedState getPreparedState() {
        return (PreparedState) Assertions.checkNotNull(this.preparedState);
    }

    private void copyLengthFromLoader(ExtractingLoadable loadable) {
        if (this.length != -1) {
            return;
        }
        this.length = loadable.length;
    }

    private void startLoading() {
        ExtractingLoadable loadable = new ExtractingLoadable(this.uri, this.dataSource, this.extractorHolder, this, this.loadCondition);
        if (this.prepared) {
            SeekMap seekMap = getPreparedState().seekMap;
            Assertions.checkState(isPendingReset());
            long j = this.durationUs;
            if (j != C.TIME_UNSET && this.pendingResetPositionUs > j) {
                this.loadingFinished = true;
                this.pendingResetPositionUs = C.TIME_UNSET;
                return;
            }
            loadable.setLoadPosition(seekMap.getSeekPoints(this.pendingResetPositionUs).first.position, this.pendingResetPositionUs);
            this.pendingResetPositionUs = C.TIME_UNSET;
        }
        this.extractedSamplesCountAtStartOfLoad = getExtractedSamplesCount();
        long elapsedRealtimeMs = this.loader.startLoading(loadable, this, this.loadErrorHandlingPolicy.getMinimumLoadableRetryCount(this.dataType));
        this.eventDispatcher.loadStarted(loadable.dataSpec, 1, -1, null, 0, null, loadable.seekTimeUs, this.durationUs, elapsedRealtimeMs);
    }

    private boolean configureRetry(ExtractingLoadable loadable, int currentExtractedSampleCount) {
        SeekMap seekMap;
        SampleQueue[] sampleQueueArr;
        if (this.length != -1 || ((seekMap = this.seekMap) != null && seekMap.getDurationUs() != C.TIME_UNSET)) {
            this.extractedSamplesCountAtStartOfLoad = currentExtractedSampleCount;
            return true;
        }
        if (this.prepared && !suppressRead()) {
            this.pendingDeferredRetry = true;
            return false;
        }
        this.notifyDiscontinuity = this.prepared;
        this.lastSeekPositionUs = 0L;
        this.extractedSamplesCountAtStartOfLoad = 0;
        for (SampleQueue sampleQueue : this.sampleQueues) {
            sampleQueue.reset();
        }
        loadable.setLoadPosition(0L, 0L);
        return true;
    }

    private boolean seekInsideBufferUs(boolean[] trackIsAudioVideoFlags, long positionUs) {
        int trackCount = this.sampleQueues.length;
        for (int i = 0; i < trackCount; i++) {
            SampleQueue sampleQueue = this.sampleQueues[i];
            boolean seekInsideQueue = sampleQueue.seekTo(positionUs, false);
            if (!seekInsideQueue && (trackIsAudioVideoFlags[i] || !this.haveAudioVideoTracks)) {
                return false;
            }
        }
        return true;
    }

    private int getExtractedSamplesCount() {
        SampleQueue[] sampleQueueArr;
        int extractedSamplesCount = 0;
        for (SampleQueue sampleQueue : this.sampleQueues) {
            extractedSamplesCount += sampleQueue.getWriteIndex();
        }
        return extractedSamplesCount;
    }

    public long getLargestQueuedTimestampUs() {
        SampleQueue[] sampleQueueArr;
        long largestQueuedTimestampUs = Long.MIN_VALUE;
        for (SampleQueue sampleQueue : this.sampleQueues) {
            largestQueuedTimestampUs = Math.max(largestQueuedTimestampUs, sampleQueue.getLargestQueuedTimestampUs());
        }
        return largestQueuedTimestampUs;
    }

    private boolean isPendingReset() {
        return this.pendingResetPositionUs != C.TIME_UNSET;
    }

    /* loaded from: classes3.dex */
    private final class SampleStreamImpl implements SampleStream {
        private final int track;

        public SampleStreamImpl(int track) {
            ProgressiveMediaPeriod.this = r1;
            this.track = track;
        }

        @Override // com.google.android.exoplayer2.source.SampleStream
        public boolean isReady() {
            return ProgressiveMediaPeriod.this.isReady(this.track);
        }

        @Override // com.google.android.exoplayer2.source.SampleStream
        public void maybeThrowError() throws IOException {
            ProgressiveMediaPeriod.this.maybeThrowError(this.track);
        }

        @Override // com.google.android.exoplayer2.source.SampleStream
        public int readData(FormatHolder formatHolder, DecoderInputBuffer buffer, boolean formatRequired) {
            return ProgressiveMediaPeriod.this.readData(this.track, formatHolder, buffer, formatRequired);
        }

        @Override // com.google.android.exoplayer2.source.SampleStream
        public int skipData(long positionUs) {
            return ProgressiveMediaPeriod.this.skipData(this.track, positionUs);
        }
    }

    /* loaded from: classes3.dex */
    public final class ExtractingLoadable implements Loader.Loadable, IcyDataSource.Listener {
        private final StatsDataSource dataSource;
        private final ExtractorHolder extractorHolder;
        private final ExtractorOutput extractorOutput;
        private TrackOutput icyTrackOutput;
        private volatile boolean loadCanceled;
        private final ConditionVariable loadCondition;
        private long seekTimeUs;
        private boolean seenIcyMetadata;
        private final Uri uri;
        private final PositionHolder positionHolder = new PositionHolder();
        private boolean pendingExtractorSeek = true;
        private long length = -1;
        private DataSpec dataSpec = buildDataSpec(0);

        public ExtractingLoadable(Uri uri, DataSource dataSource, ExtractorHolder extractorHolder, ExtractorOutput extractorOutput, ConditionVariable loadCondition) {
            ProgressiveMediaPeriod.this = this$0;
            this.uri = uri;
            this.dataSource = new StatsDataSource(dataSource);
            this.extractorHolder = extractorHolder;
            this.extractorOutput = extractorOutput;
            this.loadCondition = loadCondition;
        }

        @Override // com.google.android.exoplayer2.upstream.Loader.Loadable
        public void cancelLoad() {
            this.loadCanceled = true;
        }

        @Override // com.google.android.exoplayer2.upstream.Loader.Loadable
        public void load() throws IOException, InterruptedException {
            DataSource extractorDataSource;
            int result = 0;
            while (result == 0 && !this.loadCanceled) {
                ExtractorInput input = null;
                try {
                    long position = this.positionHolder.position;
                    DataSpec buildDataSpec = buildDataSpec(position);
                    this.dataSpec = buildDataSpec;
                    long open = this.dataSource.open(buildDataSpec);
                    this.length = open;
                    if (open != -1) {
                        this.length = open + position;
                    }
                    Uri uri = (Uri) Assertions.checkNotNull(this.dataSource.getUri());
                    ProgressiveMediaPeriod.this.icyHeaders = IcyHeaders.parse(this.dataSource.getResponseHeaders());
                    DataSource extractorDataSource2 = this.dataSource;
                    if (ProgressiveMediaPeriod.this.icyHeaders != null && ProgressiveMediaPeriod.this.icyHeaders.metadataInterval != -1) {
                        DataSource extractorDataSource3 = new IcyDataSource(this.dataSource, ProgressiveMediaPeriod.this.icyHeaders.metadataInterval, this);
                        TrackOutput icyTrack = ProgressiveMediaPeriod.this.icyTrack();
                        this.icyTrackOutput = icyTrack;
                        icyTrack.format(ProgressiveMediaPeriod.ICY_FORMAT);
                        extractorDataSource = extractorDataSource3;
                    } else {
                        extractorDataSource = extractorDataSource2;
                    }
                    ExtractorInput input2 = new DefaultExtractorInput(extractorDataSource, position, this.length);
                    Extractor extractor = this.extractorHolder.selectExtractor(input2, this.extractorOutput, uri);
                    if (ProgressiveMediaPeriod.this.icyHeaders != null && (extractor instanceof Mp3Extractor)) {
                        ((Mp3Extractor) extractor).disableSeeking();
                    }
                    if (this.pendingExtractorSeek) {
                        extractor.seek(position, this.seekTimeUs);
                        this.pendingExtractorSeek = false;
                    }
                    while (result == 0 && !this.loadCanceled) {
                        this.loadCondition.block();
                        result = extractor.read(input2, this.positionHolder);
                        if (input2.getPosition() > ProgressiveMediaPeriod.this.continueLoadingCheckIntervalBytes + position) {
                            position = input2.getPosition();
                            this.loadCondition.close();
                            ProgressiveMediaPeriod.this.handler.post(ProgressiveMediaPeriod.this.onContinueLoadingRequestedRunnable);
                        }
                    }
                    if (result == 1) {
                        result = 0;
                    } else {
                        this.positionHolder.position = input2.getPosition();
                    }
                    Util.closeQuietly(this.dataSource);
                } catch (Throwable th) {
                    if (result != 1 && 0 != 0) {
                        this.positionHolder.position = input.getPosition();
                    }
                    Util.closeQuietly(this.dataSource);
                    throw th;
                }
            }
        }

        @Override // com.google.android.exoplayer2.source.IcyDataSource.Listener
        public void onIcyMetadata(ParsableByteArray metadata) {
            long timeUs = !this.seenIcyMetadata ? this.seekTimeUs : Math.max(ProgressiveMediaPeriod.this.getLargestQueuedTimestampUs(), this.seekTimeUs);
            int length = metadata.bytesLeft();
            TrackOutput icyTrackOutput = (TrackOutput) Assertions.checkNotNull(this.icyTrackOutput);
            icyTrackOutput.sampleData(metadata, length);
            icyTrackOutput.sampleMetadata(timeUs, 1, length, 0, null);
            this.seenIcyMetadata = true;
        }

        private DataSpec buildDataSpec(long position) {
            return new DataSpec(this.uri, position, -1L, ProgressiveMediaPeriod.this.customCacheKey, 6, ProgressiveMediaPeriod.ICY_METADATA_HEADERS);
        }

        public void setLoadPosition(long position, long timeUs) {
            this.positionHolder.position = position;
            this.seekTimeUs = timeUs;
            this.pendingExtractorSeek = true;
            this.seenIcyMetadata = false;
        }
    }

    /* loaded from: classes3.dex */
    public static final class ExtractorHolder {
        private Extractor extractor;
        private final Extractor[] extractors;

        public ExtractorHolder(Extractor[] extractors) {
            this.extractors = extractors;
        }

        public Extractor selectExtractor(ExtractorInput input, ExtractorOutput output, Uri uri) throws IOException, InterruptedException {
            Extractor extractor = this.extractor;
            if (extractor != null) {
                return extractor;
            }
            Extractor[] extractorArr = this.extractors;
            int i = 0;
            if (extractorArr.length == 1) {
                this.extractor = extractorArr[0];
            } else {
                int length = extractorArr.length;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    Extractor extractor2 = extractorArr[i];
                    try {
                    } catch (EOFException e) {
                    } catch (Throwable th) {
                        input.resetPeekPosition();
                        throw th;
                    }
                    if (extractor2.sniff(input)) {
                        this.extractor = extractor2;
                        input.resetPeekPosition();
                        break;
                    }
                    continue;
                    input.resetPeekPosition();
                    i++;
                }
                if (this.extractor == null) {
                    throw new UnrecognizedInputFormatException("None of the available extractors (" + Util.getCommaDelimitedSimpleClassNames(this.extractors) + ") could read the stream.", uri);
                }
            }
            this.extractor.init(output);
            return this.extractor;
        }

        public void release() {
            Extractor extractor = this.extractor;
            if (extractor != null) {
                extractor.release();
                this.extractor = null;
            }
        }
    }

    /* loaded from: classes3.dex */
    public static final class PreparedState {
        public final SeekMap seekMap;
        public final boolean[] trackEnabledStates;
        public final boolean[] trackIsAudioVideoFlags;
        public final boolean[] trackNotifiedDownstreamFormats;
        public final TrackGroupArray tracks;

        public PreparedState(SeekMap seekMap, TrackGroupArray tracks, boolean[] trackIsAudioVideoFlags) {
            this.seekMap = seekMap;
            this.tracks = tracks;
            this.trackIsAudioVideoFlags = trackIsAudioVideoFlags;
            this.trackEnabledStates = new boolean[tracks.length];
            this.trackNotifiedDownstreamFormats = new boolean[tracks.length];
        }
    }

    /* loaded from: classes3.dex */
    public static final class TrackId {
        public final int id;
        public final boolean isIcyTrack;

        public TrackId(int id, boolean isIcyTrack) {
            this.id = id;
            this.isIcyTrack = isIcyTrack;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            TrackId other = (TrackId) obj;
            return this.id == other.id && this.isIcyTrack == other.isIcyTrack;
        }

        public int hashCode() {
            return (this.id * 31) + (this.isIcyTrack ? 1 : 0);
        }
    }

    private static Map<String, String> createIcyMetadataHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_NAME, IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE);
        return Collections.unmodifiableMap(headers);
    }
}
