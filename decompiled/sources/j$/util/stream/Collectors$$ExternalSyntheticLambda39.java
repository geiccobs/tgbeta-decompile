package j$.util.stream;

import j$.util.StringJoiner;
import j$.util.function.BiFunction;
import j$.util.function.BinaryOperator;
import j$.util.function.Function;
/* loaded from: classes2.dex */
public final /* synthetic */ class Collectors$$ExternalSyntheticLambda39 implements BinaryOperator {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda39 INSTANCE = new Collectors$$ExternalSyntheticLambda39();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda39() {
    }

    @Override // j$.util.function.BiFunction
    public /* synthetic */ BiFunction andThen(Function function) {
        return function.getClass();
    }

    @Override // j$.util.function.BiFunction
    public final Object apply(Object obj, Object obj2) {
        return ((StringJoiner) obj).merge((StringJoiner) obj2);
    }
}
