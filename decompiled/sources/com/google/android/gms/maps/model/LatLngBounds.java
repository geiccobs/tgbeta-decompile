package com.google.android.gms.maps.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.maps.GoogleMapOptions;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes3.dex */
public final class LatLngBounds extends AbstractSafeParcelable implements ReflectedParcelable {
    public static final Parcelable.Creator<LatLngBounds> CREATOR = new zzf();
    public final LatLng northeast;
    public final LatLng southwest;

    /* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
    /* loaded from: classes3.dex */
    public static final class Builder {
        private double zza = Double.POSITIVE_INFINITY;
        private double zzb = Double.NEGATIVE_INFINITY;
        private double zzc = Double.NaN;
        private double zzd = Double.NaN;

        public LatLngBounds build() {
            Preconditions.checkState(!Double.isNaN(this.zzc), "no included points");
            return new LatLngBounds(new LatLng(this.zza, this.zzc), new LatLng(this.zzb, this.zzd));
        }

        public Builder include(LatLng point) {
            Preconditions.checkNotNull(point, "point must not be null");
            this.zza = Math.min(this.zza, point.latitude);
            this.zzb = Math.max(this.zzb, point.latitude);
            double d = point.longitude;
            if (Double.isNaN(this.zzc)) {
                this.zzc = d;
                this.zzd = d;
            } else {
                double d2 = this.zzc;
                double d3 = this.zzd;
                if (d2 > d3 ? !(d2 <= d || d <= d3) : !(d2 <= d && d <= d3)) {
                    Parcelable.Creator<LatLngBounds> creator = LatLngBounds.CREATOR;
                    if (((d2 - d) + 360.0d) % 360.0d < ((d - d3) + 360.0d) % 360.0d) {
                        this.zzc = d;
                    } else {
                        this.zzd = d;
                    }
                }
            }
            return this;
        }
    }

    public LatLngBounds(LatLng southwest, LatLng northeast) {
        Preconditions.checkNotNull(southwest, "southwest must not be null.");
        Preconditions.checkNotNull(northeast, "northeast must not be null.");
        double d = northeast.latitude;
        double d2 = southwest.latitude;
        Preconditions.checkArgument(d >= d2, "southern latitude exceeds northern latitude (%s > %s)", Double.valueOf(d2), Double.valueOf(northeast.latitude));
        this.southwest = southwest;
        this.northeast = northeast;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static LatLngBounds createFromAttributes(Context context, AttributeSet attrs) {
        return GoogleMapOptions.zza(context, attrs);
    }

    private final boolean zza(double d) {
        double d2 = this.southwest.longitude;
        double d3 = this.northeast.longitude;
        return d2 <= d3 ? d2 <= d && d <= d3 : d2 <= d || d <= d3;
    }

    public boolean contains(LatLng point) {
        LatLng latLng = (LatLng) Preconditions.checkNotNull(point, "point must not be null.");
        double d = latLng.latitude;
        return this.southwest.latitude <= d && d <= this.northeast.latitude && zza(latLng.longitude);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LatLngBounds)) {
            return false;
        }
        LatLngBounds latLngBounds = (LatLngBounds) o;
        return this.southwest.equals(latLngBounds.southwest) && this.northeast.equals(latLngBounds.northeast);
    }

    public LatLng getCenter() {
        LatLng latLng = this.southwest;
        double d = latLng.latitude;
        LatLng latLng2 = this.northeast;
        double d2 = (d + latLng2.latitude) / 2.0d;
        double d3 = latLng2.longitude;
        double d4 = latLng.longitude;
        return new LatLng(d2, d4 <= d3 ? (d3 + d4) / 2.0d : ((d3 + 360.0d) + d4) / 2.0d);
    }

    public int hashCode() {
        return Objects.hashCode(this.southwest, this.northeast);
    }

    public LatLngBounds including(LatLng point) {
        LatLng latLng = (LatLng) Preconditions.checkNotNull(point, "point must not be null.");
        double min = Math.min(this.southwest.latitude, latLng.latitude);
        double max = Math.max(this.northeast.latitude, latLng.latitude);
        double d = this.northeast.longitude;
        double d2 = this.southwest.longitude;
        double d3 = latLng.longitude;
        if (!zza(d3)) {
            if (((d2 - d3) + 360.0d) % 360.0d < ((d3 - d) + 360.0d) % 360.0d) {
                d2 = d3;
            } else {
                d = d3;
            }
        }
        return new LatLngBounds(new LatLng(min, d2), new LatLng(max, d));
    }

    public String toString() {
        return Objects.toStringHelper(this).add("southwest", this.southwest).add("northeast", this.northeast).toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(out);
        SafeParcelWriter.writeParcelable(out, 2, this.southwest, flags, false);
        SafeParcelWriter.writeParcelable(out, 3, this.northeast, flags, false);
        SafeParcelWriter.finishObjectHeader(out, beginObjectHeader);
    }
}
