package j$.util.stream;

import j$.util.function.Function;
/* loaded from: classes2.dex */
public final /* synthetic */ class Collectors$$ExternalSyntheticLambda54 implements Function {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda54 INSTANCE = new Collectors$$ExternalSyntheticLambda54();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda54() {
    }

    @Override // j$.util.function.Function
    public /* synthetic */ Function andThen(Function function) {
        return function.getClass();
    }

    @Override // j$.util.function.Function
    public final Object apply(Object obj) {
        return Collectors.lambda$counting$9(obj);
    }

    @Override // j$.util.function.Function
    public /* synthetic */ Function compose(Function function) {
        return function.getClass();
    }
}
