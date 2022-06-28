package com.google.android.exoplayer2.source.chunk;

import android.os.Looper;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.SampleQueue;
import com.google.android.exoplayer2.source.SampleStream;
import com.google.android.exoplayer2.source.SequenceableLoader;
import com.google.android.exoplayer2.source.chunk.ChunkSource;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.Loader;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes3.dex */
public class ChunkSampleStream<T extends ChunkSource> implements SampleStream, SequenceableLoader, Loader.Callback<Chunk>, Loader.ReleaseCallback {
    private static final String TAG = "ChunkSampleStream";
    private final SequenceableLoader.Callback<ChunkSampleStream<T>> callback;
    private final BaseMediaChunkOutput chunkOutput;
    private final T chunkSource;
    long decodeOnlyUntilPositionUs;
    private final SampleQueue[] embeddedSampleQueues;
    private final Format[] embeddedTrackFormats;
    private final int[] embeddedTrackTypes;
    private final boolean[] embeddedTracksSelected;
    private final MediaSourceEventListener.EventDispatcher eventDispatcher;
    private long lastSeekPositionUs;
    private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
    boolean loadingFinished;
    private final ArrayList<BaseMediaChunk> mediaChunks;
    private int nextNotifyPrimaryFormatMediaChunkIndex;
    private long pendingResetPositionUs;
    private Format primaryDownstreamTrackFormat;
    private final SampleQueue primarySampleQueue;
    public final int primaryTrackType;
    private final List<BaseMediaChunk> readOnlyMediaChunks;
    private ReleaseCallback<T> releaseCallback;
    private final Loader loader = new Loader("Loader:ChunkSampleStream");
    private final ChunkHolder nextChunkHolder = new ChunkHolder();

    /* loaded from: classes3.dex */
    public interface ReleaseCallback<T extends ChunkSource> {
        void onSampleStreamReleased(ChunkSampleStream<T> chunkSampleStream);
    }

    public ChunkSampleStream(int primaryTrackType, int[] embeddedTrackTypes, Format[] embeddedTrackFormats, T chunkSource, SequenceableLoader.Callback<ChunkSampleStream<T>> callback, Allocator allocator, long positionUs, DrmSessionManager<?> drmSessionManager, LoadErrorHandlingPolicy loadErrorHandlingPolicy, MediaSourceEventListener.EventDispatcher eventDispatcher) {
        this.primaryTrackType = primaryTrackType;
        this.embeddedTrackTypes = embeddedTrackTypes;
        this.embeddedTrackFormats = embeddedTrackFormats;
        this.chunkSource = chunkSource;
        this.callback = callback;
        this.eventDispatcher = eventDispatcher;
        this.loadErrorHandlingPolicy = loadErrorHandlingPolicy;
        ArrayList<BaseMediaChunk> arrayList = new ArrayList<>();
        this.mediaChunks = arrayList;
        this.readOnlyMediaChunks = Collections.unmodifiableList(arrayList);
        int embeddedTrackCount = embeddedTrackTypes == null ? 0 : embeddedTrackTypes.length;
        this.embeddedSampleQueues = new SampleQueue[embeddedTrackCount];
        this.embeddedTracksSelected = new boolean[embeddedTrackCount];
        int[] trackTypes = new int[embeddedTrackCount + 1];
        SampleQueue[] sampleQueues = new SampleQueue[embeddedTrackCount + 1];
        SampleQueue sampleQueue = new SampleQueue(allocator, (Looper) Assertions.checkNotNull(Looper.myLooper()), drmSessionManager);
        this.primarySampleQueue = sampleQueue;
        trackTypes[0] = primaryTrackType;
        sampleQueues[0] = sampleQueue;
        for (int i = 0; i < embeddedTrackCount; i++) {
            SampleQueue sampleQueue2 = new SampleQueue(allocator, (Looper) Assertions.checkNotNull(Looper.myLooper()), DrmSessionManager.CC.getDummyDrmSessionManager());
            this.embeddedSampleQueues[i] = sampleQueue2;
            sampleQueues[i + 1] = sampleQueue2;
            trackTypes[i + 1] = embeddedTrackTypes[i];
        }
        this.chunkOutput = new BaseMediaChunkOutput(trackTypes, sampleQueues);
        this.pendingResetPositionUs = positionUs;
        this.lastSeekPositionUs = positionUs;
    }

