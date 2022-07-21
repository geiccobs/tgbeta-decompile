package j$.util;

import j$.util.function.Function;
import j$.util.function.ToIntFunction;
import java.util.Collections;
import java.util.Comparator;
/* renamed from: j$.util.Comparator$-CC */
/* loaded from: classes2.dex */
public final /* synthetic */ class Comparator$CC {
    public static Comparator a() {
        return EnumC0044f.INSTANCE;
    }

    public static <T, U extends Comparable<? super U>> Comparator<T> comparing(Function<? super T, ? extends U> function) {
        function.getClass();
        return new C0042d(function);
    }

    public static <T> Comparator<T> comparingInt(ToIntFunction<? super T> toIntFunction) {
        toIntFunction.getClass();
        return new C0042d(toIntFunction);
    }

    public static <T extends Comparable<? super T>> Comparator<T> reverseOrder() {
        return Collections.reverseOrder();
    }
}
