package com.google.android.exoplayer2;

import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
/* loaded from: classes.dex */
public interface Player {
    long getContentPosition();

    int getCurrentAdGroupIndex();

    int getCurrentAdIndexInAdGroup();

    long getCurrentPosition();

    Timeline getCurrentTimeline();

    int getCurrentWindowIndex();

    boolean getPlayWhenReady();

    int getPlaybackState();

    int getPlaybackSuppressionReason();

    long getTotalBufferedDuration();

    void seekTo(int i, long j);

    /* loaded from: classes.dex */
    public interface EventListener {
        void onIsPlayingChanged(boolean z);

        void onLoadingChanged(boolean z);

        void onPlaybackParametersChanged(PlaybackParameters playbackParameters);

        void onPlaybackSuppressionReasonChanged(int i);

        void onPlayerError(ExoPlaybackException exoPlaybackException);

        void onPlayerStateChanged(boolean z, int i);

        void onPositionDiscontinuity(int i);

        void onRepeatModeChanged(int i);

        void onSeekProcessed();

        void onTimelineChanged(Timeline timeline, int i);

        @Deprecated
        void onTimelineChanged(Timeline timeline, Object obj, int i);

        void onTracksChanged(TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray);

        /* renamed from: com.google.android.exoplayer2.Player$EventListener$-CC */
        /* loaded from: classes.dex */
        public final /* synthetic */ class CC {
            public static void $default$onIsPlayingChanged(EventListener eventListener, boolean z) {
            }

            public static void $default$onPlaybackParametersChanged(EventListener eventListener, PlaybackParameters playbackParameters) {
            }

            public static void $default$onPlaybackSuppressionReasonChanged(EventListener eventListener, int i) {
            }

            public static void $default$onPlayerError(EventListener eventListener, ExoPlaybackException exoPlaybackException) {
            }

            public static void $default$onPositionDiscontinuity(EventListener eventListener, int i) {
            }

            public static void $default$onRepeatModeChanged(EventListener eventListener, int i) {
            }

            public static void $default$onSeekProcessed(EventListener eventListener) {
            }

            @Deprecated
            public static void $default$onTimelineChanged(EventListener eventListener, Timeline timeline, Object obj, int i) {
            }

            public static void $default$onTracksChanged(EventListener eventListener, TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray) {
            }
        }
    }
}
