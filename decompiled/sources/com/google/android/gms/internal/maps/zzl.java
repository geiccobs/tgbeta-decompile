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
public interface zzl extends IInterface {
    IObjectWrapper zzA() throws RemoteException;

    void zzd() throws RemoteException;

    String zze() throws RemoteException;

    void zzf(LatLng latLng) throws RemoteException;

    LatLng zzg() throws RemoteException;

    void zzh(double d) throws RemoteException;

    double zzi() throws RemoteException;

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

    boolean zzt(@Nullable zzl zzlVar) throws RemoteException;

    int zzu() throws RemoteException;

    void zzv(boolean z) throws RemoteException;

    boolean zzw() throws RemoteException;

    void zzx(@Nullable List<PatternItem> list) throws RemoteException;

    List<PatternItem> zzy() throws RemoteException;

    void zzz(IObjectWrapper iObjectWrapper) throws RemoteException;
}
