package com.google.android.exoplayer2.upstream;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.Loader;
import java.io.FileNotFoundException;
import java.io.IOException;
/* loaded from: classes3.dex */
public class DefaultLoadErrorHandlingPolicy implements LoadErrorHandlingPolicy {
    private static final int DEFAULT_BEHAVIOR_MIN_LOADABLE_RETRY_COUNT = -1;
    public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT = 3;
    public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT_PROGRESSIVE_LIVE = 6;
    public static final long DEFAULT_TRACK_BLACKLIST_MS = 60000;
    private final int minimumLoadableRetryCount;

    public DefaultLoadErrorHandlingPolicy() {
        this(-1);
    }

    public DefaultLoadErrorHandlingPolicy(int minimumLoadableRetryCount) {
        this.minimumLoadableRetryCount = minimumLoadableRetryCount;
    }

    @Override // com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy
    public long getBlacklistDurationMsFor(int dataType, long loadDurationMs, IOException exception, int errorCount) {
        if (exception instanceof HttpDataSource.InvalidResponseCodeException) {
            int responseCode = ((HttpDataSource.InvalidResponseCodeException) exception).responseCode;
            return (responseCode == 404 || responseCode == 410 || responseCode == 416) ? DEFAULT_TRACK_BLACKLIST_MS : C.TIME_UNSET;
        }
        return C.TIME_UNSET;
    }

    @Override // com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy
    public long getRetryDelayMsFor(int dataType, long loadDurationMs, IOException exception, int errorCount) {
        if ((exception instanceof ParserException) || (exception instanceof FileNotFoundException) || (exception instanceof Loader.UnexpectedLoaderException)) {
            return C.TIME_UNSET;
        }
        return Math.min((errorCount - 1) * 1000, 5000);
    }

    @Override // com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy
    public int getMinimumLoadableRetryCount(int dataType) {
        int i = this.minimumLoadableRetryCount;
        if (i == -1) {
            if (dataType == 7) {
                return 6;
            }
            return 3;
        }
        return i;
    }
}
