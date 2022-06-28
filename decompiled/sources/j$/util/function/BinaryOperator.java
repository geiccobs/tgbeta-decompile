package j$.util.function;

import j$.util.function.BinaryOperator;
import java.util.Comparator;
/* loaded from: classes2.dex */
public interface BinaryOperator<T> extends BiFunction<T, T, T> {

    /* renamed from: j$.util.function.BinaryOperator$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static <T> BinaryOperator<T> minBy(final Comparator<? super T> comparator) {
            comparator.getClass();
            return new BinaryOperator() { // from class: j$.util.function.BinaryOperator$$ExternalSyntheticLambda1
                @Override // j$.util.function.BiFunction
                public /* synthetic */ BiFunction andThen(Function function) {
                    return function.getClass();
                }

                @Override // j$.util.function.BiFunction
                public final Object apply(Object obj, Object obj2) {
                    return BinaryOperator.CC.lambda$minBy$0(comparator, obj, obj2);
                }
            };
        }

        public static /* synthetic */ Object lambda$minBy$0(Comparator comparator, Object a, Object b) {
            return comparator.compare(a, b) <= 0 ? a : b;
        }

        public static <T> BinaryOperator<T> maxBy(final Comparator<? super T> comparator) {
            comparator.getClass();
            return new BinaryOperator() { // from class: j$.util.function.BinaryOperator$$ExternalSyntheticLambda0
                @Override // j$.util.function.BiFunction
                public /* synthetic */ BiFunction andThen(Function function) {
                    return function.getClass();
                }

                @Override // j$.util.function.BiFunction
                public final Object apply(Object obj, Object obj2) {
                    return BinaryOperator.CC.lambda$maxBy$1(comparator, obj, obj2);
                }
            };
        }

        public static /* synthetic */ Object lambda$maxBy$1(Comparator comparator, Object a, Object b) {
            return comparator.compare(a, b) >= 0 ? a : b;
        }
    }
}
