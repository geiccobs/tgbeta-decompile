package com.google.android.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.exoplayer2.util.Util;
/* loaded from: classes3.dex */
public final class InternalFrame extends Id3Frame {
    public static final Parcelable.Creator<InternalFrame> CREATOR = new Parcelable.Creator<InternalFrame>() { // from class: com.google.android.exoplayer2.metadata.id3.InternalFrame.1
        @Override // android.os.Parcelable.Creator
        public InternalFrame createFromParcel(Parcel in) {
            return new InternalFrame(in);
        }

        @Override // android.os.Parcelable.Creator
        public InternalFrame[] newArray(int size) {
            return new InternalFrame[size];
        }
    };
    public static final String ID = "----";
    public final String description;
    public final String domain;
    public final String text;

    public InternalFrame(String domain, String description, String text) {
        super(ID);
        this.domain = domain;
        this.description = description;
        this.text = text;
    }

    InternalFrame(Parcel in) {
        super(ID);
        this.domain = (String) Util.castNonNull(in.readString());
        this.description = (String) Util.castNonNull(in.readString());
        this.text = (String) Util.castNonNull(in.readString());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        InternalFrame other = (InternalFrame) obj;
        return Util.areEqual(this.description, other.description) && Util.areEqual(this.domain, other.domain) && Util.areEqual(this.text, other.text);
    }

    public int hashCode() {
        int i = 17 * 31;
        String str = this.domain;
        int i2 = 0;
        int result = i + (str != null ? str.hashCode() : 0);
        int result2 = result * 31;
        String str2 = this.description;
        int result3 = (result2 + (str2 != null ? str2.hashCode() : 0)) * 31;
        String str3 = this.text;
        if (str3 != null) {
            i2 = str3.hashCode();
        }
        return result3 + i2;
    }

    @Override // com.google.android.exoplayer2.metadata.id3.Id3Frame
    public String toString() {
        return this.id + ": domain=" + this.domain + ", description=" + this.description;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.domain);
        dest.writeString(this.text);
    }
}
