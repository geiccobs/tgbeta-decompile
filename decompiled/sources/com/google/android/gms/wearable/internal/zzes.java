package com.google.android.gms.wearable.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.common.data.DataHolder;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public abstract class zzes extends com.google.android.gms.internal.wearable.zzb implements zzet {
    public zzes() {
        super("com.google.android.gms.wearable.internal.IWearableListener");
    }

    @Override // com.google.android.gms.internal.wearable.zzb
    protected final boolean zza(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        zzeo zzeoVar;
        switch (i) {
            case 1:
                zzb((DataHolder) com.google.android.gms.internal.wearable.zzc.zzb(parcel, DataHolder.CREATOR));
                return true;
            case 2:
                zzc((zzfj) com.google.android.gms.internal.wearable.zzc.zzb(parcel, zzfj.CREATOR));
                return true;
            case 3:
                zzd((zzfw) com.google.android.gms.internal.wearable.zzc.zzb(parcel, zzfw.CREATOR));
                return true;
            case 4:
                zze((zzfw) com.google.android.gms.internal.wearable.zzc.zzb(parcel, zzfw.CREATOR));
                return true;
            case 5:
                zzf(parcel.createTypedArrayList(zzfw.CREATOR));
                return true;
            case 6:
                zzh((zzl) com.google.android.gms.internal.wearable.zzc.zzb(parcel, zzl.CREATOR));
                return true;
            case 7:
                zzj((zzax) com.google.android.gms.internal.wearable.zzc.zzb(parcel, zzax.CREATOR));
                return true;
            case 8:
                zzg((zzag) com.google.android.gms.internal.wearable.zzc.zzb(parcel, zzag.CREATOR));
                return true;
            case 9:
                zzi((zzi) com.google.android.gms.internal.wearable.zzc.zzb(parcel, zzi.CREATOR));
                return true;
            case 10:
            case 11:
            case 12:
            default:
                return false;
            case 13:
                zzfj zzfjVar = (zzfj) com.google.android.gms.internal.wearable.zzc.zzb(parcel, zzfj.CREATOR);
                IBinder readStrongBinder = parcel.readStrongBinder();
                if (readStrongBinder == null) {
                    zzeoVar = null;
                } else {
                    IInterface queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.wearable.internal.IRpcResponseCallback");
                    if (queryLocalInterface instanceof zzeo) {
                        zzeoVar = (zzeo) queryLocalInterface;
                    } else {
                        zzeoVar = new zzeo(readStrongBinder);
                    }
                }
                zzk(zzfjVar, zzeoVar);
                return true;
        }
    }
}
