package com.google.android.gms.wearable.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzba extends zzn<Status> {
    final /* synthetic */ int zza;
    final /* synthetic */ zzbi zzb;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public zzba(zzbi zzbiVar, GoogleApiClient googleApiClient, int i) {
        super(googleApiClient);
        this.zzb = zzbiVar;
        this.zza = i;
    }

    @Override // com.google.android.gms.common.api.internal.BasePendingResult
    public final /* bridge */ /* synthetic */ Result createFailedResult(Status status) {
        return status;
    }

    @Override // com.google.android.gms.common.api.internal.BaseImplementation.ApiMethodImpl
    protected final /* bridge */ /* synthetic */ void doExecute(zzhv zzhvVar) throws RemoteException {
        String str;
        str = this.zzb.zza;
        ((zzeu) zzhvVar.getService()).zzw(new zzgz(this), str, this.zza);
    }
}
