package com.google.android.exoplayer2.metadata;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.util.Util;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes3.dex */
public final class Metadata implements Parcelable {
    public static final Parcelable.Creator<Metadata> CREATOR = new Parcelable.Creator<Metadata>() { // from class: com.google.android.exoplayer2.metadata.Metadata.1
        @Override // android.os.Parcelable.Creator
        public Metadata createFromParcel(Parcel in) {
            return new Metadata(in);
        }

        @Override // android.os.Parcelable.Creator
        public Metadata[] newArray(int size) {
            return new Metadata[size];
        }
    };
    private final Entry[] entries;

    /* loaded from: classes3.dex */
    public interface Entry extends Parcelable {
        byte[] getWrappedMetadataBytes();

        Format getWrappedMetadataFormat();

        /* renamed from: com.google.android.exoplayer2.metadata.Metadata$Entry$-CC */
        /* loaded from: classes3.dex */
        public final /* synthetic */ class CC {
            public static Format $default$getWrappedMetadataFormat(Entry _this) {
                return null;
            }

            public static byte[] $default$getWrappedMetadataBytes(Entry _this) {
                return null;
            }
        }
    }

    public Metadata(Entry... entries) {
        this.entries = entries;
    }

    public Metadata(List<? extends Entry> entries) {
        Entry[] entryArr = new Entry[entries.size()];
        this.entries = entryArr;
        entries.toArray(entryArr);
    }

    Metadata(Parcel in) {
        this.entries = new Entry[in.readInt()];
        int i = 0;
        while (true) {
            Entry[] entryArr = this.entries;
            if (i < entryArr.length) {
                entryArr[i] = (Entry) in.readParcelable(Entry.class.getClassLoader());
                i++;
            } else {
                return;
            }
        }
    }

    public int length() {
        return this.entries.length;
    }

    public Entry get(int index) {
        return this.entries[index];
    }

    public Metadata copyWithAppendedEntriesFrom(Metadata other) {
        if (other == null) {
            return this;
        }
        return copyWithAppendedEntries(other.entries);
    }

    public Metadata copyWithAppendedEntries(Entry... entriesToAppend) {
        if (entriesToAppend.length == 0) {
            return this;
        }
        return new Metadata((Entry[]) Util.nullSafeArrayConcatenation(this.entries, entriesToAppend));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Metadata other = (Metadata) obj;
        return Arrays.equals(this.entries, other.entries);
    }

    public int hashCode() {
        return Arrays.hashCode(this.entries);
    }

    public String toString() {
        return "entries=" + Arrays.toString(this.entries);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        Entry[] entryArr;
        dest.writeInt(this.entries.length);
        for (Entry entry : this.entries) {
            dest.writeParcelable(entry, 0);
        }
    }
}
