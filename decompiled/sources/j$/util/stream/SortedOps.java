package j$.util.stream;

import j$.util.Collection;
import j$.util.Comparator;
import j$.util.List;
import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.IntFunction;
import j$.util.stream.DoublePipeline;
import j$.util.stream.IntPipeline;
import j$.util.stream.LongPipeline;
import j$.util.stream.Node;
import j$.util.stream.ReferencePipeline;
import j$.util.stream.Sink;
import j$.util.stream.SpinedBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
/* loaded from: classes2.dex */
final class SortedOps {
    private SortedOps() {
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<?, T, ?> */
    public static <T> Stream<T> makeRef(AbstractPipeline<?, T, ?> abstractPipeline) {
        return new OfRef(abstractPipeline);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<?, T, ?> */
    public static <T> Stream<T> makeRef(AbstractPipeline<?, T, ?> abstractPipeline, Comparator<? super T> comparator) {
        return new OfRef(abstractPipeline, comparator);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<?, java.lang.Integer, ?> */
    public static <T> IntStream makeInt(AbstractPipeline<?, Integer, ?> abstractPipeline) {
        return new OfInt(abstractPipeline);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<?, java.lang.Long, ?> */
    public static <T> LongStream makeLong(AbstractPipeline<?, Long, ?> abstractPipeline) {
        return new OfLong(abstractPipeline);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<?, java.lang.Double, ?> */
    public static <T> DoubleStream makeDouble(AbstractPipeline<?, Double, ?> abstractPipeline) {
        return new OfDouble(abstractPipeline);
    }

    /* loaded from: classes2.dex */
    public static final class OfRef<T> extends ReferencePipeline.StatefulOp<T, T> {
        private final Comparator<? super T> comparator;
        private final boolean isNaturalSort = true;

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<?, T, ?> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SortedOps$OfRef != java.util.stream.SortedOps$OfRef<T> */
        OfRef(AbstractPipeline<?, T, ?> abstractPipeline) {
            super(abstractPipeline, StreamShape.REFERENCE, StreamOpFlag.IS_ORDERED | StreamOpFlag.IS_SORTED);
            Comparator<? super T> comp = Comparator.CC.naturalOrder();
            this.comparator = comp;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<?, T, ?> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SortedOps$OfRef != java.util.stream.SortedOps$OfRef<T> */
        OfRef(AbstractPipeline<?, T, ?> abstractPipeline, java.util.Comparator<? super T> comparator) {
            super(abstractPipeline, StreamShape.REFERENCE, StreamOpFlag.IS_ORDERED | StreamOpFlag.NOT_SORTED);
            comparator.getClass();
            this.comparator = comparator;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<T> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SortedOps$OfRef != java.util.stream.SortedOps$OfRef<T> */
        @Override // j$.util.stream.AbstractPipeline
        public Sink<T> opWrapSink(int flags, Sink<T> sink) {
            sink.getClass();
            if (StreamOpFlag.SORTED.isKnown(flags) && this.isNaturalSort) {
                return sink;
            }
            if (StreamOpFlag.SIZED.isKnown(flags)) {
                return new SizedRefSortingSink(sink, this.comparator);
            }
            return new RefSortingSink(sink, this.comparator);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.function.IntFunction != java.util.function.IntFunction<T[]> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<T> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SortedOps$OfRef != java.util.stream.SortedOps$OfRef<T> */
        @Override // j$.util.stream.ReferencePipeline.StatefulOp, j$.util.stream.AbstractPipeline
        public <P_IN> Node<T> opEvaluateParallel(PipelineHelper<T> pipelineHelper, Spliterator<P_IN> spliterator, IntFunction<T[]> intFunction) {
            if (StreamOpFlag.SORTED.isKnown(pipelineHelper.getStreamAndOpFlags()) && this.isNaturalSort) {
                return pipelineHelper.evaluate(spliterator, false, intFunction);
            }
            T[] flattenedData = pipelineHelper.evaluate(spliterator, true, intFunction).asArray(intFunction);
            Arrays.sort(flattenedData, this.comparator);
            return Nodes.node(flattenedData);
        }
    }

    /* loaded from: classes2.dex */
    public static final class OfInt extends IntPipeline.StatefulOp<Integer> {
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<?, java.lang.Integer, ?> */
        OfInt(AbstractPipeline<?, Integer, ?> abstractPipeline) {
            super(abstractPipeline, StreamShape.INT_VALUE, StreamOpFlag.IS_ORDERED | StreamOpFlag.IS_SORTED);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Integer> */
        @Override // j$.util.stream.AbstractPipeline
        public Sink<Integer> opWrapSink(int flags, Sink<Integer> sink) {
            sink.getClass();
            if (StreamOpFlag.SORTED.isKnown(flags)) {
                return sink;
            }
            if (StreamOpFlag.SIZED.isKnown(flags)) {
                return new SizedIntSortingSink(sink);
            }
            return new IntSortingSink(sink);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.function.IntFunction != java.util.function.IntFunction<java.lang.Integer[]> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<java.lang.Integer> */
        @Override // j$.util.stream.IntPipeline.StatefulOp, j$.util.stream.AbstractPipeline
        public <P_IN> Node<Integer> opEvaluateParallel(PipelineHelper<Integer> pipelineHelper, Spliterator<P_IN> spliterator, IntFunction<Integer[]> intFunction) {
            if (StreamOpFlag.SORTED.isKnown(pipelineHelper.getStreamAndOpFlags())) {
                return pipelineHelper.evaluate(spliterator, false, intFunction);
            }
            Node.OfInt n = (Node.OfInt) pipelineHelper.evaluate(spliterator, true, intFunction);
            int[] content = n.asPrimitiveArray();
            Arrays.sort(content);
            return Nodes.node(content);
        }
    }

    /* loaded from: classes2.dex */
    public static final class OfLong extends LongPipeline.StatefulOp<Long> {
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<?, java.lang.Long, ?> */
        OfLong(AbstractPipeline<?, Long, ?> abstractPipeline) {
            super(abstractPipeline, StreamShape.LONG_VALUE, StreamOpFlag.IS_ORDERED | StreamOpFlag.IS_SORTED);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Long> */
        @Override // j$.util.stream.AbstractPipeline
        public Sink<Long> opWrapSink(int flags, Sink<Long> sink) {
            sink.getClass();
            if (StreamOpFlag.SORTED.isKnown(flags)) {
                return sink;
            }
            if (StreamOpFlag.SIZED.isKnown(flags)) {
                return new SizedLongSortingSink(sink);
            }
            return new LongSortingSink(sink);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.function.IntFunction != java.util.function.IntFunction<java.lang.Long[]> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<java.lang.Long> */
        @Override // j$.util.stream.LongPipeline.StatefulOp, j$.util.stream.AbstractPipeline
        public <P_IN> Node<Long> opEvaluateParallel(PipelineHelper<Long> pipelineHelper, Spliterator<P_IN> spliterator, IntFunction<Long[]> intFunction) {
            if (StreamOpFlag.SORTED.isKnown(pipelineHelper.getStreamAndOpFlags())) {
                return pipelineHelper.evaluate(spliterator, false, intFunction);
            }
            Node.OfLong n = (Node.OfLong) pipelineHelper.evaluate(spliterator, true, intFunction);
            long[] content = n.asPrimitiveArray();
            Arrays.sort(content);
            return Nodes.node(content);
        }
    }

    /* loaded from: classes2.dex */
    public static final class OfDouble extends DoublePipeline.StatefulOp<Double> {
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<?, java.lang.Double, ?> */
        OfDouble(AbstractPipeline<?, Double, ?> abstractPipeline) {
            super(abstractPipeline, StreamShape.DOUBLE_VALUE, StreamOpFlag.IS_ORDERED | StreamOpFlag.IS_SORTED);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Double> */
        @Override // j$.util.stream.AbstractPipeline
        public Sink<Double> opWrapSink(int flags, Sink<Double> sink) {
            sink.getClass();
            if (StreamOpFlag.SORTED.isKnown(flags)) {
                return sink;
            }
            if (StreamOpFlag.SIZED.isKnown(flags)) {
                return new SizedDoubleSortingSink(sink);
            }
            return new DoubleSortingSink(sink);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.function.IntFunction != java.util.function.IntFunction<java.lang.Double[]> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<java.lang.Double> */
        @Override // j$.util.stream.DoublePipeline.StatefulOp, j$.util.stream.AbstractPipeline
        public <P_IN> Node<Double> opEvaluateParallel(PipelineHelper<Double> pipelineHelper, Spliterator<P_IN> spliterator, IntFunction<Double[]> intFunction) {
            if (StreamOpFlag.SORTED.isKnown(pipelineHelper.getStreamAndOpFlags())) {
                return pipelineHelper.evaluate(spliterator, false, intFunction);
            }
            Node.OfDouble n = (Node.OfDouble) pipelineHelper.evaluate(spliterator, true, intFunction);
            double[] content = n.asPrimitiveArray();
            Arrays.sort(content);
            return Nodes.node(content);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static abstract class AbstractRefSortingSink<T> extends Sink.ChainedReference<T, T> {
        protected boolean cancellationWasRequested;
        protected final java.util.Comparator<? super T> comparator;

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<? super T> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SortedOps$AbstractRefSortingSink != java.util.stream.SortedOps$AbstractRefSortingSink<T> */
        AbstractRefSortingSink(Sink<? super T> sink, java.util.Comparator<? super T> comparator) {
            super(sink);
            this.comparator = comparator;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SortedOps$AbstractRefSortingSink != java.util.stream.SortedOps$AbstractRefSortingSink<T> */
        @Override // j$.util.stream.Sink.ChainedReference, j$.util.stream.Sink
        public final boolean cancellationRequested() {
            this.cancellationWasRequested = true;
            return false;
        }
    }

    /* loaded from: classes2.dex */
    private static final class SizedRefSortingSink<T> extends AbstractRefSortingSink<T> {
        private T[] array;
        private int offset;

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<? super T> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SortedOps$SizedRefSortingSink != java.util.stream.SortedOps$SizedRefSortingSink<T> */
        SizedRefSortingSink(Sink<? super T> sink, java.util.Comparator<? super T> comparator) {
            super(sink, comparator);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SortedOps$SizedRefSortingSink != java.util.stream.SortedOps$SizedRefSortingSink<T> */
        @Override // j$.util.stream.Sink.ChainedReference, j$.util.stream.Sink
        public void begin(long size) {
            if (size >= 2147483639) {
                throw new IllegalArgumentException("Stream size exceeds max array size");
            }
            this.array = (T[]) new Object[(int) size];
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SortedOps$SizedRefSortingSink != java.util.stream.SortedOps$SizedRefSortingSink<T> */
        @Override // j$.util.stream.Sink.ChainedReference, j$.util.stream.Sink
        public void end() {
            Arrays.sort(this.array, 0, this.offset, this.comparator);
            this.downstream.begin(this.offset);
            if (!this.cancellationWasRequested) {
                for (int i = 0; i < this.offset; i++) {
                    this.downstream.accept(this.array[i]);
                }
            } else {
                for (int i2 = 0; i2 < this.offset && !this.downstream.cancellationRequested(); i2++) {
                    this.downstream.accept(this.array[i2]);
                }
            }
            this.downstream.end();
            this.array = null;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SortedOps$SizedRefSortingSink != java.util.stream.SortedOps$SizedRefSortingSink<T> */
        @Override // j$.util.function.Consumer
        public void accept(T t) {
            T[] tArr = this.array;
            int i = this.offset;
            this.offset = i + 1;
            tArr[i] = t;
        }
    }

    /* loaded from: classes2.dex */
    private static final class RefSortingSink<T> extends AbstractRefSortingSink<T> {
        private ArrayList<T> list;

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<? super T> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SortedOps$RefSortingSink != java.util.stream.SortedOps$RefSortingSink<T> */
        RefSortingSink(Sink<? super T> sink, java.util.Comparator<? super T> comparator) {
            super(sink, comparator);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SortedOps$RefSortingSink != java.util.stream.SortedOps$RefSortingSink<T> */
        @Override // j$.util.stream.Sink.ChainedReference, j$.util.stream.Sink
        public void begin(long size) {
            if (size >= 2147483639) {
                throw new IllegalArgumentException("Stream size exceeds max array size");
            }
            this.list = size >= 0 ? new ArrayList<>((int) size) : new ArrayList<>();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SortedOps$RefSortingSink != java.util.stream.SortedOps$RefSortingSink<T> */
        @Override // j$.util.stream.Sink.ChainedReference, j$.util.stream.Sink
        public void end() {
            List.EL.sort(this.list, this.comparator);
            this.downstream.begin(this.list.size());
            if (!this.cancellationWasRequested) {
                ArrayList<T> arrayList = this.list;
                final Sink<? super E_OUT> sink = this.downstream;
                sink.getClass();
                Collection.EL.forEach(arrayList, new Consumer() { // from class: j$.util.stream.SortedOps$RefSortingSink$$ExternalSyntheticLambda0
                    @Override // j$.util.function.Consumer
                    public final void accept(Object obj) {
                        Sink.this.accept((Sink) obj);
                    }

                    @Override // j$.util.function.Consumer
                    public /* synthetic */ Consumer andThen(Consumer consumer) {
                        return consumer.getClass();
                    }
                });
            } else {
                Iterator<T> it = this.list.iterator();
                while (it.hasNext()) {
                    T t = it.next();
                    if (this.downstream.cancellationRequested()) {
                        break;
                    }
                    this.downstream.accept(t);
                }
            }
            this.downstream.end();
            this.list = null;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SortedOps$RefSortingSink != java.util.stream.SortedOps$RefSortingSink<T> */
        @Override // j$.util.function.Consumer
        public void accept(T t) {
            this.list.add(t);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static abstract class AbstractIntSortingSink extends Sink.ChainedInt<Integer> {
        protected boolean cancellationWasRequested;

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<? super java.lang.Integer> */
        AbstractIntSortingSink(Sink<? super Integer> sink) {
            super(sink);
        }

        @Override // j$.util.stream.Sink.ChainedInt, j$.util.stream.Sink
        public final boolean cancellationRequested() {
            this.cancellationWasRequested = true;
            return false;
        }
    }

    /* loaded from: classes2.dex */
    private static final class SizedIntSortingSink extends AbstractIntSortingSink {
        private int[] array;
        private int offset;

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<? super java.lang.Integer> */
        SizedIntSortingSink(Sink<? super Integer> sink) {
            super(sink);
        }

        @Override // j$.util.stream.Sink.ChainedInt, j$.util.stream.Sink
        public void begin(long size) {
            if (size >= 2147483639) {
                throw new IllegalArgumentException("Stream size exceeds max array size");
            }
            this.array = new int[(int) size];
        }

        @Override // j$.util.stream.Sink.ChainedInt, j$.util.stream.Sink
        public void end() {
            Arrays.sort(this.array, 0, this.offset);
            this.downstream.begin(this.offset);
            if (!this.cancellationWasRequested) {
                for (int i = 0; i < this.offset; i++) {
                    this.downstream.accept(this.array[i]);
                }
            } else {
                for (int i2 = 0; i2 < this.offset && !this.downstream.cancellationRequested(); i2++) {
                    this.downstream.accept(this.array[i2]);
                }
            }
            this.downstream.end();
            this.array = null;
        }

        @Override // j$.util.stream.Sink.OfInt, j$.util.stream.Sink
        public void accept(int t) {
            int[] iArr = this.array;
            int i = this.offset;
            this.offset = i + 1;
            iArr[i] = t;
        }
    }

    /* loaded from: classes2.dex */
    private static final class IntSortingSink extends AbstractIntSortingSink {
        private SpinedBuffer.OfInt b;

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<? super java.lang.Integer> */
        IntSortingSink(Sink<? super Integer> sink) {
            super(sink);
        }

        @Override // j$.util.stream.Sink.ChainedInt, j$.util.stream.Sink
        public void begin(long size) {
            if (size >= 2147483639) {
                throw new IllegalArgumentException("Stream size exceeds max array size");
            }
            this.b = size > 0 ? new SpinedBuffer.OfInt((int) size) : new SpinedBuffer.OfInt();
        }

        @Override // j$.util.stream.Sink.ChainedInt, j$.util.stream.Sink
        public void end() {
            int[] ints = this.b.asPrimitiveArray();
            Arrays.sort(ints);
            this.downstream.begin(ints.length);
            int i = 0;
            if (!this.cancellationWasRequested) {
                int length = ints.length;
                while (i < length) {
                    int anInt = ints[i];
                    this.downstream.accept(anInt);
                    i++;
                }
            } else {
                int length2 = ints.length;
                while (i < length2) {
                    int anInt2 = ints[i];
                    if (this.downstream.cancellationRequested()) {
                        break;
                    }
                    this.downstream.accept(anInt2);
                    i++;
                }
            }
            this.downstream.end();
        }

        @Override // j$.util.stream.Sink.OfInt, j$.util.stream.Sink
        public void accept(int t) {
            this.b.accept(t);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static abstract class AbstractLongSortingSink extends Sink.ChainedLong<Long> {
        protected boolean cancellationWasRequested;

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<? super java.lang.Long> */
        AbstractLongSortingSink(Sink<? super Long> sink) {
            super(sink);
        }

        @Override // j$.util.stream.Sink.ChainedLong, j$.util.stream.Sink
        public final boolean cancellationRequested() {
            this.cancellationWasRequested = true;
            return false;
        }
    }

    /* loaded from: classes2.dex */
    private static final class SizedLongSortingSink extends AbstractLongSortingSink {
        private long[] array;
        private int offset;

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<? super java.lang.Long> */
        SizedLongSortingSink(Sink<? super Long> sink) {
            super(sink);
        }

        @Override // j$.util.stream.Sink.ChainedLong, j$.util.stream.Sink
        public void begin(long size) {
            if (size >= 2147483639) {
                throw new IllegalArgumentException("Stream size exceeds max array size");
            }
            this.array = new long[(int) size];
        }

        @Override // j$.util.stream.Sink.ChainedLong, j$.util.stream.Sink
        public void end() {
            Arrays.sort(this.array, 0, this.offset);
            this.downstream.begin(this.offset);
            if (!this.cancellationWasRequested) {
                for (int i = 0; i < this.offset; i++) {
                    this.downstream.accept(this.array[i]);
                }
            } else {
                for (int i2 = 0; i2 < this.offset && !this.downstream.cancellationRequested(); i2++) {
                    this.downstream.accept(this.array[i2]);
                }
            }
            this.downstream.end();
            this.array = null;
        }

        @Override // j$.util.stream.Sink.OfLong, j$.util.stream.Sink
        public void accept(long t) {
            long[] jArr = this.array;
            int i = this.offset;
            this.offset = i + 1;
            jArr[i] = t;
        }
    }

    /* loaded from: classes2.dex */
    private static final class LongSortingSink extends AbstractLongSortingSink {
        private SpinedBuffer.OfLong b;

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<? super java.lang.Long> */
        LongSortingSink(Sink<? super Long> sink) {
            super(sink);
        }

        @Override // j$.util.stream.Sink.ChainedLong, j$.util.stream.Sink
        public void begin(long size) {
            if (size >= 2147483639) {
                throw new IllegalArgumentException("Stream size exceeds max array size");
            }
            this.b = size > 0 ? new SpinedBuffer.OfLong((int) size) : new SpinedBuffer.OfLong();
        }

        @Override // j$.util.stream.Sink.ChainedLong, j$.util.stream.Sink
        public void end() {
            long[] longs = this.b.asPrimitiveArray();
            Arrays.sort(longs);
            this.downstream.begin(longs.length);
            int i = 0;
            if (!this.cancellationWasRequested) {
                int length = longs.length;
                while (i < length) {
                    long aLong = longs[i];
                    this.downstream.accept(aLong);
                    i++;
                }
            } else {
                int length2 = longs.length;
                while (i < length2) {
                    long aLong2 = longs[i];
                    if (this.downstream.cancellationRequested()) {
                        break;
                    }
                    this.downstream.accept(aLong2);
                    i++;
                }
            }
            this.downstream.end();
        }

        @Override // j$.util.stream.Sink.OfLong, j$.util.stream.Sink
        public void accept(long t) {
            this.b.accept(t);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static abstract class AbstractDoubleSortingSink extends Sink.ChainedDouble<Double> {
        protected boolean cancellationWasRequested;

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<? super java.lang.Double> */
        AbstractDoubleSortingSink(Sink<? super Double> sink) {
            super(sink);
        }

        @Override // j$.util.stream.Sink.ChainedDouble, j$.util.stream.Sink
        public final boolean cancellationRequested() {
            this.cancellationWasRequested = true;
            return false;
        }
    }

    /* loaded from: classes2.dex */
    private static final class SizedDoubleSortingSink extends AbstractDoubleSortingSink {
        private double[] array;
        private int offset;

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<? super java.lang.Double> */
        SizedDoubleSortingSink(Sink<? super Double> sink) {
            super(sink);
        }

        @Override // j$.util.stream.Sink.ChainedDouble, j$.util.stream.Sink
        public void begin(long size) {
            if (size >= 2147483639) {
                throw new IllegalArgumentException("Stream size exceeds max array size");
            }
            this.array = new double[(int) size];
        }

        @Override // j$.util.stream.Sink.ChainedDouble, j$.util.stream.Sink
        public void end() {
            Arrays.sort(this.array, 0, this.offset);
            this.downstream.begin(this.offset);
            if (!this.cancellationWasRequested) {
                for (int i = 0; i < this.offset; i++) {
                    this.downstream.accept(this.array[i]);
                }
            } else {
                for (int i2 = 0; i2 < this.offset && !this.downstream.cancellationRequested(); i2++) {
                    this.downstream.accept(this.array[i2]);
                }
            }
            this.downstream.end();
            this.array = null;
        }

        @Override // j$.util.stream.Sink.OfDouble, j$.util.stream.Sink, j$.util.function.DoubleConsumer
        public void accept(double t) {
            double[] dArr = this.array;
            int i = this.offset;
            this.offset = i + 1;
            dArr[i] = t;
        }
    }

    /* loaded from: classes2.dex */
    private static final class DoubleSortingSink extends AbstractDoubleSortingSink {
        private SpinedBuffer.OfDouble b;

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<? super java.lang.Double> */
        DoubleSortingSink(Sink<? super Double> sink) {
            super(sink);
        }

        @Override // j$.util.stream.Sink.ChainedDouble, j$.util.stream.Sink
        public void begin(long size) {
            if (size >= 2147483639) {
                throw new IllegalArgumentException("Stream size exceeds max array size");
            }
            this.b = size > 0 ? new SpinedBuffer.OfDouble((int) size) : new SpinedBuffer.OfDouble();
        }

        @Override // j$.util.stream.Sink.ChainedDouble, j$.util.stream.Sink
        public void end() {
            double[] doubles = this.b.asPrimitiveArray();
            Arrays.sort(doubles);
            this.downstream.begin(doubles.length);
            int i = 0;
            if (!this.cancellationWasRequested) {
                int length = doubles.length;
                while (i < length) {
                    double aDouble = doubles[i];
                    this.downstream.accept(aDouble);
                    i++;
                }
            } else {
                int length2 = doubles.length;
                while (i < length2) {
                    double aDouble2 = doubles[i];
                    if (this.downstream.cancellationRequested()) {
                        break;
                    }
                    this.downstream.accept(aDouble2);
                    i++;
                }
            }
            this.downstream.end();
        }

        @Override // j$.util.stream.Sink.OfDouble, j$.util.stream.Sink, j$.util.function.DoubleConsumer
        public void accept(double t) {
            this.b.accept(t);
        }
    }
}
