package com.google.android.gms.internal.maps;

import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.maps.model.LatLng;
import javax.annotation.Nullable;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes.dex */
public interface zzx extends IInterface {
    void zzA(float f, float f2) throws RemoteException;

    void zzB(float f) throws RemoteException;

    float zzC() throws RemoteException;

    void zzD(float f) throws RemoteException;

    float zzE() throws RemoteException;

    void zzF(IObjectWrapper iObjectWrapper) throws RemoteException;

    IObjectWrapper zzG() throws RemoteException;

    void zzd() throws RemoteException;

    String zze() throws RemoteException;

    void zzf(LatLng latLng) throws RemoteException;

    LatLng zzg() throws RemoteException;

    void zzh(@Nullable String str) throws RemoteException;

    String zzi() throws RemoteException;

    void zzj(@Nullable String str) throws RemoteException;

    String zzk() throws RemoteException;

    void zzl(boolean z) throws RemoteException;

    boolean zzm() throws RemoteException;

    void zzn() throws RemoteException;

    void zzo() throws RemoteException;

    boolean zzp() throws RemoteException;

    void zzq(boolean z) throws RemoteException;

    boolean zzr() throws RemoteException;

    boolean zzs(zzx zzxVar) throws RemoteException;

    int zzt() throws RemoteException;

    void zzu(@Nullable IObjectWrapper iObjectWrapper) throws RemoteException;

    void zzv(float f, float f2) throws RemoteException;

    void zzw(boolean z) throws RemoteException;

    boolean zzx() throws RemoteException;

    void zzy(float f) throws RemoteException;

    float zzz() throws RemoteException;
}
