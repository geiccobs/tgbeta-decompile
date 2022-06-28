package com.google.android.gms.wearable.internal;

import android.net.Uri;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.MessageOptions;
import com.google.android.gms.wearable.PutDataRequest;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public final class zzeu extends com.google.android.gms.internal.wearable.zza implements IInterface {
    public zzeu(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.wearable.internal.IWearableService");
    }

    public final void zzA(zzeq zzeqVar, String str, ParcelFileDescriptor parcelFileDescriptor, long j, long j2) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.wearable.zzc.zzd(zza, zzeqVar);
        zza.writeString(str);
        com.google.android.gms.internal.wearable.zzc.zzc(zza, parcelFileDescriptor);
        zza.writeLong(j);
        zza.writeLong(j2);
        zzG(39, zza);
    }

    public final void zzd(zzeq zzeqVar, PutDataRequest putDataRequest) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.wearable.zzc.zzd(zza, zzeqVar);
        com.google.android.gms.internal.wearable.zzc.zzc(zza, putDataRequest);
        zzG(6, zza);
    }

    public final void zze(zzeq zzeqVar, Uri uri) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.wearable.zzc.zzd(zza, zzeqVar);
        com.google.android.gms.internal.wearable.zzc.zzc(zza, uri);
        zzG(7, zza);
    }

    public final void zzf(zzeq zzeqVar) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.wearable.zzc.zzd(zza, zzeqVar);
        zzG(8, zza);
    }

    public final void zzg(zzeq zzeqVar, Uri uri, int i) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.wearable.zzc.zzd(zza, zzeqVar);
        com.google.android.gms.internal.wearable.zzc.zzc(zza, uri);
        zza.writeInt(i);
        zzG(40, zza);
    }

    public final void zzh(zzeq zzeqVar, Uri uri, int i) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.wearable.zzc.zzd(zza, zzeqVar);
        com.google.android.gms.internal.wearable.zzc.zzc(zza, uri);
        zza.writeInt(i);
        zzG(41, zza);
    }

    public final void zzi(zzeq zzeqVar, String str, String str2, byte[] bArr) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.wearable.zzc.zzd(zza, zzeqVar);
        zza.writeString(str);
        zza.writeString(str2);
        zza.writeByteArray(bArr);
        zzG(12, zza);
    }

    public final void zzj(zzeq zzeqVar, String str, String str2, byte[] bArr, MessageOptions messageOptions) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.wearable.zzc.zzd(zza, zzeqVar);
        zza.writeString(str);
        zza.writeString(str2);
        zza.writeByteArray(bArr);
        com.google.android.gms.internal.wearable.zzc.zzc(zza, messageOptions);
        zzG(59, zza);
    }

    public final void zzk(zzeq zzeqVar, Asset asset) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.wearable.zzc.zzd(zza, zzeqVar);
        com.google.android.gms.internal.wearable.zzc.zzc(zza, asset);
        zzG(13, zza);
    }

    public final void zzl(zzeq zzeqVar) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.wearable.zzc.zzd(zza, zzeqVar);
        zzG(14, zza);
    }

    public final void zzm(zzeq zzeqVar) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.wearable.zzc.zzd(zza, zzeqVar);
        zzG(15, zza);
    }

    public final void zzn(zzeq zzeqVar, String str) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.wearable.zzc.zzd(zza, zzeqVar);
        zza.writeString(str);
        zzG(63, zza);
    }

    public final void zzo(zzeq zzeqVar, String str, int i) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.wearable.zzc.zzd(zza, zzeqVar);
        zza.writeString(str);
        zza.writeInt(i);
        zzG(42, zza);
    }

    public final void zzp(zzeq zzeqVar, int i) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.wearable.zzc.zzd(zza, zzeqVar);
        zza.writeInt(i);
        zzG(43, zza);
    }

    public final void zzq(zzeq zzeqVar, String str) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.wearable.zzc.zzd(zza, zzeqVar);
        zza.writeString(str);
        zzG(46, zza);
    }

    public final void zzr(zzeq zzeqVar, String str) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.wearable.zzc.zzd(zza, zzeqVar);
        zza.writeString(str);
        zzG(47, zza);
    }

    public final void zzs(zzeq zzeqVar, zzd zzdVar) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.wearable.zzc.zzd(zza, zzeqVar);
        com.google.android.gms.internal.wearable.zzc.zzc(zza, zzdVar);
        zzG(16, zza);
    }

    public final void zzt(zzeq zzeqVar, zzgg zzggVar) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.wearable.zzc.zzd(zza, zzeqVar);
        com.google.android.gms.internal.wearable.zzc.zzc(zza, zzggVar);
        zzG(17, zza);
    }

    public final void zzu(zzeq zzeqVar, String str, String str2) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.wearable.zzc.zzd(zza, zzeqVar);
        zza.writeString(str);
        zza.writeString(str2);
        zzG(31, zza);
    }

    public final void zzv(zzeq zzeqVar, String str) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.wearable.zzc.zzd(zza, zzeqVar);
        zza.writeString(str);
        zzG(32, zza);
    }

    public final void zzw(zzeq zzeqVar, String str, int i) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.wearable.zzc.zzd(zza, zzeqVar);
        zza.writeString(str);
        zza.writeInt(i);
        zzG(33, zza);
    }

    public final void zzx(zzeq zzeqVar, zzen zzenVar, String str) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.wearable.zzc.zzd(zza, zzeqVar);
        com.google.android.gms.internal.wearable.zzc.zzd(zza, zzenVar);
        zza.writeString(str);
        zzG(34, zza);
    }

    public final void zzy(zzeq zzeqVar, zzen zzenVar, String str) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.wearable.zzc.zzd(zza, zzeqVar);
        com.google.android.gms.internal.wearable.zzc.zzd(zza, zzenVar);
        zza.writeString(str);
        zzG(35, zza);
    }

    public final void zzz(zzeq zzeqVar, String str, ParcelFileDescriptor parcelFileDescriptor) throws RemoteException {
        Parcel zza = zza();
        com.google.android.gms.internal.wearable.zzc.zzd(zza, zzeqVar);
        zza.writeString(str);
        com.google.android.gms.internal.wearable.zzc.zzc(zza, parcelFileDescriptor);
        zzG(38, zza);
    }
}
