package com.google.android.gms.internal.maps;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes3.dex */
public final class zzs extends zza implements zzu {
    public zzs(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.maps.model.internal.IIndoorLevelDelegate");
    }

    @Override // com.google.android.gms.internal.maps.zzu
    public final String zzd() throws RemoteException {
        Parcel zzH = zzH(1, zza());
        String readString = zzH.readString();
        zzH.recycle();
        return readString;
    }

    @Override // com.google.android.gms.internal.maps.zzu
    public final String zze() throws RemoteException {
        Parcel zzH = zzH(2, zza());
        String readString = zzH.readString();
        zzH.recycle();
        return readString;
    }

    @Override // com.google.android.gms.internal.maps.zzu
    public final void zzf() throws RemoteException {
        zzc(3, zza());
    }

    @Override // com.google.android.gms.internal.maps.zzu
    public final boolean zzg(zzu zzuVar) throws RemoteException {
        Parcel zza = zza();
        zzc.zzf(zza, zzuVar);
        Parcel zzH = zzH(4, zza);
        boolean zza2 = zzc.zza(zzH);
        zzH.recycle();
        return zza2;
    }

    @Override // com.google.android.gms.internal.maps.zzu
    public final int zzh() throws RemoteException {
        Parcel zzH = zzH(5, zza());
        int readInt = zzH.readInt();
        zzH.recycle();
        return readInt;
    }
}
