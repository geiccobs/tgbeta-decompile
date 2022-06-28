package com.google.android.exoplayer2;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Pair;
import com.google.android.exoplayer2.BasePlayer;
import com.google.android.exoplayer2.ExoPlayerImpl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.PlayerMessage;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectorResult;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public final class ExoPlayerImpl extends BasePlayer implements ExoPlayer {
    private static final String TAG = "ExoPlayerImpl";
    final TrackSelectorResult emptyTrackSelectorResult;
    private final Handler eventHandler;
    private boolean foregroundMode;
    private boolean hasPendingPrepare;
    private boolean hasPendingSeek;
    private final ExoPlayerImplInternal internalPlayer;
    private final Handler internalPlayerHandler;
    private final CopyOnWriteArrayList<BasePlayer.ListenerHolder> listeners;
    private int maskingPeriodIndex;
    private int maskingWindowIndex;
    private long maskingWindowPositionMs;
    private MediaSource mediaSource;
    private final ArrayDeque<Runnable> pendingListenerNotifications;
    private int pendingOperationAcks;
    private int pendingSetPlaybackParametersAcks;
    private final Timeline.Period period;
    private boolean playWhenReady;
    private PlaybackInfo playbackInfo;
    private PlaybackParameters playbackParameters;
    private int playbackSuppressionReason;
    private final Renderer[] renderers;
    private int repeatMode;
    private SeekParameters seekParameters;
    private boolean shuffleModeEnabled;
    private final TrackSelector trackSelector;

    public ExoPlayerImpl(Renderer[] renderers, TrackSelector trackSelector, LoadControl loadControl, BandwidthMeter bandwidthMeter, Clock clock, Looper looper) {
        Log.i(TAG, "Init " + Integer.toHexString(System.identityHashCode(this)) + " [" + ExoPlayerLibraryInfo.VERSION_SLASHY + "] [" + Util.DEVICE_DEBUG_INFO + "]");
        Assertions.checkState(renderers.length > 0);
        this.renderers = (Renderer[]) Assertions.checkNotNull(renderers);
        this.trackSelector = (TrackSelector) Assertions.checkNotNull(trackSelector);
        this.playWhenReady = false;
        this.repeatMode = 0;
        this.shuffleModeEnabled = false;
        this.listeners = new CopyOnWriteArrayList<>();
        TrackSelectorResult trackSelectorResult = new TrackSelectorResult(new RendererConfiguration[renderers.length], new TrackSelection[renderers.length], null);
        this.emptyTrackSelectorResult = trackSelectorResult;
        this.period = new Timeline.Period();
        this.playbackParameters = PlaybackParameters.DEFAULT;
        this.seekParameters = SeekParameters.DEFAULT;
        this.playbackSuppressionReason = 0;
        Handler handler = new Handler(looper) { // from class: com.google.android.exoplayer2.ExoPlayerImpl.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                ExoPlayerImpl.this.handleEvent(msg);
            }
        };
        this.eventHandler = handler;
        this.playbackInfo = PlaybackInfo.createDummy(0L, trackSelectorResult);
        this.pendingListenerNotifications = new ArrayDeque<>();
        ExoPlayerImplInternal exoPlayerImplInternal = new ExoPlayerImplInternal(renderers, trackSelector, trackSelectorResult, loadControl, bandwidthMeter, this.playWhenReady, this.repeatMode, this.shuffleModeEnabled, handler, clock);
        this.internalPlayer = exoPlayerImplInternal;
        this.internalPlayerHandler = new Handler(exoPlayerImplInternal.getPlaybackLooper());
    }

    @Override // com.google.android.exoplayer2.Player
    public Player.AudioComponent getAudioComponent() {
        return null;
    }

    @Override // com.google.android.exoplayer2.Player
    public Player.VideoComponent getVideoComponent() {
        return null;
    }

    @Override // com.google.android.exoplayer2.Player
    public Player.TextComponent getTextComponent() {
        return null;
    }

    @Override // com.google.android.exoplayer2.Player
    public Player.MetadataComponent getMetadataComponent() {
        return null;
    }

    @Override // com.google.android.exoplayer2.ExoPlayer
    public Looper getPlaybackLooper() {
        return this.internalPlayer.getPlaybackLooper();
    }

    @Override // com.google.android.exoplayer2.Player
    public Looper getApplicationLooper() {
        return this.eventHandler.getLooper();
    }

    @Override // com.google.android.exoplayer2.Player
    public void addListener(Player.EventListener listener) {
        this.listeners.addIfAbsent(new BasePlayer.ListenerHolder(listener));
    }

    @Override // com.google.android.exoplayer2.Player
    public void removeListener(Player.EventListener listener) {
        Iterator<BasePlayer.ListenerHolder> it = this.listeners.iterator();
        while (it.hasNext()) {
            BasePlayer.ListenerHolder listenerHolder = it.next();
            if (listenerHolder.listener.equals(listener)) {
                listenerHolder.release();
                this.listeners.remove(listenerHolder);
            }
        }
    }

    @Override // com.google.android.exoplayer2.Player
    public int getPlaybackState() {
        return this.playbackInfo.playbackState;
    }

    @Override // com.google.android.exoplayer2.Player
    public int getPlaybackSuppressionReason() {
        return this.playbackSuppressionReason;
    }

    @Override // com.google.android.exoplayer2.Player
    public ExoPlaybackException getPlaybackError() {
        return this.playbackInfo.playbackError;
    }

    @Override // com.google.android.exoplayer2.ExoPlayer
    public void retry() {
        if (this.mediaSource != null && this.playbackInfo.playbackState == 1) {
            prepare(this.mediaSource, false, false);
        }
    }

    @Override // com.google.android.exoplayer2.ExoPlayer
    public void prepare(MediaSource mediaSource) {
        prepare(mediaSource, true, true);
    }

    @Override // com.google.android.exoplayer2.ExoPlayer
    public void prepare(MediaSource mediaSource, boolean resetPosition, boolean resetState) {
        this.mediaSource = mediaSource;
        PlaybackInfo playbackInfo = getResetPlaybackInfo(resetPosition, resetState, true, 2);
        this.hasPendingPrepare = true;
        this.pendingOperationAcks++;
        this.internalPlayer.prepare(mediaSource, resetPosition, resetState);
        updatePlaybackInfo(playbackInfo, false, 4, 1, false);
    }

    @Override // com.google.android.exoplayer2.Player
    public void setPlayWhenReady(boolean playWhenReady) {
        setPlayWhenReady(playWhenReady, 0);
    }

    public void setPlayWhenReady(final boolean playWhenReady, final int playbackSuppressionReason) {
        boolean oldIsPlaying = isPlaying();
        boolean z = true;
        boolean oldInternalPlayWhenReady = this.playWhenReady && this.playbackSuppressionReason == 0;
        boolean internalPlayWhenReady = playWhenReady && playbackSuppressionReason == 0;
        if (oldInternalPlayWhenReady != internalPlayWhenReady) {
            this.internalPlayer.setPlayWhenReady(internalPlayWhenReady);
        }
        final boolean playWhenReadyChanged = this.playWhenReady != playWhenReady;
        final boolean suppressionReasonChanged = this.playbackSuppressionReason != playbackSuppressionReason;
        this.playWhenReady = playWhenReady;
        this.playbackSuppressionReason = playbackSuppressionReason;
        final boolean isPlaying = isPlaying();
        if (oldIsPlaying == isPlaying) {
            z = false;
        }
        final boolean isPlayingChanged = z;
        if (playWhenReadyChanged || suppressionReasonChanged || isPlayingChanged) {
            final int playbackState = this.playbackInfo.playbackState;
            notifyListeners(new BasePlayer.ListenerInvocation() { // from class: com.google.android.exoplayer2.ExoPlayerImpl$$ExternalSyntheticLambda4
                @Override // com.google.android.exoplayer2.BasePlayer.ListenerInvocation
                public final void invokeListener(Player.EventListener eventListener) {
                    ExoPlayerImpl.lambda$setPlayWhenReady$0(playWhenReadyChanged, playWhenReady, playbackState, suppressionReasonChanged, playbackSuppressionReason, isPlayingChanged, isPlaying, eventListener);
                }
            });
        }
    }

    public static /* synthetic */ void lambda$setPlayWhenReady$0(boolean playWhenReadyChanged, boolean playWhenReady, int playbackState, boolean suppressionReasonChanged, int playbackSuppressionReason, boolean isPlayingChanged, boolean isPlaying, Player.EventListener listener) {
        if (playWhenReadyChanged) {
            listener.onPlayerStateChanged(playWhenReady, playbackState);
        }
        if (suppressionReasonChanged) {
            listener.onPlaybackSuppressionReasonChanged(playbackSuppressionReason);
        }
        if (isPlayingChanged) {
            listener.onIsPlayingChanged(isPlaying);
        }
    }

    @Override // com.google.android.exoplayer2.Player
    public boolean getPlayWhenReady() {
        return this.playWhenReady;
    }

    @Override // com.google.android.exoplayer2.Player
    public void setRepeatMode(final int repeatMode) {
        if (this.repeatMode != repeatMode) {
            this.repeatMode = repeatMode;
            this.internalPlayer.setRepeatMode(repeatMode);
            notifyListeners(new BasePlayer.ListenerInvocation() { // from class: com.google.android.exoplayer2.ExoPlayerImpl$$ExternalSyntheticLambda0
                @Override // com.google.android.exoplayer2.BasePlayer.ListenerInvocation
                public final void invokeListener(Player.EventListener eventListener) {
                    eventListener.onRepeatModeChanged(repeatMode);
                }
            });
        }
    }

    @Override // com.google.android.exoplayer2.Player
    public int getRepeatMode() {
        return this.repeatMode;
    }

    @Override // com.google.android.exoplayer2.Player
    public void setShuffleModeEnabled(final boolean shuffleModeEnabled) {
        if (this.shuffleModeEnabled != shuffleModeEnabled) {
            this.shuffleModeEnabled = shuffleModeEnabled;
            this.internalPlayer.setShuffleModeEnabled(shuffleModeEnabled);
            notifyListeners(new BasePlayer.ListenerInvocation() { // from class: com.google.android.exoplayer2.ExoPlayerImpl$$ExternalSyntheticLambda3
                @Override // com.google.android.exoplayer2.BasePlayer.ListenerInvocation
                public final void invokeListener(Player.EventListener eventListener) {
                    eventListener.onShuffleModeEnabledChanged(shuffleModeEnabled);
                }
            });
        }
    }

    @Override // com.google.android.exoplayer2.Player
    public boolean getShuffleModeEnabled() {
        return this.shuffleModeEnabled;
    }

    @Override // com.google.android.exoplayer2.Player
    public boolean isLoading() {
        return this.playbackInfo.isLoading;
    }

    @Override // com.google.android.exoplayer2.Player
    public void seekTo(int windowIndex, long positionMs) {
        Timeline timeline = this.playbackInfo.timeline;
        if (windowIndex < 0 || (!timeline.isEmpty() && windowIndex >= timeline.getWindowCount())) {
            throw new IllegalSeekPositionException(timeline, windowIndex, positionMs);
        }
        this.hasPendingSeek = true;
        this.pendingOperationAcks++;
        if (isPlayingAd()) {
            Log.w(TAG, "seekTo ignored because an ad is playing");
            this.eventHandler.obtainMessage(0, 1, -1, this.playbackInfo).sendToTarget();
            return;
        }
        this.maskingWindowIndex = windowIndex;
        if (timeline.isEmpty()) {
            this.maskingWindowPositionMs = positionMs == C.TIME_UNSET ? 0L : positionMs;
            this.maskingPeriodIndex = 0;
        } else {
            long windowPositionUs = positionMs == C.TIME_UNSET ? timeline.getWindow(windowIndex, this.window).getDefaultPositionUs() : C.msToUs(positionMs);
            Pair<Object, Long> periodUidAndPosition = timeline.getPeriodPosition(this.window, this.period, windowIndex, windowPositionUs);
            this.maskingWindowPositionMs = C.usToMs(windowPositionUs);
            this.maskingPeriodIndex = timeline.getIndexOfPeriod(periodUidAndPosition.first);
        }
        this.internalPlayer.seekTo(timeline, windowIndex, C.msToUs(positionMs));
        notifyListeners(ExoPlayerImpl$$ExternalSyntheticLambda5.INSTANCE);
    }

    @Override // com.google.android.exoplayer2.Player
    public void setPlaybackParameters(PlaybackParameters playbackParameters) {
        if (playbackParameters == null) {
            playbackParameters = PlaybackParameters.DEFAULT;
        }
        if (this.playbackParameters.equals(playbackParameters)) {
            return;
        }
        this.pendingSetPlaybackParametersAcks++;
        this.playbackParameters = playbackParameters;
        this.internalPlayer.setPlaybackParameters(playbackParameters);
        final PlaybackParameters playbackParametersToNotify = playbackParameters;
        notifyListeners(new BasePlayer.ListenerInvocation() { // from class: com.google.android.exoplayer2.ExoPlayerImpl$$ExternalSyntheticLambda2
            @Override // com.google.android.exoplayer2.BasePlayer.ListenerInvocation
            public final void invokeListener(Player.EventListener eventListener) {
                eventListener.onPlaybackParametersChanged(PlaybackParameters.this);
            }
        });
    }

    @Override // com.google.android.exoplayer2.Player
    public PlaybackParameters getPlaybackParameters() {
        return this.playbackParameters;
    }

    @Override // com.google.android.exoplayer2.ExoPlayer
    public void setSeekParameters(SeekParameters seekParameters) {
        if (seekParameters == null) {
            seekParameters = SeekParameters.DEFAULT;
        }
        if (!this.seekParameters.equals(seekParameters)) {
            this.seekParameters = seekParameters;
            this.internalPlayer.setSeekParameters(seekParameters);
        }
    }

    @Override // com.google.android.exoplayer2.ExoPlayer
    public SeekParameters getSeekParameters() {
        return this.seekParameters;
    }

    @Override // com.google.android.exoplayer2.ExoPlayer
    public void setForegroundMode(boolean foregroundMode) {
        if (this.foregroundMode != foregroundMode) {
            this.foregroundMode = foregroundMode;
            this.internalPlayer.setForegroundMode(foregroundMode);
        }
    }

    @Override // com.google.android.exoplayer2.Player
    public void stop(boolean reset) {
        if (reset) {
            this.mediaSource = null;
        }
        PlaybackInfo playbackInfo = getResetPlaybackInfo(reset, reset, reset, 1);
        this.pendingOperationAcks++;
        this.internalPlayer.stop(reset);
        updatePlaybackInfo(playbackInfo, false, 4, 1, false);
    }

    @Override // com.google.android.exoplayer2.Player
    public void release(boolean async) {
        Log.i(TAG, "Release " + Integer.toHexString(System.identityHashCode(this)) + " [" + ExoPlayerLibraryInfo.VERSION_SLASHY + "] [" + Util.DEVICE_DEBUG_INFO + "] [" + ExoPlayerLibraryInfo.registeredModules() + "]");
        this.mediaSource = null;
        this.internalPlayer.release();
        this.eventHandler.removeCallbacksAndMessages(null);
        this.playbackInfo = getResetPlaybackInfo(false, false, false, 1);
    }

    @Override // com.google.android.exoplayer2.ExoPlayer
    public PlayerMessage createMessage(PlayerMessage.Target target) {
        return new PlayerMessage(this.internalPlayer, target, this.playbackInfo.timeline, getCurrentWindowIndex(), this.internalPlayerHandler);
    }

    @Override // com.google.android.exoplayer2.Player
    public int getCurrentPeriodIndex() {
        if (shouldMaskPosition()) {
            return this.maskingPeriodIndex;
        }
        return this.playbackInfo.timeline.getIndexOfPeriod(this.playbackInfo.periodId.periodUid);
    }

    @Override // com.google.android.exoplayer2.Player
    public int getCurrentWindowIndex() {
        if (shouldMaskPosition()) {
            return this.maskingWindowIndex;
        }
        return this.playbackInfo.timeline.getPeriodByUid(this.playbackInfo.periodId.periodUid, this.period).windowIndex;
    }

    @Override // com.google.android.exoplayer2.Player
    public long getDuration() {
        if (isPlayingAd()) {
            MediaSource.MediaPeriodId periodId = this.playbackInfo.periodId;
            this.playbackInfo.timeline.getPeriodByUid(periodId.periodUid, this.period);
            long adDurationUs = this.period.getAdDurationUs(periodId.adGroupIndex, periodId.adIndexInAdGroup);
            return C.usToMs(adDurationUs);
        }
        return getContentDuration();
    }

    @Override // com.google.android.exoplayer2.Player
    public long getCurrentPosition() {
        if (shouldMaskPosition()) {
            return this.maskingWindowPositionMs;
        }
        if (this.playbackInfo.periodId.isAd()) {
            return C.usToMs(this.playbackInfo.positionUs);
        }
        return periodPositionUsToWindowPositionMs(this.playbackInfo.periodId, this.playbackInfo.positionUs);
    }

    @Override // com.google.android.exoplayer2.Player
    public long getBufferedPosition() {
        if (isPlayingAd()) {
            if (this.playbackInfo.loadingMediaPeriodId.equals(this.playbackInfo.periodId)) {
                return C.usToMs(this.playbackInfo.bufferedPositionUs);
            }
            return getDuration();
        }
        return getContentBufferedPosition();
    }

    @Override // com.google.android.exoplayer2.Player
    public long getTotalBufferedDuration() {
        return C.usToMs(this.playbackInfo.totalBufferedDurationUs);
    }

    @Override // com.google.android.exoplayer2.Player
    public boolean isPlayingAd() {
        return !shouldMaskPosition() && this.playbackInfo.periodId.isAd();
    }

    @Override // com.google.android.exoplayer2.Player
    public int getCurrentAdGroupIndex() {
        if (isPlayingAd()) {
            return this.playbackInfo.periodId.adGroupIndex;
        }
        return -1;
    }

    @Override // com.google.android.exoplayer2.Player
    public int getCurrentAdIndexInAdGroup() {
        if (isPlayingAd()) {
            return this.playbackInfo.periodId.adIndexInAdGroup;
        }
        return -1;
    }

    @Override // com.google.android.exoplayer2.Player
    public long getContentPosition() {
        if (isPlayingAd()) {
            this.playbackInfo.timeline.getPeriodByUid(this.playbackInfo.periodId.periodUid, this.period);
            if (this.playbackInfo.contentPositionUs == C.TIME_UNSET) {
                return this.playbackInfo.timeline.getWindow(getCurrentWindowIndex(), this.window).getDefaultPositionMs();
            }
            return this.period.getPositionInWindowMs() + C.usToMs(this.playbackInfo.contentPositionUs);
        }
        return getCurrentPosition();
    }

    @Override // com.google.android.exoplayer2.Player
    public long getContentBufferedPosition() {
        if (shouldMaskPosition()) {
            return this.maskingWindowPositionMs;
        }
        if (this.playbackInfo.loadingMediaPeriodId.windowSequenceNumber != this.playbackInfo.periodId.windowSequenceNumber) {
            return this.playbackInfo.timeline.getWindow(getCurrentWindowIndex(), this.window).getDurationMs();
        }
        long contentBufferedPositionUs = this.playbackInfo.bufferedPositionUs;
        if (this.playbackInfo.loadingMediaPeriodId.isAd()) {
            Timeline.Period loadingPeriod = this.playbackInfo.timeline.getPeriodByUid(this.playbackInfo.loadingMediaPeriodId.periodUid, this.period);
            contentBufferedPositionUs = loadingPeriod.getAdGroupTimeUs(this.playbackInfo.loadingMediaPeriodId.adGroupIndex);
            if (contentBufferedPositionUs == Long.MIN_VALUE) {
                contentBufferedPositionUs = loadingPeriod.durationUs;
            }
        }
        return periodPositionUsToWindowPositionMs(this.playbackInfo.loadingMediaPeriodId, contentBufferedPositionUs);
    }

    @Override // com.google.android.exoplayer2.Player
    public int getRendererCount() {
        return this.renderers.length;
    }

    @Override // com.google.android.exoplayer2.Player
    public int getRendererType(int index) {
        return this.renderers[index].getTrackType();
    }

    @Override // com.google.android.exoplayer2.Player
    public TrackGroupArray getCurrentTrackGroups() {
        return this.playbackInfo.trackGroups;
    }

    @Override // com.google.android.exoplayer2.Player
    public TrackSelectionArray getCurrentTrackSelections() {
        return this.playbackInfo.trackSelectorResult.selections;
    }

    @Override // com.google.android.exoplayer2.Player
    public Timeline getCurrentTimeline() {
        return this.playbackInfo.timeline;
    }

    void handleEvent(Message msg) {
        boolean z = true;
        switch (msg.what) {
            case 0:
                PlaybackInfo playbackInfo = (PlaybackInfo) msg.obj;
                int i = msg.arg1;
                if (msg.arg2 == -1) {
                    z = false;
                }
                handlePlaybackInfo(playbackInfo, i, z, msg.arg2);
                return;
            case 1:
                PlaybackParameters playbackParameters = (PlaybackParameters) msg.obj;
                if (msg.arg1 == 0) {
                    z = false;
                }
                handlePlaybackParameters(playbackParameters, z);
                return;
            default:
                throw new IllegalStateException();
        }
    }

    private void handlePlaybackParameters(final PlaybackParameters playbackParameters, boolean operationAck) {
        if (operationAck) {
            this.pendingSetPlaybackParametersAcks--;
        }
        if (this.pendingSetPlaybackParametersAcks == 0 && !this.playbackParameters.equals(playbackParameters)) {
            this.playbackParameters = playbackParameters;
            notifyListeners(new BasePlayer.ListenerInvocation() { // from class: com.google.android.exoplayer2.ExoPlayerImpl$$ExternalSyntheticLambda1
                @Override // com.google.android.exoplayer2.BasePlayer.ListenerInvocation
                public final void invokeListener(Player.EventListener eventListener) {
                    eventListener.onPlaybackParametersChanged(PlaybackParameters.this);
                }
            });
        }
    }

    private void handlePlaybackInfo(PlaybackInfo playbackInfo, int operationAcks, boolean positionDiscontinuity, int positionDiscontinuityReason) {
        PlaybackInfo playbackInfo2;
        int i = this.pendingOperationAcks - operationAcks;
        this.pendingOperationAcks = i;
        if (i == 0) {
            if (playbackInfo.startPositionUs != C.TIME_UNSET) {
                playbackInfo2 = playbackInfo;
            } else {
                playbackInfo2 = playbackInfo.copyWithNewPosition(playbackInfo.periodId, 0L, playbackInfo.contentPositionUs, playbackInfo.totalBufferedDurationUs);
            }
            if (!this.playbackInfo.timeline.isEmpty() && playbackInfo2.timeline.isEmpty()) {
                this.maskingPeriodIndex = 0;
                this.maskingWindowIndex = 0;
                this.maskingWindowPositionMs = 0L;
            }
            int timelineChangeReason = this.hasPendingPrepare ? 0 : 2;
            boolean seekProcessed = this.hasPendingSeek;
            this.hasPendingPrepare = false;
            this.hasPendingSeek = false;
            updatePlaybackInfo(playbackInfo2, positionDiscontinuity, positionDiscontinuityReason, timelineChangeReason, seekProcessed);
        }
    }

    private PlaybackInfo getResetPlaybackInfo(boolean resetPosition, boolean resetState, boolean resetError, int playbackState) {
        MediaSource.MediaPeriodId mediaPeriodId;
        long j = 0;
        boolean resetPosition2 = false;
        if (!resetPosition) {
            this.maskingWindowIndex = getCurrentWindowIndex();
            this.maskingPeriodIndex = getCurrentPeriodIndex();
            this.maskingWindowPositionMs = getCurrentPosition();
        } else {
            this.maskingWindowIndex = 0;
            this.maskingPeriodIndex = 0;
            this.maskingWindowPositionMs = 0L;
        }
        if (resetPosition || resetState) {
            resetPosition2 = true;
        }
        if (resetPosition2) {
            mediaPeriodId = this.playbackInfo.getDummyFirstMediaPeriodId(this.shuffleModeEnabled, this.window, this.period);
        } else {
            mediaPeriodId = this.playbackInfo.periodId;
        }
        if (!resetPosition2) {
            j = this.playbackInfo.positionUs;
        }
        long startPositionUs = j;
        long contentPositionUs = resetPosition2 ? C.TIME_UNSET : this.playbackInfo.contentPositionUs;
        return new PlaybackInfo(resetState ? Timeline.EMPTY : this.playbackInfo.timeline, mediaPeriodId, startPositionUs, contentPositionUs, playbackState, resetError ? null : this.playbackInfo.playbackError, false, resetState ? TrackGroupArray.EMPTY : this.playbackInfo.trackGroups, resetState ? this.emptyTrackSelectorResult : this.playbackInfo.trackSelectorResult, mediaPeriodId, startPositionUs, 0L, startPositionUs);
    }

    private void updatePlaybackInfo(PlaybackInfo playbackInfo, boolean positionDiscontinuity, int positionDiscontinuityReason, int timelineChangeReason, boolean seekProcessed) {
        boolean previousIsPlaying = isPlaying();
        PlaybackInfo previousPlaybackInfo = this.playbackInfo;
        this.playbackInfo = playbackInfo;
        boolean isPlaying = isPlaying();
        notifyListeners(new PlaybackInfoUpdate(playbackInfo, previousPlaybackInfo, this.listeners, this.trackSelector, positionDiscontinuity, positionDiscontinuityReason, timelineChangeReason, seekProcessed, this.playWhenReady, previousIsPlaying != isPlaying));
    }

    private void notifyListeners(final BasePlayer.ListenerInvocation listenerInvocation) {
        final CopyOnWriteArrayList<BasePlayer.ListenerHolder> listenerSnapshot = new CopyOnWriteArrayList<>(this.listeners);
        notifyListeners(new Runnable() { // from class: com.google.android.exoplayer2.ExoPlayerImpl$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                ExoPlayerImpl.invokeAll(listenerSnapshot, listenerInvocation);
            }
        });
    }

    private void notifyListeners(Runnable listenerNotificationRunnable) {
        boolean isRunningRecursiveListenerNotification = !this.pendingListenerNotifications.isEmpty();
        this.pendingListenerNotifications.addLast(listenerNotificationRunnable);
        if (isRunningRecursiveListenerNotification) {
            return;
        }
        while (!this.pendingListenerNotifications.isEmpty()) {
            this.pendingListenerNotifications.peekFirst().run();
            this.pendingListenerNotifications.removeFirst();
        }
    }

    private long periodPositionUsToWindowPositionMs(MediaSource.MediaPeriodId periodId, long positionUs) {
        long positionMs = C.usToMs(positionUs);
        this.playbackInfo.timeline.getPeriodByUid(periodId.periodUid, this.period);
        return positionMs + this.period.getPositionInWindowMs();
    }

    private boolean shouldMaskPosition() {
        return this.playbackInfo.timeline.isEmpty() || this.pendingOperationAcks > 0;
    }

    /* loaded from: classes3.dex */
    public static final class PlaybackInfoUpdate implements Runnable {
        private final boolean isLoadingChanged;
        private final boolean isPlayingChanged;
        private final CopyOnWriteArrayList<BasePlayer.ListenerHolder> listenerSnapshot;
        private final boolean playWhenReady;
        private final boolean playbackErrorChanged;
        private final PlaybackInfo playbackInfo;
        private final boolean playbackStateChanged;
        private final boolean positionDiscontinuity;
        private final int positionDiscontinuityReason;
        private final boolean seekProcessed;
        private final int timelineChangeReason;
        private final boolean timelineChanged;
        private final TrackSelector trackSelector;
        private final boolean trackSelectorResultChanged;

        public PlaybackInfoUpdate(PlaybackInfo playbackInfo, PlaybackInfo previousPlaybackInfo, CopyOnWriteArrayList<BasePlayer.ListenerHolder> listeners, TrackSelector trackSelector, boolean positionDiscontinuity, int positionDiscontinuityReason, int timelineChangeReason, boolean seekProcessed, boolean playWhenReady, boolean isPlayingChanged) {
            this.playbackInfo = playbackInfo;
            this.listenerSnapshot = new CopyOnWriteArrayList<>(listeners);
            this.trackSelector = trackSelector;
            this.positionDiscontinuity = positionDiscontinuity;
            this.positionDiscontinuityReason = positionDiscontinuityReason;
            this.timelineChangeReason = timelineChangeReason;
            this.seekProcessed = seekProcessed;
            this.playWhenReady = playWhenReady;
            this.isPlayingChanged = isPlayingChanged;
            boolean z = true;
            this.playbackStateChanged = previousPlaybackInfo.playbackState != playbackInfo.playbackState;
            this.playbackErrorChanged = (previousPlaybackInfo.playbackError == playbackInfo.playbackError || playbackInfo.playbackError == null) ? false : true;
            this.timelineChanged = previousPlaybackInfo.timeline != playbackInfo.timeline;
            this.isLoadingChanged = previousPlaybackInfo.isLoading != playbackInfo.isLoading;
            this.trackSelectorResultChanged = previousPlaybackInfo.trackSelectorResult == playbackInfo.trackSelectorResult ? false : z;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.timelineChanged || this.timelineChangeReason == 0) {
                ExoPlayerImpl.invokeAll(this.listenerSnapshot, new BasePlayer.ListenerInvocation() { // from class: com.google.android.exoplayer2.ExoPlayerImpl$PlaybackInfoUpdate$$ExternalSyntheticLambda0
                    @Override // com.google.android.exoplayer2.BasePlayer.ListenerInvocation
                    public final void invokeListener(Player.EventListener eventListener) {
                        ExoPlayerImpl.PlaybackInfoUpdate.this.m31xd48a2375(eventListener);
                    }
                });
            }
            if (this.positionDiscontinuity) {
                ExoPlayerImpl.invokeAll(this.listenerSnapshot, new BasePlayer.ListenerInvocation() { // from class: com.google.android.exoplayer2.ExoPlayerImpl$PlaybackInfoUpdate$$ExternalSyntheticLambda1
                    @Override // com.google.android.exoplayer2.BasePlayer.ListenerInvocation
                    public final void invokeListener(Player.EventListener eventListener) {
                        ExoPlayerImpl.PlaybackInfoUpdate.this.m32x52eb2754(eventListener);
                    }
                });
            }
            if (this.playbackErrorChanged) {
                ExoPlayerImpl.invokeAll(this.listenerSnapshot, new BasePlayer.ListenerInvocation() { // from class: com.google.android.exoplayer2.ExoPlayerImpl$PlaybackInfoUpdate$$ExternalSyntheticLambda2
                    @Override // com.google.android.exoplayer2.BasePlayer.ListenerInvocation
                    public final void invokeListener(Player.EventListener eventListener) {
                        ExoPlayerImpl.PlaybackInfoUpdate.this.m33xd14c2b33(eventListener);
                    }
                });
            }
            if (this.trackSelectorResultChanged) {
                this.trackSelector.onSelectionActivated(this.playbackInfo.trackSelectorResult.info);
                ExoPlayerImpl.invokeAll(this.listenerSnapshot, new BasePlayer.ListenerInvocation() { // from class: com.google.android.exoplayer2.ExoPlayerImpl$PlaybackInfoUpdate$$ExternalSyntheticLambda3
                    @Override // com.google.android.exoplayer2.BasePlayer.ListenerInvocation
                    public final void invokeListener(Player.EventListener eventListener) {
                        ExoPlayerImpl.PlaybackInfoUpdate.this.m34x4fad2f12(eventListener);
                    }
                });
            }
            if (this.isLoadingChanged) {
                ExoPlayerImpl.invokeAll(this.listenerSnapshot, new BasePlayer.ListenerInvocation() { // from class: com.google.android.exoplayer2.ExoPlayerImpl$PlaybackInfoUpdate$$ExternalSyntheticLambda4
                    @Override // com.google.android.exoplayer2.BasePlayer.ListenerInvocation
                    public final void invokeListener(Player.EventListener eventListener) {
                        ExoPlayerImpl.PlaybackInfoUpdate.this.m35xce0e32f1(eventListener);
                    }
                });
            }
            if (this.playbackStateChanged) {
                ExoPlayerImpl.invokeAll(this.listenerSnapshot, new BasePlayer.ListenerInvocation() { // from class: com.google.android.exoplayer2.ExoPlayerImpl$PlaybackInfoUpdate$$ExternalSyntheticLambda5
                    @Override // com.google.android.exoplayer2.BasePlayer.ListenerInvocation
                    public final void invokeListener(Player.EventListener eventListener) {
                        ExoPlayerImpl.PlaybackInfoUpdate.this.m36x4c6f36d0(eventListener);
                    }
                });
            }
            if (this.isPlayingChanged) {
                ExoPlayerImpl.invokeAll(this.listenerSnapshot, new BasePlayer.ListenerInvocation() { // from class: com.google.android.exoplayer2.ExoPlayerImpl$PlaybackInfoUpdate$$ExternalSyntheticLambda6
                    @Override // com.google.android.exoplayer2.BasePlayer.ListenerInvocation
                    public final void invokeListener(Player.EventListener eventListener) {
                        ExoPlayerImpl.PlaybackInfoUpdate.this.m37xcad03aaf(eventListener);
                    }
                });
            }
            if (this.seekProcessed) {
                ExoPlayerImpl.invokeAll(this.listenerSnapshot, ExoPlayerImpl$PlaybackInfoUpdate$$ExternalSyntheticLambda7.INSTANCE);
            }
        }

        /* renamed from: lambda$run$0$com-google-android-exoplayer2-ExoPlayerImpl$PlaybackInfoUpdate */
        public /* synthetic */ void m31xd48a2375(Player.EventListener listener) {
            listener.onTimelineChanged(this.playbackInfo.timeline, this.timelineChangeReason);
        }

        /* renamed from: lambda$run$1$com-google-android-exoplayer2-ExoPlayerImpl$PlaybackInfoUpdate */
        public /* synthetic */ void m32x52eb2754(Player.EventListener listener) {
            listener.onPositionDiscontinuity(this.positionDiscontinuityReason);
        }

        /* renamed from: lambda$run$2$com-google-android-exoplayer2-ExoPlayerImpl$PlaybackInfoUpdate */
        public /* synthetic */ void m33xd14c2b33(Player.EventListener listener) {
            listener.onPlayerError(this.playbackInfo.playbackError);
        }

        /* renamed from: lambda$run$3$com-google-android-exoplayer2-ExoPlayerImpl$PlaybackInfoUpdate */
        public /* synthetic */ void m34x4fad2f12(Player.EventListener listener) {
            listener.onTracksChanged(this.playbackInfo.trackGroups, this.playbackInfo.trackSelectorResult.selections);
        }

        /* renamed from: lambda$run$4$com-google-android-exoplayer2-ExoPlayerImpl$PlaybackInfoUpdate */
        public /* synthetic */ void m35xce0e32f1(Player.EventListener listener) {
            listener.onLoadingChanged(this.playbackInfo.isLoading);
        }

        /* renamed from: lambda$run$5$com-google-android-exoplayer2-ExoPlayerImpl$PlaybackInfoUpdate */
        public /* synthetic */ void m36x4c6f36d0(Player.EventListener listener) {
            listener.onPlayerStateChanged(this.playWhenReady, this.playbackInfo.playbackState);
        }

        /* renamed from: lambda$run$6$com-google-android-exoplayer2-ExoPlayerImpl$PlaybackInfoUpdate */
        public /* synthetic */ void m37xcad03aaf(Player.EventListener listener) {
            listener.onIsPlayingChanged(this.playbackInfo.playbackState == 3);
        }
    }

    public static void invokeAll(CopyOnWriteArrayList<BasePlayer.ListenerHolder> listeners, BasePlayer.ListenerInvocation listenerInvocation) {
        Iterator<BasePlayer.ListenerHolder> it = listeners.iterator();
        while (it.hasNext()) {
            BasePlayer.ListenerHolder listenerHolder = it.next();
            listenerHolder.invoke(listenerInvocation);
        }
    }
}
