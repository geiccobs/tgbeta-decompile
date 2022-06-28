package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.DoubleConsumer;
import j$.util.function.DoublePredicate;
import j$.util.function.IntConsumer;
import j$.util.function.IntPredicate;
import j$.util.function.LongConsumer;
import j$.util.function.LongPredicate;
import j$.util.function.Predicate;
import j$.util.function.Supplier;
import j$.util.stream.MatchOps;
import j$.util.stream.Sink;
/* loaded from: classes2.dex */
final class MatchOps {
    private MatchOps() {
    }

    /* loaded from: classes2.dex */
    public enum MatchKind {
        ANY(true, true),
        ALL(false, false),
        NONE(true, false);
        
        private final boolean shortCircuitResult;
        private final boolean stopOnPredicateMatches;

        MatchKind(boolean stopOnPredicateMatches, boolean shortCircuitResult) {
            this.stopOnPredicateMatches = stopOnPredicateMatches;
            this.shortCircuitResult = shortCircuitResult;
        }
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Predicate != java.util.function.Predicate<? super T> */
    public static <T> TerminalOp<T, Boolean> makeRef(final Predicate<? super T> predicate, final MatchKind matchKind) {
        predicate.getClass();
        matchKind.getClass();
        return new MatchOp(StreamShape.REFERENCE, matchKind, new Supplier() { // from class: j$.util.stream.MatchOps$$ExternalSyntheticLambda3
            @Override // j$.util.function.Supplier
            public final Object get() {
                return MatchOps.lambda$makeRef$0(MatchOps.MatchKind.this, predicate);
            }
        });
    }

    public static /* synthetic */ BooleanTerminalSink lambda$makeRef$0(MatchKind matchKind, Predicate predicate) {
        return new BooleanTerminalSink<T>(predicate) { // from class: j$.util.stream.MatchOps.1MatchSink
            final /* synthetic */ Predicate val$predicate;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(matchKind);
                MatchKind.this = matchKind;
                this.val$predicate = predicate;
            }

            @Override // j$.util.function.Consumer
            public void accept(T t) {
                if (!this.stop && this.val$predicate.test(t) == MatchKind.this.stopOnPredicateMatches) {
                    this.stop = true;
                    this.value = MatchKind.this.shortCircuitResult;
                }
            }
        };
    }

    public static TerminalOp<Integer, Boolean> makeInt(final IntPredicate predicate, final MatchKind matchKind) {
        predicate.getClass();
        matchKind.getClass();
        return new MatchOp(StreamShape.INT_VALUE, matchKind, new Supplier() { // from class: j$.util.stream.MatchOps$$ExternalSyntheticLambda1
            @Override // j$.util.function.Supplier
            public final Object get() {
                return MatchOps.lambda$makeInt$1(MatchOps.MatchKind.this, predicate);
            }
        });
    }

    /* renamed from: j$.util.stream.MatchOps$2MatchSink */
    /* loaded from: classes2.dex */
    public class C2MatchSink extends BooleanTerminalSink<Integer> implements Sink.OfInt {
        final /* synthetic */ MatchKind val$matchKind;
        final /* synthetic */ IntPredicate val$predicate;

        @Override // j$.util.stream.Sink.OfInt
        public /* synthetic */ void accept(Integer num) {
            Sink.OfInt.CC.$default$accept((Sink.OfInt) this, num);
        }

        @Override // j$.util.function.Consumer
        public /* bridge */ /* synthetic */ void accept(Object obj) {
            Sink.OfInt.CC.$default$accept(this, obj);
        }

        @Override // j$.util.function.IntConsumer
        public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
            return intConsumer.getClass();
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        C2MatchSink(MatchKind matchKind, IntPredicate intPredicate) {
            super(matchKind);
            this.val$matchKind = matchKind;
            this.val$predicate = intPredicate;
        }

        @Override // j$.util.stream.MatchOps.BooleanTerminalSink, j$.util.stream.Sink
        public void accept(int t) {
            if (!this.stop && this.val$predicate.test(t) == this.val$matchKind.stopOnPredicateMatches) {
                this.stop = true;
                this.value = this.val$matchKind.shortCircuitResult;
            }
        }
    }

    public static /* synthetic */ BooleanTerminalSink lambda$makeInt$1(MatchKind matchKind, IntPredicate predicate) {
        return new C2MatchSink(matchKind, predicate);
    }

    public static TerminalOp<Long, Boolean> makeLong(final LongPredicate predicate, final MatchKind matchKind) {
        predicate.getClass();
        matchKind.getClass();
        return new MatchOp(StreamShape.LONG_VALUE, matchKind, new Supplier() { // from class: j$.util.stream.MatchOps$$ExternalSyntheticLambda2
            @Override // j$.util.function.Supplier
            public final Object get() {
                return MatchOps.lambda$makeLong$2(MatchOps.MatchKind.this, predicate);
            }
        });
    }

