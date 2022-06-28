package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.IntFunction;
import j$.util.function.Supplier;
import j$.util.stream.BaseStream;
import j$.util.stream.Node;
/* loaded from: classes2.dex */
public abstract class AbstractPipeline<E_IN, E_OUT, S extends BaseStream<E_OUT, S>> extends PipelineHelper<E_OUT> implements BaseStream<E_OUT, S> {
    static final /* synthetic */ boolean $assertionsDisabled = true;
    private static final String MSG_CONSUMED = "source already consumed or closed";
    private static final String MSG_STREAM_LINKED = "stream has already been operated upon or closed";
    private int combinedFlags;
    private int depth;
    private boolean linkedOrConsumed;
    private AbstractPipeline nextStage;
    private boolean parallel;
    private final AbstractPipeline previousStage;
    private boolean sourceAnyStateful;
    private Runnable sourceCloseAction;
    protected final int sourceOrOpFlags;
    private Spliterator<?> sourceSpliterator;
    private final AbstractPipeline sourceStage;
    private Supplier<? extends Spliterator<?>> sourceSupplier;

    abstract <P_IN> Node<E_OUT> evaluateToNode(PipelineHelper<E_OUT> pipelineHelper, Spliterator<P_IN> spliterator, boolean z, IntFunction<E_OUT[]> intFunction);

    abstract void forEachWithCancel(Spliterator<E_OUT> spliterator, Sink<E_OUT> sink);

    public abstract StreamShape getOutputShape();

    abstract Spliterator<E_OUT> lazySpliterator(Supplier<? extends Spliterator<E_OUT>> supplier);

    @Override // j$.util.stream.PipelineHelper
    public abstract Node.Builder<E_OUT> makeNodeBuilder(long j, IntFunction<E_OUT[]> intFunction);

    abstract boolean opIsStateful();

    public abstract Sink<E_IN> opWrapSink(int i, Sink<E_OUT> sink);

