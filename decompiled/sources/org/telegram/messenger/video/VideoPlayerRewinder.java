package org.telegram.messenger.video;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.VideoPlayer;
/* loaded from: classes4.dex */
public class VideoPlayerRewinder {
    private long rewindBackSeekPlayerPosition;
    public boolean rewindByBackSeek;
    public int rewindCount;
    private boolean rewindForward;
    private long rewindLastTime;
    private long rewindLastUpdatePlayerTime;
    private long startRewindFrom;
    private Runnable updateRewindRunnable;
    private VideoPlayer videoPlayer;
    private float playSpeed = 1.0f;
    private final Runnable backSeek = new Runnable() { // from class: org.telegram.messenger.video.VideoPlayerRewinder.1
        @Override // java.lang.Runnable
        public void run() {
            long dt;
            if (VideoPlayerRewinder.this.videoPlayer != null) {
                long duration = VideoPlayerRewinder.this.videoPlayer.getDuration();
                if (duration == 0 || duration == C.TIME_UNSET) {
                    VideoPlayerRewinder.this.rewindLastTime = System.currentTimeMillis();
                    return;
                }
                long t = System.currentTimeMillis();
                long dt2 = t - VideoPlayerRewinder.this.rewindLastTime;
                VideoPlayerRewinder.this.rewindLastTime = t;
                if (VideoPlayerRewinder.this.rewindCount == 1) {
                    dt = dt2 * 3;
                } else if (VideoPlayerRewinder.this.rewindCount == 2) {
                    dt = dt2 * 6;
                } else {
                    dt = dt2 * 12;
                }
                if (VideoPlayerRewinder.this.rewindForward) {
                    VideoPlayerRewinder.access$314(VideoPlayerRewinder.this, dt);
                } else {
                    VideoPlayerRewinder.access$322(VideoPlayerRewinder.this, dt);
                }
                if (VideoPlayerRewinder.this.rewindBackSeekPlayerPosition < 0) {
                    VideoPlayerRewinder.this.rewindBackSeekPlayerPosition = 0L;
                } else if (VideoPlayerRewinder.this.rewindBackSeekPlayerPosition > duration) {
                    VideoPlayerRewinder.this.rewindBackSeekPlayerPosition = duration;
                }
                if (VideoPlayerRewinder.this.rewindByBackSeek && VideoPlayerRewinder.this.videoPlayer != null && VideoPlayerRewinder.this.rewindLastTime - VideoPlayerRewinder.this.rewindLastUpdatePlayerTime > 350) {
                    VideoPlayerRewinder videoPlayerRewinder = VideoPlayerRewinder.this;
                    videoPlayerRewinder.rewindLastUpdatePlayerTime = videoPlayerRewinder.rewindLastTime;
                    VideoPlayerRewinder.this.videoPlayer.seekTo(VideoPlayerRewinder.this.rewindBackSeekPlayerPosition);
                }
                if (VideoPlayerRewinder.this.videoPlayer != null) {
                    long timeDiff = VideoPlayerRewinder.this.rewindBackSeekPlayerPosition - VideoPlayerRewinder.this.startRewindFrom;
                    float progress = ((float) VideoPlayerRewinder.this.rewindBackSeekPlayerPosition) / ((float) VideoPlayerRewinder.this.videoPlayer.getDuration());
                    VideoPlayerRewinder videoPlayerRewinder2 = VideoPlayerRewinder.this;
                    videoPlayerRewinder2.updateRewindProgressUi(timeDiff, progress, videoPlayerRewinder2.rewindByBackSeek);
                }
                if (VideoPlayerRewinder.this.rewindBackSeekPlayerPosition == 0 || VideoPlayerRewinder.this.rewindBackSeekPlayerPosition >= duration) {
                    if (VideoPlayerRewinder.this.rewindByBackSeek && VideoPlayerRewinder.this.videoPlayer != null) {
                        VideoPlayerRewinder videoPlayerRewinder3 = VideoPlayerRewinder.this;
                        videoPlayerRewinder3.rewindLastUpdatePlayerTime = videoPlayerRewinder3.rewindLastTime;
                        VideoPlayerRewinder.this.videoPlayer.seekTo(VideoPlayerRewinder.this.rewindBackSeekPlayerPosition);
                    }
                    VideoPlayerRewinder.this.cancelRewind();
                }
                if (VideoPlayerRewinder.this.rewindCount > 0) {
                    AndroidUtilities.runOnUIThread(VideoPlayerRewinder.this.backSeek, 16L);
                }
            }
        }
    };

