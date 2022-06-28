package com.google.android.exoplayer2.source.dash;

import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.SparseArray;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerLibraryInfo;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.offline.FilteringManifestParser;
import com.google.android.exoplayer2.offline.StreamKey;
import com.google.android.exoplayer2.source.BaseMediaSource;
import com.google.android.exoplayer2.source.CompositeSequenceableLoaderFactory;
import com.google.android.exoplayer2.source.DefaultCompositeSequenceableLoaderFactory;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.source.dash.DashChunkSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.dash.PlayerEmsgHandler;
import com.google.android.exoplayer2.source.dash.manifest.AdaptationSet;
import com.google.android.exoplayer2.source.dash.manifest.DashManifest;
import com.google.android.exoplayer2.source.dash.manifest.DashManifestParser;
import com.google.android.exoplayer2.source.dash.manifest.Period;
import com.google.android.exoplayer2.source.dash.manifest.UtcTimingElement;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.Loader;
import com.google.android.exoplayer2.upstream.LoaderErrorThrower;
import com.google.android.exoplayer2.upstream.ParsingLoadable;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import j$.util.DesugarTimeZone;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes3.dex */
public final class DashMediaSource extends BaseMediaSource {
    @Deprecated
    public static final long DEFAULT_LIVE_PRESENTATION_DELAY_FIXED_MS = 30000;
    public static final long DEFAULT_LIVE_PRESENTATION_DELAY_MS = 30000;
    @Deprecated
    public static final long DEFAULT_LIVE_PRESENTATION_DELAY_PREFER_MANIFEST_MS = -1;
    private static final long MIN_LIVE_DEFAULT_START_POSITION_US = 5000000;
    private static final int NOTIFY_MANIFEST_INTERVAL_MS = 5000;
    private static final String TAG = "DashMediaSource";
    private final DashChunkSource.Factory chunkSourceFactory;
    private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
    private DataSource dataSource;
    private final DrmSessionManager<?> drmSessionManager;
    private long elapsedRealtimeOffsetMs;
    private long expiredManifestPublishTimeUs;
    private int firstPeriodId;
    private Handler handler;
    private Uri initialManifestUri;
    private final long livePresentationDelayMs;
    private final boolean livePresentationDelayOverridesManifest;
    private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
    private Loader loader;
    private DashManifest manifest;
    private final ManifestCallback manifestCallback;
    private final DataSource.Factory manifestDataSourceFactory;
    private final MediaSourceEventListener.EventDispatcher manifestEventDispatcher;
    private IOException manifestFatalError;
    private long manifestLoadEndTimestampMs;
    private final LoaderErrorThrower manifestLoadErrorThrower;
    private boolean manifestLoadPending;
    private long manifestLoadStartTimestampMs;
    private final ParsingLoadable.Parser<? extends DashManifest> manifestParser;
    private Uri manifestUri;
    private final Object manifestUriLock;
    private TransferListener mediaTransferListener;
    private final SparseArray<DashMediaPeriod> periodsById;
    private final PlayerEmsgHandler.PlayerEmsgCallback playerEmsgCallback;
    private final Runnable refreshManifestRunnable;
    private final boolean sideloadedManifest;
    private final Runnable simulateManifestRefreshRunnable;
    private int staleManifestReloadAttempt;
    private final Object tag;

    static {
        ExoPlayerLibraryInfo.registerModule("goog.exo.dash");
    }

    /* loaded from: classes3.dex */
    public static final class Factory implements MediaSourceFactory {
        private final DashChunkSource.Factory chunkSourceFactory;
        private CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
        private DrmSessionManager<?> drmSessionManager;
        private boolean isCreateCalled;
        private long livePresentationDelayMs;
        private boolean livePresentationDelayOverridesManifest;
        private LoadErrorHandlingPolicy loadErrorHandlingPolicy;
        private final DataSource.Factory manifestDataSourceFactory;
        private ParsingLoadable.Parser<? extends DashManifest> manifestParser;
        private List<StreamKey> streamKeys;
        private Object tag;

        public Factory(DataSource.Factory dataSourceFactory) {
            this(new DefaultDashChunkSource.Factory(dataSourceFactory), dataSourceFactory);
        }

        public Factory(DashChunkSource.Factory chunkSourceFactory, DataSource.Factory manifestDataSourceFactory) {
            this.chunkSourceFactory = (DashChunkSource.Factory) Assertions.checkNotNull(chunkSourceFactory);
            this.manifestDataSourceFactory = manifestDataSourceFactory;
            this.drmSessionManager = DrmSessionManager.CC.getDummyDrmSessionManager();
            this.loadErrorHandlingPolicy = new DefaultLoadErrorHandlingPolicy();
            this.livePresentationDelayMs = 30000L;
            this.compositeSequenceableLoaderFactory = new DefaultCompositeSequenceableLoaderFactory();
        }

        public Factory setTag(Object tag) {
            Assertions.checkState(!this.isCreateCalled);
            this.tag = tag;
            return this;
        }

        @Deprecated
        public Factory setMinLoadableRetryCount(int minLoadableRetryCount) {
            return setLoadErrorHandlingPolicy(new DefaultLoadErrorHandlingPolicy(minLoadableRetryCount));
        }

        public Factory setLoadErrorHandlingPolicy(LoadErrorHandlingPolicy loadErrorHandlingPolicy) {
            Assertions.checkState(!this.isCreateCalled);
            this.loadErrorHandlingPolicy = loadErrorHandlingPolicy;
            return this;
        }

        @Deprecated
        public Factory setLivePresentationDelayMs(long livePresentationDelayMs) {
            if (livePresentationDelayMs == -1) {
                return setLivePresentationDelayMs(30000L, false);
            }
            return setLivePresentationDelayMs(livePresentationDelayMs, true);
        }

        public Factory setLivePresentationDelayMs(long livePresentationDelayMs, boolean overridesManifest) {
            Assertions.checkState(!this.isCreateCalled);
            this.livePresentationDelayMs = livePresentationDelayMs;
            this.livePresentationDelayOverridesManifest = overridesManifest;
            return this;
        }

