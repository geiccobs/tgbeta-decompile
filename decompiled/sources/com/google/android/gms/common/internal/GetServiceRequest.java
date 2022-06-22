package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.RecentlyNonNull;
import com.google.android.gms.common.Feature;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.IAccountAccessor;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes.dex */
public class GetServiceRequest extends AbstractSafeParcelable {
    @RecentlyNonNull
    public static final Parcelable.Creator<GetServiceRequest> CREATOR = new zze();
    String zza;
    IBinder zzb;
    Scope[] zzc;
    Bundle zzd;
    Account zze;
    Feature[] zzf;
    Feature[] zzg;
    boolean zzh;
    private final int zzi;
    private final int zzj;
    private int zzk;
    private boolean zzl;
    private int zzm;
    private final String zzn;

    public GetServiceRequest(int i, String str) {
        this.zzi = 6;
        this.zzk = GoogleApiAvailabilityLight.GOOGLE_PLAY_SERVICES_VERSION_CODE;
        this.zzj = i;
        this.zzl = true;
        this.zzn = str;
    }

    public GetServiceRequest(int i, int i2, int i3, String str, IBinder iBinder, Scope[] scopeArr, Bundle bundle, Account account, Feature[] featureArr, Feature[] featureArr2, boolean z, int i4, boolean z2, String str2) {
        this.zzi = i;
        this.zzj = i2;
        this.zzk = i3;
        if ("com.google.android.gms".equals(str)) {
            this.zza = "com.google.android.gms";
        } else {
            this.zza = str;
        }
        if (i < 2) {
            this.zze = iBinder != null ? AccountAccessor.getAccountBinderSafe(IAccountAccessor.Stub.asInterface(iBinder)) : null;
        } else {
            this.zzb = iBinder;
            this.zze = account;
        }
        this.zzc = scopeArr;
        this.zzd = bundle;
        this.zzf = featureArr;
        this.zzg = featureArr2;
        this.zzl = z;
        this.zzm = i4;
        this.zzh = z2;
        this.zzn = str2;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(@RecentlyNonNull Parcel parcel, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
        SafeParcelWriter.writeInt(parcel, 1, this.zzi);
        SafeParcelWriter.writeInt(parcel, 2, this.zzj);
        SafeParcelWriter.writeInt(parcel, 3, this.zzk);
        SafeParcelWriter.writeString(parcel, 4, this.zza, false);
        SafeParcelWriter.writeIBinder(parcel, 5, this.zzb, false);
        SafeParcelWriter.writeTypedArray(parcel, 6, this.zzc, i, false);
        SafeParcelWriter.writeBundle(parcel, 7, this.zzd, false);
        SafeParcelWriter.writeParcelable(parcel, 8, this.zze, i, false);
        SafeParcelWriter.writeTypedArray(parcel, 10, this.zzf, i, false);
        SafeParcelWriter.writeTypedArray(parcel, 11, this.zzg, i, false);
        SafeParcelWriter.writeBoolean(parcel, 12, this.zzl);
        SafeParcelWriter.writeInt(parcel, 13, this.zzm);
        SafeParcelWriter.writeBoolean(parcel, 14, this.zzh);
        SafeParcelWriter.writeString(parcel, 15, this.zzn, false);
        SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
    }
}
