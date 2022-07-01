package j$.util;

import java.util.NoSuchElementException;
/* renamed from: j$.util.k */
/* loaded from: classes2.dex */
public final class C0044k {
    private static final C0044k c = new C0044k();
    private final boolean a;
    private final int b;

    private C0044k() {
        this.a = false;
        this.b = 0;
    }

    private C0044k(int i) {
        this.a = true;
        this.b = i;
    }

    public static C0044k a() {
        return c;
    }

    public static C0044k d(int i) {
        return new C0044k(i);
    }

    public int b() {
        if (this.a) {
            return this.b;
        }
        throw new NoSuchElementException("No value present");
    }

    public boolean c() {
        return this.a;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof C0044k)) {
            return false;
        }
        C0044k c0044k = (C0044k) obj;
        boolean z = this.a;
        if (!z || !c0044k.a) {
            if (z == c0044k.a) {
                return true;
            }
        } else if (this.b == c0044k.b) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        if (this.a) {
            return this.b;
        }
        return 0;
    }

    public String toString() {
        return this.a ? String.format("OptionalInt[%s]", Integer.valueOf(this.b)) : "OptionalInt.empty";
    }
}