        public Factory setManifestParser(ParsingLoadable.Parser<? extends DashManifest> manifestParser) {
            Assertions.checkState(!this.isCreateCalled);
            this.manifestParser = (ParsingLoadable.Parser) Assertions.checkNotNull(manifestParser);
            return this;
        }

        public Factory setCompositeSequenceableLoaderFactory(CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory) {
            Assertions.checkState(!this.isCreateCalled);
            this.compositeSequenceableLoaderFactory = (CompositeSequenceableLoaderFactory) Assertions.checkNotNull(compositeSequenceableLoaderFactory);
            return this;
        }

        public DashMediaSource createMediaSource(DashManifest manifest) {
            DashManifest manifest2 = manifest;
            Assertions.checkArgument(!manifest2.dynamic);
            this.isCreateCalled = true;
            List<StreamKey> list = this.streamKeys;
            if (list != null && !list.isEmpty()) {
                manifest2 = manifest2.copy(this.streamKeys);
            }
            return new DashMediaSource(manifest2, null, null, null, this.chunkSourceFactory, this.compositeSequenceableLoaderFactory, this.drmSessionManager, this.loadErrorHandlingPolicy, this.livePresentationDelayMs, this.livePresentationDelayOverridesManifest, this.tag);
        }

        @Deprecated
        public DashMediaSource createMediaSource(DashManifest manifest, Handler eventHandler, MediaSourceEventListener eventListener) {
            DashMediaSource mediaSource = createMediaSource(manifest);
            if (eventHandler != null && eventListener != null) {
                mediaSource.addEventListener(eventHandler, eventListener);
            }
            return mediaSource;
        }

        @Deprecated
        public DashMediaSource createMediaSource(Uri manifestUri, Handler eventHandler, MediaSourceEventListener eventListener) {
            DashMediaSource mediaSource = createMediaSource(manifestUri);
            if (eventHandler != null && eventListener != null) {
                mediaSource.addEventListener(eventHandler, eventListener);
            }
            return mediaSource;
        }

        @Override // com.google.android.exoplayer2.source.MediaSourceFactory
        public Factory setDrmSessionManager(DrmSessionManager<?> drmSessionManager) {
            DrmSessionManager<?> drmSessionManager2;
            Assertions.checkState(!this.isCreateCalled);
            if (drmSessionManager != null) {
                drmSessionManager2 = drmSessionManager;
            } else {
                drmSessionManager2 = DrmSessionManager.CC.getDummyDrmSessionManager();
            }
            this.drmSessionManager = drmSessionManager2;
            return this;
        }

        @Override // com.google.android.exoplayer2.source.MediaSourceFactory
        public DashMediaSource createMediaSource(Uri manifestUri) {
            this.isCreateCalled = true;
            if (this.manifestParser == null) {
                this.manifestParser = new DashManifestParser();
            }
            if (this.streamKeys != null) {
                this.manifestParser = new FilteringManifestParser(this.manifestParser, this.streamKeys);
            }
            return new DashMediaSource(null, (Uri) Assertions.checkNotNull(manifestUri), this.manifestDataSourceFactory, this.manifestParser, this.chunkSourceFactory, this.compositeSequenceableLoaderFactory, this.drmSessionManager, this.loadErrorHandlingPolicy, this.livePresentationDelayMs, this.livePresentationDelayOverridesManifest, this.tag);
        }

        @Override // com.google.android.exoplayer2.source.MediaSourceFactory
        public Factory setStreamKeys(List<StreamKey> streamKeys) {
            Assertions.checkState(!this.isCreateCalled);
            this.streamKeys = streamKeys;
            return this;
        }

        @Override // com.google.android.exoplayer2.source.MediaSourceFactory
        public int[] getSupportedTypes() {
            return new int[]{0};
        }
    }

    @Deprecated
    public DashMediaSource(DashManifest manifest, DashChunkSource.Factory chunkSourceFactory, Handler eventHandler, MediaSourceEventListener eventListener) {
        this(manifest, chunkSourceFactory, 3, eventHandler, eventListener);
    }

    @Deprecated
    public DashMediaSource(DashManifest manifest, DashChunkSource.Factory chunkSourceFactory, int minLoadableRetryCount, Handler eventHandler, MediaSourceEventListener eventListener) {
        this(manifest, null, null, null, chunkSourceFactory, new DefaultCompositeSequenceableLoaderFactory(), DrmSessionManager.CC.getDummyDrmSessionManager(), new DefaultLoadErrorHandlingPolicy(minLoadableRetryCount), 30000L, false, null);
        if (eventHandler != null && eventListener != null) {
            addEventListener(eventHandler, eventListener);
        }
    }

    @Deprecated
    public DashMediaSource(Uri manifestUri, DataSource.Factory manifestDataSourceFactory, DashChunkSource.Factory chunkSourceFactory, Handler eventHandler, MediaSourceEventListener eventListener) {
        this(manifestUri, manifestDataSourceFactory, chunkSourceFactory, 3, -1L, eventHandler, eventListener);
    }

    @Deprecated
    public DashMediaSource(Uri manifestUri, DataSource.Factory manifestDataSourceFactory, DashChunkSource.Factory chunkSourceFactory, int minLoadableRetryCount, long livePresentationDelayMs, Handler eventHandler, MediaSourceEventListener eventListener) {
        this(manifestUri, manifestDataSourceFactory, new DashManifestParser(), chunkSourceFactory, minLoadableRetryCount, livePresentationDelayMs, eventHandler, eventListener);
    }

