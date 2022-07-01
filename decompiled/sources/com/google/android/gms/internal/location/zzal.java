package com.google.android.gms.internal.location;

import android.location.Location;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.location.LocationSettingsRequest;
/* compiled from: com.google.android.gms:play-services-location@@18.0.0 */
/* loaded from: classes.dex */
public final class zzal extends zza implements zzam {
    public zzal(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.location.internal.IGoogleLocationManagerService");
    }

    @Override // com.google.android.gms.internal.location.zzam
    public final Location zzm() throws RemoteException {
        Parcel zzw = zzw(7, zza());
        Location location = (Location) zzc.zzb(zzw, Location.CREATOR);
        zzw.recycle();
        return location;
    }

    @Override // com.google.android.gms.internal.location.zzam
    public final Location zzn(String str) throws RemoteException {
        Parcel zza = zza();
        zza.writeString(str);
        Parcel zzw = zzw(80, zza);
        Location location = (Location) zzc.zzb(zzw, Location.CREATOR);
        zzw.recycle();
        return location;
    }

    @Override // com.google.android.gms.internal.location.zzam
    public final void zzo(zzbc zzbcVar) throws RemoteException {
        Parcel zza = zza();
        zzc.zzc(zza, zzbcVar);
        zzx(59, zza);
    }

    @Override // com.google.android.gms.internal.location.zzam
    public final void zzp(boolean z) throws RemoteException {
        Parcel zza = zza();
        zzc.zza(zza, z);
        zzx(12, zza);
    }

    @Override // com.google.android.gms.internal.location.zzam
    public final void zzt(LocationSettingsRequest locationSettingsRequest, zzao zzaoVar, String str) throws RemoteException {
        Parcel zza = zza();
        zzc.zzc(zza, locationSettingsRequest);
        zzc.zzd(zza, zzaoVar);
        zza.writeString(null);
        zzx(63, zza);
    }

    @Override // com.google.android.gms.internal.location.zzam
    public final void zzu(zzl zzlVar) throws RemoteException {
        Parcel zza = zza();
        zzc.zzc(zza, zzlVar);
        zzx(75, zza);
    }
}
