package com.google.android.gms.tasks;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.Executor;
/* compiled from: com.google.android.gms:play-services-tasks@@17.2.0 */
/* loaded from: classes3.dex */
public final class TaskExecutors {
    public static final Executor MAIN_THREAD = new zza();
    static final Executor zza = new zzt();

    private TaskExecutors() {
    }

    /* compiled from: com.google.android.gms:play-services-tasks@@17.2.0 */
    /* loaded from: classes3.dex */
    private static final class zza implements Executor {
        private final Handler zza = new com.google.android.gms.internal.tasks.zzb(Looper.getMainLooper());

        @Override // java.util.concurrent.Executor
        public final void execute(Runnable runnable) {
            this.zza.post(runnable);
        }
    }
}
