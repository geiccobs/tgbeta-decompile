package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.IntFunction;
import j$.util.stream.DoublePipeline;
import j$.util.stream.IntPipeline;
import j$.util.stream.LongPipeline;
import j$.util.stream.Node;
import j$.util.stream.ReferencePipeline;
import j$.util.stream.Sink;
import j$.util.stream.StreamSpliterators;
import java.util.concurrent.CountedCompleter;
/* loaded from: classes2.dex */
final class SliceOps {
    static final /* synthetic */ boolean $assertionsDisabled = true;

    private SliceOps() {
    }

    public static long calcSize(long size, long skip, long limit) {
        if (size >= 0) {
            return Math.max(-1L, Math.min(size - skip, limit));
        }
        return -1L;
    }

    public static long calcSliceFence(long skip, long limit) {
        long sliceFence = limit >= 0 ? skip + limit : Long.MAX_VALUE;
        if (sliceFence >= 0) {
            return sliceFence;
        }
        return Long.MAX_VALUE;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
    public static <P_IN> Spliterator<P_IN> sliceSpliterator(StreamShape shape, Spliterator<P_IN> spliterator, long skip, long limit) {
        if ($assertionsDisabled || spliterator.hasCharacteristics(16384)) {
            long sliceFence = calcSliceFence(skip, limit);
            switch (AnonymousClass5.$SwitchMap$java$util$stream$StreamShape[shape.ordinal()]) {
                case 1:
                    return new StreamSpliterators.SliceSpliterator.OfRef(spliterator, skip, sliceFence);
                case 2:
                    return new StreamSpliterators.SliceSpliterator.OfInt((Spliterator.OfInt) spliterator, skip, sliceFence);
                case 3:
                    return new StreamSpliterators.SliceSpliterator.OfLong((Spliterator.OfLong) spliterator, skip, sliceFence);
                case 4:
                    return new StreamSpliterators.SliceSpliterator.OfDouble((Spliterator.OfDouble) spliterator, skip, sliceFence);
                default:
                    throw new IllegalStateException("Unknown shape " + shape);
            }
        }
        throw new AssertionError();
    }

    /* renamed from: j$.util.stream.SliceOps$5 */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass5 {
        static final /* synthetic */ int[] $SwitchMap$java$util$stream$StreamShape;

        static {
            int[] iArr = new int[StreamShape.values().length];
            $SwitchMap$java$util$stream$StreamShape = iArr;
            try {
                iArr[StreamShape.REFERENCE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$java$util$stream$StreamShape[StreamShape.INT_VALUE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$java$util$stream$StreamShape[StreamShape.LONG_VALUE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$java$util$stream$StreamShape[StreamShape.DOUBLE_VALUE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public static <T> IntFunction<T[]> castingArray() {
        return SliceOps$$ExternalSyntheticLambda0.INSTANCE;
    }

    public static /* synthetic */ Object[] lambda$castingArray$0(int size) {
        return new Object[size];
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<?, T, ?> */
    public static <T> Stream<T> makeRef(AbstractPipeline<?, T, ?> abstractPipeline, final long skip, final long limit) {
        if (skip < 0) {
            throw new IllegalArgumentException("Skip must be non-negative: " + skip);
        }
        return new ReferencePipeline.StatefulOp<T, T>(abstractPipeline, StreamShape.REFERENCE, flags(limit)) { // from class: j$.util.stream.SliceOps.1
            /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<T> */
            Spliterator<T> unorderedSkipLimitSpliterator(Spliterator<T> spliterator, long skip2, long limit2, long sizeIfKnown) {
                if (skip2 <= sizeIfKnown) {
                    long j = sizeIfKnown - skip2;
                    if (limit2 >= 0) {
                        j = Math.min(limit2, j);
                    }
                    limit2 = j;
                    skip2 = 0;
                }
                return new StreamSpliterators.UnorderedSliceSpliterator.OfRef(spliterator, skip2, limit2);
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<T> */
            @Override // j$.util.stream.AbstractPipeline
            <P_IN> Spliterator<T> opEvaluateParallelLazy(PipelineHelper<T> pipelineHelper, Spliterator<P_IN> spliterator) {
                long size = pipelineHelper.exactOutputSizeIfKnown(spliterator);
                if (size > 0 && spliterator.hasCharacteristics(16384)) {
                    Spliterator wrapSpliterator = pipelineHelper.wrapSpliterator(spliterator);
                    long j = skip;
                    return new StreamSpliterators.SliceSpliterator.OfRef(wrapSpliterator, j, SliceOps.calcSliceFence(j, limit));
                }
                if (!StreamOpFlag.ORDERED.isKnown(pipelineHelper.getStreamAndOpFlags())) {
                    return unorderedSkipLimitSpliterator(pipelineHelper.wrapSpliterator(spliterator), skip, limit, size);
                }
                return ((Node) new SliceTask(this, pipelineHelper, spliterator, SliceOps.castingArray(), skip, limit).invoke()).spliterator();
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
            /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<T> */
            /* JADX WARN: Generic types in debug info not equals: j$.util.function.IntFunction != java.util.function.IntFunction<T[]> */
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<T> */
            @Override // j$.util.stream.ReferencePipeline.StatefulOp, j$.util.stream.AbstractPipeline
            <P_IN> Node<T> opEvaluateParallel(PipelineHelper<T> pipelineHelper, Spliterator<P_IN> spliterator, IntFunction<T[]> intFunction) {
                long size = pipelineHelper.exactOutputSizeIfKnown(spliterator);
                if (size > 0 && spliterator.hasCharacteristics(16384)) {
                    return Nodes.collect(pipelineHelper, SliceOps.sliceSpliterator(pipelineHelper.getSourceShape(), spliterator, skip, limit), true, intFunction);
                }
                if (!StreamOpFlag.ORDERED.isKnown(pipelineHelper.getStreamAndOpFlags())) {
                    return Nodes.collect(this, unorderedSkipLimitSpliterator(pipelineHelper.wrapSpliterator(spliterator), skip, limit, size), true, intFunction);
                }
                return (Node) new SliceTask(this, pipelineHelper, spliterator, intFunction, skip, limit).invoke();
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<T> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<T> opWrapSink(int flags, Sink<T> sink) {
                return new Sink.ChainedReference<T, T>(sink) { // from class: j$.util.stream.SliceOps.1.1
                    long m;
                    long n;

                    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<? super T> */
                    {
                        AnonymousClass1.this = this;
                        this.n = skip;
                        this.m = limit >= 0 ? limit : Long.MAX_VALUE;
                    }

                    @Override // j$.util.stream.Sink.ChainedReference, j$.util.stream.Sink
                    public void begin(long size) {
                        this.downstream.begin(SliceOps.calcSize(size, skip, this.m));
                    }

                    @Override // j$.util.function.Consumer
                    public void accept(T t) {
                        long j = this.n;
                        if (j == 0) {
                            long j2 = this.m;
                            if (j2 > 0) {
                                this.m = j2 - 1;
                                this.downstream.accept(t);
                                return;
                            }
                            return;
                        }
                        this.n = j - 1;
                    }

                    @Override // j$.util.stream.Sink.ChainedReference, j$.util.stream.Sink
                    public boolean cancellationRequested() {
                        return this.m == 0 || this.downstream.cancellationRequested();
                    }
                };
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<?, java.lang.Integer, ?> */
    public static IntStream makeInt(AbstractPipeline<?, Integer, ?> abstractPipeline, long skip, long limit) {
        if (skip < 0) {
            throw new IllegalArgumentException("Skip must be non-negative: " + skip);
        }
        return new AnonymousClass2(abstractPipeline, StreamShape.INT_VALUE, flags(limit), skip, limit);
    }

    /* renamed from: j$.util.stream.SliceOps$2 */
    /* loaded from: classes2.dex */
    public class AnonymousClass2 extends IntPipeline.StatefulOp<Integer> {
        final /* synthetic */ long val$limit;
        final /* synthetic */ long val$skip;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<?, java.lang.Integer, ?> */
        AnonymousClass2(AbstractPipeline abstractPipeline, StreamShape inputShape, int opFlags, long j, long j2) {
            super(abstractPipeline, inputShape, opFlags);
            this.val$skip = j;
            this.val$limit = j2;
        }

        Spliterator.OfInt unorderedSkipLimitSpliterator(Spliterator.OfInt s, long skip, long limit, long sizeIfKnown) {
            if (skip <= sizeIfKnown) {
                long j = sizeIfKnown - skip;
                if (limit >= 0) {
                    j = Math.min(limit, j);
                }
                limit = j;
                skip = 0;
            }
            return new StreamSpliterators.UnorderedSliceSpliterator.OfInt(s, skip, limit);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<java.lang.Integer> */
        @Override // j$.util.stream.AbstractPipeline
        <P_IN> Spliterator<Integer> opEvaluateParallelLazy(PipelineHelper<Integer> pipelineHelper, Spliterator<P_IN> spliterator) {
            long size = pipelineHelper.exactOutputSizeIfKnown(spliterator);
            if (size > 0 && spliterator.hasCharacteristics(16384)) {
                long j = this.val$skip;
                return new StreamSpliterators.SliceSpliterator.OfInt((Spliterator.OfInt) pipelineHelper.wrapSpliterator(spliterator), j, SliceOps.calcSliceFence(j, this.val$limit));
            }
            if (!StreamOpFlag.ORDERED.isKnown(pipelineHelper.getStreamAndOpFlags())) {
                return unorderedSkipLimitSpliterator((Spliterator.OfInt) pipelineHelper.wrapSpliterator(spliterator), this.val$skip, this.val$limit, size);
            }
            return ((Node) new SliceTask(this, pipelineHelper, spliterator, SliceOps$2$$ExternalSyntheticLambda0.INSTANCE, this.val$skip, this.val$limit).invoke()).spliterator();
        }

        public static /* synthetic */ Integer[] lambda$opEvaluateParallelLazy$0(int x$0) {
            return new Integer[x$0];
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.function.IntFunction != java.util.function.IntFunction<java.lang.Integer[]> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<java.lang.Integer> */
        @Override // j$.util.stream.IntPipeline.StatefulOp, j$.util.stream.AbstractPipeline
        <P_IN> Node<Integer> opEvaluateParallel(PipelineHelper<Integer> pipelineHelper, Spliterator<P_IN> spliterator, IntFunction<Integer[]> intFunction) {
            long size = pipelineHelper.exactOutputSizeIfKnown(spliterator);
            if (size > 0 && spliterator.hasCharacteristics(16384)) {
                return Nodes.collectInt(pipelineHelper, SliceOps.sliceSpliterator(pipelineHelper.getSourceShape(), spliterator, this.val$skip, this.val$limit), true);
            }
            if (!StreamOpFlag.ORDERED.isKnown(pipelineHelper.getStreamAndOpFlags())) {
                Spliterator.OfInt s = unorderedSkipLimitSpliterator((Spliterator.OfInt) pipelineHelper.wrapSpliterator(spliterator), this.val$skip, this.val$limit, size);
                return Nodes.collectInt(this, s, true);
            }
            return (Node) new SliceTask(this, pipelineHelper, spliterator, intFunction, this.val$skip, this.val$limit).invoke();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Integer> */
        @Override // j$.util.stream.AbstractPipeline
        public Sink<Integer> opWrapSink(int flags, Sink<Integer> sink) {
            return new Sink.ChainedInt<Integer>(sink) { // from class: j$.util.stream.SliceOps.2.1
                long m;
                long n;

                /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<? super java.lang.Integer> */
                {
                    AnonymousClass2.this = this;
                    this.n = this.val$skip;
                    this.m = this.val$limit >= 0 ? this.val$limit : Long.MAX_VALUE;
                }

                @Override // j$.util.stream.Sink.ChainedInt, j$.util.stream.Sink
                public void begin(long size) {
                    this.downstream.begin(SliceOps.calcSize(size, AnonymousClass2.this.val$skip, this.m));
                }

                @Override // j$.util.stream.Sink.OfInt, j$.util.stream.Sink
                public void accept(int t) {
                    long j = this.n;
                    if (j == 0) {
                        long j2 = this.m;
                        if (j2 > 0) {
                            this.m = j2 - 1;
                            this.downstream.accept(t);
                            return;
                        }
                        return;
                    }
                    this.n = j - 1;
                }

                @Override // j$.util.stream.Sink.ChainedInt, j$.util.stream.Sink
                public boolean cancellationRequested() {
                    return this.m == 0 || this.downstream.cancellationRequested();
                }
            };
        }
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<?, java.lang.Long, ?> */
    public static LongStream makeLong(AbstractPipeline<?, Long, ?> abstractPipeline, long skip, long limit) {
        if (skip < 0) {
            throw new IllegalArgumentException("Skip must be non-negative: " + skip);
        }
        return new AnonymousClass3(abstractPipeline, StreamShape.LONG_VALUE, flags(limit), skip, limit);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: j$.util.stream.SliceOps$3 */
    /* loaded from: classes2.dex */
    public class AnonymousClass3 extends LongPipeline.StatefulOp<Long> {
        final /* synthetic */ long val$limit;
        final /* synthetic */ long val$skip;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<?, java.lang.Long, ?> */
        AnonymousClass3(AbstractPipeline abstractPipeline, StreamShape inputShape, int opFlags, long j, long j2) {
            super(abstractPipeline, inputShape, opFlags);
            this.val$skip = j;
            this.val$limit = j2;
        }

        Spliterator.OfLong unorderedSkipLimitSpliterator(Spliterator.OfLong s, long skip, long limit, long sizeIfKnown) {
            if (skip <= sizeIfKnown) {
                long j = sizeIfKnown - skip;
                if (limit >= 0) {
                    j = Math.min(limit, j);
                }
                limit = j;
                skip = 0;
            }
            return new StreamSpliterators.UnorderedSliceSpliterator.OfLong(s, skip, limit);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<java.lang.Long> */
        @Override // j$.util.stream.AbstractPipeline
        <P_IN> Spliterator<Long> opEvaluateParallelLazy(PipelineHelper<Long> pipelineHelper, Spliterator<P_IN> spliterator) {
            long size = pipelineHelper.exactOutputSizeIfKnown(spliterator);
            if (size > 0 && spliterator.hasCharacteristics(16384)) {
                long j = this.val$skip;
                return new StreamSpliterators.SliceSpliterator.OfLong((Spliterator.OfLong) pipelineHelper.wrapSpliterator(spliterator), j, SliceOps.calcSliceFence(j, this.val$limit));
            }
            if (!StreamOpFlag.ORDERED.isKnown(pipelineHelper.getStreamAndOpFlags())) {
                return unorderedSkipLimitSpliterator((Spliterator.OfLong) pipelineHelper.wrapSpliterator(spliterator), this.val$skip, this.val$limit, size);
            }
            return ((Node) new SliceTask(this, pipelineHelper, spliterator, SliceOps$3$$ExternalSyntheticLambda0.INSTANCE, this.val$skip, this.val$limit).invoke()).spliterator();
        }

        public static /* synthetic */ Long[] lambda$opEvaluateParallelLazy$0(int x$0) {
            return new Long[x$0];
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.function.IntFunction != java.util.function.IntFunction<java.lang.Long[]> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<java.lang.Long> */
        @Override // j$.util.stream.LongPipeline.StatefulOp, j$.util.stream.AbstractPipeline
        <P_IN> Node<Long> opEvaluateParallel(PipelineHelper<Long> pipelineHelper, Spliterator<P_IN> spliterator, IntFunction<Long[]> intFunction) {
            long size = pipelineHelper.exactOutputSizeIfKnown(spliterator);
            if (size > 0 && spliterator.hasCharacteristics(16384)) {
                return Nodes.collectLong(pipelineHelper, SliceOps.sliceSpliterator(pipelineHelper.getSourceShape(), spliterator, this.val$skip, this.val$limit), true);
            }
            if (!StreamOpFlag.ORDERED.isKnown(pipelineHelper.getStreamAndOpFlags())) {
                Spliterator.OfLong s = unorderedSkipLimitSpliterator((Spliterator.OfLong) pipelineHelper.wrapSpliterator(spliterator), this.val$skip, this.val$limit, size);
                return Nodes.collectLong(this, s, true);
            }
            return (Node) new SliceTask(this, pipelineHelper, spliterator, intFunction, this.val$skip, this.val$limit).invoke();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Long> */
        @Override // j$.util.stream.AbstractPipeline
        public Sink<Long> opWrapSink(int flags, Sink<Long> sink) {
            return new Sink.ChainedLong<Long>(sink) { // from class: j$.util.stream.SliceOps.3.1
                long m;
                long n;

                /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<? super java.lang.Long> */
                {
                    AnonymousClass3.this = this;
                    this.n = this.val$skip;
                    this.m = this.val$limit >= 0 ? this.val$limit : Long.MAX_VALUE;
                }

                @Override // j$.util.stream.Sink.ChainedLong, j$.util.stream.Sink
                public void begin(long size) {
                    this.downstream.begin(SliceOps.calcSize(size, AnonymousClass3.this.val$skip, this.m));
                }

                @Override // j$.util.stream.Sink.OfLong, j$.util.stream.Sink
                public void accept(long t) {
                    long j = this.n;
                    if (j == 0) {
                        long j2 = this.m;
                        if (j2 > 0) {
                            this.m = j2 - 1;
                            this.downstream.accept(t);
                            return;
                        }
                        return;
                    }
                    this.n = j - 1;
                }

                @Override // j$.util.stream.Sink.ChainedLong, j$.util.stream.Sink
                public boolean cancellationRequested() {
                    return this.m == 0 || this.downstream.cancellationRequested();
                }
            };
        }
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<?, java.lang.Double, ?> */
    public static DoubleStream makeDouble(AbstractPipeline<?, Double, ?> abstractPipeline, long skip, long limit) {
        if (skip < 0) {
            throw new IllegalArgumentException("Skip must be non-negative: " + skip);
        }
        return new AnonymousClass4(abstractPipeline, StreamShape.DOUBLE_VALUE, flags(limit), skip, limit);
    }

    /* renamed from: j$.util.stream.SliceOps$4 */
    /* loaded from: classes2.dex */
    public class AnonymousClass4 extends DoublePipeline.StatefulOp<Double> {
        final /* synthetic */ long val$limit;
        final /* synthetic */ long val$skip;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<?, java.lang.Double, ?> */
        AnonymousClass4(AbstractPipeline abstractPipeline, StreamShape inputShape, int opFlags, long j, long j2) {
            super(abstractPipeline, inputShape, opFlags);
            this.val$skip = j;
            this.val$limit = j2;
        }

        Spliterator.OfDouble unorderedSkipLimitSpliterator(Spliterator.OfDouble s, long skip, long limit, long sizeIfKnown) {
            if (skip <= sizeIfKnown) {
                long j = sizeIfKnown - skip;
                if (limit >= 0) {
                    j = Math.min(limit, j);
                }
                limit = j;
                skip = 0;
            }
            return new StreamSpliterators.UnorderedSliceSpliterator.OfDouble(s, skip, limit);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<java.lang.Double> */
        @Override // j$.util.stream.AbstractPipeline
        <P_IN> Spliterator<Double> opEvaluateParallelLazy(PipelineHelper<Double> pipelineHelper, Spliterator<P_IN> spliterator) {
            long size = pipelineHelper.exactOutputSizeIfKnown(spliterator);
            if (size > 0 && spliterator.hasCharacteristics(16384)) {
                long j = this.val$skip;
                return new StreamSpliterators.SliceSpliterator.OfDouble((Spliterator.OfDouble) pipelineHelper.wrapSpliterator(spliterator), j, SliceOps.calcSliceFence(j, this.val$limit));
            }
            if (!StreamOpFlag.ORDERED.isKnown(pipelineHelper.getStreamAndOpFlags())) {
                return unorderedSkipLimitSpliterator((Spliterator.OfDouble) pipelineHelper.wrapSpliterator(spliterator), this.val$skip, this.val$limit, size);
            }
            return ((Node) new SliceTask(this, pipelineHelper, spliterator, SliceOps$4$$ExternalSyntheticLambda0.INSTANCE, this.val$skip, this.val$limit).invoke()).spliterator();
        }

        public static /* synthetic */ Double[] lambda$opEvaluateParallelLazy$0(int x$0) {
            return new Double[x$0];
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.function.IntFunction != java.util.function.IntFunction<java.lang.Double[]> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<java.lang.Double> */
        @Override // j$.util.stream.DoublePipeline.StatefulOp, j$.util.stream.AbstractPipeline
        <P_IN> Node<Double> opEvaluateParallel(PipelineHelper<Double> pipelineHelper, Spliterator<P_IN> spliterator, IntFunction<Double[]> intFunction) {
            long size = pipelineHelper.exactOutputSizeIfKnown(spliterator);
            if (size > 0 && spliterator.hasCharacteristics(16384)) {
                return Nodes.collectDouble(pipelineHelper, SliceOps.sliceSpliterator(pipelineHelper.getSourceShape(), spliterator, this.val$skip, this.val$limit), true);
            }
            if (!StreamOpFlag.ORDERED.isKnown(pipelineHelper.getStreamAndOpFlags())) {
                Spliterator.OfDouble s = unorderedSkipLimitSpliterator((Spliterator.OfDouble) pipelineHelper.wrapSpliterator(spliterator), this.val$skip, this.val$limit, size);
                return Nodes.collectDouble(this, s, true);
            }
            return (Node) new SliceTask(this, pipelineHelper, spliterator, intFunction, this.val$skip, this.val$limit).invoke();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Double> */
        @Override // j$.util.stream.AbstractPipeline
        public Sink<Double> opWrapSink(int flags, Sink<Double> sink) {
            return new Sink.ChainedDouble<Double>(sink) { // from class: j$.util.stream.SliceOps.4.1
                long m;
                long n;

                /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<? super java.lang.Double> */
                {
                    AnonymousClass4.this = this;
                    this.n = this.val$skip;
                    this.m = this.val$limit >= 0 ? this.val$limit : Long.MAX_VALUE;
                }

                @Override // j$.util.stream.Sink.ChainedDouble, j$.util.stream.Sink
                public void begin(long size) {
                    this.downstream.begin(SliceOps.calcSize(size, AnonymousClass4.this.val$skip, this.m));
                }

                @Override // j$.util.stream.Sink.OfDouble, j$.util.stream.Sink, j$.util.function.DoubleConsumer
                public void accept(double t) {
                    long j = this.n;
                    if (j == 0) {
                        long j2 = this.m;
                        if (j2 > 0) {
                            this.m = j2 - 1;
                            this.downstream.accept(t);
                            return;
                        }
                        return;
                    }
                    this.n = j - 1;
                }

                @Override // j$.util.stream.Sink.ChainedDouble, j$.util.stream.Sink
                public boolean cancellationRequested() {
                    return this.m == 0 || this.downstream.cancellationRequested();
                }
            };
        }
    }

    private static int flags(long limit) {
        return StreamOpFlag.NOT_SIZED | (limit != -1 ? StreamOpFlag.IS_SHORT_CIRCUIT : 0);
    }

    /* loaded from: classes2.dex */
    private static final class SliceTask<P_IN, P_OUT> extends AbstractShortCircuitTask<P_IN, P_OUT, Node<P_OUT>, SliceTask<P_IN, P_OUT>> {
        private volatile boolean completed;
        private final IntFunction<P_OUT[]> generator;
        private final AbstractPipeline<P_OUT, P_OUT, ?> op;
        private final long targetOffset;
        private final long targetSize;
        private long thisNodeSize;

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.function.IntFunction != java.util.function.IntFunction<P_OUT[]> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<P_OUT, P_OUT, ?> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<P_OUT> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SliceOps$SliceTask != java.util.stream.SliceOps$SliceTask<P_IN, P_OUT> */
        SliceTask(AbstractPipeline<P_OUT, P_OUT, ?> abstractPipeline, PipelineHelper<P_OUT> pipelineHelper, Spliterator<P_IN> spliterator, IntFunction<P_OUT[]> intFunction, long offset, long size) {
            super(pipelineHelper, spliterator);
            this.op = abstractPipeline;
            this.generator = intFunction;
            this.targetOffset = offset;
            this.targetSize = size;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SliceOps$SliceTask != java.util.stream.SliceOps$SliceTask<P_IN, P_OUT> */
        SliceTask(SliceTask<P_IN, P_OUT> sliceTask, Spliterator<P_IN> spliterator) {
            super(sliceTask, spliterator);
            this.op = sliceTask.op;
            this.generator = sliceTask.generator;
            this.targetOffset = sliceTask.targetOffset;
            this.targetSize = sliceTask.targetSize;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SliceOps$SliceTask != java.util.stream.SliceOps$SliceTask<P_IN, P_OUT> */
        @Override // j$.util.stream.AbstractTask
        public SliceTask<P_IN, P_OUT> makeChild(Spliterator<P_IN> spliterator) {
            return new SliceTask<>(this, spliterator);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SliceOps$SliceTask != java.util.stream.SliceOps$SliceTask<P_IN, P_OUT> */
        @Override // j$.util.stream.AbstractShortCircuitTask
        public final Node<P_OUT> getEmptyResult() {
            return Nodes.emptyNode(this.op.getOutputShape());
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Node != java.util.stream.Node<P_OUT> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Node$Builder != java.util.stream.Node$Builder<P_OUT> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<P_OUT> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SliceOps$SliceTask != java.util.stream.SliceOps$SliceTask<P_IN, P_OUT> */
        @Override // j$.util.stream.AbstractTask
        public final Node<P_OUT> doLeaf() {
            long j = -1;
            if (isRoot()) {
                if (StreamOpFlag.SIZED.isPreserved(this.op.sourceOrOpFlags)) {
                    j = this.op.exactOutputSizeIfKnown(this.spliterator);
                }
                long sizeIfKnown = j;
                Node.Builder<P_OUT> makeNodeBuilder = this.op.makeNodeBuilder(sizeIfKnown, this.generator);
                this.helper.copyIntoWithCancel(this.helper.wrapSink(this.op.opWrapSink(this.helper.getStreamAndOpFlags(), makeNodeBuilder)), this.spliterator);
                return makeNodeBuilder.build();
            }
            Node<P_OUT> build = ((Node.Builder) this.helper.wrapAndCopyInto(this.helper.makeNodeBuilder(-1L, this.generator), this.spliterator)).build();
            this.thisNodeSize = build.count();
            this.completed = true;
            this.spliterator = null;
            return build;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Node != java.util.stream.Node<P_OUT> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SliceOps$SliceTask != java.util.stream.SliceOps$SliceTask<P_IN, P_OUT> */
        @Override // j$.util.stream.AbstractTask, java.util.concurrent.CountedCompleter
        public final void onCompletion(CountedCompleter<?> caller) {
            Node<P_OUT> node;
            if (!isLeaf()) {
                this.thisNodeSize = ((SliceTask) this.leftChild).thisNodeSize + ((SliceTask) this.rightChild).thisNodeSize;
                if (this.canceled) {
                    this.thisNodeSize = 0L;
                    node = getEmptyResult();
                } else if (this.thisNodeSize == 0) {
                    node = getEmptyResult();
                } else if (((SliceTask) this.leftChild).thisNodeSize == 0) {
                    node = ((SliceTask) this.rightChild).getLocalResult();
                } else {
                    node = Nodes.conc(this.op.getOutputShape(), ((SliceTask) this.leftChild).getLocalResult(), ((SliceTask) this.rightChild).getLocalResult());
                }
                setLocalResult(isRoot() ? doTruncate(node) : node);
                this.completed = true;
            }
            if (this.targetSize >= 0 && !isRoot() && isLeftCompleted(this.targetOffset + this.targetSize)) {
                cancelLaterNodes();
            }
            super.onCompletion(caller);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SliceOps$SliceTask != java.util.stream.SliceOps$SliceTask<P_IN, P_OUT> */
        @Override // j$.util.stream.AbstractShortCircuitTask
        public void cancel() {
            super.cancel();
            if (this.completed) {
                setLocalResult(getEmptyResult());
            }
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Node != java.util.stream.Node<P_OUT> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SliceOps$SliceTask != java.util.stream.SliceOps$SliceTask<P_IN, P_OUT> */
        private Node<P_OUT> doTruncate(Node<P_OUT> node) {
            long to = this.targetSize >= 0 ? Math.min(node.count(), this.targetOffset + this.targetSize) : this.thisNodeSize;
            return node.truncate(this.targetOffset, to, this.generator);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SliceOps$SliceTask != java.util.stream.SliceOps$SliceTask<P_IN, P_OUT> */
        private boolean isLeftCompleted(long target) {
            SliceTask sliceTask;
            long size = this.completed ? this.thisNodeSize : completedSize(target);
            if (size >= target) {
                return true;
            }
            SliceTask<P_IN, P_OUT> sliceTask2 = this;
            for (SliceTask<P_IN, P_OUT> sliceTask3 = (SliceTask) getParent(); sliceTask3 != null; sliceTask3 = (SliceTask) sliceTask3.getParent()) {
                if (sliceTask2 == sliceTask3.rightChild && (sliceTask = (SliceTask) sliceTask3.leftChild) != null) {
                    size += sliceTask.completedSize(target);
                    if (size >= target) {
                        return true;
                    }
                }
                sliceTask2 = sliceTask3;
            }
            return size >= target;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SliceOps$SliceTask != java.util.stream.SliceOps$SliceTask<P_IN, P_OUT> */
        private long completedSize(long target) {
            if (this.completed) {
                return this.thisNodeSize;
            }
            SliceTask sliceTask = (SliceTask) this.leftChild;
            SliceTask sliceTask2 = (SliceTask) this.rightChild;
            if (sliceTask == null || sliceTask2 == null) {
                return this.thisNodeSize;
            }
            long leftSize = sliceTask.completedSize(target);
            return leftSize >= target ? leftSize : sliceTask2.completedSize(target) + leftSize;
        }
    }
}
