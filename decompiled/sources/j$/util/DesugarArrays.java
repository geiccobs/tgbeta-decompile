package j$.util;

import j$.util.Spliterator;
import j$.util.function.IntConsumer;
import j$.util.function.IntFunction;
import j$.util.function.IntToDoubleFunction;
import j$.util.function.IntToLongFunction;
import j$.util.function.IntUnaryOperator;
import j$.util.stream.DoubleStream;
import j$.util.stream.IntStream;
import j$.util.stream.LongStream;
import j$.util.stream.Stream;
import j$.util.stream.StreamSupport;
import java.util.Arrays;
/* loaded from: classes2.dex */
public class DesugarArrays {
    static final /* synthetic */ boolean $assertionsDisabled = true;

    private DesugarArrays() {
    }

    public static boolean deepEquals0(Object e1, Object e2) {
        if ($assertionsDisabled || e1 != null) {
            if ((e1 instanceof Object[]) && (e2 instanceof Object[])) {
                boolean eq = Arrays.deepEquals((Object[]) e1, (Object[]) e2);
                return eq;
            }
            boolean eq2 = e1 instanceof byte[];
            if (eq2 && (e2 instanceof byte[])) {
                boolean eq3 = Arrays.equals((byte[]) e1, (byte[]) e2);
                return eq3;
            }
            boolean eq4 = e1 instanceof short[];
            if (eq4 && (e2 instanceof short[])) {
                boolean eq5 = Arrays.equals((short[]) e1, (short[]) e2);
                return eq5;
            }
            boolean eq6 = e1 instanceof int[];
            if (eq6 && (e2 instanceof int[])) {
                boolean eq7 = Arrays.equals((int[]) e1, (int[]) e2);
                return eq7;
            }
            boolean eq8 = e1 instanceof long[];
            if (eq8 && (e2 instanceof long[])) {
                boolean eq9 = Arrays.equals((long[]) e1, (long[]) e2);
                return eq9;
            }
            boolean eq10 = e1 instanceof char[];
            if (eq10 && (e2 instanceof char[])) {
                boolean eq11 = Arrays.equals((char[]) e1, (char[]) e2);
                return eq11;
            }
            boolean eq12 = e1 instanceof float[];
            if (eq12 && (e2 instanceof float[])) {
                boolean eq13 = Arrays.equals((float[]) e1, (float[]) e2);
                return eq13;
            }
            boolean eq14 = e1 instanceof double[];
            if (eq14 && (e2 instanceof double[])) {
                boolean eq15 = Arrays.equals((double[]) e1, (double[]) e2);
                return eq15;
            }
            boolean eq16 = e1 instanceof boolean[];
            if (eq16 && (e2 instanceof boolean[])) {
                boolean eq17 = Arrays.equals((boolean[]) e1, (boolean[]) e2);
                return eq17;
            }
            boolean eq18 = e1.equals(e2);
            return eq18;
        }
        throw new AssertionError();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.IntFunction != java.util.function.IntFunction<? extends T> */
    public static <T> void setAll(T[] array, IntFunction<? extends T> intFunction) {
        intFunction.getClass();
        for (int i = 0; i < array.length; i++) {
            array[i] = intFunction.apply(i);
        }
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.IntFunction != java.util.function.IntFunction<? extends T> */
    public static <T> void parallelSetAll(final T[] array, final IntFunction<? extends T> intFunction) {
        intFunction.getClass();
        IntStream.CC.range(0, array.length).parallel().forEach(new IntConsumer() { // from class: j$.util.DesugarArrays$$ExternalSyntheticLambda3
            @Override // j$.util.function.IntConsumer
            public final void accept(int i) {
                DesugarArrays.lambda$parallelSetAll$0(array, intFunction, i);
            }

            @Override // j$.util.function.IntConsumer
            public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
                return intConsumer.getClass();
            }
        });
    }

    public static /* synthetic */ void lambda$parallelSetAll$0(Object[] array, IntFunction generator, int i) {
        array[i] = generator.apply(i);
    }

    public static void setAll(int[] array, IntUnaryOperator generator) {
        generator.getClass();
        for (int i = 0; i < array.length; i++) {
            array[i] = generator.applyAsInt(i);
        }
    }

    public static void parallelSetAll(final int[] array, final IntUnaryOperator generator) {
        generator.getClass();
        IntStream.CC.range(0, array.length).parallel().forEach(new IntConsumer() { // from class: j$.util.DesugarArrays$$ExternalSyntheticLambda1
            @Override // j$.util.function.IntConsumer
            public final void accept(int i) {
                DesugarArrays.lambda$parallelSetAll$1(array, generator, i);
            }

            @Override // j$.util.function.IntConsumer
            public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
                return intConsumer.getClass();
            }
        });
    }

