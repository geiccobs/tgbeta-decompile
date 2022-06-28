package com.google.android.exoplayer2.source;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.util.Assertions;
import java.util.Arrays;
/* loaded from: classes3.dex */
public final class TrackGroup implements Parcelable {
    public static final Parcelable.Creator<TrackGroup> CREATOR = new Parcelable.Creator<TrackGroup>() { // from class: com.google.android.exoplayer2.source.TrackGroup.1
        @Override // android.os.Parcelable.Creator
        public TrackGroup createFromParcel(Parcel in) {
            return new TrackGroup(in);
        }

        @Override // android.os.Parcelable.Creator
        public TrackGroup[] newArray(int size) {
            return new TrackGroup[size];
        }
    };
    private final Format[] formats;
    private int hashCode;
    public final int length;

    public TrackGroup(Format... formats) {
        Assertions.checkState(formats.length > 0);
        this.formats = formats;
        this.length = formats.length;
    }

    TrackGroup(Parcel in) {
        int readInt = in.readInt();
        this.length = readInt;
        this.formats = new Format[readInt];
        for (int i = 0; i < this.length; i++) {
            this.formats[i] = (Format) in.readParcelable(Format.class.getClassLoader());
        }
    }

    public Format getFormat(int index) {
        return this.formats[index];
    }

    public int indexOf(Format format) {
        int i = 0;
        while (true) {
            Format[] formatArr = this.formats;
            if (i < formatArr.length) {
                if (format != formatArr[i]) {
                    i++;
                } else {
                    return i;
                }
            } else {
                return -1;
            }
        }
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            int result = (17 * 31) + Arrays.hashCode(this.formats);
            this.hashCode = result;
        }
        return this.hashCode;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        TrackGroup other = (TrackGroup) obj;
        return this.length == other.length && Arrays.equals(this.formats, other.formats);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.length);
        for (int i = 0; i < this.length; i++) {
            dest.writeParcelable(this.formats[i], 0);
        }
    }
}
