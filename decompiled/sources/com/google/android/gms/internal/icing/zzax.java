package com.google.android.gms.internal.icing;

import android.util.Log;
import com.google.android.gms.common.api.Status;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
final class zzax extends zzaw {
    final /* synthetic */ zzay zza;

    public zzax(zzay zzayVar) {
        this.zza = zzayVar;
    }

    @Override // com.google.android.gms.internal.icing.zzaw, com.google.android.gms.internal.icing.zzat
    public final void zzc(Status status) {
        boolean z;
        z = this.zza.zzc;
        if (z) {
            Log.d("SearchAuth", "ClearTokenImpl success");
        }
        this.zza.setResult((zzay) status);
    }
}
