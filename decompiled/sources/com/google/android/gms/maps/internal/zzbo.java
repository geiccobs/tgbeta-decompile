package com.google.android.gms.maps.internal;

import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes3.dex */
public abstract class zzbo extends com.google.android.gms.internal.maps.zzb implements zzbp {
    public zzbo() {
        super("com.google.android.gms.maps.internal.IOnStreetViewPanoramaLongClickListener");
    }

    @Override // com.google.android.gms.internal.maps.zzb
    protected final boolean zza(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        if (i == 1) {
            zzb((StreetViewPanoramaOrientation) com.google.android.gms.internal.maps.zzc.zzc(parcel, StreetViewPanoramaOrientation.CREATOR));
            parcel2.writeNoException();
            return true;
        }
        return false;
    }
}
