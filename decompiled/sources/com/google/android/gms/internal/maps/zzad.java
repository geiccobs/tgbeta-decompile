package com.google.android.gms.internal.maps;

import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.maps.model.Cap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;
import java.util.List;
import javax.annotation.Nullable;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes.dex */
public interface zzad extends IInterface {
    int zzA() throws RemoteException;

    void zzB(@Nullable List<PatternItem> list) throws RemoteException;

    List<PatternItem> zzC() throws RemoteException;

    void zzD(IObjectWrapper iObjectWrapper) throws RemoteException;

    IObjectWrapper zzE() throws RemoteException;

    void zzd() throws RemoteException;

    String zze() throws RemoteException;

    void zzf(List<LatLng> list) throws RemoteException;

    List<LatLng> zzg() throws RemoteException;

    void zzh(float f) throws RemoteException;

    float zzi() throws RemoteException;

    void zzj(int i) throws RemoteException;

    int zzk() throws RemoteException;

    void zzl(float f) throws RemoteException;

    float zzm() throws RemoteException;

    void zzn(boolean z) throws RemoteException;

    boolean zzo() throws RemoteException;

    void zzp(boolean z) throws RemoteException;

    boolean zzq() throws RemoteException;

    boolean zzr(@Nullable zzad zzadVar) throws RemoteException;

    int zzs() throws RemoteException;

    void zzt(boolean z) throws RemoteException;

    boolean zzu() throws RemoteException;

    void zzv(Cap cap) throws RemoteException;

    Cap zzw() throws RemoteException;

    void zzx(Cap cap) throws RemoteException;

    Cap zzy() throws RemoteException;

    void zzz(int i) throws RemoteException;
}
