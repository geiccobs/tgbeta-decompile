package com.google.android.gms.internal.p001authapi;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest;
import com.google.android.gms.common.api.internal.IStatusCallback;
/* compiled from: com.google.android.gms:play-services-auth@@19.2.0 */
/* renamed from: com.google.android.gms.internal.auth-api.zbag */
/* loaded from: classes3.dex */
public final class zbag extends zba implements IInterface {
    public zbag(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.auth.api.identity.internal.ISignInService");
    }

    public final void zbc(zby zbyVar, BeginSignInRequest beginSignInRequest) throws RemoteException {
        Parcel zba = zba();
        zbc.zbc(zba, zbyVar);
        zbc.zbb(zba, beginSignInRequest);
        zbb(1, zba);
    }

    public final void zbd(zbab zbabVar, GetSignInIntentRequest getSignInIntentRequest) throws RemoteException {
        Parcel zba = zba();
        zbc.zbc(zba, zbabVar);
        zbc.zbb(zba, getSignInIntentRequest);
        zbb(3, zba);
    }

    public final void zbe(IStatusCallback iStatusCallback, String str) throws RemoteException {
        Parcel zba = zba();
        zbc.zbc(zba, iStatusCallback);
        zba.writeString(str);
        zbb(2, zba);
    }
}
