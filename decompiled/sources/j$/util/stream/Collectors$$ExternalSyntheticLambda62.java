package j$.util.stream;

import j$.util.function.Function;
/* loaded from: classes2.dex */
public final /* synthetic */ class Collectors$$ExternalSyntheticLambda62 implements Function {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda62 INSTANCE = new Collectors$$ExternalSyntheticLambda62();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda62() {
    }

    @Override // j$.util.function.Function
    public /* synthetic */ Function andThen(Function function) {
        return function.getClass();
    }

    @Override // j$.util.function.Function
    public final Object apply(Object obj) {
        return Collectors.lambda$averagingLong$29((long[]) obj);
    }

    @Override // j$.util.function.Function
    public /* synthetic */ Function compose(Function function) {
        return function.getClass();
    }
}