    public void discardBuffer(long positionUs, boolean toKeyframe) {
        if (isPendingReset()) {
            return;
        }
        int oldFirstSampleIndex = this.primarySampleQueue.getFirstIndex();
        this.primarySampleQueue.discardTo(positionUs, toKeyframe, true);
        int newFirstSampleIndex = this.primarySampleQueue.getFirstIndex();
        if (newFirstSampleIndex > oldFirstSampleIndex) {
            long discardToUs = this.primarySampleQueue.getFirstTimestampUs();
            int i = 0;
            while (true) {
                SampleQueue[] sampleQueueArr = this.embeddedSampleQueues;
                if (i >= sampleQueueArr.length) {
                    break;
                }
                sampleQueueArr[i].discardTo(discardToUs, toKeyframe, this.embeddedTracksSelected[i]);
                i++;
            }
        }
        discardDownstreamMediaChunks(newFirstSampleIndex);
    }

    public ChunkSampleStream<T>.EmbeddedSampleStream selectEmbeddedTrack(long positionUs, int trackType) {
        for (int i = 0; i < this.embeddedSampleQueues.length; i++) {
            if (this.embeddedTrackTypes[i] == trackType) {
                Assertions.checkState(!this.embeddedTracksSelected[i]);
                this.embeddedTracksSelected[i] = true;
                this.embeddedSampleQueues[i].seekTo(positionUs, true);
                return new EmbeddedSampleStream(this, this.embeddedSampleQueues[i], i);
            }
        }
        throw new IllegalStateException();
    }

    public T getChunkSource() {
        return this.chunkSource;
    }

    @Override // com.google.android.exoplayer2.source.SequenceableLoader
    public long getBufferedPositionUs() {
        BaseMediaChunk lastCompletedMediaChunk;
        if (this.loadingFinished) {
            return Long.MIN_VALUE;
        }
        if (isPendingReset()) {
            return this.pendingResetPositionUs;
        }
        long bufferedPositionUs = this.lastSeekPositionUs;
        BaseMediaChunk lastMediaChunk = getLastMediaChunk();
        if (lastMediaChunk.isLoadCompleted()) {
            lastCompletedMediaChunk = lastMediaChunk;
        } else if (this.mediaChunks.size() > 1) {
            ArrayList<BaseMediaChunk> arrayList = this.mediaChunks;
            lastCompletedMediaChunk = arrayList.get(arrayList.size() - 2);
        } else {
            lastCompletedMediaChunk = null;
        }
        if (lastCompletedMediaChunk != null) {
            bufferedPositionUs = Math.max(bufferedPositionUs, lastCompletedMediaChunk.endTimeUs);
        }
        return Math.max(bufferedPositionUs, this.primarySampleQueue.getLargestQueuedTimestampUs());
    }

    public long getAdjustedSeekPositionUs(long positionUs, SeekParameters seekParameters) {
        return this.chunkSource.getAdjustedSeekPositionUs(positionUs, seekParameters);
    }

    public void seekToUs(long positionUs) {
        boolean seekInsideBuffer;
        this.lastSeekPositionUs = positionUs;
        if (isPendingReset()) {
            this.pendingResetPositionUs = positionUs;
            return;
        }
        BaseMediaChunk seekToMediaChunk = null;
        int i = 0;
        while (true) {
            if (i >= this.mediaChunks.size()) {
                break;
            }
            BaseMediaChunk mediaChunk = this.mediaChunks.get(i);
            long mediaChunkStartTimeUs = mediaChunk.startTimeUs;
            if (mediaChunkStartTimeUs == positionUs && mediaChunk.clippedStartTimeUs == C.TIME_UNSET) {
                seekToMediaChunk = mediaChunk;
                break;
            } else if (mediaChunkStartTimeUs > positionUs) {
                break;
            } else {
                i++;
            }
        }
        int i2 = 0;
        if (seekToMediaChunk != null) {
            seekInsideBuffer = this.primarySampleQueue.seekTo(seekToMediaChunk.getFirstSampleIndex(0));
            this.decodeOnlyUntilPositionUs = 0L;
        } else {
            seekInsideBuffer = this.primarySampleQueue.seekTo(positionUs, positionUs < getNextLoadPositionUs());
            this.decodeOnlyUntilPositionUs = this.lastSeekPositionUs;
        }
        if (seekInsideBuffer) {
            this.nextNotifyPrimaryFormatMediaChunkIndex = primarySampleIndexToMediaChunkIndex(this.primarySampleQueue.getReadIndex(), 0);
            SampleQueue[] sampleQueueArr = this.embeddedSampleQueues;
            int length = sampleQueueArr.length;
            while (i2 < length) {
                SampleQueue embeddedSampleQueue = sampleQueueArr[i2];
                embeddedSampleQueue.seekTo(positionUs, true);
                i2++;
            }
            return;
        }
        this.pendingResetPositionUs = positionUs;
        this.loadingFinished = false;
        this.mediaChunks.clear();
        this.nextNotifyPrimaryFormatMediaChunkIndex = 0;
        if (this.loader.isLoading()) {
            this.loader.cancelLoading();
            return;
        }
        this.loader.clearFatalError();
        this.primarySampleQueue.reset();
        SampleQueue[] sampleQueueArr2 = this.embeddedSampleQueues;
        int length2 = sampleQueueArr2.length;
        while (i2 < length2) {
            SampleQueue embeddedSampleQueue2 = sampleQueueArr2[i2];
            embeddedSampleQueue2.reset();
            i2++;
        }
    }

