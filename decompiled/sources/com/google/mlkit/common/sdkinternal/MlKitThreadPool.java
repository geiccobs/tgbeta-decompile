package com.google.mlkit.common.sdkinternal;

import com.google.android.gms.internal.mlkit_common.zzal;
import java.util.Deque;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public class MlKitThreadPool extends zzal {
    private static final ThreadLocal<Deque<Runnable>> zzd = new zzj();
    private final ExecutorService zza = new ThreadPoolExecutor(0, Runtime.getRuntime().availableProcessors(), 60, TimeUnit.SECONDS, new LinkedBlockingQueue(), new ThreadFactory(this) { // from class: com.google.mlkit.common.sdkinternal.zzi
        private final MlKitThreadPool zza;

        /* JADX INFO: Access modifiers changed from: package-private */
        {
            this.zza = this;
        }

        @Override // java.util.concurrent.ThreadFactory
        public final Thread newThread(Runnable runnable) {
            return this.zza.zzb(runnable);
        }
    });
    private final ThreadFactory zzb = Executors.defaultThreadFactory();
    private final WeakHashMap<Thread, Void> zzc = new WeakHashMap<>();

    @Override // com.google.android.gms.internal.mlkit_common.zzal
    public final ExecutorService zzb() {
        return this.zza;
    }

    /* renamed from: zzc */
    public final Thread zzb(Runnable runnable) {
        Thread newThread = this.zzb.newThread(runnable);
        synchronized (this.zzc) {
            this.zzc.put(newThread, null);
        }
        return newThread;
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzal, java.util.concurrent.Executor
    public void execute(Runnable runnable) {
        boolean containsKey;
        synchronized (this.zzc) {
            containsKey = this.zzc.containsKey(Thread.currentThread());
        }
        if (containsKey) {
            zzd(runnable);
        } else {
            this.zza.execute(new Runnable(runnable) { // from class: com.google.mlkit.common.sdkinternal.zzh
                private final Runnable zza;

                /* JADX INFO: Access modifiers changed from: package-private */
                {
                    this.zza = runnable;
                }

                @Override // java.lang.Runnable
                public final void run() {
                    MlKitThreadPool.zzd(this.zza);
                }
            });
        }
    }

    public static void zzd(Runnable runnable) {
        Deque<Runnable> deque = zzd.get();
        deque.add(runnable);
        if (deque.size() > 1) {
            return;
        }
        do {
            runnable.run();
            deque.removeFirst();
            runnable = deque.peekFirst();
        } while (runnable != null);
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzal, com.google.android.gms.internal.mlkit_common.zzz
    protected final /* synthetic */ Object zza() {
        return zza();
    }
}
