package com.google.android.gms.location.places;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.firebase.messaging.Constants;
/* loaded from: classes3.dex */
public class PlaceReport extends AbstractSafeParcelable implements ReflectedParcelable {
    public static final Parcelable.Creator<PlaceReport> CREATOR = new zza();
    private final String tag;
    private final int versionCode;
    private final String zza;
    private final String zzb;

    public PlaceReport(int i, String str, String str2, String str3) {
        this.versionCode = i;
        this.zza = str;
        this.tag = str2;
        this.zzb = str3;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static PlaceReport create(String str, String str2) {
        char c;
        Preconditions.checkNotNull(str);
        Preconditions.checkNotEmpty(str2);
        Preconditions.checkNotEmpty("unknown");
        boolean z = false;
        switch ("unknown".hashCode()) {
            case -1436706272:
                if ("unknown".equals("inferredGeofencing")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -1194968642:
                if ("unknown".equals("userReported")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -284840886:
                if ("unknown".equals("unknown")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -262743844:
                if ("unknown".equals("inferredReverseGeocoding")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 1164924125:
                if ("unknown".equals("inferredSnappedToRoad")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 1287171955:
                if ("unknown".equals("inferredRadioSignals")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                z = true;
                break;
        }
        Preconditions.checkArgument(z, "Invalid source");
        return new PlaceReport(1, str, str2, "unknown");
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof PlaceReport)) {
            return false;
        }
        PlaceReport placeReport = (PlaceReport) obj;
        return Objects.equal(this.zza, placeReport.zza) && Objects.equal(this.tag, placeReport.tag) && Objects.equal(this.zzb, placeReport.zzb);
    }

    public String getPlaceId() {
        return this.zza;
    }

    public String getTag() {
        return this.tag;
    }

    public int hashCode() {
        return Objects.hashCode(this.zza, this.tag, this.zzb);
    }

    public String toString() {
        Objects.ToStringHelper stringHelper = Objects.toStringHelper(this);
        stringHelper.add("placeId", this.zza);
        stringHelper.add("tag", this.tag);
        if (!"unknown".equals(this.zzb)) {
            stringHelper.add(Constants.ScionAnalytics.PARAM_SOURCE, this.zzb);
        }
        return stringHelper.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
        SafeParcelWriter.writeInt(parcel, 1, this.versionCode);
        SafeParcelWriter.writeString(parcel, 2, getPlaceId(), false);
        SafeParcelWriter.writeString(parcel, 3, getTag(), false);
        SafeParcelWriter.writeString(parcel, 4, this.zzb, false);
        SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
    }
}
