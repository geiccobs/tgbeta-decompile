package com.google.android.gms.internal.base;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes3.dex */
final class zaq implements zam {
    /* JADX INFO: Access modifiers changed from: private */
    public zaq() {
    }

    @Override // com.google.android.gms.internal.base.zam
    public final ExecutorService zaa(int i, int i2) {
        return zaa(4, Executors.defaultThreadFactory(), i2);
    }

    @Override // com.google.android.gms.internal.base.zam
    public final ExecutorService zaa(int i, ThreadFactory threadFactory, int i2) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(i, i, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue(), threadFactory);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        return Executors.unconfigurableExecutorService(threadPoolExecutor);
    }

    @Override // com.google.android.gms.internal.base.zam
    public final ExecutorService zaa(ThreadFactory threadFactory, int i) {
        return zaa(1, threadFactory, i);
    }
}
