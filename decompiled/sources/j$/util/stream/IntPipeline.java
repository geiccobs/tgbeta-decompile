package j$.util.stream;

import j$.util.IntSummaryStatistics;
import j$.util.OptionalDouble;
import j$.util.OptionalInt;
import j$.util.Spliterator;
import j$.util.Spliterators;
import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.BinaryOperator;
import j$.util.function.Function;
import j$.util.function.IntBinaryOperator;
import j$.util.function.IntConsumer;
import j$.util.function.IntFunction;
import j$.util.function.IntPredicate;
import j$.util.function.IntToDoubleFunction;
import j$.util.function.IntToLongFunction;
import j$.util.function.IntUnaryOperator;
import j$.util.function.ObjIntConsumer;
import j$.util.function.Supplier;
import j$.util.stream.DoublePipeline;
import j$.util.stream.IntPipeline;
import j$.util.stream.LongPipeline;
import j$.util.stream.MatchOps;
import j$.util.stream.Node;
import j$.util.stream.ReferencePipeline;
import j$.util.stream.Sink;
import j$.util.stream.StreamSpliterators;
import java.util.Iterator;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public abstract class IntPipeline<E_IN> extends AbstractPipeline<E_IN, Integer, IntStream> implements IntStream {
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.AbstractPipeline, j$.util.stream.BaseStream
    public /* bridge */ /* synthetic */ IntStream parallel() {
        return (IntStream) super.parallel();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.AbstractPipeline, j$.util.stream.BaseStream
    public /* bridge */ /* synthetic */ IntStream sequential() {
        return (IntStream) super.sequential();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<? extends java.util.Spliterator<java.lang.Integer>> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    IntPipeline(Supplier<? extends Spliterator<Integer>> supplier, int sourceFlags, boolean parallel) {
        super(supplier, sourceFlags, parallel);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<java.lang.Integer> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    IntPipeline(Spliterator<Integer> spliterator, int sourceFlags, boolean parallel) {
        super(spliterator, sourceFlags, parallel);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<?, E_IN, ?> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    IntPipeline(AbstractPipeline<?, E_IN, ?> abstractPipeline, int opFlags) {
        super(abstractPipeline, opFlags);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Integer> */
    private static IntConsumer adapt(Sink<Integer> sink) {
        if (sink instanceof IntConsumer) {
            return (IntConsumer) sink;
        }
        if (Tripwire.ENABLED) {
            Tripwire.trip(AbstractPipeline.class, "using IntStream.adapt(Sink<Integer> s)");
        }
        sink.getClass();
        return new IntPipeline$$ExternalSyntheticLambda6(sink);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<java.lang.Integer> */
    public static Spliterator.OfInt adapt(Spliterator<Integer> spliterator) {
        if (spliterator instanceof Spliterator.OfInt) {
            return (Spliterator.OfInt) spliterator;
        }
        if (Tripwire.ENABLED) {
            Tripwire.trip(AbstractPipeline.class, "using IntStream.adapt(Spliterator<Integer> s)");
        }
        throw new UnsupportedOperationException("IntStream.adapt(Spliterator<Integer> s)");
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.AbstractPipeline
    public final StreamShape getOutputShape() {
        return StreamShape.INT_VALUE;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.IntFunction != java.util.function.IntFunction<java.lang.Integer[]> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<java.lang.Integer> */
    @Override // j$.util.stream.AbstractPipeline
    final <P_IN> Node<Integer> evaluateToNode(PipelineHelper<Integer> pipelineHelper, Spliterator<P_IN> spliterator, boolean flattenTree, IntFunction<Integer[]> intFunction) {
        return Nodes.collectInt(pipelineHelper, spliterator, flattenTree);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<java.util.Spliterator<P_IN>> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<java.lang.Integer> */
    @Override // j$.util.stream.AbstractPipeline
    final <P_IN> Spliterator<Integer> wrap(PipelineHelper<Integer> pipelineHelper, Supplier<Spliterator<P_IN>> supplier, boolean isParallel) {
        return new StreamSpliterators.IntWrappingSpliterator(pipelineHelper, supplier, isParallel);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<? extends java.util.Spliterator<java.lang.Integer>> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.AbstractPipeline
    /* renamed from: lazySpliterator */
    public final Spliterator<Integer> lazySpliterator2(Supplier<? extends Spliterator<Integer>> supplier) {
        return new StreamSpliterators.DelegatingSpliterator.OfInt(supplier);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<java.lang.Integer> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Integer> */
    @Override // j$.util.stream.AbstractPipeline
    final void forEachWithCancel(Spliterator<Integer> spliterator, Sink<Integer> sink) {
        Spliterator.OfInt spl = adapt(spliterator);
        IntConsumer adaptedSink = adapt(sink);
        while (!sink.cancellationRequested() && spl.tryAdvance(adaptedSink)) {
        }
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.IntFunction != java.util.function.IntFunction<java.lang.Integer[]> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.AbstractPipeline, j$.util.stream.PipelineHelper
    public final Node.Builder<Integer> makeNodeBuilder(long exactSizeIfKnown, IntFunction<Integer[]> intFunction) {
        return Nodes.intBuilder(exactSizeIfKnown);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    /* JADX WARN: Type inference failed for: r0v0, types: [j$.util.Spliterator$OfInt] */
    @Override // j$.util.stream.BaseStream, j$.util.stream.DoubleStream
    /* renamed from: iterator */
    public final Iterator<Integer> iterator2() {
        return Spliterators.iterator((Spliterator.OfInt) spliterator2());
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.AbstractPipeline, j$.util.stream.BaseStream
    /* renamed from: spliterator */
    public final Spliterator<Integer> spliterator2() {
        return adapt(super.spliterator());
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.IntStream
    public final LongStream asLongStream() {
        return new LongPipeline.StatelessOp<Integer>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) { // from class: j$.util.stream.IntPipeline.1
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Long> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<Integer> opWrapSink(int flags, Sink<Long> sink) {
                return new Sink.ChainedInt<Long>(sink) { // from class: j$.util.stream.IntPipeline.1.1
                    @Override // j$.util.stream.Sink.OfInt, j$.util.stream.Sink
                    public void accept(int t) {
                        this.downstream.accept(t);
                    }
                };
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.IntStream
    public final DoubleStream asDoubleStream() {
        return new DoublePipeline.StatelessOp<Integer>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) { // from class: j$.util.stream.IntPipeline.2
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Double> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<Integer> opWrapSink(int flags, Sink<Double> sink) {
                return new Sink.ChainedInt<Double>(sink) { // from class: j$.util.stream.IntPipeline.2.1
                    @Override // j$.util.stream.Sink.OfInt, j$.util.stream.Sink
                    public void accept(int t) {
                        this.downstream.accept(t);
                    }
                };
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.IntStream
    public final Stream<Integer> boxed() {
        return mapToObj(IntPipeline$$ExternalSyntheticLambda7.INSTANCE);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.IntStream
    public final IntStream map(final IntUnaryOperator mapper) {
        mapper.getClass();
        return new StatelessOp<Integer>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) { // from class: j$.util.stream.IntPipeline.3
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Integer> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<Integer> opWrapSink(int flags, Sink<Integer> sink) {
                return new Sink.ChainedInt<Integer>(sink) { // from class: j$.util.stream.IntPipeline.3.1
                    @Override // j$.util.stream.Sink.OfInt, j$.util.stream.Sink
                    public void accept(int t) {
                        this.downstream.accept(mapper.applyAsInt(t));
                    }
                };
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.IntFunction != java.util.function.IntFunction<? extends U> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.IntStream
    public final <U> Stream<U> mapToObj(final IntFunction<? extends U> intFunction) {
        intFunction.getClass();
        return new ReferencePipeline.StatelessOp<Integer, U>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) { // from class: j$.util.stream.IntPipeline.4
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<U> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<Integer> opWrapSink(int flags, Sink<U> sink) {
                return new Sink.ChainedInt<U>(sink) { // from class: j$.util.stream.IntPipeline.4.1
                    @Override // j$.util.stream.Sink.OfInt, j$.util.stream.Sink
                    public void accept(int t) {
                        this.downstream.accept(intFunction.apply(t));
                    }
                };
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.IntStream
    public final LongStream mapToLong(final IntToLongFunction mapper) {
        mapper.getClass();
        return new LongPipeline.StatelessOp<Integer>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) { // from class: j$.util.stream.IntPipeline.5
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Long> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<Integer> opWrapSink(int flags, Sink<Long> sink) {
                return new Sink.ChainedInt<Long>(sink) { // from class: j$.util.stream.IntPipeline.5.1
                    @Override // j$.util.stream.Sink.OfInt, j$.util.stream.Sink
                    public void accept(int t) {
                        this.downstream.accept(mapper.applyAsLong(t));
                    }
                };
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.IntStream
    public final DoubleStream mapToDouble(final IntToDoubleFunction mapper) {
        mapper.getClass();
        return new DoublePipeline.StatelessOp<Integer>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) { // from class: j$.util.stream.IntPipeline.6
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Double> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<Integer> opWrapSink(int flags, Sink<Double> sink) {
                return new Sink.ChainedInt<Double>(sink) { // from class: j$.util.stream.IntPipeline.6.1
                    @Override // j$.util.stream.Sink.OfInt, j$.util.stream.Sink
                    public void accept(int t) {
                        this.downstream.accept(mapper.applyAsDouble(t));
                    }
                };
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.IntFunction != java.util.function.IntFunction<? extends java.util.stream.IntStream> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.IntStream
    public final IntStream flatMap(final IntFunction<? extends IntStream> intFunction) {
        return new StatelessOp<Integer>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) { // from class: j$.util.stream.IntPipeline.7

            /* renamed from: j$.util.stream.IntPipeline$7$1 */
            /* loaded from: classes2.dex */
            public class AnonymousClass1 extends Sink.ChainedInt<Integer> {
                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<? super java.lang.Integer> */
                AnonymousClass1(Sink sink) {
                    super(sink);
                    AnonymousClass7.this = this$1;
                }

                @Override // j$.util.stream.Sink.ChainedInt, j$.util.stream.Sink
                public void begin(long size) {
                    this.downstream.begin(-1L);
                }

                @Override // j$.util.stream.Sink.OfInt, j$.util.stream.Sink
                public void accept(int t) {
                    IntStream result = (IntStream) intFunction.apply(t);
                    if (result != null) {
                        try {
                            result.sequential().forEach(new IntConsumer() { // from class: j$.util.stream.IntPipeline$7$1$$ExternalSyntheticLambda0
                                @Override // j$.util.function.IntConsumer
                                public final void accept(int i) {
                                    IntPipeline.AnonymousClass7.AnonymousClass1.this.m105lambda$accept$0$javautilstreamIntPipeline$7$1(i);
                                }

                                @Override // j$.util.function.IntConsumer
                                public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
                                    return intConsumer.getClass();
                                }
                            });
                        } catch (Throwable th) {
                            if (result != null) {
                                try {
                                    result.close();
                                } catch (Throwable th2) {
                                }
                            }
                            throw th;
                        }
                    }
                    if (result != null) {
                        result.close();
                    }
                }

                /* renamed from: lambda$accept$0$java-util-stream-IntPipeline$7$1 */
                public /* synthetic */ void m105lambda$accept$0$javautilstreamIntPipeline$7$1(int i) {
                    this.downstream.accept(i);
                }
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Integer> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<Integer> opWrapSink(int flags, Sink<Integer> sink) {
                return new AnonymousClass1(sink);
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.BaseStream
    public IntStream unordered() {
        if (!isOrdered()) {
            return this;
        }
        return new StatelessOp<Integer>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_ORDERED) { // from class: j$.util.stream.IntPipeline.8
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Integer> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<Integer> opWrapSink(int flags, Sink<Integer> sink) {
                return sink;
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.IntStream
    public final IntStream filter(final IntPredicate predicate) {
        predicate.getClass();
        return new StatelessOp<Integer>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_SIZED) { // from class: j$.util.stream.IntPipeline.9
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Integer> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<Integer> opWrapSink(int flags, Sink<Integer> sink) {
                return new Sink.ChainedInt<Integer>(sink) { // from class: j$.util.stream.IntPipeline.9.1
                    @Override // j$.util.stream.Sink.ChainedInt, j$.util.stream.Sink
                    public void begin(long size) {
                        this.downstream.begin(-1L);
                    }

                    @Override // j$.util.stream.Sink.OfInt, j$.util.stream.Sink
                    public void accept(int t) {
                        if (predicate.test(t)) {
                            this.downstream.accept(t);
                        }
                    }
                };
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.IntStream
    public final IntStream peek(final IntConsumer action) {
        action.getClass();
        return new StatelessOp<Integer>(this, StreamShape.INT_VALUE, 0) { // from class: j$.util.stream.IntPipeline.10
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Integer> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<Integer> opWrapSink(int flags, Sink<Integer> sink) {
                return new Sink.ChainedInt<Integer>(sink) { // from class: j$.util.stream.IntPipeline.10.1
                    @Override // j$.util.stream.Sink.OfInt, j$.util.stream.Sink
                    public void accept(int t) {
                        action.accept(t);
                        this.downstream.accept(t);
                    }
                };
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.IntStream
    public final IntStream limit(long maxSize) {
        if (maxSize < 0) {
            throw new IllegalArgumentException(Long.toString(maxSize));
        }
        return SliceOps.makeInt(this, 0L, maxSize);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.IntStream
    public final IntStream skip(long n) {
        if (n < 0) {
            throw new IllegalArgumentException(Long.toString(n));
        }
        if (n == 0) {
            return this;
        }
        return SliceOps.makeInt(this, n, -1L);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.IntStream
    public final IntStream sorted() {
        return SortedOps.makeInt(this);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.IntStream
    public final IntStream distinct() {
        return boxed().distinct().mapToInt(IntPipeline$$ExternalSyntheticLambda13.INSTANCE);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.IntStream
    public void forEach(IntConsumer action) {
        evaluate(ForEachOps.makeInt(action, false));
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.IntStream
    public void forEachOrdered(IntConsumer action) {
        evaluate(ForEachOps.makeInt(action, true));
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.IntStream
    public final int sum() {
        return reduce(0, IntPipeline$$ExternalSyntheticLambda3.INSTANCE);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.IntStream
    public final OptionalInt min() {
        return reduce(IntPipeline$$ExternalSyntheticLambda5.INSTANCE);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.IntStream
    public final OptionalInt max() {
        return reduce(IntPipeline$$ExternalSyntheticLambda4.INSTANCE);
    }

    public static /* synthetic */ long lambda$count$1(int e) {
        return 1L;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.IntStream
    public final long count() {
        return mapToLong(IntPipeline$$ExternalSyntheticLambda9.INSTANCE).sum();
    }

    public static /* synthetic */ long[] lambda$average$2() {
        return new long[2];
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.IntStream
    public final OptionalDouble average() {
        long[] avg = (long[]) collect(IntPipeline$$ExternalSyntheticLambda12.INSTANCE, IntPipeline$$ExternalSyntheticLambda11.INSTANCE, IntPipeline$$ExternalSyntheticLambda1.INSTANCE);
        if (avg[0] > 0) {
            double d = avg[1];
            double d2 = avg[0];
            Double.isNaN(d);
            Double.isNaN(d2);
            return OptionalDouble.of(d / d2);
        }
        return OptionalDouble.empty();
    }

    public static /* synthetic */ void lambda$average$3(long[] ll, int i) {
        ll[0] = ll[0] + 1;
        ll[1] = ll[1] + i;
    }

    public static /* synthetic */ void lambda$average$4(long[] ll, long[] rr) {
        ll[0] = ll[0] + rr[0];
        ll[1] = ll[1] + rr[1];
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.IntStream
    public final IntSummaryStatistics summaryStatistics() {
        return (IntSummaryStatistics) collect(Collectors$$ExternalSyntheticLambda81.INSTANCE, IntPipeline$$ExternalSyntheticLambda10.INSTANCE, IntPipeline$$ExternalSyntheticLambda0.INSTANCE);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.IntStream
    public final int reduce(int identity, IntBinaryOperator op) {
        return ((Integer) evaluate(ReduceOps.makeInt(identity, op))).intValue();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.IntStream
    public final OptionalInt reduce(IntBinaryOperator op) {
        return (OptionalInt) evaluate(ReduceOps.makeInt(op));
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BiConsumer != java.util.function.BiConsumer<R, R> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BinaryOperator != java.util.function.BinaryOperator<R> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.ObjIntConsumer != java.util.function.ObjIntConsumer<R> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<R> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.IntStream
    public final <R> R collect(Supplier<R> supplier, ObjIntConsumer<R> objIntConsumer, final BiConsumer<R, R> biConsumer) {
        return (R) evaluate(ReduceOps.makeInt(supplier, objIntConsumer, new BinaryOperator() { // from class: j$.util.stream.IntPipeline$$ExternalSyntheticLambda2
            @Override // j$.util.function.BiFunction
            public /* synthetic */ BiFunction andThen(Function function) {
                return function.getClass();
            }

            @Override // j$.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return BiConsumer.this.accept(obj, obj2);
            }
        }));
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.IntStream
    public final boolean anyMatch(IntPredicate predicate) {
        return ((Boolean) evaluate(MatchOps.makeInt(predicate, MatchOps.MatchKind.ANY))).booleanValue();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.IntStream
    public final boolean allMatch(IntPredicate predicate) {
        return ((Boolean) evaluate(MatchOps.makeInt(predicate, MatchOps.MatchKind.ALL))).booleanValue();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.IntStream
    public final boolean noneMatch(IntPredicate predicate) {
        return ((Boolean) evaluate(MatchOps.makeInt(predicate, MatchOps.MatchKind.NONE))).booleanValue();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.IntStream
    public final OptionalInt findFirst() {
        return (OptionalInt) evaluate(FindOps.makeInt(true));
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.IntStream
    public final OptionalInt findAny() {
        return (OptionalInt) evaluate(FindOps.makeInt(false));
    }

    public static /* synthetic */ Integer[] lambda$toArray$6(int x$0) {
        return new Integer[x$0];
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline != java.util.stream.IntPipeline<E_IN> */
    @Override // j$.util.stream.IntStream
    public final int[] toArray() {
        return Nodes.flattenInt((Node.OfInt) evaluateToArrayNode(IntPipeline$$ExternalSyntheticLambda8.INSTANCE)).asPrimitiveArray();
    }

    /* loaded from: classes2.dex */
    public static class Head<E_IN> extends IntPipeline<E_IN> {
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline$Head != java.util.stream.IntPipeline$Head<E_IN> */
        @Override // j$.util.stream.IntPipeline, j$.util.stream.AbstractPipeline, j$.util.stream.BaseStream
        public /* bridge */ /* synthetic */ IntStream parallel() {
            return (IntStream) super.parallel();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline$Head != java.util.stream.IntPipeline$Head<E_IN> */
        @Override // j$.util.stream.IntPipeline, j$.util.stream.AbstractPipeline, j$.util.stream.BaseStream
        public /* bridge */ /* synthetic */ IntStream sequential() {
            return (IntStream) super.sequential();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<? extends java.util.Spliterator<java.lang.Integer>> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline$Head != java.util.stream.IntPipeline$Head<E_IN> */
        public Head(Supplier<? extends Spliterator<Integer>> supplier, int sourceFlags, boolean parallel) {
            super(supplier, sourceFlags, parallel);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<java.lang.Integer> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline$Head != java.util.stream.IntPipeline$Head<E_IN> */
        public Head(Spliterator<Integer> spliterator, int sourceFlags, boolean parallel) {
            super(spliterator, sourceFlags, parallel);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline$Head != java.util.stream.IntPipeline$Head<E_IN> */
        @Override // j$.util.stream.AbstractPipeline
        final boolean opIsStateful() {
            throw new UnsupportedOperationException();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline$Head != java.util.stream.IntPipeline$Head<E_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Integer> */
        @Override // j$.util.stream.AbstractPipeline
        public final Sink<E_IN> opWrapSink(int flags, Sink<Integer> sink) {
            throw new UnsupportedOperationException();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline$Head != java.util.stream.IntPipeline$Head<E_IN> */
        @Override // j$.util.stream.IntPipeline, j$.util.stream.IntStream
        public void forEach(IntConsumer action) {
            if (!isParallel()) {
                IntPipeline.adapt(sourceStageSpliterator()).forEachRemaining(action);
            } else {
                super.forEach(action);
            }
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline$Head != java.util.stream.IntPipeline$Head<E_IN> */
        @Override // j$.util.stream.IntPipeline, j$.util.stream.IntStream
        public void forEachOrdered(IntConsumer action) {
            if (!isParallel()) {
                IntPipeline.adapt(sourceStageSpliterator()).forEachRemaining(action);
            } else {
                super.forEachOrdered(action);
            }
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class StatelessOp<E_IN> extends IntPipeline<E_IN> {
        static final /* synthetic */ boolean $assertionsDisabled = true;

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline$StatelessOp != java.util.stream.IntPipeline$StatelessOp<E_IN> */
        @Override // j$.util.stream.IntPipeline, j$.util.stream.AbstractPipeline, j$.util.stream.BaseStream
        public /* bridge */ /* synthetic */ IntStream parallel() {
            return (IntStream) super.parallel();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline$StatelessOp != java.util.stream.IntPipeline$StatelessOp<E_IN> */
        @Override // j$.util.stream.IntPipeline, j$.util.stream.AbstractPipeline, j$.util.stream.BaseStream
        public /* bridge */ /* synthetic */ IntStream sequential() {
            return (IntStream) super.sequential();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<?, E_IN, ?> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline$StatelessOp != java.util.stream.IntPipeline$StatelessOp<E_IN> */
        public StatelessOp(AbstractPipeline<?, E_IN, ?> abstractPipeline, StreamShape inputShape, int opFlags) {
            super(abstractPipeline, opFlags);
            if ($assertionsDisabled || abstractPipeline.getOutputShape() == inputShape) {
                return;
            }
            throw new AssertionError();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline$StatelessOp != java.util.stream.IntPipeline$StatelessOp<E_IN> */
        @Override // j$.util.stream.AbstractPipeline
        final boolean opIsStateful() {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static abstract class StatefulOp<E_IN> extends IntPipeline<E_IN> {
        static final /* synthetic */ boolean $assertionsDisabled = true;

        @Override // j$.util.stream.AbstractPipeline
        abstract <P_IN> Node<Integer> opEvaluateParallel(PipelineHelper<Integer> pipelineHelper, Spliterator<P_IN> spliterator, IntFunction<Integer[]> intFunction);

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline$StatefulOp != java.util.stream.IntPipeline$StatefulOp<E_IN> */
        @Override // j$.util.stream.IntPipeline, j$.util.stream.AbstractPipeline, j$.util.stream.BaseStream
        public /* bridge */ /* synthetic */ IntStream parallel() {
            return (IntStream) super.parallel();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline$StatefulOp != java.util.stream.IntPipeline$StatefulOp<E_IN> */
        @Override // j$.util.stream.IntPipeline, j$.util.stream.AbstractPipeline, j$.util.stream.BaseStream
        public /* bridge */ /* synthetic */ IntStream sequential() {
            return (IntStream) super.sequential();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<?, E_IN, ?> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline$StatefulOp != java.util.stream.IntPipeline$StatefulOp<E_IN> */
        public StatefulOp(AbstractPipeline<?, E_IN, ?> abstractPipeline, StreamShape inputShape, int opFlags) {
            super(abstractPipeline, opFlags);
            if ($assertionsDisabled || abstractPipeline.getOutputShape() == inputShape) {
                return;
            }
            throw new AssertionError();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.IntPipeline$StatefulOp != java.util.stream.IntPipeline$StatefulOp<E_IN> */
        @Override // j$.util.stream.AbstractPipeline
        final boolean opIsStateful() {
            return true;
        }
    }
}
