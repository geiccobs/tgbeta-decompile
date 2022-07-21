package j$.util;

import java.util.NoSuchElementException;
/* renamed from: j$.util.j */
/* loaded from: classes2.dex */
public final class C0049j {
    private static final C0049j c = new C0049j();
    private final boolean a;
    private final double b;

    private C0049j() {
        this.a = false;
        this.b = Double.NaN;
    }

    private C0049j(double d) {
        this.a = true;
        this.b = d;
    }

    public static C0049j a() {
        return c;
    }

    public static C0049j d(double d) {
        return new C0049j(d);
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
        if (!(obj instanceof C0049j)) {
            return false;
        }
        C0049j c0049j = (C0049j) obj;
        boolean z = this.a;
        if (!z || !c0049j.a) {
            if (z == c0049j.a) {
                return true;
            }
        } else if (Double.compare(this.b, c0049j.b) == 0) {
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
