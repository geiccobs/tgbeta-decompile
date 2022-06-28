package j$.util.stream;

import j$.util.DesugarArrays;
import j$.util.Iterator;
import j$.util.LongSummaryStatistics;
import j$.util.OptionalDouble;
import j$.util.OptionalLong;
import j$.util.PrimitiveIterator;
import j$.util.Spliterator;
import j$.util.Spliterators;
import j$.util.function.BiConsumer;
import j$.util.function.Consumer;
import j$.util.function.LongBinaryOperator;
import j$.util.function.LongConsumer;
import j$.util.function.LongFunction;
import j$.util.function.LongPredicate;
import j$.util.function.LongSupplier;
import j$.util.function.LongToDoubleFunction;
import j$.util.function.LongToIntFunction;
import j$.util.function.LongUnaryOperator;
import j$.util.function.ObjLongConsumer;
import j$.util.function.Supplier;
import j$.util.stream.StreamSpliterators;
import j$.util.stream.Streams;
import java.util.Iterator;
/* loaded from: classes2.dex */
public interface LongStream extends BaseStream<Long, LongStream> {
    boolean allMatch(LongPredicate longPredicate);

    boolean anyMatch(LongPredicate longPredicate);

    DoubleStream asDoubleStream();

    OptionalDouble average();

    Stream<Long> boxed();

    <R> R collect(Supplier<R> supplier, ObjLongConsumer<R> objLongConsumer, BiConsumer<R, R> biConsumer);

    long count();

    LongStream distinct();

    LongStream filter(LongPredicate longPredicate);

    OptionalLong findAny();

    OptionalLong findFirst();

    LongStream flatMap(LongFunction<? extends LongStream> longFunction);

    void forEach(LongConsumer longConsumer);

    void forEachOrdered(LongConsumer longConsumer);

    @Override // j$.util.stream.BaseStream, j$.util.stream.DoubleStream
    Iterator<Long> iterator();

    LongStream limit(long j);

    LongStream map(LongUnaryOperator longUnaryOperator);

    DoubleStream mapToDouble(LongToDoubleFunction longToDoubleFunction);

    IntStream mapToInt(LongToIntFunction longToIntFunction);

    <U> Stream<U> mapToObj(LongFunction<? extends U> longFunction);

    OptionalLong max();

    OptionalLong min();

    boolean noneMatch(LongPredicate longPredicate);

    @Override // j$.util.stream.BaseStream
    LongStream parallel();

    LongStream peek(LongConsumer longConsumer);

    long reduce(long j, LongBinaryOperator longBinaryOperator);

    OptionalLong reduce(LongBinaryOperator longBinaryOperator);

    @Override // j$.util.stream.BaseStream
    LongStream sequential();

    LongStream skip(long j);

    LongStream sorted();

    @Override // j$.util.stream.BaseStream
    Spliterator<Long> spliterator();

    long sum();

    LongSummaryStatistics summaryStatistics();

    long[] toArray();

    /* renamed from: j$.util.stream.LongStream$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static Builder builder() {
            return new Streams.LongStreamBuilderImpl();
        }

        public static LongStream empty() {
            return StreamSupport.longStream(Spliterators.emptyLongSpliterator(), false);
        }

        public static LongStream of(long t) {
            return StreamSupport.longStream(new Streams.LongStreamBuilderImpl(t), false);
        }

        public static LongStream of(long... values) {
            return DesugarArrays.stream(values);
        }

        public static LongStream iterate(long seed, LongUnaryOperator f) {
            f.getClass();
            PrimitiveIterator.OfLong iterator = new AnonymousClass1(seed, f);
            return StreamSupport.longStream(Spliterators.spliteratorUnknownSize(iterator, 1296), false);
        }

        public static LongStream generate(LongSupplier s) {
            s.getClass();
            return StreamSupport.longStream(new StreamSpliterators.InfiniteSupplyingSpliterator.OfLong(Long.MAX_VALUE, s), false);
        }

        public static LongStream range(long startInclusive, long endExclusive) {
            if (startInclusive >= endExclusive) {
                return empty();
            }
            if (endExclusive - startInclusive < 0) {
                long m = LongStream$$ExternalSyntheticBackport0.m(endExclusive - startInclusive, 2L) + startInclusive + 1;
                return concat(range(startInclusive, m), range(m, endExclusive));
            }
            return StreamSupport.longStream(new Streams.RangeLongSpliterator(startInclusive, endExclusive, false), false);
        }

        public static LongStream rangeClosed(long startInclusive, long endInclusive) {
            if (startInclusive > endInclusive) {
                return empty();
            }
            if ((endInclusive - startInclusive) + 1 <= 0) {
                long m = LongStream$$ExternalSyntheticBackport0.m(endInclusive - startInclusive, 2L) + startInclusive + 1;
                return concat(range(startInclusive, m), rangeClosed(m, endInclusive));
            }
            return StreamSupport.longStream(new Streams.RangeLongSpliterator(startInclusive, endInclusive, true), false);
        }

        /* JADX WARN: Type inference failed for: r1v0, types: [j$.util.Spliterator$OfLong] */
        /* JADX WARN: Type inference failed for: r2v0, types: [j$.util.Spliterator$OfLong] */
        public static LongStream concat(LongStream a, LongStream b) {
            a.getClass();
            b.getClass();
            Spliterator.OfLong split = new Streams.ConcatSpliterator.OfLong(a.spliterator(), b.spliterator());
            LongStream stream = StreamSupport.longStream(split, a.isParallel() || b.isParallel());
            return stream.onClose(Streams.composedClose(a, b));
        }
    }

    /* renamed from: j$.util.stream.LongStream$1 */
    /* loaded from: classes2.dex */
    class AnonymousClass1 implements PrimitiveIterator.OfLong, j$.util.Iterator {
        long t;
        final /* synthetic */ LongUnaryOperator val$f;
        final /* synthetic */ long val$seed;

        @Override // j$.util.PrimitiveIterator.OfLong, j$.util.Iterator
        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            PrimitiveIterator.OfLong.CC.$default$forEachRemaining((PrimitiveIterator.OfLong) this, consumer);
        }

        @Override // j$.util.PrimitiveIterator.OfLong
        public /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
            PrimitiveIterator.OfLong.CC.$default$forEachRemaining((PrimitiveIterator.OfLong) this, longConsumer);
        }

        @Override // j$.util.PrimitiveIterator
        public /* bridge */ /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
            forEachRemaining((LongConsumer) longConsumer);
        }

        @Override // java.util.Iterator, j$.util.Iterator
        public /* synthetic */ void remove() {
            Iterator.CC.$default$remove(this);
        }

        AnonymousClass1(long j, LongUnaryOperator longUnaryOperator) {
            this.val$seed = j;
            this.val$f = longUnaryOperator;
            this.t = j;
        }

        @Override // java.util.Iterator, j$.util.Iterator
        public boolean hasNext() {
            return true;
        }

        @Override // j$.util.PrimitiveIterator.OfLong
        public long nextLong() {
            long v = this.t;
            this.t = this.val$f.applyAsLong(this.t);
            return v;
        }
    }

    /* loaded from: classes2.dex */
    public interface Builder extends LongConsumer {
        @Override // j$.util.function.LongConsumer
        void accept(long j);

        Builder add(long j);

        LongStream build();

        /* renamed from: j$.util.stream.LongStream$Builder$-CC */
        /* loaded from: classes2.dex */
        public final /* synthetic */ class CC {
            public static Builder $default$add(Builder _this, long t) {
                _this.accept(t);
                return _this;
            }
        }
    }
}
