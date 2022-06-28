package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.internal.BaseImplementation;
import com.google.android.gms.wearable.NodeApi;
import java.util.ArrayList;
import java.util.List;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzhg extends zzgx<NodeApi.GetConnectedNodesResult> {
    public zzhg(BaseImplementation.ResultHolder<NodeApi.GetConnectedNodesResult> resultHolder) {
        super(resultHolder);
    }

    @Override // com.google.android.gms.wearable.internal.zza, com.google.android.gms.wearable.internal.zzeq
    public final void zzo(zzec zzecVar) {
        ArrayList arrayList = new ArrayList();
        List<zzfw> list = zzecVar.zzb;
        if (list != null) {
            arrayList.addAll(list);
        }
        zzF(new zzfn(zzgp.zza(zzecVar.zza), arrayList));
    }
}
