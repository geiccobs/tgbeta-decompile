package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import javax.annotation.Nullable;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes.dex */
public final class zzl extends AbstractSafeParcelable implements com.google.android.gms.wearable.zzb {
    public static final Parcelable.Creator<zzl> CREATOR = new zzm();
    private final int zza;
    private final String zzb;
    @Nullable
    private final String zzc;
    private final String zzd;
    private final String zze;
    private final String zzf;
    @Nullable
    private final String zzg;
    private final byte zzh;
    private final byte zzi;
    private final byte zzj;
    private final byte zzk;
    @Nullable
    private final String zzl;

    public zzl(int i, String str, @Nullable String str2, String str3, String str4, String str5, @Nullable String str6, byte b, byte b2, byte b3, byte b4, @Nullable String str7) {
        this.zza = i;
        this.zzb = str;
        this.zzc = str2;
        this.zzd = str3;
        this.zze = str4;
        this.zzf = str5;
        this.zzg = str6;
        this.zzh = b;
        this.zzi = b2;
        this.zzj = b3;
        this.zzk = b4;
        this.zzl = str7;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        zzl zzlVar = (zzl) obj;
        if (this.zza != zzlVar.zza || this.zzh != zzlVar.zzh || this.zzi != zzlVar.zzi || this.zzj != zzlVar.zzj || this.zzk != zzlVar.zzk || !this.zzb.equals(zzlVar.zzb)) {
            return false;
        }
        String str = this.zzc;
        if (str == null ? zzlVar.zzc != null : !str.equals(zzlVar.zzc)) {
            return false;
        }
        if (!this.zzd.equals(zzlVar.zzd) || !this.zze.equals(zzlVar.zze) || !this.zzf.equals(zzlVar.zzf)) {
            return false;
        }
        String str2 = this.zzg;
        if (str2 == null ? zzlVar.zzg != null : !str2.equals(zzlVar.zzg)) {
            return false;
        }
        String str3 = this.zzl;
        if (str3 != null) {
            return str3.equals(zzlVar.zzl);
        }
        return zzlVar.zzl == null;
    }

    public final int hashCode() {
        int i;
        int i2;
        int hashCode = (((this.zza + 31) * 31) + this.zzb.hashCode()) * 31;
        String str = this.zzc;
        int i3 = 0;
        if (str != null) {
            i = str.hashCode();
        } else {
            i = 0;
        }
        int hashCode2 = (((((((hashCode + i) * 31) + this.zzd.hashCode()) * 31) + this.zze.hashCode()) * 31) + this.zzf.hashCode()) * 31;
        String str2 = this.zzg;
        if (str2 != null) {
            i2 = str2.hashCode();
        } else {
            i2 = 0;
        }
        int i4 = (((((((((hashCode2 + i2) * 31) + this.zzh) * 31) + this.zzi) * 31) + this.zzj) * 31) + this.zzk) * 31;
        String str3 = this.zzl;
        if (str3 != null) {
            i3 = str3.hashCode();
        }
        return i4 + i3;
    }

    public final String toString() {
        int i = this.zza;
        String str = this.zzb;
        String str2 = this.zzc;
        String str3 = this.zzd;
        String str4 = this.zze;
        String str5 = this.zzf;
        String str6 = this.zzg;
        byte b = this.zzh;
        byte b2 = this.zzi;
        byte b3 = this.zzj;
        byte b4 = this.zzk;
        String str7 = this.zzl;
        int length = String.valueOf(str).length();
        int length2 = String.valueOf(str2).length();
        int length3 = String.valueOf(str3).length();
        int length4 = String.valueOf(str4).length();
        int length5 = String.valueOf(str5).length();
        StringBuilder sb = new StringBuilder(length + 211 + length2 + length3 + length4 + length5 + String.valueOf(str6).length() + String.valueOf(str7).length());
        sb.append("AncsNotificationParcelable{, id=");
        sb.append(i);
        sb.append(", appId='");
        sb.append(str);
        sb.append("', dateTime='");
        sb.append(str2);
        sb.append("', notificationText='");
        sb.append(str3);
        sb.append("', title='");
        sb.append(str4);
        sb.append("', subtitle='");
        sb.append(str5);
        sb.append("', displayName='");
        sb.append(str6);
        sb.append("', eventId=");
        sb.append((int) b);
        sb.append(", eventFlags=");
        sb.append((int) b2);
        sb.append(", categoryId=");
        sb.append((int) b3);
        sb.append(", categoryCount=");
        sb.append((int) b4);
        sb.append(", packageName='");
        sb.append(str7);
        sb.append("'}");
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
        SafeParcelWriter.writeInt(parcel, 2, this.zza);
        SafeParcelWriter.writeString(parcel, 3, this.zzb, false);
        SafeParcelWriter.writeString(parcel, 4, this.zzc, false);
        SafeParcelWriter.writeString(parcel, 5, this.zzd, false);
        SafeParcelWriter.writeString(parcel, 6, this.zze, false);
        SafeParcelWriter.writeString(parcel, 7, this.zzf, false);
        String str = this.zzg;
        if (str == null) {
            str = this.zzb;
        }
        SafeParcelWriter.writeString(parcel, 8, str, false);
        SafeParcelWriter.writeByte(parcel, 9, this.zzh);
        SafeParcelWriter.writeByte(parcel, 10, this.zzi);
        SafeParcelWriter.writeByte(parcel, 11, this.zzj);
        SafeParcelWriter.writeByte(parcel, 12, this.zzk);
        SafeParcelWriter.writeString(parcel, 13, this.zzl, false);
        SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
    }
}
