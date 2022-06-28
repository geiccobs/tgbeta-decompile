package com.google.firebase.appindexing.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.internal.icing.zzbp;
import com.google.firebase.appindexing.Action;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class zzc extends AbstractSafeParcelable implements Action {
    public static final Parcelable.Creator<zzc> CREATOR = new zzd();
    private final String zza;
    private final String zzb;
    private final String zzc;
    private final String zzd;
    private final zzb zze;
    private final String zzf;
    private final Bundle zzg;

    public zzc(String str, String str2, String str3, String str4, zzb zzbVar, String str5, Bundle bundle) {
        this.zza = str;
        this.zzb = str2;
        this.zzc = str3;
        this.zzd = str4;
        this.zze = zzbVar;
        this.zzf = str5;
        if (bundle != null) {
            this.zzg = bundle;
        } else {
            this.zzg = Bundle.EMPTY;
        }
        ClassLoader classLoader = getClass().getClassLoader();
        zzbp.zza(classLoader);
        this.zzg.setClassLoader(classLoader);
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ActionImpl { { actionType: '");
        sb.append(this.zza);
        sb.append("' } { objectName: '");
        sb.append(this.zzb);
        sb.append("' } { objectUrl: '");
        sb.append(this.zzc);
        sb.append("' } ");
        if (this.zzd != null) {
            sb.append("{ objectSameAs: '");
            sb.append(this.zzd);
            sb.append("' } ");
        }
        if (this.zze != null) {
            sb.append("{ metadata: '");
            sb.append(this.zze.toString());
            sb.append("' } ");
        }
        if (this.zzf != null) {
            sb.append("{ actionStatus: '");
            sb.append(this.zzf);
            sb.append("' } ");
        }
        if (!this.zzg.isEmpty()) {
            sb.append("{ ");
            sb.append(this.zzg);
            sb.append(" } ");
        }
        sb.append("}");
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
        SafeParcelWriter.writeString(parcel, 1, this.zza, false);
        SafeParcelWriter.writeString(parcel, 2, this.zzb, false);
        SafeParcelWriter.writeString(parcel, 3, this.zzc, false);
        SafeParcelWriter.writeString(parcel, 4, this.zzd, false);
        SafeParcelWriter.writeParcelable(parcel, 5, this.zze, i, false);
        SafeParcelWriter.writeString(parcel, 6, this.zzf, false);
        SafeParcelWriter.writeBundle(parcel, 7, this.zzg, false);
        SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
    }

    public final zzb zza() {
        return this.zze;
    }
}
