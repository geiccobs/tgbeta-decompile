package com.google.android.exoplayer2.metadata.flac;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.util.Util;
/* loaded from: classes3.dex */
public final class VorbisComment implements Metadata.Entry {
    public static final Parcelable.Creator<VorbisComment> CREATOR = new Parcelable.Creator<VorbisComment>() { // from class: com.google.android.exoplayer2.metadata.flac.VorbisComment.1
        @Override // android.os.Parcelable.Creator
        public VorbisComment createFromParcel(Parcel in) {
            return new VorbisComment(in);
        }

        @Override // android.os.Parcelable.Creator
        public VorbisComment[] newArray(int size) {
            return new VorbisComment[size];
        }
    };
    public final String key;
    public final String value;

    @Override // com.google.android.exoplayer2.metadata.Metadata.Entry
    public /* synthetic */ byte[] getWrappedMetadataBytes() {
        return Metadata.Entry.CC.$default$getWrappedMetadataBytes(this);
    }

    @Override // com.google.android.exoplayer2.metadata.Metadata.Entry
    public /* synthetic */ Format getWrappedMetadataFormat() {
        return Metadata.Entry.CC.$default$getWrappedMetadataFormat(this);
    }

    public VorbisComment(String key, String value) {
        this.key = key;
        this.value = value;
    }

    VorbisComment(Parcel in) {
        this.key = (String) Util.castNonNull(in.readString());
        this.value = (String) Util.castNonNull(in.readString());
    }

    public String toString() {
        return "VC: " + this.key + "=" + this.value;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        VorbisComment other = (VorbisComment) obj;
        return this.key.equals(other.key) && this.value.equals(other.value);
    }

    public int hashCode() {
        int result = (17 * 31) + this.key.hashCode();
        return (result * 31) + this.value.hashCode();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeString(this.value);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }
}
