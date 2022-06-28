package j$.util;
/* loaded from: classes2.dex */
public class OptionalConversions {
    private OptionalConversions() {
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Optional != java.util.Optional<T> */
    public static <T> java.util.Optional<T> convert(Optional<T> optional) {
        if (optional == null) {
            return null;
        }
        if (optional.isPresent()) {
            return java.util.Optional.of(optional.get());
        }
        return java.util.Optional.empty();
    }

    /* JADX WARN: Generic types in debug info not equals: java.util.Optional != j$.util.Optional<T> */
    public static <T> Optional<T> convert(java.util.Optional<T> optional) {
        if (optional == null) {
            return null;
        }
        if (optional.isPresent()) {
            return Optional.of(optional.get());
        }
        return Optional.empty();
    }

    public static java.util.OptionalDouble convert(OptionalDouble optionalDouble) {
        if (optionalDouble == null) {
            return null;
        }
        if (optionalDouble.isPresent()) {
            return java.util.OptionalDouble.of(optionalDouble.getAsDouble());
        }
        return java.util.OptionalDouble.empty();
    }

    public static OptionalDouble convert(java.util.OptionalDouble optionalDouble) {
        if (optionalDouble == null) {
            return null;
        }
        if (optionalDouble.isPresent()) {
            return OptionalDouble.of(optionalDouble.getAsDouble());
        }
        return OptionalDouble.empty();
    }

    public static java.util.OptionalLong convert(OptionalLong optionalLong) {
        if (optionalLong == null) {
            return null;
        }
        if (optionalLong.isPresent()) {
            return java.util.OptionalLong.of(optionalLong.getAsLong());
        }
        return java.util.OptionalLong.empty();
    }

    public static OptionalLong convert(java.util.OptionalLong optionalLong) {
        if (optionalLong == null) {
            return null;
        }
        if (optionalLong.isPresent()) {
            return OptionalLong.of(optionalLong.getAsLong());
        }
        return OptionalLong.empty();
    }

    public static java.util.OptionalInt convert(OptionalInt optionalInt) {
        if (optionalInt == null) {
            return null;
        }
        if (optionalInt.isPresent()) {
            return java.util.OptionalInt.of(optionalInt.getAsInt());
        }
        return java.util.OptionalInt.empty();
    }

    public static OptionalInt convert(java.util.OptionalInt optionalInt) {
        if (optionalInt == null) {
            return null;
        }
        if (optionalInt.isPresent()) {
            return OptionalInt.of(optionalInt.getAsInt());
        }
        return OptionalInt.empty();
    }
}
