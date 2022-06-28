package j$.util.concurrent.atomic;

import j$.util.function.BinaryOperator;
import j$.util.function.UnaryOperator;
import java.util.concurrent.atomic.AtomicReference;
/* loaded from: classes2.dex */
public class DesugarAtomicReference {
    private DesugarAtomicReference() {
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.UnaryOperator != java.util.function.UnaryOperator<V> */
    /* JADX WARN: Multi-variable type inference failed */
    public static <V> V getAndUpdate(AtomicReference<V> atomic, UnaryOperator<V> unaryOperator) {
        V prev;
        do {
            prev = atomic.get();
        } while (!atomic.compareAndSet(prev, unaryOperator.apply(prev)));
        return prev;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.UnaryOperator != java.util.function.UnaryOperator<V> */
    public static <V> V updateAndGet(AtomicReference<V> atomic, UnaryOperator<V> unaryOperator) {
        V prev;
        V next;
        do {
            prev = atomic.get();
            next = (V) unaryOperator.apply(prev);
        } while (!atomic.compareAndSet(prev, next));
        return next;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BinaryOperator != java.util.function.BinaryOperator<V> */
    /* JADX WARN: Multi-variable type inference failed */
    public static <V> V getAndAccumulate(AtomicReference<V> atomic, V x, BinaryOperator<V> binaryOperator) {
        V prev;
        do {
            prev = atomic.get();
        } while (!atomic.compareAndSet(prev, binaryOperator.apply(prev, x)));
        return prev;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.BinaryOperator != java.util.function.BinaryOperator<V> */
    public static <V> V accumulateAndGet(AtomicReference<V> atomic, V x, BinaryOperator<V> binaryOperator) {
        V prev;
        V next;
        do {
            prev = atomic.get();
            next = (V) binaryOperator.apply(prev, x);
        } while (!atomic.compareAndSet(prev, next));
        return next;
    }
}
