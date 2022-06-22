package com.google.android.gms.cloudmessaging;

import java.util.concurrent.Executor;
/* compiled from: com.google.android.gms:play-services-cloud-messaging@@16.0.0 */
/* loaded from: classes.dex */
final /* synthetic */ class zzz implements Executor {
    static final Executor zza = new zzz();

    private zzz() {
    }

    @Override // java.util.concurrent.Executor
    public final void execute(Runnable runnable) {
        runnable.run();
    }
}
