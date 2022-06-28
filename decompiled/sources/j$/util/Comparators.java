package j$.util;

import j$.util.Comparator;
import j$.util.function.Function;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import j$.wrappers.C$r8$wrapper$java$util$function$Function$VWRP;
import j$.wrappers.C$r8$wrapper$java$util$function$ToDoubleFunction$VWRP;
import j$.wrappers.C$r8$wrapper$java$util$function$ToIntFunction$VWRP;
import j$.wrappers.C$r8$wrapper$java$util$function$ToLongFunction$VWRP;
import java.io.Serializable;
/* loaded from: classes2.dex */
class Comparators {
    private Comparators() {
        throw new AssertionError("no instances");
    }

    /* loaded from: classes2.dex */
    public enum NaturalOrderComparator implements java.util.Comparator<Comparable<Object>>, Comparator<Comparable<Object>> {
        INSTANCE;

        @Override // j$.util.Comparator
        public /* synthetic */ java.util.Comparator<Comparable<Object>> thenComparing(Function function) {
            return Comparator.CC.$default$thenComparing(this, function);
        }

        @Override // j$.util.Comparator
        public /* synthetic */ java.util.Comparator<Comparable<Object>> thenComparing(Function function, java.util.Comparator comparator) {
            return Comparator.CC.$default$thenComparing(this, function, comparator);
        }

        @Override // java.util.Comparator, j$.util.Comparator
        public /* synthetic */ java.util.Comparator thenComparing(java.util.Comparator comparator) {
            return Comparator.CC.$default$thenComparing(this, comparator);
        }

        @Override // java.util.Comparator
        public /* synthetic */ java.util.Comparator<Comparable<Object>> thenComparing(java.util.function.Function function) {
            return thenComparing(C$r8$wrapper$java$util$function$Function$VWRP.convert(function));
        }

        @Override // java.util.Comparator
        public /* synthetic */ java.util.Comparator<Comparable<Object>> thenComparing(java.util.function.Function function, java.util.Comparator comparator) {
            return thenComparing(C$r8$wrapper$java$util$function$Function$VWRP.convert(function), comparator);
        }

        @Override // j$.util.Comparator
        public /* synthetic */ java.util.Comparator<Comparable<Object>> thenComparingDouble(ToDoubleFunction<? super Comparable<Object>> toDoubleFunction) {
            return Comparator.CC.$default$thenComparingDouble(this, toDoubleFunction);
        }

        @Override // java.util.Comparator
        public /* synthetic */ java.util.Comparator<Comparable<Object>> thenComparingDouble(java.util.function.ToDoubleFunction<? super Comparable<Object>> toDoubleFunction) {
            return thenComparingDouble(C$r8$wrapper$java$util$function$ToDoubleFunction$VWRP.convert(toDoubleFunction));
        }

        @Override // j$.util.Comparator
        public /* synthetic */ java.util.Comparator<Comparable<Object>> thenComparingInt(ToIntFunction<? super Comparable<Object>> toIntFunction) {
            return Comparator.CC.$default$thenComparingInt(this, toIntFunction);
        }

        @Override // java.util.Comparator
        public /* synthetic */ java.util.Comparator<Comparable<Object>> thenComparingInt(java.util.function.ToIntFunction<? super Comparable<Object>> toIntFunction) {
            return thenComparingInt(C$r8$wrapper$java$util$function$ToIntFunction$VWRP.convert(toIntFunction));
        }

        @Override // j$.util.Comparator
        public /* synthetic */ java.util.Comparator<Comparable<Object>> thenComparingLong(ToLongFunction<? super Comparable<Object>> toLongFunction) {
            return Comparator.CC.$default$thenComparingLong(this, toLongFunction);
        }

        @Override // java.util.Comparator
        public /* synthetic */ java.util.Comparator<Comparable<Object>> thenComparingLong(java.util.function.ToLongFunction<? super Comparable<Object>> toLongFunction) {
            return thenComparingLong(C$r8$wrapper$java$util$function$ToLongFunction$VWRP.convert(toLongFunction));
        }

        public int compare(Comparable<Object> c1, Comparable<Object> c2) {
            return c1.compareTo(c2);
        }

        @Override // java.util.Comparator, j$.util.Comparator
        public java.util.Comparator<Comparable<Object>> reversed() {
            return Comparator.CC.reverseOrder();
        }
    }

