package j$.util;

import j$.util.function.IntConsumer;
import j$.util.function.IntSupplier;
import j$.util.function.Supplier;
import java.util.NoSuchElementException;
/* loaded from: classes2.dex */
public final class OptionalInt {
    private static final OptionalInt EMPTY = new OptionalInt();
    private final boolean isPresent;
    private final int value;

    private OptionalInt() {
        this.isPresent = false;
        this.value = 0;
    }

    public static OptionalInt empty() {
        return EMPTY;
    }

    private OptionalInt(int value) {
        this.isPresent = true;
        this.value = value;
    }

    public static OptionalInt of(int value) {
        return new OptionalInt(value);
    }

    public int getAsInt() {
        if (!this.isPresent) {
            throw new NoSuchElementException("No value present");
        }
        return this.value;
    }

    public boolean isPresent() {
        return this.isPresent;
    }

    public void ifPresent(IntConsumer consumer) {
        if (this.isPresent) {
            consumer.accept(this.value);
        }
    }

    public int orElse(int other) {
        return this.isPresent ? this.value : other;
    }

    public int orElseGet(IntSupplier other) {
        return this.isPresent ? this.value : other.getAsInt();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<X extends java.lang.Throwable> */
    public <X extends Throwable> int orElseThrow(Supplier<X> supplier) {
        if (this.isPresent) {
            return this.value;
        }
        throw supplier.get();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof OptionalInt)) {
            return false;
        }
        OptionalInt other = (OptionalInt) obj;
        boolean z = this.isPresent;
        return (!z || !other.isPresent) ? z == other.isPresent : this.value == other.value;
    }

    public int hashCode() {
        if (this.isPresent) {
            return this.value;
        }
        return 0;
    }

    public String toString() {
        return this.isPresent ? String.format("OptionalInt[%s]", Integer.valueOf(this.value)) : "OptionalInt.empty";
    }
}
