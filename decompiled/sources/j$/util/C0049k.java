package j$.util;

import java.util.NoSuchElementException;
/* renamed from: j$.util.k */
/* loaded from: classes2.dex */
public final class C0049k {
    private static final C0049k c = new C0049k();
    private final boolean a;
    private final int b;

    private C0049k() {
        this.a = false;
        this.b = 0;
    }

    private C0049k(int i) {
        this.a = true;
        this.b = i;
    }

    public static C0049k a() {
        return c;
    }

    public static C0049k d(int i) {
        return new C0049k(i);
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
        if (!(obj instanceof C0049k)) {
            return false;
        }
        C0049k c0049k = (C0049k) obj;
        boolean z = this.a;
        if (!z || !c0049k.a) {
            if (z == c0049k.a) {
                return true;
            }
        } else if (this.b == c0049k.b) {
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
