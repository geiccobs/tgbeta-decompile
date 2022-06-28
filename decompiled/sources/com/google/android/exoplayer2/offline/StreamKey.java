package com.google.android.exoplayer2.offline;

import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes3.dex */
public final class StreamKey implements Comparable<StreamKey>, Parcelable {
    public static final Parcelable.Creator<StreamKey> CREATOR = new Parcelable.Creator<StreamKey>() { // from class: com.google.android.exoplayer2.offline.StreamKey.1
        @Override // android.os.Parcelable.Creator
        public StreamKey createFromParcel(Parcel in) {
            return new StreamKey(in);
        }

        @Override // android.os.Parcelable.Creator
        public StreamKey[] newArray(int size) {
            return new StreamKey[size];
        }
    };
    public final int groupIndex;
    public final int periodIndex;
    public final int trackIndex;

    public StreamKey(int groupIndex, int trackIndex) {
        this(0, groupIndex, trackIndex);
    }

    public StreamKey(int periodIndex, int groupIndex, int trackIndex) {
        this.periodIndex = periodIndex;
        this.groupIndex = groupIndex;
        this.trackIndex = trackIndex;
    }

    StreamKey(Parcel in) {
        this.periodIndex = in.readInt();
        this.groupIndex = in.readInt();
        this.trackIndex = in.readInt();
    }

    public String toString() {
        return this.periodIndex + "." + this.groupIndex + "." + this.trackIndex;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StreamKey that = (StreamKey) o;
        return this.periodIndex == that.periodIndex && this.groupIndex == that.groupIndex && this.trackIndex == that.trackIndex;
    }

    public int hashCode() {
        int result = this.periodIndex;
        return (((result * 31) + this.groupIndex) * 31) + this.trackIndex;
    }

    public int compareTo(StreamKey o) {
        int result = this.periodIndex - o.periodIndex;
        if (result == 0) {
            int result2 = this.groupIndex - o.groupIndex;
            if (result2 == 0) {
                return this.trackIndex - o.trackIndex;
            }
            return result2;
        }
        return result;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.periodIndex);
        dest.writeInt(this.groupIndex);
        dest.writeInt(this.trackIndex);
    }
}
