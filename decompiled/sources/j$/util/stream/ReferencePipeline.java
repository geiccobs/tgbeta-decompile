package j$.util.stream;

import j$.util.Optional;
import j$.util.Spliterator;
import j$.util.Spliterators;
import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.BinaryOperator;
import j$.util.function.Consumer;
import j$.util.function.DoubleConsumer;
import j$.util.function.Function;
import j$.util.function.IntConsumer;
import j$.util.function.IntFunction;
import j$.util.function.LongConsumer;
import j$.util.function.Predicate;
import j$.util.function.Supplier;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import j$.util.stream.Collector;
import j$.util.stream.DoublePipeline;
import j$.util.stream.IntPipeline;
import j$.util.stream.LongPipeline;
import j$.util.stream.MatchOps;
import j$.util.stream.Node;
import j$.util.stream.Sink;
import j$.util.stream.StreamSpliterators;
import java.util.Comparator;
import java.util.Iterator;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public abstract class ReferencePipeline<P_IN, P_OUT> extends AbstractPipeline<P_IN, P_OUT, Stream<P_OUT>> implements Stream<P_OUT> {
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<? extends java.util.Spliterator<?>> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    ReferencePipeline(Supplier<? extends Spliterator<?>> supplier, int sourceFlags, boolean parallel) {
        super(supplier, sourceFlags, parallel);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<?> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    ReferencePipeline(Spliterator<?> spliterator, int sourceFlags, boolean parallel) {
        super(spliterator, sourceFlags, parallel);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<?, P_IN, ?> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    ReferencePipeline(AbstractPipeline<?, P_IN, ?> abstractPipeline, int opFlags) {
        super(abstractPipeline, opFlags);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.AbstractPipeline
    public final StreamShape getOutputShape() {
        return StreamShape.REFERENCE;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.IntFunction != java.util.function.IntFunction<P_OUT[]> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<P_OUT> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.AbstractPipeline
    final <P_IN> Node<P_OUT> evaluateToNode(PipelineHelper<P_OUT> pipelineHelper, Spliterator<P_IN> spliterator, boolean flattenTree, IntFunction<P_OUT[]> intFunction) {
        return Nodes.collect(pipelineHelper, spliterator, flattenTree, intFunction);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<java.util.Spliterator<P_IN>> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<P_OUT> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.AbstractPipeline
    final <P_IN> Spliterator<P_OUT> wrap(PipelineHelper<P_OUT> pipelineHelper, Supplier<Spliterator<P_IN>> supplier, boolean isParallel) {
        return new StreamSpliterators.WrappingSpliterator(pipelineHelper, supplier, isParallel);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<? extends java.util.Spliterator<P_OUT>> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.AbstractPipeline
    final Spliterator<P_OUT> lazySpliterator(Supplier<? extends Spliterator<P_OUT>> supplier) {
        return new StreamSpliterators.DelegatingSpliterator(supplier);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_OUT> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<P_OUT> */
    @Override // j$.util.stream.AbstractPipeline
    final void forEachWithCancel(Spliterator<P_OUT> spliterator, Sink<P_OUT> sink) {
        while (!sink.cancellationRequested() && spliterator.tryAdvance(sink)) {
        }
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.IntFunction != java.util.function.IntFunction<P_OUT[]> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.AbstractPipeline, j$.util.stream.PipelineHelper
    public final Node.Builder<P_OUT> makeNodeBuilder(long exactSizeIfKnown, IntFunction<P_OUT[]> intFunction) {
        return Nodes.builder(exactSizeIfKnown, intFunction);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.BaseStream, j$.util.stream.DoubleStream
    public final Iterator<P_OUT> iterator() {
        return Spliterators.iterator(spliterator());
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.BaseStream
    public Stream<P_OUT> unordered() {
        if (!isOrdered()) {
            return this;
        }
        return new StatelessOp<P_OUT, P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_ORDERED) { // from class: j$.util.stream.ReferencePipeline.1
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<P_OUT> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<P_OUT> opWrapSink(int flags, Sink<P_OUT> sink) {
                return sink;
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Predicate != java.util.function.Predicate<? super P_OUT> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.Stream
    public final Stream<P_OUT> filter(final Predicate<? super P_OUT> predicate) {
        predicate.getClass();
        return new StatelessOp<P_OUT, P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SIZED) { // from class: j$.util.stream.ReferencePipeline.2
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<P_OUT> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<P_OUT> opWrapSink(int flags, Sink<P_OUT> sink) {
                return new Sink.ChainedReference<P_OUT, P_OUT>(sink) { // from class: j$.util.stream.ReferencePipeline.2.1
                    @Override // j$.util.stream.Sink.ChainedReference, j$.util.stream.Sink
                    public void begin(long size) {
                        this.downstream.begin(-1L);
                    }

                    @Override // j$.util.function.Consumer
                    public void accept(P_OUT u) {
                        if (predicate.test(u)) {
                            this.downstream.accept(u);
                        }
                    }
                };
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<? super P_OUT, ? extends R> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.Stream
    public final <R> Stream<R> map(final Function<? super P_OUT, ? extends R> function) {
        function.getClass();
        return new StatelessOp<P_OUT, R>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) { // from class: j$.util.stream.ReferencePipeline.3
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<R> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<P_OUT> opWrapSink(int flags, Sink<R> sink) {
                return new Sink.ChainedReference<P_OUT, R>(sink) { // from class: j$.util.stream.ReferencePipeline.3.1
                    @Override // j$.util.function.Consumer
                    public void accept(P_OUT u) {
                        this.downstream.accept(function.apply(u));
                    }
                };
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.ToIntFunction != java.util.function.ToIntFunction<? super P_OUT> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.Stream
    public final IntStream mapToInt(final ToIntFunction<? super P_OUT> toIntFunction) {
        toIntFunction.getClass();
        return new IntPipeline.StatelessOp<P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) { // from class: j$.util.stream.ReferencePipeline.4
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Integer> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<P_OUT> opWrapSink(int flags, Sink<Integer> sink) {
                return new Sink.ChainedReference<P_OUT, Integer>(sink) { // from class: j$.util.stream.ReferencePipeline.4.1
                    @Override // j$.util.function.Consumer
                    public void accept(P_OUT u) {
                        this.downstream.accept(toIntFunction.applyAsInt(u));
                    }
                };
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.ToLongFunction != java.util.function.ToLongFunction<? super P_OUT> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.Stream
    public final LongStream mapToLong(final ToLongFunction<? super P_OUT> toLongFunction) {
        toLongFunction.getClass();
        return new LongPipeline.StatelessOp<P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) { // from class: j$.util.stream.ReferencePipeline.5
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Long> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<P_OUT> opWrapSink(int flags, Sink<Long> sink) {
                return new Sink.ChainedReference<P_OUT, Long>(sink) { // from class: j$.util.stream.ReferencePipeline.5.1
                    @Override // j$.util.function.Consumer
                    public void accept(P_OUT u) {
                        this.downstream.accept(toLongFunction.applyAsLong(u));
                    }
                };
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.ToDoubleFunction != java.util.function.ToDoubleFunction<? super P_OUT> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.Stream
    public final DoubleStream mapToDouble(final ToDoubleFunction<? super P_OUT> toDoubleFunction) {
        toDoubleFunction.getClass();
        return new DoublePipeline.StatelessOp<P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) { // from class: j$.util.stream.ReferencePipeline.6
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Double> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<P_OUT> opWrapSink(int flags, Sink<Double> sink) {
                return new Sink.ChainedReference<P_OUT, Double>(sink) { // from class: j$.util.stream.ReferencePipeline.6.1
                    @Override // j$.util.function.Consumer
                    public void accept(P_OUT u) {
                        this.downstream.accept(toDoubleFunction.applyAsDouble(u));
                    }
                };
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<? super P_OUT, ? extends java.util.stream.Stream<? extends R>> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.Stream
    public final <R> Stream<R> flatMap(final Function<? super P_OUT, ? extends Stream<? extends R>> function) {
        function.getClass();
        return new StatelessOp<P_OUT, R>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) { // from class: j$.util.stream.ReferencePipeline.7
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<R> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<P_OUT> opWrapSink(int flags, Sink<R> sink) {
                return new Sink.ChainedReference<P_OUT, R>(sink) { // from class: j$.util.stream.ReferencePipeline.7.1
                    @Override // j$.util.stream.Sink.ChainedReference, j$.util.stream.Sink
                    public void begin(long size) {
                        this.downstream.begin(-1L);
                    }

                    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Stream != java.util.stream.Stream<? extends R> */
                    @Override // j$.util.function.Consumer
                    public void accept(P_OUT u) {
                        Stream stream = (Stream) function.apply(u);
                        if (stream != null) {
                            try {
                                stream.sequential().forEach(this.downstream);
                            } catch (Throwable th) {
                                if (stream != null) {
                                    try {
                                        stream.close();
                                    } catch (Throwable th2) {
                                    }
                                }
                                throw th;
                            }
                        }
                        if (stream != null) {
                            stream.close();
                        }
                    }
                };
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<? super P_OUT, ? extends java.util.stream.IntStream> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.Stream
    public final IntStream flatMapToInt(final Function<? super P_OUT, ? extends IntStream> function) {
        function.getClass();
        return new IntPipeline.StatelessOp<P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) { // from class: j$.util.stream.ReferencePipeline.8
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Integer> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<P_OUT> opWrapSink(int flags, Sink<Integer> sink) {
                return new Sink.ChainedReference<P_OUT, Integer>(sink) { // from class: j$.util.stream.ReferencePipeline.8.1
                    IntConsumer downstreamAsInt;

                    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<? super java.lang.Integer> */
                    {
                        AnonymousClass8.this = this;
                        Sink<? super E_OUT> sink2 = this.downstream;
                        sink2.getClass();
                        this.downstreamAsInt = new IntPipeline$$ExternalSyntheticLambda6(sink2);
                    }

                    @Override // j$.util.stream.Sink.ChainedReference, j$.util.stream.Sink
                    public void begin(long size) {
                        this.downstream.begin(-1L);
                    }

                    @Override // j$.util.function.Consumer
                    public void accept(P_OUT u) {
                        IntStream result = (IntStream) function.apply(u);
                        if (result != null) {
                            try {
                                result.sequential().forEach(this.downstreamAsInt);
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
                };
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<? super P_OUT, ? extends java.util.stream.DoubleStream> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.Stream
    public final DoubleStream flatMapToDouble(final Function<? super P_OUT, ? extends DoubleStream> function) {
        function.getClass();
        return new DoublePipeline.StatelessOp<P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) { // from class: j$.util.stream.ReferencePipeline.9
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Double> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<P_OUT> opWrapSink(int flags, Sink<Double> sink) {
                return new Sink.ChainedReference<P_OUT, Double>(sink) { // from class: j$.util.stream.ReferencePipeline.9.1
                    DoubleConsumer downstreamAsDouble;

                    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<? super java.lang.Double> */
                    {
                        AnonymousClass9.this = this;
                        Sink<? super E_OUT> sink2 = this.downstream;
                        sink2.getClass();
                        this.downstreamAsDouble = new DoublePipeline$$ExternalSyntheticLambda6(sink2);
                    }

                    @Override // j$.util.stream.Sink.ChainedReference, j$.util.stream.Sink
                    public void begin(long size) {
                        this.downstream.begin(-1L);
                    }

                    @Override // j$.util.function.Consumer
                    public void accept(P_OUT u) {
                        DoubleStream result = (DoubleStream) function.apply(u);
                        if (result != null) {
                            try {
                                result.sequential().forEach(this.downstreamAsDouble);
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
                };
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<? super P_OUT, ? extends java.util.stream.LongStream> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.Stream
    public final LongStream flatMapToLong(final Function<? super P_OUT, ? extends LongStream> function) {
        function.getClass();
        return new LongPipeline.StatelessOp<P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) { // from class: j$.util.stream.ReferencePipeline.10
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<java.lang.Long> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<P_OUT> opWrapSink(int flags, Sink<Long> sink) {
                return new Sink.ChainedReference<P_OUT, Long>(sink) { // from class: j$.util.stream.ReferencePipeline.10.1
                    LongConsumer downstreamAsLong;

                    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<? super java.lang.Long> */
                    {
                        AnonymousClass10.this = this;
                        Sink<? super E_OUT> sink2 = this.downstream;
                        sink2.getClass();
                        this.downstreamAsLong = new LongPipeline$$ExternalSyntheticLambda7(sink2);
                    }

                    @Override // j$.util.stream.Sink.ChainedReference, j$.util.stream.Sink
                    public void begin(long size) {
                        this.downstream.begin(-1L);
                    }

                    @Override // j$.util.function.Consumer
                    public void accept(P_OUT u) {
                        LongStream result = (LongStream) function.apply(u);
                        if (result != null) {
                            try {
                                result.sequential().forEach(this.downstreamAsLong);
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
                };
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<? super P_OUT> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.Stream
    public final Stream<P_OUT> peek(final Consumer<? super P_OUT> consumer) {
        consumer.getClass();
        return new StatelessOp<P_OUT, P_OUT>(this, StreamShape.REFERENCE, 0) { // from class: j$.util.stream.ReferencePipeline.11
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<P_OUT> */
            @Override // j$.util.stream.AbstractPipeline
            public Sink<P_OUT> opWrapSink(int flags, Sink<P_OUT> sink) {
                return new Sink.ChainedReference<P_OUT, P_OUT>(sink) { // from class: j$.util.stream.ReferencePipeline.11.1
                    @Override // j$.util.function.Consumer
                    public void accept(P_OUT u) {
                        consumer.accept(u);
                        this.downstream.accept(u);
                    }
                };
            }
        };
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.Stream
    public final Stream<P_OUT> distinct() {
        return DistinctOps.makeRef(this);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.Stream
    public final Stream<P_OUT> sorted() {
        return SortedOps.makeRef(this);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.Stream
    public final Stream<P_OUT> sorted(Comparator<? super P_OUT> comparator) {
        return SortedOps.makeRef(this, comparator);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.Stream
    public final Stream<P_OUT> limit(long maxSize) {
        if (maxSize < 0) {
            throw new IllegalArgumentException(Long.toString(maxSize));
        }
        return SliceOps.makeRef(this, 0L, maxSize);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.Stream
    public final Stream<P_OUT> skip(long n) {
        if (n < 0) {
            throw new IllegalArgumentException(Long.toString(n));
        }
        if (n == 0) {
            return this;
        }
        return SliceOps.makeRef(this, n, -1L);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<? super P_OUT> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.Stream
    public void forEach(Consumer<? super P_OUT> consumer) {
        evaluate(ForEachOps.makeRef(consumer, false));
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<? super P_OUT> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.Stream
    public void forEachOrdered(Consumer<? super P_OUT> consumer) {
        evaluate(ForEachOps.makeRef(consumer, true));
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.IntFunction != java.util.function.IntFunction<A[]> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // j$.util.stream.Stream
    public final <A> A[] toArray(IntFunction<A[]> intFunction) {
        return (A[]) Nodes.flatten(evaluateToArrayNode(intFunction), intFunction).asArray(intFunction);
    }

    public static /* synthetic */ Object[] lambda$toArray$0(int x$0) {
        return new Object[x$0];
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.Stream
    public final Object[] toArray() {
        return toArray(ReferencePipeline$$ExternalSyntheticLambda1.INSTANCE);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Predicate != java.util.function.Predicate<? super P_OUT> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.Stream
    public final boolean anyMatch(Predicate<? super P_OUT> predicate) {
        return ((Boolean) evaluate(MatchOps.makeRef(predicate, MatchOps.MatchKind.ANY))).booleanValue();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Predicate != java.util.function.Predicate<? super P_OUT> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.Stream
    public final boolean allMatch(Predicate<? super P_OUT> predicate) {
        return ((Boolean) evaluate(MatchOps.makeRef(predicate, MatchOps.MatchKind.ALL))).booleanValue();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Predicate != java.util.function.Predicate<? super P_OUT> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.Stream
    public final boolean noneMatch(Predicate<? super P_OUT> predicate) {
        return ((Boolean) evaluate(MatchOps.makeRef(predicate, MatchOps.MatchKind.NONE))).booleanValue();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.Stream
    public final Optional<P_OUT> findFirst() {
        return (Optional) evaluate(FindOps.makeRef(true));
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.Stream
    public final Optional<P_OUT> findAny() {
        return (Optional) evaluate(FindOps.makeRef(false));
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BinaryOperator != java.util.function.BinaryOperator<P_OUT> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.Stream
    public final P_OUT reduce(P_OUT identity, BinaryOperator<P_OUT> binaryOperator) {
        return (P_OUT) evaluate(ReduceOps.makeRef(identity, binaryOperator, binaryOperator));
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BinaryOperator != java.util.function.BinaryOperator<P_OUT> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.Stream
    public final Optional<P_OUT> reduce(BinaryOperator<P_OUT> binaryOperator) {
        return (Optional) evaluate(ReduceOps.makeRef(binaryOperator));
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BiFunction != java.util.function.BiFunction<R, ? super P_OUT, R> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BinaryOperator != java.util.function.BinaryOperator<R> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.Stream
    public final <R> R reduce(R identity, BiFunction<R, ? super P_OUT, R> biFunction, BinaryOperator<R> binaryOperator) {
        return (R) evaluate(ReduceOps.makeRef(identity, (BiFunction<R, ? super T, R>) biFunction, binaryOperator));
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BiConsumer != java.util.function.BiConsumer<A, ? super P_OUT> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Collector != java.util.stream.Collector<? super P_OUT, A, R> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.Stream
    public final <R, A> R collect(Collector<? super P_OUT, A, R> collector) {
        final A container;
        if (isParallel() && collector.characteristics().contains(Collector.Characteristics.CONCURRENT) && (!isOrdered() || collector.characteristics().contains(Collector.Characteristics.UNORDERED))) {
            container = collector.supplier().get();
            final BiConsumer<A, ? super P_OUT> accumulator = collector.accumulator();
            forEach(new Consumer() { // from class: j$.util.stream.ReferencePipeline$$ExternalSyntheticLambda0
                @Override // j$.util.function.Consumer
                public final void accept(Object obj) {
                    BiConsumer.this.accept(container, obj);
                }

                @Override // j$.util.function.Consumer
                public /* synthetic */ Consumer andThen(Consumer consumer) {
                    return consumer.getClass();
                }
            });
        } else {
            container = (A) evaluate(ReduceOps.makeRef(collector));
        }
        if (collector.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH)) {
            return container;
        }
        return collector.finisher().apply(container);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BiConsumer != java.util.function.BiConsumer<R, ? super P_OUT> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BiConsumer != java.util.function.BiConsumer<R, R> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<R> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.Stream
    public final <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super P_OUT> biConsumer, BiConsumer<R, R> biConsumer2) {
        return (R) evaluate(ReduceOps.makeRef(supplier, biConsumer, biConsumer2));
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.Stream
    public final Optional<P_OUT> max(Comparator<? super P_OUT> comparator) {
        return reduce(BinaryOperator.CC.maxBy(comparator));
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.Stream
    public final Optional<P_OUT> min(Comparator<? super P_OUT> comparator) {
        return reduce(BinaryOperator.CC.minBy(comparator));
    }

    public static /* synthetic */ long lambda$count$2(Object e) {
        return 1L;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline != java.util.stream.ReferencePipeline<P_IN, P_OUT> */
    @Override // j$.util.stream.Stream
    public final long count() {
        return mapToLong(ReferencePipeline$$ExternalSyntheticLambda2.INSTANCE).sum();
    }

    /* loaded from: classes2.dex */
    public static class Head<E_IN, E_OUT> extends ReferencePipeline<E_IN, E_OUT> {
        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<? extends java.util.Spliterator<?>> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline$Head != java.util.stream.ReferencePipeline$Head<E_IN, E_OUT> */
        public Head(Supplier<? extends Spliterator<?>> supplier, int sourceFlags, boolean parallel) {
            super(supplier, sourceFlags, parallel);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<?> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline$Head != java.util.stream.ReferencePipeline$Head<E_IN, E_OUT> */
        public Head(Spliterator<?> spliterator, int sourceFlags, boolean parallel) {
            super(spliterator, sourceFlags, parallel);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline$Head != java.util.stream.ReferencePipeline$Head<E_IN, E_OUT> */
        @Override // j$.util.stream.AbstractPipeline
        final boolean opIsStateful() {
            throw new UnsupportedOperationException();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline$Head != java.util.stream.ReferencePipeline$Head<E_IN, E_OUT> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<E_OUT> */
        @Override // j$.util.stream.AbstractPipeline
        public final Sink<E_IN> opWrapSink(int flags, Sink<E_OUT> sink) {
            throw new UnsupportedOperationException();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<? super E_OUT> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline$Head != java.util.stream.ReferencePipeline$Head<E_IN, E_OUT> */
        /* JADX WARN: Multi-variable type inference failed */
        @Override // j$.util.stream.ReferencePipeline, j$.util.stream.Stream
        public void forEach(Consumer<? super E_OUT> consumer) {
            if (!isParallel()) {
                sourceStageSpliterator().forEachRemaining(consumer);
            } else {
                super.forEach(consumer);
            }
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<? super E_OUT> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline$Head != java.util.stream.ReferencePipeline$Head<E_IN, E_OUT> */
        /* JADX WARN: Multi-variable type inference failed */
        @Override // j$.util.stream.ReferencePipeline, j$.util.stream.Stream
        public void forEachOrdered(Consumer<? super E_OUT> consumer) {
            if (!isParallel()) {
                sourceStageSpliterator().forEachRemaining(consumer);
            } else {
                super.forEachOrdered(consumer);
            }
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class StatelessOp<E_IN, E_OUT> extends ReferencePipeline<E_IN, E_OUT> {
        static final /* synthetic */ boolean $assertionsDisabled = true;

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<?, E_IN, ?> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline$StatelessOp != java.util.stream.ReferencePipeline$StatelessOp<E_IN, E_OUT> */
        public StatelessOp(AbstractPipeline<?, E_IN, ?> abstractPipeline, StreamShape inputShape, int opFlags) {
            super(abstractPipeline, opFlags);
            if ($assertionsDisabled || abstractPipeline.getOutputShape() == inputShape) {
                return;
            }
            throw new AssertionError();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline$StatelessOp != java.util.stream.ReferencePipeline$StatelessOp<E_IN, E_OUT> */
        @Override // j$.util.stream.AbstractPipeline
        final boolean opIsStateful() {
            return false;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class StatefulOp<E_IN, E_OUT> extends ReferencePipeline<E_IN, E_OUT> {
        static final /* synthetic */ boolean $assertionsDisabled = true;

        @Override // j$.util.stream.AbstractPipeline
        abstract <P_IN> Node<E_OUT> opEvaluateParallel(PipelineHelper<E_OUT> pipelineHelper, Spliterator<P_IN> spliterator, IntFunction<E_OUT[]> intFunction);

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<?, E_IN, ?> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline$StatefulOp != java.util.stream.ReferencePipeline$StatefulOp<E_IN, E_OUT> */
        public StatefulOp(AbstractPipeline<?, E_IN, ?> abstractPipeline, StreamShape inputShape, int opFlags) {
            super(abstractPipeline, opFlags);
            if ($assertionsDisabled || abstractPipeline.getOutputShape() == inputShape) {
                return;
            }
            throw new AssertionError();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.ReferencePipeline$StatefulOp != java.util.stream.ReferencePipeline$StatefulOp<E_IN, E_OUT> */
        @Override // j$.util.stream.AbstractPipeline
        final boolean opIsStateful() {
            return true;
        }
    }
}
