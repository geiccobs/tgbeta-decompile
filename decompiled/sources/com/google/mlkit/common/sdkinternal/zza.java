package com.google.mlkit.common.sdkinternal;

import java.util.concurrent.ThreadFactory;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
final /* synthetic */ class zza implements ThreadFactory {
    static final ThreadFactory zza = new zza();

    private zza() {
    }

    @Override // java.util.concurrent.ThreadFactory
    public final Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable, "MlKitCleaner");
        thread.setDaemon(true);
        return thread;
    }
}
