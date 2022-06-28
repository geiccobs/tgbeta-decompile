package com.google.android.gms.wearable.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public final class zzeo extends com.google.android.gms.internal.wearable.zza implements IInterface {
    public zzeo(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.wearable.internal.IRpcResponseCallback");
    }

    public final void zzd(boolean z, byte[] bArr) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.wearable.zzc.zza(zza, z);
        zza.writeByteArray(bArr);
        zzH(1, zza);
    }
}
