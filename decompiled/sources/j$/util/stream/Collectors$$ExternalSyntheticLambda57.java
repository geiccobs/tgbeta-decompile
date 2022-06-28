package j$.util.stream;

import j$.util.Optional;
import j$.util.function.Function;
import j$.util.stream.Collectors;
/* loaded from: classes2.dex */
public final /* synthetic */ class Collectors$$ExternalSyntheticLambda57 implements Function {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda57 INSTANCE = new Collectors$$ExternalSyntheticLambda57();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda57() {
    }

    @Override // j$.util.function.Function
    public /* synthetic */ Function andThen(Function function) {
        return function.getClass();
    }

    @Override // j$.util.function.Function
    public final Object apply(Object obj) {
        Optional ofNullable;
        ofNullable = Optional.ofNullable(((Collectors.C1OptionalBox) obj).value);
        return ofNullable;
    }

    @Override // j$.util.function.Function
    public /* synthetic */ Function compose(Function function) {
        return function.getClass();
    }
}
