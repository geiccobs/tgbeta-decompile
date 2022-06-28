package j$.util.stream;

import j$.util.Spliterator;
import j$.util.concurrent.ConcurrentHashMap;
import j$.util.function.BooleanSupplier;
import j$.util.function.Consumer;
import j$.util.function.DoubleConsumer;
import j$.util.function.DoubleSupplier;
import j$.util.function.IntConsumer;
import j$.util.function.IntSupplier;
import j$.util.function.LongConsumer;
import j$.util.function.LongSupplier;
import j$.util.function.Supplier;
import j$.util.stream.Sink;
import j$.util.stream.SpinedBuffer;
import j$.util.stream.StreamSpliterators;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicLong;
/* loaded from: classes2.dex */
class StreamSpliterators {
    StreamSpliterators() {
    }

    /* loaded from: classes2.dex */
    public static abstract class AbstractWrappingSpliterator<P_IN, P_OUT, T_BUFFER extends AbstractSpinedBuffer> implements Spliterator<P_OUT> {
        T_BUFFER buffer;
        Sink<P_IN> bufferSink;
        boolean finished;
        final boolean isParallel;
        long nextToConsume;
        final PipelineHelper<P_OUT> ph;
        BooleanSupplier pusher;
        Spliterator<P_IN> spliterator;
        private Supplier<Spliterator<P_IN>> spliteratorSupplier;

        @Override // j$.util.Spliterator
        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Spliterator.CC.$default$forEachRemaining(this, consumer);
        }

        @Override // j$.util.Spliterator
        public /* synthetic */ boolean hasCharacteristics(int i) {
            return Spliterator.CC.$default$hasCharacteristics(this, i);
        }

        abstract void initPartialTraversalState();

