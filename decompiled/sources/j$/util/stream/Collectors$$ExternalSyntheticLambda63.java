package j$.util.stream;

import j$.util.function.Function;
/* loaded from: classes2.dex */
public final /* synthetic */ class Collectors$$ExternalSyntheticLambda63 implements Function {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda63 INSTANCE = new Collectors$$ExternalSyntheticLambda63();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda63() {
    }

    @Override // j$.util.function.Function
    public /* synthetic */ Function andThen(Function function) {
        return function.getClass();
    }

    @Override // j$.util.function.Function
    public final Object apply(Object obj) {
        Long valueOf;
        valueOf = Long.valueOf(((long[]) obj)[0]);
        return valueOf;
    }

    @Override // j$.util.function.Function
    public /* synthetic */ Function compose(Function function) {
        return function.getClass();
    }
}