    /* renamed from: j$.util.stream.MatchOps$3MatchSink */
    /* loaded from: classes2.dex */
    public class C3MatchSink extends BooleanTerminalSink<Long> implements Sink.OfLong {
        final /* synthetic */ MatchKind val$matchKind;
        final /* synthetic */ LongPredicate val$predicate;

        @Override // j$.util.stream.Sink.OfLong
        public /* synthetic */ void accept(Long l) {
            Sink.OfLong.CC.$default$accept((Sink.OfLong) this, l);
        }

        @Override // j$.util.function.Consumer
        public /* bridge */ /* synthetic */ void accept(Object obj) {
            Sink.OfLong.CC.$default$accept(this, obj);
        }

        @Override // j$.util.function.LongConsumer
        public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
            return longConsumer.getClass();
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        C3MatchSink(MatchKind matchKind, LongPredicate longPredicate) {
            super(matchKind);
            this.val$matchKind = matchKind;
            this.val$predicate = longPredicate;
        }

        @Override // j$.util.stream.MatchOps.BooleanTerminalSink, j$.util.stream.Sink
        public void accept(long t) {
            if (!this.stop && this.val$predicate.test(t) == this.val$matchKind.stopOnPredicateMatches) {
                this.stop = true;
                this.value = this.val$matchKind.shortCircuitResult;
            }
        }
    }

    public static /* synthetic */ BooleanTerminalSink lambda$makeLong$2(MatchKind matchKind, LongPredicate predicate) {
        return new C3MatchSink(matchKind, predicate);
    }

    public static TerminalOp<Double, Boolean> makeDouble(final DoublePredicate predicate, final MatchKind matchKind) {
        predicate.getClass();
        matchKind.getClass();
        return new MatchOp(StreamShape.DOUBLE_VALUE, matchKind, new Supplier() { // from class: j$.util.stream.MatchOps$$ExternalSyntheticLambda0
            @Override // j$.util.function.Supplier
            public final Object get() {
                return MatchOps.lambda$makeDouble$3(MatchOps.MatchKind.this, predicate);
            }
        });
    }

    /* renamed from: j$.util.stream.MatchOps$4MatchSink */
    /* loaded from: classes2.dex */
    public class C4MatchSink extends BooleanTerminalSink<Double> implements Sink.OfDouble {
        final /* synthetic */ MatchKind val$matchKind;
        final /* synthetic */ DoublePredicate val$predicate;

        @Override // j$.util.stream.Sink.OfDouble
        public /* synthetic */ void accept(Double d) {
            Sink.OfDouble.CC.$default$accept((Sink.OfDouble) this, d);
        }

        @Override // j$.util.function.Consumer
        public /* bridge */ /* synthetic */ void accept(Object obj) {
            Sink.OfDouble.CC.$default$accept(this, obj);
        }

        @Override // j$.util.function.DoubleConsumer
        public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
            return doubleConsumer.getClass();
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        C4MatchSink(MatchKind matchKind, DoublePredicate doublePredicate) {
            super(matchKind);
            this.val$matchKind = matchKind;
            this.val$predicate = doublePredicate;
        }

        @Override // j$.util.stream.MatchOps.BooleanTerminalSink, j$.util.stream.Sink, j$.util.function.DoubleConsumer
        public void accept(double t) {
            if (!this.stop && this.val$predicate.test(t) == this.val$matchKind.stopOnPredicateMatches) {
                this.stop = true;
                this.value = this.val$matchKind.shortCircuitResult;
            }
        }
    }

    public static /* synthetic */ BooleanTerminalSink lambda$makeDouble$3(MatchKind matchKind, DoublePredicate predicate) {
        return new C4MatchSink(matchKind, predicate);
    }