        abstract AbstractWrappingSpliterator<P_IN, P_OUT, ?> wrap(Spliterator<P_IN> spliterator);

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<java.util.Spliterator<P_IN>> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<P_OUT> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$AbstractWrappingSpliterator != java.util.stream.StreamSpliterators$AbstractWrappingSpliterator<P_IN, P_OUT, T_BUFFER extends j$.util.stream.AbstractSpinedBuffer> */
        AbstractWrappingSpliterator(PipelineHelper<P_OUT> pipelineHelper, Supplier<Spliterator<P_IN>> supplier, boolean parallel) {
            this.ph = pipelineHelper;
            this.spliteratorSupplier = supplier;
            this.spliterator = null;
            this.isParallel = parallel;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<P_OUT> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$AbstractWrappingSpliterator != java.util.stream.StreamSpliterators$AbstractWrappingSpliterator<P_IN, P_OUT, T_BUFFER extends j$.util.stream.AbstractSpinedBuffer> */
        AbstractWrappingSpliterator(PipelineHelper<P_OUT> pipelineHelper, Spliterator<P_IN> spliterator, boolean parallel) {
            this.ph = pipelineHelper;
            this.spliteratorSupplier = null;
            this.spliterator = spliterator;
            this.isParallel = parallel;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$AbstractWrappingSpliterator != java.util.stream.StreamSpliterators$AbstractWrappingSpliterator<P_IN, P_OUT, T_BUFFER extends j$.util.stream.AbstractSpinedBuffer> */
        final void init() {
            if (this.spliterator == null) {
                this.spliterator = this.spliteratorSupplier.get();
                this.spliteratorSupplier = null;
            }
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$AbstractWrappingSpliterator != java.util.stream.StreamSpliterators$AbstractWrappingSpliterator<P_IN, P_OUT, T_BUFFER extends j$.util.stream.AbstractSpinedBuffer> */
        final boolean doAdvance() {
            T_BUFFER t_buffer = this.buffer;
            boolean z = false;
            if (t_buffer == null) {
                if (this.finished) {
                    return false;
                }
                init();
                initPartialTraversalState();
                this.nextToConsume = 0L;
                this.bufferSink.begin(this.spliterator.getExactSizeIfKnown());
                return fillBuffer();
            }
            long j = this.nextToConsume + 1;
            this.nextToConsume = j;
            if (j < t_buffer.count()) {
                z = true;
            }
            boolean hasNext = z;
            if (!hasNext) {
                this.nextToConsume = 0L;
                this.buffer.clear();
                return fillBuffer();
            }
            return hasNext;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$AbstractWrappingSpliterator != java.util.stream.StreamSpliterators$AbstractWrappingSpliterator<P_IN, P_OUT, T_BUFFER extends j$.util.stream.AbstractSpinedBuffer> */
        @Override // j$.util.Spliterator
        public Spliterator<P_OUT> trySplit() {
            if (!this.isParallel || this.finished) {
                return null;
            }
            init();
            Spliterator<P_IN> trySplit = this.spliterator.trySplit();
            if (trySplit != null) {
                return wrap(trySplit);
            }
            return null;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$AbstractWrappingSpliterator != java.util.stream.StreamSpliterators$AbstractWrappingSpliterator<P_IN, P_OUT, T_BUFFER extends j$.util.stream.AbstractSpinedBuffer> */
        private boolean fillBuffer() {
            while (this.buffer.count() == 0) {
                if (this.bufferSink.cancellationRequested() || !this.pusher.getAsBoolean()) {
                    if (this.finished) {
                        return false;
                    }
                    this.bufferSink.end();
                    this.finished = true;
                }
            }
            return true;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$AbstractWrappingSpliterator != java.util.stream.StreamSpliterators$AbstractWrappingSpliterator<P_IN, P_OUT, T_BUFFER extends j$.util.stream.AbstractSpinedBuffer> */
        @Override // j$.util.Spliterator
        public final long estimateSize() {
            init();
            return this.spliterator.estimateSize();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$AbstractWrappingSpliterator != java.util.stream.StreamSpliterators$AbstractWrappingSpliterator<P_IN, P_OUT, T_BUFFER extends j$.util.stream.AbstractSpinedBuffer> */
        @Override // j$.util.Spliterator
        public final long getExactSizeIfKnown() {
            init();
            if (StreamOpFlag.SIZED.isKnown(this.ph.getStreamAndOpFlags())) {
                return this.spliterator.getExactSizeIfKnown();
            }
            return -1L;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$AbstractWrappingSpliterator != java.util.stream.StreamSpliterators$AbstractWrappingSpliterator<P_IN, P_OUT, T_BUFFER extends j$.util.stream.AbstractSpinedBuffer> */
        @Override // j$.util.Spliterator
        public final int characteristics() {
            init();
            int c = StreamOpFlag.toCharacteristics(StreamOpFlag.toStreamFlags(this.ph.getStreamAndOpFlags()));
            if ((c & 64) != 0) {
                return (c & (-16449)) | (this.spliterator.characteristics() & 16448);
            }
            return c;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$AbstractWrappingSpliterator != java.util.stream.StreamSpliterators$AbstractWrappingSpliterator<P_IN, P_OUT, T_BUFFER extends j$.util.stream.AbstractSpinedBuffer> */
        @Override // j$.util.Spliterator
        public Comparator<? super P_OUT> getComparator() {
            if (!hasCharacteristics(4)) {
                throw new IllegalStateException();
            }
            return null;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$AbstractWrappingSpliterator != java.util.stream.StreamSpliterators$AbstractWrappingSpliterator<P_IN, P_OUT, T_BUFFER extends j$.util.stream.AbstractSpinedBuffer> */
        public final String toString() {
            return String.format("%s[%s]", getClass().getName(), this.spliterator);
        }
    }

    /* loaded from: classes2.dex */
    public static final class WrappingSpliterator<P_IN, P_OUT> extends AbstractWrappingSpliterator<P_IN, P_OUT, SpinedBuffer<P_OUT>> {
        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<java.util.Spliterator<P_IN>> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<P_OUT> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$WrappingSpliterator != java.util.stream.StreamSpliterators$WrappingSpliterator<P_IN, P_OUT> */
        public WrappingSpliterator(PipelineHelper<P_OUT> pipelineHelper, Supplier<Spliterator<P_IN>> supplier, boolean parallel) {
            super(pipelineHelper, supplier, parallel);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<P_OUT> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$WrappingSpliterator != java.util.stream.StreamSpliterators$WrappingSpliterator<P_IN, P_OUT> */
        WrappingSpliterator(PipelineHelper<P_OUT> pipelineHelper, Spliterator<P_IN> spliterator, boolean parallel) {
            super(pipelineHelper, spliterator, parallel);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$WrappingSpliterator != java.util.stream.StreamSpliterators$WrappingSpliterator<P_IN, P_OUT> */
        @Override // j$.util.stream.StreamSpliterators.AbstractWrappingSpliterator
        public WrappingSpliterator<P_IN, P_OUT> wrap(Spliterator<P_IN> spliterator) {
            return new WrappingSpliterator<>(this.ph, spliterator, this.isParallel);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SpinedBuffer != java.util.stream.SpinedBuffer<P_OUT> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$WrappingSpliterator != java.util.stream.StreamSpliterators$WrappingSpliterator<P_IN, P_OUT> */
        @Override // j$.util.stream.StreamSpliterators.AbstractWrappingSpliterator
        void initPartialTraversalState() {
            final SpinedBuffer spinedBuffer = new SpinedBuffer();
            this.buffer = spinedBuffer;
            PipelineHelper<P_OUT> pipelineHelper = this.ph;
            spinedBuffer.getClass();
            this.bufferSink = pipelineHelper.wrapSink(new Sink() { // from class: j$.util.stream.StreamSpliterators$WrappingSpliterator$$ExternalSyntheticLambda2
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
                public final void accept(Object obj) {
                    SpinedBuffer.this.accept(obj);
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
                public /* synthetic */ boolean cancellationRequested() {
                    return Sink.CC.$default$cancellationRequested(this);
                }

                @Override // j$.util.stream.Sink
                public /* synthetic */ void end() {
                    Sink.CC.$default$end(this);
                }
            });
            this.pusher = new BooleanSupplier() { // from class: j$.util.stream.StreamSpliterators$WrappingSpliterator$$ExternalSyntheticLambda0
                @Override // j$.util.function.BooleanSupplier
                public final boolean getAsBoolean() {
                    return StreamSpliterators.WrappingSpliterator.this.m111xf58cc34f();
                }
            };
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$WrappingSpliterator != java.util.stream.StreamSpliterators$WrappingSpliterator<P_IN, P_OUT> */
        /* renamed from: lambda$initPartialTraversalState$0$java-util-stream-StreamSpliterators$WrappingSpliterator */
        public /* synthetic */ boolean m111xf58cc34f() {
            return this.spliterator.tryAdvance(this.bufferSink);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<? super P_OUT> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$WrappingSpliterator != java.util.stream.StreamSpliterators$WrappingSpliterator<P_IN, P_OUT> */
        @Override // j$.util.Spliterator
        public boolean tryAdvance(Consumer<? super P_OUT> consumer) {
            consumer.getClass();
            boolean hasNext = doAdvance();
            if (hasNext) {
                consumer.accept((Object) ((SpinedBuffer) this.buffer).get(this.nextToConsume));
            }
            return hasNext;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<? super P_OUT> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$WrappingSpliterator != java.util.stream.StreamSpliterators$WrappingSpliterator<P_IN, P_OUT> */
        @Override // j$.util.stream.StreamSpliterators.AbstractWrappingSpliterator, j$.util.Spliterator
        public void forEachRemaining(final Consumer<? super P_OUT> consumer) {
            if (this.buffer == 0 && !this.finished) {
                consumer.getClass();
                init();
                PipelineHelper<P_OUT> pipelineHelper = this.ph;
                consumer.getClass();
                pipelineHelper.wrapAndCopyInto(new Sink() { // from class: j$.util.stream.StreamSpliterators$WrappingSpliterator$$ExternalSyntheticLambda1
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
                    public final void accept(Object obj) {
                        Consumer.this.accept(obj);
                    }

                    @Override // j$.util.function.Consumer
                    public /* synthetic */ Consumer andThen(Consumer consumer2) {
                        return consumer2.getClass();
                    }

                    @Override // j$.util.stream.Sink
                    public /* synthetic */ void begin(long j) {
                        Sink.CC.$default$begin(this, j);
                    }

                    @Override // j$.util.stream.Sink
                    public /* synthetic */ boolean cancellationRequested() {
                        return Sink.CC.$default$cancellationRequested(this);
                    }

                    @Override // j$.util.stream.Sink
                    public /* synthetic */ void end() {
                        Sink.CC.$default$end(this);
                    }
                }, this.spliterator);
                this.finished = true;
                return;
            }
            do {
            } while (tryAdvance(consumer));
        }
    }

    /* loaded from: classes2.dex */
    public static final class IntWrappingSpliterator<P_IN> extends AbstractWrappingSpliterator<P_IN, Integer, SpinedBuffer.OfInt> implements Spliterator.OfInt {
        @Override // j$.util.stream.StreamSpliterators.AbstractWrappingSpliterator, j$.util.Spliterator
        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Spliterator.OfInt.CC.$default$forEachRemaining((Spliterator.OfInt) this, consumer);
        }

        @Override // j$.util.Spliterator
        public /* synthetic */ boolean tryAdvance(Consumer consumer) {
            return Spliterator.OfInt.CC.$default$tryAdvance((Spliterator.OfInt) this, consumer);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<java.util.Spliterator<P_IN>> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<java.lang.Integer> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$IntWrappingSpliterator != java.util.stream.StreamSpliterators$IntWrappingSpliterator<P_IN> */
        public IntWrappingSpliterator(PipelineHelper<Integer> pipelineHelper, Supplier<Spliterator<P_IN>> supplier, boolean parallel) {
            super(pipelineHelper, supplier, parallel);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<java.lang.Integer> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$IntWrappingSpliterator != java.util.stream.StreamSpliterators$IntWrappingSpliterator<P_IN> */
        IntWrappingSpliterator(PipelineHelper<Integer> pipelineHelper, Spliterator<P_IN> spliterator, boolean parallel) {
            super(pipelineHelper, spliterator, parallel);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$IntWrappingSpliterator != java.util.stream.StreamSpliterators$IntWrappingSpliterator<P_IN> */
        @Override // j$.util.stream.StreamSpliterators.AbstractWrappingSpliterator
        AbstractWrappingSpliterator<P_IN, Integer, ?> wrap(Spliterator<P_IN> spliterator) {
            return new IntWrappingSpliterator((PipelineHelper<Integer>) this.ph, (Spliterator) spliterator, this.isParallel);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$IntWrappingSpliterator != java.util.stream.StreamSpliterators$IntWrappingSpliterator<P_IN> */
        @Override // j$.util.stream.StreamSpliterators.AbstractWrappingSpliterator
        void initPartialTraversalState() {
            final SpinedBuffer.OfInt b = new SpinedBuffer.OfInt();
            this.buffer = b;
            PipelineHelper<P_OUT> pipelineHelper = this.ph;
            b.getClass();
            this.bufferSink = pipelineHelper.wrapSink(new Sink.OfInt() { // from class: j$.util.stream.StreamSpliterators$IntWrappingSpliterator$$ExternalSyntheticLambda2
                @Override // j$.util.stream.Sink, j$.util.function.DoubleConsumer
                public /* synthetic */ void accept(double d) {
                    Sink.CC.$default$accept(this, d);
                }

                @Override // j$.util.stream.Sink.OfInt, j$.util.stream.Sink
                public final void accept(int i) {
                    SpinedBuffer.OfInt.this.accept(i);
                }

                @Override // j$.util.stream.Sink
                public /* synthetic */ void accept(long j) {
                    Sink.CC.$default$accept((Sink) this, j);
                }

                @Override // j$.util.stream.Sink.OfInt
                public /* synthetic */ void accept(Integer num) {
                    Sink.OfInt.CC.$default$accept((Sink.OfInt) this, num);
                }

                @Override // j$.util.function.Consumer
                public /* bridge */ /* synthetic */ void accept(Object obj) {
                    Sink.OfInt.CC.$default$accept(this, obj);
                }

                @Override // j$.util.function.Consumer
                public /* synthetic */ Consumer andThen(Consumer consumer) {
                    return consumer.getClass();
                }

                @Override // j$.util.function.IntConsumer
                public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
                    return intConsumer.getClass();
                }

                @Override // j$.util.stream.Sink
                public /* synthetic */ void begin(long j) {
                    Sink.CC.$default$begin(this, j);
                }

                @Override // j$.util.stream.Sink
                public /* synthetic */ boolean cancellationRequested() {
                    return Sink.CC.$default$cancellationRequested(this);
                }

                @Override // j$.util.stream.Sink
                public /* synthetic */ void end() {
                    Sink.CC.$default$end(this);
                }
            });
            this.pusher = new BooleanSupplier() { // from class: j$.util.stream.StreamSpliterators$IntWrappingSpliterator$$ExternalSyntheticLambda0
                @Override // j$.util.function.BooleanSupplier
                public final boolean getAsBoolean() {
                    return StreamSpliterators.IntWrappingSpliterator.this.m109x68714704();
                }
            };
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$IntWrappingSpliterator != java.util.stream.StreamSpliterators$IntWrappingSpliterator<P_IN> */
        /* renamed from: lambda$initPartialTraversalState$0$java-util-stream-StreamSpliterators$IntWrappingSpliterator */
        public /* synthetic */ boolean m109x68714704() {
            return this.spliterator.tryAdvance(this.bufferSink);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$IntWrappingSpliterator != java.util.stream.StreamSpliterators$IntWrappingSpliterator<P_IN> */
        @Override // j$.util.stream.StreamSpliterators.AbstractWrappingSpliterator, j$.util.Spliterator
        public Spliterator.OfInt trySplit() {
            return (Spliterator.OfInt) super.trySplit();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$IntWrappingSpliterator != java.util.stream.StreamSpliterators$IntWrappingSpliterator<P_IN> */
        @Override // j$.util.Spliterator.OfInt
        public boolean tryAdvance(IntConsumer consumer) {
            consumer.getClass();
            boolean hasNext = doAdvance();
            if (hasNext) {
                consumer.accept(((SpinedBuffer.OfInt) this.buffer).get(this.nextToConsume));
            }
            return hasNext;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$IntWrappingSpliterator != java.util.stream.StreamSpliterators$IntWrappingSpliterator<P_IN> */
        @Override // j$.util.Spliterator.OfInt
        public void forEachRemaining(final IntConsumer consumer) {
            if (this.buffer == 0 && !this.finished) {
                consumer.getClass();
                init();
                PipelineHelper<P_OUT> pipelineHelper = this.ph;
                consumer.getClass();
                pipelineHelper.wrapAndCopyInto(new Sink.OfInt() { // from class: j$.util.stream.StreamSpliterators$IntWrappingSpliterator$$ExternalSyntheticLambda1
                    @Override // j$.util.stream.Sink, j$.util.function.DoubleConsumer
                    public /* synthetic */ void accept(double d) {
                        Sink.CC.$default$accept(this, d);
                    }

                    @Override // j$.util.stream.Sink.OfInt, j$.util.stream.Sink
                    public final void accept(int i) {
                        IntConsumer.this.accept(i);
                    }

                    @Override // j$.util.stream.Sink
                    public /* synthetic */ void accept(long j) {
                        Sink.CC.$default$accept((Sink) this, j);
                    }

                    @Override // j$.util.stream.Sink.OfInt
                    public /* synthetic */ void accept(Integer num) {
                        Sink.OfInt.CC.$default$accept((Sink.OfInt) this, num);
                    }

                    @Override // j$.util.function.Consumer
                    public /* bridge */ /* synthetic */ void accept(Object obj) {
                        Sink.OfInt.CC.$default$accept(this, obj);
                    }

                    @Override // j$.util.function.Consumer
                    public /* synthetic */ Consumer andThen(Consumer consumer2) {
                        return consumer2.getClass();
                    }

                    @Override // j$.util.function.IntConsumer
                    public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
                        return intConsumer.getClass();
                    }

                    @Override // j$.util.stream.Sink
                    public /* synthetic */ void begin(long j) {
                        Sink.CC.$default$begin(this, j);
                    }

                    @Override // j$.util.stream.Sink
                    public /* synthetic */ boolean cancellationRequested() {
                        return Sink.CC.$default$cancellationRequested(this);
                    }

                    @Override // j$.util.stream.Sink
                    public /* synthetic */ void end() {
                        Sink.CC.$default$end(this);
                    }
                }, this.spliterator);
                this.finished = true;
                return;
            }
            do {
            } while (tryAdvance(consumer));
        }
    }

    /* loaded from: classes2.dex */
    public static final class LongWrappingSpliterator<P_IN> extends AbstractWrappingSpliterator<P_IN, Long, SpinedBuffer.OfLong> implements Spliterator.OfLong {
        @Override // j$.util.stream.StreamSpliterators.AbstractWrappingSpliterator, j$.util.Spliterator
        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Spliterator.OfLong.CC.$default$forEachRemaining((Spliterator.OfLong) this, consumer);
        }

        @Override // j$.util.Spliterator
        public /* synthetic */ boolean tryAdvance(Consumer consumer) {
            return Spliterator.OfLong.CC.$default$tryAdvance((Spliterator.OfLong) this, consumer);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<java.util.Spliterator<P_IN>> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<java.lang.Long> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$LongWrappingSpliterator != java.util.stream.StreamSpliterators$LongWrappingSpliterator<P_IN> */
        public LongWrappingSpliterator(PipelineHelper<Long> pipelineHelper, Supplier<Spliterator<P_IN>> supplier, boolean parallel) {
            super(pipelineHelper, supplier, parallel);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<java.lang.Long> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$LongWrappingSpliterator != java.util.stream.StreamSpliterators$LongWrappingSpliterator<P_IN> */
        LongWrappingSpliterator(PipelineHelper<Long> pipelineHelper, Spliterator<P_IN> spliterator, boolean parallel) {
            super(pipelineHelper, spliterator, parallel);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$LongWrappingSpliterator != java.util.stream.StreamSpliterators$LongWrappingSpliterator<P_IN> */
        @Override // j$.util.stream.StreamSpliterators.AbstractWrappingSpliterator
        AbstractWrappingSpliterator<P_IN, Long, ?> wrap(Spliterator<P_IN> spliterator) {
            return new LongWrappingSpliterator((PipelineHelper<Long>) this.ph, (Spliterator) spliterator, this.isParallel);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$LongWrappingSpliterator != java.util.stream.StreamSpliterators$LongWrappingSpliterator<P_IN> */
        @Override // j$.util.stream.StreamSpliterators.AbstractWrappingSpliterator
        void initPartialTraversalState() {
            final SpinedBuffer.OfLong b = new SpinedBuffer.OfLong();
            this.buffer = b;
            PipelineHelper<P_OUT> pipelineHelper = this.ph;
            b.getClass();
            this.bufferSink = pipelineHelper.wrapSink(new Sink.OfLong() { // from class: j$.util.stream.StreamSpliterators$LongWrappingSpliterator$$ExternalSyntheticLambda2
                @Override // j$.util.stream.Sink, j$.util.function.DoubleConsumer
                public /* synthetic */ void accept(double d) {
                    Sink.CC.$default$accept(this, d);
                }

                @Override // j$.util.stream.Sink
                public /* synthetic */ void accept(int i) {
                    Sink.CC.$default$accept((Sink) this, i);
                }

                @Override // j$.util.stream.Sink.OfLong, j$.util.stream.Sink
                public final void accept(long j) {
                    SpinedBuffer.OfLong.this.accept(j);
                }

                @Override // j$.util.stream.Sink.OfLong
                public /* synthetic */ void accept(Long l) {
                    Sink.OfLong.CC.$default$accept((Sink.OfLong) this, l);
                }

                @Override // j$.util.function.Consumer
                public /* bridge */ /* synthetic */ void accept(Object obj) {
                    Sink.OfLong.CC.$default$accept(this, obj);
                }

                @Override // j$.util.function.Consumer
                public /* synthetic */ Consumer andThen(Consumer consumer) {
                    return consumer.getClass();
                }

                @Override // j$.util.function.LongConsumer
                public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
                    return longConsumer.getClass();
                }

                @Override // j$.util.stream.Sink
                public /* synthetic */ void begin(long j) {
                    Sink.CC.$default$begin(this, j);
                }

                @Override // j$.util.stream.Sink
                public /* synthetic */ boolean cancellationRequested() {
                    return Sink.CC.$default$cancellationRequested(this);
                }

                @Override // j$.util.stream.Sink
                public /* synthetic */ void end() {
                    Sink.CC.$default$end(this);
                }
            });
            this.pusher = new BooleanSupplier() { // from class: j$.util.stream.StreamSpliterators$LongWrappingSpliterator$$ExternalSyntheticLambda0
                @Override // j$.util.function.BooleanSupplier
                public final boolean getAsBoolean() {
                    return StreamSpliterators.LongWrappingSpliterator.this.m110x44d1e433();
                }
            };
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$LongWrappingSpliterator != java.util.stream.StreamSpliterators$LongWrappingSpliterator<P_IN> */
        /* renamed from: lambda$initPartialTraversalState$0$java-util-stream-StreamSpliterators$LongWrappingSpliterator */
        public /* synthetic */ boolean m110x44d1e433() {
            return this.spliterator.tryAdvance(this.bufferSink);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$LongWrappingSpliterator != java.util.stream.StreamSpliterators$LongWrappingSpliterator<P_IN> */
        @Override // j$.util.stream.StreamSpliterators.AbstractWrappingSpliterator, j$.util.Spliterator
        public Spliterator.OfLong trySplit() {
            return (Spliterator.OfLong) super.trySplit();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$LongWrappingSpliterator != java.util.stream.StreamSpliterators$LongWrappingSpliterator<P_IN> */
        @Override // j$.util.Spliterator.OfLong
        public boolean tryAdvance(LongConsumer consumer) {
            consumer.getClass();
            boolean hasNext = doAdvance();
            if (hasNext) {
                consumer.accept(((SpinedBuffer.OfLong) this.buffer).get(this.nextToConsume));
            }
            return hasNext;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$LongWrappingSpliterator != java.util.stream.StreamSpliterators$LongWrappingSpliterator<P_IN> */
        @Override // j$.util.Spliterator.OfLong
        public void forEachRemaining(final LongConsumer consumer) {
            if (this.buffer == 0 && !this.finished) {
                consumer.getClass();
                init();
                PipelineHelper<P_OUT> pipelineHelper = this.ph;
                consumer.getClass();
                pipelineHelper.wrapAndCopyInto(new Sink.OfLong() { // from class: j$.util.stream.StreamSpliterators$LongWrappingSpliterator$$ExternalSyntheticLambda1
                    @Override // j$.util.stream.Sink, j$.util.function.DoubleConsumer
                    public /* synthetic */ void accept(double d) {
                        Sink.CC.$default$accept(this, d);
                    }

                    @Override // j$.util.stream.Sink
                    public /* synthetic */ void accept(int i) {
                        Sink.CC.$default$accept((Sink) this, i);
                    }

                    @Override // j$.util.stream.Sink.OfLong, j$.util.stream.Sink
                    public final void accept(long j) {
                        LongConsumer.this.accept(j);
                    }

                    @Override // j$.util.stream.Sink.OfLong
                    public /* synthetic */ void accept(Long l) {
                        Sink.OfLong.CC.$default$accept((Sink.OfLong) this, l);
                    }

                    @Override // j$.util.function.Consumer
                    public /* bridge */ /* synthetic */ void accept(Object obj) {
                        Sink.OfLong.CC.$default$accept(this, obj);
                    }

                    @Override // j$.util.function.Consumer
                    public /* synthetic */ Consumer andThen(Consumer consumer2) {
                        return consumer2.getClass();
                    }

                    @Override // j$.util.function.LongConsumer
                    public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
                        return longConsumer.getClass();
                    }

                    @Override // j$.util.stream.Sink
                    public /* synthetic */ void begin(long j) {
                        Sink.CC.$default$begin(this, j);
                    }

                    @Override // j$.util.stream.Sink
                    public /* synthetic */ boolean cancellationRequested() {
                        return Sink.CC.$default$cancellationRequested(this);
                    }

                    @Override // j$.util.stream.Sink
                    public /* synthetic */ void end() {
                        Sink.CC.$default$end(this);
                    }
                }, this.spliterator);
                this.finished = true;
                return;
            }
            do {
            } while (tryAdvance(consumer));
        }
    }

    /* loaded from: classes2.dex */
    public static final class DoubleWrappingSpliterator<P_IN> extends AbstractWrappingSpliterator<P_IN, Double, SpinedBuffer.OfDouble> implements Spliterator.OfDouble {
        @Override // j$.util.stream.StreamSpliterators.AbstractWrappingSpliterator, j$.util.Spliterator
        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Spliterator.OfDouble.CC.$default$forEachRemaining((Spliterator.OfDouble) this, consumer);
        }

        @Override // j$.util.Spliterator
        public /* synthetic */ boolean tryAdvance(Consumer consumer) {
            return Spliterator.OfDouble.CC.$default$tryAdvance((Spliterator.OfDouble) this, consumer);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<java.util.Spliterator<P_IN>> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<java.lang.Double> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$DoubleWrappingSpliterator != java.util.stream.StreamSpliterators$DoubleWrappingSpliterator<P_IN> */
        public DoubleWrappingSpliterator(PipelineHelper<Double> pipelineHelper, Supplier<Spliterator<P_IN>> supplier, boolean parallel) {
            super(pipelineHelper, supplier, parallel);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<java.lang.Double> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$DoubleWrappingSpliterator != java.util.stream.StreamSpliterators$DoubleWrappingSpliterator<P_IN> */
        DoubleWrappingSpliterator(PipelineHelper<Double> pipelineHelper, Spliterator<P_IN> spliterator, boolean parallel) {
            super(pipelineHelper, spliterator, parallel);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$DoubleWrappingSpliterator != java.util.stream.StreamSpliterators$DoubleWrappingSpliterator<P_IN> */
        @Override // j$.util.stream.StreamSpliterators.AbstractWrappingSpliterator
        AbstractWrappingSpliterator<P_IN, Double, ?> wrap(Spliterator<P_IN> spliterator) {
            return new DoubleWrappingSpliterator((PipelineHelper<Double>) this.ph, (Spliterator) spliterator, this.isParallel);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$DoubleWrappingSpliterator != java.util.stream.StreamSpliterators$DoubleWrappingSpliterator<P_IN> */
        @Override // j$.util.stream.StreamSpliterators.AbstractWrappingSpliterator
        void initPartialTraversalState() {
            final SpinedBuffer.OfDouble b = new SpinedBuffer.OfDouble();
            this.buffer = b;
            PipelineHelper<P_OUT> pipelineHelper = this.ph;
            b.getClass();
            this.bufferSink = pipelineHelper.wrapSink(new Sink.OfDouble() { // from class: j$.util.stream.StreamSpliterators$DoubleWrappingSpliterator$$ExternalSyntheticLambda2
                @Override // j$.util.stream.Sink.OfDouble, j$.util.stream.Sink, j$.util.function.DoubleConsumer
                public final void accept(double d) {
                    SpinedBuffer.OfDouble.this.accept(d);
                }

                @Override // j$.util.stream.Sink
                public /* synthetic */ void accept(int i) {
                    Sink.CC.$default$accept((Sink) this, i);
                }

                @Override // j$.util.stream.Sink
                public /* synthetic */ void accept(long j) {
                    Sink.CC.$default$accept((Sink) this, j);
                }

                @Override // j$.util.stream.Sink.OfDouble
                public /* synthetic */ void accept(Double d) {
                    Sink.OfDouble.CC.$default$accept((Sink.OfDouble) this, d);
                }

                @Override // j$.util.function.Consumer
                public /* bridge */ /* synthetic */ void accept(Object obj) {
                    Sink.OfDouble.CC.$default$accept(this, obj);
                }

                @Override // j$.util.function.Consumer
                public /* synthetic */ Consumer andThen(Consumer consumer) {
                    return consumer.getClass();
                }

                @Override // j$.util.function.DoubleConsumer
                public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
                    return doubleConsumer.getClass();
                }

                @Override // j$.util.stream.Sink
                public /* synthetic */ void begin(long j) {
                    Sink.CC.$default$begin(this, j);
                }

                @Override // j$.util.stream.Sink
                public /* synthetic */ boolean cancellationRequested() {
                    return Sink.CC.$default$cancellationRequested(this);
                }

                @Override // j$.util.stream.Sink
                public /* synthetic */ void end() {
                    Sink.CC.$default$end(this);
                }
            });
            this.pusher = new BooleanSupplier() { // from class: j$.util.stream.StreamSpliterators$DoubleWrappingSpliterator$$ExternalSyntheticLambda0
                @Override // j$.util.function.BooleanSupplier
                public final boolean getAsBoolean() {
                    return StreamSpliterators.DoubleWrappingSpliterator.this.m108xbf8f913e();
                }
            };
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$DoubleWrappingSpliterator != java.util.stream.StreamSpliterators$DoubleWrappingSpliterator<P_IN> */
        /* renamed from: lambda$initPartialTraversalState$0$java-util-stream-StreamSpliterators$DoubleWrappingSpliterator */
        public /* synthetic */ boolean m108xbf8f913e() {
            return this.spliterator.tryAdvance(this.bufferSink);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$DoubleWrappingSpliterator != java.util.stream.StreamSpliterators$DoubleWrappingSpliterator<P_IN> */
        @Override // j$.util.stream.StreamSpliterators.AbstractWrappingSpliterator, j$.util.Spliterator
        public Spliterator.OfDouble trySplit() {
            return (Spliterator.OfDouble) super.trySplit();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$DoubleWrappingSpliterator != java.util.stream.StreamSpliterators$DoubleWrappingSpliterator<P_IN> */
        @Override // j$.util.Spliterator.OfDouble
        public boolean tryAdvance(DoubleConsumer consumer) {
            consumer.getClass();
            boolean hasNext = doAdvance();
            if (hasNext) {
                consumer.accept(((SpinedBuffer.OfDouble) this.buffer).get(this.nextToConsume));
            }
            return hasNext;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$DoubleWrappingSpliterator != java.util.stream.StreamSpliterators$DoubleWrappingSpliterator<P_IN> */
        @Override // j$.util.Spliterator.OfDouble
        public void forEachRemaining(final DoubleConsumer consumer) {
            if (this.buffer == 0 && !this.finished) {
                consumer.getClass();
                init();
                PipelineHelper<P_OUT> pipelineHelper = this.ph;
                consumer.getClass();
                pipelineHelper.wrapAndCopyInto(new Sink.OfDouble() { // from class: j$.util.stream.StreamSpliterators$DoubleWrappingSpliterator$$ExternalSyntheticLambda1
                    @Override // j$.util.stream.Sink.OfDouble, j$.util.stream.Sink, j$.util.function.DoubleConsumer
                    public final void accept(double d) {
                        DoubleConsumer.this.accept(d);
                    }

                    @Override // j$.util.stream.Sink
                    public /* synthetic */ void accept(int i) {
                        Sink.CC.$default$accept((Sink) this, i);
                    }

                    @Override // j$.util.stream.Sink
                    public /* synthetic */ void accept(long j) {
                        Sink.CC.$default$accept((Sink) this, j);
                    }

                    @Override // j$.util.stream.Sink.OfDouble
                    public /* synthetic */ void accept(Double d) {
                        Sink.OfDouble.CC.$default$accept((Sink.OfDouble) this, d);
                    }

                    @Override // j$.util.function.Consumer
                    public /* bridge */ /* synthetic */ void accept(Object obj) {
                        Sink.OfDouble.CC.$default$accept(this, obj);
                    }

                    @Override // j$.util.function.Consumer
                    public /* synthetic */ Consumer andThen(Consumer consumer2) {
                        return consumer2.getClass();
                    }

                    @Override // j$.util.function.DoubleConsumer
                    public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
                        return doubleConsumer.getClass();
                    }

                    @Override // j$.util.stream.Sink
                    public /* synthetic */ void begin(long j) {
                        Sink.CC.$default$begin(this, j);
                    }

                    @Override // j$.util.stream.Sink
                    public /* synthetic */ boolean cancellationRequested() {
                        return Sink.CC.$default$cancellationRequested(this);
                    }

                    @Override // j$.util.stream.Sink
                    public /* synthetic */ void end() {
                        Sink.CC.$default$end(this);
                    }
                }, this.spliterator);
                this.finished = true;
                return;
            }
            do {
            } while (tryAdvance(consumer));
        }
    }

    /* loaded from: classes2.dex */
    public static class DelegatingSpliterator<T, T_SPLITR extends Spliterator<T>> implements Spliterator<T> {
        private T_SPLITR s;
        private final Supplier<? extends T_SPLITR> supplier;

        @Override // j$.util.Spliterator
        public /* synthetic */ boolean hasCharacteristics(int i) {
            return Spliterator.CC.$default$hasCharacteristics(this, i);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<? extends T_SPLITR extends j$.util.Spliterator<T>> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$DelegatingSpliterator != java.util.stream.StreamSpliterators$DelegatingSpliterator<T, T_SPLITR extends j$.util.Spliterator<T>> */
        public DelegatingSpliterator(Supplier<? extends T_SPLITR> supplier) {
            this.supplier = supplier;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$DelegatingSpliterator != java.util.stream.StreamSpliterators$DelegatingSpliterator<T, T_SPLITR extends j$.util.Spliterator<T>> */
        T_SPLITR get() {
            if (this.s == null) {
                this.s = this.supplier.get();
            }
            return this.s;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$DelegatingSpliterator != java.util.stream.StreamSpliterators$DelegatingSpliterator<T, T_SPLITR extends j$.util.Spliterator<T>> */
        @Override // j$.util.Spliterator
        public T_SPLITR trySplit() {
            return (T_SPLITR) get().trySplit();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<? super T> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$DelegatingSpliterator != java.util.stream.StreamSpliterators$DelegatingSpliterator<T, T_SPLITR extends j$.util.Spliterator<T>> */
        @Override // j$.util.Spliterator
        public boolean tryAdvance(Consumer<? super T> consumer) {
            return get().tryAdvance(consumer);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<? super T> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$DelegatingSpliterator != java.util.stream.StreamSpliterators$DelegatingSpliterator<T, T_SPLITR extends j$.util.Spliterator<T>> */
        @Override // j$.util.Spliterator
        public void forEachRemaining(Consumer<? super T> consumer) {
            get().forEachRemaining(consumer);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$DelegatingSpliterator != java.util.stream.StreamSpliterators$DelegatingSpliterator<T, T_SPLITR extends j$.util.Spliterator<T>> */
        @Override // j$.util.Spliterator
        public long estimateSize() {
            return get().estimateSize();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$DelegatingSpliterator != java.util.stream.StreamSpliterators$DelegatingSpliterator<T, T_SPLITR extends j$.util.Spliterator<T>> */
        @Override // j$.util.Spliterator
        public int characteristics() {
            return get().characteristics();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$DelegatingSpliterator != java.util.stream.StreamSpliterators$DelegatingSpliterator<T, T_SPLITR extends j$.util.Spliterator<T>> */
        @Override // j$.util.Spliterator
        public Comparator<? super T> getComparator() {
            return get().getComparator();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$DelegatingSpliterator != java.util.stream.StreamSpliterators$DelegatingSpliterator<T, T_SPLITR extends j$.util.Spliterator<T>> */
        @Override // j$.util.Spliterator
        public long getExactSizeIfKnown() {
            return get().getExactSizeIfKnown();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$DelegatingSpliterator != java.util.stream.StreamSpliterators$DelegatingSpliterator<T, T_SPLITR extends j$.util.Spliterator<T>> */
        public String toString() {
            return getClass().getName() + "[" + get() + "]";
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes2.dex */
        public static class OfPrimitive<T, T_CONS, T_SPLITR extends Spliterator.OfPrimitive<T, T_CONS, T_SPLITR>> extends DelegatingSpliterator<T, T_SPLITR> implements Spliterator.OfPrimitive<T, T_CONS, T_SPLITR> {
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$DelegatingSpliterator$OfPrimitive != java.util.stream.StreamSpliterators$DelegatingSpliterator$OfPrimitive<T, T_CONS, T_SPLITR extends j$.util.Spliterator$OfPrimitive<T, T_CONS, T_SPLITR>> */
            @Override // j$.util.stream.StreamSpliterators.DelegatingSpliterator, j$.util.Spliterator
            public /* bridge */ /* synthetic */ Spliterator.OfPrimitive trySplit() {
                return (Spliterator.OfPrimitive) super.trySplit();
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<? extends T_SPLITR extends j$.util.Spliterator$OfPrimitive<T, T_CONS, T_SPLITR>> */
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$DelegatingSpliterator$OfPrimitive != java.util.stream.StreamSpliterators$DelegatingSpliterator$OfPrimitive<T, T_CONS, T_SPLITR extends j$.util.Spliterator$OfPrimitive<T, T_CONS, T_SPLITR>> */
            OfPrimitive(Supplier<? extends T_SPLITR> supplier) {
                super(supplier);
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$DelegatingSpliterator$OfPrimitive != java.util.stream.StreamSpliterators$DelegatingSpliterator$OfPrimitive<T, T_CONS, T_SPLITR extends j$.util.Spliterator$OfPrimitive<T, T_CONS, T_SPLITR>> */
            @Override // j$.util.Spliterator.OfPrimitive
            public boolean tryAdvance(T_CONS consumer) {
                return ((Spliterator.OfPrimitive) get()).tryAdvance((Spliterator.OfPrimitive) consumer);
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$DelegatingSpliterator$OfPrimitive != java.util.stream.StreamSpliterators$DelegatingSpliterator$OfPrimitive<T, T_CONS, T_SPLITR extends j$.util.Spliterator$OfPrimitive<T, T_CONS, T_SPLITR>> */
            @Override // j$.util.Spliterator.OfPrimitive
            public void forEachRemaining(T_CONS consumer) {
                ((Spliterator.OfPrimitive) get()).forEachRemaining((Spliterator.OfPrimitive) consumer);
            }
        }

        /* loaded from: classes2.dex */
        public static final class OfInt extends OfPrimitive<Integer, IntConsumer, Spliterator.OfInt> implements Spliterator.OfInt {
            @Override // j$.util.Spliterator.OfInt
            public /* bridge */ /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
                super.forEachRemaining((OfInt) intConsumer);
            }

            @Override // j$.util.Spliterator.OfInt
            public /* bridge */ /* synthetic */ boolean tryAdvance(IntConsumer intConsumer) {
                return super.tryAdvance((OfInt) intConsumer);
            }

            @Override // j$.util.stream.StreamSpliterators.DelegatingSpliterator.OfPrimitive, j$.util.stream.StreamSpliterators.DelegatingSpliterator, j$.util.Spliterator
            public /* bridge */ /* synthetic */ Spliterator.OfInt trySplit() {
                return (Spliterator.OfInt) super.trySplit();
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<java.util.Spliterator$OfInt> */
            public OfInt(Supplier<Spliterator.OfInt> supplier) {
                super(supplier);
            }
        }

        /* loaded from: classes2.dex */
        public static final class OfLong extends OfPrimitive<Long, LongConsumer, Spliterator.OfLong> implements Spliterator.OfLong {
            @Override // j$.util.Spliterator.OfLong
            public /* bridge */ /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
                super.forEachRemaining((OfLong) longConsumer);
            }

            @Override // j$.util.Spliterator.OfLong
            public /* bridge */ /* synthetic */ boolean tryAdvance(LongConsumer longConsumer) {
                return super.tryAdvance((OfLong) longConsumer);
            }

            @Override // j$.util.stream.StreamSpliterators.DelegatingSpliterator.OfPrimitive, j$.util.stream.StreamSpliterators.DelegatingSpliterator, j$.util.Spliterator
            public /* bridge */ /* synthetic */ Spliterator.OfLong trySplit() {
                return (Spliterator.OfLong) super.trySplit();
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<java.util.Spliterator$OfLong> */
            public OfLong(Supplier<Spliterator.OfLong> supplier) {
                super(supplier);
            }
        }

        /* loaded from: classes2.dex */
        public static final class OfDouble extends OfPrimitive<Double, DoubleConsumer, Spliterator.OfDouble> implements Spliterator.OfDouble {
            @Override // j$.util.Spliterator.OfDouble
            public /* bridge */ /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
                super.forEachRemaining((OfDouble) doubleConsumer);
            }

            @Override // j$.util.Spliterator.OfDouble
            public /* bridge */ /* synthetic */ boolean tryAdvance(DoubleConsumer doubleConsumer) {
                return super.tryAdvance((OfDouble) doubleConsumer);
            }

            @Override // j$.util.stream.StreamSpliterators.DelegatingSpliterator.OfPrimitive, j$.util.stream.StreamSpliterators.DelegatingSpliterator, j$.util.Spliterator
            public /* bridge */ /* synthetic */ Spliterator.OfDouble trySplit() {
                return (Spliterator.OfDouble) super.trySplit();
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<java.util.Spliterator$OfDouble> */
            public OfDouble(Supplier<Spliterator.OfDouble> supplier) {
                super(supplier);
            }
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class SliceSpliterator<T, T_SPLITR extends Spliterator<T>> {
        static final /* synthetic */ boolean $assertionsDisabled = true;
        long fence;
        long index;
        T_SPLITR s;
        final long sliceFence;
        final long sliceOrigin;

        protected abstract T_SPLITR makeSpliterator(T_SPLITR t_splitr, long j, long j2, long j3, long j4);

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$SliceSpliterator != java.util.stream.StreamSpliterators$SliceSpliterator<T, T_SPLITR extends j$.util.Spliterator<T>> */
        SliceSpliterator(T_SPLITR s, long sliceOrigin, long sliceFence, long origin, long fence) {
            if ($assertionsDisabled || s.hasCharacteristics(16384)) {
                this.s = s;
                this.sliceOrigin = sliceOrigin;
                this.sliceFence = sliceFence;
                this.index = origin;
                this.fence = fence;
                return;
            }
            throw new AssertionError();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$SliceSpliterator != java.util.stream.StreamSpliterators$SliceSpliterator<T, T_SPLITR extends j$.util.Spliterator<T>> */
        public T_SPLITR trySplit() {
            long j = this.sliceOrigin;
            long j2 = this.fence;
            if (j < j2 && this.index < j2) {
                while (true) {
                    T_SPLITR leftSplit = (T_SPLITR) this.s.trySplit();
                    if (leftSplit == null) {
                        return null;
                    }
                    long leftSplitFenceUnbounded = this.index + leftSplit.estimateSize();
                    long leftSplitFence = Math.min(leftSplitFenceUnbounded, this.sliceFence);
                    long j3 = this.sliceOrigin;
                    if (j3 >= leftSplitFence) {
                        this.index = leftSplitFence;
                    } else {
                        long j4 = this.sliceFence;
                        if (leftSplitFence >= j4) {
                            this.s = leftSplit;
                            this.fence = leftSplitFence;
                        } else {
                            long j5 = this.index;
                            if (j5 >= j3 && leftSplitFenceUnbounded <= j4) {
                                this.index = leftSplitFence;
                                return leftSplit;
                            }
                            this.index = leftSplitFence;
                            return makeSpliterator(leftSplit, j3, j4, j5, leftSplitFence);
                        }
                    }
                }
            } else {
                return null;
            }
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$SliceSpliterator != java.util.stream.StreamSpliterators$SliceSpliterator<T, T_SPLITR extends j$.util.Spliterator<T>> */
        public long estimateSize() {
            long j = this.sliceOrigin;
            long j2 = this.fence;
            if (j < j2) {
                return j2 - Math.max(j, this.index);
            }
            return 0L;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$SliceSpliterator != java.util.stream.StreamSpliterators$SliceSpliterator<T, T_SPLITR extends j$.util.Spliterator<T>> */
        public int characteristics() {
            return this.s.characteristics();
        }

        /* loaded from: classes2.dex */
        public static final class OfRef<T> extends SliceSpliterator<T, Spliterator<T>> implements Spliterator<T> {
            @Override // j$.util.Spliterator
            public /* synthetic */ Comparator getComparator() {
                return Spliterator.CC.$default$getComparator(this);
            }

            @Override // j$.util.Spliterator
            public /* synthetic */ long getExactSizeIfKnown() {
                return Spliterator.CC.$default$getExactSizeIfKnown(this);
            }

            @Override // j$.util.Spliterator
            public /* synthetic */ boolean hasCharacteristics(int i) {
                return Spliterator.CC.$default$hasCharacteristics(this, i);
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<T> */
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$SliceSpliterator$OfRef != java.util.stream.StreamSpliterators$SliceSpliterator$OfRef<T> */
            public OfRef(Spliterator<T> spliterator, long sliceOrigin, long sliceFence) {
                this(spliterator, sliceOrigin, sliceFence, 0L, Math.min(spliterator.estimateSize(), sliceFence));
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<T> */
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$SliceSpliterator$OfRef != java.util.stream.StreamSpliterators$SliceSpliterator$OfRef<T> */
            private OfRef(Spliterator<T> spliterator, long sliceOrigin, long sliceFence, long origin, long fence) {
                super(spliterator, sliceOrigin, sliceFence, origin, fence);
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<T> */
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$SliceSpliterator$OfRef != java.util.stream.StreamSpliterators$SliceSpliterator$OfRef<T> */
            @Override // j$.util.stream.StreamSpliterators.SliceSpliterator
            protected Spliterator<T> makeSpliterator(Spliterator<T> spliterator, long sliceOrigin, long sliceFence, long origin, long fence) {
                return new OfRef(spliterator, sliceOrigin, sliceFence, origin, fence);
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<? super T> */
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$SliceSpliterator$OfRef != java.util.stream.StreamSpliterators$SliceSpliterator$OfRef<T> */
            @Override // j$.util.Spliterator
            public boolean tryAdvance(Consumer<? super T> consumer) {
                consumer.getClass();
                if (this.sliceOrigin >= this.fence) {
                    return false;
                }
                while (this.sliceOrigin > this.index) {
                    this.s.tryAdvance(StreamSpliterators$SliceSpliterator$OfRef$$ExternalSyntheticLambda1.INSTANCE);
                    this.index++;
                }
                if (this.index >= this.fence) {
                    return false;
                }
                this.index++;
                return this.s.tryAdvance(consumer);
            }

            public static /* synthetic */ void lambda$tryAdvance$0(Object e) {
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<? super T> */
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$SliceSpliterator$OfRef != java.util.stream.StreamSpliterators$SliceSpliterator$OfRef<T> */
            @Override // j$.util.Spliterator
            public void forEachRemaining(Consumer<? super T> consumer) {
                consumer.getClass();
                if (this.sliceOrigin >= this.fence || this.index >= this.fence) {
                    return;
                }
                if (this.index >= this.sliceOrigin && this.index + this.s.estimateSize() <= this.sliceFence) {
                    this.s.forEachRemaining(consumer);
                    this.index = this.fence;
                    return;
                }
                while (this.sliceOrigin > this.index) {
                    this.s.tryAdvance(StreamSpliterators$SliceSpliterator$OfRef$$ExternalSyntheticLambda0.INSTANCE);
                    this.index++;
                }
                while (this.index < this.fence) {
                    this.s.tryAdvance(consumer);
                    this.index++;
                }
            }

            public static /* synthetic */ void lambda$forEachRemaining$1(Object e) {
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes2.dex */
        public static abstract class OfPrimitive<T, T_SPLITR extends Spliterator.OfPrimitive<T, T_CONS, T_SPLITR>, T_CONS> extends SliceSpliterator<T, T_SPLITR> implements Spliterator.OfPrimitive<T, T_CONS, T_SPLITR> {
            protected abstract T_CONS emptyConsumer();

            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                Spliterator.CC.$default$forEachRemaining(this, consumer);
            }

            @Override // j$.util.Spliterator
            public /* synthetic */ Comparator getComparator() {
                return Spliterator.CC.$default$getComparator(this);
            }

            @Override // j$.util.Spliterator
            public /* synthetic */ long getExactSizeIfKnown() {
                return Spliterator.CC.$default$getExactSizeIfKnown(this);
            }

            @Override // j$.util.Spliterator
            public /* synthetic */ boolean hasCharacteristics(int i) {
                return Spliterator.CC.$default$hasCharacteristics(this, i);
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$SliceSpliterator$OfPrimitive != java.util.stream.StreamSpliterators$SliceSpliterator$OfPrimitive<T, T_SPLITR extends j$.util.Spliterator$OfPrimitive<T, T_CONS, T_SPLITR>, T_CONS> */
            @Override // j$.util.stream.StreamSpliterators.SliceSpliterator, j$.util.Spliterator.OfPrimitive, j$.util.Spliterator
            public /* bridge */ /* synthetic */ Spliterator.OfPrimitive trySplit() {
                return (Spliterator.OfPrimitive) super.trySplit();
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$SliceSpliterator$OfPrimitive != java.util.stream.StreamSpliterators$SliceSpliterator$OfPrimitive<T, T_SPLITR extends j$.util.Spliterator$OfPrimitive<T, T_CONS, T_SPLITR>, T_CONS> */
            OfPrimitive(T_SPLITR s, long sliceOrigin, long sliceFence) {
                this(s, sliceOrigin, sliceFence, 0L, Math.min(s.estimateSize(), sliceFence));
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$SliceSpliterator$OfPrimitive != java.util.stream.StreamSpliterators$SliceSpliterator$OfPrimitive<T, T_SPLITR extends j$.util.Spliterator$OfPrimitive<T, T_CONS, T_SPLITR>, T_CONS> */
            private OfPrimitive(T_SPLITR s, long sliceOrigin, long sliceFence, long origin, long fence) {
                super(s, sliceOrigin, sliceFence, origin, fence);
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$SliceSpliterator$OfPrimitive != java.util.stream.StreamSpliterators$SliceSpliterator$OfPrimitive<T, T_SPLITR extends j$.util.Spliterator$OfPrimitive<T, T_CONS, T_SPLITR>, T_CONS> */
            @Override // j$.util.Spliterator.OfPrimitive
            public boolean tryAdvance(T_CONS action) {
                action.getClass();
                if (this.sliceOrigin >= this.fence) {
                    return false;
                }
                while (this.sliceOrigin > this.index) {
                    ((Spliterator.OfPrimitive) this.s).tryAdvance((Spliterator.OfPrimitive) emptyConsumer());
                    this.index++;
                }
                if (this.index >= this.fence) {
                    return false;
                }
                this.index++;
                return ((Spliterator.OfPrimitive) this.s).tryAdvance((Spliterator.OfPrimitive) action);
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$SliceSpliterator$OfPrimitive != java.util.stream.StreamSpliterators$SliceSpliterator$OfPrimitive<T, T_SPLITR extends j$.util.Spliterator$OfPrimitive<T, T_CONS, T_SPLITR>, T_CONS> */
            @Override // j$.util.Spliterator.OfPrimitive
            public void forEachRemaining(T_CONS action) {
                action.getClass();
                if (this.sliceOrigin >= this.fence || this.index >= this.fence) {
                    return;
                }
                if (this.index >= this.sliceOrigin && this.index + ((Spliterator.OfPrimitive) this.s).estimateSize() <= this.sliceFence) {
                    ((Spliterator.OfPrimitive) this.s).forEachRemaining((Spliterator.OfPrimitive) action);
                    this.index = this.fence;
                    return;
                }
                while (this.sliceOrigin > this.index) {
                    ((Spliterator.OfPrimitive) this.s).tryAdvance((Spliterator.OfPrimitive) emptyConsumer());
                    this.index++;
                }
                while (this.index < this.fence) {
                    ((Spliterator.OfPrimitive) this.s).tryAdvance((Spliterator.OfPrimitive) action);
                    this.index++;
                }
            }
        }

        /* loaded from: classes2.dex */
        public static final class OfInt extends OfPrimitive<Integer, Spliterator.OfInt, IntConsumer> implements Spliterator.OfInt {
            @Override // j$.util.stream.StreamSpliterators.SliceSpliterator.OfPrimitive, j$.util.Spliterator
            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                Spliterator.OfInt.CC.$default$forEachRemaining((Spliterator.OfInt) this, consumer);
            }

            @Override // j$.util.Spliterator
            public /* synthetic */ boolean tryAdvance(Consumer consumer) {
                return Spliterator.OfInt.CC.$default$tryAdvance((Spliterator.OfInt) this, consumer);
            }

            @Override // j$.util.Spliterator.OfInt
            public /* bridge */ /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
                super.forEachRemaining((OfInt) intConsumer);
            }

            @Override // j$.util.Spliterator.OfInt
            public /* bridge */ /* synthetic */ boolean tryAdvance(IntConsumer intConsumer) {
                return super.tryAdvance((OfInt) intConsumer);
            }

            @Override // j$.util.stream.StreamSpliterators.SliceSpliterator.OfPrimitive, j$.util.stream.StreamSpliterators.SliceSpliterator, j$.util.Spliterator.OfPrimitive, j$.util.Spliterator
            public /* bridge */ /* synthetic */ Spliterator.OfInt trySplit() {
                return (Spliterator.OfInt) super.trySplit();
            }

            public OfInt(Spliterator.OfInt s, long sliceOrigin, long sliceFence) {
                super(s, sliceOrigin, sliceFence);
            }

            OfInt(Spliterator.OfInt s, long sliceOrigin, long sliceFence, long origin, long fence) {
                super(s, sliceOrigin, sliceFence, origin, fence);
            }

            public Spliterator.OfInt makeSpliterator(Spliterator.OfInt s, long sliceOrigin, long sliceFence, long origin, long fence) {
                return new OfInt(s, sliceOrigin, sliceFence, origin, fence);
            }

            public static /* synthetic */ void lambda$emptyConsumer$0(int e) {
            }

            @Override // j$.util.stream.StreamSpliterators.SliceSpliterator.OfPrimitive
            public IntConsumer emptyConsumer() {
                return StreamSpliterators$SliceSpliterator$OfInt$$ExternalSyntheticLambda0.INSTANCE;
            }
        }

        /* loaded from: classes2.dex */
        public static final class OfLong extends OfPrimitive<Long, Spliterator.OfLong, LongConsumer> implements Spliterator.OfLong {
            @Override // j$.util.stream.StreamSpliterators.SliceSpliterator.OfPrimitive, j$.util.Spliterator
            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                Spliterator.OfLong.CC.$default$forEachRemaining((Spliterator.OfLong) this, consumer);
            }

            @Override // j$.util.Spliterator
            public /* synthetic */ boolean tryAdvance(Consumer consumer) {
                return Spliterator.OfLong.CC.$default$tryAdvance((Spliterator.OfLong) this, consumer);
            }

            @Override // j$.util.Spliterator.OfLong
            public /* bridge */ /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
                super.forEachRemaining((OfLong) longConsumer);
            }

            @Override // j$.util.Spliterator.OfLong
            public /* bridge */ /* synthetic */ boolean tryAdvance(LongConsumer longConsumer) {
                return super.tryAdvance((OfLong) longConsumer);
            }

            @Override // j$.util.stream.StreamSpliterators.SliceSpliterator.OfPrimitive, j$.util.stream.StreamSpliterators.SliceSpliterator, j$.util.Spliterator.OfPrimitive, j$.util.Spliterator
            public /* bridge */ /* synthetic */ Spliterator.OfLong trySplit() {
                return (Spliterator.OfLong) super.trySplit();
            }

            public OfLong(Spliterator.OfLong s, long sliceOrigin, long sliceFence) {
                super(s, sliceOrigin, sliceFence);
            }

            OfLong(Spliterator.OfLong s, long sliceOrigin, long sliceFence, long origin, long fence) {
                super(s, sliceOrigin, sliceFence, origin, fence);
            }

            public Spliterator.OfLong makeSpliterator(Spliterator.OfLong s, long sliceOrigin, long sliceFence, long origin, long fence) {
                return new OfLong(s, sliceOrigin, sliceFence, origin, fence);
            }

            public static /* synthetic */ void lambda$emptyConsumer$0(long e) {
            }

            @Override // j$.util.stream.StreamSpliterators.SliceSpliterator.OfPrimitive
            public LongConsumer emptyConsumer() {
                return StreamSpliterators$SliceSpliterator$OfLong$$ExternalSyntheticLambda0.INSTANCE;
            }
        }

        /* loaded from: classes2.dex */
        public static final class OfDouble extends OfPrimitive<Double, Spliterator.OfDouble, DoubleConsumer> implements Spliterator.OfDouble {
            @Override // j$.util.stream.StreamSpliterators.SliceSpliterator.OfPrimitive, j$.util.Spliterator
            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                Spliterator.OfDouble.CC.$default$forEachRemaining((Spliterator.OfDouble) this, consumer);
            }

            @Override // j$.util.Spliterator
            public /* synthetic */ boolean tryAdvance(Consumer consumer) {
                return Spliterator.OfDouble.CC.$default$tryAdvance((Spliterator.OfDouble) this, consumer);
            }

            @Override // j$.util.Spliterator.OfDouble
            public /* bridge */ /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
                super.forEachRemaining((OfDouble) doubleConsumer);
            }

            @Override // j$.util.Spliterator.OfDouble
            public /* bridge */ /* synthetic */ boolean tryAdvance(DoubleConsumer doubleConsumer) {
                return super.tryAdvance((OfDouble) doubleConsumer);
            }

            @Override // j$.util.stream.StreamSpliterators.SliceSpliterator.OfPrimitive, j$.util.stream.StreamSpliterators.SliceSpliterator, j$.util.Spliterator.OfPrimitive, j$.util.Spliterator
            public /* bridge */ /* synthetic */ Spliterator.OfDouble trySplit() {
                return (Spliterator.OfDouble) super.trySplit();
            }

            public OfDouble(Spliterator.OfDouble s, long sliceOrigin, long sliceFence) {
                super(s, sliceOrigin, sliceFence);
            }

            OfDouble(Spliterator.OfDouble s, long sliceOrigin, long sliceFence, long origin, long fence) {
                super(s, sliceOrigin, sliceFence, origin, fence);
            }

            public Spliterator.OfDouble makeSpliterator(Spliterator.OfDouble s, long sliceOrigin, long sliceFence, long origin, long fence) {
                return new OfDouble(s, sliceOrigin, sliceFence, origin, fence);
            }

            public static /* synthetic */ void lambda$emptyConsumer$0(double e) {
            }

            @Override // j$.util.stream.StreamSpliterators.SliceSpliterator.OfPrimitive
            public DoubleConsumer emptyConsumer() {
                return StreamSpliterators$SliceSpliterator$OfDouble$$ExternalSyntheticLambda0.INSTANCE;
            }
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class UnorderedSliceSpliterator<T, T_SPLITR extends Spliterator<T>> {
        static final /* synthetic */ boolean $assertionsDisabled = true;
        static final int CHUNK_SIZE = 128;
        private final AtomicLong permits;
        protected final T_SPLITR s;
        private final long skipThreshold;
        protected final boolean unlimited;

        /* loaded from: classes2.dex */
        public enum PermitStatus {
            NO_MORE,
            MAYBE_MORE,
            UNLIMITED
        }

        protected abstract T_SPLITR makeSpliterator(T_SPLITR t_splitr);

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$UnorderedSliceSpliterator != java.util.stream.StreamSpliterators$UnorderedSliceSpliterator<T, T_SPLITR extends j$.util.Spliterator<T>> */
        UnorderedSliceSpliterator(T_SPLITR s, long skip, long limit) {
            this.s = s;
            this.unlimited = limit < 0;
            this.skipThreshold = limit >= 0 ? limit : 0L;
            this.permits = new AtomicLong(limit >= 0 ? skip + limit : skip);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$UnorderedSliceSpliterator != java.util.stream.StreamSpliterators$UnorderedSliceSpliterator<T, T_SPLITR extends j$.util.Spliterator<T>> */
        UnorderedSliceSpliterator(T_SPLITR s, UnorderedSliceSpliterator<T, T_SPLITR> unorderedSliceSpliterator) {
            this.s = s;
            this.unlimited = unorderedSliceSpliterator.unlimited;
            this.permits = unorderedSliceSpliterator.permits;
            this.skipThreshold = unorderedSliceSpliterator.skipThreshold;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$UnorderedSliceSpliterator != java.util.stream.StreamSpliterators$UnorderedSliceSpliterator<T, T_SPLITR extends j$.util.Spliterator<T>> */
        protected final long acquirePermits(long numElements) {
            long remainingPermits;
            long grabbing;
            if ($assertionsDisabled || numElements > 0) {
                do {
                    remainingPermits = this.permits.get();
                    if (remainingPermits == 0) {
                        if (!this.unlimited) {
                            return 0L;
                        }
                        return numElements;
                    }
                    grabbing = Math.min(remainingPermits, numElements);
                    if (grabbing <= 0) {
                        break;
                    }
                } while (!this.permits.compareAndSet(remainingPermits, remainingPermits - grabbing));
                if (this.unlimited) {
                    return Math.max(numElements - grabbing, 0L);
                }
                long j = this.skipThreshold;
                if (remainingPermits > j) {
                    return Math.max(grabbing - (remainingPermits - j), 0L);
                }
                return grabbing;
            }
            throw new AssertionError();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$UnorderedSliceSpliterator != java.util.stream.StreamSpliterators$UnorderedSliceSpliterator<T, T_SPLITR extends j$.util.Spliterator<T>> */
        protected final PermitStatus permitStatus() {
            if (this.permits.get() > 0) {
                return PermitStatus.MAYBE_MORE;
            }
            return this.unlimited ? PermitStatus.UNLIMITED : PermitStatus.NO_MORE;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$UnorderedSliceSpliterator != java.util.stream.StreamSpliterators$UnorderedSliceSpliterator<T, T_SPLITR extends j$.util.Spliterator<T>> */
        /* JADX WARN: Multi-variable type inference failed */
        public final T_SPLITR trySplit() {
            Spliterator<T> trySplit;
            if (this.permits.get() == 0 || (trySplit = this.s.trySplit()) == null) {
                return null;
            }
            return (T_SPLITR) makeSpliterator(trySplit);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$UnorderedSliceSpliterator != java.util.stream.StreamSpliterators$UnorderedSliceSpliterator<T, T_SPLITR extends j$.util.Spliterator<T>> */
        public final long estimateSize() {
            return this.s.estimateSize();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$UnorderedSliceSpliterator != java.util.stream.StreamSpliterators$UnorderedSliceSpliterator<T, T_SPLITR extends j$.util.Spliterator<T>> */
        public final int characteristics() {
            return this.s.characteristics() & (-16465);
        }

        /* loaded from: classes2.dex */
        public static final class OfRef<T> extends UnorderedSliceSpliterator<T, Spliterator<T>> implements Spliterator<T>, Consumer<T> {
            T tmpSlot;

            @Override // j$.util.function.Consumer
            public /* synthetic */ Consumer andThen(Consumer consumer) {
                return consumer.getClass();
            }

            @Override // j$.util.Spliterator
            public /* synthetic */ Comparator getComparator() {
                return Spliterator.CC.$default$getComparator(this);
            }

            @Override // j$.util.Spliterator
            public /* synthetic */ long getExactSizeIfKnown() {
                return Spliterator.CC.$default$getExactSizeIfKnown(this);
            }

            @Override // j$.util.Spliterator
            public /* synthetic */ boolean hasCharacteristics(int i) {
                return Spliterator.CC.$default$hasCharacteristics(this, i);
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<T> */
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$UnorderedSliceSpliterator$OfRef != java.util.stream.StreamSpliterators$UnorderedSliceSpliterator$OfRef<T> */
            public OfRef(Spliterator<T> spliterator, long skip, long limit) {
                super(spliterator, skip, limit);
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<T> */
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$UnorderedSliceSpliterator$OfRef != java.util.stream.StreamSpliterators$UnorderedSliceSpliterator$OfRef<T> */
            OfRef(Spliterator<T> spliterator, OfRef<T> ofRef) {
                super(spliterator, ofRef);
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$UnorderedSliceSpliterator$OfRef != java.util.stream.StreamSpliterators$UnorderedSliceSpliterator$OfRef<T> */
            @Override // j$.util.function.Consumer
            public final void accept(T t) {
                this.tmpSlot = t;
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<? super T> */
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$UnorderedSliceSpliterator$OfRef != java.util.stream.StreamSpliterators$UnorderedSliceSpliterator$OfRef<T> */
            @Override // j$.util.Spliterator
            public boolean tryAdvance(Consumer<? super T> consumer) {
                consumer.getClass();
                while (permitStatus() != PermitStatus.NO_MORE && this.s.tryAdvance(this)) {
                    if (acquirePermits(1L) == 1) {
                        consumer.accept((T) this.tmpSlot);
                        this.tmpSlot = null;
                        return true;
                    }
                }
                return false;
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<? super T> */
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$ArrayBuffer$OfRef != java.util.stream.StreamSpliterators$ArrayBuffer$OfRef<T> */
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$UnorderedSliceSpliterator$OfRef != java.util.stream.StreamSpliterators$UnorderedSliceSpliterator$OfRef<T> */
            @Override // j$.util.Spliterator
            public void forEachRemaining(Consumer<? super T> consumer) {
                consumer.getClass();
                ArrayBuffer.OfRef ofRef = null;
                while (true) {
                    PermitStatus permitStatus = permitStatus();
                    if (permitStatus != PermitStatus.NO_MORE) {
                        if (permitStatus == PermitStatus.MAYBE_MORE) {
                            if (ofRef == null) {
                                ofRef = new ArrayBuffer.OfRef(128);
                            } else {
                                ofRef.reset();
                            }
                            long permitsRequested = 0;
                            while (this.s.tryAdvance(ofRef)) {
                                long j = 1 + permitsRequested;
                                permitsRequested = j;
                                if (j >= 128) {
                                    break;
                                }
                            }
                            if (permitsRequested == 0) {
                                return;
                            }
                            ofRef.forEach(consumer, acquirePermits(permitsRequested));
                        } else {
                            this.s.forEachRemaining(consumer);
                            return;
                        }
                    } else {
                        return;
                    }
                }
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<T> */
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$UnorderedSliceSpliterator$OfRef != java.util.stream.StreamSpliterators$UnorderedSliceSpliterator$OfRef<T> */
            @Override // j$.util.stream.StreamSpliterators.UnorderedSliceSpliterator
            protected Spliterator<T> makeSpliterator(Spliterator<T> spliterator) {
                return new OfRef(spliterator, this);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes2.dex */
        public static abstract class OfPrimitive<T, T_CONS, T_BUFF extends ArrayBuffer.OfPrimitive<T_CONS>, T_SPLITR extends Spliterator.OfPrimitive<T, T_CONS, T_SPLITR>> extends UnorderedSliceSpliterator<T, T_SPLITR> implements Spliterator.OfPrimitive<T, T_CONS, T_SPLITR> {
            protected abstract void acceptConsumed(T_CONS t_cons);

            protected abstract T_BUFF bufferCreate(int i);

            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                Spliterator.CC.$default$forEachRemaining(this, consumer);
            }

            @Override // j$.util.Spliterator
            public /* synthetic */ Comparator getComparator() {
                return Spliterator.CC.$default$getComparator(this);
            }

            @Override // j$.util.Spliterator
            public /* synthetic */ long getExactSizeIfKnown() {
                return Spliterator.CC.$default$getExactSizeIfKnown(this);
            }

            @Override // j$.util.Spliterator
            public /* synthetic */ boolean hasCharacteristics(int i) {
                return Spliterator.CC.$default$hasCharacteristics(this, i);
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$UnorderedSliceSpliterator$OfPrimitive != java.util.stream.StreamSpliterators$UnorderedSliceSpliterator$OfPrimitive<T, T_CONS, T_BUFF extends j$.util.stream.StreamSpliterators$ArrayBuffer$OfPrimitive<T_CONS>, T_SPLITR extends j$.util.Spliterator$OfPrimitive<T, T_CONS, T_SPLITR>> */
            @Override // j$.util.stream.StreamSpliterators.UnorderedSliceSpliterator, j$.util.Spliterator.OfPrimitive, j$.util.Spliterator
            public /* bridge */ /* synthetic */ Spliterator.OfPrimitive trySplit() {
                return (Spliterator.OfPrimitive) super.trySplit();
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$UnorderedSliceSpliterator$OfPrimitive != java.util.stream.StreamSpliterators$UnorderedSliceSpliterator$OfPrimitive<T, T_CONS, T_BUFF extends j$.util.stream.StreamSpliterators$ArrayBuffer$OfPrimitive<T_CONS>, T_SPLITR extends j$.util.Spliterator$OfPrimitive<T, T_CONS, T_SPLITR>> */
            OfPrimitive(T_SPLITR s, long skip, long limit) {
                super(s, skip, limit);
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$UnorderedSliceSpliterator$OfPrimitive != java.util.stream.StreamSpliterators$UnorderedSliceSpliterator$OfPrimitive<T, T_CONS, T_BUFF extends j$.util.stream.StreamSpliterators$ArrayBuffer$OfPrimitive<T_CONS>, T_SPLITR extends j$.util.Spliterator$OfPrimitive<T, T_CONS, T_SPLITR>> */
            OfPrimitive(T_SPLITR s, OfPrimitive<T, T_CONS, T_BUFF, T_SPLITR> ofPrimitive) {
                super(s, ofPrimitive);
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$UnorderedSliceSpliterator$OfPrimitive != java.util.stream.StreamSpliterators$UnorderedSliceSpliterator$OfPrimitive<T, T_CONS, T_BUFF extends j$.util.stream.StreamSpliterators$ArrayBuffer$OfPrimitive<T_CONS>, T_SPLITR extends j$.util.Spliterator$OfPrimitive<T, T_CONS, T_SPLITR>> */
            @Override // j$.util.Spliterator.OfPrimitive
            public boolean tryAdvance(T_CONS action) {
                action.getClass();
                while (permitStatus() != PermitStatus.NO_MORE && ((Spliterator.OfPrimitive) this.s).tryAdvance(this)) {
                    if (acquirePermits(1L) == 1) {
                        acceptConsumed(action);
                        return true;
                    }
                }
                return false;
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$UnorderedSliceSpliterator$OfPrimitive != java.util.stream.StreamSpliterators$UnorderedSliceSpliterator$OfPrimitive<T, T_CONS, T_BUFF extends j$.util.stream.StreamSpliterators$ArrayBuffer$OfPrimitive<T_CONS>, T_SPLITR extends j$.util.Spliterator$OfPrimitive<T, T_CONS, T_SPLITR>> */
            @Override // j$.util.Spliterator.OfPrimitive
            public void forEachRemaining(T_CONS action) {
                action.getClass();
                T_BUFF sb = null;
                while (true) {
                    PermitStatus permitStatus = permitStatus();
                    if (permitStatus != PermitStatus.NO_MORE) {
                        if (permitStatus == PermitStatus.MAYBE_MORE) {
                            if (sb == null) {
                                sb = bufferCreate(128);
                            } else {
                                sb.reset();
                            }
                            T_BUFF t_buff = sb;
                            long permitsRequested = 0;
                            while (((Spliterator.OfPrimitive) this.s).tryAdvance((Spliterator.OfPrimitive) t_buff)) {
                                long j = 1 + permitsRequested;
                                permitsRequested = j;
                                if (j >= 128) {
                                    break;
                                }
                            }
                            if (permitsRequested == 0) {
                                return;
                            }
                            sb.forEach(action, acquirePermits(permitsRequested));
                        } else {
                            ((Spliterator.OfPrimitive) this.s).forEachRemaining((Spliterator.OfPrimitive) action);
                            return;
                        }
                    } else {
                        return;
                    }
                }
            }
        }

        /* loaded from: classes2.dex */
        public static final class OfInt extends OfPrimitive<Integer, IntConsumer, ArrayBuffer.OfInt, Spliterator.OfInt> implements Spliterator.OfInt, IntConsumer {
            int tmpValue;

            @Override // j$.util.function.IntConsumer
            public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
                return intConsumer.getClass();
            }

            @Override // j$.util.stream.StreamSpliterators.UnorderedSliceSpliterator.OfPrimitive, j$.util.Spliterator
            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                Spliterator.OfInt.CC.$default$forEachRemaining((Spliterator.OfInt) this, consumer);
            }

            @Override // j$.util.Spliterator
            public /* synthetic */ boolean tryAdvance(Consumer consumer) {
                return Spliterator.OfInt.CC.$default$tryAdvance((Spliterator.OfInt) this, consumer);
            }

            @Override // j$.util.Spliterator.OfInt
            public /* bridge */ /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
                super.forEachRemaining((OfInt) intConsumer);
            }

            @Override // j$.util.Spliterator.OfInt
            public /* bridge */ /* synthetic */ boolean tryAdvance(IntConsumer intConsumer) {
                return super.tryAdvance((OfInt) intConsumer);
            }

            @Override // j$.util.stream.StreamSpliterators.UnorderedSliceSpliterator.OfPrimitive, j$.util.stream.StreamSpliterators.UnorderedSliceSpliterator, j$.util.Spliterator.OfPrimitive, j$.util.Spliterator
            public /* bridge */ /* synthetic */ Spliterator.OfInt trySplit() {
                return (Spliterator.OfInt) super.trySplit();
            }

            public OfInt(Spliterator.OfInt s, long skip, long limit) {
                super(s, skip, limit);
            }

            OfInt(Spliterator.OfInt s, OfInt parent) {
                super(s, parent);
            }

            @Override // j$.util.function.IntConsumer
            public void accept(int value) {
                this.tmpValue = value;
            }

            public void acceptConsumed(IntConsumer action) {
                action.accept(this.tmpValue);
            }

            @Override // j$.util.stream.StreamSpliterators.UnorderedSliceSpliterator.OfPrimitive
            public ArrayBuffer.OfInt bufferCreate(int initialCapacity) {
                return new ArrayBuffer.OfInt(initialCapacity);
            }

            public Spliterator.OfInt makeSpliterator(Spliterator.OfInt s) {
                return new OfInt(s, this);
            }
        }

        /* loaded from: classes2.dex */
        public static final class OfLong extends OfPrimitive<Long, LongConsumer, ArrayBuffer.OfLong, Spliterator.OfLong> implements Spliterator.OfLong, LongConsumer {
            long tmpValue;

            @Override // j$.util.function.LongConsumer
            public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
                return longConsumer.getClass();
            }

            @Override // j$.util.stream.StreamSpliterators.UnorderedSliceSpliterator.OfPrimitive, j$.util.Spliterator
            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                Spliterator.OfLong.CC.$default$forEachRemaining((Spliterator.OfLong) this, consumer);
            }

            @Override // j$.util.Spliterator
            public /* synthetic */ boolean tryAdvance(Consumer consumer) {
                return Spliterator.OfLong.CC.$default$tryAdvance((Spliterator.OfLong) this, consumer);
            }

            @Override // j$.util.Spliterator.OfLong
            public /* bridge */ /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
                super.forEachRemaining((OfLong) longConsumer);
            }

            @Override // j$.util.Spliterator.OfLong
            public /* bridge */ /* synthetic */ boolean tryAdvance(LongConsumer longConsumer) {
                return super.tryAdvance((OfLong) longConsumer);
            }

            @Override // j$.util.stream.StreamSpliterators.UnorderedSliceSpliterator.OfPrimitive, j$.util.stream.StreamSpliterators.UnorderedSliceSpliterator, j$.util.Spliterator.OfPrimitive, j$.util.Spliterator
            public /* bridge */ /* synthetic */ Spliterator.OfLong trySplit() {
                return (Spliterator.OfLong) super.trySplit();
            }

            public OfLong(Spliterator.OfLong s, long skip, long limit) {
                super(s, skip, limit);
            }

            OfLong(Spliterator.OfLong s, OfLong parent) {
                super(s, parent);
            }

            @Override // j$.util.function.LongConsumer
            public void accept(long value) {
                this.tmpValue = value;
            }

            public void acceptConsumed(LongConsumer action) {
                action.accept(this.tmpValue);
            }

            @Override // j$.util.stream.StreamSpliterators.UnorderedSliceSpliterator.OfPrimitive
            public ArrayBuffer.OfLong bufferCreate(int initialCapacity) {
                return new ArrayBuffer.OfLong(initialCapacity);
            }

            public Spliterator.OfLong makeSpliterator(Spliterator.OfLong s) {
                return new OfLong(s, this);
            }
        }

        /* loaded from: classes2.dex */
        public static final class OfDouble extends OfPrimitive<Double, DoubleConsumer, ArrayBuffer.OfDouble, Spliterator.OfDouble> implements Spliterator.OfDouble, DoubleConsumer {
            double tmpValue;

            @Override // j$.util.function.DoubleConsumer
            public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
                return doubleConsumer.getClass();
            }

            @Override // j$.util.stream.StreamSpliterators.UnorderedSliceSpliterator.OfPrimitive, j$.util.Spliterator
            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                Spliterator.OfDouble.CC.$default$forEachRemaining((Spliterator.OfDouble) this, consumer);
            }

            @Override // j$.util.Spliterator
            public /* synthetic */ boolean tryAdvance(Consumer consumer) {
                return Spliterator.OfDouble.CC.$default$tryAdvance((Spliterator.OfDouble) this, consumer);
            }

            @Override // j$.util.Spliterator.OfDouble
            public /* bridge */ /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
                super.forEachRemaining((OfDouble) doubleConsumer);
            }

            @Override // j$.util.Spliterator.OfDouble
            public /* bridge */ /* synthetic */ boolean tryAdvance(DoubleConsumer doubleConsumer) {
                return super.tryAdvance((OfDouble) doubleConsumer);
            }

            @Override // j$.util.stream.StreamSpliterators.UnorderedSliceSpliterator.OfPrimitive, j$.util.stream.StreamSpliterators.UnorderedSliceSpliterator, j$.util.Spliterator.OfPrimitive, j$.util.Spliterator
            public /* bridge */ /* synthetic */ Spliterator.OfDouble trySplit() {
                return (Spliterator.OfDouble) super.trySplit();
            }

            public OfDouble(Spliterator.OfDouble s, long skip, long limit) {
                super(s, skip, limit);
            }

            OfDouble(Spliterator.OfDouble s, OfDouble parent) {
                super(s, parent);
            }

            @Override // j$.util.function.DoubleConsumer
            public void accept(double value) {
                this.tmpValue = value;
            }

            public void acceptConsumed(DoubleConsumer action) {
                action.accept(this.tmpValue);
            }

            @Override // j$.util.stream.StreamSpliterators.UnorderedSliceSpliterator.OfPrimitive
            public ArrayBuffer.OfDouble bufferCreate(int initialCapacity) {
                return new ArrayBuffer.OfDouble(initialCapacity);
            }

            public Spliterator.OfDouble makeSpliterator(Spliterator.OfDouble s) {
                return new OfDouble(s, this);
            }
        }
    }

    /* loaded from: classes2.dex */
    public static final class DistinctSpliterator<T> implements Spliterator<T>, Consumer<T> {
        private static final Object NULL_VALUE = new Object();
        private final Spliterator<T> s;
        private final ConcurrentHashMap<T, Boolean> seen;
        private T tmpSlot;

        @Override // j$.util.function.Consumer
        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return consumer.getClass();
        }

        @Override // j$.util.Spliterator
        public /* synthetic */ long getExactSizeIfKnown() {
            return Spliterator.CC.$default$getExactSizeIfKnown(this);
        }

        @Override // j$.util.Spliterator
        public /* synthetic */ boolean hasCharacteristics(int i) {
            return Spliterator.CC.$default$hasCharacteristics(this, i);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<T> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$DistinctSpliterator != java.util.stream.StreamSpliterators$DistinctSpliterator<T> */
        public DistinctSpliterator(Spliterator<T> spliterator) {
            this(spliterator, new ConcurrentHashMap());
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<T> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.concurrent.ConcurrentHashMap != java.util.concurrent.ConcurrentHashMap<T, java.lang.Boolean> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$DistinctSpliterator != java.util.stream.StreamSpliterators$DistinctSpliterator<T> */
        private DistinctSpliterator(Spliterator<T> spliterator, ConcurrentHashMap<T, Boolean> concurrentHashMap) {
            this.s = spliterator;
            this.seen = concurrentHashMap;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$DistinctSpliterator != java.util.stream.StreamSpliterators$DistinctSpliterator<T> */
        @Override // j$.util.function.Consumer
        public void accept(T t) {
            this.tmpSlot = t;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$DistinctSpliterator != java.util.stream.StreamSpliterators$DistinctSpliterator<T> */
        private T mapNull(T t) {
            return t != null ? t : (T) NULL_VALUE;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<? super T> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$DistinctSpliterator != java.util.stream.StreamSpliterators$DistinctSpliterator<T> */
        @Override // j$.util.Spliterator
        public boolean tryAdvance(Consumer<? super T> consumer) {
            while (this.s.tryAdvance(this)) {
                if (this.seen.putIfAbsent(mapNull(this.tmpSlot), Boolean.TRUE) == null) {
                    consumer.accept((T) this.tmpSlot);
                    this.tmpSlot = null;
                    return true;
                }
            }
            return false;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<? super T> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$DistinctSpliterator != java.util.stream.StreamSpliterators$DistinctSpliterator<T> */
        @Override // j$.util.Spliterator
        public void forEachRemaining(final Consumer<? super T> consumer) {
            this.s.forEachRemaining(new Consumer() { // from class: j$.util.stream.StreamSpliterators$DistinctSpliterator$$ExternalSyntheticLambda0
                @Override // j$.util.function.Consumer
                public final void accept(Object obj) {
                    StreamSpliterators.DistinctSpliterator.this.m107xb9bff3f1(consumer, obj);
                }

                @Override // j$.util.function.Consumer
                public /* synthetic */ Consumer andThen(Consumer consumer2) {
                    return consumer2.getClass();
                }
            });
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$DistinctSpliterator != java.util.stream.StreamSpliterators$DistinctSpliterator<T> */
        /* JADX WARN: Multi-variable type inference failed */
        /* renamed from: lambda$forEachRemaining$0$java-util-stream-StreamSpliterators$DistinctSpliterator */
        public /* synthetic */ void m107xb9bff3f1(Consumer action, Object t) {
            if (this.seen.putIfAbsent(mapNull(t), Boolean.TRUE) == null) {
                action.accept(t);
            }
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<T> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$DistinctSpliterator != java.util.stream.StreamSpliterators$DistinctSpliterator<T> */
        @Override // j$.util.Spliterator
        public Spliterator<T> trySplit() {
            Spliterator<T> trySplit = this.s.trySplit();
            if (trySplit != null) {
                return new DistinctSpliterator(trySplit, this.seen);
            }
            return null;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$DistinctSpliterator != java.util.stream.StreamSpliterators$DistinctSpliterator<T> */
        @Override // j$.util.Spliterator
        public long estimateSize() {
            return this.s.estimateSize();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$DistinctSpliterator != java.util.stream.StreamSpliterators$DistinctSpliterator<T> */
        @Override // j$.util.Spliterator
        public int characteristics() {
            return (this.s.characteristics() & (-16469)) | 1;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$DistinctSpliterator != java.util.stream.StreamSpliterators$DistinctSpliterator<T> */
        @Override // j$.util.Spliterator
        public Comparator<? super T> getComparator() {
            return this.s.getComparator();
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class InfiniteSupplyingSpliterator<T> implements Spliterator<T> {
        long estimate;

        @Override // j$.util.Spliterator
        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Spliterator.CC.$default$forEachRemaining(this, consumer);
        }

        @Override // j$.util.Spliterator
        public /* synthetic */ Comparator getComparator() {
            return Spliterator.CC.$default$getComparator(this);
        }

        @Override // j$.util.Spliterator
        public /* synthetic */ long getExactSizeIfKnown() {
            return Spliterator.CC.$default$getExactSizeIfKnown(this);
        }

        @Override // j$.util.Spliterator
        public /* synthetic */ boolean hasCharacteristics(int i) {
            return Spliterator.CC.$default$hasCharacteristics(this, i);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$InfiniteSupplyingSpliterator != java.util.stream.StreamSpliterators$InfiniteSupplyingSpliterator<T> */
        protected InfiniteSupplyingSpliterator(long estimate) {
            this.estimate = estimate;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$InfiniteSupplyingSpliterator != java.util.stream.StreamSpliterators$InfiniteSupplyingSpliterator<T> */
        @Override // j$.util.Spliterator
        public long estimateSize() {
            return this.estimate;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$InfiniteSupplyingSpliterator != java.util.stream.StreamSpliterators$InfiniteSupplyingSpliterator<T> */
        @Override // j$.util.Spliterator
        public int characteristics() {
            return 1024;
        }

        /* loaded from: classes2.dex */
        static final class OfRef<T> extends InfiniteSupplyingSpliterator<T> {
            final Supplier<T> s;

            /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<T> */
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$InfiniteSupplyingSpliterator$OfRef != java.util.stream.StreamSpliterators$InfiniteSupplyingSpliterator$OfRef<T> */
            public OfRef(long size, Supplier<T> supplier) {
                super(size);
                this.s = supplier;
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<? super T> */
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$InfiniteSupplyingSpliterator$OfRef != java.util.stream.StreamSpliterators$InfiniteSupplyingSpliterator$OfRef<T> */
            @Override // j$.util.Spliterator
            public boolean tryAdvance(Consumer<? super T> consumer) {
                consumer.getClass();
                consumer.accept((T) this.s.get());
                return true;
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$InfiniteSupplyingSpliterator$OfRef != java.util.stream.StreamSpliterators$InfiniteSupplyingSpliterator$OfRef<T> */
            @Override // j$.util.Spliterator
            public Spliterator<T> trySplit() {
                if (this.estimate == 0) {
                    return null;
                }
                long j = this.estimate >>> 1;
                this.estimate = j;
                return new OfRef(j, this.s);
            }
        }

        /* loaded from: classes2.dex */
        static final class OfInt extends InfiniteSupplyingSpliterator<Integer> implements Spliterator.OfInt {
            final IntSupplier s;

            @Override // j$.util.stream.StreamSpliterators.InfiniteSupplyingSpliterator, j$.util.Spliterator
            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                Spliterator.OfInt.CC.$default$forEachRemaining((Spliterator.OfInt) this, consumer);
            }

            @Override // j$.util.Spliterator.OfInt
            public /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
                Spliterator.OfInt.CC.$default$forEachRemaining((Spliterator.OfInt) this, intConsumer);
            }

            @Override // j$.util.Spliterator.OfPrimitive
            public /* bridge */ /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
                forEachRemaining((IntConsumer) intConsumer);
            }

            @Override // j$.util.Spliterator
            public /* synthetic */ boolean tryAdvance(Consumer consumer) {
                return Spliterator.OfInt.CC.$default$tryAdvance((Spliterator.OfInt) this, consumer);
            }

            public OfInt(long size, IntSupplier s) {
                super(size);
                this.s = s;
            }

            @Override // j$.util.Spliterator.OfInt
            public boolean tryAdvance(IntConsumer action) {
                action.getClass();
                action.accept(this.s.getAsInt());
                return true;
            }

            @Override // j$.util.Spliterator
            public Spliterator.OfInt trySplit() {
                if (this.estimate == 0) {
                    return null;
                }
                long j = this.estimate >>> 1;
                this.estimate = j;
                return new OfInt(j, this.s);
            }
        }

        /* loaded from: classes2.dex */
        static final class OfLong extends InfiniteSupplyingSpliterator<Long> implements Spliterator.OfLong {
            final LongSupplier s;

            @Override // j$.util.stream.StreamSpliterators.InfiniteSupplyingSpliterator, j$.util.Spliterator
            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                Spliterator.OfLong.CC.$default$forEachRemaining((Spliterator.OfLong) this, consumer);
            }

            @Override // j$.util.Spliterator.OfLong
            public /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
                Spliterator.OfLong.CC.$default$forEachRemaining((Spliterator.OfLong) this, longConsumer);
            }

            @Override // j$.util.Spliterator.OfPrimitive
            public /* bridge */ /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
                forEachRemaining((LongConsumer) longConsumer);
            }

            @Override // j$.util.Spliterator
            public /* synthetic */ boolean tryAdvance(Consumer consumer) {
                return Spliterator.OfLong.CC.$default$tryAdvance((Spliterator.OfLong) this, consumer);
            }

            public OfLong(long size, LongSupplier s) {
                super(size);
                this.s = s;
            }

            @Override // j$.util.Spliterator.OfLong
            public boolean tryAdvance(LongConsumer action) {
                action.getClass();
                action.accept(this.s.getAsLong());
                return true;
            }

            @Override // j$.util.Spliterator
            public Spliterator.OfLong trySplit() {
                if (this.estimate == 0) {
                    return null;
                }
                long j = this.estimate >>> 1;
                this.estimate = j;
                return new OfLong(j, this.s);
            }
        }

        /* loaded from: classes2.dex */
        static final class OfDouble extends InfiniteSupplyingSpliterator<Double> implements Spliterator.OfDouble {
            final DoubleSupplier s;

            @Override // j$.util.stream.StreamSpliterators.InfiniteSupplyingSpliterator, j$.util.Spliterator
            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                Spliterator.OfDouble.CC.$default$forEachRemaining((Spliterator.OfDouble) this, consumer);
            }

            @Override // j$.util.Spliterator.OfDouble
            public /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
                Spliterator.OfDouble.CC.$default$forEachRemaining((Spliterator.OfDouble) this, doubleConsumer);
            }

            @Override // j$.util.Spliterator.OfPrimitive
            public /* bridge */ /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
                forEachRemaining((DoubleConsumer) doubleConsumer);
            }

            @Override // j$.util.Spliterator
            public /* synthetic */ boolean tryAdvance(Consumer consumer) {
                return Spliterator.OfDouble.CC.$default$tryAdvance((Spliterator.OfDouble) this, consumer);
            }

            public OfDouble(long size, DoubleSupplier s) {
                super(size);
                this.s = s;
            }

            @Override // j$.util.Spliterator.OfDouble
            public boolean tryAdvance(DoubleConsumer action) {
                action.getClass();
                action.accept(this.s.getAsDouble());
                return true;
            }

            @Override // j$.util.Spliterator
            public Spliterator.OfDouble trySplit() {
                if (this.estimate == 0) {
                    return null;
                }
                long j = this.estimate >>> 1;
                this.estimate = j;
                return new OfDouble(j, this.s);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static abstract class ArrayBuffer {
        int index;

        ArrayBuffer() {
        }

        void reset() {
            this.index = 0;
        }

        /* loaded from: classes2.dex */
        static final class OfRef<T> extends ArrayBuffer implements Consumer<T> {
            final Object[] array;

            @Override // j$.util.function.Consumer
            public /* synthetic */ Consumer andThen(Consumer consumer) {
                return consumer.getClass();
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$ArrayBuffer$OfRef != java.util.stream.StreamSpliterators$ArrayBuffer$OfRef<T> */
            OfRef(int size) {
                this.array = new Object[size];
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$ArrayBuffer$OfRef != java.util.stream.StreamSpliterators$ArrayBuffer$OfRef<T> */
            @Override // j$.util.function.Consumer
            public void accept(T t) {
                Object[] objArr = this.array;
                int i = this.index;
                this.index = i + 1;
                objArr[i] = t;
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<? super T> */
            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$ArrayBuffer$OfRef != java.util.stream.StreamSpliterators$ArrayBuffer$OfRef<T> */
            public void forEach(Consumer<? super T> consumer, long fence) {
                for (int i = 0; i < fence; i++) {
                    consumer.accept(this.array[i]);
                }
            }
        }

        /* loaded from: classes2.dex */
        public static abstract class OfPrimitive<T_CONS> extends ArrayBuffer {
            int index;

            abstract void forEach(T_CONS t_cons, long j);

            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$ArrayBuffer$OfPrimitive != java.util.stream.StreamSpliterators$ArrayBuffer$OfPrimitive<T_CONS> */
            OfPrimitive() {
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.StreamSpliterators$ArrayBuffer$OfPrimitive != java.util.stream.StreamSpliterators$ArrayBuffer$OfPrimitive<T_CONS> */
            @Override // j$.util.stream.StreamSpliterators.ArrayBuffer
            void reset() {
                this.index = 0;
            }
        }

        /* loaded from: classes2.dex */
        public static final class OfInt extends OfPrimitive<IntConsumer> implements IntConsumer {
            final int[] array;

            @Override // j$.util.function.IntConsumer
            public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
                return intConsumer.getClass();
            }

            OfInt(int size) {
                this.array = new int[size];
            }

            @Override // j$.util.function.IntConsumer
            public void accept(int t) {
                int[] iArr = this.array;
                int i = this.index;
                this.index = i + 1;
                iArr[i] = t;
            }

            public void forEach(IntConsumer action, long fence) {
                for (int i = 0; i < fence; i++) {
                    action.accept(this.array[i]);
                }
            }
        }

        /* loaded from: classes2.dex */
        public static final class OfLong extends OfPrimitive<LongConsumer> implements LongConsumer {
            final long[] array;

            @Override // j$.util.function.LongConsumer
            public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
                return longConsumer.getClass();
            }

            OfLong(int size) {
                this.array = new long[size];
            }

            @Override // j$.util.function.LongConsumer
            public void accept(long t) {
                long[] jArr = this.array;
                int i = this.index;
                this.index = i + 1;
                jArr[i] = t;
            }

            public void forEach(LongConsumer action, long fence) {
                for (int i = 0; i < fence; i++) {
                    action.accept(this.array[i]);
                }
            }
        }

        /* loaded from: classes2.dex */
        public static final class OfDouble extends OfPrimitive<DoubleConsumer> implements DoubleConsumer {
            final double[] array;

            @Override // j$.util.function.DoubleConsumer
            public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
                return doubleConsumer.getClass();
            }

            OfDouble(int size) {
                this.array = new double[size];
            }

            @Override // j$.util.function.DoubleConsumer
            public void accept(double t) {
                double[] dArr = this.array;
                int i = this.index;
                this.index = i + 1;
                dArr[i] = t;
            }

            public void forEach(DoubleConsumer action, long fence) {
                for (int i = 0; i < fence; i++) {
                    action.accept(this.array[i]);
                }
            }
        }
    }
}
