package j$.util.stream;

import j$.util.function.BiFunction;
import j$.util.function.BinaryOperator;
import j$.util.function.Function;
/* loaded from: classes2.dex */
public final /* synthetic */ class Collectors$$ExternalSyntheticLambda42 implements BinaryOperator {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda42 INSTANCE = new Collectors$$ExternalSyntheticLambda42();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda42() {
    }

    @Override // j$.util.function.BiFunction
    public /* synthetic */ BiFunction andThen(Function function) {
        return function.getClass();
    }

    @Override // j$.util.function.BiFunction
    public final Object apply(Object obj, Object obj2) {
        return Collectors.lambda$summingDouble$20((double[]) obj, (double[]) obj2);
    }
}