package j$.util.stream;

import j$.util.function.Function;
/* loaded from: classes2.dex */
public final /* synthetic */ class Collectors$$ExternalSyntheticLambda55 implements Function {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda55 INSTANCE = new Collectors$$ExternalSyntheticLambda55();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda55() {
    }

    @Override // j$.util.function.Function
    public /* synthetic */ Function andThen(Function function) {
        return function.getClass();
    }

    @Override // j$.util.function.Function
    public final Object apply(Object obj) {
        return ((StringBuilder) obj).toString();
    }

    @Override // j$.util.function.Function
    public /* synthetic */ Function compose(Function function) {
        return function.getClass();
    }
}
