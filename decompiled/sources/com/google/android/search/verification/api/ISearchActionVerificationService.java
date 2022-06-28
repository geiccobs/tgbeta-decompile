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
/* loaded from: classes3.dex */
public interface ISearchActionVerificationService extends IInterface {
    int getVersion() throws RemoteException;

    boolean isSearchAction(Intent intent, Bundle options) throws RemoteException;

    /* loaded from: classes3.dex */
    public static abstract class Stub extends BaseStub implements ISearchActionVerificationService {
        private static final String DESCRIPTOR = "com.google.android.search.verification.api.ISearchActionVerificationService";
        static final int TRANSACTION_getVersion = 2;
        static final int TRANSACTION_isSearchAction = 1;

        public Stub() {
            super(DESCRIPTOR);
        }

        public static ISearchActionVerificationService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin instanceof ISearchActionVerificationService) {
                return (ISearchActionVerificationService) iin;
            }
            return new Proxy(obj);
        }

        @Override // com.google.android.aidl.BaseStub
        protected boolean dispatchTransaction(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case 1:
                    Intent intent = (Intent) Codecs.createParcelable(data, Intent.CREATOR);
                    Bundle options = (Bundle) Codecs.createParcelable(data, Bundle.CREATOR);
                    boolean retval = isSearchAction(intent, options);
                    reply.writeNoException();
                    Codecs.writeBoolean(reply, retval);
                    return true;
                case 2:
                    int retval2 = getVersion();
                    reply.writeNoException();
                    reply.writeInt(retval2);
                    return true;
                default:
                    return false;
            }
        }

        /* loaded from: classes3.dex */
        public static class Proxy extends BaseProxy implements ISearchActionVerificationService {
            Proxy(IBinder remote) {
                super(remote, Stub.DESCRIPTOR);
            }

            @Override // com.google.android.search.verification.api.ISearchActionVerificationService
            public boolean isSearchAction(Intent intent, Bundle options) throws RemoteException {
                Parcel data = obtainAndWriteInterfaceToken();
                Codecs.writeParcelable(data, intent);
                Codecs.writeParcelable(data, options);
                Parcel reply = transactAndReadException(1, data);
                boolean retval = Codecs.createBoolean(reply);
                reply.recycle();
                return retval;
            }

            @Override // com.google.android.search.verification.api.ISearchActionVerificationService
            public int getVersion() throws RemoteException {
                Parcel data = obtainAndWriteInterfaceToken();
                Parcel reply = transactAndReadException(2, data);
                int retval = reply.readInt();
                reply.recycle();
                return retval;
            }
        }
    }
}
