package j$.util.stream;

import j$.util.DesugarArrays;
import j$.util.DoubleSummaryStatistics;
import j$.util.Iterator;
import j$.util.OptionalDouble;
import j$.util.PrimitiveIterator;
import j$.util.Spliterator;
import j$.util.Spliterators;
import j$.util.function.BiConsumer;
import j$.util.function.Consumer;
import j$.util.function.DoubleBinaryOperator;
import j$.util.function.DoubleConsumer;
import j$.util.function.DoubleFunction;
import j$.util.function.DoublePredicate;
import j$.util.function.DoubleSupplier;
import j$.util.function.DoubleToIntFunction;
import j$.util.function.DoubleToLongFunction;
import j$.util.function.DoubleUnaryOperator;
import j$.util.function.ObjDoubleConsumer;
import j$.util.function.Supplier;
import j$.util.stream.StreamSpliterators;
import j$.util.stream.Streams;
import java.util.Iterator;
/* loaded from: classes2.dex */
public interface DoubleStream extends BaseStream<Double, DoubleStream> {
    boolean allMatch(DoublePredicate doublePredicate);

    boolean anyMatch(DoublePredicate doublePredicate);

    OptionalDouble average();

    Stream<Double> boxed();

    <R> R collect(Supplier<R> supplier, ObjDoubleConsumer<R> objDoubleConsumer, BiConsumer<R, R> biConsumer);

    long count();

    DoubleStream distinct();

    DoubleStream filter(DoublePredicate doublePredicate);

    OptionalDouble findAny();

    OptionalDouble findFirst();

    DoubleStream flatMap(DoubleFunction<? extends DoubleStream> doubleFunction);

    void forEach(DoubleConsumer doubleConsumer);

    void forEachOrdered(DoubleConsumer doubleConsumer);

    @Override // 
    Iterator<Double> iterator();

    DoubleStream limit(long j);

    DoubleStream map(DoubleUnaryOperator doubleUnaryOperator);

    IntStream mapToInt(DoubleToIntFunction doubleToIntFunction);

    LongStream mapToLong(DoubleToLongFunction doubleToLongFunction);

    <U> Stream<U> mapToObj(DoubleFunction<? extends U> doubleFunction);

    OptionalDouble max();

    OptionalDouble min();

    boolean noneMatch(DoublePredicate doublePredicate);

    @Override // j$.util.stream.BaseStream
    DoubleStream parallel();

    DoubleStream peek(DoubleConsumer doubleConsumer);

    double reduce(double d, DoubleBinaryOperator doubleBinaryOperator);

    OptionalDouble reduce(DoubleBinaryOperator doubleBinaryOperator);

    @Override // j$.util.stream.BaseStream
    DoubleStream sequential();

    DoubleStream skip(long j);

    DoubleStream sorted();

    @Override // j$.util.stream.BaseStream
    Spliterator<Double> spliterator();

    double sum();

    DoubleSummaryStatistics summaryStatistics();

    double[] toArray();

    /* renamed from: j$.util.stream.DoubleStream$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static Builder builder() {
            return new Streams.DoubleStreamBuilderImpl();
        }

        public static DoubleStream empty() {
            return StreamSupport.doubleStream(Spliterators.emptyDoubleSpliterator(), false);
        }

        public static DoubleStream of(double t) {
            return StreamSupport.doubleStream(new Streams.DoubleStreamBuilderImpl(t), false);
        }

        public static DoubleStream of(double... values) {
            return DesugarArrays.stream(values);
        }

        public static DoubleStream iterate(double seed, DoubleUnaryOperator f) {
            f.getClass();
            PrimitiveIterator.OfDouble iterator = new AnonymousClass1(seed, f);
            return StreamSupport.doubleStream(Spliterators.spliteratorUnknownSize(iterator, 1296), false);
        }

        public static DoubleStream generate(DoubleSupplier s) {
            s.getClass();
            return StreamSupport.doubleStream(new StreamSpliterators.InfiniteSupplyingSpliterator.OfDouble(Long.MAX_VALUE, s), false);
        }

        /* JADX WARN: Type inference failed for: r1v0, types: [j$.util.Spliterator$OfDouble] */
        /* JADX WARN: Type inference failed for: r2v0, types: [j$.util.Spliterator$OfDouble] */
        public static DoubleStream concat(DoubleStream a, DoubleStream b) {
            a.getClass();
            b.getClass();
            Spliterator.OfDouble split = new Streams.ConcatSpliterator.OfDouble(a.spliterator(), b.spliterator());
            DoubleStream stream = StreamSupport.doubleStream(split, a.isParallel() || b.isParallel());
            return stream.onClose(Streams.composedClose(a, b));
        }
    }

    /* renamed from: j$.util.stream.DoubleStream$1 */
    /* loaded from: classes2.dex */
    class AnonymousClass1 implements PrimitiveIterator.OfDouble, j$.util.Iterator {
        double t;
        final /* synthetic */ DoubleUnaryOperator val$f;
        final /* synthetic */ double val$seed;

        @Override // j$.util.PrimitiveIterator.OfDouble, j$.util.Iterator
        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            PrimitiveIterator.OfDouble.CC.$default$forEachRemaining((PrimitiveIterator.OfDouble) this, consumer);
        }

        @Override // j$.util.PrimitiveIterator.OfDouble
        public /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
            PrimitiveIterator.OfDouble.CC.$default$forEachRemaining((PrimitiveIterator.OfDouble) this, doubleConsumer);
        }

        @Override // j$.util.PrimitiveIterator
        public /* bridge */ /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
            forEachRemaining((DoubleConsumer) doubleConsumer);
        }

        @Override // java.util.Iterator, j$.util.Iterator
        public /* synthetic */ void remove() {
            Iterator.CC.$default$remove(this);
        }

        AnonymousClass1(double d, DoubleUnaryOperator doubleUnaryOperator) {
            this.val$seed = d;
            this.val$f = doubleUnaryOperator;
            this.t = d;
        }

        @Override // java.util.Iterator, j$.util.Iterator
        public boolean hasNext() {
            return true;
        }

        @Override // j$.util.PrimitiveIterator.OfDouble
        public double nextDouble() {
            double v = this.t;
            this.t = this.val$f.applyAsDouble(this.t);
            return v;
        }
    }

    /* loaded from: classes2.dex */
    public interface Builder extends DoubleConsumer {
        @Override // j$.util.function.DoubleConsumer
        void accept(double d);

        Builder add(double d);

        DoubleStream build();

        /* renamed from: j$.util.stream.DoubleStream$Builder$-CC */
        /* loaded from: classes2.dex */
        public final /* synthetic */ class CC {
            public static Builder $default$add(Builder _this, double t) {
                _this.accept(t);
                return _this;
            }
        }
    }
}
