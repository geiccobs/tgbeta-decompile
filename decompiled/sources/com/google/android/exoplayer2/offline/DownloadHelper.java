package com.google.android.exoplayer2.offline;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.SparseIntArray;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.RendererCapabilities;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.offline.DownloadHelper;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunkIterator;
import com.google.android.exoplayer2.trackselection.BaseTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectorResult;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;
/* loaded from: classes3.dex */
public final class DownloadHelper {
    @Deprecated
    public static final DefaultTrackSelector.Parameters DEFAULT_TRACK_SELECTOR_PARAMETERS;
    public static final DefaultTrackSelector.Parameters DEFAULT_TRACK_SELECTOR_PARAMETERS_WITHOUT_CONTEXT;
    @Deprecated
    public static final DefaultTrackSelector.Parameters DEFAULT_TRACK_SELECTOR_PARAMETERS_WITHOUT_VIEWPORT;
    private final String cacheKey;
    private Callback callback;
    private final String downloadType;
    private List<TrackSelection>[][] immutableTrackSelectionsByPeriodAndRenderer;
    private boolean isPreparedWithMedia;
    private MappingTrackSelector.MappedTrackInfo[] mappedTrackInfos;
    private MediaPreparer mediaPreparer;
    private final MediaSource mediaSource;
    private final RendererCapabilities[] rendererCapabilities;
    private TrackGroupArray[] trackGroupArrays;
    private List<TrackSelection>[][] trackSelectionsByPeriodAndRenderer;
    private final DefaultTrackSelector trackSelector;
    private final Uri uri;
    private static final Constructor<? extends MediaSourceFactory> DASH_FACTORY_CONSTRUCTOR = getConstructor("com.google.android.exoplayer2.source.dash.DashMediaSource$Factory");
    private static final Constructor<? extends MediaSourceFactory> SS_FACTORY_CONSTRUCTOR = getConstructor("com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource$Factory");
    private static final Constructor<? extends MediaSourceFactory> HLS_FACTORY_CONSTRUCTOR = getConstructor("com.google.android.exoplayer2.source.hls.HlsMediaSource$Factory");
    private final SparseIntArray scratchSet = new SparseIntArray();
    private final Handler callbackHandler = new Handler(Util.getLooper());
    private final Timeline.Window window = new Timeline.Window();

    /* loaded from: classes3.dex */
    public interface Callback {
        void onPrepareError(DownloadHelper downloadHelper, IOException iOException);

        void onPrepared(DownloadHelper downloadHelper);
    }

    /* loaded from: classes3.dex */
    public static class LiveContentUnsupportedException extends IOException {
    }

    static {
        DefaultTrackSelector.Parameters build = DefaultTrackSelector.Parameters.DEFAULT_WITHOUT_CONTEXT.buildUpon().setForceHighestSupportedBitrate(true).build();
        DEFAULT_TRACK_SELECTOR_PARAMETERS_WITHOUT_CONTEXT = build;
        DEFAULT_TRACK_SELECTOR_PARAMETERS_WITHOUT_VIEWPORT = build;
        DEFAULT_TRACK_SELECTOR_PARAMETERS = build;
    }

    public static DefaultTrackSelector.Parameters getDefaultTrackSelectorParameters(Context context) {
        return DefaultTrackSelector.Parameters.getDefaults(context).buildUpon().setForceHighestSupportedBitrate(true).build();
    }

    @Deprecated
    public static DownloadHelper forProgressive(Uri uri) {
        return forProgressive(uri, (String) null);
    }

    public static DownloadHelper forProgressive(Context context, Uri uri) {
        return forProgressive(context, uri, null);
    }

    @Deprecated
    public static DownloadHelper forProgressive(Uri uri, String cacheKey) {
        return new DownloadHelper(DownloadRequest.TYPE_PROGRESSIVE, uri, cacheKey, null, DEFAULT_TRACK_SELECTOR_PARAMETERS_WITHOUT_VIEWPORT, new RendererCapabilities[0]);
    }

