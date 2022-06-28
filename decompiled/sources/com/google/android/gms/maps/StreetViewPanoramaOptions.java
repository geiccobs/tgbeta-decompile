package com.google.android.gms.maps;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewSource;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes3.dex */
public final class StreetViewPanoramaOptions extends AbstractSafeParcelable implements ReflectedParcelable {
    public static final Parcelable.Creator<StreetViewPanoramaOptions> CREATOR = new zzap();
    private StreetViewPanoramaCamera zza;
    private String zzb;
    private LatLng zzc;
    private Integer zzd;
    private Boolean zze;
    private Boolean zzf;
    private Boolean zzg;
    private Boolean zzh;
    private Boolean zzi;
    private StreetViewSource zzj;

    public StreetViewPanoramaOptions() {
        this.zze = true;
        this.zzf = true;
        this.zzg = true;
        this.zzh = true;
        this.zzj = StreetViewSource.DEFAULT;
    }

    public Boolean getPanningGesturesEnabled() {
        return this.zzg;
    }

    public String getPanoramaId() {
        return this.zzb;
    }

    public LatLng getPosition() {
        return this.zzc;
    }

    public Integer getRadius() {
        return this.zzd;
    }

    public StreetViewSource getSource() {
        return this.zzj;
    }

    public Boolean getStreetNamesEnabled() {
        return this.zzh;
    }

    public StreetViewPanoramaCamera getStreetViewPanoramaCamera() {
        return this.zza;
    }

    public Boolean getUseViewLifecycleInFragment() {
        return this.zzi;
    }

    public Boolean getUserNavigationEnabled() {
        return this.zze;
    }

    public Boolean getZoomGesturesEnabled() {
        return this.zzf;
    }

    public StreetViewPanoramaOptions panningGesturesEnabled(boolean enabled) {
        this.zzg = Boolean.valueOf(enabled);
        return this;
    }

    public StreetViewPanoramaOptions panoramaCamera(StreetViewPanoramaCamera streetViewPanoramaCamera) {
        this.zza = streetViewPanoramaCamera;
        return this;
    }

    public StreetViewPanoramaOptions panoramaId(String str) {
        this.zzb = str;
        return this;
    }

    public StreetViewPanoramaOptions position(LatLng latLng) {
        this.zzc = latLng;
        return this;
    }

    public StreetViewPanoramaOptions position(LatLng latLng, StreetViewSource streetViewSource) {
        this.zzc = latLng;
        this.zzj = streetViewSource;
        return this;
    }

    public StreetViewPanoramaOptions position(LatLng latLng, Integer num) {
        this.zzc = latLng;
        this.zzd = num;
        return this;
    }

    public StreetViewPanoramaOptions position(LatLng latLng, Integer num, StreetViewSource streetViewSource) {
        this.zzc = latLng;
        this.zzd = num;
        this.zzj = streetViewSource;
        return this;
    }

    public StreetViewPanoramaOptions streetNamesEnabled(boolean enabled) {
        this.zzh = Boolean.valueOf(enabled);
        return this;
    }

    public String toString() {
        return Objects.toStringHelper(this).add("PanoramaId", this.zzb).add("Position", this.zzc).add("Radius", this.zzd).add("Source", this.zzj).add("StreetViewPanoramaCamera", this.zza).add("UserNavigationEnabled", this.zze).add("ZoomGesturesEnabled", this.zzf).add("PanningGesturesEnabled", this.zzg).add("StreetNamesEnabled", this.zzh).add("UseViewLifecycleInFragment", this.zzi).toString();
    }

    public StreetViewPanoramaOptions useViewLifecycleInFragment(boolean useViewLifecycleInFragment) {
        this.zzi = Boolean.valueOf(useViewLifecycleInFragment);
        return this;
    }

    public StreetViewPanoramaOptions userNavigationEnabled(boolean enabled) {
        this.zze = Boolean.valueOf(enabled);
        return this;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(out);
        SafeParcelWriter.writeParcelable(out, 2, getStreetViewPanoramaCamera(), flags, false);
        SafeParcelWriter.writeString(out, 3, getPanoramaId(), false);
        SafeParcelWriter.writeParcelable(out, 4, getPosition(), flags, false);
        SafeParcelWriter.writeIntegerObject(out, 5, getRadius(), false);
        SafeParcelWriter.writeByte(out, 6, com.google.android.gms.maps.internal.zza.zzb(this.zze));
        SafeParcelWriter.writeByte(out, 7, com.google.android.gms.maps.internal.zza.zzb(this.zzf));
        SafeParcelWriter.writeByte(out, 8, com.google.android.gms.maps.internal.zza.zzb(this.zzg));
        SafeParcelWriter.writeByte(out, 9, com.google.android.gms.maps.internal.zza.zzb(this.zzh));
        SafeParcelWriter.writeByte(out, 10, com.google.android.gms.maps.internal.zza.zzb(this.zzi));
        SafeParcelWriter.writeParcelable(out, 11, getSource(), flags, false);
        SafeParcelWriter.finishObjectHeader(out, beginObjectHeader);
    }

    public StreetViewPanoramaOptions zoomGesturesEnabled(boolean enabled) {
        this.zzf = Boolean.valueOf(enabled);
        return this;
    }

    public StreetViewPanoramaOptions(StreetViewPanoramaCamera streetViewPanoramaCamera, String str, LatLng latLng, Integer num, byte b, byte b2, byte b3, byte b4, byte b5, StreetViewSource streetViewSource) {
        this.zze = true;
        this.zzf = true;
        this.zzg = true;
        this.zzh = true;
        this.zzj = StreetViewSource.DEFAULT;
        this.zza = streetViewPanoramaCamera;
        this.zzc = latLng;
        this.zzd = num;
        this.zzb = str;
        this.zze = com.google.android.gms.maps.internal.zza.zza(b);
        this.zzf = com.google.android.gms.maps.internal.zza.zza(b2);
        this.zzg = com.google.android.gms.maps.internal.zza.zza(b3);
        this.zzh = com.google.android.gms.maps.internal.zza.zza(b4);
        this.zzi = com.google.android.gms.maps.internal.zza.zza(b5);
        this.zzj = streetViewSource;
    }
}
