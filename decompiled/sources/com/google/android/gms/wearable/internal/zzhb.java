package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.BaseImplementation;
import com.google.android.gms.wearable.CapabilityApi;
import java.util.HashMap;
import java.util.List;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzhb extends zzgx<CapabilityApi.GetAllCapabilitiesResult> {
    public zzhb(BaseImplementation.ResultHolder<CapabilityApi.GetAllCapabilitiesResult> resultHolder) {
        super(resultHolder);
    }

    @Override // com.google.android.gms.wearable.internal.zza, com.google.android.gms.wearable.internal.zzeq
    public final void zzA(zzdi zzdiVar) {
        Status zza = zzgp.zza(zzdiVar.zza);
        List<zzag> list = zzdiVar.zzb;
        HashMap hashMap = new HashMap();
        if (list != null) {
            for (zzag zzagVar : list) {
                hashMap.put(zzagVar.getName(), new zzv(zzagVar));
            }
        }
        zzF(new zzw(zza, hashMap));
    }
}