    public static DownloadHelper forProgressive(Context context, Uri uri, String cacheKey) {
        return new DownloadHelper(DownloadRequest.TYPE_PROGRESSIVE, uri, cacheKey, null, getDefaultTrackSelectorParameters(context), new RendererCapabilities[0]);
    }

    @Deprecated
    public static DownloadHelper forDash(Uri uri, DataSource.Factory dataSourceFactory, RenderersFactory renderersFactory) {
        return forDash(uri, dataSourceFactory, renderersFactory, null, DEFAULT_TRACK_SELECTOR_PARAMETERS_WITHOUT_VIEWPORT);
    }

    public static DownloadHelper forDash(Context context, Uri uri, DataSource.Factory dataSourceFactory, RenderersFactory renderersFactory) {
        return forDash(uri, dataSourceFactory, renderersFactory, null, getDefaultTrackSelectorParameters(context));
    }

    public static DownloadHelper forDash(Uri uri, DataSource.Factory dataSourceFactory, RenderersFactory renderersFactory, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, DefaultTrackSelector.Parameters trackSelectorParameters) {
        return new DownloadHelper(DownloadRequest.TYPE_DASH, uri, null, createMediaSourceInternal(DASH_FACTORY_CONSTRUCTOR, uri, dataSourceFactory, drmSessionManager, null), trackSelectorParameters, Util.getRendererCapabilities(renderersFactory));
    }

    @Deprecated
    public static DownloadHelper forHls(Uri uri, DataSource.Factory dataSourceFactory, RenderersFactory renderersFactory) {
        return forHls(uri, dataSourceFactory, renderersFactory, null, DEFAULT_TRACK_SELECTOR_PARAMETERS_WITHOUT_VIEWPORT);
    }

    public static DownloadHelper forHls(Context context, Uri uri, DataSource.Factory dataSourceFactory, RenderersFactory renderersFactory) {
        return forHls(uri, dataSourceFactory, renderersFactory, null, getDefaultTrackSelectorParameters(context));
    }

    public static DownloadHelper forHls(Uri uri, DataSource.Factory dataSourceFactory, RenderersFactory renderersFactory, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, DefaultTrackSelector.Parameters trackSelectorParameters) {
        return new DownloadHelper(DownloadRequest.TYPE_HLS, uri, null, createMediaSourceInternal(HLS_FACTORY_CONSTRUCTOR, uri, dataSourceFactory, drmSessionManager, null), trackSelectorParameters, Util.getRendererCapabilities(renderersFactory));
    }

    @Deprecated
    public static DownloadHelper forSmoothStreaming(Uri uri, DataSource.Factory dataSourceFactory, RenderersFactory renderersFactory) {
        return forSmoothStreaming(uri, dataSourceFactory, renderersFactory, null, DEFAULT_TRACK_SELECTOR_PARAMETERS_WITHOUT_VIEWPORT);
    }

    public static DownloadHelper forSmoothStreaming(Context context, Uri uri, DataSource.Factory dataSourceFactory, RenderersFactory renderersFactory) {
        return forSmoothStreaming(uri, dataSourceFactory, renderersFactory, null, getDefaultTrackSelectorParameters(context));
    }

    public static DownloadHelper forSmoothStreaming(Uri uri, DataSource.Factory dataSourceFactory, RenderersFactory renderersFactory, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, DefaultTrackSelector.Parameters trackSelectorParameters) {
        return new DownloadHelper(DownloadRequest.TYPE_SS, uri, null, createMediaSourceInternal(SS_FACTORY_CONSTRUCTOR, uri, dataSourceFactory, drmSessionManager, null), trackSelectorParameters, Util.getRendererCapabilities(renderersFactory));
    }

