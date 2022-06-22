package org.telegram.messenger.video;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.VideoPlayer;
/* loaded from: classes.dex */
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
            if (VideoPlayerRewinder.this.videoPlayer == null) {
                return;
            }
            long duration = VideoPlayerRewinder.this.videoPlayer.getDuration();
            if (duration == 0 || duration == -9223372036854775807L) {
                VideoPlayerRewinder.this.rewindLastTime = System.currentTimeMillis();
                return;
            }
            long currentTimeMillis = System.currentTimeMillis();
            long j = currentTimeMillis - VideoPlayerRewinder.this.rewindLastTime;
            VideoPlayerRewinder.this.rewindLastTime = currentTimeMillis;
            VideoPlayerRewinder videoPlayerRewinder = VideoPlayerRewinder.this;
            int i = videoPlayerRewinder.rewindCount;
            long j2 = j * (i == 1 ? 3L : i == 2 ? 6L : 12L);
            if (videoPlayerRewinder.rewindForward) {
                VideoPlayerRewinder.access$314(VideoPlayerRewinder.this, j2);
            } else {
                VideoPlayerRewinder.access$322(VideoPlayerRewinder.this, j2);
            }
            if (VideoPlayerRewinder.this.rewindBackSeekPlayerPosition < 0) {
                VideoPlayerRewinder.this.rewindBackSeekPlayerPosition = 0L;
            } else if (VideoPlayerRewinder.this.rewindBackSeekPlayerPosition > duration) {
                VideoPlayerRewinder.this.rewindBackSeekPlayerPosition = duration;
            }
            VideoPlayerRewinder videoPlayerRewinder2 = VideoPlayerRewinder.this;
            if (videoPlayerRewinder2.rewindByBackSeek && videoPlayerRewinder2.videoPlayer != null && VideoPlayerRewinder.this.rewindLastTime - VideoPlayerRewinder.this.rewindLastUpdatePlayerTime > 350) {
                VideoPlayerRewinder videoPlayerRewinder3 = VideoPlayerRewinder.this;
                videoPlayerRewinder3.rewindLastUpdatePlayerTime = videoPlayerRewinder3.rewindLastTime;
                VideoPlayerRewinder.this.videoPlayer.seekTo(VideoPlayerRewinder.this.rewindBackSeekPlayerPosition);
            }
            if (VideoPlayerRewinder.this.videoPlayer != null) {
                long j3 = VideoPlayerRewinder.this.rewindBackSeekPlayerPosition - VideoPlayerRewinder.this.startRewindFrom;
                float duration2 = ((float) VideoPlayerRewinder.this.rewindBackSeekPlayerPosition) / ((float) VideoPlayerRewinder.this.videoPlayer.getDuration());
                VideoPlayerRewinder videoPlayerRewinder4 = VideoPlayerRewinder.this;
                videoPlayerRewinder4.updateRewindProgressUi(j3, duration2, videoPlayerRewinder4.rewindByBackSeek);
            }
            if (VideoPlayerRewinder.this.rewindBackSeekPlayerPosition == 0 || VideoPlayerRewinder.this.rewindBackSeekPlayerPosition >= duration) {
                VideoPlayerRewinder videoPlayerRewinder5 = VideoPlayerRewinder.this;
                if (videoPlayerRewinder5.rewindByBackSeek && videoPlayerRewinder5.videoPlayer != null) {
                    VideoPlayerRewinder videoPlayerRewinder6 = VideoPlayerRewinder.this;
                    videoPlayerRewinder6.rewindLastUpdatePlayerTime = videoPlayerRewinder6.rewindLastTime;
                    VideoPlayerRewinder.this.videoPlayer.seekTo(VideoPlayerRewinder.this.rewindBackSeekPlayerPosition);
                }
                VideoPlayerRewinder.this.cancelRewind();
            }
            VideoPlayerRewinder videoPlayerRewinder7 = VideoPlayerRewinder.this;
            if (videoPlayerRewinder7.rewindCount <= 0) {
                return;
            }
            AndroidUtilities.runOnUIThread(videoPlayerRewinder7.backSeek, 16L);
        }
    };

    protected void onRewindCanceled() {
    }

    protected void onRewindStart(boolean z) {
    }

    protected void updateRewindProgressUi(long j, float f, boolean z) {
    }

    static /* synthetic */ long access$314(VideoPlayerRewinder videoPlayerRewinder, long j) {
        long j2 = videoPlayerRewinder.rewindBackSeekPlayerPosition + j;
        videoPlayerRewinder.rewindBackSeekPlayerPosition = j2;
        return j2;
    }

    static /* synthetic */ long access$322(VideoPlayerRewinder videoPlayerRewinder, long j) {
        long j2 = videoPlayerRewinder.rewindBackSeekPlayerPosition - j;
        videoPlayerRewinder.rewindBackSeekPlayerPosition = j2;
        return j2;
    }

    public void startRewind(VideoPlayer videoPlayer, boolean z, float f) {
        this.videoPlayer = videoPlayer;
        this.playSpeed = f;
        this.rewindForward = z;
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
                    this.videoPlayer.seekTo(videoPlayer.getCurrentPosition());
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

    /* JADX WARN: Code restructure failed: missing block: B:25:0x0048, code lost:
        if (r0 != 2) goto L27;
     */
    /* JADX WARN: Removed duplicated region for block: B:29:0x004f  */
    /* JADX WARN: Removed duplicated region for block: B:32:0x0078  */
    /* JADX WARN: Removed duplicated region for block: B:37:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void incrementRewindCount() {
        /*
            r4 = this;
            org.telegram.ui.Components.VideoPlayer r0 = r4.videoPlayer
            if (r0 != 0) goto L5
            return
        L5:
            int r1 = r4.rewindCount
            r2 = 1
            int r1 = r1 + r2
            r4.rewindCount = r1
            r3 = 0
            if (r1 != r2) goto L1d
            boolean r1 = r4.rewindForward
            if (r1 == 0) goto L1b
            boolean r0 = r0.isPlaying()
            if (r0 == 0) goto L1b
            r4.rewindByBackSeek = r3
            goto L1d
        L1b:
            r4.rewindByBackSeek = r2
        L1d:
            boolean r0 = r4.rewindForward
            r1 = 2
            if (r0 == 0) goto L44
            boolean r0 = r4.rewindByBackSeek
            if (r0 != 0) goto L44
            int r0 = r4.rewindCount
            if (r0 != r2) goto L32
            org.telegram.ui.Components.VideoPlayer r0 = r4.videoPlayer
            r1 = 1082130432(0x40800000, float:4.0)
            r0.setPlaybackSpeed(r1)
            goto L4a
        L32:
            if (r0 != r1) goto L3c
            org.telegram.ui.Components.VideoPlayer r0 = r4.videoPlayer
            r1 = 1088421888(0x40e00000, float:7.0)
            r0.setPlaybackSpeed(r1)
            goto L4a
        L3c:
            org.telegram.ui.Components.VideoPlayer r0 = r4.videoPlayer
            r1 = 1095761920(0x41500000, float:13.0)
            r0.setPlaybackSpeed(r1)
            goto L4b
        L44:
            int r0 = r4.rewindCount
            if (r0 == r2) goto L4a
            if (r0 != r1) goto L4b
        L4a:
            r3 = 1
        L4b:
            int r0 = r4.rewindCount
            if (r0 != r2) goto L6c
            org.telegram.ui.Components.VideoPlayer r0 = r4.videoPlayer
            long r0 = r0.getCurrentPosition()
            r4.rewindBackSeekPlayerPosition = r0
            long r0 = java.lang.System.currentTimeMillis()
            r4.rewindLastTime = r0
            r4.rewindLastUpdatePlayerTime = r0
            org.telegram.ui.Components.VideoPlayer r0 = r4.videoPlayer
            long r0 = r0.getCurrentPosition()
            r4.startRewindFrom = r0
            boolean r0 = r4.rewindForward
            r4.onRewindStart(r0)
        L6c:
            java.lang.Runnable r0 = r4.backSeek
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
            java.lang.Runnable r0 = r4.backSeek
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            if (r3 == 0) goto L8b
            java.lang.Runnable r0 = r4.updateRewindRunnable
            if (r0 == 0) goto L7f
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
        L7f:
            org.telegram.messenger.video.VideoPlayerRewinder$$ExternalSyntheticLambda0 r0 = new org.telegram.messenger.video.VideoPlayerRewinder$$ExternalSyntheticLambda0
            r0.<init>()
            r4.updateRewindRunnable = r0
            r1 = 2000(0x7d0, double:9.88E-321)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0, r1)
        L8b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.video.VideoPlayerRewinder.incrementRewindCount():void");
    }

    public /* synthetic */ void lambda$incrementRewindCount$0() {
        this.updateRewindRunnable = null;
        incrementRewindCount();
    }

    public float getVideoProgress() {
        return ((float) this.rewindBackSeekPlayerPosition) / ((float) this.videoPlayer.getDuration());
    }
}
