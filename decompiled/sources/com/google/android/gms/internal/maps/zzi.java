package com.google.android.gms.internal.maps;

import android.graphics.Bitmap;
import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes3.dex */
public interface zzi extends IInterface {
    IObjectWrapper zzd(int i) throws RemoteException;

    IObjectWrapper zze(String str) throws RemoteException;

    IObjectWrapper zzf(String str) throws RemoteException;

    IObjectWrapper zzg() throws RemoteException;

    IObjectWrapper zzh(float f) throws RemoteException;

    IObjectWrapper zzi(Bitmap bitmap) throws RemoteException;

    IObjectWrapper zzj(String str) throws RemoteException;
}
