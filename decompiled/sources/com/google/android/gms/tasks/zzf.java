package com.google.android.gms.tasks;
/* compiled from: com.google.android.gms:play-services-tasks@@17.2.0 */
/* loaded from: classes3.dex */
final class zzf implements Runnable {
    private final /* synthetic */ Task zza;
    private final /* synthetic */ zzd zzb;

    public zzf(zzd zzdVar, Task task) {
        this.zzb = zzdVar;
        this.zza = task;
    }

    @Override // java.lang.Runnable
    public final void run() {
        zzu zzuVar;
        zzu zzuVar2;
        zzu zzuVar3;
        Continuation continuation;
        try {
            continuation = this.zzb.zzb;
            Task task = (Task) continuation.then(this.zza);
            if (task == null) {
                this.zzb.onFailure(new NullPointerException("Continuation returned null"));
                return;
            }
            task.addOnSuccessListener(TaskExecutors.zza, this.zzb);
            task.addOnFailureListener(TaskExecutors.zza, this.zzb);
            task.addOnCanceledListener(TaskExecutors.zza, this.zzb);
        } catch (RuntimeExecutionException e) {
            if (e.getCause() instanceof Exception) {
                zzuVar2 = this.zzb.zzc;
                zzuVar2.zza((Exception) e.getCause());
                return;
            }
            zzuVar = this.zzb.zzc;
            zzuVar.zza((Exception) e);
        } catch (Exception e2) {
            zzuVar3 = this.zzb.zzc;
            zzuVar3.zza(e2);
        }
    }
}
