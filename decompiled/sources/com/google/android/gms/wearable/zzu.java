package com.google.android.gms.wearable;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzu implements Runnable {
    final /* synthetic */ com.google.android.gms.wearable.internal.zzl zza;
    final /* synthetic */ zzx zzb;

    public zzu(zzx zzxVar, com.google.android.gms.wearable.internal.zzl zzlVar) {
        this.zzb = zzxVar;
        this.zza = zzlVar;
    }

    @Override // java.lang.Runnable
    public final void run() {
        this.zzb.zza.onNotificationReceived(this.zza);
    }
}
