package com.google.android.gms.wearable;

import com.google.android.gms.wearable.internal.zzfw;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzr implements Runnable {
    final /* synthetic */ zzfw zza;
    final /* synthetic */ zzx zzb;

    public zzr(zzx zzxVar, zzfw zzfwVar) {
        this.zzb = zzxVar;
        this.zza = zzfwVar;
    }

    @Override // java.lang.Runnable
    public final void run() {
        this.zzb.zza.onPeerDisconnected(this.zza);
    }
}
