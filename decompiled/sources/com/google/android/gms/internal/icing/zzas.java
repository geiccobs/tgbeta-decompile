package com.google.android.gms.internal.icing;

import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.search.GoogleNowAuthState;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public abstract class zzas extends zzb implements zzat {
    public zzas() {
        super("com.google.android.gms.search.internal.ISearchAuthCallbacks");
    }

    @Override // com.google.android.gms.internal.icing.zzb
    protected final boolean zza(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        switch (i) {
            case 1:
                zzb((Status) zzc.zza(parcel, Status.CREATOR), (GoogleNowAuthState) zzc.zza(parcel, GoogleNowAuthState.CREATOR));
                return true;
            case 2:
                zzc((Status) zzc.zza(parcel, Status.CREATOR));
                return true;
            default:
                return false;
        }
    }
}
