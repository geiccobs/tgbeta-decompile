package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.dynamic.IObjectWrapper;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes3.dex */
public final class MarkerOptions extends AbstractSafeParcelable {
    public static final Parcelable.Creator<MarkerOptions> CREATOR = new zzi();
    private LatLng zza;
    private String zzb;
    private String zzc;
    private BitmapDescriptor zzd;
    private float zze;
    private float zzf;
    private boolean zzg;
    private boolean zzh;
    private boolean zzi;
    private float zzj;
    private float zzk;
    private float zzl;
    private float zzm;
    private float zzn;

    public MarkerOptions() {
        this.zze = 0.5f;
        this.zzf = 1.0f;
        this.zzh = true;
        this.zzi = false;
        this.zzj = 0.0f;
        this.zzk = 0.5f;
        this.zzl = 0.0f;
        this.zzm = 1.0f;
    }

    public MarkerOptions alpha(float f) {
        this.zzm = f;
        return this;
    }

    public MarkerOptions anchor(float f, float f2) {
        this.zze = f;
        this.zzf = f2;
        return this;
    }

    public MarkerOptions draggable(boolean z) {
        this.zzg = z;
        return this;
    }

    public MarkerOptions flat(boolean z) {
        this.zzi = z;
        return this;
    }

    public float getAlpha() {
        return this.zzm;
    }

    public float getAnchorU() {
        return this.zze;
    }

    public float getAnchorV() {
        return this.zzf;
    }

    public BitmapDescriptor getIcon() {
        return this.zzd;
    }

    public float getInfoWindowAnchorU() {
        return this.zzk;
    }

    public float getInfoWindowAnchorV() {
        return this.zzl;
    }

    public LatLng getPosition() {
        return this.zza;
    }

    public float getRotation() {
        return this.zzj;
    }

    public String getSnippet() {
        return this.zzc;
    }

    public String getTitle() {
        return this.zzb;
    }

    public float getZIndex() {
        return this.zzn;
    }

    public MarkerOptions icon(BitmapDescriptor bitmapDescriptor) {
        this.zzd = bitmapDescriptor;
        return this;
    }

    public MarkerOptions infoWindowAnchor(float f, float f2) {
        this.zzk = f;
        this.zzl = f2;
        return this;
    }

    public boolean isDraggable() {
        return this.zzg;
    }

    public boolean isFlat() {
        return this.zzi;
    }

    public boolean isVisible() {
        return this.zzh;
    }

    public MarkerOptions position(LatLng latlng) {
        if (latlng != null) {
            this.zza = latlng;
            return this;
        }
        throw new IllegalArgumentException("latlng cannot be null - a position is required.");
    }

    public MarkerOptions rotation(float f) {
        this.zzj = f;
        return this;
    }

    public MarkerOptions snippet(String str) {
        this.zzc = str;
        return this;
    }

    public MarkerOptions title(String str) {
        this.zzb = str;
        return this;
    }

    public MarkerOptions visible(boolean z) {
        this.zzh = z;
        return this;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(out);
        SafeParcelWriter.writeParcelable(out, 2, getPosition(), flags, false);
        SafeParcelWriter.writeString(out, 3, getTitle(), false);
        SafeParcelWriter.writeString(out, 4, getSnippet(), false);
        BitmapDescriptor bitmapDescriptor = this.zzd;
        SafeParcelWriter.writeIBinder(out, 5, bitmapDescriptor == null ? null : bitmapDescriptor.zza().asBinder(), false);
        SafeParcelWriter.writeFloat(out, 6, getAnchorU());
        SafeParcelWriter.writeFloat(out, 7, getAnchorV());
        SafeParcelWriter.writeBoolean(out, 8, isDraggable());
        SafeParcelWriter.writeBoolean(out, 9, isVisible());
        SafeParcelWriter.writeBoolean(out, 10, isFlat());
        SafeParcelWriter.writeFloat(out, 11, getRotation());
        SafeParcelWriter.writeFloat(out, 12, getInfoWindowAnchorU());
        SafeParcelWriter.writeFloat(out, 13, getInfoWindowAnchorV());
        SafeParcelWriter.writeFloat(out, 14, getAlpha());
        SafeParcelWriter.writeFloat(out, 15, getZIndex());
        SafeParcelWriter.finishObjectHeader(out, beginObjectHeader);
    }

    public MarkerOptions zIndex(float f) {
        this.zzn = f;
        return this;
    }

    public MarkerOptions(LatLng latLng, String str, String str2, IBinder iBinder, float f, float f2, boolean z, boolean z2, boolean z3, float f3, float f4, float f5, float f6, float f7) {
        this.zze = 0.5f;
        this.zzf = 1.0f;
        this.zzh = true;
        this.zzi = false;
        this.zzj = 0.0f;
        this.zzk = 0.5f;
        this.zzl = 0.0f;
        this.zzm = 1.0f;
        this.zza = latLng;
        this.zzb = str;
        this.zzc = str2;
        if (iBinder == null) {
            this.zzd = null;
        } else {
            this.zzd = new BitmapDescriptor(IObjectWrapper.Stub.asInterface(iBinder));
        }
        this.zze = f;
        this.zzf = f2;
        this.zzg = z;
        this.zzh = z2;
        this.zzi = z3;
        this.zzj = f3;
        this.zzk = f4;
        this.zzl = f5;
        this.zzm = f6;
        this.zzn = f7;
    }
}
