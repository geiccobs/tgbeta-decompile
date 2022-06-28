package com.google.android.gms.internal.icing;

import android.util.Log;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.search.GoogleNowAuthState;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
final class zzaz extends zzaw {
    final /* synthetic */ zzba zza;

    public zzaz(zzba zzbaVar) {
        this.zza = zzbaVar;
    }

    @Override // com.google.android.gms.internal.icing.zzaw, com.google.android.gms.internal.icing.zzat
    public final void zzb(Status status, GoogleNowAuthState googleNowAuthState) {
        boolean z;
        z = this.zza.zzc;
        if (z) {
            Log.d("SearchAuth", "GetGoogleNowAuthImpl success");
        }
        this.zza.setResult((zzba) new zzbb(status, googleNowAuthState));
    }
}
