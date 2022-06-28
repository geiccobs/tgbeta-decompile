package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes3.dex */
public final class StreetViewSource extends AbstractSafeParcelable {
    private final int zzb;
    private static final String zza = StreetViewSource.class.getSimpleName();
    public static final Parcelable.Creator<StreetViewSource> CREATOR = new zzr();
    public static final StreetViewSource DEFAULT = new StreetViewSource(0);
    public static final StreetViewSource OUTDOOR = new StreetViewSource(1);

    public StreetViewSource(int i) {
        this.zzb = i;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return (o instanceof StreetViewSource) && this.zzb == ((StreetViewSource) o).zzb;
    }

    public int hashCode() {
        return Objects.hashCode(Integer.valueOf(this.zzb));
    }

    public String toString() {
        String str;
        int i = this.zzb;
        switch (i) {
            case 0:
                str = "DEFAULT";
                break;
            case 1:
                str = "OUTDOOR";
                break;
            default:
                str = String.format("UNKNOWN(%s)", Integer.valueOf(i));
                break;
        }
        return String.format("StreetViewSource:%s", str);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(out);
        SafeParcelWriter.writeInt(out, 2, this.zzb);
        SafeParcelWriter.finishObjectHeader(out, beginObjectHeader);
    }
}
