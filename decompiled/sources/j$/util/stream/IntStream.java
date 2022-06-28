package j$.util.stream;

import j$.util.DesugarArrays;
import j$.util.IntSummaryStatistics;
import j$.util.Iterator;
import j$.util.OptionalDouble;
import j$.util.OptionalInt;
import j$.util.PrimitiveIterator;
import j$.util.Spliterator;
import j$.util.Spliterators;
import j$.util.function.BiConsumer;
import j$.util.function.Consumer;
import j$.util.function.IntBinaryOperator;
import j$.util.function.IntConsumer;
import j$.util.function.IntFunction;
import j$.util.function.IntPredicate;
import j$.util.function.IntSupplier;
import j$.util.function.IntToDoubleFunction;
import j$.util.function.IntToLongFunction;
import j$.util.function.IntUnaryOperator;
import j$.util.function.ObjIntConsumer;
import j$.util.function.Supplier;
import j$.util.stream.StreamSpliterators;
import j$.util.stream.Streams;
import java.util.Iterator;
/* loaded from: classes2.dex */
public interface IntStream extends BaseStream<Integer, IntStream> {
    boolean allMatch(IntPredicate intPredicate);

    boolean anyMatch(IntPredicate intPredicate);

    DoubleStream asDoubleStream();

    LongStream asLongStream();

    OptionalDouble average();

    Stream<Integer> boxed();

    <R> R collect(Supplier<R> supplier, ObjIntConsumer<R> objIntConsumer, BiConsumer<R, R> biConsumer);

    long count();

    IntStream distinct();

    IntStream filter(IntPredicate intPredicate);

    OptionalInt findAny();

    OptionalInt findFirst();

    IntStream flatMap(IntFunction<? extends IntStream> intFunction);

    void forEach(IntConsumer intConsumer);

    void forEachOrdered(IntConsumer intConsumer);

    @Override // j$.util.stream.BaseStream, j$.util.stream.DoubleStream
    Iterator<Integer> iterator();

    IntStream limit(long j);

    IntStream map(IntUnaryOperator intUnaryOperator);

    DoubleStream mapToDouble(IntToDoubleFunction intToDoubleFunction);

    LongStream mapToLong(IntToLongFunction intToLongFunction);

    <U> Stream<U> mapToObj(IntFunction<? extends U> intFunction);

    OptionalInt max();

    OptionalInt min();

    boolean noneMatch(IntPredicate intPredicate);

    @Override // j$.util.stream.BaseStream
    IntStream parallel();

    IntStream peek(IntConsumer intConsumer);

    int reduce(int i, IntBinaryOperator intBinaryOperator);

    OptionalInt reduce(IntBinaryOperator intBinaryOperator);

    @Override // j$.util.stream.BaseStream
    IntStream sequential();

    IntStream skip(long j);

    IntStream sorted();

    @Override // j$.util.stream.BaseStream
    Spliterator<Integer> spliterator();

    int sum();

    IntSummaryStatistics summaryStatistics();

    int[] toArray();

    /* renamed from: j$.util.stream.IntStream$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static Builder builder() {
            return new Streams.IntStreamBuilderImpl();
        }

        public static IntStream empty() {
            return StreamSupport.intStream(Spliterators.emptyIntSpliterator(), false);
        }

        public static IntStream of(int t) {
            return StreamSupport.intStream(new Streams.IntStreamBuilderImpl(t), false);
        }

        public static IntStream of(int... values) {
            return DesugarArrays.stream(values);
        }

        public static IntStream iterate(int seed, IntUnaryOperator f) {
            f.getClass();
            PrimitiveIterator.OfInt iterator = new AnonymousClass1(seed, f);
            return StreamSupport.intStream(Spliterators.spliteratorUnknownSize(iterator, 1296), false);
        }

        public static IntStream generate(IntSupplier s) {
            s.getClass();
            return StreamSupport.intStream(new StreamSpliterators.InfiniteSupplyingSpliterator.OfInt(Long.MAX_VALUE, s), false);
        }

        public static IntStream range(int startInclusive, int endExclusive) {
            if (startInclusive >= endExclusive) {
                return empty();
            }
            return StreamSupport.intStream(new Streams.RangeIntSpliterator(startInclusive, endExclusive, false), false);
        }

        public static IntStream rangeClosed(int startInclusive, int endInclusive) {
            if (startInclusive > endInclusive) {
                return empty();
            }
            return StreamSupport.intStream(new Streams.RangeIntSpliterator(startInclusive, endInclusive, true), false);
        }

        /* JADX WARN: Type inference failed for: r1v0, types: [j$.util.Spliterator$OfInt] */
        /* JADX WARN: Type inference failed for: r2v0, types: [j$.util.Spliterator$OfInt] */
        public static IntStream concat(IntStream a, IntStream b) {
            a.getClass();
            b.getClass();
            Spliterator.OfInt split = new Streams.ConcatSpliterator.OfInt(a.spliterator(), b.spliterator());
            IntStream stream = StreamSupport.intStream(split, a.isParallel() || b.isParallel());
            return stream.onClose(Streams.composedClose(a, b));
        }
    }

    /* renamed from: j$.util.stream.IntStream$1 */
    /* loaded from: classes2.dex */
    class AnonymousClass1 implements PrimitiveIterator.OfInt, j$.util.Iterator {
        int t;
        final /* synthetic */ IntUnaryOperator val$f;
        final /* synthetic */ int val$seed;

        @Override // j$.util.PrimitiveIterator.OfInt, j$.util.Iterator
        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            PrimitiveIterator.OfInt.CC.$default$forEachRemaining((PrimitiveIterator.OfInt) this, consumer);
        }

        @Override // j$.util.PrimitiveIterator.OfInt
        public /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
            PrimitiveIterator.OfInt.CC.$default$forEachRemaining((PrimitiveIterator.OfInt) this, intConsumer);
        }

        @Override // j$.util.PrimitiveIterator
        public /* bridge */ /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
            forEachRemaining((IntConsumer) intConsumer);
        }

        @Override // java.util.Iterator, j$.util.Iterator
        public /* synthetic */ void remove() {
            Iterator.CC.$default$remove(this);
        }

        AnonymousClass1(int i, IntUnaryOperator intUnaryOperator) {
            this.val$seed = i;
            this.val$f = intUnaryOperator;
            this.t = i;
        }

        @Override // java.util.Iterator, j$.util.Iterator
        public boolean hasNext() {
            return true;
        }

        @Override // j$.util.PrimitiveIterator.OfInt
        public int nextInt() {
            int v = this.t;
            this.t = this.val$f.applyAsInt(this.t);
            return v;
        }
    }

    /* loaded from: classes2.dex */
    public interface Builder extends IntConsumer {
        @Override // j$.util.function.IntConsumer
        void accept(int i);

        Builder add(int i);

        IntStream build();

        /* renamed from: j$.util.stream.IntStream$Builder$-CC */
        /* loaded from: classes2.dex */
        public final /* synthetic */ class CC {
            public static Builder $default$add(Builder _this, int t) {
                _this.accept(t);
                return _this;
            }
        }
    }
}
