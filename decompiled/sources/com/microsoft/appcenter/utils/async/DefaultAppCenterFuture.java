package com.microsoft.appcenter.utils.async;

import com.microsoft.appcenter.utils.HandlerUtils;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class DefaultAppCenterFuture<T> implements AppCenterFuture<T> {
    private Collection<AppCenterConsumer<T>> mConsumers;
    private final CountDownLatch mLatch = new CountDownLatch(1);
    private T mResult;

    @Override // com.microsoft.appcenter.utils.async.AppCenterFuture
    public T get() {
        while (true) {
            try {
                this.mLatch.await();
                return this.mResult;
            } catch (InterruptedException unused) {
            }
        }
    }

    public boolean isDone() {
        while (true) {
            try {
                return this.mLatch.await(0L, TimeUnit.MILLISECONDS);
            } catch (InterruptedException unused) {
            }
        }
    }

    public synchronized void complete(final T t) {
        if (!isDone()) {
            this.mResult = t;
            this.mLatch.countDown();
            if (this.mConsumers != null) {
                HandlerUtils.runOnUiThread(new Runnable() { // from class: com.microsoft.appcenter.utils.async.DefaultAppCenterFuture.2
                    /* JADX WARN: Multi-variable type inference failed */
                    @Override // java.lang.Runnable
                    public void run() {
                        for (AppCenterConsumer appCenterConsumer : DefaultAppCenterFuture.this.mConsumers) {
                            appCenterConsumer.accept(t);
                        }
                        DefaultAppCenterFuture.this.mConsumers = null;
                    }
                });
            }
        }
    }
}
