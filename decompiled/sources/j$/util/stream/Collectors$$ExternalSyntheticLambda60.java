package j$.util.stream;

import j$.util.function.Function;
/* loaded from: classes2.dex */
public final /* synthetic */ class Collectors$$ExternalSyntheticLambda60 implements Function {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda60 INSTANCE = new Collectors$$ExternalSyntheticLambda60();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda60() {
    }

    @Override // j$.util.function.Function
    public /* synthetic */ Function andThen(Function function) {
        return function.getClass();
    }

    @Override // j$.util.function.Function
    public final Object apply(Object obj) {
        Integer valueOf;
        valueOf = Integer.valueOf(((int[]) obj)[0]);
        return valueOf;
    }

    @Override // j$.util.function.Function
    public /* synthetic */ Function compose(Function function) {
        return function.getClass();
    }
}
