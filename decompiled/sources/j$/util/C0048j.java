package j$.util;

import java.util.NoSuchElementException;
/* renamed from: j$.util.j */
/* loaded from: classes2.dex */
public final class C0048j {
    private static final C0048j c = new C0048j();
    private final boolean a;
    private final double b;

    private C0048j() {
        this.a = false;
        this.b = Double.NaN;
    }

    private C0048j(double d) {
        this.a = true;
        this.b = d;
    }

    public static C0048j a() {
        return c;
    }

    public static C0048j d(double d) {
        return new C0048j(d);
    }

    public double b() {
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
        if (!(obj instanceof C0048j)) {
            return false;
        }
        C0048j c0048j = (C0048j) obj;
        boolean z = this.a;
        if (!z || !c0048j.a) {
            if (z == c0048j.a) {
                return true;
            }
        } else if (Double.compare(this.b, c0048j.b) == 0) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        if (this.a) {
            long doubleToLongBits = Double.doubleToLongBits(this.b);
            return (int) (doubleToLongBits ^ (doubleToLongBits >>> 32));
        }
        return 0;
    }

    public String toString() {
        return this.a ? String.format("OptionalDouble[%s]", Double.valueOf(this.b)) : "OptionalDouble.empty";
    }
}
