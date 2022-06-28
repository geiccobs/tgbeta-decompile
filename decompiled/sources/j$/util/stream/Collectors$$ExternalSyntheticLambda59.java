package j$.util.stream;

import j$.util.function.Function;
/* loaded from: classes2.dex */
public final /* synthetic */ class Collectors$$ExternalSyntheticLambda59 implements Function {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda59 INSTANCE = new Collectors$$ExternalSyntheticLambda59();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda59() {
    }

    @Override // j$.util.function.Function
    public /* synthetic */ Function andThen(Function function) {
        return function.getClass();
    }

    @Override // j$.util.function.Function
    public final Object apply(Object obj) {
        Double valueOf;
        valueOf = Double.valueOf(Collectors.computeFinalSum((double[]) obj));
        return valueOf;
    }

    @Override // j$.util.function.Function
    public /* synthetic */ Function compose(Function function) {
        return function.getClass();
    }
}
