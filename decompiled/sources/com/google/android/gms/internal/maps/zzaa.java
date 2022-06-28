package com.google.android.gms.internal.maps;

import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;
import java.util.List;
import javax.annotation.Nullable;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes.dex */
public interface zzaa extends IInterface {
    int zzA() throws RemoteException;

    void zzB(@Nullable List<PatternItem> list) throws RemoteException;

    List<PatternItem> zzC() throws RemoteException;

    void zzD(IObjectWrapper iObjectWrapper) throws RemoteException;

    IObjectWrapper zzE() throws RemoteException;

    void zzd() throws RemoteException;

    String zze() throws RemoteException;

    void zzf(List<LatLng> list) throws RemoteException;

    List<LatLng> zzg() throws RemoteException;

    void zzh(List list) throws RemoteException;

    List zzi() throws RemoteException;

    void zzj(float f) throws RemoteException;

    float zzk() throws RemoteException;

    void zzl(int i) throws RemoteException;

    int zzm() throws RemoteException;

    void zzn(int i) throws RemoteException;

    int zzo() throws RemoteException;

    void zzp(float f) throws RemoteException;

    float zzq() throws RemoteException;

    void zzr(boolean z) throws RemoteException;

    boolean zzs() throws RemoteException;

    void zzt(boolean z) throws RemoteException;

    boolean zzu() throws RemoteException;

    boolean zzv(@Nullable zzaa zzaaVar) throws RemoteException;

    int zzw() throws RemoteException;

    void zzx(boolean z) throws RemoteException;

    boolean zzy() throws RemoteException;

    void zzz(int i) throws RemoteException;
}
