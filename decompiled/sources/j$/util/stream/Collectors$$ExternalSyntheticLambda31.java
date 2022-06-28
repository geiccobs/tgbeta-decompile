package j$.util.stream;

import j$.util.function.BiFunction;
import j$.util.function.BinaryOperator;
import j$.util.function.Function;
/* loaded from: classes2.dex */
public final /* synthetic */ class Collectors$$ExternalSyntheticLambda31 implements BinaryOperator {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda31 INSTANCE = new Collectors$$ExternalSyntheticLambda31();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda31() {
    }

    @Override // j$.util.function.BiFunction
    public /* synthetic */ BiFunction andThen(Function function) {
        return function.getClass();
    }

    @Override // j$.util.function.BiFunction
    public final Object apply(Object obj, Object obj2) {
        return Long.valueOf(((Long) obj).longValue() + ((Long) obj2).longValue());
    }
}
