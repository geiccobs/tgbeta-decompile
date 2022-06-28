package com.google.android.gms.internal.icing;

import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.BaseImplementation;
import com.google.android.gms.search.SearchAuth;
import com.google.android.gms.search.SearchAuthApi;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
final class zzba extends BaseImplementation.ApiMethodImpl<SearchAuthApi.GoogleNowAuthResult, zzav> {
    private final String zza;
    private final String zzb;
    private final boolean zzc = Log.isLoggable("SearchAuth", 3);

    public zzba(GoogleApiClient googleApiClient, String str) {
        super(SearchAuth.API, googleApiClient);
        this.zza = str;
        this.zzb = googleApiClient.getContext().getPackageName();
    }

    @Override // com.google.android.gms.common.api.internal.BasePendingResult
    public final /* bridge */ /* synthetic */ Result createFailedResult(Status status) {
        if (this.zzc) {
            String valueOf = String.valueOf(status.getStatusMessage());
            Log.d("SearchAuth", valueOf.length() != 0 ? "GetGoogleNowAuthImpl received failure: ".concat(valueOf) : new String("GetGoogleNowAuthImpl received failure: "));
        }
        return new zzbb(status, null);
    }

    @Override // com.google.android.gms.common.api.internal.BaseImplementation.ApiMethodImpl
    protected final /* bridge */ /* synthetic */ void doExecute(zzav zzavVar) throws RemoteException {
        zzav zzavVar2 = zzavVar;
        if (this.zzc) {
            Log.d("SearchAuth", "GetGoogleNowAuthImpl started");
        }
        ((zzau) zzavVar2.getService()).zzd(new zzaz(this), this.zzb, this.zza);
    }

    @Override // com.google.android.gms.common.api.internal.BaseImplementation.ApiMethodImpl, com.google.android.gms.common.api.internal.BaseImplementation.ResultHolder
    public final /* bridge */ /* synthetic */ void setResult(Object obj) {
        super.setResult((zzba) ((Result) obj));
    }
}
