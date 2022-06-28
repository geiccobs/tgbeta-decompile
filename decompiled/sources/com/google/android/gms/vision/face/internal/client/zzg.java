package com.google.android.gms.vision.face.internal.client;

import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.internal.vision.zzs;
/* compiled from: com.google.android.gms:play-services-vision@@20.1.3 */
/* loaded from: classes3.dex */
public abstract class zzg extends com.google.android.gms.internal.vision.zza implements zzh {
    public zzg() {
        super("com.google.android.gms.vision.face.internal.client.INativeFaceDetector");
    }

    @Override // com.google.android.gms.internal.vision.zza
    protected final boolean zza(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        switch (i) {
            case 1:
                FaceParcel[] zza = zza(IObjectWrapper.Stub.asInterface(parcel.readStrongBinder()), (zzs) com.google.android.gms.internal.vision.zzd.zza(parcel, zzs.CREATOR));
                parcel2.writeNoException();
                parcel2.writeTypedArray(zza, 1);
                break;
            case 2:
                boolean zza2 = zza(parcel.readInt());
                parcel2.writeNoException();
                com.google.android.gms.internal.vision.zzd.zza(parcel2, zza2);
                break;
            case 3:
                zza();
                parcel2.writeNoException();
                break;
            case 4:
                FaceParcel[] zza3 = zza(IObjectWrapper.Stub.asInterface(parcel.readStrongBinder()), IObjectWrapper.Stub.asInterface(parcel.readStrongBinder()), IObjectWrapper.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), (zzs) com.google.android.gms.internal.vision.zzd.zza(parcel, zzs.CREATOR));
                parcel2.writeNoException();
                parcel2.writeTypedArray(zza3, 1);
                break;
            default:
                return false;
        }
        return true;
    }
}
