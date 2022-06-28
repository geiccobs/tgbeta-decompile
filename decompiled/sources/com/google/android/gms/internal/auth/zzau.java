package com.google.android.gms.internal.auth;

import android.content.Context;
import android.os.RemoteException;
import com.google.android.gms.common.api.GoogleApiClient;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public final class zzau extends zzaq {
    public zzau(zzar zzarVar, GoogleApiClient googleApiClient) {
        super(googleApiClient);
    }

    @Override // com.google.android.gms.internal.auth.zzaq
    protected final void zza(Context context, zzan zzanVar) throws RemoteException {
        zzanVar.zza(new zzav(this));
    }
}
