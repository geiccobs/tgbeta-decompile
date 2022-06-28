package com.google.android.gms.internal.location;

import android.app.PendingIntent;
import android.location.Location;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.common.api.internal.IStatusCallback;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SleepSegmentRequest;
/* compiled from: com.google.android.gms:play-services-location@@18.0.0 */
/* loaded from: classes3.dex */
public final class zzal extends zza implements zzam {
    public zzal(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.location.internal.IGoogleLocationManagerService");
    }

    @Override // com.google.android.gms.internal.location.zzam
    public final void zzd(GeofencingRequest geofencingRequest, PendingIntent pendingIntent, zzak zzakVar) throws RemoteException {
        Parcel zza = zza();
        zzc.zzc(zza, geofencingRequest);
        zzc.zzc(zza, pendingIntent);
        zzc.zzd(zza, zzakVar);
        zzx(57, zza);
    }

    @Override // com.google.android.gms.internal.location.zzam
    public final void zze(PendingIntent pendingIntent, zzak zzakVar, String str) throws RemoteException {
        Parcel zza = zza();
        zzc.zzc(zza, pendingIntent);
        zzc.zzd(zza, zzakVar);
        zza.writeString(str);
        zzx(2, zza);
    }

    @Override // com.google.android.gms.internal.location.zzam
    public final void zzf(String[] strArr, zzak zzakVar, String str) throws RemoteException {
        Parcel zza = zza();
        zza.writeStringArray(strArr);
        zzc.zzd(zza, zzakVar);
        zza.writeString(str);
        zzx(3, zza);
    }

    @Override // com.google.android.gms.internal.location.zzam
    public final void zzg(com.google.android.gms.location.zzbq zzbqVar, zzak zzakVar) throws RemoteException {
        Parcel zza = zza();
        zzc.zzc(zza, zzbqVar);
        zzc.zzd(zza, zzakVar);
        zzx(74, zza);
    }

    @Override // com.google.android.gms.internal.location.zzam
    public final void zzh(long j, boolean z, PendingIntent pendingIntent) throws RemoteException {
        Parcel zza = zza();
        zza.writeLong(j);
        zzc.zza(zza, true);
        zzc.zzc(zza, pendingIntent);
        zzx(5, zza);
    }

    @Override // com.google.android.gms.internal.location.zzam
    public final void zzi(ActivityTransitionRequest activityTransitionRequest, PendingIntent pendingIntent, IStatusCallback iStatusCallback) throws RemoteException {
        Parcel zza = zza();
        zzc.zzc(zza, activityTransitionRequest);
        zzc.zzc(zza, pendingIntent);
        zzc.zzd(zza, iStatusCallback);
        zzx(72, zza);
    }

    @Override // com.google.android.gms.internal.location.zzam
    public final void zzj(PendingIntent pendingIntent, IStatusCallback iStatusCallback) throws RemoteException {
        Parcel zza = zza();
        zzc.zzc(zza, pendingIntent);
        zzc.zzd(zza, iStatusCallback);
        zzx(73, zza);
    }

    @Override // com.google.android.gms.internal.location.zzam
    public final void zzk(PendingIntent pendingIntent) throws RemoteException {
        Parcel zza = zza();
        zzc.zzc(zza, pendingIntent);
        zzx(6, zza);
    }

    @Override // com.google.android.gms.internal.location.zzam
    public final void zzl(PendingIntent pendingIntent, IStatusCallback iStatusCallback) throws RemoteException {
        Parcel zza = zza();
        zzc.zzc(zza, pendingIntent);
        zzc.zzd(zza, iStatusCallback);
        zzx(69, zza);
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
    public final void zzq(Location location) throws RemoteException {
        Parcel zza = zza();
        zzc.zzc(zza, location);
        zzx(13, zza);
    }

    @Override // com.google.android.gms.internal.location.zzam
    public final void zzr(zzai zzaiVar) throws RemoteException {
        Parcel zza = zza();
        zzc.zzd(zza, zzaiVar);
        zzx(67, zza);
    }

    @Override // com.google.android.gms.internal.location.zzam
    public final LocationAvailability zzs(String str) throws RemoteException {
        Parcel zza = zza();
        zza.writeString(str);
        Parcel zzw = zzw(34, zza);
        LocationAvailability locationAvailability = (LocationAvailability) zzc.zzb(zzw, LocationAvailability.CREATOR);
        zzw.recycle();
        return locationAvailability;
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

    @Override // com.google.android.gms.internal.location.zzam
    public final void zzv(PendingIntent pendingIntent, SleepSegmentRequest sleepSegmentRequest, IStatusCallback iStatusCallback) throws RemoteException {
        Parcel zza = zza();
        zzc.zzc(zza, pendingIntent);
        zzc.zzc(zza, sleepSegmentRequest);
        zzc.zzd(zza, iStatusCallback);
        zzx(79, zza);
    }
}