    /* loaded from: classes2.dex */
    public static final class MatchOp<T> implements TerminalOp<T, Boolean> {
        private final StreamShape inputShape;
        final MatchKind matchKind;
        final Supplier<BooleanTerminalSink<T>> sinkSupplier;

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<java.util.stream.MatchOps$BooleanTerminalSink<T>> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.MatchOps$MatchOp != java.util.stream.MatchOps$MatchOp<T> */
        MatchOp(StreamShape shape, MatchKind matchKind, Supplier<BooleanTerminalSink<T>> supplier) {
            this.inputShape = shape;
            this.matchKind = matchKind;
            this.sinkSupplier = supplier;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.MatchOps$MatchOp != java.util.stream.MatchOps$MatchOp<T> */
        @Override // j$.util.stream.TerminalOp
        public int getOpFlags() {
            return StreamOpFlag.IS_SHORT_CIRCUIT | StreamOpFlag.NOT_ORDERED;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.MatchOps$MatchOp != java.util.stream.MatchOps$MatchOp<T> */
        @Override // j$.util.stream.TerminalOp
        public StreamShape inputShape() {
            return this.inputShape;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<S> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.MatchOps$MatchOp != java.util.stream.MatchOps$MatchOp<T> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<T> */
        /* JADX WARN: Multi-variable type inference failed */
        @Override // j$.util.stream.TerminalOp
        public <S> Boolean evaluateSequential(PipelineHelper<T> pipelineHelper, Spliterator<S> spliterator) {
            return Boolean.valueOf(((BooleanTerminalSink) pipelineHelper.wrapAndCopyInto(this.sinkSupplier.get(), spliterator)).getAndClearState());
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<S> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.MatchOps$MatchOp != java.util.stream.MatchOps$MatchOp<T> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<T> */
        @Override // j$.util.stream.TerminalOp
        public <S> Boolean evaluateParallel(PipelineHelper<T> pipelineHelper, Spliterator<S> spliterator) {
            return (Boolean) new MatchTask(this, pipelineHelper, spliterator).invoke();
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class BooleanTerminalSink<T> implements Sink<T> {
        boolean stop;
        boolean value;

        @Override // j$.util.stream.Sink, j$.util.function.DoubleConsumer
        public /* synthetic */ void accept(double d) {
            Sink.CC.$default$accept(this, d);
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ void accept(int i) {
            Sink.CC.$default$accept((Sink) this, i);
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ void accept(long j) {
            Sink.CC.$default$accept((Sink) this, j);
        }

        @Override // j$.util.function.Consumer
        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return consumer.getClass();
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ void begin(long j) {
            Sink.CC.$default$begin(this, j);
        }

        @Override // j$.util.stream.Sink
        public /* synthetic */ void end() {
            Sink.CC.$default$end(this);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.MatchOps$BooleanTerminalSink != java.util.stream.MatchOps$BooleanTerminalSink<T> */
        BooleanTerminalSink(MatchKind matchKind) {
            this.value = !matchKind.shortCircuitResult;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.MatchOps$BooleanTerminalSink != java.util.stream.MatchOps$BooleanTerminalSink<T> */
        public boolean getAndClearState() {
            return this.value;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.MatchOps$BooleanTerminalSink != java.util.stream.MatchOps$BooleanTerminalSink<T> */
        @Override // j$.util.stream.Sink
        public boolean cancellationRequested() {
            return this.stop;
        }
    }

    /* loaded from: classes2.dex */
    public static final class MatchTask<P_IN, P_OUT> extends AbstractShortCircuitTask<P_IN, P_OUT, Boolean, MatchTask<P_IN, P_OUT>> {
        private final MatchOp<P_OUT> op;

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.MatchOps$MatchOp != java.util.stream.MatchOps$MatchOp<P_OUT> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.MatchOps$MatchTask != java.util.stream.MatchOps$MatchTask<P_IN, P_OUT> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<P_OUT> */
        MatchTask(MatchOp<P_OUT> matchOp, PipelineHelper<P_OUT> pipelineHelper, Spliterator<P_IN> spliterator) {
            super(pipelineHelper, spliterator);
            this.op = matchOp;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.MatchOps$MatchTask != java.util.stream.MatchOps$MatchTask<P_IN, P_OUT> */
        MatchTask(MatchTask<P_IN, P_OUT> matchTask, Spliterator<P_IN> spliterator) {
            super(matchTask, spliterator);
            this.op = matchTask.op;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.MatchOps$MatchTask != java.util.stream.MatchOps$MatchTask<P_IN, P_OUT> */
        @Override // j$.util.stream.AbstractTask
        public MatchTask<P_IN, P_OUT> makeChild(Spliterator<P_IN> spliterator) {
            return new MatchTask<>(this, spliterator);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.MatchOps$MatchTask != java.util.stream.MatchOps$MatchTask<P_IN, P_OUT> */
        @Override // j$.util.stream.AbstractTask
        public Boolean doLeaf() {
            boolean b = ((BooleanTerminalSink) this.helper.wrapAndCopyInto(this.op.sinkSupplier.get(), this.spliterator)).getAndClearState();
            if (b == this.op.matchKind.shortCircuitResult) {
                shortCircuit(Boolean.valueOf(b));
                return null;
            }
            return null;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.MatchOps$MatchTask != java.util.stream.MatchOps$MatchTask<P_IN, P_OUT> */
        @Override // j$.util.stream.AbstractShortCircuitTask
        public Boolean getEmptyResult() {
            return Boolean.valueOf(!this.op.matchKind.shortCircuitResult);
        }
    }
}
