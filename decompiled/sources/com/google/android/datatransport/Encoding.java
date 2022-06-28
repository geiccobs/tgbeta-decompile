package com.google.android.datatransport;
/* loaded from: classes3.dex */
public final class Encoding {
    private final String name;

    public static Encoding of(String name) {
        return new Encoding(name);
    }

    public String getName() {
        return this.name;
    }

    private Encoding(String name) {
        if (name == null) {
            throw new NullPointerException("name is null");
        }
        this.name = name;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Encoding) {
            return this.name.equals(((Encoding) o).name);
        }
        return false;
    }

    public int hashCode() {
        int h = 1000003 ^ this.name.hashCode();
        return h;
    }

    public String toString() {
        return "Encoding{name=\"" + this.name + "\"}";
    }
}
