package com.microsoft.appcenter;

import android.os.Handler;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.microsoft.appcenter.channel.Channel;
import com.microsoft.appcenter.utils.AppCenterLog;
import com.microsoft.appcenter.utils.ShutdownHelper;
import java.lang.Thread;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final int SHUTDOWN_TIMEOUT = 5000;
    private final Channel mChannel;
    private Thread.UncaughtExceptionHandler mDefaultUncaughtExceptionHandler;
    private final Handler mHandler;

    public UncaughtExceptionHandler(Handler handler, Channel channel) {
        this.mHandler = handler;
        this.mChannel = channel;
    }

    @Override // java.lang.Thread.UncaughtExceptionHandler
    public void uncaughtException(Thread thread, Throwable exception) {
        if (AppCenter.getInstance().isInstanceEnabled()) {
            final Semaphore semaphore = new Semaphore(0);
            this.mHandler.post(new Runnable() { // from class: com.microsoft.appcenter.UncaughtExceptionHandler.1
                @Override // java.lang.Runnable
                public void run() {
                    UncaughtExceptionHandler.this.mChannel.shutdown();
                    AppCenterLog.debug("AppCenter", "Channel completed shutdown.");
                    semaphore.release();
                }
            });
            try {
                if (!semaphore.tryAcquire(DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS, TimeUnit.MILLISECONDS)) {
                    AppCenterLog.error("AppCenter", "Timeout waiting for looper tasks to complete.");
                }
            } catch (InterruptedException e) {
                AppCenterLog.warn("AppCenter", "Interrupted while waiting looper to flush.", e);
            }
        }
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = this.mDefaultUncaughtExceptionHandler;
        if (uncaughtExceptionHandler != null) {
            uncaughtExceptionHandler.uncaughtException(thread, exception);
        } else {
            ShutdownHelper.shutdown(10);
        }
    }

    Thread.UncaughtExceptionHandler getDefaultUncaughtExceptionHandler() {
        return this.mDefaultUncaughtExceptionHandler;
    }

    public void register() {
        this.mDefaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public void unregister() {
        Thread.setDefaultUncaughtExceptionHandler(this.mDefaultUncaughtExceptionHandler);
    }
}
