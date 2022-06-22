package com.google.android.gms.internal.maps;

import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.maps.model.LatLng;
import javax.annotation.Nullable;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes.dex */
public interface zzl extends IInterface {
    void zzd() throws RemoteException;

    void zzf(LatLng latLng) throws RemoteException;

    void zzh(double d) throws RemoteException;

    double zzi() throws RemoteException;

    void zzl(int i) throws RemoteException;

    void zzn(int i) throws RemoteException;

    boolean zzt(@Nullable zzl zzlVar) throws RemoteException;

    int zzu() throws RemoteException;
}