    /* JADX WARN: Illegal instructions before constructor call */
    @java.lang.Deprecated
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public DashMediaSource(android.net.Uri r17, com.google.android.exoplayer2.upstream.DataSource.Factory r18, com.google.android.exoplayer2.upstream.ParsingLoadable.Parser<? extends com.google.android.exoplayer2.source.dash.manifest.DashManifest> r19, com.google.android.exoplayer2.source.dash.DashChunkSource.Factory r20, int r21, long r22, android.os.Handler r24, com.google.android.exoplayer2.source.MediaSourceEventListener r25) {
        /*
            r16 = this;
            r0 = r24
            r1 = r25
            com.google.android.exoplayer2.source.DefaultCompositeSequenceableLoaderFactory r8 = new com.google.android.exoplayer2.source.DefaultCompositeSequenceableLoaderFactory
            r8.<init>()
            com.google.android.exoplayer2.drm.DrmSessionManager r9 = com.google.android.exoplayer2.drm.DrmSessionManager.CC.getDummyDrmSessionManager()
            com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy r10 = new com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy
            r15 = r21
            r10.<init>(r15)
            r2 = -1
            int r4 = (r22 > r2 ? 1 : (r22 == r2 ? 0 : -1))
            if (r4 != 0) goto L1e
            r4 = 30000(0x7530, double:1.4822E-319)
            r11 = r4
            goto L20
        L1e:
            r11 = r22
        L20:
            int r4 = (r22 > r2 ? 1 : (r22 == r2 ? 0 : -1))
            if (r4 == 0) goto L27
            r2 = 1
            r13 = 1
            goto L29
        L27:
            r2 = 0
            r13 = 0
        L29:
            r14 = 0
            r3 = 0
            r2 = r16
            r4 = r17
            r5 = r18
            r6 = r19
            r7 = r20
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r11, r13, r14)
            if (r0 == 0) goto L42
            if (r1 == 0) goto L42
            r2 = r16
            r2.addEventListener(r0, r1)
            goto L44
        L42:
            r2 = r16
        L44:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.dash.DashMediaSource.<init>(android.net.Uri, com.google.android.exoplayer2.upstream.DataSource$Factory, com.google.android.exoplayer2.upstream.ParsingLoadable$Parser, com.google.android.exoplayer2.source.dash.DashChunkSource$Factory, int, long, android.os.Handler, com.google.android.exoplayer2.source.MediaSourceEventListener):void");
    }

    private DashMediaSource(DashManifest manifest, Uri manifestUri, DataSource.Factory manifestDataSourceFactory, ParsingLoadable.Parser<? extends DashManifest> manifestParser, DashChunkSource.Factory chunkSourceFactory, CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory, DrmSessionManager<?> drmSessionManager, LoadErrorHandlingPolicy loadErrorHandlingPolicy, long livePresentationDelayMs, boolean livePresentationDelayOverridesManifest, Object tag) {
        this.initialManifestUri = manifestUri;
        this.manifest = manifest;
        this.manifestUri = manifestUri;
        this.manifestDataSourceFactory = manifestDataSourceFactory;
        this.manifestParser = manifestParser;
        this.chunkSourceFactory = chunkSourceFactory;
        this.drmSessionManager = drmSessionManager;
        this.loadErrorHandlingPolicy = loadErrorHandlingPolicy;
        this.livePresentationDelayMs = livePresentationDelayMs;
        this.livePresentationDelayOverridesManifest = livePresentationDelayOverridesManifest;
        this.compositeSequenceableLoaderFactory = compositeSequenceableLoaderFactory;
        this.tag = tag;
        boolean z = manifest != null;
        this.sideloadedManifest = z;
        this.manifestEventDispatcher = createEventDispatcher(null);
        this.manifestUriLock = new Object();
        this.periodsById = new SparseArray<>();
        this.playerEmsgCallback = new DefaultPlayerEmsgCallback();
        this.expiredManifestPublishTimeUs = C.TIME_UNSET;
        if (!z) {
            this.manifestCallback = new ManifestCallback();
            this.manifestLoadErrorThrower = new ManifestLoadErrorThrower();
            this.refreshManifestRunnable = new Runnable() { // from class: com.google.android.exoplayer2.source.dash.DashMediaSource$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    DashMediaSource.this.startLoadingManifest();
                }
            };
            this.simulateManifestRefreshRunnable = new Runnable() { // from class: com.google.android.exoplayer2.source.dash.DashMediaSource$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    DashMediaSource.this.m71x86b5dcdd();
                }
            };
            return;
        }
        Assertions.checkState(!manifest.dynamic);
        this.manifestCallback = null;
        this.refreshManifestRunnable = null;
        this.simulateManifestRefreshRunnable = null;
        this.manifestLoadErrorThrower = new LoaderErrorThrower.Dummy();
    }

    /* renamed from: lambda$new$0$com-google-android-exoplayer2-source-dash-DashMediaSource */
    public /* synthetic */ void m71x86b5dcdd() {
        processManifest(false);
    }

    public void replaceManifestUri(Uri manifestUri) {
        synchronized (this.manifestUriLock) {
            this.manifestUri = manifestUri;
            this.initialManifestUri = manifestUri;
        }
    }

    @Override // com.google.android.exoplayer2.source.BaseMediaSource, com.google.android.exoplayer2.source.MediaSource
    public Object getTag() {
        return this.tag;
    }

    @Override // com.google.android.exoplayer2.source.BaseMediaSource
    protected void prepareSourceInternal(TransferListener mediaTransferListener) {
        this.mediaTransferListener = mediaTransferListener;
        this.drmSessionManager.prepare();
        if (this.sideloadedManifest) {
            processManifest(false);
            return;
        }
        this.dataSource = this.manifestDataSourceFactory.createDataSource();
        this.loader = new Loader("Loader:DashMediaSource");
        this.handler = new Handler();
        startLoadingManifest();
    }

    @Override // com.google.android.exoplayer2.source.MediaSource
    public void maybeThrowSourceInfoRefreshError() throws IOException {
        this.manifestLoadErrorThrower.maybeThrowError();
    }

    @Override // com.google.android.exoplayer2.source.MediaSource
    public MediaPeriod createPeriod(MediaSource.MediaPeriodId periodId, Allocator allocator, long startPositionUs) {
        int periodIndex = ((Integer) periodId.periodUid).intValue() - this.firstPeriodId;
        MediaSourceEventListener.EventDispatcher periodEventDispatcher = createEventDispatcher(periodId, this.manifest.getPeriod(periodIndex).startMs);
        DashMediaPeriod mediaPeriod = new DashMediaPeriod(this.firstPeriodId + periodIndex, this.manifest, periodIndex, this.chunkSourceFactory, this.mediaTransferListener, this.drmSessionManager, this.loadErrorHandlingPolicy, periodEventDispatcher, this.elapsedRealtimeOffsetMs, this.manifestLoadErrorThrower, allocator, this.compositeSequenceableLoaderFactory, this.playerEmsgCallback);
        this.periodsById.put(mediaPeriod.id, mediaPeriod);
        return mediaPeriod;
    }

    @Override // com.google.android.exoplayer2.source.MediaSource
    public void releasePeriod(MediaPeriod mediaPeriod) {
        DashMediaPeriod dashMediaPeriod = (DashMediaPeriod) mediaPeriod;
        dashMediaPeriod.release();
        this.periodsById.remove(dashMediaPeriod.id);
    }

    @Override // com.google.android.exoplayer2.source.BaseMediaSource
    protected void releaseSourceInternal() {
        this.manifestLoadPending = false;
        this.dataSource = null;
        Loader loader = this.loader;
        if (loader != null) {
            loader.release();
            this.loader = null;
        }
        this.manifestLoadStartTimestampMs = 0L;
        this.manifestLoadEndTimestampMs = 0L;
        this.manifest = this.sideloadedManifest ? this.manifest : null;
        this.manifestUri = this.initialManifestUri;
        this.manifestFatalError = null;
        Handler handler = this.handler;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            this.handler = null;
        }
        this.elapsedRealtimeOffsetMs = 0L;
        this.staleManifestReloadAttempt = 0;
        this.expiredManifestPublishTimeUs = C.TIME_UNSET;
        this.firstPeriodId = 0;
        this.periodsById.clear();
        this.drmSessionManager.release();
    }

