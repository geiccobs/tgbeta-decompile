package j$.util;

import java.util.NoSuchElementException;
/* renamed from: j$.util.l */
/* loaded from: classes2.dex */
public final class C0046l {
    private static final C0046l c = new C0046l();
    private final boolean a;
    private final long b;

    private C0046l() {
        this.a = false;
        this.b = 0L;
    }

    private C0046l(long j) {
        this.a = true;
        this.b = j;
    }

    public static C0046l a() {
        return c;
    }

    public static C0046l d(long j) {
        return new C0046l(j);
    }

    public long b() {
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
        if (!(obj instanceof C0046l)) {
            return false;
        }
        C0046l c0046l = (C0046l) obj;
        boolean z = this.a;
        if (!z || !c0046l.a) {
            if (z == c0046l.a) {
                return true;
            }
        } else if (this.b == c0046l.b) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        if (this.a) {
            long j = this.b;
            return (int) (j ^ (j >>> 32));
        }
        return 0;
    }

    public String toString() {
        return this.a ? String.format("OptionalLong[%s]", Long.valueOf(this.b)) : "OptionalLong.empty";
    }
}
