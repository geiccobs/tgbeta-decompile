package com.google.android.gms.internal.location;

import android.location.Location;
import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.location.LocationSettingsRequest;
/* compiled from: com.google.android.gms:play-services-location@@18.0.0 */
/* loaded from: classes.dex */
public interface zzam extends IInterface {
    @Deprecated
    Location zzm() throws RemoteException;

    Location zzn(String str) throws RemoteException;

    void zzo(zzbc zzbcVar) throws RemoteException;

    void zzp(boolean z) throws RemoteException;

    void zzt(LocationSettingsRequest locationSettingsRequest, zzao zzaoVar, String str) throws RemoteException;

    void zzu(zzl zzlVar) throws RemoteException;
}