    abstract <P_IN> Spliterator<E_OUT> wrap(PipelineHelper<E_OUT> pipelineHelper, Supplier<Spliterator<P_IN>> supplier, boolean z);

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<? extends java.util.Spliterator<?>> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<E_IN, E_OUT, S extends j$.util.stream.BaseStream<E_OUT, S>> */
    public AbstractPipeline(Supplier<? extends Spliterator<?>> supplier, int sourceFlags, boolean parallel) {
        this.previousStage = null;
        this.sourceSupplier = supplier;
        this.sourceStage = this;
        int i = StreamOpFlag.STREAM_MASK & sourceFlags;
        this.sourceOrOpFlags = i;
        this.combinedFlags = ((i << 1) ^ (-1)) & StreamOpFlag.INITIAL_OPS_VALUE;
        this.depth = 0;
        this.parallel = parallel;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<?> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<E_IN, E_OUT, S extends j$.util.stream.BaseStream<E_OUT, S>> */
    public AbstractPipeline(Spliterator<?> spliterator, int sourceFlags, boolean parallel) {
        this.previousStage = null;
        this.sourceSpliterator = spliterator;
        this.sourceStage = this;
        int i = StreamOpFlag.STREAM_MASK & sourceFlags;
        this.sourceOrOpFlags = i;
        this.combinedFlags = ((i << 1) ^ (-1)) & StreamOpFlag.INITIAL_OPS_VALUE;
        this.depth = 0;
        this.parallel = parallel;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<?, E_IN, ?> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<E_IN, E_OUT, S extends j$.util.stream.BaseStream<E_OUT, S>> */
    public AbstractPipeline(AbstractPipeline<?, E_IN, ?> abstractPipeline, int opFlags) {
        if (abstractPipeline.linkedOrConsumed) {
            throw new IllegalStateException(MSG_STREAM_LINKED);
        }
        abstractPipeline.linkedOrConsumed = true;
        abstractPipeline.nextStage = this;
        this.previousStage = abstractPipeline;
        this.sourceOrOpFlags = StreamOpFlag.OP_MASK & opFlags;
        this.combinedFlags = StreamOpFlag.combineOpFlags(opFlags, abstractPipeline.combinedFlags);
        AbstractPipeline abstractPipeline2 = abstractPipeline.sourceStage;
        this.sourceStage = abstractPipeline2;
        if (opIsStateful()) {
            abstractPipeline2.sourceAnyStateful = true;
        }
        this.depth = abstractPipeline.depth + 1;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<E_IN, E_OUT, S extends j$.util.stream.BaseStream<E_OUT, S>> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.TerminalOp != java.util.stream.TerminalOp<E_OUT, R> */
    public final <R> R evaluate(TerminalOp<E_OUT, R> terminalOp) {
        if ($assertionsDisabled || getOutputShape() == terminalOp.inputShape()) {
            if (this.linkedOrConsumed) {
                throw new IllegalStateException(MSG_STREAM_LINKED);
            }
            this.linkedOrConsumed = true;
            if (isParallel()) {
                return terminalOp.evaluateParallel(this, sourceSpliterator(terminalOp.getOpFlags()));
            }
            return terminalOp.evaluateSequential(this, sourceSpliterator(terminalOp.getOpFlags()));
        }
        throw new AssertionError();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.IntFunction != java.util.function.IntFunction<E_OUT[]> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<E_IN, E_OUT, S extends j$.util.stream.BaseStream<E_OUT, S>> */
    public final Node<E_OUT> evaluateToArrayNode(IntFunction<E_OUT[]> intFunction) {
        if (this.linkedOrConsumed) {
            throw new IllegalStateException(MSG_STREAM_LINKED);
        }
        this.linkedOrConsumed = true;
        if (!isParallel() || this.previousStage == null || !opIsStateful()) {
            return evaluate(sourceSpliterator(0), true, intFunction);
        }
        this.depth = 0;
        AbstractPipeline abstractPipeline = this.previousStage;
        return opEvaluateParallel(abstractPipeline, abstractPipeline.sourceSpliterator(0), intFunction);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<E_OUT> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<E_IN, E_OUT, S extends j$.util.stream.BaseStream<E_OUT, S>> */
    public final Spliterator<E_OUT> sourceStageSpliterator() {
        AbstractPipeline<E_IN, E_OUT, S> abstractPipeline = this.sourceStage;
        if (this != abstractPipeline) {
            throw new IllegalStateException();
        }
        if (this.linkedOrConsumed) {
            throw new IllegalStateException(MSG_STREAM_LINKED);
        }
        this.linkedOrConsumed = true;
        if (abstractPipeline.sourceSpliterator != null) {
            Spliterator<E_OUT> spliterator = (Spliterator<E_OUT>) abstractPipeline.sourceSpliterator;
            abstractPipeline.sourceSpliterator = null;
            return spliterator;
        }
        Supplier<? extends Spliterator<?>> supplier = abstractPipeline.sourceSupplier;
        if (supplier != null) {
            Spliterator<E_OUT> spliterator2 = (Spliterator) supplier.get();
            this.sourceStage.sourceSupplier = null;
            return spliterator2;
        }
        throw new IllegalStateException(MSG_CONSUMED);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<E_IN, E_OUT, S extends j$.util.stream.BaseStream<E_OUT, S>> */
    @Override // j$.util.stream.BaseStream
    public final S sequential() {
        this.sourceStage.parallel = false;
        return this;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<E_IN, E_OUT, S extends j$.util.stream.BaseStream<E_OUT, S>> */
    @Override // j$.util.stream.BaseStream
    public final S parallel() {
        this.sourceStage.parallel = true;
        return this;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<E_IN, E_OUT, S extends j$.util.stream.BaseStream<E_OUT, S>> */
    @Override // j$.util.stream.BaseStream, java.lang.AutoCloseable
    public void close() {
        this.linkedOrConsumed = true;
        this.sourceSupplier = null;
        this.sourceSpliterator = null;
        AbstractPipeline abstractPipeline = this.sourceStage;
        if (abstractPipeline.sourceCloseAction != null) {
            Runnable closeAction = abstractPipeline.sourceCloseAction;
            abstractPipeline.sourceCloseAction = null;
            closeAction.run();
        }
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<E_IN, E_OUT, S extends j$.util.stream.BaseStream<E_OUT, S>> */
    @Override // j$.util.stream.BaseStream
    public S onClose(Runnable closeHandler) {
        Runnable runnable;
        AbstractPipeline abstractPipeline = this.sourceStage;
        Runnable existingHandler = abstractPipeline.sourceCloseAction;
        if (existingHandler == null) {
            runnable = closeHandler;
        } else {
            runnable = Streams.composeWithExceptions(existingHandler, closeHandler);
        }
        abstractPipeline.sourceCloseAction = runnable;
        return this;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<E_OUT> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<java.util.Spliterator<E_OUT>> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<E_IN, E_OUT, S extends j$.util.stream.BaseStream<E_OUT, S>> */
    @Override // j$.util.stream.BaseStream
    public Spliterator<E_OUT> spliterator() {
        if (this.linkedOrConsumed) {
            throw new IllegalStateException(MSG_STREAM_LINKED);
        }
        this.linkedOrConsumed = true;
        AbstractPipeline<E_IN, E_OUT, S> abstractPipeline = this.sourceStage;
        if (this == abstractPipeline) {
            if (abstractPipeline.sourceSpliterator != null) {
                Spliterator<E_OUT> spliterator = (Spliterator<E_OUT>) abstractPipeline.sourceSpliterator;
                abstractPipeline.sourceSpliterator = null;
                return spliterator;
            } else if (abstractPipeline.sourceSupplier != null) {
                Supplier<? extends Spliterator<?>> supplier = abstractPipeline.sourceSupplier;
                abstractPipeline.sourceSupplier = null;
                return lazySpliterator(supplier);
            } else {
                throw new IllegalStateException(MSG_CONSUMED);
            }
        }
        return wrap(this, new Supplier() { // from class: j$.util.stream.AbstractPipeline$$ExternalSyntheticLambda2
            @Override // j$.util.function.Supplier
            public final Object get() {
                return AbstractPipeline.this.m103lambda$spliterator$0$javautilstreamAbstractPipeline();
            }
        }, isParallel());
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<E_IN, E_OUT, S extends j$.util.stream.BaseStream<E_OUT, S>> */
    /* renamed from: lambda$spliterator$0$java-util-stream-AbstractPipeline */
    public /* synthetic */ Spliterator m103lambda$spliterator$0$javautilstreamAbstractPipeline() {
        return sourceSpliterator(0);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<E_IN, E_OUT, S extends j$.util.stream.BaseStream<E_OUT, S>> */
    @Override // j$.util.stream.BaseStream
    public final boolean isParallel() {
        return this.sourceStage.parallel;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<E_IN, E_OUT, S extends j$.util.stream.BaseStream<E_OUT, S>> */
    final int getStreamFlags() {
        return StreamOpFlag.toStreamFlags(this.combinedFlags);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<?> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<E_IN, E_OUT, S extends j$.util.stream.BaseStream<E_OUT, S>> */
    private Spliterator<?> sourceSpliterator(int terminalFlags) {
        Spliterator<E_OUT> spliterator;
        int i;
        AbstractPipeline abstractPipeline = this.sourceStage;
        if (abstractPipeline.sourceSpliterator != null) {
            Spliterator spliterator2 = abstractPipeline.sourceSpliterator;
            abstractPipeline.sourceSpliterator = null;
            spliterator = spliterator2;
        } else {
            Supplier<? extends Spliterator<?>> supplier = abstractPipeline.sourceSupplier;
            if (supplier != null) {
                Spliterator spliterator3 = supplier.get();
                this.sourceStage.sourceSupplier = null;
                spliterator = spliterator3;
            } else {
                throw new IllegalStateException(MSG_CONSUMED);
            }
        }
        if (isParallel()) {
            AbstractPipeline abstractPipeline2 = this.sourceStage;
            if (abstractPipeline2.sourceAnyStateful) {
                int depth = 1;
                AbstractPipeline u = this.sourceStage;
                spliterator = spliterator;
                AbstractPipeline p = abstractPipeline2.nextStage;
                while (u != this) {
                    int thisOpFlags = p.sourceOrOpFlags;
                    if (p.opIsStateful()) {
                        depth = 0;
                        if (StreamOpFlag.SHORT_CIRCUIT.isKnown(thisOpFlags)) {
                            thisOpFlags &= StreamOpFlag.IS_SHORT_CIRCUIT ^ (-1);
                        }
                        spliterator = p.opEvaluateParallelLazy(u, spliterator);
                        if (spliterator.hasCharacteristics(64)) {
                            i = ((StreamOpFlag.NOT_SIZED ^ (-1)) & thisOpFlags) | StreamOpFlag.IS_SIZED;
                        } else {
                            i = ((StreamOpFlag.IS_SIZED ^ (-1)) & thisOpFlags) | StreamOpFlag.NOT_SIZED;
                        }
                        thisOpFlags = i;
                    }
                    p.depth = depth;
                    p.combinedFlags = StreamOpFlag.combineOpFlags(thisOpFlags, u.combinedFlags);
                    u = p;
                    depth++;
                    spliterator = spliterator;
                    p = p.nextStage;
                }
            }
        }
        if (terminalFlags != 0) {
            this.combinedFlags = StreamOpFlag.combineOpFlags(terminalFlags, this.combinedFlags);
        }
        return spliterator;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<E_IN, E_OUT, S extends j$.util.stream.BaseStream<E_OUT, S>> */
    @Override // j$.util.stream.PipelineHelper
    public final StreamShape getSourceShape() {
        AbstractPipeline p = this;
        while (p.depth > 0) {
            p = p.previousStage;
        }
        return p.getOutputShape();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<E_IN, E_OUT, S extends j$.util.stream.BaseStream<E_OUT, S>> */
    @Override // j$.util.stream.PipelineHelper
    public final <P_IN> long exactOutputSizeIfKnown(Spliterator<P_IN> spliterator) {
        if (StreamOpFlag.SIZED.isKnown(getStreamAndOpFlags())) {
            return spliterator.getExactSizeIfKnown();
        }
        return -1L;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<E_IN, E_OUT, S extends j$.util.stream.BaseStream<E_OUT, S>> */
    /* JADX WARN: Unknown type variable: P_IN in type: java.util.Spliterator<P_IN> */
    @Override // j$.util.stream.PipelineHelper
    public final Sink wrapAndCopyInto(Sink sink, Spliterator spliterator) {
        sink.getClass();
        copyInto(wrapSink(sink), spliterator);
        return sink;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<E_IN, E_OUT, S extends j$.util.stream.BaseStream<E_OUT, S>> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<P_IN> */
    @Override // j$.util.stream.PipelineHelper
    public final <P_IN> void copyInto(Sink<P_IN> sink, Spliterator<P_IN> spliterator) {
        sink.getClass();
        if (!StreamOpFlag.SHORT_CIRCUIT.isKnown(getStreamAndOpFlags())) {
            sink.begin(spliterator.getExactSizeIfKnown());
            spliterator.forEachRemaining(sink);
            sink.end();
            return;
        }
        copyIntoWithCancel(sink, spliterator);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<E_IN, E_OUT, S extends j$.util.stream.BaseStream<E_OUT, S>> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<P_IN> */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // j$.util.stream.PipelineHelper
    public final <P_IN> void copyIntoWithCancel(Sink<P_IN> sink, Spliterator<P_IN> spliterator) {
        AbstractPipeline p = this;
        while (p.depth > 0) {
            p = p.previousStage;
        }
        sink.begin(spliterator.getExactSizeIfKnown());
        p.forEachWithCancel(spliterator, sink);
        sink.end();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<E_IN, E_OUT, S extends j$.util.stream.BaseStream<E_OUT, S>> */
    @Override // j$.util.stream.PipelineHelper
    public final int getStreamAndOpFlags() {
        return this.combinedFlags;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<E_IN, E_OUT, S extends j$.util.stream.BaseStream<E_OUT, S>> */
    public final boolean isOrdered() {
        return StreamOpFlag.ORDERED.isKnown(this.combinedFlags);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<E_IN, E_OUT, S extends j$.util.stream.BaseStream<E_OUT, S>> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Sink != java.util.stream.Sink<E_OUT> */
    @Override // j$.util.stream.PipelineHelper
    public final <P_IN> Sink<P_IN> wrapSink(Sink<E_OUT> sink) {
        sink.getClass();
        AbstractPipeline p = this;
        Consumer consumer = sink;
        while (p.depth > 0) {
            p = p.previousStage;
            consumer = p.opWrapSink(p.previousStage.combinedFlags, (Sink) consumer);
        }
        return (Sink<P_IN>) consumer;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<E_IN, E_OUT, S extends j$.util.stream.BaseStream<E_OUT, S>> */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // j$.util.stream.PipelineHelper
    public final <P_IN> Spliterator<E_OUT> wrapSpliterator(final Spliterator<P_IN> spliterator) {
        if (this.depth == 0) {
            return spliterator;
        }
        return wrap(this, new Supplier() { // from class: j$.util.stream.AbstractPipeline$$ExternalSyntheticLambda1
            @Override // j$.util.function.Supplier
            public final Object get() {
                return AbstractPipeline.lambda$wrapSpliterator$1(Spliterator.this);
            }
        }, isParallel());
    }

    public static /* synthetic */ Spliterator lambda$wrapSpliterator$1(Spliterator sourceSpliterator) {
        return sourceSpliterator;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.IntFunction != java.util.function.IntFunction<E_OUT[]> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<E_IN, E_OUT, S extends j$.util.stream.BaseStream<E_OUT, S>> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Node$Builder != java.util.stream.Node$Builder<E_OUT> */
    @Override // j$.util.stream.PipelineHelper
    public final <P_IN> Node<E_OUT> evaluate(Spliterator<P_IN> spliterator, boolean flatten, IntFunction<E_OUT[]> intFunction) {
        if (isParallel()) {
            return evaluateToNode(this, spliterator, flatten, intFunction);
        }
        return ((Node.Builder) wrapAndCopyInto(makeNodeBuilder(exactOutputSizeIfKnown(spliterator), intFunction), spliterator)).build();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.IntFunction != java.util.function.IntFunction<E_OUT[]> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<E_IN, E_OUT, S extends j$.util.stream.BaseStream<E_OUT, S>> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<E_OUT> */
    <P_IN> Node<E_OUT> opEvaluateParallel(PipelineHelper<E_OUT> pipelineHelper, Spliterator<P_IN> spliterator, IntFunction<E_OUT[]> intFunction) {
        throw new UnsupportedOperationException("Parallel evaluation is not supported");
    }

    public static /* synthetic */ Object[] lambda$opEvaluateParallelLazy$2(int i) {
        return new Object[i];
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractPipeline != java.util.stream.AbstractPipeline<E_IN, E_OUT, S extends j$.util.stream.BaseStream<E_OUT, S>> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<E_OUT> */
    <P_IN> Spliterator<E_OUT> opEvaluateParallelLazy(PipelineHelper<E_OUT> pipelineHelper, Spliterator<P_IN> spliterator) {
        return opEvaluateParallel(pipelineHelper, spliterator, AbstractPipeline$$ExternalSyntheticLambda0.INSTANCE).spliterator();
    }
}
