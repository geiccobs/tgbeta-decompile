package com.google.android.exoplayer2.extractor.mp4;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.util.Util;
import java.util.Arrays;
/* loaded from: classes3.dex */
public final class MdtaMetadataEntry implements Metadata.Entry {
    public static final Parcelable.Creator<MdtaMetadataEntry> CREATOR = new Parcelable.Creator<MdtaMetadataEntry>() { // from class: com.google.android.exoplayer2.extractor.mp4.MdtaMetadataEntry.1
        @Override // android.os.Parcelable.Creator
        public MdtaMetadataEntry createFromParcel(Parcel in) {
            return new MdtaMetadataEntry(in);
        }

        @Override // android.os.Parcelable.Creator
        public MdtaMetadataEntry[] newArray(int size) {
            return new MdtaMetadataEntry[size];
        }
    };
    public final String key;
    public final int localeIndicator;
    public final int typeIndicator;
    public final byte[] value;

    @Override // com.google.android.exoplayer2.metadata.Metadata.Entry
    public /* synthetic */ byte[] getWrappedMetadataBytes() {
        return Metadata.Entry.CC.$default$getWrappedMetadataBytes(this);
    }

    @Override // com.google.android.exoplayer2.metadata.Metadata.Entry
    public /* synthetic */ Format getWrappedMetadataFormat() {
        return Metadata.Entry.CC.$default$getWrappedMetadataFormat(this);
    }

    public MdtaMetadataEntry(String key, byte[] value, int localeIndicator, int typeIndicator) {
        this.key = key;
        this.value = value;
        this.localeIndicator = localeIndicator;
        this.typeIndicator = typeIndicator;
    }

    private MdtaMetadataEntry(Parcel in) {
        this.key = (String) Util.castNonNull(in.readString());
        byte[] bArr = new byte[in.readInt()];
        this.value = bArr;
        in.readByteArray(bArr);
        this.localeIndicator = in.readInt();
        this.typeIndicator = in.readInt();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MdtaMetadataEntry other = (MdtaMetadataEntry) obj;
        return this.key.equals(other.key) && Arrays.equals(this.value, other.value) && this.localeIndicator == other.localeIndicator && this.typeIndicator == other.typeIndicator;
    }

    public int hashCode() {
        int result = (17 * 31) + this.key.hashCode();
        return (((((result * 31) + Arrays.hashCode(this.value)) * 31) + this.localeIndicator) * 31) + this.typeIndicator;
    }

    public String toString() {
        return "mdta: key=" + this.key;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeInt(this.value.length);
        dest.writeByteArray(this.value);
        dest.writeInt(this.localeIndicator);
        dest.writeInt(this.typeIndicator);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }
}
