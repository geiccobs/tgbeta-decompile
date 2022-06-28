package com.google.android.exoplayer2.upstream;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.TraceUtil;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.ExecutorService;
/* loaded from: classes3.dex */
public final class Loader implements LoaderErrorThrower {
    private static final int ACTION_TYPE_DONT_RETRY = 2;
    private static final int ACTION_TYPE_DONT_RETRY_FATAL = 3;
    private static final int ACTION_TYPE_RETRY = 0;
    private static final int ACTION_TYPE_RETRY_AND_RESET_ERROR_COUNT = 1;
    private LoadTask<? extends Loadable> currentTask;
    private final ExecutorService downloadExecutorService;
    private IOException fatalError;
    public static final LoadErrorAction RETRY = createRetryAction(false, C.TIME_UNSET);
    public static final LoadErrorAction RETRY_RESET_ERROR_COUNT = createRetryAction(true, C.TIME_UNSET);
    public static final LoadErrorAction DONT_RETRY = new LoadErrorAction(2, C.TIME_UNSET);
    public static final LoadErrorAction DONT_RETRY_FATAL = new LoadErrorAction(3, C.TIME_UNSET);

    /* loaded from: classes3.dex */
    public interface Callback<T extends Loadable> {
        void onLoadCanceled(T t, long j, long j2, boolean z);

        void onLoadCompleted(T t, long j, long j2);

        LoadErrorAction onLoadError(T t, long j, long j2, IOException iOException, int i);
    }

    /* loaded from: classes3.dex */
    public interface Loadable {
        void cancelLoad();

        void load() throws IOException, InterruptedException;
    }

    /* loaded from: classes3.dex */
    public interface ReleaseCallback {
        void onLoaderReleased();
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    private @interface RetryActionType {
    }

    /* loaded from: classes3.dex */
    public static final class UnexpectedLoaderException extends IOException {
        public UnexpectedLoaderException(Throwable cause) {
            super("Unexpected " + cause.getClass().getSimpleName() + ": " + cause.getMessage(), cause);
        }
    }

    /* loaded from: classes3.dex */
    public static final class LoadErrorAction {
        private final long retryDelayMillis;
        private final int type;

        private LoadErrorAction(int type, long retryDelayMillis) {
            this.type = type;
            this.retryDelayMillis = retryDelayMillis;
        }

        public boolean isRetry() {
            int i = this.type;
            return i == 0 || i == 1;
        }
    }

    public Loader(String threadName) {
        this.downloadExecutorService = Util.newSingleThreadExecutor(threadName);
    }

    public static LoadErrorAction createRetryAction(boolean resetErrorCount, long retryDelayMillis) {
        return new LoadErrorAction(resetErrorCount ? 1 : 0, retryDelayMillis);
    }

    public boolean hasFatalError() {
        return this.fatalError != null;
    }

    public void clearFatalError() {
        this.fatalError = null;
    }

    public <T extends Loadable> long startLoading(T loadable, Callback<T> callback, int defaultMinRetryCount) {
        Looper looper = (Looper) Assertions.checkStateNotNull(Looper.myLooper());
        this.fatalError = null;
        long startTimeMs = SystemClock.elapsedRealtime();
        new LoadTask(looper, loadable, callback, defaultMinRetryCount, startTimeMs).start(0L);
        return startTimeMs;
    }

    public boolean isLoading() {
        return this.currentTask != null;
    }

    public void cancelLoading() {
        ((LoadTask) Assertions.checkStateNotNull(this.currentTask)).cancel(false);
    }

    public void release() {
        release(null);
    }

    public void release(ReleaseCallback callback) {
        LoadTask<? extends Loadable> loadTask = this.currentTask;
        if (loadTask != null) {
            loadTask.cancel(true);
        }
        if (callback != null) {
            this.downloadExecutorService.execute(new ReleaseTask(callback));
        }
        this.downloadExecutorService.shutdown();
    }

    @Override // com.google.android.exoplayer2.upstream.LoaderErrorThrower
    public void maybeThrowError() throws IOException {
        maybeThrowError(Integer.MIN_VALUE);
    }

    @Override // com.google.android.exoplayer2.upstream.LoaderErrorThrower
    public void maybeThrowError(int minRetryCount) throws IOException {
        IOException iOException = this.fatalError;
        if (iOException != null) {
            throw iOException;
        }
        LoadTask<? extends Loadable> loadTask = this.currentTask;
        if (loadTask != null) {
            loadTask.maybeThrowError(minRetryCount == Integer.MIN_VALUE ? loadTask.defaultMinRetryCount : minRetryCount);
        }
    }

    /* loaded from: classes3.dex */
    public final class LoadTask<T extends Loadable> extends Handler implements Runnable {
        private static final int MSG_FATAL_ERROR = 3;
        private static final int MSG_FINISH = 1;
        private static final int MSG_IO_EXCEPTION = 2;
        private static final int MSG_START = 0;
        private static final String TAG = "LoadTask";
        private Callback<T> callback;
        private boolean canceled;
        private IOException currentError;
        public final int defaultMinRetryCount;
        private int errorCount;
        private Thread executorThread;
        private final T loadable;
        private volatile boolean released;
        private final long startTimeMs;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public LoadTask(Looper looper, T loadable, Callback<T> callback, int defaultMinRetryCount, long startTimeMs) {
            super(looper);
            Loader.this = r1;
            this.loadable = loadable;
            this.callback = callback;
            this.defaultMinRetryCount = defaultMinRetryCount;
            this.startTimeMs = startTimeMs;
        }

