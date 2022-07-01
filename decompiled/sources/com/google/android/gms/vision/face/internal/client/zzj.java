package com.google.android.gms.vision.face.internal.client;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.internal.vision.zzs;
/* compiled from: com.google.android.gms:play-services-vision@@20.1.3 */
/* loaded from: classes.dex */
public final class zzj extends com.google.android.gms.internal.vision.zzb implements zzh {
    public zzj(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.vision.face.internal.client.INativeFaceDetector");
    }

    @Override // com.google.android.gms.vision.face.internal.client.zzh
    public final FaceParcel[] zza(IObjectWrapper iObjectWrapper, zzs zzsVar) throws RemoteException {
        Parcel a_ = a_();
        com.google.android.gms.internal.vision.zzd.zza(a_, iObjectWrapper);
        com.google.android.gms.internal.vision.zzd.zza(a_, zzsVar);
        Parcel zza = zza(1, a_);
        FaceParcel[] faceParcelArr = (FaceParcel[]) zza.createTypedArray(FaceParcel.CREATOR);
        zza.recycle();
        return faceParcelArr;
    }

    @Override // com.google.android.gms.vision.face.internal.client.zzh
    public final FaceParcel[] zza(IObjectWrapper iObjectWrapper, IObjectWrapper iObjectWrapper2, IObjectWrapper iObjectWrapper3, int i, int i2, int i3, int i4, int i5, int i6, zzs zzsVar) throws RemoteException {
        Parcel a_ = a_();
        com.google.android.gms.internal.vision.zzd.zza(a_, iObjectWrapper);
        com.google.android.gms.internal.vision.zzd.zza(a_, iObjectWrapper2);
        com.google.android.gms.internal.vision.zzd.zza(a_, iObjectWrapper3);
        a_.writeInt(i);
        a_.writeInt(i2);
        a_.writeInt(i3);
        a_.writeInt(i4);
        a_.writeInt(i5);
        a_.writeInt(i6);
        com.google.android.gms.internal.vision.zzd.zza(a_, zzsVar);
        Parcel zza = zza(4, a_);
        FaceParcel[] faceParcelArr = (FaceParcel[]) zza.createTypedArray(FaceParcel.CREATOR);
        zza.recycle();
        return faceParcelArr;
    }

    @Override // com.google.android.gms.vision.face.internal.client.zzh
    public final void zza() throws RemoteException {
        zzb(3, a_());
    }
}
