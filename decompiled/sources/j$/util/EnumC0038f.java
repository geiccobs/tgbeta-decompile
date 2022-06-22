package j$.util;

import j$.wrappers.B0;
import j$.wrappers.D0;
import j$.wrappers.F0;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
/* renamed from: j$.util.f */
/* loaded from: classes2.dex */
public enum EnumC0038f implements Comparator, AbstractC0037e {
    INSTANCE;

    @Override // java.util.Comparator
    public int compare(Object obj, Object obj2) {
        return ((Comparable) obj).compareTo((Comparable) obj2);
    }

    @Override // java.util.Comparator
    public Comparator reversed() {
        return Comparator$CC.reverseOrder();
    }

    @Override // java.util.Comparator
    public Comparator thenComparing(Comparator comparator) {
        comparator.getClass();
        return new C0035c(this, comparator);
    }

    @Override // java.util.Comparator
    public Comparator thenComparingDouble(ToDoubleFunction toDoubleFunction) {
        j$.util.function.z a2 = B0.a(toDoubleFunction);
        a2.getClass();
        return AbstractC0033a.H(this, new C0036d(a2));
    }

    @Override // java.util.Comparator
    public Comparator thenComparingInt(ToIntFunction toIntFunction) {
        return AbstractC0033a.H(this, Comparator$CC.comparingInt(D0.a(toIntFunction)));
    }

    @Override // java.util.Comparator
    public Comparator thenComparingLong(ToLongFunction toLongFunction) {
        j$.util.function.A a2 = F0.a(toLongFunction);
        a2.getClass();
        return AbstractC0033a.H(this, new C0036d(a2));
    }

    @Override // java.util.Comparator
    public Comparator thenComparing(Function function) {
        return AbstractC0033a.H(this, Comparator$CC.comparing(j$.wrappers.M.a(function)));
    }

    @Override // java.util.Comparator
    public Comparator thenComparing(Function function, Comparator comparator) {
        j$.util.function.Function a2 = j$.wrappers.M.a(function);
        a2.getClass();
        comparator.getClass();
        return AbstractC0033a.H(this, new C0035c(comparator, a2));
    }
}
