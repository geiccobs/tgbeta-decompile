package com.google.mlkit.common.sdkinternal;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import com.google.android.gms.internal.mlkit_common.zzao;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.mlkit.common.MlKitException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public class MLTaskExecutor {
    private static final Object zza = new Object();
    private static MLTaskExecutor zzb;
    private Handler zzc;

    public static Executor workerThreadExecutor() {
        return zza.INSTANCE;
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public enum zza implements Executor {
        INSTANCE;

        @Override // java.util.concurrent.Executor
        public final void execute(Runnable runnable) {
            MLTaskExecutor.getInstance().zzc.post(runnable);
        }
    }

    public static MLTaskExecutor getInstance() {
        MLTaskExecutor mLTaskExecutor;
        synchronized (zza) {
            if (zzb == null) {
                HandlerThread handlerThread = new HandlerThread("MLHandler", 9);
                handlerThread.start();
                zzb = new MLTaskExecutor(handlerThread.getLooper());
            }
            mLTaskExecutor = zzb;
        }
        return mLTaskExecutor;
    }

    private MLTaskExecutor(Looper looper) {
        this.zzc = new com.google.android.gms.internal.mlkit_common.zzb(looper);
    }

    public <ResultT> Task<ResultT> scheduleCallable(Callable<ResultT> callable) {
        TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
        scheduleRunnable(new Runnable(callable, taskCompletionSource) { // from class: com.google.mlkit.common.sdkinternal.zzg
            private final Callable zza;
            private final TaskCompletionSource zzb;

            /* JADX INFO: Access modifiers changed from: package-private */
            {
                this.zza = callable;
                this.zzb = taskCompletionSource;
            }

            @Override // java.lang.Runnable
            public final void run() {
                MLTaskExecutor.zza(this.zza, this.zzb);
            }
        });
        return taskCompletionSource.getTask();
    }

    public <ResultT> Task<ResultT> scheduleTaskCallable(Callable<Task<ResultT>> callable) {
        return (Task<ResultT>) scheduleCallable(callable).continueWithTask(zzao.zza(), zzf.zza);
    }

    public void scheduleRunnableDelayed(Runnable runnable, long j) {
        this.zzc.postDelayed(runnable, j);
    }

    public void scheduleRunnable(Runnable runnable) {
        workerThreadExecutor().execute(runnable);
    }

    public Handler getHandler() {
        return this.zzc;
    }

    public static final /* synthetic */ void zza(Callable callable, TaskCompletionSource taskCompletionSource) {
        try {
            taskCompletionSource.setResult(callable.call());
        } catch (MlKitException e) {
            taskCompletionSource.setException(e);
        } catch (Exception e2) {
            taskCompletionSource.setException(new MlKitException("Internal error has occurred when executing ML Kit tasks", 13, e2));
        }
    }
}
