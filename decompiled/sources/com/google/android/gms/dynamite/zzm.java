package com.google.android.gms.dynamite;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes3.dex */
public final class zzm extends com.google.android.gms.internal.common.zzb implements zzn {
    public zzm(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.dynamite.IDynamiteLoaderV2");
    }

    @Override // com.google.android.gms.dynamite.zzn
    public final IObjectWrapper zza(IObjectWrapper iObjectWrapper, String str, int i, IObjectWrapper iObjectWrapper2) throws RemoteException {
        Parcel a_ = a_();
        com.google.android.gms.internal.common.zzd.zza(a_, iObjectWrapper);
        a_.writeString(str);
        a_.writeInt(i);
        com.google.android.gms.internal.common.zzd.zza(a_, iObjectWrapper2);
        Parcel zza = zza(2, a_);
        IObjectWrapper asInterface = IObjectWrapper.Stub.asInterface(zza.readStrongBinder());
        zza.recycle();
        return asInterface;
    }

    @Override // com.google.android.gms.dynamite.zzn
    public final IObjectWrapper zzb(IObjectWrapper iObjectWrapper, String str, int i, IObjectWrapper iObjectWrapper2) throws RemoteException {
        Parcel a_ = a_();
        com.google.android.gms.internal.common.zzd.zza(a_, iObjectWrapper);
        a_.writeString(str);
        a_.writeInt(i);
        com.google.android.gms.internal.common.zzd.zza(a_, iObjectWrapper2);
        Parcel zza = zza(3, a_);
        IObjectWrapper asInterface = IObjectWrapper.Stub.asInterface(zza.readStrongBinder());
        zza.recycle();
        return asInterface;
    }
}
