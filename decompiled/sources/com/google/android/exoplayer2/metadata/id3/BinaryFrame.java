package com.google.android.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.exoplayer2.util.Util;
import java.util.Arrays;
/* loaded from: classes3.dex */
public final class BinaryFrame extends Id3Frame {
    public static final Parcelable.Creator<BinaryFrame> CREATOR = new Parcelable.Creator<BinaryFrame>() { // from class: com.google.android.exoplayer2.metadata.id3.BinaryFrame.1
        @Override // android.os.Parcelable.Creator
        public BinaryFrame createFromParcel(Parcel in) {
            return new BinaryFrame(in);
        }

        @Override // android.os.Parcelable.Creator
        public BinaryFrame[] newArray(int size) {
            return new BinaryFrame[size];
        }
    };
    public final byte[] data;

    public BinaryFrame(String id, byte[] data) {
        super(id);
        this.data = data;
    }

    BinaryFrame(Parcel in) {
        super((String) Util.castNonNull(in.readString()));
        this.data = (byte[]) Util.castNonNull(in.createByteArray());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        BinaryFrame other = (BinaryFrame) obj;
        return this.id.equals(other.id) && Arrays.equals(this.data, other.data);
    }

    public int hashCode() {
        int result = (17 * 31) + this.id.hashCode();
        return (result * 31) + Arrays.hashCode(this.data);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeByteArray(this.data);
    }
}
