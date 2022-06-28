package j$.util.stream;

import j$.util.LongSummaryStatistics;
import j$.util.OptionalDouble;
import j$.util.OptionalLong;
import j$.util.Spliterator;
import j$.util.Spliterators;
import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.BinaryOperator;
import j$.util.function.Function;
import j$.util.function.IntFunction;
import j$.util.function.LongBinaryOperator;
import j$.util.function.LongConsumer;
import j$.util.function.LongFunction;
import j$.util.function.LongPredicate;
import j$.util.function.LongToDoubleFunction;
import j$.util.function.LongToIntFunction;
import j$.util.function.LongUnaryOperator;
import j$.util.function.ObjLongConsumer;
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
public abstract class LongPipeline<E_IN> extends AbstractPipeline<E_IN, Long, LongStream> implements LongStream {
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.AbstractPipeline, j$.util.stream.BaseStream
    public /* bridge */ /* synthetic */ LongStream parallel() {
        return (LongStream) super.parallel();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.AbstractPipeline, j$.util.stream.BaseStream
    public /* bridge */ /* synthetic */ LongStream sequential() {
        return (LongStream) super.sequential();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<? extends java.util.Spliterator<java.lang.Long>> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    LongPipeline(Supplier<? extends Spliterator<Long>> supplier, int sourceFlags, boolean parallel) {
        super(supplier, sourceFlags, parallel);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<java.lang.Long> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    LongPipeline(Spliterator<Long> spliterator, int sourceFlags, boolean parallel) {
        super(spliterator, sourceFlags, parallel);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<?, E_IN, ?> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    LongPipeline(AbstractPipeline<?, E_IN, ?> abstractPipeline, int opFlags) {
        super(abstractPipeline, opFlags);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Long> */
    private static LongConsumer adapt(Sink<Long> sink) {
        if (sink instanceof LongConsumer) {
            return (LongConsumer) sink;
        }
        if (Tripwire.ENABLED) {
            Tripwire.trip(AbstractPipeline.class, "using LongStream.adapt(Sink<Long> s)");
        }
        sink.getClass();
        return new LongPipeline$$ExternalSyntheticLambda7(sink);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<java.lang.Long> */
    public static Spliterator.OfLong adapt(Spliterator<Long> spliterator) {
        if (spliterator instanceof Spliterator.OfLong) {
            return (Spliterator.OfLong) spliterator;
        }
        if (Tripwire.ENABLED) {
            Tripwire.trip(AbstractPipeline.class, "using LongStream.adapt(Spliterator<Long> s)");
        }
        throw new UnsupportedOperationException("LongStream.adapt(Spliterator<Long> s)");
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.AbstractPipeline
    public final StreamShape getOutputShape() {
        return StreamShape.LONG_VALUE;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.IntFunction != java.util.function.IntFunction<java.lang.Long[]> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<java.lang.Long> */
    @Override // j$.util.stream.AbstractPipeline
    final <P_IN> Node<Long> evaluateToNode(PipelineHelper<Long> pipelineHelper, Spliterator<P_IN> spliterator, boolean flattenTree, IntFunction<Long[]> intFunction) {
        return Nodes.collectLong(pipelineHelper, spliterator, flattenTree);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<java.util.Spliterator<P_IN>> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<java.lang.Long> */
    @Override // j$.util.stream.AbstractPipeline
    final <P_IN> Spliterator<Long> wrap(PipelineHelper<Long> pipelineHelper, Supplier<Spliterator<P_IN>> supplier, boolean isParallel) {
        return new StreamSpliterators.LongWrappingSpliterator(pipelineHelper, supplier, isParallel);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<? extends java.util.Spliterator<java.lang.Long>> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.AbstractPipeline
    /* renamed from: lazySpliterator */
    public final Spliterator<Long> lazySpliterator2(Supplier<? extends Spliterator<Long>> supplier) {
        return new StreamSpliterators.DelegatingSpliterator.OfLong(supplier);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<java.lang.Long> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Long> */
    @Override // j$.util.stream.AbstractPipeline
    final void forEachWithCancel(Spliterator<Long> spliterator, Sink<Long> sink) {
        Spliterator.OfLong spl = adapt(spliterator);
        LongConsumer adaptedSink = adapt(sink);
        while (!sink.cancellationRequested() && spl.tryAdvance(adaptedSink)) {
        }
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.IntFunction != java.util.function.IntFunction<java.lang.Long[]> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.AbstractPipeline, j$.util.stream.PipelineHelper
    public final Node.Builder<Long> makeNodeBuilder(long exactSizeIfKnown, IntFunction<Long[]> intFunction) {
        return Nodes.longBuilder(exactSizeIfKnown);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    /* JADX WARN: Type inference failed for: r0v0, types: [j$.util.Spliterator$OfLong] */
    @Override // j$.util.stream.BaseStream, j$.util.stream.DoubleStream
    /* renamed from: iterator */
    public final Iterator<Long> iterator2() {
        return Spliterators.iterator((Spliterator.OfLong) spliterator2());
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.AbstractPipeline, j$.util.stream.BaseStream
    /* renamed from: spliterator */
    public final Spliterator<Long> spliterator2() {
        return adapt(super.spliterator());
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.LongStream
    public final DoubleStream asDoubleStream() {
        return new DoublePipeline.StatelessOp<Long>(this, StreamShape.LONG_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) { // from class: j$.util.stream.LongPipeline.1
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Double> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<Long> opWrapSink(int flags, Sink<Double> sink) {
                return new Sink.ChainedLong<Double>(sink) { // from class: j$.util.stream.LongPipeline.1.1
                    @Override // j$.util.stream.Sink.OfLong, j$.util.stream.Sink
                    public void accept(long t) {
                        this.downstream.accept(t);
                    }
                };
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.LongStream
    public final Stream<Long> boxed() {
        return mapToObj(LongPipeline$$ExternalSyntheticLambda8.INSTANCE);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.LongStream
    public final LongStream map(final LongUnaryOperator mapper) {
        mapper.getClass();
        return new StatelessOp<Long>(this, StreamShape.LONG_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) { // from class: j$.util.stream.LongPipeline.2
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Long> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<Long> opWrapSink(int flags, Sink<Long> sink) {
                return new Sink.ChainedLong<Long>(sink) { // from class: j$.util.stream.LongPipeline.2.1
                    @Override // j$.util.stream.Sink.OfLong, j$.util.stream.Sink
                    public void accept(long t) {
                        this.downstream.accept(mapper.applyAsLong(t));
                    }
                };
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.LongFunction != java.util.function.LongFunction<? extends U> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.LongStream
    public final <U> Stream<U> mapToObj(final LongFunction<? extends U> longFunction) {
        longFunction.getClass();
        return new ReferencePipeline.StatelessOp<Long, U>(this, StreamShape.LONG_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) { // from class: j$.util.stream.LongPipeline.3
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<U> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<Long> opWrapSink(int flags, Sink<U> sink) {
                return new Sink.ChainedLong<U>(sink) { // from class: j$.util.stream.LongPipeline.3.1
                    @Override // j$.util.stream.Sink.OfLong, j$.util.stream.Sink
                    public void accept(long t) {
                        this.downstream.accept(longFunction.apply(t));
                    }
                };
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.LongStream
    public final IntStream mapToInt(final LongToIntFunction mapper) {
        mapper.getClass();
        return new IntPipeline.StatelessOp<Long>(this, StreamShape.LONG_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) { // from class: j$.util.stream.LongPipeline.4
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Integer> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<Long> opWrapSink(int flags, Sink<Integer> sink) {
                return new Sink.ChainedLong<Integer>(sink) { // from class: j$.util.stream.LongPipeline.4.1
                    @Override // j$.util.stream.Sink.OfLong, j$.util.stream.Sink
                    public void accept(long t) {
                        this.downstream.accept(mapper.applyAsInt(t));
                    }
                };
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.LongStream
    public final DoubleStream mapToDouble(final LongToDoubleFunction mapper) {
        mapper.getClass();
        return new DoublePipeline.StatelessOp<Long>(this, StreamShape.LONG_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) { // from class: j$.util.stream.LongPipeline.5
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Double> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<Long> opWrapSink(int flags, Sink<Double> sink) {
                return new Sink.ChainedLong<Double>(sink) { // from class: j$.util.stream.LongPipeline.5.1
                    @Override // j$.util.stream.Sink.OfLong, j$.util.stream.Sink
                    public void accept(long t) {
                        this.downstream.accept(mapper.applyAsDouble(t));
                    }
                };
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.LongFunction != java.util.function.LongFunction<? extends java.util.stream.LongStream> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.LongStream
    public final LongStream flatMap(final LongFunction<? extends LongStream> longFunction) {
        return new StatelessOp<Long>(this, StreamShape.LONG_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) { // from class: j$.util.stream.LongPipeline.6

            /* renamed from: j$.util.stream.LongPipeline$6$1 */
            /* loaded from: classes2.dex */
            public class AnonymousClass1 extends Sink.ChainedLong<Long> {
                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<? super java.lang.Long> */
                AnonymousClass1(Sink sink) {
                    super(sink);
                    AnonymousClass6.this = this$1;
                }

                @Override // j$.util.stream.Sink.ChainedLong, j$.util.stream.Sink
                public void begin(long size) {
                    this.downstream.begin(-1L);
                }

                @Override // j$.util.stream.Sink.OfLong, j$.util.stream.Sink
                public void accept(long t) {
                    LongStream result = (LongStream) longFunction.apply(t);
                    if (result != null) {
                        try {
                            result.sequential().forEach(new LongConsumer() { // from class: j$.util.stream.LongPipeline$6$1$$ExternalSyntheticLambda0
                                @Override // j$.util.function.LongConsumer
                                public final void accept(long j) {
                                    LongPipeline.AnonymousClass6.AnonymousClass1.this.m106lambda$accept$0$javautilstreamLongPipeline$6$1(j);
                                }

                                @Override // j$.util.function.LongConsumer
                                public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
                                    return longConsumer.getClass();
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

                /* renamed from: lambda$accept$0$java-util-stream-LongPipeline$6$1 */
                public /* synthetic */ void m106lambda$accept$0$javautilstreamLongPipeline$6$1(long i) {
                    this.downstream.accept(i);
                }
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Long> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<Long> opWrapSink(int flags, Sink<Long> sink) {
                return new AnonymousClass1(sink);
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.BaseStream
    public LongStream unordered() {
        if (!isOrdered()) {
            return this;
        }
        return new StatelessOp<Long>(this, StreamShape.LONG_VALUE, StreamOpFlag.NOT_ORDERED) { // from class: j$.util.stream.LongPipeline.7
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Long> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<Long> opWrapSink(int flags, Sink<Long> sink) {
                return sink;
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.LongStream
    public final LongStream filter(final LongPredicate predicate) {
        predicate.getClass();
        return new StatelessOp<Long>(this, StreamShape.LONG_VALUE, StreamOpFlag.NOT_SIZED) { // from class: j$.util.stream.LongPipeline.8
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Long> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<Long> opWrapSink(int flags, Sink<Long> sink) {
                return new Sink.ChainedLong<Long>(sink) { // from class: j$.util.stream.LongPipeline.8.1
                    @Override // j$.util.stream.Sink.ChainedLong, j$.util.stream.Sink
                    public void begin(long size) {
                        this.downstream.begin(-1L);
                    }

                    @Override // j$.util.stream.Sink.OfLong, j$.util.stream.Sink
                    public void accept(long t) {
                        if (predicate.test(t)) {
                            this.downstream.accept(t);
                        }
                    }
                };
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.LongStream
    public final LongStream peek(final LongConsumer action) {
        action.getClass();
        return new StatelessOp<Long>(this, StreamShape.LONG_VALUE, 0) { // from class: j$.util.stream.LongPipeline.9
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Long> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<Long> opWrapSink(int flags, Sink<Long> sink) {
                return new Sink.ChainedLong<Long>(sink) { // from class: j$.util.stream.LongPipeline.9.1
                    @Override // j$.util.stream.Sink.OfLong, j$.util.stream.Sink
                    public void accept(long t) {
                        action.accept(t);
                        this.downstream.accept(t);
                    }
                };
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.LongStream
    public final LongStream limit(long maxSize) {
        if (maxSize < 0) {
            throw new IllegalArgumentException(Long.toString(maxSize));
        }
        return SliceOps.makeLong(this, 0L, maxSize);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.LongStream
    public final LongStream skip(long n) {
        if (n < 0) {
            throw new IllegalArgumentException(Long.toString(n));
        }
        if (n == 0) {
            return this;
        }
        return SliceOps.makeLong(this, n, -1L);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.LongStream
    public final LongStream sorted() {
        return SortedOps.makeLong(this);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.LongStream
    public final LongStream distinct() {
        return boxed().distinct().mapToLong(LongPipeline$$ExternalSyntheticLambda13.INSTANCE);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.LongStream
    public void forEach(LongConsumer action) {
        evaluate(ForEachOps.makeLong(action, false));
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.LongStream
    public void forEachOrdered(LongConsumer action) {
        evaluate(ForEachOps.makeLong(action, true));
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.LongStream
    public final long sum() {
        return reduce(0L, LongPipeline$$ExternalSyntheticLambda4.INSTANCE);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.LongStream
    public final OptionalLong min() {
        return reduce(LongPipeline$$ExternalSyntheticLambda6.INSTANCE);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.LongStream
    public final OptionalLong max() {
        return reduce(LongPipeline$$ExternalSyntheticLambda5.INSTANCE);
    }

    public static /* synthetic */ long[] lambda$average$1() {
        return new long[2];
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.LongStream
    public final OptionalDouble average() {
        long[] avg = (long[]) collect(LongPipeline$$ExternalSyntheticLambda12.INSTANCE, LongPipeline$$ExternalSyntheticLambda11.INSTANCE, LongPipeline$$ExternalSyntheticLambda1.INSTANCE);
        if (avg[0] > 0) {
            double d = avg[1];
            double d2 = avg[0];
            Double.isNaN(d);
            Double.isNaN(d2);
            return OptionalDouble.of(d / d2);
        }
        return OptionalDouble.empty();
    }

    public static /* synthetic */ void lambda$average$2(long[] ll, long i) {
        ll[0] = ll[0] + 1;
        ll[1] = ll[1] + i;
    }

    public static /* synthetic */ void lambda$average$3(long[] ll, long[] rr) {
        ll[0] = ll[0] + rr[0];
        ll[1] = ll[1] + rr[1];
    }

    public static /* synthetic */ long lambda$count$4(long e) {
        return 1L;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.LongStream
    public final long count() {
        return map(LongPipeline$$ExternalSyntheticLambda9.INSTANCE).sum();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.LongStream
    public final LongSummaryStatistics summaryStatistics() {
        return (LongSummaryStatistics) collect(Collectors$$ExternalSyntheticLambda82.INSTANCE, LongPipeline$$ExternalSyntheticLambda10.INSTANCE, LongPipeline$$ExternalSyntheticLambda0.INSTANCE);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.LongStream
    public final long reduce(long identity, LongBinaryOperator op) {
        return ((Long) evaluate(ReduceOps.makeLong(identity, op))).longValue();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.LongStream
    public final OptionalLong reduce(LongBinaryOperator op) {
        return (OptionalLong) evaluate(ReduceOps.makeLong(op));
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BiConsumer != java.util.function.BiConsumer<R, R> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BinaryOperator != java.util.function.BinaryOperator<R> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.ObjLongConsumer != java.util.function.ObjLongConsumer<R> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<R> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.LongStream
    public final <R> R collect(Supplier<R> supplier, ObjLongConsumer<R> objLongConsumer, final BiConsumer<R, R> biConsumer) {
        return (R) evaluate(ReduceOps.makeLong(supplier, objLongConsumer, new BinaryOperator() { // from class: j$.util.stream.LongPipeline$$ExternalSyntheticLambda2
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

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.LongStream
    public final boolean anyMatch(LongPredicate predicate) {
        return ((Boolean) evaluate(MatchOps.makeLong(predicate, MatchOps.MatchKind.ANY))).booleanValue();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.LongStream
    public final boolean allMatch(LongPredicate predicate) {
        return ((Boolean) evaluate(MatchOps.makeLong(predicate, MatchOps.MatchKind.ALL))).booleanValue();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.LongStream
    public final boolean noneMatch(LongPredicate predicate) {
        return ((Boolean) evaluate(MatchOps.makeLong(predicate, MatchOps.MatchKind.NONE))).booleanValue();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.LongStream
    public final OptionalLong findFirst() {
        return (OptionalLong) evaluate(FindOps.makeLong(true));
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.LongStream
    public final OptionalLong findAny() {
        return (OptionalLong) evaluate(FindOps.makeLong(false));
    }

    public static /* synthetic */ Long[] lambda$toArray$6(int x$0) {
        return new Long[x$0];
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline != java.util.stream.LongPipeline<E_IN> */
    @Override // j$.util.stream.LongStream
    public final long[] toArray() {
        return Nodes.flattenLong((Node.OfLong) evaluateToArrayNode(LongPipeline$$ExternalSyntheticLambda3.INSTANCE)).asPrimitiveArray();
    }

    /* loaded from: classes2.dex */
    public static class Head<E_IN> extends LongPipeline<E_IN> {
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline$Head != java.util.stream.LongPipeline$Head<E_IN> */
        @Override // j$.util.stream.LongPipeline, j$.util.stream.AbstractPipeline, j$.util.stream.BaseStream
        public /* bridge */ /* synthetic */ LongStream parallel() {
            return (LongStream) super.parallel();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline$Head != java.util.stream.LongPipeline$Head<E_IN> */
        @Override // j$.util.stream.LongPipeline, j$.util.stream.AbstractPipeline, j$.util.stream.BaseStream
        public /* bridge */ /* synthetic */ LongStream sequential() {
            return (LongStream) super.sequential();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<? extends java.util.Spliterator<java.lang.Long>> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline$Head != java.util.stream.LongPipeline$Head<E_IN> */
        public Head(Supplier<? extends Spliterator<Long>> supplier, int sourceFlags, boolean parallel) {
            super(supplier, sourceFlags, parallel);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<java.lang.Long> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline$Head != java.util.stream.LongPipeline$Head<E_IN> */
        public Head(Spliterator<Long> spliterator, int sourceFlags, boolean parallel) {
            super(spliterator, sourceFlags, parallel);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline$Head != java.util.stream.LongPipeline$Head<E_IN> */
        @Override // j$.util.stream.AbstractPipeline
        final boolean opIsStateful() {
            throw new UnsupportedOperationException();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline$Head != java.util.stream.LongPipeline$Head<E_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Long> */
        @Override // j$.util.stream.AbstractPipeline
        public final Sink<E_IN> opWrapSink(int flags, Sink<Long> sink) {
            throw new UnsupportedOperationException();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline$Head != java.util.stream.LongPipeline$Head<E_IN> */
        @Override // j$.util.stream.LongPipeline, j$.util.stream.LongStream
        public void forEach(LongConsumer action) {
            if (!isParallel()) {
                LongPipeline.adapt(sourceStageSpliterator()).forEachRemaining(action);
            } else {
                super.forEach(action);
            }
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline$Head != java.util.stream.LongPipeline$Head<E_IN> */
        @Override // j$.util.stream.LongPipeline, j$.util.stream.LongStream
        public void forEachOrdered(LongConsumer action) {
            if (!isParallel()) {
                LongPipeline.adapt(sourceStageSpliterator()).forEachRemaining(action);
            } else {
                super.forEachOrdered(action);
            }
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class StatelessOp<E_IN> extends LongPipeline<E_IN> {
        static final /* synthetic */ boolean $assertionsDisabled = true;

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline$StatelessOp != java.util.stream.LongPipeline$StatelessOp<E_IN> */
        @Override // j$.util.stream.LongPipeline, j$.util.stream.AbstractPipeline, j$.util.stream.BaseStream
        public /* bridge */ /* synthetic */ LongStream parallel() {
            return (LongStream) super.parallel();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline$StatelessOp != java.util.stream.LongPipeline$StatelessOp<E_IN> */
        @Override // j$.util.stream.LongPipeline, j$.util.stream.AbstractPipeline, j$.util.stream.BaseStream
        public /* bridge */ /* synthetic */ LongStream sequential() {
            return (LongStream) super.sequential();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<?, E_IN, ?> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline$StatelessOp != java.util.stream.LongPipeline$StatelessOp<E_IN> */
        public StatelessOp(AbstractPipeline<?, E_IN, ?> abstractPipeline, StreamShape inputShape, int opFlags) {
            super(abstractPipeline, opFlags);
            if ($assertionsDisabled || abstractPipeline.getOutputShape() == inputShape) {
                return;
            }
            throw new AssertionError();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline$StatelessOp != java.util.stream.LongPipeline$StatelessOp<E_IN> */
        @Override // j$.util.stream.AbstractPipeline
        final boolean opIsStateful() {
            return false;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class StatefulOp<E_IN> extends LongPipeline<E_IN> {
        static final /* synthetic */ boolean $assertionsDisabled = true;

        @Override // j$.util.stream.AbstractPipeline
        abstract <P_IN> Node<Long> opEvaluateParallel(PipelineHelper<Long> pipelineHelper, Spliterator<P_IN> spliterator, IntFunction<Long[]> intFunction);

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline$StatefulOp != java.util.stream.LongPipeline$StatefulOp<E_IN> */
        @Override // j$.util.stream.LongPipeline, j$.util.stream.AbstractPipeline, j$.util.stream.BaseStream
        public /* bridge */ /* synthetic */ LongStream parallel() {
            return (LongStream) super.parallel();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline$StatefulOp != java.util.stream.LongPipeline$StatefulOp<E_IN> */
        @Override // j$.util.stream.LongPipeline, j$.util.stream.AbstractPipeline, j$.util.stream.BaseStream
        public /* bridge */ /* synthetic */ LongStream sequential() {
            return (LongStream) super.sequential();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<?, E_IN, ?> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline$StatefulOp != java.util.stream.LongPipeline$StatefulOp<E_IN> */
        public StatefulOp(AbstractPipeline<?, E_IN, ?> abstractPipeline, StreamShape inputShape, int opFlags) {
            super(abstractPipeline, opFlags);
            if ($assertionsDisabled || abstractPipeline.getOutputShape() == inputShape) {
                return;
            }
            throw new AssertionError();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.LongPipeline$StatefulOp != java.util.stream.LongPipeline$StatefulOp<E_IN> */
        @Override // j$.util.stream.AbstractPipeline
        final boolean opIsStateful() {
            return true;
        }
    }
}
