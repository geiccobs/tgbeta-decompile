package com.google.android.gms.wearable;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public class ConnectionConfiguration extends AbstractSafeParcelable implements ReflectedParcelable {
    public static final Parcelable.Creator<ConnectionConfiguration> CREATOR = new zzd();
    private final String zza;
    private final String zzb;
    private final int zzc;
    private final int zzd;
    private final boolean zze;
    private volatile boolean zzf;
    private volatile String zzg;
    private boolean zzh;
    private String zzi;
    private String zzj;

    public ConnectionConfiguration(String str, String str2, int i, int i2, boolean z, boolean z2, String str3, boolean z3, String str4, String str5) {
        this.zza = str;
        this.zzb = str2;
        this.zzc = i;
        this.zzd = i2;
        this.zze = z;
        this.zzf = z2;
        this.zzg = str3;
        this.zzh = z3;
        this.zzi = str4;
        this.zzj = str5;
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof ConnectionConfiguration)) {
            return false;
        }
        ConnectionConfiguration connectionConfiguration = (ConnectionConfiguration) obj;
        return Objects.equal(this.zza, connectionConfiguration.zza) && Objects.equal(this.zzb, connectionConfiguration.zzb) && Objects.equal(Integer.valueOf(this.zzc), Integer.valueOf(connectionConfiguration.zzc)) && Objects.equal(Integer.valueOf(this.zzd), Integer.valueOf(connectionConfiguration.zzd)) && Objects.equal(Boolean.valueOf(this.zze), Boolean.valueOf(connectionConfiguration.zze)) && Objects.equal(Boolean.valueOf(this.zzh), Boolean.valueOf(connectionConfiguration.zzh));
    }

    public final int hashCode() {
        return Objects.hashCode(this.zza, this.zzb, Integer.valueOf(this.zzc), Integer.valueOf(this.zzd), Boolean.valueOf(this.zze), Boolean.valueOf(this.zzh));
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder("ConnectionConfiguration[ ");
        String valueOf = String.valueOf(this.zza);
        sb.append(valueOf.length() != 0 ? "Name=".concat(valueOf) : new String("Name="));
        String valueOf2 = String.valueOf(this.zzb);
        sb.append(valueOf2.length() != 0 ? ", Address=".concat(valueOf2) : new String(", Address="));
        int i = this.zzc;
        StringBuilder sb2 = new StringBuilder(18);
        sb2.append(", Type=");
        sb2.append(i);
        sb.append(sb2.toString());
        int i2 = this.zzd;
        StringBuilder sb3 = new StringBuilder(18);
        sb3.append(", Role=");
        sb3.append(i2);
        sb.append(sb3.toString());
        boolean z = this.zze;
        StringBuilder sb4 = new StringBuilder(15);
        sb4.append(", Enabled=");
        sb4.append(z);
        sb.append(sb4.toString());
        boolean z2 = this.zzf;
        StringBuilder sb5 = new StringBuilder(19);
        sb5.append(", IsConnected=");
        sb5.append(z2);
        sb.append(sb5.toString());
        String valueOf3 = String.valueOf(this.zzg);
        sb.append(valueOf3.length() != 0 ? ", PeerNodeId=".concat(valueOf3) : new String(", PeerNodeId="));
        boolean z3 = this.zzh;
        StringBuilder sb6 = new StringBuilder(20);
        sb6.append(", BtlePriority=");
        sb6.append(z3);
        sb.append(sb6.toString());
        String valueOf4 = String.valueOf(this.zzi);
        sb.append(valueOf4.length() != 0 ? ", NodeId=".concat(valueOf4) : new String(", NodeId="));
        String valueOf5 = String.valueOf(this.zzj);
        sb.append(valueOf5.length() != 0 ? ", PackageName=".concat(valueOf5) : new String(", PackageName="));
        sb.append("]");
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
        SafeParcelWriter.writeString(parcel, 2, this.zza, false);
        SafeParcelWriter.writeString(parcel, 3, this.zzb, false);
        SafeParcelWriter.writeInt(parcel, 4, this.zzc);
        SafeParcelWriter.writeInt(parcel, 5, this.zzd);
        SafeParcelWriter.writeBoolean(parcel, 6, this.zze);
        SafeParcelWriter.writeBoolean(parcel, 7, this.zzf);
        SafeParcelWriter.writeString(parcel, 8, this.zzg, false);
        SafeParcelWriter.writeBoolean(parcel, 9, this.zzh);
        SafeParcelWriter.writeString(parcel, 10, this.zzi, false);
        SafeParcelWriter.writeString(parcel, 11, this.zzj, false);
        SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
    }
}
