package j$.util.stream;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import j$.util.DoubleSummaryStatistics;
import j$.util.IntSummaryStatistics;
import j$.util.LongSummaryStatistics;
import j$.util.Map;
import j$.util.Objects;
import j$.util.Optional;
import j$.util.StringJoiner;
import j$.util.concurrent.ConcurrentMap;
import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.BinaryOperator;
import j$.util.function.Consumer;
import j$.util.function.Function;
import j$.util.function.Predicate;
import j$.util.function.Supplier;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import j$.util.stream.Collector;
import j$.util.stream.Collectors;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collector;
/* loaded from: classes2.dex */
public final class Collectors {
    static final Set<Collector.Characteristics> CH_CONCURRENT_ID = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.CONCURRENT, Collector.Characteristics.UNORDERED, Collector.Characteristics.IDENTITY_FINISH));
    static final Set<Collector.Characteristics> CH_CONCURRENT_NOID = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.CONCURRENT, Collector.Characteristics.UNORDERED));
    static final Set<Collector.Characteristics> CH_ID = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH));
    static final Set<Collector.Characteristics> CH_UNORDERED_ID = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.UNORDERED, Collector.Characteristics.IDENTITY_FINISH));
    static final Set<Collector.Characteristics> CH_NOID = Collections.emptySet();

    private Collectors() {
    }

    public static /* synthetic */ Object lambda$throwingMerger$0(Object u, Object v) {
        throw new IllegalStateException(String.format("Duplicate key %s", u));
    }

    private static <T> BinaryOperator<T> throwingMerger() {
        return Collectors$$ExternalSyntheticLambda30.INSTANCE;
    }

    public static <I, R> Function<I, R> castingIdentity() {
        return Collectors$$ExternalSyntheticLambda53.INSTANCE;
    }

    public static /* synthetic */ Object lambda$castingIdentity$1(Object i) {
        return i;
    }

    /* loaded from: classes2.dex */
    public static class CollectorImpl<T, A, R> implements Collector<T, A, R> {
        private final BiConsumer<A, T> accumulator;
        private final Set<Collector.Characteristics> characteristics;
        private final BinaryOperator<A> combiner;
        private final Function<A, R> finisher;
        private final Supplier<A> supplier;

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.BiConsumer != java.util.function.BiConsumer<A, T> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.function.BinaryOperator != java.util.function.BinaryOperator<A> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<A, R> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<A> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Collectors$CollectorImpl != java.util.stream.Collectors$CollectorImpl<T, A, R> */
        public CollectorImpl(Supplier<A> supplier, BiConsumer<A, T> biConsumer, BinaryOperator<A> binaryOperator, Function<A, R> function, Set<Collector.Characteristics> set) {
            this.supplier = supplier;
            this.accumulator = biConsumer;
            this.combiner = binaryOperator;
            this.finisher = function;
            this.characteristics = set;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.BiConsumer != java.util.function.BiConsumer<A, T> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.function.BinaryOperator != java.util.function.BinaryOperator<A> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<A> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Collectors$CollectorImpl != java.util.stream.Collectors$CollectorImpl<T, A, R> */
        public CollectorImpl(Supplier<A> supplier, BiConsumer<A, T> biConsumer, BinaryOperator<A> binaryOperator, Set<Collector.Characteristics> set) {
            this(supplier, biConsumer, binaryOperator, Collectors.castingIdentity(), set);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Collectors$CollectorImpl != java.util.stream.Collectors$CollectorImpl<T, A, R> */
        @Override // j$.util.stream.Collector
        public BiConsumer<A, T> accumulator() {
            return this.accumulator;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Collectors$CollectorImpl != java.util.stream.Collectors$CollectorImpl<T, A, R> */
        @Override // j$.util.stream.Collector
        public Supplier<A> supplier() {
            return this.supplier;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Collectors$CollectorImpl != java.util.stream.Collectors$CollectorImpl<T, A, R> */
        @Override // j$.util.stream.Collector
        public BinaryOperator<A> combiner() {
            return this.combiner;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Collectors$CollectorImpl != java.util.stream.Collectors$CollectorImpl<T, A, R> */
        @Override // j$.util.stream.Collector
        public Function<A, R> finisher() {
            return this.finisher;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Collectors$CollectorImpl != java.util.stream.Collectors$CollectorImpl<T, A, R> */
        @Override // j$.util.stream.Collector
        public Set<Collector.Characteristics> characteristics() {
            return this.characteristics;
        }
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<C extends java.util.Collection<T>> */
    public static <T, C extends Collection<T>> Collector<T, ?, C> toCollection(Supplier<C> supplier) {
        return new CollectorImpl(supplier, Collectors$$ExternalSyntheticLambda19.INSTANCE, Collectors$$ExternalSyntheticLambda33.INSTANCE, CH_ID);
    }

    public static <T> Collector<T, ?, List<T>> toList() {
        return new CollectorImpl(Collectors$$ExternalSyntheticLambda77.INSTANCE, Collectors$$ExternalSyntheticLambda20.INSTANCE, Collectors$$ExternalSyntheticLambda36.INSTANCE, CH_ID);
    }

    public static <T> Collector<T, ?, Set<T>> toSet() {
        return new CollectorImpl(Collectors$$ExternalSyntheticLambda80.INSTANCE, Collectors$$ExternalSyntheticLambda21.INSTANCE, Collectors$$ExternalSyntheticLambda38.INSTANCE, CH_UNORDERED_ID);
    }

    public static Collector<CharSequence, ?, String> joining() {
        return new CollectorImpl(Collectors$$ExternalSyntheticLambda76.INSTANCE, Collectors$$ExternalSyntheticLambda18.INSTANCE, Collectors$$ExternalSyntheticLambda32.INSTANCE, Collectors$$ExternalSyntheticLambda55.INSTANCE, CH_NOID);
    }

    public static Collector<CharSequence, ?, String> joining(CharSequence delimiter) {
        return joining(delimiter, "", "");
    }

    public static Collector<CharSequence, ?, String> joining(final CharSequence delimiter, final CharSequence prefix, final CharSequence suffix) {
        return new CollectorImpl(new Supplier() { // from class: j$.util.stream.Collectors$$ExternalSyntheticLambda66
            @Override // j$.util.function.Supplier
            public final Object get() {
                return Collectors.lambda$joining$6(delimiter, prefix, suffix);
            }
        }, Collectors$$ExternalSyntheticLambda22.INSTANCE, Collectors$$ExternalSyntheticLambda39.INSTANCE, Collectors$$ExternalSyntheticLambda56.INSTANCE, CH_NOID);
    }

    public static /* synthetic */ StringJoiner lambda$joining$6(CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
        return new StringJoiner(delimiter, prefix, suffix);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BinaryOperator != java.util.function.BinaryOperator<V> */
    private static <K, V, M extends Map<K, V>> BinaryOperator<M> mapMerger(final BinaryOperator<V> binaryOperator) {
        return new BinaryOperator() { // from class: j$.util.stream.Collectors$$ExternalSyntheticLambda26
            @Override // j$.util.function.BiFunction
            public /* synthetic */ BiFunction andThen(Function function) {
                return function.getClass();
            }

            @Override // j$.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return Collectors.lambda$mapMerger$7(BinaryOperator.this, (Map) obj, (Map) obj2);
            }
        };
    }

    public static /* synthetic */ Map lambda$mapMerger$7(BinaryOperator mergeFunction, Map m1, Map m2) {
        for (Map.Entry entry : m2.entrySet()) {
            Map.EL.merge(m1, entry.getKey(), entry.getValue(), mergeFunction);
        }
        return m1;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BiConsumer != java.util.function.BiConsumer<A, ? super U> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<? super T, ? extends U> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Collector != java.util.stream.Collector<? super U, A, R> */
    public static <T, U, A, R> Collector<T, ?, R> mapping(final Function<? super T, ? extends U> function, Collector<? super U, A, R> collector) {
        final BiConsumer<A, ? super U> accumulator = collector.accumulator();
        return new CollectorImpl(collector.supplier(), new BiConsumer() { // from class: j$.util.stream.Collectors$$ExternalSyntheticLambda0
            @Override // j$.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                BiConsumer.this.accept(obj, function.apply(obj2));
            }

            @Override // j$.util.function.BiConsumer
            public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
                return biConsumer.getClass();
            }
        }, collector.combiner(), collector.finisher(), collector.characteristics());
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<R, RR> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Collector != java.util.stream.Collector<T, A, R> */
    /* JADX WARN: Multi-variable type inference failed */
    public static <T, A, R, RR> Collector<T, A, RR> collectingAndThen(Collector<T, A, R> collector, Function<R, RR> function) {
        Set<Collector.Characteristics> characteristics = collector.characteristics();
        if (characteristics.contains(Collector.Characteristics.IDENTITY_FINISH)) {
            if (characteristics.size() == 1) {
                characteristics = CH_NOID;
            } else {
                Set<Collector.Characteristics> characteristics2 = EnumSet.copyOf((Collection) characteristics);
                characteristics2.remove(Collector.Characteristics.IDENTITY_FINISH);
                characteristics = Collections.unmodifiableSet(characteristics2);
            }
        }
        return new CollectorImpl(collector.supplier(), collector.accumulator(), collector.combiner(), collector.finisher().andThen(function), characteristics);
    }

    public static <T> Collector<T, ?, Long> counting() {
        return reducing(0L, Collectors$$ExternalSyntheticLambda54.INSTANCE, Collectors$$ExternalSyntheticLambda31.INSTANCE);
    }

    public static /* synthetic */ Long lambda$counting$9(Object e) {
        return 1L;
    }

    public static <T> Collector<T, ?, Optional<T>> minBy(Comparator<? super T> comparator) {
        return reducing(BinaryOperator.CC.minBy(comparator));
    }

    public static <T> Collector<T, ?, Optional<T>> maxBy(Comparator<? super T> comparator) {
        return reducing(BinaryOperator.CC.maxBy(comparator));
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.ToIntFunction != java.util.function.ToIntFunction<? super T> */
    public static <T> Collector<T, ?, Integer> summingInt(final ToIntFunction<? super T> toIntFunction) {
        return new CollectorImpl(Collectors$$ExternalSyntheticLambda74.INSTANCE, new BiConsumer() { // from class: j$.util.stream.Collectors$$ExternalSyntheticLambda13
            @Override // j$.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                Collectors.lambda$summingInt$11(ToIntFunction.this, (int[]) obj, obj2);
            }

            @Override // j$.util.function.BiConsumer
            public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
                return biConsumer.getClass();
            }
        }, Collectors$$ExternalSyntheticLambda43.INSTANCE, Collectors$$ExternalSyntheticLambda60.INSTANCE, CH_NOID);
    }

    public static /* synthetic */ int[] lambda$summingInt$10() {
        return new int[1];
    }

    public static /* synthetic */ void lambda$summingInt$11(ToIntFunction mapper, int[] a, Object t) {
        a[0] = a[0] + mapper.applyAsInt(t);
    }

    public static /* synthetic */ int[] lambda$summingInt$12(int[] a, int[] b) {
        a[0] = a[0] + b[0];
        return a;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.ToLongFunction != java.util.function.ToLongFunction<? super T> */
    public static <T> Collector<T, ?, Long> summingLong(final ToLongFunction<? super T> toLongFunction) {
        return new CollectorImpl(Collectors$$ExternalSyntheticLambda75.INSTANCE, new BiConsumer() { // from class: j$.util.stream.Collectors$$ExternalSyntheticLambda17
            @Override // j$.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                Collectors.lambda$summingLong$15(ToLongFunction.this, (long[]) obj, obj2);
            }

            @Override // j$.util.function.BiConsumer
            public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
                return biConsumer.getClass();
            }
        }, Collectors$$ExternalSyntheticLambda46.INSTANCE, Collectors$$ExternalSyntheticLambda63.INSTANCE, CH_NOID);
    }

    public static /* synthetic */ long[] lambda$summingLong$14() {
        return new long[1];
    }

    public static /* synthetic */ void lambda$summingLong$15(ToLongFunction mapper, long[] a, Object t) {
        a[0] = a[0] + mapper.applyAsLong(t);
    }

    public static /* synthetic */ long[] lambda$summingLong$16(long[] a, long[] b) {
        a[0] = a[0] + b[0];
        return a;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.ToDoubleFunction != java.util.function.ToDoubleFunction<? super T> */
    public static <T> Collector<T, ?, Double> summingDouble(final ToDoubleFunction<? super T> toDoubleFunction) {
        return new CollectorImpl(Collectors$$ExternalSyntheticLambda73.INSTANCE, new BiConsumer() { // from class: j$.util.stream.Collectors$$ExternalSyntheticLambda11
            @Override // j$.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                Collectors.lambda$summingDouble$19(ToDoubleFunction.this, (double[]) obj, obj2);
            }

            @Override // j$.util.function.BiConsumer
            public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
                return biConsumer.getClass();
            }
        }, Collectors$$ExternalSyntheticLambda42.INSTANCE, Collectors$$ExternalSyntheticLambda59.INSTANCE, CH_NOID);
    }

    public static /* synthetic */ double[] lambda$summingDouble$18() {
        return new double[3];
    }

    public static /* synthetic */ void lambda$summingDouble$19(ToDoubleFunction mapper, double[] a, Object t) {
        sumWithCompensation(a, mapper.applyAsDouble(t));
        a[2] = a[2] + mapper.applyAsDouble(t);
    }

    public static /* synthetic */ double[] lambda$summingDouble$20(double[] a, double[] b) {
        sumWithCompensation(a, b[0]);
        a[2] = a[2] + b[2];
        return sumWithCompensation(a, b[1]);
    }

    public static double[] sumWithCompensation(double[] intermediateSum, double value) {
        double tmp = value - intermediateSum[1];
        double sum = intermediateSum[0];
        double velvel = sum + tmp;
        intermediateSum[1] = (velvel - sum) - tmp;
        intermediateSum[0] = velvel;
        return intermediateSum;
    }

    public static double computeFinalSum(double[] summands) {
        double tmp = summands[0] + summands[1];
        double simpleSum = summands[summands.length - 1];
        if (Double.isNaN(tmp) && Double.isInfinite(simpleSum)) {
            return simpleSum;
        }
        return tmp;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.ToIntFunction != java.util.function.ToIntFunction<? super T> */
    public static <T> Collector<T, ?, Double> averagingInt(final ToIntFunction<? super T> toIntFunction) {
        return new CollectorImpl(Collectors$$ExternalSyntheticLambda71.INSTANCE, new BiConsumer() { // from class: j$.util.stream.Collectors$$ExternalSyntheticLambda14
            @Override // j$.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                Collectors.lambda$averagingInt$23(ToIntFunction.this, (long[]) obj, obj2);
            }

            @Override // j$.util.function.BiConsumer
            public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
                return biConsumer.getClass();
            }
        }, Collectors$$ExternalSyntheticLambda44.INSTANCE, Collectors$$ExternalSyntheticLambda61.INSTANCE, CH_NOID);
    }

    public static /* synthetic */ long[] lambda$averagingInt$22() {
        return new long[2];
    }

    public static /* synthetic */ void lambda$averagingInt$23(ToIntFunction mapper, long[] a, Object t) {
        a[0] = a[0] + mapper.applyAsInt(t);
        a[1] = a[1] + 1;
    }

    public static /* synthetic */ long[] lambda$averagingInt$24(long[] a, long[] b) {
        a[0] = a[0] + b[0];
        a[1] = a[1] + b[1];
        return a;
    }

    public static /* synthetic */ Double lambda$averagingInt$25(long[] a) {
        double d;
        if (a[1] == 0) {
            d = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        } else {
            double d2 = a[0];
            double d3 = a[1];
            Double.isNaN(d2);
            Double.isNaN(d3);
            d = d2 / d3;
        }
        return Double.valueOf(d);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.ToLongFunction != java.util.function.ToLongFunction<? super T> */
    public static <T> Collector<T, ?, Double> averagingLong(final ToLongFunction<? super T> toLongFunction) {
        return new CollectorImpl(Collectors$$ExternalSyntheticLambda72.INSTANCE, new BiConsumer() { // from class: j$.util.stream.Collectors$$ExternalSyntheticLambda16
            @Override // j$.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                Collectors.lambda$averagingLong$27(ToLongFunction.this, (long[]) obj, obj2);
            }

            @Override // j$.util.function.BiConsumer
            public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
                return biConsumer.getClass();
            }
        }, Collectors$$ExternalSyntheticLambda45.INSTANCE, Collectors$$ExternalSyntheticLambda62.INSTANCE, CH_NOID);
    }

    public static /* synthetic */ long[] lambda$averagingLong$26() {
        return new long[2];
    }

    public static /* synthetic */ void lambda$averagingLong$27(ToLongFunction mapper, long[] a, Object t) {
        a[0] = a[0] + mapper.applyAsLong(t);
        a[1] = a[1] + 1;
    }

    public static /* synthetic */ long[] lambda$averagingLong$28(long[] a, long[] b) {
        a[0] = a[0] + b[0];
        a[1] = a[1] + b[1];
        return a;
    }

    public static /* synthetic */ Double lambda$averagingLong$29(long[] a) {
        double d;
        if (a[1] == 0) {
            d = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        } else {
            double d2 = a[0];
            double d3 = a[1];
            Double.isNaN(d2);
            Double.isNaN(d3);
            d = d2 / d3;
        }
        return Double.valueOf(d);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.ToDoubleFunction != java.util.function.ToDoubleFunction<? super T> */
    public static <T> Collector<T, ?, Double> averagingDouble(final ToDoubleFunction<? super T> toDoubleFunction) {
        return new CollectorImpl(Collectors$$ExternalSyntheticLambda70.INSTANCE, new BiConsumer() { // from class: j$.util.stream.Collectors$$ExternalSyntheticLambda10
            @Override // j$.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                Collectors.lambda$averagingDouble$31(ToDoubleFunction.this, (double[]) obj, obj2);
            }

            @Override // j$.util.function.BiConsumer
            public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
                return biConsumer.getClass();
            }
        }, Collectors$$ExternalSyntheticLambda41.INSTANCE, Collectors$$ExternalSyntheticLambda58.INSTANCE, CH_NOID);
    }

    public static /* synthetic */ double[] lambda$averagingDouble$30() {
        return new double[4];
    }

    public static /* synthetic */ void lambda$averagingDouble$31(ToDoubleFunction mapper, double[] a, Object t) {
        sumWithCompensation(a, mapper.applyAsDouble(t));
        a[2] = a[2] + 1.0d;
        a[3] = a[3] + mapper.applyAsDouble(t);
    }

    public static /* synthetic */ double[] lambda$averagingDouble$32(double[] a, double[] b) {
        sumWithCompensation(a, b[0]);
        sumWithCompensation(a, b[1]);
        a[2] = a[2] + b[2];
        a[3] = a[3] + b[3];
        return a;
    }

    public static /* synthetic */ Double lambda$averagingDouble$33(double[] a) {
        double d = a[2];
        double d2 = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        if (d != FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
            d2 = computeFinalSum(a) / a[2];
        }
        return Double.valueOf(d2);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BinaryOperator != java.util.function.BinaryOperator<T> */
    public static <T> Collector<T, ?, T> reducing(T identity, final BinaryOperator<T> binaryOperator) {
        return new CollectorImpl(boxSupplier(identity), new BiConsumer() { // from class: j$.util.stream.Collectors$$ExternalSyntheticLambda2
            @Override // j$.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                Collectors.lambda$reducing$34(BinaryOperator.this, (Object[]) obj, obj2);
            }

            @Override // j$.util.function.BiConsumer
            public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
                return biConsumer.getClass();
            }
        }, new BinaryOperator() { // from class: j$.util.stream.Collectors$$ExternalSyntheticLambda28
            @Override // j$.util.function.BiFunction
            public /* synthetic */ BiFunction andThen(Function function) {
                return function.getClass();
            }

            @Override // j$.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return Collectors.lambda$reducing$35(BinaryOperator.this, (Object[]) obj, (Object[]) obj2);
            }
        }, Collectors$$ExternalSyntheticLambda64.INSTANCE, CH_NOID);
    }

    public static /* synthetic */ void lambda$reducing$34(BinaryOperator op, Object[] a, Object t) {
        a[0] = op.apply(a[0], t);
    }

    public static /* synthetic */ Object[] lambda$reducing$35(BinaryOperator op, Object[] a, Object[] b) {
        a[0] = op.apply(a[0], b[0]);
        return a;
    }

    public static /* synthetic */ Object lambda$reducing$36(Object[] a) {
        return a[0];
    }

    private static <T> Supplier<T[]> boxSupplier(final T identity) {
        return new Supplier() { // from class: j$.util.stream.Collectors$$ExternalSyntheticLambda67
            @Override // j$.util.function.Supplier
            public final Object get() {
                return Collectors.lambda$boxSupplier$37(identity);
            }
        };
    }

    public static /* synthetic */ Object[] lambda$boxSupplier$37(Object identity) {
        return new Object[]{identity};
    }

    /* renamed from: j$.util.stream.Collectors$1OptionalBox */
    /* loaded from: classes2.dex */
    public class C1OptionalBox implements Consumer<T> {
        final /* synthetic */ BinaryOperator val$op;
        T value = null;
        boolean present = false;

        @Override // j$.util.function.Consumer
        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return consumer.getClass();
        }

        C1OptionalBox(BinaryOperator binaryOperator) {
            this.val$op = binaryOperator;
        }

        @Override // j$.util.function.Consumer
        public void accept(T t) {
            if (this.present) {
                this.value = this.val$op.apply(this.value, t);
                return;
            }
            this.value = t;
            this.present = true;
        }
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BinaryOperator != java.util.function.BinaryOperator<T> */
    public static <T> Collector<T, ?, Optional<T>> reducing(final BinaryOperator<T> binaryOperator) {
        return new CollectorImpl(new Supplier() { // from class: j$.util.stream.Collectors$$ExternalSyntheticLambda68
            @Override // j$.util.function.Supplier
            public final Object get() {
                return Collectors.lambda$reducing$38(BinaryOperator.this);
            }
        }, Collectors$$ExternalSyntheticLambda23.INSTANCE, Collectors$$ExternalSyntheticLambda40.INSTANCE, Collectors$$ExternalSyntheticLambda57.INSTANCE, CH_NOID);
    }

    public static /* synthetic */ C1OptionalBox lambda$reducing$38(BinaryOperator op) {
        return new C1OptionalBox(op);
    }

    public static /* synthetic */ C1OptionalBox lambda$reducing$39(C1OptionalBox a, C1OptionalBox b) {
        if (b.present) {
            a.accept(b.value);
        }
        return a;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BinaryOperator != java.util.function.BinaryOperator<U> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<? super T, ? extends U> */
    public static <T, U> Collector<T, ?, U> reducing(U identity, final Function<? super T, ? extends U> function, final BinaryOperator<U> binaryOperator) {
        return new CollectorImpl(boxSupplier(identity), new BiConsumer() { // from class: j$.util.stream.Collectors$$ExternalSyntheticLambda3
            @Override // j$.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                Collectors.lambda$reducing$41(BinaryOperator.this, function, (Object[]) obj, obj2);
            }

            @Override // j$.util.function.BiConsumer
            public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
                return biConsumer.getClass();
            }
        }, new BinaryOperator() { // from class: j$.util.stream.Collectors$$ExternalSyntheticLambda29
            @Override // j$.util.function.BiFunction
            public /* synthetic */ BiFunction andThen(Function function2) {
                return function2.getClass();
            }

            @Override // j$.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return Collectors.lambda$reducing$42(BinaryOperator.this, (Object[]) obj, (Object[]) obj2);
            }
        }, Collectors$$ExternalSyntheticLambda65.INSTANCE, CH_NOID);
    }

    public static /* synthetic */ void lambda$reducing$41(BinaryOperator op, Function mapper, Object[] a, Object t) {
        a[0] = op.apply(a[0], mapper.apply(t));
    }

    public static /* synthetic */ Object[] lambda$reducing$42(BinaryOperator op, Object[] a, Object[] b) {
        a[0] = op.apply(a[0], b[0]);
        return a;
    }

    public static /* synthetic */ Object lambda$reducing$43(Object[] a) {
        return a[0];
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<? super T, ? extends K> */
    public static <T, K> Collector<T, ?, java.util.Map<K, List<T>>> groupingBy(Function<? super T, ? extends K> function) {
        return groupingBy(function, toList());
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<? super T, ? extends K> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Collector != java.util.stream.Collector<? super T, A, D> */
    public static <T, K, A, D> Collector<T, ?, java.util.Map<K, D>> groupingBy(Function<? super T, ? extends K> function, Collector<? super T, A, D> collector) {
        return groupingBy(function, Collectors$$ExternalSyntheticLambda79.INSTANCE, collector);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BiConsumer != java.util.function.BiConsumer<A, ? super T> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BiConsumer != java.util.function.BiConsumer<java.util.Map<K, A>, T> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BinaryOperator != java.util.function.BinaryOperator<java.util.Map<K, A>> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<? super T, ? extends K> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<A, A> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<java.util.Map<K, A>, M extends java.util.Map<K, D>> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<A> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<M extends java.util.Map<K, D>> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<java.util.Map<K, A>> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Collector != java.util.stream.Collector<? super T, A, D> */
    public static <T, K, D, A, M extends java.util.Map<K, D>> Collector<T, ?, M> groupingBy(final Function<? super T, ? extends K> function, Supplier<M> supplier, Collector<? super T, A, D> collector) {
        final Supplier<A> supplier2 = collector.supplier();
        final BiConsumer<A, ? super T> accumulator = collector.accumulator();
        BiConsumer biConsumer = new BiConsumer() { // from class: j$.util.stream.Collectors$$ExternalSyntheticLambda6
            @Override // j$.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                accumulator.accept(Map.EL.computeIfAbsent((java.util.Map) obj, Objects.requireNonNull(Function.this.apply(obj2), "element cannot be mapped to a null key"), new Function() { // from class: j$.util.stream.Collectors$$ExternalSyntheticLambda49
                    @Override // j$.util.function.Function
                    public /* synthetic */ Function andThen(Function function2) {
                        return function2.getClass();
                    }

                    @Override // j$.util.function.Function
                    public final Object apply(Object obj3) {
                        Object obj4;
                        obj4 = Supplier.this.get();
                        return obj4;
                    }

                    @Override // j$.util.function.Function
                    public /* synthetic */ Function compose(Function function2) {
                        return function2.getClass();
                    }
                }), obj2);
            }

            @Override // j$.util.function.BiConsumer
            public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer2) {
                return biConsumer2.getClass();
            }
        };
        BinaryOperator mapMerger = mapMerger(collector.combiner());
        if (collector.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH)) {
            return new CollectorImpl(supplier, biConsumer, mapMerger, CH_ID);
        }
        final Function<A, D> finisher = collector.finisher();
        return new CollectorImpl(supplier, biConsumer, mapMerger, new Function() { // from class: j$.util.stream.Collectors$$ExternalSyntheticLambda47
            @Override // j$.util.function.Function
            public /* synthetic */ Function andThen(Function function2) {
                return function2.getClass();
            }

            @Override // j$.util.function.Function
            public final Object apply(Object obj) {
                return Map.EL.replaceAll((java.util.Map) obj, new BiFunction() { // from class: j$.util.stream.Collectors$$ExternalSyntheticLambda24
                    @Override // j$.util.function.BiFunction
                    public /* synthetic */ BiFunction andThen(Function function2) {
                        return function2.getClass();
                    }

                    @Override // j$.util.function.BiFunction
                    public final Object apply(Object obj2, Object obj3) {
                        Object apply;
                        apply = Function.this.apply(obj3);
                        return apply;
                    }
                });
            }

            @Override // j$.util.function.Function
            public /* synthetic */ Function compose(Function function2) {
                return function2.getClass();
            }
        }, CH_NOID);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<? super T, ? extends K> */
    public static <T, K> Collector<T, ?, ConcurrentMap<K, List<T>>> groupingByConcurrent(Function<? super T, ? extends K> function) {
        return groupingByConcurrent(function, Collectors$$ExternalSyntheticLambda83.INSTANCE, toList());
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<? super T, ? extends K> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Collector != java.util.stream.Collector<? super T, A, D> */
    public static <T, K, A, D> Collector<T, ?, ConcurrentMap<K, D>> groupingByConcurrent(Function<? super T, ? extends K> function, Collector<? super T, A, D> collector) {
        return groupingByConcurrent(function, Collectors$$ExternalSyntheticLambda83.INSTANCE, collector);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BiConsumer != java.util.function.BiConsumer<A, ? super T> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BiConsumer != java.util.function.BiConsumer<java.util.concurrent.ConcurrentMap<K, A>, T> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BinaryOperator != java.util.function.BinaryOperator<java.util.concurrent.ConcurrentMap<K, A>> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<? super T, ? extends K> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<A, A> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<java.util.concurrent.ConcurrentMap<K, A>, M extends java.util.concurrent.ConcurrentMap<K, D>> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<A> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<M extends java.util.concurrent.ConcurrentMap<K, D>> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<java.util.concurrent.ConcurrentMap<K, A>> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Collector != java.util.stream.Collector<? super T, A, D> */
    public static <T, K, A, D, M extends ConcurrentMap<K, D>> Collector<T, ?, M> groupingByConcurrent(final Function<? super T, ? extends K> function, Supplier<M> supplier, Collector<? super T, A, D> collector) {
        BiConsumer biConsumer;
        final Supplier<A> supplier2 = collector.supplier();
        final BiConsumer<A, ? super T> accumulator = collector.accumulator();
        BinaryOperator mapMerger = mapMerger(collector.combiner());
        if (collector.characteristics().contains(Collector.Characteristics.CONCURRENT)) {
            biConsumer = new BiConsumer() { // from class: j$.util.stream.Collectors$$ExternalSyntheticLambda7
                @Override // j$.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    accumulator.accept(ConcurrentMap.EL.computeIfAbsent((java.util.concurrent.ConcurrentMap) obj, Objects.requireNonNull(Function.this.apply(obj2), "element cannot be mapped to a null key"), new Function() { // from class: j$.util.stream.Collectors$$ExternalSyntheticLambda50
                        @Override // j$.util.function.Function
                        public /* synthetic */ Function andThen(Function function2) {
                            return function2.getClass();
                        }

                        @Override // j$.util.function.Function
                        public final Object apply(Object obj3) {
                            Object obj4;
                            obj4 = Supplier.this.get();
                            return obj4;
                        }

                        @Override // j$.util.function.Function
                        public /* synthetic */ Function compose(Function function2) {
                            return function2.getClass();
                        }
                    }), obj2);
                }

                @Override // j$.util.function.BiConsumer
                public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer2) {
                    return biConsumer2.getClass();
                }
            };
        } else {
            biConsumer = new BiConsumer() { // from class: j$.util.stream.Collectors$$ExternalSyntheticLambda8
                @Override // j$.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    Collectors.lambda$groupingByConcurrent$51(Function.this, supplier2, accumulator, (java.util.concurrent.ConcurrentMap) obj, obj2);
                }

                @Override // j$.util.function.BiConsumer
                public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer2) {
                    return biConsumer2.getClass();
                }
            };
        }
        if (collector.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH)) {
            return new CollectorImpl(supplier, biConsumer, mapMerger, CH_CONCURRENT_ID);
        }
        final Function<A, D> finisher = collector.finisher();
        return new CollectorImpl(supplier, biConsumer, mapMerger, new Function() { // from class: j$.util.stream.Collectors$$ExternalSyntheticLambda48
            @Override // j$.util.function.Function
            public /* synthetic */ Function andThen(Function function2) {
                return function2.getClass();
            }

            @Override // j$.util.function.Function
            public final Object apply(Object obj) {
                return ConcurrentMap.EL.replaceAll((java.util.concurrent.ConcurrentMap) obj, new BiFunction() { // from class: j$.util.stream.Collectors$$ExternalSyntheticLambda25
                    @Override // j$.util.function.BiFunction
                    public /* synthetic */ BiFunction andThen(Function function2) {
                        return function2.getClass();
                    }

                    @Override // j$.util.function.BiFunction
                    public final Object apply(Object obj2, Object obj3) {
                        Object apply;
                        apply = Function.this.apply(obj3);
                        return apply;
                    }
                });
            }

            @Override // j$.util.function.Function
            public /* synthetic */ Function compose(Function function2) {
                return function2.getClass();
            }
        }, CH_CONCURRENT_NOID);
    }

    public static /* synthetic */ void lambda$groupingByConcurrent$51(Function classifier, final Supplier downstreamSupplier, BiConsumer downstreamAccumulator, java.util.concurrent.ConcurrentMap m, Object t) {
        Object computeIfAbsent = ConcurrentMap.EL.computeIfAbsent(m, Objects.requireNonNull(classifier.apply(t), "element cannot be mapped to a null key"), new Function() { // from class: j$.util.stream.Collectors$$ExternalSyntheticLambda51
            @Override // j$.util.function.Function
            public /* synthetic */ Function andThen(Function function) {
                return function.getClass();
            }

            @Override // j$.util.function.Function
            public final Object apply(Object obj) {
                Object obj2;
                obj2 = Supplier.this.get();
                return obj2;
            }

            @Override // j$.util.function.Function
            public /* synthetic */ Function compose(Function function) {
                return function.getClass();
            }
        });
        synchronized (computeIfAbsent) {
            downstreamAccumulator.accept(computeIfAbsent, t);
        }
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Predicate != java.util.function.Predicate<? super T> */
    public static <T> Collector<T, ?, java.util.Map<Boolean, List<T>>> partitioningBy(Predicate<? super T> predicate) {
        return partitioningBy(predicate, toList());
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BiConsumer != java.util.function.BiConsumer<A, ? super T> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BiConsumer != java.util.function.BiConsumer<java.util.stream.Collectors$Partition<A>, T> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BinaryOperator != java.util.function.BinaryOperator<A> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BinaryOperator != java.util.function.BinaryOperator<java.util.stream.Collectors$Partition<A>> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<java.util.stream.Collectors$Partition<A>, java.util.Map<java.lang.Boolean, D>> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Predicate != java.util.function.Predicate<? super T> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<java.util.stream.Collectors$Partition<A>> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Collector != java.util.stream.Collector<? super T, A, D> */
    public static <T, D, A> Collector<T, ?, java.util.Map<Boolean, D>> partitioningBy(final Predicate<? super T> predicate, final Collector<? super T, A, D> collector) {
        final BiConsumer<A, ? super T> accumulator = collector.accumulator();
        BiConsumer biConsumer = new BiConsumer() { // from class: j$.util.stream.Collectors$$ExternalSyntheticLambda1
            @Override // j$.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                BiConsumer biConsumer2 = BiConsumer.this;
                Predicate predicate2 = predicate;
                biConsumer2.accept(predicate.test(t) ? r3.forTrue : ((Collectors.Partition) obj).forFalse, obj2);
            }

            @Override // j$.util.function.BiConsumer
            public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer2) {
                return biConsumer2.getClass();
            }
        };
        final BinaryOperator<A> combiner = collector.combiner();
        BinaryOperator binaryOperator = new BinaryOperator() { // from class: j$.util.stream.Collectors$$ExternalSyntheticLambda27
            @Override // j$.util.function.BiFunction
            public /* synthetic */ BiFunction andThen(Function function) {
                return function.getClass();
            }

            @Override // j$.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return Collectors.lambda$partitioningBy$55(BinaryOperator.this, (Collectors.Partition) obj, (Collectors.Partition) obj2);
            }
        };
        Supplier supplier = new Supplier() { // from class: j$.util.stream.Collectors$$ExternalSyntheticLambda69
            @Override // j$.util.function.Supplier
            public final Object get() {
                return Collectors.lambda$partitioningBy$56(Collector.this);
            }
        };
        if (collector.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH)) {
            return new CollectorImpl(supplier, biConsumer, binaryOperator, CH_ID);
        }
        return new CollectorImpl(supplier, biConsumer, binaryOperator, new Function() { // from class: j$.util.stream.Collectors$$ExternalSyntheticLambda52
            @Override // j$.util.function.Function
            public /* synthetic */ Function andThen(Function function) {
                return function.getClass();
            }

            @Override // j$.util.function.Function
            public final Object apply(Object obj) {
                return Collectors.lambda$partitioningBy$57(Collector.this, (Collectors.Partition) obj);
            }

            @Override // j$.util.function.Function
            public /* synthetic */ Function compose(Function function) {
                return function.getClass();
            }
        }, CH_NOID);
    }

    public static /* synthetic */ Partition lambda$partitioningBy$55(BinaryOperator op, Partition left, Partition right) {
        return new Partition(op.apply(left.forTrue, right.forTrue), op.apply(left.forFalse, right.forFalse));
    }

    public static /* synthetic */ Partition lambda$partitioningBy$56(Collector downstream) {
        return new Partition(downstream.supplier().get(), downstream.supplier().get());
    }

    public static /* synthetic */ java.util.Map lambda$partitioningBy$57(Collector downstream, Partition par) {
        return new Partition(downstream.finisher().apply(par.forTrue), downstream.finisher().apply(par.forFalse));
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<? super T, ? extends K> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<? super T, ? extends U> */
    public static <T, K, U> Collector<T, ?, java.util.Map<K, U>> toMap(Function<? super T, ? extends K> function, Function<? super T, ? extends U> function2) {
        return toMap(function, function2, throwingMerger(), Collectors$$ExternalSyntheticLambda79.INSTANCE);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BinaryOperator != java.util.function.BinaryOperator<U> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<? super T, ? extends K> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<? super T, ? extends U> */
    public static <T, K, U> Collector<T, ?, java.util.Map<K, U>> toMap(Function<? super T, ? extends K> function, Function<? super T, ? extends U> function2, BinaryOperator<U> binaryOperator) {
        return toMap(function, function2, binaryOperator, Collectors$$ExternalSyntheticLambda79.INSTANCE);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BiConsumer != java.util.function.BiConsumer<M extends java.util.Map<K, U>, T> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BinaryOperator != java.util.function.BinaryOperator<U> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<? super T, ? extends K> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<? super T, ? extends U> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<M extends java.util.Map<K, U>> */
    public static <T, K, U, M extends java.util.Map<K, U>> Collector<T, ?, M> toMap(final Function<? super T, ? extends K> function, final Function<? super T, ? extends U> function2, final BinaryOperator<U> binaryOperator, Supplier<M> supplier) {
        return new CollectorImpl(supplier, new BiConsumer() { // from class: j$.util.stream.Collectors$$ExternalSyntheticLambda4
            @Override // j$.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                java.util.Map map = (java.util.Map) obj;
                Map.EL.merge(map, Function.this.apply(obj2), function2.apply(obj2), binaryOperator);
            }

            @Override // j$.util.function.BiConsumer
            public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
                return biConsumer.getClass();
            }
        }, mapMerger(binaryOperator), CH_ID);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<? super T, ? extends K> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<? super T, ? extends U> */
    public static <T, K, U> Collector<T, ?, java.util.concurrent.ConcurrentMap<K, U>> toConcurrentMap(Function<? super T, ? extends K> function, Function<? super T, ? extends U> function2) {
        return toConcurrentMap(function, function2, throwingMerger(), Collectors$$ExternalSyntheticLambda83.INSTANCE);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BinaryOperator != java.util.function.BinaryOperator<U> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<? super T, ? extends K> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<? super T, ? extends U> */
    public static <T, K, U> Collector<T, ?, java.util.concurrent.ConcurrentMap<K, U>> toConcurrentMap(Function<? super T, ? extends K> function, Function<? super T, ? extends U> function2, BinaryOperator<U> binaryOperator) {
        return toConcurrentMap(function, function2, binaryOperator, Collectors$$ExternalSyntheticLambda83.INSTANCE);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BiConsumer != java.util.function.BiConsumer<M extends java.util.concurrent.ConcurrentMap<K, U>, T> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BinaryOperator != java.util.function.BinaryOperator<U> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<? super T, ? extends K> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<? super T, ? extends U> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<M extends java.util.concurrent.ConcurrentMap<K, U>> */
    public static <T, K, U, M extends java.util.concurrent.ConcurrentMap<K, U>> Collector<T, ?, M> toConcurrentMap(final Function<? super T, ? extends K> function, final Function<? super T, ? extends U> function2, final BinaryOperator<U> binaryOperator, Supplier<M> supplier) {
        return new CollectorImpl(supplier, new BiConsumer() { // from class: j$.util.stream.Collectors$$ExternalSyntheticLambda5
            @Override // j$.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                java.util.concurrent.ConcurrentMap concurrentMap = (java.util.concurrent.ConcurrentMap) obj;
                ConcurrentMap.EL.merge(concurrentMap, Function.this.apply(obj2), function2.apply(obj2), binaryOperator);
            }

            @Override // j$.util.function.BiConsumer
            public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
                return biConsumer.getClass();
            }
        }, mapMerger(binaryOperator), CH_CONCURRENT_ID);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.ToIntFunction != java.util.function.ToIntFunction<? super T> */
    public static <T> Collector<T, ?, IntSummaryStatistics> summarizingInt(final ToIntFunction<? super T> toIntFunction) {
        return new CollectorImpl(Collectors$$ExternalSyntheticLambda81.INSTANCE, new BiConsumer() { // from class: j$.util.stream.Collectors$$ExternalSyntheticLambda12
            @Override // j$.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ((IntSummaryStatistics) obj).accept(ToIntFunction.this.applyAsInt(obj2));
            }

            @Override // j$.util.function.BiConsumer
            public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
                return biConsumer.getClass();
            }
        }, Collectors$$ExternalSyntheticLambda35.INSTANCE, CH_ID);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.ToLongFunction != java.util.function.ToLongFunction<? super T> */
    public static <T> Collector<T, ?, LongSummaryStatistics> summarizingLong(final ToLongFunction<? super T> toLongFunction) {
        return new CollectorImpl(Collectors$$ExternalSyntheticLambda82.INSTANCE, new BiConsumer() { // from class: j$.util.stream.Collectors$$ExternalSyntheticLambda15
            @Override // j$.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ((LongSummaryStatistics) obj).accept(ToLongFunction.this.applyAsLong(obj2));
            }

            @Override // j$.util.function.BiConsumer
            public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
                return biConsumer.getClass();
            }
        }, Collectors$$ExternalSyntheticLambda37.INSTANCE, CH_ID);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.ToDoubleFunction != java.util.function.ToDoubleFunction<? super T> */
    public static <T> Collector<T, ?, DoubleSummaryStatistics> summarizingDouble(final ToDoubleFunction<? super T> toDoubleFunction) {
        return new CollectorImpl(Collectors$$ExternalSyntheticLambda78.INSTANCE, new BiConsumer() { // from class: j$.util.stream.Collectors$$ExternalSyntheticLambda9
            @Override // j$.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ((DoubleSummaryStatistics) obj).accept(ToDoubleFunction.this.applyAsDouble(obj2));
            }

            @Override // j$.util.function.BiConsumer
            public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
                return biConsumer.getClass();
            }
        }, Collectors$$ExternalSyntheticLambda34.INSTANCE, CH_ID);
    }

    /* loaded from: classes2.dex */
    public static final class Partition<T> extends AbstractMap<Boolean, T> implements java.util.Map<Boolean, T> {
        final T forFalse;
        final T forTrue;

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Collectors$Partition != java.util.stream.Collectors$Partition<T> */
        Partition(T forTrue, T forFalse) {
            this.forTrue = forTrue;
            this.forFalse = forFalse;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Collectors$Partition != java.util.stream.Collectors$Partition<T> */
        @Override // java.util.AbstractMap, java.util.Map
        public Set<Map.Entry<Boolean, T>> entrySet() {
            return new AbstractSet<Map.Entry<Boolean, T>>() { // from class: j$.util.stream.Collectors.Partition.1
                @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
                public Iterator<Map.Entry<Boolean, T>> iterator() {
                    Map.Entry<Boolean, T> falseEntry = new AbstractMap.SimpleImmutableEntry<>(false, Partition.this.forFalse);
                    Map.Entry<Boolean, T> trueEntry = new AbstractMap.SimpleImmutableEntry<>(true, Partition.this.forTrue);
                    return Arrays.asList(falseEntry, trueEntry).iterator();
                }

                @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
                public int size() {
                    return 2;
                }
            };
        }
    }
}
