package com.google.android.gms.tasks;
/* compiled from: com.google.android.gms:play-services-tasks@@17.2.0 */
/* loaded from: classes.dex */
final class zze implements Runnable {
    private final /* synthetic */ Task zza;
    private final /* synthetic */ zzc zzb;

    public zze(zzc zzcVar, Task task) {
        this.zzb = zzcVar;
        this.zza = task;
    }

    @Override // java.lang.Runnable
    public final void run() {
        zzu zzuVar;
        zzu zzuVar2;
        zzu zzuVar3;
        Continuation continuation;
        zzu zzuVar4;
        zzu zzuVar5;
        if (this.zza.isCanceled()) {
            zzuVar5 = this.zzb.zzc;
            zzuVar5.zza();
            return;
        }
        try {
            continuation = this.zzb.zzb;
            Object then = continuation.then(this.zza);
            zzuVar4 = this.zzb.zzc;
            zzuVar4.zza((zzu) then);
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
