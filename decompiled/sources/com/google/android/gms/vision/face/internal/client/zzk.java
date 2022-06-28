package com.google.android.gms.vision.face.internal.client;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
/* compiled from: com.google.android.gms:play-services-vision@@20.1.3 */
/* loaded from: classes3.dex */
public final class zzk extends com.google.android.gms.internal.vision.zzb implements zzi {
    public zzk(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.vision.face.internal.client.INativeFaceDetectorCreator");
    }

    @Override // com.google.android.gms.vision.face.internal.client.zzi
    public final zzh newFaceDetector(IObjectWrapper iObjectWrapper, zzf zzfVar) throws RemoteException {
        zzh zzhVar;
        Parcel a_ = a_();
        com.google.android.gms.internal.vision.zzd.zza(a_, iObjectWrapper);
        com.google.android.gms.internal.vision.zzd.zza(a_, zzfVar);
        Parcel zza = zza(1, a_);
        IBinder readStrongBinder = zza.readStrongBinder();
        if (readStrongBinder == null) {
            zzhVar = null;
        } else {
            IInterface queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.vision.face.internal.client.INativeFaceDetector");
            if (queryLocalInterface instanceof zzh) {
                zzhVar = (zzh) queryLocalInterface;
            } else {
                zzhVar = new zzj(readStrongBinder);
            }
        }
        zza.recycle();
        return zzhVar;
    }
}
