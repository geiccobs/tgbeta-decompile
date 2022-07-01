package com.google.android.gms.internal.icing;

import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import com.google.android.gms.common.api.Status;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes.dex */
public abstract class zzab extends zzb implements zzac {
    public zzab() {
        super("com.google.android.gms.appdatasearch.internal.ILightweightAppDataSearchCallbacks");
    }

    @Override // com.google.android.gms.internal.icing.zzb
    protected final boolean zza(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        if (i == 1) {
            zzb((Status) zzc.zza(parcel, Status.CREATOR));
        } else if (i == 2) {
            Status status = (Status) zzc.zza(parcel, Status.CREATOR);
            ParcelFileDescriptor parcelFileDescriptor = (ParcelFileDescriptor) zzc.zza(parcel, ParcelFileDescriptor.CREATOR);
        } else if (i != 4) {
            return false;
        } else {
            zzo zzoVar = (zzo) zzc.zza(parcel, zzo.CREATOR);
        }
        return true;
    }
}
