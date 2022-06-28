package com.google.android.exoplayer2.metadata.icy;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import java.util.List;
import java.util.Map;
/* loaded from: classes3.dex */
public final class IcyHeaders implements Metadata.Entry {
    public static final Parcelable.Creator<IcyHeaders> CREATOR = new Parcelable.Creator<IcyHeaders>() { // from class: com.google.android.exoplayer2.metadata.icy.IcyHeaders.1
        @Override // android.os.Parcelable.Creator
        public IcyHeaders createFromParcel(Parcel in) {
            return new IcyHeaders(in);
        }

        @Override // android.os.Parcelable.Creator
        public IcyHeaders[] newArray(int size) {
            return new IcyHeaders[size];
        }
    };
    public static final String REQUEST_HEADER_ENABLE_METADATA_NAME = "Icy-MetaData";
    public static final String REQUEST_HEADER_ENABLE_METADATA_VALUE = "1";
    private static final String RESPONSE_HEADER_BITRATE = "icy-br";
    private static final String RESPONSE_HEADER_GENRE = "icy-genre";
    private static final String RESPONSE_HEADER_METADATA_INTERVAL = "icy-metaint";
    private static final String RESPONSE_HEADER_NAME = "icy-name";
    private static final String RESPONSE_HEADER_PUB = "icy-pub";
    private static final String RESPONSE_HEADER_URL = "icy-url";
    private static final String TAG = "IcyHeaders";
    public final int bitrate;
    public final String genre;
    public final boolean isPublic;
    public final int metadataInterval;
    public final String name;
    public final String url;

    @Override // com.google.android.exoplayer2.metadata.Metadata.Entry
    public /* synthetic */ byte[] getWrappedMetadataBytes() {
        return Metadata.Entry.CC.$default$getWrappedMetadataBytes(this);
    }

    @Override // com.google.android.exoplayer2.metadata.Metadata.Entry
    public /* synthetic */ Format getWrappedMetadataFormat() {
        return Metadata.Entry.CC.$default$getWrappedMetadataFormat(this);
    }

    public static IcyHeaders parse(Map<String, List<String>> responseHeaders) {
        boolean icyHeadersPresent = false;
        int bitrate = -1;
        String genre = null;
        String name = null;
        String url = null;
        boolean isPublic = false;
        int metadataInterval = -1;
        List<String> headers = responseHeaders.get(RESPONSE_HEADER_BITRATE);
        if (headers != null) {
            String bitrateHeader = headers.get(0);
            try {
                bitrate = Integer.parseInt(bitrateHeader) * 1000;
                if (bitrate > 0) {
                    icyHeadersPresent = true;
                } else {
                    Log.w(TAG, "Invalid bitrate: " + bitrateHeader);
                    bitrate = -1;
                }
            } catch (NumberFormatException e) {
                Log.w(TAG, "Invalid bitrate header: " + bitrateHeader);
            }
        }
        List<String> headers2 = responseHeaders.get(RESPONSE_HEADER_GENRE);
        if (headers2 != null) {
            String genre2 = headers2.get(0);
            genre = genre2;
            icyHeadersPresent = true;
        }
        List<String> headers3 = responseHeaders.get(RESPONSE_HEADER_NAME);
        if (headers3 != null) {
            String name2 = headers3.get(0);
            name = name2;
            icyHeadersPresent = true;
        }
        List<String> headers4 = responseHeaders.get(RESPONSE_HEADER_URL);
        if (headers4 != null) {
            String url2 = headers4.get(0);
            url = url2;
            icyHeadersPresent = true;
        }
        List<String> headers5 = responseHeaders.get(RESPONSE_HEADER_PUB);
        if (headers5 != null) {
            isPublic = headers5.get(0).equals(REQUEST_HEADER_ENABLE_METADATA_VALUE);
            icyHeadersPresent = true;
        }
        List<String> headers6 = responseHeaders.get(RESPONSE_HEADER_METADATA_INTERVAL);
        if (headers6 != null) {
            String metadataIntervalHeader = headers6.get(0);
            try {
                metadataInterval = Integer.parseInt(metadataIntervalHeader);
                if (metadataInterval > 0) {
                    icyHeadersPresent = true;
                } else {
                    Log.w(TAG, "Invalid metadata interval: " + metadataIntervalHeader);
                    metadataInterval = -1;
                }
            } catch (NumberFormatException e2) {
                Log.w(TAG, "Invalid metadata interval: " + metadataIntervalHeader);
            }
        }
        if (icyHeadersPresent) {
            return new IcyHeaders(bitrate, genre, name, url, isPublic, metadataInterval);
        }
        return null;
    }

    public IcyHeaders(int bitrate, String genre, String name, String url, boolean isPublic, int metadataInterval) {
        Assertions.checkArgument(metadataInterval == -1 || metadataInterval > 0);
        this.bitrate = bitrate;
        this.genre = genre;
        this.name = name;
        this.url = url;
        this.isPublic = isPublic;
        this.metadataInterval = metadataInterval;
    }

    IcyHeaders(Parcel in) {
        this.bitrate = in.readInt();
        this.genre = in.readString();
        this.name = in.readString();
        this.url = in.readString();
        this.isPublic = Util.readBoolean(in);
        this.metadataInterval = in.readInt();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        IcyHeaders other = (IcyHeaders) obj;
        return this.bitrate == other.bitrate && Util.areEqual(this.genre, other.genre) && Util.areEqual(this.name, other.name) && Util.areEqual(this.url, other.url) && this.isPublic == other.isPublic && this.metadataInterval == other.metadataInterval;
    }

    public int hashCode() {
        int result = (17 * 31) + this.bitrate;
        int result2 = result * 31;
        String str = this.genre;
        int i = 0;
        int result3 = (result2 + (str != null ? str.hashCode() : 0)) * 31;
        String str2 = this.name;
        int result4 = (result3 + (str2 != null ? str2.hashCode() : 0)) * 31;
        String str3 = this.url;
        if (str3 != null) {
            i = str3.hashCode();
        }
        return ((((result4 + i) * 31) + (this.isPublic ? 1 : 0)) * 31) + this.metadataInterval;
    }

    public String toString() {
        return "IcyHeaders: name=\"" + this.name + "\", genre=\"" + this.genre + "\", bitrate=" + this.bitrate + ", metadataInterval=" + this.metadataInterval;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.bitrate);
        dest.writeString(this.genre);
        dest.writeString(this.name);
        dest.writeString(this.url);
        Util.writeBoolean(dest, this.isPublic);
        dest.writeInt(this.metadataInterval);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }
}
