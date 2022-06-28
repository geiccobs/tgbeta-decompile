package com.microsoft.appcenter.crashes;

import com.microsoft.appcenter.utils.ShutdownHelper;
import java.lang.Thread;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler mDefaultUncaughtExceptionHandler;
    private boolean mIgnoreDefaultExceptionHandler = false;

    @Override // java.lang.Thread.UncaughtExceptionHandler
    public void uncaughtException(Thread thread, Throwable exception) {
        Crashes.getInstance().saveUncaughtException(thread, exception);
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = this.mDefaultUncaughtExceptionHandler;
        if (uncaughtExceptionHandler != null) {
            uncaughtExceptionHandler.uncaughtException(thread, exception);
        } else {
            ShutdownHelper.shutdown(10);
        }
    }

    void setIgnoreDefaultExceptionHandler(boolean ignoreDefaultExceptionHandler) {
        this.mIgnoreDefaultExceptionHandler = ignoreDefaultExceptionHandler;
        if (ignoreDefaultExceptionHandler) {
            this.mDefaultUncaughtExceptionHandler = null;
        }
    }

    Thread.UncaughtExceptionHandler getDefaultUncaughtExceptionHandler() {
        return this.mDefaultUncaughtExceptionHandler;
    }

    public void register() {
        if (!this.mIgnoreDefaultExceptionHandler) {
            this.mDefaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        } else {
            this.mDefaultUncaughtExceptionHandler = null;
        }
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public void unregister() {
        Thread.setDefaultUncaughtExceptionHandler(this.mDefaultUncaughtExceptionHandler);
    }
}
