package j$.util.stream;

import j$.util.Optional;
import j$.util.OptionalDouble;
import j$.util.OptionalInt;
import j$.util.OptionalLong;
import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.DoubleConsumer;
import j$.util.function.IntConsumer;
import j$.util.function.LongConsumer;
import j$.util.function.Predicate;
import j$.util.function.Supplier;
import j$.util.stream.Sink;
import java.util.concurrent.CountedCompleter;
/* loaded from: classes2.dex */
final class FindOps {
    private FindOps() {
    }

    public static <T> TerminalOp<T, Optional<T>> makeRef(boolean mustFindFirst) {
        return new FindOp(mustFindFirst, StreamShape.REFERENCE, Optional.empty(), FindOps$$ExternalSyntheticLambda0.INSTANCE, FindOps$$ExternalSyntheticLambda7.INSTANCE);
    }

    public static TerminalOp<Integer, OptionalInt> makeInt(boolean mustFindFirst) {
        return new FindOp(mustFindFirst, StreamShape.INT_VALUE, OptionalInt.empty(), FindOps$$ExternalSyntheticLambda2.INSTANCE, FindOps$$ExternalSyntheticLambda5.INSTANCE);
    }

    public static TerminalOp<Long, OptionalLong> makeLong(boolean mustFindFirst) {
        return new FindOp(mustFindFirst, StreamShape.LONG_VALUE, OptionalLong.empty(), FindOps$$ExternalSyntheticLambda3.INSTANCE, FindOps$$ExternalSyntheticLambda6.INSTANCE);
    }

    public static TerminalOp<Double, OptionalDouble> makeDouble(boolean mustFindFirst) {
        return new FindOp(mustFindFirst, StreamShape.DOUBLE_VALUE, OptionalDouble.empty(), FindOps$$ExternalSyntheticLambda1.INSTANCE, FindOps$$ExternalSyntheticLambda4.INSTANCE);
    }

