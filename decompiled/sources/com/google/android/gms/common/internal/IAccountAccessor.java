package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes3.dex */
public interface IAccountAccessor extends IInterface {

    /* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends com.google.android.gms.internal.common.zza implements IAccountAccessor {
        public Stub() {
            super("com.google.android.gms.common.internal.IAccountAccessor");
        }

        /* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
        /* loaded from: classes3.dex */
        public static class zza extends com.google.android.gms.internal.common.zzb implements IAccountAccessor {
            zza(IBinder iBinder) {
                super(iBinder, "com.google.android.gms.common.internal.IAccountAccessor");
            }

            @Override // com.google.android.gms.common.internal.IAccountAccessor
            public final Account zza() throws RemoteException {
                Parcel zza = zza(2, a_());
                Account account = (Account) com.google.android.gms.internal.common.zzd.zza(zza, Account.CREATOR);
                zza.recycle();
                return account;
            }
        }

        public static IAccountAccessor asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.common.internal.IAccountAccessor");
            if (queryLocalInterface instanceof IAccountAccessor) {
                return (IAccountAccessor) queryLocalInterface;
            }
            return new zza(iBinder);
        }

        @Override // com.google.android.gms.internal.common.zza
        protected final boolean zza(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 2) {
                Account zza2 = zza();
                parcel2.writeNoException();
                com.google.android.gms.internal.common.zzd.zzb(parcel2, zza2);
                return true;
            }
            return false;
        }
    }

    Account zza() throws RemoteException;
}
