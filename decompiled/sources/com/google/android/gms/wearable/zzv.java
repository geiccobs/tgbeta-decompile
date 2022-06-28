package com.google.android.gms.wearable;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzv implements Runnable {
    final /* synthetic */ com.google.android.gms.wearable.internal.zzi zza;
    final /* synthetic */ zzx zzb;

    public zzv(zzx zzxVar, com.google.android.gms.wearable.internal.zzi zziVar) {
        this.zzb = zzxVar;
        this.zza = zziVar;
    }

    @Override // java.lang.Runnable
    public final void run() {
        this.zzb.zza.onEntityUpdate(this.zza);
    }
}
