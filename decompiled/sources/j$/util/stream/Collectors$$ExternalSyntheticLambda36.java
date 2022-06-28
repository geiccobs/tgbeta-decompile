package j$.util.stream;

import j$.util.function.BiFunction;
import j$.util.function.BinaryOperator;
import j$.util.function.Function;
import java.util.List;
/* loaded from: classes2.dex */
public final /* synthetic */ class Collectors$$ExternalSyntheticLambda36 implements BinaryOperator {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda36 INSTANCE = new Collectors$$ExternalSyntheticLambda36();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda36() {
    }

    @Override // j$.util.function.BiFunction
    public /* synthetic */ BiFunction andThen(Function function) {
        return function.getClass();
    }

    @Override // j$.util.function.BiFunction
    public final Object apply(Object obj, Object obj2) {
        return ((List) obj).addAll((List) obj2);
    }
}
