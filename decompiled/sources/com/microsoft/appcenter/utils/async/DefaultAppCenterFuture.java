package com.microsoft.appcenter.utils.async;

import com.microsoft.appcenter.utils.HandlerUtils;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
/* loaded from: classes3.dex */
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
            } catch (InterruptedException e) {
            }
        }
    }

    @Override // com.microsoft.appcenter.utils.async.AppCenterFuture
    public boolean isDone() {
        while (true) {
            try {
                return this.mLatch.await(0L, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
            }
        }
    }

    @Override // com.microsoft.appcenter.utils.async.AppCenterFuture
    public synchronized void thenAccept(final AppCenterConsumer<T> function) {
        if (isDone()) {
            HandlerUtils.runOnUiThread(new Runnable() { // from class: com.microsoft.appcenter.utils.async.DefaultAppCenterFuture.1
                /* JADX WARN: Multi-variable type inference failed */
                @Override // java.lang.Runnable
                public void run() {
                    function.accept(DefaultAppCenterFuture.this.mResult);
                }
            });
        } else {
            if (this.mConsumers == null) {
                this.mConsumers = new LinkedList();
            }
            this.mConsumers.add(function);
        }
    }

    public synchronized void complete(final T value) {
        if (!isDone()) {
            this.mResult = value;
            this.mLatch.countDown();
            if (this.mConsumers != null) {
                HandlerUtils.runOnUiThread(new Runnable() { // from class: com.microsoft.appcenter.utils.async.DefaultAppCenterFuture.2
                    /* JADX WARN: Multi-variable type inference failed */
                    @Override // java.lang.Runnable
                    public void run() {
                        for (AppCenterConsumer appCenterConsumer : DefaultAppCenterFuture.this.mConsumers) {
                            appCenterConsumer.accept(value);
                        }
                        DefaultAppCenterFuture.this.mConsumers = null;
                    }
                });
            }
        }
    }
}
