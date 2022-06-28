package com.google.android.gms.wearable.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzaz extends zzn<Status> {
    final /* synthetic */ zzbi zza;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public zzaz(zzbi zzbiVar, GoogleApiClient googleApiClient) {
        super(googleApiClient);
        this.zza = zzbiVar;
    }

    @Override // com.google.android.gms.common.api.internal.BasePendingResult
    public final /* bridge */ /* synthetic */ Result createFailedResult(Status status) {
        return status;
    }

    @Override // com.google.android.gms.common.api.internal.BaseImplementation.ApiMethodImpl
    protected final /* bridge */ /* synthetic */ void doExecute(zzhv zzhvVar) throws RemoteException {
        String str;
        str = this.zza.zza;
        ((zzeu) zzhvVar.getService()).zzv(new zzgy(this), str);
    }
}