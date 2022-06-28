package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes3.dex */
public final class Tile extends AbstractSafeParcelable {
    public static final Parcelable.Creator<Tile> CREATOR = new zzs();
    public final byte[] data;
    public final int height;
    public final int width;

    public Tile(int width, int height, byte[] data) {
        this.width = width;
        this.height = height;
        this.data = data;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(out);
        SafeParcelWriter.writeInt(out, 2, this.width);
        SafeParcelWriter.writeInt(out, 3, this.height);
        SafeParcelWriter.writeByteArray(out, 4, this.data, false);
        SafeParcelWriter.finishObjectHeader(out, beginObjectHeader);
    }
}
