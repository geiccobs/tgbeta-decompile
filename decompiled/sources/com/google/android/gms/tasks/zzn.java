package com.google.android.gms.tasks;
/* compiled from: com.google.android.gms:play-services-tasks@@17.2.0 */
/* loaded from: classes3.dex */
final class zzn implements Runnable {
    private final /* synthetic */ Task zza;
    private final /* synthetic */ zzm zzb;

    public zzn(zzm zzmVar, Task task) {
        this.zzb = zzmVar;
        this.zza = task;
    }

    @Override // java.lang.Runnable
    public final void run() {
        Object obj;
        OnSuccessListener onSuccessListener;
        OnSuccessListener onSuccessListener2;
        obj = this.zzb.zzb;
        synchronized (obj) {
            onSuccessListener = this.zzb.zzc;
            if (onSuccessListener != null) {
                onSuccessListener2 = this.zzb.zzc;
                onSuccessListener2.onSuccess(this.zza.getResult());
            }
        }
    }
}