    public void release() {
        release(null);
    }

    public void release(ReleaseCallback<T> callback) {
        SampleQueue[] sampleQueueArr;
        this.releaseCallback = callback;
        this.primarySampleQueue.preRelease();
        for (SampleQueue embeddedSampleQueue : this.embeddedSampleQueues) {
            embeddedSampleQueue.preRelease();
        }
        this.loader.release(this);
    }

    @Override // com.google.android.exoplayer2.upstream.Loader.ReleaseCallback
    public void onLoaderReleased() {
        SampleQueue[] sampleQueueArr;
        this.primarySampleQueue.release();
        for (SampleQueue embeddedSampleQueue : this.embeddedSampleQueues) {
            embeddedSampleQueue.release();
        }
        ReleaseCallback<T> releaseCallback = this.releaseCallback;
        if (releaseCallback != null) {
            releaseCallback.onSampleStreamReleased(this);
        }
    }

    @Override // com.google.android.exoplayer2.source.SampleStream
    public boolean isReady() {
        return !isPendingReset() && this.primarySampleQueue.isReady(this.loadingFinished);
    }

    @Override // com.google.android.exoplayer2.source.SampleStream
    public void maybeThrowError() throws IOException {
        this.loader.maybeThrowError();
        this.primarySampleQueue.maybeThrowError();
        if (!this.loader.isLoading()) {
            this.chunkSource.maybeThrowError();
        }
    }

    @Override // com.google.android.exoplayer2.source.SampleStream
    public int readData(FormatHolder formatHolder, DecoderInputBuffer buffer, boolean formatRequired) {
        if (isPendingReset()) {
            return -3;
        }
        maybeNotifyPrimaryTrackFormatChanged();
        return this.primarySampleQueue.read(formatHolder, buffer, formatRequired, this.loadingFinished, this.decodeOnlyUntilPositionUs);
    }

    @Override // com.google.android.exoplayer2.source.SampleStream
    public int skipData(long positionUs) {
        int skipCount;
        if (isPendingReset()) {
            return 0;
        }
        if (this.loadingFinished && positionUs > this.primarySampleQueue.getLargestQueuedTimestampUs()) {
            skipCount = this.primarySampleQueue.advanceToEnd();
        } else {
            skipCount = this.primarySampleQueue.advanceTo(positionUs);
        }
        maybeNotifyPrimaryTrackFormatChanged();
        return skipCount;
    }

    public void onLoadCompleted(Chunk loadable, long elapsedRealtimeMs, long loadDurationMs) {
        this.chunkSource.onChunkLoadCompleted(loadable);
        this.eventDispatcher.loadCompleted(loadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), loadable.type, this.primaryTrackType, loadable.trackFormat, loadable.trackSelectionReason, loadable.trackSelectionData, loadable.startTimeUs, loadable.endTimeUs, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
        this.callback.onContinueLoadingRequested(this);
    }

    public void onLoadCanceled(Chunk loadable, long elapsedRealtimeMs, long loadDurationMs, boolean released) {
        SampleQueue[] sampleQueueArr;
        this.eventDispatcher.loadCanceled(loadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), loadable.type, this.primaryTrackType, loadable.trackFormat, loadable.trackSelectionReason, loadable.trackSelectionData, loadable.startTimeUs, loadable.endTimeUs, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
        if (!released) {
            this.primarySampleQueue.reset();
            for (SampleQueue embeddedSampleQueue : this.embeddedSampleQueues) {
                embeddedSampleQueue.reset();
            }
            this.callback.onContinueLoadingRequested(this);
        }
    }

