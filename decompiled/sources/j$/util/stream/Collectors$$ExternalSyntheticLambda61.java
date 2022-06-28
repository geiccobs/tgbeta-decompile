package j$.util.stream;

import j$.util.function.Function;
/* loaded from: classes2.dex */
public final /* synthetic */ class Collectors$$ExternalSyntheticLambda61 implements Function {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda61 INSTANCE = new Collectors$$ExternalSyntheticLambda61();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda61() {
    }

    @Override // j$.util.function.Function
    public /* synthetic */ Function andThen(Function function) {
        return function.getClass();
    }

    @Override // j$.util.function.Function
    public final Object apply(Object obj) {
        return Collectors.lambda$averagingInt$25((long[]) obj);
    }

    @Override // j$.util.function.Function
    public /* synthetic */ Function compose(Function function) {
        return function.getClass();
    }
}
