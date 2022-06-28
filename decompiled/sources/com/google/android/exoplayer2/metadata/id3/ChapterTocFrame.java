package com.google.android.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.exoplayer2.util.Util;
import java.util.Arrays;
/* loaded from: classes3.dex */
public final class ChapterTocFrame extends Id3Frame {
    public static final Parcelable.Creator<ChapterTocFrame> CREATOR = new Parcelable.Creator<ChapterTocFrame>() { // from class: com.google.android.exoplayer2.metadata.id3.ChapterTocFrame.1
        @Override // android.os.Parcelable.Creator
        public ChapterTocFrame createFromParcel(Parcel in) {
            return new ChapterTocFrame(in);
        }

        @Override // android.os.Parcelable.Creator
        public ChapterTocFrame[] newArray(int size) {
            return new ChapterTocFrame[size];
        }
    };
    public static final String ID = "CTOC";
    public final String[] children;
    public final String elementId;
    public final boolean isOrdered;
    public final boolean isRoot;
    private final Id3Frame[] subFrames;

    public ChapterTocFrame(String elementId, boolean isRoot, boolean isOrdered, String[] children, Id3Frame[] subFrames) {
        super(ID);
        this.elementId = elementId;
        this.isRoot = isRoot;
        this.isOrdered = isOrdered;
        this.children = children;
        this.subFrames = subFrames;
    }

    ChapterTocFrame(Parcel in) {
        super(ID);
        this.elementId = (String) Util.castNonNull(in.readString());
        boolean z = false;
        this.isRoot = in.readByte() != 0;
        this.isOrdered = in.readByte() != 0 ? true : z;
        this.children = (String[]) Util.castNonNull(in.createStringArray());
        int subFrameCount = in.readInt();
        this.subFrames = new Id3Frame[subFrameCount];
        for (int i = 0; i < subFrameCount; i++) {
            this.subFrames[i] = (Id3Frame) in.readParcelable(Id3Frame.class.getClassLoader());
        }
    }

    public int getSubFrameCount() {
        return this.subFrames.length;
    }

    public Id3Frame getSubFrame(int index) {
        return this.subFrames[index];
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ChapterTocFrame other = (ChapterTocFrame) obj;
        return this.isRoot == other.isRoot && this.isOrdered == other.isOrdered && Util.areEqual(this.elementId, other.elementId) && Arrays.equals(this.children, other.children) && Arrays.equals(this.subFrames, other.subFrames);
    }

    public int hashCode() {
        int result = (17 * 31) + (this.isRoot ? 1 : 0);
        int result2 = ((result * 31) + (this.isOrdered ? 1 : 0)) * 31;
        String str = this.elementId;
        return result2 + (str != null ? str.hashCode() : 0);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        Id3Frame[] id3FrameArr;
        dest.writeString(this.elementId);
        dest.writeByte(this.isRoot ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isOrdered ? (byte) 1 : (byte) 0);
        dest.writeStringArray(this.children);
        dest.writeInt(this.subFrames.length);
        for (Id3Frame subFrame : this.subFrames) {
            dest.writeParcelable(subFrame, 0);
        }
    }
}