    public static MediaSource createMediaSource(DownloadRequest downloadRequest, DataSource.Factory dataSourceFactory) {
        return createMediaSource(downloadRequest, dataSourceFactory, null);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static MediaSource createMediaSource(DownloadRequest downloadRequest, DataSource.Factory dataSourceFactory, DrmSessionManager<?> drmSessionManager) {
        char c;
        Constructor<? extends MediaSourceFactory> constructor;
        String str = downloadRequest.type;
        switch (str.hashCode()) {
            case 3680:
                if (str.equals(DownloadRequest.TYPE_SS)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 103407:
                if (str.equals(DownloadRequest.TYPE_HLS)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 3075986:
                if (str.equals(DownloadRequest.TYPE_DASH)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 1131547531:
                if (str.equals(DownloadRequest.TYPE_PROGRESSIVE)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                constructor = DASH_FACTORY_CONSTRUCTOR;
                break;
            case 1:
                constructor = SS_FACTORY_CONSTRUCTOR;
                break;
            case 2:
                constructor = HLS_FACTORY_CONSTRUCTOR;
                break;
            case 3:
                return new ProgressiveMediaSource.Factory(dataSourceFactory).setCustomCacheKey(downloadRequest.customCacheKey).createMediaSource(downloadRequest.uri);
            default:
                throw new IllegalStateException("Unsupported type: " + downloadRequest.type);
        }
        return createMediaSourceInternal(constructor, downloadRequest.uri, dataSourceFactory, drmSessionManager, downloadRequest.streamKeys);
    }

    public DownloadHelper(String downloadType, Uri uri, String cacheKey, MediaSource mediaSource, DefaultTrackSelector.Parameters trackSelectorParameters, RendererCapabilities[] rendererCapabilities) {
        this.downloadType = downloadType;
        this.uri = uri;
        this.cacheKey = cacheKey;
        this.mediaSource = mediaSource;
        DefaultTrackSelector defaultTrackSelector = new DefaultTrackSelector(trackSelectorParameters, new DownloadTrackSelection.Factory());
        this.trackSelector = defaultTrackSelector;
        this.rendererCapabilities = rendererCapabilities;
        defaultTrackSelector.init(DownloadHelper$$ExternalSyntheticLambda0.INSTANCE, new DummyBandwidthMeter());
    }

    public static /* synthetic */ void lambda$new$0() {
    }

    public void prepare(final Callback callback) {
        Assertions.checkState(this.callback == null);
        this.callback = callback;
        if (this.mediaSource != null) {
            this.mediaPreparer = new MediaPreparer(this.mediaSource, this);
        } else {
            this.callbackHandler.post(new Runnable() { // from class: com.google.android.exoplayer2.offline.DownloadHelper$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    DownloadHelper.this.m51xc541203f(callback);
                }
            });
        }
    }

    /* renamed from: lambda$prepare$1$com-google-android-exoplayer2-offline-DownloadHelper */
    public /* synthetic */ void m51xc541203f(Callback callback) {
        callback.onPrepared(this);
    }

    public void release() {
        MediaPreparer mediaPreparer = this.mediaPreparer;
        if (mediaPreparer != null) {
            mediaPreparer.release();
        }
    }

    public Object getManifest() {
        if (this.mediaSource == null) {
            return null;
        }
        assertPreparedWithMedia();
        if (this.mediaPreparer.timeline.getWindowCount() <= 0) {
            return null;
        }
        return this.mediaPreparer.timeline.getWindow(0, this.window).manifest;
    }

    public int getPeriodCount() {
        if (this.mediaSource == null) {
            return 0;
        }
        assertPreparedWithMedia();
        return this.trackGroupArrays.length;
    }

    public TrackGroupArray getTrackGroups(int periodIndex) {
        assertPreparedWithMedia();
        return this.trackGroupArrays[periodIndex];
    }

    public MappingTrackSelector.MappedTrackInfo getMappedTrackInfo(int periodIndex) {
        assertPreparedWithMedia();
        return this.mappedTrackInfos[periodIndex];
    }

    public List<TrackSelection> getTrackSelections(int periodIndex, int rendererIndex) {
        assertPreparedWithMedia();
        return this.immutableTrackSelectionsByPeriodAndRenderer[periodIndex][rendererIndex];
    }

    public void clearTrackSelections(int periodIndex) {
        assertPreparedWithMedia();
        for (int i = 0; i < this.rendererCapabilities.length; i++) {
            this.trackSelectionsByPeriodAndRenderer[periodIndex][i].clear();
        }
    }

    public void replaceTrackSelections(int periodIndex, DefaultTrackSelector.Parameters trackSelectorParameters) {
        clearTrackSelections(periodIndex);
        addTrackSelection(periodIndex, trackSelectorParameters);
    }

    public void addTrackSelection(int periodIndex, DefaultTrackSelector.Parameters trackSelectorParameters) {
        assertPreparedWithMedia();
        this.trackSelector.setParameters(trackSelectorParameters);
        runTrackSelection(periodIndex);
    }

    public void addAudioLanguagesToSelection(String... languages) {
        assertPreparedWithMedia();
        for (int periodIndex = 0; periodIndex < this.mappedTrackInfos.length; periodIndex++) {
            DefaultTrackSelector.ParametersBuilder parametersBuilder = DEFAULT_TRACK_SELECTOR_PARAMETERS_WITHOUT_CONTEXT.buildUpon();
            MappingTrackSelector.MappedTrackInfo mappedTrackInfo = this.mappedTrackInfos[periodIndex];
            int rendererCount = mappedTrackInfo.getRendererCount();
            for (int rendererIndex = 0; rendererIndex < rendererCount; rendererIndex++) {
                if (mappedTrackInfo.getRendererType(rendererIndex) != 1) {
                    parametersBuilder.setRendererDisabled(rendererIndex, true);
                }
            }
            for (String language : languages) {
                parametersBuilder.setPreferredAudioLanguage(language);
                addTrackSelection(periodIndex, parametersBuilder.build());
            }
        }
    }

    public void addTextLanguagesToSelection(boolean selectUndeterminedTextLanguage, String... languages) {
        assertPreparedWithMedia();
        for (int periodIndex = 0; periodIndex < this.mappedTrackInfos.length; periodIndex++) {
            DefaultTrackSelector.ParametersBuilder parametersBuilder = DEFAULT_TRACK_SELECTOR_PARAMETERS_WITHOUT_CONTEXT.buildUpon();
            MappingTrackSelector.MappedTrackInfo mappedTrackInfo = this.mappedTrackInfos[periodIndex];
            int rendererCount = mappedTrackInfo.getRendererCount();
            for (int rendererIndex = 0; rendererIndex < rendererCount; rendererIndex++) {
                if (mappedTrackInfo.getRendererType(rendererIndex) != 3) {
                    parametersBuilder.setRendererDisabled(rendererIndex, true);
                }
            }
            parametersBuilder.setSelectUndeterminedTextLanguage(selectUndeterminedTextLanguage);
            for (String language : languages) {
                parametersBuilder.setPreferredTextLanguage(language);
                addTrackSelection(periodIndex, parametersBuilder.build());
            }
        }
    }

    public void addTrackSelectionForSingleRenderer(int periodIndex, int rendererIndex, DefaultTrackSelector.Parameters trackSelectorParameters, List<DefaultTrackSelector.SelectionOverride> overrides) {
        assertPreparedWithMedia();
        DefaultTrackSelector.ParametersBuilder builder = trackSelectorParameters.buildUpon();
        int i = 0;
        while (i < this.mappedTrackInfos[periodIndex].getRendererCount()) {
            builder.setRendererDisabled(i, i != rendererIndex);
            i++;
        }
        if (overrides.isEmpty()) {
            addTrackSelection(periodIndex, builder.build());
            return;
        }
        TrackGroupArray trackGroupArray = this.mappedTrackInfos[periodIndex].getTrackGroups(rendererIndex);
        for (int i2 = 0; i2 < overrides.size(); i2++) {
            builder.setSelectionOverride(rendererIndex, trackGroupArray, overrides.get(i2));
            addTrackSelection(periodIndex, builder.build());
        }
    }

    public DownloadRequest getDownloadRequest(byte[] data) {
        return getDownloadRequest(this.uri.toString(), data);
    }

    public DownloadRequest getDownloadRequest(String id, byte[] data) {
        if (this.mediaSource == null) {
            return new DownloadRequest(id, this.downloadType, this.uri, Collections.emptyList(), this.cacheKey, data);
        }
        assertPreparedWithMedia();
        List<StreamKey> streamKeys = new ArrayList<>();
        List<TrackSelection> allSelections = new ArrayList<>();
        int periodCount = this.trackSelectionsByPeriodAndRenderer.length;
        for (int periodIndex = 0; periodIndex < periodCount; periodIndex++) {
            allSelections.clear();
            int rendererCount = this.trackSelectionsByPeriodAndRenderer[periodIndex].length;
            for (int rendererIndex = 0; rendererIndex < rendererCount; rendererIndex++) {
                allSelections.addAll(this.trackSelectionsByPeriodAndRenderer[periodIndex][rendererIndex]);
            }
            streamKeys.addAll(this.mediaPreparer.mediaPeriods[periodIndex].getStreamKeys(allSelections));
        }
        return new DownloadRequest(id, this.downloadType, this.uri, streamKeys, this.cacheKey, data);
    }

    public void onMediaPrepared() {
        Assertions.checkNotNull(this.mediaPreparer);
        Assertions.checkNotNull(this.mediaPreparer.mediaPeriods);
        Assertions.checkNotNull(this.mediaPreparer.timeline);
        int periodCount = this.mediaPreparer.mediaPeriods.length;
        int rendererCount = this.rendererCapabilities.length;
        this.trackSelectionsByPeriodAndRenderer = (List[][]) Array.newInstance(List.class, periodCount, rendererCount);
        this.immutableTrackSelectionsByPeriodAndRenderer = (List[][]) Array.newInstance(List.class, periodCount, rendererCount);
        for (int i = 0; i < periodCount; i++) {
            for (int j = 0; j < rendererCount; j++) {
                this.trackSelectionsByPeriodAndRenderer[i][j] = new ArrayList();
                this.immutableTrackSelectionsByPeriodAndRenderer[i][j] = Collections.unmodifiableList(this.trackSelectionsByPeriodAndRenderer[i][j]);
            }
        }
        this.trackGroupArrays = new TrackGroupArray[periodCount];
        this.mappedTrackInfos = new MappingTrackSelector.MappedTrackInfo[periodCount];
        for (int i2 = 0; i2 < periodCount; i2++) {
            this.trackGroupArrays[i2] = this.mediaPreparer.mediaPeriods[i2].getTrackGroups();
            TrackSelectorResult trackSelectorResult = runTrackSelection(i2);
            this.trackSelector.onSelectionActivated(trackSelectorResult.info);
            this.mappedTrackInfos[i2] = (MappingTrackSelector.MappedTrackInfo) Assertions.checkNotNull(this.trackSelector.getCurrentMappedTrackInfo());
        }
        setPreparedWithMedia();
        ((Handler) Assertions.checkNotNull(this.callbackHandler)).post(new Runnable() { // from class: com.google.android.exoplayer2.offline.DownloadHelper$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                DownloadHelper.this.m50x87de83e3();
            }
        });
    }

    /* renamed from: lambda$onMediaPrepared$2$com-google-android-exoplayer2-offline-DownloadHelper */
    public /* synthetic */ void m50x87de83e3() {
        ((Callback) Assertions.checkNotNull(this.callback)).onPrepared(this);
    }

    public void onMediaPreparationFailed(final IOException error) {
        ((Handler) Assertions.checkNotNull(this.callbackHandler)).post(new Runnable() { // from class: com.google.android.exoplayer2.offline.DownloadHelper$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                DownloadHelper.this.m49x7320173b(error);
            }
        });
    }

    /* renamed from: lambda$onMediaPreparationFailed$3$com-google-android-exoplayer2-offline-DownloadHelper */
    public /* synthetic */ void m49x7320173b(IOException error) {
        ((Callback) Assertions.checkNotNull(this.callback)).onPrepareError(this, error);
    }

    @RequiresNonNull({"trackGroupArrays", "mappedTrackInfos", "trackSelectionsByPeriodAndRenderer", "immutableTrackSelectionsByPeriodAndRenderer", "mediaPreparer", "mediaPreparer.timeline", "mediaPreparer.mediaPeriods"})
    private void setPreparedWithMedia() {
        this.isPreparedWithMedia = true;
    }

    @EnsuresNonNull({"trackGroupArrays", "mappedTrackInfos", "trackSelectionsByPeriodAndRenderer", "immutableTrackSelectionsByPeriodAndRenderer", "mediaPreparer", "mediaPreparer.timeline", "mediaPreparer.mediaPeriods"})
    private void assertPreparedWithMedia() {
        Assertions.checkState(this.isPreparedWithMedia);
    }

    @RequiresNonNull({"trackGroupArrays", "trackSelectionsByPeriodAndRenderer", "mediaPreparer", "mediaPreparer.timeline"})
    private TrackSelectorResult runTrackSelection(int periodIndex) {
        try {
            TrackSelectorResult trackSelectorResult = this.trackSelector.selectTracks(this.rendererCapabilities, this.trackGroupArrays[periodIndex], new MediaSource.MediaPeriodId(this.mediaPreparer.timeline.getUidOfPeriod(periodIndex)), this.mediaPreparer.timeline);
            for (int i = 0; i < trackSelectorResult.length; i++) {
                TrackSelection newSelection = trackSelectorResult.selections.get(i);
                if (newSelection != null) {
                    List<TrackSelection> existingSelectionList = this.trackSelectionsByPeriodAndRenderer[periodIndex][i];
                    boolean mergedWithExistingSelection = false;
                    int j = 0;
                    while (true) {
                        if (j >= existingSelectionList.size()) {
                            break;
                        }
                        TrackSelection existingSelection = existingSelectionList.get(j);
                        if (existingSelection.getTrackGroup() != newSelection.getTrackGroup()) {
                            j++;
                        } else {
                            this.scratchSet.clear();
                            for (int k = 0; k < existingSelection.length(); k++) {
                                this.scratchSet.put(existingSelection.getIndexInTrackGroup(k), 0);
                            }
                            for (int k2 = 0; k2 < newSelection.length(); k2++) {
                                this.scratchSet.put(newSelection.getIndexInTrackGroup(k2), 0);
                            }
                            int[] mergedTracks = new int[this.scratchSet.size()];
                            for (int k3 = 0; k3 < this.scratchSet.size(); k3++) {
                                mergedTracks[k3] = this.scratchSet.keyAt(k3);
                            }
                            existingSelectionList.set(j, new DownloadTrackSelection(existingSelection.getTrackGroup(), mergedTracks));
                            mergedWithExistingSelection = true;
                        }
                    }
                    if (!mergedWithExistingSelection) {
                        existingSelectionList.add(newSelection);
                    }
                }
            }
            return trackSelectorResult;
        } catch (ExoPlaybackException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    private static Constructor<? extends MediaSourceFactory> getConstructor(String className) {
        try {
            return Class.forName(className).asSubclass(MediaSourceFactory.class).getConstructor(DataSource.Factory.class);
        } catch (ClassNotFoundException e) {
            return null;
        } catch (NoSuchMethodException e2) {
            throw new IllegalStateException(e2);
        }
    }

    private static MediaSource createMediaSourceInternal(Constructor<? extends MediaSourceFactory> constructor, Uri uri, DataSource.Factory dataSourceFactory, DrmSessionManager<?> drmSessionManager, List<StreamKey> streamKeys) {
        if (constructor == null) {
            throw new IllegalStateException("Module missing to create media source.");
        }
        try {
            MediaSourceFactory factory = constructor.newInstance(dataSourceFactory);
            if (drmSessionManager != null) {
                factory.setDrmSessionManager(drmSessionManager);
            }
            if (streamKeys != null) {
                factory.setStreamKeys(streamKeys);
            }
            return (MediaSource) Assertions.checkNotNull(factory.createMediaSource(uri));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to instantiate media source.", e);
        }
    }

    /* loaded from: classes3.dex */
    public static final class MediaPreparer implements MediaSource.MediaSourceCaller, MediaPeriod.Callback, Handler.Callback {
        private static final int DOWNLOAD_HELPER_CALLBACK_MESSAGE_FAILED = 1;
        private static final int DOWNLOAD_HELPER_CALLBACK_MESSAGE_PREPARED = 0;
        private static final int MESSAGE_CHECK_FOR_FAILURE = 1;
        private static final int MESSAGE_CONTINUE_LOADING = 2;
        private static final int MESSAGE_PREPARE_SOURCE = 0;
        private static final int MESSAGE_RELEASE = 3;
        private final DownloadHelper downloadHelper;
        private final Handler downloadHelperHandler;
        public MediaPeriod[] mediaPeriods;
        private final MediaSource mediaSource;
        private final Handler mediaSourceHandler;
        private final HandlerThread mediaSourceThread;
        private boolean released;
        public Timeline timeline;
        private final Allocator allocator = new DefaultAllocator(true, 65536);
        private final ArrayList<MediaPeriod> pendingMediaPeriods = new ArrayList<>();

        public MediaPreparer(MediaSource mediaSource, DownloadHelper downloadHelper) {
            this.mediaSource = mediaSource;
            this.downloadHelper = downloadHelper;
            Handler downloadThreadHandler = Util.createHandler(new Handler.Callback() { // from class: com.google.android.exoplayer2.offline.DownloadHelper$MediaPreparer$$ExternalSyntheticLambda0
                @Override // android.os.Handler.Callback
                public final boolean handleMessage(Message message) {
                    boolean handleDownloadHelperCallbackMessage;
                    handleDownloadHelperCallbackMessage = DownloadHelper.MediaPreparer.this.handleDownloadHelperCallbackMessage(message);
                    return handleDownloadHelperCallbackMessage;
                }
            });
            this.downloadHelperHandler = downloadThreadHandler;
            HandlerThread handlerThread = new HandlerThread("DownloadHelper");
            this.mediaSourceThread = handlerThread;
            handlerThread.start();
            Handler createHandler = Util.createHandler(handlerThread.getLooper(), this);
            this.mediaSourceHandler = createHandler;
            createHandler.sendEmptyMessage(0);
        }

        public void release() {
            if (this.released) {
                return;
            }
            this.released = true;
            this.mediaSourceHandler.sendEmptyMessage(3);
        }

        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    this.mediaSource.prepareSource(this, null);
                    this.mediaSourceHandler.sendEmptyMessage(1);
                    return true;
                case 1:
                    try {
                        if (this.mediaPeriods == null) {
                            this.mediaSource.maybeThrowSourceInfoRefreshError();
                        } else {
                            for (int i = 0; i < this.pendingMediaPeriods.size(); i++) {
                                this.pendingMediaPeriods.get(i).maybeThrowPrepareError();
                            }
                        }
                        this.mediaSourceHandler.sendEmptyMessageDelayed(1, 100L);
                    } catch (IOException e) {
                        this.downloadHelperHandler.obtainMessage(1, e).sendToTarget();
                    }
                    return true;
                case 2:
                    MediaPeriod mediaPeriod = (MediaPeriod) msg.obj;
                    if (this.pendingMediaPeriods.contains(mediaPeriod)) {
                        mediaPeriod.continueLoading(0L);
                    }
                    return true;
                case 3:
                    MediaPeriod[] mediaPeriodArr = this.mediaPeriods;
                    if (mediaPeriodArr != null) {
                        for (MediaPeriod period : mediaPeriodArr) {
                            this.mediaSource.releasePeriod(period);
                        }
                    }
                    this.mediaSource.releaseSource(this);
                    this.mediaSourceHandler.removeCallbacksAndMessages(null);
                    this.mediaSourceThread.quit();
                    return true;
                default:
                    return false;
            }
        }

        @Override // com.google.android.exoplayer2.source.MediaSource.MediaSourceCaller
        public void onSourceInfoRefreshed(MediaSource source, Timeline timeline) {
            MediaPeriod[] mediaPeriodArr;
            if (this.timeline != null) {
                return;
            }
            if (timeline.getWindow(0, new Timeline.Window()).isLive) {
                this.downloadHelperHandler.obtainMessage(1, new LiveContentUnsupportedException()).sendToTarget();
                return;
            }
            this.timeline = timeline;
            this.mediaPeriods = new MediaPeriod[timeline.getPeriodCount()];
            int i = 0;
            while (true) {
                mediaPeriodArr = this.mediaPeriods;
                if (i >= mediaPeriodArr.length) {
                    break;
                }
                MediaPeriod mediaPeriod = this.mediaSource.createPeriod(new MediaSource.MediaPeriodId(timeline.getUidOfPeriod(i)), this.allocator, 0L);
                this.mediaPeriods[i] = mediaPeriod;
                this.pendingMediaPeriods.add(mediaPeriod);
                i++;
            }
            for (MediaPeriod mediaPeriod2 : mediaPeriodArr) {
                mediaPeriod2.prepare(this, 0L);
            }
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod.Callback
        public void onPrepared(MediaPeriod mediaPeriod) {
            this.pendingMediaPeriods.remove(mediaPeriod);
            if (this.pendingMediaPeriods.isEmpty()) {
                this.mediaSourceHandler.removeMessages(1);
                this.downloadHelperHandler.sendEmptyMessage(0);
            }
        }

        public void onContinueLoadingRequested(MediaPeriod mediaPeriod) {
            if (this.pendingMediaPeriods.contains(mediaPeriod)) {
                this.mediaSourceHandler.obtainMessage(2, mediaPeriod).sendToTarget();
            }
        }

        public boolean handleDownloadHelperCallbackMessage(Message msg) {
            if (this.released) {
                return false;
            }
            switch (msg.what) {
                case 0:
                    this.downloadHelper.onMediaPrepared();
                    return true;
                case 1:
                    release();
                    this.downloadHelper.onMediaPreparationFailed((IOException) Util.castNonNull(msg.obj));
                    return true;
                default:
                    return false;
            }
        }
    }

    /* loaded from: classes3.dex */
    public static final class DownloadTrackSelection extends BaseTrackSelection {

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static final class Factory implements TrackSelection.Factory {
            private Factory() {
            }

            @Override // com.google.android.exoplayer2.trackselection.TrackSelection.Factory
            public TrackSelection[] createTrackSelections(TrackSelection.Definition[] definitions, BandwidthMeter bandwidthMeter) {
                DownloadTrackSelection downloadTrackSelection;
                TrackSelection[] selections = new TrackSelection[definitions.length];
                for (int i = 0; i < definitions.length; i++) {
                    if (definitions[i] == null) {
                        downloadTrackSelection = null;
                    } else {
                        downloadTrackSelection = new DownloadTrackSelection(definitions[i].group, definitions[i].tracks);
                    }
                    selections[i] = downloadTrackSelection;
                }
                return selections;
            }
        }

        public DownloadTrackSelection(TrackGroup trackGroup, int[] tracks) {
            super(trackGroup, tracks);
        }

        @Override // com.google.android.exoplayer2.trackselection.TrackSelection
        public int getSelectedIndex() {
            return 0;
        }

        @Override // com.google.android.exoplayer2.trackselection.TrackSelection
        public int getSelectionReason() {
            return 0;
        }

        @Override // com.google.android.exoplayer2.trackselection.TrackSelection
        public Object getSelectionData() {
            return null;
        }

        @Override // com.google.android.exoplayer2.trackselection.TrackSelection
        public void updateSelectedTrack(long playbackPositionUs, long bufferedDurationUs, long availableDurationUs, List<? extends MediaChunk> queue, MediaChunkIterator[] mediaChunkIterators) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static final class DummyBandwidthMeter implements BandwidthMeter {
        private DummyBandwidthMeter() {
        }

        @Override // com.google.android.exoplayer2.upstream.BandwidthMeter
        public long getBitrateEstimate() {
            return 0L;
        }

        @Override // com.google.android.exoplayer2.upstream.BandwidthMeter
        public TransferListener getTransferListener() {
            return null;
        }

        @Override // com.google.android.exoplayer2.upstream.BandwidthMeter
        public void addEventListener(Handler eventHandler, BandwidthMeter.EventListener eventListener) {
        }

        @Override // com.google.android.exoplayer2.upstream.BandwidthMeter
        public void removeEventListener(BandwidthMeter.EventListener eventListener) {
        }
    }
}
