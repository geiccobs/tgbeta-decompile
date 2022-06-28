package com.google.android.exoplayer2.source.hls;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.metadata.Metadata;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes3.dex */
public final class HlsTrackMetadataEntry implements Metadata.Entry {
    public static final Parcelable.Creator<HlsTrackMetadataEntry> CREATOR = new Parcelable.Creator<HlsTrackMetadataEntry>() { // from class: com.google.android.exoplayer2.source.hls.HlsTrackMetadataEntry.1
        @Override // android.os.Parcelable.Creator
        public HlsTrackMetadataEntry createFromParcel(Parcel in) {
            return new HlsTrackMetadataEntry(in);
        }

        @Override // android.os.Parcelable.Creator
        public HlsTrackMetadataEntry[] newArray(int size) {
            return new HlsTrackMetadataEntry[size];
        }
    };
    public final String groupId;
    public final String name;
    public final List<VariantInfo> variantInfos;

    @Override // com.google.android.exoplayer2.metadata.Metadata.Entry
    public /* synthetic */ byte[] getWrappedMetadataBytes() {
        return Metadata.Entry.CC.$default$getWrappedMetadataBytes(this);
    }

    @Override // com.google.android.exoplayer2.metadata.Metadata.Entry
    public /* synthetic */ Format getWrappedMetadataFormat() {
        return Metadata.Entry.CC.$default$getWrappedMetadataFormat(this);
    }

    /* loaded from: classes3.dex */
    public static final class VariantInfo implements Parcelable {
        public static final Parcelable.Creator<VariantInfo> CREATOR = new Parcelable.Creator<VariantInfo>() { // from class: com.google.android.exoplayer2.source.hls.HlsTrackMetadataEntry.VariantInfo.1
            @Override // android.os.Parcelable.Creator
            public VariantInfo createFromParcel(Parcel in) {
                return new VariantInfo(in);
            }

            @Override // android.os.Parcelable.Creator
            public VariantInfo[] newArray(int size) {
                return new VariantInfo[size];
            }
        };
        public final String audioGroupId;
        public final long bitrate;
        public final String captionGroupId;
        public final String subtitleGroupId;
        public final String videoGroupId;

        public VariantInfo(long bitrate, String videoGroupId, String audioGroupId, String subtitleGroupId, String captionGroupId) {
            this.bitrate = bitrate;
            this.videoGroupId = videoGroupId;
            this.audioGroupId = audioGroupId;
            this.subtitleGroupId = subtitleGroupId;
            this.captionGroupId = captionGroupId;
        }

        VariantInfo(Parcel in) {
            this.bitrate = in.readLong();
            this.videoGroupId = in.readString();
            this.audioGroupId = in.readString();
            this.subtitleGroupId = in.readString();
            this.captionGroupId = in.readString();
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || getClass() != other.getClass()) {
                return false;
            }
            VariantInfo that = (VariantInfo) other;
            return this.bitrate == that.bitrate && TextUtils.equals(this.videoGroupId, that.videoGroupId) && TextUtils.equals(this.audioGroupId, that.audioGroupId) && TextUtils.equals(this.subtitleGroupId, that.subtitleGroupId) && TextUtils.equals(this.captionGroupId, that.captionGroupId);
        }

        public int hashCode() {
            long j = this.bitrate;
            int result = (int) (j ^ (j >>> 32));
            int i = result * 31;
            String str = this.videoGroupId;
            int i2 = 0;
            int result2 = i + (str != null ? str.hashCode() : 0);
            int result3 = result2 * 31;
            String str2 = this.audioGroupId;
            int result4 = (result3 + (str2 != null ? str2.hashCode() : 0)) * 31;
            String str3 = this.subtitleGroupId;
            int result5 = (result4 + (str3 != null ? str3.hashCode() : 0)) * 31;
            String str4 = this.captionGroupId;
            if (str4 != null) {
                i2 = str4.hashCode();
            }
            return result5 + i2;
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.bitrate);
            dest.writeString(this.videoGroupId);
            dest.writeString(this.audioGroupId);
            dest.writeString(this.subtitleGroupId);
            dest.writeString(this.captionGroupId);
        }
    }

    public HlsTrackMetadataEntry(String groupId, String name, List<VariantInfo> variantInfos) {
        this.groupId = groupId;
        this.name = name;
        this.variantInfos = Collections.unmodifiableList(new ArrayList(variantInfos));
    }

    HlsTrackMetadataEntry(Parcel in) {
        this.groupId = in.readString();
        this.name = in.readString();
        int variantInfoSize = in.readInt();
        ArrayList<VariantInfo> variantInfos = new ArrayList<>(variantInfoSize);
        for (int i = 0; i < variantInfoSize; i++) {
            variantInfos.add((VariantInfo) in.readParcelable(VariantInfo.class.getClassLoader()));
        }
        this.variantInfos = Collections.unmodifiableList(variantInfos);
    }

    public String toString() {
        String str;
        StringBuilder sb = new StringBuilder();
        sb.append("HlsTrackMetadataEntry");
        if (this.groupId != null) {
            str = " [" + this.groupId + ", " + this.name + "]";
        } else {
            str = "";
        }
        sb.append(str);
        return sb.toString();
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        HlsTrackMetadataEntry that = (HlsTrackMetadataEntry) other;
        return TextUtils.equals(this.groupId, that.groupId) && TextUtils.equals(this.name, that.name) && this.variantInfos.equals(that.variantInfos);
    }

    public int hashCode() {
        String str = this.groupId;
        int i = 0;
        int result = str != null ? str.hashCode() : 0;
        int i2 = result * 31;
        String str2 = this.name;
        if (str2 != null) {
            i = str2.hashCode();
        }
        int result2 = i2 + i;
        return (result2 * 31) + this.variantInfos.hashCode();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.groupId);
        dest.writeString(this.name);
        int variantInfosSize = this.variantInfos.size();
        dest.writeInt(variantInfosSize);
        for (int i = 0; i < variantInfosSize; i++) {
            dest.writeParcelable(this.variantInfos.get(i), 0);
        }
    }
}
