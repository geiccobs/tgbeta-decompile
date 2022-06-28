package com.google.android.gms.maps.internal;

import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.StreetViewPanoramaOptions;
import javax.annotation.Nullable;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes.dex */
public interface zzf extends IInterface {
    IMapFragmentDelegate zzd(IObjectWrapper iObjectWrapper) throws RemoteException;

    IMapViewDelegate zze(IObjectWrapper iObjectWrapper, @Nullable GoogleMapOptions googleMapOptions) throws RemoteException;

    ICameraUpdateFactoryDelegate zzf() throws RemoteException;

    com.google.android.gms.internal.maps.zzi zzg() throws RemoteException;

    void zzh(IObjectWrapper iObjectWrapper, int i) throws RemoteException;

    IStreetViewPanoramaViewDelegate zzi(IObjectWrapper iObjectWrapper, @Nullable StreetViewPanoramaOptions streetViewPanoramaOptions) throws RemoteException;

    IStreetViewPanoramaFragmentDelegate zzj(IObjectWrapper iObjectWrapper) throws RemoteException;
}
