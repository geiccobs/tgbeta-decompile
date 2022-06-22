package com.google.android.gms.maps.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes.dex */
public final class zzby extends com.google.android.gms.internal.maps.zza implements IUiSettingsDelegate {
    public zzby(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.maps.internal.IUiSettingsDelegate");
    }

    @Override // com.google.android.gms.maps.internal.IUiSettingsDelegate
    public final void setCompassEnabled(boolean z) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.maps.zzc.zzb(zza, z);
        zzc(2, zza);
    }

    @Override // com.google.android.gms.maps.internal.IUiSettingsDelegate
    public final void setMyLocationButtonEnabled(boolean z) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.maps.zzc.zzb(zza, z);
        zzc(3, zza);
    }

    @Override // com.google.android.gms.maps.internal.IUiSettingsDelegate
    public final void setZoomControlsEnabled(boolean z) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.maps.zzc.zzb(zza, z);
        zzc(1, zza);
    }
}
