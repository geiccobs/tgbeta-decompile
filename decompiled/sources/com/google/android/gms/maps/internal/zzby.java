package com.google.android.gms.maps.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes3.dex */
public final class zzby extends com.google.android.gms.internal.maps.zza implements IUiSettingsDelegate {
    public zzby(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.maps.internal.IUiSettingsDelegate");
    }

    @Override // com.google.android.gms.maps.internal.IUiSettingsDelegate
    public final boolean isCompassEnabled() throws RemoteException {
        Parcel zzH = zzH(10, zza());
        boolean zza = com.google.android.gms.internal.maps.zzc.zza(zzH);
        zzH.recycle();
        return zza;
    }

    @Override // com.google.android.gms.maps.internal.IUiSettingsDelegate
    public final boolean isIndoorLevelPickerEnabled() throws RemoteException {
        Parcel zzH = zzH(17, zza());
        boolean zza = com.google.android.gms.internal.maps.zzc.zza(zzH);
        zzH.recycle();
        return zza;
    }

    @Override // com.google.android.gms.maps.internal.IUiSettingsDelegate
    public final boolean isMapToolbarEnabled() throws RemoteException {
        Parcel zzH = zzH(19, zza());
        boolean zza = com.google.android.gms.internal.maps.zzc.zza(zzH);
        zzH.recycle();
        return zza;
    }

    @Override // com.google.android.gms.maps.internal.IUiSettingsDelegate
    public final boolean isMyLocationButtonEnabled() throws RemoteException {
        Parcel zzH = zzH(11, zza());
        boolean zza = com.google.android.gms.internal.maps.zzc.zza(zzH);
        zzH.recycle();
        return zza;
    }

    @Override // com.google.android.gms.maps.internal.IUiSettingsDelegate
    public final boolean isRotateGesturesEnabled() throws RemoteException {
        Parcel zzH = zzH(15, zza());
        boolean zza = com.google.android.gms.internal.maps.zzc.zza(zzH);
        zzH.recycle();
        return zza;
    }

    @Override // com.google.android.gms.maps.internal.IUiSettingsDelegate
    public final boolean isScrollGesturesEnabled() throws RemoteException {
        Parcel zzH = zzH(12, zza());
        boolean zza = com.google.android.gms.internal.maps.zzc.zza(zzH);
        zzH.recycle();
        return zza;
    }

    @Override // com.google.android.gms.maps.internal.IUiSettingsDelegate
    public final boolean isScrollGesturesEnabledDuringRotateOrZoom() throws RemoteException {
        Parcel zzH = zzH(21, zza());
        boolean zza = com.google.android.gms.internal.maps.zzc.zza(zzH);
        zzH.recycle();
        return zza;
    }

    @Override // com.google.android.gms.maps.internal.IUiSettingsDelegate
    public final boolean isTiltGesturesEnabled() throws RemoteException {
        Parcel zzH = zzH(14, zza());
        boolean zza = com.google.android.gms.internal.maps.zzc.zza(zzH);
        zzH.recycle();
        return zza;
    }

    @Override // com.google.android.gms.maps.internal.IUiSettingsDelegate
    public final boolean isZoomControlsEnabled() throws RemoteException {
        Parcel zzH = zzH(9, zza());
        boolean zza = com.google.android.gms.internal.maps.zzc.zza(zzH);
        zzH.recycle();
        return zza;
    }

    @Override // com.google.android.gms.maps.internal.IUiSettingsDelegate
    public final boolean isZoomGesturesEnabled() throws RemoteException {
        Parcel zzH = zzH(13, zza());
        boolean zza = com.google.android.gms.internal.maps.zzc.zza(zzH);
        zzH.recycle();
        return zza;
    }

    @Override // com.google.android.gms.maps.internal.IUiSettingsDelegate
    public final void setAllGesturesEnabled(boolean z) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.maps.zzc.zzb(zza, z);
        zzc(8, zza);
    }

    @Override // com.google.android.gms.maps.internal.IUiSettingsDelegate
    public final void setCompassEnabled(boolean z) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.maps.zzc.zzb(zza, z);
        zzc(2, zza);
    }

    @Override // com.google.android.gms.maps.internal.IUiSettingsDelegate
    public final void setIndoorLevelPickerEnabled(boolean z) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.maps.zzc.zzb(zza, z);
        zzc(16, zza);
    }

    @Override // com.google.android.gms.maps.internal.IUiSettingsDelegate
    public final void setMapToolbarEnabled(boolean z) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.maps.zzc.zzb(zza, z);
        zzc(18, zza);
    }

    @Override // com.google.android.gms.maps.internal.IUiSettingsDelegate
    public final void setMyLocationButtonEnabled(boolean z) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.maps.zzc.zzb(zza, z);
        zzc(3, zza);
    }

    @Override // com.google.android.gms.maps.internal.IUiSettingsDelegate
    public final void setRotateGesturesEnabled(boolean z) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.maps.zzc.zzb(zza, z);
        zzc(7, zza);
    }

    @Override // com.google.android.gms.maps.internal.IUiSettingsDelegate
    public final void setScrollGesturesEnabled(boolean z) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.maps.zzc.zzb(zza, z);
        zzc(4, zza);
    }

    @Override // com.google.android.gms.maps.internal.IUiSettingsDelegate
    public final void setScrollGesturesEnabledDuringRotateOrZoom(boolean z) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.maps.zzc.zzb(zza, z);
        zzc(20, zza);
    }

    @Override // com.google.android.gms.maps.internal.IUiSettingsDelegate
    public final void setTiltGesturesEnabled(boolean z) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.maps.zzc.zzb(zza, z);
        zzc(6, zza);
    }

    @Override // com.google.android.gms.maps.internal.IUiSettingsDelegate
    public final void setZoomControlsEnabled(boolean z) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.maps.zzc.zzb(zza, z);
        zzc(1, zza);
    }

    @Override // com.google.android.gms.maps.internal.IUiSettingsDelegate
    public final void setZoomGesturesEnabled(boolean z) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.maps.zzc.zzb(zza, z);
        zzc(5, zza);
    }
}
