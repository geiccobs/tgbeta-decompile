package com.google.android.gms.signin.internal;

import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes3.dex */
public interface zae extends IInterface {
    void zaa(ConnectionResult connectionResult, zaa zaaVar) throws RemoteException;

    void zaa(Status status) throws RemoteException;

    void zaa(Status status, GoogleSignInAccount googleSignInAccount) throws RemoteException;

    void zaa(zai zaiVar) throws RemoteException;

    void zaa(zak zakVar) throws RemoteException;

    void zab(Status status) throws RemoteException;
}
