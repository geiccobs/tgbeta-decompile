package com.google.android.gms.internal.icing;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes.dex */
public final class zzaa extends zza {
    public zzaa(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.appdatasearch.internal.ILightweightAppDataSearch");
    }

    public final void zze(zzac zzacVar, com.google.firebase.appindexing.internal.zzc[] zzcVarArr) throws RemoteException {
        Parcel zza = zza();
        zzc.zzc(zza, zzacVar);
        zza.writeTypedArray(zzcVarArr, 0);
        zzc(7, zza);
    }
}
