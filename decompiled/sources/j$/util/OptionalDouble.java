package j$.util;

import j$.util.function.DoubleConsumer;
import j$.util.function.DoubleSupplier;
import j$.util.function.Supplier;
import java.util.NoSuchElementException;
/* loaded from: classes2.dex */
public final class OptionalDouble {
    private static final OptionalDouble EMPTY = new OptionalDouble();
    private final boolean isPresent;
    private final double value;

    private OptionalDouble() {
        this.isPresent = false;
        this.value = Double.NaN;
    }

    public static OptionalDouble empty() {
        return EMPTY;
    }

    private OptionalDouble(double value) {
        this.isPresent = true;
        this.value = value;
    }

    public static OptionalDouble of(double value) {
        return new OptionalDouble(value);
    }

    public double getAsDouble() {
        if (!this.isPresent) {
            throw new NoSuchElementException("No value present");
        }
        return this.value;
    }

    public boolean isPresent() {
        return this.isPresent;
    }

    public void ifPresent(DoubleConsumer consumer) {
        if (this.isPresent) {
            consumer.accept(this.value);
        }
    }

    public double orElse(double other) {
        return this.isPresent ? this.value : other;
    }

    public double orElseGet(DoubleSupplier other) {
        return this.isPresent ? this.value : other.getAsDouble();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<X extends java.lang.Throwable> */
    public <X extends Throwable> double orElseThrow(Supplier<X> supplier) {
        if (this.isPresent) {
            return this.value;
        }
        throw supplier.get();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof OptionalDouble)) {
            return false;
        }
        OptionalDouble other = (OptionalDouble) obj;
        boolean z = this.isPresent;
        return (!z || !other.isPresent) ? z == other.isPresent : Double.compare(this.value, other.value) == 0;
    }

    public int hashCode() {
        if (this.isPresent) {
            return Double.doubleToLongBits(this.value);
        }
        return 0;
    }

    public String toString() {
        return this.isPresent ? String.format("OptionalDouble[%s]", Double.valueOf(this.value)) : "OptionalDouble.empty";
    }
}
