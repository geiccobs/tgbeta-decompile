package com.google.android.gms.wallet;

import android.os.RemoteException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.internal.BaseImplementation;
/* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
/* loaded from: classes3.dex */
public abstract class zzak<R extends Result> extends BaseImplementation.ApiMethodImpl<R, com.google.android.gms.internal.wallet.zzab> {
    public zzak(GoogleApiClient googleApiClient) {
        super(Wallet.API, googleApiClient);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.google.android.gms.common.api.internal.BaseImplementation.ApiMethodImpl, com.google.android.gms.common.api.internal.BaseImplementation.ResultHolder
    public final /* bridge */ /* synthetic */ void setResult(Object obj) {
        super.setResult((zzak<R>) ((Result) obj));
    }

    /* renamed from: zza */
    public abstract void doExecute(com.google.android.gms.internal.wallet.zzab zzabVar) throws RemoteException;
}
