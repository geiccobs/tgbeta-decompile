package org.telegram.ui.Components;
/* loaded from: classes5.dex */
public class IntSize {
    public int height;
    public int width;

    public IntSize() {
    }

    public IntSize(IntSize size) {
        this.width = size.width;
        this.height = size.height;
    }

    public IntSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void set(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IntSize intSize = (IntSize) o;
        return this.width == intSize.width && this.height == intSize.height;
    }

    public int hashCode() {
        int result = this.width;
        return (result * 31) + this.height;
    }

    public String toString() {
        return "IntSize(" + this.width + ", " + this.height + ")";
    }
}
