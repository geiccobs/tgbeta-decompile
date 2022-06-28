package j$.util.stream;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import j$.util.DoubleSummaryStatistics;
import j$.util.OptionalDouble;
import j$.util.Spliterator;
import j$.util.Spliterators;
import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.BinaryOperator;
import j$.util.function.DoubleBinaryOperator;
import j$.util.function.DoubleConsumer;
import j$.util.function.DoubleFunction;
import j$.util.function.DoublePredicate;
import j$.util.function.DoubleToIntFunction;
import j$.util.function.DoubleToLongFunction;
import j$.util.function.DoubleUnaryOperator;
import j$.util.function.Function;
import j$.util.function.IntFunction;
import j$.util.function.ObjDoubleConsumer;
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
public abstract class DoublePipeline<E_IN> extends AbstractPipeline<E_IN, Double, DoubleStream> implements DoubleStream {
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    @Override // j$.util.stream.AbstractPipeline, j$.util.stream.BaseStream
    public /* bridge */ /* synthetic */ DoubleStream parallel() {
        return (DoubleStream) super.parallel();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    @Override // j$.util.stream.AbstractPipeline, j$.util.stream.BaseStream
    public /* bridge */ /* synthetic */ DoubleStream sequential() {
        return (DoubleStream) super.sequential();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<? extends java.util.Spliterator<java.lang.Double>> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    DoublePipeline(Supplier<? extends Spliterator<Double>> supplier, int sourceFlags, boolean parallel) {
        super(supplier, sourceFlags, parallel);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<java.lang.Double> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    DoublePipeline(Spliterator<Double> spliterator, int sourceFlags, boolean parallel) {
        super(spliterator, sourceFlags, parallel);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<?, E_IN, ?> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    DoublePipeline(AbstractPipeline<?, E_IN, ?> abstractPipeline, int opFlags) {
        super(abstractPipeline, opFlags);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Double> */
    private static DoubleConsumer adapt(Sink<Double> sink) {
        if (sink instanceof DoubleConsumer) {
            return (DoubleConsumer) sink;
        }
        if (Tripwire.ENABLED) {
            Tripwire.trip(AbstractPipeline.class, "using DoubleStream.adapt(Sink<Double> s)");
        }
        sink.getClass();
        return new DoublePipeline$$ExternalSyntheticLambda6(sink);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<java.lang.Double> */
    public static Spliterator.OfDouble adapt(Spliterator<Double> spliterator) {
        if (spliterator instanceof Spliterator.OfDouble) {
            return (Spliterator.OfDouble) spliterator;
        }
        if (Tripwire.ENABLED) {
            Tripwire.trip(AbstractPipeline.class, "using DoubleStream.adapt(Spliterator<Double> s)");
        }
        throw new UnsupportedOperationException("DoubleStream.adapt(Spliterator<Double> s)");
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    @Override // j$.util.stream.AbstractPipeline
    public final StreamShape getOutputShape() {
        return StreamShape.DOUBLE_VALUE;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.IntFunction != java.util.function.IntFunction<java.lang.Double[]> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<java.lang.Double> */
    @Override // j$.util.stream.AbstractPipeline
    final <P_IN> Node<Double> evaluateToNode(PipelineHelper<Double> pipelineHelper, Spliterator<P_IN> spliterator, boolean flattenTree, IntFunction<Double[]> intFunction) {
        return Nodes.collectDouble(pipelineHelper, spliterator, flattenTree);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<java.util.Spliterator<P_IN>> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<java.lang.Double> */
    @Override // j$.util.stream.AbstractPipeline
    final <P_IN> Spliterator<Double> wrap(PipelineHelper<Double> pipelineHelper, Supplier<Spliterator<P_IN>> supplier, boolean isParallel) {
        return new StreamSpliterators.DoubleWrappingSpliterator(pipelineHelper, supplier, isParallel);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<? extends java.util.Spliterator<java.lang.Double>> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    @Override // j$.util.stream.AbstractPipeline
    /* renamed from: lazySpliterator */
    public final Spliterator<Double> lazySpliterator2(Supplier<? extends Spliterator<Double>> supplier) {
        return new StreamSpliterators.DelegatingSpliterator.OfDouble(supplier);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<java.lang.Double> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Double> */
    @Override // j$.util.stream.AbstractPipeline
    final void forEachWithCancel(Spliterator<Double> spliterator, Sink<Double> sink) {
        Spliterator.OfDouble spl = adapt(spliterator);
        DoubleConsumer adaptedSink = adapt(sink);
        while (!sink.cancellationRequested() && spl.tryAdvance(adaptedSink)) {
        }
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.IntFunction != java.util.function.IntFunction<java.lang.Double[]> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    @Override // j$.util.stream.AbstractPipeline, j$.util.stream.PipelineHelper
    public final Node.Builder<Double> makeNodeBuilder(long exactSizeIfKnown, IntFunction<Double[]> intFunction) {
        return Nodes.doubleBuilder(exactSizeIfKnown);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    /* JADX WARN: Type inference failed for: r0v0, types: [j$.util.Spliterator$OfDouble] */
    @Override // j$.util.stream.BaseStream, j$.util.stream.DoubleStream
    /* renamed from: iterator */
    public final Iterator<Double> iterator2() {
        return Spliterators.iterator((Spliterator.OfDouble) spliterator2());
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    @Override // j$.util.stream.AbstractPipeline, j$.util.stream.BaseStream
    /* renamed from: spliterator */
    public final Spliterator<Double> spliterator2() {
        return adapt(super.spliterator());
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    @Override // j$.util.stream.DoubleStream
    public final Stream<Double> boxed() {
        return mapToObj(DoublePipeline$$ExternalSyntheticLambda7.INSTANCE);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    @Override // j$.util.stream.DoubleStream
    public final DoubleStream map(final DoubleUnaryOperator mapper) {
        mapper.getClass();
        return new StatelessOp<Double>(this, StreamShape.DOUBLE_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) { // from class: j$.util.stream.DoublePipeline.1
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Double> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<Double> opWrapSink(int flags, Sink<Double> sink) {
                return new Sink.ChainedDouble<Double>(sink) { // from class: j$.util.stream.DoublePipeline.1.1
                    @Override // j$.util.stream.Sink.OfDouble, j$.util.stream.Sink, j$.util.function.DoubleConsumer
                    public void accept(double t) {
                        this.downstream.accept(mapper.applyAsDouble(t));
                    }
                };
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.DoubleFunction != java.util.function.DoubleFunction<? extends U> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    @Override // j$.util.stream.DoubleStream
    public final <U> Stream<U> mapToObj(final DoubleFunction<? extends U> doubleFunction) {
        doubleFunction.getClass();
        return new ReferencePipeline.StatelessOp<Double, U>(this, StreamShape.DOUBLE_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) { // from class: j$.util.stream.DoublePipeline.2
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<U> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<Double> opWrapSink(int flags, Sink<U> sink) {
                return new Sink.ChainedDouble<U>(sink) { // from class: j$.util.stream.DoublePipeline.2.1
                    @Override // j$.util.stream.Sink.OfDouble, j$.util.stream.Sink, j$.util.function.DoubleConsumer
                    public void accept(double t) {
                        this.downstream.accept(doubleFunction.apply(t));
                    }
                };
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    @Override // j$.util.stream.DoubleStream
    public final IntStream mapToInt(final DoubleToIntFunction mapper) {
        mapper.getClass();
        return new IntPipeline.StatelessOp<Double>(this, StreamShape.DOUBLE_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) { // from class: j$.util.stream.DoublePipeline.3
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Integer> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<Double> opWrapSink(int flags, Sink<Integer> sink) {
                return new Sink.ChainedDouble<Integer>(sink) { // from class: j$.util.stream.DoublePipeline.3.1
                    @Override // j$.util.stream.Sink.OfDouble, j$.util.stream.Sink, j$.util.function.DoubleConsumer
                    public void accept(double t) {
                        this.downstream.accept(mapper.applyAsInt(t));
                    }
                };
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    @Override // j$.util.stream.DoubleStream
    public final LongStream mapToLong(final DoubleToLongFunction mapper) {
        mapper.getClass();
        return new LongPipeline.StatelessOp<Double>(this, StreamShape.DOUBLE_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) { // from class: j$.util.stream.DoublePipeline.4
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Long> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<Double> opWrapSink(int flags, Sink<Long> sink) {
                return new Sink.ChainedDouble<Long>(sink) { // from class: j$.util.stream.DoublePipeline.4.1
                    @Override // j$.util.stream.Sink.OfDouble, j$.util.stream.Sink, j$.util.function.DoubleConsumer
                    public void accept(double t) {
                        this.downstream.accept(mapper.applyAsLong(t));
                    }
                };
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.DoubleFunction != java.util.function.DoubleFunction<? extends java.util.stream.DoubleStream> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    @Override // j$.util.stream.DoubleStream
    public final DoubleStream flatMap(final DoubleFunction<? extends DoubleStream> doubleFunction) {
        return new StatelessOp<Double>(this, StreamShape.DOUBLE_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) { // from class: j$.util.stream.DoublePipeline.5

            /* renamed from: j$.util.stream.DoublePipeline$5$1 */
            /* loaded from: classes2.dex */
            public class AnonymousClass1 extends Sink.ChainedDouble<Double> {
                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<? super java.lang.Double> */
                AnonymousClass1(Sink sink) {
                    super(sink);
                    AnonymousClass5.this = this$1;
                }

                @Override // j$.util.stream.Sink.ChainedDouble, j$.util.stream.Sink
                public void begin(long size) {
                    this.downstream.begin(-1L);
                }

                @Override // j$.util.stream.Sink.OfDouble, j$.util.stream.Sink, j$.util.function.DoubleConsumer
                public void accept(double t) {
                    DoubleStream result = (DoubleStream) doubleFunction.apply(t);
                    if (result != null) {
                        try {
                            result.sequential().forEach(new DoubleConsumer() { // from class: j$.util.stream.DoublePipeline$5$1$$ExternalSyntheticLambda0
                                @Override // j$.util.function.DoubleConsumer
                                public final void accept(double d) {
                                    DoublePipeline.AnonymousClass5.AnonymousClass1.this.m104lambda$accept$0$javautilstreamDoublePipeline$5$1(d);
                                }

                                @Override // j$.util.function.DoubleConsumer
                                public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
                                    return doubleConsumer.getClass();
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

                /* renamed from: lambda$accept$0$java-util-stream-DoublePipeline$5$1 */
                public /* synthetic */ void m104lambda$accept$0$javautilstreamDoublePipeline$5$1(double i) {
                    this.downstream.accept(i);
                }
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Double> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<Double> opWrapSink(int flags, Sink<Double> sink) {
                return new AnonymousClass1(sink);
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    @Override // j$.util.stream.BaseStream
    public DoubleStream unordered() {
        if (!isOrdered()) {
            return this;
        }
        return new StatelessOp<Double>(this, StreamShape.DOUBLE_VALUE, StreamOpFlag.NOT_ORDERED) { // from class: j$.util.stream.DoublePipeline.6
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Double> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<Double> opWrapSink(int flags, Sink<Double> sink) {
                return sink;
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    @Override // j$.util.stream.DoubleStream
    public final DoubleStream filter(final DoublePredicate predicate) {
        predicate.getClass();
        return new StatelessOp<Double>(this, StreamShape.DOUBLE_VALUE, StreamOpFlag.NOT_SIZED) { // from class: j$.util.stream.DoublePipeline.7
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Double> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<Double> opWrapSink(int flags, Sink<Double> sink) {
                return new Sink.ChainedDouble<Double>(sink) { // from class: j$.util.stream.DoublePipeline.7.1
                    @Override // j$.util.stream.Sink.ChainedDouble, j$.util.stream.Sink
                    public void begin(long size) {
                        this.downstream.begin(-1L);
                    }

                    @Override // j$.util.stream.Sink.OfDouble, j$.util.stream.Sink, j$.util.function.DoubleConsumer
                    public void accept(double t) {
                        if (predicate.test(t)) {
                            this.downstream.accept(t);
                        }
                    }
                };
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    @Override // j$.util.stream.DoubleStream
    public final DoubleStream peek(final DoubleConsumer action) {
        action.getClass();
        return new StatelessOp<Double>(this, StreamShape.DOUBLE_VALUE, 0) { // from class: j$.util.stream.DoublePipeline.8
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Double> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<Double> opWrapSink(int flags, Sink<Double> sink) {
                return new Sink.ChainedDouble<Double>(sink) { // from class: j$.util.stream.DoublePipeline.8.1
                    @Override // j$.util.stream.Sink.OfDouble, j$.util.stream.Sink, j$.util.function.DoubleConsumer
                    public void accept(double t) {
                        action.accept(t);
                        this.downstream.accept(t);
                    }
                };
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    @Override // j$.util.stream.DoubleStream
    public final DoubleStream limit(long maxSize) {
        if (maxSize < 0) {
            throw new IllegalArgumentException(Long.toString(maxSize));
        }
        return SliceOps.makeDouble(this, 0L, maxSize);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    @Override // j$.util.stream.DoubleStream
    public final DoubleStream skip(long n) {
        if (n < 0) {
            throw new IllegalArgumentException(Long.toString(n));
        }
        if (n == 0) {
            return this;
        }
        return SliceOps.makeDouble(this, n, -1L);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    @Override // j$.util.stream.DoubleStream
    public final DoubleStream sorted() {
        return SortedOps.makeDouble(this);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    @Override // j$.util.stream.DoubleStream
    public final DoubleStream distinct() {
        return boxed().distinct().mapToDouble(DoublePipeline$$ExternalSyntheticLambda15.INSTANCE);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    @Override // j$.util.stream.DoubleStream
    public void forEach(DoubleConsumer consumer) {
        evaluate(ForEachOps.makeDouble(consumer, false));
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    @Override // j$.util.stream.DoubleStream
    public void forEachOrdered(DoubleConsumer consumer) {
        evaluate(ForEachOps.makeDouble(consumer, true));
    }

    public static /* synthetic */ double[] lambda$sum$1() {
        return new double[3];
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    @Override // j$.util.stream.DoubleStream
    public final double sum() {
        double[] summation = (double[]) collect(DoublePipeline$$ExternalSyntheticLambda14.INSTANCE, DoublePipeline$$ExternalSyntheticLambda12.INSTANCE, DoublePipeline$$ExternalSyntheticLambda2.INSTANCE);
        return Collectors.computeFinalSum(summation);
    }

    public static /* synthetic */ void lambda$sum$2(double[] ll, double d) {
        Collectors.sumWithCompensation(ll, d);
        ll[2] = ll[2] + d;
    }

    public static /* synthetic */ void lambda$sum$3(double[] ll, double[] rr) {
        Collectors.sumWithCompensation(ll, rr[0]);
        Collectors.sumWithCompensation(ll, rr[1]);
        ll[2] = ll[2] + rr[2];
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    @Override // j$.util.stream.DoubleStream
    public final OptionalDouble min() {
        return reduce(DoublePipeline$$ExternalSyntheticLambda5.INSTANCE);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    @Override // j$.util.stream.DoubleStream
    public final OptionalDouble max() {
        return reduce(DoublePipeline$$ExternalSyntheticLambda4.INSTANCE);
    }

    public static /* synthetic */ double[] lambda$average$4() {
        return new double[4];
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    @Override // j$.util.stream.DoubleStream
    public final OptionalDouble average() {
        double[] avg = (double[]) collect(DoublePipeline$$ExternalSyntheticLambda13.INSTANCE, DoublePipeline$$ExternalSyntheticLambda11.INSTANCE, DoublePipeline$$ExternalSyntheticLambda1.INSTANCE);
        if (avg[2] > FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
            return OptionalDouble.of(Collectors.computeFinalSum(avg) / avg[2]);
        }
        return OptionalDouble.empty();
    }

    public static /* synthetic */ void lambda$average$5(double[] ll, double d) {
        ll[2] = ll[2] + 1.0d;
        Collectors.sumWithCompensation(ll, d);
        ll[3] = ll[3] + d;
    }

    public static /* synthetic */ void lambda$average$6(double[] ll, double[] rr) {
        Collectors.sumWithCompensation(ll, rr[0]);
        Collectors.sumWithCompensation(ll, rr[1]);
        ll[2] = ll[2] + rr[2];
        ll[3] = ll[3] + rr[3];
    }

    public static /* synthetic */ long lambda$count$7(double e) {
        return 1L;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    @Override // j$.util.stream.DoubleStream
    public final long count() {
        return mapToLong(DoublePipeline$$ExternalSyntheticLambda8.INSTANCE).sum();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    @Override // j$.util.stream.DoubleStream
    public final DoubleSummaryStatistics summaryStatistics() {
        return (DoubleSummaryStatistics) collect(Collectors$$ExternalSyntheticLambda78.INSTANCE, DoublePipeline$$ExternalSyntheticLambda10.INSTANCE, DoublePipeline$$ExternalSyntheticLambda0.INSTANCE);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    @Override // j$.util.stream.DoubleStream
    public final double reduce(double identity, DoubleBinaryOperator op) {
        return ((Double) evaluate(ReduceOps.makeDouble(identity, op))).doubleValue();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    @Override // j$.util.stream.DoubleStream
    public final OptionalDouble reduce(DoubleBinaryOperator op) {
        return (OptionalDouble) evaluate(ReduceOps.makeDouble(op));
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BiConsumer != java.util.function.BiConsumer<R, R> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BinaryOperator != java.util.function.BinaryOperator<R> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.ObjDoubleConsumer != java.util.function.ObjDoubleConsumer<R> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<R> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    @Override // j$.util.stream.DoubleStream
    public final <R> R collect(Supplier<R> supplier, ObjDoubleConsumer<R> objDoubleConsumer, final BiConsumer<R, R> biConsumer) {
        return (R) evaluate(ReduceOps.makeDouble(supplier, objDoubleConsumer, new BinaryOperator() { // from class: j$.util.stream.DoublePipeline$$ExternalSyntheticLambda3
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

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    @Override // j$.util.stream.DoubleStream
    public final boolean anyMatch(DoublePredicate predicate) {
        return ((Boolean) evaluate(MatchOps.makeDouble(predicate, MatchOps.MatchKind.ANY))).booleanValue();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    @Override // j$.util.stream.DoubleStream
    public final boolean allMatch(DoublePredicate predicate) {
        return ((Boolean) evaluate(MatchOps.makeDouble(predicate, MatchOps.MatchKind.ALL))).booleanValue();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    @Override // j$.util.stream.DoubleStream
    public final boolean noneMatch(DoublePredicate predicate) {
        return ((Boolean) evaluate(MatchOps.makeDouble(predicate, MatchOps.MatchKind.NONE))).booleanValue();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    @Override // j$.util.stream.DoubleStream
    public final OptionalDouble findFirst() {
        return (OptionalDouble) evaluate(FindOps.makeDouble(true));
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    @Override // j$.util.stream.DoubleStream
    public final OptionalDouble findAny() {
        return (OptionalDouble) evaluate(FindOps.makeDouble(false));
    }

    public static /* synthetic */ Double[] lambda$toArray$9(int x$0) {
        return new Double[x$0];
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline != java.util.stream.DoublePipeline<E_IN> */
    @Override // j$.util.stream.DoubleStream
    public final double[] toArray() {
        return Nodes.flattenDouble((Node.OfDouble) evaluateToArrayNode(DoublePipeline$$ExternalSyntheticLambda9.INSTANCE)).asPrimitiveArray();
    }

    /* loaded from: classes2.dex */
    public static class Head<E_IN> extends DoublePipeline<E_IN> {
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline$Head != java.util.stream.DoublePipeline$Head<E_IN> */
        @Override // j$.util.stream.DoublePipeline, j$.util.stream.AbstractPipeline, j$.util.stream.BaseStream
        public /* bridge */ /* synthetic */ DoubleStream parallel() {
            return (DoubleStream) super.parallel();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline$Head != java.util.stream.DoublePipeline$Head<E_IN> */
        @Override // j$.util.stream.DoublePipeline, j$.util.stream.AbstractPipeline, j$.util.stream.BaseStream
        public /* bridge */ /* synthetic */ DoubleStream sequential() {
            return (DoubleStream) super.sequential();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<? extends java.util.Spliterator<java.lang.Double>> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline$Head != java.util.stream.DoublePipeline$Head<E_IN> */
        public Head(Supplier<? extends Spliterator<Double>> supplier, int sourceFlags, boolean parallel) {
            super(supplier, sourceFlags, parallel);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<java.lang.Double> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline$Head != java.util.stream.DoublePipeline$Head<E_IN> */
        public Head(Spliterator<Double> spliterator, int sourceFlags, boolean parallel) {
            super(spliterator, sourceFlags, parallel);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline$Head != java.util.stream.DoublePipeline$Head<E_IN> */
        @Override // j$.util.stream.AbstractPipeline
        final boolean opIsStateful() {
            throw new UnsupportedOperationException();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline$Head != java.util.stream.DoublePipeline$Head<E_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Double> */
        @Override // j$.util.stream.AbstractPipeline
        public final Sink<E_IN> opWrapSink(int flags, Sink<Double> sink) {
            throw new UnsupportedOperationException();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline$Head != java.util.stream.DoublePipeline$Head<E_IN> */
        @Override // j$.util.stream.DoublePipeline, j$.util.stream.DoubleStream
        public void forEach(DoubleConsumer consumer) {
            if (!isParallel()) {
                DoublePipeline.adapt(sourceStageSpliterator()).forEachRemaining(consumer);
            } else {
                super.forEach(consumer);
            }
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline$Head != java.util.stream.DoublePipeline$Head<E_IN> */
        @Override // j$.util.stream.DoublePipeline, j$.util.stream.DoubleStream
        public void forEachOrdered(DoubleConsumer consumer) {
            if (!isParallel()) {
                DoublePipeline.adapt(sourceStageSpliterator()).forEachRemaining(consumer);
            } else {
                super.forEachOrdered(consumer);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static abstract class StatelessOp<E_IN> extends DoublePipeline<E_IN> {
        static final /* synthetic */ boolean $assertionsDisabled = true;

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline$StatelessOp != java.util.stream.DoublePipeline$StatelessOp<E_IN> */
        @Override // j$.util.stream.DoublePipeline, j$.util.stream.AbstractPipeline, j$.util.stream.BaseStream
        public /* bridge */ /* synthetic */ DoubleStream parallel() {
            return (DoubleStream) super.parallel();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline$StatelessOp != java.util.stream.DoublePipeline$StatelessOp<E_IN> */
        @Override // j$.util.stream.DoublePipeline, j$.util.stream.AbstractPipeline, j$.util.stream.BaseStream
        public /* bridge */ /* synthetic */ DoubleStream sequential() {
            return (DoubleStream) super.sequential();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<?, E_IN, ?> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline$StatelessOp != java.util.stream.DoublePipeline$StatelessOp<E_IN> */
        public StatelessOp(AbstractPipeline<?, E_IN, ?> abstractPipeline, StreamShape inputShape, int opFlags) {
            super(abstractPipeline, opFlags);
            if ($assertionsDisabled || abstractPipeline.getOutputShape() == inputShape) {
                return;
            }
            throw new AssertionError();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline$StatelessOp != java.util.stream.DoublePipeline$StatelessOp<E_IN> */
        @Override // j$.util.stream.AbstractPipeline
        final boolean opIsStateful() {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static abstract class StatefulOp<E_IN> extends DoublePipeline<E_IN> {
        static final /* synthetic */ boolean $assertionsDisabled = true;

        @Override // j$.util.stream.AbstractPipeline
        abstract <P_IN> Node<Double> opEvaluateParallel(PipelineHelper<Double> pipelineHelper, Spliterator<P_IN> spliterator, IntFunction<Double[]> intFunction);

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline$StatefulOp != java.util.stream.DoublePipeline$StatefulOp<E_IN> */
        @Override // j$.util.stream.DoublePipeline, j$.util.stream.AbstractPipeline, j$.util.stream.BaseStream
        public /* bridge */ /* synthetic */ DoubleStream parallel() {
            return (DoubleStream) super.parallel();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline$StatefulOp != java.util.stream.DoublePipeline$StatefulOp<E_IN> */
        @Override // j$.util.stream.DoublePipeline, j$.util.stream.AbstractPipeline, j$.util.stream.BaseStream
        public /* bridge */ /* synthetic */ DoubleStream sequential() {
            return (DoubleStream) super.sequential();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<?, E_IN, ?> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline$StatefulOp != java.util.stream.DoublePipeline$StatefulOp<E_IN> */
        public StatefulOp(AbstractPipeline<?, E_IN, ?> abstractPipeline, StreamShape inputShape, int opFlags) {
            super(abstractPipeline, opFlags);
            if ($assertionsDisabled || abstractPipeline.getOutputShape() == inputShape) {
                return;
            }
            throw new AssertionError();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.DoublePipeline$StatefulOp != java.util.stream.DoublePipeline$StatefulOp<E_IN> */
        @Override // j$.util.stream.AbstractPipeline
        final boolean opIsStateful() {
            return true;
        }
    }
}
