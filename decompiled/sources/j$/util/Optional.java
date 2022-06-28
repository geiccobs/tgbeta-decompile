package j$.util;

import j$.util.function.Consumer;
import j$.util.function.Function;
import j$.util.function.Predicate;
import j$.util.function.Supplier;
import java.util.NoSuchElementException;
/* loaded from: classes2.dex */
public final class Optional<T> {
    private static final Optional<?> EMPTY = new Optional<>();
    private final T value;

    /* JADX WARN: Generic types in debug info not equals: j$.util.Optional != java.util.Optional<T> */
    private Optional() {
        this.value = null;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Optional != java.util.Optional<T> */
    public static <T> Optional<T> empty() {
        return (Optional<T>) EMPTY;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Optional != java.util.Optional<T> */
    private Optional(T value) {
        value.getClass();
        this.value = value;
    }

    public static <T> Optional<T> of(T value) {
        return new Optional<>(value);
    }

    public static <T> Optional<T> ofNullable(T value) {
        return value == null ? empty() : of(value);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Optional != java.util.Optional<T> */
    public T get() {
        T t = this.value;
        if (t == null) {
            throw new NoSuchElementException("No value present");
        }
        return t;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Optional != java.util.Optional<T> */
    public boolean isPresent() {
        return this.value != null;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Optional != java.util.Optional<T> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<? super T> */
    public void ifPresent(Consumer<? super T> consumer) {
        Object obj = (T) this.value;
        if (obj != null) {
            consumer.accept(obj);
        }
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Optional != java.util.Optional<T> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Predicate != java.util.function.Predicate<? super T> */
    public Optional<T> filter(Predicate<? super T> predicate) {
        predicate.getClass();
        if (!isPresent()) {
            return this;
        }
        return predicate.test((T) this.value) ? this : empty();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Optional != java.util.Optional<T> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<? super T, ? extends U> */
    public <U> Optional<U> map(Function<? super T, ? extends U> function) {
        function.getClass();
        if (!isPresent()) {
            return empty();
        }
        return ofNullable(function.apply((T) this.value));
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Optional != java.util.Optional<T> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<? super T, java.util.Optional<U>> */
    public <U> Optional<U> flatMap(Function<? super T, Optional<U>> function) {
        function.getClass();
        if (!isPresent()) {
            return empty();
        }
        Optional<U> apply = function.apply((T) this.value);
        apply.getClass();
        return apply;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Optional != java.util.Optional<T> */
    public T orElse(T other) {
        T t = this.value;
        return t != null ? t : other;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Optional != java.util.Optional<T> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<? extends T> */
    public T orElseGet(Supplier<? extends T> supplier) {
        T t = this.value;
        return t != null ? t : supplier.get();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Optional != java.util.Optional<T> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<? extends X extends java.lang.Throwable> */
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> supplier) {
        T t = this.value;
        if (t != null) {
            return t;
        }
        throw supplier.get();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Optional != java.util.Optional<?> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.Optional != java.util.Optional<T> */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Optional)) {
            return false;
        }
        return Objects.equals(this.value, ((Optional) obj).value);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Optional != java.util.Optional<T> */
    public int hashCode() {
        return Objects.hashCode(this.value);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Optional != java.util.Optional<T> */
    public String toString() {
        T t = this.value;
        return t != null ? String.format("Optional[%s]", t) : "Optional.empty";
    }
}
