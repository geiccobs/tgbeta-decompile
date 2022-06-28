package j$.util.stream;

import j$.util.DesugarArrays;
import j$.util.Optional;
import j$.util.Spliterators;
import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.BinaryOperator;
import j$.util.function.Consumer;
import j$.util.function.Function;
import j$.util.function.IntFunction;
import j$.util.function.Predicate;
import j$.util.function.Supplier;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import j$.util.function.UnaryOperator;
import j$.util.stream.StreamSpliterators;
import j$.util.stream.Streams;
import java.util.Comparator;
import java.util.Iterator;
/* loaded from: classes2.dex */
public interface Stream<T> extends BaseStream<T, Stream<T>> {
    boolean allMatch(Predicate<? super T> predicate);

    boolean anyMatch(Predicate<? super T> predicate);

    <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> biConsumer, BiConsumer<R, R> biConsumer2);

    <R, A> R collect(Collector<? super T, A, R> collector);

    long count();

    Stream<T> distinct();

    Stream<T> filter(Predicate<? super T> predicate);

    Optional<T> findAny();

    Optional<T> findFirst();

    <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> function);

    DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> function);

    IntStream flatMapToInt(Function<? super T, ? extends IntStream> function);

    LongStream flatMapToLong(Function<? super T, ? extends LongStream> function);

    void forEach(Consumer<? super T> consumer);

    void forEachOrdered(Consumer<? super T> consumer);

    Stream<T> limit(long j);

    <R> Stream<R> map(Function<? super T, ? extends R> function);

    DoubleStream mapToDouble(ToDoubleFunction<? super T> toDoubleFunction);

    IntStream mapToInt(ToIntFunction<? super T> toIntFunction);

    LongStream mapToLong(ToLongFunction<? super T> toLongFunction);

    Optional<T> max(Comparator<? super T> comparator);

    Optional<T> min(Comparator<? super T> comparator);

    boolean noneMatch(Predicate<? super T> predicate);

    Stream<T> peek(Consumer<? super T> consumer);

    Optional<T> reduce(BinaryOperator<T> binaryOperator);

    <U> U reduce(U u, BiFunction<U, ? super T, U> biFunction, BinaryOperator<U> binaryOperator);

    T reduce(T t, BinaryOperator<T> binaryOperator);

    Stream<T> skip(long j);

    Stream<T> sorted();

    Stream<T> sorted(Comparator<? super T> comparator);

    Object[] toArray();

    <A> A[] toArray(IntFunction<A[]> intFunction);

    /* renamed from: j$.util.stream.Stream$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static <T> Builder<T> builder() {
            return new Streams.StreamBuilderImpl();
        }

        public static <T> Stream<T> empty() {
            return StreamSupport.stream(Spliterators.emptySpliterator(), false);
        }

        public static <T> Stream<T> of(T t) {
            return StreamSupport.stream(new Streams.StreamBuilderImpl(t), false);
        }

        public static <T> Stream<T> of(T... values) {
            return DesugarArrays.stream(values);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.UnaryOperator != java.util.function.UnaryOperator<T> */
        public static <T> Stream<T> iterate(final T seed, final UnaryOperator<T> unaryOperator) {
            unaryOperator.getClass();
            Iterator<T> iterator = new Iterator<T>() { // from class: j$.util.stream.Stream.1
                T t = (T) Streams.NONE;

                @Override // java.util.Iterator
                public boolean hasNext() {
                    return true;
                }

                @Override // java.util.Iterator
                public T next() {
                    T apply = this.t == Streams.NONE ? (T) seed : unaryOperator.apply(this.t);
                    this.t = apply;
                    return apply;
                }
            };
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 1040), false);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<T> */
        public static <T> Stream<T> generate(Supplier<T> supplier) {
            supplier.getClass();
            return StreamSupport.stream(new StreamSpliterators.InfiniteSupplyingSpliterator.OfRef(Long.MAX_VALUE, supplier), false);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<T> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Stream != java.util.stream.Stream<? extends T> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Stream != java.util.stream.Stream<T> */
        public static <T> Stream<T> concat(Stream<? extends T> stream, Stream<? extends T> stream2) {
            stream.getClass();
            stream2.getClass();
            return StreamSupport.stream(new Streams.ConcatSpliterator.OfRef(stream.spliterator(), stream2.spliterator()), stream.isParallel() || stream2.isParallel()).onClose(Streams.composedClose(stream, stream2));
        }
    }

    /* loaded from: classes2.dex */
    public interface Builder<T> extends Consumer<T> {
        @Override // j$.util.function.Consumer
        void accept(T t);

        Builder<T> add(T t);

        Stream<T> build();

        /* renamed from: j$.util.stream.Stream$Builder$-CC */
        /* loaded from: classes2.dex */
        public final /* synthetic */ class CC {
        }
    }
}
