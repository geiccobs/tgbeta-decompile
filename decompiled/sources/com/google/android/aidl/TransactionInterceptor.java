package com.google.android.aidl;

import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes3.dex */
public interface TransactionInterceptor {
    boolean interceptTransaction(BaseStub stub, int code, Parcel data, Parcel reply, int flags) throws RemoteException;
}
