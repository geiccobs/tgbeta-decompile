package org.webrtc;
/* loaded from: classes5.dex */
public class Size {
    public int height;
    public int width;

    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public String toString() {
        return this.width + "x" + this.height;
    }

    public boolean equals(Object other) {
        if (!(other instanceof Size)) {
            return false;
        }
        Size otherSize = (Size) other;
        return this.width == otherSize.width && this.height == otherSize.height;
    }

    public int hashCode() {
        return (this.width * 65537) + 1 + this.height;
    }
}
