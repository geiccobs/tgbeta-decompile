package j$.util;

import j$.util.function.LongConsumer;
import j$.util.function.LongSupplier;
import j$.util.function.Supplier;
import java.util.NoSuchElementException;
/* loaded from: classes2.dex */
public final class OptionalLong {
    private static final OptionalLong EMPTY = new OptionalLong();
    private final boolean isPresent;
    private final long value;

    private OptionalLong() {
        this.isPresent = false;
        this.value = 0L;
    }

    public static OptionalLong empty() {
        return EMPTY;
    }

    private OptionalLong(long value) {
        this.isPresent = true;
        this.value = value;
    }

    public static OptionalLong of(long value) {
        return new OptionalLong(value);
    }

    public long getAsLong() {
        if (!this.isPresent) {
            throw new NoSuchElementException("No value present");
        }
        return this.value;
    }

    public boolean isPresent() {
        return this.isPresent;
    }

    public void ifPresent(LongConsumer consumer) {
        if (this.isPresent) {
            consumer.accept(this.value);
        }
    }

    public long orElse(long other) {
        return this.isPresent ? this.value : other;
    }

    public long orElseGet(LongSupplier other) {
        return this.isPresent ? this.value : other.getAsLong();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Supplier != java.util.function.Supplier<X extends java.lang.Throwable> */
    public <X extends Throwable> long orElseThrow(Supplier<X> supplier) {
        if (this.isPresent) {
            return this.value;
        }
        throw supplier.get();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof OptionalLong)) {
            return false;
        }
        OptionalLong other = (OptionalLong) obj;
        boolean z = this.isPresent;
        return (!z || !other.isPresent) ? z == other.isPresent : this.value == other.value;
    }

    public int hashCode() {
        if (this.isPresent) {
            return OptionalLong$$ExternalSyntheticBackport0.m(this.value);
        }
        return 0;
    }

    public String toString() {
        return this.isPresent ? String.format("OptionalLong[%s]", Long.valueOf(this.value)) : "OptionalLong.empty";
    }
}
