package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.Supplier;
import j$.util.stream.DoublePipeline;
import j$.util.stream.IntPipeline;
import j$.util.stream.LongPipeline;
import j$.util.stream.ReferencePipeline;
/* loaded from: classes2.dex */
public final class StreamSupport {
    private StreamSupport() {
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<T> */
    public static <T> Stream<T> stream(Spliterator<T> spliterator, boolean parallel) {
        spliterator.getClass();
        return new ReferencePipeline.Head((Spliterator<?>) spliterator, StreamOpFlag.fromCharacteristics((Spliterator<?>) spliterator), parallel);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<? extends java.util.Spliterator<T>> */
    public static <T> Stream<T> stream(Supplier<? extends Spliterator<T>> supplier, int characteristics, boolean parallel) {
        supplier.getClass();
        return new ReferencePipeline.Head((Supplier<? extends Spliterator<?>>) supplier, StreamOpFlag.fromCharacteristics(characteristics), parallel);
    }

    public static IntStream intStream(Spliterator.OfInt spliterator, boolean parallel) {
        return new IntPipeline.Head(spliterator, StreamOpFlag.fromCharacteristics(spliterator), parallel);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<? extends java.util.Spliterator$OfInt> */
    public static IntStream intStream(Supplier<? extends Spliterator.OfInt> supplier, int characteristics, boolean parallel) {
        return new IntPipeline.Head(supplier, StreamOpFlag.fromCharacteristics(characteristics), parallel);
    }

    public static LongStream longStream(Spliterator.OfLong spliterator, boolean parallel) {
        return new LongPipeline.Head(spliterator, StreamOpFlag.fromCharacteristics(spliterator), parallel);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<? extends java.util.Spliterator$OfLong> */
    public static LongStream longStream(Supplier<? extends Spliterator.OfLong> supplier, int characteristics, boolean parallel) {
        return new LongPipeline.Head(supplier, StreamOpFlag.fromCharacteristics(characteristics), parallel);
    }

    public static DoubleStream doubleStream(Spliterator.OfDouble spliterator, boolean parallel) {
        return new DoublePipeline.Head(spliterator, StreamOpFlag.fromCharacteristics(spliterator), parallel);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<? extends java.util.Spliterator$OfDouble> */
    public static DoubleStream doubleStream(Supplier<? extends Spliterator.OfDouble> supplier, int characteristics, boolean parallel) {
        return new DoublePipeline.Head(supplier, StreamOpFlag.fromCharacteristics(characteristics), parallel);
    }
}
