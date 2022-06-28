package com.google.android.gms.internal.wallet;

import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.wallet.CreateWalletObjectsRequest;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentDataRequest;
/* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
/* loaded from: classes3.dex */
public final class zzs extends zza implements IInterface {
    public zzs(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.wallet.internal.IOwService");
    }

    public final void zzc(CreateWalletObjectsRequest createWalletObjectsRequest, Bundle bundle, zzu zzuVar) throws RemoteException {
        Parcel zza = zza();
        zzc.zzb(zza, createWalletObjectsRequest);
        zzc.zzb(zza, bundle);
        zzc.zzc(zza, zzuVar);
        zzb(6, zza);
    }

    public final void zzd(IsReadyToPayRequest isReadyToPayRequest, Bundle bundle, zzu zzuVar) throws RemoteException {
        Parcel zza = zza();
        zzc.zzb(zza, isReadyToPayRequest);
        zzc.zzb(zza, bundle);
        zzc.zzc(zza, zzuVar);
        zzb(14, zza);
    }

    public final void zze(PaymentDataRequest paymentDataRequest, Bundle bundle, zzu zzuVar) throws RemoteException {
        Parcel zza = zza();
        zzc.zzb(zza, paymentDataRequest);
        zzc.zzb(zza, bundle);
        zzc.zzc(zza, zzuVar);
        zzb(19, zza);
    }
}