        public void maybeThrowError(int minRetryCount) throws IOException {
            IOException iOException = this.currentError;
            if (iOException != null && this.errorCount > minRetryCount) {
                throw iOException;
            }
        }

        public void start(long delayMillis) {
            Assertions.checkState(Loader.this.currentTask == null);
            Loader.this.currentTask = this;
            if (delayMillis > 0) {
                sendEmptyMessageDelayed(0, delayMillis);
            } else {
                execute();
            }
        }

        public void cancel(boolean released) {
            this.released = released;
            this.currentError = null;
            if (hasMessages(0)) {
                this.canceled = true;
                removeMessages(0);
                if (!released) {
                    sendEmptyMessage(1);
                }
            } else {
                synchronized (this) {
                    this.canceled = true;
                    this.loadable.cancelLoad();
                    Thread executorThread = this.executorThread;
                    if (executorThread != null) {
                        executorThread.interrupt();
                    }
                }
            }
            if (released) {
                finish();
                long nowMs = SystemClock.elapsedRealtime();
                ((Callback) Assertions.checkNotNull(this.callback)).onLoadCanceled(this.loadable, nowMs, nowMs - this.startTimeMs, true);
                this.callback = null;
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            boolean shouldLoad;
            try {
                synchronized (this) {
                    shouldLoad = !this.canceled;
                    this.executorThread = Thread.currentThread();
                }
                if (shouldLoad) {
                    TraceUtil.beginSection("load:" + this.loadable.getClass().getSimpleName());
                    try {
                        this.loadable.load();
                        TraceUtil.endSection();
                    } catch (Throwable th) {
                        TraceUtil.endSection();
                        throw th;
                    }
                }
                synchronized (this) {
                    this.executorThread = null;
                    Thread.interrupted();
                }
                if (!this.released) {
                    sendEmptyMessage(1);
                }
            } catch (IOException e) {
                if (!this.released) {
                    obtainMessage(2, e).sendToTarget();
                }
            } catch (Error e2) {
                Log.e(TAG, "Unexpected error loading stream", e2);
                if (!this.released) {
                    obtainMessage(3, e2).sendToTarget();
                }
                throw e2;
            } catch (InterruptedException e3) {
                Assertions.checkState(this.canceled);
                if (!this.released) {
                    sendEmptyMessage(1);
                }
            } catch (Exception e4) {
                Log.e(TAG, "Unexpected exception loading stream", e4);
                if (!this.released) {
                    obtainMessage(2, new UnexpectedLoaderException(e4)).sendToTarget();
                }
            } catch (OutOfMemoryError e5) {
                Log.e(TAG, "OutOfMemory error loading stream", e5);
                if (!this.released) {
                    obtainMessage(2, new UnexpectedLoaderException(e5)).sendToTarget();
                }
            }
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            long j;
            if (this.released) {
                return;
            }
            if (msg.what == 0) {
                execute();
            } else if (msg.what == 3) {
                throw ((Error) msg.obj);
            } else {
                finish();
                long nowMs = SystemClock.elapsedRealtime();
                long durationMs = nowMs - this.startTimeMs;
                Callback<T> callback = (Callback) Assertions.checkNotNull(this.callback);
                if (this.canceled) {
                    callback.onLoadCanceled(this.loadable, nowMs, durationMs, false);
                    return;
                }
                switch (msg.what) {
                    case 1:
                        try {
                            callback.onLoadCompleted(this.loadable, nowMs, durationMs);
                            return;
                        } catch (RuntimeException e) {
                            Log.e(TAG, "Unexpected exception handling load completed", e);
                            Loader.this.fatalError = new UnexpectedLoaderException(e);
                            return;
                        }
                    case 2:
                        IOException iOException = (IOException) msg.obj;
                        this.currentError = iOException;
                        int i = this.errorCount + 1;
                        this.errorCount = i;
                        LoadErrorAction action = callback.onLoadError(this.loadable, nowMs, durationMs, iOException, i);
                        if (action.type != 3) {
                            if (action.type != 2) {
                                if (action.type == 1) {
                                    this.errorCount = 1;
                                }
                                if (action.retryDelayMillis != C.TIME_UNSET) {
                                    j = action.retryDelayMillis;
                                } else {
                                    j = getRetryDelayMillis();
                                }
                                start(j);
                                return;
                            }
                            return;
                        }
                        Loader.this.fatalError = this.currentError;
                        return;
                    default:
                        return;
                }
            }
        }

        private void execute() {
            this.currentError = null;
            Loader.this.downloadExecutorService.execute((Runnable) Assertions.checkNotNull(Loader.this.currentTask));
        }

        private void finish() {
            Loader.this.currentTask = null;
        }

        private long getRetryDelayMillis() {
            return Math.min((this.errorCount - 1) * 1000, 5000);
        }
    }

    /* loaded from: classes3.dex */
    public static final class ReleaseTask implements Runnable {
        private final ReleaseCallback callback;

        public ReleaseTask(ReleaseCallback callback) {
            this.callback = callback;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.callback.onLoaderReleased();
        }
    }
}