    void onDashManifestRefreshRequested() {
        this.handler.removeCallbacks(this.simulateManifestRefreshRunnable);
        startLoadingManifest();
    }

    void onDashManifestPublishTimeExpired(long expiredManifestPublishTimeUs) {
        long j = this.expiredManifestPublishTimeUs;
        if (j == C.TIME_UNSET || j < expiredManifestPublishTimeUs) {
            this.expiredManifestPublishTimeUs = expiredManifestPublishTimeUs;
        }
    }

    void onManifestLoadCompleted(ParsingLoadable<DashManifest> loadable, long elapsedRealtimeMs, long loadDurationMs) {
        this.manifestEventDispatcher.loadCompleted(loadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), loadable.type, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
        DashManifest newManifest = loadable.getResult();
        DashManifest dashManifest = this.manifest;
        boolean z = false;
        int oldPeriodCount = dashManifest == null ? 0 : dashManifest.getPeriodCount();
        long newFirstPeriodStartTimeMs = newManifest.getPeriod(0).startMs;
        int removedPeriodCount = 0;
        while (removedPeriodCount < oldPeriodCount && this.manifest.getPeriod(removedPeriodCount).startMs < newFirstPeriodStartTimeMs) {
            removedPeriodCount++;
        }
        if (newManifest.dynamic) {
            boolean isManifestStale = false;
            if (oldPeriodCount - removedPeriodCount > newManifest.getPeriodCount()) {
                Log.w(TAG, "Loaded out of sync manifest");
                isManifestStale = true;
            } else if (this.expiredManifestPublishTimeUs != C.TIME_UNSET && newManifest.publishTimeMs * 1000 <= this.expiredManifestPublishTimeUs) {
                Log.w(TAG, "Loaded stale dynamic manifest: " + newManifest.publishTimeMs + ", " + this.expiredManifestPublishTimeUs);
                isManifestStale = true;
            }
            if (isManifestStale) {
                int i = this.staleManifestReloadAttempt;
                this.staleManifestReloadAttempt = i + 1;
                if (i < this.loadErrorHandlingPolicy.getMinimumLoadableRetryCount(loadable.type)) {
                    scheduleManifestRefresh(getManifestLoadRetryDelayMillis());
                    return;
                } else {
                    this.manifestFatalError = new DashManifestStaleException();
                    return;
                }
            }
            this.staleManifestReloadAttempt = 0;
        }
        this.manifest = newManifest;
        this.manifestLoadPending &= newManifest.dynamic;
        this.manifestLoadStartTimestampMs = elapsedRealtimeMs - loadDurationMs;
        this.manifestLoadEndTimestampMs = elapsedRealtimeMs;
        synchronized (this.manifestUriLock) {
            if (loadable.dataSpec.uri == this.manifestUri) {
                z = true;
            }
            boolean isSameUriInstance = z;
            if (isSameUriInstance) {
                this.manifestUri = this.manifest.location != null ? this.manifest.location : loadable.getUri();
            }
        }
        if (oldPeriodCount != 0) {
            this.firstPeriodId += removedPeriodCount;
            processManifest(true);
        } else if (this.manifest.dynamic && this.manifest.utcTiming != null) {
            resolveUtcTimingElement(this.manifest.utcTiming);
        } else {
            processManifest(true);
        }
    }

