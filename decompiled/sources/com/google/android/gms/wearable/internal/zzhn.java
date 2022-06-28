package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.internal.BaseImplementation;
import com.google.android.gms.wearable.DataApi;
import java.util.List;
import java.util.concurrent.FutureTask;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public final class zzhn extends zzgx<DataApi.DataItemResult> {
    private final List<FutureTask<Boolean>> zza;

    public zzhn(BaseImplementation.ResultHolder<DataApi.DataItemResult> resultHolder, List<FutureTask<Boolean>> list) {
        super(resultHolder);
        this.zza = list;
    }

    @Override // com.google.android.gms.wearable.internal.zza, com.google.android.gms.wearable.internal.zzeq
    public final void zzg(zzge zzgeVar) {
        zzF(new zzcg(zzgp.zza(zzgeVar.zza), zzgeVar.zzb));
        if (zzgeVar.zza != 0) {
            for (FutureTask<Boolean> futureTask : this.zza) {
                futureTask.cancel(true);
            }
        }
    }
}
