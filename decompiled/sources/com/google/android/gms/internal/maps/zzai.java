package com.google.android.gms.internal.maps;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.maps.model.Tile;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes3.dex */
public abstract class zzai extends zzb implements zzaj {
    public zzai() {
        super("com.google.android.gms.maps.model.internal.ITileProviderDelegate");
    }

    public static zzaj zzc(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.maps.model.internal.ITileProviderDelegate");
        if (queryLocalInterface instanceof zzaj) {
            return (zzaj) queryLocalInterface;
        }
        return new zzah(iBinder);
    }

    @Override // com.google.android.gms.internal.maps.zzb
    protected final boolean zza(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        if (i == 1) {
            Tile zzb = zzb(parcel.readInt(), parcel.readInt(), parcel.readInt());
            parcel2.writeNoException();
            zzc.zze(parcel2, zzb);
            return true;
        }
        return false;
    }
}
