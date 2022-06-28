package j$.util.stream;

import j$.util.function.Function;
/* loaded from: classes2.dex */
public final /* synthetic */ class Collectors$$ExternalSyntheticLambda58 implements Function {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda58 INSTANCE = new Collectors$$ExternalSyntheticLambda58();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda58() {
    }

    @Override // j$.util.function.Function
    public /* synthetic */ Function andThen(Function function) {
        return function.getClass();
    }

    @Override // j$.util.function.Function
    public final Object apply(Object obj) {
        return Collectors.lambda$averagingDouble$33((double[]) obj);
    }

    @Override // j$.util.function.Function
    public /* synthetic */ Function compose(Function function) {
        return function.getClass();
    }
}
