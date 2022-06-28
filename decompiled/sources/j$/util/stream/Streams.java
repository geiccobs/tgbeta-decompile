package j$.util.stream;

import j$.lang.Iterable;
import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.DoubleConsumer;
import j$.util.function.IntConsumer;
import j$.util.function.LongConsumer;
import j$.util.stream.DoubleStream;
import j$.util.stream.IntStream;
import j$.util.stream.LongStream;
import j$.util.stream.SpinedBuffer;
import j$.util.stream.Stream;
import java.util.Comparator;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class Streams {
    static final Object NONE = new Object();

    private Streams() {
        throw new Error("no instances");
    }

    /* loaded from: classes2.dex */
    public static final class RangeIntSpliterator implements Spliterator.OfInt {
        private static final int BALANCED_SPLIT_THRESHOLD = 16777216;
        private static final int RIGHT_BALANCED_SPLIT_RATIO = 8;
        private int from;
        private int last;
        private final int upTo;

        @Override // j$.util.Spliterator.OfInt, j$.util.Spliterator
        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Spliterator.OfInt.CC.$default$forEachRemaining((Spliterator.OfInt) this, consumer);
        }

        @Override // j$.util.Spliterator
        public /* synthetic */ long getExactSizeIfKnown() {
            return Spliterator.CC.$default$getExactSizeIfKnown(this);
        }

        @Override // j$.util.Spliterator
        public /* synthetic */ boolean hasCharacteristics(int i) {
            return Spliterator.CC.$default$hasCharacteristics(this, i);
        }

        @Override // j$.util.Spliterator.OfInt, j$.util.Spliterator
        public /* synthetic */ boolean tryAdvance(Consumer consumer) {
            return Spliterator.OfInt.CC.$default$tryAdvance((Spliterator.OfInt) this, consumer);
        }

        public RangeIntSpliterator(int from, int upTo, boolean closed) {
            this(from, upTo, closed ? 1 : 0);
        }

        private RangeIntSpliterator(int from, int upTo, int last) {
            this.from = from;
            this.upTo = upTo;
            this.last = last;
        }

        @Override // j$.util.Spliterator.OfInt
        public boolean tryAdvance(IntConsumer consumer) {
            consumer.getClass();
            int i = this.from;
            if (i < this.upTo) {
                this.from++;
                consumer.accept(i);
                return true;
            } else if (this.last <= 0) {
                return false;
            } else {
                this.last = 0;
                consumer.accept(i);
                return true;
            }
        }

        @Override // j$.util.Spliterator.OfInt
        public void forEachRemaining(IntConsumer consumer) {
            consumer.getClass();
            int i = this.from;
            int hUpTo = this.upTo;
            int hLast = this.last;
            this.from = this.upTo;
            this.last = 0;
            while (i < hUpTo) {
                consumer.accept(i);
                i++;
            }
            if (hLast > 0) {
                consumer.accept(i);
            }
        }

        @Override // j$.util.Spliterator
        public long estimateSize() {
            return (this.upTo - this.from) + this.last;
        }

        @Override // j$.util.Spliterator
        public int characteristics() {
            return 17749;
        }

        @Override // j$.util.Spliterator
        public Comparator<? super Integer> getComparator() {
            return null;
        }

        @Override // j$.util.Spliterator.OfInt, j$.util.Spliterator.OfPrimitive, j$.util.Spliterator
        public Spliterator.OfInt trySplit() {
            long size = estimateSize();
            if (size <= 1) {
                return null;
            }
            int i = this.from;
            int splitPoint = splitPoint(size) + i;
            this.from = splitPoint;
            return new RangeIntSpliterator(i, splitPoint, 0);
        }

        private int splitPoint(long size) {
            int d = size < 16777216 ? 2 : 8;
            return (int) (size / d);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static final class RangeLongSpliterator implements Spliterator.OfLong {
        static final /* synthetic */ boolean $assertionsDisabled = true;
        private static final long BALANCED_SPLIT_THRESHOLD = 16777216;
        private static final long RIGHT_BALANCED_SPLIT_RATIO = 8;
        private long from;
        private int last;
        private final long upTo;

        @Override // j$.util.Spliterator.OfLong, j$.util.Spliterator
        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Spliterator.OfLong.CC.$default$forEachRemaining((Spliterator.OfLong) this, consumer);
        }

        @Override // j$.util.Spliterator
        public /* synthetic */ long getExactSizeIfKnown() {
            return Spliterator.CC.$default$getExactSizeIfKnown(this);
        }

        @Override // j$.util.Spliterator
        public /* synthetic */ boolean hasCharacteristics(int i) {
            return Spliterator.CC.$default$hasCharacteristics(this, i);
        }

        @Override // j$.util.Spliterator.OfLong, j$.util.Spliterator
        public /* synthetic */ boolean tryAdvance(Consumer consumer) {
            return Spliterator.OfLong.CC.$default$tryAdvance((Spliterator.OfLong) this, consumer);
        }

        public RangeLongSpliterator(long from, long upTo, boolean closed) {
            this(from, upTo, closed ? 1 : 0);
        }

        private RangeLongSpliterator(long from, long upTo, int last) {
            if ($assertionsDisabled || (upTo - from) + last > 0) {
                this.from = from;
                this.upTo = upTo;
                this.last = last;
                return;
            }
            throw new AssertionError();
        }

        @Override // j$.util.Spliterator.OfLong
        public boolean tryAdvance(LongConsumer consumer) {
            consumer.getClass();
            long i = this.from;
            if (i < this.upTo) {
                this.from++;
                consumer.accept(i);
                return true;
            } else if (this.last <= 0) {
                return false;
            } else {
                this.last = 0;
                consumer.accept(i);
                return true;
            }
        }

        @Override // j$.util.Spliterator.OfLong
        public void forEachRemaining(LongConsumer consumer) {
            consumer.getClass();
            long i = this.from;
            long hUpTo = this.upTo;
            int hLast = this.last;
            this.from = this.upTo;
            this.last = 0;
            while (i < hUpTo) {
                consumer.accept(i);
                i = 1 + i;
            }
            if (hLast > 0) {
                consumer.accept(i);
            }
        }

        @Override // j$.util.Spliterator
        public long estimateSize() {
            return (this.upTo - this.from) + this.last;
        }

        @Override // j$.util.Spliterator
        public int characteristics() {
            return 17749;
        }

        @Override // j$.util.Spliterator
        public Comparator<? super Long> getComparator() {
            return null;
        }

        @Override // j$.util.Spliterator.OfLong, j$.util.Spliterator.OfPrimitive, j$.util.Spliterator
        public Spliterator.OfLong trySplit() {
            long size = estimateSize();
            if (size <= 1) {
                return null;
            }
            long j = this.from;
            long splitPoint = splitPoint(size) + j;
            this.from = splitPoint;
            return new RangeLongSpliterator(j, splitPoint, 0);
        }

        private long splitPoint(long size) {
            long d = size < BALANCED_SPLIT_THRESHOLD ? 2L : RIGHT_BALANCED_SPLIT_RATIO;
            return size / d;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class AbstractStreamBuilderImpl<T, S extends Spliterator<T>> implements Spliterator<T> {
        int count;

        @Override // j$.util.Spliterator
        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Spliterator.CC.$default$forEachRemaining(this, consumer);
        }

        @Override // j$.util.Spliterator
        public /* synthetic */ Comparator getComparator() {
            return Spliterator.CC.$default$getComparator(this);
        }

        @Override // j$.util.Spliterator
        public /* synthetic */ long getExactSizeIfKnown() {
            return Spliterator.CC.$default$getExactSizeIfKnown(this);
        }

        @Override // j$.util.Spliterator
        public /* synthetic */ boolean hasCharacteristics(int i) {
            return Spliterator.CC.$default$hasCharacteristics(this, i);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Streams$AbstractStreamBuilderImpl != java.util.stream.Streams$AbstractStreamBuilderImpl<T, S extends j$.util.Spliterator<T>> */
        private AbstractStreamBuilderImpl() {
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Streams$AbstractStreamBuilderImpl != java.util.stream.Streams$AbstractStreamBuilderImpl<T, S extends j$.util.Spliterator<T>> */
        @Override // j$.util.Spliterator
        public S trySplit() {
            return null;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Streams$AbstractStreamBuilderImpl != java.util.stream.Streams$AbstractStreamBuilderImpl<T, S extends j$.util.Spliterator<T>> */
        @Override // j$.util.Spliterator
        public long estimateSize() {
            return (-this.count) - 1;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Streams$AbstractStreamBuilderImpl != java.util.stream.Streams$AbstractStreamBuilderImpl<T, S extends j$.util.Spliterator<T>> */
        @Override // j$.util.Spliterator
        public int characteristics() {
            return 17488;
        }
    }

    /* loaded from: classes2.dex */
    static final class StreamBuilderImpl<T> extends AbstractStreamBuilderImpl<T, Spliterator<T>> implements Stream.Builder<T> {
        SpinedBuffer<T> buffer;
        T first;

        @Override // j$.util.function.Consumer
        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return consumer.getClass();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Streams$StreamBuilderImpl != java.util.stream.Streams$StreamBuilderImpl<T> */
        public StreamBuilderImpl() {
            super();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Streams$StreamBuilderImpl != java.util.stream.Streams$StreamBuilderImpl<T> */
        public StreamBuilderImpl(T t) {
            super();
            this.first = t;
            this.count = -2;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Streams$StreamBuilderImpl != java.util.stream.Streams$StreamBuilderImpl<T> */
        @Override // j$.util.stream.Stream.Builder, j$.util.function.Consumer
        public void accept(T t) {
            if (this.count == 0) {
                this.first = t;
                this.count++;
            } else if (this.count > 0) {
                if (this.buffer == null) {
                    SpinedBuffer<T> spinedBuffer = new SpinedBuffer<>();
                    this.buffer = spinedBuffer;
                    spinedBuffer.accept(this.first);
                    this.count++;
                }
                this.buffer.accept(t);
            } else {
                throw new IllegalStateException();
            }
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Streams$StreamBuilderImpl != java.util.stream.Streams$StreamBuilderImpl<T> */
        @Override // j$.util.stream.Stream.Builder
        public Stream.Builder<T> add(T t) {
            accept(t);
            return this;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Streams$StreamBuilderImpl != java.util.stream.Streams$StreamBuilderImpl<T> */
        @Override // j$.util.stream.Stream.Builder
        public Stream<T> build() {
            int c = this.count;
            if (c >= 0) {
                this.count = (-this.count) - 1;
                return c < 2 ? StreamSupport.stream(this, false) : StreamSupport.stream(Iterable.EL.spliterator(this.buffer), false);
            }
            throw new IllegalStateException();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<? super T> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Streams$StreamBuilderImpl != java.util.stream.Streams$StreamBuilderImpl<T> */
        @Override // j$.util.Spliterator
        public boolean tryAdvance(Consumer<? super T> consumer) {
            consumer.getClass();
            if (this.count == -2) {
                consumer.accept((T) this.first);
                this.count = -1;
                return true;
            }
            return false;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<? super T> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Streams$StreamBuilderImpl != java.util.stream.Streams$StreamBuilderImpl<T> */
        @Override // j$.util.stream.Streams.AbstractStreamBuilderImpl, j$.util.Spliterator
        public void forEachRemaining(Consumer<? super T> consumer) {
            consumer.getClass();
            if (this.count == -2) {
                consumer.accept((T) this.first);
                this.count = -1;
            }
        }
    }

    /* loaded from: classes2.dex */
    static final class IntStreamBuilderImpl extends AbstractStreamBuilderImpl<Integer, Spliterator.OfInt> implements IntStream.Builder, Spliterator.OfInt {
        SpinedBuffer.OfInt buffer;
        int first;

        @Override // j$.util.stream.IntStream.Builder
        public /* synthetic */ IntStream.Builder add(int i) {
            return accept(i);
        }

        @Override // j$.util.function.IntConsumer
        public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
            return intConsumer.getClass();
        }

        @Override // j$.util.stream.Streams.AbstractStreamBuilderImpl, j$.util.Spliterator
        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Spliterator.OfInt.CC.$default$forEachRemaining((Spliterator.OfInt) this, consumer);
        }

        @Override // j$.util.Spliterator
        public /* synthetic */ boolean tryAdvance(Consumer consumer) {
            return Spliterator.OfInt.CC.$default$tryAdvance((Spliterator.OfInt) this, consumer);
        }

        public IntStreamBuilderImpl() {
            super();
        }

        public IntStreamBuilderImpl(int t) {
            super();
            this.first = t;
            this.count = -2;
        }

        @Override // j$.util.stream.IntStream.Builder, j$.util.function.IntConsumer
        public void accept(int t) {
            if (this.count == 0) {
                this.first = t;
                this.count++;
            } else if (this.count > 0) {
                if (this.buffer == null) {
                    SpinedBuffer.OfInt ofInt = new SpinedBuffer.OfInt();
                    this.buffer = ofInt;
                    ofInt.accept(this.first);
                    this.count++;
                }
                this.buffer.accept(t);
            } else {
                throw new IllegalStateException();
            }
        }

        @Override // j$.util.stream.IntStream.Builder
        public IntStream build() {
            int c = this.count;
            if (c >= 0) {
                this.count = (-this.count) - 1;
                return c < 2 ? StreamSupport.intStream(this, false) : StreamSupport.intStream(this.buffer.spliterator(), false);
            }
            throw new IllegalStateException();
        }

        @Override // j$.util.Spliterator.OfInt
        public boolean tryAdvance(IntConsumer action) {
            action.getClass();
            if (this.count == -2) {
                action.accept(this.first);
                this.count = -1;
                return true;
            }
            return false;
        }

        @Override // j$.util.Spliterator.OfInt
        public void forEachRemaining(IntConsumer action) {
            action.getClass();
            if (this.count == -2) {
                action.accept(this.first);
                this.count = -1;
            }
        }
    }

    /* loaded from: classes2.dex */
    static final class LongStreamBuilderImpl extends AbstractStreamBuilderImpl<Long, Spliterator.OfLong> implements LongStream.Builder, Spliterator.OfLong {
        SpinedBuffer.OfLong buffer;
        long first;

        @Override // j$.util.stream.LongStream.Builder
        public /* synthetic */ LongStream.Builder add(long j) {
            return accept(j);
        }

        @Override // j$.util.function.LongConsumer
        public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
            return longConsumer.getClass();
        }

        @Override // j$.util.stream.Streams.AbstractStreamBuilderImpl, j$.util.Spliterator
        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Spliterator.OfLong.CC.$default$forEachRemaining((Spliterator.OfLong) this, consumer);
        }

        @Override // j$.util.Spliterator
        public /* synthetic */ boolean tryAdvance(Consumer consumer) {
            return Spliterator.OfLong.CC.$default$tryAdvance((Spliterator.OfLong) this, consumer);
        }

        public LongStreamBuilderImpl() {
            super();
        }

        public LongStreamBuilderImpl(long t) {
            super();
            this.first = t;
            this.count = -2;
        }

        @Override // j$.util.stream.LongStream.Builder, j$.util.function.LongConsumer
        public void accept(long t) {
            if (this.count == 0) {
                this.first = t;
                this.count++;
            } else if (this.count > 0) {
                if (this.buffer == null) {
                    SpinedBuffer.OfLong ofLong = new SpinedBuffer.OfLong();
                    this.buffer = ofLong;
                    ofLong.accept(this.first);
                    this.count++;
                }
                this.buffer.accept(t);
            } else {
                throw new IllegalStateException();
            }
        }

        @Override // j$.util.stream.LongStream.Builder
        public LongStream build() {
            int c = this.count;
            if (c >= 0) {
                this.count = (-this.count) - 1;
                return c < 2 ? StreamSupport.longStream(this, false) : StreamSupport.longStream(this.buffer.spliterator(), false);
            }
            throw new IllegalStateException();
        }

        @Override // j$.util.Spliterator.OfLong
        public boolean tryAdvance(LongConsumer action) {
            action.getClass();
            if (this.count == -2) {
                action.accept(this.first);
                this.count = -1;
                return true;
            }
            return false;
        }

        @Override // j$.util.Spliterator.OfLong
        public void forEachRemaining(LongConsumer action) {
            action.getClass();
            if (this.count == -2) {
                action.accept(this.first);
                this.count = -1;
            }
        }
    }

    /* loaded from: classes2.dex */
    static final class DoubleStreamBuilderImpl extends AbstractStreamBuilderImpl<Double, Spliterator.OfDouble> implements DoubleStream.Builder, Spliterator.OfDouble {
        SpinedBuffer.OfDouble buffer;
        double first;

        @Override // j$.util.stream.DoubleStream.Builder
        public /* synthetic */ DoubleStream.Builder add(double d) {
            return accept(d);
        }

        @Override // j$.util.function.DoubleConsumer
        public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
            return doubleConsumer.getClass();
        }

        @Override // j$.util.stream.Streams.AbstractStreamBuilderImpl, j$.util.Spliterator
        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Spliterator.OfDouble.CC.$default$forEachRemaining((Spliterator.OfDouble) this, consumer);
        }

        @Override // j$.util.Spliterator
        public /* synthetic */ boolean tryAdvance(Consumer consumer) {
            return Spliterator.OfDouble.CC.$default$tryAdvance((Spliterator.OfDouble) this, consumer);
        }

        public DoubleStreamBuilderImpl() {
            super();
        }

        public DoubleStreamBuilderImpl(double t) {
            super();
            this.first = t;
            this.count = -2;
        }

        @Override // j$.util.stream.DoubleStream.Builder, j$.util.function.DoubleConsumer
        public void accept(double t) {
            if (this.count == 0) {
                this.first = t;
                this.count++;
            } else if (this.count > 0) {
                if (this.buffer == null) {
                    SpinedBuffer.OfDouble ofDouble = new SpinedBuffer.OfDouble();
                    this.buffer = ofDouble;
                    ofDouble.accept(this.first);
                    this.count++;
                }
                this.buffer.accept(t);
            } else {
                throw new IllegalStateException();
            }
        }

        @Override // j$.util.stream.DoubleStream.Builder
        public DoubleStream build() {
            int c = this.count;
            if (c >= 0) {
                this.count = (-this.count) - 1;
                return c < 2 ? StreamSupport.doubleStream(this, false) : StreamSupport.doubleStream(this.buffer.spliterator(), false);
            }
            throw new IllegalStateException();
        }

        @Override // j$.util.Spliterator.OfDouble
        public boolean tryAdvance(DoubleConsumer action) {
            action.getClass();
            if (this.count == -2) {
                action.accept(this.first);
                this.count = -1;
                return true;
            }
            return false;
        }

        @Override // j$.util.Spliterator.OfDouble
        public void forEachRemaining(DoubleConsumer action) {
            action.getClass();
            if (this.count == -2) {
                action.accept(this.first);
                this.count = -1;
            }
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class ConcatSpliterator<T, T_SPLITR extends Spliterator<T>> implements Spliterator<T> {
        protected final T_SPLITR aSpliterator;
        protected final T_SPLITR bSpliterator;
        boolean beforeSplit = true;
        final boolean unsized;

        @Override // j$.util.Spliterator
        public /* synthetic */ long getExactSizeIfKnown() {
            return Spliterator.CC.$default$getExactSizeIfKnown(this);
        }

        @Override // j$.util.Spliterator
        public /* synthetic */ boolean hasCharacteristics(int i) {
            return Spliterator.CC.$default$hasCharacteristics(this, i);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Streams$ConcatSpliterator != java.util.stream.Streams$ConcatSpliterator<T, T_SPLITR extends j$.util.Spliterator<T>> */
        public ConcatSpliterator(T_SPLITR aSpliterator, T_SPLITR bSpliterator) {
            this.aSpliterator = aSpliterator;
            this.bSpliterator = bSpliterator;
            boolean z = true;
            this.unsized = aSpliterator.estimateSize() + bSpliterator.estimateSize() >= 0 ? false : z;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Streams$ConcatSpliterator != java.util.stream.Streams$ConcatSpliterator<T, T_SPLITR extends j$.util.Spliterator<T>> */
        @Override // j$.util.Spliterator
        public T_SPLITR trySplit() {
            T_SPLITR ret = this.beforeSplit ? this.aSpliterator : (T_SPLITR) this.bSpliterator.trySplit();
            this.beforeSplit = false;
            return ret;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<? super T> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Streams$ConcatSpliterator != java.util.stream.Streams$ConcatSpliterator<T, T_SPLITR extends j$.util.Spliterator<T>> */
        @Override // j$.util.Spliterator
        public boolean tryAdvance(Consumer<? super T> consumer) {
            if (this.beforeSplit) {
                boolean hasNext = this.aSpliterator.tryAdvance(consumer);
                if (!hasNext) {
                    this.beforeSplit = false;
                    return this.bSpliterator.tryAdvance(consumer);
                }
                return hasNext;
            }
            return this.bSpliterator.tryAdvance(consumer);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<? super T> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Streams$ConcatSpliterator != java.util.stream.Streams$ConcatSpliterator<T, T_SPLITR extends j$.util.Spliterator<T>> */
        @Override // j$.util.Spliterator
        public void forEachRemaining(Consumer<? super T> consumer) {
            if (this.beforeSplit) {
                this.aSpliterator.forEachRemaining(consumer);
            }
            this.bSpliterator.forEachRemaining(consumer);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Streams$ConcatSpliterator != java.util.stream.Streams$ConcatSpliterator<T, T_SPLITR extends j$.util.Spliterator<T>> */
        @Override // j$.util.Spliterator
        public long estimateSize() {
            if (this.beforeSplit) {
                long size = this.aSpliterator.estimateSize() + this.bSpliterator.estimateSize();
                if (size < 0) {
                    return Long.MAX_VALUE;
                }
                return size;
            }
            return this.bSpliterator.estimateSize();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Streams$ConcatSpliterator != java.util.stream.Streams$ConcatSpliterator<T, T_SPLITR extends j$.util.Spliterator<T>> */
        @Override // j$.util.Spliterator
        public int characteristics() {
            if (this.beforeSplit) {
                return this.aSpliterator.characteristics() & this.bSpliterator.characteristics() & (((this.unsized ? 16448 : 0) | 5) ^ (-1));
            }
            return this.bSpliterator.characteristics();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Streams$ConcatSpliterator != java.util.stream.Streams$ConcatSpliterator<T, T_SPLITR extends j$.util.Spliterator<T>> */
        @Override // j$.util.Spliterator
        public Comparator<? super T> getComparator() {
            if (this.beforeSplit) {
                throw new IllegalStateException();
            }
            return this.bSpliterator.getComparator();
        }

        /* loaded from: classes2.dex */
        static class OfRef<T> extends ConcatSpliterator<T, Spliterator<T>> {
            /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<T> */
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Streams$ConcatSpliterator$OfRef != java.util.stream.Streams$ConcatSpliterator$OfRef<T> */
            public OfRef(Spliterator<T> spliterator, Spliterator<T> spliterator2) {
                super(spliterator, spliterator2);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static abstract class OfPrimitive<T, T_CONS, T_SPLITR extends Spliterator.OfPrimitive<T, T_CONS, T_SPLITR>> extends ConcatSpliterator<T, T_SPLITR> implements Spliterator.OfPrimitive<T, T_CONS, T_SPLITR> {
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Streams$ConcatSpliterator$OfPrimitive != java.util.stream.Streams$ConcatSpliterator$OfPrimitive<T, T_CONS, T_SPLITR extends j$.util.Spliterator$OfPrimitive<T, T_CONS, T_SPLITR>> */
            @Override // j$.util.stream.Streams.ConcatSpliterator, j$.util.Spliterator
            public /* bridge */ /* synthetic */ Spliterator.OfPrimitive trySplit() {
                return (Spliterator.OfPrimitive) super.trySplit();
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Streams$ConcatSpliterator$OfPrimitive != java.util.stream.Streams$ConcatSpliterator$OfPrimitive<T, T_CONS, T_SPLITR extends j$.util.Spliterator$OfPrimitive<T, T_CONS, T_SPLITR>> */
            private OfPrimitive(T_SPLITR aSpliterator, T_SPLITR bSpliterator) {
                super(aSpliterator, bSpliterator);
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Streams$ConcatSpliterator$OfPrimitive != java.util.stream.Streams$ConcatSpliterator$OfPrimitive<T, T_CONS, T_SPLITR extends j$.util.Spliterator$OfPrimitive<T, T_CONS, T_SPLITR>> */
            @Override // j$.util.Spliterator.OfPrimitive
            public boolean tryAdvance(T_CONS action) {
                if (this.beforeSplit) {
                    boolean hasNext = ((Spliterator.OfPrimitive) this.aSpliterator).tryAdvance((Spliterator.OfPrimitive) action);
                    if (!hasNext) {
                        this.beforeSplit = false;
                        return ((Spliterator.OfPrimitive) this.bSpliterator).tryAdvance((Spliterator.OfPrimitive) action);
                    }
                    return hasNext;
                }
                return ((Spliterator.OfPrimitive) this.bSpliterator).tryAdvance((Spliterator.OfPrimitive) action);
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Streams$ConcatSpliterator$OfPrimitive != java.util.stream.Streams$ConcatSpliterator$OfPrimitive<T, T_CONS, T_SPLITR extends j$.util.Spliterator$OfPrimitive<T, T_CONS, T_SPLITR>> */
            @Override // j$.util.Spliterator.OfPrimitive
            public void forEachRemaining(T_CONS action) {
                if (this.beforeSplit) {
                    ((Spliterator.OfPrimitive) this.aSpliterator).forEachRemaining((Spliterator.OfPrimitive) action);
                }
                ((Spliterator.OfPrimitive) this.bSpliterator).forEachRemaining((Spliterator.OfPrimitive) action);
            }
        }

        /* loaded from: classes2.dex */
        static class OfInt extends OfPrimitive<Integer, IntConsumer, Spliterator.OfInt> implements Spliterator.OfInt {
            @Override // j$.util.Spliterator.OfInt
            public /* bridge */ /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
                super.forEachRemaining((OfInt) intConsumer);
            }

            @Override // j$.util.Spliterator.OfInt
            public /* bridge */ /* synthetic */ boolean tryAdvance(IntConsumer intConsumer) {
                return super.tryAdvance((OfInt) intConsumer);
            }

            @Override // j$.util.stream.Streams.ConcatSpliterator.OfPrimitive, j$.util.stream.Streams.ConcatSpliterator, j$.util.Spliterator
            public /* bridge */ /* synthetic */ Spliterator.OfInt trySplit() {
                return (Spliterator.OfInt) super.trySplit();
            }

            public OfInt(Spliterator.OfInt aSpliterator, Spliterator.OfInt bSpliterator) {
                super(aSpliterator, bSpliterator);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes2.dex */
        public static class OfLong extends OfPrimitive<Long, LongConsumer, Spliterator.OfLong> implements Spliterator.OfLong {
            @Override // j$.util.Spliterator.OfLong
            public /* bridge */ /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
                super.forEachRemaining((OfLong) longConsumer);
            }

            @Override // j$.util.Spliterator.OfLong
            public /* bridge */ /* synthetic */ boolean tryAdvance(LongConsumer longConsumer) {
                return super.tryAdvance((OfLong) longConsumer);
            }

            @Override // j$.util.stream.Streams.ConcatSpliterator.OfPrimitive, j$.util.stream.Streams.ConcatSpliterator, j$.util.Spliterator
            public /* bridge */ /* synthetic */ Spliterator.OfLong trySplit() {
                return (Spliterator.OfLong) super.trySplit();
            }

            public OfLong(Spliterator.OfLong aSpliterator, Spliterator.OfLong bSpliterator) {
                super(aSpliterator, bSpliterator);
            }
        }

        /* loaded from: classes2.dex */
        static class OfDouble extends OfPrimitive<Double, DoubleConsumer, Spliterator.OfDouble> implements Spliterator.OfDouble {
            @Override // j$.util.Spliterator.OfDouble
            public /* bridge */ /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
                super.forEachRemaining((OfDouble) doubleConsumer);
            }

            @Override // j$.util.Spliterator.OfDouble
            public /* bridge */ /* synthetic */ boolean tryAdvance(DoubleConsumer doubleConsumer) {
                return super.tryAdvance((OfDouble) doubleConsumer);
            }

            @Override // j$.util.stream.Streams.ConcatSpliterator.OfPrimitive, j$.util.stream.Streams.ConcatSpliterator, j$.util.Spliterator
            public /* bridge */ /* synthetic */ Spliterator.OfDouble trySplit() {
                return (Spliterator.OfDouble) super.trySplit();
            }

            public OfDouble(Spliterator.OfDouble aSpliterator, Spliterator.OfDouble bSpliterator) {
                super(aSpliterator, bSpliterator);
            }
        }
    }

    public static Runnable composeWithExceptions(final Runnable a, final Runnable b) {
        return new Runnable() { // from class: j$.util.stream.Streams.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    a.run();
                    b.run();
                } catch (Throwable e1) {
                    try {
                        b.run();
                    } catch (Throwable th) {
                    }
                    throw e1;
                }
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.BaseStream != java.util.stream.BaseStream<?, ?> */
    public static Runnable composedClose(final BaseStream<?, ?> baseStream, final BaseStream<?, ?> baseStream2) {
        return new Runnable() { // from class: j$.util.stream.Streams.2
            @Override // java.lang.Runnable
            public void run() {
                try {
                    baseStream.close();
                    baseStream2.close();
                } catch (Throwable e1) {
                    try {
                        baseStream2.close();
                    } catch (Throwable th) {
                    }
                    throw e1;
                }
            }
        };
    }
}
