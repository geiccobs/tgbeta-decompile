package com.google.android.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.exoplayer2.util.Util;
import java.util.Arrays;
/* loaded from: classes3.dex */
public final class GeobFrame extends Id3Frame {
    public static final Parcelable.Creator<GeobFrame> CREATOR = new Parcelable.Creator<GeobFrame>() { // from class: com.google.android.exoplayer2.metadata.id3.GeobFrame.1
        @Override // android.os.Parcelable.Creator
        public GeobFrame createFromParcel(Parcel in) {
            return new GeobFrame(in);
        }

        @Override // android.os.Parcelable.Creator
        public GeobFrame[] newArray(int size) {
            return new GeobFrame[size];
        }
    };
    public static final String ID = "GEOB";
    public final byte[] data;
    public final String description;
    public final String filename;
    public final String mimeType;

    public GeobFrame(String mimeType, String filename, String description, byte[] data) {
        super(ID);
        this.mimeType = mimeType;
        this.filename = filename;
        this.description = description;
        this.data = data;
    }

    GeobFrame(Parcel in) {
        super(ID);
        this.mimeType = (String) Util.castNonNull(in.readString());
        this.filename = (String) Util.castNonNull(in.readString());
        this.description = (String) Util.castNonNull(in.readString());
        this.data = (byte[]) Util.castNonNull(in.createByteArray());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        GeobFrame other = (GeobFrame) obj;
        return Util.areEqual(this.mimeType, other.mimeType) && Util.areEqual(this.filename, other.filename) && Util.areEqual(this.description, other.description) && Arrays.equals(this.data, other.data);
    }

    public int hashCode() {
        int i = 17 * 31;
        String str = this.mimeType;
        int i2 = 0;
        int result = i + (str != null ? str.hashCode() : 0);
        int result2 = result * 31;
        String str2 = this.filename;
        int result3 = (result2 + (str2 != null ? str2.hashCode() : 0)) * 31;
        String str3 = this.description;
        if (str3 != null) {
            i2 = str3.hashCode();
        }
        return ((result3 + i2) * 31) + Arrays.hashCode(this.data);
    }

    @Override // com.google.android.exoplayer2.metadata.id3.Id3Frame
    public String toString() {
        return this.id + ": mimeType=" + this.mimeType + ", filename=" + this.filename + ", description=" + this.description;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mimeType);
        dest.writeString(this.filename);
        dest.writeString(this.description);
        dest.writeByteArray(this.data);
    }
}
