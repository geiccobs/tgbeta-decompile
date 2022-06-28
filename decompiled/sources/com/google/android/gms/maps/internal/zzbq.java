package com.google.android.gms.maps.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes3.dex */
public abstract class zzbq extends com.google.android.gms.internal.maps.zzb implements zzbr {
    public zzbq() {
        super("com.google.android.gms.maps.internal.IOnStreetViewPanoramaReadyCallback");
    }

    @Override // com.google.android.gms.internal.maps.zzb
    protected final boolean zza(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        IStreetViewPanoramaDelegate iStreetViewPanoramaDelegate;
        if (i == 1) {
            IBinder readStrongBinder = parcel.readStrongBinder();
            if (readStrongBinder == null) {
                iStreetViewPanoramaDelegate = null;
            } else {
                IInterface queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                if (queryLocalInterface instanceof IStreetViewPanoramaDelegate) {
                    iStreetViewPanoramaDelegate = (IStreetViewPanoramaDelegate) queryLocalInterface;
                } else {
                    iStreetViewPanoramaDelegate = new zzbv(readStrongBinder);
                }
            }
            zzb(iStreetViewPanoramaDelegate);
            parcel2.writeNoException();
            return true;
        }
        return false;
    }
}
