package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.DoubleConsumer;
import j$.util.function.IntConsumer;
import j$.util.function.LongConsumer;
/* loaded from: classes2.dex */
public interface Sink<T> extends Consumer<T> {
    void accept(double d);

    void accept(int i);

    void accept(long j);

    void begin(long j);

    boolean cancellationRequested();

    void end();

    /* renamed from: j$.util.stream.Sink$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static void $default$begin(Sink _this, long size) {
        }

        public static void $default$end(Sink _this) {
        }

        public static boolean $default$cancellationRequested(Sink _this) {
            return false;
        }

        public static void $default$accept(Sink _this, int value) {
            throw new IllegalStateException("called wrong accept method");
        }

        public static void $default$accept(Sink _this, long value) {
            throw new IllegalStateException("called wrong accept method");
        }

        public static void $default$accept(Sink _this, double value) {
            throw new IllegalStateException("called wrong accept method");
        }
    }

    /* loaded from: classes2.dex */
    public interface OfInt extends Sink<Integer>, IntConsumer {
        @Override // j$.util.stream.Sink
        void accept(int i);

        void accept(Integer num);

        /* renamed from: j$.util.stream.Sink$OfInt$-CC */
        /* loaded from: classes2.dex */
        public final /* synthetic */ class CC {
            public static /* bridge */ /* synthetic */ void $default$accept(OfInt _this, Object obj) {
                _this.accept((Integer) obj);
            }

            public static void $default$accept(OfInt _this, Integer i) {
                if (Tripwire.ENABLED) {
                    Tripwire.trip(_this.getClass(), "{0} calling Sink.OfInt.accept(Integer)");
                }
                _this.accept(i.intValue());
            }
        }
    }

    /* loaded from: classes2.dex */
    public interface OfLong extends Sink<Long>, LongConsumer {
        @Override // j$.util.stream.Sink
        void accept(long j);

        void accept(Long l);

        /* renamed from: j$.util.stream.Sink$OfLong$-CC */
        /* loaded from: classes2.dex */
        public final /* synthetic */ class CC {
            public static /* bridge */ /* synthetic */ void $default$accept(OfLong _this, Object obj) {
                _this.accept((Long) obj);
            }

            public static void $default$accept(OfLong _this, Long i) {
                if (Tripwire.ENABLED) {
                    Tripwire.trip(_this.getClass(), "{0} calling Sink.OfLong.accept(Long)");
                }
                _this.accept(i.longValue());
            }
        }
    }

    /* loaded from: classes2.dex */
    public interface OfDouble extends Sink<Double>, DoubleConsumer {
        @Override // j$.util.stream.Sink, j$.util.function.DoubleConsumer
        void accept(double d);

        void accept(Double d);

