package j$.util.stream;

import j$.util.function.BiFunction;
import j$.util.function.BinaryOperator;
import j$.util.function.Function;
import j$.util.stream.Collectors;
/* loaded from: classes2.dex */
public final /* synthetic */ class Collectors$$ExternalSyntheticLambda40 implements BinaryOperator {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda40 INSTANCE = new Collectors$$ExternalSyntheticLambda40();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda40() {
    }

    @Override // j$.util.function.BiFunction
    public /* synthetic */ BiFunction andThen(Function function) {
        return function.getClass();
    }

    @Override // j$.util.function.BiFunction
    public final Object apply(Object obj, Object obj2) {
        return Collectors.lambda$reducing$39((Collectors.C1OptionalBox) obj, (Collectors.C1OptionalBox) obj2);
    }
}
