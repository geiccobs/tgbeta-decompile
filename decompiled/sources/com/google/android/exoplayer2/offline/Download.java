package com.google.android.exoplayer2.offline;

import com.google.android.exoplayer2.util.Assertions;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes3.dex */
public final class Download {
    public static final int FAILURE_REASON_NONE = 0;
    public static final int FAILURE_REASON_UNKNOWN = 1;
    public static final int STATE_COMPLETED = 3;
    public static final int STATE_DOWNLOADING = 2;
    public static final int STATE_FAILED = 4;
    public static final int STATE_QUEUED = 0;
    public static final int STATE_REMOVING = 5;
    public static final int STATE_RESTARTING = 7;
    public static final int STATE_STOPPED = 1;
    public static final int STOP_REASON_NONE = 0;
    public final long contentLength;
    public final int failureReason;
    final DownloadProgress progress;
    public final DownloadRequest request;
    public final long startTimeMs;
    public final int state;
    public final int stopReason;
    public final long updateTimeMs;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface FailureReason {
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface State {
    }

    public Download(DownloadRequest request, int state, long startTimeMs, long updateTimeMs, long contentLength, int stopReason, int failureReason) {
        this(request, state, startTimeMs, updateTimeMs, contentLength, stopReason, failureReason, new DownloadProgress());
    }

    public Download(DownloadRequest request, int state, long startTimeMs, long updateTimeMs, long contentLength, int stopReason, int failureReason, DownloadProgress progress) {
        Assertions.checkNotNull(progress);
        boolean z = true;
        Assertions.checkArgument((failureReason == 0) == (state != 4));
        if (stopReason != 0) {
            Assertions.checkArgument((state == 2 || state == 0) ? false : z);
        }
        this.request = request;
        this.state = state;
        this.startTimeMs = startTimeMs;
        this.updateTimeMs = updateTimeMs;
        this.contentLength = contentLength;
        this.stopReason = stopReason;
        this.failureReason = failureReason;
        this.progress = progress;
    }

    public boolean isTerminalState() {
        int i = this.state;
        return i == 3 || i == 4;
    }

    public long getBytesDownloaded() {
        return this.progress.bytesDownloaded;
    }

    public float getPercentDownloaded() {
        return this.progress.percentDownloaded;
    }
}
