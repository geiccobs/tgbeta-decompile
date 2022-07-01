package com.google.android.search.verification.api;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.aidl.BaseProxy;
import com.google.android.aidl.BaseStub;
import com.google.android.aidl.Codecs;
/* loaded from: classes.dex */
public interface ISearchActionVerificationService extends IInterface {
    int getVersion() throws RemoteException;

    boolean isSearchAction(Intent intent, Bundle options) throws RemoteException;

    /* loaded from: classes.dex */
    public static abstract class Stub extends BaseStub implements ISearchActionVerificationService {
        public static ISearchActionVerificationService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface queryLocalInterface = obj.queryLocalInterface("com.google.android.search.verification.api.ISearchActionVerificationService");
            if (queryLocalInterface instanceof ISearchActionVerificationService) {
                return (ISearchActionVerificationService) queryLocalInterface;
            }
            return new Proxy(obj);
        }

        /* loaded from: classes.dex */
        public static class Proxy extends BaseProxy implements ISearchActionVerificationService {
            Proxy(IBinder remote) {
                super(remote, "com.google.android.search.verification.api.ISearchActionVerificationService");
            }

            @Override // com.google.android.search.verification.api.ISearchActionVerificationService
            public boolean isSearchAction(Intent intent, Bundle options) throws RemoteException {
                Parcel obtainAndWriteInterfaceToken = obtainAndWriteInterfaceToken();
                Codecs.writeParcelable(obtainAndWriteInterfaceToken, intent);
                Codecs.writeParcelable(obtainAndWriteInterfaceToken, options);
                Parcel transactAndReadException = transactAndReadException(1, obtainAndWriteInterfaceToken);
                boolean createBoolean = Codecs.createBoolean(transactAndReadException);
                transactAndReadException.recycle();
                return createBoolean;
            }

            @Override // com.google.android.search.verification.api.ISearchActionVerificationService
            public int getVersion() throws RemoteException {
                Parcel transactAndReadException = transactAndReadException(2, obtainAndWriteInterfaceToken());
                int readInt = transactAndReadException.readInt();
                transactAndReadException.recycle();
                return readInt;
            }
        }
    }
}