    public static /* synthetic */ void lambda$parallelSetAll$1(int[] array, IntUnaryOperator generator, int i) {
        array[i] = generator.applyAsInt(i);
    }

    public static void setAll(long[] array, IntToLongFunction generator) {
        generator.getClass();
        for (int i = 0; i < array.length; i++) {
            array[i] = generator.applyAsLong(i);
        }
    }

    public static void parallelSetAll(final long[] array, final IntToLongFunction generator) {
        generator.getClass();
        IntStream.CC.range(0, array.length).parallel().forEach(new IntConsumer() { // from class: j$.util.DesugarArrays$$ExternalSyntheticLambda2
            @Override // j$.util.function.IntConsumer
            public final void accept(int i) {
                DesugarArrays.lambda$parallelSetAll$2(array, generator, i);
            }

            @Override // j$.util.function.IntConsumer
            public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
                return intConsumer.getClass();
            }
        });
    }

    public static /* synthetic */ void lambda$parallelSetAll$2(long[] array, IntToLongFunction generator, int i) {
        array[i] = generator.applyAsLong(i);
    }

    public static void setAll(double[] array, IntToDoubleFunction generator) {
        generator.getClass();
        for (int i = 0; i < array.length; i++) {
            array[i] = generator.applyAsDouble(i);
        }
    }

    public static void parallelSetAll(final double[] array, final IntToDoubleFunction generator) {
        generator.getClass();
        IntStream.CC.range(0, array.length).parallel().forEach(new IntConsumer() { // from class: j$.util.DesugarArrays$$ExternalSyntheticLambda0
            @Override // j$.util.function.IntConsumer
            public final void accept(int i) {
                DesugarArrays.lambda$parallelSetAll$3(array, generator, i);
            }

            @Override // j$.util.function.IntConsumer
            public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
                return intConsumer.getClass();
            }
        });
    }

    public static /* synthetic */ void lambda$parallelSetAll$3(double[] array, IntToDoubleFunction generator, int i) {
        array[i] = generator.applyAsDouble(i);
    }

    public static <T> Spliterator<T> spliterator(T[] array) {
        return Spliterators.spliterator(array, 1040);
    }

    public static <T> Spliterator<T> spliterator(T[] array, int startInclusive, int endExclusive) {
        return Spliterators.spliterator(array, startInclusive, endExclusive, 1040);
    }

    public static Spliterator.OfInt spliterator(int[] array) {
        return Spliterators.spliterator(array, 1040);
    }

    public static Spliterator.OfInt spliterator(int[] array, int startInclusive, int endExclusive) {
        return Spliterators.spliterator(array, startInclusive, endExclusive, 1040);
    }

    public static Spliterator.OfLong spliterator(long[] array) {
        return Spliterators.spliterator(array, 1040);
    }

    public static Spliterator.OfLong spliterator(long[] array, int startInclusive, int endExclusive) {
        return Spliterators.spliterator(array, startInclusive, endExclusive, 1040);
    }

    public static Spliterator.OfDouble spliterator(double[] array) {
        return Spliterators.spliterator(array, 1040);
    }

    public static Spliterator.OfDouble spliterator(double[] array, int startInclusive, int endExclusive) {
        return Spliterators.spliterator(array, startInclusive, endExclusive, 1040);
    }

    public static <T> Stream<T> stream(T[] array) {
        return stream(array, 0, array.length);
    }

    public static <T> Stream<T> stream(T[] array, int startInclusive, int endExclusive) {
        return StreamSupport.stream(spliterator(array, startInclusive, endExclusive), false);
    }

    public static IntStream stream(int[] array) {
        return stream(array, 0, array.length);
    }

    public static IntStream stream(int[] array, int startInclusive, int endExclusive) {
        return StreamSupport.intStream(spliterator(array, startInclusive, endExclusive), false);
    }

    public static LongStream stream(long[] array) {
        return stream(array, 0, array.length);
    }

    public static LongStream stream(long[] array, int startInclusive, int endExclusive) {
        return StreamSupport.longStream(spliterator(array, startInclusive, endExclusive), false);
    }

    public static DoubleStream stream(double[] array) {
        return stream(array, 0, array.length);
    }

    public static DoubleStream stream(double[] array, int startInclusive, int endExclusive) {
        return StreamSupport.doubleStream(spliterator(array, startInclusive, endExclusive), false);
    }
}
