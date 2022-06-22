package com.google.android.gms.internal.clearcut;

import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataHolder;
/* loaded from: classes.dex */
public interface zzl extends IInterface {
    void zza(Status status) throws RemoteException;

    void zza(Status status, long j) throws RemoteException;

    void zza(Status status, com.google.android.gms.clearcut.zzc zzcVar) throws RemoteException;

    void zza(Status status, com.google.android.gms.clearcut.zze[] zzeVarArr) throws RemoteException;

    void zza(DataHolder dataHolder) throws RemoteException;

    void zzb(Status status) throws RemoteException;

    void zzb(Status status, long j) throws RemoteException;

    void zzb(Status status, com.google.android.gms.clearcut.zzc zzcVar) throws RemoteException;

    void zzc(Status status) throws RemoteException;
}
