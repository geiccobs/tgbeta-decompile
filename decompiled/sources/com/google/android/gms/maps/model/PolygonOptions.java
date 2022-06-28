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
public final class PolygonOptions extends AbstractSafeParcelable {
    public static final Parcelable.Creator<PolygonOptions> CREATOR = new zzl();
    private final List<LatLng> zza;
    private final List<List<LatLng>> zzb;
    private float zzc;
    private int zzd;
    private int zze;
    private float zzf;
    private boolean zzg;
    private boolean zzh;
    private boolean zzi;
    private int zzj;
    private List<PatternItem> zzk;

    public PolygonOptions() {
        this.zzc = 10.0f;
        this.zzd = -16777216;
        this.zze = 0;
        this.zzf = 0.0f;
        this.zzg = true;
        this.zzh = false;
        this.zzi = false;
        this.zzj = 0;
        this.zzk = null;
        this.zza = new ArrayList();
        this.zzb = new ArrayList();
    }

    public PolygonOptions add(LatLng point) {
        Preconditions.checkNotNull(point, "point must not be null.");
        this.zza.add(point);
        return this;
    }

    public PolygonOptions addAll(Iterable<LatLng> iterable) {
        Preconditions.checkNotNull(iterable, "points must not be null.");
        for (LatLng latLng : iterable) {
            this.zza.add(latLng);
        }
        return this;
    }

    public PolygonOptions addHole(Iterable<LatLng> iterable) {
        Preconditions.checkNotNull(iterable, "points must not be null.");
        ArrayList arrayList = new ArrayList();
        for (LatLng latLng : iterable) {
            arrayList.add(latLng);
        }
        this.zzb.add(arrayList);
        return this;
    }

    public PolygonOptions clickable(boolean z) {
        this.zzi = z;
        return this;
    }

    public PolygonOptions fillColor(int i) {
        this.zze = i;
        return this;
    }

    public PolygonOptions geodesic(boolean z) {
        this.zzh = z;
        return this;
    }

    public int getFillColor() {
        return this.zze;
    }

    public List<List<LatLng>> getHoles() {
        return this.zzb;
    }

    public List<LatLng> getPoints() {
        return this.zza;
    }

    public int getStrokeColor() {
        return this.zzd;
    }

    public int getStrokeJointType() {
        return this.zzj;
    }

    public List<PatternItem> getStrokePattern() {
        return this.zzk;
    }

    public float getStrokeWidth() {
        return this.zzc;
    }

    public float getZIndex() {
        return this.zzf;
    }

    public boolean isClickable() {
        return this.zzi;
    }

    public boolean isGeodesic() {
        return this.zzh;
    }

    public boolean isVisible() {
        return this.zzg;
    }

    public PolygonOptions strokeColor(int i) {
        this.zzd = i;
        return this;
    }

    public PolygonOptions strokeJointType(int i) {
        this.zzj = i;
        return this;
    }

    public PolygonOptions strokePattern(List<PatternItem> list) {
        this.zzk = list;
        return this;
    }

    public PolygonOptions strokeWidth(float f) {
        this.zzc = f;
        return this;
    }

    public PolygonOptions visible(boolean z) {
        this.zzg = z;
        return this;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(out);
        SafeParcelWriter.writeTypedList(out, 2, getPoints(), false);
        SafeParcelWriter.writeList(out, 3, this.zzb, false);
        SafeParcelWriter.writeFloat(out, 4, getStrokeWidth());
        SafeParcelWriter.writeInt(out, 5, getStrokeColor());
        SafeParcelWriter.writeInt(out, 6, getFillColor());
        SafeParcelWriter.writeFloat(out, 7, getZIndex());
        SafeParcelWriter.writeBoolean(out, 8, isVisible());
        SafeParcelWriter.writeBoolean(out, 9, isGeodesic());
        SafeParcelWriter.writeBoolean(out, 10, isClickable());
        SafeParcelWriter.writeInt(out, 11, getStrokeJointType());
        SafeParcelWriter.writeTypedList(out, 12, getStrokePattern(), false);
        SafeParcelWriter.finishObjectHeader(out, beginObjectHeader);
    }

    public PolygonOptions zIndex(float f) {
        this.zzf = f;
        return this;
    }

    public PolygonOptions add(LatLng... points) {
        Preconditions.checkNotNull(points, "points must not be null.");
        this.zza.addAll(Arrays.asList(points));
        return this;
    }

    public PolygonOptions(List<LatLng> list, List list2, float f, int i, int i2, float f2, boolean z, boolean z2, boolean z3, int i3, List<PatternItem> list3) {
        this.zza = list;
        this.zzb = list2;
        this.zzc = f;
        this.zzd = i;
        this.zze = i2;
        this.zzf = f2;
        this.zzg = z;
        this.zzh = z2;
        this.zzi = z3;
        this.zzj = i3;
        this.zzk = list3;
    }
}
