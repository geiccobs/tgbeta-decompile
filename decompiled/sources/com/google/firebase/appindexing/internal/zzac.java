package com.google.firebase.appindexing.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.internal.icing.zzbp;
import com.google.firebase.appindexing.Indexable;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class zzac extends AbstractSafeParcelable implements Indexable.Metadata {
    public static final Parcelable.Creator<zzac> CREATOR = new zzx();
    private final boolean zza;
    private final int zzb;
    private final String zzc;
    private final Bundle zzd;
    private final Bundle zze;

    public zzac(boolean z, int i, String str, Bundle bundle, Bundle bundle2) {
        this.zza = z;
        this.zzb = i;
        this.zzc = str;
        this.zzd = bundle == null ? new Bundle() : bundle;
        bundle2 = bundle2 == null ? new Bundle() : bundle2;
        this.zze = bundle2;
        ClassLoader classLoader = getClass().getClassLoader();
        zzbp.zza(classLoader);
        bundle2.setClassLoader(classLoader);
    }

    public final boolean equals(Object obj) {
        boolean zze;
        boolean zze2;
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof zzac)) {
            return false;
        }
        zzac zzacVar = (zzac) obj;
        if (Objects.equal(Boolean.valueOf(this.zza), Boolean.valueOf(zzacVar.zza)) && Objects.equal(Integer.valueOf(this.zzb), Integer.valueOf(zzacVar.zzb)) && Objects.equal(this.zzc, zzacVar.zzc)) {
            zze = Thing.zze(this.zzd, zzacVar.zzd);
            if (zze) {
                zze2 = Thing.zze(this.zze, zzacVar.zze);
                if (zze2) {
                    return true;
                }
            }
        }
        return false;
    }

    public final int hashCode() {
        int zzf;
        int zzf2;
        zzf = Thing.zzf(this.zzd);
        zzf2 = Thing.zzf(this.zze);
        return Objects.hashCode(Boolean.valueOf(this.zza), Integer.valueOf(this.zzb), this.zzc, Integer.valueOf(zzf), Integer.valueOf(zzf2));
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("worksOffline: ");
        sb.append(this.zza);
        sb.append(", score: ");
        sb.append(this.zzb);
        if (!this.zzc.isEmpty()) {
            sb.append(", accountEmail: ");
            sb.append(this.zzc);
        }
        Bundle bundle = this.zzd;
        if (bundle != null && !bundle.isEmpty()) {
            sb.append(", Properties { ");
            Thing.zzd(this.zzd, sb);
            sb.append("}");
        }
        if (!this.zze.isEmpty()) {
            sb.append(", embeddingProperties { ");
            Thing.zzd(this.zze, sb);
            sb.append("}");
        }
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
        SafeParcelWriter.writeBoolean(parcel, 1, this.zza);
        SafeParcelWriter.writeInt(parcel, 2, this.zzb);
        SafeParcelWriter.writeString(parcel, 3, this.zzc, false);
        SafeParcelWriter.writeBundle(parcel, 4, this.zzd, false);
        SafeParcelWriter.writeBundle(parcel, 5, this.zze, false);
        SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
    }
}
