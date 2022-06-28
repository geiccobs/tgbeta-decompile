package com.google.android.aidl;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes3.dex */
public abstract class BaseProxy implements IInterface {
    private final String mDescriptor;
    private final IBinder mRemote;

    public BaseProxy(IBinder remote, String descriptor) {
        this.mRemote = remote;
        this.mDescriptor = descriptor;
    }

    @Override // android.os.IInterface
    public IBinder asBinder() {
        return this.mRemote;
    }

    public Parcel obtainAndWriteInterfaceToken() {
        Parcel parcel = Parcel.obtain();
        parcel.writeInterfaceToken(this.mDescriptor);
        return parcel;
    }

    public Parcel transactAndReadException(int code, Parcel in) throws RemoteException {
        in = Parcel.obtain();
        try {
            this.mRemote.transact(code, in, in, 0);
            in.readException();
            return in;
        } catch (RuntimeException e) {
            throw e;
        } finally {
            in.recycle();
        }
    }

    protected void transactAndReadExceptionReturnVoid(int code, Parcel in) throws RemoteException {
        Parcel out = Parcel.obtain();
        try {
            this.mRemote.transact(code, in, out, 0);
            out.readException();
        } finally {
            in.recycle();
            out.recycle();
        }
    }

    protected void transactOneway(int code, Parcel in) throws RemoteException {
        try {
            this.mRemote.transact(code, in, null, 1);
        } finally {
            in.recycle();
        }
    }
}
