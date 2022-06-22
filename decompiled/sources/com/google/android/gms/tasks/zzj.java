package com.google.android.gms.tasks;
/* compiled from: com.google.android.gms:play-services-tasks@@17.2.0 */
/* loaded from: classes.dex */
final class zzj implements Runnable {
    private final /* synthetic */ Task zza;
    private final /* synthetic */ zzi zzb;

    public zzj(zzi zziVar, Task task) {
        this.zzb = zziVar;
        this.zza = task;
    }

    @Override // java.lang.Runnable
    public final void run() {
        Object obj;
        OnCompleteListener onCompleteListener;
        OnCompleteListener onCompleteListener2;
        obj = this.zzb.zzb;
        synchronized (obj) {
            onCompleteListener = this.zzb.zzc;
            if (onCompleteListener != null) {
                onCompleteListener2 = this.zzb.zzc;
                onCompleteListener2.onComplete(this.zza);
            }
        }
    }
}
