package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataHolder;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public abstract class zzep extends com.google.android.gms.internal.wearable.zzb implements zzeq {
    public zzep() {
        super("com.google.android.gms.wearable.internal.IWearableCallbacks");
    }

    @Override // com.google.android.gms.internal.wearable.zzb
    protected final boolean zza(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        switch (i) {
            case 2:
                zzb((zzdy) com.google.android.gms.internal.wearable.zzc.zzb(parcel, zzdy.CREATOR));
                break;
            case 3:
                zzg((zzge) com.google.android.gms.internal.wearable.zzc.zzb(parcel, zzge.CREATOR));
                break;
            case 4:
                zzh((zzee) com.google.android.gms.internal.wearable.zzc.zzb(parcel, zzee.CREATOR));
                break;
            case 5:
                zzi((DataHolder) com.google.android.gms.internal.wearable.zzc.zzb(parcel, DataHolder.CREATOR));
                break;
            case 6:
                zzj((zzdg) com.google.android.gms.internal.wearable.zzc.zzb(parcel, zzdg.CREATOR));
                break;
            case 7:
                zzk((zzgm) com.google.android.gms.internal.wearable.zzc.zzb(parcel, zzgm.CREATOR));
                break;
            case 8:
                zzm((zzei) com.google.android.gms.internal.wearable.zzc.zzb(parcel, zzei.CREATOR));
                break;
            case 9:
                zzn((zzek) com.google.android.gms.internal.wearable.zzc.zzb(parcel, zzek.CREATOR));
                break;
            case 10:
                zzo((zzec) com.google.android.gms.internal.wearable.zzc.zzb(parcel, zzec.CREATOR));
                break;
            case 11:
                zzx((Status) com.google.android.gms.internal.wearable.zzc.zzb(parcel, Status.CREATOR));
                break;
            case 12:
                zzy((zzgq) com.google.android.gms.internal.wearable.zzc.zzb(parcel, zzgq.CREATOR));
                break;
            case 13:
                zzc((zzea) com.google.android.gms.internal.wearable.zzc.zzb(parcel, zzea.CREATOR));
                break;
            case 14:
                zzq((zzfy) com.google.android.gms.internal.wearable.zzc.zzb(parcel, zzfy.CREATOR));
                break;
            case 15:
                zzr((zzbu) com.google.android.gms.internal.wearable.zzc.zzb(parcel, zzbu.CREATOR));
                break;
            case 16:
                zzs((zzbu) com.google.android.gms.internal.wearable.zzc.zzb(parcel, zzbu.CREATOR));
                break;
            case 17:
                zzt((zzdm) com.google.android.gms.internal.wearable.zzc.zzb(parcel, zzdm.CREATOR));
                break;
            case 18:
                zzu((zzdo) com.google.android.gms.internal.wearable.zzc.zzb(parcel, zzdo.CREATOR));
                break;
            case 19:
                zzv((zzbo) com.google.android.gms.internal.wearable.zzc.zzb(parcel, zzbo.CREATOR));
                break;
            case 20:
                zzw((zzbq) com.google.android.gms.internal.wearable.zzc.zzb(parcel, zzbq.CREATOR));
                break;
            case 21:
            case 24:
            case 25:
            case 31:
            case 32:
            case 33:
            default:
                return false;
            case 22:
                zzz((zzdk) com.google.android.gms.internal.wearable.zzc.zzb(parcel, zzdk.CREATOR));
                break;
            case 23:
                zzA((zzdi) com.google.android.gms.internal.wearable.zzc.zzb(parcel, zzdi.CREATOR));
                break;
            case 26:
                zzB((zzf) com.google.android.gms.internal.wearable.zzc.zzb(parcel, zzf.CREATOR));
                break;
            case 27:
                zzC((zzgi) com.google.android.gms.internal.wearable.zzc.zzb(parcel, zzgi.CREATOR));
                break;
            case 28:
                zzd((zzdr) com.google.android.gms.internal.wearable.zzc.zzb(parcel, zzdr.CREATOR));
                break;
            case NalUnitTypes.NAL_TYPE_RSV_VCL29 /* 29 */:
                zzf((zzdv) com.google.android.gms.internal.wearable.zzc.zzb(parcel, zzdv.CREATOR));
                break;
            case 30:
                zze((zzdt) com.google.android.gms.internal.wearable.zzc.zzb(parcel, zzdt.CREATOR));
                break;
            case 34:
                zzl((zzgk) com.google.android.gms.internal.wearable.zzc.zzb(parcel, zzgk.CREATOR));
                break;
            case 35:
                zzD((zzeg) com.google.android.gms.internal.wearable.zzc.zzb(parcel, zzeg.CREATOR));
                break;
            case 36:
                zzE((zzgc) com.google.android.gms.internal.wearable.zzc.zzb(parcel, zzgc.CREATOR));
                break;
            case 37:
                zzp((zzdw) com.google.android.gms.internal.wearable.zzc.zzb(parcel, zzdw.CREATOR));
                break;
        }
        parcel2.writeNoException();
        return true;
    }
}
