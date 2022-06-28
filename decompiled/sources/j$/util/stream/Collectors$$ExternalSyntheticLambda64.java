package j$.util.stream;

import j$.util.function.Function;
/* loaded from: classes2.dex */
public final /* synthetic */ class Collectors$$ExternalSyntheticLambda64 implements Function {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda64 INSTANCE = new Collectors$$ExternalSyntheticLambda64();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda64() {
    }

    @Override // j$.util.function.Function
    public /* synthetic */ Function andThen(Function function) {
        return function.getClass();
    }

    @Override // j$.util.function.Function
    public final Object apply(Object obj) {
        return Collectors.lambda$reducing$36((Object[]) obj);
    }

    @Override // j$.util.function.Function
    public /* synthetic */ Function compose(Function function) {
        return function.getClass();
    }
}
