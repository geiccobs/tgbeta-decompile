package com.google.android.exoplayer2.offline;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import com.microsoft.appcenter.Constants;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/* loaded from: classes3.dex */
public final class DownloadRequest implements Parcelable {
    public static final Parcelable.Creator<DownloadRequest> CREATOR = new Parcelable.Creator<DownloadRequest>() { // from class: com.google.android.exoplayer2.offline.DownloadRequest.1
        @Override // android.os.Parcelable.Creator
        public DownloadRequest createFromParcel(Parcel in) {
            return new DownloadRequest(in);
        }

        @Override // android.os.Parcelable.Creator
        public DownloadRequest[] newArray(int size) {
            return new DownloadRequest[size];
        }
    };
    public static final String TYPE_DASH = "dash";
    public static final String TYPE_HLS = "hls";
    public static final String TYPE_PROGRESSIVE = "progressive";
    public static final String TYPE_SS = "ss";
    public final String customCacheKey;
    public final byte[] data;
    public final String id;
    public final List<StreamKey> streamKeys;
    public final String type;
    public final Uri uri;

    /* loaded from: classes3.dex */
    public static class UnsupportedRequestException extends IOException {
    }

    public DownloadRequest(String id, String type, Uri uri, List<StreamKey> streamKeys, String customCacheKey, byte[] data) {
        if (TYPE_DASH.equals(type) || TYPE_HLS.equals(type) || TYPE_SS.equals(type)) {
            boolean z = customCacheKey == null;
            Assertions.checkArgument(z, "customCacheKey must be null for type: " + type);
        }
        this.id = id;
        this.type = type;
        this.uri = uri;
        ArrayList<StreamKey> mutableKeys = new ArrayList<>(streamKeys);
        Collections.sort(mutableKeys);
        this.streamKeys = Collections.unmodifiableList(mutableKeys);
        this.customCacheKey = customCacheKey;
        this.data = data != null ? Arrays.copyOf(data, data.length) : Util.EMPTY_BYTE_ARRAY;
    }

    DownloadRequest(Parcel in) {
        this.id = (String) Util.castNonNull(in.readString());
        this.type = (String) Util.castNonNull(in.readString());
        this.uri = Uri.parse((String) Util.castNonNull(in.readString()));
        int streamKeyCount = in.readInt();
        ArrayList<StreamKey> mutableStreamKeys = new ArrayList<>(streamKeyCount);
        for (int i = 0; i < streamKeyCount; i++) {
            mutableStreamKeys.add((StreamKey) in.readParcelable(StreamKey.class.getClassLoader()));
        }
        this.streamKeys = Collections.unmodifiableList(mutableStreamKeys);
        this.customCacheKey = in.readString();
        this.data = (byte[]) Util.castNonNull(in.createByteArray());
    }

    public DownloadRequest copyWithId(String id) {
        return new DownloadRequest(id, this.type, this.uri, this.streamKeys, this.customCacheKey, this.data);
    }

    public DownloadRequest copyWithMergedRequest(DownloadRequest newRequest) {
        List<StreamKey> mergedKeys;
        Assertions.checkArgument(this.id.equals(newRequest.id));
        Assertions.checkArgument(this.type.equals(newRequest.type));
        if (this.streamKeys.isEmpty() || newRequest.streamKeys.isEmpty()) {
            mergedKeys = Collections.emptyList();
        } else {
            mergedKeys = new ArrayList<>(this.streamKeys);
            for (int i = 0; i < newRequest.streamKeys.size(); i++) {
                StreamKey newKey = newRequest.streamKeys.get(i);
                if (!mergedKeys.contains(newKey)) {
                    mergedKeys.add(newKey);
                }
            }
        }
        return new DownloadRequest(this.id, this.type, newRequest.uri, mergedKeys, newRequest.customCacheKey, newRequest.data);
    }

    public String toString() {
        return this.type + Constants.COMMON_SCHEMA_PREFIX_SEPARATOR + this.id;
    }

    public boolean equals(Object o) {
        if (!(o instanceof DownloadRequest)) {
            return false;
        }
        DownloadRequest that = (DownloadRequest) o;
        return this.id.equals(that.id) && this.type.equals(that.type) && this.uri.equals(that.uri) && this.streamKeys.equals(that.streamKeys) && Util.areEqual(this.customCacheKey, that.customCacheKey) && Arrays.equals(this.data, that.data);
    }

    public final int hashCode() {
        int result = this.type.hashCode();
        int result2 = ((((((((result * 31) + this.id.hashCode()) * 31) + this.type.hashCode()) * 31) + this.uri.hashCode()) * 31) + this.streamKeys.hashCode()) * 31;
        String str = this.customCacheKey;
        return ((result2 + (str != null ? str.hashCode() : 0)) * 31) + Arrays.hashCode(this.data);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.type);
        dest.writeString(this.uri.toString());
        dest.writeInt(this.streamKeys.size());
        for (int i = 0; i < this.streamKeys.size(); i++) {
            dest.writeParcelable(this.streamKeys.get(i), 0);
        }
        dest.writeString(this.customCacheKey);
        dest.writeByteArray(this.data);
    }
}