    /* loaded from: classes2.dex */
    public static final class FindOp<T, O> implements TerminalOp<T, O> {
        final O emptyValue;
        final boolean mustFindFirst;
        final Predicate<O> presentPredicate;
        private final StreamShape shape;
        final Supplier<TerminalSink<T, O>> sinkSupplier;

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Predicate != java.util.function.Predicate<O> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<java.util.stream.TerminalSink<T, O>> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.FindOps$FindOp != java.util.stream.FindOps$FindOp<T, O> */
        FindOp(boolean mustFindFirst, StreamShape shape, O emptyValue, Predicate<O> predicate, Supplier<TerminalSink<T, O>> supplier) {
            this.mustFindFirst = mustFindFirst;
            this.shape = shape;
            this.emptyValue = emptyValue;
            this.presentPredicate = predicate;
            this.sinkSupplier = supplier;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.FindOps$FindOp != java.util.stream.FindOps$FindOp<T, O> */
        @Override // j$.util.stream.TerminalOp
        public int getOpFlags() {
            return StreamOpFlag.IS_SHORT_CIRCUIT | (this.mustFindFirst ? 0 : StreamOpFlag.NOT_ORDERED);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.FindOps$FindOp != java.util.stream.FindOps$FindOp<T, O> */
        @Override // j$.util.stream.TerminalOp
        public StreamShape inputShape() {
            return this.shape;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<S> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.FindOps$FindOp != java.util.stream.FindOps$FindOp<T, O> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<T> */
        /* JADX WARN: Multi-variable type inference failed */
        @Override // j$.util.stream.TerminalOp
        public <S> O evaluateSequential(PipelineHelper<T> pipelineHelper, Spliterator<S> spliterator) {
            O result = (O) ((TerminalSink) pipelineHelper.wrapAndCopyInto(this.sinkSupplier.get(), spliterator)).get();
            return result != null ? result : this.emptyValue;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.FindOps$FindOp != java.util.stream.FindOps$FindOp<T, O> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<T> */
        @Override // j$.util.stream.TerminalOp
        public <P_IN> O evaluateParallel(PipelineHelper<T> pipelineHelper, Spliterator<P_IN> spliterator) {
            return (O) new FindTask(this, pipelineHelper, spliterator).invoke();
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class FindSink<T, O> implements TerminalSink<T, O> {
        boolean hasValue;
        T value;

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

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.FindOps$FindSink != java.util.stream.FindOps$FindSink<T, O> */
        FindSink() {
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.FindOps$FindSink != java.util.stream.FindOps$FindSink<T, O> */
        @Override // j$.util.function.Consumer
        public void accept(T value) {
            if (!this.hasValue) {
                this.hasValue = true;
                this.value = value;
            }
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.FindOps$FindSink != java.util.stream.FindOps$FindSink<T, O> */
        @Override // j$.util.stream.Sink
        public boolean cancellationRequested() {
            return this.hasValue;
        }

        /* loaded from: classes2.dex */
        static final class OfRef<T> extends FindSink<T, Optional<T>> {
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.FindOps$FindSink$OfRef != java.util.stream.FindOps$FindSink$OfRef<T> */
            @Override // j$.util.function.Supplier
            public Optional<T> get() {
                if (this.hasValue) {
                    return Optional.of(this.value);
                }
                return null;
            }
        }

        /* loaded from: classes2.dex */
        static final class OfInt extends FindSink<Integer, OptionalInt> implements Sink.OfInt {
            @Override // j$.util.function.IntConsumer
            public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
                return intConsumer.getClass();
            }

            @Override // j$.util.stream.Sink.OfInt
            public /* bridge */ /* synthetic */ void accept(Integer num) {
                super.accept((OfInt) num);
            }

            @Override // j$.util.stream.FindOps.FindSink, j$.util.stream.Sink
            public void accept(int value) {
                accept((OfInt) Integer.valueOf(value));
            }

            @Override // j$.util.function.Supplier
            public OptionalInt get() {
                if (this.hasValue) {
                    return OptionalInt.of(((Integer) this.value).intValue());
                }
                return null;
            }
        }

        /* loaded from: classes2.dex */
        static final class OfLong extends FindSink<Long, OptionalLong> implements Sink.OfLong {
            @Override // j$.util.function.LongConsumer
            public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
                return longConsumer.getClass();
            }

            @Override // j$.util.stream.Sink.OfLong
            public /* bridge */ /* synthetic */ void accept(Long l) {
                super.accept((OfLong) l);
            }

            @Override // j$.util.stream.FindOps.FindSink, j$.util.stream.Sink
            public void accept(long value) {
                accept((OfLong) Long.valueOf(value));
            }

            @Override // j$.util.function.Supplier
            public OptionalLong get() {
                if (this.hasValue) {
                    return OptionalLong.of(((Long) this.value).longValue());
                }
                return null;
            }
        }

        /* loaded from: classes2.dex */
        static final class OfDouble extends FindSink<Double, OptionalDouble> implements Sink.OfDouble {
            @Override // j$.util.function.DoubleConsumer
            public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
                return doubleConsumer.getClass();
            }

            @Override // j$.util.stream.Sink.OfDouble
            public /* bridge */ /* synthetic */ void accept(Double d) {
                super.accept((OfDouble) d);
            }

            @Override // j$.util.stream.FindOps.FindSink, j$.util.stream.Sink, j$.util.function.DoubleConsumer
            public void accept(double value) {
                accept((OfDouble) Double.valueOf(value));
            }

            @Override // j$.util.function.Supplier
            public OptionalDouble get() {
                if (this.hasValue) {
                    return OptionalDouble.of(((Double) this.value).doubleValue());
                }
                return null;
            }
        }
    }

    /* loaded from: classes2.dex */
    private static final class FindTask<P_IN, P_OUT, O> extends AbstractShortCircuitTask<P_IN, P_OUT, O, FindTask<P_IN, P_OUT, O>> {
        private final FindOp<P_OUT, O> op;

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.FindOps$FindOp != java.util.stream.FindOps$FindOp<P_OUT, O> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.FindOps$FindTask != java.util.stream.FindOps$FindTask<P_IN, P_OUT, O> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<P_OUT> */
        FindTask(FindOp<P_OUT, O> findOp, PipelineHelper<P_OUT> pipelineHelper, Spliterator<P_IN> spliterator) {
            super(pipelineHelper, spliterator);
            this.op = findOp;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.FindOps$FindTask != java.util.stream.FindOps$FindTask<P_IN, P_OUT, O> */
        FindTask(FindTask<P_IN, P_OUT, O> findTask, Spliterator<P_IN> spliterator) {
            super(findTask, spliterator);
            this.op = findTask.op;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.FindOps$FindTask != java.util.stream.FindOps$FindTask<P_IN, P_OUT, O> */
        @Override // j$.util.stream.AbstractTask
        public FindTask<P_IN, P_OUT, O> makeChild(Spliterator<P_IN> spliterator) {
            return new FindTask<>(this, spliterator);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.FindOps$FindTask != java.util.stream.FindOps$FindTask<P_IN, P_OUT, O> */
        @Override // j$.util.stream.AbstractShortCircuitTask
        protected O getEmptyResult() {
            return this.op.emptyValue;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.FindOps$FindTask != java.util.stream.FindOps$FindTask<P_IN, P_OUT, O> */
        private void foundResult(O answer) {
            if (isLeftmostNode()) {
                shortCircuit(answer);
            } else {
                cancelLaterNodes();
            }
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.FindOps$FindTask != java.util.stream.FindOps$FindTask<P_IN, P_OUT, O> */
        @Override // j$.util.stream.AbstractTask
        public O doLeaf() {
            O result = (O) ((TerminalSink) this.helper.wrapAndCopyInto(this.op.sinkSupplier.get(), this.spliterator)).get();
            if (!this.op.mustFindFirst) {
                if (result != null) {
                    shortCircuit(result);
                }
                return null;
            } else if (result == null) {
                return null;
            } else {
                foundResult(result);
                return result;
            }
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.FindOps$FindTask != java.util.stream.FindOps$FindTask<P_IN, P_OUT, O> */
        @Override // j$.util.stream.AbstractTask, java.util.concurrent.CountedCompleter
        public void onCompletion(CountedCompleter<?> caller) {
            if (this.op.mustFindFirst) {
                FindTask findTask = (FindTask) this.leftChild;
                FindTask findTask2 = null;
                while (true) {
                    if (findTask != findTask2) {
                        O result = findTask.getLocalResult();
                        if (result == null || !this.op.presentPredicate.test(result)) {
                            findTask2 = findTask;
                            findTask = (FindTask) this.rightChild;
                        } else {
                            setLocalResult(result);
                            foundResult(result);
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
            super.onCompletion(caller);
        }
    }
}
