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
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes3.dex */
public final class CameraPosition extends AbstractSafeParcelable implements ReflectedParcelable {
    public static final Parcelable.Creator<CameraPosition> CREATOR = new zza();
    public final float bearing;
    public final LatLng target;
    public final float tilt;
    public final float zoom;

    /* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
    /* loaded from: classes3.dex */
    public static final class Builder {
        private LatLng zza;
        private float zzb;
        private float zzc;
        private float zzd;

        public Builder() {
        }

        public Builder(CameraPosition previous) {
            CameraPosition cameraPosition = (CameraPosition) Preconditions.checkNotNull(previous, "previous must not be null.");
            this.zza = cameraPosition.target;
            this.zzb = cameraPosition.zoom;
            this.zzc = cameraPosition.tilt;
            this.zzd = cameraPosition.bearing;
        }

        public Builder bearing(float f) {
            this.zzd = f;
            return this;
        }

        public CameraPosition build() {
            return new CameraPosition(this.zza, this.zzb, this.zzc, this.zzd);
        }

        public Builder target(LatLng location) {
            this.zza = (LatLng) Preconditions.checkNotNull(location, "location must not be null.");
            return this;
        }

        public Builder tilt(float f) {
            this.zzc = f;
            return this;
        }

        public Builder zoom(float f) {
            this.zzb = f;
            return this;
        }
    }

    public CameraPosition(LatLng target, float zoom, float tilt, float bearing) {
        Preconditions.checkNotNull(target, "camera target must not be null.");
        Preconditions.checkArgument(tilt >= 0.0f && tilt <= 90.0f, "Tilt needs to be between 0 and 90 inclusive: %s", Float.valueOf(tilt));
        this.target = target;
        this.zoom = zoom;
        this.tilt = tilt + 0.0f;
        this.bearing = (((double) bearing) <= FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE ? (bearing % 360.0f) + 360.0f : bearing) % 360.0f;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(CameraPosition camera) {
        return new Builder(camera);
    }

    public static CameraPosition createFromAttributes(Context context, AttributeSet attrs) {
        return GoogleMapOptions.zzb(context, attrs);
    }

    public static final CameraPosition fromLatLngZoom(LatLng target, float zoom) {
        return new CameraPosition(target, zoom, 0.0f, 0.0f);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CameraPosition)) {
            return false;
        }
        CameraPosition cameraPosition = (CameraPosition) o;
        return this.target.equals(cameraPosition.target) && Float.floatToIntBits(this.zoom) == Float.floatToIntBits(cameraPosition.zoom) && Float.floatToIntBits(this.tilt) == Float.floatToIntBits(cameraPosition.tilt) && Float.floatToIntBits(this.bearing) == Float.floatToIntBits(cameraPosition.bearing);
    }

    public int hashCode() {
        return Objects.hashCode(this.target, Float.valueOf(this.zoom), Float.valueOf(this.tilt), Float.valueOf(this.bearing));
    }

    public String toString() {
        return Objects.toStringHelper(this).add("target", this.target).add("zoom", Float.valueOf(this.zoom)).add("tilt", Float.valueOf(this.tilt)).add("bearing", Float.valueOf(this.bearing)).toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(out);
        SafeParcelWriter.writeParcelable(out, 2, this.target, flags, false);
        SafeParcelWriter.writeFloat(out, 3, this.zoom);
        SafeParcelWriter.writeFloat(out, 4, this.tilt);
        SafeParcelWriter.writeFloat(out, 5, this.bearing);
        SafeParcelWriter.finishObjectHeader(out, beginObjectHeader);
    }
}