    /* loaded from: classes2.dex */
    static final class NullComparator<T> implements java.util.Comparator<T>, Serializable, Comparator<T> {
        private static final long serialVersionUID = -7569533591570686392L;
        private final boolean nullFirst;
        private final java.util.Comparator<T> real;

        @Override // j$.util.Comparator
        public /* synthetic */ java.util.Comparator thenComparing(Function function) {
            return Comparator.CC.$default$thenComparing(this, function);
        }

        @Override // j$.util.Comparator
        public /* synthetic */ java.util.Comparator thenComparing(Function function, java.util.Comparator comparator) {
            return Comparator.CC.$default$thenComparing(this, function, comparator);
        }

        @Override // java.util.Comparator
        public /* synthetic */ java.util.Comparator thenComparing(java.util.function.Function function) {
            return thenComparing(C$r8$wrapper$java$util$function$Function$VWRP.convert(function));
        }

        @Override // java.util.Comparator
        public /* synthetic */ java.util.Comparator thenComparing(java.util.function.Function function, java.util.Comparator comparator) {
            return thenComparing(C$r8$wrapper$java$util$function$Function$VWRP.convert(function), comparator);
        }

        @Override // j$.util.Comparator
        public /* synthetic */ java.util.Comparator thenComparingDouble(ToDoubleFunction toDoubleFunction) {
            return Comparator.CC.$default$thenComparingDouble(this, toDoubleFunction);
        }

        @Override // java.util.Comparator
        public /* synthetic */ java.util.Comparator thenComparingDouble(java.util.function.ToDoubleFunction toDoubleFunction) {
            return thenComparingDouble(C$r8$wrapper$java$util$function$ToDoubleFunction$VWRP.convert(toDoubleFunction));
        }

        @Override // j$.util.Comparator
        public /* synthetic */ java.util.Comparator thenComparingInt(ToIntFunction toIntFunction) {
            return Comparator.CC.$default$thenComparingInt(this, toIntFunction);
        }

        @Override // java.util.Comparator
        public /* synthetic */ java.util.Comparator thenComparingInt(java.util.function.ToIntFunction toIntFunction) {
            return thenComparingInt(C$r8$wrapper$java$util$function$ToIntFunction$VWRP.convert(toIntFunction));
        }

        @Override // j$.util.Comparator
        public /* synthetic */ java.util.Comparator thenComparingLong(ToLongFunction toLongFunction) {
            return Comparator.CC.$default$thenComparingLong(this, toLongFunction);
        }

        @Override // java.util.Comparator
        public /* synthetic */ java.util.Comparator thenComparingLong(java.util.function.ToLongFunction toLongFunction) {
            return thenComparingLong(C$r8$wrapper$java$util$function$ToLongFunction$VWRP.convert(toLongFunction));
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Comparators$NullComparator != java.util.Comparators$NullComparator<T> */
        /* JADX WARN: Multi-variable type inference failed */
        public NullComparator(boolean nullFirst, java.util.Comparator<? super T> real) {
            this.nullFirst = nullFirst;
            this.real = real;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Comparators$NullComparator != java.util.Comparators$NullComparator<T> */
        @Override // java.util.Comparator, j$.util.Comparator
        public int compare(T a, T b) {
            if (a == null) {
                if (b == null) {
                    return 0;
                }
                return this.nullFirst ? -1 : 1;
            } else if (b == null) {
                return this.nullFirst ? 1 : -1;
            } else {
                java.util.Comparator<T> comparator = this.real;
                if (comparator != null) {
                    return comparator.compare(a, b);
                }
                return 0;
            }
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Comparators$NullComparator != java.util.Comparators$NullComparator<T> */
        @Override // java.util.Comparator, j$.util.Comparator
        public java.util.Comparator<T> thenComparing(java.util.Comparator<? super T> other) {
            other.getClass();
            boolean z = this.nullFirst;
            java.util.Comparator<T> comparator = this.real;
            return new NullComparator(z, comparator == null ? other : Comparator.EL.thenComparing(comparator, other));
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Comparators$NullComparator != java.util.Comparators$NullComparator<T> */
        @Override // java.util.Comparator, j$.util.Comparator
        public java.util.Comparator<T> reversed() {
            boolean z = !this.nullFirst;
            java.util.Comparator<T> comparator = this.real;
            return new NullComparator(z, comparator == null ? null : Comparator.EL.reversed(comparator));
        }
    }
}
