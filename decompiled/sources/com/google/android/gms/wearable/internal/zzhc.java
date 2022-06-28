package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.BaseImplementation;
import com.google.android.gms.wearable.CapabilityApi;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzhc extends zzgx<CapabilityApi.GetCapabilityResult> {
    public zzhc(BaseImplementation.ResultHolder<CapabilityApi.GetCapabilityResult> resultHolder) {
        super(resultHolder);
    }

    @Override // com.google.android.gms.wearable.internal.zza, com.google.android.gms.wearable.internal.zzeq
    public final void zzz(zzdk zzdkVar) {
        Status zza = zzgp.zza(zzdkVar.zza);
        zzag zzagVar = zzdkVar.zzb;
        zzF(new zzx(zza, zzagVar == null ? null : new zzv(zzagVar)));
    }
}
