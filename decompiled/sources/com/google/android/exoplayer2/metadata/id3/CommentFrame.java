package com.google.android.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.exoplayer2.util.Util;
/* loaded from: classes3.dex */
public final class CommentFrame extends Id3Frame {
    public static final Parcelable.Creator<CommentFrame> CREATOR = new Parcelable.Creator<CommentFrame>() { // from class: com.google.android.exoplayer2.metadata.id3.CommentFrame.1
        @Override // android.os.Parcelable.Creator
        public CommentFrame createFromParcel(Parcel in) {
            return new CommentFrame(in);
        }

        @Override // android.os.Parcelable.Creator
        public CommentFrame[] newArray(int size) {
            return new CommentFrame[size];
        }
    };
    public static final String ID = "COMM";
    public final String description;
    public final String language;
    public final String text;

    public CommentFrame(String language, String description, String text) {
        super(ID);
        this.language = language;
        this.description = description;
        this.text = text;
    }

    CommentFrame(Parcel in) {
        super(ID);
        this.language = (String) Util.castNonNull(in.readString());
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
        CommentFrame other = (CommentFrame) obj;
        return Util.areEqual(this.description, other.description) && Util.areEqual(this.language, other.language) && Util.areEqual(this.text, other.text);
    }

    public int hashCode() {
        int i = 17 * 31;
        String str = this.language;
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
        return this.id + ": language=" + this.language + ", description=" + this.description;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.language);
        dest.writeString(this.text);
    }
}
