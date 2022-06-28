package com.google.android.exoplayer2.metadata.icy;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.util.Assertions;
import java.util.Arrays;
/* loaded from: classes3.dex */
public final class IcyInfo implements Metadata.Entry {
    public static final Parcelable.Creator<IcyInfo> CREATOR = new Parcelable.Creator<IcyInfo>() { // from class: com.google.android.exoplayer2.metadata.icy.IcyInfo.1
        @Override // android.os.Parcelable.Creator
        public IcyInfo createFromParcel(Parcel in) {
            return new IcyInfo(in);
        }

        @Override // android.os.Parcelable.Creator
        public IcyInfo[] newArray(int size) {
            return new IcyInfo[size];
        }
    };
    public final byte[] rawMetadata;
    public final String title;
    public final String url;

    @Override // com.google.android.exoplayer2.metadata.Metadata.Entry
    public /* synthetic */ byte[] getWrappedMetadataBytes() {
        return Metadata.Entry.CC.$default$getWrappedMetadataBytes(this);
    }

    @Override // com.google.android.exoplayer2.metadata.Metadata.Entry
    public /* synthetic */ Format getWrappedMetadataFormat() {
        return Metadata.Entry.CC.$default$getWrappedMetadataFormat(this);
    }

    public IcyInfo(byte[] rawMetadata, String title, String url) {
        this.rawMetadata = rawMetadata;
        this.title = title;
        this.url = url;
    }

    IcyInfo(Parcel in) {
        this.rawMetadata = (byte[]) Assertions.checkNotNull(in.createByteArray());
        this.title = in.readString();
        this.url = in.readString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        IcyInfo other = (IcyInfo) obj;
        return Arrays.equals(this.rawMetadata, other.rawMetadata);
    }

    public int hashCode() {
        return Arrays.hashCode(this.rawMetadata);
    }

    public String toString() {
        return String.format("ICY: title=\"%s\", url=\"%s\", rawMetadata.length=\"%s\"", this.title, this.url, Integer.valueOf(this.rawMetadata.length));
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(this.rawMetadata);
        dest.writeString(this.title);
        dest.writeString(this.url);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }
}
