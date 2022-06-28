package com.google.android.gms.internal.location;

import android.app.PendingIntent;
import android.location.Location;
import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.common.api.internal.IStatusCallback;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SleepSegmentRequest;
/* compiled from: com.google.android.gms:play-services-location@@18.0.0 */
/* loaded from: classes3.dex */
public interface zzam extends IInterface {
    void zzd(GeofencingRequest geofencingRequest, PendingIntent pendingIntent, zzak zzakVar) throws RemoteException;

    void zze(PendingIntent pendingIntent, zzak zzakVar, String str) throws RemoteException;

    void zzf(String[] strArr, zzak zzakVar, String str) throws RemoteException;

    void zzg(com.google.android.gms.location.zzbq zzbqVar, zzak zzakVar) throws RemoteException;

    void zzh(long j, boolean z, PendingIntent pendingIntent) throws RemoteException;

    void zzi(ActivityTransitionRequest activityTransitionRequest, PendingIntent pendingIntent, IStatusCallback iStatusCallback) throws RemoteException;

    void zzj(PendingIntent pendingIntent, IStatusCallback iStatusCallback) throws RemoteException;

    void zzk(PendingIntent pendingIntent) throws RemoteException;

    void zzl(PendingIntent pendingIntent, IStatusCallback iStatusCallback) throws RemoteException;

    @Deprecated
    Location zzm() throws RemoteException;

    Location zzn(String str) throws RemoteException;

    void zzo(zzbc zzbcVar) throws RemoteException;

    void zzp(boolean z) throws RemoteException;

    void zzq(Location location) throws RemoteException;

    void zzr(zzai zzaiVar) throws RemoteException;

    LocationAvailability zzs(String str) throws RemoteException;

    void zzt(LocationSettingsRequest locationSettingsRequest, zzao zzaoVar, String str) throws RemoteException;

    void zzu(zzl zzlVar) throws RemoteException;

    void zzv(PendingIntent pendingIntent, SleepSegmentRequest sleepSegmentRequest, IStatusCallback iStatusCallback) throws RemoteException;
}
