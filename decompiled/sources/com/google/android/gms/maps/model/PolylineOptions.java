package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes3.dex */
public final class PolylineOptions extends AbstractSafeParcelable {
    public static final Parcelable.Creator<PolylineOptions> CREATOR = new zzm();
    private final List<LatLng> zza;
    private float zzb;
    private int zzc;
    private float zzd;
    private boolean zze;
    private boolean zzf;
    private boolean zzg;
    private Cap zzh;
    private Cap zzi;
    private int zzj;
    private List<PatternItem> zzk;

    public PolylineOptions() {
        this.zzb = 10.0f;
        this.zzc = -16777216;
        this.zzd = 0.0f;
        this.zze = true;
        this.zzf = false;
        this.zzg = false;
        this.zzh = new ButtCap();
        this.zzi = new ButtCap();
        this.zzj = 0;
        this.zzk = null;
        this.zza = new ArrayList();
    }

    public PolylineOptions add(LatLng point) {
        Preconditions.checkNotNull(this.zza, "point must not be null.");
        this.zza.add(point);
        return this;
    }

    public PolylineOptions addAll(Iterable<LatLng> iterable) {
        Preconditions.checkNotNull(iterable, "points must not be null.");
        for (LatLng latLng : iterable) {
            this.zza.add(latLng);
        }
        return this;
    }

    public PolylineOptions clickable(boolean z) {
        this.zzg = z;
        return this;
    }

    public PolylineOptions color(int i) {
        this.zzc = i;
        return this;
    }

    public PolylineOptions endCap(Cap endCap) {
        this.zzi = (Cap) Preconditions.checkNotNull(endCap, "endCap must not be null");
        return this;
    }

    public PolylineOptions geodesic(boolean z) {
        this.zzf = z;
        return this;
    }

    public int getColor() {
        return this.zzc;
    }

    public Cap getEndCap() {
        return this.zzi;
    }

    public int getJointType() {
        return this.zzj;
    }

    public List<PatternItem> getPattern() {
        return this.zzk;
    }

    public List<LatLng> getPoints() {
        return this.zza;
    }

    public Cap getStartCap() {
        return this.zzh;
    }

    public float getWidth() {
        return this.zzb;
    }

    public float getZIndex() {
        return this.zzd;
    }

    public boolean isClickable() {
        return this.zzg;
    }

    public boolean isGeodesic() {
        return this.zzf;
    }

    public boolean isVisible() {
        return this.zze;
    }

    public PolylineOptions jointType(int i) {
        this.zzj = i;
        return this;
    }

    public PolylineOptions pattern(List<PatternItem> list) {
        this.zzk = list;
        return this;
    }

    public PolylineOptions startCap(Cap startCap) {
        this.zzh = (Cap) Preconditions.checkNotNull(startCap, "startCap must not be null");
        return this;
    }

    public PolylineOptions visible(boolean z) {
        this.zze = z;
        return this;
    }

    public PolylineOptions width(float f) {
        this.zzb = f;
        return this;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(out);
        SafeParcelWriter.writeTypedList(out, 2, getPoints(), false);
        SafeParcelWriter.writeFloat(out, 3, getWidth());
        SafeParcelWriter.writeInt(out, 4, getColor());
        SafeParcelWriter.writeFloat(out, 5, getZIndex());
        SafeParcelWriter.writeBoolean(out, 6, isVisible());
        SafeParcelWriter.writeBoolean(out, 7, isGeodesic());
        SafeParcelWriter.writeBoolean(out, 8, isClickable());
        SafeParcelWriter.writeParcelable(out, 9, getStartCap(), flags, false);
        SafeParcelWriter.writeParcelable(out, 10, getEndCap(), flags, false);
        SafeParcelWriter.writeInt(out, 11, getJointType());
        SafeParcelWriter.writeTypedList(out, 12, getPattern(), false);
        SafeParcelWriter.finishObjectHeader(out, beginObjectHeader);
    }

    public PolylineOptions zIndex(float f) {
        this.zzd = f;
        return this;
    }

    public PolylineOptions add(LatLng... points) {
        Preconditions.checkNotNull(points, "points must not be null.");
        this.zza.addAll(Arrays.asList(points));
        return this;
    }

    public PolylineOptions(List list, float f, int i, float f2, boolean z, boolean z2, boolean z3, Cap cap, Cap cap2, int i2, List<PatternItem> list2) {
        this.zzb = 10.0f;
        this.zzc = -16777216;
        this.zzd = 0.0f;
        this.zze = true;
        this.zzf = false;
        this.zzg = false;
        this.zzh = new ButtCap();
        this.zzi = new ButtCap();
        this.zza = list;
        this.zzb = f;
        this.zzc = i;
        this.zzd = f2;
        this.zze = z;
        this.zzf = z2;
        this.zzg = z3;
        if (cap != null) {
            this.zzh = cap;
        }
        if (cap2 != null) {
            this.zzi = cap2;
        }
        this.zzj = i2;
        this.zzk = list2;
    }
}
