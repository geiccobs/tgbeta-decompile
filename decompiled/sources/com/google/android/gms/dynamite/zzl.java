package com.google.android.gms.dynamite;

import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes3.dex */
public interface zzl extends IInterface {
    int zza(IObjectWrapper iObjectWrapper, String str, boolean z) throws RemoteException;

    IObjectWrapper zza(IObjectWrapper iObjectWrapper, String str, int i) throws RemoteException;

    IObjectWrapper zza(IObjectWrapper iObjectWrapper, String str, int i, IObjectWrapper iObjectWrapper2) throws RemoteException;

    IObjectWrapper zza(IObjectWrapper iObjectWrapper, String str, boolean z, long j) throws RemoteException;

    int zzb() throws RemoteException;

    int zzb(IObjectWrapper iObjectWrapper, String str, boolean z) throws RemoteException;

    IObjectWrapper zzb(IObjectWrapper iObjectWrapper, String str, int i) throws RemoteException;
}
