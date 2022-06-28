package com.google.android.gms.internal.maps;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.maps.model.Cap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;
import java.util.ArrayList;
import java.util.List;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes3.dex */
public final class zzab extends zza implements zzad {
    public zzab(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.maps.model.internal.IPolylineDelegate");
    }

    @Override // com.google.android.gms.internal.maps.zzad
    public final int zzA() throws RemoteException {
        Parcel zzH = zzH(24, zza());
        int readInt = zzH.readInt();
        zzH.recycle();
        return readInt;
    }

    @Override // com.google.android.gms.internal.maps.zzad
    public final void zzB(List<PatternItem> list) throws RemoteException {
        Parcel zza = zza();
        zza.writeTypedList(list);
        zzc(25, zza);
    }

    @Override // com.google.android.gms.internal.maps.zzad
    public final List<PatternItem> zzC() throws RemoteException {
        Parcel zzH = zzH(26, zza());
        ArrayList createTypedArrayList = zzH.createTypedArrayList(PatternItem.CREATOR);
        zzH.recycle();
        return createTypedArrayList;
    }

    @Override // com.google.android.gms.internal.maps.zzad
    public final void zzD(IObjectWrapper iObjectWrapper) throws RemoteException {
        Parcel zza = zza();
        zzc.zzf(zza, iObjectWrapper);
        zzc(27, zza);
    }

    @Override // com.google.android.gms.internal.maps.zzad
    public final IObjectWrapper zzE() throws RemoteException {
        Parcel zzH = zzH(28, zza());
        IObjectWrapper asInterface = IObjectWrapper.Stub.asInterface(zzH.readStrongBinder());
        zzH.recycle();
        return asInterface;
    }

    @Override // com.google.android.gms.internal.maps.zzad
    public final void zzd() throws RemoteException {
        zzc(1, zza());
    }

    @Override // com.google.android.gms.internal.maps.zzad
    public final String zze() throws RemoteException {
        Parcel zzH = zzH(2, zza());
        String readString = zzH.readString();
        zzH.recycle();
        return readString;
    }

    @Override // com.google.android.gms.internal.maps.zzad
    public final void zzf(List<LatLng> list) throws RemoteException {
        Parcel zza = zza();
        zza.writeTypedList(list);
        zzc(3, zza);
    }

    @Override // com.google.android.gms.internal.maps.zzad
    public final List<LatLng> zzg() throws RemoteException {
        Parcel zzH = zzH(4, zza());
        ArrayList createTypedArrayList = zzH.createTypedArrayList(LatLng.CREATOR);
        zzH.recycle();
        return createTypedArrayList;
    }

    @Override // com.google.android.gms.internal.maps.zzad
    public final void zzh(float f) throws RemoteException {
        Parcel zza = zza();
        zza.writeFloat(f);
        zzc(5, zza);
    }

    @Override // com.google.android.gms.internal.maps.zzad
    public final float zzi() throws RemoteException {
        Parcel zzH = zzH(6, zza());
        float readFloat = zzH.readFloat();
        zzH.recycle();
        return readFloat;
    }

    @Override // com.google.android.gms.internal.maps.zzad
    public final void zzj(int i) throws RemoteException {
        Parcel zza = zza();
        zza.writeInt(i);
        zzc(7, zza);
    }

    @Override // com.google.android.gms.internal.maps.zzad
    public final int zzk() throws RemoteException {
        Parcel zzH = zzH(8, zza());
        int readInt = zzH.readInt();
        zzH.recycle();
        return readInt;
    }

    @Override // com.google.android.gms.internal.maps.zzad
    public final void zzl(float f) throws RemoteException {
        Parcel zza = zza();
        zza.writeFloat(f);
        zzc(9, zza);
    }

    @Override // com.google.android.gms.internal.maps.zzad
    public final float zzm() throws RemoteException {
        Parcel zzH = zzH(10, zza());
        float readFloat = zzH.readFloat();
        zzH.recycle();
        return readFloat;
    }

    @Override // com.google.android.gms.internal.maps.zzad
    public final void zzn(boolean z) throws RemoteException {
        Parcel zza = zza();
        zzc.zzb(zza, z);
        zzc(11, zza);
    }

    @Override // com.google.android.gms.internal.maps.zzad
    public final boolean zzo() throws RemoteException {
        Parcel zzH = zzH(12, zza());
        boolean zza = zzc.zza(zzH);
        zzH.recycle();
        return zza;
    }

    @Override // com.google.android.gms.internal.maps.zzad
    public final void zzp(boolean z) throws RemoteException {
        Parcel zza = zza();
        zzc.zzb(zza, z);
        zzc(13, zza);
    }

    @Override // com.google.android.gms.internal.maps.zzad
    public final boolean zzq() throws RemoteException {
        Parcel zzH = zzH(14, zza());
        boolean zza = zzc.zza(zzH);
        zzH.recycle();
        return zza;
    }

    @Override // com.google.android.gms.internal.maps.zzad
    public final boolean zzr(zzad zzadVar) throws RemoteException {
        Parcel zza = zza();
        zzc.zzf(zza, zzadVar);
        Parcel zzH = zzH(15, zza);
        boolean zza2 = zzc.zza(zzH);
        zzH.recycle();
        return zza2;
    }

    @Override // com.google.android.gms.internal.maps.zzad
    public final int zzs() throws RemoteException {
        Parcel zzH = zzH(16, zza());
        int readInt = zzH.readInt();
        zzH.recycle();
        return readInt;
    }

    @Override // com.google.android.gms.internal.maps.zzad
    public final void zzt(boolean z) throws RemoteException {
        Parcel zza = zza();
        zzc.zzb(zza, z);
        zzc(17, zza);
    }

    @Override // com.google.android.gms.internal.maps.zzad
    public final boolean zzu() throws RemoteException {
        Parcel zzH = zzH(18, zza());
        boolean zza = zzc.zza(zzH);
        zzH.recycle();
        return zza;
    }

    @Override // com.google.android.gms.internal.maps.zzad
    public final void zzv(Cap cap) throws RemoteException {
        Parcel zza = zza();
        zzc.zzd(zza, cap);
        zzc(19, zza);
    }

    @Override // com.google.android.gms.internal.maps.zzad
    public final Cap zzw() throws RemoteException {
        Parcel zzH = zzH(20, zza());
        Cap cap = (Cap) zzc.zzc(zzH, Cap.CREATOR);
        zzH.recycle();
        return cap;
    }

    @Override // com.google.android.gms.internal.maps.zzad
    public final void zzx(Cap cap) throws RemoteException {
        Parcel zza = zza();
        zzc.zzd(zza, cap);
        zzc(21, zza);
    }

    @Override // com.google.android.gms.internal.maps.zzad
    public final Cap zzy() throws RemoteException {
        Parcel zzH = zzH(22, zza());
        Cap cap = (Cap) zzc.zzc(zzH, Cap.CREATOR);
        zzH.recycle();
        return cap;
    }

    @Override // com.google.android.gms.internal.maps.zzad
    public final void zzz(int i) throws RemoteException {
        Parcel zza = zza();
        zza.writeInt(i);
        zzc(23, zza);
    }
}
