package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.audio.AudioProcessor;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.audio.TeeAudioProcessor;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.video.SurfaceNotValidException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FourierTransform;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.secretmedia.ExtendedDefaultDataSourceFactory;
import org.telegram.ui.Components.VideoPlayer;
/* loaded from: classes5.dex */
public class VideoPlayer implements Player.EventListener, SimpleExoPlayer.VideoListener, AnalyticsListener, NotificationCenter.NotificationCenterDelegate {
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final int RENDERER_BUILDING_STATE_BUILDING = 2;
    private static final int RENDERER_BUILDING_STATE_BUILT = 3;
    private static final int RENDERER_BUILDING_STATE_IDLE = 1;
    private SimpleExoPlayer audioPlayer;
    private boolean audioPlayerReady;
    private String audioType;
    Handler audioUpdateHandler;
    private Uri audioUri;
    private AudioVisualizerDelegate audioVisualizerDelegate;
    private boolean autoplay;
    private Uri currentUri;
    private VideoPlayerDelegate delegate;
    private boolean isStreaming;
    private boolean lastReportedPlayWhenReady;
    private int lastReportedPlaybackState;
    private boolean looping;
    private boolean loopingMediaSource;
    private Handler mainHandler;
    private DataSource.Factory mediaDataSourceFactory;
    private boolean mixedAudio;
    private boolean mixedPlayWhenReady;
    private SimpleExoPlayer player;
    private int repeatCount;
    private boolean shouldPauseOther;
    private Surface surface;
    private TextureView textureView;
    private MappingTrackSelector trackSelector;
    private boolean triedReinit;
    private boolean videoPlayerReady;
    private String videoType;
    private Uri videoUri;

    /* loaded from: classes5.dex */
    public interface AudioVisualizerDelegate {
        boolean needUpdate();

