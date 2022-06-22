package com.google.android.gms.tasks;

import com.google.android.gms.common.internal.Preconditions;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.concurrent.GuardedBy;
/* compiled from: com.google.android.gms:play-services-tasks@@17.2.0 */
/* loaded from: classes.dex */
public final class Tasks {

    /* compiled from: com.google.android.gms:play-services-tasks@@17.2.0 */
    /* loaded from: classes.dex */
    public interface zza<T> extends OnCanceledListener, OnFailureListener, OnSuccessListener<T> {
    }

    public static <TResult> Task<TResult> forResult(TResult tresult) {
        zzu zzuVar = new zzu();
        zzuVar.zza((zzu) tresult);
        return zzuVar;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: com.google.android.gms:play-services-tasks@@17.2.0 */
    /* loaded from: classes.dex */
    public static final class zzb implements zza {
        private final CountDownLatch zza;

        private zzb() {
            this.zza = new CountDownLatch(1);
        }

        @Override // com.google.android.gms.tasks.OnSuccessListener
        public final void onSuccess(Object obj) {
            this.zza.countDown();
        }

        @Override // com.google.android.gms.tasks.OnFailureListener
        public final void onFailure(Exception exc) {
            this.zza.countDown();
        }

        @Override // com.google.android.gms.tasks.OnCanceledListener
        public final void onCanceled() {
            this.zza.countDown();
        }

        public final void zza() throws InterruptedException {
            this.zza.await();
        }

        public final boolean zza(long j, TimeUnit timeUnit) throws InterruptedException {
            return this.zza.await(j, timeUnit);
        }

        /* synthetic */ zzb(zzy zzyVar) {
            this();
        }
    }

    public static <TResult> Task<TResult> forException(Exception exc) {
        zzu zzuVar = new zzu();
        zzuVar.zza(exc);
        return zzuVar;
    }

    /* compiled from: com.google.android.gms:play-services-tasks@@17.2.0 */
    /* loaded from: classes.dex */
    public static final class zzc implements zza {
        private final Object zza = new Object();
        private final int zzb;
        private final zzu<Void> zzc;
        @GuardedBy("mLock")
        private int zzd;
        @GuardedBy("mLock")
        private int zze;
        @GuardedBy("mLock")
        private int zzf;
        @GuardedBy("mLock")
        private Exception zzg;
        @GuardedBy("mLock")
        private boolean zzh;

        public zzc(int i, zzu<Void> zzuVar) {
            this.zzb = i;
            this.zzc = zzuVar;
        }

        @Override // com.google.android.gms.tasks.OnFailureListener
        public final void onFailure(Exception exc) {
            synchronized (this.zza) {
                this.zze++;
                this.zzg = exc;
                zza();
            }
        }

        @Override // com.google.android.gms.tasks.OnSuccessListener
        public final void onSuccess(Object obj) {
            synchronized (this.zza) {
                this.zzd++;
                zza();
            }
        }

        @Override // com.google.android.gms.tasks.OnCanceledListener
        public final void onCanceled() {
            synchronized (this.zza) {
                this.zzf++;
                this.zzh = true;
                zza();
            }
        }

        @GuardedBy("mLock")
        private final void zza() {
            if (this.zzd + this.zze + this.zzf == this.zzb) {
                if (this.zzg != null) {
                    zzu<Void> zzuVar = this.zzc;
                    int i = this.zze;
                    int i2 = this.zzb;
                    StringBuilder sb = new StringBuilder(54);
                    sb.append(i);
                    sb.append(" out of ");
                    sb.append(i2);
                    sb.append(" underlying tasks failed");
                    zzuVar.zza(new ExecutionException(sb.toString(), this.zzg));
                } else if (this.zzh) {
                    this.zzc.zza();
                } else {
                    this.zzc.zza((zzu<Void>) null);
                }
            }
        }
    }

    public static <TResult> Task<TResult> forCanceled() {
        zzu zzuVar = new zzu();
        zzuVar.zza();
        return zzuVar;
    }

    @Deprecated
    public static <TResult> Task<TResult> call(Executor executor, Callable<TResult> callable) {
        Preconditions.checkNotNull(executor, "Executor must not be null");
        Preconditions.checkNotNull(callable, "Callback must not be null");
        zzu zzuVar = new zzu();
        executor.execute(new zzy(zzuVar, callable));
        return zzuVar;
    }

    public static <TResult> TResult await(Task<TResult> task) throws ExecutionException, InterruptedException {
        Preconditions.checkNotMainThread();
        Preconditions.checkNotNull(task, "Task must not be null");
        if (task.isComplete()) {
            return (TResult) zza(task);
        }
        zzb zzbVar = new zzb(null);
        zza(task, zzbVar);
        zzbVar.zza();
        return (TResult) zza(task);
    }

    public static <TResult> TResult await(Task<TResult> task, long j, TimeUnit timeUnit) throws ExecutionException, InterruptedException, TimeoutException {
        Preconditions.checkNotMainThread();
        Preconditions.checkNotNull(task, "Task must not be null");
        Preconditions.checkNotNull(timeUnit, "TimeUnit must not be null");
        if (task.isComplete()) {
            return (TResult) zza(task);
        }
        zzb zzbVar = new zzb(null);
        zza(task, zzbVar);
        if (!zzbVar.zza(j, timeUnit)) {
            throw new TimeoutException("Timed out waiting for Task");
        }
        return (TResult) zza(task);
    }

    public static Task<Void> whenAll(Collection<? extends Task<?>> collection) {
        if (collection == null || collection.isEmpty()) {
            return forResult(null);
        }
        for (Task<?> task : collection) {
            if (task == null) {
                throw new NullPointerException("null tasks are not accepted");
            }
        }
        zzu zzuVar = new zzu();
        zzc zzcVar = new zzc(collection.size(), zzuVar);
        for (Task<?> task2 : collection) {
            zza(task2, zzcVar);
        }
        return zzuVar;
    }

    public static Task<List<Task<?>>> whenAllComplete(Collection<? extends Task<?>> collection) {
        if (collection == null || collection.isEmpty()) {
            return forResult(Collections.emptyList());
        }
        return whenAll(collection).continueWithTask(new zzz(collection));
    }

    public static Task<List<Task<?>>> whenAllComplete(Task<?>... taskArr) {
        if (taskArr == null || taskArr.length == 0) {
            return forResult(Collections.emptyList());
        }
        return whenAllComplete(Arrays.asList(taskArr));
    }

    private static <TResult> TResult zza(Task<TResult> task) throws ExecutionException {
        if (task.isSuccessful()) {
            return task.getResult();
        }
        if (task.isCanceled()) {
            throw new CancellationException("Task is already canceled");
        }
        throw new ExecutionException(task.getException());
    }

    private static <T> void zza(Task<T> task, zza<? super T> zzaVar) {
        Executor executor = TaskExecutors.zza;
        task.addOnSuccessListener(executor, zzaVar);
        task.addOnFailureListener(executor, zzaVar);
        task.addOnCanceledListener(executor, zzaVar);
    }
}
