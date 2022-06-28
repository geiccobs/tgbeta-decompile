package com.google.android.exoplayer2.source;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.Loader;
import com.google.android.exoplayer2.upstream.StatsDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/* loaded from: classes3.dex */
public final class SingleSampleMediaPeriod implements MediaPeriod, Loader.Callback<SourceLoadable> {
    private static final int INITIAL_SAMPLE_SIZE = 1024;
    private final DataSource.Factory dataSourceFactory;
    private final DataSpec dataSpec;
    private final long durationUs;
    private final MediaSourceEventListener.EventDispatcher eventDispatcher;
    final Format format;
    private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
    boolean loadingFinished;
    boolean notifiedReadingStarted;
    byte[] sampleData;
    int sampleSize;
    private final TrackGroupArray tracks;
    private final TransferListener transferListener;
    final boolean treatLoadErrorsAsEndOfStream;
    private final ArrayList<SampleStreamImpl> sampleStreams = new ArrayList<>();
    final Loader loader = new Loader("Loader:SingleSampleMediaPeriod");

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public /* synthetic */ List getStreamKeys(List list) {
        List emptyList;
        emptyList = Collections.emptyList();
        return emptyList;
    }

    public SingleSampleMediaPeriod(DataSpec dataSpec, DataSource.Factory dataSourceFactory, TransferListener transferListener, Format format, long durationUs, LoadErrorHandlingPolicy loadErrorHandlingPolicy, MediaSourceEventListener.EventDispatcher eventDispatcher, boolean treatLoadErrorsAsEndOfStream) {
        this.dataSpec = dataSpec;
        this.dataSourceFactory = dataSourceFactory;
        this.transferListener = transferListener;
        this.format = format;
        this.durationUs = durationUs;
        this.loadErrorHandlingPolicy = loadErrorHandlingPolicy;
        this.eventDispatcher = eventDispatcher;
        this.treatLoadErrorsAsEndOfStream = treatLoadErrorsAsEndOfStream;
        this.tracks = new TrackGroupArray(new TrackGroup(format));
        eventDispatcher.mediaPeriodCreated();
    }

