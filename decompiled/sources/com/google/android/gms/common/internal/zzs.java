package com.google.android.gms.common.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes3.dex */
public final class zzs extends com.google.android.gms.internal.common.zzb implements zzr {
    public zzs(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.common.internal.IGoogleCertificatesApi");
    }

    @Override // com.google.android.gms.common.internal.zzr
    public final boolean zza(com.google.android.gms.common.zzq zzqVar, IObjectWrapper iObjectWrapper) throws RemoteException {
        Parcel a_ = a_();
        com.google.android.gms.internal.common.zzd.zza(a_, zzqVar);
        com.google.android.gms.internal.common.zzd.zza(a_, iObjectWrapper);
        Parcel zza = zza(5, a_);
        boolean zza2 = com.google.android.gms.internal.common.zzd.zza(zza);
        zza.recycle();
        return zza2;
    }

    @Override // com.google.android.gms.common.internal.zzr
    public final com.google.android.gms.common.zzl zza(com.google.android.gms.common.zzj zzjVar) throws RemoteException {
        Parcel a_ = a_();
        com.google.android.gms.internal.common.zzd.zza(a_, zzjVar);
        Parcel zza = zza(6, a_);
        com.google.android.gms.common.zzl zzlVar = (com.google.android.gms.common.zzl) com.google.android.gms.internal.common.zzd.zza(zza, com.google.android.gms.common.zzl.CREATOR);
        zza.recycle();
        return zzlVar;
    }

    @Override // com.google.android.gms.common.internal.zzr
    public final boolean zza() throws RemoteException {
        Parcel zza = zza(7, a_());
        boolean zza2 = com.google.android.gms.internal.common.zzd.zza(zza);
        zza.recycle();
        return zza2;
    }
}
