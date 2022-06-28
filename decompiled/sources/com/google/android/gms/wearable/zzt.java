package com.google.android.gms.wearable;

import com.google.android.gms.wearable.internal.zzag;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzt implements Runnable {
    final /* synthetic */ zzag zza;
    final /* synthetic */ zzx zzb;

    public zzt(zzx zzxVar, zzag zzagVar) {
        this.zzb = zzxVar;
        this.zza = zzagVar;
    }

    @Override // java.lang.Runnable
    public final void run() {
        this.zzb.zza.onCapabilityChanged(this.zza);
    }
}
