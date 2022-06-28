package com.google.android.gms.internal.play_billing;

import android.os.Bundle;
import android.os.IInterface;
import android.os.RemoteException;
/* compiled from: com.android.billingclient:billing@@5.0.0 */
/* loaded from: classes3.dex */
public interface zze extends IInterface {
    int zza(int i, String str, String str2) throws RemoteException;

    int zzc(int i, String str, String str2, Bundle bundle) throws RemoteException;

    Bundle zzd(int i, String str, String str2, Bundle bundle) throws RemoteException;

    Bundle zze(int i, String str, String str2, Bundle bundle) throws RemoteException;

    Bundle zzf(int i, String str, String str2, String str3, String str4) throws RemoteException;

    Bundle zzg(int i, String str, String str2, String str3, String str4, Bundle bundle) throws RemoteException;

    Bundle zzh(int i, String str, String str2, String str3, Bundle bundle) throws RemoteException;

    Bundle zzi(int i, String str, String str2, String str3) throws RemoteException;

    Bundle zzj(int i, String str, String str2, String str3, Bundle bundle) throws RemoteException;

    Bundle zzk(int i, String str, String str2, Bundle bundle) throws RemoteException;

    Bundle zzl(int i, String str, String str2, Bundle bundle, Bundle bundle2) throws RemoteException;

    Bundle zzm(int i, String str, String str2, String str3, Bundle bundle) throws RemoteException;

    void zzn(int i, String str, Bundle bundle, zzg zzgVar) throws RemoteException;

    int zzr(int i, String str, String str2) throws RemoteException;
}
