package com.google.android.exoplayer2.extractor;
/* loaded from: classes3.dex */
public final class SeekPoint {
    public static final SeekPoint START = new SeekPoint(0, 0);
    public final long position;
    public final long timeUs;

    public SeekPoint(long timeUs, long position) {
        this.timeUs = timeUs;
        this.position = position;
    }

    public String toString() {
        return "[timeUs=" + this.timeUs + ", position=" + this.position + "]";
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SeekPoint other = (SeekPoint) obj;
        return this.timeUs == other.timeUs && this.position == other.position;
    }

    public int hashCode() {
        int result = (int) this.timeUs;
        return (result * 31) + ((int) this.position);
    }
}
