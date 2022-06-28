package j$.util.stream;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import j$.util.Optional;
import j$.util.OptionalDouble;
import j$.util.OptionalInt;
import j$.util.OptionalLong;
import j$.util.Spliterator;
import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.BinaryOperator;
import j$.util.function.Consumer;
import j$.util.function.DoubleBinaryOperator;
import j$.util.function.DoubleConsumer;
import j$.util.function.IntBinaryOperator;
import j$.util.function.IntConsumer;
import j$.util.function.LongBinaryOperator;
import j$.util.function.LongConsumer;
import j$.util.function.ObjDoubleConsumer;
import j$.util.function.ObjIntConsumer;
import j$.util.function.ObjLongConsumer;
import j$.util.function.Supplier;
import j$.util.stream.Collector;
import j$.util.stream.Sink;
import j$.util.stream.TerminalOp;
import java.util.concurrent.CountedCompleter;
/* loaded from: classes2.dex */
public final class ReduceOps {

    /* loaded from: classes2.dex */
    public interface AccumulatingSink<T, R, K extends AccumulatingSink<T, R, K>> extends TerminalSink<T, R> {
        void combine(K k);
    }

    private ReduceOps() {
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BiFunction != java.util.function.BiFunction<U, ? super T, U> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BinaryOperator != java.util.function.BinaryOperator<U> */
    public static <T, U> TerminalOp<T, U> makeRef(final U seed, final BiFunction<U, ? super T, U> biFunction, final BinaryOperator<U> binaryOperator) {
        biFunction.getClass();
        binaryOperator.getClass();
        return new ReduceOp<T, U, C1ReducingSink>(StreamShape.REFERENCE) { // from class: j$.util.stream.ReduceOps.1
            @Override // j$.util.stream.ReduceOps.ReduceOp
            public C1ReducingSink makeSink() {
                return new C1ReducingSink(seed, biFunction, binaryOperator);
            }
        };
    }

    /* renamed from: j$.util.stream.ReduceOps$1ReducingSink */
    /* loaded from: classes2.dex */
    public class C1ReducingSink extends Box<U> implements AccumulatingSink<T, U, C1ReducingSink> {
        final /* synthetic */ BinaryOperator val$combiner;
        final /* synthetic */ BiFunction val$reducer;
        final /* synthetic */ Object val$seed;

        @Override // j$.util.stream.Sink, j$.util.function.DoubleConsumer
        public /* synthetic */ void accept(double d) {
            Sink.CC.$default$accept(this, d);
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ void accept(int i) {
            Sink.CC.$default$accept((Sink) this, i);
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ void accept(long j) {
            Sink.CC.$default$accept((Sink) this, j);
        }

        @Override // j$.util.function.Consumer
        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return consumer.getClass();
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ boolean cancellationRequested() {
            return Sink.CC.$default$cancellationRequested(this);
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ void end() {
            Sink.CC.$default$end(this);
        }

        C1ReducingSink(Object obj, BiFunction biFunction, BinaryOperator binaryOperator) {
            this.val$seed = obj;
            this.val$reducer = biFunction;
            this.val$combiner = binaryOperator;
        }

        /* JADX WARN: Type inference failed for: r0v0, types: [U, java.lang.Object] */
        @Override // j$.util.stream.Sink
        public void begin(long size) {
            this.state = this.val$seed;
        }

        /* JADX WARN: Type inference failed for: r0v1, types: [U, java.lang.Object] */
        @Override // j$.util.function.Consumer
        public void accept(T t) {
            this.state = this.val$reducer.apply(this.state, t);
        }

        /* JADX WARN: Type inference failed for: r0v1, types: [U, java.lang.Object] */
        public void combine(C1ReducingSink other) {
            this.state = this.val$combiner.apply(this.state, other.state);
        }
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BinaryOperator != java.util.function.BinaryOperator<T> */
    public static <T> TerminalOp<T, Optional<T>> makeRef(final BinaryOperator<T> binaryOperator) {
        binaryOperator.getClass();
        return new ReduceOp<T, Optional<T>, C2ReducingSink>(StreamShape.REFERENCE) { // from class: j$.util.stream.ReduceOps.2
            /* JADX WARN: Type inference failed for: r0v0, types: [j$.util.stream.ReduceOps$2ReducingSink] */
            @Override // j$.util.stream.ReduceOps.ReduceOp
            public C2ReducingSink makeSink() {
                final BinaryOperator binaryOperator2 = binaryOperator;
                return new AccumulatingSink<T, Optional<T>, C2ReducingSink>() { // from class: j$.util.stream.ReduceOps.2ReducingSink
                    private boolean empty;
                    private T state;

                    @Override // j$.util.stream.Sink, j$.util.function.DoubleConsumer
                    public /* synthetic */ void accept(double d) {
                        Sink.CC.$default$accept(this, d);
                    }

                    @Override // j$.util.stream.Sink
                    public /* synthetic */ void accept(int i) {
                        Sink.CC.$default$accept((Sink) this, i);
                    }

                    @Override // j$.util.stream.Sink
                    public /* synthetic */ void accept(long j) {
                        Sink.CC.$default$accept((Sink) this, j);
                    }

                    @Override // j$.util.function.Consumer
                    public /* synthetic */ Consumer andThen(Consumer consumer) {
                        return consumer.getClass();
                    }

                    @Override // j$.util.stream.Sink
                    public /* synthetic */ boolean cancellationRequested() {
                        return Sink.CC.$default$cancellationRequested(this);
                    }

                    @Override // j$.util.stream.Sink
                    public /* synthetic */ void end() {
                        Sink.CC.$default$end(this);
                    }

                    @Override // j$.util.stream.Sink
                    public void begin(long size) {
                        this.empty = true;
                        this.state = null;
                    }

                    @Override // j$.util.function.Consumer
                    public void accept(T t) {
                        if (this.empty) {
                            this.empty = false;
                            this.state = t;
                            return;
                        }
                        this.state = binaryOperator2.apply(this.state, t);
                    }

                    @Override // j$.util.function.Supplier
                    public Optional<T> get() {
                        return this.empty ? Optional.empty() : Optional.of(this.state);
                    }

                    public void combine(C2ReducingSink other) {
                        if (!other.empty) {
                            accept((C2ReducingSink) other.state);
                        }
                    }
                };
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BiConsumer != java.util.function.BiConsumer<I, ? super T> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BinaryOperator != java.util.function.BinaryOperator<I> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<I> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Collector != java.util.stream.Collector<? super T, I, ?> */
    public static <T, I> TerminalOp<T, I> makeRef(final Collector<? super T, I, ?> collector) {
        collector.getClass();
        final Supplier<I> supplier = collector.supplier();
        final BiConsumer<I, ? super T> accumulator = collector.accumulator();
        final BinaryOperator<I> combiner = collector.combiner();
        return new ReduceOp<T, I, C3ReducingSink>(StreamShape.REFERENCE) { // from class: j$.util.stream.ReduceOps.3
            @Override // j$.util.stream.ReduceOps.ReduceOp
            public C3ReducingSink makeSink() {
                return new C3ReducingSink(supplier, accumulator, combiner);
            }

            @Override // j$.util.stream.ReduceOps.ReduceOp, j$.util.stream.TerminalOp
            public int getOpFlags() {
                if (collector.characteristics().contains(Collector.Characteristics.UNORDERED)) {
                    return StreamOpFlag.NOT_ORDERED;
                }
                return 0;
            }
        };
    }

    /* renamed from: j$.util.stream.ReduceOps$3ReducingSink */
    /* loaded from: classes2.dex */
    public class C3ReducingSink extends Box<I> implements AccumulatingSink<T, I, C3ReducingSink> {
        final /* synthetic */ BiConsumer val$accumulator;
        final /* synthetic */ BinaryOperator val$combiner;
        final /* synthetic */ Supplier val$supplier;

        @Override // j$.util.stream.Sink, j$.util.function.DoubleConsumer
        public /* synthetic */ void accept(double d) {
            Sink.CC.$default$accept(this, d);
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ void accept(int i) {
            Sink.CC.$default$accept((Sink) this, i);
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ void accept(long j) {
            Sink.CC.$default$accept((Sink) this, j);
        }

        @Override // j$.util.function.Consumer
        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return consumer.getClass();
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ boolean cancellationRequested() {
            return Sink.CC.$default$cancellationRequested(this);
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ void end() {
            Sink.CC.$default$end(this);
        }

        C3ReducingSink(Supplier supplier, BiConsumer biConsumer, BinaryOperator binaryOperator) {
            this.val$supplier = supplier;
            this.val$accumulator = biConsumer;
            this.val$combiner = binaryOperator;
        }

        /* JADX WARN: Type inference failed for: r0v1, types: [U, java.lang.Object] */
        @Override // j$.util.stream.Sink
        public void begin(long size) {
            this.state = this.val$supplier.get();
        }

        @Override // j$.util.function.Consumer
        public void accept(T t) {
            this.val$accumulator.accept(this.state, t);
        }

        /* JADX WARN: Type inference failed for: r0v1, types: [U, java.lang.Object] */
        public void combine(C3ReducingSink other) {
            this.state = this.val$combiner.apply(this.state, other.state);
        }
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BiConsumer != java.util.function.BiConsumer<R, ? super T> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BiConsumer != java.util.function.BiConsumer<R, R> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<R> */
    public static <T, R> TerminalOp<T, R> makeRef(final Supplier<R> supplier, final BiConsumer<R, ? super T> biConsumer, final BiConsumer<R, R> biConsumer2) {
        supplier.getClass();
        biConsumer.getClass();
        biConsumer2.getClass();
        return new ReduceOp<T, R, C4ReducingSink>(StreamShape.REFERENCE) { // from class: j$.util.stream.ReduceOps.4
            @Override // j$.util.stream.ReduceOps.ReduceOp
            public C4ReducingSink makeSink() {
                return new C4ReducingSink(supplier, biConsumer, biConsumer2);
            }
        };
    }

    /* renamed from: j$.util.stream.ReduceOps$4ReducingSink */
    /* loaded from: classes2.dex */
    public class C4ReducingSink extends Box<R> implements AccumulatingSink<T, R, C4ReducingSink> {
        final /* synthetic */ BiConsumer val$accumulator;
        final /* synthetic */ BiConsumer val$reducer;
        final /* synthetic */ Supplier val$seedFactory;

        @Override // j$.util.stream.Sink, j$.util.function.DoubleConsumer
        public /* synthetic */ void accept(double d) {
            Sink.CC.$default$accept(this, d);
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ void accept(int i) {
            Sink.CC.$default$accept((Sink) this, i);
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ void accept(long j) {
            Sink.CC.$default$accept((Sink) this, j);
        }

        @Override // j$.util.function.Consumer
        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return consumer.getClass();
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ boolean cancellationRequested() {
            return Sink.CC.$default$cancellationRequested(this);
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ void end() {
            Sink.CC.$default$end(this);
        }

        C4ReducingSink(Supplier supplier, BiConsumer biConsumer, BiConsumer biConsumer2) {
            this.val$seedFactory = supplier;
            this.val$accumulator = biConsumer;
            this.val$reducer = biConsumer2;
        }

        /* JADX WARN: Type inference failed for: r0v1, types: [U, java.lang.Object] */
        @Override // j$.util.stream.Sink
        public void begin(long size) {
            this.state = this.val$seedFactory.get();
        }

        @Override // j$.util.function.Consumer
        public void accept(T t) {
            this.val$accumulator.accept(this.state, t);
        }

        public void combine(C4ReducingSink other) {
            this.val$reducer.accept(this.state, other.state);
        }
    }

    /* renamed from: j$.util.stream.ReduceOps$5ReducingSink */
    /* loaded from: classes2.dex */
    public class C5ReducingSink implements AccumulatingSink<Integer, Integer, C5ReducingSink>, Sink.OfInt {
        private int state;
        final /* synthetic */ int val$identity;
        final /* synthetic */ IntBinaryOperator val$operator;

        @Override // j$.util.stream.Sink, j$.util.function.DoubleConsumer
        public /* synthetic */ void accept(double d) {
            Sink.CC.$default$accept(this, d);
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ void accept(long j) {
            Sink.CC.$default$accept((Sink) this, j);
        }

        @Override // j$.util.stream.Sink.OfInt
        public /* synthetic */ void accept(Integer num) {
            Sink.OfInt.CC.$default$accept((Sink.OfInt) this, num);
        }

        @Override // j$.util.function.Consumer
        public /* bridge */ /* synthetic */ void accept(Object obj) {
            Sink.OfInt.CC.$default$accept(this, obj);
        }

        @Override // j$.util.function.Consumer
        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return consumer.getClass();
        }

        @Override // j$.util.function.IntConsumer
        public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
            return intConsumer.getClass();
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ boolean cancellationRequested() {
            return Sink.CC.$default$cancellationRequested(this);
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ void end() {
            Sink.CC.$default$end(this);
        }

        C5ReducingSink(int i, IntBinaryOperator intBinaryOperator) {
            this.val$identity = i;
            this.val$operator = intBinaryOperator;
        }

        @Override // j$.util.stream.Sink
        public void begin(long size) {
            this.state = this.val$identity;
        }

        @Override // j$.util.stream.Sink
        public void accept(int t) {
            this.state = this.val$operator.applyAsInt(this.state, t);
        }

        @Override // j$.util.function.Supplier
        public Integer get() {
            return Integer.valueOf(this.state);
        }

        public void combine(C5ReducingSink other) {
            accept(other.state);
        }
    }

    public static TerminalOp<Integer, Integer> makeInt(final int identity, final IntBinaryOperator operator) {
        operator.getClass();
        return new ReduceOp<Integer, Integer, C5ReducingSink>(StreamShape.INT_VALUE) { // from class: j$.util.stream.ReduceOps.5
            @Override // j$.util.stream.ReduceOps.ReduceOp
            public C5ReducingSink makeSink() {
                return new C5ReducingSink(identity, operator);
            }
        };
    }

    /* renamed from: j$.util.stream.ReduceOps$6ReducingSink */
    /* loaded from: classes2.dex */
    public class C6ReducingSink implements AccumulatingSink<Integer, OptionalInt, C6ReducingSink>, Sink.OfInt {
        private boolean empty;
        private int state;
        final /* synthetic */ IntBinaryOperator val$operator;

        @Override // j$.util.stream.Sink, j$.util.function.DoubleConsumer
        public /* synthetic */ void accept(double d) {
            Sink.CC.$default$accept(this, d);
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ void accept(long j) {
            Sink.CC.$default$accept((Sink) this, j);
        }

        @Override // j$.util.stream.Sink.OfInt
        public /* synthetic */ void accept(Integer num) {
            Sink.OfInt.CC.$default$accept((Sink.OfInt) this, num);
        }

        @Override // j$.util.function.Consumer
        public /* bridge */ /* synthetic */ void accept(Object obj) {
            Sink.OfInt.CC.$default$accept(this, obj);
        }

        @Override // j$.util.function.Consumer
        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return consumer.getClass();
        }

        @Override // j$.util.function.IntConsumer
        public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
            return intConsumer.getClass();
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ boolean cancellationRequested() {
            return Sink.CC.$default$cancellationRequested(this);
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ void end() {
            Sink.CC.$default$end(this);
        }

        C6ReducingSink(IntBinaryOperator intBinaryOperator) {
            this.val$operator = intBinaryOperator;
        }

        @Override // j$.util.stream.Sink
        public void begin(long size) {
            this.empty = true;
            this.state = 0;
        }

        @Override // j$.util.stream.Sink
        public void accept(int t) {
            if (this.empty) {
                this.empty = false;
                this.state = t;
                return;
            }
            this.state = this.val$operator.applyAsInt(this.state, t);
        }

        @Override // j$.util.function.Supplier
        public OptionalInt get() {
            return this.empty ? OptionalInt.empty() : OptionalInt.of(this.state);
        }

        public void combine(C6ReducingSink other) {
            if (!other.empty) {
                accept(other.state);
            }
        }
    }

    public static TerminalOp<Integer, OptionalInt> makeInt(final IntBinaryOperator operator) {
        operator.getClass();
        return new ReduceOp<Integer, OptionalInt, C6ReducingSink>(StreamShape.INT_VALUE) { // from class: j$.util.stream.ReduceOps.6
            @Override // j$.util.stream.ReduceOps.ReduceOp
            public C6ReducingSink makeSink() {
                return new C6ReducingSink(operator);
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BinaryOperator != java.util.function.BinaryOperator<R> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.ObjIntConsumer != java.util.function.ObjIntConsumer<R> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<R> */
    public static <R> TerminalOp<Integer, R> makeInt(final Supplier<R> supplier, final ObjIntConsumer<R> objIntConsumer, final BinaryOperator<R> binaryOperator) {
        supplier.getClass();
        objIntConsumer.getClass();
        binaryOperator.getClass();
        return new ReduceOp<Integer, R, C7ReducingSink>(StreamShape.INT_VALUE) { // from class: j$.util.stream.ReduceOps.7
            @Override // j$.util.stream.ReduceOps.ReduceOp
            public C7ReducingSink makeSink() {
                return new C7ReducingSink(supplier, objIntConsumer, binaryOperator);
            }
        };
    }

    /* renamed from: j$.util.stream.ReduceOps$7ReducingSink */
    /* loaded from: classes2.dex */
    public class C7ReducingSink extends Box<R> implements AccumulatingSink<Integer, R, C7ReducingSink>, Sink.OfInt {
        final /* synthetic */ ObjIntConsumer val$accumulator;
        final /* synthetic */ BinaryOperator val$combiner;
        final /* synthetic */ Supplier val$supplier;

        @Override // j$.util.stream.Sink, j$.util.function.DoubleConsumer
        public /* synthetic */ void accept(double d) {
            Sink.CC.$default$accept(this, d);
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ void accept(long j) {
            Sink.CC.$default$accept((Sink) this, j);
        }

        @Override // j$.util.stream.Sink.OfInt
        public /* synthetic */ void accept(Integer num) {
            Sink.OfInt.CC.$default$accept((Sink.OfInt) this, num);
        }

        @Override // j$.util.function.Consumer
        public /* bridge */ /* synthetic */ void accept(Object obj) {
            Sink.OfInt.CC.$default$accept(this, obj);
        }

        @Override // j$.util.function.Consumer
        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return consumer.getClass();
        }

        @Override // j$.util.function.IntConsumer
        public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
            return intConsumer.getClass();
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ boolean cancellationRequested() {
            return Sink.CC.$default$cancellationRequested(this);
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ void end() {
            Sink.CC.$default$end(this);
        }

        C7ReducingSink(Supplier supplier, ObjIntConsumer objIntConsumer, BinaryOperator binaryOperator) {
            this.val$supplier = supplier;
            this.val$accumulator = objIntConsumer;
            this.val$combiner = binaryOperator;
        }

        /* JADX WARN: Type inference failed for: r0v1, types: [U, java.lang.Object] */
        @Override // j$.util.stream.Sink
        public void begin(long size) {
            this.state = this.val$supplier.get();
        }

        @Override // j$.util.stream.Sink
        public void accept(int t) {
            this.val$accumulator.accept(this.state, t);
        }

        /* JADX WARN: Type inference failed for: r0v1, types: [U, java.lang.Object] */
        public void combine(C7ReducingSink other) {
            this.state = this.val$combiner.apply(this.state, other.state);
        }
    }

    /* renamed from: j$.util.stream.ReduceOps$8ReducingSink */
    /* loaded from: classes2.dex */
    public class C8ReducingSink implements AccumulatingSink<Long, Long, C8ReducingSink>, Sink.OfLong {
        private long state;
        final /* synthetic */ long val$identity;
        final /* synthetic */ LongBinaryOperator val$operator;

        @Override // j$.util.stream.Sink, j$.util.function.DoubleConsumer
        public /* synthetic */ void accept(double d) {
            Sink.CC.$default$accept(this, d);
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ void accept(int i) {
            Sink.CC.$default$accept((Sink) this, i);
        }

        @Override // j$.util.stream.Sink.OfLong
        public /* synthetic */ void accept(Long l) {
            Sink.OfLong.CC.$default$accept((Sink.OfLong) this, l);
        }

        @Override // j$.util.function.Consumer
        public /* bridge */ /* synthetic */ void accept(Object obj) {
            Sink.OfLong.CC.$default$accept(this, obj);
        }

        @Override // j$.util.function.Consumer
        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return consumer.getClass();
        }

        @Override // j$.util.function.LongConsumer
        public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
            return longConsumer.getClass();
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ boolean cancellationRequested() {
            return Sink.CC.$default$cancellationRequested(this);
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ void end() {
            Sink.CC.$default$end(this);
        }

        C8ReducingSink(long j, LongBinaryOperator longBinaryOperator) {
            this.val$identity = j;
            this.val$operator = longBinaryOperator;
        }

        @Override // j$.util.stream.Sink
        public void begin(long size) {
            this.state = this.val$identity;
        }

        @Override // j$.util.stream.Sink
        public void accept(long t) {
            this.state = this.val$operator.applyAsLong(this.state, t);
        }

        @Override // j$.util.function.Supplier
        public Long get() {
            return Long.valueOf(this.state);
        }

        public void combine(C8ReducingSink other) {
            accept(other.state);
        }
    }

    public static TerminalOp<Long, Long> makeLong(final long identity, final LongBinaryOperator operator) {
        operator.getClass();
        return new ReduceOp<Long, Long, C8ReducingSink>(StreamShape.LONG_VALUE) { // from class: j$.util.stream.ReduceOps.8
            @Override // j$.util.stream.ReduceOps.ReduceOp
            public C8ReducingSink makeSink() {
                return new C8ReducingSink(identity, operator);
            }
        };
    }

    /* renamed from: j$.util.stream.ReduceOps$9ReducingSink */
    /* loaded from: classes2.dex */
    public class C9ReducingSink implements AccumulatingSink<Long, OptionalLong, C9ReducingSink>, Sink.OfLong {
        private boolean empty;
        private long state;
        final /* synthetic */ LongBinaryOperator val$operator;

        @Override // j$.util.stream.Sink, j$.util.function.DoubleConsumer
        public /* synthetic */ void accept(double d) {
            Sink.CC.$default$accept(this, d);
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ void accept(int i) {
            Sink.CC.$default$accept((Sink) this, i);
        }

        @Override // j$.util.stream.Sink.OfLong
        public /* synthetic */ void accept(Long l) {
            Sink.OfLong.CC.$default$accept((Sink.OfLong) this, l);
        }

        @Override // j$.util.function.Consumer
        public /* bridge */ /* synthetic */ void accept(Object obj) {
            Sink.OfLong.CC.$default$accept(this, obj);
        }

        @Override // j$.util.function.Consumer
        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return consumer.getClass();
        }

        @Override // j$.util.function.LongConsumer
        public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
            return longConsumer.getClass();
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ boolean cancellationRequested() {
            return Sink.CC.$default$cancellationRequested(this);
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ void end() {
            Sink.CC.$default$end(this);
        }

        C9ReducingSink(LongBinaryOperator longBinaryOperator) {
            this.val$operator = longBinaryOperator;
        }

        @Override // j$.util.stream.Sink
        public void begin(long size) {
            this.empty = true;
            this.state = 0L;
        }

        @Override // j$.util.stream.Sink
        public void accept(long t) {
            if (this.empty) {
                this.empty = false;
                this.state = t;
                return;
            }
            this.state = this.val$operator.applyAsLong(this.state, t);
        }

        @Override // j$.util.function.Supplier
        public OptionalLong get() {
            return this.empty ? OptionalLong.empty() : OptionalLong.of(this.state);
        }

        public void combine(C9ReducingSink other) {
            if (!other.empty) {
                accept(other.state);
            }
        }
    }

    public static TerminalOp<Long, OptionalLong> makeLong(final LongBinaryOperator operator) {
        operator.getClass();
        return new ReduceOp<Long, OptionalLong, C9ReducingSink>(StreamShape.LONG_VALUE) { // from class: j$.util.stream.ReduceOps.9
            @Override // j$.util.stream.ReduceOps.ReduceOp
            public C9ReducingSink makeSink() {
                return new C9ReducingSink(operator);
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BinaryOperator != java.util.function.BinaryOperator<R> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.ObjLongConsumer != java.util.function.ObjLongConsumer<R> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<R> */
    public static <R> TerminalOp<Long, R> makeLong(final Supplier<R> supplier, final ObjLongConsumer<R> objLongConsumer, final BinaryOperator<R> binaryOperator) {
        supplier.getClass();
        objLongConsumer.getClass();
        binaryOperator.getClass();
        return new ReduceOp<Long, R, C10ReducingSink>(StreamShape.LONG_VALUE) { // from class: j$.util.stream.ReduceOps.10
            @Override // j$.util.stream.ReduceOps.ReduceOp
            public C10ReducingSink makeSink() {
                return new C10ReducingSink(supplier, objLongConsumer, binaryOperator);
            }
        };
    }

    /* renamed from: j$.util.stream.ReduceOps$10ReducingSink */
    /* loaded from: classes2.dex */
    public class C10ReducingSink extends Box<R> implements AccumulatingSink<Long, R, C10ReducingSink>, Sink.OfLong {
        final /* synthetic */ ObjLongConsumer val$accumulator;
        final /* synthetic */ BinaryOperator val$combiner;
        final /* synthetic */ Supplier val$supplier;

        @Override // j$.util.stream.Sink, j$.util.function.DoubleConsumer
        public /* synthetic */ void accept(double d) {
            Sink.CC.$default$accept(this, d);
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ void accept(int i) {
            Sink.CC.$default$accept((Sink) this, i);
        }

        @Override // j$.util.stream.Sink.OfLong
        public /* synthetic */ void accept(Long l) {
            Sink.OfLong.CC.$default$accept((Sink.OfLong) this, l);
        }

        @Override // j$.util.function.Consumer
        public /* bridge */ /* synthetic */ void accept(Object obj) {
            Sink.OfLong.CC.$default$accept(this, obj);
        }

        @Override // j$.util.function.Consumer
        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return consumer.getClass();
        }

        @Override // j$.util.function.LongConsumer
        public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
            return longConsumer.getClass();
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ boolean cancellationRequested() {
            return Sink.CC.$default$cancellationRequested(this);
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ void end() {
            Sink.CC.$default$end(this);
        }

        C10ReducingSink(Supplier supplier, ObjLongConsumer objLongConsumer, BinaryOperator binaryOperator) {
            this.val$supplier = supplier;
            this.val$accumulator = objLongConsumer;
            this.val$combiner = binaryOperator;
        }

        /* JADX WARN: Type inference failed for: r0v1, types: [U, java.lang.Object] */
        @Override // j$.util.stream.Sink
        public void begin(long size) {
            this.state = this.val$supplier.get();
        }

        @Override // j$.util.stream.Sink
        public void accept(long t) {
            this.val$accumulator.accept(this.state, t);
        }

        /* JADX WARN: Type inference failed for: r0v1, types: [U, java.lang.Object] */
        public void combine(C10ReducingSink other) {
            this.state = this.val$combiner.apply(this.state, other.state);
        }
    }

    /* renamed from: j$.util.stream.ReduceOps$11ReducingSink */
    /* loaded from: classes2.dex */
    public class C11ReducingSink implements AccumulatingSink<Double, Double, C11ReducingSink>, Sink.OfDouble {
        private double state;
        final /* synthetic */ double val$identity;
        final /* synthetic */ DoubleBinaryOperator val$operator;

        @Override // j$.util.stream.Sink
        public /* synthetic */ void accept(int i) {
            Sink.CC.$default$accept((Sink) this, i);
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ void accept(long j) {
            Sink.CC.$default$accept((Sink) this, j);
        }

        @Override // j$.util.stream.Sink.OfDouble
        public /* synthetic */ void accept(Double d) {
            Sink.OfDouble.CC.$default$accept((Sink.OfDouble) this, d);
        }

        @Override // j$.util.function.Consumer
        public /* bridge */ /* synthetic */ void accept(Object obj) {
            Sink.OfDouble.CC.$default$accept(this, obj);
        }

        @Override // j$.util.function.Consumer
        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return consumer.getClass();
        }

        @Override // j$.util.function.DoubleConsumer
        public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
            return doubleConsumer.getClass();
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ boolean cancellationRequested() {
            return Sink.CC.$default$cancellationRequested(this);
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ void end() {
            Sink.CC.$default$end(this);
        }

        C11ReducingSink(double d, DoubleBinaryOperator doubleBinaryOperator) {
            this.val$identity = d;
            this.val$operator = doubleBinaryOperator;
        }

        @Override // j$.util.stream.Sink
        public void begin(long size) {
            this.state = this.val$identity;
        }

        @Override // j$.util.stream.Sink, j$.util.function.DoubleConsumer
        public void accept(double t) {
            this.state = this.val$operator.applyAsDouble(this.state, t);
        }

        @Override // j$.util.function.Supplier
        public Double get() {
            return Double.valueOf(this.state);
        }

        public void combine(C11ReducingSink other) {
            accept(other.state);
        }
    }

    public static TerminalOp<Double, Double> makeDouble(final double identity, final DoubleBinaryOperator operator) {
        operator.getClass();
        return new ReduceOp<Double, Double, C11ReducingSink>(StreamShape.DOUBLE_VALUE) { // from class: j$.util.stream.ReduceOps.11
            @Override // j$.util.stream.ReduceOps.ReduceOp
            public C11ReducingSink makeSink() {
                return new C11ReducingSink(identity, operator);
            }
        };
    }

    /* renamed from: j$.util.stream.ReduceOps$12ReducingSink */
    /* loaded from: classes2.dex */
    public class C12ReducingSink implements AccumulatingSink<Double, OptionalDouble, C12ReducingSink>, Sink.OfDouble {
        private boolean empty;
        private double state;
        final /* synthetic */ DoubleBinaryOperator val$operator;

        @Override // j$.util.stream.Sink
        public /* synthetic */ void accept(int i) {
            Sink.CC.$default$accept((Sink) this, i);
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ void accept(long j) {
            Sink.CC.$default$accept((Sink) this, j);
        }

        @Override // j$.util.stream.Sink.OfDouble
        public /* synthetic */ void accept(Double d) {
            Sink.OfDouble.CC.$default$accept((Sink.OfDouble) this, d);
        }

        @Override // j$.util.function.Consumer
        public /* bridge */ /* synthetic */ void accept(Object obj) {
            Sink.OfDouble.CC.$default$accept(this, obj);
        }

        @Override // j$.util.function.Consumer
        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return consumer.getClass();
        }

        @Override // j$.util.function.DoubleConsumer
        public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
            return doubleConsumer.getClass();
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ boolean cancellationRequested() {
            return Sink.CC.$default$cancellationRequested(this);
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ void end() {
            Sink.CC.$default$end(this);
        }

        C12ReducingSink(DoubleBinaryOperator doubleBinaryOperator) {
            this.val$operator = doubleBinaryOperator;
        }

        @Override // j$.util.stream.Sink
        public void begin(long size) {
            this.empty = true;
            this.state = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        }

        @Override // j$.util.stream.Sink, j$.util.function.DoubleConsumer
        public void accept(double t) {
            if (this.empty) {
                this.empty = false;
                this.state = t;
                return;
            }
            this.state = this.val$operator.applyAsDouble(this.state, t);
        }

        @Override // j$.util.function.Supplier
        public OptionalDouble get() {
            return this.empty ? OptionalDouble.empty() : OptionalDouble.of(this.state);
        }

        public void combine(C12ReducingSink other) {
            if (!other.empty) {
                accept(other.state);
            }
        }
    }

    public static TerminalOp<Double, OptionalDouble> makeDouble(final DoubleBinaryOperator operator) {
        operator.getClass();
        return new ReduceOp<Double, OptionalDouble, C12ReducingSink>(StreamShape.DOUBLE_VALUE) { // from class: j$.util.stream.ReduceOps.12
            @Override // j$.util.stream.ReduceOps.ReduceOp
            public C12ReducingSink makeSink() {
                return new C12ReducingSink(operator);
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BinaryOperator != java.util.function.BinaryOperator<R> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.ObjDoubleConsumer != java.util.function.ObjDoubleConsumer<R> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<R> */
    public static <R> TerminalOp<Double, R> makeDouble(final Supplier<R> supplier, final ObjDoubleConsumer<R> objDoubleConsumer, final BinaryOperator<R> binaryOperator) {
        supplier.getClass();
        objDoubleConsumer.getClass();
        binaryOperator.getClass();
        return new ReduceOp<Double, R, C13ReducingSink>(StreamShape.DOUBLE_VALUE) { // from class: j$.util.stream.ReduceOps.13
            @Override // j$.util.stream.ReduceOps.ReduceOp
            public C13ReducingSink makeSink() {
                return new C13ReducingSink(supplier, objDoubleConsumer, binaryOperator);
            }
        };
    }

    /* renamed from: j$.util.stream.ReduceOps$13ReducingSink */
    /* loaded from: classes2.dex */
    public class C13ReducingSink extends Box<R> implements AccumulatingSink<Double, R, C13ReducingSink>, Sink.OfDouble {
        final /* synthetic */ ObjDoubleConsumer val$accumulator;
        final /* synthetic */ BinaryOperator val$combiner;
        final /* synthetic */ Supplier val$supplier;

        @Override // j$.util.stream.Sink
        public /* synthetic */ void accept(int i) {
            Sink.CC.$default$accept((Sink) this, i);
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ void accept(long j) {
            Sink.CC.$default$accept((Sink) this, j);
        }

        @Override // j$.util.stream.Sink.OfDouble
        public /* synthetic */ void accept(Double d) {
            Sink.OfDouble.CC.$default$accept((Sink.OfDouble) this, d);
        }

        @Override // j$.util.function.Consumer
        public /* bridge */ /* synthetic */ void accept(Object obj) {
            Sink.OfDouble.CC.$default$accept(this, obj);
        }

        @Override // j$.util.function.Consumer
        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return consumer.getClass();
        }

        @Override // j$.util.function.DoubleConsumer
        public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
            return doubleConsumer.getClass();
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ boolean cancellationRequested() {
            return Sink.CC.$default$cancellationRequested(this);
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ void end() {
            Sink.CC.$default$end(this);
        }

        C13ReducingSink(Supplier supplier, ObjDoubleConsumer objDoubleConsumer, BinaryOperator binaryOperator) {
            this.val$supplier = supplier;
            this.val$accumulator = objDoubleConsumer;
            this.val$combiner = binaryOperator;
        }

        /* JADX WARN: Type inference failed for: r0v1, types: [U, java.lang.Object] */
        @Override // j$.util.stream.Sink
        public void begin(long size) {
            this.state = this.val$supplier.get();
        }

        @Override // j$.util.stream.Sink, j$.util.function.DoubleConsumer
        public void accept(double t) {
            this.val$accumulator.accept(this.state, t);
        }

        /* JADX WARN: Type inference failed for: r0v1, types: [U, java.lang.Object] */
        public void combine(C13ReducingSink other) {
            this.state = this.val$combiner.apply(this.state, other.state);
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Box<U> {
        U state;

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReduceOps$Box != java.util.stream.ReduceOps$Box<U> */
        Box() {
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReduceOps$Box != java.util.stream.ReduceOps$Box<U> */
        public U get() {
            return this.state;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class ReduceOp<T, R, S extends AccumulatingSink<T, R, S>> implements TerminalOp<T, R> {
        private final StreamShape inputShape;

        @Override // j$.util.stream.TerminalOp
        public /* synthetic */ int getOpFlags() {
            return TerminalOp.CC.$default$getOpFlags(this);
        }

        public abstract S makeSink();

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReduceOps$ReduceOp != java.util.stream.ReduceOps$ReduceOp<T, R, S extends j$.util.stream.ReduceOps$AccumulatingSink<T, R, S>> */
        ReduceOp(StreamShape shape) {
            this.inputShape = shape;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReduceOps$ReduceOp != java.util.stream.ReduceOps$ReduceOp<T, R, S extends j$.util.stream.ReduceOps$AccumulatingSink<T, R, S>> */
        @Override // j$.util.stream.TerminalOp
        public StreamShape inputShape() {
            return this.inputShape;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<T> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReduceOps$ReduceOp != java.util.stream.ReduceOps$ReduceOp<T, R, S extends j$.util.stream.ReduceOps$AccumulatingSink<T, R, S>> */
        @Override // j$.util.stream.TerminalOp
        public <P_IN> R evaluateSequential(PipelineHelper<T> pipelineHelper, Spliterator<P_IN> spliterator) {
            return ((AccumulatingSink) pipelineHelper.wrapAndCopyInto(makeSink(), spliterator)).get();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<T> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReduceOps$ReduceOp != java.util.stream.ReduceOps$ReduceOp<T, R, S extends j$.util.stream.ReduceOps$AccumulatingSink<T, R, S>> */
        @Override // j$.util.stream.TerminalOp
        public <P_IN> R evaluateParallel(PipelineHelper<T> pipelineHelper, Spliterator<P_IN> spliterator) {
            return ((AccumulatingSink) new ReduceTask(this, pipelineHelper, spliterator).invoke()).get();
        }
    }

    /* loaded from: classes2.dex */
    private static final class ReduceTask<P_IN, P_OUT, R, S extends AccumulatingSink<P_OUT, R, S>> extends AbstractTask<P_IN, P_OUT, S, ReduceTask<P_IN, P_OUT, R, S>> {
        private final ReduceOp<P_OUT, R, S> op;

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<P_OUT> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReduceOps$ReduceOp != java.util.stream.ReduceOps$ReduceOp<P_OUT, R, S extends j$.util.stream.ReduceOps$AccumulatingSink<P_OUT, R, S>> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReduceOps$ReduceTask != java.util.stream.ReduceOps$ReduceTask<P_IN, P_OUT, R, S extends j$.util.stream.ReduceOps$AccumulatingSink<P_OUT, R, S>> */
        ReduceTask(ReduceOp<P_OUT, R, S> reduceOp, PipelineHelper<P_OUT> pipelineHelper, Spliterator<P_IN> spliterator) {
            super(pipelineHelper, spliterator);
            this.op = reduceOp;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReduceOps$ReduceTask != java.util.stream.ReduceOps$ReduceTask<P_IN, P_OUT, R, S extends j$.util.stream.ReduceOps$AccumulatingSink<P_OUT, R, S>> */
        ReduceTask(ReduceTask<P_IN, P_OUT, R, S> reduceTask, Spliterator<P_IN> spliterator) {
            super(reduceTask, spliterator);
            this.op = reduceTask.op;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReduceOps$ReduceTask != java.util.stream.ReduceOps$ReduceTask<P_IN, P_OUT, R, S extends j$.util.stream.ReduceOps$AccumulatingSink<P_OUT, R, S>> */
        @Override // j$.util.stream.AbstractTask
        public ReduceTask<P_IN, P_OUT, R, S> makeChild(Spliterator<P_IN> spliterator) {
            return new ReduceTask<>(this, spliterator);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReduceOps$ReduceTask != java.util.stream.ReduceOps$ReduceTask<P_IN, P_OUT, R, S extends j$.util.stream.ReduceOps$AccumulatingSink<P_OUT, R, S>> */
        @Override // j$.util.stream.AbstractTask
        public S doLeaf() {
            return (S) this.helper.wrapAndCopyInto(this.op.makeSink(), this.spliterator);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReduceOps$ReduceTask != java.util.stream.ReduceOps$ReduceTask<P_IN, P_OUT, R, S extends j$.util.stream.ReduceOps$AccumulatingSink<P_OUT, R, S>> */
        @Override // j$.util.stream.AbstractTask, java.util.concurrent.CountedCompleter
        public void onCompletion(CountedCompleter<?> caller) {
            if (!isLeaf()) {
                AccumulatingSink accumulatingSink = (AccumulatingSink) ((ReduceTask) this.leftChild).getLocalResult();
                accumulatingSink.combine((AccumulatingSink) ((ReduceTask) this.rightChild).getLocalResult());
                setLocalResult(accumulatingSink);
            }
            super.onCompletion(caller);
        }
    }
}
