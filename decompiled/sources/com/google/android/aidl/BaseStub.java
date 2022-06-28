package com.google.android.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes3.dex */
public abstract class BaseStub extends Binder implements IInterface {
    private static TransactionInterceptor globalInterceptor = null;

    static synchronized void installTransactionInterceptorPackagePrivate(TransactionInterceptor interceptor) {
        synchronized (BaseStub.class) {
            try {
                if (interceptor == null) {
                    throw new IllegalArgumentException("null interceptor");
                }
                if (globalInterceptor != null) {
                    throw new IllegalStateException("Duplicate TransactionInterceptor installation.");
                }
                globalInterceptor = interceptor;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public BaseStub(String descriptor) {
        attachInterface(this, descriptor);
    }

    @Override // android.os.IInterface
    public IBinder asBinder() {
        return this;
    }

    protected boolean routeToSuperOrEnforceInterface(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        if (code > 16777215) {
            return super.onTransact(code, data, reply, flags);
        }
        data.enforceInterface(getInterfaceDescriptor());
        return false;
    }

    @Override // android.os.Binder
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        if (routeToSuperOrEnforceInterface(code, data, reply, flags)) {
            return true;
        }
        TransactionInterceptor transactionInterceptor = globalInterceptor;
        if (transactionInterceptor == null) {
            return dispatchTransaction(code, data, reply, flags);
        }
        return transactionInterceptor.interceptTransaction(this, code, data, reply, flags);
    }

    protected boolean dispatchTransaction(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        return false;
    }
}