    public void release() {
        this.loader.release();
        this.eventDispatcher.mediaPeriodReleased();
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public void prepare(MediaPeriod.Callback callback, long positionUs) {
        callback.onPrepared(this);
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public void maybeThrowPrepareError() throws IOException {
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public TrackGroupArray getTrackGroups() {
        return this.tracks;
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public long selectTracks(TrackSelection[] selections, boolean[] mayRetainStreamFlags, SampleStream[] streams, boolean[] streamResetFlags, long positionUs) {
        for (int i = 0; i < selections.length; i++) {
            if (streams[i] != null && (selections[i] == null || !mayRetainStreamFlags[i])) {
                this.sampleStreams.remove(streams[i]);
                streams[i] = null;
            }
            if (streams[i] == null && selections[i] != null) {
                SampleStreamImpl stream = new SampleStreamImpl();
                this.sampleStreams.add(stream);
                streams[i] = stream;
                streamResetFlags[i] = true;
            }
        }
        return positionUs;
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public void discardBuffer(long positionUs, boolean toKeyframe) {
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
    public void reevaluateBuffer(long positionUs) {
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
    public boolean continueLoading(long positionUs) {
        if (this.loadingFinished || this.loader.isLoading() || this.loader.hasFatalError()) {
            return false;
        }
        DataSource dataSource = this.dataSourceFactory.createDataSource();
        TransferListener transferListener = this.transferListener;
        if (transferListener != null) {
            dataSource.addTransferListener(transferListener);
        }
        long elapsedRealtimeMs = this.loader.startLoading(new SourceLoadable(this.dataSpec, dataSource), this, this.loadErrorHandlingPolicy.getMinimumLoadableRetryCount(1));
        this.eventDispatcher.loadStarted(this.dataSpec, 1, -1, this.format, 0, null, 0L, this.durationUs, elapsedRealtimeMs);
        return true;
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
    public boolean isLoading() {
        return this.loader.isLoading();
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
    public long getNextLoadPositionUs() {
        return (this.loadingFinished || this.loader.isLoading()) ? Long.MIN_VALUE : 0L;
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
    public long getBufferedPositionUs() {
        return this.loadingFinished ? Long.MIN_VALUE : 0L;
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public long seekToUs(long positionUs) {
        for (int i = 0; i < this.sampleStreams.size(); i++) {
            this.sampleStreams.get(i).reset();
        }
        return positionUs;
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public long getAdjustedSeekPositionUs(long positionUs, SeekParameters seekParameters) {
        return positionUs;
    }

    public void onLoadCompleted(SourceLoadable loadable, long elapsedRealtimeMs, long loadDurationMs) {
        this.sampleSize = (int) loadable.dataSource.getBytesRead();
        this.sampleData = (byte[]) Assertions.checkNotNull(loadable.sampleData);
        this.loadingFinished = true;
        this.eventDispatcher.loadCompleted(loadable.dataSpec, loadable.dataSource.getLastOpenedUri(), loadable.dataSource.getLastResponseHeaders(), 1, -1, this.format, 0, null, 0L, this.durationUs, elapsedRealtimeMs, loadDurationMs, this.sampleSize);
    }

    public void onLoadCanceled(SourceLoadable loadable, long elapsedRealtimeMs, long loadDurationMs, boolean released) {
        this.eventDispatcher.loadCanceled(loadable.dataSpec, loadable.dataSource.getLastOpenedUri(), loadable.dataSource.getLastResponseHeaders(), 1, -1, null, 0, null, 0L, this.durationUs, elapsedRealtimeMs, loadDurationMs, loadable.dataSource.getBytesRead());
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x0039  */
    /* JADX WARN: Removed duplicated region for block: B:16:0x003e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public com.google.android.exoplayer2.upstream.Loader.LoadErrorAction onLoadError(com.google.android.exoplayer2.source.SingleSampleMediaPeriod.SourceLoadable r31, long r32, long r34, java.io.IOException r36, int r37) {
        /*
            r30 = this;
            r0 = r30
            com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy r1 = r0.loadErrorHandlingPolicy
            r2 = 1
            r3 = r34
            r5 = r36
            r6 = r37
            long r1 = r1.getRetryDelayMsFor(r2, r3, r5, r6)
            r3 = 0
            r4 = -9223372036854775807(0x8000000000000001, double:-4.9E-324)
            r6 = 1
            int r7 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r7 == 0) goto L27
            com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy r7 = r0.loadErrorHandlingPolicy
            int r7 = r7.getMinimumLoadableRetryCount(r6)
            r8 = r37
            if (r8 < r7) goto L25
            goto L29
        L25:
            r7 = 0
            goto L2a
        L27:
            r8 = r37
        L29:
            r7 = 1
        L2a:
            boolean r9 = r0.treatLoadErrorsAsEndOfStream
            if (r9 == 0) goto L35
            if (r7 == 0) goto L35
            r0.loadingFinished = r6
            com.google.android.exoplayer2.upstream.Loader$LoadErrorAction r3 = com.google.android.exoplayer2.upstream.Loader.DONT_RETRY
            goto L41
        L35:
            int r9 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r9 == 0) goto L3e
            com.google.android.exoplayer2.upstream.Loader$LoadErrorAction r3 = com.google.android.exoplayer2.upstream.Loader.createRetryAction(r3, r1)
            goto L40
        L3e:
            com.google.android.exoplayer2.upstream.Loader$LoadErrorAction r3 = com.google.android.exoplayer2.upstream.Loader.DONT_RETRY_FATAL
        L40:
        L41:
            com.google.android.exoplayer2.source.MediaSourceEventListener$EventDispatcher r9 = r0.eventDispatcher
            r4 = r31
            com.google.android.exoplayer2.upstream.DataSpec r10 = r4.dataSpec
            com.google.android.exoplayer2.upstream.StatsDataSource r5 = com.google.android.exoplayer2.source.SingleSampleMediaPeriod.SourceLoadable.access$100(r31)
            android.net.Uri r11 = r5.getLastOpenedUri()
            com.google.android.exoplayer2.upstream.StatsDataSource r5 = com.google.android.exoplayer2.source.SingleSampleMediaPeriod.SourceLoadable.access$100(r31)
            java.util.Map r12 = r5.getLastResponseHeaders()
            r13 = 1
            com.google.android.exoplayer2.Format r15 = r0.format
            r16 = 0
            r17 = 0
            r18 = 0
            r20 = r15
            long r14 = r0.durationUs
            com.google.android.exoplayer2.upstream.StatsDataSource r21 = com.google.android.exoplayer2.source.SingleSampleMediaPeriod.SourceLoadable.access$100(r31)
            long r26 = r21.getBytesRead()
            boolean r21 = r3.isRetry()
            r29 = r21 ^ 1
            r21 = r14
            r5 = -1
            r14 = r5
            r15 = r20
            r20 = r21
            r22 = r32
            r24 = r34
            r28 = r36
            r9.loadError(r10, r11, r12, r13, r14, r15, r16, r17, r18, r20, r22, r24, r26, r28, r29)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.SingleSampleMediaPeriod.onLoadError(com.google.android.exoplayer2.source.SingleSampleMediaPeriod$SourceLoadable, long, long, java.io.IOException, int):com.google.android.exoplayer2.upstream.Loader$LoadErrorAction");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public final class SampleStreamImpl implements SampleStream {
        private static final int STREAM_STATE_END_OF_STREAM = 2;
        private static final int STREAM_STATE_SEND_FORMAT = 0;
        private static final int STREAM_STATE_SEND_SAMPLE = 1;
        private boolean notifiedDownstreamFormat;
        private int streamState;

        private SampleStreamImpl() {
            SingleSampleMediaPeriod.this = r1;
        }

        public void reset() {
            if (this.streamState == 2) {
                this.streamState = 1;
            }
        }

        @Override // com.google.android.exoplayer2.source.SampleStream
        public boolean isReady() {
            return SingleSampleMediaPeriod.this.loadingFinished;
        }

        @Override // com.google.android.exoplayer2.source.SampleStream
        public void maybeThrowError() throws IOException {
            if (!SingleSampleMediaPeriod.this.treatLoadErrorsAsEndOfStream) {
                SingleSampleMediaPeriod.this.loader.maybeThrowError();
            }
        }

        @Override // com.google.android.exoplayer2.source.SampleStream
        public int readData(FormatHolder formatHolder, DecoderInputBuffer buffer, boolean requireFormat) {
            maybeNotifyDownstreamFormat();
            int i = this.streamState;
            if (i == 2) {
                buffer.addFlag(4);
                return -4;
            } else if (requireFormat || i == 0) {
                formatHolder.format = SingleSampleMediaPeriod.this.format;
                this.streamState = 1;
                return -5;
            } else if (SingleSampleMediaPeriod.this.loadingFinished) {
                if (SingleSampleMediaPeriod.this.sampleData != null) {
                    buffer.addFlag(1);
                    buffer.timeUs = 0L;
                    if (buffer.isFlagsOnly()) {
                        return -4;
                    }
                    buffer.ensureSpaceForWrite(SingleSampleMediaPeriod.this.sampleSize);
                    buffer.data.put(SingleSampleMediaPeriod.this.sampleData, 0, SingleSampleMediaPeriod.this.sampleSize);
                } else {
                    buffer.addFlag(4);
                }
                this.streamState = 2;
                return -4;
            } else {
                return -3;
            }
        }

        @Override // com.google.android.exoplayer2.source.SampleStream
        public int skipData(long positionUs) {
            maybeNotifyDownstreamFormat();
            if (positionUs > 0 && this.streamState != 2) {
                this.streamState = 2;
                return 1;
            }
            return 0;
        }

        private void maybeNotifyDownstreamFormat() {
            if (!this.notifiedDownstreamFormat) {
                SingleSampleMediaPeriod.this.eventDispatcher.downstreamFormatChanged(MimeTypes.getTrackType(SingleSampleMediaPeriod.this.format.sampleMimeType), SingleSampleMediaPeriod.this.format, 0, null, 0L);
                this.notifiedDownstreamFormat = true;
            }
        }
    }

    /* loaded from: classes3.dex */
    public static final class SourceLoadable implements Loader.Loadable {
        private final StatsDataSource dataSource;
        public final DataSpec dataSpec;
        private byte[] sampleData;

        public SourceLoadable(DataSpec dataSpec, DataSource dataSource) {
            this.dataSpec = dataSpec;
            this.dataSource = new StatsDataSource(dataSource);
        }

        @Override // com.google.android.exoplayer2.upstream.Loader.Loadable
        public void cancelLoad() {
        }

        @Override // com.google.android.exoplayer2.upstream.Loader.Loadable
        public void load() throws IOException, InterruptedException {
            this.dataSource.resetBytesRead();
            try {
                this.dataSource.open(this.dataSpec);
                int result = 0;
                while (result != -1) {
                    int sampleSize = (int) this.dataSource.getBytesRead();
                    byte[] bArr = this.sampleData;
                    if (bArr == null) {
                        this.sampleData = new byte[1024];
                    } else if (sampleSize == bArr.length) {
                        this.sampleData = Arrays.copyOf(bArr, bArr.length * 2);
                    }
                    StatsDataSource statsDataSource = this.dataSource;
                    byte[] bArr2 = this.sampleData;
                    result = statsDataSource.read(bArr2, sampleSize, bArr2.length - sampleSize);
                }
            } finally {
                Util.closeQuietly(this.dataSource);
            }
        }
    }
}
