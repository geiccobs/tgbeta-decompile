package com.google.android.gms.wearable;

import com.google.android.gms.wearable.internal.zzau;
import com.google.android.gms.wearable.internal.zzax;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzw implements Runnable {
    final /* synthetic */ zzax zza;
    final /* synthetic */ zzx zzb;

    public zzw(zzx zzxVar, zzax zzaxVar) {
        this.zzb = zzxVar;
        this.zza = zzaxVar;
    }

    @Override // java.lang.Runnable
    public final void run() {
        zzau zzauVar;
        this.zza.zza(this.zzb.zza);
        zzax zzaxVar = this.zza;
        zzauVar = this.zzb.zza.zzh;
        zzaxVar.zza(zzauVar);
    }
}
