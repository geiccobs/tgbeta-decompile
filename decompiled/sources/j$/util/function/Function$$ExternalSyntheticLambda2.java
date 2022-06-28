package j$.util.function;

import j$.util.function.Function;
/* loaded from: classes2.dex */
public final /* synthetic */ class Function$$ExternalSyntheticLambda2 implements Function {
    public static final /* synthetic */ Function$$ExternalSyntheticLambda2 INSTANCE = new Function$$ExternalSyntheticLambda2();

    private /* synthetic */ Function$$ExternalSyntheticLambda2() {
    }

    @Override // j$.util.function.Function
    public /* synthetic */ Function andThen(Function function) {
        return function.getClass();
    }

    @Override // j$.util.function.Function
    public final Object apply(Object obj) {
        return Function.CC.lambda$identity$2(obj);
    }

    @Override // j$.util.function.Function
    public /* synthetic */ Function compose(Function function) {
        return function.getClass();
    }
}