    public Loader.LoadErrorAction onLoadError(Chunk loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error, int errorCount) {
        long blacklistDurationMs;
        Loader.LoadErrorAction loadErrorAction;
        Loader.LoadErrorAction loadErrorAction2;
        long bytesLoaded = loadable.bytesLoaded();
        boolean isMediaChunk = isMediaChunk(loadable);
        int lastChunkIndex = this.mediaChunks.size() - 1;
        boolean cancelable = bytesLoaded == 0 || !isMediaChunk || !haveReadFromMediaChunk(lastChunkIndex);
        if (cancelable) {
            blacklistDurationMs = this.loadErrorHandlingPolicy.getBlacklistDurationMsFor(loadable.type, loadDurationMs, error, errorCount);
        } else {
            blacklistDurationMs = -9223372036854775807L;
        }
        Loader.LoadErrorAction loadErrorAction3 = null;
        if (this.chunkSource.onChunkLoadError(loadable, cancelable, error, blacklistDurationMs)) {
            if (cancelable) {
                loadErrorAction3 = Loader.DONT_RETRY;
                if (isMediaChunk) {
                    BaseMediaChunk removed = discardUpstreamMediaChunksFromIndex(lastChunkIndex);
                    Assertions.checkState(removed == loadable);
                    if (this.mediaChunks.isEmpty()) {
                        this.pendingResetPositionUs = this.lastSeekPositionUs;
                    }
                }
            } else {
                Log.w(TAG, "Ignoring attempt to cancel non-cancelable load.");
            }
        }
        if (loadErrorAction3 != null) {
            loadErrorAction = loadErrorAction3;
        } else {
            long retryDelayMs = this.loadErrorHandlingPolicy.getRetryDelayMsFor(loadable.type, loadDurationMs, error, errorCount);
            if (retryDelayMs != C.TIME_UNSET) {
                loadErrorAction2 = Loader.createRetryAction(false, retryDelayMs);
            } else {
                loadErrorAction2 = Loader.DONT_RETRY_FATAL;
            }
            loadErrorAction = loadErrorAction2;
        }
        boolean canceled = !loadErrorAction.isRetry();
        this.eventDispatcher.loadError(loadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), loadable.type, this.primaryTrackType, loadable.trackFormat, loadable.trackSelectionReason, loadable.trackSelectionData, loadable.startTimeUs, loadable.endTimeUs, elapsedRealtimeMs, loadDurationMs, bytesLoaded, error, canceled);
        if (canceled) {
            this.callback.onContinueLoadingRequested(this);
        }
        return loadErrorAction;
    }

    @Override // com.google.android.exoplayer2.source.SequenceableLoader
    public boolean continueLoading(long positionUs) {
        long loadPositionUs;
        List<BaseMediaChunk> chunkQueue;
        boolean resetToMediaChunk = false;
        if (this.loadingFinished || this.loader.isLoading() || this.loader.hasFatalError()) {
            return false;
        }
        boolean pendingReset = isPendingReset();
        if (pendingReset) {
            chunkQueue = Collections.emptyList();
            loadPositionUs = this.pendingResetPositionUs;
        } else {
            chunkQueue = this.readOnlyMediaChunks;
            loadPositionUs = getLastMediaChunk().endTimeUs;
        }
        this.chunkSource.getNextChunk(positionUs, loadPositionUs, chunkQueue, this.nextChunkHolder);
        boolean endOfStream = this.nextChunkHolder.endOfStream;
        Chunk loadable = this.nextChunkHolder.chunk;
        this.nextChunkHolder.clear();
        if (endOfStream) {
            this.pendingResetPositionUs = C.TIME_UNSET;
            this.loadingFinished = true;
            return true;
        } else if (loadable == null) {
            return false;
        } else {
            if (isMediaChunk(loadable)) {
                BaseMediaChunk mediaChunk = (BaseMediaChunk) loadable;
                if (pendingReset) {
                    long j = mediaChunk.startTimeUs;
                    long j2 = this.pendingResetPositionUs;
                    if (j == j2) {
                        resetToMediaChunk = true;
                    }
                    if (resetToMediaChunk) {
                        j2 = 0;
                    }
                    this.decodeOnlyUntilPositionUs = j2;
                    this.pendingResetPositionUs = C.TIME_UNSET;
                }
                mediaChunk.init(this.chunkOutput);
                this.mediaChunks.add(mediaChunk);
            } else if (loadable instanceof InitializationChunk) {
                ((InitializationChunk) loadable).init(this.chunkOutput);
            }
            long elapsedRealtimeMs = this.loader.startLoading(loadable, this, this.loadErrorHandlingPolicy.getMinimumLoadableRetryCount(loadable.type));
            this.eventDispatcher.loadStarted(loadable.dataSpec, loadable.type, this.primaryTrackType, loadable.trackFormat, loadable.trackSelectionReason, loadable.trackSelectionData, loadable.startTimeUs, loadable.endTimeUs, elapsedRealtimeMs);
            return true;
        }
    }

    @Override // com.google.android.exoplayer2.source.SequenceableLoader
    public boolean isLoading() {
        return this.loader.isLoading();
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
    public void reevaluateBuffer(long positionUs) {
        int currentQueueSize;
        int preferredQueueSize;
        if (this.loader.isLoading() || this.loader.hasFatalError() || isPendingReset() || (currentQueueSize = this.mediaChunks.size()) <= (preferredQueueSize = this.chunkSource.getPreferredQueueSize(positionUs, this.readOnlyMediaChunks))) {
            return;
        }
        int newQueueSize = currentQueueSize;
        int i = preferredQueueSize;
        while (true) {
            if (i >= currentQueueSize) {
                break;
            } else if (haveReadFromMediaChunk(i)) {
                i++;
            } else {
                newQueueSize = i;
                break;
            }
        }
        if (newQueueSize == currentQueueSize) {
            return;
        }
        long endTimeUs = getLastMediaChunk().endTimeUs;
        BaseMediaChunk firstRemovedChunk = discardUpstreamMediaChunksFromIndex(newQueueSize);
        if (this.mediaChunks.isEmpty()) {
            this.pendingResetPositionUs = this.lastSeekPositionUs;
        }
        this.loadingFinished = false;
        this.eventDispatcher.upstreamDiscarded(this.primaryTrackType, firstRemovedChunk.startTimeUs, endTimeUs);
    }

    private boolean isMediaChunk(Chunk chunk) {
        return chunk instanceof BaseMediaChunk;
    }

    private boolean haveReadFromMediaChunk(int mediaChunkIndex) {
        BaseMediaChunk mediaChunk = this.mediaChunks.get(mediaChunkIndex);
        if (this.primarySampleQueue.getReadIndex() > mediaChunk.getFirstSampleIndex(0)) {
            return true;
        }
        int i = 0;
        while (true) {
            SampleQueue[] sampleQueueArr = this.embeddedSampleQueues;
            if (i >= sampleQueueArr.length) {
                return false;
            }
            if (sampleQueueArr[i].getReadIndex() > mediaChunk.getFirstSampleIndex(i + 1)) {
                return true;
            }
            i++;
        }
    }

    boolean isPendingReset() {
        return this.pendingResetPositionUs != C.TIME_UNSET;
    }

    private void discardDownstreamMediaChunks(int discardToSampleIndex) {
        int discardToMediaChunkIndex = Math.min(primarySampleIndexToMediaChunkIndex(discardToSampleIndex, 0), this.nextNotifyPrimaryFormatMediaChunkIndex);
        if (discardToMediaChunkIndex > 0) {
            Util.removeRange(this.mediaChunks, 0, discardToMediaChunkIndex);
            this.nextNotifyPrimaryFormatMediaChunkIndex -= discardToMediaChunkIndex;
        }
    }

    private void maybeNotifyPrimaryTrackFormatChanged() {
        int readSampleIndex = this.primarySampleQueue.getReadIndex();
        int notifyToMediaChunkIndex = primarySampleIndexToMediaChunkIndex(readSampleIndex, this.nextNotifyPrimaryFormatMediaChunkIndex - 1);
        while (true) {
            int i = this.nextNotifyPrimaryFormatMediaChunkIndex;
            if (i <= notifyToMediaChunkIndex) {
                this.nextNotifyPrimaryFormatMediaChunkIndex = i + 1;
                maybeNotifyPrimaryTrackFormatChanged(i);
            } else {
                return;
            }
        }
    }

    private void maybeNotifyPrimaryTrackFormatChanged(int mediaChunkReadIndex) {
        BaseMediaChunk currentChunk = this.mediaChunks.get(mediaChunkReadIndex);
        Format trackFormat = currentChunk.trackFormat;
        if (!trackFormat.equals(this.primaryDownstreamTrackFormat)) {
            this.eventDispatcher.downstreamFormatChanged(this.primaryTrackType, trackFormat, currentChunk.trackSelectionReason, currentChunk.trackSelectionData, currentChunk.startTimeUs);
        }
        this.primaryDownstreamTrackFormat = trackFormat;
    }

    private int primarySampleIndexToMediaChunkIndex(int primarySampleIndex, int minChunkIndex) {
        for (int i = minChunkIndex + 1; i < this.mediaChunks.size(); i++) {
            if (this.mediaChunks.get(i).getFirstSampleIndex(0) > primarySampleIndex) {
                return i - 1;
            }
        }
        return this.mediaChunks.size() - 1;
    }

    private BaseMediaChunk getLastMediaChunk() {
        ArrayList<BaseMediaChunk> arrayList = this.mediaChunks;
        return arrayList.get(arrayList.size() - 1);
    }

    private BaseMediaChunk discardUpstreamMediaChunksFromIndex(int chunkIndex) {
        BaseMediaChunk firstRemovedChunk = this.mediaChunks.get(chunkIndex);
        ArrayList<BaseMediaChunk> arrayList = this.mediaChunks;
        Util.removeRange(arrayList, chunkIndex, arrayList.size());
        this.nextNotifyPrimaryFormatMediaChunkIndex = Math.max(this.nextNotifyPrimaryFormatMediaChunkIndex, this.mediaChunks.size());
        this.primarySampleQueue.discardUpstreamSamples(firstRemovedChunk.getFirstSampleIndex(0));
        int i = 0;
        while (true) {
            SampleQueue[] sampleQueueArr = this.embeddedSampleQueues;
            if (i < sampleQueueArr.length) {
                sampleQueueArr[i].discardUpstreamSamples(firstRemovedChunk.getFirstSampleIndex(i + 1));
                i++;
            } else {
                return firstRemovedChunk;
            }
        }
    }

    /* loaded from: classes3.dex */
    public final class EmbeddedSampleStream implements SampleStream {
        private final int index;
        private boolean notifiedDownstreamFormat;
        public final ChunkSampleStream<T> parent;
        private final SampleQueue sampleQueue;

        public EmbeddedSampleStream(ChunkSampleStream<T> parent, SampleQueue sampleQueue, int index) {
            ChunkSampleStream.this = this$0;
            this.parent = parent;
            this.sampleQueue = sampleQueue;
            this.index = index;
        }

        @Override // com.google.android.exoplayer2.source.SampleStream
        public boolean isReady() {
            return !ChunkSampleStream.this.isPendingReset() && this.sampleQueue.isReady(ChunkSampleStream.this.loadingFinished);
        }

        @Override // com.google.android.exoplayer2.source.SampleStream
        public int skipData(long positionUs) {
            if (ChunkSampleStream.this.isPendingReset()) {
                return 0;
            }
            maybeNotifyDownstreamFormat();
            if (ChunkSampleStream.this.loadingFinished && positionUs > this.sampleQueue.getLargestQueuedTimestampUs()) {
                int skipCount = this.sampleQueue.advanceToEnd();
                return skipCount;
            }
            int skipCount2 = this.sampleQueue.advanceTo(positionUs);
            return skipCount2;
        }

        @Override // com.google.android.exoplayer2.source.SampleStream
        public void maybeThrowError() throws IOException {
        }

        @Override // com.google.android.exoplayer2.source.SampleStream
        public int readData(FormatHolder formatHolder, DecoderInputBuffer buffer, boolean formatRequired) {
            if (ChunkSampleStream.this.isPendingReset()) {
                return -3;
            }
            maybeNotifyDownstreamFormat();
            return this.sampleQueue.read(formatHolder, buffer, formatRequired, ChunkSampleStream.this.loadingFinished, ChunkSampleStream.this.decodeOnlyUntilPositionUs);
        }

        public void release() {
            Assertions.checkState(ChunkSampleStream.this.embeddedTracksSelected[this.index]);
            ChunkSampleStream.this.embeddedTracksSelected[this.index] = false;
        }

        private void maybeNotifyDownstreamFormat() {
            if (!this.notifiedDownstreamFormat) {
                ChunkSampleStream.this.eventDispatcher.downstreamFormatChanged(ChunkSampleStream.this.embeddedTrackTypes[this.index], ChunkSampleStream.this.embeddedTrackFormats[this.index], 0, null, ChunkSampleStream.this.lastSeekPositionUs);
                this.notifiedDownstreamFormat = true;
            }
        }
    }
}