    static /* synthetic */ long access$314(VideoPlayerRewinder x0, long x1) {
        long j = x0.rewindBackSeekPlayerPosition + x1;
        x0.rewindBackSeekPlayerPosition = j;
        return j;
    }

    static /* synthetic */ long access$322(VideoPlayerRewinder x0, long x1) {
        long j = x0.rewindBackSeekPlayerPosition - x1;
        x0.rewindBackSeekPlayerPosition = j;
        return j;
    }

    public void startRewind(VideoPlayer videoPlayer, boolean forward, float playbackSpeed) {
        this.videoPlayer = videoPlayer;
        this.playSpeed = playbackSpeed;
        this.rewindForward = forward;
        cancelRewind();
        incrementRewindCount();
    }

    public void cancelRewind() {
        if (this.rewindCount != 0) {
            this.rewindCount = 0;
            VideoPlayer videoPlayer = this.videoPlayer;
            if (videoPlayer != null) {
                if (this.rewindByBackSeek) {
                    videoPlayer.seekTo(this.rewindBackSeekPlayerPosition);
                } else {
                    long current = videoPlayer.getCurrentPosition();
                    this.videoPlayer.seekTo(current);
                }
                this.videoPlayer.setPlaybackSpeed(this.playSpeed);
            }
        }
        AndroidUtilities.cancelRunOnUIThread(this.backSeek);
        Runnable runnable = this.updateRewindRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.updateRewindRunnable = null;
        }
        onRewindCanceled();
    }

    private void incrementRewindCount() {
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer == null) {
            return;
        }
        int i = this.rewindCount + 1;
        this.rewindCount = i;
        boolean needUpdate = false;
        if (i == 1) {
            if (this.rewindForward && videoPlayer.isPlaying()) {
                this.rewindByBackSeek = false;
            } else {
                this.rewindByBackSeek = true;
            }
        }
        if (this.rewindForward && !this.rewindByBackSeek) {
            int i2 = this.rewindCount;
            if (i2 == 1) {
                this.videoPlayer.setPlaybackSpeed(4.0f);
                needUpdate = true;
            } else if (i2 == 2) {
                this.videoPlayer.setPlaybackSpeed(7.0f);
                needUpdate = true;
            } else {
                this.videoPlayer.setPlaybackSpeed(13.0f);
            }
        } else {
            int i3 = this.rewindCount;
            if (i3 == 1 || i3 == 2) {
                needUpdate = true;
            }
        }
        if (this.rewindCount == 1) {
            this.rewindBackSeekPlayerPosition = this.videoPlayer.getCurrentPosition();
            long currentTimeMillis = System.currentTimeMillis();
            this.rewindLastTime = currentTimeMillis;
            this.rewindLastUpdatePlayerTime = currentTimeMillis;
            this.startRewindFrom = this.videoPlayer.getCurrentPosition();
            onRewindStart(this.rewindForward);
        }
        AndroidUtilities.cancelRunOnUIThread(this.backSeek);
        AndroidUtilities.runOnUIThread(this.backSeek);
        if (needUpdate) {
            Runnable runnable = this.updateRewindRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            Runnable runnable2 = new Runnable() { // from class: org.telegram.messenger.video.VideoPlayerRewinder$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    VideoPlayerRewinder.this.m1265xfe904fcb();
                }
            };
            this.updateRewindRunnable = runnable2;
            AndroidUtilities.runOnUIThread(runnable2, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        }
    }

    /* renamed from: lambda$incrementRewindCount$0$org-telegram-messenger-video-VideoPlayerRewinder */
    public /* synthetic */ void m1265xfe904fcb() {
        this.updateRewindRunnable = null;
        incrementRewindCount();
    }

    protected void updateRewindProgressUi(long timeDiff, float progress, boolean rewindByBackSeek) {
    }

    protected void onRewindStart(boolean rewindForward) {
    }

    protected void onRewindCanceled() {
    }

    public float getVideoProgress() {
        return ((float) this.rewindBackSeekPlayerPosition) / ((float) this.videoPlayer.getDuration());
    }
}
