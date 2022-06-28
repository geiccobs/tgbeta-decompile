package com.google.firebase.appindexing.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class zzb extends AbstractSafeParcelable {
    public static final Parcelable.Creator<zzb> CREATOR = new zzy();
    private int zza;
    private final boolean zzb;
    private final String zzc;
    private final String zzd;
    private final byte[] zze;
    private final boolean zzf;

    public zzb(int i, boolean z, String str, String str2, byte[] bArr, boolean z2) {
        this.zza = i;
        this.zzb = z;
        this.zzc = str;
        this.zzd = str2;
        this.zze = bArr;
        this.zzf = z2;
    }

    public final String toString() {
        byte[] bArr;
        StringBuilder sb = new StringBuilder();
        sb.append("MetadataImpl { { eventStatus: '");
        sb.append(this.zza);
        sb.append("' } { uploadable: '");
        sb.append(this.zzb);
        sb.append("' } ");
        if (this.zzc != null) {
            sb.append("{ completionToken: '");
            sb.append(this.zzc);
            sb.append("' } ");
        }
        if (this.zzd != null) {
            sb.append("{ accountName: '");
            sb.append(this.zzd);
            sb.append("' } ");
        }
        if (this.zze != null) {
            sb.append("{ ssbContext: [ ");
            for (byte b : this.zze) {
                sb.append("0x");
                sb.append(Integer.toHexString(b));
                sb.append(" ");
            }
            sb.append("] } ");
        }
        sb.append("{ contextOnly: '");
        sb.append(this.zzf);
        sb.append("' } }");
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
        SafeParcelWriter.writeInt(parcel, 1, this.zza);
        SafeParcelWriter.writeBoolean(parcel, 2, this.zzb);
        SafeParcelWriter.writeString(parcel, 3, this.zzc, false);
        SafeParcelWriter.writeString(parcel, 4, this.zzd, false);
        SafeParcelWriter.writeByteArray(parcel, 5, this.zze, false);
        SafeParcelWriter.writeBoolean(parcel, 6, this.zzf);
        SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
    }

    public final void zza(int i) {
        this.zza = i;
    }

    public zzb(boolean z, String str, String str2, byte[] bArr, boolean z2) {
        this.zza = 0;
        this.zzb = z;
        this.zzc = null;
        this.zzd = null;
        this.zze = null;
        this.zzf = false;
    }
}