        /* renamed from: j$.util.stream.Sink$OfDouble$-CC */
        /* loaded from: classes2.dex */
        public final /* synthetic */ class CC {
            public static /* bridge */ /* synthetic */ void $default$accept(OfDouble _this, Object obj) {
                _this.accept((Double) obj);
            }

            public static void $default$accept(OfDouble _this, Double i) {
                if (Tripwire.ENABLED) {
                    Tripwire.trip(_this.getClass(), "{0} calling Sink.OfDouble.accept(Double)");
                }
                _this.accept(i.doubleValue());
            }
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class ChainedReference<T, E_OUT> implements Sink<T> {
        protected final Sink<? super E_OUT> downstream;

        @Override // j$.util.stream.Sink, j$.util.function.DoubleConsumer
        public /* synthetic */ void accept(double d) {
            CC.$default$accept(this, d);
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ void accept(int i) {
            CC.$default$accept((Sink) this, i);
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ void accept(long j) {
            CC.$default$accept((Sink) this, j);
        }

        @Override // j$.util.function.Consumer
        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return consumer.getClass();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<? super E_OUT> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink$ChainedReference != java.util.stream.Sink$ChainedReference<T, E_OUT> */
        public ChainedReference(Sink<? super E_OUT> sink) {
            sink.getClass();
            this.downstream = sink;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink$ChainedReference != java.util.stream.Sink$ChainedReference<T, E_OUT> */
        @Override // j$.util.stream.Sink
        public void begin(long size) {
            this.downstream.begin(size);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink$ChainedReference != java.util.stream.Sink$ChainedReference<T, E_OUT> */
        @Override // j$.util.stream.Sink
        public void end() {
            this.downstream.end();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink$ChainedReference != java.util.stream.Sink$ChainedReference<T, E_OUT> */
        @Override // j$.util.stream.Sink
        public boolean cancellationRequested() {
            return this.downstream.cancellationRequested();
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class ChainedInt<E_OUT> implements OfInt {
        protected final Sink<? super E_OUT> downstream;

        @Override // j$.util.stream.Sink, j$.util.function.DoubleConsumer
        public /* synthetic */ void accept(double d) {
            CC.$default$accept(this, d);
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ void accept(long j) {
            CC.$default$accept((Sink) this, j);
        }

        @Override // j$.util.stream.Sink.OfInt
        public /* synthetic */ void accept(Integer num) {
            OfInt.CC.$default$accept((OfInt) this, num);
        }

        @Override // j$.util.function.Consumer
        public /* bridge */ /* synthetic */ void accept(Object obj) {
            OfInt.CC.$default$accept(this, obj);
        }

        @Override // j$.util.function.Consumer
        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return consumer.getClass();
        }

        @Override // j$.util.function.IntConsumer
        public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
            return intConsumer.getClass();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<? super E_OUT> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink$ChainedInt != java.util.stream.Sink$ChainedInt<E_OUT> */
        public ChainedInt(Sink<? super E_OUT> sink) {
            sink.getClass();
            this.downstream = sink;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink$ChainedInt != java.util.stream.Sink$ChainedInt<E_OUT> */
        @Override // j$.util.stream.Sink
        public void begin(long size) {
            this.downstream.begin(size);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink$ChainedInt != java.util.stream.Sink$ChainedInt<E_OUT> */
        @Override // j$.util.stream.Sink
        public void end() {
            this.downstream.end();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink$ChainedInt != java.util.stream.Sink$ChainedInt<E_OUT> */
        @Override // j$.util.stream.Sink
        public boolean cancellationRequested() {
            return this.downstream.cancellationRequested();
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class ChainedLong<E_OUT> implements OfLong {
        protected final Sink<? super E_OUT> downstream;

        @Override // j$.util.stream.Sink, j$.util.function.DoubleConsumer
        public /* synthetic */ void accept(double d) {
            CC.$default$accept(this, d);
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ void accept(int i) {
            CC.$default$accept((Sink) this, i);
        }

        @Override // j$.util.stream.Sink.OfLong
        public /* synthetic */ void accept(Long l) {
            OfLong.CC.$default$accept((OfLong) this, l);
        }

        @Override // j$.util.function.Consumer
        public /* bridge */ /* synthetic */ void accept(Object obj) {
            OfLong.CC.$default$accept(this, obj);
        }

        @Override // j$.util.function.Consumer
        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return consumer.getClass();
        }

        @Override // j$.util.function.LongConsumer
        public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
            return longConsumer.getClass();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<? super E_OUT> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink$ChainedLong != java.util.stream.Sink$ChainedLong<E_OUT> */
        public ChainedLong(Sink<? super E_OUT> sink) {
            sink.getClass();
            this.downstream = sink;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink$ChainedLong != java.util.stream.Sink$ChainedLong<E_OUT> */
        @Override // j$.util.stream.Sink
        public void begin(long size) {
            this.downstream.begin(size);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink$ChainedLong != java.util.stream.Sink$ChainedLong<E_OUT> */
        @Override // j$.util.stream.Sink
        public void end() {
            this.downstream.end();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink$ChainedLong != java.util.stream.Sink$ChainedLong<E_OUT> */
        @Override // j$.util.stream.Sink
        public boolean cancellationRequested() {
            return this.downstream.cancellationRequested();
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class ChainedDouble<E_OUT> implements OfDouble {
        protected final Sink<? super E_OUT> downstream;

        @Override // j$.util.stream.Sink
        public /* synthetic */ void accept(int i) {
            CC.$default$accept((Sink) this, i);
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ void accept(long j) {
            CC.$default$accept((Sink) this, j);
        }

        @Override // j$.util.stream.Sink.OfDouble
        public /* synthetic */ void accept(Double d) {
            OfDouble.CC.$default$accept((OfDouble) this, d);
        }

        @Override // j$.util.function.Consumer
        public /* bridge */ /* synthetic */ void accept(Object obj) {
            OfDouble.CC.$default$accept(this, obj);
        }

        @Override // j$.util.function.Consumer
        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return consumer.getClass();
        }

        @Override // j$.util.function.DoubleConsumer
        public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
            return doubleConsumer.getClass();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<? super E_OUT> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink$ChainedDouble != java.util.stream.Sink$ChainedDouble<E_OUT> */
        public ChainedDouble(Sink<? super E_OUT> sink) {
            sink.getClass();
            this.downstream = sink;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink$ChainedDouble != java.util.stream.Sink$ChainedDouble<E_OUT> */
        @Override // j$.util.stream.Sink
        public void begin(long size) {
            this.downstream.begin(size);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink$ChainedDouble != java.util.stream.Sink$ChainedDouble<E_OUT> */
        @Override // j$.util.stream.Sink
        public void end() {
            this.downstream.end();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink$ChainedDouble != java.util.stream.Sink$ChainedDouble<E_OUT> */
        @Override // j$.util.stream.Sink
        public boolean cancellationRequested() {
            return this.downstream.cancellationRequested();
        }
    }
}
