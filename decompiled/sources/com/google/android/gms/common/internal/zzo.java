package com.google.android.gms.common.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes3.dex */
public abstract class zzo extends com.google.android.gms.internal.common.zza implements zzm {
    public zzo() {
        super("com.google.android.gms.common.internal.ICertData");
    }

    public static zzm zza(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.common.internal.ICertData");
        if (queryLocalInterface instanceof zzm) {
            return (zzm) queryLocalInterface;
        }
        return new zzn(iBinder);
    }

    @Override // com.google.android.gms.internal.common.zza
    protected final boolean zza(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        switch (i) {
            case 1:
                IObjectWrapper zzb = zzb();
                parcel2.writeNoException();
                com.google.android.gms.internal.common.zzd.zza(parcel2, zzb);
                return true;
            case 2:
                int zzc = zzc();
                parcel2.writeNoException();
                parcel2.writeInt(zzc);
                return true;
            default:
                return false;
        }
    }
}
