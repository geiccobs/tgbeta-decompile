package j$.util.stream;

import j$.util.Spliterator;
import j$.util.concurrent.ConcurrentHashMap;
import j$.util.function.Consumer;
import j$.util.function.IntFunction;
import j$.util.stream.DistinctOps;
import j$.util.stream.ReferencePipeline;
import j$.util.stream.Sink;
import j$.util.stream.StreamSpliterators;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
/* loaded from: classes2.dex */
final class DistinctOps {
    private DistinctOps() {
    }

    /* renamed from: j$.util.stream.DistinctOps$1 */
    /* loaded from: classes2.dex */
    public class AnonymousClass1 extends ReferencePipeline.StatefulOp<T, T> {
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<?, T, ?> */
        AnonymousClass1(AbstractPipeline abstractPipeline, StreamShape inputShape, int opFlags) {
            super(abstractPipeline, inputShape, opFlags);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<T> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.TerminalOp != java.util.stream.TerminalOp<T, java.util.LinkedHashSet<T>> */
        <P_IN> Node<T> reduce(PipelineHelper<T> pipelineHelper, Spliterator<P_IN> spliterator) {
            return Nodes.node((Collection) ReduceOps.makeRef(DistinctOps$1$$ExternalSyntheticLambda3.INSTANCE, DistinctOps$1$$ExternalSyntheticLambda0.INSTANCE, DistinctOps$1$$ExternalSyntheticLambda1.INSTANCE).evaluateParallel(pipelineHelper, spliterator));
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.concurrent.ConcurrentHashMap != java.util.concurrent.ConcurrentHashMap<T, java.lang.Boolean> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.function.IntFunction != java.util.function.IntFunction<T[]> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<T> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.TerminalOp != java.util.stream.TerminalOp<T, java.lang.Void> */
        @Override // j$.util.stream.ReferencePipeline.StatefulOp, j$.util.stream.AbstractPipeline
        <P_IN> Node<T> opEvaluateParallel(PipelineHelper<T> pipelineHelper, Spliterator<P_IN> spliterator, IntFunction<T[]> intFunction) {
            if (StreamOpFlag.DISTINCT.isKnown(pipelineHelper.getStreamAndOpFlags())) {
                return pipelineHelper.evaluate(spliterator, false, intFunction);
            }
            if (StreamOpFlag.ORDERED.isKnown(pipelineHelper.getStreamAndOpFlags())) {
                return reduce(pipelineHelper, spliterator);
            }
            final AtomicBoolean seenNull = new AtomicBoolean(false);
            final ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
            ForEachOps.makeRef(new Consumer() { // from class: j$.util.stream.DistinctOps$1$$ExternalSyntheticLambda2
                @Override // j$.util.function.Consumer
                public final void accept(Object obj) {
                    DistinctOps.AnonymousClass1.lambda$opEvaluateParallel$0(seenNull, concurrentHashMap, obj);
                }

                @Override // j$.util.function.Consumer
                public /* synthetic */ Consumer andThen(Consumer consumer) {
                    return consumer.getClass();
                }
            }, false).evaluateParallel(pipelineHelper, spliterator);
            Collection keySet = concurrentHashMap.keySet();
            HashSet hashSet = keySet;
            if (seenNull.get()) {
                HashSet hashSet2 = new HashSet(keySet);
                hashSet2.add(null);
                hashSet = hashSet2;
            }
            return Nodes.node(hashSet);
        }

        public static /* synthetic */ void lambda$opEvaluateParallel$0(AtomicBoolean seenNull, ConcurrentHashMap map, Object t) {
            if (t == null) {
                seenNull.set(true);
            } else {
                map.putIfAbsent(t, Boolean.TRUE);
            }
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<T> */
        @Override // j$.util.stream.AbstractPipeline
        <P_IN> Spliterator<T> opEvaluateParallelLazy(PipelineHelper<T> pipelineHelper, Spliterator<P_IN> spliterator) {
            if (StreamOpFlag.DISTINCT.isKnown(pipelineHelper.getStreamAndOpFlags())) {
                return pipelineHelper.wrapSpliterator(spliterator);
            }
            if (StreamOpFlag.ORDERED.isKnown(pipelineHelper.getStreamAndOpFlags())) {
                return reduce(pipelineHelper, spliterator).spliterator();
            }
            return new StreamSpliterators.DistinctSpliterator(pipelineHelper.wrapSpliterator(spliterator));
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<T> */
        @Override // j$.util.stream.AbstractPipeline
        public Sink<T> opWrapSink(int flags, Sink<T> sink) {
            sink.getClass();
            if (StreamOpFlag.DISTINCT.isKnown(flags)) {
                return sink;
            }
            if (StreamOpFlag.SORTED.isKnown(flags)) {
                return new Sink.ChainedReference<T, T>(sink) { // from class: j$.util.stream.DistinctOps.1.1
                    T lastSeen;
                    boolean seenNull;

                    @Override // j$.util.stream.Sink.ChainedReference, j$.util.stream.Sink
                    public void begin(long size) {
                        this.seenNull = false;
                        this.lastSeen = null;
                        this.downstream.begin(-1L);
                    }

                    @Override // j$.util.stream.Sink.ChainedReference, j$.util.stream.Sink
                    public void end() {
                        this.seenNull = false;
                        this.lastSeen = null;
                        this.downstream.end();
                    }

                    /* JADX WARN: Multi-variable type inference failed */
                    @Override // j$.util.function.Consumer
                    public void accept(T t) {
                        if (t == 0) {
                            if (!this.seenNull) {
                                this.seenNull = true;
                                Consumer consumer = this.downstream;
                                this.lastSeen = null;
                                consumer.accept(null);
                                return;
                            }
                            return;
                        }
                        Object obj = this.lastSeen;
                        if (obj == null || !t.equals(obj)) {
                            Consumer consumer2 = this.downstream;
                            this.lastSeen = t;
                            consumer2.accept(t);
                        }
                    }
                };
            }
            return new Sink.ChainedReference<T, T>(sink) { // from class: j$.util.stream.DistinctOps.1.2
                Set<T> seen;

                @Override // j$.util.stream.Sink.ChainedReference, j$.util.stream.Sink
                public void begin(long size) {
                    this.seen = new HashSet();
                    this.downstream.begin(-1L);
                }

                @Override // j$.util.stream.Sink.ChainedReference, j$.util.stream.Sink
                public void end() {
                    this.seen = null;
                    this.downstream.end();
                }

                @Override // j$.util.function.Consumer
                public void accept(T t) {
                    if (!this.seen.contains(t)) {
                        this.seen.add(t);
                        this.downstream.accept(t);
                    }
                }
            };
        }
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<?, T, ?> */
    public static <T> ReferencePipeline<T, T> makeRef(AbstractPipeline<?, T, ?> abstractPipeline) {
        return new AnonymousClass1(abstractPipeline, StreamShape.REFERENCE, StreamOpFlag.IS_DISTINCT | StreamOpFlag.NOT_SIZED);
    }
}