    Loader.LoadErrorAction onManifestLoadError(ParsingLoadable<DashManifest> loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error, int errorCount) {
        Loader.LoadErrorAction loadErrorAction;
        long retryDelayMs = this.loadErrorHandlingPolicy.getRetryDelayMsFor(4, loadDurationMs, error, errorCount);
        if (retryDelayMs == C.TIME_UNSET) {
            loadErrorAction = Loader.DONT_RETRY_FATAL;
        } else {
            loadErrorAction = Loader.createRetryAction(false, retryDelayMs);
        }
        this.manifestEventDispatcher.loadError(loadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), loadable.type, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded(), error, !loadErrorAction.isRetry());
        return loadErrorAction;
    }

    void onUtcTimestampLoadCompleted(ParsingLoadable<Long> loadable, long elapsedRealtimeMs, long loadDurationMs) {
        this.manifestEventDispatcher.loadCompleted(loadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), loadable.type, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
        onUtcTimestampResolved(loadable.getResult().longValue() - elapsedRealtimeMs);
    }

    Loader.LoadErrorAction onUtcTimestampLoadError(ParsingLoadable<Long> loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error) {
        this.manifestEventDispatcher.loadError(loadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), loadable.type, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded(), error, true);
        onUtcTimestampResolutionError(error);
        return Loader.DONT_RETRY;
    }

    void onLoadCanceled(ParsingLoadable<?> loadable, long elapsedRealtimeMs, long loadDurationMs) {
        this.manifestEventDispatcher.loadCanceled(loadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), loadable.type, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
    }

    private void resolveUtcTimingElement(UtcTimingElement timingElement) {
        String scheme = timingElement.schemeIdUri;
        if (Util.areEqual(scheme, "urn:mpeg:dash:utc:direct:2014") || Util.areEqual(scheme, "urn:mpeg:dash:utc:direct:2012")) {
            resolveUtcTimingElementDirect(timingElement);
        } else if (Util.areEqual(scheme, "urn:mpeg:dash:utc:http-iso:2014") || Util.areEqual(scheme, "urn:mpeg:dash:utc:http-iso:2012")) {
            resolveUtcTimingElementHttp(timingElement, new Iso8601Parser());
        } else if (Util.areEqual(scheme, "urn:mpeg:dash:utc:http-xsdate:2014") || Util.areEqual(scheme, "urn:mpeg:dash:utc:http-xsdate:2012")) {
            resolveUtcTimingElementHttp(timingElement, new XsDateTimeParser());
        } else {
            onUtcTimestampResolutionError(new IOException("Unsupported UTC timing scheme"));
        }
    }

    private void resolveUtcTimingElementDirect(UtcTimingElement timingElement) {
        try {
            long utcTimestampMs = Util.parseXsDateTime(timingElement.value);
            onUtcTimestampResolved(utcTimestampMs - this.manifestLoadEndTimestampMs);
        } catch (ParserException e) {
            onUtcTimestampResolutionError(e);
        }
    }

    private void resolveUtcTimingElementHttp(UtcTimingElement timingElement, ParsingLoadable.Parser<Long> parser) {
        startLoading(new ParsingLoadable(this.dataSource, Uri.parse(timingElement.value), 5, parser), new UtcTimestampCallback(), 1);
    }

    private void onUtcTimestampResolved(long elapsedRealtimeOffsetMs) {
        this.elapsedRealtimeOffsetMs = elapsedRealtimeOffsetMs;
        processManifest(true);
    }

    private void onUtcTimestampResolutionError(IOException error) {
        Log.e(TAG, "Failed to resolve UtcTiming element.", error);
        processManifest(true);
    }

    private void processManifest(boolean scheduleRefresh) {
        boolean windowChangingImplicitly;
        for (int i = 0; i < this.periodsById.size(); i++) {
            int id = this.periodsById.keyAt(i);
            if (id >= this.firstPeriodId) {
                this.periodsById.valueAt(i).updateManifest(this.manifest, id - this.firstPeriodId);
            }
        }
        boolean windowChangingImplicitly2 = false;
        int lastPeriodIndex = this.manifest.getPeriodCount() - 1;
        PeriodSeekInfo firstPeriodSeekInfo = PeriodSeekInfo.createPeriodSeekInfo(this.manifest.getPeriod(0), this.manifest.getPeriodDurationUs(0));
        PeriodSeekInfo lastPeriodSeekInfo = PeriodSeekInfo.createPeriodSeekInfo(this.manifest.getPeriod(lastPeriodIndex), this.manifest.getPeriodDurationUs(lastPeriodIndex));
        long currentStartTimeUs = firstPeriodSeekInfo.availableStartTimeUs;
        long currentEndTimeUs = lastPeriodSeekInfo.availableEndTimeUs;
        if (this.manifest.dynamic && !lastPeriodSeekInfo.isIndexExplicit) {
            long liveStreamDurationUs = getNowUnixTimeUs() - C.msToUs(this.manifest.availabilityStartTimeMs);
            long liveStreamEndPositionInLastPeriodUs = liveStreamDurationUs - C.msToUs(this.manifest.getPeriod(lastPeriodIndex).startMs);
            currentEndTimeUs = Math.min(liveStreamEndPositionInLastPeriodUs, currentEndTimeUs);
            if (this.manifest.timeShiftBufferDepthMs != C.TIME_UNSET) {
                long timeShiftBufferDepthUs = C.msToUs(this.manifest.timeShiftBufferDepthMs);
                long offsetInPeriodUs = currentEndTimeUs - timeShiftBufferDepthUs;
                int periodIndex = lastPeriodIndex;
                long offsetInPeriodUs2 = offsetInPeriodUs;
                while (offsetInPeriodUs2 < 0 && periodIndex > 0) {
                    periodIndex--;
                    offsetInPeriodUs2 += this.manifest.getPeriodDurationUs(periodIndex);
                    windowChangingImplicitly2 = windowChangingImplicitly2;
                }
                if (periodIndex == 0) {
                    currentStartTimeUs = Math.max(currentStartTimeUs, offsetInPeriodUs2);
                } else {
                    currentStartTimeUs = this.manifest.getPeriodDurationUs(0);
                }
            }
            windowChangingImplicitly = true;
        } else {
            windowChangingImplicitly = false;
        }
        long windowDurationUs = currentEndTimeUs - currentStartTimeUs;
        for (int i2 = 0; i2 < this.manifest.getPeriodCount() - 1; i2++) {
            windowDurationUs += this.manifest.getPeriodDurationUs(i2);
        }
        long windowDefaultStartPositionUs = 0;
        if (this.manifest.dynamic) {
            long presentationDelayForManifestMs = this.livePresentationDelayMs;
            if (!this.livePresentationDelayOverridesManifest && this.manifest.suggestedPresentationDelayMs != C.TIME_UNSET) {
                presentationDelayForManifestMs = this.manifest.suggestedPresentationDelayMs;
            }
            windowDefaultStartPositionUs = windowDurationUs - C.msToUs(presentationDelayForManifestMs);
            if (windowDefaultStartPositionUs < MIN_LIVE_DEFAULT_START_POSITION_US) {
                windowDefaultStartPositionUs = Math.min((long) MIN_LIVE_DEFAULT_START_POSITION_US, windowDurationUs / 2);
            }
        }
        long windowStartTimeMs = C.TIME_UNSET;
        if (this.manifest.availabilityStartTimeMs != C.TIME_UNSET) {
            windowStartTimeMs = this.manifest.availabilityStartTimeMs + this.manifest.getPeriod(0).startMs + C.usToMs(currentStartTimeUs);
        }
        DashTimeline timeline = new DashTimeline(this.manifest.availabilityStartTimeMs, windowStartTimeMs, this.firstPeriodId, currentStartTimeUs, windowDurationUs, windowDefaultStartPositionUs, this.manifest, this.tag);
        refreshSourceInfo(timeline);
        if (!this.sideloadedManifest) {
            this.handler.removeCallbacks(this.simulateManifestRefreshRunnable);
            if (windowChangingImplicitly) {
                this.handler.postDelayed(this.simulateManifestRefreshRunnable, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
            }
            if (this.manifestLoadPending) {
                startLoadingManifest();
            } else if (scheduleRefresh && this.manifest.dynamic && this.manifest.minUpdatePeriodMs != C.TIME_UNSET) {
                long minUpdatePeriodMs = this.manifest.minUpdatePeriodMs;
                if (minUpdatePeriodMs == 0) {
                    minUpdatePeriodMs = DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
                }
                long nextLoadTimestampMs = this.manifestLoadStartTimestampMs + minUpdatePeriodMs;
                long delayUntilNextLoadMs = Math.max(0L, nextLoadTimestampMs - SystemClock.elapsedRealtime());
                scheduleManifestRefresh(delayUntilNextLoadMs);
            }
        }
    }

    private void scheduleManifestRefresh(long delayUntilNextLoadMs) {
        this.handler.postDelayed(this.refreshManifestRunnable, delayUntilNextLoadMs);
    }

    public void startLoadingManifest() {
        Uri manifestUri;
        this.handler.removeCallbacks(this.refreshManifestRunnable);
        if (this.loader.hasFatalError()) {
            return;
        }
        if (this.loader.isLoading()) {
            this.manifestLoadPending = true;
            return;
        }
        synchronized (this.manifestUriLock) {
            manifestUri = this.manifestUri;
        }
        this.manifestLoadPending = false;
        startLoading(new ParsingLoadable(this.dataSource, manifestUri, 4, this.manifestParser), this.manifestCallback, this.loadErrorHandlingPolicy.getMinimumLoadableRetryCount(4));
    }

    private long getManifestLoadRetryDelayMillis() {
        return Math.min((this.staleManifestReloadAttempt - 1) * 1000, 5000);
    }

    private <T> void startLoading(ParsingLoadable<T> loadable, Loader.Callback<ParsingLoadable<T>> callback, int minRetryCount) {
        long elapsedRealtimeMs = this.loader.startLoading(loadable, callback, minRetryCount);
        this.manifestEventDispatcher.loadStarted(loadable.dataSpec, loadable.type, elapsedRealtimeMs);
    }

    private long getNowUnixTimeUs() {
        if (this.elapsedRealtimeOffsetMs != 0) {
            return C.msToUs(SystemClock.elapsedRealtime() + this.elapsedRealtimeOffsetMs);
        }
        return C.msToUs(System.currentTimeMillis());
    }

    /* loaded from: classes3.dex */
    public static final class PeriodSeekInfo {
        public final long availableEndTimeUs;
        public final long availableStartTimeUs;
        public final boolean isIndexExplicit;

        public static PeriodSeekInfo createPeriodSeekInfo(Period period, long durationUs) {
            Period period2 = period;
            int adaptationSetCount = period2.adaptationSets.size();
            boolean haveAudioVideoAdaptationSets = false;
            for (int i = 0; i < adaptationSetCount; i++) {
                int type = period2.adaptationSets.get(i).type;
                if (type == 1 || type == 2) {
                    haveAudioVideoAdaptationSets = true;
                    break;
                }
            }
            long availableStartTimeUs = 0;
            boolean isIndexExplicit = false;
            boolean seenEmptyIndex = false;
            int i2 = 0;
            long availableEndTimeUs = Long.MAX_VALUE;
            while (i2 < adaptationSetCount) {
                AdaptationSet adaptationSet = period2.adaptationSets.get(i2);
                if (!haveAudioVideoAdaptationSets || adaptationSet.type != 3) {
                    DashSegmentIndex index = adaptationSet.representations.get(0).getIndex();
                    if (index == null) {
                        return new PeriodSeekInfo(true, 0L, durationUs);
                    }
                    boolean isIndexExplicit2 = isIndexExplicit | index.isExplicit();
                    int segmentCount = index.getSegmentCount(durationUs);
                    if (segmentCount == 0) {
                        availableEndTimeUs = 0;
                        isIndexExplicit = isIndexExplicit2;
                        seenEmptyIndex = true;
                        availableStartTimeUs = 0;
                    } else if (seenEmptyIndex) {
                        isIndexExplicit = isIndexExplicit2;
                    } else {
                        long firstSegmentNum = index.getFirstSegmentNum();
                        long adaptationSetAvailableStartTimeUs = index.getTimeUs(firstSegmentNum);
                        availableStartTimeUs = Math.max(availableStartTimeUs, adaptationSetAvailableStartTimeUs);
                        if (segmentCount == -1) {
                            isIndexExplicit = isIndexExplicit2;
                        } else {
                            long lastSegmentNum = (segmentCount + firstSegmentNum) - 1;
                            availableEndTimeUs = Math.min(availableEndTimeUs, index.getTimeUs(lastSegmentNum) + index.getDurationUs(lastSegmentNum, durationUs));
                            isIndexExplicit = isIndexExplicit2;
                        }
                    }
                }
                i2++;
                period2 = period;
            }
            return new PeriodSeekInfo(isIndexExplicit, availableStartTimeUs, availableEndTimeUs);
        }

        private PeriodSeekInfo(boolean isIndexExplicit, long availableStartTimeUs, long availableEndTimeUs) {
            this.isIndexExplicit = isIndexExplicit;
            this.availableStartTimeUs = availableStartTimeUs;
            this.availableEndTimeUs = availableEndTimeUs;
        }
    }

    /* loaded from: classes3.dex */
    public static final class DashTimeline extends Timeline {
        private final int firstPeriodId;
        private final DashManifest manifest;
        private final long offsetInFirstPeriodUs;
        private final long presentationStartTimeMs;
        private final long windowDefaultStartPositionUs;
        private final long windowDurationUs;
        private final long windowStartTimeMs;
        private final Object windowTag;

        public DashTimeline(long presentationStartTimeMs, long windowStartTimeMs, int firstPeriodId, long offsetInFirstPeriodUs, long windowDurationUs, long windowDefaultStartPositionUs, DashManifest manifest, Object windowTag) {
            this.presentationStartTimeMs = presentationStartTimeMs;
            this.windowStartTimeMs = windowStartTimeMs;
            this.firstPeriodId = firstPeriodId;
            this.offsetInFirstPeriodUs = offsetInFirstPeriodUs;
            this.windowDurationUs = windowDurationUs;
            this.windowDefaultStartPositionUs = windowDefaultStartPositionUs;
            this.manifest = manifest;
            this.windowTag = windowTag;
        }

        @Override // com.google.android.exoplayer2.Timeline
        public int getPeriodCount() {
            return this.manifest.getPeriodCount();
        }

        @Override // com.google.android.exoplayer2.Timeline
        public Timeline.Period getPeriod(int periodIndex, Timeline.Period period, boolean setIdentifiers) {
            Assertions.checkIndex(periodIndex, 0, getPeriodCount());
            Integer num = null;
            Object id = setIdentifiers ? this.manifest.getPeriod(periodIndex).id : null;
            if (setIdentifiers) {
                num = Integer.valueOf(this.firstPeriodId + periodIndex);
            }
            Object uid = num;
            return period.set(id, uid, 0, this.manifest.getPeriodDurationUs(periodIndex), C.msToUs(this.manifest.getPeriod(periodIndex).startMs - this.manifest.getPeriod(0).startMs) - this.offsetInFirstPeriodUs);
        }

        @Override // com.google.android.exoplayer2.Timeline
        public int getWindowCount() {
            return 1;
        }

        @Override // com.google.android.exoplayer2.Timeline
        public Timeline.Window getWindow(int windowIndex, Timeline.Window window, long defaultPositionProjectionUs) {
            Assertions.checkIndex(windowIndex, 0, 1);
            long windowDefaultStartPositionUs = getAdjustedWindowDefaultStartPositionUs(defaultPositionProjectionUs);
            Object obj = Timeline.Window.SINGLE_WINDOW_UID;
            Object obj2 = this.windowTag;
            DashManifest dashManifest = this.manifest;
            return window.set(obj, obj2, dashManifest, this.presentationStartTimeMs, this.windowStartTimeMs, true, isMovingLiveWindow(dashManifest), this.manifest.dynamic, windowDefaultStartPositionUs, this.windowDurationUs, 0, getPeriodCount() - 1, this.offsetInFirstPeriodUs);
        }

        @Override // com.google.android.exoplayer2.Timeline
        public int getIndexOfPeriod(Object uid) {
            if (!(uid instanceof Integer)) {
                return -1;
            }
            int periodId = ((Integer) uid).intValue();
            int periodIndex = periodId - this.firstPeriodId;
            if (periodIndex >= 0 && periodIndex < getPeriodCount()) {
                return periodIndex;
            }
            return -1;
        }

        private long getAdjustedWindowDefaultStartPositionUs(long defaultPositionProjectionUs) {
            long windowDefaultStartPositionUs = this.windowDefaultStartPositionUs;
            if (!isMovingLiveWindow(this.manifest)) {
                return windowDefaultStartPositionUs;
            }
            if (defaultPositionProjectionUs > 0) {
                windowDefaultStartPositionUs += defaultPositionProjectionUs;
                if (windowDefaultStartPositionUs > this.windowDurationUs) {
                    return C.TIME_UNSET;
                }
            }
            int periodIndex = 0;
            long defaultStartPositionInPeriodUs = this.offsetInFirstPeriodUs + windowDefaultStartPositionUs;
            long periodDurationUs = this.manifest.getPeriodDurationUs(0);
            while (periodIndex < this.manifest.getPeriodCount() - 1 && defaultStartPositionInPeriodUs >= periodDurationUs) {
                defaultStartPositionInPeriodUs -= periodDurationUs;
                periodIndex++;
                periodDurationUs = this.manifest.getPeriodDurationUs(periodIndex);
            }
            Period period = this.manifest.getPeriod(periodIndex);
            int videoAdaptationSetIndex = period.getAdaptationSetIndex(2);
            if (videoAdaptationSetIndex == -1) {
                return windowDefaultStartPositionUs;
            }
            DashSegmentIndex snapIndex = period.adaptationSets.get(videoAdaptationSetIndex).representations.get(0).getIndex();
            if (snapIndex == null || snapIndex.getSegmentCount(periodDurationUs) == 0) {
                return windowDefaultStartPositionUs;
            }
            long segmentNum = snapIndex.getSegmentNum(defaultStartPositionInPeriodUs, periodDurationUs);
            return (snapIndex.getTimeUs(segmentNum) + windowDefaultStartPositionUs) - defaultStartPositionInPeriodUs;
        }

        @Override // com.google.android.exoplayer2.Timeline
        public Object getUidOfPeriod(int periodIndex) {
            Assertions.checkIndex(periodIndex, 0, getPeriodCount());
            return Integer.valueOf(this.firstPeriodId + periodIndex);
        }

        private static boolean isMovingLiveWindow(DashManifest manifest) {
            return manifest.dynamic && manifest.minUpdatePeriodMs != C.TIME_UNSET && manifest.durationMs == C.TIME_UNSET;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public final class DefaultPlayerEmsgCallback implements PlayerEmsgHandler.PlayerEmsgCallback {
        private DefaultPlayerEmsgCallback() {
            DashMediaSource.this = r1;
        }

        @Override // com.google.android.exoplayer2.source.dash.PlayerEmsgHandler.PlayerEmsgCallback
        public void onDashManifestRefreshRequested() {
            DashMediaSource.this.onDashManifestRefreshRequested();
        }

        @Override // com.google.android.exoplayer2.source.dash.PlayerEmsgHandler.PlayerEmsgCallback
        public void onDashManifestPublishTimeExpired(long expiredManifestPublishTimeUs) {
            DashMediaSource.this.onDashManifestPublishTimeExpired(expiredManifestPublishTimeUs);
        }
    }

    /* loaded from: classes3.dex */
    public final class ManifestCallback implements Loader.Callback<ParsingLoadable<DashManifest>> {
        private ManifestCallback() {
            DashMediaSource.this = r1;
        }

        public void onLoadCompleted(ParsingLoadable<DashManifest> loadable, long elapsedRealtimeMs, long loadDurationMs) {
            DashMediaSource.this.onManifestLoadCompleted(loadable, elapsedRealtimeMs, loadDurationMs);
        }

        public void onLoadCanceled(ParsingLoadable<DashManifest> loadable, long elapsedRealtimeMs, long loadDurationMs, boolean released) {
            DashMediaSource.this.onLoadCanceled(loadable, elapsedRealtimeMs, loadDurationMs);
        }

        public Loader.LoadErrorAction onLoadError(ParsingLoadable<DashManifest> loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error, int errorCount) {
            return DashMediaSource.this.onManifestLoadError(loadable, elapsedRealtimeMs, loadDurationMs, error, errorCount);
        }
    }

    /* loaded from: classes3.dex */
    public final class UtcTimestampCallback implements Loader.Callback<ParsingLoadable<Long>> {
        private UtcTimestampCallback() {
            DashMediaSource.this = r1;
        }

        public void onLoadCompleted(ParsingLoadable<Long> loadable, long elapsedRealtimeMs, long loadDurationMs) {
            DashMediaSource.this.onUtcTimestampLoadCompleted(loadable, elapsedRealtimeMs, loadDurationMs);
        }

        public void onLoadCanceled(ParsingLoadable<Long> loadable, long elapsedRealtimeMs, long loadDurationMs, boolean released) {
            DashMediaSource.this.onLoadCanceled(loadable, elapsedRealtimeMs, loadDurationMs);
        }

        public Loader.LoadErrorAction onLoadError(ParsingLoadable<Long> loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error, int errorCount) {
            return DashMediaSource.this.onUtcTimestampLoadError(loadable, elapsedRealtimeMs, loadDurationMs, error);
        }
    }

    /* loaded from: classes3.dex */
    public static final class XsDateTimeParser implements ParsingLoadable.Parser<Long> {
        private XsDateTimeParser() {
        }

        @Override // com.google.android.exoplayer2.upstream.ParsingLoadable.Parser
        public Long parse(Uri uri, InputStream inputStream) throws IOException {
            String firstLine = new BufferedReader(new InputStreamReader(inputStream)).readLine();
            return Long.valueOf(Util.parseXsDateTime(firstLine));
        }
    }

    /* loaded from: classes3.dex */
    public static final class Iso8601Parser implements ParsingLoadable.Parser<Long> {
        private static final Pattern TIMESTAMP_WITH_TIMEZONE_PATTERN = Pattern.compile("(.+?)(Z|((\\+|-|−)(\\d\\d)(:?(\\d\\d))?))");

        Iso8601Parser() {
        }

        @Override // com.google.android.exoplayer2.upstream.ParsingLoadable.Parser
        public Long parse(Uri uri, InputStream inputStream) throws IOException {
            String firstLine = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8"))).readLine();
            try {
                Matcher matcher = TIMESTAMP_WITH_TIMEZONE_PATTERN.matcher(firstLine);
                if (!matcher.matches()) {
                    throw new ParserException("Couldn't parse timestamp: " + firstLine);
                }
                String timestampWithoutTimezone = matcher.group(1);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                format.setTimeZone(DesugarTimeZone.getTimeZone("UTC"));
                long timestampMs = format.parse(timestampWithoutTimezone).getTime();
                String timezone = matcher.group(2);
                if (!"Z".equals(timezone)) {
                    long sign = "+".equals(matcher.group(4)) ? 1L : -1L;
                    long hours = Long.parseLong(matcher.group(5));
                    String minutesString = matcher.group(7);
                    long minutes = TextUtils.isEmpty(minutesString) ? 0L : Long.parseLong(minutesString);
                    long timestampOffsetMs = ((hours * 60) + minutes) * 60 * 1000 * sign;
                    timestampMs -= timestampOffsetMs;
                }
                return Long.valueOf(timestampMs);
            } catch (ParseException e) {
                throw new ParserException(e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public final class ManifestLoadErrorThrower implements LoaderErrorThrower {
        ManifestLoadErrorThrower() {
            DashMediaSource.this = this$0;
        }

        @Override // com.google.android.exoplayer2.upstream.LoaderErrorThrower
        public void maybeThrowError() throws IOException {
            DashMediaSource.this.loader.maybeThrowError();
            maybeThrowManifestError();
        }

        @Override // com.google.android.exoplayer2.upstream.LoaderErrorThrower
        public void maybeThrowError(int minRetryCount) throws IOException {
            DashMediaSource.this.loader.maybeThrowError(minRetryCount);
            maybeThrowManifestError();
        }

        private void maybeThrowManifestError() throws IOException {
            if (DashMediaSource.this.manifestFatalError != null) {
                throw DashMediaSource.this.manifestFatalError;
            }
        }
    }
}
