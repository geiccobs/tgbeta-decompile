package com.google.android.gms.internal.maps;

import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes3.dex */
public interface zzo extends IInterface {
    void zzA(IObjectWrapper iObjectWrapper) throws RemoteException;

    IObjectWrapper zzB() throws RemoteException;

    void zzd() throws RemoteException;

    String zze() throws RemoteException;

    void zzf(LatLng latLng) throws RemoteException;

    LatLng zzg() throws RemoteException;

    void zzh(float f) throws RemoteException;

    void zzi(float f, float f2) throws RemoteException;

    float zzj() throws RemoteException;

    float zzk() throws RemoteException;

    void zzl(LatLngBounds latLngBounds) throws RemoteException;

    LatLngBounds zzm() throws RemoteException;

    void zzn(float f) throws RemoteException;

    float zzo() throws RemoteException;

    void zzp(float f) throws RemoteException;

    float zzq() throws RemoteException;

    void zzr(boolean z) throws RemoteException;

    boolean zzs() throws RemoteException;

    void zzt(float f) throws RemoteException;

    float zzu() throws RemoteException;

    boolean zzv(zzo zzoVar) throws RemoteException;

    int zzw() throws RemoteException;

    void zzx(IObjectWrapper iObjectWrapper) throws RemoteException;

    void zzy(boolean z) throws RemoteException;

    boolean zzz() throws RemoteException;
}