        void onVisualizerUpdate(boolean z, boolean z2, float[] fArr);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onAudioAttributesChanged(AnalyticsListener.EventTime eventTime, AudioAttributes audioAttributes) {
        AnalyticsListener.CC.$default$onAudioAttributesChanged(this, eventTime, audioAttributes);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onAudioSessionId(AnalyticsListener.EventTime eventTime, int i) {
        AnalyticsListener.CC.$default$onAudioSessionId(this, eventTime, i);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onAudioUnderrun(AnalyticsListener.EventTime eventTime, int i, long j, long j2) {
        AnalyticsListener.CC.$default$onAudioUnderrun(this, eventTime, i, j, j2);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onBandwidthEstimate(AnalyticsListener.EventTime eventTime, int i, long j, long j2) {
        AnalyticsListener.CC.$default$onBandwidthEstimate(this, eventTime, i, j, j2);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onDecoderDisabled(AnalyticsListener.EventTime eventTime, int i, DecoderCounters decoderCounters) {
        AnalyticsListener.CC.$default$onDecoderDisabled(this, eventTime, i, decoderCounters);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onDecoderEnabled(AnalyticsListener.EventTime eventTime, int i, DecoderCounters decoderCounters) {
        AnalyticsListener.CC.$default$onDecoderEnabled(this, eventTime, i, decoderCounters);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onDecoderInitialized(AnalyticsListener.EventTime eventTime, int i, String str, long j) {
        AnalyticsListener.CC.$default$onDecoderInitialized(this, eventTime, i, str, j);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onDecoderInputFormatChanged(AnalyticsListener.EventTime eventTime, int i, Format format) {
        AnalyticsListener.CC.$default$onDecoderInputFormatChanged(this, eventTime, i, format);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onDownstreamFormatChanged(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        AnalyticsListener.CC.$default$onDownstreamFormatChanged(this, eventTime, mediaLoadData);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onDrmKeysLoaded(AnalyticsListener.EventTime eventTime) {
        AnalyticsListener.CC.$default$onDrmKeysLoaded(this, eventTime);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onDrmKeysRemoved(AnalyticsListener.EventTime eventTime) {
        AnalyticsListener.CC.$default$onDrmKeysRemoved(this, eventTime);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onDrmKeysRestored(AnalyticsListener.EventTime eventTime) {
        AnalyticsListener.CC.$default$onDrmKeysRestored(this, eventTime);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onDrmSessionAcquired(AnalyticsListener.EventTime eventTime) {
        AnalyticsListener.CC.$default$onDrmSessionAcquired(this, eventTime);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onDrmSessionManagerError(AnalyticsListener.EventTime eventTime, Exception exc) {
        AnalyticsListener.CC.$default$onDrmSessionManagerError(this, eventTime, exc);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onDrmSessionReleased(AnalyticsListener.EventTime eventTime) {
        AnalyticsListener.CC.$default$onDrmSessionReleased(this, eventTime);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onDroppedVideoFrames(AnalyticsListener.EventTime eventTime, int i, long j) {
        AnalyticsListener.CC.$default$onDroppedVideoFrames(this, eventTime, i, j);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onIsPlayingChanged(AnalyticsListener.EventTime eventTime, boolean z) {
        AnalyticsListener.CC.$default$onIsPlayingChanged(this, eventTime, z);
    }

    @Override // com.google.android.exoplayer2.Player.EventListener
    public /* synthetic */ void onIsPlayingChanged(boolean z) {
        Player.EventListener.CC.$default$onIsPlayingChanged(this, z);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onLoadCanceled(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        AnalyticsListener.CC.$default$onLoadCanceled(this, eventTime, loadEventInfo, mediaLoadData);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onLoadCompleted(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        AnalyticsListener.CC.$default$onLoadCompleted(this, eventTime, loadEventInfo, mediaLoadData);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onLoadError(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData, IOException iOException, boolean z) {
        AnalyticsListener.CC.$default$onLoadError(this, eventTime, loadEventInfo, mediaLoadData, iOException, z);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onLoadStarted(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        AnalyticsListener.CC.$default$onLoadStarted(this, eventTime, loadEventInfo, mediaLoadData);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onLoadingChanged(AnalyticsListener.EventTime eventTime, boolean z) {
        AnalyticsListener.CC.$default$onLoadingChanged(this, eventTime, z);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onMediaPeriodCreated(AnalyticsListener.EventTime eventTime) {
        AnalyticsListener.CC.$default$onMediaPeriodCreated(this, eventTime);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onMediaPeriodReleased(AnalyticsListener.EventTime eventTime) {
        AnalyticsListener.CC.$default$onMediaPeriodReleased(this, eventTime);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onMetadata(AnalyticsListener.EventTime eventTime, Metadata metadata) {
        AnalyticsListener.CC.$default$onMetadata(this, eventTime, metadata);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onPlaybackParametersChanged(AnalyticsListener.EventTime eventTime, PlaybackParameters playbackParameters) {
        AnalyticsListener.CC.$default$onPlaybackParametersChanged(this, eventTime, playbackParameters);
    }

    @Override // com.google.android.exoplayer2.Player.EventListener
    public /* synthetic */ void onPlaybackSuppressionReasonChanged(int i) {
        Player.EventListener.CC.$default$onPlaybackSuppressionReasonChanged(this, i);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onPlaybackSuppressionReasonChanged(AnalyticsListener.EventTime eventTime, int i) {
        AnalyticsListener.CC.$default$onPlaybackSuppressionReasonChanged(this, eventTime, i);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onPlayerError(AnalyticsListener.EventTime eventTime, ExoPlaybackException exoPlaybackException) {
        AnalyticsListener.CC.$default$onPlayerError(this, eventTime, exoPlaybackException);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onPlayerStateChanged(AnalyticsListener.EventTime eventTime, boolean z, int i) {
        AnalyticsListener.CC.$default$onPlayerStateChanged(this, eventTime, z, i);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onPositionDiscontinuity(AnalyticsListener.EventTime eventTime, int i) {
        AnalyticsListener.CC.$default$onPositionDiscontinuity(this, eventTime, i);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onReadingStarted(AnalyticsListener.EventTime eventTime) {
        AnalyticsListener.CC.$default$onReadingStarted(this, eventTime);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onRepeatModeChanged(AnalyticsListener.EventTime eventTime, int i) {
        AnalyticsListener.CC.$default$onRepeatModeChanged(this, eventTime, i);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onShuffleModeChanged(AnalyticsListener.EventTime eventTime, boolean z) {
        AnalyticsListener.CC.$default$onShuffleModeChanged(this, eventTime, z);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onSurfaceSizeChanged(AnalyticsListener.EventTime eventTime, int i, int i2) {
        AnalyticsListener.CC.$default$onSurfaceSizeChanged(this, eventTime, i, i2);
    }

    @Override // com.google.android.exoplayer2.Player.EventListener
    public /* synthetic */ void onTimelineChanged(Timeline timeline, int i) {
        Player.EventListener.CC.$default$onTimelineChanged(this, timeline, i);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onTimelineChanged(AnalyticsListener.EventTime eventTime, int i) {
        AnalyticsListener.CC.$default$onTimelineChanged(this, eventTime, i);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onTracksChanged(AnalyticsListener.EventTime eventTime, TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray) {
        AnalyticsListener.CC.$default$onTracksChanged(this, eventTime, trackGroupArray, trackSelectionArray);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onUpstreamDiscarded(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        AnalyticsListener.CC.$default$onUpstreamDiscarded(this, eventTime, mediaLoadData);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onVideoSizeChanged(AnalyticsListener.EventTime eventTime, int i, int i2, int i3, float f) {
        AnalyticsListener.CC.$default$onVideoSizeChanged(this, eventTime, i, i2, i3, f);
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public /* synthetic */ void onVolumeChanged(AnalyticsListener.EventTime eventTime, float f) {
        AnalyticsListener.CC.$default$onVolumeChanged(this, eventTime, f);
    }

    /* loaded from: classes5.dex */
    public interface VideoPlayerDelegate {
        void onError(VideoPlayer videoPlayer, Exception exc);

        void onRenderedFirstFrame();

        void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime);

        void onSeekFinished(AnalyticsListener.EventTime eventTime);

        void onSeekStarted(AnalyticsListener.EventTime eventTime);

        void onStateChanged(boolean z, int i);

        boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture);

        void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture);

        void onVideoSizeChanged(int i, int i2, int i3, float f);

        /* renamed from: org.telegram.ui.Components.VideoPlayer$VideoPlayerDelegate$-CC */
        /* loaded from: classes5.dex */
        public final /* synthetic */ class CC {
            public static void $default$onRenderedFirstFrame(VideoPlayerDelegate _this, AnalyticsListener.EventTime eventTime) {
            }

            public static void $default$onSeekStarted(VideoPlayerDelegate _this, AnalyticsListener.EventTime eventTime) {
            }

            public static void $default$onSeekFinished(VideoPlayerDelegate _this, AnalyticsListener.EventTime eventTime) {
            }
        }
    }

    public VideoPlayer() {
        this(true);
    }

    public VideoPlayer(boolean pauseOther) {
        this.audioUpdateHandler = new Handler(Looper.getMainLooper());
        Context context = ApplicationLoader.applicationContext;
        DefaultBandwidthMeter defaultBandwidthMeter = BANDWIDTH_METER;
        this.mediaDataSourceFactory = new ExtendedDefaultDataSourceFactory(context, defaultBandwidthMeter, new DefaultHttpDataSourceFactory("Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20150101 Firefox/47.0 (Chrome)", defaultBandwidthMeter));
        this.mainHandler = new Handler();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(defaultBandwidthMeter);
        this.trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        this.lastReportedPlaybackState = 1;
        this.shouldPauseOther = pauseOther;
        if (pauseOther) {
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.playerDidStartPlaying);
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.playerDidStartPlaying) {
            VideoPlayer p = (VideoPlayer) args[0];
            if (p != this && isPlaying()) {
                pause();
            }
        }
    }

    private void ensurePlayerCreated() {
        DefaultRenderersFactory factory;
        DefaultLoadControl loadControl = new DefaultLoadControl(new DefaultAllocator(true, 65536), 15000, 50000, 100, 5000, -1, true);
        if (this.player == null) {
            if (this.audioVisualizerDelegate != null) {
                factory = new AudioVisualizerRenderersFactory(ApplicationLoader.applicationContext);
            } else {
                factory = new DefaultRenderersFactory(ApplicationLoader.applicationContext);
            }
            factory.setExtensionRendererMode(2);
            SimpleExoPlayer newSimpleInstance = ExoPlayerFactory.newSimpleInstance(ApplicationLoader.applicationContext, factory, this.trackSelector, loadControl, (DrmSessionManager<FrameworkMediaCrypto>) null);
            this.player = newSimpleInstance;
            newSimpleInstance.addAnalyticsListener(this);
            this.player.addListener(this);
            this.player.setVideoListener(this);
            TextureView textureView = this.textureView;
            if (textureView != null) {
                this.player.setVideoTextureView(textureView);
            } else {
                Surface surface = this.surface;
                if (surface != null) {
                    this.player.setVideoSurface(surface);
                }
            }
            this.player.setPlayWhenReady(this.autoplay);
            this.player.setRepeatMode(this.looping ? 2 : 0);
        }
        if (this.mixedAudio && this.audioPlayer == null) {
            SimpleExoPlayer newSimpleInstance2 = ExoPlayerFactory.newSimpleInstance(ApplicationLoader.applicationContext, this.trackSelector, loadControl, (DrmSessionManager<FrameworkMediaCrypto>) null, 2);
            this.audioPlayer = newSimpleInstance2;
            newSimpleInstance2.addListener(new Player.EventListener() { // from class: org.telegram.ui.Components.VideoPlayer.1
                @Override // com.google.android.exoplayer2.Player.EventListener
                public /* synthetic */ void onIsPlayingChanged(boolean z) {
                    Player.EventListener.CC.$default$onIsPlayingChanged(this, z);
                }

                @Override // com.google.android.exoplayer2.Player.EventListener
                public /* synthetic */ void onPlaybackSuppressionReasonChanged(int i) {
                    Player.EventListener.CC.$default$onPlaybackSuppressionReasonChanged(this, i);
                }

                @Override // com.google.android.exoplayer2.Player.EventListener
                public /* synthetic */ void onTimelineChanged(Timeline timeline, int i) {
                    Player.EventListener.CC.$default$onTimelineChanged(this, timeline, i);
                }

                {
                    VideoPlayer.this = this;
                }

                @Override // com.google.android.exoplayer2.Player.EventListener
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                }

                @Override // com.google.android.exoplayer2.Player.EventListener
                public void onLoadingChanged(boolean isLoading) {
                }

                @Override // com.google.android.exoplayer2.Player.EventListener
                public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
                }

                @Override // com.google.android.exoplayer2.Player.EventListener
                public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
                }

                @Override // com.google.android.exoplayer2.Player.EventListener
                public void onPositionDiscontinuity(int reason) {
                }

                @Override // com.google.android.exoplayer2.Player.EventListener
                public void onSeekProcessed() {
                }

                @Override // com.google.android.exoplayer2.Player.EventListener
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    if (!VideoPlayer.this.audioPlayerReady && playbackState == 3) {
                        VideoPlayer.this.audioPlayerReady = true;
                        VideoPlayer.this.checkPlayersReady();
                    }
                }

                @Override // com.google.android.exoplayer2.Player.EventListener
                public void onRepeatModeChanged(int repeatMode) {
                }

                @Override // com.google.android.exoplayer2.Player.EventListener
                public void onPlayerError(ExoPlaybackException error) {
                }

                @Override // com.google.android.exoplayer2.Player.EventListener
                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                }
            });
            this.audioPlayer.setPlayWhenReady(this.autoplay);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0055, code lost:
        if (r11.equals(com.google.android.exoplayer2.offline.DownloadRequest.TYPE_SS) != false) goto L19;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void preparePlayerLoop(android.net.Uri r20, java.lang.String r21, android.net.Uri r22, java.lang.String r23) {
        /*
            Method dump skipped, instructions count: 230
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.VideoPlayer.preparePlayerLoop(android.net.Uri, java.lang.String, android.net.Uri, java.lang.String):void");
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:11:0x0038, code lost:
        if (r11.equals(com.google.android.exoplayer2.offline.DownloadRequest.TYPE_DASH) != false) goto L19;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void preparePlayer(android.net.Uri r10, java.lang.String r11) {
        /*
            r9 = this;
            r9.videoUri = r10
            r9.videoType = r11
            r0 = 0
            r9.audioUri = r0
            r9.audioType = r0
            r0 = 0
            r9.loopingMediaSource = r0
            r9.videoPlayerReady = r0
            r9.mixedAudio = r0
            r9.currentUri = r10
            java.lang.String r1 = r10.getScheme()
            r2 = 1
            if (r1 == 0) goto L23
            java.lang.String r3 = "file"
            boolean r3 = r1.startsWith(r3)
            if (r3 != 0) goto L23
            r3 = 1
            goto L24
        L23:
            r3 = 0
        L24:
            r9.isStreaming = r3
            r9.ensurePlayerCreated()
            r3 = -1
            int r4 = r11.hashCode()
            switch(r4) {
                case 3680: goto L45;
                case 103407: goto L3b;
                case 3075986: goto L32;
                default: goto L31;
            }
        L31:
            goto L4f
        L32:
            java.lang.String r4 = "dash"
            boolean r4 = r11.equals(r4)
            if (r4 == 0) goto L31
            goto L50
        L3b:
            java.lang.String r0 = "hls"
            boolean r0 = r11.equals(r0)
            if (r0 == 0) goto L31
            r0 = 1
            goto L50
        L45:
            java.lang.String r0 = "ss"
            boolean r0 = r11.equals(r0)
            if (r0 == 0) goto L31
            r0 = 2
            goto L50
        L4f:
            r0 = -1
        L50:
            switch(r0) {
                case 0: goto L85;
                case 1: goto L79;
                case 2: goto L65;
                default: goto L53;
            }
        L53:
            com.google.android.exoplayer2.source.ExtractorMediaSource r0 = new com.google.android.exoplayer2.source.ExtractorMediaSource
            com.google.android.exoplayer2.upstream.DataSource$Factory r5 = r9.mediaDataSourceFactory
            com.google.android.exoplayer2.extractor.DefaultExtractorsFactory r6 = new com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
            r6.<init>()
            android.os.Handler r7 = r9.mainHandler
            r8 = 0
            r3 = r0
            r4 = r10
            r3.<init>(r4, r5, r6, r7, r8)
            goto L99
        L65:
            com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource r0 = new com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
            com.google.android.exoplayer2.upstream.DataSource$Factory r5 = r9.mediaDataSourceFactory
            com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource$Factory r6 = new com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource$Factory
            com.google.android.exoplayer2.upstream.DataSource$Factory r3 = r9.mediaDataSourceFactory
            r6.<init>(r3)
            android.os.Handler r7 = r9.mainHandler
            r8 = 0
            r3 = r0
            r4 = r10
            r3.<init>(r4, r5, r6, r7, r8)
            goto L99
        L79:
            com.google.android.exoplayer2.source.hls.HlsMediaSource$Factory r0 = new com.google.android.exoplayer2.source.hls.HlsMediaSource$Factory
            com.google.android.exoplayer2.upstream.DataSource$Factory r3 = r9.mediaDataSourceFactory
            r0.<init>(r3)
            com.google.android.exoplayer2.source.hls.HlsMediaSource r0 = r0.createMediaSource(r10)
            goto L99
        L85:
            com.google.android.exoplayer2.source.dash.DashMediaSource r0 = new com.google.android.exoplayer2.source.dash.DashMediaSource
            com.google.android.exoplayer2.upstream.DataSource$Factory r5 = r9.mediaDataSourceFactory
            com.google.android.exoplayer2.source.dash.DefaultDashChunkSource$Factory r6 = new com.google.android.exoplayer2.source.dash.DefaultDashChunkSource$Factory
            com.google.android.exoplayer2.upstream.DataSource$Factory r3 = r9.mediaDataSourceFactory
            r6.<init>(r3)
            android.os.Handler r7 = r9.mainHandler
            r8 = 0
            r3 = r0
            r4 = r10
            r3.<init>(r4, r5, r6, r7, r8)
        L99:
            com.google.android.exoplayer2.SimpleExoPlayer r3 = r9.player
            r3.prepare(r0, r2, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.VideoPlayer.preparePlayer(android.net.Uri, java.lang.String):void");
    }

    public boolean isPlayerPrepared() {
        return this.player != null;
    }

    public void releasePlayer(boolean async) {
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release(async);
            this.player = null;
        }
        SimpleExoPlayer simpleExoPlayer2 = this.audioPlayer;
        if (simpleExoPlayer2 != null) {
            simpleExoPlayer2.release(async);
            this.audioPlayer = null;
        }
        if (this.shouldPauseOther) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.playerDidStartPlaying);
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onSeekStarted(AnalyticsListener.EventTime eventTime) {
        VideoPlayerDelegate videoPlayerDelegate = this.delegate;
        if (videoPlayerDelegate != null) {
            videoPlayerDelegate.onSeekStarted(eventTime);
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onSeekProcessed(AnalyticsListener.EventTime eventTime) {
        VideoPlayerDelegate videoPlayerDelegate = this.delegate;
        if (videoPlayerDelegate != null) {
            videoPlayerDelegate.onSeekFinished(eventTime);
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime, Surface surface) {
        VideoPlayerDelegate videoPlayerDelegate = this.delegate;
        if (videoPlayerDelegate != null) {
            videoPlayerDelegate.onRenderedFirstFrame(eventTime);
        }
    }

    public void setTextureView(TextureView texture) {
        if (this.textureView == texture) {
            return;
        }
        this.textureView = texture;
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer == null) {
            return;
        }
        simpleExoPlayer.setVideoTextureView(texture);
    }

    public void setSurface(Surface s) {
        if (this.surface == s) {
            return;
        }
        this.surface = s;
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer == null) {
            return;
        }
        simpleExoPlayer.setVideoSurface(s);
    }

    public boolean getPlayWhenReady() {
        return this.player.getPlayWhenReady();
    }

    public int getPlaybackState() {
        return this.player.getPlaybackState();
    }

    public Uri getCurrentUri() {
        return this.currentUri;
    }

    public void play() {
        this.mixedPlayWhenReady = true;
        if (this.mixedAudio && (!this.audioPlayerReady || !this.videoPlayerReady)) {
            SimpleExoPlayer simpleExoPlayer = this.player;
            if (simpleExoPlayer != null) {
                simpleExoPlayer.setPlayWhenReady(false);
            }
            SimpleExoPlayer simpleExoPlayer2 = this.audioPlayer;
            if (simpleExoPlayer2 != null) {
                simpleExoPlayer2.setPlayWhenReady(false);
                return;
            }
            return;
        }
        SimpleExoPlayer simpleExoPlayer3 = this.player;
        if (simpleExoPlayer3 != null) {
            simpleExoPlayer3.setPlayWhenReady(true);
        }
        SimpleExoPlayer simpleExoPlayer4 = this.audioPlayer;
        if (simpleExoPlayer4 != null) {
            simpleExoPlayer4.setPlayWhenReady(true);
        }
    }

    public void pause() {
        this.mixedPlayWhenReady = false;
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(false);
        }
        SimpleExoPlayer simpleExoPlayer2 = this.audioPlayer;
        if (simpleExoPlayer2 != null) {
            simpleExoPlayer2.setPlayWhenReady(false);
        }
        if (this.audioVisualizerDelegate != null) {
            this.audioUpdateHandler.removeCallbacksAndMessages(null);
            this.audioVisualizerDelegate.onVisualizerUpdate(false, true, null);
        }
    }

    public void setPlaybackSpeed(float speed) {
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            float f = 1.0f;
            if (speed > 1.0f) {
                f = 0.98f;
            }
            simpleExoPlayer.setPlaybackParameters(new PlaybackParameters(speed, f));
        }
    }

    public void setPlayWhenReady(boolean playWhenReady) {
        this.mixedPlayWhenReady = playWhenReady;
        if (playWhenReady && this.mixedAudio && (!this.audioPlayerReady || !this.videoPlayerReady)) {
            SimpleExoPlayer simpleExoPlayer = this.player;
            if (simpleExoPlayer != null) {
                simpleExoPlayer.setPlayWhenReady(false);
            }
            SimpleExoPlayer simpleExoPlayer2 = this.audioPlayer;
            if (simpleExoPlayer2 != null) {
                simpleExoPlayer2.setPlayWhenReady(false);
                return;
            }
            return;
        }
        this.autoplay = playWhenReady;
        SimpleExoPlayer simpleExoPlayer3 = this.player;
        if (simpleExoPlayer3 != null) {
            simpleExoPlayer3.setPlayWhenReady(playWhenReady);
        }
        SimpleExoPlayer simpleExoPlayer4 = this.audioPlayer;
        if (simpleExoPlayer4 != null) {
            simpleExoPlayer4.setPlayWhenReady(playWhenReady);
        }
    }

    public long getDuration() {
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            return simpleExoPlayer.getDuration();
        }
        return 0L;
    }

    public long getCurrentPosition() {
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            return simpleExoPlayer.getCurrentPosition();
        }
        return 0L;
    }

    public boolean isMuted() {
        SimpleExoPlayer simpleExoPlayer = this.player;
        return simpleExoPlayer != null && simpleExoPlayer.getVolume() == 0.0f;
    }

    public void setMute(boolean value) {
        SimpleExoPlayer simpleExoPlayer = this.player;
        float f = 0.0f;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setVolume(value ? 0.0f : 1.0f);
        }
        SimpleExoPlayer simpleExoPlayer2 = this.audioPlayer;
        if (simpleExoPlayer2 != null) {
            if (!value) {
                f = 1.0f;
            }
            simpleExoPlayer2.setVolume(f);
        }
    }

    @Override // com.google.android.exoplayer2.Player.EventListener
    public void onRepeatModeChanged(int repeatMode) {
    }

    @Override // com.google.android.exoplayer2.video.VideoListener
    public void onSurfaceSizeChanged(int width, int height) {
    }

    public void setVolume(float volume) {
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setVolume(volume);
        }
        SimpleExoPlayer simpleExoPlayer2 = this.audioPlayer;
        if (simpleExoPlayer2 != null) {
            simpleExoPlayer2.setVolume(volume);
        }
    }

    public void seekTo(long positionMs) {
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.seekTo(positionMs);
        }
    }

    public void setDelegate(VideoPlayerDelegate videoPlayerDelegate) {
        this.delegate = videoPlayerDelegate;
    }

    public void setAudioVisualizerDelegate(AudioVisualizerDelegate audioVisualizerDelegate) {
        this.audioVisualizerDelegate = audioVisualizerDelegate;
    }

    public int getBufferedPercentage() {
        if (this.isStreaming) {
            SimpleExoPlayer simpleExoPlayer = this.player;
            if (simpleExoPlayer == null) {
                return 0;
            }
            return simpleExoPlayer.getBufferedPercentage();
        }
        return 100;
    }

    public long getBufferedPosition() {
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            return this.isStreaming ? simpleExoPlayer.getBufferedPosition() : simpleExoPlayer.getDuration();
        }
        return 0L;
    }

    public boolean isStreaming() {
        return this.isStreaming;
    }

    public boolean isPlaying() {
        SimpleExoPlayer simpleExoPlayer;
        return (this.mixedAudio && this.mixedPlayWhenReady) || ((simpleExoPlayer = this.player) != null && simpleExoPlayer.getPlayWhenReady());
    }

    public boolean isBuffering() {
        return this.player != null && this.lastReportedPlaybackState == 2;
    }

    public void setStreamType(int type) {
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setAudioStreamType(type);
        }
        SimpleExoPlayer simpleExoPlayer2 = this.audioPlayer;
        if (simpleExoPlayer2 != null) {
            simpleExoPlayer2.setAudioStreamType(type);
        }
    }

    public void setLooping(boolean looping) {
        if (this.looping != looping) {
            this.looping = looping;
            SimpleExoPlayer simpleExoPlayer = this.player;
            if (simpleExoPlayer != null) {
                simpleExoPlayer.setRepeatMode(looping ? 2 : 0);
            }
        }
    }

    public boolean isLooping() {
        return this.looping;
    }

    public void checkPlayersReady() {
        if (this.audioPlayerReady && this.videoPlayerReady && this.mixedPlayWhenReady) {
            play();
        }
    }

    @Override // com.google.android.exoplayer2.Player.EventListener
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override // com.google.android.exoplayer2.Player.EventListener
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        maybeReportPlayerState();
        if (playWhenReady && playbackState == 3 && !isMuted() && this.shouldPauseOther) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.playerDidStartPlaying, this);
        }
        if (!this.videoPlayerReady && playbackState == 3) {
            this.videoPlayerReady = true;
            checkPlayersReady();
        }
        if (playbackState != 3) {
            this.audioUpdateHandler.removeCallbacksAndMessages(null);
            AudioVisualizerDelegate audioVisualizerDelegate = this.audioVisualizerDelegate;
            if (audioVisualizerDelegate != null) {
                audioVisualizerDelegate.onVisualizerUpdate(false, true, null);
            }
        }
    }

    @Override // com.google.android.exoplayer2.Player.EventListener
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
    }

    @Override // com.google.android.exoplayer2.Player.EventListener
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
    }

    @Override // com.google.android.exoplayer2.Player.EventListener
    public void onPositionDiscontinuity(int reason) {
        if (reason == 0) {
            this.repeatCount++;
        }
    }

    @Override // com.google.android.exoplayer2.Player.EventListener
    public void onSeekProcessed() {
    }

    @Override // com.google.android.exoplayer2.Player.EventListener
    public void onPlayerError(ExoPlaybackException error) {
        Throwable cause = error.getCause();
        TextureView textureView = this.textureView;
        if (textureView != null && ((!this.triedReinit && (cause instanceof MediaCodecRenderer.DecoderInitializationException)) || (cause instanceof SurfaceNotValidException))) {
            this.triedReinit = true;
            if (this.player != null) {
                ViewGroup parent = (ViewGroup) textureView.getParent();
                if (parent != null) {
                    int i = parent.indexOfChild(this.textureView);
                    parent.removeView(this.textureView);
                    parent.addView(this.textureView, i);
                }
                this.player.clearVideoTextureView(this.textureView);
                this.player.setVideoTextureView(this.textureView);
                if (this.loopingMediaSource) {
                    preparePlayerLoop(this.videoUri, this.videoType, this.audioUri, this.audioType);
                } else {
                    preparePlayer(this.videoUri, this.videoType);
                }
                play();
                return;
            }
            return;
        }
        this.delegate.onError(this, error);
    }

    @Override // com.google.android.exoplayer2.Player.EventListener
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override // com.google.android.exoplayer2.video.VideoListener
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        this.delegate.onVideoSizeChanged(width, height, unappliedRotationDegrees, pixelWidthHeightRatio);
    }

    @Override // com.google.android.exoplayer2.video.VideoListener
    public void onRenderedFirstFrame() {
        this.delegate.onRenderedFirstFrame();
    }

    @Override // com.google.android.exoplayer2.video.VideoListener
    public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
        return this.delegate.onSurfaceDestroyed(surfaceTexture);
    }

    @Override // com.google.android.exoplayer2.video.VideoListener
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        this.delegate.onSurfaceTextureUpdated(surfaceTexture);
    }

    @Override // com.google.android.exoplayer2.Player.EventListener
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
    }

    private void maybeReportPlayerState() {
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer == null) {
            return;
        }
        boolean playWhenReady = simpleExoPlayer.getPlayWhenReady();
        int playbackState = this.player.getPlaybackState();
        if (this.lastReportedPlayWhenReady != playWhenReady || this.lastReportedPlaybackState != playbackState) {
            this.delegate.onStateChanged(playWhenReady, playbackState);
            this.lastReportedPlayWhenReady = playWhenReady;
            this.lastReportedPlaybackState = playbackState;
        }
    }

    public int getRepeatCount() {
        return this.repeatCount;
    }

    /* loaded from: classes5.dex */
    public class AudioVisualizerRenderersFactory extends DefaultRenderersFactory {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public AudioVisualizerRenderersFactory(Context context) {
            super(context);
            VideoPlayer.this = r1;
        }

        @Override // com.google.android.exoplayer2.DefaultRenderersFactory
        public void buildAudioRenderers(Context context, int extensionRendererMode, MediaCodecSelector mediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, boolean playClearSamplesWithoutKeys, boolean enableDecoderFallback, AudioProcessor[] audioProcessors, Handler eventHandler, AudioRendererEventListener eventListener, ArrayList<Renderer> out) {
            AudioProcessor audioProcessor = new TeeAudioProcessor(new VisualizerBufferSink());
            super.buildAudioRenderers(context, extensionRendererMode, mediaCodecSelector, drmSessionManager, playClearSamplesWithoutKeys, enableDecoderFallback, new AudioProcessor[]{audioProcessor}, eventHandler, eventListener, out);
        }
    }

    /* loaded from: classes5.dex */
    public class VisualizerBufferSink implements TeeAudioProcessor.AudioBufferSink {
        ByteBuffer byteBuffer;
        long lastUpdateTime;
        private final int BUFFER_SIZE = 1024;
        private final int MAX_BUFFER_SIZE = 8192;
        FourierTransform.FFT fft = new FourierTransform.FFT(1024, 48000.0f);
        float[] real = new float[1024];
        int position = 0;

        public VisualizerBufferSink() {
            VideoPlayer.this = r4;
            ByteBuffer allocateDirect = ByteBuffer.allocateDirect(8192);
            this.byteBuffer = allocateDirect;
            allocateDirect.position(0);
        }

        @Override // com.google.android.exoplayer2.audio.TeeAudioProcessor.AudioBufferSink
        public void flush(int sampleRateHz, int channelCount, int encoding) {
        }

        @Override // com.google.android.exoplayer2.audio.TeeAudioProcessor.AudioBufferSink
        public void handleBuffer(ByteBuffer buffer) {
            if (VideoPlayer.this.audioVisualizerDelegate == null) {
                return;
            }
            if (buffer != AudioProcessor.EMPTY_BUFFER && VideoPlayer.this.mixedPlayWhenReady) {
                if (!VideoPlayer.this.audioVisualizerDelegate.needUpdate()) {
                    return;
                }
                int len = buffer.limit();
                if (len > 8192) {
                    VideoPlayer.this.audioUpdateHandler.removeCallbacksAndMessages(null);
                    VideoPlayer.this.audioVisualizerDelegate.onVisualizerUpdate(false, true, null);
                    return;
                }
                this.byteBuffer.put(buffer);
                int i = this.position + len;
                this.position = i;
                if (i >= 1024) {
                    this.byteBuffer.position(0);
                    for (int i2 = 0; i2 < 1024; i2++) {
                        this.real[i2] = this.byteBuffer.getShort() / 32768.0f;
                    }
                    this.byteBuffer.rewind();
                    this.position = 0;
                    this.fft.forward(this.real);
                    float sum = 0.0f;
                    for (int i3 = 0; i3 < 1024; i3++) {
                        float r = this.fft.getSpectrumReal()[i3];
                        float img = this.fft.getSpectrumImaginary()[i3];
                        float peak = ((float) Math.sqrt((r * r) + (img * img))) / 30.0f;
                        if (peak > 1.0f) {
                            peak = 1.0f;
                        } else if (peak < 0.0f) {
                            peak = 0.0f;
                        }
                        sum += peak * peak;
                    }
                    float amplitude = (float) Math.sqrt(sum / 1024);
                    final float[] partsAmplitude = new float[7];
                    partsAmplitude[6] = amplitude;
                    if (amplitude < 0.4f) {
                        for (int k = 0; k < 7; k++) {
                            partsAmplitude[k] = 0.0f;
                        }
                    } else {
                        int part = 1024 / 6;
                        for (int k2 = 0; k2 < 6; k2++) {
                            int start = part * k2;
                            float r2 = this.fft.getSpectrumReal()[start];
                            float img2 = this.fft.getSpectrumImaginary()[start];
                            partsAmplitude[k2] = (float) (Math.sqrt((r2 * r2) + (img2 * img2)) / 30.0d);
                            if (partsAmplitude[k2] > 1.0f) {
                                partsAmplitude[k2] = 1.0f;
                            } else if (partsAmplitude[k2] < 0.0f) {
                                partsAmplitude[k2] = 0.0f;
                            }
                        }
                    }
                    if (System.currentTimeMillis() - this.lastUpdateTime >= 64) {
                        this.lastUpdateTime = System.currentTimeMillis();
                        VideoPlayer.this.audioUpdateHandler.postDelayed(new Runnable() { // from class: org.telegram.ui.Components.VideoPlayer$VisualizerBufferSink$$ExternalSyntheticLambda1
                            @Override // java.lang.Runnable
                            public final void run() {
                                VideoPlayer.VisualizerBufferSink.this.m3200xb50afb68(partsAmplitude);
                            }
                        }, 130L);
                        return;
                    }
                    return;
                }
                return;
            }
            VideoPlayer.this.audioUpdateHandler.postDelayed(new Runnable() { // from class: org.telegram.ui.Components.VideoPlayer$VisualizerBufferSink$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    VideoPlayer.VisualizerBufferSink.this.m3199xfa955ae7();
                }
            }, 80L);
        }

        /* renamed from: lambda$handleBuffer$0$org-telegram-ui-Components-VideoPlayer$VisualizerBufferSink */
        public /* synthetic */ void m3199xfa955ae7() {
            VideoPlayer.this.audioUpdateHandler.removeCallbacksAndMessages(null);
            VideoPlayer.this.audioVisualizerDelegate.onVisualizerUpdate(false, true, null);
        }

        /* renamed from: lambda$handleBuffer$1$org-telegram-ui-Components-VideoPlayer$VisualizerBufferSink */
        public /* synthetic */ void m3200xb50afb68(float[] partsAmplitude) {
            VideoPlayer.this.audioVisualizerDelegate.onVisualizerUpdate(true, true, partsAmplitude);
        }
    }
}
