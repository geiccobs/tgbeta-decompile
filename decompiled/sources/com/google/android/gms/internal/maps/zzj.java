package com.google.android.gms.internal.maps;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.maps.model.LatLng;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes.dex */
public final class zzj extends zza implements zzl {
    public zzj(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.maps.model.internal.ICircleDelegate");
    }

    @Override // com.google.android.gms.internal.maps.zzl
    public final void zzd() throws RemoteException {
        zzc(1, zza());
    }

    @Override // com.google.android.gms.internal.maps.zzl
    public final void zzf(LatLng latLng) throws RemoteException {
        Parcel zza = zza();
        zzc.zzd(zza, latLng);
        zzc(3, zza);
    }

    @Override // com.google.android.gms.internal.maps.zzl
    public final void zzh(double d) throws RemoteException {
        Parcel zza = zza();
        zza.writeDouble(d);
        zzc(5, zza);
    }

    @Override // com.google.android.gms.internal.maps.zzl
    public final double zzi() throws RemoteException {
        Parcel zzH = zzH(6, zza());
        double readDouble = zzH.readDouble();
        zzH.recycle();
        return readDouble;
    }

    @Override // com.google.android.gms.internal.maps.zzl
    public final void zzl(int i) throws RemoteException {
        Parcel zza = zza();
        zza.writeInt(i);
        zzc(9, zza);
    }

    @Override // com.google.android.gms.internal.maps.zzl
    public final void zzn(int i) throws RemoteException {
        Parcel zza = zza();
        zza.writeInt(i);
        zzc(11, zza);
    }

    @Override // com.google.android.gms.internal.maps.zzl
    public final boolean zzt(zzl zzlVar) throws RemoteException {
        Parcel zza = zza();
        zzc.zzf(zza, zzlVar);
        Parcel zzH = zzH(17, zza);
        boolean zza2 = zzc.zza(zzH);
        zzH.recycle();
        return zza2;
    }

    @Override // com.google.android.gms.internal.maps.zzl
    public final int zzu() throws RemoteException {
        Parcel zzH = zzH(18, zza());
        int readInt = zzH.readInt();
        zzH.recycle();
        return readInt;
    }
}
