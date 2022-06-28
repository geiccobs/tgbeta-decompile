package j$.util;

import j$.util.Comparators;
import j$.util.function.Function;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import java.util.Collections;
/* loaded from: classes2.dex */
public interface Comparator<T> {

    /* renamed from: j$.util.Comparator$-EL */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class EL {
        public static /* synthetic */ java.util.Comparator reversed(java.util.Comparator comparator) {
            return comparator instanceof Comparator ? ((Comparator) comparator).reversed() : Collections.reverseOrder(comparator);
        }

        public static /* synthetic */ java.util.Comparator thenComparing(java.util.Comparator comparator, Function function) {
            return comparator instanceof Comparator ? ((Comparator) comparator).thenComparing(function) : CC.$default$thenComparing(comparator, function);
        }

        public static /* synthetic */ java.util.Comparator thenComparing(java.util.Comparator comparator, Function function, java.util.Comparator comparator2) {
            return comparator instanceof Comparator ? ((Comparator) comparator).thenComparing(function, comparator2) : CC.$default$thenComparing(comparator, function, comparator2);
        }

        public static /* synthetic */ java.util.Comparator thenComparing(java.util.Comparator comparator, java.util.Comparator comparator2) {
            return comparator instanceof Comparator ? ((Comparator) comparator).thenComparing(comparator2) : CC.$default$thenComparing(comparator, comparator2);
        }

        public static /* synthetic */ java.util.Comparator thenComparingDouble(java.util.Comparator comparator, ToDoubleFunction toDoubleFunction) {
            return comparator instanceof Comparator ? ((Comparator) comparator).thenComparingDouble(toDoubleFunction) : CC.$default$thenComparingDouble(comparator, toDoubleFunction);
        }

        public static /* synthetic */ java.util.Comparator thenComparingInt(java.util.Comparator comparator, ToIntFunction toIntFunction) {
            return comparator instanceof Comparator ? ((Comparator) comparator).thenComparingInt(toIntFunction) : CC.$default$thenComparingInt(comparator, toIntFunction);
        }

        public static /* synthetic */ java.util.Comparator thenComparingLong(java.util.Comparator comparator, ToLongFunction toLongFunction) {
            return comparator instanceof Comparator ? ((Comparator) comparator).thenComparingLong(toLongFunction) : CC.$default$thenComparingLong(comparator, toLongFunction);
        }
    }

    int compare(T t, T t2);

    boolean equals(Object obj);

    java.util.Comparator<T> reversed();

    <U extends Comparable<? super U>> java.util.Comparator<T> thenComparing(Function<? super T, ? extends U> function);

    <U> java.util.Comparator<T> thenComparing(Function<? super T, ? extends U> function, java.util.Comparator<? super U> comparator);

    java.util.Comparator<T> thenComparing(java.util.Comparator<? super T> comparator);

    java.util.Comparator<T> thenComparingDouble(ToDoubleFunction<? super T> toDoubleFunction);

    java.util.Comparator<T> thenComparingInt(ToIntFunction<? super T> toIntFunction);

    java.util.Comparator<T> thenComparingLong(ToLongFunction<? super T> toLongFunction);

    /* renamed from: j$.util.Comparator$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static java.util.Comparator $default$thenComparing(java.util.Comparator _this, java.util.Comparator comparator) {
            comparator.getClass();
            return new Comparator$$ExternalSyntheticLambda0(_this, comparator);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<? super T, ? extends U> */
        public static java.util.Comparator $default$thenComparing(java.util.Comparator _this, Function function, java.util.Comparator comparator) {
            return EL.thenComparing(_this, comparing(function, comparator));
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<? super T, ? extends U> */
        public static java.util.Comparator $default$thenComparing(java.util.Comparator _this, Function function) {
            return EL.thenComparing(_this, comparing(function));
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.ToIntFunction != java.util.function.ToIntFunction<? super T> */
        public static java.util.Comparator $default$thenComparingInt(java.util.Comparator _this, ToIntFunction toIntFunction) {
            return EL.thenComparing(_this, comparingInt(toIntFunction));
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.ToLongFunction != java.util.function.ToLongFunction<? super T> */
        public static java.util.Comparator $default$thenComparingLong(java.util.Comparator _this, ToLongFunction toLongFunction) {
            return EL.thenComparing(_this, comparingLong(toLongFunction));
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.ToDoubleFunction != java.util.function.ToDoubleFunction<? super T> */
        public static java.util.Comparator $default$thenComparingDouble(java.util.Comparator _this, ToDoubleFunction toDoubleFunction) {
            return EL.thenComparing(_this, comparingDouble(toDoubleFunction));
        }

        public static <T extends Comparable<? super T>> java.util.Comparator<T> reverseOrder() {
            return Collections.reverseOrder();
        }

        public static <T extends Comparable<? super T>> java.util.Comparator<T> naturalOrder() {
            return Comparators.NaturalOrderComparator.INSTANCE;
        }

        public static <T> java.util.Comparator<T> nullsFirst(java.util.Comparator<? super T> comparator) {
            return new Comparators.NullComparator(true, comparator);
        }

        public static <T> java.util.Comparator<T> nullsLast(java.util.Comparator<? super T> comparator) {
            return new Comparators.NullComparator(false, comparator);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<? super T, ? extends U> */
        public static <T, U> java.util.Comparator<T> comparing(Function<? super T, ? extends U> function, java.util.Comparator<? super U> keyComparator) {
            function.getClass();
            keyComparator.getClass();
            return new Comparator$$ExternalSyntheticLambda1(keyComparator, function);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<? super T, ? extends U extends java.lang.Comparable<? super U>> */
        public static <T, U extends Comparable<? super U>> java.util.Comparator<T> comparing(Function<? super T, ? extends U> function) {
            function.getClass();
            return new Comparator$$ExternalSyntheticLambda2(function);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.ToIntFunction != java.util.function.ToIntFunction<? super T> */
        public static <T> java.util.Comparator<T> comparingInt(ToIntFunction<? super T> toIntFunction) {
            toIntFunction.getClass();
            return new Comparator$$ExternalSyntheticLambda4(toIntFunction);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.ToLongFunction != java.util.function.ToLongFunction<? super T> */
        public static <T> java.util.Comparator<T> comparingLong(ToLongFunction<? super T> toLongFunction) {
            toLongFunction.getClass();
            return new Comparator$$ExternalSyntheticLambda5(toLongFunction);
        }

        public static /* synthetic */ int lambda$comparingLong$6043328a$1(ToLongFunction keyExtractor, Object c1, Object c2) {
            return (keyExtractor.applyAsLong(c1) > keyExtractor.applyAsLong(c2) ? 1 : (keyExtractor.applyAsLong(c1) == keyExtractor.applyAsLong(c2) ? 0 : -1));
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.ToDoubleFunction != java.util.function.ToDoubleFunction<? super T> */
        public static <T> java.util.Comparator<T> comparingDouble(ToDoubleFunction<? super T> toDoubleFunction) {
            toDoubleFunction.getClass();
            return new Comparator$$ExternalSyntheticLambda3(toDoubleFunction);
        }
    }
}
