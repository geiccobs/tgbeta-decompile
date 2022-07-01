package com.google.android.gms.internal.maps;

import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.maps.model.LatLng;
import javax.annotation.Nullable;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes.dex */
public interface zzx extends IInterface {
    void zzF(IObjectWrapper iObjectWrapper) throws RemoteException;

    IObjectWrapper zzG() throws RemoteException;

    void zzd() throws RemoteException;

    void zzf(LatLng latLng) throws RemoteException;

    LatLng zzg() throws RemoteException;

    boolean zzs(zzx zzxVar) throws RemoteException;

    int zzt() throws RemoteException;

    void zzu(@Nullable IObjectWrapper iObjectWrapper) throws RemoteException;

    void zzy(float f) throws RemoteException;
}
