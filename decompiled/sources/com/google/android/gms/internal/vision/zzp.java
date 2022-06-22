package com.google.android.gms.internal.vision;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
/* compiled from: com.google.android.gms:play-services-vision@@20.1.3 */
/* loaded from: classes.dex */
public final class zzp extends zzb implements zzn {
    public zzp(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.vision.barcode.internal.client.INativeBarcodeDetectorCreator");
    }

    @Override // com.google.android.gms.internal.vision.zzn
    public final zzl zza(IObjectWrapper iObjectWrapper, zzk zzkVar) throws RemoteException {
        zzl zzlVar;
        Parcel a_ = a_();
        zzd.zza(a_, iObjectWrapper);
        zzd.zza(a_, zzkVar);
        Parcel zza = zza(1, a_);
        IBinder readStrongBinder = zza.readStrongBinder();
        if (readStrongBinder == null) {
            zzlVar = null;
        } else {
            IInterface queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.vision.barcode.internal.client.INativeBarcodeDetector");
            if (queryLocalInterface instanceof zzl) {
                zzlVar = (zzl) queryLocalInterface;
            } else {
                zzlVar = new zzo(readStrongBinder);
            }
        }
        zza.recycle();
        return zzlVar;
    }
}
